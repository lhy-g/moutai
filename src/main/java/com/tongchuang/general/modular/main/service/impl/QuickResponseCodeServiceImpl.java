package com.tongchuang.general.modular.main.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tongchuang.general.core.constant.ResEnum;
import com.tongchuang.general.core.constant.UserConst;
import com.tongchuang.general.core.constant.WeseeLink;
import com.tongchuang.general.core.exception.BizException;
import com.tongchuang.general.core.utils.WeseeUtils;
import com.tongchuang.general.core.web.responce.R;
import com.tongchuang.general.modular.main.entity.QuickResponseCode;
import com.tongchuang.general.modular.main.entity.QuickResponseCodeFile;
import com.tongchuang.general.modular.main.entity.User;
import com.tongchuang.general.modular.main.mapper.QuickResponseCodeMapper;
import com.tongchuang.general.modular.main.model.dto.*;
import com.tongchuang.general.modular.main.model.vo.QuickResponseCodeVO;
import com.tongchuang.general.modular.main.service.IQuickResponseCodeFileService;
import com.tongchuang.general.modular.main.service.IQuickResponseCodeService;
import com.tongchuang.general.modular.main.service.IUserService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class QuickResponseCodeServiceImpl extends ServiceImpl<QuickResponseCodeMapper, QuickResponseCode> implements IQuickResponseCodeService {

    @Autowired
    private QuickResponseCodeMapper quickResponseCodeMapper;

    @Autowired
    private IQuickResponseCodeFileService quickResponseCodeFileService;

    @Autowired
    private IUserService userService;

    @Override
    public IPage<QuickResponseCodeVO> quickResponseCodeList(QuickResponseCodeQueryDTO quickResponseCodeQueryDto) {
        return quickResponseCodeMapper.quickResponseCodeList(quickResponseCodeQueryDto.getPage().getObj(), quickResponseCodeQueryDto);
    }

    @Override
    public boolean quickResponseCodeInsert(QuickResponseCodeFormDTO quickResponseCodeFormDto) {
        QueryWrapper<QuickResponseCode> quickResponseCodeWra = new QueryWrapper();
        quickResponseCodeWra.lambda().eq(QuickResponseCode::getNumber, quickResponseCodeFormDto.getNumber());

        if (count(quickResponseCodeWra) > 0) {
            throw new BizException("ID已存在").end(ResEnum.BAD_REQUEST);
        }

        QuickResponseCode quickResponseCode = new QuickResponseCode();
        BeanUtils.copyProperties(quickResponseCodeFormDto, quickResponseCode);
        quickResponseCode.setAuthorizeUserId(UserConst.DEFAULT_ID);
        return save(quickResponseCode);
    }

    @Override
    public boolean quickResponseCodeUpdate(QuickResponseCodeFormDTO quickResponseCodeFormDto) {
        QuickResponseCode quickResponseCode = new QuickResponseCode();
        BeanUtils.copyProperties(quickResponseCodeFormDto, quickResponseCode);
        return updateById(quickResponseCode);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean quickResponseCodeEdit(QuickResponseCodeEditDTO quickResponseCodeEditDto) {
        QuickResponseCode quickResponseCode = new QuickResponseCode();
        BeanUtils.copyProperties(quickResponseCodeEditDto.getQuickResponseCodeFormDto(), quickResponseCode);

        if (Objects.nonNull(quickResponseCodeEditDto.getQuickResponseCodeFileFormDtos())) {
            QueryWrapper<QuickResponseCodeFile> quickResponseCodeFileQueryWrapper = new QueryWrapper<>();
            quickResponseCodeFileQueryWrapper.lambda().eq(QuickResponseCodeFile::getQuickResponseCodeId, quickResponseCode.getId());

            List<QuickResponseCodeFile> updateQuickResponseCodeFiles = new ArrayList<>();
            quickResponseCodeEditDto.getQuickResponseCodeFileFormDtos().forEach(d -> {
                QuickResponseCodeFile quickResponseCodeFile = new QuickResponseCodeFile();
                BeanUtils.copyProperties(d, quickResponseCodeFile);
                if (StringUtils.isNotBlank(d.getWeseeLink())) {
                    quickResponseCodeFile.setQuickResponseCodeId(quickResponseCode.getId());
                    try {
                        WeseeLink weseeLink = WeseeUtils.findLink(quickResponseCodeFile.getWeseeLink());
                        quickResponseCodeFile.setWeseeImageLink(weseeLink.getImageLink());
                        quickResponseCodeFile.setWeseeVideoLink(weseeLink.getVideoLink());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    quickResponseCodeFile.setWeseeVideoLink("");
                    quickResponseCodeFile.setWeseeImageLink("");
                }
                quickResponseCodeFile.setQuickResponseCodeId(quickResponseCode.getId());
                updateQuickResponseCodeFiles.add(quickResponseCodeFile);
            });
            List<QuickResponseCodeFile> quickResponseCodeFiles = quickResponseCodeFileService.list(quickResponseCodeFileQueryWrapper);
            if (quickResponseCodeFiles.size() > updateQuickResponseCodeFiles.size()) {
                List<Long> sourceIds = new ArrayList<>();
                quickResponseCodeFiles.forEach(d -> {
                    sourceIds.add(d.getId());
                });
                List<Long> newIds = new ArrayList<>();
                updateQuickResponseCodeFiles.forEach(d -> {
                    if (d.getId() != null) {
                        newIds.add(d.getId());
                    }
                });
                sourceIds.removeAll(new ArrayList<>(newIds));

                //删除缺少值
                quickResponseCodeFileService.removeByIds(sourceIds);
            }
            if (Objects.nonNull(updateQuickResponseCodeFiles) && updateQuickResponseCodeFiles.size() > 0) {
                quickResponseCodeFileService.saveOrUpdateBatch(updateQuickResponseCodeFiles);
            }
        }
        return updateById(quickResponseCode);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean quickResponseCodeDelete(Long id) {
        QueryWrapper<QuickResponseCodeFile> queryWrapper = new QueryWrapper();
        if (quickResponseCodeFileService.count(queryWrapper) > 0) {
            queryWrapper.lambda().eq(QuickResponseCodeFile::getQuickResponseCodeId, id);
            quickResponseCodeFileService.remove(queryWrapper);
        }
        return removeById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean quickResponseCodeAdd(QuickResponseCodeAddDTO quickResponseCodeAddDto) {
        QuickResponseCode quickResponseCode = new QuickResponseCode();
        BeanUtils.copyProperties(quickResponseCodeAddDto.getQuickResponseCodeFormDto(), quickResponseCode);
        quickResponseCode.setAuthorizeUserId(UserConst.DEFAULT_ID);
        quickResponseCode.setCreateTime(LocalDateTime.now());
        if (save(quickResponseCode)) {
            if (quickResponseCodeAddDto.getQuickResponseCodeFileFormDtos() != null) {
                AtomicBoolean error = new AtomicBoolean(false);
                List<QuickResponseCodeFile> quickResponseCodeFiles = new ArrayList<>();
                quickResponseCodeAddDto.getQuickResponseCodeFileFormDtos().forEach(d -> {
                    QuickResponseCodeFile quickResponseCodeFile = new QuickResponseCodeFile();
                    BeanUtils.copyProperties(d, quickResponseCodeFile);
                    quickResponseCodeFile.setQuickResponseCodeId(quickResponseCode.getId());
                    if (StringUtils.isNotBlank(d.getWeseeLink())) {
                        try {
                            WeseeLink weseeLink = WeseeUtils.findLink(quickResponseCodeFile.getWeseeLink());
                            quickResponseCodeFile.setWeseeImageLink(weseeLink.getImageLink());
                            quickResponseCodeFile.setWeseeVideoLink(weseeLink.getVideoLink());
                        } catch (IOException e) {
                            e.printStackTrace();
                            error.set(true);
                        }
                        quickResponseCodeFile.setQuickResponseCodeId(quickResponseCode.getId());
                    }
                    quickResponseCodeFiles.add(quickResponseCodeFile);
                });
                if (error.get()) {
                    throw new BizException(ResEnum.UNKNOWN_ERROR);
                }
                quickResponseCodeFileService.saveBatch(quickResponseCodeFiles);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean quickResponseCodeAuthorize(QuickResponseCodeAuthorizeDTO quickResponseCodeAuthorizeDto) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getPhone, quickResponseCodeAuthorizeDto.getPhone());
        User user = userService.getOne(queryWrapper);

        if (user == null) {
            throw new BizException("该用户未注册").end(ResEnum.NOT_FOUND);
        }

        QuickResponseCode quickResponseCode = getById(quickResponseCodeAuthorizeDto.getId());
        quickResponseCode.setAuthorizeUserId(user.getId());
        return updateById(quickResponseCode);
    }

    @Override
    public boolean quickResponseCodeAuthorizeCancel(Long id) {
        QuickResponseCode quickResponseCode = getById(id);
        quickResponseCode.setAuthorizeUserId(Long.valueOf(0));
        return updateById(quickResponseCode);
    }


}
