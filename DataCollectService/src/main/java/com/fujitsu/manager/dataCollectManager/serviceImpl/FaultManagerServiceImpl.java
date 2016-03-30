package com.fujitsu.manager.dataCollectManager.serviceImpl;

import globaldefs.NameAndStringValue_T;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import com.fujitsu.IService.IMongodbCommonService;
import com.fujitsu.activeMq.JMSSender;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.DataCollectDefine;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.dao.mysql.DataCollectMapper;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.AlarmDataModel;
import com.fujitsu.manager.dataCollectManager.service.FaultManagerService;
import com.fujitsu.util.NameAndStringValueUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

@Service
// @Transactional(rollbackFor = Exception.class)
public class FaultManagerServiceImpl extends FaultManagerService {

	@Resource
	private IMongodbCommonService mongodbCommonService;
	@Resource
	private Mongo mongo;
	@Resource
	private DataCollectMapper dataCollectMapper;
	private static DBCollection conn = null;
	
	private String ip;
	private String host;

	@Override
	public void alarmDataToMongodb(List<AlarmDataModel> modelList, Integer emsConnectionId,
			Integer neId, Integer operateType) throws CommonException {
		// Date t1 = new Date();
		// 获取数据库连接
		try {
			if (conn == null) {
				conn = mongo.getDB(DataCollectDefine.MONGODB_NAME).getCollection(
						DataCollectDefine.T_CURRENT_ALARM);
			}
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_DB_CONNECT);
		}
		// Date t2 = new Date();
		// 执行屏蔽流程
		judgeIsNeedShield(modelList, emsConnectionId, neId);
		// CommonUtil.timeDif("告警屏蔽查询：", t2, new Date());
		// 判读是否全部都被屏蔽
		if (modelList.isEmpty()) {
			if (operateType == DataCollectDefine.ALARM_TO_DB_TYPE_SYNCH) {
				clearAlarmNotInCurList(conn, emsConnectionId, neId, new ArrayList<AlarmDataModel>());
			}
			return;
		}

