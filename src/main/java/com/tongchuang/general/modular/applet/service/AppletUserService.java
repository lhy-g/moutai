package com.tongchuang.general.modular.applet.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tongchuang.general.modular.applet.dto.CompanyInfoDTO;

public interface AppletUserService {

	String getUserPrivateKey(String userId) throws Exception;

	String getUserPublicKey(String userId) throws Exception;

	void saveCompanyInfo(CompanyInfoDTO companyInfo);

	/** 查询用户所在公司Id*/
	String queryCompanyIdByMemberId(String member);

	/** 搜索公司*/
	IPage<Map<String, String>> searchCompany(Integer size, Integer current);

	/** */
	List<Map<String, Object>> queryUserType(String unionId);

	/** 获取成员老板ID*/
	String getBossIdByMemberId(String memberId);

	/** 添加公司成员申请*/
	void addMemberApply(String bossId, String type, String userId);

	IPage<Map<String, String>> queryCompanyMember(Integer size, Integer current, String status, String unionId);

	/** 成员管理*/
	void updateMemberStatusByUserId(String bossId, String memberId, String status);

	/** 查看背景图*/
	String queryCompanyImgByBossId(String bossId);

	/** 获取老板的unionId*/
	String getBossUnionIdByMemberId(String userId);

	/** 获取我的公司信息
	 * @return */
	CompanyInfoDTO getCompanyInfo();

	 

	 

}
