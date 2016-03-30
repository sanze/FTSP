package com.fujitsu.common;

public enum CircuitDefine {
	SDH("t_cir_circuit", "CIR_CIRCUIT_ID", "CIR_CIRCUIT_INFO_ID",
			"base_sdh_CTP_ID", "t_base_sdh_ctp", "t_cir_circuit_info",
			"t_base_sdh_crs", "BASE_SDH_CRS_ID","t_cir_circuit_route"), 
	OTN("t_cir_otn_circuit","CIR_OTN_CIRCUIT_ID", "CIR_OTN_CIRCUIT_INFO_ID", 
			"BASE_OTN_CTP_ID","T_base_otn_ctp", "t_cir_otn_circuit_info", 
			"t_base_otn_crs","BASE_OTN_CRS_ID","t_cir_otn_circuit_route");

	{
		actp = "ctp1.DISPLAY_NAME";
		zctp = "ctp2.DISPLAY_NAME";
	}

	private CircuitDefine(String cirTable, String cirId, String cirInfoId,
			String ctpId, String ctpTable, String cirInfoTable,
			String crsTable, String crsId,String routeTable) {
		this.cirTable = cirTable;
		this.cirId = cirId;
		this.cirInfoId = cirInfoId;
		this.ctpId = ctpId;
		this.ctpTable = ctpTable;
		this.cirInfoTable = cirInfoTable;
		this.crsTable = crsTable;
		this.crsId = crsId;
		this.routeTable=routeTable;
	}

	public final String cirTable;
	public final String cirId;
	public final String cirInfoTable;
	public final String cirInfoId;
	public final String ctpTable;
	public final String ctpId;
	public final String actp;
	public final String zctp;
	public final String crsTable;
	public final String crsId;
	public final String routeTable;

}
