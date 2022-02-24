package com.hkma.home.life.controller;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hkma.home.life.entity.ExpenseEntity;
import com.hkma.home.life.repository.ExpenseRepository;

@Controller("LifeDashboardExpenseData")
public class DashboardExpenseDataController {
	@Autowired
	private ExpenseRepository expenseRepository;
	
	@Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@RequestMapping("/life/dashboard/expense/data")
	public String data(
			@RequestParam(required=false, value="userId") String userId,
			@RequestParam(required=false, value="groupId") String groupId,
			Model model) {
		DecimalFormat decimalFormat = new DecimalFormat("###,###");
		SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");
		SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
		
		Calendar calendar = Calendar.getInstance();
		
		List<Map<String,Object>> thisMonthExpenseList = new ArrayList<>();
		List<Map<String,Object>> lastMonthExpenseList = new ArrayList<>();
		List<Map<String,Object>> lastYearExpenseList = new ArrayList<>();
		List<Map<String,Object>> expenseYearList = new ArrayList<>();
		
		double blance = 0;
		double thisMonthBlance = 0;
		double lastMonthBlance = 0;
		double lastYearBlance = 0;
		
		//本月
		List<ExpenseEntity> expenseEntityList = expenseRepository.findByUserIdAndGroupIdAndMonthOrderByRecordDate(userId, groupId, monthFormat.format(calendar.getTime()));
		for (ExpenseEntity expense : expenseEntityList) {
			String recordDate = expense.getRecordDate();
			double amount = expense.getAmount();
			
			Map<String,Object> record = new HashMap<>();
			record.put("recordDate", recordDate.substring(0, 4) + "/" + recordDate.substring(4, 6) + "/" + recordDate.substring(6, 8));
			record.put("memo", expense.getMemo());
			
			switch(expense.getTransMode()) {
				case "1":
					record.put("plus", decimalFormat.format(amount));
					record.put("minus", "");
					
					blance = blance + amount;
					
					break;
				case "2":
					record.put("plus", "");
					record.put("minus", decimalFormat.format(amount));
					
					blance = blance - amount;
					
					break;
			}
			
			thisMonthBlance = blance;
			
			record.put("blance", decimalFormat.format(blance));
			
			thisMonthExpenseList.add(record);
		};
		
		Collections.reverse(thisMonthExpenseList);
		
		//前月
		calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);
		blance = 0;
		
		expenseEntityList = expenseRepository.findByUserIdAndGroupIdAndMonthOrderByRecordDate(userId, groupId, monthFormat.format(calendar.getTime()));
		for (ExpenseEntity expense : expenseEntityList) {
			String recordDate = expense.getRecordDate();
			double amount = expense.getAmount();
			
			Map<String,Object> record = new HashMap<>();
			record.put("recordDate", recordDate.substring(0, 4) + "/" + recordDate.substring(4, 6) + "/" + recordDate.substring(6, 8));
			record.put("memo", expense.getMemo());
			
			switch(expense.getTransMode()) {
				case "1":
					record.put("plus", decimalFormat.format(amount));
					record.put("minus", "");
					
					blance = blance + amount;
					
					break;
				case "2":
					record.put("plus", "");
					record.put("minus", decimalFormat.format(amount));
					
					blance = blance - amount;
					
					break;
			}
			
			lastMonthBlance = blance;
			
			record.put("blance", decimalFormat.format(blance));
			
			lastMonthExpenseList.add(record);
		};
		
		Collections.reverse(lastMonthExpenseList);
		
