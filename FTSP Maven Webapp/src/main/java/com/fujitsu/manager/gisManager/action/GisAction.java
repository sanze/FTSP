package com.fujitsu.manager.gisManager.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.fujitsu.IService.IGisManagerService;
import com.fujitsu.IService.IMethodLog;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;


public class GisAction extends AbstractAction {
	@Resource
	public IGisManagerService gisManagerService;
	
	private String jsonString;
	
	// 转换Map 内容 字符串“null" -> null
	public Map<String, Object>  ChangeMapNull(Map<String, Object> map)
	{
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try
		{
			if(map != null)
			{
				Set set = map.entrySet();
				for(Iterator iter = set.iterator(); iter.hasNext();)
				{
					Map.Entry entry = (Map.Entry)iter.next();

					String key = (String)entry.getKey();

					if(entry.getValue() == null || entry.getValue().equals("null")){
						resultMap.put(key, null);
					}else{
						resultMap.put(key, entry.getValue().toString());
					}
				}
			}

		}catch(Exception e){
			e.printStackTrace();
		}
		return resultMap;
	}
	
	//封装sort和前台录入参数 ->map
	private Map<String, Object> getParameterMap(String jsonString){
		//前台传递参数map
		Map<String, Object> map = new HashMap<String, Object>(); 

		try{
			if(jsonString!=null){
				JSONObject jsonObject = JSONObject.fromObject(jsonString);
				map = (Map<String, Object>) jsonObject;
			}
			map = ChangeMapNull(map);
		}catch (Exception e) {
			e.printStackTrace();
		}

		return map;
	}
	
	@IMethodLog(desc = "获取GIS数据")
	public String getGisResourceData(){
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			//获取sort和参数封装后的map
			map = getParameterMap(jsonString);
			
			List<Map<String,Object>> dataList = gisManagerService.getGisResourceData(map);
			// 将返回的结果转成JSON对象，返回前台
			resultArray = JSONArray.fromObject(dataList);
			System.out.println("GIS数据边界："+jsonString);
			System.out.println("获取GIS数据:"+resultArray);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return RESULT_ARRAY;
	}
	
	@IMethodLog(desc = "获取测试路由")
	public String getTestRoutesByAZ(){
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			//获取sort和参数封装后的map
			map = getParameterMap(jsonString);
			
			List<Map<String,Object>> dataList = gisManagerService.getTestRoutesByAZ(map);
			// 将返回的结果转成JSON对象，返回前台
			resultArray = JSONArray.fromObject(dataList);
			System.out.println("获取测试路由:"+resultArray);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return RESULT_ARRAY;		
	}
	
	@IMethodLog(desc = "资源显示方式")
	public String displayDataByStrategy(){
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			//获取sort和参数封装后的map
			map = getParameterMap(jsonString);
			
			Map<String,Object> data = gisManagerService.displayDataByStrategy(map);
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(data);
			System.out.println("资源显示方式:"+resultObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return RESULT_OBJ;		
	}
	
	@IMethodLog(desc = "获取当前区域下的传输系统")
	public String getTransSystems(){
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			//获取sort和参数封装后的map
			map = getParameterMap(jsonString);
			
			Map<String,Object> data = gisManagerService.getTransSystems(map);
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(data);
			System.out.println("获取当前区域下的传输系统:"+resultObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return RESULT_OBJ;	
	}
	
	@IMethodLog(desc = "获取不在光缆段上的机房")
	public String getStationsNotInCable(){
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			//获取sort和参数封装后的map
			map = getParameterMap(jsonString);
			
			List<Map<String, Object>> list = gisManagerService.getStationsNotInCable(map);		
			resultArray = JSONArray.fromObject(list);
			System.out.println("获取不在光缆段上的机房:"+resultArray);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return RESULT_ARRAY;	
	}
	
	@IMethodLog(desc = "执行测试", type = IMethodLog.InfoType.MOD)
	public String doTest(){
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			//获取sort和参数封装后的map
			map = getParameterMap(jsonString);
			
			CommonResult flag = gisManagerService.doTest(map);
			result.setReturnResult(flag.getReturnResult());
			result.setReturnMessage(flag.getReturnMessage());
			resultObj = JSONObject.fromObject(result);
		} catch (Exception e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "获取指定测试路由的测试参数")
	public String getTestParamById(){
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			//获取sort和参数封装后的map
			map = getParameterMap(jsonString);
			
			Map<String, Object> result = gisManagerService.getTestParamById(map);
			result.put("returnResult",CommonDefine.SUCCESS);
			resultObj=JSONObject.fromObject(result);
		} catch (Exception e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "测试参数确认：获取波长数据")
	public String getWaveLengthList(){
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			Map map = (Map) jsonObject;
			
			List<Map> data = gisManagerService.getWaveLengthList(map);
			
			Map rmap = new HashMap();
			rmap.put("rows", data);
			resultObj = JSONObject.fromObject(rmap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "测试参数确认：获取量程数据")
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String getRangeList(){
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			Map map = (Map) jsonObject;
			
			List<Map> data = gisManagerService.getRangeList(map);
			Map rmap = new HashMap();
			rmap.put("rows", data);
			resultObj = JSONObject.fromObject(rmap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@IMethodLog(desc = "测试参数确认：获取脉冲宽度数据列表")
	public String getPluseWidthList(){
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			Map map = (Map) jsonObject;
			
			List<Map> data = gisManagerService.getPluseWidthList(map);
			Map rmap = new HashMap();
			rmap.put("rows", data);
			resultObj = JSONObject.fromObject(rmap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	

	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}
}
