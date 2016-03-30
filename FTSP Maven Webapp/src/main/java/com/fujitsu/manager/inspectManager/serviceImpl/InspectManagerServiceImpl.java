package com.fujitsu.manager.inspectManager.serviceImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.IService.ICommonManagerService;
import com.fujitsu.IService.IExportExcel;
import com.fujitsu.IService.IQuartzManagerService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.dao.mysql.InspectManagerMapper;
import com.fujitsu.manager.inspectManager.service.InspectManagerService;
import com.fujitsu.util.ExportExcelUtil;
import com.fujitsu.util.ZipUtil;

@Service
@Transactional(rollbackFor=Exception.class)
public class InspectManagerServiceImpl extends InspectManagerService{

	@Resource
	private InspectManagerMapper inspectManagerMapper;
	@Resource
	public ICommonManagerService commonManagerService;
	@Resource
	public IQuartzManagerService quartzManagerService;
	
	/** 
	 * 包机人查询:查询所有包机人的信息
	 * 返回的Map中分别为List<Map>类型的包机人信息、String类型的包机人信息条数
	 * 
	 * @param startNumber pageSize - 分页参数
	 * @return Map<String,Object>
	 * @throws CommonException
	 */
	public Map<String,Object> getInspectEngineerList(int start, int limit) throws CommonException{

		Map map=new HashMap();
		Map returnMap=new HashMap();
		List<Map> enigneerList = new ArrayList<Map>();
		
		//int total=inspectManagerMapper.countEngineerList(map);
		
		map.put("start", start);
		map.put("limit", limit);
		
		
		enigneerList=inspectManagerMapper.selectEngineerList(map);
		
		int total=inspectManagerMapper.countEngineerList(map);
		
		returnMap.put("rows", enigneerList);
		returnMap.put("total", total);
	
		return returnMap;
	}
	
	/**
	 * 包机人所属区域信息加载
	 * 返回的List<Map>是需加载的区域信息
	 * 
	 * @param int level - 区域级别
	 * @return List<Map>
	 * @throws CommonException
	 */
	public List<Map> getAreaList (int level) throws CommonException{
		
		List<Map> areaList = new ArrayList<Map>();
		
		areaList=inspectManagerMapper.selectAreaList(level);

		return areaList;
		
	}
	
	/**
	 * 判断包机人工号是否重复
	 * 返回是否存在信息：true 存在/false 不存在
	 * 
	 * @param JobNo - 包机人工号
	 * @param engineerId - 包机人id
	 * @return Boolean true/false
	 * @throws CommonException
	 */
	public Boolean checkJobNoExist (Map map) throws CommonException{
		Boolean exit = true;
		
		List<Map> returnList = inspectManagerMapper.getJobNoExitList(map);
		if(returnList.size() == 0){
			exit = false;
		}
		return exit;
	}
	
	/**
	 * 新增包机人
	 * 返回的String,新增包机人是否成功
	 * 
	 * @param Map - 包机人信息
	 * @return String
	 * @throws CommonException
	 */
	public void addInspectEngineer (Map map, List<String> inspectEquipList) throws CommonException{
		
		List<Map> equipList = new ArrayList<Map>();
		//保存包机人基本信息
		inspectManagerMapper.storeInspectEngineer(map); 
		
		String[] equipInfo ;
		Map<String, Object> InspectEquipInfo ;
		//判断巡检设备是否为空
		String equip = inspectEquipList.get(0);
		System.out.println("第一个:"+equip);
		System.out.println("equipSize:"+inspectEquipList.size());
		if(!(inspectEquipList.size() ==1 && equip == "")){
			for(int i = 0; i<inspectEquipList.size();i++){
				equipInfo = inspectEquipList.get(i).split("_");
				// 需要保存的包机人巡检设备信息整合在map中
				InspectEquipInfo = new HashMap<String, Object>();
				
				InspectEquipInfo.put("inspectEngineerInfoId", null);
				InspectEquipInfo.put("inspectEngineerId", map.get("engineerId"));
				InspectEquipInfo.put("equipType", equipInfo[0]);
				InspectEquipInfo.put("equipId", equipInfo[1]);
				
				equipList.add(InspectEquipInfo);
			}
			//保存包机人巡检设备信息
			inspectManagerMapper.storeInspectEquip(equipList);
		}
	}
	
	/**
	 * 修改包机人/巡检任务页面初始化
	 * 返回的List<Map>是巡检设备列表
	 * 
	 * @param int id - 包机人ID/巡检任务ID
	 * @param int flag - 标志信息：1包机人/2巡检任务
	 * @return List<Map>
	 * @throws CommonException
	 */
	public List<Map> getInspectEquipList (int id, int flag) throws CommonException{
		
		List<Map> data = new ArrayList<Map>();
		
		if(flag == CommonDefine.INSPECT_ENGINEER){
			data = inspectManagerMapper.getInspectEquipList(id);
		}else if(flag == CommonDefine.INSPECT_TASK){
			data = inspectManagerMapper.getInspectTaskInfo(id);
		}
		
		//组装巡检设备名，用于前台显示
		for(Map<String,Object> row:data){
			//组装设备名，用于前台显示
			Map<String, Object> node = new HashMap<String, Object>();
			//节点列表信息
			List nodeList = new ArrayList();
			if(row.get("TARGET_TYPE")==null||row.get("TARGET_ID")==null){
				continue;
			}
			int targetType = Integer.valueOf(String.valueOf(row.get("TARGET_TYPE"))).intValue();
			int targetId = Integer.valueOf(String.valueOf(row.get("TARGET_ID"))).intValue();
			
			Map<String, Object> equipMap = new HashMap<String, Object>();
			
			equipMap.put("TARGET_TYPE",targetType);
			equipMap.put("TARGET_ID",targetId);
			
			if(targetType>CommonDefine.TREE.NODE.LEAFMAX||
				targetType<CommonDefine.TREE.NODE.ROOT){
				continue;
			}
			Map nodeInfo = new HashMap();
			node = commonManagerService.treeGetNodesByKey(null,targetId,targetType,0,0,true,0,0,CommonDefine.USER_ADMIN_ID);
			nodeList = (ArrayList)node.get("rows");
			String displayName = "";
			for(int j = nodeList.size()-2; j >= 0; j--){
				nodeInfo = (Map)nodeList.get(j);
				displayName = displayName+ CommonDefine.NameSeparator + String.valueOf(nodeInfo.get("text"));	
			}
			displayName=displayName.replaceFirst(""+CommonDefine.NameSeparator, "");
			row.put("DISPLAY_NAME",displayName);
		}
		return data;
	}
	
