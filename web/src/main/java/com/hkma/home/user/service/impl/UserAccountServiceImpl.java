package com.hkma.home.user.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.hkma.home.user.entity.UserAccountEntity;
import com.hkma.home.user.repository.UserAccountRepository;
import com.hkma.home.user.service.UserAccountService;

@Service
public class UserAccountServiceImpl implements UserAccountService{
	@Autowired
	private UserAccountRepository userAccountRepository;
	
	@Override
	public Optional<UserAccountEntity> findByUserId(String userId) {
		return userAccountRepository.findByUserId(userId);
	}
	
	@Override
	public String getPasswordByUserId(String userId) {
        return new BCryptPasswordEncoder().encode(userAccountRepository.getPasswordByUserId(userId));
    }
}