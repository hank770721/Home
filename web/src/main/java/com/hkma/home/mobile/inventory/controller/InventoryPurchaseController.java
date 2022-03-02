package com.hkma.home.mobile.inventory.controller;

import java.security.Principal;
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

import com.hkma.home.inventory.entity.AuthorityEntity;
import com.hkma.home.inventory.entity.InventoryPurchaseEntity;
import com.hkma.home.inventory.entity.InventoryUseEntity;
import com.hkma.home.inventory.entity.StockroomEntity;
import com.hkma.home.inventory.repository.InventoryAuthorityRepository;
import com.hkma.home.inventory.repository.InventoryPurchaseRepository;
import com.hkma.home.inventory.repository.InventoryUseRepository;
import com.hkma.home.inventory.repository.StockroomRepository;

@Controller("MobileInventoryPurchase")
@RequestMapping("/m/inventory/record/purchase")
public class InventoryPurchaseController {
	@Autowired
	private InventoryPurchaseRepository inventoryPurchaseRepository;
	
	@Autowired
	private InventoryUseRepository inventoryUseRepository;
	
	@Autowired
	private InventoryAuthorityRepository inventoryAuthorityRepository;
	
	@Autowired
	private StockroomRepository stockroomRepository;
	
	@GetMapping({"/","/index"})
	public String indexPurchaseGet(
			Principal principal,
			Model model){
		SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");
		
		String userId = "";
		String month = monthFormat.format(new Date());
		
		if (principal != null) {
			userId = principal.getName();
		}
		
		List<Map<String, Object>> list = new ArrayList<>();
		
		List<InventoryPurchaseEntity> inventoryPurchaseList = inventoryPurchaseRepository.findByUserIdAndMonthOrderByRecordId(userId, month);
		
		inventoryPurchaseList.forEach(purchase -> {
			Map<String, Object> map = new HashMap<>();
			
			String recordId = purchase.getRecordId();
			String brand = purchase.getBrand();
			String name = purchase.getName();
			String purchaseDate = purchase.getPurchaseDate();
			
			if (purchaseDate == null) {
				purchaseDate = "";
			}else {
				purchaseDate = purchaseDate.substring(0,4) + "/" + purchaseDate.substring(4,6) + "/" + purchaseDate.substring(6,8);
			}

			map.put("recordId", recordId);
			map.put("name", brand + name);
			map.put("purchaseDate", purchaseDate);
			
			list.add(map);
		});
		
		model.addAttribute("list", list);
		
		return "mobile/inventory/record/purchase/index";
	}
	
	@PostMapping("/index")
	public String indexPurchasePost(
			@RequestParam(required=false, value="name") String name,
			@RequestParam(required=false, value="inventoryType") String inventoryType,
			Principal principal,
			Model model){
		String userId = "";
		
		if (principal != null) {
			userId = principal.getName();
		}
		
		List<Map<String, Object>> list = new ArrayList<>();
		
		List<InventoryPurchaseEntity> InventoryPurchaseList = inventoryPurchaseRepository.findByUserIdAndNameAndInventoryTypeOrderByRecordId(userId, name, inventoryType);
		
		InventoryPurchaseList.forEach(purchase -> {
			Map<String, Object> map = new HashMap<>();
			
			String recordId = purchase.getRecordId();
			String brand = purchase.getBrand();
			String purchaseName = purchase.getName();
			String purchaseDate = purchase.getPurchaseDate();
			
			if (purchaseDate == null) {
				purchaseDate = "";
			}else {
				purchaseDate = purchaseDate.substring(0,4) + "/" + purchaseDate.substring(4,6) + "/" + purchaseDate.substring(6,8);
			}

			map.put("recordId", recordId);
			map.put("name", brand + purchaseName);
			map.put("purchaseDate", purchaseDate);
			list.add(map);
		});
		
		model.addAttribute("list", list);
		
		return "mobile/inventory/record/purchase/index";
	}
	
