package com.tongchuang.general.modular.applet.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.tongchuang.general.core.annotation.OpenMapping;
import com.tongchuang.general.core.constant.WeseeLink;
import com.tongchuang.general.core.exception.BizException;
import com.tongchuang.general.core.utils.DateUtils;
import com.tongchuang.general.core.utils.MyCodeUtil;
import com.tongchuang.general.core.utils.MyDateUtils;
import com.tongchuang.general.core.utils.MyFileUtils;
import com.tongchuang.general.core.utils.UserContextHolder;
import com.tongchuang.general.core.utils.WeseeUtils;
import com.tongchuang.general.core.web.responce.R;
import com.tongchuang.general.modular.applet.constant.CodeConstant;
import com.tongchuang.general.modular.applet.service.CertService;
import com.tongchuang.general.modular.applet.service.CodeService;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@Api(tags = "2.1 亲亲和和二维码")
@Slf4j
@RestController
@RequestMapping("/applet/code")
@SuppressWarnings("unchecked")
public class CodeController {

	@Autowired
	private CodeService codeService;
	@Autowired
	private CertService certService;

	@ApiOperation("权益生成:查看权益列表")
	@ApiOperationSupport(order = 101)
	@RequestMapping(value = "/query_code_type",method = RequestMethod.GET)
	public R<Void> queryCodeType(
			@ApiParam(value = "行数", required = true) @RequestParam Integer size,
			@ApiParam(value = "页数", required = true) @RequestParam Integer current
			) {
		IPage<Map<String,String>> ipage = codeService.queryCodeType(size,current,UserContextHolder.getUserId());
		modifyImageUrl(ipage.getRecords());
		return R.success().setData(ipage);
	}
	
	@ApiOperation("权益生成:增加权益类型")
	@ApiOperationSupport(order = 102)
	@RequestMapping(value = "/add_code_type",method = RequestMethod.GET)
	public R<Void> addCodeType(
			@ApiParam(value = "类型名称", required = true) @RequestParam String typeName,
			@ApiParam(value = "图片", required = true) @RequestParam String img) {
		if(img.startsWith("https")) {
			img = img.substring(img.indexOf("upload/") + 7);
		}
		codeService.addCodeType(typeName,img);
		return R.success();
	}

	@ApiOperation("权益生成:新增权益(1次增加1000个)")
	@ApiOperationSupport(order = 103)
	@RequestMapping(value = "/add_code",method = RequestMethod.GET)
	public R<Void> addCode(
			@ApiParam(value = "商品类型Id", required = true) @RequestParam String typeId
			) {
		codeService.addCodeInfoByTypeId(typeId);
		return R.success(); 
	}
	
	@ApiOperation("权益生成:查看权益视频链接列表")
	@ApiOperationSupport(order = 104)
	@RequestMapping(value = "/query_code_video",method = RequestMethod.GET)
	public R<Void> queryVideoInfoList(
			@ApiParam(value="权益类型ID",required=true)@RequestParam String typeId) {
		try {
			List<LinkedHashMap<String,String>> list = codeService.queryCodeLinkByTypeId(typeId);
			if(list.isEmpty()) {
				return R.success().setMsg("无视频");
			}
			for(Map<String,String> map:list) {
				WeseeLink	weseeLinks = WeseeUtils.findLink(map.get("link"));
				map.put("img",weseeLinks.getImageLink());
				map.put("video",weseeLinks.getVideoLink());
			}
			return R.success().setData(list);
		} catch (Exception e) {
			log.error("权益生成:查看权益视频链接列表异常",e); 
			return R.fail().setMsg(e.getMessage());
		}
	}
	
	@ApiOperation("权益生成:新增或者修改权益视频链接")
	@ApiOperationSupport(order = 105)
	@RequestMapping(value = "/update_code_video_list",method = RequestMethod.GET)
	public R<Void> updateVideoInfoList(@ApiParam(value = "视频ID", required = true) @RequestParam String videoId,
			@ApiParam(value = "权益类型ID", required = true) @RequestParam String typeId,
			@ApiParam(value = "视频链接地址", required = true) @RequestParam String weseeLink,
			@ApiParam(value = "序号", required = true) @RequestParam String sort,
			@ApiParam(value = "状态(1:可以修改;2不可以修改)", required = true) @RequestParam String status) {
		try {
			if (StringUtils.equals("null", videoId)) {
				videoId = null;
			}
			codeService.updateCodeCompanyLink(typeId, videoId, sort, weseeLink, status);
			return R.success();
		} catch (Exception e) {
			log.error("权益生成:修改权益视频链接", e);
			return R.fail().setMsg(e.getMessage());
		}
	}
	
