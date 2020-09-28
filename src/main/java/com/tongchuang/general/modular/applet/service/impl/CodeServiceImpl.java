package com.tongchuang.general.modular.applet.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.catalina.startup.UserConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.hutool.core.util.IdUtil;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tongchuang.general.core.constant.WeseeLink;
import com.tongchuang.general.core.exception.BizException;
import com.tongchuang.general.core.utils.DateUtils;
import com.tongchuang.general.core.utils.MyDateUtils;
import com.tongchuang.general.core.utils.ProveUtils;
import com.tongchuang.general.core.utils.UserContextHolder;
import com.tongchuang.general.core.utils.WeseeUtils;
import com.tongchuang.general.modular.applet.dto.CodeAddRecordDTO;
import com.tongchuang.general.modular.applet.dto.CodeInfoDTO;
import com.tongchuang.general.modular.applet.dto.CodeTypeDTO;
import com.tongchuang.general.modular.applet.dto.CodeVideoDTO;
import com.tongchuang.general.modular.applet.entity.RqCode;
import com.tongchuang.general.modular.applet.mapper.CodeMapper;
import com.tongchuang.general.modular.applet.service.AppletUserService;
import com.tongchuang.general.modular.applet.service.CertService;
import com.tongchuang.general.modular.applet.service.CodeService;
import com.tongchuang.general.modular.main.entity.QuickResponseCode;
import com.tongchuang.general.modular.main.mapper.UserMapper;
import com.tongchuang.general.modular.main.model.vo.UserVO;
@Service

public class CodeServiceImpl implements CodeService{
	 
	@Autowired
	private CodeMapper codeMapper;
	
	@Autowired
	private AppletUserService userService;
	
	 

	@Autowired
	private CertService certService;
	@Override
	public void addCodeType(String typeName,String img) {
		 CodeTypeDTO code = new CodeTypeDTO();
		 code.setTypeName(typeName);
		 code.setTimeStamp(DateUtils.getTimeStamp());
		 code.setBossId(UserContextHolder.getUserId());
		 code.setImg(img);
		 String sign = "sign";
		 code.setSign(sign);
		 codeMapper.addCodeType(code);
	}

	/**
	 *
	 */
	@Transactional
	@Override
	public void addCodeInfoByTypeId(String typeId) {
		List<CodeInfoDTO> list = new LinkedList<CodeInfoDTO>();
		Integer num = codeMapper.getCodeMaxNumber(typeId);
		num = num == null ? 0 : num;
		String userId = UserContextHolder.getUserId();
		try {
			CodeInfoDTO codeInfo;
			for (int i = num + 1; i <= 1000 + num; i++) {
				codeInfo = new CodeInfoDTO();
				codeInfo.setTypeId(typeId);
				codeInfo.setCodeId(MyDateUtils.getNowTimeStamp());
				codeInfo.setNumber(i);
				String sign = certService.sign(codeInfo.getCodeId(), userId);
				codeInfo.setSign(sign);
				codeInfo.setUserId(userId);
				list.add(codeInfo);
				if (codeInfo.getCodeId().endsWith("999")) {
					Thread.sleep(1000);
				}
			}
			codeMapper.addCodeInfoByTypeId(list);
			String timeStamp = MyDateUtils.getTimeStamp();
			String sign2 = certService.sign(typeId+timeStamp, userId);
			//增加权益生成记录
			CodeAddRecordDTO codeAddRecordDTO = new CodeAddRecordDTO();
			codeAddRecordDTO.setTypeId(typeId);
			codeAddRecordDTO.setUserId(userId);
			codeAddRecordDTO.setStartNum(num+1);
			codeAddRecordDTO.setEndNum(num+1000);
			codeAddRecordDTO.setSign(sign2);
			codeAddRecordDTO.setTimeStamp(timeStamp);
			codeMapper.addCodeAddRecord(codeAddRecordDTO);
		} catch (Exception e) {
			throw new BizException("新增权益异常!");
		}
	}

	 
	
	@Override
	public IPage<Map<String, String>> queryCode(Integer size, Integer current, String typeId, String userId) {
		Page<Map<String, String>> page = new Page<Map<String, String>>(current, size);
		IPage<Map<String, String>> ipage = codeMapper.queryCode(page, userId,typeId);
		return ipage;
		 
	}