	/**
	 * 修改包机人页面初始化
	 * 返回的Map是包机人基本信息
	 * 
	 * @param int engineerId - 包机人ID
	 * @return Map<String,Object>
	 * @throws CommonException
	 */
	public Map<String,Object> getInspectEngineerInfo (int engineerId) throws CommonException{
		Map returnMap=new HashMap();
		
		List<Map> data = inspectManagerMapper.getInspectEngineerInfo(engineerId);
		
		if(data.size() != 0){
			
			returnMap.put("engineerName", data.get(0).get("NAME"));
			returnMap.put("JobNo", data.get(0).get("JOB_NO"));
			returnMap.put("telephone", data.get(0).get("TELEPHONE"));
			
			if(data.get(0).get("RESOURCE_AREA_ID") != null){
				int resourceId = Integer.valueOf(String.valueOf(data.get(0).get("RESOURCE_AREA_ID"))).intValue();
				List<Map> engineerAreaInfo = inspectManagerMapper.getResourceAreaInfo(resourceId);
				int areaLevel = Integer.valueOf(String.valueOf(engineerAreaInfo.get(0).get("AREA_LEVEL"))).intValue();
				if(areaLevel == 1){
					//省级
					List<Map> firstLevelArea = inspectManagerMapper.getResourceAreaInfo(resourceId);
					returnMap.put("firstLevelCombo", firstLevelArea.get(0).get("AREA_NAME"));
					returnMap.put("firstLevelComboId", firstLevelArea.get(0).get("RESOURCE_AREA_ID"));
					returnMap.put("secondLevelCombo", "");
					returnMap.put("secondLevelComboId", "");
					returnMap.put("thirdLevelCombo", "");
					returnMap.put("thirdLevelComboId", "");
				}else if(areaLevel == 2){
					//市级
					List<Map> secondLevelArea = inspectManagerMapper.getResourceAreaInfo(resourceId);
					int firstLevelId = Integer.valueOf(String.valueOf(secondLevelArea.get(0).get("AREA_PARENT_ID"))).intValue();
					returnMap.put("secondLevelCombo", secondLevelArea.get(0).get("AREA_NAME"));
					returnMap.put("secondLevelComboId", secondLevelArea.get(0).get("RESOURCE_AREA_ID"));
					//省级
					List<Map> firstLevelArea = inspectManagerMapper.getResourceAreaInfo(firstLevelId);
					returnMap.put("firstLevelCombo", firstLevelArea.get(0).get("AREA_NAME"));
					returnMap.put("firstLevelComboId", firstLevelArea.get(0).get("RESOURCE_AREA_ID"));
					
					returnMap.put("thirdLevelCombo", "");
					returnMap.put("thirdLevelComboId", "");
				}else if(areaLevel == 3){
					//县级
					List<Map> thirdLevelArea = inspectManagerMapper.getResourceAreaInfo(resourceId);
					int secondLevelId = Integer.valueOf(String.valueOf(thirdLevelArea.get(0).get("AREA_PARENT_ID"))).intValue();
					returnMap.put("thirdLevelCombo", thirdLevelArea.get(0).get("AREA_NAME"));
					returnMap.put("thirdLevelComboId", resourceId);
					//市级
					List<Map> secondLevelArea = inspectManagerMapper.getResourceAreaInfo(secondLevelId);
					int firstLevelId = Integer.valueOf(String.valueOf(secondLevelArea.get(0).get("AREA_PARENT_ID"))).intValue();
					returnMap.put("secondLevelCombo", secondLevelArea.get(0).get("AREA_NAME"));
					returnMap.put("secondLevelComboId", secondLevelArea.get(0).get("RESOURCE_AREA_ID"));
					//省级
					List<Map> firstLevelArea = inspectManagerMapper.getResourceAreaInfo(firstLevelId);
					returnMap.put("firstLevelCombo", firstLevelArea.get(0).get("AREA_NAME"));
					returnMap.put("firstLevelComboId", firstLevelArea.get(0).get("RESOURCE_AREA_ID"));
				}
			}

			returnMap.put("department", data.get(0).get("OFFICE"));
			returnMap.put("role", data.get(0).get("ROLE"));
			returnMap.put("note", data.get(0).get("NOTE"));
		}
		return returnMap;
		
	}
	
	/**
	 * 修改包机人
	 * 返回空,修改包机人是否成功
	 * 
	 * @param Map - 包机人信息
	 * @return void
	 * @throws CommonException
	 */
	public void updateInspectEngineer (Map map, List<String> inspectEquipList,List<String> inspectEquipNameList) throws CommonException{
		
        List<Map> equipList = new ArrayList<Map>();
        List<Integer> engineerIdList = new ArrayList<Integer>();
        Map<String, Object> engineerIdMap = new HashMap<String, Object>();  
        		
		inspectManagerMapper.updateInspectEngineer(map); 
		
		int engineerId = Integer.valueOf(String.valueOf(map.get("engineerId"))).intValue();
		
		engineerIdList.add(engineerId);
		engineerIdMap.put("engineerIdList", engineerIdList);
		//删除
		inspectManagerMapper.deleteInspectEngineerEquip(engineerIdMap);
		
		String[] equipInfo ;
		Map<String, Object> InspectEquipInfo ;
		//判断巡检设备是否为空
		String equip = inspectEquipList.get(0);
		System.out.println("第一个:"+equip);
		System.out.println("equipSize:"+inspectEquipList.size());
		if(!(inspectEquipList.size() ==1 && equip == "")){
		if(inspectEquipList.size() == inspectEquipNameList.size()){
			
			for(int i = 0; i<inspectEquipList.size();i++){
				equipInfo = inspectEquipList.get(i).split("_");
				// 需要保存的包机人信息整合在map中
				InspectEquipInfo = new HashMap<String, Object>();
				
				InspectEquipInfo.put("inspectEngineerInfoId", null);
				InspectEquipInfo.put("inspectEngineerId", map.get("engineerId"));
				InspectEquipInfo.put("equipType", equipInfo[0]);
				InspectEquipInfo.put("equipId", equipInfo[1]);
				InspectEquipInfo.put("equipDisplayName", inspectEquipNameList.get(i)); 
				
				equipList.add(InspectEquipInfo);
			}
			
		}
		inspectManagerMapper.storeInspectEquip(equipList);
		}
		
	}
	
	/**
	 * 删除包机人
	 * 返回删除包机人是否成功信息
	 * 
	 * @param int engineerId - 区域级别
	 * @return void
	 * @throws CommonException
	 */
	public void deleteInspectEngineer (List<Integer> engineerIdList) throws CommonException{
		
		Map<String, Object> map = new HashMap<String, Object>();  
	    map.put("engineerIdList", engineerIdList); 
		
		inspectManagerMapper.deleteInspectEngineerEquip(map);
		inspectManagerMapper.deleteInspectEngineer(map);

	}
	
