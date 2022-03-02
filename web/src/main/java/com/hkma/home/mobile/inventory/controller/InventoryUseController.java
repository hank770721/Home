package com.hkma.home.mobile.inventory.controller;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import com.hkma.home.inventory.entity.InventoryPurchaseEntity;
import com.hkma.home.inventory.entity.InventoryUseEntity;
import com.hkma.home.inventory.repository.InventoryAuthorityRepository;
import com.hkma.home.inventory.repository.InventoryPurchaseRepository;
import com.hkma.home.inventory.repository.InventoryUseRepository;

@Controller("MobileInventoryUse")
@RequestMapping("/m/inventory/record/use")
public class InventoryUseController {
	@Autowired
	private InventoryUseRepository inventoryUseRepository;
	
	@Autowired
	private InventoryPurchaseRepository inventoryPurchaseRepository;
	
	@Autowired
	private InventoryAuthorityRepository authorityRepository;
	
	@GetMapping({"/","/index"})
	public String indexUseGet(
			Principal principal,
			Model model){
		SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");
		
		String userId = "";
		String month = monthFormat.format(new Date());
		
		if (principal != null) {
			userId = principal.getName();
		}

		List<Map<String, Object>> list = new ArrayList<>();
		
		List<InventoryUseEntity> InventoryUseList = inventoryUseRepository.findByUserIdAndMonthOrderByRecordIdDesc(userId, month);
		
		InventoryUseList.forEach(use -> {
			Map<String, Object> map = new HashMap<>();
			
			String recordId = use.getRecordId();
			String purchaseId = use.getPurchaseId();
			String beginDate = use.getBeginDate();
			String name;
			
			Optional<InventoryPurchaseEntity> purchaseOptional = inventoryPurchaseRepository.findById(purchaseId);
			
			if (purchaseOptional.isPresent()) {
				InventoryPurchaseEntity purchase = purchaseOptional.get();
				
				name = purchase.getBrand() + purchase.getName();
			}else {
				name = "";
			}
			
			if (beginDate == null) {
				beginDate = "";
			}else {
				beginDate = beginDate.substring(0,4) + "/" + beginDate.substring(4,6) + "/" + beginDate.substring(6,8);
			}

			map.put("recordId", recordId);
			map.put("name", name);
			map.put("beginDate", beginDate);
			
			list.add(map);
		});
		
		model.addAttribute("list", list);
		
		return "mobile/inventory/record/use/index";
	}
	
	@PostMapping("/index")
	public String indexUsePost(
			@RequestParam(required=false, value="name") String name,
			Principal principal,
			Model model){
		String userId = "";
		
		if (principal != null) {
			userId = principal.getName();
		}
		
		List<Map<String, Object>> list = new ArrayList<>();
		
		List<InventoryUseEntity> inventoryUseList = inventoryUseRepository.findByUserIdAndNameOrderByRecordIdDesc(userId, name);
		
		inventoryUseList.forEach(use -> {
			Map<String, Object> map = new HashMap<>();
			
			String recordId = use.getRecordId();
			String purchaseId = use.getPurchaseId();
			String beginDate = use.getBeginDate();
			String purchaseName;
			
			Optional<InventoryPurchaseEntity> purchaseOptional = inventoryPurchaseRepository.findById(purchaseId);
			
			if (purchaseOptional.isPresent()) {
				InventoryPurchaseEntity purchase = purchaseOptional.get();
				
				purchaseName = purchase.getBrand() + purchase.getName();
			}else {
				purchaseName = "";
			}
			
			if (beginDate == null) {
				beginDate = "";
			}else {
				beginDate = beginDate.substring(0,4) + "/" + beginDate.substring(4,6) + "/" + beginDate.substring(6,8);
			}

			map.put("recordId", recordId);
			map.put("name", purchaseName);
			map.put("beginDate", beginDate);
			
			list.add(map);
		});
		
		model.addAttribute("list", list);
		
		return "mobile/inventory/record/use/index";
	}
	
	@GetMapping("/new")
	public String newUseGet(
			@ModelAttribute("use") InventoryUseEntity use){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		String recordDate = dateFormat.format(new Date());
		
		use.setRecordDate(recordDate);
		
		return "mobile/inventory/record/use/new";
	}
	
