package com.fujitsu.IService;

import java.io.File;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;
import com.fujitsu.model.TopoLineModel;

public interface ICircuitManagerService {

	/**
	 * @author wangjian (前半) daihuijun (后半)
	 */

	/** ********************************wangjian**begin************************ */
	/**
	 * @@@分权分域到网元@@@
	 * 获取ems的任务执行情况
	 * 
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getAllEMSTask(Integer userId,int emsGroupId, int start,
			int limit) throws CommonException;

	/**
	 * 获取ems的任务执行情况
	 * 
	 * @return
	 * @throws CommonException
	 */
	public List<Map> getAllGroup(Map map) throws CommonException;

	/**
	 * 局部电路生成SDH电路
	 * 
	 * @param list
	 *            网元id
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> createCircuit(List<Map> list,String processKey)
			throws CommonException;

	/**
	 * 全局电路生成
	 * 
	 * @throws CommonException
	 */
	public void createAllCircuit(String processKey) throws CommonException;

	/**
	 * SDH 电路生成方法
	 */
	public boolean newCircuit(Map map,String id,String sessionId,int countSdh,int countOtn,int userId) throws CommonException;

	/**
	 * 中兴以太网电路生成
	 */
	public boolean createZTECir(Map map,String id,String sessionId,int userId) throws CommonException;
	

	/**
	 * 中兴otn电路生成
	 */
	public boolean createZteOtn(Map mapSelect, String id, String sessionId,int userId) throws CommonException;
	
	/**
	 * 贝尔ptn电路生成
	 */
	public boolean createAluPtnCir(Map mapSelect, String id, String sessionId,int userId) throws CommonException;

	
	
	/**
	 * OTN 电路生成方法
	 */
	public boolean createHWOtnCrs(Map mapSelect, String id, String sessionId,
			int countSdh, int countOtn,int userId);
	
	/**
	 * PTN 电路生成方法
	 */
	public boolean createZtePtnCircuit(Map mapSelect, String id, String sessionId,
			int countSdh, int countOtn,int userId);
	/**
	 * 查询最近生成的电路
	 * 
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> selectCircuitLast(int type, int start, int limit)
			throws CommonException;

	/**
	 * 启用任务
	 * 
	 * @throws CommonException
	 */
	public void setCircuitTaskOn(List<Map> list_map) throws CommonException;

	/**
	 * 挂起任务
	 * 
	 * @throws CommonException
	 */
	public void setCircuitTaskHold(List<Map> list_map) throws CommonException,
			ParseException;

	/**
	 * 设置任务的周期
	 * 
	 * @param map
	 * @throws CommonException
	 */
	public void setCycle(Map map) throws CommonException;

	/**
	 * 设置开始时间
	 * 
	 * @param map
	 * @return
	 * @throws CommonException
	 */
	public Map setBeginTime(Map map) throws CommonException;

	/**
	 * 检查要挂起的任务是否有正在执行的。
	 * 
	 * @param list_map
	 * @return
	 * @throws CommonException
	 */
	public Map checkTask(List<Map> list_map) throws CommonException;

	/**
	 * 新增网管时，给电路生成新增一条任务记录
	 * 
	 * @param emsId
	 */
	public void addCirTask(int emsId,String emsName) throws CommonException;

	/**
	 * 删除网管时，删除任务记录
	 * 
	 * @param emsId
	 */
	public void deleteCircuitTask(int emsId) throws CommonException;

	/**
	 * 更新任务状态，供quartz调用
	 * 
	 * @param map
	 *            需要更新的状态
	 */
	public void updateTask(Map map) throws CommonException;

	/** ********************************wangjian**end************************* */
	/** ################楚##河#############汉##界############################### */
	/** ********************************daihuijun**begin********************** */
	/**
	 * 获取指定网元交叉连接
	 * 
	 * @param map
	 * @return
	 * @throws CommonException
	 */
	public Map selectCrossConnect(Map map, Map to_Map) throws CommonException;

	/**
	 * 存储修改过的电路
	 * 
	 * @param list
	 * @return
	 * @throws CommonException
	 */
	public void modifyCircuit(List<Map> list) throws CommonException;

	/**
	 * 根据端到端信息查询电路
	 * 
	 * @param Map
	 *            coditions
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getCircuitByPtp(Map conditions)
			throws CommonException;

	/**
	 * 查询sdh类型电路的路由详情
	 * 
	 * @param circuitId
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getCircuitRoute(int circuitId)
			throws CommonException;

	/**
	 * 获取网元内部的路由
	 * 
	 * @param map
	 *            {neId=网元id，circuitId=电路Id}
	 * @return
	 * @throws CommonException
	 */
	public Map getInnerRoute(Map map) throws CommonException;

	/**
	 * 查询以太网子电路信息
	 * 
	 * @param parentCir
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getSubCircuitInfo(int parentCir)
			throws CommonException;

	/**
	 * 获取设备名称
	 * 
	 * @param map
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getEquipmentName(Map map) throws CommonException;

	/**
	 * 电路相关性查询
	 * 
	 * @param map
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> selectCircuitAbout(Map map)
			throws CommonException;
	/**
	 * 割接模块的相关性查询，不区分sdh，wdm和以太网电路
	 * @param map
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> selectAllCircuitAbout(Map map)throws CommonException;

	/**
	 * 查询链路
	 * 
	 * @param map
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> selectLinks(Map map) throws CommonException;

	/**
	 * 删除链路
	 */
	public void deleteLinks(Map toMap) throws CommonException;

