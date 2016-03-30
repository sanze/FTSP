package com.fujitsu.manager.resourceManager.serviceImpl;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import jxl.read.biff.BiffException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import com.fujitsu.IService.ICircuitManagerService;
import com.fujitsu.IService.ICommonManagerService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.dao.mysql.CircuitManagerMapper;
import com.fujitsu.dao.mysql.ResourceCircuitManagerMapper;
import com.fujitsu.handler.MessageHandler;
import com.fujitsu.manager.resourceManager.service.ResourceCircuitManagerService;

@Service
//@Transactional(rollbackFor = Exception.class)
public class ResourceCircuitManagerServiceImpl extends
		ResourceCircuitManagerService {

	/**
	 * @author wangjian
	 */

	@Resource
	private ResourceCircuitManagerMapper resourceCircuitManagerMapper;

	@Resource
	private CircuitManagerMapper circuitManagerMapper;

	@Resource
	private ICommonManagerService commonManagerService;
	
	@Resource
	private ICircuitManagerService circuitManagerService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fujitsu.IService.IResourceCircuitManagerService#getNeRelation(java.util.Map,
	 *      int, int)
	 */
	@Override
	public Map<String, Object> getNeRelation(Map map, int start, int limit)
			throws CommonException {
		Map<String, Object> map_re = new HashMap<String, Object>();

		// 查询资源网元关系表的总数
		Map map_to = resourceCircuitManagerMapper.getResourceNeTotal(map);

		// 查询网元关系表的数据
		List<Map> list_map = resourceCircuitManagerMapper.getResourceNe(map,
				start, limit);
		map_re.put("total", Integer.parseInt(map_to.get("total").toString()));
		map_re.put("rows", list_map);

		return map_re;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fujitsu.IService.IResourceCircuitManagerService#modifyResourceNe(java.util.List)
	 */
	@Override
	public CommonResult modifyResourceNe(List<Map> list) throws CommonException {
		// TODO Auto-generated method stub
		CommonResult result = new CommonResult();
		result.setReturnResult(CommonDefine.SUCCESS);
		result.setReturnMessage(MessageHandler
				.getErrorMessage(MessageCodeDefine.CIRCUIT_UPDATE_SUCCESS));
		Map select = null;
		for (Map map : list) {
			// 判断是否已经存在该网元名,资源系统网管标识
			select = hashMapSon("t_resource_ne", "RESOURCE_NE_NAME", map
					.get("RESOURCE_NE_NAME"), null, null, null);
			List<Map> list_res = circuitManagerMapper.getByParameter(select);
			if (list_res != null && list_res.size() > 0) {
				if (list_res.size() > 1
						|| !list_res.get(0).get("RESOURCE_NE_ID").toString()
								.equals(map.get("RESOURCE_NE_ID"))) {
					result.setReturnResult(CommonDefine.FAILED);
					result.setReturnMessage(MessageHandler
							.getErrorMessage(MessageCodeDefine.RES_EXIST));
					return result;
				}

			}

			// 判断是否已经存在该网元名,ftsp网元标识
			select = hashMapSon("t_resource_ne", "FTSP_NE_NAME", map
					.get("FTSP_NE_NAME"), null, null, null);
			List<Map> list_ftsp = circuitManagerMapper.getByParameter(select);
			if (list_ftsp != null && list_ftsp.size() > 0) {
				if (list_ftsp.size() > 1
						|| !list_ftsp.get(0).get("RESOURCE_NE_ID").toString()
								.equals(map.get("RESOURCE_NE_ID"))) {

					result.setReturnResult(CommonDefine.FAILED);
					result.setReturnMessage(MessageHandler
							.getErrorMessage(MessageCodeDefine.FTSP_EXIST));
					return result;
				}
			}
			resourceCircuitManagerMapper.updateResourceNe(map);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fujitsu.IService.IResourceCircuitManagerService#deleteResourceNe(java.util.List)
	 */
	@Override
	public void deleteResourceNe(List<Map> list) throws CommonException {
		for (Map map : list) {
			resourceCircuitManagerMapper.deleteResourceNe(map);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fujitsu.IService.IResourceCircuitManagerService#addResourceNe(java.util.Map)
	 */
	@Override
	public CommonResult addResourceNe(Map map) throws CommonException {
		CommonResult result = new CommonResult();
		result.setReturnResult(CommonDefine.SUCCESS);
		result.setReturnMessage(MessageHandler
				.getErrorMessage(MessageCodeDefine.CIRCUIT_UPDATE_SUCCESS));
		Map select = null;
		// 判断是否已经存在该网元名,资源系统网管标识
		select = hashMapSon("t_resource_ne", "RESOURCE_NE_NAME", map
				.get("RESOURCE_NE_NAME"), null, null, null);
		List<Map> list_res = circuitManagerMapper.getByParameter(select);
		if (list_res != null && list_res.size() > 0) {
			if (list_res.size() > 1
					|| !list_res.get(0).get("RESOURCE_NE_ID").toString()
							.equals(map.get("RESOURCE_NE_ID"))) {
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage(MessageHandler
						.getErrorMessage(MessageCodeDefine.RES_EXIST));
				return result;
			}

		}

		// 判断是否已经存在该网元名,ftsp网元标识
		select = hashMapSon("t_resource_ne", "FTSP_NE_NAME", map
				.get("FTSP_NE_NAME"), null, null, null);
		List<Map> list_ftsp = circuitManagerMapper.getByParameter(select);
		if (list_ftsp != null && list_ftsp.size() > 0) {
			if (list_ftsp.size() > 1
					|| !list_ftsp.get(0).get("RESOURCE_NE_ID").toString()
							.equals(map.get("RESOURCE_NE_ID"))) {

				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage(MessageHandler
						.getErrorMessage(MessageCodeDefine.FTSP_EXIST));
				return result;
			}

		}
		resourceCircuitManagerMapper.addResourceNe(map);
		return result;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fujitsu.IService.IResourceCircuitManagerService#selectResourceCircuit(java.lang.String,
	 *      int, int)
	 */
	@Override
	public Map<String, Object> selectResourceCircuit(String resCirName,
			int start, int limit) throws CommonException {
		Map<String, Object> map_re = new HashMap<String, Object>();

		// 查询资源网元关系表的总数
		int total = resourceCircuitManagerMapper
				.getResourceCircuitTotal(resCirName);

		// 查询网元关系表的数据
		List<Map> list_map = resourceCircuitManagerMapper.getResourceCircuit(
				resCirName, start, limit);
		map_re.put("total", total);
		map_re.put("rows", list_map);

		return map_re;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fujitsu.IService.IResourceCircuitManagerService#resultCount()
	 */
	@Override
	public Map<String, Object> resultCount() throws CommonException {

		Map<String, Object> map = new HashMap<String, Object>();
		List<Object> list = new ArrayList<Object>();
		Map select = null;
		Map tc_1 = new HashMap();
		Map tc_2 = new HashMap();
		Map tc_3 = new HashMap();
		Map tc_4 = new HashMap();

		select = hashMapSon("t_resource_cir", "COMPARE_RESULT",
				CommonDefine.RES_CIR_YES, null, null, " Count(1) as total ");
		List<Map> list_yes = circuitManagerMapper.getByParameter(select);
		int num_yes = 0;
		if (list_yes != null && list_yes.size() > 0) {
			num_yes = Integer.parseInt(list_yes.get(0).get("total").toString());
		}

		select = hashMapSon("t_resource_cir", "COMPARE_RESULT",
				CommonDefine.RES_CIR_NO, null, null, " Count(1) as total ");
		List<Map> list_no = circuitManagerMapper.getByParameter(select);

		int num_no = 0;
		if (list_no != null && list_no.size() > 0) {
			num_no = Integer.parseInt(list_no.get(0).get("total").toString());
		}

		select = hashMapSon("t_resource_cir", "COMPARE_RESULT",
				CommonDefine.RES_CIR_UNCOM, null, null, " Count(1) as total ");
		List<Map> list_un = circuitManagerMapper.getByParameter(select);

		int num_un = 0;
		if (list_un != null && list_un.size() > 0) {
			num_un = Integer.parseInt(list_un.get(0).get("total").toString());
		}

		int total = num_yes + num_no + num_un;

		// 统计未比对的
		tc_1.put("re_name", "未比对");
		tc_1.put("num", num_un);
		tc_1.put("perc", (double) ((num_un * 10000) / total) / 100 + "%");

		list.add(tc_1);
		// 比对成功的
		tc_2.put("re_name", "比对成功");
		tc_2.put("num", num_yes);
		tc_2.put("perc", (double) ((num_yes * 10000) / total) / 100 + "%");
		list.add(tc_2);

		// 比对失败
		tc_3.put("re_name", "比对失败");
		tc_3.put("num", num_no);
		tc_3.put("perc", (double) ((num_no * 10000) / total) / 100 + "%");
		list.add(tc_3);

		// 总计
		tc_4.put("re_name", "总计");
		tc_4.put("num", total);
		tc_4.put("perc", "100%");
		list.add(tc_4);

		map.put("rows", list);
		map.put("total", list.size());
		return map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fujitsu.IService.IResourceCircuitManagerService#selectResCirRoute(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public Map<String, Object> selectResCirRoute(String resCirId,
			String routeNum) throws CommonException {
		Map<String, Object> map = new HashMap<String, Object>();

		Map map_select = hashMapSon("t_resource_cir_route", "RESOURCE_CIR_ID",
				resCirId, "ROUTE_NO", routeNum, null);
		List<Map> list = circuitManagerMapper.getByParameter(map_select);
		map.put("total", list.size());
		map.put("rows", list);

		return map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fujitsu.IService.IResourceCircuitManagerService#getSingleCir(java.lang.String)
	 */
	@Override
	public Map getSingleCir(String resCirId) throws CommonException {
		Map map = new HashMap();

		Map map_select = hashMapSon("t_resource_cir", "RESOURCE_CIR_ID",
				resCirId, null, null, null);
		List<Map> list = circuitManagerMapper.getByParameter(map_select);
		if (list != null && list.size() > 0) {
			map = list.get(0);
		}

		return map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fujitsu.IService.IResourceCircuitManagerService#getFtspRouteNumber(java.lang.String)
	 */
	@Override
	public Map getFtspRouteNumber(String resCirId) throws CommonException {

		Map<String, Object> map = new HashMap<String, Object>();

		List<Map> list = resourceCircuitManagerMapper
				.getFtspRouteNumber(resCirId);
		if (list != null && list.size() > 0) {
			List lt = new ArrayList();
			for (int i = 0; i < list.size(); i++) {
				Map map_temp = new HashMap();
				char c = (char) (97 + i);
				map_temp.put("displayname", "路径-" + c);
				map_temp.put("circuitId", list.get(i).get("CIR_CIRCUIT_ID"));
				lt.add(map_temp);
			}
			map.put("total", lt.size());
			map.put("rows", lt);
		}
		return map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fujitsu.IService.IResourceCircuitManagerService#selectCircuitRoute(java.lang.String)
	 */
	@Override
	public Map selectCircuitRoute(String resCirId) throws CommonException {
		Map<String, Object> map_return = new HashMap<String, Object>();
		List<Map> origianlData = circuitManagerMapper.getCircuitRoute(Integer
				.parseInt(resCirId));
		List<Map> list = new ArrayList<Map>();
		if (origianlData != null && origianlData.size() > 0) {
			for (Map map : origianlData) {

				if (map.get("NE_NAME") != null) {
					Map map_front = new HashMap();

					map_front.put("NE_NAME", map.get("NE_NAME"));
					map_front.put("END_CTP", map.get("A_END_CTP"));
					map_front.put("END_PORT", map.get("A_END_PORT"));
					list.add(map_front);

					Map map_back = new HashMap();
					map_back.put("NE_NAME", map.get("NE_NAME"));
					map_back.put("END_CTP", map.get("Z_END_CTP"));
					map_back.put("END_PORT", map.get("Z_END_PORT"));
					list.add(map_back);
				}
			}
		}
		map_return.put("rows", list);
		map_return.put("total", list.size());

		return map_return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fujitsu.IService.IResourceCircuitManagerService#compareCircuit(java.util.List)
	 */
	@Override
	public Map compareCircuit(List<Map> list) throws CommonException {

		Map<String, Object> map = new HashMap<String, Object>();
		List<Map> list_result = new ArrayList<Map>();

		Map select = null;
		if (list != null && list.size() > 0) {
			// 全局变量，用来衡量多路径时，是否都是相同
			// boolean is_all = true;
			// 路径错误原因

			for (int i = 0; i < list.size(); i++) {
				Map map_task = list.get(i);
				if(map_task.get("ROUTE_NUMBER")!=null&&!map_task.get("ROUTE_NUMBER").toString().isEmpty()){
					for (int j = 1; j <= Integer.parseInt(map_task.get(
							"ROUTE_NUMBER").toString()); j++) {

						boolean is_yes = true;
						String reason = "";

						select = hashMapSon("t_resource_cir_route",
								"RESOURCE_CIR_ID", map_task.get("RESOURCE_CIR_ID"),
								"ROUTE_NO", j, null);
						List<Map> list_route = circuitManagerMapper
								.getByParameter(select);

						// 取路由的az两端去元数据库查询
						if (list_route != null && list_route.size() > 0) {
							// 电路只含有单挑路劲的
							if (Integer.parseInt(map_task.get("ROUTE_NUMBER")
									.toString()) == 1) {

								// a端值分解
								Map map_aport = list_route.get(0);
								Map para_a = getParameter(map_aport);

								// z端分解
								Map map_zport = list_route
										.get(list_route.size() - 1);
								Map para_z = getParameter(map_zport);

								// 匹配a端起点ctp
								String ctp_a = "0";
								List<Map> lt_a = resourceCircuitManagerMapper
										.getCtpId(para_a);
								if (lt_a != null && lt_a.size() > 0) {
									ctp_a = lt_a.get(0).get("BASE_SDH_CTP_ID")
											.toString();
								}

								// 匹配z端起点
								String ctp_z = "0";
								List<Map> lt_z = resourceCircuitManagerMapper
										.getCtpId(para_z);
								if (lt_z.size() > 0) {
									ctp_z = lt_z.get(0).toString();
								}

								select = hashMapSon("t_cir_circuit", "A_END_CTP",
										ctp_a, "Z_END_CTP", ctp_z, null);

								// 正向查询
								List<Map> data_zx = circuitManagerMapper
										.getByParameter(select);
								if (data_zx != null && data_zx.size() > 0) {
									// 已经找到需要比对的电路，可以进行比对

									// 将资源编号存存入端到端电路
									Map map_zx = data_zx.get(0);

									Map info_save = hashMapSon(
											"t_cir_circuit_info", "SOURCE_NO",
											map_task.get("RESOURCE_CIR_NAME"),
											"CIR_CIRCUIT_INFO_ID", map_zx
													.get("CIR_CIRCUIT_INFO_ID"),
											null);
									circuitManagerMapper
											.updateByParameter(info_save);

									// 如果电路路径数不对，则显示比对不等
									if (data_zx.size() != 1) {
										// 当前电路比对结果不符
										map_task.put("COMPARE_RESULT",
												CommonDefine.RES_CIR_NO);
										map_task.put("COMPARE_REASON", "电路路径数不相等");
									} else {
										map = compareSingleCircuit(list_route,
												data_zx);
										if (Integer.parseInt(map
												.get("returnResult").toString()) == CommonDefine.TRUE) {
											map_task.put("COMPARE_RESULT",
													CommonDefine.RES_CIR_YES);
										} else {
											if (Integer.parseInt(map.get(
													"returnResult").toString()) >= 3) {

												map_task.put("DIFF_ROUTE_ID", map
														.get("returnResult"));
											}
											map_task.put("COMPARE_RESULT",
													CommonDefine.RES_CIR_NO);

										}
										map_task.put("COMPARE_REASON", map
												.get("returnMessage"));

									}
									// 存档详细表,开始的时候已经进行了更新或新增
									Map compare_select = hashMapSon(
											"t_resource_compare",
											"RESOURCE_CIR_ID", map_aport
													.get("RESOURCE_CIR_ID"), null,
											null, null);
									// 查询看记录是否已存在
									List<Map> list_save = circuitManagerMapper
											.getByParameter(compare_select);
									// 存在就更新记录
									if (list_save != null && list_save.size() > 0) {
										Map compare_update = hashMapSon(
												"t_resource_compare",
												"CIR_CIRCUIT_INFO_ID",
												map_zx.get("CIR_CIRCUIT_INFO_ID"),
												"RESOURCE_CIR_ID", map_aport
														.get("RESOURCE_CIR_ID"),
												null);
										circuitManagerMapper
												.updateByParameter(compare_update);
									} else {
										// 不存在则插入
										Map compare_insert = new HashMap();
										compare_insert.put("RESOURCE_CIR_ID",
												map_aport.get("RESOURCE_CIR_ID"));
										compare_insert.put("CIR_CIRCUIT_INFO_ID",
												map_zx.get("CIR_CIRCUIT_INFO_ID"));
										resourceCircuitManagerMapper
												.insertResCompare(compare_insert);
									}
									continue;
								} else {
									// 交换条件再次进行比对

									select = hashMapSon("t_cir_circuit",
											"A_END_CTP", ctp_z, "Z_END_CTP", ctp_a,
											null);

									// 正向查询
									List<Map> data_nx = circuitManagerMapper
											.getByParameter(select);

									if (data_nx != null && data_nx.size() > 0) {
										// 已经逆向找到需要比对的电路，可以进行比对

										// 将资源编号存存入端到端电路
										Map map_nx = data_nx.get(0);

										Map info_save = hashMapSon(
												"t_cir_circuit_info", "SOURCE_NO",
												map_task.get("RESOURCE_CIR_NAME"),
												"CIR_CIRCUIT_INFO_ID",
												map_nx.get("CIR_CIRCUIT_INFO_ID"),
												null);
										circuitManagerMapper
												.updateByParameter(info_save);

										// 查看路径数是否一致
										if (data_nx.size() != 1) {
											// 当前电路比对结果不符
											map_task.put("COMPARE_RESULT",
													CommonDefine.RES_CIR_NO);
											map_task.put("COMPARE_REASON",
													"电路路径数不相等");
										} else {
											map = compareSingleCircuit(list_route,
													data_nx);
											if (Integer.parseInt(map.get(
													"returnResult").toString()) == CommonDefine.TRUE) {
												map_task.put("COMPARE_RESULT",
														CommonDefine.RES_CIR_YES);
											} else {
												if (Integer.parseInt(map.get(
														"returnResult").toString()) >= 3) {

													map_task
															.put(
																	"DIFF_ROUTE_ID",
																	map
																			.get("returnResult"));
												}
												map_task.put("COMPARE_RESULT",
														CommonDefine.RES_CIR_NO);

											}
											map_task.put("COMPARE_REASON", map
													.get("returnMessage"));

										}

										// 存档详细表,开始的时候已经进行了更新或新增
										Map compare_select = hashMapSon(
												"t_resource_compare",
												"RESOURCE_CIR_ID", map_aport
														.get("RESOURCE_CIR_ID"),
												null, null, null);
										// 查询看记录是否已存在
										List<Map> list_save = circuitManagerMapper
												.getByParameter(compare_select);
										// 存在就更新记录
										if (list_save != null
												&& list_save.size() > 0) {
											Map compare_update = hashMapSon(
													"t_resource_compare",
													"CIR_CIRCUIT_INFO_ID",
													map_nx
															.get("CIR_CIRCUIT_INFO_ID"),
													"RESOURCE_CIR_ID",
													map_aport
															.get("RESOURCE_CIR_ID"),
													null);
											circuitManagerMapper
													.updateByParameter(compare_update);
										} else {
											// 不存在则插入
											Map compare_insert = new HashMap();
											compare_insert
													.put(
															"RESOURCE_CIR_ID",
															map_aport
																	.get("RESOURCE_CIR_ID"));
											compare_insert
													.put(
															"CIR_CIRCUIT_INFO_ID",
															map_nx
																	.get("CIR_CIRCUIT_INFO_ID"));
											resourceCircuitManagerMapper
													.insertResCompare(compare_insert);
										}

										continue;

									} else {
										// 没有找到相关电路 进行单点匹配
										// 从a端查找
										select = hashMapSon("t_cir_circuit",
												"A_END_CTP", ctp_a, null, null,
												null);

										// 正向单条件查询
										List<Map> data_zd = circuitManagerMapper
												.getByParameter(select);

										// 记录当前的起始点
										if (data_zd.size() > 0) {

											// 将资源编号存存入端到端电路
											Map map_zd = data_zd.get(0);

											Map info_save = hashMapSon(
													"t_cir_circuit_info",
													"SOURCE_NO",
													map_task
															.get("RESOURCE_CIR_NAME"),
													"CIR_CIRCUIT_INFO_ID",
													map_zd
															.get("CIR_CIRCUIT_INFO_ID"),
													null);
											circuitManagerMapper
													.updateByParameter(info_save);

											// 将z端设为不同点
											map_task.put("DIFF_ROUTE_ID", map_zport
													.get("RESOURCE_CIR_ROUTE_ID"));

											// 存档详细表,开始的时候已经进行了更新或新增
											Map compare_select = hashMapSon(
													"t_resource_compare",
													"RESOURCE_CIR_ID",
													map_aport
															.get("RESOURCE_CIR_ID"),
													null, null, null);
											// 查询看记录是否已存在
											List<Map> list_save = circuitManagerMapper
													.getByParameter(compare_select);
											// 存在就更新记录
											if (list_save != null
													&& list_save.size() > 0) {
												Map compare_update = hashMapSon(
														"t_resource_compare",
														"CIR_CIRCUIT_INFO_ID",
														map_zd
																.get("CIR_CIRCUIT_INFO_ID"),
														"RESOURCE_CIR_ID",
														map_aport
																.get("RESOURCE_CIR_ID"),
														null);
												circuitManagerMapper
														.updateByParameter(compare_update);
											} else {
												// 不存在则插入
												Map compare_insert = new HashMap();
												compare_insert
														.put(
																"RESOURCE_CIR_ID",
																map_aport
																		.get("RESOURCE_CIR_ID"));
												compare_insert
														.put(
																"CIR_CIRCUIT_INFO_ID",
																map_zd
																		.get("CIR_CIRCUIT_INFO_ID"));
												resourceCircuitManagerMapper
														.insertResCompare(compare_insert);
											}

											map = compareSingleCircuit(list_route,
													data_zd);
											map_task.put("COMPARE_RESULT",
													CommonDefine.RES_CIR_NO);
											map_task
													.put(
															"COMPARE_REASON",
															"此电路某端与电路不匹配："
																	+ map
																			.get("returnMessage"));

											continue;
										} else {
											// 从a端查找，作为z端查找
											select = hashMapSon("t_cir_circuit",
													"A_END_CTP", ctp_z, null, null,
													null);

											// 正向单条件查询
											List<Map> data_nd = circuitManagerMapper
													.getByParameter(select);

											if (data_nd != null
													&& data_nd.size() > 0) {

												// 将资源编号存存入端到端电路
												Map map_nd = data_nd.get(0);

												Map info_save = hashMapSon(
														"t_cir_circuit_info",
														"SOURCE_NO",
														map_task
																.get("RESOURCE_CIR_NAME"),
														"CIR_CIRCUIT_INFO_ID",
														map_nd
																.get("CIR_CIRCUIT_INFO_ID"),
														null);
												circuitManagerMapper
														.updateByParameter(info_save);

												// 将z端设为不同点
												map_task
														.put(
																"DIFF_ROUTE_ID",
																map_zport
																		.get("RESOURCE_CIR_ROUTE_ID"));

												// 存档详细表,开始的时候已经进行了更新或新增
												Map compare_select = hashMapSon(
														"t_resource_compare",
														"RESOURCE_CIR_ID",
														map_aport
																.get("RESOURCE_CIR_ID"),
														null, null, null);
												// 查询看记录是否已存在
												List<Map> list_save = circuitManagerMapper
														.getByParameter(compare_select);
												// 存在就更新记录
												if (list_save != null
														&& list_save.size() > 0) {
													Map compare_update = hashMapSon(
															"t_resource_compare",
															"CIR_CIRCUIT_INFO_ID",
															map_nd
																	.get("CIR_CIRCUIT_INFO_ID"),
															"RESOURCE_CIR_ID",
															map_aport
																	.get("RESOURCE_CIR_ID"),
															null);
													circuitManagerMapper
															.updateByParameter(compare_update);
												} else {
													// 不存在则插入
													Map compare_insert = new HashMap();
													compare_insert
															.put(
																	"RESOURCE_CIR_ID",
																	map_aport
																			.get("RESOURCE_CIR_ID"));
													compare_insert
															.put(
																	"CIR_CIRCUIT_INFO_ID",
																	map_nd
																			.get("CIR_CIRCUIT_INFO_ID"));
													resourceCircuitManagerMapper
															.insertResCompare(compare_insert);
												}

												map = compareSingleCircuit(
														list_route, data_nd);
												map_task.put("COMPARE_RESULT",
														CommonDefine.RES_CIR_NO);
												map_task
														.put(
																"COMPARE_REASON",
																"此电路某端与电路不匹配："
																		+ map
																				.get("returnMessage"));
												continue;
											} else {
												// 将az颠倒查询
												select = hashMapSon(
														"t_cir_circuit",
														"Z_END_CTP", ctp_a, null,
														null, null);

												// 正向单条件查询
												List<Map> data_z_a = circuitManagerMapper
														.getByParameter(select);

												if (data_z_a.size() > 0) {
													// 将资源编号存存入端到端电路
													Map map_zd = data_z_a.get(0);

													Map info_save = hashMapSon(
															"t_cir_circuit_info",
															"SOURCE_NO",
															map_task
																	.get("RESOURCE_CIR_NAME"),
															"CIR_CIRCUIT_INFO_ID",
															map_zd
																	.get("CIR_CIRCUIT_INFO_ID"),
															null);
													circuitManagerMapper
															.updateByParameter(info_save);
													// 将a端设为不同点
													map_task
															.put(
																	"DIFF_ROUTE_ID",
																	map_aport
																			.get("RESOURCE_CIR_ROUTE_ID"));

													// 存档详细表,开始的时候已经进行了更新或新增
													Map compare_select = hashMapSon(
															"t_resource_compare",
															"RESOURCE_CIR_ID",
															map_aport
																	.get("RESOURCE_CIR_ID"),
															null, null, null);
													// 查询看记录是否已存在
													List<Map> list_save = circuitManagerMapper
															.getByParameter(compare_select);
													// 存在就更新记录
													if (list_save != null
															&& list_save.size() > 0) {
														Map compare_update = hashMapSon(
																"t_resource_compare",
																"CIR_CIRCUIT_INFO_ID",
																map_zd
																		.get("CIR_CIRCUIT_INFO_ID"),
																"RESOURCE_CIR_ID",
																map_aport
																		.get("RESOURCE_CIR_ID"),
																null);
														circuitManagerMapper
																.updateByParameter(compare_update);
													} else {
														// 不存在则插入
														Map compare_insert = new HashMap();
														compare_insert
																.put(
																		"RESOURCE_CIR_ID",
																		map_aport
																				.get("RESOURCE_CIR_ID"));
														compare_insert
																.put(
																		"CIR_CIRCUIT_INFO_ID",
																		map_zd
																				.get("CIR_CIRCUIT_INFO_ID"));
														resourceCircuitManagerMapper
																.insertResCompare(compare_insert);
													}

													map = compareSingleCircuit(
															list_route, data_z_a);
													map_task
															.put(
																	"COMPARE_RESULT",
																	CommonDefine.RES_CIR_NO);
													map_task
															.put(
																	"COMPARE_REASON",
																	"此电路某端与电路不匹配："
																			+ map
																					.get("returnMessage"));

													continue;
												} else {
													// 将az颠倒查询
													select = hashMapSon(
															"t_cir_circuit",
															"Z_END_CTP", ctp_z,
															null, null, null);

													// 正向单条件查询
													List<Map> data_z_z = circuitManagerMapper
															.getByParameter(select);

													if (data_z_z.size() > 0) {

														// 将资源编号存存入端到端电路
														Map map_zd = data_z_z
																.get(0);

														Map info_save = hashMapSon(
																"t_cir_circuit_info",
																"SOURCE_NO",
																map_task
																		.get("RESOURCE_CIR_NAME"),
																"CIR_CIRCUIT_INFO_ID",
																map_zd
																		.get("CIR_CIRCUIT_INFO_ID"),
																null);
														circuitManagerMapper
																.updateByParameter(info_save);
														// 将a端设为不同点
														map_task
																.put(
																		"DIFF_ROUTE_ID",
																		map_aport
																				.get("RESOURCE_CIR_ROUTE_ID"));

														// 存档详细表,开始的时候已经进行了更新或新增
														Map compare_select = hashMapSon(
																"t_resource_compare",
																"RESOURCE_CIR_ID",
																map_aport
																		.get("RESOURCE_CIR_ID"),
																null, null, null);
														// 查询看记录是否已存在
														List<Map> list_save = circuitManagerMapper
																.getByParameter(compare_select);
														// 存在就更新记录
														if (list_save != null
																&& list_save.size() > 0) {
															Map compare_update = hashMapSon(
																	"t_resource_compare",
																	"CIR_CIRCUIT_INFO_ID",
																	map_zd
																			.get("CIR_CIRCUIT_INFO_ID"),
																	"RESOURCE_CIR_ID",
																	map_aport
																			.get("RESOURCE_CIR_ID"),
																	null);
															circuitManagerMapper
																	.updateByParameter(compare_update);
														} else {
															// 不存在则插入
															Map compare_insert = new HashMap();
															compare_insert
																	.put(
																			"RESOURCE_CIR_ID",
																			map_aport
																					.get("RESOURCE_CIR_ID"));
															compare_insert
																	.put(
																			"CIR_CIRCUIT_INFO_ID",
																			map_zd
																					.get("CIR_CIRCUIT_INFO_ID"));
															resourceCircuitManagerMapper
																	.insertResCompare(compare_insert);
														}

														map = compareSingleCircuit(
																list_route,
																data_z_a);
														map_task
																.put(
																		"COMPARE_RESULT",
																		CommonDefine.RES_CIR_NO);
														map_task
																.put(
																		"COMPARE_REASON",
																		"此电路某端与电路不匹配："
																				+ map
																						.get("returnMessage"));
														continue;
													} else {
														map_task
																.put(
																		"COMPARE_RESULT",
																		CommonDefine.RES_CIR_NO);
														map_task.put(
																"COMPARE_REASON",
																"此电路不存在相匹配的电路");
														continue;
													}
												}
											}
										}

									}
								}

							} else {
								// 有多条路经的情况下进行比对

								// 正向查找
								// a端值分解
								Map map_aport = list_route.get(0);
								Map para_a = getParameter(map_aport);

								// z端分解
								Map map_zport = list_route
										.get(list_route.size() - 1);
								Map para_z = getParameter(map_zport);

								// 匹配a端起点ctp
								String ctp_a = "0";
								List<Map> lt_a = resourceCircuitManagerMapper
										.getCtpId(para_a);
								if (lt_a != null && lt_a.size() > 0) {
									ctp_a = lt_a.get(0).get("BASE_SDH_CTP_ID")
											.toString();
								}

								// 匹配z端起点
								String ctp_z = "0";
								List<Map> lt_z = resourceCircuitManagerMapper
										.getCtpId(para_z);
								if (lt_z.size() > 0) {
									ctp_z = lt_z.get(0).toString();
								}

								Map map_b = hashMapSon("t_cir_circuit",
										"A_END_CTP", ctp_a, "Z_END_CTP", ctp_z,
										null);

								// 正向查询
								List<Map> data_mult_zx = circuitManagerMapper
										.getByParameter(map_b);

								if (data_mult_zx != null && data_mult_zx.size() > 0) {

									// 将资源编号存存入端到端电路
									Map map_zd = data_mult_zx.get(0);

									Map info_save = hashMapSon(
											"t_cir_circuit_info", "SOURCE_NO",
											map_task.get("RESOURCE_CIR_NAME"),
											"CIR_CIRCUIT_INFO_ID", map_zd
													.get("CIR_CIRCUIT_INFO_ID"),
											null);
									circuitManagerMapper
											.updateByParameter(info_save);

									// 已经找到需要比对的电路，可以进行比对

									map = compareSingleCircuit(list_route,
											data_mult_zx);
									if (Integer.parseInt(map.get("returnResult")
											.toString()) == CommonDefine.TRUE) {
										if (j == 1L) {
											map_task.put("COMPARE_RESULT",
													CommonDefine.RES_CIR_YES);
										}
									} else {
										is_yes = false;
										reason += map.get("returnMessage")
												.toString()
												+ ";";
										if (Integer.parseInt(map
												.get("returnResult").toString()) >= 3) {
											map_task.put("DIFF_ROUTE_ID", map
													.get("returnResult"));
										}
										map_task.put("COMPARE_RESULT",
												CommonDefine.RES_CIR_NO);
									}
									if (j >= 2L) {
										if (!is_yes) {
											map_task.put("COMPARE_RESULT",
													CommonDefine.RES_CIR_NO);
											map_task.put("COMPARE_REASON", reason);

										}
									} else {
										map_task.put("COMPARE_REASON", map
												.get("returnMessage"));
									}

									// 存档详细表,开始的时候已经进行了更新或新增
									Map compare_select = hashMapSon(
											"t_resource_compare",
											"RESOURCE_CIR_ID", map_aport
													.get("RESOURCE_CIR_ID"), null,
											null, null);
									// 查询看记录是否已存在
									List<Map> list_save = circuitManagerMapper
											.getByParameter(compare_select);
									// 存在就更新记录
									if (list_save != null && list_save.size() > 0) {
										Map compare_update = hashMapSon(
												"t_resource_compare",
												"CIR_CIRCUIT_INFO_ID",
												map_zd.get("CIR_CIRCUIT_INFO_ID"),
												"RESOURCE_CIR_ID", map_aport
														.get("RESOURCE_CIR_ID"),
												null);
										circuitManagerMapper
												.updateByParameter(compare_update);
									} else {
										// 不存在则插入
										Map compare_insert = new HashMap();
										compare_insert.put("RESOURCE_CIR_ID",
												map_aport.get("RESOURCE_CIR_ID"));
										compare_insert.put("CIR_CIRCUIT_INFO_ID",
												map_zd.get("CIR_CIRCUIT_INFO_ID"));
										resourceCircuitManagerMapper
												.insertResCompare(compare_insert);
									}
									// continue;

								} else {
									// 交换条件再次进行比对

									select = hashMapSon("t_cir_circuit",
											"A_END_CTP", ctp_z, "Z_END_CTP", ctp_a,
											null);

									// 正向查询
									List<Map> data_mult_nx = circuitManagerMapper
											.getByParameter(select);

									if (data_mult_nx != null
											&& data_mult_nx.size() > 0) {
										// 已经逆向找到需要比对的电路，可以进行比对

										// 将资源编号存存入端到端电路
										Map map_zd = data_mult_zx.get(0);

										Map info_save = hashMapSon(
												"t_cir_circuit_info", "SOURCE_NO",
												map_task.get("RESOURCE_CIR_NAME"),
												"CIR_CIRCUIT_INFO_ID",
												map_zd.get("CIR_CIRCUIT_INFO_ID"),
												null);
										circuitManagerMapper
												.updateByParameter(info_save);

										// 查看路径数是否一致

										map = compareSingleCircuit(list_route,
												data_mult_nx);
										if (Integer.parseInt(map
												.get("returnResult").toString()) == CommonDefine.TRUE) {
											if (j == 1L) {
												map_task.put("COMPARE_RESULT",
														CommonDefine.RES_CIR_YES);
											}
										} else {
											is_yes = false;
											reason += map.get("returnMessage")
													.toString()
													+ ";";
											if (Integer.parseInt(map.get(
													"returnResult").toString()) >= 3) {
												map_task.put("DIFF_ROUTE_ID", map
														.get("returnResult"));
											}
											map_task.put("COMPARE_RESULT",
													CommonDefine.RES_CIR_NO);
										}
										if (j >= 2L) {
											if (!is_yes) {
												map_task.put("COMPARE_RESULT",
														CommonDefine.RES_CIR_NO);
												map_task.put("COMPARE_REASON",
														reason);

											}
										} else {
											map_task.put("COMPARE_REASON", map
													.get("returnMessage"));
										}

										// 存档详细表,开始的时候已经进行了更新或新增
										Map compare_select = hashMapSon(
												"t_resource_compare",
												"RESOURCE_CIR_ID", map_aport
														.get("RESOURCE_CIR_ID"),
												null, null, null);
										// 查询看记录是否已存在
										List<Map> list_save = circuitManagerMapper
												.getByParameter(compare_select);
										// 存在就更新记录
										if (list_save != null
												&& list_save.size() > 0) {
											Map compare_update = hashMapSon(
													"t_resource_compare",
													"CIR_CIRCUIT_INFO_ID",
													map_zd
															.get("CIR_CIRCUIT_INFO_ID"),
													"RESOURCE_CIR_ID",
													map_aport
															.get("RESOURCE_CIR_ID"),
													null);
											circuitManagerMapper
													.updateByParameter(compare_update);
										} else {
											// 不存在则插入
											Map compare_insert = new HashMap();
											compare_insert
													.put(
															"RESOURCE_CIR_ID",
															map_aport
																	.get("RESOURCE_CIR_ID"));
											compare_insert
													.put(
															"CIR_CIRCUIT_INFO_ID",
															map_zd
																	.get("CIR_CIRCUIT_INFO_ID"));
											resourceCircuitManagerMapper
													.insertResCompare(compare_insert);
										}
										// continue;

									} else {
										// // 没有找到相关电路
										// tjc.setCompareResult(2L);
										// tjc
										// .setCompareReason("此电路(多路径电路)不存在相匹配的电路");
										// continue;

										// 没有找到相关电路 进行单点匹配
										// 从a端查找

										select = hashMapSon("t_cir_circuit",
												"A_END_CTP", ctp_a, null, null,
												null);

										// 正向查询
										List<Map> data_mult_za = circuitManagerMapper
												.getByParameter(select);

										// 记录当前的起始点
										if (data_mult_za != null
												&& data_mult_za.size() > 0) {

											// 将资源编号存存入端到端电路
											Map map_zd = data_mult_zx.get(0);

											Map info_save = hashMapSon(
													"t_cir_circuit_info",
													"SOURCE_NO",
													map_task
															.get("RESOURCE_CIR_NAME"),
													"CIR_CIRCUIT_INFO_ID",
													map_zd
															.get("CIR_CIRCUIT_INFO_ID"),
													null);
											circuitManagerMapper
													.updateByParameter(info_save);

											// 将z端设为不同点
											map = compareSingleCircuit(list_route,
													data_mult_za);
											is_yes = false;
											reason += map.get("returnMessage")
													.toString()
													+ ";";
											// 将a端设为不同点
											map_task.put("DIFF_ROUTE_ID", map_zport
													.get("RESOURCE_CIR_ROUTE_ID"));

											// 存档详细表,开始的时候已经进行了更新或新增
											Map compare_select = hashMapSon(
													"t_resource_compare",
													"RESOURCE_CIR_ID",
													map_aport
															.get("RESOURCE_CIR_ID"),
													null, null, null);
											// 查询看记录是否已存在
											List<Map> list_save = circuitManagerMapper
													.getByParameter(compare_select);
											// 存在就更新记录
											if (list_save != null
													&& list_save.size() > 0) {
												Map compare_update = hashMapSon(
														"t_resource_compare",
														"CIR_CIRCUIT_INFO_ID",
														map_zd
																.get("CIR_CIRCUIT_INFO_ID"),
														"RESOURCE_CIR_ID",
														map_aport
																.get("RESOURCE_CIR_ID"),
														null);
												circuitManagerMapper
														.updateByParameter(compare_update);
											} else {
												// 不存在则插入
												Map compare_insert = new HashMap();
												compare_insert
														.put(
																"RESOURCE_CIR_ID",
																map_aport
																		.get("RESOURCE_CIR_ID"));
												compare_insert
														.put(
																"CIR_CIRCUIT_INFO_ID",
																map_zd
																		.get("CIR_CIRCUIT_INFO_ID"));
												resourceCircuitManagerMapper
														.insertResCompare(compare_insert);
											}

											map_task.put("COMPARE_RESULT",
													CommonDefine.RES_CIR_NO);
											map_task.put("COMPARE_REASON",
													"此电路（多路径）某端与电路不匹配:" + reason);
											// continue;
										} else {
											// 从a端查找，作为z端查找
											select = hashMapSon("t_cir_circuit",
													"A_END_CTP", ctp_z, null, null,
													null);

											// 正向查询
											List<Map> data_mult_zz = circuitManagerMapper
													.getByParameter(select);
											if (data_mult_zz != null
													&& data_mult_zz.size() > 0) {

												// 将资源编号存存入端到端电路
												Map map_zd = data_mult_zx.get(0);

												Map info_save = hashMapSon(
														"t_cir_circuit_info",
														"SOURCE_NO",
														map_task
																.get("RESOURCE_CIR_NAME"),
														"CIR_CIRCUIT_INFO_ID",
														map_zd
																.get("CIR_CIRCUIT_INFO_ID"),
														null);
												circuitManagerMapper
														.updateByParameter(info_save);

												// 将z端设为不同点
												map = compareSingleCircuit(
														list_route, data_mult_zz);
												is_yes = false;
												reason += map.get("returnMessage")
														.toString()
														+ ";";
												// 将a端设为不同点
												map_task
														.put(
																"DIFF_ROUTE_ID",
																map_zport
																		.get("RESOURCE_CIR_ROUTE_ID"));

												// 存档详细表,开始的时候已经进行了更新或新增
												Map compare_select = hashMapSon(
														"t_resource_compare",
														"RESOURCE_CIR_ID",
														map_aport
																.get("RESOURCE_CIR_ID"),
														null, null, null);
												// 查询看记录是否已存在
												List<Map> list_save = circuitManagerMapper
														.getByParameter(compare_select);
												// 存在就更新记录
												if (list_save != null
														&& list_save.size() > 0) {
													Map compare_update = hashMapSon(
															"t_resource_compare",
															"CIR_CIRCUIT_INFO_ID",
															map_zd
																	.get("CIR_CIRCUIT_INFO_ID"),
															"RESOURCE_CIR_ID",
															map_aport
																	.get("RESOURCE_CIR_ID"),
															null);
													circuitManagerMapper
															.updateByParameter(compare_update);
												} else {
													// 不存在则插入
													Map compare_insert = new HashMap();
													compare_insert
															.put(
																	"RESOURCE_CIR_ID",
																	map_aport
																			.get("RESOURCE_CIR_ID"));
													compare_insert
															.put(
																	"CIR_CIRCUIT_INFO_ID",
																	map_zd
																			.get("CIR_CIRCUIT_INFO_ID"));
													resourceCircuitManagerMapper
															.insertResCompare(compare_insert);
												}

												map_task.put("COMPARE_RESULT",
														CommonDefine.RES_CIR_NO);
												map_task.put("COMPARE_REASON",
														"此电路（多路径）某端与电路不匹配:"
																+ reason);
												// continue;
											} else {
												// 将az颠倒查询
												select = hashMapSon(
														"t_cir_circuit",
														"Z_END_CTP", ctp_z, null,
														null, null);

												// 正向查询
												List<Map> data_mult_zf = circuitManagerMapper
														.getByParameter(select);
												if (data_mult_zf != null
														&& data_mult_zf.size() > 0) {

													// 将资源编号存存入端到端电路
													Map map_zd = data_mult_zx
															.get(0);

													Map info_save = hashMapSon(
															"t_cir_circuit_info",
															"SOURCE_NO",
															map_task
																	.get("RESOURCE_CIR_NAME"),
															"CIR_CIRCUIT_INFO_ID",
															map_zd
																	.get("CIR_CIRCUIT_INFO_ID"),
															null);
													circuitManagerMapper
															.updateByParameter(info_save);

													// 将z端设为不同点
													map = compareSingleCircuit(
															list_route,
															data_mult_zf);
													is_yes = false;
													reason += map.get(
															"returnMessage")
															.toString()
															+ ";";
													// 将a端设为不同点
													map_task
															.put(
																	"DIFF_ROUTE_ID",
																	map_aport
																			.get("RESOURCE_CIR_ROUTE_ID"));

													// 存档详细表,开始的时候已经进行了更新或新增
													Map compare_select = hashMapSon(
															"t_resource_compare",
															"RESOURCE_CIR_ID",
															map_aport
																	.get("RESOURCE_CIR_ID"),
															null, null, null);
													// 查询看记录是否已存在
													List<Map> list_save = circuitManagerMapper
															.getByParameter(compare_select);
													// 存在就更新记录
													if (list_save != null
															&& list_save.size() > 0) {
														Map compare_update = hashMapSon(
																"t_resource_compare",
																"CIR_CIRCUIT_INFO_ID",
																map_zd
																		.get("CIR_CIRCUIT_INFO_ID"),
																"RESOURCE_CIR_ID",
																map_aport
																		.get("RESOURCE_CIR_ID"),
																null);
														circuitManagerMapper
																.updateByParameter(compare_update);
													} else {
														// 不存在则插入
														Map compare_insert = new HashMap();
														compare_insert
																.put(
																		"RESOURCE_CIR_ID",
																		map_aport
																				.get("RESOURCE_CIR_ID"));
														compare_insert
																.put(
																		"CIR_CIRCUIT_INFO_ID",
																		map_zd
																				.get("CIR_CIRCUIT_INFO_ID"));
														resourceCircuitManagerMapper
																.insertResCompare(compare_insert);
													}

													map_task
															.put(
																	"COMPARE_RESULT",
																	CommonDefine.RES_CIR_NO);
													map_task.put("COMPARE_REASON",
															"此电路（多路径）某端与电路不匹配:"
																	+ reason);
													// continue;
												} else {
													// 将az颠倒查询
													select = hashMapSon(
															"t_cir_circuit",
															"Z_END_CTP", ctp_a,
															null, null, null);

													// 正向查询
													List<Map> data_mult_fa = circuitManagerMapper
															.getByParameter(select);
													if (data_mult_fa != null
															&& data_mult_fa.size() > 0) {

														// 将资源编号存存入端到端电路
														Map map_zd = data_mult_zx
																.get(0);

														Map info_save = hashMapSon(
																"t_cir_circuit_info",
																"SOURCE_NO",
																map_task
																		.get("RESOURCE_CIR_NAME"),
																"CIR_CIRCUIT_INFO_ID",
																map_zd
																		.get("CIR_CIRCUIT_INFO_ID"),
																null);
														circuitManagerMapper
																.updateByParameter(info_save);

														// 将z端设为不同点
														map = compareSingleCircuit(
																list_route,
																data_mult_fa);
														is_yes = false;
														reason += map.get(
																"returnMessage")
																.toString()
																+ ";";
														// 将a端设为不同点
														map_task
																.put(
																		"DIFF_ROUTE_ID",
																		map_aport
																				.get("RESOURCE_CIR_ROUTE_ID"));

														// 存档详细表,开始的时候已经进行了更新或新增
														Map compare_select = hashMapSon(
																"t_resource_compare",
																"RESOURCE_CIR_ID",
																map_aport
																		.get("RESOURCE_CIR_ID"),
																null, null, null);
														// 查询看记录是否已存在
														List<Map> list_save = circuitManagerMapper
																.getByParameter(compare_select);
														// 存在就更新记录
														if (list_save != null
																&& list_save.size() > 0) {
															Map compare_update = hashMapSon(
																	"t_resource_compare",
																	"CIR_CIRCUIT_INFO_ID",
																	map_zd
																			.get("CIR_CIRCUIT_INFO_ID"),
																	"RESOURCE_CIR_ID",
																	map_aport
																			.get("RESOURCE_CIR_ID"),
																	null);
															circuitManagerMapper
																	.updateByParameter(compare_update);
														} else {
															// 不存在则插入
															Map compare_insert = new HashMap();
															compare_insert
																	.put(
																			"RESOURCE_CIR_ID",
																			map_aport
																					.get("RESOURCE_CIR_ID"));
															compare_insert
																	.put(
																			"CIR_CIRCUIT_INFO_ID",
																			map_zd
																					.get("CIR_CIRCUIT_INFO_ID"));
															resourceCircuitManagerMapper
																	.insertResCompare(compare_insert);
														}

														map_task
																.put(
																		"COMPARE_RESULT",
																		CommonDefine.RES_CIR_NO);
														map_task.put(
																"COMPARE_REASON",
																"此电路（多路径）某端与电路不匹配:"
																		+ reason);
														// continue;
													} else {
														map_task
																.put(
																		"COMPARE_RESULT",
																		CommonDefine.RES_CIR_NO);
														map_task
																.put(
																		"COMPARE_REASON",
																		"此电路（多路径）不存在相匹配的电路");
														// continue;
													}
												}
											}
										}

									}
								}

							}
						}

					}

					list_result.add(map_task);

				
				}

			}
		}

		// 更新电路比对状态
		if (list_result.size() > 0) {
			for (int i = 0; i < list_result.size(); i++) {
				Map result = list_result.get(i);
				Map map_update = new HashMap();
				map_update.put("COMPARE_RESULT", result.get("COMPARE_RESULT"));
				map_update.put("COMPARE_REASON", result.get("COMPARE_REASON"));
				map_update.put("DIFF_ROUTE_ID", result.get("DIFF_ROUTE_ID"));
				map_update
						.put("RESOURCE_CIR_ID", result.get("RESOURCE_CIR_ID"));
				resourceCircuitManagerMapper.updateResCir(map_update);
			}
		}
		map.put("returnResult", CommonDefine.TRUE);
		map.put("returnMessage", MessageHandler
				.getErrorMessage(MessageCodeDefine.RES_COMPARE_SUCCESS));

		return map;

	}

	/**
	 * 根据稽核端口，槽道和时隙，整合查询条件，查出ftsp对应的ctp
	 * 
	 * @param map
	 * @return
	 */
	protected Map getParameter(Map map) {
		Map map_return = new HashMap();
		map_return.put("map.BASE_NE_ID", map.get("map.BASE_NE_ID"));
		map_return.put("UNIT_NO", map.get("UNIT_NO"));
		map_return.put("PORT_NO", map.get("PORT_NO"));
		if (map.get("PORT_VALUE") != null
				&& !map.get("PORT_VALUE").toString().isEmpty()) {
			String[] ctp = map.get("PORT_VALUE").toString().split("-");
			if (ctp.length >= 1) {
				map_return.put("CTP_J_ORIGINAL", ctp[0]);
			}
			if (ctp.length >= 2) {
				map_return.put("CTP_K", ctp[1]);
			}
			if (ctp.length >= 3) {
				map_return.put("CTP_L", ctp[2]);
			}
			if (ctp.length >= 4) {
				map_return.put("CTP_M", ctp[3]);
			}
		}
		return map_return;
	}

	/**
	 * 单条电路稽核
	 * 
	 * @param route_j
	 *            稽核电路路由信息
	 * @param cir_ftsp
	 *            ftsp电路信息
	 * @return
	 * @throws CommonException
	 */
	protected Map<String, Object> compareSingleCircuit(List<Map> route_j,
			List<Map> cir_ftsp) throws CommonException {
		Map<String, Object> map = new HashMap<String, Object>();
		String return_data = "";
		String result = "";
		Map select = null;
		boolean is_go_on = true;// 当路由个数匹配上时，for循环是否还要继续比下去
		// 判断符合条件的电路有几条
		for (int i = 0; i < cir_ftsp.size(); i++) {
			if (!is_go_on) {
				break;
			}
			return_data = "";
			// 定义一个标记为，判断是否比对成功
			boolean is_yes = true;
			boolean is_up = true;
			Map map_cir_ftsp = cir_ftsp.get(i);

			// 查询ftsp电路信息
			select = hashMapSon("t_cir_circuit_route", "CIR_CIRCUIT_ID",
					map_cir_ftsp.get("CIR_CIRCUIT_ID"), "CHAIN_TYPE",
					CommonDefine.CHAIN_TYPE_SDH_CRS, null);
			List<Map> route_f = (List<Map>) selectCircuitRoute(
					map_cir_ftsp.get("CIR_CIRCUIT_ID").toString()).get("rows");

			// 首先匹配数量，如果不一样长，则明显不对

			for (int j = 0; j < route_j.size(); j++) {
				if (route_f != null && route_j.size() == route_f.size()) {
					// 取出一条稽核路由信息
					Map rj = route_j.get(j);
					// 取出一条ftsp路由信息
					Map rf = route_f.get(j);

					// 已经匹配上个数，不需要选择另外一条路径进行比较
					is_go_on = false;
					// 比对网元
					if (!rf.get("NE_NAME").equals(rj.get("NE_NAME"))) {
						// 过滤设置

						// 如不过滤则抛出错误
						result = "存在网元名不对等:" + "网管网元("
								+ rf.get("NE_NAME").toString() + ")VS资源系统网元("
								+ rj.get("NE_NAME").toString() + ")";
						return_data += result + ";";
						// 封装返回结果
						map
								.put("returnResult", rj
										.get("RESOURCE_CIR_ROUTE_ID"));

						// 更新当前路由的比对结果
						select = hashMapSon("t_resource_cir_route",
								"IS_COMPARE", CommonDefine.RES_CIR_UNCOM,
								"RESOURCE_CIR_ROUTE_ID", rj
										.get("RESOURCE_CIR_ROUTE_ID"), null);
						circuitManagerMapper.updateByParameter(select);

						is_yes = false;
						continue;
						// break;
					}

					// 在ftsp提供的port中截取槽道号
					// 需要确定新班的端口显示排列规则,目前以 1-1-2(ban)-9(port) 规则解析
					String[] temp = rf.get("END_PORT").toString().split("-");
					String[] slot = temp[2].split("\\(");
					// 比对槽道和端口，暂时忽略板卡
					if (!slot[0].equals(rj.get("UNIT_NO").toString())) {
						// 过滤设置

						// 如不过滤则抛出错误
						result = "存在槽道不对等:网元名(" + rf.get("NE_NAME").toString()
								+ ")，网管槽道号(" + slot[0] + ")VS资源系统槽道号("
								+ rj.get("UNIT_NO").toString() + ")";
						return_data += result + ";";

						// 封装返回结果
						map
								.put("returnResult", rj
										.get("RESOURCE_CIR_ROUTE_ID"));

						// 更新当前路由的比对结果
						select = hashMapSon("t_resource_cir_route",
								"IS_COMPARE", CommonDefine.RES_CIR_UNCOM,
								"RESOURCE_CIR_ROUTE_ID", rj
										.get("RESOURCE_CIR_ROUTE_ID"), null);
						circuitManagerMapper.updateByParameter(select);

						is_yes = false;
						continue;
						// break;
					}
					// 需要确定新班的端口显示排列规则,目前以 1-1-2(ban)-9(port) 规则解析
					String[] port = temp[3].split("\\(");
					if (!port[0].equals(rj.get("PORT_VALUE").toString())) {
						// 过滤设置
						if (rj.get("UNIT_NO").toString().contains("OCS4")
								&& port[0].equals("3")
								&& rj.get("PORT_VALUE").toString().equals("1")) {

						} else {

							// 如不过滤则抛出错误
							result = "存在端口不对等：网元名("
									+ rf.get("NE_NAME").toString() + ")，槽道号("
									+ slot[0] + ")，网管端口号(" + port[0]
									+ ")VS资源系统端口号("
									+ rj.get("PORT_VALUE").toString() + ")";
							return_data += result + ";";

							// 封装返回结果
							map.put("returnResult", rj
									.get("RESOURCE_CIR_ROUTE_ID"));

							// 更新当前路由的比对结果
							select = hashMapSon("t_resource_cir_route",
									"IS_COMPARE", CommonDefine.RES_CIR_UNCOM,
									"RESOURCE_CIR_ROUTE_ID", rj
											.get("RESOURCE_CIR_ROUTE_ID"), null);
							circuitManagerMapper.updateByParameter(select);

							is_yes = false;
							continue;
							// break;
						}
					}
					// 比对ctp
					if (!rf.get("END_CTP").equals(rj.get("CTP_VALUE"))) {
						// 过滤设置

						// 如不过滤则抛出错误
						result = "存在时隙不对等：网元名(" + rf.get("NE_NAME").toString()
								+ ")，槽道号(" + slot[0] + ")，端口号(" + port[0]
								+ ")，网管时隙(" + rf.get("END_CTP").toString()
								+ ")VS资源系统时隙(" + rj.get("CTP_VALUE").toString()
								+ ")";
						return_data += result + ";";

						// 封装返回结果
						map
								.put("returnResult", rj
										.get("RESOURCE_CIR_ROUTE_ID"));

						// 更新当前路由的比对结果
						select = hashMapSon("t_resource_cir_route",
								"IS_COMPARE", CommonDefine.RES_CIR_UNCOM,
								"RESOURCE_CIR_ROUTE_ID", rj
										.get("RESOURCE_CIR_ROUTE_ID"), null);
						circuitManagerMapper.updateByParameter(select);

						is_yes = false;
						continue;
						// break;
					}
					// 更新当前路由的比对结果
					select = hashMapSon("t_resource_cir_route", "IS_COMPARE",
							CommonDefine.RES_CIR_YES, "RESOURCE_CIR_ROUTE_ID",
							rj.get("RESOURCE_CIR_ROUTE_ID"), null);
					circuitManagerMapper.updateByParameter(select);
				} else {
					// 如果是单电路
					if (cir_ftsp.size() <= 1) {
						is_up = false;
					}
					result = "路由个数不一致";
					return_data = result;
					map.put("returnResult", CommonDefine.FALSE);
					is_yes = false;
					break;

				}
			}
			// 单条电路路由个数不一致，直接跳出
			if (!is_up) {
				// 将比对上的电路 存入比对表
				result = "路由个数不一致";
				return_data = result;
				map.put("returnResult", CommonDefine.FALSE);
				break;
			}
			// 如果是true，表示稽核电路和此条电路匹配
			if (is_yes) {
				// 将比对上的电路 存入比对表
				result = "比对成功;";
				return_data = result;
				map.put("returnResult", CommonDefine.TRUE);
				break;
			}
		}
		map.put("returnMessage", return_data);
		return map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fujitsu.IService.IResourceCircuitManagerService#UploadcheckNe(java.io.File,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public Map UploadcheckNe(File uploadFile, String fileName, String path)
			throws CommonException {
		Map map = new HashMap();
		boolean rc = false;
		try {
			// 如果文件不为空
			if (uploadFile != null) {
				rc = commonManagerService
						.uploadFile(uploadFile, fileName, path);
			}
			// 上传成功，则进行数据导入

			if (rc) {
				File file = new File(path + "\\" + fileName);
				boolean is2007 = false;
				if (fileName.endsWith(".xlsx")) {
					is2007 = true;
				}
				readExcelFileForJiheNe(file, 0, is2007);
				rc = true;
			}
			map.put("success", rc);
		} catch (FileNotFoundException e) {
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_NOTFOUND);
		} catch (IOException e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_IO);
		} catch (BiffException e) {
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_BIFF);
		}catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_ERROR);
		}
		return map;
	}

	/**
	 * 读取已经处理过的网元对应关系
	 * 
	 * @param File
	 *            稽核电路信息文件
	 * @return void
	 * @throws IOException
	 * @throws BiffException
	 */
	public void readExcelFileForJiheNe(File file, int sheetNumber,
			boolean is2007) throws BiffException, IOException,Exception {

		Workbook workbook = null;
		InputStream in = null;
		BufferedReader bufferedReader = null;
		Map select = null;

		in = new FileInputStream(file);
		// bufferedReader = new BufferedReader(new InputStreamReader(
		// in,"GBK"));

		if (is2007) {
			workbook = new XSSFWorkbook(in);
		} else {
			workbook = new HSSFWorkbook(in);
		}

		Sheet sheet = workbook.getSheetAt(sheetNumber);
		//
		int rowCount = sheet.getPhysicalNumberOfRows();
		String strLine = "";
		// // 循环读取电路信息文件中内容 8 9 12 13
		for (int i = 1; i < rowCount; i++) {
			if(sheet.getRow(i).getCell(1).getStringCellValue().isEmpty()){
				continue;
			}

			String jihe_name = sheet.getRow(i).getCell(1).getStringCellValue();
			String ftsp_name = sheet.getRow(i).getCell(2).getStringCellValue();

			// 查询看对应关系是否已经存在
			select = hashMapSon("t_resource_ne", "RESOURCE_NE_NAME", jihe_name,
					null, null, null);
			List<Map> list = circuitManagerMapper.getByParameter(select);

			// 查询看，对应的网元是否在网管中存在 new String(ftsp_name.getBytes("GBK"), "UTF-8")
			select = hashMapSon("t_resource_ne", "FTSP_NE_NAME", ftsp_name,
					null, null, null);
			List<Map> list_ftsp = circuitManagerMapper.getByParameter(select);

			if (list != null && list.size() > 0) {

				Map update_map = hashMapSon("t_resource_ne", "RESOURCE_NE_ID",
						list.get(0).get("RESOURCE_NE_ID"), "FTSP_NE_NAME",
						ftsp_name, null);
				circuitManagerMapper.updateByParameter(update_map);
			} else if (list_ftsp != null && list_ftsp.size() > 0) {
				Map update_map = hashMapSon("t_resource_ne", "RESOURCE_NE_ID",
						list_ftsp.get(0).get("RESOURCE_NE_ID"),
						"RESOURCE_NE_NAME", jihe_name, null);
				circuitManagerMapper.updateByParameter(update_map);
			} else {
				// 否则插入一条新的记录
				Map insert_map = new HashMap();
				insert_map.put("RESOURCE_NE_NAME", jihe_name);
				insert_map.put("FTSP_NE_NAME", ftsp_name);
				resourceCircuitManagerMapper.addResourceNe(insert_map);
			}

		}

		// // 查询看，对应的网元是否在网管中存在 new String(ftsp_name.getBytes("GBK"), "UTF-8")
		// select = hashMapSon("t_base_ne", "DISPLAY_NAME", ftsp_name, null,
		// null, null);
		// List<Map> list_ftsp = circuitManagerMapper.getByParameter(select);
		//
		// String ne_name = "";
		// if (list_ftsp != null && list_ftsp.size() > 0) {
		// ne_name = ftsp_name;
		// }
		// if (list != null && list.size() > 0) {
		// // 记录在网元对应关系表中已经存在，并且能够对应上ftsp的网元
		// if (ne_name != null && !ne_name.isEmpty()) {
		// Map update_map = hashMapSon("t_resource_ne",
		// "FTSP_NE_NAME", ne_name, "RESOURCE_NE_ID", list
		// .get(0).get("RESOURCE_NE_ID"), null);
		// circuitManagerMapper.updateByParameter(update_map);
		// }
		// } else {
		// // 否则插入一条新的记录
		// Map insert_map = new HashMap();
		// insert_map.put("RESOURCE_NE_NAME", jihe_name);
		// insert_map.put("FTSP_NE_NAME", ne_name);
		// resourceCircuitManagerMapper.addResourceNe(insert_map);
		// }
		//
		// }

		in.close();
		// workbook.close();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fujitsu.IService.IResourceCircuitManagerService#UploadResCir(java.io.File,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public Map UploadResCir(File uploadFile, String fileName, String path)
			throws CommonException {
		Map map = new HashMap();
		boolean rc = false;
		try {
			// 如果文件不为空
			if (uploadFile != null) {

				rc = commonManagerService
						.uploadFile(uploadFile, fileName, path);

			}
			// 上传成功，则进行数据导入
			if (rc) {
				File file = new File(path + "/" + fileName);

				// 判断是2003 还是2007
				boolean is2007 = false;
				if (fileName.endsWith(".xlsx")) {
					is2007 = true;
				}
				readExcelFileForResCircuit(file, 0, is2007);
				readExcelFileForResCircuitRoute(file, 0, is2007);
				rc = true;

			}

			map.put("success", rc);
		} catch (FileNotFoundException e) {
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_NOTFOUND);
		} catch (IOException e) {
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_IO);
		} catch (BiffException e) {
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_BIFF);
		}catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_ERROR);
		}
		return map;
	}

	/**
	 * 读取稽核电路信息
	 * 
	 * @param File
	 *            稽核电路信息文件
	 * @return void
	 * @throws IOException
	 * @throws BiffException
	 */
	public void readExcelFileForResCircuit(File file, int sheetNumber,
			boolean is2007) throws BiffException, IOException,Exception {

		Workbook workbook = null;
		InputStream in = null;
		Map select = null;

		in = new FileInputStream(file);
		if (is2007) {
			workbook = new XSSFWorkbook(in);
		} else {
			workbook = new HSSFWorkbook(in);
		}

		Sheet sheet = workbook.getSheetAt(sheetNumber);

		int rowCount = sheet.getPhysicalNumberOfRows();

		// 循环读取电路信息文件中内容
		for (int i = 1; i < rowCount; i++) {
			if (sheet.getRow(i).getCell(0).getStringCellValue().isEmpty()) {
				continue;
			}

			String content = sheet.getRow(i).getCell(0).getStringCellValue();
			// 判断是否存在
			select = hashMapSon("t_resource_cir", "RESOURCE_CIR_NAME", content,
					null, null, null);

			List<Map> list = circuitManagerMapper.getByParameter(select);
			if (list != null && list.size() > 0) {

			} else {

				Map insert_map = new HashMap();
				insert_map.put("RESOURCE_CIR_NAME", content);
				resourceCircuitManagerMapper.insertResCir(insert_map);
			}

		}

		in.close();
		// workbook.();

	}

	/**
	 * 读取稽核电路路由信息
	 * 
	 * @param File
	 *            稽核电路路由信息文件
	 * @return void
	 * @throws IOException
	 * @throws BiffException
	 */
	public void readExcelFileForResCircuitRoute(File file, int sheetNumber,
			boolean is2007) throws BiffException, IOException,Exception {

		Workbook workbook = null;
		InputStream in = null;

		in = new FileInputStream(file);
		if (is2007) {
			workbook = new XSSFWorkbook(in);
		} else {
			workbook = new HSSFWorkbook(in);
		}

		int sheetCount = workbook.getNumberOfSheets();
		if (sheetCount > 0) {
			for (int i = 1; i < sheetCount; i++) {
				Sheet sheet = workbook.getSheetAt(i);
				int rowCount = sheet.getPhysicalNumberOfRows();
				
				String content = sheet.getRow(0).getCell(0)
						.getStringCellValue();
				System.out.println(content);
				String[] circuitName = content.split("详细路由");
				// 查询电路是否需要导入
				Map sel_map = hashMapSon("t_resource_cir", "RESOURCE_CIR_NAME",
						circuitName[0], null, null, null);
				List<Map> list_cir = circuitManagerMapper
						.getByParameter(sel_map);
				if (list_cir != null && list_cir.size() > 0) {
					// 循环读取电路信息文件中内容
					// 路由名称
					String route_name = "";
					String[] a_1 = sheet.getRow(3).getCell(0)
							.getStringCellValue().split(":");
					if (a_1.length > 1) {
						route_name = a_1[1].replace(" ", "");
					}
					// 客户名称
					String client_name = "";
					String[] a_2 = sheet.getRow(3).getCell(1)
							.getStringCellValue().split(":");
					if (a_2.length > 1) {
						client_name = a_2[1].replace(" ", "");
					}

					// 电路用途
					String use_for = "";
					String[] a_3 = sheet.getRow(4).getCell(0)
							.getStringCellValue().split(":");
					if (a_3.length > 1) {
						use_for = a_3[1].replace(" ", "");
					}
					// 电路类别
					String circuit_type = "";
					String[] a_4 = sheet.getRow(4).getCell(1)
							.getStringCellValue().split(":");
					if (a_4.length > 1) {
						circuit_type = a_4[1].replace(" ", "");
					}

					// 业务/电路类型
					String circuit_model = "";
					String[] a_5 = sheet.getRow(5).getCell(0)
							.getStringCellValue().split(":");
					if (a_5.length > 1) {
						circuit_model = a_5[1].replace(" ", "");
					}
					// A端业务节点
					String a_port = "";
					String[] a_6 = sheet.getRow(6).getCell(0)
							.getStringCellValue().split(":");
					if (a_6.length > 1) {
						a_port = a_6[1].replace(" ", "");
					}
					// Z端业务节点
					String z_port = "";
					String[] a_7 = sheet.getRow(6).getCell(1)
							.getStringCellValue().split(":");
					if (a_7.length > 1) {
						z_port = a_7[1].replace(" ", "");
					}
					// A端用户
					String a_user = "";
					String[] a_8 = sheet.getRow(7).getCell(0)
							.getStringCellValue().split(":");
					if (a_8.length > 1) {
						a_user = a_8[1].replace(" ", "");
					}
					// Z端用户
					String z_user = "";
					String[] a_9 = sheet.getRow(7).getCell(1)
							.getStringCellValue().split(":");
					if (a_9.length > 1) {
						z_user = a_9[1].replace(" ", "");
					}
					Map update_cir = new HashMap();
					update_cir.put("ROUTE_NAME", route_name);
					update_cir.put("CLIENT_NAME", client_name);
					update_cir.put("USED_FOR", use_for);
					update_cir.put("CIR_TYPE", circuit_type);
					update_cir.put("CIR_MODEL", circuit_model);
					update_cir.put("A_PORT", a_port);
					update_cir.put("Z_PORT", z_port);
					update_cir.put("A_USER", a_user);
					update_cir.put("Z_USER", z_user);
					update_cir.put("RESOURCE_CIR_ID", list_cir.get(0).get(
							"RESOURCE_CIR_ID"));

					resourceCircuitManagerMapper.updateResCir(update_cir);

					// 插入电路相关信息

					List list_all = new ArrayList();
					List<Map> list = new ArrayList<Map>();

					// 循环读取电路信息文件中内容
					for (int k = 9; k <=rowCount; k++) {

						String port_no = "";
						String equip_no = "";
						String ctp = "";
						// 根据设别比对表，确定网元名称
						String ne_name = sheet.getRow(k).getCell(0)
								.getStringCellValue();
						// 查询对应的网元名
						Map select_map = hashMapSon("t_resource_ne",
								"RESOURCE_NE_NAME", ne_name, null, null, null);

						List<Map> list_res_ne = circuitManagerMapper
								.getByParameter(select_map);
						
						if (sheet.getRow(k) == null) {
							continue;
						}
						// 去网元相关表查询出相应的网元
						if (ne_name.contains("设备名称")) {
							continue;
						}
						if (ne_name.contains("###")) {
							continue;
						}
						if (ne_name.contains("<<>>")) {
							list_all.add(list);
							list = new ArrayList<Map>();
							continue;
						}
						if (ne_name.contains("////")) {
							break;
							// list_all = new ArrayList();
							// list = new ArrayList<TJiheRoute>();
							// continue;
						}
						// 端口编码
						String port_name = sheet.getRow(k).getCell(1)
								.getStringCellValue();
						String[] port_about = port_name.split("/");
						String equip = port_about[port_about.length - 2];
						// 获取槽道号
						String[] equ = equip.split("\\(");
						System.out.println(equ[0]);
						equip_no = removeZero(equ[0]);
						String port = port_about[port_about.length - 1];
						// 获取端口号
						if (port != null && port.length() > 0) {
							port_no = getNumber(port);
						}
						// 时隙
						String ctp_name = sheet.getRow(k).getCell(2)
								.getStringCellValue();
						if (ctp_name != null && ctp_name.length() > 0) {
							ctp = getCtp(port, ctp_name);
						}

						Map res_cir_route = new HashMap();
						res_cir_route.put("RESOURCE_CIR_ID", list_cir.get(0)
								.get("RESOURCE_CIR_ID"));
						res_cir_route.put("CTP_ORGI_VALUE", ctp_name);
						res_cir_route.put("CTP_VALUE", ctp);
						res_cir_route.put("UNIT_NAME", equip);
						res_cir_route.put("UNIT_NO", equip_no);
						res_cir_route.put("PORT_ORGI_VALUE", port);
						res_cir_route.put("PORT_VALUE", port_no);
						res_cir_route.put("NE_INTERNAL_NAME", ne_name);

						if (list_res_ne != null && list_res_ne.size() > 0) {
							// 去网元表查询网元名和id
							Map select = hashMapSon("t_base_ne",
									"DISPLAY_NAME", list_res_ne.get(0).get(
											"FTSP_NE_NAME"), null, null, null);
							List<Map> list_ne = circuitManagerMapper
									.getByParameter(select);
							if (list_ne != null && list_ne.size() > 0) {
								res_cir_route.put("BASE_NE_ID", list_ne.get(0).get("BASE_NE_ID"));
								res_cir_route.put("NE_NAME", ne_name);
							}

						}
						// 保存电路信息
						list.add(res_cir_route);
						// commonDAOService.storeObject(tjr);

					}
					list_all.add(list);
					if (list_all.size() > 0) {
						for (int m = 0; m < list_all.size(); m++) {
							List<Map> li = (List<Map>) list_all.get(m);
							// 单路径
							if (m == 0 && li.size() > 0) {
								for (int n = 0; n < li.size(); n++) {
									Map tr = li.get(n);
									tr.put("ROUTE_NO", m + 1);
									resourceCircuitManagerMapper
											.insertResRoute(tr);

								}
								// 更新稽核电路表的电路数
								Map update_res_cir = new HashMap();
								update_res_cir.put("NAME", "t_resource_cir");
								update_res_cir.put("ID_NAME", "RESOURCE_CIR_ID");
								update_res_cir.put("ID_VALUE", li.get(0)
										.get("RESOURCE_CIR_ID"));
								update_res_cir.put("ID_NAME_2", "ROUTE_NUMBER");
								update_res_cir.put("ID_VALUE_2", m + 1);
								circuitManagerMapper
										.updateByParameter(update_res_cir);
							}
							// 有保护电路的情况
							if (m >= 1 && li.size() > 0) {
								List<Map> lst_0 = (List<Map>) list_all.get(0);
								// 判定电路的起点
								for (int c = 0; c < lst_0.size(); c++) {
									Map tr_0 = lst_0.get(c);
									Map tr_i = li.get(li.size() - 1);
									// 如果相同, 先将环路部分插入数据库
									if ((tr_0.get("NE_INTERNAL_NAME")
											.equals(tr_i
													.get("NE_INTERNAL_NAME")))
											&& (tr_0.get("UNIT_NAME")
													.equals(tr_i
															.get("UNIT_NAME")))
											&& (tr_0.get("PORT_ORGI_VALUE")
													.equals(tr_i
															.get("PORT_ORGI_VALUE")))
											&& (tr_0.get("CTP_ORGI_VALUE")
													.equals(tr_i
															.get("CTP_ORGI_VALUE")))) {
										for (int n = li.size() - 1; n >= 0; n--) {
											// 此时在增加路径2的电路
											Map tr = li.get(n);
											tr.put("ROUTE_NO", m + 1);
											resourceCircuitManagerMapper
													.insertResRoute(tr);
										}
										break;
									} else {
										// 不相同的话，则先从前电路的起点开始插入，直到相同
										// 此时在增加路径2的电路
										Map tr_f = lst_0.get(c);
										tr_f.put("ROUTE_NO", m + 1);
										resourceCircuitManagerMapper
												.insertResRoute(tr_f);
									}
								}
								//
								// for (int n = li.size() - 1; n >= 0; n--)
								// {
								// // 此时在增加路径2的电路
								// TJiheRoute tr = li.get(n);
								// tr.setRouteNo(m + 1L);
								// commonDAOService.storeObject(tr);
								// }

								// 开始遍历前一条电路，将与保护电路重复的部分移走
								while (lst_0.size() > 0) {
									//
									Map tr_0_k = lst_0.get(0);
									Map tr_i_0 = li.get(0);
									if ((tr_0_k.get("NE_INTERNAL_NAME")
											.equals(tr_i_0
													.get("NE_INTERNAL_NAME")))
											&& (tr_0_k.get("UNIT_NAME")
													.equals(tr_i_0
															.get("UNIT_NAME")))
											&& (tr_0_k.get("PORT_ORGI_VALUE")
													.equals(tr_i_0
															.get("PORT_ORGI_VALUE")))
											&& (tr_0_k.get("CTP_ORGI_VALUE")
													.equals(tr_i_0
															.get("CTP_ORGI_VALUE")))) {
										lst_0.remove(0);
										break;
									} else {
										lst_0.remove(0);
									}
								}
								// 将剩下的部分插入电路
								for (int k = 0; k < lst_0.size(); k++) {
									System.out.println(lst_0.size());
									Map trr = lst_0.get(k);
									trr.put("ROUTE_NO", m + 1);
									resourceCircuitManagerMapper
											.insertResRoute(trr);
								}
								// 更新稽核电路表的电路数
								Map update_res_cir = hashMapSon(
										"t_resource_cir","RESOURCE_CIR_ID", li.get(0)
										.get("RESOURCE_CIR_ID"), "ROUTE_NUMBER",
										m + 1,  null);
								circuitManagerMapper
										.updateByParameter(update_res_cir);
							}
						}
					}
				}
			}
		}

	}

	protected String removeZero(String num_) {
		String num = num_;
		if (num_.length() > 0) {
			for (int j = 0; j < num_.length(); j++) {
				if ("0".equals(num_.charAt(j) + "")) {
					num = num_.substring(1);
				} else {
					break;
				}
			}
		}
		return num;
	}

	protected String getCtp(String port, String name) {
		// 截取出最后的端口号,逆向遍历String

		String ctp = "";
		String rate = "";
		// 判断端口是不是2M
		if (port.contains("M")) {
			String[] rate_ = port.split("M");
			rate = rate_[0] + "M";
		}
		// 如果是0结尾 表示是落地点
		if ("0".equals(name) || "2M".equals(rate)) {
			ctp = "1-1-1-1";
		} else if (name.length() < 5 && (!name.contains("-"))
				&& (port.contains("10G") || port.contains("2.5G"))) {
			// 处理AU4级别
			if ("0".equals(name.charAt(0) + "")) {
				ctp = name.substring(1) + "-0-0-0";
			} else {
				ctp = name + "-0-0-0";
			}
		} else if (name.contains("(") && name.contains(")")) {
			// 含有括号的，直接去括号里面的数据
			String[] temp = name.split("\\(");
			String[] temp_ = temp[1].split("\\)");
			ctp = temp_[0];
		} else {
			ctp = name;
		}

		return ctp;
	}

	protected String getNumber(String name) {
		// 截取出最后的端口号,逆向遍历String

		String num = "";
		for (int i = name.length() - 1; i >= 0; i--) {
			if (Character.isDigit(name.charAt(i))) {
				num = name.charAt(i) + num;
			} else {
				break;
			}

		}
		// 将最前面的0去掉
		String num_ = num;
		if (num_.length() > 0) {
			for (int j = 0; j < num_.length(); j++) {
				if ("0".equals(num_.charAt(j) + "")) {
					num = num.substring(1);
				} else {
					break;
				}
			}
		}
		return num;
	}

	@Override
	public Map UploadNc(File uploadFile, String fileName, String path)
			throws CommonException {

		Map map = new HashMap();
		boolean rc = false;
		try {
			// 如果文件不为空
			if (uploadFile != null) {

				rc = commonManagerService
						.uploadFile(uploadFile, fileName, path);

			}
			// 上传成功，则进行数据导入
			if (rc) {
				File file = new File(path + "/" + fileName);

				// 判断是2003 还是2007
				boolean is2007 = false;
				if (fileName.endsWith(".xlsx")) {
					is2007 = true;
				}
				readExcelFileForNcHW(file, 0, is2007);
				rc = true;

			}

			map.put("success", rc);
		} catch (FileNotFoundException e) {
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_NOTFOUND);
		} catch (IOException e) {
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_IO);
		} catch (BiffException e) {
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_BIFF);
		}catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_ERROR);
		}
		return map;
	
	}
	
	/**
	 * 读取南昌华为网管电路数据
	 * 
	 * @param File
	 *            稽核电路信息文件
	 * @return void
	 * @throws IOException
	 * @throws BiffException
	 */
	public void readExcelFileForNcHW(File file, int sheetNumber,
			boolean is2007) throws BiffException, IOException,Exception {

		Workbook workbook = null;
		InputStream in = null;
		BufferedReader bufferedReader = null;
		Map select = null;
		Map update = null;
		
		in = new FileInputStream(file);
		// bufferedReader = new BufferedReader(new InputStreamReader(
		// in,"GBK"));

		if (is2007) {
			workbook = new XSSFWorkbook(in);
		} else {
			workbook = new HSSFWorkbook(in);
		}

		//就一个sheet
		Sheet sheet = workbook.getSheetAt(0);
		//
		int rowCount = sheet.getPhysicalNumberOfRows();
		String strLine = "";
		//先确认网管是什么网管
		String ip = sheet.getRow(0).getCell(0).getStringCellValue().trim();
		select = hashMapSon("t_base_ems_connection", "IP",ip, "IS_DEL", CommonDefine.FALSE, null);
		List<Map> listems = circuitManagerMapper.getByParameter(select);
		Object ems = null;
		if(listems!=null&&listems.size()>0){
			ems = listems.get(0).get("BASE_EMS_CONNECTION_ID");
		}
		// // 循环读取电路信息文件中内容 8 9 12 13
		int k = 0;
		int m = 0;
		System.out.println("rowCount=="+rowCount);
		for (int i = 1; i < rowCount; i++) {   
			System.out.println("第几个=="+i);
			if(sheet.getRow(i).getCell(1).getStringCellValue().isEmpty()){
				continue;
			}

			String routeName = sheet.getRow(i).getCell(5).getStringCellValue().trim();
			//   153-OSN3500-D-14-SEP-3(宜春S-1N0004入)
			String fromName = sheet.getRow(i).getCell(6).getStringCellValue().trim();
			System.out.println("fromName=="+fromName);
			String fromCtp = sheet.getRow(i).getCell(7).getStringCellValue().trim();
			String toName = sheet.getRow(i).getCell(8).getStringCellValue().trim();
			String toCtp = sheet.getRow(i).getCell(9).getStringCellValue().trim();
			
			// 解析起端网元
			// 先确定不含有虚拟网元
			if(!isVirNe(fromName)){
				// 确定最后一个的（的位置
				//fromName.lastIndexOf("\\(");
				String form = fromName.substring(0,fromName.lastIndexOf("("));
				//String [] from = fromName.split("\\(");
				if(form.length() > 0){
				// 取前面一段
				String [] array = form.split("-");
				//倒数第一个是端口号
				String portNo = array[array.length-1];
				//倒数第二个是板卡名
				String equipName = array[array.length-2];
				//倒数第三个是槽道号
				String slotNo = array[array.length-3];
				
				String neName = form.split("-"+slotNo+"-"+equipName+"-"+portNo)[0];
				
				// 根据上述信息确认端口网元id
				select = hashMapSon("t_base_ne", "DISPLAY_NAME",neName, "BASE_EMS_CONNECTION_ID", ems, null);
				List<Map> list_ne = circuitManagerMapper.getByParameter(select);
				if(list_ne!=null&&list_ne.size()>0){
					Map mapNe = list_ne.get(0);
					select = hashMapSon("t_base_slot", "SLOT_NO",slotNo, "BASE_NE_ID", mapNe.get("BASE_NE_ID"), null);
					List<Map> listSlot = circuitManagerMapper.getByParameter(select);
					if(listSlot!=null&&listSlot.size()>0){
						Map mapSlot = listSlot.get(0);
						select = hashMapSon("t_base_unit", "DISPLAY_NAME",equipName, "BASE_SLOT_ID", mapSlot.get("BASE_SLOT_ID"), null);
						List<Map> listUnit = circuitManagerMapper.getByParameter(select);
						if(listUnit!=null&&listUnit.size()>0){
							Map mapUnit = listUnit.get(0);
							select = hashMapSon("t_base_ptp", "BASE_UNIT_ID",mapUnit.get("BASE_UNIT_ID"), "PORT_NO", portNo, null);
							List<Map> listport = circuitManagerMapper.getByParameter(select);
							if(listport!=null&&listport.size()>0){
								Map mapPort = listport.get(0);
								//   VC4:1-VC12:14[2-5-1]
								String [] ctp = fromCtp.split("\\[");
								//先解析 j位值
								if(ctp.length>1){
									//VC4:1-VC12:14
									String [] ctpJ = ctp[0].split("-");
									int  ctpJFrom = Integer.parseInt(ctpJ[0].split(":")[1]);
									// 解析klm值
									String [] klm = ctp[1].split("-");
									int ctpk = Integer.parseInt(klm[0]);
									int ctpl = Integer.parseInt(klm[1]);
									int ctpm = Integer.parseInt(klm[2].substring(0,klm[2].length()-1));
									
										// 查处相应的时隙
										select = new HashMap();
										select.put("NAME", "t_base_sdh_ctp");
										select.put("VALUE", "*");
										select.put("ID_NAME", "BASE_PTP_ID");
										select.put("ID_VALUE", mapPort.get("BASE_PTP_ID"));
										select.put("ID_NAME_2", "CTP_J_ORIGINAL");
										select.put("ID_VALUE_2", ctpJFrom);
										select.put("ID_NAME_3", "CTP_K");
										select.put("ID_VALUE_3", ctpk);
										select.put("ID_NAME_4", "CTP_L");
										select.put("ID_VALUE_4", ctpl);
										select.put("ID_NAME_5", "CTP_M");
										select.put("ID_VALUE_5", ctpm);
										List<Map> listctp = circuitManagerMapper.getByParameter(select);
										if(listctp!=null&&listctp.size()>0){
											Map mapCtp = listctp.get(0);
											// 去电路表匹配电路
											select = hashMapSon("t_cir_circuit_info", "A_END_CTP",mapCtp.get("BASE_SDH_CTP_ID"), null, null, null);
											List<Map> listCir = circuitManagerMapper.getByParameter(select);
											if(listCir!=null&&listCir.size()>0){
												for(int l = 0 ;l<listCir.size();l++){
												Map mapCir = listCir.get(l);
												update  = new HashMap();
												update.put("NAME", "t_cir_circuit_info");
												update.put("ID_NAME", "CIR_CIRCUIT_INFO_ID");
												update.put("ID_VALUE", mapCir.get("CIR_CIRCUIT_INFO_ID"));
												update.put("ID_NAME_2", "CIR_NAME");
												update.put("ID_VALUE_2", routeName);
												circuitManagerMapper.updateByParameter(update);
												
												}
												System.out.println("匹配第个=="+ ++m);
											}else{
												select = hashMapSon("t_cir_circuit_info", "Z_END_CTP",mapCtp.get("BASE_SDH_CTP_ID"), null, null, null);
												List<Map> listCirZ = circuitManagerMapper.getByParameter(select);
												if(listCirZ!=null&&listCirZ.size()>0){
													for(int l = 0 ;l<listCirZ.size();l++){
													Map mapCir = listCirZ.get(l);
													update  = new HashMap();
													update.put("NAME", "t_cir_circuit_info");
													update.put("ID_NAME", "CIR_CIRCUIT_INFO_ID");
													update.put("ID_VALUE", mapCir.get("CIR_CIRCUIT_INFO_ID"));
													update.put("ID_NAME_2", "CIR_NAME");
													update.put("ID_VALUE_2", routeName);
													circuitManagerMapper.updateByParameter(update);
													
													}
													System.out.println("匹配第个=="+ ++m);
												}
											}
										
									}
								}
							}
							
						}
						
					}
				}
				
				
				}
			}

			// 先确定不含有虚拟网元
			if(!isVirNe(toName)){
				//String [] from = toName.split("\\(");
				String from = fromName.substring(0,fromName.lastIndexOf("("));
				if(from.length() > 0){
				// 取前面一段
				String [] array = from.split("-");
				//倒数第一个是端口号
				String portNo = array[array.length-1];
				//倒数第二个是板卡名
				String equipName = array[array.length-2];
				//倒数第三个是槽道号
				String slotNo = array[array.length-3];
				
				String neName = from.split("-"+slotNo+"-"+equipName+"="+portNo)[0];
				// 根据上述信息确认端口网元id
				select = hashMapSon("t_base_ne", "DISPLAY_NAME",neName, "BASE_EMS_CONNECTION_ID", ems, null);
				List<Map> list_ne = circuitManagerMapper.getByParameter(select);
				if(list_ne!=null&&list_ne.size()>0){
					Map mapNe = list_ne.get(0);
					select = hashMapSon("t_base_slot", "SLOT_NO",slotNo, "BASE_NE_ID", mapNe.get("BASE_NE_ID"), null);
					List<Map> listSlot = circuitManagerMapper.getByParameter(select);
					if(listSlot!=null&&listSlot.size()>0){
						Map mapSlot = listSlot.get(0);
						select = hashMapSon("t_base_unit", "DISPLAY_NAME",equipName, "BASE_SLOT_ID", mapSlot.get("BASE_SLOT_ID"), null);
						List<Map> listUnit = circuitManagerMapper.getByParameter(select);
						if(listUnit!=null&&listUnit.size()>0){
							Map mapUnit = listUnit.get(0);
							select = hashMapSon("t_base_ptp", "BASE_UNIT_ID",mapUnit.get("BASE_UNIT_ID"), "PORT_NO", portNo, null);
							List<Map> listport = circuitManagerMapper.getByParameter(select);
							if(listport!=null&&listport.size()>0){
								Map mapPort = listport.get(0);
								//   VC4:1-VC12:14[2-5-1]
								String [] ctp = toCtp.split("\\[");
								//先解析 j位值
								if(ctp.length>1){
									//VC4:1-VC12:14
									String [] ctpJ = ctp[0].split("-");
									int  ctpJFrom = Integer.parseInt(ctpJ[0].split(":")[1]);
									// 解析klm值
									String [] klm = ctp[1].split("-");
									int ctpk = Integer.parseInt(klm[0]);
									int ctpl = Integer.parseInt(klm[1]);
									int ctpm = Integer.parseInt(klm[2].substring(0,klm[2].length()-1));
									
										// 查处相应的时隙
										select = hashMapSon("t_base_sdh_ctp", "BASE_PTP_ID",mapPort.get("BASE_PTP_ID"), "CTP_J_ORIGINAL", ctpJFrom, null);
										select.put("ID_NAME_3", "CTP_K");
										select.put("ID_VALUE_3", ctpk);
										select.put("ID_NAME_4", "CTP_L");
										select.put("ID_VALUE_4", ctpl);
										select.put("ID_NAME_5", "CTP_M");
										select.put("ID_VALUE_5", ctpm);
										
										List<Map> listctp = circuitManagerMapper.getByParameter(select);
										if(listctp!=null&&listctp.size()>0){
											Map mapCtp = listctp.get(0);
											// 去电路表匹配电路
											select = hashMapSon("t_cir_circuit_info", "A_END_CTP",mapCtp.get("BASE_SDH_CTP_ID"), null, null, null);
											List<Map> listCir = circuitManagerMapper.getByParameter(select);
											if(listCir!=null&&listCir.size()>0){
												for(int l = 0 ;l<listCir.size();l++){
												Map mapCir = listCir.get(l);
												update  = new HashMap();
												update.put("NAME", "t_cir_circuit_info");
												update.put("ID_NAME", "CIR_CIRCUIT_INFO_ID");
												update.put("ID_VALUE", mapCir.get("CIR_CIRCUIT_INFO_ID"));
												update.put("ID_NAME_2", "CIR_NAME");
												update.put("ID_VALUE_2", routeName);
												circuitManagerMapper.updateByParameter(update);
												
												}
												System.out.println("匹配第个=="+ ++m);
											}else{
												select = hashMapSon("t_cir_circuit_info", "Z_END_CTP",mapCtp.get("BASE_SDH_CTP_ID"), null, null, null);
												List<Map> listCirZ = circuitManagerMapper.getByParameter(select);
												if(listCirZ!=null&&listCirZ.size()>0){
													for(int l = 0 ;l<listCirZ.size();l++){
													Map mapCir = listCirZ.get(l);
													update  = new HashMap();
													update.put("NAME", "t_cir_circuit_info");
													update.put("ID_NAME", "CIR_CIRCUIT_INFO_ID");
													update.put("ID_VALUE", mapCir.get("CIR_CIRCUIT_INFO_ID"));
													update.put("ID_NAME_2", "CIR_NAME");
													update.put("ID_VALUE_2", routeName);
													circuitManagerMapper.updateByParameter(update);
													
													}
													System.out.println("匹配第个=="+ ++m);
												}
											}
										}
									
								}
							}
							
						}
						
					}
				}
				
				
				}
			}
		}

		in.close();
		// workbook.close();

	}
	
	/**
	 * 读取南昌华为网管电路数据
	 * 
	 * @param File
	 *            稽核电路信息文件
	 * @return void
	 * @throws IOException
	 * @throws BiffException
	 */
	public void readExcelFileForNcHWWDM(File file, int sheetNumber,
			boolean is2007) throws BiffException, IOException,Exception {

		Workbook workbook = null;
		InputStream in = null;
		BufferedReader bufferedReader = null;
		Map select = null;
		Map update = null;
		
		in = new FileInputStream(file);
		// bufferedReader = new BufferedReader(new InputStreamReader(
		// in,"GBK"));

		if (is2007) {
			workbook = new XSSFWorkbook(in);
		} else {
			workbook = new HSSFWorkbook(in);
		}

		//就一个sheet
		Sheet sheet = workbook.getSheetAt(0);
		//
		int rowCount = sheet.getPhysicalNumberOfRows();
		String strLine = "";
		//先确认网管是什么网管
		String ip = sheet.getRow(0).getCell(0).getStringCellValue().trim();
		select = hashMapSon("t_base_ems_connection", "IP",ip, "IS_DEL", CommonDefine.FALSE, null);
		List<Map> listems = circuitManagerMapper.getByParameter(select);
		Object ems = null;
		if(listems!=null&&listems.size()>0){
			ems = listems.get(0).get("BASE_EMS_CONNECTION_ID");
		}
		// // 循环读取电路信息文件中内容 8 9 12 13
		int k = 0;
		System.out.println("rowCount==="+rowCount);
		for (int i = 1; i < rowCount; i++) {
			System.out.println("第几个=="+i);
			if(sheet.getRow(i).getCell(1).getStringCellValue().isEmpty()){
				continue;
			}

			String routeName = sheet.getRow(i).getCell(5).getStringCellValue().trim();
			System.out.println("routeName=="+routeName);
			//   153-OSN3500-D-14-SEP-3(宜春S-1N0004入)
			String fromName = sheet.getRow(i).getCell(6).getStringCellValue().trim();
			
			//String fromCtp = sheet.getRow(i).getCell(7).getStringCellValue().trim();
			String toName = sheet.getRow(i).getCell(8).getStringCellValue().trim();
			//String toCtp = sheet.getRow(i).getCell(9).getStringCellValue().trim();
			if(fromName.isEmpty()&&toName.isEmpty()){
				continue;
			}
			// 解析起端网元
			// 先确定不含有虚拟网元
			if(!isVirNe(fromName)){
				// 确定最后一个的（的位置
				//fromName.lastIndexOf("\\(");
				String form = fromName.substring(0,fromName.lastIndexOf("("));
				//String [] from = fromName.split("\\(");
				if(form.length() > 0){
				// 取前面一段
				String [] array = form.split("-");
				//倒数第一个是端口号
				String portNo = array[array.length-1];
				//倒数第二个是板卡名
				String equipName = array[array.length-2];
				//倒数第三个是槽道号
				String slotNo = array[array.length-3];
				
				String neName = form.split("-"+slotNo+"-"+equipName+"-"+portNo)[0];
				
				// 根据上述信息确认端口网元id
				select = hashMapSon("t_base_ne", "DISPLAY_NAME",neName, "BASE_EMS_CONNECTION_ID", ems, null);
				List<Map> list_ne = circuitManagerMapper.getByParameter(select);
				if(list_ne!=null&&list_ne.size()>0){
					Map mapNe = list_ne.get(0);
					select = hashMapSon("t_base_slot", "SLOT_NO",slotNo, "BASE_NE_ID", mapNe.get("BASE_NE_ID"), null);
					List<Map> listSlot = circuitManagerMapper.getByParameter(select);
					if(listSlot!=null&&listSlot.size()>0){
						Map mapSlot = listSlot.get(0);
						select = hashMapSon("t_base_unit", "DISPLAY_NAME",equipName, "BASE_SLOT_ID", mapSlot.get("BASE_SLOT_ID"), null);
						List<Map> listUnit = circuitManagerMapper.getByParameter(select);
						if(listUnit!=null&&listUnit.size()>0){
							Map mapUnit = listUnit.get(0);
							select = hashMapSon("t_base_ptp", "BASE_UNIT_ID",mapUnit.get("BASE_UNIT_ID"), "PORT_NO", portNo, null);
							List<Map> listport = circuitManagerMapper.getByParameter(select);
							if(listport!=null&&listport.size()>0){
								Map mapPort = listport.get(0);
								select  = new HashMap();
								select.put("nodes", mapPort.get("BASE_PTP_ID"));
								select.put("nodeLevel", 8);
								select.put("serviceType", 3);
								select.put("srart",0);
								select.put("limit", 1000);
								Map<String, Object> mapCir = circuitManagerService.selectCircuitAbout(select);
								if(mapCir!=null){
									List<Map> ListCir = (List<Map>) mapCir.get("rows");
									if(ListCir!=null&&ListCir.size()>0){
										for(int l = 0;l<ListCir.size();l++){
										Map mapCirWDM = ListCir.get(l);
										update  = new HashMap();
										update.put("NAME", "t_cir_circuit_info");
										update.put("ID_NAME", "CIR_CIRCUIT_INFO_ID");
										update.put("ID_VALUE", mapCirWDM.get("CIR_CIRCUIT_INFO_ID"));
										update.put("ID_NAME_2", "CIR_NAME");
										update.put("ID_VALUE_2", routeName);
										circuitManagerMapper.updateByParameter(update);
										}
									}
								}
								
							}
							
						}
						
					}
				}
				
				
				}
			}

			// 先确定不含有虚拟网元
			if(!isVirNe(toName)){
				//String [] from = toName.split("\\(");
				String from = fromName.substring(0,fromName.lastIndexOf("("));
				if(from.length() > 0){
				// 取前面一段
				String [] array = from.split("-");
				//倒数第一个是端口号
				String portNo = array[array.length-1];
				//倒数第二个是板卡名
				String equipName = array[array.length-2];
				//倒数第三个是槽道号
				String slotNo = array[array.length-3];
				
				String neName = from.split("-"+slotNo+"-"+equipName+"="+portNo)[0];
				// 根据上述信息确认端口网元id
				select = hashMapSon("t_base_ne", "DISPLAY_NAME",neName, "BASE_EMS_CONNECTION_ID", ems, null);
				List<Map> list_ne = circuitManagerMapper.getByParameter(select);
				if(list_ne!=null&&list_ne.size()>0){
					Map mapNe = list_ne.get(0);
					select = hashMapSon("t_base_slot", "SLOT_NO",slotNo, "BASE_NE_ID", mapNe.get("BASE_NE_ID"), null);
					List<Map> listSlot = circuitManagerMapper.getByParameter(select);
					if(listSlot!=null&&listSlot.size()>0){
						Map mapSlot = listSlot.get(0);
						select = hashMapSon("t_base_unit", "DISPLAY_NAME",equipName, "BASE_SLOT_ID", mapSlot.get("BASE_SLOT_ID"), null);
						List<Map> listUnit = circuitManagerMapper.getByParameter(select);
						if(listUnit!=null&&listUnit.size()>0){
							Map mapUnit = listUnit.get(0);
							select = hashMapSon("t_base_ptp", "BASE_UNIT_ID",mapUnit.get("BASE_UNIT_ID"), "PORT_NO", portNo, null);
							List<Map> listport = circuitManagerMapper.getByParameter(select);
							if(listport!=null&&listport.size()>0){
								Map mapPort = listport.get(0);
								select  = new HashMap();
								select.put("nodes", mapPort.get("BASE_PTP_ID"));
								select.put("nodeLevel", 8);
								select.put("serviceType", 3);
								select.put("srart",0);
								select.put("limit", 1000);
								Map<String, Object> mapCir = circuitManagerService.selectCircuitAbout(select);
								if(mapCir!=null){
									List<Map> ListCir = (List<Map>) mapCir.get("rows");
									if(ListCir!=null&&ListCir.size()>0){
										for(int l = 0;l<ListCir.size();l++){
										Map mapCirWDM = ListCir.get(l);
										update  = new HashMap();
										update.put("NAME", "t_cir_circuit_info");
										update.put("ID_NAME", "CIR_CIRCUIT_INFO_ID");
										update.put("ID_VALUE", mapCirWDM.get("CIR_CIRCUIT_INFO_ID"));
										update.put("ID_NAME_2", "CIR_NAME");
										update.put("ID_VALUE_2", routeName);
										circuitManagerMapper.updateByParameter(update);
										}
									}
								}
							}
							
						}
						
					}
				}
				
				
				}
			}
		}

		in.close();
		// workbook.close();

	}
	public boolean isVirNe(String fromName){
		boolean isTrue = false;
		if(fromName.contains("华为ASON9500/30-2")){
			isTrue = true;
		}else if(fromName.contains("华为ASON9500/19-2")){
			isTrue = true;
		}else if(fromName.contains("二厂楼ASON(17-1)")){
			isTrue = true;
		}else if(fromName.contains("二厂楼ASON(17-3)")){
			isTrue = true;
		}else if(fromName.contains("ASON-17-2")){
			isTrue = true;
		}
		return isTrue;
	}

	@Override
	public Map UploadNcWDM(File uploadFile, String fileName, String path)
			throws CommonException {

		Map map = new HashMap();
		boolean rc = false;
		try {
			// 如果文件不为空
			if (uploadFile != null) {

				rc = commonManagerService
						.uploadFile(uploadFile, fileName, path);

			}
			// 上传成功，则进行数据导入
			if (rc) {
				File file = new File(path + "/" + fileName);

				// 判断是2003 还是2007
				boolean is2007 = false;
				if (fileName.endsWith(".xlsx")) {
					is2007 = true;
				}
				readExcelFileForNcHWWDM(file, 0, is2007);
				rc = true;

			}

			map.put("success", rc);
		} catch (FileNotFoundException e) {
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_NOTFOUND);
		} catch (IOException e) {
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_IO);
		} catch (BiffException e) {
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_BIFF);
		}catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_ERROR);
		}
		return map;
	
	}
}
