package com.hkma.home.asset.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Data {
	public static double getBlalance(String userId, String date) {
		double amount = 0;
		Connection con = null;
        CallableStatement cStmt = null;
        ResultSet rs = null;
        
        try {
        	con = DriverManager.getConnection("jdbc:mysql://localhost:3306/home?serverTimezone=UTC","user01","1234");
	        
	        cStmt = con.prepareCall("call home.sp_asset_getBalance(?,?);");
            cStmt.setString(1, userId);
            cStmt.setString(2, date);
            rs = cStmt.executeQuery();
            
            while (rs.next()) {
            	amount = rs.getDouble("amount");
            }
        }catch(SQLException ex) {
        	System.out.println(ex);
        }finally {
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
        
        return amount;
	}

	public static ArrayList<String> getLineId(String userId) {
		ArrayList<String> lineId = new ArrayList<>();
		
		Connection con = null;
        CallableStatement cStmt = null;
        ResultSet rs = null;
        PreparedStatement pstmt;
		
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/home?serverTimezone=UTC","user01","1234");
	        
	        pstmt = con.prepareStatement("SELECT lineId FROM home.bank_accountnotify WHERE userId = ?");
	        pstmt.setString(1, userId);
	        rs = pstmt.executeQuery();
	        
	        while(rs.next()){
	        	lineId.add(rs.getString(1));
	        }
	        
		}catch(SQLException ex){
			System.out.println(ex);
		}finally {
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
		
		return lineId;
	}
}