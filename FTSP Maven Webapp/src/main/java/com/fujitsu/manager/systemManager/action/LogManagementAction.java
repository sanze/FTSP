package com.fujitsu.manager.systemManager.action;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.IService.IMethodLog;
import com.fujitsu.IService.ISystemManagerService;
import com.fujitsu.abstractAction.DownloadAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.manager.reportManager.util.ReportExportExcel;
import com.fujitsu.manager.systemManager.model.LogModel;
import com.mongodb.DBObject;
import com.opensymphony.xwork2.ModelDriven;
public class LogManagementAction  extends DownloadAction implements ModelDriven<LogModel>{
	private static final long serialVersionUID = -1584948162970054220L;
	public LogModel logModel=new LogModel();
	@Resource
	public ISystemManagerService iLogManagerService;
	@Override
	public LogModel getModel(){
		  return logModel;
	}
	
	
	/**
	 * 根据条件收索日志列表
	 * @throws ParseException 
	 * @throws CommonException 
	 * 
	 */
	@IMethodLog(desc = "日志查询")
	public String searchLogList() throws CommonException, ParseException{
		//jsonParam:{userGroupId："20",userGroupName:"用户组", userId: "59",userName:"姓名",
		//startDate:"2013-12-19",endDate:"2013-12-19",logKeyword:"关键字"} 
		Map<String, Object> datas=iLogManagerService.getJournals(logModel,start,limit);
		//Map<String, Object>     total:100    rows: list<Map>   userGroupName,userName,startTime,logKeyword,logDescription
		resultObj = JSONObject.fromObject(datas);
		return RESULT_OBJ;
	}
	


	@IMethodLog(desc = "导出日志")
	@SuppressWarnings("unchecked")
	public String downloadLogManage() throws CommonException{
			 Map<String, Object> datas=null;
			try {
				datas = iLogManagerService.getJournals(logModel,start,limit);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			 List<DBObject> list = (List<DBObject>)datas.get("rows");
			 //获取隐藏的列
			 Object hiddenColumns=logModel.getHiddenColoumms();
			 List<String> hColumns=new ArrayList<String>();
			 if(hiddenColumns!=null && !"".equals((String)hiddenColumns)){
				String[] columns=((String)hiddenColumns).split(",");
				for(String c:columns){
					hColumns.add(c);
				}
			 }
			 
			 String destination = null;
			 SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
			 String myFlieName="log_management"+sdf.format(new Date());
			 ReportExportExcel ex=null;
			 ex = new ReportExportExcel(CommonDefine.PATH_ROOT+CommonDefine.EXCEL.TEMP_DIR,myFlieName,"日志管理",hColumns);
			 destination = ex.writeExcel(list,null,CommonDefine.EXCEL.LOG_MANAGE, false);
			 setFilePath(destination);
			 return RESULT_DOWNLOAD;
	}
}