	/**
	 * 导出包机人
	 * 返回导出包机人是否成功信息
	 * 
	 * @param List<Integer> engineerIdList - 包机人Id列表
	 * @return void
	 * @throws CommonException
	 */
	public CommonResult exportInspectEngineer (List<Integer> engineerIdList) throws CommonException{
		
	
		CommonResult result = new CommonResult();
		Map<String, Object> map = new HashMap<String, Object>();  
	    map.put("engineerIdList", engineerIdList);
		
		List<Map> engineerList = inspectManagerMapper.selectEngineerListByIdList(map);
		
		List<Map> exportList = new ArrayList<Map>();
		for(int i = 0;i<engineerList.size();i++){
			map = new HashMap<String, Object>();
			map.put("name", engineerList.get(i).get("NAME"));
			map.put("job_no", engineerList.get(i).get("JOB_NO"));
			map.put("telephone", engineerList.get(i).get("TELEPHONE"));
			map.put("area", engineerList.get(i).get("AREA_NAME"));
			map.put("department", engineerList.get(i).get("OFFICE"));
			map.put("role", engineerList.get(i).get("ROLE"));
			map.put("note", engineerList.get(i).get("NOTE"));
			
			exportList.add(map);
		}

	    SimpleDateFormat date=new SimpleDateFormat("yyyyMMdd");
	    
	    Calendar cal=Calendar.getInstance();
	    
	    //获取当前时间
	    long currentDate = cal.getTimeInMillis();
	    Date dateTo=new Date(currentDate);
	    String currentTime = date.format(dateTo);
	    System.out.println("当前时间："+currentTime);
	    
	    String fileName = "包机人_" + currentTime;
		
		IExportExcel ex = new ExportExcelUtil(CommonDefine.PATH_ROOT+CommonDefine.EXCEL.TEMP_DIR,fileName);
		String destination=ex.writeExcel(exportList, CommonDefine.EXCEL.INSPECT_ENGINEER_EXPORT,false);
		if(destination != null){
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(destination);
		}else{
			result.setReturnResult(CommonDefine.FAILED);
		}
		
		return result;
		
	}
	
/*-------------------------------------巡检报告--------------------------------------------*/
	
	/**
	 * 巡检报告页面初始化，筛选时间数据获取
	 * 返回的List<Map>是巡检报告Combobox时间数据
	 * 
	 * @return List<Map>
	 * @throws CommonException
	 */
	public List<Map> getDateLimitList () throws CommonException{
		Map paraMap = new HashMap();
		List<Map> returnList = new ArrayList<Map>();
		List<Map> yearList = new ArrayList<Map>();
		
		paraMap.put("value", 1);
		paraMap.put("name", CommonDefine.ONE_MONTH);
		returnList.add(paraMap);
		
		paraMap = new HashMap();
		paraMap.put("value", 2);
		paraMap.put("name", CommonDefine.SIX_MONTH);
		returnList.add(paraMap);
		
		paraMap = new HashMap();
		paraMap.put("value", 3);
		paraMap.put("name", CommonDefine.ONE_YEAR);
		returnList.add(paraMap);
		
		yearList = inspectManagerMapper.selectYearListFromReport();
		for(int i=0;i<yearList.size(); i++){
			if(yearList.get(i)==null) continue;
			paraMap = new HashMap();
			paraMap.put("value", i+4);
			paraMap.put("name", yearList.get(i).get("year"));
			System.out.println(yearList.get(i).get("year"));
			returnList.add(paraMap);
		}
		
		paraMap = new HashMap();
		paraMap.put("value", 4+yearList.size());
		paraMap.put("name", CommonDefine.ALL);
		returnList.add(paraMap);
		
		return returnList;
	}
	
	/**
	 * 巡检报告页面初始化:查询用户有权限查看的所有巡检报告
	 * 返回的Map中分别为List<Map>类型的包机人信息、String类型的包机人信息条数
	 * 
	 * @param Map - 当前用户Id,分页参数,巡检时间，任务名称，创建人
	 * @return Map<String,Object>
	 * @throws CommonException
	 */
	public Map<String,Object> getInspectReportList (Map map) throws CommonException{
		
		Map paraMap = new HashMap();
		Map returnMap = new HashMap();
		List<Map> groupList = new ArrayList<Map>();
		List<Map> inspectReportList = new ArrayList<Map>();
		List<String> groupIdList = new ArrayList<String>();
		String groupId;
		String inspectTime = null;
		String currentTimeTo = null;
		String inspectTimeFrom = null;
		long fromDate;
		Date dateFrom=new Date();
		Date dateTo=new Date();
		dateFrom = null;
		dateTo  = null;
		
		int userId = Integer.valueOf(String.valueOf(map.get("userId"))).intValue();
		paraMap.put("loginUser", String.valueOf(map.get("userId")));
		
		if(userId == -1){
			paraMap.put("groupIdList", null);
			
		}else{
			groupList = inspectManagerMapper.getUserGroupId(userId);
			
			if(groupList.size()!=0){
				for(int i=0;i<groupList.size();i++){
					groupId = groupList.get(i).get("SYS_USER_GROUP_ID").toString();
					System.out.println(groupId);
					groupIdList.add("%,"+groupId+",%");
				}
			}else{
				groupIdList.add("%,-1,%");
			}
			paraMap.put("groupIdList",groupIdList);
		}
		
		try{
		//巡检时间转换
	    SimpleDateFormat date=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    
	    Calendar cal=Calendar.getInstance();
	    
	    //获取当前时间
	    long currentDate = cal.getTimeInMillis();
	    dateTo=new Date(currentDate);
	    currentTimeTo = date.format(dateTo);
	    String currentYear = currentTimeTo.substring(0, 4);
	    System.out.println("当前时间："+currentYear);

	    if(map.get("inspectTime") != null){
	    	
	    	inspectTime = String.valueOf(map.get("inspectTime"));
		    //查询时间判断
			if(inspectTime.equals(CommonDefine.ALL)){
				
			}else if(inspectTime.equals(CommonDefine.ONE_MONTH)){
				cal.add(Calendar.MONTH, -1);    //得到前一个月 
				fromDate = cal.getTimeInMillis();
			    dateFrom=new Date(fromDate);
			    inspectTimeFrom = date.format(dateFrom);
			    System.out.println("从。。时间："+inspectTimeFrom);
				
			}else if(inspectTime.equals(CommonDefine.SIX_MONTH)){
				cal.add(Calendar.MONTH, -6);    //得到前六个月
				fromDate = cal.getTimeInMillis();
			    dateFrom=new Date(fromDate);
			    inspectTimeFrom = date.format(dateFrom);
			    System.out.println("从。。时间："+inspectTimeFrom);
				
			}else if(inspectTime.equals(CommonDefine.ONE_YEAR)){
				cal.add(Calendar.YEAR, -1);    //得到前一年
				fromDate = cal.getTimeInMillis();
			    dateFrom=new Date(fromDate);
			    inspectTimeFrom = date.format(dateFrom);
			    System.out.println("从。。时间："+inspectTimeFrom);
				
			}else if(inspectTime.equals(currentYear)){
				inspectTimeFrom =  inspectTime + "-01-01 00:00:00";  //本年度
				System.out.println("从。。时间："+inspectTimeFrom);
				dateFrom = date.parse(inspectTimeFrom);
			    
			}else{
				inspectTimeFrom = inspectTime + "-01-01 00:00:00";
				System.out.println("从。。时间："+inspectTimeFrom);
				dateFrom = date.parse(inspectTimeFrom);
				String inspectTimeTo = inspectTime + "-12-31 59:59:59";
				System.out.println("到。。时间："+inspectTimeTo);
				dateTo = date.parse(inspectTimeTo);
			}
	    }
		
		paraMap.put("inspectTimeFrom", dateFrom);
		paraMap.put("currentTimeTo", dateTo);
		paraMap.put("taskName", map.get("taskName"));
		paraMap.put("inspector", map.get("inspector"));
		paraMap.put("start", map.get("start"));
		paraMap.put("limit", map.get("limit"));
		
		inspectReportList = inspectManagerMapper.selectReportList(paraMap);
		
		int total=inspectManagerMapper.countReportList(paraMap);
		
		System.out.println("列表个数："+inspectReportList.size());
		List<Map> returnList = new ArrayList();
		
		String createPerson;
		int personId;
		Map personInfo;
		//替换报告生成时间格式用于前台显示
		for(int i=0;i<inspectReportList.size(); i++){
		    Map timeParse = inspectReportList.get(i);
		    currentTimeTo = date.format(timeParse.get("CREATE_TIME"));
		    timeParse.put("CREATE_TIME", currentTimeTo);
		    
		    createPerson = String.valueOf(timeParse.get("CREATE_USER")).toString();
		    personId = Integer.valueOf(createPerson).intValue();
		    personInfo = new HashMap();
		    personInfo = inspectManagerMapper.getUserInfo(personId);
		    
		    timeParse.put("CREATE_USER", personInfo.get("USER_NAME"));
		    
		    returnList.add(timeParse);
		}

		returnMap.put("rows", returnList);
		returnMap.put("total", total);

		}catch(ParseException e){
			e.printStackTrace();
		}
		return returnMap;
	}
	
