package com.hkma.home.mobile.bank.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hkma.home.bank.entity.AccountGroupEntity;
import com.hkma.home.bank.entity.AuthorityEntity;
import com.hkma.home.bank.entity.BankAccountEntity;
import com.hkma.home.bank.entity.BankEntity;
import com.hkma.home.bank.repository.AccountGroupRepository;
import com.hkma.home.bank.repository.AuthorityRepository;
import com.hkma.home.bank.repository.BankAccountRepository;

@Controller("MobileBankPopup")
@RequestMapping("/m/bank/popup")
public class Popup {
	@Autowired
	private AuthorityRepository authorityRepository;
	
	@Autowired
	private BankAccountRepository bankAccountRepository;
	
	@Autowired
	private AccountGroupRepository accountGroupRepository;
	
	@PostMapping("/account")
	public String accunt(
			Principal principal,
			Model model) {
		List<Map<String,Object>> accountList = new ArrayList<>();
		
		if (principal != null){
			String userId = principal.getName();
			
			List<AuthorityEntity> authorityList = authorityRepository.findByUserIdOrderByOrderNumberAsc(userId);
			authorityList.forEach(authority ->{
				String authorityAccountUserId = authority.getAccountUserId();
				String authorityAccountId = authority.getAccountId();
				
				Optional<BankAccountEntity> bankAccountOptional = bankAccountRepository.findByUserIdAndId(authorityAccountUserId, authorityAccountId);
				
				if(bankAccountOptional.isPresent()) {
					BankAccountEntity bankAccountEntity = bankAccountOptional.get();
					String memo = bankAccountEntity.getMemo();
					String isBankAccount = bankAccountEntity.getIsBankAccount();
					
					if(bankAccountEntity.getBank() != null) {
						BankEntity bank = bankAccountEntity.getBank();
						memo = bank.getName() + " - " + memo;
					};
					
					if(isBankAccount != null && isBankAccount.equals("1")) {
						Map<String,Object> map = new HashMap<>();
						
						map.put("accountUserId", authorityAccountUserId);
						map.put("accountId", authorityAccountId);
						map.put("memo", memo);
						
						accountList.add(map);
					}
				}
			});
		}
		
		model.addAttribute("accountList", accountList);
		
		return "mobile/bank/popup/account";
	}
	
	@PostMapping("/accountGroup")
	public String accuntGroup(
			Principal principal,
			Model model) {
		List<Map<String,Object>> groupList = new ArrayList<>();
		
		if (principal != null){
			String userId = principal.getName();
		
			List<AccountGroupEntity> accountGroupList = accountGroupRepository.findByUserId(userId);
			accountGroupList.forEach(accountGroup ->{
				Map<String,Object> map = new HashMap<>();
				
				map.put("userId", accountGroup.getUserId());
				map.put("groupId", accountGroup.getGroupId());
				map.put("groupName", accountGroup.getGroupName());
				
				groupList.add(map);
			});
		}
		
		model.addAttribute("groupList", groupList);
		
		return "mobile/bank/popup/accountgroup";
	}
}
