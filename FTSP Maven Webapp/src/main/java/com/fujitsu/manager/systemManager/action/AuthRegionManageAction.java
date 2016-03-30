package com.fujitsu.manager.systemManager.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.IService.IAuthRegionManageService;
import com.fujitsu.IService.IMethodLog;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.manager.systemManager.model.AuthRegion;
import com.fujitsu.manager.systemManager.model.AuthTreeModel;
import com.fujitsu.util.JsonUtil;
import com.opensymphony.xwork2.ModelDriven;

public class AuthRegionManageAction extends AbstractAction implements ModelDriven<AuthRegion>{

	private static final long serialVersionUID = -218037914366763644L;
	@Resource
	public IAuthRegionManageService authRegionManageService;
	public AuthRegion authRegion = new AuthRegion();
	@Override
	public AuthRegion getModel(){
		  return authRegion;
	}
	/**
	 * Method name: getAuthRegionData <BR>
	 * Description: 查询t_sys_auth_domain表分页元素<BR>
	 * Remark: 2013-12-02<BR>
	 * @author wuchao
	 * @return String<BR>
	 */
	@IMethodLog(desc = "查询权限域分页元素")
	public String searchAuthDomain(){
		try {
			Map<String, Object> emsGroupMap = authRegionManageService.searchAuthDomain(start,limit);
			resultObj = JSONObject.fromObject(emsGroupMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: getAuthTreeNodes <BR>
	 * Description: 获取树状数据<BR>
	 * Remark: 2013-12-07<BR>
	 * @author wuchao
	 * @return String<BR>
	 */
	public String getAuthTreeNodes(){
		List<AuthTreeModel> treeList= authRegionManageService.getAuthTreeNodes(authRegion);
		resultArray =JsonUtil.getJson4JavaList(treeList);
//		for(Object o:resultArray){
//			JSONObject json=(JSONObject)o;
//			if(!(Boolean)json.get("leaf")){
//				json.remove("checked");
//			}
//		}
		return RESULT_ARRAY;
	}
	
	/**
	 * Method name: saveDeviceRegionData <BR>
	 * Description: 设备域保存<BR>
	 * Remark: 2013-12-07<BR>
	 * @author wuchao
	 * @return String<BR>
	 */
	@IMethodLog(desc = "设备域保存", type = IMethodLog.InfoType.MOD)
	public String saveAuthRegionData(){
		Map<String, Object> map=new HashMap();
		if(authRegion.getId().equals("0")){
			//验证权限域名称是否存在
			Map validateResult=validateUserAuthDomainName();
			if(!(Boolean)validateResult.get("success")){
				resultObj = JSONObject.fromObject(validateResult);
				return RESULT_OBJ;
			}
		}
		try {
			map = authRegionManageService.saveAuthRegionData(authRegion);
		} catch (CommonException e) {
			e.printStackTrace();
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		// 将返回的结果转成JSON对象，返回前台
		resultObj = JSONObject.fromObject(map);
		return RESULT_OBJ;
	}
	
	private Map validateUserAuthDomainName() {
		return authRegionManageService.validateUserAuthDomainName(authRegion);
	}
	/**
	 * Method name: deleteAuthRegions <BR>
	 * Description: 删除权限域记录<BR>
	 * Remark: 2013-12-07<BR>
	 * @author wuchao
	 * @return String<BR>
	 */
	@IMethodLog(desc = "删除权限域记录", type = IMethodLog.InfoType.DELETE)
	public String deleteAuthRegions(){
		Map<String, Object> map=new HashMap();
		try {
			map = authRegionManageService.deleteAuthRegions(authRegion);
		} catch (CommonException e) {
			e.printStackTrace();
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			e.printStackTrace();
		}
		// 将返回的结果转成JSON对象，返回前台
		resultObj = JSONObject.fromObject(map);
		return RESULT_OBJ;
	}
	
	
	/**
	 * Method name: getMenuAuthsByAuthDomainId <BR>
	 * Description: 获取权限域对应的菜单权限<BR>
	 * Remark: 2013-12-10<BR>
	 * @author wuchao
	 * @return String<BR>
	 */
	public String getMenuAuthsByAuthDomainId(){
		Map<String,Object> menus;
		try {
			List<Map> menuAuths= authRegionManageService.getMenuAuthsByAuthDomainId(authRegion);
			menus = new HashMap<String,Object>();
			menus.put("menus",menuAuths);
			resultObj = JSONObject.fromObject(menus);
		} catch (CommonException e) {
			e.printStackTrace();
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
}
