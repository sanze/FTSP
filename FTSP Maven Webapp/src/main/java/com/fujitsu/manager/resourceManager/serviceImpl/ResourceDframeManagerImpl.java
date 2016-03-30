package com.fujitsu.manager.resourceManager.serviceImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.IService.ICommonManagerService;
import com.fujitsu.IService.IExportExcel;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;
import com.fujitsu.common.Result;
import com.fujitsu.dao.mysql.ResourceDframeManagerMapper;
import com.fujitsu.manager.resourceManager.service.ResourceDframeManagerService;
import com.fujitsu.util.ExportExcelUtil;
@Service
@Transactional(rollbackFor = Exception.class)
public class ResourceDframeManagerImpl extends ResourceDframeManagerService {
	@Resource
	private ResourceDframeManagerMapper resourceDframeManagerMapper; 
	@Resource
	private ICommonManagerService commonManagerService;
	
	/**  
	 * @@@分权分域到网元@@@  
	 * ODF 架初始化页面getOdfList
	 * @param String 
	 * @return Map<String,Object>
	 * @throws CommonException
	 */ 
	@Override
	public Map<String,Object> getOdfList(Map<String,String> map,int start ,int limit,int userId) 
			throws CommonException{ 
		Map returnData = new HashedMap();
		List<Map> returnList = new ArrayList<Map>();
		List<Integer> count = new ArrayList<Integer>();
		int total = 0;
		try{
			total = resourceDframeManagerMapper.countOdfList(map);    
			returnList = resourceDframeManagerMapper.getOdfList(map,start,limit);    
			// 获取该用户有权限的网元列表
			List<Map> allNeModel = commonManagerService.getAllNeByEmsId(userId, CommonDefine.VALUE_ALL, false, null); 
			for (Map rtn : returnList) {
				//配出类型转换
				if(rtn.get("outType")!=null){
					if(rtn.get("outType").equals(1)){
						rtn.put("outType", "端口"); 
					}else if(rtn.get("outType").equals(2)){
						rtn.put("outType", "ODF"); 
					}
				}
				
				//权限管理
				rtn.put("boolUserDeviceDomain", true);
				//遍历所有的记录 
				if(rtn.get("neId")!=null){ 
					rtn.put("boolUserDeviceDomain", false);
					for(Map ne:allNeModel){
						if(rtn.get("neId").equals(ne.get("BASE_NE_ID"))){
							rtn.put("boolUserDeviceDomain", true);
							break;
						}
					} 
				}  
			} 
			returnData.put("total", total); 
			returnData.put("rows", returnList);
		} catch (Exception e) { 
		} 
		return returnData;
	} 
	
	/** 
	 * 查询条件combo用途 
	 * @return Map<String,Object>
	 * @throws CommonException
	 */ 
	@Override
	public Map<String,Object> getUseableList() throws CommonException{ 
		Map returnData = new HashedMap();
		List<Map> returnList = new ArrayList<Map>();
		try{
			returnList = resourceDframeManagerMapper.getUseableList();  
			returnData.put("rows", returnList);
		} catch (Exception e) {
			 
		} 
		return returnData;
	} 
	
	/** 
	 * 查询条件value cableName
	 * @return Map<String,Object>
	 * @throws CommonException
	 */ 
	@Override
	public Map<String,Object> getCableNameList(String value,int start ,int limit) 
			throws CommonException{ 
		Map returnData = new HashedMap();
		List<Map> returnList = new ArrayList<Map>();
		int total = 0;
		try{
			total = resourceDframeManagerMapper.countCableNameList(value);  
			returnList = resourceDframeManagerMapper.getCableNameList(value,start,limit);  
			returnData.put("total", total); 
			returnData.put("rows", returnList);
		} catch (Exception e) {
		} 
		return returnData;
	}  
	
