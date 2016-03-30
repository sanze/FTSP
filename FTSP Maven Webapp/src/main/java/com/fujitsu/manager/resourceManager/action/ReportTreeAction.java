package com.fujitsu.manager.resourceManager.action;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.fujitsu.IService.IMethodLog;
import com.fujitsu.IService.IReportTreeService;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.manager.resourceManager.model.ReportTreeModel;
import com.opensymphony.xwork2.ModelDriven;
public class ReportTreeAction extends AbstractAction implements ModelDriven<ReportTreeModel>{
	private static final long serialVersionUID = -4234556718228213653L;
	ReportTreeModel reportTreeModel=new ReportTreeModel();
	/**
	 * 业务层对象
	 */
	@Resource
	public IReportTreeService iReportTreeService;
	
	@Override
	public ReportTreeModel getModel() {
		return reportTreeModel;
	}

	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "共通树:获取所有子节点")
	public String getChildNodes() {
		String returnString = RESULT_OBJ;
		if(sysUserId!=null){
			reportTreeModel.setUserId(sysUserId);
		}
		try {
			List<Map> nodes = iReportTreeService.treeGetChildNodes(reportTreeModel);
			resultArray = JSONArray.fromObject(nodes);
			returnString = RESULT_ARRAY;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}
}
