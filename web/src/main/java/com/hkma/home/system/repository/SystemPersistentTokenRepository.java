package com.hkma.home.system.repository;

import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

public interface SystemPersistentTokenRepository extends PersistentTokenRepository{
	void removeTokens(String seriesId);
}