	/** 
	 * 查询条件value cableId
	 * @return Map<String,Object>
	 * @throws CommonException
	 */ 
	@Override
	public Map<String,Object> getFiberNameList(int value)throws CommonException { 
		Map returnData = new HashedMap();
		List<Map> returnList = new ArrayList<Map>();
 
		try{
			returnList = resourceDframeManagerMapper.getFiberNoList(value);
			returnData.put("rows", returnList);
		} catch (Exception e) {
			 
		} 
		return returnData;
	} 

	/** 
	 * 增加ODF子架
	 * @return Map<String,Object>
	 * @throws CommonException
	 */ 
	@Override
	public Result addOdfs(List<Map> odfList,int value)throws CommonException { 
		
		CommonResult result = new CommonResult();
		result.setReturnResult(CommonDefine.SUCCESS);  
		int count=0; 
		try {   
			//判断新增记录是否有重名ODF
			for (Map odf : odfList) {  
				//value = ResourceRoomId
				count = resourceDframeManagerMapper.judgeOdf(odf.get("odfNo").toString(),value);
				if(count>0){ 
					result.setReturnResult(CommonDefine.FAILED);
					result.setReturnMessage("ODF端子号已存在!"); 
					return result;
				}
			}
			//数据库中插入ODF记录
			for (Map odf : odfList) {  
				int fiberId=0;
				//value = ResourceCableId 
				if(odf.get("cableId") != null && !odf.get("cableId").toString().isEmpty()
						&& odf.get("fiberNo") != null && !odf.get("fiberNo").toString().isEmpty()){  
					fiberId = resourceDframeManagerMapper.getResourceFiberID(odf.get("fiberNo").toString(),
							Integer.parseInt(odf.get("cableId").toString()));
				} 
				resourceDframeManagerMapper.insertOdf(odf,value,fiberId);
			}
			result.setReturnMessage("新增ODF成功！"); 	
		}catch (Exception e) { 
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("新增ODF失败！"); 	
		}
		return result;
	}
	
	/** 
	 * 删除ODF子架
	 * @return Map<String,Object>
	 * @throws CommonException
	 */ 
	@Override
	public Result deleteOdfs(List<Map>sourceIds)throws CommonException {
		CommonResult result = new CommonResult();
		result.setReturnResult(CommonDefine.SUCCESS);   
		try { 
			for (Map odf : sourceIds) {  
				resourceDframeManagerMapper.deleteOdf(Integer.parseInt(odf.get("odfId").toString()));
			}
			result.setReturnMessage("删除ODF成功！"); 	
		}catch (Exception e) { 
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("删除ODF失败！"); 	
		}
		return result;
	}
	
	/** 
	 * 修改ODF子架
	 * @return Map<String,Object>
	 * @throws CommonException
	 */ 
	@Override
	public Result modifyODF(Map<String,String>map)throws CommonException {
		CommonResult result = new CommonResult();
		result.setReturnResult(CommonDefine.SUCCESS);   
		int fiberId=0;
		try { 
			//搜索关联的光纤Id
			if(map.get("cableId") != null && !map.get("cableId").toString().isEmpty()
					&& map.get("fiberNo") != null && !map.get("fiberNo").toString().isEmpty()){  
				fiberId = resourceDframeManagerMapper.getResourceFiberID(map.get("fiberNo").toString(),
						Integer.parseInt(map.get("cableId").toString()));
			} 
			if(fiberId!=0){
				map.put("fiberId",String.valueOf(fiberId));
			}else{
				map.put("fiberId","");
			} 
			resourceDframeManagerMapper.updateOdf(map); 
			result.setReturnMessage("修改ODF成功！"); 	
		}catch (Exception e) { 
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("修改ODF失败！"); 	
		}
		return result;
	}
	
	/** 
	 * 获取关联的ODF子架
	 * @return Map<String,Object>
	 * @throws CommonException
	 */ 
	@Override
	public Map<String,Object> getRelateOdfList(int value)throws CommonException{
		Map returnData = new HashedMap();
		List<Map> returnList = new ArrayList<Map>(); 
	 
		try{
			returnList = resourceDframeManagerMapper.getRelateOdfList(value);     
			returnData.put("rows", returnList);
		} catch (Exception e) {
			 
		} 
		return returnData;	
	}
	