		// 同步告警入库
		if (operateType == DataCollectDefine.ALARM_TO_DB_TYPE_SYNCH) {
			// 定义未清除的告警数据集合
			List<AlarmDataModel> occurModelList = new ArrayList<AlarmDataModel>();
			// 遍历先处理以清除的告警
			for (int i = 0; i < modelList.size(); i++) {
				// 清除告警，贝尔网管isClearable字段为true时也表示为清除告警
				if (DataCollectDefine.ALARM_PS_CLEARED == modelList.get(i).getPerceivedSeverity()
						|| (DataCollectDefine.FACTORY_ALU_FLAG == modelList.get(i).getFactory() 
							&& true == modelList.get(i).isClearable())) {
					// 清除告警具体逻辑操作
					updateClearAlarm(conn, modelList.get(i));
				} else {
					// 新增或更新告警
					insertOccurAlarm(conn, getOccurMatchCon(modelList.get(i)), modelList.get(i),
							operateType);
					occurModelList.add(modelList.get(i));
				}
			}
			// 清除数据库中有当前告警列表中没有的非“清除”状态的告警
			clearAlarmNotInCurList(conn, emsConnectionId, neId, occurModelList);

		} else {// 推送告警入库
			// 推送告警只会有一条告警
			AlarmDataModel model = modelList.get(0);
			// 保存推送的原始告警信息
			oriAlmMsgSave(model);
			// 通过告警级别字段的值，判断该告警是告警发生，还是清除告警('PS_CLEARED'{对应数字5}表示是清除告警，其它为告警发生)
			// 贝尔网管isClearable字段为true时也表示为清除告警
			if (DataCollectDefine.ALARM_PS_CLEARED == model.getPerceivedSeverity()
					|| (DataCollectDefine.FACTORY_ALU_FLAG == model.getFactory() 
						&& true == model.isClearable())) {// 清除告警
				// 清除告警具体逻辑操作
				updateClearAlarm(conn, model);
			} else {// 告警发生
				// 发生告警具体逻辑操作
				insertOccurAlarm(conn, getOccurMatchCon(model), model, operateType);
			}
		}
		// CommonUtil.timeDif("告警入库：", t1, new Date());
	}

	/**
	 * Method name: updateClearAlarm <BR>
	 * Description: 清除告警具体逻辑操作抽取的公共方法<BR>
	 * Remark: 2013-12-02<BR>
	 * 
	 * @author CaiJiaJia
	 * @return void<BR>
	 * @throws CommonException
	 */
	public void updateClearAlarm(DBCollection conn, AlarmDataModel model) throws CommonException {
		// 获取告警清除匹配条件
		BasicDBObject condition = getClearMatchCon(model);
		// 查询数据库匹配的记录
		DBObject alarmWillBeCleared = conn.findOne(condition);
		// 为防止有闪烁告警存在，如未找到需要清除的匹配告警，延时两秒后再次确认一下
		if (alarmWillBeCleared == null) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			alarmWillBeCleared = conn.findOne(condition);
		}
		// 如果记录数不为空，表示该条告警在当前告警表中存在，此时根据规则更新该条告警在数据库中的状态，否则该条告警为丢弃
		if (alarmWillBeCleared != null) {
			// 更新原始告警信息
			alarmWillBeCleared.put("ORIGINAL_INFO", model.getOriginalInfo());
			updateAlarm(alarmWillBeCleared, conn);
		} else {
			// 将应丢弃的告警清除的信息保存，以便进行告警不同步的问题分析用
			// 系统配置参数格式："保存开关(true/false),厂家代号(0-5/9)"，厂家代号"0"表示所有厂家
			Map<String, Object> paramMap = dataCollectMapper
					.getSystemParam(DataCollectDefine.WASTE_ALARM_CLEAR_KEY);
			if (paramMap == null) {
				// 未发现该系统参数
				return;
			}
			String values = paramMap.get("PARAM_VALUE").toString();
			String[] valueArray = values.split(",");
			if (valueArray.length != 2) {
				// 参数个数不对
				return;
			}
			boolean saveWasteAlm = Boolean.valueOf(valueArray[0]);
			if (!valueArray[1].matches("[0-9]+")) {
				// 第二个参数不是数字
				return;
			}
			int factory = Integer.valueOf(valueArray[1]);

			// 如果系统配置允许保存丢弃的告警清除信息，则进行保存操作
			if (saveWasteAlm && (factory == 0 || factory == model.getFactory())) {
				DBCollection conn1 = mongo.getDB(DataCollectDefine.MONGODB_NAME).getCollection(
						DataCollectDefine.T_WASTE_ALM_MSG);
				DBObject almMsg = new BasicDBObject();
				// 根据表名获取主键id的值
				int id = mongodbCommonService.getSequenceId(DataCollectDefine.T_WASTE_ALM_MSG);
				almMsg.put("_id", id);
				almMsg.put("ORIGINAL_INFO", model.getOriginalInfo());
				almMsg.put("CREATE_TIME", new Date());
				conn1.insert(almMsg);
			}
		}
	}

	/**
	 * Method name: updateAlarm <BR>
	 * Description: 更新清除告警<BR>
	 * Remark: 2014-02-27<BR>
	 * 
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	@SuppressWarnings("unchecked")
	public void updateAlarm(DBObject clearAlarm, DBCollection conn) throws CommonException {
		// 把清除时间更新为当前系统时间
		clearAlarm.put("CLEAR_TIME", new Date());
		// 把清除状态更新为已清除(1： 已清除 2: 未清除)
		clearAlarm.put("IS_CLEAR", DataCollectDefine.IS_CLEAR_YES);
		// 把更新时间更新为当前系统时间
		clearAlarm.put("UPDATE_TIME", new Date());

		// 处理告警反转状态(如果要清除的告警处于反转状态，则不再进行告警推送)
		if (clearAlarm.get("REVERSAL") != null && (Boolean) clearAlarm.get("REVERSAL")) {
			// 更新数据库
			conn.save(clearAlarm);
		} else {
			// 更新数据库
			try {
				conn.save(clearAlarm);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 推送实时告警信息
			pushAlarmMessage(clearAlarm.toMap(), DataCollectDefine.ALARM_STATUS_CLEARED);
		}

		// 获取该告警的确信息，如果未确认
		if (DataCollectDefine.IS_ACK_NO == (Integer) clearAlarm.get("IS_ACK")) {
			// 查询告警自动确认设置，判断是否需要立即确认
			boolean confirmFlag = judgeIsNeedConfirm((Integer) clearAlarm.get("EMS_ID"),
					(Integer) clearAlarm.get("PERCEIVED_SEVERITY"));
			
			// 如果需要立即确认 ,此时根据规则更新该条告警在数据库中的状态
			if (confirmFlag) {
				clearAlarm.put("ACK_USER", "系统");
				// 把确认状态更新为已确认(1： 已确认 2: 未确认)
				clearAlarm.put("IS_ACK", DataCollectDefine.IS_ACK_YES);
				// 更新确认时间
				clearAlarm.put("ACK_TIME", new Date());
				// 更新数据库
				conn.save(clearAlarm);
			} else {
				// 无需立即确认
				return;
			}
		}

		// 查询生命周期信息(告警转移设置)，判断是否需要立即转移到历史告警
		boolean lifeCycleFlag = judgeIsNeedShift();
		
		// 表示没有生命周期，立即将该条告警转移到历史表
		if (lifeCycleFlag) {
			// 从当前告警表删除
			conn.remove(clearAlarm);
			// 插入到历史告警表
			conn = mongo.getDB(DataCollectDefine.MONGODB_NAME).getCollection(
					DataCollectDefine.T_HISTORY_ALARM);
			//转入历史库时，保留原始的入库时间和更新时间
			//clearAlarm.put("UPDATE_TIME", "");
			//clearAlarm.put("CREATE_TIME", new Date());
			conn.insert(clearAlarm);
		}
	}

	/**
	 * Method name: insertOccurAlarm <BR>
	 * Description: 发生告警具体逻辑操作抽取的公共方法<BR>
	 * Remark: 2013-12-02<BR>
	 * 
	 * @author CaiJiaJia
	 * @return void<BR>
	 * @throws ParseException
	 * @throws CommonException
	 */
	@SuppressWarnings("unchecked")
	public void insertOccurAlarm(DBCollection conn, BasicDBObject condition, AlarmDataModel model,
			Integer operateType) throws CommonException {
		// 添加同步关键字，否则并发时会出现重复入库现象
		// FlashAlarmId不同时可异步处理
		synchronized (getFlashAlarmId(model).intern()) {
			// 定义时间格式转换器
			SimpleDateFormat sf = new SimpleDateFormat(DataCollectDefine.COMMON_FORMAT);
			// 查询数据库匹配的记录
			DBObject occurAlarm = conn.findOne(condition);
			// 如果记录数不为空，表示该条告警在当前告警表中存在，此时根据规则更新该条告警在数据库中的状态，否则插入该条告警
			if (occurAlarm != null) {
				// 更新最近发生时间
				try {
					occurAlarm.put("NE_TIME",
							!model.getNeTime().isEmpty() ? sf.parse(model.getNeTime()) : "");
				} catch (ParseException e) {
					throw new CommonException(e, MessageCodeDefine.CORBA_PARSE_EXCEPTION);
				}
				// 把更新时间更新为当前系统时间
				occurAlarm.put("UPDATE_TIME", new Date());
				// 更新清除告警标志
				occurAlarm.put("CLEAR_ALARM_ID", getClearAlarmId(model));
				// 更新原始告警信息
				occurAlarm.put("ORIGINAL_INFO", model.getOriginalInfo());

				// 同步告警
				if (operateType == DataCollectDefine.ALARM_TO_DB_TYPE_SYNCH) {
					// 更新告警附加信息
					Map<String, Object> alarmAdditionInfo = DataCollectServiceImpl
							.getTargetAdditionInfo1(model);
					occurAlarm.putAll(alarmAdditionInfoToTable(alarmAdditionInfo));

					occurAlarm.put(
							"FILTER_ALARM_NAME_FACTORY",
							occurAlarm.get("NATIVE_PROBABLE_CAUSE").toString() + ","
									+ occurAlarm.get("FACTORY"));// 过滤器告警名称和厂家联合匹配字段
					occurAlarm.put("FILTER_NE_MODEL_FACTORY", occurAlarm.get("PRODUCT_NAME")
							.toString() + "," + occurAlarm.get("FACTORY"));// 过滤器网元型号和厂家联合匹配字段

					occurAlarm.putAll(alarmRedefine(model));
					occurAlarm.putAll(alarmNormalize(model));

					// 对于数据库中存在的未清除告警，将更新“清除状态”和频次加1，并进行告警推送
					if (DataCollectDefine.IS_CLEAR_YES == (Integer) occurAlarm.get("IS_CLEAR")) {
						// 闪告次数加1
						occurAlarm.put("AMOUNT",
								Integer.parseInt(occurAlarm.get("AMOUNT").toString()) + 1);
						// 更新为未清除，清空清除时间
						occurAlarm.put("IS_CLEAR", DataCollectDefine.IS_CLEAR_NO);
						occurAlarm.put("CLEAR_TIME", "");
						// 处理告警反转状态，如果该告警已被反转则取消其反转
						if (occurAlarm.get("REVERSAL") != null
								&& (Boolean) occurAlarm.get("REVERSAL")) {
							occurAlarm.put("REVERSAL", false);
						}
						// 更新数据库
						try {
							conn.save(occurAlarm);
						} catch (Exception e) {
							e.printStackTrace();
						}

						// 推送实时告警信息给视图模块
						pushAlarmMessage(occurAlarm.toMap(), DataCollectDefine.ALARM_STATUS_OCCUR);

					} else {
						// 更新数据库
						conn.save(occurAlarm);
					}
					// 推送告警
				} else {
					// 闪告次数加1
					occurAlarm.put("AMOUNT",
							Integer.parseInt(occurAlarm.get("AMOUNT").toString()) + 1);
					// 更新为未清除，清空清除时间
					occurAlarm.put("IS_CLEAR", DataCollectDefine.IS_CLEAR_NO);
					occurAlarm.put("CLEAR_TIME", "");
					// 处理告警反转状态，如果该告警已被反转则取消其反转
					if (occurAlarm.get("REVERSAL") != null && (Boolean) occurAlarm.get("REVERSAL")) {
						occurAlarm.put("REVERSAL", false);
					}
					// 更新数据库
					try {
						conn.save(occurAlarm);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (operateType == DataCollectDefine.ALARM_TO_DB_TYPE_PUSH) {
						// 推送实时告警信息给视图模块
						pushAlarmMessage(occurAlarm.toMap(), DataCollectDefine.ALARM_STATUS_OCCUR);
					}
				}
			} else {// 表示该条告警在当前告警表中不存在，此时插入该条告警
				// 数据类型转换
				occurAlarm = new BasicDBObject();
				// 根据表名获取主键id的值
				int id = mongodbCommonService.getSequenceId(DataCollectDefine.T_CURRENT_ALARM);
				occurAlarm.put("_id", id);
				occurAlarm.put("NOTIFICATION_ID", model.getNotificationId());
				occurAlarm.put("DIRECTION", model.getDirection());// 方向
				occurAlarm.put("LOCATION", model.getLocation());// 位置
				occurAlarm.put("CONFIRM_STATUS_ORI", model.getConfirmStatusOri());// 确认信息
				occurAlarm.put("ACK_USER", "");// 确认用户,初始时为空,用户确认时更新
				occurAlarm.put("ACK_TIME", "");// 确认时间,初始时为空,用户确认时更新
				occurAlarm.put("IS_CLEARABLE", model.isClearable());// 是否可以清除
				occurAlarm.put("CLEAR_STATUS", model.getClearStatus());// 清除信息
				// 因华为设备无最近清除时间字段，使用的是EMS Time，初次获取的数据内容不为空，所以入库时将其置空
				occurAlarm.put("CLEAR_TIME", "");
				occurAlarm.put("HANDLING_SUGGESTION", model.getHandlingSuggestion());// 处理建议
				try {// 首次发生时间
					occurAlarm.put("FIRST_TIME",
							!model.getNeTime().isEmpty() ? sf.parse(model.getNeTime()) : "");
				} catch (ParseException e) {
					throw new CommonException(e, MessageCodeDefine.CORBA_PARSE_EXCEPTION);
				}
				try {// 最近发生时间
					occurAlarm.put("NE_TIME",
							!model.getNeTime().isEmpty() ? sf.parse(model.getNeTime()) : "");
				} catch (ParseException e) {
					throw new CommonException(e, MessageCodeDefine.CORBA_PARSE_EXCEPTION);
				}
				try {// 网管上报时间
					occurAlarm.put("EMS_TIME",
							!model.getEmsTime().isEmpty() ? sf.parse(model.getEmsTime()) : "");
				} catch (ParseException e) {
					throw new CommonException(e, MessageCodeDefine.CORBA_PARSE_EXCEPTION);
				}
				occurAlarm.put("ALARM_TYPE", model.getAlarmType());// 告警基本类型
				occurAlarm.put("SERVICE_AFFECTING", model.getServiceAffecting());// 影响业务
				occurAlarm.put("PERCEIVED_SEVERITY", model.getPerceivedSeverity());// 告警级别
				// 发生告警唯一性标识
				occurAlarm.put("FLASH_ALARM_ID", getFlashAlarmId(model));
				// 清除告警唯一性标识
				occurAlarm.put("CLEAR_ALARM_ID", getClearAlarmId(model));
				// 告警原因扩展
				occurAlarm.put("PROBABLE_CAUSE_QUALIFIER", model.getProbableCauseQualifier());
				occurAlarm.put("PROBABLE_CAUSE", model.getProbableCause());// 告警标准名
				occurAlarm.put("LAYER_RATE", model.getLayerRate());// 层速率
				occurAlarm.put("OBJECT_TYPE", model.getObjectType());// 告警源实体类型
				occurAlarm.put("NATIVE_PROBABLE_CAUSE", model.getNativeProbableCause());// 告警名称
				// occurAlarm.put("NORMAL_CAUSE","");// 归一化名称
				// 告警源的本地名称
				occurAlarm.put("NATIVE_EMS_NAME", model.getNativeEmsName());
				// 产生告警的实体名称
				occurAlarm.put("OBJECT_NAME", nameAndStringCom(model.getObjectName()));
				occurAlarm.put("EMS_ID", model.getEmsId());// 网管ID
				// 告警源实体类型扩展
				occurAlarm.put("OBJECT_TYPE_QUALIFIER", model.getObjectTypeQualifier());
				// 告警描述（ 华为）
				occurAlarm.put("ALARM_REASON", model.getAlarmReason());
				// 闪告次数，默认是1
				occurAlarm.put("AMOUNT", DataCollectDefine.AMOUNT);
				// 入库时间
				occurAlarm.put("CREATE_TIME", new Date());
				// 更新时间
				occurAlarm.put("UPDATE_TIME", "");
				// 厂家
				occurAlarm.put("FACTORY", model.getFactory());
				// 是否确认  1:已确认  2:未确认
				occurAlarm.put("IS_ACK", DataCollectDefine.IS_ACK_NO);
				// 是否清除   1:已清除  2:未清除
				occurAlarm.put("IS_CLEAR", DataCollectDefine.IS_CLEAR_NO);
				// 是够派单   1:派单  2:不派单
				occurAlarm.put("IS_ORDER", DataCollectDefine.IS_ORDER_NO);
				// 定位信息
				occurAlarm.put("LOCATION_INFO", model.getLocationInfo());
				// 添加告警反转字段
				occurAlarm.put("REVERSAL", false);
				// 添加子告警拥有标志（告警收敛用）
				occurAlarm.put("HAVE_CHILD", false);
				// 添加原始告警信息
				occurAlarm.put("ORIGINAL_INFO", model.getOriginalInfo());

				// 告警附加信息 需要调用接口
				Map<String, Object> alarmAdditionInfo = DataCollectServiceImpl
						.getTargetAdditionInfo1(model);

				occurAlarm.putAll(alarmAdditionInfoToTable(alarmAdditionInfo));
				// 增加告警收敛分析用字段
				occurAlarm.put("CONVERGE_FLAG", 1);
				occurAlarm.put("PARENT_ID", 0);
				// 新增的故障诊断状态
				occurAlarm.put("ANALYSIS_STATUS", DataCollectDefine.NEGATIVE);

				// Date t3 = new Date();
				occurAlarm.put("FILTER_ALARM_NAME_FACTORY", occurAlarm.get("NATIVE_PROBABLE_CAUSE")
						.toString() + "," + occurAlarm.get("FACTORY"));// 过滤器告警名称和厂家联合匹配字段
				occurAlarm.put("FILTER_NE_MODEL_FACTORY", occurAlarm.get("PRODUCT_NAME").toString()
						+ "," + occurAlarm.get("FACTORY"));// 过滤器网元型号和厂家联合匹配字段

				// 插入数据前，执行重定义
				occurAlarm.putAll(alarmRedefine(model));
				// Date t4 = new Date();
				// CommonUtil.timeDif("告警重定义：",t3 , t4);
				// 插入数据前，执行归一化
				occurAlarm.putAll(alarmNormalize(model));
				// Date t5 = new Date();
				// CommonUtil.timeDif("告警归一化：", t4, t5);
				// 在入库告警中附加采集服务器的IP或远程RMI客户端的IP
				occurAlarm.put("IP", getHostAddress());

				// 插入数据
				try {
					conn.insert(occurAlarm);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 推送实时告警信息给视图模块
				pushAlarmMessage(occurAlarm.toMap(), DataCollectDefine.ALARM_STATUS_OCCUR);
			}
		}
	}

	/**
	 * Method name: getClearMatchCon <BR>
	 * Description: 获取清除告警的匹配条件<BR>
	 * Remark: 2014-02-27<BR>
	 * 
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public BasicDBObject getClearMatchCon(AlarmDataModel model) {
		// 定义查询条件
		BasicDBObject condition = new BasicDBObject();
		// 各网管AlarmSerialNo可能重复,网管连接中断告警的AlarmSerialNo为""
		/* 已经在CLEAR_ALARM_ID中增加了emsId，这里将emsId条件删除 */
		// condition.put("EMS_ID",model.getEmsId());
		condition.put("CLEAR_ALARM_ID", getClearAlarmId(model));
		return condition;
	}

	public String getClearAlarmId(AlarmDataModel model) {
		/*
		 * 故障管理的BD已经更新，告警清除的匹配条件不再独立分割，而是采取与告警产生一致的条件
		 * mongodb中CLEAR_ALARM_ID字段的内容将和FLASH_ALARM_ID一样 
		 */
		return getFlashAlarmId(model);
	}

	/**
	 * Method name: getOccurMatchCon <BR>
	 * Description: 获取发生告警的匹配条件<BR>
	 * Remark: 2014-02-27<BR>
	 * 
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public String getFlashAlarmId(AlarmDataModel model) {
		String objectName;
		// 判断告警所属厂家(1:华为 2:中兴 3:朗讯 4:烽火 )
		switch (model.getFactory()) {
		case DataCollectDefine.FACTORY_HW_FLAG:// 表示华为
		case DataCollectDefine.FACTORY_LUCENT_FLAG:// 表示朗讯
		default:
			// 根据设备告警发生的唯一性匹配条件(emsId、objectName、probableCauseQualifier)
			// 查询数据库，看该条告警是否存在
			objectName = "" + model.getEmsId() + nameAndStringCom(model.getObjectName())
					+ model.getProbableCauseQualifier();
			return objectName;
		case DataCollectDefine.FACTORY_ZTE_FLAG:// 表示中兴
		case DataCollectDefine.FACTORY_FIBERHOME_FLAG:// 表示烽火
			// 根据设备告警发生的唯一性匹配条件(emsId、objectName、nativeProbableCause、layerRate)
			// 查询数据库，看该条告警是否存在
			objectName = "" + model.getEmsId() + nameAndStringCom(model.getObjectName())
					+ model.getNativeProbableCause() + model.getLayerRate();
			return objectName;
		case DataCollectDefine.FACTORY_ALU_FLAG:// 表示贝尔
			// 告警发生的唯一性匹配条件（emsId、objectName、nativeProbableCause、LayerRate）
			objectName = "" + model.getEmsId() + nameAndStringCom(model.getObjectName())
					+ model.getNativeProbableCause() + model.getLayerRate();
			return objectName;
		}
	}

	public BasicDBObject getOccurMatchCon(AlarmDataModel model) {
		// 定义查询条件
		BasicDBObject condition = new BasicDBObject();
		condition.put("FLASH_ALARM_ID", getFlashAlarmId(model));
		return condition;
	}

	/**
	 * Method name: nameAndStringCom <BR>
	 * Description: 将NameAndStringValue_T[]转成字符串<BR>
	 * Remark: 2013-12-02<BR>
	 * 
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public String nameAndStringCom(NameAndStringValue_T[] array) {
		String objectName = "";
		for (int i = 0; i < array.length; i++) {
			objectName += array[i].name + array[i].value;
		}
		return objectName;
	}

	/**
	 * Method name: judgeIsNeedShield <BR>
	 * Description: 根据厂家、告警名称、告警类型、告警级别、业务影响和<BR>
	 * 				网元Id(因为屏蔽器的源只能选网元级别),判断是否需要屏蔽<BR>
	 * Remark: 2014-02-08<BR>
	 * 
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void judgeIsNeedShield(List<AlarmDataModel> modelList, Integer emsConnectionId,
			Integer neId) {
		Map<String, Object> shieldMap = new HashMap<String, Object>();
		NameAndStringValueUtil nameUtil = new NameAndStringValueUtil();
		StringBuffer strBuild = new StringBuffer();
		List<AlarmDataModel> alm = new ArrayList<AlarmDataModel>();
		Map<String, Object> firstByEms = null;
		List<String> shieldByNULL = new ArrayList<String>();
		List<String> shieldByEms = new ArrayList<String>();
		// 复制告警列表
		for (AlarmDataModel model : modelList) {
			alm.add(model);
		}

		shieldMap.put("status", DataCollectDefine.ALARM_SHIELD_STATUS_ENABLE);
		// 获取未指定告警源的屏蔽模板
		List<Map<String, Object>> shieldListByNULL = dataCollectMapper
				.judgeIsNeedShieldByNULL(shieldMap);
		if (!shieldListByNULL.isEmpty() && shieldListByNULL.size() > 0
				&& !shieldListByNULL.get(0).isEmpty()) {
			for (Map<String, Object> item : shieldListByNULL) {
				strBuild.setLength(0);
				strBuild.append(item.get("FACTORY")).append(",");
				strBuild.append(item.get("NATIVE_PROBABLE_CAUSE")).append(",");
				strBuild.append(item.get("ALARM_TYPE")).append(",");
				strBuild.append(item.get("ALARM_LEVEL")).append(",");
				strBuild.append(item.get("ALARM_AFFECTING"));
				shieldByNULL.add(strBuild.toString());
			}
			for (AlarmDataModel model : alm) {
				if (!shieldByNULL.isEmpty()) {
					for (Map<String, Object> sl : shieldListByNULL) {
						strBuild.setLength(0);
						// 通过首个元素来判断屏蔽模板中哪些参数为空
						if (null != sl.get("FACTORY")) {
							strBuild.append(model.getFactory()).append(",");
							strBuild.append(model.getNativeProbableCause()).append(",");
						} else
							strBuild.append("null,null,");
						if (null != sl.get("ALARM_TYPE")) {
							strBuild.append(model.getAlarmType()).append(",");
						} else
							strBuild.append("null,");
						if (null != sl.get("ALARM_LEVEL")) {
							strBuild.append(model.getPerceivedSeverity()).append(",");
						} else
							strBuild.append("null,");
						if (null != sl.get("ALARM_AFFECTING")) {
							strBuild.append(model.getServiceAffecting());
						} else
							strBuild.append("null");
						if (shieldByNULL.contains(strBuild.toString())) {
							modelList.remove(model);
						}
					}
				}
			}

			if (modelList.isEmpty()) {
				return;
			}
			alm.clear();
			// 复制剩余告警列表
			for (AlarmDataModel model : modelList) {
				alm.add(model);
			}
		}

		shieldMap.put("deviceId", emsConnectionId);
		shieldMap.put("deviceType", DataCollectDefine.COMMON.TARGET_TYPE_EMS_FLAG);

		// 获取指定EMS的屏蔽模板
		List<Map<String, Object>> shieldListByEms = dataCollectMapper.judgeIsNeedShield(shieldMap);
		if (!shieldListByEms.isEmpty() && shieldListByEms.size() > 0
				&& !shieldListByEms.get(0).isEmpty()) {
			firstByEms = shieldListByEms.get(0);
			if (firstByEms != null) {
				for (Map<String, Object> item : shieldListByEms) {
					strBuild.setLength(0);
					strBuild.append(item.get("FACTORY")).append(",");
					strBuild.append(item.get("NATIVE_PROBABLE_CAUSE")).append(",");
					strBuild.append(item.get("ALARM_TYPE")).append(",");
					strBuild.append(item.get("ALARM_LEVEL")).append(",");
					strBuild.append(item.get("ALARM_AFFECTING"));
					shieldByEms.add(strBuild.toString());
				}
			}
		}

		// 按EMS进行屏蔽
		for (AlarmDataModel model : alm) {
			if (!shieldByEms.isEmpty()) {
				strBuild.setLength(0);
				// 通过首个元素来判断屏蔽模板中哪些参数为空
				if (null != firstByEms.get("FACTORY")) {
					strBuild.append(model.getFactory()).append(",");
					strBuild.append(model.getNativeProbableCause()).append(",");
				} else
					strBuild.append("null,null,");
				if (null != firstByEms.get("ALARM_TYPE")) {
					strBuild.append(model.getAlarmType()).append(",");
				} else
					strBuild.append("null,");
				if (null != firstByEms.get("ALARM_LEVEL")) {
					strBuild.append(model.getPerceivedSeverity()).append(",");
				} else
					strBuild.append("null,");
				if (null != firstByEms.get("ALARM_AFFECTING")) {
					strBuild.append(model.getServiceAffecting());
				} else
					strBuild.append("null");

				// 表示存在，说明此条告警需要屏蔽
				if (shieldByEms.contains(strBuild.toString())) {
					modelList.remove(model);
				}
			}
		}

		if (modelList.isEmpty()) {
			return;
		}
		alm.clear();
		// 复制剩余告警列表
		for (AlarmDataModel model : modelList) {
			alm.add(model);
		}

		// 按NE进行屏蔽
		shieldMap.put("deviceType", DataCollectDefine.COMMON.TARGET_TYPE_NE_FLAG);
		for (AlarmDataModel model : alm) {
			if (neId != null) {
				shieldMap.put("deviceId", neId);
			} else {
				// 取得网元序列号 589826
				String neSerialNo = nameUtil.getNeSerialNo(model.getObjectName());
				if (neSerialNo == null) { // 不是网元的告警，网元序列号为空
					shieldMap.put("deviceId", "");
				} else {
					// 组织查询语句
					Map select = new HashMap();
					select.put("NAME", "t_base_ne");
					select.put("ID_NAME", "BASE_EMS_CONNECTION_ID");
					select.put("ID_VALUE", emsConnectionId);
					select.put("ID_NAME_2", "NAME");
					select.put("ID_VALUE_2", neSerialNo);
					List<Map> list_ne = dataCollectMapper.getByParameter(select);
					// 获取网元ID
					if (list_ne != null && list_ne.size() > 0) {
						shieldMap.put("deviceId", list_ne.get(0).get("BASE_NE_ID") == null ? ""
								: list_ne.get(0).get("BASE_NE_ID"));
					} else {
						shieldMap.put("deviceId", "");
					}
				}
			}

			// 不是网元告警，不需要屏蔽器
			if ("".equals(shieldMap.get("deviceId").toString())) {
				continue;
			} else {
				// 查询指定neId的屏蔽模板
				List<Map<String, Object>> shieldList = dataCollectMapper
						.judgeIsNeedShield(shieldMap);
				if (!shieldList.isEmpty() && shieldList.size() > 0 && !shieldList.get(0).isEmpty()) {
					Map<String, Object> first = shieldList.get(0);
					if (first != null) {
						List<String> shieldByNe = new ArrayList<String>();
						for (Map<String, Object> item : shieldList) {
							strBuild.setLength(0);
							strBuild.append(item.get("FACTORY")).append(",");
							strBuild.append(item.get("NATIVE_PROBABLE_CAUSE")).append(",");
							strBuild.append(item.get("ALARM_TYPE")).append(",");
							strBuild.append(item.get("ALARM_LEVEL")).append(",");
							strBuild.append(item.get("ALARM_AFFECTING"));
							shieldByNe.add(strBuild.toString());
						}

						strBuild.setLength(0);
						// 通过首个元素来判断屏蔽模板中哪些参数为空
						if (null != first.get("FACTORY")) {
							strBuild.append(model.getFactory()).append(",");
							strBuild.append(model.getNativeProbableCause()).append(",");
						} else
							strBuild.append("null,null,");
						if (null != first.get("ALARM_TYPE")) {
							strBuild.append(model.getAlarmType()).append(",");
						} else
							strBuild.append("null,");
						if (null != first.get("ALARM_LEVEL")) {
							strBuild.append(model.getPerceivedSeverity()).append(",");
						} else
							strBuild.append("null,");
						if (null != first.get("ALARM_AFFECTING")) {
							strBuild.append(model.getServiceAffecting());
						} else
							strBuild.append("null");

						// 表示存在，说明此条告警需要屏蔽
						if (shieldByNe.contains(strBuild.toString())) {
							modelList.remove(model);
						}
					}
				}
			}
		} // 循环告警列表
	}

	/**
	 * Method name: judgeIsNeedConfirm <BR>
	 * Description: 根据网管Id,判断是否需要立即确认<BR>
	 * Remark: 2014-02-11<BR>
	 * 
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public boolean judgeIsNeedConfirm(int emsId, int alarmLv) {
		// 查询符合条件的屏蔽模板
		List<Map<String, Object>> confirmList = dataCollectMapper.judgeIsNeedConfirm(emsId);
		// 表示存在，说明此条告警需要屏蔽
		if (!confirmList.isEmpty()) {
			if (alarmLv == 1) {
				if (Integer.parseInt(confirmList.get(0).get("PS_CRITICAL_CONFIRM").toString()) == DataCollectDefine.ALARM_IMMEDIATELY_CONFIRM) {
					return true;
				} else {
					return false;
				}
			} else if (alarmLv == 2) {
				if (Integer.parseInt(confirmList.get(0).get("PS_MAJOR_CONFIRM").toString()) == DataCollectDefine.ALARM_IMMEDIATELY_CONFIRM) {
					return true;
				} else {
					return false;
				}
			} else if (alarmLv == 3) {
				if (Integer.parseInt(confirmList.get(0).get("PS_MINOR_CONFIRM").toString()) == DataCollectDefine.ALARM_IMMEDIATELY_CONFIRM) {
					return true;
				} else {
					return false;
				}
			} else if (alarmLv == 4) {
				if (Integer.parseInt(confirmList.get(0).get("PS_WARNING_CONFIRM").toString()) == DataCollectDefine.ALARM_IMMEDIATELY_CONFIRM) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Method name: judgeIsNeedShift <BR>
	 * Description: 判断是否需要立即转移到历史告警<BR>
	 * Remark: 2014-02-11<BR>
	 * 
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public boolean judgeIsNeedShift() {
		Map<String, Object> shiftMap = dataCollectMapper
				.getSystemParam(DataCollectDefine.ALARM_SHIFT_PARAM_KEY);
		String[] paramArr = shiftMap.get("PARAM_VALUE").toString().split(",");
		// 表示没有生命周期，需要立即转移到历史告警表
		if ("false".equals(paramArr[0])) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Method name: pushAlarmMessageToView <BR>
	 * Description: 推送实时告警信息给视图模块<BR>
	 * Remark: 2014-02-10<BR>
	 * 
	 * @param status
	 *            1：清除告警 2：产生告警
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void pushAlarmMessage(Map<String, Object> map, int status) throws CommonException {
		// 告警对象
		map.put("status", status);
		
		// 保存告警JMS消息稽核数据		
		ObjectId objId = new ObjectId();
		map.put("JMS_ID", objId);
		saveJmsMsg(map);
		try {
    		// 发送告警
    		JMSSender.sendMessage(DataCollectDefine.MESSAGE_TYPE_ALARM, map);
    		updateJmsMsg(objId, "STATUS", "发送成功");
    		updateJmsMsg(objId, "SEND_TIME", new Date());
		} catch (Exception e) {
			updateJmsMsg(objId, "STATUS", "发送失败");
    		updateJmsMsg(objId, "SEND_TIME", new Date());
    		e.printStackTrace();
		}
	}

	/**
	 * 查找数据库中有但当前告警列表中没有的非“清除”状态的告警，并执行“告警清除流程”
	 * 
	 * @param conn
	 * @param emsConnectionId
	 * @param neId
	 * @param curAlmList
	 * @throws CommonException
	 */
	public void clearAlarmNotInCurList(DBCollection conn, Integer emsConnectionId, Integer neId,
			List<AlarmDataModel> curAlmList) throws CommonException {
		// 处理未清除的告警，先查询数据库中该网管或者网元的所有未清除告警
		BasicDBObject condition = new BasicDBObject();

		if (neId == null) { // 同步网管告警
			condition.put("EMS_ID", emsConnectionId);
		} else {// 同步网元告警
			condition.put("NE_ID", neId);
		}
		// 未清除
		condition.put("IS_CLEAR", DataCollectDefine.IS_CLEAR_NO);
		// 查询该网管或者网元在数据库中的告警
		DBCursor alarm = conn.find(condition);
		// 循环比较，判断是否存在数据库里面有，但当前告警列表中没有
		while (alarm.hasNext()) {
			DBObject dbo = alarm.next();
			boolean find = false;
			for (int i = 0; i < curAlmList.size(); i++) {
				// 发生告警匹配标志
				String FLASH_ALARM_ID = getOccurMatchCon(curAlmList.get(i)).get("FLASH_ALARM_ID")
						.toString();
				if (neId == null) { // 同步网管告警
					if (dbo.get("FLASH_ALARM_ID").toString().equals(FLASH_ALARM_ID)) {
						find = true;
						break;
					}
				} else { // 同步网元告警
					if (dbo.get("FLASH_ALARM_ID").toString().equals(FLASH_ALARM_ID)) {
						// 数据库中存在指定网元的告警，不清除
						find = true;
						break;
					} else {
						if ((Integer) dbo.get("NE_ID") != neId) {
							// 数据库中不存在指定网元的告警，但数据库中告警的网元ID不是指定的neId时不清除
							find = true;
							break;
						}
					}
				}
			}
			// 数据库里有，新告警里面没有
			if (!find) {
				// 更新清除告警
				updateAlarm(dbo, conn);
			}
		}
	}

	// 告警重定义
	public Map<String, Object> alarmRedefine(AlarmDataModel model) {
		Map<String, Object> alarm = new HashMap<String, Object>();
		// 执行重定义
		Map<String, Object> redefineMap = new HashMap<String, Object>();
		redefineMap.put("status", DataCollectDefine.ALARM_REDEFINE_STATUS_ENABLE);
		redefineMap.put("alarmName", model.getNativeProbableCause());
		redefineMap.put("emsId", model.getEmsId());
		redefineMap.put("alarmLv", model.getPerceivedSeverity());
		List<Map<String, Object>> redefineList = dataCollectMapper.judgeIsNeedRedefine(redefineMap);
		if (!redefineList.isEmpty()) {
			// 不为空，需要重定义告警级别
			alarm.put("PERCEIVED_SEVERITY",
					Integer.parseInt(redefineList.get(0).get("NEW_ALARM_LEVEL").toString()));
		} else {
			alarm.put("PERCEIVED_SEVERITY", model.getPerceivedSeverity());
		}
		return alarm;
	}

	// 告警归一化
	public Map<String, Object> alarmNormalize(AlarmDataModel model) {
		Map<String, Object> alarm = new HashMap<String, Object>();
		// 执行归一化
		Map<String, Object> normalizedMap = new HashMap<String, Object>();
		normalizedMap.put("factory", model.getFactory());
		normalizedMap.put("alarmName", model.getNativeProbableCause());
		// BUG#2189
		// normalizedMap.put("normalAlarmName", model.getProbableCause());
		List<Map<String, Object>> normalizedList = dataCollectMapper
				.judgeIsNeedNormalized(normalizedMap);
		if (!normalizedList.isEmpty()) {
			alarm.put("NORMAL_CAUSE", normalizedList.get(0).get("REDEFINE_PROBABLE_CAUSE"));
		} else {
			alarm.put("NORMAL_CAUSE", "");
		}
		return alarm;
	}

	// 告警附加信息
	public Map<String, Object> alarmAdditionInfoToTable(Map<String, Object> alarmAdditionInfo) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 链路ID
		map.put("BASE_LINK_ID", alarmAdditionInfo.get("BASE_LINK_ID") == null ? ""
				: alarmAdditionInfo.get("BASE_LINK_ID"));
		// 保护组ID
		map.put("BASE_PRO_GROUP_ID", alarmAdditionInfo.get("BASE_PRO_GROUP_ID") == null ? ""
				: alarmAdditionInfo.get("BASE_PRO_GROUP_ID"));
		// 网管名称
		map.put("EMS_NAME",
				alarmAdditionInfo.get("EMS_NAME") == null ? "" : alarmAdditionInfo.get("EMS_NAME"));
		// 网元ID
		map.put("NE_ID",
				alarmAdditionInfo.get("NE_ID") == null ? "" : alarmAdditionInfo.get("NE_ID"));
		// 网元名称
		map.put("NE_NAME",
				alarmAdditionInfo.get("NE_NAME") == null ? "" : alarmAdditionInfo.get("NE_NAME"));
		// 网元型号
		map.put("PRODUCT_NAME", alarmAdditionInfo.get("PRODUCT_NAME") == null ? ""
				: alarmAdditionInfo.get("PRODUCT_NAME"));
		// 网元类型
		map.put("NE_TYPE",
				alarmAdditionInfo.get("NE_TYPE") == null ? "" : alarmAdditionInfo.get("NE_TYPE"));
		// 网管分组ID
		map.put("BASE_EMS_GROUP_ID", alarmAdditionInfo.get("BASE_EMS_GROUP_ID") == null ? ""
				: alarmAdditionInfo.get("BASE_EMS_GROUP_ID"));
		// 网管分组名称
		map.put("EMS_GROUP_NAME", alarmAdditionInfo.get("EMS_GROUP_NAME") == null ? ""
				: alarmAdditionInfo.get("EMS_GROUP_NAME"));
		// 区域ID
		map.put("AREA_ID",
				alarmAdditionInfo.get("AREA_ID") == null ? "" : alarmAdditionInfo.get("AREA_ID"));
		// 区域显示名称
		map.put("DISPLAY_AREA", alarmAdditionInfo.get("DISPLAY_AREA") == null ? ""
				: alarmAdditionInfo.get("DISPLAY_AREA"));
		// 局站ID
		map.put("STATION_ID",
				alarmAdditionInfo.get("STATION_ID") == null ? "" : alarmAdditionInfo
						.get("STATION_ID"));
		// 局站显示名称
		map.put("DISPLAY_STATION", alarmAdditionInfo.get("DISPLAY_STATION") == null ? ""
				: alarmAdditionInfo.get("DISPLAY_STATION"));
		// 机房ID
		map.put("RESOURCE_ROOM_ID", alarmAdditionInfo.get("RESOURCE_ROOM_ID") == null ? ""
				: alarmAdditionInfo.get("RESOURCE_ROOM_ID"));
		// 机房显示名称
		map.put("RESOURCE_ROOM", alarmAdditionInfo.get("RESOURCE_ROOM") == null ? ""
				: alarmAdditionInfo.get("RESOURCE_ROOM"));
		// 子网ID
		map.put("SUBNET_ID",
				alarmAdditionInfo.get("SUBNET_ID") == null ? "" : alarmAdditionInfo
						.get("SUBNET_ID"));
		// 子网名称
		map.put("SUBNET_NAME", alarmAdditionInfo.get("SUBNET_NAME") == null ? ""
				: alarmAdditionInfo.get("SUBNET_NAME"));
		// 机架
		map.put("RACK_NO",
				alarmAdditionInfo.get("RACK_NO") == null ? "" : alarmAdditionInfo.get("RACK_NO"));
		// 子架
		map.put("SHELF_NO",
				alarmAdditionInfo.get("SHELF_NO") == null ? "" : alarmAdditionInfo.get("SHELF_NO"));
		// 槽道
		map.put("SLOT_NO",
				alarmAdditionInfo.get("SLOT_NO") == null ? "" : alarmAdditionInfo.get("SLOT_NO"));
		// 业务类型
		map.put("DOMAIN",
				alarmAdditionInfo.get("DOMAIN") == null ? "" : alarmAdditionInfo.get("DOMAIN"));
		// 端口号
		map.put("PORT_NO",
				alarmAdditionInfo.get("PORT_NO") == null ? "" : alarmAdditionInfo.get("PORT_NO"));
		// 端口名
		map.put("PORT_NAME",
				alarmAdditionInfo.get("PORT_NAME") == null ? "" : alarmAdditionInfo
						.get("PORT_NAME"));
		// 端口类型
		map.put("PTP_TYPE",
				alarmAdditionInfo.get("PTP_TYPE") == null ? "" : alarmAdditionInfo.get("PTP_TYPE"));
		// 端口速率
		map.put("INTERFACE_RATE", alarmAdditionInfo.get("INTERFACE_RATE") == null ? ""
				: alarmAdditionInfo.get("INTERFACE_RATE"));
		// 板卡ID
		map.put("UNIT_ID",
				alarmAdditionInfo.get("UNIT_ID") == null ? "" : alarmAdditionInfo.get("UNIT_ID"));
		// 板卡名称
		map.put("UNIT_NAME",
				alarmAdditionInfo.get("UNIT_NAME") == null ? "" : alarmAdditionInfo
						.get("UNIT_NAME"));
		// 板卡描述
		map.put("UNIT_DESC",
				alarmAdditionInfo.get("UNIT_DESC") == null ? "" : alarmAdditionInfo
						.get("UNIT_DESC"));
		// 通道名
		map.put("CTP_NAME",
				alarmAdditionInfo.get("CTP_NAME") == null ? "" : alarmAdditionInfo.get("CTP_NAME"));
		// 通道ID
		map.put("CTP_ID",
				alarmAdditionInfo.get("CTP_ID") == null ? "" : alarmAdditionInfo.get("CTP_ID"));
		// 通道类型
		map.put("CTP_TYPE",
				alarmAdditionInfo.get("CTP_TYPE") == null ? "" : alarmAdditionInfo.get("CTP_TYPE"));
		// 包机人ID
		map.put("INSPECT_ENGINEER_ID", alarmAdditionInfo.get("INSPECT_ENGINEER_ID") == null ? ""
				: alarmAdditionInfo.get("INSPECT_ENGINEER_ID"));
		// 包机人
		map.put("INSPECT_ENGINEER", alarmAdditionInfo.get("INSPECT_ENGINEER") == null ? ""
				: alarmAdditionInfo.get("INSPECT_ENGINEER"));
		// 子架ID
		map.put("SHELF_ID",
				alarmAdditionInfo.get("SHELF_ID") == null ? "" : alarmAdditionInfo.get("SHELF_ID"));
		// 子板卡ID
		map.put("SUB_UNIT_ID", alarmAdditionInfo.get("SUB_UNIT_ID") == null ? ""
				: alarmAdditionInfo.get("SUB_UNIT_ID"));
		// 端口ID
		map.put("PTP_ID",
				alarmAdditionInfo.get("PTP_ID") == null ? "" : alarmAdditionInfo.get("PTP_ID"));
		
		StringBuilder sb = new StringBuilder();
		// 槽道的显示名称是由几个字段的display_name拼接而成
		if (alarmAdditionInfo.get("SLOT_DISPLAY_NAME") != null) {
			sb.append(alarmAdditionInfo.get("RACK_DISPLAY_NAME")).append("-");
			sb.append(alarmAdditionInfo.get("SHELF_DISPLAY_NAME")).append("-");
			sb.append(alarmAdditionInfo.get("SLOT_DISPLAY_NAME"));
			map.put("SLOT_DISPLAY_NAME", sb.toString());
		} else {
			map.put("SLOT_DISPLAY_NAME", "");
		}

		/** 原名规范名 */
		String[] copyList = new String[] { "NE_NATIVE_EMS_NAME", "NE_USER_LABEL",
				"UNIT_NATIVE_EMS_NAME", "UNIT_USER_LABEL", "PTP_NATIVE_EMS_NAME", "PTP_USER_LABEL",
				"CTP_NATIVE_EMS_NAME", "CTP_USER_LABEL", };
		for (String copy : copyList) {
			map.put(copy, alarmAdditionInfo.get(copy) == null ? "" : alarmAdditionInfo.get(copy));
		}
		// 槽道的原始名称是由几个字段的NATIVE_EMS_NAME拼接而成
		if (alarmAdditionInfo.get("SLOT_NATIVE_EMS_NAME") != null) {
			sb.setLength(0);
			sb.append(alarmAdditionInfo.get("RACK_NATIVE_EMS_NAME")).append("-");
			sb.append(alarmAdditionInfo.get("SHELF_NATIVE_EMS_NAME")).append("-");
			sb.append(alarmAdditionInfo.get("SLOT_NATIVE_EMS_NAME"));
			map.put("SLOT_NATIVE_EMS_NAME", sb.toString() );
		} else {
			map.put("SLOT_NATIVE_EMS_NAME", "");
		}
		// 槽道的规范名称是由几个字段的USER_LABEL拼接而成
		if (alarmAdditionInfo.get("SLOT_USER_LABEL") != null) {
			sb.setLength(0);
			sb.append(alarmAdditionInfo.get("RACK_USER_LABEL")).append("-");
			sb.append(alarmAdditionInfo.get("SHELF_USER_LABEL")).append("-");
			sb.append(alarmAdditionInfo.get("SLOT_USER_LABEL"));
			map.put("SLOT_USER_LABEL", sb.toString());
		} else {
			map.put("SLOT_USER_LABEL", "");
		}
		/** 原名规范名 */
		/** 影响对象 */
		if(alarmAdditionInfo.get("affectTPs")!=null){
			map.put("AFFECT_TPS", net.sf.json.JSONArray.fromObject(alarmAdditionInfo.get("affectTPs")).toString());
		}
		/** 影响对象 */
		return map;
	}

	/**
	 * 获取RMI客户端的iP地址
	 * 
	 * @return
	 */
	private String getHostAddress() {
		try {
			String clientHost;
			InetAddress ia;
			// 获取远端RMI客户端IP
			clientHost = RemoteServer.getClientHost();
			ia = java.net.InetAddress.getByName(clientHost);
			// 返回RMI客户端IP和本地采集服务IP
			return "R:" + ia.getHostAddress() + ",L:"
					+ java.net.InetAddress.getLocalHost().getHostAddress();
		} catch (ServerNotActiveException e) {
			// 如果不是远端调用则获取本地采集IP
			try {
				return "L:" + java.net.InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
				return "【】";
			}
			// return "";
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return "【】";
		}
	}

	/**
	 * 原始告警消息保存
	 */
	void oriAlmMsgSave(AlarmDataModel almData) throws CommonException {
		// 系统配置参数格式："保存开关(true/false),厂家代号(0-5/9)"，厂家代号"0"表示所有厂家
		Map<String, Object> paramMap = dataCollectMapper
				.getSystemParam(DataCollectDefine.ALARM_MSG_SAVE_KEY);
		if (paramMap == null) {
			// 未发现该系统参数
			return;
		}
		String values = paramMap.get("PARAM_VALUE").toString();
		String[] valueArray = values.split(",");
		if (valueArray.length != 2) {
			// 参数个数不对
			return;
		}
		if (!"true".equals(valueArray[0].toLowerCase())
				&& !"false".equals(valueArray[0].toLowerCase())) {
			// 开关参数不对
			return;
		}
		boolean saveAlmMsg = Boolean.valueOf(valueArray[0]);
		if (!valueArray[1].matches("[0-9]+")) {
			// 第二个参数不是数字
			return;
		}
		int factory = Integer.valueOf(valueArray[1]);

		// 如果系统配置允许保存丢弃的告警清除信息，则进行保存操作
		if (saveAlmMsg && (factory == 0 || factory == almData.getFactory())) {
			DBCollection conn1 = mongo.getDB(DataCollectDefine.MONGODB_NAME).getCollection(
					DataCollectDefine.T_ALARM_MSG);
			DBObject almMsg = new BasicDBObject();

			// 根据表名获取主键id的值
			int id = mongodbCommonService.getSequenceId(DataCollectDefine.T_ALARM_MSG);
			almMsg.put("_id", id);
			almMsg.put("EMS_ID", almData.getEmsId());
			almMsg.put("ALM_SEVERITY", almData.getPerceivedSeverity());
			almMsg.put("ORIGINAL_INFO", almData.getOriginalInfo());

			// 如原始告警信息JSON字符串为空，则保存告警唯一性条件相关字段
			if (almData.getOriginalInfo() == null) {
				// 按告警所属厂家保存告警唯一性条件字段(1:华为 2:中兴 3:朗讯 4:烽火 )
				switch (almData.getFactory()) {
				case DataCollectDefine.FACTORY_HW_FLAG:// 表示华为
				case DataCollectDefine.FACTORY_LUCENT_FLAG:// 表示朗讯
				default:
					// 保存设备告警发生的唯一性匹配条件(emsId、objectName、probableCauseQualifier)
					almMsg.put("OBJECT_NAME", nameAndStringCom(almData.getObjectName()));
					almMsg.put("PROBABLE_CAUSE_QUALIFIER", almData.getProbableCauseQualifier());
					break;
				case DataCollectDefine.FACTORY_ZTE_FLAG:// 表示中兴
				case DataCollectDefine.FACTORY_FIBERHOME_FLAG:// 表示烽火
					// 保存设备告警发生的唯一性匹配条件(emsId、objectName、nativeProbableCause、layerRate)
					almMsg.put("OBJECT_NAME", nameAndStringCom(almData.getObjectName()));
					almMsg.put("NATIVE_PROBABLE_CAUSE", almData.getNativeProbableCause());
					almMsg.put("LAYER_RATE", almData.getLayerRate());
					break;
				case DataCollectDefine.FACTORY_ALU_FLAG:// 表示贝尔
					// 保存设备告警发生的唯一性匹配条件（emsId、objectName、nativeProbableCause、LayerRate）
					almMsg.put("OBJECT_NAME", nameAndStringCom(almData.getObjectName()));
					almMsg.put("NATIVE_PROBABLE_CAUSE", almData.getNativeProbableCause());
					almMsg.put("LAYER_RATE", almData.getLayerRate());
					break;
				}
				almMsg.put("FLASH_ALARM_ID", getFlashAlarmId(almData));
			}
			almMsg.put("CREATE_TIME", new Date());

			conn1.insert(almMsg);
		}
	}
	
	/**
	 * 告警JMS稽核数据保存
	 * @param id
	 * @param alm
	 */
	private void saveJmsMsg(Map<String, Object> alm) {
		try {
			if (ip == null)
				ip = java.net.InetAddress.getLocalHost().getHostAddress();
			if (host == null)
				host = java.net.InetAddress.getLocalHost().getHostName();
			StringBuilder source = new StringBuilder();
			source.append(ip).append("@").append(host);

			DBCollection conn = mongo.getDB(DataCollectDefine.MONGODB_NAME)
					.getCollection(DataCollectDefine.ALARM_JMS_AUDIT);
			BasicDBObject dbo = new BasicDBObject();
			dbo.put("_id", alm.get("JMS_ID"));
			dbo.put("ALARM_ID", alm.get("_id"));
			dbo.put("SOURCE", source.toString());
			dbo.put("EMS_NAME",alm.get("EMS_NAME"));
			dbo.put("EMS_ID", alm.get("EMS_ID"));
			dbo.put("STATUS", "初始值");
			conn.insert(dbo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 更新告警JMS稽核数据的特定字段
	 * @param id
	 * @param key
	 * @param data
	 */
	private void updateJmsMsg(ObjectId id, String key, Object data) {
		DBCollection conn = mongo.getDB("test").getCollection("ALARM_JMS_AUDIT");
		BasicDBObject cond = new BasicDBObject("_id",id);
		BasicDBObject update = new BasicDBObject(key, data);
		conn.update(cond, new BasicDBObject("$set", update));
	}
}
