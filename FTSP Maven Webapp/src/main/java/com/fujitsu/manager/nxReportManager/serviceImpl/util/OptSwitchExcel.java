/**
 * 
 */
package com.fujitsu.manager.nxReportManager.serviceImpl.util;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.fujitsu.common.CommonDefine;
import com.fujitsu.util.TimeUtil;

/**
 * @author TianHongjun
 *
 */
public class OptSwitchExcel extends BaseExcelUtil {
	private CellStyle exceptionStyle;
	private List<Map> PM_DATA = null;
	public OptSwitchExcel(String fileName) {
		super(fileName);
		exceptionStyle = createStyle(XSSFCellStyle.ALIGN_CENTER,
				XSSFCellStyle.VERTICAL_CENTER, true, true);
		setFont(exceptionStyle, (short)0, false, false, Font.COLOR_RED);
	}
	public void writeData(Map switchData) {
		Map<String, String> searchCond;
		searchCond = (Map<String, String>) switchData.get("searchCond");
		searchCond.put("genType", switchData.get("genType") + "");
		this.dates = getColumnNames(searchCond);
		for(Integer date:dates){
			writeSheet(switchData, date);
		}
		close();
	}

	private void writeSheet(Map switchData, int date){
		String sheetName = "光开关盘" + (date/10000) + "年" + (date%10000/100) + "月" + (date%100) + "日";
		sheetName = WorkbookUtil.createSafeSheetName(sheetName);
		sheet = book.createSheet(sheetName);
		Map cond = (Map) switchData.get("searchCond");
		String header =  cond.get("taskName").toString();
		int genType = (Integer) switchData.get("genType");
		int period = Integer.parseInt(cond.get("period").toString());
		try {
			Date start = TimeUtil.parseString2Date(cond.get("start").toString(), CommonDefine.COMMON_FORMAT);
			Date end = TimeUtil.parseString2Date(cond.get("end").toString(), CommonDefine.COMMON_FORMAT);
			//如果是计划任务
			String startStr = "",endStr = "";
			if(genType == 0){
				if(period == 0){
					startStr = TimeUtil.parseDate2String(start, CommonDefine.REPORT_CN_FORMAT);
				}else if(period == 1){
					startStr = TimeUtil.parseDate2String(start, CommonDefine.REPORT_CN_FORMAT_MONTH);
				}
				header += "        " + startStr;
			//如果是手动生成
			}else if(genType == 1){
				if(period == 0){
					startStr = TimeUtil.parseDate2String(start, CommonDefine.REPORT_CN_FORMAT);
					endStr = TimeUtil.parseDate2String(end, CommonDefine.REPORT_CN_FORMAT);
					header += "        " + startStr + " -> " + endStr;
				}else if(period == 1){
					startStr = TimeUtil.parseDate2String(start, CommonDefine.REPORT_CN_FORMAT_MONTH);
					endStr = TimeUtil.parseDate2String(end, CommonDefine.REPORT_CN_FORMAT_MONTH);
					header += "        " + startStr + " -> " + endStr + " 每月" + cond.get("pmDate").toString() + "日";
				}					
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int curRow = 0;
		writeCell(curRow++, 0, header, headerStyle);
		//合并A1-M1
		merge(0, 0, 0, 16);
		List<Map> detail = (List<Map>) switchData.get("NE_ROWS");
		PM_DATA = (List<Map>) switchData.get("PM_DATA");
		for(Map m:detail){
//			填写每一组信息
			curRow = writeInfo(m, curRow);
//			填写表头
			curRow = writeColumn(curRow);
//			填写具体数据
			List<Map> unitDetail = (List<Map>) m.get("UNIT_ROWS");
			for(Map rec:unitDetail){
				curRow = writeRecord(rec, curRow++, date);
			}
		}

	}
	/**
	 * 填写波分方向信息
	 * @param data 光开关信息
	 * @param startRow 起始行（0为第一行
	 * @return
	 */
    private int writeInfo(Map data, int startRow){
    	int curRow = startRow;
		//单位:
		writeCell(curRow, 0, "单位名称：" + getVal(data, "DEPARTMENT"), null);
		//网络名称：
		writeCell(curRow++, 10, "网络名称：" + getVal(data, "NET_WORK_NAME"), null);
		//设备类型：
		writeCell(curRow, 0, "设备类型：" + getVal(data, "EQPT_TYPE"), null);
		//站名：
		writeCell(curRow++, 10, "站名：" + getVal(data, "STATION"), null);
		//设备名称:
		writeCell(curRow, 0, "设备名称:" + getVal(data, "EQPT_NAME"), null);
		return curRow;
    }
    
	/**
	 * 填写表头信息
	 * @param startRow 起始行（0为第一行）
	 * @return
	 */
    private int writeColumn(int startRow){
    	int curRow = startRow;
    	//机盘名称	槽位编号	对应业务盘名称	对应业务盘槽位号	业务盘波分侧灵敏度	波道/波长	倒换门限
		//填写表头 
		writeCell(curRow, 0, "机盘名称", centerStyle);
		merge(startRow, 0, startRow+1, 0, true);
		writeCell(curRow, 1, "槽位编号", centerStyle);
		merge(startRow, 1, startRow+1, 1, true);
		writeCell(curRow, 2, "对应业务盘名称", centerStyle);
		merge(startRow, 2, startRow+1, 2, true);
		writeCell(curRow, 3, "对应业务盘槽位号", centerStyle);
		merge(startRow, 3, startRow+1, 3, true);
		writeCell(curRow, 4, "业务盘波分侧灵敏度", centerStyle);
		merge(startRow, 4, startRow+1, 4, true);
		writeCell(curRow, 5, "波道/波长", centerStyle);
		merge(startRow, 5, startRow+1, 5, true);
		writeCell(curRow, 6, "倒换门限", centerStyle);
		merge(startRow, 6, startRow+1, 6, true);

		writeCell(curRow, 7, "和路口OUT侧：（名称）", centerStyle);
		merge(startRow, 7, startRow, 9, true);
		writeCell(curRow+1, 7, "输入功率", centerStyle);
		writeCell(curRow+1, 8, "预算功率", centerStyle);
		writeCell(curRow+1, 9, "业务名称", centerStyle);
		
		writeCell(curRow, 10, "分路口一波分侧:(名称）", centerStyle);
		merge(startRow, 10, startRow, 12, true);
		writeCell(curRow+1, 10, "输入功率", centerStyle);
		writeCell(curRow+1, 11, "预算功率", centerStyle);
		writeCell(curRow+1, 12, "方向", centerStyle);
		
		writeCell(curRow, 13, "分路口二波分侧:（名称）", centerStyle);
		merge(startRow, 13, startRow, 15, true);
		writeCell(curRow+1, 13, "输入功率", centerStyle);
		writeCell(curRow+1, 14, "预算功率", centerStyle);
		writeCell(curRow+1, 15, "方向", centerStyle);
		
		writeCell(curRow, 16, "分析结论", centerStyle);
		merge(startRow, 16, startRow+1, 16, true);
		sheet.setColumnWidth(0, 16*256);
		for (int i = 1; i < 16; i++) {
			sheet.setColumnWidth(i, 9*256);
		}
		curRow+=2;
		return curRow;
    }
    /**
     * 写入一条记录
     * @param data
     * @param startRow
     */
    /*
     * "DISPLAY_NAME" : "JSTI",
                    "rpms_u" : [],
                    "STD_WAVE_NUM" : 0,
                    "BASE_UNIT_ID" : 6191,
                    "tpms_u" : [],
                    "ACTUAL_WAVE_NUM" : 0*/
    private int writeRecord(Map data, int startRow, int date){
		//机盘名称
		writeCell(startRow, 0, getVal(data, "UNIT_NAME"), borderStyle);
		//槽位编号
		writeCell(startRow, 1, getVal(data, "SLOT_NO"), borderStyle);
		//所有保护组
		int protGroCnt = getInt(data, "PROTECT_GROUP_COUNT");
		List<Map> protGrps = (List<Map>) data.get("PROTECT_GROUPS");
		int protGrpDataCnt = 0;
		if(protGrps!=null){
			protGrpDataCnt = protGrps.size();
		}
		for(int i = 0; i < protGrpDataCnt; i++){
			System.out.println("写入保护组DATA<" + (i+1) + " / " + protGrpDataCnt + ">");
			Map protGrp_i = protGrps.get(i);
			int grpIndex = getInt(protGrp_i, "GROUP_NUM");
			System.out.println("当前分组：" + grpIndex);
			//对应业务盘名称
			writeCell(startRow + grpIndex, 2, getVal(protGrp_i, "UNIT_NAME_COR"), borderStyle);
			//对应业务盘槽位号
			writeCell(startRow + grpIndex, 3, getVal(protGrp_i, "SLOT_NO_COR"), borderStyle);
			//业务盘波分侧灵敏度
			writeCell(startRow + grpIndex, 4, getVal(protGrp_i, "SENSITIVITY"), borderStyle);
			//波道/波长
			writeCell(startRow + grpIndex, 5, getVal(protGrp_i, "WAVE_LENGTH"), borderStyle);
			//倒换门限
			writeCell(startRow + grpIndex, 6, getVal(protGrp_i, "SWITCH_THRESHOLD"), borderStyle);
			//和路口OUT侧-输入功率(从data中取)
			int ptpId1_he = getInt(protGrp_i, "ptpId1_he");
			String combPm = getPM(ptpId1_he, date);
			writeCell(startRow + grpIndex, 7, combPm, borderStyle);
			//和路口OUT侧-预算功率
			writeCell(startRow + grpIndex, 8, getVal(protGrp_i, "powerBudget_he"), borderStyle);
			//和路口OUT侧-业务名称
			writeCell(startRow + grpIndex, 9, getVal(protGrp_i, "bussinessName_he"), borderStyle);
			//分路口一波分侧-输入功率(从data中取)
			int ptpId1_fen = getInt(protGrp_i, "ptpId1_fen");
			String sepPm1 = getPM(ptpId1_fen, date);
			writeCell(startRow + grpIndex, 10, sepPm1, borderStyle);
			//分路口一波分侧-预算功率
			writeCell(startRow + grpIndex, 11, getVal(protGrp_i, "powerBudget1_fen"), borderStyle);
			//分路口一波分侧-方向
			writeCell(startRow + grpIndex, 12, getVal(protGrp_i, "direction1_fen"), borderStyle);
			//分路口二波分侧-输入功率(从data中取)
			int ptpId2_fen = getInt(protGrp_i, "ptpId2_fen");
			String sepPm2 = getPM(ptpId2_fen, date);
			writeCell(startRow + grpIndex, 13, sepPm2, borderStyle);
			//分路口二波分侧-预算功率
			writeCell(startRow + grpIndex, 14, getVal(protGrp_i, "powerBudget2_fen"), borderStyle);
			//分路口二波分侧-方向
			writeCell(startRow + grpIndex, 15, getVal(protGrp_i, "direction2_fen"), borderStyle);
			
			/*
			 * “分析结论”判断方法
			 * 和路口OUT输入功率，或分路口一波分侧输入功率，或分路口二波分侧输入功率符合以下条件：>-2或<-8，所在行判断为异常
			 */
			
//			System.out.println("ptpIDs = " + ptpId1_he + ", " + ptpId1_fen + ", " + ptpId2_fen);
			//收光功率
			String rlt = "-";
			boolean isNormal = true;
//			System.out.println("和路口PM = " + combPm);
			if(!combPm.equals("-") && isNormal){
				double pm = Double.parseDouble(combPm);
				isNormal = (pm >= -8 && pm <= -2);
				rlt = isNormal ? "正常":"异常";
			}
//			System.out.println("分路口1PM = " + sepPm1);
			if(!sepPm1.equals("-") && isNormal){
				double pm = Double.parseDouble(sepPm1);
				isNormal = (pm >= -8 && pm <= -2);
				rlt = isNormal ? "正常":"异常";
			}
//			System.out.println("分路口2PM = " + sepPm2);
			if(!sepPm2.equals("-") && isNormal){
				double pm = Double.parseDouble(sepPm2);
				isNormal = (pm >= -8 && pm <= -2);
				rlt = isNormal ? "正常":"异常";
			}
			writeCell(startRow + grpIndex, 16, rlt, isNormal ? borderStyle:exceptionStyle);
		}
		if(protGrpDataCnt == 0){
			protGrpDataCnt = 1;
			for(int i=2;i<17;i++){
				writeCell(startRow, i, "-", borderStyle);
			}
		}else{
			merge(startRow, 0, startRow+protGrpDataCnt-1, 0, true);
			merge(startRow, 1, startRow+protGrpDataCnt-1, 1, true);
		}
		return protGrpDataCnt+startRow;
    }
    /**
     * 
     * @param ptpId
     * @param date
     * @return
     */
    private String getPM(int ptpId, int date){
    	String rv = "-";
    	if(PM_DATA!=null){
    		for(Map m:PM_DATA){
    			if(ptpId == Integer.parseInt("" + m.get("BASE_PTP_ID"))
    					&& date == Integer.parseInt("" + m.get("RTRV_TIME"))){
    				rv = m.get("PM_VALUE") + "";
    				break;
    			}
    		}
    	}
    	System.out.println("getPM(" + ptpId + ", " + date + ") = " + rv);
    	return rv;
    }
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String datStr = "{\"searchCond\":{\"TABLE_NAME\":\"t_pm_origi_data_7_2014_07\",\"period\":\"0\",\"pmDate\":\"1\",\"taskName\":\"svca\",\"nendRx\":\"1\",\"nendTx\":\"3\",\"start\":\"2014-06-02 00:00:00\",\"dataSrc\":\"0\",\"privilege\":\"3\",\"userId\":\"-1\",\"continueAbnormal\":\"1\",\"retrivalTime\":\"1\",\"end\":\"2014-07-03 23:59:59\"},\"genType\":1,\"PM_DATA\":[{\"LOCATION\":1,\"BASE_PTP_ID\":61252,\"RTRV_TIME\":\"20140603\",\"BASE_UNIT_ID\":5767,\"PM_VALUE\":\"-3.59\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":3,\"day\":2,\"hours\":0,\"minutes\":0,\"month\":5,\"nanos\":0,\"seconds\":0,\"time\":1401724800000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":61252,\"RTRV_TIME\":\"20140604\",\"BASE_UNIT_ID\":5767,\"PM_VALUE\":\"-3.58\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":4,\"day\":3,\"hours\":0,\"minutes\":0,\"month\":5,\"nanos\":0,\"seconds\":0,\"time\":1401811200000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":61252,\"RTRV_TIME\":\"20140605\",\"BASE_UNIT_ID\":5767,\"PM_VALUE\":\"-3.58\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":5,\"day\":4,\"hours\":0,\"minutes\":0,\"month\":5,\"nanos\":0,\"seconds\":0,\"time\":1401897600000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":61252,\"RTRV_TIME\":\"20140606\",\"BASE_UNIT_ID\":5767,\"PM_VALUE\":\"-3.58\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":6,\"day\":5,\"hours\":0,\"minutes\":0,\"month\":5,\"nanos\":0,\"seconds\":0,\"time\":1401984000000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":61252,\"RTRV_TIME\":\"20140607\",\"BASE_UNIT_ID\":5767,\"PM_VALUE\":\"-3.58\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":7,\"day\":6,\"hours\":0,\"minutes\":0,\"month\":5,\"nanos\":0,\"seconds\":0,\"time\":1402070400000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":61252,\"RTRV_TIME\":\"20140608\",\"BASE_UNIT_ID\":5767,\"PM_VALUE\":\"-3.58\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":8,\"day\":0,\"hours\":0,\"minutes\":0,\"month\":5,\"nanos\":0,\"seconds\":0,\"time\":1402156800000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":61252,\"RTRV_TIME\":\"20140611\",\"BASE_UNIT_ID\":5767,\"PM_VALUE\":\"-3.58\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":11,\"day\":3,\"hours\":0,\"minutes\":0,\"month\":5,\"nanos\":0,\"seconds\":0,\"time\":1402416000000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":61252,\"RTRV_TIME\":\"20140612\",\"BASE_UNIT_ID\":5767,\"PM_VALUE\":\"-3.58\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":12,\"day\":4,\"hours\":0,\"minutes\":0,\"month\":5,\"nanos\":0,\"seconds\":0,\"time\":1402502400000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":61252,\"RTRV_TIME\":\"20140613\",\"BASE_UNIT_ID\":5767,\"PM_VALUE\":\"-3.58\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":13,\"day\":5,\"hours\":0,\"minutes\":0,\"month\":5,\"nanos\":0,\"seconds\":0,\"time\":1402588800000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":61252,\"RTRV_TIME\":\"20140614\",\"BASE_UNIT_ID\":5767,\"PM_VALUE\":\"-3.58\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":14,\"day\":6,\"hours\":0,\"minutes\":0,\"month\":5,\"nanos\":0,\"seconds\":0,\"time\":1402675200000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":61252,\"RTRV_TIME\":\"20140616\",\"BASE_UNIT_ID\":5767,\"PM_VALUE\":\"-3.58\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":16,\"day\":1,\"hours\":0,\"minutes\":0,\"month\":5,\"nanos\":0,\"seconds\":0,\"time\":1402848000000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":61252,\"RTRV_TIME\":\"20140617\",\"BASE_UNIT_ID\":5767,\"PM_VALUE\":\"-3.58\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":17,\"day\":2,\"hours\":0,\"minutes\":0,\"month\":5,\"nanos\":0,\"seconds\":0,\"time\":1402934400000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":61252,\"RTRV_TIME\":\"20140618\",\"BASE_UNIT_ID\":5767,\"PM_VALUE\":\"-3.58\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":18,\"day\":3,\"hours\":0,\"minutes\":0,\"month\":5,\"nanos\":0,\"seconds\":0,\"time\":1403020800000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":61252,\"RTRV_TIME\":\"20140619\",\"BASE_UNIT_ID\":5767,\"PM_VALUE\":\"-3.58\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":19,\"day\":4,\"hours\":0,\"minutes\":0,\"month\":5,\"nanos\":0,\"seconds\":0,\"time\":1403107200000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":61252,\"RTRV_TIME\":\"20140620\",\"BASE_UNIT_ID\":5767,\"PM_VALUE\":\"-3.58\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":20,\"day\":5,\"hours\":0,\"minutes\":0,\"month\":5,\"nanos\":0,\"seconds\":0,\"time\":1403193600000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":61252,\"RTRV_TIME\":\"20140621\",\"BASE_UNIT_ID\":5767,\"PM_VALUE\":\"-3.58\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":21,\"day\":6,\"hours\":0,\"minutes\":0,\"month\":5,\"nanos\":0,\"seconds\":0,\"time\":1403280000000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":61252,\"RTRV_TIME\":\"20140622\",\"BASE_UNIT_ID\":5767,\"PM_VALUE\":\"-3.58\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":22,\"day\":0,\"hours\":0,\"minutes\":0,\"month\":5,\"nanos\":0,\"seconds\":0,\"time\":1403366400000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":61252,\"RTRV_TIME\":\"20140623\",\"BASE_UNIT_ID\":5767,\"PM_VALUE\":\"-3.58\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":23,\"day\":1,\"hours\":0,\"minutes\":0,\"month\":5,\"nanos\":0,\"seconds\":0,\"time\":1403452800000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":61252,\"RTRV_TIME\":\"20140624\",\"BASE_UNIT_ID\":5767,\"PM_VALUE\":\"-3.58\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":24,\"day\":2,\"hours\":0,\"minutes\":0,\"month\":5,\"nanos\":0,\"seconds\":0,\"time\":1403539200000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":61252,\"RTRV_TIME\":\"20140625\",\"BASE_UNIT_ID\":5767,\"PM_VALUE\":\"-3.58\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":25,\"day\":3,\"hours\":0,\"minutes\":0,\"month\":5,\"nanos\":0,\"seconds\":0,\"time\":1403625600000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":61252,\"RTRV_TIME\":\"20140626\",\"BASE_UNIT_ID\":5767,\"PM_VALUE\":\"-3.58\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":26,\"day\":4,\"hours\":0,\"minutes\":0,\"month\":5,\"nanos\":0,\"seconds\":0,\"time\":1403712000000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":61252,\"RTRV_TIME\":\"20140627\",\"BASE_UNIT_ID\":5767,\"PM_VALUE\":\"-3.58\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":27,\"day\":5,\"hours\":0,\"minutes\":0,\"month\":5,\"nanos\":0,\"seconds\":0,\"time\":1403798400000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":61252,\"RTRV_TIME\":\"20140628\",\"BASE_UNIT_ID\":5767,\"PM_VALUE\":\"-3.58\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":28,\"day\":6,\"hours\":0,\"minutes\":0,\"month\":5,\"nanos\":0,\"seconds\":0,\"time\":1403884800000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":61252,\"RTRV_TIME\":\"20140629\",\"BASE_UNIT_ID\":5767,\"PM_VALUE\":\"-3.58\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":29,\"day\":0,\"hours\":0,\"minutes\":0,\"month\":5,\"nanos\":0,\"seconds\":0,\"time\":1403971200000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":61252,\"RTRV_TIME\":\"20140701\",\"BASE_UNIT_ID\":5767,\"PM_VALUE\":\"-3.58\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":1,\"day\":2,\"hours\":0,\"minutes\":0,\"month\":6,\"nanos\":0,\"seconds\":0,\"time\":1404144000000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":61252,\"RTRV_TIME\":\"20140702\",\"BASE_UNIT_ID\":5767,\"PM_VALUE\":\"-3.58\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":2,\"day\":3,\"hours\":0,\"minutes\":0,\"month\":6,\"nanos\":0,\"seconds\":0,\"time\":1404230400000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0}],\"NE_ROWS\":[{\"TARGET_ID\":14,\"STATION\":\"2222222222\",\"EQPT_NAME\":\"111-广安\",\"UNIT_ROWS\":[{\"TARGET_ID\":14,\"SLOT_NO\":\"1\",\"PROTECT_GROUP_COUNT\":\"3\",\"UNIT_NAME\":\"OMU40\",\"BASE_UNIT_ID\":5767,\"PROTECT_GROUPS\":[{\"UNIT_NAME_COR\":\"OMU40\",\"GROUP_NUM\":0,\"SENSITIVITY\":\"-14\",\"powerBudget_he\":\"-8.00\",\"bussinessName_he\":\"\",\"powerBudget1_fen\":\"-8.00\",\"ptpId1_he\":\"61252\",\"SLOT_NO_COR\":\"1\",\"ptpId1_fen\":\"61254\"},{\"UNIT_NAME_COR\":\"OMU40\",\"GROUP_NUM\":1,\"SENSITIVITY\":\"-34\",\"powerBudget_he\":\"-8.00\",\"bussinessName_he\":\"\",\"powerBudget1_fen\":\"-8.00\",\"ptpId1_he\":\"61256\",\"SLOT_NO_COR\":\"1\",\"ptpId1_fen\":\"61257\"},{\"UNIT_NAME_COR\":\"OMU40\",\"GROUP_NUM\":2,\"powerBudget_he\":\"-8.00\",\"bussinessName_he\":\"\",\"powerBudget1_fen\":\"-8.00\",\"ptpId1_he\":\"61259\",\"SLOT_NO_COR\":\"1\",\"ptpId1_fen\":\"61260\"}]},{\"TARGET_ID\":14,\"SLOT_NO\":\"4\",\"UNIT_NAME\":\"EOTU10G T/R\",\"BASE_UNIT_ID\":5769}],\"NET_WORK_NAME\":\"444444444444444444444\",\"DEPARTMENT\":\"1111111111\",\"EMS_ID\":7}]}";
		Map dat = JSONObject.fromObject(datStr);
		OptSwitchExcel xls = new OptSwitchExcel("光开关盘测试-"
				+TimeUtil.parseDate2String(new Date(), CommonDefine.REPORT_CN_FORMAT_24H)
				+ ".xlsx");
		xls.writeData(dat);
	}
}