	/**
	 * ODF关联ODF
	 * @param sourceIds
	 * @param targetIds
	 * @return
	 */
	public Result associateOdfWithOdf(List<Map>sourceIds, List<Map>targetIds) throws CommonException{
		CommonResult result = new CommonResult();
		result.setReturnResult(CommonDefine.SUCCESS);   
		Map  sourcedata = new HashedMap();
		Map  targetdata = new HashedMap();
		Boolean flag = true;
		int failedCnt = 0;
		try {
			//判断odf端子是否存在关联
			String str = ""; 
			String tmp = "";
			//判断段口是否被占用
			for (Map odfO : targetIds){
				tmp= resourceDframeManagerMapper.judgeOdfOccupation(odfO.get("odfId").toString());
				if(tmp !=null && !tmp.isEmpty() &&tmp!=""){					
					str = str+tmp+",";
					flag = false;
				}
			}
			if(str!=""){
				//特殊分支。针对端口已被占用的情况 
				result.setReturnMessage("端子" +str.substring(0, str.length()-1) +"已被占用！");
			}
			
			//判断是否选择自身关联
			int i = 0;
			for (Map odf : sourceIds) {  
				sourcedata = resourceDframeManagerMapper.getOdfData(
						Integer.parseInt(odf.get("odfId").toString()));
				targetdata = resourceDframeManagerMapper.getOdfData(
						Integer.parseInt(targetIds.get(i).get("odfId").toString()));
				if((null!=sourcedata ) && ( null != targetdata) && 
						(odf.get("odfId").equals(targetIds.get(i).get("odfId")))){ 
					flag = false;
					result.setReturnMessage("请选择非自身端子！");
					break;
				}
				i++;
			}
			if(flag){ 
				int j = 0;
				for (Map odf : sourceIds) {  
					sourcedata = resourceDframeManagerMapper.getOdfData(
							Integer.parseInt(odf.get("odfId").toString()));
					targetdata = resourceDframeManagerMapper.getOdfData(
							Integer.parseInt(targetIds.get(j).get("odfId").toString()));
					if((null!=sourcedata ) && ( null != targetdata)){
						Map conMap = new HashedMap();
						conMap.put("odfId", odf.get("odfId"));  
						conMap.put("OUT_TYPE", 2);
						conMap.put("OUT_TARGET", targetIds.get(j).get("odfId"));
						resourceDframeManagerMapper.updateOdfRelate(conMap);
					}else {
						failedCnt++;
					}
					j++;  
				} 
				if (failedCnt == 0){
					result.setReturnMessage("关联成功！");
				}else{
					result.setReturnMessage(failedCnt + "条记录关联失败！");
				}
			}
		} catch (Exception e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("关联失败！"); 	
		}
		return result;
	}
	