	/**
	 * 管理电路（修改或添加）
	 */
	public Map<String, Object> manageLink(Map map) throws CommonException;

	/**
	 * 根据电路编号查询所的电路
	 */
	public Map<String, Object> getCircuitBycircuitNo(Map map)
			throws CommonException;
	/**
	 * 根据电路编号查询所的电路
	 */
	public Map<String, Object> getPtnCircuitBycircuitNo(Map map)
			throws CommonException;
	
	public Map<String , Object>getOtnCircuitBycircuitNo(Map map)
			throws CommonException;

	/**
	 * 将链路信息的.xls文件上传至服务器并导入
	 */
	public Map<String, Object> importLinksExcel(File uploadFile,
			String fileName, String path) throws CommonException;

	/**
	 * 通过电路id获取电路详细信息
	 */
	public Map<String, Object> getCirInfoById(Map map) throws CommonException;
	/**
	 * 通过电路id获取ptn电路详细信息
	 */
	public Map<String, Object> getPtnCirInfoById(Map map) throws CommonException;
	
	/**
	 * 获取otn电路详情
	 * @param map
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object>getOtnCirInfoById(Map map) throws CommonException;
	/**
	 * 查询电路路由信息并返回topo图需求数据（sdh电路）
	 * 
	 * @param circuitId
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getRouteTopo(int circuitId)
			throws CommonException;
	/**
	 * 查询电路路由信息并返回topo图需求数据（ptn电路）
	 * 
	 * @param circuitId
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getPtnRouteTopo(int circuitId)
			throws CommonException;
	/**
	 * 查询电路路由信息并返回topo图需求数据（otn电路）
	 * 
	 * @param circuitId
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getOtnRouteTopo(int circuitId)
			throws CommonException;
	/**
	 * 查询otn电路的路由详情
	 * 
	 * @param vCircuit
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getOtnCircuitRoute(int circuitId)
			throws CommonException;
	/**
	 * 查询ptn电路的路由详情
	 * 
	 * @param vCircuit
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getPtnCircuitRoute(int circuitId)
			throws CommonException;
	/**
	 * 将导出结果写到excel中
	 * @param map 查询条件（由前台传入）
	 * @param toMap查询条件（只针对交叉连接查询设置的查询条件）
	 * @return 如果导出成功则返回文件路径，否则返回""
	 * @throws CommonException
	 */
	public String exportExcel(Map map,Map toMap)throws CommonException;
	public String linksOnPageExport(Map map, Map<String, Object> toMap)throws CommonException;
	public String exportRoute(Map map)throws CommonException;
	public Map<String,Object> getLinksByIds (Map map)throws CommonException;
	/**
	 * 根据电路编号，和电路类型获取电路的网元和端口
	 * @param cirNo
	 * @param type
	 * @return
	 */
	public Map<String ,Object>getNeAndPortByCirNo(int cirNo,int type)throws CommonException;
	/**
	 * 根据电路编号，和电路类型获取电路的网元和端口,时隙
	 * 
	 * @param cirNo
	 * @param type
	 * @return
	 */
	public Map<String, Object> getNeAndPortAndCtpByCirNo(int cirNo, int type)
			throws CommonException;
	/**
	 * 告警模块相关性查询
	 * @param map
	 * @return
	 * @throws CommonException
	 */
	public Map<String,Object>getAboutCircuit(Map map)throws CommonException;
	/**
	 * 查询电路中的告警信息
	 * @param jsonString
	 * @param start
	 * @return
	 */
	public Map<String,Object> getCurrentAlarmForCircuit(String jsonString,int start);
	/**
	 * 转化相关性查询信息
	 * @param map
	 */
	public void getAboutCircuitCon(Map map);
	/** ********************************daihuijun**end************************ */

	/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~333~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~**/
	/**
	 * 重保监测用的加载拓扑图--OTN和SDH电路
	 */
	public Map<String, Object> getRouteTopoOtnAndSdh(List<Map<String,Object>> cirList)
			throws CommonException;
	
	/**
	 * 去重复
	 * @param lineList
	 * @return
	 */
	public List<TopoLineModel> deleteRepeatLink(List<TopoLineModel> lineList);
	/**
	 * 光缆光纤的关联
	 * @param linkId
	 * @return
	 */
	public void relateFiber(Map<String,Object>map)throws CommonException;
	/**
	 * 删除和光缆光纤的关联
	 * @param linkId
	 * @return
	 */
	public void cancelRelateFiber(List<Map<String, Object>> list)throws CommonException;
	
	public Map<String, Object>  getRelateInfo(long linkId,int aNodeId,int zNodeId)throws CommonException;
	
	public boolean deleteAllCirAbout(String id,String sessionId,int userId) throws Exception;
}
