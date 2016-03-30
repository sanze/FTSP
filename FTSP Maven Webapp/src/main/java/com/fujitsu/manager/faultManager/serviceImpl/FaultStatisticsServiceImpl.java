package com.fujitsu.manager.faultManager.serviceImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;
import com.fujitsu.common.ListResult;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.dao.mysql.AlarmManagementMapper;
import com.fujitsu.dao.mysql.FaultStatisticsMapper;
import com.fujitsu.manager.faultManager.model.AlarmQueryCondition;
import com.fujitsu.manager.faultManager.model.EquipNameModel;
import com.fujitsu.manager.faultManager.model.FaultAlarmModel;
import com.fujitsu.manager.faultManager.model.FaultInfoModel;
import com.fujitsu.manager.faultManager.model.FaultProcessModel;
import com.fujitsu.manager.faultManager.model.FaultQueryCondition;
import com.fujitsu.manager.faultManager.model.StationQueryCondition;
import com.fujitsu.manager.faultManager.service.FaultStatisticsService;
import com.fujitsu.model.FaultAnalysisModel;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

import fusioncharts.FusionChartDesigner;

@Service
@Transactional(rollbackFor = Exception.class)
public class FaultStatisticsServiceImpl extends FaultStatisticsService{
	@Resource
	private FaultStatisticsMapper faultStatisticsMapper ;
	@Resource
	private AlarmManagementMapper alarmManagementMapper;
	@Resource
	private Mongo mongo;
	@Override
	public ListResult getFaultList(String jsonString,int start, int limit)
			throws CommonException {
		ListResult listResult = new ListResult();
		try {
			FaultQueryCondition con = new FaultQueryCondition(jsonString);
			con.setLimit(limit);
			con.setStart(start);
			listResult = getFaultList(con);
		} catch (CommonException e) {
			throw e;
		}  catch (Exception e) {
			throw new CommonException(e,MessageCodeDefine.COM_EXCPT_INVALID_INPUT);
		}
		return listResult;
	}
	
	@Override
	public ListResult getFaultList(FaultQueryCondition con)
			throws CommonException {
		ListResult listResult = new ListResult();
		try{
			listResult.setRows(faultStatisticsMapper.getFaultList(con));
			listResult.setTotal(con.getCount());
			listResult.setReturnResult(CommonDefine.SUCCESS);
		}catch(Exception e){
			e.printStackTrace();
			throw new CommonException(e,MessageCodeDefine.COM_EXCPT_DB_OP);
		}
		return listResult;
	}

	@Override
	@SuppressWarnings({"rawtypes"})
	public ListResult getFaultReason() throws CommonException {
		ListResult listResult = new ListResult();
		try{
			List<Map> rows = faultStatisticsMapper.getFaultReason();
			listResult.setRows(rows);
			listResult.setTotal(rows.size());
			listResult.setReturnResult(CommonDefine.SUCCESS);
		}catch(Exception e){
			throw new CommonException(e,MessageCodeDefine.COM_EXCPT_DB_OP);
		}
		return listResult;
	}

	@Override
	@SuppressWarnings({"rawtypes"})
	public ListResult getSubFaultReason(int id) throws CommonException {
		ListResult listResult = new ListResult();
		try{
			List<Map> rows = faultStatisticsMapper.getSubFaultReason(id);
			listResult.setRows(rows);
			listResult.setTotal(rows.size());
			listResult.setReturnResult(CommonDefine.SUCCESS);
		}catch(Exception e){
			throw new CommonException(e,MessageCodeDefine.COM_EXCPT_DB_OP);
		}
		return listResult;
	}

	@Override
	public CommonResult deleteFaultRecord(int faultId) throws CommonException {
		CommonResult result = new CommonResult();
		try{
			FaultInfoModel model = new FaultInfoModel();
			model.setFaultId(faultId);
			faultStatisticsMapper.faultDelete(model);
			result.setReturnResult(CommonDefine.SUCCESS);
		}catch(Exception e){
			e.printStackTrace();
			throw new CommonException(e,MessageCodeDefine.COM_EXCPT_DB_OP);
		}
		return result;
	}
	
	@Override
	@SuppressWarnings({"rawtypes"})
	public ListResult getTransformSystem() throws CommonException {
		ListResult listResult = new ListResult();
		try{
			List<Map> rows = faultStatisticsMapper.getTransformSystem();
			listResult.setRows(rows);
			listResult.setTotal(rows.size());
			listResult.setReturnResult(CommonDefine.SUCCESS);
		}catch(Exception e){
			throw new CommonException(e,MessageCodeDefine.COM_EXCPT_DB_OP);
		}
		return listResult;
	}
	