	@Override
	public List<String> queryCodeVideoUrlByData(String data) {
		Map<String, String> map = certService.dataStringToMap(data);
	//	String agentId = userService.queryUnionIdByUserId(map.get(CertConstant.APPLICANT));
	//	String timeStamp = map.get(CertConstant.TIME_STAMP);
		String codeId = map.get("c");
		//查看当前视频 状态
		List<String> list=null;
		String status = codeMapper.queryCodeStatus(codeId);
		if(StringUtils.equals("1", status)) {
			String bossId = codeMapper.getCodeBossIdByCodeId(codeId);
			list = codeMapper.queryCodeVideoLink(codeId,bossId);
		}else if(StringUtils.equals("4", status)) {
			list = codeMapper.queryCodeVideoUrl(codeId);
		}else {
			String bossId = codeMapper.getCompanyCodeBossIdByCodeId(codeId);
			list = codeMapper.querySaleCodeVideoLink(codeId,bossId);
		}
		if(null==list||list.isEmpty()) {
			throw new BizException("视频为空!");
		}
		List<String> urlList = new LinkedList<String>();
		try {
			WeseeLink weseeLinks;
			for (String weseeLink : list) {
				weseeLinks = WeseeUtils.findLink(weseeLink);
				urlList.add(weseeLinks.getVideoLink());
			}
		} catch (IOException e) {
			throw new BizException("视频解析异常");
		}
		// 添加浏览记录
		UserVO userVO = UserContextHolder.get();
		System.out.println("登录信息:"+userVO);
		if (null != userVO) {
			codeMapper.addCodeBrowse(codeId,userVO.getUserId());
		}
		return urlList;
	 
	}

	@Transactional
	@Override
	public void sellCodeByData(Map<String, String> dateMap) {
		 String codeId = dateMap.get("c");
		 String userId = dateMap.get("u");
		 String num=dateMap.get("n");
		 if(!StringUtils.equals("s1", num)) {
			 throw new BizException("非法操作!");
		 }
		 
		 /*查看用户是否是*/
		Map<String,String> map = codeMapper.getCodeBossIdAndTypeIdByCodeId(codeId);
		if(null==map) {
			throw new BizException("无权操作当前权益");
		}
		String bossId = map.get("b");
		String typeId = map.get("t");
		String sellBossId = userService.getBossIdByMemberId(UserContextHolder.getUserId());
		if(!StringUtils.equals(sellBossId, bossId)) {
			throw new BizException("非该二维码权益人");
		}
		CodeInfoDTO codeInfo = new CodeInfoDTO();
		codeInfo.setCodeId(codeId);
		codeInfo.setUserId(userId);
		codeInfo.setSign("签字");
		codeInfo.setTypeId(typeId);
		try {
			codeMapper.addCodeSellUser(codeInfo);
			// 视频
			codeMapper.addMyCodeLinkFromSell(typeId, bossId, codeId, userId);
			// 修改状态
			codeMapper.updateCodeStatusByCodeId(codeId);
			codeMapper.updateCodeSellCompanyStatus(codeId);
		} catch (Exception e) {
			throw new BizException("系统错误,请稍后重试!");
		}
		 //codeMapper.queryCompanyCodeLinkBycodeId(codeId,sellCompanyId);
	}
	
	@Transactional
	@Override
	public void sellCodeToUserByData(Map<String, String> dateMap) {
		 String codeId = dateMap.get("c");
		 String userId = dateMap.get("u");
		 String num=dateMap.get("n");
		 if(!StringUtils.equals("s1", num)) {
			 throw new BizException("非法操作!");
		 }
		 /*查看用户是否是*/
		String  sellerId = codeMapper.getCodeUserIdByCodeId(codeId);
		if(!StringUtils.equals(sellerId, UserContextHolder.getUserId())) {
			throw new BizException("非该二维码权益人");
		}
		 codeMapper.updateCodeUserId(codeId,userId);
		 codeMapper.addCodeTranRecord(codeId,sellerId,userId);
		// codeMapper.addMyCodeLinkFromSell(typeId,bossId, codeId,userId);
		//codeMapper.queryCompanyCodeLinkBycodeId(codeId,sellCompanyId);
	}

