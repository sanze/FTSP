package com.fujitsu.util;

import globaldefs.NameAndStringValue_T;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.DataCollectDefine;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.LayeredParametersModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.PmDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.PmMeasurementModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.TCADataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.TerminationPointModel;

/**
 * @author xuxiaojun
 * 
 */
public class FileWriterUtil {

	public final static String BASE_FILE_PATH = System.getProperty("user.dir")
			+ "/OutPutFiles/";

	private static String lineSeparator = System.getProperty("line.separator");

	// public boolean writeToTxt(String filePath)
	// throws CommonException {
	//
	// FileWriter fileWriter = null;
	//
	// try{
	// fileWriter = new FileWriter(filePath+"text.txt", true);
	//
	// for(int i = 0;i<100000;i++){
	// StringBuffer modelString = new StringBuffer();
	// modelString.append("value" +i+",value" +i);
	// fileWriter.write(modelString.toString());
	// fileWriter.write(lineSeparator);
	// System.out.println(modelString.toString());
	// }
	// fileWriter.flush();
	// fileWriter.close();
	//
	// }catch (Exception e) {
	// return false;
	// } finally {
	//
	// }
	// return true;
	// }

	/**
	 * @param filePath
	 * @param model
	 * @param append
	 * @return
	 * @throws CommonException
	 */
	public static boolean writeToTxtTCA(String filePath,
			TCADataModel model)
			throws CommonException {

		FileWriter fileWriter = null;
		
		try {
			File file = new File(filePath);
			// 创建父文件
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			fileWriter = new FileWriter(file.getPath(), true);

			StringBuffer modelString = new StringBuffer();

			modelString.append("" + "【"+new Date()+"】");
			modelString.append("，" + model.getObjectNameFullString());
			modelString.append("，" + model.getGranularity());
			modelString.append("，" + model.getPmParameterName());
			modelString.append("，" + model.getValue());
			modelString.append("，" + model.getDescription());
			modelString.append("，" + model.getVendorProbableCause());

			fileWriter.write(modelString.toString());
			fileWriter.write(lineSeparator);
				
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			throw new CommonException(e, MessageCodeDefine.CORBA_IO_EXCEPTION);
		} finally {

		}
		return true;
	}
	
	/**
	 * @param filePath
	 * @param model
	 * @param append
	 * @return
	 * @throws CommonException
	 */
	public static boolean writeToTxtCurrentPmData_PtpList(String filePath,
			List<PmDataModel> modelList)
			throws CommonException {

		FileWriter fileWriter = null;
		
		try {
			File file = new File(filePath);
			// 创建父文件
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			//删除旧文件
			if(file.exists()){
				file.delete();
			}
			fileWriter = new FileWriter(file.getPath(), true);
			
			for (PmDataModel model : modelList) {

				StringBuffer modelString = new StringBuffer();
				
				modelString.append(" " + model.getDisplayEms());
				modelString.append(" " + model.getDisplayNe());
				modelString.append(" " + model.getDisplayPortDesc());
				modelString.append(" " + model.getTpNameString());
				modelString.append(" " + "【"+new Date()+"】");
				fileWriter.write(modelString.toString());
				//分隔
				fileWriter.write(lineSeparator);
				for(PmMeasurementModel pmMeasurementModel:model.getPmMeasurementList()){
					modelString = new StringBuffer();
					modelString.append("              " + pmMeasurementModel.getPmParameterName());
					modelString.append("              " + pmMeasurementModel.getPmStdIndex());
					modelString.append("              " + pmMeasurementModel.getPmdescription());
					modelString.append("              " + pmMeasurementModel.getValue());
					modelString.append("              " + pmMeasurementModel.getPmCompareValue());
					fileWriter.write(modelString.toString());
					//分隔
					fileWriter.write(lineSeparator);
				}
			}
				
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			throw new CommonException(e, MessageCodeDefine.CORBA_IO_EXCEPTION);
		} finally {

		}
		return true;
	}
	
