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

 Date: 21/05/2025 14:20:04
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `admin_id` bigint NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `last_login_time` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',
  `created_at` datetime(0) NOT NULL COMMENT '创建时间',
  `updated_at` datetime(0) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`admin_id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '管理员表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, 'admin', '$2a$10$4ygeK4eTLZYV1xqsB6s9Yeh2nQunWiZ.iXStTqcrhBhxy8PEaQp/S', '系统管理员', 'admin@example.com', NULL, NULL, 1, '2025-04-25 16:53:07', '2025-04-03 20:50:02', '2025-04-03 20:50:02');

-- ----------------------------
-- Table structure for content_likes
-- ----------------------------
DROP TABLE IF EXISTS `content_likes`;
CREATE TABLE `content_likes`  (
  `like_id` int NOT NULL AUTO_INCREMENT COMMENT '点赞记录ID',
  `content_id` int NOT NULL COMMENT '被点赞内容ID',
  `user_id` int NOT NULL COMMENT '点赞用户ID',
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '点赞时间',
  PRIMARY KEY (`like_id`) USING BTREE,
  UNIQUE INDEX `unique_like`(`content_id`, `user_id`) USING BTREE COMMENT '防止重复点赞',
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `content_likes_ibfk_1` FOREIGN KEY (`content_id`) REFERENCES `contents` (`content_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `content_likes_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '内容点赞记录表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of content_likes
-- ----------------------------

-- ----------------------------
-- Table structure for content_media
-- ----------------------------
DROP TABLE IF EXISTS `content_media`;
CREATE TABLE `content_media`  (
  `media_id` int NOT NULL AUTO_INCREMENT COMMENT '媒体文件ID',
  `content_id` int NOT NULL COMMENT '关联内容ID',
  `media_type` enum('video','image') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '媒体类型',
  `media_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件存储路径',
  `duration` int NULL DEFAULT NULL COMMENT '视频时长（秒）',
  `sort_order` tinyint NULL DEFAULT 1 COMMENT '多图/视频排序序号',
  PRIMARY KEY (`media_id`) USING BTREE,
  INDEX `content_id`(`content_id`) USING BTREE,
  CONSTRAINT `content_media_ibfk_1` FOREIGN KEY (`content_id`) REFERENCES `contents` (`content_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '内容多媒体附件表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of content_media
-- ----------------------------

-- ----------------------------
-- Table structure for contents
-- ----------------------------
DROP TABLE IF EXISTS `contents`;
CREATE TABLE `contents`  (
  `content_id` int NOT NULL AUTO_INCREMENT COMMENT '内容唯一ID',
  `user_id` int NOT NULL COMMENT '发布者ID',
  `content_type` enum('video','image','text') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '内容类型',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '内容标题',
  `content_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '正文内容',
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地理位置信息',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览计数',
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '发布时间',
  PRIMARY KEY (`content_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `contents_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户生成内容主表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of contents
-- ----------------------------

-- ----------------------------
-- Table structure for follows
-- ----------------------------
DROP TABLE IF EXISTS `follows`;
CREATE TABLE `follows`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `from_user_id` int NOT NULL COMMENT '关注者用户ID',
  `to_user_id` int NOT NULL COMMENT '被关注者用户ID',
  `follow_time` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '关注时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_from_to`(`from_user_id`, `to_user_id`) USING BTREE,
  INDEX `idx_to_user_id`(`to_user_id`) USING BTREE,
  CONSTRAINT `fk_follow_from_user` FOREIGN KEY (`from_user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_follow_to_user` FOREIGN KEY (`to_user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '关注记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of follows
-- ----------------------------
INSERT INTO `follows` VALUES (2, 7, 1, '2025-04-21 12:07:41');
INSERT INTO `follows` VALUES (4, 7, 2, '2025-04-21 12:23:51');
INSERT INTO `follows` VALUES (5, 7, 6, '2025-04-21 13:32:49');
INSERT INTO `follows` VALUES (8, 8, 7, '2025-04-22 15:31:25');
INSERT INTO `follows` VALUES (17, 9, 7, '2025-04-27 16:07:01');
INSERT INTO `follows` VALUES (24, 7, 8, '2025-05-16 20:33:00');
INSERT INTO `follows` VALUES (26, 7, 3, '2025-05-16 20:37:18');
INSERT INTO `follows` VALUES (27, 7, 4, '2025-05-16 20:39:43');
INSERT INTO `follows` VALUES (28, 12, 7, '2025-05-19 16:05:31');
INSERT INTO `follows` VALUES (31, 12, 4, '2025-05-19 16:08:22');
INSERT INTO `follows` VALUES (32, 12, 3, '2025-05-19 16:08:27');

-- ----------------------------
-- Table structure for order_items
-- ----------------------------
DROP TABLE IF EXISTS `order_items`;
CREATE TABLE `order_items`  (
  `order_item_id` int NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
  `order_id` int NOT NULL COMMENT '订单ID',
  `product_id` int NOT NULL COMMENT '商品ID',
  `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品名称',
  `product_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品图片',
  `quantity` int NOT NULL COMMENT '购买数量',
  `price` decimal(10, 2) NOT NULL COMMENT '单价',
  `subtotal` decimal(10, 2) NOT NULL COMMENT '小计金额',
  `created_at` datetime(0) NOT NULL COMMENT '创建时间',
  `updated_at` datetime(0) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`order_item_id`) USING BTREE,
  INDEX `idx_order_id`(`order_id`) USING BTREE,
  INDEX `idx_product_id`(`product_id`) USING BTREE,
  CONSTRAINT `fk_order_items_order_id` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 67 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单商品表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of order_items
-- ----------------------------
INSERT INTO `order_items` VALUES (2, 2, 1, '猫粮', '/statics/images/upload/12ffdd31-ba9a-41e7-a417-0e3af7df7dfa.jpg', 10, 100.00, 1000.00, '2025-04-06 20:16:50', '2025-04-06 20:16:50');
INSERT INTO `order_items` VALUES (3, 3, 1, '猫粮', '/statics/images/upload/12ffdd31-ba9a-41e7-a417-0e3af7df7dfa.jpg', 10, 100.00, 1000.00, '2025-04-10 10:08:34', '2025-04-10 10:08:34');
INSERT INTO `order_items` VALUES (6, 6, 1, '猫粮', '/statics/images/upload/12ffdd31-ba9a-41e7-a417-0e3af7df7dfa.jpg', 10, 100.00, 1000.00, '2025-04-10 10:17:19', '2025-04-10 10:17:19');
INSERT INTO `order_items` VALUES (7, 7, 1, '猫粮', '/statics/images/upload/12ffdd31-ba9a-41e7-a417-0e3af7df7dfa.jpg', 10, 100.00, 1000.00, '2025-04-10 10:19:06', '2025-04-10 10:19:06');
INSERT INTO `order_items` VALUES (14, 14, 1, '猫粮', '/statics/images/upload/12ffdd31-ba9a-41e7-a417-0e3af7df7dfa.jpg', 10, 150.00, 1500.00, '2025-04-10 11:18:27', '2025-04-10 11:18:27');
INSERT INTO `order_items` VALUES (15, 15, 2, '猫咪玩具车自动逗猫棒自嗨解闷电动充电智能激光灯红外线宠物用品', '/images/product/f55c9269-586e-4f4d-a124-d6deca6c6b3c.jpg;/images/product/308df7f8-41eb-49d4-96fa-3485f2def905.jpg', 10, 38.00, 380.00, '2025-04-29 16:07:21', '2025-04-29 16:07:21');
INSERT INTO `order_items` VALUES (18, 18, 16, '星宴猫粮全价风干粮成猫幼猫小猫营养高蛋白蓝猫布偶猫专用国产', '/images/product/2b0f61bb-568b-4619-b58e-00c053b7ac55.jpg', 2, 158.00, 316.00, '2025-04-30 18:08:11', '2025-04-30 18:08:11');
INSERT INTO `order_items` VALUES (19, 19, 16, '星宴猫粮全价风干粮成猫幼猫小猫营养高蛋白蓝猫布偶猫专用国产', '/images/product/2b0f61bb-568b-4619-b58e-00c053b7ac55.jpg', 2, 158.00, 316.00, '2025-04-30 18:09:12', '2025-04-30 18:09:12');
INSERT INTO `order_items` VALUES (20, 20, 19, '全犬通用狗粮10斤装冻干小型犬农村土狗专用成犬幼犬中大型犬', '/images/product/93b71c2e-ade3-40ed-b1f9-d5073ca5f579.jpg', 2, 45.00, 90.00, '2025-04-30 18:09:52', '2025-04-30 18:09:52');
INSERT INTO `order_items` VALUES (21, 21, 19, '全犬通用狗粮10斤装冻干小型犬农村土狗专用成犬幼犬中大型犬', '/images/product/93b71c2e-ade3-40ed-b1f9-d5073ca5f579.jpg', 1, 45.00, 45.00, '2025-04-30 20:09:17', '2025-04-30 20:09:17');
INSERT INTO `order_items` VALUES (22, 22, 17, '狗粮40斤装成犬幼型犬专用金毛拉布拉多边牧通用全价', '/images/product/9d1d2d74-0a56-4532-9052-ded624225f94.jpg', 1, 115.00, 115.00, '2025-04-30 20:11:08', '2025-04-30 20:11:08');
INSERT INTO `order_items` VALUES (23, 23, 17, '狗粮40斤装成犬幼型犬专用金毛拉布拉多边牧通用全价', '/images/product/9d1d2d74-0a56-4532-9052-ded624225f94.jpg', 1, 115.00, 115.00, '2025-04-30 20:12:44', '2025-04-30 20:12:44');
INSERT INTO `order_items` VALUES (24, 24, 16, '星宴猫粮全价风干粮成猫幼猫小猫营养高蛋白蓝猫布偶猫专用国产', '/images/product/2b0f61bb-568b-4619-b58e-00c053b7ac55.jpg', 1, 158.00, 158.00, '2025-04-30 20:14:12', '2025-04-30 20:14:12');
INSERT INTO `order_items` VALUES (25, 25, 6, '幼犬专用狗粮 3kg装', '/images/product/0dc5a953-2703-44d5-b6e4-ff24a45867e5.jpg;/images/product/b2111937-8bce-412c-bf6f-0831d4cc683e.jpg;/images/product/364b92ab-370d-42d8-b1c7-9ac78640cc17.jpg', 1, 100.00, 100.00, '2025-04-30 20:15:27', '2025-04-30 20:15:27');
INSERT INTO `order_items` VALUES (29, 29, 6, '幼犬专用狗粮 3kg装', '/images/product/0dc5a953-2703-44d5-b6e4-ff24a45867e5.jpg;/images/product/b2111937-8bce-412c-bf6f-0831d4cc683e.jpg;/images/product/364b92ab-370d-42d8-b1c7-9ac78640cc17.jpg', 2, 100.00, 200.00, '2025-04-30 20:21:54', '2025-04-30 20:21:54');
INSERT INTO `order_items` VALUES (30, 30, 4, '新款帅气宠物狗狗猫咪美式条纹T恤春夏季薄款雪纳瑞泰迪小型犬~潮', '/images/product/e1c5b067-3b15-4f8f-af05-ab9ffd6f94e8.jpg;/images/product/cd598064-487c-43c3-947e-20d9c286e364.jpg', 3, 49.99, 149.97, '2025-04-30 20:23:42', '2025-04-30 20:23:42');
INSERT INTO `order_items` VALUES (31, 31, 22, '小佩自动喂食器SOLO猫咪定时自动投食机猫粮狗粮智能宠物喂食机', '/images/product/ff8af37d-ffee-4d64-9af8-6df06510b5ac.jpg;/images/product/5816a9a4-a3ea-4159-884e-99dfa25b9c4f.jpg', 3, 269.00, 807.00, '2025-04-30 20:25:24', '2025-04-30 20:25:24');
INSERT INTO `order_items` VALUES (32, 32, 15, '麦德氏小分子鲨鱼软骨素狗狗专用泰迪金毛宠物狗狗关节补钙软骨素', '/images/product/7f029bec-9283-4b9b-ba5b-01c33125abf3.jpg;/images/product/7632354d-23fb-41ee-9c86-41e69393dd8f.jpg;/images/product/59cf41f1-bc5c-4a91-a9cb-74dc41a65100.jpg;/images/product/6d0c3e51-5245-4082-a0cd-a8bf77428bb8.jpg', 5, 128.00, 640.00, '2025-04-30 20:31:37', '2025-04-30 20:31:37');
INSERT INTO `order_items` VALUES (33, 33, 20, '狗狗毛绒发声飞盘玩具自嗨解闷神器柯基消耗体力宠物耐咬拔河玩具', '/images/product/05520aba-7e43-4185-9b32-f46dfe72e859.jpg', 3, 78.80, 236.40, '2025-04-30 20:33:14', '2025-04-30 20:33:14');
INSERT INTO `order_items` VALUES (34, 34, 20, '狗狗毛绒发声飞盘玩具自嗨解闷神器柯基消耗体力宠物耐咬拔河玩具', '/images/product/05520aba-7e43-4185-9b32-f46dfe72e859.jpg', 4, 78.80, 315.20, '2025-04-30 20:40:47', '2025-04-30 20:40:47');
INSERT INTO `order_items` VALUES (35, 35, 16, '星宴猫粮全价风干粮成猫幼猫小猫营养高蛋白蓝猫布偶猫专用国产', '/images/product/2b0f61bb-568b-4619-b58e-00c053b7ac55.jpg', 2, 158.00, 316.00, '2025-04-30 20:49:00', '2025-04-30 20:49:00');
INSERT INTO `order_items` VALUES (36, 36, 20, '狗狗毛绒发声飞盘玩具自嗨解闷神器柯基消耗体力宠物耐咬拔河玩具', '/images/product/05520aba-7e43-4185-9b32-f46dfe72e859.jpg', 3, 78.80, 236.40, '2025-04-30 20:52:22', '2025-04-30 20:52:22');
INSERT INTO `order_items` VALUES (37, 37, 15, '麦德氏小分子鲨鱼软骨素狗狗专用泰迪金毛宠物狗狗关节补钙软骨素', '/images/product/7f029bec-9283-4b9b-ba5b-01c33125abf3.jpg;/images/product/7632354d-23fb-41ee-9c86-41e69393dd8f.jpg;/images/product/59cf41f1-bc5c-4a91-a9cb-74dc41a65100.jpg;/images/product/6d0c3e51-5245-4082-a0cd-a8bf77428bb8.jpg', 3, 128.00, 384.00, '2025-04-30 20:52:45', '2025-04-30 20:52:45');
INSERT INTO `order_items` VALUES (38, 38, 20, '狗狗毛绒发声飞盘玩具自嗨解闷神器柯基消耗体力宠物耐咬拔河玩具', '/images/product/05520aba-7e43-4185-9b32-f46dfe72e859.jpg', 2, 78.80, 157.60, '2025-04-30 20:54:37', '2025-04-30 20:54:37');
INSERT INTO `order_items` VALUES (39, 39, 19, '全犬通用狗粮10斤装冻干小型犬农村土狗专用成犬幼犬中大型犬', '/images/product/93b71c2e-ade3-40ed-b1f9-d5073ca5f579.jpg', 2, 45.00, 90.00, '2025-04-30 20:59:36', '2025-04-30 20:59:36');
INSERT INTO `order_items` VALUES (40, 40, 2, '猫咪玩具车自动逗猫棒自嗨解闷电动充电智能激光灯红外线宠物用品', '/images/product/f55c9269-586e-4f4d-a124-d6deca6c6b3c.jpg;/images/product/308df7f8-41eb-49d4-96fa-3485f2def905.jpg', 3, 38.00, 114.00, '2025-04-30 21:12:19', '2025-04-30 21:12:19');
INSERT INTO `order_items` VALUES (41, 41, 17, '狗粮40斤装成犬幼型犬专用金毛拉布拉多边牧通用全价', '/images/product/9d1d2d74-0a56-4532-9052-ded624225f94.jpg', 1, 115.00, 115.00, '2025-04-30 21:16:07', '2025-04-30 21:16:07');
INSERT INTO `order_items` VALUES (42, 42, 6, '幼犬专用狗粮 3kg装', '/images/product/0dc5a953-2703-44d5-b6e4-ff24a45867e5.jpg;/images/product/b2111937-8bce-412c-bf6f-0831d4cc683e.jpg;/images/product/364b92ab-370d-42d8-b1c7-9ac78640cc17.jpg', 1, 100.00, 100.00, '2025-04-30 21:19:03', '2025-04-30 21:19:03');
INSERT INTO `order_items` VALUES (43, 43, 4, '新款帅气宠物狗狗猫咪美式条纹T恤春夏季薄款雪纳瑞泰迪小型犬~潮', '/images/product/e1c5b067-3b15-4f8f-af05-ab9ffd6f94e8.jpg;/images/product/cd598064-487c-43c3-947e-20d9c286e364.jpg', 2, 49.99, 99.98, '2025-04-30 21:31:04', '2025-04-30 21:31:04');
INSERT INTO `order_items` VALUES (44, 44, 20, '狗狗毛绒发声飞盘玩具自嗨解闷神器柯基消耗体力宠物耐咬拔河玩具', '/images/product/05520aba-7e43-4185-9b32-f46dfe72e859.jpg', 1, 78.80, 78.80, '2025-04-30 21:32:51', '2025-04-30 21:32:51');
INSERT INTO `order_items` VALUES (45, 45, 20, '狗狗毛绒发声飞盘玩具自嗨解闷神器柯基消耗体力宠物耐咬拔河玩具', '/images/product/05520aba-7e43-4185-9b32-f46dfe72e859.jpg', 1, 78.80, 78.80, '2025-04-30 21:33:08', '2025-04-30 21:33:08');
INSERT INTO `order_items` VALUES (46, 46, 17, '狗粮40斤装成犬幼型犬专用金毛拉布拉多边牧通用全价', '/images/product/9d1d2d74-0a56-4532-9052-ded624225f94.jpg', 3, 115.00, 345.00, '2025-04-30 22:03:15', '2025-04-30 22:03:15');
INSERT INTO `order_items` VALUES (47, 47, 15, '麦德氏小分子鲨鱼软骨素狗狗专用泰迪金毛宠物狗狗关节补钙软骨素', '/images/product/7f029bec-9283-4b9b-ba5b-01c33125abf3.jpg;/images/product/7632354d-23fb-41ee-9c86-41e69393dd8f.jpg;/images/product/59cf41f1-bc5c-4a91-a9cb-74dc41a65100.jpg;/images/product/6d0c3e51-5245-4082-a0cd-a8bf77428bb8.jpg', 2, 128.00, 256.00, '2025-04-30 22:09:37', '2025-04-30 22:09:37');
INSERT INTO `order_items` VALUES (48, 48, 18, '麦富迪狗粮barf霸弗生骨肉主食冻干粮泰迪比熊柯基通用成犬天然粮', '/images/product/97371594-a3a4-494d-b146-a19b55a979fd.jpg', 2, 109.00, 218.00, '2025-04-30 22:10:26', '2025-04-30 22:10:26');
INSERT INTO `order_items` VALUES (49, 49, 22, '小佩自动喂食器SOLO猫咪定时自动投食机猫粮狗粮智能宠物喂食机', '/images/product/ff8af37d-ffee-4d64-9af8-6df06510b5ac.jpg;/images/product/5816a9a4-a3ea-4159-884e-99dfa25b9c4f.jpg', 2, 269.00, 538.00, '2025-04-30 22:13:11', '2025-04-30 22:13:11');
INSERT INTO `order_items` VALUES (50, 50, 22, '小佩自动喂食器SOLO猫咪定时自动投食机猫粮狗粮智能宠物喂食机', '/images/product/ff8af37d-ffee-4d64-9af8-6df06510b5ac.jpg;/images/product/5816a9a4-a3ea-4159-884e-99dfa25b9c4f.jpg', 2, 269.00, 538.00, '2025-04-30 22:14:21', '2025-04-30 22:14:21');
INSERT INTO `order_items` VALUES (51, 51, 21, '伸缩逗猫棒钢丝羽毛1.8m超长钓鱼竿耐咬带铃铛猫玩具解闷宠物用品', '/images/product/0633731b-3ffe-4103-947e-7edffead26fd.jpg;/images/product/d704eed5-e9d2-49a7-b114-9dedeba693a1.jpg', 1, 87.90, 87.90, '2025-04-30 22:17:31', '2025-04-30 22:17:31');
INSERT INTO `order_items` VALUES (52, 52, 22, '小佩自动喂食器SOLO猫咪定时自动投食机猫粮狗粮智能宠物喂食机', '/images/product/ff8af37d-ffee-4d64-9af8-6df06510b5ac.jpg;/images/product/5816a9a4-a3ea-4159-884e-99dfa25b9c4f.jpg', 2, 269.00, 538.00, '2025-04-30 22:19:00', '2025-04-30 22:19:00');
INSERT INTO `order_items` VALUES (53, 53, 17, '狗粮40斤装成犬幼型犬专用金毛拉布拉多边牧通用全价', '/images/product/9d1d2d74-0a56-4532-9052-ded624225f94.jpg', 5, 115.00, 575.00, '2025-04-30 22:33:35', '2025-04-30 22:33:35');
INSERT INTO `order_items` VALUES (54, 54, 19, '全犬通用狗粮10斤装冻干小型犬农村土狗专用成犬幼犬中大型犬', '/images/product/93b71c2e-ade3-40ed-b1f9-d5073ca5f579.jpg', 2, 45.00, 90.00, '2025-04-30 22:44:43', '2025-04-30 22:44:43');
INSERT INTO `order_items` VALUES (55, 55, 4, '新款帅气宠物狗狗猫咪美式条纹T恤春夏季薄款雪纳瑞泰迪小型犬~潮', '/images/product/e1c5b067-3b15-4f8f-af05-ab9ffd6f94e8.jpg;/images/product/cd598064-487c-43c3-947e-20d9c286e364.jpg', 3, 49.99, 149.97, '2025-05-07 11:07:14', '2025-05-07 11:07:14');
INSERT INTO `order_items` VALUES (56, 56, 19, '全犬通用狗粮10斤装冻干小型犬农村土狗专用成犬幼犬中大型犬', '/images/product/93b71c2e-ade3-40ed-b1f9-d5073ca5f579.jpg', 1, 45.00, 45.00, '2025-05-07 13:15:16', '2025-05-07 13:15:16');
INSERT INTO `order_items` VALUES (57, 57, 19, '全犬通用狗粮10斤装冻干小型犬农村土狗专用成犬幼犬中大型犬', '/images/product/93b71c2e-ade3-40ed-b1f9-d5073ca5f579.jpg', 2, 45.00, 90.00, '2025-05-07 13:15:57', '2025-05-07 13:15:57');
INSERT INTO `order_items` VALUES (58, 58, 4, '新款帅气宠物狗狗猫咪美式条纹T恤春夏季薄款雪纳瑞泰迪小型犬~潮', '/images/product/e1c5b067-3b15-4f8f-af05-ab9ffd6f94e8.jpg;/images/product/cd598064-487c-43c3-947e-20d9c286e364.jpg', 1, 49.99, 49.99, '2025-05-07 13:24:38', '2025-05-07 13:24:38');
INSERT INTO `order_items` VALUES (59, 59, 4, '新款帅气宠物狗狗猫咪美式条纹T恤春夏季薄款雪纳瑞泰迪小型犬~潮', '/images/product/e1c5b067-3b15-4f8f-af05-ab9ffd6f94e8.jpg;/images/product/cd598064-487c-43c3-947e-20d9c286e364.jpg', 1, 49.99, 49.99, '2025-05-08 13:33:56', '2025-05-08 13:33:56');
INSERT INTO `order_items` VALUES (60, 60, 4, '新款帅气宠物狗狗猫咪美式条纹T恤春夏季薄款雪纳瑞泰迪小型犬~潮', '/images/product/e1c5b067-3b15-4f8f-af05-ab9ffd6f94e8.jpg;/images/product/cd598064-487c-43c3-947e-20d9c286e364.jpg', 1, 49.99, 49.99, '2025-05-08 14:24:49', '2025-05-08 14:24:49');
INSERT INTO `order_items` VALUES (61, 61, 20, '狗狗毛绒发声飞盘玩具自嗨解闷神器柯基消耗体力宠物耐咬拔河玩具', '/images/product/05520aba-7e43-4185-9b32-f46dfe72e859.jpg', 3, 78.80, 236.40, '2025-05-09 11:32:05', '2025-05-09 11:32:05');
INSERT INTO `order_items` VALUES (62, 62, 6, '幼犬专用狗粮 3kg装', '/images/product/0dc5a953-2703-44d5-b6e4-ff24a45867e5.jpg;/images/product/b2111937-8bce-412c-bf6f-0831d4cc683e.jpg;/images/product/364b92ab-370d-42d8-b1c7-9ac78640cc17.jpg', 2, 100.00, 200.00, '2025-05-12 10:44:55', '2025-05-12 10:44:55');
INSERT INTO `order_items` VALUES (63, 63, 15, '麦德氏小分子鲨鱼软骨素狗狗专用泰迪金毛宠物狗狗关节补钙软骨素', '/images/product/7f029bec-9283-4b9b-ba5b-01c33125abf3.jpg;/images/product/7632354d-23fb-41ee-9c86-41e69393dd8f.jpg;/images/product/59cf41f1-bc5c-4a91-a9cb-74dc41a65100.jpg;/images/product/6d0c3e51-5245-4082-a0cd-a8bf77428bb8.jpg', 3, 128.00, 384.00, '2025-05-12 15:24:00', '2025-05-12 15:24:00');
INSERT INTO `order_items` VALUES (65, 65, 16, '星宴猫粮全价风干粮成猫幼猫小猫营养高蛋白蓝猫布偶猫专用国产', '/images/product/2b0f61bb-568b-4619-b58e-00c053b7ac55.jpg', 2, 158.00, 316.00, '2025-05-15 09:11:29', '2025-05-15 09:11:29');

-- ----------------------------
-- Table structure for order_payment
-- ----------------------------
DROP TABLE IF EXISTS `order_payment`;
CREATE TABLE `order_payment`  (
  `payment_id` int NOT NULL AUTO_INCREMENT COMMENT '支付ID',
  `order_id` int NOT NULL COMMENT '订单ID',
  `payment_method` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '支付方式',
  `payment_amount` decimal(10, 2) NOT NULL COMMENT '支付金额',
  `payment_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '支付状态',
  `transaction_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '交易号',
  `payment_time` datetime(0) NULL DEFAULT NULL COMMENT '支付时间',
  `created_at` datetime(0) NOT NULL COMMENT '创建时间',
  `updated_at` datetime(0) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`payment_id`) USING BTREE,
  UNIQUE INDEX `idx_order_id`(`order_id`) USING BTREE,
  INDEX `idx_payment_status`(`payment_status`) USING BTREE,
  INDEX `idx_payment_time`(`payment_time`) USING BTREE,
  CONSTRAINT `fk_order_payment_order_id` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单支付表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of order_payment
-- ----------------------------
INSERT INTO `order_payment` VALUES (3, 2, 'wx', 1000.00, 'success', NULL, '2025-04-10 10:59:28', '2025-04-10 10:59:28', '2025-04-10 10:59:28');
INSERT INTO `order_payment` VALUES (4, 14, 'wx', 1500.00, 'success', NULL, '2025-04-10 11:19:12', '2025-04-10 11:19:12', '2025-04-10 11:19:12');
INSERT INTO `order_payment` VALUES (5, 35, 'card', 316.00, 'success', NULL, '2025-04-30 20:49:41', '2025-04-30 20:49:41', '2025-04-30 20:49:41');
INSERT INTO `order_payment` VALUES (6, 36, 'wx', 236.40, 'success', NULL, '2025-04-30 20:52:27', '2025-04-30 20:52:27', '2025-04-30 20:52:27');
INSERT INTO `order_payment` VALUES (7, 37, 'zfb', 384.00, 'success', NULL, '2025-04-30 20:52:52', '2025-04-30 20:52:52', '2025-04-30 20:52:52');
INSERT INTO `order_payment` VALUES (8, 38, 'zfb', 157.60, 'success', NULL, '2025-04-30 20:54:42', '2025-04-30 20:54:42', '2025-04-30 20:54:42');
INSERT INTO `order_payment` VALUES (9, 39, 'zfb', 90.00, 'success', NULL, '2025-04-30 20:59:41', '2025-04-30 20:59:41', '2025-04-30 20:59:41');
INSERT INTO `order_payment` VALUES (10, 40, 'card', 114.00, 'success', NULL, '2025-04-30 21:12:47', '2025-04-30 21:12:47', '2025-04-30 21:12:47');
INSERT INTO `order_payment` VALUES (11, 41, 'wx', 115.00, 'success', NULL, '2025-04-30 21:16:41', '2025-04-30 21:16:41', '2025-04-30 21:16:41');
INSERT INTO `order_payment` VALUES (12, 44, 'wx', 78.80, 'success', NULL, '2025-04-30 21:32:59', '2025-04-30 21:32:59', '2025-04-30 21:32:59');
INSERT INTO `order_payment` VALUES (13, 46, 'zfb', 345.00, 'success', NULL, '2025-04-30 22:04:11', '2025-04-30 22:04:11', '2025-04-30 22:04:11');
INSERT INTO `order_payment` VALUES (14, 50, 'wx', 538.00, 'success', NULL, '2025-04-30 22:14:33', '2025-04-30 22:14:33', '2025-04-30 22:14:33');
INSERT INTO `order_payment` VALUES (15, 51, 'wx', 87.90, 'success', NULL, '2025-04-30 22:18:01', '2025-04-30 22:18:01', '2025-04-30 22:18:01');
INSERT INTO `order_payment` VALUES (16, 52, 'wx', 538.00, 'success', NULL, '2025-04-30 22:19:13', '2025-04-30 22:19:13', '2025-04-30 22:19:13');
INSERT INTO `order_payment` VALUES (17, 53, 'wx', 575.00, 'success', NULL, '2025-04-30 22:34:01', '2025-04-30 22:34:01', '2025-04-30 22:34:01');
INSERT INTO `order_payment` VALUES (18, 54, 'wx', 90.00, 'success', NULL, '2025-04-30 22:44:51', '2025-04-30 22:44:51', '2025-04-30 22:44:51');
INSERT INTO `order_payment` VALUES (19, 55, 'wx', 149.97, 'success', NULL, '2025-05-07 11:07:37', '2025-05-07 11:07:37', '2025-05-07 11:07:37');
INSERT INTO `order_payment` VALUES (20, 56, 'wx', 45.00, 'success', NULL, '2025-05-07 13:15:27', '2025-05-07 13:15:27', '2025-05-07 13:15:27');
INSERT INTO `order_payment` VALUES (21, 57, 'zfb', 90.00, 'success', NULL, '2025-05-07 13:16:03', '2025-05-07 13:16:03', '2025-05-07 13:16:03');
INSERT INTO `order_payment` VALUES (22, 58, 'card', 49.99, 'success', NULL, '2025-05-07 13:24:47', '2025-05-07 13:24:47', '2025-05-07 13:24:47');
INSERT INTO `order_payment` VALUES (23, 59, 'wx', 49.99, 'success', NULL, '2025-05-08 13:34:09', '2025-05-08 13:34:09', '2025-05-08 13:34:09');
INSERT INTO `order_payment` VALUES (24, 61, 'wx', 236.40, 'success', NULL, '2025-05-09 11:32:31', '2025-05-09 11:32:31', '2025-05-09 11:32:31');
INSERT INTO `order_payment` VALUES (25, 62, 'zfb', 200.00, 'success', NULL, '2025-05-12 10:45:07', '2025-05-12 10:45:07', '2025-05-12 10:45:07');
INSERT INTO `order_payment` VALUES (28, 63, 'wx', 384.00, 'success', NULL, '2025-05-13 11:26:33', '2025-05-13 11:26:33', '2025-05-13 11:26:33');

-- ----------------------------
-- Table structure for order_shipping
-- ----------------------------
DROP TABLE IF EXISTS `order_shipping`;
CREATE TABLE `order_shipping`  (
  `shipping_id` int NOT NULL AUTO_INCREMENT COMMENT '物流ID',
  `order_id` int NOT NULL COMMENT '订单ID',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '收货地址',
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '收货人姓名',
  `receiver_mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '收货人电话',
  `shipping_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '物流状态',
  `tracking_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '运单号',
  `shipping_time` datetime(0) NULL DEFAULT NULL COMMENT '发货时间',
  `completion_time` datetime(0) NULL DEFAULT NULL COMMENT '收货时间',
  `shipping_company` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '物流公司',
  `created_at` datetime(0) NOT NULL COMMENT '创建时间',
  `updated_at` datetime(0) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`shipping_id`) USING BTREE,
  UNIQUE INDEX `idx_order_id`(`order_id`) USING BTREE,
  INDEX `idx_shipping_status`(`shipping_status`) USING BTREE,
  INDEX `idx_tracking_number`(`tracking_number`) USING BTREE,
  CONSTRAINT `fk_order_shipping_order_id` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 67 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单物流表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of order_shipping
-- ----------------------------
INSERT INTO `order_shipping` VALUES (2, 2, '四川成都金堂县白果街道', '李四', '18957390912', 'pending', '', NULL, NULL, '', '2025-04-06 20:16:50', '2025-04-06 20:16:50');
INSERT INTO `order_shipping` VALUES (3, 3, '四川成都金堂县白果街道', '李四', '18957390912', 'pending', '', NULL, NULL, '', '2025-04-10 10:08:34', '2025-04-10 10:08:34');
INSERT INTO `order_shipping` VALUES (6, 6, '四川成都金堂县白果街道', '李四', '18957390912', 'pending', '', NULL, NULL, '', '2025-04-10 10:17:19', '2025-04-10 10:17:19');
INSERT INTO `order_shipping` VALUES (7, 7, '四川成都金堂县白果街道', '李四', '18957390912', 'pending', '', NULL, NULL, '', '2025-04-10 10:19:06', '2025-04-10 10:19:06');
INSERT INTO `order_shipping` VALUES (14, 14, '四川成都金堂县白果街道', '李四', '18957390912', 'pending', '', NULL, NULL, '', '2025-04-10 11:18:27', '2025-04-10 11:18:27');
INSERT INTO `order_shipping` VALUES (15, 15, '四川成都金堂县白果街道', '李四', '18957390912', 'pending', NULL, NULL, NULL, NULL, '2025-04-29 16:07:21', '2025-04-29 16:07:21');
INSERT INTO `order_shipping` VALUES (18, 18, '四川省成都市金堂县', '测试用户', '18890908888', 'pending', NULL, NULL, NULL, NULL, '2025-04-30 18:08:11', '2025-04-30 18:08:11');
INSERT INTO `order_shipping` VALUES (19, 19, '四川省成都市金堂县', '测试用户', '18890908888', 'pending', NULL, NULL, NULL, NULL, '2025-04-30 18:09:12', '2025-04-30 18:09:12');
INSERT INTO `order_shipping` VALUES (20, 20, '四川省成都市金堂县', '测试用户', '18890908888', 'pending', NULL, NULL, NULL, NULL, '2025-04-30 18:09:52', '2025-04-30 18:09:52');
INSERT INTO `order_shipping` VALUES (21, 21, '四川省成都市金堂县', '测试用户', '18890908888', 'pending', NULL, NULL, NULL, NULL, '2025-04-30 20:09:17', '2025-04-30 20:09:17');
INSERT INTO `order_shipping` VALUES (22, 22, '四川省成都市金堂县', '测试用户', '18890908888', 'pending', NULL, NULL, NULL, NULL, '2025-04-30 20:11:08', '2025-04-30 20:11:08');
INSERT INTO `order_shipping` VALUES (23, 23, '四川省成都市金堂县', '测试用户', '18890908888', 'pending', NULL, NULL, NULL, NULL, '2025-04-30 20:12:44', '2025-04-30 20:12:44');
INSERT INTO `order_shipping` VALUES (24, 24, '四川省成都市金堂县', '测试用户', '18890908888', 'pending', NULL, NULL, NULL, NULL, '2025-04-30 20:14:12', '2025-04-30 20:14:12');
INSERT INTO `order_shipping` VALUES (25, 25, '四川省成都市金堂县', '测试用户', '18890908888', 'pending', NULL, NULL, NULL, NULL, '2025-04-30 20:15:27', '2025-04-30 20:15:27');
INSERT INTO `order_shipping` VALUES (29, 29, '四川省成都市金堂县', '测试用户', '18890908888', 'pending', NULL, NULL, NULL, NULL, '2025-04-30 20:21:54', '2025-04-30 20:21:54');
INSERT INTO `order_shipping` VALUES (30, 30, '四川省成都市金堂县', '测试用户', '18890908888', 'pending', NULL, NULL, NULL, NULL, '2025-04-30 20:23:42', '2025-04-30 20:23:42');
INSERT INTO `order_shipping` VALUES (31, 31, '四川省成都市金堂县', '测试用户', '18890908888', 'pending', NULL, NULL, NULL, NULL, '2025-04-30 20:25:24', '2025-04-30 20:25:24');
INSERT INTO `order_shipping` VALUES (32, 32, '四川省成都市金堂县', '测试用户', '18890908888', 'pending', NULL, NULL, NULL, NULL, '2025-04-30 20:31:37', '2025-04-30 20:31:37');
INSERT INTO `order_shipping` VALUES (33, 33, '四川省成都市金堂县', '测试用户', '18890908888', 'pending', NULL, NULL, NULL, NULL, '2025-04-30 20:33:14', '2025-04-30 20:33:14');
INSERT INTO `order_shipping` VALUES (34, 34, '四川省成都市金堂县', '测试用户', '18890908888', 'pending', NULL, NULL, NULL, NULL, '2025-04-30 20:40:47', '2025-04-30 20:40:47');
INSERT INTO `order_shipping` VALUES (35, 35, '四川省成都市金堂县', '测试用户', '18890908888', 'pending', NULL, NULL, NULL, NULL, '2025-04-30 20:49:00', '2025-04-30 20:49:00');
INSERT INTO `order_shipping` VALUES (36, 36, '四川省成都市金堂县', '测试用户', '18890908888', 'pending', NULL, NULL, NULL, NULL, '2025-04-30 20:52:22', '2025-04-30 20:52:22');
INSERT INTO `order_shipping` VALUES (37, 37, '四川省成都市金堂县', '测试用户', '18890908888', 'pending', NULL, NULL, NULL, NULL, '2025-04-30 20:52:45', '2025-04-30 20:52:45');
INSERT INTO `order_shipping` VALUES (38, 38, '四川省成都市金堂县', '测试用户', '18890908888', 'pending', NULL, NULL, NULL, NULL, '2025-04-30 20:54:37', '2025-04-30 20:54:37');
INSERT INTO `order_shipping` VALUES (39, 39, '四川省成都市金堂县', '测试用户', '18890908888', 'pending', NULL, NULL, NULL, NULL, '2025-04-30 20:59:36', '2025-04-30 20:59:36');
INSERT INTO `order_shipping` VALUES (40, 40, '四川省成都市金堂县', '测试用户', '18890908888', 'pending', NULL, NULL, NULL, NULL, '2025-04-30 21:12:19', '2025-04-30 21:12:19');
INSERT INTO `order_shipping` VALUES (41, 41, '四川省成都市金堂县', '测试用户', '18890908888', 'pending', NULL, NULL, NULL, NULL, '2025-04-30 21:16:07', '2025-04-30 21:16:07');
INSERT INTO `order_shipping` VALUES (42, 42, '四川省成都市金堂县', '测试用户', '18890908888', 'pending', NULL, NULL, NULL, NULL, '2025-04-30 21:19:03', '2025-04-30 21:19:03');
INSERT INTO `order_shipping` VALUES (43, 43, '四川省成都市金堂县', '测试用户', '18890908888', 'pending', NULL, NULL, NULL, NULL, '2025-04-30 21:31:04', '2025-04-30 21:31:04');
INSERT INTO `order_shipping` VALUES (44, 44, '四川省成都市金堂县', '测试用户', '18890908888', 'pending', NULL, NULL, NULL, NULL, '2025-04-30 21:32:51', '2025-04-30 21:32:51');
INSERT INTO `order_shipping` VALUES (45, 45, '四川省成都市金堂县', '测试用户', '18890908888', 'pending', NULL, NULL, NULL, NULL, '2025-04-30 21:33:08', '2025-04-30 21:33:08');
INSERT INTO `order_shipping` VALUES (46, 46, '四川省成都市金堂县', '测试用户', '18890908888', 'pending', NULL, NULL, NULL, NULL, '2025-04-30 22:03:15', '2025-04-30 22:03:15');
INSERT INTO `order_shipping` VALUES (47, 47, '四川省成都市金堂县', '测试用户', '18890908888', 'pending', NULL, NULL, NULL, NULL, '2025-04-30 22:09:37', '2025-04-30 22:09:37');
INSERT INTO `order_shipping` VALUES (48, 48, '四川省成都市金堂县', '测试用户', '18890908888', 'pending', NULL, NULL, NULL, NULL, '2025-04-30 22:10:26', '2025-04-30 22:10:26');
INSERT INTO `order_shipping` VALUES (49, 49, '四川省成都市金堂县', '测试用户', '18890908888', 'pending', NULL, NULL, NULL, NULL, '2025-04-30 22:13:11', '2025-04-30 22:13:11');
INSERT INTO `order_shipping` VALUES (50, 50, '四川省成都市金堂县', '测试用户', '18890908888', 'pending', NULL, NULL, NULL, NULL, '2025-04-30 22:14:21', '2025-04-30 22:14:21');
INSERT INTO `order_shipping` VALUES (51, 51, '四川省成都市金堂县', '测试用户', '18890908888', 'pending', NULL, NULL, NULL, NULL, '2025-04-30 22:17:31', '2025-04-30 22:17:31');
INSERT INTO `order_shipping` VALUES (52, 52, '四川省成都市金堂县', '测试用户', '18890908888', 'pending', NULL, NULL, NULL, NULL, '2025-04-30 22:19:00', '2025-04-30 22:19:00');
INSERT INTO `order_shipping` VALUES (53, 53, '四川省成都市金堂县', '测试用户', '18890908888', 'delivered', 'SF999999999', '2025-05-06 15:27:07', '2025-05-15 09:06:11', 'SF', '2025-04-30 22:33:35', '2025-05-15 09:06:11');
INSERT INTO `order_shipping` VALUES (54, 54, '四川省成都市金堂县', '测试用户', '18890908888', 'delivered', 'ST101010999333', '2025-05-03 19:18:05', '2025-05-12 18:05:26', 'STO', '2025-04-30 22:44:43', '2025-05-12 18:05:25');
INSERT INTO `order_shipping` VALUES (55, 55, '四川省成都市金堂县', '测试用户', '18890908888', 'pending', NULL, NULL, NULL, NULL, '2025-05-07 11:07:14', '2025-05-07 11:07:14');
INSERT INTO `order_shipping` VALUES (56, 56, '四川省成都市金堂县 白果街道', '张三', '18958648888', 'pending', NULL, NULL, NULL, NULL, '2025-05-07 13:15:16', '2025-05-07 13:15:16');
INSERT INTO `order_shipping` VALUES (57, 57, '四川省成都市双流区 九江街道', '李四', '18966666666', 'pending', NULL, NULL, NULL, NULL, '2025-05-07 13:15:57', '2025-05-07 13:15:57');
INSERT INTO `order_shipping` VALUES (58, 58, '北京市北京市石景山区 好', '王五', '18966666666', 'pending', NULL, NULL, NULL, NULL, '2025-05-07 13:24:38', '2025-05-07 13:24:38');
INSERT INTO `order_shipping` VALUES (59, 59, '四川省成都市金堂县 白果街道', '张三', '18958648888', 'pending', NULL, NULL, NULL, NULL, '2025-05-08 13:33:56', '2025-05-08 13:33:56');
INSERT INTO `order_shipping` VALUES (60, 60, '四川省成都市金堂县 白果街道', '张三', '18958648888', 'pending', NULL, NULL, NULL, NULL, '2025-05-08 14:24:49', '2025-05-08 14:24:49');
INSERT INTO `order_shipping` VALUES (61, 61, '四川省成都市金堂县 白果街道', '张三', '18958648888', 'pending', NULL, NULL, NULL, NULL, '2025-05-09 11:32:05', '2025-05-09 11:32:05');
INSERT INTO `order_shipping` VALUES (62, 62, '北京市北京市石景山区 xx景区', '王五', '18966666666', 'pending', NULL, NULL, NULL, NULL, '2025-05-12 10:44:55', '2025-05-12 10:44:55');
INSERT INTO `order_shipping` VALUES (63, 63, '四川省成都市金堂县 白果街道', '张三', '18958648888', 'shipped', 'SF777777777', '2025-05-16 10:02:44', NULL, 'SF', '2025-05-12 15:24:00', '2025-05-16 10:02:44');
INSERT INTO `order_shipping` VALUES (65, 65, '四川省成都市金堂县 白果街道', '张三', '18958648888', 'pending', NULL, NULL, NULL, NULL, '2025-05-15 09:11:29', '2025-05-15 09:11:29');

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `order_id` int NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单号',
  `total_amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '订单总金额',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单状态：pending,paid,shipped,completed,cancelled',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `created_at` datetime(0) NOT NULL COMMENT '创建时间',
  `updated_at` datetime(0) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`order_id`) USING BTREE,
  UNIQUE INDEX `idx_order_no`(`order_no`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_created_at`(`created_at`) USING BTREE,
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 67 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单主表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (2, 4, 'ORD202504067928b9', 1000.00, 'paid', '10份猫粮', '2025-04-06 20:16:50', '2025-04-10 10:59:28');
INSERT INTO `orders` VALUES (3, 4, 'ORD20250410034c62', 1000.00, 'cancelled', '10份猫粮', '2025-04-10 10:08:34', '2025-04-10 10:09:34');
INSERT INTO `orders` VALUES (6, 4, 'ORD20250410cc682f', 1000.00, 'cancelled', '10份猫粮', '2025-04-10 10:17:19', '2025-04-10 10:18:19');
INSERT INTO `orders` VALUES (7, 4, 'ORD20250410f92650', 1000.00, 'cancelled', '10份猫粮', '2025-04-10 10:19:06', '2025-04-10 10:20:06');
INSERT INTO `orders` VALUES (14, 4, 'ORD2025041041303b', 1500.00, 'paid', '10份猫粮', '2025-04-10 11:18:27', '2025-04-10 11:19:12');
INSERT INTO `orders` VALUES (15, 4, 'ORD2025042940cc3c', 380.00, 'cancelled', '哈哈哈哈', '2025-04-29 16:07:21', '2025-04-29 16:08:21');
INSERT INTO `orders` VALUES (18, 7, 'ORD2025043049cd34', 316.00, 'cancelled', '2.5L 白色 user:用户备注', '2025-04-30 18:08:11', '2025-04-30 18:09:11');
INSERT INTO `orders` VALUES (19, 7, 'ORD202504301f836b', 316.00, 'cancelled', '4L 蓝色 user:用户备注', '2025-04-30 18:09:12', '2025-04-30 18:10:12');
INSERT INTO `orders` VALUES (20, 7, 'ORD20250430185fdc', 90.00, 'cancelled', '2.5L 白色 user:备注信息', '2025-04-30 18:09:52', '2025-04-30 18:10:52');
INSERT INTO `orders` VALUES (21, 7, 'ORD20250430cd9052', 45.00, 'cancelled', '2.5L 白色 user:备注信息', '2025-04-30 20:09:17', '2025-04-30 20:10:17');
INSERT INTO `orders` VALUES (22, 7, 'ORD202504305d7afd', 115.00, 'cancelled', '2.5L 白色 user:备注信息', '2025-04-30 20:11:08', '2025-04-30 20:12:08');
INSERT INTO `orders` VALUES (23, 7, 'ORD20250430ba2ccc', 115.00, 'cancelled', '2.5L 白色 user:备注信息', '2025-04-30 20:12:44', '2025-04-30 20:13:43');
INSERT INTO `orders` VALUES (24, 7, 'ORD202504303239de', 158.00, 'cancelled', '2.5L 白色 user:备注信息', '2025-04-30 20:14:12', '2025-04-30 20:15:12');
INSERT INTO `orders` VALUES (25, 7, 'ORD20250430e5e530', 100.00, 'cancelled', '2.5L 白色 user:备注信息', '2025-04-30 20:15:27', '2025-04-30 20:15:27');
INSERT INTO `orders` VALUES (29, 7, 'ORD20250430c74a66', 200.00, 'cancelled', '2.5L 白色 user:备注信息', '2025-04-30 20:21:54', '2025-04-30 20:22:53');
INSERT INTO `orders` VALUES (30, 7, 'ORD202504307ec1c4', 149.97, 'cancelled', '2.5L 白色 user:备注信息', '2025-04-30 20:23:42', '2025-04-30 20:24:42');
INSERT INTO `orders` VALUES (31, 7, 'ORD202504300ab4ad', 807.00, 'pending', '2.5L 白色 user:备注信息', '2025-04-30 20:25:24', '2025-04-30 20:25:24');
INSERT INTO `orders` VALUES (32, 7, 'ORD20250430af4344', 640.00, 'cancelled', '2.5L 白色 user:备注信息', '2025-04-30 20:31:37', '2025-04-30 20:32:37');
INSERT INTO `orders` VALUES (33, 7, 'ORD2025043092bcaa', 236.40, 'cancelled', '2.5L 白色 user:备注信息', '2025-04-30 20:33:14', '2025-04-30 20:40:38');
INSERT INTO `orders` VALUES (34, 7, 'ORD20250430c45d3b', 315.20, 'cancelled', '2.5L 白色 user:备注信息', '2025-04-30 20:40:47', '2025-04-30 20:41:46');
INSERT INTO `orders` VALUES (35, 7, 'ORD202504300f9f95', 316.00, 'paid', '2.5L 白色 user:备注信息', '2025-04-30 20:49:00', '2025-04-30 20:49:41');
INSERT INTO `orders` VALUES (36, 7, 'ORD20250430aa0fa4', 236.40, 'paid', '2.5L 粉色 user:备注信息', '2025-04-30 20:52:22', '2025-04-30 20:52:27');
INSERT INTO `orders` VALUES (37, 7, 'ORD20250430a97a9a', 384.00, 'paid', '2.5L 白色 user:备注信息', '2025-04-30 20:52:45', '2025-04-30 20:52:52');
INSERT INTO `orders` VALUES (38, 7, 'ORD20250430fac8ef', 157.60, 'paid', '2.5L 白色 user:备注信息', '2025-04-30 20:54:37', '2025-04-30 20:54:42');
INSERT INTO `orders` VALUES (39, 7, 'ORD20250430a48b47', 90.00, 'paid', '2.5L 白色 user:备注信息', '2025-04-30 20:59:36', '2025-04-30 20:59:41');
INSERT INTO `orders` VALUES (40, 7, 'ORD20250430b6cb9a', 114.00, 'paid', '2.5L 白色 user:备注信息', '2025-04-30 21:12:19', '2025-04-30 21:12:47');
INSERT INTO `orders` VALUES (41, 7, 'ORD202504305b99d0', 115.00, 'paid', '2.5L 白色 user:备注信息', '2025-04-30 21:16:07', '2025-04-30 21:16:41');
INSERT INTO `orders` VALUES (42, 7, 'ORD20250430dcc5a1', 100.00, 'cancelled', '2.5L 白色 user:备注信息', '2025-04-30 21:19:03', '2025-04-30 21:20:03');
INSERT INTO `orders` VALUES (43, 7, 'ORD20250430301409', 99.98, 'cancelled', '2.5L 粉色 user:备注信息', '2025-04-30 21:31:04', '2025-04-30 21:32:03');
INSERT INTO `orders` VALUES (44, 7, 'ORD2025043006e583', 78.80, 'paid', '2.5L 白色 user:备注信息', '2025-04-30 21:32:51', '2025-04-30 21:32:58');
INSERT INTO `orders` VALUES (45, 7, 'ORD202504308f7ca4', 78.80, 'cancelled', '2.5L 白色 user:备注信息', '2025-04-30 21:33:08', '2025-04-30 21:34:08');
INSERT INTO `orders` VALUES (46, 7, 'ORD2025043065f9f4', 345.00, 'paid', '2.5L 白色 user:备注信息', '2025-04-30 22:03:15', '2025-04-30 22:04:10');
INSERT INTO `orders` VALUES (47, 7, 'ORD20250430aae914', 256.00, 'cancelled', '2.5L 白色 user:备注信息', '2025-04-30 22:09:37', '2025-04-30 22:10:36');
INSERT INTO `orders` VALUES (48, 7, 'ORD20250430c43851', 218.00, 'cancelled', '2.5L 白色 user:备注信息', '2025-04-30 22:10:26', '2025-04-30 22:11:26');
INSERT INTO `orders` VALUES (49, 7, 'ORD20250430373579', 538.00, 'cancelled', '2.5L 白色 user:备注信息', '2025-04-30 22:13:11', '2025-04-30 22:14:10');
INSERT INTO `orders` VALUES (50, 7, 'ORD20250430f88fa7', 538.00, 'paid', '2.5L 白色 user:备注信息', '2025-04-30 22:14:21', '2025-04-30 22:14:32');
INSERT INTO `orders` VALUES (51, 7, 'ORD20250430111f56', 87.90, 'paid', '2.5L 白色 user:备注信息', '2025-04-30 22:17:31', '2025-04-30 22:18:01');
INSERT INTO `orders` VALUES (52, 7, 'ORD20250430457021', 538.00, 'paid', '2.5L 白色 user:备注信息', '2025-04-30 22:19:00', '2025-04-30 22:19:13');
INSERT INTO `orders` VALUES (53, 7, 'ORD202504308682dc', 575.00, 'completed', '2.5L 白色 user:备注信息', '2025-04-30 22:33:35', '2025-05-15 09:06:11');
INSERT INTO `orders` VALUES (54, 7, 'ORD2025043098814f', 90.00, 'completed', '2.5L 白色 user:备注信息', '2025-04-30 22:44:43', '2025-05-12 18:05:25');
INSERT INTO `orders` VALUES (55, 7, 'ORD20250507d4d185', 149.97, 'paid', '2.5L 白色 user:备注信息', '2025-05-07 11:07:14', '2025-05-07 11:07:37');
INSERT INTO `orders` VALUES (56, 7, 'ORD20250507e885e1', 45.00, 'paid', '2.5L 白色 user:备注信息', '2025-05-07 13:15:16', '2025-05-07 13:15:27');
INSERT INTO `orders` VALUES (57, 7, 'ORD20250507567b8c', 90.00, 'paid', '2.5L 粉色 user:备注信息', '2025-05-07 13:15:57', '2025-05-07 13:16:03');
INSERT INTO `orders` VALUES (58, 7, 'ORD20250507a76e64', 49.99, 'paid', '2.5L 白色 user:备注信息', '2025-05-07 13:24:38', '2025-05-07 13:24:47');
INSERT INTO `orders` VALUES (59, 7, 'ORD202505080113c5', 49.99, 'paid', '2.5L 白色 user:备注信息', '2025-05-08 13:33:56', '2025-05-08 13:34:08');
INSERT INTO `orders` VALUES (60, 7, 'ORD20250508b95c29', 49.99, 'cancelled', '2.5L 白色 user:备注信息', '2025-05-08 14:24:49', '2025-05-08 14:25:00');
INSERT INTO `orders` VALUES (61, 7, 'ORD202505099ae07d', 236.40, 'paid', '2.5L 蓝色 user:备注信息', '2025-05-09 11:32:05', '2025-05-09 11:32:31');
INSERT INTO `orders` VALUES (62, 7, 'ORD20250512cec0dc', 200.00, 'paid', '2.5L 白色 user:备注信息', '2025-05-12 10:44:55', '2025-05-12 10:45:07');
INSERT INTO `orders` VALUES (63, 7, 'ORD20250512d5c5c3', 384.00, 'shipped', '2.5L 粉色  用户备注：备注信息', '2025-05-12 15:24:00', '2025-05-16 10:02:44');
INSERT INTO `orders` VALUES (65, 7, 'ORD2025051549e6d4', 316.00, 'cancelled', '2.5L 白色  用户备注：备注信息', '2025-05-15 09:11:29', '2025-05-15 09:12:29');

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
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '宠物信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pets
-- ----------------------------
INSERT INTO `pets` VALUES (3, 7, '小狗', 'dog', '拉布拉多', '2024-05-01', '/images/default/default_dog.jpg', '2025-05-15 20:44:59', '2025-05-15 21:28:44');
INSERT INTO `pets` VALUES (4, 7, '小咪', 'cat', '狸花猫', '2025-02-03', '/images/default/default_cat.jpg', '2025-05-15 21:16:02', '2025-05-15 21:37:29');
INSERT INTO `pets` VALUES (10, 7, '小小咪', 'cat', '小咪', '2025-04-01', '/images/petAvatar/ae41b7e5-48de-4247-8682-0cd9e0885c9a.jpg', '2025-05-16 11:59:20', '2025-05-16 18:46:43');
INSERT INTO `pets` VALUES (11, 12, '狗狗', 'dog', '好', '2025-05-19', '/images/petAvatar/fedd4ae5-52ff-4034-8514-dcdc5b7b068a.jpg', '2025-05-19 15:14:14', '2025-05-19 15:14:33');
INSERT INTO `pets` VALUES (12, 8, '小猫', 'other', '布偶猫', '2025-03-05', '/images/petAvatar/0ad688e6-7d12-4fc5-a4b9-5cbd8ecc5e48.jpg', '2025-05-20 13:11:03', '2025-05-20 16:06:47');
INSERT INTO `pets` VALUES (13, 12, '小鹉', 'bird', '鹦鹉', '2025-05-06', '/images/petAvatar/0ad688e6-7d12-4fc5-a4b9-5cbd8ecc5e48.jpg', '2025-05-20 16:46:35', '2025-05-20 16:52:39');
INSERT INTO `pets` VALUES (15, 12, '鱼', 'fish', '锦鲤', '2025-05-14', '/statics/images/defaultPetAvatar.jpg', '2025-05-20 16:47:48', '2025-05-20 16:47:48');
INSERT INTO `pets` VALUES (16, 12, '旺财', 'dog', '金毛犬', '2024-01-15', '/images/petAvatar/dog_golden.jpg', '2025-05-20 16:58:49', '2025-05-20 16:58:49');
INSERT INTO `pets` VALUES (17, 12, '布丁', 'cat', '布偶猫', '2025-03-20', '/images/petAvatar/cat_ragdoll.jpg', '2025-05-20 16:58:49', '2025-05-20 16:58:49');
INSERT INTO `pets` VALUES (18, 12, '小蓝', 'bird', '虎皮鹦鹉', '2025-02-10', '/images/petAvatar/bird_parrot.jpg', '2025-05-20 16:58:49', '2025-05-20 16:58:49');
INSERT INTO `pets` VALUES (19, 12, '泡泡', 'fish', '孔雀鱼', '2025-04-05', '/images/petAvatar/fish_guppy.jpg', '2025-05-20 16:58:49', '2025-05-20 16:58:49');
INSERT INTO `pets` VALUES (20, 12, '雪球', 'rabbit', '垂耳兔', '2025-01-30', '/images/petAvatar/rabbit_lop.jpg', '2025-05-20 16:58:49', '2025-05-20 16:58:49');
INSERT INTO `pets` VALUES (21, 12, '豆丁', 'hamster', '金丝熊', '2025-05-01', '/images/petAvatar/hamster_syrian.jpg', '2025-05-20 16:58:49', '2025-05-20 16:58:49');
INSERT INTO `pets` VALUES (22, 12, '小鳞', 'reptile', '鬃狮蜥', '2024-11-12', '/images/petAvatar/reptile_bearded.jpg', '2025-05-20 16:58:49', '2025-05-20 16:58:49');
INSERT INTO `pets` VALUES (23, 12, '刺刺', 'other', '非洲迷你刺猬', '2025-03-15', '/statics/images/defaultPetAvatar.jpg', '2025-05-20 16:58:49', '2025-05-20 16:58:49');
INSERT INTO `pets` VALUES (24, 12, '小bird', 'bird', '文鸟', '2023-12-06', '/statics/images/defaultPetAvatar.jpg', '2024-02-04 17:48:34', '2024-02-04 17:48:34');
INSERT INTO `pets` VALUES (25, 12, '调皮fish', 'fish', '锦鲤', '2021-12-28', '/images/petAvatar/fish_1.jpg', '2024-02-13 17:48:34', '2024-02-13 17:48:34');
INSERT INTO `pets` VALUES (26, 12, '调皮dog', 'dog', '泰迪', '2024-02-25', '/images/petAvatar/dog_9.jpg', '2024-03-16 17:48:34', '2024-03-16 17:48:34');
INSERT INTO `pets` VALUES (27, 12, '调皮dog', 'dog', '柯基', '2020-10-06', '/images/petAvatar/dog_3.jpg', '2023-06-05 17:48:34', '2023-06-05 17:48:34');
INSERT INTO `pets` VALUES (28, 12, '聪明cat', 'cat', '英短', '2024-02-05', '/images/petAvatar/cat_6.jpg', '2024-02-04 17:48:34', '2024-02-04 17:48:34');
INSERT INTO `pets` VALUES (29, 12, '小dog', 'dog', '柯基', '2020-07-20', '/images/petAvatar/dog_5.jpg', '2024-04-05 17:48:34', '2024-04-05 17:48:34');
INSERT INTO `pets` VALUES (30, 12, '调皮fish', 'fish', '锦鲤', '2023-03-07', '/statics/images/defaultPetAvatar.jpg', '2024-07-23 17:48:34', '2024-07-23 17:48:34');
INSERT INTO `pets` VALUES (31, 12, '调皮hamster', 'hamster', '金丝熊', '2022-01-24', '/statics/images/defaultPetAvatar.jpg', '2024-03-09 17:48:34', '2024-03-09 17:48:34');
INSERT INTO `pets` VALUES (32, 12, '可爱dog', 'dog', '柯基', '2024-11-08', '/images/petAvatar/dog_2.jpg', '2024-04-25 17:48:34', '2024-04-25 17:48:34');
INSERT INTO `pets` VALUES (33, 12, '温柔dog', 'dog', '柯基', '2024-05-22', '/images/petAvatar/dog_4.jpg', '2023-07-03 17:48:34', '2023-07-03 17:48:34');
INSERT INTO `pets` VALUES (34, 12, '温柔dog', 'dog', '边牧', '2024-11-25', '/images/petAvatar/dog_3.jpg', '2023-11-20 17:48:34', '2023-11-20 17:48:34');
INSERT INTO `pets` VALUES (35, 12, '可爱fish', 'fish', '金鱼', '2023-03-05', '/statics/images/defaultPetAvatar.jpg', '2023-08-03 17:48:34', '2023-08-03 17:48:34');
INSERT INTO `pets` VALUES (36, 12, '可爱fish', 'fish', '斗鱼', '2020-07-29', '/images/petAvatar/fish_1.jpg', '2024-09-27 17:48:34', '2024-09-27 17:48:34');
INSERT INTO `pets` VALUES (37, 12, '霸气hamster', 'hamster', '金丝熊', '2023-01-20', '/statics/images/defaultPetAvatar.jpg', '2024-10-15 17:48:34', '2024-10-15 17:48:34');
INSERT INTO `pets` VALUES (38, 12, '调皮other', 'other', '蜜袋鼯', '2024-02-10', '/images/petAvatar/other_7.jpg', '2023-07-31 17:48:34', '2023-07-31 17:48:34');
INSERT INTO `pets` VALUES (39, 12, '聪明cat', 'cat', '英短', '2023-12-02', '/images/petAvatar/cat_9.jpg', '2024-08-23 17:48:34', '2024-08-23 17:48:34');
INSERT INTO `pets` VALUES (40, 12, '活泼rabbit', 'rabbit', '安哥拉兔', '2023-01-24', '/images/petAvatar/rabbit_3.jpg', '2023-12-15 17:48:34', '2023-12-15 17:48:34');
INSERT INTO `pets` VALUES (41, 12, '霸气cat', 'cat', '布偶猫', '2020-06-22', '/statics/images/defaultPetAvatar.jpg', '2023-08-16 17:48:34', '2023-08-16 17:48:34');
INSERT INTO `pets` VALUES (42, 12, '温柔dog', 'dog', '哈士奇', '2021-12-16', '/statics/images/defaultPetAvatar.jpg', '2024-04-24 17:48:34', '2024-04-24 17:48:34');
INSERT INTO `pets` VALUES (43, 12, '活泼bird', 'bird', '八哥', '2023-11-23', '/images/petAvatar/bird_1.jpg', '2023-06-23 17:48:34', '2023-06-23 17:48:34');
INSERT INTO `pets` VALUES (44, 12, '温柔fish', 'fish', '龙鱼', '2021-02-03', '/statics/images/defaultPetAvatar.jpg', '2023-08-02 17:48:34', '2023-08-02 17:48:34');
INSERT INTO `pets` VALUES (45, 12, '聪明cat', 'cat', '橘猫', '2024-06-01', '/images/petAvatar/cat_5.jpg', '2023-12-24 17:48:34', '2023-12-24 17:48:34');
INSERT INTO `pets` VALUES (46, 12, '调皮rabbit', 'rabbit', '垂耳兔', '2020-06-03', '/images/petAvatar/rabbit_9.jpg', '2023-09-04 17:48:34', '2023-09-04 17:48:34');
INSERT INTO `pets` VALUES (47, 12, '聪明dog', 'dog', '金毛', '2022-04-12', '/images/petAvatar/dog_9.jpg', '2024-05-19 17:48:34', '2024-05-19 17:48:34');
INSERT INTO `pets` VALUES (48, 12, '活泼bird', 'bird', '鹦鹉', '2024-09-03', '/images/petAvatar/bird_5.jpg', '2024-09-16 17:48:34', '2024-09-16 17:48:34');
INSERT INTO `pets` VALUES (49, 12, '调皮dog', 'dog', '拉布拉多', '2024-10-27', '/images/petAvatar/dog_2.jpg', '2023-11-21 17:48:34', '2023-11-21 17:48:34');
INSERT INTO `pets` VALUES (50, 12, '可爱rabbit', 'rabbit', '安哥拉兔', '2022-08-21', '/images/petAvatar/rabbit_3.jpg', '2024-08-21 17:48:34', '2024-08-21 17:48:34');
INSERT INTO `pets` VALUES (51, 12, '调皮cat', 'cat', '橘猫', '2024-02-12', '/images/petAvatar/cat_4.jpg', '2024-07-04 17:48:34', '2024-07-04 17:48:34');
INSERT INTO `pets` VALUES (52, 12, '温柔cat', 'cat', '缅因猫', '2023-03-18', '/images/petAvatar/cat_9.jpg', '2024-02-12 17:48:34', '2024-02-12 17:48:34');
INSERT INTO `pets` VALUES (53, 12, '可爱dog', 'dog', '拉布拉多', '2023-05-29', '/statics/images/defaultPetAvatar.jpg', '2023-10-30 17:48:34', '2023-10-30 17:48:34');
INSERT INTO `pets` VALUES (54, 12, '可爱dog', 'dog', '拉布拉多', '2024-11-26', '/images/petAvatar/dog_1.jpg', '2024-09-27 17:48:34', '2024-09-27 17:48:34');
INSERT INTO `pets` VALUES (55, 12, '小dog', 'dog', '柯基', '2024-11-26', '/images/petAvatar/dog_5.jpg', '2023-07-04 17:48:34', '2023-07-04 17:48:34');
INSERT INTO `pets` VALUES (56, 12, '调皮cat', 'cat', '英短', '2021-03-31', '/images/petAvatar/cat_10.jpg', '2023-10-07 17:48:34', '2023-10-07 17:48:34');
INSERT INTO `pets` VALUES (57, 12, '霸气cat', 'cat', '橘猫', '2020-10-10', '/images/petAvatar/cat_2.jpg', '2024-02-09 17:48:34', '2024-02-09 17:48:34');
INSERT INTO `pets` VALUES (58, 12, '聪明fish', 'fish', '金鱼', '2022-12-31', '/images/petAvatar/fish_1.jpg', '2023-06-30 17:48:34', '2023-06-30 17:48:34');
INSERT INTO `pets` VALUES (59, 12, '温柔fish', 'fish', '锦鲤', '2024-12-17', '/images/petAvatar/fish_6.jpg', '2024-01-28 17:48:34', '2024-01-28 17:48:34');
INSERT INTO `pets` VALUES (60, 12, '可爱dog', 'dog', '泰迪', '2022-10-15', '/statics/images/defaultPetAvatar.jpg', '2024-11-13 17:48:34', '2024-11-13 17:48:34');
INSERT INTO `pets` VALUES (61, 12, '可爱dog', 'dog', '拉布拉多', '2022-10-26', '/images/petAvatar/dog_9.jpg', '2024-02-08 17:48:34', '2024-02-08 17:48:34');
INSERT INTO `pets` VALUES (62, 12, '调皮dog', 'dog', '拉布拉多', '2024-10-11', '/images/petAvatar/dog_4.jpg', '2024-11-30 17:48:34', '2024-11-30 17:48:34');
INSERT INTO `pets` VALUES (63, 12, '活泼cat', 'cat', '英短', '2020-06-15', '/images/petAvatar/cat_1.jpg', '2024-12-02 17:48:34', '2024-12-02 17:48:34');
INSERT INTO `pets` VALUES (64, 12, '活泼cat', 'cat', '美短', '2020-11-27', '/images/petAvatar/cat_5.jpg', '2024-03-12 17:48:34', '2024-03-12 17:48:34');
INSERT INTO `pets` VALUES (65, 12, '温柔fish', 'fish', '龙鱼', '2021-08-04', '/images/petAvatar/fish_3.jpg', '2024-11-06 17:48:34', '2024-11-06 17:48:34');
INSERT INTO `pets` VALUES (66, 12, '小dog', 'dog', '泰迪', '2021-03-06', '/images/petAvatar/dog_6.jpg', '2024-03-22 17:48:34', '2024-03-22 17:48:34');
INSERT INTO `pets` VALUES (67, 12, '活泼dog', 'dog', '泰迪', '2020-07-09', '/images/petAvatar/dog_1.jpg', '2023-12-16 17:48:34', '2023-12-16 17:48:34');
INSERT INTO `pets` VALUES (68, 12, '可爱dog', 'dog', '拉布拉多', '2021-12-19', '/statics/images/defaultPetAvatar.jpg', '2024-09-21 17:48:34', '2024-09-21 17:48:34');
INSERT INTO `pets` VALUES (69, 12, '调皮cat', 'cat', '英短', '2022-01-21', '/images/petAvatar/cat_10.jpg', '2024-02-25 17:48:34', '2024-02-25 17:48:34');
INSERT INTO `pets` VALUES (70, 12, '调皮cat', 'cat', '狸花猫', '2022-05-16', '/images/petAvatar/cat_5.jpg', '2024-10-22 17:48:34', '2024-10-22 17:48:34');
INSERT INTO `pets` VALUES (71, 12, '聪明fish', 'fish', '孔雀鱼', '2023-10-25', '/images/petAvatar/fish_5.jpg', '2024-02-06 17:48:34', '2024-02-06 17:48:34');
INSERT INTO `pets` VALUES (72, 12, '霸气cat', 'cat', '布偶猫', '2024-04-01', '/images/petAvatar/cat_10.jpg', '2024-02-06 17:48:34', '2024-02-06 17:48:34');
INSERT INTO `pets` VALUES (73, 12, '可爱dog', 'dog', '泰迪', '2023-10-25', '/statics/images/defaultPetAvatar.jpg', '2024-05-27 17:48:34', '2024-05-27 17:48:34');
INSERT INTO `pets` VALUES (74, 12, '可爱reptile', 'reptile', '守宫', '2023-09-15', '/statics/images/defaultPetAvatar.jpg', '2023-11-10 17:48:34', '2023-11-10 17:48:34');
INSERT INTO `pets` VALUES (75, 12, '调皮fish', 'fish', '龙鱼', '2024-04-04', '/images/petAvatar/fish_9.jpg', '2024-07-17 17:48:34', '2024-07-17 17:48:34');
INSERT INTO `pets` VALUES (76, 12, '小other', 'other', '龙猫', '2023-10-27', '/statics/images/defaultPetAvatar.jpg', '2024-01-30 17:48:34', '2024-01-30 17:48:34');
INSERT INTO `pets` VALUES (77, 12, '可爱rabbit', 'rabbit', '荷兰侏儒兔', '2023-09-21', '/images/petAvatar/rabbit_6.jpg', '2024-10-23 17:48:34', '2024-10-23 17:48:34');
INSERT INTO `pets` VALUES (78, 12, '霸气fish', 'fish', '斗鱼', '2022-06-20', '/images/petAvatar/fish_1.jpg', '2024-02-14 17:48:34', '2024-02-14 17:48:34');
INSERT INTO `pets` VALUES (79, 12, '霸气bird', 'bird', '文鸟', '2024-01-20', '/statics/images/defaultPetAvatar.jpg', '2024-01-06 17:48:34', '2024-01-06 17:48:34');
INSERT INTO `pets` VALUES (80, 12, '聪明bird', 'bird', '鸽子', '2021-02-22', '/images/petAvatar/bird_7.jpg', '2023-10-23 17:48:34', '2023-10-23 17:48:34');
INSERT INTO `pets` VALUES (81, 12, '小dog', 'dog', '边牧', '2024-06-19', '/images/petAvatar/dog_6.jpg', '2023-10-01 17:48:34', '2023-10-01 17:48:34');
INSERT INTO `pets` VALUES (82, 12, '活泼bird', 'bird', '鸽子', '2024-01-25', '/images/petAvatar/bird_1.jpg', '2024-05-18 17:48:34', '2024-05-18 17:48:34');
INSERT INTO `pets` VALUES (83, 12, '可爱dog', 'dog', '边牧', '2024-09-22', '/images/petAvatar/dog_9.jpg', '2024-03-29 17:48:34', '2024-03-29 17:48:34');
INSERT INTO `pets` VALUES (84, 12, '温柔other', 'other', '龙猫', '2023-12-23', '/images/petAvatar/other_3.jpg', '2024-03-23 17:48:34', '2024-03-23 17:48:34');
INSERT INTO `pets` VALUES (85, 12, '温柔dog', 'dog', '柯基', '2020-11-30', '/images/petAvatar/dog_4.jpg', '2024-08-19 17:48:34', '2024-08-19 17:48:34');
INSERT INTO `pets` VALUES (86, 12, '温柔dog', 'dog', '拉布拉多', '2023-02-27', '/images/petAvatar/dog_8.jpg', '2024-10-04 17:48:34', '2024-10-04 17:48:34');
INSERT INTO `pets` VALUES (87, 12, '活泼dog', 'dog', '柯基', '2023-08-04', '/images/petAvatar/dog_4.jpg', '2024-10-03 17:48:34', '2024-10-03 17:48:34');
INSERT INTO `pets` VALUES (88, 12, '可爱dog', 'dog', '哈士奇', '2023-06-18', '/statics/images/defaultPetAvatar.jpg', '2024-03-09 17:48:34', '2024-03-09 17:48:34');
INSERT INTO `pets` VALUES (89, 12, '聪明other', 'other', '蜜袋鼯', '2021-09-15', '/images/petAvatar/other_9.jpg', '2024-06-14 17:48:34', '2024-06-14 17:48:34');
INSERT INTO `pets` VALUES (90, 12, '小other', 'other', '蜜袋鼯', '2024-04-20', '/images/petAvatar/other_5.jpg', '2024-04-10 17:48:34', '2024-04-10 17:48:34');
INSERT INTO `pets` VALUES (91, 12, '活泼dog', 'dog', '柯基', '2021-03-20', '/images/petAvatar/dog_7.jpg', '2023-09-07 17:48:34', '2023-09-07 17:48:34');
INSERT INTO `pets` VALUES (92, 12, '小other', 'other', '宠物猪', '2022-01-26', '/statics/images/defaultPetAvatar.jpg', '2024-04-04 17:48:34', '2024-04-04 17:48:34');
INSERT INTO `pets` VALUES (93, 12, '小fish', 'fish', '龙鱼', '2020-10-12', '/images/petAvatar/fish_4.jpg', '2024-05-12 17:48:34', '2024-05-12 17:48:34');
INSERT INTO `pets` VALUES (94, 12, '温柔cat', 'cat', '美短', '2021-10-22', '/images/petAvatar/cat_2.jpg', '2024-07-16 17:48:34', '2024-07-16 17:48:34');
INSERT INTO `pets` VALUES (95, 12, '温柔cat', 'cat', '橘猫', '2023-06-22', '/images/petAvatar/cat_5.jpg', '2023-10-30 17:48:34', '2023-10-30 17:48:34');
INSERT INTO `pets` VALUES (96, 12, '活泼dog', 'dog', '边牧', '2023-03-06', '/images/petAvatar/dog_3.jpg', '2023-08-28 17:48:34', '2023-08-28 17:48:34');
INSERT INTO `pets` VALUES (97, 12, '活泼bird', 'bird', '八哥', '2020-08-17', '/statics/images/defaultPetAvatar.jpg', '2024-07-30 17:48:34', '2024-07-30 17:48:34');
INSERT INTO `pets` VALUES (98, 12, '霸气dog', 'dog', '泰迪', '2021-11-25', '/images/petAvatar/dog_8.jpg', '2024-02-22 17:48:34', '2024-02-22 17:48:34');
INSERT INTO `pets` VALUES (99, 12, '可爱cat', 'cat', '狸花猫', '2022-08-25', '/images/petAvatar/cat_3.jpg', '2023-07-15 17:48:34', '2023-07-15 17:48:34');
INSERT INTO `pets` VALUES (100, 12, '调皮dog', 'dog', '边牧', '2021-10-02', '/images/petAvatar/dog_2.jpg', '2024-09-21 17:48:34', '2024-09-21 17:48:34');
INSERT INTO `pets` VALUES (101, 12, '可爱fish', 'fish', '孔雀鱼', '2024-06-23', '/statics/images/defaultPetAvatar.jpg', '2024-09-11 17:48:34', '2024-09-11 17:48:34');
INSERT INTO `pets` VALUES (102, 12, '调皮other', 'other', '刺猬', '2022-10-01', '/images/petAvatar/other_10.jpg', '2024-11-01 17:48:34', '2024-11-01 17:48:34');
INSERT INTO `pets` VALUES (103, 12, '活泼fish', 'fish', '金鱼', '2023-01-23', '/statics/images/defaultPetAvatar.jpg', '2023-07-18 17:48:34', '2023-07-18 17:48:34');
INSERT INTO `pets` VALUES (104, 12, '霸气fish', 'fish', '锦鲤', '2020-12-08', '/images/petAvatar/fish_3.jpg', '2023-06-07 17:48:34', '2023-06-07 17:48:34');
INSERT INTO `pets` VALUES (105, 12, '小cat', 'cat', '狸花猫', '2023-11-27', '/images/petAvatar/cat_2.jpg', '2024-02-07 17:48:34', '2024-02-07 17:48:34');
INSERT INTO `pets` VALUES (106, 12, '活泼dog', 'dog', '边牧', '2022-04-29', '/images/petAvatar/dog_10.jpg', '2024-06-29 17:48:34', '2024-06-29 17:48:34');
INSERT INTO `pets` VALUES (107, 12, '活泼other', 'other', '宠物猪', '2020-08-23', '/images/petAvatar/other_8.jpg', '2024-12-08 17:48:34', '2024-12-08 17:48:34');
INSERT INTO `pets` VALUES (108, 12, '活泼rabbit', 'rabbit', '荷兰侏儒兔', '2023-11-07', '/images/petAvatar/rabbit_5.jpg', '2023-12-03 17:48:34', '2023-12-03 17:48:34');
INSERT INTO `pets` VALUES (109, 12, '可爱cat', 'cat', '美短', '2022-11-02', '/statics/images/defaultPetAvatar.jpg', '2024-07-31 17:48:34', '2024-07-31 17:48:34');
INSERT INTO `pets` VALUES (110, 12, '聪明dog', 'dog', '柯基', '2022-04-10', '/statics/images/defaultPetAvatar.jpg', '2024-12-16 17:48:34', '2024-12-16 17:48:34');
INSERT INTO `pets` VALUES (111, 12, '温柔dog', 'dog', '金毛', '2021-05-05', '/statics/images/defaultPetAvatar.jpg', '2024-03-21 17:48:34', '2024-03-21 17:48:34');
INSERT INTO `pets` VALUES (112, 12, '调皮cat', 'cat', '橘猫', '2022-12-21', '/statics/images/defaultPetAvatar.jpg', '2024-03-15 17:48:34', '2024-03-15 17:48:34');
INSERT INTO `pets` VALUES (113, 12, '霸气cat', 'cat', '狸花猫', '2021-05-27', '/statics/images/defaultPetAvatar.jpg', '2023-10-08 17:48:34', '2023-10-08 17:48:34');
INSERT INTO `pets` VALUES (114, 12, '小cat', 'cat', '美短', '2023-08-18', '/images/petAvatar/cat_7.jpg', '2023-11-15 17:48:34', '2023-11-15 17:48:34');
INSERT INTO `pets` VALUES (115, 12, '调皮rabbit', 'rabbit', '安哥拉兔', '2024-01-25', '/images/petAvatar/rabbit_5.jpg', '2024-02-28 17:48:34', '2024-02-28 17:48:34');
INSERT INTO `pets` VALUES (116, 12, '活泼cat', 'cat', '美短', '2021-07-21', '/images/petAvatar/cat_7.jpg', '2024-12-01 17:48:34', '2024-12-01 17:48:34');
INSERT INTO `pets` VALUES (117, 12, '可爱hamster', 'hamster', '老公公', '2022-04-23', '/statics/images/defaultPetAvatar.jpg', '2024-04-11 17:48:34', '2024-04-11 17:48:34');
INSERT INTO `pets` VALUES (118, 12, '温柔bird', 'bird', '鹦鹉', '2024-10-31', '/images/petAvatar/bird_3.jpg', '2024-02-02 17:48:34', '2024-02-02 17:48:34');
INSERT INTO `pets` VALUES (119, 12, '聪明dog', 'dog', '哈士奇', '2023-06-20', '/statics/images/defaultPetAvatar.jpg', '2024-06-19 17:48:34', '2024-06-19 17:48:34');
INSERT INTO `pets` VALUES (120, 12, '活泼cat', 'cat', '美短', '2021-01-31', '/images/petAvatar/cat_7.jpg', '2024-09-03 17:48:34', '2024-09-03 17:48:34');
INSERT INTO `pets` VALUES (121, 12, '活泼hamster', 'hamster', '仓鼠', '2021-02-06', '/images/petAvatar/hamster_6.jpg', '2024-10-02 17:48:34', '2024-10-02 17:48:34');
INSERT INTO `pets` VALUES (122, 12, '霸气fish', 'fish', '锦鲤', '2021-01-27', '/statics/images/defaultPetAvatar.jpg', '2024-09-08 17:48:34', '2024-09-08 17:48:34');
INSERT INTO `pets` VALUES (123, 12, '小dog', 'dog', '泰迪', '2024-06-12', '/statics/images/defaultPetAvatar.jpg', '2024-01-21 17:48:34', '2024-01-21 17:48:34');
INSERT INTO `pets` VALUES (124, 12, '调皮fish', 'fish', '孔雀鱼', '2022-09-11', '/images/petAvatar/fish_6.jpg', '2024-11-29 17:48:34', '2024-11-29 17:48:34');
INSERT INTO `pets` VALUES (125, 12, '温柔bird', 'bird', '鹦鹉', '2020-12-10', '/images/petAvatar/bird_5.jpg', '2024-01-29 17:48:34', '2024-01-29 17:48:34');
INSERT INTO `pets` VALUES (126, 12, '聪明bird', 'bird', '八哥', '2021-07-03', '/images/petAvatar/bird_7.jpg', '2024-04-08 17:48:34', '2024-04-08 17:48:34');
INSERT INTO `pets` VALUES (127, 12, '可爱other', 'other', '刺猬', '2021-12-25', '/images/petAvatar/other_4.jpg', '2024-02-04 17:48:34', '2024-02-04 17:48:34');
INSERT INTO `pets` VALUES (128, 12, '聪明fish', 'fish', '龙鱼', '2020-10-14', '/statics/images/defaultPetAvatar.jpg', '2023-10-18 17:48:34', '2023-10-18 17:48:34');
INSERT INTO `pets` VALUES (129, 12, '聪明rabbit', 'rabbit', '安哥拉兔', '2022-11-25', '/images/petAvatar/rabbit_2.jpg', '2023-12-18 17:48:34', '2023-12-18 17:48:34');
INSERT INTO `pets` VALUES (130, 12, '小bird', 'bird', '画眉', '2020-09-09', '/images/petAvatar/bird_7.jpg', '2024-05-14 17:48:34', '2024-05-14 17:48:34');
INSERT INTO `pets` VALUES (131, 12, '霸气bird', 'bird', '画眉', '2021-05-03', '/statics/images/defaultPetAvatar.jpg', '2024-11-20 17:48:34', '2024-11-20 17:48:34');
INSERT INTO `pets` VALUES (132, 12, '温柔rabbit', 'rabbit', '荷兰侏儒兔', '2021-11-06', '/images/petAvatar/rabbit_9.jpg', '2024-01-10 17:48:34', '2024-01-10 17:48:34');
INSERT INTO `pets` VALUES (133, 12, '霸气cat', 'cat', '缅因猫', '2021-12-24', '/statics/images/defaultPetAvatar.jpg', '2024-09-10 17:48:34', '2024-09-10 17:48:34');
INSERT INTO `pets` VALUES (134, 12, '小hamster', 'hamster', '金丝熊', '2022-10-10', '/statics/images/defaultPetAvatar.jpg', '2024-01-30 17:48:34', '2024-01-30 17:48:34');
INSERT INTO `pets` VALUES (135, 12, '温柔bird', 'bird', '画眉', '2023-06-02', '/statics/images/defaultPetAvatar.jpg', '2024-06-21 17:48:34', '2024-06-21 17:48:34');
INSERT INTO `pets` VALUES (136, 12, '可爱dog', 'dog', '柯基', '2020-10-30', '/statics/images/defaultPetAvatar.jpg', '2024-08-06 17:48:34', '2024-08-06 17:48:34');
INSERT INTO `pets` VALUES (137, 12, '温柔reptile', 'reptile', '守宫', '2022-03-09', '/statics/images/defaultPetAvatar.jpg', '2024-11-05 17:48:34', '2024-11-05 17:48:34');
INSERT INTO `pets` VALUES (138, 12, '温柔dog', 'dog', '哈士奇', '2022-07-25', '/images/petAvatar/dog_4.jpg', '2023-06-12 17:48:34', '2023-06-12 17:48:34');
INSERT INTO `pets` VALUES (139, 12, '温柔cat', 'cat', '橘猫', '2022-10-16', '/images/petAvatar/cat_2.jpg', '2024-07-22 17:48:34', '2024-07-22 17:48:34');
INSERT INTO `pets` VALUES (140, 12, '温柔dog', 'dog', '金毛', '2023-08-02', '/images/petAvatar/dog_6.jpg', '2024-01-21 17:48:34', '2024-01-21 17:48:34');
INSERT INTO `pets` VALUES (141, 12, '聪明bird', 'bird', '画眉', '2021-02-13', '/images/petAvatar/bird_6.jpg', '2023-05-31 17:48:34', '2023-05-31 17:48:34');
INSERT INTO `pets` VALUES (142, 12, '聪明cat', 'cat', '美短', '2024-01-04', '/statics/images/defaultPetAvatar.jpg', '2024-11-03 17:48:34', '2024-11-03 17:48:34');
INSERT INTO `pets` VALUES (143, 12, '温柔cat', 'cat', '狸花猫', '2021-01-11', '/images/petAvatar/cat_4.jpg', '2024-10-10 17:48:34', '2024-10-10 17:48:34');
INSERT INTO `pets` VALUES (144, 12, '聪明rabbit', 'rabbit', '安哥拉兔', '2021-12-30', '/images/petAvatar/rabbit_9.jpg', '2023-09-16 17:48:34', '2023-09-16 17:48:34');
INSERT INTO `pets` VALUES (145, 12, '小dog', 'dog', '金毛', '2023-08-23', '/statics/images/defaultPetAvatar.jpg', '2024-11-17 17:48:34', '2024-11-17 17:48:34');
INSERT INTO `pets` VALUES (146, 12, '温柔cat', 'cat', '狸花猫', '2024-08-04', '/statics/images/defaultPetAvatar.jpg', '2024-03-25 17:48:34', '2024-03-25 17:48:34');
INSERT INTO `pets` VALUES (147, 12, '温柔bird', 'bird', '鸽子', '2021-09-22', '/images/petAvatar/bird_8.jpg', '2024-02-03 17:48:34', '2024-02-03 17:48:34');
INSERT INTO `pets` VALUES (148, 12, '调皮reptile', 'reptile', '守宫', '2022-09-02', '/images/petAvatar/reptile_6.jpg', '2024-11-12 17:48:34', '2024-11-12 17:48:34');
INSERT INTO `pets` VALUES (149, 12, '聪明other', 'other', '宠物猪', '2024-08-30', '/images/petAvatar/other_7.jpg', '2024-01-02 17:48:34', '2024-01-02 17:48:34');
INSERT INTO `pets` VALUES (150, 12, '温柔dog', 'dog', '边牧', '2024-01-01', '/statics/images/defaultPetAvatar.jpg', '2023-10-21 17:48:34', '2023-10-21 17:48:34');
INSERT INTO `pets` VALUES (151, 12, '小other', 'other', '龙猫', '2024-08-21', '/images/petAvatar/other_7.jpg', '2023-09-07 17:48:34', '2023-09-07 17:48:34');
INSERT INTO `pets` VALUES (152, 12, '调皮bird', 'bird', '鸽子', '2020-12-14', '/images/petAvatar/bird_8.jpg', '2024-02-21 17:48:34', '2024-02-21 17:48:34');
INSERT INTO `pets` VALUES (153, 12, '调皮dog', 'dog', '哈士奇', '2023-08-04', '/images/petAvatar/dog_1.jpg', '2023-09-03 17:48:34', '2023-09-03 17:48:34');
INSERT INTO `pets` VALUES (154, 12, '小other', 'other', '龙猫', '2021-10-17', '/statics/images/defaultPetAvatar.jpg', '2023-07-13 17:48:34', '2023-07-13 17:48:34');
INSERT INTO `pets` VALUES (155, 12, '可爱dog', 'dog', '金毛', '2022-03-17', '/statics/images/defaultPetAvatar.jpg', '2024-05-17 17:48:34', '2024-05-17 17:48:34');
INSERT INTO `pets` VALUES (156, 12, '温柔cat', 'cat', '狸花猫', '2022-09-01', '/statics/images/defaultPetAvatar.jpg', '2024-09-24 17:48:34', '2024-09-24 17:48:34');
INSERT INTO `pets` VALUES (157, 12, '聪明dog', 'dog', '边牧', '2020-06-25', '/images/petAvatar/dog_4.jpg', '2024-08-22 17:48:34', '2024-08-22 17:48:34');
INSERT INTO `pets` VALUES (158, 12, '温柔rabbit', 'rabbit', '安哥拉兔', '2021-08-05', '/images/petAvatar/rabbit_4.jpg', '2024-03-10 17:48:34', '2024-03-10 17:48:34');
INSERT INTO `pets` VALUES (159, 12, '可爱dog', 'dog', '边牧', '2024-06-19', '/images/petAvatar/dog_3.jpg', '2024-03-07 17:48:34', '2024-03-07 17:48:34');
INSERT INTO `pets` VALUES (160, 12, '霸气dog', 'dog', '柯基', '2021-12-19', '/images/petAvatar/dog_10.jpg', '2024-07-28 17:48:34', '2024-07-28 17:48:34');
INSERT INTO `pets` VALUES (161, 12, '活泼cat', 'cat', '缅因猫', '2023-08-07', '/images/petAvatar/cat_5.jpg', '2023-12-24 17:48:34', '2023-12-24 17:48:34');
INSERT INTO `pets` VALUES (162, 12, '活泼cat', 'cat', '布偶猫', '2021-12-13', '/statics/images/defaultPetAvatar.jpg', '2024-03-03 17:48:34', '2024-03-03 17:48:34');
INSERT INTO `pets` VALUES (163, 12, '调皮rabbit', 'rabbit', '荷兰侏儒兔', '2024-02-27', '/statics/images/defaultPetAvatar.jpg', '2023-06-29 17:48:34', '2023-06-29 17:48:34');
INSERT INTO `pets` VALUES (164, 12, '调皮hamster', 'hamster', '老公公', '2024-03-09', '/images/petAvatar/hamster_5.jpg', '2023-10-07 17:48:34', '2023-10-07 17:48:34');
INSERT INTO `pets` VALUES (165, 12, '霸气dog', 'dog', '金毛', '2022-08-31', '/images/petAvatar/dog_8.jpg', '2024-08-08 17:48:34', '2024-08-08 17:48:34');
INSERT INTO `pets` VALUES (166, 12, '调皮dog', 'dog', '哈士奇', '2021-09-28', '/images/petAvatar/dog_2.jpg', '2024-01-24 17:48:34', '2024-01-24 17:48:34');
INSERT INTO `pets` VALUES (167, 12, '聪明cat', 'cat', '美短', '2021-07-18', '/statics/images/defaultPetAvatar.jpg', '2024-12-06 17:48:34', '2024-12-06 17:48:34');
INSERT INTO `pets` VALUES (168, 12, '温柔cat', 'cat', '橘猫', '2023-09-08', '/images/petAvatar/cat_7.jpg', '2023-11-03 17:48:34', '2023-11-03 17:48:34');
INSERT INTO `pets` VALUES (169, 12, '小dog', 'dog', '柯基', '2023-11-21', '/images/petAvatar/dog_7.jpg', '2023-12-10 17:48:34', '2023-12-10 17:48:34');
INSERT INTO `pets` VALUES (170, 12, '小hamster', 'hamster', '老公公', '2020-06-05', '/images/petAvatar/hamster_7.jpg', '2024-03-25 17:48:34', '2024-03-25 17:48:34');
INSERT INTO `pets` VALUES (171, 12, '聪明dog', 'dog', '泰迪', '2021-01-22', '/images/petAvatar/dog_8.jpg', '2024-03-28 17:48:34', '2024-03-28 17:48:34');
INSERT INTO `pets` VALUES (172, 12, '调皮dog', 'dog', '金毛', '2023-09-09', '/images/petAvatar/dog_8.jpg', '2024-01-15 17:48:34', '2024-01-15 17:48:34');
INSERT INTO `pets` VALUES (173, 12, '温柔dog', 'dog', '泰迪', '2020-10-27', '/statics/images/defaultPetAvatar.jpg', '2024-12-08 17:48:34', '2024-12-08 17:48:34');
INSERT INTO `pets` VALUES (174, 12, '小cat', 'cat', '狸花猫', '2023-01-06', '/statics/images/defaultPetAvatar.jpg', '2024-01-23 17:48:34', '2024-01-23 17:48:34');
INSERT INTO `pets` VALUES (175, 12, '小cat', 'cat', '缅因猫', '2020-08-20', '/statics/images/defaultPetAvatar.jpg', '2024-07-25 17:48:34', '2024-07-25 17:48:34');
INSERT INTO `pets` VALUES (176, 12, '聪明cat', 'cat', '橘猫', '2020-08-15', '/images/petAvatar/cat_8.jpg', '2024-09-12 17:48:34', '2024-09-12 17:48:34');
INSERT INTO `pets` VALUES (177, 12, '可爱cat', 'cat', '橘猫', '2024-03-26', '/images/petAvatar/cat_9.jpg', '2024-12-31 17:48:34', '2024-12-31 17:48:34');
INSERT INTO `pets` VALUES (178, 12, '聪明reptile', 'reptile', '守宫', '2023-05-25', '/images/petAvatar/reptile_9.jpg', '2024-08-19 17:48:34', '2024-08-19 17:48:34');
INSERT INTO `pets` VALUES (179, 12, '聪明hamster', 'hamster', '老公公', '2023-08-18', '/images/petAvatar/hamster_10.jpg', '2023-07-14 17:48:34', '2023-07-14 17:48:34');
INSERT INTO `pets` VALUES (180, 12, '可爱cat', 'cat', '橘猫', '2022-04-13', '/images/petAvatar/cat_6.jpg', '2023-11-01 17:48:34', '2023-11-01 17:48:34');
INSERT INTO `pets` VALUES (181, 12, '活泼rabbit', 'rabbit', '荷兰侏儒兔', '2023-07-01', '/images/petAvatar/rabbit_4.jpg', '2023-10-12 17:48:34', '2023-10-12 17:48:34');
INSERT INTO `pets` VALUES (182, 12, '霸气cat', 'cat', '橘猫', '2024-01-21', '/images/petAvatar/cat_8.jpg', '2023-05-28 17:48:34', '2023-05-28 17:48:34');
INSERT INTO `pets` VALUES (183, 12, '活泼other', 'other', '宠物猪', '2021-07-31', '/images/petAvatar/other_3.jpg', '2023-12-29 17:48:34', '2023-12-29 17:48:34');
INSERT INTO `pets` VALUES (184, 12, '可爱other', 'other', '蜜袋鼯', '2020-06-04', '/statics/images/defaultPetAvatar.jpg', '2024-02-24 17:48:34', '2024-02-24 17:48:34');
INSERT INTO `pets` VALUES (185, 12, '活泼dog', 'dog', '拉布拉多', '2021-04-02', '/statics/images/defaultPetAvatar.jpg', '2023-07-19 17:48:34', '2023-07-19 17:48:34');
INSERT INTO `pets` VALUES (186, 12, '小other', 'other', '宠物猪', '2020-12-02', '/images/petAvatar/other_6.jpg', '2023-08-20 17:48:34', '2023-08-20 17:48:34');
INSERT INTO `pets` VALUES (187, 12, '温柔fish', 'fish', '锦鲤', '2020-07-11', '/images/petAvatar/fish_9.jpg', '2024-04-25 17:48:34', '2024-04-25 17:48:34');
INSERT INTO `pets` VALUES (188, 12, '温柔other', 'other', '宠物猪', '2020-05-30', '/images/petAvatar/other_10.jpg', '2024-03-10 17:48:34', '2024-03-10 17:48:34');
INSERT INTO `pets` VALUES (189, 12, '温柔dog', 'dog', '柯基', '2024-01-11', '/images/petAvatar/dog_7.jpg', '2024-02-08 17:48:34', '2024-02-08 17:48:34');
INSERT INTO `pets` VALUES (190, 12, '温柔rabbit', 'rabbit', '垂耳兔', '2022-10-25', '/statics/images/defaultPetAvatar.jpg', '2024-07-17 17:48:34', '2024-07-17 17:48:34');
INSERT INTO `pets` VALUES (191, 12, '小reptile', 'reptile', '守宫', '2020-11-02', '/statics/images/defaultPetAvatar.jpg', '2024-05-17 17:48:34', '2024-05-17 17:48:34');
INSERT INTO `pets` VALUES (192, 12, '可爱cat', 'cat', '英短', '2022-04-29', '/images/petAvatar/cat_3.jpg', '2024-11-26 17:48:34', '2024-11-26 17:48:34');
INSERT INTO `pets` VALUES (193, 12, '调皮dog', 'dog', '哈士奇', '2021-10-29', '/images/petAvatar/dog_8.jpg', '2023-09-30 17:48:34', '2023-09-30 17:48:34');
INSERT INTO `pets` VALUES (194, 12, '活泼dog', 'dog', '边牧', '2023-11-17', '/images/petAvatar/dog_1.jpg', '2024-01-29 17:48:34', '2024-01-29 17:48:34');
INSERT INTO `pets` VALUES (195, 12, '温柔reptile', 'reptile', '玉米蛇', '2024-06-01', '/statics/images/defaultPetAvatar.jpg', '2024-05-31 17:48:34', '2024-05-31 17:48:34');
INSERT INTO `pets` VALUES (196, 12, '聪明other', 'other', '宠物猪', '2021-06-07', '/statics/images/defaultPetAvatar.jpg', '2024-07-14 17:48:34', '2024-07-14 17:48:34');
INSERT INTO `pets` VALUES (197, 12, '活泼dog', 'dog', '哈士奇', '2024-09-14', '/statics/images/defaultPetAvatar.jpg', '2023-10-12 17:48:34', '2023-10-12 17:48:34');
INSERT INTO `pets` VALUES (198, 12, '聪明cat', 'cat', '缅因猫', '2023-08-25', '/statics/images/defaultPetAvatar.jpg', '2023-07-15 17:48:34', '2023-07-15 17:48:34');
INSERT INTO `pets` VALUES (199, 12, '小cat', 'cat', '狸花猫', '2020-06-14', '/images/petAvatar/cat_1.jpg', '2023-09-07 17:48:34', '2023-09-07 17:48:34');
INSERT INTO `pets` VALUES (200, 12, '霸气rabbit', 'rabbit', '荷兰侏儒兔', '2020-12-14', '/images/petAvatar/rabbit_10.jpg', '2024-05-24 17:48:34', '2024-05-24 17:48:34');
INSERT INTO `pets` VALUES (201, 12, '可爱dog', 'dog', '泰迪', '2023-11-24', '/images/petAvatar/dog_3.jpg', '2023-08-15 17:48:34', '2023-08-15 17:48:34');
INSERT INTO `pets` VALUES (224, 12, '小bird', 'bird', '文鸟', '2023-12-06', '/statics/images/defaultPetAvatar.jpg', '2024-02-04 17:52:37', '2024-02-04 17:52:37');
INSERT INTO `pets` VALUES (225, 12, '调皮fish', 'fish', '锦鲤', '2021-12-28', '/images/petAvatar/fish_1.jpg', '2024-02-13 17:52:37', '2024-02-13 17:52:37');
INSERT INTO `pets` VALUES (226, 12, '调皮dog', 'dog', '泰迪', '2024-02-25', '/images/petAvatar/dog_9.jpg', '2024-03-16 17:52:37', '2024-03-16 17:52:37');
INSERT INTO `pets` VALUES (227, 12, '调皮dog', 'dog', '柯基', '2020-10-06', '/images/petAvatar/dog_3.jpg', '2023-06-05 17:52:37', '2023-06-05 17:52:37');
INSERT INTO `pets` VALUES (228, 12, '聪明cat', 'cat', '英短', '2024-02-05', '/images/petAvatar/cat_6.jpg', '2024-02-04 17:52:37', '2024-02-04 17:52:37');
INSERT INTO `pets` VALUES (229, 12, '小dog', 'dog', '柯基', '2020-07-20', '/images/petAvatar/dog_5.jpg', '2024-04-05 17:52:37', '2024-04-05 17:52:37');
INSERT INTO `pets` VALUES (230, 12, '调皮fish', 'fish', '锦鲤', '2023-03-07', '/statics/images/defaultPetAvatar.jpg', '2024-07-23 17:52:37', '2024-07-23 17:52:37');
INSERT INTO `pets` VALUES (231, 12, '调皮hamster', 'hamster', '金丝熊', '2022-01-24', '/statics/images/defaultPetAvatar.jpg', '2024-03-09 17:52:37', '2024-03-09 17:52:37');
INSERT INTO `pets` VALUES (232, 12, '可爱dog', 'dog', '柯基', '2024-11-08', '/images/petAvatar/dog_2.jpg', '2024-04-25 17:52:37', '2024-04-25 17:52:37');
INSERT INTO `pets` VALUES (233, 12, '温柔dog', 'dog', '柯基', '2024-05-22', '/images/petAvatar/dog_4.jpg', '2023-07-03 17:52:37', '2023-07-03 17:52:37');
INSERT INTO `pets` VALUES (234, 12, '温柔dog', 'dog', '边牧', '2024-11-25', '/images/petAvatar/dog_3.jpg', '2023-11-20 17:52:37', '2023-11-20 17:52:37');
INSERT INTO `pets` VALUES (235, 12, '可爱fish', 'fish', '金鱼', '2023-03-05', '/statics/images/defaultPetAvatar.jpg', '2023-08-03 17:52:37', '2023-08-03 17:52:37');
INSERT INTO `pets` VALUES (236, 12, '可爱fish', 'fish', '斗鱼', '2020-07-29', '/images/petAvatar/fish_1.jpg', '2024-09-27 17:52:37', '2024-09-27 17:52:37');
INSERT INTO `pets` VALUES (237, 12, '霸气hamster', 'hamster', '金丝熊', '2023-01-20', '/statics/images/defaultPetAvatar.jpg', '2024-10-15 17:52:37', '2024-10-15 17:52:37');
INSERT INTO `pets` VALUES (238, 12, '调皮other', 'other', '蜜袋鼯', '2024-02-10', '/images/petAvatar/other_7.jpg', '2023-07-31 17:52:37', '2023-07-31 17:52:37');
INSERT INTO `pets` VALUES (239, 12, '聪明cat', 'cat', '英短', '2023-12-02', '/images/petAvatar/cat_9.jpg', '2024-08-23 17:52:37', '2024-08-23 17:52:37');
INSERT INTO `pets` VALUES (240, 12, '活泼rabbit', 'rabbit', '安哥拉兔', '2023-01-24', '/images/petAvatar/rabbit_3.jpg', '2023-12-15 17:52:37', '2023-12-15 17:52:37');
INSERT INTO `pets` VALUES (241, 12, '霸气cat', 'cat', '布偶猫', '2020-06-22', '/statics/images/defaultPetAvatar.jpg', '2023-08-16 17:52:37', '2023-08-16 17:52:37');
INSERT INTO `pets` VALUES (242, 12, '温柔dog', 'dog', '哈士奇', '2021-12-16', '/statics/images/defaultPetAvatar.jpg', '2024-04-24 17:52:37', '2024-04-24 17:52:37');
INSERT INTO `pets` VALUES (243, 12, '活泼bird', 'bird', '八哥', '2023-11-23', '/images/petAvatar/bird_1.jpg', '2023-06-23 17:52:37', '2023-06-23 17:52:37');
INSERT INTO `pets` VALUES (244, 12, '温柔fish', 'fish', '龙鱼', '2021-02-03', '/statics/images/defaultPetAvatar.jpg', '2023-08-02 17:52:37', '2023-08-02 17:52:37');
INSERT INTO `pets` VALUES (245, 12, '聪明cat', 'cat', '橘猫', '2024-06-01', '/images/petAvatar/cat_5.jpg', '2023-12-24 17:52:37', '2023-12-24 17:52:37');
INSERT INTO `pets` VALUES (246, 12, '调皮rabbit', 'rabbit', '垂耳兔', '2020-06-03', '/images/petAvatar/rabbit_9.jpg', '2023-09-04 17:52:37', '2023-09-04 17:52:37');
INSERT INTO `pets` VALUES (247, 12, '聪明dog', 'dog', '金毛', '2022-04-12', '/images/petAvatar/dog_9.jpg', '2024-05-19 17:52:37', '2024-05-19 17:52:37');
INSERT INTO `pets` VALUES (248, 12, '活泼bird', 'bird', '鹦鹉', '2024-09-03', '/images/petAvatar/bird_5.jpg', '2024-09-16 17:52:37', '2024-09-16 17:52:37');
INSERT INTO `pets` VALUES (249, 12, '调皮dog', 'dog', '拉布拉多', '2024-10-27', '/images/petAvatar/dog_2.jpg', '2023-11-21 17:52:37', '2023-11-21 17:52:37');

-- ----------------------------
-- Table structure for product_category
-- ----------------------------
DROP TABLE IF EXISTS `product_category`;
CREATE TABLE `product_category`  (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `category_code` int NOT NULL COMMENT '分类编码',
  `category_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '分类名称',
  PRIMARY KEY (`category_id`) USING BTREE,
  UNIQUE INDEX `uk_category_code`(`category_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '产品分类表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of product_category
-- ----------------------------
INSERT INTO `product_category` VALUES (1, 1, '食物');
INSERT INTO `product_category` VALUES (2, 2, '玩具');
INSERT INTO `product_category` VALUES (5, 3, '服饰');
INSERT INTO `product_category` VALUES (6, 4, '健康');
INSERT INTO `product_category` VALUES (7, 5, '清洁');
INSERT INTO `product_category` VALUES (8, 6, '出行');
INSERT INTO `product_category` VALUES (9, 7, '智能');
INSERT INTO `product_category` VALUES (10, 8, '居住');
INSERT INTO `product_category` VALUES (11, 9, '训导');

-- ----------------------------
-- Table structure for products
-- ----------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products`  (
  `product_id` int NOT NULL AUTO_INCREMENT COMMENT '商品唯一ID',
  `product_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品名称',
  `category` int NULL DEFAULT 0 COMMENT '商品分类',
  `price` decimal(10, 2) NOT NULL COMMENT '商品单价',
  `stock` int NOT NULL DEFAULT 0 COMMENT '库存数量',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '商品详细描述',
  `main_image` varchar(9999) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '主图URL',
  `status` int NULL DEFAULT 0 COMMENT '商品状态 0下架 1上架',
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_at` timestamp(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`product_id`) USING BTREE,
  INDEX `idx_category`(`category`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  CONSTRAINT `products_ibfk_1` FOREIGN KEY (`category`) REFERENCES `product_category` (`category_code`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品信息表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of products
-- ----------------------------
INSERT INTO `products` VALUES (1, '优质天然猫粮 2kg装', 1, 150.00, 9999, '猫粮', '/images/product/7ddf05d9-c91b-4203-8844-3758273a8a1c.jpg;/images/product/12ffdd31-ba9a-41e7-a417-0e3af7df7dfa.jpg', 1, '2025-04-03 22:31:00', '2025-04-23 20:36:54');
INSERT INTO `products` VALUES (2, '猫咪玩具车自动逗猫棒自嗨解闷电动充电智能激光灯红外线宠物用品', 2, 38.00, 9999, '122', '/images/product/f55c9269-586e-4f4d-a124-d6deca6c6b3c.jpg;/images/product/308df7f8-41eb-49d4-96fa-3485f2def905.jpg', 1, '2025-04-04 00:23:33', '2025-04-23 20:36:54');
INSERT INTO `products` VALUES (4, '新款帅气宠物狗狗猫咪美式条纹T恤春夏季薄款雪纳瑞泰迪小型犬~潮', 3, 49.99, 9999, '蓝白色-棉织条纹【春夏薄款】', '/images/product/e1c5b067-3b15-4f8f-af05-ab9ffd6f94e8.jpg;/images/product/cd598064-487c-43c3-947e-20d9c286e364.jpg', 1, '2025-04-04 00:24:07', '2025-04-23 20:36:54');
INSERT INTO `products` VALUES (6, '幼犬专用狗粮 3kg装', 1, 100.00, 990, '高级', '/images/product/0dc5a953-2703-44d5-b6e4-ff24a45867e5.jpg;/images/product/b2111937-8bce-412c-bf6f-0831d4cc683e.jpg;/images/product/364b92ab-370d-42d8-b1c7-9ac78640cc17.jpg', 1, '2025-04-04 13:20:02', '2025-04-23 20:36:54');
INSERT INTO `products` VALUES (15, '麦德氏小分子鲨鱼软骨素狗狗专用泰迪金毛宠物狗狗关节补钙软骨素', 4, 128.00, 1000, '1g 【养护版】小分子软骨素340g', '/images/product/7f029bec-9283-4b9b-ba5b-01c33125abf3.jpg;/images/product/7632354d-23fb-41ee-9c86-41e69393dd8f.jpg;/images/product/59cf41f1-bc5c-4a91-a9cb-74dc41a65100.jpg;/images/product/6d0c3e51-5245-4082-a0cd-a8bf77428bb8.jpg', 1, '2025-04-05 21:09:44', '2025-04-23 20:36:54');
INSERT INTO `products` VALUES (16, '星宴猫粮全价风干粮成猫幼猫小猫营养高蛋白蓝猫布偶猫专用国产', 1, 158.00, 9999, '鸡肉+牛肉+鸭肉+三文鱼 1.5kg', '/images/product/2b0f61bb-568b-4619-b58e-00c053b7ac55.jpg', 1, '2025-04-11 14:10:37', '2025-04-23 20:36:54');
INSERT INTO `products` VALUES (17, '狗粮40斤装成犬幼型犬专用金毛拉布拉多边牧通用全价', 1, 115.00, 9999, '40斤大包装通用型（基础款无冻干）', '/images/product/9d1d2d74-0a56-4532-9052-ded624225f94.jpg', 1, '2025-04-11 14:22:40', '2025-04-23 20:36:54');
INSERT INTO `products` VALUES (18, '麦富迪狗粮barf霸弗生骨肉主食冻干粮泰迪比熊柯基通用成犬天然粮', 1, 109.00, 0, '【1.0经典款】成犬粮 -「鸡肉配方」2kg 【单包】', '/images/product/97371594-a3a4-494d-b146-a19b55a979fd.jpg', 1, '2025-04-11 14:26:03', '2025-04-23 20:36:54');
INSERT INTO `products` VALUES (19, '全犬通用狗粮10斤装冻干小型犬农村土狗专用成犬幼犬中大型犬', 1, 45.00, 9999, '6拼冻干粮10斤装【初乳蛋白球】更多客户首选', '/images/product/93b71c2e-ade3-40ed-b1f9-d5073ca5f579.jpg;/images/product/d216cc3b-8c05-4e8e-bb18-ddd8c9152c62.jpg', 1, '2025-04-11 14:30:02', '2025-05-12 18:05:45');
INSERT INTO `products` VALUES (20, '狗狗毛绒发声飞盘玩具自嗨解闷神器柯基消耗体力宠物耐咬拔河玩具', 2, 78.80, 9999, '【优惠组合】小狗飞盘+青蛙飞盘', '/images/product/05520aba-7e43-4185-9b32-f46dfe72e859.jpg', 1, '2025-04-12 13:34:13', '2025-04-23 20:36:54');
INSERT INTO `products` VALUES (21, '伸缩逗猫棒钢丝羽毛1.8m超长钓鱼竿耐咬带铃铛猫玩具解闷宠物用品', 2, 87.90, 9999, '四节伸缩杆+黑羽毛', '/images/product/0633731b-3ffe-4103-947e-7edffead26fd.jpg;/images/product/d704eed5-e9d2-49a7-b114-9dedeba693a1.jpg', 1, '2025-04-12 13:40:42', '2025-05-20 15:52:28');
INSERT INTO `products` VALUES (22, '小佩自动喂食器SOLO猫咪定时自动投食机猫粮狗粮智能宠物喂食机', 7, 269.00, 9999, '【性价比优选】-SOLO喂食器', '/images/product/ff8af37d-ffee-4d64-9af8-6df06510b5ac.jpg;/images/product/5816a9a4-a3ea-4159-884e-99dfa25b9c4f.jpg', 1, '2025-04-23 20:28:19', '2025-04-25 16:54:28');

-- ----------------------------
-- Table structure for user_addresses
-- ----------------------------
DROP TABLE IF EXISTS `user_addresses`;
CREATE TABLE `user_addresses`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `recipient_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人姓名',
  `recipient_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人手机号',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '省份',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '城市',
  `district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '区县',
  `detail_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '详细地址',
  `address_tag` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '家' COMMENT '地址标签',
  `is_default` tinyint(1) NULL DEFAULT 0 COMMENT '是否默认地址：0-否，1-是',
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_is_default`(`is_default`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户收货地址表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of user_addresses
-- ----------------------------
INSERT INTO `user_addresses` VALUES (1, 7, '张三', '18958648888', '四川省', '成都市', '金堂县', '白果街道', '学校', 1, '2025-05-07 10:44:43', '2025-05-07 13:27:25');
INSERT INTO `user_addresses` VALUES (3, 7, '李四', '18966666666', '四川省', '成都市', '双流区', '九江街道', '学校', 0, '2025-05-07 10:53:35', '2025-05-07 12:55:47');
INSERT INTO `user_addresses` VALUES (4, 7, '王五', '18966666666', '北京市', '北京市', '石景山区', 'xx景区', '景区', 0, '2025-05-07 12:56:04', '2025-05-08 08:54:58');
INSERT INTO `user_addresses` VALUES (5, 7, '小周', '18956567878', '四川省', '成都市', '锦江区', 'xx街道', '家', 0, '2025-05-07 13:26:57', '2025-05-08 08:54:11');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT '用户唯一标识',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '加密后的密码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '绑定邮箱',
  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '绑定手机号',
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像存储路径',
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `gender` int NOT NULL DEFAULT 2 COMMENT '性别  0男 1女 2保密',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地区',
  `bio` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '个人简介',
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '账户创建时间',
  `updated_at` timestamp(0) NULL DEFAULT NULL COMMENT '最后更新时间',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态 0禁用 1正常',
  PRIMARY KEY (`user_id`, `email`) USING BTREE,
  UNIQUE INDEX `unique_username`(`username`) USING BTREE,
  UNIQUE INDEX `unique_email`(`email`) USING BTREE,
  UNIQUE INDEX `unique_mobile`(`mobile`) USING BTREE,
  INDEX `idx_username`(`username`) USING BTREE,
  INDEX `idx_email`(`email`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户基本信息表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'test01', '$2a$10$4ygeK4eTLZYV1xqsB6s9Yeh2nQunWiZ.iXStTqcrhBhxy8PEaQp/S', 'john@pettest.com', '13800138000', '/images/default/defaultAvatar.jpg', 'test01', 0, NULL, NULL, NULL, '2025-03-15 17:50:32', '2025-05-16 10:55:43', 1);
INSERT INTO `users` VALUES (2, 'test02', '$2a$10$4ygeK4eTLZYV1xqsB6s9Yeh2nQunWiZ.iXStTqcrhBhxy8PEaQp/S', 'john@exampe.com', '1380013800', '/images/default/defaultAvatar.jpg', 'test02', 0, NULL, NULL, NULL, '2025-03-16 12:57:28', '2025-05-16 10:55:44', 1);
INSERT INTO `users` VALUES (3, 'test03', '$2a$10$4ygeK4eTLZYV1xqsB6s9Yeh2nQunWiZ.iXStTqcrhBhxy8PEaQp/S', 'john@qq.com', '13800138010', '/images/default/defaultAvatar.jpg', 'test03', 0, NULL, NULL, '这是我的个人简介', '2025-03-16 13:01:25', '2025-05-16 10:55:46', 1);
INSERT INTO `users` VALUES (4, 'test04', '$2a$10$4ygeK4eTLZYV1xqsB6s9Yeh2nQunWiZ.iXStTqcrhBhxy8PEaQp/S', 'aaa@qq.com', '18900000000', '/images/userAvatar/d7520107-29b3-44c3-8705-45b35c5db05a.jpg', 'test04', 2, NULL, NULL, '这是我的个人简介', '2025-03-18 13:49:02', '2025-05-16 20:14:59', 1);
INSERT INTO `users` VALUES (6, 'meng_1743684451454', '$2a$10$3vlIc1d.ScP.Bkf0TQ.qGeWLBrhEY0YLIhNxGEa61knmY9jK46gEm', '2179853438@qq.com', '345', '/images/default/defaultAvatar.jpg', '哈哈哈', 2, '2025-04-17', '北京市 - 北京市', '哈哈哈', '2025-04-03 20:47:31', '2025-05-16 10:55:48', 1);
INSERT INTO `users` VALUES (7, 'meng_1744901047662', '$2a$10$ac7sOZQeo/So05AVZXWJauVwedCCTYq93.meFiJJ8b9nFt7GblrSe', '2179853437@qq.com', NULL, '/images/userAvatar/9ba3c7d7-942a-4588-85c6-e1cd8cf00957.jpg', 'zhouyu', 0, '2005-09-02', '四川省 - 成都市', '你好，这是我的简介', '2025-04-17 22:44:08', '2025-04-27 11:46:32', 1);
INSERT INTO `users` VALUES (8, 'meng_1745306871452', '$2a$10$Glm38/ENoDJfONHC6BVcvO1KknPQi.7Kfx7l/gJ6c6JgJFx624CCy', '3205047387@qq.com', NULL, '/images/userAvatar/41d54ad8-8cd6-4b29-b252-55d8a183c66c.jpg', 'zhouyan', 1, '2003-07-09', '四川省 - 成都市', NULL, '2025-04-22 15:27:52', '2025-04-27 09:17:24', 1);
INSERT INTO `users` VALUES (9, 'meng_1745741191028', '$2a$10$UuNO3yuu2PaBKH8YcPyPGOMrYQd235AaMBefHp157o0sA4h6e1pG.', '3328276692@qq.com', NULL, '/images/default/defaultAvatar.jpg', '用户2FxjWWHPKxF', 2, NULL, NULL, NULL, '2025-04-27 16:06:31', '2025-05-16 10:55:51', 1);
INSERT INTO `users` VALUES (12, 'meng_1747638823273', '$2a$10$oUTSMDOAnpgfnEH0f4/ZaeRa8wnQzh.8GYJ5PbQ9UM4.8VSvy9E/K', '3559153356@qq.com', NULL, '/images/default/defaultAvatar.jpg', '用户frLwnkG1b56', 0, '2025-05-19', '海南省 - 海口市', '哈哈哈哈哈哈好', '2025-05-19 15:13:43', '2025-05-19 16:07:26', 1);
INSERT INTO `users` VALUES (14, 'meng_770487', '$2a$10$fakehashedpassword', 'meng_770487@example.com', '13714095506', '/images/default/defaultAvatar.jpg', 'meng_770487', 0, '1984-04-05', '福建省沈阳县', NULL, '2025-01-14 08:33:29', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (15, 'meng_809570', '$2a$10$fakehashedpassword', 'meng_809570@example.com', NULL, '/images/default/defaultAvatar.jpg', 'meng_809570', 2, '1967-10-12', '海南省建华市', '兔子专业户，垂耳兔太治愈了', '2025-04-22 22:43:16', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (16, 'meng_329258', '$2a$10$fakehashedpassword', 'meng_329258@example.com', '14492791434', '/images/default/defaultAvatar.jpg', 'meng_329258', 2, '1970-04-20', '香港特别行政区武汉县', '爬宠爱好者，守宫蜥蜴是我的伙伴', '2023-01-17 20:19:39', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (17, 'meng_539898', '$2a$10$fakehashedpassword', 'meng_539898@example.com', '14483595557', '/images/default/defaultAvatar.jpg', 'meng_539898', 0, '2006-07-05', '吉林省沈阳市', '动物行为训练师，专治各种不服', '2023-09-23 22:11:04', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (18, 'meng_391369', '$2a$10$fakehashedpassword', 'meng_391369@example.com', NULL, '/images/default/defaultAvatar.jpg', 'meng_391369', 0, '1964-12-27', '四川省婷婷市', '宠物烘焙达人，专做健康零食', '2024-11-14 15:42:20', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (19, 'meng_988662', '$2a$10$fakehashedpassword', 'meng_988662@example.com', NULL, '/images/default/defaultAvatar.jpg', 'meng_988662', 2, '1971-02-27', '四川省辽阳市', '宠物是我的家人', '2022-11-05 01:38:47', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (20, 'meng_496922', '$2a$10$fakehashedpassword', 'meng_496922@example.com', NULL, '/images/default/defaultAvatar.jpg', 'meng_496922', 2, '1989-06-05', '四川省辛集市', '爬宠爱好者，守宫蜥蜴是我的伙伴', '2022-12-07 12:45:09', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (21, 'meng_793384', '$2a$10$fakehashedpassword', 'meng_793384@example.com', '14457412307', '/images/default/defaultAvatar.jpg', 'meng_793384', 0, '1995-04-19', '台湾省石家庄市', '收养了3只流浪猫的爱心妈妈', '2024-11-07 18:43:40', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (22, 'meng_766563', '$2a$10$fakehashedpassword', 'meng_766563@example.com', '18369773664', '/images/default/defaultAvatar.jpg', 'meng_766563', 1, '1998-12-03', '辽宁省淑珍市', '专业遛狗师，微信步数永远第一', '2024-04-13 01:56:54', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (23, 'meng_835911', '$2a$10$fakehashedpassword', 'meng_835911@example.com', '18921693803', '/images/default/defaultAvatar.jpg', 'meng_835911', 2, '2006-08-14', '西藏自治区鑫县', '鱼缸造景师，打造水下乐园', '2020-12-11 13:41:05', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (24, 'meng_383060', '$2a$10$fakehashedpassword', 'meng_383060@example.com', '18945986313', '/images/default/defaultAvatar.jpg', 'meng_383060', 0, '1978-03-22', '河南省长沙市', NULL, '2021-06-19 15:59:23', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (25, 'meng_520651', '$2a$10$fakehashedpassword', 'meng_520651@example.com', '14720844456', '/images/default/defaultAvatar.jpg', 'meng_520651', 2, '2007-03-24', '吉林省娟县', '宠物美容师，让每个宝贝都美美哒', '2024-06-10 15:29:48', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (26, 'meng_514850', '$2a$10$fakehashedpassword', 'meng_514850@example.com', '18685173340', '/images/default/defaultAvatar.jpg', 'meng_514850', 1, '1999-06-10', '山东省阳县', '宠物医院护士，照顾生病的小天使们', '2022-11-24 06:22:20', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (27, 'meng_665158', '$2a$10$fakehashedpassword', 'meng_665158@example.com', '14680197543', '/images/default/defaultAvatar.jpg', 'meng_665158', 1, '1999-09-30', '江西省林县', NULL, '2025-04-30 20:24:51', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (28, 'meng_195325', '$2a$10$fakehashedpassword', 'meng_195325@example.com', '17817712796', '/images/default/defaultAvatar.jpg', 'meng_195325', 0, '2001-08-05', '云南省佛山市', '动物行为训练师，专治各种不服', '2022-05-20 11:30:09', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (29, 'meng_166613', '$2a$10$fakehashedpassword', 'meng_166613@example.com', '15472528668', '/images/default/defaultAvatar.jpg', 'meng_166613', 2, '1978-06-02', '河南省鑫县', '动物保护协会志愿者', '2020-06-20 23:34:53', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (30, 'meng_112038', '$2a$10$fakehashedpassword', 'meng_112038@example.com', '17328769797', '/images/default/defaultAvatar.jpg', 'meng_112038', 1, '1984-08-10', '北京市欣市', '异宠爱好者，蜘蛛蝎子都是宝贝', '2024-06-02 02:04:03', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (31, 'meng_265840', '$2a$10$fakehashedpassword', 'meng_265840@example.com', '15910535315', '/images/default/defaultAvatar.jpg', 'meng_265840', 2, '1967-08-25', '辽宁省乌鲁木齐市', '家有拆家小能手的二哈家长', '2022-01-23 16:56:14', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (32, 'meng_755674', '$2a$10$fakehashedpassword', 'meng_755674@example.com', '14993174258', '/images/default/defaultAvatar.jpg', 'meng_755674', 0, '1967-05-29', '吉林省呼和浩特县', '自制猫饭达人，研究科学喂养', '2024-11-23 12:25:53', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (33, 'meng_916449', '$2a$10$fakehashedpassword', 'meng_916449@example.com', NULL, '/images/default/defaultAvatar.jpg', 'meng_916449', 2, '1999-11-20', '澳门特别行政区兴城市', '宠物美容师，让每个宝贝都美美哒', '2021-02-05 23:31:53', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (34, 'meng_480612', '$2a$10$fakehashedpassword', 'meng_480612@example.com', '18660384923', '/images/default/defaultAvatar.jpg', 'meng_480612', 0, '1985-05-19', '香港特别行政区敏市', '鱼缸造景师，打造水下乐园', '2022-07-10 17:00:51', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (35, 'meng_189814', '$2a$10$fakehashedpassword', 'meng_189814@example.com', '17689622133', '/images/default/defaultAvatar.jpg', 'meng_189814', 2, '2004-12-15', '山西省桂荣市', '宠物医院护士，照顾生病的小天使们', '2021-01-31 02:18:20', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (36, 'meng_676510', '$2a$10$fakehashedpassword', 'meng_676510@example.com', '14053429645', '/images/default/defaultAvatar.jpg', 'meng_676510', 2, '1972-05-19', '贵州省辉县', '兔子专业户，垂耳兔太治愈了', '2020-12-05 03:09:14', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (37, 'meng_891952', '$2a$10$fakehashedpassword', 'meng_891952@example.com', '17642956107', '/images/default/defaultAvatar.jpg', 'meng_891952', 1, '1999-05-02', '重庆市宜都市', '自制猫饭达人，研究科学喂养', '2024-12-12 11:38:47', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (38, 'meng_573417', '$2a$10$fakehashedpassword', 'meng_573417@example.com', '13750614681', '/images/default/defaultAvatar.jpg', 'meng_573417', 0, '1971-03-22', '陕西省岩市', '异宠爱好者，蜘蛛蝎子都是宝贝', '2023-10-05 17:09:11', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (39, 'meng_341292', '$2a$10$fakehashedpassword', 'meng_341292@example.com', NULL, '/images/default/defaultAvatar.jpg', 'meng_341292', 0, '1970-10-27', '宁夏回族自治区西宁市', '动物沟通师，懂毛孩子的语言', '2024-12-28 20:50:02', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (40, 'meng_132938', '$2a$10$fakehashedpassword', 'meng_132938@example.com', '18564132160', '/images/default/defaultAvatar.jpg', 'meng_132938', 2, '1981-05-07', '新疆维吾尔自治区哈尔滨县', '鱼缸造景师，打造水下乐园', '2024-05-15 17:20:38', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (41, 'meng_324643', '$2a$10$fakehashedpassword', 'meng_324643@example.com', NULL, '/images/default/defaultAvatar.jpg', 'meng_324643', 0, '1968-12-26', '香港特别行政区华县', '专业铲屎官', '2022-03-26 10:34:08', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (42, 'meng_201639', '$2a$10$fakehashedpassword', 'meng_201639@example.com', '17280626804', '/images/default/defaultAvatar.jpg', 'meng_201639', 1, '1973-09-18', '山西省北镇县', '养布偶猫的奢侈品收藏家', '2020-06-29 20:36:37', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (43, 'meng_156802', '$2a$10$fakehashedpassword', 'meng_156802@example.com', '17326121993', '/images/default/defaultAvatar.jpg', 'meng_156802', 1, '2006-05-09', '山西省西宁县', '异宠爱好者，蜘蛛蝎子都是宝贝', '2024-05-31 14:00:26', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (44, 'meng_299448', '$2a$10$fakehashedpassword', 'meng_299448@example.com', '16483503296', '/images/default/defaultAvatar.jpg', 'meng_299448', 1, '1976-12-27', '湖北省婷县', '家有拆家小能手的二哈家长', '2022-04-24 05:36:53', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (45, 'meng_179046', '$2a$10$fakehashedpassword', 'meng_179046@example.com', '15826041828', '/images/default/defaultAvatar.jpg', 'meng_179046', 2, '1990-03-22', '香港特别行政区娟县', '家有两只喵主子的幸福铲屎官🐱', '2024-06-30 06:08:01', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (46, 'meng_989921', '$2a$10$fakehashedpassword', 'meng_989921@example.com', '14537247658', '/images/default/defaultAvatar.jpg', 'meng_989921', 1, '1970-05-20', '海南省辽阳市', '专业铲屎官', '2025-04-08 08:26:58', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (47, 'meng_161483', '$2a$10$fakehashedpassword', 'meng_161483@example.com', '14072091035', '/images/default/defaultAvatar.jpg', 'meng_161483', 1, '1964-06-01', '河南省通辽县', '动物保护协会志愿者', '2022-08-21 23:55:47', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (48, 'meng_830429', '$2a$10$fakehashedpassword', 'meng_830429@example.com', '17689733536', '/images/default/defaultAvatar.jpg', 'meng_830429', 0, '1992-05-08', '台湾省潮州市', '宠物殡葬师，送别每一个小天使', '2024-03-18 07:08:24', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (49, 'meng_707314', '$2a$10$fakehashedpassword', 'meng_707314@example.com', NULL, '/images/default/defaultAvatar.jpg', 'meng_707314', 2, '1991-11-12', '海南省杭州县', '宠物美容师，让每个宝贝都美美哒', '2023-06-17 01:19:34', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (50, 'meng_599948', '$2a$10$fakehashedpassword', 'meng_599948@example.com', '16297013579', '/images/default/defaultAvatar.jpg', 'meng_599948', 0, '1997-08-26', '广西壮族自治区天津市', '宠物心理疗愈师', '2022-04-14 12:18:25', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (51, 'meng_171849', '$2a$10$fakehashedpassword', 'meng_171849@example.com', '16821134853', '/images/default/defaultAvatar.jpg', 'meng_171849', 1, '1995-05-10', '吉林省桂芝县', '柯基的短腿守护者', '2020-06-02 20:53:30', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (52, 'meng_723398', '$2a$10$fakehashedpassword', 'meng_723398@example.com', '13223437868', '/images/default/defaultAvatar.jpg', 'meng_723398', 2, '2006-05-05', '澳门特别行政区永安县', '宠物美容师，让每个宝贝都美美哒', '2024-02-06 11:37:42', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (53, 'meng_850981', '$2a$10$fakehashedpassword', 'meng_850981@example.com', '15049105351', '/images/default/defaultAvatar.jpg', 'meng_850981', 1, '1969-10-29', '江苏省瑜县', '宠物医院护士，照顾生病的小天使们', '2024-02-26 17:27:47', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (54, 'meng_579434', '$2a$10$fakehashedpassword', 'meng_579434@example.com', '15021881152', '/images/default/defaultAvatar.jpg', 'meng_579434', 1, '1987-01-28', '山东省上海县', '收养了3只流浪猫的爱心妈妈', '2025-02-04 11:39:06', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (55, 'meng_630458', '$2a$10$fakehashedpassword', 'meng_630458@example.com', '14631706718', '/images/default/defaultAvatar.jpg', 'meng_630458', 0, '1997-06-22', '山西省玉珍市', '鱼缸造景师，打造水下乐园', '2023-03-09 03:58:21', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (56, 'meng_559469', '$2a$10$fakehashedpassword', 'meng_559469@example.com', '18399005956', '/images/default/defaultAvatar.jpg', 'meng_559469', 2, '1975-07-15', '上海市兴安盟市', '家有两只喵主子的幸福铲屎官🐱', '2025-05-15 10:57:07', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (57, 'meng_413921', '$2a$10$fakehashedpassword', 'meng_413921@example.com', '18926973200', '/images/default/defaultAvatar.jpg', 'meng_413921', 1, '1999-06-26', '山东省辉市', '柯基的短腿守护者', '2024-08-27 02:47:42', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (58, 'meng_262998', '$2a$10$fakehashedpassword', 'meng_262998@example.com', '14756164450', '/images/default/defaultAvatar.jpg', 'meng_262998', 2, '1994-09-24', '浙江省彬县', '异宠爱好者，蜘蛛蝎子都是宝贝', '2024-12-13 18:29:58', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (59, 'meng_994343', '$2a$10$fakehashedpassword', 'meng_994343@example.com', '14692809004', '/images/default/defaultAvatar.jpg', 'meng_994343', 1, '1997-03-10', '台湾省红梅县', '动物沟通师，懂毛孩子的语言', '2020-10-31 12:02:43', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (60, 'meng_969634', '$2a$10$fakehashedpassword', 'meng_969634@example.com', '14717221058', '/images/default/defaultAvatar.jpg', 'meng_969634', 1, '1987-05-03', '河北省云市', '宠物医院护士，照顾生病的小天使们', '2022-08-26 03:04:35', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (61, 'meng_269430', '$2a$10$fakehashedpassword', 'meng_269430@example.com', NULL, '/images/default/defaultAvatar.jpg', 'meng_269430', 2, '1985-09-27', '辽宁省阜新县', '家有两只喵主子的幸福铲屎官🐱', '2023-03-29 09:13:05', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (62, 'meng_824586', '$2a$10$fakehashedpassword', 'meng_824586@example.com', '18734429938', '/images/default/defaultAvatar.jpg', 'meng_824586', 0, '1991-04-08', '山西省济南县', '自制猫饭达人，研究科学喂养', '2022-07-05 20:32:16', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (63, 'meng_550664', '$2a$10$fakehashedpassword', 'meng_550664@example.com', '13816856050', '/images/default/defaultAvatar.jpg', 'meng_550664', 1, '1988-07-12', '重庆市深圳县', '猫狗双全的人生赢家', '2024-12-09 14:58:15', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (64, 'meng_361650', '$2a$10$fakehashedpassword', 'meng_361650@example.com', '17226846794', '/images/default/defaultAvatar.jpg', 'meng_361650', 2, '2001-03-07', '台湾省哈尔滨县', '养布偶猫的奢侈品收藏家', '2020-12-11 00:00:51', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (65, 'meng_348237', '$2a$10$fakehashedpassword', 'meng_348237@example.com', '18536623900', '/images/default/defaultAvatar.jpg', 'meng_348237', 1, '1990-08-19', '澳门特别行政区桂英县', '狗派家长，金毛的专属仆人', '2020-05-24 21:10:19', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (66, 'meng_448321', '$2a$10$fakehashedpassword', 'meng_448321@example.com', NULL, '/images/default/defaultAvatar.jpg', 'meng_448321', 1, '1996-03-08', '广西壮族自治区峰县', '动物行为训练师，专治各种不服', '2024-09-13 02:15:37', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (67, 'meng_140605', '$2a$10$fakehashedpassword', 'meng_140605@example.com', '18487114644', '/images/default/defaultAvatar.jpg', 'meng_140605', 0, '1986-04-11', '内蒙古自治区小红县', '宠物是我的家人', '2023-08-19 10:56:36', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (68, 'meng_933980', '$2a$10$fakehashedpassword', 'meng_933980@example.com', '18547284652', '/images/default/defaultAvatar.jpg', 'meng_933980', 0, '1993-01-01', '北京市颖县', '爬宠爱好者，守宫蜥蜴是我的伙伴', '2024-09-13 22:45:53', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (69, 'meng_172792', '$2a$10$fakehashedpassword', 'meng_172792@example.com', '17955736753', '/images/default/defaultAvatar.jpg', 'meng_172792', 2, '1990-09-20', '天津市涛县', '宠物心理疗愈师', '2021-11-11 20:22:04', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (70, 'meng_984642', '$2a$10$fakehashedpassword', 'meng_984642@example.com', '16464251452', '/images/default/defaultAvatar.jpg', 'meng_984642', 0, '1979-08-21', '黑龙江省建国县', '动物保护协会志愿者', '2024-04-29 13:35:38', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (71, 'meng_378361', '$2a$10$fakehashedpassword', 'meng_378361@example.com', '13227768119', '/images/default/defaultAvatar.jpg', 'meng_378361', 1, '1988-04-26', '上海市慧县', '宠物美容师，让每个宝贝都美美哒', '2022-01-28 06:35:58', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (72, 'meng_636265', '$2a$10$fakehashedpassword', 'meng_636265@example.com', '13773114114', '/images/default/defaultAvatar.jpg', 'meng_636265', 1, '1975-06-27', '天津市宁县', '猫狗双全的人生赢家', '2021-12-11 02:00:58', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (73, 'meng_645175', '$2a$10$fakehashedpassword', 'meng_645175@example.com', '18998214228', '/images/default/defaultAvatar.jpg', 'meng_645175', 1, '1969-06-03', '香港特别行政区荆门市', '兔子专业户，垂耳兔太治愈了', '2022-01-03 21:45:45', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (74, 'meng_446239', '$2a$10$fakehashedpassword', 'meng_446239@example.com', '16961433041', '/images/default/defaultAvatar.jpg', 'meng_446239', 2, '1995-08-20', '青海省乌鲁木齐县', '养了10年乌龟的佛系家长', '2024-08-07 20:58:19', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (75, 'meng_528231', '$2a$10$fakehashedpassword', 'meng_528231@example.com', '15075925844', '/images/default/defaultAvatar.jpg', 'meng_528231', 2, '1978-05-26', '澳门特别行政区丽娟县', '宠物医院护士，照顾生病的小天使们', '2023-05-29 05:59:11', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (76, 'meng_497562', '$2a$10$fakehashedpassword', 'meng_497562@example.com', '17338515930', '/images/default/defaultAvatar.jpg', 'meng_497562', 1, '2000-08-15', '香港特别行政区红市', '家有两只喵主子的幸福铲屎官🐱', '2025-01-18 19:06:52', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (77, 'meng_550770', '$2a$10$fakehashedpassword', 'meng_550770@example.com', '18062798618', '/images/default/defaultAvatar.jpg', 'meng_550770', 1, '1972-11-23', '河北省旭县', '爱宠人士，家有毛孩子', '2021-08-22 17:10:30', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (78, 'meng_596171', '$2a$10$fakehashedpassword', 'meng_596171@example.com', '18037802389', '/images/default/defaultAvatar.jpg', 'meng_596171', 1, '1982-01-18', '台湾省广州县', '宠物心理疗愈师', '2024-11-04 16:21:58', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (79, 'meng_451470', '$2a$10$fakehashedpassword', 'meng_451470@example.com', NULL, '/images/default/defaultAvatar.jpg', 'meng_451470', 0, '1987-09-03', '山西省六盘水市', '爬宠爱好者，守宫蜥蜴是我的伙伴', '2022-07-29 23:47:50', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (80, 'meng_356736', '$2a$10$fakehashedpassword', 'meng_356736@example.com', '16021938461', '/images/default/defaultAvatar.jpg', 'meng_356736', 1, '1970-05-24', '安徽省银川县', '爬宠爱好者，守宫蜥蜴是我的伙伴', '2021-05-21 00:19:54', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (81, 'meng_519066', '$2a$10$fakehashedpassword', 'meng_519066@example.com', NULL, '/images/default/defaultAvatar.jpg', 'meng_519066', 0, '1970-09-21', '四川省辉市', '兔子专业户，垂耳兔太治愈了', '2022-07-04 20:44:42', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (82, 'meng_829716', '$2a$10$fakehashedpassword', 'meng_829716@example.com', '16386111822', '/images/default/defaultAvatar.jpg', 'meng_829716', 2, '1988-06-28', '山东省旭县', '鱼缸造景师，打造水下乐园', '2022-05-25 03:24:45', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (83, 'meng_940346', '$2a$10$fakehashedpassword', 'meng_940346@example.com', '15997026198', '/images/default/defaultAvatar.jpg', 'meng_940346', 1, '2000-07-30', '黑龙江省宁德县', '宠物心理疗愈师', '2022-04-19 20:45:33', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (84, 'meng_674485', '$2a$10$fakehashedpassword', 'meng_674485@example.com', '15836078777', '/images/default/defaultAvatar.jpg', 'meng_674485', 1, '1991-06-09', '山西省浩县', '动物保护协会志愿者', '2022-03-10 11:58:55', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (85, 'meng_390782', '$2a$10$fakehashedpassword', 'meng_390782@example.com', '17995408939', '/images/default/defaultAvatar.jpg', 'meng_390782', 2, '1967-10-06', '河北省天津县', '鱼缸造景师，打造水下乐园', '2023-12-31 16:47:53', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (86, 'meng_848206', '$2a$10$fakehashedpassword', 'meng_848206@example.com', NULL, '/images/default/defaultAvatar.jpg', 'meng_848206', 1, '1990-02-11', '浙江省辛集县', '宠物美容师，让每个宝贝都美美哒', '2024-05-26 02:16:56', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (87, 'meng_258157', '$2a$10$fakehashedpassword', 'meng_258157@example.com', '14472753503', '/images/default/defaultAvatar.jpg', 'meng_258157', 2, '1967-12-02', '海南省瑞市', '宠物摄影师大咖，记录美好瞬间', '2023-04-18 03:02:07', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (88, 'meng_446954', '$2a$10$fakehashedpassword', 'meng_446954@example.com', '16486337812', '/images/default/defaultAvatar.jpg', 'meng_446954', 0, '1987-08-25', '黑龙江省沈阳市', '宠物摄影师大咖，记录美好瞬间', '2022-03-20 19:19:37', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (89, 'meng_907189', '$2a$10$fakehashedpassword', 'meng_907189@example.com', '16713207232', '/images/default/defaultAvatar.jpg', 'meng_907189', 1, '2001-01-18', '湖北省兴安盟市', '家有两只喵主子的幸福铲屎官🐱', '2024-03-06 08:18:09', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (90, 'meng_508930', '$2a$10$fakehashedpassword', 'meng_508930@example.com', '18478659818', '/images/default/defaultAvatar.jpg', 'meng_508930', 2, '1967-07-26', '山西省石家庄县', NULL, '2022-01-05 19:22:58', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (91, 'meng_557007', '$2a$10$fakehashedpassword', 'meng_557007@example.com', '16114757371', '/images/default/defaultAvatar.jpg', 'meng_557007', 1, '1986-01-23', '浙江省洋市', '救助流浪动物10年+', '2023-03-24 02:37:59', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (92, 'meng_233827', '$2a$10$fakehashedpassword', 'meng_233827@example.com', '16997511441', '/images/default/defaultAvatar.jpg', 'meng_233827', 1, '1968-08-22', '青海省志强县', '狗派家长，金毛的专属仆人', '2022-05-09 20:47:48', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (93, 'meng_242291', '$2a$10$fakehashedpassword', 'meng_242291@example.com', '18585643977', '/images/default/defaultAvatar.jpg', 'meng_242291', 0, '1973-02-15', '河北省海口市', '动物保护协会志愿者', '2022-08-26 20:29:33', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (94, 'meng_576786', '$2a$10$fakehashedpassword', 'meng_576786@example.com', '15065297211', '/images/default/defaultAvatar.jpg', 'meng_576786', 1, '1977-09-09', '西藏自治区嘉禾县', '养布偶猫的奢侈品收藏家', '2022-03-24 01:33:48', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (95, 'meng_593152', '$2a$10$fakehashedpassword', 'meng_593152@example.com', NULL, '/images/default/defaultAvatar.jpg', 'meng_593152', 1, '1982-12-10', '陕西省辉市', NULL, '2022-04-14 00:29:03', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (96, 'meng_783414', '$2a$10$fakehashedpassword', 'meng_783414@example.com', NULL, '/images/default/defaultAvatar.jpg', 'meng_783414', 0, '1973-01-06', '吉林省北镇县', '狗派家长，金毛的专属仆人', '2023-10-07 19:40:39', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (97, 'meng_232352', '$2a$10$fakehashedpassword', 'meng_232352@example.com', NULL, '/images/default/defaultAvatar.jpg', 'meng_232352', 1, '1987-06-24', '西藏自治区璐市', '动物保护协会志愿者', '2022-08-31 23:18:51', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (98, 'meng_736745', '$2a$10$fakehashedpassword', 'meng_736745@example.com', NULL, '/images/default/defaultAvatar.jpg', 'meng_736745', 1, '1996-10-04', '福建省淑英县', '收养了3只流浪猫的爱心妈妈', '2024-12-29 07:01:45', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (99, 'meng_427110', '$2a$10$fakehashedpassword', 'meng_427110@example.com', '16671497498', '/images/default/defaultAvatar.jpg', 'meng_427110', 2, '1981-12-09', '云南省秀英县', '爬宠爱好者，守宫蜥蜴是我的伙伴', '2020-07-16 12:08:05', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (100, 'meng_970917', '$2a$10$fakehashedpassword', 'meng_970917@example.com', '17049782669', '/images/default/defaultAvatar.jpg', 'meng_970917', 2, '1970-07-18', '江苏省晨县', '养了10年乌龟的佛系家长', '2023-11-18 13:08:31', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (101, 'meng_226935', '$2a$10$fakehashedpassword', 'meng_226935@example.com', '18016726688', '/images/default/defaultAvatar.jpg', 'meng_226935', 2, '1987-05-23', '广东省福州市', '兔子专业户，垂耳兔太治愈了', '2024-12-24 05:31:28', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (102, 'meng_630538', '$2a$10$fakehashedpassword', 'meng_630538@example.com', '17165901207', '/images/default/defaultAvatar.jpg', 'meng_630538', 1, '1976-06-08', '台湾省宜都市', '动物保护志愿者', '2021-08-30 02:52:23', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (103, 'meng_479781', '$2a$10$fakehashedpassword', 'meng_479781@example.com', '17085323506', '/images/default/defaultAvatar.jpg', 'meng_479781', 1, '1965-05-11', '吉林省宜都县', '家有拆家小能手的二哈家长', '2022-03-11 04:11:20', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (104, 'meng_383201', '$2a$10$fakehashedpassword', 'meng_383201@example.com', '16998178921', '/images/default/defaultAvatar.jpg', 'meng_383201', 1, '2005-01-11', '河北省建华县', '兔子专业户，垂耳兔太治愈了', '2023-05-18 00:20:50', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (105, 'meng_993085', '$2a$10$fakehashedpassword', 'meng_993085@example.com', '14524195569', '/images/default/defaultAvatar.jpg', 'meng_993085', 1, '2000-09-27', '天津市明县', '鱼缸造景师，打造水下乐园', '2025-03-31 18:41:17', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (106, 'meng_800646', '$2a$10$fakehashedpassword', 'meng_800646@example.com', '15465111470', '/images/default/defaultAvatar.jpg', 'meng_800646', 1, '1969-04-01', '西藏自治区潮州县', '宠物美容师，让每个宝贝都美美哒', '2021-06-04 22:33:15', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (107, 'meng_472060', '$2a$10$fakehashedpassword', 'meng_472060@example.com', NULL, '/images/default/defaultAvatar.jpg', 'meng_472060', 1, '1966-06-28', '广西壮族自治区石家庄县', '给主子画肖像的自由画师', '2021-06-03 07:51:16', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (108, 'meng_300337', '$2a$10$fakehashedpassword', 'meng_300337@example.com', '13549547658', '/images/default/defaultAvatar.jpg', 'meng_300337', 1, '2002-06-30', '天津市华县', '鱼缸造景师，打造水下乐园', '2022-01-18 21:40:44', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (109, 'meng_846420', '$2a$10$fakehashedpassword', 'meng_846420@example.com', '16183421282', '/images/default/defaultAvatar.jpg', 'meng_846420', 0, '1967-06-05', '福建省丽华市', '宠物殡葬师，送别每一个小天使', '2024-08-09 11:37:16', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (110, 'meng_355123', '$2a$10$fakehashedpassword', 'meng_355123@example.com', '14970468753', '/images/default/defaultAvatar.jpg', 'meng_355123', 2, '2003-02-02', '青海省石家庄市', '专业遛狗师，微信步数永远第一', '2023-05-06 00:18:28', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (111, 'meng_677124', '$2a$10$fakehashedpassword', 'meng_677124@example.com', '15167648434', '/images/default/defaultAvatar.jpg', 'meng_677124', 1, '1977-10-27', '西藏自治区春梅市', '养了10年乌龟的佛系家长', '2023-05-06 01:51:34', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (112, 'meng_856321', '$2a$10$fakehashedpassword', 'meng_856321@example.com', '14261692958', '/images/default/defaultAvatar.jpg', 'meng_856321', 2, '1997-10-07', '海南省乌鲁木齐市', '家有拆家小能手的二哈家长', '2020-12-14 15:49:56', '2025-05-20 18:26:20', 1);
INSERT INTO `users` VALUES (113, 'meng_607719', '$2a$10$fakehashedpassword', 'meng_607719@example.com', '14795955636', '/images/default/defaultAvatar.jpg', 'meng_607719', 0, '1966-11-13', '四川省沈阳市', '爬宠爱好者，守宫蜥蜴是我的伙伴', '2023-07-16 17:12:12', '2025-05-20 18:26:20', 1);

-- ----------------------------
-- Triggers structure for table products
-- ----------------------------
DROP TRIGGER IF EXISTS `products_update_timestamp`;
delimiter ;;
CREATE TRIGGER `products_update_timestamp` BEFORE UPDATE ON `products` FOR EACH ROW BEGIN
    SET NEW.updated_at = IFNULL(CURRENT_TIMESTAMP, NULL);
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table users
-- ----------------------------
DROP TRIGGER IF EXISTS `users_update_timestamp`;
delimiter ;;
CREATE TRIGGER `users_update_timestamp` BEFORE UPDATE ON `users` FOR EACH ROW BEGIN
    SET NEW.updated_at = IFNULL(CURRENT_TIMESTAMP, NULL);
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
