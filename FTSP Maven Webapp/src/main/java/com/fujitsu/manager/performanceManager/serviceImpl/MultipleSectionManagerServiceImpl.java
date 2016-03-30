package com.fujitsu.manager.performanceManager.serviceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import jxl.read.biff.BiffException;
import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.fujitsu.IService.ICommonManagerService;
import com.fujitsu.IService.IDataCollectServiceProxy;
import com.fujitsu.IService.IExportExcel;
import com.fujitsu.IService.IPerformanceManagerService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;
import com.fujitsu.common.CsvUtil;
import com.fujitsu.common.ExportResult;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.common.PMDataUtil;
import com.fujitsu.common.poi.ColumnMap;
import com.fujitsu.common.poi.CoverGenerator;
import com.fujitsu.common.poi.MSExcelUtil;
import com.fujitsu.dao.mysql.CircuitManagerMapper;
import com.fujitsu.dao.mysql.PerformanceManagerMapper;
import com.fujitsu.dao.mysql.PmMultipleSectionManagerMapper;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.handler.MessageHandler;
import com.fujitsu.handler.PmMessageHandler;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.PmDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.PmMeasurementModel;
import com.fujitsu.manager.performanceManager.service.MultipleSectionManagerService;
import com.fujitsu.util.CommonUtil;
import com.fujitsu.util.ExportExcelUtil;
import com.fujitsu.util.SpringContextUtil;

/**
 * @author wangjian
 * 
 */