	@Transactional
	@Override
	public void updateCodeCompanyId(String typeId, String bossId, String start, Integer limitCount) {
		//String sellCompanyId = userService.queryCompanyIdByMemberId(UserContextHolder.getUserId());
		String sellBossId = userService.getBossIdByMemberId(UserContextHolder.getUserId());
		List<Map<String, Object>> list = codeMapper.queryCodeIdListFromStartLimitCount(typeId,start,limitCount);
		List<CodeInfoDTO> codeList=new ArrayList<CodeInfoDTO>();
		List<String> codeIdList = new ArrayList<String>();
		CodeInfoDTO codeInfoDTO;
		try {
			for (Map<String, Object> map : list) {
				String codeId = map.get("c").toString();
				String sign = map.get("s").toString();
				String userId = map.get("u").toString();
				certService.certVerify2(codeId, userId, sign);
				codeInfoDTO = new CodeInfoDTO();
				codeInfoDTO.setCodeId(codeId);
				codeInfoDTO.setTypeId(typeId);
				codeInfoDTO.setBossId(bossId);
				String sellerSign = certService.sign(codeId, UserContextHolder.getUserId());
				codeInfoDTO.setSign(sellerSign);
				codeList.add(codeInfoDTO);
				codeIdList.add(codeId);
			}
		} catch (Exception e) {
			throw new BizException("签字异常!");
		}
		if(!codeList.isEmpty()) {
			int count = codeMapper.updateCodeCompanyIdByCodeList(codeList);
			codeMapper.addCodeBatchSellRecord(typeId,count,start,null,UserContextHolder.getUserId(),bossId);
			Integer linkCount = codeMapper.queryCompanyLinkCount(typeId,bossId);
			if (linkCount == 0) {
				codeMapper.addCompanyCodeLinkFromSell(typeId, bossId, sellBossId);
			}
			codeMapper.updateCodeStatus(codeIdList);
		}
	//	codeMapper.updateCodeCompanyId(typeId,companyId,start,end);
		/* 转让相对应权益视频链接*/
	}

	@Override
	public void addCodeSellToCompany(String typeId, String bossId, String start, String end) {
		String sellBossId = userService.getBossIdByMemberId(UserContextHolder.getUserId());
		List<Map<String, Object>> list = codeMapper.queryCodeIdListFromStartToEnd(typeId, start, end);
		List<CodeInfoDTO> codeList = new ArrayList<CodeInfoDTO>();
		List<String> codeIdList = new ArrayList<String>();
		CodeInfoDTO codeInfoDTO;
		try {
			for (Map<String, Object> map : list) {
				String codeId = map.get("c").toString();
				String sign = map.get("s").toString();
				String userId = map.get("u").toString();
				certService.certVerify2(codeId, userId, sign);
				codeInfoDTO = new CodeInfoDTO();
				codeInfoDTO.setCodeId(codeId);
				codeInfoDTO.setTypeId(typeId);
				codeInfoDTO.setBossId(bossId);
				String sellerSign = certService.sign(codeId, UserContextHolder.getUserId());
				codeInfoDTO.setSign(sellerSign);
				codeList.add(codeInfoDTO);
				codeIdList.add(codeId);
			}
		} catch (Exception e) {
			throw new BizException("签字异常!");
		}
		if (!codeList.isEmpty()) {
			int count = codeMapper.updateCodeCompanyIdByCodeList(codeList);
			codeMapper.addCodeBatchSellRecord(typeId, count, start, end,UserContextHolder.getUserId(), bossId);
			//
			Integer linkCount = codeMapper.queryCompanyLinkCount(typeId,bossId);
			if (linkCount == 0) {
				codeMapper.addCompanyCodeLinkFromSell(typeId, bossId, sellBossId);
			}
			//
			codeMapper.updateCodeStatus(codeIdList);
		}
		
	}
	
