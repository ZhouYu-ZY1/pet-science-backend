package com.pet_science.exception;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.pet_science.pojo.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;

// 全局异常处理器
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BaseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Result<?>> handleBaseException(BaseException e) {
        // 处理已知异常
        ResponseEntity<Result<?>> handled = handleKnownException(e);
        if(handled != null){
            return handled;
        }
        // 处理业务异常
        logger.error("业务异常", e);
        return ResponseEntity.status(HttpStatus.valueOf(e.getCode()))
                .body(Result.error(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Result<?>> handleException(Exception e) {
        // 处理已知异常
        ResponseEntity<Result<?>> handled = handleKnownException(e);
        if(handled != null){
            return handled;
        }
        // 处理未知异常
        logger.error("系统错误", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error(500, "操作失败"));
    }

    /**
     * 处理已知异常
     */
    public ResponseEntity<Result<?>> handleKnownException(Exception e){
        if(e instanceof TokenExpiredException ||
                (e instanceof NullPointerException
                        && e.getMessage().contains("because \"jwt\" is null"))){
            logger.error("Token过期", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Result.error(401, "Token过期"));

        }
        return null;
    }
}