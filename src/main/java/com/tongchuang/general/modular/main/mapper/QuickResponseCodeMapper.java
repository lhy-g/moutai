package com.tongchuang.general.modular.main.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tongchuang.general.modular.main.entity.QuickResponseCode;
import com.tongchuang.general.modular.main.model.dto.QuickResponseCodeQueryDTO;
import com.tongchuang.general.modular.main.model.vo.QuickResponseCodeVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QuickResponseCodeMapper extends BaseMapper<QuickResponseCode> {

    IPage<QuickResponseCodeVO> quickResponseCodeList(Page page, @Param("quickResponseCodeQueryDto") QuickResponseCodeQueryDTO quickResponseCodeQueryDto);
}
