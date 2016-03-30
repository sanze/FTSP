package com.fujitsu.manager.dataCollectManager.service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import org.omg.CosNaming.Binding;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.BindingType;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import com.fujitsu.IService.IEMSCollect;
import com.fujitsu.IService.IEMSSession;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.DataCollectDefine;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.CrossConnectModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.FdfrModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.FlowDomainModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.PmDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.SubnetworkConnectionModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.TopologicalLinkModel;
import com.fujitsu.manager.dataCollectManager.serviceImpl.ALUCorba.ALUDataToModel;
import com.fujitsu.manager.dataCollectManager.serviceImpl.FIMCorba.FIMDataToModel;
import com.fujitsu.manager.dataCollectManager.serviceImpl.HWCorba.HWDataToModel;
import com.fujitsu.manager.dataCollectManager.serviceImpl.LUCENTCorba.LUCENTDataToModel;
import com.fujitsu.manager.dataCollectManager.serviceImpl.ZTEU31Corba.ZTEU31DataToModel;
import com.fujitsu.util.CSVUtil;
import com.fujitsu.util.CommonUtil;
import com.fujitsu.util.FtpUtils;
import com.fujitsu.util.NameAndStringValueUtil;

public abstract class EMSCollectService implements IEMSCollect {

	protected String corbaName;
	protected String corbaPassword;
	protected String corbaIp;
	protected String corbaPort;
	protected String emsName;
	protected String internalEmsName;

	protected NameAndStringValueUtil nameUtil = new NameAndStringValueUtil();

	protected HWDataToModel hwDataToModel ;
	protected ZTEU31DataToModel zteU31DataToModel ;
	protected FIMDataToModel fimDataToModel ;
	protected ALUDataToModel aluDataToModel ;
	protected LUCENTDataToModel lucentDataToModel ;

	public static HashMap<String, Object> orbMap = new HashMap<String, Object>();
	
	public static HashMap<String, IEMSSession> sessionMap = new HashMap<String, IEMSSession>();

	public static HashMap<String, Object> pushSupplierMap = new HashMap<String, Object>();
	
	public static Map<String,Date> heartBeatReceiveTime = new HashMap<String,Date>();
	
	public static Map<String,Date> pingReceiveTime = new HashMap<String,Date>();

	public static Map<String,Timer> heartBeatTimerMap = new HashMap<String,Timer>();
	
	public static Map<String,Timer> pingTimerMap = new HashMap<String,Timer>();
	
	public static Map<Integer,Boolean> needOccouredAlarmMap = new HashMap<Integer,Boolean>();
	
	protected static final Pattern IPV4_PATTERN = 
	        Pattern.compile(
	                "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");	
	
