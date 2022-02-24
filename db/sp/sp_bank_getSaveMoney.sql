DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_bank_getSaveMoney`(in_accountUserId VARCHAR(20), in_accountId VARCHAR(4), in_startMonth VARCHAR(6), in_endMonth VARCHAR(6))
BEGIN
	DECLARE i_count INT;
    DECLARE i_month INT;
    DECLARE dt_record DATETIME;
    DECLARE dt_temp DATETIME;
    
    SET i_month = TIMESTAMPDIFF(MONTH,concat(in_startMonth,'01'), concat(in_endMonth,'01') );
	SELECT count(1) INTO i_count FROM home.bank_getsavemoney_temp WHERE accountUserId = in_accountUserId AND accountId = in_accountId AND month BETWEEN in_startMonth AND in_endMonth;
    
    SELECT max(enterDatetime) INTO dt_record 
		FROM home.bank_record
		WHERE (
			(fromAccountUserId = in_accountUserId AND fromAccountId = in_accountId)
            OR (toAccountUserId = in_accountUserId AND toAccountId = in_accountId)
		);
        
    SELECT max(enterDatetime) INTO dt_temp
		FROM home.bank_getsavemoney_temp
        WHERE accountUserId = in_accountUserId
        AND accountId = in_accountId
        AND month BETWEEN in_startMonth AND in_endMonth;
    
    IF (i_count <> i_month + 1 OR dt_temp < dt_record) THEN
		DELETE home.bank_getsavemoney_temp FROM home.bank_getsavemoney_temp WHERE accountUserId = in_accountUserId AND accountId = in_accountId;
        
		DROP TEMPORARY TABLE IF EXISTS `temp_SaveMoney`;
		CREATE TEMPORARY TABLE temp_SaveMoney(
			month VARCHAR(6),
			amount DECIMAL(8,0)
		);
		
		SET @month = in_startMonth;
		
		WHILE (@month <= in_endMonth) DO
			INSERT INTO temp_SaveMoney(month, amount)
				SELECT 	@month, 
						ifnull(sum( (CASE transMode WHEN '1' THEN amount WHEN '2' THEN amount * (-1) END) ),0) 
				FROM home.bank_record
				WHERE (
					(fromAccountUserId = in_accountUserId AND fromAccountId = in_accountId)
                    OR (toAccountUserId = in_accountUserId AND toAccountId = in_accountId)
				)
				AND isDividend IS NULL
				AND type IS NULL
				AND left(recordDate,6) <= @month;

			SET @month = date_format(date_add(concat(@month,'01'), interval 1 MONTH),'%Y%m');
		END WHILE;
		
        INSERT INTO bank_getsavemoney_temp(accountUserId, accountId, month, amount, enterDatetime)
			SELECT in_accountUserId, in_accountId, month, amount, sysdate() FROM temp_SaveMoney;
            
		DROP TABLE temp_SaveMoney;
	END IF;
    
    SELECT month, amount 
		FROM home.bank_getsavemoney_temp
		WHERE accountUserId = in_accountUserId
        AND accountId = in_accountId
        AND month BETWEEN in_startMonth AND in_endMonth
        ORDER BY month;
END$$
DELIMITER ;
