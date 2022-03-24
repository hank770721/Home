package com.hkma.home.stock.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hkma.home.stock.entity.AssetTypeEntity;

public interface AssetTypeRepository extends JpaRepository<AssetTypeEntity,Long>{
	List<AssetTypeEntity> findByAccountUserIdAndAccountId(String accountUserId, String accountId);
}