	@PostMapping("/new")
	public String newUsePost(
			@ModelAttribute("use") InventoryUseEntity use,
			Principal principal,
    		Model model){
		String userId = "";
		String purchaseIdError;
		
		if (principal != null) {
			userId = principal.getName();
		}
		
		String recordDate = use.getRecordDate();
		String purchaseId = use.getPurchaseId();
		String beginDate = use.getBeginDate();
		String endDate = use.getEndDate();
		String isRunOut = use.getIsRunOut();
		
		Optional<InventoryPurchaseEntity> purchaseOptional = inventoryPurchaseRepository.findById(purchaseId);
		
		if (purchaseOptional.isPresent()) {
			InventoryPurchaseEntity purchase = purchaseOptional.get();
			
			if(!authorityRepository.existsByUserIdAndStockroomUserId(userId, purchase.getStockroomUserId())) {
				purchaseIdError = "無此購買單號";
				
				model.addAttribute("purchaseIdError", purchaseIdError);
			}
		}else {
			purchaseIdError = "無此購買單號";
			
			model.addAttribute("purchaseIdError", purchaseIdError);
			
			return "mobile/inventory/record/use/new";
		}
		
		if (recordDate.equals("")) {
			recordDate = null;
		}else {
			recordDate = recordDate.replaceAll("-","");
		}
		
		if (beginDate.equals("")) {
			beginDate = null;
		}else {
			beginDate = beginDate.replaceAll("-","");
		}
		
		if (endDate.equals("")) {
			endDate = null;
		}else {
			endDate = endDate.replaceAll("-","");
		}
		
		if(isRunOut == null) {
			isRunOut = "0";
		}else {
			isRunOut = "1";
		}

		use.setRecordDate(recordDate);
		use.setBeginDate(beginDate);
		use.setEndDate(endDate);
		use.setIsRunOut(isRunOut);
		
		String recordId;
		Optional<String> recordIdOptional = inventoryUseRepository.getMaxRecordIdByRecordDate(recordDate);

		if (!recordIdOptional.isPresent()) {
			recordId = recordDate + "0001";
		}else {
			recordId = recordIdOptional.get();

			recordId = recordDate + String.format("%04d", (Integer.parseInt(recordId.substring(8,12)) + 1) );
		}

		use.setRecordId(recordId);

		if (principal != null){
			use.setEnterUserId(principal.getName());
			use.setUpdateUserId(principal.getName());
		}
		
		inventoryUseRepository.save(use);
		
		return "redirect:/m/inventory/record/use/index";
	}
	
	@GetMapping("/view/{recordId}")
	public String viewUseGet(
			@PathVariable("recordId") String recordId,
			Principal principal,
			Model model){
		String userId = "";
		
		if (principal != null) {
			userId = principal.getName();
		}
		
		Optional<InventoryUseEntity> optional = inventoryUseRepository.findById(recordId);
		
		if(optional.isPresent()) {
			InventoryUseEntity use = optional.get();
			
			String recordDate = use.getRecordDate();
			String purchaseId = use.getPurchaseId();
			String beginDate = use.getBeginDate();
			String endDate = use.getEndDate();
			String isRunOut = use.getIsRunOut();
			String name;
			
			Optional<InventoryPurchaseEntity> purchaseOptional = inventoryPurchaseRepository.findById(purchaseId);
				
			if (purchaseOptional.isPresent()) {
				InventoryPurchaseEntity purchase = purchaseOptional.get();
				
				if(authorityRepository.existsByUserIdAndStockroomUserId(userId, purchase.getStockroomUserId())) {
					name = purchase.getBrand() + purchase.getName();
					
					if(!(recordDate == null)) {
						use.setRecordDate(recordDate.substring(0,4) + "-" + recordDate.substring(4,6) + "-" + recordDate.substring(6,8));
					}
					
					if(!(beginDate == null)) {
						use.setBeginDate(beginDate.substring(0,4) + "-" + beginDate.substring(4,6) + "-" + beginDate.substring(6,8));
					}
					
					if(!(endDate == null)) {
						use.setEndDate(endDate.substring(0,4) + "-" + endDate.substring(4,6) + "-" + endDate.substring(6,8));
					}
					
					if(isRunOut.equals("1")) {
						isRunOut = "on";
					}
		
					model.addAttribute("use", use);
					model.addAttribute("name", name);
		
					return "mobile/inventory/record/use/view";
				}else {
					return "redirect:/m/inventory/record/use/index";
				}
			}else {
				return "redirect:/m/inventory/record/use/index";
			}
		}else {
			return "redirect:/m/inventory/record/use/index";
		}
	}
			
