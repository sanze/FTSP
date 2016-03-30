package com.fujitsu.job;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
//import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRCsvDataSource;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.fujitsu.IService.IAlarmManagementService;
import com.fujitsu.IService.IAreaManagerService;
import com.fujitsu.IService.ICommonManagerService;
import com.fujitsu.IService.IInspectManagerService;
import com.fujitsu.IService.IPerformanceManagerService;
import com.fujitsu.IService.IQuartzManagerService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.dao.mysql.InspectManagerMapper;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.manager.resourceManager.serviceImpl.AreaManagerImpl;
import com.fujitsu.model.InspectDetailItemModel;
import com.fujitsu.model.InspectDetailModel;
import com.fujitsu.model.InspectSummaryItemModel;
import com.fujitsu.model.InspectSummaryModel;
import com.fujitsu.util.CommonUtil;
import com.fujitsu.util.SpringContextUtil;
/**
 * @author ZJL
 *
 */
public class InspectJob implements Job {

	private IInspectManagerService inspectManagerService;
	private ICommonManagerService commonManagerService;
	private IPerformanceManagerService performanceManagerService;
	private IQuartzManagerService quartzManagerService;
	private IAreaManagerService areaManagerService;
	private IAlarmManagementService alarmManagementService;
	
	private InspectManagerMapper inspectionManagerMapper;
	
