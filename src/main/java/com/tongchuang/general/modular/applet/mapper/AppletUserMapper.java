package com.tongchuang.general.modular.applet.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tongchuang.general.modular.applet.dto.CompanyInfoDTO;
import com.tongchuang.general.modular.applet.entity.UserInfo;
import com.tongchuang.general.modular.main.entity.User;

public interface AppletUserMapper extends BaseMapper<UserInfo> {
 
	/** 获取私钥*/
	String getUserPrivateKey(String userId);
	/** 获取公钥*/
	String getUserPublicKey(String userId);

	/** 添加密钥对*/
	void addUserKeyPari(String publicKey, String privateKey, String userId);

	/** 注册公司*/
	void saveCompanyInfo(CompanyInfoDTO companyInfo);

	/** 添加公司成员-老板*/
	void addCompanyBossMember(String boss, String unionId,String type);

	/** 获取用户所在公司id */
	String getCompanyIdByMemberId(String memberId);

	/** 获取用户信息*/
	UserInfo queryUserInfoByPhone(String unionId);
	
	/** 获取用户信息*/
	UserInfo queryUserInfoByPhone2(String phone);
	
	int updateUserInfo(UserInfo userInfo);
	
	/** 搜索公司*/
	IPage<Map<String, String>> searchCompany(Page<Map<String, String>> page,String userId);
	
	/** 获取用户类型*/
	List<Map<String, Object>> getUserType(String unionId);
	
	/** 获取成员老板Id**/
	String getBossIdByMemberId(String memberId);
	
	/** 添加一个成员申请*/
	void addMemberApply(String bossId, String type, String memberId);
	
	/** 查看公司成员*/
	IPage<Map<String, String>> queryCompanyMember(Page<Map<String, String>> page, String status, String bossId);
	
	/** 查看基础数据*/
	List<Map<String, Object>> queryBaseDataByType(String string);
	
	/** 成员管理*/
	void updateMemberStatusByUserId(String bossId, String memberId, String status);
	
	/** 查看公司背景图*/
	String queryCompanyImgByBossId(String bossId);
	
	/** 获取老板的unionId*/
	String getBossUnionIdByMemberId(String memberId);
	
	/** 获取公司信息*/
	CompanyInfoDTO getcompanyInfoByBossId(String bossId);


}
