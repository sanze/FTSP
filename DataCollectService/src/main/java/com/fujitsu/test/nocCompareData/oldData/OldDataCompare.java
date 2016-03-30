package com.fujitsu.test.nocCompareData.oldData;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;


/**
 * @author xuxiaojun
 *
 */
public interface OldDataCompare {

	public List selectNeList_OLA(
			@Param(value = "emsConnectionId") int emsConnectionId,
			@Param(value = "neModel") String neModel);
	
	public Map selectTableById(
			@Param(value = "tableName") String tableName,
			@Param(value = "idName") String idName,
			@Param(value = "id") int id);
	
	public List selectTableListById(
			@Param(value = "tableName") String tableName,
			@Param(value = "idName") String idName,
			@Param(value = "id") int id);
	
	public void updateNeById(Map ne);
	
	public void updateShelfById(Map shelf);
	
	public void updateSlotById(Map slot);

	public void updateEquipById(Map equip);
	
	public void updatePtpById(Map ptp);
	
	public void insertNode(Map node);
	
	public void insertNodeBatch(List<Map> nodes);
	
}
