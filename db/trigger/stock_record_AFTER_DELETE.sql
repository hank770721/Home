DELIMITER $$
CREATE DEFINER=`root`@`localhost` TRIGGER `home`.`stock_record_AFTER_DELETE` AFTER DELETE ON `stock_record` FOR EACH ROW
BEGIN
	DECLARE i_count INT;
    DECLARE s_transMode_record CHAR(1);
    DECLARE d_quantity DECIMAL(4,0);
    DECLARE d_amount DECIMAL(5,0);
    DECLARE d_quantity_record DECIMAL(4,0);
    DECLARE d_amount_record DECIMAL(5,0);
    DECLARE d_quantity_treasury DECIMAL(4,0);
    DECLARE d_amount_treasury DECIMAL(5,0);
    DECLARE done INT DEFAULT true;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = false;
    
    -- stock_record
	DELETE bank_record FROM bank_record WHERE fromTable = 'stock_record' AND fromId = old.recordId;
    
    -- stock_treasury
    SET d_quantity = 0;
	SET d_amount = 0;
    
    BEGIN
		DECLARE cur_record CURSOR FOR
			SELECT transMode, quantity, amount
            FROM stock_record
            WHERE accountUserId = old.accountUserId
            AND accountId = old.accountId
            AND assetType = old.assetType
            AND stockId = old.stockId
            ORDER BY recordDate, recordId;
            
		OPEN cur_record;
		loop1: LOOP
			FETCH cur_record INTO s_transMode_record, d_quantity_record, d_amount_record;
			
			IF(done = false) THEN
				LEAVE loop1; 
			END IF;
            
            IF(s_transMode_record = '1') THEN
				SET d_quantity = d_quantity + d_quantity_record;
				SET d_amount = d_amount + d_amount_record;
			ELSEIF(s_transMode_record = '2') THEN
				SET d_quantity = d_quantity - d_quantity_record;
				SET d_amount = d_amount - d_amount_record;
			END IF;
		END LOOP;
		CLOSE cur_record;
        
        SELECT COUNT(1) INTO i_count 
			FROM stock_treasury 
            WHERE accountUserId = old.accountUserId 
            AND accountId = old.accountId 
            AND assetType = old.assetType 
            AND stockId = old.stockId;
        
        IF(i_count > 0) THEN
			DELETE stock_treasury
				FROM stock_treasury 
				WHERE accountUserId = old.accountUserId 
				AND accountId = old.accountId 
				AND assetType = old.assetType 
				AND stockId = old.stockId;
        END IF;
        
        IF(d_quantity > 0) THEN
			INSERT INTO stock_treasury(accountUserId, accountId, assetType, stockId, quantity, amount)
				VALUES(old.accountUserId, old.accountId, old.assetType, old.stockId, d_quantity, d_amount);
		END IF;
	END;
END$$
DELIMITER ;
