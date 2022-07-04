package com.hkma.home.mobile.system.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hkma.home.system.entity.AuthorityEntity;
import com.hkma.home.system.repository.SystemAuthorityRepository;

@Controller("MobileSystemInclude")
@RequestMapping("/m/system/include")
public class IncludeController {
	@Autowired
	private SystemAuthorityRepository systemAuthorityRepository;
	
	@PostMapping("/menu")
	public String profile(
			@RequestParam(required=false, value="stockId") String stockId,
			Principal principal,
			Model model) {
		boolean isLogin = false;
		List<Map<String,Object>> systemlist = new ArrayList<>();
		
		if (principal == null){
			isLogin = false;
		}else {
			isLogin = true;
			String userId = principal.getName();
			
			List<AuthorityEntity> authorityList = systemAuthorityRepository.findByUserIdOrderByOrderNumber(userId);
			
			authorityList.forEach(authority ->{
				boolean isFindSystem = false;
				Map<String,Object> systemMap = null;
				Map<String,Object> processMap = null;
				
				for (int i=0; i<systemlist.size(); i++) {					
					if (systemlist.get(i).get("systemId").equals(authority.getSystemId())) {
						isFindSystem = true;
						systemMap = systemlist.get(i);
					}
				}
				
				if (!isFindSystem) {
					systemMap = new HashMap<>();
					
					systemMap.put("systemId", authority.getSystemId());
					systemMap.put("systemName", authority.getSystemName());
					systemMap.put("processList", new ArrayList<Map<String,Object>>());
					
					systemlist.add(systemMap);
				}
				
				processMap = new HashMap<>();
				
				processMap.put("modelId", authority.getModelId());
				processMap.put("processId", authority.getProcessId());
				processMap.put("processName", authority.getProcessName());
				
				if (authority.getModelId().equals("record")) {
					processMap.put("action", "index");
				} else {
					processMap.put("action", "search");
				}
				
				((List)systemMap.get("processList")).add(processMap);
			});
		}
		
		model.addAttribute("isLogin", isLogin);
		model.addAttribute("list", systemlist);
		
		return "mobile/system/include/menu";
	}
}
