package com.hkma.home.life.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hkma.home.life.entity.ExpenseEntity;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity,String>{
	@Query(value="SELECT MAX(recordId) FROM life_expense WHERE recordDate = :recordDate", nativeQuery=true)
	Optional<String> getMaxRecordIdByRecordDate(String recordDate);
	
	@Query(value="SELECT * FROM life_expense "
			+ "WHERE LEFT(recordDate,6) = :month "
			+ "AND EXISTS (SELECT 1 FROM bank_authority WHERE userId = :userId AND bank_authority.accountUserId = life_expense.accountUserId AND bank_authority.accountId = life_expense.accountId) "
			+ "ORDER BY recordDate DESC, recordId DESC", nativeQuery=true)
	List<ExpenseEntity> findByUserIdMonthOrderByRecordDateDescRecordIdDesc(String userId, String month);
	
	@Query(value="SELECT * FROM life_expense "
			+ "WHERE LEFT(recordDate,6) = :month "
			+ "AND EXISTS (SELECT 1 FROM bank_authority WHERE userId = :userId AND bank_authority.accountUserId = life_expense.accountUserId AND bank_authority.accountId = life_expense.accountId) "
			+ "AND ("
			+ 	"(accountUserId = :accountUserId AND accountId = :accountId) "
			+ 	"OR (accountUserId = :accountUserId AND :accountId = '') "
			+ 	"OR (:accountUserId = '' AND :accountId = '')"
			+ ")"
			+ "ORDER BY recordDate DESC", nativeQuery=true)
	List<ExpenseEntity> findByUserIdMonthAccountUserIdAccountIdOrderByRecordDateDesc(String userId, String month, String accountUserId, String accountId);
	
	//@Query(value="SELECT * FROM life_expense WHERE accountUserId = :accountUserId AND accountId = :accountId ORDER BY recordDate", nativeQuery=true)
	//List<ExpenseEntity> findByAccountUserIdAndAccountIdOrderByRecordDateAsc(String accountUserId, String accountId);
	
	@Query(value="SELECT * FROM life_expense "
			+ "JOIN bank_accountgroupdetail accountgroupdetail ON accountgroupdetail.accountUserId = life_expense.accountUserId AND accountgroupdetail.accountId = life_expense.accountId "
			+ "WHERE LEFT(life_expense.vestingDate,6) = :month "
			+ "AND accountgroupdetail.userId = :userId "
			+ "AND accountgroupdetail.groupId = :groupId "
			+ "ORDER BY life_expense.vestingDate", nativeQuery=true)
	List<ExpenseEntity> findByUserIdAndGroupIdAndMonthOrderByVestingDate(String userId, String groupId, String month);
}
