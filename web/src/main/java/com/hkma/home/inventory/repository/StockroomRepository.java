package com.hkma.home.inventory.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hkma.home.inventory.entity.StockroomEntity;

public interface StockroomRepository extends JpaRepository<StockroomEntity,Long>{
	Optional<StockroomEntity> findByUserIdAndId(String userId, String id);
	
	@Query(value="SELECT (CASE WHEN COUNT(1)>0 THEN 'true' ELSE 'false' END) "
			+ "FROM inventory_stockroom "
			+ "JOIN inventory_authority ON inventory_authority.userId = :userId AND inventory_authority.stockroomUserId = inventory_stockroom.userId AND inventory_authority.stockroomId = inventory_stockroom.id "
			+ "WHERE inventory_stockroom.userId = :stockroomUserId "
			+ "AND inventory_stockroom.id = :stockroomId", nativeQuery=true)
	boolean existsByUserIdAndStockroomUserIdAndStockroomId(String userId, String stockroomUserId, String stockroomId);
}
