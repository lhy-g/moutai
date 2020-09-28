package com.tongchuang.general.modular.main.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tongchuang.general.core.constant.WeseeLink;
import com.tongchuang.general.modular.main.entity.QuickResponseCodeFile;
import com.tongchuang.general.modular.main.model.dto.QuickResponseCodeFileFormDTO;
import com.tongchuang.general.modular.main.model.dto.QuickResponseCodeFileQueryDTO;
import com.tongchuang.general.modular.main.model.vo.QuickResponseCodeFileVO;

import java.io.IOException;
import java.util.List;

public interface IQuickResponseCodeFileService extends IService<QuickResponseCodeFile> {

    List<QuickResponseCodeFileVO> quickResponseCodeFileList(QuickResponseCodeFileQueryDTO quickResponseCodeFileQueryDto);

    boolean quickResponseCodeFileUpdate(QuickResponseCodeFileFormDTO quickResponseCodeFileFormDto) throws IOException;

    boolean quickResponseCodeFileUpdateBatch(List<QuickResponseCodeFileFormDTO> quickResponseCodeFileFormDtos) throws IOException;

    List<WeseeLink> weseeAnalysis(List<String> weseeLink);
}
