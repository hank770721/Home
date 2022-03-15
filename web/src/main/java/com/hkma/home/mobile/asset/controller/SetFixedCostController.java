package com.hkma.home.mobile.asset.controller;

import java.security.Principal;
import java.text.DecimalFormat;
import java.util.ArrayList;
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

import com.hkma.home.asset.entity.FixedCostEntity;
import com.hkma.home.asset.repository.FixedCostRepository;

@Controller("MobileAssetSetFixedCost")
@RequestMapping("/m/asset/set/fixedcost")
public class SetFixedCostController {
	@Autowired
	private FixedCostRepository fixedCostRepository;
	
	@GetMapping({"/","/index"})
	public String indexFixedCostGet(
			Principal principal,
			Model model){
		DecimalFormat decimalFormat = new DecimalFormat("###,###");
		List<Map<String, Object>> list = new ArrayList<>();
		
		if (principal != null) {
			String userId = principal.getName();
			
			List<FixedCostEntity> fixedCostList = fixedCostRepository.findByUserId(userId);
			
			fixedCostList.forEach(fixedCost -> {
				Map<String, Object> map = new HashMap<>();
				
				map.put("userId", fixedCost.getUserId());
				map.put("id", fixedCost.getId());
				map.put("name", fixedCost.getName());
				map.put("amount", decimalFormat.format(fixedCost.getAmount()));
				
				list.add(map);
			});
			
			model.addAttribute("list", list);
			
			return "mobile/asset/set/fixedcost/index";
		}else {
			model.addAttribute("list", list);
			
			return "mobile/asset/set/fixedcost/index";
		}
	}
	
	@PostMapping("/index")
	public String indexFixedCostPost(
			@RequestParam(required=false, value="name") String name,
			Principal principal,
			Model model){
		DecimalFormat decimalFormat = new DecimalFormat("###,###");
		List<Map<String, Object>> list = new ArrayList<>();
		
		if (principal != null) {
			String userId = principal.getName();
			
			List<FixedCostEntity> fixedCostList = fixedCostRepository.findByUserIdAndName(userId, name);
			
			fixedCostList.forEach(fixedCost -> {
				Map<String, Object> map = new HashMap<>();
				
				map.put("userId", fixedCost.getUserId());
				map.put("id", fixedCost.getId());
				map.put("name", fixedCost.getName());
				map.put("amount", decimalFormat.format(fixedCost.getAmount()));
				
				list.add(map);
			});
			
			model.addAttribute("list", list);
			
			return "mobile/asset/set/fixedcost/index";
		}else {			
			return "redirect:/m/asset/set/fixedcost/index";
		}
	}
	
	@GetMapping("/new")
	public String newFixedCostGet(
			@ModelAttribute("fixedCost") FixedCostEntity fixedCost,
			Principal principal){
		if (principal != null) {
			String userId = principal.getName();
			
			String id = fixedCostRepository.getMaxIdByUserId(userId);
			
			if (id == null) {
				id = "001";
			}else {
				id = String.format("%03d", Integer.parseInt(id) + 1);
			}
			
			fixedCost.setId(id);
			
			return "mobile/asset/set/fixedcost/new";
		}else {
			return "redirect:/m/asset/set/fixedcost/index";
		}
	}
	
	@PostMapping("/new")
	public String newFixedCostPost(
			@ModelAttribute("fixedCost") @Valid FixedCostEntity fixedCost,
			BindingResult bindingResult,
			Principal principal,
			Model model){
		if (principal != null) {
			boolean isError = false;
			String idError = null;
			
			String userId = principal.getName();
			
			String id = fixedCost.getId();
			Double amount = fixedCost.getAmount();
			
			if (bindingResult.hasErrors()) {
				isError = true;
			}
			
			if(id.equals("")) {
				idError = "未輸入";
				isError = true;
			}
			
			if (isError) {
				model.addAttribute("idError", idError);
				
				if(amount != null) {
					model.addAttribute("amount", (int)((double)amount));
				}else {
					model.addAttribute("amount", amount);
				}
				
				return "mobile/asset/set/fixedcost/new";
			}
			
			fixedCost.setUserId(userId);
			fixedCost.setEnterUserId(userId);
			fixedCost.setUpdateUserId(userId);
			
			fixedCostRepository.save(fixedCost);
			
			return "redirect:/m/asset/set/fixedcost/index";
		}else {
			return "redirect:/m/asset/set/fixedcost/index";
		}
	}

	@GetMapping("/view/{userId}&{id}")
	public String viewFixedCostGet(
			@PathVariable("userId") String userId,
			@PathVariable("id") String id,
			Principal principal,
			Model model){
		if (principal != null) {
			Optional<FixedCostEntity> optional = fixedCostRepository.findByUserIdAndId(userId,id);
			
			if(optional.isPresent()) {
				FixedCostEntity fixedCost = optional.get();
				
				model.addAttribute("fixedCost", fixedCost);
				model.addAttribute("amount", (int)((double)fixedCost.getAmount()));
				
				return "mobile/asset/set/fixedcost/view";
			}else {
				return "redirect:/m/asset/set/fixedcost/index";
			}
		}else {
			return "redirect:/m/asset/set/fixedcost/index";
		}
	}

	@PutMapping("/view/{userId}&{id}")
    public String viewFixedCostPut(
    		@PathVariable("userId") String userId,
			@PathVariable("id") String id,
    		@ModelAttribute("fixedCost") @Valid FixedCostEntity fixedCost,
    		BindingResult bindingResult,
			Principal principal,
    		Model model){
		if (principal != null) {
			if(!fixedCost.getUserId().equals(userId) || !fixedCost.getId().equals(id)) {
				return "redirect:/m/asset/set/fixedcost/index";
			}
			
			boolean isError = false;
			String userIdError = null, idError = null;
			
			Double amount = fixedCost.getAmount();
			
			if(bindingResult.hasErrors()) {
				isError = true;
			}
			
			if(userId.equals("")) {
				userIdError = "未輸入";
				isError = true;
			}
			
			if(id.equals("")) {
				idError = "未輸入";
				isError = true;
			}
			
			if(isError) {
				model.addAttribute("userIdError", userIdError);
				model.addAttribute("idError", idError);
				
				if(amount != null) {
					model.addAttribute("amount", (int)((double)amount));
				}else {
					model.addAttribute("amount", amount);
				}
				
				return "mobile/asset/fixedcost/goal/view";
			}

			fixedCost.setUpdateUserId(principal.getName());
			
			fixedCostRepository.save(fixedCost);
			
			return "redirect:/m/asset/set/fixedcost/index";
		}else {
			return "redirect:/m/asset/set/fixedcost/index";
		}
    }
	
	@DeleteMapping("/view/{userId}&{id}")
    public String viewFixedCostDelete(
    		@PathVariable("userId") String userId,
			@PathVariable("id") String id,
			Principal principal){
		if (principal != null) {
			fixedCostRepository.deleteByUserIdAndId(userId,id);
			
			return "redirect:/m/asset/set/fixedcost/index";
		}else {
			return "redirect:/m/asset/set/fixedcost/index";
		}
    }
	
	@GetMapping("/search")
	public String searchFixedCostSearch(
			Principal principal){
		if (principal != null) {	
			return "mobile/asset/set/fixedcost/search";
		}else {
			return "redirect:/m/asset/set/fixedcost/index";
		}
	}
}