	/**
	 * 删除巡检报告
	 * 返回删除巡检报告是否成功信息
	 * 
	 * @param List<Integer> reportIdList - 巡检报告Id列表
	 * @return void
	 * @throws CommonException
	 */
	public void deleteInspectReport (List<Integer> reportIdList) throws CommonException{
		
		Map<String, Object> map = new HashMap<String, Object>();  
	    map.put("reportIdList", reportIdList); 
		
		inspectManagerMapper.deleteInspectReport(map);
		
		
	}
	
    /*-------------------------------------巡检任务--------------------------------------------*/
	
	/**
	 * 巡检任务查询:查询所有巡检任务
	 * 返回的Map中分别为List<Map>类型的巡检任务信息、String类型的巡检任务信息条数
	 * 
	 * @param start limit - 分页参数
	 * @return Map<String,Object>
	 * @throws CommonException
	 */
	public Map<String,Object> getInspectTaskList (int start, int limit) throws CommonException{
		
		Map map=new HashMap();
		Map returnMap=new HashMap();
		List<Map> taskList = new ArrayList<Map>();
		List<Map> returnList = new ArrayList<Map>();
		String nextTime = null;
		String lastTime = null;
		int taskStatus;
		int actionStatus = 0;
		
		//巡检时间转换
		//Date dt=new Date();
	    SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    
		map.put("start", start);
		map.put("limit", limit);
		map.put("taskType", CommonDefine.QUARTZ.JOB_INSPECT);
		map.put("taskStatus", CommonDefine.QUARTZ.JOB_DELETE);
		
		int total=inspectManagerMapper.countTaskList(map);
		
		taskList=inspectManagerMapper.selectTaskList(map);
		
		
		//替换时间格式用于前台显示
		for(int i=0;i<taskList.size(); i++){
		    Map task = taskList.get(i);
			//Map task = taskList.get(2);
		   //替换时间格式用于前台显示
		    nextTime = time.format(task.get("NEXT_TIME"));
		    task.put("NEXT_TIME", nextTime);		    
		    System.out.println("下次执行时间："+nextTime);
		    if(task.get("END_TIME") != null){
			    lastTime = time.format(task.get("END_TIME"));
			    task.put("END_TIME", lastTime);		    
			    System.out.println("上次执行时间："+lastTime);
		    }
		   //替换任务状态用于前台显示
		    taskStatus = (Integer)task.get("TASK_STATUS");
		    task.put("TASK_STATUS", CommonDefine.QUARTZ.TASK.STATUS.valueToString(taskStatus));
		    //if(taskStatus == CommonDefine.START_UP){
		    //替换执行状态用于前台显示
		    if(task.get("RESULT") != null){
		    	actionStatus = (Integer)task.get("RESULT");
		    	task.put("RESULT", CommonDefine.QUARTZ.TASK.ACTION_STATUS.valueToString(actionStatus));
		    }else{
		    	task.put("RESULT", "");
		    }
		    
		    returnList.add(task);
		}
		
		returnMap.put("rows", returnList);
		returnMap.put("total", total);
		//returnMap.put("total", returnList.size());
		
				
		return returnMap;
		
		
	}
	
	/**
	 * 获取操作权限组列表:查询所有操作权限组的信息
	 * 返回的Map中分别为List<Map>类型的用户组信息、String类型的用户组条数
	 * 
	 * @param start limit - 分页参数
	 * @return Map<String,Object>
	 * @throws CommonException
	 */
	public Map<String,Object> getPrivilegeList () throws CommonException{
		Map returnMap=new HashMap();
		
		List<Map> returnList = inspectManagerMapper.getPrivilegeList();
		
		returnMap.put("rows", returnList);
		returnMap.put("total", returnList.size());
		return returnMap;
		
	}
	
	/**
	 * 获取当前登录用户所在组ID
	 * 返回的Map中是登录用户所在分组的Id
	 * 
	 * @param userId - 当前登录用户的Id
	 * @return Map<String,Object>
	 * @throws CommonException
	 */
	public Map<String,Object> getCurrentUserGroup (int userId) throws CommonException{
		
        Map returnMap=new HashMap();
		
		List<Map> returnList = inspectManagerMapper.getCurrentUserGroup(userId);
		
		//returnMap.put("groupId", returnList.get(0).get("SYS_USER_GROUP_ID"));
		returnMap.put("checkedGroupList", returnList);
		
		return returnMap;
		
	}
	
	/**
	 * 判断巡检任务名是否重复
	 * 返回是否存在信息：true 存在/false 不存在
	 * 
	 * @param taskName - 巡检任务名
	 * @return Boolean true/false
	 * @throws CommonException
	 */
	public Boolean checkTaskNameExist (Map map) throws CommonException{
		Boolean exit = true;
		
		List<Map> returnList = inspectManagerMapper.getInspectTaskExitList(map);
		if(returnList.size() == 0){
			exit = false;
		}
		return exit;
	}
	
