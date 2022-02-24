CREATE TABLE `user_account` (
  `userId` varchar(20) NOT NULL,
  `password` varchar(20) DEFAULT NULL,
  `userName` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
