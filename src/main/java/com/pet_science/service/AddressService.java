package com.pet_science.service;

import com.pet_science.pojo.Order;
import com.pet_science.pojo.PageResult;
import com.pet_science.pojo.UserAddress;

import java.util.List;
import java.util.Map;

public interface AddressService {
    /**
     * 获取用户的所有收货地址
     * @param userId 用户ID
     * @return 地址列表
     */
    List<UserAddress> getUserAddresses(Integer userId);
    
    /**
     * 根据ID获取收货地址
     * @param id 地址ID
     * @param userId 用户ID
     * @return 地址信息
     */
    UserAddress getAddressById(Integer id, Integer userId);
    
    /**
     * 添加收货地址
     * @param address 地址信息
     * @return 添加的地址
     */
    UserAddress addAddress(UserAddress address);
    
    /**
     * 更新收货地址
     * @param address 地址信息
     * @return 是否更新成功
     */
    boolean updateAddress(UserAddress address);
    
    /**
     * 删除收货地址
     * @param id 地址ID
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteAddress(Integer id, Integer userId);
    
    /**
     * 设置默认地址
     * @param id 地址ID
     * @param userId 用户ID
     * @return 是否设置成功
     */
    boolean setDefaultAddress(Integer id, Integer userId);
    
    /**
     * 获取用户的默认地址
     * @param userId 用户ID
     * @return 默认地址
     */
    UserAddress getDefaultAddress(Integer userId);
}