	/**
	 * 新增巡检任务
	 * 返回的String,新增巡检任务是否成功
	 * 
	 * @param Map - 巡检任务信息
	 * @return String
	 * @throws CommonException
	 */
	public void addInspectTask (Map map,  List<String> inspectEquipList, List<String> inspectEquipNameList) throws CommonException{
		
		//保存任务基本信息
		inspectManagerMapper.storeInspectTask(map);
		String equip = inspectEquipList.get(0);
		System.out.println("第一个:"+equip);
		System.out.println("equipSize:"+inspectEquipList.size());
		if(!(inspectEquipList.size() ==1 && equip == "")){
		//保存巡检设备相关信息
		List<Map> equipList = new ArrayList<Map>();
		String[] equipInfo ;
		Map<String, Object> InspectEquipInfo ;
		if(inspectEquipList.size() == inspectEquipNameList.size()){
			for(int i = 0; i<inspectEquipList.size();i++){
				equipInfo = inspectEquipList.get(i).split("_");
				// 需要保存的巡检设备信息整合在map中
				InspectEquipInfo = new HashMap<String, Object>();
				
				InspectEquipInfo.put("taskInfoId", null);
				InspectEquipInfo.put("taskId", map.get("taskId"));
				InspectEquipInfo.put("equipType", equipInfo[0]);
				InspectEquipInfo.put("equipId", equipInfo[1]);
				InspectEquipInfo.put("equipDisplayName", inspectEquipNameList.get(i));
				InspectEquipInfo.put("isSuccess", CommonDefine.FALSE);
				InspectEquipInfo.put("isComplete", CommonDefine.FALSE);
				
				equipList.add(InspectEquipInfo);
			}
		}
		inspectManagerMapper.storeTaskInfo(equipList);
		}
		
		//操作权限组保存
		Map taskParamMap=new HashMap();
		List<Map> taskParamList = new ArrayList<Map>();
		List<String> privilegeList = new ArrayList<String>();
		privilegeList = (ArrayList<String>)map.get("privilegeList");
		String privilegeListString = null;
		for(int i = 0; i<privilegeList.size();i++){
			if(i==0){
				privilegeListString = privilegeList.get(i);
			}else{
				privilegeListString = privilegeListString + "," +  privilegeList.get(i);
			}
		}
		taskParamMap.put("taskParamId",null);
		taskParamMap.put("taskId",map.get("taskId"));
		taskParamMap.put("paramName",CommonDefine.PRIVILEGE);
		taskParamMap.put("paramValue",privilegeListString);
		taskParamList.add(taskParamMap);
		//巡检项目保存
		taskParamMap=new HashMap();
		List<String> inspectItemList = new ArrayList<String>();
		inspectItemList = (ArrayList<String>)map.get("inspectItemList");
		String inspectItemListString = null;
		for(int i = 0; i<inspectItemList.size();i++){
			if(i==0){
				inspectItemListString = String.valueOf(inspectItemList.get(i));
			}else{
				inspectItemListString = inspectItemListString + "," +  inspectItemList.get(i);
			}
		}
		taskParamMap.put("taskParamId",null);
		taskParamMap.put("taskId",map.get("taskId"));
		taskParamMap.put("paramName",CommonDefine.INSPECT_ITEM);
		taskParamMap.put("paramValue",inspectItemListString);
		taskParamList.add(taskParamMap);
		
		inspectManagerMapper.storeTaskParam(taskParamList);
		addInspectTaskJob(map);
	}
	
	public void addInspectTaskJob(Map map) throws CommonException{
		//通过Quartz新增巡检任务计划
		int taskType = CommonDefine.QUARTZ.JOB_INSPECT;
		int taskID = Integer.valueOf(String.valueOf(map.get("taskId"))).intValue();
		if (!quartzManagerService.IsJobExist(taskType, taskID)) {
			Class jobClass = com.fujitsu.job.InspectJob.class;
			//cron 表达式
		    int periodType = Integer.valueOf(String.valueOf(map.get("periodType"))).intValue();
		    String period = String.valueOf(map.get("period"));
		    String cronExpression = cronExpression(periodType,period);
		    Map jobParam = new HashMap<String, Object>();
		    jobParam.put("taskId", taskID);
			System.out.println(cronExpression);
			quartzManagerService.addJob(taskType, taskID, jobClass, cronExpression,jobParam);
		}
	}
	/**
	 * 修改巡检任务页面初始化
	 * 返回的List<Map>是巡检任务信息列表
	 * 
	 * @param int inspectTaskId - 巡检任务ID
	 * @return Map<String,Object>
	 * @throws CommonException
	 */
	public Map<String,Object> getInspectTaskInfo (int inspectTaskId) throws CommonException{
        
		Map returnMap=new HashMap();
		Map map=new HashMap();
		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		List<Map> taskList = inspectManagerMapper.getInspectTask(inspectTaskId);
		Map task = taskList.get(0);
		//替换时间格式用于前台显示
	    String startTime = time.format(task.get("START_TIME"));
	    task.put("START_TIME", startTime);
	  //  String endTime = time.format(task.get("END_TIME"));
	  //  task.put("END_TIME", endTime);
	    String nextTime = time.format(task.get("NEXT_TIME"));
	    task.put("NEXT_TIME", nextTime);
	    
	    //组装周期信息
	    int periodType = Integer.valueOf(String.valueOf(task.get("PERIOD_TYPE"))).intValue();
	    String[] periodString = String.valueOf(task.get("PERIOD")).split(",");
	    Map periodMap = new HashMap();
	    switch(periodType){
	       case CommonDefine.YEAR:
	    	    periodMap.put("month", periodString[2]);
		        periodMap.put("day", periodString[4]);
		    	periodMap.put("time", periodString[5]);
		    	periodMap.put("summary", "每年"+periodString[2]+"月"+periodString[4]+"号");
		    	periodMap.put("cycleField", "每年"+periodString[2]+"月"+periodString[4]+"号 "+ periodString[5]);
		    	break;
	       case CommonDefine.QUARTER:
	    	    periodMap.put("month", periodString[2]);
		        periodMap.put("day", periodString[4]);
		    	periodMap.put("time", periodString[5]);
		    	String quarterTime = "";
		    	if(periodString[0].equals("1")){
		    		quarterTime = "第一个月";
		    	}else if(periodString[0].equals("2")){
		    		quarterTime = "第二个月";
		    	}else if(periodString[0].equals("3")){
		    		quarterTime = "第三个月";
		    	}
		    	periodMap.put("summary", "每季" + quarterTime + periodString[4] + "号");
		    	periodMap.put("cycleField", "每季" + quarterTime + periodString[4]+ "号 " + periodString[5]);
		    	break;
	       case CommonDefine.MONTH:
	    	    periodMap.put("month", "");
		        periodMap.put("day", periodString[4]);
		    	periodMap.put("time", periodString[5]);
		    	periodMap.put("summary", "每月" + periodString[4] + "号");
		    	periodMap.put("cycleField", "每月" + periodString[4] + "号 " + periodString[5]);
		    	break;
	    }
	    
	    //巡检项目、操作权限组组合前台显示
	    String[] privilegeList = null ;
	    String[] inspectItemList = null;
	    String privilegeParamId = null;
	    String inspectItemParamId = null;

	    List<Map> taskParamList = inspectManagerMapper.getInspectTaskParam(inspectTaskId);
	    for(int i = 0; i<taskParamList.size();i++){
	    	Map taskParam = taskParamList.get(i);
	    	String paramName = String.valueOf(taskParam.get("PARAM_NAME"));
	    	if(paramName.equals(CommonDefine.PRIVILEGE)){
	    		privilegeList = String.valueOf(taskParam.get("PARAM_VALUE")).split(",");
	    		privilegeParamId = String.valueOf(taskParam.get("SYS_TASK_PARAM_ID"));
	    	}else if(paramName.equals(CommonDefine.INSPECT_ITEM)){
	    		inspectItemList = String.valueOf(taskParam.get("PARAM_VALUE")).split(",");
	    		inspectItemParamId = String.valueOf(taskParam.get("SYS_TASK_PARAM_ID"));
	    		
	    	}
	    }
	    //解析字符串
	 //   String paramName = String.valueOf(taskParam.get(arg0))

	    returnMap.put("task", task);
	    returnMap.put("periodType", periodType);
	    returnMap.put("period", periodMap);
		returnMap.put("privilegeList", privilegeList);
		returnMap.put("inspectItemList", inspectItemList);
		returnMap.put("privilegeParamId", privilegeParamId);
		returnMap.put("inspectItemParamId", inspectItemParamId);
		//returnMap.put("total", total);
		return returnMap;
		
		
	}
	
