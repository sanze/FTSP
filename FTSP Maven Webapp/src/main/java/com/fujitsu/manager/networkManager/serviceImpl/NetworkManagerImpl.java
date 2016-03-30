package com.fujitsu.manager.networkManager.serviceImpl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.annotations.Param;
import org.apache.struts2.ServletActionContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.IService.ICommonManagerService;
import com.fujitsu.IService.IExportExcel;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.dao.mysql.AreaManagerMapper;
import com.fujitsu.dao.mysql.CommonManagerMapper;
import com.fujitsu.dao.mysql.NetworkManagerMapper;
import com.fujitsu.manager.networkManager.service.NetworkManagerService;
import com.fujitsu.util.ExportExcelUtil;
import com.fujitsu.util.XmlUtil;
@Service
@Transactional(rollbackFor = Exception.class)
public class NetworkManagerImpl extends NetworkManagerService {
	@Resource
	private NetworkManagerMapper networkManagerMapper;
	@Resource
	private CommonManagerMapper commonManagerMapper;
	@Resource
	private ICommonManagerService commonManagerService;
	@Resource
	private AreaManagerMapper areaManagerMapper;
	@Resource
	private TransSystemServiceImpl transSystemServiceImpl;
	@Override
    public Map<String,Object> getEarlyAlarmSetting(String selectText)
    		throws CommonException{
		Map<String,Object> data=new HashMap<String,Object>(); 
		data=networkManagerMapper.getEarlyAlarmSetting(selectText); 
		return data;
	} 
	
	@Override
	public CommonResult updateEarlyAlarmSetting(Map<String,String> paramMap)
			throws CommonException{
      	CommonResult result = new CommonResult();
        Map<String,Object> data=new HashMap<String,Object>();
        String selectText="*";
        try{
            data=networkManagerMapper.getEarlyAlarmSetting(selectText); 
            if(data==null){
                networkManagerMapper.insertEarlyAlarmSetting(paramMap); 
            }else{
                networkManagerMapper.updateEarlyAlarmSetting(paramMap);
            }
            result.setReturnResult(CommonDefine.SUCCESS);
            result.setReturnMessage("设置成功！");
        }catch (Exception e) {
			e.printStackTrace();
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("设置失败！");
		} 
		return result;
	} 
	
	@SuppressWarnings("rawtypes")
	public Map<String,Object> searchNeEarlyWarn(Map<String,String> paramMap,int start,int limit) 
			throws CommonException{ 
		List<Map> dataRtn =new ArrayList<Map>();
		int total = 0;
		dataRtn = networkManagerMapper.searchNeEarlyWarn(paramMap,start,limit); 
		total =  networkManagerMapper.countNeEarlyWarn(paramMap); 
		Map<String,Object> result =new HashMap<String,Object>();
		result.put("rows", dataRtn);
 		result.put("total", total);
		return result; 
	} 
	
   //复用段详情,以环、链为单位  
	@SuppressWarnings("rawtypes")
    public List<Map> getPortMultiSecOccupy(String flag,List<Map> ptpData,List<Map> ctpData)
    		throws CommonException{   
    	 int cal=-1,min=-1,max=-1;   
         for(Map ptp:ptpData){
        	 if(ptp.get("rate")==null || "".equals(ptp.get("rate").toString())||
        			 CommonDefine.VCRATE.get(ptp.get("rate").toString())["VC4".equals(flag)?0:1]==0 ){  
        		 ptp.put(flag, 0);
	        	 ptp.put(flag+"MIN",0);
	        	 ptp.put(flag+"MAX", 0);
        	 }else{
	        	 int allVc=CommonDefine.VCRATE.get(ptp.get("rate").toString())["VC4".equals(flag)?0:1];  
	        	 int tmpV=-1;  
	        	 if(ptp.get("A_END_PTP")!=null && ptp.get("Z_END_PTP")!=null &&
	        	 	!"".equals(ptp.get("A_END_PTP").toString()) &&
	        	 	!"".equals(ptp.get("Z_END_PTP").toString())){   
		        	 for(Map ctp:ctpData){ 
		        		 if((ptp.get("A_END_PTP").toString().equals(ctp.get("ptpId").toString())
		        			|| ptp.get("Z_END_PTP").toString().equals(ctp.get("ptpId").toString()))){  
		        			 cal = (new BigDecimal(Integer.valueOf(ctp.get("cnt").toString())*1.0*100/allVc).setScale(0,BigDecimal.ROUND_HALF_UP)).intValue();
		        			 if(min==-1){min=cal;}
		        			 if(max<cal) max=cal;
		        			 if(min>cal) min=cal;
		        			 if(tmpV<cal) tmpV=cal; 
			        	 } 
			    	 }   
		         }  
	        	 ptp.put(flag, tmpV==-1?100:(100-tmpV));
	        	 ptp.put(flag+"MIN", max==-1?100:(100-max));
	        	 ptp.put(flag+"MAX", min==-1?100:(100-min)); 
        	 }
    	 }
		 return ptpData; 
	}  
		