		//前年
		calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -1);
		blance = 0;
		
		expenseEntityList = expenseRepository.findByUserIdAndGroupIdAndMonthOrderByRecordDate(userId, groupId, monthFormat.format(calendar.getTime()));
		for (ExpenseEntity expense : expenseEntityList) {
			String recordDate = expense.getRecordDate();
			double amount = expense.getAmount();
			
			Map<String,Object> record = new HashMap<>();
			record.put("recordDate", recordDate.substring(0, 4) + "/" + recordDate.substring(4, 6) + "/" + recordDate.substring(6, 8));
			record.put("memo", expense.getMemo());
			
			switch(expense.getTransMode()) {
				case "1":
					record.put("plus", decimalFormat.format(amount));
					record.put("minus", "");
					
					blance = blance + amount;
					
					break;
				case "2":
					record.put("plus", "");
					record.put("minus", decimalFormat.format(amount));
					
					blance = blance - amount;
					
					break;
			}
			
			lastYearBlance = blance;
			
			record.put("blance", decimalFormat.format(blance));
			
			lastYearExpenseList.add(record);
		};
		
		Collections.reverse(lastYearExpenseList);
		
		{
			calendar = Calendar.getInstance();
			String year = yearFormat.format(calendar.getTime());
			String sql = "call sp_life_getMonthExpenseByUserIdAndGroupIdAndYear(:in_userId, :in_groupId, :in_year);";
			MapSqlParameterSource params = new MapSqlParameterSource();
			params.addValue("in_userId", userId);
			params.addValue("in_groupId", groupId);
			params.addValue("in_year", year);
		
			List<Map<String,Object>> monthExpenseList = namedParameterJdbcTemplate.queryForList(sql, params);
			{
				List<Double> monthAmountList = new ArrayList<>();
				for (int i = 1; i <= 12; i++) {
					double amount = 0;
					
					for (Map<String, Object> monthExpense : monthExpenseList) {
						if(monthExpense.get("month").equals(year + String.format("%02d", i))) {
							amount = amount - Double.parseDouble(monthExpense.get("amount").toString());
						}
					}
					
					monthAmountList.add(amount);
				}
				
				{
					Map<String,Object> expenseYear = new HashMap<>();
					expenseYear.put("name", year);
					expenseYear.put("data", monthAmountList);
					expenseYearList.add(expenseYear);
				}
			}
		}
		
		{
			calendar = Calendar.getInstance();
			calendar.add(Calendar.YEAR, -1);
			String year = yearFormat.format(calendar.getTime());
			String sql = "call sp_life_getMonthExpenseByUserIdAndGroupIdAndYear(:in_userId, :in_groupId, :in_year);";
			MapSqlParameterSource params = new MapSqlParameterSource();
			params.addValue("in_userId", userId);
			params.addValue("in_groupId", groupId);
			params.addValue("in_year", year);
		
			List<Map<String,Object>> monthExpenseList = namedParameterJdbcTemplate.queryForList(sql, params);
			{
				List<Double> monthAmountList = new ArrayList<>();
				for (int i = 1; i <= 12; i++) {
					double amount = 0;
					
					for (Map<String, Object> monthExpense : monthExpenseList) {
						if(monthExpense.get("month").equals(year + String.format("%02d", i))) {
							amount = amount - Double.parseDouble(monthExpense.get("amount").toString());
						}
					}
					
					monthAmountList.add(amount);
				}
				
				{
					Map<String,Object> expenseYear = new HashMap<>();
					expenseYear.put("name", year);
					expenseYear.put("data", monthAmountList);
					expenseYearList.add(expenseYear);
				}
			}
		}
		
		{
			calendar = Calendar.getInstance();
			calendar.add(Calendar.YEAR, -2);
			String year = yearFormat.format(calendar.getTime());
			String sql = "call sp_life_getMonthExpenseByUserIdAndGroupIdAndYear(:in_userId, :in_groupId, :in_year);";
			MapSqlParameterSource params = new MapSqlParameterSource();
			params.addValue("in_userId", userId);
			params.addValue("in_groupId", groupId);
			params.addValue("in_year", year);
		
			List<Map<String,Object>> monthExpenseList = namedParameterJdbcTemplate.queryForList(sql, params);
			{
				List<Double> monthAmountList = new ArrayList<>();
				for (int i = 1; i <= 12; i++) {
					double amount = 0;
					
					for (Map<String, Object> monthExpense : monthExpenseList) {
						if(monthExpense.get("month").equals(year + String.format("%02d", i))) {
							amount = amount - Double.parseDouble(monthExpense.get("amount").toString());
						}
					}
					
					monthAmountList.add(amount);
				}
				
				{
					Map<String,Object> expenseYear = new HashMap<>();
					expenseYear.put("name", year);
					expenseYear.put("data", monthAmountList);
					expenseYearList.add(expenseYear);
				}
			}
		}
		
		model.addAttribute("thisMonthBlance", decimalFormat.format(thisMonthBlance));
		model.addAttribute("lastMonthBlance", decimalFormat.format(lastMonthBlance));
		model.addAttribute("lastYearBlance", decimalFormat.format(lastYearBlance));
		model.addAttribute("thisMonthExpenseList", thisMonthExpenseList);
		model.addAttribute("lastMonthExpenseList", lastMonthExpenseList);
		model.addAttribute("lastYearExpenseList", lastYearExpenseList);
		model.addAttribute("expenseYearList", expenseYearList);
		
		return "life/dashboard/expense/data";
	}
}
