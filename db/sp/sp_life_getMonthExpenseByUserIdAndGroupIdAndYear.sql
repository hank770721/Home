DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_life_getMonthExpenseByUserIdAndGroupIdAndYear`(in_userId VARCHAR(12), in_groupId VARCHAR(4), in_year VARCHAR(4))
BEGIN
	SELECT LEFT(recordDate,6) AS month, SUM((CASE transMode WHEN '1' THEN amount WHEN '2' THEN amount * (-1) END)) AS amount
		FROM life_expense
        JOIN life_expense_accountgroupdetail accountgroupdetail ON accountgroupdetail.accountUserId = life_expense.accountUserId AND accountgroupdetail.accountId = life_expense.accountId
		WHERE accountgroupdetail.userId = in_userId
		AND accountgroupdetail.groupId = in_groupId
        AND LEFT(recordDate,4) = in_year
		GROUP BY LEFT(recordDate,6);
END$$
DELIMITER ;
