package com.fujitsu.manager.systemManager.action;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.IService.IMethodLog;
import com.fujitsu.IService.ISysInterfaceManageService;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.activeMq.JMSSender;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;

public class InterfaceManageAction extends AbstractAction {
	@Resource
	private ISysInterfaceManageService interfaceManageService;
	private String jsonString;
	
	public String getJsonString() {
		return jsonString;
	}
	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}
	/**
	 * 
	 * Method name: getAllInterface <BR>
	 * Description: 查询系统接口 <BR>
	 * Remark: <BR>
	 * @return  String<BR>
	 */
	@IMethodLog(desc = "查询系统接口")
	public String getAllInterface(){
		try {
			Map<String, Object> reMap = interfaceManageService.getSysInterfaceManageData(start,limit);
			resultObj = JSONObject.fromObject(reMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	/**
	 * Method name: addInterface <BR>
	 * Description: 新增接口 <BR>
	 * Remark: <BR>
	 * @return  String<BR>
	 */
	@IMethodLog(desc = "新增接口", type = IMethodLog.InfoType.MOD)
	public String addInterface(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			interfaceManageService.addInterface(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("保存成功");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * 
	 * Method name: modifyInterface <BR>
	 * Description: 修改接口 <BR>
	 * Remark: <BR>
	 * @return  String<BR>
	 */
	@IMethodLog(desc = "修改接口", type = IMethodLog.InfoType.MOD)
	public String modifyInterface(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			interfaceManageService.modifyInterface(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("修改成功");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	
	}
	
	/**
	 * 
	 * Method name: deleteInterface <BR>
	 * Description: 删除接口 <BR>
	 * Remark: <BR>
	 * @return  String<BR>
	 */
	@IMethodLog(desc = "删除接口", type = IMethodLog.InfoType.DELETE)
	public String deleteInterface(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			interfaceManageService.deleteInterface(paramMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("删除成功");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: getDetailById <BR>
	 * Description: 根据Id查接口 <BR>
	 * Remark: <BR>
	 * @return  String<BR>
	 */
	@IMethodLog(desc = "根据Id查接口")
	public String getDetailById(){
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			Map<String, Object> reMap = interfaceManageService.getDetailById(paramMap);
			resultObj = JSONObject.fromObject(reMap);
			System.out.println(resultObj.toString());
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "测试推告警")
	public String test(){
		System.out.println("qwdqdwdwd");
		Map<String,Object> map = new HashMap<String, Object>();
    	map.put("_id", 222);
    	map.put("NE_ID", 3);
//    	JSONObject jsonObject = JSONObject.fromObject(map);
		JMSSender.sendMessage(CommonDefine.MESSAGE_TYPE_ALARM, map);
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "验证接口")
	public String checkInterface(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap = (Map<String, Object>) jsonObject;
			Map<String,Object> rest = interfaceManageService.checkInterface(paramMap);
			result.setReturnResult(Integer.valueOf(rest.get("FLAG").toString()));
			result.setReturnMessage(rest.get("MESSAGE").toString());
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	
	}
}
