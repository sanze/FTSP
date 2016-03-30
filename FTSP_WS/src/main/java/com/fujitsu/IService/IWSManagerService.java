package com.fujitsu.IService;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;


/**
 * 
 * 接口名再考虑
 * @author xuxiaojun
 *
 */
@WebService
public interface IWSManagerService {

	/**
	 * 获取网管列表
	 * 
	 * @return Map = {BASE_EMS_CONNECTION_ID = 1，DISPLAY_NAME = 我是网管}
	 */
	@WebMethod 
	@WebResult
	public String getEmsList();

	/**
	 * 获取网元列表
	 * 
	 * @param emsId
	 *            网管id
	 * @param neName
	 *            网元名，用于模糊匹配
	 * @return Map = {BASE_NE_ID = 1，DISPLAY_NAME = 我是网元}
	 */
	@WebMethod 
	@WebResult
	public String getNeList(Integer emsId, String neName);

	/**
	 * 获取板卡列表
	 * 
	 * @param neId
	 *            网元id
	 * @param unitName
	 *            板卡名，用于模糊匹配
	 * @return Map = {BASE_UNIT_ID = 1，DISPLAY_NAME = 我是板卡}
	 */
	@WebMethod 
	@WebResult
	public String getUnitList(Integer neId, String unitName);

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
	@WebMethod 
	@WebResult
	public String getPtpList(Integer UnitId);

}
