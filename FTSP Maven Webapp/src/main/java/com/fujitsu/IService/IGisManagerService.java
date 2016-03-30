package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;


public interface IGisManagerService  {
	/**
	 * 获取gis数据
	 * @param map
	 * @return
	 */
	public List<Map<String,Object>> getGisResourceData(Map<String, Object> map);
	
	
	/**
	 * 获取测试路由数据
	 * @param map
	 * @return
	 */
	public List<Map<String,Object>> getTestRoutesByAZ(Map<String, Object> map);


	/**
	 * 资源显示方式
	 * @param map
	 * @return
	 * @throws CommonException 
	 */
	public Map<String, Object> displayDataByStrategy(Map<String, Object> map) throws CommonException;

	
	/**
	 * 获取当前区域下的传输系统
	 * @param map
	 * @return
	 */
	public Map<String, Object> getTransSystems(Map<String, Object> map);

	/**
	 * 获取不在光缆段上的机房
	 * @param map
	 * @return
	 */
	public List<Map<String, Object>> getStationsNotInCable(Map<String, Object> map);

	/**
	 * 对指定的测试路由执行光缆测试
	 * @param map
	 * @return
	 */
	public CommonResult doTest(Map<String, Object> map);

	/**
	 * 获取指定测试路由的测试参数
	 * @param map
	 * @return
	 */
	public Map<String, Object> getTestParamById(Map<String, Object> map);
	
	/**
	 * 获取指定测试路由的设备波长信息
	 * @param map
	 * @return
	 * @throws CommonException
	 */
	public List<Map> getWaveLengthList(Map map) throws CommonException;
	
	/**
	 * 获取指定测试路由的设备量程信息
	 * @param map
	 * @return
	 * @throws CommonException
	 */
	public List<Map> getRangeList(Map map) throws CommonException;
	
	/**
	 * 获取指定测试路由测测量脉宽信息
	 * @param map
	 * @return
	 * @throws CommonException
	 */
	public List<Map> getPluseWidthList(Map map) throws CommonException;
}
