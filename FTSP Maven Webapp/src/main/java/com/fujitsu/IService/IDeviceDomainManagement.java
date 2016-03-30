package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;
import com.fujitsu.manager.systemManager.model.DeviceRegion;

public interface IDeviceDomainManagement {
	/**
	 * Method name: searchDeviceDomain <BR>
	 * Description: 获取符合查询条件的当前设备管理域列表<BR>
	 * Remark: 2013-12-02<BR>
	 * @author wuchao
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> searchDeviceDomain(int startNumber, int pageSize) throws CommonException;
	/**
	 * Method name: createDeviceDomain <BR>
	 * Description: 新增一个设备管理域<BR>
	 * param:map-->设备管理域信息，包含设备管理域名，描述 
	 *       lists-->当前设备管理域ID的设备列表
	 * Remark: 2013-12-02<BR>
	 * @author wuchao
	 * @return true代表成功，false代表失败<BR>
	 */
	public boolean createDeviceDomain(Map<String,String> map,List<Map<String,Integer>> lists);
	/**
	 * Method name: deleteDeviceDomain <BR>
	 * Description: 删除设备管理域<BR>
	 * Remark: 2013-12-02<BR>
	 * @author wuchao
	 * @return String<BR>
	 */
	public boolean deleteDeviceDomain(List<Integer> lists);
	/**
	 * Method name: getDeviceTreeData <BR>
	 * Description: 获取网元的树状数据<BR>
	 * Remark: 2013-12-05<BR>
	 * @author wuchao
	 * @return String<BR>
	 */
	//public Map<String, Object> getDeviceTreeData();
	
	/**
	 * Method name: getDeviceTreeNodes <BR>
	 * Description: 树状数据<BR>
	 * Remark: 2013-12-05<BR>
	 * @author wuchao
	 * @return String<BR>
	 */
	//public List<DeviceTreeModel> getDeviceTreeNodes(DeviceRegion deviceRegion);
	
	/**
	 * Method name: getDeviceRegionDetailById <BR>
	 * Description: 获取设备域信息<BR>
	 * Remark: 2013-12-07<BR>
	 * @author wuchao
	 * @return String<BR>
	 */
	public Map<String,Object> getDeviceRegionDetailById(DeviceRegion deviceRegion) throws CommonException;
	/**
	 * Method name: getMenuAuthsByAuthDomainId <BR>
	 * Description: 获取设备域对应的所有设备<BR>
	 * Remark: 2013-12-26<BR>
	 * @author wuchao
	 * @return String<BR>
	 */
	public List<Map> getNesByDeviceDomainId(DeviceRegion deviceRegion) throws CommonException;
	public Map validateUserDeviceDomainName(DeviceRegion deviceRegion);
}
