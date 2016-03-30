package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * @author xuxiaojun
 *
 */
public interface DataCollectMapper {
	
	
	/**
	 * 	获取整表数据
	 * @param tableName 表名
	 * @return
	 */
	public List selectTable(
			@Param(value = "tableName") String tableName);
	
	/**
	 * 	按id获取一条数据
	 * @param tableName 表名
	 * @param idName id字段名
	 * @param id id值
	 * @return
	 */
	public Map selectTableById(
			@Param(value = "tableName") String tableName,
			@Param(value = "idName") String idName,
			@Param(value = "id") int id);
	
	/**
	 * 按字段获取数据
	 * @param tableName 表名
	 * @param columnName 字段名
	 * @param columnValue 字段值
	 * @return
	 */
	public Map selectTableByColumn(
			@Param(value = "tableName") String tableName,
			@Param(value = "columnName") String columnName,
			@Param(value = "columnValue") String columnValue);
	
	/**
	 * 	按id获取数据列表
	 * @param tableName 表名
	 * @param idName id字段名
	 * @param id id值
	 * @return
	 */
	public List selectTableListById(
			@Param(value = "tableName") String tableName,
			@Param(value = "idName") String idName,
			@Param(value = "id") int id);
	
	/**
	 * 按ip获取ems连接对象
	 * @param ip
	 * @param isDel
	 * @return
	 */
	public Map selectEmsConnectionByIP(
			@Param(value = "ip") String ip,
			@Param(value = "isDel") Integer isDel);
	
	/**
	 * 查询网元对象
	 * @param emsConnectionId 网管id
	 * @param neName	网元序列号
	 * @return 网元表对象
	 */
	public Map selectNeByNeName(
			@Param(value = "emsConnectionId") int emsConnectionId,
			@Param(value = "neName") String neName);
	
	/**
	 * 获取网元型号映射信息
	 * @param productName 网元型号
	 * @param factory 厂家 1.HW 2.ZTE 3.朗讯 4.烽火 5.ALC 9.富士通
	 * @return
	 */
	public Map selectProductMapping(
			@Param(value = "productName") String productName,
			@Param(value = "factory") int factory);
	
	/**
	 * 查询ptp名和网元id
	 * 
	 * @param ptpId
	 *            ptpId
	 * @return key:PTP_NAME value:t_base_ne.name+"_"+t_base_ptp.name
	 *         例：3145729_PTP:/rack=1/shelf=1/slot=3/domain=sdh/port=1 
	 *        			key:BASE_NE_ID
	 */
	public Map selectPtpNameAndNeNameByPtpId(@Param(value = "ptpId") int ptpId);
	
	
	/**
	 * 获取已经同步过ctp的ptp对象
	 * @param domain in条件 业务类型 1.SDH 2.WDM 3.ETH 4.ATM 数据范例 1,2
	 * @param ptpType 端口类型： MP MAC等
	 * @param emsType 网管类型类型 11.T2000 12.U2000 21.E300 23.U31 31.lucent oms 41.烽火 otnm2000 51.Fujitsu 99.vems
	 * @param productName 型号
	 * @param unitName 板卡名
	 * @return
	 */
	public Map selectPtpForSyncCtp(
			@Param(value = "domain") Integer[] domain,
			@Param(value = "ptpType") String ptpType,
			@Param(value = "emsType") int emsType,
			@Param(value = "productName") String productName,
			@Param(value = "unitName") String unitName);
			
	/**
	 * 通过网管id获取数据集合
	 * @param tableName 表名
	 * @param emsConnectionId  网管id
	 * @return
	 */
	public List selectDataListByEmsConnectionId(
			@Param(value = "tableName") String tableName,
			@Param(value = "emsConnectionId") int emsConnectionId,
			@Param(value = "isDel") Integer isDel);
	
	/**
	 * 通过网元id获取数据集合
	 * @param tableName 表名
	 * @param neId 网元id
	 * @return
	 */
	public List selectDataListByNeId(
			@Param(value = "tableName") String tableName,
			@Param(value = "neId") int neId,
			@Param(value = "isDel") Integer isDel);
	
	/**
	 * 通过网管id获取link集合
	 * @param emsConnectionId 网管id
	 * @param neId 网元id
	 * @param linkType in条件 1.外部link 2.内部link 3.手工link
	 * @return
	 */
	public List selectLinkListByEmsConnectionId(
			@Param(value = "emsConnectionId") int emsConnectionId,
			@Param(value = "neId") Integer neId,
			@Param(value = "linkType") Integer[] linkType,
			@Param(value = "isDel") Integer isDel);
	
	/**
	 * 获取设备集合通过设备号
	 * @param tableName 表名
	 * @param neId 网元id
	 * @param equipNo key:rackNo,key:shelfNo,key:slotNo,key:subSlotNo
	 * @return
	 */
	public Map selectEquipByEquipNo(
			@Param(value = "tableName") String tableName,
			@Param(value = "neId") int neId,
			@Param(value = "equipNo") Map equipNo,
			@Param(value = "isDel") Integer isDel);

	/**
	 * 通过网元id,name获取Unit
	 * @param neId 网元id
	 * @param name 名称
	 * @return
	 */
	public Map selectUnitByNeIdAndName(
			@Param(value = "neId") int neId,
			@Param(value = "name") String name);

