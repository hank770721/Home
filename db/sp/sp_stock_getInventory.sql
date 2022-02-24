DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_stock_getInventory`(in_accountUserId VARCHAR(20), in_accountId VARCHAR(4))
BEGIN
	SELECT 	stockId,
			(SELECT name FROM collect.stock_profile WHERE stock_profile.id = stock_record.stockId) AS stockName,
			quantity,
			price,
			quantity * price AS amount,
            cost,
            (quantity * price) - cost AS profit
		FROM (
			-- 20220221 改用庫存股table
			/*
			SELECT 	stockId,
					sum(quantity) AS quantity,
                    sum(amount) AS cost,
					(SELECT stock_historical.closingPrice FROM collect.stock_historical WHERE stock_historical.stockId = stock_record.stockId ORDER BY recordDate DESC LIMIT 1) AS price
			FROM (
				SELECT 	stockId,
						(CASE WHEN transMode = '1' THEN quantity ELSE (-1) * quantity END) AS quantity,
                        (CASE WHEN transMode = '1' THEN amount ELSE (-1) * amount END) AS amount
				FROM home.stock_record
				WHERE accountUserId = in_accountUserId
                AND accountId = in_accountId
			) AS stock_record
			GROUP BY stockId
			HAVING sum(quantity) > 0
            */
            SELECT 	stockId,
					quantity,
                    amount AS cost,
					(SELECT stock_historical.closingPrice FROM collect.stock_historical WHERE stock_historical.stockId = stock_treasury.stockId ORDER BY recordDate DESC LIMIT 1) AS price
            FROM stock_treasury
            WHERE accountUserId = in_accountUserId
			AND accountId = in_accountId
		) stock_record
		ORDER BY stockId;
END$$
DELIMITER ;
