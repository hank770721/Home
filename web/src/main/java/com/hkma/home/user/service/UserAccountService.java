package com.hkma.home.user.service;

import java.util.Optional;

import com.hkma.home.user.entity.UserAccountEntity;

public interface UserAccountService {
	public Optional<UserAccountEntity> findByUserId(String userId);
	
	public String getPasswordbyUserId(String userId);
}