	@ApiOperation("权益生成:批量转让权益(选择个数)")
	@ApiOperationSupport(order = 106)
	@RequestMapping(value = "/sell_codes",method = RequestMethod.GET)
	public R<Void> sellCodes(
			@ApiParam(value = "转让公司bossId", required = true) @RequestParam String bossId,
			@ApiParam(value = "商品类型Id", required = true) @RequestParam String typeId,
			@ApiParam(value = "开始编号Id", required = true) @RequestParam String start,
			@ApiParam(value = "转移个数", required = true) @RequestParam Integer count
			) {
		//验证
		codeService.verifyCodeByTypeId(typeId);
		if(bossId.length()>20) {
			bossId = bossId.substring(20);
		}
		if(bossId.length()!=8) {
			throw new BizException("该二维码不可当前操作!");
		}
		codeService.updateCodeCompanyId(typeId,bossId,start,count);
		return R.success();
	}
	
	@ApiOperation("权益生成:批量转让权益(手动输入起始和结束编号)")
	@ApiOperationSupport(order = 107)
	@RequestMapping(value = "/sell_codes_from_start",method = RequestMethod.GET)
	public R<Void> sellCodesFromStartToEnd(
			@ApiParam(value = "转让公司bossId", required = true) @RequestParam String bossId,
			@ApiParam(value = "商品类型Id", required = true) @RequestParam String typeId,
			@ApiParam(value = "开始编号Id", required = true) @RequestParam String start,
			@ApiParam(value = "结束编号Id", required = true) @RequestParam String end
			) {
		codeService.verifyCodeByTypeId(typeId);
		if(bossId.length()>20) {
			bossId = bossId.substring(20);
		}
		if(bossId.length()!=8) {
			throw new BizException("该二维码不可当前操作!");
		}
		codeService.addCodeSellToCompany(typeId,bossId,start,end);
		return R.success();
	}
	
	@ApiOperation("权益生成:查看起始ID与结束ID之间个数")
	@ApiOperationSupport(order = 107)
	@RequestMapping(value = "/query_codes_count",method = RequestMethod.GET)
	public R<Void> sellCodesFromStartToEnd(
			@ApiParam(value = "开始编号Id", required = true) @RequestParam String start,
			@ApiParam(value = "结束编号Id", required = true) @RequestParam String end,
			@ApiParam(value = "权益类型Id", required = true) @RequestParam String typeId
			) {
		if(StringUtils.isEmpty(start)||StringUtils.isEmpty(end)) {
			return R.success().setData(null);
		}
		if(start.length()<13||end.length()<13) {
			return R.success().setData(null);
		}
		Integer count = codeService.queryCountFromStartToEnd(Long.valueOf(start),Long.valueOf(end),typeId);
		return R.success().setData(count);
	}
	
	@ApiOperation("扫码返加权益编号")
	@ApiOperationSupport(order = 103)
	@RequestMapping(value = "/get_data_code",method = RequestMethod.GET)
	public R<Void> getDataCodeId(
			@ApiParam(value = "扫码内容", required = true) @RequestParam String data
			) {
		Map<String, String> map = certService.dataStringToMap(data);
		String codeId = map.get("c");
		codeService.queryCodeJurisdiction(codeId);
		return R.success().setData(codeId); 
	}
	
	@ApiOperation("扫码批量转移")
	@ApiOperationSupport(order = 103)
	@RequestMapping(value = "/sell_code_by_scan_code",method = RequestMethod.GET)
	public R<Void> sellCodeByScanCode(
			@ApiParam(value = "扫码内容", required = true) @RequestParam List<String> list,
			@ApiParam(value = "转让公司", required = true) @RequestParam String bossId,
			@ApiParam(value = "权益类型", required = true) @RequestParam String typeId
			) {
		codeService.sellCodeByScanCode(list,typeId,bossId);
		return R.success().setData(null); 
	}
	
	@ApiOperation("传播和社交公司:查看权益列表")
	@ApiOperationSupport(order = 201)
	@RequestMapping(value = "/company_query_code_type",method = RequestMethod.GET)
	public R<Void> companyQueryCodeType(
			@ApiParam(value = "行数", required = true) @RequestParam Integer size,
			@ApiParam(value = "页数", required = true) @RequestParam Integer current
			) {
		IPage<Map<String,String>> ipage = codeService.queryCodeTypeList(size,current);
		modifyImageUrl(ipage.getRecords());
		return R.success().setData(ipage);
	}
	