	@GetMapping("/new")
	public String newPurchaseGet(
			@ModelAttribute("purchase") InventoryPurchaseEntity purchase,
			Principal principal,
    		Model model){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		String recordDate = dateFormat.format(new Date());
		String userId = "";
		List<Map<String,Object>> stockroomList = new ArrayList<>();
		
		if (principal != null) {
			userId = principal.getName();
		}
		
		List<AuthorityEntity> authorityList = inventoryAuthorityRepository.findByUserId(userId);
		authorityList.forEach(authority ->{
			String stockroomUserId = authority.getStockroomUserId();
			String stockroomId = authority.getStockroomId();
			
			Optional<StockroomEntity> bankAccountOptional = stockroomRepository.findByUserIdAndId(stockroomUserId, stockroomId);
			
			if(bankAccountOptional.isPresent()) {
				StockroomEntity stockroomEntity = bankAccountOptional.get();
				
				String name = stockroomEntity.getName();
				
				Map<String,Object> map = new HashMap<>();
				
				map.put("stockroomUserId", stockroomUserId);
				map.put("stockroomId", stockroomId);
				map.put("name", name);
				
				stockroomList.add(map);
			}
		});
		
		purchase.setRecordDate(recordDate);
		
		model.addAttribute("stockroomList", stockroomList);
		
		return "mobile/inventory/record/purchase/new";
	}
	
	@PostMapping("/new")
	public String newPurchasePost(
			@ModelAttribute("purchase") @Valid InventoryPurchaseEntity purchase,
			BindingResult bindingResult,
			Principal principal,
    		Model model){
		boolean isError = false;
		String userId = "";
		String stockroomIdError = null;
		
		if (principal != null) {
			userId = principal.getName();
		}
		
		String recordDate = purchase.getRecordDate();
		String stockroomUserId = purchase.getStockroomUserId();
		String stockroomId = purchase.getStockroomId();
		Double quantity = purchase.getQuantity();
		String purchaseDate = purchase.getPurchaseDate();
		String manufactureDate = purchase.getManufactureDate();
		String expiryDate = purchase.getExpiryDate();
		Double amount = purchase.getAmount();
		
		if (bindingResult.hasErrors()) {
			isError = true;
		}
		
		if (!stockroomRepository.existsByUserIdAndStockroomUserIdAndStockroomId(userId, stockroomUserId, stockroomId)) {
			stockroomIdError = "倉庫不存在";
			isError = true;
		}
		
		if (isError) {
			model.addAttribute("stockroomIdError", stockroomIdError);
			
			if(quantity != null) {
				model.addAttribute("quantity", (int)((double)quantity));
			}else {
				model.addAttribute("quantity", quantity);
			}
			
			if(amount != null) {
				model.addAttribute("amount", (int)((double)amount));
			}else {
				model.addAttribute("amount", amount);
			}
			
			List<Map<String,Object>> stockroomList = new ArrayList<>();
			
			List<AuthorityEntity> authorityList = inventoryAuthorityRepository.findByUserId(userId);
			authorityList.forEach(authority ->{
				String stockroomUserId_list = authority.getStockroomUserId();
				String stockroomId_list = authority.getStockroomId();
				
				Optional<StockroomEntity> bankAccountOptional = stockroomRepository.findByUserIdAndId(stockroomUserId_list, stockroomId_list);
				
				if(bankAccountOptional.isPresent()) {
					StockroomEntity stockroomEntity = bankAccountOptional.get();
					
					String name = stockroomEntity.getName();
					
					Map<String,Object> map = new HashMap<>();
					
					map.put("stockroomUserId", stockroomUserId_list);
					map.put("stockroomId", stockroomId_list);
					map.put("name", name);
					
					stockroomList.add(map);
				}
			});
			
			model.addAttribute("stockroomList", stockroomList);
			
			return "mobile/inventory/record/purchase/new";
		}
		
		if (recordDate.equals("")) {
			recordDate = null;
		}else {
			recordDate = recordDate.replaceAll("-","");
		}
		
		if (purchaseDate.equals("")) {
			purchaseDate = null;
		}else {
			purchaseDate = purchaseDate.replaceAll("-","");
		}
		
		if (manufactureDate.equals("")) {
			manufactureDate = null;
		}else {
			manufactureDate = manufactureDate.replaceAll("-","");
		}
		
		if (expiryDate.equals("")) {
			expiryDate = null;
		}else {
			expiryDate = expiryDate.replaceAll("-","");
		}
		
		if (amount == null) {
			amount = 0.0;
		}

		purchase.setRecordDate(recordDate);
		purchase.setPurchaseDate(purchaseDate);
		purchase.setManufactureDate(manufactureDate);
		purchase.setExpiryDate(expiryDate);
		purchase.setAmount(amount);

		String recordId;
		Optional<String> recordIdOptional = inventoryPurchaseRepository.getMaxRecordIdByRecordDate(recordDate);
		
		if (!recordIdOptional.isPresent()) {
			recordId = recordDate + "0001";
		}else {
			recordId = recordIdOptional.get();
			recordId = recordDate + String.format("%04d", (Integer.parseInt(recordId.substring(8,12)) + 1) );
		}
		
		purchase.setRecordId(recordId);

		if (principal != null){
			purchase.setEnterUserId(principal.getName());
			purchase.setUpdateUserId(principal.getName());
		}
		
		inventoryPurchaseRepository.save(purchase);
		
		return "redirect:/m/inventory/record/purchase/index";
	}
	
