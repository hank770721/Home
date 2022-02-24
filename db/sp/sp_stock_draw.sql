DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_stock_draw`(in_accountUserId VARCHAR(20), in_accountId VARCHAR(4))
BEGIN
	DROP TEMPORARY TABLE IF EXISTS `temp_Draw`;
    DROP TEMPORARY TABLE IF EXISTS `temp_Draw2`;
    
	CREATE TEMPORARY TABLE temp_Draw(
		recordDate VARCHAR(10),
        name VARCHAR(9),
		income DECIMAL(6,0),
        dataType INT
	);
    
    INSERT INTO temp_Draw(recordDate, name, income, dataType)
		SELECT 	concat(left(stock_record.recordDate,4),'/',substr(stock_record.recordDate,5,2),'/',right(stock_record.recordDate,2) ) AS recordDate, 
				stock_profile.name, 
                (CASE WHEN stock_record.transMode = '1' THEN (-1) * stock_record.amount ELSE stock_record.amount - stock_record.cost END) AS income,
                1 AS dataType
		FROM home.stock_record LEFT JOIN collect.stock_profile ON stock_profile.id = stock_record.stockId
		WHERE stock_record.accountUserId = in_accountUserId
        AND stock_record.accountId = in_accountId
        -- 20211210以前只記錄未中籤手續費
        AND ( (stock_record.recordDate < '20211210' AND stock_record.transMode = '1' AND stock_record.quantity = 0) OR (stock_record.transMode = '2') )
        AND stock_record.assetType = '3';
        
	CREATE TEMPORARY TABLE temp_Draw2 LIKE temp_Draw;
    INSERT INTO temp_Draw2 SELECT * FROM temp_Draw;
        
	INSERT INTO temp_Draw(recordDate, name, income, dataType)
		SELECT '', '', ifnull(sum(income),0), 2
        FROM temp_Draw2;
        
	SELECT recordDate, name, format(income,0) AS income, dataType FROM temp_Draw ORDER BY dataType, recordDate DESC;
    
    DROP TABLE temp_Draw;
    DROP TABLE temp_Draw2;
END$$
DELIMITER ;
