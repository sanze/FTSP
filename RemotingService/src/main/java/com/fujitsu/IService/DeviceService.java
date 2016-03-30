package com.fujitsu.IService;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.fujitsu.model.DeviceInfoBean;

public interface DeviceService extends Remote{
	public DeviceInfoBean getDeviceInfo() throws RemoteException;
	
	public DeviceInfoBean getUsageInfo() throws RemoteException;
}
