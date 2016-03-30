package com.fujitsu.manager.inspectManager.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.fujitsu.IService.ICommonManagerService;
import com.fujitsu.IService.IInspectManagerService;
import com.fujitsu.IService.IMethodLog;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;
import com.fujitsu.common.MessageCodeDefine;

/**
 * 
 * @author WangXiaoye 包机人web接口
 */
public class InspectEngineerAction extends AbstractAction {

	private static final long serialVersionUID = 7252401476028719189L;

	@Resource
	public IInspectManagerService inspectionManagerService;
	@Resource
	public ICommonManagerService commonManagerService;
	public int level = 0;
	public String engineerName;
	public String JobNo;
	public String telephone;
	public String area;
	public String thirdLevelCombo;
	public String department;
	public String role;
	public String note;
	public List<String> inspectEquipList;
	public List<String> inspectEquipNameList;
	public int engineerId;
	public List<Integer> engineerIdList;
	private String jsonString;

	/**
	 * 包机人查询:查询所有包机人的信息
	 * 
	 * @param params
	 *            查询参数
	 * @return
	 * 
	 *         SUCCESS resultObj - Map<String,Object> 返回数据列表 ERROR resultObj -
	 *         CommonResult 返回异常信息
	 */
	@IMethodLog(desc = "获取包机人信息列表")
	public String getEngineerList() {
		try {
			Map<String, Object> data = inspectionManagerService
					.getInspectEngineerList(start, limit);
			resultObj = JSONObject.fromObject(data);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(MessageCodeDefine.INSPECT_ENGINEER_LIST_GET_FAILED));
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	/**
	 * 包机人所属区域信息加载 返回的List<Map>是需加载的区域信息
	 * 
	 * @return SUCCESS resultObj - List<Map> 返回数据列表 ERROR resultObj -
	 *         CommonResult 返回异常信息
	 */
	@IMethodLog(desc = "获取包机人所属区域列表")
	public String getAreaNameList() {
		try {
			List<Map> data = inspectionManagerService.getAreaList(level);
			resultArray = JSONArray.fromObject(data);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(MessageCodeDefine.AREA_LIST_GET_FAILED));
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_ARRAY;
	}
	
	/**
	 * 判断包机人工号是否重复
	 * 
	 * @return SUCCESS resultObj - CommonResult 返回判断通过信息 ERROR resultObj -
	 *         CommonResult 返回判断失败信息
	 */
	@IMethodLog(desc = "判断包机人工号是否重复")
	public String checkJobNoExist() {
		try {
			// String utf8TaskName=new String(taskName.getBytes(),"utf8");
			Map<String, Object> returnMap = new HashMap<String, Object>();

			// 需要保存的包机人信息整合在map中
			Map<String, Object> map = new HashMap<String, Object>();

			map.put("engineerId", engineerId);
			map.put("JobNo", JobNo);

			Boolean data = inspectionManagerService.checkJobNoExist(map);
			returnMap.put("exit", data);
			resultObj = JSONObject.fromObject(returnMap);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("判断失败！");
			resultObj = JSONObject.fromObject(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return RESULT_OBJ;
	}

	/**
	 * 新增包机人 返回的String,新增包机人是否成功
	 * 
	 * @return SUCCESS resultObj - CommonResult 返回保存成功提示信息 ERROR resultObj -
	 *         CommonResult 返回异常信息
	 */
	@IMethodLog(desc = "新增包机人", type = IMethodLog.InfoType.MOD)
	public String addInspectEngineer() {

		try {
			// 需要保存的包机人信息整合在map中
			Map<String, Object> map = new HashMap<String, Object>();
			
			if(thirdLevelCombo.equals("")){
				thirdLevelCombo = null;
			}
			
			map.put("engineerId", null);
			map.put("engineerName", engineerName);
			map.put("JobNo", JobNo);
			map.put("telephone", telephone);
			map.put("thirdLevelCombo", thirdLevelCombo);
			map.put("department", department);
			map.put("role", role);
			map.put("note", note);
			// map.put("inspectEquipList", inspectEquipList);
			inspectionManagerService.addInspectEngineer(map, inspectEquipList);

			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.INSPECT_ENGINEER_ADD_SUCCESS));
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(MessageCodeDefine.INSPECT_ENGINEER_ADD_FAILED));
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	/**
	 * 编辑包机人保存 返回的String,保存编辑包机人是否成功
	 * 
	 * @return SUCCESS resultObj - CommonResult 返回保存成功提示信息 ERROR resultObj -
	 *         CommonResult 返回异常信息
	 */
	@IMethodLog(desc = "编辑更新包机人信息", type = IMethodLog.InfoType.MOD)
	public String updateInspectEngineer() {
		try {
			// 需要保存的包机人信息整合在map中
			Map<String, Object> map = new HashMap<String, Object>();

			if(thirdLevelCombo.equals("")){
				thirdLevelCombo = null;
			}
			map.put("engineerId", engineerId);
			map.put("engineerName", engineerName);
			map.put("JobNo", JobNo);
			map.put("telephone", telephone);
			map.put("thirdLevelCombo", thirdLevelCombo);
			map.put("department", department);
			map.put("role", role);
			map.put("note", note);
			// map.put("inspectEquipList", inspectEquipList);

			inspectionManagerService.updateInspectEngineer(map,
					inspectEquipList, inspectEquipNameList);

			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.INSPECT_ENGINEER_UPDATE_SUCCESS));
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(MessageCodeDefine.INSPECT_ENGINEER_UPDATE_FAILED));
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	/**
	 * 修改包机人页面初始化 返回的List<Map>是包机人巡检设备信息
	 * 
	 * @return SUCCESS resultObj - List<Map> 返回数据列表 ERROR resultObj -
	 *         CommonResult 返回异常信息
	 */
	@IMethodLog(desc = "获取巡检设备列表")
	public String initInspectEquip() {
		try {
			Map<String, Object> map = new HashMap<String, Object>();

			List<Map> data = inspectionManagerService.getInspectEquipList(
					engineerId, CommonDefine.INSPECT_ENGINEER);

			map.put("rows", data);
			map.put("total", data.size());

			resultObj = JSONObject.fromObject(map);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(MessageCodeDefine.INSPECT_EQUIP_GET_FAILED));
			resultObj = JSONObject.fromObject(result);

		}

		return RESULT_OBJ;
	}

