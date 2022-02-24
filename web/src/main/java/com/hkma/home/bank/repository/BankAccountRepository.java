package com.hkma.home.bank.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hkma.home.bank.entity.BankAccountEntity;

public interface BankAccountRepository extends JpaRepository<BankAccountEntity,Long>{
	Optional<BankAccountEntity> findByUserIdAndId(String authorityAccountUserId, String authorityAccountId);
}
