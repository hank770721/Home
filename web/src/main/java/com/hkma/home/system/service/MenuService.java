package com.hkma.home.system.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hkma.home.system.entity.AuthorityEntity;
import com.hkma.home.system.repository.SystemAuthorityRepository;

@Service
public class MenuService {
	@Autowired
	private SystemAuthorityRepository systemAuthorityRepository;
	
	public Map<String,Object> getMenu(String userId) {
		Map<String,Object> menuMap = new HashMap<>();
		
		if (userId == null){
			menuMap.put("loginHref", "/login");
			menuMap.put("loginText", "登入");
			menuMap.put("systemList", null);
		}else {
			List<Map<String,Object>> systemList = new ArrayList<>();
			
			List<AuthorityEntity> authorityList = systemAuthorityRepository.findByUserIdOrderByOrderNumber(userId);
			
			authorityList.forEach(authority ->{
				boolean isFindSystem = false;
				Map<String,Object> systemMap = null;
				Map<String,Object> processMap = null;
				
				for (int i=0; i<systemList.size(); i++) {					
					if (systemList.get(i).get("systemId").equals(authority.getSystemId())) {
						isFindSystem = true;
						systemMap = systemList.get(i);
					}
				}
				
				if (!isFindSystem) {
					systemMap = new HashMap<>();
					
					systemMap.put("systemId", authority.getSystemId());
					systemMap.put("systemName", authority.getSystemName());
					systemMap.put("processList", new ArrayList<Map<String,Object>>());
					
					systemList.add(systemMap);
				}
				
				List<Map<String,Object>> processList = (List)systemMap.get("processList");
				
				processMap = new HashMap<>();
				
				processMap.put("modelId", authority.getModelId());
				processMap.put("processId", authority.getProcessId());
				processMap.put("processName", authority.getProcessName());
				processMap.put("href", authority.getHref());
				
				processList.add(processMap);
			});
			
			menuMap.put("loginHref", "/logout");
			menuMap.put("loginText", "登出");
			menuMap.put("systemList", systemList);
		}
		
		return menuMap;
    }
}
