package com.fujitsu.manager.equipmentTestManager.serviceImpl.XTWTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.concurrent.Callable;

/**
 * @Description：监测昕天卫测试结果文件
 * @author cao senrong
 * @date 2015-1-12
 * @version V1.0
 */
public class ResultFileListenThread implements Callable<String>{

	private Date testStartTime;
	private String filePath;
	private String fileName;
	
	/**
	 * @param testStartTime
	 * @param testTime
	 */
	public ResultFileListenThread(Date testStartTime, String filePath,String fileName) {
		super();
		this.testStartTime = testStartTime;
		this.filePath = filePath;
		this.fileName = fileName;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public String call() throws Exception {
		String result = "";
		
		long start = testStartTime.getTime();
		
		File file = new File(filePath + "\\" + fileName);
		//监测文件是否写完
		while(!file.exists()){
			Thread.sleep(3 * 1000);
		}
		long fileModefyTime = file.lastModified();
		try{
		if(fileModefyTime>start){
			StringBuilder sb = new StringBuilder();
			String s ="";
			BufferedReader br = new BufferedReader(new FileReader(file));
			while( (s = br.readLine()) != null) {
			sb.append(s + "\n");
			}
			br.close();
			result = sb.toString();
		}
		}finally{
			//删除文件
			if(file.exists()){
				file.delete();
			}
		}
		return result;
	}
}
