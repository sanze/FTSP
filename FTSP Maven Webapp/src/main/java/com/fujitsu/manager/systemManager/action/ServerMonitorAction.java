package com.fujitsu.manager.systemManager.action;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.IService.DeviceService;
import com.fujitsu.IService.IMethodLog;
import com.fujitsu.IService.ISystemManagerService;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.model.DeviceInfoBean;

public class ServerMonitorAction extends AbstractAction {
	
	@Resource
	public ISystemManagerService systemManagerService;
	private String ipAddress;
	
	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	//	public String testMon(){
//		//getNetInfo();
//		try{
//    		//调用远程对象，注意RMI路径与接口必须与服务器配置一致
//    		DeviceService deviceService=(DeviceService)Naming.lookup("rmi://10.167.14.72:6600/DeviceService");
//    		DeviceInfoBean bean=deviceService.getDeviceInfo();
//    		DeviceInfoBean bean1=deviceService.getUsageInfo();
//    		JSONObject rlt = new JSONObject();
//    		
//    		rlt.put("bean",bean);
//    		rlt.put("bean1",bean1);
//    		System.out.println(rlt.toString());
// //    		List<PersonEntity> personList=personService.GetList();
////    		for(PersonEntity person:personList){
////    			System.out.println("ID:"+person.getId()+" Age:"+person.getAge()+" Name:"+person.getName());
////    		}
//    	}catch(Exception ex){
//    		ex.printStackTrace();
//    	}
//		try{
//			//getEthernetInfo();
//		}catch(Exception e){
//		}
////		JSONObject rlt = new JSONObject();
////		Map map = sysMonService.testMap();
////		rlt.put("total",1);
////		rlt.put("rows",map);
//		//resultObj = rlt;
//		return RESULT_OBJ;
//	}
	public String getUsage(){
		JSONObject rlt = new JSONObject();
		DeviceService deviceService;
		try {
			String address="rmi://"+ipAddress+":6600/DeviceService";
			deviceService = (DeviceService)Naming.lookup(address);
			DeviceInfoBean bean=deviceService.getUsageInfo();
			rlt.putAll(bean.getUsage());
			resultObj = rlt;
			result.setReturnResult(CommonDefine.SUCCESS);
		} catch (Exception e) {
			result.setReturnResult(CommonDefine.FAILED);
			resultObj = JSONObject.fromObject(result);
			e.printStackTrace();
		}
		
		return RESULT_OBJ;
	}

	public String getSysInfo(){
		JSONObject rlt = new JSONObject();
		DeviceService deviceService;
		try {
			String address="rmi://"+ipAddress+":6600/DeviceService";
			deviceService = (DeviceService)Naming.lookup(address);
			DeviceInfoBean bean=deviceService.getDeviceInfo();
			rlt.put("cpuInfo", bean.getCpuInfo());
			rlt.put("memInfo", bean.getMemInfo());
			rlt.put("driveInfo", bean.getDriveInfo());
			rlt.put("sysInfo",bean.getSysInfo());
			rlt.put("netInfo", bean.getNetInfo());
			resultObj = rlt;
		} catch (Exception e) {
			result.setReturnResult(CommonDefine.FAILED);
			resultObj = JSONObject.fromObject(result);
			e.printStackTrace();
		}
		return RESULT_OBJ;
	}
	
	/**
	 * Method name: getAllServers <BR>
	 * Description: 查询所有服务器<BR>
	 * Remark: 2014-02-23<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 * @throws NotBoundException 
	 * @throws RemoteException 
	 * @throws MalformedURLException 
	 */
	@IMethodLog(desc = "查询所有服务器")
	public String getAllServers() throws MalformedURLException, RemoteException, NotBoundException{
		DeviceService deviceService;
		
		try {
			// 查询所有服务器
			Map<String, Object> serverMap = systemManagerService.getAllServers();
			List<Map<String, Object>> serverList=(List<Map<String, Object>>)serverMap.get("rows");
			for(Map<String,Object> m:serverList){
				String address="rmi://"+m.get("SERVER_IP")+":6600/DeviceService";
				try{
				deviceService = (DeviceService)Naming.lookup(address);
				DeviceInfoBean bean=deviceService.getDeviceInfo();
				m.put("otherIps",bean.getIps());
				}catch(Exception e){
					
				}
				
			}
			// 将返回的结果转成JSON对象，返回前台
			resultObj = JSONObject.fromObject(serverMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
}
