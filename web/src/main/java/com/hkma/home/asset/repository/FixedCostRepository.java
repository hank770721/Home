package com.hkma.home.asset.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hkma.home.asset.entity.FixedCostEntity;

public interface FixedCostRepository extends JpaRepository<FixedCostEntity,String>{
	void deleteByUserIdAndId(String accountUserId, String accountId);
	
	Optional<FixedCostEntity> findByUserIdAndId(String userId, String id);

	List<FixedCostEntity> findByUserId(String userId);
	
	@Query(value="SELECT * FROM asset_fixedcost WHERE userId=:userId AND name LIKE CONCAT('%',:name,'%')", nativeQuery=true)
	List<FixedCostEntity> findByUserIdAndName(String userId, String name);
	
	@Query(value="SELECT id FROM asset_fixedcost WHERE userId=:userId ORDER BY id DESC LIMIT 1", nativeQuery=true)
	String getMaxIdByUserId(String userId);
}
