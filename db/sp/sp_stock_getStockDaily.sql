DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_stock_getStockDaily`(in_accountUserId VARCHAR(20), in_accountId VARCHAR(4), in_endDate VARCHAR(8))
BEGIN
	DECLARE s_recordDate VARCHAR(8);
    DECLARE s_stockId VARCHAR(5);
    DECLARE s_assetType CHAR(1);
    DECLARE s_name VARCHAR(2);
    DECLARE done INT DEFAULT true;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = false;

	DROP TEMPORARY TABLE IF EXISTS `temp_StockDaily`;
	CREATE TEMPORARY TABLE temp_StockDaily(
		date VARCHAR(8),
        stockId VARCHAR(5),
        assetType CHAR(1),
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
				DECLARE cur_Stock CURSOR FOR 
					SELECT stockId, assetType FROM stock_record 
                    WHERE accountUserId = in_accountUserId AND accountId = in_accountId
                    GROUP BY stockId, assetType 
                    HAVING sum( (CASE WHEN transMode = '1' THEN quantity ELSE (-1) * quantity END) ) > 0;
                
                OPEN cur_Stock;
                loop2: LOOP
					FETCH cur_Stock INTO s_stockId, s_assetType;
					
					IF (done = false) THEN
						LEAVE loop2;
					END IF ;
				
					SELECT 	ifnull(sum(quantity * (SELECT historical.closingPrice FROM collect.stock_historical historical WHERE historical.stockId = record.stockId AND historical.recordDate = s_recordDate ORDER BY recordDate DESC LIMIT 1) ),0),
							ifnull(sum(cost),0)
						INTO @amount, @cost
						FROM (
							SELECT 	stockId,
									sum( (CASE transMode WHEN '1' THEN quantity WHEN '2' THEN quantity * (-1) END) ) AS quantity,
									sum( (CASE transMode WHEN '1' THEN amount WHEN '2' THEN cost * (-1) END) ) AS cost
							FROM stock_record
							WHERE accountUserId = in_accountUserId
							AND accountId = in_accountId
							AND stockId = s_stockId
                            AND assetType = s_assetType
							AND recordDate <= s_recordDate
							GROUP BY stockId
						) record;
					
					INSERT INTO temp_StockDaily(date, assetType, stockId, amount, cost)
						VALUES (s_recordDate, s_assetType, s_stockId, @amount, @cost);
				END LOOP;
				CLOSE cur_Stock;
			END;
        
			SET done = true;
		END LOOP;
		CLOSE cur_Date;
    END;
    
    SELECT temp_StockDaily.date, temp_StockDaily.assetType, temp_StockDaily.stockId, stock_profile.name AS stockName, temp_StockDaily.amount, temp_StockDaily.cost 
		FROM temp_StockDaily JOIN collect.stock_profile ON stock_profile.id = temp_StockDaily.stockId
        ORDER BY temp_StockDaily.date, temp_StockDaily.assetType, temp_StockDaily.stockId;
    
    DROP TABLE temp_StockDaily;
END$$
DELIMITER ;
