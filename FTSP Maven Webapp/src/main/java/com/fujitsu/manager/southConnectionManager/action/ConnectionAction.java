package com.fujitsu.manager.southConnectionManager.action;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

import com.fujitsu.IService.IMethodLog;
import com.fujitsu.IService.ISouthConnectionService;
import com.fujitsu.abstractAction.DownloadAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.handler.MessageHandler;
import com.fujitsu.manager.resourceStockManager.service.ResourceStockService;
import com.fujitsu.model.EmsConnectionModel;
import com.fujitsu.model.EmsGroupModel;
import com.fujitsu.model.NeModel;
import com.fujitsu.model.SdhCrsModel;
import com.fujitsu.model.SubnetModel;
import com.fujitsu.model.SysServiceModel;

public class ConnectionAction extends DownloadAction {

	@Resource
	public ISouthConnectionService southConnectionService;
	@Resource
	public ResourceStockService resourceStockService;

	EmsConnectionModel emsConnectionModel = new EmsConnectionModel();
	EmsGroupModel emsGroupModel = new EmsGroupModel();
	SysServiceModel sysServiceModel = new SysServiceModel();
	NeModel neModel = new NeModel();
	SdhCrsModel sdhCrsModel = new SdhCrsModel();
	SubnetModel subnetModel = new SubnetModel();


	private Integer emsConnectionId;
	private Integer emsGroupId;
	private Integer sysSvcRecordId;
	private Integer subnetId;
	private Integer taskId;
	private Integer minutes;
	private Integer saveValue;
	private Integer periodType;
	private Integer syncMode;

	private List<String> jsonString;
	protected String jString;
	private String period;