	/**
	 * 修改巡检任务保存
	 * 返回的String,修改巡检任务是否成功
	 * 
	 * @param Map - 巡检任务信息
	 * @return String
	 * @throws CommonException
	 */
	public void updateInspectTask (Map map, List<String> inspectEquipList, 
			List<String> inspectEquipNameList) throws CommonException{
		
		List<Map> equipList = new ArrayList<Map>();
        List<Integer> taskIdList = new ArrayList<Integer>();
        Map<String, Object> taskIdMap = new HashMap<String, Object>();  
       
        int taskId = Integer.valueOf(String.valueOf(map.get("taskId"))).intValue();
        //判断周期是否改变
        List<Map> taskList = inspectManagerMapper.getInspectTask(taskId);
		Map task = taskList.get(0);
		//周期类型
	    int periodType = Integer.valueOf(String.valueOf(task.get("PERIOD_TYPE"))).intValue();
	    String period = String.valueOf(task.get("PERIOD"));
	    int periodTypeNew = Integer.valueOf(String.valueOf(map.get("periodType"))).intValue();
	    String periodNew = String.valueOf(map.get("period"));
	    if(periodTypeNew != periodType || periodNew != period){
	    	//通过Quartz新增巡检任务计划
	    	int taskType = CommonDefine.QUARTZ.JOB_INSPECT;
			int taskID = Integer.valueOf(String.valueOf(map.get("taskId"))).intValue();
			//cron 表达式
			String cronExpression = cronExpression(periodTypeNew,periodNew);
			System.out.println(cronExpression);
			quartzManagerService.modifyJobTime(taskType,taskID,cronExpression);
	    }
		
	    
        //更新任务表
		inspectManagerMapper.updateInspectTask(map); 

		String equip = inspectEquipList.get(0);
		System.out.println("第一个:"+equip);
		System.out.println("equipSize:"+inspectEquipList.size());
		if(!(inspectEquipList.size() ==1 && equip == "")){
		//删除任务详细信息表中信息
		taskIdList.add(taskId);
		taskIdMap.put("taskIdList", taskIdList);
		inspectManagerMapper.deleteTaskInfo(taskIdMap);
		//更新任务详细信息表中信息
		String[] equipInfo ;
		Map<String, Object> InspectEquipInfo ;
		if(inspectEquipList.size() == inspectEquipNameList.size()){
			
			for(int i = 0; i<inspectEquipList.size();i++){
				equipInfo = inspectEquipList.get(i).split("_");
				// 需要保存的包机人信息整合在map中
				InspectEquipInfo = new HashMap<String, Object>();
				
				InspectEquipInfo.put("taskInfoId", null);
				InspectEquipInfo.put("taskId", map.get("taskId"));
				InspectEquipInfo.put("equipType", equipInfo[0]);
				InspectEquipInfo.put("equipId", equipInfo[1]);
			//	InspectEquipInfo.put("targetDisplayName", inspectEquipNameList.get(i));
				InspectEquipInfo.put("isSuccess", 0);
				InspectEquipInfo.put("isComplete", 0);
				
				equipList.add(InspectEquipInfo);
			}
			
		}
		inspectManagerMapper.storeTaskInfo(equipList);
		}
		//操作权限组、巡检项目保存
		Map taskParamMap=new HashMap();
		List<Map> taskParamList = new ArrayList<Map>();
		List<String> privilegeList = new ArrayList<String>();
		privilegeList = (ArrayList<String>)map.get("privilegeList");
		String privilegeListString = null;
		for(int i = 0; i<privilegeList.size();i++){
			if(i==0){
				privilegeListString = privilegeList.get(i);
			}else{
				privilegeListString = privilegeListString + "," +  privilegeList.get(i);
			}
		}
		taskParamMap.put("taskParamId",map.get("privilegeParamId"));
		taskParamMap.put("paramValue",privilegeListString);
		taskParamList.add(taskParamMap);
		inspectManagerMapper.updateTaskParam(taskParamMap);
		
		taskParamMap = new HashMap();
		List<String> inspectItemList = new ArrayList<String>();
		inspectItemList = (ArrayList<String>)map.get("inspectItemList");
		String inspectItemListString = null;
		for(int i = 0; i<inspectItemList.size();i++){
			if(i==0){
				inspectItemListString = String.valueOf(inspectItemList.get(i));
			}else{
				inspectItemListString = inspectItemListString + "," +  inspectItemList.get(i);
			}
		}
		taskParamMap.put("taskParamId",map.get("inspectItemParamId"));
		taskParamMap.put("paramValue",inspectItemListString);
		taskParamList.add(taskParamMap);
		
		inspectManagerMapper.updateTaskParam(taskParamMap);		
		
	}
	
	/**
	 * 拼接cron表达式，用于Quartz新增修改巡检任务计划
	 * 返回cron表达式
	 * 
	 * @param int periodTypeNew - 周期类型
	 * @param String periodNew - 周期
	 * @return String
	 * @throws CommonException
	 */
	private String cronExpression(int periodTypeNew, String periodNew)throws CommonException{
		//cron 表达式
		String cronExpression = null;
		String month = null;
		String hour = null;
		String miniute = null;
		String[] periodArray = periodNew.split(",");
		String[] periodTime = periodArray[5].split(":");
		hour = periodTime[0];
		if(periodTime[1].endsWith(CommonDefine.TIME_CLOCK)){
			miniute = "0";
		}else if(periodTime[1].endsWith(CommonDefine.TIME_HALF)){
			miniute = periodTime[1];
		}
		//cron表达式拼接
		if(periodTypeNew == CommonDefine.MONTH){
			cronExpression = "0 " + miniute + " " +  hour + " " + periodArray[4] + " * ?";
		}else if(periodTypeNew == CommonDefine.QUARTER){
			switch(Integer.valueOf(periodArray[2]).intValue()){
			case 1:
				month = "1,4,7,10";
			case 2:
				month = "2,5,8,11";
			case 3:
				month = "3,6,9,12";
			}
			cronExpression = "0 " + miniute + " " +  hour + " " + periodArray[4] + " " + month + " ?";
		}else if(periodTypeNew == CommonDefine.YEAR){
			cronExpression = "0 " + miniute + " " +  hour + " " + periodArray[4] + " " + periodArray[2] + " ?";
		}
		
		return cronExpression;
	}
	
