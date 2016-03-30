package com.fujitsu.manager.resourceManager.serviceImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.dao.mysql.ResourceAuditMapper;
import com.fujitsu.manager.resourceManager.service.ResourceAuditService;
import com.fujitsu.util.CommonUtil;
import com.fujitsu.util.FtpUtils;
/* 
 *   2015/08/24
 *   @author fanguangming
 */
@Service
//@Transactional(rollbackFor = Exception.class)
public class ResourceAuditServiceImpl extends ResourceAuditService {
	//数据的记录数
	private int dataCount;
	//所有增量数据的记录数
	private int addDataCount;
	//所有删除数据的记录数
	private int deleteDataCount;
	//所有更新数据的记录数
	private int updateDataCount;
	//每次从数据库中查询的记录数
	private int everySelectCnt = 10000;
	//查询次数
	private int selectCnt;
	//开始查询的记录数
	private int selectStart;
	//向FTP上传失败的次数
	private int updateFailCnt = 0;
	//向ftp上传异常的次数
	private int updateAbnormalCnt = 0;
	//获取系统临时路径
	private String tempPath = System.getProperty("java.io.tmpdir");
	
	@Resource
	private ResourceAuditMapper resourceAuditMapper;

	/**
	 * 建立csv文件
	 * @ ctreatCSV
	 * @param startTime	开始时间
	 * @param endTime	结束时间
	 * @param dataType	数据类型
	 * @param serailNo	流水号
	 * @throws CommonException 
	 */
    @Override
    public boolean requestEmsData(String startTime, String endTime, 
    		int dataType, String serailNo) throws CommonException {
    	File newCsvFile = null;
		List<Map> csvFileDataList = new ArrayList<Map>();
		boolean requestEmsDataResult = true;
/*******************************************客户需求变更，暂时删除********************************************/
//		//传入开始时间或者结束时间为空的情况下，传出全部有效数据
//		if (startTime == null || endTime == null) {
		//传入数据类型为网元资源稽核数据类型
		if (dataType == 1) {
			//获取网元全部有效数据
			csvFileDataList = getNeData(null, null,"all");
			//创建网元数据文件（csv）
			newCsvFile = creatNeDataCsv(csvFileDataList, serailNo);
		}
		//传入数据类型为子架数据稽核数据类型
		else if (dataType == 2) {
			//获取子架全部有效数据
			csvFileDataList = getShelfData(null, null,"all");
			//创建网元数据文件（csv）
			newCsvFile = creatShelfDataCsv(csvFileDataList, serailNo);
		}
		//传入数据类型为板卡数据稽核数据类型
		else if (dataType == 3) {
			//获取板卡全部有效数据
			csvFileDataList = getUnitData(null, null,"all");
			newCsvFile =  creatUnitDataCsv(csvFileDataList, serailNo);
		}
		//传入数据类型为端口数据稽核数据类型
		else if (dataType == 4) {
			//获取端口全部有效数据并且创建csv
			newCsvFile = getPtpDataAndCreatCSV(null, null,"all",serailNo);
		}
		//传入数据类型为SDH交叉数据稽核数据类型
		else if (dataType == 5) {
			//获取SDH交叉全部有效数据并且创建csv
			newCsvFile = getSDHDataAndCreateCSV(null, null,"all",serailNo);
		}
		//传入数据类型为OTN交叉数据稽核数据类型
		else if (dataType == 6) {
			//获取OTN交叉全部有效数据并且创建csv
			newCsvFile = getOTNDataAndCteateCSV(null, null,"all",serailNo);
		}
		//传入数据类型不在有效范围之内的场合
		else {
			requestEmsDataResult = false;
		}
//		}
/*******************************************客户需求变更，暂时删除********************************************/                           
//		//传入开始时间和结束时间都不为空的情况下，传出增量，删除和更新数据
//		else if (startTime != null && endTime != null) {
//			//传入数据类型为网元资源稽核数据类型
//			if (dataType == 1) {
//				//获取网元增量数据
//				List<Map> csvFileDataAddList = getNeData(startTime, endTime,"add");
//				//获取网元删除数据
//				List<Map> csvFileDataDeleteList = getNeData(startTime, endTime,"delete");
//				//获取网元更新数据
//				List<Map> csvFileDataUpdateList = getNeData(startTime, endTime,"update");
//				//整合获取的增量，删除和更新数据
//				csvFileDataList = IntegrationChildDataList(csvFileDataList, csvFileDataAddList, 
//						csvFileDataDeleteList, csvFileDataUpdateList);
//				//创建网元数据文件（csv）
//				newCsvFile = creatNeDataCsv(csvFileDataList, serailNo);
//			}
//			//传入数据类型为子架数据稽核数据类型
//			else if (dataType == 2) {
//				//获取子架增量数据
//				List<Map> csvFileDataAddList = getShelfData(startTime, endTime,"add");
//				//获取子架删除数据
//				List<Map> csvFileDataDeleteList = getShelfData(startTime, endTime,"delete");
//				//获取子架更新数据
//				List<Map> csvFileDataUpdateList = getShelfData(startTime, endTime,"update");
//				
//				//整合获取的增量，删除和更新数据
//				csvFileDataList = IntegrationChildDataList(csvFileDataList, csvFileDataAddList, 
//						csvFileDataDeleteList, csvFileDataUpdateList);
//				//创建网元数据文件（csv）
//				newCsvFile = creatShelfDataCsv(csvFileDataList, serailNo);
//			}
//			//传入数据类型为板卡数据稽核数据类型
//			else if (dataType == 3) {
//				//获取板卡增量数据
//				List<Map> csvFileDataAddList = getUnitData(startTime, endTime,"add");
//				//获取板卡删除数据
//				List<Map> csvFileDataDeleteList = getUnitData(startTime, endTime,"delete");
//				//获取板卡更新数据
//				List<Map> csvFileDataUpdateList = getUnitData(startTime, endTime,"update");
//				
//				//整合获取的增量，删除和更新数据
//				csvFileDataList = IntegrationChildDataList(csvFileDataList, csvFileDataAddList, 
//						csvFileDataDeleteList, csvFileDataUpdateList);
//				//创建板卡数据文件（csv）
//				newCsvFile = creatUnitDataCsv(csvFileDataList, serailNo);
//			}
//			//传入数据类型为端口数据稽核数据类型
//			else if (dataType == 4) {
//				//获取端口增量数据
//				List<Map> csvFileDataAddList = getPtpData(startTime, endTime,"add");
//				//获取端口删除数据
//				List<Map> csvFileDataDeleteList = getPtpData(startTime, endTime,"delete");
//				//获取端口更新数据
//				List<Map> csvFileDataUpdateList = getPtpData(startTime, endTime,"update");
//				
//				//整合获取的增量，删除和更新数据
//				csvFileDataList = IntegrationChildDataList(csvFileDataList, csvFileDataAddList, 
//						csvFileDataDeleteList, csvFileDataUpdateList);
//				//创建端口数据文件（csv）
//				newCsvFile = creatPtpDataCsv(csvFileDataList, serailNo);
//
//			}
//			//传入数据类型为SDH交叉数据稽核数据类型
//			else if (dataType == 5) {
//				//获取SDH交叉增量数据
//				List<Map> csvFileDataAddList = getSDHData(startTime, endTime,"add");
//				//获取SDH交叉删除数据
//				List<Map> csvFileDataDeleteList = getSDHData(startTime, endTime,"delete");
//				//获取SDH交叉更新数据
//				List<Map> csvFileDataUpdateList = getSDHData(startTime, endTime,"update");
//
//				//整合获取的增量，删除和更新数据
//				csvFileDataList = IntegrationChildDataList(csvFileDataList, csvFileDataAddList, 
//						csvFileDataDeleteList, csvFileDataUpdateList);
//				//创建SDH交叉数据文件（csv）
//				newCsvFile = creatSDHDataCsv(csvFileDataList, serailNo);
//			}
//			//传入数据类型为OTN交叉数据稽核数据类型
//			else if (dataType == 6) {
//				//获取OTN交叉增量数据
//				List<Map> csvFileDataAddList = getOTNData(startTime, endTime,"add");
//				//获取OTN交叉删除数据
//				List<Map> csvFileDataDeleteList = getOTNData(startTime, endTime,"delete");
//				//获取OTN交叉更新数据
//				List<Map> csvFileDataUpdateList = getOTNData(startTime, endTime,"update");
//				
//				//整合获取的增量，删除和更新数据
//				csvFileDataList = IntegrationChildDataList(csvFileDataList, csvFileDataAddList, 
//						csvFileDataDeleteList, csvFileDataUpdateList);
//				//创建OTN交叉数据文件（csv）
//				newCsvFile = creatOTNDataCsv(csvFileDataList, serailNo);
//			}
//			//传入数据类型不在有效范围之内的场合
//			else {
//				requestEmsDataResult = false;
//			}
//		}
		//向FTP传数据文件
		if (requestEmsDataResult) {
			boolean updateToFtpResult = updateToFtp(newCsvFile);
			requestEmsDataResult = updateToFtpResult;
		}
		//删除本地存放的临时文件
		deleteFile(newCsvFile.getName());
		return requestEmsDataResult;	
	}

