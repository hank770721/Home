package com.hkma.home.mobile.life.controller;

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

import com.hkma.home.life.entity.TemperatureEntity;
import com.hkma.home.life.repository.TemperatureRepository;
import com.hkma.home.user.entity.UserAccountEntity;
import com.hkma.home.user.repository.UserAccountRepository;

@Controller("MobileLifeTemperature")
@RequestMapping("/m/life/record/temperature")
public class TemperatureController {
	@Autowired
	private TemperatureRepository temperatureRepository;
	
	@Autowired
	private UserAccountRepository userAccountRepository;
	
	@GetMapping({"/","/index"})
	public String indexTemperatureRecordGet(
			@RequestParam(required=false, value="userid") String userId,
			@RequestParam(required=false, value="month") String month,
			Principal principal,
			Model model){
		SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");
		
		if (userId == null) {
			if (principal != null){
				userId = principal.getName();
			}else {
				userId = "mia";
			}
		}
		
		if (month == null) {
			month = monthFormat.format(new Date());
		}

		List<Map<String, Object>> list2 = new ArrayList<>();
		
		List<TemperatureEntity> list = temperatureRepository.findByUserMonth(userId, month);
		
		list.forEach(temperatureRecord -> {
			double temperature;
			String recordDate, temperatureStr;
			Map<String, Object> map = new HashMap<>();
			
			recordDate = temperatureRecord.getRecordDate();
			temperature = temperatureRecord.getTemperature();
			
			recordDate = recordDate.substring(0,4) + "/" + recordDate.substring(4,6) + "/" + recordDate.substring(6,8);
			temperatureStr = temperature + "℃";
			
			map.put("recordId", temperatureRecord.getRecordId());
			map.put("recordDate", recordDate);
			map.put("temperature", temperatureStr);
			
			list2.add(map);
		});
		
		model.addAttribute("list", list2);
		
		return "mobile/life/record/temperature/index";
	}
	
	@PostMapping("/index")
	public String indexTemperatureRecordPost(
			@RequestParam(required=false, value="userid") String userId,
			@RequestParam(required=false, value="month") String month,
			Model model){
		SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");
		
		if (userId == null) {
			userId = "mia";
		}
		
		if (month == null) {
			month = monthFormat.format(new Date());
		}

		List<Map<String, Object>> list2 = new ArrayList<>();
		
		List<TemperatureEntity> list = temperatureRepository.findByUserMonth(userId, month);
		
		list.forEach(temperatureRecord -> {
			double temperature;
			String recordDate, temperatureStr;
			Map<String, Object> map = new HashMap<>();
			
			recordDate = temperatureRecord.getRecordDate();
			temperature = temperatureRecord.getTemperature();
			
			recordDate = recordDate.substring(0,4) + "/" + recordDate.substring(4,6) + "/" + recordDate.substring(6,8);
			temperatureStr = temperature + "℃";
			
			map.put("recordId", temperatureRecord.getRecordId());
			map.put("recordDate", recordDate);
			map.put("temperature", temperatureStr);
			
			list2.add(map);
		});
		
		model.addAttribute("list", list2);
		
		return "mobile/life/record/temperature/index";
	}
	
	@GetMapping("/new")
	public String newTemperatureRecordGet(
			@ModelAttribute("temperatureRecord") TemperatureEntity temperatureRecord,
			Principal principal,
			Model model){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		String userId;
		String recordDate = dateFormat.format(new Date());
		
		if (principal != null){
			userId = principal.getName();
		}else {
			userId = "mia";
		}
		
		temperatureRecord.setRecordDate(recordDate);
		temperatureRecord.setUserId(userId);
		
		List<UserAccountEntity> userAccountList = userAccountRepository.findAll();
		
		model.addAttribute("userAccountList",userAccountList);
		
		return "mobile/life/record/temperature/new";
	}
	
	@PostMapping("/new")
	public String newTemperatureRecordPost(
			@ModelAttribute("temperatureRecord") @Valid TemperatureEntity temperatureRecord,
			BindingResult bindingResult,
			Principal principal,
			Model model){
		String recordDate = temperatureRecord.getRecordDate();
		
		if (bindingResult.hasErrors()) {
			List<UserAccountEntity> userAccountList = userAccountRepository.findAll();
			
			model.addAttribute("userAccountList",userAccountList);
			
			return "mobile/life/record/temperature/new";
		}
		
		recordDate = recordDate.replaceAll("-","");
		temperatureRecord.setRecordDate(recordDate);
		
		String recordId;
		Optional<String> recordIdOptional = temperatureRepository.getMaxRecordIdByRecordDate(recordDate);
		
		if (!recordIdOptional.isPresent()) {
			recordId = recordDate + "0001";
		}else {
			recordId = recordIdOptional.get();
			recordId = recordDate + String.format("%04d", (Integer.parseInt(recordId.substring(8,12)) + 1) );
		}
		
		temperatureRecord.setRecordId(recordId);

		if (principal != null){
			temperatureRecord.setEnterUserId(principal.getName());
			temperatureRecord.setUpdateUserId(principal.getName());
		}
		
		temperatureRepository.save(temperatureRecord);
		
		return "redirect:/m/life/record/temperature/index";
	}

	@GetMapping("/view/{recordId}")
	public String viewTemperatureRecordGet(
			@PathVariable("recordId") String recordId,
			Model model){
		Optional<TemperatureEntity> recordOptional = temperatureRepository.findById(recordId);
		
		if(recordOptional.isPresent()) {
			TemperatureEntity temperatureRecord = recordOptional.get();
			
			String recordDate = temperatureRecord.getRecordDate();
			temperatureRecord.setRecordDate(recordDate.substring(0,4) + "-" + recordDate.substring(4,6) + "-" + recordDate.substring(6,8));
			
			model.addAttribute("temperatureRecord", temperatureRecord);
			
			return "mobile/life/record/temperature/view";
		}else {
			return "redirect:/m/life/record/temperature/index";
		}
	}

	@PutMapping("/view/{recordId}")
    public String viewTemperatureRecordPut(
    		@PathVariable("recordId") String recordId,
    		@ModelAttribute("temperatureRecord") @Valid TemperatureEntity temperatureRecord,
    		BindingResult bindingResult,
			Principal principal,
    		Model model){
		String recordDate = temperatureRecord.getRecordDate();
		
		if (bindingResult.hasErrors()) {
			return "mobile/life/record/temperature/view";
		}
		
		temperatureRecord.setRecordId(recordId);
		temperatureRecord.setRecordDate(recordDate.replaceAll("-",""));
		
		if (principal != null){
			temperatureRecord.setUpdateUserId(principal.getName());
		}
		
		temperatureRepository.save(temperatureRecord);
		
		return "redirect:/m/life/record/temperature/index";
    }
	
	@DeleteMapping("/view/{recordId}")
    public String viewTemperatureRecordDelete(
    		@PathVariable("recordId") String recordId){
		temperatureRepository.deleteById(recordId);
		
		return "redirect:/m/life/record/temperature/index";
    }
	
	@GetMapping("/search")
	public String searchTemperatureRecord(
			Principal principal,
			Model model){
		SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");

		String userId = "mia";
		String month = monthFormat.format(new Date());
		List<String> userIdList = temperatureRepository.getUserIdList();
		
		if (principal != null){
			userId = principal.getName();
		}
		
		model.addAttribute("userIdList", userIdList);
		model.addAttribute("userId", userId);
		model.addAttribute("month", month);
		
		return "mobile/life/record/temperature/search";
	}
}