	/**
	 * ODF关联端口
	 * 
	 * @param sourceIds
	 * @param targetIds
	 * @return
	 */
	public Result associateOdfWithPtp(List<Map>sourceIds, List<Map>targetIds) throws CommonException{
		CommonResult result = new CommonResult();
		result.setReturnResult(CommonDefine.SUCCESS);   
		Map  sourcedata = new HashedMap();
		int failedCnt = 0;
		try {
			//判断端口是否存在关联
			String str = "";
			List<Map> tmp = new ArrayList<Map>();
			for (Map ptp : targetIds){
				tmp= resourceDframeManagerMapper.judgePortOccupation(ptp.get("nodeId").toString());
				if(tmp !=null && !tmp.isEmpty()){
					str = str+tmp.get(0).get("ptpTargetName")+",";
				}
			}
			if(str!=""){
				//特殊分支。针对端口已被占用的情况 
				result.setReturnMessage("端口" +str.substring(0, str.length()-1) +"已被占用！");
			}else{
			
				int i = 0;
				for (Map odf : sourceIds) {   
					sourcedata = resourceDframeManagerMapper.getOdfData(
							Integer.parseInt(odf.get("odfId").toString()));
					if(null!=sourcedata ){
						Map conMap = new HashedMap();
						conMap.put("odfId", odf.get("odfId"));  
						conMap.put("OUT_TYPE", 1);
						conMap.put("OUT_TARGET", targetIds.get(i).get("nodeId"));
						resourceDframeManagerMapper.updateOdfRelate(conMap);
					}else {
						failedCnt++;
					}
					i++; 
					
				} 
				if (failedCnt == 0){
					result.setReturnMessage("关联成功！");
				}else{
					result.setReturnMessage(failedCnt + "条记录关联失败！");
				}
			}
		} catch (Exception e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("关联失败！"); 	
		}
		return result;
	} 
	
	/**
	 * ODF删除关联
	 * @param sourceIds
	 * @return
	 */
	public Result deleteOdfRelate(List<Map>sourceIds)throws CommonException {
		CommonResult result = new CommonResult();
		result.setReturnResult(CommonDefine.SUCCESS);   
		Map  sourcedata = new HashedMap();
		try {
			for (Map odf : sourceIds) {
				sourcedata = resourceDframeManagerMapper.getOdfData(
						Integer.parseInt(odf.get("odfId").toString()));
				if(null!=sourcedata ){
					Map conMap = new HashedMap();
					conMap.put("odfId", odf.get("odfId"));  
					conMap.put("OUT_TYPE", 0);
					conMap.put("OUT_TARGET", 0);
					resourceDframeManagerMapper.updateOdfDelete(conMap);
				} 
			}
			result.setReturnMessage("删除关联成功！");
		} catch (Exception e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("删除关联失败！");
		}
		return result;
	}
	
	/**
	 * ODF导出
	 * @param map
	 * @return
	 */
	public CommonResult odfExport(Map<String,String> map)throws CommonException {  
		CommonResult result = new CommonResult();
		result.setReturnResult(CommonDefine.SUCCESS);
		//不分页 
		List<Map> returnList = new ArrayList<Map>();
		returnList = resourceDframeManagerMapper.getOdfList(map,0,-1);   
		String destination = "";
		
		for (Map rtn : returnList) {
			//配出类型转换
			if(rtn.get("outType")!=null){
				if(rtn.get("outType").equals(1)){
					rtn.put("outType", "端口"); 
				}else if(rtn.get("outType").equals(2)){
					rtn.put("outType", "ODF"); 
				}
			}
		} 
		try{
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd"); 
			String fileName = "ODF_"+sf.format(new Date()); 
			IExportExcel ex = new ExportExcelUtil(CommonDefine.PATH_ROOT+CommonDefine.EXCEL.ODF_EXPORT,fileName);
			destination=ex.writeExcel(returnList,CommonDefine.EXCEL.ODF_EXPORT,false);
			if(destination != ""){ 
				result.setReturnMessage(destination);
			}else{
				result.setReturnResult(CommonDefine.FAILED);
			}
		}catch(Exception e) {  
			result.setReturnResult(CommonDefine.FAILED);
		}
		return result;  
	}
 
