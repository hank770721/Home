DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_stock_income`(in_accountUserId VARCHAR(20), in_accountId VARCHAR(4))
BEGIN
	DROP TEMPORARY TABLE IF EXISTS `temp_Income`;
    DROP TEMPORARY TABLE IF EXISTS `temp_Income2`;
    
	CREATE TEMPORARY TABLE temp_Income(
		month VARCHAR(6),
		recordDate VARCHAR(10),
        name VARCHAR(9),
		income DECIMAL(6,0),
        dataType INT
	);
    
    INSERT INTO temp_Income(month, recordDate, name, income, dataType)
		SELECT left(stock_record.recordDate,6), concat(left(stock_record.recordDate,4),'/',substr(stock_record.recordDate,5,2),'/',right(stock_record.recordDate,2) ) AS recordDate, stock_profile.name, (amount - cost) AS income, 1
		FROM home.stock_record JOIN collect.stock_profile ON stock_profile.id = stock_record.stockId
		WHERE stock_record.accountUserId = in_accountUserId
        AND stock_record.accountId = in_accountId
        AND stock_record.transMode = '2'
        AND stock_record.assetType <> '3';
        
	CREATE TEMPORARY TABLE temp_Income2 LIKE temp_Income;
    INSERT INTO temp_Income2 SELECT * FROM temp_Income;
        
	INSERT INTO temp_Income(month, recordDate, name, income, dataType)
		SELECT month, '', '', ifnull(sum(income),0), 2
        FROM temp_Income2
        GROUP BY month;
        
	INSERT INTO temp_Income(month, recordDate, name, income, dataType)
		SELECT '', '', '', ifnull(sum(income),0), 3
        FROM temp_Income2;
        
	SELECT month, recordDate, name, format(income,0) AS income, dataType FROM temp_Income ORDER BY month DESC, dataType, recordDate DESC;
END$$
DELIMITER ;