	/**
	 * 南向连接信息列表
	 * 
	 */
	@IMethodLog(desc = "数据采集管理:南向连接管理中根据网管分组Id查询连接列表")
	public String getConnectionListByGroupId() {
		
		String returnString = RESULT_OBJ;
		try {
			Map connectionList = southConnectionService
					.getConnectionListByGroupId(sysUserId,CommonDefine.FALSE,start, limit, emsGroupId);
			resultObj = JSONObject.fromObject(connectionList);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	/**
	 * 
	 * @return
	 */
	@IMethodLog(desc = "数据采集管理:南向连接管理中根据网管Id查询连接列表")
	public String getConnectionByEmsConnectionId() {
		String returnString = RESULT_OBJ;
		try {
			Map map = southConnectionService
					.getConnectionByEmsConnectionId(emsConnectionModel);
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(map);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	/**
	 * 获取所有网管分组
	 * 
	 */
	@IMethodLog(desc = "数据采集管理:南向连接管理中获取所有网管分组")
	public String getConnectGroup() {
		String returnString = RESULT_OBJ;
		try {
			Map map = southConnectionService.getConnectGroup(emsGroupId);
			result.setReturnResult(CommonDefine.SUCCESS);
			// result.setReturnMessage(getText(MessageCodeDefine.CORBA_CONNECTION_ADD_SUCCESS));
			resultObj = JSONObject.fromObject(map);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}
	
	/**
	 * 获取所有网管分组
	 * 
	 */
	@IMethodLog(desc = "数据采集管理:南向连接管理中获取所有网管分组")
	public String getEmsConnectGroup() {
		String returnString = RESULT_OBJ;
		try {
			Map map = southConnectionService.getEmsConnectionGroup(emsGroupId);
			result.setReturnResult(CommonDefine.SUCCESS);
			// result.setReturnMessage(getText(MessageCodeDefine.CORBA_CONNECTION_ADD_SUCCESS));
			resultObj = JSONObject.fromObject(map);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	/**
	 * 检查同一网管分组下，网管名称是否重复
	 * 
	 * @return
	 * @throws ParseException
	 */
	@IMethodLog(desc = "数据采集管理:南向连接管理页面新增修改连接时 网管名称检查", type = IMethodLog.InfoType.MOD)
	public String checkConnectionNameExist() {
		String returnString = RESULT_OBJ;
		try {
			Boolean data = southConnectionService
					.checkConnectionNameExist(emsConnectionModel);
			if (data)
				result.setReturnResult(CommonDefine.FAILED);
			else
				result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	/**
	 * 新增修改连接时检查 网管IP地址是否重复
	 * 
	 * @return
	 * @throws ParseException
	 */
	@IMethodLog(desc = "数据采集管理:南向连接管理页面新增修改连接时 网管IP地址检查", type = IMethodLog.InfoType.MOD)
	public String checkIpAddressExist() {
		String returnString = RESULT_OBJ;
		try {
			Boolean data = southConnectionService
					.checkIpAddressExist(emsConnectionModel);
			if (data)
				result.setReturnResult(CommonDefine.FAILED);
			else
				result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	/**
	 * 
	 * @return
	 */
	@IMethodLog(desc = "数据采集管理:南向连接管理中获取所有接入服务器")
	public String getConnectService() {
		String returnString = RESULT_OBJ;
		try {
			Map result = southConnectionService.getConnectService();
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	/**
	 * 启动连接
	 * 
	 * @return
	 */
	@IMethodLog(desc = "数据采集管理:南向连接管理中启动连接")
	public String startConnect() {
		String returnString = RESULT_ARRAY;
		try {
			southConnectionService.startConnect(emsConnectionModel);
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	@IMethodLog(desc = "数据采集管理:南向连接管理中断开连接")
	public String disConnect() {
		String returnString = RESULT_ARRAY;
		try {
			southConnectionService.disConnect(emsConnectionModel);
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	@IMethodLog(desc = "数据采集管理:南向连接管理中新增Corba连接", type = IMethodLog.InfoType.MOD)
	public String addCorbaConnection() {
		String returnString = RESULT_ARRAY;
		try {
			boolean checkConName = southConnectionService
					.checkConnectionNameExist(emsConnectionModel);
			if (true == checkConName) {
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage(getText(MessageCodeDefine.RENAME_EMS_CONNECTION));
			} else {
				boolean checkConIp = southConnectionService.checkIpAddressExist(emsConnectionModel);
				if (true == checkConIp){
					result.setReturnResult(CommonDefine.FAILED);
					result.setReturnMessage(getText(MessageCodeDefine.REPEAT_EMS_CONNECTION_IP));
				} else {
					String emsConnectionId = southConnectionService.addCorbaConnection(emsConnectionModel);
					emsConnectionModel.setEmsConnectionId(Integer.parseInt(emsConnectionId));
					if(emsConnectionModel.getConnectMode() == CommonDefine.CONNECT_MODE_AUTO){
						startConnect();
					}
					result.setReturnResult(CommonDefine.SUCCESS);
					result.setReturnMessage(getText(MessageCodeDefine.CORBA_CONNECTION_ADD_SUCCESS));
				}
			}
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	@IMethodLog(desc = "数据采集管理:南向连接管理中修改连接", type = IMethodLog.InfoType.MOD)
	public String modifyConnection() {
		String returnString = RESULT_ARRAY;
		try {
			boolean checkConName = southConnectionService
					.getConnectionByInfo(emsConnectionModel);
			if (true == checkConName) {
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage(getText(MessageCodeDefine.REPEAT_EMS_CONNECTION_IP_NAME));
			} else {
				southConnectionService.modifyConnection(emsConnectionModel,
						neModel);
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage(getText(MessageCodeDefine.MODIFY_CONNECTION_SUCCESS));
			}
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	@IMethodLog(desc = "数据采集管理:南向连接管理中新增telnet连接", type = IMethodLog.InfoType.MOD)
	public String addTelnetConnection() {
		String returnString = RESULT_ARRAY;
		try {
			boolean checkConName = southConnectionService
					.checkConnectionNameExist(emsConnectionModel);
			if (true == checkConName) {
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage(getText(MessageCodeDefine.RENAME_EMS_CONNECTION));
			} else {
				boolean checkConIp = southConnectionService.checkIpAddressExist(emsConnectionModel);
				if (true == checkConIp){
					result.setReturnResult(CommonDefine.FAILED);
					result.setReturnMessage(getText(MessageCodeDefine.REPEAT_EMS_CONNECTION_IP));
				} else {
					String emsConnectionId = southConnectionService.addTelnetConnection(emsConnectionModel,
							neModel);
					emsConnectionModel.setEmsConnectionId(Integer.parseInt(emsConnectionId));
					if(emsConnectionModel.getConnectMode() == CommonDefine.CONNECT_MODE_AUTO){
						startConnect();
					}
					result.setReturnResult(CommonDefine.SUCCESS);
					result.setReturnMessage(getText(MessageCodeDefine.TELNET_CONNECTION_ADD_SUCCESS));
				}
			}
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	@IMethodLog(desc = "数据采集管理:南向连接管理中删除链接", type = IMethodLog.InfoType.DELETE)
	public String deleteConnection() {
		String returnString = RESULT_OBJ;
		try {
			// 删除连接表中该记录
			southConnectionService.deleteConnection(emsConnectionModel);

			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.DELETE_CONNECTION_SUCCESS));
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	@IMethodLog(desc = "数据采集管理:南向连接管理中 任务控制操作")
	public String updateCollectStatus() {
		String returnString = RESULT_OBJ;
		try {
			// 删除连接表中该记录
			southConnectionService.updateCollectStatus(emsConnectionModel,
					minutes);

			result.setReturnResult(CommonDefine.SUCCESS);
//			result.setReturnMessage(getText(MessageCodeDefine.DELETE_CONNECTION_SUCCESS));
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}
	
	@IMethodLog(desc = "数据采集管理:南向连接管理中 任务控制操作暂停状态后计时功能", type = IMethodLog.InfoType.MOD)
	public String timeUpdateCollectStatus() {
		String returnString = RESULT_OBJ;
		try {
			// 删除连接表中该记录
			southConnectionService.timeUpdateCollectStatus(emsConnectionModel);

			result.setReturnResult(CommonDefine.SUCCESS);
//			result.setReturnMessage(getText(MessageCodeDefine.DELETE_CONNECTION_SUCCESS));
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	/**
	 * 检索网管分组信息
	 * 
	 * @param start
	 * @param limit
	 * @param emsGroupId
	 * @return
	 */
	@IMethodLog(desc = "数据采集管理:网管分组管理")
	public String getEmsGroupListByGroupId() {
		String returnString = RESULT_OBJ;
//		HttpServletRequest request = ServletActionContext.getRequest();
//		HttpSession session = request.getSession();
//		Integer userId = session.getAttribute("SYS_USER_ID") != null ? Integer
//				.valueOf(session.getAttribute("SYS_USER_ID").toString()) : null;
				
		try {
			Map<String, Object> data = southConnectionService
					.getEmsGroupListByGroupId(start, limit,
							emsGroupModel.getEmsGroupId());
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(data);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return returnString;
	}

	/**
	 * 新增网管分组
	 * 
	 * @param emsGroupName
	 * @param emsGroupNote
	 * @return
	 * @throws CommonException
	 */
	@IMethodLog(desc = "数据采集管理:新增网管分组", type = IMethodLog.InfoType.MOD)
	public String addEmsGroup() {
		String returnString = RESULT_ARRAY;
		try {
			boolean checkConnection = southConnectionService
					.getEmsGroupByName(emsGroupModel);
			if (checkConnection == true) {
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage(getText(MessageCodeDefine.RENAME_EMS_GROUP));
			} else {
				southConnectionService.addEmsGroup(emsGroupModel);
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage(getText(MessageCodeDefine.ADD_EMS_GROUP_SUCCESS));
			}
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}

		return RESULT_OBJ;
	}

	/**
	 * 删除网管分组
	 * 
	 * @param emsGroupId
	 * @return
	 * @throws CommonException
	 */
	@IMethodLog(desc = "数据采集管理:删除网管分组", type = IMethodLog.InfoType.DELETE)
	public String deleteEmsGroup() {
		try {
			southConnectionService
					.deleteEmsGroup(emsGroupModel.getEmsGroupId());

			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.DELETE_EMS_GROUP_SUCCESS));
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	/**
	 * 修改网管分组
	 * 
	 * @return
	 */
	@IMethodLog(desc = "数据采集管理:修改网管分组", type = IMethodLog.InfoType.MOD)
	public String modifyEmsGroup() {
		String returnString = RESULT_ARRAY;
		try {
			boolean checkService = southConnectionService
					.getEmsGroupByName(emsGroupModel);
			if (checkService == true) {
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage(getText(MessageCodeDefine.RENAME_EMS_GROUP));
			} else {
				southConnectionService.modifyEmsGroup(emsGroupModel);
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage(getText(MessageCodeDefine.MODIFY_EMS_GROUP_SUCCESS));
			}
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	/**
	 * 新增子网
	 * 
	 * @return
	 */
	@IMethodLog(desc = "数据采集管理:新增子网", type = IMethodLog.InfoType.MOD)
	public String addSubnet() {
		String returnString = RESULT_ARRAY;
		try {
			boolean checkConnection = southConnectionService
					.getSubnetInfo(subnetModel);
			if (checkConnection == true) {
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage(getText(MessageCodeDefine.RENAME_SUBNET));
			} else {
				southConnectionService.addSubnet(subnetModel);
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage(getText(MessageCodeDefine.ADD_SUBNET_SUCCESS));
			}
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}

		return returnString;
	}

	/**
	 * 
	 * @return
	 * @throws ParseException
	 */
	@IMethodLog(desc = "数据采集管理:子网管理页面     新增子网 时 子网名称检查", type = IMethodLog.InfoType.MOD)
	public String checkSubnetNameExist() {
		String returnString = RESULT_OBJ;
		try {
			Boolean data = southConnectionService
					.checkSubnetNameExist(subnetModel);
			if (data)
				result.setReturnResult(CommonDefine.FAILED);
			else
				result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	/**
	 * 修改子网
	 * 
	 * @return
	 */
	@IMethodLog(desc = "数据采集管理:修改子网", type = IMethodLog.InfoType.MOD)
	public String modifySubnet() {
		String returnString = RESULT_ARRAY;
		try {
			boolean checkConnection = southConnectionService
					.getSubnetInfo(subnetModel);
			if (checkConnection == true) {
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage(getText(MessageCodeDefine.RENAME_EMS_CONNECTION));
			} else {
				southConnectionService.modifySubnet(subnetModel);
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage(getText(MessageCodeDefine.MODIFY_SUBNET_SUCCESS));
			}
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}

		return returnString;
	}

	/**
	 * 获取要修改的子网信息
	 * 
	 * @return
	 */
	@IMethodLog(desc = "数据采集管理:获取要修改的子网信息", type = IMethodLog.InfoType.MOD)
	public String getSubnetBySubnetId() {
		try {
			Map subnetList = southConnectionService
					.getSubnetBySubnetId(subnetModel.getSubnetId());

			resultObj = JSONObject.fromObject(subnetList);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	/**
	 * 删除子网下的子网
	 * 
	 * @return
	 */
	@IMethodLog(desc = "数据采集管理:删除子网的子网", type = IMethodLog.InfoType.DELETE)
	public String deleteSubnetSubnet() {
		try {
			southConnectionService.deleteSubnetSubnet(
					subnetModel.getParentSubnetId(), subnetModel.getSubnetId());

			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.DELETE_SUBNET_SUCCESS));
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	/**
	 * 删除网管下的子网
	 * 
	 * @return
	 */
	@IMethodLog(desc = "数据采集管理:删除网管下第一层子网", type = IMethodLog.InfoType.DELETE)
	public String deleteEmsSubnet() {
		try {

			southConnectionService.deleteEmsSubnet(emsConnectionId,
					subnetModel.getSubnetId());

			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.DELETE_SUBNET_SUCCESS));
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	/**
	 * 保存已分组子网下的网元
	 * 
	 * @return
	 */
	@IMethodLog(desc = "数据采集管理:保存已分组子网下的网元", type = IMethodLog.InfoType.MOD)
	public String saveClassifiedNe() {
		try {
			// 转化成JSONArray对象
			JSONArray jsonArray = JSONArray.fromObject(jString);

			// 转成SyncNeInfoModel对象
			JSONObject jsonObject = (JSONObject) jsonArray.get(0);
			Map map = (Map) jsonObject;

			southConnectionService.saveClassifiedNe(
					Integer.valueOf(map.get("subnetId").toString()), jString);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.SAVE_CLASSIFIED_NE));
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	/**
	 * 网元管理页面 列表同步 操作
	 * 
	 * @return
	 */
	@IMethodLog(desc = "数据采集管理:网元管理页面  获取网管下网元信息")
	public String getSyncNeListByEmsInfo() {
		String returnString = RESULT_OBJ;
		try {
			Map<String, Object> syncNeList = southConnectionService
					.getSyncNeListByEmsInfo(sysUserId,start, limit, emsGroupId,
							emsConnectionId);
			resultObj = JSONObject.fromObject(syncNeList);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	/**
	 * 网元管理页面获取网管操作
	 * 
	 * @return
	 */
	@IMethodLog(desc = "数据采集管理:网元管理页面获取网管信息")
	public String getEmsConnection() {
		String returnString = RESULT_OBJ;
		try {
			Map result = southConnectionService.getEmsConnection(emsGroupId);

			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	/**
	 * 网元管理页面的网元列表同步功能中检索新增的网元列表
	 * 
	 * @return
	 */
	@IMethodLog(desc = "数据采集管理:网元管理页面的列表同步功能中检索新增/删除的网元列表", type = IMethodLog.InfoType.MOD)
	public String getAlterNeByEmsConnectionId() {
		String returnString = RESULT_OBJ;
		Map map = new HashMap();

		try {
			map = southConnectionService
					.getAlterNeByEmsConnectionId(emsConnectionId);
			resultObj = JSONObject.fromObject(map);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	// /**
	// * 网元管理页面的网元列表同步功能中检索删除的网元列表
	// * @return
	// */
	// @IMethodLog(desc = "数据采集管理:网元管理页面的网元列表同步功能中检索删除的网元列表", type = IMethodLog.InfoType.DELETE)
	// public String getDeleteNeByEmsConnectionId() {
	// String returnString = RESULT_OBJ;
	// Map map = new HashMap();
	// try {
	// map = southConnectionService
	// .getDeleteNeByEmsConnectionId(emsConnectionId);
	// resultObj = JSONObject.fromObject(map);
	// returnString = RESULT_OBJ;
	// } catch (CommonException e) {
	// result.setReturnResult(CommonDefine.FAILED);
	// result.setReturnMessage(e.getErrorMessage());
	// resultObj = JSONObject.fromObject(result);
	// returnString = RESULT_OBJ;
	// }
	// return returnString;
	// }

	/**
	 * 网元管理页面的网元列表弹窗中的新增按钮操作
	 * 
	 * @return
	 */
	@IMethodLog(desc = "数据采集管理:网元管理页面的网元列表弹窗中的新增按钮操作", type = IMethodLog.InfoType.MOD)
	public String neListSyncAdd() {
		String returnString = RESULT_OBJ;
		try {
			southConnectionService.neListSyncAdd(emsConnectionId);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.UPDATE_NE_SUCCESS));
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	/**
	 * 网元管理页面的网元同步操作
	 * 
	 * @return
	 */
	@IMethodLog(desc = "数据采集管理:网元管理页面的网元同步")
	public String syncSelectedNe() {
		String returnString = RESULT_OBJ;
		try {
			southConnectionService.syncSelectedNe(jString);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.SYNC_NE_SUCCESS));
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	/**
	 * 获取网管下未划分子网的网元
	 * @@@分权分域到网元@@@
	 * @return
	 */
	@IMethodLog(desc = "子网管理：获取网管下未划分子网的网元")
	public String getNeListByEmsConnnectionId() {
		String returnString = RESULT_OBJ;
		try {
			Map result = southConnectionService
					.getNeListByEmsConnnectionId(neModel.getEmsConnectionId(), sysUserId);

			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	/**
	 * 获取网管下具体子网下的网元
	 * @@@分权分域到网元@@@
	 * @return
	 */
	@IMethodLog(desc = "子网管理：获取网管下具体子网下的网元")
	public String getNeListByEmsConnnectionIdAndSubnetId() {
		String returnString = RESULT_OBJ;
		try {
			Map map = southConnectionService
					.getNeListByEmsConnnectionIdAndSubnetId(neModel,sysUserId);

			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(map);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	/**
	 * 网元管理页面的 新增网元功能
	 * 
	 * @return
	 */
	@IMethodLog(desc = "数据采集管理:网元管理页面的 新增网元功能", type = IMethodLog.InfoType.MOD)
	public String addTelnetNe() {
		List<Map> neList = ListStringtoListMap(this.jsonString);
		try {
			southConnectionService.addTelnetNe(neList);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.ADD_TELNET_NE_SUCCESS));
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	/**
	 * 修改网元前检索网元信息
	 * 
	 * @return
	 */
	@IMethodLog(desc = "网元管理页面：修改网元前检索网元信息", type = IMethodLog.InfoType.MOD)
	public String getTelnetNeByNeId() {
		String returnString = RESULT_OBJ;
		try {
			Map map = southConnectionService.getTelnetNeByNeId(neModel);
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(map);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	/**
	 * 修改网元
	 * 
	 * @return
	 */
	@IMethodLog(desc = "网元管理:修改网元", type = IMethodLog.InfoType.MOD)
	public String modifyCorbaNe() {
		try {
			
			Map<String,Object> map = new HashMap<String,Object>();			
			map.put("standardName", neModel.getUserLabel());
			map.put("resourceId", neModel.getNeId());
			boolean checkNeName = resourceStockService.checkNeNameExit(map);
			
			if ( checkNeName) {
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage(getText(MessageCodeDefine.RENAME_NE_NAME));
			} else {
				southConnectionService.modifyCorbaNe(neModel);

				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage(getText(MessageCodeDefine.MODIFY_CORBA_NE_SUCCESS));
			}
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	/**
	 * 修改网元
	 * 
	 * @return
	 */
	@IMethodLog(desc = "网元管理:修改网元", type = IMethodLog.InfoType.MOD)
	public String modifyTelnetNe() {
		try {
			boolean checkNeName = southConnectionService
					.checkNeNameExist(neModel);
			if (true == checkNeName) {
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage(getText(MessageCodeDefine.RENAME_NE_NAME));
			} else {
				southConnectionService.modifyTelnetNe(neModel);

				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage(getText(MessageCodeDefine.MODIFY_TELNET_NE_SUCCESS));
			}
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	/**
	 * 网元管理中删除网元
	 * 
	 * @return
	 */
	@IMethodLog(desc = "网元管理:网元管理中删除网元", type = IMethodLog.InfoType.DELETE)
	public String deleteTelnetNe() {
		String returnString = RESULT_OBJ;
		try {
			// 删除连接表中该记录
			southConnectionService.deleteTelnetNe(neModel.getEmsConnectionId(),
					neModel.getNeId());

			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.DELETE_TELNET_NE_SUCCESS));
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	/**
	 * 网元管理中登录网元
	 * 
	 * @return
	 */
	@IMethodLog(desc = "网元管理:网元管理中登录网元")
	public String logonTelnetNe() {
		String returnString = RESULT_OBJ;
		try {
			southConnectionService.logonTelnetNe(jString);

			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.LOG_ON_NE_SUCCESS));
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	/**
	 * 网元管理中退出登录网元
	 * 
	 * @return
	 */
	@IMethodLog(desc = "网元管理:网元管理中退出登录网元")
	public String logoutTelnetNe() {
		String returnString = RESULT_OBJ;
		try {
			southConnectionService.logoutTelnetNe(jString);

			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.LOG_OUT_NE_SUCCESS));
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	/**
	 * 交叉连接管理页面网管分组、网管查询功能
	 * 
	 * @return
	 */
	@IMethodLog(desc = "数据采集管理:交叉连接管理页面网管分组、网管查询功能")
	public String getCrossConnectListByEmsInfo() {
		String returnString = RESULT_OBJ;
		try {
			Map<String, Object> crossConnectList = southConnectionService
					.getCrossConnectListByEmsInfo(sysUserId,start, limit, emsGroupId,
							emsConnectionId);
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(crossConnectList);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	/**
	 * 交叉连接管理页面同步功能
	 * 
	 * @return
	 */
	@IMethodLog(desc = "数据采集管理:交叉连接管理页面中 同步操作")
	public String syncNeCrossConnnection() {
		String returnString = RESULT_OBJ;
		try {
			southConnectionService.syncNeCrossConnnection(jString);
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	/**
	 * 交叉连接管理页面同步功能
	 * 
	 * @return
	 */
	@IMethodLog(desc = "数据采集管理:交叉连接管理页面中 详情操作")
	public String getCrsNeDetailInfoByNeId() {
		String returnString = RESULT_OBJ;
		try {
			Map map = southConnectionService.getCrsNeDetailInfoByNeId(start,
					limit, neModel, sdhCrsModel);
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(map);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}
	

	/**
	 * 更新最近一次开始时间
	 * 
	 * @return
	 */
	public String setBeginTime() {
		try {
			// 转化成JSONArray对象
			JSONArray jsonArray = JSONArray.fromObject(jString);

			// 转成SyncNeInfoModel对象
			JSONObject jsonObject = (JSONObject) jsonArray.get(0);
			Map map = (Map) jsonObject;

			Map ma = southConnectionService.setBeginTime(map);
			resultObj = JSONObject.fromObject(ma);
			// resultArrary = JSONArray.fromObject(list);
			System.out.println(resultObj.toString());
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			// result.setReturnMessage(e.getErrorMessage());
			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIR_EXCPT_UPDATE));
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}

	/**
	 * 以太网管理页面网管分组、网管查询功能
	 * 
	 * @return
	 */
	@IMethodLog(desc = "数据采集管理:以太网管理页面网管分组、网管查询功能")
	public String getMstpListByEmsInfo() {
		String returnString = RESULT_OBJ;
		try {
			Map<String, Object> crossConnectList = southConnectionService
					.getMstpListByEmsInfo(sysUserId,start, limit, emsGroupId,
							emsConnectionId);
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(crossConnectList);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	/**
	 * 以太网同步管理页面的同步操作
	 * 
	 * @return
	 */
	@IMethodLog(desc = "数据采集管理:以太网同步管理页面的同步操作")
	public String syncMstpNe() {
		String returnString = RESULT_OBJ;
		try {
			southConnectionService.syncMstpNe(jString);
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	/**
	 * 拓扑链路同步管理页面检索功能
	 * 
	 * @return
	 */
	@IMethodLog(desc = "拓扑链路同步管理页面：拓扑链路同步管理页面检索功能")
	public String getTopoLinkSyncListByEmsGroupId() {
		String returnString = RESULT_OBJ;
		try {
			Map map = southConnectionService.getTopoLinkSyncListByEmsGroupId(sysUserId,
					start, limit, emsConnectionModel.getEmsGroupId());
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(map);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	/**
	 * 拓扑链路同步管理页面同步操作
	 * 
	 * @return
	 */
	@IMethodLog(desc = "数据采集管理:拓扑链路同步管理页面同步操作")
	public String topoLinkSync() {
		String returnString = RESULT_OBJ;
		try {
			Map map = southConnectionService.topoLinkSync(emsConnectionModel);
//			result.setReturnResult(CommonDefine.SUCCESS);
			// result.setReturnMessage(getText(MessageCodeDefine.SAVE_CLASSIFIED_NE));
			map.put("returnResult", CommonDefine.SUCCESS);
			//过滤null值,否则出错
			JsonConfig cfg = new JsonConfig();
			cfg.setJsonPropertyFilter(new PropertyFilter() {
				@Override
				public boolean apply(Object source, String name, Object value) {
					return value == null;
				}
			});
			resultObj = JSONObject.fromObject(map,cfg);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}
	
	/**
	 * 拓扑链路同步管理页面同步操作
	 * 
	 * @return
	 */
	/* 与 topoLinkSync 合并
	@IMethodLog(desc = "数据采集管理:拓扑链路同步管理页面同步操作返回信息")
	public String topoLinkSyncReturnInfo() {
		String returnString = RESULT_OBJ;
		try {
			Map map = southConnectionService.topoLinkSyncReturnInfo(emsConnectionModel);
			result.setReturnResult(CommonDefine.SUCCESS);
			// result.setReturnMessage(getText(MessageCodeDefine.SAVE_CLASSIFIED_NE));
			resultObj = JSONObject.fromObject(map);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}*/

	/**
	 * 拓扑链路同步管理页面同步操作
	 * 
	 * @return
	 */
	@IMethodLog(desc = "数据采集管理:拓扑链路同步管理页面同步操作")
	public String topoLinkSyncChangeList() {
		String returnString = RESULT_OBJ;
		try {
			Integer collectLevel = CommonDefine.COLLECT_LEVEL_1;
			JsonConfig cfg=new JsonConfig();
			Map classMap=new HashMap();
			classMap.put("CREATE_TIME", Date.class);
			classMap.put("UPDATE_TIME", Date.class);
			classMap.put("conflictList", HashMap.class);
			cfg.setClassMap(classMap);
			cfg.setRootClass(com.fujitsu.model.LinkAlterModel.class);
			cfg.setJavaPropertyFilter(new PropertyFilter() {
				@Override
				public boolean apply(Object source, String name, Object value) {
					return value == JSONNull.getInstance();
				}
			});
			List linkList=new ArrayList(net.sf.json.JSONArray.toCollection(
					net.sf.json.JSONArray.fromObject(jsonString), 
					cfg));
			southConnectionService.topoLinkSyncChangeList(emsConnectionId,collectLevel,linkList,null);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.TOPO_LINK_SYNC_SUCCESS));
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	/**
	 * 网管同步管理页面检索功能
	 * 
	 * @return
	 */
	@IMethodLog(desc = "网管同步管理页面：网管同步管理页面检索功能")
	public String getEmsConnectionSyncInfo() {
		String returnString = RESULT_OBJ;
		try {
			Map map = southConnectionService.getEmsConnectionSyncInfo(sysUserId,start,
					limit, emsGroupId);
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(map);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	/**
	 * 网管同步管理页面 启动任务操作
	 * 
	 * @return
	 */
	@IMethodLog(desc = "数据采集管理:网管同步管理页面 启动任务操作")
	public String startTask() {
		String returnString = RESULT_ARRAY;
		try {
			// 转化成JSONArray对象
			JSONArray jsonArray = JSONArray.fromObject(jString);

			// 转成SyncNeInfoModel对象
			JSONObject jsonObject = (JSONObject) jsonArray.get(0);
			Map map = (Map) jsonObject;
			southConnectionService.startTask(map);
			returnString = RESULT_OBJ;
		} catch (ParseException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("");
			e.printStackTrace();
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	/**
	 * 网管同步管理页面 挂起任务操作
	 * 
	 * @return
	 */
	@IMethodLog(desc = "数据采集管理:网管同步管理页面 挂起任务操作", type = IMethodLog.InfoType.MOD)
	public String disTask() {
		String returnString = RESULT_ARRAY;
		try {
			// 转化成JSONArray对象
			JSONArray jsonArray = JSONArray.fromObject(jString);

			// 转成SyncNeInfoModel对象
			JSONObject jsonObject = (JSONObject) jsonArray.get(0);
			Map map = (Map) jsonObject;
			southConnectionService.disTask(map);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	/**
	 * 设置周期
	 * 
	 * @return
	 */
	public String setCycle() {
		try {
			// 转化成JSONArray对象
			JSONArray jsonArray = JSONArray.fromObject(jString);

			// 转成SyncNeInfoModel对象
			JSONObject jsonObject = (JSONObject) jsonArray.get(0);
			Map map = (Map) jsonObject;

			southConnectionService.setCycle(map);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.MODIFY_EMS_SYNC_SUCCESS));
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}

	/**
	 * 网管同步管理页面 挂起任务操作
	 * 
	 * @return
	 * @throws Exception
	 */
	@IMethodLog(desc = "数据采集管理:网管同步管理页面 手动同步操作")
	public String manualSyncEms() {
		String returnString = RESULT_ARRAY;
		try {
			southConnectionService.manualSyncEms(taskId, emsConnectionId);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.EMS_MANUAL_SYNC_SUCCESS));
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnString;
	}

	/**
	 * 网管同步管理页面任务状态查看功能
	 * 
	 * @return
	 */
	@IMethodLog(desc = "网管同步管理页面：网管同步管理页面任务状态查看功能")
	public String getTaskDetailInfo() {
		String returnString = RESULT_OBJ;
		try {
			Map map = southConnectionService.getTaskDetailInfo(emsConnectionId);
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(map);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}

		return returnString;
	}

	/**
	 * 网管同步管理页面设置同步暂停时间功能
	 * 
	 * @return
	 */
	@IMethodLog(desc = "网管同步管理页面：网管同步管理页面设置同步暂停时间功能", type = IMethodLog.InfoType.MOD)
	public String updateEmsConnectionSync() {
		String returnString = RESULT_OBJ;
		try {
			southConnectionService.updateEmsConnectionSync(emsConnectionId,
					taskId, minutes);
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			System.out.print("eee");
			e.printStackTrace();

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		} catch (Exception e) {
			System.out.println("-------------");
			e.printStackTrace();
		}
		return returnString;
	}
	
	/**
	 * 网管同步管理页面设置同步 继续功能
	 * @return
	 */
	@IMethodLog(desc = "网管同步管理页面：网管同步管理页面设置同步 继续功能", type = IMethodLog.InfoType.MOD)
	public String proceedTaskSetting() {
		String returnString = RESULT_OBJ;
		try {
			southConnectionService.proceedTaskSetting(emsConnectionId,taskId);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.EMS_SYNC_TASK_PROCEED_SUCCESS));
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}
	
	/**
	 * 网管同步管理页面设置同步 停止功能
	 * @return
	 */
	@IMethodLog(desc = "网管同步管理页面：网管同步管理页面设置同步 停止功能", type = IMethodLog.InfoType.MOD)
	public String stopTaskSetting() {
		String returnString = RESULT_OBJ;
		try {
			southConnectionService.stopTaskSetting(emsConnectionId,taskId);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.EMS_SYNC_TASK_STOP_SUCCESS));
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	/**
	 * 
	 * @return
	 */
	@IMethodLog(desc = "数据采集管理:接入服务器器管理中获取所有接入服务器")
	public String getSysServiceRecord() {
		String returnString = RESULT_OBJ;
		try {
			Map map = southConnectionService.getSysServiceRecord(start, limit,
					sysSvcRecordId);
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(map);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	/**
	 * 接入服务器管理页面的 新增接入服务器功能
	 * 
	 * @return
	 */
	@IMethodLog(desc = "数据采集管理:接入服务器管理页面的  新增接入服务器功能", type = IMethodLog.InfoType.MOD)
	public String addSysService() {
		try {
			boolean checkService = southConnectionService
					.getSysServiceBySvcInfo(sysServiceModel);
			if (true == checkService) {
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage(getText(MessageCodeDefine.RENAME_SYS_SERVICE));
			} else {
				boolean checkConIp = southConnectionService
						.getSysServiceBySvcIpAddress(sysServiceModel);
				if (true == checkConIp) {
					result.setReturnResult(CommonDefine.FAILED);
					result.setReturnMessage(getText(MessageCodeDefine.RENAME_SYS_IP_ADDRESS));
				} else {
					southConnectionService.addSysService(sysServiceModel);
					result.setReturnResult(CommonDefine.SUCCESS);
					result.setReturnMessage(getText(MessageCodeDefine.ADD_SYS_SERVICE_SUCCESS));
				}
			}
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	/**
	 * 删除接入服务器
	 * 
	 * @param emsGroupId
	 * @return
	 * @throws CommonException
	 */
	@IMethodLog(desc = "数据采集管理:接入服务器管理页面 删除接入服务器", type = IMethodLog.InfoType.DELETE)
	public String deleteSysService() {
		try {
			southConnectionService.deleteSysService(sysServiceModel
					.getSysSvcRecordId());

			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.DELETE_SYS_SERVICE_SUCCESS));
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	/**
	 * 拓扑链路同步管理页面检索功能
	 * 
	 * @return
	 */
	@IMethodLog(desc = "接入服务器管理页面中 删除前检索接入服务器是否有南向连接", type = IMethodLog.InfoType.DELETE)
	public String getSouthConnectionListBySysServiceId() {
		String returnString = RESULT_OBJ;
		try {
			Map map = southConnectionService
					.getConnectionListBySysServiceId(sysServiceModel);
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(map);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	/**
	 * 获取要修改的接入服务器
	 * 
	 * @return
	 */
	@IMethodLog(desc = "数据采集管理:获取要修改的接入服务器", type = IMethodLog.InfoType.MOD)
	public String getSysServiceBySysSvcId() {
		String returnString = RESULT_OBJ;
		try {
			Map map = southConnectionService
					.getSysServiceBySysSvcId(sysServiceModel
							.getSysSvcRecordId());

			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(map);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	/**
	 * 修改接入服务器
	 * 
	 * @return
	 */
	@IMethodLog(desc = "数据采集管理:修改接入服务器", type = IMethodLog.InfoType.MOD)
	public String modifySysService() {
		String returnString = RESULT_ARRAY;
		try {
			boolean checkService = southConnectionService
					.getSysServiceBySvcInfo(sysServiceModel);
			if (true == checkService) {
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage(getText(MessageCodeDefine.RENAME_SYS_SERVICE));
			} else {
				boolean checkConIp = southConnectionService
						.getSysServiceBySvcIpAddress(sysServiceModel);
				if (true == checkConIp) {
					result.setReturnResult(CommonDefine.FAILED);
					result.setReturnMessage(getText(MessageCodeDefine.RENAME_SYS_IP_ADDRESS));
				} else {
					southConnectionService.modifySysService(sysServiceModel);
					result.setReturnResult(CommonDefine.SUCCESS);
					result.setReturnMessage(getText(MessageCodeDefine.MODIFY_SYS_SERVICE_SUCCESS));
				}
			}
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}

	/**
	 * 检查名称是否重复
	 * 
	 * @return
	 * @throws ParseException
	 */
	@IMethodLog(desc = "数据采集管理:名称检查")
	public String checkNameExist() {
		String returnString = RESULT_OBJ;
		try {
			Boolean data = southConnectionService.checkNameExist(
					emsConnectionModel, sysServiceModel, subnetModel,
					emsGroupModel);
			if (data)
				result.setReturnResult(CommonDefine.FAILED);
			else
				result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}
	
	/**
	 * 南向连接管理中导出功能
	 * 
	 */
	@IMethodLog(desc = "数据采集管理:南向连接管理中导出功能")
	public String exportExcel(){
//		JSONObject jsonObject = JSONObject.fromObject(jString);
//		Map map = (Map) jsonObject;	
		List list = new ArrayList();
		Map map = new HashMap();
		int excelFlag = 0;
		try {
			map = southConnectionService.getConnectionListByGroupId(sysUserId,CommonDefine.TRUE,start, limit, emsGroupId);
			String destination=southConnectionService.exportExcel(map,excelFlag);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(destination);
			resultObj=JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			return RESULT_OBJ;
		}	
    	return RESULT_OBJ;
    }
	
	/**
	 * 网元管理页面列表同步导出功能
	 * 
	 */
	@IMethodLog(desc = "数据采集管理:网元管理页面列表同步导出功能")
	public String exportAlterNeExcel(){
		
		Map map = new HashMap();
		Map mapTemp = new HashMap();
		int excelFlag = 1;
		try {
			map = southConnectionService
					.getAlterNeByEmsConnectionId(emsConnectionId);
//			mapTemp = (Map)map.get("rows");
			String destination=southConnectionService.exportExcel(map,excelFlag);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(destination);
			resultObj=JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			return RESULT_OBJ;
		}	
    	return RESULT_OBJ;
    }

//---------------------------2014/10/16 更新--------------------------------
	
	@IMethodLog(desc = "拓扑链路同步:新增同步模式", type = IMethodLog.InfoType.MOD)
	public String editSyncMode() {
		String returnString = RESULT_OBJ;
		try {
			southConnectionService.editSyncMode(emsConnectionId,syncMode); 
			result.setReturnResult(CommonDefine.SUCCESS);  
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("同步模式设定失败!"); 
		}
		resultObj = JSONObject.fromObject(result);
		return RESULT_OBJ;
	}
	
	
	
	public EmsConnectionModel getEmsConnectionModel() {
		return emsConnectionModel;
	}

	public void setEmsConnectionModel(EmsConnectionModel emsConnectionModel) {
		this.emsConnectionModel = emsConnectionModel;
	}

	public NeModel getNeModel() {
		return neModel;
	}

	public void setNeModel(NeModel neModel) {
		this.neModel = neModel;
	}

	public EmsGroupModel getEmsGroupModel() {
		return emsGroupModel;
	}

	public void setEmsGroupModel(EmsGroupModel emsGroupModel) {
		this.emsGroupModel = emsGroupModel;
	}

	public List<String> getJsonString() {
		return jsonString;
	}

	public void setJsonString(List<String> jsonString) {
		this.jsonString = jsonString;
	}

	public SubnetModel getSubnetModel() {
		return subnetModel;
	}

	public void setSubnetModel(SubnetModel subnetModel) {
		this.subnetModel = subnetModel;
	}

	public Integer getEmsConnectionId() {
		return emsConnectionId;
	}

	public void setEmsConnectionId(Integer emsConnectionId) {
		this.emsConnectionId = emsConnectionId;
	}

	public Integer getSubnetId() {
		return subnetId;
	}

	public void setSubnetId(Integer subnetId) {
		this.subnetId = subnetId;
	}

	public String getJString() {
		return jString;
	}

	public void setJString(String jString) {
		this.jString = jString;
	}

	public SdhCrsModel getSdhCrsModel() {
		return sdhCrsModel;
	}

	public void setSdhCrsModel(SdhCrsModel sdhCrsModel) {
		this.sdhCrsModel = sdhCrsModel;
	}

	public Integer getEmsGroupId() {
		return emsGroupId;
	}

	public void setEmsGroupId(Integer emsGroupId) {
		this.emsGroupId = emsGroupId;
	}

	public Integer getSysSvcRecordId() {
		return sysSvcRecordId;
	}

	public void setSysSvcRecordId(Integer sysSvcRecordId) {
		this.sysSvcRecordId = sysSvcRecordId;
	}

	public SysServiceModel getSysServiceModel() {
		return sysServiceModel;
	}

	public void setSysServiceModel(SysServiceModel sysServiceModel) {
		this.sysServiceModel = sysServiceModel;
	}

	public Integer getMinutes() {
		return minutes;
	}

	public void setMinutes(Integer minutes) {
		this.minutes = minutes;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public Integer getSaveValue() {
		return saveValue;
	}

	public void setSaveValue(Integer saveValue) {
		this.saveValue = saveValue;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public Integer getPeriodType() {
		return periodType;
	}

	public void setPeriodType(Integer periodType) {
		this.periodType = periodType;
	}

	public Integer getSyncMode() {
		return syncMode;
	}

	public void setSyncMode(Integer syncMode) {
		this.syncMode = syncMode;
	} 
}
