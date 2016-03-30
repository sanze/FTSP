package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;
import com.fujitsu.model.LinkAlarmModel;
import com.fujitsu.model.TopoLineModel;

public interface ITopoManagerService{

	public Map<String, Object> getNode(int nodeId,int nodeType,String direction,int userId,String privilege,boolean needAlarmInfo) throws CommonException;
	
	/**
	 * @@@分权分域到网元@@@
	 * @param positionArray
	 * @throws CommonException
	 */
	public void savePosition(List<String> positionArray) throws CommonException;
	
	/**
	 * @@@分权分域到网元@@@
	 * @param nodeId
	 * @param nodeType
	 * @param userId
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getTreeNode(int nodeId,int nodeType,int userId) throws CommonException;
	
	/**
	 * @@@分权分域到网元@@@
	 * @param displayName
	 * @param userId
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getTreeNeLike(String displayName,int userId) throws CommonException;
	
	/**
	 * @@@分权分域到网元@@@
	 * @param nodeType
	 * @param nodeId
	 * @param displayName
	 * @param userId
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> addNodeTree(int nodeType,int nodeId,String displayName,Integer userId) throws CommonException;

	/**
	 * @@@分权分域到网元@@@
	 * @param nodeType
	 * @param nodeId
	 * @param displayName
	 * @param userId
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> modifyNodeNameTree(int nodeType,int nodeId,String displayName,Integer userId) throws CommonException;

	/**
	 * @@@分权分域到网元@@@
	 * 删除拓扑树节点
	 * @param nodeType
	 * @param nodeId
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> dltNodeTree(int nodeType,int nodeId,Integer userId) throws CommonException;
	
	/**
	 * 获取网元属性
	 * @param nodeId
	 * @return
	 */
	public Map<String, Object> getNeAttributes(int nodeId) throws CommonException;
	
	/**
	 * @@@分权分域到网元@@@
	 * 刷新对象树
	 * @param expandedNodeArray
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> refreshTree(List<String> expandedNodeArray, int userId) throws CommonException;
	
	
	/**
	 * 获取系统使用的告警颜色
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getAlarmColorSet() throws CommonException;
	
	/**将重复的link进行整合
	 * @param lineList
	 * @return
	 */
	public List<TopoLineModel> deleteRepeatLink(List<TopoLineModel> lineList) throws CommonException;
	
	/**
	 * 从map转换为model（路由图用）
	 * @param map
	 * @return
	 * @throws CommonException
	 */
	public LinkAlarmModel transMap2LinkAlarmModel(Map<String,Object> map) throws CommonException;
}
