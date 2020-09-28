package com.tongchuang.general.modular.main.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tongchuang.general.modular.main.entity.QuickResponseCode;
import com.tongchuang.general.modular.main.model.dto.*;
import com.tongchuang.general.modular.main.model.vo.QuickResponseCodeVO;

import java.util.List;

public interface IQuickResponseCodeService extends IService<QuickResponseCode> {

    IPage<QuickResponseCodeVO> quickResponseCodeList(QuickResponseCodeQueryDTO quickResponseCodeQueryDto);

    boolean quickResponseCodeInsert(QuickResponseCodeFormDTO quickResponseCodeFormDto);

    boolean quickResponseCodeUpdate(QuickResponseCodeFormDTO quickResponseCodeFormDto);

    boolean quickResponseCodeEdit(QuickResponseCodeEditDTO quickResponseCodeEditDto);

    boolean quickResponseCodeDelete(Long id);

    boolean quickResponseCodeAdd(QuickResponseCodeAddDTO quickResponseCodeAddDto);

    boolean quickResponseCodeAuthorize(QuickResponseCodeAuthorizeDTO quickResponseCodeAuthorizeDto);

    boolean quickResponseCodeAuthorizeCancel(Long id);
}
