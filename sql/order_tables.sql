-- 订单主表
CREATE TABLE `orders` (
  `order_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `total_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '订单总金额',
  `status` varchar(20) NOT NULL COMMENT '订单状态：pending,paid,shipped,completed,cancelled',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `idx_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表';

-- 订单商品表
CREATE TABLE `order_items` (
  `order_item_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
  `order_id` int(11) NOT NULL COMMENT '订单ID',
  `product_id` int(11) NOT NULL COMMENT '商品ID',
  `product_name` varchar(100) NOT NULL COMMENT '商品名称',
  `product_image` varchar(255) DEFAULT NULL COMMENT '商品图片',
  `quantity` int(11) NOT NULL COMMENT '购买数量',
  `price` decimal(10,2) NOT NULL COMMENT '单价',
  `subtotal` decimal(10,2) NOT NULL COMMENT '小计金额',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`order_item_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品表';

-- 订单支付表
CREATE TABLE `order_payment` (
  `payment_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '支付ID',
  `order_id` int(11) NOT NULL COMMENT '订单ID',
  `payment_method` varchar(50) NOT NULL COMMENT '支付方式',
  `payment_amount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `payment_status` varchar(20) NOT NULL COMMENT '支付状态',
  `transaction_no` varchar(100) DEFAULT NULL COMMENT '交易号',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`payment_id`),
  UNIQUE KEY `idx_order_id` (`order_id`),
  KEY `idx_payment_status` (`payment_status`),
  KEY `idx_payment_time` (`payment_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单支付表';

-- 订单物流表
CREATE TABLE `order_shipping` (
  `shipping_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '物流ID',
  `order_id` int(11) NOT NULL COMMENT '订单ID',
  `address` varchar(255) NOT NULL COMMENT '收货地址',
  `receiver_name` varchar(50) NOT NULL COMMENT '收货人姓名',
  `receiver_mobile` varchar(20) NOT NULL COMMENT '收货人电话',
  `shipping_status` varchar(20) NOT NULL COMMENT '物流状态',
  `tracking_number` varchar(50) DEFAULT NULL COMMENT '运单号',
  `shipping_time` datetime DEFAULT NULL COMMENT '发货时间',
  `delivery_time` datetime DEFAULT NULL COMMENT '送达时间',
  `shipping_company` varchar(50) DEFAULT NULL COMMENT '物流公司',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`shipping_id`),
  UNIQUE KEY `idx_order_id` (`order_id`),
  KEY `idx_shipping_status` (`shipping_status`),
  KEY `idx_tracking_number` (`tracking_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单物流表';

-- 添加外键约束（如果需要）
ALTER TABLE `order_items` ADD CONSTRAINT `fk_order_items_order_id` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE;
ALTER TABLE `order_payment` ADD CONSTRAINT `fk_order_payment_order_id` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE;
ALTER TABLE `order_shipping` ADD CONSTRAINT `fk_order_shipping_order_id` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE;