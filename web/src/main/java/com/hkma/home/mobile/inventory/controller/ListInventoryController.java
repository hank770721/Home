package com.hkma.home.mobile.inventory.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hkma.home.inventory.entity.InventoryPurchaseEntity;
import com.hkma.home.inventory.entity.InventoryUseEntity;
import com.hkma.home.inventory.repository.InventoryPurchaseRepository;
import com.hkma.home.inventory.repository.InventoryUseRepository;
import com.hkma.home.system.service.MenuService;

@Controller("MobileInventoryListInventoryView")
@RequestMapping("/m/inventory/list/inventory")
public class ListInventoryController {
	@Autowired
	private MenuService menuService;
	
	@Autowired
	private InventoryPurchaseRepository inventoryPurchaseRepository;
	
	@Autowired
	private InventoryUseRepository inventoryUseRepository;
	
	@RequestMapping("view")
	public String view(
		Principal principal,
		Model model){
		String userId = null;
		
		if (principal != null) {
			userId = principal.getName();
		}
		
		//Optional<List<String>> class1Optional = inventoryPurchaseRepository.findClass1();
		//
		//if (class1Optional.isPresent()) {
		//	List<String> class1List = class1Optional.get();
		//	
		//	class1List.forEach(class1 -> {
		//		Optional<List<String>> class2Optional = inventoryPurchaseRepository.findClass2ByClass1(class1);
		//		
		//		if (class2Optional.isPresent()) {
		//			List<String> class2List = class2Optional.get();
		//			
		//			class2List.forEach(class2 -> {
		//				Optional<List<String>> nameOptional = inventoryPurchaseRepository.findNameByClass1Class2(class1,class2);
		//				
		//				if (nameOptional.isPresent()) {
		//					List<String> nameList = nameOptional.get();
		//					
		//					nameList.forEach(name -> {
		//						System.out.println(name);
		//					});
		//				}
		//			});
		//		}
		//	});
		//}
		List<Map<String,Object>> class1List = new ArrayList<>();
		
		List<InventoryPurchaseEntity> inventoryPurchaseList = inventoryPurchaseRepository.findInventory();
		
		inventoryPurchaseList.forEach(inventoryPurchase -> {
			boolean isFind;
			int class1ListIndex = 0, class2ListIndex = 0, nameListIndex = 0, purchaseListIndex = 0;
			
			String recordId = inventoryPurchase.getRecordId();
			String class1 = inventoryPurchase.getClass1();
			String class2 = inventoryPurchase.getClass2();
			String brand = inventoryPurchase.getBrand();
			String name = inventoryPurchase.getName();
			String purchaseDate = inventoryPurchase.getPurchaseDate();
			String expiryDate = inventoryPurchase.getExpiryDate();
			
			int inventoryQuantity = (int)((double)inventoryPurchaseRepository.getInventoryQuantityByRecordId(recordId).get());
			
			if (class1 == null) class1 = "　 ";
			if (class2 == null) class2 = "　";
			if (brand == null) brand = "　";
			if (name == null) name = "　";
			
			if (purchaseDate == null) {
				purchaseDate = "　";
			}else {
				if(purchaseDate.length() > 6) {
					purchaseDate = purchaseDate.substring(0,4) + "/" + purchaseDate.substring(4,6) + "/" + purchaseDate.substring(6,8);
				}else {
					purchaseDate = purchaseDate.substring(0,4) + "/" + purchaseDate.substring(4,6);
				}
			}
			
			if (expiryDate == null) {
				expiryDate = "　";
			}else {
				if(expiryDate.length() > 6) {
					expiryDate = expiryDate.substring(0,4) + "/" + expiryDate.substring(4,6) + "/" + expiryDate.substring(6,8);
				}else {
					expiryDate = expiryDate.substring(0,4) + "/" + expiryDate.substring(4,6);
				}
			}
			
			name = brand + name;
			
			//class1
			{
				isFind = false;
				for(class1ListIndex = 0; class1ListIndex < class1List.size(); class1ListIndex++) {
					if(class1List.get(class1ListIndex).get("class1").equals(class1)) {
						isFind = true;
						break;
					}
				}
				
				if (!isFind) {
					Map<String, Object> class1Map = new HashMap<String, Object>();
					class1Map.put("class1",class1);
					class1List.add(class1Map);
					class1ListIndex = class1List.size() - 1;
				}
			}
			
			//class2
			List<Map<String, Object>> class2List = (List<Map<String, Object>>) class1List.get(class1ListIndex).get("class2List");
			{
				isFind = false;
				
				if (class2List == null) {
					class2List = new ArrayList<Map<String, Object>>();
					class1List.get(class1ListIndex).put("class2List",class2List);
				}else {
					for(class2ListIndex = 0; class2ListIndex < class2List.size(); class2ListIndex++) {
						if(class2List.get(class2ListIndex).get("class2").equals(class2)) {
							isFind = true;
							break;
						}
					}
				}
				
				if (!isFind) {
					Map<String, Object> class2Map = new HashMap<String, Object>();
					class2Map.put("class2",class2);
					class2Map.put("quantity",inventoryQuantity);
					class2List.add(class2Map);
					class2ListIndex = class2List.size() - 1;
				}else {
					int class2Quantity = (int) class2List.get(class2ListIndex).get("quantity");
					class2Quantity = class2Quantity + inventoryQuantity;
					class2List.get(class2ListIndex).replace("quantity", class2Quantity);
				}
			}
			
			//name
			List<Map<String, Object>> nameList = (List<Map<String, Object>>) class2List.get(class2ListIndex).get("nameList");
			{
				isFind = false;
				
				if (nameList == null) {
					nameList = new ArrayList<Map<String, Object>>();
					class2List.get(class2ListIndex).put("nameList",nameList);
				}else {
					for(nameListIndex = 0; nameListIndex < nameList.size(); nameListIndex++) {
						if(nameList.get(nameListIndex).get("name").equals(name)) {
							isFind = true;
							break;
						}
					}
				}
				
				if (!isFind) {
					Map<String, Object> nameMap = new HashMap<String, Object>();
					nameMap.put("name",name);
					nameMap.put("quantity",inventoryQuantity);
					nameList.add(nameMap);
					nameListIndex = nameList.size() - 1;
				}else {
					int nameQuantity = (int) nameList.get(nameListIndex).get("quantity");
					nameQuantity = nameQuantity + inventoryQuantity;
					nameList.get(nameListIndex).replace("quantity", nameQuantity);
				}
			}
			
			//purchase
			List<Map<String, Object>> purchaseList = (List<Map<String, Object>>) nameList.get(nameListIndex).get("purchaseList");
			{
				isFind = false;
				
				if (purchaseList == null) {
					purchaseList = new ArrayList<Map<String, Object>>();
					nameList.get(nameListIndex).put("purchaseList",purchaseList);
				}else {
					for(purchaseListIndex = 0; purchaseListIndex < purchaseList.size(); purchaseListIndex++) {
						if(purchaseList.get(purchaseListIndex).get("recordId").equals(recordId)) {
							isFind = true;
							break;
						}
					}
				}
				
				if (!isFind) {
					Map<String, Object> purchaseDateMap = new LinkedHashMap<String, Object>();
					purchaseDateMap.put("recordId",recordId);
					purchaseDateMap.put("purchaseDate",purchaseDate);
					purchaseDateMap.put("expiryDate",expiryDate);
					purchaseDateMap.put("quantity",inventoryQuantity);
					purchaseList.add(purchaseDateMap);
					purchaseListIndex = purchaseList.size() - 1;
				}else {
					int purchaseQuantity = (int) purchaseList.get(purchaseListIndex).get("quantity");
					purchaseQuantity = purchaseQuantity + inventoryQuantity;
					purchaseList.get(purchaseListIndex).replace("quantity", purchaseQuantity);
				}
			}
			
			//use
			List<Map<String, Object>> useList = (List<Map<String, Object>>) purchaseList.get(purchaseListIndex).get("useList");
			{
				if (useList == null) {
					useList = new ArrayList<Map<String, Object>>();
					purchaseList.get(purchaseListIndex).put("useList",useList);
				}
				
				List<InventoryUseEntity> inventoryUseList = inventoryUseRepository.findUsingByPurchaseId(recordId);
				
				if (inventoryUseList.size() > 0) {
					for(InventoryUseEntity inventoryUse : inventoryUseList) {
						String beginDate = inventoryUse.getBeginDate();
						
						if (beginDate == null) {
							beginDate = "　";
						}else {
							beginDate = beginDate.substring(0,4) + "/" + beginDate.substring(4,6) + "/" + beginDate.substring(6,8);
						}
						
						Map<String, Object> useMap = new LinkedHashMap<String, Object>();
						useMap.put("useId",inventoryUse.getRecordId());
						useMap.put("beginDate",beginDate);
						useList.add(useMap);
					}
				}
			}
		});
		
		model.addAttribute("class1List",class1List);
		model.addAttribute("menuList",menuService.getMenu(userId));
		
		return "mobile/inventory/list/inventory/view";
	}
}