	/**
	 * @param filePath
	 * @param model
	 * @param append
	 * @return
	 * @throws CommonException
	 */
	public static boolean writeToTxtSdhCtp(String filePath,
			List<TerminationPointModel> modelList, int type)
			throws CommonException {

		FileWriter fileWriter = null;

		SimpleDateFormat fomat = CommonUtil
				.getDateFormatter(DataCollectDefine.COMMON_FORMAT);
		try {
			File file = new File(filePath);
			// 创建父文件
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			fileWriter = new FileWriter(file.getPath(), true);

			// 取得j k l m 参数
			String[] ctpParamter = null;

			String ctp64c = null;
			String ctp16c = null;
			String ctp8c = null;
			String ctp4c = null;

			String ctpJ = null;
			String ctpK = null;
			String ctpL = null;
			String ctpM = null;
			// 连接速率
			String connectRate = null;
			// j原值
			String ctpJOriginal = null;

			String displayName = null;

			for (TerminationPointModel model : modelList) {

				switch (type) {
				case DataCollectDefine.NMS_TYPE_T2000_FLAG:
				case DataCollectDefine.NMS_TYPE_U2000_FLAG:
				case DataCollectDefine.NMS_TYPE_E300_FLAG:
				case DataCollectDefine.NMS_TYPE_U31_FLAG:
				case DataCollectDefine.NMS_TYPE_OTNM2000_FLAG:
					// 取得j k l m 参数
					ctpParamter = getSdhCtpParameterFromCtpName(
							model.getName()[3].value).split("-");

					ctp64c = ctpParamter[0];
					ctp16c = ctpParamter[1];
					ctp8c = ctpParamter[2];
					ctp4c = ctpParamter[3];

					ctpJ = ctpParamter[4];
					ctpK = ctpParamter[5];
					ctpL = ctpParamter[6];
					ctpM = ctpParamter[7];
					// 连接速率
					connectRate = ctpParamter[8];
					// j原值
					ctpJOriginal = ctpParamter[9];

					break;
				//朗讯暂不解析jklm
				case DataCollectDefine.NMS_TYPE_LUCENT_OMS_FLAG:
					break;
				case DataCollectDefine.NMS_TYPE_ALU_FLAG:
					
					String ctpParamterString = getSdhCtpParameterFromCtpName_ALU(
							model.getName()[3].value,model.getName()[2].value,model.getLayerRateString());
					
					if(ctpParamterString == null){
						continue;
					}else{
						// 取得j k l m 参数
						ctpParamter = ctpParamterString.split("-");
					}
					ctp64c = ctpParamter[0];
					ctp16c = ctpParamter[1];
					ctp8c = ctpParamter[2];
					ctp4c = ctpParamter[3];

					ctpJ = ctpParamter[4];
					ctpK = ctpParamter[5];
					ctpL = ctpParamter[6];
					ctpM = ctpParamter[7];
					// 连接速率
					connectRate = ctpParamter[8];
					// j原值
					ctpJOriginal = ctpParamter[9];
					break;
				default:
				}

				if (model.getNativeEMSName() == null
						|| model.getNativeEMSName().isEmpty()) {
					displayName = ctpJOriginal + "-" + ctpK + "-" + ctpL + "-"
							+ ctpM;
				} else {
					displayName = ctpJOriginal + "-" + ctpK + "-" + ctpL + "-"
							+ ctpM + "(" + model.getNativeEMSName() + ")";
				}

				StringBuffer modelString = new StringBuffer();
				// BASE_SDH_CTP_ID
				modelString.append("" + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// BASE_EMS_CONNECTION_ID
				modelString.append(model.getEmsConnectionId() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// BASE_NE_ID
				modelString.append(model.getNeId() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// BASE_PTP_ID
				modelString.append(model.getPtpId() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				//REL_PTP_TYPE
				modelString.append(model.getRelPtpType() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// NAME
				modelString.append(model.getNameString() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// USER_LABEL
				modelString.append(model.getUserLabel() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// NATIVE_EMS_NAME
				modelString.append(model.getNativeEMSName() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// DISPLAY_NAME
				modelString.append(displayName + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// OWNER
				modelString.append(model.getOwner() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// CONNECTION_STATE
				modelString.append(model.getConnectionState() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// TP_MAPPING_MODE
				modelString.append(model.getTpMappingMode() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// DIRECTION
				modelString.append(model.getDirection() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// TP_PROTECTION_ASSOCIATION
				modelString.append(model.getTpProtectionAssociation() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// EDGE_POINT
				modelString
						.append(model.isEdgePoint() ? DataCollectDefine.SUCCESS
								: DataCollectDefine.FALSE + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// TOP_CTP
				modelString.append("\\N" + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// CTP_VALUE
				modelString.append(model.getCtpValue() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// CTP_64C
				modelString.append(ctp64c + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// CTP_16C
				modelString.append(ctp16c + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// CTP_8C
				modelString.append(ctp8c + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// CTP_4C
				modelString.append(ctp4c + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// CTP_J_ORIGINAL
				modelString.append(ctpJOriginal + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// CTP_J
				modelString.append(ctpJ + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// CTP_K
				modelString.append(ctpK + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// CTP_L
				modelString.append(ctpL + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// CTP_M
				modelString.append(ctpM + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// CONNECTION_TYPE
				modelString.append("\\N" + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// CONNECT_RATE
				modelString.append(connectRate + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				//LAYER_RATE
				modelString.append(model.getLayerRateString() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				//IS_ETH
				modelString.append(DataCollectDefine.FALSE + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// IS_SEPARATE
				modelString.append(DataCollectDefine.FALSE + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// IS_DEL
				modelString.append(DataCollectDefine.FALSE + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// CREATE_TIME
				modelString.append(fomat.format(new Date()) + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// UPDATE_TIME
				modelString.append(fomat.format(new Date()));

				fileWriter.write(modelString.toString());
				fileWriter.write(lineSeparator);
			}
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			throw new CommonException(e, MessageCodeDefine.CORBA_IO_EXCEPTION);
		} finally {

		}
		return true;
	}

	/**
	 * @param filePath
	 * @param model
	 * @param append
	 * @return
	 * @throws CommonException
	 */
	public static boolean writeToTxtSdhCtp(String filePath,
			List<Map> sdhCtps)
			throws CommonException {

		FileWriter fileWriter = null;

		SimpleDateFormat fomat = CommonUtil
				.getDateFormatter(DataCollectDefine.COMMON_FORMAT);
		try {
			File file = new File(filePath);
			// 创建父文件
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			fileWriter = new FileWriter(file.getPath(), true);


			for (Map ctp : sdhCtps) {

				StringBuffer modelString = new StringBuffer();
				// BASE_SDH_CTP_ID
				modelString.append("" + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// BASE_EMS_CONNECTION_ID
				modelString.append(ctp.get("BASE_EMS_CONNECTION_ID") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// BASE_NE_ID
				modelString.append(ctp.get("BASE_NE_ID") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// BASE_PTP_ID
				modelString.append(ctp.get("BASE_PTP_ID") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// REL_PTP_TYPE
				modelString.append(ctp.get("REL_PTP_TYPE") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// NAME
				modelString.append(ctp.get("NAME") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// USER_LABEL
				modelString.append(ctp.get("USER_LABEL") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// NATIVE_EMS_NAME
				modelString.append(ctp.get("NATIVE_EMS_NAME") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// DISPLAY_NAME
				modelString.append(ctp.get("DISPLAY_NAME") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// OWNER
				modelString.append(ctp.get("OWNER") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// CONNECTION_STATE
				modelString.append(ctp.get("CONNECTION_STATE") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// TP_MAPPING_MODE
				modelString.append(ctp.get("TP_MAPPING_MODE") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// DIRECTION
				modelString.append(ctp.get("DIRECTION") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// TP_PROTECTION_ASSOCIATION
				modelString.append(ctp.get("TP_PROTECTION_ASSOCIATION") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// EDGE_POINT
				modelString.append(ctp.get("EDGE_POINT") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// TOP_CTP
				modelString.append("\\N" + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// CTP_VALUE
				modelString.append(ctp.get("CTP_VALUE") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// CTP_64C
				modelString.append(ctp.get("CTP_64C") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// CTP_16C
				modelString.append(ctp.get("CTP_16C") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// CTP_8C
				modelString.append(ctp.get("CTP_8C") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// CTP_4C
				modelString.append(ctp.get("CTP_4C") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// CTP_J_ORIGINAL
				modelString.append(ctp.get("CTP_J_ORIGINAL") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// CTP_J
				modelString.append(ctp.get("CTP_J") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// CTP_K
				modelString.append(ctp.get("CTP_K") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// CTP_L
				modelString.append(ctp.get("CTP_L") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// CTP_M
				modelString.append(ctp.get("CTP_M") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// CONNECTION_TYPE
				modelString.append("\\N" + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// CONNECT_RATE
				modelString.append(ctp.get("CONNECT_RATE") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				//LAYER_RATE
				modelString.append(ctp.get("LAYER_RATE") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				//IS_ETH
				modelString.append(DataCollectDefine.FALSE + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// IS_SEPARATE
				modelString.append(DataCollectDefine.FALSE + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// IS_DEL
				modelString.append(DataCollectDefine.FALSE + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// CREATE_TIME
				modelString.append(fomat.format(new Date()) + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// UPDATE_TIME
				modelString.append(fomat.format(new Date()));

				fileWriter.write(modelString.toString());
				fileWriter.write(lineSeparator);
			}
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			throw new CommonException(e, MessageCodeDefine.CORBA_IO_EXCEPTION);
		} finally {

		}
		return true;
	}

	/**
	 * @param filePath
	 * @param model
	 * @param append
	 * @return
	 * @throws CommonException
	 */
	public static boolean writeToTxtOtnCtp(String filePath,
			List<TerminationPointModel> modelList)
			throws CommonException {

		FileWriter fileWriter = null;

		SimpleDateFormat fomat = CommonUtil
				.getDateFormatter(DataCollectDefine.COMMON_FORMAT);
		try {
			File file = new File(filePath);
			// 创建父文件
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			fileWriter = new FileWriter(file.getPath(), true);

			for (TerminationPointModel model : modelList) {

				StringBuffer modelString = new StringBuffer();
				// BASE_OTN_CTP_ID
				modelString.append("" + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// BASE_EMS_CONNECTION_ID
				modelString.append(model.getEmsConnectionId() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// BASE_NE_ID
				modelString.append(model.getNeId() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// BASE_PTP_ID
				modelString.append(model.getPtpId() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// NAME
				modelString.append(model.getNameString() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// USER_LABEL
				modelString.append(model.getUserLabel() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// NATIVE_EMS_NAME
				modelString.append(model.getNativeEMSName() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// DISPLAY_NAME
				modelString.append(model.getDisplayName() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// OWNER
				modelString.append(model.getOwner() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// CONNECTION_STATE
				modelString.append(model.getConnectionState() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// TP_MAPPING_MODE
				modelString.append(model.getTpMappingMode() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// DIRECTION
				modelString.append(model.getDirection() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// TP_PROTECTION_ASSOCIATION
				modelString.append(model.getTpProtectionAssociation() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// EDGE_POINT
				modelString
						.append(model.isEdgePoint() ? DataCollectDefine.SUCCESS
								: DataCollectDefine.FALSE + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// CTP_VALUE
				modelString.append(model.getCtpValue()+DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				//IS_CTP
				int isCtp = DataCollectDefine.TRUE;
				if (model.getName().length > 3) {
					
				} else {
					isCtp = DataCollectDefine.FALSE;
				}
				modelString.append(isCtp + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// IS_DEL
				modelString.append(DataCollectDefine.FALSE + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// CREATE_TIME
				modelString.append(fomat.format(new Date()) + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// UPDATE_TIME
				modelString.append(fomat.format(new Date()));

				fileWriter.write(modelString.toString());
				fileWriter.write(lineSeparator);
			}
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			throw new CommonException(e, MessageCodeDefine.CORBA_IO_EXCEPTION);
		} finally {

		}
		return true;
	}
	
	/**
	 * @param filePath
	 * @param model
	 * @param append
	 * @return
	 * @throws CommonException
	 */
	public static boolean writeToTxtPtnCtp(String filePath,
			List<TerminationPointModel> modelList)
			throws CommonException {

		FileWriter fileWriter = null;

		SimpleDateFormat fomat = CommonUtil
				.getDateFormatter(DataCollectDefine.COMMON_FORMAT);
		try {
			File file = new File(filePath);
			// 创建父文件
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			fileWriter = new FileWriter(file.getPath(), true);

			for (TerminationPointModel model : modelList) {

				StringBuffer modelString = new StringBuffer();
				// BASE_PTN_CTP
				modelString.append("" + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// BASE_EMS_CONNECTION_ID
				modelString.append(model.getEmsConnectionId() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// BASE_NE_ID
				modelString.append(model.getNeId() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// BASE_PTP_ID
				modelString.append(model.getPtpId() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// NAME
				modelString.append(model.getNameString() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// USER_LABEL
				modelString.append(model.getUserLabel() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// NATIVE_EMS_NAME
				modelString.append(model.getNativeEMSName() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// DISPLAY_NAME
				modelString.append(model.getDisplayName() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// OWNER
				modelString.append(model.getOwner() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// LAYER_RATE
				modelString.append(model.getLayerRateString() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// TUNNEL_ID
				modelString.append(model.getPTNTP_TUNNELId() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// PW_ID
				modelString.append(model.getPTNTP_PWId() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// PW_TYPE
				modelString.append(model.getPTNTP_PWType() + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// LSP_TYPE
				modelString.append(""+DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// SRC_IN_LABEL
				modelString.append(model.getSrcInLabel()+DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// SRC_OUT_LABEL
				modelString.append(model.getSrcOutLabel()+DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// DEST_IN_LABEL
				modelString.append(""+DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// DEST_OUT_LABEL
				modelString.append(""+DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// SRC_IP
				modelString.append(""+DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// DEST_IP
				modelString.append(""+DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// BELONGED_TRAIL
				modelString.append(""+DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// IS_DEL
				modelString.append(DataCollectDefine.FALSE + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// CREATE_TIME
				modelString.append(fomat.format(new Date()) + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// UPDATE_TIME
				modelString.append(fomat.format(new Date()));

				fileWriter.write(modelString.toString());
				fileWriter.write(lineSeparator);
			}
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			throw new CommonException(e, MessageCodeDefine.CORBA_IO_EXCEPTION);
		} finally {

		}
		return true;
	}

	
	/**
	 * @param filePath
	 * @param model
	 * @param append
	 * @return
	 * @throws CommonException
	 */
	public static boolean writeToTxtOtnCtpParam(String filePath,
			List<LayeredParametersModel> modelList,int ptpId,String ctpValue,int type)
			throws CommonException {

		FileWriter fileWriter = null;

		try {
			File file = new File(filePath);
			// 创建父文件
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			fileWriter = new FileWriter(file.getPath(), true);
			
			Map layeredParametersMap = null;

			for (LayeredParametersModel model : modelList) {
				
				layeredParametersMap = layeredParametersModelToMap(model,type);

				StringBuffer modelString = new StringBuffer();
				// BASE_OTN_CTP_PARAM_ID
				modelString.append("" + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// BASE_PTP_ID
				modelString.append(ptpId + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// CTP_NAME
				modelString.append(ctpValue + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// TRANS_LAYER
				modelString.append(layeredParametersMap.get("TRANS_LAYER") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// TRANS_CLIENT_RATE
				modelString.append(layeredParametersMap.get("TRANS_CLIENT_RATE") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// TRANS_MAX_CLIENT_RATE
				modelString.append(layeredParametersMap.get("TRANS_MAX_CLIENT_RATE") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// TRANS_MIN_CLIENT_RATE
				modelString.append(layeredParametersMap.get("TRANS_MIN_CLIENT_RATE") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// TRANS_CLIENT_TYPE
				modelString.append(layeredParametersMap.get("TRANS_CLIENT_TYPE") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// TRANS_FREQUENCY
				modelString.append(layeredParametersMap.get("TRANS_FREQUENCY") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// TRANS_FREQUENCY_LIST
				modelString.append(layeredParametersMap.get("TRANS_FREQUENCY_LIST") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// TRANS_REG_TYPE
				modelString.append(layeredParametersMap.get("TRANS_REG_TYPE") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// TRANS_MAX_NUMBER_OCH
				modelString.append(layeredParametersMap.get("TRANS_MAX_NUMBER_OCH") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
				// TRANS_FREQUENCY_SPACING
				modelString.append(layeredParametersMap.get("TRANS_FREQUENCY_SPACING") + DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);

				fileWriter.write(modelString.toString());
				fileWriter.write(lineSeparator);
			}
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			throw new CommonException(e, MessageCodeDefine.CORBA_IO_EXCEPTION);
		} finally {

		}
		return true;
	}
	
	/**
	 * @param model
	 * @param type
	 * @return
	 */
	private static Map layeredParametersModelToMap(LayeredParametersModel model,int type) {

		Map map = new HashMap();

		map.put("TRANS_LAYER", model.getLayer());
		map.put("TRANS_CLIENT_RATE", "");
		map.put("TRANS_MAX_CLIENT_RATE", "");
		map.put("TRANS_MIN_CLIENT_RATE", "");
		map.put("TRANS_CLIENT_TYPE", "");
		map.put("TRANS_FREQUENCY", "");
		map.put("TRANS_FREQUENCY_LIST", "");
		map.put("TRANS_REG_TYPE", "");
		map.put("TRANS_MAX_NUMBER_OCH", "");
		map.put("TRANS_FREQUENCY_SPACING", "");
		switch (type) {
		case DataCollectDefine.NMS_TYPE_T2000_FLAG:
		case DataCollectDefine.NMS_TYPE_U2000_FLAG:
			for (NameAndStringValue_T name : model.getTransmissionParams()) {
				if (name.name.equals("ClientRate")) {
					map.put("TRANS_CLIENT_RATE", name.value);
				} else if (name.name.equals("MaxClientRate")) {
					map.put("TRANS_MAX_CLIENT_RATE", name.value);
				} else if (name.name.equals("MinClientRate")) {
					map.put("TRANS_MIN_CLIENT_RATE", name.value);
				} else if (name.name.equals("ClientType")) {
					map.put("TRANS_CLIENT_TYPE", name.value);
				} else if (name.name.equals("Frequency")) {
					map.put("TRANS_FREQUENCY", name.value);
				} else if (name.name.equals("FrequencyList")) {
					map.put("TRANS_FREQUENCY_LIST", name.value);
				} else if (name.name.equals("RegType")) {
					map.put("TRANS_REG_TYPE", name.value);
				} else if (name.name.equals("MaxNumberOCh")) {
					map.put("TRANS_MAX_NUMBER_OCH", name.value);
				} else if (name.name.equals("FrequencySpacing")) {
					map.put("TRANS_FREQUENCY_SPACING", name.value);
				}
			}
			break;
		case DataCollectDefine.NMS_TYPE_E300_FLAG:
		case DataCollectDefine.NMS_TYPE_U31_FLAG:
			break;
		case DataCollectDefine.NMS_TYPE_LUCENT_OMS_FLAG:
			break;
		case DataCollectDefine.NMS_TYPE_OTNM2000_FLAG:
			for (NameAndStringValue_T name : model.getTransmissionParams()) {
				if (name.name.equals("ClientLayerRate")) {
					map.put("TRANS_CLIENT_RATE", name.value);
				} else if (name.name.equals("MaxClientRate")) {
					map.put("TRANS_MAX_CLIENT_RATE", name.value);
				} else if (name.name.equals("MinClientRate")) {
					map.put("TRANS_MIN_CLIENT_RATE", name.value);
				} else if (name.name.equals("ClientType")) {
					map.put("TRANS_CLIENT_TYPE", name.value);
				} else if (name.name.endsWith("Frequency")) {
					//TunableBaseFrequency,TunedFrequency
					map.put("TRANS_FREQUENCY", name.value);
				} else if (name.name.equals("RegeneratorType")) {
					map.put("TRANS_REG_TYPE", name.value);
				} else if (name.name.endsWith("FrequencySpacing")) {
					//TunableFrequencySpacing,OscFrequencySpacing
					map.put("TRANS_FREQUENCY_SPACING", name.value);
				}
			}
			break;
		default:
		}
		return map;
	}

	// private static Map<String, String> getOtnCtpParameter(
	// List<LayeredParametersModel> transmissionParams) {
	// return null;
	// }

	// 获取sdh ctp参数
	public static String getSdhCtpParameterFromCtpName(String ctpValue) {
		// NameAndStringValue_T tempName1 = ctpName[ctpName.length - 1];
		// String ctpValue = tempName1.value;
		String spilt = "-";

		String connectRate = "";

		String sdh_cacade_4c = "sts12c_vc4_4c";
		String sdh_cacade_8c = "sts24c_vc4_8c";
		String sdh_cacade_16c = "sts48c_vc4_16c";
		String sdh_cacade_64c = "sts192c_vc4_64c";

		String PDH_E4 = "sts3c_au4";
		String PDH_E3 = "tu3_vc3";
		String PDH_E1 = "vt2_tu12";

		String jParamter = "-j=";
		String kParamter = "-k=";
		String lParamter = "-l=";
		String mParamter = "-m=";

		int sdh_64c = 0;
		int sdh_16c = 0;
		int sdh_8c = 0;
		int sdh_4c = 0;

		int jOrigin = 0;

		int j = 0;
		int k = 0;
		int l = 0;
		int m = 0;

		// CTP是SDH结构
		if (ctpValue.contains(jParamter) || ctpValue.contains(kParamter)
				|| ctpValue.contains(lParamter) || ctpValue.contains(mParamter)) {

			String[] temp = ctpValue.split("=");
			// CTP是SDH结构,VC3级别：“/sts3c_au4-j=[1..n]/tu3_vc3-k=[1..3]”n在[64,16,4,1]中取值；
			if (temp.length == 3) {
				// j原值 可能有三位
				String jValue_1 = String.valueOf(ctpValue.charAt(ctpValue
						.lastIndexOf(jParamter) + 3));
				String jValue_2 = String.valueOf(ctpValue.charAt(ctpValue
						.lastIndexOf(jParamter) + 4));
				String jValue_3 = String.valueOf(ctpValue.charAt(ctpValue
						.lastIndexOf(jParamter) + 5));
				try {
					Integer.valueOf(jValue_3);
					j = Integer.valueOf(jValue_1 + jValue_2 + jValue_3);
				} catch (Exception e) {
					try {
						Integer.valueOf(jValue_2);
						j = Integer.valueOf(jValue_1 + jValue_2);
					} catch (Exception e1) {
						j = Integer.valueOf(jValue_1);
					}
				}

				// k原值
				String kValue = String.valueOf(ctpValue.charAt(ctpValue
						.lastIndexOf(kParamter) + 3));
				k = Integer.valueOf(kValue);

				l = 0;
				m = 0;

				// 64c
				sdh_64c = quotient(j, 64);
				// 16c
				sdh_16c = modAndQuotient(j, 64, 16);
				// 8c
				sdh_8c = modAndQuotient(j, 16, 8);
				// 4c
				sdh_4c = modAndQuotient(j, 8, 4);
				// j原始值
				jOrigin = j;
				// j计算值
				j = mod(j, 4);

				connectRate = DataCollectDefine.COMMON.CONNECT_RATE_VC3;
			}

			// 此段代码与文档描述不符，但现场采集数据位此种形式，参照华为AU4级别处理--start
			// CTP是SDH结构,AU4级别：sts3c_au4-j=4
			if (temp.length == 2) {
				// j原值 可能有三位
				String jValue_1 = String.valueOf(ctpValue.charAt(ctpValue
						.lastIndexOf(jParamter) + 3));
				try {
					String jValue_2 = String.valueOf(ctpValue.charAt(ctpValue
							.lastIndexOf(jParamter) + 4));
					String jValue_3 = String.valueOf(ctpValue.charAt(ctpValue
							.lastIndexOf(jParamter) + 5));
					Integer.valueOf(jValue_3);
					j = Integer.valueOf(jValue_1 + jValue_2 + jValue_3);
				} catch (Exception e) {
					try {
						String jValue_2 = String.valueOf(ctpValue
								.charAt(ctpValue.lastIndexOf(jParamter) + 4));
						Integer.valueOf(jValue_2);
						j = Integer.valueOf(jValue_1 + jValue_2);
					} catch (Exception e1) {
						j = Integer.valueOf(jValue_1);
					}
				}

				k = 0;
				l = 0;
				m = 0;

				// 64c
				sdh_64c = quotient(j, 64);
				// 16c
				sdh_16c = modAndQuotient(j, 64, 16);
				// 8c
				sdh_8c = modAndQuotient(j, 16, 8);
				// 4c
				sdh_4c = modAndQuotient(j, 8, 4);
				// j原始值
				jOrigin = j;
				// j计算值
				j = mod(j, 4);

				connectRate = DataCollectDefine.COMMON.CONNECT_RATE_VC4;
			}
			// 参照华为AU4处理--end

			// CTP是SDH结构,AU4级别：/direction=src
			// /sts1_au3-j=[1..n]-k=[1..3]”n在[64,16,4,1]中取值；
			if (temp.length == 3 && ctpValue.contains("direction")) {
				// j原值 可能有三位
				String jValue_1 = String.valueOf(ctpValue.charAt(ctpValue
						.lastIndexOf(jParamter) + 3));
				try {
					String jValue_2 = String.valueOf(ctpValue.charAt(ctpValue
							.lastIndexOf(jParamter) + 4));
					String jValue_3 = String.valueOf(ctpValue.charAt(ctpValue
							.lastIndexOf(jParamter) + 5));
					Integer.valueOf(jValue_3);
					j = Integer.valueOf(jValue_1 + jValue_2 + jValue_3);
				} catch (Exception e) {
					try {
						String jValue_2 = String.valueOf(ctpValue
								.charAt(ctpValue.lastIndexOf(jParamter) + 4));
						Integer.valueOf(jValue_2);
						j = Integer.valueOf(jValue_1 + jValue_2);
					} catch (Exception e1) {
						j = Integer.valueOf(jValue_1);
					}
				}

				k = 0;
				l = 0;
				m = 0;

				// 64c
				sdh_64c = quotient(j, 64);
				// 16c
				sdh_16c = modAndQuotient(j, 64, 16);
				// 8c
				sdh_8c = modAndQuotient(j, 16, 8);
				// 4c
				sdh_4c = modAndQuotient(j, 8, 4);
				// j原始值
				jOrigin = j;
				// j计算值
				j = mod(j, 4);

				connectRate = DataCollectDefine.COMMON.CONNECT_RATE_VC4;
			}
			// CTP是SDH结构,TU12级别：sts3c_au4-j=60/vt2_tu12-k=3-l=2-m=1
			if (temp.length == 5) {
				// j原值 可能有三位
				String jValue_1 = String.valueOf(ctpValue.charAt(ctpValue
						.lastIndexOf(jParamter) + 3));
				String jValue_2 = String.valueOf(ctpValue.charAt(ctpValue
						.lastIndexOf(jParamter) + 4));
				String jValue_3 = String.valueOf(ctpValue.charAt(ctpValue
						.lastIndexOf(jParamter) + 5));
				try {
					Integer.valueOf(jValue_3);
					j = Integer.valueOf(jValue_1 + jValue_2 + jValue_3);
				} catch (Exception e) {
					try {
						Integer.valueOf(jValue_2);
						j = Integer.valueOf(jValue_1 + jValue_2);
					} catch (Exception e1) {
						j = Integer.valueOf(jValue_1);
					}
				}
				// k原值
				String kValue = String.valueOf(ctpValue.charAt(ctpValue
						.lastIndexOf(kParamter) + 3));
				k = Integer.valueOf(kValue);
				// l原值
				String lValue = String.valueOf(ctpValue.charAt(ctpValue
						.lastIndexOf(lParamter) + 3));
				l = Integer.valueOf(lValue);
				// m原值
				String mValue = String.valueOf(ctpValue.charAt(ctpValue
						.lastIndexOf(mParamter) + 3));
				m = Integer.valueOf(mValue);
				// 64c
				sdh_64c = quotient(j, 64);
				// 16c
				sdh_16c = modAndQuotient(j, 64, 16);
				// 8c
				sdh_8c = modAndQuotient(j, 16, 8);
				// 4c
				sdh_4c = modAndQuotient(j, 8, 4);
				// j原始值
				jOrigin = j;
				// j计算值
				j = mod(j, 4);

				connectRate = DataCollectDefine.COMMON.CONNECT_RATE_VC12;
			}
		}
		// CTP是SDH 级联结构
		else if (ctpValue.contains(sdh_cacade_4c)
				|| ctpValue.contains(sdh_cacade_8c)
				|| ctpValue.contains(sdh_cacade_16c)
				|| ctpValue.contains(sdh_cacade_64c)) {

			// 连接速率4C
			if (ctpValue.contains(sdh_cacade_4c)) {
				j = Integer.valueOf(ctpValue.split("=")[1]);

				k = 0;
				l = 0;
				m = 0;

				// 64c
				sdh_64c = quotient(j, 64);
				// 16c
				sdh_16c = modAndQuotient(j, 64, 16);
				// 8c
				sdh_8c = modAndQuotient(j, 16, 8);
				// 4c
				sdh_4c = modAndQuotient(j, 8, 4);
				// j原始值
				jOrigin = j;
				// j计算值
				j = 0;

				connectRate = DataCollectDefine.COMMON.CONNECT_RATE_VC4_4C;
			}

			// 连接速率8C
			if (ctpValue.contains(sdh_cacade_8c)) {
				j = Integer.valueOf(ctpValue.split("=")[1]);

				k = 0;
				l = 0;
				m = 0;

				// 64c
				sdh_64c = quotient(j, 64);
				// 16c
				sdh_16c = modAndQuotient(j, 64, 16);
				// 8c
				sdh_8c = modAndQuotient(j, 16, 8);
				// 4c
				sdh_4c = 0;
				// j原始值
				jOrigin = j;
				// j计算值
				j = 0;

				connectRate = DataCollectDefine.COMMON.CONNECT_RATE_VC4_8C;
			}

			// 连接速率16C
			if (ctpValue.contains(sdh_cacade_16c)) {
				j = Integer.valueOf(ctpValue.split("=")[1]);

				k = 0;
				l = 0;
				m = 0;

				// 64c
				sdh_64c = quotient(j, 64);
				// 16c
				sdh_16c = modAndQuotient(j, 64, 16);
				// 8c
				sdh_8c = 0;
				// 4c
				sdh_4c = 0;
				// j原始值
				jOrigin = j;
				// j计算值
				j = 0;

				connectRate = DataCollectDefine.COMMON.CONNECT_RATE_VC4_16C;
			}

			// 连接速率64C
			if (ctpValue.contains(sdh_cacade_64c)) {
				j = Integer.valueOf(ctpValue.split("=")[1]);

				k = 0;
				l = 0;
				m = 0;

				// 64c
				sdh_64c = quotient(j, 64);
				// 16c
				sdh_16c = 0;
				// 8c
				sdh_8c = 0;
				// 4c
				sdh_4c = 0;
				// j原始值
				jOrigin = j;
				// j计算值
				j = 0;

				connectRate = DataCollectDefine.COMMON.CONNECT_RATE_VC4_64C;
			}
		}
		// CTP是PDH结构
		else if (ctpValue.contains(PDH_E4) || ctpValue.contains(PDH_E3)
				|| ctpValue.contains(PDH_E1)) {

			// 连接速率AU4
			if (ctpValue.contains(PDH_E4)) {

				if(ctpValue.split(PDH_E4+"=").length>1){
					
					k=0;
					l=0;
					m=0;

					int jOriginal = Integer.valueOf(ctpValue.split(PDH_E4+"=")[1]);
					// 64c
					sdh_64c = modAndQuotient(jOriginal, 1*4*2*2*4,4*2*2*4);
					// 16c
					sdh_16c = modAndQuotient(jOriginal, 4*2*2*4,2*2*4);
					// 8c
					sdh_8c = modAndQuotient(jOriginal, 2*2*4,2*4);
					// 4c
					sdh_4c = modAndQuotient(jOriginal, 2*4,4);
					
					j = mod(jOriginal,4);
					
//					k = mod(kOriginal,3);
					
//					l = modAndQuotient(mOriginal,7*3,3);
//					
//					m = mod(mOriginal,3);
					jOrigin=(sdh_64c-1)*64+(sdh_16c-1)*16+(sdh_8c-1)*8+(sdh_4c-1)*4+j;
				}

				connectRate = DataCollectDefine.COMMON.CONNECT_RATE_VC4;
			}
			// 连接速率VC3
			if (ctpValue.contains(PDH_E3)) {
				
				if(ctpValue.split(PDH_E3+"=").length>1){
					
					l=0;
					m=0;
					
					int kOriginal = Integer.valueOf(ctpValue.split(PDH_E3+"=")[1]);
					// 64c
					sdh_64c = modAndQuotient(kOriginal, 1*4*2*2*4*3,4*2*2*4*3);
					// 16c
					sdh_16c = modAndQuotient(kOriginal, 4*2*2*4*3,2*2*4*3);
					// 8c
					sdh_8c = modAndQuotient(kOriginal, 2*2*4*3,2*4*3);
					// 4c
					sdh_4c = modAndQuotient(kOriginal, 2*4*3,4*3);
					
					j = modAndQuotient(kOriginal,4*3,3);
					
					k = mod(kOriginal,3);
					
//					l = modAndQuotient(mOriginal,7*3,3);
//					
//					m = mod(mOriginal,3);
					jOrigin=(sdh_64c-1)*64+(sdh_16c-1)*16+(sdh_8c-1)*8+(sdh_4c-1)*4+j;
				}
				
				connectRate = DataCollectDefine.COMMON.CONNECT_RATE_VC3;
			}
			// 连接速率TU12
			if (ctpValue.contains(PDH_E1)) {
				if(ctpValue.split(PDH_E1+"=").length>1){
					int mOriginal = Integer.valueOf(ctpValue.split(PDH_E1+"=")[1]);
					// 64c
					sdh_64c = modAndQuotient(mOriginal, 1*4*2*2*4*3*7*3,4*2*2*4*3*7*3);
					// 16c
					sdh_16c = modAndQuotient(mOriginal, 4*2*2*4*3*7*3,2*2*4*3*7*3);
					// 8c
					sdh_8c = modAndQuotient(mOriginal, 2*2*4*3*7*3,2*4*3*7*3);
					// 4c
					sdh_4c = modAndQuotient(mOriginal, 2*4*3*7*3,4*3*7*3);
					
					j = modAndQuotient(mOriginal,4*3*7*3,3*7*3);
					
					k = modAndQuotient(mOriginal,3*7*3,7*3);
					
					l = modAndQuotient(mOriginal,7*3,3);
					
					m = mod(mOriginal,3);
					
					jOrigin=(sdh_64c-1)*64+(sdh_16c-1)*16+(sdh_8c-1)*8+(sdh_4c-1)*4+j;
				}
				
				connectRate = DataCollectDefine.COMMON.CONNECT_RATE_VC12;
			}
		}

		String result = sdh_64c + spilt + sdh_16c + spilt + sdh_8c + spilt
				+ sdh_4c + spilt + j + spilt + k + spilt + l + spilt + m
				+ spilt + connectRate + spilt + jOrigin;

		return result;
	}
	
	
	// 获取sdh ctp参数
	public static String getSdhCtpParameterFromCtpName_ALU(String ctpValue,String ptpValue,String layerRate) {
		// NameAndStringValue_T tempName1 = ctpName[ctpName.length - 1];
		// String ctpValue = tempName1.value;
		
		int sdh_64c = 0;
		int sdh_16c = 0;
		int sdh_8c = 0;
		int sdh_4c = 0;

		int jOrigin = 0;

		int j = 0;
		int k = 0;
		int l = 0;
		int m = 0;
		
		String spilt = "-";
		//
		String connectRate = "";
		//截出jklm值
		String paramter ="";
		if(ctpValue.split(ptpValue).length>1){
			paramter = ctpValue.split(ptpValue)[1].trim();
		}
		//CTP:HaiYuanJu3_EEEE/r01s1b31p001 U 不要
		//CTP:HaiYuanJu3_EEEE/r01s1b31p001 01 cap 不要
		if(paramter.contains("U")||paramter.contains("cap")){
			return null;
		}
		
		if(paramter.isEmpty()){
			//CTP:HaiYuanJu3_EEEE/r01s1b31p001 层速率非11不要 层速率11 填8个1
			if(layerRate!=null&&Integer.valueOf(layerRate) == 11){
				
				// 64c
				sdh_64c = 1;
				// 16c
				sdh_16c = 1;
				// 8c
				sdh_8c = 1;
				// 4c
				sdh_4c = 1;
				// j原始值
				jOrigin = 1;
				// j计算值
				j = 1;
				k = 1;
				l = 1;
				m=1;
				
			}else{
				return null;
			}
		}else{
			String[] temp = paramter.split("/");

			
			
			//CTP:HaiYuanJu3_EEEE/r01s1b31p001 U
			//CTP:HaiYuanJu3_EEEE/r01s1b37p001 01
			//CTP:HaiYuanJu3_EEEE/r01s1b31p001 01 cap
			//CTP:HaiYuanJu3_EEEE/r01s1b37p001 01/1/1.1
			try{
				j = Integer.valueOf(temp[0]);
			}catch(Exception e){
				
			}
			try{
				k = Integer.valueOf(temp[1]);
			}catch(Exception e){
				
			}
			try{
				l = Integer.valueOf(temp[2].split("\\.")[0]);
			}catch(Exception e){
				
			}
			try{
				m = Integer.valueOf(temp[2].split("\\.")[1]);
			}catch(Exception e){
				
			}
			// 64c
			sdh_64c = quotient(j, 64);
			// 16c
			sdh_16c = modAndQuotient(j, 64, 16);
			// 8c
			sdh_8c = modAndQuotient(j, 16, 8);
			// 4c
			sdh_4c = modAndQuotient(j, 8, 4);
			// j原始值
			jOrigin = j;
			// j计算值
			j = mod(j, 4);
		}
		//先用层速率判断
		if (layerRate != null && !layerRate.isEmpty()) {
			Integer layerRateNum = Integer.valueOf(layerRate);

			if (layerRateNum.intValue() == 11) {
				connectRate = "VC12";
			} else if (layerRateNum.intValue() == 13) {
				connectRate = "VC3";
			} else if (layerRateNum.intValue() == 15) {
				connectRate = "VC4";
			}
		}
		//如果还是为空使用jklm判断
		if (connectRate == null || connectRate.isEmpty()) {
			if(m>0){
				connectRate = "VC12";
			}else if(k>0&&l==0&&m==0){
				connectRate = "VC3";
			}else if(j>0&&k==0&&l==0&&m==0){
				connectRate = "VC4";
			}
		}
		String result = sdh_64c + spilt + sdh_16c + spilt + sdh_8c + spilt
				+ sdh_4c + spilt + j + spilt + k + spilt + l + spilt + m
				+ spilt + connectRate + spilt + jOrigin;

		return result;
	}

	private static int modAndQuotient(int souceValue, int modValue,
			int quotientValue) {

		int result = (int) ((Math.floor(((souceValue - 1) % modValue)
				/ quotientValue)) + 1);

		return result;
	}

	private static int mod(int souceValue, int modValue) {

		int result = (souceValue - 1) % modValue + 1;

		return result;
	}

	private static int quotient(int souceValue, int quotientValue) {

		int result = (int) ((Math.floor((souceValue - 1) / quotientValue)) + 1);

		return result;
	}

	public static void main(String args[]) throws CommonException {
		for(int i=1;i<266;i++){
			System.out.println(getSdhCtpParameterFromCtpName("/vt2_tu12="+i));
		}
		
	}

}
