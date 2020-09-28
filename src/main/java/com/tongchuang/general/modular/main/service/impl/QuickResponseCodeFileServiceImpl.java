package com.tongchuang.general.modular.main.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tongchuang.general.core.constant.QuickResponseCodeFileConst;
import com.tongchuang.general.core.constant.ResEnum;
import com.tongchuang.general.core.constant.WeseeLink;
import com.tongchuang.general.core.exception.BizException;
import com.tongchuang.general.core.utils.WeseeUtils;
import com.tongchuang.general.modular.main.entity.QuickResponseCode;
import com.tongchuang.general.modular.main.entity.QuickResponseCodeFile;
import com.tongchuang.general.modular.main.mapper.QuickResponseCodeFileMapper;
import com.tongchuang.general.modular.main.model.dto.QuickResponseCodeFileFormDTO;
import com.tongchuang.general.modular.main.model.dto.QuickResponseCodeFileQueryDTO;
import com.tongchuang.general.modular.main.model.vo.QuickResponseCodeFileVO;
import com.tongchuang.general.modular.main.model.vo.QuickResponseCodeVO;
import com.tongchuang.general.modular.main.service.IQuickResponseCodeFileService;
import com.tongchuang.general.modular.main.service.IQuickResponseCodeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class QuickResponseCodeFileServiceImpl extends ServiceImpl<QuickResponseCodeFileMapper, QuickResponseCodeFile> implements IQuickResponseCodeFileService {

    @Autowired
    private IQuickResponseCodeService quickResponseCodeService;

    @Override
    public List<QuickResponseCodeFileVO> quickResponseCodeFileList(QuickResponseCodeFileQueryDTO quickResponseCodeFileQueryDto) {
        QueryWrapper<QuickResponseCodeFile> queryWrapper = new QueryWrapper<>();
        if (quickResponseCodeFileQueryDto.getQuickResponseCodeId() != null) {
            queryWrapper.lambda().eq(QuickResponseCodeFile::getQuickResponseCodeId, quickResponseCodeFileQueryDto.getQuickResponseCodeId());
        } else if (StringUtils.isNotBlank(quickResponseCodeFileQueryDto.getQuickResponseCodeNumber())) {
            QuickResponseCode quickResponseCode = quickResponseCodeService.getOne(
                    new QueryWrapper<QuickResponseCode>().lambda().eq(QuickResponseCode::getNumber, quickResponseCodeFileQueryDto.getQuickResponseCodeNumber())
            );
            if (Objects.isNull(quickResponseCode)) {
                throw new BizException("找不到此二维码信息").end(ResEnum.NOT_FOUND);
            }
            queryWrapper.lambda().eq(QuickResponseCodeFile::getQuickResponseCodeId, quickResponseCode.getId());
        }
        List<QuickResponseCodeFile> quickResponseCodeFiles = list(queryWrapper);

        List<QuickResponseCodeFileVO> quickResponseCodeFileVos = new ArrayList<>();
        quickResponseCodeFiles.forEach(d -> {
            QuickResponseCodeFileVO quickResponseCodeFileVo = new QuickResponseCodeFileVO();
            BeanUtils.copyProperties(d, quickResponseCodeFileVo);
            quickResponseCodeFileVos.add(quickResponseCodeFileVo);
        });


        return quickResponseCodeFileVos;
    }

    @Override
    public boolean quickResponseCodeFileUpdate(QuickResponseCodeFileFormDTO quickResponseCodeFileFormDto) throws IOException {
        QuickResponseCodeFile quickResponseCodeFile = new QuickResponseCodeFile();
        BeanUtils.copyProperties(quickResponseCodeFileFormDto, quickResponseCodeFile);

        WeseeLink weseeLink = WeseeUtils.findLink(quickResponseCodeFile.getWeseeLink());
        quickResponseCodeFile.setWeseeImageLink(weseeLink.getImageLink());
        quickResponseCodeFile.setWeseeVideoLink(weseeLink.getVideoLink());

        return updateById(quickResponseCodeFile);
    }

    @Override
    public boolean quickResponseCodeFileUpdateBatch(List<QuickResponseCodeFileFormDTO> quickResponseCodeFileFormDtos) throws IOException {
        List<QuickResponseCodeFile> quickResponseCodeFiles = new ArrayList<>();
        AtomicBoolean error = new AtomicBoolean(false);
        quickResponseCodeFileFormDtos.forEach(d -> {
            QuickResponseCodeFile quickResponseCodeFile = new QuickResponseCodeFile();
            BeanUtils.copyProperties(d, quickResponseCodeFile);
            if(StringUtils.isNotBlank(d.getWeseeLink())){
                if (QuickResponseCodeFileConst.UPDATE_STATUS_TRUE.equals(quickResponseCodeFile.getUpdateStatus())) {
                    try {
                        WeseeLink weseeLink = WeseeUtils.findLink(quickResponseCodeFile.getWeseeLink());
                        quickResponseCodeFile.setWeseeImageLink(weseeLink.getImageLink());
                        quickResponseCodeFile.setWeseeVideoLink(weseeLink.getVideoLink());
                    } catch (IOException e) {
                        e.printStackTrace();
                        error.set(true);
                    }
                }
            }
            quickResponseCodeFiles.add(quickResponseCodeFile);
        });
        if (error.get()) {
            throw new BizException(ResEnum.UNKNOWN_ERROR);
        }
        return updateBatchById(quickResponseCodeFiles);
    }

    @Override
    public List<WeseeLink> weseeAnalysis(List<String> weseeLink){
        List<WeseeLink> weseeLinks = new ArrayList<>();
        weseeLink.forEach(d -> {
            try {
                weseeLinks.add(WeseeUtils.findLink(d));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return weseeLinks;
    }
}
