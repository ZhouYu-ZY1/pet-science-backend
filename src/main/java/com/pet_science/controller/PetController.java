package com.pet_science.controller;

import com.pet_science.annotation.RequireUser;
import com.pet_science.exception.BusinessException;
import com.pet_science.pojo.PageResult;
import com.pet_science.pojo.Pet;
import com.pet_science.pojo.Result;
import com.pet_science.service.PetService;
import com.pet_science.utils.JWTUtil;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pet")
@RequireUser
@Api(tags = "宠物管理接口")
public class PetController {
    
    @Autowired
    private PetService petService;
    
    @GetMapping("/list")
    @ApiOperation(value = "获取用户的所有宠物", notes = "获取当前用户的所有宠物")
    public Result<List<Pet>> getUserPets(@RequestParam("userId") Integer userId,@RequestHeader("Authorization") String token) {
        if(userId == null || userId == 0){
            // 从token中获取用户ID
            userId = JWTUtil.getUserId(token);
        }
        List<Pet> pets = petService.getUserPets(userId);
        return Result.successResultData(pets);
    }
    
    @GetMapping("/page")
    @ApiOperation(value = "分页获取用户的宠物", notes = "分页获取当前用户的宠物")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Integer"),
        @ApiImplicitParam(name = "pageSize", value = "每页记录数", dataType = "Integer")
    })
    public Result<PageResult<Pet>> getUserPetsPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestHeader("Authorization") String token) {
        Integer userId = JWTUtil.getUserId(token);
        PageResult<Pet> pageResult = petService.getUserPetsPage(pageNum, pageSize, userId);
        return Result.successResultData(pageResult);
    }
    
    @GetMapping("/{id}")
    @ApiOperation(value = "获取宠物详情", notes = "根据宠物ID获取宠物详情")
    @ApiImplicitParam(name = "id", value = "宠物ID", required = true, dataType = "Long", paramType = "path")
    public Result<Pet> getPetDetail(@PathVariable("id") Long id, @RequestHeader("Authorization") String token) {
        Integer userId = JWTUtil.getUserId(token);
        Pet pet = petService.getPetDetail(id, userId);
        return Result.successResultData(pet);
    }
    
    @PostMapping("/add")
    @ApiOperation(value = "添加宠物", notes = "添加新的宠物")
    public Result<Pet> addPet(@RequestBody Pet pet, @RequestHeader("Authorization") String token) {
        Integer userId = JWTUtil.getUserId(token);
        pet.setUserId(userId);
        Pet addedPet = petService.addPet(pet);
        return Result.successResultData(addedPet);
    }
    
    @PutMapping("/update")
    @ApiOperation(value = "更新宠物信息", notes = "更新指定的宠物信息")
    public Result<String> updatePet(@RequestBody Pet pet, @RequestHeader("Authorization") String token) {
        Integer userId = JWTUtil.getUserId(token);
        pet.setUserId(userId);
        boolean result = petService.updatePet(pet);
        if (result) {
            return Result.successResultData("更新宠物信息成功");
        }
        throw new BusinessException("更新宠物信息失败");
    }
    
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除宠物", notes = "删除指定的宠物")
    @ApiImplicitParam(name = "id", value = "宠物ID", required = true, dataType = "Long", paramType = "path")
    public Result<String> deletePet(@PathVariable("id") Long id, @RequestHeader("Authorization") String token) {
        Integer userId = JWTUtil.getUserId(token);
        boolean result = petService.deletePet(id, userId);
        if (result) {
            return Result.successResultData("删除宠物成功");
        }
        throw new BusinessException("删除宠物失败");
    }
}