	@Override
	public void sellCodeByScanCode(List<String> list, String typeId, String bossId) {
		List<CodeInfoDTO> codeList = new ArrayList<CodeInfoDTO>();
		List<String> codeIdList = new ArrayList<String>();
		CodeInfoDTO codeInfoDTO;
		try {
			for (String codeId : list) {
				codeInfoDTO = new CodeInfoDTO();
				codeInfoDTO.setCodeId(codeId);
				codeInfoDTO.setTypeId(typeId);
				codeInfoDTO.setBossId(bossId);
			//	String sellerSign = certService.sign(codeId, UserContextHolder.getUserId());
			//	codeInfoDTO.setSign(sellerSign);
				codeList.add(codeInfoDTO);
				codeIdList.add(codeId);
			}
		} catch (Exception e) {
			throw new BizException("签字异常!");
		}
		if (!codeList.isEmpty()) {
			int count = codeMapper.updateCodeCompanyIdByCodeList(codeList);
		//	codeMapper.addCodeBatchSellRecord(typeId, count, start, end,UserContextHolder.getUserId(), bossId);
		//	codeMapper.addCompanyCodeLinkFromSell(typeId, bossId, sellBossId);
			codeMapper.updateCodeStatus(codeIdList);
		}
	}
	
	
	@Override
	public void updateCodeCompanyIdByCompany(String typeId, String bossId, String start, String count) {
		String sellBossId = userService.getBossIdByMemberId(UserContextHolder.getUserId());
		codeMapper.updateCodeCompanyIdByCompany(typeId,sellBossId,bossId,start,Integer.valueOf(count));
		Integer linkCount = codeMapper.queryCompanyLinkCount(typeId,bossId);
		if (linkCount == 0) {
			codeMapper.addCompanyCodeLinkFromSell(typeId, bossId, sellBossId);
		}
	}
	
	@Override
	public void addCompanySellCodesToCompany(String typeId, String bossId, String start, String end) {
		String sellBossId = userService.getBossIdByMemberId(UserContextHolder.getUserId());
		codeMapper.addCompanySellCodesToCompany(typeId,sellBossId,bossId,start,end);
		Integer count = codeMapper.queryCompanyLinkCount(typeId,bossId);
		if(count==0) {
			codeMapper.addCompanyCodeLinkFromSell(typeId, bossId, sellBossId);
		}
	}

	
	@Override
	public IPage<Map<String, String>> queryCodeType(Integer size, Integer current, String userId) {
		Page<Map<String, String>> page = new Page<Map<String, String>>(current, size);
		IPage<Map<String, String>> ipage = codeMapper.queryCodeType(page, userId);
		return ipage;
	}
	
	@Override
	public IPage<Map<String, String>> queryCodeTypeList(Integer size, Integer current) {
		//String companyId = userService.queryCompanyIdByMemberId(UserContextHolder.getUserId());
		String bossId = userService.getBossIdByMemberId(UserContextHolder.getUserId());
		Page<Map<String, String>> page = new Page<Map<String, String>>(current, size);
		IPage<Map<String, String>> ipage = codeMapper.queryCodeTypeList(page,bossId);
		return ipage;
	}

	@Override
	public IPage<Map<String, String>> queryMyCode(Integer size, Integer current, String userId) {
		Page<Map<String, String>> page = new Page<Map<String, String>>(current, size);
		IPage<Map<String, String>> ipage = codeMapper.queryMyCode(page, userId);
		return ipage;
	}

	@Override
	public void updateCodeWeseeLink(String typeId, String sort, String weseeLink) {
		WeseeLink weseeLinks;
		if (weseeLink.indexOf("https") > 1) {
			weseeLink = weseeLink.substring(weseeLink.indexOf("https"));
		}
		try {
			weseeLinks = WeseeUtils.findLink(weseeLink);
		} catch (Exception e) {
			 throw new BizException("视频解析失败!");
		}
		CodeVideoDTO codeVideoDTO = new CodeVideoDTO();
		
		codeVideoDTO.setWeseeLink(weseeLink);
		codeVideoDTO.setImageUrl(weseeLinks.getImageLink());
		codeVideoDTO.setSort(sort);
		codeVideoDTO.setCodeId(typeId);
		 
		codeMapper.updateCodeWeseeLink(codeVideoDTO);
	}

