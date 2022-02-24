package com.hkma.home.mobile.inventory.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hkma.home.inventory.entity.InventoryPurchaseEntity;
import com.hkma.home.inventory.repository.InventoryPurchaseRepository;

@Controller("MobileInventoryPopup")
@RequestMapping("/m/inventory/popup")
public class PopupController {
	@Autowired
	private InventoryPurchaseRepository inventoryPurchaseRepository;
	
	@RequestMapping("/purchase")
	public String purchase(
		@RequestParam(required=false, value="purchaseId") String purchaseId,
		Principal principal,
		Model model) {
		
		String userId = "";
		
		if (principal != null) {
			userId = principal.getName();
		}

		List<Map<String,Object>> list = new ArrayList<>();
		
		List<InventoryPurchaseEntity> inventoryPurchaseList = inventoryPurchaseRepository.findByUserIdAndRecordIdOrName(userId, purchaseId);
		
		inventoryPurchaseList.forEach(purchase -> {
			int quantity = (int)((double)inventoryPurchaseRepository.getInventoryQuantityByRecordId(purchase.getRecordId()).get());
			
			Map<String,Object> map = new HashMap<>();
			map.put("recordId", purchase.getRecordId());
			map.put("name", purchase.getBrand() + purchase.getName());
			map.put("quantity", quantity);
			
			list.add(map);
		});
		
		model.addAttribute("list", list);
		
		return "mobile/inventory/popup/purchase";
	}
}
