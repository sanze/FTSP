package com.fujitsu.manager.resourceManager.action;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.fujitsu.IService.IMethodLog;
import com.fujitsu.IService.IResourceCircuitManagerService;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.handler.MessageHandler;

public class ResourceCircuitAction extends AbstractAction {

	/**
	 * @author wangjian
	 */
	private static final long serialVersionUID = 1L;

	@Resource
	public IResourceCircuitManagerService resourceCircuitManagerService;

	private String jsonString;
	private String resCirName;
	private String resCirId;
	private String routeNum;
	private File uploadFile;
	private File uploadFileNc;
	private File uploadFileNcWDM;
	

	/**
	 * 查询网元与资源系统的对应关系
	 * 
	 * @return
	 */
	@IMethodLog(desc = "查询资源网元")
	public String getNeRelation() {

		try {

			Map map = new HashMap();

			// 转化成JSONArray对象
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			// 转成SyncNeInfoModel对象
			JSONObject jsonObject = (JSONObject) jsonArray.get(0);

			map = (Map) jsonObject;

			Map<String, Object> ma = resourceCircuitManagerService
					.getNeRelation(map, start, limit);
			resultObj = JSONObject.fromObject(ma);
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
	 * 修改网元对应关系表
	 * 
	 * @return
	 */
	@IMethodLog(desc = "修改资源网元", type = IMethodLog.InfoType.MOD)
	public String modifyResourceNe() {

		List<Map> list_map = new ArrayList<Map>();
		try {
			// 转化成JSONArray对象
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			for (Object obj : jsonArray) {
				// 转成SyncNeInfoModel对象
				JSONObject jsonObject = (JSONObject) obj;
				Map map = (Map) jsonObject;
				list_map.add(map);
			}
			result = resourceCircuitManagerService.modifyResourceNe(list_map);

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
	 * 删除网元关系
	 * 
	 * @return
	 */
	@IMethodLog(desc = "删除资源网元", type = IMethodLog.InfoType.DELETE)
	public String deleteResourceNe() {
		List<Map> list_map = new ArrayList<Map>();
		try {
			// 转化成JSONArray对象
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			for (Object obj : jsonArray) {
				// 转成SyncNeInfoModel对象
				JSONObject jsonObject = (JSONObject) obj;
				Map map = (Map) jsonObject;
				list_map.add(map);
			}
			resourceCircuitManagerService.deleteResourceNe(list_map);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIRCUIT_DELETE_SUCCESS));
			resultObj = JSONObject.fromObject(result);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			// result.setReturnMessage(e.getErrorMessage());
			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIR_EXCPT_DELETE));
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	/**
	 * 新增网元关系
	 * 
	 * @return
	 */
	@IMethodLog(desc = "新增资源网元", type = IMethodLog.InfoType.MOD)
	public String addResourceNe() {
		try {
			// 转化成JSONArray对象
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			// 转成SyncNeInfoModel对象
			JSONObject jsonObject = (JSONObject) jsonArray.get(0);
			Map map = (Map) jsonObject;

			result = resourceCircuitManagerService.addResourceNe(map);
			// result.setReturnResult(CommonDefine.SUCCESS);
			// result.setReturnMessage(MessageHandler
			// .getErrorMessage(MessageCodeDefine.CIRCUIT_INSERT_SUCCESS));
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
	 * 查询资源电路
	 * 
	 * @return
	 */
	@IMethodLog(desc = "资源电路查询")
	public String selectResourceCircuit() {
		try {
			Map<String, Object> ma = resourceCircuitManagerService
					.selectResourceCircuit(resCirName, start, limit);
			resultObj = JSONObject.fromObject(ma);
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
	 * 统计电路稽核结果
	 * 
	 * @return
	 */
	@IMethodLog(desc = "统计电路稽核结果")
	public String resultCount() {
		try {
			Map<String, Object> ma = resourceCircuitManagerService
					.resultCount();
			resultObj = JSONObject.fromObject(ma);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			// result.setReturnMessage(e.getErrorMessage());
			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIR_EXCPT_DO));
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	/**
	 * 查询稽核电路路由信息
	 * 
	 * @return
	 */
	public String selectResCirRoute() {
		try {
			Map<String, Object> ma = resourceCircuitManagerService
					.selectResCirRoute(resCirId, routeNum);
			resultObj = JSONObject.fromObject(ma);
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
	 * 获取单条稽核电路信息
	 * 
	 * @return
	 */
	public String getSingleCir() {
		try {
			Map ma = resourceCircuitManagerService.getSingleCir(resCirId);
			resultObj = JSONObject.fromObject(ma);
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
	 * 跟据稽核电路与id查询出ftsp电路的路径
	 * 
	 * @return
	 */
	public String getFtspRouteNumber() {
		try {
			Map ma = resourceCircuitManagerService.getFtspRouteNumber(resCirId);
			resultObj = JSONObject.fromObject(ma);
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
	 * 获取电路的路由信息
	 * 
	 * @return
	 */
	public String selectCircuitRoute() {
		try {
			Map ma = resourceCircuitManagerService.selectCircuitRoute(resCirId);
			resultObj = JSONObject.fromObject(ma);
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
	 * 电路比对
	 * 
	 * @return
	 */
	public String compareCircuit() {
		List<Map> list_map = new ArrayList<Map>();
		try {
			// 转化成JSONArray对象
			JSONArray jsonArray = JSONArray.fromObject(jsonString);

			for (Object obj : jsonArray) {
				// 转成SyncNeInfoModel对象
				JSONObject jsonObject = (JSONObject) obj;
				Map map = (Map) jsonObject;
				list_map.add(map);
			}
			Map ma = resourceCircuitManagerService.compareCircuit(list_map);
			resultObj = JSONObject.fromObject(ma);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			// result.setReturnMessage(e.getErrorMessage());
			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIR_EXCPT_DO));
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	/**
	 * 导入网元对应关系
	 * 
	 * @return
	 */
	public String UploadcheckNe() {

		try {
			Map rlt;
			// 生成文件路径以及文件名
			String path = CommonDefine.PATH_ROOT + CommonDefine.EXCEL.UPLOAD_PATH;
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy_MM_dd_HH-mm-ss");
			String fileName = formatter.format(new Date(System
					.currentTimeMillis()));
			if(jsonString.endsWith(".xlsx")){
				fileName+= ".xlsx";
			}else if(jsonString.endsWith(".xls")){
				fileName+=".xls";
			}
					
			// 转存上传的文件
			rlt = resourceCircuitManagerService.UploadcheckNe(uploadFile,
					fileName, path);

			resultObj = JSONObject.fromObject(rlt);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			// result.setReturnMessage(e.getErrorMessage());
			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIR_EXCPT_ERROR));
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_UPLOAD;
	}

	/**
	 * 导入稽核电路路由信息
	 * 
	 * @return
	 */
	public String UploadResCir() {

		try {
			Map rlt;
			// 生成文件路径以及文件名
			String path = CommonDefine.PATH_ROOT + CommonDefine.EXCEL.UPLOAD_PATH;
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy_MM_dd_HH-mm-ss");
			String fileName = formatter.format(new Date(System
					.currentTimeMillis()));
			if(jsonString.endsWith(".xlsx")){
				fileName+= ".xlsx";
			}else if(jsonString.endsWith(".xls")){
				fileName+=".xls";
			}					
			// 转存上传的文件
			rlt = resourceCircuitManagerService.UploadResCir(uploadFile,
					fileName, path);

			resultObj = JSONObject.fromObject(rlt);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			// result.setReturnMessage(e.getErrorMessage());
			result.setReturnMessage(MessageHandler
					.getErrorMessage(e.getErrorCode()));
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_UPLOAD;
	}
	
	@IMethodLog(desc = "南昌华为网管数据与电路关联")
	public String UploadNc(){


		try {
			Map rlt;
			// 生成文件路径以及文件名
			String path = CommonDefine.PATH_ROOT + CommonDefine.EXCEL.UPLOAD_PATH;
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy_MM_dd_HH-mm-ss");
			String fileName = formatter.format(new Date(System
					.currentTimeMillis()));
			if(jsonString.endsWith(".xlsx")){
				fileName+= ".xlsx";
			}else if(jsonString.endsWith(".xls")){
				fileName+=".xls";
			}					
			// 转存上传的文件
			rlt = resourceCircuitManagerService.UploadNc(uploadFileNc,
					fileName, path);

			resultObj = JSONObject.fromObject(rlt);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			// result.setReturnMessage(e.getErrorMessage());
			result.setReturnMessage(MessageHandler
					.getErrorMessage(e.getErrorCode()));
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_UPLOAD;
	
	}
	@IMethodLog(desc = "南昌华为网管WDM数据与电路关联")
	public String UploadNcWDM(){


		try {
			Map rlt;
			// 生成文件路径以及文件名
			String path = CommonDefine.PATH_ROOT + CommonDefine.EXCEL.UPLOAD_PATH;
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy_MM_dd_HH-mm-ss");
			String fileName = formatter.format(new Date(System
					.currentTimeMillis()));
			if(jsonString.endsWith(".xlsx")){
				fileName+= ".xlsx";
			}else if(jsonString.endsWith(".xls")){
				fileName+=".xls";
			}					
			// 转存上传的文件
			rlt = resourceCircuitManagerService.UploadNcWDM(uploadFileNcWDM,
					fileName, path);

			resultObj = JSONObject.fromObject(rlt);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			// result.setReturnMessage(e.getErrorMessage());
			result.setReturnMessage(MessageHandler
					.getErrorMessage(e.getErrorCode()));
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_UPLOAD;
	
	}
	
	/** **************************get and set *********************************** */

	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}

	public String getResCirName() {
		return resCirName;
	}

	public void setResCirName(String resCirName) {
		this.resCirName = resCirName;
	}

	public String getResCirId() {
		return resCirId;
	}

	public void setResCirId(String resCirId) {
		this.resCirId = resCirId;
	}

	public String getRouteNum() {
		return routeNum;
	}

	public void setRouteNum(String routeNum) {
		this.routeNum = routeNum;
	}

	public File getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(File uploadFile) {
		this.uploadFile = uploadFile;
	}

	public File getUploadFileNc() {
		return uploadFileNc;
	}

	public void setUploadFileNc(File uploadFileNc) {
		this.uploadFileNc = uploadFileNc;
	}

	public File getUploadFileNcWDM() {
		return uploadFileNcWDM;
	}

	public void setUploadFileNcWDM(File uploadFileNcWDM) {
		this.uploadFileNcWDM = uploadFileNcWDM;
	}

}
