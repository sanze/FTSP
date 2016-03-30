package rmi.serviceImpl;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.OperatingSystem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarNotImplementedException;
import org.hyperic.sigar.Swap;

import com.fujitsu.IService.DeviceService;
import com.fujitsu.model.DeviceInfoBean;
public class DeviceServiceImpl extends UnicastRemoteObject implements
		DeviceService {
	Sigar sigar = new Sigar();

	public DeviceServiceImpl() throws RemoteException {
	}

	public DeviceInfoBean getDeviceInfo() throws RemoteException {
		DeviceInfoBean bean = new DeviceInfoBean();
		bean.setCpuInfo(getCpuInfo());
		bean.setDriveInfo(getDriveInfo());
		bean.setMemInfo(getMemInfo());
		bean.setNetInfo(getNetInfo());
		bean.setSysInfo(getSysInfo());
		bean.setIps(getNetIPsInfo());
		return bean;
	}

	public List<Map<String, Object>> getCpuInfo() {
		List rlt = new ArrayList();
		try {
			CpuInfo[] infos = this.sigar.getCpuInfoList();
			for (int i = 0; i < infos.length; i++) {
				Map tmp = new HashMap();
				tmp.put("id", Integer.valueOf(i));
				tmp.put("mhz", Integer.valueOf(infos[i].getMhz()));
				tmp.put("vendor", infos[i].getVendor());
				tmp.put("model", infos[i].getModel());
				rlt.add(tmp);
			}

			CpuPerc[] cpuList = null;
			try {
				cpuList = this.sigar.getCpuPercList();
			} catch (SigarException e) {
				e.printStackTrace();
				return null;
			}
			for (int i = 0; i < cpuList.length; i++) {
				Map tmp = (Map) rlt.get(i);
				tmp.put("User", CpuPerc.format(cpuList[i].getUser()));
				tmp.put("Sys", CpuPerc.format(cpuList[i].getSys()));
				tmp.put("Wait", CpuPerc.format(cpuList[i].getWait()));
				tmp.put("Nice", CpuPerc.format(cpuList[i].getNice()));
				tmp.put("Idle", CpuPerc.format(cpuList[i].getIdle()));
				tmp.put("Total", CpuPerc.format(cpuList[i].getCombined()));
			}
		} catch (SigarException e) {
			e.printStackTrace();
		}
		return rlt;
	}

	public HashMap<String, Double> getUsage() {
		HashMap rlt = new HashMap();
		try {
			CpuPerc cpu = this.sigar.getCpuPerc();
			rlt.put("cpu", Double.valueOf(cpu.getCombined() * 100.0D));
			Mem mem = this.sigar.getMem();
			rlt.put("mem",
					Double.valueOf(mem.getUsed() * 100.0D / mem.getTotal()));
		} catch (SigarException e) {
			e.printStackTrace();
		}
		return rlt;
	}

	public List<Map<String, Object>> getDriveInfo() {
		List rlt = new ArrayList();

		String[] types = { "TYPE_UNKNOWN", "TYPE_NONE", "本地磁盘", "网络", "闪存",
				"光驱", "页面交换" };
		try {
			FileSystem[] fslist = this.sigar.getFileSystemList();
			for (int i = 0; i < fslist.length; i++) {
				Map tmp = new HashMap();
				FileSystem fs = fslist[i];
				tmp.put("devName", fs.getDevName());
				tmp.put("dirName", fs.getDirName());
				tmp.put("fileSystem", fs.getSysTypeName());
				tmp.put("driveType", types[(fs.getType() % 7)]);
				FileSystemUsage usage = null;
				try {
					usage = this.sigar.getFileSystemUsage(fs.getDirName());
				} catch (SigarException e) {
					if (fs.getType() == 2)
						throw e;
					rlt.add(tmp);
					continue;
				}
				switch (fs.getType()) {
				case 0:
					break;
				case 1:
					break;
				case 2:
				case 5:
					tmp.put("total", Long.valueOf(usage.getTotal()));

					tmp.put("free", Long.valueOf(usage.getFree()));

					tmp.put("used", Long.valueOf(usage.getUsed()));

					double usePercent = usage.getUsePercent();
					tmp.put("percent", Double.valueOf(usePercent));
					break;
				case 3:
					break;
				case 4:
					break;
				case 6:
				}

				rlt.add(tmp);
			}
		} catch (SigarException localSigarException1) {
		}
		return rlt;
	}

	public List<Map<String, Object>> getMemInfo() {
		List rlt = new ArrayList();
		try {
			Map jMem = new HashMap();
			Map jPage = new HashMap();
			Mem mem = this.sigar.getMem();
			jMem.put("id", "内存");

			jMem.put("total", Long.valueOf(mem.getTotal()));

			jMem.put("used", Long.valueOf(mem.getUsed()));

			jMem.put("free", Long.valueOf(mem.getFree()));
			jMem.put("percent",
					Double.valueOf(mem.getUsed() * 1.0D / mem.getTotal()));

			Swap swap = this.sigar.getSwap();
			jPage.put("id", "页面文件");

			jPage.put("total", Long.valueOf(swap.getTotal()));

			jPage.put("used", Long.valueOf(swap.getUsed()));

			jPage.put("free", Long.valueOf(swap.getFree()));
			jPage.put("percent",
					Double.valueOf(swap.getUsed() * 1.0D / swap.getTotal()));

			rlt.add(jMem);
			rlt.add(jPage);
		} catch (SigarException e) {
			e.printStackTrace();
		}
		return rlt;
	}

	public List<String> getNetIPsInfo() {
		List ips = new ArrayList();
		try {
			String[] ifNames = this.sigar.getNetInterfaceList(); 
			sigar.getNetInfo();
			try {
				Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
	            NetworkInterface networkInterface;
	            Enumeration<InetAddress> inetAddresses;
	            InetAddress inetAddress;
	            String ip;
	            while (networkInterfaces.hasMoreElements()) {
	                networkInterface = networkInterfaces.nextElement();
	                inetAddresses = networkInterface.getInetAddresses();
	                while (inetAddresses.hasMoreElements()) {
	                    inetAddress = inetAddresses.nextElement();
	                    if (inetAddress != null && inetAddress instanceof InetAddress) { // IPV4
	                        ip = inetAddress.getHostAddress();
	                        if(!ip.contains(":") && !"127.0.0.1".equals(ip)){
	                        	ips.add(ip);
	                        }
	                    }
	                }
	            }
			} catch (SocketException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
//			for (int i = 0; i < ifNames.length; i++) {
//				String name = ifNames[i];
//				NetInterfaceConfig ifconfig = this.sigar.getNetInterfaceConfig(name);
//				if ((ifconfig.getAddress().length() >= 8) && (!ifconfig.getAddress().equals("127.0.0.1"))) {
//					ips.add(ifconfig.getAddress());
//				}
//			}
		} catch (SigarNotImplementedException localSigarNotImplementedException) {
		} catch (SigarException e) {
			System.out.println(e.getMessage());
		}
		return ips;
	}

	public Map<String, Object> getNetInfo() {
		Map returnMap = new HashMap();
		List netInfos = new ArrayList();
		try {
			String[] ifNames = this.sigar.getNetInterfaceList();
			for (int i = 0; i < ifNames.length; i++) {
				Map rlt = new HashMap();
				String name = ifNames[i];
				NetInterfaceConfig ifconfig = this.sigar
						.getNetInterfaceConfig(name);
				if ((ifconfig.getAddress() != null)
						&& (ifconfig.getAddress().length() >= 8)
						&& (!ifconfig.getAddress().equals("127.0.0.1"))
						&& (!"".equals(ifconfig.getAddress()))) {
					rlt.put("ip", ifconfig.getAddress());
					rlt.put("mask", ifconfig.getNetmask());
					if ((ifconfig.getFlags() & 1L) > 0L) {
						NetInterfaceStat ifstat = this.sigar
								.getNetInterfaceStat(name);
						rlt.put("rxPacket", Long.valueOf(ifstat.getRxPackets()));
						rlt.put("txPacket", Long.valueOf(ifstat.getTxPackets()));
						rlt.put("rxByte", Long.valueOf(ifstat.getRxBytes()));
						rlt.put("txByte", Long.valueOf(ifstat.getTxBytes()));
						rlt.put("rxErr", Long.valueOf(ifstat.getRxErrors()));
						rlt.put("txErr", Long.valueOf(ifstat.getTxErrors()));
						rlt.put("rxDrop", Long.valueOf(ifstat.getRxDropped()));
						rlt.put("txDrop", Long.valueOf(ifstat.getTxDropped()));
						netInfos.add(rlt);
					}
				}
			}
		} catch (SigarNotImplementedException localSigarNotImplementedException) {
		} catch (SigarException e) {
			System.out.println(e.getMessage());
		}

		returnMap.put("netInfos", netInfos);
		return returnMap;
	}

	public Map<String, Object> getSysInfo() {
		Map rlt = new HashMap();
		OperatingSystem OS = OperatingSystem.getInstance();

		rlt.put("arch", OS.getArch());
		rlt.put("dataBit", OS.getDataModel());
		try {
			Mem mem = this.sigar.getMem();
			rlt.put("totalMem", Long.valueOf(mem.getTotal()));
			FileSystem[] fs = this.sigar.getFileSystemList();
			long total = 0L;
			FileSystemUsage usage = null;
			for (int i = 0; i < fs.length; i++) {
				usage = this.sigar.getFileSystemUsage(fs[i].getDirName());
				total += usage.getTotal();
			}
			rlt.put("totalSpace", Long.valueOf(total));
		} catch (SigarException localSigarException) {
		}

		rlt.put("os", System.getProperty("os.name"));
		rlt.put("patch", OS.getPatchLevel());

		rlt.put("vendor", OS.getVendor());

		rlt.put("vendorCodeName", OS.getVendorCodeName());

		rlt.put("version", OS.getVersion());
		return rlt;
	}

	public DeviceInfoBean getUsageInfo() throws RemoteException {
		DeviceInfoBean bean = new DeviceInfoBean();
		bean.setUsage(getUsage());
		return bean;
	}
}
