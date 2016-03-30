package com.fujitsu.manager.viewManager.action;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.IService.IMethodLog;
import com.fujitsu.IService.ITopoManagerService;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;

public class TopoAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -999327094258526189L;
	
	@Resource
	public ITopoManagerService topoManagerService;
	private int nodeId;
	private int nodeType;
	private String direction;
	private String displayName;
	private List<String> positionArray;
	private List<String> expandedNodeArray;
	private String privilege;

	/**
	 * 查询拓扑图节点信息
	 * 
	 * @return
	 */
	@IMethodLog(desc = "查询拓扑图节点信息")
	public String getNode()
	{
		try {
			Map<String, Object> data = topoManagerService.getNode(
					nodeId, nodeType,direction,sysUserId,privilege,true);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	/**
	 * 查询拓扑树信息
	 * @@@分权分域到网元@@@
	 * @return
	 */
	@IMethodLog(desc = "查询拓扑树信息")
	public String getTreeNode()
	{
		try {
			Map<String, Object> data = topoManagerService.getTreeNode(
					nodeId, nodeType,sysUserId);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	/**
	 * 查询网元属性
	 * 
	 * @return
	 */
	@IMethodLog(desc = "查询网元属性")
	public String getNeAttributes()
	{
		System.out.println("nodeId: "  + nodeId);
		
		try {
			
			Map<String, Object> data = topoManagerService.getNeAttributes(nodeId);
			resultObj = JSONObject.fromObject(data);
			
		} catch (CommonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return RESULT_OBJ;
	}
	
	/**
	 * 模糊查询拓扑树网元信息
	 * @@@分权分域到网元@@@
	 * @return
	 */
	@IMethodLog(desc = "模糊查询拓扑树网元信息")
	public String getTreeNeLike()
	{
		try {
			Map<String, Object> data = topoManagerService.getTreeNeLike(displayName,sysUserId);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	/**
	 * 向拓扑树增加新的节点
	 * @@@分权分域到网元@@@
	 * @return
	 */
	@IMethodLog(desc = "向拓扑树增加新的节点", type = IMethodLog.InfoType.MOD)
	public String addNodeTree()
	{
		System.out.println("displayName: "  + displayName);

		try {
			Map<String, Object> data = topoManagerService.addNodeTree(nodeType,nodeId,displayName,sysUserId);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	
	/**
	 * 向拓扑树增加新的节点
	 * @@@分权分域到网元@@@
	 * @return
	 */
	@IMethodLog(desc = "向拓扑树增加新的节点", type = IMethodLog.InfoType.MOD)
	public String modifyNodeNameTree()
	{
		System.out.println("newName: "  + displayName);

		try {
			Map<String, Object> data = topoManagerService.modifyNodeNameTree(nodeType,nodeId,displayName,sysUserId);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	/**
	 * 删除拓扑树的节点
	 * @@@分权分域到网元@@@
	 * @return
	 */
	@IMethodLog(desc = "删除拓扑树的节点", type = IMethodLog.InfoType.DELETE)
	public String dltNodeTree()
	{
		System.out.println("nodeId: "  + nodeId);
		System.out.println("nodeType: " + nodeType);

		try {
			Map<String, Object> data = topoManagerService.dltNodeTree(nodeType,nodeId,sysUserId);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}

	/**
	 * 保存节点坐标
	 * @@@分权分域到网元@@@
	 * @return
	 */
	@IMethodLog(desc = "保存节点坐标", type = IMethodLog.InfoType.MOD)
	public String savePosition() {
		
		result.setReturnResult(CommonDefine.SUCCESS);
		result.setReturnMessage("保存布局成功！");
		try {
			topoManagerService.savePosition(this.positionArray);
		} catch (CommonException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		resultObj = JSONObject.fromObject(result);
		
		return RESULT_OBJ;
	}
	
	/**
	 * 刷新对象树
	 * @@@分权分域到网元@@@
	 * @return
	 */
	@IMethodLog(desc = "刷新对象树")
	public String refreshTree() {
		
//		result.setReturnResult(CommonDefine.SUCCESS);
		
		try {
			Map<String, Object> data = topoManagerService.refreshTree(this.expandedNodeArray,sysUserId);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	public List<String> getExpandedNodeArray() {
		return expandedNodeArray;
	}

	public void setExpandedNodeArray(List<String> expandedNodeArray) {
		this.expandedNodeArray = expandedNodeArray;
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}
	
	public int getNodeId() {
		return nodeId;
	}


	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}


	public int getNodeType() {
		return nodeType;
	}


	public void setNodeType(int nodeType) {
		this.nodeType = nodeType;
	}
	
	public List<String> getPositionArray() {
		return positionArray;
	}

	public void setPositionArray(List<String> positionArray) {
		this.positionArray = positionArray;
	}
	
	public String getPrivilege() {
		return privilege;
	}

	public void setPrivilege(String privilege) {
		this.privilege = privilege;
	}

}
