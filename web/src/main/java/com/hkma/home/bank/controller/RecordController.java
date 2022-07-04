package com.hkma.home.bank.controller;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hkma.home.bank.entity.AuthorityEntity;
import com.hkma.home.bank.entity.BankAccountEntity;
import com.hkma.home.bank.entity.BankEntity;
import com.hkma.home.bank.entity.RecordEntity;
import com.hkma.home.bank.repository.AuthorityRepository;
import com.hkma.home.bank.repository.BankAccountRepository;
import com.hkma.home.bank.repository.RecordRepository;
import com.hkma.home.stock.entity.StockProfileEntity;
import com.hkma.home.stock.repository.StockProfileRepository;

@Controller("BankRecord")
@RequestMapping("/bank/record/record")
public class RecordController {
	@Autowired
	private RecordRepository recordRepository;
	
	@Autowired
	private StockProfileRepository stockProfileRepository;
	
	@Autowired
	private AuthorityRepository authorityRepository;
	
	@Autowired
	private BankAccountRepository bankAccountRepository;
	
	@GetMapping({"/","/index2"})
	public String indexGet2(
			Principal principal,
			Model model){
		SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");
		String month = monthFormat.format(new Date());
		month = "202203";
		
		List<Map<String, Object>> list = new ArrayList<>();
		
		if (principal != null) {
			String userId = principal.getName();
			
			DecimalFormat decimalFormat = new DecimalFormat("###,###");
			
			List<RecordEntity> recordList = recordRepository.findByUserIdMonthOrderByRecordDateDescRecordIdDesc(userId, month);
			
			int rowNumber = 0;
			for (RecordEntity record : recordList) {
				rowNumber++;
				
				double amount;
				String transMode, recordDate;
				String recordDateDisplay, transModeDisplay, amountDisplay;
				Map<String, Object> map = new HashMap<>();
				
				recordDate = record.getRecordDate();
				transMode = record.getTransMode();
				amount = record.getAmount();
				
				recordDateDisplay = recordDate.substring(0,4) + "/" + recordDate.substring(4,6) + "/" + recordDate.substring(6,8);
				
				switch (transMode) {
					case "1":
						transModeDisplay = "存款";
						break;
					case "2":
						transModeDisplay = "提款";
						break;
					default:
						transModeDisplay = "";
				}
				
				amountDisplay = decimalFormat.format(amount);
				
				map.put("rowNumber", rowNumber);
				map.put("recordId", record.getRecordId());
				map.put("recordDate", recordDate);
				map.put("transMode", transMode);
				map.put("fromAccountUserId", record.getFromAccountUserId());
				map.put("fromAccountId", record.getFromAccountId());
				map.put("toAccountUserId", record.getToAccountUserId());
				map.put("toAccountId", record.getToAccountId());
				map.put("memo", record.getMemo());
				map.put("amount", amount);
				map.put("isDividend", record.getIsDividend());
				map.put("stockId", record.getStockId());
				map.put("fromTable", record.getFromTable());

				map.put("recordDateDisplay", recordDateDisplay);
				map.put("transModeDisplay", transModeDisplay);
				map.put("amountDisplay", amountDisplay);
				
				list.add(map);
			};
		}
		
		model.addAttribute("list", list);
		model.addAttribute("month", month);
		
		return "bank/record/record/index2";
	}
	
	@GetMapping({"/","/index"})
	public String indexGet(
			Principal principal,
			Model model){
		SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");
		String month = monthFormat.format(new Date());
		
		List<Map<String, Object>> list = new ArrayList<>();
		
		if (principal != null) {
			String userId = principal.getName();
			
			DecimalFormat decimalFormat = new DecimalFormat("###,###");
			
			List<RecordEntity> recordList = recordRepository.findByUserIdMonthOrderByRecordDateDescRecordIdDesc(userId, month);
			
			int rowNumber = 0;
			for (RecordEntity record : recordList) {
				rowNumber++;
				
				double amount;
				String transMode, transModeStr = "", recordDate, stockId, stockName, memo = "";
				Map<String, Object> map = new HashMap<>();
				
				recordDate = record.getRecordDate();
				amount = record.getAmount();
				stockId = record.getStockId();
				transMode = record.getTransMode();
				
				map.put("rowNumber", rowNumber);
				map.put("recordId", record.getRecordId());
				map.put("recordDate", recordDate);
				map.put("transMode", transMode);
				map.put("fromAccountUserId", record.getFromAccountUserId());
				map.put("fromAccountId", record.getFromAccountId());
				map.put("toAccountUserId", record.getToAccountUserId());
				map.put("toAccountId", record.getToAccountId());
				map.put("memo", memo);
				map.put("amount", decimalFormat.format(amount));
				map.put("isDividend", record.getIsDividend());
				map.put("stockId", record.getStockId());
				map.put("fromTable", record.getFromTable());
				
				list.add(map);
			};
		}
		
		model.addAttribute("list", list);
		model.addAttribute("month", month);
		
		return "bank/record/record/index";
	}
	
	@PostMapping("/index")
	public String indexPost(
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
		
		List<RecordEntity> recordList = recordRepository.findByUserIdMonthOrderByRecordDateDescRecordIdDesc(userId, month);
		
		int rowNumber = 0;
		for (RecordEntity record : recordList) {
			rowNumber++;
			
			double amount;
			String transMode, transModeStr = "", recordDate, stockId, stockName, memo = "";
			Map<String, Object> map = new HashMap<>();
			
			recordDate = record.getRecordDate();
			amount = record.getAmount();
			stockId = record.getStockId();
			transMode = record.getTransMode();
			
			map.put("rowNumber", rowNumber);
			map.put("recordId", record.getRecordId());
			map.put("recordDate", recordDate);
			map.put("transMode", transMode);
			map.put("fromAccountUserId", record.getFromAccountUserId());
			map.put("fromAccountId", record.getFromAccountId());
			map.put("toAccountUserId", record.getToAccountUserId());
			map.put("toAccountId", record.getToAccountId());
			map.put("memo", memo);
			map.put("amount", decimalFormat.format(amount));
			map.put("isDividend", record.getIsDividend());
			map.put("stockId", record.getStockId());
			map.put("fromTable", record.getFromTable());
			
			list.add(map);
		};
		
		model.addAttribute("list", list);
		model.addAttribute("month", month);
		
		return "bank/record/record/index";
	}

	@PutMapping("/index")
    public String indexPut(
    		@RequestParam(required=false, value="data") String data,
    		Principal principal){
		ObjectMapper objectMapper = new ObjectMapper();
		List<RecordEntity> recordList;
		
		try {
			recordList = objectMapper.readValue(data, new TypeReference<List<RecordEntity>>() {});
			
			recordList.forEach(record ->{
				String recordId = record.getRecordId();
				String recordDate = record.getRecordDate();
				
				if (recordId.equals("")) {
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
				}
				
				recordRepository.save(record);
			});
		} catch (Exception ex) {
			System.out.println(ex);
		}
		
		return "redirect:/bank/record/record/index";
    }
	
	@DeleteMapping("/index")
    public String indexDelete(
    		@RequestParam(required=false, value="data") String data){
		ObjectMapper objectMapper = new ObjectMapper();
		List<RecordEntity> recordList = null;
		
		try {
			recordList = objectMapper.readValue(data, new TypeReference<List<RecordEntity>>() {});
			
			
		} catch (Exception ex) {
			System.out.println(ex);
		}
		
		recordList.forEach(record ->{
			recordRepository.deleteById(record.getRecordId());
		});
		
		return "redirect:/bank/record/record/index";
    }
}