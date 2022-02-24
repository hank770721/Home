package com.hkma.home.bank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hkma.home.bank.entity.AuthorityEntity;

public interface AuthorityRepository extends JpaRepository<AuthorityEntity,Long>{
	List<AuthorityEntity> findByUserId(String userId);
	
	List<AuthorityEntity> findByUserIdOrderByOrderNumberAsc(String userId);
}
