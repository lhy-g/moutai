package com.tongchuang.general.core.handler;

import com.tongchuang.general.core.exception.BizException;
import com.tongchuang.general.core.web.responce.R;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
public class BaseExceptionHandler {

    @ExceptionHandler(value = BizException.class)
    public R handleFebsException(BizException e) {
        log.error("业务错误", e);
        R r = new R();
        r.setCode(e.getCode());
        r.setMsg(e.getMessage());
        return r;
    }
}
