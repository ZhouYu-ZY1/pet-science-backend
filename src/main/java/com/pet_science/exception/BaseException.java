package com.pet_science.exception;

public class BaseException extends RuntimeException {
    private Integer code;
    private Integer status;
    private String message;

    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
        this.status = code;
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}