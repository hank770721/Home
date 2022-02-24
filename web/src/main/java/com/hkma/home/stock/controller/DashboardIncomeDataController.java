package com.hkma.home.stock.controller;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("StockDashboardIncomeData")
public class DashboardIncomeDataController {
	
	@RequestMapping("/stock/dashboard/income/data")
	public String data(
			@RequestParam(required=false, value = "accountUserId") String accountUserId,
			@RequestParam(required=false, value = "accountId") String accountId,
			Model model){
		
		List<String> dates = new ArrayList<String>();
	    List<HashMap<String,Object>> stockDailys = new ArrayList<>();
	    
	    Connection con = null;
	    CallableStatement cStmt = null;
	    ResultSet rs = null;
	    
	    Calendar calendar = Calendar.getInstance();
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	    Date endDate = new Date();
	    calendar.setTime(endDate);
	    
	    try {
	    	con = DriverManager.getConnection("jdbc:mysql://localhost:3306/home?serverTimezone=UTC","user01","1234");
	        
	        //<editor-fold desc="stockDaily">
	        cStmt = con.prepareCall("call sp_stock_getStockDaily(?,?,?);");
	        cStmt.setString(1, accountUserId);
	        cStmt.setString(2, accountId);
	        cStmt.setString(3, dateFormat.format(endDate));
	        rs = cStmt.executeQuery();
	
	        while (rs.next()) {
	            boolean isFind = false;
	            String date = rs.getString("date").substring(0, 4) + "/" + rs.getString("date").substring(4, 6) + "/" + rs.getString("date").substring(6, 8);
	            final String assetType = rs.getString("assetType");
	            final String stockId = rs.getString("stockId");
	            final String stockName = rs.getString("stockName");
	            final double stock = rs.getDouble("amount");
	            final double cost = rs.getDouble("cost");
	            
	            if (dates.indexOf(date) < 0){
	                dates.add(date);
	            }
	
	            for(HashMap<String,Object> map : stockDailys){
	                if(map.get("assetType").toString().equals(assetType) && map.get("stockId").toString().equals(stockId)){
	                    isFind = true;
	                    ((ArrayList)map.get("stock")).add(stock);
	                    ((ArrayList)map.get("cost")).add(cost);
	                }
	            }
	
	            if(!isFind){
	                stockDailys.add(new HashMap<String,Object>(){
	                    {
	                        put("assetType", assetType);
	                        put("stockId", stockId);
	                        put("stockName", stockName);
	                        put("stock", new ArrayList<Double>(){
	                            {
	                                add(stock);
	                            }
	                        });
	                        put("cost", new ArrayList<Double>(){
	                            {
	                                add(cost);
	                            }
	                        });
	                    }
	                });
	            }
	        }
	        //</editor-fold>
	        
	        model.addAttribute("dates", dates);
	        model.addAttribute("stockDailys", stockDailys);
	    } catch (SQLException ex) {
	        System.out.println(ex);
	    } finally {
	        if (rs != null) {
	            try {
	                rs.close();
	            } catch (SQLException ex) {
	                System.out.println(ex);
	            }
	        }
	
	        if (cStmt != null) {
	            try {
	                cStmt.close();
	            } catch (SQLException ex) {
	                System.out.println(ex);
	            }
	        }
	
	        if (con != null) {
	            try {
	                con.close();
	            } catch (SQLException ex) {
	                System.out.println(ex);
	            }
	        }
	    }
	    
	    return "stock/dashboard/income/data";
	}
}
