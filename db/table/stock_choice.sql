CREATE TABLE `stock_choice` (
  `stockId` varchar(5) NOT NULL,
  `recordDate` varchar(8) DEFAULT NULL,
  `targetDate` varchar(8) DEFAULT NULL,
  `targetPrice` decimal(6,2) DEFAULT NULL,
  PRIMARY KEY (`stockId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
