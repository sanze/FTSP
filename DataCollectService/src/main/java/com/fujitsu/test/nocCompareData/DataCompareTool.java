package com.fujitsu.test.nocCompareData;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.DataCollectDefine;
import com.fujitsu.test.nocCompareData.newData.NewDataCompare;
import com.fujitsu.test.nocCompareData.oldData.OldDataCompare;
import com.fujitsu.util.BeanUtil;
import com.fujitsu.util.NameAndStringValueUtil;

public class DataCompareTool {

	public static OldDataCompare oldDataCompare = (OldDataCompare) BeanUtil
			.getBean("OldDataCompare");

	public static NewDataCompare newDataCompare = (NewDataCompare) BeanUtil
			.getBean("NewDataCompare");

	// // 旧数据库连接
	// public static SqlSessionTemplate sessionOld = (SqlSessionTemplate)
	// BeanUtil.getBean("sqlSession-191-old");
	// // 新数据库连接
	// public static SqlSessionTemplate sessionNew = (SqlSessionTemplate)
	// BeanUtil.getBean("sqlSession-191-new");
	
	public static void main(String[] args) throws CommonException {
		
		
		// 更新网元数据
		syncNe();
		// 更新shelf数据
//		syncShelf();
		// 更新slot数据
//		syncSlot();
		// 更新equip数据
//		syncEquipment();
		// 更新ptp数据
//		syncPtp();
		// 插入网元节点
//		addNeNode();
		// 插入shelf节点
//		addShelfNode();
		// 插入equip节点
//		addEquipNode();
		// 插入ptp节点
//		addPtpNode();
		

	}

	//更新网元
	private static void syncNe() {
		
		List<Map> oldNeMap = oldDataCompare.selectTableListById("T_NE", "EMS_CONNECTION_ID",2009);
		List<Map> newNeMap = newDataCompare.selectTableListById("T_NE", "EMS_CONNECTION_ID",2002);
		
		System.out.println(oldNeMap.size());
		
		System.out.println(newNeMap.size());

		int i = 0;
		
		boolean isPickUp = false;
		
		for (Map oldNe : oldNeMap) {
			
			isPickUp = false;

			for (Map newNe : newNeMap) {

				if (newNe.get("NE_DISPLAY_NAME").toString()
						.equals(oldNe.get("NE_DISPLAY_NAME").toString())) {

					isPickUp = true;
					
					oldNe.put("NE_NAME", newNe.get("NE_NAME"));
					oldNe.put("NE_TYPE", newNe.get("NE_TYPE"));
					oldNe.put("SUPORT_RATES", newNe.get("SUPORT_RATES"));

//					oldDataCompare.updateNeById(oldNe);
					i++;

					break;
				}
			}
			
			if(!isPickUp){
				System.out.println("不匹配网元：");
				System.out.println(oldNe.get("NE_ID"));
				System.out.println(oldNe.get("NE_NAME"));
				System.out.println(oldNe.get("NE_DISPLAY_NAME"));
			}
		}
		
		for (Map newNe : newNeMap) {
			
			isPickUp = false;

			for (Map oldNe : oldNeMap) {

				if (oldNe.get("NE_DISPLAY_NAME").toString()
						.equals(newNe.get("NE_DISPLAY_NAME").toString())) {

					isPickUp = true;
					
					newNe.put("NE_NAME", newNe.get("NE_NAME"));
					newNe.put("NE_TYPE", newNe.get("NE_TYPE"));
					newNe.put("SUPORT_RATES", newNe.get("SUPORT_RATES"));

//					oldDataCompare.updateNeById(oldNe);
					i++;

					break;
				}
			}
			
			if(!isPickUp){
				System.out.println("不匹配网元：");
				System.out.println(newNe.get("NE_ID"));
				System.out.println(newNe.get("NE_NAME"));
				System.out.println(newNe.get("NE_DISPLAY_NAME"));
			}
		}
		
	}

	
	//更新shelf
	private static void syncShelf(){
		
		List<Map> oldNeData = oldDataCompare.selectTableListById("T_NE", "EMS_CONNECTION_ID",2009);
		List<Map> newNeData = newDataCompare.selectTableListById("T_NE", "EMS_CONNECTION_ID",2002);

		// 更新shelf数据
		for (Map oldNe : oldNeData) {

			for (Map newNe : newNeData) {

				if (newNe.get("NE_DISPLAY_NAME").toString()
						.equals(oldNe.get("NE_DISPLAY_NAME").toString())) {

					// 更新shelf数据
					List<Map> oldData = oldDataCompare.selectTableListById("T_SHELF",
							"NE_ID",
							Integer.valueOf(oldNe.get("NE_ID").toString()));

					List<Map> newData = newDataCompare.selectTableListById("T_SHELF",
							"NE_ID",
							Integer.valueOf(newNe.get("NE_ID").toString()));
					
					if(oldData.size() != newData.size()){
						
						syncSingleNeShelf(oldData, newData);
//						System.out.println("子架不匹配网元：");
//						System.out.println(oldNe.get("NE_ID"));
//						System.out.println(oldNe.get("NE_NAME"));
//						System.out.println(oldNe.get("NE_DISPLAY_NAME"));
					}else{
						syncSingleNeShelf(oldData, newData);
					}
					break;
				}
			}
		}
	}
	
