package com.hkma.home.system.service;

import java.math.BigDecimal;

public class Number {
	public static String chineseConvert(double money){
        String integerStr, decimalStr;
        BigDecimal moneyBg, integerBg, decimalBg;
        
        moneyBg = java.math.BigDecimal.valueOf(money);
        integerBg = java.math.BigDecimal.valueOf((int)money);
        integerStr = integerBg + "";
        decimalBg = moneyBg.subtract(integerBg);
        decimalStr = decimalBg + "";
        String splitStr, str = "";
        
        for (int i = 0; i < integerStr.length(); i++){
            splitStr = integerStr.substring(i, i + 1);
            
            switch (splitStr){
                case "0":
                    break;
                case "1":
                    if ( ( (integerStr.length() - i) == 2 && integerStr.length() == 2) || 
                            ( (integerStr.length() - i) == 6 && integerStr.length() == 6) ){
                    }else{
                        str = str + "一";
                    }
                    
                    break;
                case "2":
                    if ( ( (integerStr.length() - i) == 1 && integerStr.length() == 1) ||
                            ( (integerStr.length() - i) == 3) ||
                            ( (integerStr.length() - i) == 4) ||
                            ( (integerStr.length() - i) == 5 && integerStr.length() == 5) ||
                            ( (integerStr.length() - i) == 7) ||
                            ( (integerStr.length() - i) == 8) ||
                            ( (integerStr.length() - i) == 9 && integerStr.length() == 9)){
                        str = str + "兩";
                    }else{
                        str = str + "二";
                    }
                    
                    break;
                case "3":
                    str = str + "三";
                    break;
                case "4":
                    str = str + "四";
                    break;
                case "5":
                    str = str + "五";
                    break;
                case "6":
                    str = str + "六";
                    break;
                case "7":
                    str = str + "七";
                    break;
                case "8":
                    str = str + "八";
                    break;
                case "9":
                    str = str + "九";
                    break;
            }
            
            switch (integerStr.length() - i){
                case 2:
                    if (!splitStr.equals("0")){
                        str = str + "十";
                    }
                    break;
                case 3:
                    if (!splitStr.equals("0")){
                        str = str + "百";
                    }
                    break;
                case 4:
                    if (!splitStr.equals("0")){
                        str = str + "千";
                    }
                    break;
                case 5:
                    if ( (integerStr.length() >= 8 && integerStr.substring(integerStr.length() - 8, integerStr.length() - 8 + 1).equals("0") ) &&
                            (integerStr.length() >= 7 && integerStr.substring(integerStr.length() - 7, integerStr.length() - 7 + 1).equals("0") ) &&
                            (integerStr.length() >= 6 && integerStr.substring(integerStr.length() - 6, integerStr.length() - 6 + 1).equals("0") ) &&
                            (integerStr.length() >= 5 && integerStr.substring(integerStr.length() - 5, integerStr.length() - 5 + 1).equals("0") ) ){
                    }else{
                        str = str + "萬";
                    }
                    
                    break;
                case 6:
                    if (!splitStr.equals("0")){
                        str = str + "十";
                    }
                    break;
                case 7:
                    if (!splitStr.equals("0")){
                        str = str + "百";
                    }
                    break;
                case 8:
                    if (!splitStr.equals("0")){
                        str = str + "千";
                    }
                    break;
                case 9:
                    str = str + "億";
                    break;
            }
        }
        
        return str;
    }
}
