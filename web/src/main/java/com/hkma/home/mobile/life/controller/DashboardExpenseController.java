package com.hkma.home.mobile.life.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hkma.home.life.entity.ExpenseAccountGroupEntity;
import com.hkma.home.life.repository.ExpenseAccountGroupRepository;

@Controller("MobileLifeDashboardExpense")
@RequestMapping("/m/life/dashboard/expense")
public class DashboardExpenseController {
	@Autowired
	private ExpenseAccountGroupRepository expenseAccountGroupRepository;
	
	@GetMapping("/view")
	public String view(
			@RequestParam(required=false, value="userId") String userId,
			@RequestParam(required=false, value="groupId") String groupId,
			Model model){
		model.addAttribute("userId", userId);
		model.addAttribute("groupId", groupId);
		
		return "mobile/life/dashboard/expense/view";
	}
	
	@GetMapping("/search")
	public String search(
			Principal principal,
			Model model){
		String userId, userIdModel, groupId;
		List<Map<String,Object>> groupList = new ArrayList<>();
		
		if (principal != null){
			userId = principal.getName();
			userIdModel = "";
			groupId = "";
		}else {
			userId = "mia";
			userIdModel = "mia";
			groupId = "001";
		}
		
		List<ExpenseAccountGroupEntity> expenseAccountGroupList = expenseAccountGroupRepository.findByUserId(userId);
		expenseAccountGroupList.forEach(expenseAccountGroup ->{
			Map<String,Object> map = new HashMap<>();
			
			map.put("userId", expenseAccountGroup.getUserId());
			map.put("groupId", expenseAccountGroup.getGroupId());
			map.put("groupName", expenseAccountGroup.getGroupName());
			
			groupList.add(map);
		});
		
		model.addAttribute("userId", userIdModel);
		model.addAttribute("groupId", groupId);
		model.addAttribute("groupList", groupList);
		
		return "mobile/life/dashboard/expense/search";
	}
}
