package com.fujitsu.manager.dataCollectManager.corbaDataModel;

import globaldefs.NameAndStringValue_T;

import java.util.List;

import com.fujitsu.common.DataCollectDefine;

/**
 * @author xuxiaojun
 *
 */
public class TerminationPointModel {

	private NameAndStringValue_T[] name;
	private String userLabel;
	private String nativeEMSName;
	private String owner;
	private NameAndStringValue_T[] ingressTrafficDescriptorName;
	private NameAndStringValue_T[] egressTrafficDescriptorName;
	private int type;
	private int connectionState;
	private int tpMappingMode;
	private int direction;
	private List<LayeredParametersModel> transmissionParams;
	private int tpProtectionAssociation;
	private boolean edgePoint;
	private NameAndStringValue_T[] additionalInfo;
	
	// *********************** ZTE ******************************
	private globaldefs.NameAndStringValue_T[][] includeTPList;
	
	//extend
	private int relPtpType = 1;
	
	//extend
	private String nameString;
	private int ptpOrFtp;
	private int domain;
	private String ptpType;
	private String rate;
	private String rackNo;
	private String shelfNo;
	private String slotNo;
	private String subSlotNo;
	private String portNo;
	private String layerRateString;
	private int isSyncCtp = DataCollectDefine.FALSE;
	private int portServiceType;
	//for CTP
	private int emsConnectionId;
	private int neId;
	private int ptpId;
	private String displayName;
	private String ctpValue;
	
	private String PTNTP_TUNNELId;
	private String PTNTP_PWId;
	private String PTNTP_PWMode;
	private String PTNTP_PWType;
	private String PTNTP_InLabel;
	private String PTNTP_OutLabel;
	private String PTNTP_PSNType;
	private String PTNTP_VCId;
	private String srcInLabel;
	private String srcOutLabel;
//	private Integer ctp64C;
//	private Integer ctp16C;
//	private Integer ctp8C;
//	private Integer ctp4C;
//	private Integer ctpJOriginal;
//	private Integer ctpJ;
//	private Integer ctpK;
//	private Integer ctpL;
//	private Integer ctpM;
//	private String connectRate;

	public NameAndStringValue_T[] getName() {
		return name;
	}

	public void setName(NameAndStringValue_T[] name) {
		this.name = name;
	}

	public String getUserLabel() {
		return userLabel;
	}

	public void setUserLabel(String userLabel) {
		this.userLabel = userLabel;
	}

	public String getNativeEMSName() {
		return nativeEMSName;
	}

