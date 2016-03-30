package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface LinkEvalTopoMapper {
	
	/**
	 * 按网络级别获取传输系统信息
	 * @param netLevel
	 * @return
	 */
	public List<Map<String, Object>> getTransmissionSystemByNetlevel(
			@Param(value = "netLevel") int netLevel);

	public List<Map<String, Object>> getTansSysNeById(
			@Param(value = "sysIdList") int[] sysIdList);
	
	/**
	 * 获取指定的传输系统内的网元节点数据
	 * @param sysId
	 * @return List<Map<String, Object>>
	 *         Key: NE_ID, STATION_NAME, NE_NAME, NE_TYPE, POSITION_X, POSITION_Y
	 */
	public List<Map<String, Object>> getTransSysNeBySysId(
			@Param(value = "sysId") int sysId);
	
	/**
	 * 获取指定传输系统内的Link信息
	 * @param sysId
	 * @return List<Map<String, Object>>
	 * 	       Key: LINK_ID, FROM_NODE, FROM_NODE_TYPE, TO_NODE, TO_NODE_TYPE, LINE_TYPE
	 */
	public List<Map<String, Object>> getTransSysLinkBySysId(
			@Param(value = "sysId") int sysId);
	
	/**保存网元坐标
	 * @param neId
	 * @param position_X
	 * @param position_Y
	 */
	public void saveNePosition
			(@Param(value = "neId")int neId,
			 @Param(value = "position_X")int position_X,
			 @Param(value = "position_Y")int position_Y);
}
