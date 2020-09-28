package com.tongchuang.general.modular.applet.mapper;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
 
import com.tongchuang.general.modular.applet.dto.CodeAddRecordDTO;
import com.tongchuang.general.modular.applet.dto.CodeInfoDTO;
import com.tongchuang.general.modular.applet.dto.CodeTypeDTO;
import com.tongchuang.general.modular.applet.dto.CodeVideoDTO;

/***
 * 促销券Mapper
 * 
 * @author Admin
 *
 */
public interface CodeMapper {

	void addCodeType(CodeTypeDTO code);

	/** 权益生成:批量添加权益,**/
	void addCodeInfoByTypeId(List<CodeInfoDTO> list);
	
	/** 权益生成:添加权益生成记录*/
	void addCodeAddRecord(CodeAddRecordDTO codeAddRecordDTO);

	IPage<Map<String, String>> queryCode(Page<Map<String, String>> page, String userId, String typeId);

	/** 权益为生成方,查看视频链接*/
	LinkedList<String> queryCodeVideoLink(String codeId,String bossId);
	
	/** 运营公司,和社交查看视频链接*/
	LinkedList<String> querySaleCodeVideoLink(String codeId,String bossId);
	
	/** 权益为个人时,查看视频链接*/
	LinkedList<String> queryCodeVideoUrl(String codeId);

	/** 添加浏览记录*/
	void addCodeBrowse(String codeId, String unionId);

	/** 查找二维码权益人ID*/
	String getCodeUserIdByCodeId(String codeId);

	/** 公司转个人二维码权益*/
	void addCodeSellUser(CodeInfoDTO codeInfoDto);
	
	/** 个人转个人*/
	void updateCodeUserId(String codeId,String userId);

	/** 权益生成:批量转移给公司*/
	void updateCodeCompanyId(String typeId, String companyId, String start, String end);

	/** 公司批量转让公司*/
	void updateCodeCompanyIdByCompany(String typeId, String sellBossId, String bossId, String start, Integer count);

	/** 公司按起始编号批量转让公司*/
	void addCompanySellCodesToCompany(String typeId, String sellBossId, String bossId, String start, String end);
	
	/**	权益生成 :公司查看商品*/
	IPage<Map<String, String>> queryCodeType(Page<Map<String, String>> page, String userId);

	IPage<Map<String, String>> queryCodeTypeList(Page<Map<String, String>> page, String bossId);
	
	/** 个人查看二维码*/
	IPage<Map<String, String>> queryMyCode(Page<Map<String, String>> page, String userId);

	/** 权益生成:新增或者修改二维码视频链接*/
	void updateCodeWeseeLink(CodeVideoDTO codeVideoDTO);

	/** 查询公司一个类型商品类型下的所有二维码Id*/
	List<String> queryAllCodeIdByCompanyId(String typeId, String companyId);

	/** 批量增加二维码视频*/
	void addBatchCodeLink(List<CodeVideoDTO> list);

	/** 公司:修改二维码 */
	void updateCodeWeseeLink2(String typeId, String companyId, String sort, String link);

	/** 权益生成:查询一个类型最大编号*/
	Integer getCodeMaxNumber(String typeId);

	List<Map<String, Object>> queryCodeIdFromStartToEnd(String start, Integer count,String typeId);

	/** 查看一个权益类型视频链接 */
	List<Map<String, String>> queryCodeWeseeLink(String typeId);

	/** 查看二维码状态*/
	String queryCodeStatus(String codeId);

	IPage<Map<String, String>> queryMyCodeBrowse(Page<Map<String, String>> page, String unionId);

	/** 修改公司视频链接*/
	void updateCodeCompanyLink(String typeId,String videoId, String bossId, String sort, String link,String status);

	/** 通过bossId和typeId,查看公司权益视频链接 */
	List<LinkedHashMap<String, String>> queryCompanyCodeLink(String typeId, String bossId);

	/** 个人修改视频链接*/
	void updateCodeLink(String videoId,String codeId,Integer sort, String link,String status);
	
	/** 个人查看视频链接列表*/
	LinkedList<Map<String, String>> queryMyCodeLink(String codeId);
	
	/** 批量转让权益时,将权益视频链接转给接收方*/
	void addCompanyCodeLinkFromSell(String typeId,String bossId,String sellBossId);

	/** 批量转让二维码时,让二维码视频链接转给接收方*/
	void addMyCodeLinkFromSell(String typeId,String bossId,String codeId,String userId);

	/** 权益生成批量转移权益给公司*/
	Integer updateCodeCompanyIdByCodeList(List<CodeInfoDTO> list);

	List<Map<String, Object>> queryCodeIdListFromStartLimitCount(String typeId, String start, Integer count);
	
	List<Map<String, Object>> queryCodeIdListFromStartToEnd(String typeId, String start, String end);

	/** 批量更新权益生成的权益状态*/
	void updateCodeStatus(List<String> list);

	/** 获取权益公司老板,权益类型Id*/
	Map<String, String> getCodeBossIdAndTypeIdByCodeId(String codeId);

	/** 更新一个权益状态*/
	void updateCodeStatusByCodeId(String codeId);
	
	/** 更新传播公司权益状态*/
	void updateCodeSellCompanyStatus(String codeId);

	/** 权益生成公司成员查看权益类型所属老板ID*/
	String getTypeBossIdByTypeId(String typeId);

	/** 添加批量转让权益记录*/
	void addCodeBatchSellRecord(String typeId, int count, String start,String end, String sellerId, String bossId);

	/** 查看权益扫码记录*/
	IPage<Map<String, String>> queryCodeVodeoBrowseList(Page<Map<String, String>> page, String userId);

	 

	void excel(List<Map<String, String>> listMap);

	/** 权益生成查看起始ID和结束ID之间数量*/
	Integer queryCountFromStartToEnd(Long start, Long end, String typeId,String bossId);
	
	/** 传播社交查看起始ID和结束ID之间数量*/
	Integer queryCountFromStartToEnd2(Long start, Long end, String typeId,String bossId);

	/** 查看个人的权益交易记录*/
	IPage<Map<String, String>> queryMyCodeTranRecord(Page<Map<String, String>> page, String userId,String type);

	/** 添加个人交易记录*/
	void addCodeTranRecord(String codeId, String sellerId, String userId);

	/** 查看个人权益视频链接个数*/
	Integer queryCodeVidoCountByCodeId(String codeId);

	/** 查看公司权益类型视频链接个数*/
	Integer queryCompanyLinkCount(String typeId, String bossId);

	/** 获取权益生成的权益的老板ID*/
	String getCodeBossIdByCodeId(String codeId);
	
	/** 获取公司的权益的老板ID*/
	String getCompanyCodeBossIdByCodeId(String codeId);

	/** 查看一个权益的排序*/
	List<Integer> queryCodeVidoSortList(String codeId);
	
	List<Integer> queryCompanyCodeVidoSortList(String codeId);

	void transferCompanyCodeType(String typeId, String start, String end,String bossId);

	/** */
	IPage<Map<String, String>> queryCompanyCodesType(Page<Map<String, String>> page, String bossId);

	/** */
	void addtransferCompanyCodeTypeLink(String typeId, String oldTypeId,String bossId);

	void updateCodeInfoType(String typeId, String start, String end, String bossId);

	/** */
	Integer queryCompanyCodeVideoLinkCount(String typeId);

	/** 查看公司权益类型视频链接个数*/
	//Integer queryCompanyCodeVidoCountByTypeId(String typeId, String bossId);

	


	
 
	

	 

	 
}