	@ApiOperation("传播和社交公司:查看二维码视频链接列表")
	@ApiOperationSupport(order = 202)
	@RequestMapping(value = "/query_company_video_link",method = RequestMethod.GET)
	public R<Void> queryCompanyCodeLink(
			@ApiParam(value="权益类型ID",required=true)@RequestParam String typeId) {
		try {
			List<LinkedHashMap<String,String>> list = codeService.queryCompanyCodeLink(typeId);
			for(Map<String,String> map:list) {
				WeseeLink	weseeLinks = WeseeUtils.findLink(map.get("link"));
				map.put("img",weseeLinks.getImageLink());
				map.put("video",weseeLinks.getVideoLink());
			}
			return R.success().setData(list);
		} catch (Exception e) {
			log.error("传播和社交公司,查看二维码视频链接列表:",e);
			return R.fail().setMsg(e.getMessage());
		}
	}
	
	@ApiOperation("传播和社交公司:增加或者修改二维码视频链接")
	@ApiOperationSupport(order = 204)
	@RequestMapping(value = "/update_company_video",method = RequestMethod.GET)
	public R<Void> updateCodeWeseeLink2(
			@ApiParam(value="视频ID",required=true)@RequestParam String videoId,
			@ApiParam(value="权益类型ID",required=true)@RequestParam String typeId,
			@ApiParam(value="排序",required=true)@RequestParam String sort,
			@ApiParam(value="视频链接地址",required=true)@RequestParam String link,
			@ApiParam(value="状态(1:可以修改;2不可以修改)",required=true)@RequestParam String status) {
		try {
			if (StringUtils.equals("null", videoId)) {
				videoId = null;
			}
			codeService.updateCodeCompanyLink(typeId, videoId, sort, link, status);
			//codeService.updateCodeCompanyLink(videoId,typeId, sort, link,status);
			return R.success();
		} catch (Exception e) {
			log.error("传播和社交公司:增加或者修改二维码视频链接:",e);
			return R.fail().setMsg(e.getMessage());
		}
	}
	
	@ApiOperation("传播-->社交:批量转让权益(输入个数)")
	@ApiOperationSupport(order = 206)
	@RequestMapping(value = "/sell_codes_company",method = RequestMethod.GET)
	public R<Void> sellCodesCompanyId(
			@ApiParam(value = "转让公司BossId", required = true) @RequestParam String bossId,
			@ApiParam(value = "商品类型Id", required = true) @RequestParam String typeId,
			@ApiParam(value = "开始编号Id", required = true) @RequestParam String start,
			@ApiParam(value = "转移数量", required = true) @RequestParam String count
			) {
		if(bossId.length()>20) {
			bossId = bossId.substring(20);
		}
		if(bossId.length()!=8) {
			throw new BizException("该二维码不可当前操作!");
		}
		log.info("开始编号Id:"+start);
		log.info("结束编号Id:"+count);
		codeService.updateCodeCompanyIdByCompany(typeId,bossId,start,count);
		return R.success();
	}
	
	@ApiOperation("传播-->社交:批量转让权益(手动输入起始和结束编号)")
	@ApiOperationSupport(order = 207)
	@RequestMapping(value = "/sell_company_codes_to_company",method = RequestMethod.GET)
	public R<Void> addCompanySellCodesToCompany(
			@ApiParam(value = "转让公司BossId", required = true) @RequestParam String bossId,
			@ApiParam(value = "商品类型Id", required = true) @RequestParam String typeId,
			@ApiParam(value = "开始编号Id", required = true) @RequestParam String start,
			@ApiParam(value = "结束编号Id", required = true) @RequestParam String end
			) {
		if(bossId.length()>20) {
			bossId = bossId.substring(20);
		}
		if(bossId.length()!=8) {
			throw new BizException("该二维码不可当前操作!");
		}
		log.info("开始编号Id:"+start);
		log.info("结束编号Id:"+end);
		codeService.addCompanySellCodesToCompany(typeId,bossId,start,end);
		return R.success();
	}
	
