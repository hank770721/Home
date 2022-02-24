package com.hkma.home.life.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hkma.home.life.entity.ExpenseAccountGroupEntity;

public interface ExpenseAccountGroupRepository extends JpaRepository<ExpenseAccountGroupEntity,Long>{
	List<ExpenseAccountGroupEntity> findByUserId(String userId);
}
