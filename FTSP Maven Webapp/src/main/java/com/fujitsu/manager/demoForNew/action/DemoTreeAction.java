package com.fujitsu.manager.demoForNew.action;

import java.util.List;

import javax.annotation.Resource;

import com.fujitsu.IService.IDemoTreeService;
import com.fujitsu.IService.IMethodLog;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.manager.demoForNew.model.TreeModel;
import com.fujitsu.util.JsonUtil;
import com.opensymphony.xwork2.ModelDriven;

public class DemoTreeAction extends AbstractAction implements ModelDriven<TreeModel>{

	private static final long serialVersionUID = 1797873533671620254L;
	@Resource
	public IDemoTreeService demoTreeService;
	public TreeModel treeModel = new TreeModel();
	@Override
	public TreeModel getModel() {
		  return treeModel;
	}
	/**
	 * Method name: getAllEmsGroups <BR>
	 * Description: 查询demo_test分页元素<BR>
	 * Remark: 2013-12-15<BR>
	 * @author hg
	 * @return String<BR>
	 */
	@IMethodLog(desc = "查询demo_tree元素数据")
	public String getLowerTreeNOdes(){
//		String[] node = request.getParameterValues("node");
		System.out.println("node = 555555f---------------------");
		System.out.println("node = "+ treeModel.getId());
		// 查询所有网管分组
		List<TreeModel> treeList= demoTreeService.getLowerTreeNOdes(treeModel.getId());
		// 将返回的结果转成JSON对象，返回前台
		resultArray = JsonUtil.getJson4JavaList(treeList);
		return RESULT_ARRAY;
	}
	
}
