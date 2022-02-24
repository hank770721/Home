package com.hkma.home.inventory.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hkma.home.inventory.entity.InventoryPurchaseEntity;

public interface InventoryPurchaseRepository extends JpaRepository<InventoryPurchaseEntity,String>{
	@Query(value="SELECT MAX(recordId) FROM inventory_purchase WHERE recordDate = :recordDate", nativeQuery=true)
	Optional<String> getMaxRecordIdByRecordDate(String recordDate);
	
	@Query(value="SELECT inventory_purchase.quantity - IFNULL((SELECT COUNT(1) FROM home.inventory_use WHERE inventory_use.purchaseId = inventory_purchase.recordId AND inventory_use.isRunOut),0) FROM inventory_purchase WHERE recordId = :recordId", nativeQuery=true)
	Optional<Double> getInventoryQuantityByRecordId(String recordId);
	
	@Query(value="SELECT * FROM inventory_purchase "
			+ "WHERE SUBSTRING(recordDate,1,6) = :month "
			+ "AND EXISTS (SELECT 1 FROM inventory_authority WHERE inventory_authority.userId = :userId AND inventory_authority.dataUserId = inventory_purchase.userId) "
			+ "ORDER BY recordId DESC", nativeQuery=true)
	List<InventoryPurchaseEntity> findByUserIdAndMonthOrderByRecordId(String userId, String month);
	
	@Query(value="SELECT * FROM inventory_purchase "
			+ "WHERE name LIKE CONCAT('%',:name,'%') "
			+ "AND EXISTS (SELECT 1 FROM inventory_authority WHERE inventory_authority.userId = :userId AND inventory_authority.dataUserId = inventory_purchase.userId) "
			+ "AND ("
			+ "(:inventoryType = '1' AND (inventory_purchase.quantity - IFNULL((SELECT COUNT(1) FROM home.inventory_use WHERE inventory_use.purchaseId = inventory_purchase.recordId AND inventory_use.isRunOut),0) > 0)) "
			+ "OR (:inventoryType = '2' AND (inventory_purchase.quantity - IFNULL((SELECT COUNT(1) FROM home.inventory_use WHERE inventory_use.purchaseId = inventory_purchase.recordId AND inventory_use.isRunOut),0) = 0)) "
			+ "OR (:inventoryType = '3')"
			+ ") "
			+ "ORDER BY recordId DESC", nativeQuery=true)
	List<InventoryPurchaseEntity> findByUserIdAndNameAndInventoryTypeOrderByRecordId(String userId, String name, String inventoryType);
	
	@Query(value="SELECT * FROM inventory_purchase WHERE inventory_purchase.quantity - IFNULL((SELECT COUNT(1) FROM home.inventory_use WHERE inventory_use.purchaseId = inventory_purchase.recordId AND inventory_use.isRunOut),0) > 0 ORDER BY inventory_purchase.class1, inventory_purchase.class2, inventory_purchase.name, inventory_purchase.recordId", nativeQuery=true)
	List<InventoryPurchaseEntity> findInventory();
	
	@Query(value="SELECT * FROM inventory_purchase "
			+ "WHERE EXISTS (SELECT 1 FROM inventory_authority WHERE inventory_authority.userId = :userId AND inventory_authority.dataUserId = inventory_purchase.userId) "
			+ "AND inventory_purchase.quantity - IFNULL((SELECT COUNT(1) FROM home.inventory_use WHERE inventory_use.purchaseId = inventory_purchase.recordId),0) > 0 AND (recordId LIKE CONCAT('%',:recordId,'%') OR name LIKE CONCAT('%',:recordId,'%')) "
			+ "ORDER BY recordId", nativeQuery=true)
	List<InventoryPurchaseEntity> findByUserIdAndRecordIdOrName(String userId, String recordId);
	
	@Query(value="SELECT inventory_purchase.class1 FROM inventory_purchase WHERE inventory_purchase.quantity - IFNULL((SELECT COUNT(1) FROM home.inventory_use WHERE inventory_use.purchaseId = inventory_purchase.recordId AND inventory_use.isRunOut),0) > 0 GROUP BY inventory_purchase.class1", nativeQuery=true)
	Optional<List<String>> findClass1();
	
	@Query(value="SELECT inventory_purchase.class2 FROM inventory_purchase WHERE inventory_purchase.class1 = :class1 AND inventory_purchase.quantity - IFNULL((SELECT COUNT(1) FROM home.inventory_use WHERE inventory_use.purchaseId = inventory_purchase.recordId AND inventory_use.isRunOut),0) > 0 GROUP BY inventory_purchase.class2", nativeQuery=true)
	Optional<List<String>> findClass2ByClass1(String class1);
	
	@Query(value="SELECT CONCAT(inventory_purchase.brand,inventory_purchase.name) FROM inventory_purchase WHERE class1 = :class1 AND class2 = :class2 AND inventory_purchase.quantity - IFNULL((SELECT COUNT(1) FROM home.inventory_use WHERE inventory_use.purchaseId = inventory_purchase.recordId AND inventory_use.isRunOut),0) > 0 GROUP BY CONCAT(inventory_purchase.brand,inventory_purchase.name)", nativeQuery=true)
	Optional<List<String>> findNameByClass1Class2(String class1, String class2);
}
