package com.itheima.tanhua.exception;

import com.itheima.tanhua.pojo.ErrorResult;
import lombok.Data;

/**
 * 自定义异常类
 */
@Data
public class BusinessException extends RuntimeException {

    private ErrorResult errorResult;

    public BusinessException(ErrorResult errorResult) {
        super(errorResult.getErrMessage());
        this.errorResult = errorResult;
    }
}