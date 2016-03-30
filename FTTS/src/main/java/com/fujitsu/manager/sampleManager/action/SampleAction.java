package com.fujitsu.manager.sampleManager.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.IService.IMethodLog;
import com.fujitsu.IService.ISampleManagerService;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;

public class SampleAction extends AbstractAction{

	@Resource
	public ISampleManagerService sampleManagerService;

	@IMethodLog(desc = "获取示例数据")
	public String getSampleData(){
		try {
			// 查询所有网管分组
			List<Map> dataList = sampleManagerService.getSampleData("t_sys_user");
			// 将返回的结果转成JSON对象，返回前台
			Map map = new HashMap();
			map.put("rows", dataList);
			map.put("total", dataList.size());
			resultObj = JSONObject.fromObject(map);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
}