    /** 	
     * 创建csv文件 	
     * @param listSource 行数据 	
     * @param sTitle     字段名 	
     * @param localPath  目录路径 	
     * @param fName      文件名 	
     * */  	
    private static File createCSVFile(List listSource, String sTitle, String localPath, String fName){
    	File newfile = null; 	
    	OutputStreamWriter writer = null;
    	
    	FileOutputStream fos = null;
    	BufferedWriter bw = null;
    	//设置字符类型为UTF-8
        String encoding = "UTF-8";
    	//获取系统时间
		Date date=new Date();
		DateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
		String systemTime=format.format(date);
    	try {
    		newfile = new File(localPath,fName + systemTime + ".csv");
            fos = new FileOutputStream(newfile);
            writer = new OutputStreamWriter(fos, encoding);
            bw = new BufferedWriter(writer);
            
    		//根据系统平台得到换行符
    		String lineSeparator = System.getProperty("line.separator");
        	//标题为空的时候
    		if (sTitle != null) {
    			sTitle = sTitle + lineSeparator;
    		}
    		else {
    			sTitle = "" + lineSeparator;
    		}
    		bw.write("\uFEFF" + sTitle);
    		if (listSource != null && listSource.size() > 0) {
    			for (int i = 0; i < listSource.size(); i++) {
    				String sData = listSource.get(i) + lineSeparator;
    				bw.write("\uFEFF" + sData);
    			}
    		}
			bw.flush();
			writer.flush();
			fos.flush();
			bw.close();
			writer.close();
			fos.close();
        } catch (IOException e)	
        {
            e.printStackTrace();
        }finally{
            try
            {
                if(null != bw) bw.close();
                if(null != fos) fos.close();
                if(null != writer) writer.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return newfile;
    }
    
    /** 	
     * 向存在的csv文件文件中添加数据	
     * @param listSource 行数据 		
     * @param fName      文件名 	
     * */  	
    private static File addDataToCSVFile(List listSource, File newfile){
	
    	OutputStreamWriter writer = null;
    	FileOutputStream fos = null;
    	BufferedWriter bw = null;
    	String encoding = "UTF-8";
    	try {
    		
            fos = new FileOutputStream(newfile,true);
            writer = new OutputStreamWriter(fos, encoding);
            bw = new BufferedWriter(writer);
             
    		//根据系统平台得到换行符
    		String lineSeparator = System.getProperty("line.separator");
    		if (listSource != null && listSource.size() > 0) {
    			for (int i = 0; i < listSource.size(); i++) {
    				String sData = listSource.get(i) + lineSeparator;
    				bw.write("\uFEFF" + sData);
    			}
    		}
			bw.flush();
			writer.flush();
			fos.flush();
			bw.close();
			writer.close();
			fos.close();
        } catch (IOException e)	
        {
            e.printStackTrace();
        }finally{
            try
            {
                if(null != bw) bw.close();
                if(null != fos) fos.close();
                if(null != writer) writer.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return newfile;
    }

	/**
	 * 作成资源稽核-网元数据数据文件（csv文件）
	 */
    private File creatNeDataCsv(List<Map> csvFileDataList, String serailNo) {

		List<String> newCsvFileDataList = new ArrayList<String>();
		String csvFileHeard = null;
		StringBuilder sb = new StringBuilder();
		//查询结果不为空的情况下
		if (csvFileDataList != null && csvFileDataList.size() > 0) {
			//写入数据文件（csv文件）标题
			csvFileHeard = "所属网管,所属子网,原生网元名称,网元标识,扩展标识,规范化网元名称,网元型号名称,网元类型,网络地址,厂家";
			//获取数据文件（csv文件）内容
			for (int i = 0; i < csvFileDataList.size(); i++) {
				//清空 csvFileDataStringBuilder
				sb.setLength(0);
				//所属网管
				sb.append(csvFileDataList.get(i).get("BELONGS_NET_MANAGEMENT") 
						!= null ? csvFileDataList.get(i).get("BELONGS_NET_MANAGEMENT") : "");
				sb.append(",");
				//所属子网
				sb.append(csvFileDataList.get(i).get("SUB_NETWORK")
						!= null ? csvFileDataList.get(i).get("SUB_NETWORK")  : "");
				sb.append(",");
				//原生网元名称
				sb.append(csvFileDataList.get(i).get("NATIVE_EMS_NAME") 
						!= null ? csvFileDataList.get(i).get("NATIVE_EMS_NAME") : "");
				sb.append(",");
				//网元标识
				sb.append(csvFileDataList.get(i).get("NAME")
						!= null ? csvFileDataList.get(i).get("NAME") : "");
				sb.append(",");
				//扩展标识
				sb.append(csvFileDataList.get(i).get("NAME") 
						!= null ? csvFileDataList.get(i).get("NAME") : "");
				sb.append(",");
				//规范化网元名称
				sb.append(csvFileDataList.get(i).get("USER_LABEL")
						!= null ? csvFileDataList.get(i).get("USER_LABEL") : "");
				sb.append(",");
				//网元型号名称
				sb.append(csvFileDataList.get(i).get("PRODUCT_NAME") 
						!= null ? csvFileDataList.get(i).get("PRODUCT_NAME") : "");
				sb.append(",");
				//网元类型
				String neTypeName = null;
				if (csvFileDataList.get(i).get("TYPE") != null 
						&& !"".equals(csvFileDataList.get(i).get("TYPE"))) {
					neTypeName = getNeTypeName(Integer.parseInt(
							String.valueOf(csvFileDataList.get(i).get("TYPE"))));
				} 
				else {
					neTypeName = "";
				}
				sb.append(neTypeName);
				sb.append(",");
				//网络地址
				sb.append(csvFileDataList.get(i).get("NET_ADDRESS")
						!= null ? csvFileDataList.get(i).get("NET_ADDRESS") : "");
				sb.append(",");
				//厂家
				String factoryName = null;
				if (csvFileDataList.get(i).get("FACTORY") != null 
						&& !"".equals(csvFileDataList.get(i).get("FACTORY"))) {
					factoryName = getFactoryName(Integer.parseInt(
							String.valueOf(csvFileDataList.get(i).get("FACTORY"))));
				}
				else {
					factoryName = "";
				}
				sb.append(factoryName);
//				csvFileDataStringBuilder.append(",");
//				//数据状态
//				csvFileDataStringBuilder.append(csvFileDataList.get(i).get("DATA_STATE")
//					!= null ? csvFileDataList.get(i).get("DATA_STATE") : "");

				//转换成String型添加进list
				newCsvFileDataList.add(sb.toString());
			}
		}
		//创建数据文件（csv文件）
		File newCsvFile = createCSVFile(newCsvFileDataList, csvFileHeard, tempPath, "FTSPDATA01");
		return newCsvFile;    	
	}
      
	/**
	 * 作成资源稽核-子架数据数据文件（csv文件）
	 */
    private File creatShelfDataCsv(List<Map> csvFileDataList, String serailNo) {
    	
		List<String> newCsvFileDataList = new ArrayList<String>();
		String csvFileHeard = null;
		StringBuilder sb = new StringBuilder();
		//查询结果不为空的情况下
		if (csvFileDataList != null && csvFileDataList.size() > 0) {
			//写入数据文件（csv文件）标题
			csvFileHeard = "网管名称,网元名称,子架名称,子架类型,机架号,子架号";
			//获取数据文件（csv文件）内容
			for (int i = 0; i < csvFileDataList.size(); i++) {
				//清空 csvFileDataStringBuilder
				sb.setLength(0);
				//网管名称
				sb.append(csvFileDataList.get(i).get("NET_MANAGEMENT_NAME")
						!= null ? csvFileDataList.get(i).get("NET_MANAGEMENT_NAME") : "");
				sb.append(",");
				//网元名称
				sb.append(csvFileDataList.get(i).get("NE_MANE")
						!= null ? csvFileDataList.get(i).get("NE_MANE") : "");
				sb.append(",");
				//子架名称
				sb.append(csvFileDataList.get(i).get("VENDOR_NAME")
						!= null ? csvFileDataList.get(i).get("VENDOR_NAME") : "");
				sb.append(",");
				//子架类型
				sb.append(csvFileDataList.get(i).get("SHELF_TYPE")
						!= null ? csvFileDataList.get(i).get("SHELF_TYPE") : "");
				sb.append(",");
				//机架号
				sb.append(csvFileDataList.get(i).get("RACK_NO")
						!= null ? csvFileDataList.get(i).get("RACK_NO") : "");
				sb.append(",");
				//子架号
				sb.append(csvFileDataList.get(i).get("SHELF_NO")
						!= null ? csvFileDataList.get(i).get("SHELF_NO") : "");
//				csvFileDataStringBuilder.append(",");
//				//数据状态
//				csvFileDataStringBuilder.append(csvFileDataList.get(i).get("DATA_STATE")
//					!= null ? csvFileDataList.get(i).get("DATA_STATE") : "");
				
				//转换成String型添加进list
				newCsvFileDataList.add(sb.toString());
			}
		}
		//创建数据文件（csv文件）
		File newCsvFile = createCSVFile(newCsvFileDataList, csvFileHeard, tempPath, "FTSPDATA02");
		return newCsvFile;    	
	}
       
	/**
	 * 作成资源稽核-板卡数据数据文件（csv文件）
	 */
    private File creatUnitDataCsv(List<Map> csvFileDataList, String serailNo) {

    	List<String> newCsvFileDataList = new ArrayList<String>();
		String csvFileHeard = null;
		StringBuilder sb = new StringBuilder();
		//查询结果不为空的情况下
		if (csvFileDataList != null && csvFileDataList.size() > 0) {
			//写入数据文件（csv文件）标题
			csvFileHeard = "网管名称,网元名称,机架编号,子架编号,槽道编号,安装板卡类型,是否保护,保护方式";
			//获取数据文件（csv文件）内容
			for (int i = 0; i < csvFileDataList.size(); i++) {
				//清空 csvFileDataStringBuilder
				sb.setLength(0);
				//网管名称
				sb.append(csvFileDataList.get(i).get("NET_MANAGEMENT_NAME")
						!= null ? csvFileDataList.get(i).get("NET_MANAGEMENT_NAME") : "");
				sb.append(",");
				//网元名称
				sb.append(csvFileDataList.get(i).get("NE_MANE")
						!= null ? csvFileDataList.get(i).get("NE_MANE")  : "");
				sb.append(",");
				//机架编号
				sb.append(csvFileDataList.get(i).get("RACK_NO")
						!= null ? csvFileDataList.get(i).get("RACK_NO")  : "");
				sb.append(",");
				//子架编号
				sb.append(csvFileDataList.get(i).get("SHELF_NO") 
						!= null ? csvFileDataList.get(i).get("SHELF_NO")  : "");
				sb.append(",");
				//槽道编号
				sb.append(csvFileDataList.get(i).get("SLOT_NO") 
						!= null ? csvFileDataList.get(i).get("SLOT_NO")  : "");
				sb.append(",");
				//安装板卡类型
				sb.append(csvFileDataList.get(i).get("INSTALLED_EQUIP_OBJ_TYPE")
						!= null ? csvFileDataList.get(i).get("INSTALLED_EQUIP_OBJ_TYPE")  : "");
				sb.append(",");
				//是否保护
				String hasProtectName = null;
				if (csvFileDataList.get(i).get("HAS_PROTECTION") != null
						&& !"".equals(csvFileDataList.get(i).get("HAS_PROTECTION"))) {
					hasProtectName = getHasProtectName(Integer.parseInt(
						String.valueOf(csvFileDataList.get(i).get("HAS_PROTECTION"))));
				}
				else {
					hasProtectName = "";
				}
				sb.append(hasProtectName);
				sb.append(",");
				//保护方式
				String protectModeName = null;
				if (csvFileDataList.get(i).get("PROTECT_MODE") != null 
						&& !"".equals(csvFileDataList.get(i).get("PROTECT_MODE"))) {
					protectModeName = getProtectModeName(Integer.parseInt(
							String.valueOf(csvFileDataList.get(i).get("PROTECT_MODE"))));
				}
				else {
					protectModeName = "";
				}
				sb.append(protectModeName);
//				csvFileDataStringBuilder.append(",");
//				//数据状态
//				csvFileDataStringBuilder.append(csvFileDataList.get(i).get("DATA_STATE")
//					!= null ? csvFileDataList.get(i).get("DATA_STATE") : "");
				
				//转换成String型添加进list
				newCsvFileDataList.add(sb.toString());
			}
		}
		//创建数据文件（csv文件）
		File newCsvFile = createCSVFile(newCsvFileDataList, csvFileHeard, tempPath, "FTSPDATA03");
		return newCsvFile;    	
	}
    
	/**
	 * 作成资源稽核-端口数据数据文件（csv文件）
	 */
    private File creatPtpDataCsv(List<Map> csvFileDataList, String serailNo, String ctreatState, File csvFile) {
    	File newCsvFile = null;
		List<String> newCsvFileDataList = new ArrayList<String>();
		String csvFileHeard = null;
		StringBuilder sb = new StringBuilder();
		//查询结果不为空的情况下
		if (csvFileDataList != null && csvFileDataList.size() > 0) {
			//写入数据文件（csv文件）标题
			csvFileHeard = "网管名称,网元名称,槽位编号,PTP名,业务类型,端口类型,速率,是否有保护";
			//获取数据文件（csv文件）内容
			for (int i = 0; i < csvFileDataList.size(); i++) {
				//清空 csvFileDataStringBuilder
				sb.setLength(0);
				//网管名称
				sb.append(csvFileDataList.get(i).get("NET_MANAGEMENT_NAME")
						!= null ? csvFileDataList.get(i).get("NET_MANAGEMENT_NAME") : "");
				sb.append(",");
				//网元名称
				sb.append(csvFileDataList.get(i).get("NE_MANE") 
						!= null ? csvFileDataList.get(i).get("NE_MANE") : "");
				sb.append(",");
				//槽位编号
				sb.append(csvFileDataList.get(i).get("SLOT_NO")
						!= null ? csvFileDataList.get(i).get("SLOT_NO") : "");
				sb.append(",");
				//PTP名
				sb.append(replaceCommaToBlank(csvFileDataList.get(i).get("NAME") 
						!= null ? String.valueOf(csvFileDataList.get(i).get("NAME")) : ""));
				sb.append(",");
				//业务类型
				String domainName = null;
				if (csvFileDataList.get(i).get("DOMAIN") != null 
						&& !"".equals(csvFileDataList.get(i).get("DOMAIN"))) {
					domainName = getDomainName(Integer.parseInt(
							String.valueOf(csvFileDataList.get(i).get("DOMAIN"))));
				}
				else {
					domainName = "";
				}
				sb.append(domainName);
				sb.append(",");
				//端口类型
				sb.append(csvFileDataList.get(i).get("PTP_TYPE") 
						!= null ? csvFileDataList.get(i).get("PTP_TYPE") : "");
				sb.append(",");
				//速率
				sb.append(csvFileDataList.get(i).get("RATE")
						!= null ? csvFileDataList.get(i).get("RATE") : "");
				sb.append(",");
				//是否有保护
				String hasProtectName = null;
				if (csvFileDataList.get(i).get("IS_PROTECTED") != null
						&& !"".equals(csvFileDataList.get(i).get("IS_PROTECTED"))) {
					hasProtectName = getHasProtectName(Integer.parseInt(
							String.valueOf(csvFileDataList.get(i).get("IS_PROTECTED"))));
				}
				else {
					hasProtectName = "";
				}
				sb.append(hasProtectName);
//				csvFileDataStringBuilder.append(",");
//				//数据状态
//				csvFileDataStringBuilder.append(csvFileDataList.get(i).get("DATA_STATE") 
//					!= null ? csvFileDataList.get(i).get("DATA_STATE") : "");
				
				//转换成String型添加进list
				newCsvFileDataList.add(sb.toString());
			}
		}
		if ("create".equals(ctreatState)) {
			//创建数据文件（csv文件）
			newCsvFile = createCSVFile(newCsvFileDataList, csvFileHeard, tempPath, "FTSPDATA04");
		}
		else if ("add".equals(ctreatState)) {
			newCsvFile = addDataToCSVFile(newCsvFileDataList, csvFile);
		}
		return newCsvFile;    	
	}
      
	/**
	 * 作成资源稽核-SDH交叉数据数据文件（csv文件）
	 */
    private File creatSDHDataCsv(List<Map> csvFileDataList, String serailNo, String ctreatState, File csvFile) {
    	File newCsvFile = null;
		List<String> newCsvFileDataList = new ArrayList<String>();
		String csvFileHeard = null;
		StringBuilder sb = new StringBuilder();
		//查询结果不为空的情况下
		if (csvFileDataList != null && csvFileDataList.size() > 0) {
			//写入数据文件（csv文件）标题
			csvFileHeard = "网管名称,网元名称,交叉连接方向,速率,A端PTP名称,A端时隙名,A端原生时隙名,A端时隙VC4-64C值,A端时隙VC4-16C值," +
					"A端时隙VC4-8C值,A端时隙VC4-4C值,A端时隙VC4原值(J),A端时隙VC4值(J),A端时隙K值,A端时隙L值,A端时隙M值,Z端PTP名称," +
					"Z端时隙名,Z端原生时隙名,Z端时隙VC4-64C值,Z端时隙VC4-16C值,Z端时隙VC4-8C值,Z端时隙VC4-4C值,Z端时隙VC4原值(J)," +
					"Z端时隙VC4值(J),Z端时隙K值,Z端时隙L值,Z端时隙M值";
			//获取数据文件（csv文件）内容
			for (int i = 0; i < csvFileDataList.size(); i++) {
				//清空 csvFileDataStringBuilder
				sb.setLength(0);
				//网管名称
				sb.append(csvFileDataList.get(i).get("NET_MANAGEMENT_NAME")
						!= null ? csvFileDataList.get(i).get("NET_MANAGEMENT_NAME") : "");
				sb.append(",");
				//网元名称
				sb.append(csvFileDataList.get(i).get("NE_MANE")
						!= null ? csvFileDataList.get(i).get("NE_MANE") : "");
				sb.append(",");
				//交叉连接方向
				String directionName = null;
				if (csvFileDataList.get(i).get("DIRECTION") != null
						&& !"".equals(csvFileDataList.get(i).get("DIRECTION"))) {
					directionName = getDirectionName(Integer.parseInt(
							String.valueOf(csvFileDataList.get(i).get("DIRECTION"))));
				}
				else {
					directionName = "";
				}
				sb.append(directionName);
				sb.append(",");
				//速率
				sb.append(csvFileDataList.get(i).get("RATE")
						!= null ? csvFileDataList.get(i).get("RATE") : "");
				sb.append(",");
				//A端PTP名称
				sb.append(csvFileDataList.get(i).get("A_PTP_NAME")
						!= null ? csvFileDataList.get(i).get("A_PTP_NAME") : "");
				sb.append(",");
				//A端时隙名
				sb.append(csvFileDataList.get(i).get("A_CTP_NAME")
						!= null ? csvFileDataList.get(i).get("A_CTP_NAME") : "");
				sb.append(",");
				//A端原生时隙名
				sb.append(csvFileDataList.get(i).get("A_NATIVE_EMS_NAME")
						!= null ? csvFileDataList.get(i).get("A_NATIVE_EMS_NAME") : "");
				sb.append(",");
				//A端时隙VC4-64C值
				sb.append(csvFileDataList.get(i).get("A_CTP_64C")
						!= null ? csvFileDataList.get(i).get("A_CTP_64C") : "");
				sb.append(",");
				//A端时隙VC4-16C值
				sb.append(csvFileDataList.get(i).get("A_CTP_16C")
						!= null ? csvFileDataList.get(i).get("A_CTP_16C") : "");
				sb.append(",");
				//A端时隙VC4-8C值
				sb.append(csvFileDataList.get(i).get("A_CTP_8C")
						!= null ? csvFileDataList.get(i).get("A_CTP_8C") : "");
				sb.append(",");
				//A端时隙VC4-4C值
				sb.append(csvFileDataList.get(i).get("A_CTP_4C")
						!= null ? csvFileDataList.get(i).get("A_CTP_4C") : "");
				sb.append(",");
				//A端时隙VC4原值(J)
				sb.append(csvFileDataList.get(i).get("A_CTP_J_ORIGINAL")
						!= null ? csvFileDataList.get(i).get("A_CTP_J_ORIGINAL") : "");
				sb.append(",");
				//A端时隙VC4值(J)
				sb.append(csvFileDataList.get(i).get("A_CTP_J")
						!= null ? csvFileDataList.get(i).get("A_CTP_J") : "");
				sb.append(",");
				//A端时隙K值
				sb.append(csvFileDataList.get(i).get("A_CTP_K")
						!= null ? csvFileDataList.get(i).get("A_CTP_K") : "");
				sb.append(",");
				//A端时隙L值
				sb.append(csvFileDataList.get(i).get("A_CTP_L")
						!= null ? csvFileDataList.get(i).get("A_CTP_L") : "");
				sb.append(",");
				//A端时隙M值
				sb.append(csvFileDataList.get(i).get("A_CTP_M")
						!= null ? csvFileDataList.get(i).get("A_CTP_M") : "");
				sb.append(",");
				//Z端PTP名称
				sb.append(csvFileDataList.get(i).get("Z_PTP_NAME")
						!= null ? csvFileDataList.get(i).get("Z_PTP_NAME") : "");
				sb.append(",");
				//Z端时隙名
				sb.append(csvFileDataList.get(i).get("Z_CTP_NAME")
						!= null ? csvFileDataList.get(i).get("Z_CTP_NAME") : "");
				sb.append(",");
				//Z端原生时隙名
				sb.append(csvFileDataList.get(i).get("Z_NATIVE_EMS_NAME")
						!= null ? csvFileDataList.get(i).get("Z_NATIVE_EMS_NAME") : "");
				sb.append(",");
				//Z端时隙VC4-64C值
				sb.append(csvFileDataList.get(i).get("Z_CTP_64C")
						!= null ? csvFileDataList.get(i).get("Z_CTP_64C") : "");
				sb.append(",");
				//Z端时隙VC4-16C值
				sb.append(csvFileDataList.get(i).get("Z_CTP_16C")
						!= null ? csvFileDataList.get(i).get("Z_CTP_16C") : "");
				sb.append(",");
				//Z端时隙VC4-8C值
				sb.append(csvFileDataList.get(i).get("Z_CTP_8C")
						!= null ? csvFileDataList.get(i).get("Z_CTP_8C") : "");
				sb.append(",");
				//Z端时隙VC4-4C值
				sb.append(csvFileDataList.get(i).get("Z_CTP_4C")
						!= null ? csvFileDataList.get(i).get("Z_CTP_4C") : "");
				sb.append(",");
				//Z端时隙VC4原值(J)
				sb.append(csvFileDataList.get(i).get("Z_CTP_J_ORIGINAL")
						!= null ? csvFileDataList.get(i).get("Z_CTP_J_ORIGINAL") : "");
				sb.append(",");
				//Z端时隙VC4值(J)
				sb.append(csvFileDataList.get(i).get("Z_CTP_J")
						!= null ? csvFileDataList.get(i).get("Z_CTP_J") : "");
				sb.append(",");
				//Z端时隙K值
				sb.append(csvFileDataList.get(i).get("Z_CTP_K")
						!= null ? csvFileDataList.get(i).get("Z_CTP_K") : "");
				sb.append(",");
				//Z端时隙L值
				sb.append(csvFileDataList.get(i).get("Z_CTP_L")
						!= null ? csvFileDataList.get(i).get("Z_CTP_L") : "");
				sb.append(",");
				//Z端时隙M值
				sb.append(csvFileDataList.get(i).get("Z_CTP_M")
						!= null ? csvFileDataList.get(i).get("Z_CTP_M") : "");
//				csvFileDataStringBuilder.append(",");
//				//数据状态
//				csvFileDataStringBuilder.append(csvFileDataList.get(i).get("DATA_STATE")
//					!= null ? csvFileDataList.get(i).get("DATA_STATE") : "");
				
				//转换成String型添加进list
				newCsvFileDataList.add(sb.toString());
			}
		}
		if ("create".equals(ctreatState)) {
			//创建数据文件（csv文件）
			newCsvFile = createCSVFile(newCsvFileDataList, csvFileHeard, tempPath, "FTSPDATA05");
		}
		else if ("add".equals(ctreatState)) {
			newCsvFile = addDataToCSVFile(newCsvFileDataList, csvFile);
		}
		return newCsvFile;    	
	}
	
	/**
	 * 作成资源稽核-OTN交叉数据数据文件（csv文件）
	 */
    private File creatOTNDataCsv(List<Map> csvFileDataList, String serailNo, String ctreatState, File csvFile) {
    	File newCsvFile = null; 
		List<String> newCsvFileDataList = new ArrayList<String>();
		String csvFileHeard = null;
		StringBuilder sb = new StringBuilder();
		//查询结果不为空的情况下
		if (csvFileDataList != null && csvFileDataList.size() > 0) {
			//写入数据文件（csv文件）标题
			csvFileHeard = "网管名称,网元名称,交叉连接方向,A端PTP名,A端时隙名,A端原生时隙名,A_OS,A_OTS,A_OMS,A_OCH,A_ODU0," +
					"A_ODU1,A_ODU2,A_ODU3,A_OTU0,A_OTU1,A_OTU2,A_OTU3,A_DSR,A端OAC端口类型,A端OAC端口值,A端时隙类型,Z端端口," +
					"Z端时隙名,Z端原生时隙名,Z_OS,Z_OTS,Z_OMS,Z_OCH,Z_ODU0,Z_ODU1,Z_ODU2,Z_ODU3,Z_OTU0,Z_OTU1,Z_OTU2,Z_OTU3," +
					"Z_DSR,Z端OAC端口类型,Z端OAC端口值,Z端时隙类型,客户端接口类型,客户端接口速率,速率";

			//获取数据文件（csv文件）内容
			for (int i = 0; i < csvFileDataList.size(); i++) {
				//清空 csvFileDataStringBuilder
				sb.setLength(0);
				//网管名称
				sb.append(csvFileDataList.get(i).get("NET_MANAGEMENT_NAME")
						!= null ? csvFileDataList.get(i).get("NET_MANAGEMENT_NAME") : "");
				sb.append(",");
				//网元名称
				sb.append(csvFileDataList.get(i).get("NE_MANE") != null ? csvFileDataList.get(i).get("NE_MANE") : "");
				sb.append(",");
				//交叉连接方向
				String directionName = null;
				if (csvFileDataList.get(i).get("DIRECTION") != null
						&& !"".equals(csvFileDataList.get(i).get("DIRECTION"))) {
					directionName = getDirectionName(Integer.parseInt(
							String.valueOf(csvFileDataList.get(i).get("DIRECTION"))));
				}
				else {
					directionName = "";
				}
				sb.append(directionName);
				sb.append(",");
				//A端PTP名称
				sb.append(csvFileDataList.get(i).get("A_PTP_NAME")
						!= null ? csvFileDataList.get(i).get("A_PTP_NAME") : "");
				sb.append(",");
				//A端时隙名
				sb.append(csvFileDataList.get(i).get("A_CTP_NAME")
						!= null ? csvFileDataList.get(i).get("A_CTP_NAME") : "");
				sb.append(",");
				//A端原生时隙名
				sb.append(csvFileDataList.get(i).get("A_NATIVE_EMS_NAME")
						!= null ? csvFileDataList.get(i).get("A_NATIVE_EMS_NAME") : "");
				sb.append(",");
				//A_OS
				sb.append(replaceCommaToBlank(csvFileDataList.get(i).get("A_OS")
						!= null ? String.valueOf(csvFileDataList.get(i).get("A_OS")) : ""));
				sb.append(",");
				//A_OTS
				sb.append(replaceCommaToBlank(csvFileDataList.get(i).get("A_OTS")
						!= null ? String.valueOf(csvFileDataList.get(i).get("A_OTS")) : ""));
				sb.append(",");
				//A_OMS
				sb.append(replaceCommaToBlank(csvFileDataList.get(i).get("A_OMS")
						!= null ? String.valueOf(csvFileDataList.get(i).get("A_OMS")) : ""));
				sb.append(",");
				//A_OCH
				sb.append(replaceCommaToBlank(csvFileDataList.get(i).get("A_OCH")
						!= null ? String.valueOf(csvFileDataList.get(i).get("A_OCH")) : ""));
				sb.append(",");
				//A_ODU0
				sb.append(replaceCommaToBlank(csvFileDataList.get(i).get("A_ODU0") 
						!= null ? String.valueOf(csvFileDataList.get(i).get("A_ODU0")) : ""));
				sb.append(",");
				//A_ODU1
				sb.append(replaceCommaToBlank(csvFileDataList.get(i).get("A_ODU1") 
						!= null ? String.valueOf(csvFileDataList.get(i).get("A_ODU1")) : ""));
				sb.append(",");
				//A_ODU2
				sb.append(replaceCommaToBlank(csvFileDataList.get(i).get("A_ODU2") 
						!= null ? String.valueOf(csvFileDataList.get(i).get("A_ODU2")) : ""));
				sb.append(",");
				//A_ODU3
				sb.append(replaceCommaToBlank(csvFileDataList.get(i).get("A_ODU3")
						!= null ? String.valueOf(csvFileDataList.get(i).get("A_ODU3")) : ""));
				sb.append(",");
				//A_OTU0
				sb.append(replaceCommaToBlank(csvFileDataList.get(i).get("A_OTU0") 
						!= null ? String.valueOf(csvFileDataList.get(i).get("A_OTU0")) : ""));
				sb.append(",");
				//A_OTU1
				sb.append(replaceCommaToBlank(csvFileDataList.get(i).get("A_OTU1")
						!= null ? String.valueOf(csvFileDataList.get(i).get("A_OTU1")) : ""));
				sb.append(",");
				//A_OTU2
				sb.append(replaceCommaToBlank(csvFileDataList.get(i).get("A_OTU2")
						!= null ? String.valueOf(csvFileDataList.get(i).get("A_OTU2")) : ""));
				sb.append(",");
				//A_OTU3
				sb.append(replaceCommaToBlank(csvFileDataList.get(i).get("A_OTU3")
						!= null ? String.valueOf(csvFileDataList.get(i).get("A_OTU3")) : ""));
				sb.append(",");
				//A_DSR
				sb.append(replaceCommaToBlank(csvFileDataList.get(i).get("A_DSR") 
						!= null ? String.valueOf(csvFileDataList.get(i).get("A_DSR")) : ""));
				sb.append(",");
				//A端OAC端口类型
				sb.append(replaceCommaToBlank(csvFileDataList.get(i).get("A_OAC_TYPE") 
						!= null ? String.valueOf(csvFileDataList.get(i).get("A_OAC_TYPE")) : ""));
				sb.append(",");
				//A端OAC端口值
				sb.append(replaceCommaToBlank(csvFileDataList.get(i).get("A_OAC_VALUE") 
						!= null ? String.valueOf(csvFileDataList.get(i).get("A_OAC_VALUE")) : ""));
				sb.append(",");
				//A端时隙类型
				sb.append(replaceCommaToBlank(csvFileDataList.get(i).get("A_TYPE") 
						!= null ? String.valueOf(csvFileDataList.get(i).get("A_TYPE")) : ""));
				sb.append(",");
				//Z端端口
				sb.append(replaceCommaToBlank(csvFileDataList.get(i).get("Z_PTP_NAME") 
						!= null ? String.valueOf(csvFileDataList.get(i).get("Z_PTP_NAME")) : ""));
				sb.append(",");
				//Z端时隙名
				sb.append(replaceCommaToBlank(csvFileDataList.get(i)
						.get("Z_CTP_NAME") != null ? String.valueOf(csvFileDataList.get(i).get("Z_CTP_NAME")) : ""));
				sb.append(",");
				//Z端原生时隙名
				sb.append(csvFileDataList.get(i).get("Z_NATIVE_EMS_NAME")
						!= null ? csvFileDataList.get(i).get("Z_NATIVE_EMS_NAME") : "");
				sb.append(",");
				//Z_OS
				sb.append(replaceCommaToBlank(csvFileDataList.get(i)
						.get("Z_OS") != null ? String.valueOf(csvFileDataList.get(i).get("Z_OS")) : ""));
				sb.append(",");
				//Z_OTS
				sb.append(replaceCommaToBlank(csvFileDataList.get(i)
						.get("Z_OTS") != null ? String.valueOf(csvFileDataList.get(i).get("Z_OTS")) : ""));
				sb.append(",");
				//Z_OMS
				sb.append(replaceCommaToBlank(csvFileDataList.get(i).get("Z_OMS")
						!= null ? String.valueOf(csvFileDataList.get(i).get("Z_OMS")) : ""));
				sb.append(",");
				//Z_OCH
				sb.append(replaceCommaToBlank(csvFileDataList.get(i)
						.get("Z_OCH") != null ? String.valueOf(csvFileDataList.get(i).get("Z_OCH")) : ""));
				sb.append(",");
				//Z_ODU0
				sb.append(replaceCommaToBlank(csvFileDataList.get(i)
						.get("Z_ODU0") != null ? String.valueOf(csvFileDataList.get(i).get("Z_ODU0")) : ""));
				sb.append(",");
				//Z_ODU1
				sb.append(replaceCommaToBlank(csvFileDataList.get(i)
						.get("Z_ODU1") != null ? String.valueOf(csvFileDataList.get(i).get("Z_ODU1")) : ""));
				sb.append(",");
				//Z_ODU2
				sb.append(replaceCommaToBlank(csvFileDataList.get(i)
						.get("Z_ODU2") != null ? String.valueOf(csvFileDataList.get(i).get("Z_ODU2")) : ""));
				sb.append(",");
				//Z_ODU3
				sb.append(replaceCommaToBlank(csvFileDataList.get(i)
						.get("Z_ODU3") != null ? String.valueOf(csvFileDataList.get(i).get("Z_ODU3")) : ""));
				sb.append(",");
				//Z_OTU0
				sb.append(replaceCommaToBlank(csvFileDataList.get(i)
						.get("Z_OTU0") != null ? String.valueOf(csvFileDataList.get(i).get("Z_OTU0")) : ""));
				sb.append(",");
				//Z_OTU1
				sb.append(replaceCommaToBlank(csvFileDataList.get(i)
						.get("Z_OTU1") != null ? String.valueOf(csvFileDataList.get(i).get("Z_OTU1")) : ""));
				sb.append(",");
				//Z_OTU2
				sb.append(replaceCommaToBlank(csvFileDataList.get(i)
						.get("Z_OTU2") != null ? String.valueOf(csvFileDataList.get(i).get("Z_OTU2")) : ""));
				sb.append(",");		
				//Z_OTU3
				sb.append(replaceCommaToBlank(csvFileDataList.get(i)
						.get("Z_OTU3") != null ? String.valueOf(csvFileDataList.get(i).get("Z_OTU3")) : ""));
				sb.append(",");
				//Z_DSR
				sb.append(replaceCommaToBlank(csvFileDataList.get(i).get("Z_DSR") 
						!= null ? String.valueOf(csvFileDataList.get(i).get("Z_DSR")) : ""));
				sb.append(",");
				//Z端OAC端口类型
				sb.append(replaceCommaToBlank(csvFileDataList.get(i).get("Z_OAC_TYPE")
						!= null ? String.valueOf(csvFileDataList.get(i).get("Z_OAC_TYPE")) : ""));
				sb.append(",");
				//Z端OAC端口值
				sb.append(replaceCommaToBlank(csvFileDataList.get(i).get("Z_OAC_VALUE")
						!= null ? String.valueOf(csvFileDataList.get(i).get("Z_OAC_VALUE")) : ""));
				sb.append(",");
				//Z端时隙类型
				sb.append(replaceCommaToBlank(csvFileDataList.get(i).get("Z_TYPE")
						!= null ? String.valueOf(csvFileDataList.get(i).get("Z_TYPE")) : ""));
				sb.append(",");
				//客户端接口类型
				sb.append(replaceCommaToBlank(csvFileDataList.get(i).get("CLIENT_TYPE")
						!= null ? String.valueOf(csvFileDataList.get(i).get("CLIENT_TYPE")) : ""));
				sb.append(",");
				//客户端接口速率
				sb.append(csvFileDataList.get(i).get("CLIENT_RATE")
						!= null ? csvFileDataList.get(i).get("CLIENT_RATE") : "");
				sb.append(",");		
				//速率
				sb.append(csvFileDataList.get(i).get("RATE")
						!= null ? csvFileDataList.get(i).get("RATE") : "");
//				csvFileDataStringBuilder.append(",");
//				//数据状态
//				csvFileDataStringBuilder.append(csvFileDataList.get(i)
//				.get("DATA_STATE") != null ? csvFileDataList.get(i).get("DATA_STATE") : "");
				
				//转换成String型添加进list
				newCsvFileDataList.add(sb.toString());
			}
		}
		if ("create".equals(ctreatState)) {
			//创建数据文件（csv文件）
			newCsvFile = createCSVFile(newCsvFileDataList, csvFileHeard, tempPath, "FTSPDATA06");
		}
		else if ("add".equals(ctreatState)) {
			newCsvFile = addDataToCSVFile(newCsvFileDataList, csvFile);
		}
		return newCsvFile;    	
	}
	
	/**
	 * 获取网元数据
	 */
    private List<Map> getNeData(String startTime, String endTime, String dataState) {
		List<Map> neDataMapList = new ArrayList<Map>();
		neDataMapList = resourceAuditMapper.getBaseNeData(startTime, endTime, dataState);
		return neDataMapList;
	}
    
	/**
	 * 获取子架数据
	 */
    private List<Map> getShelfData(String startTime, String endTime, String dataState) {
		List<Map> shelftDataMapList = new ArrayList<Map>();
		shelftDataMapList = resourceAuditMapper.getBaseShelfData(startTime, endTime, dataState);
		return shelftDataMapList;
	}
	
	/**
	 * 获取板卡数据
	 */
    private List<Map> getUnitData(String startTime, String endTime, String dataState) {
		List<Map> unitMapList = new ArrayList<Map>();
		unitMapList = resourceAuditMapper.getBaseUnitData(startTime, endTime, dataState);
		return unitMapList;
	}
    
	/**
	 * 获取端口数据并且写入csv
	 */
    private File getPtpDataAndCreatCSV(String startTime, String endTime, String dataState, String serailNo) {
//		List<Map> ptpDataMapList = new ArrayList<Map>();
		File csvFile = null;
		//所有有效数据的记录数
		if ("all".equals(dataState)) {
			dataCount = resourceAuditMapper.getBasePtpDataCount(startTime, endTime, dataState);
		} else {
			//所有增量数据的记录数
			if ("add".equals(dataState)) {
				addDataCount = resourceAuditMapper.getBasePtpDataCount(startTime, endTime, dataState);
			}
			//所有删除数据的记录数
			if ("delete".equals(dataState)) {
				deleteDataCount = resourceAuditMapper.getBasePtpDataCount(startTime, endTime, dataState);
			}
			//所有更新数据的记录数
			if ("update".equals(dataState)) {
				updateDataCount = resourceAuditMapper.getBasePtpDataCount(startTime, endTime, dataState);
			}
			dataCount = addDataCount + deleteDataCount + updateDataCount;
		}
		selectCnt = dataCount/everySelectCnt;
		for (int i = 0; i <= selectCnt; i++) {
			selectStart = everySelectCnt * i;
			List<Map> dataMapList = resourceAuditMapper.getBasePtpData(
					startTime, endTime, dataState, selectStart, everySelectCnt);
			//第一次查询结果是创建csv文件有标题
			if (i == 0) {
				csvFile = creatPtpDataCsv(dataMapList, serailNo, "create", null);
			}
			//后续情况向csv中添加数据
			else {
				//查询结果不为空的情况下
				if (dataMapList != null && dataMapList.size() > 0) {
					csvFile = creatPtpDataCsv(dataMapList, serailNo, "add", csvFile);
				}
			}
		}
		return csvFile;
	}
    
	/**
	 * 获取SDH交叉数据并创建csv文件
	 */
    private File getSDHDataAndCreateCSV(String startTime,
    		String endTime, String dataState, String serailNo) {
//		List<Map> sdhDataMapList = new ArrayList<Map>();
    	File csvFile = null;
		//所有有效数据的记录数
		if ("all".equals(dataState)) {
			dataCount = resourceAuditMapper.getBaseSDHDataCount(startTime, endTime, dataState);
		} else {
			//所有增量数据的记录数
			if ("add".equals(dataState)) {
				addDataCount = resourceAuditMapper.getBaseSDHDataCount(startTime, endTime, dataState);
			}
			//所有删除数据的记录数
			if ("delete".equals(dataState)) {
				deleteDataCount = resourceAuditMapper.getBaseSDHDataCount(startTime, endTime, dataState);
			}
			//所有更新数据的记录数
			if ("update".equals(dataState)) {
				updateDataCount = resourceAuditMapper.getBaseSDHDataCount(startTime, endTime, dataState);
			}
			dataCount = addDataCount + deleteDataCount + updateDataCount;
		}
		selectCnt = dataCount/everySelectCnt;
		for (int i = 0; i <= selectCnt; i++) {
			selectStart = everySelectCnt * i;
			List<Map> dataMapList = resourceAuditMapper
					.getBaseSDHData(startTime, endTime, dataState, selectStart, everySelectCnt);
			//第一次查询结果是创建csv文件有标题
			if (i == 0) {
				csvFile = creatSDHDataCsv(dataMapList, serailNo, "create", null);
			}
			//后续情况向csv中添加数据
			else {
				//查询结果不为空的情况下
				if (dataMapList != null && dataMapList.size() > 0) {
					csvFile = creatSDHDataCsv(dataMapList, serailNo, "add", csvFile);
				}
			}
		}
		
		return csvFile;
	}
    
	/**
	 * 获取OTN交叉数据
	 */
    private File getOTNDataAndCteateCSV(String startTime,
    		String endTime, String dataState, String serailNo) {
//		List<Map> otnDataMapList = new ArrayList<Map>();
    	File csvFile = null;
		//所有有效数据的记录数
		if ("all".equals(dataState)) {
			dataCount = resourceAuditMapper
					.getBaseOTNDataCount(startTime, endTime, dataState);
		} else {
			//所有增量数据的记录数
			if ("add".equals(dataState)) {
				addDataCount = resourceAuditMapper
						.getBaseOTNDataCount(startTime, endTime, dataState);
			}
			//所有删除数据的记录数
			if ("delete".equals(dataState)) {
				deleteDataCount = resourceAuditMapper
						.getBaseOTNDataCount(startTime, endTime, dataState);
			}
			//所有更新数据的记录数
			if ("update".equals(dataState)) {
				updateDataCount = resourceAuditMapper
						.getBaseOTNDataCount(startTime, endTime, dataState);
			}
			dataCount = addDataCount + deleteDataCount + updateDataCount;
		}
		selectCnt = dataCount/everySelectCnt;
		for (int i = 0; i <= selectCnt; i++) {
			selectStart = everySelectCnt * i;
			List<Map> dataMapList = resourceAuditMapper
					.getBaseOTNData(startTime, endTime, dataState, selectStart, everySelectCnt);
			//第一次查询结果是创建csv文件有标题
			if (i == 0) {
				csvFile = creatOTNDataCsv(dataMapList, serailNo, "create", null);
			}
			//后续情况向csv中添加数据
			else {
				//查询结果不为空的情况下
				if (dataMapList != null && dataMapList.size() > 0) {
					csvFile = creatOTNDataCsv(dataMapList, serailNo, "add", csvFile);
				}
			}
		}
		return csvFile;
	}
    
      
	/**
	 * 把创建的数据文件传到FTP
	 * @param newCsvFile
	 * @throws CommonException 
	 * return
	 */
    private boolean updateToFtp(File newCsvFile) {
    	boolean updateToFtpResult = false;	
    	boolean updateResult = false;

		//创建数据文件结果为true的场合，向ftp上传文件
		//ip
		String ip = CommonUtil.getSystemConfigProperty("ftpIp");
		//port
		int port = Integer.parseInt(CommonUtil.getSystemConfigProperty("ftpPort"));
		//userName
		String userName = CommonUtil.getSystemConfigProperty("ftpUserName");
		//password
		String password = CommonUtil.getSystemConfigProperty("ftpPassword");
		//directory
		String ftpDirectory = CommonUtil.getSystemConfigProperty("ftpDir");
		
		try {
			FtpUtils ftpUtils = new FtpUtils(ip, port, userName, password);

			//判断FTP上是否存在此文件，如果存在则删除
			boolean ftpHasFile = ftpUtils.checkFileExist(newCsvFile.getName());
			if (ftpHasFile) {
				ftpUtils.deleteFile(newCsvFile.getName());
				updateToFtp(newCsvFile);
			} else {
				//把文件上传到ftp
				String sourceName = tempPath + "/" + newCsvFile.getName();
				updateResult = ftpUtils.uploadFile(sourceName, ftpDirectory, newCsvFile.getName());
				//上传失败的场合
				if (!updateResult) {
					//上传失败的情况下重新上传，但是限定次数
					if (updateFailCnt < 2) {
						updateFailCnt++;
						updateToFtp(newCsvFile);
					}
					else {
						updateFailCnt = 0;
					}
				}
				//上传成功的场合
				else if (updateResult) {
					updateToFtpResult = true;
				}
			}
		} 
		//发生异常的情况也重新上传，但是限定次数
		catch (CommonException e) {
			//上传异常的情况下重新上传，但是限定次数
			if (updateAbnormalCnt < 2) {
				updateAbnormalCnt++;
				updateToFtp(newCsvFile);
			}
			else {
				updateAbnormalCnt = 0;
			}
		}
		return updateToFtpResult;	
	}
      
	/**
	 * 获取网元类型实际名
	 */
    private String getNeTypeName(int neTypeInt){
    	String neTypeName = null;
    	
		//网元类型1.SDH
		if (CommonDefine.NE_TYPE_SDH_FLAG == neTypeInt) {
			neTypeName = "SDH";
		}
		//网元类型2.WDM
		else if (CommonDefine.NE_TYPE_WDM_FLAG == neTypeInt) {
			neTypeName = "WDM";
		}
		//网元类型3.OTN
		else if (CommonDefine.NE_TYPE_OTN_FLAG == neTypeInt) {
			neTypeName = "OTN";
		}
		//网元类型4.PTN
		else if (CommonDefine.NE_TYPE_PTN_FLAG == neTypeInt) {
			neTypeName = "PTN";
		}
		//网元类型5.微波
		else if (CommonDefine.NE_TYPE_MICROWAVE_FLAG == neTypeInt) {
			neTypeName = "微波";
		}
		//网元类型6.FTTX
		else if (CommonDefine.NE_TYPE_FTTX_FLAG == neTypeInt) {
			neTypeName = "FTTX";
		}
		//网元类型9.虚拟网元
		else if (CommonDefine.NE_TYPE_VIRTUAL_NE_FLAG == neTypeInt) {
			neTypeName = "虚拟网元";
		}
		//网元类型99. 未知
		else if (CommonDefine.NE_TYPE_UNKNOW_FLAG == neTypeInt) {
			neTypeName = "未知";
		}
		//不在有效网元类型之内
		else {
			neTypeName = "";
		}

		return neTypeName;
	}
	
    /**
	 * 获取设备厂家名称
	 */
    private String getFactoryName(int factoryInt){
    	String factoryName = null;

		//华为
		if (CommonDefine.FACTORY_HW_FLAG == factoryInt) {
			factoryName = "华为";
		}
		//中兴
		else if (CommonDefine.FACTORY_ZTE_FLAG == factoryInt) {
			factoryName = "中兴";
		}	
		//朗讯
		else if (CommonDefine.FACTORY_LUCENT_FLAG == factoryInt) {
			factoryName = "朗讯";
		}
		//烽火
		else if (CommonDefine.FACTORY_FIBERHOME_FLAG == factoryInt) {
			factoryName = "烽火";
		}
		//贝尔
		else if (CommonDefine.FACTORY_ALU_FLAG == factoryInt) {
			factoryName = "贝尔";
		}
		//富士通
		else if (CommonDefine.FACTORY_FUJITSU_FLAG == factoryInt) {
			factoryName = "富士通";
		}
		//不在有效设备厂家名称之内
		else {
			factoryName = "";
		} 	
		return factoryName;
	}
    
	/**
	 * 是否保护
	 */
    private String getHasProtectName(int hasProtectInt){
    	String hasProtectName = null;

		//是否保护（是，否）
		if (hasProtectInt == 0) {
			hasProtectName = "不是";
		}
		else if (hasProtectInt == 1) {
			hasProtectName = "是";
		}
		else {
			hasProtectName = "";
		}

		return hasProtectName;
	}
    
	/**
	 * 获取保护方式
	 */
    private String getProtectModeName(int protectModeInt){
    	String protectModeName = null;

		//保护方式
		if (protectModeInt == CommonDefine.UNIT_PROMODE_CTP_FLAG) {
			protectModeName = CommonDefine.UNIT_PROMODE_CTP;
		} else if (protectModeInt == CommonDefine.UNIT_PROMODE_MS_FLAG) {
			protectModeName = CommonDefine.UNIT_PROMODE_MS;
		}
		else {
			protectModeName = "";
		}
		return protectModeName;
	}
    
	/**
	 * 获取业务类型名称
	 */
    private String getDomainName(int domainInt){
    	String domainName = null;

		//业务类型
		//SDH
		if (domainInt == CommonDefine.PM.DOMAIN.DOMAIN_SDH_FLAG) {
			domainName = CommonDefine.PM.DOMAIN_SDH_DISPLAY;
		}
		//WDM
		else if (domainInt == CommonDefine.PM.DOMAIN.DOMAIN_WDM_FLAG) {
			domainName = CommonDefine.PM.DOMAIN_WDM_DISPLAY;
		}
		//ETH
		else if (domainInt == CommonDefine.PM.DOMAIN.DOMAIN_ETH_FLAG) {
			domainName = CommonDefine.PM.DOMAIN_ETH_DISPLAY;
		}
		//ATM
		else if (domainInt == CommonDefine.PM.DOMAIN.DOMAIN_ATM_FLAG) {
			domainName = CommonDefine.PM.DOMAIN_ATM_DISPLAY;
		}
		//未知
		else if (domainInt == CommonDefine.PM.DOMAIN.DOMAIN_UNKNOW_FLAG) {
			domainName = CommonDefine.PM.DOMAIN_UNKNOW_DISPLAY;
		}
		//不在有效业务类型之内
		else {
			domainName = "";
		}

		return domainName;
	}
	   
	/**
	 * 获取交叉连接方向
	 */
    private String getDirectionName(int directionInt){
    	String directionName = null;

		//交叉连接方向
		//0._D_NA
		if (directionInt == 0) {
			directionName = "_D_NA";
		}
		//1._D_BIDIRECTIONAL
		else if (directionInt == 1) {
			directionName = "_D_BIDIRECTIONAL";
		}
		//2._D_SOURCE
		else if (directionInt == 2) {
			directionName = "_D_SOURCE";
		}
		//3._D_SINK
		else if (directionInt == 3) {
			directionName = "_D_SINK";
		}
		//不在有效交叉连接方向之内
		else {
			directionName = "";
		}

		return directionName;
	}
	
    /**
 	 * 整合增量，删除和更新数据
 	 */
     private List<Map> IntegrationChildDataList(
    		 List<Map> csvFileDataList, List<Map> csvFileDataAddList,
    		 List<Map> csvFileDataDeleteList, List<Map> csvFileDataUpdateList){
    	 //增量数据不为空的场合
    	 if (csvFileDataAddList != null && csvFileDataAddList.size() > 0) {
    		 for (int i = 0; i < csvFileDataAddList.size(); i++) {
    			 csvFileDataList.add(csvFileDataAddList.get(i));
    		 }
    	 }
    	 //删除数据不为空的场合
    	 if (csvFileDataDeleteList != null && csvFileDataDeleteList.size() > 0) {
    		 for (int i = 0; i < csvFileDataDeleteList.size(); i++) {
    			 csvFileDataList.add(csvFileDataDeleteList.get(i));
    		 }
    	 }
    	 //更新数据不为空的场合
    	 if (csvFileDataUpdateList != null && csvFileDataUpdateList.size() > 0) {
    		 for (int i = 0; i < csvFileDataUpdateList.size(); i++) {
    			 csvFileDataList.add(csvFileDataUpdateList.get(i));
    		 }
    	 }
    	 return csvFileDataList;
 	}
     
 	/**
 	 * 把半角逗号（,）变为空格
 	 */
     private String replaceCommaToBlank(String commaDataStr){
     	//把半角逗号（,）变为空格
     	String blankData = commaDataStr.replaceAll(",", "");
 		return blankData;
 	}

     /** 
      * 删除单个文件
      * @param   sName    被删除文件的文件名
      * @return 单个文件删除成功返回true，否则返回false
      */
     private boolean deleteFile(String sName) {
         boolean flag = false;
         String tempPath = System.getProperty("java.io.tmpdir");
         String sPathName = tempPath + "/" + sName;
         File file = new File(sPathName);
         // 路径为文件且不为空则进行删除
         if (file.isFile() && file.exists()) {
             file.delete();
             flag = true;
         }
         return flag;
     }
}