	/**
	 * 通过网元id获取ptp集合
	 * @param neId 网元id
	 * @param domain in条件 业务类型 1.SDH 2.WDM 3.ETH 4.ATM 数据范例 1,2
	 * @return
	 */
	public List selectPtpListByNeId(
			@Param(value = "neId") int neId,
			@Param(value = "domain")  Integer[] domain,
			@Param(value = "isDel") Integer isDel);
	
	/**
	 * 获取ptp通过ptp名，网元序列号
	 * @param emsConnectionId  网管id
	 * @param neName 网元序列号
	 * @param ptpName ptp名
	 * @return
	 */
	public Map selectPtpByNeSerialNoAndPtpName(
			@Param(value = "emsConnectionId") int emsConnectionId,
			@Param(value = "neName") String neName,
			@Param(value = "ptpName") String ptpName);
	
	/**
	 * 获取ctp通过ctp名，网元序列号
	 * @param emsConnectionId  网管id
	 * @param neName 网元序列号
	 * @param ctpName ptp名
	 * @return
	 */
	public Map selectPtnCtpByNeSerialNoAndCtpName(
			@Param(value = "emsConnectionId") int emsConnectionId,
			@Param(value = "neName") String neName,
			@Param(value = "ptpName") String ptpName,
			@Param(value = "ctpName") String ctpName);
	
	/**
	 * 通过制定条件获取ctp对象
	 * @param emsConnectionId 网管id
	 * @param neId 网元id
	 * @param ptpName ptp名
	 * @param ctpName ctp名
	 * @return
	 */
	public Map selectSdhCtp(
			@Param(value = "emsConnectionId") int emsConnectionId,
			@Param(value = "neId") int neId,
			@Param(value = "ptpName") String ptpName,
			@Param(value = "ctpName") String ctpName);
	
	/**
	 * 通过制定条件获取ctp对象
	 * @param emsConnectionId 网管id
	 * @param ptpId
	 * @param ctpName ctp名
	 * @param relPtpType
	 * @return
	 */
	public Map selectSdhCtpRelPtpIdAndRelPtpType(
			@Param(value = "ptpId") int ptpId,
			@Param(value = "ctpName") String ctpName,
			@Param(value = "relPtpType") int relPtpType);
	
	/**
	 * 通过指定条件获取ctp对象
	 * @param emsConnectionId 网管id
	 * @param neId 网元id
	 * @param ptpName ptp名
	 * @param ctpName ctp名
	 * @return
	 */
	public Map selectOtnCtp(
			@Param(value = "emsConnectionId") int emsConnectionId,
			@Param(value = "neId") int neId,
			@Param(value = "ptpName") String ptpName,
			@Param(value = "ctpName") String ctpName);
	
	/**
	 * 通过指定条件获取ctp对象 for E300
	 * @param emsConnectionId
	 * @param neId
	 * @param direction
	 * @param ptpType
	 * @param rackNo
	 * @param shelfNo
	 * @param slotNo
	 * @param portNo
	 * @param ctpName
	 * @return
	 */
	public Map selectCtpForE300(
			@Param(value = "emsConnectionId") int emsConnectionId,
			@Param(value = "neId") int neId,
			@Param(value = "direction") int direction,
			@Param(value = "ptpType") String ptpType,
			@Param(value = "rackNo") String rackNo,
			@Param(value = "shelfNo") String shelfNo,
			@Param(value = "slotNo") String slotNo,
			@Param(value = "portNo") String portNo,
			@Param(value = "ctpName") String ctpName);
	
	/**
	 * 通过指定条件获取ctp对象
	 * @param emsConnectionId
	 * @param ptpId
	 * @return
	 */
	public Integer selectOtnCtpIdTypeIsFtp(
			@Param(value = "emsConnectionId") int emsConnectionId,
			@Param(value = "ptpId") int ptpId);
	
	/**
	 * 通过网元id,ptp名获取ptp
	 * @param neId 网元id
	 * @param ptpName ptp名
	 * @return
	 */
	public Map selectPtpByNeIdAndPtpName(
			@Param(value = "neId") int neId,
			@Param(value = "ptpName") String ptpName);
	
	/**
	 * e300 根据属性获取ptp
	 * @param neId
	 * @param direction
	 * @param ptptype
	 * @param rack
	 * @param shelf
	 * @param slot
	 * @param port
	 * @return
	 */
	public Map selectPtpForE300(
			@Param(value = "neId") int neId,
			@Param(value = "direction") int direction,
			@Param(value = "ptpType") String ptpType,
			@Param(value = "rackNo") String rackNo,
			@Param(value = "shelfNo") String shelfNo,
			@Param(value = "slotNo") String slotNo,
			@Param(value = "portNo") String portNo);
	
	public Map<String, Object> selectPtpByProperties(
			@Param(value = "neId") int neId,
			@Param(value = "properties") Map<String, Object> properties);
	
	/**
	 * 获取ptp virtual表的最小Id
	 */
	public Integer selectPtpVirtualMinId();
	
	/**
	 * 中兴 根据属性获取ptp
	 * @param neId
	 * @param ptptype
	 * @param rack
	 * @param shelf
	 * @param slot
	 * @param port
	 * @return
	 */
	public Map selectPtpVirtualForZTE(
			@Param(value = "neId") int neId,
			@Param(value = "ptpType") String ptpType,
			@Param(value = "rackNo") String rackNo,
			@Param(value = "shelfNo") String shelfNo,
			@Param(value = "slotNo") String slotNo,
			@Param(value = "portNo") String portNo);
	
	
	/**
	 * 组装端口显示名称
	 * @param rackId
	 * @param shelfId
	 * @param slotId
	 * @param unitId
	 * @return
	 */
	public String selectPortDescByIds(
			@Param(value = "rackId") int rackId,
			@Param(value = "shelfId") int shelfId,
			@Param(value = "slotId") int slotId,
			@Param(value = "unitId") int unitId);
	
