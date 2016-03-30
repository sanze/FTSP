package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import com.fujitsu.serviceImpl.VEMS.VEMSSession;
import com.fujitsu.util.IDLSerializableUtil;

public class GetAndPrintData {

	protected static boolean DirByProductName = true;
	protected static String basePath;

	public final static String FILEPATH = System.getProperty("user.dir")+"/OutPutFiles/";
	
	public final static String CFG_FILEPATH = "config.properties";
//	protected final static String CFG_FILEPATH_HW = "config_HW.properties";
//	protected final static String CFG_FILEPATH_ZTE = "config_ZTE.properties";
//	protected final static String CFG_FILEPATH_LUCENT = "config_LUCENT.properties";
//	protected final static String CFG_FILEPATH_FIM = "config_FIM.properties";
	
	protected final static String FILEPATH_HW = VEMSSession.FACTORY_HW;
	protected final static String FILEPATH_ZTE = VEMSSession.FACTORY_ZTE;
	protected final static String FILEPATH_LUCENT = VEMSSession.FACTORY_LUCENT;
	protected final static String FILEPATH_FIM = VEMSSession.FACTORY_FIBERHOME;
	protected final static String FILEPATH_ALU = VEMSSession.FACTORY_ALU;
	
	protected final static String ENCODE = "encode";

	protected final static String NAME = "corbaname";
	protected final static String PASSWORD = "corbapassword";
	protected final static String IP = "corbaip";
	protected final static String INTERNAL_EMS_NAME = "internalEmsName";
	protected final static String NMS_TYPE = "nmsType";
	protected final static String NMS_NAME = "nmsname";
	protected final static String PORT = "port";

	protected final static String FTPIP = "ftpIp";
	protected final static String FTPPORT = "ftpPort";
	protected final static String FTPUSERNAME = "ftpUserName";
	protected final static String FTPPASSWORD = "ftpPassword";
	protected final static String STARTTIME = "startTime";
	protected final static String ENDTIME = "endTime";
	
	public final static String EMS = "EMS";
	public final static String COLLECT_ALL_NE = "collectAllNe";
	public final static String INTERVALTIME = "intervalTime";
	public final static String EXPORTEXCEL = "exportExcel";
	public final static String EXPORTOBJECT = "exportObject";
	public final static String EXPORTTXT = "exportTxt";
	protected final static String SELECTED_NE_ID = "selectedNeId";
	protected final static String ENABLE = "true";
	 
	protected final static String IS_CONNECTION_RATE_LIST_NULL = "isConnectionRatelistNull";
	
	protected static String neName;
	protected String neprop;

	protected static int SheetNumber = 0;

	protected static Properties p;

	protected static String corbaname;
	protected static String corbapassword;
	protected static String nmsname;
	protected static String corbaip;
	protected static String port;
	protected static int nmsType;
	protected static String encode;
	protected static String internalEmsName;
	
	protected static String ftpIp;
	protected static int ftpPort;
	protected static String userName;
	protected static String password;
	protected static String startTime;
	protected static String endTime;
	
	protected static boolean isConnectionRatelistNull;
	
	protected static long intervalTime = 0;
	protected boolean needToCollectAll;
	protected static boolean exportExcel;
	protected static boolean exportObject;
	protected static boolean exportTxt;
	
	protected static boolean checkProperty(String property){
		try{
			if (((String) p.get(property)).equals(ENABLE)) {
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			return false;
		}
	}

	// 转码
	protected String Stringformat(String value) {
		try {
			return new String(value.getBytes("ISO8859_1"), encode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public void writeObject(Object object,String filePath,String fileName){
		if(exportObject)
			IDLSerializableUtil.writeObject(object, FILEPATH+filePath+"/", fileName);
		//休息一下再采
		haveARest();
	}
	
	protected Object readObject(String filePath,String fileName){
		Object xxx = IDLSerializableUtil.readObject(FILEPATH+filePath+"/", fileName);
		return xxx;
	}
	
	public void writeObject(Object object,String filePath,String fileName,Class helper){
		if(exportObject)
			IDLSerializableUtil.writeObject(object, FILEPATH+filePath+"/", fileName, helper);
		//休息一下再采
		haveARest();
	}
	
	protected Object readObject(String filePath,String fileName,Class helper){
		Object xxx = IDLSerializableUtil.readObject(FILEPATH+filePath+"/", fileName, helper);
		return xxx;
	}
	
	public static void writeObject(Object object,String filePath,String CMD,HashMap<String, Class> IDLObjectHelper){
		if(exportObject)
			IDLSerializableUtil.writeObject(object, FILEPATH+filePath+"/", CMD,IDLObjectHelper.get(CMD.split("_")[0]));
		//休息一下再采
		haveARest();
	}
	
	protected Object readObject(String filePath,String CMD,HashMap<String, Class> IDLObjectHelper){
		Object xxx = IDLSerializableUtil.readObject(FILEPATH+filePath+"/", CMD,IDLObjectHelper.get(CMD.split("_")[0]));
		return xxx;
	}
	
	/**
	 * 防网管挂,歇一歇
	 * 
	 */
	protected static void haveARest(){
		try {
			Thread.sleep(intervalTime*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 保存网元列表
	 * 
	 * @param neList
	 */
	protected void saveNeList(Set neList){
		BufferedWriter writer;
		File targetFile = new File(VEMSSession.NE_DIR);
		try {
			if(!neList.isEmpty()){
				if(targetFile.exists()){
					// 如果存在该文件,删除
					targetFile.delete();
				}
				// 新建文件
				targetFile.createNewFile();
				writer = new BufferedWriter(new FileWriter(targetFile));
				String out = neList.toString();
				writer.write(out, 1, out.length()-2);
				writer.close();
			}else{
				// 没有没采集的网元了,删除
				targetFile.delete();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	

}
