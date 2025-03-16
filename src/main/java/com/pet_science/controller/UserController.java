package com.pet_science.controller;


import com.alibaba.fastjson.JSONObject;
import com.pet_science.exception.BaseException;
import com.pet_science.pojo.Result;
import com.pet_science.pojo.User;
import com.pet_science.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/user")
@Api(tags = "用户接口")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @ApiOperation(value = "用户注册", notes = "注册新用户，用户名、邮箱和手机号必须唯一")
    public Result<String> register(@RequestBody @Valid User user) {
        try {
            String message = userService.register(user);
            return Result.successResultData(message);
        }catch (BaseException e){
            return Result.error(e.getCode(),e.getMessage());
        }
    }

    @PostMapping("/login")
    @ApiOperation(value = "用户登录", notes = "支持用户名、邮箱和手机号登录，系统会自动识别登录类型")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "account", value = "账号（用户名/邮箱/手机号）", required = true),
        @ApiImplicitParam(name = "password", value = "密码", required = true)
    })
    public Result<String> login(
            @RequestParam String account,
            @RequestParam String password) {
        try {
            String token = userService.login(account, password);
            return Result.successResultData(token);
        }catch (BaseException e){
            return Result.error(e.getCode(),e.getMessage());
        }
    }
}
