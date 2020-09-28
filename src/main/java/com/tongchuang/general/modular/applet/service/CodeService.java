package com.tongchuang.general.modular.applet.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tongchuang.general.modular.applet.entity.RqCode;

public interface CodeService {
	
	/** 权益生成:查看我的权益列表*/ 
	IPage<Map<String, String>> queryCodeType(Integer size, Integer current,  String unionId);

	/** 权益生成:添加一个权益类型*/
	void addCodeType(String typeName,String img);

	/** 权益生成:从权益类型ID,批量添加权益**/
	void addCodeInfoByTypeId(String typeId);
	
	/** 权益生成:查看权益类型视频链接*/
	List<LinkedHashMap<String, String>> queryCodeLinkByTypeId(String typeId);

	/** 权益生成:新增或者修改权益类型视频链接*/
	void updateCodeCompanyLink(String typeId ,String videoId, String sort, String link,String status);
	
	/** 权益生成:批量转让权益*/
	void updateCodeCompanyId(String typeId, String bossId, String start,Integer count);
	
	/** 权益生成:根据起始编号批量转让权益*/
	void addCodeSellToCompany(String typeId, String bossId, String start, String end);
	
	
	/** 传播公司:查看我的权益列表*/
	IPage<Map<String, String>> queryCodeTypeList(Integer size, Integer current);

	/** 传播-->社交:批量转让权益*/
	void updateCodeCompanyIdByCompany(String typeId, String bossId, String start, String end);
	
	/**	传播-->社交:按起始编号批量转让权益*/
	void addCompanySellCodesToCompany(String typeId, String bossId, String start, String end);
	
	/** 查询二维码
	 * @return */
	IPage<Map<String, String>> queryCode(Integer size, Integer current, String typeId, String unionId);

	/** 扫码看视频 */
	List<String> queryCodeVideoUrlByData(String data);

	/** 公司转个人用户*/
	void sellCodeByData(Map<String, String> dateMap);
	
	/** 个人用户转个人用户*/
	void sellCodeToUserByData(Map<String, String> dateMap);

	
	
	

	

	
	
	/** 个人查看我的二维码*/
	IPage<Map<String, String>> queryMyCode(Integer size, Integer current, String unionId);

	/** 权益生成:修改一个二维码链接*/
	void updateCodeWeseeLink(String typeId, String sort, String weseeLink);

	/** 传播公司:修改一个二维码链接*/
	void updateCodeWeseeLink2(String typeId, String sort, String link);
	
	/** 公司批量添加二维码视频 */
	void addCodeWeseeLinkByCompany(String typeId, String sort, String weseeLink);

	/** 查看打打印权益编号*/
	List<Map<String, Object>> queryCodeIdFromStartToEnd(String  start, Integer  count,String typeId);

	

	/** 查看我权益浏览记录*/
	IPage<Map<String, String>> queryMyCodeBrowse(Integer size, Integer current, String unionId);

	 

	/** 公司查看公司权益视频链接*/
	List<LinkedHashMap<String, String>> queryCompanyCodeLink(String typeId);

	/** 个人修改视频链接**/
	void updateCodeLink(String videoId,String codeId, Integer sort, String link,String status);

	/** 个人查看视频链接列表*/
	List<Map<String, String>> queryMyCodeLink(String codeId);

	/** 校验权益*/
	void verifyCodeByTypeId(String typeId);

	/** 查看*/
	IPage<Map<String, String>> queryCodeVodeoBrowseList(Integer size, Integer current);

	 

	void excel(List<Map<String, String>> listMap);

	/** 权益公司:查看起始编号到结束编号之前权益个数 */
	Integer queryCountFromStartToEnd(Long start, Long end, String typeId);
	
	/** 传播公司:查看起始编号到结束编号之前权益个数 */
	Integer queryCountFromStartToEnd2(Long start, Long end, String typeId);

	/** 个人:查看交易记录*/
	IPage<Map<String, String>> queryMyCodeTranRecord(Integer size, Integer current, String userId,String type);

	/** 扫码出售权益
	 * @param bossId 
	 * @param typeId */
	void sellCodeByScanCode(List<String> list, String typeId, String bossId);

	/** 判断当前用户是否可以操作权益*/
	void queryCodeJurisdiction(String codeId);

	void transferCompanyCodeType(String typeId, String start, String end);

	IPage<Map<String, String>> queryCompanyCodesType(Integer size, Integer current);

	void addtransferCompanyCodeTypeLink(String typeId, String oldTypeId);

	

	

	

 
}
