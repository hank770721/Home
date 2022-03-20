CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_bank_record`(in_userId VARCHAR(12), in_type CHAR(1), in_accountUserId VARCHAR(12), in_accountId VARCHAR(4), in_groupUserId VARCHAR(12), in_groupId VARCHAR(3))
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
    
    -- 支出
    INSERT INTO temp_Detail(recordId, recordDate, memo, plus, minus)
		SELECT 	bank_record.recordId,
				concat(left(bank_record.recordDate,4),'/',substr(bank_record.recordDate,5,2),'/',right(bank_record.recordDate,2) ) AS recordDate,
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
                0 AS plus,
                bank_record.amount AS minus
		FROM bank_record
        JOIN bank_account ON bank_account.userId = bank_record.fromAccountUserId AND bank_account.id = bank_record.fromAccountId
        JOIN bank_authority ON bank_authority.accountUserId = bank_account.userId AND bank_authority.accountId = bank_account.id
        LEFT JOIN collect.stock_profile ON stock_profile.id = bank_record.stockId
		WHERE bank_authority.userId = in_userId
        AND bank_account.isBankAccount = '1'
        AND ( 
			(
				in_type = '1'
				AND (
					(bank_account.userId = in_accountUserId AND bank_account.id = in_accountId)
					OR (in_accountUserId = '' AND in_accountId = '')
                )
			)
			OR (
				in_type = '2'
                AND (
					EXISTS (
						SELECT 1 FROM bank_accountgroupdetail 
						WHERE bank_accountgroupdetail.accountUserId = bank_account.userId 
						AND bank_accountgroupdetail.accountId = bank_account.id 
						AND bank_accountgroupdetail.userId = in_userId 
						AND bank_accountgroupdetail.groupId = in_groupId
					)
                    OR (in_groupUserId = '' AND in_groupId = '')
				)
            )
		)
		ORDER BY bank_record.recordDate, bank_record.recordId;
	
    -- 存入
	INSERT INTO temp_Detail(recordId, recordDate, memo, plus, minus)
		SELECT 	bank_record.recordId,
				concat(left(bank_record.recordDate,4),'/',substr(bank_record.recordDate,5,2),'/',right(bank_record.recordDate,2) ) AS recordDate,
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
                bank_record.amount AS plus,
                0 AS minus
		FROM bank_record
        JOIN bank_account ON bank_account.userId = bank_record.toAccountUserId AND bank_account.id = bank_record.toAccountId
        JOIN bank_authority ON bank_authority.accountUserId = bank_account.userId AND bank_authority.accountId = bank_account.id
		LEFT JOIN collect.stock_profile ON stock_profile.id = bank_record.stockId
        WHERE bank_authority.userId = in_userId
        AND bank_account.isBankAccount = '1'
        AND ( 
			(
				in_type = '1'
				AND (
					(bank_account.userId = in_accountUserId AND bank_account.id = in_accountId)
					OR (in_accountUserId = '' AND in_accountId = '')
                )
			)
			OR (
				in_type = '2'
                AND (
					EXISTS (
						SELECT 1 FROM bank_accountgroupdetail 
						WHERE bank_accountgroupdetail.accountUserId = bank_account.userId 
						AND bank_accountgroupdetail.accountId = bank_account.id 
						AND bank_accountgroupdetail.userId = in_userId 
						AND bank_accountgroupdetail.groupId = in_groupId
					)
                    OR (in_groupUserId = '' AND in_groupId = '')
				)
            )
		)
		ORDER BY bank_record.recordDate, bank_record.recordId;
        
	SET @i = 0;
	INSERT INTO temp_Detail2(recordId, recordDate, memo, plus, minus, blance)
		SELECT recordId, recordDate, memo, plus, minus, @i := @i + plus - minus FROM temp_Detail ORDER BY recordId, recordDate;
        
	SELECT 	recordDate,
			memo,
            format(plus,0) AS plus,
            format(minus,0) AS minus,
            format(blance,0) AS blance
		FROM temp_Detail2 ORDER BY recordDate DESC, recordId DESC;
    
    DROP TABLE temp_Detail;
    DROP TABLE temp_Detail2;
END