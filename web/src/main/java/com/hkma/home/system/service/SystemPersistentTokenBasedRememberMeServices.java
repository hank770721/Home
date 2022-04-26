package com.hkma.home.system.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.log.LogMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;

import com.hkma.home.system.repository.SystemPersistentTokenRepository;
import com.hkma.home.system.repository.impl.SystemInMemoryTokenRepositoryImpl;

public class SystemPersistentTokenBasedRememberMeServices extends PersistentTokenBasedRememberMeServices{
	private SystemPersistentTokenRepository tokenRepository = new SystemInMemoryTokenRepositoryImpl();

	public SystemPersistentTokenBasedRememberMeServices(String key, UserDetailsService userDetailsService,
			SystemPersistentTokenRepository tokenRepository) {
		super(key, userDetailsService, tokenRepository);
		
		this.tokenRepository = tokenRepository;
	}

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		//super.logout(request, response, authentication);
		this.logger.debug(LogMessage
				.of(() -> "Logout of user " + ((authentication != null) ? authentication.getName() : "Unknown")));
		cancelCookie(request, response);
		
		if (authentication != null) {
			//this.tokenRepository.removeUserTokens(authentication.getName());
			
			String rememberMeCookie = extractRememberMeCookie(request);
		    if (rememberMeCookie == null) {
		        return;
		    }
		    String[] cookieTokens = decodeCookie(rememberMeCookie);
		    if (cookieTokens.length == 2){
		        String seriesId = cookieTokens[0];
		        this.tokenRepository.removeTokens(seriesId);
		    }
		}
	}
}
