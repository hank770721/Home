package com.hkma.home.mobile.stock.controller;

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

import com.hkma.home.stock.entity.StockProfileEntity;
import com.hkma.home.stock.entity.StockRecordEntity;
import com.hkma.home.stock.repository.StockProfileRepository;
import com.hkma.home.stock.repository.StockRecordRepository;

@Controller("MobileStockRecord")
@RequestMapping("/m/stock/record/record")
public class RecordController {
	@Autowired
	private StockRecordRepository stockRecordRepository;
	
	@Autowired
	private StockProfileRepository stockProfileRepository;
	
	@GetMapping({"/","/index"})
	public String indexGet(
			Principal principal,
			Model model){
		if (principal == null) {
			return "mobile/stock/record/record/index";
		}else {
			SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");
			DecimalFormat decimalFormat = new DecimalFormat("###,###");
			
			String month = monthFormat.format(new Date());
			
			List<Map<String, Object>> list = new ArrayList<>();
			
			String userId = principal.getName();
			List<StockRecordEntity> stockRecordList = stockRecordRepository.findByUserIdMonthOrderByRecordDateDesc(userId, month);
			
			stockRecordList.forEach(stockRecord -> {
				String transModeStr, stockName;
				Map<String, Object> map = new HashMap<>();
				
				String recordDate = stockRecord.getRecordDate();
				String transMode = stockRecord.getTransMode();
				String stockId = stockRecord.getStockId();
				Double quantity = stockRecord.getQuantity();
				Double amount = stockRecord.getAmount();
				
				recordDate = recordDate.substring(0,4) + "/" + recordDate.substring(4,6) + "/" + recordDate.substring(6,8);
				
				switch (transMode) {
					case "1":
						transModeStr = "買進";
						break;
					case "2":
						transModeStr = "賣出";
						break;
					default:
						transModeStr = "";
						break;
				}
				
				StockProfileEntity stockProfile = stockProfileRepository.findById(stockId);
				if (stockProfile == null) {
					stockName = "";
				}else {
					stockName = stockProfile.getName();
				}
				
				map.put("recordId", stockRecord.getRecordId());
				map.put("recordDate", recordDate);
				map.put("transMode", transMode);
				map.put("transModeStr", transModeStr);
				map.put("stockName", stockName);
				map.put("quantity", decimalFormat.format(quantity));
				map.put("amount", decimalFormat.format(amount));
				
				list.add(map);
			});
			
			model.addAttribute("list", list);
			
			return "mobile/stock/record/record/index";
		}
	}
	
	@PostMapping("/index")
	public String indexPost(
			@RequestParam(required=false, value="accountUserId") String accountUserId,
			@RequestParam(required=false, value="accountId") String accountId,
			@RequestParam(required=false, value="month") String month,
			Principal principal,
			Model model){
		if (principal == null) {
			return "mobile/stock/record/record/index";
		}else {
			DecimalFormat decimalFormat = new DecimalFormat("###,###");
			
			List<Map<String, Object>> list = new ArrayList<>();
			
			String userId = principal.getName();
			List<StockRecordEntity> stockRecordList = stockRecordRepository.findByUserIdMonthAccountUserIdAccountIdOrderByRecordDateDesc(userId, month, accountUserId, accountId);
			
			stockRecordList.forEach(stockRecord -> {
				String transModeStr, stockName;
				Map<String, Object> map = new HashMap<>();
				
				String recordDate = stockRecord.getRecordDate();
				String transMode = stockRecord.getTransMode();
				String stockId = stockRecord.getStockId();
				Double quantity = stockRecord.getQuantity();
				Double amount = stockRecord.getAmount();
				
				recordDate = recordDate.substring(0,4) + "/" + recordDate.substring(4,6) + "/" + recordDate.substring(6,8);
				
				switch (transMode) {
					case "1":
						transModeStr = "買進";
						break;
					case "2":
						transModeStr = "賣出";
						break;
					default:
						transModeStr = "";
						break;
				}
				
				StockProfileEntity stockProfile = stockProfileRepository.findById(stockId);
				if (stockProfile == null) {
					stockName = "";
				}else {
					stockName = stockProfile.getName();
				}
				
				map.put("recordId", stockRecord.getRecordId());
				map.put("recordDate", recordDate);
				map.put("transMode", transMode);
				map.put("transModeStr", transModeStr);
				map.put("stockName", stockName);
				map.put("quantity", decimalFormat.format(quantity));
				map.put("amount", decimalFormat.format(amount));
				
				list.add(map);
			});
			
			model.addAttribute("list", list);
			
			return "mobile/stock/record/record/index";
		}
	}
	
