package com.fujitsu.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.omg.CosNaming.Binding;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.BindingType;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import com.fujitsu.IService.IEMSSession;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.DataCollectDefine;
import com.fujitsu.common.MessageCodeDefine;

public abstract class EMSCollectService {

	public static HashMap<String, Object> orbMap = new HashMap<String, Object>();
	
	public static HashMap<String, IEMSSession> sessionMap = new HashMap<String, IEMSSession>();

	public static HashMap<String, Object> pushSupplierMap = new HashMap<String, Object>();
	
	public static Map<String,Date> heartBeatReceiveTime = new HashMap<String,Date>();
	
	public static Map<String,Date> pingReceiveTime = new HashMap<String,Date>();

	public static Map<String,Timer> heartBeatTimerMap = new HashMap<String,Timer>();
	
	public static Map<String,Timer> pingTimerMap = new HashMap<String,Timer>();
	
		
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
}
