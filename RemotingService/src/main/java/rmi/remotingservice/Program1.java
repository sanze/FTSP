package rmi.remotingservice;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.ResourceBundle;

import rmi.serviceImpl.DeviceServiceImpl;

import com.fujitsu.IService.DeviceService;

public class Program1{

	public static void main(String[] args) {
        try {
        	String ip = "127.0.0.1";

        	ResourceBundle bundle = ResourceBundle.getBundle("systemConfig");
        	
    		try {
    			ip = bundle.getString("rmi.ip");
    			System.setProperty("java.rmi.server.hostname",ip);
    		} catch (Exception e) {

    		}
    		
			DeviceService deviceService=new DeviceServiceImpl();
			LocateRegistry.createRegistry(6600);
			System.out.println("RMI服务绑定ip地址为："+ip);
			Naming.rebind("rmi://"+ip+":6600/DeviceService", deviceService);
			System.out.println("Server monitor service started!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}