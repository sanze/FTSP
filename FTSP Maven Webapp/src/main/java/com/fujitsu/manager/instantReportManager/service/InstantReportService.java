package com.fujitsu.manager.instantReportManager.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fujitsu.IService.IInstantReportService;
import com.fujitsu.abstractService.AbstractService;
import com.fujitsu.common.CommonDefine;

public abstract class InstantReportService extends AbstractService implements
		IInstantReportService {

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

	public static Map<String, Object> RegularDefine = new HashMap<String, Object>();
	static {
		RegularDefine.put("FALSE", CommonDefine.FALSE);
		RegularDefine.put("TRUE", CommonDefine.TRUE);
	}

	// //测试用header
	// public static MultiColumnMap[] BITERR_HEARDER_F2 = {
	// new MultiColumnMap("id","ID", 0),
	// new MultiColumnMap("prop1","其┐", 1),
	// new MultiColumnMap("prop2","实的", 1),
	// new MultiColumnMap("prop3","我萌", 2),
	// new MultiColumnMap("prop4","是卖", 3),
	// new MultiColumnMap("prop5","来来",0),
	// new MultiColumnMap("prop6","卖是",0),
	// new MultiColumnMap("prop7","萌我",0),
	// new MultiColumnMap("prop8","的实",0),
	// new MultiColumnMap("prop9","└其",0)
	// };


	public static String[] BITERR_HEARDER_MID = {"B1","B2"};
	public static String[] BITERR_HEARDER_MID_PREFIX = {"RS_","MS_"};
	public static String[] BITERR_HEARDER_BOT = {"ES","SES","UAS","BBE"};
	
	public static String[] LP_HEARDER_MID = {"发送光功率","接收光功率"};
	public static String[] LP_HEARDER_BOT = {"初始值","测试值"};
	public static String[] LP_HEARDER_PREFIX = {"COMP_",""};
	public static String[] LP_HEARDER_FIX = {"TPL_MIN","RPL_MIN"};
	
	public static SimpleDateFormat SDF_COMMON = new SimpleDateFormat(CommonDefine.COMMON_FORMAT);
	public static int NE_INTERVAL = 50;
	
	public static String getTodayString(String formatPattern){
		String returnResult = "";
		try{
		SimpleDateFormat sdf = new SimpleDateFormat(formatPattern);
		Date today = new Date();
		returnResult = sdf.format(today);
		}catch(Exception e){
			e.printStackTrace();
		}
		return returnResult;
	}
}

