package com.fujitsu.manager.systemManager.action;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.IService.ICommonManagerService;
import com.fujitsu.IService.IDeviceDomainManagement;
import com.fujitsu.IService.IMethodLog;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.manager.systemManager.model.DeviceRegion;
import com.fujitsu.manager.systemManager.util.JavaBeanMapUtil;
import com.fujitsu.manager.systemManager.util.TypeChangeUtil;
import com.opensymphony.xwork2.ModelDriven;
public class DeviceRegionManageAction extends AbstractAction implements ModelDriven<DeviceRegion>{
	private static final long serialVersionUID = -218037914366763644L;
	@Resource
	public IDeviceDomainManagement deviceRegionManageService;
	@Resource
	public ICommonManagerService commonManagerService;
	public DeviceRegion deviceRegion = new DeviceRegion();
	@Override
	public DeviceRegion getModel(){
		  return deviceRegion;
	}
	/**
	 * Method name: getAuthRegionData <BR>
	 * Description: 查询t_sys_auth_domain表分页元素<BR>
	 * Remark: 2013-12-02<BR>
	 * @author wuchao
	 * @return String<BR>
	 */
	@IMethodLog(desc = "查询设备域分页元素")
	public String getDeviceRegionData(){
		try {
			System.out.println("limit:"+limit);
			Map<String, Object> emsGroupMap = deviceRegionManageService.searchDeviceDomain(start,limit);
			resultObj = JSONObject.fromObject(emsGroupMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	
	/**
	 * Method name: getDeviceTreeNodes <BR>
	 * Description: 获取树状数据<BR>
	 * Remark: 2013-12-05<BR>
	 * @author wuchao
	 * @return String<BR>
	 */
//	public String getDeviceTreeNodes(){
//		List<DeviceTreeModel> treeList= deviceRegionManageService.getDeviceTreeNodes(deviceRegion);
//		resultArray =JsonUtil.getJson4JavaList(treeList);
//		return RESULT_ARRAY;
//	}
	/**
	 * Method name: getDeviceTreeData <BR>
	 * Description: 获取网元的树状数据<BR>
	 * Remark: 2013-12-05<BR>
	 * @author wuchao
	 * @return String<BR>
	 */
//	public String getDeviceTreeData(){
//		Map<String, Object> deviceTrees =deviceRegionManageService.getDeviceTreeData();
//		return RESULT_OBJ;
//	}
	
	/**
	 * Method name: createDeviceDomain <BR>
	 * Description: 设备域保存<BR>
	 * Remark: 2013-12-07<BR>
	 * @author wuchao
	 * @return String<BR>
	 */
	@IMethodLog(desc = "设备域保存", type = IMethodLog.InfoType.MOD)
	public String createDeviceDomain(){
		Map result = new HashMap();
		if(deviceRegion.getId().equals("0")){
			//验证设备域名称是否存在
			Map validateResult=validateUserDeviceDomainName();
			if(!(Boolean)validateResult.get("success")){
				resultObj = JSONObject.fromObject(validateResult);
				return RESULT_OBJ;
			}
		}
		
		List<Map> lists=ListStringtoListMap(deviceRegion.getNeList());
		List<Map<String,Integer>> nes=new ArrayList<Map<String,Integer>>();
		if(lists!=null){
			for(int i=0;i<lists.size();i++){
				Map m=lists.get(i);
				int nodeLevel=TypeChangeUtil.changeObjToInteger(m.get("nodeLevel"));
//				if(CommonDefine.TREE.NODE.NE==nodeLevel){//传过来是网元
					Map<String,Integer> ne=new HashMap<String,Integer>();
					ne.put("neId",TypeChangeUtil.changeObjToInteger(m.get("nodeId")));
					ne.put("neType",nodeLevel);
					nes.add(ne);
//				}else{//传过来是上级网元
//					putChildNodes(nes,TypeChangeUtil.changeObjToInteger(m.get("nodeId")),TypeChangeUtil.changeObjToInteger(m.get("nodeLevel")),TypeChangeUtil.changeObjToInteger(m.get("endLevel")));
//				}
			}
		}
		
		boolean isCreate = deviceRegionManageService.createDeviceDomain(JavaBeanMapUtil.convertBean(deviceRegion),nes);
		if(isCreate){
			result.put("success", true);
			result.put("msg", "保存成功！");
		}else{
			result.put("success", false);
			result.put("msg", "保存失败!");
		}
		resultObj = JSONObject.fromObject(result);
		return RESULT_OBJ;
	}
	
	private Map validateUserDeviceDomainName() {
		return deviceRegionManageService.validateUserDeviceDomainName(deviceRegion);
	}
	public void putChildNodes(List<Map<String,Integer>> nes,int nodeId,int nodeLevel,int endLevel){
		try {
			if (endLevel > 0) {
				List<Map> nodes = commonManagerService.treeGetChildNodes(nodeId,nodeLevel,endLevel,CommonDefine.USER_ADMIN_ID);
				for(Map node:nodes){
					if(TypeChangeUtil.changeObjToInteger(node.get("nodeLevel"))==CommonDefine.TREE.NODE.NE){
						Map<String,Integer> ne=new HashMap<String,Integer>();
						ne.put("neId",TypeChangeUtil.changeObjToInteger(node.get("nodeId")));
						nes.add(ne);
					}else{
						putChildNodes(nes,TypeChangeUtil.changeObjToInteger(node.get("nodeId")),TypeChangeUtil.changeObjToInteger(node.get("nodeLevel")),endLevel);
					}
				}
			}
		} catch (CommonException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Method name: getDeviceRegionDetailById <BR>
	 * Description: 获取设备域详细信息<BR>
	 * Remark: 2013-12-07<BR>
	 * @author wuchao
	 * @return String<BR>
	 */
	@IMethodLog(desc = "获取设备域详细信息")
	public String getDeviceRegionDetailById(){
		try {
			Map<String,Object> deviceRegionDetail =deviceRegionManageService.getDeviceRegionDetailById(deviceRegion);
			resultObj = JSONObject.fromObject(deviceRegionDetail);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	/**
	 * Method name: deleteDeviceRegions <BR>
	 * Description: 删除设备域记录<BR>
	 * Remark: 2013-12-07<BR>
	 * @author wuchao
	 * @return String<BR>
	 */
	@IMethodLog(desc = "删除设备域", type = IMethodLog.InfoType.DELETE)
	public String deleteDeviceDomain(){
		List<Integer> lists=new ArrayList<Integer>();
		if(deviceRegion.getIds()!=null){
			for(int i=0;i<deviceRegion.getIds().size();i++){
				String id=deviceRegion.getIds().get(i);
				lists.add(Integer.parseInt(id));
			}
		}
		boolean isDelete= deviceRegionManageService.deleteDeviceDomain(lists);
		Map map=new HashMap();
		if(isDelete){
			map.put("success",true);
		}else{
			map.put("success",false);
		}
		// 将返回的结果转成JSON对象，返回前台
		resultObj = JSONObject.fromObject(map);
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: getNesByDeviceDomainId <BR>
	 * Description: 获取设备域对应的所有设备<BR>
	 * Remark: 2013-12-26<BR>
	 * @author wuchao
	 * @return String<BR>
	 */
	public String getNesByDeviceDomainId(){
		Map<String,Object> devices;
		try {
			List<Map> lists= deviceRegionManageService.getNesByDeviceDomainId(deviceRegion);
			devices = new HashMap<String,Object>();
			devices.put("devices",lists);
			resultObj = JSONObject.fromObject(devices);
		} catch (CommonException e) {
			e.printStackTrace();
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
}