	private static void syncSingleNeShelf(List<Map> oldData, List<Map> newData) {
		
		int offsetNo = 65536;
		int oldNo = 0;
		int newNo = 0;
		
		boolean isPickUp = false;

		// OLA网元情况
		if (oldData.size() == 1&&newData.size() == 1) {

			oldData.get(0).put("SHELF_NAME", newData.get(0).get("SHELF_NAME"));
			oldData.get(0).put("SHELF_NO", newData.get(0).get("SHELF_NO"));

//			oldDataCompare.updateShelfById(oldData.get(0));
		}
		// OTM OADM网元情况
		else if (oldData.size() > 1) {

			
			for (Map oldObject : oldData) {
				
				isPickUp = false;
				
				String shelfDisplayName = oldObject.get("SHELF_DISPLAY_NAME").toString();
				
				if(shelfDisplayName.split("-").length>0){
					try{
						oldNo = Integer.valueOf(shelfDisplayName.split("-")[0]);
					}catch(Exception e){
						System.out.println("不能转化shelf：****************************************");
						System.out.println(oldObject.get("NE_ID"));
						System.out.println(oldObject.get("SHELF_ID"));
						System.out.println(oldObject.get("SHELF_NAME"));
						System.out.println(oldObject.get("SHELF_DISPLAY_NAME"));
						continue;
					}
				}else{
					System.out.println("不能转化shelf：****************************************");
					System.out.println(oldObject.get("NE_ID"));
					System.out.println(oldObject.get("SHELF_ID"));
					System.out.println(oldObject.get("SHELF_NAME"));
					System.out.println(oldObject.get("SHELF_DISPLAY_NAME"));
					continue;
				}

				for (Map newObject : newData) {
					
					newNo = Integer.valueOf(newObject.get("SHELF_NO").toString());
					
					if(newNo == offsetNo+oldNo){
						
						oldObject.put("SHELF_NAME", newObject.get("SHELF_NAME"));
						oldObject.put("SHELF_NO", newObject.get("SHELF_NO"));

//						oldDataCompare.updateShelfById(oldObject);
						
						isPickUp = true;
						
						break;
					}
				}
				
				if(!isPickUp){
//					System.out.println("不匹配shelf：&&&&&&&&&&&&&&&&&&&&&&&&&&&");
//					System.out.println(oldObject.get("NE_ID"));
//					System.out.println(oldObject.get("SHELF_ID"));
//					System.out.println(oldObject.get("SHELF_NAME"));
//					System.out.println(oldObject.get("SHELF_DISPLAY_NAME"));
				}
				
			}
		}
	}

