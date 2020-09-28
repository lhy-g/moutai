package com.tongchuang.general.modular.applet.service.impl;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tongchuang.general.core.exception.BizException;
import com.tongchuang.general.core.utils.DateUtils;
import com.tongchuang.general.core.utils.MyKeyPariUtils;
import com.tongchuang.general.core.utils.RsaUtils;
import com.tongchuang.general.core.utils.UserContextHolder;
import com.tongchuang.general.modular.applet.dto.CompanyInfoDTO;
import com.tongchuang.general.modular.applet.entity.UserInfo;
import com.tongchuang.general.modular.applet.mapper.AppletUserMapper;
import com.tongchuang.general.modular.applet.service.AppletUserService;

 

@Service
public class AppletUserServiceImpl extends ServiceImpl<AppletUserMapper, UserInfo>  implements AppletUserService{

	@Autowired
	private AppletUserMapper userMapper;
	
	 
	
	
	@Override
	public String getUserPrivateKey(String userId) throws Exception {
		String privateKey = userMapper.getUserPrivateKey(userId);
		if (StringUtils.isEmpty(privateKey)) {
			// 为空时,给用户添加密钥对
			try {
				KeyPair keyPair = MyKeyPariUtils.getKeyPair();
				privateKey = new String(Base64.encodeBase64(keyPair.getPrivate().getEncoded()));
				String publicKey = new String(Base64.encodeBase64(keyPair.getPublic().getEncoded()));
				userMapper.addUserKeyPari(publicKey, privateKey, userId);
			} catch (Exception e) {
				throw new BizException("获取用户签名信息失败,请稍后重试!!");
			}
		}
		return privateKey;
	}

	@Override
	public String getUserPublicKey(String userId) throws Exception {
		String publicKey = userMapper.getUserPublicKey(userId);
		if (StringUtils.isEmpty(publicKey)) {
			try {
				KeyPair keyPair = MyKeyPariUtils.getKeyPair();
				String privateKey = new String(Base64.encodeBase64(keyPair.getPrivate().getEncoded()));
				publicKey = new String(Base64.encodeBase64(keyPair.getPublic().getEncoded()));
				userMapper.addUserKeyPari(publicKey, privateKey, userId);
			} catch (Exception e) {
				throw new BizException("获取用户签名信息失败,请稍后重试!!");
			}
		}
		return publicKey;
	}

	@Transactional
	@Override
	public void saveCompanyInfo(CompanyInfoDTO companyInfo) {
		if (StringUtils.isEmpty(companyInfo.getCompanyId())) {
			companyInfo.setCompanyId("CI" + DateUtils.getGuid());
		}else {
			String bossId = userMapper.getBossIdByMemberId(UserContextHolder.getUserId());
			if(!StringUtils.equals(bossId, UserContextHolder.getUserId())) {
				throw new BizException("只有老板才可以修改!");
			}
		}
		companyInfo.setStatus("1");
		companyInfo.setBossId(UserContextHolder.getUserId());
		userMapper.saveCompanyInfo(companyInfo);
		String type = companyInfo.getType();
		if(StringUtils.equals("1", type)) {
			type="11";
		}else if(StringUtils.equals("2", type)) {
			type="21";
		}else if(StringUtils.equals("3", type)) {
			type="31";
		}
		userMapper.addCompanyBossMember(UserContextHolder.getUserId(),UserContextHolder.getUserId(),type);
	}

	@Override
	public String queryCompanyIdByMemberId(String memberId) {
		 
		return userMapper.getCompanyIdByMemberId(memberId);
	}

	@Override
	public IPage<Map<String, String>> searchCompany(Integer size, Integer current) {
		Page<Map<String, String>> page = new Page<Map<String, String>>(current, size);
		IPage<Map<String, String>> ipage = userMapper.searchCompany(page,UserContextHolder.getUserId());
		return ipage;
	}

