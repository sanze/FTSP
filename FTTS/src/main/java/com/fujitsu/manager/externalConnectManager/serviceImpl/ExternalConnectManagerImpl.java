package com.fujitsu.manager.externalConnectManager.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.IService.IAreaManagerService;
import com.fujitsu.IService.IResourceCableManagerService;
import com.fujitsu.common.CommonException;
import com.fujitsu.dao.mysql.CommonMapper;
import com.fujitsu.dao.mysql.ExternalConnectMapper;
import com.fujitsu.manager.externalConnectManager.service.ExternalConnectManagerService;

@Service
@Transactional(rollbackFor=Exception.class)
public class ExternalConnectManagerImpl extends ExternalConnectManagerService {

	@Resource
	private ExternalConnectMapper externalConnectMapper;
	@Resource
	private CommonMapper commonMapper;
	@Resource
	private IResourceCableManagerService resourceCableImpl;
	@Resource
	private IAreaManagerService areaManagerImpl;
	
	@Override
	public Map<String, Object> getCableList(int stationId) throws CommonException {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try {
			int total = 0;
			List<Map<String,Object>> rows = new ArrayList<Map<String,Object>>();
			String staName = "";			
			// 获取系统内的所有光缆段信息
			Map<String,Object> cableMap = resourceCableImpl.getCableList(new HashMap<String,Object>(), 0, 0);
			// 获取光缆段计数
			if (cableMap.get("total") != null) {
				total = Integer.valueOf(cableMap.get("total").toString());
			}
			// 存在光缆段信息时再进行处理
			if (total > 0) {
				// 获取光缆段信息的数据行
				List<Map<String,Object>> cableRows = (List<Map<String,Object>>) cableMap.get("rows");
				// 获取局站名称
				Map<String,Object> staMap = commonMapper.selectTableById("t_resource_station", "RESOURCE_STATION_ID", stationId);
				staName = (staMap != null) && (staMap.get("STATION_NAME") != null )? staMap.get("STATION_NAME").toString() : "";
				// 筛选与指定局站相关的光缆段信息
				for (Map<String,Object> row : cableRows) {
					if (staName.equals(row.get("A_END_STATION_NAME").toString()) ||
							staName.equals(row.get("Z_END_STATION_NAME").toString())) {
						rows.add(row);
					}
				}
				// 指定局站的光缆段为空时的处理
				if (rows.isEmpty()) {
					Map<String,Object> row = new HashMap<String,Object>();
					row.put("CABLE_ID", -1);
					row.put("CABLE_NAME_FTTS", "无");
					rows.add(row);
				}
			} else {
				Map<String,Object> row = new HashMap<String,Object>();
				row.put("CABLE_ID", -1);
				row.put("CABLE_NAME_FTTS", "无");
				rows.add(row);	
			}
			// 组织返回结果			
			resultMap.put("total", rows.size());
			resultMap.put("rows", rows);
			
		} catch (Exception e) {
			throw new CommonException(e, -1, "查询指定局站的相关光缆段信息失败！");
		}
		return resultMap;
	}

