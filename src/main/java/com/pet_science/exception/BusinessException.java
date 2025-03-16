package com.pet_science.exception;

/**
 * 业务异常
 */
public class BusinessException extends BaseException {
    //处理异常
    public BusinessException(String message) {
        super(400, message);
    }
}