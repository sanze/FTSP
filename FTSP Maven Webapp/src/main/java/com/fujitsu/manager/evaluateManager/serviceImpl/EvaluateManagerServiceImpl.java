package com.fujitsu.manager.evaluateManager.serviceImpl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.IService.ICommonManagerService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.dao.mysql.EvaluateManagerMapper;
import com.fujitsu.manager.evaluateManager.service.EvaluateManagerService;

import fusioncharts.FusionChartDesigner;

@Scope("prototype")
@Service
@Transactional(rollbackFor = Exception.class)
public class EvaluateManagerServiceImpl extends EvaluateManagerService{
	@Resource
	ICommonManagerService commonManagerService;
	@Resource
	EvaluateManagerMapper evaluateManagerMapper;
	/**
	 * 获取偏差值信息
	 * 
	 * @throws CommonException
	 */
	public Map<String,Object> getOffsetValue() throws CommonException {

		Map<String,Object> offsetParam = null;

		offsetParam = commonManagerService.getSysParam("RESOURCE_OPTICAL_LINK_OFFSET");

		if (offsetParam != null&&!offsetParam.isEmpty()) {
			Object paramValue=offsetParam.get("PARAM_VALUE");
			if(paramValue!=null){
				String[] offsets=paramValue.toString().split(",");
				if(offsets.length>=3&&
						offsets[0].matches("\\d*(\\.\\d*)?")&&
						offsets[1].matches("\\d*(\\.\\d*)?")&&
						offsets[2].matches("\\d*(\\.\\d*)?")){
					offsetParam.put("upperOffset", offsets[0]);
					offsetParam.put("middleOffset", offsets[1]);
					offsetParam.put("downOffset", offsets[2]);
					return offsetParam;
				}
			}
		}
		offsetParam = new HashMap<String,Object>();
		offsetParam.put("PARAM_KEY", "RESOURCE_OPTICAL_LINK_OFFSET");
		String offset="3,2,1.5";
		offsetParam.put("PARAM_VALUE", offset);
		commonManagerService.setSysParam(offsetParam);
		offsetParam.put("upperOffset", offset.split(",")[0]);
		offsetParam.put("middleOffset", offset.split(",")[1]);
		offsetParam.put("downOffset", offset.split(",")[2]);
		return offsetParam;
	}
	/**
	 * 修改偏差值信息
	 * 
	 * @throws CommonException
	 */
	public void modifyOffsetValue(String upperOffset, String middleOffset, String downOffset)
			throws CommonException {

		Map<String,Object> offsetParam = new HashMap<String,Object>();
		offsetParam.put("PARAM_KEY", "RESOURCE_OPTICAL_LINK_OFFSET");
		offsetParam.put("PARAM_VALUE", String.format("%s,%s,%s", 
				upperOffset,middleOffset,downOffset));

		commonManagerService.setSysParam(offsetParam);
	}
	public Map<String,Object> searchFiberLink(
			Map<String, Object> param, int start,
			int limit) throws CommonException {
		Integer total = 0;
		param.put("total", total);
		List<Map<String,Object>> rows = evaluateManagerMapper.callFiberLinkPmSP(param,start,limit);
		total = (param.get("total")+"").matches("\\d+")?Integer.valueOf((param.get("total"))+""):0;
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("rows", rows);
		result.put("total", total);
		return result;
	}
	@Override
	public Map<String, Object> generateDiagram(Map<String, Object> param)
			throws CommonException {
		Map<String, Object> returnResult = new HashMap<String, Object>();
		try{
		String nendOrFend = param.get("nendOrFend").toString();
		if(nendOrFend.equals("fend")){
			Integer fendLinkId = evaluateManagerMapper.getFendLinkId(param);
			if(fendLinkId==null){
				returnResult.put("chartXml", "");
				return returnResult;
			}
			param.put("RESOURCE_LINK_ID", fendLinkId);
		}
		param.put("total", "1");
		List<Map> rows = (List)evaluateManagerMapper.callFiberLinkPmSP(param,0,0);
		String chartName = null;
		if(rows.size()>0){
			String direction;
			if(rows.get(0).get("A_END_STATION")==null||rows.get(0).get("Z_END_STATION")==null){
				direction=rows.get(0).get("A_NE_NAME")+"->"+rows.get(0).get("Z_NE_NAME");
			}else{
				direction=rows.get(0).get("A_END_STATION")+"->"+rows.get(0).get("Z_END_STATION");
			}
			chartName = "系统名称："
						+ rows.get(0).get("PROJECT_NAME")
						+ "  方向："
						+ direction
						+ "  链路："
						+ rows.get(0).get("LINK_NAME");
		}else{
			returnResult.put("chartXml", "");
			return returnResult;
		}
		String[] displayItems = param.get("displayItems").toString().split(",");
		FusionChartDesigner fcd = new FusionChartDesigner();
		setFusionChartSettings(fcd);
		fcd.setChart_caption(chartName);
		Double chart_yAxisMinValue=null;
		Double chart_yAxisMaxValue=null;
		for(String item:displayItems){
			fcd.addSerialDatasList(DIAGRAM_MAP.get(item), rows, "COLLECT_DATE", item);
			for(Map<String, Object> row:rows){
				Object yValue=row.get(item);
				if(yValue!=null){
					Double value=Double.valueOf(yValue.toString());
					if(chart_yAxisMinValue==null||value<chart_yAxisMinValue){
						chart_yAxisMinValue=value;
					}
					if(chart_yAxisMaxValue==null||value>chart_yAxisMaxValue){
						chart_yAxisMaxValue=value;
					}
				}
			}
		}
		Double offset=1.0;
		if(chart_yAxisMaxValue!=null&&chart_yAxisMinValue!=null&&
			(chart_yAxisMaxValue-chart_yAxisMinValue)<1){
			offset=0.1;
		}
		if(chart_yAxisMinValue!=null){
			if(offset>=1)
				fcd.setChart_yAxisMinValue(""+Math.floor(chart_yAxisMinValue-offset));
			else
				fcd.setChart_yAxisMinValue(""+Math.floor((chart_yAxisMinValue-offset)*10)/10);
		}
		if(chart_yAxisMaxValue!=null){
			if(offset>=1)
				fcd.setChart_yAxisMaxValue(""+Math.ceil(chart_yAxisMaxValue+offset));
			else
				fcd.setChart_yAxisMaxValue(""+Math.ceil((chart_yAxisMaxValue+offset)*10)/10);
		}
		String chartXml = fcd.getManyDimensionsXmlData();
		returnResult.put("chartXml", chartXml);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return returnResult;
	}
	
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

	public Map<String,Object> getAllResourceLink(
			Map<String, Object> param, int start,
			int limit) throws CommonException {
		try{
			Integer total = evaluateManagerMapper.cntAllResourceLink(param);
			List<Map<String,Object>> rows = evaluateManagerMapper.getAllResourceLink(param,start,limit);
			Map<String,Object> result = new HashMap<String,Object>();
			result.put("rows", rows);
			result.put("total", total);
			return result;
		}catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
	}
	
	public void deleteResourceLink(
			Map<String, Object> param) throws CommonException {
		try{
			evaluateManagerMapper.deleteResourceLink(param);
		}catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
	}
	public void setResourceLink(
			Map<String, Object> param) throws CommonException {
		try{
			if(param.get("RESOURCE_LINK_ID")==null||
//				param.get("RESOURCE_FIBER_ID")==null||
				param.get("A_PTP_MAIN")==null||
				param.get("Z_PTP_MAIN")==null){
				throw new CommonException(new Exception(), MessageCodeDefine.COM_EXCPT_INVALID_INPUT);
			}
			evaluateManagerMapper.relateResourceLink(param);
			evaluateManagerMapper.setResourceLink(param);
		}catch (CommonException e) {
			throw e;
		}catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
	}
	
	//--------------------------------------链路评估统计部分-----------------------------------
	@Override
	public Map<String, Object> generateDiagramLine(Map<String, Object> param)
			throws CommonException {
 		Map<String, Object> returnResult = new HashMap<String, Object>();
		Map data = new HashMap();
		try{
			Calendar date = Calendar.getInstance();
			String time = param.get("month").toString();  
			date.set(Calendar.YEAR, Integer.valueOf(time.split("-")[0]));  
			date.set(Calendar.MONTH,  Integer.valueOf(time.split("-")[1])-1); 
			date.set(Calendar.DATE, 1);  
			date.roll(Calendar.DATE, -1);   
			int days = date.getActualMaximum(Calendar.DAY_OF_MONTH); 
		
			param.put("START_DATE", param.get("month").toString()+"-01");
			param.put("END_DATE", param.get("month").toString()+"-"+days); 
			param.put("total", 0);
			List<Map> rows = (List)evaluateManagerMapper.callFiberLinkPmSP(param,0,0);
			int rSize = rows.size();
			if(rSize>0){
				//合并RESOURCE_LINK_ID相同的数据
				List<List> lists = new ArrayList();
				lists=megereDataList(rows,"RESOURCE_LINK_ID");
				int cntRtn = 0;
				List rList = new ArrayList();
				for(int i=0;i<lists.size();i++){
					Map<String,Object> line=new HashMap<String,Object>();
					Map map = (Map)lists.get(i).get(0); 
					String chartName;
					if(map.get("A_END_STATION")==null||map.get("Z_END_STATION")==null){
						chartName=map.get("A_NE_NAME")+"->"+map.get("Z_NE_NAME");
					}else{
						chartName=map.get("A_END_STATION")+"->"+map.get("Z_END_STATION");
					}  
					FusionChartDesigner fcd = new FusionChartDesigner();  
					setFusionChartSettings(fcd);  
					fcd.setChart_caption(chartName);
					//节点颜色
					String[] anchorBgColors = {"#33CC00","#3366CC","#FF9933","#FF0000"};
					fcd.setAnchorBgColors(anchorBgColors);
					//取纵坐标最大最小值  
					double YMin = 100000;
					double YMax = 0;
					boolean isE=false;
					for(int j=0;j<lists.get(i).size();j++){ 
						Map dataMap = (Map)lists.get(i).get(j);
						Double ATT_VALUE=null;
						Double ATT_VALUE_OSC=null;
						if(dataMap.get("ATT_VALUE")!=null){
							ATT_VALUE=Double.valueOf(dataMap.get("ATT_VALUE").toString());
							if(YMin > ATT_VALUE)
								YMin = ATT_VALUE;
							if(YMax < ATT_VALUE)
								YMax = ATT_VALUE;
						}
						if(dataMap.get("ATT_VALUE_OSC")!=null){
							ATT_VALUE_OSC=Double.valueOf(dataMap.get("ATT_VALUE_OSC").toString());
							if(YMin > ATT_VALUE_OSC)
								YMin = ATT_VALUE_OSC;
							if(YMax < ATT_VALUE_OSC)
								YMax = ATT_VALUE_OSC;
						}
						if(ATT_VALUE!=null || ATT_VALUE_OSC!=null){
							isE=true;
							cntRtn++;
						}
					}
					if(isE){					
						fcd.setChart_yAxisMinValue(String.valueOf(((int)YMin-1)));
						fcd.setChart_yAxisMaxValue(String.valueOf(((int)YMax+2))); 
						fcd.addSerialDatasList(DIAGRAM_MAP.get("ATT_VALUE"), lists.get(i), "COLLECT_DATE", "ATT_VALUE","OFFSET_LEVEL"); 
						fcd.addSerialDatasList(DIAGRAM_MAP.get("ATT_VALUE_OSC"), lists.get(i), "COLLECT_DATE", "ATT_VALUE_OSC","OFFSET_LEVEL_OSC"); 
						String chartXml = fcd.getManyDimensionsXmlData();
						line.put("lineId",String.valueOf(i)); 
						line.put("chartXml",chartXml); 
						rList.add(line);
					}
				}
				data.put("rList", rList); 
				if(cntRtn==0) data.put("returnMessage","性能数据为空！");
			}else{
				data.put("returnMessage","性能数据为空！");
			}
			data.put("returnResult",CommonDefine.SUCCESS);
			returnResult = JSONObject.fromObject(data); 
		}catch (Exception e) {
			e.printStackTrace();
		}
		return returnResult;
	} 

	
	public Map<String, Object> generateDiagramTable(Map<String, Object> param)
			throws CommonException {
		Map<String, Object> returnResult = new HashMap<String, Object>();
		Map data = new HashMap();
		try{
			Calendar date = Calendar.getInstance();
			String time = param.get("month").toString();  
			date.set(Calendar.YEAR, Integer.valueOf(time.split("-")[0]));  
			date.set(Calendar.MONTH,  Integer.valueOf(time.split("-")[1])-1); 
			date.set(Calendar.DATE, 1);  
			date.roll(Calendar.DATE, -1);   
			int days = date.getActualMaximum(Calendar.DAY_OF_MONTH); 
		
			param.put("START_DATE", param.get("month").toString()+"-01");
			param.put("END_DATE", param.get("month").toString()+"-"+days); 
			param.put("total", 0);
			List<Map> rows = (List)evaluateManagerMapper.callEvaluatelinkPmSP(param,0,0);
			int rSize = rows.size();
			if(rSize>0){
				//合并base_link_id相同的数据
				List<List> lists = new ArrayList();
				lists=megereDataList(rows,"base_link_id"); 
				
				List<List> rList = new ArrayList();
				for(int i=0;i<lists.size();i++){ 
					rList=getTable(rList,lists.get(i)); 
				}
				data.put("rList", rList);
				data.put("days", days);
			}else{
				data.put("returnMessage","性能数据为空！");
			}		
			data.put("returnResult",CommonDefine.SUCCESS);
			returnResult = JSONObject.fromObject(data); 
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnResult;
	} 
	
	/**
	 * 已排序好的数据进行合并处理
	 * @param dataList
	 * @param colName
	 * @return
	 * @throws CommonException
	 */
	public List<List> megereDataList(List<Map> dataList,String colName)throws CommonException {
		List<List> lists = new ArrayList(); 
		Map tmp =(Map) dataList.get(0);
		String tempLinkId =tmp.get(colName).toString();
		int from = 0;
		if(dataList.size()>1){
			for(int i=1;i<dataList.size();i++){
				Map map =(Map) dataList.get(i); 
				if(i==dataList.size()-1){
					if(!tempLinkId.equals(map.get(colName).toString())){
						lists.add(dataList.subList(from, i)); 
						lists.add(dataList.subList(i, i+1)); 
					}else{
						lists.add(dataList.subList(from, i+1)); 
					}
				}else{
					if(!tempLinkId.equals(map.get(colName).toString())){
						lists.add(dataList.subList(from, i)); 
						from = i;
					} 
					tempLinkId = map.get(colName).toString();
				}
			}    
		}else{
			lists.add(dataList.subList(0, 1)); 
		}
		return lists;
	}  
	
	/**
	 * 
	 * @param dataList
	 * @param colName
	 * @param isflag
	 * @return
	 * @throws CommonException
	 */
	public List<Map> sortDataList(List<Map> dataList,String colName,String isflag)throws CommonException {
		Map tMap = new HashMap(); 
		for(int i=0;i<dataList.size()-1;i++){
			for(int j=1;j<dataList.size()-i;j++){
				Map map1 =(Map) dataList.get(j-1);
				Map map2 =(Map) dataList.get(j); 
				if((map1.get(colName).toString()).compareTo(map2.get(colName).toString())>0){
					tMap = map1;
					dataList.set(j-1, map2);
					dataList.set(j, tMap);
				}
			}   
		}  
		if(!"isLine".equals(isflag)){
			dataList.get(0).put("isflag",isflag); 
			if(dataList.get(0).get("A_END_STATION")==null||dataList.get(0).get("Z_END_STATION")==null){
				dataList.get(0).put("direction",dataList.get(0).get("A_NE_NAME")+"->"+dataList.get(0).get("Z_NE_NAME"));
			}else{
				dataList.get(0).put("direction",dataList.get(0).get("A_END_STATION")+"->"+dataList.get(0).get("Z_END_STATION"));
			}  
		}
		return dataList;
	}  
	
	public List getTable(List<List> rtnLists,List<Map> dataList)
			throws CommonException {
 		boolean isOsc=false,isMain=false;
		List<Map> data = new ArrayList();  
		for(Map<String, Object> m : dataList){
			Map<String, Object> n = new HashMap<String, Object>();
			for(Map.Entry<String, Object> entry : m.entrySet()){  
				n.put(entry.getKey(), entry.getValue());  
				if(!isMain && "z_PM_DESCRIPTION".equals(entry.getKey())){ 
					if(entry.getValue()!= null && !"".equals(entry.getValue())){
						isMain=true;
					}
				}
				if(!isOsc && "zOsc_PM_DESCRIPTION".equals(entry.getKey())){ 
					if(entry.getValue()!= null && !"".equals(entry.getValue())){
						isOsc=true;
					}
				}
			}
			data.add(n);
		} 
		if(isMain){
			data=sortDataList(data,"z_PM_DESCRIPTION","isMain");  
			rtnLists.add(megereDataList(data,"z_PM_DESCRIPTION"));
		}
		if(isOsc){
			dataList=sortDataList(dataList,"zOsc_PM_DESCRIPTION","isOsc");   
			rtnLists.add(megereDataList(dataList,"zOsc_PM_DESCRIPTION"));
		} 
		return rtnLists;
	}
	
}
