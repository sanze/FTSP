package com.fujitsu.manager.dataCollectManager.corbaDataModel;

import java.net.InetAddress;

import globaldefs.NameAndStringValue_T;

public class CrossConnectModel{

	private boolean active;
	private int direction;
	private int ccType;
	private NameAndStringValue_T[][] aEndNameList;
	private NameAndStringValue_T[][] zEndNameList;
	private NameAndStringValue_T[] additionalInfo;

	// extend
	private String clientType;
	private String clientRate;

	private String LayerRate;
	private String LSPType;
	private String PWType;
	private String SrcInLabel;
	private String SrcOutLabel;
	private String DestInLabel;
	private String DestOutLabel;
	private String SrcNextHopIP;
	private String DestNextHopIP;
	private String SrcIP;
	private String DestIP;
	private String BelongedTrail;

	private String UserLabel;
	private String NativeEMSName;

	private int sequence;
	
	private Integer SrcNextHopIPNum;
	private Integer DestNextHopIPNum;
	
	//0 正常 1 头或者尾  2 pw
	private int routeType = 0;
	
	private boolean isFixed = false;

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getCcType() {
		return ccType;
	}

	public void setCcType(int ccType) {
		this.ccType = ccType;
	}

	public NameAndStringValue_T[][] getaEndNameList() {
		return aEndNameList;
	}

	public void setaEndNameList(NameAndStringValue_T[][] aEndNameList) {
		this.aEndNameList = aEndNameList;
	}

	public NameAndStringValue_T[][] getzEndNameList() {
		return zEndNameList;
	}

	public void setzEndNameList(NameAndStringValue_T[][] zEndNameList) {
		this.zEndNameList = zEndNameList;
	}

	public NameAndStringValue_T[] getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(NameAndStringValue_T[] additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public String getClientType() {
		return clientType;
	}

	public void setClientType(String clientType) {
		this.clientType = clientType;
	}

	public String getClientRate() {
		return clientRate;
	}

	public void setClientRate(String clientRate) {
		this.clientRate = clientRate;
	}

	public boolean isFixed() {
		return isFixed;
	}

	public void setFixed(boolean isFixed) {
		this.isFixed = isFixed;
	}

	public String getLSPType() {
		return LSPType;
	}

	public void setLSPType(String lSPType) {
		LSPType = lSPType;
	}

	public String getPWType() {
		return PWType;
	}

	public void setPWType(String pWType) {
		PWType = pWType;
	}

	public String getSrcInLabel() {
		return SrcInLabel;
	}

	public void setSrcInLabel(String srcInLabel) {
		SrcInLabel = srcInLabel;
	}

	public String getSrcOutLabel() {
		return SrcOutLabel;
	}

	public void setSrcOutLabel(String srcOutLabel) {
		SrcOutLabel = srcOutLabel;
	}

	public String getDestInLabel() {
		return DestInLabel;
	}

	public void setDestInLabel(String destInLabel) {
		DestInLabel = destInLabel;
	}

	public String getDestOutLabel() {
		return DestOutLabel;
	}

	public void setDestOutLabel(String destOutLabel) {
		DestOutLabel = destOutLabel;
	}

	public String getSrcIP() {
		return SrcIP;
	}

	public void setSrcIP(String srcIP) {
		SrcIP = srcIP;
	}

	public String getDestIP() {
		return DestIP;
	}

	public void setDestIP(String destIP) {
		DestIP = destIP;
	}

	public String getBelongedTrail() {
		return BelongedTrail;
	}

	public void setBelongedTrail(String belongedTrail) {
		BelongedTrail = belongedTrail;
	}

	public String getLayerRate() {
		return LayerRate;
	}

	public void setLayerRate(String layerRate) {
		LayerRate = layerRate;
	}

	public String getUserLabel() {
		return UserLabel;
	}

	public void setUserLabel(String userLabel) {
		UserLabel = userLabel;
	}

	public String getNativeEMSName() {
		return NativeEMSName;
	}

	public void setNativeEMSName(String nativeEMSName) {
		NativeEMSName = nativeEMSName;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getSrcNextHopIP() {
		return SrcNextHopIP;
	}

	public void setSrcNextHopIP(String srcNextHopIP) {
		SrcNextHopIP = srcNextHopIP;
		
		if(srcNextHopIP!=null && !srcNextHopIP.isEmpty()){
			SrcNextHopIPNum = ipToInt(srcNextHopIP);
		}else{
			SrcNextHopIPNum = null;
		}
	}

	public String getDestNextHopIP() {
		return DestNextHopIP;
	}

	public void setDestNextHopIP(String destNextHopIP) {
		DestNextHopIP = destNextHopIP;
		
		if(destNextHopIP!=null && !destNextHopIP.isEmpty()){
			DestNextHopIPNum = ipToInt(destNextHopIP);
		}else{
			DestNextHopIPNum = null;
		}
	}

	public Integer getSrcNextHopIPNum() {
		return SrcNextHopIPNum;
	}

	public void setSrcNextHopIPNum(Integer srcNextHopIPNum) {
		SrcNextHopIPNum = srcNextHopIPNum;
	}

	public Integer getDestNextHopIPNum() {
		return DestNextHopIPNum;
	}

	public void setDestNextHopIPNum(Integer destNextHopIPNum) {
		DestNextHopIPNum = destNextHopIPNum;
	}

	public int getRouteType() {
		return routeType;
	}

	public void setRouteType(int routeType) {
		this.routeType = routeType;
	}
	
	/**
	 * 把IP地址转化为int
	 * @param ipAddr
	 * @return int
	 */
	private static int ipToInt(String ipAddr) {
	    try {
	        return bytesToInt(ipToBytesByInet(ipAddr));
	    } catch (Exception e) {
	        throw new IllegalArgumentException(ipAddr + " is invalid IP");
	    }
	}

	/**
	 * 根据位运算把 byte[] -> int
	 * @param bytes
	 * @return int
	 */
	private static int bytesToInt(byte[] bytes) {
	    int addr = bytes[3] & 0xFF;
	    addr |= ((bytes[2] << 8) & 0xFF00);
	    addr |= ((bytes[1] << 16) & 0xFF0000);
	    addr |= ((bytes[0] << 24) & 0xFF000000);
	    return addr;
	}

	/**
	 * 把IP地址转化为字节数组
	 * @param ipAddr
	 * @return byte[]
	 */
	private static byte[] ipToBytesByInet(String ipAddr) {
	    try {
	        return InetAddress.getByName(ipAddr).getAddress();
	    } catch (Exception e) {
	        throw new IllegalArgumentException(ipAddr + " is invalid IP");
	    }
	}
}
