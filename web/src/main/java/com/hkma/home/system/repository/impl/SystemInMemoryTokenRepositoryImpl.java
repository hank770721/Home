package com.hkma.home.system.repository.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

import com.hkma.home.system.repository.SystemPersistentTokenRepository;

public class SystemInMemoryTokenRepositoryImpl extends InMemoryTokenRepositoryImpl implements SystemPersistentTokenRepository {
	private final Map<String, PersistentRememberMeToken> seriesTokens = new HashMap<>();

	@Override
	public synchronized void removeTokens(String seriesId) {
		Iterator<String> series = this.seriesTokens.keySet().iterator();
		while (series.hasNext()) {
			if (series.next().equals(seriesId)) {
				series.remove();
			}
		}
	}

}
