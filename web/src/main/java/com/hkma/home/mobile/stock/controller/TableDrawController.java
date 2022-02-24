package com.hkma.home.mobile.stock.controller;

import java.security.Principal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hkma.home.bank.entity.AuthorityEntity;
import com.hkma.home.bank.entity.BankAccountEntity;
import com.hkma.home.bank.entity.BankEntity;
import com.hkma.home.bank.repository.AuthorityRepository;
import com.hkma.home.bank.repository.BankAccountRepository;

@Controller("MobileStockTableDraw")
@RequestMapping("/m/stock/table/draw")
public class TableDrawController {
	@Autowired
	private AuthorityRepository authorityRepository;
	
	@Autowired
	private BankAccountRepository bankAccountRepository;
	
	@GetMapping("/view")
	public String view(
			@RequestParam(required=false, value = "accountUserId") String accountUserId,
			@RequestParam(required=false, value = "accountId") String accountId,
			Model model){
		Connection con = null;
        CallableStatement cStmt = null;
        ResultSet rs = null;
        
        try {
        	con = DriverManager.getConnection("jdbc:mysql://localhost:3306/home?serverTimezone=UTC","user01","1234");
            
            cStmt = con.prepareCall("call sp_stock_draw(?,?);");
            cStmt.setString(1, accountUserId);
	        cStmt.setString(2, accountId);
            rs = cStmt.executeQuery();
            
            ResultSetMetaData metadata = rs.getMetaData();
            int columnNumber = metadata.getColumnCount();

            List<HashMap<String,Object>> list = new ArrayList<>();
            while (rs.next()) {
                HashMap<String,Object> map = new HashMap<>(columnNumber);
                for(int i = 1; i <= columnNumber; i++) {
                	map.put(metadata.getColumnName(i), rs.getObject(i));
                }
                
                list.add(map);
            }
            
            model.addAttribute("list", list);
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
		
		return "mobile/stock/table/draw/view";
	}
	
	@GetMapping("/search")
	public String search(
			Principal principal,
			Model model){
		String userId, accountUserId, accountId;
		List<Map<String,Object>> accountList = new ArrayList<>();
		
		if (principal != null){
			userId = principal.getName();
			accountUserId = "";
			accountId = "";
		}else {
			userId = "mia";
			accountUserId = "mia";
			accountId = "001";
		}
		
		List<AuthorityEntity> authorityList = authorityRepository.findByUserId(userId);
		authorityList.forEach(authority ->{
			String authorityAccountUserId = authority.getAccountUserId();
			String authorityAccountId = authority.getAccountId();
			
			Optional<BankAccountEntity> bankAccountOptional = bankAccountRepository.findByUserIdAndId(authorityAccountUserId, authorityAccountId);
			
			if(bankAccountOptional.isPresent()) {
				BankAccountEntity bankAccountEntity = bankAccountOptional.get();
				
				String memo = bankAccountEntity.getMemo();
				String isSecurities = bankAccountEntity.getIsSecurities();
				
				if(bankAccountEntity.getBank() != null) {
					BankEntity bank = bankAccountEntity.getBank();
					memo = bank.getName() + " - " + memo;
				};
				
				if(isSecurities != null && isSecurities.equals("1")) {
					Map<String,Object> map = new HashMap<>();
					
					map.put("accountUserId", authorityAccountUserId);
					map.put("accountId", authorityAccountId);
					map.put("memo", memo);
					
					accountList.add(map);
				}
			}
		});
		
		model.addAttribute("accountUserId", accountUserId);
		model.addAttribute("accountId", accountId);
		model.addAttribute("accountList", accountList);
		
		return "mobile/stock/table/draw/search";
	}
}
