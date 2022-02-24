DELIMITER $$
CREATE DEFINER=`root`@`localhost` TRIGGER `home`.`stock_record_AFTER_INSERT` AFTER INSERT ON `stock_record` FOR EACH ROW
BEGIN
	DECLARE i_count INT;
    DECLARE d_quantity DECIMAL(4,0);
    DECLARE d_amount DECIMAL(5,0);
    DECLARE d_quantity_treasury DECIMAL(4,0);
    DECLARE d_amount_treasury DECIMAL(5,0);
    
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
END$$
DELIMITER ;