	@PostMapping("/view/{recordId}")
	public String viewUsePost(
			@PathVariable("recordId") String recordId,
			@RequestParam(required=false, value="url") String url,
			Principal principal,
			Model model){
		String userId = "";
		
		if (principal != null) {
			userId = principal.getName();
		}
		
		Optional<InventoryUseEntity> optional = inventoryUseRepository.findById(recordId);
		
		if(optional.isPresent()) {
			InventoryUseEntity use = optional.get();
			
			String recordDate = use.getRecordDate();
			String purchaseId = use.getPurchaseId();
			String beginDate = use.getBeginDate();
			String endDate = use.getEndDate();
			String isRunOut = use.getIsRunOut();
			String name;
			
			if(!(recordDate == null)) {
				use.setRecordDate(recordDate.substring(0,4) + "-" + recordDate.substring(4,6) + "-" + recordDate.substring(6,8));
			}
			
			Optional<InventoryPurchaseEntity> purchaseOptional = inventoryPurchaseRepository.findById(purchaseId);
			
			if (purchaseOptional.isPresent()) {
				InventoryPurchaseEntity purchase = purchaseOptional.get();
				
				if(authorityRepository.existsByUserIdAndStockroomUserId(userId, purchase.getStockroomUserId())) {
					name = purchase.getBrand() + purchase.getName();
					
					if(!(beginDate == null)) {
						use.setBeginDate(beginDate.substring(0,4) + "-" + beginDate.substring(4,6) + "-" + beginDate.substring(6,8));
					}
					
					if(!(endDate == null)) {
						use.setEndDate(endDate.substring(0,4) + "-" + endDate.substring(4,6) + "-" + endDate.substring(6,8));
					}
					
					if(isRunOut.equals("1")) {
						isRunOut = "on";
					}
					
					model.addAttribute("use", use);
					model.addAttribute("url", url);
					model.addAttribute("name", name);

					return "mobile/inventory/record/use/view";
				}else {
					if (!url.equals("")) {
						return "redirect:" + url;
					}else {
						return "redirect:/m/inventory/record/use/index";
					}
				}
			}else{
				if (!url.equals("")) {
					return "redirect:" + url;
				}else {
					return "redirect:/m/inventory/record/use/index";
				}
			}
		}else {
			if (!url.equals("")) {
				return "redirect:" + url;
			}else {
				return "redirect:/m/inventory/record/use/index";
			}
		}
	}

	@PutMapping("/view/{recordId}")
    public String viewUsePut(
    		@PathVariable("recordId") String recordId,
    		@ModelAttribute("use") InventoryUseEntity use,
    		@RequestParam(required=false, value="url") String url,
    		BindingResult bindingResult,
			Principal principal,
    		Model model){
		
		String userId = "";
		String purchaseIdError;
		
		if (principal != null) {
			userId = principal.getName();
		}
		
		String recordDate = use.getRecordDate();
		String purchaseId = use.getPurchaseId();
		String beginDate = use.getBeginDate();
		String endDate = use.getEndDate();
		String isRunOut = use.getIsRunOut();
		
		Optional<InventoryPurchaseEntity> purchaseOptional = inventoryPurchaseRepository.findById(purchaseId);
		
		if (purchaseOptional.isPresent()) {
			InventoryPurchaseEntity purchase = purchaseOptional.get();
			
			if(!authorityRepository.existsByUserIdAndStockroomUserId(userId, purchase.getStockroomUserId())) {
				purchaseIdError = "無此購買單號";
				
				model.addAttribute("purchaseIdError", purchaseIdError);
			}
		}else {
			purchaseIdError = "無此購買單號";
			
			model.addAttribute("purchaseIdError", purchaseIdError);
			
			return "mobile/inventory/record/use/view";
		}
		
		if (recordDate.equals("")) {
			recordDate = null;
		}else {
			recordDate = recordDate.replaceAll("-","");
		}
		
		if (beginDate.equals("")) {
			beginDate = null;
		}else {
			beginDate = beginDate.replaceAll("-","");
		}
		
		if (endDate.equals("")) {
			endDate = null;
		}else {
			endDate = endDate.replaceAll("-","");
		}
		
		if(isRunOut == null) {
			isRunOut = "0";
		}else {
			isRunOut = "1";
		}

		use.setRecordId(recordId);
		use.setRecordDate(recordDate);
		use.setBeginDate(beginDate);
		use.setEndDate(endDate);
		use.setIsRunOut(isRunOut);
		
		if (principal != null){
			use.setUpdateUserId(principal.getName());
		}
		
		inventoryUseRepository.save(use);
		
		if (!url.equals("")) {
			return "redirect:" + url;
		}else {
			return "redirect:/m/inventory/record/use/index";
		}
    }
	
	@DeleteMapping("/view/{recordId}")
    public String viewUseDelete(
    		@PathVariable("recordId") String recordId){
		inventoryUseRepository.deleteById(recordId);
		
		return "redirect:/m/inventory/record/use/index";
    }
	
	@GetMapping("/search")
	public String searchUseGet(Model model){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		String recordDate = dateFormat.format(new Date());
		
		model.addAttribute("recordDate", recordDate);
		
		return "mobile/inventory/record/use/search";
	}
}