	@GetMapping("/view/{recordId}")
	public String viewPurchaseGet(
			@PathVariable("recordId") String recordId,
			Principal principal,
			Model model){
		Optional<InventoryPurchaseEntity> optional = inventoryPurchaseRepository.findById(recordId);
		
		if(optional.isPresent()) {
			InventoryPurchaseEntity purchase = optional.get();
			
			int quantity = (int)((double)purchase.getQuantity());
			String purchaseDate = purchase.getPurchaseDate();
			String manufactureDate = purchase.getManufactureDate();
			String expiryDate = purchase.getExpiryDate();
			int amount = (int)((double)purchase.getAmount());
			
			if(!(purchaseDate == null)) {
				purchase.setPurchaseDate(purchaseDate.substring(0,4) + "-" + purchaseDate.substring(4,6) + "-" + purchaseDate.substring(6,8));
			}
			
			if(!(manufactureDate == null)) {
				purchase.setManufactureDate(manufactureDate.substring(0,4) + "-" + manufactureDate.substring(4,6) + "-" + manufactureDate.substring(6,8));
			}
			
			if(!(expiryDate == null)) {
				purchase.setExpiryDate(expiryDate.substring(0,4) + "-" + expiryDate.substring(4,6) + "-" + expiryDate.substring(6,8));
			}
			
			model.addAttribute("purchase", purchase);
			model.addAttribute("quantity", quantity);
			model.addAttribute("amount", amount);
			
			List<InventoryUseEntity> useList = inventoryUseRepository.findByPurchaseIdOrderByRecordIdAsc(recordId);

			if(useList.size() == 0) {
				String userId = "";
				List<Map<String,Object>> stockroomList = new ArrayList<>();
				
				if (principal != null) {
					userId = principal.getName();
				}
				
				List<AuthorityEntity> authorityList = inventoryAuthorityRepository.findByUserId(userId);
				authorityList.forEach(authority ->{
					String stockroomUserId = authority.getStockroomUserId();
					String stockroomId = authority.getStockroomId();
					
					Optional<StockroomEntity> bankAccountOptional = stockroomRepository.findByUserIdAndId(stockroomUserId, stockroomId);
					
					if(bankAccountOptional.isPresent()) {
						StockroomEntity stockroomEntity = bankAccountOptional.get();
						
						String name = stockroomEntity.getName();
						
						Map<String,Object> map = new HashMap<>();
						
						map.put("stockroomUserId", stockroomUserId);
						map.put("stockroomId", stockroomId);
						map.put("name", name);
						
						stockroomList.add(map);
					}
				});
				
				model.addAttribute("stockroomList", stockroomList);
				
				return "mobile/inventory/record/purchase/view";
			}else {
				return "mobile/inventory/record/purchase/view_noedit";
			}
		}else {
			return "redirect:/m/inventory/record/purchase/index";
		}
	}