	@Override 
	public Map<String,Object> initMultiEarlyWarn(Map<String,String> paramMap,Integer userId)
			throws CommonException {
		Map<String, Object> result = new HashMap<String, Object> ();
		Map<String,Object> searchCond=new HashMap<String,Object>();
		List<Map> dataRtn=new ArrayList<Map>();  
		searchCond.put("rlId", Integer.valueOf(paramMap.get("rlId"))); 
		dataRtn = networkManagerMapper.searchMsEarlyWarn(searchCond,userId,CommonDefine.TREE.TREE_DEFINE);
		if(dataRtn!=null && dataRtn.size()>0){ 
			if(dataRtn.get(0).get("RESOURCE_AREA_ID")!=null){
				dataRtn.get(0).put("areaName", commonManagerService.getMulitLevelFullName(
					Integer.valueOf(dataRtn.get(0).get("RESOURCE_AREA_ID").toString()),
					"T_RESOURCE_AREA","RESOURCE_AREA_ID", "AREA_PARENT_ID","AREA_NAME")); 
			}
		}
		result.put("rows",dataRtn);
		result.put("total", dataRtn.size()); 
		return result; 
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public Map<String,Object> searchMultiEarlyWarn(Map<String,String> paramMap,Integer userId,
			int start,int limit) throws CommonException{  
		List<Map> dataRtn =new ArrayList<Map>();
		Map<String,Object> result =new HashMap<String,Object>();
		Map<String,Object> searchCond=new HashMap<String,Object>(); 
		searchCond.put("VC4MJ", paramMap.get("VC4MJ")==null?null:Integer.valueOf(paramMap.get("VC4MJ"))); 
		searchCond.put("VC4MN", paramMap.get("VC4MN")==null?null:Integer.valueOf(paramMap.get("VC4MN"))); 
		searchCond.put("VC4WR", paramMap.get("VC4WR")==null?null:Integer.valueOf(paramMap.get("VC4WR")));  
		searchCond.put("VC12MJ", paramMap.get("VC12MJ")==null?null:Integer.valueOf(paramMap.get("VC12MJ"))); 
		searchCond.put("VC12MN", paramMap.get("VC12MN")==null?null:Integer.valueOf(paramMap.get("VC12MN"))); 
		searchCond.put("VC12WR", paramMap.get("VC12WR")==null?null:Integer.valueOf(paramMap.get("VC12WR")));   
		searchCond.put("alarm1",paramMap.get("alarm1")==null?null: paramMap.get("alarm1")); 
		searchCond.put("alarm2", paramMap.get("alarm2")==null?null:paramMap.get("alarm2")); 
		searchCond.put("alarm3",paramMap.get("alarm3")==null?null: paramMap.get("alarm3"));  
		searchCond.put("alarm4",paramMap.get("alarm4")==null?null: paramMap.get("alarm4"));  
		searchCond.put("areaIds",getSubAreaIds(paramMap.get("areaIds")));
		searchCond.put("levelCombo",Integer.valueOf(paramMap.get("levelCombo")));
		searchCond.put("structCombo",Integer.valueOf(paramMap.get("structCombo")));
		searchCond.put("start", start); 
		searchCond.put("limit",limit);  
		int total = 0;
		dataRtn = networkManagerMapper.searchMsEarlyWarn(searchCond,userId,CommonDefine.TREE.TREE_DEFINE);
		total =  networkManagerMapper.countMsEarlyWarn(searchCond,userId,CommonDefine.TREE.TREE_DEFINE);
		if(dataRtn!=null && dataRtn.size()>0){ 
			for(Map rl:dataRtn){
				if(rl.get("RESOURCE_AREA_ID")!=null){
					rl.put("areaName", commonManagerService.getMulitLevelFullName(
						Integer.valueOf(rl.get("RESOURCE_AREA_ID").toString()),
						"T_RESOURCE_AREA","RESOURCE_AREA_ID", "AREA_PARENT_ID","AREA_NAME")); 
				}
			}   
		}
		result.put("rows", dataRtn);
 		result.put("total", total);
		return result; 
	} 
	
	@SuppressWarnings("rawtypes")
	public String getSubAreaIds(String parentIds)throws CommonException {
		if("".equals(parentIds)){
			return "";
		}
		List ids = new ArrayList();
		String areaIds = "(" + parentIds + ")";
		for(int i = 0; i < 4 && areaIds.length() > 2; i++){ 
			List<Map<String, Object>> tmp = areaManagerMapper.getSubAreaByParentIds(areaIds);
			areaIds = "(";
			int j;
			for (j = 0; j < tmp.size(); j++) {
				areaIds += tmp.get(j).get("RESOURCE_AREA_ID").toString() + ", ";
				ids.add(tmp.get(j).get("RESOURCE_AREA_ID").toString());
			}
			if(j==0)break;
			areaIds = areaIds.substring(0, areaIds.length()-2) + ")"; 
		}

		String pIds = "(" + parentIds;
		for(int i = 0; i< ids.size(); i++){
			pIds += "," + ids.get(i);
		}
		pIds += ")";
		
		return pIds;
	}
	
	@SuppressWarnings("rawtypes")
	public Map<String,Object> searchDetailMulti(Map<String,String> paramMap) 
			throws CommonException{
		Map<String, Object> result = new HashMap();
		List<Map> ptpData=new ArrayList<Map>();///环、链上端口 
	    List<Map> ctpData=new ArrayList<Map>(); 
		ptpData = networkManagerMapper.getRLptpIds(Integer.valueOf(paramMap.get("rlId")));
		try{
			if(ptpData!=null && ptpData.size()>0){
				for(Map ptpl:ptpData){
					if(ptpl.get("rate")!=null && !"".equals(ptpl.get("rate").toString()) &&
					   !("2M".equals(ptpl.get("rate").toString())||"155M".equals(ptpl.get("rate").toString())||
						"622M".equals(ptpl.get("rate").toString())||"2.5G".equals(ptpl.get("rate").toString())||
						"10G".equals(ptpl.get("rate").toString())||"40G".equals(ptpl.get("rate").toString())||
						"100G".equals(ptpl.get("rate").toString()))){
						continue;
					} 
				 	 String ptpIds="(";
					 for(Map ptp:ptpData){
						 if(ptp.get("A_END_PTP")!=null)ptpIds+=ptp.get("A_END_PTP").toString()+",";
						 if(ptp.get("Z_END_PTP")!=null)ptpIds+=ptp.get("Z_END_PTP").toString()+",";
					 }
					 ptpIds=ptpIds.substring(0,ptpIds.length()-1)+")";
					 if(ptpIds.length()>0){	 
					 	 int VC12=0,VC4=0;
						//VC12
				   		 ctpData=networkManagerMapper.getVC12Data(ptpIds);
				   		 ptpData=getPortMultiSecOccupy("VC12",ptpData,ctpData); 
						//VC4
				   		 ctpData=networkManagerMapper.getVC4Data(ptpIds);
				   		 ptpData=getPortMultiSecOccupy("VC4",ptpData,ctpData); 
					 }
				}
			}
		}catch (Exception e){ 
		}
		result.put("rows", ptpData);
 		result.put("total", ptpData.size());
		return result; 
	}   
	
	@Override 
	public Map<String,Object> searchCommonEarlyAlarm(Map<String,String> paramMap,Integer userId,
			int start,int limit) throws CommonException{ 
        List<Map> dataRtn = new ArrayList();    
        int total=0;
      //组装查询条件
    	Map<String,Object> searchCond=new HashMap<String,Object>(); 
		searchCond.put("MJ", paramMap.get("MJ")==null?null:Integer.valueOf(paramMap.get("MJ"))); 
		searchCond.put("MN", paramMap.get("MN")==null?null:Integer.valueOf(paramMap.get("MN"))); 
		searchCond.put("WR", paramMap.get("WR")==null?null:Integer.valueOf(paramMap.get("WR")));    
		searchCond.put("areaIds", paramMap.get("areaIds")==null?null:getSubAreaIds(paramMap.get("areaIds")));
		searchCond.put("protectTypeCombo",paramMap.get("protectTypeCombo")==null?null: Integer.valueOf(paramMap.get("protectTypeCombo")));
		searchCond.put("levelCombo",paramMap.get("levelCombo")==null?null: Integer.valueOf(paramMap.get("levelCombo"))); 
		searchCond.put("tag", paramMap.get("tag")==null?null:Integer.valueOf(paramMap.get("tag")));  
		searchCond.put("alarm1",paramMap.get("alarm1")==null?null: paramMap.get("alarm1")); 
		searchCond.put("alarm2", paramMap.get("alarm2")==null?null:paramMap.get("alarm2")); 
		searchCond.put("alarm3",paramMap.get("alarm3")==null?null: paramMap.get("alarm3"));  
		searchCond.put("start", start); 
		searchCond.put("limit",limit);  
		dataRtn = networkManagerMapper.getResourceTransSysByCond(searchCond,userId,CommonDefine.TREE.TREE_DEFINE);
		total = networkManagerMapper.countResourceTransSysByCond(searchCond,userId,CommonDefine.TREE.TREE_DEFINE);
		if(dataRtn!=null && !dataRtn.isEmpty()){
			for(Map rl:dataRtn){ 
				if(rl.get("RESOURCE_AREA_ID")!=null){
					rl.put("areaName", commonManagerService.getMulitLevelFullName(
						Integer.valueOf(rl.get("RESOURCE_AREA_ID").toString()),
						"T_RESOURCE_AREA","RESOURCE_AREA_ID", "AREA_PARENT_ID","AREA_NAME")); 
				}
			} 
		}	
        Map<String, Object> result = new HashMap();
		result.put("rows", dataRtn);  
		result.put("total", total);
		return result; 
	} 
	
	@Override 
	public Map<String,Object> searchAreaNodeList(Map<String,String> paramMap) 
			throws CommonException{ 
        List<Map> data = new ArrayList();   
        Map<String,Object> searchCond=new HashMap<String,Object>(); 
		searchCond.put("rlId", Integer.valueOf(paramMap.get("rlId"))); 
		searchCond.put("areaName", paramMap.get("areaName")); 
		data = networkManagerMapper.searchAreaNodeList(searchCond);    
        Map<String, Object> result = new HashMap();
		result.put("rows", data);
		result.put("total", data.size());
		return result; 
	} 

	@Override 
	public Map<String,Object> getTopoNodeAndLink(Map<String,String> paramMap) 
			throws CommonException{ 
        List<Map> data = new ArrayList();   
        List<Map> dataLink = new ArrayList();    
		data = networkManagerMapper.getTopoNodes(Integer.valueOf(paramMap.get("rlId")));    
		dataLink = networkManagerMapper.getTopoLinks(Integer.valueOf(paramMap.get("rlId"))); 
		data.addAll(dataLink);
        Map<String, Object> result = new HashMap();
		result.put("rows", data);
		result.put("total", data.size());
		return result; 
	} 
	
	// 导出文件
	@SuppressWarnings("rawtypes")
	public String exportExcel(Map<String,String>paramMap,Integer userId)
			throws CommonException {
		int flag = Integer.valueOf(paramMap.get("flag"));
		String name = "";
		String sheetName = "ExoportExcel";
		String resultMessage = "";  
		int tag=0;
		switch (flag) { 
			case 1: {
				flag = CommonDefine.EXCEL.NE_EARLY_WARN;
				name = "网元资源预警分析-";
			}
				break;
			case 2:{ 
				flag = CommonDefine.EXCEL.MULTI_SEC_EARLY_WARN; 
				name = "复用段资源预警分析-";
			}
				break;
			case 21:{
//				21 为链接页面的一条数据
				tag=1;
				flag = CommonDefine.EXCEL.MULTI_SEC_EARLY_WARN; 
				name = "复用段资源预警分析-";
			}
				break;
			case 3: {
				flag = CommonDefine.EXCEL.SUPER_BIG; 
				name = "超大环-";
			}
				break;
			case 5: {
				flag = CommonDefine.EXCEL.LONG_SINGLE; 
				name = "长单链-";
			}
				break;
			case 7: {
				flag = CommonDefine.EXCEL.WITHOUT_PROTECTION; 
				name = "无保护环-";
			}
				break;
			case 9: {
				flag = CommonDefine.EXCEL.TRANS_SYS; 
				name = "传输系统-";
			}
				break;
			case 10: {
				flag = CommonDefine.EXCEL.LINK_AVAILABILITY_VC12; 
				name = "VC12时隙可用率";
				sheetName = paramMap.get("subnetName")+" "+name;
			}
				break;
			case 11: {
				flag = CommonDefine.EXCEL.LINK_AVAILABILITY_VC4; 
				name = "VC4时隙可用率";
				sheetName = paramMap.get("subnetName")+" "+name;
			}
				break;
			default:
				return resultMessage;
		}
 
		Map result = null;
		HttpServletRequest request = ServletActionContext.getRequest();
		String path = CommonDefine.PATH_ROOT + CommonDefine.EXCEL.UPLOAD_PATH;
		SimpleDateFormat formatter = new SimpleDateFormat(CommonDefine.RETRIEVAL_TIME_FORMAT); 
		String fileName =  name + formatter.format(new Date(System.currentTimeMillis()));
		IExportExcel ex2 = new ExportExcelUtil(path, fileName, sheetName,2000);
		try {
			// 根据flag选择sql查询方法
			result = getExportResult(flag,tag,paramMap,userId);
			if (result == null) {
				return resultMessage;
			}
			List<Map> rows = (List<Map>) result.get("rows");
			int total = Integer.parseInt(result.get("total").toString()); 
			formData(flag, rows);
			resultMessage = ex2.writeExcel(rows, flag, false);
		} catch (Exception e) {
			e.printStackTrace();
			ex2.close();
			return resultMessage;
		}
		return resultMessage;
	}

	// 导出文件
	@SuppressWarnings("rawtypes")
	public String exportExcel(Map<String,Object> map) throws CommonException {
		int flag = Integer.valueOf(map.get("flag").toString());
		String name = "";
		String resultMessage = "";  
		switch (flag) { 
			case 4: {
				flag = CommonDefine.EXCEL.MULTI_CIRCLE; 
				name = "多环节点-";
			}
				break;
			case 6: {
				flag = CommonDefine.EXCEL.BIG_GATHER; 
				name = "大汇聚点-";
			}
				break;
			case 8: {
				flag = CommonDefine.EXCEL.NONE_CIRCLE; 
				name = "未成环网元-";
			}
				break;
			default:
				return resultMessage;
		}
 
		Map result = null;
		String path = CommonDefine.PATH_ROOT + CommonDefine.EXCEL.UPLOAD_PATH;
		SimpleDateFormat formatter = new SimpleDateFormat(CommonDefine.RETRIEVAL_TIME_FORMAT); 
		String fileName =  name + formatter.format(new Date(System.currentTimeMillis()));
		IExportExcel ex2 = new ExportExcelUtil(path, fileName, "ExoportExcel",2000);
		try {
			// 根据flag选择sql查询方法
			result = getExportResult(flag,map);
			if (result == null) {
				return resultMessage;
			}
			List<Map> rows = (List<Map>) result.get("rows");
			int total = Integer.parseInt(result.get("total").toString()); 
			formData(flag, rows);
			resultMessage = ex2.writeExcel(rows, flag, false);
		} catch (Exception e) {
			e.printStackTrace();
			ex2.close();
			return resultMessage;
		}
		return resultMessage;
	}
	
	/**
	 * 
	 * @param data
	 *            从数据库中查询出来的结果
	 * @param cell
	 *            记录中字段名
	 * @param disName
	 *            需要替换成的值
	 * @return
	 */
	
	@SuppressWarnings("rawtypes")
	private Map getExportResult(int flag,int tag,Map<String,String>paramMap,Integer userId) 
			throws CommonException {
		Map result = null; 
		switch (flag) {
			case CommonDefine.EXCEL.NE_EARLY_WARN: 
				result = searchNeEarlyWarn(paramMap,0,0); 
				break;
			case CommonDefine.EXCEL.MULTI_SEC_EARLY_WARN: 
				if(tag==1){
					result = initMultiEarlyWarn(paramMap,userId);
				} else{
					result = searchMultiEarlyWarn(paramMap,userId,0,0); 
				} 
				break;
			case CommonDefine.EXCEL.SUPER_BIG: 
			case CommonDefine.EXCEL.LONG_SINGLE: 
			case CommonDefine.EXCEL.WITHOUT_PROTECTION:
				result = searchCommonEarlyAlarm(paramMap,userId,0,0); 
				break;
			case CommonDefine.EXCEL.TRANS_SYS:
				result = transSystemServiceImpl.queryTransmissionSystem(
						TransSystemServiceImpl.arrangeCondsForQueryTransmissionSystem(paramMap),userId,0,0); 
				break;
			case CommonDefine.EXCEL.LINK_AVAILABILITY_VC12:
			case CommonDefine.EXCEL.LINK_AVAILABILITY_VC4:
				result =getLinkAnalysisInfo(paramMap);
				break;
			default:
				break;
		}
		return result;
	} 
	
	
	public Map getLinkAnalysisInfo(Map paramMap){
		
		String linkIdsString = paramMap.get("linkIds").toString();
		
		List<Integer> linkIds = new ArrayList<Integer>();
		
		if(linkIdsString.split(",").length>0){
			for(String linkId:linkIdsString.split(",")){
				linkIds.add(Integer.valueOf(linkId));
			}
		}
		List<Map> dataList = networkManagerMapper.getLinkAnalysisInfo(linkIds);
		
		Map<String, Object> result = new HashMap();
		result.put("rows", dataList);  
		result.put("total", dataList.size());
		return result; 
	}
	
	@SuppressWarnings("rawtypes")
	private Map getExportResult(int flag,Map<String,Object>map) 
			throws CommonException {
		Map result = null; 
		switch (flag) {
			case CommonDefine.EXCEL.MULTI_CIRCLE: 
			case CommonDefine.EXCEL.BIG_GATHER:
				result = searchNodeNeList(map); 
				break; 
			case CommonDefine.EXCEL.NONE_CIRCLE: 
				result = searchNoCyclicNodeList(map);
				break; 
			default:
				break;
		}
		return result;
	} 
	
	private void formData(int flag, List<Map> rows) {
		if(flag == CommonDefine.EXCEL.NE_EARLY_WARN){ 
			transformNeMsData(rows,"SLOT_OCCUPANCY");
			transformNeMsData(rows,"PTP_OCCUPANCY_2M");
			transformNeMsData(rows,"PTP_OCCUPANCY_STM1");
			transformNeMsData(rows,"PTP_OCCUPANCY_STM4"); 
			transformNeMsData(rows,"PTP_OCCUPANCY_STM16");
			transformNeMsData(rows,"PTP_OCCUPANCY_STM64");
			transformNeMsData(rows,"PTP_OCCUPANCY_STM256");
			transformNeMsData(rows,"MS_OCCUPANCY");
		}
		if(flag == CommonDefine.EXCEL.MULTI_SEC_EARLY_WARN){
			transformNeMsData(rows,"VC4_OCCUPANCY_MAX");
			transformNeMsData(rows,"VC4_OCCUPANCY_AVG");
			transformNeMsData(rows,"VC4_OCCUPANCY_MIN");
			transformNeMsData(rows,"V12_OCCUPANCY_MAX"); 
			transformNeMsData(rows,"VC12_OCCUPANCY_AVG");
			transformNeMsData(rows,"VC12_OCCUPANCY_MIN");
		}
		if (flag == CommonDefine.EXCEL.MULTI_SEC_EARLY_WARN
			|| flag == CommonDefine.EXCEL.SUPER_BIG
			|| flag == CommonDefine.EXCEL.LONG_SINGLE
			|| flag == CommonDefine.EXCEL.WITHOUT_PROTECTION 
			|| flag == CommonDefine.EXCEL.TRANS_SYS) {
			transformData(rows, "DOMAIN", new String[] {"0","SDH", "WDM","MSTP","MSAP","ASON","PDH"},6);
			transformData(rows, "TYPE",new String[] { "0", "环", "链" },2);
			transformData(rows, "TRANS_MEDIUM", new String[] { "","光", "电","微波"},3);
			transformData(rows, "PRO_GROUP_TYPE", 
					new String[] { "1+1 MSP","1:N MSP", "2F BLSR","4F BLSR","1+1 ATM","1:N ATM"},5);
			transformData(rows, "NET_LEVEL", 
					new String[] { "","骨干级", "汇聚级","接入级","一干","二干"},5);
			transformData(rows, "GENERATE_METHOD", 
					new String[] { "","自动", "手动"},2);
			transformData(rows, "STATUS", 
					new String[] { "","存在", "不存在"},2);
		}  
	}  

	private boolean transformNeMsData(List<Map> data, String cell) {
		if (data.size() == 0 || cell.equals("")) {
			return false;
		}
		for (Map tmp : data) {
			if (tmp.get(cell) != null) {
				if (!("".equals(tmp.get(cell).toString())) && !("-".equals(tmp.get(cell).toString()))){ 
					if("MS_OCCUPANCY".equals(cell)){
						String tmpStr="";
//						id:Name:VC4:VC12;id:Name:VC4:VC12
						String[] mss = tmp.get(cell).toString().split(";");
						for (String str : mss) {
							String[] ms = str.split(":");
							tmpStr=ms[1]+": VC4 "+ ((ms[2]==null || ms[2]=="") ?"":(ms[2]+"%"))
									+": VC12 "+ ((ms[3]==null || ms[3]=="") ?"":(ms[3]+"%"));
							if(!str.equals(mss[mss.length-1]))
								tmpStr+=";";
						} 
						tmp.put(cell, tmpStr); 
					}else{
						tmp.put(cell, tmp.get(cell).toString()+"%"); 
					}
				}
			} 
		}
		return true;
	} 
	
	
	@SuppressWarnings("rawtypes")
	private boolean transformData(List<Map> data, String cell,String[] displayName,int cnt) {
		if (displayName.length == 0 || data.size() == 0 || cell.equals("")) {
			return false;
		}
		for (Map tmp : data) {
			if (tmp.get(cell) != null) {
				if (!("".equals(tmp.get(cell).toString()))) {
					if (Integer.parseInt(tmp.get(cell).toString()) > cnt) {
						tmp.put(cell, tmp.get(cell).toString());
					} else
						tmp.put(cell, displayName[Integer.parseInt(tmp.get(
								cell).toString())]);
				}
			} else
				return false;
		}
		return true;
	}

	@Override
	/**
	 * 多环节点、大汇聚点网元信息查询
	 * 返回多环节点网元信息列表
	 * 
	 * @param Map<String,Object> - 子网(或网管)节点Id、预警级别
	 * @return Map<String,Object> - 网元信息列表
	 * @throws CommonException
	 */
	public Map<String, Object> searchNodeNeList(
			Map<String, Object> map) throws CommonException {
		// TODO Auto-generated method stub
		Map<String,Object> returnMap = new HashMap<String,Object>();
		try{
		List<Map<String,Object>> returnList = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> transformList = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> emsIdList = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> subnetIdList = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> emsGroupIdList = new ArrayList<Map<String,Object>>();
		
		Map<String,Object> paramMap = new HashMap<String,Object>();
		
		List<Map> treeNodes = (List<Map>)map.get("nodeList");
		
		for(Map node : treeNodes){
			int nodeLevel = Integer.valueOf(String.valueOf(node.get("nodeLevel")).toString()).intValue();
			int nodeId = Integer.valueOf(String.valueOf(node.get("nodeId")).toString()).intValue();
			if(nodeLevel == CommonDefine.TREE.NODE.EMS ){
				paramMap = new HashMap<String,Object>();
				paramMap.put("emsId", nodeId);
				emsIdList.add(paramMap);
			}else if(nodeLevel == CommonDefine.TREE.NODE.SUBNET){
				paramMap = new HashMap<String,Object>();
				paramMap.put("subnetId", nodeId);
				subnetIdList.add(paramMap);
			}else if(nodeLevel == CommonDefine.TREE.NODE.EMSGROUP ){
				paramMap = new HashMap<String,Object>();
				paramMap.put("emsGroupId", nodeId);
				emsGroupIdList.add(paramMap);
			}
		}
		map.put("subnetIdList", subnetIdList.size() > 0 ? subnetIdList : null);
		map.put("emsIdList", emsIdList.size() > 0 ? emsIdList : null);
		map.put("emsGroupIdList", emsGroupIdList.size() > 0 ? emsGroupIdList : null);
		int count = networkManagerMapper.countPolycyclicNodeNeList(map); 
		returnList = networkManagerMapper.searchPolycyclicNodeNeList(map);
		for(Map<String,Object> node : returnList){
			if(node.get("areaId") != null){
				int areaId = Integer.valueOf(String.valueOf(node.get("areaId")).toString()).intValue();
				node.put("areaName", commonManagerService
							.getMulitLevelFullName(areaId, "T_RESOURCE_AREA",
									"RESOURCE_AREA_ID", "AREA_PARENT_ID",
									"AREA_NAME"));
			}
			transformList.add(node);
		}
		returnMap.put("rows", transformList);
		returnMap.put("total", count);
		
		}catch(Exception e){
			e.printStackTrace();
		}
		return returnMap;
	}

	@Override
	/**
	 * 预警参数值获取
	 * 指定环、链类型的预警参数值
	 * 
	 * @param Map<String,Object> - 环、链类型
	 * @return Map<String,Object> - 指定环类型的预警参数值
	 * @throws CommonException
	 */
	public Map<String, Object> getWRConfig(int getWRConfig)
			throws CommonException {
		// TODO Auto-generated method stub
		Map<String,Object> returnMap = new HashMap<String,Object>();
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap = networkManagerMapper.getWRConfig();
		if(getWRConfig == 1){
			returnMap.put("MJ", resultMap.get("MULTI_NODE_MJ"));
			returnMap.put("MN", resultMap.get("MULTI_NODE_MN"));
			returnMap.put("WR", resultMap.get("MULTI_NODE_WR"));
		}
		
		return returnMap;
	}

	@Override
	/**
	 * 多环节点、大汇聚点环链信息查询
	 * 返回多环节点、大汇聚点环链信息列表
	 * 
	 * @param Map<String,Object> - 网元Id、分页参数
	 * @return Map<String,Object> - 环信息列表
	 * @throws CommonException
	 */
	public Map<String, Object> searchCycleList(Map<String, Object> map)
			throws CommonException {
		// TODO Auto-generated method stub
		Map<String,Object> returnMap = new HashMap<String,Object>();
		try{
			List<Map<String,Object>> returnList = new ArrayList<Map<String,Object>>();
			List<Map<String,Object>> transformList = new ArrayList<Map<String,Object>>();
			returnList = networkManagerMapper.searchCycleList(map);
			for(Map<String,Object> node : returnList){
				if(node.get("protectType") != null){
					int protectType = Integer.valueOf(String.valueOf(node.get("protectType")).toString()).intValue();
					node.put("protectType", CommonDefine.RESOURCE.TRANS_SYS.PRO_GROUP_TYPE.get(protectType));
				}
				if(node.get("level") != null){
					int level = Integer.valueOf(String.valueOf(node.get("level")).toString()).intValue();
					node.put("level", CommonDefine.RESOURCE.TRANS_SYS.NET_LEVEL.get(level));
				}
				transformList.add(node);
			}
			returnMap.put("rows", transformList);
			returnMap.put("total", transformList.size());
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return returnMap;
	}

	@Override
	/**
	 * 未成环网元信息查询
	 * 返回未成环网元信息列表
	 * 
	 * @param Map<String,Object> - 子网(或网管)节点Id
	 * @return Map<String,Object> - 网元信息列表
	 * @throws CommonException
	 */
	public Map<String, Object> searchNoCyclicNodeList(Map<String, Object> map)
			throws CommonException {
		// TODO Auto-generated method stub
		Map<String,Object> returnMap = new HashMap<String,Object>();
		try{
		List<Map<String,Object>> returnList = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> transformList = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> emsIdList = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> subnetIdList = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> emsGroupIdList = new ArrayList<Map<String,Object>>();
		
		Map<String,Object> paramMap = new HashMap<String,Object>();
		
		List<Map> treeNodes = (List<Map>)map.get("nodeList");
		
		for(Map node : treeNodes){
			int nodeLevel = Integer.valueOf(String.valueOf(node.get("nodeLevel")).toString()).intValue();
			int nodeId = Integer.valueOf(String.valueOf(node.get("nodeId")).toString()).intValue();
			if(nodeLevel == CommonDefine.TREE.NODE.EMS ){
				paramMap = new HashMap<String,Object>();
				paramMap.put("emsId", nodeId);
				emsIdList.add(paramMap);
			}else if(nodeLevel == CommonDefine.TREE.NODE.SUBNET){
				paramMap = new HashMap<String,Object>();
				paramMap.put("subnetId", nodeId);
				subnetIdList.add(paramMap);
			}else if(nodeLevel == CommonDefine.TREE.NODE.EMSGROUP ){
				paramMap = new HashMap<String,Object>();
				paramMap.put("emsGroupId", nodeId);
				emsGroupIdList.add(paramMap);
			}
		}
		map.put("subnetIdList", subnetIdList.size() > 0 ? subnetIdList : null);
		map.put("emsIdList", emsIdList.size() > 0 ? emsIdList : null);
		map.put("emsGroupIdList", emsGroupIdList.size() > 0 ? emsGroupIdList : null);
		int count = networkManagerMapper.countNoCyclicNeList(map); 
		returnList = networkManagerMapper.searchNoCyclicNeList(map);
		for(Map<String,Object> node : returnList){
			if(node.get("areaId") != null){
				int areaId = Integer.valueOf(String.valueOf(node.get("areaId")).toString()).intValue();
				node.put("areaName", commonManagerService
							.getMulitLevelFullName(areaId, "T_RESOURCE_AREA",
									"RESOURCE_AREA_ID", "AREA_PARENT_ID",
									"AREA_NAME"));
			}
			//判断是否成链
			int isLinkNe = networkManagerMapper.isExistInResourceSysNe(node);
			
			node.put("networkLocation", isLinkNe > 0 ? CommonDefine.NETWORK.LOCATION_LINK : CommonDefine.NETWORK.LOCATION_SINGLE);
			
			transformList.add(node);
		}
		returnMap.put("rows", transformList);
		returnMap.put("total", count);
		
		}catch(Exception e){
			e.printStackTrace();
		}
		return returnMap;
	} 
	
	@Override
	/**
	 * 查询可用率表头
	 * @throws CommonException
	 */
	public Map<String, Object> searchAvailabilityHeader(int type,List<Map> equipList)
			throws CommonException {
		Map result = new LinkedHashMap();
		
		String tableName = "";
		switch(type){
		case CommonDefine.NETWORK.NWA_SLOT:
			tableName = "t_nwa_ne_analysis_detail";
			break;
		case CommonDefine.NETWORK.NWA_PORT:
			tableName = "t_nwa_port_analysis_detail";
			break;
			
		case CommonDefine.NETWORK.NWA_PORT_ROUTE:
			tableName = "t_nwa_port_analysis_detail_route";
			break;
		}
		List<Map> dataList = networkManagerMapper.searchAvailabilityHeader(tableName);

		for(Map map:dataList){
			int hashCode = map.get("TYPE_NAME").hashCode();
			result.put(String.valueOf(hashCode), map.get("TYPE_NAME"));
		}
		
		return result;
	} 
	
	/**
	 * 查询可用率数据
	 * @throws CommonException
	 */
	public Map<String, Object> searchAvailabilityData(int type,List<Map> equipList,
														int warningType,int start,int limit)
			throws CommonException {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		
		List<Map> dataList = new ArrayList<Map>();
		
		List<Map> dataDetailList = new ArrayList<Map>();
		List<Map> returnList = new ArrayList<Map>();
		
		List<Integer> neIdList = new ArrayList<Integer>();
		//获取网元 ID列表
		neIdList = getNeIdList(equipList);
		if(neIdList.size() == 0){
			neIdList.add(-1);
		}
		
		String tableName = "";
		String subTableName = "";
		
		//获取预警值
		Map warnning = searchWarningValue(type);
		int MJ = Integer.valueOf(String.valueOf(warnning.get("AVAILABILITY_MJ")));//重要
		int MN = Integer.valueOf(String.valueOf(warnning.get("AVAILABILITY_MN")));//次要
		int WR = Integer.valueOf(String.valueOf(warnning.get("AVAILABILITY_WR")));//一般
		
		switch(type){
		case CommonDefine.NETWORK.NWA_SLOT:
			tableName = "t_nwa_ne_analysis";
			subTableName = "t_nwa_ne_analysis_detail";
			break;
		case CommonDefine.NETWORK.NWA_PORT:
			tableName = "t_nwa_port_analysis";
			subTableName = "t_nwa_port_analysis_detail";
			break;
		case CommonDefine.NETWORK.NWA_PORT_ROUTE:
			tableName = "t_nwa_port_analysis_route";
			subTableName = "t_nwa_port_analysis_detail_route";
			break;
		}
		
		switch(type){
		case CommonDefine.NETWORK.NWA_SLOT:
		case CommonDefine.NETWORK.NWA_PORT:
		case CommonDefine.NETWORK.NWA_PORT_ROUTE:
			//获取数据
			dataList = networkManagerMapper.searchAvailabilityData(tableName,neIdList,start,limit);
			
			dataDetailList = networkManagerMapper.searchAvailabilityDataDetail(subTableName,neIdList);
			
			for(Map data:dataList){
				
				int neId = Integer.valueOf(data.get("BASE_NE_ID").toString());
				
				int availability = Integer.valueOf(data.get("WARNNING").toString()); 
				
				/*if(availability >= 0 && availability <= MJ){
					data.put("warningLevel", CommonDefine.ALARM_PS_CRITICAL);//重要预警
				}else if(availability > MJ && availability <= MN){
					data.put("warningLevel", CommonDefine.ALARM_PS_MAJOR);//次要预警
				}else if(availability > MN && availability < WR){
					data.put("warningLevel", CommonDefine.ALARM_PS_MINOR);//一般预警
				}else{
					data.put("warningLevel", CommonDefine.ALARM_PS_INDETERMINATE);
				}*/
				switch(warningType){
				case CommonDefine.ALARM_PS_CRITICAL://重要预警
					if(availability >= 0 && availability < MJ){
						returnList.add(data);
					}
					break;
				case CommonDefine.ALARM_PS_MAJOR://次要预警
					if(availability >= MJ && availability < MN){
						returnList.add(data);
					}
					break;
				case CommonDefine.ALARM_PS_MINOR://一般预警
					if(availability >= MN && availability < WR){
						returnList.add(data);
					}
					break;
				case CommonDefine.ALARM_PS_INDETERMINATE://所有数据，不筛选
					returnList.add(data);
					break;
				}
				
				for(Map dataDetail:dataDetailList){
					int neIdRel = Integer.valueOf(dataDetail.get("BASE_NE_ID").toString());
					if(neId == neIdRel){
						int hashCode = dataDetail.get("TYPE_NAME").hashCode();
						data.put(String.valueOf(hashCode), dataDetail.get("TYPE_NAME_VALUE"));
					}
				}
			}
			
			break;
			
		case CommonDefine.NETWORK.NWA_CTP:
			dataList = networkManagerMapper.searchAvailabilityData4Ctp(neIdList,start,limit);
			for(Map data:dataList){
				int availability = Integer.valueOf(data.get("WARNNING").toString()); 
				
				/*if(availability >= MJ && availability < MN){
					data.put("warningLevel", CommonDefine.ALARM_PS_CRITICAL);
				}else if(availability >= MN && availability < WR){
					data.put("warningLevel", CommonDefine.ALARM_PS_MAJOR);
				}else if(availability >= WR){
					data.put("warningLevel", CommonDefine.ALARM_PS_MINOR);
				}else{
					data.put("warningLevel", CommonDefine.ALARM_PS_INDETERMINATE);
				}*/
				switch(warningType){
				case CommonDefine.ALARM_PS_CRITICAL://重要预警
					if(availability >= 0 && availability < MJ){
						returnList.add(data);
					}
					break;
				case CommonDefine.ALARM_PS_MAJOR://次要预警
					if(availability >= MJ && availability < MN){
						returnList.add(data);
					}
					break;
				case CommonDefine.ALARM_PS_MINOR://一般预警
					if(availability >= MN && availability < WR){
						returnList.add(data);
					}
					break;
				case CommonDefine.ALARM_PS_INDETERMINATE://所有数据，不筛选
					returnList.add(data);
					break;
				}
			}
			break;
		}
		returnMap.put("rows", returnList);
		returnMap.put("total", returnList.size());
		
		return returnMap;
	} 
	
	/**
	 * 查询可用率数据
	 * @throws CommonException
	 */
	private Map searchAvailabilityData4Chart(int type,String subType,List<Integer> neIdList)
			throws CommonException {
		Map result = new LinkedHashMap();
		Map data = new HashMap();
		List<Map> dataList = new ArrayList<Map>();
		
		String tableName = "";
		
		switch(type){
		case CommonDefine.NETWORK.NWA_SLOT_ZONGHE:
			tableName = "t_nwa_ne_analysis";
			break;
		case CommonDefine.NETWORK.NWA_SLOT_SUB:
			tableName = "t_nwa_ne_analysis_detail";
			break;
		case CommonDefine.NETWORK.NWA_PORT_ZONGHE:
			tableName = "t_nwa_port_analysis";
			break;
		case CommonDefine.NETWORK.NWA_PORT_SUB:
			tableName = "t_nwa_port_analysis_detail";
			break;
			
		case CommonDefine.NETWORK.NWA_PORT_ZONGHE_ROUTE:
			tableName = "t_nwa_port_analysis_route";
			break;
		case CommonDefine.NETWORK.NWA_PORT_SUB_ROUTE:
			tableName = "t_nwa_port_analysis_detail_route";
			break;
		}
		
		switch(type){
		case CommonDefine.NETWORK.NWA_SLOT_ZONGHE:
		case CommonDefine.NETWORK.NWA_PORT_ZONGHE:
		case CommonDefine.NETWORK.NWA_PORT_ZONGHE_ROUTE:
			data = networkManagerMapper.searchAvailabilityData4Chart_ZONGHE(tableName,neIdList);
			if(data != null){
				result.put("可用", data.get("TYPE_UNUSE_VALUE"));
				result.put("不可用", 100-Integer.valueOf(data.get("TYPE_UNUSE_VALUE").toString()));
			}
			break;
		case CommonDefine.NETWORK.NWA_CTP_ZONGHE:
			if(subType.equals("VC4")){
				data = networkManagerMapper.searchAvailabilityData4Chart_ZONGHE_VC4(neIdList);
				if(data != null){
					result.put("可用", data.get("TYPE_UNUSE_VALUE"));
					result.put("不可用", 100-Integer.valueOf(data.get("TYPE_UNUSE_VALUE").toString()));
				}
				
			}else{
				data = networkManagerMapper.searchAvailabilityData4Chart_ZONGHE_VC12(neIdList);
				if(data != null){
					result.put("可用", data.get("TYPE_UNUSE_VALUE"));
					result.put("不可用", 100-Integer.valueOf(data.get("TYPE_UNUSE_VALUE").toString()));
				}
			}
			break;
		case CommonDefine.NETWORK.NWA_SLOT_SUB:
		case CommonDefine.NETWORK.NWA_PORT_SUB:
		case CommonDefine.NETWORK.NWA_PORT_SUB_ROUTE:
			dataList = networkManagerMapper.searchAvailabilityData4Chart(tableName,neIdList);
			if(dataList.size() > 0){
				for(Map dataDetail :dataList){
					result.put(dataDetail.get("TYPE_NAME"), dataDetail.get("TYPE_VALUE"));
				}
			}
			break;
		}
		
		
		return result;
	} 
	
	
	/**
	 * 生成槽道可用率统计信息图表数据
	 * @return
	 */
	public Map generateDiagramXml(Map paramMap,List<Map> equipList){
		
		List<Integer> neIdList = new ArrayList<Integer>();
		
		//获取网元 ID列表
		neIdList = getNeIdList(equipList);
		
		if(neIdList.size() == 0){
			neIdList.add(-1);
		}
		
		int type = Integer.valueOf(paramMap.get("type").toString());
		Map result = new HashMap();
		if(type == CommonDefine.NETWORK.NWA_CTP_ZONGHE){
			try {
				Map dataVC4 = searchAvailabilityData4Chart(type,"VC4",neIdList);
				String xmlStringVC4 = XmlUtil.generalXmlImpl(dataVC4,type,"VC4");
				result.put("chart_one", xmlStringVC4);
				
				Map dataVC12 = searchAvailabilityData4Chart(type,"VC12",neIdList);
				String xmlStringVC12 = XmlUtil.generalXmlImpl(dataVC12,type,"VC12");
				result.put("chart_two", xmlStringVC12);
			} catch (CommonException e) {
				e.printStackTrace();
			}
		}
		else if(type == CommonDefine.NETWORK.NWA_CTP_SUB){
			try {
				List<Map> dataList = networkManagerMapper.searchAvailabilityData4Chart_CTP(neIdList);
				String xmlString = XmlUtil.generalXmlImpl(dataList,type);
				result.put("chart_one", xmlString);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else{
			try {
				Map data = searchAvailabilityData4Chart(type,null,neIdList);
				String xmlString = XmlUtil.generalXmlImpl(data,type,null);
				result.put("chart_one", xmlString);
			} catch (CommonException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public void modifyWarningValue(Map map)
			throws CommonException {
		try {
			//更新设置值
			networkManagerMapper.modifyWarningValue(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public Map searchWarningValue(int type) throws CommonException{
		Map result = networkManagerMapper.searchWarningValue(type);
		//插入默认值
		if(result == null){
			result = new HashMap();
			result.put("TYPE", type);
			result.put("AVAILABILITY_MJ", 10);
			result.put("AVAILABILITY_MN", 30);
			result.put("AVAILABILITY_WR", 50);
			result.put("CREATE_TIME", new Date());
			networkManagerMapper.insertWarningValue(result);
		}
		return result;
	}

	@Override
	public Map<String, Object> ctpNameCustomList(int start,int limit) 
			throws CommonException {
		// TODO Auto-generated method stub
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int total = networkManagerMapper.countCtpNameCustomList();
		list = networkManagerMapper.searchCtpNameCustomList(start,limit);
		result.put("total", total);
		result.put("rows", list);
		return result;
	}

	@Override
	public void addCtpCategory(int sortA, String sortB) throws CommonException {
		// TODO Auto-generated method stub
		Map<String,Object> m = new HashMap<String,Object>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式 
		Date now;
		try {
			now = df.parse(df.format(new Date()));
			m.put("sortA", sortA);
			m.put("sortB", sortB);
			m.put("createTime", now);
			m.put("updateTime", now);
			networkManagerMapper.addCtpCategory(m);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Map<String,Object> deleteCtpCategory(List<Integer> unitTypeList) throws CommonException {
		// TODO Auto-generated method stub
		Map<String, Object> result = new HashMap<String, Object>();
		int total = networkManagerMapper.countCtpCategoryRel(unitTypeList);
		if(total>0){
			result.put("success", false);
			result.put("msg", "板卡类型使用中，请取消关联使用！");
			return result;
		}
		networkManagerMapper.deleteCtpCategory(unitTypeList);
		result.put("success", true);
		result.put("msg", "删除板卡类别成功！");
		return result;
	}

	@Override
	public void updateCtpCategory(List<Map> unitTypeList)
			throws CommonException {
		// TODO Auto-generated method stub
		try {
			Map map = null;
			for (Map unitType : unitTypeList) {
				map = new HashMap<String, Object>();
				map.put("NWA_UNIT_TYPE_ID", unitType.get("NWA_UNIT_TYPE_ID"));
				map.put("SORT_B",unitType.get("SORT_B"));
				map.put("SORT_A",unitType.get("SORT_A"));
				map.put("UPDATE_TIME", new Date());
				networkManagerMapper.updateCtpCategory(map);
			}
		} catch (ClassCastException e) {
			throw new CommonException(e,
					MessageCodeDefine.PM_PARAMETER_TYPE_ERROR);
		} catch (NullPointerException e) {
			throw new CommonException(e,
					MessageCodeDefine.PM_PARAMETER_NULL_ERROR);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}

	@Override
	public Map<String, Object> getCtpCategoryListById(int factoryId,int start, int limit)
			throws CommonException {
		// TODO Auto-generated method stub
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int total = networkManagerMapper.countCtpCategoryListById(factoryId);
		list = networkManagerMapper.getCtpCategoryListById(factoryId,start,limit);
		/*for(int i=0;i<list.size();i++){
			
		}*/
		result.put("total", total);
		result.put("rows", list);
		return result;
	}

	@Override
	public Map<String, Object> validateCtpName(String sortB)
			throws CommonException {
		// TODO Auto-generated method stub
		Map<String, Object> result = new HashMap<String, Object>();
		boolean success = true; 
		int count = 0;
		count = networkManagerMapper.getListBySortB(sortB);
		if(count > 0){
			success = false;
		}
		result.put("success", success);
		return result;
	}

	@Override
	public void setCtpCategory(List<Map> unitTypeList) throws CommonException {
		// TODO Auto-generated method stub
		try {
			Map map = null;
			for (Map unitType : unitTypeList) {
				map = new HashMap<String, Object>();
				map.put("NWA_UNIT_TYPE_DEFINE_ID", unitType.get("NWA_UNIT_TYPE_DEFINE_ID"));
				/*map.put("NWA_UNIT_TYPE_ID",Integer.valueOf(String.valueOf(
						unitType.get("NWA_UNIT_TYPE_ID")).toString()).intValue());*/
				map.put("NWA_UNIT_TYPE_ID",unitType.get("NWA_UNIT_TYPE_ID"));
				map.put("UPDATE_TIME", new Date());
				networkManagerMapper.setCtpCategory(map);
			}
		} catch (ClassCastException e) {
			throw new CommonException(e,
					MessageCodeDefine.PM_PARAMETER_TYPE_ERROR);
		} catch (NullPointerException e) {
			throw new CommonException(e,
					MessageCodeDefine.PM_PARAMETER_NULL_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}
	
	private List<Integer> getNeIdList(List<Map> equipList){
		List<Integer> neIdList = new ArrayList<Integer>();
		String tableName = "";
		String subTableName = "";
		String idName = "";
		int id;
		List neList;
		for(Map map:equipList){
			int node = Integer.valueOf(String.valueOf(map.get("equipType")));
			switch(node){
			case CommonDefine.TREE.NODE.EMSGROUP:
				tableName = "T_BASE_NE";
				idName = "BASE_EMS_CONNECTION_ID";
				id = Integer.valueOf(String.valueOf(map.get("equipId")));
				neList = new ArrayList();
				neList = networkManagerMapper.selectNeListByEmsId(id);
				for(Object ne:neList){
					Map neMap = (Map)ne;
					neIdList.add(Integer.valueOf(String.valueOf(neMap.get("BASE_NE_ID"))));
				}
				break;
			case CommonDefine.TREE.NODE.EMS:
				tableName = "T_BASE_NE";
				idName = "BASE_EMS_CONNECTION_ID";
				id = Integer.valueOf(String.valueOf(map.get("equipId")));
				neList = new ArrayList();
				neList = commonManagerMapper.selectTableListById(tableName,idName,id,null,null);
				for(Object ne:neList){
					Map neMap = (Map)ne;
					neIdList.add(Integer.valueOf(String.valueOf(neMap.get("BASE_NE_ID"))));
				}
				break;
			case CommonDefine.TREE.NODE.SUBNET:
				tableName = "T_BASE_NE";
				idName = "BASE_SUBNET_ID";
				id = Integer.valueOf(String.valueOf(map.get("equipId")));
				neList = new ArrayList();
				neList = commonManagerMapper.selectTableListById(tableName,idName,id,null,null);
				for(Object ne:neList){
					Map neMap = (Map)ne;
					neIdList.add(Integer.valueOf(String.valueOf(neMap.get("BASE_NE_ID"))));
				}
				break;
			case CommonDefine.TREE.NODE.NE:
				neIdList.add(Integer.valueOf(String.valueOf(map.get("equipId"))));
				break;
			}
		}
		return neIdList;
	}

	@Override
	public String exportAvailabilityData(int type, List<Map> equipList)
			throws CommonException {
		// TODO Auto-generated method stub
		String resultMessage = "";
		String name = "";
		int flag = -1;
		List<Map> dataList = new ArrayList<Map>();
		List<Map> dataDetailList = new ArrayList<Map>();
		List<Integer> neIdList = new ArrayList<Integer>();
		
		String path = CommonDefine.PATH_ROOT + CommonDefine.EXCEL.UPLOAD_PATH;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		//获取网元 ID列表
		neIdList = getNeIdList(equipList);
		
		String tableName = "";
		String subTableName = "";
		String excelHeader = "";
		
		//动态获取Excel Header
		Map<String, Object> headerMap = searchAvailabilityHeader(type,equipList);
		for(String key: headerMap.keySet()){
			excelHeader = excelHeader + "," + key+"="+String.valueOf(headerMap.get(key));
		}
		String fixedHeader = "EMS_GROUP_DISPLAY_NAME=网管分组,EMS_DISPLAY_NAME=所属网管,NE_DISPLAY_NAME=网元名称," +
				"AREA_DISPLAY_NAME=区域,FACTORY_DISPLAY_NAME=厂家,PRODUCT_DISPLAY_NAME=网元型号,AVAILABILITY=综合";
		String writeStr = "{"+fixedHeader+excelHeader+"}";
		//数据获取
		dataList = new ArrayList<Map>();
		switch(type){
		case CommonDefine.NETWORK.NWA_SLOT:
			tableName = "t_nwa_ne_analysis";
			subTableName = "t_nwa_ne_analysis_detail";
			flag = CommonDefine.EXCEL.SLOT_AVAILABILITY;
			name = "槽道可用率";
			break;
		case CommonDefine.NETWORK.NWA_PORT:
			tableName = "t_nwa_port_analysis";
			subTableName = "t_nwa_port_analysis_detail";
			flag = CommonDefine.EXCEL.PORT_AVAILABILITY;
			name = "端口可用率";
			break;
		case CommonDefine.NETWORK.NWA_CTP:
			flag = CommonDefine.EXCEL.CTP_AVAILABILITY;
			name = "时隙可用率";
			break;
		case CommonDefine.NETWORK.NWA_PORT_ROUTE:
			tableName = "t_nwa_port_analysis_route";
			subTableName = "t_nwa_port_analysis_detail_route";
			flag = CommonDefine.EXCEL.PORT_AVAILABILITY_ROUTE;
			name = "端口可用率-资源";
			break;
		}
		String fileName = name + "_"+ formatter.format(new Date(System.currentTimeMillis()));
		IExportExcel ex2 = new ExportExcelUtil(path, fileName, "ExoportExcel",1000);
		try{
		switch(type){
		case CommonDefine.NETWORK.NWA_SLOT:
		case CommonDefine.NETWORK.NWA_PORT:
		case CommonDefine.NETWORK.NWA_PORT_ROUTE:
			//获取数据
			dataList = networkManagerMapper.searchAvailabilityData(tableName,neIdList,null,null);
			
			dataDetailList = networkManagerMapper.searchAvailabilityDataDetail(subTableName,neIdList);
			
			for(Map data:dataList){
				
				int neId = Integer.valueOf(data.get("BASE_NE_ID").toString());
				
				for(Map dataDetail:dataDetailList){
					int neIdRel = Integer.valueOf(dataDetail.get("BASE_NE_ID").toString());
					if(neId == neIdRel){
						int hashCode = dataDetail.get("TYPE_NAME").hashCode();
						data.put(String.valueOf(hashCode), dataDetail.get("TYPE_NAME_VALUE"));
					}
				}
			}
			// 导出数据
			resultMessage = ex2.writeExcel(dataList,writeStr, flag, false);
			//resultMessage = ex2.writeExcel(dataList, flag, false);
			break;
			
		case CommonDefine.NETWORK.NWA_CTP:
			dataList = networkManagerMapper.searchAvailabilityData4Ctp(neIdList,null,null);
			// 导出数据
			resultMessage = ex2.writeExcel(dataList, flag, false);
			break;
		}
		}catch(Exception e){
			e.printStackTrace();
			ex2.close();
		}
		return resultMessage;
	}
	
	/*private void setProperty(String excelHeader){
		Properties pros = new Properties();
		try{
			FileInputStream input = new FileInputStream("D:/JDKTomcat/tomcat7/webapps/FTSP/resourceConfig/excelHeader/excelHeaderResource.properties");
			pros.load(input);
			input.close();
			System.out.println(pros.get("8002"));
			//Map<String,Object> m = new HashMap<String,Object>();
			String m = String.valueOf(pros.get("8002"));
			String[] arr = m.split(",");
			String valueSlot = "EMS_GROUP_DISPLAY_NAME=网管分组,EMS_DISPLAY_NAME=所属网管,NE_DISPLAY_NAME=网元名称,AREA_DISPLAY_NAME=区域,FACTORY_DISPLAY_NAME=厂家,PRODUCT_DISPLAY_NAME=网元型号,AVAILABILITY=综合";
			String valuePort = "EMS_GROUP_DISPLAY_NAME=网管分组,EMS_DISPLAY_NAME=所属网管,NE_DISPLAY_NAME=网元名称,AREA_DISPLAY_NAME=区域,FACTORY_DISPLAY_NAME=厂家,PRODUCT_DISPLAY_NAME=网元型号,AVAILABILITY=综合";
			String writeStr = "{"+valueSlot+excelHeader+"}";
			String a = "{EMS_GROUP_DISPLAY_NAME=网管分组,EMS_DISPLAY_NAME=所属网管,NE_DISPLAY_NAME=网元名称,AREA_DISPLAY_NAME=区域,FACTORY_DISPLAY_NAME=厂家,PRODUCT_DISPLAY_NAME=网元型号,AVAILABILITY=综合," +
					"2188=E1,76079=MAC,78068=OCH,79230128=STM-1,79230131=STM-4,-1838833274=STM-16,-1838833121=STM-64}";
			//pros.put("8002",writeStr);
			pros.put("8002",a);
			System.out.println(pros.get("8002"));
			FileOutputStream fos = new FileOutputStream("D:/JDKTomcat/tomcat7/webapps/FTSP/resourceConfig/excelHeader/excelHeaderResource.properties");
			pros.store(fos,"");
		}catch(IOException e){
			e.printStackTrace();
		}
	}*/

	@Override
	public Map<String, Object> getPortDetial(int neId,int type) throws CommonException {
		Map<String, Object> result = new HashMap<String, Object>();
		try{
			String tableName = "";
			String leftJoinTableName = "";
			switch(type){
			case CommonDefine.NETWORK.NWA_PORT:
				tableName = "t_nwa_port_analysis_detail_ptp";
				leftJoinTableName = "t_nwa_port_analysis";
				break;
			case CommonDefine.NETWORK.NWA_PORT_ROUTE:
				tableName = "t_nwa_port_analysis_detail_ptp_route";
				leftJoinTableName = "t_nwa_port_analysis_route";
				break;
			}
			
			
			List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
			int total = networkManagerMapper.countPortDetial(neId,tableName);
			dataList = networkManagerMapper.getPortDetial(neId,tableName,leftJoinTableName);
			result.put("total", total);
			result.put("rows", dataList);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
}