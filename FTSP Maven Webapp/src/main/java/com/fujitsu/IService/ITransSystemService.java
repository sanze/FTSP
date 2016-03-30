package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;

public interface ITransSystemService {
//	public Map<String,Object> getAllTransmissionSystem(Map<String,Object> param)throws CommonException;

	public Map<String, Object> getAllLink(Map<String, Object> param) throws CommonException;

	/**
	 * 查询传输系统
	 * @param paramMap
	 * @param sysUserId
	 * @param start
	 * @param limit
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> queryTransmissionSystem(Map<String, Object> paramMap, Integer sysUserId, int start, int limit) throws CommonException;
//----------------------------333333333-------------------------------
	
	/**
	 * 获取网元信息和所属区域信息
	 * @param nodeList [nodeId，nodeLevel]
	 * @param sysUserId 
	 * @return [total,rows]
	 */
	@SuppressWarnings("rawtypes")
	Map<String, Object> getNeInfoWithArea(List<Map> nodeList, Integer sysUserId,List<Integer> idList) throws CommonException;

	/**
	 * 获得网元范围内的LINK
	 * @param intList
	 * @param paramMap 
	 * @return
	 * @throws CommonException
	 */
	Map<String, Object> getLinkBetweenNe(List<Integer> intList, Map<String, String> paramMap) throws CommonException;

	
	
	/**
	 * 删除传输系统
	 * @param paramMap
	 * @return
	 * @throws CommonException
	 */
	public CommonResult deleteTransmissionSystem(Map<String, String> paramMap) throws CommonException;

	/**
	 * 新建传输系统-主
	 * @param paramMap
	 * @return
	 * @throws CommonException
	 */
	public CommonResult newTransSystem(Map<String, String> paramMap,List<Integer> intList) throws CommonException;

	/**
	 * 保存传输系统-网元
	 * @param intList
	 * @param paramMap
	 * @return
	 * @throws CommonException
	 */
	public CommonResult saveTransSystemNe(List<Integer> intList,
			Map<String, String> paramMap) throws CommonException;

	/**
	 *  保存传输系统-Link
	 * @param intList
	 * @param paramMap
	 * @return
	 * @throws CommonException
	 */
	public CommonResult saveTransSystemLink(Map<String, String> paramMap) throws CommonException;

	/**
	 * 获取一个传输系统的信息
	 * @param paramMap
	 * @return
	 */
	public Map<String, Object> getTransSystem(Map<String, String> paramMap) throws CommonException;

	/**
	 * 获取一个传输系统中的Link
	 * @param paramMap
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getTransSysLink(Map<String, String> paramMap) throws CommonException;

	/**
	 * 修改传输系统
	 * @param paramMap
	 * @return
	 */
	public CommonResult updateTransSystem(Map<String, String> paramMap)  throws CommonException;

	/**
	 * 检查网元能否删除
	 * @param intList
	 * @param paramMap
	 * @return
	 * @throws CommonException
	 */
	public CommonResult checkIfNeDeletable(List<Integer> intList,
			Map<String, String> paramMap) throws CommonException;

	/**
	 * 自动发现传输系统
	 * @param nodeList
	 * @return
	 * @throws CommonException
	 */
	public CommonResult autoFindSystem(List<Map> nodeList) throws CommonException;
}
