package com.hkma.home.stock.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hkma.home.stock.entity.StockProfileEntity;

public interface StockProfileRepository extends JpaRepository<StockProfileEntity,Long>{
	StockProfileEntity findById(String id);
	
	@Query(value="SELECT * FROM collect.stock_profile WHERE id LIKE CONCAT('%',:id,'%') OR name LIKE CONCAT('%',:id,'%')", nativeQuery=true)
	List<StockProfileEntity> findByIdOrNameLike(String id);
}
