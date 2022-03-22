CREATE DEFINER=`root`@`%` PROCEDURE `sp_bank_credit`(in_userId VARCHAR(12), in_accountUserId VARCHAR(12), in_accountId VARCHAR(4))
BEGIN
	DROP TEMPORARY TABLE IF EXISTS `temp_Detail`;
    DROP TEMPORARY TABLE IF EXISTS `temp_Detail2`;
    
    CREATE TEMPORARY TABLE temp_Detail(
		dateType CHAR(1),
		recordId VARCHAR(12),
		recordDate VARCHAR(10),
        memo VARCHAR(20),
		plus DECIMAL(8,0),
        minus DECIMAL(8,0)
	);
    
    CREATE TEMPORARY TABLE temp_Detail2(
		dateType CHAR(1),
		recordId VARCHAR(12),
		recordDate VARCHAR(10),
        memo VARCHAR(20),
		plus DECIMAL(8,0),
        minus DECIMAL(8,0),
        blance DECIMAL(8,0)
	);
    
    -- 銀行
    INSERT INTO temp_Detail(dateType, recordId, recordDate, memo, plus, minus)
		SELECT 	'1',
				bank_record.recordId,
				concat(left(bank_record.recordDate,4),'/',substr(bank_record.recordDate,5,2),'/',right(bank_record.recordDate,2) ) AS recordDate,
				bank_record.memo,
                0 AS plus,
                (CASE WHEN bank_record.transMode = '2' THEN bank_record.amount ELSE 0 END) AS minus
		FROM home.bank_record
        JOIN bank_account ON bank_account.userId = bank_record.fromAccountUserId AND bank_account.id = bank_record.fromAccountId AND bank_account.isCreditCard = '1'
        JOIN bank_authority ON bank_authority.userId = in_userId AND bank_authority.accountUserId = bank_account.userId AND bank_authority.accountId = bank_account.id
		WHERE (
			(bank_account.userId = in_accountUserId AND bank_account.id = in_accountId)
            OR (in_accountUserId = '' AND in_accountId = '')
		);
        
	INSERT INTO temp_Detail(dateType, recordId, recordDate, memo, plus, minus)
		SELECT 	'1',
				bank_record.recordId,
				concat(left(bank_record.recordDate,4),'/',substr(bank_record.recordDate,5,2),'/',right(bank_record.recordDate,2) ) AS recordDate,
				bank_record.memo,
                (CASE WHEN bank_record.transMode = '1' THEN bank_record.amount ELSE 0 END) AS plus,
                0 AS minus
		FROM home.bank_record
        JOIN bank_account ON bank_account.userId = bank_record.toAccountUserId AND bank_account.id = bank_record.toAccountId AND bank_account.isCreditCard = '1'
        JOIN bank_authority ON bank_authority.userId = in_userId AND bank_authority.accountUserId = bank_account.userId AND bank_authority.accountId = bank_account.id
		WHERE (
            (bank_account.userId = in_accountUserId AND bank_account.id = in_accountId)
            OR (in_accountUserId = '' AND in_accountId = '')
		);
        
	-- 生活
    INSERT INTO temp_Detail(dateType, recordId, recordDate, memo, plus, minus)
		SELECT 	'2',
				life_expense.recordId,
				concat(left(life_expense.recordDate,4),'/',substr(life_expense.recordDate,5,2),'/',right(life_expense.recordDate,2) ) AS recordDate,
				life_expense.memo,
				(CASE WHEN life_expense.transMode = '1' THEN life_expense.amount ELSE 0 END) AS plus,
				(CASE WHEN life_expense.transMode = '2' THEN life_expense.amount ELSE 0 END) AS minus
		FROM life_expense
        JOIN bank_account ON bank_account.userId = life_expense.accountUserId AND bank_account.id = life_expense.accountId AND bank_account.isCreditCard = '1'
        JOIN bank_authority ON bank_authority.userId = in_userId AND bank_authority.accountUserId = bank_account.userId AND bank_authority.accountId = bank_account.id
        WHERE (bank_account.userId = in_accountUserId AND bank_account.id = in_accountId)
        OR (in_accountUserId = '' AND in_accountId = '');
        
	SET @i = 0;
	INSERT INTO temp_Detail2(dateType, recordId, recordDate, memo, plus, minus, blance)
		SELECT dateType, recordId, recordDate, memo, plus, minus, @i := @i + plus - minus FROM temp_Detail ORDER BY recordDate, recordId, dateType;
        
	SELECT 	recordDate,
			memo,
            format(plus,0) AS plus,
            format(minus,0) AS minus,
            format(blance,0) AS blance
		FROM temp_Detail2 ORDER BY recordDate DESC, recordId DESC, dateType DESC;
    
    DROP TABLE temp_Detail;
    DROP TABLE temp_Detail2;
END