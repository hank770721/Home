package com.hkma.home.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hkma.home.inventory.entity.AuthorityEntity;

public interface InventoryAuthorityRepository extends JpaRepository<AuthorityEntity,Long>{
	boolean existsByUserIdAndDataUserId(String userId, String dataUserId);
}