	/** 
	 * @@@分权分域到网元@@@  
	 * DDF 架初始化页面getDdfList
	 * @param String 
	 * @return Map<String,Object>
	 * @throws CommonException
	 */ 
	@Override
	public Map<String,Object> getDdfList(Map<String,String> map,int start ,int limit,int userId) 
			throws CommonException{ 
		Map returnData = new HashedMap();
		List<Map> returnList = new ArrayList<Map>();
		List<Integer> count = new ArrayList<Integer>();
		int total = 0;
		try{
			total = resourceDframeManagerMapper.countDdfList(map);    
			returnList = resourceDframeManagerMapper.getDdfList(map,start,limit);     
			// 获取该用户有权限的网元列表
			List<Map> allNeModel = commonManagerService.getAllNeByEmsId(userId, CommonDefine.VALUE_ALL, false, null); 
			//区分网元所属网管的域
			for (Map rtn : returnList) {
				//设置默认值
				rtn.put("boolUserDeviceDomain", true);
				//遍历所有的记录 
				if(rtn.get("neId")!=null){ 
					rtn.put("boolUserDeviceDomain", false);
					for(Map ne:allNeModel){
						if(rtn.get("neId").equals(ne.get("BASE_NE_ID"))){
							rtn.put("boolUserDeviceDomain", true);
							break;
						}
					}
				}
			}  
			returnData.put("total", total); 
			returnData.put("rows", returnList);
		} catch (Exception e) {
			 
		} 
		return returnData;
	} 
	
	/** 
	 * 查询条件combo用途 
	 * @return Map<String,Object>
	 * @throws CommonException
	 */ 
	@Override
	public Map<String,Object> getDdfUseableList() throws CommonException{ 
		Map returnData = new HashedMap();
		List<Map> returnList = new ArrayList<Map>();
		try{
			returnList = resourceDframeManagerMapper.getDdfUseableList();  
			returnData.put("rows", returnList);
		} catch (Exception e) {
			 
		} 
		return returnData;
	} 
	
	/** 
	 * 增加DDF子架
	 * @return Map<String,Object>
	 * @throws CommonException
	 */ 
	@Override
	public Result addDdfs(List<Map> ddfList,int value)throws CommonException {  
		CommonResult result = new CommonResult();
		result.setReturnResult(CommonDefine.SUCCESS);  
		int count=0; 
		try {   
			//判断新增记录是否有重名DDF
			for (Map ddf : ddfList) {  
				//value = ResourceRoomId
				count = resourceDframeManagerMapper.judgeDdf(ddf.get("ddfNo").toString(),value);
				if(count>0){ 
					result.setReturnResult(CommonDefine.FAILED);
					result.setReturnMessage("DDF端子号已存在!"); 
					return result;
				}
			}
			//数据库中插入DDF记录
			for (Map ddf : ddfList) { 
				resourceDframeManagerMapper.insertDdf(ddf,value);
			}
			result.setReturnMessage("新增DDF成功！"); 	
		}catch (Exception e) { 
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("新增DDF失败！"); 	
		}
		return result;
	}
	
	/** 
	 * 删除DDF子架
	 * @return Map<String,Object>
	 * @throws CommonException
	 */ 
	@Override
	public Result deleteDdfs(List<Map>sourceIds)throws CommonException {
		CommonResult result = new CommonResult();
		result.setReturnResult(CommonDefine.SUCCESS);   
		try { 
			for (Map ddf : sourceIds) {  
				resourceDframeManagerMapper.deleteDdf(Integer.parseInt(ddf.get("ddfId").toString()));
			}
			result.setReturnMessage("删除DDF成功！"); 	
		}catch (Exception e) { 
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("删除DDF失败！"); 	
		}
		return result;
	}
	
	/** 
	 * 修改DDF子架
	 * @return Map<String,Object>
	 * @throws CommonException
	 */ 
	@Override
	public Result modifyDDF(Map<String,String>map)throws CommonException {
		CommonResult result = new CommonResult();
		result.setReturnResult(CommonDefine.SUCCESS);  
		try { 
			resourceDframeManagerMapper.updateDdf(map); 
			result.setReturnMessage("修改DDF成功！"); 	
		}catch (Exception e) { 
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("修改DDF失败！"); 	
		}
		return result;
	}
	
