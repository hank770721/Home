DELIMITER $$
CREATE DEFINER=`root`@`%` PROCEDURE `sp_bank_credit`(in_accountUserId VARCHAR(20), in_accountId VARCHAR(4))
BEGIN
	DROP TEMPORARY TABLE IF EXISTS `temp_Detail`;
    DROP TEMPORARY TABLE IF EXISTS `temp_Detail2`;
    
    CREATE TEMPORARY TABLE temp_Detail(
		recordId VARCHAR(12),
		recordDate VARCHAR(10),
        memo VARCHAR(20),
		plus DECIMAL(8,0),
        minus DECIMAL(8,0)
	);
    
    CREATE TEMPORARY TABLE temp_Detail2(
		recordId VARCHAR(12),
		recordDate VARCHAR(10),
        memo VARCHAR(20),
		plus DECIMAL(8,0),
        minus DECIMAL(8,0),
        blance DECIMAL(8,0)
	);
    
    -- 銀行
    INSERT INTO temp_Detail(recordId, recordDate, memo, plus, minus)
		SELECT 	recordId,
				concat(left(recordDate,4),'/',substr(recordDate,5,2),'/',right(recordDate,2) ) AS recordDate,
				memo,
                (CASE WHEN transMode = '1' THEN amount ELSE 0 END) AS plus,
                (CASE WHEN transMode = '2' THEN amount ELSE 0 END) AS minus
		FROM home.bank_record
		WHERE (
			(fromAccountUserId = in_accountUserId AND fromAccountId = in_accountId) 
            OR (toAccountUserId = in_accountUserId AND toAccountId = in_accountId)
		);
        
	-- 生活
    INSERT INTO temp_Detail(recordId, recordDate, memo, plus, minus)
		SELECT 	recordId,
				concat(left(recordDate,4),'/',substr(recordDate,5,2),'/',right(recordDate,2) ) AS recordDate,
				memo,
				(CASE WHEN transMode = '1' THEN amount ELSE 0 END) AS plus,
				(CASE WHEN transMode = '2' THEN amount ELSE 0 END) AS minus
		FROM life_expense WHERE accountUserId = in_accountUserId AND accountId = in_accountId;
        
	SET @i = 0;
	INSERT INTO temp_Detail2(recordId, recordDate, memo, plus, minus, blance)
		SELECT recordId, recordDate, memo, plus, minus, @i := @i + plus - minus FROM temp_Detail ORDER BY recordDate, recordId;
        
	SELECT 	recordDate,
			memo,
            format(plus,0) AS plus,
            format(minus,0) AS minus,
            format(blance,0) AS blance
		FROM temp_Detail2 ORDER BY recordDate DESC, recordId DESC;
    
    DROP TABLE temp_Detail;
    DROP TABLE temp_Detail2;
END$$
DELIMITER ;