	protected void initParameter(String corbaName, String corbaPassword,
			String corbaIp, String corbaPort, String emsName,
			String internalEmsName){
		
		this.corbaName = corbaName;
		this.corbaPassword = corbaPassword;
		this.corbaIp = corbaIp;
		this.corbaPort = corbaPort;
		this.emsName = emsName;
		this.internalEmsName = internalEmsName;
	}
	
	
	/**
	 * @return
	 * @throws CommonException
	 */
	public static Object getDataThread(Callable<Object> thread)
			throws CommonException {

		ExecutorService executorService = Executors.newSingleThreadExecutor();
		// 添加采集进程
		FutureTask<Object> future = new FutureTask<Object>(thread);
		// 执行采集进程
		executorService.submit(future);

		Object data = null;
		try {
			data = future.get(10, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_INTERRUPTED_EXCEPTION);
		} catch (ExecutionException e) {
			if (CommonException.class.isInstance(e.getCause())) {
				throw new CommonException(e,
						((CommonException) e.getCause()).getErrorCode(),
						((CommonException) e.getCause()).getErrorMessage());
			} else {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_EXCUTION_EXCEPTION);
			}
		} catch (TimeoutException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_TIMEOUT_EXCEPTION);
		} finally {
			executorService.shutdown();
		}
		return data;
	}

	// 获取命名树
	public static ArrayList<NameComponent[]> getAllEntityPathWithContext(
			NamingContextExt namingContext, ArrayList<NameComponent> parent) {
		ArrayList<NameComponent[]> EntityPath = new ArrayList<NameComponent[]>();
		BindingListHolder arg1 = new BindingListHolder();
		BindingIteratorHolder arg2 = new BindingIteratorHolder();
		namingContext.list(1, arg1, arg2);
		boolean uend = true;
		do {
			for (Binding name : arg1.value) {
				try {
					// for(NameComponent binding_name:name.binding_name)
					// namingTree.add(tab+binding_name.id+":"+binding_name.kind);
					if (name.binding_type.value() == BindingType._ncontext) {
						org.omg.CORBA.Object objRef = namingContext
								.resolve(name.binding_name);
						NamingContextExt nameContext = NamingContextExtHelper
								.narrow(objRef);
						for (NameComponent binding_name : name.binding_name) {
							if (parent == null)
								parent = new ArrayList<NameComponent>();
							ArrayList<NameComponent> tmpParent = new ArrayList<NameComponent>();
							tmpParent.addAll(parent);
							tmpParent.add(binding_name);
							ArrayList<NameComponent[]> tmpEntity = getAllEntityPathWithContext(
									nameContext, tmpParent);
							if (tmpEntity != null)
								EntityPath.addAll(tmpEntity);
						}
					} else if (name.binding_type.value() == BindingType._nobject) {
						for (NameComponent binding_name : name.binding_name) {
							if (parent == null)
								parent = new ArrayList<NameComponent>();
							ArrayList<NameComponent> tmpParent = new ArrayList<NameComponent>();
							tmpParent.addAll(parent);
							tmpParent.add(binding_name);
							NameComponent[] arrayComponents = new NameComponent[tmpParent
									.size()];
							arrayComponents = tmpParent
									.toArray(arrayComponents);
							EntityPath.add(arrayComponents);
						}
					}
				} catch (Exception e) {
					System.err.println(e);
				}
			}
			uend = (arg2 == null || arg2.value == null) ? false : arg2.value
					.next_n(1, arg1);
		} while (uend);
		return EntityPath;
	}

	/**
	 * 文件是否完成判断方法
	 * @param ftpUtils
	 * @param fileName
	 * @return
	 * @throws CommonException
	 */
	public static boolean isFileTransferComplete(FtpUtils ftpUtils,
			String fileName) throws CommonException {

		boolean result = false;

		int historyPmCommandIntervalTime = Integer
				.valueOf(CommonUtil
						.getSystemConfigProperty(DataCollectDefine.HISTORY_PM_COMMAND_INTERVAL_TIME));

		//等待时间设置为99分钟即可认为是无限等待，利用数据采集本身的超时机制来控制
		long waiteTime = 99*60*1000;
		//文件当前大小
		long curSize = ftpUtils.getFileSize("", fileName);
		//文件不存在情况
		long currentTime = new Date().getTime();

		while(curSize == -1&&(new Date().getTime()-currentTime)<=waiteTime){
			//休眠10秒
			try {
				Thread.sleep(1000 * 10);
			} catch (InterruptedException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_INTERRUPTED_EXCEPTION);
			}
			curSize = ftpUtils.getFileSize("", fileName);
		}
		//规定时间未传输文件--返回false
		if(curSize == -1){
			return false;
		}else{
			//有文件，判断是否传输完成，暂定8分钟
			long lastSize = curSize;
			while((new Date().getTime()-currentTime)<=waiteTime){
				//休眠10秒
				try {
					Thread.sleep(1000 * 10);
				} catch (InterruptedException e) {
					throw new CommonException(e,
							MessageCodeDefine.CORBA_INTERRUPTED_EXCEPTION);
				}
				curSize = ftpUtils.getFileSize("", fileName);
				//文件未增长返回true
				if(lastSize == curSize){
					result = true;
					break;
				}else{
					lastSize = curSize;
				}
			}
		}
		if (result) {
			//休息10秒让文件下载到本地，经测试此时直接去取会导致文件拿不到
			try {
				Thread.sleep(historyPmCommandIntervalTime*10*1000);
			} catch (InterruptedException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_INTERRUPTED_EXCEPTION);
			}
		}
		return result;
	}
	
	// 构造FTP文件完整路径
	public static String constructFtpDestination(String ip, int port, String fileName, int factory){

		switch(factory){
		case DataCollectDefine.FACTORY_HW_FLAG:
		case DataCollectDefine.FACTORY_LUCENT_FLAG:
			return ip+":/"+fileName; //华为不支持端口,只能是默认的21
		case DataCollectDefine.FACTORY_FIBERHOME_FLAG:
			return ip+"/"+fileName;//烽火也不支持端口,且不能带冒号
		case DataCollectDefine.FACTORY_ZTE_FLAG:
			return ip+"|/"+fileName+"|"+port;
		case DataCollectDefine.FACTORY_ALU_FLAG:
			return ip+":./"+fileName;
		default:
			return ip+":"+port+":/"+fileName;
		}
	}
	
	/**
	 * @param fileName
	 * @return
	 * @throws CommonException
	 */
	protected List<PmDataModel> getPmDataFromFtp(String ip, int port,
			String userName, String password, String fileName, int factory,
			String startTime, String endTime) throws CommonException {
		//获取ftp工具类
		FtpUtils ftpUtils = new FtpUtils(ip,port,userName,password);
		// 采集数据集合
		List<PmDataModel> data = new ArrayList<PmDataModel>();
		// 获取下载至本地的缓存文件
		File file = getHistoryPMTempFile(ftpUtils, fileName);
		
		int checkTime = 0;
		//循环三次获取文件
		while(file == null&&checkTime<=3){
			try {
				Thread.sleep(30*1000);
			} catch (InterruptedException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_INTERRUPTED_EXCEPTION);
			}
			file = getHistoryPMTempFile(ftpUtils, fileName);
			checkTime++;
		}
		//如果还没取到,网络实在太差，只能报错了
		if (file == null) {
			String errorMessage = "性能文件传输超时！";
			throw new CommonException(new NullPointerException(),
					MessageCodeDefine.CORBA_FILE_DOWNLOAD_EXCEPTION,errorMessage);
		}
		//是否utc时间
		boolean isUtcTime = false;
		SimpleDateFormat retrievalTimeFormat = CommonUtil
				.getDateFormatter((DataCollectDefine.RETRIEVAL_TIME_FORMAT));
		switch(factory){
		case DataCollectDefine.FACTORY_HW_FLAG:
			// 从历史性能数据文件中读取性能数据
			List<HW.performance.PMData_T> pmDataList_HW = CSVUtil.readCsvFileForHW(file, hwDataToModel.encode);
			// 数据转换
			if (pmDataList_HW != null) {
				for (HW.performance.PMData_T pmData : pmDataList_HW) {
					PmDataModel model = hwDataToModel.PMDataToModel(pmData);
					//过滤非采集时间段的性能数据--华为实测会出现两天的数据
					if(model.getRetrievalTime().toUpperCase().contains("Z")){
						//UTC时间，需要加8小时
						isUtcTime = true;
					}
					try{
						if(isPmTimeAllowed(
								retrievalTimeFormat.parse(startTime),
								retrievalTimeFormat.parse(endTime),
								model.getRetrievalTimeDisplay(),isUtcTime)){
							data.add(model);
						}
					}catch(Exception e){
						data.add(model);
					}
				}
			}
			break;
		case DataCollectDefine.FACTORY_ZTE_FLAG:
			// 从历史性能数据文件中读取性能数据
			List<ZTE_U31.performance.PMData_T> pmDataList_ZTE = CSVUtil.readCsvFileForZTE(file, zteU31DataToModel.encode);
			// 数据转换
			if (pmDataList_ZTE != null) {
				for (ZTE_U31.performance.PMData_T pmData : pmDataList_ZTE) {
					PmDataModel model = zteU31DataToModel.PMDataToModel(pmData);
					//保险起见,按照,"T1-48小时"~T1-24小时"过滤
					try{
						if(isPmTimeAllowed(
								CommonUtil.getSpecifiedDay(retrievalTimeFormat.parse(startTime),1,0),
								retrievalTimeFormat.parse(endTime),
								model.getRetrievalTimeDisplay(),isUtcTime)){
							data.add(model);
						}
					}catch(Exception e){
						data.add(model);
					}
				}
			}
			break;
		case DataCollectDefine.FACTORY_FIBERHOME_FLAG:
			// 从历史性能数据文件中读取性能数据
			List<FENGHUO.performance.PMData_T> pmDataList = CSVUtil.readCsvFileForFIM(file, fimDataToModel.encode);
			// 数据转换
			if (pmDataList != null) {
				for (FENGHUO.performance.PMData_T pmData : pmDataList) {
					PmDataModel model = fimDataToModel.PMDataToModel(pmData);
					//烽火时间规律还没研究透,按汤健指示取值区间(T1-72h)~T1,筛选区间(T1-48h)~(T1-24h)
					try{
						if(isPmTimeAllowed(
								CommonUtil.getSpecifiedDay(retrievalTimeFormat.parse(startTime),1,0),
								CommonUtil.getSpecifiedDay(retrievalTimeFormat.parse(endTime),-1,0),
								model.getRetrievalTimeDisplay(),isUtcTime)){
							data.add(model);
						}
					}catch(Exception e){
						data.add(model);
					}
				}
			}
			break;
		case DataCollectDefine.FACTORY_LUCENT_FLAG:
			// 从历史性能数据文件中读取性能数据
			List<LUCENT.performance.PMData_T> pmDataList_LUCENT = CSVUtil.readCsvFileForLUCENT(file, lucentDataToModel.encode);
			// 数据转换
			if (pmDataList_LUCENT != null) {
				for (LUCENT.performance.PMData_T pmData : pmDataList_LUCENT) {
					PmDataModel model = lucentDataToModel.PMDataToModel(pmData);
					//过滤非采集时间段的性能数据--华为实测会出现两天的数据
					if(model.getRetrievalTime().toUpperCase().contains("Z")){
						//UTC时间，需要加8小时
						isUtcTime = true;
					}
					try{
						//朗讯不做时间删选
//						if(isPmTimeAllowed(
//								retrievalTimeFormat.parse(startTime),
//								retrievalTimeFormat.parse(endTime),
//								model.getRetrievalTimeDisplay(),isUtcTime)){
//							data.add(model);
//						}
						data.add(model);
					}catch(Exception e){
						data.add(model);
					}
				}
			}
			break;
		case DataCollectDefine.FACTORY_ALU_FLAG:
			// 从历史性能数据文件中读取性能数据
			List<ALU.performance.PMData_T> pmDataList_ALU = CSVUtil.readCsvFileForALU(file, aluDataToModel.encode);
			// 数据转换
			if (pmDataList_ALU != null) {
				for (ALU.performance.PMData_T pmData : pmDataList_ALU) {
					PmDataModel model = aluDataToModel.PMDataToModel(pmData);
					try{
//						if(isPmTimeAllowed(
//								CommonUtil.getSpecifiedDay(retrievalTimeFormat.parse(startTime),1,0),
//								CommonUtil.getSpecifiedDay(retrievalTimeFormat.parse(endTime),-1,0),
//								model.getRetrievalTimeDisplay(),isUtcTime)){
//							data.add(model);
//						}
						data.add(model);
					}catch(Exception e){
						data.add(model);
					}
				}
			}
			break;
		}
		
		// 删除缓存文件
		if (file.exists()) {
			
			Calendar cal = Calendar.getInstance();// 使用日历类
			int year = cal.get(Calendar.YEAR);// 得到年
			int month = cal.get(Calendar.MONTH) + 1;// 得到月，因为从0开始的，所以要加1
			int day = cal.get(Calendar.DAY_OF_MONTH);// 得到天
			// 解析完成之后 --移除文件
			String destFile = year + "年/" + month + "月/" + day+"日";
			try {
				destFile =  new String(destFile.getBytes("GBK"), "iso-8859-1");
			} catch (UnsupportedEncodingException e) {
				throw new CommonException(new NullPointerException(),
						MessageCodeDefine.CORBA_UNSUPPORTED_ENCODING_EXCEPTION);
			} 
			//转移文件
			boolean uploadResult = ftpUtils.uploadFile(file.getPath(),destFile,file.getName());
			//删除缓存文件
			file.delete();
			//删除原始文件
			if(uploadResult){
				ftpUtils.deleteFile(file.getName());
			}
		}
		return data;
	}
	
	//判断性能日期是否是需要采集的性能日期
	private static boolean isPmTimeAllowed(Date startTime,Date endTime,Date retrievalTime,boolean isUtcTime){
		Calendar c = Calendar.getInstance();
		//获取pm数据中的年月日
		c.setTime(retrievalTime);
		//如果是UTC时间，需要减8小时后再和目标时间对比
		if(isUtcTime){
			int hour = c.get(Calendar.HOUR);
			c.set(Calendar.HOUR, hour - 8);
		}
		retrievalTime = c.getTime();
//		int pmYear = c.get(Calendar.YEAR);
//		int pmMonth = c.get(Calendar.MONTH);
//		int pmDay = c.get(Calendar.DATE);
		//获取目标开始时间
		Date targetStartTime = startTime;
//		SimpleDateFormat retrievalTimeFormat = CommonUtil
//				.getDateFormatter((DataCollectDefine.RETRIEVAL_TIME_FORMAT));
//		try {
//			targetStartTime = retrievalTimeFormat.parse(startTime);
//		} catch (ParseException e) {
//		}
//		c.setTime(targetStartTime);
//		int pmTargetYear = c.get(Calendar.YEAR);
//		int pmTargetMonth = c.get(Calendar.MONTH);
//		int pmTargetDay = c.get(Calendar.DATE);
		
		//获取目标结束时间
		Date targetEndTime = endTime;
//		try {
//			targetEndTime = retrievalTimeFormat.parse(endTime);
//		} catch (ParseException e) {
//		}
//		c.setTime(targetEndTime);
//		int pmTargetYear = c.get(Calendar.YEAR);
//		int pmTargetMonth = c.get(Calendar.MONTH);
//		int pmTargetDay = c.get(Calendar.DATE);
		
		//性能文件中性能在采集时间范围内
//		if(retrievalTime.after(targetStartTime)&&retrievalTime.before(targetEndTime)){
		if (!(retrievalTime.compareTo(targetStartTime) < 0)
				&& !(retrievalTime.compareTo(targetEndTime) > 0)) {
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * link去重
	 * @param data 
	 * @return
	 * @throws CommonException
	 */
	protected List<TopologicalLinkModel> distinctLinkData(
			List<TopologicalLinkModel> data) throws CommonException {
		// key容器
		Map<String, String> keyMap = new HashMap<String, String>();
		// 结果集
		List<TopologicalLinkModel> result = new ArrayList<TopologicalLinkModel>();
		// 过滤相同link
		for (TopologicalLinkModel link : data) {
			String key = link.getaEndNESerialNo() + "@" + link.getaEndPtpName()
					+ "@" + link.getzEndNESerialNo() + "@"
					+ link.getzEndPtpName();
			if (!keyMap.containsKey(key)) {
				result.add(link);
				keyMap.put(key, key);
			} else {

			}
		}
		return result;
	}

	/**
	 * 获取历史性能缓存文件
	 * @param ftpUtils
	 * @param fileName
	 * @return
	 * @throws CommonException
	 */
	public static File getHistoryPMTempFile(FtpUtils ftpUtils, String fileName)
			throws CommonException {
		File file = null;
		// 缓存文件地址
		String destFileName = fileName;
		String tempPath = System.getProperty("java.io.tmpdir");
		// 获得文件输入流
		boolean downResult = ftpUtils.downloadFile("/" + fileName, tempPath,
				destFileName);
		if (downResult) {
			file = new File(tempPath + destFileName);
		}
		return file;
	}
	
	public List<SubnetworkConnectionModel> getAllSubnetworkConnections()
			throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<CrossConnectModel> getRoute(boolean needSort) throws CommonException{
		// TODO Auto-generated method stub
				return null;
	}
	
	@Override
	public List<FlowDomainModel> getAllFlowDomains() throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<FdfrModel> getAllFDFrs() throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TopologicalLinkModel> getAllLinkOfFDFrs() throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
