package com.hkma.home.bank.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hkma.home.bank.entity.RecordEntity;

public interface RecordRepository extends JpaRepository<RecordEntity,String>{
	@Query(value="SELECT MAX(recordId) FROM bank_record WHERE recordDate = :recordDate", nativeQuery=true)
	Optional<String> getMaxRecordIdByRecordDate(String recordDate);
	
	@Query(value="SELECT * FROM bank_record "
			+ "WHERE LEFT(recordDate,6) = :month "
			+ "AND ("
			+ 	"EXISTS (SELECT 1 FROM bank_authority WHERE userId = :userId AND bank_authority.accountUserId = bank_record.fromAccountUserId AND bank_authority.accountId = bank_record.fromAccountId) "
			+ 	"OR EXISTS (SELECT 1 FROM bank_authority WHERE userId = :userId AND bank_authority.accountUserId = bank_record.toAccountUserId AND bank_authority.accountId = bank_record.toAccountId)"
			+ ") "
			+ "ORDER BY recordDate DESC, recordId DESC", nativeQuery=true)
	List<RecordEntity> findByUserIdMonthOrderByRecordDateDescRecordIdDesc(String userId, String month);
	
	@Query(value="SELECT * FROM bank_record "
			+ "WHERE LEFT(recordDate,6) = :month "
			+ "AND ("
			+ 	"EXISTS (SELECT 1 FROM bank_authority WHERE userId = :userId AND bank_authority.accountUserId = bank_record.fromAccountUserId AND bank_authority.accountId = bank_record.fromAccountId) "
			+ 	"OR EXISTS (SELECT 1 FROM bank_authority WHERE userId = :userId AND bank_authority.accountUserId = bank_record.toAccountUserId AND bank_authority.accountId = bank_record.toAccountId)"
			+ ") "
			+ "AND ("
			+ 	"(fromAccountUserId = :accountUserId AND fromAccountId = :accountId) "
			+ 	"OR (toAccountUserId = :accountUserId AND toAccountId = :accountId) "
			+ 	"OR ((fromAccountUserId = :accountUserId OR toAccountUserId = :accountUserId) AND :accountId = '') "
			+ 	"OR (:accountUserId = '' AND :accountId = '')"
			+ ")"
			+ "ORDER BY recordDate DESC, recordId DESC", nativeQuery=true)
	List<RecordEntity> findByUserIdMonthAccountUserIdAccountIdOrderByRecordDateDescRecordIdDesc(String userId, String month, String accountUserId, String accountId);
	
	@Query(value="SELECT * FROM bank_record WHERE ((fromAccountUserId = :accountUserId AND fromAccountId = :accountId) OR (toAccountUserId = :accountUserId AND toAccountId = :accountId)) ORDER BY recordDate", nativeQuery=true)
	List<RecordEntity> findByAccountUserIdAccountId(String accountUserId, String accountId);
}