	/**
	 * DDF关联端口
	 * 
	 * @param sourceIds
	 * @param targetIds
	 * @return
	 */
	public Result associateDdfWithPtp(List<Map>sourceIds, List<Map>targetIds)throws CommonException {
		CommonResult result = new CommonResult();
		result.setReturnResult(CommonDefine.SUCCESS);   
		Map  sourcedata = new HashedMap();
		int failedCnt = 0;
		try {
			//判断端口是否存在关联
			String str = "";
			List<Map> tmp = new ArrayList<Map>();
			for (Map ptp : targetIds){
				tmp= resourceDframeManagerMapper.judgePortDDFOccupation(ptp.get("nodeId").toString());
				if(tmp !=null && !tmp.isEmpty()){
					str = str+tmp.get(0).get("ptpTargetName")+","; 
				}
			}
			if(str!=""){
				//特殊分支。针对端口已被占用的情况 
				result.setReturnMessage("端口" +str.substring(0, str.length()-1) +"已被占用！");
			}else{
			
				int i = 0;
				for (Map ddf : sourceIds) {   
					sourcedata = resourceDframeManagerMapper.getDdfData(
							Integer.parseInt(ddf.get("ddfId").toString()));
					if(null!=sourcedata ){
						Map conMap = new HashedMap();
						conMap.put("ddfId", ddf.get("ddfId"));   
						conMap.put("source", targetIds.get(i).get("nodeId"));
						resourceDframeManagerMapper.updateDdfRelate(conMap);
					}else {
						failedCnt++;
					}
					i++; 
					
				} 
				if (failedCnt == 0){
					result.setReturnMessage("关联成功！");
				}else{
					result.setReturnMessage(failedCnt + "条记录关联失败！");
				}
			}
		} catch (Exception e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("关联失败！"); 	
		}
		return result;
	} 
	
	/**
	 * DDF删除关联
	 * @param sourceIds
	 * @return
	 */
	public Result deleteDdfRelate(List<Map>sourceIds) throws CommonException{
		CommonResult result = new CommonResult();
		result.setReturnResult(CommonDefine.SUCCESS);   
		Map  sourcedata = new HashedMap();
		try {
			for (Map ddf : sourceIds) {
				sourcedata = resourceDframeManagerMapper.getDdfData(
						Integer.parseInt(ddf.get("ddfId").toString()));
				if(null!=sourcedata ){
					Map conMap = new HashedMap();
					conMap.put("ddfId", ddf.get("ddfId"));  
					conMap.put("source", 0); 
					resourceDframeManagerMapper.updateDdfDelete(conMap);
				} 
			}
			result.setReturnMessage("删除关联成功！");
		} catch (Exception e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("删除关联失败！");
		}
		return result;
	}
	
	/** 
	 * 获取关联的DDF子架
	 * @return Map<String,Object>
	 * @throws CommonException
	 */ 
	@Override
	public Map<String,Object> getRelateDdfList(int value)throws CommonException{
		Map returnData = new HashedMap();
		List<Map> returnList = new ArrayList<Map>(); 
	 
		try{
			returnList = resourceDframeManagerMapper.getRelateDdfList(value);     
			returnData.put("rows", returnList);
		} catch (Exception e) {
			 
		} 
		return returnData;	
	}
	
