package com.pet_science.exception;

/**
 * 系统异常
 */
public class SystemException extends BaseException {
    public SystemException(String message) {
        super(500, message);
    }
}