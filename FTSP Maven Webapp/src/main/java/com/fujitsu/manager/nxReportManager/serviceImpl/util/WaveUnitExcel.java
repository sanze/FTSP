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
 * 合波盘/分波盘
 * @author TianHongjun
 *
 */
public class WaveUnitExcel extends BaseExcelUtil {
	private CellStyle exceptionStyle;
	private List<Map> PM_DATA = null;
	public WaveUnitExcel(String fileName) {
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
		Map cond = (Map) switchData.get("searchCond");
		int unitType = Integer.parseInt("" + cond.get("unitType"));
		String sheetPrefix = "合波盘";
		if(4 == unitType){
			sheetPrefix = "分波盘";
		}
		String sheetName = sheetPrefix + (date/10000) + "年" + (date%10000/100) + "月" + (date%100) + "日";
		sheetName = WorkbookUtil.createSafeSheetName(sheetName);
		sheet = book.createSheet(sheetName);
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
		merge(0, 0, 0, 9);
		List<Map> detail = (List<Map>) switchData.get("BASE_INFO_ROWS");
		PM_DATA = (List<Map>) switchData.get("PM_DATA");
		for(Map m:detail){
//			填写每一组信息
			curRow = writeInfo(m, curRow);
//			填写表头
			curRow = writeColumn(curRow, unitType);
//			填写具体数据
			List<Map> unitDetail = (List<Map>) m.get("UNIT_ROWS");
			if(unitDetail != null){
				for(Map rec:unitDetail){
					curRow = writeRecord(rec, curRow++, date, 4 != unitType);
				}
			}
			writeCell(curRow, 0, "完成说明：有各波导功率检测功能的合波盘填写各波功率，" +
					"在分析结论中要说明正常与否，不正常要计算分析原因。\n" +
					"数据单位为dBm、dB在表格中不填写单位。", wrapStyle);
			merge(curRow, 0, curRow+1, 9);
			curRow+=3;
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
		writeCell(curRow++, 5, "网络名称：" + getVal(data, "NET_WORK_NAME"), null);
		//设备类型：
		writeCell(curRow, 0, "设备类型：" + getVal(data, "EQPT_TYPE"), null);
		//站名：
		writeCell(curRow++, 5, "容量（波数）/实开（波数）：" + getVal(data, "STD_WAVE_NUM") + "/" + getVal(data, "ACTUAL_WAVE_NUM"), null);
		return curRow;
    }
    
	/**
	 * 填写表头信息
	 * @param startRow 起始行（0为第一行）
	 * @param unitType 盘类型
	 * <li>27:合波盘</li>
	 * <li>28:分波盘</li>
	 * @return
	 */
    private int writeColumn(int startRow, int unitType){
    	int curRow = startRow;
    	unitType -= 3;
    	String[] lastMonthStr = {"上月发光功率", "上月收光功率"};
    	String[] thisMonthStr = {"本月发光功率", "本月收光功率"};
		//填写表头 
    	//站点
		writeCell(curRow, 0, "站点", centerStyle);
    	//设备名称
		writeCell(curRow, 1, "设备名称", centerStyle);
    	//机盘名称
		writeCell(curRow, 2, "机盘名称", centerStyle);
    	//槽位编号
		writeCell(curRow, 3, "槽位编号", centerStyle);
    	//上月发光功率
		writeCell(curRow, 4, lastMonthStr[unitType], centerStyle);
    	//本月发光功率
		writeCell(curRow, 5, thisMonthStr[unitType], centerStyle);
    	//预算功率
		writeCell(curRow, 6, "预算功率", centerStyle);
    	//插损
		writeCell(curRow, 7, "插损", centerStyle);
    	//在用波数
		writeCell(curRow, 8, "在用波数", centerStyle);
    	//分析结论
		writeCell(curRow, 9, "分析结论", centerStyle);

		sheet.setColumnWidth(0, 16*256);
		for (int i = 1; i < 16; i++) {
			sheet.setColumnWidth(i, 12*256);
		}
		curRow++;
		return curRow;
    }
    /**
     * 写入一条记录
     * @param data
     * @param startRow
     */
    /*
     * 分波板预算功率
     * +插损+10*lg(在用波数)+(-6dBm)
     * */
    private int writeRecord(Map data, int startRow, int date, boolean isHebopan){
		//站点 
		writeCell(startRow, 0, getVal(data, "STATION_NAME"), borderStyle);
		// 设备名称
		writeCell(startRow, 1, getVal(data, "EQPT_NAME"), borderStyle);
		//机盘名称
		writeCell(startRow, 2, getVal(data, "UNIT_NAME"), borderStyle);
    	//槽位编号
		writeCell(startRow, 3, getVal(data, "SLOT_NAME"), borderStyle);
		//上月收光功率
		int ptpId = getInt(data, "PTP_ID");
		int lastMonth = getLastMonth(date);
		String lastPM = getPM(ptpId, lastMonth);
		writeCell(startRow, 4, lastPM, borderStyle);
		//本月收光功率
		String curPM = getPM(ptpId, date);
		writeCell(startRow, 5, curPM, borderStyle);
		//插损
		String insLoss = getVal(data, "INSERTION_LOSS");
		writeCell(startRow, 7, insLoss, borderStyle);
		//在用波数
		String waveNum = getVal(data, "ACTUAL_WAVE_NUM");
		writeCell(startRow, 8, waveNum, borderStyle);

		//预算功率 = -插损+10*lg(在用波数)+(-6dBm)
		if("-".equals(insLoss) || "-".equals(waveNum)){
			writeCell(startRow, 6, "-", borderStyle);
		}else{
			double iL = Double.parseDouble(insLoss);
			int wN = Integer.parseInt(waveNum);
			if(wN == 0){
				writeCell(startRow, 6, "ERROR", exceptionStyle);
			}else{
				double pwr = (isHebopan?-iL:iL) + 10 * Math.log10(wN) - 6;
				writeCell(startRow, 6, pwr, float2Style);
			}
		}
		//分析结论，BD没有给出
		writeCell(startRow, 9, "", borderStyle);
		startRow++;
		return startRow;
    }
    private int getLastMonth(int date) {
    	int year = date/10000;
    	int month = date%10000/100;
    	int day = date%100;
    	month--;
    	if(month == 0){
    		month = 12;
    		year--;
    	}
		return year*10000+month*100+day;
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
//    	System.out.println("getPM(" + ptpId + ", " + date + ") = " + rv);
    	return rv;
    }
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String datStr = "{\"searchCond\":{\"TABLE_NAME\":\"t_pm_origi_data_8_2014_06\",\"unitType\":\"3\",\"period\":\"0\",\"pmDate\":\"1\",\"taskName\":\"WAVE_JOIN_REPORT\",\"nendRx\":\"1\",\"nendTx\":\"3\",\"start\":\"2014-07-01 00:00:00\",\"dataSrc\":\"0\",\"privilege\":\"3\",\"userId\":\"-1\",\"continueAbnormal\":\"1\",\"retrivalTime\":\"1\",\"end\":\"2014-07-31 23:59:59\"},\"genType\":1,\"PM_DATA\":[{\"LOCATION\":1,\"BASE_PTP_ID\":97491,\"RTRV_TIME\":\"20140701\",\"BASE_UNIT_ID\":10370,\"PM_VALUE\":\"-16.16\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":1,\"day\":2,\"hours\":0,\"minutes\":0,\"month\":6,\"nanos\":0,\"seconds\":0,\"time\":1404144000000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":97498,\"RTRV_TIME\":\"20140701\",\"BASE_UNIT_ID\":10372,\"PM_VALUE\":\"11.54\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":1,\"day\":2,\"hours\":0,\"minutes\":0,\"month\":6,\"nanos\":0,\"seconds\":0,\"time\":1404144000000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":97491,\"RTRV_TIME\":\"20140702\",\"BASE_UNIT_ID\":10370,\"PM_VALUE\":\"-15.97\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":2,\"day\":3,\"hours\":0,\"minutes\":0,\"month\":6,\"nanos\":0,\"seconds\":0,\"time\":1404230400000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":97498,\"RTRV_TIME\":\"20140702\",\"BASE_UNIT_ID\":10372,\"PM_VALUE\":\"11.52\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":2,\"day\":3,\"hours\":0,\"minutes\":0,\"month\":6,\"nanos\":0,\"seconds\":0,\"time\":1404230400000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":97491,\"RTRV_TIME\":\"20140701\",\"BASE_UNIT_ID\":10370,\"PM_VALUE\":\"-16.16\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":1,\"day\":2,\"hours\":0,\"minutes\":0,\"month\":6,\"nanos\":0,\"seconds\":0,\"time\":1404144000000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":97498,\"RTRV_TIME\":\"20140701\",\"BASE_UNIT_ID\":10372,\"PM_VALUE\":\"11.54\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":1,\"day\":2,\"hours\":0,\"minutes\":0,\"month\":6,\"nanos\":0,\"seconds\":0,\"time\":1404144000000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":97491,\"RTRV_TIME\":\"20140702\",\"BASE_UNIT_ID\":10370,\"PM_VALUE\":\"-15.97\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":2,\"day\":3,\"hours\":0,\"minutes\":0,\"month\":6,\"nanos\":0,\"seconds\":0,\"time\":1404230400000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":97498,\"RTRV_TIME\":\"20140702\",\"BASE_UNIT_ID\":10372,\"PM_VALUE\":\"11.52\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":2,\"day\":3,\"hours\":0,\"minutes\":0,\"month\":6,\"nanos\":0,\"seconds\":0,\"time\":1404230400000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":97491,\"RTRV_TIME\":\"20140701\",\"BASE_UNIT_ID\":10370,\"PM_VALUE\":\"-16.16\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":1,\"day\":2,\"hours\":0,\"minutes\":0,\"month\":6,\"nanos\":0,\"seconds\":0,\"time\":1404144000000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":97498,\"RTRV_TIME\":\"20140701\",\"BASE_UNIT_ID\":10372,\"PM_VALUE\":\"11.54\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":1,\"day\":2,\"hours\":0,\"minutes\":0,\"month\":6,\"nanos\":0,\"seconds\":0,\"time\":1404144000000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":97491,\"RTRV_TIME\":\"20140702\",\"BASE_UNIT_ID\":10370,\"PM_VALUE\":\"-15.97\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":2,\"day\":3,\"hours\":0,\"minutes\":0,\"month\":6,\"nanos\":0,\"seconds\":0,\"time\":1404230400000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"LOCATION\":1,\"BASE_PTP_ID\":97498,\"RTRV_TIME\":\"20140702\",\"BASE_UNIT_ID\":10372,\"PM_VALUE\":\"11.52\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":2,\"day\":3,\"hours\":0,\"minutes\":0,\"month\":6,\"nanos\":0,\"seconds\":0,\"time\":1404230400000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0}],\"BASE_INFO_ROWS\":[{\"TARGET_ID\":3,\"STD_WAVE_NUM\":3,\"UNIT_ROWS\":[{\"TARGET_ID\":3,\"T_PTP_ID\":103039,\"UNIT_NAME\":\"OCI\",\"EQPT_NAME\":\"21-绵阳236局\",\"PTP_ID\":\"103039\",\"R_PTP_ID\":103038,\"SLOT_NAME\":\"18\",\"INSERTION_LOSS\":6,\"ACTUAL_WAVE_NUM\":122,\"EMS_ID\":8}],\"NET_WORK_NAME\":\"dfsaf\",\"DEPARTMENT\":\"adfadf\",\"INSERTION_LOSS\":6,\"ACTUAL_WAVE_NUM\":1},{\"TARGET_ID\":4,\"STD_WAVE_NUM\":2,\"UNIT_ROWS\":[{\"TARGET_ID\":4,\"T_PTP_ID\":97491,\"UNIT_NAME\":\"EONAD\",\"EQPT_NAME\":\"11-绵阳236局\",\"PTP_ID\":\"97491\",\"R_PTP_ID\":97492,\"SLOT_NAME\":\"12\",\"INSERTION_LOSS\":6,\"ACTUAL_WAVE_NUM\":26,\"EMS_ID\":8},{\"TARGET_ID\":4,\"T_PTP_ID\":97498,\"UNIT_NAME\":\"EONAD\",\"EQPT_NAME\":\"11-绵阳236局\",\"PTP_ID\":\"97498\",\"R_PTP_ID\":97499,\"SLOT_NAME\":\"12\",\"INSERTION_LOSS\":6,\"ACTUAL_WAVE_NUM\":17,\"EMS_ID\":8}],\"NET_WORK_NAME\":\"asfd\",\"DEPARTMENT\":\"adf\",\"INSERTION_LOSS\":6,\"ACTUAL_WAVE_NUM\":0}]}";
		Map dat = JSONObject.fromObject(datStr);
		WaveUnitExcel xls = new WaveUnitExcel("合波分波盘测试-"
				+TimeUtil.parseDate2String(new Date(), CommonDefine.REPORT_CN_FORMAT_24H)
				+ ".xlsx");
		xls.writeData(dat);
	}
}
