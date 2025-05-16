package com.pet_science.service;

import com.pet_science.pojo.Pet;
import com.pet_science.pojo.PageResult;

import java.util.List;

public interface PetService {
    /**
     * 添加宠物
     * @param pet 宠物信息
     * @return 添加的宠物
     */
    Pet addPet(Pet pet);
    
    /**
     * 更新宠物信息
     * @param pet 宠物信息
     * @return 是否更新成功
     */
    boolean updatePet(Pet pet);
    
    /**
     * 删除宠物
     * @param id 宠物ID
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deletePet(Long id, Integer userId);
    
    /**
     * 获取宠物详情
     * @param id 宠物ID
     * @param userId 用户ID（用于权限验证）
     * @return 宠物信息
     */
    Pet getPetDetail(Long id, Integer userId);
    
    /**
     * 获取用户的所有宠物
     * @param userId 用户ID
     * @return 宠物列表
     */
    List<Pet> getUserPets(Integer userId);
    
    /**
     * 获取用户的宠物列表（分页）
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @param userId 用户ID
     * @return 分页结果
     */
    PageResult<Pet> getUserPetsPage(Integer pageNum, Integer pageSize, Integer userId);
}