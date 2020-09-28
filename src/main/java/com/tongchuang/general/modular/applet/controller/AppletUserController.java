package com.tongchuang.general.modular.applet.controller;

 
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.FormSubmitEvent.MethodType;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.tongchuang.general.core.annotation.OpenMapping;
import com.tongchuang.general.core.utils.DateUtils;
import com.tongchuang.general.core.utils.JwtUtils;
import com.tongchuang.general.core.utils.MyCodeUtil;
import com.tongchuang.general.core.utils.MyFileUtils;
import com.tongchuang.general.core.utils.ProveUtils;
import com.tongchuang.general.core.utils.QRCodeUtil;
import com.tongchuang.general.core.utils.UserContextHolder;
import com.tongchuang.general.core.web.responce.R;
import com.tongchuang.general.modular.applet.constant.CodeConstant;
import com.tongchuang.general.modular.applet.dto.CompanyInfoDTO;
import com.tongchuang.general.modular.applet.entity.Prove;
import com.tongchuang.general.modular.applet.entity.UserInfo;
import com.tongchuang.general.modular.applet.service.AppletUserService;
import com.tongchuang.general.modular.main.entity.QuickResponseCode;
import com.tongchuang.general.modular.main.model.vo.SynInfoVO;
import com.tongchuang.general.modular.main.model.vo.UserVO;

import cn.hutool.core.util.IdUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@Api(tags = "2.0 亲亲和和用户相关")
@Slf4j
@RestController
@RequestMapping("/applet/user")
@SuppressWarnings("unchecked")
public class AppletUserController {

	@Autowired
	private AppletUserService userService;
	
	@ApiOperation("注册/更新公司")
	@ApiOperationSupport(order = 101)
	@RequestMapping(value = "/save_company_info", method = RequestMethod.POST)
	public R<Void> saveCompanyInfo(@RequestBody CompanyInfoDTO companyInfo) {
		try {
			userService.saveCompanyInfo(companyInfo);
			return R.success().setData(null);
		} catch (Exception e) {
			log.error("注册公司信息异常:", e);
			return R.fail().setMsg("系统异常!");
		}
	}
	
	@ApiOperation("查看我的公司信息")
	@ApiOperationSupport(order = 101)
	@RequestMapping(value = "/get_company_info", method = RequestMethod.GET)
	public R<Void> getCompanyInfo() {
		try {
			CompanyInfoDTO companyInfoDTO = userService.getCompanyInfo();
			return R.success().setData(companyInfoDTO);
		} catch (Exception e) {
			log.error("注册公司信息异常:", e);
			return R.fail().setMsg("系统异常!");
		}
	}
	
	@ApiOperation("搜索公司")
	@ApiOperationSupport(order = 102)
	@RequestMapping(value = "/search_company", method = RequestMethod.GET)
	public R<Void> saveCompanyInfose(
			@ApiParam(value = "行数", required = true) @RequestParam Integer size,
			@ApiParam(value = "页数", required = true) @RequestParam Integer current) {
		try {
			IPage<Map<String,String>> ipage = userService.searchCompany(size,current);
			return R.success().setData(ipage);
		} catch (Exception e) {
			log.error("注册公司信息异常:", e);
			return R.fail().setMsg("系统异常!");
		}
	}

	@ApiOperation("申请成为公司成员")
	@ApiOperationSupport(order = 201)
    @RequestMapping(value="/add_company_member",method = RequestMethod.GET)
    public R<Void> addMemberApply(
    		@ApiParam(value = "老板ID",required = true) @RequestParam String bossId,
    		@ApiParam(value = "类型",required = true) @RequestParam String type) {
    	try {
    		userService.addMemberApply(bossId,type,UserContextHolder.getUserId());
    		return R.success().setData(null);
		} catch (Exception e) {
			log.error("申请成为公司成员异常:",e);
			return R.fail().setMsg("系统异常!");
		}
    }
	
