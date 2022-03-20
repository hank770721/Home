package com.hkma.home.mobile.bank.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("MobileBankTableRecordView")
@RequestMapping("/m/bank/table/record")
public class TableRecordController {
	//@Autowired
	//private JdbcTemplate jdbcTemplate;
	
	@Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@GetMapping("/view")
	public String view(
			@RequestParam(required=false, value = "type") String type,
			@RequestParam(required=false, value = "accountUserId") String accountUserId,
			@RequestParam(required=false, value = "accountId") String accountId,
			@RequestParam(required=false, value = "groupUserId") String groupUserId,
			@RequestParam(required=false, value = "groupId") String groupId,
			Principal principal,
			Model model){
		String userId;
		List<Map<String,Object>> list = new ArrayList<>();
		
		if (principal != null){
			userId = principal.getName();
		}else {
			userId = "";
		}
		
		//list = jdbcTemplate.queryForList("call home.sp_asset_detail;");
		
		String sql = "call home.sp_bank_record(:userId, :type, :accountUserId, :accountId, :groupUserId, :groupId);";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("userId", userId);
		params.addValue("type", type);
		params.addValue("accountUserId", accountUserId);
		params.addValue("accountId", accountId);
		params.addValue("groupUserId", groupUserId);
		params.addValue("groupId", groupId);
		
		list = namedParameterJdbcTemplate.queryForList(sql, params);

        model.addAttribute("list", list);
		
		return "mobile/bank/table/record/view";
	}
	
	@GetMapping("/search")
	public String search(
			Principal principal,
			Model model){
		String accountUserId, accountId;
		
		if (principal != null){
			accountUserId = "";
			accountId = "";
		}else {
			accountUserId = "mia";
			accountId = "001";
		}
		
		model.addAttribute("accountUserId", accountUserId);
		model.addAttribute("accountId", accountId);
		
		return "mobile/bank/table/record/search";
	}
}
