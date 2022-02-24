package com.hkma.home.system.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class TimerListener implements ServletContextListener {
	com.hkma.home.asset.schedule.PushBalanceSchedule pushBalanceSchedule;

	@Override
    public void contextInitialized(ServletContextEvent sce) {
    	pushBalanceSchedule = new com.hkma.home.asset.schedule.PushBalanceSchedule();
    }

	@Override
    public void contextDestroyed(ServletContextEvent sce) {
    	pushBalanceSchedule.close();
    }
}