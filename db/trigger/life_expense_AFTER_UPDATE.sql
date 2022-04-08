CREATE DEFINER=`root`@`localhost` TRIGGER `life_expense_AFTER_UPDATE` AFTER UPDATE ON `life_expense` FOR EACH ROW BEGIN
	DECLARE s_isBankAccount_old CHAR(1);
    DECLARE s_isBankAccount_new CHAR(1);
    
	SELECT isBankAccount INTO s_isBankAccount_old FROM bank_account WHERE userId = old.accountUserId AND id = old.accountId;
    
    SELECT isBankAccount INTO s_isBankAccount_new FROM bank_account WHERE userId = new.accountUserId AND id = new.accountId;
    
    IF (s_isBankAccount_old = '1' AND s_isBankAccount_new = '1') THEN
		IF (new.transMode = '1') THEN
			UPDATE bank_record 
				SET recordDate = new.recordDate,
					transMode = '1',
                    fromAccountUserId = NULL,
                    fromAccountId = NULL,
                    toAccountUserId = new.accountUserId,
                    toAccountId = new.accountId,
					memo = new.memo,
                    amount = new.amount,
                    fromId = new.recordId,
                    enterUserId = new.enterUserId,
                    enterDatetime = new.enterDatetime,
                    updateUserId = new.updateUserId,
                    updateDatetime = new.updateDatetime
				WHERE fromTable = 'life_expense'
                AND fromId = old.recordId;
		ELSEIF (new.transMode = '2') THEN
			UPDATE bank_record 
				SET recordDate = new.recordDate,
					transMode = '2',
                    fromAccountUserId = new.accountUserId,
                    fromAccountId = new.accountId,
                    toAccountUserId = NULL,
                    toAccountId = NULL,
					memo = new.memo,
                    amount = new.amount,
                    fromId = new.recordId,
                    enterUserId = new.enterUserId,
                    enterDatetime = new.enterDatetime,
                    updateUserId = new.updateUserId,
                    updateDatetime = new.updateDatetime
				WHERE fromTable = 'life_expense'
                AND fromId = old.recordId;
        END IF;
	ELSE
		-- DELETE
		IF (s_isBankAccount_old = '1') THEN
			DELETE bank_record FROM bank_record WHERE fromTable = 'life_expense' AND fromId = old.recordId;
		END IF;
        
        -- INSERT
		IF (s_isBankAccount_new = '1') THEN
			SELECT MAX(recordId) INTO @recordId FROM bank_record WHERE recordDate = new.recordDate;
				
			IF(@recordId IS NULL) THEN
				SET @no = 0;
			ELSE
				SET @no = substring(@recordId, 9, 4);
			END IF;
			
			SET @no = @no + 1;
			SET @recordId = concat(new.recordDate, lpad(@no,4,0) );
			
			IF(new.transMode = '1') THEN
				INSERT INTO bank_record(recordId, recordDate, transMode, toAccountUserId, toAccountId, memo, amount, fromTable, fromId, enterUserId, enterDatetime)
					VALUE(@recordId, new.recordDate, '1', new.accountUserId, new.accountId, new.memo, new.amount, 'life_expense', new.recordId, new.enterUserId, new.enterDatetime);
			ELSEIF(new.transMode = '2') THEN    
				INSERT INTO bank_record(recordId, recordDate, transMode, fromAccountUserId, fromAccountId, memo, amount, fromTable, fromId, enterUserId, enterDatetime)
					VALUE(@recordId, new.recordDate, '2', new.accountUserId, new.accountId, new.memo, new.amount, 'life_expense', new.recordId, new.enterUserId, new.enterDatetime);
			END IF;
		END IF;
    END IF;
END