	/**
	 * 通过网元id获取sdh交叉连接列表
	 * @param neId 网元id
	 * @param isVirtual 是否虚拟交叉
	 * @return
	 */
	public List selectSdhCrsListByNeId(
			@Param(value = "neId") int neId,
			@Param(value = "isVirtual") int isVirtual,
			@Param(value = "isDel") Integer isDel);
	
	/**
	 * 通过网元id获取otn交叉连接列表
	 * @param neId 网元id
	 * @param isVirtual 是否虚拟交叉
	 * @return
	 */
	public List selectOtnCrsListByNeId(
			@Param(value = "neId") int neId,
			@Param(value = "isVirtual") int isVirtual,
			@Param(value = "isFromRoute") int isFromRoute,
			@Param(value = "isDel") Integer isDel);
	
	/**
	 * 获取从route渠道同步的otn交叉连接列表
	 * @param isFromRoute
	 * @return
	 */
	public List selectOtnCrsListFromRoute(
			@Param(value = "isFromRoute") int isFromRoute,
			@Param(value = "isDel") Integer isDel);
	
	/**
	 * 获取连接至指定ip和端口采集服务器的网管列表
	 * @param ip
	 * @param port
	 * @return
	 */
	public List<Map> selectEmsListByIpAndPort(
		@Param(value = "ip") String ip,
		@Param(value = "port") String port
	);
	
	/**
	 * 获取所有性能代号映射(T_PM_STD_INDEX)
	 * @return
	 */
	public List selectPmStdIndexByAll();
	
	/**
	 * 获取所有性能模版(T_PM_TEMPLATE_INFO)
	 * @return
	 */
	public List selectPmTemplateInfoByAll();
	
	/**
	 * 获取所有光口标准内容(T_PM_STD_OPT_PORT)
	 * @return
	 */
	public List selectPmStdOptPortByAll();
	
	/**
	 * 按ptpId、sdhCtpId、otnCtpId、targetType和pmStdIndex来获取PM物理量的基准值
	 * @param ptpId  PTP的ID
	 * @param sdhCtpId  SDH的CTP ID
	 * @param otnCtpId  OTN的CTP ID
	 * @param unitId  板卡的UNIT ID
	 * @param targetType  目标类型 1.EMSGROUP 2.EMS 3.SUBNET 4.NE 5.Shelf 6.Equipment 7.ptp 8.SDH-CTP 9.OTN-CTP
	 * @param pmStdIndex  标准PM参数名
	 * @return
	 */
	public Map selectPmCompareByTargetIdAndTypeAndStdIndex(
			@Param(value = "unitId") Integer unitId,
			@Param(value = "ptpId") Integer ptpId,
			@Param(value = "sdhCtpId") Integer sdhCtpId,
			@Param(value = "otnCtpId") Integer otnCtpId,
			@Param(value = "targetType") int targetType,
			@Param(value = "pmStdIndex") String pmStdIndex);
	

	/**
	 * 按ptmId、otnCtpId、sdhCtpId、pmStdIndex、location和接收时间获取历史性能表中连续异常计数器
	 * @param tableName  历史性能表的名称
	 * @param unitId 板卡的UNIT ID
	 * @param ptpId	PTP的ID
	 * @param otnCtpId OTN的CTP ID
	 * @param sdhCtpId SDH的CTP ID
	 * @param pmStdIndex 标准PM参数名
	 * @param location 位置
	 * @param beginTime 开始时间
	 * @param endTime 结束时间
	 * @return
	 */
	public List<Map> selectExcCountByPtpCtpIdAndStdIndexAndTime(
			@Param(value ="tableName") String tableName,
			@Param(value ="unitId") Integer unitId,
			@Param(value = "ptpId") Integer ptpId,
			@Param(value = "otnCtpId") Integer otnCtpId,
			@Param(value = "sdhCtpId") Integer sdhCtpId,
			@Param(value = "pmStdIndex") String pmStdIndex,
			@Param(value = "location") int location,
			@Param(value = "beginTime") String beginDate,
			@Param(value = "endTime") String endDate);
	
	/**
	 * 更新基准值表的内容项
	 * @param baseValue
	 */
	public void updatePmCompare(Map baseValue);
	
	/**
	 * 在基准值表中插入新的内容项
	 * @param baseValue
	 */
	public void insertPmCompare(Map baseValue);
	
	/**
	 * 判断原始性能表是否存在
	 * 
	 * @param tableName
	 * @return
	 */
	public Integer getPmTableExistance(
			@Param(value = "tableName") String tableName,
			@Param(value = "schemaName") String schemaName);
	
	/**
	 * 通过NeId获取网元所在的区域名和站名
	 * @param neId
	 * @return AREA_NAME,STATION_NAME
	 */
	public Map selectAreaNameAndStationNameByNeId(
			@Param(value ="neId") Integer neId);
			