	private static void syncSlot() {

		int i = 0;

		// 更新slot数据
		List<Map> oldData = oldDataCompare.selectTableListById("T_SLOT",
				"EMS_CONNECTION_ID", 2009);

		for (Map slot : oldData) {

			i++;

			Integer shelfId = Integer.valueOf(slot.get("SHELF_ID").toString());

			String slotName = slot.get("SLOT_NAME").toString();

			String slotNo = slot.get("SLOT_NO").toString();

			String slotDisplayName = slot.get("SLOT_DISPLAY_NAME").toString();

			Map shelf = oldDataCompare.selectTableById("T_SHELF", "SHELF_ID",
					shelfId);

			String shelfName = shelf.get("SHELF_NAME").toString();

			slotName = shelfName + "/slot=" + slotNo;

			if (slotDisplayName.isEmpty()) {
				slotDisplayName = "Slot-" + slotNo;
			}
			slot.put("SLOT_NAME", slotName);
			slot.put("SLOT_DISPLAY_NAME", slotDisplayName);
//			oldDataCompare.updateSlotById(slot);
		}
		System.out.println("共更新slot数据条目：" + i);
	}
	
	private static void syncEquipment() {
		
		NameAndStringValueUtil nameUtil = new NameAndStringValueUtil();

		int i = 0;

		// 更新equip数据
		List<Map> oldData = oldDataCompare.selectTableListById("T_EQUIP",
				"EMS_CONNECTION_ID", 2009);

		for (Map equip : oldData) {

			i++;

			Integer shelfId = Integer.valueOf(equip.get("SHELF_ID").toString());
			
			Map shelf = oldDataCompare.selectTableById("T_SHELF", "SHELF_ID",
					shelfId);
			
			String shelfNo = shelf.get("SHELF_NO").toString();

			
			String equipName = equip.get("EQUIP_NAME").toString();

			String slotNo = nameUtil.getEquipmentNoFromTargetName(equipName,
					DataCollectDefine.COMMON.SLOT);

			equipName = "EquipmentHolder:/rack=1/shelf="+shelfNo+"/slot="+slotNo;
			
			System.out.println(equipName);
			
			equip.put("EQUIP_NAME", equipName);
//			oldDataCompare.updateEquipById(equip);
		}
		System.out.println("共更新equip数据条目：" + i);
	}
	
	private static void syncPtp() {
		
		NameAndStringValueUtil nameUtil = new NameAndStringValueUtil();

		int i = 0;

		// 更新equip数据
		List<Map> oldData = oldDataCompare.selectTableListById("T_PTP",
				"EMS_CONNECTION_ID", 2009);

		for (Map ptp : oldData) {

			i++;

			Integer shelfId = Integer.valueOf(ptp.get("SHELF_ID").toString());
			
			Map shelf = oldDataCompare.selectTableById("T_SHELF", "SHELF_ID",
					shelfId);
			
			String shelfNo = shelf.get("SHELF_NO").toString();

			
			String ptpName = ptp.get("PTP_NAME").toString();

			String slotNo = nameUtil.getEquipmentNoFromTargetName(ptpName,
					DataCollectDefine.COMMON.SLOT);
			
			String domain = nameUtil.getEquipmentNoFromTargetName(ptpName,
					DataCollectDefine.HW.HW_DOMAIN);
			
			String portNo = nameUtil.getEquipmentNoFromTargetName(ptpName,
					DataCollectDefine.COMMON.PORT);

			ptpName = "PTP:/rack=1/shelf="+shelfNo+"/slot="+slotNo+"/domain="+domain+"/port="+portNo;
			
			System.out.println(ptpName);
			
			ptp.put("PTP_NAME", ptpName);
			ptp.put("SHELF_NO", shelfNo);
			oldDataCompare.updatePtpById(ptp);
		}
		System.out.println("共更新ptp数据条目：" + i);
	}
	
	private static void addNeNode(){
		List<Map> oldMap = oldDataCompare.selectTableListById("T_NE", "EMS_CONNECTION_ID",2009);
		
		Map node;
		
		List<Map> nodeList = new ArrayList<Map>();
		
		for (Map old : oldMap) {
			
			node = new HashMap();
			
			node.put("NODE_ID", null);
			node.put("EMS_CONNECTION_ID", 2009);
			node.put("NODE_NAME", old.get("NE_ID"));
			node.put("NODE_DISPLAYNAME", old.get("NE_DISPLAY_NAME"));
			node.put("NODE_TYPE", 2);
			node.put("NODE_PARENT_ID", 2009);
			node.put("NODE_PROPERTY", null);
			node.put("SYNC_STATUS", 1);
			node.put("SYNC_TIME", new Date());
			node.put("HREF", null);
			node.put("NEED_DISPALY", 0);
			node.put("IS_LEAF", 1);
			node.put("POSITION_X", null);
			node.put("POSITION_Y", null);

			nodeList.add(node);
		}
		
//		oldDataCompare.insertNodeBatch(nodeList);
	}
	
