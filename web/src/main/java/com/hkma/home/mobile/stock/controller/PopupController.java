package com.hkma.home.mobile.stock.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hkma.home.stock.entity.AssetTypeEntity;
import com.hkma.home.stock.entity.StockProfileEntity;
import com.hkma.home.stock.repository.AssetTypeRepository;
import com.hkma.home.stock.repository.StockProfileRepository;

@Controller("MobileStockPopup")
@RequestMapping("/m/stock/popup")
public class PopupController {
	@Autowired
	private StockProfileRepository stockProfileRepository;
	
	@Autowired
	private AssetTypeRepository assetTypeRepository;
	
	@PostMapping("/profile")
	public String profile(
			@RequestParam(required=false, value="stockId") String stockId,
			Principal principal,
			Model model) {
		List<Map<String,Object>> list = new ArrayList<>();
		
		if (principal != null){
			List<StockProfileEntity> stockProfileList = stockProfileRepository.findByIdOrNameLike(stockId);
			
			stockProfileList.forEach(stockProfile ->{
				Map<String,Object> map = new HashMap<>();
				
				map.put("id", stockProfile.getId());
				map.put("name", stockProfile.getName());
				
				list.add(map);
			});
		}
		
		model.addAttribute("list", list);
		
		return "mobile/stock/popup/profile";
	}
	
	@PostMapping("/assettype")
	public String assettype(
			@RequestParam(required=false) String accountUserId,
			@RequestParam(required=false) String accountId,
			Principal principal,
			Model model) {
		List<Map<String,Object>> list = new ArrayList<>();
		
		if (principal != null){
			List<AssetTypeEntity> assetTypeList = assetTypeRepository.findByAccountUserIdAndAccountId(accountUserId, accountId);
			
			assetTypeList.forEach(assetType ->{
				Map<String,Object> map = new HashMap<>();
				
				map.put("id", assetType.getId());
				map.put("name", assetType.getName());
				
				list.add(map);
			});
		}
		
		model.addAttribute("list", list);
		
		return "mobile/stock/popup/assettype";
	}
}