	/**
	 * 删除巡检任务
	 * 返回删除巡检任务是否成功信息
	 * 
	 * @param List<Integer> taskIdList - 巡检任务Id列表
	 * @return void
	 * @throws CommonException
	 */
	public void deleteInspectTask (List<Integer> taskIdList) throws CommonException{
		
		changeInspectTaskStatus(taskIdList,CommonDefine.QUARTZ.JOB_DELETE);
		
		Map<String, Object> map = new HashMap<String, Object>();  
	    map.put("taskIdList", taskIdList); 
		
		inspectManagerMapper.deleteTaskRunDetial(map);
		inspectManagerMapper.deleteTaskParam(map);
		inspectManagerMapper.deleteTaskInfo(map);
		inspectManagerMapper.deleteTask(map);
		
		
	}
	
	/**
	 * 立即执行巡检任务
	 * 返回立即执行巡检任务是否成功信息
	 * 
	 * @param int inspectTaskId - 巡检任务id
	 * @return void
	 * @throws CommonException
	 */
	public void startTaskImmediately (String inspectTaskId) throws CommonException{
		
		//通过Quartz立即执行巡检任务
		int taskType = CommonDefine.QUARTZ.JOB_INSPECT;
		int taskID = Integer.valueOf(inspectTaskId).intValue();
		quartzManagerService.ctrlJob(taskType, taskID, CommonDefine.QUARTZ.JOB_ACTIVATE);
		
	}
	
	/**
	 * 巡检任务启用、挂起
	 * 返回启用、挂起巡检任务是否成功信息
	 * 
	 * @param int inspectTaskId - 巡检任务Id
	 * @return void
	 * @throws CommonException
	 */
	public void changeInspectTaskStatus (List<Integer> taskIdList, int statusFlag) throws CommonException{
		
		//改变巡检任务状态并保存
		List<Map> updateList = new ArrayList<Map>();
		List<Map> taskList = inspectManagerMapper.getInspectTaskList(taskIdList);
        
		for(int i = 0; i< taskList.size();i++){
			Map task = taskList.get(i);

			task.put("TASK_STATUS", statusFlag);
			updateList.add(task);
			int taskType = CommonDefine.QUARTZ.JOB_INSPECT;
			int taskID = Integer.valueOf(String.valueOf(task.get("SYS_TASK_ID"))).intValue();

			//暂停任务/恢复任务/删除任务
			quartzManagerService.ctrlJob(taskType, taskID, CommonDefine.QUARTZ.TASK.STATUS.toJobStatus(statusFlag));
			
			inspectManagerMapper.updateTaskStatus(task);
			
		}
        
	}
	
	/**
	 * 巡检任务执行情况信息获取
	 * 返回的Map中分别为List<Map>类型的巡检任务执行情况信息
	 * 
	 * @param inspectTaskId - 巡检任务Id
	 * @return Map<String,Object>
	 * @throws CommonException
	 */
	public Map<String,Object> getTaskRunDetial (int inspectTaskId) throws CommonException{
		
		Map<String,Object> returnMap = new HashMap<String,Object>();
		Map<String,Object> taskRunDetial = new HashMap<String,Object>();
		List<Map> taskRunList = new ArrayList<Map>();
		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		//巡检设备个数
		int count = inspectManagerMapper.countInspectEquip(inspectTaskId);
		String equipNum = String.valueOf(count);
		
		//获取巡检任务的巡检项目列表
		List<Map> taskItemList = inspectManagerMapper.getInspectTaskItem(inspectTaskId);
		
		for(int i = 0; i<taskItemList.size();i++){
			Map taskItem = taskItemList.get(i);
			taskRunDetial = new HashMap<String,Object>();
						
			taskRunDetial.put("inspectItem", taskItem.get("TARGET_NAME"));
			if(taskItem.get("CREATE_TIME") != null){
				//替换时间格式用于前台显示
				taskRunDetial.put("finishTime", time.format(taskItem.get("CREATE_TIME")));
			}
			if(taskItem.get("RUN_RESULT") != null){
				int runResult = Integer.valueOf(String.valueOf(taskItem.get("RUN_RESULT"))).intValue();
				taskRunDetial.put("runStatus", 
						CommonDefine.QUARTZ.TASK.ACTION_STATUS.valueToString(runResult));
				switch(runResult){
				case CommonDefine.QUARTZ.TASK.ACTION_STATUS.RUNNING :
					int completedEquip = inspectManagerMapper.countCompletedEquip(inspectTaskId);
					String completedEquipNum = String.valueOf(completedEquip);
					taskRunDetial.put("itemNum", completedEquipNum + "/" + equipNum);
					break;
				case CommonDefine.QUARTZ.TASK.ACTION_STATUS.WAITING :
					//taskRunDetial.put("itemNum", "");
					break;
				default:
					taskRunDetial.put("itemNum", equipNum + "/" + equipNum);
				}
			}
			if(taskItem.get("DETAIL_INFO") != null){
				taskRunDetial.put("detialInfo", taskItem.get("DETAIL_INFO"));
			}
			taskRunList.add(taskRunDetial);
		}
		returnMap.put("rows", taskRunList);
		returnMap.put("total", taskRunList.size());
		
		return returnMap;
		
	}
	
