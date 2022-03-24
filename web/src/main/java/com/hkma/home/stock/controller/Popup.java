package com.hkma.home.stock.controller;

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

import com.hkma.home.stock.entity.StockProfileEntity;
import com.hkma.home.stock.repository.StockProfileRepository;

@Controller("MobileStockPopup")
@RequestMapping("/m/stock/popup")
public class Popup {
	@Autowired
	private StockProfileRepository stockProfileRepository;
	
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
}
