package com.fujitsu.manager.reportManager.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.map.ListOrderedMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.IService.IQuartzManagerService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.dao.mysql.ReportManagerMapper;
import com.fujitsu.job.PerformanceDataConvergeJob;
import com.fujitsu.manager.reportManager.service.ReportManagerService;

@Service()
@Transactional(rollbackFor = Exception.class)
public class ReportManagerServiceImpl extends ReportManagerService {
	
	@Resource
	private ReportManagerMapper reportManagerMapper;

	@Resource
	public IQuartzManagerService quartzManagerService;
	@Override
	@SuppressWarnings("rawtypes")
	public List<Map> getResourceChartByStationAndNeModel(Map<String, Object> map) throws CommonException {
		// 按网元型号查询某些局站的资源统计信息
		List<Map> resourceList = reportManagerMapper.getResourceChartByStationAndNeModel(map);
		return resourceList;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public List<Map> getAllNeModelsByStation(Map<String, Object> map) throws CommonException {
		List<Map> neModelList = reportManagerMapper.getAllNeModelsByStation(map);
		return neModelList;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public List<Map> getResourceChartByNeModelAndStation(Map<String, Object> map) throws CommonException {
		/// 按局站查询某网元型号的资源统计信息
		List<Map> resourceList = reportManagerMapper.getResourceChartByNeModelAndStation(map);
		return resourceList;
	}

	@Override
	public Map<String, Object> getResourceDetailByStation(Map<String, Object> map,int start,int limit) throws CommonException {
		Map<String, Object> valueMap = new HashMap<String, Object>();
		// 按局站查询资源总数
		Map<String, Object> resourceCount = reportManagerMapper.getResourceCountByStation(map);
		// 分页按局站查询资源详细信息
		List<Map<String, Object>> resourceList = reportManagerMapper.getResourceDetailByStation(map,start,limit);
		// 封装J前台表格数据->总记录数
		valueMap.put("total", Integer.parseInt(resourceCount.get("total").toString()));
		// 封装前台表格数据->列表信息
		valueMap.put("rows", resourceList);
		return valueMap;
	}
	@Override
	public Map<String, Object> getEmsGroupInfo_Resource(Map<String, Object> map,int start,int limit) {
		Map<String, Object> valueMap = new HashMap<String, Object>();
		// 根据年份查询告警总数
		Map<String, Object> emsGroupCount = reportManagerMapper.getEmsGroupTotal_Resource(map);
		// 分页查询告警信息
		List<Map<String, Object>> emsGroupList = reportManagerMapper.getEmsGroupInfo_Resource(map,start,limit);
		// 封装J前台表格数据->总记录数
		valueMap.put("total", Integer.parseInt(emsGroupCount.get("total").toString()));
		// 封装前台表格数据->列表信息
		valueMap.put("rows", emsGroupList);
		return valueMap;
	}
	
	@Override
	public List<Map<String, Object>> getEmsGroupFusionChart_Resource(Map<String, Object> map) {
		List<Map<String, Object>> valueMap  = reportManagerMapper.getEmsGroupFusionChart_Resource(map);
		return valueMap;
	}
	/** 性能报表--按网管分组统计 */
	@Override
	public Map<String, Object> getEmsGroupInfo_Performance(Map<String, Object> map,int start,int limit) {
		Map<String, Object> valueMap = new HashMap<String, Object>();
		List<Map<String, Object>> emsGroupList = reportManagerMapper.getEmsGroupInfo_Performance(map,start,limit);
		if(emsGroupList==null){
			emsGroupList=new ArrayList<Map<String, Object>>();
		}
		valueMap.put("rows", emsGroupList);
		return valueMap;
	}
	/** 性能报表--按网管分组统计 */
	@Override
	public List<Map<String, Object>> getEmsGroupFusionChart_Performance_1(Map<String, Object> map) {
		List<Map<String, Object>> valueMap  = reportManagerMapper.getEmsGroupFusionChart_Performance_1(map);
		return valueMap;
	}
	/** 性能报表--按网管分组统计 */
	@Override
	public List<ListOrderedMap> getEmsGroupFusionChart_Performance_2(Map<String, Object> map) {
		List<ListOrderedMap> valueMap  = reportManagerMapper.getEmsGroupFusionChart_Performance_2(map);
		return valueMap;
	}
	
	@Override
	public List<Map<String, Object>> getEmsGroupFusionChart_Performance_LV(Map<String, Object> map) {
		List<Map<String, Object>> valueMap  = reportManagerMapper.getEmsGroupFusionChart_Performance_LV(map);
		return valueMap;
	}
	
	@Override
	public List<Map<String, Object>> getEmsGroupFusionChart_Performance_3(Map<String, Object> map) {
		List<Map<String, Object>> valueMap  = reportManagerMapper.getEmsGroupFusionChart_Performance_3(map);
		return valueMap;
	}
	
	@Override
	public Map<String, Object> getEmsInfo_Resource(Map<String, Object> map,int start,int limit) {
		Map<String, Object> valueMap = new HashMap<String, Object>();
		// 根据年份查询告警总数
		Map<String, Object> emsGroupCount = reportManagerMapper.getEmsTotal_Resource(map);
		// 分页查询告警信息
		List<Map<String, Object>> emsGroupList = reportManagerMapper.getEmsInfo_Resource(map,start,limit);
		// 封装J前台表格数据->总记录数
		valueMap.put("total", Integer.parseInt(emsGroupCount.get("total").toString()));
		// 封装前台表格数据->列表信息
		valueMap.put("rows", emsGroupList);
		return valueMap;
	}
	
	@Override
	public List<Map<String, Object>> getEmsFusionChart_Resource(Map<String, Object> map) {
		List<Map<String, Object>> valueMap  = reportManagerMapper.getEmsFusionChart_Resource(map);
		return valueMap;
	}
	/** 性能报表--按网管统计 */
	@Override
	public Map<String, Object> getEmsInfo_Performance(Map<String, String> map,int start,int limit) {
		Map<String, Object> valueMap = new HashMap<String, Object>();
		// 根据年份查询告警总数
		Map<String, Object> emsCount = reportManagerMapper.getEmsTotal_Performance(map);
		// 分页查询告警信息
		List<Map<String, Object>> emsList = reportManagerMapper.getEmsInfo_Performance(map,start,limit);
		// 封装J前台表格数据->总记录数
		valueMap.put("total", Integer.parseInt(emsCount.get("total").toString()));
		// 封装前台表格数据->列表信息
		valueMap.put("rows", emsList);
		return valueMap;
	}
	/** 性能报表--按网管统计 */
	@Override
	public List<Map<String, Object>> getEmsFusionChart_Performance_1(Map<String, Object> map) {
		List<Map<String, Object>> valueMap  = reportManagerMapper.getEmsFusionChart_Performance_1(map);
		return valueMap;
	}
	/** 性能报表--按网管统计 */
	@Override
	public List<Map<String,Object>> getEmsFusionChart_Performance_2(Map<String, Object> map) {
		List<Map<String,Object>> valueMap  = reportManagerMapper.getEmsFusionChart_Performance_2(map);
		return valueMap;
	}
	
	@Override
	public Map<String, Object> getEmsGroupInfo_Circuit(Map<String, Object> map,int start,int limit) {
		Map<String, Object> valueMap = new HashMap<String, Object>();
		// 根据年份查询告警总数
		Map<String, Object> emsGroupCount = reportManagerMapper.getEmsGroupTotal_Circuit(map);
		// 分页查询告警信息
		List<Map<String, Object>> emsGroupList = reportManagerMapper.getEmsGroupInfo_Circuit(map,start,limit);
		// 封装J前台表格数据->总记录数
		valueMap.put("total", Integer.parseInt(emsGroupCount.get("total").toString()));
		// 封装前台表格数据->列表信息
		valueMap.put("rows", emsGroupList);
		return valueMap;
	}
	
	@Override
	public List<Map<String, Object>> getEmsGroupFusionChart_Circuit_1(Map<String, Object> map) {
		List<Map<String, Object>> valueMap  = reportManagerMapper.getEmsGroupFusionChart_Circuit_1(map);
		return valueMap;
	}
	
	@Override
	public List<Map<String, Object>> getEmsGroupFusionChart_Circuit_2(Map<String, Object> map) {
		List<Map<String, Object>> valueMap  = reportManagerMapper.getEmsGroupFusionChart_Circuit_2(map);
		return valueMap;
	}
	
	@Override
	public List<Map<String, Object>> getEmsFusionChart_Alarm_test(Map<String, Object> map) {
		List<Map<String, Object>> valueMap  = reportManagerMapper.getEmsFusionChart_Alarm_test(map);
		return valueMap;
	}

	@Override
	public Map callPerformaceSP(Map map) throws CommonException {
		reportManagerMapper.callPerformaceSP(map);
		return null;
	}

	@Override
	public Map callPerformanceSPJob(Map param) throws CommonException {
		int jobType = CommonDefine.QUARTZ.JOB_PERFORMANCE_SP;
		int jobID=1;
		String jobTime=(String)param.get("jobTime");
		if(quartzManagerService.IsJobExist(jobType,jobID)){
			quartzManagerService.ctrlJob(jobType,jobID,CommonDefine.QUARTZ.JOB_PAUSE);
			quartzManagerService.ctrlJob(jobType, jobID, CommonDefine.QUARTZ.JOB_DELETE);
		}
		quartzManagerService.addJob(jobType,jobID,PerformanceDataConvergeJob.class,jobTime,param);
		return null;
	}

	@Override
	public Map<String, Object> getPMMonthDataByEmsGroup(Map<String, Object> map) {
		Map<String, Object> valueMap = new HashMap<String, Object>();
		List<Map<String, Object>> emsGroupList = reportManagerMapper.getPMMonthDataByEmsGroup(map);
		if(emsGroupList==null){
			emsGroupList=new ArrayList<Map<String, Object>>();
		}
		valueMap.put("rows", emsGroupList);
		return valueMap;
	}

	@Override
	public Map<String, Object> getPMDayDataByEmsGroup(Map<String, Object> map) {
		Map<String, Object> valueMap = new HashMap<String, Object>();
		List<Map<String, Object>> emsGroupList = reportManagerMapper.getPMDayDataByEmsGroup(map);
		if(emsGroupList==null){
			emsGroupList=new ArrayList<Map<String, Object>>();
		}
		valueMap.put("rows", emsGroupList);
		return valueMap;
	}

	@Override
	public Map<String, Object> getPMDayDataByQueryDayAndGroup(Map<String, Object> map) {
		Map<String, Object> valueMap = new HashMap<String, Object>();
		List<Map<String, Object>> emsGroupList = reportManagerMapper.getPMDayDataByQueryDayAndGroup(map);
		if(emsGroupList==null){
			emsGroupList=new ArrayList<Map<String, Object>>();
		}
		valueMap.put("rows", emsGroupList);
		return valueMap;
	}

	@Override
	public void insertAlarmDataFromMonodb(List<Map> datas) {
		
		if(datas!=null && datas.size()>0){
			String firstTime=datas.get(0).get("FIRST_TIME").toString();
			//先删除当前数据
			reportManagerMapper.deleteAlarmDataByDayForever(firstTime);
			List<Map> list=new ArrayList<Map>();
			for (int i=0;i<datas.size();i++) {
				list.add(datas.get(i));
				if(list.size()==1000){
					reportManagerMapper.insertAlarmDataFromMonodb(list);
					list.clear();
				}
			}
			if(list.size()>0){
				reportManagerMapper.insertAlarmDataFromMonodb(list);
			}
			
			reportManagerMapper.insertAlarmAnaData(firstTime);
			reportManagerMapper.deleteAlarmDataByDayForever(firstTime);
		}
	}

	@Override
	public Map<String, Object> getReportAlarmByEms(Map<String, Object> paramMap) {
		Map<String, Object> valueMap = new HashMap<String, Object>();
		List<Map<String, Object>> emsGroupList = reportManagerMapper.getReportAlarmByEms(paramMap);
		if(emsGroupList==null){
			emsGroupList=new ArrayList<Map<String, Object>>();
		}
		valueMap.put("rows", emsGroupList);
		return valueMap;
	}

	@Override
	public Map<String, Object> getAlarmMonthDataByEms(Map<String, Object> paramMap) {
		Map<String, Object> valueMap = new HashMap<String, Object>();
		List<Map<String, Object>> emsGroupList = reportManagerMapper.getAlarmMonthDataByEms(paramMap);
		if(emsGroupList==null){
			emsGroupList=new ArrayList<Map<String, Object>>();
		}
		valueMap.put("rows", emsGroupList);
		return valueMap;
	}

	@Override
	public Map<String, Object> getAlarmDayDataByEms(Map<String, Object> paramMap) {
		Map<String, Object> valueMap = new HashMap<String, Object>();
		List<Map<String, Object>> emsGroupList = reportManagerMapper.getAlarmDayDataByEms(paramMap);
		if(emsGroupList==null){
			emsGroupList=new ArrayList<Map<String, Object>>();
		}
		valueMap.put("rows", emsGroupList);
		return valueMap;
	}

	@Override
	public Map<String, Object> getAlarmDayDataByQueryDayAndEms(Map<String, Object> paramMap) {
		Map<String, Object> valueMap = new HashMap<String, Object>();
		List<Map<String, Object>> emsGroupList = reportManagerMapper.getAlarmDayDataByQueryDayAndEms(paramMap);
		if(emsGroupList==null){
			emsGroupList=new ArrayList<Map<String, Object>>();
		}
		valueMap.put("rows", emsGroupList);
		return valueMap;
	}

//533@@@@@@@@@@@@@@@@@@@@
	@Override
	public Map<String, Object> getPMDataPerMonthByEmsId(
			Map<String, String> condMap) {
		Map<String, Object> valueMap = new HashMap<String, Object>();
		List<Map<String, Object>> dataList = reportManagerMapper.getPMDataPerMonthByEmsId(condMap);
		valueMap.put("rows", dataList);
		valueMap.put("total", dataList.size());
		return valueMap;
	}

	@Override
	public Map<String, Object> getPMDataPerDayEms(Map<String, String> condMap) {
		Map<String, Object> valueMap = new HashMap<String, Object>();
		List<Map<String, Object>> dataList = reportManagerMapper.getPMDataPerDayEms(condMap);
		valueMap.put("rows", dataList);
		valueMap.put("total", dataList.size());
		return valueMap;
	}

	@Override
	public Map<String, Object> getPMDataPerDayByEmsId(
			Map<String, String> condMap) {
		Map<String, Object> valueMap = new HashMap<String, Object>();
		List<Map<String, Object>> dataList = reportManagerMapper.getPMDataPerDayByEmsId(condMap);
		valueMap.put("rows", dataList);
		valueMap.put("total", dataList.size());
		return valueMap;
	}

	@Override
	public String getEmsName(Map<String, String> condMap) {
		String emsName = reportManagerMapper.getEmsName(condMap);
		return emsName;
	}

}
