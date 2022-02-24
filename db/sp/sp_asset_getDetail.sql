DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_asset_getDetail`(in_userid VARCHAR(20))
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
		recordDate VARCHAR(8),
        memo VARCHAR(20),
		plus DECIMAL(8,0),
        minus DECIMAL(8,0),
        blance DECIMAL(8,0)
	);
    
    INSERT INTO temp_Detail(recordId, recordDate, memo, plus, minus)
		SELECT 	asset_record.recordId,
				asset_record.recordDate,
				(CASE WHEN asset_record.stockId IS NULL THEN asset_record.memo ELSE (CASE WHEN asset_record.isDividend IS NULL THEN (CASE WHEN asset_record.amount = 20 THEN '抽籤' ELSE ifnull(stock_profile.name,'') END) ELSE concat(stock_profile.name,'股息') END) END) AS memo,
                (CASE WHEN asset_record.transMode = '1' THEN asset_record.amount ELSE 0 END) AS plus,
                (CASE WHEN asset_record.transMode = '1' THEN 0 ELSE asset_record.amount END) AS minus
		FROM home.asset_record
		LEFT JOIN collect.stock_profile ON stock_profile.id = asset_record.stockId
		LEFT JOIN home.stock_assettype ON stock_assettype.userId = ifnull(accountFromUserId,accountToUserId) AND stock_assettype.id = asset_record.type
		WHERE (asset_record.accountFromUserId = in_userid OR asset_record.accountToUserId = in_userid)
		ORDER BY asset_record.recordDate, asset_record.recordId;
        
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
END$$
DELIMITER ;
