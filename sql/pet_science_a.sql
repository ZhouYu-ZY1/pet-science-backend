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

 Date: 24/05/2025 12:00:22
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
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '关注记录表' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 78 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单商品表' ROW_FORMAT = COMPACT;

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
) ENGINE = InnoDB AUTO_INCREMENT = 41 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单支付表' ROW_FORMAT = COMPACT;

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
) ENGINE = InnoDB AUTO_INCREMENT = 78 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单物流表' ROW_FORMAT = COMPACT;

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
) ENGINE = InnoDB AUTO_INCREMENT = 78 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单主表' ROW_FORMAT = COMPACT;

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
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户收货地址表' ROW_FORMAT = COMPACT;

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
) ENGINE = InnoDB AUTO_INCREMENT = 114 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户基本信息表' ROW_FORMAT = COMPACT;

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
