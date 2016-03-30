package com.fujitsu.dao.mysql;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface WSManagerMapper {
	/**
	 * 获取网管列表
	 * 
	 * @return Map = {BASE_EMS_CONNECTION_ID = 1，DISPLAY_NAME = 我是网管}
	 */
	public List<HashMap> getEmsList();

	/**
	 * 获取网元列表
	 * 
	 * @param emsId
	 *            网管id
	 * @param neName
	 *            网元名，用于模糊匹配
	 * @return Map = {BASE_NE_ID = 1，DISPLAY_NAME = 我是网元}
	 */
	public List<HashMap> getNeList(@Param(value="emsId")Integer emsId, @Param(value="neName")String neName);

	/**
	 * 获取板卡列表
	 * 
	 * @param neId
	 *            网元id
	 * @param unitName
	 *            板卡名，用于模糊匹配
	 * @return Map = {BASE_UNIT_ID = 1，DISPLAY_NAME = 我是板卡}
	 */
	public List<HashMap> getUnitList(@Param(value="neId")Integer neId, @Param("unitName")String unitName);

	/**
	 * 获取ptp列表
	 * 
	 * @param UnitId
	 *            板卡Id
	 * @return Map = {
	 * 	BASE_EMS_CONNECTION_ID = 1，
	 * 	NE_DISPLAY_NAME = 贝森，
	 * 	NE_NAME = 3145728， 
	 * 	DISPLAY_NAME = 1-156-贝森汇聚环五-1(52ND2)-1(IN1/OUT1)，
	 * IDENTIFY = RACK_NO/SHELF_NO/SLOT_NO/PORT_NO/DOMAIN}
	 * 备注：IDENTIFY真实数据样例：1/1/1/1/1
	 */
	public List<HashMap> getPtpList(@Param("unitId")Integer unitId);
}
