package com.fujitsu.manager.commonManager.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.fujitsu.IService.ICommonManagerService;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;

public class MenuAction extends AbstractAction{

	@Resource
	public ICommonManagerService commonManagerService;
	
	private Integer parentMenuId;
//	private String userId;
	private List<Integer> menuIds;
	
	public String getMenuList() throws Exception {
		String returnString = RESULT_OBJ;
		try {
			List<Map> nodes = commonManagerService.getMenuList(sysUserId,menuIds);
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
	
	/**
	 * @return
	 * @throws Exception
	 */
	public String getSubMenuList() throws Exception {
		boolean needAuthCheck = true;
		//管理员用户
		if(sysUserId!=null&&sysUserId.intValue() == CommonDefine.USER_ADMIN_ID){
			needAuthCheck = false;
		}

	    List<Map> data = new ArrayList<Map>();
	    
	    if(needAuthCheck){
		if (sysUserId != null) {
			data = commonManagerService.getSubMenuList(sysUserId,
						Integer.valueOf(parentMenuId),needAuthCheck);
		}
	    }else{
	    	data = commonManagerService.getSubMenuList(sysUserId,
					Integer.valueOf(parentMenuId),needAuthCheck);
	    }

		resultArray = JSONArray.fromObject(data);

		return RESULT_ARRAY;
	}

	public Integer getParentMenuId() {
		return parentMenuId;
	}

	public void setParentMenuId(Integer parentMenuId) {
		this.parentMenuId = parentMenuId;
	}

	public List<Integer> getMenuIds() {
		return menuIds;
	}

	public void setMenuIds(List<Integer> menuIds) {
		this.menuIds = menuIds;
	}

}
