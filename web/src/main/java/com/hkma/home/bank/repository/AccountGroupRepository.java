package com.hkma.home.bank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hkma.home.bank.entity.AccountGroupEntity;

public interface AccountGroupRepository extends JpaRepository<AccountGroupEntity,Long>{
	List<AccountGroupEntity> findByUserId(String userId);
}