	@ApiOperation("查看公司成员")
	@ApiOperationSupport(order = 202)
    @RequestMapping(value="/query_company_member",method = RequestMethod.GET)
    public R<Void> queryCompanyMember(
    		@ApiParam(value="行数",required=true) @RequestParam Integer size,
    		@ApiParam(value="页数",required=true) @RequestParam Integer current,
    		@ApiParam(value = "类型(0:申请人员;1:在职人员)",required = true) @RequestParam String status) {
    	try {
    		IPage<Map<String,String>> result = userService.queryCompanyMember(size,current,status,UserContextHolder.getUserId());
    		return R.success().setData(result);
		} catch (Exception e) {
			log.error("查看公司成员异常:",e);
			return R.fail().setMsg("系统异常!");
		}
    }
	
	@ApiOperation("成员管理")
	@ApiOperationSupport(order = 6)
    @RequestMapping(value="/member_manage",method = RequestMethod.GET)
    public R<Void> memberManage(
    		@ApiParam(value = "用户ID",required = true) @RequestParam String memberId,
    		@ApiParam(value = "1:同意,2:拒绝,3辞退",required = true) @RequestParam String status) {
    	try {
    		String bossId = UserContextHolder.getUserId();
    		userService.updateMemberStatusByUserId(bossId,memberId,status);
    		return R.success().setData(null);
		} catch (Exception e) {
			log.error("成员管理异常:",e);
			return R.fail().setMsg("系统异常!");
		}
    }
	
	@ApiOperation("背景图")
	@RequestMapping(value = "/query_company_img", method = RequestMethod.GET)
	public R<String> queryBusinessImg() {
		String bossId = userService.getBossIdByMemberId(UserContextHolder.getUserId());
		String img = userService.queryCompanyImgByBossId(bossId);
		return R.success().setData(img);
	}
	
	@ApiOperation("切换身份")
	@RequestMapping(value = "/change_user_type",method = RequestMethod.GET)
	@ApiOperationSupport(order = 301)
	public R<UserVO> changeUserType(
			@ApiParam(value = "切换身份类型", required = true) @RequestParam String type) {
		UserVO userVo = UserContextHolder.get();
		userVo.setType(type);
		return R.success().setData(SynInfoVO.builder().token(JwtUtils.generateToken(userVo)).build());
	}

	@ApiOperation("查看可切换身份")
	@RequestMapping(value = "/query_user_type",method = RequestMethod.GET)
	@ApiOperationSupport(order = 302)
	public R<UserVO> queryUserType() {
		List<Map<String, Object>> list = userService.queryUserType(UserContextHolder.getUserId());
		return R.success().setData(list);
	}

	@ApiOperation("公司接收权益(公司老板unionId二维码)")
	@ApiOperationSupport(order = 203)
	@RequestMapping(value = "/show_user_company_id", method = RequestMethod.GET)
	public R<String> showUserCompanyId() throws Exception {
		String bossUnionId= userService.getBossUnionIdByMemberId(UserContextHolder.getUserId());
		String encode = MyCodeUtil.generateQRCodeToEncode1(bossUnionId);
		return R.success().setData(encode);
	}

	@ApiOperation("用户二维码")
	@ApiOperationSupport(order = 301)
	@RequestMapping(value = "/showUserQRCode", method = RequestMethod.GET)
	public R<String> showUserQRCode() throws Exception {
		String unionId = UserContextHolder.getUnionId();
		String encode = MyCodeUtil.generateQRCodeToEncode1(unionId);
		return R.success().setData(encode);
	}

	@ApiOperation("上传文件")
	@ApiOperationSupport(order = 501)
	@RequestMapping(value = "/file/upload", method = RequestMethod.POST)
	public R<String> uploadMusicFile(HttpServletRequest request, @ApiParam("文件") MultipartFile file,
			@ApiParam(name = "type", value = "上传类型(co:二维码原图)") @RequestParam() String type) {
		long fileSize = file.getSize() / 1024 / 1024;
		String name = file.getOriginalFilename();
		if ("co".equals(type)) {
//			if (!name.endsWith("jpg")) {
//				return R.fail().setMsg("目前只支持JPG格式图片!");
//			}
		}
		if (fileSize > 5) {
//    		if("cp".equals(type)&&fileSize>1.5) {
//    			return R.fail().setMsg("文件大小不得大于1.5M");
//    		}
			return R.fail().setMsg("文件大小不得大于5M");
		}
		return R.success().setData(MyFileUtils.fileUpload(file, type));
	}
}