	@Override
	public List<Map<String, Object>> queryUserType(String userId) {
		List<Map<String, Object>> typeList = userMapper.getUserType(userId);
		if(typeList.isEmpty()) {
			return typeList;
		}
		String type = typeList.get(0).get("type").toString();
		if(StringUtils.equals("11", type)) {
			return userMapper.queryBaseDataByType("company_type_1");
		}
		if(StringUtils.equals("21", type)) {
			return userMapper.queryBaseDataByType("company_type_2");
		}
		if(StringUtils.equals("31", type)) {
			return userMapper.queryBaseDataByType("company_type_3");
		}
		return typeList;
	}

	@Override
	public String getBossIdByMemberId(String memberId) {
		 
		return userMapper.getBossIdByMemberId(memberId);
	}

	@Override
	public void addMemberApply(String bossId, String type, String userId) {
		userMapper.addMemberApply(bossId, type, userId);
	}

	@Override
	public IPage<Map<String, String>> queryCompanyMember(Integer size, Integer current, String status, String bossId) {
		Page<Map<String, String>> page = new Page<Map<String, String>>(current, size);
		IPage<Map<String, String>> iPage = userMapper.queryCompanyMember(page, status,bossId);
		return iPage;
	}

	@Override
	public void updateMemberStatusByUserId(String bossId, String memberId, String status) {
		userMapper.updateMemberStatusByUserId(bossId, memberId, status);
	}

	@Override
	public String queryCompanyImgByBossId(String bossId) {
		return userMapper.queryCompanyImgByBossId(bossId);
	}

	@Override
	public String getBossUnionIdByMemberId(String userId) {
		return userMapper.getBossUnionIdByMemberId(userId);
	}

	@Override
	public CompanyInfoDTO getCompanyInfo() {
		String bossId = userMapper.getBossIdByMemberId(UserContextHolder.getUserId());
		
		return userMapper.getcompanyInfoByBossId(bossId);
	}

	 

 
	
	
//	@Override
//	public List<MemberRelationDTO> queryMemberList(String unionId, String type) {
//		List<MemberRelationDTO> list = appletUserMapper.queryMemberList(unionId, type);
//		if (null == list) {
//			return new ArrayList<MemberRelationDTO>();
//		}
//		return list;
//	}
//	
//	@Transactional
//	@Override
//	public boolean addMember(String memberId,String type) {
//		MemberRelationDTO memberRelationDTO = new MemberRelationDTO();
//		memberRelationDTO.setLeaderId("101");
//		memberRelationDTO.setMemberId(memberId);
//		memberRelationDTO.setStatus("1");
//		memberRelationDTO.setType(type);
//		int count = appletUserMapper.addMember(memberRelationDTO);
//		appletUserMapper.updateUserType(memberId, type);
//		if(count==1) {
//			return true;
//		} 
//		return false;
//	}
//
//	@Override
//	public boolean delMember(String memberId) {
//		appletUserMapper.updateMemberStatusByMemberId(memberId);
//		appletUserMapper.updateUserType(memberId, "消费者");
//		return false;
//	}
//	
//	
//	@Override
//	public String queryUserIdByphone(String phone) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public int saveUserId(String phone) {
//		UserInfo user = new UserInfo();
//		user.setUnionId("13798591639");
//		user.setStatus("1");
//		user.setType("1");
//		saveOrUpdate(user);
//		return 0;
//	}
//
//	@Override
//	public String queryUserPrivateKey(String unionId) throws Exception {
//		String privateKey = appletUserMapper.queryUserPrivateKey(unionId);
//		if(null==privateKey) {
//			// 为空时,给用户添加密钥对
//			KeyPair keyPair = RsaUtils.getKeyPair();
//			privateKey = new String(Base64.encodeBase64(keyPair.getPrivate().getEncoded()));
//			String publicKey = new String(Base64.encodeBase64(keyPair.getPublic().getEncoded()));
//			appletUserMapper.addUserPrivateKey(unionId, privateKey);
//			appletUserMapper.addUserPublicKey(unionId, publicKey);
//		}
//		return privateKey;
//	}
//
//	@Override
//	public int addUserPrivateKey(String nuionId, String privateKey) {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	@Override
//	public String queryUserPublicKey(String unionId) {
//		return appletUserMapper.queryUserPublicKey(unionId);
//	}

	

	

}
