package com.hkma.home.inventory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hkma.home.inventory.entity.AuthorityEntity;

public interface InventoryAuthorityRepository extends JpaRepository<AuthorityEntity,Long>{
	List<AuthorityEntity> findByUserId(String userId);
}