	@ApiOperation(":查看起始ID与结束ID之间个数")
	@ApiOperationSupport(order = 207)
	@RequestMapping(value = "/query_codes_count_from_start_to_end",method = RequestMethod.GET)
	public R<Void> queryCodesFromStartToEnd(
			@ApiParam(value = "开始编号Id", required = true) @RequestParam String start,
			@ApiParam(value = "结束编号Id", required = true) @RequestParam String end,
			@ApiParam(value = "权益类型Id", required = true) @RequestParam String typeId
			) {
		if(StringUtils.isEmpty(start)||StringUtils.isEmpty(start)) {
			return R.success().setData(null);
		}
		if(start.length()<13||end.length()<13) {
			return R.success().setData(null);
		}
		Integer count = codeService.queryCountFromStartToEnd(Long.valueOf(start),Long.valueOf(end),typeId);
		return R.success().setData(count);
	}
	
	@ApiOperation("公司转让个人:权益转让")
	@ApiOperationSupport(order = 208)
	@RequestMapping(value = "/company_sell_code",method = RequestMethod.GET)
	public R<List<String>> companySellCodeToUser(
			@ApiParam(value="二维码",required=true)@RequestParam String data) {
		try {
			Map<String, String> dateMap = certService.dataStringToMap(data);
			codeService.sellCodeByData(dateMap);
			return R.success().setData(null);
		} catch (Exception e) {
			log.error("公司转让个人:权益转让:",e);
			return R.fail().setMsg(e.getMessage());
		}
	}
	
	//@ApiOperation("传播和社交公司:查看视频浏览记录")
	//@ApiOperationSupport(order = 209)
	//@RequestMapping(value = "/query_code_video_browse",method = RequestMethod.GET)
	public R<Void> companyQueryCodeVodeoBrowse(
			@ApiParam(value = "行数", required = true) @RequestParam Integer size,
			@ApiParam(value = "页数", required = true) @RequestParam Integer current
			) {
		IPage<Map<String,String>> ipage = codeService.queryCodeVodeoBrowseList(size,current);
		return R.success().setData(ipage);
	}
	
	@ApiOperation("传播和社交公司:增加权益项目")
	@ApiOperationSupport(order = 209)
	@RequestMapping(value = "/add_company_code_type",method = RequestMethod.GET)
	public R<Void> addCompanyCodeType(
			@ApiParam(value = "项目名称", required = true) @RequestParam String typeName,
			@ApiParam(value = "图片", required = true) @RequestParam String img) {
		if(img.startsWith("https")) {
			img = img.substring(img.indexOf("upload/") + 7);
		}
		codeService.addCodeType(typeName,img);
		return R.success();
	}
	
	@ApiOperation("传播和社交:项目转移(手动输入起始和结束编号)")
	@ApiOperationSupport(order = 210)
	@RequestMapping(value = "/transfer_company_code_type",method = RequestMethod.GET)
	public R<Void> addCompanyCodesTypeToCompany(
			@ApiParam(value = "原项目Id", required = true) @RequestParam String oldTypeId,
			@ApiParam(value = "转移的项目Id", required = true) @RequestParam String typeId,
			@ApiParam(value = "开始编号Id", required = true) @RequestParam String start,
			@ApiParam(value = "结束编号Id", required = true) @RequestParam String end
			) {
		codeService.transferCompanyCodeType(typeId,start,end);
		codeService.addtransferCompanyCodeTypeLink(typeId,oldTypeId);
		return R.success();
	}
	
	
	@ApiOperation("传播和社交:项目列表")
	@ApiOperationSupport(order = 211)
	@RequestMapping(value = "/query_company_code_type",method = RequestMethod.GET)
	public R<Void> queryCompanyCodesType(
			@ApiParam(value = "行数", required = true) @RequestParam Integer size,
			@ApiParam(value = "页数", required = true) @RequestParam Integer current) {
		IPage<Map<String,String>>  ipage = codeService.queryCompanyCodesType(size,current);
		return R.success().setData(ipage);
	}
	
	
	/****						个人中心		
	 * 												*/
	@ApiOperation("个人:扫码看视频")
	@ApiOperationSupport(order = 301)
	@RequestMapping(value = "/get_video_link",method = RequestMethod.GET)
	public R<List<String>> queryQRCodeVideoInfoList(
			@ApiParam(name="data",value="二维码data",required=true)@RequestParam String data) {
		List<String> videoUrl = codeService.queryCodeVideoUrlByData(data);
		return R.success().setData(videoUrl);
	}
	
