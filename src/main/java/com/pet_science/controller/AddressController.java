package com.pet_science.controller;

import com.pet_science.annotation.RequireUser;
import com.pet_science.exception.BusinessException;
import com.pet_science.pojo.Result;
import com.pet_science.pojo.user.UserAddress;
import com.pet_science.service.AddressService;
import com.pet_science.utils.JWTUtil;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
@RequireUser // 用户身份验证注解
@Api(tags = "地址管理接口")
public class AddressController {
    
    @Autowired
    private AddressService addressService;
    
    @GetMapping("/list")
    @ApiOperation(value = "获取用户的所有收货地址", notes = "获取当前用户的所有收货地址")
    public Result<List<UserAddress>> getUserAddresses(@RequestHeader("Authorization") String token) {
        Integer userId = JWTUtil.getUserId(token);
        List<UserAddress> addresses = addressService.getUserAddresses(userId);
        return Result.successResultData(addresses);
    }
    
    @GetMapping("/{id}")
    @ApiOperation(value = "获取收货地址详情", notes = "根据地址ID获取收货地址详情")
    @ApiImplicitParam(name = "id", value = "地址ID", required = true, dataType = "Integer", paramType = "path")
    public Result<UserAddress> getAddressDetail(@PathVariable("id") Integer id, @RequestHeader("Authorization") String token) {
        Integer userId = JWTUtil.getUserId(token);
        UserAddress address = addressService.getAddressById(id, userId);
        return Result.successResultData(address);
    }
    
    @GetMapping("/default")
    @ApiOperation(value = "获取默认收货地址", notes = "获取当前用户的默认收货地址")
    public Result<UserAddress> getDefaultAddress(@RequestHeader("Authorization") String token) {
        Integer userId = JWTUtil.getUserId(token);
        UserAddress address = addressService.getDefaultAddress(userId);
        return Result.successResultData(address);
    }
    
    @PostMapping("/add")
    @ApiOperation(value = "添加收货地址", notes = "添加新的收货地址")
    public Result<UserAddress> addAddress(@RequestBody UserAddress address, @RequestHeader("Authorization") String token) {
        Integer userId = JWTUtil.getUserId(token);
        address.setUserId(userId);
        UserAddress addedAddress = addressService.addAddress(address);
        return Result.successResultData(addedAddress);
    }
    
    @PutMapping("/update")
    @ApiOperation(value = "更新收货地址", notes = "更新指定的收货地址")
    public Result<String> updateAddress(@RequestBody UserAddress address, @RequestHeader("Authorization") String token) {
        Integer userId = JWTUtil.getUserId(token);
        address.setUserId(userId);
        boolean result = addressService.updateAddress(address);
        if (result) {
            return Result.successResultData("更新地址成功");
        }
        throw new BusinessException("更新地址失败");
    }
    
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除收货地址", notes = "删除指定的收货地址")
    @ApiImplicitParam(name = "id", value = "地址ID", required = true, dataType = "Integer", paramType = "path")
    public Result<String> deleteAddress(@PathVariable("id") Integer id, @RequestHeader("Authorization") String token) {
        Integer userId = JWTUtil.getUserId(token);
        boolean result = addressService.deleteAddress(id, userId);
        if (result) {
            return Result.successResultData("删除地址成功");
        }
        throw new BusinessException("删除地址失败");
    }
    
    @PutMapping("/default/{id}")
    @ApiOperation(value = "设置默认地址", notes = "将指定地址设置为默认收货地址")
    @ApiImplicitParam(name = "id", value = "地址ID", required = true, dataType = "Integer", paramType = "path")
    public Result<String> setDefaultAddress(@PathVariable("id") Integer id, @RequestHeader("Authorization") String token) {
        Integer userId = JWTUtil.getUserId(token);
        boolean result = addressService.setDefaultAddress(id, userId);
        if (result) {
            return Result.successResultData("设置默认地址成功");
        }
        throw new BusinessException("设置默认地址失败");
    }
}