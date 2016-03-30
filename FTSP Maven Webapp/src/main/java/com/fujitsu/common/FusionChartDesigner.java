package com.fujitsu.common;
/**
 *  Author: Lian Yongqing  Date: 2011-08-16 
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.json.JSONArray;

public class FusionChartDesigner {
	private Map<String,String> chartNodePro = new HashMap<String, String>();
	private Map trendlines_linePro = new HashMap();
	/* beile */
	private Map YAxismap = new HashMap<String,String>();
	/* beile */
//	The properties for the <dataset ....></dataset> these properties should be set into the trendlines_linePro
//	property list: parentYAxis, color, numberPrefix, anchorSides, anchorRadius, anchorBorderColor.
	private Map<String, String> dataSetNodePro = new HashMap<String, String>();
	

	
//	The properties for the <chart .....></chart>
	private String chart_caption = "Default Caption";
	private String chart_xAxisName = "Default X Axis Name";
	private String chart_yAxisName = "Default Y Axis Name";
	private String chart_formatNumberScale= "0";
	private String chart_decimalPrecision="0";
	private String chart_subCaption = "";
	private String chart_numberPrefix = ""; //前缀
	private String chart_numberSuffix = ""; //后缀
	private String chart_showValues="0";	
	private String chart_bgcolor ="";
	private String chart_bgAlpha="";
	private String chart_showBorder="";
	private String chart_canvasBorderThickness="";//默认值 2。画布边框宽度，0为表示不显示边框。
	private String chart_showColumnShadow="1";
	private String chart_divlinecolor="";
	private String chart_divLineAlpha="";
	private String chart_showAlternateHGridColor="";
	private String chart_alternateHGridColor="";
	private String chart_alternateHGridAlpha="";
	private String chart_SYAxisMaxValue="";
	private String chart_PYAxisName = "";
	private String chart_SYAxisName = "";
	private String chart_exportEnabled = "false";
	private String chart_exportAtClient ="";
	private String chart_exportAction="";
	private String chart_exportHandler="";
	private String chart_exportFileName="";
	private String chart_exportTargetWindow="";
	private String chart_rotateLabels = "";//'0/1'(是否旋转x轴的坐标值,1表示旋转)
	private String chart_slantLabels = "";//'0/1'(将x轴坐标值旋转为倾斜的还是完全垂直的,1表示倾斜)
	private String chart_useRoundEdges = ""; //'0/1'(是否圆柱显示，1表示圆柱显示)
	private String chart_yAxisMinValue = ""; //设置Y轴最小值
	private String chart_yAxisMaxValue = ""; //设置Y轴最大值
	private String chart_Decimals = ""; //设置保留几位小数
	private String chart_labelStep="1";//横坐标每隔n个显示一条
	private String chart_anchorRadius="3";//曲线上点的大小设置，默认3,值越大点越大
	private String chart_showLabels="1";//是否显示X轴坐标
	
	