	@OpenMapping
	@ApiOperation("个人:扫码看视频(登录)")
	@ApiOperationSupport(order = 301)
	@RequestMapping(value = "/get_video_link_not_log",method = RequestMethod.GET)
	public R<List<String>> queryCodeVideo(
			@ApiParam(name="data",value="二维码data",required=true)@RequestParam String data) {
		List<String> videoUrl = codeService.queryCodeVideoUrlByData(data);
		return R.success().setData(videoUrl);
	}
	
	@ApiOperation("个人:查看我的浏览")
	@ApiOperationSupport(order = 303)
	@RequestMapping(value = "/query_my_code_browse",method = RequestMethod.GET)
	public R<Void> queryMyCodeBrowse(
			@ApiParam(value = "行数", required = true) @RequestParam Integer size,
			@ApiParam(value = "页数", required = true) @RequestParam Integer current) {
		IPage<Map<String,String>> ipage = codeService.queryMyCodeBrowse(size,current,UserContextHolder.getUserId());
		modifyImageUrl(ipage.getRecords());
		return R.success().setData(ipage);
	}
	
	@ApiOperation("个人:点击我的浏览(展示二维码获取该二维码权益)")
	@ApiOperationSupport(order = 304)
	@RequestMapping(value = "/showCodeToSell",method = RequestMethod.GET)
	public R<List<String>> showCodeToSell(
			@ApiParam(value="二维码编号",required=true)@RequestParam String codeId) {
		String unionId = UserContextHolder.getUserId();
		Map<String,String> map = new HashMap<String, String>();
		map.put("u", unionId);
		map.put("c", codeId);
		map.put("n", "s1");
		byte[] fimalByte;
		try {
			fimalByte = MyCodeUtil.generateQRCodeToEncode2(map.toString());
			String fileName ="tmp-" +IdUtil.simpleUUID()+".jpg";
			//生成文件,供用户下载促销劵图片,临时文件
			File file = new File(MyFileUtils.uploadPath + fileName);
			MyFileUtils.fileWriteByte(file, fimalByte);
			String path = MyFileUtils.urlPath + fileName;
			return R.success().setData(path);
		} catch (Exception e) {
			 log.error("个人:点击我的浏览",e);
			return R.fail().setMsg(e.getMessage());
		}
	}
	
	@ApiOperation("个人:查看我的权益列表")
	@ApiOperationSupport(order = 305)
	@RequestMapping(value = "/query_my_code",method = RequestMethod.GET)
	public R<Void> queryMyCode(
			@ApiParam(value = "行数", required = true) @RequestParam Integer size,
			@ApiParam(value = "页数", required = true) @RequestParam Integer current) {
		IPage<Map<String,String>> ipage = codeService.queryMyCode(size,current,UserContextHolder.getUserId());
		modifyImageUrl(ipage.getRecords());
		return R.success().setData(ipage);
	}
	
	
	@ApiOperation("个人:查看二维码视频链接列表")
	@ApiOperationSupport(order = 306)
	@RequestMapping(value = "/query_my_video_link",method = RequestMethod.GET)
	public R<Void> queryMyCodeLink(
			@ApiParam(value="二维码ID",required=true)@RequestParam String codeId) {
		try {
			List<Map<String,String>> list = codeService.queryMyCodeLink(codeId);
			for(Map<String,String> map:list) {
				WeseeLink	weseeLinks = WeseeUtils.findLink(map.get("link"));
				map.put("img",weseeLinks.getImageLink());
				map.put("video",weseeLinks.getVideoLink());
			}
			return R.success().setData(list);
		} catch (Exception e) {
			log.error("个人查看二维码视频链接列表:",e);
			return R.fail().setMsg(e.getMessage());
		}
	}
	
	@ApiOperation("个人:修改二维码视频链接")
	@ApiOperationSupport(order = 307)
	@RequestMapping(value = "/update_my_code_link",method = RequestMethod.GET)
	public R<List<String>> updateCode(
			@ApiParam(value="视频ID",required=true)@RequestParam String videoId,
			@ApiParam(value="二维码ID",required=true)@RequestParam String codeId,
			@ApiParam(value="视频链接地址",required=true)@RequestParam String link,
			@ApiParam(value="排序:",required=true)@RequestParam String sort,
			@ApiParam(value="状态:(1:可以修改,2:不可修改)",required=true)@RequestParam String status
			) {
		try {
			if (StringUtils.equals("null", videoId)) {
				videoId = null;
			}
			codeService.updateCodeLink(videoId,codeId,Integer.valueOf(sort),link,status);
			return R.success().setData(null);
		} catch (Exception e) {
			log.error("个人修改二维码视频链接:",e);
			return R.fail().setMsg(e.getMessage());
		}
	}
	
