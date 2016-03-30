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
			System.out.println("jsonResult:"+resultArray);
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
			System.out.println("jsonResult:"+resultArray);
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
			System.out.println("jsonResult:"+resultObj);
		} catch (Exception e) {
			e.printStackTrace();
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
