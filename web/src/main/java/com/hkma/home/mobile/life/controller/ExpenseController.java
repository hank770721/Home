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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
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
	public String indexGet(
			Principal principal,
			Model model){
		List<Map<String, Object>> list = new ArrayList<>();
		
		if (principal == null) {
			model.addAttribute("list", list);
			
			return "mobile/life/record/expense/index";
		}else {
			SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");
			DecimalFormat decimalFormat = new DecimalFormat("###,###");
			
			String userId = principal.getName();
			
			String month = monthFormat.format(new Date());
			
			List<ExpenseEntity> expenseList = expenseRepository.findByUserIdMonthOrderByRecordDateDescRecordIdDesc(userId, month);
			
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
	}
	
	@PostMapping("/index")
	public String indexPost(
			@RequestParam(required=false, value="accountUserId") String accountUserId,
			@RequestParam(required=false, value="accountId") String accountId,
			@RequestParam(required=false, value="month") String month,
			Principal principal,
			Model model){
		List<Map<String, Object>> list = new ArrayList<>();
		
		if (principal == null) {
			model.addAttribute("list", list);
			
			return "mobile/life/record/expense/index";
		}else {
			DecimalFormat decimalFormat = new DecimalFormat("###,###");
			
			String userId = principal.getName();
			
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
	}
	
	@GetMapping("/new")
	public String newGet(
			@ModelAttribute("expense") ExpenseEntity expense,
			Principal principal,
			Model model){
		if (principal == null){
			return "redirect:/m/life/record/expense/index";
		}else {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			
			String userId = principal.getName();
			String recordDate = dateFormat.format(new Date());
			
			//帳戶
			List<Map<String,Object>> accountList = new ArrayList<>();
			List<AuthorityEntity> authorityList = authorityRepository.findByUserIdOrderByOrderNumberAsc(userId);
			authorityList.forEach(authority ->{
				String authorityAccountUserId = authority.getAccountUserId();
				String authorityAccountId = authority.getAccountId();
				
				Optional<BankAccountEntity> bankAccountOptional = bankAccountRepository.findByUserIdAndId(authorityAccountUserId, authorityAccountId);
				
				if(bankAccountOptional.isPresent()) {
					BankAccountEntity bankAccountEntity = bankAccountOptional.get();
					
					String memo = bankAccountEntity.getMemo();
					
					if(bankAccountEntity.getBank() != null) {
						BankEntity bank = bankAccountEntity.getBank();
						memo = bank.getName() + " - " + memo;
					};
					
					Map<String,Object> map = new HashMap<>();
					
					map.put("accountUserId", authorityAccountUserId);
					map.put("accountId", authorityAccountId);
					map.put("memo", memo);
					
					accountList.add(map);
				}
			});
			
			expense.setRecordDate(recordDate);
			//20220409 增加歸屬日期
			expense.setVestingDate(recordDate);
			expense.setTransMode("1");		
			
			model.addAttribute("accountList", accountList);
			
			return "mobile/life/record/expense/new";
		}
	}
	
	@Transactional(isolation=Isolation.SERIALIZABLE)
	@PostMapping("/new")
	public String newPost(
			@ModelAttribute("expense") @Valid ExpenseEntity expense,
			BindingResult bindingResult,
			Principal principal,
			Model model){
		if (principal == null){
			return "redirect:/m/life/record/expense/index";
		}else {
			boolean isError = false;
			String accountUserIdError = null, accountIdError = null;
			
			String recordDate = expense.getRecordDate();
			//20220409 增加歸屬日期
			String vestingDate = expense.getVestingDate();
			String accountUserId = expense.getAccountUserId();
			String accountId = expense.getAccountId();
			Double amount = expense.getAmount();
			String isConsolidation = expense.getIsConsolidation();
			
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
				
				String userId = principal.getName();
				
				//帳戶
				List<Map<String,Object>> accountList = new ArrayList<>();
				
				List<AuthorityEntity> authorityList = authorityRepository.findByUserIdOrderByOrderNumberAsc(userId);
				authorityList.forEach(authority ->{
					String authorityAccountUserId = authority.getAccountUserId();
					String authorityAccountId = authority.getAccountId();
					
					Optional<BankAccountEntity> bankAccountOptional = bankAccountRepository.findByUserIdAndId(authorityAccountUserId, authorityAccountId);
					
					if(bankAccountOptional.isPresent()) {
						BankAccountEntity bankAccountEntity = bankAccountOptional.get();
						
						String memo = bankAccountEntity.getMemo();
						
						if(bankAccountEntity.getBank() != null) {
							BankEntity bank = bankAccountEntity.getBank();
							memo = bank.getName() + " - " + memo;
						};
						
						Map<String,Object> map = new HashMap<>();
						
						map.put("accountUserId", authorityAccountUserId);
						map.put("accountId", authorityAccountId);
						map.put("memo", memo);
						
						accountList.add(map);
					}
				});
				
				model.addAttribute("accountList", accountList);
				
				return "mobile/life/record/expense/new";
			}
			
			recordDate = recordDate.replaceAll("-","");
			
			//20220409 增加歸屬日期
			vestingDate = vestingDate.replaceAll("-","");
			
			if(isConsolidation == null) {
				isConsolidation = "0";
			}else {
				isConsolidation = "1";
			}
			
			expense.setRecordDate(recordDate);
			//20220409 增加歸屬日期
			expense.setVestingDate(vestingDate);
			expense.setIsConsolidation(isConsolidation);
			
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
	}

	@GetMapping("/view/{recordId}")
	public String viewGet(
			@PathVariable("recordId") String recordId,
			Principal principal,
			Model model){
		if (principal == null){
			return "redirect:/m/life/record/expense/index";
		}else {
			Optional<ExpenseEntity> expenseOptional = expenseRepository.findById(recordId);
			
			if(!expenseOptional.isPresent()) {
				return "redirect:/m/life/record/expense/index";
			}else {
				ExpenseEntity expense = expenseOptional.get();
				
				String recordDate = expense.getRecordDate();
				//20220409 增加歸屬日期
				String vestingDate = expense.getVestingDate();
				Double amount = expense.getAmount();
				String isConsolidation = expense.getIsConsolidation();
				
				String userId;
				
				expense.setRecordDate(recordDate.substring(0,4) + "-" + recordDate.substring(4,6) + "-" + recordDate.substring(6,8));
				
				//20220409 增加歸屬日期
				expense.setVestingDate(vestingDate.substring(0,4) + "-" + vestingDate.substring(4,6) + "-" + vestingDate.substring(6,8));
				
				userId = principal.getName();
				
				//帳戶
				List<Map<String,Object>> accountList = new ArrayList<>();
				
				List<AuthorityEntity> authorityList = authorityRepository.findByUserIdOrderByOrderNumberAsc(userId);
				authorityList.forEach(authority ->{
					String authorityAccountUserId = authority.getAccountUserId();
					String authorityAccountId = authority.getAccountId();
					
					Optional<BankAccountEntity> bankAccountOptional = bankAccountRepository.findByUserIdAndId(authorityAccountUserId, authorityAccountId);
					
					if(bankAccountOptional.isPresent()) {
						BankAccountEntity bankAccountEntity = bankAccountOptional.get();
						
						String memo = bankAccountEntity.getMemo();
						
						if(bankAccountEntity.getBank() != null) {
							BankEntity bank = bankAccountEntity.getBank();
							memo = bank.getName() + " - " + memo;
						};
						
						Map<String,Object> map = new HashMap<>();
						
						map.put("accountUserId", authorityAccountUserId);
						map.put("accountId", authorityAccountId);
						map.put("memo", memo);
						
						accountList.add(map);
					}
				});
				
				if(isConsolidation.equals("1")) {
					isConsolidation = "on";
					expense.setIsConsolidation(isConsolidation);
				}
				
				model.addAttribute("expense", expense);
				model.addAttribute("amount", (int)((double)amount));
				model.addAttribute("accountList", accountList);
				
				return "mobile/life/record/expense/view";
			}
		}
	}

	@Transactional(isolation=Isolation.SERIALIZABLE)
	@PutMapping("/view/{recordId}")
    public String viewPut(
    		@PathVariable("recordId") String recordId,
    		@ModelAttribute("expense") @Valid ExpenseEntity expense,
    		BindingResult bindingResult,
			Principal principal,
    		Model model){
		if (principal == null){
			return "redirect:/m/life/record/expense/index";
		}else {
			boolean isError = false;
			String accountUserIdError = null, accountIdError = null;
			
			String recordDate = expense.getRecordDate().replaceAll("-","");
			//20220409 增加歸屬日期
			String vestingDate = expense.getVestingDate().replaceAll("-","");
			String accountUserId = expense.getAccountUserId();
			String accountId = expense.getAccountId();
			Double amount = expense.getAmount();
			String isConsolidation = expense.getIsConsolidation();
			
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
				String userId = principal.getName();
				
				model.addAttribute("accountUserIdError", accountUserIdError);
				model.addAttribute("accountIdError", accountIdError);
				
				if(amount != null) {
					model.addAttribute("amount", (int)((double)amount));
				}else {
					model.addAttribute("amount", amount);
				}
				
				//帳戶
				List<Map<String,Object>> accountList = new ArrayList<>();
				
				List<AuthorityEntity> authorityList = authorityRepository.findByUserIdOrderByOrderNumberAsc(userId);
				authorityList.forEach(authority ->{
					String authorityAccountUserId = authority.getAccountUserId();
					String authorityAccountId = authority.getAccountId();
					
					Optional<BankAccountEntity> bankAccountOptional = bankAccountRepository.findByUserIdAndId(authorityAccountUserId, authorityAccountId);
					
					if(bankAccountOptional.isPresent()) {
						BankAccountEntity bankAccountEntity = bankAccountOptional.get();
						
						String memo = bankAccountEntity.getMemo();
						
						if(bankAccountEntity.getBank() != null) {
							BankEntity bank = bankAccountEntity.getBank();
							memo = bank.getName() + " - " + memo;
						};
						
						Map<String,Object> map = new HashMap<>();
						
						map.put("accountUserId", authorityAccountUserId);
						map.put("accountId", authorityAccountId);
						map.put("memo", memo);
						
						accountList.add(map);
					}
				});
				
				model.addAttribute("accountList", accountList);
				
				return "mobile/life/record/expense/view";
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
			
			if(isConsolidation == null) {
				isConsolidation = "0";
			}else {
				isConsolidation = "1";
			}
			
			expense.setRecordDate(recordDate);
			//20220409 增加歸屬日期
			expense.setVestingDate(vestingDate);
			expense.setIsConsolidation(isConsolidation);
			
			if (principal != null){
				expense.setUpdateUserId(principal.getName());
			}
			
			expenseRepository.save(expense);
			
			return "redirect:/m/life/record/expense/index";
		}
    }
	
	@DeleteMapping("/view/{recordId}")
    public String viewDelete(
    		@PathVariable("recordId") String recordId,
    		Principal principal){
		if (principal == null) {
			return "redirect:/m/life/record/expense/index";
		}else {
			expenseRepository.deleteById(recordId);
			
			return "redirect:/m/life/record/expense/index";
		}
    }
	
	@GetMapping("/search")
	public String searchGet(
			Principal principal,
			Model model){
		if (principal == null) {
			return "redirect:/m/life/record/expense/index";
		}else {
			SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");
			
			String userId = principal.getName();
	
			String month = monthFormat.format(new Date());
			List<Map<String,Object>> accountList = new ArrayList<>();
			
			//帳戶
			List<AuthorityEntity> authorityList = authorityRepository.findByUserIdOrderByOrderNumberAsc(userId);
			authorityList.forEach(authority ->{
				String authorityAccountUserId = authority.getAccountUserId();
				String authorityAccountId = authority.getAccountId();
				
				Optional<BankAccountEntity> bankAccountOptional = bankAccountRepository.findByUserIdAndId(authorityAccountUserId, authorityAccountId);
				
				if(bankAccountOptional.isPresent()) {
					BankAccountEntity bankAccountEntity = bankAccountOptional.get();
					
					String memo = bankAccountEntity.getMemo();
					
					if(bankAccountEntity.getBank() != null) {
						BankEntity bank = bankAccountEntity.getBank();
						memo = bank.getName() + " - " + memo;
					};
					
					Map<String,Object> map = new HashMap<>();
					
					map.put("accountUserId", authorityAccountUserId);
					map.put("accountId", authorityAccountId);
					map.put("memo", memo);
					
					accountList.add(map);
				}
			});
			
			model.addAttribute("accountList", accountList);
			model.addAttribute("month", month);
			
			return "mobile/life/record/expense/search";
		}
	}
}
