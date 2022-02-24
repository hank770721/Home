package com.hkma.home.mobile.life.controller;

import java.security.Principal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import com.hkma.home.bank.entity.AuthorityEntity;
import com.hkma.home.bank.entity.BankAccountEntity;
import com.hkma.home.bank.entity.BankEntity;
import com.hkma.home.bank.repository.AuthorityRepository;
import com.hkma.home.bank.repository.BankAccountRepository;
import com.hkma.home.life.entity.ExpenseEntity;
import com.hkma.home.life.repository.ExpenseRepository;

@Controller("MobileLifeExpense")
@RequestMapping("/m/life/record/expense")
public class ExpenseController {
	@Autowired
	private ExpenseRepository expenseRepository;
	
	@Autowired
	private AuthorityRepository authorityRepository;
	
	@Autowired
	private BankAccountRepository bankAccountRepository;
	
	@GetMapping({"/","/index"})
	public String indexExpenseGet(
			Principal principal,
			Model model){
		SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");
		
		String userId;
		String month = monthFormat.format(new Date());
		
		if (principal != null) {
			userId = principal.getName();
		}else {
			userId = "mia";
		}

		DecimalFormat decimalFormat = new DecimalFormat("###,###");
		
		List<Map<String, Object>> list = new ArrayList<>();
		
		List<ExpenseEntity> expenseList = expenseRepository.findByUserIdMonthOrderByRecordDateDesc(userId, month);
		
		expenseList.forEach(expense -> {
			double amount;
			String transMode, transModeStr = "", recordDate, memo = "";
			Map<String, Object> map = new HashMap<>();
			
			recordDate = expense.getRecordDate();
			amount = expense.getAmount();
			transMode = expense.getTransMode();
			
			switch (transMode) {
				case "1":
					transModeStr = "收入";
					break;
				case "2":
					transModeStr = "支出";
					break;
			}
			
			recordDate = recordDate.substring(0,4) + "/" + recordDate.substring(4,6) + "/" + recordDate.substring(6,8);
			memo = expense.getMemo();
			
			map.put("recordId", expense.getRecordId());
			map.put("transMode", transMode);
			map.put("transModeStr", transModeStr);
			map.put("recordDate", recordDate);
			map.put("amount", decimalFormat.format(amount));
			map.put("memo", memo);
			
			list.add(map);
		});
		
		model.addAttribute("list", list);
		
		return "mobile/life/record/expense/index";
	}
	
	@PostMapping("/index")
	public String indexAssetRecordPost(
			@RequestParam(required=false, value="accountUserId") String accountUserId,
			@RequestParam(required=false, value="accountId") String accountId,
			@RequestParam(required=false, value="month") String month,
			Principal principal,
			Model model){
		DecimalFormat decimalFormat = new DecimalFormat("###,###");
		
		String userId;
		List<Map<String, Object>> list = new ArrayList<>();
		
		if (principal != null) {
			userId = principal.getName();
		}else {
			userId = "mia";
		}
		
		List<ExpenseEntity> expenseList = expenseRepository.findByUserIdMonthAccountUserIdAccountIdOrderByRecordDateDesc(userId, month, accountUserId, accountId);
		
		expenseList.forEach(expense -> {
			double amount;
			String transMode, transModeStr = "", recordDate, memo = "";
			Map<String, Object> map = new HashMap<>();
			
			recordDate = expense.getRecordDate();
			amount = expense.getAmount();
			transMode = expense.getTransMode();
			
			switch (transMode) {
				case "1":
					transModeStr = "收入";
					break;
				case "2":
					transModeStr = "支出";
					break;
			}
			
			recordDate = recordDate.substring(0,4) + "/" + recordDate.substring(4,6) + "/" + recordDate.substring(6,8);
			
			memo = expense.getMemo();
			
			map.put("recordId", expense.getRecordId());
			map.put("transMode", transMode);
			map.put("transModeStr", transModeStr);
			map.put("recordDate", recordDate);
			map.put("amount", decimalFormat.format(amount));
			map.put("memo", memo);
			
			list.add(map);
		});
		
		model.addAttribute("list", list);
		
		return "mobile/life/record/expense/index";
	}
	
