package com.hkma.home.asset.schedule;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PushBalanceSchedule {
	ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1); 
    
    public PushBalanceSchedule() {
        long delay, period;
        
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 00);
        
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        delay = calendar.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
        period = 24 * 60 * 60 * 1000;
    
        exec.scheduleAtFixedRate(new PushBalanceRunnable(), delay, period, TimeUnit.MILLISECONDS);
    }
    
    private class PushBalanceRunnable implements Runnable{
        @Override
        public void run() {
            int dayOfWeek;
            Calendar calendar = Calendar.getInstance();
            dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            
            if (dayOfWeek >= 1 && dayOfWeek <= 5){
            	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            	String date = dateFormat.format(calendar.getTime());
            	
            	com.hkma.home.asset.service.Notify.pushBalance("mia", date);
            }
        }
    }
    
    public void close(){
        exec.shutdown();
    }
}
