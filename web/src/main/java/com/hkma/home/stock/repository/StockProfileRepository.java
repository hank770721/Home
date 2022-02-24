package com.hkma.home.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hkma.home.stock.entity.StockProfileEntity;

public interface StockProfileRepository extends JpaRepository<StockProfileEntity,Long>{
	StockProfileEntity findById(String id);
}
