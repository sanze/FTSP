package com.fujitsu.manager.demoForNew.action;

import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.IService.IDemoService;
import com.fujitsu.IService.IMethodLog;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.manager.demoForNew.model.DemoTest;
import com.opensymphony.xwork2.ModelDriven;

public class DemoAction extends AbstractAction implements ModelDriven<DemoTest>{

	private static final long serialVersionUID = -218037914366763644L;
	@Resource
	public IDemoService demoService;
	public DemoTest demoTest = new DemoTest();
	@Override
	public DemoTest getModel() {
		  return demoTest;
	}
	/**
	 * Method name: getAllEmsGroups <BR>
	 * Description: 查询demo_test表分页元素<BR>
	 * Remark: 2013-12-15<BR>
	 * @author hg
	 * @return String<BR>
	 */
	@IMethodLog(desc = "查询demo_test分页元素")
	public String getAllDemoData(){
		try {
			// 查询所有网管分组
			Map<String, Object> emsGroupMap = demoService.getAllDemoData(start,limit);
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(emsGroupMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: saveDemoTest <BR>
	 * Description: 提交表单<BR>
	 * Remark: 2013-12-15<BR>
	 * @author hg
	 * @return String<BR>
	 */
	@IMethodLog(desc = "提交表单demo_test表保存", type = IMethodLog.InfoType.MOD)
	public String saveDemoTest(){
		
		Map<String, Object> map = demoService.saveDemoTest(demoTest);
		// 将返回的结果转成JSON对象，返回前台
		resultObj = JSONObject.fromObject(map);
		return RESULT_OBJ;
	}
	
	
	/**
	 * Method name: deleteDemoTest <BR>
	 * Description: 删除demo_test一行记录<BR>
	 * Remark: 2013-12-15<BR>
	 * @author hg
	 * @return String<BR>
	 */
	@IMethodLog(desc = "删除demo_test一行记录", type = IMethodLog.InfoType.DELETE)
	public String deleteDemoTest(){
		System.out.println("demoTest id = "+demoTest.getId());
		Map<String, Object> map = demoService.deleteDemoTest(demoTest);
		// 将返回的结果转成JSON对象，返回前台
		resultObj = JSONObject.fromObject(map);
		return RESULT_OBJ;
	}
}