	@ApiOperation("个人转让个人:扫二维码转让权益")
	@ApiOperationSupport(order = 308)
	@RequestMapping(value = "/SellCode",method = RequestMethod.GET)
	public R<List<String>> SellCode(
			@ApiParam(value="二维码",required=true)@RequestParam String data) {
		try {
			Map<String, String> dateMap = certService.dataStringToMap(data);
			codeService.sellCodeToUserByData(dateMap);
			return R.success().setData(null);
		} catch (Exception e) {
			log.error("个人转让个人:扫二维码转让权益:",e);
			return R.fail().setMsg(e.getMessage());
		}
	}
	
	@ApiOperation("个人:查看权益交易记录列表")
	@ApiOperationSupport(order = 309)
	@RequestMapping(value = "/query_my_code_tran",method = RequestMethod.GET)
	public R<Void> queryMyCodeTranRecord(
			@ApiParam(value = "行数", required = true) @RequestParam Integer size,
			@ApiParam(value = "页数", required = true) @RequestParam Integer current,
			@ApiParam(value = "查看类型(1:查看全部;2:转入;3转出)", required = true) @RequestParam String type
			) {
		IPage<Map<String,String>> ipage = codeService.queryMyCodeTranRecord(size,current,UserContextHolder.getUserId(),type);
		return R.success().setData(ipage);
	}
	
	@ApiOperation("打印图片1")
	@ApiOperationSupport(order = 501)
	@RequestMapping(value = "/download_code_by_type",method = RequestMethod.GET)
	public R<Void> downCodeType(
			@ApiParam(value = "开始", required = true) @RequestParam String start,
			@ApiParam(value = "结束", required = true) @RequestParam Integer end,
			@ApiParam(value = "图片编号", required = true) @RequestParam String img,
			@ApiParam(value = "权益类型", required = true) @RequestParam String typeId,
			@ApiParam(value = "权益类型", required = true) @RequestParam String typeName
			) {
		List<Map<String, Object>> list = codeService.queryCodeIdFromStartToEnd(start,end,typeId);
		String url;
		String filepath = "C:\\Users\\Admin\\Desktop\\权益生成\\"+typeName+"\\";
		String path;
		File file =new File(filepath);   
		if (!file.exists() && !file.isDirectory()) {
			file.mkdir();
		}
		for(Map<String, Object> map:list) {
			url=CodeConstant.URL+map;
			byte[] fimalByte = MyCodeUtil.generateQRCodeToByte(url, img);
			path = filepath+map.get("c")+".jpg";
			MyCodeUtil.byte2image(fimalByte, path);
		}
		return R.success().setData(null);
	}
	
	@ApiOperation("打印图片2")
	@ApiOperationSupport(order = 502)
	@RequestMapping(value = "/download_code_2",method = RequestMethod.GET)
	public R<Void> downCodeType2(
			@ApiParam(value = "权益类型Id", required = true) @RequestParam String typeId,
			@ApiParam(value = "权益类型名称", required = true) @RequestParam String typeName,
			@ApiParam(value = "开始", required = true) @RequestParam String start,
			@ApiParam(value = "个数", required = true) @RequestParam Integer  end
			) {
		try {
			List<Map<String, Object>> list = codeService.queryCodeIdFromStartToEnd(start, end, typeId);
			String url;
			String filepath = "C:\\Users\\Admin\\Desktop\\权益生成\\" + typeName + "\\";
			String path;
			File file = new File(filepath);
			if (!file.exists() && !file.isDirectory()) {
				file.mkdir();
			}
			for (Map<String, Object> map : list) {
//				String bossId = map.get("b");
//				String userId = map.get("a");
//				map.put("b",bossId.substring(20));
//				map.put("a",userId.substring(20));
				url = CodeConstant.URL + map;
				byte[] fimalByte = MyCodeUtil.generateQRCodeToEncode2(url);
				path = filepath + map.get("c").toString() + ".jpg";
				MyCodeUtil.byte2image(fimalByte, path);
				File file2 = new File(path); 
				FileUtils.writeByteArrayToFile(file2, fimalByte);
			}
			return R.success().setData(null).setMsg("生成个数:"+list.size());
		} catch (Exception e) {
			return R.fail().setMsg(e.getMessage());
		}
	}
	