	/**
	 *  通用查询法
	 * 
	 * @param NAME
	 *            表名
	 * @param ID_NAME
	 *            字段名
	 * @param ID_VALUE
	 *            字段值
	 * @param ID_NAME_2
	 *            字段名2
	 * @param ID_VALUE_2
	 *            字段值2
	 */
	public List<Map> getByParameter(@Param(value = "map")Map map);
	
	
	/**
	 *  查询包机人相关信息
	 * @param ID_NAME
	 *            字段名
	 * @param ID_VALUE
	 *            字段值
	 * @param ID_NAME_2
	 *            字段名2
	 * @param ID_VALUE_2
	 *            字段值2
	 * @return
	 */
	public List<Map> getInspect(@Param(value = "map")Map map);
	
	/**
	 * 查询所有子网的包机人信息
	 * @param ID_NAME
	 *            字段名
	 * @param ID_VALUE
	 *            字段值
	 * @param ID_NAME_2
	 *            字段名2
	 * @param ID_VALUE_2
	 *            list ，用于foreach 循环
	 * @return
	 */
	public List<Map> getInspectSubnet(@Param(value = "map")Map map);
	/**
	 * 根据id更新网管连接表
	 * @param emsConnection
	 */
	public void updateEmsConnectionByIP(Map emsConnection);

	/**
	 * @param ne
	 */
	public void updateNeById(Map ne);
	
	/**
	 * @param ne
	 */
	public void updateSncById(Map snc);
	
	/**
	 * @param ne
	 */
	public void updateRouteById(Map route);
	
	/**
	 * @param ne
	 */
	public void updateFdfrById(Map fdfr);
	
	/**
	 * @param ne
	 */
	public void updateLinkOfFdfrsById(Map linkOfFdfr);
	

	/**
	 * 初始化网元同步状态，接入服务器异常关闭可能导致网元同步状态一直为正在同步，需要初始化为同步失败
	 * @param emsConnectionId
	 * @param syncStatus
	 */
	public void initNeSyncStatusByEmsConnectionId(
			@Param(value = "emsConnectionId") Integer emsConnectionId,
			@Param(value = "syncStatus")Integer syncStatus);
	
	/**
	 * @param rack
	 */
	public void updateRackById(Map rack);
	
	/**
	 * @param shelf
	 */
	public void updateShelfById(Map shelf);
	
	/**
	 * @param slot
	 */
	public void updateSlotById(Map slot);
	
	/**
	 * @param slot
	 */
	public void updateSubSlotById(Map subSlot);
	
	/**
	 * @param unit
	 */
	public void updateUnitById(Map unit);
	
	/**
	 * @param unit
	 */
	public void updateSubUnitById(Map unit);
	
	/**
	 * @param ptp
	 */
	public void updatePtpById(Map ptp);
	
	/**
	 * @param link
	 */
	public void updateLinkById(Map link);
	
	/**
	 * @param clock
	 */
	public void updateClockSourceById(Map clock);
	
	/**
	 * @param eProtectionGroup
	 */
	public void updateEProtectionGroupById(Map eProtectionGroup);
	
	/**
	 * @param protectionGroup
	 */
	public void updateProtectionGroupById(Map protectionGroup);
	
	/**
	 * @param wdmProtectionGroup
	 */
	public void updateWdmProtectionGroupById(Map wdmProtectionGroup);
	
	/**
	 * @param ethService
	 */
	public void updateEthServiceById(Map ethService);
	
	/**
	 * @param vb
	 */
	public void updateVBById(Map vb);
	
	/**
	 * @param crs
	 */
	public void updateSdhCrsById(Map crs);
	
	/**
	 * @param crs
	 */
	public void updateOtnCrsById(Map crs);
	
	/**
	 * @param sdhCtp
	 */
	public void updateSdhCtpById(Map sdhCtp);
	
	/**
	 * @param tcaData
	 */
	public void updateTcaDataByKey(Map tcaData);
	
	/**
	 * @param tcaData
	 */
	public void insertTcaData(Map tcaData);
	
	/**
	 * @param protectionSwitchData
	 */
	public void insertProtectionSwitchData(Map protectionSwitchData);
	
	/**
	 * @param nes
	 */
	public void insertNeBatch(List<Map> nes);
	
	/**
	 * @param units
	 */
	public void insertSncBatch(List<Map> sncs);
	
	/**
	 * @param units
	 */
	public void insertRouteBatch(List<Map> routes);
	
	
	/**
	 * @param fdfr
	 */
	public void insertFdfr(Map fdfr);
	
	/**
	 * @param fdfrs
	 */
	public void insertFdfrBatch(List<Map> fdfrs);
	
	/**
	 * @param fdfrsList
	 */
	public void insertFdfrListBatch(List<Map> fdfrsList);
	
	/**
	 * @param linkOfFdfrsList
	 */
	public void insertLinkOfFdfrsBatch(List<Map> linkOfFdfrsList);
	
	
	
	
	/**
	 * @param racks
	 */
	public void insertRackBatch(List<Map> racks);
	
	/**
	 * @param shelfs
	 */
	public void insertShelfBatch(List<Map> shelfs);
	
	/**
	 * @param slots
	 */
	public void insertSlotBatch(List<Map> slots);
	
	/**
	 * @param slots
	 */
	public void insertSubSlotBatch(List<Map> subSlots);
	
	/**
	 * @param units
	 */
	public void insertUnitBatch(List<Map> units);
	
	/**
	 * @param units
	 */
	public void insertSubUnitBatch(List<Map> subUnits);
	
