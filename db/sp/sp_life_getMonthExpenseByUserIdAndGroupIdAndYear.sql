CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_life_getMonthExpenseByUserIdAndGroupIdAndYear`(in_userId VARCHAR(12), in_groupId VARCHAR(4), in_year VARCHAR(4))
BEGIN
	SELECT LEFT(life_expense.vestingDate,6) AS month, SUM((CASE life_expense.transMode WHEN '1' THEN life_expense.amount WHEN '2' THEN life_expense.amount * (-1) END)) AS amount
		FROM life_expense
        JOIN bank_accountgroupdetail accountgroupdetail ON accountgroupdetail.accountUserId = life_expense.accountUserId AND accountgroupdetail.accountId = life_expense.accountId
		WHERE accountgroupdetail.userId = in_userId
		AND accountgroupdetail.groupId = in_groupId
        AND LEFT(life_expense.vestingDate,4) = in_year
        GROUP BY LEFT(life_expense.vestingDate,6);
END