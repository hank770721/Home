DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_stock_getStockTypeDaily`(in_accountUserid VARCHAR(20), in_accountId VARCHAR(4), in_endDate VARCHAR(8))
BEGIN
    DECLARE i_dateCount INT;
    DECLARE i_tempDateCount INT;
    DECLARE i_differCount INT;
    DECLARE dt_stockRecord DATETIME;
    DECLARE dt_stockHistorical DATETIME;
    DECLARE dt_temp DATETIME;
    
	DECLARE s_recordDate VARCHAR(8);
    DECLARE s_id CHAR(1);
    DECLARE s_name VARCHAR(2);
    DECLARE done INT DEFAULT true;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = false;
    
    -- 天數
    SET i_dateCount = 30;
	SELECT count(1) INTO i_tempDateCount
		FROM (
			SELECT date
			FROM home.stock_getstocktypedaily_temp
            WHERE accountUserId = in_accountUserid
            AND accountId = in_accountId
            GROUP BY date
		) temp;
    
    -- 更新時間
    SELECT max(enterDatetime) INTO dt_stockRecord FROM home.stock_record WHERE accountUserId = in_accountUserid AND accountId = in_accountId;
    SELECT max(enterDatetime) INTO dt_stockHistorical FROM collect.stock_historical;
    /*
    IF (sysdate() > date_format(sysdate(),'%Y/%m/%d 14:05:00') ) THEN
		SET dt_stockHistorical = date_format(sysdate(),'%Y/%m/%d 14:05:00');
	ELSE
		SET dt_stockHistorical = date_format(date_add(sysdate(),interval -1 day),'%Y/%m/%d 14:05:00');
	END IF;
    */
    SELECT max(enterDatetime) INTO dt_temp FROM home.stock_getstocktypedaily_temp WHERE accountUserId = in_accountUserid AND accountId = in_accountId AND date <= in_endDate;
    
    -- 不同時間產生
    SELECT count(1) INTO i_differCount
		FROM (
			SELECT enterDatetime
            FROM home.stock_getstocktypedaily_temp
            WHERE accountUserId = in_accountUserid
            AND accountId = in_accountId
            GROUP BY enterDatetime
		) temp;
	
	IF (i_tempDateCount <> i_dateCount OR dt_temp < dt_stockRecord OR dt_temp < dt_stockHistorical OR i_differCount > 1) THEN
		DELETE stock_getstocktypedaily_temp FROM stock_getstocktypedaily_temp WHERE accountUserId = in_accountUserid AND accountId = in_accountId;
    
		DROP TEMPORARY TABLE IF EXISTS `temp_StockTypeDaily`;
		CREATE TEMPORARY TABLE temp_StockTypeDaily(
			date VARCHAR(8),
			typeId CHAR(1),
			amount DECIMAL(8,0),
			cost DECIMAL(8,0)
		);
		
		BEGIN
			DECLARE cur_Date CURSOR FOR SELECT recordDate FROM (SELECT recordDate FROM collect.stock_historical GROUP BY recordDate ORDER BY recordDate DESC LIMIT 30) stock_historical ORDER BY recordDate;
			
			OPEN cur_Date;
			loop1: LOOP
				FETCH cur_Date INTO s_recordDate;
				
				IF (done = false) THEN
					LEAVE loop1; 
				END IF ;
				
				BEGIN
					DECLARE cur_Assettype CURSOR FOR SELECT id, name FROM home.stock_assettype WHERE userId = in_accountUserid AND accountId = in_accountId ORDER BY id;
					
					OPEN cur_Assettype;
					loop2: LOOP
						FETCH cur_Assettype INTO s_id, s_name;
						
						IF (done = false) THEN
							LEAVE loop2;
						END IF ;
					
						SELECT 	ifnull(sum(quantity * (SELECT historical.closingPrice FROM collect.stock_historical historical WHERE historical.recordDate = s_recordDate AND historical.stockId = record.stockId ORDER BY recordDate DESC LIMIT 1) ),0),
								ifnull(sum(cost),0)
							INTO @amount, @cost
							FROM (
								SELECT 	stockId,
										sum( (CASE transMode WHEN '1' THEN quantity WHEN '2' THEN quantity * (-1) END) ) AS quantity,
										sum( (CASE transMode WHEN '1' THEN amount WHEN '2' THEN cost * (-1) END) ) AS cost
								FROM stock_record
								WHERE accountUserId = in_accountUserid
                                AND accountId = in_accountId
								AND assetType = s_id
								AND recordDate <= s_recordDate
								GROUP BY stockId
							) record;
						
						INSERT INTO temp_StockTypeDaily(date, typeId, amount, cost)
							VALUES (s_recordDate, s_id, @amount, @cost);
					END LOOP;
					CLOSE cur_Assettype;
				END;
			
				SET done = true;
			END LOOP;
			CLOSE cur_Date;
		END;
		
        INSERT INTO stock_getstocktypedaily_temp(accountUserId, accountId, date, typeId, amount, cost, enterDatetime)
			SELECT in_accountUserid, in_accountId, date, typeId, amount, cost, sysdate() FROM temp_StockTypeDaily;
		
		DROP TABLE temp_StockTypeDaily;
	END IF;
    
    SELECT date, typeId, amount, cost
		FROM home.stock_getstocktypedaily_temp WHERE accountUserId = in_accountUserid AND accountId = in_accountId
        ORDER BY date;
END$$
DELIMITER ;
