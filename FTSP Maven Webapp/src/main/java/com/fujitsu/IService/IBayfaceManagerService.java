package com.fujitsu.IService;

import java.util.Map;

import com.fujitsu.common.CommonException;

public interface IBayfaceManagerService{

	/**
	 * @param 板卡Id 
	 * @return 板卡详细信息
	 */
	public Map<String,Object> getUnitAttribute (String unitId) throws CommonException;
	
	/**
	 * @param 网元Id 
	 * @return 板卡详细信息
	 */
	public Map<String,Object> getBayfaceDataFromNE (String neId, String speShelfNo) throws CommonException;
//	根据端口取端口ID和Domain
	/**
	 * @param map
	 * @return 端口详细信息
	 */
	public Map<String,Object> getPortDomain (Map <String,String> map) throws CommonException; 
	/**
	 * @param map
	 * @return 端口详细信息
	 */
	public Map<String,Object> getBayfaceUintId (Map <String,String> map) throws CommonException;	
	/**
	 * @param map
	 * @return 网元详细信息
	 */
	public Map<String,Object> getNeRelate (String neId) throws CommonException;	  
}
