CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_bank_getRecord`(in_accountUserId VARCHAR(20), in_accountId VARCHAR(4))
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
    
    INSERT INTO temp_Detail(recordId, recordDate, memo, plus, minus)
		SELECT 	bank_record.recordId,
				bank_record.recordDate,
				(
					CASE
					WHEN bank_record.stockId IS NULL THEN bank_record.memo
                    ELSE (
						CASE 
							WHEN bank_record.isDividend IS NULL THEN (CASE WHEN bank_record.type = '3' THEN concat('抽籤 ',ifnull(stock_profile.name,'') ) ELSE ifnull(stock_profile.name,'') END)
                            ELSE concat(stock_profile.name,'股息')
						END
					)
					END
				) AS memo,
                (CASE WHEN bank_record.transMode = '1' THEN bank_record.amount ELSE 0 END) AS plus,
                (CASE WHEN bank_record.transMode = '1' THEN 0 ELSE bank_record.amount END) AS minus
		FROM home.bank_record
		LEFT JOIN collect.stock_profile ON stock_profile.id = bank_record.stockId
		LEFT JOIN home.stock_assettype ON stock_assettype.accountUserId = ifnull(fromAccountUserId,toAccountUserId) AND stock_assettype.id = bank_record.type
		WHERE (
			(bank_record.fromAccountUserId = in_accountUserId AND bank_record.fromAccountId = in_accountId)
            OR (bank_record.toAccountUserId = in_accountUserId AND bank_record.toAccountId = in_accountId)
		)
		ORDER BY bank_record.recordDate, bank_record.recordId;
        
	SET @i = 0;
	INSERT INTO temp_Detail2(recordId, recordDate, memo, plus, minus, blance)
		SELECT recordId, recordDate, memo, plus, minus, @i := @i + plus - minus FROM temp_Detail;
        
	SELECT 	recordDate,
			memo,
            plus,
            minus,
            blance
		FROM temp_Detail2 ORDER BY recordDate DESC, recordId DESC;
    
    DROP TABLE temp_Detail;
    DROP TABLE temp_Detail2;
END