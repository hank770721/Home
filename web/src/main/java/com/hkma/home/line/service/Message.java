package com.hkma.home.line.service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

public class Message {
	private static final String CHANNEL_ACCESS_TOKEN = "";
    static HttpURLConnection connection;
    static java.net.URL url;
    
    static void connectionInit() throws IOException{
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + CHANNEL_ACCESS_TOKEN);
    }
    
    static void connectionConnect(String message) throws IOException{
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.connect();
        OutputStream os = connection.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
        osw.write(message);
        osw.flush();
        connection.getResponseCode();
    }
    
    public static void push(String id, String messages) throws IOException{
    	try {
    		JSONArray jMessages = new JSONArray(messages);
            JSONObject obj = new JSONObject();
            obj.put("to", id);
            obj.put("messages", jMessages);
            
            url = new java.net.URL("https://api.line.me/v2/bot/message/push");
            
            connectionInit();
            connectionConnect(obj.toString());
    	} catch(Exception ex){
    		System.out.println(ex);
    	}
    }
}
