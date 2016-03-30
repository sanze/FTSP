package com.fujitsu.manager.reportManager.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.fujitsu.IService.ICommonManagerService;
import com.fujitsu.IService.IExportExcel;
import com.fujitsu.IService.IAlarmManagementService;
import com.fujitsu.IService.IMethodLog;
import com.fujitsu.IService.IReportManagerService;
import com.fujitsu.abstractAction.DownloadAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.util.ExportExcelUtil;
import com.mongodb.DBObject;
import fusioncharts.FusionChartDesigner;
public class ReportAction extends DownloadAction {

	private static final long serialVersionUID = 1L;
	private static final String TABLE_PRI="t_pm_origi_data_collect";
	private static final String TABLE_PRI_MONTH="t_pm_origi_data_collect_month";
	private static final String COLOR_CRITICAL="ff0000";
	private static final String COLOR_MAJOR="ff8000";
	private static final String COLOR_MINOR="ffff00";
	private static final String COLOR_WARNING="800000";
	@Resource
	public IReportManagerService reportManagerService;
	@Resource
	public ICommonManagerService commonManagerService;
	@Resource
	public IAlarmManagementService alarmManagementService;
	// 表名
	public String table_name = "";
	private boolean displayAll;
	private boolean displayNone;
	private String query;
	int nodeId;
	int nodeLevel;
	int endId;
	int endLevel;
	String text;
	boolean hasPath;
	private String level;
	private Map<String, String> condMap;
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public int getNodeId() {
		return nodeId;
	}
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
	public int getNodeLevel() {
		return nodeLevel;
	}
	public void setNodeLevel(int nodeLevel) {
		this.nodeLevel = nodeLevel;
	}
	public int getEndId() {
		return endId;
	}
	public void setEndId(int endId) {
		this.endId = endId;
	}
	public int getEndLevel() {
		return endLevel;
	}
	public void setEndLevel(int endLevel) {
		this.endLevel = endLevel;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public boolean isHasPath() {
		return hasPath;
	}
	public void setHasPath(boolean hasPath) {
		this.hasPath = hasPath;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public boolean isDisplayAll() {
		return displayAll;
	}
	public void setDisplayAll(boolean displayAll) {
		this.displayAll = displayAll;
	}
	public boolean isDisplayNone() {
		return displayNone;
	}
	public void setDisplayNone(boolean displayNone) {
		this.displayNone = displayNone;
	}
	public String getTable_name() {
		return table_name;
	}
	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}
	// 查询参数
	private String jsonString;
	public String getJsonString() {
		return jsonString;
	}
	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}

	/**
	 * Method name: getResourceChartByStation <BR>
	 * Description: 按局站查询资源统计信息<BR>
	 * Remark: 2013-12-05<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@IMethodLog(desc = "按局站查询资源统计信息")
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String getResourceChartByStation(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			// 将查询结果转成FusionCharts所需格式
			String xmlString = "";
			FusionChartDesigner fcd  = new FusionChartDesigner();
			// 按局站查询资源统计信息
			if(paramMap.get("stationId")!=null){
				// 判断是页面上那个chart图
				if("left".equals(paramMap.get("flag").toString())){
					// 需要统计的局站ID和名称
					String[] stationIdSize = paramMap.get("stationId").toString().split(",");
					String[] stationNameSize = paramMap.get("stationName").toString().split(",");
					List<Map> mdListMap = new ArrayList<Map>();
					for (int i = 0; i < stationIdSize.length; i++) {
						Map map = new HashMap();
						map.put("RESOURCE_STATION_ID", Integer.parseInt(stationIdSize[i]));
						mdListMap.add(map);
					}
					// 按网元型号查询某局站的资源统计信息
					List<Map> resourceList = reportManagerService.getResourceChartByStationAndNeModel(paramMap);
					// 行专列
					rowToCol(resourceList, mdListMap, "NE_MODEL", "COUNT", "RESOURCE_STATION_ID");
					for(int i=0;i<mdListMap.size();i++){
						fcd.addSerialDatasList(stationNameSize[i], (ArrayList<Map>) resourceList, "NE_MODEL", mdListMap.get(i).get("RESOURCE_STATION_ID").toString());
					}
				}else{
					// 查询某些局站下的所有网元型号
					List<Map> neModelList = reportManagerService.getAllNeModelsByStation(paramMap);
					String neModel = "";
					if(neModelList.size()>0){
						for (int i = 0; i < neModelList.size(); i++) {
							neModel += neModelList.get(i).get("NE_MODEL").toString()+",";
						}
						neModel = neModel.substring(0,neModel.lastIndexOf(","));
						paramMap.put("neModel", neModel);
					}
					List<Map> resourceList = reportManagerService.getResourceChartByNeModelAndStation(paramMap);
					// 行专列
					rowToCol(resourceList, neModelList, "RESOURCE_STATION_ID", "COUNT", "NE_MODEL");
					for(int i=0;i<neModelList.size();i++){
						fcd.addSerialDatasList(neModelList.get(i).get("NE_MODEL").toString(), (ArrayList<Map>) resourceList, "STATION_NAME", neModelList.get(i).get("NE_MODEL").toString());
					}
				}
			}
			xmlString = fcd.getManyDimensionsXmlData("","","");
			// 将封装成Map格式,用来转成JSON，返回前台
			Map<String, Object> valueMap = new HashMap<String, Object>();
			valueMap.put("xml", xmlString);
			resultObj = JSONObject.fromObject(valueMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
     * 
     * Method name: rowToCol <BR>
     * Description: rowToCol <BR>
     * Remark: <BR>
     * @param list<Map> 所有集合  
     * @param mdListMap 所需要行转列的信息 NET_TYPE:移动，NET_TYPE:联通，NET_TYPE:电信
     * @param fcXKey    对应要配置在x轴的字段
     * @param fcYKey    对应要配置在y周的字段
     * @param dType     mdListMap中的key  
     */

