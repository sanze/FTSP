package com.fujitsu.manager.resourceManager.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.fujitsu.IService.IAreaManagerService;
import com.fujitsu.IService.IResourceStatisticManagerService;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonException;

import fusioncharts.FusionChartDesigner;

public class ResourceStatisticAction extends AbstractAction {
  
	private String jsonString; 
	private String type; 

	@Resource
	public IResourceStatisticManagerService resourceStatisticManagerService;
	@Resource
	public IAreaManagerService areaManagerService; 
	
	public String getStatisticGrid() { 
		List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
		Map<String,Object> rtn = new HashMap <String,Object>();
		try { 
			// 转化成JSONArray对象
			if(jsonString!=null &&jsonString!=""){				
				JSONArray jsonArray = JSONArray.fromObject(jsonString);  
				for (Object obj : jsonArray) { 
					JSONObject jsonObject = (JSONObject) obj;
					Map toMap = (Map) jsonObject;
					data.add(toMap); 
					}
				rtn = resourceStatisticManagerService.getStatisticGrid(data,type,start,limit);
				}  
			resultObj = JSONObject.fromObject(rtn);
		
		} catch (CommonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return RESULT_OBJ;
	}
	

	/**
	 * Method name: getStatisticNeChart <BR>
	 * Description: 网元统计信息<BR> 
	 * @return String<BR>
	 */
	@SuppressWarnings("unchecked")
	public String getStatisticChart() throws CommonException {  
		Map<String, String> xmlMap = new HashMap<String, String>();
		FusionChartDesigner fcd = new FusionChartDesigner();
		setFusionChartSettings(fcd); 
		String chartXml = "";
		try {
			List<Map> data = new ArrayList<Map>();
			// 转化成JSONArray对象
			if(jsonString!=null &&jsonString!=""){				
				JSONArray jsonArray = JSONArray.fromObject(jsonString);  
				for (Object obj : jsonArray) { 
					JSONObject jsonObject = (JSONObject) obj;
					Map toMap = (Map) jsonObject;
					data = resourceStatisticManagerService.getStatistic(toMap,type);
					fcd.setXListFromMultiDataList(data, type);
					fcd.addSerialDatasList(toMap.get("text").toString(), data, type, "count"); 
				}
			} 
			chartXml = fcd.getManyDimensionsXmlData();
			xmlMap.put("xml", chartXml);
			Object obj = (Object)xmlMap;
			resultObj = JSONObject.fromObject(obj);
			return RESULT_OBJ;
		} catch (CommonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return RESULT_OBJ;
	}
		
	public void setFusionChartSettings(FusionChartDesigner fcd_1){
		fcd_1.setChart_caption("");
		fcd_1.setChart_xAxisName("");
		fcd_1.setChart_yAxisName("");
		fcd_1.setChart_bgcolor("#F3f3f3"); 
		fcd_1.setChart_showValues("0");
		fcd_1.getChartNodePro().put("baseFontSize","12");
		fcd_1.setLabelDisplay("WRAP");
		fcd_1.setChartClickEnable(false);
		fcd_1.setChart_show_legend("1");
		fcd_1.setChart_legend_position("right");
		fcd_1.setChart_percent_label("1");
		fcd_1.setChart_Decimals("2");   
	}
	
	public String getJsonString() {
		return jsonString;
	}  
	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}  
	public String getType() {
		return type;
	} 
	public void setType(String type) {
		this.type = type;
	} 
}
