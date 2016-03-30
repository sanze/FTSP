package com.fujitsu.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fujitsu.IService.IAlarmManagementService;


public class DelaySync implements Runnable {
	
	private IAlarmManagementService alarmManagementService;
	private int i;
	public DelaySync(int i, IAlarmManagementService alarmManagementService) {
		super();
		this.i=i;
		this.alarmManagementService=alarmManagementService;
	}
	@Override
	public void run() {
		// TODO 
		try {
			Thread.sleep(i*1000*60);
			//查找
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			list =alarmManagementService.getCanDelaySync(i);
			for (Map<String,Object> map : list) {
				alarmManagementService.alarmAutoSynch(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