	/**
	 * @param ptps
	 */
	public void insertPtpBatch(List<Map> ptps);
	
	/**
	 * @param links
	 */
	public void insertLinkBatch(List<Map> links);
	
	/**
	 * @param clocks
	 */
	public void insertClockSourceBatch(List<Map> clocks);
	
	/**
	 * @param ethServces
	 */
	public void insertEthServiceBatch(List<Map> ethServces);
	
	/**
	 * @param bindingPaths
	 */
	public void insertBindingPathBatch(List<Map> bindingPaths);
	
	/**
	 * @param eProtectionGroup
	 */
	public void insertEProtectionGroup(Map eProtectionGroup);
	
	/**
	 * @param protectionGroup
	 */
	public void insertProtectionGroup(Map protectionGroup);
	
	/**
	 * @param wdmProtectionGroup
	 */
	public void insertWdmProtectionGroup(Map wdmProtectionGroup);
	
	/**
	 * @param protectUnit
	 */
	public void insertProtectUnit(Map protectUnit);
	
	/**
	 * @param protectPtp
	 */
	public void insertProtectPtp(Map protectPtp);
	
	/**
	 * @param wdmProtectPtp
	 */
	public void insertWdmProtectPtp(Map wdmProtectPtp);
	
	/**
	 * @param eProtectionGroups
	 */
	public void insertEProtectionGroupBatch(List<Map> eProtectionGroups);
	
	/**
	 * @param crss
	 */
	public void insertSdhCrsBatch(List<Map> crss);
	
	/**
	 * @param crss
	 */
	public void insertOtnCrsBatch(List<Map> crss);
	
	/**
	 * @param ptpVirtual
	 */
	public void insertPtpVirtual(Map ptpVirtual);
	
	/**
	 * @param sdhCtp
	 */
	public void insertSdhCtp(Map sdhCtp);
	
	/**
	 * @param otnCtp
	 */
	public void insertOtnCtp(Map otnCtp);
	
	/**
	 * @param sdhCtps
	 */
	public void insertSdhCtpBatch(List<Map> sdhCtps);
	
	/**
	 * @param otnCtpParams
	 */
	public void insertOtnCtpParamBatch(List<Map> otnCtpParams);
	
	/**
	 * @param filePath
	 * @param terminatedSymbol
	 */
	public void insertSdhCtpBatchTxt(
			@Param(value = "filePath") String filePath,
			@Param(value = "terminatedSymbol") String terminatedSymbol);
	
	/**
	 * @param filePath
	 * @param terminatedSymbol
	 */
	public void insertOtnCtpBatchTxt(
			@Param(value = "filePath") String filePath,
			@Param(value = "terminatedSymbol") String terminatedSymbol);
	
	/**
	 * @param filePath
	 * @param terminatedSymbol
	 */
	public void insertPtnCtpBatchTxt(
			@Param(value = "filePath") String filePath,
			@Param(value = "terminatedSymbol") String terminatedSymbol);
	
	/**
	 * @param filePath
	 * @param terminatedSymbol
	 */
	public void insertOtnCtpParamBatchTxt(
			@Param(value = "filePath") String filePath,
			@Param(value = "terminatedSymbol") String terminatedSymbol);
	
	/**
	 * @param ptnCtps
	 */
	public void insertOrUpdatePtnCtp(Map ptnCtp);
	
	/**
	 * @param productMapping
	 */
	public void insertProductMapping(Map productMapping);
	
	/**
	 * @param insertAlarmTransfer
	 */
	public void insertAlarmTransfer(Map insertAlarmTransfer);
	
	/**
	 * @param vb
	 */
	public void insertVB(Map vb);
	
	/**
	 * @param ptps
	 */
	public void insertVBPtpBatch(List<Map> ptps);
	
	
	/**
	 * @param eProtectionGroupId
	 */
	public void deleteProtectUnitByEProtectionGroupId(@Param(value = "eProtectionGroupId") int eProtectionGroupId);
	
	/**
	 * @param protectionGroupId
	 */
	public void deleteProtectPtpByProtectionGroupId(@Param(value = "protectionGroupId") int protectionGroupId);
	
	/**
	 * @param wdmProtectionGroupId
	 */
	public void deleteWdmProtectPtpByProtectionGroupId(@Param(value = "wdmProtectionGroupId") int wdmProtectionGroupId);
	
	/**
	 * @param ptpId
	 */
	public void deleteBindingPathByPtpId(@Param(value = "ptpId") int ptpId);
	
	/**
	 * @param ptpId
	 */
	public void deleteBindingPathByNeIdPtpName(
			@Param(value = "neId") int neId,
			@Param(value = "ptpName") String ptpName);

	/**
	 * @param vbId
	 */
	public void deletePtpByVbId(@Param(value = "vbId") int vbId);
	
	/**
	 * @param vbId
	 */
	public void deletePtnFdfrListByEmsConnectionId(@Param(value = "emsConnectionId") int emsConnectionId);

