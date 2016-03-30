package com.fujitsu.manager.faultManager.action;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.IService.IColorManagement;
import com.fujitsu.IService.IAlarmManagementService;
import com.fujitsu.IService.IMethodLog;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.model.AlarmHabitModel;
import com.opensymphony.xwork2.ModelDriven;
public class AlarmHabitSetManageAction extends AbstractAction implements ModelDriven<AlarmHabitModel>{
	private static final long serialVersionUID = -218037914366763644L;
	private String jsonString;
	@Resource
	public IColorManagement iColorManagement;
	@Resource
	public IAlarmManagementService alarmManagementService;
	public AlarmHabitModel alarmHabitModel = new AlarmHabitModel();
	@Override
	public AlarmHabitModel getModel(){
		  return alarmHabitModel;
	}
	
	/**
	 * Method name: setAlarmColorConfig <BR>
	 * Description: 设置各个告警等级的配置信息<BR>
	 * Remark: 2014-01-15<BR>
	 * @author wuchao
	 * @return String<BR>
	 */
	@IMethodLog(desc = "设置各个告警级别的配置信息", type = IMethodLog.InfoType.MOD)
	public String setAlarmColorConfig(){
		Map result = new HashMap();
		List<Map> datas=ListStringtoListMap(alarmHabitModel.getDatas());
		if(datas!=null && datas.size()==6){
			try {
				alarmManagementService.modifyAlarmConfirmShift(datas.get(5));
				datas.remove(5);
				boolean isSet=iColorManagement.setAlarmColorConfig(datas);
				if(isSet){
					result.put("success", true);
					result.put("msg", "设置成功！");
				}else{
					result.put("success",false);
					result.put("msg", "设置失败！");
				}
			} catch (CommonException e) {
				result.put("success",false);
				result.put("msg", "设置失败！");
				e.printStackTrace();
			}
		}
		resultObj = JSONObject.fromObject(result);
		return RESULT_OBJ;
	}
	
	
	/**
	 * Method name: getAlarmColorInit <BR>
	 * Description: <BR>
	 */
	@SuppressWarnings("unchecked")
	@IMethodLog(desc = "获取告警颜色")
	public String getAlarmColorInit(){
		try {
			// 将参数专程JSON对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 将JSON对象转成Map对象
			Map paramMap = new HashMap();
			paramMap = (Map) jsonObject;
			// 定义一个Map接受查询返回的值
			List<Map> alarmColor = iColorManagement.getAlarmColorConfig(paramMap);
			System.out.println(alarmColor);
			// 将返回的结果转成JSON对象，返回前台
			Map returnMap=new HashMap();
			returnMap.put("result", alarmColor);
			returnMap.put("returnResult",CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(returnMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}
	
}
