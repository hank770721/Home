package com.hkma.home.inventory.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hkma.home.inventory.entity.InventoryUseEntity;

public interface InventoryUseRepository extends JpaRepository<InventoryUseEntity,String>{
	@Query(value="SELECT MAX(recordId) FROM inventory_use WHERE recordDate = :recordDate", nativeQuery=true)
	Optional<String> getMaxRecordIdByRecordDate(String recordDate);
	
	@Query(value="SELECT * FROM inventory_use "
			+ "WHERE EXISTS (SELECT 1 FROM inventory_authority JOIN inventory_purchase ON inventory_authority.userId = :userId AND inventory_authority.dataUserId = inventory_purchase.userId WHERE inventory_purchase.recordId = inventory_use.purchaseId) "
			+ "AND SUBSTRING(recordDate,1,6) = :month "
			+ "ORDER BY recordId DESC", nativeQuery=true)
	List<InventoryUseEntity> findByUserIdAndMonthOrderByRecordIdDesc(String userId, String month);
	
	@Query(value="SELECT * FROM inventory_use "
			+ "WHERE EXISTS (SELECT 1 FROM inventory_authority JOIN inventory_purchase ON inventory_authority.userId = :userId AND inventory_authority.dataUserId = inventory_purchase.userId WHERE inventory_purchase.recordId = inventory_use.purchaseId) "
			+ "AND (SELECT inventory_purchase.name FROM inventory_purchase WHERE inventory_purchase.recordId = inventory_use.purchaseId) LIKE CONCAT('%',:name,'%') "
			+ "ORDER BY recordId DESC", nativeQuery=true)
	List<InventoryUseEntity> findByUserIdAndNameOrderByRecordIdDesc(String userId, String name);
	
	List<InventoryUseEntity> findByPurchaseIdOrderByRecordIdAsc(String purchaseId);
	
	@Query(value="SELECT * FROM inventory_use WHERE purchaseId = :purchaseId AND isRunOut = '0' ORDER BY recordId", nativeQuery=true)
	List<InventoryUseEntity> findUsingByPurchaseId(String purchaseId);
}