	public List<Map> getProtectGroups(int neId,List<Integer> SCHEMA_STATE) throws CommonException{
		List<Map> returnData = new ArrayList<Map>();
		try {
			Map<String, Object> DEFINE = new HashMap<String, Object>();
			DEFINE.put("FALSE", CommonDefine.FALSE);
			returnData = inspectManagerMapper.getProtectGroups(neId,SCHEMA_STATE,DEFINE);
		} catch (Exception e) {
			throw new CommonException(e,
					MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
		return returnData;
	}
	public List<Map> getEProtectGroups(int neId,List<Integer> SCHEMA_STATE) throws CommonException{
		List<Map> returnData = new ArrayList<Map>();
		try {
			Map<String, Object> DEFINE = new HashMap<String, Object>();
			DEFINE.put("FALSE", CommonDefine.FALSE);
			returnData = inspectManagerMapper.getEProtectGroups(neId,SCHEMA_STATE,DEFINE);
		} catch (Exception e) {
			throw new CommonException(e,
					MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
		return returnData;
	}
	public List<Map> getWDMProtectGroups(int neId,List<Integer> SCHEMA_STATE) throws CommonException{
		List<Map> returnData = new ArrayList<Map>();
		try {
			Map<String, Object> DEFINE = new HashMap<String, Object>();
			DEFINE.put("FALSE", CommonDefine.FALSE);
			returnData = inspectManagerMapper.getWDMProtectGroups(neId,SCHEMA_STATE,DEFINE);
		} catch (Exception e) {
			throw new CommonException(e,
					MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
		return returnData;
	}
	
	@Override
	public List<Map<String, Object>> getProtectedList(Integer category,
			Integer pgId, Map<String, Object> param) throws CommonException {
		List<Map<String, Object>> returnData = new ArrayList<Map<String, Object>>();
		try {
			Map<String, Object> DEFINE = new HashMap<String, Object>();
			DEFINE.put("FALSE", CommonDefine.FALSE);
			DEFINE.put("CATEGORY_PROTECTION", CommonDefine.INSPECT.PRO_GROUP_CATEGORY.PROTECTION);
			DEFINE.put("CATEGORY_EPROTECTION", CommonDefine.INSPECT.PRO_GROUP_CATEGORY.EPROTECTION);
			DEFINE.put("CATEGORY_WDMPROTECTION", CommonDefine.INSPECT.PRO_GROUP_CATEGORY.WDMPROTECTION);
			if(param!=null)DEFINE.putAll(param);
			returnData = inspectManagerMapper.getProtectedList(category,pgId,DEFINE);
		} catch (Exception e) {
			throw new CommonException(e,
					MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
		return returnData;
	}

	public List<Map> getClockSources(int neId) throws CommonException{
		List<Map> returnData = new ArrayList<Map>();
		try {
			Map<String, Object> DEFINE = new HashMap<String, Object>();
			DEFINE.put("FALSE", CommonDefine.FALSE);
			returnData = inspectManagerMapper.getClockSources(neId,DEFINE);
		} catch (Exception e) {
			throw new CommonException(e,
					MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
		return returnData;
	}
	public Map getResourceInfoByRoom(int roomId) throws CommonException{
		Map returnData = null;
		try {
			returnData = inspectManagerMapper.getResourceInfoByRoom(roomId);
		} catch (Exception e) {
			throw new CommonException(e,
					MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
		return returnData;
	}
	public List<Map> getEngineerByNodes(List<Map> nodes) throws CommonException{
		List<Map> returnData = null;
		try {
			returnData = inspectManagerMapper.getEngineerByNodes(nodes);
		} catch (Exception e) {
			throw new CommonException(e,
					MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
		return returnData;
	}
	public List<Map> getPtpTypeByNe(int neId) throws CommonException{
		List<Map> returnData = new ArrayList<Map>();
		try {
			Map<String, Object> DEFINE = new HashMap<String, Object>();
			DEFINE.put("FALSE", CommonDefine.FALSE);
			returnData = inspectManagerMapper.getPtpTypeByNe(neId,DEFINE);
		} catch (Exception e) {
			throw new CommonException(e,
					MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
		return returnData;
	}

	@Override
	public int CountNePtpByType(int neId, String ptpType)
			throws CommonException {
		int returnData = 0;
		try {
			Map<String, Object> DEFINE = new HashMap<String, Object>();
			DEFINE.put("FALSE", CommonDefine.FALSE);
			returnData = inspectManagerMapper.CountNePtpByType(neId,ptpType,DEFINE);
		} catch (Exception e) {
			throw new CommonException(e,
					MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
		return returnData;
	}
	@Override
	public int CountNePtpInUSE(int neId) throws CommonException {
		int returnData = 0;
		try {
			Map<String, Object> DEFINE = new HashMap<String, Object>();
			DEFINE.put("FALSE", CommonDefine.FALSE);
			returnData = inspectManagerMapper.CountNePtpHasCrs(neId,DEFINE);
		} catch (Exception e) {
			throw new CommonException(e,
					MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
		return returnData;
	}
	@Override
	public int CountNePtp(int neId) throws CommonException {
		int returnData = 0;
		try {
			Map<String, Object> DEFINE = new HashMap<String, Object>();
			DEFINE.put("FALSE", CommonDefine.FALSE);
			returnData = inspectManagerMapper.CountNePtp(neId,DEFINE);
		} catch (Exception e) {
			throw new CommonException(e,
					MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
		return returnData;
	}
	@Override
	public int CountNeCtpInUSE(int neId) throws CommonException {
		int returnData = 0;
		try {
			Map<String, Object> DEFINE = new HashMap<String, Object>();
			DEFINE.put("FALSE", CommonDefine.FALSE);
			returnData = inspectManagerMapper.CountNeCtpHasCrs(neId,DEFINE);
		} catch (Exception e) {
			throw new CommonException(e,
					MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
		return returnData;
	}
	@Override
	public int CountNeCtp(int neId) throws CommonException {
		int returnData = 0;
		try {
			Map<String, Object> DEFINE = new HashMap<String, Object>();
			DEFINE.put("FALSE", CommonDefine.FALSE);
			returnData = inspectManagerMapper.CountNeCtp(neId,DEFINE);
		} catch (Exception e) {
			throw new CommonException(e,
					MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
		return returnData;
	}
	@Override
	public String getReportUrl(Integer reportId,boolean zip,String[] selectList) throws CommonException{
		String path=null;
		try{
			Map reportMap=null;
			if(reportId!=null)
				reportMap=inspectManagerMapper.selectReport(reportId);
			if(reportMap==null||reportMap.isEmpty()){
				throw new CommonException(null,MessageCodeDefine.COM_EXCPT_ENTITY_NOT_FOUND);
			}
			String basePath=CommonDefine.EXCEL.REPORT_DIR+
					"/"+CommonDefine.EXCEL.INSPECT_BASE+
					"/"+reportMap.get("REPORT_NAME");
			List<String> paths=new ArrayList<String>();
			boolean noneExist=true;
			for(String select:selectList){
				String html=basePath+"-"+
						select+".htm";
				if(new File(CommonDefine.PATH_ROOT+html).exists()){
					paths.add(html);
					if(new File(CommonDefine.PATH_ROOT+html+"_files").exists())
						paths.add(html+"_files");
					noneExist=false;
				}
			}
			if(noneExist){
				throw new CommonException(new FileNotFoundException(),MessageCodeDefine.COM_EXCPT_ENTITY_NOT_FOUND);
			}
			if(zip){
				for(int i=0;i<paths.size();i++){
					paths.set(i, CommonDefine.PATH_ROOT+paths.get(i));
				}
				//压缩
				String zipPath=System.getProperty("java.io.tmpdir")
						+ "/ExportFiles/"+basePath+"-"+selectList.hashCode()+".zip";
				File pathDir = new File(zipPath).getParentFile();
				if (!(pathDir.exists()&&pathDir.isDirectory())&&!pathDir.mkdirs()){
					throw new CommonException(new Exception(),MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
				}
				boolean isZiped = ZipUtil.getInstance().CreateZipFile(
						paths,zipPath,false);
				if(isZiped){
					path=zipPath;
				}else{
					throw new CommonException(new Exception(),MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
				}
			}else{
				path=paths.get(0);
				for(int i=1;i<paths.size();i++){
					if(paths.get(i).endsWith(".htm"))
						path+="*"+paths.get(i);
				}
			}
		}catch(CommonException e){
			throw e;
		}catch(Exception e){
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
		return path;
	}
}
