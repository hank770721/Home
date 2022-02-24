CREATE TABLE `asset_goalcycle` (
  `accountUserId` varchar(40) DEFAULT NULL,
  `accountId` varchar(4) DEFAULT NULL,
  `id` int(11) NOT NULL,
  `month` int(2) DEFAULT NULL,
  `day` int(2) DEFAULT NULL,
  `hour` int(2) DEFAULT NULL,
  `minute` int(2) DEFAULT NULL,
  `second` int(2) DEFAULT NULL,
  `cycleQty` int(1) DEFAULT NULL,
  `cycleType` char(1) CHARACTER SET utf8mb4 DEFAULT NULL,
  `amount` decimal(5,0) DEFAULT NULL,
  `memo` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
