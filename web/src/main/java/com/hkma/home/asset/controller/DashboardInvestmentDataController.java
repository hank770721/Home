package com.hkma.home.asset.controller;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
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

@Controller("AssetDashboardInvestmentData")
public class DashboardInvestmentDataController {
	
	@RequestMapping("/asset/dashboard/investment/data")
	public String data(
			@RequestParam(required=false, value="accountUserId") String accountUserId,
			@RequestParam(required=false, value="accountId") String accountId,
			Model model){		
		double totalAmount = 0;
		List<HashMap<String,Object>> assetDetail = new ArrayList<>();
		List<HashMap<String,Object>> stockInventory = new ArrayList<>();
		
		List<String> months = new ArrayList<>();
        List<Double> deposits = new ArrayList<>();
        List<Double> totalStocks = new ArrayList<>();
        List<Double> totals = new ArrayList<>();
        List<Double> saveMoneys = new ArrayList<>();
        
        List<String> dates = new ArrayList<>();
        List<HashMap<String,Object>> stockDailys = new ArrayList<>();
        List<HashMap<String,Object>> stocks = new ArrayList<>();
        List<HashMap<String,Object>> pieData = new ArrayList<>();
        String pieSubtitleText = "";

        Date beginDate, endDate;
		Connection con = null;
        CallableStatement cStmt = null;
        ResultSet rs = null;
        
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        final DecimalFormat decimalFormat = new DecimalFormat("###,###");
        final DecimalFormat decimalFormat2 = new DecimalFormat("###,###.00");
        
        endDate = new Date();
        
		try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/home?serverTimezone=UTC","user01","1234");
            
            calendar.setTime(endDate);
            calendar.add(Calendar.MONTH, -23);
            beginDate = calendar.getTime();
            
            //assetDetail
            cStmt = con.prepareCall("call home.sp_bank_getRecord(?,?);");
            cStmt.setString(1, accountUserId);
            cStmt.setString(2, accountId);
            rs = cStmt.executeQuery();
            
            int row = 0;
            while (rs.next()) {
                final String recordDate = rs.getString("recordDate");
                final String memo = rs.getString("memo");
                final double plus = rs.getDouble("plus");
                final double minus = rs.getDouble("minus");
                final double blance = rs.getDouble("blance");
                
                assetDetail.add(new HashMap<String,Object>(){
                    {
                        put("recordDate", recordDate.substring(0,4) + "/" + recordDate.substring(4,6) + "/" + recordDate.substring(6,8));
                        put("memo", memo);
                        put("plus", (plus == 0 ? "" : decimalFormat.format(plus) ) );
                        put("minus", (minus == 0 ? "" : decimalFormat.format(minus) ) );
                        put("blance", (blance == 0 ? "" : decimalFormat.format(blance) ) );
                    }
                });
                
                if(row == 0){
                    totalAmount = totalAmount + blance;
                }
                    
                row++;
            }
            
            rs.close();
            cStmt.close();
            
            /*stockInventory*/
            cStmt = con.prepareCall("call home.sp_stock_getInventory(?,?);");
            cStmt.setString(1, accountUserId);
            cStmt.setString(2, accountId);
            rs = cStmt.executeQuery();
            
            {
                double sumAmount = 0;
                double sumCost = 0;
                double sumProfit = 0;
                
                while (rs.next()) {
                    final String stockId = rs.getString("stockId");
                    final String stockName = rs.getString("stockName");
                    final double quantity = rs.getDouble("quantity");
                    final double price = rs.getDouble("price");
                    final double amount = rs.getDouble("amount");
                    final double cost = rs.getDouble("cost");
                    final double profit = rs.getDouble("profit");

                    stockInventory.add(new HashMap<String,Object>(){
                        {
                            put("stockId", stockId);
                            put("stockName", stockName);
                            put("quantity", decimalFormat.format(quantity) );
                            put("price", decimalFormat2.format(price) );
                            put("amount", decimalFormat.format(amount) );
                            put("cost", decimalFormat.format(cost) );
                            put("profit", decimalFormat.format(profit) );
                        }
                    });
                    
                    sumAmount += amount;
                    sumCost += cost;
                    sumProfit += profit;
                }
                
                final double sumAmount2 = sumAmount;
                final double sumCost2 = sumCost;
                final double sumProfit2 = sumProfit;
                
                stockInventory.add(new HashMap<String,Object>(){
                    {
                        put("stockId", "合計");
                        put("stockName", "");
                        put("quantity", "");
                        put("price", "");
                        put("amount", decimalFormat.format(sumAmount2) );
                        put("cost", decimalFormat.format(sumCost2) );
                        put("profit", decimalFormat.format(sumProfit2) );
                    }
                });
                
                totalAmount = totalAmount + sumAmount;
            }
            
            cStmt.close();

            //saveMoney, month
            cStmt = con.prepareCall("call home.sp_bank_getSaveMoney(?,?,?,?);");
            cStmt.setString(1, accountUserId);
            cStmt.setString(2, accountId);
            cStmt.setString(3, monthFormat.format(beginDate));
            cStmt.setString(4, monthFormat.format(endDate));
            rs = cStmt.executeQuery();

            while (rs.next()) {
                months.add(rs.getString("month").substring(0, 4) + "/" + rs.getString("month").substring(4, 6) );
                saveMoneys.add(Double.parseDouble(rs.getString("amount")));
            }
            
            rs.close();
            cStmt.close();
            
            //stock, cost, dividend, profit
            cStmt = con.prepareCall("call home.sp_stock_getStockType(?,?,?,?);");
            cStmt.setString(1, accountUserId);
            cStmt.setString(2, accountId);
            cStmt.setString(3, monthFormat.format(beginDate));
            cStmt.setString(4, monthFormat.format(endDate));
            rs = cStmt.executeQuery();

            while (rs.next()) {
                boolean isFind = false;
                final String typeId = rs.getString("typeId");
                final String typeName = rs.getString("typeName");
                final double stock = rs.getDouble("stock");
                final double cost = rs.getDouble("cost");
                final double dividend = rs.getDouble("dividend");
                final double profit = rs.getDouble("profit");

                for(HashMap<String,Object> map : stocks){                            
                    if (map.get("typeId").toString().equals(typeId) ){
                        ( (ArrayList)map.get("stock") ).add(stock);
                        ( (ArrayList)map.get("cost") ).add(cost);
                        ( (ArrayList)map.get("dividend") ).add(dividend);
                        ( (ArrayList)map.get("profit") ).add(profit);

                        isFind = true;
                    }
                }

                if (!isFind){
                    stocks.add(new HashMap<String,Object>(){
                        {
                            put("typeId", typeId);
                            put("typeName", typeName);
                            put("stock", new ArrayList(){
                                {
                                    add(stock);
                                }
                            });
                            put("cost", new ArrayList(){
                                {
                                    add(cost);
                                }
                            });
                            put("dividend", new ArrayList(){
                                {
                                    add(dividend);
                                }
                            });
                            put("profit", new ArrayList(){
                                {
                                    add(profit);
                                }
                            });
                        }
                    });
                }
            }
            
            cStmt.close();
            
            //summary
            for (int i = 0; i < months.size(); i++){
                double saveMoney = Double.parseDouble(saveMoneys.get(i) + "");
                double deposit = saveMoney;
                double total = saveMoney;
                double totalStock = 0;

                for(HashMap<String,Object> map : stocks){
                    double cost = Double.parseDouble(((ArrayList)map.get("cost")).get(i) + "");
                    double dividend = Double.parseDouble(((ArrayList)map.get("dividend")).get(i) + "");
                    double profit = Double.parseDouble(((ArrayList)map.get("profit")).get(i) + "");
                    double stock = Double.parseDouble(((ArrayList)map.get("stock")).get(i) + "");
                    
                    deposit = deposit - cost + dividend + profit;
                    total = total - cost + stock + dividend + profit;
                    totalStock = totalStock + stock;
                }
                
                deposits.add(deposit);
                totals.add(total);
                totalStocks.add(totalStock);
            }
            
            //stockDaily
            cStmt = con.prepareCall("call home.sp_stock_getStockTypeDaily(?,?,?);");
            cStmt.setString(1, accountUserId);
            cStmt.setString(2, accountId);
            cStmt.setString(3, dateFormat.format(endDate));
            rs = cStmt.executeQuery();

            while (rs.next()) {
                boolean isFind = false;
                String date = rs.getString("date").substring(0, 4) + "/" + rs.getString("date").substring(4, 6) + "/" + rs.getString("date").substring(6, 8);
                final String typeId = rs.getString("typeId");
                final double stock = rs.getDouble("amount");
                final double cost = rs.getDouble("cost");
                
                if (dates.indexOf(date) < 0){
                    dates.add(date);
                }

                for(HashMap<String,Object> map : stockDailys){
                    if(map.get("typeId").toString().equals(typeId)){
                        isFind = true;
                        ((ArrayList)map.get("stock")).add(stock);
                        ((ArrayList)map.get("cost")).add(cost);
                        ((ArrayList)map.get("difference")).add(stock - cost);
                    }
                }

                if(!isFind){
                    stockDailys.add(new HashMap<String,Object>(){
                        {
                            put("typeId", typeId);
                            put("stock", new ArrayList(){
                                {
                                    add(stock);
                                }
                            });
                            put("cost", new ArrayList(){
                                {
                                    add(cost);
                                }
                            });
                            put("difference", new ArrayList(){
                                {
                                    add(stock - cost);
                                }
                            });
                        }
                    });
                }
            }
            
            //pieData
            for (final HashMap<String,Object> map : stocks){
                double cost = (double)((ArrayList)map.get("cost")).get(months.size() - 1);
                double profit = (double)((ArrayList)map.get("profit")).get(months.size() - 1);
                double dividend = (double)((ArrayList)map.get("dividend")).get(months.size() - 1);
                final double y = (cost - profit - dividend < 0) ? 0 : (cost - profit - dividend);
                
                pieData.add(new HashMap<String,Object>(){
                    {
                        put("name", map.get("typeName"));
                        put("y", y);
                    }
                });
            }
            
            {
                double y = Double.parseDouble(saveMoneys.get(saveMoneys.size() - 1) + "");
                for(HashMap<String,Object> map : pieData){
                    y = y - Double.parseDouble(map.get("y") + "");
                }

                HashMap<String,Object> pieDataMap = new HashMap<>();
                pieDataMap.put("name", "餘額");
                pieDataMap.put("y", y);
                
                pieData.add(pieDataMap);
            }
            
            for(HashMap<String,Object> map : stocks){
                String typeName = map.get("typeName").toString();
                int saveMoneysSub = (int)(Double.parseDouble(saveMoneys.get(saveMoneys.size() - 1) + "") / 3);
                int costSub = (int)(Double.parseDouble(((ArrayList)map.get("cost")).get(months.size() - 1) + ""));
                int profitSub = (int)(Double.parseDouble(((ArrayList)map.get("profit")).get(months.size() - 1) + ""));
                int dividendSub = (int)(Double.parseDouble(((ArrayList)map.get("dividend")).get(months.size() - 1) + ""));
                
                pieSubtitleText = pieSubtitleText + typeName + "尚可投資 " + decimalFormat.format(saveMoneysSub) + " - " + decimalFormat.format(costSub) + " + " + decimalFormat.format(profitSub) + " + " + decimalFormat.format(dividendSub) + " = " + decimalFormat.format( (saveMoneysSub - costSub + profitSub + dividendSub) ) + "<br/>";
            }
            
            con.close();
            
            model.addAttribute("deposit", assetDetail.get(0).get("blance") );
            model.addAttribute("stock", stockInventory.get(stockInventory.size() - 1).get("amount") );
            model.addAttribute("totalAmount", decimalFormat.format(totalAmount) );
            
            model.addAttribute("assetDetail", assetDetail);
            model.addAttribute("stockInventory", stockInventory);
            
            model.addAttribute("months", months);
            model.addAttribute("deposits", deposits);
            model.addAttribute("totalStocks", totalStocks);
            model.addAttribute("totals", totals);
            model.addAttribute("saveMoneys", saveMoneys);
            
            model.addAttribute("dates", dates);
            model.addAttribute("stockDailys", stockDailys);
            
            model.addAttribute("pieSubtitleText", pieSubtitleText);
            model.addAttribute("pieData", pieData);
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
		
		return "asset/dashboard/investment/data";
	}
	
