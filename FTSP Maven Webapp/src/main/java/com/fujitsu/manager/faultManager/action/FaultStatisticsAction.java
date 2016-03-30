package com.fujitsu.manager.faultManager.action;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;

import com.fujitsu.IService.IFaultStatisticsService;
import com.fujitsu.IService.IMethodLog;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.manager.faultManager.model.EquipNameModel;

@Controller
public class FaultStatisticsAction extends AbstractAction{
	
	private static final long serialVersionUID = 8445215474404612023L;
	//接受前台封装好的查询条件
	private String jsonString ;
	// 故障Id
	private int faultId;
	//一级故障原因Id
	private int parentFaultReasonId;
	private int alarmId;
	//传输系统Id
	private int sysId;
	//板卡Id
	private int unitId;
	private String alarmName;
	private int severity;
	private String neName;
	private String startTime;
	private String clearTime;
	// 故障处理 2：响应 3：恢复 5：归档
	private int processType;
	private Map<String,String> paramMap;
	@Resource(name = "faultStatisticsServiceImpl")
	private IFaultStatisticsService faultService ;
	@IMethodLog(desc = "获取故障列表")
	public String getFaultList(){
		try{
			listResult = faultService.getFaultList(jsonString,start,limit == 0?200:limit);
		}catch(CommonException e){
			listResult.setReturnMessage(e.getErrorMessage());
			listResult.setReturnResult(CommonDefine.FAILED);
		}
		resultObj = JSONObject.fromObject(listResult);
		return	RESULT_OBJ;
	}
	
