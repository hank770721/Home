package com.hkma.home.mobile.bank.controller;

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
import com.hkma.home.bank.entity.RecordEntity;
import com.hkma.home.bank.repository.AuthorityRepository;
import com.hkma.home.bank.repository.BankAccountRepository;
import com.hkma.home.bank.repository.RecordRepository;
import com.hkma.home.stock.entity.StockProfileEntity;
import com.hkma.home.stock.repository.StockProfileRepository;

@Controller("MobileBankRecord")
@RequestMapping("/m/bank/record/record")
public class RecordController {
	@Autowired
	private RecordRepository recordRepository;
	
	@Autowired
	private StockProfileRepository stockProfileRepository;
	
	@Autowired
	private AuthorityRepository authorityRepository;
	
	@Autowired
	private BankAccountRepository bankAccountRepository;
	
	@GetMapping({"/","/index"})
	public String indexRecordGet(
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
		
		List<RecordEntity> recordList = recordRepository.findByUserIdMonthOrderByRecordDateDesc(userId, month);
		
		recordList.forEach(record -> {
			double amount;
			String transMode, transModeStr = "", recordDate, stockId, stockName, memo = "";
			Map<String, Object> map = new HashMap<>();
			
			recordDate = record.getRecordDate();
			amount = record.getAmount();
			stockId = record.getStockId();
			transMode = record.getTransMode();
			
			switch (transMode) {
				case "1":
					transModeStr = "存款";
					break;
				case "2":
					transModeStr = "提款";
					break;
				case "3":
					transModeStr = "轉帳";
					break;
			}
			
			recordDate = recordDate.substring(0,4) + "/" + recordDate.substring(4,6) + "/" + recordDate.substring(6,8);
			
			if (stockId == null) {
				//20220215
				//memo = "儲蓄";
				memo = record.getMemo();
			}else {
				StockProfileEntity stockProfile = stockProfileRepository.findById(stockId);
				if (stockProfile == null) {
					stockName = "";
				}else {
					stockName = stockProfile.getName();
				}					
				
				if (record.getIsDividend() == null) {
					if (amount == 20) {
						memo = "抽籤";
					}else {
						memo = stockName;
					}
				}else {
					memo = stockName + "股息";
				}
			}
			
			map.put("recordId", record.getRecordId());
			map.put("transMode", transMode);
			map.put("transModeStr", transModeStr);
			map.put("recordDate", recordDate);
			map.put("amount", decimalFormat.format(amount));
			map.put("memo", memo);
			
			list.add(map);
		});
		
		model.addAttribute("list", list);
		
		return "mobile/bank/record/record/index";
	}
	
	@PostMapping("/index")
	public String indexRecordPost(
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
		
		List<RecordEntity> recordList = recordRepository.findByUserIdMonthAccountUserIdAccountIdOrderByRecordDateDesc(userId, month, accountUserId, accountId);
		
		recordList.forEach(record -> {
			double amount;
			String transMode, transModeStr = "", recordDate, stockId, stockName, memo = "";
			Map<String, Object> map = new HashMap<>();
			
			recordDate = record.getRecordDate();
			amount = record.getAmount();
			stockId = record.getStockId();
			transMode = record.getTransMode();
			
			switch (transMode) {
				case "1":
					transModeStr = "存款";
					break;
				case "2":
					transModeStr = "提款";
					break;
				case "3":
					transModeStr = "轉帳";
					break;
			}
			
			recordDate = recordDate.substring(0,4) + "/" + recordDate.substring(4,6) + "/" + recordDate.substring(6,8);
			
			if (stockId == null) {
				//20220215
				//memo = "儲蓄";
				memo = record.getMemo();
			}else {
				StockProfileEntity stockProfile = stockProfileRepository.findById(stockId);
				if (stockProfile == null) {
					stockName = "";
				}else {
					stockName = stockProfile.getName();
				}					
				
				if (record.getIsDividend() == null) {
					if (amount == 20) {
						memo = "抽籤";
					}else {
						memo = stockName;
					}
				}else {
					memo = stockName + "股息";
				}
			}
			
			map.put("recordId", record.getRecordId());
			map.put("transMode", transMode);
			map.put("transModeStr", transModeStr);
			map.put("recordDate", recordDate);
			map.put("amount", decimalFormat.format(amount));
			map.put("memo", memo);
			
			list.add(map);
		});
		
		model.addAttribute("list", list);
		
		return "mobile/bank/record/record/index";
	}
	