@Scope("prototype")
@Service
// @Transactional(rollbackFor = Exception.class)
public class MultipleSectionManagerServiceImpl extends
		MultipleSectionManagerService {

	@Resource
	private PmMultipleSectionManagerMapper pmMultipleSectionManagerMapper;

	@Resource
	private CircuitManagerMapper circuitManagerMapper;

	@Resource
	private PerformanceManagerMapper performanceManagerMapper;

	@Resource
	ICommonManagerService commonManagerService;

	@Resource
	IPerformanceManagerService performanceManagerService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fujitsu.IService.IPmMultipleSectionManagerService#getAllEMS(int,
	 * int, int)
	 */
	@Override
	public Map<String, Object> selectAllEMS(int emsGroupId, int isAll)
			throws CommonException {

		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Map> listTask = pmMultipleSectionManagerMapper
					.selectAllEMS(emsGroupId);
			if (isAll == CommonDefine.TRUE) {
				Map insert = new HashMap();
				insert.put("BASE_EMS_CONNECTION_ID", 0);
				insert.put("DISPLAY_NAME", "全部");
				listTask.add(0, insert);
			}
			map.put("rows", listTask);
			map.put("total", listTask.size());
		} catch (Exception e) {
			throw new CommonException(new Exception(),
					MessageCodeDefine.CIR_EXCPT_SELECT);
		}

		return map;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fujitsu.IService.IPmMultipleSectionManagerService#getAllGroup()
	 */
	@Override
	public List<Map> selectAllGroup() throws CommonException {
		List<Map> listGroup = null;
		try {
			listGroup = pmMultipleSectionManagerMapper.selectAllGroup();
			// 将全部放入list
			Map map = new HashMap();
			map.put("BASE_EMS_GROUP_ID", 0);
			map.put("GROUP_NAME", "全部");
			listGroup.add(0, map);
			map = new HashMap();
			map.put("BASE_EMS_GROUP_ID", -1);
			map.put("GROUP_NAME", "无");
			listGroup.add(map);
		} catch (Exception e) {
			throw new CommonException(new Exception(),
					MessageCodeDefine.CIR_EXCPT_SELECT);
		}
		return listGroup;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.IService.IPmMultipleSectionManagerService#selectTrunkLine
	 * (java.util.Map)
	 */
	@Override
	public Map<String, Object> selectTrunkLine(Map map) throws CommonException {
		// TODO Auto-generated method stub
		Map<String, Object> returnMap = new HashMap<String, Object>();
		// 添加权限过滤
		List listEmsId = new ArrayList();
		try {
			// 按网管查询
			if (map.get("BASE_EMS_CONNECTION_ID") != null) {
				listEmsId.add(map.get("BASE_EMS_CONNECTION_ID"));
				// 按网管分组查询
			} else {
				if (map.get("userId") == null
						|| "null".equals(map.get("userId").toString())
						|| map.get("userId").toString().isEmpty()) {
					throw new CommonException(new Exception(),
							MessageCodeDefine.USER_LOGIN_AGAIN);
				} else {
					List<Map> listems = commonManagerService
							.getAllEmsByEmsGroupId(Integer.parseInt(map.get(
									"userId").toString()), Integer.parseInt(map
									.get("BASE_EMS_GROUP_ID").toString()), 0, 0,false);
					for (Map ma : listems) {
						listEmsId.add(ma.get("BASE_EMS_CONNECTION_ID"));
					}
				}
			}
			map.put("ID", listEmsId);
			List<Map> listTrunk = pmMultipleSectionManagerMapper
					.selectTrunkLine(map);
			Map total = pmMultipleSectionManagerMapper
					.selectTrunkLineTotal(map);
			// 判断是否要加全部，如果limit为null，则表示查询结果用于下拉显示

			returnMap.put("rows", listTrunk);
			returnMap.put("total", total.get("total"));
		} catch (Exception e) {
			throw new CommonException(new Exception(),
					MessageCodeDefine.CIR_EXCPT_SELECT);
		}
		return returnMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.IService.IPmMultipleSectionManagerService#addTrunkLine(java
	 * .util.Map)
	 */
	@Override
	public CommonResult addTrunkLine(Map map) throws CommonException {
		CommonResult result = new CommonResult();
		try {
			Map select = hashMapSon("t_pm_trunk_line", "DISPLAY_NAME",
					map.get("DISPLAY_NAME"), "BASE_EMS_CONNECTION_ID",
					map.get("BASE_EMS_CONNECTION_ID"), null);
			// 先判断是否存在
			List<Map> list = circuitManagerMapper.getByParameter(select);
			if (list != null && list.size() > 0) {
				// 如果存在，则提示干线名称已存在
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage(MessageHandler
						.getErrorMessage(MessageCodeDefine.PM_EXIST));
			} else {
				// 查询网管ip,用作数据导入
				select = hashMapSon("t_base_ems_connection",
						"BASE_EMS_CONNECTION_ID",
						map.get("BASE_EMS_CONNECTION_ID"), null, null, null);
				Map mapIp = circuitManagerMapper.getByParameter(select).get(0);
				map.put("IP", mapIp.get("IP"));
				pmMultipleSectionManagerMapper.insertTrunkLine(map);
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage(MessageHandler
						.getErrorMessage(MessageCodeDefine.CIRCUIT_INSERT_SUCCESS));
			}
		} catch (Exception e) {
			throw new CommonException(new Exception(),
					MessageCodeDefine.CIR_EXCPT_SELECT);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.IService.IPmMultipleSectionManagerService#deleteTrunkLine
	 * (java.util.Map)
	 */
	@Override
	public CommonResult deleteTrunkLine(List<Map> listMap)
			throws CommonException {
		CommonResult result = new CommonResult();
		Map delete = null;
		try {
			for (Map map : listMap) {
				Map select = hashMapSon("t_pm_multi_sec", "PM_TRUNK_LINE_ID",
						map.get("PM_TRUNK_LINE_ID"), null, null, null);
				// 先判断是否存在
				List<Map> list = circuitManagerMapper.getByParameter(select);
				if (list != null && list.size() > 0) {
					throw new CommonException(new Exception(),
							MessageCodeDefine.PM_EXIST_SECTION);
				} else {
					delete = new HashMap();
					delete.put("NAME", "t_pm_trunk_line");
					delete.put("ID_NAME", "PM_TRUNK_LINE_ID");
					delete.put("ID_VALUE", map.get("PM_TRUNK_LINE_ID"));
					circuitManagerMapper.deleteByParameter(delete);
				}

			}
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIRCUIT_DELETE_SUCCESS));
		} catch (CommonException e) {
			throw e;
		} catch (Exception e) {
			throw new CommonException(new Exception(),
					MessageCodeDefine.CIR_EXCPT_SELECT);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.IService.IPmMultipleSectionManagerService#modifyTrunkLine
	 * (java.util.List)
	 */
	@Override
	public CommonResult modifyTrunkLine(List<Map> listMap)
			throws CommonException {
		CommonResult result = new CommonResult();
		Map update = null;
		try {
			for (Map map : listMap) {
				Map select = hashMapSon("t_pm_trunk_line", "DISPLAY_NAME",
						map.get("DISPLAY_NAME"), null, null, null);
				// 先判断是否存在
				List<Map> list = circuitManagerMapper.getByParameter(select);
				if (list != null && list.size() > 0) {
					throw new CommonException(new Exception(),
							MessageCodeDefine.PM_EXIST);
				} else {
					select = hashMapSon("t_base_ems_connection",
							"BASE_EMS_CONNECTION_ID",
							map.get("BASE_EMS_CONNECTION_ID"), null, null, null);
					Map mapIp = circuitManagerMapper.getByParameter(select)
							.get(0);
					map.put("IP", mapIp.get("IP"));
					update = new HashMap();
					update.put("NAME", "t_pm_trunk_line");
					update.put("ID_NAME", "PM_TRUNK_LINE_ID");
					update.put("ID_VALUE", map.get("PM_TRUNK_LINE_ID"));
					update.put("ID_NAME_2", "DISPLAY_NAME");
					update.put("ID_VALUE_2", map.get("DISPLAY_NAME"));
					update.put("ID_NAME_3", "IP");
					update.put("ID_VALUE_3", map.get("IP"));
					circuitManagerMapper.updateByParameter(update);
				}
			}
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIRCUIT_UPDATE_SUCCESS));
		} catch (CommonException e) {
			throw e;
		} catch (Exception e) {
			throw new CommonException(new Exception(),
					MessageCodeDefine.CIR_EXCPT_SELECT);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.IService.IPmMultipleSectionManagerService#selectMultipleSection
	 * (java.util.Map)
	 */
	@Override
	public Map<String, Object> selectMultipleSection(Map map)
			throws CommonException {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		List listEmsId = new ArrayList();
		try {
			if (map.get("userId") == null
					|| "null".equals(map.get("userId").toString())
					|| map.get("userId").toString().isEmpty()) {
				throw new CommonException(new Exception(),
						MessageCodeDefine.USER_LOGIN_AGAIN);
			}
			// 按网管查询
			if (map.get("BASE_EMS_CONNECTION_ID") != null) {
				listEmsId.add(map.get("BASE_EMS_CONNECTION_ID"));
				// 按网管分组查询
			} else {
				List<Map> listems = commonManagerService.getAllEmsByEmsGroupId(
						Integer.parseInt(map.get("userId").toString()), Integer
								.parseInt(map.get("BASE_EMS_GROUP_ID")
										.toString()), 0, 0,false);
				for (Map ma : listems) {
					listEmsId.add(ma.get("BASE_EMS_CONNECTION_ID"));
				}
				listEmsId.add(0);
			}
			map.put("ID", listEmsId);
			List<Map> listSection = pmMultipleSectionManagerMapper
					.selectMultipleSection(map);
			Map total = pmMultipleSectionManagerMapper
					.selectMultipleSectionTotal(map);

			returnMap.put("rows", listSection);
			returnMap.put("total", total.get("total"));
		} catch (CommonException e) {
			throw e;
		} catch (Exception e) {
			throw new CommonException(new Exception(),
					MessageCodeDefine.CIR_EXCPT_SELECT);
		}
		return returnMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.IService.IPmMultipleSectionManagerService#addMultipleSection
	 * (java.util.Map)
	 */
	@Override
	public CommonResult addMultipleSection(Map map) throws CommonException {
		CommonResult result = new CommonResult();
		Map select = null;
		try {
			select = hashMapSon("t_pm_multi_sec", "SEC_NAME",
					map.get("SEC_NAME"), "PM_TRUNK_LINE_ID",
					map.get("PM_TRUNK_LINE_ID"), null);
			// 先判断是否存在
			List<Map> list = circuitManagerMapper.getByParameter(select);
			if (list != null && list.size() > 0) {
				// 如果存在，则提示干线名称已存在
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage(MessageHandler
						.getErrorMessage(MessageCodeDefine.PM_SECTION_EXIST));
			} else {
				pmMultipleSectionManagerMapper.insertMultipleSection(map);
				// 获取id
				Integer id = Integer.valueOf(map.get("PM_MULTI_SEC_ID")
						.toString());
				// 更新时间
				Map update = new HashMap();
				update.put("NAME", "t_pm_multi_sec");
				update.put("ID_NAME", "PM_MULTI_SEC_ID");
				update.put("ID_VALUE", id);
				update.put("ID_NAME_2", "ROUTE_UPDATE_TIME");
				update.put("ID_VALUE_2", new Date());
				circuitManagerMapper.updateByParameter(update);
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage(MessageHandler
						.getErrorMessage(MessageCodeDefine.CIRCUIT_INSERT_SUCCESS));
				result.setReturnId(id.toString());
				result.setReturnName(map.get("SEC_NAME").toString());
			}
		} catch (Exception e) {
			throw new CommonException(new Exception(),
					MessageCodeDefine.CIR_EXCPT_SELECT);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.IService.IPmMultipleSectionManagerService#deleteMultipleSection
	 * (java.util.List)
	 */
	@Override
	public void deleteMultipleSection(List<Map> listMap) throws CommonException {
		Map delete = null;
		try {
			for (Map map : listMap) {
				// 先删除光复用段端口表
				delete = new HashMap();
				delete.put("NAME", "t_pm_multi_sec_ptp");
				delete.put("ID_NAME", "MULTI_SEC_ID");
				delete.put("ID_VALUE", map.get("PM_MULTI_SEC_ID"));
				circuitManagerMapper.deleteByParameter(delete);
				// 再删光复用段网元表
				delete = new HashMap();
				delete.put("NAME", "t_pm_multi_sec_ne");
				delete.put("ID_NAME", "MULTI_SEC_ID");
				delete.put("ID_VALUE", map.get("PM_MULTI_SEC_ID"));
				circuitManagerMapper.deleteByParameter(delete);
				// 删除光复用段
				delete = new HashMap();
				delete.put("NAME", "t_pm_multi_sec");
				delete.put("ID_NAME", "PM_MULTI_SEC_ID");
				delete.put("ID_VALUE", map.get("PM_MULTI_SEC_ID"));
				circuitManagerMapper.deleteByParameter(delete);

			}
		} catch (Exception e) {
			throw new CommonException(new Exception(),
					MessageCodeDefine.CIR_EXCPT_SELECT);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.IService.IPmMultipleSectionManagerService#modifyMultipleSection
	 * (java.util.List)
	 */
	@Override
	public CommonResult modifyMultipleSection(List<Map> listMap)
			throws CommonException {
		CommonResult result = new CommonResult();
		Map update = null;
		try {
			for (Map map : listMap) {
				Map select = hashMapSon("t_pm_multi_sec", "SEC_NAME",
						map.get("SEC_NAME"), "PM_TRUNK_LINE_ID",
						map.get("PM_TRUNK_LINE_ID"), null);
				// 先判断是否存在
				List<Map> list = circuitManagerMapper.getByParameter(select);
				if (list != null
						&& (list.size() > 1 || (list.size() > 0 && !map.get(
								"PM_MULTI_SEC_ID").equals(
								list.get(0).get("PM_MULTI_SEC_ID"))))) {
					throw new CommonException(new Exception(),
							MessageCodeDefine.PM_SECTION_EXIST);
				} else {
					update = new HashMap();
					update.put("NAME", "t_pm_multi_sec");
					update.put("ID_NAME", "PM_MULTI_SEC_ID");
					update.put("ID_VALUE", map.get("PM_MULTI_SEC_ID"));
					update.put("ID_NAME_2", "SEC_NAME");
					update.put("ID_VALUE_2", map.get("SEC_NAME"));
					update.put("ID_NAME_3", "STD_WAVE");
					update.put("ID_VALUE_3", map.get("STD_WAVE"));
					update.put("ID_NAME_4", "ACTULLY_WAVE");
					update.put("ID_VALUE_4", map.get("ACTULLY_WAVE"));
					update.put("ID_NAME_5", "ROUTE_UPDATE_TIME");
					update.put("ID_VALUE_5", new Date());
					circuitManagerMapper.updateByParameter(update);
				}
				// 计算理论值
				// 计算正向理论值
				caculateValue(map.get("PM_MULTI_SEC_ID").toString(),
						CommonDefine.PM.DIRECTION.FORWARD + "");
				// 如果有反向，则计算反向的理论值
				caculateValue(map.get("PM_MULTI_SEC_ID").toString(),
						CommonDefine.PM.DIRECTION.OPPOSITE + "");
			}

			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIRCUIT_UPDATE_SUCCESS));
		} catch (CommonException e) {
			throw e;
		} catch (Exception e) {
			throw new CommonException(new Exception(),
					MessageCodeDefine.CIR_EXCPT_SELECT);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.IService.IPmMultipleSectionManagerService#selectMultipleSectionNe
	 * (java.util.Map)
	 */
	@Override
	public Map<String, Object> selectMultipleSectionNe(Map map)
			throws CommonException {

		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			List<Map> listSectionNe = pmMultipleSectionManagerMapper
					.selectMultipleSectionNe(map);

			returnMap.put("rows", listSectionNe);
			returnMap.put("total", listSectionNe.size());
		} catch (Exception e) {
			throw new CommonException(new Exception(),
					MessageCodeDefine.CIR_EXCPT_SELECT);
		}
		return returnMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.IService.IPmMultipleSectionManagerService#selectByNeId(java
	 * .util.Map)
	 */
	@Override
	public Map<String, Object> selectByNeId(Map map) throws CommonException {
		// TODO Auto-generated method stub
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			List<Map> listSectionNe = pmMultipleSectionManagerMapper
					.selectByNeId(map);
			returnMap = listSectionNe.get(0);
		} catch (Exception e) {
			throw new CommonException(new Exception(),
					MessageCodeDefine.CIR_EXCPT_SELECT);
		}
		return returnMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.IService.IPmMultipleSectionManagerService#saveNeForward(java
	 * .util.List)
	 */
	@Override
	public void saveNeForward(List<Map> listMap, int mulId, int direction)
			throws CommonException {
		// TODO Auto-generated method stub
		Map update = null;
		Map insert = null;
		Map delete = null;
		Map select = null;
		// 将所有光复用段所属网元置为假删除
		try {
			update = new HashMap();
			update.put("NAME", "t_pm_multi_sec_ne");
			update.put("ID_NAME", "MULTI_SEC_ID");
			update.put("ID_VALUE", mulId);
			update.put("ID_NAME_2", "IS_DEL");
			update.put("ID_VALUE_2", CommonDefine.TRUE);
			circuitManagerMapper.updateByParameter(update);

			int n = 1;
			for (Map map : listMap) {
				select = hashMapSon("t_base_ne", "BASE_NE_ID",
						map.get("BASE_NE_ID"), null, null, null);
				Map neName = circuitManagerMapper.getByParameter(select).get(0);
				map.put("NE_NAME", neName.get("NAME"));
				// 判断是否是新增
				if (map.get("MULTI_SEC_NE_ID") != null
						&& !map.get("MULTI_SEC_NE_ID").toString().isEmpty()) {
					update = new HashMap();
					update.put("NAME", "t_pm_multi_sec_ne");
					update.put("ID_NAME", "MULTI_SEC_NE_ID");
					update.put("ID_VALUE", map.get("MULTI_SEC_NE_ID"));
					update.put("ID_NAME_2", "IS_DEL");
					update.put("ID_VALUE_2", CommonDefine.FALSE);
					update.put("ID_NAME_3", "SEQUENCE");
					update.put("ID_VALUE_3", n);
					update.put("ID_NAME_4", "NE_NAME");
					update.put("ID_VALUE_4", map.get("NE_NAME"));
					circuitManagerMapper.updateByParameter(update);
				} else {
					// 否则是新增
					insert = new HashMap();
					insert.put("MULTI_SEC_ID", map.get("MULTI_SEC_ID"));
					insert.put("NE_ID", map.get("BASE_NE_ID"));
					insert.put("DIRECTION", map.get("DIRECTION"));
					insert.put("NE_NAME", map.get("NE_NAME"));
					insert.put("SEQUENCE", n);
					pmMultipleSectionManagerMapper.insertMulSecNe(insert);
				}
				n++;
			}

			// 查找出要删除的网元id
			select = new HashMap();
			select.put("NAME", "t_pm_multi_sec_ne");
			select.put("VALUE", "*");
			select.put("ID_NAME", "IS_DEL");
			select.put("ID_VALUE", CommonDefine.TRUE);
			select.put("ID_NAME_2", "MULTI_SEC_ID");
			select.put("ID_VALUE_2", mulId);
			select.put("ID_NAME_3", "DIRECTION");
			select.put("ID_VALUE_3", direction);
			List<Map> deleteMap = circuitManagerMapper.getByParameter(select);
			for (Map delMap : deleteMap) {
				// 先查看删除数据是否有相关的子数据
				delete = new HashMap();
				delete.put("NAME", "t_pm_multi_sec_ptp");
				delete.put("ID_NAME_2", "MULTI_SECT_NE_ROUTE_ID");
				delete.put("ID_VALUE_2", delMap.get("MULTI_SEC_NE_ID"));
				circuitManagerMapper.deleteByParameter(delete);
			}

			// 将假删除的数据删除

			delete = new HashMap();
			delete.put("NAME", "t_pm_multi_sec_ne");
			delete.put("ID_NAME", "IS_DEL");
			delete.put("ID_VALUE", CommonDefine.TRUE);
			delete.put("ID_NAME_2", "MULTI_SEC_ID");
			delete.put("ID_VALUE_2", mulId);
			delete.put("ID_NAME_3", "DIRECTION");
			delete.put("ID_VALUE_3", direction);

			circuitManagerMapper.deleteByParameter(delete);

		} catch (Exception e) {
			throw new CommonException(new Exception(),
					MessageCodeDefine.CIR_EXCPT_SELECT);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.IService.IPmMultipleSectionManagerService#getPtpName(java
	 * .util.List)
	 */
	@Override
	public Map<String, Object> selecrPtpName(List<Map> listMap)
			throws CommonException {

		Map<String, Object> returnMap = new HashMap<String, Object>();
		Map update = null;
		String name = "";
		try {
			for (Map map : listMap) {
				Map select = hashMapSon("t_base_ptp", "BASE_PTP_ID",
						map.get("BASE_PTP_ID"), null, null, null);
				// 先判断是否存在
				List<Map> list = circuitManagerMapper.getByParameter(select);
				if (list != null && list.size() > 0) {
					name += list.get(0).get("PORT_DESC").toString() + " ";
				}
			}
			returnMap.put("ptpName", name);
		} catch (Exception e) {
			throw new CommonException(new Exception(),
					MessageCodeDefine.CIR_EXCPT_SELECT);
		}
		return returnMap;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.IService.IPmMultipleSectionManagerService#selectModelType
	 * (java.util.Map)
	 */
	@Override
	public Map<String, Object> selectModelType(Map map) throws CommonException {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			List<Map> listMap = pmMultipleSectionManagerMapper
					.selectModelType(map);
			returnMap.put("rows", listMap);
			returnMap.put("total", listMap.size());
		} catch (Exception e) {
			throw new CommonException(new Exception(),
					MessageCodeDefine.CIR_EXCPT_SELECT);
		}
		return returnMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.IService.IPmMultipleSectionManagerService#selectMultipleModel
	 * (java.util.Map)
	 */
	@Override
	public Map<String, Object> selectMultipleModel() throws CommonException {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			List<Map> listMap = pmMultipleSectionManagerMapper
					.selectMultipleModel();
			Map map = new HashMap();
			map.put("MODEL", "全部");
			listMap.add(0, map);
			returnMap.put("rows", listMap);
			returnMap.put("total", listMap.size());
		} catch (Exception e) {
			throw new CommonException(new Exception(),
					MessageCodeDefine.CIR_EXCPT_SELECT);
		}
		return returnMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.IService.IPmMultipleSectionManagerService#selectStandOptVal
	 * (java.util.Map)
	 */
	@Override
	public Map<String, Object> selectStandOptVal(Map map)
			throws CommonException {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			List<Map> listMap = pmMultipleSectionManagerMapper
					.selectStandOptVal(map);
			Map Mapotal = pmMultipleSectionManagerMapper
					.selectStandOptValTotal(map);
			returnMap.put("rows", listMap);
			returnMap.put("total", Mapotal.get("total"));
		} catch (Exception e) {
			throw new CommonException(new Exception(),
					MessageCodeDefine.CIR_EXCPT_SELECT);
		}
		return returnMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.IService.IPmMultipleSectionManagerService#addStandOptVal(
	 * java.util.Map)
	 */
	@Override
	public CommonResult addStandOptVal(Map map) throws CommonException {

		CommonResult result = new CommonResult();
		try {
			// 查看是否同厂家存在相同的光放型号
			Map select = hashMapSon("t_pm_std_opt_amp", "FACTORY",
					map.get("FACTORY"), "MODEL", map.get("MODEL"), null);
			// 先判断是否存在
			List<Map> list = circuitManagerMapper.getByParameter(select);
			if (list != null && list.size() > 0) {
				// 如果存在，则提示同厂家已经存在改光放
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage(MessageHandler
						.getErrorMessage(MessageCodeDefine.PM_STANDOPTVAL_EXIST));
			} else {
				// for(int i = 0 ; i<600;i++){
				// Map insert = new HashMap();
				// insert.put("FACTORY", 1);
				// insert.put("TYPE", 1);
				// insert.put("MODEL", "yu"+i);
				// insert.put("MAX_OUT", 13);
				// insert.put("TYPICAL_GAIN", 3);
				// pmMultipleSectionManagerMapper.insertStandOptVal(insert);
				//
				// }
				pmMultipleSectionManagerMapper.insertStandOptVal(map);
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage(MessageHandler
						.getErrorMessage(MessageCodeDefine.CIRCUIT_INSERT_SUCCESS));
			}
		} catch (Exception e) {
			throw new CommonException(new Exception(),
					MessageCodeDefine.CIR_EXCPT_SELECT);
		}
		return result;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.IService.IPmMultipleSectionManagerService#deleteStandOptVal
	 * (java.util.List)
	 */
	@Override
	public CommonResult deleteStandOptVal(List<Map> listMap)
			throws CommonException {

		CommonResult result = new CommonResult();
		Map delete = null;
		try {
			for (Map map : listMap) {
				Map select = hashMapSon("t_pm_multi_sec_ptp",
						"PM_STD_OPT_AMP_ID", map.get("PM_STD_OPT_AMP_ID"),
						null, null, null);
				// 先判断是否存在
				List<Map> list = circuitManagerMapper.getByParameter(select);
				if (list != null && list.size() > 0) {
					throw new CommonException(new Exception(),
							MessageCodeDefine.PM_STANDOPTVAL_IN_SECTION);
				} else {
					delete = new HashMap();
					delete.put("NAME", "t_pm_std_opt_amp");
					delete.put("ID_NAME", "PM_STD_OPT_AMP_ID");
					delete.put("ID_VALUE", map.get("PM_STD_OPT_AMP_ID"));
					circuitManagerMapper.deleteByParameter(delete);
				}

			}
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIRCUIT_DELETE_SUCCESS));
		} catch (CommonException e) {
			throw e;
		} catch (Exception e) {
			throw new CommonException(new Exception(),
					MessageCodeDefine.CIR_EXCPT_SELECT);
		}
		return result;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.IService.IPmMultipleSectionManagerService#modifyStandOptVal
	 * (java.util.List)
	 */
	@Override
	public CommonResult modifyStandOptVal(List<Map> listMap)
			throws CommonException {
		CommonResult result = new CommonResult();
		Map update = null;
		try {
			for (Map map : listMap) {

				update = new HashMap();
				update.put("NAME", "t_pm_std_opt_amp");
				update.put("ID_NAME", "PM_STD_OPT_AMP_ID");
				update.put("ID_VALUE", map.get("PM_STD_OPT_AMP_ID"));
				update.put("ID_NAME_2", "MAX_OUT");
				update.put("ID_VALUE_2", map.get("MAX_OUT"));
				update.put("ID_NAME_3", "MIN_GAIN");
				update.put("ID_VALUE_3", map.get("MIN_GAIN"));
				update.put("ID_NAME_4", "MAX_GAIN");
				update.put("ID_VALUE_4", map.get("MAX_GAIN"));
				update.put("ID_NAME_5", "TYPICAL_GAIN");
				update.put("ID_VALUE_5", map.get("TYPICAL_GAIN"));
				update.put("ID_NAME_6", "MAX_IN");
				update.put("ID_VALUE_6", map.get("MAX_IN"));
				update.put("ID_NAME_7", "MIN_IN");
				update.put("ID_VALUE_7", map.get("MIN_IN"));
				update.put("ID_NAME_8", "TYPICAL_IN");
				update.put("ID_VALUE_8", map.get("TYPICAL_IN"));
				circuitManagerMapper.updateByParameter(update);

			}
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIRCUIT_UPDATE_SUCCESS));
		} catch (Exception e) {
			throw new CommonException(new Exception(),
					MessageCodeDefine.CIR_EXCPT_SELECT);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.IService.IPmMultipleSectionManagerService#selectPtpRouteList
	 * (java.util.Map)
	 */
	@Override
	public Map<String, Object> selectPtpRouteList(Map map)
			throws CommonException {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			List<Map> listPtplist = pmMultipleSectionManagerMapper
					.selectPtpRouteList(map);

			returnMap.put("rows", listPtplist);
			returnMap.put("total", listPtplist.size());
		} catch (Exception e) {
			throw new CommonException(new Exception(),
					MessageCodeDefine.CIR_EXCPT_SELECT);
		}
		return returnMap;
	}

	@Override
	public Map<String, Object> selectSubPtpRouteList(Map map)
			throws CommonException {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			List<Map> listPtplist = pmMultipleSectionManagerMapper
					.selectSubPtpRouteList(map);

			returnMap.put("rows", listPtplist);
			returnMap.put("total", listPtplist.size());
		} catch (Exception e) {
			throw new CommonException(new Exception(),
					MessageCodeDefine.CIR_EXCPT_SELECT);
		}
		return returnMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.IService.IPmMultipleSectionManagerService#savePtpForward(
	 * java.util.List)
	 */
	@Override
	public void savePtpForward(List<Map> listMap, int neId, int direction)
			throws CommonException {

		// TODO Auto-generated method stub
		Map select = null;
		Map update = null;
		Map insert = null;
		Map delete = null;
		// 将所有光复用段所属网元置为假删除
		try {
			update = new HashMap();
			update.put("NAME", "t_pm_multi_sec_ptp");
			update.put("ID_NAME", "MULTI_SECT_NE_ROUTE_ID");
			update.put("ID_VALUE", neId);
			update.put("ID_NAME_2", "IS_DEL");
			update.put("ID_VALUE_2", CommonDefine.TRUE);
			circuitManagerMapper.updateByParameter(update);

			int n = 1;
			for (Map map : listMap) {
				String namePtp = "";
				String nameSubPtp = "";
				// 判断ptp是否为空
				if (map.get("PTP_ID") != null
						&& !map.get("PTP_ID").toString().isEmpty()) {

					String[] ptpName = map.get("PTP_ID").toString().split(",");
					if (ptpName.length > 0) {
						select = hashMapSon("t_base_ptp", "BASE_PTP_ID",
								ptpName[0], null, null, null);
						List<Map> listPtp = circuitManagerMapper
								.getByParameter(select);
						if (listPtp != null && listPtp.size() > 0) {
							Map mapPtp = listPtp.get(0);
							// 导入使用，格式
							// direction|ptptype|rackno|shelfno|slotno|portno
							// 如有多个,分隔
							namePtp = mapPtp.get("DIRECTION").toString() + "|"
									+ mapPtp.get("PTP_TYPE").toString() + "|"
									+ mapPtp.get("RACK_NO").toString() + "|"
									+ mapPtp.get("SHELF_NO").toString() + "|"
									+ mapPtp.get("SLOT_NO").toString() + "|"
									+ mapPtp.get("PORT_NO").toString();

							for (int i = 1; i < ptpName.length - 1; i++) {
								select = hashMapSon("t_base_ptp",
										"BASE_PTP_ID", ptpName[i], null, null,
										null);
								Map mapPtpfor = circuitManagerMapper
										.getByParameter(select).get(0);
								// 导入使用，格式
								// direction|ptptype|rackno|shelfno|slotno|portno
								// 如有多个,分隔
								namePtp += ","
										+ mapPtpfor.get("DIRECTION").toString()
										+ "|"
										+ mapPtpfor.get("PTP_TYPE").toString()
										+ "|"
										+ mapPtpfor.get("RACK_NO").toString()
										+ "|"
										+ mapPtpfor.get("SHELF_NO").toString()
										+ "|"
										+ mapPtpfor.get("SLOT_NO").toString()
										+ "|"
										+ mapPtpfor.get("PORT_NO").toString();

							}
						}
					}
				}

				if (map.get("SUB_PTP_ID") != null
						&& !map.get("SUB_PTP_ID").toString().isEmpty()) {

					String[] ptpName = map.get("SUB_PTP_ID").toString()
							.split(",");
					if (ptpName.length > 0) {
						select = hashMapSon("t_base_ptp", "BASE_PTP_ID",
								ptpName[0], null, null, null);
						List<Map> listPtp = circuitManagerMapper
								.getByParameter(select);
						if (listPtp != null && listPtp.size() > 0) {
							Map mapPtp = listPtp.get(0);
							// 导入使用，格式
							// direction|ptptype|rackno|shelfno|slotno|portno
							// 如有多个,分隔
							nameSubPtp = mapPtp.get("DIRECTION").toString()
									+ "|" + mapPtp.get("PTP_TYPE").toString()
									+ "|" + mapPtp.get("RACK_NO").toString()
									+ "|" + mapPtp.get("SHELF_NO").toString()
									+ "|" + mapPtp.get("SLOT_NO").toString()
									+ "|" + mapPtp.get("PORT_NO").toString();

							for (int i = 1; i < ptpName.length - 1; i++) {
								select = hashMapSon("t_base_ptp",
										"BASE_PTP_ID", ptpName[i], null, null,
										null);
								Map mapPtpfor = circuitManagerMapper
										.getByParameter(select).get(0);
								// 导入使用，格式
								// direction|ptptype|rackno|shelfno|slotno|portno
								// 如有多个,分隔
								nameSubPtp += ","
										+ mapPtpfor.get("DIRECTION").toString()
										+ "|"
										+ mapPtpfor.get("PTP_TYPE").toString()
										+ "|"
										+ mapPtpfor.get("RACK_NO").toString()
										+ "|"
										+ mapPtpfor.get("SHELF_NO").toString()
										+ "|"
										+ mapPtpfor.get("SLOT_NO").toString()
										+ "|"
										+ mapPtpfor.get("PORT_NO").toString();

							}
						}
					}
				}
				// 判断是否是新增
				if (map != null && map.get("PM_MULTI_SEC_PTP_ID") != null
						&& !map.get("PM_MULTI_SEC_PTP_ID").toString().isEmpty()) {
					update = new HashMap();
					update.put("PM_MULTI_SEC_PTP_ID",
							map.get("PM_MULTI_SEC_PTP_ID"));
					update.put("MULTI_SEC_ID", map.get("MULTI_SEC_ID"));
					update.put("MULTI_SECT_NE_ROUTE_ID",
							map.get("MULTI_SECT_NE_ROUTE_ID"));
					update.put("PTP_ID", map.get("PTP_ID"));
					update.put("EQUIP_NAME", map.get("EQUIP_NAME"));
					update.put("PTP_NAME", map.get("PTP_NAME"));
					update.put("PM_STD_OPT_AMP_ID",
							map.get("PM_STD_OPT_AMP_ID"));
					update.put("PM_TYPE", map.get("PM_TYPE"));

					update.put("NOTE", map.get("NOTE"));
					update.put("CALCULATE_POINT", map.get("CALCULATE_POINT"));
					update.put("ROUTE_TYPE", map.get("ROUTE_TYPE"));
					update.put("SUB_PTP_ID", map.get("SUB_PTP_ID"));
					update.put("SUB_PTP_NAME", map.get("SUB_PTP_NAME"));
					update.put("SUB_PM_TYPE", map.get("SUB_PM_TYPE"));
					update.put("SUB_NOTE", map.get("SUB_NOTE"));
					update.put("SUB_CALCULATE_POINT",
							map.get("SUB_CALCULATE_POINT"));
					update.put("CUT_PM_VALUE", map.get("CUT_PM_VALUE"));
					update.put("SUB_CUT_PM_VALUE", map.get("SUB_CUT_PM_VALUE"));
					update.put("SUB_ROUTE_TYPE", map.get("SUB_ROUTE_TYPE"));
					update.put("SUB_PM_STD_OPT_AMP_ID",
							map.get("SUB_PM_STD_OPT_AMP_ID"));
					update.put("PTP_TAG", namePtp);
					update.put("SUB_PTP_TAG", nameSubPtp);
					update.put("IS_DEL", CommonDefine.FALSE);
					update.put("SEQUENCE", n);
					pmMultipleSectionManagerMapper.updateMultiplePtp(update);
				} else {
					// 否则是新增
					insert = new HashMap();
					insert.put("PM_MULTI_SEC_PTP_ID",
							map.get("PM_MULTI_SEC_PTP_ID"));
					insert.put("MULTI_SEC_ID", map.get("MULTI_SEC_ID"));
					insert.put("MULTI_SECT_NE_ROUTE_ID",
							map.get("MULTI_SECT_NE_ROUTE_ID"));
					insert.put("PTP_ID", map.get("PTP_ID"));
					insert.put("EQUIP_NAME", map.get("EQUIP_NAME"));
					insert.put("PTP_NAME", map.get("PTP_NAME"));
					insert.put("PM_STD_OPT_AMP_ID",
							map.get("PM_STD_OPT_AMP_ID"));
					insert.put("PM_TYPE", map.get("PM_TYPE"));

					insert.put("NOTE", map.get("NOTE"));
					insert.put("CALCULATE_POINT", map.get("CALCULATE_POINT"));
					insert.put("ROUTE_TYPE", map.get("ROUTE_TYPE"));
					insert.put("SUB_PTP_ID", map.get("SUB_PTP_ID"));
					insert.put("SUB_PTP_NAME", map.get("SUB_PTP_NAME"));
					insert.put("SUB_PM_TYPE", map.get("SUB_PM_TYPE"));
					insert.put("SUB_NOTE", map.get("SUB_NOTE"));
					insert.put("SUB_CALCULATE_POINT",
							map.get("SUB_CALCULATE_POINT"));
					insert.put("CALCULATE_POINT", map.get("CALCULATE_POINT"));
					insert.put("SUB_CUT_PM_VALUE", map.get("SUB_CUT_PM_VALUE"));
					insert.put("SUB_ROUTE_TYPE", map.get("SUB_ROUTE_TYPE"));
					insert.put("SUB_PM_STD_OPT_AMP_ID",
							map.get("SUB_PM_STD_OPT_AMP_ID"));

					insert.put("IS_DEL", CommonDefine.FALSE);
					insert.put("DIRECTION", map.get("DIRECTION"));
					insert.put("SEQUENCE", n);
					insert.put("PTP_TAG", namePtp);
					insert.put("SUB_PTP_TAG", nameSubPtp);
					pmMultipleSectionManagerMapper.insertMultiplePtp(insert);
				}
				n++;
			}

			delete = new HashMap();
			delete.put("NAME", "t_pm_multi_sec_ptp");
			delete.put("ID_NAME", "MULTI_SECT_NE_ROUTE_ID");
			delete.put("ID_VALUE", neId);

			delete.put("ID_NAME_2", "IS_DEL");
			delete.put("ID_VALUE_2", CommonDefine.TRUE);

			delete.put("ID_NAME_3", "DIRECTION");
			delete.put("ID_VALUE_3", direction);
			circuitManagerMapper.deleteByParameter(delete);

			// 计算理论值
			if (listMap != null && listMap.size() > 0) {
				caculateValue(listMap.get(0).get("MULTI_SEC_ID").toString(),
						listMap.get(0).get("DIRECTION").toString());
			}
		} catch (Exception e) {
			throw new CommonException(new Exception(),
					MessageCodeDefine.CIR_EXCPT_SELECT);
		}
	}

	/**
	 * 计算理论值的方法提取
	 * 
	 * @param listMap
	 */
	private void caculateValue(String multiId, String direction) {
		Map select = null;
		Map update = null;
		Map insert = null;
		Map delete = null;
		// 计算理论值

		// 先用普通情况粗略计算输入输出值
		select = new HashMap();
		select.put("MULTI_SEC_ID", multiId);
		select.put("DIRECTION", direction);
		List<Map> listPtpRoute = pmMultipleSectionManagerMapper
				.selectMultiplePtpList(select);
		// 查询出波道数
		select = hashMapSon("t_pm_multi_sec", "PM_MULTI_SEC_ID", multiId, null,
				null, null);
		List<Map> listWave = circuitManagerMapper.getByParameter(select);
		int stdWave = 0;
		int actullyWave = 0;
		if (listWave != null && listWave.size() > 0) {
			if (listWave.get(0).get("STD_WAVE") != null
					&& !listWave.get(0).get("STD_WAVE").toString().isEmpty()) {
				stdWave = Integer.parseInt(listWave.get(0).get("STD_WAVE")
						.toString());
			}
			if (listWave.get(0).get("ACTULLY_WAVE") != null
					&& !listWave.get(0).get("ACTULLY_WAVE").toString()
							.isEmpty()) {
				actullyWave = Integer.parseInt(listWave.get(0)
						.get("ACTULLY_WAVE").toString());
			}
		}
		// 计算首网元的输入输出
		if (listPtpRoute != null && listPtpRoute.size() > 0) {
			for (int i = 0; i < listPtpRoute.size(); i++) {
				// 初始化理论值
				double caVlue = 0.00;
				double caSubVlue = 0.00;

				Map map = listPtpRoute.get(i);
				if (map.get("CALCULATE_POINT") != null
						&& !map.get("CALCULATE_POINT").toString().isEmpty()) {
					caVlue = Double.parseDouble(map.get("CALCULATE_POINT")
							.toString());
				}
				if (map.get("SUB_CALCULATE_POINT") != null
						&& !map.get("SUB_CALCULATE_POINT").toString().isEmpty()) {
					caSubVlue = Double.parseDouble(map.get(
							"SUB_CALCULATE_POINT").toString());
				}
				// 1.端口 2. 虚拟端口
				if (map.get("ROUTE_TYPE") != null
						&& ((CommonDefine.PM.SECTON_ROUTE_TYPE.PTP + "")
								.equals(map.get("ROUTE_TYPE").toString()) || (CommonDefine.PM.SECTON_ROUTE_TYPE.VIR_PORT + "")
								.equals(map.get("ROUTE_TYPE").toString()))) {
					// 获取光放型号,查出最大输出功率和增益典型值
					select = hashMapSon("t_pm_std_opt_amp",
							"PM_STD_OPT_AMP_ID", map.get("PM_STD_OPT_AMP_ID"),
							null, null, null);
					List<Map> listOa = circuitManagerMapper
							.getByParameter(select);
					String maxOut = "0.00";
					String typicalGain = "0.00";
					// System.out.println(listOa == null);
					// System.out.println(listOa.size() > 0);
					if (listOa != null && listOa.size() > 0) {
						maxOut = listOa.get(0).get("MAX_OUT").toString();
						typicalGain = listOa.get(0).get("TYPICAL_GAIN")
								.toString();

						// 判断是输入或者输出
						if (map.get("PM_TYPE") != null
								&& (CommonDefine.PM.PORT_TYPE.PORT_IN + "")
										.equals(map.get("PM_TYPE").toString())) {
							// 输入理论值计算公式Pnin(理想输入）=Pnout（理想输出）-A(增益典型值)
							if (actullyWave > 0 && stdWave > 0) {
								caVlue = 10 * Math.log10(actullyWave) - 10
										* Math.log10(stdWave)
										+ Double.parseDouble(maxOut)
										- Double.parseDouble(typicalGain);
							}

						} else if (map.get("PM_TYPE") != null
								&& (CommonDefine.PM.PORT_TYPE.PORT_OUT + "")
										.equals(map.get("PM_TYPE").toString())) {
							// 输出理论值计算公式Pnout(理想输出)=10lgN(实际波道数）-10lgN(标称波道数）+Pn（最大输出功率）
							if (actullyWave > 0 && stdWave > 0) {
								caVlue = 10 * Math.log10(actullyWave) - 10
										* Math.log10(stdWave)
										+ Double.parseDouble(maxOut);
							}
						}
					}
					// 将计算出来的值存入数据库,不为0的时候才入库
					if (caVlue != 0) {
						update = new HashMap();
						update.put("NAME", "t_pm_multi_sec_ptp");
						update.put("ID_NAME", "PM_MULTI_SEC_PTP_ID");
						update.put("ID_VALUE", map.get("PM_MULTI_SEC_PTP_ID"));
						update.put("ID_NAME_2", "CALCULATE_POINT");
						update.put("ID_VALUE_2", getDouble(caVlue));
						// update.put("ID_NAME_3", "SUB_CALCULATE_POINT");
						// update.put("ID_VALUE_3", caSubVlue);
						circuitManagerMapper.updateByParameter(update);
					}
				}
				// 其他几种情况，无需计算

				// 下面是备用端口
				// 1.端口 2. 虚拟端口
				if (map.get("SUB_ROUTE_TYPE") != null
						&& ((CommonDefine.PM.SECTON_ROUTE_TYPE.PTP + "")
								.equals(map.get("SUB_ROUTE_TYPE").toString()) || (CommonDefine.PM.SECTON_ROUTE_TYPE.VIR_PORT + "")
								.equals(map.get("SUB_ROUTE_TYPE").toString()))) {
					// 获取光放型号,查出最大输出功率和增益典型值
					select = hashMapSon("t_pm_std_opt_amp",
							"PM_STD_OPT_AMP_ID",
							map.get("SUB_PM_STD_OPT_AMP_ID"), null, null, null);
					List<Map> listOa = circuitManagerMapper
							.getByParameter(select);
					String maxOut = "0.00";
					String typicalGain = "0.00";
					if (listOa != null && listOa.size() > 0) {
						if (listOa.get(0).get("MAX_OUT") != null
								&& !listOa.get(0).get("MAX_OUT").toString()
										.isEmpty()) {
							maxOut = listOa.get(0).get("MAX_OUT").toString();
						}
						if (listOa.get(0).get("TYPICAL_GAIN") != null
								&& !listOa.get(0).get("TYPICAL_GAIN")
										.toString().isEmpty()) {
							typicalGain = listOa.get(0).get("TYPICAL_GAIN")
									.toString();
						}

						// 判断是输入或者输出
						if (map.get("SUB_PM_TYPE") != null
								&& (CommonDefine.PM.PORT_TYPE.PORT_IN + "")
										.equals(map.get("SUB_PM_TYPE")
												.toString())) {
							// 输入理论值计算公式Pnin(理想输入）=Pnout（理想输出）-A(增益典型值)
							caSubVlue = 10 * Math.log10(actullyWave) - 10
									* Math.log10(stdWave)
									+ Double.parseDouble(maxOut)
									- Double.parseDouble(typicalGain);

						} else if (map.get("SUB_PM_TYPE") != null
								&& (CommonDefine.PM.PORT_TYPE.PORT_OUT + "")
										.equals(map.get("SUB_PM_TYPE")
												.toString())) {
							// 输出理论值计算公式Pnout(理想输出)=10lgN(实际波道数）-10lgN(标称波道数）+Pn（最大输出功率）
							caSubVlue = 10 * Math.log10(actullyWave) - 10
									* Math.log10(stdWave)
									+ Double.parseDouble(maxOut);
						}
					}
					update = new HashMap();
					update.put("NAME", "t_pm_multi_sec_ptp");
					update.put("ID_NAME", "PM_MULTI_SEC_PTP_ID");
					update.put("ID_VALUE", map.get("PM_MULTI_SEC_PTP_ID"));
					update.put("ID_NAME_2", "SUB_CALCULATE_POINT");
					update.put("ID_VALUE_2", getDouble(caSubVlue));
					circuitManagerMapper.updateByParameter(update);
				}

			}
		}
		// 纠正含有光纤，下游端口的输入值
		select = new HashMap();
		select.put("MULTI_SEC_ID", multiId);
		select.put("DIRECTION", direction);
		List<Map> listPtpRouteAgain = pmMultipleSectionManagerMapper
				.selectMultiplePtpList(select);
		boolean isContnue = false;
		if (listPtpRouteAgain != null && listPtpRouteAgain.size() > 0) {
			for (int j = 0; j < listPtpRouteAgain.size(); j++) {
				Map mapAgain = listPtpRouteAgain.get(j);
				// 先找到光缆
				if (mapAgain.get("ROUTE_TYPE") != null
						&& (CommonDefine.PM.SECTON_ROUTE_TYPE.FIBER + "")
								.equals(mapAgain.get("ROUTE_TYPE").toString())) {
					double out = 0.00;
					double caValue = 0.00;
					// 记下当前网元路由id
					String neRouteId = mapAgain.get("MULTI_SECT_NE_ROUTE_ID")
							.toString();
					// 向上查找出上一条的输出端口
					for (int m = j; m >= 0; m--) {
						Map mapM = listPtpRouteAgain.get(m);
						if (mapM.get("PM_TYPE") == null) {
							continue;
						}
						// 判断先遇到输入还是输出端口
						if (mapM.get("PM_TYPE") != null
								&& mapM.get("PM_TYPE").equals(
										CommonDefine.PM.PORT_TYPE.PORT_IN + "")) {
							// 结束本次循环，并跳过此光缆的计算
							isContnue = true;
							break;
						} else if (mapM.get("PM_TYPE") != null
								&& mapM.get("PM_TYPE")
										.equals(CommonDefine.PM.PORT_TYPE.PORT_OUT
												+ "")) {
							if (mapM.get("CALCULATE_POINT") != null
									&& !mapM.get("CALCULATE_POINT").toString()
											.isEmpty()) {
								out = Double.parseDouble(mapM.get(
										"CALCULATE_POINT").toString());
							}
							break;
						}
					}
					if (isContnue) {
						continue;
					}
					// 向下查找出下一跳的输入端口
					for (int k = j + 1; k < listPtpRouteAgain.size(); k++) {
						Map mapK = listPtpRouteAgain.get(k);
						// 判断先遇到输入还是输出端口
						if (mapK.get("PM_TYPE") != null
								&& mapK.get("PM_TYPE").equals(
										CommonDefine.PM.PORT_TYPE.PORT_IN + "")) {
							if (mapAgain.get("CALCULATE_POINT") != null
									&& !mapAgain.get("CALCULATE_POINT")
											.toString().isEmpty()) {
								caValue = out
										- Double.parseDouble(mapAgain.get(
												"CALCULATE_POINT").toString());
							}
							// 更新下游输入端的理论值
							update = new HashMap();
							update.put("NAME", "t_pm_multi_sec_ptp");
							update.put("ID_NAME", "PM_MULTI_SEC_PTP_ID");
							update.put("ID_VALUE",
									mapK.get("PM_MULTI_SEC_PTP_ID"));
							update.put("ID_NAME_2", "CALCULATE_POINT");
							update.put("ID_VALUE_2", caValue);
							circuitManagerMapper.updateByParameter(update);
							break;
						} else if (mapK.get("PM_TYPE") != null
								&& mapK.get("PM_TYPE")
										.equals(CommonDefine.PM.PORT_TYPE.PORT_OUT
												+ "")) {

							// 结束本次循环，并跳过此光缆的计算
							isContnue = true;
							break;
						}
					}
					if (isContnue) {
						continue;
					}
					// 如果循环没有结束，则更新下游输入端的理论值

				}

				// 备用端口值计算
				if (mapAgain.get("SUB_ROUTE_TYPE") != null
						&& mapAgain
								.get("SUB_ROUTE_TYPE")
								.toString()
								.equals(CommonDefine.PM.SECTON_ROUTE_TYPE.FIBER
										+ "")) {
					double out = 0.00;
					double caSubValue = 0.00;
					// 记下当前网元路由id
					String neRouteId = mapAgain.get("MULTI_SECT_NE_ROUTE_ID")
							.toString();
					// 向上查找出上一条的输出端口
					for (int m = j; m >= 0; m--) {
						Map mapM = listPtpRouteAgain.get(m);
						if (mapM.get("SUB_PM_TYPE") == null) {
							continue;
						}
						// 判断先遇到输入还是输出端口
						if (mapM.get("SUB_PM_TYPE").equals(
								CommonDefine.PM.PORT_TYPE.PORT_IN + "")) {
							// 结束本次循环，并跳过此光缆的计算
							isContnue = true;
							break;
						} else if (mapM.get("SUB_PM_TYPE").equals(
								CommonDefine.PM.PORT_TYPE.PORT_OUT + "")) {
							if (mapM.get("SUB_CALCULATE_POINT") != null
									&& !mapM.get("SUB_CALCULATE_POINT")
											.toString().isEmpty()) {
								out = Double.parseDouble(mapM.get(
										"SUB_CALCULATE_POINT").toString());
							}
							break;
						}
					}
					if (isContnue) {
						continue;
					}
					// 向下查找出下一跳的输入端口
					for (int k = j + 1; k < listPtpRouteAgain.size(); k++) {
						Map mapK = listPtpRouteAgain.get(k);
						// 判断先遇到输入还是输出端口
						if (mapK.get("SUB_PM_TYPE") != null
								&& mapK.get("SUB_PM_TYPE").equals(
										CommonDefine.PM.PORT_TYPE.PORT_IN + "")) {
							if (mapAgain.get("SUB_CALCULATE_POINT") != null
									&& !mapAgain.get("SUB_CALCULATE_POINT")
											.toString().isEmpty()) {
								caSubValue = out
										- Double.parseDouble(mapAgain.get(
												"SUB_CALCULATE_POINT")
												.toString());
							}
							// 更新下游输入端的理论值
							update = new HashMap();
							update.put("NAME", "t_pm_multi_sec_ptp");
							update.put("ID_NAME", "PM_MULTI_SEC_PTP_ID");
							update.put("ID_VALUE",
									mapK.get("PM_MULTI_SEC_PTP_ID"));
							update.put("ID_NAME_2", "SUB_CALCULATE_POINT");
							update.put("ID_VALUE_2", caSubValue);
							circuitManagerMapper.updateByParameter(update);
							break;
						} else if (mapK.get("SUB_PM_TYPE") != null
								&& mapK.get("SUB_PM_TYPE")
										.equals(CommonDefine.PM.PORT_TYPE.PORT_OUT
												+ "")) {

							// 结束本次循环，并跳过此光缆的计算
							isContnue = true;
							break;
						}
					}
					if (isContnue) {
						continue;
					}
					// 如果循环没有结束，则更新下游输入端的理论值

				}
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.IService.IPmMultipleSectionManagerService#sortMultipleSection
	 * (java.util.List)
	 */
	@Override
	public CommonResult sortMultipleSection(List<Map> listMap)
			throws CommonException {
		CommonResult result = new CommonResult();
		Map update = null;
		try {
			int n = 1;
			for (Map map : listMap) {
				update = new HashMap();
				update.put("NAME", "t_pm_multi_sec");
				update.put("ID_NAME", "PM_MULTI_SEC_ID");
				update.put("ID_VALUE", map.get("PM_MULTI_SEC_ID"));
				update.put("ID_NAME_2", "SEQUENCE");
				update.put("ID_VALUE_2", n);
				circuitManagerMapper.updateByParameter(update);
				n++;

			}
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.CIRCUIT_UPDATE_SUCCESS));
		} catch (Exception e) {
			throw new CommonException(new Exception(),
					MessageCodeDefine.CIR_EXCPT_SELECT);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.IService.IPmMultipleSectionManagerService#selectMultipleAbout
	 * (java.util.Map)
	 */
	@Override
	public Map<String, Object> selectMultipleAbout(Map map)
			throws CommonException {
		Map<String, Object> mapr = null;
		try {

			List<Map> listPtplist = pmMultipleSectionManagerMapper
					.selectMultipleAbout(map);
			mapr = listPtplist.get(0);
		} catch (Exception e) {
			throw new CommonException(new Exception(),
					MessageCodeDefine.CIR_EXCPT_SELECT);
		}
		return mapr;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.IService.IPmMultipleSectionManagerService#selectMultiplePtpRoute
	 * (java.util.Map)
	 */
	@Override
	public Map<String, Object> selectMultiplePtpRoute(Map map)
			throws CommonException {

		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			List<Map> listPtplist = pmMultipleSectionManagerMapper
					.selectMultiplePtpRoute(map);

			returnMap.put("rows", listPtplist);
			returnMap.put("total", listPtplist.size());
		} catch (Exception e) {
			throw new CommonException(new Exception(),
					MessageCodeDefine.CIR_EXCPT_SELECT);
		}
		return returnMap;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.IService.IPmMultipleSectionManagerService#saveMultipleDetail
	 * (java.util.List)
	 */
	@Override
	public void saveMultipleDetail(List<Map> listMap) throws CommonException {

		// TODO Auto-generated method stub
		Map update = null;
		try {
			for (Map map : listMap) {
				// 判断是否是新增

				update = new HashMap();
				update.put("NAME", "t_pm_multi_sec_ptp");
				update.put("ID_NAME", "PM_MULTI_SEC_PTP_ID");
				update.put("ID_VALUE", map.get("PM_MULTI_SEC_PTP_ID"));
				update.put("ID_NAME_2", "CUT_PM_VALUE");
				update.put("ID_VALUE_2", map.get("CUT_PM_VALUE"));
				update.put("ID_NAME_3", "SUB_CUT_PM_VALUE");
				update.put("ID_VALUE_3", map.get("SUB_CUT_PM_VALUE"));
				circuitManagerMapper.updateByParameter(update);
			}
		} catch (Exception e) {
			throw new CommonException(new Exception(),
					MessageCodeDefine.CIR_EXCPT_SELECT);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.IService.IPmMultipleSectionManagerService#sycPmByMultiple
	 * (java.util.List)
	 */
	@Override
	public void sycPmByMultiple(List<Map> listMap, int cutoverFlag)
			throws CommonException {
		// TODO Auto-generated method stub
		String processKey = null;
		if (listMap.get(0).get("processKey") != null)
			processKey = listMap.get(0).get("processKey").toString();
		try {
			Map select = null;
			Map update = null;

			if (listMap != null && listMap.size() > 0) {
				for (int i = 0; i < listMap.size(); i++) {
					if (CommonDefine.getIsCanceled(getSessionId(), processKey)) {
						CommonDefine.respCancel(getSessionId(), processKey);
						break;
					}
					

					Map map = listMap.get(i);
					List listPort = getPortList(map);
					// 获取需要更新的光复用段的端口
					select = hashMapSon("t_pm_multi_sec_ptp", "MULTI_SEC_ID",
							map.get("PM_MULTI_SEC_ID"), null, null, null);
					List<Map> list = circuitManagerMapper
							.getByParameter(select);

					// 采集数据
					List<PmDataModel> listPm = getCurrentPmData(listPort);

					List<Map> updateList = new ArrayList<Map>();
					// 更新pm端口值
					for (Map ma : list) {
						Map addMap = new HashMap();
						addMap.put("PM_MULTI_SEC_PTP_ID",
								ma.get("PM_MULTI_SEC_PTP_ID"));
						// 更新主用端口值
						if (ma.get("ROUTE_TYPE") != null
								&& (CommonDefine.PM.SECTON_ROUTE_TYPE.PTP + "")
										.equals(ma.get("ROUTE_TYPE").toString())) {
							for (PmDataModel pmDataModel : listPm) {
								if (ma.get("PTP_ID") != null
										&& pmDataModel
												.getPtpId()
												.toString()
												.equals(ma.get("PTP_ID")
														.toString())) {
									List<PmMeasurementModel> pmMeasurementList = pmDataModel
											.getPmMeasurementList();
									if (ma.get("PM_TYPE") != null
											&& (CommonDefine.PM.PORT_TYPE.PORT_IN + "")
													.equals(ma.get("PM_TYPE")
															.toString())) {
										for (PmMeasurementModel pmMeasurementModel : pmMeasurementList) {
											// 输入光功率当前值
											if (CommonDefine.PM.STD_INDEX_RPL_CUR
													.equals(pmMeasurementModel
															.getPmStdIndex())
													|| CommonDefine.PM.STD_INDEX_RPL_AVG
															.equals(pmMeasurementModel
																	.getPmStdIndex())) {
												//
												addMap.put("CURRENT_PM_VALUE",
														pmMeasurementModel
																.getValue());
												addMap.put("CURRENT_PM_TIME",
														new Date());
												break;
											}
										}
									} else if (ma.get("PM_TYPE") != null
											&& (CommonDefine.PM.PORT_TYPE.PORT_OUT + "")
													.equals(ma.get("PM_TYPE")
															.toString())) {
										for (PmMeasurementModel pmMeasurementModel : pmMeasurementList) {
											// 输出光功率当前值
											if (CommonDefine.PM.STD_INDEX_TPL_CUR
													.equals(pmMeasurementModel
															.getPmStdIndex())
													|| CommonDefine.PM.STD_INDEX_TPL_AVG
															.equals(pmMeasurementModel
																	.getPmStdIndex())) {
												//
												addMap.put("CURRENT_PM_VALUE",
														pmMeasurementModel
																.getValue());
												addMap.put("CURRENT_PM_TIME",
														new Date());
												break;
											}
										}

									}

									// 结束本次循环
									// break;
								}
							}
						} else if (ma.get("ROUTE_TYPE") != null
								&& (CommonDefine.PM.SECTON_ROUTE_TYPE.VIR_PORT + "")
										.equals(ma.get("ROUTE_TYPE").toString())) {// 更新主用虚拟端口

							double value = 0.0;

							if (ma.get("PTP_ID") != null) {
								String ids[] = ma.get("PTP_ID").toString()
										.split(",");
								for (String id : ids) {
									for (PmDataModel pmDataModel : listPm) {
										if (pmDataModel.getPtpId().toString()
												.equals(id)) {
											List<PmMeasurementModel> pmMeasurementList = pmDataModel
													.getPmMeasurementList();
											if (ma.get("PM_TYPE") != null
													&& (CommonDefine.PM.PORT_TYPE.PORT_IN + "")
															.equals(ma
																	.get("PM_TYPE"))) {
												for (PmMeasurementModel pmMeasurementModel : pmMeasurementList) {
													// 输入光功率当前值
													if (CommonDefine.PM.STD_INDEX_RPL_CUR
															.equals(pmMeasurementModel
																	.getPmStdIndex())
															|| CommonDefine.PM.STD_INDEX_RPL_AVG
																	.equals(pmMeasurementModel
																			.getPmStdIndex())) {
														value += Math
																.pow(10,
																		Double.valueOf(pmMeasurementModel
																				.getValue()) / 10);

														break;
													}
												}
											} else if (ma.get("PM_TYPE") != null
													&& (CommonDefine.PM.PORT_TYPE.PORT_OUT + "")
															.equals(ma.get(
																	"PM_TYPE")
																	.toString())) {
												for (PmMeasurementModel pmMeasurementModel : pmMeasurementList) {
													// 输出光功率当前值
													if (CommonDefine.PM.STD_INDEX_TPL_CUR
															.equals(pmMeasurementModel
																	.getPmStdIndex())
															|| CommonDefine.PM.STD_INDEX_TPL_AVG
																	.equals(pmMeasurementModel
																			.getPmStdIndex())) {
														value += Math
																.pow(10,
																		Double.valueOf(pmMeasurementModel
																				.getValue()) / 10);
														break;
													}
												}

											}

											// 结束本次循环
											// break;
										}
									}
								}
							}

							if (value != 0) {
								value = 10 * Math.log10(value);
							}
							addMap.put("CURRENT_PM_VALUE", getDouble(value));
							addMap.put("CURRENT_PM_TIME", new Date());
						}

						if (ma.get("SUB_ROUTE_TYPE") != null
								&& (CommonDefine.PM.SECTON_ROUTE_TYPE.PTP + "")
										.equals(ma.get("SUB_ROUTE_TYPE")
												.toString())) {
							for (PmDataModel pmDataModel : listPm) {
								if (ma.get("SUB_PTP_ID") != null
										&& pmDataModel.getPtpId().toString()
												.equals(ma.get("SUB_PTP_ID"))) {
									List<PmMeasurementModel> pmMeasurementList = pmDataModel
											.getPmMeasurementList();
									if (ma.get("SUB_PM_TYPE") != null
											&& (CommonDefine.PM.PORT_TYPE.PORT_IN + "")
													.equals(ma
															.get("SUB_PM_TYPE"))) {
										for (PmMeasurementModel pmMeasurementModel : pmMeasurementList) {
											// 输入光功率当前值
											if (CommonDefine.PM.STD_INDEX_RPL_CUR
													.equals(pmMeasurementModel
															.getPmStdIndex())
													|| CommonDefine.PM.STD_INDEX_RPL_AVG
															.equals(pmMeasurementModel
																	.getPmStdIndex())) {
												//
												addMap.put(
														"SUB_CURRENT_PM_VALUE",
														pmMeasurementModel
																.getValue());
												addMap.put("CURRENT_PM_TIME",
														new Date());

												break;
											}
										}
									} else if (ma.get("SUB_PM_TYPE") != null
											&& (CommonDefine.PM.PORT_TYPE.PORT_OUT + "")
													.equals(ma.get(
															"SUB_PM_TYPE")
															.toString())) {
										for (PmMeasurementModel pmMeasurementModel : pmMeasurementList) {
											// 输出光功率当前值
											if (CommonDefine.PM.STD_INDEX_TPL_CUR
													.equals(pmMeasurementModel
															.getPmStdIndex())
													|| CommonDefine.PM.STD_INDEX_TPL_AVG
															.equals(pmMeasurementModel
																	.getPmStdIndex())) {
												//
												addMap.put(
														"SUB_CURRENT_PM_VALUE",
														pmMeasurementModel
																.getValue());
												addMap.put("CURRENT_PM_TIME",
														new Date());
												break;
											}
										}

									}

									// 结束本次循环
									// break;
								}
							}
						} else if (ma.get("SUB_ROUTE_TYPE") != null
								&& (CommonDefine.PM.SECTON_ROUTE_TYPE.VIR_PORT + "")
										.equals(ma.get("SUB_ROUTE_TYPE"))) {
							// 虚拟端口
							if (ma.get("SUB_PTP_ID") != null) {
								String ids[] = ma.get("SUB_PTP_ID").toString()
										.split(",");
								for (String id : ids) {
									// 更新主用虚拟端口
									double value = 0.0;
									for (PmDataModel pmDataModel : listPm) {
										if (pmDataModel.getPtpId().toString()
												.equals(id)) {
											List<PmMeasurementModel> pmMeasurementList = pmDataModel
													.getPmMeasurementList();
											if (ma.get("SUB_PM_TYPE") != null
													&& (CommonDefine.PM.PORT_TYPE.PORT_IN + "")
															.equals(ma
																	.get("SUB_PM_TYPE")
																	.toString())) {
												for (PmMeasurementModel pmMeasurementModel : pmMeasurementList) {
													// 输入光功率当前值
													if (CommonDefine.PM.STD_INDEX_RPL_CUR
															.equals(pmMeasurementModel
																	.getPmStdIndex())
															|| CommonDefine.PM.STD_INDEX_RPL_AVG
																	.equals(pmMeasurementModel
																			.getPmStdIndex())) {
														value += Math
																.pow(10,
																		Double.valueOf(pmMeasurementModel
																				.getValue()) / 10);

														break;
													}
												}
											} else if (ma.get("SUB_PM_TYPE") != null
													&& (CommonDefine.PM.PORT_TYPE.PORT_OUT + "")
															.equals(ma
																	.get("SUB_PM_TYPE")
																	.toString())) {
												for (PmMeasurementModel pmMeasurementModel : pmMeasurementList) {
													// 输出光功率当前值
													if (CommonDefine.PM.STD_INDEX_TPL_CUR
															.equals(pmMeasurementModel
																	.getPmStdIndex())
															|| CommonDefine.PM.STD_INDEX_TPL_AVG
																	.equals(pmMeasurementModel
																			.getPmStdIndex())) {
														value += Math
																.pow(10,
																		Double.valueOf(pmMeasurementModel
																				.getValue()) / 10);
														break;
													}
												}

											}

											// 结束本次循环
											// break;
										}
									}
									if (value != 0) {
										value = 10 * Math.log10(value);
									}
									addMap.put("SUB_CURRENT_PM_VALUE",
											getDouble(value));
									addMap.put("CURRENT_PM_TIME", new Date());
								}
							}
						}

						updateList.add(addMap);
					}
					String curr = "CURRENT_PM_VALUE";
					if (cutoverFlag == 1) {// 割接前
						curr = "PM_BEFORE_CUTOVER";
						// 更新光复用段当前值
						for (Map up : updateList) {
							pmMultipleSectionManagerMapper
									.updateMSPtpBeforeCutover(up);

						}
						// 更新光复用段时间
						if (updateList != null && updateList.size() > 0) {
							pmMultipleSectionManagerMapper
									.updateMSBeforeCutover(map);
						}
					} else if (cutoverFlag == 2) {// 割接后
						curr = "PM_AFTER_CUTOVER";
						// 更新光复用段当前值
						for (Map up : updateList) {
							pmMultipleSectionManagerMapper
									.updateMSPtpAfterCutover(up);
						}
						// 更新光复用段时间
						if (updateList != null && updateList.size() > 0) {
							pmMultipleSectionManagerMapper
									.updateMSAfterCutover(map);
						}

						insertMultipleStateCutover(map);
					} else {
						// 更新光复用段当前值
						for (Map up : updateList) {
							update = new HashMap();
							update.put("NAME", "t_pm_multi_sec_ptp");
							update.put("ID_NAME", "PM_MULTI_SEC_PTP_ID");
							update.put("ID_VALUE",
									up.get("PM_MULTI_SEC_PTP_ID"));
							if (up.get("CURRENT_PM_VALUE") != null) {
								update.put("ID_NAME_2", "CURRENT_PM_VALUE");
								update.put("ID_VALUE_2",
										up.get("CURRENT_PM_VALUE"));
							}
							if (up.get("SUB_CURRENT_PM_VALUE") != null) {
								update.put("ID_NAME_3", "SUB_CURRENT_PM_VALUE");
								update.put("ID_VALUE_3",
										up.get("SUB_CURRENT_PM_VALUE"));
							}

							if (up.get("CURRENT_PM_TIME") != null) {
								update.put("ID_NAME_4", "CURRENT_PM_TIME");
								update.put("ID_VALUE_4",
										up.get("CURRENT_PM_TIME"));

							}
							update.put("ID_NAME_4", "PM_MULTI_SEC_PTP_ID");
							update.put("ID_VALUE_4",
									up.get("PM_MULTI_SEC_PTP_ID"));

							circuitManagerMapper.updateByParameter(update);
						}
						// 更新光复用段时间

						update = new HashMap();
						update.put("NAME", "t_pm_multi_sec");
						update.put("ID_NAME", "PM_MULTI_SEC_ID");
						update.put("ID_VALUE", map.get("PM_MULTI_SEC_ID"));
						update.put("ID_NAME_2", "PM_UPDATE_TIME");
						update.put("ID_VALUE_2", new Date());

						circuitManagerMapper.updateByParameter(update);

					}
					// 先重新取一遍光复用段
					// select = hashMapSon("t_pm_multi_sec_ptp",
					// "MULTI_SEC_ID",map.get("PM_MULTI_SEC_ID"), "DIRECTION",
					// 1, null);
					select = new HashMap();
					select.put("MULTI_SEC_ID", map.get("PM_MULTI_SEC_ID"));
					select.put("DIRECTION", 1);
					// hashMapSon("t_pm_multi_sec_ptp",
					// "MULTI_SEC_ID",listMap.get(0).get("PM_MULTI_SEC_ID"),
					// "DIRECTION", 1, null);
					List<Map> listz = pmMultipleSectionManagerMapper
							.selectMultiplePtpRoute(select);
					// 计算当前性能段衰耗以及衰耗值
					updateDuanValue(listz, curr, cutoverFlag);

					// 先重新取一遍光复用段
					// select = hashMapSon("t_pm_multi_sec_ptp",
					// "MULTI_SEC_ID",map.get("PM_MULTI_SEC_ID"), "DIRECTION",
					// 2, null);

					select = new HashMap();
					select.put("MULTI_SEC_ID", map.get("PM_MULTI_SEC_ID"));
					select.put("DIRECTION", 2);
					// hashMapSon("t_pm_multi_sec_ptp",
					// "MULTI_SEC_ID",listMap.get(0).get("PM_MULTI_SEC_ID"),
					// "DIRECTION", 1, null);
					List<Map> listf = pmMultipleSectionManagerMapper
							.selectMultiplePtpRoute(select);
					// 计算当前性能段衰耗以及衰耗值
					updateDuanValue(listf, curr, cutoverFlag);

					// 统计复用段告警信息
					getMultipleState(map);
					
					// 进度描述信息更改--此处修改
					/*String text = "当前进度" + (i + 1) + "/" + listMap.size();
					// 加入进度值
					CommonDefine.setProcessParameter(
							getSessionId(),
							processKey,
							text,
							Double.valueOf((i + 1)
									/ ((double) (listMap.size()))));*/
					CommonDefine.setProcessParameter(
							getSessionId(),
							processKey,
							(i + 1),
							(listMap.size()),null);
				}
			} else {
				// 进度描述信息更改--此处修改
				/*String text = "当前进度0/0";
				// 加入进度值
				CommonDefine.setProcessParameter(getSessionId(), processKey, text,
						Double.valueOf(1));*/
				CommonDefine.setProcessParameter(getSessionId(), processKey, 0,0,null);
			}
		} catch (CommonException e) {
			// 进度描述信息更改--此处修改
			/*String text = "当前进度0/0";
			// 加入进度值
			CommonDefine.setProcessParameter(getSessionId(), processKey, text,
					Double.valueOf(1));*/
			CommonDefine.respCancel(getSessionId(), processKey);
			throw e;
		} catch (Exception e) {
			throw new CommonException(new Exception(),
					MessageCodeDefine.CIR_EXCPT_SELECT);
		}
		// catch (Exception e) {
		// // 进度描述信息更改--此处修改
		// String text = "当前进度0/0";
		// // 加入进度值
		// CommonDefine.setProcessParameter(getSessionId(), processKey, text, Double
		// .valueOf(1));
		// }

	}

	/**
	 * 更新段衰耗，衰耗器的值
	 * 
	 * @param listAfter
	 * @param name
	 *            字段名 当前性能还是历史性能
	 * @param log
	 *            是割接用还是光复用段 1 割接前 2 割接后 其他正常
	 */
	private void updateDuanValue(List<Map> listAfter, String name, int log) {
		String sub_name = "SUB_" + name;
		if (log == 1 || log == 2) {
			sub_name = name + "_SUB";
		}
		for (int k = 0; k < listAfter.size(); k++) {

			Map mapAfter = listAfter.get(k);
			// 如果是起点或者结尾，则直接跳过
			if (k == 0 || k == listAfter.size() - 1) {
				continue;
			}
			// 判断是否是衰耗值
			if (mapAfter.get("PM_TYPE") != null
					&& (CommonDefine.PM.SECTON_ROUTE_TYPE.DOWN + "")
							.equals(mapAfter.get("PM_TYPE").toString())) {
				// 判断上一跳是否是输出以及下一跳是否是输入
				if (listAfter.get(k - 1).get("PM_TYPE") != null
						&& (CommonDefine.PM.PORT_TYPE.PORT_OUT + "")
								.equals(listAfter.get(k - 1).get("PM_TYPE")
										.toString())
						&& listAfter.get(k + 1).get("PM_TYPE") != null
						&& (CommonDefine.PM.PORT_TYPE.PORT_IN + "")
								.equals(listAfter.get(k + 1).get("PM_TYPE")
										.toString())) {
					// 计算衰耗值 = 上游输出光功率-下游输入光功率
					// 判断上下游有值的情况下
					if (listAfter.get(k - 1).get(name) != null
							&& (!listAfter.get(k - 1).get(name).toString()
									.isEmpty())
							&& listAfter.get(k + 1).get(name) != null
							&& (!listAfter.get(k + 1).get(name).toString()
									.isEmpty())) {
						// 计算衰耗值
						double shzz = getDouble(Double.valueOf(listAfter
								.get(k - 1).get(name).toString()))
								- getDouble(Double.valueOf(listAfter.get(k + 1)
										.get(name).toString()));
						// 先记录下，回头处理
						mapAfter.put(name, getDouble(shzz));
					}
				}
			}
			// 判断是否是衰耗值
			if (mapAfter.get("SUB_PM_TYPE") != null
					&& (CommonDefine.PM.SECTON_ROUTE_TYPE.DOWN + "")
							.equals(mapAfter.get("SUB_PM_TYPE").toString())) {
				// 判断上一跳是否是输出以及下一跳是否是输入
				if (listAfter.get(k - 1).get("SUB_PM_TYPE") != null
						&& (CommonDefine.PM.PORT_TYPE.PORT_OUT + "")
								.equals(listAfter.get(k - 1).get("SUB_PM_TYPE")
										.toString())
						&& listAfter.get(k + 1).get("SUB_PM_TYPE") != null
						&& (CommonDefine.PM.PORT_TYPE.PORT_IN + "")
								.equals(listAfter.get(k + 1).get("SUB_PM_TYPE")
										.toString())) {
					// 计算衰耗值 = 上游输出光功率-下游输入光功率
					// 判断上下游有值的情况下
					if (listAfter.get(k - 1).get(sub_name) != null
							&& (!listAfter.get(k - 1).get(sub_name).toString()
									.isEmpty())
							&& listAfter.get(k + 1).get(sub_name) != null
							&& (!listAfter.get(k + 1).get(sub_name).toString()
									.isEmpty())) {
						// 计算衰耗值
						double shzb = getDouble(Double.valueOf(listAfter
								.get(k - 1).get(sub_name).toString()))
								- getDouble(Double.valueOf(listAfter.get(k + 1)
										.get(sub_name).toString()));
						// 先记录下，回头处理
						mapAfter.put(sub_name, getDouble(shzb));
					}
				}
			}

			// 判断是否是段衰耗
			if (mapAfter.get("PM_TYPE") != null
					&& (CommonDefine.PM.SECTON_ROUTE_TYPE.PART_DOWN + "")
							.equals(mapAfter.get("PM_TYPE").toString())) {
				// 记录上下网元id，不会跨两个网元
				String upNeId = listAfter.get(k - 1)
						.get("MULTI_SECT_NE_ROUTE_ID").toString();
				String downNeId = listAfter.get(k + 1)
						.get("MULTI_SECT_NE_ROUTE_ID").toString();
				// 记录上下的性能值
				double upPm = 0.0;
				double downPm = 0.0;
				// 向上查找上一跳的光放板卡输出光功率，光放板卡判定标准，光放型号id不为空，且不能跨越两个网元
				for (int l = k - 1; l >= 0; l--) {
					// 是否跨网元
					if (listAfter.get(l).get("MULTI_SECT_NE_ROUTE_ID") != null
							&& upNeId.equals(listAfter.get(l)
									.get("MULTI_SECT_NE_ROUTE_ID").toString())) {
						// 查找上游输出光功率, 首先判断是否是光放盘
						if (listAfter.get(l).get("PM_STD_OPT_AMP_ID") != null
								&& listAfter.get(l).get("PM_STD_OPT_AMP_ID")
										.toString().length() > 0) {
							// 再次判断是否是输出光功率
							if (listAfter.get(l).get("PM_TYPE") != null
									&& (CommonDefine.PM.PORT_TYPE.PORT_OUT + "")
											.equals(listAfter.get(l)
													.get("PM_TYPE").toString())) {
								// 如果存在当前值，则赋值，并继续，否则表示没有取到当前值，
								if (listAfter.get(l).get(name) != null
										&& !listAfter.get(l).get(name)
												.toString().isEmpty()) {
									upPm = Double.valueOf(listAfter.get(l)
											.get(name).toString());
									break;
								}
							}
						}
					}
				}
				// 向下查找下游输入光功率
				for (int m = k + 1; m < listAfter.size(); m++) {
					// 是否跨网元
					if (listAfter.get(m).get("MULTI_SECT_NE_ROUTE_ID") != null
							&& downNeId.equals(listAfter.get(m)
									.get("MULTI_SECT_NE_ROUTE_ID").toString())) {
						// 查找上游输出光功率, 首先判断是否是光放盘
						if (listAfter.get(m).get("PM_STD_OPT_AMP_ID") != null
								&& listAfter.get(m).get("PM_STD_OPT_AMP_ID")
										.toString().length() > 0) {
							// 再次判断是否是输出光功率
							if (listAfter.get(m).get("PM_TYPE") != null
									&& (CommonDefine.PM.PORT_TYPE.PORT_IN + "")
											.equals(listAfter.get(m)
													.get("PM_TYPE").toString())) {
								// 如果存在当前值，则赋值，并继续，否则表示没有取到当前值，
								if (listAfter.get(m).get(name) != null
										&& !listAfter.get(m).get(name)
												.toString().isEmpty()) {
									downPm = Double.valueOf(listAfter.get(m)
											.get(name).toString());
									break;
								}
							}
						}
					}
				}
				// 计算当前段衰耗
				if (upPm != 0 && downPm != 0) {
					double dsh = getDouble(upPm) - getDouble(downPm);
					mapAfter.put(name, getDouble(dsh));
				}
			}

			// 判断是否是段衰耗 备用
			if (mapAfter.get("SUB_PM_TYPE") != null
					&& (CommonDefine.PM.SECTON_ROUTE_TYPE.PART_DOWN + "")
							.equals(mapAfter.get("SUB_PM_TYPE").toString())) {
				// 记录上下网元id，不会跨两个网元
				String upNeId = listAfter.get(k - 1)
						.get("MULTI_SECT_NE_ROUTE_ID").toString();
				String downNeId = listAfter.get(k + 1)
						.get("MULTI_SECT_NE_ROUTE_ID").toString();
				// 记录上下的性能值
				double upPm = 0.0;
				double downPm = 0.0;
				// 向上查找上一跳的光放板卡输出光功率，光放板卡判定标准，光放型号id不为空，且不能跨越两个网元
				for (int l = k - 1; l >= 0; l--) {
					// 是否跨网元
					if (listAfter.get(l).get("MULTI_SECT_NE_ROUTE_ID") != null
							&& upNeId.equals(listAfter.get(l)
									.get("MULTI_SECT_NE_ROUTE_ID").toString())) {
						// 查找上游输出光功率, 首先判断是否是光放盘
						if (listAfter.get(l).get("SUB_PM_STD_OPT_AMP_ID") != null
								&& listAfter.get(l)
										.get("SUB_PM_STD_OPT_AMP_ID")
										.toString().length() > 0) {
							// 再次判断是否是输出光功率
							if (listAfter.get(l).get("SUB_PM_TYPE") != null
									&& (CommonDefine.PM.PORT_TYPE.PORT_OUT + "")
											.equals(listAfter.get(l)
													.get("SUB_PM_TYPE")
													.toString())) {
								// 如果存在当前值，则赋值，并继续，否则表示没有取到当前值，
								if (listAfter.get(l).get(sub_name) != null
										&& !listAfter.get(l).get(sub_name)
												.toString().isEmpty()) {
									upPm = Double.valueOf(listAfter.get(l)
											.get(sub_name).toString());
									break;
								}
							}
						}
					}
				}
				// 向下查找下游输入光功率
				for (int m = k + 1; m < listAfter.size(); m++) {
					// 是否跨网元
					if (listAfter.get(m).get("MULTI_SECT_NE_ROUTE_ID") != null
							&& downNeId.equals(listAfter.get(m)
									.get("MULTI_SECT_NE_ROUTE_ID").toString())) {
						// 查找上游输出光功率, 首先判断是否是光放盘
						if (listAfter.get(m).get("SUB_PM_STD_OPT_AMP_ID") != null
								&& listAfter.get(m)
										.get("SUB_PM_STD_OPT_AMP_ID")
										.toString().length() > 0) {
							// 再次判断是否是输出光功率
							if (listAfter.get(m).get("SUB_PM_TYPE") != null
									&& (CommonDefine.PM.PORT_TYPE.PORT_IN + "")
											.equals(listAfter.get(m)
													.get("SUB_PM_TYPE")
													.toString())) {
								// 如果存在当前值，则赋值，并继续，否则表示没有取到当前值，
								if (listAfter.get(m).get(sub_name) != null
										&& !listAfter.get(m).get(sub_name)
												.toString().isEmpty()) {
									downPm = Double.valueOf(listAfter.get(m)
											.get(sub_name).toString());
									break;
								}
							}
						}
					}
				}
				// 计算当前段衰耗
				if (upPm != 0 && downPm != 0) {
					double dsh = getDouble(upPm) - getDouble(downPm);
					mapAfter.put(sub_name, getDouble(dsh));
				}
			}
			// 若非复用段报表调用则入库
			if (log != 4) {
				Map update = new HashMap();
				update.put("NAME", "t_pm_multi_sec_ptp");
				update.put("ID_NAME", "PM_MULTI_SEC_PTP_ID");
				update.put("ID_VALUE", mapAfter.get("PM_MULTI_SEC_PTP_ID"));
				update.put("ID_NAME_2", name);
				update.put("ID_VALUE_2", mapAfter.get(name));
				update.put("ID_NAME_3", sub_name);
				update.put("ID_VALUE_3", mapAfter.get(sub_name));
				// 割接后值，计算衰耗前后差值
				if (log == 2) {
					if (mapAfter.get("PM_AFTER_CUTOVER") != null
							&& !mapAfter.get("PM_AFTER_CUTOVER").toString()
									.isEmpty()) {
						if (mapAfter.get("PM_BEFORE_CUTOVER") != null
								&& !mapAfter.get("PM_BEFORE_CUTOVER")
										.toString().isEmpty()) {
							double differ = Double.valueOf(mapAfter.get(
									"PM_AFTER_CUTOVER").toString())
									- Double.valueOf(mapAfter.get(
											"PM_AFTER_CUTOVER").toString());
							update.put("ID_NAME_4", "PM_DIFF_CUTOVER");
							update.put("ID_VALUE_4", getDouble(differ));
						}
					}
					if (mapAfter.get("PM_AFTER_CUTOVER_SUB") != null
							&& !mapAfter.get("PM_AFTER_CUTOVER_SUB").toString()
									.isEmpty()) {
						if (mapAfter.get("PM_BEFORE_CUTOVER_SUB") != null
								&& !mapAfter.get("PM_BEFORE_CUTOVER_SUB")
										.toString().isEmpty()) {
							double differ = Double.valueOf(mapAfter.get(
									"PM_AFTER_CUTOVER_SUB").toString())
									- Double.valueOf(mapAfter.get(
											"PM_AFTER_CUTOVER_SUB").toString());
							update.put("ID_NAME_5", "PM_DIFF_CUTOVER_SUB");
							update.put("ID_VALUE_5", getDouble(differ));
						}
					}

				}
				circuitManagerMapper.updateByParameter(update);
			}
		}
	}

	private void insertMultipleStateCutover(Map map) {
		Map update = null;
		double value = pmMultipleSectionManagerMapper.getGreatestDiff(map);
		int level = getLevel(0, value);
		update = new HashMap();
		update.put("NAME", "t_pm_multi_sec");
		update.put("ID_NAME", "PM_MULTI_SEC_ID");
		update.put("ID_VALUE", map.get("PM_MULTI_SEC_ID"));
		update.put("ID_NAME_2", "SEC_STATE_CUTOVER");
		update.put("ID_VALUE_2", level);

		circuitManagerMapper.updateByParameter(update);
		// TODO Auto-generated method stub

	}

	public void getMultipleState(Map map) {
		Map update = null;
		int level = multiState(map);
		update = new HashMap();
		update.put("NAME", "t_pm_multi_sec");
		update.put("ID_NAME", "PM_MULTI_SEC_ID");
		update.put("ID_VALUE", map.get("PM_MULTI_SEC_ID"));
		update.put("ID_NAME_2", "SEC_STATE");
		update.put("ID_VALUE_2", level);

		circuitManagerMapper.updateByParameter(update);
	}

	public int getLevel(int level, double value) {
		double level1 = PmMessageHandler.getPmMessage("level1");
		double level2 = PmMessageHandler.getPmMessage("level2");
		double level3 = PmMessageHandler.getPmMessage("level3");
		if (value < level1) {
			return CommonDefine.PM.MUL.SEC_PM_ZC;
		} else if (value < level2) {
			return CommonDefine.PM.MUL.SEC_PM_YB;
		} else if (value < level3) {
			return CommonDefine.PM.MUL.SEC_PM_CY;
		} else {
			return CommonDefine.PM.MUL.SEC_PM_ZY;
		}

	}

	public int multiState(Map map) {
		Map select = null;
		Map update = null;
		// 查询出所有记录
		select = hashMapSon("t_pm_multi_sec_ptp", "MULTI_SEC_ID",
				map.get("PM_MULTI_SEC_ID"), null, null, null);
		List<Map> listSec = circuitManagerMapper.getByParameter(select);
		int level = CommonDefine.PM.MUL.SEC_PM_ZC;
		int levelTemp = 0;

		for (Map mapSec : listSec) {
			double value = 0.0;
			double curPm = 0.0;
			double stdPm = 0.0;
			double subCurPm = 0.0;
			double stdStdPm = 0.0;
			// 如果主用侧是端口或者虚拟端口，则进行计算
			if (mapSec.get("ROUTE_TYPE") != null
					&& (mapSec.get("ROUTE_TYPE").toString()
							.equals(CommonDefine.PM.SECTON_ROUTE_TYPE.PTP + "") || mapSec
							.get("ROUTE_TYPE")
							.toString()
							.equals(CommonDefine.PM.SECTON_ROUTE_TYPE.VIR_PORT
									+ ""))) {
				if (mapSec.get("CURRENT_PM_VALUE") != null
						&& !mapSec.get("CURRENT_PM_VALUE").toString().isEmpty()) {
					curPm = Double.parseDouble(mapSec.get("CURRENT_PM_VALUE")
							.toString());
				}
				if (mapSec.get("CUT_PM_VALUE") != null
						&& !mapSec.get("CUT_PM_VALUE").toString().isEmpty()) {
					if (isDouble(mapSec.get("CUT_PM_VALUE"))) {
						stdPm = Double.parseDouble(mapSec.get("CUT_PM_VALUE")
								.toString());
					} else {
						stdPm = 0.0;
					}

				}
				value = Math.abs(stdPm - curPm);
				// 判断级别
				levelTemp = getLevel(level, value);
				if (levelTemp > level) {
					level = levelTemp;
				}
			}

			// 如果备用侧是端口或者虚拟端口，则进行计算
			if (mapSec.get("SUB_ROUTE_TYPE") != null
					&& (mapSec.get("SUB_ROUTE_TYPE").toString()
							.equals(CommonDefine.PM.SECTON_ROUTE_TYPE.PTP + "") || mapSec
							.get("SUB_ROUTE_TYPE")
							.toString()
							.equals(CommonDefine.PM.SECTON_ROUTE_TYPE.VIR_PORT
									+ ""))) {
				if (mapSec.get("SUB_CURRENT_PM_VALUE") != null
						&& !mapSec.get("SUB_CURRENT_PM_VALUE").toString()
								.isEmpty()) {
					subCurPm = Double.parseDouble(mapSec.get(
							"SUB_CURRENT_PM_VALUE").toString());
				}
				if (mapSec.get("SUB_CUT_PM_VALUE") != null
						&& !mapSec.get("SUB_CUT_PM_VALUE").toString().isEmpty()) {
					stdStdPm = Double.parseDouble(mapSec
							.get("SUB_CUT_PM_VALUE").toString());
				}
				value = Math.abs(subCurPm - stdStdPm);
				// 判断级别
				levelTemp = getLevel(level, value);
				if (levelTemp > level) {
					level = levelTemp;
				}
			}
		}
		return level;
	}

	public List getPortList(Map map) {
		Map select = null;
		List listPort = new ArrayList();
		// 获取需要更新的光复用段的端口
		if (map.get("PM_MULTI_SEC_ID") != null) {
			select = hashMapSon("t_pm_multi_sec_ptp", "MULTI_SEC_ID",
					map.get("PM_MULTI_SEC_ID"), null, null, null);
		} else {
			select = hashMapSon("t_pm_multi_sec_ptp", "MULTI_SEC_ID",
					map.get("MULTI_SEC_ID"), null, null, null);
		}

		List<Map> list = circuitManagerMapper.getByParameter(select);
		for (Map ma : list) {

			// 判断是否是端口
			if (ma.get("ROUTE_TYPE") != null
					&& (CommonDefine.PM.SECTON_ROUTE_TYPE.PTP + "").equals(ma
							.get("ROUTE_TYPE").toString())) {
				listPort.add(ma.get("PTP_ID").toString().replace(",", "")
						.trim());
			} else if (ma.get("ROUTE_TYPE") != null
					&& (CommonDefine.PM.SECTON_ROUTE_TYPE.VIR_PORT + "")
							.equals(ma.get("ROUTE_TYPE").toString())) {
				// 虚拟端口
				if (ma.get("PTP_ID") != null) {
					String ids[] = ma.get("PTP_ID").toString().split(",");
					for (String id : ids) {
						listPort.add(id.replace(",", "").trim());
					}
				}
			}

			if (ma.get("SUB_ROUTE_TYPE") != null
					&& (CommonDefine.PM.SECTON_ROUTE_TYPE.PTP + "").equals(ma
							.get("SUB_ROUTE_TYPE").toString())) {
				listPort.add(ma.get("SUB_PTP_ID").toString().replace(",", "")
						.trim());
			} else if (ma.get("SUB_ROUTE_TYPE") != null
					&& (CommonDefine.PM.SECTON_ROUTE_TYPE.VIR_PORT + "")
							.equals(ma.get("SUB_ROUTE_TYPE").toString())) {
				// 虚拟端口
				if (ma.get("SUB_PTP_ID") != null) {
					String ids[] = ma.get("SUB_PTP_ID").toString().split(",");
					for (String id : ids) {
						listPort.add(id.replace(",", "").trim());
					}
				}
			}
		}
		return listPort;
	}

	public List<PmDataModel> getCurrentPmData(List ptpIds)
			throws CommonException {
		List<PmDataModel> pmDataList = null;
		try {
			// 先获取所有PtpId

			// 数据采集与保存
			if (ptpIds.size() > 0) {
				Map select = hashMapSon("t_base_ptp", "base_ptp_id",
						ptpIds.get(0), null, null, null);
				List<Map> listP = circuitManagerMapper.getByParameter(select);
				// 初始化为第一个端口网管ID
				int emsConnectionId = Integer.parseInt(listP.get(0)
						.get("BASE_EMS_CONNECTION_ID").toString());
				List<Integer> ptpIdGroupByEms = new ArrayList<Integer>();
				for (Object obj : ptpIds) {
					if (!obj.toString().isEmpty()) {
						String id = obj.toString().replace(",", "").trim();
						ptpIdGroupByEms.add(Integer.parseInt(id));
					}
				}
				// 采集性能
				IDataCollectServiceProxy dataCollectService = SpringContextUtil
						.getDataCollectServiceProxy(emsConnectionId);
				pmDataList = dataCollectService.getCurrentPmData_PtpList(
						ptpIdGroupByEms, new short[] {}, new int[] {
								CommonDefine.PM.PM_LOCATION_NEAR_END_RX_FLAG,
								CommonDefine.PM.PM_LOCATION_NEAR_END_TX_FLAG },
						new int[] { CommonDefine.PM.GRANULARITY_15MIN_FLAG },
						false, true, false, CommonDefine.COLLECT_LEVEL_1);

			}
		} catch (CommonException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return pmDataList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.IService.IPmMultipleSectionManagerService#sycPmHistory(java
	 * .util.Map)
	 */
	@Override
	public void sycPmHistory(Map map) throws CommonException {
		try {
			Map update = null;
			Map select = null;
			List<Map> updateList = getMulitSecPm(map);
			// 正向
			select = new HashMap();
			if (map.get("PM_MULTI_SEC_ID") != null) {
				select.put("MULTI_SEC_ID", map.get("PM_MULTI_SEC_ID"));
				// select = hashMapSon("t_pm_multi_sec_ptp", "MULTI_SEC_ID",
				// map.get("PM_MULTI_SEC_ID"), "DIRECTION", 1, null);
			} else {
				select.put("MULTI_SEC_ID", map.get("MULTI_SEC_ID"));
				// select = hashMapSon("t_pm_multi_sec_ptp", "MULTI_SEC_ID",
				// map.get("MULTI_SEC_ID"), "DIRECTION", 1, null);
			}
			select.put("DIRECTION", 1);
			// hashMapSon("t_pm_multi_sec_ptp",
			// "MULTI_SEC_ID",listMap.get(0).get("PM_MULTI_SEC_ID"),
			// "DIRECTION", 1, null);
			List<Map> list = pmMultipleSectionManagerMapper
					.selectMultiplePtpRoute(select);

			// List<Map> list = circuitManagerMapper.getByParameter(select);
			updateDuanValue(list, "HISTORY_PM_VALUE", 0);
			// 反向
			select = new HashMap();
			if (map.get("PM_MULTI_SEC_ID") != null) {
				select.put("MULTI_SEC_ID", map.get("PM_MULTI_SEC_ID"));
				// select = hashMapSon("t_pm_multi_sec_ptp", "MULTI_SEC_ID",
				// map.get("PM_MULTI_SEC_ID"), "DIRECTION", 2, null);
			} else {
				select.put("MULTI_SEC_ID", map.get("MULTI_SEC_ID"));
				// select = hashMapSon("t_pm_multi_sec_ptp", "MULTI_SEC_ID",
				// map.get("MULTI_SEC_ID"), "DIRECTION", 2, null);
			}
			select.put("DIRECTION", 2);
			List<Map> listf = pmMultipleSectionManagerMapper
					.selectMultiplePtpRoute(select);
			// List<Map> listf = circuitManagerMapper.getByParameter(select);
			updateDuanValue(listf, "HISTORY_PM_VALUE", 0);

			// 更新光复用段当前值
			for (Map up : updateList) {
				update = new HashMap();
				update.put("NAME", "t_pm_multi_sec_ptp");
				update.put("ID_NAME", "PM_MULTI_SEC_PTP_ID");
				update.put("ID_VALUE", up.get("PM_MULTI_SEC_PTP_ID"));
				// 判断性能值是否为0，如果是则不更新
				if (up.get("HISTORY_PM_VALUE") != null
						&& Double.parseDouble(up.get("HISTORY_PM_VALUE")
								.toString()) != 0) {
					update.put("ID_NAME_2", "HISTORY_PM_VALUE");
					update.put("ID_VALUE_2", up.get("HISTORY_PM_VALUE"));
				}
				if (up.get("SUB_HISTORY_PM_VALUE") != null
						&& Double.parseDouble(up.get("SUB_HISTORY_PM_VALUE")
								.toString()) != 0) {
					update.put("ID_NAME_3", "SUB_HISTORY_PM_VALUE");
					update.put("ID_VALUE_3", up.get("SUB_HISTORY_PM_VALUE"));
				}
				update.put("ID_NAME_4", "HISTORY_PM_TIME");
				update.put("ID_VALUE_4", up.get("HISTORY_PM_TIME"));

				circuitManagerMapper.updateByParameter(update);
			}
			// 更新光复用段时间
			if (updateList != null && updateList.size() > 0) {
				update = new HashMap();
				update.put("NAME", "t_pm_multi_sec");
				update.put("ID_NAME", "PM_MULTI_SEC_ID");
				update.put("ID_VALUE", map.get("PM_MULTI_SEC_ID"));
				update.put("ID_NAME_2", "PM_HISTORY_TIME");
				update.put("ID_VALUE_2", map.get("startTime"));

				circuitManagerMapper.updateByParameter(update);
			}

		} catch (CommonException e1) {
			throw e1;
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}

	}

	private List<Map> getMulitSecPm(Map map) throws CommonException {
		// 更新光复用段表的历史性能
		List<Map> updateList = new ArrayList<Map>();

		Map select = null;
		// HttpServletRequest request = ServletActionContext.getRequest();
		Map searchCond = new HashMap();
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdfStart = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		SimpleDateFormat sdfEnd = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<String> tableNameList = new ArrayList();
		// 性能查询开始时间
		Date startTime = new Date();
		Date endTime = new Date();
		try {
			startTime = sdf.parse(map.get("startTime").toString());
			endTime = sdf.parse(map.get("startTime").toString());
			searchCond.put("endTime", sdfEnd.format(startTime));
			searchCond.put("startTime", sdfStart.format(startTime));
			// 查询出emsId，根据ptpid
			List listPort = getPortList(map);
			// 如果查询结果为空，直接返回
			if (listPort == null || listPort.size() < 1) {
				return updateList;
			}

			tableNameList = getPmTableName(startTime, endTime,
					map.get("BASE_EMS_CONNECTION_ID").toString());

			// 获取需要更新的光复用段的端口
			if (map.get("PM_MULTI_SEC_ID") != null) {
				select = hashMapSon("t_pm_multi_sec_ptp", "MULTI_SEC_ID",
						map.get("PM_MULTI_SEC_ID"), null, null, null);
			} else {
				select = hashMapSon("t_pm_multi_sec_ptp", "MULTI_SEC_ID",
						map.get("MULTI_SEC_ID"), null, null, null);
			}
			List<Map> list = circuitManagerMapper.getByParameter(select);

			Integer existance = performanceManagerMapper.getPmTableExistance(
					tableNameList.get(0).toString(),
					SpringContextUtil.getDataBaseParam(CommonDefine.DB_SID));
			List<Map> returnListIn = new ArrayList<Map>();
			List<Map> returnListOut = new ArrayList<Map>();
			if (existance != null && existance == 1) {
				select = hashMapSon("t_base_ems_connection",
						"BASE_EMS_CONNECTION_ID",
						map.get("BASE_EMS_CONNECTION_ID"), null, null, null);

				Map mapFactoy = circuitManagerMapper.getByParameter(select)
						.get(0);
				// 判断是否是中兴网管，如果是去性能平均值，不是则取当前值
				if (mapFactoy.get("FACTORY") != null
						&& (CommonDefine.FACTORY_ZTE_FLAG + "")
								.equals(mapFactoy.get("FACTORY").toString())) {

					returnListIn = pmMultipleSectionManagerMapper
							.generateDiagramNend(searchCond,
									CommonDefine.PM.STD_INDEX_RPL_AVG,
									tableNameList.get(0).toString(), listPort);
					returnListOut = pmMultipleSectionManagerMapper
							.generateDiagramNend(searchCond,
									CommonDefine.PM.STD_INDEX_TPL_AVG,
									tableNameList.get(0).toString(), listPort);
				} else {
					returnListIn = pmMultipleSectionManagerMapper
							.generateDiagramNend(searchCond,
									CommonDefine.PM.STD_INDEX_RPL_CUR,
									tableNameList.get(0).toString(), listPort);
					returnListOut = pmMultipleSectionManagerMapper
							.generateDiagramNend(searchCond,
									CommonDefine.PM.STD_INDEX_TPL_CUR,
									tableNameList.get(0).toString(), listPort);
				}
			}

			// 更新pm端口值
			for (Map ma : list) {
				Map addMap = new HashMap();
				addMap.put("PM_MULTI_SEC_PTP_ID", ma.get("PM_MULTI_SEC_PTP_ID"));
				// 更新主用端口值
				// 主用端口是单端口输入
				if (ma.get("ROUTE_TYPE") != null
						&& (CommonDefine.PM.SECTON_ROUTE_TYPE.PTP + "")
								.equals(ma.get("ROUTE_TYPE").toString())) {
					for (Map mapIn : returnListIn) {
						if (mapIn.get("BASE_PTP_ID") != null
								&& ma.get("PTP_ID") != null
								&& mapIn.get("BASE_PTP_ID").toString()
										.equals(ma.get("PTP_ID").toString())) {
							if (ma.get("PM_TYPE") != null
									&& (CommonDefine.PM.PORT_TYPE.PORT_IN + "")
											.equals(ma.get("PM_TYPE")
													.toString())) {
								addMap.put("HISTORY_PM_VALUE",
										mapIn.get("pmValue"));
								addMap.put("HISTORY_PM_TIME", new Date());

							}

							// 结束本次循环
							break;
						}
					}
					// 主用端口是虚拟端口输入
				} else if (ma.get("ROUTE_TYPE") != null
						&& (CommonDefine.PM.SECTON_ROUTE_TYPE.VIR_PORT + "")

						.equals(ma.get("ROUTE_TYPE").toString())) {// 更新主用虚拟端口

					double value = 0.0;

					if (ma.get("PTP_ID") != null) {
						String ids[] = ma.get("PTP_ID").toString().split(",");
						for (String id : ids) {
							for (Map mapIn : returnListIn) {
								if (mapIn.get("BASE_PTP_ID") != null
										&& mapIn.get("BASE_PTP_ID").toString()
												.equals(id)) {
									if (ma.get("PM_TYPE") != null
											&& (CommonDefine.PM.PORT_TYPE.PORT_IN + "")
													.equals(ma.get("PM_TYPE")
															.toString())) {
										// 输入光功率当前值
										value += Math
												.pow(10, Double.valueOf(mapIn
														.get("pmValue")
														.toString()) / 10);
									}
									// 结束本次循环
									break;
								}
							}
						}
					}

					if (value != 0) {
						value = 10 * Math.log10(value);
					}

					addMap.put("HISTORY_PM_VALUE", getDouble(value));
					addMap.put("HISTORY_PM_TIME", new Date());
				}

				// 主用端口是单端口输出
				if (ma.get("ROUTE_TYPE") != null
						&& (CommonDefine.PM.SECTON_ROUTE_TYPE.PTP + "")
								.equals(ma.get("ROUTE_TYPE").toString())) {
					for (Map mapOut : returnListOut) {
						if (mapOut.get("BASE_PTP_ID") != null
								&& ma.get("PTP_ID") != null
								&& mapOut.get("BASE_PTP_ID").toString()
										.equals(ma.get("PTP_ID").toString())) {
							if (ma.get("PM_TYPE") != null
									&& (CommonDefine.PM.PORT_TYPE.PORT_OUT + "")
											.equals(ma.get("PM_TYPE")
													.toString())) {
								//
								addMap.put("HISTORY_PM_VALUE",
										mapOut.get("pmValue"));
								addMap.put("HISTORY_PM_TIME", new Date());
							}

							// 结束本次循环
							break;
						}
					}
					// 主用端口是虚拟口输出
				} else if (ma.get("ROUTE_TYPE") != null
						&& (CommonDefine.PM.SECTON_ROUTE_TYPE.VIR_PORT + "")
								.equals(ma.get("ROUTE_TYPE").toString())) {
					// 虚拟端口
					if (ma.get("PTP_ID") != null) {
						String ids[] = ma.get("PTP_ID").toString().split(",");
						for (String id : ids) {
							// 更新主用虚拟端口
							double value = 0.0;
							for (Map mapOut : returnListOut) {
								if (mapOut.get("PTP_ID") != null
										&& mapOut.get("PTP_ID").toString()
												.equals(id)) {
									if (ma.get("PM_TYPE") != null
											&& (CommonDefine.PM.PORT_TYPE.PORT_OUT + "")
													.equals(ma.get("PM_TYPE")
															.toString())) {

										// 输出光功率当前值
										value += Math
												.pow(10, Double.valueOf(mapOut
														.get("pmValue")
														.toString()) / 10);
									}

									// 结束本次循环
									break;
								}
							}

							if (value != 0) {
								value = 10 * Math.log10(value);
							}
							addMap.put("HISTORY_PM_VALUE", getDouble(value));
							addMap.put("HISTORY_PM_TIME", new Date());
						}
					}
				}

				// 判断备用端口是单端口输入
				if (ma.get("SUB_ROUTE_TYPE") != null
						&& (CommonDefine.PM.SECTON_ROUTE_TYPE.PTP + "")
								.equals(ma.get("SUB_ROUTE_TYPE").toString())) {
					for (Map mapIn : returnListIn) {
						if (mapIn.get("BASE_PTP_ID") != null
								&& ma.get("SUB_PTP_ID") != null
								&& mapIn.get("BASE_PTP_ID")
										.toString()
										.equals(ma.get("SUB_PTP_ID").toString())) {
							if (ma.get("SUB_PM_TYPE") != null
									&& (CommonDefine.PM.PORT_TYPE.PORT_IN + "")
											.equals(ma.get("SUB_PM_TYPE")
													.toString())) {
								//
								addMap.put("SUB_HISTORY_PM_VALUE",
										mapIn.get("pmValue"));
								addMap.put("HISTORY_PM_TIME", new Date());
							}

							// 结束本次循环
							break;
						}
					}
					// 判断备用端口是虚拟口输入
				} else if (ma.get("SUB_ROUTE_TYPE") != null
						&& (CommonDefine.PM.SECTON_ROUTE_TYPE.VIR_PORT + "")
								.equals(ma.get("SUB_ROUTE_TYPE").toString())) {
					// 虚拟端口
					if (ma.get("SUB_PTP_ID") != null) {
						String ids[] = ma.get("SUB_PTP_ID").toString()
								.split(",");
						for (String id : ids) {
							// 更新主用虚拟端口
							double value = 0.0;
							for (Map mapIn : returnListIn) {
								if (mapIn.get("BASE_PTP_ID") != null
										&& mapIn.get("BASE_PTP_ID").toString()
												.equals(id)) {
									if (ma.get("SUB_PM_TYPE") != null
											&& (CommonDefine.PM.PORT_TYPE.PORT_IN + "")
													.equals(ma.get(
															"SUB_PM_TYPE")
															.toString())) {

										// 输出光功率当前值
										value += Math
												.pow(10, Double.valueOf(mapIn
														.get("pmValue")
														.toString()) / 10);
									}

									// 结束本次循环
									break;
								}
							}

							if (value != 0) {
								value = 10 * Math.log10(value);
							}
							addMap.put("SUB_HISTORY_PM_VALUE", getDouble(value));
							addMap.put("HISTORY_PM_TIME", new Date());
						}
					}
				}

				// 备用端口是单端口输出
				if (ma.get("SUB_ROUTE_TYPE") != null
						&& (CommonDefine.PM.SECTON_ROUTE_TYPE.PTP + "")
								.equals(ma.get("SUB_ROUTE_TYPE").toString())) {
					for (Map mapOut : returnListOut) {
						if (mapOut.get("BASE_PTP_ID") != null
								&& ma.get("SUB_PTP_ID") != null
								&& mapOut
										.get("BASE_PTP_ID")
										.toString()
										.equals(ma.get("SUB_PTP_ID").toString())) {
							if (ma.get("SUB_PM_TYPE") != null
									&& (CommonDefine.PM.PORT_TYPE.PORT_OUT + "")
											.equals(ma.get("SUB_PM_TYPE")
													.toString())) {
								//
								addMap.put("SUB_HISTORY_PM_VALUE",
										mapOut.get("pmValue"));
								addMap.put("HISTORY_PM_TIME", new Date());
							}

							// 结束本次循环
							break;
						}
					}
					// 备用端口是虚拟口输出
				} else if (ma.get("SUB_ROUTE_TYPE") != null
						&& (CommonDefine.PM.SECTON_ROUTE_TYPE.VIR_PORT + "")
								.equals(ma.get("SUB_ROUTE_TYPE").toString())) {
					// 虚拟端口
					if (ma.get("SUB_PTP_ID") != null) {
						String ids[] = ma.get("SUB_PTP_ID").toString()
								.split(",");
						for (String id : ids) {
							// 更新主用虚拟端口
							double value = 0.0;
							for (Map mapOut : returnListOut) {
								if (mapOut.get("BASE_PTP_ID") != null
										&& mapOut.get("BASE_PTP_ID").toString()
												.equals(id)) {
									if (ma.get("SUB_PM_TYPE") != null
											&& (CommonDefine.PM.PORT_TYPE.PORT_OUT + "")
													.equals(ma.get(
															"SUB_PM_TYPE")
															.toString())) {

										// 输出光功率当前值
										value += Math
												.pow(10, Double.valueOf(mapOut
														.get("pmValue")
														.toString()) / 10);
									}

									// 结束本次循环
									break;
								}
							}

							if (value != 0) {
								value = 10 * Math.log10(value);
							}
							addMap.put("SUB_HISTORY_PM_VALUE", getDouble(value));
							addMap.put("HISTORY_PM_TIME", new Date());
						}
					}
				}

				updateList.add(addMap);
			}
		} catch (java.text.ParseException e1) {
			throw new CommonException(e1,
					MessageCodeDefine.PM_TIME_FORMART_ERROR);
		} catch (Exception e) {
			throw new CommonException(new Exception(),
					MessageCodeDefine.PM_TIME_FORMART_ERROR);
		}
		return updateList;
	}

	private List<String> getPmTableName(Date date1, Date date2,
			String emsConnectionId) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM");
		List<String> tableName = new ArrayList();
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		if (date1.compareTo(date2) < 0) {
			cal1.setTime(date1);
			cal2.setTime(date2);
		} else {
			cal1.setTime(date2);
			cal2.setTime(date1);
		}
		cal2.add(Calendar.MONTH, 1);
		while (cal1.get(Calendar.MONTH) != cal2.get(Calendar.MONTH)) {
			String table = CommonDefine.PM.PM_TABLE_NAMES.ORIGINAL_DATA + "_"
					+ emsConnectionId + "_" + sdf.format(cal1.getTime());
			tableName.add(table);
			cal1.add(Calendar.MONTH, 1);
		}

		return tableName;

	}

	@Override
	public Map<String, Object> getPmFromTaskId(int taskId)
			throws CommonException {
		return getPmFromTaskId(taskId, Calendar.DAY_OF_YEAR);
	}

	@Override
	public Map<String, Object> getPmFromTaskId(int taskId, int steps)
			throws CommonException {
		// 任务周期
		Map searchCond = new HashMap();
		searchCond.put("taskId", taskId);
		Map<String, Object> taskInfo = (Map<String, Object>) performanceManagerMapper
				.searchNETaskInfoForEdit(searchCond).get(0);
		JSONObject o = JSONObject.fromObject(taskInfo);
		// 初始时间
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date startTime, endTime;
		if (0 == ((Integer) taskInfo.get("period"))) {
			// 日报
			Integer delay = Integer.valueOf(taskInfo.get("delay").toString());
			calendar.add(Calendar.DAY_OF_YEAR, -delay);
			startTime = calendar.getTime();
			endTime = calendar.getTime();
		} else {
			// 月报
			endTime = calendar.getTime();
			calendar.add(Calendar.MONTH, -1);
			startTime = calendar.getTime();
		}
		return getPmFromTaskId(taskId, steps, startTime, endTime);
	}

	@Override
	public List<Map> getMultiSecDetailInfoForReportSummary(
			List<Integer> idList, Date startTime, Date endTime) {
		List<Map> returnList = new ArrayList<Map>();
		// 设置日历对象
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startTime);
		// 日期格式化
		SimpleDateFormat startFormat = new SimpleDateFormat(
				CommonDefine.COMMON_START_FORMAT);
		SimpleDateFormat endFormat = new SimpleDateFormat(
				CommonDefine.COMMON_END_FORMAT);
		String yearAndMonth = new SimpleDateFormat("yyyy_MM").format(calendar
				.getTime());
		// 先查出复用段列表
		String idListString = idList.toString();
		String idString = idListString.substring(1, idListString.length() - 1);
		// System.out.println("idString = " + idString);
		List<Map> multiSecDataList = pmMultipleSectionManagerMapper
				.selectMultipleInfoForReport(idString);
		Map toAdd;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		while (calendar.getTimeInMillis() <= endTime.getTime()) {
			Date now = calendar.getTime();
			String start = startFormat.format(now);
			String end = endFormat.format(now);
			for (Map aMs : multiSecDataList) {
				toAdd = new HashMap();
				int emsId = (Integer) aMs.get("BASE_EMS_CONNECTION_ID");
				String tableName = CommonDefine.PM.PM_TABLE_NAMES.ORIGINAL_DATA
						+ "_" + emsId + "_" + yearAndMonth;
				// 表存在？
				Integer existance = performanceManagerMapper
						.getPmTableExistance(tableName,
								SpringContextUtil.getDataBaseParam(CommonDefine.DB_SID));
				Integer exceptionLevel = null;
				if (existance != null && existance == 1) {
					exceptionLevel = pmMultipleSectionManagerMapper
							.selectMultiplePmInfoForReport(tableName,
									(Integer) aMs.get("PM_MULTI_SEC_ID"),
									start, end);

				}
				toAdd.put("PM_MULTI_SEC_ID", aMs.get("PM_MULTI_SEC_ID"));
				toAdd.put("EMS_DISPLAY_NAME", aMs.get("EMS_DISPLAY_NAME"));
				toAdd.put("EMS_TYPE", aMs.get("EMS_TYPE"));
				toAdd.put("EMS_GROUP_DISPLAY_NAME",
						aMs.get("EMS_GROUP_DISPLAY_NAME"));
				toAdd.put("TRUNK_LINE_DISPLAY_NAME",
						aMs.get("TRUNK_LINE_DISPLAY_NAME"));
				toAdd.put("DIRECTION", aMs.get("DIRECTION"));
				toAdd.put("STD_WAVE", aMs.get("STD_WAVE"));
				toAdd.put("ACTULLY_WAVE", aMs.get("ACTULLY_WAVE"));
				toAdd.put("SEC_NAME", aMs.get("SEC_NAME"));
				toAdd.put("retrievalTime", sdf.format(now));
				toAdd.put("MSStatus", exceptionLevel);
				returnList.add(toAdd);
			}
			// 加一步径
			calendar.add(Calendar.DAY_OF_YEAR, 1);
		}
		return returnList;
	}

	@Override
	public Map<String, Object> getPmFromTaskId(int taskId, int steps,
			Date startTime, Date endTime) throws CommonException {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		// 先获取任务ID和类型
		List<Map> targetList = performanceManagerMapper
				.getTaskTargetIds(taskId);
		Integer taskType = (Integer) targetList.get(0).get("TARGET_TYPE");
		List<Map> multiSecsDataList = new ArrayList<Map>(); // 正向
		List<Map> multiSecsDataListReverse = new ArrayList<Map>(); // 反向
		int direction = 0; // 复用段方向
		if (taskType == CommonDefine.TASK_TARGET_TYPE.TRUNK_LINE) {
			// 存储值

			// 目标是干线
			for (Map target : targetList) {
				List<Integer> multiSecIdList = performanceManagerMapper
						.getMultiSecIds(target.get("TARGET_Id").toString());
				for (int id : multiSecIdList) {
					// 复用段单向还是双向？
					direction = Integer.parseInt(pmMultipleSectionManagerMapper
							.getMultiSecDirection(id));
					if (direction == 2) {
						// 双向
						multiSecsDataListReverse
								.addAll(getMultiSecInfoByMultiSecId(id,
										startTime, endTime, 2, steps,
										Integer.parseInt(target
												.get("TARGET_Id").toString())));
					}
					multiSecsDataList.addAll(getMultiSecInfoByMultiSecId(id,
							startTime, endTime, 1, steps, Integer
									.parseInt(target.get("TARGET_Id")
											.toString())));
				}
			}
			// String trunkLineIdString = getTargetIdString(targetList);

		} else if (taskType == CommonDefine.TASK_TARGET_TYPE.MULTI_SEC) {
			// 目标是复用段
			for (Map m : targetList) {
				int id = (Integer) m.get("TARGET_Id");
				// 复用段单向还是双向？
				direction = Integer.parseInt(pmMultipleSectionManagerMapper
						.getMultiSecDirection(id));
				if (direction == 2) {
					// 双向
					multiSecsDataListReverse
							.addAll(getMultiSecInfoByMultiSecId(id, startTime,
									endTime, 2, steps, null));
				}
				multiSecsDataList.addAll(getMultiSecInfoByMultiSecId(id,
						startTime, endTime, 1, steps, null));
			}
		}
		returnMap.put("forward", multiSecsDataList);
		returnMap.put("reverse", multiSecsDataListReverse);
		return returnMap;
	}

	/**
	 * @param id
	 *            复用段ID
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @param direction
	 *            方向
	 * @param stepPath
	 *            步径，Calendar.DAY_OF_YEAR或Calendar.MONTH
	 * @param trunkLineId
	 *            若是干线，则有干线ID
	 * @return 组成复用段的端口列表，包含虚拟端口。 其中性能key为PM_VALUE和SUB_PM_VALUE，二者为map对象，key为时间
	 * @throws CommonException
	 */
	private List<Map> getMultiSecInfoByMultiSecId(int id, Date startTime,
			Date endTime, int direction, int stepPath, Integer trunkLineId)
			throws CommonException {
		// 设置日历对象
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startTime);
		// 先查出端口列表
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("MULTI_SEC_ID", id);
		conditionMap.put("DIRECTION", direction);
		List<Map> multiSecDataList = pmMultipleSectionManagerMapper
				.selectMultiplePtpRouteForReport(conditionMap);
		if (multiSecDataList.size() > 0) {
			// 添加干线信息
			if (trunkLineId != null) {
				for (Map mMS : multiSecDataList) {
					mMS.put("trunkLineId", trunkLineId);
				}
			}
			// 查询性能
			// 按天循环
			while (calendar.getTimeInMillis() <= endTime.getTime()) {
				SimpleDateFormat format = new SimpleDateFormat(
						CommonDefine.COMMON_SIMPLE_FORMAT);
				String currentTime = format.format(calendar.getTime());
				conditionMap.put("startTime", currentTime);
				conditionMap.put("BASE_EMS_CONNECTION_ID", multiSecDataList
						.get(0).get("BASE_EMS_CONNECTION_ID"));
				List<Map> mulitSecPmList = getMulitSecPm(conditionMap);
				// 梳理性能列表
				Map<Integer, Map<String, String>> multiSecPmMap = new HashMap<Integer, Map<String, String>>();
				Map<String, String> aMap = null;
				for (Map mPm : mulitSecPmList) {
					aMap = new HashMap<String, String>();
					if (mPm.get("HISTORY_PM_VALUE") != null) {
						aMap.put("HISTORY_PM_VALUE", mPm
								.get("HISTORY_PM_VALUE").toString());
					}
					if (mPm.get("SUB_HISTORY_PM_VALUE") != null) {
						aMap.put("SUB_HISTORY_PM_VALUE",
								mPm.get("SUB_HISTORY_PM_VALUE").toString());
					}
					multiSecPmMap.put((Integer) mPm.get("PM_MULTI_SEC_PTP_ID"),
							aMap);
				}
				// 新增段衰耗需求
				List<Map> multiSecDataListForDuanValue = pmMultipleSectionManagerMapper
						.selectSimpleMultiplePtpRouteForReport(conditionMap);
				aMap = null;
				for (Map ms : multiSecDataListForDuanValue) {
					aMap = multiSecPmMap.get((Integer) ms
							.get("PM_MULTI_SEC_PTP_ID"));
					ms.put("HISTORY_PM_VALUE", aMap.get("HISTORY_PM_VALUE"));
					ms.put("SUB_HISTORY_PM_VALUE",
							aMap.get("SUB_HISTORY_PM_VALUE"));
				}
				updateDuanValue(multiSecDataListForDuanValue,
						"HISTORY_PM_VALUE", 4);
				// 将性能按时间塞进端口列表
				// for (Map mMS : multiSecDataList) {
				// Integer multiSecPtpId = (Integer) mMS
				// .get("PM_MULTI_SEC_PTP_ID");
				// if (multiSecPmMap.containsKey(multiSecPtpId)) {
				// if (mMS.get("PM_VALUE") == null) {
				// mMS.put("PM_VALUE", new HashMap<String, String>());
				// }
				// if (mMS.get("SUB_PM_VALUE") == null) {
				// mMS.put("SUB_PM_VALUE",
				// new HashMap<String, String>());
				// }
				// ((Map) mMS.get("PM_VALUE")).put(
				// currentTime,
				// multiSecPmMap.get(multiSecPtpId).get(
				// "HISTORY_PM_VALUE"));
				// ((Map) mMS.get("SUB_PM_VALUE")).put(
				// currentTime,
				// multiSecPmMap.get(multiSecPtpId).get(
				// "SUB_HISTORY_PM_VALUE"));
				// }
				// }
				for (int i = 0; i < multiSecDataList.size(); i++) {
					Map mMS = multiSecDataList.get(i);
					Map mPM = multiSecDataListForDuanValue.get(i);
					if (((Integer) mMS.get("PM_MULTI_SEC_PTP_ID"))
							.equals((Integer) mPM.get("PM_MULTI_SEC_PTP_ID"))) {
						if (mMS.get("PM_VALUE") == null) {
							mMS.put("PM_VALUE", new HashMap<String, String>());
						}
						if (mMS.get("SUB_PM_VALUE") == null) {
							mMS.put("SUB_PM_VALUE",
									new HashMap<String, String>());
						}
						((Map) mMS.get("PM_VALUE")).put(currentTime,
								mPM.get("HISTORY_PM_VALUE"));
						((Map) mMS.get("SUB_PM_VALUE")).put(currentTime,
								mPM.get("SUB_HISTORY_PM_VALUE"));
					}

				}
				// 加一步径
				calendar.add(stepPath, 1);
			}

			return multiSecDataList;
		}
		return new ArrayList<Map>();
	}

	// 将目标ID转化为in条件字符串
	// private String getTargetIdString(List<Map> targetList) {
	// StringBuffer sb = new StringBuffer();
	// for (Map target : targetList) {
	// sb.append(target.get("TARGET_Id"));
	// sb.append(",");
	// }
	// String targetIds = sb.toString();
	// return targetIds.substring(0, targetIds.length() - 1);
	// }

	@Override
	public Map<String, List<Map<String, Object>>> getExportPmInfo(
			Map<String, Object> multiSecPm) {
		List<Map> multiSecsDataList = (List<Map>) multiSecPm.get("forward"); // 正向
		List<Map> multiSecsDataListReverse = (List<Map>) multiSecPm
				.get("reverse"); // 反向

		// 返回值
		Map<String, List<Map<String, Object>>> returnMap = new HashMap<String, List<Map<String, Object>>>();
		List<Map<String, Object>> multiSecsDataListMain = new ArrayList<Map<String, Object>>(); // 正向主用
		List<Map<String, Object>> multiSecsDataListBackup = new ArrayList<Map<String, Object>>(); // 正向备用
		List<Map<String, Object>> multiSecsDataListReverseMain = new ArrayList<Map<String, Object>>(); // 反向主用
		List<Map<String, Object>> multiSecsDataListReverseBackup = new ArrayList<Map<String, Object>>(); // 反向备用

		if (multiSecsDataList.size() > 0) {
			Map currentMap;
			int lastNeId = 0, lastUnitId = 0, lastPmType = 0, neRowCount = 0;
			int lastSubUnitId = 0, lastSubPmType = 0, subNeRowCount = 0;
			for (int i = 0; i < multiSecsDataList.size(); i++) {
				currentMap = multiSecsDataList.get(i);
				if (lastNeId != (Integer) currentMap.get("NE_ID")) {
					// 这是一个新网元
					// 为上一个网元添加平衡行
					Map<String, Object> aRow;
					if (neRowCount > subNeRowCount) {
						// 主用多于备用
						int blankRows = neRowCount - subNeRowCount;
						for (int j = 0; j < blankRows; j++) {
							aRow = new HashMap<String, Object>();
							aRow.put("NE_ID",
									multiSecsDataList.get(i - 1).get("NE_ID")
											.toString());
							aRow.put(
									"NE_DISPLAY_NAME",
									multiSecsDataList.get(i - 1)
											.get("NE_DISPLAY_NAME").toString());
							multiSecsDataListBackup.add(aRow);
						}
					}
					if (neRowCount < subNeRowCount) {
						// 主用少于备用
						int blankRows = neRowCount - subNeRowCount;
						for (int j = 0; j < blankRows; j++) {
							aRow = new HashMap<String, Object>();
							aRow.put("NE_ID",
									multiSecsDataList.get(i - 1).get("NE_ID")
											.toString());
							aRow.put(
									"NE_DISPLAY_NAME",
									multiSecsDataList.get(i - 1)
											.get("NE_DISPLAY_NAME").toString());
							multiSecsDataListMain.add(aRow);
						}

					}
					// 新网元初始化网元临时变量
					lastNeId = (Integer) currentMap.get("NE_ID");
					neRowCount = 0;
					subNeRowCount = 0;
					// 分析路由行
					int[] returnArray = analysisARow(currentMap, lastUnitId,
							lastPmType, neRowCount, lastSubUnitId,
							lastSubPmType, subNeRowCount,
							multiSecsDataListMain, multiSecsDataListBackup, 1);
					lastUnitId = returnArray[0];
					lastPmType = returnArray[1];
					neRowCount = returnArray[2];
					lastSubUnitId = returnArray[3];
					lastSubPmType = returnArray[4];
					subNeRowCount = returnArray[5];
				} else {
					// 这是一个旧网元
					// 分析路由行
					int[] returnArray = analysisARow(currentMap, lastUnitId,
							lastPmType, neRowCount, lastSubUnitId,
							lastSubPmType, subNeRowCount,
							multiSecsDataListMain, multiSecsDataListBackup, 1);
					lastUnitId = returnArray[0];
					lastPmType = returnArray[1];
					neRowCount = returnArray[2];
					lastSubUnitId = returnArray[3];
					lastSubPmType = returnArray[4];
					subNeRowCount = returnArray[5];
				}
			}
			// 为最后一个网元添加平衡行
			if (neRowCount > subNeRowCount) {
				// 主用多于备用
				int blankRows = neRowCount - subNeRowCount;
				for (int j = 0; j < blankRows; j++) {
					Map<String, Object> aRow = new HashMap<String, Object>();
					aRow.put("NE_ID",
							multiSecsDataList.get(multiSecsDataList.size() - 1)
									.get("NE_ID").toString());
					aRow.put("NE_DISPLAY_NAME",
							multiSecsDataList.get(multiSecsDataList.size() - 1)
									.get("NE_DISPLAY_NAME").toString());
					multiSecsDataListBackup.add(aRow);
				}
			}
			if (neRowCount < subNeRowCount) {
				// 主用少于备用
				int blankRows = subNeRowCount - neRowCount;
				for (int j = 0; j < blankRows; j++) {
					Map<String, Object> aRow = new HashMap<String, Object>();
					aRow.put("NE_ID",
							multiSecsDataList.get(multiSecsDataList.size() - 1)
									.get("NE_ID").toString());
					aRow.put("NE_DISPLAY_NAME",
							multiSecsDataList.get(multiSecsDataList.size() - 1)
									.get("NE_DISPLAY_NAME").toString());
					multiSecsDataListMain.add(aRow);
				}

			}
			if (multiSecsDataListReverse.size() > 0) {
				// 还需要反向
				lastNeId = 0;
				lastUnitId = 0;
				lastPmType = 0;
				neRowCount = 0;
				lastSubUnitId = 0;
				lastSubPmType = 0;
				subNeRowCount = 0;
				for (int i = 0; i < multiSecsDataListReverse.size(); i++) {
					currentMap = multiSecsDataListReverse.get(i);
					if (lastNeId != (Integer) currentMap.get("NE_ID")) {
						// 这是一个新网元
						// 为上一个网元添加平衡行
						Map aRow;
						if (neRowCount > subNeRowCount) {
							// 主用多于备用
							int blankRows = neRowCount - subNeRowCount;
							for (int j = 0; j < blankRows; j++) {
								aRow = new HashMap<String, Object>();
								aRow.put("NE_ID",
										multiSecsDataListReverse.get(i - 1)
												.get("NE_ID").toString());
								aRow.put("NE_DISPLAY_NAME",
										multiSecsDataListReverse.get(i - 1)
												.get("NE_DISPLAY_NAME")
												.toString());
								multiSecsDataListReverseBackup.add(aRow);
							}
						}
						if (neRowCount < subNeRowCount) {
							// 主用少于备用
							int blankRows = subNeRowCount - neRowCount;
							for (int j = 0; j < blankRows; j++) {
								aRow = new HashMap<String, Object>();
								aRow.put("NE_ID",
										multiSecsDataListReverse.get(i - 1)
												.get("NE_ID").toString());
								aRow.put("NE_DISPLAY_NAME",
										multiSecsDataListReverse.get(i - 1)
												.get("NE_DISPLAY_NAME")
												.toString());
								multiSecsDataListReverseMain.add(aRow);
							}

						}
						// 新网元初始化网元临时变量
						lastNeId = (Integer) currentMap.get("NE_ID");
						neRowCount = 0;
						subNeRowCount = 0;
						// 分析路由行
						int[] returnArray = analysisARow(currentMap,
								lastUnitId, lastPmType, neRowCount,
								lastSubUnitId, lastSubPmType, subNeRowCount,
								multiSecsDataListReverseMain,
								multiSecsDataListReverseBackup, 2);
						lastUnitId = returnArray[0];
						lastPmType = returnArray[1];
						neRowCount = returnArray[2];
						lastSubUnitId = returnArray[3];
						lastSubPmType = returnArray[4];
						subNeRowCount = returnArray[5];
					} else {
						// 这是一个旧网元
						// 分析路由行
						int[] returnArray = analysisARow(currentMap,
								lastUnitId, lastPmType, neRowCount,
								lastSubUnitId, lastSubPmType, subNeRowCount,
								multiSecsDataListReverseMain,
								multiSecsDataListReverseBackup, 2);
						lastUnitId = returnArray[0];
						lastPmType = returnArray[1];
						neRowCount = returnArray[2];
						lastSubUnitId = returnArray[3];
						lastSubPmType = returnArray[4];
						subNeRowCount = returnArray[5];
					}
				}
				// 为上一个网元添加平衡行
				Map aRow;
				if (neRowCount > subNeRowCount) {
					// 主用多于备用
					int blankRows = neRowCount - subNeRowCount;
					for (int j = 0; j < blankRows; j++) {
						aRow = new HashMap<String, Object>();
						aRow.put(
								"NE_ID",
								multiSecsDataListReverse
										.get(multiSecsDataListReverse.size() - 1)
										.get("NE_ID").toString());
						aRow.put(
								"NE_DISPLAY_NAME",
								multiSecsDataListReverse
										.get(multiSecsDataListReverse.size() - 1)
										.get("NE_DISPLAY_NAME").toString());
						multiSecsDataListReverseBackup.add(aRow);
					}
				}
				if (neRowCount < subNeRowCount) {
					// 主用少于备用
					int blankRows = subNeRowCount - neRowCount;
					for (int j = 0; j < blankRows; j++) {
						aRow = new HashMap<String, Object>();
						aRow.put(
								"NE_ID",
								multiSecsDataListReverse
										.get(multiSecsDataListReverse.size() - 1)
										.get("NE_ID").toString());
						aRow.put(
								"NE_DISPLAY_NAME",
								multiSecsDataListReverse
										.get(multiSecsDataListReverse.size() - 1)
										.get("NE_DISPLAY_NAME").toString());
						multiSecsDataListReverseMain.add(aRow);
					}

				}
			}
		}

		returnMap.put("multiSecsDataListMain", multiSecsDataListMain);
		returnMap.put("multiSecsDataListBackup", multiSecsDataListBackup);
		returnMap.put("multiSecsDataListReverseMain",
				multiSecsDataListReverseMain);
		returnMap.put("multiSecsDataListReverseBackup",
				multiSecsDataListReverseBackup);

		return returnMap;
	}

	// 分析一行路由行
	private int[] analysisARow(Map currentMap, int lastUnitId, int lastPmType,
			int neRowCount, int lastSubUnitId, int lastSubPmType,
			int subNeRowCount, List<Map<String, Object>> multiSecsDataListMain,
			List<Map<String, Object>> multiSecsDataListBackup, int direction) {
		String arrow;
		if (direction == 1) {
			arrow = "↓";
		} else {
			arrow = "↑";
		}
		Map<String, Object> aRow, aSubRow;
		int[] returnArray = new int[6];
		// 主路由
		if ((Integer) currentMap.get("ROUTE_TYPE") == 1) {
			// This is a 真实端口
			if (lastUnitId != (Integer) currentMap.get("UNIT_ID")) {
				// Here! 新板卡，初始化板卡变量
				if (currentMap.get("PM_TYPE") != null
						&& !"".equals(currentMap.get("PM_TYPE").toString())) {
					lastPmType = Integer.parseInt(currentMap.get("PM_TYPE")
							.toString());
				}
				if (currentMap.get("UNIT_ID") != null
						&& !"".equals(currentMap.get("UNIT_ID").toString())) {
					lastUnitId = (Integer) currentMap.get("UNIT_ID"); // 现在最后一个板卡变成这个啦
				}
				if (direction == 1) {
					// 正向
					if (lastPmType == 1) {
						// 正常的先来个输入嘛 o(*￣▽￣*)ブ
						multiSecsDataListMain.add(constructAPmRowMain(
								currentMap, arrow));
						neRowCount++;
						// 再来一行板卡嘛(>▽<)
						multiSecsDataListMain
								.add(constructAUnitRowMain(currentMap));
						neRowCount++;
					} else if (lastPmType == 2) {
						// 居然先来了个输出Σ( ° △ °|||)︴ 那先给它个板卡吧
						multiSecsDataListMain
								.add(constructAUnitRowMain(currentMap));
						neRowCount++;
						// 再加上输出
						multiSecsDataListMain.add(constructAPmRowMain(
								currentMap, arrow));
						neRowCount++;
					}
				} else if (direction == 2) {
					// 反向
					if (lastPmType == 2) {
						// 正常的先来个输出嘛 o(*￣▽￣*)ブ
						multiSecsDataListMain.add(constructAPmRowMain(
								currentMap, arrow));
						neRowCount++;
						// 再来一行板卡嘛(>▽<)
						multiSecsDataListMain
								.add(constructAUnitRowMain(currentMap));
						neRowCount++;
					} else if (lastPmType == 1) {
						// 居然先来了个输入Σ( ° △ °|||)︴ 那先给它个板卡吧
						multiSecsDataListMain
								.add(constructAUnitRowMain(currentMap));
						neRowCount++;
						// 再加上输出
						multiSecsDataListMain.add(constructAPmRowMain(
								currentMap, arrow));
						neRowCount++;
					}
				}
				if (lastPmType != 1 && lastPmType != 2) {
					// 不是输入也不是输出！这都是什么奇怪的数据啊！ノ￣ー￣)ノ
					aRow = new HashMap<String, Object>();
					aRow.put("NE_ID", currentMap.get("NE_ID"));
					aRow.put("NE_DISPLAY_NAME",
							currentMap.get("NE_DISPLAY_NAME"));
					if (currentMap.get("NOTE") != null
							&& !"".equals(currentMap.get("NOTE"))) {
						aRow.put("UNIT_INFO", currentMap.get("PTP_NAME")
								.toString()
								+ "("
								+ currentMap.get("NOTE")
								+ ")");
					} else {
						aRow.put("UNIT_INFO", currentMap.get("PTP_NAME"));
					}
					aRow.put("PM_VALUE", currentMap.get("PM_VALUE"));
					// 光放理论值是啥( ´•︵•` )？
					aRow.put("THEORETICAL_VALUE",
							currentMap.get("CALCULATE_POINT"));
					aRow.put("MULTI_SEC_ID", currentMap.get("MULTI_SEC_ID"));
					aRow.put("trunkLineId", currentMap.get("trunkLineId"));
					multiSecsDataListMain.add(aRow);
					neRowCount++;
				}

			} else {
				// 旧板卡
				int thisPmType = 0;
				if (!"".equals(currentMap.get("PM_TYPE"))) {
					thisPmType = Integer.parseInt(currentMap.get("PM_TYPE")
							.toString());
				}
				if (direction == 1) {
					// 正向
					if (lastPmType == 1 && thisPmType == 2) {
						// 上一个是输入这个是输出！完美！o((>ω< ))o 先来个输出性能！
						aRow = constructAPmRowMain(currentMap, arrow);
						multiSecsDataListMain.add(aRow);
						neRowCount++;
						// 再给板卡算下性能差！
						calculateUnitPm(
								multiSecsDataListMain.get(multiSecsDataListMain
										.size() - 3),
								multiSecsDataListMain.get(multiSecsDataListMain
										.size() - 2), aRow, direction);
					} else if (thisPmType == 1) {
						// 上一个是输入这个还是输入！或者上个是输出这个是输入！或者上一个是不可理喻！总之不正常！
						multiSecsDataListMain.add(constructAPmRowMain(
								currentMap, arrow));
						neRowCount++;
						// 再来一行板卡好了
						multiSecsDataListMain
								.add(constructAUnitRowMain(currentMap));
						neRowCount++;
					} else if (thisPmType == 2) {
						// 上个是输出这个还是输出！或者上一个是不可理喻！还是不正常！
						// 先来一行板卡！
						multiSecsDataListMain
								.add(constructAUnitRowMain(currentMap));
						neRowCount++;
						// 再给他个输出！
						multiSecsDataListMain.add(constructAPmRowMain(
								currentMap, arrow));
						neRowCount++;
					} else {
						// 神知道这是啥啊！ノ￣ー￣)ノ
						aRow = new HashMap<String, Object>();
						aRow.put("NE_ID", currentMap.get("NE_ID"));
						aRow.put("NE_DISPLAY_NAME",
								currentMap.get("NE_DISPLAY_NAME"));
						if (currentMap.get("NOTE") != null
								&& !"".equals(currentMap.get("NOTE"))) {
							aRow.put("UNIT_INFO", currentMap.get("PTP_NAME")
									.toString()
									+ "("
									+ currentMap.get("NOTE")
									+ ")");
						} else {
							aRow.put("UNIT_INFO", currentMap.get("PTP_NAME"));
						}
						aRow.put("PM_VALUE", currentMap.get("PM_VALUE"));
						// 光放理论值是啥( ´•︵•` )？
						aRow.put("THEORETICAL_VALUE",
								currentMap.get("CALCULATE_POINT"));
						aRow.put("MULTI_SEC_ID", currentMap.get("MULTI_SEC_ID"));
						aRow.put("trunkLineId", currentMap.get("trunkLineId"));
						multiSecsDataListMain.add(aRow);
						neRowCount++;
					}
				} else if (direction == 2) {
					// 反向
					if (lastPmType == 2 && thisPmType == 1) {
						// 上一个是输出这个是输入！完美！o((>ω< ))o 先来个输入性能！
						aRow = constructAPmRowMain(currentMap, arrow);
						multiSecsDataListMain.add(aRow);
						neRowCount++;
						// 再给板卡算下性能差！
						calculateUnitPm(
								multiSecsDataListMain.get(multiSecsDataListMain
										.size() - 3),
								multiSecsDataListMain.get(multiSecsDataListMain
										.size() - 2), aRow, direction);
					} else if (thisPmType == 2) {
						// 上一个是输出这个还是输出！或者上个是输入这个是输出！或者上一个是不可理喻！总之不正常！
						multiSecsDataListMain.add(constructAPmRowMain(
								currentMap, arrow));
						neRowCount++;
						// 再来一行板卡好了
						multiSecsDataListMain
								.add(constructAUnitRowMain(currentMap));
						neRowCount++;
					} else if (thisPmType == 1) {
						// 上个是输入这个还是输入！或者上一个是不可理喻！还是不正常！
						// 先来一行板卡！
						multiSecsDataListMain
								.add(constructAUnitRowMain(currentMap));
						neRowCount++;
						// 再给他个输出！
						multiSecsDataListMain.add(constructAPmRowMain(
								currentMap, arrow));
						neRowCount++;
					} else {
						// 神知道这是啥啊！ノ￣ー￣)ノ
						aRow = new HashMap<String, Object>();
						aRow.put("NE_ID", currentMap.get("NE_ID"));
						aRow.put("NE_DISPLAY_NAME",
								currentMap.get("NE_DISPLAY_NAME"));
						if (currentMap.get("NOTE") != null
								&& !"".equals(currentMap.get("NOTE"))) {
							aRow.put("UNIT_INFO", currentMap.get("PTP_NAME")
									.toString()
									+ "("
									+ currentMap.get("NOTE")
									+ ")");
						} else {
							aRow.put("UNIT_INFO", currentMap.get("PTP_NAME"));
						}
						aRow.put("PM_VALUE", currentMap.get("PM_VALUE"));
						// 光放理论值是啥( ´•︵•` )？
						aRow.put("THEORETICAL_VALUE",
								currentMap.get("CALCULATE_POINT"));
						aRow.put("MULTI_SEC_ID", currentMap.get("MULTI_SEC_ID"));
						aRow.put("trunkLineId", currentMap.get("trunkLineId"));
						multiSecsDataListMain.add(aRow);
						neRowCount++;
					}
				}
				lastPmType = thisPmType;
			}

		} else {
			// 非端口，重置板卡和性能类型
			lastUnitId = 0;
			lastPmType = 0;
			if ((Integer) currentMap.get("ROUTE_TYPE") == 2) {
				// 虚拟端口
				int thisPmType = Integer.parseInt(currentMap.get("PM_TYPE")
						.toString());
				if (direction == 1) {
					// 正向
					if (thisPmType == 1) {
						// 输入
						// 先加入性能行
						multiSecsDataListMain.add(constructAPmRowMain(
								currentMap, arrow));
						neRowCount++;
						// 再来一行板卡好了
						multiSecsDataListMain
								.add(constructAVirtualUnitRowMain(currentMap));
						neRowCount++;
					} else if (thisPmType == 2) {
						// 输出
						// 先加入板卡
						multiSecsDataListMain
								.add(constructAVirtualUnitRowMain(currentMap));
						neRowCount++;
						// 再来一行性能行
						multiSecsDataListMain.add(constructAPmRowMain(
								currentMap, arrow));
						neRowCount++;
					}
				} else if (direction == 2) {
					// 反向
					if (thisPmType == 2) {
						// 输出
						// 先加入性能行
						multiSecsDataListMain.add(constructAPmRowMain(
								currentMap, arrow));
						neRowCount++;
						// 再来一行板卡好了
						multiSecsDataListMain
								.add(constructAVirtualUnitRowMain(currentMap));
						neRowCount++;
					} else if (thisPmType == 1) {
						// 输入
						// 先加入板卡
						multiSecsDataListMain
								.add(constructAVirtualUnitRowMain(currentMap));
						neRowCount++;
						// 再来一行性能行
						multiSecsDataListMain.add(constructAPmRowMain(
								currentMap, arrow));
						neRowCount++;
					}
				}

			} else if ((Integer) currentMap.get("ROUTE_TYPE") == 5) {
				// 光缆
				// 首先平衡下主备用——这里平衡下就行了，备用一定也是光缆
				if (neRowCount > subNeRowCount) {
					// 主用多于备用
					int blankRows = neRowCount - subNeRowCount;
					for (int j = 0; j < blankRows; j++) {
						aRow = new HashMap<String, Object>();
						aRow.put("NE_ID", currentMap.get("NE_ID").toString());
						aRow.put("NE_DISPLAY_NAME",
								currentMap.get("NE_DISPLAY_NAME").toString());
						multiSecsDataListBackup.add(aRow);
						subNeRowCount++;
					}
				}
				if (neRowCount < subNeRowCount) {
					// 主用少于备用
					int blankRows = neRowCount - subNeRowCount;
					for (int j = 0; j < blankRows; j++) {
						aRow = new HashMap<String, Object>();
						aRow.put("NE_ID", currentMap.get("NE_ID").toString());
						aRow.put("NE_DISPLAY_NAME",
								currentMap.get("NE_DISPLAY_NAME").toString());
						multiSecsDataListMain.add(aRow);
						neRowCount++;
					}

				}
				aRow = new HashMap<String, Object>();
				aRow.put("NE_DISPLAY_NAME", currentMap.get("EQUIP_NAME"));
				if (currentMap.get("NOTE") != null
						&& !"".equals(currentMap.get("NOTE"))) {
					aRow.put("UNIT_INFO", currentMap.get("PTP_NAME").toString()
							+ "(" + currentMap.get("NOTE") + ")");
				} else {
					aRow.put("UNIT_INFO", currentMap.get("PTP_NAME"));
				}
				if (currentMap.get("CALCULATE_POINT") != null)
					aRow.put("THEORETICAL_VALUE",
							currentMap.get("CALCULATE_POINT").toString());
				else
					aRow.put("THEORETICAL_VALUE", "");
				aRow.put("PM_VALUE", currentMap.get("PM_VALUE"));
				aRow.put("MULTI_SEC_ID", currentMap.get("MULTI_SEC_ID"));
				aRow.put("trunkLineId", currentMap.get("trunkLineId"));
				multiSecsDataListMain.add(aRow);
				neRowCount++;
			} else if ((Integer) currentMap.get("ROUTE_TYPE") == 3) {
				// 衰耗器
				aRow = new HashMap<String, Object>();
				aRow.put("NE_ID", currentMap.get("NE_ID"));
				aRow.put("NE_DISPLAY_NAME", currentMap.get("NE_DISPLAY_NAME"));
				if (currentMap.get("NOTE") != null
						&& !"".equals(currentMap.get("NOTE"))) {
					aRow.put("UNIT_INFO", currentMap.get("PTP_NAME").toString()
							+ "(" + currentMap.get("NOTE") + ")");
				} else {
					aRow.put("UNIT_INFO", currentMap.get("PTP_NAME"));
				}
				aRow.put("PM_VALUE", currentMap.get("PM_VALUE"));
				// 光放理论值是啥( ´•︵•` )？
				aRow.put("THEORETICAL_VALUE", currentMap.get("CALCULATE_POINT"));
				aRow.put("PM_VALUE", currentMap.get("PM_VALUE"));
				aRow.put("MULTI_SEC_ID", currentMap.get("MULTI_SEC_ID"));
				aRow.put("trunkLineId", currentMap.get("trunkLineId"));
				multiSecsDataListMain.add(aRow);
				neRowCount++;
			} else if ((Integer) currentMap.get("ROUTE_TYPE") == 6) {
				// 空白行
				aRow = new HashMap<String, Object>();
				aRow.put("NE_ID", currentMap.get("NE_ID"));
				aRow.put("NE_DISPLAY_NAME", currentMap.get("NE_DISPLAY_NAME"));
				aRow.put("MULTI_SEC_ID", currentMap.get("MULTI_SEC_ID"));
				aRow.put("trunkLineId", currentMap.get("trunkLineId"));
				multiSecsDataListMain.add(aRow);
				neRowCount++;
			} else if ((Integer) currentMap.get("ROUTE_TYPE") == 7) {
				// DCM.....
				aRow = new HashMap<String, Object>();
				aRow.put("NE_ID", currentMap.get("NE_ID"));
				aRow.put("NE_DISPLAY_NAME", currentMap.get("NE_DISPLAY_NAME"));
				aRow.put("MULTI_SEC_ID", currentMap.get("MULTI_SEC_ID"));
				aRow.put("trunkLineId", currentMap.get("trunkLineId"));
				if (currentMap.get("NOTE") != null
						&& !"".equals(currentMap.get("NOTE"))) {
					aRow.put("UNIT_INFO", currentMap.get("PTP_NAME").toString()
							+ "(" + currentMap.get("NOTE") + ")");
				} else {
					aRow.put("UNIT_INFO", currentMap.get("PTP_NAME"));
				}
				aRow.put("THEORETICAL_VALUE", currentMap.get("CALCULATE_POINT"));
				aRow.put("PM_VALUE", currentMap.get("PM_VALUE"));
				multiSecsDataListMain.add(aRow);
				neRowCount++;
			}
		}
		// 备用路由
		if (currentMap.get("SUB_ROUTE_TYPE") == null) {
			// 需求变更，备用路由可以为空。
		} else if ((Integer) currentMap.get("SUB_ROUTE_TYPE") == 1) {
			// This is a 真实端口
			if (lastSubUnitId != (Integer) currentMap.get("SUB_UNIT_ID")) {
				// Here! 新板卡，初始化板卡变量
				if (currentMap.get("SUB_PM_TYPE") != null
						&& !"".equals(currentMap.get("SUB_PM_TYPE").toString())) {
					lastSubPmType = Integer.parseInt(currentMap.get(
							"SUB_PM_TYPE").toString());
				}
				if (currentMap.get("SUB_UNIT_ID") != null
						&& !"".equals(currentMap.get("SUB_UNIT_ID").toString())) {
					lastSubUnitId = (Integer) currentMap.get("SUB_UNIT_ID"); // 现在最后一个板卡变成这个啦
				}
				if (direction == 1) {
					// 正向
					if (lastSubPmType == 1) {
						// 正常的先来个输入嘛 o(*￣▽￣*)ブ
						multiSecsDataListBackup.add(constructAPmRowSub(
								currentMap, arrow));
						subNeRowCount++;
						// 再来一行板卡嘛(>▽<)
						multiSecsDataListBackup
								.add(constructAUnitRowSub(currentMap));
						subNeRowCount++;
					} else if (lastSubPmType == 2) {
						// 居然先来了个输出Σ( ° △ °|||)︴ 那先给它个板卡吧
						multiSecsDataListBackup
								.add(constructAUnitRowSub(currentMap));
						subNeRowCount++;
						// 再加上输出
						multiSecsDataListBackup.add(constructAPmRowSub(
								currentMap, arrow));
						subNeRowCount++;
					}
				} else if (direction == 2) {
					// 反向
					if (lastSubPmType == 2) {
						// 正常的先来个输出嘛 o(*￣▽￣*)ブ
						multiSecsDataListBackup.add(constructAPmRowSub(
								currentMap, arrow));
						subNeRowCount++;
						// 再来一行板卡嘛(>▽<)
						multiSecsDataListBackup
								.add(constructAUnitRowSub(currentMap));
						subNeRowCount++;
					} else if (lastSubPmType == 1) {
						// 居然先来了个输入Σ( ° △ °|||)︴ 那先给它个板卡吧
						multiSecsDataListBackup
								.add(constructAUnitRowSub(currentMap));
						subNeRowCount++;
						// 再加上输出
						multiSecsDataListBackup.add(constructAPmRowSub(
								currentMap, arrow));
						subNeRowCount++;
					}
				}
				if (lastSubPmType != 1 && lastSubPmType != 2) {
					// 不是输入也不是输出！这都是什么奇怪的数据啊！ノ￣ー￣)ノ
					aSubRow = new HashMap<String, Object>();
					aSubRow.put("NE_ID", currentMap.get("NE_ID"));
					aSubRow.put("NE_DISPLAY_NAME",
							currentMap.get("NE_DISPLAY_NAME"));
					if (currentMap.get("SUB_NOTE") != null
							&& !"".equals(currentMap.get("SUB_NOTE"))) {
						aSubRow.put("UNIT_INFO", currentMap.get("SUB_PTP_NAME")
								.toString()
								+ "("
								+ currentMap.get("SUB_NOTE")
								+ ")");
					} else {
						aSubRow.put("UNIT_INFO", currentMap.get("SUB_PTP_NAME"));
					}
					aSubRow.put("PM_VALUE", currentMap.get("SUB_PM_VALUE"));
					// 光放理论值是啥( ´•︵•` )？
					aSubRow.put("THEORETICAL_VALUE",
							currentMap.get("SUB_CALCULATE_POINT"));
					aSubRow.put("MULTI_SEC_ID", currentMap.get("MULTI_SEC_ID"));
					aSubRow.put("trunkLineId", currentMap.get("trunkLineId"));
					multiSecsDataListBackup.add(aSubRow);
					subNeRowCount++;
				}
			} else {
				// 旧板卡
				int thisPmType = 0;
				if (!"".equals(currentMap.get("SUB_PM_TYPE"))) {
					thisPmType = Integer.parseInt(currentMap.get("SUB_PM_TYPE")
							.toString());
				}
				if (direction == 1) {
					// 正向
					if (lastSubPmType == 1 && thisPmType == 2) {
						// 上一个是输入这个是输出！完美！o((>ω< ))o 先来个输出性能！
						aSubRow = constructAPmRowSub(currentMap, arrow);
						multiSecsDataListBackup.add(aSubRow);
						subNeRowCount++;
						// 再给板卡算下性能差！
						calculateUnitPm(
								multiSecsDataListBackup
										.get(multiSecsDataListBackup.size() - 3),
								multiSecsDataListBackup
										.get(multiSecsDataListBackup.size() - 2),
								aSubRow, direction);
					} else if (thisPmType == 1) {
						// 上一个是输入这个还是输入！或者上个是输出这个是输入！或者上一个是不可理喻！总之不正常！
						multiSecsDataListBackup.add(constructAPmRowSub(
								currentMap, arrow));
						subNeRowCount++;
						// 再来一行板卡好了
						multiSecsDataListBackup
								.add(constructAUnitRowSub(currentMap));
						subNeRowCount++;
					} else if (thisPmType == 2) {
						// 上个是输出这个还是输出！或者上一个是不可理喻！还是不正常！
						// 先来一行板卡！
						multiSecsDataListBackup
								.add(constructAUnitRowSub(currentMap));
						subNeRowCount++;
						// 再给他个输出！
						multiSecsDataListBackup.add(constructAPmRowSub(
								currentMap, arrow));
						subNeRowCount++;
					} else {
						// 神知道这是啥啊！ノ￣ー￣)ノ
						aSubRow = new HashMap<String, Object>();
						aSubRow.put("NE_ID", currentMap.get("NE_ID"));
						aSubRow.put("NE_DISPLAY_NAME",
								currentMap.get("NE_DISPLAY_NAME"));
						if (currentMap.get("SUB_NOTE") != null
								&& !"".equals(currentMap.get("SUB_NOTE"))) {
							aSubRow.put("UNIT_INFO",
									currentMap.get("SUB_PTP_NAME").toString()
											+ "(" + currentMap.get("SUB_NOTE")
											+ ")");
						} else {
							aSubRow.put("UNIT_INFO",
									currentMap.get("SUB_PTP_NAME"));
						}
						aSubRow.put("PM_VALUE", currentMap.get("SUB_PM_VALUE"));
						// 光放理论值是啥( ´•︵•` )？
						aSubRow.put("THEORETICAL_VALUE",
								currentMap.get("SUB_CALCULATE_POINT"));
						aSubRow.put("MULTI_SEC_ID",
								currentMap.get("MULTI_SEC_ID"));
						aSubRow.put("trunkLineId",
								currentMap.get("trunkLineId"));
						multiSecsDataListBackup.add(aSubRow);
						subNeRowCount++;
					}
				} else if (direction == 2) {
					// 反向
					if (lastSubPmType == 2 && thisPmType == 1) {
						// 上一个是输出这个是输入！完美！o((>ω< ))o 先来个输入性能！
						aSubRow = constructAPmRowSub(currentMap, arrow);
						multiSecsDataListBackup.add(aSubRow);
						subNeRowCount++;
						// 再给板卡算下性能差！
						calculateUnitPm(
								multiSecsDataListBackup
										.get(multiSecsDataListBackup.size() - 3),
								multiSecsDataListBackup
										.get(multiSecsDataListBackup.size() - 2),
								aSubRow, direction);
					} else if (thisPmType == 2) {
						// 上一个是输出这个还是输出！或者上个是输入这个是输出！或者上一个是不可理喻！总之不正常！
						multiSecsDataListBackup.add(constructAPmRowSub(
								currentMap, arrow));
						subNeRowCount++;
						// 再来一行板卡好了
						multiSecsDataListBackup
								.add(constructAUnitRowSub(currentMap));
						subNeRowCount++;
					} else if (thisPmType == 1) {
						// 上个是输入这个还是输入！或者上一个是不可理喻！还是不正常！
						// 先来一行板卡！
						multiSecsDataListBackup
								.add(constructAUnitRowSub(currentMap));
						subNeRowCount++;
						// 再给他个输出！
						multiSecsDataListBackup.add(constructAPmRowSub(
								currentMap, arrow));
						subNeRowCount++;
					} else {
						// 神知道这是啥啊！ノ￣ー￣)ノ
						aSubRow = new HashMap<String, Object>();
						aSubRow.put("NE_ID", currentMap.get("NE_ID"));
						aSubRow.put("NE_DISPLAY_NAME",
								currentMap.get("NE_DISPLAY_NAME"));
						if (currentMap.get("SUB_NOTE") != null
								&& !"".equals(currentMap.get("SUB_NOTE"))) {
							aSubRow.put("UNIT_INFO",
									currentMap.get("SUB_PTP_NAME").toString()
											+ "(" + currentMap.get("SUB_NOTE")
											+ ")");
						} else {
							aSubRow.put("UNIT_INFO",
									currentMap.get("SUB_PTP_NAME"));
						}
						aSubRow.put("PM_VALUE", currentMap.get("SUB_PM_VALUE"));
						// 光放理论值是啥( ´•︵•` )？
						aSubRow.put("THEORETICAL_VALUE",
								currentMap.get("SUB_CALCULATE_POINT"));
						aSubRow.put("MULTI_SEC_ID",
								currentMap.get("MULTI_SEC_ID"));
						aSubRow.put("trunkLineId",
								currentMap.get("trunkLineId"));
						multiSecsDataListBackup.add(aSubRow);
						subNeRowCount++;
					}
				}

				lastSubPmType = thisPmType;
			}

		} else {
			// 非端口，重置板卡和性能类型
			lastSubUnitId = 0;
			lastSubPmType = 0;
			if ((Integer) currentMap.get("SUB_ROUTE_TYPE") == 2) {
				// 虚拟端口
				int thisPmType = Integer.parseInt(currentMap.get("SUB_PM_TYPE")
						.toString());
				if (direction == 1) {
					// 正向
					if (thisPmType == 1) {
						// 输入
						// 先加入性能行
						multiSecsDataListBackup.add(constructAPmRowSub(
								currentMap, arrow));
						subNeRowCount++;
						// 再来一行板卡好了
						multiSecsDataListBackup
								.add(constructAVirtualUnitRowSub(currentMap));
						subNeRowCount++;
					} else if (thisPmType == 2) {
						// 输出
						// 先加入板卡
						multiSecsDataListBackup
								.add(constructAVirtualUnitRowSub(currentMap));
						subNeRowCount++;
						// 再来一行性能行
						multiSecsDataListBackup.add(constructAPmRowSub(
								currentMap, arrow));
						subNeRowCount++;
					}
				} else if (direction == 2) {
					// 反向
					if (thisPmType == 2) {
						// 输出
						// 先加入性能行
						multiSecsDataListBackup.add(constructAPmRowSub(
								currentMap, arrow));
						subNeRowCount++;
						// 再来一行板卡好了
						multiSecsDataListBackup
								.add(constructAVirtualUnitRowSub(currentMap));
						subNeRowCount++;
					} else if (thisPmType == 1) {
						// 输入
						// 先加入板卡
						multiSecsDataListBackup
								.add(constructAVirtualUnitRowSub(currentMap));
						subNeRowCount++;
						// 再来一行性能行
						multiSecsDataListBackup.add(constructAPmRowSub(
								currentMap, arrow));
						subNeRowCount++;
					}
				}

			} else if ((Integer) currentMap.get("SUB_ROUTE_TYPE") == 5) {
				// 光缆
				// 首先平衡下左右行数-上面干了！

				aSubRow = new HashMap<String, Object>();
				aSubRow.put("NE_DISPLAY_NAME", currentMap.get("EQUIP_NAME"));
				if (currentMap.get("NOTE") != null
						&& !"".equals(currentMap.get("NOTE"))) {
					aSubRow.put("UNIT_INFO", currentMap.get("SUB_PTP_NAME")
							.toString() + "(" + currentMap.get("NOTE") + ")");
				} else {
					aSubRow.put("UNIT_INFO", currentMap.get("SUB_PTP_NAME"));
				}
				// if (currentMap.get("SUB_NOTE") != null
				// && !"".equals(currentMap.get("SUB_NOTE"))) {
				// aSubRow.put("UNIT_INFO", currentMap.get("EQUIP_NAME")
				// .toString()
				// + "("
				// + currentMap.get("SUB_NOTE")
				// + ")");
				// } else {
				// aSubRow.put("UNIT_INFO", currentMap.get("EQUIP_NAME"));
				// }
				if (currentMap.get("SUB_CALCULATE_POINT") == null)
					currentMap.put("SUB_CALCULATE_POINT", "");

				aSubRow.put("THEORETICAL_VALUE",
						currentMap.get("SUB_CALCULATE_POINT").toString());
				aSubRow.put("PM_VALUE", currentMap.get("SUB_PM_VALUE"));
				aSubRow.put("MULTI_SEC_ID", currentMap.get("MULTI_SEC_ID"));
				aSubRow.put("trunkLineId", currentMap.get("trunkLineId"));
				multiSecsDataListBackup.add(aSubRow);
				subNeRowCount++;
			} else if ((Integer) currentMap.get("SUB_ROUTE_TYPE") == 3) {
				// 衰耗器
				aSubRow = new HashMap<String, Object>();
				aSubRow.put("NE_ID", currentMap.get("NE_ID"));
				aSubRow.put("NE_DISPLAY_NAME",
						currentMap.get("NE_DISPLAY_NAME"));
				if (currentMap.get("SUB_NOTE") != null
						&& !"".equals(currentMap.get("SUB_NOTE"))) {
					aSubRow.put("UNIT_INFO", currentMap.get("SUB_PTP_NAME")
							.toString()
							+ "("
							+ currentMap.get("SUB_NOTE")
							+ ")");
				} else {
					aSubRow.put("UNIT_INFO", currentMap.get("SUB_PTP_NAME"));
				}
				aSubRow.put("PM_VALUE", currentMap.get("SUB_PM_VALUE"));
				// 光放理论值是啥( ´•︵•` )？
				aSubRow.put("THEORETICAL_VALUE",
						currentMap.get("SUB_CALCULATE_POINT"));
				aSubRow.put("PM_VALUE", currentMap.get("SUB_PM_VALUE"));
				aSubRow.put("MULTI_SEC_ID", currentMap.get("MULTI_SEC_ID"));
				aSubRow.put("trunkLineId", currentMap.get("trunkLineId"));
				multiSecsDataListBackup.add(aSubRow);
				subNeRowCount++;
			} else if ((Integer) currentMap.get("SUB_ROUTE_TYPE") == 6) {
				// 空白行
				aSubRow = new HashMap<String, Object>();
				aSubRow.put("NE_ID", currentMap.get("NE_ID"));
				aSubRow.put("NE_DISPLAY_NAME",
						currentMap.get("NE_DISPLAY_NAME"));
				aSubRow.put("MULTI_SEC_ID", currentMap.get("MULTI_SEC_ID"));
				aSubRow.put("trunkLineId", currentMap.get("trunkLineId"));
				multiSecsDataListBackup.add(aSubRow);
				subNeRowCount++;
			} else if ((Integer) currentMap.get("ROUTE_TYPE") == 7) {
				// DCM.....
				aSubRow = new HashMap<String, Object>();
				aSubRow.put("NE_ID", currentMap.get("NE_ID"));
				aSubRow.put("NE_DISPLAY_NAME",
						currentMap.get("NE_DISPLAY_NAME"));
				aSubRow.put("MULTI_SEC_ID", currentMap.get("MULTI_SEC_ID"));
				aSubRow.put("trunkLineId", currentMap.get("trunkLineId"));
				if (currentMap.get("NOTE") != null
						&& !"".equals(currentMap.get("NOTE"))) {
					aSubRow.put("UNIT_INFO", currentMap.get("SUB_PTP_NAME")
							.toString() + "(" + currentMap.get("NOTE") + ")");
				} else {
					aSubRow.put("UNIT_INFO", currentMap.get("SUB_PTP_NAME"));
				}
				aSubRow.put("THEORETICAL_VALUE",
						currentMap.get("SUB_CALCULATE_POINT"));
				aSubRow.put("PM_VALUE", currentMap.get("SUB_PM_VALUE"));
				multiSecsDataListBackup.add(aSubRow);
				subNeRowCount++;
			}
		}
		returnArray[0] = lastUnitId;
		returnArray[1] = lastPmType;
		returnArray[2] = neRowCount;
		returnArray[3] = lastSubUnitId;
		returnArray[4] = lastSubPmType;
		returnArray[5] = subNeRowCount;
		return returnArray;
	}

	// 主用路由的板卡行
	private Map<String, Object> constructAUnitRowMain(Map currentMap) {
		Map<String, Object> aRow;
		aRow = new HashMap<String, Object>();
		aRow.put("NE_ID", currentMap.get("NE_ID").toString());
		aRow.put("NE_DISPLAY_NAME", currentMap.get("NE_DISPLAY_NAME")
				.toString());
		if (currentMap.get("NOTE") != null
				&& !"".equals(currentMap.get("NOTE"))) {
			aRow.put("UNIT_INFO", currentMap.get("UNIT_DISPLAY_NAME")
					.toString() + "(" + currentMap.get("NOTE") + ")");
		} else {
			aRow.put("UNIT_INFO", currentMap.get("UNIT_DISPLAY_NAME")
					.toString());
		}
		aRow.put("MULTI_SEC_ID", currentMap.get("MULTI_SEC_ID"));
		aRow.put("trunkLineId", currentMap.get("trunkLineId"));
		return aRow;
	}

	// 主用路由性能行
	private Map<String, Object> constructAPmRowMain(Map currentMap, String arrow) {
		Map<String, Object> aRow;
		aRow = new HashMap<String, Object>();
		aRow.put("NE_ID", currentMap.get("NE_ID").toString());
		aRow.put("NE_DISPLAY_NAME", currentMap.get("NE_DISPLAY_NAME")
				.toString());
		aRow.put("UNIT_INFO", arrow);
		aRow.put("PM_VALUE", currentMap.get("PM_VALUE"));
		// 光放理论值是啥( ´•︵•` )？
		if (currentMap.get("CALCULATE_POINT") == null)
			currentMap.put("CALCULATE_POINT", "");
		aRow.put("THEORETICAL_VALUE", currentMap.get("CALCULATE_POINT")
				.toString());
		aRow.put("MULTI_SEC_ID", currentMap.get("MULTI_SEC_ID"));
		aRow.put("trunkLineId", currentMap.get("trunkLineId"));
		return aRow;
	}

	// 主用路由的虚拟设备行
	private Map<String, Object> constructAVirtualUnitRowMain(Map currentMap) {
		Map<String, Object> aRow;
		aRow = new HashMap<String, Object>();
		aRow.put("NE_ID", currentMap.get("NE_ID").toString());
		aRow.put("NE_DISPLAY_NAME", currentMap.get("NE_DISPLAY_NAME")
				.toString());
		if (currentMap.get("NOTE") != null
				&& !"".equals(currentMap.get("NOTE"))) {
			aRow.put("UNIT_INFO", currentMap.get("PTP_NAME").toString() + "("
					+ currentMap.get("NOTE") + ")");
		} else {
			aRow.put("UNIT_INFO", currentMap.get("PTP_NAME").toString());
		}
		aRow.put("PM_VALUE", currentMap.get("PM_VALUE"));
		aRow.put("MULTI_SEC_ID", currentMap.get("MULTI_SEC_ID"));
		aRow.put("trunkLineId", currentMap.get("trunkLineId"));
		return aRow;
	}

	// 计算板卡性能
	private void calculateUnitPm(Map preRow, Map unitRow, Map currentRow,
			int direction) {
		Map<String, Object> prePmMap = (Map<String, Object>) preRow
				.get("PM_VALUE");
		Map<String, Object> curPmMap = (Map<String, Object>) currentRow
				.get("PM_VALUE");
		Map<String, String> unitPmMap = new HashMap<String, String>();
		double prePm, curPm;
		DecimalFormat df = new DecimalFormat("0.00");
		if (prePmMap != null && curPmMap != null) {
			Set<String> set = prePmMap.keySet();
			for (String key : set) {
				if (prePmMap.containsKey(key) && prePmMap.get(key) != null
						&& curPmMap.containsKey(key)
						&& curPmMap.get(key) != null) {
					prePm = Double.parseDouble(prePmMap.get(key).toString());
					curPm = Double.parseDouble(curPmMap.get(key).toString());
					if (direction == 1) {
						unitPmMap.put(key, df.format(curPm - prePm));
					} else {
						unitPmMap.put(key, df.format(prePm - curPm));
					}
				}
			}
		}
		unitRow.put("PM_VALUE", unitPmMap);
		// 光放理论值是啥( ´•︵•` )？
		String preTv = preRow.get("THEORETICAL_VALUE").toString();
		String curTv = currentRow.get("THEORETICAL_VALUE").toString();
		if (preTv != null && curTv != null && !"".equals(preTv)
				&& !"".equals(curTv)) {
			prePm = Double.parseDouble(preTv);
			curPm = Double.parseDouble(curTv);
			if (direction == 1) {
				unitRow.put("THEORETICAL_VALUE", df.format(curPm - prePm));
			} else {
				unitRow.put("THEORETICAL_VALUE", df.format(prePm - curPm));
			}
		}
	}

	// 备用路由的板卡行
	private Map<String, Object> constructAUnitRowSub(Map currentMap) {
		Map<String, Object> aRow;
		aRow = new HashMap<String, Object>();
		aRow.put("NE_ID", currentMap.get("NE_ID").toString());
		aRow.put("NE_DISPLAY_NAME", currentMap.get("NE_DISPLAY_NAME")
				.toString());
		if (currentMap.get("SUB_NOTE") != null
				&& !"".equals(currentMap.get("SUB_NOTE"))) {
			aRow.put("UNIT_INFO", currentMap.get("SUB_UNIT_DISPLAY_NAME")
					.toString() + "(" + currentMap.get("SUB_NOTE") + ")");
		} else {
			aRow.put("UNIT_INFO", currentMap.get("SUB_UNIT_DISPLAY_NAME")
					.toString());
		}
		aRow.put("MULTI_SEC_ID", currentMap.get("MULTI_SEC_ID"));
		aRow.put("trunkLineId", currentMap.get("trunkLineId"));
		return aRow;
	}

	// 备用路由性能行
	private Map<String, Object> constructAPmRowSub(Map currentMap, String arrow) {
		Map<String, Object> aRow;
		aRow = new HashMap<String, Object>();
		aRow.put("NE_ID", currentMap.get("NE_ID").toString());
		aRow.put("NE_DISPLAY_NAME", currentMap.get("NE_DISPLAY_NAME")
				.toString());
		aRow.put("UNIT_INFO", arrow);
		aRow.put("PM_VALUE", currentMap.get("SUB_PM_VALUE"));
		// 光放理论值是啥( ´•︵•` )？
		aRow.put("THEORETICAL_VALUE", currentMap.get("SUB_CALCULATE_POINT")
				.toString());
		aRow.put("MULTI_SEC_ID", currentMap.get("MULTI_SEC_ID"));
		aRow.put("trunkLineId", currentMap.get("trunkLineId"));
		return aRow;
	}

	// 备用路由的虚拟设备行
	private Map<String, Object> constructAVirtualUnitRowSub(Map currentMap) {
		Map<String, Object> aRow;
		aRow = new HashMap<String, Object>();
		aRow.put("NE_ID", currentMap.get("NE_ID").toString());
		aRow.put("NE_DISPLAY_NAME", currentMap.get("NE_DISPLAY_NAME")
				.toString());
		if (currentMap.get("SUB_NOTE") != null
				&& !"".equals(currentMap.get("SUB_NOTE"))) {
			aRow.put("UNIT_INFO", currentMap.get("SUB_PTP_NAME").toString()
					+ "(" + currentMap.get("SUB_NOTE") + ")");
		} else {
			aRow.put("UNIT_INFO", currentMap.get("SUB_PTP_NAME").toString());
		}
		aRow.put("PM_VALUE", currentMap.get("SUB_PM_VALUE"));
		aRow.put("MULTI_SEC_ID", currentMap.get("MULTI_SEC_ID"));
		aRow.put("trunkLineId", currentMap.get("trunkLineId"));
		return aRow;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.IService.IPmMultipleSectionManagerService#ecportSecDetail
	 * (java.util.List)
	 */
	@Override
	public CommonResult ecportSecDetail(Map map) throws CommonException {

		CommonResult result = new CommonResult();
		Map select = null;
		select = new HashMap();
		List<Map> listExportz = new ArrayList<Map>();
		List<Map> listExportf = new ArrayList<Map>();

		select.put("MULTI_SEC_ID", map.get("MULTI_SEC_ID"));
		select.put("DIRECTION", CommonDefine.PM.DIRECTION.FORWARD);

		List<Map> listZh = pmMultipleSectionManagerMapper
				.selectMultiplePtpRoute(select);

		for (Map ma : listZh) {
			if (ma.get("PM_TYPE") != null
					&& ma.get("PM_TYPE").toString().equals("1")) {
				ma.put("PM_TYPE", "输入光功率");
			} else if (ma.get("PM_TYPE") != null
					&& ma.get("PM_TYPE").toString().equals("2")) {
				ma.put("PM_TYPE", "输出光功率");
			} else if (ma.get("PM_TYPE") != null
					&& ma.get("PM_TYPE").toString().equals("3")) {
				ma.put("PM_TYPE", "衰耗值");
			}
			if (ma.get("SUB_PM_TYPE") != null
					&& ma.get("SUB_PM_TYPE").toString().equals("1")) {
				ma.put("PM_TYPE", "输入光功率");
			} else if (ma.get("SUB_PM_TYPE") != null
					&& ma.get("SUB_PM_TYPE").toString().equals("2")) {
				ma.put("SUB_PM_TYPE", "输出光功率");
			} else if (ma.get("SUB_PM_TYPE") != null
					&& ma.get("SUB_PM_TYPE").toString().equals("3")) {
				ma.put("SUB_PM_TYPE", "衰耗值");
			}

			listExportz.add(ma);
		}
		select.put("DIRECTION", CommonDefine.PM.DIRECTION.OPPOSITE);

		List<Map> listFa = pmMultipleSectionManagerMapper
				.selectMultiplePtpRoute(select);
		if (listFa != null && listFa.size() > 0) {
			for (int i = listFa.size() - 1; i >= 0; i--) {
				Map ma = listFa.get(i);
				if (ma.get("PM_TYPE") != null
						&& ma.get("PM_TYPE").toString().equals("1")) {
					ma.put("PM_TYPE", "输入光功率");
				} else if (ma.get("PM_TYPE") != null
						&& ma.get("PM_TYPE").toString().equals("2")) {
					ma.put("PM_TYPE", "输出光功率");
				} else if (ma.get("PM_TYPE") != null
						&& ma.get("PM_TYPE").toString().equals("3")) {
					ma.put("PM_TYPE", "衰耗值");
				}
				if (ma.get("SUB_PM_TYPE") != null
						&& ma.get("SUB_PM_TYPE").toString().equals("1")) {
					ma.put("SUB_PM_TYPE", "输入光功率");
				} else if (ma.get("SUB_PM_TYPE") != null
						&& ma.get("SUB_PM_TYPE").toString().equals("2")) {
					ma.put("SUB_PM_TYPE", "输出光功率");
				} else if (ma.get("SUB_PM_TYPE") != null
						&& ma.get("SUB_PM_TYPE").toString().equals("3")) {
					ma.put("SUB_PM_TYPE", "衰耗值");
				}

				listExportf.add(ma);
			}
		}
		int headCode;
		String filename = "DEFAULT";
		if (map.get("cutoverFlag") != null
				&& map.get("cutoverFlag").toString().equals("1")) {
			headCode = CommonDefine.EXCEL.SEC_DETAIL_CUTOVER_EXPORT;
			filename = map.get("filename").toString();
		} else {
			headCode = CommonDefine.EXCEL.SEC_DETAIL_EXPORT;
			if (map.get("SEC_NAME") != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHMMss");
				String startString = sdf.format(new Date());
				filename = map.get("SEC_NAME").toString() + "_" + startString;

			}
		}
		IExportExcel ex = new ExportExcelUtil(CommonDefine.PATH_ROOT
				+ CommonDefine.EXCEL.TEMP_DIR, filename);
		String destination = ex.writeExcel(listExportz, headCode, true);
		if (listExportf != null && listExportf.size() > 0) {
			destination = ex.writeExcel(listExportf, headCode, true);
		}
		ex.close();
		if (destination != null) {
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(destination);
		} else {
			result.setReturnResult(CommonDefine.FAILED);
		}

		return result;
	}

	// ******************wangshanshan**************
	private List<Map> getMSStateForCSV(List<Map> targetList, Date startTime,
			Date endTime) throws CommonException {
		try {
			List<Map> returnList = new ArrayList<Map>();

			List<Map> MSIdList = pmMultipleSectionManagerMapper.getMSIdList(
					targetList, CommonDefine.TARGET_TYPE_MAP);
			Calendar start = Calendar.getInstance();
			Calendar end = Calendar.getInstance();
			start.setTime(startTime);
			end.setTime(endTime);
			for (Map id : MSIdList) {
				while (start.get(Calendar.DAY_OF_YEAR) <= end
						.get(Calendar.DAY_OF_YEAR)) {
					Map map = new HashMap();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					String startString = sdf.format(start.getTime());
					map.put("startTime", startString);
					map.put("BASE_EMS_CONNECTION_ID", id.get("emsId"));
					map.put("PM_MULTI_SEC_ID", id.get("MSId"));
					sycPmHistory(map);
					int level = multiStateHistory(map);
					Map MSInfoForCSV = pmMultipleSectionManagerMapper
							.getMSInfoForCSV(map);
					MSInfoForCSV.put("MSStatus", level);
					MSInfoForCSV.put("retrievalTime", startString);
					returnList.add(MSInfoForCSV);
					start.add(Calendar.DAY_OF_YEAR, 1);
				}
			}
			return returnList;
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}

	public int multiStateHistory(Map map) {
		Map select = null;
		// 查询出所有记录
		select = hashMapSon("t_pm_multi_sec_ptp", "MULTI_SEC_ID",
				map.get("PM_MULTI_SEC_ID"), null, null, null);
		List<Map> listSec = circuitManagerMapper.getByParameter(select);
		int level = CommonDefine.PM.MUL.SEC_PM_ZC;
		int levelTemp = 0;

		for (Map mapSec : listSec) {
			double value = 0.0;
			double curPm = 0.0;
			double stdPm = 0.0;
			double subCurPm = 0.0;
			double stdStdPm = 0.0;
			// 如果主用侧是端口或者虚拟端口，则进行计算
			if (mapSec.get("ROUTE_TYPE").toString()
					.equals(CommonDefine.PM.SECTON_ROUTE_TYPE.PTP + "")
					|| mapSec
							.get("ROUTE_TYPE")
							.toString()
							.equals(CommonDefine.PM.SECTON_ROUTE_TYPE.VIR_PORT
									+ "")) {
				if (mapSec.get("PM_VALUE") != null) {
					curPm = Double.parseDouble(mapSec.get("PM_VALUE")
							.toString());
				}
				if (mapSec.get("CUT_PM_VALUE") != null) {
					stdPm = Double.parseDouble(mapSec.get("CUT_PM_VALUE")
							.toString());
				}
				value = Math.abs(stdPm - curPm);
				// 判断级别
				levelTemp = getLevel(level, value);
				if (levelTemp > level) {
					level = levelTemp;
				}
			}

			// 如果备用侧是端口或者虚拟端口，则进行计算
			if (mapSec.get("SUB_ROUTE_TYPE").toString()
					.equals(CommonDefine.PM.SECTON_ROUTE_TYPE.PTP + "")
					|| mapSec
							.get("SUB_ROUTE_TYPE")
							.toString()
							.equals(CommonDefine.PM.SECTON_ROUTE_TYPE.VIR_PORT
									+ "")) {
				if (mapSec.get("SUB_PM_VALUE") != null) {
					subCurPm = Double.parseDouble(mapSec.get("SUB_PM_VALUE")
							.toString());
				}
				if (mapSec.get("SUB_CUT_PM_VALUE") != null) {
					stdStdPm = Double.parseDouble(mapSec
							.get("SUB_CUT_PM_VALUE").toString());
				}
				value = Math.abs(subCurPm - stdStdPm);
				// 判断级别
				levelTemp = getLevel(level, value);
				if (levelTemp > level) {
					level = levelTemp;
				}
			}
		}
		return level;
	}

	@Override
	public CommonResult exportMsPmReport(int taskId, boolean isInstantGen)
			throws CommonException {
		CommonResult result = new CommonResult();
		Map<String, List<Map<String, Object>>> origData;
		try {
			origData = getExportPmInfo(getPmFromTaskId(taskId));
			List<Map> newData = PMDataUtil.combineMsData(origData, false);
			List<Map> newDataRev = PMDataUtil.combineMsData(origData, true);
			Map<String, Object> headerInfo = PMDataUtil.getMsDates(origData);
			ColumnMap[] header = (ColumnMap[]) headerInfo.get("header");
			Map taskInfo = getTaskInfo(taskId);
			List<Map> targetList = pmMultipleSectionManagerMapper
					.getTargetList(taskId);

			// 包含了复用段以及干线信息
			List<Map> MSInfoList = pmMultipleSectionManagerMapper
					.getMSInfoListForReport(targetList,
							CommonDefine.TARGET_TYPE_MAP);
			Map<String, Map> MSInfoMap = new HashMap<String, Map>();
			for (Map m : MSInfoList) {
				MSInfoMap.put(m.get("MSId").toString(), m);
			}

			String taskDate = taskInfo.get("taskDate").toString();
			// [报表任务名称]([报表周期]-原始数据)_[实际性能事件日期]
			MSExcelUtil xls = new MSExcelUtil(CommonDefine.PATH_ROOT
					+ CommonDefine.EXCEL.REPORT_DIR + "\\"
					+ CommonDefine.EXCEL.PM_BASE + "\\" + taskDate + "\\"
					+ CommonDefine.EXCEL.PM_EXCEL + "\\"
					+ taskInfo.get("fileName"), header);
			xls.setInfoMap(MSInfoMap);
			xls.export(newData, newDataRev);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(xls.getFilePath());
			// 保存导出信息
			File xlsFile = new File(xls.getFilePath());
			Map<String, Object> exportInfo = new HashMap<String, Object>();
			exportInfo.put("SYS_TASK_ID", taskId);
			exportInfo.put("PRIVILEGE", taskInfo.get("privilege"));
			exportInfo.put("REPORT_NAME", taskInfo.get("fileName"));
			exportInfo.put("EXPORT_TIME", new Date());
			exportInfo.put("CREATOR", taskInfo.get("creator"));
			exportInfo.put("TASK_TYPE", CommonDefine.QUARTZ.JOB_REPORT_MS);
			exportInfo.put("PERIOD", taskInfo.get("period"));// 1月报 0日报
			exportInfo.put("SIZE", xlsFile.length() / 1024);
			exportInfo.put("EXCEL_URL", xls.getFilePath());
			Date startDate, endDate;
			startDate = (Date) taskInfo.get("startTime");
			endDate = (Date) taskInfo.get("endTime");
			// 导出到CSV并存储相关信息
			Map<String, String> csvInfo = exportMSDetailInCsv(taskId,
					startDate, endDate, taskInfo.get("csvFileName").toString());
			exportInfo.put("NORMAL_CSV_PATH", csvInfo.get("NORMAL_CSV_PATH"));
			exportInfo.put("ABNORMAL_CSV_PATH",
					csvInfo.get("ABNORMAL_CSV_PATH"));

			int stepPath = (Integer) taskInfo.get("step");
			Map statInfo = performanceManagerService.calculateReportCountInfo(
					taskId, startDate, endDate, stepPath);
			exportInfo
					.put("PM_EXCEPTION_LV1", statInfo.get("PM_EXCEPTION_LV1"));
			exportInfo
					.put("PM_EXCEPTION_LV2", statInfo.get("PM_EXCEPTION_LV2"));
			exportInfo
					.put("PM_EXCEPTION_LV3", statInfo.get("PM_EXCEPTION_LV3"));
			exportInfo
					.put("PM_ABNORMAL_RATE", statInfo.get("PM_ABNORMAL_RATE"));
			// 下面的貌似没用，先留着吧
			exportInfo.put("COLLECT_SUCCESS_RATE_PTP",
					statInfo.get("COLLECT_SUCCESS_RATE_PTP"));
			exportInfo.put("COLLECT_SUCCESS_RATE_MULTISEC",
					statInfo.get("COLLECT_SUCCESS_RATE_MULTISEC"));
			if (statInfo.get("FAILED_ID_PTP") == null)
				exportInfo.put("FAILED_ID_PTP", "");
			else
				exportInfo.put("FAILED_ID_PTP", statInfo.get("FAILED_ID_PTP"));
			if (statInfo.get("FAILED_ID_MULTI_SEC") == null)
				exportInfo.put("FAILED_ID_MULTI_SEC", "");
			else
				exportInfo.put("FAILED_ID_MULTI_SEC",
						statInfo.get("FAILED_ID_MULTI_SEC"));
			Map idMap = new HashMap();
			performanceManagerMapper.savePmExportInfo(exportInfo, idMap);
			performanceManagerMapper.savePmAnalysisInfo(exportInfo,
					CommonDefine.TARGET_TYPE_MAP, idMap);
		} catch (CommonException e) {
			e.printStackTrace();
			result.setReturnResult(CommonDefine.FAILED);
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			result.setReturnResult(CommonDefine.FAILED);
			throw new CommonException(e, -99999, "Excel报表导出发生错误！");
		}

		return result;
	}

	/**
	 * 根据任务Id导出复用段报表到Csv文件
	 * 
	 * @param taskId
	 *            任务Id
	 * @param startTime
	 *            任务起始时间
	 * @param endTime
	 *            任务结束时间
	 * @param fileName
	 *            csv文件名
	 * @return Map:{NORMAL_CSV_PATH:XXX,ABNORMAL_CSV_PATH:YYYYY}
	 */
	private Map<String, String> exportMSDetailInCsv(int taskId, Date startTime,
			Date endTime, String fileName) {
		// System.out.println("-------->exportMSDetailInCsv<--------");
		Map<String, String> rv = new HashMap<String, String>();
		List<Integer> msIdList = new ArrayList<Integer>();
		// 首先取出任务表中的所有复用段/干线ID
		List<Map> idList = performanceManagerMapper.getTaskTargetIds(taskId);

		for (int i = 0, len = idList.size(); i < len; i++) {
			Map idMap = idList.get(i);
			// 把所有的干线ID转化为复用段ID
			String idString = "";
			if (idMap.get("TARGET_Id") != null) {
				idString = idMap.get("TARGET_Id").toString();
			}
			String typeString = "";
			if (idMap.get("TARGET_TYPE") != null) {
				typeString = idMap.get("TARGET_TYPE").toString();
			}
			if ("10".equals(typeString)) {
				List<Integer> msIds = performanceManagerMapper
						.getMultiSecIds(idString);
				msIdList.addAll(msIds);
			} else if ("11".equals(typeString)) {
				Integer id = Integer.parseInt(idString);
				// 如果是复用段ID则直接push
				msIdList.add(id);
			}
		}
		// 取出复用段数据，保存为csv
		List<Map> dat = getMultiSecDetailInfoForReportSummary(msIdList,
				startTime, endTime);

		ColumnMap[] msCsvSrcHeader = {
				new ColumnMap("PM_MULTI_SEC_ID", "复用段ID",
						CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 12),
				new ColumnMap("EMS_GROUP_DISPLAY_NAME", "网管分组",
						CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 12),
				new ColumnMap("EMS_DISPLAY_NAME", "网管",
						CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 12),
				new ColumnMap("EMS_TYPE", "网管类型",
						CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
				new ColumnMap("TRUNK_LINE_DISPLAY_NAME", "干线",
						CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 12),
				new ColumnMap("SEC_NAME", "复用段",
						CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 15),
				new ColumnMap("DIRECTION", "方向",
						CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 16),
				new ColumnMap("STD_WAVE", "STD_WAVE",
						CommonDefine.PM.CUSTOM_REPORT.COMBO_DATE, 12),
				new ColumnMap("ACTULLY_WAVE", "ACTULLY_WAVE",
						CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
				new ColumnMap("MSStatus", "MSStatus",
						CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
				new ColumnMap("retrievalTime", "retrievalTime",
						CommonDefine.PM.CUSTOM_REPORT.COMBO_VALUE, 8) };
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		String yearmonth = sdf.format(new Date());
		// 原始数据
		CsvUtil csvOrig = new CsvUtil(CommonDefine.PATH_ROOT
				+ CommonDefine.EXCEL.REPORT_DIR + "/"
				+ CommonDefine.EXCEL.PM_BASE + "/" + yearmonth + "/"
				+ CommonDefine.EXCEL.PM_CSV + "/"
				+ fileName.replace("%TYPE%", "原始数据"), msCsvSrcHeader);
		ExportResult expRltOrig = csvOrig.exportMSData(dat, false);
		// 异常数据
		CsvUtil csvException = new CsvUtil(CommonDefine.PATH_ROOT
				+ CommonDefine.EXCEL.REPORT_DIR + "/"
				+ CommonDefine.EXCEL.PM_BASE + "/" + yearmonth + "/"
				+ CommonDefine.EXCEL.PM_CSV + "/"
				+ fileName.replace("%TYPE%", "异常数据"), msCsvSrcHeader);
		ExportResult expRltEx = csvException.exportMSData(dat, true);

		rv.put("NORMAL_CSV_PATH", expRltOrig.getFilePath());
		rv.put("ABNORMAL_CSV_PATH", expRltEx.getFilePath());
		return rv;
	}

	@Override
	public CommonResult exportMsPmReportInstant(List<Map> nodeList,
			Map<String, String> searchCond, int currentUserId)
			throws CommonException {
		int taskId = -1;
		List<Map<String, String>> target = new ArrayList<Map<String, String>>();
		CommonResult result = new CommonResult();
		Map<String, List<Map<String, Object>>> origData;
		for (Map m : nodeList) {
			Map<String, String> tar = new HashMap<String, String>();
			tar.put("targetId", m.get("targetId").toString());
			tar.put("targetType", m.get("targetType").toString());
			target.add(tar);
		}
		try {
			// 返回的新Id
			Map<String, Long> idMap = new HashMap<String, Long>();
			// 保存任务主要信息到t_sys_task表
			performanceManagerMapper.saveMSSysTask(searchCond, currentUserId,
					CommonDefine.QUARTZ.JOB_REPORT_MS, idMap);
			// 保存任务节点信息到t_sys_task_info
			performanceManagerMapper.saveMSSysTaskInfo(target, idMap);
			// 保存其他一些信息到param表中
			performanceManagerMapper.saveMSTaskParam(searchCond, idMap);

			// 获取之前建立的任务ID
			taskId = idMap.get("newId").intValue();
			Map taskInfo = getTaskInfo(taskId);
			List<Map> targetList = pmMultipleSectionManagerMapper
					.getTargetList(taskId);

			// 包含了复用段以及干线信息
			List<Map> MSInfoList = pmMultipleSectionManagerMapper
					.getMSInfoListForReport(targetList,
							CommonDefine.TARGET_TYPE_MAP);
			Map<String, Map> MSInfoMap = new HashMap<String, Map>();
			for (Map m : MSInfoList) {
				MSInfoMap.put(m.get("MSId").toString(), m);
			}
			int[] steps = { Calendar.DAY_OF_YEAR, Calendar.MONTH };
			// 判断是日报还是月报
			int period = Integer.parseInt(searchCond.get("period"));
			// 获取起止时间
			SimpleDateFormat dateParser = new SimpleDateFormat(
					CommonDefine.COMMON_SIMPLE_FORMAT);
			SimpleDateFormat cnSdfYmd = new SimpleDateFormat(
					CommonDefine.REPORT_CN_FORMAT);
			Date startTime = dateParser.parse(searchCond.get("start"));
			Date endTime = dateParser.parse(searchCond.get("end"));
			Map<String, Object> srcData = getPmFromTaskId(taskId,
					steps[period], startTime, endTime);
			origData = getExportPmInfo(srcData);

			List<Map> newData = PMDataUtil.combineMsData(origData, false);
			List<Map> newDataRev = PMDataUtil.combineMsData(origData, true);
			Map<String, Object> headerInfo = PMDataUtil.getMsDates(origData);
			ColumnMap[] header = (ColumnMap[]) headerInfo.get("header");
			// [报表任务名称]([开始日期]-[结束日期]-[周期]-原始数据)_即时报表_[报表生成日期]
			String[] periods = { "每日", "每月%day%号" };
			String taskName = searchCond.get("taskName");
			String dateRange = cnSdfYmd.format(startTime)
					+ "-"
					+ cnSdfYmd.format(endTime)
					+ "-"
					+ periods[period]
							.replace("%day%", searchCond.get("pmDate"))
					+ "-原始数据";
			SimpleDateFormat sdf = new SimpleDateFormat(
					CommonDefine.MS_REPORT_DAILY_FORMAT);
			String fileName = taskName + "(" + dateRange + ")_即时报表_"
					+ sdf.format(new Date()) + ".xlsx";
			// [报表任务名称]([报表周期]-原始数据)_[实际性能事件日期]
			MSExcelUtil xls = new MSExcelUtil(CommonDefine.PATH_ROOT
					+ CommonDefine.EXCEL.INSTANT_REPORT_DIR + "\\" + fileName,
					header);
			xls.setInfoMap(MSInfoMap);
			List<Map<String, String>> nodes = procNodes(nodeList);
			int targetType = Integer.parseInt(nodeList.get(0).get("targetType")
					.toString());
			CoverGenerator cov = new CoverGenerator(xls.getXls(), searchCond,
					nodes);
			cov.genMsCover(targetType == 10);
			xls.export(newData, newDataRev);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(xls.getFilePath());

			Map<String, Object> exportInfo = new HashMap<String, Object>();
			// exportInfo.put("SYS_TASK_ID", taskId);
			exportInfo.put("REPORT_NAME", fileName);
			exportInfo.put("EXPORT_TIME", new Date());
			exportInfo.put("CREATOR", currentUserId);
			exportInfo.put("TASK_TYPE", CommonDefine.QUARTZ.JOB_REPORT_MS);
			exportInfo.put("PERIOD", searchCond.get("period"));// 1是日报
			exportInfo.put("SIZE", xls.getSize());
			exportInfo.put("PRIVILEGE", searchCond.get("privilege"));
			exportInfo.put("EXCEL_URL", xls.getFilePath());
			Date startDate, endDate;
			startDate = (Date) taskInfo.get("startTime");
			endDate = (Date) taskInfo.get("endTime");
			exportInfo.put("NORMAL_CSV_PATH", "");
			exportInfo.put("ABNORMAL_CSV_PATH", "");

			int stepPath = (Integer) taskInfo.get("step");
			Map statInfo = performanceManagerService.calculateReportCountInfo(
					taskId, startDate, endDate, stepPath);
			exportInfo
					.put("PM_EXCEPTION_LV1", statInfo.get("PM_EXCEPTION_LV1"));
			exportInfo
					.put("PM_EXCEPTION_LV2", statInfo.get("PM_EXCEPTION_LV2"));
			exportInfo
					.put("PM_EXCEPTION_LV3", statInfo.get("PM_EXCEPTION_LV3"));
			exportInfo
					.put("PM_ABNORMAL_RATE", statInfo.get("PM_ABNORMAL_RATE"));
			performanceManagerMapper.savePmExportInfo(exportInfo, idMap);

		} catch (CommonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.setReturnResult(CommonDefine.FAILED);
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			result.setReturnResult(CommonDefine.FAILED);
			throw new CommonException(e, -99999, "Excel报表导出发生错误！");
		} finally {
			if (taskId > 0) {
				deleteReportTask(taskId);
			}
		}

		return result;
	}

	/**
	 * 删除临时建立的任务
	 * 
	 * @param taskId
	 * @throws CommonException
	 */
	private void deleteReportTask(int taskId) throws CommonException {
		Map<String, String> searchCond = new HashMap<String, String>();
		searchCond.put("taskId", taskId + "");
		Map<String, Object> def = new HashMap<String, Object>();
		def.put("TRUE", CommonDefine.TRUE);
		// JSONObject obj = JSONObject.fromObject(searchCond);
		// System.out.println("MS-deleteReportTask : ");
		// System.out.println(obj.toString());
		try {
			performanceManagerMapper.deleteReportTask(searchCond, def);
			// 删除对应的定时任务
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}

	private Map getTaskInfo(int taskId) {
		Map rv = new HashMap();
		Map searchCond = new HashMap();
		searchCond.put("taskId", taskId);
		Map<String, Object> taskInfo = (Map<String, Object>) performanceManagerMapper
				.searchNETaskInfoForEdit(searchCond).get(0);
		// 初始时间
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date startTime;
		String taskName = "", period = "", actualTime = "";
		taskName = taskInfo.get("taskName").toString();
		rv.put("taskName", taskName);
		SimpleDateFormat sf = null;
		SimpleDateFormat sdfFolder = new SimpleDateFormat(
				CommonDefine.GROUP_FORMAT);
		// [报表任务名称]([报表周期]-原始数据)_[实际性能事件日期]
		rv.put("period", taskInfo.get("period"));
		if (0 == ((Integer) taskInfo.get("period"))) {
			// 日报
			period = "日报";
			Integer delay = Integer.valueOf(taskInfo.get("delay").toString());
			calendar.add(Calendar.DAY_OF_YEAR, -delay);
			startTime = calendar.getTime();
			rv.put("startTime", startTime);
			rv.put("endTime", startTime);
			rv.put("step", Calendar.DAY_OF_YEAR);
			sf = CommonUtil
					.getDateFormatter(CommonDefine.MS_REPORT_DAILY_FORMAT);
		} else if (1 == ((Integer) taskInfo.get("period"))) {
			// 月报
			period = "月报";
			rv.put("endTime", calendar.getTime());
			calendar.add(Calendar.MONTH, -1);
			startTime = calendar.getTime();
			rv.put("startTime", startTime);
			rv.put("step", Calendar.MONTH);
			sf = CommonUtil
					.getDateFormatter(CommonDefine.MS_REPORT_MONTHLY_FORMAT);
		} else {
			// 即时报表
			period = "即时报表";
			calendar.add(Calendar.MONTH, -1);
			startTime = calendar.getTime();
			sf = CommonUtil
					.getDateFormatter(CommonDefine.MS_REPORT_MONTHLY_FORMAT);
		}
		actualTime = sf.format(startTime);
		rv.put("fileName", taskName + "(" + period + "-原始数据)_" + actualTime
				+ ".xlsx");
		rv.put("csvFileName", taskName + "(" + period + "-%TYPE%)_"
				+ actualTime + ".csv");
		rv.put("taskName", taskName);
		String taskDate = sdfFolder.format(startTime);
		rv.put("taskDate", taskDate);
		Object creator = taskInfo.get("creator");
		if (creator == null)
			creator = "-";
		rv.put("creator", creator.toString());
		rv.put("privilege", taskInfo.get("privilege"));
		return rv;
	}

	@Override
	public Map uploadTrunkLine(File uploadFile, String fileName, String path,
			int emsId) throws CommonException {
		Map map = new HashMap();
		boolean rc = false;
		try {
			// 如果文件不为空
			if (uploadFile != null) {
				rc = commonManagerService
						.uploadFile(uploadFile, fileName, path);
			}
			// 上传成功，则进行数据导入

			if (rc) {
				File file = new File(path + "\\" + fileName);
				boolean is2007 = false;
				if (fileName.endsWith(".xlsx")) {
					is2007 = true;
				}
				readExcelFileForTrunk(file, 0, is2007, emsId);
				rc = true;
			}
			map.put("success", rc);
		} catch (FileNotFoundException e) {
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_NOTFOUND);
		} catch (IOException e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_IO);
		} catch (BiffException e) {
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_BIFF);
		}
		return map;
	}

	/**
	 * 读取稽核电路路由信息
	 * 
	 * @param File
	 *            稽核电路路由信息文件
	 * @return void
	 * @throws IOException
	 * @throws BiffException
	 */
	public void readExcelFileForTrunk(File file, int sheetNumber,
			boolean is2007, int emsId) throws BiffException, IOException {
		CommonResult result = new CommonResult();
		Workbook workbook = null;
		InputStream in = null;
		Map select = null;
		Object lineId = null;

		in = new FileInputStream(file);
		if (is2007) {
			workbook = new XSSFWorkbook(in);
		} else {
			workbook = new HSSFWorkbook(in);
		}

		int sheetCount = workbook.getNumberOfSheets();
		String stdWave = "";// 标称波道数
		if (sheetCount > 0) {
			// 首先读取干线名称以及标称波道数
			Sheet sheet = workbook.getSheetAt(0);
			int rowCount = sheet.getPhysicalNumberOfRows();
			for (int i = 0; i < rowCount; i++) {
				if ("DWDM名称".equals(sheet.getRow(i).getCell(0)
						.getStringCellValue())) {
					// 插入干线信息
					String trunkLine = sheet.getRow(i).getCell(1)
							.getStringCellValue();

					// 验证干线是否已经存在
					select = hashMapSon("t_pm_trunk_line", "DISPLAY_NAME",
							trunkLine, "BASE_EMS_CONNECTION_ID", emsId, null);
					// 先判断是否存在
					List<Map> list = circuitManagerMapper
							.getByParameter(select);
					if (list != null && list.size() > 0) {
						// 如果存在，则提示干线名称已存在
						// result.setReturnResult(CommonDefine.FAILED);
						// result.setReturnMessage(MessageHandler
						// .getErrorMessage(MessageCodeDefine.PM_EXIST));
						// return result;
					} else {
						Map insert = new HashMap();
						insert.put("DISPLAY_NAME", trunkLine);
						insert.put("BASE_EMS_CONNECTION_ID", emsId);
						pmMultipleSectionManagerMapper.insertTrunkLine(insert);
						// select = new HashMap();
						// select.put("NAME", "t_pm_trunk_line");
						// select.put("IN_NAME", "DISPLAY_NAME");
						// select.put("IN_VALUE", trunkLine);
						// select.put("ID", "PM_TRUNK_LINE_ID");
						//
						// Map map =
						// circuitManagerMapper.getLatestRecord(select);
						lineId = insert.get("PM_TRUNK_LINE_ID");
					}
				}
				if ("系统波数量".equals(sheet.getRow(i).getCell(0)
						.getStringCellValue())) {
					// 记录标称波道数
					stdWave = sheet.getRow(i).getCell(1).getStringCellValue();
				}
			}
			// 导入光复用段
			if (sheetCount > 1) {
				Sheet sheetMultiple = workbook.getSheetAt(1);
				int rowCountMultiple = sheetMultiple.getPhysicalNumberOfRows();
				for (int j = 1; j < rowCountMultiple; j++) {
					if (sheetMultiple.getRow(j).getCell(1) != null
							&& !sheetMultiple.getRow(j).getCell(1)
									.getStringCellValue().isEmpty()) {
						// 记录标称波道数
						String multipleName = sheetMultiple.getRow(j)
								.getCell(1).getStringCellValue().trim();
						// 插入光复用段
						select = hashMapSon("t_pm_multi_sec", "SEC_NAME",
								multipleName, "PM_TRUNK_LINE_ID", lineId, null);
						// 先判断是否存在
						List<Map> list = circuitManagerMapper
								.getByParameter(select);
						if (list != null && list.size() > 0) {
							// 如果存在，则提示干线名称已存在

						} else {
							Map insert = new HashMap();
							insert.put("SEC_NAME", multipleName);
							insert.put("STD_WAVE", stdWave);
							insert.put("PM_TRUNK_LINE_ID", lineId);
							insert.put("DIRECTION", CommonDefine.DIRECTION_TWO);
							pmMultipleSectionManagerMapper
									.insertMultipleSection(insert);
						}
					}
				}
			}
		}

	}

	@Override
	public void sycPmByMultipleByPort(List<Map> listMap) throws CommonException {
		// TODO Auto-generated method stub
		try {
			Map select = null;
			Map update = null;

			if (listMap != null && listMap.size() > 0) {
				List listPort = new ArrayList();
				for (int i = 0; i < listMap.size(); i++) {

					Map ma = listMap.get(i);

					if (ma.get("ROUTE_TYPE") != null) {
						// 如果是实际端口
						if ((CommonDefine.PM.SECTON_ROUTE_TYPE.PTP + "")
								.equals(ma.get("ROUTE_TYPE").toString())) {
							listPort.add(ma.get("PTP_ID"));
						} else if ((CommonDefine.PM.SECTON_ROUTE_TYPE.VIR_PORT + "")
								.equals(ma.get("ROUTE_TYPE").toString())) {
							// 虚拟端口
							if (ma.get("PTP_ID") != null) {
								String ids[] = ma.get("PTP_ID").toString()
										.split(",");
								for (String id : ids) {
									listPort.add(id);
								}
							}
						}
					}
					if (ma.get("SUB_ROUTE_TYPE") != null) {
						// 如果是实际端口
						if ((CommonDefine.PM.SECTON_ROUTE_TYPE.PTP + "")
								.equals(ma.get("SUB_ROUTE_TYPE").toString())) {
							listPort.add(ma.get("SUB_PTP_ID"));
						} else if ((CommonDefine.PM.SECTON_ROUTE_TYPE.VIR_PORT + "")
								.equals(ma.get("ROUTE_TYPE").toString())) {
							// 虚拟端口
							if (ma.get("SUB_PTP_ID") != null) {
								String ids[] = ma.get("SUB_PTP_ID").toString()
										.split(",");
								for (String id : ids) {
									listPort.add(id);
								}
							}
						}
					}

				}
				// 采集数据

				List<PmDataModel> listPm = getCurrentPmData(listPort);

				for (int i = 0; i < listMap.size(); i++) {
					Map ma = listMap.get(i);
					List<Map> updateList = new ArrayList<Map>();

					Map addMap = new HashMap();
					addMap.put("PM_MULTI_SEC_PTP_ID",
							ma.get("PM_MULTI_SEC_PTP_ID"));

					// 更新主用端口值
					if (ma.get("ROUTE_TYPE") != null
							&& (CommonDefine.PM.SECTON_ROUTE_TYPE.PTP + "")
									.equals(ma.get("ROUTE_TYPE").toString())) {
						for (PmDataModel pmDataModel : listPm) {
							if (ma.get("PTP_ID") != null
									&& pmDataModel
											.getPtpId()
											.toString()
											.equals(ma.get("PTP_ID").toString())) {
								List<PmMeasurementModel> pmMeasurementList = pmDataModel
										.getPmMeasurementList();
								if (ma.get("PM_TYPE") != null
										&& (CommonDefine.PM.PORT_TYPE.PORT_IN + "")
												.equals(ma.get("PM_TYPE")
														.toString())) {
									for (PmMeasurementModel pmMeasurementModel : pmMeasurementList) {

										// 输入光功率当前值
										if (CommonDefine.PM.STD_INDEX_RPL_CUR
												.equals(pmMeasurementModel
														.getPmStdIndex())
												|| CommonDefine.PM.STD_INDEX_RPL_AVG
														.equals(pmMeasurementModel
																.getPmStdIndex())) {
											//
											addMap.put("CURRENT_PM_VALUE",
													pmMeasurementModel
															.getValue());
											addMap.put("CURRENT_PM_TIME",
													new Date());
											break;
										}
									}
								} else if (ma.get("PM_TYPE") != null
										&& (CommonDefine.PM.PORT_TYPE.PORT_OUT + "")
												.equals(ma.get("PM_TYPE")
														.toString())) {
									for (PmMeasurementModel pmMeasurementModel : pmMeasurementList) {
										// 输出光功率当前值
										if (CommonDefine.PM.STD_INDEX_TPL_CUR
												.equals(pmMeasurementModel
														.getPmStdIndex())
												|| CommonDefine.PM.STD_INDEX_TPL_AVG
														.equals(pmMeasurementModel
																.getPmStdIndex())) {
											//
											addMap.put("CURRENT_PM_VALUE",
													pmMeasurementModel
															.getValue());
											addMap.put("CURRENT_PM_TIME",
													new Date());
											break;
										}
									}

								}

								// 结束本次循环

							}
						}
					} else if (ma.get("ROUTE_TYPE") != null
							&& (CommonDefine.PM.SECTON_ROUTE_TYPE.VIR_PORT + "")
									.equals(ma.get("ROUTE_TYPE").toString())) {// 更新主用虚拟端口

						double value = 0.0;

						if (ma.get("PTP_ID") != null) {
							String ids[] = ma.get("PTP_ID").toString()
									.split(",");
							for (String id : ids) {
								for (PmDataModel pmDataModel : listPm) {
									if (pmDataModel.getPtpId().toString()
											.equals(id)) {
										List<PmMeasurementModel> pmMeasurementList = pmDataModel
												.getPmMeasurementList();
										if (ma.get("PM_TYPE") != null
												&& (CommonDefine.PM.PORT_TYPE.PORT_IN + "")
														.equals(ma
																.get("PM_TYPE"))) {
											for (PmMeasurementModel pmMeasurementModel : pmMeasurementList) {
												// 输入光功率当前值
												if (CommonDefine.PM.STD_INDEX_RPL_CUR
														.equals(pmMeasurementModel
																.getPmStdIndex())
														|| CommonDefine.PM.STD_INDEX_RPL_AVG
																.equals(pmMeasurementModel
																		.getPmStdIndex())) {
													value += Math
															.pow(10,
																	Double.valueOf(pmMeasurementModel
																			.getValue()) / 10);

													break;
												}
											}
										} else if (ma.get("PM_TYPE") != null
												&& (CommonDefine.PM.PORT_TYPE.PORT_OUT + "")
														.equals(ma.get(
																"PM_TYPE")
																.toString())) {
											for (PmMeasurementModel pmMeasurementModel : pmMeasurementList) {
												// 输出光功率当前值
												if (CommonDefine.PM.STD_INDEX_TPL_CUR
														.equals(pmMeasurementModel
																.getPmStdIndex())
														|| CommonDefine.PM.STD_INDEX_TPL_AVG
																.equals(pmMeasurementModel
																		.getPmStdIndex())) {
													value += Math
															.pow(10,
																	Double.valueOf(pmMeasurementModel
																			.getValue()) / 10);
													break;
												}
											}

										}

										// 结束本次循环

									}
								}
							}
						}
						if (value != 0) {
							value = 10 * Math.log10(value);
						}
						addMap.put("CURRENT_PM_VALUE", getDouble(value));
						addMap.put("CURRENT_PM_TIME", new Date());
					}

					if (ma.get("SUB_ROUTE_TYPE") != null
							&& (CommonDefine.PM.SECTON_ROUTE_TYPE.PTP + "")
									.equals(ma.get("SUB_ROUTE_TYPE").toString())) {
						for (PmDataModel pmDataModel : listPm) {
							if (ma.get("SUB_PTP_ID") != null
									&& pmDataModel.getPtpId().toString()
											.equals(ma.get("SUB_PTP_ID"))) {
								List<PmMeasurementModel> pmMeasurementList = pmDataModel
										.getPmMeasurementList();
								if (ma.get("SUB_PM_TYPE") != null
										&& (CommonDefine.PM.PORT_TYPE.PORT_IN + "")
												.equals(ma.get("SUB_PM_TYPE"))) {
									for (PmMeasurementModel pmMeasurementModel : pmMeasurementList) {
										// 输入光功率当前值
										if (CommonDefine.PM.STD_INDEX_RPL_CUR
												.equals(pmMeasurementModel
														.getPmStdIndex())
												|| CommonDefine.PM.STD_INDEX_RPL_AVG
														.equals(pmMeasurementModel
																.getPmStdIndex())) {
											//
											addMap.put("SUB_CURRENT_PM_VALUE",
													pmMeasurementModel
															.getValue());
											addMap.put("CURRENT_PM_TIME",
													new Date());

											break;
										}
									}
								} else if (ma.get("SUB_PM_TYPE") != null
										&& (CommonDefine.PM.PORT_TYPE.PORT_OUT + "")
												.equals(ma.get("SUB_PM_TYPE")
														.toString())) {
									for (PmMeasurementModel pmMeasurementModel : pmMeasurementList) {
										// 输出光功率当前值
										if (CommonDefine.PM.STD_INDEX_TPL_CUR
												.equals(pmMeasurementModel
														.getPmStdIndex())
												|| CommonDefine.PM.STD_INDEX_TPL_AVG
														.equals(pmMeasurementModel
																.getPmStdIndex())) {
											//
											addMap.put("SUB_CURRENT_PM_VALUE",
													pmMeasurementModel
															.getValue());
											addMap.put("CURRENT_PM_TIME",
													new Date());
											break;
										}
									}

								}

								// 结束本次循环

							}
						}
					} else if (ma.get("SUB_ROUTE_TYPE") != null
							&& (CommonDefine.PM.SECTON_ROUTE_TYPE.VIR_PORT + "")
									.equals(ma.get("SUB_ROUTE_TYPE"))) {
						// 虚拟端口
						if (ma.get("SUB_PTP_ID") != null) {
							String ids[] = ma.get("SUB_PTP_ID").toString()
									.split(",");
							for (String id : ids) {
								// 更新主用虚拟端口
								double value = 0.0;
								for (PmDataModel pmDataModel : listPm) {
									if (pmDataModel.getPtpId().toString()
											.equals(id)) {
										List<PmMeasurementModel> pmMeasurementList = pmDataModel
												.getPmMeasurementList();
										if (ma.get("SUB_PM_TYPE") != null
												&& (CommonDefine.PM.PORT_TYPE.PORT_IN + "")
														.equals(ma.get(
																"SUB_PM_TYPE")
																.toString())) {
											for (PmMeasurementModel pmMeasurementModel : pmMeasurementList) {
												// 输入光功率当前值
												if (CommonDefine.PM.STD_INDEX_RPL_CUR
														.equals(pmMeasurementModel
																.getPmStdIndex())
														|| CommonDefine.PM.STD_INDEX_RPL_AVG
																.equals(pmMeasurementModel
																		.getPmStdIndex())) {
													value += Math
															.pow(10,
																	Double.valueOf(pmMeasurementModel
																			.getValue()) / 10);

													break;
												}
											}
										} else if (ma.get("SUB_PM_TYPE") != null
												&& (CommonDefine.PM.PORT_TYPE.PORT_OUT + "")
														.equals(ma.get(
																"SUB_PM_TYPE")
																.toString())) {
											for (PmMeasurementModel pmMeasurementModel : pmMeasurementList) {
												// 输出光功率当前值
												if (CommonDefine.PM.STD_INDEX_TPL_CUR
														.equals(pmMeasurementModel
																.getPmStdIndex())
														|| CommonDefine.PM.STD_INDEX_TPL_AVG
																.equals(pmMeasurementModel
																		.getPmStdIndex())) {
													value += Math
															.pow(10,
																	Double.valueOf(pmMeasurementModel
																			.getValue()) / 10);
													break;
												}
											}

										}

										// 结束本次循环

									}
								}

								if (value != 0) {
									value = 10 * Math.log10(value);
								}
								addMap.put("SUB_CURRENT_PM_VALUE",
										getDouble(value));
								addMap.put("CURRENT_PM_TIME", new Date());
							}
						}
					}

					updateList.add(addMap);

					// 更新光复用段当前值
					for (Map up : updateList) {
						update = new HashMap();
						update.put("NAME", "t_pm_multi_sec_ptp");
						update.put("ID_NAME", "PM_MULTI_SEC_PTP_ID");
						update.put("ID_VALUE", up.get("PM_MULTI_SEC_PTP_ID"));
						update.put("ID_NAME_2", "CURRENT_PM_VALUE");
						update.put("ID_VALUE_2", up.get("CURRENT_PM_VALUE"));
						update.put("ID_NAME_3", "SUB_CURRENT_PM_VALUE");
						update.put("ID_VALUE_3", up.get("SUB_CURRENT_PM_VALUE"));
						update.put("ID_NAME_4", "CURRENT_PM_TIME");
						update.put("ID_VALUE_4", up.get("CURRENT_PM_TIME"));

						circuitManagerMapper.updateByParameter(update);
					}
					// 更新光复用段时间
					if (updateList != null && updateList.size() > 0) {
						update = new HashMap();
						update.put("NAME", "t_pm_multi_sec");
						update.put("ID_NAME", "PM_MULTI_SEC_ID");
						update.put("ID_VALUE", ma.get("PM_MULTI_SEC_ID"));
						update.put("ID_NAME_2", "PM_UPDATE_TIME");
						update.put("ID_VALUE_2", new Date());

						circuitManagerMapper.updateByParameter(update);
					}

					getMultipleState(ma);

				}
				// 先重新取一遍光复用段 ,正向，反向分别取
				select = new HashMap();
				select.put("MULTI_SEC_ID", listMap.get(0)
						.get("PM_MULTI_SEC_ID"));
				select.put("DIRECTION", 1);
				// hashMapSon("t_pm_multi_sec_ptp",
				// "MULTI_SEC_ID",listMap.get(0).get("PM_MULTI_SEC_ID"),
				// "DIRECTION", 1, null);
				List<Map> listz = pmMultipleSectionManagerMapper
						.selectMultiplePtpRoute(select);
				// 计算当前性能段衰耗以及衰耗值
				updateDuanValue(listz, "CURRENT_PM_VALUE", 0);
				// 反向
				// select = hashMapSon("t_pm_multi_sec_ptp",
				// "MULTI_SEC_ID",listMap.get(0).get("PM_MULTI_SEC_ID"),
				// "DIRECTION", 2, null);
				select = new HashMap();
				select.put("MULTI_SEC_ID", listMap.get(0)
						.get("PM_MULTI_SEC_ID"));
				select.put("DIRECTION", 2);
				List<Map> listf = pmMultipleSectionManagerMapper
						.selectMultiplePtpRoute(select);
				// 计算当前性能段衰耗以及衰耗值
				updateDuanValue(listf, "CURRENT_PM_VALUE", 0);
				// 统计复用段告警信息
			}
		} catch (CommonException e) {
			throw e;
		}

	}

	/** * 判断字符串是否是浮点数 */
	public boolean isDouble(Object value) {
		try {
			if (value != null) {
				Double.parseDouble(value.toString());
			} else {
				return false;
			}

			return true;

		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public CommonResult exportAllInformation(List<Map> listMap)
			throws CommonException {

		CommonResult result = new CommonResult();
		int headCode;
		String filename = "DEFAULT";
		headCode = CommonDefine.EXCEL.SEC_ALL_EXPORT;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHMMss");
		String startString = sdf.format(new Date());
		filename = "光复用段_" + startString;

		IExportExcel ex = new ExportExcelUtil(CommonDefine.PATH_ROOT
				+ CommonDefine.EXCEL.TEMP_DIR, filename);
		String destination = "";

		Map select = null;
		select = new HashMap();
		List<Map> listSec = null;
		// 判断是导全部还是部分光复用段
		if (listMap != null && listMap.size() > 0) {
		} else {
			select = hashMapSon("t_pm_multi_sec", "IS_DEL", CommonDefine.FALSE,
					null, null, null);
			listMap = circuitManagerMapper.getByParameter(select);
		}

		if (listMap != null && listMap.size() > 0) {
			for (int i = 0; i < listMap.size(); i++) {
				// 导出数据集合
				List<Map> listExport = new ArrayList<Map>();

				// 查出光复用端信息
				select = hashMapSon("t_pm_multi_sec", "PM_MULTI_SEC_ID",
						listMap.get(i).get("PM_MULTI_SEC_ID"), null, null, null);
				List<Map> listSection = circuitManagerMapper
						.getByParameter(select);
				if (listSection != null && listSection.size() > 0) {
					Map mapSection = listSection.get(0);
					// 取第一条并获取其干线信息
					select = hashMapSon("t_pm_trunk_line", "PM_TRUNK_LINE_ID",
							listSection.get(0).get("PM_TRUNK_LINE_ID"), null,
							null, null);
					List<Map> listTrunk = circuitManagerMapper
							.getByParameter(select);
					if (listTrunk != null && listTrunk.size() > 0) {
						mapSection.put("IP", listTrunk.get(0).get("IP"));
						mapSection.put("DISPLAY_NAME",
								listTrunk.get(0).get("DISPLAY_NAME"));
					} else {
						result.setReturnResult(CommonDefine.FAILED);
						result.setReturnMessage("");
						return result;
					}
					mapSection
							.put("SEC_DIRECTION", mapSection.get("DIRECTION"));
					mapSection.remove("DIRECTION");
					// 先将干线信息存入数据库
					listExport.add(mapSection);
					// 路由信息导入
					select = hashMapSon("t_pm_multi_sec_ptp", "MULTI_SEC_ID",
							listSection.get(0).get("PM_MULTI_SEC_ID"), null,
							null, null);
					List<Map> listRoute = circuitManagerMapper
							.getByParameter(select);
					if (listRoute != null && listRoute.size() > 0) {
						for (int j = 0; j < listRoute.size(); j++) {
							Map mapRoute = listRoute.get(j);
							// 获取网元名称，等记录信息
							select = hashMapSon("t_pm_multi_sec_ne",
									"MULTI_SEC_NE_ID",
									mapRoute.get("MULTI_SECT_NE_ROUTE_ID"),
									null, null, null);
							List<Map> listNe = circuitManagerMapper
									.getByParameter(select);
							if (listNe != null && listNe.size() > 0) {
								mapRoute.put("NE_NAME",
										listNe.get(0).get("NE_NAME"));
								mapRoute.put("NE_SEQUENCE",
										listNe.get(0).get("SEQUENCE"));
							} else {
								result.setReturnResult(CommonDefine.FAILED);
								result.setReturnMessage("");
								return result;
							}
							// 获取光放盘型号（主）
							if (mapRoute.get("PM_STD_OPT_AMP_ID") != null
									&& !mapRoute.get("PM_STD_OPT_AMP_ID")
											.toString().isEmpty()) {
								select = hashMapSon("t_pm_std_opt_amp",
										"PM_STD_OPT_AMP_ID",
										mapRoute.get("PM_STD_OPT_AMP_ID"),
										null, null, null);
								List<Map> listOpt = circuitManagerMapper
										.getByParameter(select);
								if (listOpt != null && listOpt.size() > 0) {
									mapRoute.put("FACTORY",
											listOpt.get(0).get("FACTORY"));
									mapRoute.put("TYPE",
											listOpt.get(0).get("TYPE"));
									mapRoute.put("MODEL",
											listOpt.get(0).get("MODEL"));
									mapRoute.put("MAX_OUT",
											listOpt.get(0).get("MAX_OUT"));
									mapRoute.put("MIN_GAIN", listOpt.get(0)
											.get("MIN_GAIN"));
									mapRoute.put("MAX_GAIN", listOpt.get(0)
											.get("MAX_GAIN"));
									mapRoute.put("TYPICAL_GAIN", listOpt.get(0)
											.get("TYPICAL_GAIN"));
									mapRoute.put("MAX_IN",
											listOpt.get(0).get("MAX_IN"));
									mapRoute.put("MIN_IN",
											listOpt.get(0).get("MIN_IN"));
									mapRoute.put("TYPICAL_IN", listOpt.get(0)
											.get("TYPICAL_IN"));
								}
							}
							// 获取光放盘型号（备）
							if (mapRoute.get("SUB_PM_STD_OPT_AMP_ID") != null
									&& !mapRoute.get("SUB_PM_STD_OPT_AMP_ID")
											.toString().isEmpty()) {
								select = hashMapSon("t_pm_std_opt_amp",
										"PM_STD_OPT_AMP_ID",
										mapRoute.get("SUB_PM_STD_OPT_AMP_ID"),
										null, null, null);
								List<Map> listOpt = circuitManagerMapper
										.getByParameter(select);
								if (listOpt != null && listOpt.size() > 0) {
									mapRoute.put("SUB_FACTORY", listOpt.get(0)
											.get("FACTORY"));
									mapRoute.put("SUB_TYPE", listOpt.get(0)
											.get("TYPE"));
									mapRoute.put("SUB_MODEL", listOpt.get(0)
											.get("MODEL"));
									mapRoute.put("SUB_MAX_OUT", listOpt.get(0)
											.get("MAX_OUT"));
									mapRoute.put("SUB_MIN_GAIN", listOpt.get(0)
											.get("MIN_GAIN"));
									mapRoute.put("SUB_MAX_GAIN", listOpt.get(0)
											.get("MAX_GAIN"));
									mapRoute.put("SUB_TYPICAL_GAIN", listOpt
											.get(0).get("TYPICAL_GAIN"));
									mapRoute.put("SUB_MAX_IN", listOpt.get(0)
											.get("MAX_IN"));
									mapRoute.put("SUB_MIN_IN", listOpt.get(0)
											.get("MIN_IN"));
									mapRoute.put("SUB_TYPICAL_IN",
											listOpt.get(0).get("TYPICAL_IN"));
								}
							}

							listExport.add(mapRoute);
						}

					}
				}

				destination = ex.writeExcel(listExport, headCode, true);
			}
		}
		ex.close();
		if (destination != null) {
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(destination);
		} else {
			result.setReturnResult(CommonDefine.FAILED);
		}

		return result;

	}

	@Override
	public Map uploadSectionAll(File uploadFile, String fileName, String path)
			throws CommonException {
		Map map = new HashMap();
		boolean rc = false;
		try {
			// 如果文件不为空
			if (uploadFile != null) {
				rc = commonManagerService
						.uploadFile(uploadFile, fileName, path);
			}
			// 上传成功，则进行数据导入
			CommonResult res = new CommonResult();
			if (rc) {
				File file = new File(path + "\\" + fileName);
				boolean is2007 = false;
				if (fileName.endsWith(".xlsx")) {
					is2007 = true;
				}
				res = readExcelForSecTion(file, 0, is2007);
			}
			map.put("success", "导入成功！");
		} catch (FileNotFoundException e) {
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_NOTFOUND);
		} catch (IOException e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_IO);
		} catch (BiffException e) {
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_BIFF);
		}
		return map;
	}

	/**
	 * 读取光复用段全部导入信息
	 * 
	 * @param File
	 *            读取光复用段全部导入信息
	 * @return void
	 * @throws IOException
	 * @throws BiffException
	 */
	public CommonResult readExcelForSecTion(File file, int sheetNumber,
			boolean is2007) throws BiffException, IOException, CommonException {
		CommonResult result = new CommonResult();
		result.setReturnMessage("导入成功！");
		Workbook workbook = null;
		InputStream in = null;
		Map select = null;
		Map insert = null;
		Map update = null;
		Object lineId = null;

		in = new FileInputStream(file);
		if (is2007) {
			workbook = new XSSFWorkbook(in);
		} else {
			workbook = new HSSFWorkbook(in);
		}

		int sheetCount = workbook.getNumberOfSheets();
		if (sheetCount > 0) {
			// 遍历sheet，每个sheet就是一个光复用段
			for (int i = 0; i < sheetCount; i++) {
				Sheet sheet = workbook.getSheetAt(i);
				int rowCount = sheet.getPhysicalNumberOfRows();
				// 首先读取光复用段信息，默认在第二行
				// 第一行第一列是网管ip
				String ip = getCell(sheet.getRow(1).getCell(0));
				// 第一行第二列是干线名称
				String trunkName = getCell(sheet.getRow(1).getCell(1));
				// 第一行第三列是光复用段
				String secName = getCell(sheet.getRow(1).getCell(2));
				// 标称波道数
				String std_wave = ""
						.equals(getCell(sheet.getRow(1).getCell(3))) ? "0"
						: getCell(sheet.getRow(1).getCell(3));
				// 实际波道数
				String act_wave = ""
						.equals(getCell(sheet.getRow(1).getCell(4))) ? "0"
						: getCell(sheet.getRow(1).getCell(4));
				// 单向还是双向
				String sec_direction = "".equals(getCell(sheet.getRow(1)
						.getCell(5))) ? "1" : getCell(sheet.getRow(1)
						.getCell(5));

				select = hashMapSon("t_base_ems_connection", "IP", ip, null,
						null, null);
				List<Map> listEms = circuitManagerMapper.getByParameter(select);
				if (listEms != null && listEms.size() > 0) {
					Object emsId = listEms.get(0).get("BASE_EMS_CONNECTION_ID");
					// 查看干线是否存在,不存在则新增
					select = hashMapSon("t_pm_trunk_line", "IP", ip,
							"DISPLAY_NAME", trunkName, null);
					List<Map> listTrunk = circuitManagerMapper
							.getByParameter(select);
					Object trunkId = "";
					if (listTrunk != null && listTrunk.size() > 0) {
						trunkId = listTrunk.get(0).get("PM_TRUNK_LINE_ID");
					} else {
						// 如果不存在，则插入干线
						insert = new HashMap();
						insert.put("BASE_EMS_CONNECTION_ID", emsId);
						insert.put("IP", ip);
						insert.put("DISPLAY_NAME", trunkName);
						pmMultipleSectionManagerMapper.insertTrunkLine(insert);
						trunkId = insert.get("PM_TRUNK_LINE_ID");
					}

					// 判断光复用段是否存在
					select = hashMapSon("t_pm_multi_sec", "PM_TRUNK_LINE_ID",
							trunkId, "SEC_NAME", secName, null);
					List<Map> listSec = circuitManagerMapper
							.getByParameter(select);
					if (listSec != null && listSec.size() > 0) {
						// 提示光复用段已经存在
						throw new CommonException(new Exception(), -1, "sheet"
								+ (i + 1) + "光复用段" + secName + "已经存在！");
						// result.setReturnMessage("光复用段以经存在");
						// result.setReturnResult(CommonDefine.FAILED);
						// return result;
					} else {
						// 新增光复用段
						insert = new HashMap();
						insert.put("SEC_NAME", secName);
						insert.put("STD_WAVE", std_wave);
						insert.put("ACTULLY_WAVE", act_wave);
						insert.put("PM_TRUNK_LINE_ID", trunkId);
						insert.put("DIRECTION", sec_direction);
						pmMultipleSectionManagerMapper
								.insertMultipleSection(insert);
						Object secId = insert.get("PM_MULTI_SEC_ID");
						// 从第三行往下遍历，都是路由详情
						for (int j = 2; j < rowCount; j++) {
							// 路由信息从第12列开始
							String neName = getCell(sheet.getRow(j).getCell(11));
							// 正向反向
							String direction = getCell(sheet.getRow(j).getCell(
									42));
							if ("".equals(neName)) {
								// 表示已经结束
								break;
							}
							// 查询网元在数据库的id
							select = hashMapSon("t_base_ne", "NAME", neName,
									"BASE_EMS_CONNECTION_ID", emsId, null);
							List<Map> listNe = circuitManagerMapper
									.getByParameter(select);
							if (listNe != null && listNe.size() > 0) {
								Object neId = listNe.get(0).get("BASE_NE_ID");
								// 往网元路由表中插入数据
								String neSquence = getCell(sheet.getRow(j)
										.getCell(12));

								// 看是否已存在数据
								select = hashMapSon("t_pm_multi_sec_ne",
										"MULTI_SEC_ID", secId, "NE_ID", neId,
										null);
								select.put("ID_NAME_3", "DIRECTION");
								select.put("ID_VALUE_3", direction);
								List<Map> listNeRoute = circuitManagerMapper
										.getByParameter(select);
								Object neRouteId = null;
								if (listNeRoute != null
										&& listNeRoute.size() > 0) {
									neRouteId = listNeRoute.get(0).get(
											"MULTI_SEC_NE_ID");
									// 提示已存在网元路由信息，无法导入
									// result.setReturnMessage("");
									// result.setReturnResult(CommonDefine.FAILED);
									// return result;

								} else {
									insert = new HashMap();
									insert.put("MULTI_SEC_ID", secId);
									insert.put("NE_ID", neId);
									insert.put("NE_NAME", neName);
									insert.put("SEQUENCE", neSquence);
									insert.put("DIRECTION", direction);
									pmMultipleSectionManagerMapper
											.insertMulSecNe(insert);
									neRouteId = insert.get("MULTI_SEC_NE_ID");
								}

								// 判断光放盘是否存在，不存在则插入
								String optName = getCell(sheet.getRow(j)
										.getCell(43));
								Object optId = null;
								Object optSubId = null;
								if ("".equals(optName)) {

								} else {
									String factory = getCell(sheet.getRow(j)
											.getCell(44));
									String type = getCell(sheet.getRow(j)
											.getCell(45));
									String model = getCell(sheet.getRow(j)
											.getCell(46));
									String maxOut = getCell(sheet.getRow(j)
											.getCell(47));
									String minGain = getCell(sheet.getRow(j)
											.getCell(48));
									String maxGain = getCell(sheet.getRow(j)
											.getCell(49));
									String typicalGain = getCell(sheet
											.getRow(j).getCell(50));
									String maxIn = getCell(sheet.getRow(j)
											.getCell(51));
									String minIn = getCell(sheet.getRow(j)
											.getCell(52));
									String typicalIn = getCell(sheet.getRow(j)
											.getCell(53));
									select = hashMapSon("t_pm_std_opt_amp",
											"FACTORY", factory, "MODEL", model,
											null);
									List<Map> listOpt = circuitManagerMapper
											.getByParameter(select);

									if (listOpt != null && listOpt.size() > 0) {
										optId = listOpt.get(0).get(
												"PM_STD_OPT_AMP_ID");
									} else {
										// 如果不存在，则插入数据
										insert = new HashMap();
										insert.put("FACTORY", factory);
										insert.put("TYPE", type);
										insert.put("MODEL", model);
										insert.put("MAX_OUT", maxOut);
										insert.put("MIN_GAIN", minGain);
										insert.put("MAX_GAIN", maxGain);
										insert.put("TYPICAL_GAIN", typicalGain);
										insert.put("MAX_IN", maxIn);
										insert.put("MIN_IN", minIn);
										insert.put("TYPICAL_IN", typicalIn);
										pmMultipleSectionManagerMapper
												.insertStandOptVal(insert);
										optId = insert.get("PM_STD_OPT_AMP_ID");

									}
								}
								String optSubName = getCell(sheet.getRow(j)
										.getCell(54));
								if ("".equals(optSubName)) {

								} else {
									String subFactory = getCell(sheet.getRow(j)
											.getCell(55));
									String subType = getCell(sheet.getRow(j)
											.getCell(56));
									String subModel = getCell(sheet.getRow(j)
											.getCell(57));
									String subMaxOut = getCell(sheet.getRow(j)
											.getCell(58));
									String subMinGain = getCell(sheet.getRow(j)
											.getCell(59));
									String subMaxGain = getCell(sheet.getRow(j)
											.getCell(60));
									String subTypicalGain = getCell(sheet
											.getRow(j).getCell(61));
									String subMaxIn = getCell(sheet.getRow(j)
											.getCell(62));
									String subMinIn = getCell(sheet.getRow(j)
											.getCell(63));
									String subTypicalIn = getCell(sheet.getRow(
											j).getCell(64));
									select = hashMapSon("t_pm_std_opt_amp",
											"FACTORY", subFactory, "MODEL",
											subModel, null);
									List<Map> listSubOpt = circuitManagerMapper
											.getByParameter(select);

									if (listSubOpt != null
											&& listSubOpt.size() > 0) {
										optSubId = listSubOpt.get(0).get(
												"PM_STD_OPT_AMP_ID");
									} else {
										// 如果不存在，则插入数据
										insert = new HashMap();
										insert.put("FACTORY", subFactory);
										insert.put("TYPE", subType);
										insert.put("MODEL", subModel);
										insert.put("MAX_OUT", subMaxOut);
										insert.put("MIN_GAIN", subMinGain);
										insert.put("MAX_GAIN", subMaxGain);
										insert.put("TYPICAL_GAIN",
												subTypicalGain);
										insert.put("MAX_IN", subMaxIn);
										insert.put("MIN_IN", subMinIn);
										insert.put("TYPICAL_IN", subTypicalIn);
										pmMultipleSectionManagerMapper
												.insertStandOptVal(insert);
										optSubId = insert
												.get("PM_STD_OPT_AMP_ID");

									}
								}
								// 解析端口
								String ptpId = "";
								String ptpName = getCell(sheet.getRow(j)
										.getCell(14));
								if (!ptpName.isEmpty()) {
									String[] port = ptpName.split(",");
									for (String name : port) {
										// 导入使用，格式
										// direction|ptptype|rackno|shelfno|slotno|portno
										// 如有多个,分隔
										String[] no = name.split("\\|");
										select = hashMapSon("t_base_ptp",
												"PORT_NO", no[5], "SLOT_NO",
												no[4], null);
										select.put("ID_NAME_3", "SHELF_NO");
										select.put("ID_VALUE_3", no[3]);
										select.put("ID_NAME_4", "RACK_NO");
										select.put("ID_VALUE_4", no[2]);
										select.put("ID_NAME_5", "PTP_TYPE");
										select.put("ID_VALUE_5", no[1]);
										select.put("ID_NAME_6", "DIRECTION");
										select.put("ID_VALUE_6", no[0]);
										select.put("ID_NAME_7", "BASE_NE_ID");
										select.put("ID_VALUE_7", neId);
										List<Map> listptp = circuitManagerMapper
												.getByParameter(select);
										if (listptp != null
												&& listptp.size() > 0) {
											ptpId = listptp.get(0)
													.get("BASE_PTP_ID")
													.toString()
													+ ",";
										}

									}
								}
								if (ptpId.length() > 0) {
									ptpId = ptpId.substring(0,
											ptpId.length() - 1);
								}
								// 解析备用端口
								String ptpSubName = getCell(sheet.getRow(j)
										.getCell(15));
								String ptpSubId = "";
								if (!ptpSubName.isEmpty()) {
									String[] port = ptpName.split(",");
									for (String name : port) {
										// 导入使用，格式
										// direction|ptptype|rackno|shelfno|slotno|portno
										// 如有多个,分隔
										String[] no = name.split("\\|");
										select = hashMapSon("t_base_ptp",
												"PORT_NO", no[5], "SLOT_NO",
												no[4], null);
										select.put("ID_NAME_3", "SHELF_NO");
										select.put("ID_VALUE_3", no[3]);
										select.put("ID_NAME_4", "RACK_NO");
										select.put("ID_VALUE_4", no[2]);
										select.put("ID_NAME_5", "PTP_TYPE");
										select.put("ID_VALUE_5", no[1]);
										select.put("ID_NAME_6", "DIRECTION");
										select.put("ID_VALUE_6", no[0]);
										select.put("ID_NAME_7", "BASE_NE_ID");
										select.put("ID_VALUE_7", neId);
										List<Map> listptp = circuitManagerMapper
												.getByParameter(select);
										if (listptp != null
												&& listptp.size() > 0) {
											ptpSubId = listptp.get(0)
													.get("BASE_PTP_ID")
													.toString()
													+ ",";
										}

									}
								}
								if (ptpSubId.length() > 0) {
									ptpSubId = ptpSubId.substring(0,
											ptpSubId.length() - 1);
								}
								String equipName = getCell(sheet.getRow(j)
										.getCell(13));
								String ptpTag = getCell(sheet.getRow(j)
										.getCell(14));
								String ptpSubTag = getCell(sheet.getRow(j)
										.getCell(15));
								String ptpNa = getCell(sheet.getRow(j).getCell(
										16));
								String ptpSubNa = getCell(sheet.getRow(j)
										.getCell(17));
								String cauPoint = getCell(sheet.getRow(j)
										.getCell(20));
								String cauSubPoint = getCell(sheet.getRow(j)
										.getCell(21));
								String note = getCell(sheet.getRow(j).getCell(
										26));
								String subNote = getCell(sheet.getRow(j)
										.getCell(27));

								String pmType = getCell(sheet.getRow(j)
										.getCell(28));
								String pmSubType = getCell(sheet.getRow(j)
										.getCell(29));

								String routeType = getCell(sheet.getRow(j)
										.getCell(30));
								String routeSubType = getCell(sheet.getRow(j)
										.getCell(31));

								String ptpSquence = getCell(sheet.getRow(j)
										.getCell(32));

								String hisPm = getCell(sheet.getRow(j).getCell(
										33));
								String hisSubPm = getCell(sheet.getRow(j)
										.getCell(34));

								String cutPm = getCell(sheet.getRow(j).getCell(
										35));
								String cutSubPm = getCell(sheet.getRow(j)
										.getCell(36));

								String curPm = getCell(sheet.getRow(j).getCell(
										37));
								String curSubPm = getCell(sheet.getRow(j)
										.getCell(38));

								Map add = new HashMap();
								add.put("MULTI_SEC_ID", secId);
								add.put("MULTI_SECT_NE_ROUTE_ID", neRouteId);
								add.put("EQUIP_NAME", equipName);
								add.put("PTP_ID", ptpId);
								add.put("PTP_TAG", ptpTag);
								add.put("SUB_PTP_ID", ptpSubId);
								add.put("SUB_PTP_TAG", ptpSubTag);
								add.put("PTP_NAME", ptpNa);
								add.put("SUB_PTP_NAME", ptpSubNa);
								add.put("CALCULATE_POINT", cauPoint);
								add.put("SUB_CALCULATE_POINT", cauSubPoint);
								add.put("NOTE", note);
								add.put("SUB_NOTE", subNote);
								add.put("PM_TYPE", pmType);

								add.put("SUB_PM_TYPE", pmSubType);
								add.put("ROUTE_TYPE", routeType);
								add.put("SUB_ROUTE_TYPE", routeSubType);
								add.put("SEQUENCE", ptpSquence);
								add.put("HISTORY_PM_VALUE", hisPm);
								add.put("SUB_HISTORY_PM_VALUE", hisSubPm);
								add.put("CUT_PM_VALUE", cutPm);
								add.put("SUB_CUT_PM_VALUE", cutSubPm);
								add.put("CURRENT_PM_VALUE", curPm);
								add.put("SUB_CURRENT_PM_VALUE", curSubPm);
								add.put("PM_STD_OPT_AMP_ID", optId);
								add.put("SUB_PM_STD_OPT_AMP_ID", optSubId);
								add.put("DIRECTION", direction);

								pmMultipleSectionManagerMapper
										.insertMultiplePtp(add);

							} else {

								// 删除前面导入的光复用段
								List<Map> listDelete = new ArrayList<Map>();
								Map mapPut = new HashMap();
								mapPut.put("PM_MULTI_SEC_ID", secId);
								listDelete.add(mapPut);
								deleteMultipleSection(listDelete);
								throw new CommonException(new Exception(), -1,
										"sheet" + (i + 1) + "光复用段" + secName
												+ "网元" + neName + "不存在，无法导入！");
								// 提示网元不存在，无法导入
								// result.setReturnMessage("提示网元不存在，无法导入");
								// result.setReturnResult(CommonDefine.FAILED);
								// return result;
							}
						}
					}
				} else {
					// 提示网管不存在，无法导入
					throw new CommonException(new Exception(), -1, "sheet"
							+ (i + 1) + "光复用段" + secName + "网管不存在，无法导入！");
				}

			}

		}
		return result;

	}

	// 获取Excelcell中值
	public String getCell(Cell cell) {
		if (cell == null)
			return "";
		switch (cell.getCellType()) {
		case 1:
			return "-".equals(cell.getStringCellValue()) ? "" : cell
					.getStringCellValue();
		case 0:
			return String.valueOf((int) cell.getNumericCellValue());
		default:
			return "";

		}
	}

	@Override
	public Boolean hasRecord(int mulId, int direction) throws CommonException {
		/*
		 * 说明： 根据插入逻辑，其实只要判断t_pm_multi_sec_ne中是否有数据即可，
		 * 但由于删除的操作是分别针对t_pm_multi_sec_ne和t_pm_multi_sec_ptp两张表进行的，
		 * 为了防止由于删除异常而导致的不可预见的非法数据在数据库中存在,所以每次都检查两张表中是否有路由数据以防万一
		 */
		// 判断t_pm_multi_sec_ne表中是否有路由数据
		Boolean bl = true;
		// 判断t_pm_multi_sec_ptp表中是否有路由数据
		Boolean blp = true;
		try {
			// 查看t_pm_multi_sec_ne表中是否存在记录
			bl = pmMultipleSectionManagerMapper.getMultipleRouteRecordTotal(
					mulId, direction) > 0;
			// 查看t_pm_multi_sec_ptp表中是否存在记录
			blp = pmMultipleSectionManagerMapper
					.getMultipleRouteRecordTotalOnPtp(mulId, direction) > 0;
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
			throw new CommonException(e, -1, "自动生成失败:查询是否存在记录时出错");
		}
		return bl || blp;
	}

	@Override
	public void deleteRouteRecordByMark(int mulId, int direction)
			throws CommonException {
		try {
			// 根据光复用段Id、方向,删除t_pm_multi_sec_ne表中的记录
			pmMultipleSectionManagerMapper.deleteRouteOnNeByMark(mulId,
					direction);
			// 根据光复用段Id、方向,删除t_pm_multi_sec_ptp表中的记录
			pmMultipleSectionManagerMapper.deleteRouteOnPtpByMark(mulId,
					direction);
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
			throw new CommonException(e, -1, "自动生成失败:标记删除记录时出错");
		}
	}

	@Override
	public void deleteRouteRecord(int mulId, int direction)
			throws CommonException {
		try {
			// 先删除t_pm_multi_sec_ptp再删除t_pm_multi_sec_ne(外键)
			// 根据光复用段Id、方向,删除t_pm_multi_sec_ptp表中的记录
			pmMultipleSectionManagerMapper.deleteRouteOnPtp(mulId, direction);
			// 根据光复用段Id、方向,删除t_pm_multi_sec_ne表中的记录
			pmMultipleSectionManagerMapper.deleteRouteOnNe(mulId, direction);
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
			throw new CommonException(e, -1, "自动生成失败:删除记录时出错");
		}
	}

	/**
	 * 根据电路编号自动插入光复用段路由记录,由autoCreateRoute方法调用
	 * 
	 * @param mulId
	 *            光复用段Id
	 * @param direction
	 *            方向：1正向,2反向
	 * @param cirId
	 *            电路Id
	 * @param routeId
	 *            电路路由Id
	 * @throws CommonException
	 */
	private void saveMultiRoute(int mulId, int direction, int cirId, int routeId)
			throws CommonException {
		try {
			// 截取合适的路由记录:以routeId开始,读取每条路由记录,直到下一跳A端口类型值不为:OS/OTS/OMS的路由记录
			List<Map> routeInfoTemp = pmMultipleSectionManagerMapper
					.getRouteInfo(cirId, routeId);
			// 获取路由信息
			List<Map> routeInfo = new ArrayList<Map>();
			// 获取截取的最后一个routeId
			int endRouteId = 0;
			List aType = new ArrayList();
			aType.add("A_OS");
			aType.add("A_OMS");
			aType.add("A_OTS");
			for (Map temp : routeInfoTemp) {
				if (!aType.contains(temp.get("A_TYPE"))) {
					break;
				} else {
					endRouteId = Integer.parseInt(temp.get(
							"CIR_OTN_CIRCUIT_ROUTE_ID").toString());
					routeInfo.add(temp);
				}
			}
			// 根据电路Id和路由Id获取路由中符合条件的网元信息
			List<Map> neInRoute = pmMultipleSectionManagerMapper.getNeInRoute(
					cirId, routeId, endRouteId);
			// 向t_pm_multi_sec_ne表中循环插入路由信息
			for (int i = 0; i < neInRoute.size(); i++) {
				Map temp = new HashMap();
				temp.put("MULTI_SEC_ID", mulId);
				temp.put("NE_ID", neInRoute.get(i).get("BASE_NE_ID"));
				temp.put("SEQUENCE", i + 1);
				temp.put("DIRECTION", direction);
				temp.put("NE_NAME", neInRoute.get(i).get("DISPLAY_NAME"));
				pmMultipleSectionManagerMapper.insertMulSecNe(temp);
			}

			// 定义aheadNeId记录上一个网元的Id,以减少查询数据库的次数
			int aheadNeId = 0;
			// 获取t_pm_multi_sec_ne表中的MULTI_SEC_NE_ID字段
			Object neRouteId = null;
			Object equipName = null;
			for (int j = 0; j < routeInfo.size(); j++) {
				// 如果查询到指定条件的MULTI_SEC_NE_ID字段,将其赋值给neRouteId变量;
				if (routeInfo.get(j).get("BASE_NE_ID") != null) {
					int neId = Integer.parseInt(routeInfo.get(j)
							.get("BASE_NE_ID").toString());
					// 当neId是没有查询过的
					if (aheadNeId != neId) {
						// 查询MULTI_SEC_NE_ID字段
						List<Map> tempResult = pmMultipleSectionManagerMapper
								.getMultiSecNeId(mulId, neId, direction);
						if (tempResult.size() > 0) {
							neRouteId = tempResult.get(0)
									.get("MULTI_SEC_NE_ID");
							// 将neId赋值给aheadNeId
							aheadNeId = neId;
						} else {
							// 先将neRouteId置为null
							neRouteId = null;
						}
					}
				} else {
					neRouteId = null;
				}
				// common包括：复用段Id(MULTI_SEC_ID)、外键(MULTI_SECT_NE_ROUTE_ID)、方向(DIRECTION)、设备名称(EQUIP_NAME)等信息
				equipName = routeInfo.get(j).get("NE_NAME");
				Map common = new HashMap();
				common.put("MULTI_SEC_ID", mulId);
				common.put("MULTI_SECT_NE_ROUTE_ID", neRouteId);
				common.put("DIRECTION", direction);
				common.put("EQUIP_NAME", equipName);
				common.put("ROUTE_TYPE", 1);
				// aEnd包括端口Id(PTP_ID),端口名称(PTP_NAME),序列(SEQUENCE);
				Map aEnd = new HashMap();
				aEnd.put("PTP_ID", routeInfo.get(j).get("A_END_PTP"));
				aEnd.put("PTP_NAME", routeInfo.get(j).get("A_PTP_NAME"));
				aEnd.put("SEQUENCE", 2 * j + 1);
				aEnd.putAll(common);
				// 插入A端数据
				pmMultipleSectionManagerMapper.insertMultiplePtp(aEnd);
				// zEnd包括端口Id(PTP_ID),端口名称(PTP_NAME),序列(SEQUENCE);
				Map zEnd = new HashMap();
				zEnd.put("PTP_ID", routeInfo.get(j).get("Z_END_PTP"));
				zEnd.put("PTP_NAME", routeInfo.get(j).get("Z_PTP_NAME"));
				zEnd.put("SEQUENCE", 2 * j + 2);
				zEnd.putAll(common);
				// 插入Z端数据
				pmMultipleSectionManagerMapper.insertMultiplePtp(zEnd);
			}
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
			throw new CommonException(e, -1, "自动生成失败:插入数据时发生错误");
		}
	}

	@Override
	public CommonResult autoCreateRoute(int mulId, int direction, int startPtp)
			throws CommonException {
		CommonResult result = new CommonResult();
		try {
			// 根据起始端口Id(startPtp)查询出一组电路路由信息,并判断A_TYPE是否为OS/OTS/OMS：电路Id(CIR_OTN_CIRCUIT_ID),路由Id(CIR_OTN_CIRCUIT_ROUTE_ID)
			List<Map<String, Integer>> cirInfo = pmMultipleSectionManagerMapper
					.getCircuitRouteInfoByPtp(startPtp);
			// 如果查询结果为空,跳出程序
			if (cirInfo.size() == 0) {
				result.setReturnResult(CommonDefine.FALSE);
				result.setReturnMessage("自动生成失败:未找到任何路由记录");
				return result;
			}
			// 如果只有一条记录,却要反向生成,跳出程序
			if (cirInfo.size() == 1 && direction == 2) {
				result.setReturnResult(CommonDefine.FALSE);
				result.setReturnMessage("自动生成失败:未找到反向路由记录");
				return result;
			}
			/*
			 * 如果查询到的记录个数>1,则向t_pm_multi_sec_ne,t_pm_multi_sec_ptp表中插入数据，
			 * 否则抛出为查询到路由记录异常;
			 * 当direction为1(正向)时，根据cirIds的第一条记录插入，当direction为2(反向
			 * )时，根据cirIds的第二条记录(如果存在)插入;
			 * 插入之前,根据复用段id(mulId)信息调用deleteRouteRecordByMark方法删除已存在的记录
			 * ,注：这里的删除为标记删除,即将t_pm_multi_sec_ne,t_pm_multi_sec_ptp表中IS_DEL字段置1
			 * 插入之后
			 * ,调用deleteRouteRecord方法将之前标记删除的数据从数据库中彻底删除,这样标记删除+真实删除,确保数据库出现异常时能恢复数据
			 * ;
			 */
			// 标记删除存在的记录
			this.deleteRouteRecordByMark(mulId, direction);
			// 开始插入信息
			Map<String, Integer> temp = new HashMap<String, Integer>();
			// 正向
			if (direction == 1 && cirInfo.size() > 0) {
				temp = cirInfo.get(0);
			} else if (direction == 2 && cirInfo.size() > 1) {// 反向
				temp = cirInfo.get(1);
			} else {
				throw new Exception();
			}
			// 保存路由记录
			this.saveMultiRoute(mulId, direction,
					temp.get("CIR_OTN_CIRCUIT_ID"),
					temp.get("CIR_OTN_CIRCUIT_ROUTE_ID"));
			// 真实删除存在的记录：即将deleteRouteRcordByMark方法标记删除的数据从数据库中删除
			this.deleteRouteRecord(mulId, direction);
		} catch (CommonException e) {
			ExceptionHandler.handleException(e);
			throw e;
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
			throw new CommonException(e, -1, "自动生成失败:内部错误");
		}
		result.setReturnResult(CommonDefine.SUCCESS);
		return result;
	}

	private List<Map<String, String>> procNodes(List<Map> nodes) {
		List<Map> dbNodes = new ArrayList<Map>();
		for (Map m : nodes) {
			Map<String, String> tar = new HashMap<String, String>();
			tar.put("nodeId", m.get("targetId").toString());
			tar.put("nodeLevel", m.get("targetType").toString());
			dbNodes.add(tar);
		}
		int targetType = Integer.parseInt(nodes.get(0).get("targetType")
				.toString());
		List<Map> taskNodesInfo;
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		if (targetType == 11) {
			taskNodesInfo = performanceManagerMapper.searchMSNodesInfo(dbNodes,
					REPORT_DEFINE);
			for (Map m : taskNodesInfo) {
				Map<String, String> tar = new HashMap<String, String>();
				tar.put("trunkLineName", m.get("trunkLineName").toString());
				tar.put("MSName", m.get("MSName").toString());
				tar.put("direction",
						"1".equals(m.get("direction").toString()) ? "单向" : "双向");
				result.add(tar);
			}
		}
		if (targetType == 10) {
			List<Map> TLInfoList = performanceManagerMapper.searchTLNodesInfo(
					dbNodes, REPORT_DEFINE);
			List<Map> TLMS = performanceManagerMapper.searchTLMS(TLInfoList,
					REPORT_DEFINE);
			taskNodesInfo = TLInfoTransformer(TLInfoList, TLMS);
			for (Map m : taskNodesInfo) {
				Map<String, String> tar = new HashMap<String, String>();
				tar.put("trunkLineName", m.get("trunkLineName").toString());
				tar.put("MSNameTag", m.get("MSNameTag").toString());
				tar.put("MSNameList", m.get("MSNameList").toString());
				result.add(tar);
			}
		}
		return result;
	}

	// 为了在提示中显示复用段信息，需要组装数据
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<Map> TLInfoTransformer(List<Map> list, List<Map> TLMS) {
		List<Map> returnList = new ArrayList<Map>();
		for (Map mapTL : list) {
			for (Map mapMS : TLMS) {
				if (mapTL.get("TLId").equals(mapMS.get("TLId"))) {
					String MSNameList = mapMS.get("MSNameList").toString();
					String[] listMS = MSNameList.split(",");
					StringBuffer MSNameTag = new StringBuffer();
					if (listMS.length > 0) {
						for (int i = 0; i < listMS.length; i++) {
							MSNameTag.append(listMS[i]);
							if (i != listMS.length - 1)
								MSNameTag.append(",");
							if ((i + 1) % 5 == 0) {
								MSNameTag.append("\r\n");
							}
						}
					}
					mapTL.put("MSNameList", MSNameList);
					mapTL.put("MSNameTag", MSNameTag.toString());
				}
			}
			returnList.add(mapTL);
		}
		return returnList;
	}

	public double getDouble(double value) {
		// 精确到小数点后两位。。。
		DecimalFormat df = new DecimalFormat("#0.00");
		String db = df.format(value);
		return Double.parseDouble(db);
	}
}