	public InspectJob(){
		inspectManagerService = (IInspectManagerService) SpringContextUtil
				.getBean("inspectManagerServiceImpl");
		commonManagerService = (ICommonManagerService) SpringContextUtil
				.getBean("commonManagerServiceImpl");
		performanceManagerService = (IPerformanceManagerService) SpringContextUtil
				.getBean("performanceManagerServiceImpl");
		inspectionManagerMapper = (InspectManagerMapper) SpringContextUtil
				.getBean("inspectManagerMapper");
		quartzManagerService = (IQuartzManagerService) SpringContextUtil
				.getBean("quartzManagerService");
		areaManagerService = (IAreaManagerService) SpringContextUtil
				.getBean("areaManagerImpl");
		alarmManagementService = (IAlarmManagementService) SpringContextUtil
				.getBean("alarmManagementServiceImpl");
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void execute(JobExecutionContext context)throws JobExecutionException {

		Integer taskId = context.getJobDetail().getJobDataMap().getIntValue("taskId");
		Integer taskFailCnt = 0;//巡检项失败统计
		Integer taskResult = CommonDefine.QUARTZ.TASK.ACTION_STATUS.COMPLETED;
		Map<String,Object> taskInfoMap = null;
		String REPORT_NAME = null;
		String REPORT_DESCRIPTION = null;
		Date REPORT_START_TIME = new Date();
		Date REPORT_END_TIME = null;
		try{
			SimpleDateFormat REPORT_NAME_FORMAT = CommonUtil.getDateFormatter(
					CommonDefine.INSPECT.REPORT_NAME_DATE_FORMAT);
//			SimpleDateFormat COMMON_FORMAT = new SimpleDateFormat(CommonDefine.COMMON_FORMAT);
//			char NameSeparator='/';
//			char levelSeparator=CommonDefine.NameSeparator;
			taskInfoMap = inspectManagerService.getInspectTaskInfo(taskId);
			REPORT_DESCRIPTION = (String)((Map)taskInfoMap.get("task")).get("TASK_DESCRIPTION");

			// 任务状态： 更新任务状态为执行中
//			int userId = CommonDefine.USER_SYSTEM_ID;
			
		    updateTaskStatus(taskId ,CommonDefine.QUARTZ.TASK.ACTION_STATUS.RUNNING);
			
			Long REPORT_ITEM_CNT = 0L;
			Long REPORT_ITEM_SUB_CNT = 0L;
			Long REPORT_ITEM_SUB_ERROR_CNT = 0L;
			Long REPORT_EQUIP_NE_CNT = 0L;
			Long REPORT_EQUIP_NE_ERROR_CNT = 0L;
			Long REPORT_PTP_CNT = 0L;
			Long REPORT_PTP_USE_CNT = 0L;
			Long REPORT_CTP_CNT = 0L;
			Long REPORT_CTP_USE_CNT = 0L;
			
			Map<Integer,InspectSummaryItemModel> netDataKI = new HashMap<Integer,InspectSummaryItemModel>();
			Map<Integer,InspectSummaryItemModel> itemDataKI = new HashMap<Integer,InspectSummaryItemModel>();
			
			Map<Integer,InspectSummaryItemModel> psDataKI = new HashMap<Integer,InspectSummaryItemModel>();
			psDataKI.put(CommonDefine.INSPECT.SEVERITY.CRITICAL, 
					new InspectSummaryItemModel(
							CommonDefine.INSPECT.SEVERITY.valueToString(
									CommonDefine.INSPECT.SEVERITY.CRITICAL),0,0));
			psDataKI.put(CommonDefine.INSPECT.SEVERITY.MAJOR, 
					new InspectSummaryItemModel(
							CommonDefine.INSPECT.SEVERITY.valueToString(
									CommonDefine.INSPECT.SEVERITY.MAJOR),0,0));
			psDataKI.put(CommonDefine.INSPECT.SEVERITY.MINOR, 
					new InspectSummaryItemModel(
							CommonDefine.INSPECT.SEVERITY.valueToString(
									CommonDefine.INSPECT.SEVERITY.MINOR),0,0));
			
			Map<String,InspectSummaryItemModel> equipDataKI = new HashMap<String,InspectSummaryItemModel>();
			Map<Integer,InspectSummaryItemModel> areaDataKI = new HashMap<Integer,InspectSummaryItemModel>();
			Map<Integer,InspectSummaryItemModel> engineerDataKI = new HashMap<Integer,InspectSummaryItemModel>();
			Map<String,InspectSummaryItemModel> ptpDataKI = new HashMap<String,InspectSummaryItemModel>();
			
			Map<Integer,Integer> ERROR_SUBITEM_LIST = new HashMap<Integer,Integer>();
			
			//最终的总的数据源
			REPORT_NAME = (String)((Map)taskInfoMap.get("task")).get("TASK_NAME")
					+"报告"+REPORT_NAME_FORMAT.format(REPORT_START_TIME);
			File outDataFile=new File(CommonDefine.PATH_ROOT
					+CommonDefine.EXCEL.REPORT_DIR+"/"
					+CommonDefine.EXCEL.INSPECT_BASE+"/"
					+REPORT_NAME+".csv");
			writeCsvDataSourceHeader(InspectDetailItemModel.getColumnNames(),outDataFile);
			// neId SEVERITY 记录最高异常级别
			List<Integer> NE_LIST = new ArrayList<Integer>();
			// neId SEVERITY 记录最高异常级别
			Map<Integer, Integer> ERROR_NE_LIST = new HashMap<Integer, Integer>();
			
			//---------------------
			String[] inspectItemArray = (String[])taskInfoMap.get("inspectItemList");
			List<String> inspectItemStrList = Arrays.asList(inspectItemArray);
			List<Integer> inspectItemList = new ArrayList<Integer>();
			List<Map> equipList = inspectManagerService.getInspectEquipList(taskId,CommonDefine.INSPECT_TASK);
			for(String inspectItem:inspectItemStrList){
				Integer inspectItemValue=Integer.parseInt(inspectItem);
				if(!itemDataKI.containsKey(inspectItemValue)){
					inspectItemList.add(inspectItemValue);
					itemDataKI.put(inspectItemValue, new InspectSummaryItemModel(
							CommonDefine.INSPECT.TASK_ITEM.valueToString(inspectItemValue),0,0));
				}
				int [] subItem=new int []{3,2,0,1,1,1,3};
				if(inspectItemValue>0&&inspectItemValue<=subItem.length){
					REPORT_ITEM_SUB_CNT += subItem[inspectItemValue-1];
				}
			}
			// 任务状态： 任务执行详情中修改所有巡检项执行状态为等待，进度为0/equipList.size()
			inspectItemArray = null;
			inspectItemStrList = null;
			String inspectItemName;
			initAllItemStatus(taskId,inspectItemList);
			
			//巡检项目循环
			for(Integer inspectItemValue:inspectItemList){
				// 任务状态： 修改任务执行详情中该巡检项执行状态为执行中。
				InspectSummaryItemModel itemItem = itemDataKI.get(inspectItemValue);
				
				inspectItemName = CommonDefine.INSPECT.TASK_ITEM.valueToString(inspectItemValue);
				updateInspectItemStatus(taskId,inspectItemName,"",CommonDefine.QUARTZ.TASK.ACTION_STATUS.RUNNING);
				initAllEquipStatus(taskId,inspectItemName);

				Integer itemFailCnt = 0;//巡检设备失败统计
				//巡检设备循环
				for(Map equipMap:equipList){
					Integer nodeLevel = (Integer)equipMap.get("TARGET_TYPE");
					Integer nodeId = (Integer)equipMap.get("TARGET_ID");
					int isSuccess = CommonDefine.TRUE;
					try{
					
					if(CommonDefine.TREE.NODE.NE>=nodeLevel){
						final int limit = 200;
						int offset=0;
						Map neDataModel = new HashMap();
						Integer total = 0;
						if (CommonDefine.TREE.NODE.NE==nodeLevel){
							//取得所有网元
							Map<String, Object> neDetail = new HashMap<String, Object>();
							neDetail = commonManagerService.getNodeInfo(nodeId, nodeLevel);
							if(neDetail!=null&&!neDetail.isEmpty()){
								List<Map> neDetailList = new ArrayList<Map>();
								neDetailList.add(neDetail);
								neDataModel.put("rows", neDetailList);
								neDataModel.put("total", 1);
							}
						}else {
							//取得所有网元
							if (CommonDefine.TREE.NODE.EMS>=nodeLevel){
								//
							}
							neDataModel=commonManagerService.getNodesInfoByRoot(nodeId, nodeLevel,CommonDefine.TREE.NODE.NE, offset, limit);
						}
						total = (Integer)neDataModel.get("total");
						if(total<=0){
							continue;//巡检设备不存在,跳过
						}
						for(offset=0;offset<total;offset+=limit){
							if(offset>0){
								neDataModel = commonManagerService.getNodesInfoByRoot(nodeId, nodeLevel, CommonDefine.TREE.NODE.NE, offset, limit);
								total = (Integer)neDataModel.get("total");
							}
							List<Map> neDetailList = new ArrayList<Map>();
							neDetailList = (List<Map>)neDataModel.get("rows");
							if(neDetailList==null||neDetailList.isEmpty()){
								break;
							}
//							REPORT_EQUIP_NE_CNT+=neDetailList.size();
							//单网元巡检
							for(Map neDetail:neDetailList){
								itemItem.setNeCnt(itemItem.getNeCnt()+1);
								
								Integer neId = (Integer)neDetail.get("BASE_NE_ID");
								Integer emsId = (Integer)neDetail.get("BASE_EMS_CONNECTION_ID");
								
								//取得以下几个字段值
								Map<String, String> displayMap=getDisplayMap(neDetail);

								//异常等级结果
								Integer SEVERITY = null;
								InspectDetailItemModel tpl = new InspectDetailItemModel(
										displayMap.get("DISPLAY_NE"),
										inspectItemName,null,
										null,
										null,
										null,
										CommonDefine.INSPECT.SEVERITY.NORMAL,
										displayMap.get("DISPLAY_EMS"),
										displayMap.get("DISPLAY_SUBNET"),
										displayMap.get("PRODUCT_NAME"),
										displayMap.get("DISPLAY_AREA"),
										null,
										displayMap.get("OWNER"),
										null);
//								List<InspectDetailItemModel> datailList=new ArrayList<InspectDetailItemModel>();
//								Integer subItem = null;
//								String evaluateItemName = null;
//								Integer exceptionLv = null;
								Map<Integer, Integer> exceptionLvMap=new HashMap<Integer, Integer>();
								switch (inspectItemValue) {
								/* 基础数据
								 * COMMUNICATION_STATE 通信状态 0.在线 1.离线 2.未知 INT  是 
								 * BASIC_SYNC_STATUS 同步状态 1.已同步 2.未同步 3.同步失败 INT  是    
								 * BASIC_SYNC_TIME 网元同步时间 DATETIME  是    
								 * BASIC_SYNC_RESULT 同步结果 VARCHAR(64) 64 是 
								 * CREATE_TIME 创建时间 DATETIME  是    
								 * UPDATE_TIME 更新时间 TIMESTAMP 
								 */
								case CommonDefine.INSPECT.TASK_ITEM.BASE:
									exceptionLvMap=evaluateBase(tpl, neDetail, outDataFile);
									break;
								/* 性能
								 * T_BASE_NE(网元表)
								 * LAST_COLLECT_TIME 最近采集时间 DATETIME  是 
								 * COLLECT_RESULT 采集结果 VARCHAR(256) 256 是 "完成""进行中""采集失败*"
								 * 
								 * T_PM_ORIGI_DATA_X_Y_Z 
								 * EXCEPTION_LV 异常等级 0：正常 1：告警等级1 2：告警等级2 3：告警等级3 INT  是    
								 * EXCEPTION_COUNT 连续异常次数 INT 
								 */
								case CommonDefine.INSPECT.TASK_ITEM.PM_COUNT:
								//case CommonDefine.INSPECT.TASK_ITEM.PM_PHYSICAL:
									exceptionLvMap=evaluatePM(tpl, neDetail, outDataFile);
									break;
								/* 告警
								 * 
								 */
								case CommonDefine.INSPECT.TASK_ITEM.ALARM:
									exceptionLvMap=evaluateAlarm(tpl, neDetail, outDataFile);
									break;
								// 网元时间 无
								case CommonDefine.INSPECT.TASK_ITEM.TIME:
									exceptionLvMap=evaluateTime(tpl, neDetail, outDataFile);
									break;
								/* 保护
								 * T_BASE_PRO_GROUP(保护组) T_BASE_E_PRO_GROUP T_BASE_WDM_PRO_GROUP
								 * BASE_PRO_GROUP_ID BASE_PRO_GROUP_ID INT      
								 * BASE_EMS_CONNECTION_ID BASE_EMS_CONNECTION_ID INT  是    
								 * BASE_NE_ID id INT  是   
								 * IS_DEL 是否删除 0：不是 1：是 INT  是  0  
								 * CREATE_TIME 创建时间 DATETIME  是    
								 * UPDATE_TIME 更新时间 TIMESTAMP 
								 * PROTECTION_SCHEMA_STATE 0._PSS_UNKNOWN 1._PSS_AUTOMATIC 2._PSS_FORCED_OR_LOCKED_OUT INT 
								 */
								case CommonDefine.INSPECT.TASK_ITEM.PROTECT:
									exceptionLvMap=evaluateProtect(tpl, neDetail, outDataFile);
									break;
								/* 时钟
								 * T_BASE_CLOCK
								 * BASE_CLOCK_ID BASE_CLOCK_ID INT      
								 * BASE_NE_ID BASE_NE_ID INT 
								 * IS_CURRENT 0：CURRENT 1：BACKUP 
								 * QUALITY	0：CSQ_LEVELUNKNOWN——表示未知。
								            1：CSQ_G811——表示G.811。
								            2：CSQ_G812TRANSIT——表示G.812Transit。
								            3：CSQ_G812LOCAL——表示G.812Local。
								            4：CSQ_G813——表示G.813。
								            5：CSQ_NOTFORSYNCLK——表示非同步时钟源。
								 * IS_DEL 是否删除 0：不是 1：是 INT  是  0  
								 * CREATE_TIME 创建时间 DATETIME  是    
								 * UPDATE_TIME 更新时间 TIMESTAMP 
								 */
								case CommonDefine.INSPECT.TASK_ITEM.CLOCK:
									exceptionLvMap=evaluateClock(tpl, neDetail, outDataFile);
									break;
								default:
									break;
								}
								ERROR_SUBITEM_LIST.putAll(exceptionLvMap);
								if(!exceptionLvMap.isEmpty()){
									List<Integer> exceptionLvList=new ArrayList(exceptionLvMap.values());
									Collections.sort(exceptionLvList);
									SEVERITY = exceptionLvList.get(exceptionLvList.size()-1);
								}
								//网络层次汇总
								Map<String, Object> emsDetail = commonManagerService.getNodeInfo(emsId, CommonDefine.TREE.NODE.EMS);
								InspectSummaryItemModel netItem = null;
								if(netDataKI.containsKey(emsId)){
									netItem=netDataKI.get(emsId);
								}else{
									netItem = new InspectSummaryItemModel(
											(String)emsDetail.get("DISPLAY_NAME"),0,0);
									netDataKI.put(emsId, netItem);
								}
								//设备类型汇总
								String producName = (String)neDetail.get("PRODUCT_NAME");
								InspectSummaryItemModel equipItem = null;
								if(equipDataKI.containsKey(producName)){
									equipItem=equipDataKI.get(producName);
								}else{
									equipItem = new InspectSummaryItemModel(
											producName,0,0);
									equipDataKI.put(producName, equipItem);
								}
								//区域层次汇总
								Integer roomId = (Integer)neDetail.get("RESOURCE_ROOM_ID");
								Map<String, Object> resourceDetail = new HashMap<String, Object>();
								if(roomId!=null){
									resourceDetail = inspectManagerService.getResourceInfoByRoom(roomId);
								}
								if(resourceDetail==null||resourceDetail.isEmpty()){
									resourceDetail = new HashMap<String, Object>();
									resourceDetail.put("RESOURCE_AREA_ID", 
											CommonDefine.INSPECT.CONST.UNDEFINE);
									resourceDetail.put("AREA_NAME", 
											CommonDefine.INSPECT.CONST.valueToString(CommonDefine.INSPECT.CONST.UNDEFINE));
								}
								Integer areaId = (Integer)resourceDetail.get("RESOURCE_AREA_ID");
								InspectSummaryItemModel areaItem = null;
								if(areaDataKI.containsKey(areaId)){
									areaItem=areaDataKI.get(areaId);
								}else{
									areaItem = new InspectSummaryItemModel(
											(String)resourceDetail.get("AREA_NAME"),0,0);
									areaDataKI.put(areaId, areaItem);
								}
								//包机人汇总
								Map pathNodesMap=commonManagerService.treeGetNodesByKey(null, neId, CommonDefine.TREE.NODE.NE, 0, 0, true, 0, 0,CommonDefine.USER_ADMIN_ID);
								List<Map> engineerList = null;
								if(pathNodesMap!=null&&pathNodesMap.get("rows")!=null&&!((List)pathNodesMap.get("rows")).isEmpty()){
									engineerList = inspectManagerService.getEngineerByNodes((List<Map>)pathNodesMap.get("rows"));
								}
								if(engineerList==null||engineerList.isEmpty()){
									Map<String, Object> engineer = new HashMap<String, Object>();
									engineer.put("INSPECT_ENGINEER_ID", CommonDefine.INSPECT.CONST.UNDEFINE);
									engineer.put("NAME", 
											CommonDefine.INSPECT.CONST.valueToString(
													CommonDefine.INSPECT.CONST.UNDEFINE));
									engineerList = new ArrayList<Map>();
									engineerList.add(engineer);
								}
								List<InspectSummaryItemModel> engineerItems = new ArrayList<InspectSummaryItemModel>();
								for(Map engineer:engineerList){
									Integer engineerId = (Integer)engineer.get("INSPECT_ENGINEER_ID");
									if(engineerDataKI.containsKey(engineerId)){
										engineerItems.add(engineerDataKI.get(engineerId));
									}else{
										InspectSummaryItemModel engineerItem = new InspectSummaryItemModel(
												(String)engineer.get("NAME"),0,0);
										engineerItems.add(engineerItem);
										engineerDataKI.put(engineerId, engineerItem);
									}
								}
								//端口类型统计
								List<InspectSummaryItemModel> ptpItems = new ArrayList<InspectSummaryItemModel>();
								List<Map> ptpTypeList = inspectManagerService.getPtpTypeByNe(neId);
								if(ptpTypeList!=null&&!ptpTypeList.isEmpty()){
									for(Map ptpType:ptpTypeList){
										final String NA = "NA";
										String ptpTypeId = NA;
										if (ptpType!=null){
											ptpTypeId = (String)ptpType.get("PTP_TYPE");
										}
										if(ptpTypeId==null||ptpTypeId.isEmpty())
											ptpTypeId = NA;
										if(ptpDataKI.containsKey(ptpTypeId)){
											ptpItems.add(ptpDataKI.get(ptpTypeId));
										}else{
											InspectSummaryItemModel ptpItem = new InspectSummaryItemModel(
													ptpTypeId,0,0);
											ptpItems.add(ptpItem);
											ptpDataKI.put(ptpTypeId, ptpItem);
										}
									}
								}
								
								if(!NE_LIST.contains(neId)){
									NE_LIST.add(neId);
									//网元总数累加
									netItem.setNeCnt(netItem.getNeCnt()+1);
									equipItem.setNeCnt(equipItem.getNeCnt()+1);
									areaItem.setNeCnt(areaItem.getNeCnt()+1);
									for(InspectSummaryItemModel engineerItem:engineerItems){
										engineerItem.setNeCnt(engineerItem.getNeCnt()+1);
									}
									//端口类型统计
									for(InspectSummaryItemModel ptpItem:ptpItems){
										int nePtpCnt = inspectManagerService.CountNePtpByType(neId,ptpItem.getDisplayName());
										ptpItem.setNeCnt(ptpItem.getNeCnt()+nePtpCnt);
									}
									//端口使用率统计
									Integer countNePtp=inspectManagerService.CountNePtp(neId);
									Integer countNePtpInUSE=inspectManagerService.CountNePtpInUSE(neId);
									REPORT_PTP_CNT+=countNePtp;
									REPORT_PTP_USE_CNT+=countNePtpInUSE;
									//时隙利用率统计
									Integer countNeCtp=inspectManagerService.CountNeCtp(neId);
									Integer countNeCtpInUSE=inspectManagerService.CountNeCtpInUSE(neId);
									REPORT_CTP_CNT+=countNeCtp;
									REPORT_CTP_USE_CNT+=countNeCtpInUSE;
									
									{//端口使用率、时隙利用率
										List<InspectDetailItemModel> datailList=new ArrayList<InspectDetailItemModel>();
										java.text.DecimalFormat df = new java.text.DecimalFormat("##.00%");    
										java.math.BigDecimal percent = new java.math.BigDecimal(0); 
										InspectDetailItemModel detail=tpl.clone();
										detail.setInspectItemName(null);
										detail.setValueDesc("端口使用率");
										percent=new java.math.BigDecimal(countNePtpInUSE*1.0/countNePtp);
										detail.setValue(df.format(percent));
										datailList.add(detail);
										detail=tpl.clone();
										detail.setInspectItemName(null);
										detail.setValueDesc("时隙利用率");
										percent=new java.math.BigDecimal(countNeCtpInUSE*1.0/countNeCtp);
										detail.setValue(df.format(percent));
										datailList.add(detail);
										writeCsvDataSource(datailList,outDataFile);
									}
								}
								if(SEVERITY!=null){//异常网元
									//巡检项汇总
									itemItem.setNeErrorCnt(itemItem.getNeErrorCnt()+1);
									if(!ERROR_NE_LIST.containsKey(neId)){//未报告异常,添加
										ERROR_NE_LIST.put(neId, SEVERITY);
										//网络层次汇总
										netItem.setNeErrorCnt(netItem.getNeErrorCnt()+1);
										//设备类型汇总
										equipItem.setNeErrorCnt(equipItem.getNeErrorCnt()+1);
										//区域汇总
										areaItem.setNeErrorCnt(areaItem.getNeErrorCnt()+1);
										//包机人汇总
										for(InspectSummaryItemModel engineerItem:engineerItems){
											engineerItem.setNeErrorCnt(engineerItem.getNeErrorCnt()+1);
										}
										//巡检项汇总
										//itemItem.setNeErrorCnt(itemItem.getNeErrorCnt()+1);
										
										//严重度汇总
										InspectSummaryItemModel psItem = psDataKI.get(SEVERITY);
										psItem.setNeErrorCnt(psItem.getNeErrorCnt()+1);
									}else{
										Integer severity = ERROR_NE_LIST.get(neId);
										if(severity<SEVERITY){//一报告比本次低级的异常,修改异常级别,去除低级异常统计
											InspectSummaryItemModel psItem = psDataKI.get(severity);
											psItem.setNeErrorCnt(psItem.getNeErrorCnt()-1);//扣除之前统计的低级异常
											
											psItem = psDataKI.get(SEVERITY);
											psItem.setNeErrorCnt(psItem.getNeErrorCnt()+1);//添加本次高级异常
											ERROR_NE_LIST.put(neId,SEVERITY);//记录网元异常级别
										}
										//else if(severity<SEVERITY)此网元已报告比此次更严重的异常 忽略本次异常统计
										//else 此网元已报告过相同级别异常
									}
								}
							}
						}
					}else{
						//设备类型错误,跳过
						//continue;
					}
					}catch(Exception e){
						// 任务状态： 修改任务执行详情中该巡检项描述，失败数+1。并统计失败数保存在变量中
						itemFailCnt++;
						isSuccess = CommonDefine.FALSE;
						//isComplete = CommonDefine.IS_COMPLETE.UNCOMPLETE;
						//updateEquipStatus(taskId,nodeLevel,nodeId,isSuccess,isComplete);
					}finally{
					// 任务状态： 修改任务执行详情中该巡检项进度，完成设备数+1。
						//isSuccess = CommonDefine.IS_SUCCESS.SUCCESS;
						updateEquipStatus(taskId,nodeLevel,nodeId,isSuccess,CommonDefine.TRUE);
					}
				}
				Integer itemResult = CommonDefine.QUARTZ.TASK.ACTION_STATUS.COMPLETED;
				if(itemFailCnt<=0){//完成
					itemResult = CommonDefine.QUARTZ.TASK.ACTION_STATUS.COMPLETED;
				}else if(itemFailCnt>0&&itemFailCnt<equipList.size()){//部分成功
					itemResult = CommonDefine.QUARTZ.TASK.ACTION_STATUS.PARTLY_COMPLETED;
					taskFailCnt++;
				}else{//失败
					itemResult = CommonDefine.QUARTZ.TASK.ACTION_STATUS.UNCOMPLETED;
					taskFailCnt++;
				}
				// 任务状态： 根据统计的失败数,修改任务执行详情中该巡检项状态为完成/部分成功/失败,更新巡检项执行完成时间。
			    inspectItemName = CommonDefine.INSPECT.TASK_ITEM.valueToString(inspectItemValue);
				updateInspectItemStatus(taskId,inspectItemName,"",itemResult);
				
			}
			REPORT_END_TIME = new Date();
//			REPORT_NAME = (String)((Map)taskInfoMap.get("task")).get("TASK_NAME")+"报告"+REPORT_NAME_FORMAT.format(REPORT_END_TIME);
			
			String templateURL=null;
			File outReportFile=null;
			{//巡检报告纲要
				templateURL=new String("resourceConfig/template/inspectSummary.jasper");
				
				outReportFile=new File(CommonDefine.PATH_ROOT
						+CommonDefine.EXCEL.REPORT_DIR+"/"
						+CommonDefine.EXCEL.INSPECT_BASE+"/"
						+REPORT_NAME+"-巡检报告纲要"+".htm");
				
				REPORT_ITEM_CNT = (long)inspectItemList.size();
				REPORT_EQUIP_NE_CNT = (long)NE_LIST.size();
				REPORT_EQUIP_NE_ERROR_CNT = (long)ERROR_NE_LIST.size();
				REPORT_ITEM_SUB_ERROR_CNT = (long)ERROR_SUBITEM_LIST.size();
				List<InspectSummaryItemModel> equipDataList = new ArrayList<InspectSummaryItemModel>();
//				equipDataList.add(new InspectSummaryItemModel("网管",0,0));
				equipDataList.addAll(equipDataKI.values());
				for(InspectSummaryItemModel data:psDataKI.values()){
					data.setNeCnt(REPORT_EQUIP_NE_CNT.intValue());
				}
				JRCsvDataSource detailDS=new JRCsvDataSource(outDataFile);
				try {
					detailDS.setUseFirstRowAsHeader(true);
					detailDS.setDateFormat(DateFormat.getDateTimeInstance());
					InspectSummaryModel datas=new InspectSummaryModel(
							REPORT_NAME,REPORT_START_TIME,REPORT_END_TIME,
							REPORT_DESCRIPTION,
							REPORT_ITEM_CNT,REPORT_ITEM_SUB_CNT,
							REPORT_EQUIP_NE_CNT,REPORT_EQUIP_NE_ERROR_CNT,REPORT_ITEM_SUB_ERROR_CNT,
							new JRBeanCollectionDataSource(netDataKI.values()),
							new JRBeanCollectionDataSource(itemDataKI.values()),
							new JRBeanCollectionDataSource(psDataKI.values()),
							new JRBeanCollectionDataSource(equipDataList),
							new JRBeanCollectionDataSource(areaDataKI.values()),
							new JRBeanCollectionDataSource(engineerDataKI.values()),
							detailDS,
							new JRBeanCollectionDataSource(ptpDataKI.values()),
							REPORT_PTP_CNT,REPORT_PTP_USE_CNT,REPORT_CTP_CNT,REPORT_CTP_USE_CNT);
					exportReportToHtmlFile(datas,templateURL,outReportFile,null);
				} catch (Exception e) {
					throw e;
				} finally {
					detailDS.close();
				}
			}
			{//异常项明细报告
				templateURL=new String("resourceConfig/template/detailReport.jasper");
				outReportFile=new File(CommonDefine.PATH_ROOT
						+ CommonDefine.EXCEL.REPORT_DIR + "/"
						+ CommonDefine.EXCEL.INSPECT_BASE + "/"
						+ REPORT_NAME+"-异常项明细报告" + ".htm");
				
				JRCsvDataSource detailDS=new JRCsvDataSource(outDataFile);
				try {
					detailDS.setUseFirstRowAsHeader(true);
					detailDS.setDateFormat(DateFormat.getDateTimeInstance());
					InspectDetailModel main = new InspectDetailModel(
							REPORT_NAME+"-异常项明细报告",
							REPORT_END_TIME,REPORT_START_TIME,
							REPORT_DESCRIPTION,
							detailDS);
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("SUBREPORT_FILE", "exDetailSubReport.jasper");
					parameters.put("GROUP_NAME", "LVL");
					
					exportReportToHtmlFile(main,templateURL,outReportFile,parameters);
				} catch (Exception e) {
					throw e;
				} finally {
					detailDS.close();
				}
			}
			{//网络层次明细报告
				templateURL=new String("resourceConfig/template/detailReport.jasper");
				outReportFile=new File(CommonDefine.PATH_ROOT
						+ CommonDefine.EXCEL.REPORT_DIR + "/"
						+ CommonDefine.EXCEL.INSPECT_BASE + "/"
						+ REPORT_NAME+"-网络层次明细报告" + ".htm");
				
				JRCsvDataSource detailDS=new JRCsvDataSource(outDataFile);
				try {
					detailDS.setUseFirstRowAsHeader(true);
					detailDS.setDateFormat(DateFormat.getDateTimeInstance());
					InspectDetailModel main = new InspectDetailModel(
							REPORT_NAME+"-网络层次明细报告",
							REPORT_END_TIME,REPORT_START_TIME,
							REPORT_DESCRIPTION,
							detailDS);
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("SUBREPORT_FILE", "detailSubReport.jasper");

					exportReportToHtmlFile(main,templateURL,outReportFile,parameters);
				} catch (Exception e) {
					throw e;
				} finally {
					detailDS.close();
				}
			}
			{//网络层次明细报告
				templateURL=new String("resourceConfig/template/detailReport.jasper");
				outReportFile=new File(CommonDefine.PATH_ROOT
						+ CommonDefine.EXCEL.REPORT_DIR + "/"
						+ CommonDefine.EXCEL.INSPECT_BASE + "/"
						+ REPORT_NAME+"-包机人明细报告" + ".htm");
				
				JRCsvDataSource detailDS=new JRCsvDataSource(outDataFile);
				try {
					detailDS.setUseFirstRowAsHeader(true);
					detailDS.setDateFormat(DateFormat.getDateTimeInstance());
					InspectDetailModel main = new InspectDetailModel(
							REPORT_NAME+"-包机人明细报告",
							REPORT_END_TIME,REPORT_START_TIME,
							REPORT_DESCRIPTION,
							detailDS);
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("SUBREPORT_FILE", "detailSubReport.jasper");
					parameters.put("GROUP_NAME", "OWNER");
					exportReportToHtmlFile(main,templateURL,outReportFile,parameters);
				} catch (Exception e) {
					throw e;
				} finally {
					detailDS.close();
				}
			}
			// 任务状态： 更新任务执行状态为执行成功
			if (taskFailCnt <= 0) {// 完成
				taskResult = CommonDefine.QUARTZ.TASK.ACTION_STATUS.COMPLETED;
			} else if (taskFailCnt > 0 && taskFailCnt < inspectItemList.size()) {// 部分成功
				taskResult = CommonDefine.QUARTZ.TASK.ACTION_STATUS.PARTLY_COMPLETED;
				taskFailCnt++;
			} else {// 失败
				taskResult = CommonDefine.QUARTZ.TASK.ACTION_STATUS.UNCOMPLETED;
				taskFailCnt++;
			}
		}catch(Exception e){
			taskResult = CommonDefine.QUARTZ.TASK.ACTION_STATUS.UNCOMPLETED;
			ExceptionHandler.handleException(e);
		}catch(Throwable e){
			taskResult = CommonDefine.QUARTZ.TASK.ACTION_STATUS.UNCOMPLETED;
		}finally{
			// 任务状态： 往巡检报告中插入一条记录
			String[] privilegeList = (String[])taskInfoMap.get("privilegeList");
			String GROUP_ARRAY = "";
			if(privilegeList!=null){
				GROUP_ARRAY=Arrays.asList(privilegeList).toString();
				GROUP_ARRAY=","+GROUP_ARRAY.substring(1,GROUP_ARRAY.length()-1)+",";
			}
			Integer CREATE_USER = (Integer)((Map)taskInfoMap.get("task")).get("CREATE_PERSON");

			// 任务状态： 更新任务执行状态
			updateTaskStatus(taskId ,taskResult);
			// 任务状态：更新任务下次执行时间
			updateTaskTimeStatus(taskId);
			addReport(REPORT_NAME,REPORT_DESCRIPTION,taskResult,GROUP_ARRAY,CREATE_USER,REPORT_END_TIME);
		}
	}
	
	private void addReport(String REPORT_NAME,String REPORT_DESCRIPTION,Integer taskResult,String GROUP_ARRAY,Integer CREATE_USER,Date CREATE_TIME){
		Map<String,Object> report = new HashMap<String,Object>();
		report.put("REPORT_NAME", REPORT_NAME);
		report.put("NOTE", REPORT_DESCRIPTION);
		report.put("RESULT", CommonDefine.QUARTZ.TASK.ACTION_STATUS.valueToString(taskResult));
		report.put("GROUP_ARRAY", GROUP_ARRAY);
		report.put("CREATE_USER", CREATE_USER);
		report.put("CREATE_TIME", CREATE_TIME);
		inspectionManagerMapper.insertInspectReport(report);
	}

	public void updateTaskStatus(int taskId ,int result){
	    try{
	    	Map<String,Object> map = new HashMap<String,Object>();
		    map.put("taskId", taskId);		    
		    map.put("result", result);		    
		    inspectionManagerMapper.updateTaskInfo(map);	    	
		}catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}

	public void updateTaskTimeStatus(int taskId){
		//巡检时间转换
	    Calendar cal=Calendar.getInstance();
	    //获取当前时间
	    long currentDate = cal.getTimeInMillis();
	    Date currentTime = new Date(currentDate);
	    Date last = currentTime;
	    Date nextTime = null;
	    try{
		    // 任务下次执行时间更新
			nextTime = (Date)quartzManagerService.getJobInfo(CommonDefine.QUARTZ.JOB_INSPECT, taskId).get("nextFireTime");
		}catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
        Map<String,Object> map = new HashMap<String,Object>();
	    map.put("taskId", taskId);
	    map.put("endTime", last);
	    map.put("nextTime", nextTime);
	    inspectionManagerMapper.updateTaskInfo(map);
	}

	private void initAllEquipStatus(int taskId,String inspectItem){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("taskId", taskId);
		map.put("targetType", null);
		map.put("targetId", null);
		map.put("isSuccess", CommonDefine.FALSE);
		map.put("isComplete", CommonDefine.FALSE);
		inspectionManagerMapper.updateEquipStatus(map);
	}

	private void updateEquipStatus(int taskId,int targetType, int targetId,int isSuccess,int isComplete){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("taskId", taskId);
		map.put("targetType", targetType);
		map.put("targetId", targetId);
		map.put("isSuccess", isSuccess);
		map.put("isComplete", isComplete);
		inspectionManagerMapper.updateEquipStatus(map);
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initAllItemStatus(int taskId,List<Integer> inspectItemList){
		List<Map> dbRunDetails=inspectionManagerMapper.getInspectTaskItem(taskId);
		List<Map> addList = new ArrayList<Map>();
		List<Map> updateList = new ArrayList<Map>();
		List<Map> delList = new ArrayList<Map>();
		
		int index=0;
		for(Integer inspectItemValue:inspectItemList){
			String inspectItem = CommonDefine.INSPECT.TASK_ITEM.valueToString(inspectItemValue);
			Map map=new HashMap();
			if(dbRunDetails.size()>index){
				map.put("taskRunDetialId", dbRunDetails.get(index).get("SYS_TASK_RUN_DETAIL_ID"));
				updateList.add(map);
			}else{
				map.put("taskRunDetialId", null);
				addList.add(map);
			}
			index++;
			//巡检时间转换
		    SimpleDateFormat date=CommonUtil.getDateFormatter(
					CommonDefine.COMMON_FORMAT);
		    Calendar cal=Calendar.getInstance();
		    //获取当前时间
		    long currentDate = cal.getTimeInMillis();
		    Date dateStart = new Date(currentDate);
		    String createTime = date.format(dateStart);

			map.put("taskId", taskId);
			map.put("targetName", inspectItem);
			map.put("runResult", CommonDefine.QUARTZ.TASK.ACTION_STATUS.WAITING);
			map.put("detialInfo", "");
			map.put("createTime", createTime);
//			addInspectItemStatus(taskId,inspectItemName,"",CommonDefine.QUARTZ.WAITING);
		}
		if(index<dbRunDetails.size()){
			delList=dbRunDetails.subList(index,dbRunDetails.size());
			inspectionManagerMapper.delInspectItemStatus(delList);
		}
		for(Map map:updateList)
			inspectionManagerMapper.updateInspectItemStatus(map);
		if(!addList.isEmpty())
			inspectionManagerMapper.addInspectItemStatus(addList);
	}
/*   private void addInspectItemStatus(int taskId,String InspectItem,String detialInfo, int runResult){
		
		//巡检时间转换
	    SimpleDateFormat date=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	    Calendar cal=Calendar.getInstance();
	    //获取当前时间
	    long currentDate = cal.getTimeInMillis();
	    Date dateStart = new Date(currentDate);
	    String createTime = date.format(dateStart);
		
		Map map = new HashMap();

		map.put("taskRunDetialId", null);
		map.put("taskId", taskId);
		map.put("targetName", InspectItem);
		map.put("runResult", runResult);
		map.put("detialInfo", detialInfo);
		map.put("createTime", createTime);
		
		inspectionManagerMapper.addInspectItemStatus(map);
		
	}*/

	private void updateInspectItemStatus(int taskId,String InspectItem,String detialInfo, int runResult){
		//巡检时间转换
	    SimpleDateFormat date=CommonUtil.getDateFormatter(CommonDefine.COMMON_FORMAT);
	    Calendar cal=Calendar.getInstance();
	    //获取当前时间
	    long currentDate = cal.getTimeInMillis();
	    Date dateStart = new Date(currentDate);
	    String updateTime = date.format(dateStart);
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("taskId", taskId);
		map.put("targetName", InspectItem);
		map.put("runResult", runResult);
		map.put("detialInfo", detialInfo);
		map.put("updateTime", updateTime);
		
		inspectionManagerMapper.updateInspectItemStatus(map);
	}

	public static void exportReportToHtmlFile(Object datas,String templateURL,File ouputfile,Map<String, Object> parameters) throws JRException{
		List<Object> colsList=new ArrayList<Object>();
		colsList.add(datas);
		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(colsList);
		InputStream fileIn = Thread.currentThread().getContextClassLoader().getResourceAsStream(templateURL);
		if(parameters==null)
			parameters = new HashMap<String, Object>();
		parameters.put("SUBREPORT_DIR", "resourceConfig/template/");
		JasperPrint jasperPrint = JasperFillManager.fillReport(fileIn, parameters, dataSource);
		if(ouputfile.getParentFile()!=null&&(!ouputfile.getParentFile().exists()||!ouputfile.getParentFile().isDirectory()))
			ouputfile.getParentFile().mkdirs();
		JRHtmlExporter exporter = new JRHtmlExporter();
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
//		exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
		exporter.setParameter(JRExporterParameter.OUTPUT_FILE, ouputfile);
//		exporter.setParameter(JRHtmlExporterParameter.IGNORE_PAGE_MARGINS,  Boolean.TRUE);
		exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);  
		exporter.setParameter(JRHtmlExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.FALSE);  
		exporter.setParameter(JRHtmlExporterParameter.FRAMES_AS_NESTED_TABLES, Boolean.TRUE);  

		exporter.exportReport();
//		JasperExportManager.exportReportToHtmlFile(jasperPrint,ouputfile.getPath());
	}
	public static void writeCsvDataSourceHeader(String[] columnNames, File ouputfile) throws CommonException{
		FileWriter fileWriter = null;
		try{
			// 创建父文件
			if (!ouputfile.getParentFile().exists()) {
				ouputfile.getParentFile().mkdirs();
			}
			fileWriter = new FileWriter(ouputfile);
			StringBuffer line = new StringBuffer();
			for(int i=0;i<columnNames.length;i++){
				line.append(columnNames[i]);
				if(i<columnNames.length-1)
					line.append(',');
			}
			fileWriter.append(line.append("\r\n"));
			fileWriter.flush();
			fileWriter.close();
		}catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_UNKNOW);
		}
	}
	public static void writeCsvDataSource(List<InspectDetailItemModel> datailList,File ouputfile) throws CommonException{
		FileWriter fileWriter = null;
		try{
			// 创建父文件
			if (!ouputfile.getParentFile().exists()) {
				ouputfile.getParentFile().mkdirs();
			}
			fileWriter = new FileWriter(ouputfile,true);
			for(InspectDetailItemModel detail:datailList){
				fileWriter.append(detail.toCsvRow(',')+"\r\n");
			}
			fileWriter.flush();
			fileWriter.close();
		}catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_UNKNOW);
		}
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map<String,String> getDisplayMap(Map<String, Object> neDetail) throws CommonException{
		String DISPLAY_NE=String.valueOf(neDetail.get("DISPLAY_NAME"));
		String PRODUCT_NAME=String.valueOf(neDetail.get("PRODUCT_NAME"));
		String DISPLAY_EMS = null;
		String DISPLAY_SUBNET = CommonDefine.INSPECT.CONST.valueToString(CommonDefine.INSPECT.CONST.UNDEFINE);
		String DISPLAY_AREA = null;
		String OWNER = null;
		Integer neId = (Integer)neDetail.get("BASE_NE_ID");
		Map pathMap=commonManagerService.treeGetNodesByKey(null,neId,
				CommonDefine.TREE.NODE.NE,CommonDefine.TREE.ROOT_ID,CommonDefine.TREE.NODE.ROOT,true,0,0,CommonDefine.USER_ADMIN_ID);
		if(pathMap!=null){
			List<Map> pathList=(List<Map>)pathMap.get("rows");
			if(pathList!=null){
				for(int i=0;i<pathList.size();i++){
					Map node=pathList.get(i);
					if(DISPLAY_EMS==null&&Integer.valueOf(CommonDefine.TREE.NODE.EMS)
							.equals(node.get("nodeLevel"))){
						DISPLAY_EMS=String.valueOf(node.get("text"));
					}
					if(Integer.valueOf(CommonDefine.TREE.NODE.SUBNET)
							.equals(node.get("nodeLevel"))){
						if(DISPLAY_SUBNET==null){
							DISPLAY_SUBNET=String.valueOf(node.get("text"));
						}else{
							DISPLAY_SUBNET=String.valueOf(node.get("text"))
								+CommonDefine.PathSeparator+DISPLAY_SUBNET;
						}
					}
				}
				List<Map> ownerList=inspectManagerService.getEngineerByNodes(pathList);
				if(ownerList!=null&&!ownerList.isEmpty()){
					OWNER = (String)ownerList.get(0).get("NAME");
					for(int i=1;i<ownerList.size();i++){
						OWNER += "，"+(String)ownerList.get(i).get("NAME");
					}
				}
			}
		}
		Integer roomId = (Integer)neDetail.get("RESOURCE_ROOM_ID");
		if(roomId!=null){
			Map<String, Object> resourceDetail = inspectManagerService.getResourceInfoByRoom(roomId);
			if(resourceDetail!=null&&!resourceDetail.isEmpty()){
				DISPLAY_AREA=""+resourceDetail.get("AREA_NAME")+
						CommonDefine.NameSeparator+
						resourceDetail.get("STATION_NAME");
				Integer areaId = (Integer)resourceDetail.get("AREA_PARENT_ID");
				Integer areaLevel = (Integer)resourceDetail.get("AREA_LEVEL");
				if(areaLevel!=null)areaLevel=areaLevel-1;
				while(areaId!=null&&areaLevel!=null&&
					areaLevel>AreaManagerImpl.AreaDef.LEVEL.ROOT){
					Map<String, Object> areaMap=areaManagerService.getAreaInfo(areaId, areaLevel);
					if(areaMap!=null&&!areaMap.isEmpty()){
						DISPLAY_AREA=""+areaMap.get("AREA_NAME")
							+CommonDefine.PathSeparator+DISPLAY_AREA;
					}else {
						break;
					}
					areaId=(Integer)areaMap.get("AREA_PARENT_ID");
					areaLevel = (Integer)areaMap.get("AREA_LEVEL");
					if(areaLevel!=null)areaLevel=areaLevel-1;
				}
			}
		}
		Map<String,String> resultMap=new HashMap<String, String>();
		resultMap.put("DISPLAY_NE", DISPLAY_NE);
		resultMap.put("PRODUCT_NAME", PRODUCT_NAME);
		resultMap.put("DISPLAY_EMS", DISPLAY_EMS);
		resultMap.put("DISPLAY_SUBNET", DISPLAY_SUBNET);
		resultMap.put("DISPLAY_AREA", DISPLAY_AREA);
		resultMap.put("OWNER", OWNER);
		return resultMap;
	}
	private Map<Integer,Integer> evaluateBase(InspectDetailItemModel template,Map<String,Object> neDetail,File outDataFile) throws CommonException{
		Map<Integer,Integer> ERROR_SUBITEM_LIST = new HashMap<Integer,Integer>();

		//网元连接
		List<InspectDetailItemModel> datailList=new ArrayList<InspectDetailItemModel>();
		Integer exceptionLv=CommonDefine.INSPECT.SEVERITY.NORMAL;
		Integer subItem=CommonDefine.INSPECT.TASK_ITEM.BASE_SUB.CONNECT_STATE;
		String evaluateItemName=CommonDefine.INSPECT.TASK_ITEM.BASE_SUB.valueToString(subItem);
		InspectDetailItemModel m=template.clone();
		m.setEvaluateItemName(evaluateItemName);
		m.setValueDesc("网管侧状态");
		Integer IS_DEL=(Integer)neDetail.get("IS_DEL");
		//通信状态非在线
		if(IS_DEL!=null&&CommonDefine.FALSE!=IS_DEL){
			exceptionLv=CommonDefine.INSPECT.SEVERITY.MAJOR;
			ERROR_SUBITEM_LIST.put(subItem, exceptionLv);
		}
		m.setValue(CommonDefine.INSPECT.IS_DEL.valueToString(IS_DEL));
		m.setExceptionLv(exceptionLv);
		datailList.add(m);
		//网元在线
		exceptionLv=CommonDefine.INSPECT.SEVERITY.NORMAL;
		subItem=CommonDefine.INSPECT.TASK_ITEM.BASE_SUB.COMMUNICATION_STATE;
		evaluateItemName=CommonDefine.INSPECT.TASK_ITEM.BASE_SUB.valueToString(subItem);
		m=template.clone();
		m.setEvaluateItemName(evaluateItemName);
		m.setValueDesc("登陆状态");
		Integer COMMUNICATION_STATE=(Integer)neDetail.get("COMMUNICATION_STATE");
		//通信状态非在线
		if(COMMUNICATION_STATE!=null&&
				CommonDefine.BASE.COMMUNICATION_STATE.AVAILABLE!=COMMUNICATION_STATE){
			exceptionLv=CommonDefine.INSPECT.SEVERITY.MAJOR;
			ERROR_SUBITEM_LIST.put(subItem, exceptionLv);
		}
		m.setValue(CommonDefine.BASE.COMMUNICATION_STATE.valueToString(COMMUNICATION_STATE));
		m.setExceptionLv(exceptionLv);
		datailList.add(m);
		//网元基础数据
		exceptionLv=CommonDefine.INSPECT.SEVERITY.NORMAL;
		subItem=CommonDefine.INSPECT.TASK_ITEM.BASE_SUB.SYNC_STATE;
		evaluateItemName=CommonDefine.INSPECT.TASK_ITEM.BASE_SUB.valueToString(subItem);
		m=template.clone();
		m.setEvaluateItemName(evaluateItemName);
		m.setValueDesc("同步状态");
		//同步状态非已同步
		Integer BASIC_SYNC_STATUS=(Integer)neDetail.get("BASIC_SYNC_STATUS");
		if(BASIC_SYNC_STATUS!=null&&CommonDefine.NE_SYNC_HAD!=BASIC_SYNC_STATUS){
			exceptionLv = CommonDefine.INSPECT.SEVERITY.MINOR;
			ERROR_SUBITEM_LIST.put(subItem, exceptionLv);
		}
		m.setValue(CommonDefine.INSPECT.BASE_SYNC_STATE.valueToString(BASIC_SYNC_STATUS));
		m.setExceptionLv(exceptionLv);
		datailList.add(m);
		writeCsvDataSource(datailList,outDataFile);
		return ERROR_SUBITEM_LIST;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map<Integer,Integer> evaluatePM(InspectDetailItemModel template,Map<String,Object> neDetail,File outDataFile) throws CommonException{
		int userId = CommonDefine.USER_SYSTEM_ID;
		SimpleDateFormat COMMON_FORMAT = CommonUtil.getDateFormatter(CommonDefine.COMMON_FORMAT);		
		Integer neId = (Integer)neDetail.get("BASE_NE_ID");
		Integer emsId = (Integer)neDetail.get("BASE_EMS_CONNECTION_ID");
		
		Map<Integer,Integer> ERROR_SUBITEM_LIST = new HashMap<Integer,Integer>();

		Integer exceptionLv=CommonDefine.INSPECT.SEVERITY.NORMAL;
		Integer subItem=null;
		String evaluateItemName=null;
		InspectDetailItemModel m=null;
		
		List<InspectDetailItemModel> datailList=new ArrayList<InspectDetailItemModel>();
		
		//取上次采集时间
//		String SUCCESS="完成";
		String FAILED="采集失败";
//		String RUNNING="进行中";
		String COLLECT_RESULT = (String)neDetail.get("COLLECT_RESULT");
		Date LAST_COLLECT_TIME = (Date)neDetail.get("LAST_COLLECT_TIME");

		//未采集或采集失败
		if(COLLECT_RESULT==null||LAST_COLLECT_TIME==null||COLLECT_RESULT.startsWith(FAILED)){
			if(COLLECT_RESULT==null||COLLECT_RESULT.isEmpty()){
				COLLECT_RESULT="未采集";
			}
			exceptionLv=CommonDefine.INSPECT.SEVERITY.MAJOR;

			subItem=CommonDefine.INSPECT.TASK_ITEM.PM_SUB.PHYSICAL;
			evaluateItemName=CommonDefine.INSPECT.TASK_ITEM.PM_SUB.valueToString(subItem);
			m=template.clone();
			m.setEvaluateItemName(evaluateItemName);
			m.setExceptionLv(exceptionLv);
			m.setValueDesc("采集状态");
			m.setValue(COLLECT_RESULT);
			datailList.add(m);
			ERROR_SUBITEM_LIST.put(subItem, exceptionLv);
			
			subItem=CommonDefine.INSPECT.TASK_ITEM.PM_SUB.COUNT;
			evaluateItemName=CommonDefine.INSPECT.TASK_ITEM.PM_SUB.valueToString(subItem);
			m=template.clone();
			m.setEvaluateItemName(evaluateItemName);
			m.setExceptionLv(exceptionLv);
			m.setValueDesc("采集状态");
			m.setValue(COLLECT_RESULT);
			datailList.add(m);
			writeCsvDataSource(datailList,outDataFile);
			ERROR_SUBITEM_LIST.put(subItem, exceptionLv);
		}else{
			List<Integer> igException=Arrays.asList(new Integer[]{
					MessageCodeDefine.PM_DONT_SELECT_SDH,
					MessageCodeDefine.PM_DONT_SELECT_WDM});
			//if(COLLECT_RESULT.startsWith(RUNNING)){
				//记录全部巡检完后再检查一次
			//}else/* if(COLLECT_RESULT.startsWith(SUCCESS))*/{
			//}
			//'yyyy-MM-dd HH:mm:ss'
			Map<String, Object> neMap = new HashMap<String, Object>();
			neMap.put("nodeId", neId);
			neMap.put("nodeLevel", CommonDefine.TREE.NODE.NE);
			neMap.put("emsId", emsId);
			List<Map> nodeList = new ArrayList<Map>();
			nodeList.add(neMap);
			
//			Calendar cld=Calendar.getInstance();
//			//设置查询起始时间
//			cld.setTime(CommonUtil.Yesterday(LAST_COLLECT_TIME));
//			cld.set(Calendar.HOUR_OF_DAY, 0);
//			cld.set(Calendar.MINUTE, 0);
//			cld.set(Calendar.SECOND, 0);
			String startTime = COMMON_FORMAT.format(
					CommonUtil.getSpecifiedDay(LAST_COLLECT_TIME,-2,0));
			//设置查询结束时间
//			cld.set(Calendar.HOUR_OF_DAY, 23);
//			cld.set(Calendar.MINUTE, 59);
//			cld.set(Calendar.SECOND, 59);
			String endTime = COMMON_FORMAT.format(
					CommonUtil.getSpecifiedDay(LAST_COLLECT_TIME,-1,0));
			List<String> tpLevel = new ArrayList<String>();
			
			int start = 0;
			int page = 200;
			//sdh
			boolean isSdh=true;
			List<String> stdIndexList = null;
			//物理量/计数值
			//if(CommonDefine.INSPECT.TASK_ITEM.PM_COUNT == inspectItemValue){
				stdIndexList=Arrays.asList(CommonDefine.INSPECT.pmSdhCountStdIndex);
			//}else{
//				stdIndexList.addAll(Arrays.asList(CommonDefine.INSPECT.pmSdhPhysicalStdIndex));
			//}
			boolean uend = true;
			while(uend){
				try{
					datailList.clear();
					int searchTag = performanceManagerService.getHistoryPmData(true,
							isSdh, nodeList, startTime,
							endTime, tpLevel,
							stdIndexList, userId,CommonDefine.PM_SEARCH_TYPE.PM_SEARCH);
					Map pageResult = performanceManagerService.getTempPmDataByPage(
							CommonDefine.PM.PM_TABLE_NAMES.HISTORY_SDH_DATA,
							CommonDefine.TRUE, userId,
							searchTag, start, page);
					List<Map> pageList =(List<Map>)pageResult.get("rows");
					if(pageList==null||pageList.isEmpty()){
						break;
					}
					for(Map pm:pageList){
						subItem=Integer.valueOf(CommonDefine.PM.PM_TYPE.COUNT_VALUE).equals(pm.get("TYPE"))?
								CommonDefine.INSPECT.TASK_ITEM.PM_SUB.COUNT:
								Integer.valueOf(CommonDefine.PM.PM_TYPE.PHYSICAL).equals(pm.get("TYPE"))?
								CommonDefine.INSPECT.TASK_ITEM.PM_SUB.PHYSICAL:null;
						evaluateItemName=CommonDefine.INSPECT.TASK_ITEM.PM_SUB.valueToString(subItem);
						m = pmTableToModel(template,evaluateItemName,pm);
						exceptionLv=m.getExceptionLv();
						if(exceptionLv>CommonDefine.INSPECT.EXCEPTION_LV.NORMAL){
							Integer old=ERROR_SUBITEM_LIST.get(subItem);
							if(old==null||old<exceptionLv)
								ERROR_SUBITEM_LIST.put(subItem, exceptionLv);
						}
						m.setExceptionLv(exceptionLv);
						datailList.add(m);
					}
					
					writeCsvDataSource(datailList,outDataFile);
					start+=page;
					uend=start<(Integer)pageResult.get("total");
				}catch(CommonException e){
					if(!igException.contains(e.getErrorCode()))
							throw e;
					else
						break;
				}
			}
			
			//wdm
			start = 0;
			isSdh=false;
			//物理量/计数值
			//if(CommonDefine.INSPECT.TASK_ITEM.PM_COUNT == inspectItemValue){
				stdIndexList = 
					Arrays.asList(CommonDefine.INSPECT.pmWdmCountStdIndex);
			//}else{
//				stdIndexList.addAll(
//					Arrays.asList(CommonDefine.INSPECT.pmWdmPhysicalStdIndex));
			//}
			uend = true;
			while(uend){
				try{
					datailList.clear();
					int searchTag = performanceManagerService.getHistoryPmData(true,
							isSdh, nodeList, startTime,
							endTime, tpLevel,
							stdIndexList, userId,CommonDefine.PM_SEARCH_TYPE.PM_SEARCH);
					Map pageResult = performanceManagerService.getTempPmDataByPage(
							CommonDefine.PM.PM_TABLE_NAMES.HISTORY_WDM_DATA,
							CommonDefine.TRUE, userId,
							searchTag, start, page);
					List<Map> pageList =(List<Map>)pageResult.get("rows");
					if(pageList==null||pageList.isEmpty()){
						break;
					}
					for(Map pm:pageList){
						if(pm.get("TYPE")==null) continue;
						subItem=Integer.valueOf(CommonDefine.PM.PM_TYPE.COUNT_VALUE).equals(pm.get("TYPE"))?
								CommonDefine.INSPECT.TASK_ITEM.PM_SUB.COUNT:
								Integer.valueOf(CommonDefine.PM.PM_TYPE.PHYSICAL).equals(pm.get("TYPE"))?
								CommonDefine.INSPECT.TASK_ITEM.PM_SUB.PHYSICAL:null;
						evaluateItemName=CommonDefine.INSPECT.TASK_ITEM.PM_SUB.valueToString(subItem);
						m = pmTableToModel(template,evaluateItemName,pm);
						exceptionLv=m.getExceptionLv();
						if(exceptionLv>CommonDefine.INSPECT.EXCEPTION_LV.NORMAL){
							Integer old=ERROR_SUBITEM_LIST.get(subItem);
							if(old==null||old<exceptionLv)
								ERROR_SUBITEM_LIST.put(subItem, exceptionLv);
						}
						datailList.add(m);
					}
					
					writeCsvDataSource(datailList,outDataFile);
					start+=page;
					uend=start<(Integer)pageResult.get("total");
				}catch(CommonException e){
					if(!igException.contains(e.getErrorCode()))
						throw e;
					else{
						break;
					}
				}
			}
		}
		return ERROR_SUBITEM_LIST;
	}
	/**
	 * @param template 数据模板
	 * @param evaluateItemName 评估项
	 * @param pm 单条性能数据
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static InspectDetailItemModel pmTableToModel(InspectDetailItemModel template,String evaluateItemName,Map pm){
		Integer exceptionLv=CommonDefine.INSPECT.SEVERITY.NORMAL;
		Integer EXCEPTION_LV = (Integer)pm.get("EXCEPTION_LV");
		if(EXCEPTION_LV!=null) exceptionLv=EXCEPTION_LV;
		Integer EXCEPTION_COUNT = (Integer)pm.get("EXCEPTION_COUNT");
	
		InspectDetailItemModel m = template.clone();
		m.setEvaluateItemName(evaluateItemName);
		m.setTargetDesc((String)pm.get("DISPLAY_PORT_DESC"));
		m.setValueDesc((String)pm.get("PM_DESCRIPTION"));
		m.setValue((String)pm.get("PM_VALUE"));
		m.setCompareValue((String)pm.get("PM_COMPARE_VALUE"));
		m.setRetrievalTime((Date)pm.get("RETRIEVAL_TIME"));
		
		if(EXCEPTION_COUNT!=null&&EXCEPTION_COUNT>CommonDefine.INSPECT.EXCEPTION_COUNT_CR){
			exceptionLv=CommonDefine.INSPECT.SEVERITY.CRITICAL;
		}
		m.setExceptionLv(exceptionLv);
		return m;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map<Integer,Integer> evaluateAlarm(InspectDetailItemModel template,Map<String,Object> neDetail,File outDataFile) throws CommonException{
		//告警（2M、VC12级别不计）layerRate 5=2M，11=VC12，13=VC3
		Integer neId = (Integer)neDetail.get("BASE_NE_ID");
		
		Map<Integer,Integer> ERROR_SUBITEM_LIST = new HashMap<Integer,Integer>();
		Integer exceptionLv=CommonDefine.INSPECT.SEVERITY.NORMAL;
		Integer subItem=null;
		String evaluateItemName=null;
		
		List<InspectDetailItemModel> datailList=new ArrayList<InspectDetailItemModel>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		//alarmLv filterId statusId isConverge
		//emsGroupId emsId subnetId neId ptpId unitId
		paramMap.put("neId", neId);

		paramMap.put("emsGroupId", "");
		paramMap.put("emsId", "");
		paramMap.put("subnetId", "");
		paramMap.put("unitId", "");
		paramMap.put("ptpId", "");
		paramMap.put("alarmLv", "");
		paramMap.put("filterId", "");
		paramMap.put("statusId", CommonDefine.ALARM_NOT_CLEARED);
		paramMap.put("isConverge", false);
		int start = 0;
		int page = 200;
		boolean uend = true;
		while(uend){
			Map<String, Object> pageResult = alarmManagementService.getCurrentAlarms(paramMap, start, page, CommonDefine.USER_ADMIN_ID);
			List<Map> pageList =(List<Map>)pageResult.get("rows");
			if(pageList==null||pageList.isEmpty()){
				break;
			}
			for(Map alm:pageList){
				Integer LAYER_RATE=(Integer)alm.get("LAYER_RATE");
				Integer IS_CLEAR = (Integer)alm.get("IS_CLEAR");
				if((IS_CLEAR!=null&&IS_CLEAR==CommonDefine.IS_CLEAR_YES)||
					(LAYER_RATE!=null&&
						(LAYER_RATE==CommonDefine.INSPECT.LAYER_RATE._2M
						||LAYER_RATE==CommonDefine.INSPECT.LAYER_RATE._VC12
						||LAYER_RATE==CommonDefine.INSPECT.LAYER_RATE._VC3)))
					continue;//过滤已清除和2M、VC12、VC3告警
				subItem=CommonDefine.INSPECT.TASK_ITEM.ALARM_SUB.ALARM;
				evaluateItemName=CommonDefine.INSPECT.TASK_ITEM.ALARM_SUB.valueToString(subItem);
				InspectDetailItemModel m = alarmTableToModel(template,evaluateItemName,alm);
				exceptionLv=m.getExceptionLv();
				if(exceptionLv>CommonDefine.INSPECT.EXCEPTION_LV.NORMAL){
					Integer old=ERROR_SUBITEM_LIST.get(subItem);
					if(old==null||old<exceptionLv)
						ERROR_SUBITEM_LIST.put(subItem, exceptionLv);
				}
				m.setExceptionLv(exceptionLv);
				datailList.add(m);
			}
			writeCsvDataSource(datailList,outDataFile);
			datailList.clear();
			start+=page;
			uend=start<(Integer)pageResult.get("total");
		}
		return ERROR_SUBITEM_LIST;
	}
	/**
	 * @param template 数据模板
	 * @param evaluateItemName 评估项
	 * @param pm 单条性能数据
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static InspectDetailItemModel alarmTableToModel(InspectDetailItemModel template,String evaluateItemName,Map alm){
		Integer exceptionLv=CommonDefine.INSPECT.SEVERITY.NORMAL;
		Integer IS_CLEAR = (Integer)alm.get("IS_CLEAR");
		if(IS_CLEAR==null||IS_CLEAR!=CommonDefine.IS_CLEAR_YES){
			Integer PERCEIVED_SEVERITY = (Integer)alm.get("PERCEIVED_SEVERITY");
			exceptionLv=CommonDefine.INSPECT.SEVERITY.fromAlarmSeverity(PERCEIVED_SEVERITY);
		}
		InspectDetailItemModel m = template.clone();
		m.setEvaluateItemName(evaluateItemName);
		String SLOT_DISPLAY_NAME=(String)alm.get("SLOT_DISPLAY_NAME");
		String UNIT_NAME=(String)alm.get("UNIT_NAME");
		String PORT_NAME=(String)alm.get("PORT_NAME");
		String CTP_NAME=(String)alm.get("CTP_NAME");
		
		if(SLOT_DISPLAY_NAME!=null&&!SLOT_DISPLAY_NAME.isEmpty()){
			StringBuffer targetDesc=new StringBuffer();
			targetDesc.append(SLOT_DISPLAY_NAME);
			if(UNIT_NAME!=null&&!UNIT_NAME.isEmpty()){
				targetDesc.append(CommonDefine.NameSeparator);
				targetDesc.append(UNIT_NAME);
				if(PORT_NAME!=null&&!PORT_NAME.isEmpty()){
					targetDesc.append(CommonDefine.NameSeparator);
					targetDesc.append(PORT_NAME);
					if(CTP_NAME!=null&&!CTP_NAME.isEmpty()){
						targetDesc.append(CommonDefine.NameSeparator);
						targetDesc.append(CTP_NAME);
					}
				}
			}
			m.setTargetDesc(targetDesc.toString());
		}
//		m.setValueDesc((String)alm.get("NATIVE_PROBABLE_CAUSE"));
		m.setValue((String)alm.get("NATIVE_PROBABLE_CAUSE"));
//		m.setCompareValue((String)alm.get("PM_COMPARE_VALUE"));
		if(alm.get("NE_TIME")!=null
				&&alm.get("NE_TIME") instanceof String
				&&!"".equals(alm.get("NE_TIME"))){
			SimpleDateFormat df = CommonUtil.getDateFormatter(
					CommonDefine.COMMON_FORMAT);
			try {
				m.setRetrievalTime(df.parse((String)alm.get("NE_TIME")));
			} catch (ParseException e) {
			}
		}else if(alm.get("NE_TIME") instanceof Date){
			m.setRetrievalTime((Date)alm.get("NE_TIME"));
		}
		m.setExceptionLv(exceptionLv);
		return m;
	}
	private Map<Integer,Integer> evaluateTime(InspectDetailItemModel template,Map<String,Object> neDetail,File outDataFile) throws CommonException{
		Map<Integer,Integer> ERROR_SUBITEM_LIST = new HashMap<Integer,Integer>();
		//FIXME 评估网元时间
		return ERROR_SUBITEM_LIST;
	}
	@SuppressWarnings({ "rawtypes" })
	private Map<Integer,Integer> evaluateProtect(InspectDetailItemModel template,Map<String,Object> neDetail,File outDataFile) throws CommonException{
		Map<Integer,Integer> ERROR_SUBITEM_LIST = new HashMap<Integer,Integer>();
		
		Integer neId = (Integer)neDetail.get("BASE_NE_ID");
		Integer exceptionLv=CommonDefine.INSPECT.SEVERITY.NORMAL;
		Integer subItem=null;
		String evaluateItemName=null;
//		InspectDetailItemModel m=null;
		
		List<InspectDetailItemModel> datailList=new ArrayList<InspectDetailItemModel>();

		subItem=CommonDefine.INSPECT.TASK_ITEM.PROTECT_SUB.PROTECT;
		evaluateItemName=CommonDefine.INSPECT.TASK_ITEM.PROTECT_SUB.valueToString(subItem);
		
		List<Integer> SCHEMA_STATE = new ArrayList<Integer>();
		Integer category=CommonDefine.INSPECT.PRO_GROUP_CATEGORY.PROTECTION;
		List<Map> protectList=inspectManagerService.getProtectGroups(neId,SCHEMA_STATE);
		if(protectList!=null&&!protectList.isEmpty()){//处于保护状态的网元
			for(Map protect:protectList){
				InspectDetailItemModel temp = protectTableToModel(
						category,template,evaluateItemName,protect);
				exceptionLv=temp.getExceptionLv();
				if(exceptionLv>CommonDefine.INSPECT.EXCEPTION_LV.NORMAL){
					Integer old=ERROR_SUBITEM_LIST.get(subItem);
					if(old==null||old<exceptionLv)
						ERROR_SUBITEM_LIST.put(subItem, exceptionLv);
				}
				Integer pgId=(Integer)protect.get("BASE_PRO_GROUP_ID");
				Map<String, Object> param=new HashMap<String, Object>();
				List<Map<String,Object>> tpList=inspectManagerService.getProtectedList(category,pgId,param);
				for(Map<String,Object> tp:tpList){
					if((tp.get("IS_DEL")!=null&&tp.get("IS_DEL").equals(CommonDefine.TRUE))
						||tp.get("BASE_SDH_CTP_ID")!=null
						||tp.get("BASE_OTN_CTP_ID")!=null)
						continue;
					InspectDetailItemModel m=protectTpTableToModel(temp,tp);
					datailList.add(m);
				}
			}
		}
		protectList.clear();
		category=CommonDefine.INSPECT.PRO_GROUP_CATEGORY.EPROTECTION;
		protectList=inspectManagerService.getEProtectGroups(neId,SCHEMA_STATE);
		if(protectList!=null&&!protectList.isEmpty()){//处于保护状态的网元
			for(Map protect:protectList){
				InspectDetailItemModel temp = protectTableToModel(
						category,template,evaluateItemName,protect);
				exceptionLv=temp.getExceptionLv();
				if(exceptionLv>CommonDefine.INSPECT.EXCEPTION_LV.NORMAL){
					Integer old=ERROR_SUBITEM_LIST.get(subItem);
					if(old==null||old<exceptionLv)
						ERROR_SUBITEM_LIST.put(subItem, exceptionLv);
				}
				Integer pgId=(Integer)protect.get("BASE_E_PRO_GROUP_ID");
				Map<String, Object> param=new HashMap<String, Object>();
				List<Map<String,Object>> tpList=inspectManagerService.getProtectedList(
						category,pgId,param);
				for(Map<String,Object> tp:tpList){
					if(tp.get("TYPE")!=null&&!tp.get("TYPE").equals(
							CommonDefine.INSPECT.PRO_EQUIP_TYPE.PROTECTED))
						continue;
					InspectDetailItemModel m=protectTpTableToModel(temp,tp);
					datailList.add(m);
				}
			}
		}
		protectList.clear();
		category=CommonDefine.INSPECT.PRO_GROUP_CATEGORY.WDMPROTECTION;
		protectList=inspectManagerService.getWDMProtectGroups(neId,SCHEMA_STATE);
		if(protectList!=null&&!protectList.isEmpty()){//处于保护状态的网元
			for(Map protect:protectList){
				InspectDetailItemModel temp = protectTableToModel(
						category,template,evaluateItemName,protect);
				exceptionLv=temp.getExceptionLv();
				if(exceptionLv>CommonDefine.INSPECT.EXCEPTION_LV.NORMAL){
					Integer old=ERROR_SUBITEM_LIST.get(subItem);
					if(old==null||old<exceptionLv)
						ERROR_SUBITEM_LIST.put(subItem, exceptionLv);
				}
				Integer pgId=(Integer)protect.get("BASE_WDM_PRO_GROUP_ID");
				Map<String, Object> param=new HashMap<String, Object>();
				List<Map<String,Object>> tpList=inspectManagerService.getProtectedList(category,pgId,param);
				for(Map<String,Object> tp:tpList){
					InspectDetailItemModel m=protectTpTableToModel(temp,tp);
					datailList.add(m);
				}
			}
		}
		writeCsvDataSource(datailList,outDataFile);
		return ERROR_SUBITEM_LIST;
	}
	/**
	 * @param catetory 1=protect/2=eProtect/3=wdmProtect
	 * @param template 数据模板
	 * @param evaluateItemName 评估项
	 * @param protect 单条保护组数据
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static InspectDetailItemModel protectTableToModel(Integer catetory,InspectDetailItemModel template,String evaluateItemName,Map protect){
		String groupTypeCol=protect.containsKey("E_PROTECTION_GROUP_TYPE")?
				"E_PROTECTION_GROUP_TYPE":"PROTECTION_GROUP_TYPE";
		
		Integer exceptionLv=CommonDefine.INSPECT.SEVERITY.NORMAL;
		
		Integer SCHEMA_STATE = (Integer)protect.get("PROTECTION_SCHEMA_STATE");
		if(SCHEMA_STATE!=null){
			if(CommonDefine.INSPECT.SCHEMA_STATE.UNKNOWN!=SCHEMA_STATE){
				exceptionLv=CommonDefine.INSPECT.SEVERITY.MAJOR;
			}
		}else{
			SCHEMA_STATE = CommonDefine.INSPECT.SCHEMA_STATE.UNKNOWN;
		}

		InspectDetailItemModel m = template.clone();
		m.setEvaluateItemName(CommonDefine.INSPECT.PRO_GROUP_CATEGORY.valueToString(catetory));
		if(protect.get(groupTypeCol) instanceof String){
			m.setValueDesc((String)protect.get(groupTypeCol));
		}else{
			Integer GROUP_TYPE=(Integer)protect.get(groupTypeCol);
			if(GROUP_TYPE!=null){
				m.setValueDesc(CommonDefine.RESOURCE.TRANS_SYS.PRO_GROUP_TYPE.get(GROUP_TYPE));
			}
		}
		m.setValue(CommonDefine.INSPECT.SCHEMA_STATE.valueToString(SCHEMA_STATE));
//		m.setRetrievalTime((Date)protect.get("RETRIEVAL_TIME"));
		m.setExceptionLv(exceptionLv);
		return m;
	}
	private static InspectDetailItemModel protectTpTableToModel(InspectDetailItemModel template,Map<String,Object> tp){
		String targetCol=tp.containsKey("PORT_DESC")?"PORT_DESC":"UNIT_DESC";
		InspectDetailItemModel m = template.clone();
		m.setTargetDesc((String)tp.get(targetCol));
		return m;
	}
	@SuppressWarnings("rawtypes")
	private Map<Integer,Integer> evaluateClock(InspectDetailItemModel template,Map<String,Object> neDetail,File outDataFile) throws CommonException{
		Map<Integer,Integer> ERROR_SUBITEM_LIST = new HashMap<Integer,Integer>();
		
		Integer neId = (Integer)neDetail.get("BASE_NE_ID");
		Integer exceptionLv=CommonDefine.INSPECT.SEVERITY.NORMAL;
		Integer subItem=null;
		String evaluateItemName=null;
		InspectDetailItemModel m=null;
		
		List<InspectDetailItemModel> datailList=new ArrayList<InspectDetailItemModel>();
		
		Map currentMap=null;
		List<Map> clockList=inspectManagerService.getClockSources(neId);

		subItem=CommonDefine.INSPECT.TASK_ITEM.CLOCK_SUB.SETTING;
		evaluateItemName=CommonDefine.INSPECT.TASK_ITEM.CLOCK_SUB.valueToString(subItem);
		if(clockList==null||clockList.isEmpty()){//没有时钟设置
			exceptionLv = CommonDefine.INSPECT.SEVERITY.MAJOR;
			m=template.clone();
			m.setEvaluateItemName(evaluateItemName);
			m.setValue("没有时钟设置");
			m.setExceptionLv(exceptionLv);
			datailList.add(m);
			ERROR_SUBITEM_LIST.put(subItem, exceptionLv);
		}else{
			exceptionLv=CommonDefine.INSPECT.SEVERITY.NORMAL;
			for(Map clock:clockList){
				Integer IS_CURRENT=(Integer)clock.get("IS_CURRENT");
				if(currentMap==null&&IS_CURRENT!=null&&0==IS_CURRENT){
					currentMap=clock;
				}
				String VALUE=String.valueOf(clock.get("DISPLAY_NAME"));
				m=template.clone();
				m.setEvaluateItemName(evaluateItemName);
				m.setValue(VALUE);
				m.setExceptionLv(exceptionLv);
				datailList.add(m);
			}
		}
		if(currentMap==null){
			subItem=CommonDefine.INSPECT.TASK_ITEM.CLOCK_SUB.CURRENT;
			evaluateItemName=CommonDefine.INSPECT.TASK_ITEM.CLOCK_SUB.valueToString(subItem);
			exceptionLv = CommonDefine.INSPECT.SEVERITY.MAJOR;
			m=template.clone();
			m.setEvaluateItemName(evaluateItemName);
			m.setValue("没有当前时钟");
			m.setExceptionLv(exceptionLv);
			datailList.add(m);
			ERROR_SUBITEM_LIST.put(subItem, exceptionLv);
		}else{
			subItem=CommonDefine.INSPECT.TASK_ITEM.CLOCK_SUB.CURRENT;
			evaluateItemName=CommonDefine.INSPECT.TASK_ITEM.CLOCK_SUB.valueToString(subItem);
			exceptionLv = CommonDefine.INSPECT.SEVERITY.NORMAL;
			
			String VALUE=null;
			VALUE=String.valueOf(currentMap.get("DISPLAY_NAME"));
			m=template.clone();
			m.setEvaluateItemName(evaluateItemName);
			m.setValue(VALUE);
			m.setExceptionLv(exceptionLv);
			datailList.add(m);
			
			exceptionLv = CommonDefine.INSPECT.SEVERITY.NORMAL;
			subItem=CommonDefine.INSPECT.TASK_ITEM.CLOCK_SUB.QUALITY;
			evaluateItemName=CommonDefine.INSPECT.TASK_ITEM.CLOCK_SUB.valueToString(subItem);
			
			Integer QUALITY=CommonDefine.INSPECT.CLOCK_QUALITY.LEVELUNKNOWN;
			if(currentMap.get("QUALITY")!=null)
				QUALITY=Integer.valueOf(String.valueOf(currentMap.get("QUALITY")));
			VALUE=CommonDefine.INSPECT.CLOCK_QUALITY.valueToString(QUALITY);
			if(CommonDefine.INSPECT.CLOCK_QUALITY.NOTFORSYNCLK==QUALITY){
				//当前时钟质量低
				exceptionLv = CommonDefine.INSPECT.SEVERITY.MINOR;
				ERROR_SUBITEM_LIST.put(subItem, exceptionLv);
			}
			m=template.clone();
			m.setEvaluateItemName(evaluateItemName);
			m.setValue(VALUE);
			m.setExceptionLv(exceptionLv);
			datailList.add(m);
		}
		writeCsvDataSource(datailList,outDataFile);
		return ERROR_SUBITEM_LIST;
	}
}
