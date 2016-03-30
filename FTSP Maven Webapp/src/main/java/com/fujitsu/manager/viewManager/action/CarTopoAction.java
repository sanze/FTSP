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

public class CarTopoAction extends AbstractAction {

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
					nodeId, nodeType,direction,sysUserId,privilege,false);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
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
