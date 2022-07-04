package com.hkma.home.mobile.asset.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.PersistenceUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hkma.home.asset.entity.GoalEntity;
import com.hkma.home.asset.repository.GoalRepository;
import com.hkma.home.bank.entity.AuthorityEntity;
import com.hkma.home.bank.entity.BankAccountEntity;
import com.hkma.home.bank.entity.BankEntity;
import com.hkma.home.bank.repository.AuthorityRepository;
import com.hkma.home.bank.repository.BankAccountRepository;

@Controller("MobileAssetDashboardInvestmentView")
@RequestMapping("/m/asset/dashboard")
public class DashboardInvestmentController {	
	@Autowired
	private AuthorityRepository authorityRepository;
	
	@Autowired
	private BankAccountRepository bankAccountRepository;
	
	@Autowired
	private GoalRepository goalRepository;
	
	@GetMapping("/investment/view")
	public String view(
			@RequestParam(required=false, value="accountUserId") String accountUserId,
			@RequestParam(required=false, value="accountId") String accountId,
			Model model){
		
		model.addAttribute("accountUserId", accountUserId);
		model.addAttribute("accountId", accountId);
		
		return "mobile/asset/dashboard/investment/view";
	}
	
	@GetMapping("/investment/search")
	public String search(
			Principal principal,
			Model model){
		String userId, accountUserId, accountId;
		List<Map<String,Object>> accountList = new ArrayList<>();
		
		if (principal != null){
			userId = principal.getName();
			accountUserId = "";
			accountId = "";
		}else {
			userId = "mia";
			accountUserId = "mia";
			accountId = "001";
		}
		
		//證券帳戶
		List<AuthorityEntity> authorityList = authorityRepository.findByUserId(userId);
		authorityList.forEach(authority ->{
			String authorityAccountUserId = authority.getAccountUserId();
			String authorityAccountId = authority.getAccountId();
			
			Optional<BankAccountEntity> bankAccountOptional = bankAccountRepository.findByUserIdAndId(authorityAccountUserId, authorityAccountId);
			
			if(bankAccountOptional.isPresent()) {
				BankAccountEntity bankAccountEntity = bankAccountOptional.get();
				
				String memo = bankAccountEntity.getMemo();
				String isSecurities = bankAccountEntity.getIsSecurities();
				
				if(bankAccountEntity.getBank() != null) {
					BankEntity bank = bankAccountEntity.getBank();
					memo = bank.getName() + " - " + memo;
				};
				
				if(isSecurities != null && isSecurities.equals("1")) {
					Map<String,Object> map = new HashMap<>();
					
					map.put("accountUserId", authorityAccountUserId);
					map.put("accountId", authorityAccountId);
					map.put("memo", memo);
					
					accountList.add(map);
				}
			}
		});
		
		model.addAttribute("accountUserId", accountUserId);
		model.addAttribute("accountId", accountId);
		model.addAttribute("accountList", accountList);
		
		return "mobile/asset/dashboard/investment/search";
	}
	
	@GetMapping("/investment2/view")
	public String view2(
			@RequestParam(required=false, value="accountUserId") String accountUserId,
			@RequestParam(required=false, value="accountId") String accountId,
			@RequestParam(required=false, defaultValue="1000000", value="goal") String goal_param,
			Model model){
		if(accountUserId == null || accountUserId.equals("")) {
			accountUserId = "mia";
		}
		
		double goal = 0;
		
		try{
            goal = Double.parseDouble(goal_param);
        }catch(NullPointerException|NumberFormatException ex){
        }
		
		model.addAttribute("accountUserId", accountUserId);
		model.addAttribute("accountId", accountId);
		model.addAttribute("goal", goal);
		
		return "mobile/asset/dashboard/investment2/view";
	}
	
	@GetMapping("/investment2/search")
	public String search2(
			Principal principal,
			Model model){
		double goal;
		String userId, accountUserId, accountId;
		List<Map<String,Object>> accountList = new ArrayList<>();
		
		if (principal != null){
			userId = principal.getName();
			accountUserId = "";
			accountId = "";
		}else {
			userId = "mia";
			accountUserId = "mia";
			accountId = "001";
		}
		
		//證券帳戶
		List<AuthorityEntity> authorityList = authorityRepository.findByUserId(userId);
		authorityList.forEach(authority ->{
			String authorityAccountUserId = authority.getAccountUserId();
			String authorityAccountId = authority.getAccountId();
			
			Optional<BankAccountEntity> bankAccountOptional = bankAccountRepository.findByUserIdAndId(authorityAccountUserId, authorityAccountId);
			
			if(bankAccountOptional.isPresent()) {
				BankAccountEntity bankAccountEntity = bankAccountOptional.get();
				
				String memo = bankAccountEntity.getMemo();
				String isSecurities = bankAccountEntity.getIsSecurities();
				
				if(bankAccountEntity.getBank() != null) {
					BankEntity bank = bankAccountEntity.getBank();
					memo = bank.getName() + " - " + memo;
				};
				
				if(isSecurities != null && isSecurities.equals("1")) {
					double goal_set; 
					
					Optional<GoalEntity> goalOptional = goalRepository.findByAccountUserIdAndAccountId(authorityAccountUserId, authorityAccountId);
					
					if(goalOptional.isPresent()) {
						goal_set = goalOptional.get().getAmount();
					}else {
						goal_set = (double)1000000; 
					}
					
					Map<String,Object> map = new HashMap<>();
					
					map.put("accountUserId", authorityAccountUserId);
					map.put("accountId", authorityAccountId);
					map.put("memo", memo);
					map.put("goal", (int)goal_set);
					
					accountList.add(map);
				}
			}
		});
		
		Optional<GoalEntity> goalOptional = goalRepository.findByAccountUserIdAndAccountId(accountUserId, accountId);
		
		if(goalOptional.isPresent()) {
			goal = goalOptional.get().getAmount();
		}else {
			goal = (double)1000000; 
		}
		
		model.addAttribute("accountUserId", accountUserId);
		model.addAttribute("accountId", accountId);
		model.addAttribute("accountList", accountList);
		model.addAttribute("goal", (int)goal);
		
		return "mobile/asset/dashboard/investment2/search";
	}
}
