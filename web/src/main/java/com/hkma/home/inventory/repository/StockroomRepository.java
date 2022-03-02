package com.hkma.home.inventory.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hkma.home.inventory.entity.StockroomEntity;

public interface StockroomRepository extends JpaRepository<StockroomEntity,Long>{
	Optional<StockroomEntity> findByUserIdAndId(String userId, String id);
}