	@RequestMapping("/asset/dashboard/investment2/data")
	public String data2(
			@RequestParam(value="accountUserId") String accountUserId,
			@RequestParam(value="accountId") String accountId,
			@RequestParam(value="goal") String goal_param,
			Model model){	
		
		List<String> months = new ArrayList<>();
        List<Double> saveMoneys = new ArrayList<>();
        List<Double> deposits = new ArrayList<>();
        List<Double> totals = new ArrayList<>();
        List<Double> totalStocks = new ArrayList<>();
        List<String> dates = new ArrayList<>();
        List<HashMap<String,Object>> stockDailys = new ArrayList<>();
        List<HashMap<String,Object>> goalList = new ArrayList<>();
        List<HashMap<String,Object>> stocks = new ArrayList<>();
        List<HashMap<String,Object>> pieData = new ArrayList<>();
        String pieSubtitleText = "";

        double money = 0, goal = 0;
        String goalMoney;
        Date beginDate, endDate, goalDate = new Date();
        Connection con = null;
        CallableStatement cStmt = null;
        ResultSet rs = null;
        
        try{
            goal = Double.parseDouble(goal_param);
        }catch(NullPointerException|NumberFormatException ex){
        }
        
        goalMoney = com.hkma.home.system.service.Number.chineseConvert(goal);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        final SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        final DecimalFormat decimalFormat = new DecimalFormat("###,##0");

        endDate = new Date();

        try {
        	con = DriverManager.getConnection("jdbc:mysql://localhost:3306/home?serverTimezone=UTC","user01","1234");
            
            calendar.setTime(endDate);
            calendar.add(Calendar.MONTH, -23);
            beginDate = calendar.getTime();

            //<editor-fold desc="saveMoney, month">
            cStmt = con.prepareCall("call home.sp_bank_getSaveMoney(?,?,?,?);");
            cStmt.setString(1, accountUserId);
            cStmt.setString(2, accountId);
            cStmt.setString(3, monthFormat.format(beginDate));
            cStmt.setString(4, monthFormat.format(endDate));
            rs = cStmt.executeQuery();

            while (rs.next()) {
                months.add(rs.getString("month").substring(0, 4) + "/" + rs.getString("month").substring(4, 6));
                saveMoneys.add(rs.getDouble("amount"));
            }
            
            rs.close();
            cStmt.close();
            //</editor-fold>
                
            //<editor-fold desc="stock, cost, dividend, profit">
            cStmt = con.prepareCall("call home.sp_stock_getStockType(?,?,?,?);");
            cStmt.setString(1, accountUserId);
            cStmt.setString(2, accountId);
            cStmt.setString(3, monthFormat.format(beginDate));
            cStmt.setString(4, monthFormat.format(endDate));
            rs = cStmt.executeQuery();

            while (rs.next()) {
                boolean isFind = false;
                final String typeId = rs.getString("typeId");
                final String typeName = rs.getString("typeName");
                final double stock = rs.getDouble("stock");
                final double cost = rs.getDouble("cost");
                final double dividend = rs.getDouble("dividend");
                final double profit = rs.getDouble("profit");

                for(HashMap map : stocks){                            
                    if (map.get("typeId").toString().equals(typeId) ){
                        ( (ArrayList)map.get("stock") ).add(stock);
                        ( (ArrayList)map.get("cost") ).add(cost);
                        ( (ArrayList)map.get("dividend") ).add(dividend);
                        ( (ArrayList)map.get("profit") ).add(profit);

                        isFind = true;
                    }
                }

                if (!isFind){
                    stocks.add(new HashMap<String, Object>(){
                        {
                            put("typeId", typeId);
                            put("typeName", typeName);
                            put("stock", new ArrayList(){
                                {
                                    add(stock);
                                }
                            });
                            put("cost", new ArrayList(){
                                {
                                    add(cost);
                                }
                            });
                            put("dividend", new ArrayList(){
                                {
                                    add(dividend);
                                }
                            });
                            put("profit", new ArrayList(){
                                {
                                    add(profit);
                                }
                            });
                        }
                    });
                }
            }
            
            rs.close();
            cStmt.close();
            //</editor-fold>

            //<editor-fold desc="summary">
            for (int i = 0; i < months.size(); i++){
                double saveMoney = Double.parseDouble(saveMoneys.get(i) + "");
                double deposit = saveMoney;
                double total = saveMoney;
                double totalStock = 0;

                for(HashMap map : stocks){
                    double cost = Double.parseDouble(((ArrayList)map.get("cost")).get(i) + "");
                    double dividend = Double.parseDouble(((ArrayList)map.get("dividend")).get(i) + "");
                    double profit = Double.parseDouble(((ArrayList)map.get("profit")).get(i) + "");
                    double stock = Double.parseDouble(((ArrayList)map.get("stock")).get(i) + "");
                    
                    deposit = deposit - cost + dividend + profit;
                    total = total - cost + stock + dividend + profit;
                    totalStock = totalStock + stock;

                    //現在資產
                    money = total;
                }
                
                deposits.add(deposit);
                totals.add(total);
                totalStocks.add(totalStock);
            }
            //</editor-fold>

            //<editor-fold desc="stockDaily">
            cStmt = con.prepareCall("call home.sp_stock_getStockTypeDaily(?,?,?);");
            cStmt.setString(1, accountUserId);
            cStmt.setString(2, accountId);
            cStmt.setString(3, dateFormat.format(endDate));
            rs = cStmt.executeQuery();

            while (rs.next()) {
                boolean isFind = false;
                String date = rs.getString("date").substring(0, 4) + "/" + rs.getString("date").substring(4, 6) + "/" + rs.getString("date").substring(6, 8);
                final String typeId = rs.getString("typeId");
                final double stock = rs.getDouble("amount");
                final double cost = rs.getDouble("cost");
                
                if (dates.indexOf(date) < 0){
                    dates.add(date);
                }

                for(HashMap map : stockDailys){
                    if(map.get("typeId").toString().equals(typeId)){
                        isFind = true;
                        ((ArrayList)map.get("stock")).add(stock);
                        ((ArrayList)map.get("cost")).add(cost);
                    }
                }

                if(!isFind){
                    stockDailys.add(new HashMap<String,Object>(){
                        {
                            put("typeId", typeId);
                            put("stock", new ArrayList(){
                                {
                                    add(stock);
                                }
                            });
                            put("cost", new ArrayList(){
                                {
                                    add(cost);
                                }
                            });
                        }
                    });
                }
            }
            
            //20220307 做空資料
            if (dates.size() == 0) {
            	dates.add(dateFormat2.format(endDate));
            	
            	stockDailys.add(new HashMap<String,Object>(){
                    {
                        put("typeId", "1");
                        put("stock", new ArrayList(){
                            {
                                add(0);
                            }
                        });
                        put("cost", new ArrayList(){
                            {
                                add(0);
                            }
                        });
                    }
                });
            	
            	stockDailys.add(new HashMap<String,Object>(){
                    {
                        put("typeId", "2");
                        put("stock", new ArrayList(){
                            {
                                add(0);
                            }
                        });
                        put("cost", new ArrayList(){
                            {
                                add(0);
                            }
                        });
                    }
                });
            }
            
            rs.close();
            cStmt.close();
            //</editor-fold>

            //<editor-fold desc="goal">
            HashMap goalCycleMap = new HashMap();
            //20220307 改用Statement
            //Statement stmt = con.createStatement();
            //rs = stmt.executeQuery("SELECT id, month, day, hour, minute, second, cycleQty, cycleType, amount, memo FROM asset_goalcycle;");
            PreparedStatement ps = null;
            ps = con.prepareStatement("SELECT id, month, day, hour, minute, second, cycleQty, cycleType, amount, memo FROM asset_goalcycle WHERE accountUserId = ?;");
            ps.setString(1, accountUserId);
            rs = ps.executeQuery();
            ResultSetMetaData md = rs.getMetaData();
            int columns = md.getColumnCount();
            while (rs.next()) {
                HashMap map = new HashMap();
                for (int i = 1; i <= columns; i++) {
                    map.put(md.getColumnName(i), rs.getObject(i));
                }

                goalCycleMap.put(rs.getObject("id"), map);
            }

            Calendar now = Calendar.getInstance();

            for (Object a : goalCycleMap.keySet()) {
                int month, day, hour, minute, second;
                String cycleType;

                HashMap map = (HashMap)goalCycleMap.get(a);
                calendar.setTime(now.getTime());

                if (map.get("month") != null) {
                    month = Integer.parseInt(map.get("month") + "");
                } else {
                    month = calendar.get(Calendar.MONTH) + 1;
                }

                day = Integer.parseInt(map.get("day") + "");
                hour = Integer.parseInt(map.get("hour") + "");
                minute = Integer.parseInt(map.get("minute") + "");
                second = Integer.parseInt(map.get("second") + "");
                cycleType = (String)map.get("cycleType");

                calendar.set(Calendar.MONTH, month - 1);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, second);

                if (calendar.getTime().getTime() < now.getTime().getTime()) {
                    switch (cycleType) {
                        case "1":
                            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + Integer.parseInt(map.get("cycleQty") + "") );
                            break;
                        case "2":
                            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + Integer.parseInt(map.get("cycleQty") + "") );
                            break;
                    }
                }

