package com.hkma.home.mobile.bank.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hkma.home.bank.entity.AuthorityEntity;
import com.hkma.home.bank.entity.BankAccountEntity;
import com.hkma.home.bank.entity.BankEntity;
import com.hkma.home.bank.repository.AuthorityRepository;
import com.hkma.home.bank.repository.BankAccountRepository;

@Controller("MobileBankTableCredit")
@RequestMapping("/m/bank/table/credit")
public class TableCreditController {	
	@Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Autowired
	private AuthorityRepository authorityRepository;
	
	@Autowired
	private BankAccountRepository bankAccountRepository;
	
	@GetMapping("/view")
	public String view(
			@RequestParam(required=false, value = "accountUserId") String accountUserId,
			@RequestParam(required=false, value = "accountId") String accountId,
			Principal principal,
			Model model){
		String userId;
		
		if (principal != null) {
			userId = principal.getName();
		}else {
			userId = "mia";
		}
		
		String sql;
		List<Map<String,Object>> list = new ArrayList<>();
		
		sql = "call home.sp_bank_credit(:userId, :accountUserId, :accountId);";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("userId", userId);
		params.addValue("accountUserId", accountUserId);
		params.addValue("accountId", accountId);
		
		list = namedParameterJdbcTemplate.queryForList(sql, params);

        model.addAttribute("list", list);
		
		return "mobile/bank/table/credit/view";
	}
	
	@GetMapping("/search")
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
		
		List<AuthorityEntity> authorityList = authorityRepository.findByUserIdOrderByOrderNumberAsc(userId);
		authorityList.forEach(authority ->{
			String authorityAccountUserId = authority.getAccountUserId();
			String authorityAccountId = authority.getAccountId();
			
			Optional<BankAccountEntity> bankAccountOptional = bankAccountRepository.findByUserIdAndId(authorityAccountUserId, authorityAccountId);
			
			if(bankAccountOptional.isPresent()) {
				BankAccountEntity bankAccountEntity = bankAccountOptional.get();
				String memo = bankAccountEntity.getMemo();
				String isCreditCard = bankAccountEntity.getIsCreditCard();
				
				if(bankAccountEntity.getBank() != null) {
					BankEntity bank = bankAccountEntity.getBank();
					memo = bank.getName() + " - " + memo;
				};
				
				if(isCreditCard != null && isCreditCard.equals("1")) {
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
		
		return "mobile/bank/table/credit/search";
	}
}
