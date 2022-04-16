package com.hkma.home.mobile.bank.controller;

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

import com.hkma.home.bank.entity.AccountGroupEntity;
import com.hkma.home.bank.entity.BankAccountListEntity;
import com.hkma.home.bank.repository.AccountGroupRepository;
import com.hkma.home.bank.repository.BankAccountListRepository;

@Controller("MobileBankPopup")
@RequestMapping("/m/bank/popup")
public class PopupController {
	@Autowired
	private BankAccountListRepository bankAccountListRepository;
	
	@Autowired
	private AccountGroupRepository accountGroupRepository;
	
	@PostMapping("/account")
	public String accunt(
			@RequestParam(required=false, value="isBankAccount") Boolean isBankAccount,
			@RequestParam(required=false, value="isSecurities") Boolean isSecurities,
			Principal principal,
			Model model) {
		List<Map<String,Object>> list = new ArrayList<>();
		
		if (principal != null){
			String userId = principal.getName();
			
			List<BankAccountListEntity> bankAccountList = bankAccountListRepository.findByUserIdAndIsBankAccountAndIsSecurities(userId, isBankAccount, isSecurities);
			bankAccountList.forEach(bankAccount ->{
				String memo = bankAccount.getMemo();
				String bankName = bankAccount.getBankName();
				
				if (bankName != null) memo = bankName + " - " + memo;
				
				Map<String,Object> map = new HashMap<>();
				
				map.put("accountUserId", bankAccount.getUserId());
				map.put("accountId", bankAccount.getId());
				map.put("memo", memo);
				
				list.add(map);
			});
		}
		
		model.addAttribute("list", list);
		
		return "mobile/bank/popup/account";
	}
	
	@PostMapping("/accountGroup")
	public String accuntGroup(
			Principal principal,
			Model model) {
		List<Map<String,Object>> list = new ArrayList<>();
		
		if (principal != null){
			String userId = principal.getName();
		
			List<AccountGroupEntity> accountGroupList = accountGroupRepository.findByUserId(userId);
			accountGroupList.forEach(accountGroup ->{
				Map<String,Object> map = new HashMap<>();
				
				map.put("userId", accountGroup.getUserId());
				map.put("groupId", accountGroup.getGroupId());
				map.put("groupName", accountGroup.getGroupName());
				
				list.add(map);
			});
		}
		
		model.addAttribute("list", list);
		
		return "mobile/bank/popup/accountgroup";
	}
}
