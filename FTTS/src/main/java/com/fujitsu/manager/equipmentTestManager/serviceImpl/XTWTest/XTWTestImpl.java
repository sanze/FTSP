package com.fujitsu.manager.equipmentTestManager.serviceImpl.XTWTest;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import com.fujitsu.activeMq.JMSSender;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.dao.mysql.PlanMapper;
import com.fujitsu.manager.equipmentTestManager.service.DeviceTest;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.XTWTest.base.RTU;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.EqptInfoModel;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.LightPathModel;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.RTUAlarm;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.RTUConfiguration;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.RoutePointInfoModel;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.SysInfoModel;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.TestParaInfoModel;
import com.fujitsu.util.CommonUtil;

public class XTWTestImpl extends DeviceTest {

	private String preCommand = "__________________________________________________________________________________________________________________________________________________________________________________________________________________";
	private RTU rtu = new RTU();
	private static String TEMP_FILE_PATH = System.getProperty("java.io.tmpdir");
	private static String TEMP_FILE_NAME = "resultTemp.tsv";
	private static String TEMP_TEST_RESULT_FILE_NAME = "test_result.dat";
	@Resource
	private PlanMapper planMapper;

	@SuppressWarnings("unchecked")
	private void setLightPath(String rtuIp, int rtuPort, String Rcode, String Ncode, String Nip1, String Nip2, String Nip3, List<LightPathModel> lightPathList) throws CommonException {
		boolean paraValidate = true;
		String command = "";
		try {
			int trec = lightPathList.size();
			int top = 0;
			for (int i = 0; i < lightPathList.size(); i++) {
				LightPathModel lightPath = lightPathList.get(i);
				int topTemp = Integer.valueOf(lightPath.getNop());
				if (topTemp > top) {
					top = topTemp;
				}
			}

			if (Rcode.length() <= 10 && Ncode.length() <= 10 && Nip1.length() <= 10 && Nip2.length() <= 10 && Nip3.length() <= 10) {
				String tempCommand = "";
				if (Rcode.length() < 10) {
					for (int i = 0; i < 10 - Rcode.length(); i++) {
						tempCommand += " ";
					}
				}
				tempCommand += Rcode;
				if (Ncode.length() < 10) {
					for (int i = 0; i < 10 - Ncode.length(); i++) {
						tempCommand += " ";
					}
				}
				tempCommand += Ncode;
				if (Nip1.length() < 10) {
					for (int i = 0; i < 10 - Nip1.length(); i++) {
						tempCommand += " ";
					}
				}
				tempCommand += Nip1;
				if (Nip2.length() < 10) {
					for (int i = 0; i < 10 - Nip2.length(); i++) {
						tempCommand += " ";
					}
				}
				tempCommand += Nip2;
				if (Nip3.length() < 10) {
					for (int i = 0; i < 10 - Nip3.length(); i++) {
						tempCommand += " ";
					}
				}
				tempCommand += Nip3;
				command += tempCommand;
			} else {
				paraValidate = false;
			}
			command += String.format("%1$04d", top);
			command += String.format("%1$04d", trec);
			String tempCommand = "";
			for (int i = 0; i < lightPathList.size(); i++) {
				LightPathModel lightPath = lightPathList.get(i);
				if (lightPath.getNop().length() <= 4) {
					for (int j = 0; j < 4 - lightPath.getNop().length(); j++) {
						tempCommand += " ";
					}
				} else {
					paraValidate = false;
					break;
				}
				tempCommand += lightPath.getNop();
				command += tempCommand;
				tempCommand = "";

				if (lightPath.getNops().length() <= 2) {
					for (int j = 0; j < 2 - lightPath.getNops().length(); j++) {
						tempCommand += " ";
					}
				} else {
					paraValidate = false;
					break;
				}
				tempCommand += lightPath.getNops();
				command += tempCommand;
				tempCommand = "";

				if (lightPath.getRip().length() <= 10) {
					for (int j = 0; j < 10 - lightPath.getRip().length(); j++) {
						tempCommand += " ";
					}
				} else {
					paraValidate = false;
					break;
				}
				tempCommand += lightPath.getRip();
				command += tempCommand;
				tempCommand = "";

				if (lightPath.getRpt().length() <= 10) {
					for (int j = 0; j < 10 - lightPath.getRpt().length(); j++) {
						tempCommand += " ";
					}
				} else {
					paraValidate = false;
					break;
				}
				tempCommand += lightPath.getRpt();
				command += tempCommand;
				tempCommand = "";

				if (lightPath.getTu().length() <= 2) {
					for (int j = 0; j < 2 - lightPath.getTu().length(); j++) {
						tempCommand += " ";
					}
				} else {
					paraValidate = false;
					break;
				}
				tempCommand += lightPath.getTu();
				command += tempCommand;
				tempCommand = "";

				if (lightPath.getSl().length() <= 2) {
					for (int j = 0; j < 2 - lightPath.getSl().length(); j++) {
						tempCommand += " ";
					}
				} else {
					paraValidate = false;
					break;
				}
				tempCommand += lightPath.getSl();
				command += tempCommand;
				tempCommand = "";

				if (lightPath.getP().length() <= 2) {
					for (int j = 0; j < 2 - lightPath.getP().length(); j++) {
						tempCommand += " ";
					}
				} else {
					paraValidate = false;
					break;
				}
				tempCommand += lightPath.getP();
				command += tempCommand;
				tempCommand = "";
			}
			if (paraValidate) {
				command = preCommand + String.format("%1$ 10d", command.length() + 4) + "400C" + command;
				boolean conResult = rtu.connect(rtuIp, rtuPort);
				if (conResult) {
					String res = rtu.sendCommand(command);
					if (!"".equals(res)) {
						String result = res.split("701C")[1];
						String resFlag = result.substring(10, 12).trim();
						if (!"0".equals(resFlag)) {
							throw new CommonException(null, MessageCodeDefine.CMD_EXECUTE_FAIL, result);
						}
					} else {
						throw new CommonException(null, MessageCodeDefine.CMD_RETURN_EMPTY_ERROR);
					}
				} else {
					throw new CommonException(null, MessageCodeDefine.CMD_CONNECT_ERROR);
				}
			} else {
				throw new CommonException(null, MessageCodeDefine.CMD_PARA_ILLEG);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
		rtu.disConnect();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see xintianwei.RTUBaseService#takeMannalTest(java.lang.String, int,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	private void takeMannalTest(String rtuIp, int rtuPort, String Rcode, String Nip, String Onum, String Tmode, String p1, String p2, String p3, String p4, String p5) throws CommonException {
		boolean paraValidate = true;
		String tempCommand = "";
		try {
			if (Rcode.length() <= 10) {
				for (int i = 0; i < 10 - Rcode.length(); i++) {
					tempCommand += " ";
				}
			} else {
				paraValidate = false;
			}
			tempCommand += Rcode;

			if (Nip.length() <= 10) {
				for (int i = 0; i < 10 - Nip.length(); i++) {
					tempCommand += " ";
				}
			} else {
				paraValidate = false;
			}
			tempCommand += Nip;

			if (Onum.length() <= 3) {
				for (int i = 0; i < 3 - Onum.length(); i++) {
					tempCommand += " ";
				}
			} else {
				paraValidate = false;
			}
			tempCommand += Onum;

			if (Tmode.length() == 1) {
				tempCommand += Tmode;
			} else {
				paraValidate = false;
			}

			if (p1.length() <= 10) {
				for (int i = 0; i < 10 - p1.length(); i++) {
					tempCommand += " ";
				}
			} else {
				paraValidate = false;
			}
			tempCommand += p1;

			if (p2.length() <= 10) {
				for (int i = 0; i < 10 - p2.length(); i++) {
					tempCommand += " ";
				}
			} else {
				paraValidate = false;
			}
			tempCommand += p2;

			if (p3.length() <= 10) {
				for (int i = 0; i < 10 - p3.length(); i++) {
					tempCommand += " ";
				}
			} else {
				paraValidate = false;
			}
			tempCommand += p3;

			if (p4.length() <= 10) {
				for (int i = 0; i < 10 - p4.length(); i++) {
					tempCommand += " ";
				}
			} else {
				paraValidate = false;
			}
			tempCommand += p4;

			if (p5.length() <= 10) {
				for (int i = 0; i < 10 - p5.length(); i++) {
					tempCommand += " ";
				}
			} else {
				paraValidate = false;
			}
			tempCommand += p5;

			if (paraValidate) {
				String command = preCommand + "        78110C" + tempCommand;
				boolean conResult = rtu.connect(rtuIp, rtuPort);
				if (conResult) {
					String res = rtu.sendCommand(command);
					if (!"".equals(res)) {
						String result = res.split("701C")[1];
						String resFlag = result.substring(10, 12).trim();
						if (!"0".equals(resFlag)) {
							throw new CommonException(null, MessageCodeDefine.CMD_EXECUTE_FAIL, result);
						}
					} else {
						throw new CommonException(null, MessageCodeDefine.CMD_RETURN_EMPTY_ERROR);
					}
				} else {
					throw new CommonException(null, MessageCodeDefine.CMD_CONNECT_ERROR);
				}
			} else {
				throw new CommonException(null, MessageCodeDefine.CMD_PARA_ILLEG);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
		rtu.disConnect();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fujitsu.manager.equipmentTestManager.serviceImpl.XTWTest.base.
	 * RTUBaseService#loadRTUConfiguration(java.lang.String, int,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<RTUConfiguration> loadRTUConfiguration(String rtuIp, int rtuPort, String Rcode, String Ncode, String Para) throws CommonException {
		List<RTUConfiguration> rtuConfigurationList = new ArrayList();
		try {
			if (Rcode.length() <= 10 && Ncode.length() <= 10 && Para.length() <= 10) {
				String tempCommand = "";
				if (Rcode.length() < 10) {
					for (int i = 0; i < 10 - Rcode.length(); i++) {
						tempCommand += " ";
					}
				}
				tempCommand += Rcode;
				if (Ncode.length() < 10) {
					for (int i = 0; i < 10 - Ncode.length(); i++) {
						tempCommand += " ";
					}
				}
				tempCommand += Ncode;
				if (Para.length() < 10) {
					for (int i = 0; i < 10 - Para.length(); i++) {
						tempCommand += " ";
					}
				}
				tempCommand += Para;
				String command = preCommand + "        34610C" + tempCommand;
				boolean conResult = rtu.connect(rtuIp, rtuPort);
				if (conResult) {
					String res = rtu.sendCommand(command);
					if (!"".equals(res)) {
						if (res.contains("615C")) {
							String result = res.split("615C")[1];
							int num = Integer.parseInt(result.substring(10, 12).trim());

							for (int i = 0; i < num; i++) {
								RTUConfiguration rtuConfiguration = new RTUConfiguration();
								rtuConfiguration.setTu(result.substring(12 + 32 * i, 14 + 32 * i).trim());
								rtuConfiguration.setSlot(result.substring(14 + 32 * i, 16 + 32 * i).trim());
								rtuConfiguration.setMadeDate(result.substring(16 + 32 * i, 24 + 32 * i).trim());
								rtuConfiguration.setSn(result.substring(24 + 32 * i, 34 + 32 * i).trim());
								rtuConfiguration.setHardwareVersion(result.substring(34 + 32 * i, 38 + 32 * i).trim());
								rtuConfiguration.setSoftwareVersion(result.substring(38 + 32 * i, 42 + 32 * i).trim());
								rtuConfiguration.setStatus(result.substring(42 + 32 * i, 44 + 32 * i).trim());
								rtuConfigurationList.add(rtuConfiguration);
							}
						} else {
							throw new CommonException(null, MessageCodeDefine.CMD_EXECUTE_FAIL, res.split("701C")[1]);
						}
					} else {
						throw new CommonException(null, MessageCodeDefine.CMD_RETURN_EMPTY_ERROR);
					}
				} else {
					throw new CommonException(null, MessageCodeDefine.CMD_CONNECT_ERROR);
				}
			} else {
				throw new CommonException(null, MessageCodeDefine.CMD_PARA_ILLEG);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
		rtu.disConnect();
		return rtuConfigurationList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fujitsu.manager.equipmentTestManager.serviceImpl.XTWTest.base.
	 * RTUBaseService#loadRTUAlarm(java.lang.String, int, java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<RTUAlarm> loadRTUAlarm(String rtuIp, int rtuPort, String Rcode, String Nip, String Amode) throws CommonException {
		List<RTUAlarm> rtuAlarmList = new ArrayList();
		try {
			if (Rcode.length() <= 10 && Nip.length() <= 10 && Amode.length() <= 2) {
				String tempCommand = "";
				if (Rcode.length() < 10) {
					for (int i = 0; i < 10 - Rcode.length(); i++) {
						tempCommand += " ";
					}
				}
				tempCommand += Rcode;
				if (Nip.length() < 10) {
					for (int i = 0; i < 10 - Nip.length(); i++) {
						tempCommand += " ";
					}
				}
				tempCommand += Nip;
				if (Amode.length() < 2) {
					for (int i = 0; i < 2 - Amode.length(); i++) {
						tempCommand += " ";
					}
				}
				tempCommand += Amode;

				String command = preCommand + "        26630C" + tempCommand;
				boolean conResult = rtu.connect(rtuIp, rtuPort);
				if (conResult) {
					String res = rtu.sendCommand(command);
					if (!"".equals(res)) {
						if (res.contains("635C")) {
							String result = res.split("635C")[1];
							String rcode = result.substring(0, 10).trim();
							String amode = result.substring(10, 12).trim();
							int num = Integer.parseInt(result.substring(12, 22).trim());

							for (int i = 0; i < num; i++) {
								RTUAlarm rtuAlarm = new RTUAlarm();
								rtuAlarm.setRcode(rcode);
								rtuAlarm.setAmode(amode);
								rtuAlarm.setAlarmMachineType(result.substring(22 + 32 * i, 32 + 32 * i).trim());
								rtuAlarm.setAlarmSlot(result.substring(32 + 32 * i, 34 + 32 * i).trim());
								rtuAlarm.setAlarmPort(result.substring(34 + 32 * i, 36 + 32 * i).trim());
								rtuAlarm.setAlarmLevel(result.substring(36 + 32 * i, 38 + 32 * i).trim());
								rtuAlarm.setAlarmContent(result.substring(38 + 32 * i, 40 + 32 * i).trim());
								rtuAlarm.setAlarmTime(result.substring(40 + 32 * i, 54 + 32 * i).trim());
								rtuAlarmList.add(rtuAlarm);
							}
						} else {
							throw new CommonException(null, MessageCodeDefine.CMD_EXECUTE_FAIL, res.split("701C")[1]);
						}
					} else {
						throw new CommonException(null, MessageCodeDefine.CMD_RETURN_EMPTY_ERROR);
					}
				} else {
					throw new CommonException(null, MessageCodeDefine.CMD_CONNECT_ERROR);
				}
			} else {
				throw new CommonException(null, MessageCodeDefine.CMD_PARA_ILLEG);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
		rtu.disConnect();
		return rtuAlarmList;
	}

	private void OTDRResult(byte[] resultArray) {
		// TODO 测试结果返回值处理

		// File file = new File(filePath);
		// BufferedReader reader = null;
		int temp;
		SimpleDateFormat timeParser = new SimpleDateFormat("yyyyMMddHHmmss");
		StringBuffer sb = new StringBuffer();
		try {
			// reader = new BufferedReader(new FileReader(file));
			int i = 0;
			int highByte = 0;
			int lowByte = 0;
			boolean flag = false;
			BigDecimal distance = new BigDecimal(0);
			BigDecimal segmentCount = new BigDecimal(0);
			StringBuilder stringBuilderType = new StringBuilder();
			String testType = "";
			int testTypeValue = 0;
			int totalPoint = 0;
			int pointIndex = 0;
			StringBuilder sb2 = new StringBuilder();
			String rcode = "";
			String onum = "";
			String tmode = "";
			String p1 = "";
			String p2 = "";
			String p3 = "";
			String p4 = "";
			String p5 = "";
			String date = "";
			String AT21 = "";
			String AT22 = "";
			String AT23 = "";
			String AT24 = "";
			String SR = "";
			String DN = "";
			int index = 0;
			while (i < resultArray.length) {
				temp = resultArray[i];
				if (i < 224) {
				}

				else if (i < 462) {
					sb.append((char) temp);
				} else {
					if (i == 462) {
						String info = sb.toString();
						sb = new StringBuffer();
						rcode = info.substring(0, 10);
						onum = info.substring(10, 13);
						tmode = info.substring(63, 64);
						// 全程传输损耗值(dB)、全程光学长度值(米)、全程事件点数、全程反向损耗值可能为空
						// 量程
						p1 = info.substring(64, 74);
						// 脉宽
						p2 = info.substring(74, 84);
						// 波长
						p3 = info.substring(84, 94);
						// 平均化次数
						p4 = info.substring(94, 104);
						// 群折射率
						p5 = info.substring(104, 114);
						date = info.substring(114, 128);
						// 全程传输损耗值
						AT21 = info.substring(128, 138);
						// 全程光学长度值
						AT22 = info.substring(138, 148);
						// 全程事件点数
						AT23 = info.substring(148, 158);
						// 全程反向损耗值
						AT24 = info.substring(158, 168);
						// 采样频率
						SR = info.substring(214, 220);
						DN = info.substring(224, 230);
						distance = new BigDecimal(p1.trim());
						segmentCount = new BigDecimal(Integer.valueOf(DN.trim()) / 2 - 1);
						totalPoint = Integer.valueOf(DN.trim()) / 2;
						for (int j = 0; j < 14; j++) {
							stringBuilderType.append((char) resultArray[224 + 238 + Integer.valueOf(DN.trim()) + j]);
						}
						testType = stringBuilderType.toString();
						
						sb2.append("@").append(CommonDefine.OTDR_TYPE_ANRITUS);
						//waveLength
						sb2.append("@").append(Double.valueOf(p3.trim()));
						//pulseWidth
						sb2.append("@").append(Double.valueOf(p2.trim()));
						//range
						sb2.append("@").append(Double.valueOf(p1.trim()));
						//averageCount
						sb2.append("@").append(Double.valueOf(p4.trim()));
						//refractCoefficient
						sb2.append("@").append(Double.valueOf(p5.trim()));
						//attenuation
						sb2.append("@").append(AT21.trim().length()!=0?(Float.valueOf(AT21.trim()).equals(new Float(8192.0))?"-":String.valueOf(AT21.trim())):"0");
						//opticalDistance
						sb2.append("@").append(AT22.trim().length() != 0 ? Double.valueOf(AT22.trim()) : 0);
						//reverseAttenuation
						// 全程反向损耗值的内容有时包含非数字字符（如"<"），导致浮点数字转换异常；添加异常捕获处理
						// sb2.append("@").append(AT24.trim().length()!=0?(Float.valueOf(AT24.trim()).equals(new Float(8192.0))?"-":String.valueOf(AT24.trim())):"0");
						if (AT24.trim().length()!=0) {
							try {
								Float f = Float.valueOf(AT24.trim());
								sb2.append("@").append(f.equals(new Float(8192.0))?"-":String.valueOf(AT24.trim()));
							} catch (NumberFormatException e) {
								sb2.append("@").append(String.valueOf(AT24.trim()));
							}
						} else {
							sb2.append("@").append("0");
						}
						sb2.append("\n");
					}

					if (i % 2 == 0) {
						lowByte = temp;
					} else {
						highByte = temp;
					}
					if (i % 2 != 0) {
						pointIndex++;
						// 这么多个点数，写进文件
						if (pointIndex <= totalPoint) {
							String highByteString = Integer.toHexString(highByte & 0xFF);
							String lowByteString = Integer.toHexString(lowByte & 0xFF);
							// Logger log =
							// Logger.getLogger(RTUSFMUploadCommand.class);
							int highByte1 = 0;
							int highByte2 = 0;
							if (highByteString.length() == 2) {
								highByte1 = (Integer.parseInt(highByteString.substring(0, 1), 16)) * 16 * 16 * 16;
								highByte2 = (Integer.parseInt(highByteString.substring(1), 16)) * 16 * 16;
							} else {
								highByte1 = 0;
								highByte2 = (Integer.parseInt(highByteString.substring(0, 1), 16)) * 16 * 16;
							}
							int lowByte1 = 0;
							int lowByte2 = 0;
							if (lowByteString.length() == 2) {
								lowByte1 = (Integer.parseInt(lowByteString.substring(0, 1), 16)) * 16;
								lowByte2 = (Integer.parseInt(lowByteString.substring(1), 16));
							} else {
								lowByte1 = 0;
								lowByte2 = (Integer.parseInt(lowByteString.substring(0, 1), 16));
							}
							int finalResult = highByte1 + highByte2 + lowByte1 + lowByte2;
							BigDecimal actualResult = 0 != finalResult ? new BigDecimal(finalResult).divide(new BigDecimal(1000), 10, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
							BigDecimal dist = new BigDecimal(SR.trim()).multiply(new BigDecimal(pointIndex - 1)).divide(new BigDecimal(1000), 10, BigDecimal.ROUND_HALF_UP);
							// 防止“0E-10”格式的数字在后续处理不能识别，导致无法绘图
							if ("0E-10".equals(dist.toString())) {
								sb2.append("0.0").append(",");
							} else {
								sb2.append(dist).append(",");	
							}
							
							sb2.append(actualResult).append("\n");
						}

					}

				}

				i++;
			}
			// 保存测试事件
			int eventCount = 0;
			if (AT23.trim().length() != 0) {
				eventCount = Integer.valueOf(AT23.trim());
			}

			List<Map> eventList = new ArrayList();
			for (int k = 0; k < eventCount; k++) {
				StringBuffer sbLocation = new StringBuffer();
				StringBuffer sbAttenuation = new StringBuffer();
				StringBuffer sbReflectValue = new StringBuffer();
				StringBuffer sbType = new StringBuffer();
				String location = "";
				String attenuation = "";
				String reflectValue = "";
				String type = "";
				int eventStartIndex = 462 + Integer.valueOf(DN.trim()) + 14;
				// 事件点k位置
				for (int m = eventStartIndex + 32 * k; m < eventStartIndex + 32 * k + 10; m++) {
					sbLocation.append((char) resultArray[m]);
				}
				// 事件点k接头衰耗
				for (int m = eventStartIndex + 10 + 32 * k; m < eventStartIndex + 10 + 32 * k + 10; m++) {
					sbAttenuation.append((char) resultArray[m]);
				}
				// 事件点k反射值
				for (int m = eventStartIndex + 20 + 32 * k; m < eventStartIndex + 20 + 32 * k + 10; m++) {
					sbReflectValue.append((char) resultArray[m]);
				}
				// 事件点k类型
				for (int m = eventStartIndex + 30 + 32 * k; m < eventStartIndex + 30 + 32 * k + 2; m++) {
					sbType.append((char) resultArray[m]);
				}
				location = sbLocation.toString().trim();
				
				attenuation = sbAttenuation.toString().trim();
				try {
    				Float attenuationFloat = Float.valueOf(attenuation);
    				attenuation = attenuationFloat.equals(new Float(8192.0))?"-":attenuation;
				} catch (NumberFormatException e) {
					System.out.println("事件点k接头衰耗格式转换异常：("+attenuation+")");
				}
				
				reflectValue = sbReflectValue.toString().trim();
				Float reflectValueFloat = null;
				try {
					reflectValueFloat = Float.valueOf(reflectValue);
					reflectValue = reflectValueFloat.equals(new Float(8192.0))?"-":reflectValue;
				} catch (NumberFormatException e) {
					System.out.println("事件点k反射值格式转换异常：("+reflectValue+")");
				}
				
				type = sbType.toString().trim();
				if (type.equals("S")) {
					type = "Saturated reflective event";
				} else if (type.equals("N")) {
					type = "Non-reflective event";
				} else if (type.equals("R")) {
					type = "Reflective event";
				} else if (type.equals("E")) {
					type = "Fiber-end event";
				}
				sb2.append("###  ");
				sb2.append("#" + k).append("#" + location).append("#" + attenuation).append("#" + reflectValue).append("#" + type);
			}

			CommonUtil.SaveOTDRFile(TEMP_FILE_PATH, TEMP_FILE_NAME, sb2.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	private void OTDRResult_china(byte[] resultArray) {
		int temp;
		SimpleDateFormat timeParser = new SimpleDateFormat("yyyyMMddHHmmss");
		StringBuffer sb = new StringBuffer();
		try {
			// reader = new BufferedReader(new FileReader(file));
			int i = 0;
			int highByte = 0;
			int lowByte = 0;
			boolean flag = false;
			BigDecimal distance = new BigDecimal(0);
			// BigDecimal segmentCount = new BigDecimal(0);
			StringBuilder stringBuilderType = new StringBuilder();
			String testType = "";
			int testTypeValue = 0;
			StringBuilder sb2 = new StringBuilder();
			String rcode = "";
			String onum = "";
			String tmode = "";
			String p1 = "";
			String p2 = "";
			String p3 = "";
			String p4 = "";
			String p5 = "";
			String date = "";
			String AT21 = "";
			String AT22 = "";
			String AT23 = "";
			String AT24 = "";
			String SR = "";
			// String DN = "";
			List<String> test_info = new ArrayList<String>();

			// 测试结果数据的个数
			int result_data_num = 0;
			List<String> result_data = new ArrayList<String>();

			int event_num = 0;
			// List<String> event_data = new ArrayList<String>();
			List<List> event = new ArrayList<List>();

			int index = 0;

			String result_head = "";// 测试消息头
			String test_info_temp = "";
			String part1 = "";
			String part2 = "";
			String part3 = "";
			String part4 = "";
			System.out.println(resultArray.length);

			i = 224;
			while ((i < resultArray.length) && (i < 220 + 4 + 92)) {
				result_head = result_head + (char) resultArray[i];
				i++;
			}

			// test info
			int test_info_num = 13;// 测试条件共13个字估，都是4个字节
			int test_info_temp_num = 0;
			System.out.println("before i=" + i);
			i = 220 + 4 + 92;
			System.out.println("after i=" + i);
			while ((i < resultArray.length) && (test_info_temp_num < test_info_num)) {
				test_info_temp = "";
				part1 = "";
				part2 = "";
				part3 = "";
				part4 = "";
				part1 = Integer.toHexString(resultArray[i + 3] & 0xFF);
				if (part1.length() == 1) {
					part1 = '0' + part1;
				}
				part2 = Integer.toHexString(resultArray[i + 2] & 0xFF);
				if (part2.length() == 1) {
					part2 = '0' + part2;
				}
				part3 = Integer.toHexString(resultArray[i + 1] & 0xFF);
				if (part3.length() == 1) {
					part3 = '0' + part3;
				}
				part4 = Integer.toHexString(resultArray[i] & 0xFF);
				if (part4.length() == 1) {
					part4 = '0' + part4;
				}
				if (5 <= test_info_temp_num && test_info_temp_num <= 8)// float类型
				{

					test_info_temp = Float.toString(Float.intBitsToFloat(Integer.parseInt(part1 + part2 + part3 + part4, 16)));
				} else {
					test_info_temp = Integer.toString(Integer.parseInt(part1 + part2 + part3 + part4, 16));
				}
				System.out.println("检查16进制转换是否正确-：" + part1 + part2 + part3 + part4 + "," + test_info_temp);
				test_info.add(test_info_temp);

				i = i + 4;
				test_info_temp_num++;
			}

			// test result
			System.out.println("before i=" + i);
			i = 220 + 4 + 92 + 13 * 4;
			System.out.println("after i=" + i);
			part1 = "";
			part2 = "";
			part3 = "";
			part4 = "";
			// result_data_num
			part1 = Integer.toHexString(resultArray[i + 3] & 0xFF);
			if (part1.length() == 1) {
				part1 = '0' + part1;
			}
			part2 = Integer.toHexString(resultArray[i + 2] & 0xFF);
			if (part2.length() == 1) {
				part2 = '0' + part2;
			}
			part3 = Integer.toHexString(resultArray[i + 1] & 0xFF);
			if (part3.length() == 1) {
				part3 = '0' + part3;
			}
			part4 = Integer.toHexString(resultArray[i] & 0xFF);
			if (part4.length() == 1) {
				part4 = '0' + part4;
			}
			// 测试结果数据的个数
			result_data_num = Integer.parseInt(part1 + part2 + part3 + part4, 16);

			int result_data_temp_num = 0;
			i = 220 + 4 + 92 + 13 * 4 + 4;
			float data_float = 0;
			while ((i < resultArray.length) && (result_data_temp_num < result_data_num)) {
				test_info_temp = "";
				part1 = Integer.toHexString(resultArray[i + 1] & 0xFF);
				if (part1.length() == 1) {
					part1 = '0' + part1;
				}
				part2 = Integer.toHexString(resultArray[i] & 0xFF);
				if (part2.length() == 1) {
					part2 = '0' + part2;
				}
				data_float = Float.valueOf(Integer.toString(Integer.parseInt(part1 + part2, 16))) / 1000;

				// Modify by Lijie:如果Y值小于0,那么赋值为0即可(黄B确认)
				if (data_float < 0) {
					data_float = 0;
				}

				test_info_temp = Float.toString(data_float);
				result_data.add(test_info_temp);

				i = i + 2;
				result_data_temp_num++;
			}
			System.out.println("result_data_num" + result_data_num);
			// event
			i = 220 + 4 + 92 + 13 * 4 + 4 + result_data_num * 2;
			part1 = "";
			part2 = "";
			part3 = "";
			part4 = "";
			part1 = Integer.toHexString(resultArray[i + 3] & 0xFF);
			if (part1.length() == 1) {
				part1 = '0' + part1;
			}
			part2 = Integer.toHexString(resultArray[i + 2] & 0xFF);
			if (part2.length() == 1) {
				part2 = '0' + part2;
			}
			part3 = Integer.toHexString(resultArray[i + 1] & 0xFF);
			if (part3.length() == 1) {
				part3 = '0' + part3;
			}
			part4 = Integer.toHexString(resultArray[i] & 0xFF);
			if (part4.length() == 1) {
				part4 = '0' + part4;
			}
			// 测试事件点个数
			event_num = Integer.parseInt(part1 + part2 + part3 + part4, 16);
			System.out.println("before i=" + i);
			i = 220 + 4 + 92 + 13 * 4 + 4 + result_data_num * 2 + 4;
			System.out.println("after i=" + i);
			int j = 0;
			int k = 0;
			Float temp_float = 0.0f;
			System.out.println("event_num:" + event_num);
			while ((i < resultArray.length) && (j < event_num)) {
				k = 0;
				List<String> event_data = new ArrayList<String>();
				while (k < 6) {
					test_info_temp = "";
					part1 = "";
					part2 = "";
					part3 = "";
					part4 = "";

					part1 = Integer.toHexString(resultArray[i + 3] & 0xFF);
					if (part1.length() == 1) {
						part1 = '0' + part1;
					}
					part2 = Integer.toHexString(resultArray[i + 2] & 0xFF);
					if (part2.length() == 1) {
						part2 = '0' + part2;
					}
					part3 = Integer.toHexString(resultArray[i + 1] & 0xFF);
					if (part3.length() == 1) {
						part3 = '0' + part3;
					}
					part4 = Integer.toHexString(resultArray[i] & 0xFF);
					if (part4.length() == 1) {
						part4 = '0' + part4;
					}
					if (k < 2) {
						test_info_temp = Integer.toString(Integer.parseInt(part1 + part2 + part3 + part4, 16));
					} else// 后4个参数为float类型
					{
						test_info_temp = Float.toString(Float.intBitsToFloat((int) (Long.parseLong(part1 + part2 + part3 + part4, 16))));
					}
					event_data.add(test_info_temp);

					i = i + 4;
					k = k + 1;
				}
				System.out.print("\n打印事件值:");
				k = 0;
				while (k < 6) {
					System.out.print(event_data.get(k) + "/");
					k++;
				}
				event.add(event_data);
				j++;
			}

			// 打印事件点信息
			System.out.print("event_num:" + event_num);
			j = 0;
			List<String> temp_list = new ArrayList<String>();
			while ((j < event.size()) && (j < 20))// 只打印前20条
			{
				k = 0;
				System.out.print("\n event_data_" + j + "-size:" + event.get(j).size());
				while (k < (event.get(j).size())) {
					System.out.print(event.get(j).get(k) + ":k=" + k + "/");
					k++;
				}
				j++;
			}

			rcode = result_head.substring(0, 10);
			onum = result_head.substring(10, 13);
			tmode = result_head.substring(63, 64);
			// 全程传输损耗值(dB)、全程光学长度值(米)、全程事件点数、全程反向损耗值可能为空
			// 量程
			p1 = test_info.get(1);
			// 脉宽
			p2 = test_info.get(2);
			// 波长
			p3 = test_info.get(3);
			// 平均化次数
			// p4 = "0";//info.substring(94, 104);
			if (test_info.get(4).equals("")) {
				p4 = "0";
			} else {
				p4 = String.valueOf(Integer.parseInt(test_info.get(4)) / 1000);
			}

			// 群折射率
			p5 = test_info.get(5);
			// 测试日期
			date = result_head.substring(64, 78);
			// 全程传输损耗值
			AT21 = test_info.get(7);
			// 全程光学长度值
			AT22 = test_info.get(6);
			// 全程事件点数
			AT23 = Integer.toString(event_num);
			// 全程反向损耗值
			AT24 = test_info.get(8);
			// 采样频率
			SR = test_info.get(0);

			distance = new BigDecimal(p1.trim());
			
			//OTDR类型
			sb2.append("@").append(CommonDefine.OTDR_TYPE_CHINA);
			//waveLength
			sb2.append("@").append(Double.valueOf(p3.trim()));
			//pulseWidth
			sb2.append("@").append(Double.valueOf(p2.trim()));
			//range
			sb2.append("@").append(Double.valueOf(p1.trim())/1000);
			//averageCount
			sb2.append("@").append(Double.valueOf(p4.trim()));
			//refractCoefficient
			sb2.append("@").append(Double.valueOf(p5.trim()));
			//attenuation
			sb2.append("@").append(AT21.trim().length()!=0?(Float.valueOf(AT21.trim()).equals(new Float(8192.0))?"-":String.valueOf(AT21.trim())):"0");
			//opticalDistance
			sb2.append("@").append(AT22.trim().length() != 0 ? (Double.valueOf(AT22.trim())/1000) : 0);
			//reverseAttenuation
			sb2.append("@").append(AT24.trim().length()!=0?(Float.valueOf(AT24.trim()).equals(new Float(8192.0))?"-":String.valueOf(AT24.trim())):"0");
			sb2.append("\n");
			
			k = 0;
			// 写测试结果数据点
			Float result_data_temp;
			// System.out.println("\nSave result data..."+result_data_num);
			while (k < result_data_num) {
				if (k < 50) {
					System.out.print("/" + k);
				}
				result_data_temp = Float.valueOf(result_data.get(k));
				
				Float site_y = result_data_temp;
				double site_x = 0;
				double sr = Double.parseDouble(SR);
				double p = Double.parseDouble(p5);
				double count = 2 * p * (sr / 299792458);

				if (k == 0) {
					site_x = 0;
				} else {
					site_x = (k / count) / 1000;
				}

				sb2.append(site_x).append(",");
				sb2.append(site_y).append("\n");
				k++;
			}

			k = 0;
			int type = 0;
			String typeName = "";
			List<Map> eventList = new ArrayList();
			
			while (k < event_num) {
				sb2.append("###  ");
				typeName = "";
				type = Integer.parseInt(event.get(k).get(1).toString());
				if (0 == type) {
					typeName = "Saturated reflective event";
				} else if (1 == type) {
					typeName = "Reflective event";
				} else if (2 == type) {
					typeName = "Non-reflective event";
				} else if (3 == type) {
					typeName = "Fiber-end event";
				}
				Map event_temp = new HashMap();
				// 序号
				sb2.append("#" + k);
				// 测试ID
				// 位置算法
				// 1米包含的采样点数量=2*群折射率*采样频率/光速
				// 光速=299792458
				// 事件点位置=返回的事件点序号/1米包含的采样点数量
				double no = Double.parseDouble(event.get(k).get(0).toString());
				double sr = Double.parseDouble(SR);
				double p = Double.parseDouble(p5);
				double count = 2 * p * (sr / 299792458);
				System.out.println("*返回的事件点序号:" + no);
				System.out.println("*采样频率:" + sr);
				System.out.println("*群折射率:" + p);
				System.out.println("*1米包含的采样点数量:" + count);
				double position = (no / count)/1000;
				java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
				System.out.println("* 事件点位置:" + position);

				if (no == 0.0) {
					sb2.append("#" + 0);
				} else {
					sb2.append("#" + df.format(position));
				}

				// 接头损耗 插入损耗
				String attenuation = String.valueOf(event.get(k).get(3));
				Float attenuationFloat = Float.valueOf(attenuation);
				attenuation = attenuationFloat.equals(new Float(8192.0))?"-":attenuation;
				sb2.append("#" + attenuation);
				// 反射值 回波损耗
				String reflectValue = String.valueOf(event.get(k).get(2));
				Float reflectValueFloat = Float.valueOf(reflectValue);
				reflectValue = reflectValueFloat.equals(new Float(8192.0))?"-":reflectValue;
				sb2.append("#" + reflectValue);
				// 类型
				sb2.append("#" + typeName);
				k++;
			}
			CommonUtil.SaveOTDRFile(TEMP_FILE_PATH, TEMP_FILE_NAME , sb2.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void rtuAlarmPush(byte[] resultArr) {
		String result = "";
		try {
			result = new String(resultArr, "gbk");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		result = result.split("635C")[1];
		int total = Integer.parseInt(result.substring(12, 22).trim());
		String RTUNO = result.substring(0, 10).trim();
		List<RTUAlarm> alarmMapList = new ArrayList();
		for (int i = 0; i < total; i++) {
			RTUAlarm originalAlarm = new RTUAlarm();
			originalAlarm.setRcode(RTUNO);
			originalAlarm.setAmode(result.substring(10, 12));
			originalAlarm.setAlarmMachineType(result.substring(22 + 32 * i, 32 + 32 * i));
			originalAlarm.setAlarmSlot(result.substring(32 + 32 * i, 34 + 32 * i));
			originalAlarm.setAlarmPort(result.substring(34 + 32 * i, 36 + 32 * i));
			originalAlarm.setAlarmLevel(result.substring(36 + 32 * i, 38 + 32 * i));
			originalAlarm.setAlarmContent(result.substring(38 + 32 * i, 40 + 32 * i));
			originalAlarm.setAlarmTime(result.substring(40 + 32 * i, 54 + 32 * i));
			alarmMapList.add(originalAlarm);
		}
		Map map = new HashMap();
		map.put("alarmMapList", alarmMapList);
		JMSSender.sendMessage(MessageCodeDefine.ALARM_JMS_CODE, map);
	}

	/**
	 * 昕天卫完整测试
	 * 
	 * @param eqpt
	 * @param sys
	 * @param routeList
	 * @param testPara
	 * @return
	 * @throws CommonException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String startTest(EqptInfoModel eqpt, SysInfoModel sys, List<RoutePointInfoModel> routePointList, TestParaInfoModel testPara) throws CommonException {
		String testResult = "";
		if (eqpt != null && sys != null && routePointList.size() > 0 && testPara != null) {
			boolean emptyFlag1 = CommonUtil.checkObjectNotEmpty((Object) eqpt,
					null);
			boolean emptyFlag2 = CommonUtil.checkObjectNotEmpty((Object) sys,
					null);
			boolean emptyFlag3 = CommonUtil.checkObjectNotEmpty(
					(Object) testPara, null);
			boolean emptyFlag4 = true;
			for (int i = 0; i < routePointList.size(); i++) {
				RoutePointInfoModel routePoint = routePointList.get(i);
				emptyFlag4 = CommonUtil.checkObjectNotEmpty(
						(Object) routePoint, null);
				if (!emptyFlag4) {
					break;
				}
			}
			if (emptyFlag1 && emptyFlag2 && emptyFlag3 && emptyFlag4) {
				List lightPathList = new ArrayList();
				for (int i = 0; i < routePointList.size(); i++) {
					RoutePointInfoModel route = routePointList.get(i);
					LightPathModel lightPath = new LightPathModel();
					lightPath.setNop("1");
					lightPath.setNops(String.valueOf(i + 1));
					lightPath.setRip(CommonUtil.ipToDecString(eqpt.getRtuIp()));
					lightPath.setRpt(String.valueOf(eqpt.getRtuPort()));
					lightPath.setTu("1");
					lightPath.setSl(route.getSlot());
					lightPath.setP(route.getPort());

					lightPathList.add(lightPath);
				}

				String rtuIp = eqpt.getRtuIp();
				int rtuPort = eqpt.getRtuPort();
				String rtuCode = eqpt.getRcode();
				String sysIp = sys.getNip();
				String sysCode = sys.getNcode();

				setLightPath(rtuIp, rtuPort, rtuCode, sysCode, CommonUtil.ipToDecString(sysIp), "", "", lightPathList);

				String otdrTestRange = testPara.getOtdrTestRange();
				String otdrWaveLength = testPara.getOtdrWaveLength();
				String otdrPluseWidth = testPara.getOtdrPluseWidth();
				String otdrRefractCoefficient = testPara.getOtdrRefractCoefficient();
				String otdrTestTime = testPara.getOtdrTestTime();

				takeMannalTest(rtuIp, rtuPort, rtuCode, CommonUtil.ipToDecString(sysIp), "1", "1", otdrTestRange, otdrPluseWidth, otdrWaveLength, otdrTestTime, otdrRefractCoefficient);
				
				try {
					// 启动结果监测线程
					ExecutorService execute = Executors.newSingleThreadExecutor();
					ResultFileListenThread thread = new ResultFileListenThread(
							(new Date()), TEMP_FILE_PATH , TEMP_FILE_NAME);
					Future<String> result = execute.submit(thread);
					testResult = result.get();
					execute.shutdown();
				} catch (Exception e) {
					e.printStackTrace();
					throw new CommonException(e, 0);
				}
				
			}else{
				throw new CommonException(null, 0);
			}
		}
		return testResult;
	}

	/**
	 * @function:查询设备配置
	 * @data:2015-1-6
	 * @author cao senrong
	 * @param eqpt
	 * @param sys
	 * @return List<RTUConfiguration>
	 * @throws CommonException
	 * 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<RTUConfiguration> loadRTUConfiguration(EqptInfoModel eqpt, SysInfoModel sys) throws CommonException {

		List<RTUConfiguration> rtuConfigList = new ArrayList();

		boolean emptyFlag1 = CommonUtil
				.checkObjectNotEmpty((Object) eqpt, null);
		boolean emptyFlag2 = CommonUtil.checkObjectNotEmpty((Object) sys, null);

		if (emptyFlag1 && emptyFlag2) {
			String rtuIp = eqpt.getRtuIp();
			int rtuPort = eqpt.getRtuPort();
			String Rcode = eqpt.getRcode();
			String Ncode = sys.getNcode();
			rtuConfigList = loadRTUConfiguration(rtuIp, rtuPort, Rcode, Ncode, "0");

		}
		
		return rtuConfigList;
	}

	/**
	 * @function:查询设备告警
	 * @data:2015-1-6
	 * @author cao senrong
	 * @param eqpt
	 * @param sys
	 * @return List<RTUAlarm>
	 * @throws CommonException
	 * 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<RTUAlarm> loadRTUAlarm(EqptInfoModel eqpt, SysInfoModel sys) throws CommonException {

		List<RTUAlarm> rtuAlarmList = new ArrayList();
		
		boolean emptyFlag1 = CommonUtil.checkObjectNotEmpty((Object) eqpt,
				new String[] { "rtuType" });
		boolean emptyFlag2 = CommonUtil.checkObjectNotEmpty((Object) sys, null);

		if (emptyFlag1 && emptyFlag2) {
			String rtuIp = eqpt.getRtuIp();
			int rtuPort = eqpt.getRtuPort();
			String Rcode = eqpt.getRcode();
			String Nip = CommonUtil.ipToDecString(sys.getNip());
			rtuAlarmList = loadRTUAlarm(rtuIp, rtuPort, Rcode, Nip, "1");
		}

		return rtuAlarmList;
	}

	/**
	 * @function:处理socketserver上报的消息
	 * @data:2015-1-8
	 * @author cao senrong
	 * @param inputStream
	 *            void
	 * 
	 */
	public void socketServerMsgHandle(InputStream inputStream) {
		byte[] resultArray = new byte[150000];
		try {
			byte[] buffer = new byte[150000];
			int taotalCharactet = 0;
			int destPos = 0;

			while ((taotalCharactet = inputStream.read(buffer)) != -1) {
				System.arraycopy(buffer, 0, resultArray, destPos, taotalCharactet);
				destPos = destPos + taotalCharactet;
			}
			try {
				StringBuilder sb = new StringBuilder();
				sb.append((char)resultArray[220]).append((char)resultArray[221]);
				sb.append((char)resultArray[222]).append((char)resultArray[223]);
				System.out.println("接收到的socket响应代码：" + sb.toString());
				
				// 635C
				//if ((char) resultArray[220] == '6' && (char) resultArray[221] == '3' && (char) resultArray[222] == '5' && (char) resultArray[223] == 'C') {
				if ("635C".equals(sb.toString())) {
					rtuAlarmPush(resultArray);
				}
				// 520C 测试结果
//				else if ((char) resultArray[220] == '5' && (char) resultArray[221] == '2' && (char) resultArray[222] == '0' && (char) resultArray[223] == 'C') {
				else if ("520C".equals(sb.toString())) {
					CommonUtil.saveTestResultToBinaryFile(TEMP_FILE_PATH, TEMP_TEST_RESULT_FILE_NAME, resultArray);
					OTDRResult(resultArray);
				}
				// 521C 国内设备测试结果
				//else if ((char) resultArray[220] == '5' && (char) resultArray[221] == '2' && (char) resultArray[222] == '1' && (char) resultArray[223] == 'C') {
				else if ("521C".equals(sb.toString())) {
					CommonUtil.saveTestResultToBinaryFile(TEMP_FILE_PATH, TEMP_TEST_RESULT_FILE_NAME, resultArray);
					OTDRResult_china(resultArray);
				} 
				else {
					Map paramMap = planMapper.getSysParam("RECORD_UNPROCESSED_RESPONSE_RESULT");
					if (paramMap == null)
						return;
					boolean needRecordResponseResult = Boolean.valueOf(paramMap.get("PARAM_VALUE").toString());
					if (needRecordResponseResult) {
						CommonUtil.saveTestResultToBinaryFile(TEMP_FILE_PATH, TEMP_TEST_RESULT_FILE_NAME, resultArray);
    					// 输出未处理的命令响应结果
						sb.setLength(0);
    					sb.append("未处理的命令响应结果：");
    					for (int i=0; i<resultArray.length; i++)
    						sb.append((char)resultArray[i]);
    					System.out.println(sb.toString());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
