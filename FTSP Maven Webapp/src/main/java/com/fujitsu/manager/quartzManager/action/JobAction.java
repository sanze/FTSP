package com.fujitsu.manager.quartzManager.action;

import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.IService.IMethodLog;
import com.fujitsu.IService.IQuartzManagerService;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;


public class JobAction extends AbstractAction{

	@Resource
	public IQuartzManagerService quartzManagerService;
	public int jobType;
	public int jobID;
	public String jobTime;
	public String jobName;
	public String triggerName;
	public int jobFlag;
	/**
	 * 登入
	 * @throws CommonException 
	 */
	@IMethodLog(desc = "Test Quartz! - newJob")
	@SuppressWarnings("rawtypes")
	public String newJob(){
		try {
			System.err.println("newJob Action Activated！");
			
			quartzManagerService.addJob(jobType, jobID, TestJob.class, jobTime);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("success");
			//quartzManagerService.
		} catch (Exception e) {
			e.printStackTrace();
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("Exception @ " + e.getMessage());
		}
		resultObj = JSONObject.fromObject(result);
		System.out.println(resultObj);
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "Test Quartz! - getInfo")
	public String getInfo(){
		try {
			System.out.println("-----Back door @ getInfo-----");
			Map<String,Object> rv = quartzManagerService.getJobInfo(jobType, jobID);
			System.out.println("");
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("success");
			resultObj = JSONObject.fromObject(rv);
			//quartzManagerService.
		} catch (Exception e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("Exception @ " + e.getMessage() + "\r\nStacks:\r\n" + e.getStackTrace());
			resultObj = JSONObject.fromObject(result);
		}
		
		System.out.println(resultObj);
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "Test Quartz! - getAllJobInfo")
	public String getAllJobInfo(){
		try {
			Map<String,Object> rv = quartzManagerService.getAllJobInfo();
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("success");
			resultObj = JSONObject.fromObject(rv);
			//quartzManagerService.
		} catch (Exception e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("Exception @ " + e.getMessage() + "\r\nStacks:\r\n" + e.getStackTrace());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}

	@IMethodLog(desc = "Test Quartz! - ctrl")
	public String ctrl(){
		try {
			System.out.println("-----Back door @ ctrl-----");
//			jobType = CommonDefine.QUARTZ.JOB_TESTQUARTZ;
			if(jobType>0)
				quartzManagerService.ctrlJob(jobType, jobID, jobFlag);
			else
				quartzManagerService.ctrlJob(jobName, triggerName, jobFlag);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("success");
			//quartzManagerService.
		} catch (Exception e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("Exception @ " + e.getMessage() + "\r\nStacks:\r\n" + e.getStackTrace());
		}
		resultObj = JSONObject.fromObject(result);
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "Test Quartz! - modify time")
	public String modTime(){
		try {
//			System.err.println("modTime Activated！");
			System.out.println("-----Back door @ modTime-----");
			if(jobType>0)
				quartzManagerService.modifyJobTime(jobType, jobID, jobTime);
			else
				quartzManagerService.modifyJobTime(jobName, triggerName, jobTime);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("success");
			//quartzManagerService.
		} catch (Exception e) {
			e.printStackTrace();
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("Exception @ " + e.getMessage());
		}
		resultObj = JSONObject.fromObject(result);
		System.out.println(resultObj);
		return RESULT_OBJ;
	}

	public String getJobTime() {
		return jobTime;
	}

	public void setJobTime(String jobTime) {
		this.jobTime = jobTime;
	}

	public int getJobFlag() {
		return jobFlag;
	}
	public void setJobFlag(int jobFlag) {
		this.jobFlag = jobFlag;
	}
	public int getJobType() {
		return jobType;
	}
	public void setJobType(int jobType) {
		this.jobType = jobType;
	}
	public int getJobID() {
		return jobID;
	}
	public void setJobID(int jobID) {
		this.jobID = jobID;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getTriggerName() {
		return triggerName;
	}
	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}
}