	public void setNativeEMSName(String nativeEMSName) {
		this.nativeEMSName = nativeEMSName;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public NameAndStringValue_T[] getIngressTrafficDescriptorName() {
		return ingressTrafficDescriptorName;
	}

	public void setIngressTrafficDescriptorName(
			NameAndStringValue_T[] ingressTrafficDescriptorName) {
		this.ingressTrafficDescriptorName = ingressTrafficDescriptorName;
	}

	public NameAndStringValue_T[] getEgressTrafficDescriptorName() {
		return egressTrafficDescriptorName;
	}

	public void setEgressTrafficDescriptorName(
			NameAndStringValue_T[] egressTrafficDescriptorName) {
		this.egressTrafficDescriptorName = egressTrafficDescriptorName;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getConnectionState() {
		return connectionState;
	}

	public void setConnectionState(int connectionState) {
		this.connectionState = connectionState;
	}

	public int getTpMappingMode() {
		return tpMappingMode;
	}

	public void setTpMappingMode(int tpMappingMode) {
		this.tpMappingMode = tpMappingMode;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public List<LayeredParametersModel> getTransmissionParams() {
		return transmissionParams;
	}

	public void setTransmissionParams(List<LayeredParametersModel> transmissionParams) {
		this.transmissionParams = transmissionParams;
	}

	public int getTpProtectionAssociation() {
		return tpProtectionAssociation;
	}

	public void setTpProtectionAssociation(int tpProtectionAssociation) {
		this.tpProtectionAssociation = tpProtectionAssociation;
	}

	public boolean isEdgePoint() {
		return edgePoint;
	}

	public void setEdgePoint(boolean edgePoint) {
		this.edgePoint = edgePoint;
	}

	public NameAndStringValue_T[] getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(NameAndStringValue_T[] additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public globaldefs.NameAndStringValue_T[][] getIncludeTPList() {
		return includeTPList;
	}

	public void setIncludeTPList(globaldefs.NameAndStringValue_T[][] includeTPList) {
		this.includeTPList = includeTPList;
	}

	public String getNameString() {
		return nameString;
	}

	public void setNameString(String nameString) {
		this.nameString = nameString;
	}

	public String getRackNo() {
		return rackNo;
	}

	public void setRackNo(String rackNo) {
		this.rackNo = rackNo;
	}

	public String getShelfNo() {
		return shelfNo;
	}

	public void setShelfNo(String shelfNo) {
		this.shelfNo = shelfNo;
	}

	public String getSlotNo() {
		return slotNo;
	}

	public void setSlotNo(String slotNo) {
		this.slotNo = slotNo;
	}

	public String getPortNo() {
		return portNo;
	}

	public void setPortNo(String portNo) {
		this.portNo = portNo;
	}

	public String getSubSlotNo() {
		return subSlotNo;
	}

	public void setSubSlotNo(String subSlotNo) {
		this.subSlotNo = subSlotNo;
	}

	public int getDomain() {
		return domain;
	}

	public void setDomain(int domain) {
		this.domain = domain;
	}

	public int getPtpOrFtp() {
		return ptpOrFtp;
	}

	public void setPtpOrFtp(int ptpOrFtp) {
		this.ptpOrFtp = ptpOrFtp;
	}

	public String getLayerRateString() {
		return layerRateString;
	}

	public void setLayerRateString(String layerRateString) {
		this.layerRateString = layerRateString;
	}

	public String getPtpType() {
		return ptpType;
	}

	public void setPtpType(String ptpType) {
		this.ptpType = ptpType;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getCtpValue() {
		return ctpValue;
	}

	public void setCtpValue(String ctpValue) {
		this.ctpValue = ctpValue;
	}

//	public Integer getCtp64C() {
//		return ctp64C;
//	}
//
//	public void setCtp64C(Integer ctp64c) {
//		ctp64C = ctp64c;
//	}
//
//	public Integer getCtp16C() {
//		return ctp16C;
//	}
//
//	public void setCtp16C(Integer ctp16c) {
//		ctp16C = ctp16c;
//	}
//
//	public Integer getCtp8C() {
//		return ctp8C;
//	}
//
//	public void setCtp8C(Integer ctp8c) {
//		ctp8C = ctp8c;
//	}
//
//	public Integer getCtp4C() {
//		return ctp4C;
//	}
//
//	public void setCtp4C(Integer ctp4c) {
//		ctp4C = ctp4c;
//	}
//
//	public Integer getCtpJOriginal() {
//		return ctpJOriginal;
//	}
//
//	public void setCtpJOriginal(Integer ctpJOriginal) {
//		this.ctpJOriginal = ctpJOriginal;
//	}
//
//	public Integer getCtpJ() {
//		return ctpJ;
//	}
//
//	public void setCtpJ(Integer ctpJ) {
//		this.ctpJ = ctpJ;
//	}
//
//	public Integer getCtpK() {
//		return ctpK;
//	}
//
//	public void setCtpK(Integer ctpK) {
//		this.ctpK = ctpK;
//	}
//
//	public Integer getCtpL() {
//		return ctpL;
//	}
//
//	public void setCtpL(Integer ctpL) {
//		this.ctpL = ctpL;
//	}
//
//	public Integer getCtpM() {
//		return ctpM;
//	}
//
//	public void setCtpM(Integer ctpM) {
//		this.ctpM = ctpM;
//	}
//
//	public String getConnectRate() {
//		return connectRate;
//	}
//
//	public void setConnectRate(String connectRate) {
//		this.connectRate = connectRate;
//	}

	public int getEmsConnectionId() {
		return emsConnectionId;
	}

	public void setEmsConnectionId(int emsConnectionId) {
		this.emsConnectionId = emsConnectionId;
	}

	public int getNeId() {
		return neId;
	}

	public void setNeId(int neId) {
		this.neId = neId;
	}

	public int getPtpId() {
		return ptpId;
	}

	public void setPtpId(int ptpId) {
		this.ptpId = ptpId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public int getIsSyncCtp() {
		return isSyncCtp;
	}

	public void setIsSyncCtp(int isSyncCtp) {
		this.isSyncCtp = isSyncCtp;
	}

	/**
	 * @return the portServiceType
	 */
	public int getPortServiceType() {
		return portServiceType;
	}

	/**
	 * @param portServiceType the portServiceType to set
	 */
	public void setPortServiceType(int portServiceType) {
		this.portServiceType = portServiceType;
	}

	public int getRelPtpType() {
		return relPtpType;
	}

	public void setRelPtpType(int relPtpType) {
		this.relPtpType = relPtpType;
	}

	public String getPTNTP_PWId() {
		return PTNTP_PWId;
	}

	public void setPTNTP_PWId(String pTNTP_PWId) {
		PTNTP_PWId = pTNTP_PWId;
	}

	public String getPTNTP_PWMode() {
		return PTNTP_PWMode;
	}

	public void setPTNTP_PWMode(String pTNTP_PWMode) {
		PTNTP_PWMode = pTNTP_PWMode;
	}

	public String getPTNTP_PWType() {
		return PTNTP_PWType;
	}

	public void setPTNTP_PWType(String pTNTP_PWType) {
		PTNTP_PWType = pTNTP_PWType;
	}

	public String getPTNTP_InLabel() {
		return PTNTP_InLabel;
	}

	public void setPTNTP_InLabel(String pTNTP_InLabel) {
		PTNTP_InLabel = pTNTP_InLabel;
	}

	public String getPTNTP_OutLabel() {
		return PTNTP_OutLabel;
	}

	public void setPTNTP_OutLabel(String pTNTP_OutLabel) {
		PTNTP_OutLabel = pTNTP_OutLabel;
	}

	public String getPTNTP_PSNType() {
		return PTNTP_PSNType;
	}

	public void setPTNTP_PSNType(String pTNTP_PSNType) {
		PTNTP_PSNType = pTNTP_PSNType;
	}

	public String getPTNTP_VCId() {
		return PTNTP_VCId;
	}

	public void setPTNTP_VCId(String pTNTP_VCId) {
		PTNTP_VCId = pTNTP_VCId;
	}

	public String getSrcInLabel() {
		return srcInLabel;
	}

	public void setSrcInLabel(String srcInLabel) {
		this.srcInLabel = srcInLabel;
	}

	public String getSrcOutLabel() {
		return srcOutLabel;
	}

	public void setSrcOutLabel(String srcOutLabel) {
		this.srcOutLabel = srcOutLabel;
	}

	public String getPTNTP_TUNNELId() {
		return PTNTP_TUNNELId;
	}

	public void setPTNTP_TUNNELId(String pTNTP_TUNNELId) {
		PTNTP_TUNNELId = pTNTP_TUNNELId;
	}
}