	@PutMapping("/view/{recordId}")
    public String viewPurchasePut(
    		@PathVariable("recordId") String recordId,
    		@ModelAttribute("purchase") @Valid InventoryPurchaseEntity purchase,
    		BindingResult bindingResult,
			Principal principal,
    		Model model){
		boolean isError = false;
		String userId = "";
		String stockroomIdError = null;
		
		if (principal != null) {
			userId = principal.getName();
		}
		
		String recordDate = purchase.getRecordDate();
		String stockroomUserId = purchase.getStockroomUserId();
		String stockroomId = purchase.getStockroomId();
		Double quantity = purchase.getQuantity();
		String purchaseDate = purchase.getPurchaseDate();
		String manufactureDate = purchase.getManufactureDate();
		String expiryDate = purchase.getExpiryDate();
		Double amount = purchase.getAmount();
		
		if (bindingResult.hasErrors()) {
			isError = true;
		}
		
		if (!stockroomRepository.existsByUserIdAndStockroomUserIdAndStockroomId(userId, stockroomUserId, stockroomId)) {
			stockroomIdError = "倉庫不存在";
			isError = true;
		}
		
		if (isError) {
			model.addAttribute("stockroomIdError", stockroomIdError);
			
			if(quantity != null) {
				model.addAttribute("quantity", (int)((double)quantity));
			}else {
				model.addAttribute("quantity", quantity);
			}
			
			if(amount != null) {
				model.addAttribute("amount", (int)((double)amount));
			}else {
				model.addAttribute("amount", amount);
			}
			
			List<Map<String,Object>> stockroomList = new ArrayList<>();
			
			List<AuthorityEntity> authorityList = inventoryAuthorityRepository.findByUserId(userId);
			authorityList.forEach(authority ->{
				String stockroomUserId_list = authority.getStockroomUserId();
				String stockroomId_list = authority.getStockroomId();
				
				Optional<StockroomEntity> bankAccountOptional = stockroomRepository.findByUserIdAndId(stockroomUserId_list, stockroomId_list);
				
				if(bankAccountOptional.isPresent()) {
					StockroomEntity stockroomEntity = bankAccountOptional.get();
					
					String name = stockroomEntity.getName();
					
					Map<String,Object> map = new HashMap<>();
					
					map.put("stockroomUserId", stockroomUserId_list);
					map.put("stockroomId", stockroomId_list);
					map.put("name", name);
					
					stockroomList.add(map);
				}
			});
			
			model.addAttribute("stockroomList", stockroomList);
			
			return "mobile/inventory/record/purchase/view";
		}
		
		if (recordDate.equals("")) {
			recordDate = null;
		}else {
			recordDate = recordDate.replaceAll("-","");
		}
		
		if (purchaseDate.equals("")) {
			purchaseDate = null;
		}else {
			purchaseDate = purchaseDate.replaceAll("-","");
		}
		
		if (manufactureDate.equals("")) {
			manufactureDate = null;
		}else {
			manufactureDate = manufactureDate.replaceAll("-","");
		}
		
		if (expiryDate.equals("")) {
			expiryDate = null;
		}else {
			expiryDate = expiryDate.replaceAll("-","");
		}

		purchase.setRecordId(recordId);
		purchase.setRecordDate(recordDate);
		purchase.setPurchaseDate(purchaseDate);
		purchase.setManufactureDate(manufactureDate);
		purchase.setExpiryDate(expiryDate);
		
		if (principal != null){
			purchase.setUpdateUserId(principal.getName());
		}
		
		inventoryPurchaseRepository.save(purchase);
		
		return "redirect:/m/inventory/record/purchase/index";
    }
	
	@DeleteMapping("/view/{recordId}")
    public String viewPurchaseDelete(
    		@PathVariable("recordId") String recordId){
		inventoryPurchaseRepository.deleteById(recordId);
		
		return "redirect:/m/inventory/record/purchase/index";
    }
	
	@GetMapping("/search")
	public String searchPurchaseGet(Model model){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		String recordDate = dateFormat.format(new Date());
		
		model.addAttribute("recordDate", recordDate);
		
		return "mobile/inventory/record/purchase/search";
	}
}
