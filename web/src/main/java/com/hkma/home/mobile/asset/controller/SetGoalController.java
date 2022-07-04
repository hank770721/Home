package com.hkma.home.mobile.asset.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hkma.home.asset.entity.GoalEntity;
import com.hkma.home.asset.repository.GoalRepository;
import com.hkma.home.bank.entity.AuthorityEntity;
import com.hkma.home.bank.entity.BankAccountEntity;
import com.hkma.home.bank.entity.BankEntity;
import com.hkma.home.bank.repository.AuthorityRepository;
import com.hkma.home.bank.repository.BankAccountRepository;

@Controller("MobileAssetSetGoal")
@RequestMapping("/m/asset/set/goal")
public class SetGoalController {
	@Autowired
	private GoalRepository goalRepository;
	
	@Autowired
	private AuthorityRepository authorityRepository;
	
	@Autowired
	private BankAccountRepository bankAccountRepository;
	
	@GetMapping({"/","/index"})
	public String indexGoalGet(
			Principal principal,
			Model model){		
		String userId;
		List<Map<String, Object>> list = new ArrayList<>();
		
		if (principal != null) {
			userId = principal.getName();
		}else {
			userId = "mia";
		}
		
		List<GoalEntity> goalList = goalRepository.findByUserId(userId);
		
		goalList.forEach(goal -> {
			String accountUserId, accountId;
			
			accountUserId = goal.getAccountUserId();
			accountId = goal.getAccountId();
			
			Optional<BankAccountEntity> bankAccountOptional = bankAccountRepository.findByUserIdAndId(accountUserId, accountId);
			
			if(bankAccountOptional.isPresent()) {
				Map<String, Object> map = new HashMap<>();
				
				map.put("accountUserId", accountUserId);
				map.put("accountId", accountId);
				map.put("memo", bankAccountOptional.get().getMemo());
				
				list.add(map);
			}
		});
		
		model.addAttribute("list", list);
		
		return "mobile/asset/set/goal/index";
	}
	
	@PostMapping("/index")
	public String indexAssetRecordPost(
			@RequestParam(required=false, value="accountUserId") String accountUserId,
			@RequestParam(required=false, value="accountId") String accountId,
			Principal principal,
			Model model){		
		String userId;
		List<Map<String, Object>> list = new ArrayList<>();
		
		if (principal != null) {
			userId = principal.getName();
		}else {
			userId = "mia";
		}
		
		List<GoalEntity> goalList = goalRepository.findByUserIdAndAccountUserIdAndAccountId(userId, accountUserId, accountId);
		
		goalList.forEach(goal -> {
			Optional<BankAccountEntity> bankAccountOptional = bankAccountRepository.findByUserIdAndId(accountUserId, accountId);
			
			if(bankAccountOptional.isPresent()) {
				Map<String, Object> map = new HashMap<>();
				
				map.put("accountUserId", accountUserId);
				map.put("accountId", accountId);
				map.put("memo", bankAccountOptional.get().getMemo());
				
				list.add(map);
			}
		});
		
		model.addAttribute("list", list);
		
		return "mobile/asset/set/goal/index";
	}
	
	@GetMapping("/new")
	public String newAssetRecordGet(
			@ModelAttribute("goal") GoalEntity goal,
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
		List<AuthorityEntity> authorityList = authorityRepository.findByUserIdOrderByOrderNumberAsc(userId);
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
		
		goal.setAccountUserId(accountUserId);
		goal.setAccountId(accountId);
		
		model.addAttribute("accountList", accountList);
		
		return "mobile/asset/set/goal/new";
	}
	
