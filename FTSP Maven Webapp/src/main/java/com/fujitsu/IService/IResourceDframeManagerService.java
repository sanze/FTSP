package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;
import com.fujitsu.common.Result;

public interface IResourceDframeManagerService {  
	//初始化ODF子架信息
	/**
	 * @@@分权分域到网元@@@ 
	 */
	public Map<String,Object> getOdfList(Map<String,String> map,int start,int limit,int userId)throws CommonException;
	//查询条件用途的combo
	public Map<String,Object> getUseableList() throws CommonException;
	//查询条件用途光缆名称的combo
	public Map<String,Object> getCableNameList(String value,int start ,int limit)throws CommonException;
	//光纤芯号的combo
	public Map<String,Object> getFiberNameList(int value)throws CommonException; 
	//保存ODF子架
	public Result addOdfs(List<Map> odfList,int value)throws CommonException;
	//删除ODF子架
	public Result deleteOdfs(List<Map> sourceIds)throws CommonException; 
	//修改ODF子架
	public Result modifyODF(Map<String,String> map)throws CommonException; 
	//获取关联的ODF子架
	public Map<String,Object> getRelateOdfList(int value)throws CommonException; 
	//ODF关联ODF子架
	public Result associateOdfWithOdf(List<Map> sourceIds,List<Map> targetIds)throws CommonException; 
	//ODF关联端口
	public Result associateOdfWithPtp(List<Map> sourceIds,List<Map> targetIds)throws CommonException; 
	//ODF删除关联
	public Result deleteOdfRelate(List<Map> sourceIds)throws CommonException;  
	//ODF导出
	public CommonResult odfExport(Map<String,String> map)throws CommonException;  
//------------------------------------------------------------------------------------------------------------
	//初始化DDF子架信息
	/**
	 * @@@分权分域到网元@@@ 
	 */
	public Map<String,Object> getDdfList(Map<String,String> map,int start,int limit,int userId)throws CommonException; 
	//查询条件DDF用途的combo
	public Map<String,Object> getDdfUseableList() throws CommonException;
	//保存DDF子架
	public Result addDdfs(List<Map> odfList,int value)throws CommonException;
	//删除DDF子架
	public Result deleteDdfs(List<Map> sourceIds)throws CommonException; 
	//修改DDF子架
	public Result modifyDDF(Map<String,String> map)throws CommonException; 
	//DDF关联端口
	public Result associateDdfWithPtp(List<Map> sourceIds,List<Map> targetIds)throws CommonException; 
	//ODF删除关联
	public Result deleteDdfRelate(List<Map> sourceIds)throws CommonException; 
	//获取跳线管理的DDF子架
	public Map<String,Object> getRelateDdfList(int value)throws CommonException;  
	//DDF设置跳线
	public Result associateDdfWithDdf(List<Map> sourceIds,List<Map> targetIds)throws CommonException;  
	//DDF删除跳线
	public Result deleteDdfJumpLine(List<Map> sourceIds)throws CommonException;  
	//DDF导出
	public CommonResult ddfExport(Map<String,String> map)throws CommonException;  
}
