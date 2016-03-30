package com.fujitsu.manager.viewManager.action;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.fujitsu.IService.IChannelManagerService;
import com.fujitsu.IService.IMethodLog;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;

public class ChannelAction extends AbstractAction {
	@Resource
	public IChannelManagerService  channelManagerService;
	private int id;
	/**
	 * 右键菜单，板卡属性
	 * @throws CommonException 
	 */
	@IMethodLog(desc = "测试Action")
	public String test()throws CommonException{
		//Map<String,String> data = new HashMap<String,String>();
		try{
			List<Map<String, Object>> rv = channelManagerService.getEmsGroup();
			resultArray = JSONArray.fromObject(rv);
//			Map<String,Object> data =  bayfaceManagerService.getUnitAttribute(unitId);
//			resultObj = JSONObject.fromObject(data);	 
		} catch ( CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());			
			resultObj = JSONObject.fromObject(result);
		}
		
		System.out.println(resultArray.toString());  
		return RESULT_ARRAY;
	} 
	/**
	 * 获取Ems分组
	 * @throws CommonException 
	 */
	@IMethodLog(desc = "获取Ems分组")
	public String getEmsGroup()throws CommonException{
		try{
			List<Map<String, Object>> rv = channelManagerService.getEmsGroup();
			resultArray = JSONArray.fromObject(rv);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());			
			resultObj = JSONObject.fromObject(result);
		}
		
		System.out.println(resultArray.toString());  
		return RESULT_ARRAY;
	} 
	/**
	 * 获取 Ems 列表
	 * @throws CommonException 
	 */
	@IMethodLog(desc = "获取Ems列表")
	public String getEmsList()throws CommonException{
		try{
			List<Map<String, Object>> rv = channelManagerService.getEmsList(id);
			resultArray = JSONArray.fromObject(rv);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());			
			resultObj = JSONObject.fromObject(result);
		}
		
		System.out.println(resultArray.toString());  
		return RESULT_ARRAY;
	} 
	/**
	 * 获取 子网 分组
	 * @throws CommonException 
	 */
	@IMethodLog(desc = "获取Ems分组")
	public String getSystemList()throws CommonException{
		try{
			List<Map<String, Object>> rv = channelManagerService.getSubnetList(id);
			resultArray = JSONArray.fromObject(rv);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());			
			resultObj = JSONObject.fromObject(result);
		}
		
		System.out.println(resultArray.toString());  
		return RESULT_ARRAY;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	} 
}
