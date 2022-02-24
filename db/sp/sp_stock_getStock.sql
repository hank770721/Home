DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_stock_getStock`(in_startMonth VARCHAR(6), in_endMonth VARCHAR(6))
BEGIN
	DECLARE s_id CHAR(1);
    DECLARE s_name VARCHAR(2);
	DECLARE done INT DEFAULT true;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = false;
    
    DROP TEMPORARY TABLE IF EXISTS `temp_Stock`;
	CREATE TEMPORARY TABLE temp_Stock(
		month VARCHAR(6),
        stock DECIMAL(8,0),
		cost DECIMAL(8,0),
        dividend DECIMAL(8,0),
        profit DECIMAL(8,0)
	);
    
	SET @month = in_startMonth;
    
    WHILE (@month <= in_endMonth) DO
		-- stock
		SELECT 	ifnull(sum(quantity * (SELECT historical.closingPrice FROM collect.stock_historical historical WHERE historical.stockId = record.stockId AND left(historical.recordDate,6) <= @month ORDER BY recordDate DESC LIMIT 1) ),0),
				ifnull(sum(cost),0) AS cost,
				ifnull(sum(profit),0) AS profit
			INTO @stock, @cost, @profit
			FROM (
				SELECT 	stockId,
						sum( (CASE transMode WHEN '1' THEN quantity WHEN '2' THEN quantity * (-1) END) ) AS quantity,
						sum( (CASE transMode WHEN '1' THEN amount WHEN '2' THEN cost * (-1) END) ) AS cost,
						sum( (CASE transMode WHEN '2' THEN amount - cost END) ) AS profit
				FROM home.stock_record
				WHERE accountUserId = 'mia'
				AND accountId = '001'
				AND left(recordDate,6) <= @month
				GROUP BY stockId
			) record;
			
		-- stockCost, dividend
		SELECT 	ifnull(sum( (CASE WHEN isDividend = '1' THEN 0 ELSE (CASE WHEN transMode = '2' THEN amount ELSE 0 END) END) ),0),
				ifnull(sum( (CASE WHEN isDividend = '1' THEN amount ELSE 0 END) ),0)
			INTO @stockCost, @dividend
			FROM home.asset_record
			WHERE ( (fromAccountUserId = 'mia' AND fromAccountId = '001') OR (toAccountUserId = 'mia' AND toAccountId = '001') )
			AND left(recordDate,6) <= @month;
			
		INSERT INTO temp_Stock(month, stock, cost, dividend, profit)
			VALUES (@month, @stock, @cost, @dividend, @profit);
            
        SET @month = date_format(date_add(concat(@month,'01'), interval 1 MONTH),'%Y%m');
	END WHILE;
    
    SELECT month, stock, cost, dividend, profit FROM temp_Stock;
    
    DROP TABLE temp_Stock;
END$$
DELIMITER ;