	@IMethodLog(desc = "获取一级故障原因列表")
	public String getFaultReason(){
		try{
			listResult = faultService.getFaultReason();
		}catch(CommonException e){
			listResult.setReturnMessage(e.getErrorMessage());
			listResult.setReturnResult(CommonDefine.FAILED);
		}
		resultObj = JSONObject.fromObject(listResult);
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "获取二级故障原因列表")
	public String getSubFaultReason(){
		try{
			listResult = faultService.getSubFaultReason(parentFaultReasonId);
		}catch(CommonException e){
			listResult.setReturnMessage(e.getErrorMessage());
			listResult.setReturnResult(CommonDefine.FAILED);
		}
		resultObj = JSONObject.fromObject(listResult);
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "删除指定故障信息记录", type = IMethodLog.InfoType.DELETE)
	public String deleteFaultRecord(){
		try{
			result = faultService.deleteFaultRecord(faultId);
		}catch(CommonException e){
			result.setReturnMessage(e.getErrorMessage());
			result.setReturnResult(CommonDefine.FAILED);
		}
		resultObj = JSONObject.fromObject(result);
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "获取传输系统选择框值")
	public String getTransformSystem(){
		try{
			listResult = faultService.getTransformSystem();
		}catch(CommonException e){
			listResult.setReturnMessage(e.getErrorMessage());
			listResult.setReturnResult(CommonDefine.FAILED);
		}
		if(paramMap!=null&&paramMap.get("needAll").equals("0")){
		}else{
			// 添加一条全部的选项
			Map r = new HashMap();
			r.put("RESOURCE_PROJECTS_ID","0");
			r.put("DISPLAY_NAME", "全部");
			listResult.getRows().add(r);
			listResult.setTotal(listResult.getTotal()+1);
		}
		resultObj = JSONObject.fromObject(listResult);
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "获取传输系统列表")
	public String getTransformSystemList(){
		try{
			listResult = faultService.getTransformSystem();
		}catch(CommonException e){
			listResult.setReturnMessage(e.getErrorMessage());
			listResult.setReturnResult(CommonDefine.FAILED);
		}
		resultObj = JSONObject.fromObject(listResult);
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "获取相关告警")
	public String getAlarmByFaultId(){
		try{
			listResult = faultService.getAlarmByFaultId(faultId);
		}catch(CommonException e){
			listResult.setReturnMessage(e.getErrorMessage());
			listResult.setReturnResult(CommonDefine.FAILED);
		}
		resultObj = JSONObject.fromObject(listResult);
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "获取台站")
	public String getStateBySysId(){
		try{
			listResult = faultService.getStateBySysId(sysId);
		}catch(CommonException e){
			listResult.setReturnMessage(e.getErrorMessage());
			listResult.setReturnResult(CommonDefine.FAILED);
		}
		resultObj = JSONObject.fromObject(listResult);
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "保存", type = IMethodLog.InfoType.MOD)
	public String save(){
		try{
			listResult = faultService.save(jsonString);
		}catch(CommonException e){
			listResult.setReturnMessage(e.getErrorMessage());
			listResult.setReturnResult(CommonDefine.FAILED);
		}
		resultObj = JSONObject.fromObject(listResult);
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "故障响应/恢复/归档处理", type = IMethodLog.InfoType.MOD)
	public String faultProcess(){
		try{
			result = faultService.faultProcess(faultId,processType);
		}catch(CommonException e){
			result.setReturnMessage(e.getErrorMessage());
			result.setReturnResult(CommonDefine.FAILED);
		}
		resultObj = JSONObject.fromObject(result);
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "删除故障下的告警", type = IMethodLog.InfoType.DELETE)
	public String alarmDelete(){
		try{
			result = faultService.alarmDelete(faultId,alarmId);
		}catch(CommonException e){
			result.setReturnMessage(e.getErrorMessage());
			result.setReturnResult(CommonDefine.FAILED);
		}
		resultObj = JSONObject.fromObject(result);
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "获取网管网元板卡台站子网名称")
	public String getEquipName(){
		EquipNameModel result;
		try{
			result = faultService.getEquipName(unitId);
		}catch(CommonException e){
			result = new EquipNameModel();
		}
		resultObj = JSONObject.fromObject(result);
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "获取当前告警")
	public String getCurAlmList(){
		Map<String, Object> result = new HashMap<String, Object>();
		try{
			result  = faultService.getCurAlmList(start,limit == 0?500:limit);
		}catch(CommonException e){
		}
		resultObj = JSONObject.fromObject(result);
		return	RESULT_OBJ;
	}
	public String alarmAdd(){
		try{
			result = faultService.alarmAdd(faultId,neName,alarmId,alarmName,severity,startTime,clearTime);
			result.setReturnResult(CommonDefine.SUCCESS);
		}catch(CommonException e){
			result.setReturnMessage(e.getErrorMessage());
			result.setReturnResult(CommonDefine.FAILED);
		}
		resultObj = JSONObject.fromObject(result);
		return RESULT_OBJ;
	}
	//-----333-----
	@IMethodLog(desc = "查询故障总计图")
	public String getFaultStatisticsTotal(){
		try{
			Map<String,Object> datasMap=faultService.getFaultStatisticsTotal(paramMap);
			resultObj=JSONObject.fromObject(datasMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "查询故障统计图")
	public String getFaultStatisticsClassify(){
		try{
			Map<String,Object> datasMap=faultService.getFaultStatisticsClassify(paramMap);
			resultObj=JSONObject.fromObject(datasMap);
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

	public int getParentFaultReasonId() {
		return parentFaultReasonId;
	}

	public void setParentFaultReasonId(int parentFaultReasonId) {
		this.parentFaultReasonId = parentFaultReasonId;
	}

	public int getFaultId() {
		return faultId;
	}

	public void setFaultId(int faultId) {
		this.faultId = faultId;
	}

	public int getSysId() {
		return sysId;
	}

	public void setSysId(int sysId) {
		this.sysId = sysId;
	}

	public int getProcessType() {
		return processType;
	}

	public void setProcessType(int processType) {
		this.processType = processType;
	}

	public int getAlarmId() {
		return alarmId;
	}

	public void setAlarmId(int alarmId) {
		this.alarmId = alarmId;
	}

	public int getUnitId() {
		return unitId;
	}

	public void setUnitId(int unitId) {
		this.unitId = unitId;
	}

	public String getAlarmName() {
		return alarmName;
	}

	public void setAlarmName(String alarmName) {
		this.alarmName = alarmName;
	}

	public int getSeverity() {
		return severity;
	}

	public void setSeverity(int severity) {
		this.severity = severity;
	}

	public String getNeName() {
		return neName;
	}

	public void setNeName(String neName) {
		this.neName = neName;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getClearTime() {
		return clearTime;
	}

	public void setClearTime(String clearTime) {
		this.clearTime = clearTime;
	}
	
	
}
