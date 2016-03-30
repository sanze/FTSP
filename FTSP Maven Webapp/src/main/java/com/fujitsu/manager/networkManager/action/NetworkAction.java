package com.fujitsu.manager.networkManager.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;

import com.fujitsu.IService.IMethodLog;
import com.fujitsu.IService.INetworkManagerService;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.util.SpringContextUtil;

@SuppressWarnings("serial")
public class NetworkAction extends AbstractAction { 
	@Resource
	public INetworkManagerService networkManagerService;

	private Map<String,String> paramMap=new HashMap<String,String>(); 
	private String jsonString;
	private List<String> treeNodes;
	private String cycleType;
	private String MJ;
	private String MN;
	private String WR;
	private String MJChecked;
	private String MNChecked;
	private String WRChecked;
	private String neId;
	private String flag; 
	private String sortA;
	private String sortB;
	private String unitTypeId;
	private List<String> modifyList;
	private List<Integer> unitTypeList;
	private String factoryId;
	private String warningType;
	@IMethodLog(desc = "查询预警值")
	public String getEarlyAlarmSetting(){ 
		try{
			Map<String,Object> data=networkManagerService.getEarlyAlarmSetting(jsonString);   
			resultObj=JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
    
    @IMethodLog(desc = "设置预警值", type = IMethodLog.InfoType.MOD)
	public String updateEarlyAlarmSetting(){
		try{
			result = networkManagerService.updateEarlyAlarmSetting(paramMap); 
			resultObj = JSONObject.fromObject(result); 
		} catch (CommonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return RESULT_OBJ;
	}
    
    @IMethodLog(desc = "查询网元资源预警")
	public String searchNeEarlyWarn(){
		try{   
			Map<String,Object> data=networkManagerService.searchNeEarlyWarn(paramMap,start,limit);
			data.put("returnResult",CommonDefine.SUCCESS);
			resultObj=JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	} 
    
    @IMethodLog(desc = "网元预警链接到复用段资源预警时初始化")
	public String initMultiEarlyWarn(){
		try{ 
			Map<String,Object> data=networkManagerService.initMultiEarlyWarn(paramMap,sysUserId);
			data.put("returnResult",CommonDefine.SUCCESS);
			resultObj=JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	} 
    
    @IMethodLog(desc = "查询复用段资源预警")
	public String searchMultiEarlyWarn(){
		try{   
			Map<String,Object> data=networkManagerService.searchMultiEarlyWarn(paramMap,sysUserId,start,limit);
			data.put("returnResult",CommonDefine.SUCCESS);
			resultObj=JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	} 
    
    @IMethodLog(desc = "查询复用段资源预警的详细信息")
	public String searchDetailMulti(){
		try{
			Map<String,Object> data=networkManagerService.searchDetailMulti(paramMap);
			data.put("returnResult",CommonDefine.SUCCESS);
			resultObj=JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	} 
    
    @IMethodLog(desc = "超大环/长单链/无保护环查询")
  	public String searchCommonEarlyAlarm(){
  		try{  
  			Map<String,Object> data=networkManagerService.searchCommonEarlyAlarm(paramMap,sysUserId,start,limit);
  			data.put("returnResult",CommonDefine.SUCCESS);
  			resultObj=JSONObject.fromObject(data);
  		} catch (CommonException e) {
  			result.setReturnResult(CommonDefine.FAILED);
  			result.setReturnMessage(e.getErrorMessage());
  			resultObj = JSONObject.fromObject(result);
  		}
  		return RESULT_OBJ;
  	} 
    
    
    @IMethodLog(desc = "超大环/长单链/无保护环及节点链接查询")
  	public String searchAreaNodeList(){
  		try{  
  			Map<String,Object> data=networkManagerService.searchAreaNodeList(paramMap);
  			data.put("returnResult",CommonDefine.SUCCESS);
  			resultObj=JSONObject.fromObject(data);
  		} catch (CommonException e) {
  			result.setReturnResult(CommonDefine.FAILED);
  			result.setReturnMessage(e.getErrorMessage());
  			resultObj = JSONObject.fromObject(result);
  		}
  		return RESULT_OBJ;
  	}  
	 
    @IMethodLog(desc = "获取系统的拓扑数据")
  	public String getTopoNodeAndLink(){
  		try{  
  			Map<String,Object> data=networkManagerService.getTopoNodeAndLink(paramMap);
  			data.put("returnResult",CommonDefine.SUCCESS);
  			resultObj=JSONObject.fromObject(data);
  		} catch (CommonException e) {
  			result.setReturnResult(CommonDefine.FAILED);
  			result.setReturnMessage(e.getErrorMessage());
  			resultObj = JSONObject.fromObject(result);
  		}
  		return RESULT_OBJ;
  	}  
    
    @IMethodLog(desc = "网络分析部分的导出")
	public String exportExcel() { 
		try {
			String destination = networkManagerService.exportExcel(paramMap,sysUserId);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(destination);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	} 
    
    @IMethodLog(desc = "资源拓扑图link时隙可用率导出")
   	public String exportLinkToExcel() { 
   		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			Map<String, String> paramMap = (Map<String, String>) jsonObject;
   			String destination = networkManagerService.exportExcel(paramMap,sysUserId);
   			result.setReturnResult(CommonDefine.SUCCESS);
   			result.setReturnMessage(destination);
   			resultObj = JSONObject.fromObject(result);
   		} catch (CommonException e) {
   			result.setReturnResult(CommonDefine.FAILED);
   			result.setReturnMessage(e.getErrorMessage());
   			resultObj = JSONObject.fromObject(result);
   		}		System.out.println(resultObj);
   		return RESULT_OBJ;
   	}
    
    @IMethodLog(desc = "网络分析w部分的导出")
	public String exportExcelByParams() { 
		try {
			Map<String,Object> map = new HashMap<String,Object>();
    		List<Map> nodeList = ListStringtoListMap(this.treeNodes);
    		map.put("nodeList", nodeList);
    		map.put("MJ", MJ==null? null : MJ);
    		map.put("MN", MN==null? null : MN);
    		map.put("WR", WR==null? null : WR);
    		map.put("MJChecked", MJChecked==null ? null : MJChecked);
    		map.put("MNChecked", MNChecked==null ? null : MNChecked);
    		map.put("WRChecked", WRChecked==null ? null : WRChecked);
    		if("4".equals(flag) || "8".equals(flag)){
//    			多环节点、未成环网元
    	   		map.put("type", CommonDefine.NETWORK.CIRCLE);
    		}else if("6".equals(flag)){
//    			大汇聚点链
    	   		map.put("type", CommonDefine.NETWORK.LINK);
    		}  
    		map.put("flag", flag);
			String destination = networkManagerService.exportExcel(map);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(destination);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	} 
    
    @IMethodLog(desc = "多环节点网元信息查询")
    public String searchPolycyclicNodeList(){
    	try{
    		Map<String,Object> map = new HashMap<String,Object>();
    		List<Map> nodeList = ListStringtoListMap(this.treeNodes);
    		map.put("nodeList", nodeList);
    		map.put("MJ", Integer.valueOf(MJ).intValue());
    		map.put("MN", Integer.valueOf(MN).intValue());
    		map.put("WR", Integer.valueOf(WR).intValue());
    		map.put("MJChecked", CommonDefine.FALSE == Integer.valueOf(MJChecked).intValue() ? null : MJChecked);
    		map.put("MNChecked", CommonDefine.FALSE == Integer.valueOf(MNChecked).intValue() ? null : MNChecked);
    		map.put("WRChecked", CommonDefine.FALSE == Integer.valueOf(WRChecked).intValue() ? null : WRChecked);
    		map.put("type", CommonDefine.NETWORK.CIRCLE);
    		map.put("start", start);
    		map.put("limit", limit);
    		Map<String,Object> data = networkManagerService.searchNodeNeList(map);
    		resultObj = JSONObject.fromObject(data);
    	}catch(CommonException e){
    		result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
    	}   	
    	return RESULT_OBJ;
    }
    
    @IMethodLog(desc = "大汇聚点链网元信息查询")
    public String searchLargeConvergenceNodeList(){
    	try{
    		Map<String,Object> map = new HashMap<String,Object>();
    		List<Map> nodeList = ListStringtoListMap(this.treeNodes);
    		map.put("nodeList", nodeList);
    		map.put("MJ", Integer.valueOf(MJ).intValue());
    		map.put("MN", Integer.valueOf(MN).intValue());
    		map.put("WR", Integer.valueOf(WR).intValue());
    		map.put("MJChecked", CommonDefine.FALSE == Integer.valueOf(MJChecked).intValue() ? null : MJChecked);
    		map.put("MNChecked", CommonDefine.FALSE == Integer.valueOf(MNChecked).intValue() ? null : MNChecked);
    		map.put("WRChecked", CommonDefine.FALSE == Integer.valueOf(WRChecked).intValue() ? null : WRChecked);
    		map.put("type", CommonDefine.NETWORK.LINK);
    		map.put("start", start);
    		map.put("limit", limit);
    		Map<String,Object> data = networkManagerService.searchNodeNeList(map);
    		resultObj = JSONObject.fromObject(data);
    	}catch(CommonException e){
    		result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
    	}   	
    	return RESULT_OBJ;
    }
    
    @IMethodLog(desc = "未成环网元信息查询")
    public String searchNoCyclicNodeList(){
    	try{
    		Map<String,Object> map = new HashMap<String,Object>();
    		List<Map> nodeList = ListStringtoListMap(this.treeNodes);
    		map.put("nodeList", nodeList);
    		map.put("type", CommonDefine.NETWORK.CIRCLE);
    		map.put("start", start);
    		map.put("limit", limit);
    		Map<String,Object> data = networkManagerService.searchNoCyclicNodeList(map);
    		resultObj = JSONObject.fromObject(data);
    	}catch(CommonException e){
    		result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
    	}   	
    	return RESULT_OBJ;
    }
    
    @IMethodLog(desc = "预警参数值获取")
    public String getWRConfig(){
    	try{
    		Map<String,Object> data = networkManagerService.getWRConfig(Integer.valueOf(cycleType).intValue());
    		resultObj = JSONObject.fromObject(data);
    	}catch(CommonException e){
    		result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
    	}   	
    	return RESULT_OBJ;
    }
    
    @IMethodLog(desc = "多环节点环信息查询")
    public String searchPolycycleList(){
    	try{
    		Map<String,Object> map = new HashMap<String,Object>();
    		map.put("neId", Integer.valueOf(neId).intValue());
    		map.put("start", start);
    		map.put("limit", limit);
    		map.put("type", CommonDefine.NETWORK.CIRCLE);
    		Map<String,Object> data = networkManagerService.searchCycleList(map);
    		resultObj = JSONObject.fromObject(data);
    	}catch(CommonException e){
    		result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
    	}   	
    	return RESULT_OBJ;
    }
    
    @IMethodLog(desc = "大汇聚点链信息查询")
    public String searchLargeConvergenceList(){
    	try{
    		Map<String,Object> map = new HashMap<String,Object>();
    		map.put("neId", Integer.valueOf(neId).intValue());
    		map.put("start", start);
    		map.put("limit", limit);
    		map.put("type", CommonDefine.NETWORK.LINK);
    		Map<String,Object> data = networkManagerService.searchCycleList(map);
    		resultObj = JSONObject.fromObject(data);
    	}catch(CommonException e){
    		result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
    	}   	
    	return RESULT_OBJ;
    }
    
    @IMethodLog(desc = "查询槽道可用率分析表头信息")
    public String searchAvailabilityHeader(){
    	int type = Integer.valueOf(paramMap.get("type"));
    	List<Map> equipList = ListStringtoListMap(this.modifyList);
    	try{
    		Map<String,Object> data = networkManagerService.searchAvailabilityHeader(type,equipList);
    		resultObj = JSONObject.fromObject(data);
    	}catch(CommonException e){
    		result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
    	}   	
    	return RESULT_OBJ;
    }
    
    @IMethodLog(desc = "查询槽道可用率统计信息")
    public String searchAvailabilityData(){
    	int type = Integer.valueOf(paramMap.get("type"));
    	List<Map> equipList = ListStringtoListMap(this.modifyList);
    	try{
    		Map<String,Object> data = networkManagerService.searchAvailabilityData(type,equipList,
    				Integer.valueOf(warningType).intValue(),start,limit);
    		resultObj = JSONObject.fromObject(data);
    	}catch(CommonException e){
    		result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
    	}   	
    	return RESULT_OBJ;
    }
    
	@IMethodLog(desc = "生成槽道可用率统计信息图表数据")
	public String generateDiagramXml() {
		try {
			List<Map> equipList = ListStringtoListMap(this.modifyList);
			Map result = networkManagerService
					.generateDiagramXml(paramMap,equipList);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "查询网络分析预警值")
	public String searchWarningValue() {
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = (Map<String, Object>) jsonObject;
			int type = Integer.valueOf(paramMap.get("type").toString());
			Map data = networkManagerService.searchWarningValue(type);
			
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "修改网络分析预警值")
	public String modifyWarningValue() {
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			Map<String, String> paramMap = (Map<String, String>) jsonObject;
			networkManagerService.modifyWarningValue(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "查询板卡类别名称自定义列表")
    public String ctpNameCustomList(){
    	try{
    		Map<String,Object> data = networkManagerService.ctpNameCustomList(start,limit);
    		resultObj = JSONObject.fromObject(data);
    	}catch(CommonException e){
    		result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
    	}   	
    	return RESULT_OBJ;
    }
	
	@IMethodLog(desc = "新增板卡类别")
    public String addCtpCategory(){
		Map<String,Object> m = new HashMap<String,Object>();
		try {
			//验证用户信息
			Map<String,Object> validateResult=networkManagerService.validateCtpName(sortB);
			boolean isExists=(Boolean)validateResult.get("success");
			if(!isExists){
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage("新增板卡类别已存在！");
				resultObj = JSONObject.fromObject(result);
				return RESULT_OBJ;
			}
			networkManagerService.addCtpCategory(Integer.valueOf(sortA).intValue(),sortB);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("新增板卡类别成功！");
		} catch (CommonException e) {
			e.printStackTrace();
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("新增板卡类别失败！");
		}
		resultObj = JSONObject.fromObject(result);
		return RESULT_OBJ;
    }
	@IMethodLog(desc = "删除板卡类别")
    public String deleteCtpCategory(){
		Map<String,Object> m = new HashMap<String,Object>();
		try {
			m = networkManagerService.deleteCtpCategory(unitTypeList);
		} catch (CommonException e) {
			e.printStackTrace();
			m.put("success", false);
			m.put("msg", "删除板卡类别失败！");
		}
		resultObj = JSONObject.fromObject(m);
		return RESULT_OBJ;
    }
	@IMethodLog(desc = "修改板卡类别")
	public String updateCtpCategory() {
		String returnString = RESULT_OBJ;
		List<Map> unitTypeList = ListStringtoListMap(this.modifyList);
		try {
			networkManagerService.updateCtpCategory(unitTypeList);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("修改板卡类别成功！");
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}
	@IMethodLog(desc = "获取厂商列表")
    public String getFactoryGroup(){
		Map<String,Object> data = new HashMap<String,Object>();
		Map<String,Object> map = new HashMap<String,Object>();
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		int factoryId = 0;
		String factoryName = "全部";
		map.put("factoryId", factoryId);
		map.put("factoryName", factoryName);
		list.add(map);
		map = new HashMap<String,Object>();
		map.put("factoryId", CommonDefine.FACTORY_HW_FLAG);
		map.put("factoryName", CommonDefine.FACTORY_HW_NAME);
		list.add(map);
		map = new HashMap<String,Object>();
		map.put("factoryId", CommonDefine.FACTORY_ZTE_FLAG);
		map.put("factoryName", CommonDefine.FACTORY_ZTE_NAME);
		list.add(map);
		map = new HashMap<String,Object>();
		map.put("factoryId", CommonDefine.FACTORY_LUCENT_FLAG);
		map.put("factoryName", CommonDefine.FACTORY_LUCENT_NAME);
		list.add(map);
		map = new HashMap<String,Object>();
		map.put("factoryId", CommonDefine.FACTORY_FIBERHOME_FLAG);
		map.put("factoryName", CommonDefine.FACTORY_FIBERHOME_NAME);
		list.add(map);
		map = new HashMap<String,Object>();
		map.put("factoryId", CommonDefine.FACTORY_ALU_FLAG);
		map.put("factoryName", CommonDefine.FACTORY_ALU_NAME);
		list.add(map);
		map = new HashMap<String,Object>();
		map.put("factoryId", CommonDefine.FACTORY_FUJITSU_FLAG);
		map.put("factoryName", CommonDefine.FACTORY_FUJITSU_NAME);
		list.add(map);
		data.put("total", list.size());
		data.put("rows", list);
		resultObj = JSONObject.fromObject(data);  	
    	return RESULT_OBJ;
    }
	@IMethodLog(desc = "查询板卡类别自定义列表")
    public String getCtpCategoryListById(){
    	try{
    		Map<String,Object> data = networkManagerService.getCtpCategoryListById(
    				Integer.valueOf(factoryId).intValue(),start,limit);
    		resultObj = JSONObject.fromObject(data);
    	}catch(CommonException e){
    		result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
    	}   	
    	return RESULT_OBJ;
    }
	@IMethodLog(desc = "设定板卡类别")
	public String setCtpCategory() {
		String returnString = RESULT_OBJ;
		List<Map> unitTypeList = ListStringtoListMap(this.modifyList);
		try {
			networkManagerService.setCtpCategory(unitTypeList);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("设定板卡类别成功！");
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}
	@IMethodLog(desc = "导出数据")
	public String exportAvailabilityData(){
		int type = Integer.valueOf(paramMap.get("type"));
		List<Map> equipList = ListStringtoListMap(this.modifyList);
		try{
			String filePath = networkManagerService.exportAvailabilityData(type,equipList);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(filePath);
			resultObj = JSONObject.fromObject(result);
		}catch(Exception e){
			e.printStackTrace();
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("导出失败！");
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * ETL数据抽取
	 * 
	 * @return
	 */
	@IMethodLog(desc = "ETL数据抽取")
	public String runTransfer()
	{
		int type = Integer.valueOf(paramMap.get("type"));
		
		String path = CommonDefine.PATH_ROOT;
		switch(type){
		case CommonDefine.NETWORK.NWA_SLOT:
			path = path+"WEB-INF/classes/kettle/NWA_SLOT.ktr";
			break;
		case CommonDefine.NETWORK.NWA_PORT:
			path = path+"WEB-INF/classes/kettle/NWA_PORT.ktr";
			break;
		case CommonDefine.NETWORK.NWA_CTP:
			path = path+"WEB-INF/classes/kettle/NWA_CTP.ktr";
			break;
		case CommonDefine.NETWORK.NWA_PORT_ROUTE:
			path = path+"WEB-INF/classes/kettle/NWA_PORT--ROUTE.ktr";
			break;
		}
		try {
			Map param = SpringContextUtil.getDataBaseParam();
			//运行kettle job
			runTransfer(param,path);
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);
		} catch (Exception e) {
			result.setReturnResult(CommonDefine.FAILED);
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	//
	private void runTransfer(Map params, String ktrPath) {
		Trans trans = null;
		try {
			// // 初始化
			// 转换元对象
			KettleEnvironment.init();// 初始化
			EnvUtil.environmentInit();
			TransMeta transMeta = new TransMeta(ktrPath);
			// 转换
			trans = new Trans(transMeta);
			trans.setVariable("host", params.get(CommonDefine.DB_HOST)
					.toString());
			trans.setVariable("sid", params.get(CommonDefine.DB_SID).toString());
			trans.setVariable("port", params.get(CommonDefine.DB_PORT)
					.toString());
			trans.setVariable("username", params.get(CommonDefine.DB_USERNAME)
					.toString());
			trans.setVariable("password", params.get(CommonDefine.DB_PASSWORD)
					.toString());
			// 执行转换
			trans.execute(new String[] {});
			// 等待转换执行结束
			trans.waitUntilFinished();
			// 抛出异常
			if (trans.getErrors() > 0) {
				throw new Exception(
						"There are errors during transformation exception!(传输过程中发生异常)");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 导入槽道可用板卡信息
	 * 
	 * @return
	 */
	@IMethodLog(desc = "导入槽道可用板卡信息")
	public String importAcceptEqptType()
	{
		try {
			Map param = SpringContextUtil.getDataBaseParam();
			//运行kettle job
			String path = CommonDefine.PATH_ROOT+"WEB-INF/classes/kettle/ACCEPT_EQPT_TYPE.ktr";
			runTransfer(param,path);
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);
		} catch (Exception e) {
			result.setReturnResult(CommonDefine.FAILED);
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "查询网元端口使用详情")
	public String getPortDetial(){ 
		try{
			int neId = Integer.valueOf(paramMap.get("neId").toString()).intValue();
			int type = Integer.valueOf(paramMap.get("type").toString()).intValue();
			Map<String,Object> data=networkManagerService.getPortDetial(neId,type);   
			resultObj=JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	public Map<String, String> getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map<String, String> paramMap) {
		this.paramMap = paramMap;
	}

	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public List<String> getTreeNodes() {
		return treeNodes;
	}

	public void setTreeNodes(List<String> treeNodes) {
		this.treeNodes = treeNodes;
	}

	public String getCycleType() {
		return cycleType;
	}

	public void setCycleType(String cycleType) {
		this.cycleType = cycleType;
	}

	public String getMJ() {
		return MJ;
	}

	public void setMJ(String mJ) {
		MJ = mJ;
	}

	public String getMN() {
		return MN;
	}

	public void setMN(String mN) {
		MN = mN;
	}

	public String getWR() {
		return WR;
	}

	public void setWR(String wR) {
		WR = wR;
	}

	public String getMJChecked() {
		return MJChecked;
	}

	public void setMJChecked(String mJChecked) {
		MJChecked = mJChecked;
	}

	public String getMNChecked() {
		return MNChecked;
	}

	public void setMNChecked(String mNChecked) {
		MNChecked = mNChecked;
	}

	public String getWRChecked() {
		return WRChecked;
	}

	public void setWRChecked(String wRChecked) {
		WRChecked = wRChecked;
	}

	public String getNeId() {
		return neId;
	}

	public void setNeId(String neId) {
		this.neId = neId;
	}

	public String getSortB() {
		return sortB;
	}

	public void setSortB(String sortB) {
		this.sortB = sortB;
	}

	public String getSortA() {
		return sortA;
	}

	public void setSortA(String sortA) {
		this.sortA = sortA;
	}

	public String getUnitTypeId() {
		return unitTypeId;
	}

	public void setUnitTypeId(String unitTypeId) {
		this.unitTypeId = unitTypeId;
	}

	public List<String> getModifyList() {
		return modifyList;
	}

	public void setModifyList(List<String> modifyList) {
		this.modifyList = modifyList;
	}
	
	public List<Integer> getUnitTypeList() {
		return unitTypeList;
	}

	public void setUnitTypeList(List<Integer> unitTypeList) {
		this.unitTypeList = unitTypeList;
	}


	public String getFactoryId() {
		return factoryId;
	}

	public void setFactoryId(String factoryId) {
		this.factoryId = factoryId;
	}

	public String getWarningType() {
		return warningType;
	}

	public void setWarningType(String warningType) {
		this.warningType = warningType;
	}
	
}