	private static void addShelfNode(){
		List<Map> oldMap = oldDataCompare.selectTableListById("T_SHELF", "EMS_CONNECTION_ID",2009);
		
		Map node;
		
		List<Map> nodeList = new ArrayList<Map>();
		
		for (Map old : oldMap) {
			
			node = new HashMap();
			
			String nodeDisplayName = "1-" + old.get("SHELF_NO") + "-"
					+ old.get("SHELF_DISPLAY_NAME");
			
			node.put("NODE_ID", null);
			node.put("EMS_CONNECTION_ID", 2009);
			node.put("NODE_NAME", old.get("SHELF_ID"));

			node.put("NODE_DISPLAYNAME", nodeDisplayName);
			node.put("NODE_TYPE", 3);
			node.put("NODE_PARENT_ID", old.get("NE_ID"));
			node.put("NODE_PROPERTY", null);
			node.put("SYNC_STATUS", null);
			node.put("SYNC_TIME", null);
			node.put("HREF", null);
			node.put("NEED_DISPALY", 0);
			node.put("IS_LEAF", 1);
			node.put("POSITION_X", null);
			node.put("POSITION_Y", null);

			nodeList.add(node);
		}
		
//		oldDataCompare.insertNodeBatch(nodeList);
	}
	
	private static void addEquipNode(){
		List<Map> oldMap = oldDataCompare.selectTableListById("T_EQUIP", "EMS_CONNECTION_ID",2009);
		
		Map node;
		
		List<Map> nodeList = new ArrayList<Map>();
		
		for (Map old : oldMap) {
			
			node = new HashMap();
			
			Map slot = oldDataCompare.selectTableById("T_SLOT", "SLOT_ID", Integer.valueOf(old.get("SLOT_ID").toString()));
			
			String nodeDisplayName = slot.get("SLOT_NO") + "-"
					+ old.get("EQUIP_DISPLAY_NAME");
			
			node.put("NODE_ID", null);
			node.put("EMS_CONNECTION_ID", 2009);
			node.put("NODE_NAME", old.get("EQUIP_ID"));

			node.put("NODE_DISPLAYNAME", nodeDisplayName);
			
			node.put("NODE_TYPE", 4);
			node.put("NODE_PARENT_ID", old.get("SHELF_ID"));
			node.put("NODE_PROPERTY", null);
			node.put("SYNC_STATUS", null);
			node.put("SYNC_TIME", null);
			node.put("HREF", null);
			node.put("NEED_DISPALY", 0);
			node.put("IS_LEAF", 1);
			node.put("POSITION_X", null);
			node.put("POSITION_Y", null);

			nodeList.add(node);
		}
		
//		oldDataCompare.insertNodeBatch(nodeList);
	}
	
	private static void addPtpNode(){
		List<Map> oldMap = oldDataCompare.selectTableListById("T_PTP", "EMS_CONNECTION_ID",2009);
		
		Map node;
		
		List<Map> nodeList = new ArrayList<Map>();
		
		for (Map old : oldMap) {
			
			node = new HashMap();
			
			String nodeDisplayName = old.get("PORT_NO") + "-"
					+ old.get("PTP_DISPLAY_NAME");
			
			node.put("NODE_ID", null);
			node.put("EMS_CONNECTION_ID", 2009);
			node.put("NODE_NAME", old.get("PTP_ID"));

			node.put("NODE_DISPLAYNAME", nodeDisplayName);
			
			node.put("NODE_TYPE", 5);
			node.put("NODE_PARENT_ID", old.get("EQUIP_ID"));
			node.put("NODE_PROPERTY", null);
			node.put("SYNC_STATUS", null);
			node.put("SYNC_TIME", null);
			node.put("HREF", null);
			node.put("NEED_DISPALY", 0);
			node.put("IS_LEAF", 0);
			node.put("POSITION_X", null);
			node.put("POSITION_Y", null);

			oldDataCompare.insertNode(node);
		}
	}
	
}