	@Override
	public ListResult getAlarmByFaultId(int id) throws CommonException {
		ListResult listResult = new ListResult();
		try {
			AlarmQueryCondition con = new AlarmQueryCondition();
			con.setFaultId(id);
			listResult = getAlarmByFaultId(con);
		} catch (CommonException e) {
			throw e;
		}  catch (Exception e) {
			throw new CommonException(e,MessageCodeDefine.COM_EXCPT_INVALID_INPUT);
		}
		return listResult;
	}

	@Override
	public ListResult getAlarmByFaultId(AlarmQueryCondition con)
			throws CommonException {
		ListResult listResult = new ListResult();
		try{
			listResult.setRows(faultStatisticsMapper.getAlarmByFaultId(con));
			listResult.setTotal(con.getCount());
			listResult.setReturnResult(CommonDefine.SUCCESS);
		}catch(Exception e){
			e.printStackTrace();
			throw new CommonException(e,MessageCodeDefine.COM_EXCPT_DB_OP);
		}
		return listResult;
	}
	@Override
	public ListResult getStateBySysId(int id) throws CommonException {
		ListResult listResult = new ListResult();
		try {
			StationQueryCondition con = new StationQueryCondition();
			con.setSysId(id);
			listResult = getStateBySysId(con);
		} catch (CommonException e) {
			throw e;
		}  catch (Exception e) {
			throw new CommonException(e,MessageCodeDefine.COM_EXCPT_INVALID_INPUT);
		}
		return listResult;
	}

	@Override
	public ListResult getStateBySysId(StationQueryCondition con)
			throws CommonException {
		ListResult listResult = new ListResult();
		try{
			listResult.setRows(faultStatisticsMapper.getStateBySysId(con));
			listResult.setTotal(con.getCount());
			listResult.setReturnResult(CommonDefine.SUCCESS);
		}catch(Exception e){
			e.printStackTrace();
			throw new CommonException(e,MessageCodeDefine.COM_EXCPT_DB_OP);
		}
		return listResult;
	}
	@Override
	public ListResult save(String jsonString) throws CommonException {
		ListResult listResult = new ListResult();
		try{
			FaultInfoModel con = new FaultInfoModel(jsonString);
			faultStatisticsMapper.save(con);
			listResult.setReturnResult(CommonDefine.SUCCESS);
			listResult.setTotal(con.getFaultId());
		}catch(Exception e){
			e.printStackTrace();
			throw new CommonException(e,MessageCodeDefine.COM_EXCPT_DB_OP);
		}
		return listResult;
	}
	
	@Override
	public CommonResult faultProcess(int faultId, int processType)
			throws CommonException {
		CommonResult result = new CommonResult();
		try{
			FaultProcessModel model = new FaultProcessModel(faultId,processType);
			faultStatisticsMapper.faultProcess(model);
			//如果是故障响应的话
			if(processType == 3){
				List<FaultAnalysisModel> alarmInfo = getAlmsByFaultIdFromHisAlm(faultId);
				if(alarmInfo.size() > 0){
					result.setReturnMessage(alarmInfo.get(0).getClearTime() == null ? "":alarmInfo.get(0).getClearTime());
				}
			}
			result.setReturnResult(CommonDefine.SUCCESS);
		}catch(Exception e){
			e.printStackTrace();
			throw new CommonException(e,MessageCodeDefine.COM_EXCPT_DB_OP);
		}
		return result;
	}
	@Override
	public CommonResult alarmDelete(int faultId ,int alarmId)throws CommonException{
		CommonResult result = new CommonResult();
		try{
			FaultAlarmModel model = new FaultAlarmModel(faultId, alarmId);
			faultStatisticsMapper.alarmDelete(model);
			result.setReturnResult(CommonDefine.SUCCESS);
		}catch(Exception e){
			e.printStackTrace();
			throw new CommonException(e,MessageCodeDefine.COM_EXCPT_DB_OP);
		}
		return result;
		
	}
	@Override
	public CommonResult alarmAdd(int faultId,String neName, int alarmId, String alarmName,
			int severity, String startTime, String clearTime)
			throws CommonException {
		CommonResult result = new CommonResult();
		Map map = new HashMap();
		map.put("faultId", faultId);
		map.put("neName", neName);
		map.put("alarmId", alarmId);
		map.put("alarmName", alarmName);
		map.put("severity", severity);
		map.put("startTime", startTime);
		map.put("clearTime", clearTime);
		map.put("errorCode", 1);
		faultStatisticsMapper.alarmAdd(map);
		return result;
	}
	@Override
	public EquipNameModel getEquipName(int unitId) throws CommonException {
		EquipNameModel model = new EquipNameModel();
		try{
			model.setUnitId(unitId);
			List<EquipNameModel> list = faultStatisticsMapper.getEquipName(model);
			if(list.size()>0){
				model = list.get(0);
				model.setErrorCode(0);
			}else model.setErrorCode(-1);
		}catch(Exception e){
			e.printStackTrace();
			throw new CommonException(e,MessageCodeDefine.COM_EXCPT_DB_OP);
		}
		return model;
	}
	//------333-------
	/**
	 * 图的一些通用设置
	 * @param fcd
	 */
	public void setFusionChartSettings(FusionChartDesigner fcd){
		fcd.setChart_caption("");
		fcd.setChart_xAxisName("");
		fcd.setChart_yAxisName("");
		fcd.setChart_bgcolor("#F3f3f3");
		fcd.setChart_showValues("1");
		fcd.getChartNodePro().put("baseFontSize","12");
		fcd.setLabelDisplay("WRAP");
//		fcd_1.setChartClickEnable(true);
		fcd.setChart_show_legend("1");
		fcd.setChart_legend_position("right");
		fcd.setChart_percent_label("1");
		fcd.setChart_Decimals("2");
		fcd.getChartNodePro().put("pieRadius","150");//饼状图半径控制
		//fcd_1.getChartNodePro().put("maxColWidth","30");
		fcd.getChartNodePro().put("showAboutMenuItem","0");
		fcd.getChartNodePro().put("showPrintMenuItem","0");
	}
	
