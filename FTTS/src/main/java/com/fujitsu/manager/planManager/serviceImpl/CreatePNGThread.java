package com.fujitsu.manager.planManager.serviceImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.concurrent.Callable;

import com.fujitsu.util.CreateLineChart;

/**
 * @Description：画图
 * @author cao senrong
 * @date 2015-1-12
 * @version V1.0
 */
public class CreatePNGThread implements Callable<String>{

	private static String TEMP_FILE_PATH = System.getProperty("java.io.tmpdir");
	private static String TEMP_FILE_NAME = "resultTemp.xml";
	/**
	 * @param testStartTime
	 * @param testTime
	 */
	public CreatePNGThread() {
		super();
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public String call() throws Exception {
		
		CreateLineChart clc = new CreateLineChart();
		clc.drowOTDRChart(TEMP_FILE_PATH+"\\",TEMP_FILE_NAME,TEMP_FILE_PATH+"\\"+TEMP_FILE_NAME);
		
		return "";
	}
}