	@Override
	public void addCodeWeseeLinkByCompany(String typeId, String sort, String weseeLink) {
		String companyId = userService.queryCompanyIdByMemberId(UserContextHolder.getUserId());
		List<String> codeIdList = codeMapper.queryAllCodeIdByCompanyId(typeId, companyId);
		List<CodeVideoDTO> list = new ArrayList<CodeVideoDTO>();
		CodeVideoDTO codeVideoDTO;
		for (String codeId : codeIdList) {
			codeVideoDTO = new CodeVideoDTO();
			codeVideoDTO.setWeseeLink(weseeLink);
			codeVideoDTO.setSort(sort);
			codeVideoDTO.setCodeId(codeId);
			list.add(codeVideoDTO);
		}
		codeMapper.addBatchCodeLink(list);
	}

	@Override
	public void updateCodeWeseeLink2(String typeId, String sort, String link) {
		String companyId = userService.queryCompanyIdByMemberId(UserContextHolder.getUserId());
		codeMapper.updateCodeWeseeLink2(typeId,companyId,sort,link);
	}

	@Override
	public List<Map<String, Object>> queryCodeIdFromStartToEnd(String start, Integer count,String typeId) {
		
		return codeMapper.queryCodeIdFromStartToEnd(start,count,typeId);
	}

	@Override
	public List<LinkedHashMap<String, String>> queryCodeLinkByTypeId(String typeId) {
		String bossId = userService.getBossIdByMemberId(UserContextHolder.getUserId());
		//return codeMapper.queryCodeWeseeLink(typeId);
		
		return codeMapper.queryCompanyCodeLink(typeId, bossId);
	}

	@Override
	public IPage<Map<String, String>> queryMyCodeBrowse(Integer size, Integer current, String unionId) {
		Page<Map<String, String>> page = new Page<Map<String, String>>(current, size);
		IPage<Map<String, String>> ipage = codeMapper.queryMyCodeBrowse(page,unionId);
		return ipage;
	}

	@Override
	public void updateCodeCompanyLink(String typeId,String videoId,String sort, String link,String status) {
		try {
			if (link.indexOf("https") > 1) {
				link = link.substring(link.indexOf("https"));
			}
			WeseeUtils.findLink(link);
		} catch (Exception e) {
			throw new BizException("视频解析失败,请更换合法微视视频链接地址!");
		}
		String bossId = userService.getBossIdByMemberId(UserContextHolder.getUserId());
		if(StringUtils.isEmpty(videoId)) {
			Integer count = codeMapper.queryCompanyLinkCount(typeId,bossId);
			if(count>=9) {
				throw new BizException("视频链接总数上限为9个");
			}
		}
		codeMapper.updateCodeCompanyLink(typeId,videoId, bossId, sort, link,status);
	}

	@Override
	public List<LinkedHashMap<String, String>> queryCompanyCodeLink(String typeId) {
		String bossId = userService.getBossIdByMemberId(UserContextHolder.getUserId());
		return codeMapper.queryCompanyCodeLink(typeId,bossId);
	}

