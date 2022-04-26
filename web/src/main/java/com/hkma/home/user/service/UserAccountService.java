package com.hkma.home.user.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hkma.home.user.entity.UserAccountEntity;
import com.hkma.home.user.repository.UserAccountRepository;

@Service
public class UserAccountService {
	@Autowired
	private UserAccountRepository userAccountRepository;
	
	public Optional<UserAccountEntity> findByUserId(String userId) {
		return userAccountRepository.findByUserId(userId);
	}
	
	public String getPasswordByUserId(String userId) {
        //return new BCryptPasswordEncoder().encode(userAccountRepository.getPasswordByUserId(userId));
		return userAccountRepository.getPasswordByUserId(userId);
    }
}
