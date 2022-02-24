package com.hkma.home.asset.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hkma.home.asset.entity.GoalEntity;

public interface GoalRepository extends JpaRepository<GoalEntity,String>{
	void deleteByAccountUserIdAndAccountId(String accountUserId, String accountId);
	
	Optional<GoalEntity> findByAccountUserIdAndAccountId(String accountUserId, String accountId);
	
	@Query(value="SELECT * FROM asset_goal "
			+ "WHERE ("
			+ 	"EXISTS (SELECT 1 FROM bank_authority WHERE userId = :userId AND bank_authority.accountUserId = asset_goal.accountUserId AND bank_authority.accountId = asset_goal.accountId) "
			+ ")", nativeQuery=true)
	List<GoalEntity> findByUserId(String userId);
	
	@Query(value="SELECT * FROM asset_goal "
			+ "WHERE accountUserId = :accountUserId "
			+ "AND accountId = :accountId "
			+ "AND ("
			+ 	"EXISTS (SELECT 1 FROM bank_authority WHERE userId = :userId AND bank_authority.accountUserId = asset_goal.accountUserId AND bank_authority.accountId = asset_goal.accountId) "
			+ ")", nativeQuery=true)
	List<GoalEntity> findByUserIdAndAccountUserIdAndAccountId(String userId, String accountUserId, String accountId);
}
