package com.hkma.home.mobile.stock.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
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

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.hkma.home.bank.entity.AuthorityEntity;
import com.hkma.home.bank.entity.BankAccountEntity;
import com.hkma.home.bank.entity.BankEntity;
import com.hkma.home.bank.repository.AuthorityRepository;
import com.hkma.home.bank.repository.BankAccountRepository;

@RestController("MobileStockTableIncome")
@RequestMapping("/m/stock/table/income")
public class TableIncomeController {
	@Autowired
	private AuthorityRepository authorityRepository;
	
	@Autowired
	private BankAccountRepository bankAccountRepository;
	
	@GetMapping("/view")
	public ModelAndView view(
			@RequestParam(required=false, value = "accountUserId") String accountUserId,
			@RequestParam(required=false, value = "accountId") String accountId,
			Model model){
		ModelAndView mav = new ModelAndView("mobile/stock/table/income/view");
		
		Connection con = null;
        CallableStatement cStmt = null;
        ResultSet rs = null;
        
        try {
        	con = DriverManager.getConnection("jdbc:mysql://localhost:3306/home?serverTimezone=UTC","user01","1234");
            
            cStmt = con.prepareCall("call home.sp_stock_income(?,?);");
            cStmt.setString(1, accountUserId);
	        cStmt.setString(2, accountId);
            rs = cStmt.executeQuery();
            
            ResultSetMetaData metadata = rs.getMetaData();
            cStmt.setString(1, accountUserId);
	        cStmt.setString(2, accountId);
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
		
		return mav;
	}
	
	@GetMapping("/excel")
	public String excel(HttpServletResponse response) throws Exception{
		HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("統計表");
        createTitle(workbook,sheet);
        
        List<BankAccountEntity> rows = bankAccountRepository.findAll();

        //設定日期格式
        HSSFCellStyle style = workbook.createCellStyle();
        style.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));

        //新增資料行，並且設定單元格資料
        int rowNum=1;
        for(BankAccountEntity user:rows){
            HSSFRow row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(user.getUserId());
            row.createCell(1).setCellValue(user.getId());
            row.createCell(2).setCellValue(user.getBankId());
            rowNum++;
        }

        String filePath = "E:/";
        String fileName = "excel.xls";

        //生成excel檔案
        buildExcelFile(filePath, fileName, workbook);

        //瀏覽器下載excel
        buildExcelDocument(filePath, fileName,workbook,response);

        return "download excel";
	}
	
	private void createTitle(HSSFWorkbook workbook, HSSFSheet sheet){
        HSSFRow row = sheet.createRow(0);
        //設定列寬，setColumnWidth的第二個引數要乘以256，這個引數的單位是1/256個字元寬度
        sheet.setColumnWidth(1,12*256);
        sheet.setColumnWidth(3,17*256);

        //設定為居中加粗
        HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setFont(font);

        HSSFCell cell;
        cell = row.createCell(0);
        cell.setCellValue("ID");
        cell.setCellStyle(style);


        cell = row.createCell(1);
        cell.setCellValue("顯示名");
        cell.setCellStyle(style);

        cell = row.createCell(2);
        cell.setCellValue("使用者名稱");
        cell.setCellStyle(style);
    }
	
	protected void buildExcelFile(String filePath, String filename,HSSFWorkbook workbook) throws Exception{
        FileOutputStream fos = new FileOutputStream(filePath + filename);
        workbook.write(fos);
        fos.flush();
        fos.close();
    }
	
	protected void buildExcelDocument(String filePath, String filename,HSSFWorkbook workbook,HttpServletResponse response) throws Exception{
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode(filename, "utf-8"));
        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
        
        new File(filePath + filename).delete();
    }
	
	@GetMapping("/search")
	public ModelAndView search(
			Principal principal,
			Model model){
		ModelAndView mav = new ModelAndView("mobile/stock/table/income/search");
		
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
		
		//證券帳戶
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
		
		return mav;
	}
}
