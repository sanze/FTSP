package com.fujitsu.model;

import globaldefs.NameAndStringValue_T;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fujitsu.common.DataCollectDefine;

/**
 * @author ZJL
 * @desc 根据性能文件头信息解析出各列对应的数据
 */
public class PmColumn {
	private final static String[] Col_EMS_Name=new String[]{"EMS Name"};
	private final static String[] Col_ME_Name=new String[]{"ME Name","MeName",/*贝尔*/"NE"};
	private final static String[] Col_PTP_Name=new String[]{"PTP Name","PmSource_Value",/*贝尔*/"TP"};
	private final static String[] Col_CTP_Name=new String[]{"CTP Name"};
	private final static String[] Col_LayerRate=new String[]{"Layer Rate"};
	private final static String[] Col_Granularity=new String[]{"Granularity","PmGranularity"};
	private final static String[] Col_RetrievalTime=new String[]{"Monitored Time","EndOrElapsedTime",/*贝尔*/"Period"};
	private final static String[] Col_Location=new String[]{"PMLocation",/*贝尔*/"Side"};
	private final static String[] Col_Parameter=new String[]{"PM Parameter","PmParameter"};
	private final static String[] Col_Value=new String[]{"Value","PMValue"};
	private final static String[] Col_Unit=new String[]{"Unit","unit"};
	private final static String[] Col_Status=new String[]{"Status"};
	private final static String[] Col_ParameterValue=new String[]{/*贝尔*/"BBE","ES","OFS","SES","UAS","NEUAS","LOD"};
	public Integer EMS_Name;
	public Integer ME_Name;
	public Integer PTP_Name;
	public Integer CTP_Name;
	public Integer LayerRate;
	public Integer Granularity;
	public Integer RetrievalTime;
	public Integer Location;
	public Integer Parameter;
	public Integer Value;
	public Integer Unit;
	public Integer Status;
	/*贝尔*/
	public Map<String,Integer> ParameterValue=new HashMap<String,Integer>();
	public static PmColumn getPmColumn(String line){
		String [] columns=line.split(",");
		PmColumn pmColumn=new PmColumn();
		for(int i=0,l=columns.length;i<l;i++){
			if(Arrays.asList(Col_EMS_Name).contains(columns[i]))
				pmColumn.EMS_Name=i;
			else if(Arrays.asList(Col_ME_Name).contains(columns[i]))
				pmColumn.ME_Name=i;
			else if(Arrays.asList(Col_PTP_Name).contains(columns[i]))
				pmColumn.PTP_Name=i;
			else if(Arrays.asList(Col_CTP_Name).contains(columns[i]))
				pmColumn.CTP_Name=i;
			else if(Arrays.asList(Col_LayerRate).contains(columns[i]))
				pmColumn.LayerRate=i;
			else if(Arrays.asList(Col_Granularity).contains(columns[i]))
				pmColumn.Granularity=i;
			else if(Arrays.asList(Col_RetrievalTime).contains(columns[i]))
				pmColumn.RetrievalTime=i;
			else if(Arrays.asList(Col_Location).contains(columns[i]))
				pmColumn.Location=i;
			else if(Arrays.asList(Col_Parameter).contains(columns[i]))
				pmColumn.Parameter=i;
			else if(Arrays.asList(Col_Value).contains(columns[i]))
				pmColumn.Value=i;
			else if(Arrays.asList(Col_Unit).contains(columns[i]))
				pmColumn.Unit=i;
			else if(Arrays.asList(Col_Status).contains(columns[i]))
				pmColumn.Status=i;
			else if(Arrays.asList(Col_ParameterValue).contains(columns[i]))
				pmColumn.ParameterValue.put(columns[i], i);
		}
		return pmColumn;
	}
	public static List<NameAndStringValue_T> constructPtpName(String ptpString){
		List<NameAndStringValue_T> name= new ArrayList<NameAndStringValue_T>();
		if(ptpString==null||ptpString.isEmpty()) return name;
		if(ptpString.startsWith(DataCollectDefine.COMMON.EQUIPMENT)){//UNIT
			int hold=ptpString.indexOf(":");
			int unit=ptpString.lastIndexOf("/");
			NameAndStringValue_T holdName=new NameAndStringValue_T(
					DataCollectDefine.COMMON.EQUIPMENT_HOLDER,
					ptpString.substring(hold+1,unit));
			name.add(holdName);
			NameAndStringValue_T unitName=new NameAndStringValue_T(
					DataCollectDefine.COMMON.EQUIPMENT,
					ptpString.substring(unit+1));
			name.add(unitName);
		}else if(ptpString.startsWith(DataCollectDefine.COMMON.FTP)){//FTP
			int ptp=ptpString.indexOf(":");
			NameAndStringValue_T holdName=new NameAndStringValue_T(
					DataCollectDefine.COMMON.FTP,
					ptpString.substring(ptp+1));
			name.add(holdName);
		}else if(ptpString.startsWith("/")){//U31PTP 或 E300
			/*if(ptpString.contains(DataCollectDefine.ZTE.ZTE_CHANEL_NO)){
				
			}else */
			if(ptpString.contains(DataCollectDefine.ZTE.ZTE_PHYSICAL_PORT)||
					ptpString.contains(DataCollectDefine.COMMON.PORT)){
				NameAndStringValue_T holdName=new NameAndStringValue_T(
						DataCollectDefine.COMMON.PTP,
						ptpString);
				name.add(holdName);
			}else if(ptpString.matches(".*/"+DataCollectDefine.COMMON.SLOT+"[^/]*/[^/]*")){//UNIT
				int hold=-1;
				int unit=ptpString.lastIndexOf("/");
				NameAndStringValue_T holdName=new NameAndStringValue_T(
						DataCollectDefine.COMMON.EQUIPMENT_HOLDER,
						ptpString.substring(hold+1,unit));
				name.add(holdName);
				NameAndStringValue_T unitName=new NameAndStringValue_T(
						DataCollectDefine.COMMON.EQUIPMENT,
						ptpString.substring(unit+1));
				name.add(unitName);
			}else{//HOLDER
				NameAndStringValue_T holdName=new NameAndStringValue_T(
						DataCollectDefine.COMMON.EQUIPMENT_HOLDER,
						ptpString);
				name.add(holdName);
			}
		}else{
			NameAndStringValue_T holdName=new NameAndStringValue_T(
					DataCollectDefine.COMMON.PTP,
					ptpString);
			name.add(holdName);
		}
		return name;
	}
	public static List<NameAndStringValue_T> constructCtpName(String ctpString){
		List<NameAndStringValue_T> name= new ArrayList<NameAndStringValue_T>();
		if(ctpString==null||ctpString.isEmpty()) return name;
		if(ctpString.startsWith("/")){//CTP
			NameAndStringValue_T ctp=new NameAndStringValue_T(
					DataCollectDefine.COMMON.CTP,
					ctpString);
			name.add(ctp);
		}
		return name;
	}
	public static String getProperty(String nameString,String property){
		String value=null;
		if(nameString.contains(property)){
			value=nameString.replaceAll(".*"+property+"=", "");
			value=value.replaceAll("/.*", "");
		}
		return value;
	}
	public boolean inValid(){
		return (ME_Name==null||PTP_Name==null||Granularity==null
				||RetrievalTime==null||((Parameter==null||Value==null)&&ParameterValue.isEmpty())); 
	}
	public boolean basicInValid(List<String> colValues){
		return (ME_Name==null||PTP_Name==null||Granularity==null||RetrievalTime==null||
			isEmpty(colValues.get(ME_Name))||/*isEmpty(colValues.get(PTP_Name))|| 会有网元级性能存在*/
			isEmpty(colValues.get(Granularity))||isEmpty(colValues.get(RetrievalTime)));
	}
	public boolean measurementInValid(List<String> colValues){
		return (Parameter==null||Value==null||
			isEmpty(colValues.get(Parameter))||isEmpty(colValues.get(Value)))&&ParameterValue.isEmpty();
	}
	public static boolean isEmpty(String value){
		return value==null||value.trim().isEmpty();
	}
	public static boolean equals(NameAndStringValue_T[] arg1,NameAndStringValue_T[] arg2){
		if(arg1==null&&arg2==null)
			return true;
		if(arg1==null||arg2==null||
			arg1.length!=arg2.length)
			return false;
		for(int i=0,l=arg1.length;i<l;i++){
			if(!arg1[i].name.equals(arg2[i].name)||
				!arg1[i].value.equals(arg2[i].value))
				return false;
		}
		return true;
	}
	public static boolean equals(String arg1,String arg2){
		if(arg1==null&&arg2==null)
			return true;
		if(arg1==null||arg2==null)
			return false;
		return arg1.equals(arg2);
	}
}

