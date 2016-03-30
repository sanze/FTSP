package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.fujitsu.manager.systemManager.model.DeviceRegion;

public interface DeviceRegionManageMapper {
	/**
	 * Method name: selectAuthRegionDataList <BR>
	 * Description: 查询所有设备域分页数据<BR>
	 * Remark: 2013-12-02<BR>
	 * @author wuchao
	 * @return Map<String, Object><BR>
	 */
	public List<Map> selectDeviceRegionDataList(@Param(value = "map")Map map);
	/**
	 * Method name: countAuthRegionDataList <BR>
	 * Description: 查询所有设备域总数<BR>
	 * Remark: 2013-12-02<BR>
	 * @author wuchao
	 * @return Map<String, Object><BR>
	 */
	public int countDeviceRegionDataList(@Param(value = "map")Map map);
	/**
	 * Method name: insert <BR>
	 * Description: 插入数据<BR>
	 * Remark: 2013-12-02<BR>
	 * @author wuchao
	 * @return Map<String, Object><BR>
	 */
	public void insert(@Param(value = "map")Map m);
	/**
	 * Method name: update <BR>
	 * Description: 更新数据<BR>
	 * Remark: 2013-12-02<BR>
	 * @author wuchao
	 * @return Map<String, Object><BR>
	 */
	public void update(@Param(value = "deviceRegion")DeviceRegion deviceRegion);
	/**
	 * Method name: delete<BR>
	 * Description: 删除一行记录<BR>
	 * Remark: 2013-12-02<BR>
	 * @author hg
	 * @return String<BR>
	 */
	public void delete(@Param(value = "deviceRegion")DeviceRegion deviceRegion);
	
	//获取网管数据
	public List<Map> getEMSDatas(@Param(value = "deviceRegion")DeviceRegion deviceRegion);
	//根据网管id过去子网数据
	public List<Map> getSubnetByEMSIdDatas(@Param(value = "deviceRegion")DeviceRegion deviceRegion);
	//根据子网id获取网元
	public List<Map> getDeviceBySubnetIdDatas(@Param(value = "deviceRegion")DeviceRegion deviceRegion);
	//插入设备域关联的网元
	public void insertDeviceRegionRefNe(@Param(value = "deviceRegion")DeviceRegion deviceRegion);
	//删除设备域关联的网元
	public void deleteDeviceRegionRefNe(@Param(value = "deviceRegion")DeviceRegion deviceRegion);
	//删除设备域关联的用户
	public void deleteDeviceRegionRefUser(@Param(value = "deviceRegion")DeviceRegion deviceRegion);
	public List<Map> getNesByDeviceDomainId(@Param(value = "deviceRegion")DeviceRegion deviceRegion);
	public Map getParentPathByNodeLevelAndNodeId(@Param(value = "nodeLevel")int nodeLevel,@Param(value = "nodeId")int nodeId,@Param(value = "Define") Map Define);
	public int validateUserDeviceDomainName(@Param(value = "name")String name);
}