//  The properties for the <trendlines> <line ... /> </trendlines>
	private String trendlines_line_startValue="";
	private String trendlines_line_displayValue="";
	private String trendlines_line_showOnTop="";
	private String trendlines_line_color="";
	private String trendlines_line_thickness="";
	
	private List ColorListOneDimension = new ArrayList();
	
	private List xListOneDimension = new ArrayList<String>();
	private List yListOneDimension = new ArrayList<String>();
	
	private List xListManyDimension = new ArrayList(); 
	private List<ArrayList> yListsManyDimension = new ArrayList<ArrayList>();
	
	private String XKeyOneDimension = "x";
	private String YKeyOneDimension = "y";
	
	private String XKeyManyDimension = "x";
	private String YKeyManyDimension = "y";
	
	private List seriesNameList = new ArrayList<String>();
	
	private boolean chartClickEnable = false;
	
	//chengyingqi 2012-2-1
	private List<Map> allxList = new ArrayList<Map>(); 
	
	//lianyongqing 2012-4-24
	private Map userDefinedProperty = new HashMap();
	
	
	/**
	 * Set default properties for a chart. 
	 * 
	 */
	public FusionChartDesigner(){
		chartNodePro.put("caption", "Default Caption");
		chartNodePro.put("xAxisName", "Default X Axis Name");
		chartNodePro.put("yAxisName", "Default Y Axis Name");
//		chartNodePro.put("exportEnabled", "1");
//		chartNodePro.put("exportAtClient", "1");
//		chartNodePro.put("exportAction", "download");
//		chartNodePro.put("exportHandler", "fcExporter1");
//		chartNodePro.put("exportFileName", "MyFileName");
//		chartNodePro.put("exportTargetWindow", "_blank");
		chartNodePro.put("bgcolor", "#DFE8F6");
		chartNodePro.put("baseFontSize", "12");
		chartNodePro.put("baseFont", "宋体");
		chartNodePro.put("formatNumberScale", "0");
		chartNodePro.put("Decimals", "6");
	}
	
	
	/**
	 * Change Chinese code to Unicode to avoid the messy code in java script link functions. 
	 * 
	 */
    private String chinaToUnicode(String str)
    {
        String result = "";
        for (int i = 0; i < str.length(); i++)
        {
            int chr1 = (char) str.charAt(i);
            if(chr1 >= 256)
            {
            	result  += "\\u" + Integer.toHexString(chr1);
            }else{
            	result = result + str.charAt(i);
            }
            
        }
        return result;
//    	return str;
    }


	/**
	 * Draw XML trendline for one&many dimension chart
	 * 
	 */
	private StringBuffer drawTrendLines() {
		StringBuffer buffer = new StringBuffer("");
		if(this.trendlines_linePro.size()==0)
		{
			return buffer;
		}
		buffer.append("<trendlines> ");
		buffer.append("<line " );
		Iterator it = this.trendlines_linePro.keySet().iterator();
		while(it.hasNext()){
			String key = it.next().toString();
			String value = this.trendlines_linePro.get(key).toString();
			buffer.append(key + "='" +value + "' ");
			
		}
		buffer.append(" /> ");
		buffer.append("</trendlines> ");
		return buffer;
	}
	
	/**
	 * Draw the XML title for one&many dimension chart
	 * 
	 */
	private StringBuffer drawXMLTitle(){
		StringBuffer buffer = new StringBuffer("");
		buffer.append("<chart ");
		//防止创建时未定义双Y轴的横坐标
		if(YAxismap.isEmpty()){
			this.chartNodePro.remove(chart_PYAxisName);
			this.chartNodePro.remove(chart_SYAxisName);
		}
		Iterator it = this.chartNodePro.keySet().iterator();
		while(it.hasNext()){
			String key = it.next().toString().replace("chart_", "");
			String value = this.chartNodePro.get(key).toString();
			buffer.append(key + "='" + value + "' ");
		}
		buffer.append("> ");
		return buffer;
	}
	
	/**
	 * util tool function to split ArrayList<Map> to two ArrayList, the ArrayLists can not be sort.
	 * 
	 */
	
	private Object[] splitListMapToXYList(ArrayList<Map> listMap, String xKey, String yKey){
		ArrayList xList = new ArrayList();
		ArrayList yList = new ArrayList();
		for(int i=0; i<listMap.size();i++){
			Map map = listMap.get(i);
			xList.add(map.get(xKey)==null?"":map.get(xKey).toString());
			yList.add(map.get(yKey)==null?"":map.get(yKey).toString());
		}
		Object[] xyList = {xList, yList};
		return xyList;
	}

	
	
	//One dimension Chart
	
	/**
	 * Set data for a chart, data type: ArrayList<Map>, the default xKeyOneDimension is 'x', the default yKeyOneDimension is 'y'
	 * the xKeyOneDimension and yKeyOneDimension can be set by set methods.
	 */
	public void setOneDimensionList(ArrayList<Map> oneDimensionsList){
		
		setOneDimensionList(oneDimensionsList, this.XKeyOneDimension, this.YKeyOneDimension);
	}
	
	/**
	 * Set data for a chart, data type: ArrayList<Map>, user named the XKeyOneDimension and YKeyOneDimension.
	 * 
	 */
	public void setOneDimensionList(List<Map> oneDimensionsList, String XKeyOneDimension, String YKeyOneDimension){
		Object[] xyList = splitListMapToXYList((ArrayList<Map>)oneDimensionsList, XKeyOneDimension, YKeyOneDimension);
		setxListOneDimension((ArrayList)xyList[0]);
		setyListOneDimension((ArrayList)xyList[1]);
	}
	
	/**
	 * Set data for a chart, xListOneDimension is the names in the x axis, yListOneDimension is the values in the y axis.
	 * User should keep the right map of values based on the index of the two ArrayList. 
	 */
	public void setOneDimensionXYList(ArrayList xListOneDimension, ArrayList yListOneDimension){
		setxListOneDimension(xListOneDimension);
		setyListOneDimension(yListOneDimension);
	}
	
	/**
	 * Draw the XML set nodes for one dimension chart
	 * 
	 */
	private StringBuffer drawOneDimensionXMLSet(){
		StringBuffer buffer = new StringBuffer("");
		Map map = new HashMap();
		for(int i=0; i<this.xListOneDimension.size(); i++){
			String label = this.xListOneDimension.get(i).toString();
			String value = "";
			buffer.append("<set label = '" + label + "' ");
			if(this.yListOneDimension.get(i)!=null && !this.yListOneDimension.get(i).toString().trim().equals("")){
				value = this.yListOneDimension.get(i).toString();
				buffer.append("value = " + "'"+value+"' ");
			}
			if(this.chartClickEnable){
				map.clear();
				map.put("caption", chinaToUnicode(this.chartNodePro.get("caption")));
				map.put("label", chinaToUnicode(label));
				map.put("value", value);
//				添加用户自定义的变量值lianyongqing
				Set udset = this.userDefinedProperty.entrySet();
				Iterator udit = udset.iterator();
				while(udit.hasNext()){
					Map.Entry<String, String> enter = (Entry<String, String>) udit.next();
					map.put(enter.getKey(), enter.getValue());
				}
				JSONArray json = JSONArray.fromObject(map);
				String jsonStr = json.toString();
				buffer.append(" link='JavaScript:FusionChartClick("+jsonStr.substring(1, jsonStr.length()-1)+");' ");
//				buffer.append("link=\\\"JavaScript:FusionChartClick('"+chinaToUnicode(this.chartNodePro.get("caption"))+"', '"+ chinaToUnicode(label) + "', '"+value+"');\\\" ");
			}
			if(this.ColorListOneDimension!=null && this.ColorListOneDimension.size()>0){
				buffer.append(ColorListOneDimension.get(i).toString());
			}
			
			buffer.append("/> ");
		}
		return buffer;
	}

	/**
	 * Get the XML String with out parameters.
	 * 
	 */
	public String getOneDimensionsXMLData() {
		StringBuffer buffer = drawXMLTitle();
		buffer.append(drawOneDimensionXMLSet());
		buffer.append(drawTrendLines());
		buffer.append("</chart> ");
		return buffer.toString();
	}
	
	/**
	 * Get the XML String with parameters.
	 * 
	 */
	public String getOneDimensionsXMLData(String caption, String xAxisName, String yAxisName, ArrayList<Map> oneDimensionsList, String xKey, String yKey) {
		this.chartNodePro.put("caption", caption);
		this.chartNodePro.put("xAxisName", xAxisName);
		this.chartNodePro.put("yAxisName", yAxisName);
		setXKeyOneDimension(xKey);
		setYKeyOneDimension(yKey);
		setOneDimensionList(oneDimensionsList);
		StringBuffer buffer = drawXMLTitle();
		buffer.append(drawOneDimensionXMLSet());
		buffer.append("</chart> ");
		return buffer.toString();
	}
	
	/**
	 * Get the XML String with parameters.
	 * 
	 */
	public String getOneDimensionsXMLData(String caption, String xAxisName, String yAxisName, ArrayList xListOneDimension, ArrayList yListOneDimension) {
		this.chartNodePro.put("caption", caption);
		this.chartNodePro.put("xAxisName", xAxisName);
		this.chartNodePro.put("yAxisName", yAxisName);
		this.xListOneDimension = xListOneDimension;
		this.yListOneDimension = yListOneDimension;
		
		StringBuffer buffer = drawXMLTitle();
		buffer.append(drawOneDimensionXMLSet());
		buffer.append(drawTrendLines());
		buffer.append("</chart> ");
		return buffer.toString();
	}
	
	
	
	
	
