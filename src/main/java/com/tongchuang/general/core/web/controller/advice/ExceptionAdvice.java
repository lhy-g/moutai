package com.tongchuang.general.core.web.controller.advice;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tongchuang.general.core.handler.BaseExceptionHandler;

@RestControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class ExceptionAdvice extends BaseExceptionHandler {
}
