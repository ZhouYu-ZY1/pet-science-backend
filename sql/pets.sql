/*
 Navicat MySQL Data Transfer

 Source Server         : localhost3307_8.0
 Source Server Type    : MySQL
 Source Server Version : 80042
 Source Host           : localhost:3307
 Source Schema         : pet_science

 Target Server Type    : MySQL
 Target Server Version : 80042
 File Encoding         : 65001

 Date: 26/05/2025 11:27:06
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for pets
-- ----------------------------
DROP TABLE IF EXISTS `pets`;
CREATE TABLE `pets`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '宠物ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '宠物名称',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '宠物类型：cat-猫, dog-狗, other-其他',
  `breed` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '宠物品种',
  `birthday` date NULL DEFAULT NULL COMMENT '出生日期',
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '/statics/images/defaultPetAvatar.jpg' COMMENT '宠物头像',
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE COMMENT '用户ID索引',
  CONSTRAINT `pets_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 426 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '宠物信息表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
