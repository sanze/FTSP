package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface CircuitManagerMapper {

	/**
	 * @author wangjian (前半) daihuijun (后半)
	 */

	/** ********************************wangjian**begin************************ */
	/**
	 * @param connectionId
	 *            (网管id) 查询电路生成的任务页面
	 * @@@分权分域到网元@@@
	 * @param connectionId
	 *            (网管id)
	 * @return 自动生成任务的实体
	 */
	public List<Map> getAllEMSTask(@Param(value = "emsGroupId") int emsGroupId,
			@Param(value = "start") int start, @Param(value = "limit") int limit,
			@Param(value = "listEmsId")List listEmsId,
			@Param(value = "userId") int userId,
			@Param(value = "Define") Map Define);

	/**
	 * @param emsId
	 * @param targetType
	 * @param taskType
	 * @return
	 */
	public Integer getTaskIdFromEmsId(
			@Param(value = "emsId") int emsId,
			@Param(value = "targetType") int targetType,
			@Param(value = "taskType") int taskType);

	/**
	 * @@@分权分域到网元@@@
	 * 查询符合生成电路的交叉连接数目
	 * 
	 * @param map_c
	 * @return
	 */
	public Map getTotal(@Param(value = "map") Map map,
			@Param(value = "userId") int userId,
			@Param(value = "Define") Map Define);

	/**
	 * 查询符合生成电路的交叉连接
	 * @@@分权分域到网元@@@
	 * @param map
	 * @param startNum
	 * @param size
	 * @return
	 */
	public List<Map> getPartCrossId(@Param(value = "map") Map map,
			@Param(value = "startNum") int startNum,
			@Param(value = "size") int size,
			@Param(value = "userId") int userId,
			@Param(value = "Define") Map Define);

	/**
	 * 查询交叉连接
	 * 
	 * @param crossConnectionId
	 * @return Map
	 */
	public List<Map> getCrossConnect(@Param(value = "map") Map map);

	/**
	 * 根据a端时隙查询电路(可能有多条，降序排列，去第一条)
	 * 
	 * @param a_ctp_id
	 * @return list
	 */
	public List<Map> getCircuitByCtp(@Param(value = "a_ctp_id") int a_ctp_id);

	/**
	 * 查询ptp
	 * 
	 * @param map
	 * @author Administrator
	 * @return Map
	 */
	public List<Map> getPtp(@Param(value = "map") Map map);

	/**
	 * 取得当前的最大电路编号
	 * 
	 * @return Map
	 */
	public Map getMaxCircuitNo();
	
	/**
	 * 取得当前的otn最大电路编号
	 * 
	 * @return Map
	 */
	public Map getMaxOtnCircuitNo();
	
	/**
	 * 取得当前的ptn最大电路编号
	 * 
	 * @return Map
	 */
	public Map getMaxPtnCircuitNo();
	
	

	/**
	 * 根据组合条件查询ctp
	 * 
	 * @param map
	 * @return list
	 */
	public List<Map> getCtp(@Param(value = "map") Map map);

	/**
	 * 查询电路
	 * 
	 * @param map
	 * @return List
	 */
	public List<Map> getCircuit(@Param(value = "map") Map map);

	/**
	 * 从路由表中查出交叉连接（两表关联查询）
	 * 
	 * @param cirId
	 * @param isCrs
	 *            交叉连接
	 * @return List
	 */
	public List<Map> getCrsFromRoute(@Param(value = "cirId") int cirId,
			@Param(value = "isCrs") int isCrs);

	/**
	 * 从路由表中查出link（两表关联查询）
	 * 
	 * @param cirId
	 * @return List
	 */
	public List<Map> getLinkFromRoute(@Param(value = "cirId") int cirId,
			@Param(value = "isLink") int isLink);

	/**
	 * 将某条电路，某个路由之前的记录全部查询出来
	 * 
	 * @param cirId
	 * @param routeId
	 * @return
	 */
	public List<Map> getRouteBefore(@Param(value = "cirId") int cirId,
			@Param(value = "routeId") int routeId);
	
	/**
	 * 判断电路的端口是否是华为端口
	 * @param map
	 * @return
	 */
	public List<Map> selectHwPort(@Param(value = "map") Map map);
	
	

	/**
	 * 万能查询法
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

	public List<Map> getByParameter(@Param(value = "map") Map map);
	
	/**
	 * 查询a端起，为以太网的电路
	 * 
	 */

	public List<Map> getEthFromInfoA();
	/**
	 * 查询z端起，为以太网的电路
	 * 
	 */

	public List<Map> getEthFromInfoZ();
	
	/**
	 * 查询需要合并的虚拟交叉
	 * 
	 */

	public List<Map> selectDoubleACtp();
	

	/**
	 * 查询出最新一条插入的记录
	 * 
	 * @param NAME
	 *            表名
	 * @param IN_NAME
	 *            字段名
	 * @param IN_VALUE
	 *            字段值
	 * @param ID
	 *            排序名称
	 * @return
	 */

	public Map getLatestRecord(@Param(value = "map") Map map);

	/**
	 * 获取网管分组
	 * 
	 * @return
	 */
	public List<Map> getAllGroup();

	/**
	 * 获取要被删除的电路id
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> getDeleteCir(@Param(value = "map") Map map);

	/**
	 * 获取要被删除的otn电路id
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> getDeleteOtnCir(@Param(value = "map") Map map);
	
	/**
	 * 根据端口查询相关电路
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> getCirByPort(@Param(value = "map") Map map);
	
	/**
	 * 新增交叉时，根据端口查询相关电路
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> getCirByPortCrs(@Param(value = "map") Map map);
	
	/**
	 * 根据端口查询相关电路
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> getOtnCirByPort(@Param(value = "map") Map map);
	
	/**
	 * 根据端口查询相关虚拟交叉
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> getOtnVirByPort(@Param(value = "map") Map map);
	
	/**
	 * 根据交叉连接查询相关电路
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> getCirByCrs(@Param(value = "map") Map map);
	/**
	 * 根据交叉连接查询otn相关电路
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> getOtnCirByCrs(@Param(value = "map") Map map);
	

	/**
	 * 根据ctp和ptp条件查询下一跳的otn交叉
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> getOtnCrsByCtp(@Param(value = "map") Map map);

	/**
	 * 查询下一跳交叉连接向上进位
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> getOtnCrsUp(@Param(value = "map") Map map);

	/**
	 * 往电路信息表中插入数据
	 * 
	 * @param cir_info
	 */
	public void insertCircuitInfo(@Param(value = "map") Map map);

	/**
	 * 电路表中插入信息
	 * 
	 * @param cir
	 */
	public void insertCircuit(@Param(value = "map") Map map);

	/**
	 * 路由信息插入
	 * 
	 * @param route
	 */
	public void insertRoute(@Param(value = "map") Map map);

	/**
	 * 插入虚拟交叉
	 * 
	 * @param map
	 */

	public void insertCtp(@Param(value = "map") Map map);

	/**
	 * 查询电路（显示用）（多表关联查询）
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> selectCircuit(@Param(value = "map") Map map);
	
	/**
	 * 查询最新生成的otn电路
	 * @param map
	 * @return
	 */
	public List<Map> selectOtnCircuit(@Param(value="map") Map map);
	
	/**
	 * 查询最新生成的Ptn电路
	 * @param map
	 * @return
	 */
	public List<Map> selectPtnCircuit(@Param(value="map") Map map);
	

	/**
	 * 查询电路的总数（显示用）（多表关联查询）
	 * 
	 * @param map
	 * @return
	 */
	public Map selectCircuitTotal(@Param(value = "map") Map map);
	
	
	
	/**
	 * 查询最新生成的otn电路的总数
	 * @param map
	 * @return
	 */
	public Map selectOtnCircuitTotal(@Param(value="map")Map map);
	
	/**
	 * 查询最新生成的ptn电路的总数
	 * @param map
	 * @return
	 */
	public Map selectPtnCircuitTotal(@Param(value="map")Map map);

	/**
	 * 新增ton 关系表
	 * 
	 * @param map
	 */
	public void insertOtnRelation(@Param(value = "map") Map map);

	/**
	 * 插入虚拟交叉
	 * 
	 * @param map
	 */
	public void insertOtnCrs(@Param(value = "map") Map map);

	/**
	 * 向otn路由表中插入信息
	 * 
	 * @param map
	 */
	public void insertOtnRoute(@Param(value = "map") Map map);

	/**
	 * 插入一条otn电路
	 * 
	 * @param map
	 */
	public void insertOtnCir(@Param(value = "map") Map map);

	/**
	 * 插入电路信息表
	 * 
	 * @param map
	 */
	public void insertOtnInfo(@Param(value = "map") Map map);

	/**
	 * 向task表中插入一条任务
	 * 
	 * @param map
	 */
	public void insertTask(@Param(value = "map") Map map);

	/**
	 * 向task详细表中插入一条任务
	 * 
	 * @param map
	 */
	public void insertTaskInfo(@Param(value = "map") Map map);

	/**
	 * 更新电路表
	 * 
	 * @param
	 */
	public void updateCircuit(@Param(value = "map") Map map);

	/**
	 * 更新交叉连接表
	 * 
	 * @param map
	 */
	public void updateCrs(@Param(value = "map") Map map);

	/**
	 * 更新交叉连接
	 * 
	 * @param
	 */
	public void updateCrsState(@Param(value = "state") int state);

	/**
	 * 更新电路状态
	 * 
	 * @param state
	 */
	public void updateCirState(@Param(value = "state") int state);

	/**
	 * 修改更新电路信息表的数据
	 * 
	 * @param map
	 */
	public void updateCircuitInfo(@Param(value = "map") Map map);

	/**
	 * 动态查询，根据传入的表名，字段名，组织查询语句
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> selectTable(@Param(value = "map") Map map);

	/**
	 * 更新任务状态
	 * 
	 * @param map
	 */
	public void updateTask(@Param(value = "map") Map map);

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

	/**
	 * 查询生成虚拟交叉的起点交叉的总数
	 * @@@分权分域到网元@@@
	 * @param isEnd
	 *            ptp 还是ftp
	 * @param isV
	 *            自身不是虚拟交叉
	 * @return
	 */
	public Map getOtnCrsTotal(@Param(value = "map") Map map,
			@Param(value = "userId") int userId,
			@Param(value = "Define") Map Define);

	/**
	 * link查询
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> getLink(@Param(value = "map") Map map);

	/**
	 * 路由查询
	 * 
	 * @param map
	 * @return List
	 */
	public List<Map> getRoute(@Param(value = "map") Map map);

	/**
	 * 删除路由信息
	 * 
	 * @param routeId
	 */
	public void deleteRoute(@Param(value = "routeId") int routeId);

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
	 * 查询虚拟交叉子表，虚拟id相同，自增id小于等于当前id的记录
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> getOtnBefore(@Param(value = "map") Map map);

	/**
	 * 更新otn 交叉连接表
	 * 
	 * @param map
	 */
	public void updateOtnCrs(@Param(value = "map") Map map);

	/**
	 * 跟新otn电路表
	 * 
	 * @param map
	 */
	public void updateOtnCir(@Param(value = "map") Map map);

	/**
	 * 更新电路信息表
	 * 
	 * @param map
	 */
	public void updateOtnInfo(@Param(value = "map") Map map);
	
	/**
	 * 往虚拟交叉路由表中插入信息
	 * @param map
	 */
	public void insertOtnCrsVir(@Param(value = "map") Map map);
	
	/**
	 * 往中兴mac临时表中插入数据
	 * @param map
	 */
	public void insertTemp(@Param(value = "map") Map map);
	
	/**
	 * 查询以太网生成临时表
	 * @return
	 */
	public List<Map> selectZTETemp(@Param(value = "map") Map map,@Param(value = "startNum") int startNum,
			@Param(value = "size") int size);
	
	/**
	 * 查询可生成电路的总数
	 * @return
	 */
	public Map selectZTETempCount(@Param(value = "map") Map map);
	
	/**
	 * 查询中兴mac口
	 * @return
	 */
	public List<Map> selectETHPort(@Param(value = "map") Map map,
			@Param(value = "userId") int userId,
			@Param(value = "Define") Map Define,@Param(value = "startNum") int startNum,
			@Param(value = "size") int size);
	
	/**
	 * 查询中兴mac口总数
	 * @return
	 */
	public Map selectETHPortCount(@Param(value = "map") Map map,
			@Param(value = "userId") int userId,
			@Param(value = "Define") Map Define);
	

	/** ********************************wangjian**end************************* */
	/** ################楚##河#############汉##界############################### */
	/** ********************************daihuijun**begin********************** */

	/**
	 * 查询电路信息表
	 * 
	 * @param aCtpId
	 * @return List
	 */
	public List<Map> getCircuitInfo(@Param(value = "map") Map map);

	/**
	 * 选择所有ctp
	 */
	public List<Map<String, Object>> getCtpByNull();

	/**
	 * 根据网元Id查找该网元下所有端口上的时隙Id
	 * 
	 * 
	 * @param int nodeId,
	 * @return list<Map> 代码示例：getCtpByNe(103); 返回值示例：[{BASE_SDH_CTP_ID=1},
	 *         {BASE_SDH_CTP_ID=2}]
	 */

	public List<Map<String, Object>> getCtpByNe(
			@Param(value = "nodeId") int nodeId);

	/**
	 * 根据ptpId查找该端口上所有的时隙id
	 * 
	 * @param int nodeId,
	 * @return list<Map> 代码示例：getCtpByPort(103); 返回值示例：[{BASE_SDH_CTP_ID=1},
	 *         {BASE_SDH_CTP_ID=2}]
	 */

	public List<Map<String, Object>> getCtpByPort(
			@Param(value = "nodeId") int nodeId);

	/**
	 * 根据A/Z端的时隙Id查找出所有以A/Z为两端的电路
	 * 
	 * @param HashMap
	 *            map:{aList=[actpId1,actpId2...],zList=[zctpId1,zctpId2...]}
	 * @return map:
	 */
	public List<Map> getCircuitInfoByCtp(@Param(value = "map") Map map);

	/**
	 * 根据A/Z端的时隙Id查找出所有以A/Z为两端的电路的总数
	 * 
	 * @param map
	 * @return
	 */

	public Map getptpCircuitTotal(@Param(value = "map") Map map);

	/**
	 * 查询sdh电路路由详细信息
	 */

	public List<Map> getCircuitRoute(@Param(value = "circuitId") int circuitId);
	/**
	 * 查询sdh电路路由topo图信息
	 */
	public List<Map> getCircuitRouteTopo(@Param(value = "circuitId") int circuitId);
	/**
	 * 查询ptn电路路由topo图信息
	 */
	public List<Map> getPtnCircuitRouteTopo(@Param(value = "circuitId") int circuitId);
	
	/**
	 * 根据id查询ptn电路信息
	 * @param circuitId
	 * @return
	 */
	public List<Map> getPtnCirById(@Param(value = "circuitId") int circuitId);
	
	/**
	 * 查询sdh电路路由topo图信息
	 */
	public List<Map> getOtnCircuitRouteTopo(@Param(value = "circuitId") int circuitId);

	public List<Map> getPortInfo(@Param(value = "portId") int portId);
	/**
	 * 根据父电路id查询出子电路的A、Z端时隙
	 */

	public List<Map> getSubCircuit(@Param(value = "map") Map map);

	/**
	 * 获取网元名称
	 */

	public Map getNeName(@Param(value = "map") Map map);

	/**
	 * 获取网元、板卡名称
	 */

	public Map getUnitName(@Param(value = "map") Map map);

	/**
	 * 获取网元、板卡、端口名称
	 */

	public Map getPortName(@Param(value = "map") Map map);

	/**
	 * 相关性查询
	 */

	public List<Map> selectCircuitAbout(@Param(value = "map") Map map);

	/**
	 * 获得相关性查询总数
	 */

	public Map circuitAboutTotal(@Param(value = "map") Map map);
	/**
	 * 查询链路在页面显示
	 * @param map
	 * @return
	 */
	public List<Map> selectLinks(@Param(value = "map") Map map);
	public List<Map> queryLinksById(@Param(value="map")Map map);
	/**
	 * 查询链路用于导出
	 * @param map
	 * @return
	 */
	public List<Map> getExportLinks(@Param(value = "map") Map map);
	public List<Map> queryExportLinksById(@Param(value="map")Map map);

	public Map linksTotal(@Param(value = "map") Map map);

	/**
	 * 查询otn的虚拟交叉的下一跳
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> getNextOtnCrs(@Param(value = "map") Map map);

	/**
	 * 根据电路编号查询所有的需要显示sdh路由的电路id
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> getCircuitBycircuitNo(@Param(value = "map") Map map);
	
	/**
	 * 根据电路编号查询所有的需要显示ptn路由的电路id
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> getPtnCircuitBycircuitNo(@Param(value = "map") Map map);
	
	/**
	 * 根据电路编号查询所有的需要显示otn路由的电路id
	 * 
	 * @param map
	 * @return
	 */
	public List<Map>getOtnCircuitBycircuitNo(@Param(value = "map") Map map);

	/**
	 * get linkInfo in the form
	 * of("emsName-neName-rackNo-shelfNO-slotNo-portNo")
	 */
	public List<Map> getLinksInfo(@Param(value = "map") Map map);

	/**
	 * 按电路Id获取电路详细信息
	 */
	public List<Map> getCirInfoById(@Param(value = "map") Map map);
	/**
	 * 按电路Id获取ptn电路详细信息
	 */
	public List<Map> getPtnCirInfoById(@Param(value = "map") Map map);
	

	/**
	 * 端到端查询以太网
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> getEthInfoByPtp(@Param(value = "map") Map map);

	/**
	 * 获取端到端查询一台网电路总条数
	 * 
	 * @param map
	 * @return
	 */
	public Map getEthTotalByPtp(@Param(value = "map") Map map);

	/**
	 * 插入单条链路
	 */
	public void insertSingleLink(@Param(value = "map") Map map);

	/**
	 * 查询端口上是否有链路
	 */
	public Map hasLinkOnPtp(@Param(value = "map") Map map);

	public Map hasLinkOnPtps(@Param(value = "map") Map map);

	/**
	 * 批量插入链路
	 */
	public void insertLinksfromexcel(@Param(value = "map") Map map);

	/**
	 * @@@分权分域到网元@@@
	 * 查询生成虚拟交叉的起点交叉
	 * 
	 * @param isEnd
	 *            ptp是边界点
	 * @param isV
	 *            自身不是虚拟交叉
	 * @return
	 */
	public List<Map> getOtnCrs(@Param(value = "map") Map map, @Param(value = "start") int start,
			@Param(value = "limit") int limit,
			@Param(value = "userId") int userId,
			@Param(value = "Define") Map Define);
	
	/**
	 * 查询生成otn电路的虚拟交叉
	 * @param map
	 * @return
	 */
	public List<Map> getOtnCrsCir(@Param(value = "map") Map map,@Param(value = "start") int start,
			@Param(value = "limit") int limit);
	
	/**
	 * 查询生成电路的虚拟交叉数目
	 * @param map
	 * @return
	 */
	public Map getOtnCrsCirTotal(@Param(value = "map") Map map);

	/**
	 * 更新单条链路
	 */
	public void updateSingleLink(@Param(value = "map") Map map);

	/**
	 * 删除链路
	 * 
	 * @param map
	 */
	public void deleteLinks(@Param(value = "map") Map map);

	/**
	 * 交叉连接查询结果显示（多表查询）
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> selectCrossConnect(@Param(value = "map") Map map);

	/**
	 * 交叉连接查询总数（多表查询）
	 * 
	 * @param map
	 * @return
	 */

	public Map crossConnectTotel(@Param(value = "map") Map map);

	/**
	 * 根据电路号获取该电路的路由详情
	 * 
	 * @param circuitId
	 * @return
	 */
	public List<Map> getOtnCircuitRoute(
			@Param(value = "circuitId") int circuitId);

	/**
	 * 根据电路号获取该电路的路由详情
	 * 
	 * @param circuitId
	 * @return
	 */
	public List<Map> getPtnCircuitRoute(
			@Param(value = "circuitId") int circuitId);
	/**
	 * 查询朗讯内部的ftp-ftp的交叉连接
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> getInnerCrs(@Param(value = "map") Map map);

	/**
	 * 根据朗讯的内部ftp-ftp类型的交叉连接查询其两端的交叉连接
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> getCrsByInnerCrs(@Param(value = "map") Map map);
	
	public List<Map> getCtpIdByPortNo(@Param(value="map")Map map);
	/**
	 * 查询网元内部路由
	 */
	public List<Map> getInnerRoute(@Param(value="map")Map map);
	/**
	 * 查询指定电路id下所有网元和端口id
	 */
	public	List<Map<String,Integer>>getNeAndPortByCirNo(@Param(value="map")Map map);
	/**
	 * 查询ptn指定电路id下所有网元和端口id
	 */
	public	List<Map<String,Integer>>getPtnNeAndPortByCirNo(@Param(value="map")Map map);
	/**
	 * 查询电路两端端口
	 * @param map
	 * @return
	 */
	public List<Map<String ,Integer>>getEndPtpsByCirNo(@Param(value="map")Map map);
	
	/**
	 * 查询电路端口所属的板卡id
	 * @param map
	 * @return
	 */
	public List<Map>selectUnitId(@Param(value="listptp")List<Integer> ptpList);
	
	
	public List<Map<String,Integer>>getPtpByProId(@Param(value="nodeId")Integer nodeId);
	/**
	 * 根据link两端信息查询网名称网管号
	 * @param map
	 * @return
	 */
	public List<Map>getNeInfoByLink(@Param(value="map")Map map);
	
	/**
	 * 根据ctpId确定交叉连接Id
	 * @param ctpId 时隙Id
	 * @param serviceType 电路类型。1：sdh、2：以太网、3：otn
	 * @return
	 */
	public List getCrsByCtpId(@Param(value="ctpId")Object ctpId,@Param(value="serviceType")Object serviceType);
	/** ********************************daihuijun**end************************ */

	/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~333~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~**/
	/**
	 * 根据电路编号列表查询所有的需要显示sdh路由的电路id
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> getCircuitBycircuitNoList(@Param(value = "noList") List<Integer> noList);
	
	/**
	 * 根据电路编号查询所有的需要显示otn路由的电路id
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> getOtnCircuitBycircuitNoList(@Param(value = "noList") List<Integer> noList);
	
	public void cancelRelateFiber(@Param(value = "linkId") int linkId,
			@Param(value = "linkId_") int linkId_); 
	//关联A端光纤
	public void  relateFiber(@Param(value = "map") Map<String,Object>map);
	
	public  Map<String,Object> getRelateInfo(@Param(value = "aNodeId") int aNodeId,
			@Param(value = "zNodeId") int zNodeId); 
	
	
	// 中兴ptn电路 开始。。。。。。。。。。
	
	/**
	 * 过滤查询符合ptn起始电路的端口
	 * @return
	 */
	public List<Map> getPtnPtp(@Param(value = "map") Map map,
			@Param(value = "start") int start,
			@Param(value = "limit") int limit,
			@Param(value = "userId") int userId,
			@Param(value = "Define") Map Define);
	
	/**
	 * 过滤查询符合ptn起始电路的端口的总数
	 * @return
	 */
	public int getPtnPtpCount(@Param(value = "map") Map map,
			@Param(value = "userId") int userId,
			@Param(value = "Define") Map Define);
	
	
	/**
	 *  6X00 从bingpath表获取ftp
	 * @param map
	 * @return
	 */
	public List<Map> getFtpFromBingding(@Param(value = "map") Map<String,Object> map);
	
	/**
	 * 6X00 从vb表获取ftp
	 * @param map
	 * @return
	 */
	public List<Map> getFtpfromVb(@Param(value = "map") Map<String,Object> map);
	
	/**
	 * 根据pw_id获取纬线id
	 * @param map
	 * @return
	 */
	public List<Map> getPwByPtp(@Param(value = "map") Map<String,Object> map);
	
	/**
	 * 根据pw_id获取纬线id
	 * @param map
	 * @return
	 */
	public List<Map> getPwByPw(@Param(value = "map") Map<String,Object> map);
	
	
	/**
	 * 往ptn临时表中ptp和pw对应关系
	 * @param map
	 */
	public void insertPtnTemp(@Param(value = "map") Map map);
	
	/**
	 * 9004 获取Ftp端口
	 * @param map
	 * @return
	 */
	public List<Map> getPtpFromVb(@Param(value = "map") Map<String,Object> map);
	
	/**
	 * 获取ptn电路临时表的数据
	 * @param map
	 * @return
	 */
	public List<Map> getptnTemp(@Param(value = "map") Map<String,Object> map);
	
	/**
	 * 获取ptn电路临时表的总数
	 * @param map
	 * @return
	 */
	public int getptnTempCount(@Param(value = "map") Map<String,Object> map);
	
	/**
	 * 根据纬线的值 去路由表 查隧道,且端口号相同
	 * @param map
	 * @return
	 */
	public List<Map> getTunelNameFromCtp(@Param(value = "map") Map<String,Object> map);
	
	/**
	 * 根据纬线的值 去路由表 查隧道,且端口号相同
	 * @param map
	 * @return
	 */
	public List<Map> getTunelFromRoute(@Param(value = "map") Map<String,Object> map);
	
	/**
	 * 用belongtrail 号  查出该条路由所有的相关隧道
	 * @param map
	 * @return
	 */
	public List<Map> getTunelbyName(@Param(value = "map") Map<String,Object> map);
	
	/**
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> getlinkByAZ(@Param(value = "map") Map<String,Object> map);
	
	/**
	 * 逆向查找结束纬线
	 * @param map
	 * @return
	 */
	public List<Map> getLastPtnPw(@Param(value = "map") Map<String,Object> map);
	
	/**
	 * 查询ptnctp
	 * @param map
	 * @return
	 */
	public List<Map> getPtnCtp(@Param(value = "map") Map<String,Object> map);
	
	
	/**
	 * 临时表中查出另一端电路口
	 * @param map
	 * @return
	 */
	public List<Map> getLastPtnPtp(@Param(value = "map") Map<String,Object> map);
	
	/**
	 * 查看当前电路是否已经存在
	 * @param map
	 * @return
	 */
	public List<Map> getPtnInfo(@Param(value = "map") Map<String,Object> map);
	
	/**
	 * 更新电路数
	 * @param map
	 */
	public void updatePtnInfo(@Param(value = "map") Map<String,Object> map);
	
	/**
	 * 向ptn info 表中插入数据
	 * @param map
	 */
	public void insertPtnInfo(@Param(value = "map") Map map);
	
	/**
	 * 向ptn cir 表中插入数据
	 * @param map
	 */
	public void insertPtnCir(@Param(value = "map") Map map);
	
	/**
	 *  向ptn 路由  表中  插入数据
	 * @param map
	 */
	public void insertPtnRoute(@Param(value = "map") Map map);
	
	/**
	 * 删除电路临时表的所有数据
	 * @param map
	 */
	public void deleteAllTemp(@Param(value = "map") Map map);
	
	/**
	 * 删除某条临时表数据
	 * @param map
	 */
	public void deletePtnTemp(@Param(value = "map") Map map);
	
	/**
	 * ptn电路端到端查询
	 * @param map
	 * @return
	 */
	public List<Map> getPtnInfoByPtp(@Param(value = "map") Map<String,Object> map);
	
	/**
	 * ptn电路端到端查询总数
	 * @param map
	 * @return
	 */
	public Map getPtnInfoByPtpTotal(@Param(value = "map") Map<String,Object> map);
	
	/**
	 * ptn电路相关性查询
	 * @param map
	 * @return
	 */
	public List<Map> selectPtnCircuitAbout(@Param(value = "map") Map<String,Object> map);
	
	/**
	 * ptn电路相关性查询总数
	 * @param map
	 * @return
	 */
	public Map selectPtnCircuitAboutCount(@Param(value = "map") Map<String,Object> map);
	
	
	/**
	 * 中兴U31otn电路符合条件的交叉连接
	 * @param map
	 * @return
	 */
	public List<Map> selectZteOtn(@Param(value = "map") Map map, @Param(value = "start") int start,
			@Param(value = "limit") int limit,
			@Param(value = "userId") int userId,
			@Param(value = "Define") Map Define);
	
	/**
	 * 中兴U31otn电路符合条件的交叉连接总数
	 * @param map
	 * @return
	 */
	public Map selectZteOtnCount(@Param(value = "map") Map map,
			@Param(value = "userId") int userId,
			@Param(value = "Define") Map Define);
	
	/**
	 * 中兴U31otn电路 下一跳交叉连接
	 * @param map
	 * @return
	 */
	public List<Map> selectZteOtnNextCrs(@Param(value = "map") Map map);
	
	/**
	 * 验证交叉连接是否是起点
	 * @param map
	 * @return
	 */
	public List<Map> selectIsEndPort(@Param(value = "map") Map map);
	

	/**
	 * 获取符合条件的中兴U31ptp端口总数
	 * @param map
	 * @return
	 */
	public Map selectU31EthPortCount(@Param(value = "map") Map map,@Param(value = "userId") int userId,
			@Param(value = "Define") Map Define);
	
	/**
	 * 获取符合条件的中兴U31ptp端口
	 * @param map
	 * @return
	 */
	public List<Map> selectU31EthPort(@Param(value = "map") Map map,@Param(value = "userId") int userId,
			@Param(value = "Define") Map Define,@Param(value = "startNum") int startNum,
			@Param(value = "size") int size);
	
	/**
	 * 根据bangdingid查询数据
	 * @param map
	 * @return
	 */
	public List<Map> selectFromBangByBangID(@Param(value = "map") Map map);
	
	/**
	 * 根据网元id查询vb
	 * @param map
	 * @return
	 */
	public List<Map> selectFromVbByNeId(@Param(value = "map") Map map);
	
	/**
	 * 根据ftpid，查询出绑定的ftpc
	 * @param map
	 * @return
	 */
	public List<Map> selectFromVbListByFtpId(@Param(value = "map") Map map);
	
	/**
	 * 根据ftp去bangdingPatn表中查询对应Ftp的bangdingID
	 * @param map
	 * @return
	 */
	public List<Map> selectFromBangByPtpId(@Param(value = "map") Map map);
	
	/**
	 * 根据baseptpId查询ctp信息
	 * @param map
	 * @return
	 */
	public List<Map> selectFromBangdingByPTp(@Param(value = "map") Map map);
	
	/**
	 * 查询符合条件跨类型link电路的总数
	 * @param map
	 * @return
	 */
	public Map selectLinkFromJiheCount(@Param(value = "map") Map map);
	
	/**
	 * 查询符合条件跨类型link电路
	 * @param map
	 * @return
	 */
	public List<Map> selectLinkFromJihe(@Param(value = "map") Map map);
	
	/**
	 * 用端口去匹配符合的otn完整电路
	 * @param map
	 * @return
	 */
	public List<Map> selectOtnCirByAZ(@Param(value = "map") Map map);
	
	/**
	 * 用端口去匹配资源link表
	 * @param map
	 * @return
	 */
	public List<Map> selectLinkFromJiheByPort(@Param(value = "map") Map map);
	
	/**
	 * 绑定otn电路和link的关系
	 * @param map
	 */
	public void updateLinkCir(@Param(value = "map") Map map);
	
	/**
	 * 查询所有外部link
	 * @return
	 */
	public List<Map> selectLink();
	
	/**
	 * 资源稽核表中查询起点相同，z端不同的link
	 * @param map
	 * @return
	 */
	public List<Map> selectPortA(@Param(value = "map") Map map);
	
	/**
	 * 资源稽核表中查询z点相同，a端不同的link
	 * @param map
	 * @return
	 */
	public List<Map> selectPortZ(@Param(value = "map") Map map);
	
	/**
	 * 查询符合条件的完整电路
	 * @param map
	 * @return
	 */
	public List<Map> selectOtnCirByaz(@Param(value = "aport")List aport,@Param(value = "zport")List zport);
	
	/**
	 * 默认is_del = 0
	 * @param tableName 表名
	 * @param columnName 列名
	 * @param columnValue 列值
	 * @return
	 */
	public List<Map> selectTableByColumn(@Param(value = "tableName")String tableName,@Param(value = "columnName")String columnName,@Param(value = "columnValue")Object columnValue);
	
	/**
	 * 不默认is_del = 0
	 * @param tableName 表名
	 * @param columnName 列名
	 * @param columnValue 列值
	 * @return
	 */
	public Map selectCountTableByColumn(@Param(value = "tableName")String tableName,@Param(value = "columnName")String columnName,@Param(value = "columnValue")Object columnValue);
	
	
	/**
	 *  每次查询出一条符合条件的数据，不默认is_del = 0
	 * @param tableName
	 * @param columnName
	 * @param columnValue
	 * @return
	 */
	public Map selectTableByColumnForOne(@Param(value = "tableName")String tableName,@Param(value = "columnName")String columnName,@Param(value = "columnValue")Object columnValue);

	/**
	 * 删除符合条件的数据
	 * @param tableName
	 * @param columnName
	 * @param columnValue
	 */
	public void deleteTableByColumn(@Param(value = "tableName")String tableName,@Param(value = "columnName")String columnName,@Param(value = "columnValue")Object columnValue);

	/**
	 * 获取一条符合电路生成的数据
	 * @return
	 */
	public List<Map> selectOnefromFdfrList(@Param(value = "map") Map map,@Param(value = "userId") int userId,
			@Param(value = "Define") Map Define);
	
	/**
	 * 统计符合电路生成数量的总数
	 * @return
	 */
	public Map selectCountfromFdfrList(@Param(value = "map") Map map,@Param(value = "userId") int userId,
			@Param(value = "Define") Map Define);

	/**
	 * 根据id查询出电路的az两端
	 * @param fdrId
	 * @return
	 */
	public List<Map> selectFdfrList(@Param(value = "fdrId")Object fdrId);
	
	/**
	 * 从linkfdfr表中查询出和端口匹配的ctp
	 * @return
	 */
	public List<Map> selectPtpfromlinkFdfr(@Param(value = "fdrId")Object fdrId,@Param(value = "ptpId")Object ptpId);
	
	public void updateTableByColumn(@Param(value = "tableName")String tableName,
									@Param(value = "upName")String upName,
									@Param(value = "upValue")Object upValue,
									@Param(value = "columnName")String columnName,
									@Param(value = "columnValue")Object columnValue);
	
	public void updateEthTableByColumn(@Param(value = "tableName")String tableName,
			@Param(value = "upName")String upName,
			@Param(value = "upValue")Object upValue,
			@Param(value = "columnName")String columnName,
			@Param(value = "columnValue")Object columnValue);
	
	public void updateEthCir(@Param(value = "tableName")String tableName,
			@Param(value = "upName")String upName,
			@Param(value = "upValue")Object upValue,
			@Param(value = "columnName")String columnName,
			@Param(value = "columnValue")Object columnValue);
	
	
	/**
	 * 根据交叉连接查询所在的sdh电路
	 * @param crsId
	 * @return
	 */
	public List<Map> selectSdhDeleteCirByCrs(@Param(value = "crsId")Object crsId);
	
	/**
	 * 根据交叉连接查询所在的otn电路
	 * @param crsId
	 * @return
	 */
	public List<Map> selectOtnDeleteCirByCrs(@Param(value = "crsId")Object crsId);
	
	public List<Map> selectSdhDeleteCirByLink(@Param(value = "crsId")Object crsId);
	
	public List<Map> selectOtnDeleteCirByLink(@Param(value = "crsId")Object crsId);
	
	/**
	 * 查询符合条件的华为eth子电路
	 * @param map
	 * @return
	 */
	public List<Map> getHwEthFromInfo(@Param(value = "map") Map map,@Param(value = "start") int start,@Param(value = "limit") int limit);
	
	/**
	 * 查询符合条件的华为eth子电路的总数
	 * @param int
	 * @return
	 */
	public int getHwEthFromInfoCount(@Param(value = "map") Map map);
	
	
}