	/**
	 * DDF跳线管理
	 * @param sourceIds
	 * @param targetIds
	 * @return
	 */
	public Result associateDdfWithDdf(List<Map>sourceIds, List<Map>targetIds)throws CommonException {
		CommonResult result = new CommonResult();
		result.setReturnResult(CommonDefine.SUCCESS);   
		Map  sourcedata = new HashedMap();
		Map  targetdata = new HashedMap(); 
		Boolean flag = true;
		int failedCnt = 0;
		try {
			//判断ddf端子是否存在跳线
			String str = ""; 
			String tmp = "";
			for (Map ddfD : targetIds){
				tmp= resourceDframeManagerMapper.judgeDdfOccupation(ddfD.get("ddfId").toString());
				if(tmp !=null && !tmp.isEmpty() &&tmp!=""){
					flag = false;
					str = str+tmp+",";
				}
			}
			if(str!=""){
				//特殊分支。针对端口已被占用的情况 
				result.setReturnMessage("端子" +str.substring(0, str.length()-1) +"已被占用！");
			}
			//判断非自身挂链
			int i = 0;
			for (Map ddf : sourceIds) {  
				sourcedata = resourceDframeManagerMapper.getDdfData(
						Integer.parseInt(ddf.get("ddfId").toString()));
				targetdata = resourceDframeManagerMapper.getDdfData(
						Integer.parseInt(targetIds.get(i).get("ddfId").toString()));
				if((null!=sourcedata ) && ( null != targetdata) && 
						(ddf.get("ddfId").equals(targetIds.get(i).get("ddfId")))){
					flag=false;
					result.setReturnMessage("请选择非自身端子！");
					break;
				}
				i++; 
			}
			if(flag){ 
				int j = 0;
				for (Map ddf : sourceIds) {  
					sourcedata = resourceDframeManagerMapper.getDdfData(
							Integer.parseInt(ddf.get("ddfId").toString()));
					targetdata = resourceDframeManagerMapper.getDdfData(
							Integer.parseInt(targetIds.get(j).get("ddfId").toString()));
					if((null!=sourcedata ) && ( null != targetdata) && 
							(!ddf.get("ddfId").equals(targetIds.get(j).get("ddfId")))){
						Map conMap = new HashedMap();
						conMap.put("ddfId", ddf.get("ddfId"));   
						conMap.put("destination", targetdata.get("DDF_NO"));
						resourceDframeManagerMapper.updateDdfJumpLine(conMap);
					}else {
						failedCnt++;
					}
					j++; 
					
				} 
				if (failedCnt == 0){
					result.setReturnMessage("跳线设置成功！");
				}else{
					result.setReturnMessage(failedCnt + "条记录失败！");
				}
			}
		} catch (Exception e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("跳线设置失败！"); 	
		}
		return result;
	}

	/**
	 * DDF删除跳线
	 * @param sourceIds
	 * @return
	 */
	public Result deleteDdfJumpLine(List<Map>sourceIds)throws CommonException {
		CommonResult result = new CommonResult();
		result.setReturnResult(CommonDefine.SUCCESS);   
		Map  sourcedata = new HashedMap();
		try {
			for (Map ddf : sourceIds) {
				sourcedata = resourceDframeManagerMapper.getDdfData(
						Integer.parseInt(ddf.get("ddfId").toString()));
				if(null!=sourcedata ){
					Map conMap = new HashedMap();
					conMap.put("ddfId", ddf.get("ddfId"));  
					conMap.put("destination", 0); 
					resourceDframeManagerMapper.updateDelDdfJumpLine(conMap);
				} 
			}
			result.setReturnMessage("删除跳线成功！");
		} catch (Exception e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("删除跳线失败！");
		}
		return result;
	} 
	
	/**
	 * DDF导出
	 * @param map
	 * @return
	 */
	public CommonResult ddfExport(Map<String,String> map)throws CommonException {  
		CommonResult result = new CommonResult();
		result.setReturnResult(CommonDefine.SUCCESS);
		//不分页 
		List<Map> returnList = new ArrayList<Map>();
		returnList = resourceDframeManagerMapper.getDdfList(map,0,-1);   
		String destination = "";
		try{
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd"); 
			String fileName = "DDF_"+sf.format(new Date()); 
			IExportExcel ex = new ExportExcelUtil(CommonDefine.PATH_ROOT+CommonDefine.EXCEL.DDF_EXPORT,fileName);
			destination=ex.writeExcel(returnList,CommonDefine.EXCEL.DDF_EXPORT,false);
			if(destination != ""){ 
				result.setReturnMessage(destination);
			}else{
				result.setReturnResult(CommonDefine.FAILED);
			}
		}catch(Exception e) {
			result.setReturnResult(CommonDefine.FAILED);
		}  
		return result;
	} 
}