	@GetMapping("/new")
	public String newRecordGet(
			@ModelAttribute("record") RecordEntity record,
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
		
		record.setRecordDate(recordDate);
		record.setTransMode("1");
		record.setToAccountUserId(accountUserId);
		record.setToAccountId(accountId);
		
		model.addAttribute("accountList", accountList);
		
		return "mobile/bank/record/record/new";
	}
	
	@PostMapping("/new")
	public String newRecordPost(
			@ModelAttribute("record") @Valid RecordEntity record,
			BindingResult bindingResult,
			Principal principal,
			Model model){
		boolean isError = false;
		String fromAccountUserIdError = null, fromAccountIdError = null, toAccountUserIdError = null, toAccountIdError = null;
		
		String recordDate = record.getRecordDate();
		String transMode = record.getTransMode();
		String accountFromUserId = record.getFromAccountUserId();
		String accountFrom = record.getFromAccountId();
		String accountToUserId = record.getToAccountUserId();
		String accountTo = record.getToAccountId();
		Double amount = record.getAmount();
		
		if (bindingResult.hasErrors()) {
			isError = true;
		}
		
		switch(transMode) {
			case "1":
				if(accountToUserId.equals("")) {
					toAccountUserIdError = "未輸入";
					isError = true;
				}
				
				if(accountTo.equals("")) {
					toAccountIdError = "未輸入";
					isError = true;
				}
				
				break;
			case "2":
				if(accountFromUserId.equals("")) {
					fromAccountUserIdError = "未輸入";
					isError = true;
				}
				
				if(accountFrom.equals("")) {
					fromAccountIdError = "未輸入";
					isError = true;
				}
				
				break;
			case "3":
				if(accountFromUserId.equals("")) {
					fromAccountUserIdError = "未輸入";
					isError = true;
				}
				
				if(accountFrom.equals("")) {
					fromAccountIdError = "未輸入";
					isError = true;
				}
				
				if(accountToUserId.equals("")) {
					toAccountUserIdError = "未輸入";
					isError = true;
				}
				
				if(accountTo.equals("")) {
					toAccountIdError = "未輸入";
					isError = true;
				}
				
				break;
		}
		
		if (isError) {
			model.addAttribute("fromAccountUserIdError", fromAccountUserIdError);
			model.addAttribute("fromAccountIdError", fromAccountIdError);
			model.addAttribute("toAccountUserIdError", toAccountUserIdError);
			model.addAttribute("toAccountIdError", toAccountIdError);
			
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
			
			return "mobile/bank/record/record/new";
		}
		
		recordDate = recordDate.replaceAll("-","");
		record.setRecordDate(recordDate);
		
		switch(record.getTransMode()) {
			case "1":
				record.setFromAccountUserId(null);
				record.setFromAccountId(null);
				
				break;
			case "2":
				record.setToAccountUserId(null);
				record.setToAccountId(null);
				
				break;
		}
		
		if (record.getIsDividend() != null) {
			record.setIsDividend("1");
		}

		String recordId;
		Optional<String> recordIdOptional = recordRepository.getMaxRecordIdByRecordDate(recordDate);
		
		if (!recordIdOptional.isPresent()) {
			recordId = recordDate + "0001";
		}else {
			recordId = recordIdOptional.get();
			recordId = recordDate + String.format("%04d", (Integer.parseInt(recordId.substring(8,12)) + 1) );
		}
		
		record.setRecordId(recordId);

		if (principal != null){
			record.setEnterUserId(principal.getName());
			record.setUpdateUserId(principal.getName());
		}
		
		recordRepository.save(record);
		
		return "redirect:/m/bank/record/record/index";
	}

	@GetMapping("/view/{recordId}")
	public String viewRecordGet(
			@PathVariable("recordId") String recordId,
			Principal principal,
			Model model){
		Optional<RecordEntity> optional = recordRepository.findById(recordId);
		
		if(optional.isPresent()) {
			RecordEntity record = optional.get();
			
			String recordDate, userId;
			List<Map<String,Object>> accountList = new ArrayList<>();
			
			recordDate = record.getRecordDate();
			Double amount = record.getAmount();
			
			record.setRecordDate(recordDate.substring(0,4) + "-" + recordDate.substring(4,6) + "-" + recordDate.substring(6,8));
			
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
			
			model.addAttribute("record", record);
			model.addAttribute("amount", (int)((double)amount));
			model.addAttribute("accountList", accountList);
			
			String fromTable = record.getFromTable();
			
			if (fromTable == null) {
				return "mobile/bank/record/record/view";
			}else {
				String transModeName = "";
				
				switch (record.getTransMode()) {
					case "1":
						transModeName = "存款";
						break;
					case "2":
						transModeName = "提款";
						break;
					case "3":
						transModeName = "存款";
						break;
				}
				
				model.addAttribute("transModeName", transModeName);
				
				return "mobile/bank/record/record/view_noedit";
			}
		}else {
			return "redirect:/m/bank/record/record/index";
		}
	}

