package com.fujitsu.manager.inspectManager.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.handler.MessageHandler;
import com.fujitsu.IService.IInspectManagerService;
import com.fujitsu.IService.IMethodLog;

/**
 * 
 * @author WangXiaoye
 *	巡检报告web接口
 */
public class InspectReportAction extends AbstractAction{

	private static final long serialVersionUID = 7252401476028719189L;
    

	@Resource
	public IInspectManagerService inspectionManagerService;
	public String userId;
    public String inspectTime;
    public String taskName;
    public String inspector;
    public List<Integer> reportIdList;
    
    public int operation;//查看或导出
    public String[] selectList;
    
    public static final int OP_VIEW=1;
    public static final int OP_EXPORT=2;
    /**
	 * 巡检报告页面初始化，筛选时间数据获取
	 * 返回的List<Map>是巡检报告Combobox时间数据
	 * @return
	 *	SUCCESS resultObj - List<Map> 返回数据列表
	 *	ERROR resultObj - CommonResult 返回异常信息
	 */
	@IMethodLog(desc = "获取巡检报告筛选时间条件数据列表")
	public String getDateLimitList(){
		try {
			List<Map> data = inspectionManagerService.getDateLimitList();
			
			resultArray = JSONArray.fromObject(data);			

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.IDATE_LIMIT_LIST_GET_FAILED));	
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_ARRAY;
	}
    
	/**
	 * 巡检报告查询:查询符合条件的巡检报告
	 * @param
	 *	params 查询参数
	 * @return
	 *	SUCCESS resultObj - Map<String,Object> 返回数据列表
	 *	ERROR resultObj - CommonResult 返回异常信息
	 */
	@IMethodLog(desc = "获取巡检报告列表")
	public String getInspectReportList(){
		try {

			// 查询条件整合在map中
			Map<String, Object> map = new HashMap<String, Object>();

			map.put("userId", userId);
			
			if(inspectTime ==null){
				map.put("inspectTime", inspectTime);
			}else if(inspectTime.equals("")){
				map.put("inspectTime", null);
			}else{
				map.put("inspectTime", inspectTime);
			}
			
			if(taskName ==null){
				map.put("taskName", taskName);
			}else if(taskName.equals("")){
				map.put("taskName", null);
			}else{
				map.put("taskName", "%"+taskName+"%");
			}
			
			if(inspector ==null){
				map.put("inspector", inspector);
			}else if(inspector.equals("")){
				map.put("inspector", null);
			}else{
				map.put("inspector", "%"+inspector+"%");
			}
			
			map.put("start", start);
			map.put("limit", limit);
			
			Map<String,Object> data = inspectionManagerService.getInspectReportList(map);
			resultObj = JSONObject.fromObject(data);			

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.INSPECT_REPORT_LIST_GET_FAILED));			
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	
	/**
	 * 删除巡检报告
	 * 返回删除巡检报告是否成功信息
	 * @return
	 *	SUCCESS resultObj - CommonResult 返回删除成功信息
	 *	ERROR resultObj - CommonResult 返回异常信息
	 */
	@IMethodLog(desc = "删除巡检报告", type = IMethodLog.InfoType.DELETE)
	public String deleteInspectReport(){
		try {
			
			inspectionManagerService.deleteInspectReport(reportIdList);
			
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.INSPECT_REPORT_LIST_GET_FAILED));			
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.INSPECT_REPORT_LIST_GET_FAILED));			
			resultObj = JSONObject.fromObject(result);
			
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "获取巡检报告路径")
	public String getReportUrl(){
		try {
			Integer reportId=null;
			if(reportIdList!=null&&!reportIdList.isEmpty()){
				reportId=reportIdList.get(0);
			}
			String reportUrl=inspectionManagerService.getReportUrl(reportId,OP_EXPORT==operation,selectList);
			
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(reportUrl);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());			
			resultObj = JSONObject.fromObject(result);
			
		}
		
		return RESULT_OBJ;
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getInspectTime() {
		return inspectTime;
	}


	public void setInspectTime(String inspectTime) {
		this.inspectTime = inspectTime;
	}


	public String getTaskName() {
		return taskName;
	}


	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}


	public String getInspector() {
		return inspector;
	}


	public void setInspector(String inspector) {
		this.inspector = inspector;
	}



	public List<Integer> getReportIdList() {
		return reportIdList;
	}



	public void setReportIdList(List<Integer> reportIdList) {
		this.reportIdList = reportIdList;
	}

	public int getOperation() {
		return operation;
	}

	public void setOperation(int operation) {
		this.operation = operation;
	}

	public String[] getSelectList() {
		return selectList;
	}

	public void setSelectList(String[] selectList) {
		if(selectList==null||selectList.length==0){
			this.selectList=new String[]{"巡检报告纲要","异常项明细报告","网络层次明细报告","包机人明细报告"};
		}else
			this.selectList = selectList;
	}
}