	/**
	 * 修改包机人页面初始化 返回的Map是包机人基本信息
	 * 
	 * @return SUCCESS resultObj - Map 返回数据列表 ERROR resultObj - CommonResult
	 *         返回异常信息
	 */
	@IMethodLog(desc = "获取包机人基本信息")
	public String initInspectEngineer() {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			Map data = inspectionManagerService
					.getInspectEngineerInfo(engineerId);
			resultObj = JSONObject.fromObject(data);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(MessageCodeDefine.INSPECT_ENGINEER_INFO_GET_FAILED));
			resultObj = JSONObject.fromObject(result);

		}

		return RESULT_OBJ;
	}

	/**
	 * 删除包机人 返回删除包机人是否成功信息
	 * 
	 * @return SUCCESS resultObj - CommonResult 返回删除成功信息 ERROR resultObj -
	 *         CommonResult 返回异常信息
	 */
	@IMethodLog(desc = "删除包机人", type = IMethodLog.InfoType.DELETE)
	public String deleteInspectEngineer() {
		try {
			inspectionManagerService.deleteInspectEngineer(engineerIdList);

			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.INSPECT_ENGINEER_DELETE_SUCCESS));
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(MessageCodeDefine.INSPECT_ENGINEER_DELETE_FAILED));
			resultObj = JSONObject.fromObject(result);

		}

		return RESULT_OBJ;
	}
	
	public String exportInspectEngineer(){
		try{
			
			CommonResult data = inspectionManagerService.exportInspectEngineer(engineerIdList);
			resultObj = JSONObject.fromObject(data);
			
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
	
	return RESULT_OBJ;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getEngineerName() {
		return engineerName;
	}

	public void setEngineerName(String engineerName) {
		this.engineerName = engineerName;
	}

	public String getJobNo() {
		return JobNo;
	}

	public void setJobNo(String jobNo) {
		JobNo = jobNo;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public List<String> getInspectEquipList() {
		return inspectEquipList;
	}

	public void setInspectEquipList(List<String> inspectEquipList) {
		this.inspectEquipList = inspectEquipList;
	}

	public List<String> getInspectEquipNameList() {
		return inspectEquipNameList;
	}

	public void setInspectEquipNameList(List<String> inspectEquipNameList) {
		this.inspectEquipNameList = inspectEquipNameList;
	}

	public int getEngineerId() {
		return engineerId;
	}

	public void setEngineerId(int engineerId) {
		this.engineerId = engineerId;
	}

	public List<Integer> getEngineerIdList() {
		return engineerIdList;
	}

	public void setEngineerIdList(List<Integer> engineerIdList) {
		this.engineerIdList = engineerIdList;
	}

	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}

	public void setThirdLevelCombo(String thirdLevelCombo) {
		this.thirdLevelCombo = thirdLevelCombo;
	}

}
