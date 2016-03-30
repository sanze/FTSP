package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;

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
	 */
	public Map<String, Object> displayDataByStrategy(Map<String, Object> map);	
}