	/** ----告警模块---- **/
	/**
	 * Method name: judgeIsNeedShield <BR>
	 * Description: 根据厂家、告警名称、告警类型、告警级别、业务影响、网元Id(因为屏蔽器的源只能选网元级别),判断是否需要屏蔽<BR>
	 * Remark: 2014-02-08<BR>
	 * @author CaiJiaJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> judgeIsNeedShield(@Param(value = "map")Map<String, Object> paramMap);
	/**
	 * Method name: judgeIsNeedRedefine <BR>
	 * Description: 根据告警名称、网管ID、原告警级别,判断是否需要重定义<BR>
	 * Remark: 2014-02-08<BR>
	 * @author CaiJiaJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> judgeIsNeedRedefine(@Param(value = "map")Map<String, Object> paramMap);
	/**
	 * Method name: judgeIsNeedNormalized <BR>
	 * Description: 根据厂家、告警名称、告警标准名称,判断是否需要归一化<BR>
	 * Remark: 2014-02-08<BR>
	 * @author CaiJiaJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> judgeIsNeedNormalized(@Param(value = "map")Map<String, Object> paramMap);
	/**
	 * Method name: judgeIsNeedConfirm <BR>
	 * Description: 根据网管Id,判断是否需要立即确认<BR>
	 * Remark: 2014-02-11<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public List<Map<String, Object>> judgeIsNeedConfirm(@Param(value = "emsId")int emsId);
	/**
	 * Method name: getSystemParam <BR>
	 * Description: 查询系统参数<BR>
	 * Remark: 2014-02-11<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getSystemParam(@Param(value = "paramKey")String paramKey);
	/**
	 * 通过特定参数获取指定的单元盘数据
	 * @param emsConnectionId
	 * @param neId
	 * @param rackNo
	 * @param shelfNo
	 * @param slotNo
	 * @return
	 */
	public Map getUnitByManyParam(
			@Param(value = "emsConnectionId") int emsConnectionId,
			@Param(value = "neId") Integer neId,
			@Param(value = "rackNo") Integer rackNo,
			@Param(value = "shelfNo") Integer shelfNo,
			@Param(value = "slotNo") Integer slotNo);
	
	
	/**
	 * Method name: judgeIsNeedShieldByNULL <BR>
	 * Description: 判断是否需要屏蔽,未选择告警源<BR>
	 * Remark: 2014-02-08<BR>
	 * @author CaiJiaJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> judgeIsNeedShieldByNULL(@Param(value = "map")Map<String, Object> paramMap);
	
	/** 告警入库附加信息获取 **/
	/**
	 * 获取特定的网管分组及网管信息
	 * @param emsConnectionId
	 * @return Map </br>
	 *         Key:EMS_GROUP_NAME,BASE_EMS_GROUP_ID,EMS_NAME
	 */
	public Map<String, Object> getEmsObjInfoForAlm(@Param(value = "emsId") int emsId);	
	
	/**
	 * 获取特定网元的网管、网元及子网信息
	 * @param emsConnectionId
	 * @param neSerialNo
	 * @return Map </br>
	 *         Key:EMS_GROUP_NAME,BASE_EMS_GROUP_ID,EMS_NAME,SUBNET_ID,SUBNET_NAME,NE_ID,</br>
	 *             NE_NAME,NE_NATIVE_EMS_NAME,NE_USER_LABEL,NE_TYPE,PRODUCT_NAME,RESOURCE_ROOM_ID
	 */
	public Map<String, Object> getNeObjInfoForAlm(
			@Param(value = "emsId") int emsId,
			@Param(value = "neSerialNo") String neSerialNo);
	
	/**
	 * 获取特定网元的网管、网元及子网信息
	 * @param emsConnectionId
	 * @param nativeEmsName
	 * @return Map </br>
	 *         Key:EMS_GROUP_NAME,BASE_EMS_GROUP_ID,EMS_NAME,SUBNET_ID,SUBNET_NAME,NE_ID,</br>
	 *             NE_NAME,NE_NATIVE_EMS_NAME,NE_USER_LABEL,NE_TYPE,PRODUCT_NAME,RESOURCE_ROOM_ID
	 */
	public Map<String, Object> getNeObjInfoByNativeNameForAlm(
			@Param(value = "emsId") int emsId,
			@Param(value = "nativeEmsName") String nativeEmsName);
	
	/**
	 * 通过机房ID获取机房和局站信息
	 * @param roomId
	 * @return Map </br>
	 *         Key:ROOM_NAME,STATION_ID,STATION_NAME,RESOURCE_AREA_ID
	 */
	public Map<String, Object> getRoomAndStationInfoByRoomId(@Param(value = "roomId") int roomId);
	
	/**
	 * 获取特定链路的网管、子网及网元信息
	 * @param emsConnectionId
	 * @param neSerialNo
	 * @param linkName
	 * @return Map </br>
	 *         Key:EMS_GROUP_NAME,BASE_EMS_GROUP_ID,EMS_NAME,BASE_LINK_ID
	 */
	public Map<String, Object> getLinkObjInfoForAlm(
			@Param(value = "emsId") int emsId,
			@Param(value = "linkName") String linkName);
	
