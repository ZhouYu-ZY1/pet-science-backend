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

 Date: 21/04/2025 15:21:20
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
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '管理员表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, 'admin', '$2a$10$4ygeK4eTLZYV1xqsB6s9Yeh2nQunWiZ.iXStTqcrhBhxy8PEaQp/S', '系统管理员', 'admin@example.com', NULL, NULL, 1, '2025-04-10 17:12:14', '2025-04-03 20:50:02', '2025-04-03 20:50:02');

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
) ENGINE = InnoDB AUTO_INCREMENT = 56 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '关注记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of follows
-- ----------------------------
INSERT INTO `follows` VALUES (2, 7, 1, '2025-04-21 12:07:41');
INSERT INTO `follows` VALUES (4, 7, 2, '2025-04-21 12:23:51');
INSERT INTO `follows` VALUES (5, 7, 6, '2025-04-21 13:32:49');

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
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单商品表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of order_items
-- ----------------------------
INSERT INTO `order_items` VALUES (1, 1, 15, '健康', '/statics/images/upload/7632354d-23fb-41ee-9c86-41e69393dd8f.jpg;/statics/images/upload/59cf41f1-bc5c-4a91-a9cb-74dc41a65100.jpg', 2, 100.00, 200.00, '2025-04-05 22:58:54', '2025-04-05 22:58:54');
INSERT INTO `order_items` VALUES (2, 2, 1, '猫粮', '/statics/images/upload/12ffdd31-ba9a-41e7-a417-0e3af7df7dfa.jpg', 10, 100.00, 1000.00, '2025-04-06 20:16:50', '2025-04-06 20:16:50');
INSERT INTO `order_items` VALUES (3, 3, 1, '猫粮', '/statics/images/upload/12ffdd31-ba9a-41e7-a417-0e3af7df7dfa.jpg', 10, 100.00, 1000.00, '2025-04-10 10:08:34', '2025-04-10 10:08:34');
INSERT INTO `order_items` VALUES (6, 6, 1, '猫粮', '/statics/images/upload/12ffdd31-ba9a-41e7-a417-0e3af7df7dfa.jpg', 10, 100.00, 1000.00, '2025-04-10 10:17:19', '2025-04-10 10:17:19');
INSERT INTO `order_items` VALUES (7, 7, 1, '猫粮', '/statics/images/upload/12ffdd31-ba9a-41e7-a417-0e3af7df7dfa.jpg', 10, 100.00, 1000.00, '2025-04-10 10:19:06', '2025-04-10 10:19:06');
INSERT INTO `order_items` VALUES (14, 14, 1, '猫粮', '/statics/images/upload/12ffdd31-ba9a-41e7-a417-0e3af7df7dfa.jpg', 10, 150.00, 1500.00, '2025-04-10 11:18:27', '2025-04-10 11:18:27');

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
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单支付表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of order_payment
-- ----------------------------
INSERT INTO `order_payment` VALUES (1, 1, 'zfb', 200.00, 'success', '666666', '2025-04-05 23:39:11', '2025-04-05 23:39:11', '2025-04-05 23:39:11');
INSERT INTO `order_payment` VALUES (3, 2, 'wx', 1000.00, 'success', NULL, '2025-04-10 10:59:28', '2025-04-10 10:59:28', '2025-04-10 10:59:28');
INSERT INTO `order_payment` VALUES (4, 14, 'wx', 1500.00, 'success', NULL, '2025-04-10 11:19:12', '2025-04-10 11:19:12', '2025-04-10 11:19:12');

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
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单物流表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of order_shipping
-- ----------------------------
INSERT INTO `order_shipping` VALUES (1, 1, '上海', '张三', '18917181111', 'shipped', 'SF88888888', '2025-04-06 18:28:00', NULL, 'SF', '2025-04-05 22:58:54', '2025-04-06 18:28:00');
INSERT INTO `order_shipping` VALUES (2, 2, '四川成都金堂县白果街道', '李四', '18957390912', 'pending', '', NULL, NULL, '', '2025-04-06 20:16:50', '2025-04-06 20:16:50');
INSERT INTO `order_shipping` VALUES (3, 3, '四川成都金堂县白果街道', '李四', '18957390912', 'pending', '', NULL, NULL, '', '2025-04-10 10:08:34', '2025-04-10 10:08:34');
INSERT INTO `order_shipping` VALUES (6, 6, '四川成都金堂县白果街道', '李四', '18957390912', 'pending', '', NULL, NULL, '', '2025-04-10 10:17:19', '2025-04-10 10:17:19');
INSERT INTO `order_shipping` VALUES (7, 7, '四川成都金堂县白果街道', '李四', '18957390912', 'pending', '', NULL, NULL, '', '2025-04-10 10:19:06', '2025-04-10 10:19:06');
INSERT INTO `order_shipping` VALUES (14, 14, '四川成都金堂县白果街道', '李四', '18957390912', 'pending', '', NULL, NULL, '', '2025-04-10 11:18:27', '2025-04-10 11:18:27');

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
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单主表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, 6, 'ORD202504051ff172', 200.00, 'shipped', '备注。。。', '2025-04-05 22:58:54', '2025-04-06 18:28:00');
INSERT INTO `orders` VALUES (2, 4, 'ORD202504067928b9', 1000.00, 'paid', '10份猫粮', '2025-04-06 20:16:50', '2025-04-10 10:59:28');
INSERT INTO `orders` VALUES (3, 4, 'ORD20250410034c62', 1000.00, 'cancelled', '10份猫粮', '2025-04-10 10:08:34', '2025-04-10 10:09:34');
INSERT INTO `orders` VALUES (6, 4, 'ORD20250410cc682f', 1000.00, 'cancelled', '10份猫粮', '2025-04-10 10:17:19', '2025-04-10 10:18:19');
INSERT INTO `orders` VALUES (7, 4, 'ORD20250410f92650', 1000.00, 'cancelled', '10份猫粮', '2025-04-10 10:19:06', '2025-04-10 10:20:06');
INSERT INTO `orders` VALUES (14, 4, 'ORD2025041041303b', 1500.00, 'paid', '10份猫粮', '2025-04-10 11:18:27', '2025-04-10 11:19:12');

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
-- Table structure for product_images
-- ----------------------------
DROP TABLE IF EXISTS `product_images`;
CREATE TABLE `product_images`  (
  `image_id` int NOT NULL AUTO_INCREMENT COMMENT '图片ID',
  `product_id` int NOT NULL COMMENT '关联商品ID',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '图片URL',
  `sort_order` tinyint NULL DEFAULT 1 COMMENT '图片排序',
  PRIMARY KEY (`image_id`) USING BTREE,
  INDEX `product_id`(`product_id`) USING BTREE,
  CONSTRAINT `product_images_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品图片表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of product_images
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品信息表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of products
-- ----------------------------
INSERT INTO `products` VALUES (1, '优质天然猫粮 2kg装', 1, 150.00, 9999, '猫粮', '/statics/images/upload/7ddf05d9-c91b-4203-8844-3758273a8a1c.jpg;/statics/images/upload/12ffdd31-ba9a-41e7-a417-0e3af7df7dfa.jpg', 1, '2025-04-03 22:31:00', '2025-04-15 10:53:56');
INSERT INTO `products` VALUES (2, '猫咪玩具车自动逗猫棒自嗨解闷电动充电智能激光灯红外线宠物用品', 2, 38.00, 9999, '122', '/statics/images/upload/f55c9269-586e-4f4d-a124-d6deca6c6b3c.jpg;/statics/images/upload/308df7f8-41eb-49d4-96fa-3485f2def905.jpg', 1, '2025-04-04 00:23:33', '2025-04-12 14:10:13');
INSERT INTO `products` VALUES (4, '新款帅气宠物狗狗猫咪美式条纹T恤春夏季薄款雪纳瑞泰迪小型犬~潮', 3, 49.99, 9999, '蓝白色-棉织条纹【春夏薄款】', '/statics/images/upload/e1c5b067-3b15-4f8f-af05-ab9ffd6f94e8.jpg;/statics/images/upload/cd598064-487c-43c3-947e-20d9c286e364.jpg', 1, '2025-04-04 00:24:07', '2025-04-21 13:47:13');
INSERT INTO `products` VALUES (6, '幼犬专用狗粮 3kg装', 1, 100.00, 990, '高级', '/statics/images/upload/0dc5a953-2703-44d5-b6e4-ff24a45867e5.jpg;/statics/images/upload/b2111937-8bce-412c-bf6f-0831d4cc683e.jpg;/statics/images/upload/364b92ab-370d-42d8-b1c7-9ac78640cc17.jpg', 1, '2025-04-04 13:20:02', '2025-04-15 10:54:18');
INSERT INTO `products` VALUES (15, '麦德氏小分子鲨鱼软骨素狗狗专用泰迪金毛宠物狗狗关节补钙软骨素', 4, 128.00, 1000, '1g 【养护版】小分子软骨素340g', '/statics/images/upload/7f029bec-9283-4b9b-ba5b-01c33125abf3.jpg;/statics/images/upload/7632354d-23fb-41ee-9c86-41e69393dd8f.jpg;/statics/images/upload/59cf41f1-bc5c-4a91-a9cb-74dc41a65100.jpg', 1, '2025-04-05 21:09:44', '2025-04-21 13:58:04');
INSERT INTO `products` VALUES (16, '星宴猫粮全价风干粮成猫幼猫小猫营养高蛋白蓝猫布偶猫专用国产', 1, 158.00, 9999, '鸡肉+牛肉+鸭肉+三文鱼 1.5kg', '/statics/images/upload/2b0f61bb-568b-4619-b58e-00c053b7ac55.jpg', 1, '2025-04-11 14:10:37', '2025-04-11 14:18:46');
INSERT INTO `products` VALUES (17, '狗粮40斤装成犬幼型犬专用金毛拉布拉多边牧通用全价', 1, 115.00, 9999, '40斤大包装通用型（基础款无冻干）', '/statics/images/upload/9d1d2d74-0a56-4532-9052-ded624225f94.jpg', 1, '2025-04-11 14:22:40', '2025-04-11 14:22:40');
INSERT INTO `products` VALUES (18, '麦富迪狗粮barf霸弗生骨肉主食冻干粮泰迪比熊柯基通用成犬天然粮', 1, 109.00, 0, '【1.0经典款】成犬粮 -「鸡肉配方」2kg 【单包】', '/statics/images/upload/97371594-a3a4-494d-b146-a19b55a979fd.jpg', 1, '2025-04-11 14:26:03', '2025-04-12 13:34:41');
INSERT INTO `products` VALUES (19, '全犬通用狗粮10斤装冻干小型犬农村土狗专用成犬幼犬中大型犬', 1, 45.00, 9999, '6拼冻干粮10斤装【初乳蛋白球】更多客户首选', '/statics/images/upload/93b71c2e-ade3-40ed-b1f9-d5073ca5f579.jpg', 1, '2025-04-11 14:30:02', '2025-04-15 15:11:46');
INSERT INTO `products` VALUES (20, '狗狗毛绒发声飞盘玩具自嗨解闷神器柯基消耗体力宠物耐咬拔河玩具', 2, 78.80, 9999, '【优惠组合】小狗飞盘+青蛙飞盘', '/statics/images/upload/05520aba-7e43-4185-9b32-f46dfe72e859.jpg', 1, '2025-04-12 13:34:13', '2025-04-13 21:34:24');
INSERT INTO `products` VALUES (21, '伸缩逗猫棒钢丝羽毛1.8m超长钓鱼竿耐咬带铃铛猫玩具解闷宠物用品', 2, 87.90, 0, '四节伸缩杆+黑羽毛', '/statics/images/upload/123b0694-de08-46ec-9af6-eff0d1a544a7.jpg', 1, '2025-04-12 13:40:42', '2025-04-16 11:05:01');

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
  `gender` int NULL DEFAULT 2 COMMENT '性别  0男 1女 2保密',
  `birthday` timestamp(0) NULL DEFAULT NULL COMMENT '生日',
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地区',
  `bio` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '个人简介',
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '账户创建时间',
  `updated_at` timestamp(0) NULL DEFAULT NULL COMMENT '最后更新时间',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态 0禁用 2正常',
  PRIMARY KEY (`user_id`, `email`) USING BTREE,
  UNIQUE INDEX `unique_username`(`username`) USING BTREE,
  UNIQUE INDEX `unique_email`(`email`) USING BTREE,
  UNIQUE INDEX `unique_mobile`(`mobile`) USING BTREE,
  INDEX `idx_username`(`username`) USING BTREE,
  INDEX `idx_email`(`email`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户基本信息表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'test01', '$2a$10$4ygeK4eTLZYV1xqsB6s9Yeh2nQunWiZ.iXStTqcrhBhxy8PEaQp/S', 'john@pettest.com', '13800138000', '/statics/images/defaultAvatar.jpg', 'test01', 0, NULL, NULL, NULL, '2025-03-15 17:50:32', '2025-04-18 14:13:32', 1);
INSERT INTO `users` VALUES (2, 'test02', '$2a$10$4ygeK4eTLZYV1xqsB6s9Yeh2nQunWiZ.iXStTqcrhBhxy8PEaQp/S', 'john@exampe.com', '1380013800', '/statics/images/defaultAvatar.jpg', 'test02', 0, NULL, NULL, NULL, '2025-03-16 12:57:28', '2025-04-18 14:13:38', 1);
INSERT INTO `users` VALUES (3, 'test03', '$2a$10$4ygeK4eTLZYV1xqsB6s9Yeh2nQunWiZ.iXStTqcrhBhxy8PEaQp/S', 'john@qq.com', '13800138010', '/statics/images/defaultAvatar.jpg', 'test03', 0, NULL, NULL, '这是我的个人简介', '2025-03-16 13:01:25', '2025-04-18 14:13:41', 1);
INSERT INTO `users` VALUES (4, 'test04', '$2a$10$4ygeK4eTLZYV1xqsB6s9Yeh2nQunWiZ.iXStTqcrhBhxy8PEaQp/S', 'aaa@qq.com', '18900000000', '/statics/images/defaultAvatar.jpg', 'test04', 0, NULL, NULL, '这是我的个人简介', '2025-03-18 13:49:02', '2025-04-18 14:13:44', 1);
INSERT INTO `users` VALUES (6, 'meng_1743684451454', '$2a$10$3vlIc1d.ScP.Bkf0TQ.qGeWLBrhEY0YLIhNxGEa61knmY9jK46gEm', '2179853438@qq.com', '345', '/statics/images/defaultAvatar.jpg', '哈哈哈', 2, '2025-04-17 08:00:00', '北京市 - 北京市', '哈哈哈', '2025-04-03 20:47:31', '2025-04-21 13:26:52', 1);
INSERT INTO `users` VALUES (7, 'meng_1744901047662', '$2a$10$ac7sOZQeo/So05AVZXWJauVwedCCTYq93.meFiJJ8b9nFt7GblrSe', '2179853437@qq.com', NULL, '/statics/images/defaultAvatar.jpg', '嘿嘿嘿', 1, '2025-04-17 08:00:00', '四川省 - 成都市', '你好我是xxx', '2025-04-17 22:44:08', '2025-04-21 13:26:58', 1);

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
