package com.fujitsu.job;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.fujitsu.IService.IDataCollectServiceProxy;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.dao.mysql.NxReportManagerMapper;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.util.SpringContextUtil;

public class PtnReportDataSourceJob implements Job {

	private NxReportManagerMapper nxReportManagerMapper;

	public PtnReportDataSourceJob() {
		nxReportManagerMapper = (NxReportManagerMapper) SpringContextUtil
				.getBean("nxReportManagerMapper");
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		//获取所有ptn系统信息
		List<Map> ptnSysInfoList = nxReportManagerMapper.selectAllPtnSysInfo();
		//分组
		Map<String,List<Integer>> map = new HashMap<String,List<Integer>>();
		
		for(Map ptnSysInfo:ptnSysInfoList){
			String ptnSysKey = ptnSysInfo.get("BASE_EMS_CONNECTION_ID")+"_"+ptnSysInfo.get("T_RESOURCE_PTN_SYS_ID")+"_"+ptnSysInfo.get("SYS_NAME");
			if(map.containsKey(ptnSysKey)){
				List<Integer> neIdList = map.get(ptnSysKey);
				if(!neIdList.contains(Integer.valueOf(ptnSysInfo.get("BASE_NE_ID").toString()))){
					neIdList.add(Integer.valueOf(ptnSysInfo.get("BASE_NE_ID").toString()));
				}
			}else{
				List<Integer> neIdList = new ArrayList<Integer>();
				neIdList.add(Integer.valueOf(ptnSysInfo.get("BASE_NE_ID").toString()));
				map.put(ptnSysKey, neIdList);
			}
		}
		
		SimpleDateFormat corbaFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		//采集
		for (String ptnSysKey : map.keySet()) {
			String[] temp = ptnSysKey.split("_");
			IDataCollectServiceProxy dataCollectService;
			try {
				dataCollectService = SpringContextUtil
						.getDataCollectServiceProxy(Integer.valueOf(temp[0]));
				//采集历史性能
				dataCollectService.getPtnReportData(temp[1], temp[2],
						map.get(ptnSysKey), corbaFormat.format(new Date()),
						CommonDefine.COLLECT_LEVEL_2);
			} catch (Exception e) {
				ExceptionHandler.handleException(e);
			}

		}
	}
}
