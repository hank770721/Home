package com.hkma.home.life.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hkma.home.life.entity.TemperatureEntity;

public interface TemperatureRepository extends JpaRepository<TemperatureEntity,String>{
	@Query(value="SELECT MAX(recordId) FROM life_temperature WHERE recordDate = :recordDate", nativeQuery=true)
	Optional<String> getMaxRecordIdByRecordDate(String recordDate);
	
	@Query(value="SELECT * FROM life_temperature WHERE userId = :userId AND LEFT(recordDate,6) = :month ORDER BY recordId DESC", nativeQuery=true)
	List<TemperatureEntity> findByUserMonth(String userId, String month);
	
	/*
	@Query(value="SELECT * FROM (SELECT * FROM life_temperature WHERE userId = :userId AND recordDate LIKE CONCAT(:recordDate,'%') ORDER BY recordDate DESC LIMIT 30) temperature ORDER BY recordDate", nativeQuery=true)
	List<TemperatureEntity> findByUserIdLikeRecordDate(String userId, String recordDate);
	*/
	@Query(value="SELECT * FROM life_temperature WHERE userId = :userId AND recordDate LIKE CONCAT(:recordDate,'%') ORDER BY recordDate", nativeQuery=true)
	List<TemperatureEntity> findByUserIdLikeRecordDate(String userId, String recordDate);
	
	@Query(value="SELECT userId FROM life_temperature GROUP BY userId", nativeQuery=true)
	List<String> getUserIdList();
}
