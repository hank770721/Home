package com.hkma.home.life.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hkma.home.life.entity.ExpenseAccountGroupDetailEntity;

public interface ExpenseAccountGroupDetailRepository extends JpaRepository<ExpenseAccountGroupDetailEntity,Long>{
	List<ExpenseAccountGroupDetailEntity> findByUserIdAndGroupId(String userId, String groupId);
}
