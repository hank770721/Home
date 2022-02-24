DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_asset_getBalance`(in_userid VARCHAR(20), in_date VARCHAR(8))
BEGIN
	DECLARE d_bank DECIMAL(7,0);
	DECLARE d_stock DECIMAL(7,0);
    DECLARE d_draw DECIMAL(7,0);
    DECLARE d_draw_get DECIMAL(7,0);
    
    -- 20211213 抽籤獨立處理 begin
    -- SELECT ifnull(sum( (CASE transMode WHEN '1' THEN amount WHEN '2' THEN amount * (-1) END) ),0) INTO d_asset
	-- 	FROM home.asset_record
	-- 	WHERE ( (accountFromUserId = in_userid) OR (accountToUserId = in_userid) )
	-- 	AND recordDate <= in_date;
    SELECT ifnull(sum( (CASE transMode WHEN '1' THEN amount WHEN '2' THEN amount * (-1) END) ),0) INTO d_bank
		FROM home.bank_record
		WHERE ( (fromAccountUserId = in_userid) OR (toAccountUserId = in_userid) )
		AND recordDate <= in_date
		AND (type <> '3' OR type IS NULL);
        
	-- 未中籤
	SELECT ifnull(sum( (CASE transMode WHEN '1' THEN (-1) * amount WHEN '2' THEN amount END) ),0) INTO d_draw
		FROM home.stock_record
		WHERE accountUserId = in_userid
        AND assetType = '3'
        AND quantity = 0;
        
	-- 已中籤
	SELECT ifnull(sum( (CASE transMode WHEN '1' THEN (-1) * amount WHEN '2' THEN amount END) ),0) INTO d_draw_get
		FROM home.stock_record
		WHERE accountUserId = in_userid
        AND assetType = '3'
        AND quantity > 0
        AND recordDate <= in_date;
	-- 20211213 抽籤獨立處理 end
    
	SELECT ifnull(sum(quantity * price),0) INTO d_stock
		FROM (
			SELECT 	stockId,
					sum(quantity) AS quantity,
					(SELECT stock_historical.closingPrice FROM collect.stock_historical WHERE stock_historical.stockId = stock_record.stockId AND recordDate <= in_date ORDER BY recordDate DESC LIMIT 1) AS price
			FROM (
				SELECT 	stockId,
						(CASE WHEN transMode = '1' THEN quantity ELSE (-1) * quantity END) AS quantity
				FROM home.stock_record
				WHERE accountUserId = in_userid
                AND recordDate <= in_date
			) AS stock_record
			GROUP BY stockId
			HAVING sum(quantity) > 0
		) stock_record;
        
	SELECT d_bank + d_draw + d_draw_get + d_stock AS amount;
END$$
DELIMITER ;
