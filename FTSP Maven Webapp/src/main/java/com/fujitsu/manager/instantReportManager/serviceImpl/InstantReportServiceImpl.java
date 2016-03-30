package com.fujitsu.manager.instantReportManager.serviceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.IService.IPerformanceManagerService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.common.PMDataUtil;
import com.fujitsu.common.poi.MultiColumnMap;
import com.fujitsu.common.poi.MultiHearderExcelUtil;
import com.fujitsu.dao.mysql.InstantReportMapper;
import com.fujitsu.dao.mysql.PerformanceManagerMapper;
import com.fujitsu.manager.instantReportManager.service.InstantReportService;
import com.fujitsu.util.SpringContextUtil;

@Scope("prototype")
@Service
@Transactional(rollbackFor = Exception.class)
public class InstantReportServiceImpl extends InstantReportService {
	@Resource
	private InstantReportMapper instantReportMapper;
	@Resource
	private PerformanceManagerMapper performanceManagerMapper;

	@Resource
	public IPerformanceManagerService performanceManagerService;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String generateOptPathBitErrReport(Map<String, String> condMap,
			List<Map> nodeList, Integer sysUserId) throws CommonException {
		try {
			List<Map> resultMap = new ArrayList<Map>();
			// tplevel条件处理
//			condMap.get("tpLevel").replaceAll(", ", "','");
			condMap.put("tpLevel",
					"'" + condMap.get("tpLevel").replaceAll(", ", "','") + "'");
			// 转换为[nodeId，nodeLevel，emsId]格式的数据
			Map<String, String> conditionMap = performanceManagerService
					.nodeListClassify(nodeList);
			//只要SDH的
			conditionMap.put("neType", String.valueOf(CommonDefine.NE_TYPE_SDH_FLAG));
			// 换算为NE
			nodeList = instantReportMapper.getNeUnderThisNode(conditionMap,
					RegularDefine, TREE_DEFINE);
			// filepath
			String fileName = condMap.get("taskName") + "("
					+ condMap.get("start") + "到" + condMap.get("end") + ")_即时报表_"
					+ getTodayString(CommonDefine.COMMON_SIMPLE_FORMAT) + ".xlsx";
			String filePath = CommonDefine.PATH_ROOT
					+ CommonDefine.EXCEL.INSTANT_REPORT_DIR + "\\" + fileName;
			MultiHearderExcelUtil xls = new MultiHearderExcelUtil(filePath);
			xls.setReplaceEmpty("-");
			int startNe = 0, countNe = nodeList.size(), endNe;
//			MultiHearderExcelUtil.openFile("C://合并区域记录1.txt");
			List<String> dateList = null;
			while (countNe > 0) {
				// 判断一下到不到了
				endNe = countNe - NE_INTERVAL > 0 ? startNe + NE_INTERVAL
						: startNe + countNe;
				// 一次处理N个Ne
				List<Map> partNe = nodeList.subList(startNe, endNe);
				// 节点条件分层级筛选至各个EMS[emsId，Map<nodeLvl,nodeIdStr>]
				Map<Integer, Map<String, Object>> emsNodeMaps = performanceManagerService
						.getConditionsFromNodesGroupByEmsIds(partNe);
				List<Map<String, Object>> tableNodesList = null;
				// 将tablename放到条件中去，和他们的ems下的node放在一起
				tableNodesList = tableName_nodeMap_timeCond(condMap,
						emsNodeMaps);
				dateList = (List<String>) tableNodesList.get(
						tableNodesList.size() - 1).get("dateList");
				tableNodesList.remove(tableNodesList.size() - 1);
				// 第一次才设置header
				if (xls.getHeader() == null || xls.getHeader().size() == 0) {
					List<List<MultiColumnMap>> header = getBitErrHeader(dateList);
					xls.setHeader(header);
				}
				if (tableNodesList.size() == 0)
					xls.writeSheet("光路误码监测表", resultMap, true);
				else {
					// 查询导出的数据
					resultMap = instantReportMapper.searchPM4BitErrReporty(
							tableNodesList, condMap, -1);
					// 处理导出的数据
					List<Map> src = PMDataUtil.combineBitErrData(resultMap);
					// 再导出到Excel
					xls.writeSheet("光路误码监测表", src, true);
					// MultiHearderExcelUtil.writeFile(src);
				}
				startNe += NE_INTERVAL;
				countNe -= NE_INTERVAL;
			}
			// 导出数据完毕后还需写最下面几行
			List<List<MultiColumnMap>> footer = getBitErrFooter(dateList,
					sysUserId);
			xls.setAddBottomCols(footer);
			xls.writeFooter();
//			MultiHearderExcelUtil.closeFile();
			xls.close();
			// ExportResult result = xls.getResult();
			return filePath.replace('\\', '/');
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}

	// 将表名结合进node条件里
	private List<Map<String, Object>> tableName_nodeMap_timeCond(
			Map<String, String> condMap,
			Map<Integer, Map<String, Object>> emsNodeMaps) {
		List<Map<String, Object>> returnResult = new ArrayList<Map<String, Object>>();
		try {
			
			SimpleDateFormat sdf = new SimpleDateFormat(
					CommonDefine.COMMON_SIMPLE_FORMAT);
			if ("1".equals(condMap.get("reportType")))
				sdf.applyPattern("yyyy-MM");
				
			Calendar startDate = Calendar.getInstance();
			Calendar endDate = Calendar.getInstance();
			startDate.setTime(sdf.parse(condMap.get("start")));
			endDate.setTime(sdf.parse(condMap.get("end")));
			List<String> dayList = new ArrayList<String>();
			sdf.applyPattern(CommonDefine.COMMON_SIMPLE_FORMAT);
			if ("0".equals(condMap.get("reportType"))) {// 光路误码监测记录表
				// 获取需要取数据的日期
				endDate.add(Calendar.DAY_OF_MONTH, 1);
				while (startDate.before(endDate)) {
					String day = sdf.format(startDate.getTime());
					dayList.add(day);
					startDate.add(Calendar.DAY_OF_YEAR,
							Integer.parseInt(condMap.get("interval")) + 1);
				}
				// 恢复
				startDate.setTime(sdf.parse(condMap.get("start")));
				endDate.setTime(sdf.parse(condMap.get("end")));
			}
			for (Iterator<Integer> it = emsNodeMaps.keySet().iterator(); it
					.hasNext();) {
				Integer key = it.next();
				Map<String, Object> nodeMap = emsNodeMaps.get(key);
				endDate.add(Calendar.MONTH, 1);

				while (startDate.get(Calendar.MONTH) != endDate
						.get(Calendar.MONTH)) {
					Map<String, Object> e = new HashMap<String, Object>();
					e.putAll(nodeMap);
					sdf.applyPattern("yyyy_MM");
					String tableName = CommonDefine.PM.PM_TABLE_NAMES.ORIGINAL_DATA
							+ "_"
							+ key.toString()
							+ "_"
							+ sdf.format(startDate.getTime());
					// 检查表名是否存在
					Integer existance = performanceManagerMapper
							.getPmTableExistance(tableName,
									SpringContextUtil.getDataBaseParam(CommonDefine.DB_SID));
					e.put("tableName", tableName);
					if("1".equals(condMap.get("reportType"))){
						sdf.applyPattern("yyyy-MM");
						dayList.add(sdf.format(startDate.getTime()));
					}
					if ("0".equals(condMap.get("reportType"))) {
						e.put("timeCond", dayList);
					} 
					startDate.add(Calendar.MONTH, 1);
					if (existance != 1) {
						continue;
					}
					returnResult.add(e);
				}
			}
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("dateList", dayList);
			returnResult.add(m);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return returnResult;
	}

	@Override
	public int neCountCheck(List<Map> nodeList) throws CommonException {
		Map<String, String> conditionMap = performanceManagerService
				.nodeListClassify(nodeList);
		try {
			int count = performanceManagerMapper.getCountOfNeUnderThisNode(
					conditionMap, RegularDefine);
			if (count > 500)
				return 0;
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
		return 1;
	}

	/**
	 * 为误码报表生成表头
	 * 
	 *	+--------------+-----------------------------------------------------+
	 *	|              |                    11月4日                                                                      |                               	 |
	 *	|              |--------------------------+--------------------------+
	 *	|              |          B1              |             B2           |
	 *	+------+-------+------+------+-----+------+-----+------+------+------+
	 *	| 系统   | 端口      |  ES  | SES  | UAS | BBE  | ES  | SES  | UAS  | BBE  |
	 *	+------+-------+------+------+-----+------+-----+------+------+------+
	 * 
	 * @param dateList
	 *            日期列表
	 * @return
	 */
	public List<List<MultiColumnMap>> getBitErrHeader(List<String> dateList) {
		List<List<MultiColumnMap>> multiHearder = new ArrayList<List<MultiColumnMap>>();
		if (dateList.size() > 0) {
			// 大标题
			List<MultiColumnMap> bitErrHeaderTitle = new ArrayList<MultiColumnMap>();
			bitErrHeaderTitle.add(new MultiColumnMap("title", "光路误码监测表",
					dateList.size() * 8 + 2, 1));
			multiHearder.add(bitErrHeaderTitle);
			// 日期行
			List<MultiColumnMap> bitErrHeaderSubTitle = new ArrayList<MultiColumnMap>();
			bitErrHeaderSubTitle.add(new MultiColumnMap("subtitle", dateList
					.get(0) + "至" + dateList.get(dateList.size() - 1), dateList
					.size() * 8 + 2, 1));
			multiHearder.add(bitErrHeaderSubTitle);
			// 第一层
			List<MultiColumnMap> bitErrHeaderF1 = new ArrayList<MultiColumnMap>();
			bitErrHeaderF1.add(new MultiColumnMap("", "", 2, 2));

			// 第二层

			List<MultiColumnMap> bitErrHeaderF2 = new ArrayList<MultiColumnMap>();
			bitErrHeaderF2.add(new MultiColumnMap("", "", 0, 0));
			bitErrHeaderF2.add(new MultiColumnMap("", "", 0, 0));

			// 第三层
			// 如果是合并行，可以在key中用%附加上参考合并信息
			List<MultiColumnMap> bitErrHeaderF3 = new ArrayList<MultiColumnMap>();
			bitErrHeaderF3.add(new MultiColumnMap("ne", "系统", 1, 1, 18,true));
			bitErrHeaderF3
					.add(new MultiColumnMap("portDesc", "端口", 1, 1, 30,false));

			for (int fIndex = 0; fIndex < dateList.size(); fIndex++) {
				// 处理第一层的
				MultiColumnMap columnF1 = new MultiColumnMap(
						dateList.get(fIndex), dateList.get(fIndex), 8, 1);
				bitErrHeaderF1.add(columnF1);
				// 处理第二层
				for (int i = 0; i < BITERR_HEARDER_MID.length; i++) {
					MultiColumnMap columnF2 = new MultiColumnMap(
							BITERR_HEARDER_MID_PREFIX[i],
							BITERR_HEARDER_MID[i], 4, 1);
					bitErrHeaderF2.add(columnF2);

					// 处理第三层
					for (int k = 0; k < BITERR_HEARDER_BOT.length; k++) {
						MultiColumnMap columnF3 = new MultiColumnMap(
								dateList.get(fIndex)
										+ BITERR_HEARDER_MID_PREFIX[i]
										+ BITERR_HEARDER_BOT[k],
								BITERR_HEARDER_BOT[k], 1, 1);
						bitErrHeaderF3.add(columnF3);
					}
				}

			}
			multiHearder.add(bitErrHeaderF1);
			multiHearder.add(bitErrHeaderF2);
			multiHearder.add(bitErrHeaderF3);
		}

		return multiHearder;
	}

	/**
	 * 生成最下面几行
	 * 
	 * @param dateList
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public List<List<MultiColumnMap>> getBitErrFooter(List<String> dateList,
			Integer userId) {
		List<List<MultiColumnMap>> multiFooter = new ArrayList<List<MultiColumnMap>>();
		if (dateList.size() > 0) {
			String userName = performanceManagerMapper.getUserNameById(userId);
			List<MultiColumnMap> recordTime = new ArrayList<MultiColumnMap>();
			List<MultiColumnMap> recordPerson = new ArrayList<MultiColumnMap>();
			List<MultiColumnMap> verifyPerson = new ArrayList<MultiColumnMap>();
			List<MultiColumnMap> comment = new ArrayList<MultiColumnMap>();
			recordTime.add(new MultiColumnMap("", "记录时间", 2, 1));
			recordPerson.add(new MultiColumnMap("", "记录人", 2, 1));
			verifyPerson.add(new MultiColumnMap("", "审核人", 2, 1));
			comment.add(new MultiColumnMap("", "备注", 2, 1));
			String time = SDF_COMMON.format(new Date()).substring(11, 16);
			for (int i = 0; i < dateList.size(); i++) {
				recordTime.add(new MultiColumnMap("", time, 8, 1));
				recordPerson.add(new MultiColumnMap("", userName, 8, 1));
				verifyPerson.add(new MultiColumnMap("", "", 8, 1));
				comment.add(new MultiColumnMap("", "", 8, 1));
			}
			multiFooter.add(recordTime);
			multiFooter.add(recordPerson);
			multiFooter.add(verifyPerson);
			multiFooter.add(comment);
		}
		return multiFooter;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public String generateSDHLightPowerReport(Map<String, String> condMap,
			List<Map> nodeList, Integer sysUserId) throws CommonException {
		try {
			List<Map> resultMap = new ArrayList<Map>();
			condMap.put("pmStdIndex", "'TPL_MIN','RPL_MIN'");
			//如果是1日转为01日
			if(condMap.get("pmDate").length()==1)
				condMap.put("pmDate", "0"+condMap.get("pmDate"));
			// tplevel条件处理
//			condMap.get("tpLevel").replaceAll(", ", "','");
			condMap.put("tpLevel",
					"'" + condMap.get("tpLevel").replaceAll(", ", "','") + "'");
			// 转换为[nodeId，nodeLevel，emsId]格式的数据
			Map<String, String> conditionMap = performanceManagerService
					.nodeListClassify(nodeList);
			//只要SDH的
			conditionMap.put("neType", String.valueOf(CommonDefine.NE_TYPE_SDH_FLAG));
			// 换算为NE
			nodeList = instantReportMapper.getNeUnderThisNode(conditionMap,
					RegularDefine, TREE_DEFINE);
			// filepath
			String fileName = condMap.get("taskName") + "("
					+ condMap.get("start") + "到" + condMap.get("end") + "_每月"
					+ condMap.get("pmDate") + "日)_即时报表_"
					+ getTodayString(CommonDefine.COMMON_SIMPLE_FORMAT)
					+ ".xlsx";
			String filePath = CommonDefine.PATH_ROOT
					+ CommonDefine.EXCEL.INSTANT_REPORT_DIR + "\\" + fileName;
			MultiHearderExcelUtil xls = new MultiHearderExcelUtil(filePath);
			xls.setReplaceEmpty("-");
			int startNe = 0, countNe = nodeList.size(), endNe;
			// MultiHearderExcelUtil.openFile("C://合并区域记录1.txt");
			List<String> dateList = null;
			while (countNe > 0) {
				// 判断一下到不到了
				endNe = countNe - NE_INTERVAL > 0 ? startNe + NE_INTERVAL
						: startNe + countNe;
				// 一次处理N个Ne
				List<Map> partNe = nodeList.subList(startNe, endNe);
				// 节点条件分层级筛选至各个EMS[emsId，Map<nodeLvl,nodeIdStr>]
				Map<Integer, Map<String, Object>> emsNodeMaps = performanceManagerService
						.getConditionsFromNodesGroupByEmsIds(partNe);
				List<Map<String, Object>> tableNodesList = null;
				// 将tableName放到条件中去，和他们的ems下的node放在一起
				tableNodesList = tableName_nodeMap_timeCond(condMap,
						emsNodeMaps);
				dateList = (List<String>) tableNodesList.get(
						tableNodesList.size() - 1).get("dateList");
				tableNodesList.remove(tableNodesList.size() - 1);
				// 第一次才设置header
				if (xls.getHeader() == null || xls.getHeader().size() == 0) {
					List<List<MultiColumnMap>> header = getSDHLightPowerHeader(dateList);
					xls.setHeader(header);
				}
				if (tableNodesList.size() == 0)
					xls.writeSheet("SDH发送、接收光功率记录表", resultMap, true);
				else {
					// 查询导出的数据
					resultMap = instantReportMapper.generateSDHLightPowerReport(
							tableNodesList, condMap);
					// 处理导出的数据
					List<Map> src = PMDataUtil.combineSDHLightPowerData(resultMap);
					// 再导出到Excel
					xls.writeSheet("SDH发送、接收光功率记录表", src, true);
					// MultiHearderExcelUtil.writeFile(src);
				}
				startNe += NE_INTERVAL;
				countNe -= NE_INTERVAL;
			}
			// 导出数据完毕后还需写最下面几行
			List<List<MultiColumnMap>> footer = getLightPowerFooter(dateList,
					sysUserId);
			xls.setAddBottomCols(footer);
			xls.writeFooter();
//			MultiHearderExcelUtil.closeFile();
			xls.close();
			// ExportResult result = xls.getResult();
			return filePath.replace('\\', '/');
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PM_DB_ERROR);
		}
	}
	
	/**
	 * 为误码报表生成表头
	 * 
	 *	+--------------+-----------------------------------------------------+
	 *	|              |                    1月                                                                                |                               	 |
	 *	|              |--------------------------+--------------------------+
	 *	|              |         发送光功率                    |            接收光功率             |
	 *	+------+-------+-------------+------------+------------+-------------+
	 *	| 系统   | 端口      |  初始值               | 测试值              |   初始值         | 测试值                 |
	 *	+------+-------+-------------+------------+------------+-------------+
	 * 
	 * @param dateList
	 *            日期列表
	 * @return
	 */
	public List<List<MultiColumnMap>> getSDHLightPowerHeader(List<String> dateList) {
		List<List<MultiColumnMap>> multiHearder = new ArrayList<List<MultiColumnMap>>();
		if (dateList.size() > 0) {
			// 大标题
			List<MultiColumnMap> lightPowerHeaderTitle = new ArrayList<MultiColumnMap>();
			lightPowerHeaderTitle.add(new MultiColumnMap("title", "SDH发送、接收光功率记录表",
					dateList.size() * 4 + 2, 1));
			multiHearder.add(lightPowerHeaderTitle);
			// 日期行
//			List<MultiColumnMap> bitErrHeaderSubTitle = new ArrayList<MultiColumnMap>();
//			bitErrHeaderSubTitle.add(new MultiColumnMap("subtitle", dateList
//					.get(0) + "至" + dateList.get(dateList.size() - 1), dateList
//					.size() * 8 + 2, 1));
//			multiHearder.add(bitErrHeaderSubTitle);
			// 第一层
			List<MultiColumnMap> lightPowerHeaderF1 = new ArrayList<MultiColumnMap>();
			lightPowerHeaderF1.add(new MultiColumnMap("", "", 2, 2));

			// 第二层

			List<MultiColumnMap> ligthPowerHeaderF2 = new ArrayList<MultiColumnMap>();
			ligthPowerHeaderF2.add(new MultiColumnMap("", "", 0, 0));
			ligthPowerHeaderF2.add(new MultiColumnMap("", "", 0, 0));

			// 第三层
			// 如果是合并行，可以在key中用%附加上参考合并信息
			List<MultiColumnMap> lightPowerHeaderF3 = new ArrayList<MultiColumnMap>();
			lightPowerHeaderF3.add(new MultiColumnMap("ne", "系统", 1, 1,18, true));
			lightPowerHeaderF3
					.add(new MultiColumnMap("portDesc", "端口", 1, 1, 30,false));

			for (int fIndex = 0; fIndex < dateList.size(); fIndex++) {
				// 处理第一层的
				MultiColumnMap columnF1 = new MultiColumnMap(
						dateList.get(fIndex), dateList.get(fIndex), 4, 1);
				lightPowerHeaderF1.add(columnF1);
				// 处理第二层
				for (int i = 0; i < LP_HEARDER_MID.length; i++) {
					MultiColumnMap columnF2 = new MultiColumnMap(
							LP_HEARDER_PREFIX[i],
							LP_HEARDER_MID[i], 2, 1);
					ligthPowerHeaderF2.add(columnF2);

					// 处理第三层
					for (int k = 0; k < LP_HEARDER_BOT.length; k++) {
						MultiColumnMap columnF3 = new MultiColumnMap(
								LP_HEARDER_PREFIX[i]
								+ dateList.get(fIndex) + LP_HEARDER_FIX[i],
								LP_HEARDER_BOT[k], 1, 1);
						lightPowerHeaderF3.add(columnF3);
					}
				}

			}
			multiHearder.add(lightPowerHeaderF1);
			multiHearder.add(ligthPowerHeaderF2);
			multiHearder.add(lightPowerHeaderF3);
		}

		return multiHearder;
	}
	
	/**
	 * 生成最下面几行
	 * 
	 * @param dateList
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public List<List<MultiColumnMap>> getLightPowerFooter(List<String> dateList,
			Integer userId) {
		List<List<MultiColumnMap>> multiFooter = new ArrayList<List<MultiColumnMap>>();
		if (dateList.size() > 0) {
			String userName = performanceManagerMapper.getUserNameById(userId);
			List<MultiColumnMap> recordTime = new ArrayList<MultiColumnMap>();
			List<MultiColumnMap> recordPerson = new ArrayList<MultiColumnMap>();
			List<MultiColumnMap> verifyPerson = new ArrayList<MultiColumnMap>();
			recordTime.add(new MultiColumnMap("", "测试日期", 2, 1));
			recordPerson.add(new MultiColumnMap("", "测试人", 2, 1));
			verifyPerson.add(new MultiColumnMap("", "审核人/日期", 2, 1));
			String time = SDF_COMMON.format(new Date()).substring(5, 10);
			for (int i = 0; i < dateList.size(); i++) {
				recordTime.add(new MultiColumnMap("", time, 4, 1));
				recordPerson.add(new MultiColumnMap("", userName, 4, 1));
				verifyPerson.add(new MultiColumnMap("", "", 4, 1));
			}
			multiFooter.add(recordTime);
			multiFooter.add(recordPerson);
			multiFooter.add(verifyPerson);
		}
		return multiFooter;
	}
}