                map.put("actionDate", calendar.getTime());
            }

            while (money < goal) {
                int id = 0;
                Date actionDate = null;
                
                for (Object key : goalCycleMap.keySet()) {
                    HashMap map = (HashMap)goalCycleMap.get(key);

                    if (actionDate == null) {
                        id = (int)map.get("id");
                        actionDate = (Date)map.get("actionDate");
                    } else {
                        if (((Date)map.get("actionDate")).getTime() - actionDate.getTime() < 0) {
                            id = (int)map.get("id");
                            actionDate = (Date)map.get("actionDate");
                        }
                    }
                }

                HashMap map = (HashMap)goalCycleMap.get(id);
                goalDate = (Date)map.get("actionDate");
                money = money + Double.parseDouble(map.get("amount") + "");
                String cycleType = (String)map.get("cycleType");
                final String memo = (String)map.get("memo");
                
                final Date goalDateSub = goalDate;
                final double moneySub = money;
                goalList.add(new HashMap<String, Object>(){
                    {
                        put("recordDate", dateFormat2.format(goalDateSub));
                        put("money", decimalFormat.format(moneySub));
                        put("memo", memo);
                    }
                });

                switch (cycleType) {
                    case "1":
                        calendar.setTime((Date)map.get("actionDate"));
                        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + (int)map.get("cycleQty"));
                        map.replace("actionDate", calendar.getTime());
                        break;
                    case "2":
                        calendar.setTime((Date)map.get("actionDate"));
                        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + (int)map.get("cycleQty"));
                        map.replace("actionDate", calendar.getTime());
                        break;
                }
            }
            //</editor-fold>
            
            //<editor-fold desc="pieData">
            for (final HashMap map : stocks){
                double cost = (double)((ArrayList)map.get("cost")).get(months.size() - 1);
                double profit = (double)((ArrayList)map.get("profit")).get(months.size() - 1);
                double dividend = (double)((ArrayList)map.get("dividend")).get(months.size() - 1);
                final double y = (cost - profit - dividend < 0) ? 0 : (cost - profit - dividend);
                
                pieData.add(new HashMap<String, Object>(){
                    {
                        put("name", map.get("typeName"));
                        put("y", y);
                    }
                });
            }
            
            {
                double y = Double.parseDouble(saveMoneys.get(saveMoneys.size() - 1) + "");
                for(HashMap map : pieData){
                    y = y - Double.parseDouble(map.get("y") + "");
                }

                HashMap<String, Object> pieDataMap = new HashMap<>();
                pieDataMap.put("name", "餘額");
                pieDataMap.put("y", y);
                
                pieData.add(pieDataMap);
            }
            //</editor-fold>
            
            //<editor-fold desc="pieSubtitleText">
            for(HashMap map : stocks){
                String typeName = map.get("typeName").toString();
                int saveMoneysSub = (int)(Double.parseDouble(saveMoneys.get(saveMoneys.size() - 1) + "") / 3);
                int costSub = (int)(Double.parseDouble(((ArrayList)map.get("cost")).get(months.size() - 1) + ""));
                int profitSub = (int)(Double.parseDouble(((ArrayList)map.get("profit")).get(months.size() - 1) + ""));
                int dividendSub = (int)(Double.parseDouble(((ArrayList)map.get("dividend")).get(months.size() - 1) + ""));
                
                pieSubtitleText = pieSubtitleText + typeName + "尚可投資 " + decimalFormat.format(saveMoneysSub) + " - " + decimalFormat.format(costSub) + " + " + decimalFormat.format(profitSub) + " + " + decimalFormat.format(dividendSub) + " = " + decimalFormat.format( (saveMoneysSub - costSub + profitSub + dividendSub) ) + "<br/>";
            }
            //</editor-fold>
            
            model.addAttribute("money", decimalFormat.format(totals.get(totals.size() - 1)));
            model.addAttribute("goalMoney", goalMoney);
            model.addAttribute("goalDate", timeFormat.format(goalDate));
            model.addAttribute("goalList", goalList);
            
            model.addAttribute("months", months);
            model.addAttribute("deposits", deposits);
            model.addAttribute("totalStocks", totalStocks);
            model.addAttribute("totals", totals);
            model.addAttribute("saveMoneys", saveMoneys);
            
            model.addAttribute("dates", dates);
            model.addAttribute("stockDailys", stockDailys);
            
            model.addAttribute("pieSubtitleText", pieSubtitleText);
            model.addAttribute("pieData", pieData);
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
		
		return "asset/dashboard/investment2/data";
	}
}
