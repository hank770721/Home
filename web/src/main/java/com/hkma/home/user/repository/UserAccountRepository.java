package com.hkma.home.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.hkma.home.user.entity.UserAccountEntity;

public interface UserAccountRepository extends JpaRepository<UserAccountEntity,String>, JpaSpecificationExecutor<UserAccountEntity>{
	Optional<UserAccountEntity> findByUserId(String userId);
	
	@Query(value="SELECT password FROM user_account WHERE userId = :userId", nativeQuery=true)
	String getPasswordbyUserId(String userId);
}