//  Many dimension Chart
	
	/**
	 * Set data for a chart, data type: ArrayList<Map>, the default XKeyManyDimension is 'x', the default YKeyManyDimension is 'y'
	 * the XKeyManyDimension and YKeyManyDimension can be set by set methods.
	 */
	public void addSerialDatasList(String serialName, ArrayList<Map> dataList){
		addSerialDatasList(serialName, dataList, this.XKeyManyDimension, this.YKeyManyDimension );
	} 
	
	
	/**
	 * Set data for a chart based on the serialName, xListManyDimension is the names in the x axis, yListManyDimension is the values in the y axis.
	 * User should keep the right map of values based on the index of the two ArrayList. 
	 */
	public void addSerialDataList(String serialName, ArrayList xListManyDimension, ArrayList yListManyDimension){
		this.seriesNameList.add(serialName);
		this.xListManyDimension = xListManyDimension;
		this.yListsManyDimension.add(yListManyDimension);
	} 
	
	/**
	 * Set data for a chart based on the serialName, xListManyDimension is the names in the x axis, yListManyDimension is the values in the y axis.
	 * User should keep the right map of values based on the index of the two ArrayList.
	 * 增加多系列图线条颜色设置 
	 */
	public void addSerialDatasListColor(String serialName, ArrayList<Map> serieDataList, String XKeyManyDimension, String YKeyManyDimension,String color){
		this.seriesNameList.add(serialName + "'  color='" + color);
		Object[] xyList = splitListMapToXYList(serieDataList, XKeyManyDimension, YKeyManyDimension);
		//chengyingqi 2012-2-1 start
		ArrayList tempList =  (ArrayList) xyList[0];
		Map map = new HashMap();
		for(int i = 0; i < tempList.size(); i++){
			map.put(tempList.get(i), "exist");
		}
		allxList.add(map);
		//chengyingqi 2012-2-1 end
		this.yListsManyDimension.add((ArrayList)xyList[1]);
		if(this.xListManyDimension==null || this.xListManyDimension.size()==0){
			this.xListManyDimension = (ArrayList) xyList[0];
		}
		this.XKeyManyDimension = XKeyManyDimension;
		this.YKeyManyDimension = YKeyManyDimension;
	} 
	
	/**
	 * Set data for a chart, data type: ArrayList<Map>, user named the XKeyManyDimension and YKeyManyDimension.
	 * 
	 */
	
	public void addSerialDatasList(String serialName, ArrayList<Map> serieDataList, String XKeyManyDimension, String YKeyManyDimension ){
		this.seriesNameList.add(serialName);
		Object[] xyList = splitListMapToXYList(serieDataList, XKeyManyDimension, YKeyManyDimension);
		//chengyingqi 2012-2-1 start
		ArrayList tempList =  (ArrayList) xyList[0];
		Map map = new HashMap();
		for(int i = 0; i < tempList.size(); i++){
			map.put(tempList.get(i), "exist");
		}
		allxList.add(map);
		//chengyingqi 2012-2-1 end
		this.yListsManyDimension.add((ArrayList)xyList[1]);
		if(this.xListManyDimension==null || this.xListManyDimension.size()==0){
			this.xListManyDimension = (ArrayList) xyList[0];
		}
		this.XKeyManyDimension = XKeyManyDimension;
		this.YKeyManyDimension = YKeyManyDimension;
	} 
	/**
	 * Set data for a chart, data type: ArrayList<Map>, user named the XKeyManyDimension and PYKeyManyDimension.
	 * 
	 */
	
	public void addSerialDatasListPY(String serialName, ArrayList<Map> serieDataList, String XKeyManyDimension, String YKeyManyDimension,String renderAs ){
		setLeftYAxis(serialName);
		addSerialDatasList(serialName, serieDataList,XKeyManyDimension, YKeyManyDimension,renderAs  );
	} 
	/**
	 * Set data for a chart, data type: ArrayList<Map>, user named the XKeyManyDimension and SYKeyManyDimension.
	 * 
	 */
	
	public void addSerialDatasListSY(String serialName, ArrayList<Map> serieDataList, String XKeyManyDimension, String YKeyManyDimension,String renderAs ){
		setRightYAxis(serialName);
		addSerialDatasList(serialName, serieDataList, XKeyManyDimension, YKeyManyDimension,renderAs  );
	} 
	/**
	 * 在做组合图时，使用这个方法
	 * 可以使以下加入数据产生想要的图形
	 * 比如Line，就可以生产折线,Area产生柱状图
	 */
	
	public void addSerialDatasList(String serialName, ArrayList<Map> serieDataList, String XKeyManyDimension, String YKeyManyDimension,String renderAs){
		this.seriesNameList.add(serialName + "'  renderAs='" + renderAs);
		Object[] xyList = splitListMapToXYList(serieDataList, XKeyManyDimension, YKeyManyDimension);
		//chengyingqi 2012-2-1 start
		ArrayList tempList =  (ArrayList) xyList[0];
		Map map = new HashMap();
		for(int i = 0; i < tempList.size(); i++){
			map.put(tempList.get(i), "exist");
		}
		allxList.add(map);
		//chengyingqi 2012-2-1 end
		this.yListsManyDimension.add((ArrayList)xyList[1]);
		if(this.xListManyDimension==null || this.xListManyDimension.size()==0){
			this.xListManyDimension = (ArrayList) xyList[0];
		}
		this.XKeyManyDimension = XKeyManyDimension;
		this.YKeyManyDimension = YKeyManyDimension;
	} 
	
	/**
	 * Set data for a categories, xListManyDimension is the names in the x axis.
	 */
	private StringBuffer drawXMLCategories() {
		StringBuffer buffer = new StringBuffer("");
		buffer.append("<categories>");
		for (int i = 0; i < this.xListManyDimension.size(); i++) {
			buffer.append("<category label = '" + this.xListManyDimension.get(i) + "' />");
		}
		buffer.append("</categories>");
		return buffer;
	}
	
	/**
	 * Set data for a dataset node.
	 */
	private StringBuffer drawXMLDataSet(){
		Map map = new HashMap();
		StringBuffer buffer = new StringBuffer("");
		for (int i = 0; i < this.seriesNameList.size(); i++) {
			String seriesName = this.seriesNameList.get(i).toString().split("'")[0];
			buffer.append("<dataset seriesName='" + seriesName +"' ");
			/* beile */
			if("L".equals(YAxismap.get(seriesName)==null?"":YAxismap.get(seriesName))){
				buffer.append(" parentYAxis='P' ");
			}else if("R".equals(YAxismap.get(seriesName)==null?"":YAxismap.get(seriesName))){
				buffer.append(" parentYAxis='S' color='8BBA00' anchorSides='10' anchorRadius='3' anchorBorderColor='009900'");
			}
			/* beile */
			Iterator it = this.dataSetNodePro.keySet().iterator();
			while(it.hasNext()){
				String key = it.next().toString();
				if(key.startsWith(seriesName+"_")){
					String value = this.dataSetNodePro.get(key);
					buffer.append(key.replace(seriesName+"_", "") + "='"+value+"' ");
				}
			}
			buffer.append(" > ");
			ArrayList yList  = this.yListsManyDimension.get(i);
			//chengyingqi 2012-2-1 判断值是否要补充对应所有的x坐标点 start
			//如果缺少值就补充"",空值
			if(yList.size() < this.xListManyDimension.size()){
				ArrayList tempyList = new ArrayList();
				int tempx = 0;
				for (int j = 0; j < this.xListManyDimension.size(); j++) {
					if(!allxList.get(i).containsKey(this.xListManyDimension.get(j).toString() ) || tempx >= yList.size()){
						tempyList.add("");
					}else{
						tempyList.add(yList.get(tempx));
						tempx++;
					}
				}
				yList.clear();
				yList.addAll(tempyList);
			}
			//chengyingqi 2012-2-1 end
			for (int j = 0; j < this.xListManyDimension.size(); j++) {
				//加入判断，避免坐标上同一时间段内有不同数值点的图生成时遇到的越界异常    2012.1.30 by guzhijie
				if(j < yList.size()){
					buffer.append("<set value='" + yList.get(j).toString() + "' ");
					if (this.chartClickEnable) {
						map.clear();
						map.put("caption", chinaToUnicode(this.chartNodePro.get("caption")));
						map.put("seriesName", chinaToUnicode(seriesName));
						map.put("label", chinaToUnicode(this.xListManyDimension.get(j).toString()));
						map.put("value", yList.get(j).toString());
//						添加用户自定义的变量值lianyongqing
						Set udset = this.userDefinedProperty.entrySet();
						Iterator udit = udset.iterator();
						while(udit.hasNext()){
							Map.Entry<String, String> enter = (Entry<String, String>) udit.next();
							map.put(enter.getKey(), enter.getValue());
						}
						JSONArray json = JSONArray.fromObject(map);
						String jsonStr = json.toString();
//						buffer.append(" link='JavaScript:FusionChartClick("+jsonStr.substring(1,jsonStr.length()-1)+");' ");
						String temp_json = jsonStr.substring(1,jsonStr.length()-1);
						temp_json = temp_json.replace("\"", "'");
						buffer.append(" link=\"JavaScript:FusionChartClick("+chinaToUnicode(temp_json)+");\" ");
						
//						buffer.append("link=\"JavaScript:FusionChartClick('"
//								+ chinaToUnicode(chartNodePro.get("caption")) + "', '" + chinaToUnicode(seriesName)
//								+ "', '" + chinaToUnicode(this.xListManyDimension.get(j).toString())
//								+ "', '" + yList.get(j).toString() + "');\" ");
					}
					buffer.append("/> ");
				}
				
			}

			buffer.append("</dataset> ");
		}

		return buffer;
	}
	
	/**
	 * Get the XML String with out parameters.
	 * 
	 */
	public String getManyDimensionsXmlData() {
		StringBuffer buffer = drawXMLTitle();
		buffer.append(drawXMLCategories());
		buffer.append(drawXMLDataSet());
		buffer.append(drawTrendLines());
		buffer.append("</chart>");
		return buffer.toString();
	}
	
	/**
	 * Get the XML String with parameters.
	 * 
	 */
	public String getManyDimensionsXmlData(String caption, String xAxisName, String yAxisName) {
		setChart_caption(caption);
		setChart_xAxisName(xAxisName);
		setChart_yAxisName(yAxisName);
		StringBuffer buffer = drawXMLTitle();
		buffer.append(drawXMLCategories());
		buffer.append(drawXMLDataSet());
		buffer.append(drawTrendLines());
		buffer.append("</chart>");
		return buffer.toString();
	}
	
	
	/**
	 * Get the XML String with parameters.
	 * 实现双Y轴
	 */
	public String getManyDimensionsXmlData(String caption, String xAxisName, String PYAxisName, String SYAxisName) {
		setChart_caption(caption);
		setChart_xAxisName(xAxisName);
		setChart_PYAxisName(PYAxisName);
		setChart_SYAxisName(SYAxisName);
		StringBuffer buffer = drawXMLTitle();
		buffer.append(drawXMLCategories());
		buffer.append(drawXMLDataSet());
		buffer.append(drawTrendLines());
		buffer.append("</chart>");
		return buffer.toString();
	}
	
	
