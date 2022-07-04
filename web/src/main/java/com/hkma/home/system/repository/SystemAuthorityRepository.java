package com.hkma.home.system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hkma.home.system.entity.AuthorityEntity;

public interface SystemAuthorityRepository extends JpaRepository<AuthorityEntity,Long>{
	@Query(value="SELECT system_authority.userId, system_authority.systemId, system_authority.modelId, system_authority.processId, system_system.name AS systemName, system_process.name AS processName, system_process.href "
			+ "FROM system_authority "
			+ "JOIN system_system ON system_system.id = system_authority.systemId "
			+ "JOIN system_process ON system_process.systemId = system_authority.systemId AND system_process.modelId = system_authority.modelId AND system_process.id = system_authority.processId "
			+ "WHERE system_authority.userId = :userId "
			+ "ORDER BY system_system.orderNumber, system_process.orderNumber", nativeQuery=true)
	List<AuthorityEntity> findByUserIdOrderByOrderNumber(String userId);
}
