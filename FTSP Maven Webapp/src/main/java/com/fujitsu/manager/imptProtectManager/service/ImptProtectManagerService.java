package com.fujitsu.manager.imptProtectManager.service;

import java.util.HashMap;
import java.util.Map;

import com.fujitsu.IService.IImptProtectManagerService;
import com.fujitsu.common.CommonDefine;

public abstract class ImptProtectManagerService implements IImptProtectManagerService{
	private static int searchTag = 20000;

	synchronized protected int getSearchTag() {
		if (searchTag < 29999) {
			searchTag++;
		} else {
			searchTag = 20000;
		}
		return searchTag;
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

}
