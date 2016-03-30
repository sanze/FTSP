package com.fujitsu.manager.performanceManager.service;

import java.util.HashMap;
import java.util.Map;

import com.fujitsu.IService.IPerformanceManagerService;
import com.fujitsu.abstractService.AbstractService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.FieldNameDefine;
import com.fujitsu.common.poi.ColumnMap;

public abstract class PerformanceManagerService extends AbstractService
		implements IPerformanceManagerService {
	public static Map<String, Object> RegularPmAnalysisDefine = new HashMap<String, Object>();
	static {
		RegularPmAnalysisDefine.put("EMS", CommonDefine.PM.TARGET_TYPE.EMS);
		RegularPmAnalysisDefine.put("PM_TASK_TYPE", CommonDefine.PM.PM_TASK_TYPE);
		RegularPmAnalysisDefine.put("FALSE", CommonDefine.FALSE);
		RegularPmAnalysisDefine.put("TRUE", CommonDefine.TRUE);
	}

	protected static Map<String, Object> TREE_DEFINE = new HashMap<String, Object>();
	static {
		TREE_DEFINE.put("NODE_ROOT", CommonDefine.TREE.NODE.ROOT);
		TREE_DEFINE.put("NODE_EMSGROUP", CommonDefine.TREE.NODE.EMSGROUP);
		TREE_DEFINE.put("NODE_EMS", CommonDefine.TREE.NODE.EMS);
		TREE_DEFINE.put("NODE_SUBNET", CommonDefine.TREE.NODE.SUBNET);
		TREE_DEFINE.put("NODE_NE", CommonDefine.TREE.NODE.NE);
		TREE_DEFINE.put("NODE_SHELF", CommonDefine.TREE.NODE.SHELF);
		TREE_DEFINE.put("NODE_UNIT", CommonDefine.TREE.NODE.UNIT);
		TREE_DEFINE.put("NODE_SUBUNIT", CommonDefine.TREE.NODE.SUBUNIT);
		TREE_DEFINE.put("NODE_PTP", CommonDefine.TREE.NODE.PTP);
	}

	protected static Map<Integer, String> DOMAIN_DEFINE = new HashMap<Integer, String>();
	static {
		DOMAIN_DEFINE.put(CommonDefine.PM.DOMAIN.DOMAIN_SDH_FLAG, "SDH");
		DOMAIN_DEFINE.put(CommonDefine.PM.DOMAIN.DOMAIN_WDM_FLAG, "WDM");
		DOMAIN_DEFINE.put(CommonDefine.PM.DOMAIN.DOMAIN_ETH_FLAG, "ETH");
		DOMAIN_DEFINE.put(CommonDefine.PM.DOMAIN.DOMAIN_ATM_FLAG, "ATM");
	}

	protected static Map<String, Object> REPORT_DEFINE = new HashMap<String, Object>();
	static {
		REPORT_DEFINE.put("FALSE", CommonDefine.FALSE);
		REPORT_DEFINE.put("TRUE", CommonDefine.TRUE);
		REPORT_DEFINE.put("NE_REPORT", CommonDefine.QUARTZ.JOB_REPORT_NE);
		REPORT_DEFINE.put("MS_REPORT", CommonDefine.QUARTZ.JOB_REPORT_MS);
		REPORT_DEFINE.put("NODE_TL", CommonDefine.TASK_TARGET_TYPE.TRUNK_LINE);
		REPORT_DEFINE.put("NODE_MS", CommonDefine.TASK_TARGET_TYPE.MULTI_SEC);
	}

	private static int searchTag = 0;

	synchronized protected int getSearchTag() {
		if (searchTag < 9999) {
			searchTag++;
		} else {
			searchTag = 1;
		}
		return searchTag;
	}

	protected static String[] PM_REPORT_NE_DETAIL_HEADER = { "emsGroup", "ems",
			"ne", "neType", "portDesc", "ctp", "pmDesc", "retrievalTime",
			"ptpId", "location", "pmValue", "exceptionLv", "ctpId", "domain",
			"pmStdIndex", "emsId", "targetType","pmType","unitId","neId",
			//2014-8-12 改善项要求添加
			"DISPLAY_SUBNET",
			"DISPLAY_AREA",
			"DISPLAY_STATION",
			"DISPLAY_PRODUCT_NAME",
			"PTP_TYPE",
			"RATE",
			"UNIT",
			"PM_COMPARE_VALUE_DISPLAY",
			"PM_COMPARE_VALUE",
			"EXCEPTION_COUNT",
			"THRESHOLD_1",
			"THRESHOLD_2",
			"THRESHOLD_3",
			"FILTER_VALUE",
			"OFFSET",
			"UPPER_VALUE",
			"UPPER_OFFSET",
			"LOWER_VALUE",
			"LOWER_OFFSET",
//			"DISPLAY_TEMPLATE_NAME",
			"GRANULARITY"};

	protected static String[] PM_REPORT_MS_DETAIL_HEADER = { "MSId",
			"emsGroup", "ems", "emsType", "TL", "MS", "direction",
			"standardWave", "actualWave", "MSStatus", "retrievalTime" };

	//报表查询页面-导出类型
	protected static int ANALYSIS_EXPORT_TYPE_NE = 1;
	protected static int ANALYSIS_EXPORT_TYPE_MS = 2;
	protected static int ANALYSIS_EXPORT_TYPE_CFMS = 3;
	protected static int ANALYSIS_EXPORT_TYPE_CFPTP = 4;
	protected static int ANALYSIS_EXPORT_TYPE_CFNE = 5;

	//复用段状态‘
	protected static int MS_STATUS_NORMAL = 0;
	protected static int MS_STATUS_LV1 = 1;
	protected static int MS_STATUS_LV2 = 2;
	protected static int MS_STATUS_LV3 = 3;
	protected static int MS_STATUS_INCOMPLETE = 4;
	//NE日报Csv文件Header
	protected static ColumnMap[] neCsvSrcHeader = {
		new ColumnMap("emsGroup", "网管分组", CommonDefine.PM.CUSTOM_REPORT.COMBO_AUTO, 12),
		new ColumnMap("ems", "网管", CommonDefine.PM.CUSTOM_REPORT.COMBO_AUTO, 12),
		new ColumnMap("ne", "网元", CommonDefine.PM.CUSTOM_REPORT.COMBO_AUTO, 12),
		new ColumnMap("neType", "网元类型", CommonDefine.PM.CUSTOM_REPORT.COMBO_AUTO, 8),
		new ColumnMap("portDesc", "端口", CommonDefine.PM.CUSTOM_REPORT.COMBO_KEY, 12),
		new ColumnMap("ctp", "通道", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 15),
		new ColumnMap("pmDesc", "性能事件", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 16),
		new ColumnMap("retrievalTime", "采集时间", CommonDefine.PM.CUSTOM_REPORT.COMBO_DATE, 12),
		new ColumnMap("ptpId", "PTP ID", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("location", "方向", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("pmValue", "性能值", CommonDefine.PM.CUSTOM_REPORT.COMBO_VALUE, 8),
		new ColumnMap("exceptionLv", "异常等级", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("ctpId", "CTP ID", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("domain", "业务类型", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("pmStdIndex", "PM_STD_INDEX", CommonDefine.PM.CUSTOM_REPORT.COMBO_SUBKEY, 8),
		new ColumnMap("emsId", "网管ID", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("targetType", "目标类型", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("pmType", "PM类型", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("unitId", "板卡ID", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("neId", "网元ID", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("DISPLAY_SUBNET", "子网", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("DISPLAY_AREA", FieldNameDefine.AREA_NAME, CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("DISPLAY_STATION", FieldNameDefine.STATION_NAME, CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("DISPLAY_PRODUCT_NAME", "型号", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("PTP_TYPE", "端口类型", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("RATE", "速率", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("UNIT", "单位", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("PM_COMPARE_VALUE_DISPLAY", "性能比较值", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("PM_COMPARE_VALUE", "性能基准值", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("EXCEPTION_COUNT", "连续异常", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("THRESHOLD_1", "计数值阈值1", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("THRESHOLD_2", "计数值阈值2", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("THRESHOLD_3", "计数值阈值3", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("FILTER_VALUE", "计数值过滤值", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("OFFSET", "物理量基准值偏差", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("UPPER_VALUE", "物理量上限值", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("UPPER_OFFSET", "物理量上限值偏差", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("LOWER_VALUE", "物理量下限值", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("LOWER_OFFSET", "物理量下限值偏差", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
//		new ColumnMap("DISPLAY_TEMPLATE_NAME", "性能模板", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("GRANULARITY", "周期", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8)
	};
	//baseHeader不应该带有日期、性能值字段
	protected static ColumnMap[] neCsvBaseHeader = {
		new ColumnMap("emsGroup", "网管分组", CommonDefine.PM.CUSTOM_REPORT.COMBO_AUTO, 12),
		new ColumnMap("ems", "网管", CommonDefine.PM.CUSTOM_REPORT.COMBO_AUTO, 12),
		new ColumnMap("ne", "网元", CommonDefine.PM.CUSTOM_REPORT.COMBO_AUTO, 12),
		new ColumnMap("neType", "网元类型", CommonDefine.PM.CUSTOM_REPORT.COMBO_AUTO, 8),
		new ColumnMap("portDesc", "端口", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 12),
		new ColumnMap("ctp", "通道", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 15),
		new ColumnMap("pmDesc", "性能事件", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 16),
		new ColumnMap("ptpId", "PTP ID", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("location", "方向", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("exceptionLv", "异常等级", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("ctpId", "CTP ID", CommonDefine.PM.CUSTOM_REPORT.COMBO_SUBKEY, 8),
		new ColumnMap("domain", "业务类型", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("pmStdIndex", "PM_STD_INDEX", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("emsId", "网管ID", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("targetType", "目标类型", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("pmType", "PM类型", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("DISPLAY_SUBNET", "子网", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("DISPLAY_AREA", FieldNameDefine.AREA_NAME, CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("DISPLAY_STATION", FieldNameDefine.STATION_NAME, CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("DISPLAY_PRODUCT_NAME", "型号", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("PTP_TYPE", "端口类型", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("RATE", "速率", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("UNIT", "单位", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("PM_COMPARE_VALUE_DISPLAY", "性能比较值", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("PM_COMPARE_VALUE", "性能基准值", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("EXCEPTION_COUNT", "连续异常", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("THRESHOLD_1", "计数值阈值1", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("THRESHOLD_2", "计数值阈值2", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("THRESHOLD_3", "计数值阈值3", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("FILTER_VALUE", "计数值过滤值", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("OFFSET", "物理量基准值偏差", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("UPPER_VALUE", "物理量上限值", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("UPPER_OFFSET", "物理量上限值偏差", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("LOWER_VALUE", "物理量下限值", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("LOWER_OFFSET", "物理量下限值偏差", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("DISPLAY_TEMPLATE_NAME", "性能模板", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("GRANULARITY", "周期", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
	};
	//NE日报Xls文件Header
	protected static ColumnMap[] neXlsSrcHeader = {
		new ColumnMap("emsGroup", "网管分组", CommonDefine.PM.CUSTOM_REPORT.COMBO_AUTO, 12),
		new ColumnMap("ems", "网管", CommonDefine.PM.CUSTOM_REPORT.COMBO_AUTO, 12),
		new ColumnMap("ne", "网元", CommonDefine.PM.CUSTOM_REPORT.COMBO_AUTO, 12),
		new ColumnMap("neType", "网元类型", CommonDefine.PM.CUSTOM_REPORT.COMBO_AUTO, 8),
		new ColumnMap("portDesc", "端口", CommonDefine.PM.CUSTOM_REPORT.COMBO_KEY, 12),
		new ColumnMap("ctp", "时隙", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8),
		new ColumnMap("pmDesc", "性能事件", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 16),
		new ColumnMap("retrievalTime", "采集时间", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 12),
		new ColumnMap("pmValue", "性能值", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 8)
	};
	protected static ColumnMap[] neXlsBaseHeader = {
		new ColumnMap("emsGroup", "网管分组", CommonDefine.PM.CUSTOM_REPORT.COMBO_AUTO, 12),
		new ColumnMap("ems", "网管", CommonDefine.PM.CUSTOM_REPORT.COMBO_AUTO, 12),
		new ColumnMap("ne", "网元", CommonDefine.PM.CUSTOM_REPORT.COMBO_AUTO, 20),
		new ColumnMap("neType", "网元类型", CommonDefine.PM.CUSTOM_REPORT.COMBO_AUTO, 8),
		new ColumnMap("portDesc", "端口", CommonDefine.PM.CUSTOM_REPORT.COMBO_KEY, 40),
		new ColumnMap("ctp", "时隙", CommonDefine.PM.CUSTOM_REPORT.COMBO_SUBKEY, 12),
		new ColumnMap("pmDesc", "性能事件", CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 24)
	};
	
		//用于定时任务
	protected static Map<String, Object> DEFINE = new HashMap<String, Object>();
	static {
		DEFINE.put("FALSE", CommonDefine.FALSE);
		DEFINE.put("TRUE", CommonDefine.TRUE);
		DEFINE.put("NE_REPORT", CommonDefine.QUARTZ.JOB_REPORT_NE);
		DEFINE.put("MS_REPORT", CommonDefine.QUARTZ.JOB_REPORT_MS);
		DEFINE.put("NODE_TL", CommonDefine.TASK_TARGET_TYPE.TRUNK_LINE);
		DEFINE.put("NODE_MS", CommonDefine.TASK_TARGET_TYPE.MULTI_SEC);
	}
	
	public static int NE_INTERVAL = 5;
}
