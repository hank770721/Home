package com.hkma.home.stock.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hkma.home.stock.entity.StockRecordEntity;

public interface StockRecordRepository extends JpaRepository<StockRecordEntity,String>{
	Optional<StockRecordEntity> findByRecordId(String recordId);
	
	@Query(value="SELECT MAX(recordId) FROM stock_record WHERE recordDate = :recordDate", nativeQuery=true)
	Optional<String> getMaxRecordIdByRecordDate(String recordDate);
	
	@Query(value="SELECT stock_record.* FROM stock_record "
			+ "JOIN bank_authority ON bank_authority.userId = :userId AND bank_authority.accountUserId = stock_record.accountUserId AND bank_authority.accountId = stock_record.accountId "
			+ "WHERE LEFT(stock_record.recordDate,6) = :month "
			+ "ORDER BY stock_record.recordDate DESC", nativeQuery=true)
	List<StockRecordEntity> findByUserIdMonthOrderByRecordDateDesc(String userId, String month);
	
	@Query(value="SELECT stock_record.* FROM stock_record "
			+ "JOIN bank_authority ON bank_authority.userId = :userId AND bank_authority.accountUserId = stock_record.accountUserId AND bank_authority.accountId = stock_record.accountId "
			+ "WHERE LEFT(stock_record.recordDate,6) = :month "
			+ "AND ("
			+ 	"(stock_record.accountUserId = :accountUserId AND stock_record.accountId = :accountId)"
			+ 	"OR (:accountUserId = '' AND :accountId = '')"
			+ ")"
			+ "ORDER BY stock_record.recordDate DESC", nativeQuery=true)
	List<StockRecordEntity> findByUserIdMonthAccountUserIdAccountIdOrderByRecordDateDesc(String userId, String month, String accountUserId, String accountId);
}
