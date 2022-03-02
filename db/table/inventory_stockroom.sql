CREATE TABLE `inventory_stockroom` (
  `userId` varchar(12) NOT NULL,
  `id` varchar(3) NOT NULL,
  `name` varchar(12) DEFAULT NULL,
  PRIMARY KEY (`userId`,`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