    private void rowToCol(List<Map> list, List<Map> mdListMap, String fcXKey, String fcYKey, String dType){
        Map map = new HashMap();
        int mdSizeFlag = 0;
        Map rowMap1 = null;
        Map rowMap2 = null;
        String xValue = "";
        Set set = new HashSet();
        for (int i = 0; i < list.size(); i++) {
           mdSizeFlag = 1;
           rowMap1 = list.get(i);
           xValue = rowMap1.get(fcXKey).toString();
           for (int j = 0; j <mdListMap.size(); j++) {
               Map mdRowMap = mdListMap.get(j);
               rowMap1.put(mdRowMap.get(dType).toString(),0);
           }
           rowMap1.put(rowMap1.get(dType).toString(),rowMap1.get(fcYKey).toString());
           set.add(rowMap1.get(dType).toString());
           for (int j = i + 1; j < list.size(); j++) {
               rowMap2 = list.get(j);
               if (xValue.equals(rowMap2.get(fcXKey).toString())) {
                   rowMap1.put(rowMap2.get(dType).toString(),rowMap2.get(fcYKey).toString());
                  set.add(rowMap2.get(dType).toString());
                  list.remove(j);
                  --j;
                  if (++mdSizeFlag == mdListMap.size()) {
                      break;
                  }
               }
           }
        }
    }
	/**
	 * Method name: getResourceDetailByStation <BR>
	 * Description: 按局站查询资源详细信息<BR>
	 * Remark: 2013-12-06<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	@IMethodLog(desc = "按局站查询资源详细信息")
	@SuppressWarnings("unchecked")
	public String getResourceDetailByStation(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			// 定义一个Map接受查询返回的值
			Map<String, Object> resotrceMap = reportManagerService.getResourceDetailByStation(paramMap,start,limit);
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(resotrceMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: getEmsGroupInfo_Resource <BR>
	 * Description: 资源报表--按网管分组查询<BR>
	 * Remark: 2013-12-02<BR>
	 * @author YuanJia
	 * @return List<Map<String, Object>><BR>
	 */
	@IMethodLog(desc = "查询getEmsGroupInfo")
	@SuppressWarnings("unchecked")
	public String getEmsGroupInfo_Resource(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			Map<String, Object> resultMap = new HashMap<String, Object>();
			
			resultMap = reportManagerService.getEmsGroupInfo_Resource(paramMap,start,limit);
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(resultMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: emsGroupFusionChart_Resource <BR>
	 * Description: 资源报表--fusionChart<BR>
	 * Remark: 2013-12-03<BR>
	 * @author YuanJia
	 * @return List<Map<String, Object>><BR>
	 */
	@SuppressWarnings("unchecked")
	public String emsGroupFusionChart_Resource() throws CommonException{
		// 将参数专程JSON对象
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		// 将JSON对象转成Map对象
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap = (Map<String, Object>) jsonObject;
		List<Map<String,Object>> List = reportManagerService.getEmsGroupFusionChart_Resource(paramMap);
		/** 设置fusionChart */
		String chartXml = "";
		ArrayList<Map> xmlList = new ArrayList<Map>(); 
		for(int i=0; i<List.size();i++){
			Map lom = List.get(i);
			xmlList.add((Map)lom);
		}
		FusionChartDesigner fcd = new FusionChartDesigner();  
		fcd.setOneDimensionList(xmlList, "X", "Y");
		fcd.setChart_bgcolor("#F3f3f3");
		fcd.setChart_caption("按网管分组统计");
		fcd.setChart_xAxisName("");
		fcd.setChart_yAxisName("");
		fcd.setChartClickEnable(true);
		chartXml = fcd.getOneDimensionsXMLData();
		Map<String,String> xmlMap = new HashMap<String,String>();
		xmlMap.put("xml", chartXml);
		Object obj = (Object)xmlMap;
		resultObj = JSONObject.fromObject(obj);
		return RESULT_OBJ;
		
	}
	
	/**
	 * Method name: getEmsInfo_Resource <BR>
	 * Description: 资源报表--按网管分组查询<BR>
	 * Remark: 2013-12-02<BR>
	 * @author YuanJia
	 * @return List<Map<String, Object>><BR>
	 */
	@IMethodLog(desc = "查询getEmsGroupInfo")
	@SuppressWarnings("unchecked")
	public String getEmsInfo_Resource(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			Map<String, Object> resultMap = new HashMap<String, Object>();
			
			resultMap = reportManagerService.getEmsInfo_Resource(paramMap,start,limit);
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(resultMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: emsFusionChart_Resource <BR>
	 * Description: 资源报表--fusionChart<BR>
	 * Remark: 2013-12-03<BR>
	 * @author YuanJia
	 * @return List<Map<String, Object>><BR>
	 */
	@SuppressWarnings("unchecked")
	public String emsFusionChart_Resource() throws CommonException{
		// 将参数专程JSON对象
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		// 将JSON对象转成Map对象
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap = (Map<String, Object>) jsonObject;
		List<Map<String,Object>> List = reportManagerService.getEmsFusionChart_Resource(paramMap);
		/** 设置fusionChart */
		String chartXml = "";
		ArrayList<Map> xmlList = new ArrayList<Map>(); 
		for(int i=0; i<List.size();i++){
			Map lom = List.get(i);
			xmlList.add((Map)lom);
		}
		FusionChartDesigner fcd = new FusionChartDesigner();  
		fcd.setOneDimensionList(xmlList, "X", "Y");
		fcd.setChart_bgcolor("#F3f3f3");
		fcd.setChart_caption("");
		fcd.setChart_xAxisName("");
		fcd.setChart_yAxisName("");
		//fcd.setChart_rotateLabels("1");
		//fcd.setChart_slantLabels("1");
		//fcd.getChartNodePro().put("maxColWidth","30");
		fcd.getChartNodePro().put("baseFontSize","12");
		fcd.setLabelDisplay("WRAP");
		fcd.setChartClickEnable(true);
		chartXml = fcd.getOneDimensionsXMLData();
		Map<String,String> xmlMap = new HashMap<String,String>();
		xmlMap.put("xml", chartXml);
		Object obj = (Object)xmlMap;
		resultObj = JSONObject.fromObject(obj);
		return RESULT_OBJ;
		
	}
	
	/**
	 * Method name: getEmsGroupInfo_Performance <BR>
	 * Description: 性能报表--按网管分组查询<BR>
	 * Remark: 2013-12-11<BR>
	 * @author YuanJia
	 * @return List<Map<String, Object>><BR>
	 */
	@IMethodLog(desc = "查询getEmsGroupInfo")
	@SuppressWarnings("unchecked")
	public String getEmsGroupInfo_Performance(){
		try {
			Map<String, Object> paramMap = (Map<String, Object>)JSONObject.fromObject(jsonString);
			String timeType=(String)paramMap.get("timeType");
			if("day".equals(timeType)){
				setTable_name(TABLE_PRI);
			}else{
				setTable_name(TABLE_PRI+"_month");
			}
			paramMap.put("table_name",table_name);
			paramMap.put("timeType", paramMap.get("timeType"));
			paramMap.put("time",paramMap.get("time").toString());
			paramMap.put("query","list");
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap = reportManagerService.getEmsGroupInfo_Performance(paramMap,start,limit);
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(resultMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	
	
	public void setFusionChartSettings(FusionChartDesigner fcd_1){
		fcd_1.setChart_caption("");
		fcd_1.setChart_xAxisName("");
		fcd_1.setChart_yAxisName("");
		fcd_1.setChart_bgcolor("#F3f3f3");
		//fcd_1.setChart_bgcolor("#ffffff");
		fcd_1.setChart_showValues("1");
		fcd_1.getChartNodePro().put("baseFontSize","12");
		fcd_1.setLabelDisplay("WRAP");
		fcd_1.setChartClickEnable(true);
		fcd_1.setChart_show_legend("1");
		fcd_1.setChart_legend_position("right");
		fcd_1.setChart_percent_label("1");
		fcd_1.setChart_Decimals("2");
		
		fcd_1.getChartNodePro().put("pieRadius","150");//饼状图半径控制
		//fcd_1.getChartNodePro().put("maxColWidth","30");
		
		fcd_1.getChartNodePro().put("showAboutMenuItem","0");
		fcd_1.getChartNodePro().put("showPrintMenuItem","0");
	}
	
	
	/**
	 * Method name: fusionChart_Performance <BR>
	 * Description: 性能报表--fusionChart<BR>
	 * Remark: 2013-12-11<BR>
	 * @author YuanJia
	 * @return List<Map<String, Object>><BR>
	 */
	@SuppressWarnings("unchecked")
	public String fusionChart_Performance() throws CommonException{
		Map<String, Object> paramMap = (Map<String, Object>)JSONObject.fromObject(jsonString);
		String timeType=paramMap.get("timeType").toString();
		if("day".equals(timeType)){
			paramMap.put("table_name","t_pm_origi_data_collect");
		}else{
			paramMap.put("table_name","t_pm_origi_data_collect_month");
		}
		paramMap.put("timeType",timeType);
		paramMap.put("time",paramMap.get("time").toString());
		Map<String,String> xmlMap = new HashMap<String,String>();
		level=paramMap.get("level")==null?"":paramMap.get("level").toString();
		if(level==null || "".equals(level)){//生成第一层图表
			String chartXml_1 = "";
			Map<String, Object> map = reportManagerService.getEmsGroupInfo_Performance(paramMap,start,limit);	
			List<Map> List_1=(List<Map>)map.get("rows");
			List emsGroupIdList = new ArrayList<String>();
			for (Map m : List_1) {
				emsGroupIdList.add(m.get("base_ems_group_id"));
			}
			FusionChartDesigner fcd_1 = new FusionChartDesigner();  
			setFusionChartSettings(fcd_1);
			fcd_1.setxListOneDimensionAdditionnal(emsGroupIdList);
			fcd_1.setOneDimensionList(List_1, "group_name", "cou");
			fcd_1.getUserDefinedProperty().put("level","one");
			fcd_1.setChart_caption(paramMap.get("time").toString()+"异常性能按网管分组统计");
			chartXml_1 = fcd_1.getOneDimensionsXMLData();
			xmlMap.put("xml_1", chartXml_1);
		}else if(level!=null && "one".equals(level)){//点击第一层图表进来
			String chartXml_1 = "";
			Map<String, Object> map=null;
			if("year".equals(timeType)){
				map = reportManagerService.getPMMonthDataByEmsGroup(paramMap);
				List<Map> List_1=(List<Map>)map.get("rows");
				FusionChartDesigner fcd_1 = new FusionChartDesigner();  
				setFusionChartSettings(fcd_1);
				fcd_1.setChart_showValues("0");
				fcd_1.addSerialDatasList("计数值3级",(ArrayList)List_1,"retrieval_time","count_3");
				fcd_1.addSerialDatasList("计数值2级",(ArrayList)List_1,"retrieval_time","count_2");
				fcd_1.addSerialDatasList("计数值1级",(ArrayList)List_1,"retrieval_time","count_1");
				fcd_1.addSerialDatasList("物理量3级",(ArrayList)List_1,"retrieval_time","physics_3");
				fcd_1.addSerialDatasList("物理量2级",(ArrayList)List_1,"retrieval_time","physics_2");
				fcd_1.addSerialDatasList("物理量1级",(ArrayList)List_1,"retrieval_time","physics_1");
				fcd_1.getUserDefinedProperty().put("level","two");
				fcd_1.getUserDefinedProperty().put("emsId",paramMap.get("group_name"));
				fcd_1.setChart_caption(paramMap.get("time").toString()+paramMap.get("label")+"网管组异常性能分布");
				chartXml_1 = fcd_1.getManyDimensionsXmlData();
				xmlMap.put("xml_1", chartXml_1);
			}else if("month".equals(timeType)){
				paramMap.put("query_month",paramMap.get("time").toString());
				map = reportManagerService.getPMDayDataByEmsGroup(paramMap);
				List<Map> List_1=(List<Map>)map.get("rows");
				FusionChartDesigner fcd_1 = new FusionChartDesigner();  
				setFusionChartSettings(fcd_1);
				fcd_1.setChart_showValues("0");
				fcd_1.addSerialDatasList("计数值3级",(ArrayList)List_1,"retrieval_time","count_3");
				fcd_1.addSerialDatasList("计数值2级",(ArrayList)List_1,"retrieval_time","count_2");
				fcd_1.addSerialDatasList("计数值1级",(ArrayList)List_1,"retrieval_time","count_1");
				fcd_1.addSerialDatasList("物理量3级",(ArrayList)List_1,"retrieval_time","physics_3");
				fcd_1.addSerialDatasList("物理量2级",(ArrayList)List_1,"retrieval_time","physics_2");
				fcd_1.addSerialDatasList("物理量1级",(ArrayList)List_1,"retrieval_time","physics_1");
				fcd_1.getUserDefinedProperty().put("level","two");
				fcd_1.getUserDefinedProperty().put("emsId",paramMap.get("group_name"));
				fcd_1.setChart_caption(paramMap.get("time").toString()+paramMap.get("label")+"网管组异常性能分布");
				chartXml_1 = fcd_1.getManyDimensionsXmlData();
				xmlMap.put("xml_1", chartXml_1);
			}else if("day".equals(timeType)){
				paramMap.put("query_day",paramMap.get("time").toString());
				map = reportManagerService.getPMDayDataByQueryDayAndGroup(paramMap);
				List<Map> List_1=(List<Map>)map.get("rows");
				FusionChartDesigner fcd_1 = new FusionChartDesigner();  
				setFusionChartSettings(fcd_1);
				fcd_1.setOneDimensionList(List_1, "type_level", "cou");
				fcd_1.getUserDefinedProperty().put("level","three");
				fcd_1.setChart_caption(paramMap.get("query_day").toString()+paramMap.get("label")+"网管组异常性能分布");
				chartXml_1 = fcd_1.getOneDimensionsXMLData();
				xmlMap.put("xml_1", chartXml_1);
			}
		}else if(level!=null && "two".equals(level)){//点击第二层图表进来
			String chartXml_1 = "";
			Map<String, Object> map=null;
			if("year".equals(timeType)){
				paramMap.put("query_month",paramMap.get("label"));
				map = reportManagerService.getPMDayDataByEmsGroup(paramMap);
				List<Map> List_1=(List<Map>)map.get("rows");
				FusionChartDesigner fcd_1 = new FusionChartDesigner();  
				setFusionChartSettings(fcd_1);
				fcd_1.setChart_showValues("0");
				fcd_1.addSerialDatasList("计数值3级",(ArrayList)List_1,"retrieval_time","count_3");
				fcd_1.addSerialDatasList("计数值2级",(ArrayList)List_1,"retrieval_time","count_2");
				fcd_1.addSerialDatasList("计数值1级",(ArrayList)List_1,"retrieval_time","count_1");
				fcd_1.addSerialDatasList("物理量3级",(ArrayList)List_1,"retrieval_time","physics_3");
				fcd_1.addSerialDatasList("物理量2级",(ArrayList)List_1,"retrieval_time","physics_2");
				fcd_1.addSerialDatasList("物理量1级",(ArrayList)List_1,"retrieval_time","physics_1");
				fcd_1.getUserDefinedProperty().put("level","three");
				fcd_1.setChart_caption(paramMap.get("label").toString()+paramMap.get("gName")+"网管组异常性能分布");
				chartXml_1 = fcd_1.getManyDimensionsXmlData();
				xmlMap.put("xml_1", chartXml_1);
			}else if("month".equals(timeType)){
				paramMap.put("group_name",paramMap.get("group_name"));
				paramMap.put("query_day",paramMap.get("label"));
				map = reportManagerService.getPMDayDataByQueryDayAndGroup(paramMap);
				List<Map> List_1=(List<Map>)map.get("rows");
				FusionChartDesigner fcd_1 = new FusionChartDesigner();  
				setFusionChartSettings(fcd_1);
				fcd_1.setOneDimensionList(List_1, "type_level", "cou");
				fcd_1.getUserDefinedProperty().put("level","three");
				fcd_1.setChart_caption(paramMap.get("label").toString()+paramMap.get("gName")+"网管组异常性能分布");
				chartXml_1 = fcd_1.getOneDimensionsXMLData();
				xmlMap.put("xml_1", chartXml_1);
			}
		}
		Object obj = (Object)xmlMap;
		resultObj = JSONObject.fromObject(obj);
		return RESULT_OBJ;
	}
	
	
	
	
	
	@SuppressWarnings("unchecked")
	public String emsFusionChart_Alarm() throws CommonException{
		Map<String, Object> paramMap = (Map<String, Object>)JSONObject.fromObject(jsonString);
		String timeType=paramMap.get("timeType").toString();
		paramMap.put("timeType",timeType);
		paramMap.put("time",paramMap.get("time").toString());
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String,String> xmlMap = new HashMap<String,String>();
		level=paramMap.get("level")==null?"":paramMap.get("level").toString();
		if(level==null || "".equals(level)){//生成第一层图表
			resultMap = reportManagerService.getReportAlarmByEms(paramMap); 
			List<Map> List_1=(List<Map>)resultMap.get("rows");
			List emsIdList = new ArrayList<String>();
			for (Map m : List_1) {
				emsIdList.add(m.get("base_ems_connection_id"));
			}
			String chartXml_1 = "";
			FusionChartDesigner fcd_1 = new FusionChartDesigner();  
			setFusionChartSettings(fcd_1);
			fcd_1.setxListOneDimensionAdditionnal(emsIdList);
			fcd_1.setOneDimensionList(List_1, "display_name", "cou");
			fcd_1.getUserDefinedProperty().put("level","one");
			fcd_1.setChart_caption(paramMap.get("time").toString()+"告警按网管统计");
			chartXml_1 = fcd_1.getOneDimensionsXMLData();
			xmlMap.put("xml_1", chartXml_1);
		}else if(level!=null && "one".equals(level)){//点击第一层图表进来
			String chartXml_1 = "";
			Map<String, Object> map=null;
			if("year".equals(timeType)){
				map =reportManagerService.getAlarmMonthDataByEms(paramMap);
				List<Map> List_1=(List<Map>)map.get("rows");
				FusionChartDesigner fcd_1 = new FusionChartDesigner();  
				setFusionChartSettings(fcd_1);
				fcd_1.setChart_showValues("0");
				fcd_1.addSerialDatasList("提示",COLOR_WARNING,(ArrayList)List_1,"first_time","ps_warning");
				fcd_1.addSerialDatasList("一般",COLOR_MINOR,(ArrayList)List_1,"first_time","ps_minor");
				fcd_1.addSerialDatasList("重要",COLOR_MAJOR,(ArrayList)List_1,"first_time","ps_major");
				fcd_1.addSerialDatasList("紧急",COLOR_CRITICAL,(ArrayList)List_1,"first_time","ps_critical");
				fcd_1.getUserDefinedProperty().put("level","two");
				fcd_1.getUserDefinedProperty().put("emsId",paramMap.get("EMS_NAME"));
				fcd_1.setChart_caption(paramMap.get("time").toString()+paramMap.get("label")+"告警分布");
				chartXml_1 = fcd_1.getManyDimensionsXmlData();
				xmlMap.put("xml_1", chartXml_1);
			}else if("month".equals(timeType)){
				paramMap.put("query_month",paramMap.get("time").toString());
				map = reportManagerService.getAlarmDayDataByEms(paramMap);
				List<Map> List_1=(List<Map>)map.get("rows");
				FusionChartDesigner fcd_1 = new FusionChartDesigner();  
				setFusionChartSettings(fcd_1);
				fcd_1.setChart_showValues("0");
				fcd_1.addSerialDatasList("提示",COLOR_WARNING,(ArrayList)List_1,"first_time","ps_warning");
				fcd_1.addSerialDatasList("一般",COLOR_MINOR,(ArrayList)List_1,"first_time","ps_minor");
				fcd_1.addSerialDatasList("重要",COLOR_MAJOR,(ArrayList)List_1,"first_time","ps_major");
				fcd_1.addSerialDatasList("紧急",COLOR_CRITICAL,(ArrayList)List_1,"first_time","ps_critical");
				fcd_1.getUserDefinedProperty().put("level","two");
				fcd_1.getUserDefinedProperty().put("emsId",paramMap.get("EMS_NAME"));
				fcd_1.setChart_caption(paramMap.get("time").toString()+paramMap.get("label")+"告警分布");
				chartXml_1 = fcd_1.getManyDimensionsXmlData();
				xmlMap.put("xml_1", chartXml_1);
			}else if("day".equals(timeType)){
				paramMap.put("query_day",paramMap.get("time").toString());
				map = reportManagerService.getAlarmDayDataByQueryDayAndEms(paramMap);
				List<Map> List_1=(List<Map>)map.get("rows");
				FusionChartDesigner fcd_1 = new FusionChartDesigner();  
				setFusionChartSettings(fcd_1);
				fcd_1.setOneDimensionList(List_1, "type_level", "cou");
				fcd_1.getUserDefinedProperty().put("level","three");
				fcd_1.setChart_caption(paramMap.get("query_day").toString()+paramMap.get("label")+"告警分布");
				chartXml_1 = fcd_1.getOneDimensionsXMLData();
				xmlMap.put("xml_1", chartXml_1);
			}
		}else if(level!=null && "two".equals(level)){//点击第二层图表进来
			String chartXml_1 = "";
			Map<String, Object> map=null;
			if("year".equals(timeType)){
				paramMap.put("query_month",paramMap.get("label"));
				map = reportManagerService.getAlarmDayDataByEms(paramMap);
				List<Map> List_1=(List<Map>)map.get("rows");
				FusionChartDesigner fcd_1 = new FusionChartDesigner();  
				setFusionChartSettings(fcd_1);
				fcd_1.setChart_showValues("0");
				fcd_1.addSerialDatasList("提示",COLOR_WARNING,(ArrayList)List_1,"first_time","ps_warning");
				fcd_1.addSerialDatasList("一般",COLOR_MINOR,(ArrayList)List_1,"first_time","ps_minor");
				fcd_1.addSerialDatasList("重要",COLOR_MAJOR,(ArrayList)List_1,"first_time","ps_major");
				fcd_1.addSerialDatasList("紧急",COLOR_CRITICAL,(ArrayList)List_1,"first_time","ps_critical");
				fcd_1.getUserDefinedProperty().put("level","three");
				fcd_1.setChart_caption(paramMap.get("label").toString()+paramMap.get("eName")+"告警分布");
				chartXml_1 = fcd_1.getManyDimensionsXmlData();
				xmlMap.put("xml_1", chartXml_1);
			}else if("month".equals(timeType)){
				paramMap.put("query_day",paramMap.get("label"));
				map = reportManagerService.getAlarmDayDataByQueryDayAndEms(paramMap);
				List<Map> List_1=(List<Map>)map.get("rows");
				FusionChartDesigner fcd_1 = new FusionChartDesigner();  
				setFusionChartSettings(fcd_1);
				fcd_1.setOneDimensionList(List_1, "type_level", "cou");
				fcd_1.getUserDefinedProperty().put("level","three");
				fcd_1.setChart_caption(paramMap.get("query_day").toString()+paramMap.get("eName")+"告警分布");
				chartXml_1 = fcd_1.getOneDimensionsXMLData();
				xmlMap.put("xml_1", chartXml_1);
			}
		}
		Object obj = (Object)xmlMap;
		resultObj = JSONObject.fromObject(obj);
		return RESULT_OBJ;
		
		
	}
	
	
	
	/**
	 * Method name: getEmsInfo_Performance <BR>
	 * Description: 性能报表--按网管查询<BR>
	 * Remark: 2013-12-11<BR>
	 * @author YuanJia
	 * @return List<Map<String, Object>><BR>
	 */
	@IMethodLog(desc = "查询getEmsInfo")
	@SuppressWarnings("unchecked")
	public String getEmsInfo_Performance(){
//		try {
//			// 将参数专程JSON对象
//			JSONObject jsonObject = JSONObject.fromObject(jsonString);
//			// 将JSON对象转成Map对象
//			Map<String, Object> paramMap = new HashMap<String, Object>();
//			paramMap = (Map<String, Object>) jsonObject;
//			setTable_name(TABLE_PRI+paramMap.get("time").toString().substring(0,4));
//			paramMap.put("table_name",table_name);
//			paramMap.put("timeType", paramMap.get("timeType"));
//			paramMap.put("time",paramMap.get("time").toString());
//			paramMap.put("query","list");
//			Map<String, Object> resultMap = new HashMap<String, Object>();
//			resultMap = reportManagerService.getEmsInfo_Performance(paramMap,start,limit);
//			// 将返回的结果转成JSON对象，返回前台
//			resultObj = JSONObject.fromObject(resultMap);
//		} catch (CommonException e) {
//			result.setReturnResult(CommonDefine.FAILED);
//			result.setReturnMessage(e.getErrorMessage());
//			resultObj = JSONObject.fromObject(result);
//		}
		return RESULT_OBJ;
	}
	

	/**
	 * Method name: emsFusionChart_Performance <BR>
	 * Description: 性能报表--fusionChart<BR>
	 * Remark: 2013-12-11<BR>
	 * 
	 * @author YuanJia
	 * @return List<Map<String, Object>><BR>
	 */
	@SuppressWarnings("unchecked")
	public String emsFusionChart_Performance() throws CommonException {
		String timeType = condMap.get("timeType").toString();
		Map<String, String> xmlMap = new HashMap<String, String>();
		level = condMap.get("level") == null ? "" : condMap.get("level")
				.toString();
		String title = "";
		try {
			SimpleDateFormat msdf = new SimpleDateFormat("yyyy年MM月");
			SimpleDateFormat dsdf = new SimpleDateFormat("yyyy年MM月dd日");
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM");
			if (condMap.get("timeType").equals("day")){
				Date time = sdf1.parse(condMap.get("time"));
				title = dsdf.format(time);
				}
			else if (condMap.get("timeType").equals("month")){
				Date time = sdf2.parse(condMap.get("time"));
				title = msdf.format(time);
			}
			else if (condMap.get("timeType").equals("year")){
				title = condMap.get("time")+"年";
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (level == null || "".equals(level)) {// 生成第一层图表
			String flag = condMap.get("firstGraph").toString();
			if ("1".equals(flag)) {
				String chartXml_1 = "";
				String chartXml_2 = "";
				if (condMap.get("timeType").equals("day"))
					condMap.put("table_name", TABLE_PRI);
				else
					condMap.put("table_name", TABLE_PRI_MONTH);
				condMap.put("query", "chart");
				Map<String, Object> map = reportManagerService
						.getEmsInfo_Performance(condMap, start, limit); // 获取fusion_01数据源
				List<Map> List_1 = (List<Map>) map.get("rows");
				List emsIdList = new ArrayList<String>();
				for (Map m : List_1) {
					emsIdList.add(m.get("BASE_EMS_CONNECTION_ID"));
				}
				FusionChartDesigner fcd_1 = new FusionChartDesigner();
				setFusionChartSettings(fcd_1);
				// 需要emsId，因为网管名有可能重复
				fcd_1.setxListOneDimensionAdditionnal(emsIdList);
				fcd_1.setOneDimensionList(List_1, "DISPLAY_NAME", "COU");
				fcd_1.getUserDefinedProperty().put("level", "one");
				fcd_1.setChart_caption(title + "异常性能按网管统计");
				chartXml_1 = fcd_1.getOneDimensionsXMLData();
//				System.out.println(chartXml_1);
				xmlMap.put("xml_1", chartXml_1);
			}
		} else if (level != null && "one".equals(level)) {// 点击第一层图表进来
			String chartXml_1 = "";
			Map<String, Object> map = null;
			if ("year".equals(timeType)) {
				map = reportManagerService.getPMDataPerMonthByEmsId(condMap);
				List<Map> List_1 = (List<Map>) map.get("rows");
				FusionChartDesigner fcd_1 = new FusionChartDesigner();
				setFusionChartSettings(fcd_1);
				fcd_1.setChart_showValues("0");
				fcd_1.addSerialDatasList("计数值3级", (ArrayList) List_1,
						"retrieval_time", "count_3");
				fcd_1.addSerialDatasList("计数值2级", (ArrayList) List_1,
						"retrieval_time", "count_2");
				fcd_1.addSerialDatasList("计数值1级", (ArrayList) List_1,
						"retrieval_time", "count_1");
				fcd_1.addSerialDatasList("物理量3级", (ArrayList) List_1,
						"retrieval_time", "physics_3");
				fcd_1.addSerialDatasList("物理量2级", (ArrayList) List_1,
						"retrieval_time", "physics_2");
				fcd_1.addSerialDatasList("物理量1级", (ArrayList) List_1,
						"retrieval_time", "physics_1");
				fcd_1.getUserDefinedProperty().put("level", "two");
				fcd_1.getUserDefinedProperty().put("emsId",
						condMap.get("emsId"));
				fcd_1.setChart_caption(title + condMap.get("label") + "异常性能分布");
				chartXml_1 = fcd_1.getManyDimensionsXmlData();
				xmlMap.put("xml_1", chartXml_1);
			} else if ("month".equals(timeType)) {
				map = reportManagerService.getPMDataPerMonthByEmsId(condMap);
				List<Map> List_1 = (List<Map>) map.get("rows");
				FusionChartDesigner fcd_1 = new FusionChartDesigner();
				setFusionChartSettings(fcd_1);
				fcd_1.setChart_showValues("0");
				fcd_1.addSerialDatasList("计数值3级", (ArrayList) List_1,
						"retrieval_time", "count_3");
				fcd_1.addSerialDatasList("计数值2级", (ArrayList) List_1,
						"retrieval_time", "count_2");
				fcd_1.addSerialDatasList("计数值1级", (ArrayList) List_1,
						"retrieval_time", "count_1");
				fcd_1.addSerialDatasList("物理量3级", (ArrayList) List_1,
						"retrieval_time", "physics_3");
				fcd_1.addSerialDatasList("物理量2级", (ArrayList) List_1,
						"retrieval_time", "physics_2");
				fcd_1.addSerialDatasList("物理量1级", (ArrayList) List_1,
						"retrieval_time", "physics_1");
				fcd_1.getUserDefinedProperty().put("level", "two");
				fcd_1.getUserDefinedProperty().put("emsId",
						condMap.get("emsId"));
				fcd_1.setChart_caption(title + condMap.get("label") + "异常性能分布");
				chartXml_1 = fcd_1.getManyDimensionsXmlData();
				xmlMap.put("xml_1", chartXml_1);
			} else if ("day".equals(timeType)) {
				condMap.put("query_day", condMap.get("time").toString());
				map = reportManagerService.getPMDataPerDayEms(condMap);
				List<Map> List_1 = (List<Map>) map.get("rows");
				FusionChartDesigner fcd_1 = new FusionChartDesigner();
				setFusionChartSettings(fcd_1);
				fcd_1.setOneDimensionList(List_1, "type_level", "cou");
				fcd_1.getUserDefinedProperty().put("level", "three");
				fcd_1.setChartClickEnable(false);
				fcd_1.setChart_caption(title + condMap.get("label") + "异常性能分布");
				chartXml_1 = fcd_1.getOneDimensionsXMLData();
				xmlMap.put("xml_1", chartXml_1);
			}
		} else if (level != null && "two".equals(level)) {// 点击第二层图表进来
			String chartXml_1 = "";
			Map<String, Object> map = null;
			if ("year".equals(timeType)) {
				condMap.put("query_month", condMap.get("label"));
				map = reportManagerService.getPMDataPerDayByEmsId(condMap);
				List<Map> List_1 = (List<Map>) map.get("rows");
				FusionChartDesigner fcd_1 = new FusionChartDesigner();
				setFusionChartSettings(fcd_1);
				fcd_1.setChart_showValues("0");
				fcd_1.addSerialDatasList("计数值3级", (ArrayList) List_1,
						"retrieval_time", "count_3");
				fcd_1.addSerialDatasList("计数值2级", (ArrayList) List_1,
						"retrieval_time", "count_2");
				fcd_1.addSerialDatasList("计数值1级", (ArrayList) List_1,
						"retrieval_time", "count_1");
				fcd_1.addSerialDatasList("物理量3级", (ArrayList) List_1,
						"retrieval_time", "physics_3");
				fcd_1.addSerialDatasList("物理量2级", (ArrayList) List_1,
						"retrieval_time", "physics_2");
				fcd_1.addSerialDatasList("物理量1级", (ArrayList) List_1,
						"retrieval_time", "physics_1");
				fcd_1.setChartClickEnable(false);
				fcd_1.getUserDefinedProperty().put("level", "three");
				String emsName = reportManagerService.getEmsName(condMap);
				fcd_1.setChart_caption(condMap.get("label") + emsName + "异常性能分布");
				chartXml_1 = fcd_1.getManyDimensionsXmlData();
				xmlMap.put("xml_1", chartXml_1);
			} else if ("month".equals(timeType)) {
				condMap.put("query_day", condMap.get("label"));
				map = reportManagerService.getPMDataPerDayEms(condMap);
				List<Map> List_1 = (List<Map>) map.get("rows");
				FusionChartDesigner fcd_1 = new FusionChartDesigner();
				setFusionChartSettings(fcd_1);
				fcd_1.setOneDimensionList(List_1, "type_level", "cou");
				fcd_1.getUserDefinedProperty().put("level", "three");
				fcd_1.setChartClickEnable(false);
				String emsName = reportManagerService.getEmsName(condMap);
				fcd_1.setChart_caption(condMap.get("label") +emsName+"异常性能分布");
				chartXml_1 = fcd_1.getOneDimensionsXMLData();
				xmlMap.put("xml_1", chartXml_1);
			}
		}
		Object obj = (Object) xmlMap;
		resultObj = JSONObject.fromObject(obj);
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: getEmsInfo_Alarm <BR>
	 * Description: 告警报表--按网管查询<BR>
	 * Remark: 2013-12-19<BR>
	 * @author YuanJia
	 * @return List<Map<String, Object>><BR>
	 */
	@IMethodLog(desc = "查询getEmsInfo_Alarm")
	@SuppressWarnings("unchecked")
	public String getEmsInfo_Alarm(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			String t=paramMap.get("time").toString().replaceAll("-", "");
			int time=Integer.parseInt(t);
			int groupId=0;
			int[] ems=null;
			if(paramMap.get("GROUPID")!=null && !"".equals(paramMap.get("GROUPID"))){
				groupId=Integer.parseInt(paramMap.get("GROUPID").toString());
			}
			if(paramMap.get("EMSIDS")!=null && !"".equals(paramMap.get("EMSIDS"))){
				String emsIds=paramMap.get("EMSIDS").toString();
				if(emsIds!=""){
					String[] ids=emsIds.split(",");
					ems=new int[ids.length];
					for(int i=0;i<ids.length;i++){
						ems[i]=Integer.parseInt(ids[i]);
					}
				}
			}
			setTable_name(CommonDefine.T_HISTORY_ALARM);
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap = alarmManagementService.getReportAlarmByEms(groupId,ems,this.getTable_name(),start,limit,time); /** 此处数据为假，仅供测试*/
			if(resultMap==null || resultMap.get("rows")==null || ((List<DBObject>)resultMap.get("rows")).size()==0){
				setTable_name(CommonDefine.T_CURRENT_ALARM);
				resultMap = alarmManagementService.getReportAlarmByEms(groupId,ems,this.getTable_name(),start,limit,time);
			}
			
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(e.getMessage()));
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
		
	}
	
	
	

	
	/**
	 * Method name: emsFusionChart_Alarm <BR>
	 * Description: 告警报表--fusionChart<BR>
	 * Remark: 2013-12-19<BR>
	 * @author YuanJia
	 * @return List<Map<String, Object>><BR>
	 */
//	@SuppressWarnings("unchecked")
//	public String emsFusionChart_Alarm() throws CommonException{
//		// 将参数专程JSON对象
//		JSONObject jsonObject = JSONObject.fromObject(jsonString);
//		// 将JSON对象转成Map对象
//		Map<String, Object> paramMap = new HashMap<String, Object>();
//		paramMap = (Map<String, Object>) jsonObject;
//		String timeType=paramMap.get("timeType").toString();
//		String t=paramMap.get("time").toString().replaceAll("-", "");
//		int time=Integer.parseInt(t);
//		int groupId=0;
//		int[] ems=null;
//		if(paramMap.get("GROUPID")!=null && !"".equals(paramMap.get("GROUPID"))){
//			groupId=Integer.parseInt(paramMap.get("GROUPID").toString());
//		}
//		if(paramMap.get("EMSIDS")!=null && !"".equals(paramMap.get("EMSIDS"))){
//			String emsIds=paramMap.get("EMSIDS").toString();
//			if(emsIds!=""){
//				String[] ids=emsIds.split(",");
//				ems=new int[ids.length];
//				for(int i=0;i<ids.length;i++){
//					ems[i]=Integer.parseInt(ids[i]);
//				}
//			}
//		}
//		Map<String, Object> resultMap = new HashMap<String, Object>();
//		Map<String,String> xmlMap = new HashMap<String,String>();
//		level=paramMap.get("level")==null?"":paramMap.get("level").toString();
//		if(level==null || "".equals(level)){//生成第一层图表
//			setTable_name(CommonDefine.T_CURRENT_ALARM);
//			resultMap = faultManagerService.getReportAlarmByEms(groupId,ems,this.getTable_name(),start,limit,time); 
//			if(resultMap==null || resultMap.get("rows")==null || ((List<DBObject>)resultMap.get("rows")).size()==0){
//				setTable_name(CommonDefine.T_HISTORY_ALARM);
//				resultMap = faultManagerService.getReportAlarmByEms(groupId,ems,this.getTable_name(),start,limit,time);
//			}
//			List<Map> List_1=(List<Map>)resultMap.get("rows");
//			String chartXml_1 = "";
//			FusionChartDesigner fcd_1 = new FusionChartDesigner();  
//			setFusionChartSettings(fcd_1);
//			fcd_1.setOneDimensionList(List_1, "EMS_NAME", "count");
//			fcd_1.getUserDefinedProperty().put("level","one");
//			fcd_1.setChart_caption(paramMap.get("time").toString()+"告警按网管统计");
//			chartXml_1 = fcd_1.getOneDimensionsXMLData();
//			xmlMap.put("xml_1", chartXml_1);
//		}else if(level!=null && "one".equals(level)){//点击第一层图表进来
//			String chartXml_1 = "";
//			Map<String, Object> map=null;
//			if("year".equals(timeType)){
//				paramMap.put("table_name", CommonDefine.T_CURRENT_ALARM);
//				resultMap =faultManagerService.getAlarmMonthDataByEms(paramMap);
//				if(resultMap==null || resultMap.get("rows")==null || ((List<DBObject>)resultMap.get("rows")).size()==0){
//					paramMap.put("table_name", CommonDefine.T_HISTORY_ALARM);
//					resultMap = faultManagerService.getAlarmMonthDataByEms(paramMap);
//				}
//				List<Map> List_1=(List<Map>)map.get("rows");
//				FusionChartDesigner fcd_1 = new FusionChartDesigner();  
//				setFusionChartSettings(fcd_1);
//				fcd_1.addSerialDatasList("计数值3级",(ArrayList)List_1,"retrieval_time","count_3");
//				fcd_1.addSerialDatasList("计数值2级",(ArrayList)List_1,"retrieval_time","count_2");
//				fcd_1.addSerialDatasList("计数值1级",(ArrayList)List_1,"retrieval_time","count_1");
//				fcd_1.addSerialDatasList("物理量3级",(ArrayList)List_1,"retrieval_time","physics_3");
//				fcd_1.addSerialDatasList("物理量2级",(ArrayList)List_1,"retrieval_time","physics_2");
//				fcd_1.addSerialDatasList("物理量1级",(ArrayList)List_1,"retrieval_time","physics_1");
//				fcd_1.getUserDefinedProperty().put("level","two");
//				fcd_1.setChart_caption(paramMap.get("time").toString()+paramMap.get("label")+"网管组异常性能分布");
//				chartXml_1 = fcd_1.getManyDimensionsXmlData();
//				xmlMap.put("xml_1", chartXml_1);
//			}else if("month".equals(timeType)){
//				paramMap.put("group_name",paramMap.get("label"));
//				paramMap.put("query_month",paramMap.get("time").toString());
//				map = reportManagerService.getPMDayDataByEmsGroup(paramMap);
//				List<Map> List_1=(List<Map>)map.get("rows");
//				FusionChartDesigner fcd_1 = new FusionChartDesigner();  
//				setFusionChartSettings(fcd_1);
//				fcd_1.addSerialDatasList("计数值3级",(ArrayList)List_1,"retrieval_time","count_3");
//				fcd_1.addSerialDatasList("计数值2级",(ArrayList)List_1,"retrieval_time","count_2");
//				fcd_1.addSerialDatasList("计数值1级",(ArrayList)List_1,"retrieval_time","count_1");
//				fcd_1.addSerialDatasList("物理量3级",(ArrayList)List_1,"retrieval_time","physics_3");
//				fcd_1.addSerialDatasList("物理量2级",(ArrayList)List_1,"retrieval_time","physics_2");
//				fcd_1.addSerialDatasList("物理量1级",(ArrayList)List_1,"retrieval_time","physics_1");
//				fcd_1.getUserDefinedProperty().put("level","two");
//				fcd_1.setChart_caption(paramMap.get("time").toString()+paramMap.get("label")+"网管组异常性能分布");
//				chartXml_1 = fcd_1.getManyDimensionsXmlData();
//				xmlMap.put("xml_1", chartXml_1);
//			}else if("day".equals(timeType)){
//				paramMap.put("group_name",paramMap.get("group_name"));
//				paramMap.put("query_day",paramMap.get("time").toString());
//				map = reportManagerService.getPMDayDataByQueryDayAndGroup(paramMap);
//				List<Map> List_1=(List<Map>)map.get("rows");
//				FusionChartDesigner fcd_1 = new FusionChartDesigner();  
//				setFusionChartSettings(fcd_1);
//				fcd_1.setOneDimensionList(List_1, "type_level", "cou");
//				fcd_1.getUserDefinedProperty().put("level","three");
//				fcd_1.setChart_caption(paramMap.get("label")+"异常性能按网管分组统计");
//				chartXml_1 = fcd_1.getOneDimensionsXMLData();
//				xmlMap.put("xml_1", chartXml_1);
//			}
//		}else if(level!=null && "two".equals(level)){//点击第二层图表进来
//			String chartXml_1 = "";
//			Map<String, Object> map=null;
//			if("year".equals(timeType)){
//				paramMap.put("group_name",paramMap.get("group_name"));
//				paramMap.put("query_month",paramMap.get("label"));
//				map = reportManagerService.getPMDayDataByEmsGroup(paramMap);
//				List<Map> List_1=(List<Map>)map.get("rows");
//				FusionChartDesigner fcd_1 = new FusionChartDesigner();  
//				setFusionChartSettings(fcd_1);
//				fcd_1.addSerialDatasList("计数值3级",(ArrayList)List_1,"retrieval_time","count_3");
//				fcd_1.addSerialDatasList("计数值2级",(ArrayList)List_1,"retrieval_time","count_2");
//				fcd_1.addSerialDatasList("计数值1级",(ArrayList)List_1,"retrieval_time","count_1");
//				fcd_1.addSerialDatasList("物理量3级",(ArrayList)List_1,"retrieval_time","physics_3");
//				fcd_1.addSerialDatasList("物理量2级",(ArrayList)List_1,"retrieval_time","physics_2");
//				fcd_1.addSerialDatasList("物理量1级",(ArrayList)List_1,"retrieval_time","physics_1");
//				fcd_1.getUserDefinedProperty().put("level","three");
//				fcd_1.setChart_caption(paramMap.get("label").toString()+paramMap.get("group_name")+"网管组异常性能分布");
//				chartXml_1 = fcd_1.getManyDimensionsXmlData();
//				xmlMap.put("xml_1", chartXml_1);
//			}else if("month".equals(timeType)){
//				paramMap.put("group_name",paramMap.get("group_name"));
//				paramMap.put("query_day",paramMap.get("label"));
//				map = reportManagerService.getPMDayDataByQueryDayAndGroup(paramMap);
//				List<Map> List_1=(List<Map>)map.get("rows");
//				FusionChartDesigner fcd_1 = new FusionChartDesigner();  
//				setFusionChartSettings(fcd_1);
//				fcd_1.setOneDimensionList(List_1, "type_level", "cou");
//				fcd_1.getUserDefinedProperty().put("level","three");
//				fcd_1.setChart_caption(paramMap.get("label")+"异常性能按网管分组统计");
//				chartXml_1 = fcd_1.getOneDimensionsXMLData();
//				xmlMap.put("xml_1", chartXml_1);
//			}
//		}
//		Object obj = (Object)xmlMap;
//		resultObj = JSONObject.fromObject(obj);
//		return RESULT_OBJ;
//		
//		
//	}
	
	/**
	 * Method name: getEmsGroupInfo_Circuit <BR>
	 * Description: 资源报表--按网管分组查询<BR>
	 * Remark: 2013-12-02<BR>
	 * @author YuanJia
	 * @return List<Map<String, Object>><BR>
	 */
	@IMethodLog(desc = "查询getEmsGroupInfo_Circuit")
	@SuppressWarnings("unchecked")
	public String getEmsGroupInfo_Circuit(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			Map<String, Object> resultMap = new HashMap<String, Object>();
			
			resultMap = reportManagerService.getEmsGroupInfo_Circuit(paramMap,start,limit);
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(resultMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: emsGroupFusionChart_Circuit <BR>
	 * Description: 资源报表--fusionChart<BR>
	 * Remark: 2013-12-23<BR>
	 * @author YuanJia
	 * @return List<Map<String, Object>><BR>
	 */
	@SuppressWarnings("unchecked")
	public String emsGroupFusionChart_Circuit() throws CommonException{
		// 将参数专程JSON对象
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		// 将JSON对象转成Map对象
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap = (Map<String, Object>) jsonObject;
		List<Map<String,Object>> List = null;
		
		FusionChartDesigner fcd = new FusionChartDesigner(); 
		if(paramMap.get("PARA")!=null){
			List = reportManagerService.getEmsGroupFusionChart_Circuit_1(paramMap);
			fcd.setChart_caption("电路:按网管分组统计");
		}else{
			List = reportManagerService.getEmsGroupFusionChart_Circuit_2(paramMap);
			fcd.setChart_caption("按网管统计");
		}
		/** 设置fusionChart */
		String chartXml = "";
		ArrayList<Map> xmlList = new ArrayList<Map>(); 
		for(int i=0; i<List.size();i++){
			Map lom = List.get(i);
			xmlList.add((Map)lom);
		}
		fcd.setOneDimensionList(xmlList, "X", "Y");
		fcd.setChart_bgcolor("#F3f3f3");
		fcd.setChart_xAxisName("");
		fcd.setChart_yAxisName("");
		fcd.setChartClickEnable(true);
		chartXml = fcd.getOneDimensionsXMLData();
		Map<String,String> xmlMap = new HashMap<String,String>();
		xmlMap.put("xml", chartXml);
		Object obj = (Object)xmlMap;
		resultObj = JSONObject.fromObject(obj);
		return RESULT_OBJ;
		
	}
	
	@IMethodLog(desc = "查询所有网管分组")
	public String getAllEmsGroups(){
		try {
			// 查询所有网管分组
			List<Map> dataList = commonManagerService.getAllEmsGroups(sysUserId,displayAll,displayNone, false);
			// 将返回的结果转成JSON对象，返回前台
			Map map = new HashMap();
			map.put("rows", dataList);
			resultObj = JSONObject.fromObject(map);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	

//	@IMethodLog(desc = "导出性能数据按网管统计")
//	@SuppressWarnings("unchecked")
//	public void exportEmsInfo_Performance(){
//		HttpServletResponse response = ServletActionContext.getResponse();
//		try {
//			// 将参数专程JSON对象
//			JSONObject jsonObject = JSONObject.fromObject(jsonString);
//			// 将JSON对象转成Map对象
//			Map<String, Object> paramMap = new HashMap<String, Object>();
//			paramMap = (Map<String, Object>) jsonObject;
//			if("month".equals(paramMap.get("timeType")) || "day".equals(paramMap.get("timeType"))){
//				String time = paramMap.get("time").toString().split("-")[0] + "_" + paramMap.get("time").toString().split("-")[1];
//				setTable_name("t_pm_origi_data_2_" + time);
//			}else{
//				String time = paramMap.get("time").toString().split("-")[0];
//				setTable_name("t_pm_origi_data_2_" + time);
//			}
//			if("day".equals(paramMap.get("timeType"))){
//				paramMap.put("day",paramMap.get("time").toString());
//			}
//			paramMap.put("table_name", table_name);
//			Map<String, Object> resultMap = new HashMap<String, Object>();
//			resultMap = reportManagerService.getEmsInfo_Performance(paramMap,0,limit);
//			// 将返回的结果转成JSON对象，返回前台
//			List<Map<String, Object>> list = (List<Map<String, Object>>)resultMap.get("rows");
//			//Export Excel
//			 String myFlieName="性能数据按网管统计.xls";
//			 response.setContentType("application/vnd.ms-excel");
//			 response.addHeader("Content-Disposition","attachment;   filename="+new String(myFlieName.getBytes("gb2312"), "ISO8859-1" ));
//			 OutputStream os = response.getOutputStream();
//			     WritableWorkbook wwb = null;    
//		         wwb = Workbook.createWorkbook(os);
//		     if(wwb!=null){    
//		    	 WritableSheet  ws = wwb.createSheet("性能数据按网管分组统计", 0);
//		    	 ws.addCell(new Label(0, 0,"异常等级"));
//		    	 ws.addCell(new Label(1, 0,"网管分组"));
//		    	 ws.addCell(new Label(2, 0,"网管"));
//		         ws.addCell(new Label(3, 0,"子网"));
//		         ws.addCell(new Label(4, 0,"网元"));
//		         ws.addCell(new Label(5, 0,"型号"));
//		         ws.addCell(new Label(6, 0,"端口"));
//		         ws.addCell(new Label(7, 0,"业务类型"));
//		         ws.addCell(new Label(8, 0,"端口类型"));
//		         ws.addCell(new Label(9, 0,"速率"));
//		         ws.addCell(new Label(10, 0,"通道"));
//		         ws.addCell(new Label(11, 0,"性能时间"));
//		         ws.addCell(new Label(12, 0,"方向"));
//		         ws.addCell(new Label(13, 0,"性能值"));
//		         ws.addCell(new Label(14, 0,"性能基准值"));
//		         ws.addCell(new Label(15, 0,"连续异常"));
//		         ws.addCell(new Label(16, 0,"性能分析模板"));
//		         ws.addCell(new Label(17, 0,"采集时间"));
//		         for(int i=0;i<list.size();i++){
//		        	 int value=(list.get(i).get("EXCEPTION_LV")==null?0:Integer.valueOf(list.get(i).get("EXCEPTION_LV").toString()));
//		        	 String gjjb="";//异常级别
//		        	 WritableCellFormat wff_color = new WritableCellFormat();
//		        		 if(value==0){
//			        		 wff_color.setBackground(Colour.GREEN);
//			        		 gjjb="正常";
//			 			}else if(value==1){
//			 				 wff_color.setBackground(Colour.YELLOW);
//			        		 gjjb="告警等级1";
//			 			}else if(value==2){
//			 				 wff_color.setBackground(Colour.ORANGE);
//			        		 gjjb="告警等级2";
//			        	}else if(value==3){
//			 				 wff_color.setBackground(Colour.RED);
//			        		 gjjb="告警等级3";
//			        	}
//		        	 ws.addCell(new Label(0, i+1,gjjb,wff_color));
//			         ws.addCell(new Label(1, i+1,list.get(i).get("DISPLAY_EMS_GROUP")==null?"":list.get(i).get("DISPLAY_EMS_GROUP").toString()));
//			         ws.addCell(new Label(2, i+1,list.get(i).get("DISPLAY_EMS")==null?"":list.get(i).get("DISPLAY_EMS").toString()));
//			         ws.addCell(new Label(3, i+1,list.get(i).get("DISPLAY_SUBNET")==null?"":list.get(i).get("DISPLAY_SUBNET").toString()));
//			         ws.addCell(new Label(4, i+1,list.get(i).get("DISPLAY_NE")==null?"":list.get(i).get("DISPLAY_NE").toString()));
//			         ws.addCell(new Label(5, i+1,list.get(i).get("NET_TYPE")==null?"":list.get(i).get("NET_TYPE").toString()));
//			         ws.addCell(new Label(6, i+1,list.get(i).get("PORT")==null?"":list.get(i).get("PORT").toString()));
//			         ws.addCell(new Label(7, i+1,list.get(i).get("BUSINESS_TYPE")==null?"":list.get(i).get("BUSINESS_TYPE").toString()));
//			         ws.addCell(new Label(8, i+1,list.get(i).get("PTP_TYPE")==null?"":list.get(i).get("PTP_TYPE").toString()));
//			         ws.addCell(new Label(9, i+1,list.get(i).get("SPEED")==null?"":list.get(i).get("SPEED").toString()));
//			         ws.addCell(new Label(10, i+1,list.get(i).get("DISPLAY_CTP")==null?"":list.get(i).get("DISPLAY_CTP").toString()));
//			         ws.addCell(new Label(11, i+1,list.get(i).get("PERFORMANCE_EVENT")==null?"":list.get(i).get("PERFORMANCE_EVENT").toString()));
//			         ws.addCell(new Label(12, i+1,list.get(i).get("DIRECTION")==null?"":list.get(i).get("DIRECTION").toString()));
//			         ws.addCell(new Label(13, i+1,list.get(i).get("PM_VALUE")==null?"":list.get(i).get("PM_VALUE").toString()));
//			         ws.addCell(new Label(14, i+1,list.get(i).get("PM_COMPARE_VALUE")==null?"":list.get(i).get("PM_COMPARE_VALUE").toString()));
//			         ws.addCell(new Label(15, i+1,list.get(i).get("EXCEPTION_COUNT")==null?"":list.get(i).get("EXCEPTION_COUNT").toString()));
//			         ws.addCell(new Label(16, i+1,list.get(i).get("DISPLAY_TEMPLATE_NAME")==null?"":list.get(i).get("DISPLAY_TEMPLATE_NAME").toString()));
//			         ws.addCell(new Label(17, i+1,list.get(i).get("COLLECT_TIME")==null?"":list.get(i).get("COLLECT_TIME").toString()));
//		         }
//		         try {    
//		             wwb.write();    
//		         } catch (IOException e) {   		        	
//		             e.printStackTrace();    
//		         }finally{
//		        	 wwb.close();   
//		             os.close();
//		             response.flushBuffer();
//		         }
//		     }
//		
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	
	@IMethodLog(desc = "导出性能数据按网管统计")
	@SuppressWarnings("unchecked")
	public String exportEmsInfo_Performance(){
//			// 将参数专程JSON对象
//			JSONObject jsonObject = JSONObject.fromObject(jsonString);
//			// 将JSON对象转成Map对象
//			Map<String, Object> paramMap = new HashMap<String, Object>();
//			paramMap = (Map<String, Object>) jsonObject;
//			setTable_name(TABLE_PRI+paramMap.get("time").toString().substring(0,4));
//			paramMap.put("table_name",table_name);
//			paramMap.put("timeType", paramMap.get("timeType"));
//			paramMap.put("time",paramMap.get("time").toString());
//			paramMap.put("query","list");
//			Map<String, Object> resultMap = new HashMap<String, Object>();
//			try {
//				resultMap = reportManagerService.getEmsInfo_Performance(paramMap,start,limit);
//			} catch (CommonException e) {
//				e.printStackTrace();
//			}
//			List<Map> list = (List<Map>)resultMap.get("rows");
//			String destination = null;
//			String myFlieName="性能数据按网管统计";
//			IExportExcel ex=new ExportExcelUtil(CommonDefine.PATH_ROOT+ CommonDefine.EXCEL.TEMP_DIR,myFlieName);
//			destination = ex.writeExcel(list,CommonDefine.EXCEL.REPORT_PERFROMANCE_EMS, false);
//			setFilePath(destination);
			return RESULT_DOWNLOAD;
	}
	
	
	
	
	@IMethodLog(desc = "导出性能数据按网管分组统计")
	@SuppressWarnings("unchecked")
	public String exportEmsGroupInfo_Performance(){
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			setTable_name(TABLE_PRI+paramMap.get("time").toString().substring(0,4));
			paramMap.put("table_name",table_name);
			paramMap.put("timeType", paramMap.get("timeType"));
			paramMap.put("time",paramMap.get("time").toString());
			paramMap.put("query","list");
			Map<String, Object> resultMap = new HashMap<String, Object>();
			try {
				resultMap = reportManagerService.getEmsGroupInfo_Performance(paramMap,start,limit);
			} catch (CommonException e) {
				e.printStackTrace();
			}
			// 将返回的结果转成JSON对象，返回前台
			List<Map> list = (List<Map>)resultMap.get("rows");
			String destination = null;
			String myFlieName="性能数据按网管分组统计";
			IExportExcel ex=new ExportExcelUtil(CommonDefine.PATH_ROOT+ CommonDefine.EXCEL.TEMP_DIR,myFlieName);
			destination = ex.writeExcel(list,CommonDefine.EXCEL.REPORT_PERFROMANCE_EMS, false);
			setFilePath(destination);
			return RESULT_DOWNLOAD;
	}

	@IMethodLog(desc = "导出告警按网管统计")
	@SuppressWarnings("unchecked")
	public String exportEmsInfo_Alarm(){
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			String t=paramMap.get("time").toString().replaceAll("-", "");
			int time=Integer.parseInt(t);
			int groupId=0;
			int[] ems=null;
			if(paramMap.get("GROUPID")!=null && !"".equals(paramMap.get("GROUPID"))){
				groupId=Integer.parseInt(paramMap.get("GROUPID").toString());
			}
			if(paramMap.get("EMSIDS")!=null && !"".equals(paramMap.get("EMSIDS"))){
				String emsIds=paramMap.get("EMSIDS").toString();
				if(emsIds!=""){
					String[] ids=emsIds.split(",");
					ems=new int[ids.length];
					for(int i=0;i<ids.length;i++){
						ems[i]=Integer.parseInt(ids[i]);
					}
				}
			}
			setTable_name(CommonDefine.T_HISTORY_ALARM);
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap = alarmManagementService.getReportAlarmByEms(groupId,ems,this.getTable_name(),start,limit,time); /** 此处数据为假，仅供测试*/
			if(resultMap==null || resultMap.get("rows")==null || ((List<DBObject>)resultMap.get("rows")).size()==0){
				setTable_name(CommonDefine.T_CURRENT_ALARM);
				resultMap = alarmManagementService.getReportAlarmByEms(groupId,ems,this.getTable_name(),start,limit,time);
			}
			List<Map> list = (List<Map>)resultMap.get("rows");
			String destination = null;
			String myFlieName="告警按网管统计";
			IExportExcel ex=new ExportExcelUtil(CommonDefine.PATH_ROOT
					+ CommonDefine.EXCEL.TEMP_DIR,myFlieName);
			destination = ex.writeExcel(list,CommonDefine.EXCEL.REPORT_ALARM_EMS, false);
			setFilePath(destination);
			return RESULT_DOWNLOAD;
	}
	
	@IMethodLog(desc = "导出网元数量按网管统计")
	@SuppressWarnings("unchecked")
	public String exportEmsInfo_Resource(){
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			Map<String, Object> resultMap = new HashMap<String, Object>();
			
			try {
				resultMap = reportManagerService.getEmsInfo_Resource(paramMap,start,limit);
			} catch (CommonException e1) {
				e1.printStackTrace();
			}
			// 将返回的结果转成JSON对象，返回前台
			List<Map> list = (List<Map>)resultMap.get("rows");
			String destination = null;
			String myFlieName="资源数据按网管统计";
			IExportExcel ex=new ExportExcelUtil(CommonDefine.PATH_ROOT
					+ CommonDefine.EXCEL.TEMP_DIR,myFlieName);
			destination = ex.writeExcel(list,CommonDefine.EXCEL.REPORT_RESOURCE_EMS, false);
			setFilePath(destination);
			return RESULT_DOWNLOAD;
	}
	
	
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "共通树:获取所有子节点")
	public String getChildNodes() {
		String returnString = RESULT_OBJ;
		try {
			if (endLevel > 0) {
				List<Map> nodes = commonManagerService.treeGetChildNodes(nodeId,
						nodeLevel, endLevel,sysUserId);
				dealNodes(nodes);//网管分组为-1时
				resultArray = JSONArray.fromObject(nodes);
			}else{
				resultArray=new JSONArray();
			}
			returnString = RESULT_ARRAY;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}

		return returnString;
	}
	private void dealNodes(List<Map> nodes) {
		for(int i=nodes.size()-1;i>=0;i--){
			Map m=nodes.get(i);
			int nodeLevel=(Integer)m.get("nodeLevel");
			if(nodeLevel==1){
				nodes.remove(i);
			}
		}
	}
	
	
	
	/**
	 * 调用性能存储过程job
	 * @throws CommonException 
	 */
	@IMethodLog(desc = "调用性能存储过程job")
	@SuppressWarnings("rawtypes")
	public String callPerformanceSPJob(){
		try {
			Map param=new HashMap();
			param.put("month","2013-03");
			param.put("jobTime","0 0 23 2 * ?");
			Map returnParm=reportManagerService.callPerformanceSPJob(param);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("设置成功");
		} catch (Exception e) {
			e.printStackTrace();
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getMessage());
		}
		resultObj = JSONObject.fromObject(result);
		return RESULT_OBJ;
	}
	public Map<String, String> getCondMap() {
		return condMap;
	}
	public void setCondMap(Map<String, String> condMap) {
		this.condMap = condMap;
	}
	

}
