package com.pet_science.controller;

import com.alibaba.fastjson.JSONObject;
import com.pet_science.exception.BaseException;
import com.pet_science.pojo.Result;
import com.pet_science.service.AdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@Api(tags = "管理员接口")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @PostMapping("/login")
    @ApiOperation(value = "管理员登录", notes = "支持用户名、邮箱登录，系统会自动识别登录类型")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "account", value = "账号（用户名/邮箱）", required = true),
        @ApiImplicitParam(name = "password", value = "密码", required = true)
    })
    public Result<JSONObject> login(
            @RequestParam String account,
            @RequestParam String password) {
        JSONObject data = adminService.login(account, password);
        return Result.successResultData(data);
    }
}
