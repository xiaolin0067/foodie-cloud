package com.zlin.user.exception;

import com.zlin.pojo.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * @author zlin
 * @date 20210124
 */
@RestControllerAdvice
public class CustomExceptionHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler
    public Result handlerUploadFileMaxSize(MaxUploadSizeExceededException e) {
        LOGGER.error("上传文件大小超过限制", e);
        return Result.errorMsg("上传文件大小超过限制");
    }
}
