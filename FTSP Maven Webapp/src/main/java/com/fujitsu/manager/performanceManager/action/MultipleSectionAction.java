package com.fujitsu.manager.performanceManager.action;

import com.fujitsu.IService.IMethodLog;
import com.fujitsu.IService.IMultipleSectionManagerService;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.handler.MessageHandler;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author wangjian
 * 
 */
public class MultipleSectionAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Resource
	public IMultipleSectionManagerService pmMultipleSectionManagerService;

	private int emsGroupId = 0; // 网管groupid
	private int emsId;
	private String jsonString;
	private int isAll; // 1 是 0 否
	private File uploadFile;
	private int mulId;
	private int direction;
	private int neId;
	private int cutoverFlag;
	private String filename;
	private int startPtp;

	/**
	 * 获取网管分组
	 */
	@IMethodLog(desc = "获取网管分组")
	public String selectAllGroup() {
		try {
			List<Map> data = pmMultipleSectionManagerService.selectAllGroup();
			resultArray = JSONArray.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			// result.setReturnMessage(e.getErrorMessage());
			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIR_EXCPT_SELECT));
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_ARRAY;

	}

	/**
	 * 根据网管分组id获取具体网管
	 */
	@IMethodLog(desc = "根据网管分组id获取具体网管")
	public String selectAllEMS() {
		try {
			Map<String, Object> data = pmMultipleSectionManagerService
					.selectAllEMS(emsGroupId, isAll);
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
	 * 查询干线信息
	 */
	@IMethodLog(desc = "查询干线信息")
	public String selectTrunkLine() {
		try {
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			// 转成SyncNeInfoModel对象
			JSONObject jsonObject = (JSONObject) jsonArray.get(0);

			Map select = (Map) jsonObject;
			select.put("start", start);
			Map<String, Object> data = pmMultipleSectionManagerService
					.selectTrunkLine(select);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			// result.setReturnMessage(e.getErrorMessage());
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
	 * 新增一条干线
	 */
	@IMethodLog(desc = "新增一条干线", type = IMethodLog.InfoType.MOD)
	public String addTrunkLine() {
		try {
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			// 转成SyncNeInfoModel对象
			JSONObject jsonObject = (JSONObject) jsonArray.get(0);

			Map insert = (Map) jsonObject;
			result = pmMultipleSectionManagerService.addTrunkLine(insert);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			// result.setReturnMessage(e.getErrorMessage());
			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIR_EXCPT_ADD));
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}

	/**
	 * 删除干线
	 */
	@IMethodLog(desc = "删除干线", type = IMethodLog.InfoType.DELETE)
	public String deleteTrunkLine() {
		try {
			List<Map> listMap = new ArrayList<Map>();
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			for (Object obj : jsonArray) {
				// 转成SyncNeInfoModel对象
				JSONObject jsonObject = (JSONObject) obj;
				Map map = (Map) jsonObject;
				listMap.add(map);
			}
			result = pmMultipleSectionManagerService.deleteTrunkLine(listMap);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			if (e.getErrorCode() == MessageCodeDefine.PM_EXIST_SECTION) {
				result.setReturnMessage(e.getErrorMessage());
			} else {
				result.setReturnMessage(MessageHandler
						.getErrorMessage(MessageCodeDefine.CIR_EXCPT_DELETE));
			}
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}

	/**
	 * 修改干线
	 */
	@IMethodLog(desc = "修改干线", type = IMethodLog.InfoType.MOD)
	public String modifyTrunkLine() {
		try {
			List<Map> listMap = new ArrayList<Map>();
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			for (Object obj : jsonArray) {
				// 转成SyncNeInfoModel对象
				JSONObject jsonObject = (JSONObject) obj;
				Map map = (Map) jsonObject;
				listMap.add(map);
			}
			result = pmMultipleSectionManagerService.modifyTrunkLine(listMap);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			if (e.getErrorCode() == MessageCodeDefine.PM_EXIST) {
				result.setReturnMessage(e.getErrorMessage());
			} else {
				result.setReturnMessage(MessageHandler
						.getErrorMessage(MessageCodeDefine.CIR_EXCPT_UPDATE));
			}
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}

	/**
	 * 查询光复用段
	 */
	@IMethodLog(desc = "查询光复用段")
	public String selectMultipleSection() {
		try {
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			// 转成SyncNeInfoModel对象
			JSONObject jsonObject = (JSONObject) jsonArray.get(0);
			Map map = (Map) jsonObject;

			Map<String, Object> data = pmMultipleSectionManagerService
					.selectMultipleSection(map);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			System.out.println(e.getErrorCode());
			if (e.getErrorCode() == MessageCodeDefine.USER_LOGIN_AGAIN) {
				result.setReturnMessage(e.getErrorMessage());
			} else {
				result.setReturnMessage(MessageHandler
						.getErrorMessage(MessageCodeDefine.CIR_EXCPT_UPDATE));
			}
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}

	/**
	 * 新增光复用段
	 */
	@IMethodLog(desc = "新增光复用段", type = IMethodLog.InfoType.MOD)
	public String addMultipleSection() {
		try {
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			// 转成SyncNeInfoModel对象
			JSONObject jsonObject = (JSONObject) jsonArray.get(0);
			Map map = (Map) jsonObject;

			result = pmMultipleSectionManagerService.addMultipleSection(map);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);

			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIR_EXCPT_UPDATE));

			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}

	/**
	 * 删除光复用段
	 */
	@IMethodLog(desc = "删除光复用段", type = IMethodLog.InfoType.DELETE)
	public String deleteMultipleSection() {
		try {
			List<Map> listMap = new ArrayList<Map>();
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			for (Object obj : jsonArray) {
				// 转成SyncNeInfoModel对象
				JSONObject jsonObject = (JSONObject) obj;
				Map map = (Map) jsonObject;
				listMap.add(map);
			}
			pmMultipleSectionManagerService.deleteMultipleSection(listMap);
			result.setReturnResult(CommonDefine.SUCCESS);

			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIRCUIT_DELETE_SUCCESS));

			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);

			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIR_EXCPT_DELETE));

			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}

	/**
	 * 修改光复用段
	 */
	@IMethodLog(desc = "修改光复用段", type = IMethodLog.InfoType.MOD)
	public String modifyMultipleSection() {
		try {
			List<Map> listMap = new ArrayList<Map>();
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			for (Object obj : jsonArray) {
				// 转成SyncNeInfoModel对象
				JSONObject jsonObject = (JSONObject) obj;
				Map map = (Map) jsonObject;
				listMap.add(map);
			}
			result = pmMultipleSectionManagerService
					.modifyMultipleSection(listMap);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			if (e.getErrorCode() == MessageCodeDefine.PM_SECTION_EXIST) {
				result.setReturnMessage(e.getErrorMessage());
			} else {
				result.setReturnMessage(MessageHandler
						.getErrorMessage(MessageCodeDefine.CIR_EXCPT_UPDATE));
			}
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}

	/**
	 * 查询光复用段所包含的网元
	 */
	@IMethodLog(desc = "查询光复用段")
	public String selectMultipleSectionNe() {
		try {
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			// 转成SyncNeInfoModel对象
			JSONObject jsonObject = (JSONObject) jsonArray.get(0);
			Map map = (Map) jsonObject;

			Map<String, Object> data = pmMultipleSectionManagerService
					.selectMultipleSectionNe(map);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);

			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIR_EXCPT_UPDATE));

			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}

	/**
	 * 根据网元id查询网元相关信息
	 */
	@IMethodLog(desc = "根据网元id查询网元相关信息")
	public String selectByNeId() {
		try {
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			// 转成SyncNeInfoModel对象
			JSONObject jsonObject = (JSONObject) jsonArray.get(0);
			Map map = (Map) jsonObject;

			Map<String, Object> data = pmMultipleSectionManagerService
					.selectByNeId(map);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);

			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIR_EXCPT_UPDATE));

			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}

	/**
	 * 保存正向光复用段网元设置
	 */
	@IMethodLog(desc = "保存正向光复用段网元设置", type = IMethodLog.InfoType.MOD)
	public String saveNeForward() {
		try {
			List<Map> listMap = new ArrayList<Map>();
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			for (Object obj : jsonArray) {
				// 转成SyncNeInfoModel对象
				JSONObject jsonObject = (JSONObject) obj;
				Map map = (Map) jsonObject;
				listMap.add(map);
			}

			pmMultipleSectionManagerService.saveNeForward(listMap, mulId,
					direction);
			result.setReturnResult(CommonDefine.TRUE);

			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIRCUIT_UPDATE_SUCCESS));
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);

			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIR_EXCPT_UPDATE));

			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}

	/**
	 * 根据ptpId获取ptpName
	 */
	@IMethodLog(desc = "根据ptpId获取ptpName")
	public String selecrPtpName() {
		try {
			List<Map> listMap = new ArrayList<Map>();
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			for (Object obj : jsonArray) {
				// 转成SyncNeInfoModel对象
				JSONObject jsonObject = (JSONObject) obj;
				Map map = (Map) jsonObject;
				listMap.add(map);
			}
			Map map = pmMultipleSectionManagerService.selecrPtpName(listMap);
			resultObj = JSONObject.fromObject(map);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIR_EXCPT_SELECT));
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	/**
	 * 查询去重的光放型号
	 */
	@IMethodLog(desc = "查询去重的光放型号")
	public String selectMultipleModel() {
		try {

			Map<String, Object> data = pmMultipleSectionManagerService
					.selectMultipleModel();
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);

			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIR_EXCPT_SELECT));

			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}

	/**
	 * 查询光放型号
	 */
	@IMethodLog(desc = "查询光放型号")
	public String selectModelType() {
		try {
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			// 转成SyncNeInfoModel对象
			JSONObject jsonObject = (JSONObject) jsonArray.get(0);
			Map map = (Map) jsonObject;

			Map<String, Object> data = pmMultipleSectionManagerService
					.selectModelType(map);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);

			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIR_EXCPT_SELECT));

			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}

	/**
	 * 查询光放型号数据
	 */
	@IMethodLog(desc = "查询光放型号数据")
	public String selectStandOptVal() {
		try {
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			// 转成SyncNeInfoModel对象
			JSONObject jsonObject = (JSONObject) jsonArray.get(0);
			Map map = (Map) jsonObject;
			map.put("start", start);
			Map<String, Object> data = pmMultipleSectionManagerService
					.selectStandOptVal(map);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);

			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIR_EXCPT_SELECT));

			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}

	/**
	 * 新增光放段单元
	 */
	@IMethodLog(desc = "新增光放单元", type = IMethodLog.InfoType.MOD)
	public String addStandOptVal() {
		try {
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			// 转成SyncNeInfoModel对象
			JSONObject jsonObject = (JSONObject) jsonArray.get(0);
			Map map = (Map) jsonObject;

			result = pmMultipleSectionManagerService.addStandOptVal(map);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);

			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIR_EXCPT_UPDATE));

			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}

	/**
	 * 删除光放单元
	 */
	@IMethodLog(desc = "删除光放单元", type = IMethodLog.InfoType.DELETE)
	public String deleteStandOptVal() {
		try {
			List<Map> listMap = new ArrayList<Map>();
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			for (Object obj : jsonArray) {
				// 转成SyncNeInfoModel对象
				JSONObject jsonObject = (JSONObject) obj;
				Map map = (Map) jsonObject;
				listMap.add(map);
			}
			result = pmMultipleSectionManagerService.deleteStandOptVal(listMap);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			if (e.getErrorCode() == MessageCodeDefine.PM_STANDOPTVAL_IN_SECTION) {
				result.setReturnMessage(e.getErrorMessage());
			} else {
				result.setReturnMessage(MessageHandler
						.getErrorMessage(MessageCodeDefine.CIR_EXCPT_DELETE));
			}
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}

	/**
	 * 修改光放单元
	 */
	@IMethodLog(desc = "修改光放单元", type = IMethodLog.InfoType.MOD)
	public String modifyStandOptVal() {
		try {
			List<Map> listMap = new ArrayList<Map>();
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			for (Object obj : jsonArray) {
				// 转成SyncNeInfoModel对象
				JSONObject jsonObject = (JSONObject) obj;
				Map map = (Map) jsonObject;
				listMap.add(map);
			}
			result = pmMultipleSectionManagerService.modifyStandOptVal(listMap);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			if (e.getErrorCode() == MessageCodeDefine.PM_EXIST) {
				result.setReturnMessage(e.getErrorMessage());
			} else {
				result.setReturnMessage(MessageHandler
						.getErrorMessage(MessageCodeDefine.CIR_EXCPT_UPDATE));
			}
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}

	/**
	 * 根据网元id查询光复用段ptp路由
	 */
	@IMethodLog(desc = "根据网元id查询光复用段ptp路由")
	public String selectPtpRouteList() {
		try {
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			// 转成SyncNeInfoModel对象
			JSONObject jsonObject = (JSONObject) jsonArray.get(0);
			Map map = (Map) jsonObject;

			Map<String, Object> data = pmMultipleSectionManagerService
					.selectPtpRouteList(map);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);

			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIR_EXCPT_UPDATE));

			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}

	/**
	 * 根据网元id查询光复用段ptp路由
	 */
	@IMethodLog(desc = "根据网元id查询光复用段ptp路由")
	public String selectSubPtpRouteList() {
		try {
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			// 转成SyncNeInfoModel对象
			JSONObject jsonObject = (JSONObject) jsonArray.get(0);
			Map map = (Map) jsonObject;

			Map<String, Object> data = pmMultipleSectionManagerService
					.selectSubPtpRouteList(map);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);

			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIR_EXCPT_UPDATE));

			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}

	/**
	 * 保存正向光复用段ptp路由设置
	 */
	@IMethodLog(desc = "保存正向光复用段ptp路由设置", type = IMethodLog.InfoType.MOD)
	public String savePtpForward() {
		try {
			List<Map> listMap = new ArrayList<Map>();
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			for (Object obj : jsonArray) {
				// 转成SyncNeInfoModel对象
				JSONObject jsonObject = (JSONObject) obj;
				Map map = (Map) jsonObject;
				listMap.add(map);
			}

			pmMultipleSectionManagerService.savePtpForward(listMap, neId,
					direction);
			result.setReturnResult(CommonDefine.TRUE);

			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIRCUIT_UPDATE_SUCCESS));
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);

			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIR_EXCPT_UPDATE));

			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}

	/**
	 * 光复用段排序
	 */
	@IMethodLog(desc = "光复用段排序")
	public String sortMultipleSection() {
		try {
			List<Map> listMap = new ArrayList<Map>();
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			for (Object obj : jsonArray) {
				// 转成SyncNeInfoModel对象
				JSONObject jsonObject = (JSONObject) obj;
				Map map = (Map) jsonObject;
				listMap.add(map);
			}
			result = pmMultipleSectionManagerService
					.sortMultipleSection(listMap);
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			if (e.getErrorCode() == MessageCodeDefine.PM_SECTION_EXIST) {
				result.setReturnMessage(e.getErrorMessage());
			} else {
				result.setReturnMessage(MessageHandler
						.getErrorMessage(MessageCodeDefine.CIR_EXCPT_UPDATE));
			}
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}

	/**
	 * 查询光复用段相关信息
	 */
	@IMethodLog(desc = "查询光复用段相关信息")
	public String selectMultipleAbout() {
		try {
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			// 转成SyncNeInfoModel对象
			JSONObject jsonObject = (JSONObject) jsonArray.get(0);
			Map map = (Map) jsonObject;

			Map<String, Object> data = pmMultipleSectionManagerService
					.selectMultipleAbout(map);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);

			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIR_EXCPT_SELECT));

			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}

	/**
	 * 查询光复用段详细信息
	 */
	@IMethodLog(desc = "查询光复用段详细信息")
	public String selectMultiplePtpRoute() {
		try {
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			// 转成SyncNeInfoModel对象
			JSONObject jsonObject = (JSONObject) jsonArray.get(0);
			Map map = (Map) jsonObject;

			map.put("start", start);
			Map<String, Object> data = pmMultipleSectionManagerService
					.selectMultiplePtpRoute(map);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);

			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIR_EXCPT_SELECT));

			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}

	/**
	 * 保存光复用段的详细设置
	 */
	@IMethodLog(desc = "保存光复用段的详细设置", type = IMethodLog.InfoType.MOD)
	public String saveMultipleDetail() {
		try {
			List<Map> listMap = new ArrayList<Map>();
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			for (Object obj : jsonArray) {
				// 转成SyncNeInfoModel对象
				JSONObject jsonObject = (JSONObject) obj;
				Map map = (Map) jsonObject;
				listMap.add(map);
			}

			pmMultipleSectionManagerService.saveMultipleDetail(listMap);
			result.setReturnResult(CommonDefine.TRUE);

			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIRCUIT_UPDATE_SUCCESS));
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);

			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIR_EXCPT_UPDATE));

			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}

	/**
	 * 同步光复用段性能
	 */
	@IMethodLog(desc = "同步光复用段性能")
	public String sycPmByMultiple() {
		try {
			List<Map> listMap = new ArrayList<Map>();
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			for (Object obj : jsonArray) {
				// 转成SyncNeInfoModel对象
				JSONObject jsonObject = (JSONObject) obj;
				Map map = (Map) jsonObject;
				listMap.add(map);
			}

			pmMultipleSectionManagerService.sycPmByMultiple(listMap,
					cutoverFlag);
			result.setReturnResult(CommonDefine.TRUE);

			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIRCUIT_SYC_SUCCESS));
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);

			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIRCUIT_SYC_FAILED));

			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}

	/**
	 * 根据端口同步光复用段性能
	 */
	@IMethodLog(desc = "根据端口同步光复用段性能")
	public String sycPmByMultipleByPort() {
		try {
			List<Map> listMap = new ArrayList<Map>();
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			for (Object obj : jsonArray) {
				// 转成SyncNeInfoModel对象
				JSONObject jsonObject = (JSONObject) obj;
				Map map = (Map) jsonObject;
				listMap.add(map);
			}

			pmMultipleSectionManagerService.sycPmByMultipleByPort(listMap);
			result.setReturnResult(CommonDefine.TRUE);

			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIRCUIT_SYC_SUCCESS));
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);

			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIRCUIT_SYC_FAILED));

			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}

	/**
	 * 同步历史性能
	 */
	@IMethodLog(desc = "同步历史性能")
	public String sycPmHistory() {
		try {
			List<Map> listMap = new ArrayList<Map>();
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			// 转成SyncNeInfoModel对象
			JSONObject jsonObject = (JSONObject) jsonArray.get(0);
			Map map = (Map) jsonObject;

			pmMultipleSectionManagerService.sycPmHistory(map);
			result.setReturnResult(CommonDefine.TRUE);

			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.SELECT_SUCCESS));
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);

			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.SELECT_FAILED));

			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;

	}

	@IMethodLog(desc = "导出光复用段详细信息")
	public String ecportSecDetail() {

		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			// 定义一个map类型变量用来存储查询条件
			Map<String, Object> map = (Map) JSONObject.toBean(jsonObject,
					Map.class);
			if (cutoverFlag != 0) {
				map.put("cutoverFlag", cutoverFlag);
				map.put("filename", filename);
			}
			CommonResult data = pmMultipleSectionManagerService
					.ecportSecDetail(map);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	@IMethodLog(desc = "导出光复用段所有信息")
	public String exportAllInformation() {

		try {
			List<Map> listMap = new ArrayList<Map>();
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			for (Object obj : jsonArray) {
				// 转成SyncNeInfoModel对象
				JSONObject jsonObject = (JSONObject) obj;
				Map map = (Map) jsonObject;
				listMap.add(map);
			}
			CommonResult data = pmMultipleSectionManagerService
					.exportAllInformation(listMap);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}

	/**
	 * 导入稽核电路路由信息
	 * 
	 * @return
	 */
	@IMethodLog(desc = "导入干线及光复用段信息")
	public String UploadTrunkLine() {

		try {
			Map rlt;
			// 生成文件路径以及文件名
			String path = CommonDefine.PATH_ROOT + CommonDefine.EXCEL.UPLOAD_PATH;
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy_MM_dd_HH-mm-ss");
			String fileName = formatter.format(new Date(System
					.currentTimeMillis()));
			if (jsonString.endsWith(".xlsx")) {
				fileName += ".xlsx";
			} else if (jsonString.endsWith(".xls")) {
				fileName += ".xls";
			}
			// 转存上传的文件
			rlt = pmMultipleSectionManagerService.uploadTrunkLine(uploadFile,
					fileName, path, emsId);

			resultObj = JSONObject.fromObject(rlt);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			// result.setReturnMessage(e.getErrorMessage());
			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIR_EXCPT_IMPORT));
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_UPLOAD;
	}

	/**
	 * 根据起始端口，光复用段自动生成
	 * 
	 * @return
	 */
	@IMethodLog(desc = "光复用段自动生成", type = IMethodLog.InfoType.MOD)
	public String autoCreateRoute() {
		try {
			// 根据传递的端口(startPtp),为指定的光复用段(mulId)，生成指定方向(direction)的路由。
			result = pmMultipleSectionManagerService.autoCreateRoute(mulId,
					direction, startPtp);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
		}
		resultObj = JSONObject.fromObject(result);
		return RESULT_OBJ;
	}

	/**
	 * 检测指定光复用段上是否已存在路由记录
	 * 
	 * @return
	 */
	public String hasRecord() {
		try {
			Boolean hasRecord = pmMultipleSectionManagerService.hasRecord(
					mulId, direction);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(hasRecord.toString());
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
		}
		resultObj = JSONObject.fromObject(result);
		return RESULT_OBJ;
	}

	/**
	 * 导入稽核电路路由信息
	 * 
	 * @return
	 */
	@IMethodLog(desc = "导入光复用段全部信息")
	public String UploadSectionAll() {

		try {
			Map rlt;
			// 生成文件路径以及文件名
			String path = CommonDefine.PATH_ROOT + CommonDefine.EXCEL.UPLOAD_PATH;
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy_MM_dd_HH-mm-ss");
			String fileName = formatter.format(new Date(System
					.currentTimeMillis()));
			if (jsonString.endsWith(".xlsx")) {
				fileName += ".xlsx";
			} else if (jsonString.endsWith(".xls")) {
				fileName += ".xls";
			}
			// 转存上传的文件
			rlt = pmMultipleSectionManagerService.uploadSectionAll(uploadFile,
					fileName, path);

			resultObj = JSONObject.fromObject(rlt);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			// result.setReturnMessage(MessageHandler
			// .getErrorMessage(MessageCodeDefine.CIR_EXCPT_IMPORT));
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_UPLOAD;
	}

//	public String test() {
//		try {
//			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//			Map<String, List<Map<String, Object>>> data = pmMultipleSectionManagerService
//					.getExportPmInfo(pmMultipleSectionManagerService
//							.getPmFromTaskId(274, Calendar.DAY_OF_YEAR,
//									format.parse("2014-05-15"),
//									format.parse("2014-05-16")));
//			System.out.println(data.toString());
//			resultObj = JSONObject.fromObject(data.toString());
//
//		} catch (CommonException e) {
//			// TODO Auto-generated catch block
//			result.setReturnResult(CommonDefine.FAILED);
//			result.setReturnMessage(e.getErrorMessage());
//			resultObj = JSONObject.fromObject(result);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return RESULT_OBJ;
//	}

	/** ***********************get and set*********************************** */

	public int getEmsGroupId() {
		return emsGroupId;
	}

	public void setEmsGroupId(int emsGroupId) {
		this.emsGroupId = emsGroupId;
	}

	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}

	public int getIsAll() {
		return isAll;
	}

	public void setIsAll(int isAll) {
		this.isAll = isAll;
	}

	public File getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(File uploadFile) {
		this.uploadFile = uploadFile;
	}

	public int getEmsId() {
		return emsId;
	}

	public void setEmsId(int emsId) {
		this.emsId = emsId;
	}

	public int getMulId() {
		return mulId;
	}

	public void setMulId(int mulId) {
		this.mulId = mulId;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getNeId() {
		return neId;
	}

	public void setNeId(int neId) {
		this.neId = neId;
	}

	public int getCutoverFlag() {
		return cutoverFlag;
	}

	public void setCutoverFlag(int cutoverFlag) {
		this.cutoverFlag = cutoverFlag;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getStartPtp() {
		return startPtp;
	}

	public void setStartPtp(int startPtp) {
		this.startPtp = startPtp;
	}

}