	@GetMapping("/new")
	public String newExpenseGet(
			@ModelAttribute("expense") ExpenseEntity expense,
			Principal principal,
			Model model){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		String recordDate = dateFormat.format(new Date());
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
				String isBankAccount = bankAccountEntity.getIsBankAccount();
				
				if(bankAccountEntity.getBank() != null) {
					BankEntity bank = bankAccountEntity.getBank();
					memo = bank.getName() + " - " + memo;
				};
				
				if(isBankAccount == null) {
					Map<String,Object> map = new HashMap<>();
					
					map.put("accountUserId", authorityAccountUserId);
					map.put("accountId", authorityAccountId);
					map.put("memo", memo);
					
					accountList.add(map);
				}
			}
		});
		
		expense.setRecordDate(recordDate);
		expense.setTransMode("1");
		expense.setAccountUserId(accountUserId);
		expense.setAccountId(accountId);
		
		model.addAttribute("accountList", accountList);
		
		return "mobile/life/record/expense/new";
	}
	
	@PostMapping("/new")
	public String newExpensePost(
			@ModelAttribute("expense") @Valid ExpenseEntity expense,
			BindingResult bindingResult,
			Principal principal,
			Model model){
		boolean isError = false;
		String accountUserIdError = null, accountIdError = null;
		
		String recordDate = expense.getRecordDate();
		String accountUserId = expense.getAccountUserId();
		String accountId = expense.getAccountId();
		Double amount = expense.getAmount();
		
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
			
			String userId;
			List<Map<String,Object>> accountList = new ArrayList<>();
			
			if (principal != null){
				userId = principal.getName();
			}else {
				userId = "mia";
			}
			
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
					
					if(isBankAccount == null) {
						Map<String,Object> map = new HashMap<>();
						
						map.put("accountUserId", authorityAccountUserId);
						map.put("accountId", authorityAccountId);
						map.put("memo", memo);
						
						accountList.add(map);
					}
				}
			});
			
			model.addAttribute("accountList", accountList);
			
			return "mobile/life/record/expense/new";
		}
		
		recordDate = recordDate.replaceAll("-","");
		expense.setRecordDate(recordDate);
		
		String recordId;
		Optional<String> recordIdOptional = expenseRepository.getMaxRecordIdByRecordDate(recordDate);
		
		if (!recordIdOptional.isPresent()) {
			recordId = recordDate + "0001";
		}else {
			recordId = recordIdOptional.get();
			recordId = recordDate + String.format("%04d", (Integer.parseInt(recordId.substring(8,12)) + 1) );
		}
		
		expense.setRecordId(recordId);

		if (principal != null){
			expense.setEnterUserId(principal.getName());
			expense.setUpdateUserId(principal.getName());
		}
		
		expenseRepository.save(expense);
		
		return "redirect:/m/life/record/expense/index";
	}

	@GetMapping("/view/{recordId}")
	public String viewExpenseGet(
			@PathVariable("recordId") String recordId,
			Principal principal,
			Model model){
		Optional<ExpenseEntity> expenseOptional = expenseRepository.findById(recordId);
		
		if(expenseOptional.isPresent()) {
			ExpenseEntity expense = expenseOptional.get();
			
			String recordDate, userId;
			List<Map<String,Object>> accountList = new ArrayList<>();
			
			recordDate = expense.getRecordDate();
			int amount = (int)((double)expense.getAmount());
			
			expense.setRecordDate(recordDate.substring(0,4) + "-" + recordDate.substring(4,6) + "-" + recordDate.substring(6,8));
			
			if (principal != null){
				userId = principal.getName();
			}else {
				userId = "mia";
			}
			
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
					
					if(isBankAccount == null) {
						Map<String,Object> map = new HashMap<>();
						
						map.put("accountUserId", authorityAccountUserId);
						map.put("accountId", authorityAccountId);
						map.put("memo", memo);
						
						accountList.add(map);
					}
				}
			});
			
			model.addAttribute("expense", expense);
			model.addAttribute("amount", amount);
			model.addAttribute("accountList", accountList);
			
			return "mobile/life/record/expense/view";
		}else {
			return "redirect:/m/life/record/expense/index";
		}
	}

	@PutMapping("/view/{recordId}")
    public String viewExpensePut(
    		@PathVariable("recordId") String recordId,
    		@ModelAttribute("expense") @Valid ExpenseEntity expense,
    		BindingResult bindingResult,
			Principal principal,
    		Model model){
		boolean isError = false;
		String accountUserIdError = null, accountIdError = null;
		
		String recordDate = expense.getRecordDate().replaceAll("-","");
		String accountUserId = expense.getAccountUserId();
		String accountId = expense.getAccountId();
		Double amount = expense.getAmount();
		
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
			String userId;
			List<Map<String,Object>> accountList = new ArrayList<>();
			
			if (principal != null){
				userId = principal.getName();
			}else {
				userId = "mia";
			}
			
			model.addAttribute("accountUserIdError", accountUserIdError);
			model.addAttribute("accountIdError", accountIdError);
			
			if(amount != null) {
				model.addAttribute("amount", (int)((double)amount));
			}else {
				model.addAttribute("amount", amount);
			}
			
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
					
					if(isBankAccount == null) {
						Map<String,Object> map = new HashMap<>();
						
						map.put("accountUserId", authorityAccountUserId);
						map.put("accountId", authorityAccountId);
						map.put("memo", memo);
						
						accountList.add(map);
					}
				}
			});
			
			model.addAttribute("accountList", accountList);
			
			return "mobile/asset/record/assetrecord/view";
		}
		
		//日期修改重取單號
		Optional<ExpenseEntity> expenseOptional = expenseRepository.findById(recordId);
		
		if(expenseOptional.isPresent()) {
			ExpenseEntity expense_old = expenseOptional.get();
			
			if(!expense_old.getRecordDate().equals(recordDate)) {
				expenseRepository.delete(expense_old);
				
				String recordId_new;
				Optional<String> recordIdOptional = expenseRepository.getMaxRecordIdByRecordDate(recordDate);
				
				if (!recordIdOptional.isPresent()) {
					recordId_new = recordDate + "0001";
				}else {
					recordId_new = recordIdOptional.get();
					recordId_new = recordDate + String.format("%04d", (Integer.parseInt(recordId_new.substring(8,12)) + 1) );
				}
				
				expense.setRecordId(recordId_new);
				
				if (principal != null){
					expense.setEnterUserId(principal.getName());
				}
			}
		}
		
		expense.setRecordDate(recordDate);
		
		if (principal != null){
			expense.setUpdateUserId(principal.getName());
		}
		
		expenseRepository.save(expense);
		
		return "redirect:/m/life/record/expense/index";
    }
	
	@DeleteMapping("/view/{recordId}")
    public String viewExpenseDelete(
    		@PathVariable("recordId") String recordId){		
		expenseRepository.deleteById(recordId);
		
		return "redirect:/m/life/record/expense/index";
    }
	
	@GetMapping("/search")
	public String searchExpenseRecord(
			Principal principal,
			Model model){
		SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");

		String userId, accountUserId, accountId;
		String month = monthFormat.format(new Date());
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
				String isBankAccount = bankAccountEntity.getIsBankAccount();
				
				if(bankAccountEntity.getBank() != null) {
					BankEntity bank = bankAccountEntity.getBank();
					memo = bank.getName() + " - " + memo;
				};
				
				if(isBankAccount == null) {
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
		model.addAttribute("month", month);
		
		return "mobile/life/record/expense/search";
	}
}
