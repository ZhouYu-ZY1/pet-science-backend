package com.pet_science.mapper;

import com.pet_science.pojo.Order;
import com.pet_science.pojo.UserAddress;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface AddressMapper {
    /**
     * 获取用户的所有收货地址
     * @param userId 用户ID
     * @return 地址列表
     */
    @Select("SELECT * FROM user_addresses WHERE user_id = #{userId} ORDER BY is_default DESC, updated_at DESC")
    List<UserAddress> getUserAddresses(Integer userId);
    
    /**
     * 根据ID获取收货地址
     * @param id 地址ID
     * @return 地址信息
     */
    @Select("SELECT * FROM user_addresses WHERE id = #{id}")
    UserAddress getAddressById(Integer id);
    
    /**
     * 添加收货地址
     * @param address 地址信息
     * @return 影响行数
     */
    @Insert("INSERT INTO user_addresses (user_id, recipient_name, recipient_phone, province, city, district, " +
            "detail_address, address_tag, is_default, created_at, updated_at) " +
            "VALUES (#{userId}, #{recipientName}, #{recipientPhone}, #{province}, #{city}, #{district}, " +
            "#{detailAddress}, #{addressTag}, #{isDefault}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int addAddress(UserAddress address);
    
    /**
     * 更新收货地址
     * @param address 地址信息
     * @return 影响行数
     */
    @Update("UPDATE user_addresses SET recipient_name = #{recipientName}, recipient_phone = #{recipientPhone}, " +
            "province = #{province}, city = #{city}, district = #{district}, detail_address = #{detailAddress}, " +
            "address_tag = #{addressTag}, is_default = #{isDefault}, updated_at = NOW() " +
            "WHERE id = #{id} AND user_id = #{userId}")
    int updateAddress(UserAddress address);
    
    /**
     * 删除收货地址
     * @param id 地址ID
     * @param userId 用户ID
     * @return 影响行数
     */
    @Delete("DELETE FROM user_addresses WHERE id = #{id} AND user_id = #{userId}")
    int deleteAddress(@Param("id") Integer id, @Param("userId") Integer userId);
    
    /**
     * 将用户的所有地址设置为非默认
     * @param userId 用户ID
     * @return 影响行数
     */
    @Update("UPDATE user_addresses SET is_default = 0 WHERE user_id = #{userId}")
    int clearDefaultAddress(Integer userId);
    
    /**
     * 设置默认地址
     * @param id 地址ID
     * @param userId 用户ID
     * @return 影响行数
     */
    @Update("UPDATE user_addresses SET is_default = 1 WHERE id = #{id} AND user_id = #{userId}")
    int setDefaultAddress(@Param("id") Integer id, @Param("userId") Integer userId);
    
    /**
     * 获取用户的默认地址
     * @param userId 用户ID
     * @return 默认地址
     */
    @Select("SELECT * FROM user_addresses WHERE user_id = #{userId} AND is_default = 1 LIMIT 1")
    UserAddress getDefaultAddress(Integer userId);
}