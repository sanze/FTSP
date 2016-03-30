package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;
import com.fujitsu.manager.systemManager.model.DeviceRegion;
import com.fujitsu.manager.systemManager.model.DeviceTreeModel;

public interface IDeviceRegionManageService {
	/**
	 * Method name: getAuthRegionData <BR>
	 * Description: 获取设备域数据<BR>
	 * Remark: 2013-12-02<BR>
	 * @author wuchao
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getDeviceRegionData(int startNumber, int pageSize) throws CommonException;
	/**
	 * Method name: saveAuthDevice <BR>
	 * Description: 保存t_sys_device_domain表单元素<BR>
	 * Remark: 2013-12-02<BR>
	 * @author wuchao
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> saveDeviceRegion(DeviceRegion deviceRegion);
	/**
	 * Method name: deleteDemoTest <BR>
	 * Description: 删除t_sys_device_domain一行记录<BR>
	 * Remark: 2013-12-02<BR>
	 * @author wuchao
	 * @return String<BR>
	 */
	public Map<String, Object> deleteDeviceRegion(DeviceRegion deviceRegion);
	/**
	 * Method name: getDeviceTreeData <BR>
	 * Description: 获取网元的树状数据<BR>
	 * Remark: 2013-12-05<BR>
	 * @author wuchao
	 * @return String<BR>
	 */
	public Map<String, Object> getDeviceTreeData();
	
	/**
	 * Method name: getDeviceTreeNodes <BR>
	 * Description: 树状数据<BR>
	 * Remark: 2013-12-05<BR>
	 * @author wuchao
	 * @return String<BR>
	 */
	public List<DeviceTreeModel> getDeviceTreeNodes(DeviceRegion deviceRegion);
	
	/**
	 * Method name: getDeviceRegionDetailById <BR>
	 * Description: 获取设备域信息<BR>
	 * Remark: 2013-12-07<BR>
	 * @author wuchao
	 * @return String<BR>
	 */
	public Map<String,Object> getDeviceRegionDetailById(DeviceRegion deviceRegion) throws CommonException;
}
