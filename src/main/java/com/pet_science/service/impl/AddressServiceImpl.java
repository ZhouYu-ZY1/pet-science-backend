package com.pet_science.service.impl;

import com.pet_science.exception.BusinessException;
import com.pet_science.exception.SystemException;
import com.pet_science.mapper.AddressMapper;
import com.pet_science.pojo.*;
import com.pet_science.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    /**
     * 获取用户的所有收货地址
     *
     * @param userId 用户ID
     * @return 地址列表
     */
    @Override
    public List<UserAddress> getUserAddresses(Integer userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        return addressMapper.getUserAddresses(userId);
    }

    /**
     * 根据ID获取收货地址
     *
     * @param id     地址ID
     * @param userId 用户ID
     * @return 地址信息
     */
    @Override
    public UserAddress getAddressById(Integer id, Integer userId) {
        if (id == null) {
            throw new BusinessException("地址ID不能为空");
        }
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        UserAddress address = addressMapper.getAddressById(id);
        if (address == null) {
            throw new BusinessException("地址不存在");
        }
        
        // 验证地址是否属于当前用户
        if (!address.getUserId().equals(userId)) {
            throw new BusinessException("无权访问该地址");
        }
        
        return address;
    }

    /**
     * 添加收货地址
     *
     * @param address 地址信息
     * @return 添加的地址
     */
    @Override
    @Transactional
    public UserAddress addAddress(UserAddress address) {
        if (address == null) {
            throw new BusinessException("地址信息不能为空");
        }
        if (address.getUserId() == null) {
            throw new BusinessException("用户ID不能为空");
        }
        if (address.getRecipientName() == null || address.getRecipientName().trim().isEmpty()) {
            throw new BusinessException("收货人姓名不能为空");
        }
        if (address.getRecipientPhone() == null || address.getRecipientPhone().trim().isEmpty()) {
            throw new BusinessException("收货人手机号不能为空");
        }
        if (address.getProvince() == null || address.getProvince().trim().isEmpty()) {
            throw new BusinessException("省份不能为空");
        }
        if (address.getCity() == null || address.getCity().trim().isEmpty()) {
            throw new BusinessException("城市不能为空");
        }
        if (address.getDistrict() == null || address.getDistrict().trim().isEmpty()) {
            throw new BusinessException("区县不能为空");
        }
        if (address.getDetailAddress() == null || address.getDetailAddress().trim().isEmpty()) {
            throw new BusinessException("详细地址不能为空");
        }
        
        // 设置默认值
        if (address.getAddressTag() == null || address.getAddressTag().trim().isEmpty()) {
            address.setAddressTag("无标签");
        }
        
        // 如果是默认地址，先将该用户的所有地址设为非默认
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            addressMapper.clearDefaultAddress(address.getUserId());
        } else {
            address.setIsDefault(0);
        }
        
        // 添加地址
        int result = addressMapper.addAddress(address);
        if (result <= 0) {
            throw new SystemException("添加地址失败");
        }
        
        return addressMapper.getAddressById(address.getId());
    }

    /**
     * 更新收货地址
     *
     * @param address 地址信息
     * @return 是否更新成功
     */
    @Override
    @Transactional
    public boolean updateAddress(UserAddress address) {
        if (address == null) {
            throw new BusinessException("地址信息不能为空");
        }
        if (address.getId() == null) {
            throw new BusinessException("地址ID不能为空");
        }
        if (address.getUserId() == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        // 验证地址是否存在且属于当前用户
        UserAddress existingAddress = addressMapper.getAddressById(address.getId());
        if (existingAddress == null) {
            throw new BusinessException("地址不存在");
        }
        if (!existingAddress.getUserId().equals(address.getUserId())) {
            throw new BusinessException("无权修改该地址");
        }
        
        // 如果是默认地址，先将该用户的所有地址设为非默认
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            addressMapper.clearDefaultAddress(address.getUserId());
        }
        
        // 更新地址
        int result = addressMapper.updateAddress(address);
        return result > 0;
    }

    /**
     * 删除收货地址
     *
     * @param id     地址ID
     * @param userId 用户ID
     * @return 是否删除成功
     */
    @Override
    @Transactional
    public boolean deleteAddress(Integer id, Integer userId) {
        if (id == null) {
            throw new BusinessException("地址ID不能为空");
        }
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        // 验证地址是否存在且属于当前用户
        UserAddress existingAddress = addressMapper.getAddressById(id);
        if (existingAddress == null) {
            throw new BusinessException("地址不存在");
        }
        if (!existingAddress.getUserId().equals(userId)) {
            throw new BusinessException("无权删除该地址");
        }
        
        // 删除地址
        int result = addressMapper.deleteAddress(id, userId);
        
        // 如果删除的是默认地址，且用户还有其他地址，则将最新的一个地址设为默认地址
        if (existingAddress.getIsDefault() == 1) {
            List<UserAddress> remainingAddresses = addressMapper.getUserAddresses(userId);
            if (remainingAddresses != null && !remainingAddresses.isEmpty()) {
                addressMapper.setDefaultAddress(remainingAddresses.get(0).getId(), userId);
            }
        }
        
        return result > 0;
    }

    /**
     * 设置默认地址
     *
     * @param id     地址ID
     * @param userId 用户ID
     * @return 是否设置成功
     */
    @Override
    @Transactional
    public boolean setDefaultAddress(Integer id, Integer userId) {
        if (id == null) {
            throw new BusinessException("地址ID不能为空");
        }
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        // 验证地址是否存在且属于当前用户
        UserAddress existingAddress = addressMapper.getAddressById(id);
        if (existingAddress == null) {
            throw new BusinessException("地址不存在");
        }
        if (!existingAddress.getUserId().equals(userId)) {
            throw new BusinessException("无权设置该地址");
        }
        
        // 先将该用户的所有地址设为非默认
        addressMapper.clearDefaultAddress(userId);
        
        // 设置默认地址
        int result = addressMapper.setDefaultAddress(id, userId);
        return result > 0;
    }

    /**
     * 获取用户的默认地址
     *
     * @param userId 用户ID
     * @return 默认地址
     */
    @Override
    public UserAddress getDefaultAddress(Integer userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        return addressMapper.getDefaultAddress(userId);
    }
}