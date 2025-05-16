package com.pet_science.mapper;

import com.pet_science.pojo.Pet;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PetMapper {
    /**
     * 添加宠物
     */
    @Insert("INSERT INTO pets (user_id, name, type, breed, birthday, avatar_url, created_at, updated_at) " +
            "VALUES (#{userId}, #{name}, #{type}, #{breed}, #{birthday}, #{avatarUrl}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int addPet(Pet pet);
    
    /**
     * 更新宠物信息
     */
    @Update("<script>" +
            "UPDATE pets " +
            "<set>" +
            "   <if test='name != null and name != \"\"'>name = #{name},</if>" +
            "   <if test='type != null and type != \"\"'>type = #{type},</if>" +
            "   <if test='breed != null and breed != \"\"'>breed = #{breed},</if>" +
            "   <if test='birthday != null'>birthday = #{birthday},</if>" +
            "   <if test='avatarUrl != null and avatarUrl != \"\"'>avatar_url = #{avatarUrl},</if>" +
            "   updated_at = NOW()" +
            "</set>" +
            "WHERE id = #{id} AND user_id = #{userId}" +
            "</script>")
    int updatePet(Pet pet);
    
    /**
     * 删除宠物
     */
    @Delete("DELETE FROM pets WHERE id = #{id} AND user_id = #{userId}")
    int deletePet(@Param("id") Long id, @Param("userId") Integer userId);
    
    /**
     * 根据ID获取宠物信息
     */
    @Select("SELECT * FROM pets WHERE id = #{id}")
    Pet getPetById(Long id);
    
    /**
     * 获取用户的所有宠物
     */
    @Select("SELECT * FROM pets WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Pet> getUserPets(Integer userId);
}