	@GetMapping("/new")
	public String newGet(
			@ModelAttribute("stockRecord") StockRecordEntity stockRecord,
			Principal principal,
			Model model){
		if (principal == null){
			return "redirect:/m/stock/record/record/index";
		}else {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			
			String recordDate = dateFormat.format(new Date());
			
			stockRecord.setRecordDate(recordDate);
			stockRecord.setTransMode("1");
			
			model.addAttribute("stockRecord", stockRecord);
			
			return "mobile/stock/record/record/new";
		}
	}
	
	@PostMapping("/new")
	public String newPost(
			@ModelAttribute("stockRecord") @Valid StockRecordEntity stockRecord,
			BindingResult bindingResult,
			Principal principal,
			Model model){
		if (principal == null){
			return "redirect:/m/stock/record/record/index";
		}else {
			boolean isError = false;
			String accountUserIdError = null, accountIdError = null, stockIdError = null;
			
			String recordDate = stockRecord.getRecordDate();
			String transMode = stockRecord.getTransMode();
			String accountUserId = stockRecord.getAccountUserId();
			String accountId = stockRecord.getAccountId();
			String stockId = stockRecord.getStockId();
			Double quantity = stockRecord.getQuantity();
			Double price = stockRecord.getPrice();
			Double fee = stockRecord.getFee();
			Double tax = stockRecord.getTax();
			Double amount = stockRecord.getAmount();
			String memo = stockRecord.getMemo();
			Double cost = stockRecord.getCost();
			
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
			
			if(stockId.equals("")) {
				stockIdError = "未輸入";
				isError = true;
			}
			
			if (isError) {
				model.addAttribute("accountUserIdError", accountUserIdError);
				model.addAttribute("accountIdError", accountIdError);
				model.addAttribute("stockIdError", stockIdError);
				
				if(amount != null) {
					model.addAttribute("amount", (int)((double)amount));
				}else {
					model.addAttribute("amount", amount);
				}
				
				return "mobile/stock/record/record/new";
			}
			
			recordDate = recordDate.replaceAll("-","");
			stockRecord.setRecordDate(recordDate);
	
			String recordId;
			Optional<String> recordIdOptional = stockRecordRepository.getMaxRecordIdByRecordDate(recordDate);
			
			if (!recordIdOptional.isPresent()) {
				recordId = recordDate + "0001";
			}else {
				recordId = recordIdOptional.get();
				recordId = recordDate + String.format("%04d", (Integer.parseInt(recordId.substring(8,12)) + 1) );
			}
			
			stockRecord.setRecordId(recordId);
			
			if (quantity == null) {
				stockRecord.setQuantity((double)0);
			}
			
			if (price == null) {
				stockRecord.setPrice((double)0);
			}
			
			if (fee == null) {
				stockRecord.setFee((double)0);
			}
			
			if (tax == null) {
				stockRecord.setTax((double)0);
			}
			
			if (amount == null) {
				stockRecord.setAmount((double)0);
			}
			
			if (memo.equals("")) {
				stockRecord.setMemo(null);
			}
			
			switch (transMode) {
				case "1":
					stockRecord.setCost(null);
					
					break;
				case "2":
					if (cost == null) {
						stockRecord.setCost((double)0);
					}
					
					break;
			}
	
			stockRecord.setEnterUserId(principal.getName());
			stockRecord.setUpdateUserId(principal.getName());
			
			stockRecordRepository.save(stockRecord);
			
			return "redirect:/m/stock/record/record/index";
		}
	}

	@GetMapping("/view/{recordId}")
	public String viewGet(
			@PathVariable("recordId") String recordId,
			Principal principal,
			Model model){
		Optional<StockRecordEntity> optional = stockRecordRepository.findByRecordId(recordId);
		
		if (principal == null){
			return "redirect:/m/stock/record/record/index";
		}else {
			if(!optional.isPresent()) {
				return "redirect:/m/stock/record/record/index";
			}else {
				StockRecordEntity stockRecord = optional.get();
				
				String recordDate = stockRecord.getRecordDate();
				Double quantity = stockRecord.getQuantity();
				Double price = stockRecord.getPrice();
				Double fee = stockRecord.getFee();
				Double tax = stockRecord.getTax();
				Double amount = stockRecord.getAmount();
				
				stockRecord.setRecordDate(recordDate.substring(0,4) + "-" + recordDate.substring(4,6) + "-" + recordDate.substring(6,8));
				
				String stockName;
				
				StockProfileEntity stockProfile = stockProfileRepository.findById(stockRecord.getStockId());
				if (stockProfile == null) {
					stockName = "";
				}else {
					stockName = stockProfile.getName();
				}
				
				model.addAttribute("stockRecord", stockRecord);
				model.addAttribute("stockName", stockName);
				model.addAttribute("quantity", (int)((double)quantity));
				model.addAttribute("price", (double)price);
				model.addAttribute("fee", (int)((double)fee));
				model.addAttribute("tax", (int)((double)tax));
				model.addAttribute("amount", (int)((double)amount));
				
				return "mobile/stock/record/record/view";
			}
		}
	}

