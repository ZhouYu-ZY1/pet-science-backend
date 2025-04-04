package com.pet_science.controller;


import com.alibaba.fastjson.JSONObject;
import com.pet_science.exception.BaseException;
import com.pet_science.pojo.PageResult;
import com.pet_science.pojo.Result;
import com.pet_science.pojo.User;
import com.pet_science.service.UserService;
import com.pet_science.service.EmailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/user")
@Api(tags = "用户接口")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @ApiOperation(value = "用户登录", notes = "支持用户名、邮箱和手机号登录，系统会自动识别登录类型")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "account", value = "账号（用户名/邮箱/手机号）", required = true),
        @ApiImplicitParam(name = "password", value = "密码", required = true)
    })
    public Result<JSONObject> login(
            @RequestParam String account,
            @RequestParam String password) {
        try {
            JSONObject data = userService.login(account, password);
            return Result.successResultData(data);
        }catch (BaseException e){
            return Result.error(e.getCode(),e.getMessage());
        }
    }

    @PostMapping("/sendVerificationCode")
    @ApiOperation(value = "发送邮箱验证码")
    public Result<String> sendVerificationCode(@RequestParam String email) {
        try {
            boolean isSend = userService.sendVerificationCode(email);
            if(isSend){
                return Result.successResultData("验证码发送成功");
            }else {
                return Result.error(400, "验证码发送失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "验证码发送失败");
        }
    }

    @PostMapping("/loginByCode")
    @ApiOperation(value = "邮箱验证码登录", notes = "验证码登录，如果邮箱不存在则自动注册")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "email", value = "邮箱", required = true),
        @ApiImplicitParam(name = "code", value = "验证码", required = true)
    })
    public Result<JSONObject> loginByCode(
            @RequestParam String email,
            @RequestParam String code) {
        try {
            JSONObject token = userService.loginByCode(email, code);
            return Result.successResultData(token);
        } catch (BaseException e) {
            return Result.error(e.getCode(), e.getMessage());
        }
    }
    
    @GetMapping("/list")
    @ApiOperation(value = "获取用户列表", notes = "支持按用户名、邮箱、手机号和状态筛选，支持分页查询")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Integer"),
        @ApiImplicitParam(name = "pageSize", value = "每页记录数", dataType = "Integer"),
        @ApiImplicitParam(name = "username", value = "用户名", dataType = "String"),
        @ApiImplicitParam(name = "email", value = "邮箱", dataType = "String"),
        @ApiImplicitParam(name = "mobile", value = "手机号", dataType = "String"),
        @ApiImplicitParam(name = "status", value = "状态", dataType = "Integer")
    })
    public Result<PageResult<User>> getUserList(@RequestParam(required = false) Map<String, Object> params) {
        try {
            // 获取分页参数
            Integer pageNum = params.get("pageNum") != null ? Integer.parseInt(params.get("pageNum").toString()) : 1;
            Integer pageSize = params.get("pageSize") != null ? Integer.parseInt(params.get("pageSize").toString()) : 10;
            
            // 调用服务层方法获取分页数据
            PageResult<User> pageResult = userService.getUserListPage(pageNum, pageSize, params);
            return Result.successResultData(pageResult);
        } catch (BaseException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "获取用户列表失败");
        }
    }
    
    @GetMapping("/{id}")
    @ApiOperation(value = "获取用户详情", notes = "根据用户ID获取用户详细信息")
    @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Integer", paramType = "path")
    public Result<User> getUserDetail(@PathVariable("id") Integer id) {
        try {
            User user = userService.getUserDetail(id);
            return Result.successResultData(user);
        } catch (BaseException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "获取用户详情失败");
        }
    }
    
    @PutMapping("/status")
    @ApiOperation(value = "更新用户状态", notes = "更新指定用户的状态")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "Integer"),
        @ApiImplicitParam(name = "status", value = "状态值", required = true, dataType = "Integer")
    })
    public Result<String> updateUserStatus(@RequestBody JSONObject jsonObject) {
        try {
            Integer userId = jsonObject.getInteger("userId");
            Integer status = jsonObject.getInteger("status");
            boolean result = userService.updateUserStatus(userId, status);
            if (result) {
                return Result.successResultData("更新用户状态成功");
            } else {
                return Result.error(400, "更新用户状态失败");
            }
        } catch (BaseException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "更新用户状态失败");
        }
    }
}