	@Override
	public void updateCodeLink(String videoId,String codeId, Integer sort, String link,String status) {
		if (link.indexOf("https") > 1) {
			link = link.substring(link.indexOf("https"));
		}
		try {
			 WeseeUtils.findLink(link);
		} catch (Exception e) {
			 throw new BizException("视频解析失败!");
		}
		List<Integer> list = codeMapper.queryCodeVidoSortList(codeId);
		if(StringUtils.isEmpty(videoId)) {
			sort = checkCodeLinkSort(sort,list);
		} 
		codeMapper.updateCodeLink(videoId,codeId,sort,link,status);
	}

	
	/** 校验视频链接排序问题*/
	public Integer checkCodeLinkSort(Integer sort,List<Integer> list) {
		if(list.size()>=9) {
			throw new BizException("视频链接总数上限为9个");
		}
		boolean off=true;
		if (list.contains(sort)) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i) != i + 1) {
					sort = i + 1;
					off = false;
					break;
				}
			}
			if (off) {
				sort = list.size() + 1;
			}
		}
		return sort;
	}
	
	@Override
	public List<Map<String, String>> queryMyCodeLink(String codeId) {
		return codeMapper.queryMyCodeLink(codeId);
	}

	@Override
	public void verifyCodeByTypeId(String typeId) {
		String bossId = userService.getBossIdByMemberId(UserContextHolder.getUserId());
		String typeBossId = codeMapper.getTypeBossIdByTypeId(typeId);
		if (StringUtils.isEmpty(bossId)) {
			throw new BizException("没有权限操作!");
		}
		if (!StringUtils.equals(bossId, typeBossId)) {
			throw new BizException("没有权限操作!!");
		}
	}

	@Override
	public IPage<Map<String, String>> queryCodeVodeoBrowseList(Integer size, Integer current) {
		Page<Map<String, String>> page = new Page<Map<String, String>>(current, size);
		IPage<Map<String, String>> ipage = codeMapper.queryCodeVodeoBrowseList(page,UserContextHolder.getUserId());
		return ipage;
	}

	@Override
	public void excel(List<Map<String, String>> listMap) {
		 
		codeMapper.excel(listMap);
	}

	@Override
	public Integer queryCountFromStartToEnd(Long start, Long end, String typeId) {
		 String bossId = userService.getBossIdByMemberId(UserContextHolder.getUserId());
		 String type = UserContextHolder.get().getType();
		 if(type.startsWith("1")) {
			 return codeMapper.queryCountFromStartToEnd(start,end,typeId,bossId);
		 }else {
			 return codeMapper.queryCountFromStartToEnd2(start, end, typeId, bossId);
		 }
	}
	@Override
	public Integer queryCountFromStartToEnd2(Long start, Long end, String typeId) {
		 String bossId = userService.getBossIdByMemberId(UserContextHolder.getUserId());
		return codeMapper.queryCountFromStartToEnd2(start,end,typeId,bossId);
	}

	@Override
	public IPage<Map<String, String>> queryMyCodeTranRecord(Integer size, Integer current, String userId,String type) {
		Page<Map<String, String>> page = new Page<Map<String, String>>(current, size);
		IPage<Map<String, String>> ipage = codeMapper.queryMyCodeTranRecord(page,userId,type);
		//if (StringUtils.equals("1", type)) {
			for (Map<String, String> map : ipage.getRecords()) {
				String sellerId = map.get("s");
				String user = map.get("u");
				if(sellerId.equals(userId)) {
					map.put("resutl", "转出交予："+map.get("userName"));
					continue;
				}else if(user.equals(userId)) {
					map.put("resutl", "转入来自："+map.get("sellName"));
				}
			}
		//}
		
		return ipage;
	}

	@Override
	public void queryCodeJurisdiction(String codeId) {
		UserVO userVO = UserContextHolder.get();
		String bossId;
		if(userVO.getType().startsWith("1")) {
			bossId = codeMapper.getCodeBossIdByCodeId(codeId);
		} else if(userVO.getType().startsWith("2")) {
			bossId = codeMapper.getCompanyCodeBossIdByCodeId(codeId);
		}
		
	}

	@Override
	public void transferCompanyCodeType(String typeId, String start, String end) {
		String bossId = userService.getBossIdByMemberId(UserContextHolder.getUserId());
		codeMapper.transferCompanyCodeType(typeId,start,end,bossId);
		codeMapper.updateCodeInfoType(typeId,start,end,bossId);
		
	}

	@Override
	public void addtransferCompanyCodeTypeLink(String typeId, String oldTypeId) {
		String bossId = userService.getBossIdByMemberId(UserContextHolder.getUserId());
		Integer count = codeMapper.queryCompanyCodeVideoLinkCount(typeId);
		if(count==0) {
			codeMapper.addtransferCompanyCodeTypeLink(typeId,oldTypeId,bossId);
		}
	}
	
	@Override
	public IPage<Map<String, String>> queryCompanyCodesType(Integer size, Integer current) {
		String bossId = userService.getBossIdByMemberId(UserContextHolder.getUserId());
		Page<Map<String, String>> page = new Page<Map<String, String>>(current, size);
		IPage<Map<String, String>> ipage = codeMapper.queryCompanyCodesType(page,bossId);
		return ipage;
	}

	

	
	

	

	//公司传公司
	
	 
	
	 
	
}