	@PutMapping("/view/{recordId}")
    public String viewPut(
    		@PathVariable("recordId") String recordId,
    		@ModelAttribute("stockRecord") @Valid StockRecordEntity stockRecord,
    		BindingResult bindingResult,
			Principal principal,
    		Model model){
		if (principal == null){
			return "redirect:/m/stock/record/record/index";
		}else {
			boolean isError = false;
			String accountUserIdError = null, accountIdError = null, stockIdError = null;
			
			String recordDate = stockRecord.getRecordDate().replaceAll("-","");
			String transMode = stockRecord.getTransMode();
			String accountUserId = stockRecord.getAccountUserId();
			String accountId = stockRecord.getAccountId();
			String stockId = stockRecord.getStockId();
			Double quantity = stockRecord.getQuantity();
			Double price = stockRecord.getPrice();
			Double fee = stockRecord.getFee();
			Double tax = stockRecord.getTax();
			Double amount = stockRecord.getAmount();
			String memo = stockRecord.getMemo();
			Double cost = stockRecord.getCost();
			
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
			
			if(stockId.equals("")) {
				stockIdError = "未輸入";
				isError = true;
			}
			
			if (isError) {
				model.addAttribute("accountUserIdError", accountUserIdError);
				model.addAttribute("accountIdError", accountIdError);
				model.addAttribute("stockIdError", stockIdError);
				
				if(amount != null) {
					model.addAttribute("amount", (int)((double)amount));
				}else {
					model.addAttribute("amount", amount);
				}
				
				return "mobile/stock/record/record/view";
			}
			
			//日期修改重取單號
			Optional<StockRecordEntity> stockRecordOptional = stockRecordRepository.findById(recordId);
			
			if(stockRecordOptional.isPresent()) {
				StockRecordEntity stockRecord_old = stockRecordOptional.get();
				
				if(!stockRecord_old.getRecordDate().equals(recordDate)) {
					stockRecordRepository.delete(stockRecord_old);
					
					String recordId_new;
					Optional<String> recordIdOptional = stockRecordRepository.getMaxRecordIdByRecordDate(recordDate);
					
					if (!recordIdOptional.isPresent()) {
						recordId_new = recordDate + "0001";
					}else {
						recordId_new = recordIdOptional.get();
						recordId_new = recordDate + String.format("%04d", (Integer.parseInt(recordId_new.substring(8,12)) + 1) );
					}
					
					stockRecord.setRecordId(recordId_new);
					stockRecord.setEnterUserId(principal.getName());
				}
			}
			
			stockRecord.setRecordDate(recordDate);
			
			if (quantity == null) {
				stockRecord.setQuantity((double)0);
			}
			
			if (price == null) {
				stockRecord.setPrice((double)0);
			}
			
			if (fee == null) {
				stockRecord.setFee((double)0);
			}
			
			if (tax == null) {
				stockRecord.setTax((double)0);
			}
			
			if (amount == null) {
				stockRecord.setAmount((double)0);
			}
			
			if (memo.equals("")) {
				stockRecord.setMemo(null);
			}
			
			switch (transMode) {
				case "1":
					stockRecord.setCost(null);
					
					break;
				case "2":
					if (cost == null) {
						stockRecord.setCost((double)0);
					}
					
					break;
			}
			
			stockRecord.setUpdateUserId(principal.getName());
			
			stockRecordRepository.save(stockRecord);
			
			return "redirect:/m/stock/record/record/index";
		}
    }
	
	@DeleteMapping("/view/{recordId}")
    public String viewDelete(
    		@PathVariable("recordId") String recordId){		
		stockRecordRepository.deleteById(recordId);
		
		return "redirect:/m/stock/record/record/index";
    }
	
	@GetMapping("/search")
	public String search(
			Principal principal,
			Model model){
		if (principal == null){
			return "redirect:/m/stock/record/record/index";
		}else {
			SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");
	
			String month = monthFormat.format(new Date());
			
			model.addAttribute("month", month);
			
			return "mobile/stock/record/record/search";
		}
	}
}
