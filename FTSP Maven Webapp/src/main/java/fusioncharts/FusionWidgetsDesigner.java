package fusioncharts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FusionWidgetsDesigner {
	private Map<String,String> chartNodePro = new HashMap<String, String>();
	private List<Map> dials = new ArrayList<Map>();
	private List<Map> colorRange=new ArrayList<Map>();
	
//	The properties for the <chart .....></chart>
	//刻度值上限
	private String upperLimit="";
	//刻度值下限
	private String lowerLimit="";
	//刻度值上限名称
	private String upperLimitDisplay="";
	//刻度值下限名称
	private String lowerLimitDisplay="";
	//是否显示极限值
	private String Limits="1";
	//刻度值字体颜色
	private String baseFontColor="";
	//需要将仪表盘分成的等份值
	private String majorTMNumber="";
	//刻度线的颜色
	private String majorTMColor="";
	//刻度线的长度
	private String majorTMHeight="";
	//仪表盘刻度线间小刻度线的数量
	private String minorTMNumber="";
	//仪表盘刻度线间小刻度线颜色
	private String minorTMColor="";
	//仪表盘刻度线间小刻度线长度
	private String minorTMHeight="";
	//针心圆半径
	private String pivotRadius="";
	//是否显示刻度盘边框
	private String showGaugeBorder ="";
	//开始刻度盘角度
	private String gaugeStartAngle ="";
	//结束刻度盘角度
	private String gaugeEndAngle ="";
	//刻度盘外围半径
	private String gaugeOuterRadius ="";
	//刻度盘内围半径
	private String gaugeInnerRadius ="";
	//刻度盘圆心X坐标
	private String gaugeOriginX ="";
	//刻度盘圆心Y坐标
	private String gaugeOriginY ="";
	//刻度盘比例（度数）
	private String gaugeScaleAngle ="";
	//显示值与刻度线的距离
	private String displayValueDistance ="";
	//显示值是否位于刻度盘的内部
	private String placeValuesInside ="";
	//刻度盘颜色是否混合
	private String gaugeFillMix ="";
	//仪表盘轴心是否混合
	private String pivotFillMix ="";
	//轴心边框颜色
	private String pivotBorderColor ="";
	//轴心比率
	private String pivotfillRatio ="";
	//是否显示阴影
	private String showShadow="";
	//数值后缀
	private String numberSuffix="";
	//数值前缀
	private String numberPrefix="";
	//是否显示值
	private String showValue="";
	//动画效果
	private String palette="";
//	The properties for the <colorRange><color..../></colorRange>	
	private String color_minValue="";
	private String color_maxValue="";
	private String color_code="";
	private String color_name="";
	private String color_alpha="";
//	The properties for the <dials><dial..../></dials>	
	private String dial_value="";
	private String dial_borderAlpha="";
	private String dial_baseWidth="";
	private String dial_topWidth="";
//	The properties for the <trendpoints><point..../></trendpoints>	
	
	
	
	public FusionWidgetsDesigner(){
//		chartNodePro
		chartNodePro.put("upperLimit", "100");
		chartNodePro.put("lowerLimit", "0");
		chartNodePro.put("lowerLimitDisplay", "good");
		chartNodePro.put("upperLimitDisplay", "bad");
		chartNodePro.put("palette", "1");
		//chartNodePro.put("tickValueDistance", "10");
		chartNodePro.put("showValue", "1");
	}
	
	
	/**
	 * 
	 * Method name: setColorRange <BR>
	 * Description: 定义仪表盘分段制，minvalue为最小值，maxvalue为最大值，code为颜色 <BR>
	 * Remark: <BR>
	 * @param minValue
	 * @param maxValue
	 * @param code  void<BR>
	 */
	public void setColorRange(String minValue,String maxValue,String code){
		Map map = new HashMap();
		map.put("minValue", minValue);
		map.put("maxValue", maxValue);
		map.put("code", code);
		colorRange.add(map);
	}
	
	public void setColorRangeDefault(){
		Map map = new HashMap();
		map.put("minValue", "0");
		map.put("maxValue", "50");
		map.put("code", "8BBA00");
		colorRange.add(map);
		map.put("minValue", "50");
		map.put("maxValue", "80");
		map.put("code", "F6BD0F");
		colorRange.add(map);
		map.put("minValue", "80");
		map.put("maxValue", "100");
		map.put("code", "FF654F");
		colorRange.add(map);
	}
	
	public void setDial(String dial){
		Map map = new HashMap();
		map.put("value", dial);
		dials.add(map);
	}
	
	private StringBuffer drawXMLTitle(){
		StringBuffer buffer = new StringBuffer("");
		buffer.append("<chart ");
		Iterator it = this.chartNodePro.keySet().iterator();
		while(it.hasNext()){
			String key = it.next().toString().replace("chart_", "");
			String value = this.chartNodePro.get(key).toString();
			buffer.append(key + "='" + value + "' ");
		}
		buffer.append("> ");
		return buffer;
	}
	
	private StringBuffer drawColorRange(){
		StringBuffer buffer = new StringBuffer("");
		buffer.append("<colorRange>");
		Map map = new HashMap();
		String minValue = "";
		String maxValue ="";
		String code ="";
		for (int i = 0; i < colorRange.size(); i++) {
			map = colorRange.get(i);
			buffer.append("<color ");
			if(map.containsKey("minValue")){
				buffer.append(" minValue = '"+map.get("minValue")+"' ");
			}
			if(map.containsKey("maxValue")){
				buffer.append(" maxValue = '"+map.get("maxValue")+"' ");
			}
			if(map.containsKey("code")){
				buffer.append(" code = '"+map.get("code")+"' ");
			}
			buffer.append(" />");
		}
		buffer.append("</colorRange>");
		return buffer;
	}
	
	private StringBuffer drawDials(){
		StringBuffer buffer = new StringBuffer("");
		buffer.append("<dials>");
		Map map = new HashMap();
		for (int i = 0; i < dials.size(); i++) {
			map = dials.get(i);
			buffer.append("<dial value = '"+map.get("value")+"' />");
		}
		
		buffer.append("</dials>");
		return buffer;
	}
	
	public String getAngularGaugeXML(){
		StringBuffer buffer = drawXMLTitle();
		buffer.append(drawColorRange());
		buffer.append(drawDials());
		buffer.append("</chart> ");
		return buffer.toString();
	}


	public Map<String, String> getChartNodePro() {
		return chartNodePro;
	}


	public void setChartNodePro(Map<String, String> chartNodePro) {
		this.chartNodePro = chartNodePro;
	}


	public String getBaseFontColor() {
		return baseFontColor;
	}


	public void setBaseFontColor(String baseFontColor) {
		this.baseFontColor = baseFontColor;
		chartNodePro.put("baseFontColor", baseFontColor);
	}


	public String getColor_minValue() {
		return color_minValue;
	}


	public void setColor_minValue(String color_minValue) {
		this.color_minValue = color_minValue;
	}


	public String getColor_maxValue() {
		return color_maxValue;
	}


	public void setColor_maxValue(String color_maxValue) {
		this.color_maxValue = color_maxValue;
	}


	public String getColor_code() {
		return color_code;
	}


	public void setColor_code(String color_code) {
		this.color_code = color_code;
	}


	public String getColor_name() {
		return color_name;
	}


	public void setColor_name(String color_name) {
		this.color_name = color_name;
	}


	public String getColor_alpha() {
		return color_alpha;
	}


	public void setColor_alpha(String color_alpha) {
		this.color_alpha = color_alpha;
	}


	public String getDial_value() {
		return dial_value;
	}


	public void setDial_value(String dial_value) {
		this.dial_value = dial_value;
	}


	public String getDial_borderAlpha() {
		return dial_borderAlpha;
	}


	public void setDial_borderAlpha(String dial_borderAlpha) {
		this.dial_borderAlpha = dial_borderAlpha;
	}


	public String getDial_baseWidth() {
		return dial_baseWidth;
	}


	public void setDial_baseWidth(String dial_baseWidth) {
		this.dial_baseWidth = dial_baseWidth;
	}


	public String getDial_topWidth() {
		return dial_topWidth;
	}


	public void setDial_topWidth(String dial_topWidth) {
		this.dial_topWidth = dial_topWidth;
	}


	public String getUpperLimit() {
		return upperLimit;
	}


	public void setUpperLimit(String upperLimit) {
		this.upperLimit = upperLimit;
		chartNodePro.put("upperLimit", upperLimit);
	}


	public String getLowerLimit() {
		return lowerLimit;
	}


	public void setLowerLimit(String lowerLimit) {
		this.lowerLimit = lowerLimit;
		chartNodePro.put("lowerLimit", lowerLimit);
	}


	public String getUpperLimitDisplay() {
		return upperLimitDisplay;
	}


	public void setUpperLimitDisplay(String upperLimitDisplay) {
		this.upperLimitDisplay = upperLimitDisplay;
		chartNodePro.put("upperLimitDisplay", upperLimitDisplay);
	}


	public String getLowerLimitDisplay() {
		return lowerLimitDisplay;
	}


	public void setLowerLimitDisplay(String lowerLimitDisplay) {
		this.lowerLimitDisplay = lowerLimitDisplay;
		chartNodePro.put("lowerLimitDisplay", lowerLimitDisplay);
	}


	public String getLimits() {
		return Limits;
	}


	public void setLimits(String limits) {
		Limits = limits;
		chartNodePro.put("limits", limits);
	}


	public String getMajorTMNumber() {
		return majorTMNumber;
	}


	public void setMajorTMNumber(String majorTMNumber) {
		this.majorTMNumber = majorTMNumber;
		chartNodePro.put("majorTMNumber", majorTMNumber);
	}


	public String getMajorTMColor() {
		return majorTMColor;
	}


	public void setMajorTMColor(String majorTMColor) {
		this.majorTMColor = majorTMColor;
		chartNodePro.put("majorTMColor", majorTMColor);
	}


	public String getMajorTMHeight() {
		return majorTMHeight;
	}


	public void setMajorTMHeight(String majorTMHeight) {
		this.majorTMHeight = majorTMHeight;
		chartNodePro.put("majorTMHeight", majorTMHeight);
	}


	public String getMinorTMNumber() {
		return minorTMNumber;
	}


	public void setMinorTMNumber(String minorTMNumber) {
		this.minorTMNumber = minorTMNumber;
		chartNodePro.put("minorTMNumber", minorTMNumber);
	}


	public String getMinorTMColor() {
		return minorTMColor;
	}


	public void setMinorTMColor(String minorTMColor) {
		this.minorTMColor = minorTMColor;
		chartNodePro.put("minorTMColor", minorTMColor);
	}


	public String getMinorTMHeight() {
		return minorTMHeight;
	}


	public void setMinorTMHeight(String minorTMHeight) {
		this.minorTMHeight = minorTMHeight;
		chartNodePro.put("minorTMHeight", minorTMHeight);
	}


	public String getPivotRadius() {
		return pivotRadius;
	}


	public void setPivotRadius(String pivotRadius) {
		this.pivotRadius = pivotRadius;
		chartNodePro.put("pivotRadius", pivotRadius);
	}


	public String getShowGaugeBorder() {
		return showGaugeBorder;
	}


	public void setShowGaugeBorder(String showGaugeBorder) {
		this.showGaugeBorder = showGaugeBorder;
		chartNodePro.put("showGaugeBorder", showGaugeBorder);
	}


	public String getGaugeStartAngle() {
		return gaugeStartAngle;
	}


	public void setGaugeStartAngle(String gaugeStartAngle) {
		this.gaugeStartAngle = gaugeStartAngle;
		chartNodePro.put("gaugeStartAngle", gaugeStartAngle);
	}


	public String getGaugeEndAngle() {
		return gaugeEndAngle;
	}


	public void setGaugeEndAngle(String gaugeEndAngle) {
		this.gaugeEndAngle = gaugeEndAngle;
		chartNodePro.put("gaugeEndAngle", gaugeEndAngle);
	}


	public String getGaugeOuterRadius() {
		return gaugeOuterRadius;
	}


	public void setGaugeOuterRadius(String gaugeOuterRadius) {
		this.gaugeOuterRadius = gaugeOuterRadius;
		chartNodePro.put("gaugeOuterRadius", gaugeOuterRadius);
	}


	public String getGaugeInnerRadius() {
		return gaugeInnerRadius;
	}


	public void setGaugeInnerRadius(String gaugeInnerRadius) {
		this.gaugeInnerRadius = gaugeInnerRadius;
		chartNodePro.put("gaugeInnerRadius", gaugeInnerRadius);
	}


	public String getGaugeOriginX() {
		return gaugeOriginX;
	}


	public void setGaugeOriginX(String gaugeOriginX) {
		this.gaugeOriginX = gaugeOriginX;
		chartNodePro.put("gaugeOriginX", gaugeOriginX);
	}


	public String getGaugeOriginY() {
		return gaugeOriginY;
	}


	public void setGaugeOriginY(String gaugeOriginY) {
		this.gaugeOriginY = gaugeOriginY;
		chartNodePro.put("gaugeOriginY", gaugeOriginY);
	}


	public String getGaugeScaleAngle() {
		return gaugeScaleAngle;
	}


	public void setGaugeScaleAngle(String gaugeScaleAngle) {
		this.gaugeScaleAngle = gaugeScaleAngle;
		chartNodePro.put("gaugeScaleAngle", gaugeScaleAngle);
	}


	public String getDisplayValueDistance() {
		return displayValueDistance;
	}


	public void setDisplayValueDistance(String displayValueDistance) {
		this.displayValueDistance = displayValueDistance;
		chartNodePro.put("displayValueDistance", displayValueDistance);
	}


	public String getPlaceValuesInside() {
		return placeValuesInside;
	}


	public void setPlaceValuesInside(String placeValuesInside) {
		this.placeValuesInside = placeValuesInside;
		chartNodePro.put("placeValuesInside", placeValuesInside);
	}


	public String getGaugeFillMix() {
		return gaugeFillMix;
	}


	public void setGaugeFillMix(String gaugeFillMix) {
		this.gaugeFillMix = gaugeFillMix;
		chartNodePro.put("gaugeFillMix", gaugeFillMix);
	}


	public String getPivotFillMix() {
		return pivotFillMix;
	}


	public void setPivotFillMix(String pivotFillMix) {
		this.pivotFillMix = pivotFillMix;
		chartNodePro.put("pivotFillMix", pivotFillMix);
	}


	public String getPivotBorderColor() {
		return pivotBorderColor;
	}


	public void setPivotBorderColor(String pivotBorderColor) {
		this.pivotBorderColor = pivotBorderColor;
		chartNodePro.put("pivotBorderColor", pivotBorderColor);
	}


	public String getPivotfillRatio() {
		return pivotfillRatio;
	}


	public void setPivotfillRatio(String pivotfillRatio) {
		this.pivotfillRatio = pivotfillRatio;
		chartNodePro.put("pivotfillRatio", pivotfillRatio);
	}


	public String getShowShadow() {
		return showShadow;
	}


	public void setShowShadow(String showShadow) {
		this.showShadow = showShadow;
		chartNodePro.put("showShadow", showShadow);
	}


	public String getNumberSuffix() {
		return numberSuffix;
	}


	public void setNumberSuffix(String numberSuffix) {
		this.numberSuffix = numberSuffix;
		chartNodePro.put("numberSuffix", numberSuffix);
	}


	public String getNumberPrefix() {
		return numberPrefix;
	}


	public void setNumberPrefix(String numberPrefix) {
		this.numberPrefix = numberPrefix;
		chartNodePro.put("numberPrefix", numberPrefix);
	}


	public String getShowValue() {
		return showValue;
	}


	public void setShowValue(String showValue) {
		this.showValue = showValue;
		chartNodePro.put("showValue", showValue);
	}


	public String getPalette() {
		return palette;
	}


	public void setPalette(String palette) {
		this.palette = palette;
		chartNodePro.put("palette", palette);
	}

}