	@SuppressWarnings({ "rawtypes", "unused" })
	@Override
	public Map<String, Object> getFaultStatisticsTotal( 
			Map<String, String> paramMap) throws CommonException{
		Map<String, Object> returnResult = new HashMap<String, Object>();
		try {
			paramMap.put("r_code", "233");
			paramMap.put("start", paramMap.get("start")+" 00:00:00");
			paramMap.put("end", paramMap.get("end")+" 23:59:59");
			List<Map> faultCount = faultStatisticsMapper
					.getFaultStatisticsTotal(paramMap);
			FusionChartDesigner fcd = new FusionChartDesigner();
			setFusionChartSettings(fcd);
			fcd.setChart_caption("故障总计");
			fcd.setOneDimensionList(faultCount, "systemName", "cnt");
			String chartXml = fcd.getOneDimensionsXMLData();
			returnResult.put("chartXml", chartXml);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnResult;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> getFaultStatisticsClassify(
			Map<String, String> paramMap) throws CommonException {
		Map<String, Object> returnResult = new HashMap<String, Object>();
		try{
			paramMap.put("r_code", "233");
			paramMap.put("start", paramMap.get("start")+" 00:00:00");
			paramMap.put("end", paramMap.get("end")+" 23:59:59");
			List<Map> faultCount = null;
			String xKey = "";
			FusionChartDesigner fcd = new FusionChartDesigner();
			setFusionChartSettings(fcd);
			switch(Integer.valueOf(paramMap.get("chartType"))){
			case 0: 
				faultCount = faultStatisticsMapper.getFaultStatisticsByType(paramMap);
				fcd.setChart_caption("故障按类型统计");
				xKey = "type";
				break;
			case 1: 
				faultCount = faultStatisticsMapper.getFaultStatisticsByReason(paramMap);
				fcd.setChart_caption("故障按二级原因统计");
				xKey = "reasonName";
				break;
			case 2: 
				faultCount = faultStatisticsMapper.getFaultStatisticsByFactory(paramMap);
				fcd.setChart_caption("故障按厂家统计");
				xKey = "factory";
				break;
			case 3: 
				faultCount = faultStatisticsMapper.getFaultStatisticsByNe(paramMap);
				fcd.setChart_caption("故障按网元型号统计");
				xKey = "productName";
				break;
			case 4:
				faultCount = faultStatisticsMapper.getFaultStatisticsByUnit(paramMap);
				fcd.setChart_caption("故障按板卡类型统计");
				xKey = "unit";
				break;
			}
			
			Map<String,List<Map>> filterData = fcd.dataListFilter(faultCount, "systemName");
			Iterator it = filterData.keySet().iterator();
			while(it.hasNext()){
				List<Map> dataList = filterData.get(it.next());
				fcd.addSerialDatasList(dataList.get(0).get("systemName")+"",dataList,xKey,"cnt");
			}
			fcd.setXListFromDataList(faultCount, xKey);
			String chartXml = fcd.getManyDimensionsXmlData();
			returnResult.put("chartXml", chartXml);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return returnResult;
	}

	@Override
	public Map<String, Object> getCurAlmList(int start, int limit) throws CommonException {
		
		Map<String, Object> result = new HashMap<String, Object>();
		// 定义时间格式转换器
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 获取数据库连接
		DBCollection conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_CURRENT_ALARM);
		
		// 设定查询条件
		BasicDBObject conditionQuery = new BasicDBObject ();
		// 条件：未清除，未分析
		conditionQuery.put("IS_CLEAR", CommonDefine.IS_CLEAR_NO);
		conditionQuery.put("ANALYSIS_STATUS", CommonDefine.IS_ANALYSIS_NO);

		// 查询当前告警
		DBCursor alms = conn.find(conditionQuery).skip(start).limit(limit);

		int total = alms.count();
		// 告警结果保存
		result.put("total", total);
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		while (alms.hasNext()) {
			DBObject dbo = alms.next();
			Map<String, Object> row = new HashMap<String, Object>();
			row.put("AlarmId", dbo.get("_id"));
			row.put("AlarmName", dbo.get("NATIVE_PROBABLE_CAUSE"));
			row.put("NeName", dbo.get("NE_NAME"));
			row.put("StartTime", "".equals(dbo.get("FIRST_TIME"))?"":sf.format(dbo.get("FIRST_TIME")));
			row.put("ClearTime", "".equals(dbo.get("CLEAR_TIME"))?"":sf.format(dbo.get("CLEAR_TIME")));
			row.put("Severity", dbo.get("PERCEIVED_SEVERITY"));
			row.put("PtpId", dbo.get("PTP_ID"));
			row.put("SystemName", dbo.get("SUBNET_NAME"));
			row.put("EmsName", dbo.get("EMS_NAME"));
			row.put("StationName", dbo.get("DISPLAY_STATION"));
			row.put("UnitDesc", dbo.get("UNIT_DESC"));
			row.put("UnitId", dbo.get("UNIT_ID"));
			rows.add(row);
		}

		result.put("rows", rows);
		return result;
	}
	
	@Override
	public List<FaultAnalysisModel> getAlmsByFaultIdFromHisAlm(int faultId)
			throws CommonException{
		
		List<FaultAnalysisModel> result = new ArrayList<FaultAnalysisModel>();
		// 定义时间格式转换器
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		List<Integer> alarmIds = alarmManagementMapper.getAlarmIdByFaultId(faultId);
		
		// 获取数据库连接
		DBCollection conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_HISTORY_ALARM);
		
		// 设定查询条件
		BasicDBObject conditionQuery = new BasicDBObject ();
		// 条件：未清除，未分析
		conditionQuery.put("_id", new BasicDBObject("$in", alarmIds.toArray()));

		// 查询历史告警
		DBCursor alarms = conn.find(conditionQuery).sort(new BasicDBObject("CLEAR_TIME",-1));
		
		// 告警结果保存
		while (alarms.hasNext()) {
			DBObject dbo = alarms.next();
			FaultAnalysisModel alm = new FaultAnalysisModel();
			alm.setAlarmId((Integer)dbo.get("_id"));
			alm.setAlarmName(dbo.get("NATIVE_PROBABLE_CAUSE").toString());
			alm.setNeName(dbo.get("NE_NAME").toString());
			alm.setStartTime("".equals(dbo.get("FIRST_TIME"))?"":sf.format(dbo.get("FIRST_TIME")));
			alm.setClearTime("".equals(dbo.get("CLEAR_TIME"))?"":sf.format(dbo.get("CLEAR_TIME")));
			alm.setSeverity((Integer)dbo.get("PERCEIVED_SEVERITY"));
			alm.setPtpId("".equals(dbo.get("PTP_ID"))?0:Integer.valueOf(dbo.get("PTP_ID").toString()));
			alm.setSysName(dbo.get("SUBNET_NAME").toString());
			alm.setEmsName(dbo.get("EMS_NAME").toString());
			alm.setStationName(dbo.get("DISPLAY_STATION").toString());
			alm.setUnitDesc(dbo.get("UNIT_DESC").toString());
			alm.setUnitId("".equals(dbo.get("UNIT_ID"))?0:Integer.valueOf(dbo.get("UNIT_ID").toString()));
			result.add(alm);
		}
		
		return result;
	}
}
