package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * @author sse
 *
 */
public interface TopoManagerMapper {

	public List<Map> getAllEMSGroup();
	public List<Map> getAllEMS();
	public List<Map> getAllEMSInFTSP();
	public List<Map> getEMSInGroup(@Param(value = "nodeId")int nodeId);
	
	
	/**
	 * @@@分权分域到网元@@@
	 * 获取网管直属子网
	 * @param nodeId
	 * @return
	 */
	public List<Map> getDirectSubnetInEMS(@Param(value = "nodeId")int nodeId,
			@Param(value = "userId") Integer userId, 
			@Param(value = "Define") Map Define);
	
	
	/**
	 * @@@分权分域到网元@@@
	 * 获取网管直属网元
	 * @param nodeId
	 * @return
	 */
	public List<Map> getDirectNeInEMS(@Param(value = "nodeId")int nodeId,
			@Param(value = "userId") Integer userId, 
			@Param(value = "Define") Map Define);
	
	/**
	 * @@@分权分域到网元@@@
	 * 获取网管下所有的外部link
	 * @param nodeId
	 * @return
	 */
	public List<Map> getLinkInEMS(@Param(value = "nodeId")int nodeId,
			@Param(value = "userId") Integer userId, 
			@Param(value = "Define") Map Define);
	
	/**
	 * 获取当前网管下所有的子网（直属和非直属）
	 * @param nodeId
	 * @return
	 */
	public List<Map> getAllSubnet(@Param(value = "nodeId")int nodeId);
	
	
	public Map getNeIdByPtpId(@Param(value = "ptpId")int ptpId);
	
	
	public Map getNeByNeId(@Param(value = "neId")int neId);
	
	/**
	 * @@@分权分域到网元@@@
	 * @param subnetId
	 * @param userId
	 * @param Define
	 * @return
	 */
	public Map getSubnetBySubnetId(@Param(value = "subnetId")int subnetId,
			@Param(value = "userId") Integer userId, 
			@Param(value = "Define") Map Define);
	
	/**
	 * @@@分权分域到网元@@@
	 * @param subnetId
	 * @param userId
	 * @param Define
	 * @return
	 */
	public List<Map> getDirectSubnetInSubnet(@Param(value = "subnetId")int subnetId,
			@Param(value = "userId") Integer userId, 
			@Param(value = "Define") Map Define);
	
	
	/**
	 * @@@分权分域到网元@@@
	 * 获取当前子网下的所有直属网元
	 * @param subnetId
	 * @return
	 */
	public List<Map> getDirectNeInSubnet(@Param(value = "subnetId")int subnetId,
			@Param(value = "userId") Integer userId, 
			@Param(value = "Define") Map Define);
	
	
	/**保存网管分组的坐标
	 * @param emsGroupId
	 * @param position_X
	 * @param position_Y
	 */
	public void saveEMSGroupPosition(@Param(value = "emsGroupId")int emsGroupId,@Param(value = "position_X")int position_X,@Param(value = "position_Y")int position_Y);
	
	/**保存网管坐标
	 * @param emsId
	 * @param position_X
	 * @param position_Y
	 */
	public void saveEMSPosition(@Param(value = "emsId")int subnetId,@Param(value = "position_X")int position_X,@Param(value = "position_Y")int position_Y);
	
	/**保存子网坐标
	 * @param subnetId
	 * @param position_X
	 * @param position_Y
	 */
	public void saveSubnetPosition(@Param(value = "subnetId")int subnetId,@Param(value = "position_X")int position_X,@Param(value = "position_Y")int position_Y);
	
	/**保存网元坐标
	 * @param neId
	 * @param position_X
	 * @param position_Y
	 */
	public void saveNePosition(@Param(value = "neId")int neId,@Param(value = "position_X")int position_X,@Param(value = "position_Y")int position_Y);
	