	@Override
	public Map<String, Object> getFiberListByCableId(int cableId)
			throws CommonException {
		if (cableId < 0) {
			Map<String,Object> result = new HashMap<String,Object>();
			result.put("total", -1);
			result.put("rows", "");
			return result;
		}
		return resourceCableImpl.getFiberListByCableId(cableId, 0, 0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getRcListByStationId(int stationId)
			throws CommonException {
		Map<String,Object> result = new HashMap<String,Object>();
		try {
			// 查询指定局站下的测试设备，返回字段信息：RC_ID,NUMBER,NAME
			List<Map<String,Object>> rcs = externalConnectMapper.getRcInfoListByStationId(stationId);
			// 组织返回结果
			if (rcs.isEmpty()) {
				Map<String,Object> rc = new HashMap<String,Object>();
				rc.put("RC_ID", -1);
				rc.put("NUMBER", "无");
				rc.put("NAME", "无");
				rcs = new ArrayList<Map<String,Object>>();
				rcs.add(rc);
			}
			result.put("total", rcs.size());
			result.put("rows", rcs);

		} catch (Exception e) {
			throw new CommonException(e, -1, "查询指定局站的测试设备信息失败！");
		}
		return result;
	}

	@Override
	public Map<String, Object> getUnitListByRcId(int rcId)
			throws CommonException {
		Map<String,Object> result = new HashMap<String,Object>();
		if (rcId < 0) {
			result.put("total", 0);
			result.put("rows", "");
			return result;
		}
			
		try {
			// 查询指定测试设备的单元盘信息（OTDR和OSW）
			List<Map<String,Object>> units = externalConnectMapper.getUnitListByRcId(rcId);
			for (Map<String,Object> unit : units) {
				if ("OTDR".equals(unit.get("NAME"))) {
					unit.put("PORT_IDS","");
				} else {
					// 如是OSW单元盘，则组织其端口ID信息
					int unitId = Integer.valueOf(unit.get("UNIT_ID").toString());
					List<Map<String,Object>> portInfos = commonMapper.selectTableListById("t_ftts_port","UNIT_ID",unitId,null,null);
					StringBuilder sb = new StringBuilder();
					for (Map<String,Object> port : portInfos) {
						sb.append(port.get("PORT_ID").toString());
						sb.append(",");
					}
					sb.deleteCharAt(sb.length()-1);
					unit.put("PORT_IDS",sb.toString());
				}
			}
			result.put("total", units.size());
			result.put("rows", units);
		} catch (Exception e) {
			throw new CommonException(e, -1, "查询指定设备的单元信息失败！");
		}
		return result;
	}

	@Override
	public void addOneExternalConnect(Map<String, Object> connectData)
			throws CommonException {
		try {
			// 增加指定的外部光纤连接
			externalConnectMapper.addOneExternalConnect(connectData);
			// 增加指定外部光纤连接对应的测试路由
			externalConnectMapper.addTestRoute(connectData);
		} catch (Exception e) {
			throw new CommonException(e, -1, "增加指定的外部光纤连接失败！");
		}
	}

	@Override
	public void delOneExternalConnect(Map<String, Object> connectData)
			throws CommonException {
		try {
			// 先按条件查询外部光纤连接信息
			Map<String,Object> item = externalConnectMapper.getExternalConnectByParam(connectData);
			// 如果此条件的外部光纤连接存在，则按ID删除该外部光纤连接
			if (item != null && item.get("CONNECT_ID") != null) {
				// 删除外部光纤连接对应的测试路由
				externalConnectMapper.delTestRoute(item);
				// 删除指定的外部光纤连接
				externalConnectMapper.delOneExternalConnect(item);	
			}
		} catch (Exception e) {
			throw new CommonException(e, -1, "删除指定的外部光纤连接失败！");
		}
	}

	@Override
	public Map<String, Object> getConnectInfoByStationId(int stationId)
			throws CommonException {
		Map<String,Object> result = new HashMap<String,Object>();
		try {
			// 按局站查询外部连接信息
			List<Map<String,Object>> conns = commonMapper.selectTableListById("t_ftts_connect","STATION_ID",stationId,null,null);
			// 添加光纤的附加信息，并组织返回结果
			if (conns != null) {
				for (Map<String,Object> conn : conns) {
					int connType = Integer.valueOf(conn.get("CONN_TYPE").toString());
					int zEndId = Integer.valueOf(conn.get("Z_END_ID").toString());
					String fiberInfo="";
					if (connType == 2 || connType == 4) {
						Map<String,Object> fiberMap = commonMapper.selectTableById("t_resource_fiber", "RESOURCE_FIBER_ID", zEndId);
						int cableId = Integer.valueOf(fiberMap.get("RESOURCE_CABLE_ID").toString());
						Map<String,Object> cableMap = commonMapper.selectTableById("t_resource_cable", "RESOURCE_CABLE_ID", cableId);
						fiberInfo = cableMap.get("CABLE_NAME").toString() + "(" + fiberMap.get("FIBER_NO").toString() + ")";
					}
					conn.put("FIBER_INFO", fiberInfo);
				}
				// 添加站名信息
				Map<String,Object> staMap = commonMapper.selectTableById("t_resource_station", "RESOURCE_STATION_ID", stationId);
				String stationName = staMap.get("STATION_NAME")!=null ? staMap.get("STATION_NAME").toString() : "";
				result.put("STATION_NAME", stationName);
				result.put("total", conns.size());
				result.put("rows", conns);					
			} else {
				result.put("total", -1);
				result.put("rows", null);
			}
	
		} catch (Exception e) {
			throw new CommonException(e, -1, "按局站查询外部连接信息失败！");
		}
		return result;
	}

	@Override
	public Map<String, Object> getStationList(int parentId, int parentLevel, boolean showAll, String name,
			int start, int limit) throws CommonException {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try {
			List<Map<String, Object>> rows = new ArrayList<Map<String,Object>>();
			
			// 获取系统内的所有光缆段信息
			Map<String,Object> cableMap = resourceCableImpl.getCableList(new HashMap<String,Object>(), 0, 0);
					
			// 获取拥有测试设备的局站信息
			List<Map<String, Object>> stationIdMaps = externalConnectMapper.getStationIdWithRC();
			List<Integer> stationIdListWithRC = new ArrayList<Integer>();
			for (Map<String, Object> stationIdMap : stationIdMaps) {
				stationIdListWithRC.add((Integer) stationIdMap.get("RESOURCE_STATION_ID"));
			}

			// 获取光缆段信息的数据行
			List<Map<String,Object>> cableSectionRows = (List<Map<String,Object>>) cableMap.get("rows");
			
			// 获取局站
			Map<String, Object> staGridMap = areaManagerImpl.getStationGrid(parentId, showAll, name, 0, 0);		
			List<Map<String,Object>> staMaps = (List<Map<String, Object>>) staGridMap.get("rows");
			
			int stationId = 0;
			int cableSectionCount = 0;
			
			for (Map<String, Object> staMap : staMaps) {				
				stationId = (Integer) staMap.get("stationId");
				// 筛选与指定局站相关的光缆段信息
				for (Map<String,Object> row : cableSectionRows) {
					if (stationId == (Integer) row.get("A_END") || stationId == (Integer) row.get("Z_END")) {
						cableSectionCount++;
					}
				}
				// 拥有两个及以上的光缆段或拥有测试设备的局站才会出现在外部连接管理列表中
				if (cableSectionCount >= 2 || stationIdListWithRC.contains(stationId)) {
					rows.add(staMap);
				}
				cableSectionCount = 0;
			}
			
			// 组织返回结果
			int total = rows.size();
			
			List<Map<String, Object>> pageRows = new ArrayList<Map<String,Object>>();
			if (total > limit) {
				for (int i=start; i<start+limit; i++) {
					if (rows.size()-1<i) {
						break;
					}
					pageRows.add(rows.get(i));
				}
				resultMap.put("rows", pageRows);
			} else {
				resultMap.put("rows", rows);
			}
			resultMap.put("total", rows.size());
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, -1, "获取外部光纤连接相关的局站信息失败！");
		}
		
		return resultMap;
	}

}