	@OpenMapping
	@ApiOperation("打印图片3")
	@ApiOperationSupport(order = 503)
	@RequestMapping(value = "/download_code_e",method = RequestMethod.GET)
	public R<Void> downCodeImg(
			@ApiParam(value = "开始", required = true) @RequestParam String start,
			@ApiParam(value = "结束", required = true) @RequestParam Integer end,
			@ApiParam(value = "图片编号", required = true) @RequestParam String img,
			@ApiParam(value = "权益类型", required = true) @RequestParam String typeId,
			@ApiParam(value = "权益类型", required = true) @RequestParam String typeName,
			HttpServletResponse response
			) throws IOException {
		List<Map<String, Object>> list = codeService.queryCodeIdFromStartToEnd(start,end,typeId);
		String url;
		String path;
		File file =new File(typeName);   
		if (!file.exists() && !file.isDirectory()) {
			file.mkdir();
		}
		for(Map<String, Object> map:list) {
			url=CodeConstant.URL+map;
			byte[] fimalByte = MyCodeUtil.generateQRCodeToByte(url, img);
			path = typeName+"/"+map.get("c")+".jpg";
			MyCodeUtil.byte2image(fimalByte, path);
		}
		MyFileUtils.fileToZip(typeName, "/home/mt/", typeName);
		File file2 = new File(typeName + ".zip");
		byte[] data = FileUtils.readFileToByteArray(file2);
		typeName = URLEncoder.encode(typeName, "UTF-8");
		response.reset();
		response.setHeader("Content-Disposition", "attachment; filename=\"" + typeName + ".zip\"");
		response.addHeader("Content-Length", "" + data.length);
		response.setContentType("application/octet-stream;charset=UTF-8");
		OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
		outputStream.write(data);
		outputStream.flush();
		outputStream.close();
		response.flushBuffer();
		return R.success().setData(null);
	}
	
	
////	@ApiOperationSupport(order = 502)
//	@RequestMapping(value = "/download_code_4",method = RequestMethod.GET)
//	public R<Void> downCodeType4(
//			@ApiParam(value = "权益类型Id", required = true) @RequestParam String typeId,
//			@ApiParam(value = "权益类型名称", required = true) @RequestParam String typeName,
//			@ApiParam(value = "开始", required = true) @RequestParam String start,
//			@ApiParam(value = "数量", required = true) @RequestParam Integer  count,
//			HttpServletResponse response) {
//		ServletOutputStream out = null;
//		ExcelWriter writer = ExcelUtil.getWriter(true);
//		try {
//			List<Map<String, Object>> list = codeService.queryCodeIdFromStartToEnd(start, count, typeId);
//			List<Map<String, Object>> codeDataList = new LinkedList<Map<String,Object>>();
//			Map<String, Object>  codeData = null;
//			Integer num=0;
//			for (Map<String, Object> map : list) {
//				codeData = new LinkedHashMap<String, Object>();
//				codeData.put("序号", ++num);
//				codeData.put("权益编号", map.get("c")); //+ ".jpg");
//				codeData.put("二维码内容", CodeConstant.URL + map);
//				codeDataList.add(codeData);
//			}
//			writer.write(codeDataList, true);
//			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"); 
//			response.setHeader("Content-Disposition","attachment;filename="+MyDateUtils.getTimeStamp()+".xlsx"); 
//			out=response.getOutputStream(); 
//			writer.flush(out, true);
//			return null;
//		} catch (Exception e) {
//			return R.fail().setMsg(e.getMessage());
//		}finally {
//			writer.close();
//			if(null!=out) {
//				IoUtil.close(out);
//			}
//		}
//	}
	
//	@ApiOperation("消费公司:新增二维码视频链接")
//	@ApiOperationSupport(order = 203)
//	@RequestMapping(value = "/addnCodeVideo",method = RequestMethod.GET)
//	public R<Void> addCodeWeseeLink2(
//			@ApiParam(value="二维码ID",required=true)@RequestParam String typeId,
//			@ApiParam(name="sort",value="排序",required=true)@RequestParam String sort,
//			@ApiParam(value="视频链接地址",required=true)@RequestParam String weseeLink) {
//		try {
//			codeService.addCodeWeseeLinkByCompany(typeId, sort, weseeLink);
//		} catch (Exception e) {
//			log.info("11",e);
//		}
//		return R.fail();
//	}
//	@ApiOperation("消费公司:新增二维码视频链接")
//	@ApiOperationSupport(order = 203)
//	@RequestMapping(value = "/addnCodeVideo",method = RequestMethod.GET)
//	public R<Void> addCodeWeseeLink2(
//			@ApiParam(value="二维码ID",required=true)@RequestParam String typeId,
//			@ApiParam(name="sort",value="排序",required=true)@RequestParam String sort,
//			@ApiParam(value="视频链接地址",required=true)@RequestParam String weseeLink) {
//		try {
//			codeService.addCodeWeseeLinkByCompany(typeId, sort, weseeLink);
//		} catch (Exception e) {
//			log.info("11",e);
//		}
//		return R.fail();
//	}
//	
//	@ApiOperation("消费公司:修改二维码视频链接")
//	@ApiOperationSupport(order = 204)
//	@RequestMapping(value = "/updateCodeVideo",method = RequestMethod.GET)
//	public R<Void> updateCodeWeseeLink2(
//			@ApiParam(value="二维码ID",required=true)@RequestParam String typeId,
//			@ApiParam(name="sort",value="排序",required=true)@RequestParam String sort,
//			@ApiParam(value="视频链接地址",required=true)@RequestParam String link) {
//		try {
//			codeService.updateCodeWeseeLink2(typeId, sort, link);
//			return R.success();
//		} catch (Exception e) {
//			 
//			return R.fail();
//		}
//	}
	
//	@ApiOperation("传交公司:批量转让权益")
//	@ApiOperationSupport(order = 105)
//	@RequestMapping(value = "/company_sell_code",method = RequestMethod.GET)
//	public R<Void> companySellCodeToCompany(
//			@ApiParam(value = "转让公司Id", required = true) @RequestParam String companyId,
//			@ApiParam(value = "商品类型Id", required = true) @RequestParam String typeId,
//			@ApiParam(value = "开始编号Id", required = true) @RequestParam String start,
//			@ApiParam(value = "结束结束Id", required = true) @RequestParam String end
//			) {
//		codeService.updateCodeCompanyId(typeId,companyId,start,end);
//		return R.success();
//	}
	 

//	@ApiOperation("获取商品二维码图片")
//	@PostMapping(value = "/download_code")
//	public R<Void> download_code(
//			@ApiParam(value = "行数", required = true) @RequestParam Integer size,
//			@ApiParam(value = "页数", required = true) @RequestParam Integer current,
//			@ApiParam(value = "商品类型Id", required = true) @RequestParam String typeId,
//			@ApiParam(value = "商品图", required = true) @RequestParam String img) {
//		IPage<Map<String,String>> ipage = codeService.queryCode(size,current,typeId,UserContextHolder.getUserId());
//		List<Map<String,String>> list= new LinkedList<Map<String,String>>();
//		Map<String,String> resultMap = null;
//		for (Map<String,String> map:ipage.getRecords()) {
//			resultMap=new HashMap<String, String>();
//			String number= map.remove("n");
//			String path = buildCodeImgPath(map,"101");
//			resultMap.put("number", number);
//			resultMap.put("url", path);
//			list.add(resultMap);
//		}
//		return R.success().setData(list);
 
