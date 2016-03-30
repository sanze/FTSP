package com.fujitsu.manager.resourceManager.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/*
 * 用来存放南昌电路名称excel表
 */
public class RecordModel {
	// 根据excel行对象初始化对象
	public RecordModel(Row row) {
		circuitName = getCell(row.getCell(1));
		multipleFrame = getCell(row.getCell(2));
		location = new Location(getCell(row.getCell(3)));
		note = getCell(row.getCell(4));
	}
	private int neId;
	// 对应字段:电路名称
	private String circuitName;
	// 复用框
	private String multipleFrame;
	// 长本10G环位置(网元名称-槽道号-端口号-(origianl_j))
	private Location location;
	// 备注
	private String note;
	// 时隙说明("2M"+(1~63)数字)
	private Ctp ctpDes;
	// 状态 1:占用,2:空闲
	private int state;
	// 左端口名称
	private String leftPortName;
	// 左端口状态:1:占用,2:空闲
	private int leftPortState;
	// 右端口名称
	private String rightPortName;
	// 右端口状态
	private int rightPortState;
	// 承载资源(注：添加到cir_info表的source_no字段)
	private String source;
	// 起始站点
	private String startStation;
	// 终止站点
	private String endStation;
	// 业务类型(用途)
	private String useFor;
	// 调通日期
	private String date;
	// 客户名称
	private String clientName;
	// 主备标志1:主用电路,2:备用电路
	private int tag;

	private int serviceType;

	// 根据第二份excel中的记录改变Record的Feild
	public void changeRecord(Row row,int type) {
		this.ctpDes = new Ctp(getCell(row.getCell(1)),type);
		this.setState(getCell(row.getCell(2)));
		this.leftPortName = getCell(row.getCell(3));
		this.setLeftPortState(getCell(row.getCell(4)));
		this.rightPortName = getCell(row.getCell(5));
		this.setRightPortState(getCell(row.getCell(6)));
		this.source = getCell(row.getCell(7));
		this.startStation = getCell(row.getCell(8));
		this.endStation = getCell(row.getCell(9));
		this.useFor = getCell(row.getCell(10));
		this.date = getCell(row.getCell(11));
		this.clientName = getCell(row.getCell(12));
		this.setTag(getCell(row.getCell(13)));
	}

	// 处理cell的工具方法
	private String getCell(Cell cell) {
		if (cell == null)
			return "";
		switch (cell.getCellType()) {
		case 1:
			return cell.getStringCellValue();
		case 0:
			return String.valueOf((int) cell.getNumericCellValue());
		default:
			return "";
		}
	}
	
	public Map getCtpQueryCondition(){
		Map con = new HashMap();
		con.put("BASE_NE_ID", neId);
		con.put("UNIT_NO", location.getSlotNo());
		con.put("PORT_NO", location.getPortNo());
		con.put("CTP_J_ORIGINAL", location.getOrigianlJ());
		con.put("CTP_K",ctpDes.getK());
		con.put("CTP_L", ctpDes.getL());
		con.put("CTP_M", ctpDes.getM());
		return con;
	}
	
	public Map getUpdateInfo(){
		Map con = new HashMap();
		con.put("SOURCE_NO",this.source);
		con.put("USED_FOR", this.useFor);
		con.put("CLIENT_NAME", this.clientName);
		return con;
	}

	public String getCircuitName() {
		return circuitName;
	}

	public void setCircuitName(String circuitName) {
		this.circuitName = circuitName;
	}

	public String getMultipleFrame() {
		return multipleFrame;
	}

	public void setMultipleFrame(String multipleFrame) {
		this.multipleFrame = multipleFrame;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Ctp getCtpDes() {
		return ctpDes;
	}

	public void setCtpDes(Ctp ctpDes) {
		this.ctpDes = ctpDes;
	}

	public int getState() {
		return state;
	}

	public void setState(String state) {
		if (state.equals("空闲")) {
			this.state = 2;
		}
		if (state.equals("占用")) {
			this.state = 1;
		}
	}

	public String getLeftPortName() {
		return leftPortName;
	}

	public void setLeftPortName(String leftPortName) {
		this.leftPortName = leftPortName;
	}

	public int getLeftPortState() {
		return leftPortState;
	}

	public void setLeftPortState(String leftPortState) {
		if (leftPortState.equals("空闲")) {
			this.leftPortState = 2;
		}
		if (leftPortState.equals("占用")) {
			this.leftPortState = 1;
		}
	}

	public String getRightPortName() {
		return rightPortName;
	}

	public void setRightPortName(String rightPortName) {
		this.rightPortName = rightPortName;
	}

	public int getRightPortState() {
		return rightPortState;
	}

	public void setRightPortState(String rightPortState) {
		if (rightPortState.equals("空闲")) {
			this.rightPortState = 2;
		}
		if (rightPortState.equals("占用")) {
			this.rightPortState = 1;
		}
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getStartStation() {
		return startStation;
	}

	public void setStartStation(String startStation) {
		this.startStation = startStation;
	}

	public String getEndStation() {
		return endStation;
	}

	public void setEndStation(String endStation) {
		this.endStation = endStation;
	}

	public String getUseFor() {
		return useFor;
	}

	public void setUseFor(String useFor) {
		this.useFor = useFor;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(String tag) {
		if (tag.equals("主用电路")) {
			this.tag = 1;
		}
		if (tag.equals("备用电路")) {
			this.tag = 2;
		}
	}

	public int getServiceType() {
		return serviceType;
	}

	public void setServiceType(int serviceType) {
		this.serviceType = serviceType;
	}

	public int getNeId() {
		return neId;
	}

	public void setNeId(int neId) {
		this.neId = neId;
	}
}