	/**
	 * @@@分权分域到网元@@@
	 * 通过网元名模糊查询出所有相关的网元记录
	 * @param displayName
	 * @return
	 */
	public List<Map> getTreeNeLike(@Param(value = "displayName")String displayName,
			@Param(value = "userId") Integer userId, 
			@Param(value = "Define") Map Define);
	
	
	/**
	 * @@@分权分域到网元@@@
	 * 通过网管分组的Id取出该条网管分组的记录
	 * @param emsGroupId
	 * @return
	 */
	public Map getEMSGroupByEMSGroupId(@Param(value = "emsGroupId")int emsGroupId,
			@Param(value = "userId") Integer userId, 
			@Param(value = "Define") Map Define);
	
	/**
	 * @@@分权分域到网元@@@
	 * 通过网管的Id号取出该条网管的记录
	 * @param emsId
	 * @return
	 */
	public Map getEMSByEMSId(@Param(value = "emsId")int emsId,
			@Param(value = "userId") Integer userId, 
			@Param(value = "Define") Map Define);
	
	public List<Map<String, Object>> getEMSListByIds(@Param(value = "emsIds") List<Integer> emsIds,
			@Param(value = "userId") Integer userId, 
			@Param(value = "Define") Map Define);
	
	/**通过网管分组名查找网管分组
	 * @param displayName
	 * @return
	 */
	public Map getEMSGroupByDisplayName(@Param(value = "displayName")String displayName);
	
	/**新增一条网管分组记录
	 * @param displayName
	 */
	public void addEMSGroup(@Param(value = "displayName")String displayName);
	
	
	/**通过子网名称查询网管中的子网
	 * @param emsId
	 * @param displayName
	 * @return
	 */
	public Map getSubnetInEMSByName(@Param(value = "emsId")int emsId,@Param(value = "displayName")String displayName);
	
	
	/**通过子网名查询子网中的子网
	 * @param subnetId
	 * @param displayName
	 * @return
	 */
	public Map getSubnetInSubnetByName(@Param(value = "subnetId")int subnetId,@Param(value = "displayName")String displayName);
	
	
	/**向指定网管中新增一条子网记录
	 * @param emsId
	 * @param displayName
	 */
	public void addSubnetInEMS(@Param(value = "emsId")int emsId,@Param(value = "displayName")String displayName);
	
	
	
	/**向指定子网中新增一条子网记录
	 * @param subnetId
	 * @param displayName
	 */
	public void addSubnetInSubnet(@Param(value = "subnetId")int subnetId,@Param(value = "displayName")String displayName,
			@Param(value = "emsId")int emsId);
	
	
	/**修改EMS分组的名称
	 * @param emsGroupId
	 * @param displayName
	 */
	public void modifyEMSGroupName(@Param(value = "emsGroupId")int emsGroupId,@Param(value = "displayName")String displayName);
	
	
	
	/**修改子网名称
	 * @param subnetId
	 * @param displayName
	 */
	public void modifySubnetName(@Param(value = "subnetId")int subnetId,@Param(value = "displayName")String displayName);
	
	
	/**删除EMS分组
	 * @param emsGroupId
	 */
	public void dltEMSGroupById(@Param(value = "emsGroupId")int emsGroupId);
	
	/**删除子网
	 * @param subnetId
	 */
	public void dltSubnetById(@Param(value = "subnetId")int subnetId);
	
	/**
	 * 通过ptpIds获取PtpList
	 * @param ptpIds
	 * @return
	 */
	public List<Map<String, Object>> getPtpListByPtpIds(@Param(value = "ptpIds") List<Integer> ptpIds);
	
	public List<Map<String, Object>> getNeListByNeIds(@Param(value = "neIds") List<Integer> neIds);
	
	public List<Map<String, Object>> getAllNeInEMS(@Param(value = "nodeId")int nodeId,
			@Param(value = "userId") Integer userId, 
			@Param(value = "Define") Map Define);
	
	
	
	
}