//	get and set methods for the chart properties.
	public String getChart_caption() {
		return chart_caption;
	}

	public void setChart_caption(String chartCaption) {
		chart_caption = chartCaption;
		chartNodePro.put("caption", chartCaption);
	}

	public String getChart_xAxisName() {
		return chart_xAxisName;
	}

	public void setChart_xAxisName(String chartXAxisName) {
		chart_xAxisName = chartXAxisName;
		chartNodePro.put("xAxisName", chartXAxisName);
	}

	public String getChart_yAxisName() {
		return chart_yAxisName;
	}

	public void setChart_yAxisName(String chartYAxisName) {
		chart_yAxisName = chartYAxisName;
		chartNodePro.put("yAxisName", chartYAxisName);
	}

	public String getChart_subCaption() {
		return chart_subCaption;
	}

	public void setChart_subCaption(String chartSubCaption) {
		chart_subCaption = chartSubCaption;
		chartNodePro.put("subCaption", chartSubCaption);
	}

	public String getChart_numberPrefix() {
		return chart_numberPrefix;
	}

	public void setChart_numberPrefix(String chartNumberPrefix) {
		chart_numberPrefix = chartNumberPrefix;
		chartNodePro.put("nubmerPrefix", chartNumberPrefix);
	}
	
	

	public String getChart_numberSuffix() {
		return chart_numberSuffix;
	}


	public void setChart_numberSuffix(String chartNumberSuffix) {
		chart_numberSuffix = chartNumberSuffix;
		chartNodePro.put("numberSuffix", chart_numberSuffix);
	}


	public String getChart_showValues() {
		return chart_showValues;
	}

	public void setChart_showValues(String chartShowValues) {
		chart_showValues = chartShowValues;
		chartNodePro.put("showValues", chartShowValues);
	}

	public String getChart_decimalPrecision() {
		return chart_decimalPrecision;
	}

	public void setChart_decimalPrecision(String chartDecimalPrecision) {
		chart_decimalPrecision = chartDecimalPrecision;
		chartNodePro.put("decimalPrecision", chartDecimalPrecision);
	}

	public String getChart_bgcolor() {
		return chart_bgcolor;
	}

	public void setChart_bgcolor(String chartBgcolor) {
		chart_bgcolor = chartBgcolor;
		chartNodePro.put("bgcolor", chartBgcolor);
	}

	public String getChart_bgAlpha() {
		return chart_bgAlpha;
	}

	public void setChart_bgAlpha(String chartBgAlpha) {
		chart_bgAlpha = chartBgAlpha;
		chartNodePro.put("bgAlpha", chartBgAlpha);
	}

	public String getChart_showColumnShadow() {
		return chart_showColumnShadow;
	}

	public void setChart_showColumnShadow(String chartShowColumnShadow) {
		chart_showColumnShadow = chartShowColumnShadow;
		chartNodePro.put("showColumnShadow", chartShowColumnShadow);
	}

	public String getChart_divlinecolor() {
		return chart_divlinecolor;
	}

	public void setChart_divlinecolor(String chartDivlinecolor) {
		chart_divlinecolor = chartDivlinecolor;
		chartNodePro.put("divlinecolor", chartDivlinecolor);
	}

	public String getChart_divLineAlpha() {
		return chart_divLineAlpha;
	}

	public void setChart_divLineAlpha(String chartDivLineAlpha) {
		chart_divLineAlpha = chartDivLineAlpha;
		chartNodePro.put("divLineAlpha", chartDivLineAlpha);
	}

	public String getChart_showAlternateHGridColor() {
		return chart_showAlternateHGridColor;
	}

	public void setChart_showAlternateHGridColor(String chartShowAlternateHGridColor) {
		chart_showAlternateHGridColor = chartShowAlternateHGridColor;
		chartNodePro.put("showAlternateHGridColor", chartShowAlternateHGridColor);
	}

	public String getChart_alternateHGridColor() {
		return chart_alternateHGridColor;
	}

	public void setChart_alternateHGridColor(String chartAlternateHGridColor) {
		chart_alternateHGridColor = chartAlternateHGridColor;
		chartNodePro.put("alternateHGridColor", chartAlternateHGridColor);
	}

	public String getChart_alternateHGridAlpha() {
		return chart_alternateHGridAlpha;
	}

	public void setChart_alternateHGridAlpha(String chartAlternateHGridAlpha) {
		chart_alternateHGridAlpha = chartAlternateHGridAlpha;
		chartNodePro.put("alternateHGridAlpha", chartAlternateHGridAlpha);
	}

	public String getChart_SYAxisMaxValue() {
		return chart_SYAxisMaxValue;
	}

	public void setChart_SYAxisMaxValue(String chartSYAxisMaxValue) {
		chart_SYAxisMaxValue = chartSYAxisMaxValue;
		chartNodePro.put("SYAxisMaxValue", chartSYAxisMaxValue);
	}

	public String getChart_PYAxisName() {
		return chart_PYAxisName;
	}

	public void setChart_PYAxisName(String chartPYAxisName) {
		chart_PYAxisName = chartPYAxisName;
		chartNodePro.put("PYAxisName", chartPYAxisName);
	}

	public String getChart_SYAxisName() {
		return chart_SYAxisName;
	}

	public void setChart_SYAxisName(String chartSYAxisName) {
		chart_SYAxisName = chartSYAxisName;
		chartNodePro.put("SYAxisName", chartSYAxisName);
	}

	public String getChart_rotateLabels() {
		return chart_rotateLabels;
	}


	public void setChart_rotateLabels(String chartRotateLabels) {
		chartNodePro.put("rotateLabels", chartRotateLabels);
		this.chart_rotateLabels = chartRotateLabels;
	}


	public String getChart_slantLabels() {
		return chart_slantLabels;
	}


	public void setChart_slantLabels(String chartSlantLabels) {
		chartNodePro.put("slantLabels", chartSlantLabels);
		this.chart_slantLabels = chartSlantLabels;
	}


	public String getChart_exportEnabled() {
		return chart_exportEnabled;
	}


	public void setChart_exportEnabled(String chartExportEnabled) {
		chart_exportEnabled = chartExportEnabled;
		chartNodePro.put("exportEnabled", chartExportEnabled);
	}


	public String getChart_exportAtClient() {
		return chart_exportAtClient;
	}


	public void setChart_exportAtClient(String chartExportAtClient) {
		chart_exportAtClient = chartExportAtClient;
		chartNodePro.put("exportAtClient", chartExportAtClient);
	}


	public String getChart_exportAction() {
		return chart_exportAction;
	}


	public void setChart_exportAction(String chartExportAction) {
		chart_exportAction = chartExportAction;
		chartNodePro.put("exportAction", chartExportAction);
	}


	public String getChart_exportHandler() {
		return chart_exportHandler;
	}


	public void setChart_exportHandler(String chartExportHandler) {
		chart_exportHandler = chartExportHandler;
		chartNodePro.put("exportHandler", chartExportHandler);
	}


	public String getChart_exportFileName() {
		return chart_exportFileName;
	}


	public void setChart_exportFileName(String chartExportFileName) {
		chart_exportFileName = chartExportFileName;
		chartNodePro.put("exportFileName", chartExportFileName);
	}


	public String getChart_exportTargetWindow() {
		return chart_exportTargetWindow;
	}


	public void setChart_exportTargetWindow(String chartExportTargetWindow) {
		chart_exportTargetWindow = chartExportTargetWindow;
		chartNodePro.put("exportTargetWindow", chartExportTargetWindow);
	}


	public List getColorListOneDimension() {
		return ColorListOneDimension;
	}

	public void setColorListOneDimension(ArrayList colorListOneDimension) {
		ColorListOneDimension = colorListOneDimension;
	}

	public List getxListOneDimension() {
		return xListOneDimension;
	}

	public void setxListOneDimension(ArrayList xListOneDimension) {
		this.xListOneDimension = xListOneDimension;
	}

	public List getyListOneDimension() {
		return yListOneDimension;
	}

	public void setyListOneDimension(ArrayList yListOneDimension) {
		this.yListOneDimension = yListOneDimension;
	}

	public List getxListManyDimension() {
		return xListManyDimension;
	}

	public void setxListManyDimension(ArrayList xListManyDimension) {
		this.xListManyDimension = xListManyDimension;
	}

	public List getSeriesNameList() {
		return seriesNameList;
	}

	public void setSeriesNameList(ArrayList seriesNameList) {
		this.seriesNameList = seriesNameList;
	}

	public String getXKeyOneDimension() {
		return XKeyOneDimension;
	}

	public void setXKeyOneDimension(String xKeyOneDimension) {
		XKeyOneDimension = xKeyOneDimension;
	}

	public String getYKeyOneDimension() {
		return YKeyOneDimension;
	}

	public void setYKeyOneDimension(String yKeyOneDimension) {
		YKeyOneDimension = yKeyOneDimension;
	}

	public String getXKeyManyDimension() {
		return XKeyManyDimension;
	}

	public void setXKeyManyDimension(String xKeyManyDimension) {
		XKeyManyDimension = xKeyManyDimension;
	}

	public String getYKeyManyDimension() {
		return YKeyManyDimension;
	}

	public void setYKeyManyDimension(String yKeyManyDimension) {
		YKeyManyDimension = yKeyManyDimension;
	}

	public boolean isChartClickEnable() {
		return chartClickEnable;
	}

	public void setChartClickEnable(boolean chartClickEnable) {
		this.chartClickEnable = chartClickEnable;
	}


	public Map getTrendlines_linePro() {
		return trendlines_linePro;
	}


	public void setTrendlines_linePro(Map trendlinesLinePro) {
		trendlines_linePro = trendlinesLinePro;
	}


	public String getTrendlines_line_startValue() {
		return trendlines_line_startValue;
	}


	public void setTrendlines_line_startValue(String trendlinesLineStartValue) {
		trendlines_line_startValue = trendlinesLineStartValue;
		this.trendlines_linePro.put("startValue", trendlinesLineStartValue);
	}


	public String getTrendlines_line_displayValue() {
		return trendlines_line_displayValue;
	}


	public void setTrendlines_line_displayValue(String trendlinesLineDisplayValue) {
		trendlines_line_displayValue = trendlinesLineDisplayValue;
		this.trendlines_linePro.put("displayValue", trendlinesLineDisplayValue);
	}


	public String getTrendlines_line_showOnTop() {
		return trendlines_line_showOnTop;
	}


	public void setTrendlines_line_showOnTop(String trendlinesLineShowOnTop) {
		trendlines_line_showOnTop = trendlinesLineShowOnTop;
		this.trendlines_linePro.put("showOnTop", trendlinesLineShowOnTop);
	}


	public String getTrendlines_line_color() {
		return trendlines_line_color;
	}


	public void setTrendlines_line_color(String trendlinesLineColor) {
		trendlines_line_color = trendlinesLineColor;
		this.trendlines_linePro.put("color", trendlinesLineColor);
	}


	public String getTrendlines_line_thickness() {
		return trendlines_line_thickness;
	}


	public void setTrendlines_line_thickness(String trendlines_line_thickness) {
		this.trendlines_line_thickness = trendlines_line_thickness;
		this.trendlines_linePro.put("thickness", trendlines_line_thickness);
	}


	public String getDataset_parentYAxis(String seriesName) {
		return dataSetNodePro.get(seriesName+"_parentYAxis");
	}


	public void setDataset_parentYAxis(String seriesName, String datasetParentYAxis) {
		dataSetNodePro.put(seriesName+"_parentYAxis", datasetParentYAxis);
	}


	public String getDataset_color(String seriesName) {
		return dataSetNodePro.get(seriesName+"_color");
	}


	public void setDataset_color(String seriesName,String datasetColor) {
		dataSetNodePro.put(seriesName+"_parentYAxis", datasetColor);
	}


	public String getDataset_numberPrefix(String seriesName) {
		return dataSetNodePro.get(seriesName+"_numberPrefix");
	}


	public void setDataset_numberPrefix(String seriesName, String datasetNumberPrefix) {
		dataSetNodePro.put(seriesName+"_numberPrefix", datasetNumberPrefix);
	}


	public String getDataset_anchorSides(String seriesName) {
		return dataSetNodePro.get(seriesName+"_anchorSides");
	}


	public void setDataset_anchorSides(String seriesName, String datasetAnchorSides) {
		dataSetNodePro.put(seriesName+"_anchorSides", datasetAnchorSides);
	}


	public String getDataset_anchorRadius(String seriesName) {
		return dataSetNodePro.get(seriesName+"_anchorRadius");
	}


	public void setDataset_anchorRadius(String seriesName, String datasetAnchorRadius) {
		dataSetNodePro.put(seriesName+"_anchorRadius", datasetAnchorRadius);
	}


	public String getDataset_anchorBorderColor(String seriesName) {
		return dataSetNodePro.get(seriesName+"_anchorBorderColor");
	}


	public void setDataset_anchorBorderColor(String seriesName, String datasetAnchorBorderColor) {
		dataSetNodePro.put(seriesName+"_anchorBorderColor", datasetAnchorBorderColor);
	}


	public Map<String, String> getChartNodePro() {
		return chartNodePro;
	}


	public void setChartNodePro(Map<String, String> chartNodePro) {
		this.chartNodePro = chartNodePro;
	}


	public Map getUserDefinedProperty() {
		return userDefinedProperty;
	}


	public void setUserDefinedProperty(Map userDefinedProperty) {
		this.userDefinedProperty = userDefinedProperty;
	}


	public String getChart_useRoundEdges() {
		return chart_useRoundEdges;
	}


	public void setChart_useRoundEdges(String chart_useRoundEdges) {
		this.chart_useRoundEdges = chart_useRoundEdges;
		chartNodePro.put("useRoundEdges", chart_useRoundEdges);
	}


	public String getChart_formatNumberScale() {
		return chart_formatNumberScale;
	}


	public void setChart_formatNumberScale(String chart_formatNumberScale) {
		this.chart_formatNumberScale = chart_formatNumberScale;
		chartNodePro.put("formatNumberScale", chart_formatNumberScale);
	}


	public String getChart_canvasBorderThickness() {
		return chart_canvasBorderThickness;
	}


	public void setChart_canvasBorderThickness(String chart_canvasBorderThickness) {
		this.chart_canvasBorderThickness = chart_canvasBorderThickness;
		chartNodePro.put("canvasBorderThickness", chart_canvasBorderThickness);
	}


	public String getChart_showBorder() {
		return chart_showBorder;
	}


	public void setChart_showBorder(String chart_showBorder) {
		this.chart_showBorder = chart_showBorder;
		chartNodePro.put("showBorder", chart_showBorder);
	}


	public String getChart_yAxisMinValue() {
		return chart_yAxisMinValue;
	}


	public void setChart_yAxisMinValue(String chart_yAxisMinValue) {
		this.chart_yAxisMinValue = chart_yAxisMinValue;
		chartNodePro.put("yAxisMinValue", chart_yAxisMinValue);
	}


	public String getChart_yAxisMaxValue() {
		return chart_yAxisMaxValue;
	}


	public void setChart_yAxisMaxValue(String chart_yAxisMaxValue) {
		this.chart_yAxisMaxValue = chart_yAxisMaxValue;
		chartNodePro.put("yAxisMaxValue", chart_yAxisMaxValue);
	}
	
	public String getChart_Decimals() {
		return chart_Decimals;
	}

	public void setChart_Decimals(String chart_Decimals) {
		this.chart_Decimals = chart_Decimals;
		chartNodePro.put("Decimals", chart_Decimals);
	}

	public String getChart_labelStep() {
		return chart_labelStep;
	}

	public void setChart_labelStep(String chart_labelStep) {
		this.chart_labelStep = chart_labelStep;
		chartNodePro.put("labelStep", chart_labelStep);
	}

	public String getChart_anchorRadius() {
		return chart_anchorRadius;
	}

	public void setChart_anchorRadius(String chart_anchorRadius) {
		this.chart_anchorRadius = chart_anchorRadius;
		chartNodePro.put("anchorRadius", chart_anchorRadius);
	}


	public String getChart_showLabels() {
		return chart_showLabels;
	}


	public void setChart_showLabels(String chart_showLabels) {
		this.chart_showLabels = chart_showLabels;
		chartNodePro.put("showLabels", chart_showLabels);
	}


	public Map getYAxisMap() {
		return YAxismap;
	}
	
	public void setYAxisMap(String seriesName,String parentYAxis) {
		YAxismap.put(seriesName, parentYAxis);
	}
	
	public void setLeftYAxis(String seriesName){
		YAxismap.put(seriesName,"L");
	}

	public void setRightYAxis(String seriesName){
		YAxismap.put(seriesName,"R");
	}
}
