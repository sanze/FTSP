package com.fujitsu.manager.commonManager.action;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.fujitsu.IService.ICommonManagerService;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;

/**
 * @author ZJL 共通树web接口
 */
public class TreeAction extends AbstractAction {

	private static final long serialVersionUID = -2237168811089960262L;

	int nodeId;
	int nodeLevel;
	int endId;
	int endLevel;
	String text;
	boolean hasPath;
	/**
	 * 业务层对象
	 */
	@Resource
	public ICommonManagerService commonManagerService;

	@SuppressWarnings("unchecked")
//	@IMethodLog(desc = "共通树:获取所有子节点")
	public String getChildNodes() {
		String returnString = RESULT_OBJ;
		try {
			if (endLevel > 0) {
				List<Map> nodes = commonManagerService.treeGetChildNodes(nodeId,
						nodeLevel, endLevel,sysUserId);
				resultArray = JSONArray.fromObject(nodes);
			}else{
				resultArray=new JSONArray();
			}
			returnString = RESULT_ARRAY;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}

		return returnString;
	}

	@SuppressWarnings("unchecked")
//	@IMethodLog(desc = "共通树:获取单节点")
	public String getNode() {
		String returnString = RESULT_OBJ;
		try {
			Map node = commonManagerService.treeGetNode(nodeId, nodeLevel, sysUserId);
			resultObj = JSONObject.fromObject(node);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		} catch (Exception e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(MessageCodeDefine.COM_EXCPT_UNKNOW));
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}

		return returnString;
	}

//	@IMethodLog(desc = "共通树:搜索节点列表")
	public String searchNodes() {
		String returnString = RESULT_OBJ;
		try {
			Map dataModel = commonManagerService.treeGetNodesByKey(text,
					nodeId, nodeLevel, endId, endLevel, hasPath, start,
					limit, sysUserId);
			resultObj = JSONObject.fromObject(dataModel);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}

		return returnString;
	}

	public int getNodeId() {
		return nodeId;
	}

	public int getNodeLevel() {
		return nodeLevel;
	}

	public int getEndId() {
		return endId;
	}

	public int getEndLevel() {
		return endLevel;
	}

	public String getText() {
		return text;
	}

	public boolean isHasPath() {
		return hasPath;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public void setNodeLevel(int nodeLevel) {
		this.nodeLevel = nodeLevel;
	}

	public void setEndId(int endId) {
		this.endId = endId;
	}

	public void setEndLevel(int endLevel) {
		this.endLevel = endLevel;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setHasPath(boolean hasPath) {
		this.hasPath = hasPath;
	}

}