	@PutMapping("/view/{recordId}")
    public String viewRecordPut(
    		@PathVariable("recordId") String recordId,
    		@ModelAttribute("record") @Valid RecordEntity record,
    		BindingResult bindingResult,
			Principal principal,
    		Model model){
		boolean isError = false;
		String accountFromUserIdError = null, accountFromError = null, accountToUserIdError = null, accountToError = null;
		
		String recordDate = record.getRecordDate().replaceAll("-","");
		String transMode = record.getTransMode();
		String accountFromUserId = record.getFromAccountUserId();
		String accountFrom = record.getFromAccountId();
		String accountToUserId = record.getToAccountUserId();
		String accountTo = record.getToAccountId();
		Double amount = record.getAmount();
		
		if(bindingResult.hasErrors()) {
			isError = true;
		}
		
		switch(transMode) {
			case "1":
				if(accountToUserId.equals("")) {
					accountToUserIdError = "未輸入";
					isError = true;
				}
				
				if(accountTo.equals("")) {
					accountToError = "未輸入";
					isError = true;
				}
				
				break;
			case "2":
				if(accountFromUserId.equals("")) {
					accountFromUserIdError = "未輸入";
					isError = true;
				}
				
				if(accountFrom.equals("")) {
					accountFromError = "未輸入";
					isError = true;
				}
				
				break;
			case "3":
				if(accountFromUserId.equals("")) {
					accountFromUserIdError = "未輸入";
					isError = true;
				}
				
				if(accountFrom.equals("")) {
					accountFromError = "未輸入";
					isError = true;
				}
				
				if(accountToUserId.equals("")) {
					accountToUserIdError = "未輸入";
					isError = true;
				}
				
				if(accountTo.equals("")) {
					accountToError = "未輸入";
					isError = true;
				}
				
				break;
		}
		
		if (isError) {
			String userId;
			List<Map<String,Object>> accountList = new ArrayList<>();
			
			if (principal != null){
				userId = principal.getName();
			}else {
				userId = "mia";
			}
			
			model.addAttribute("accountFromUserIdError", accountFromUserIdError);
			model.addAttribute("accountFromError", accountFromError);
			model.addAttribute("accountToUserIdError", accountToUserIdError);
			model.addAttribute("accountToError", accountToError);
			
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
			
			return "mobile/bank/record/record/view";
		}
		
		//日期修改重取單號
		Optional<RecordEntity> recordOptional = recordRepository.findById(recordId);
		
		if(recordOptional.isPresent()) {
			RecordEntity record_old = recordOptional.get();
			
			if(!record_old.getRecordDate().equals(recordDate)) {
				recordRepository.delete(record_old);
				
				String recordId_new;
				Optional<String> recordIdOptional = recordRepository.getMaxRecordIdByRecordDate(recordDate);
				
				if (!recordIdOptional.isPresent()) {
					recordId_new = recordDate + "0001";
				}else {
					recordId_new = recordIdOptional.get();
					recordId_new = recordDate + String.format("%04d", (Integer.parseInt(recordId_new.substring(8,12)) + 1) );
				}
				
				record.setRecordId(recordId_new);
				
				if (principal != null){
					record.setEnterUserId(principal.getName());
				}
			}
		}
		
		record.setRecordDate(recordDate);
		
		switch(record.getTransMode()) {
			case "1":
				record.setFromAccountUserId(null);
				record.setFromAccountId(null);
				
				break;
			case "2":
				record.setToAccountUserId(null);
				record.setToAccountId(null);
				
				break;
		}
		
		if (record.getIsDividend() != null) {
			record.setIsDividend("1");
		}
		
		if (principal != null){
			record.setUpdateUserId(principal.getName());
		}
		
		recordRepository.save(record);
		
		return "redirect:/m/bank/record/record/index";
    }
	
	@DeleteMapping("/view/{recordId}")
    public String viewRecordDelete(
    		@PathVariable("recordId") String recordId){		
		recordRepository.deleteById(recordId);
		
		return "redirect:/m/bank/record/record/index";
    }
	
	@GetMapping("/search")
	public String searchRecord(
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
		
		model.addAttribute("accountUserId", accountUserId);
		model.addAttribute("accountId", accountId);
		model.addAttribute("accountList", accountList);
		model.addAttribute("month", month);
		
		return "mobile/bank/record/record/search";
	}
}
