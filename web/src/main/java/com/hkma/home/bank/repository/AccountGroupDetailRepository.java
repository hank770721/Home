package com.hkma.home.bank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hkma.home.bank.entity.AccountGroupDetailEntity;

public interface AccountGroupDetailRepository extends JpaRepository<AccountGroupDetailEntity,Long>{
	List<AccountGroupDetailEntity> findByUserIdAndGroupId(String userId, String groupId);
}
