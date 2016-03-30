package com.fujitsu.manager.circuitManager.action;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

import com.fujitsu.IService.ICircuitManagerService;
import com.fujitsu.IService.IAlarmManagementService;
import com.fujitsu.IService.IMethodLog;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.handler.MessageHandler;

public class CircuitAction extends AbstractAction {

	/**
	 * @author wangjian (前半) daihuijun (后半)
	 */
	private static final long serialVersionUID = 1L;
	private int emsGroupId = 0;

	@Resource
	public ICircuitManagerService circuitManagerService;
	@Resource
	public IAlarmManagementService alarmManagementService;
	protected String connectRate = null;
	protected int circuitState = 0;
	protected int crossChange = 0;
	protected int isFix = 0;
	protected String jsonString;
	protected int aNodeId;
	protected int aNodeLevel;
	protected int zNodeId;
	protected int zNodeLevel;
	protected int parentCir;
	protected long linkId=-1L;
	protected int vCircuit;
	protected File uploadFile;
	protected String uploadFileFileName;
	protected String uploadFileContentType;
	protected String devMode;
	protected int type;// 电路类型
	protected String cirNo;
	protected int linkType;
	//protected String userId;
	protected String processKey;

	/** ********************************wangjian**begin************************ */
	/** 
	 * @@@分权分域到网元@@@
	 * 获取网管任务列表
	 */
	@IMethodLog(desc = "获取网管任务列表")
	public String getAllEMSTask() {
		try {
			Map<String, Object> data = circuitManagerService.getAllEMSTask(
					sysUserId, emsGroupId, start, limit);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			if (e.getErrorCode() == MessageCodeDefine.USER_LOGIN_AGAIN) {
				result.setReturnMessage(e.getErrorMessage());
			} else {
				result.setReturnMessage(MessageHandler
						.getErrorMessage(MessageCodeDefine.CIR_EXCPT_SELECT));
			}
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}

	/**
	 * 获取网管分组
	 */
	public String getAllGroup() {
		try {
			// 转化成JSONArray对象
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			// 转成SyncNeInfoModel对象
			JSONObject jsonObject = (JSONObject) jsonArray.get(0);
			Map map = (Map) jsonObject;
			List<Map> data = circuitManagerService.getAllGroup(map);
			resultArray = JSONArray.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			if (e.getErrorCode() == MessageCodeDefine.USER_LOGIN_AGAIN) {
				result.setReturnMessage(e.getErrorMessage());
			} else {
				result.setReturnMessage(MessageHandler
						.getErrorMessage(MessageCodeDefine.CIR_EXCPT_SELECT));
			}
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_ARRAY;

	}

	/**
	 * 设置周期
	 * 
	 * @return
	 */
	public String setCycle() {
		try {
			// 转化成JSONArray对象
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			// 转成SyncNeInfoModel对象
			JSONObject jsonObject = (JSONObject) jsonArray.get(0);
			Map map = (Map) jsonObject;

			circuitManagerService.setCycle(map);
			result.setReturnResult(CommonDefine.SUCCESS);
			result
					.setReturnMessage(getText(MessageCodeDefine.CIRCUIT_UPDATE_SUCCESS));
			resultObj = JSONObject.fromObject(result);

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
	 * 更新最近一次开始时间
	 * 
	 * @return
	 */
	public String setBeginTime() {
		try {
			// 转化成JSONArray对象
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			// 转成SyncNeInfoModel对象
			JSONObject jsonObject = (JSONObject) jsonArray.get(0);
			Map map = (Map) jsonObject;

			Map ma = circuitManagerService.setBeginTime(map);
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
	 * 局部范围电路生成
	 * 
	 * @return
	 * @throws Exception
	 */
	@IMethodLog(desc = "局部电路生成")
	public String createCircuit() {
		try {
			List<Map> list_map = new ArrayList<Map>();

			// 转化成JSONArray对象
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			for (Object obj : jsonArray) {
				// 转成SyncNeInfoModel对象
				JSONObject jsonObject = (JSONObject) obj;
				Map map = (Map) jsonObject;
				list_map.add(map);
			}

			Map map = circuitManagerService.createCircuit(list_map,processKey);

//			result.setReturnResult(Integer.parseInt(map.get("ReturnResult")
//					.toString()));
//			result.setReturnMessage(map.get("ReturnMessage").toString());
			resultObj = JSONObject.fromObject(map);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			// result.setReturnMessage(e.getErrorMessage());
			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIR_EXCPT_NEW));
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}

	/**
	 * 全网电路生成
	 * 
	 * @return
	 * @throws Exception
	 */
	@IMethodLog(desc = "全网电路生成", type = IMethodLog.InfoType.MOD)
	public String createAllCircuit() {
		try {

			circuitManagerService.createAllCircuit(processKey);

			result.setReturnResult(CommonDefine.SUCCESS);
			result
					.setReturnMessage(getText(MessageCodeDefine.CIRCUIT_CREATE_SUCCESS));
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			// result.setReturnMessage(e.getErrorMessage());
			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIR_EXCPT_NEW));
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}

	/**
	 * 查询最近一次生成的电路
	 * 
	 * @return
	 */
	@IMethodLog(desc = "最新生成电路查询")
	public String selectCircuitLast() {
		try {

			Map<String, Object> data = circuitManagerService.selectCircuitLast(
					type, start, limit);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			// result.setReturnMessage(e.getErrorMessage());
			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIR_EXCPT_SELECT));
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	/**
	 * 启用自动生成任务
	 * 
	 * @return
	 */
	@IMethodLog(desc = "启用电路生成任务", type = IMethodLog.InfoType.MOD)
	public String setCircuitTaskOn() {
		List<Map> list_map = new ArrayList<Map>();
		try {
			// 转化成JSONArray对象
			JSONArray jsonArray = JSONArray.fromObject(jsonString);
			for (Object obj : jsonArray) {
				// 转成SyncNeInfoModel对象
				JSONObject jsonObject = (JSONObject) obj;
				Map toMap = (Map) jsonObject;
				list_map.add(toMap);
			}

			circuitManagerService.setCircuitTaskOn(list_map);
			result.setReturnResult(CommonDefine.SUCCESS);
			result
					.setReturnMessage(getText(MessageCodeDefine.CIRCUIT_UPDATE_SUCCESS));
			resultObj = JSONObject.fromObject(result);
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
	 * 挂起电路生成任务
	 * 
	 * @return
	 */
	@IMethodLog(desc = "挂起电路生成任务", type = IMethodLog.InfoType.MOD)
	public String setCircuitTaskHold() {
		List<Map> list_map = new ArrayList<Map>();
		try {
			// 转化成JSONArray对象
			JSONArray jsonArray = JSONArray.fromObject(jsonString);
			for (Object obj : jsonArray) {
				// 转成SyncNeInfoModel对象
				JSONObject jsonObject = (JSONObject) obj;
				Map toMap = (Map) jsonObject;
				list_map.add(toMap);
			}

			circuitManagerService.setCircuitTaskHold(list_map);
			result.setReturnResult(CommonDefine.SUCCESS);
			result
					.setReturnMessage(getText(MessageCodeDefine.CIRCUIT_UPDATE_SUCCESS));
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			
		} catch (ParseException e) {
			result.setReturnResult(CommonDefine.FAILED);
			// result.setReturnMessage(e.getErrorMessage());
			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIR_EXCPT_UPDATE));
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	/**
	 * 检查是否有任务正在执行
	 * 
	 * @return
	 */
	public String checkTask() {
		List<Map> list_map = new ArrayList<Map>();
		try {
			// 转化成JSONArray对象
			JSONArray jsonArray = JSONArray.fromObject(jsonString);
			for (Object obj : jsonArray) {
				// 转成SyncNeInfoModel对象
				JSONObject jsonObject = (JSONObject) obj;
				Map toMap = (Map) jsonObject;
				list_map.add(toMap);
			}

			Map map = circuitManagerService.checkTask(list_map);
			resultObj = JSONObject.fromObject(map);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			// result.setReturnMessage(e.getErrorMessage());
			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIR_EXCPT_DO));
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	/** ********************************wangjian**end************************* */
	/** ################楚##河#############汉##界############################### */
	/** ********************************daihuijun**begin********************** */
	/**
	 * @author DaiHuijun 查询交叉连接
	 * @param connectRate
	 *            交叉连接速率 circuitState 交叉连接类别:1表示离散，2表示正常， 0表示全部状态
	 *            交叉连接改变：1新增，2删除，3不变，0全部 neId 网元id pageSize 一次取多少条数据
	 * @return RESULT_OBJ 包含记录总数totel，和分页查找的记录的json对象
	 */
	@IMethodLog(desc = "交叉连接查询")
	public String selectCrossConnect() {
		// 定义一个map类型变量用来存储查询条件
		Map<String, Object> map = new HashMap<String, Object>();
		List<Integer> list = new ArrayList();
		list.add(1);
		// 将传递过来的jsonString转化成一个map对象
		try {
			// 转化成JSONArray对象
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			Map toMap = (Map) jsonObject;
			// 给map赋值
			map.put("connectRate", connectRate);
			map.put("circuitState", circuitState);
			map.put("crossChange", crossChange);
			map.put("isFix", isFix);
			map.put("limit", limit);
			map.put("start", start);

			// 调用方法查询符合条件的记录
			Map<String, Object> cirInfo;
			cirInfo = circuitManagerService.selectCrossConnect(map, toMap);
			if (cirInfo.get("ReturnResult") != null) {
				result.setReturnResult(Integer.parseInt(cirInfo.get(
						"ReturnResult").toString()));
				result
						.setReturnMessage(cirInfo.get("ReturnMessage")
								.toString());
				resultObj = JSONObject.fromObject(result);
			}
			// 如果查询结果不为空的话，叫查询结果转化成json数组
			else
				resultObj = JSONObject.fromObject(cirInfo);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		// System.out.println(resultObj);
		return RESULT_OBJ;
	}

	/**
	 * 修改电路信息
	 * 
	 * @return
	 */
	@IMethodLog(desc = "修改电路", type = IMethodLog.InfoType.MOD)
	public String modifyCircuit() {
		try {
			List<Map> list_map = new ArrayList<Map>();

			// 转化成JSONArray对象
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			for (Object obj : jsonArray) {
				// 转成SyncNeInfoModel对象
				JSONObject jsonObject = (JSONObject) obj;
				Map map = (Map) jsonObject;
				list_map.add(map);
			}
			circuitManagerService.modifyCircuit(list_map);
			result.setReturnResult(CommonDefine.SUCCESS);
			result
					.setReturnMessage(getText(MessageCodeDefine.CIRCUIT_UPDATE_SUCCESS));
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	/**
	 * 端到端查询 jsonString = { "aNodeId" : aNodeId, "aNodeLevel" : aNodeLevel,
	 * "zNodeId" : zNodeId, "zNodeLevel" : zNodeLevel, "aLocationLevel" :
	 * aLocationLevel, "aLocationId" : aLocationId, "zLocationLevel" :
	 * zLocationLevel, "zLocationId" : zLocationId, "serviceType" : serviceType,
	 * "limit" : 200 };
	 * 
	 * @return
	 */
	@IMethodLog(desc = "端到端查询")
	public String getCircuitByPtp() {
		Map<String, Object> cirInfo;
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		Map map = (Map) jsonObject;
		map.put("start", start);
		try {
			// 如果查询结果不为空的话，将查询结果转化成json数组
			cirInfo = circuitManagerService.getCircuitByPtp(map);
			resultObj = JSONObject.fromObject(cirInfo);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	@IMethodLog(desc = "获取电路详细信息")
	public String getCirInfoById() {
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		Map<String, Object> cirInfo;
		Map map = (Map) jsonObject;
		try {
			// 根据所给电路id获取电路详细信息
			cirInfo = circuitManagerService.getCirInfoById(map);
			resultObj = JSONObject.fromObject(cirInfo);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "获取ptn电路详细信息")
	public String getPtnCirInfoById() {
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		Map<String, Object> cirInfo;
		Map map = (Map) jsonObject;
		try {
			// 根据所给电路id获取电路详细信息
			cirInfo = circuitManagerService.getPtnCirInfoById(map);
			resultObj = JSONObject.fromObject(cirInfo);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "获取otn电路详细信息")
	public String getOtnCirInfoById() {
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		Map<String, Object> cirInfo;
		Map map = (Map) jsonObject;
		try {
			// 根据所给电路id获取电路详细信息
			cirInfo = circuitManagerService.getOtnCirInfoById(map);
			resultObj = JSONObject.fromObject(cirInfo);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	/**
	 * 查询sdh类型电路的路由详情
	 * 
	 * @return
	 */
	@IMethodLog(desc = "获取路由信息")
	public String getCircuitRoute() {
		try {
			//配置过滤null值
			JsonConfig cfg = new JsonConfig();
			cfg.setJsonPropertyFilter(new PropertyFilter() {
				@Override
				public boolean apply(Object source, String name, Object value) {
					return value == null;
				}
			});
			if (vCircuit != 0) {
				if (type == 3) {
					// 如果是otn电路
					Map<String, Object> data = circuitManagerService
							.getOtnCircuitRoute(vCircuit);
					resultObj = JSONObject.fromObject(data,cfg);
				} else if (type == 4) {
					// 如果是otn电路
					Map<String, Object> data = circuitManagerService
							.getPtnCircuitRoute(vCircuit);
					resultObj = JSONObject.fromObject(data,cfg);
				}else {
					// 以太网或sdh电路
					// 获取参数中的关键字value对应的值，查询路由
					Map<String, Object> data = circuitManagerService
							.getCircuitRoute(vCircuit);
					resultObj = JSONObject.fromObject(data,cfg);
				}
			}
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	/**
	 * 获取网元内部路由
	 * 
	 * @return
	 */
	@IMethodLog(desc = "获取网元内部路由")
	public String getInnerRoute() {
		Map map = new HashMap();
		map.put("circuitId", cirNo);
		map.put("neId", aNodeId);
		try {
			Map data = circuitManagerService.getInnerRoute(map);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			e.printStackTrace();
		}
		return RESULT_OBJ;
	}

	/**
	 * 获取以太网子电路信息清单
	 * 
	 * @return
	 */
	@IMethodLog(desc = "获取以太网子电路清单")
	public String getSubCircuitInfo() {
		try {
			Map data = circuitManagerService.getSubCircuitInfo(parentCir);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	/**
	 * 获取设备的网元、板卡、端口名称
	 * 
	 * @return
	 */
	@IMethodLog(desc = "获取设备名称")
	public String getEquipmentName() {
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		Map map = (Map) jsonObject;
		try {
			Map data = circuitManagerService.getEquipmentName(map);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	@IMethodLog(desc = "相关性查询")
	public String selectCircuitAbout() {
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		Map toMap = (Map) jsonObject;
		toMap.put("start", start);
		try {

			Map data = circuitManagerService.selectCircuitAbout(toMap);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	/**
	 * 链路查询
	 */
	public String selectLinks() {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map.put("linkId", linkId);
			map.put("limit", limit);
			map.put("aNodeId", aNodeId);
			map.put("aNodeLevel", aNodeLevel);
			map.put("start", start);
			map.put("userId", sysUserId);
			map.put("linkType", linkType);
			if("cableId".equals(jsonString)){
				map.put("cableId",Integer.parseInt(processKey));
			}else if("fiberId".equals(jsonString)){
				map.put("fiberId",Integer.parseInt(processKey));
			}
			Map data = circuitManagerService.selectLinks(map);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	public String getLinksByIds(){
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("jsonString", jsonString);
		map.put("linkType", linkType);
		Map<String,Object> data=new HashMap<String,Object>();
		try {
			data = circuitManagerService.getLinksByIds(map);
			resultObj=JSONObject.fromObject(data);
		} catch (CommonException e) {
			e.printStackTrace();
			data.put("total", 0);
			data.put("rows",new ArrayList());
			resultObj=JSONObject.fromObject(data);
		}
		return RESULT_OBJ;
	}

	/**
	 * 删除链路
	 */
	public String deleteLinks() {// 如要一次删除多条电路返回1454版本
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			Map toMap = (Map) jsonObject;
			circuitManagerService.deleteLinks(toMap);
			result.setReturnResult(CommonDefine.SUCCESS);
			result
					.setReturnMessage(getText(MessageCodeDefine.CIRCUIT_DELETE_SUCCESS));
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	/**
	 * 添加链路
	 */
	public String addLink() {
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		Map toMap = (Map) jsonObject;
		try {
			Map map = circuitManagerService.manageLink(toMap);
			if ((Integer) map.get("returnResult") == 1) {
				result.setReturnResult(CommonDefine.SUCCESS);
				result
						.setReturnMessage(getText(MessageCodeDefine.LINK_ADD_SUCCESS));
			} else if ((Integer) map.get("returnResult") == 0) {
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage(map.get("returnMessage").toString());
			}
		} catch (CommonException e) {
			// TODO Auto-generated catch block
			result.setReturnResult(CommonDefine.FAILED);
			result
					.setReturnMessage(getText(MessageCodeDefine.CIRCUIT_UPDATE_FAILED));
		}
		resultObj = JSONObject.fromObject(result);
		return RESULT_OBJ;
	}

	/**
	 * 根据电路编号查找出所有的电路circuitNo-->circuitId
	 */
	public String getCircuitBycircuitNo() {
		try {// 定义一个map用来存放参数
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("vCircuit", vCircuit);
			Map data = circuitManagerService.getCircuitBycircuitNo(params);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	/**
	 * 根据电路编号查找出所有的电路circuitNo-->circuitId
	 */
	public String getPtnCircuitBycircuitNo() {
		try {// 定义一个map用来存放参数
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("vCircuit", vCircuit);
			Map data = circuitManagerService.getPtnCircuitBycircuitNo(params);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	/**
	 * 根据otn电路编号查找出所有的电路circuitNo-->circuitId
	 */
	public String getOtnCircuitBycircuitNo() {
		try {// 定义一个map用来存放参数
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("vCircuit", vCircuit);
			Map data = circuitManagerService.getOtnCircuitBycircuitNo(params);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	/**
	 * 将本地的.xls文件上传至服务器，从服务器中读取.xls文件，并将合法的数据添加到数据库中
	 * 
	 * @return
	 */
	public String importLinksExcel() {
		String fileType="";
		if(jsonString.endsWith(".xls")){
			 fileType=".xls";
		}else{
			 fileType=".xlsx";
		} 
		try {
			String path = CommonDefine.PATH_ROOT + CommonDefine.EXCEL.UPLOAD_PATH;
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy_MM_dd_HH-mm-ss");
			String fileName = formatter.format(new Date(System
					.currentTimeMillis()))+fileType;
			// importLinksExcel()将会返回一个含有键“result”的Map类型，“result”中包含了执行是否成功的判断
			Map map = circuitManagerService.importLinksExcel(uploadFile,
					fileName, path);
			if (map.get("result").toString().equals(""))
				map.put("result", "导入成功");
			resultObj = JSONObject.fromObject(map);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_UPLOAD;
	}

	/**
	 * 获取电路路由拓扑图（sdh）
	 * 
	 * @return
	 */
	public String getRouteTopo() {
		try {
			if (vCircuit != 0) {
				// 获取参数中的关键字value对应的值，查询路由
				Map<String, Object> data = circuitManagerService
						.getRouteTopo(vCircuit);
				resultObj = JSONObject.fromObject(data);
			}
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	/**
	 * 获取电路路由拓扑图（ptn）
	 * 
	 * @return
	 */
	public String getPtnRouteTopo() {
		try {
			if (vCircuit != 0) {
				// 获取参数中的关键字value对应的值，查询路由
				Map<String, Object> data = circuitManagerService
						.getPtnRouteTopo(vCircuit);
				resultObj = JSONObject.fromObject(data);
			}
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	/**
	 * 获取电路路由拓扑图（otn）
	 * 
	 * @return
	 */
	public String getOtnRouteTopo() {
		try {
			if (vCircuit != 0) {
				// 获取参数中的关键字value对应的值，查询路由
				Map<String, Object> data = circuitManagerService
						.getOtnRouteTopo(vCircuit);
				resultObj = JSONObject.fromObject(data);
			}
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	/**
	 * 获取otn电路的路由详情
	 * 
	 * @return
	 */
	@IMethodLog(desc = "获取otn路由详情")
	public String getOtnCircuitRoute() {
		try {
			// 当电路id不为空时
			if (vCircuit != 0) {
				// 查询指定电路的路由详情
				Map<String, Object> data = circuitManagerService
						.getOtnCircuitRoute(vCircuit);
				resultObj = JSONObject.fromObject(data);
			}
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	/**
	 * 获取告警信息
	 */
	public String getCurrentAlarmForCircuit(){
		Map result=circuitManagerService.getCurrentAlarmForCircuit(jsonString,start);
		resultObj=JSONObject.fromObject(result);
		return RESULT_OBJ;
	}
	//获取指定电路号的所有电路经过的网元和端口（排除两端）
	public String getNeAndPortByCirNo(){
			try {
				resultObj=JSONObject.fromObject(circuitManagerService.getNeAndPortByCirNo(vCircuit,type));
			} catch (CommonException e) {
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage(e.getErrorMessage());
				resultObj = JSONObject.fromObject(result);
			}
			return RESULT_OBJ;
	}
	/**
	 * 告警模块相关性
	 * @return
	 */
	@IMethodLog(desc = "告警模块相关性查询")
	public String getAboutCircuit(){
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		Map toMap = (Map) jsonObject;
		toMap.put("start", start);
		try {

			Map data = circuitManagerService.getAboutCircuit(toMap);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	@IMethodLog(desc = "光缆光纤的关联")
	public String relateFiber(){   
		try {  
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			Map map = (Map) jsonObject;
			circuitManagerService.relateFiber(map); 
			result.setReturnMessage("关联成功");
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "删除和光缆光纤的关联", type = IMethodLog.InfoType.DELETE)
	public String cancelRelateFiber(){   
		try {  
			JSONArray jsonArray = JSONArray.fromObject(jsonString); 
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			for(Object o : jsonArray){
				JSONObject jsonObject = (JSONObject) o; 
				list.add((Map) jsonObject);
			} 
			circuitManagerService.cancelRelateFiber(list); 
			result.setReturnMessage("删除关联成功");
			result.setReturnResult(CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "关联前的初始化信息")
	public String getRelateInfo(){  
		Map<String,Object> data= new HashMap<String,Object>();
		try {  
			data = circuitManagerService.getRelateInfo(linkId,aNodeId,zNodeId);  
			data.put("returnResult",CommonDefine.SUCCESS);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	/** ********************************daihuijun**end************************ */

	/** ************************get and set ******************************** */

	public Integer getEmsGroupId() {
		return emsGroupId;
	}

	public void setEmsGroupId(Integer emsGroupId) {
		this.emsGroupId = emsGroupId;
	}

	public String getConnectRate() {
		return connectRate;
	}

	public void setConnectRate(String connectRate) {
		this.connectRate = connectRate;
	}

	public int getCircuitState() {
		return circuitState;
	}

	public void setCircuitState(int circuitState) {
		this.circuitState = circuitState;
	}

	public int getCrossChange() {
		return crossChange;
	}

	public void setCrossChange(int crossChange) {
		this.crossChange = crossChange;
	}

	public int getIsFix() {
		return isFix;
	}

	public void setIsFix(int isFix) {
		this.isFix = isFix;
	}

	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}

	public int getANodeId() {
		return aNodeId;
	}

	public void setANodeId(int aNodeId) {
		this.aNodeId = aNodeId;
	}

	public int getANodeLevel() {
		return aNodeLevel;
	}

	public void setANodeLevel(int aNodeLevel) {
		this.aNodeLevel = aNodeLevel;
	}

	public int getZNodeId() {
		return zNodeId;
	}

	public void setZNodeId(int zNodeId) {
		this.zNodeId = zNodeId;
	}

	public int getZNodeLevel() {
		return zNodeLevel;
	}

	public void setZNodeLevel(int zNodeLevel) {
		this.zNodeLevel = zNodeLevel;
	}

	public int getParentCir() {
		return parentCir;
	}

	public void setParentCir(int parentCir) {
		this.parentCir = parentCir;
	}

	public long getLinkId() {
		return linkId;
	}

	public int getVCircuit() {
		return vCircuit;
	}

	public void setLinkId(long linkId) {
		this.linkId = linkId;
	}

	public void setVCircuit(int vCircuit) {
		this.vCircuit = vCircuit;
	}

	public File getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(File uploadFile) {
		this.uploadFile = uploadFile;
	}

	public String getUploadFileFileName() {
		return uploadFileFileName;
	}

	public void setUploadFileFileName(String uploadFileFileName) {
		this.uploadFileFileName = uploadFileFileName;
	}

	public String getUploadFileContentType() {
		return uploadFileContentType;
	}

	public void setUploadFileContentType(String uploadFileContentType) {
		this.uploadFileContentType = uploadFileContentType;
	}

	public String getDevMode() {
		return devMode;
	}

	public void setDevMode(String devMode) {
		this.devMode = devMode;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getProcessKey() {
		return processKey;
	}

	public void setProcessKey(String processKey) {
		this.processKey = processKey;
	}
	public String getCirNo() {
		return cirNo;
	}

	public void setCirNo(String cirNo) {
		this.cirNo = cirNo;
	}

	public int getLinkType() {
		return linkType;
	}

	public void setLinkType(int linkType) {
		this.linkType = linkType;
	}

}
