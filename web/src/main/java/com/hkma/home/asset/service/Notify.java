package com.hkma.home.asset.service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Notify {
	public static void pushBalance(String userId, String date) {
		final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        final DecimalFormat decimalFormat = new DecimalFormat("###,###");
		
		try {
			double amount, amountBefore, blalance, amountLastMonth, blalanceLastMonth;
			String messages;
			ArrayList<String> lineIdList = new ArrayList<>();
			Calendar calendar = Calendar.getInstance();
			
			amount = com.hkma.home.asset.service.Data.getBlalance(userId, date);
			
			//yesterday
			{
				calendar.setTime(dateFormat.parse(date));
				calendar.add(Calendar.DAY_OF_MONTH, -1);
				
				amountBefore = com.hkma.home.asset.service.Data.getBlalance(userId, dateFormat.format(calendar.getTime()));
				
				blalance = amount - amountBefore;
			}
			
			//last month
			{
				calendar.setTime(dateFormat.parse(date.substring(0,6) + "01"));
				calendar.add(Calendar.DAY_OF_MONTH, -1);
				
				amountLastMonth = com.hkma.home.asset.service.Data.getBlalance(userId, dateFormat.format(calendar.getTime()));
				
				blalanceLastMonth = amount - amountLastMonth;
			}
			
			messages =	"["
					+		"{"
					+			"\"type\":\"flex\","
					+			"\"altText\":\"投資金額通知\","
					+			"\"contents\":{"
					+				"\"type\": \"bubble\","
					+				"\"hero\": {"
					+ 					"\"type\": \"image\","
					+ 					"\"url\": \"https://scdn.line-apps.com/n/channel_devcenter/img/fx/01_1_cafe.png\","
					+ 					"\"size\": \"full\","
					+ 					"\"aspectRatio\": \"20:13\","
					+ 					"\"aspectMode\": \"cover\","
					+ 					"\"action\": {"
					+ 						"\"type\": \"uri\","
					+ 						"\"uri\": \"http://linecorp.com/\""
					+ 					"}"
					+ 				"},"
					+ 				"\"body\": {"
					+ 					"\"type\": \"box\","
					+ 					"\"layout\": \"vertical\","
					+ 					"\"contents\": ["
					+ 						"{"
					+ 							"\"type\": \"text\","
					+ 							"\"text\": \"投資金額通知\","
					+ 							"\"weight\": \"bold\","
					+ 							"\"size\": \"xl\","
					+ 							"\"color\": \"#007979\""
					+ 						"},"
					+ 						"{"
					+ 							"\"type\": \"text\","
					+ 							"\"text\": \"新臺幣" + decimalFormat.format(amount) + "元\","
					+ 							"\"size\": \"xl\","
					+ 							"\"weight\": \"bold\""
					+ 						"},"
					+ 						"{"
					+ 							"\"type\": \"box\","
					+ 							"\"layout\": \"vertical\","
					+ 							"\"contents\": ["
					+ 								"{"
					+ 									"\"type\": \"text\","
					+ 									"\"text\": \"昨日結餘：" + decimalFormat.format(amountBefore) + "\","
					+ 									"\"align\": \"start\","
					+ 									"\"size\": \"md\""
					+ 								"},";
			
			if (blalance < 0) {
				messages = messages
						+ 								"{"
						+ 									"\"type\": \"text\","
						+ 									"\"text\": \"虧損：" + decimalFormat.format(blalance) + "\","
						+ 									"\"size\": \"md\","
						+ 									"\"color\": \"#03FF00\""
						+ 								"},";
			}else {
				messages = messages
						+ 								"{"
						+ 									"\"type\": \"text\","
						+ 									"\"text\": \"盈利：" + decimalFormat.format(blalance) + "\","
						+ 									"\"size\": \"md\","
						+ 									"\"color\": \"#FA0000\""
						+ 								"},";
			}
			
			messages = messages
					+ 								"{"
					+ 									"\"type\": \"text\","
					+ 									"\"text\": \"上月結餘：" + decimalFormat.format(amountLastMonth) + "\","
					+ 									"\"align\": \"start\","
					+ 									"\"size\": \"md\""
					+ 								"},";
			
			if (blalanceLastMonth < 0) {
				messages = messages
						+ 								"{"
						+ 									"\"type\": \"text\","
						+ 									"\"text\": \"虧損：" + decimalFormat.format(blalanceLastMonth) + "\","
						+ 									"\"size\": \"md\","
						+ 									"\"color\": \"#03FF00\""
						+ 								"}";
			}else {
				messages = messages
						+ 								"{"
						+ 									"\"type\": \"text\","
						+ 									"\"text\": \"盈利：" + decimalFormat.format(blalanceLastMonth) + "\","
						+ 									"\"size\": \"md\","
						+ 									"\"color\": \"#FA0000\""
						+ 								"}";
			}
			
			messages = messages
					+ 							"]"
					+ 						"}"
					+ 					"]"
					+ 				"},"
					+ 				"\"footer\": {"
					+ 					"\"type\": \"box\","
					+ 					"\"layout\": \"vertical\","
					+ 					"\"spacing\": \"sm\","
					+ 					"\"contents\": ["
					+ 						"{"
					+ 							"\"type\": \"button\","
					+ 							"\"style\": \"link\","
					+ 							"\"height\": \"sm\","
					+ 							"\"action\": {"
					+ 								"\"type\": \"uri\","
					+ 								"\"label\": \"投資儀錶板\","
					+ 								"\"uri\": \"http://13.75.40.140:8080/home/m/asset/dashboard/investment/view?accountUserId=mia&accountId=001\""
					+ 							"},"
					+ 							"\"color\": \"#007979\""
					+ 						"}"
					+ 					"],"
					+ 					"\"flex\": 0"
					+ 				"}"
					+			"}"
					+ 		"}"
					+	"]";
			
			lineIdList = com.hkma.home.asset.service.Data.getLineId(userId);
			
			for(String lineId : lineIdList) {
				com.hkma.home.line.service.Message.push(lineId, messages);
			}
		} catch (ParseException | IOException ex) {
			System.out.println(ex);
		}
	}
}
