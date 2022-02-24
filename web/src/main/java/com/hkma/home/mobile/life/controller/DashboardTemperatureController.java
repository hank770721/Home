package com.hkma.home.mobile.life.controller;


import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hkma.home.life.entity.TemperatureEntity;
import com.hkma.home.life.repository.TemperatureRepository;

@Controller("MobileLifeTemperatureDashboard")
@RequestMapping("/m/life/dashboard/temperature")
public class DashboardTemperatureController {
	@Autowired
	private TemperatureRepository temperatureRepository;
	
	@GetMapping("/view")
	public String viewTemperature(
			@RequestParam(required=false, value="userid") String userId,
			@RequestParam(required=false, value="recordDate") String recordDate,
			Model model){
		SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");

		List<String> recordDateList = new ArrayList<>();
		List<Double> temperatureList = new ArrayList<>();
		List<String> recordDateListPart = new ArrayList<>();
		List<Double> temperatureListPart = new ArrayList<>();
		
		if (userId == null || userId.equals("")) {
			userId = "mia";
		}
		
		if (recordDate == null) {
			recordDate = monthFormat.format(new Date());
		}

		List<TemperatureEntity> temperatureClassList = temperatureRepository.findByUserIdLikeRecordDate(userId, recordDate);

		temperatureClassList.forEach(temperature -> {
			String recordDateData = temperature.getRecordDate();
			
			recordDateList.add(recordDateData.substring(0,4) + "/" + recordDateData.substring(4,6) + "/" + recordDateData.substring(6,8));
			temperatureList.add(temperature.getTemperature());
		});
		
		if (recordDateList.size() > 30) {
			recordDateListPart = recordDateList.subList(recordDateList.size() - 30, recordDateList.size());
			temperatureListPart = temperatureList.subList(recordDateList.size() - 30, recordDateList.size());
		}else {
			recordDateListPart = recordDateList;
			temperatureListPart = temperatureList;
		}
		
		model.addAttribute("recordDateList", recordDateList);
		model.addAttribute("temperatureList", temperatureList);
		model.addAttribute("recordDateListPart", recordDateListPart);
		model.addAttribute("temperatureListPart", temperatureListPart);
		model.addAttribute("partIndex", recordDateList.size() - 30);
		
		return "mobile/life/dashboard/temperature/view4";
	}
	
	@GetMapping("/search")
	public String searchTemperature(
			Principal principal,
			Model model){
		String userId = "mia";

		List<String> userIdList = temperatureRepository.getUserIdList();
		
		if (principal != null){
			userId = principal.getName();
		}
		
		model.addAttribute("userIdList", userIdList);
		model.addAttribute("userId", userId);
		
		return "mobile/life/dashboard/temperature/search";
	}
}