	@PostMapping("/new")
	public String newAssetRecordPost(
			@ModelAttribute("goal") @Valid GoalEntity goal,
			BindingResult bindingResult,
			Principal principal,
			Model model){
		boolean isError = false;
		String accountUserIdError = null, accountIdError = null;
		
		String accountUserId = goal.getAccountUserId();
		String accountId = goal.getAccountId();
		Double amount = goal.getAmount();
		
		if (bindingResult.hasErrors()) {
			isError = true;
		}
		
		if(accountUserId.equals("")) {
			accountUserIdError = "未輸入";
			isError = true;
		}
		
		if(accountId.equals("")) {
			accountIdError = "未輸入";
			isError = true;
		}
		
		if(!isError) {
			if(goalRepository.findByAccountUserIdAndAccountId(accountUserId, accountId).isPresent()) {
				accountUserIdError = "此帳號已建立";
				isError = true;
			}
		}
		
		if (isError) {
			model.addAttribute("accountUserIdError", accountUserIdError);
			model.addAttribute("accountIdError", accountIdError);
			
			if(amount != null) {
				model.addAttribute("amount", (int)((double)amount));
			}else {
				model.addAttribute("amount", amount);
			}
			
			String userId;
			List<Map<String,Object>> accountList = new ArrayList<>();
			
			if (principal != null){
				userId = principal.getName();
			}else {
				userId = "mia";
			}
			
			//證券帳戶
			List<AuthorityEntity> authorityList = authorityRepository.findByUserIdOrderByOrderNumberAsc(userId);
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
			
			model.addAttribute("accountList", accountList);
			
			return "mobile/asset/set/goal/new";
		}
		
		goalRepository.save(goal);
		
		return "redirect:/m/asset/set/goal/index";
	}

	@GetMapping("/view/{accountUserId}&{accountId}")
	//@GetMapping("/view/accountUserId={accountUserId}&accountId={accountId}")
	public String viewAssetRecordGet(
			@PathVariable("accountUserId") String accountUserId,
			@PathVariable("accountId") String accountId,
			//@RequestParam(required=false, value="accountUserId") String accountUserId,
			//@RequestParam(required=false, value="accountId") String accountId,
			Principal principal,
			Model model){
		Optional<GoalEntity> optional = goalRepository.findByAccountUserIdAndAccountId(accountUserId,accountId);
		
		if(optional.isPresent()) {
			GoalEntity goal = optional.get();
			
			String userId;
			List<Map<String,Object>> accountList = new ArrayList<>();
			
			if (principal != null){
				userId = principal.getName();
			}else {
				userId = "mia";
			}
			
			//證券帳戶
			List<AuthorityEntity> authorityList = authorityRepository.findByUserIdOrderByOrderNumberAsc(userId);
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
			
			model.addAttribute("goal", goal);
			model.addAttribute("amount", (int)((double)goal.getAmount()));
			model.addAttribute("accountList", accountList);
			
			return "mobile/asset/set/goal/view";
		}else {
			return "redirect:/m/asset/set/goal/index";
		}
	}

	@PutMapping("/view/{accountUserId}&{accountId}")
    public String viewAssetRecordPut(
    		@PathVariable("accountUserId") String accountUserId,
			@PathVariable("accountId") String accountId,
    		@ModelAttribute("goal") @Valid GoalEntity goal,
    		BindingResult bindingResult,
			Principal principal,
    		Model model){
		if(!goal.getAccountUserId().equals(accountUserId) || !goal.getAccountId().equals(accountId)) {
			return "redirect:/m/asset/set/goal/index";
		}
		
		boolean isError = false;
		String accountUserIdError = null, accountIdError = null;
		
		Double amount = goal.getAmount();
		
		if(bindingResult.hasErrors()) {
			isError = true;
		}
		
		if(accountUserId.equals("")) {
			accountUserIdError = "未輸入";
			isError = true;
		}
		
		if(accountId.equals("")) {
			accountIdError = "未輸入";
			isError = true;
		}
		
		if(isError) {
			model.addAttribute("accountUserIdError", accountUserIdError);
			model.addAttribute("accountIdError", accountIdError);
			
			if(amount != null) {
				model.addAttribute("amount", (int)((double)amount));
			}else {
				model.addAttribute("amount", amount);
			}
			
			return "mobile/asset/set/goal/view";
		}
		
		if (principal != null){
			goal.setUpdateUserId(principal.getName());
		}
		
		goalRepository.save(goal);
		
		return "redirect:/m/asset/set/goal/index";
    }
	
	@DeleteMapping("/view/{accountUserId}&{accountId}")
    public String viewAssetRecordDelete(
    		@PathVariable("accountUserId") String accountUserId,
			@PathVariable("accountId") String accountId){    			
		goalRepository.deleteByAccountUserIdAndAccountId(accountUserId,accountId);
		
		return "redirect:/m/asset/set/goal/index";
    }
	
	@GetMapping("/search")
	public String searchAssetRecordRecord(
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
		List<AuthorityEntity> authorityList = authorityRepository.findByUserIdOrderByOrderNumberAsc(userId);
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
		
		return "mobile/asset/set/goal/search";
	}
}
