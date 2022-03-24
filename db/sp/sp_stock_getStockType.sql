CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_stock_getStockType`(in_accountUserId VARCHAR(20), in_accountId VARCHAR(4), in_startMonth VARCHAR(6), in_endMonth VARCHAR(6))
BEGIN
	DECLARE i_count INT;
    DECLARE i_month INT;
    DECLARE dt_bankRecord DATETIME;
    DECLARE dt_stockRecord DATETIME;
    DECLARE dt_stockHistorical DATETIME;
    DECLARE dt_temp DATETIME;
    
	DECLARE s_id CHAR(1);
    DECLARE s_name VARCHAR(2);
	DECLARE done INT DEFAULT true;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = false;
    
    SET i_month = TIMESTAMPDIFF(MONTH,concat(in_startMonth,'01'), concat(in_endMonth,'01') );
	SELECT count(1) INTO i_count
		FROM (
			SELECT month
            FROM home.stock_getstocktype_temp
            WHERE accountUserId = in_accountUserId AND accountId = in_accountId AND month BETWEEN in_startMonth AND in_endMonth
            GROUP BY month
		) temp;
    
    SELECT max(enterDatetime) INTO dt_bankRecord 
		FROM home.bank_record 
        WHERE (
			(fromAccountUserId = in_accountUserId AND fromAccountId = in_accountId)
            OR (toAccountUserId = in_accountUserId AND toAccountId = in_accountId)
        );
        
    SELECT max(enterDatetime) INTO dt_stockRecord FROM home.stock_record WHERE accountUserId = in_accountUserId AND accountId = in_accountId;
    SELECT max(enterDatetime) INTO dt_stockHistorical FROM collect.stock_historical;
    /*
    IF (sysdate() > date_format(sysdate(),'%Y/%m/%d 14:05:00') ) THEN
		SET dt_stockHistorical = date_format(sysdate(),'%Y/%m/%d 14:05:00');
	ELSE
		SET dt_stockHistorical = date_format(date_add(sysdate(),interval -1 day),'%Y/%m/%d 14:05:00');
	END IF;
	*/
    SELECT max(enterDatetime) INTO dt_temp
		FROM home.stock_getstocktype_temp
        WHERE accountUserId = in_accountUserId
        AND accountId = in_accountId
        AND month BETWEEN in_startMonth AND in_endMonth;
    
    IF (i_count <> i_month + 1 OR dt_temp < dt_bankRecord OR dt_temp < dt_stockRecord OR dt_temp < dt_stockHistorical) THEN
		DELETE home.stock_getstocktype_temp FROM home.stock_getstocktype_temp WHERE accountUserId = in_accountUserId AND accountId = in_accountId;
    
		DROP TEMPORARY TABLE IF EXISTS `temp_Stock`;
		CREATE TEMPORARY TABLE temp_Stock(
			month VARCHAR(6),
			typeId CHAR(1),
			typeName VARCHAR(2),
			stock DECIMAL(8,0),
			cost DECIMAL(8,0),
			dividend DECIMAL(8,0),
			profit DECIMAL(8,0)
		);
		
		SET @month = in_startMonth;
		
		WHILE (@month <= in_endMonth) DO
			BEGIN
				DECLARE cur_Assettype CURSOR FOR SELECT id, name FROM home.stock_assettype WHERE accountUserId = in_accountUserId AND accountId = in_accountId ORDER BY id;
				
				OPEN cur_Assettype;
				loop1: LOOP
					FETCH cur_Assettype INTO s_id, s_name;
					
					IF (done = false) THEN
						LEAVE loop1; 
					END IF ;
					
					-- stock, cost, profit
					SELECT 	ifnull(sum(quantity * (SELECT stock_historical.closingPrice FROM collect.stock_historical WHERE stock_historical.stockId = record.stockId AND stock_historical.recordDate <= CONCAT(@month,'31') ORDER BY stock_historical.recordDate DESC LIMIT 1) ),0),
							ifnull(sum(cost),0) AS cost,
							ifnull(sum(profit),0) AS profit
						INTO @stock, @cost, @profit
						FROM (
							SELECT 	stockId,
									sum( (CASE transMode WHEN '1' THEN quantity WHEN '2' THEN quantity * (-1) END) ) AS quantity,
									sum( (CASE transMode WHEN '1' THEN amount WHEN '2' THEN cost * (-1) END) ) AS cost,
									sum( (CASE transMode WHEN '2' THEN amount - cost END) ) AS profit
							FROM home.stock_record
							WHERE accountUserId = in_accountUserId
                            AND accountId = in_accountId
							AND assetType = s_id
							AND left(recordDate,6) <= @month
							GROUP BY stockId
						) record;
					
					-- stockCost, dividend
					SELECT 	ifnull(sum( (CASE WHEN isDividend = '1' THEN 0 ELSE (CASE WHEN transMode = '2' THEN amount ELSE 0 END) END) ),0),
							ifnull(sum( (CASE WHEN isDividend = '1' THEN amount ELSE 0 END) ),0)
						INTO @stockCost, @dividend
						FROM home.bank_record
						WHERE (
							(fromAccountUserId = in_accountUserId AND fromAccountId = in_accountId) 
							OR (toAccountUserId = in_accountUserId AND toAccountId = in_accountId)
						)
						AND type = s_id
						AND left(recordDate,6) <= @month;
					
					INSERT INTO temp_Stock(month, typeId, typeName, stock, cost, dividend, profit)
						VALUES (@month, s_id, s_name, @stock, @cost, @dividend, @profit);
				END LOOP;
				CLOSE cur_Assettype;
			END;
			
			SET done = true;
			SET @month = date_format(date_add(concat(@month,'01'), INTERVAL 1 MONTH),'%Y%m');
		END WHILE;
		
        INSERT INTO home.stock_getstocktype_temp(accountUserId, accountId, month, typeId, typeName, stock, cost, dividend, profit, enterDatetime)
			SELECT in_accountUserId, in_accountId, month, typeId, typeName, stock, cost, dividend, profit, sysdate() FROM temp_Stock;
		
		DROP TABLE temp_Stock;
	END IF;
    
    SELECT month, typeId, typeName, stock, cost, dividend, profit 
		FROM home.stock_getstocktype_temp
        WHERE accountUserId = in_accountUserId
        AND accountId = in_accountId
        AND month BETWEEN in_startMonth AND in_endMonth
        ORDER BY month;
END