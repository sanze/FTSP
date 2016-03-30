package com.fujitsu.IService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;

public interface IImptProtectManagerService {
	/*-------------------------------------任务--------------------------------------------*/
	public boolean checkTaskNameExist(Map<String,Object> param) throws CommonException;
	public Integer editTask(Map<String,Object> param) throws CommonException;
	public void changeTaskStatus(Map<String,Object> param) throws CommonException;
	public Map<String,Object> getTaskList(Map<String,Object> param,int userId,int start,int limit) throws CommonException;
	public Map<String,Object> getTask(Map<String, Object> param, int userId) throws CommonException;
	public Map<String,Object> getTaskInfo(Integer taskId, boolean hasName) throws CommonException;
	/*_____________________________________任务____________________________________________*/
	
	
	/*-------------------------------------监测--------------------------------------------*/
	/**
	 * 获取任务下的对象（转换为网元）
	 * @param taskId
	 * @return
	 * @throws CommonException
	 */
	public List<Map> getTaskTargetNe(Integer taskId) throws CommonException;
	
	/**
	 * 查询当前性能到临时表，按PTP
	 * @param ptpList
	 * @param paramMap
	 * @param sysUserId
	 * @return
	 */
	public Map searchCurrentDataIntoTempTableByPtp(List<Integer> ptpList,
			Map<String, String> paramMap, Integer sysUserId) throws CommonException;
	/**
	 * 为每个node添加emsId属性
	 * @param nodeList
	 * @return
	 * @throws CommonException
	 */
	public List<Map> plusEmsIdToNodeList(List<Map> nodeList)throws CommonException;
	
	
	public List<Map<String, Object>> getPmExceedData(
			Map<String, Object> paramMap, int start, int limit) throws CommonException;
	
	/**
	 * 为设备任务拓扑图加载写的方法
	 * @param neIdList
	 * @param linkList
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getTopoDataEquip(List<Map> neList,
			List<Map> linkList) throws CommonException;
	
	/**
	 * 端口对应的板卡-电路任务
	 * @param list
	 * @return
	 */
	public List<Integer> getUnitListByPtpList(List<Integer> list)throws CommonException;
	/**
	 * 端口的一系列信息-电路任务（neid，unitid，ptpid）
	 * @param list
	 * @return
	 */
	public List<Map> getPtpInfo(List<Integer> list)throws CommonException;
	/**
	 * 获取APA坐标
	 * @param param
	 * @return
	 */
	public Map getAPAPosition(Map<String, Object> param)throws CommonException;
	/**
	 * 保存APA坐标
	 * @param param
	 * @throws CommonException
	 */
	public void saveAPAPosition(HashMap<String, Object> param)throws CommonException;
	/*_____________________________________监测____________________________________________*/
	

}