	/**
	 * 通过网元序列号及ptp名获取网管、网元、子网及端口信息
	 * @param emsConnectionId
	 * @param neSerialNo
	 * @param ptpName
	 * @return Map </br>
	 *         Key:EMS_GROUP_NAME,BASE_EMS_GROUP_ID,EMS_NAME,SUBNET_ID,SUBNET_NAME,NE_ID,NE_NAME,NE_NATIVE_EMS_NAME,
	 *         NE_USER_LABEL,NE_TYPE,PRODUCT_NAME,RACK_NO,RACK_DISPLAY_NAME,SHELF_ID,SHELF_NO,SHELF_DISPLAY_NAME,
	 *         SLOT_NO,SLOT_DISPLAY_NAME,SLOT_NATIVE_EMS_NAME,SLOT_USER_LABEL,UNIT_ID,SUB_UNIT_ID,UNIT_DESC,UNIT_NAME,
	 *         UNIT_NATIVE_EMS_NAME,UNIT_USER_LABEL,PTP_ID,PORT_NO,PORT_NAME,PTP_NATIVE_EMS_NAME,PTP_USER_LABEL,
	 *         PTP_TYPE,INTERFACE_RATE,DOMAIN,RESOURCE_ROOM_ID
	 */
	public Map<String, Object> getPtpObjInfoForAlm(
			@Param(value = "emsId") int emsId,
			@Param(value = "neSerialNo") String neSerialNo,
			@Param(value = "ptpName") String ptpName);
	
	/**
	 * 通过网元序列号及ptp名获取网管、网元、子网及端口信息（E300专用）
	 * @param emsConnectionId
	 * @param neSerialNo
	 * @param ptpName
	 * @return Map </br>
	 *         Key:EMS_GROUP_NAME,BASE_EMS_GROUP_ID,EMS_NAME,SUBNET_ID,SUBNET_NAME,NE_ID,NE_NAME,NE_NATIVE_EMS_NAME,
	 *         NE_USER_LABEL,NE_TYPE,PRODUCT_NAME,RACK_NO,RACK_DISPLAY_NAME,SHELF_ID,SHELF_NO,SHELF_DISPLAY_NAME,
	 *         SLOT_NO,SLOT_DISPLAY_NAME,SLOT_NATIVE_EMS_NAME,SLOT_USER_LABEL,UNIT_ID,SUB_UNIT_ID,UNIT_DESC,UNIT_NAME,
	 *         UNIT_NATIVE_EMS_NAME,UNIT_USER_LABEL,PTP_ID,PORT_NO,PORT_NAME,PTP_NATIVE_EMS_NAME,PTP_USER_LABEL,
	 *         PTP_TYPE,INTERFACE_RATE,DOMAIN,RESOURCE_ROOM_ID
	 */
	public Map<String, Object> getPtpObjInfoForAlmE300(
			@Param(value = "emsId") int emsId,
			@Param(value = "neSerialNo") String neSerialNo,
			@Param(value = "direction") int direction,
			@Param(value = "ptpType") String ptpType,
			@Param(value = "rackNo") String rackNo,
			@Param(value = "shelfNo") String shelfNo,
			@Param(value = "slotNo") String slotNo,
			@Param(value = "portNo") String portNo);
	
	/**
	 * 获取特定机架的相关网管分组、网管、子网、网元及机架信息
	 * @param emsConnectionId
	 * @param neSerialNo
	 * @param rackObjName
	 * @return Map </br>
	 *         Key:EMS_GROUP_NAME,BASE_EMS_GROUP_ID,EMS_NAME,SUBNET_ID,SUBNET_NAME,NE_ID,NE_NAME,NE_NATIVE_EMS_NAME,
	 *         NE_USER_LABEL,NE_TYPE,PRODUCT_NAME,RACK_NO,RACK_DISPLAY_NAME,RESOURCE_ROOM_ID
	 */
	public Map<String, Object> getRackObjInfoForAlm(
			@Param(value = "emsId") int emsId,
			@Param(value = "neSerialNo") String neSerialNo,
			@Param(value = "rackObjName") String rackObjName);
	
	/**
	 * 获取特定子架的相关网管分组、网管、子网、网元及子架信息
	 * @param emsConnectionId
	 * @param neSerialNo
	 * @param shelfObjName
	 * @return Map </br>
	 *         Key:EMS_GROUP_NAME,BASE_EMS_GROUP_ID,EMS_NAME,SUBNET_ID,SUBNET_NAME,NE_ID,NE_NAME,NE_NATIVE_EMS_NAME,
	 *         NE_USER_LABEL,NE_TYPE,PRODUCT_NAME,RACK_NO,RACK_DISPLAY_NAME,SHELF_ID,SHELF_NO,
	 *         SHELF_DISPLAY_NAME,RESOURCE_ROOM_ID
	 */
	public Map<String, Object> getShelfObjInfoForAlm(
			@Param(value = "emsId") int emsId,
			@Param(value = "neSerialNo") String neSerialNo,
			@Param(value = "shelfObjName") String shelfObjName);

	/**
	 * 获取特定槽道的相关网管分组、网管、子网、网元及槽道信息
	 * @param emsConnectionId
	 * @param neSerialNo
	 * @param slotObjName
	 * @return Map </br>
	 *         Key:EMS_GROUP_NAME,BASE_EMS_GROUP_ID,EMS_NAME,SUBNET_ID,SUBNET_NAME,NE_ID,NE_NAME,NE_NATIVE_EMS_NAME,
	 *         NE_USER_LABEL,NE_TYPE,PRODUCT_NAME,RACK_NO,RACK_DISPLAY_NAME,SHELF_ID,SHELF_NO,SHELF_DISPLAY_NAME,
	 *         SLOT_NO,SLOT_DISPLAY_NAME,SLOT_NATIVE_EMS_NAME,SLOT_USER_LABEL,RESOURCE_ROOM_ID
	 */
	public Map<String, Object> getSlotObjInfoForAlm(
			@Param(value = "emsId") int emsId,
			@Param(value = "neSerialNo") String neSerialNo,
			@Param(value = "slotObjName") String slotObjName);

