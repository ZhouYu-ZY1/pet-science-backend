package com.pet_science.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pet_science.exception.BusinessException;
import com.pet_science.exception.SystemException;
import com.pet_science.mapper.PetMapper;
import com.pet_science.pojo.PageResult;
import com.pet_science.pojo.Pet;
import com.pet_science.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class PetServiceImpl implements PetService {

    @Autowired
    private PetMapper petMapper;

    @Override
    @Transactional
    public Pet addPet(Pet pet) {
        if (pet == null) {
            throw new BusinessException("宠物信息不能为空");
        }
        if (pet.getUserId() == null) {
            throw new BusinessException("用户ID不能为空");
        }
        if (pet.getName() == null || pet.getName().trim().isEmpty()) {
            throw new BusinessException("宠物名称不能为空");
        }
        if (pet.getType() == null || pet.getType().trim().isEmpty()) {
            throw new BusinessException("宠物类型不能为空");
        }
        
        // 设置默认值
        setPetDefaultAvatar(pet);

        // 添加宠物
        int result = petMapper.addPet(pet);
        if (result <= 0) {
            throw new SystemException("添加宠物失败");
        }
        
        return petMapper.getPetById(pet.getId());
    }

    private void setPetDefaultAvatar(Pet pet) {
        if (pet.getAvatarUrl() == null || pet.getAvatarUrl().trim().isEmpty()) {
            switch (pet.getType()) {
                case "cat" -> pet.setAvatarUrl("/images/default/default_cat.jpg");
                case "dog" -> pet.setAvatarUrl("/images/default/default_dog.jpg");
                default -> pet.setAvatarUrl("/images/default/default_other_pet.png");
            }
        }
    }

    @Override
    @Transactional
    public boolean updatePet(Pet pet) {
        if (pet == null) {
            throw new BusinessException("宠物信息不能为空");
        }
        if (pet.getId() == null) {
            throw new BusinessException("宠物ID不能为空");
        }
        if (pet.getUserId() == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        // 验证宠物是否存在且属于当前用户
        Pet existingPet = petMapper.getPetById(pet.getId());
        if (existingPet == null) {
            throw new BusinessException("宠物不存在");
        }
        if (!existingPet.getUserId().equals(pet.getUserId())) {
            throw new BusinessException("无权修改该宠物信息");
        }

        // 如果宠物类型发生变化且没有提供头像，则设置更新默认头像
        if(!existingPet.getType().equals(pet.getType())){
            setPetDefaultAvatar(pet);
        }
        
        // 更新宠物信息
        int result = petMapper.updatePet(pet);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean deletePet(Long id, Integer userId) {
        if (id == null) {
            throw new BusinessException("宠物ID不能为空");
        }
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        // 验证宠物是否存在且属于当前用户
        Pet existingPet = petMapper.getPetById(id);
        if (existingPet == null) {
            throw new BusinessException("宠物不存在");
        }
        if (!existingPet.getUserId().equals(userId)) {
            throw new BusinessException("无权删除该宠物");
        }
        
        // 删除宠物
        int result = petMapper.deletePet(id, userId);
        return result > 0;
    }

    @Override
    public Pet getPetDetail(Long id, Integer userId) {
        if (id == null) {
            throw new BusinessException("宠物ID不能为空");
        }
        
        Pet pet = petMapper.getPetById(id);
        if (pet == null) {
            throw new BusinessException("宠物不存在");
        }
        
        // 如果提供了用户ID，则验证宠物是否属于该用户
        if (userId != null && !pet.getUserId().equals(userId)) {
            throw new BusinessException("无权查看该宠物信息");
        }
        
        return pet;
    }

    @Override
    public List<Pet> getUserPets(Integer userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        return petMapper.getUserPets(userId);
    }

    @Override
    public PageResult<Pet> getUserPetsPage(Integer pageNum, Integer pageSize, Integer userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        // 设置分页参数
        PageHelper.startPage(pageNum, pageSize);
        
        // 查询数据
        List<Pet> petList = petMapper.getUserPets(userId);
        
        // 获取分页信息
        PageInfo<Pet> pageInfo = new PageInfo<>(petList);
        
        // 返回分页结果
        return PageResult.restPage(pageInfo);
    }
}