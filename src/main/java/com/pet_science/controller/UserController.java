package com.pet_science.controller;


import com.alibaba.fastjson.JSONObject;
import com.pet_science.annotation.RequireAdmin;
import com.pet_science.annotation.RequireUser;
import com.pet_science.exception.BusinessException;
import com.pet_science.pojo.user.FollowVO;
import com.pet_science.pojo.PageResult;
import com.pet_science.pojo.Result;
import com.pet_science.pojo.user.User;
import com.pet_science.service.UserService;
import com.pet_science.utils.JWTUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
//        account = "test01";
        JSONObject data = userService.login(account, password);
        return Result.successResultData(data);
    }

    @PostMapping("/sendVerificationCode")
    @ApiOperation(value = "发送邮箱验证码")
    public Result<String> sendVerificationCode(@RequestBody JSONObject json) {
        String email = json.getString("email");
        boolean isSend = userService.sendVerificationCode(email);
        if(isSend){
            return Result.successResultData("验证码发送成功");
        }
        throw new BusinessException("验证码发送失败");
    }

    @PostMapping("/loginByCode")
    @ApiOperation(value = "邮箱验证码登录", notes = "验证码登录，如果邮箱不存在则自动注册")
    public Result<JSONObject> loginByCode(@RequestBody JSONObject json) {
        String email = json.getString("email");
        String code = json.getString("code");
        JSONObject result = userService.loginByCode(email, code);
        return Result.successResultData(result);
    }
    
    @GetMapping("/list")
    @RequireUser
    @ApiOperation(value = "获取用户列表", notes = "支持按用户名、邮箱、手机号和状态筛选，支持分页查询")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Integer"),
        @ApiImplicitParam(name = "pageSize", value = "每页记录数", dataType = "Integer"),
        @ApiImplicitParam(name = "username", value = "用户名", dataType = "String"),
        @ApiImplicitParam(name = "email", value = "邮箱", dataType = "String"),
        @ApiImplicitParam(name = "mobile", value = "手机号", dataType = "String"),
        @ApiImplicitParam(name = "status", value = "状态", dataType = "Integer")
    })
    public Result<PageResult<User>> getUserList(@RequestParam(required = false) Map<String, Object> params,@RequestHeader("Authorization") String token) {
        // 获取分页参数
        Integer pageNum = params.get("pageNum") != null ? Integer.parseInt(params.get("pageNum").toString()) : 1;
        Integer pageSize = params.get("pageSize") != null ? Integer.parseInt(params.get("pageSize").toString()) : 10;
        
        // 调用服务层方法获取分页数据
        PageResult<User> pageResult = userService.getUserListPage(pageNum, pageSize, params,token);
        return Result.successResultData(pageResult);
    }
    
    @GetMapping("/{id}")
    @ApiOperation(value = "获取用户详情", notes = "根据用户ID获取用户详细信息")
    @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Integer", paramType = "path")
    public Result<User> getUserDetail(@PathVariable("id") Integer id, @RequestHeader("Authorization") String token) {
        Integer queryId = null;
        if(!JWTUtil.verifyAdmin(token)){ // 如果不是管理员，则获取当前查询用户ID
            queryId = JWTUtil.getUserId(token);
        }
        User user = userService.getUserDetail(id,queryId);
        return Result.successResultData(user);
    }


    @GetMapping("/getUserInfo")
    public Result<User> getUserInfo(@RequestParam("id") Integer id, @RequestHeader("Authorization") String token) {
        if(id == null || id == 0){
            // 从token中获取用户ID
            id = JWTUtil.getUserId(token);
        }
        Result<User> userDetail = getUserDetail(id,token);
        return userDetail;
    }

    @PutMapping("/status")
    @RequireAdmin
    @ApiOperation(value = "更新用户状态", notes = "更新指定用户的状态")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "Integer"),
        @ApiImplicitParam(name = "status", value = "状态值", required = true, dataType = "Integer")
    })
    public Result<String> updateUserStatus(@RequestBody JSONObject jsonObject) {
        Integer userId = jsonObject.getInteger("userId");
        Integer status = jsonObject.getInteger("status");
        boolean result = userService.updateUserStatus(userId, status);
        if (result) {
            return Result.successResultData("更新用户状态成功");
        }
        throw new BusinessException("更新用户状态失败");
    }
    
    @PutMapping("/update")
    @RequireUser
    @ApiOperation(value = "更新用户信息", notes = "更新用户的基本信息")
    public Result<String> updateUser(@RequestBody User user, @RequestHeader("Authorization") String token) {
        // 从token中获取用户ID
        Integer userId = JWTUtil.getUserId(token);
        user.setUserId(userId);
        boolean result = userService.updateUser(user);
        if (result) {
            return Result.successResultData("用户信息更新成功");
        }
        throw new BusinessException("用户信息更新失败");
    }

    @PostMapping("/followUser")
    @RequireUser
    @ApiOperation(value = "关注/取消关注用户", notes = "关注或取消关注指定用户")
    public Result<String> followUser(@RequestHeader("Authorization") String token, @RequestParam Integer toUserId,@RequestParam boolean isFollow) {
        Integer fromUserId = JWTUtil.getUserId(token);
        String type = isFollow ? "关注":"取消关注";
        FollowVO follow = new FollowVO();
        follow.setFromUserId(fromUserId);
        follow.setToUserId(toUserId);
        boolean result = userService.followUser(follow,isFollow);
        if (result) {
            return Result.successResultData(type+"成功");
        }
        throw new BusinessException(type+"失败");
    }

    @GetMapping("/getFollowList")
    @RequireUser
    @ApiOperation(value = "获取关注列表", notes = "获取当前用户的关注列表")
    public Result<List<User>> getFollowList(@RequestHeader("Authorization") String token) {
        Integer userId = JWTUtil.getUserId(token);
        PageResult<User> users = userService.getFollowList(1,20,userId);
        List<User> list = users.getList();
        return Result.successResultData(list);
    }

    @GetMapping("/getFansList")
    @RequireUser
    @ApiOperation(value = "获取粉丝列表", notes = "获取当前用户的粉丝列表")
    public Result<List<User>> getFansList(@RequestHeader("Authorization") String token) {
        Integer userId = JWTUtil.getUserId(token);
        PageResult<User> users = userService.getFansList(1,20,userId);
        List<User> list = users.getList();
        return Result.successResultData(list);
    }
}