	// 修改我的二维码视频链接 ---个人

	//

	/**
	 * 生成泥票图片请求路径
	 * */
	public static String buildCodeImgPath(Map<String,String> map,String img) {
		 
		String url = CodeConstant.URL + map;
		byte[] fimalByte = MyCodeUtil.generateQRCodeToByte(url, img);
		String fileName ="tmp-" +DateUtils.getTimeStamp()+".jpg";
		File file = new File(MyFileUtils.uploadPath + fileName);
		MyFileUtils.fileWriteByte(file, fimalByte);
		String path = MyFileUtils.urlPath + fileName;
		return path;
		//return "https://p.3p3.top/upload/test.jpg";
	}
	
	/**
	 * 修改图片路径
	 * @param list
	 * @return
	 */
	public static List<Map<String,String>> modifyImageUrl(List<Map<String,String>> list){
		StringBuilder sb = new StringBuilder(MyFileUtils.urlPath);
		for(Map<String,String> map:list) {
			sb.append(map.get("img"));
			map.put("img",sb.toString());
			//map.put("img","https://q.3p3.top/upload/test.jpg");
			sb.delete(MyFileUtils.urlPath.length(),sb.length());
			//dto.setImageNum("https://p.3p3.top/upload/2008105278528101.jpg");
		}
		return list;
	}
	
	 
}
