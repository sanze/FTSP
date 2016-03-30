package com.fujitsu.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xuxiaojun
 * 
 */
public class CommonDefine extends BaseDefine{
	
	//下拉框显示all
	public final static int VALUE_ALL = -99;
	//下拉框显示无
	public final static int VALUE_NONE = -1;
	public final static int USER_ADMIN_ID = -1;//系统级用户ID,用于绕过权限验证
	/** ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ 共通树部分 ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ **/
	public static class TREE {
		public static final int CHILD_MAX = 5000;
		public static final int ROOT_ID = 0;
		public static final String ROOT_TEXT = "FTSP";
		public static final String CHECKED_ALL = "all";
		public static final String CHECKED_PART = "part";
		public static final String CHECKED_NONE = "none";

		// 节点包含信息
		public static final String PROPERTY_NODE_ID = "nodeId";
		public static final String PROPERTY_NODE_LEVEL = "nodeLevel";
		public static final String PROPERTY_TEXT = "text";

		public static Map<String, Object> TREE_DEFINE = new HashMap<String, Object>();
		static {
			TREE_DEFINE.put("FALSE", CommonDefine.FALSE);
			TREE_DEFINE.put("CHILD_MAX", CommonDefine.TREE.CHILD_MAX);
			TREE_DEFINE.put("ROOT_ID", CommonDefine.TREE.ROOT_ID);
			TREE_DEFINE.put("ROOT_TEXT", CommonDefine.TREE.ROOT_TEXT);
			TREE_DEFINE.put("NODE_ROOT", CommonDefine.TREE.NODE.ROOT);
			TREE_DEFINE.put("NODE_EMSGROUP", CommonDefine.TREE.NODE.EMSGROUP);
			TREE_DEFINE.put("NODE_EMS", CommonDefine.TREE.NODE.EMS);
			TREE_DEFINE.put("NODE_SUBNET", CommonDefine.TREE.NODE.SUBNET);
			TREE_DEFINE.put("NODE_NE", CommonDefine.TREE.NODE.NE);
			TREE_DEFINE.put("NODE_SHELF", CommonDefine.TREE.NODE.SHELF);
			TREE_DEFINE.put("NODE_UNIT", CommonDefine.TREE.NODE.UNIT);
			TREE_DEFINE.put("NODE_SUBUNIT", CommonDefine.TREE.NODE.SUBUNIT);
			TREE_DEFINE.put("NODE_PTP", CommonDefine.TREE.NODE.PTP);
			TREE_DEFINE.put("USER_ADMIN_ID", CommonDefine.USER_ADMIN_ID);
			TREE_DEFINE.put("VALUE_ALL", CommonDefine.VALUE_ALL);
			TREE_DEFINE.put("VALUE_NONE", CommonDefine.VALUE_NONE);
			TREE_DEFINE.put("AUTH_ALL", CommonDefine.TRUE);
			TREE_DEFINE.put("AUTH_VIEW", CommonDefine.FALSE);
//			TREE_DEFINE.put("DISPLAY_MODE_NONE", RESOURCE_STOCK.NONE_MODE);
		}
		public static class NODE {
			public static final int ROOT = 0;
			public static final int EMSGROUP = 1;
			public static final int EMS = 2;
			public static final int SUBNET = 3;
			public static final int NE = 4;
			public static final int SHELF = 5;
			public static final int UNIT = 6;
			public static final int SUBUNIT = 7;
			public static final int PTP = 8;
			public static final int LEAFMAX = 8;
		}
		public static String[] LEVEL_NAME = { "FTSP", "", "", "", "","","","","","","",
			FieldNameDefine.STATION_NAME, "机房" };
	}

}
