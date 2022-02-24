package com.hkma.home.system.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.hkma.home.user.entity.UserAccountEntity;
import com.hkma.home.user.service.UserAccountService;

@Service
public class SystemUserDetailsService implements UserDetailsService{
	@Autowired
	private UserAccountService service;

	@Override
	public UserDetails loadUserByUsername(String userId){
		Optional<UserAccountEntity> userAccountOptional = service.findByUserId(userId);
		
		if (userAccountOptional.isPresent()) {
			String password = service.getPasswordbyUserId(userId);

            return new User(userId, password, Collections.emptyList());
		}else {
			return null;
		}
	}
}