	/**
	 * 获取特定板卡的相关网管分组、网管、子网、网元及板卡信息
	 * @param emsConnectionId
	 * @param neSerialNo
	 * @param unitObjName
	 * @return Map </br>
	 *         Key:EMS_GROUP_NAME,BASE_EMS_GROUP_ID,EMS_NAME,SUBNET_ID,SUBNET_NAME,NE_ID,NE_NAME,NE_NATIVE_EMS_NAME,
	 *         NE_USER_LABEL,NE_TYPE,PRODUCT_NAME,RACK_NO,RACK_DISPLAY_NAME,SHELF_ID,SHELF_NO,
	 *         SHELF_DISPLAY_NAME,SLOT_NO,SLOT_DISPLAY_NAME,SLOT_NATIVE_EMS_NAME,SLOT_USER_LABEL,UNIT_ID,UNIT_DESC,
	 *         UNIT_NAME,UNIT_NATIVE_EMS_NAME,UNIT_USER_LABEL,RESOURCE_ROOM_ID
	 */
	public Map<String, Object> getUnitObjInfoForAlm(
			@Param(value = "emsId") int emsId,
			@Param(value = "neSerialNo") String neSerialNo,
			@Param(value = "unitObjName") String unitObjName);
	
	/**
	 * 获取特定保护组的网管分组、网管、子网及网元信息
	 * @param emsConnectionId
	 * @param neSerialNo
	 * @param protectName
	 * @return Map </br>
	 *         Key:EMS_GROUP_NAME,BASE_EMS_GROUP_ID,EMS_NAME,SUBNET_ID,SUBNET_NAME,NE_ID,</br>
	 *             NE_NAME,NE_NATIVE_EMS_NAME,NE_USER_LABEL,NE_TYPE,PRODUCT_NAME,RESOURCE_ROOM_ID
	 */
	public Map<String, Object> getProtectObjInfoForAlm(
			@Param(value = "emsId") int emsId,
			@Param(value = "neSerialNo") String neSerialNo,
			@Param(value = "protectName") String protectName);

	/**
	 * 获取特定AID的相关网管分组、网管、子网、网元及板卡信息
	 * @param emsConnectionId
	 * @param neSerialNo
	 * @param rackNo
	 * @param shelfNo
	 * @param slotNo
	 * @return Map </br>
	 *         Key:EMS_GROUP_NAME,BASE_EMS_GROUP_ID,EMS_NAME,SUBNET_ID,SUBNET_NAME,NE_ID,NE_NAME,NE_NATIVE_EMS_NAME,
	 *         NE_USER_LABEL,NE_TYPE,PRODUCT_NAME,RACK_NO,SHELF_ID,SHELF_NO,SLOT_NO,SLOT_DISPLAY_NAME,
     *         SLOT_NATIVE_EMS_NAME,SLOT_USER_LABEL,UNIT_ID,UNIT_DESC,UNIT_NAME,UNIT_NATIVE_EMS_NAME,
     *         UNIT_USER_LABEL,RESOURCE_ROOM_ID
	 */
	public Map<String, Object> getAidObjInfoForAlm(
			@Param(value = "emsId") int emsId,
			@Param(value = "neSerialNo") String neSerialNo,
			@Param(value = "rackNo") String rackNo,
			@Param(value = "shelfNo") String shelfNo,
			@Param(value = "slotNo") String slotNo);
	
	/**
	 * 获取用于贝尔AID级告警的相关网管分组、网管、子网、网元及板卡信息
	 * @param emsId
	 * @param nativeEmsNameOri
	 * @param rackNo
	 * @param shelfNo
	 * @param slotNo
	 * @return
	 */
	public Map<String, Object> getAidObjInfoForAlu(
			@Param(value = "emsId") int emsId,
			@Param(value = "nativeEmsNameOri") String nativeEmsNameOri,
			@Param(value = "rackNo") String rackNo,
			@Param(value = "shelfNo") String shelfNo,
			@Param(value = "slotNo") String slotNo);

	/**
	 * 获取对端端口信息
	 * @param ptpId
	 * @param linkType
	 * @param isDel
	 * @return
	 */
	public List<Map<String, Object>> getAffectTpsByPtpId(
			@Param(value = "ptpId") int ptpId,
			@Param(value = "linkType") Integer[] linkType,
			@Param(value = "isDel") Integer isDel);
	
	/**
	 * 通过网管分组、网管、子网及网元查询包机人信息
	 */
	public List<Map<String, Object>> getInspectInfoForAlm(@Param(value = "map")Map<String, Object> map);
	
	
	public void insertUnitTypeDefineBatch(@Param(value = "data") Map data);
	
	/**
	 * 万能删除法
	 * 
	 * @param NAME
	 *            表名
	 * @param ID_NAME
	 *            字段名
	 * @param ID_VALUE
	 *            字段值
	 * @param ID_NAME_2
	 *            字段名2
	 * @param ID_VALUE_2
	 *            字段值2
	 */
	public void deleteByParameter(@Param(value = "map") Map map);
	
	/**
	 * 万能更新法
	 * 
	 * @param NAME
	 *            表名
	 * @param ID_NAME
	 *            字段名 where 条件
	 * @param ID_VALUE
	 *            字段值
	 * @param ID_NAME_2
	 *            字段名2
	 * @param ID_VALUE_2
	 *            字段值2
	 */
	public void updateByParameter(@Param(value = "map") Map map);
}
