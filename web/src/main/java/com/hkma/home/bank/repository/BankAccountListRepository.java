package com.hkma.home.bank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.hkma.home.bank.entity.BankAccountListEntity;

public interface BankAccountListRepository extends Repository<BankAccountListEntity,Long>{
	@Query(value="SELECT bank_account.userId, bank_account.id, bank_account.bankId, bank_account.memo, bank_bank.name AS bankName "
			+ "FROM bank_account "
			+ "JOIN bank_authority ON bank_authority.accountUserId=bank_account.userId AND bank_authority.accountId=bank_account.id "
			+ "LEFT JOIN bank_bank ON bank_bank.id=bank_account.bankId "
			+ "WHERE bank_authority.userId=:userId "
			+ "AND (bank_account.isBankAccount=:isBankAccount OR :isBankAccount IS NULL)"
			+ "AND (bank_account.isSecurities=:isSecurities OR :isSecurities IS NULL)"
			+ "ORDER BY bank_authority.orderNumber", nativeQuery=true)
	List<BankAccountListEntity> findByUserIdAndIsBankAccountAndIsSecurities(String userId, Boolean isBankAccount, Boolean isSecurities);
}
