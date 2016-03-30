package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;

public interface IResourceStatisticManagerService { 
	
	/**获取网元列表信息
	 * @param cableFiberModel
	 * @return
	 * @throws CommonException
	 */
	public List<Map>  getStatistic(Map <String,Object> map,String type) 
			throws CommonException;

	public Map<String,Object>  getStatisticGrid(List<Map<String,Object>> data,
			String type,int start,int limit) throws CommonException;
	
}
