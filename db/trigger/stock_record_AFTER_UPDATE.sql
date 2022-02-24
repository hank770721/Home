DELIMITER $$
CREATE DEFINER=`root`@`localhost` TRIGGER `home`.`stock_record_AFTER_UPDATE` AFTER UPDATE ON `stock_record` FOR EACH ROW
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
    
	-- DELETE
	DELETE bank_record FROM bank_record WHERE fromTable = 'stock_record' AND fromId = old.recordId;
    
    -- INSERT
    IF (new.amount > 0) THEN
		SELECT name INTO @memo FROM collect.stock_profile WHERE stock_profile.id = new.stockId;
        
		SELECT MAX(recordId) INTO @recordId FROM bank_record WHERE recordDate = new.recordDate;
			
		IF (@recordId IS NULL) THEN
			SET @no = 0;
		ELSE
			SET @no = substring(@recordId, 9, 4);
		END IF;
		
		SET @no = @no + 1;
		SET @recordId = concat(new.recordDate, lpad(@no,4,0) );
			
		IF (new.transMode = '1') THEN    
			INSERT INTO bank_record(	recordId, recordDate, transMode, fromAccountUserId, fromAccountId,
										memo, amount, type, stockId, fromTable,
										fromId, enterUserId, enterDatetime, updateUserId, updateDatetime)
				VALUE(	@recordId, new.recordDate, '2', new.accountUserId, new.accountId,
						@memo, new.amount, new.assetType, new.stockId, 'stock_record',
                        new.recordId, new.enterUserId, new.enterDatetime, new.updateUserId, new.updateDatetime);
		ELSEIF (new.transMode = '2') THEN
			INSERT INTO bank_record(	recordId, recordDate, transMode, toAccountUserId, toAccountId,
										memo, amount, type, stockId, fromTable,
										fromId, enterUserId, enterDatetime, updateUserId, updateDatetime)
				VALUE(	@recordId, new.recordDate, '1', new.accountUserId, new.accountId,
						@memo, new.amount, new.assetType, new.stockId, 'stock_record',
                        new.recordId, new.enterUserId, new.enterDatetime, new.updateUserId, new.updateDatetime);
		END IF;
	END IF;
    
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
    
    IF(new.accountUserId <> old.accountUserId OR new.accountId <> old.accountId OR new.assetType <> old.assetType OR new.stockId <> old.stockId) THEN
		SELECT count(1) INTO i_count FROM stock_treasury WHERE accountUserId = new.accountUserId AND accountId = new.accountId AND assetType = new.assetType AND stockId = new.stockId;
    
		IF(i_count = 0) THEN
			SET d_quantity = 0;
			SET d_amount = 0;
			
			IF(new.transMode = '1') THEN
				SET d_quantity = new.quantity;
				SET d_amount = new.amount;
			ELSEIF(new.transMode = '2') THEN
				SET d_quantity = new.quantity * (-1);
				SET d_amount = new.amount * (-1);
			END IF;
			
			INSERT INTO stock_treasury(accountUserId, accountId, assetType, stockId, quantity, amount)
				VALUES(new.accountUserId, new.accountId, new.assetType, new.stockId, d_quantity, d_amount);
		ELSE
			SELECT quantity, amount INTO d_quantity_treasury, d_amount_treasury FROM stock_treasury WHERE accountUserId = new.accountUserId AND accountId = new.accountId AND assetType = new.assetType AND stockId = new.stockId;
			
			IF(new.transMode = '1') THEN
				SET d_quantity = d_quantity_treasury + new.quantity;
				SET d_amount = d_amount_treasury + new.amount;
			ELSEIF(new.transMode = '2') THEN
				SET d_quantity = d_quantity_treasury - new.quantity;
				SET d_amount = d_amount_treasury - new.amount;
			END IF;
			
			IF(d_quantity = 0) THEN
				DELETE stock_treasury FROM stock_treasury WHERE accountUserId = new.accountUserId AND accountId = new.accountId AND assetType = new.assetType AND stockId = new.stockId; 
			ELSE
				UPDATE stock_treasury 
					SET quantity = d_quantity,
						amount = d_amount
					WHERE accountUserId = new.accountUserId
					AND accountId = new.accountId
					AND assetType = new.assetType
					AND stockId = new.stockId;
			END IF;
		END IF;
    END IF;
END$$
DELIMITER ;
