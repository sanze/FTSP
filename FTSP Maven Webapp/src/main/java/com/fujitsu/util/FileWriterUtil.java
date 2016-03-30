package com.fujitsu.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.PmDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.PmMeasurementModel;

/**
 * @author 533
 * 
 */
public class FileWriterUtil {

	public final static String BASE_FILE_PATH = System.getProperty("user.dir")
			+ "/OutPutFiles/";

	private static String lineSeparator = "\n";//System.getProperty("line.separator");

	/**
	 * @param filePath
	 * @param model
	 * @param append
	 * @return
	 * @throws CommonException
	 */
	public static boolean writeToTxtPm(String filePath,
			List<PmDataModel> pmDataList) throws CommonException {

		OutputStreamWriter fileWriter = null;

		SimpleDateFormat fomat = CommonUtil
				.getDateFormatter(CommonDefine.COMMON_FORMAT);
		try {
			File file = new File(filePath);
			// 创建父文件
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			// fileWriter = new FileWriter(file.getPath(), true);
			fileWriter = new OutputStreamWriter(new FileOutputStream(file.getPath()),"UTF-8");
			for (PmDataModel model : pmDataList) {
				List<PmMeasurementModel> pmDetailList = model
						.getPmMeasurementList();
				for (PmMeasurementModel pmDetailModel : pmDetailList) {
					StringBuffer pmDetailString = new StringBuffer();
					// `ID`,
					pmDetailString.append("\\N" + ",");
					// `BASE_EMS_CONNECTION_ID`,
					pmDetailString.append(getTxtValue(model
							.getEmsConnectionId().toString()) + ",");
					// `BASE_NE_ID`,
					pmDetailString.append(getTxtValue(model.getNeId()) + ",");
					// `BASE_RACK_ID`,
					pmDetailString.append(getTxtValue(model.getRackId()) + ",");
					// `BASE_SHELF_ID`,
					pmDetailString.append(getTxtValue(model.getShelfId()) + ",");
					// `BASE_SLOT_ID`,
					pmDetailString.append(getTxtValue(model.getSlotId()) + ",");
					// `BASE_SUB_SLOT_ID`,
					pmDetailString.append(getTxtValue(model.getSubSlotId()) + ",");
					// `BASE_UNIT_ID`,
					pmDetailString.append(getTxtValue(model.getUnitId()) + ",");
					// `BASE_SUB_UNIT_ID`,
					pmDetailString.append(getTxtValue(model.getSubUnitId()) + ",");
					// `BASE_PTP_ID`,
					pmDetailString.append(getTxtValue(model.getPtpId()) + ",");
					// `BASE_OTN_CTP_ID`,
					pmDetailString.append(getTxtValue(model.getOtnCtpId()) + ",");
					// `BASE_SDH_CTP_ID`,
					pmDetailString.append(getTxtValue(model.getSdhCtpId()) + ",");
					// `TARGET_TYPE`,
					pmDetailString.append(getTxtValue(model.getTargetType()) + ",");
					// `LAYER_RATE`,
					pmDetailString.append(getTxtValue(model.getLayerRate()) + ",");
					// `PM_STD_INDEX`,
					pmDetailString.append(getTxtValue(pmDetailModel.getPmStdIndex()) + ",");
					// `PM_INDEX`,
					pmDetailString.append(getTxtValue(pmDetailModel.getPmParameterName()) + ",");
					// `PM_VALUE`,
					pmDetailString.append(getTxtValue(pmDetailModel.getValue())+ ",");
					// `PM_COMPARE_VALUE`,
					pmDetailString.append(getTxtValue(pmDetailModel.getPmCompareValue()) + ",");
					// `PM_COMPARE_VALUE_DISPLAY`,
					pmDetailString.append(getTxtValue(pmDetailModel.getDisplayCompareValue()) + ",");
					// `TYPE`,
					pmDetailString.append(getTxtValue(pmDetailModel.getType())+ ",");
					// `THRESHOLD_1`,
					pmDetailString.append(getTxtValue(pmDetailModel.getThreshold1()) + ",");
					// `THRESHOLD_2`,
					pmDetailString.append(getTxtValue(pmDetailModel.getThreshold2()) + ",");
					// `THRESHOLD_3`,
					pmDetailString.append(getTxtValue(pmDetailModel.getThreshold3()) + ",");
					// `FILTER_VALUE`,
					pmDetailString.append(getTxtValue(pmDetailModel.getFilterValue()) + ",");
					// `OFFSET`,
					pmDetailString.append(getTxtValue(pmDetailModel.getOffset()) + ",");
					// `UPPER_VALUE`,
					pmDetailString.append(getTxtValue(pmDetailModel.getUpperValue()) + ",");
					// `UPPER_OFFSET`,
					pmDetailString.append(getTxtValue(pmDetailModel.getUpperOffset()) + ",");
					// `LOWER_VALUE`,
					pmDetailString.append(getTxtValue(pmDetailModel.getLowerValue()) + ",");
					// `LOWER_OFFSET`,
					pmDetailString.append(getTxtValue(pmDetailModel.getLowerOffset()) + ",");
					// `PM_DESCRIPTION`,
					pmDetailString.append(getTxtValue(pmDetailModel.getPmdescription())
							+ ",");
					// `LOCATION`,
					pmDetailString
							.append(getTxtValue(pmDetailModel.getLocationFlag()) + ",");
					// `UNIT`,
					pmDetailString.append(getTxtValue(pmDetailModel.getUnit()) + ",");
					// `GRANULARITY`,
					pmDetailString.append(getTxtValue(model.getGranularityFlag()) + ",");
					// `EXCEPTION_LV`,
					pmDetailString.append(getTxtValue(pmDetailModel.getExceptionLv()) + ",");
					// `EXCEPTION_COUNT`,
					pmDetailString.append(getTxtValue(pmDetailModel.getExceptionCount())+ ",");
					// `RETRIEVAL_TIME`,---使用retrievalTimeDisplay字段，此字段经过UTC时间处理
					pmDetailString.append(getTxtValue(fomat.format(model.getRetrievalTimeDisplay())) + ",");
					// `DISPLAY_EMS_GROUP`,
					pmDetailString.append(getTxtValue(model.getDisplayEmsGroup()) + ",");
					// `DISPLAY_EMS`,
					pmDetailString.append(getTxtValue(model.getDisplayEms()) + ",");
					// `DISPLAY_SUBNET`,
					pmDetailString.append(getTxtValue(model.getDisplaySubnet()) + ",");
					// `DISPLAY_NE`,
					pmDetailString.append(getTxtValue(model.getDisplayNe()) + ",");
					// `DISPLAY_AREA`,
					pmDetailString.append(getTxtValue(model.getDisplayArea()) + ",");
					// `DISPLAY_STATION`,
					pmDetailString.append(getTxtValue(model.getDisplayStation()) + ",");
					// `DISPLAY_PRODUCT_NAME`,
					pmDetailString.append(getTxtValue(model.getDisplayProductName()) + ",");
					// `DISPLAY_PORT_DESC`,
					pmDetailString.append(getTxtValue(model.getDisplayPortDesc()) + ",");
					// `RATE`,
					pmDetailString.append(getTxtValue(model.getRate()) + ",");
					// `DISPLAY_CTP`,
					pmDetailString.append(getTxtValue(model.getDisplayCtp()) + ",");
					// `DISPLAY_TEMPLATE_NAME`,
					pmDetailString.append(getTxtValue(model.getDisplayTemplateName()) + ",");
					// `TEMPLATE_ID`,
					pmDetailString.append(getTxtValue(model.getPmTemplateId()) + ",");
					// `BASE_EMS_GROUP_ID`,
					pmDetailString.append(getTxtValue(model.getEmsGroupId()) + ",");
					// `BASE_SUBNET_ID`,
					pmDetailString.append(getTxtValue(model.getSubnetId()) + ",");
					// `DOMAIN`,
					pmDetailString.append(getTxtValue(model.getDomain()) + ",");
					// `PTP_TYPE`
					pmDetailString.append(getTxtValue(model.getPtpType())+ ",");
					// `COLLECT_TIME`
					pmDetailString.append(fomat.format(new Date()));

					fileWriter.write(pmDetailString.toString());
					fileWriter.write(lineSeparator);
				}
			}
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_UNKNOW);
		} finally {

		}
		return true;
	}

	private static String getTxtValue(Object in) {
		if (in == null) {
			return "\\N";
		} else {
			return in.toString();
		}
	}

}
