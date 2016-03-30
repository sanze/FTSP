package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface ResourceDframeManagerMapper {
	
//--------------------------------------ODF--------------------------------------------------
	//初始化ODF子架信息时，获取记录数量，便于分页
	public int countOdfList( @Param(value = "conMap") Map<String,String> conMap); 
	//初始化ODF子架信息
	public List<Map> getOdfList( @Param(value = "conMap") Map<String,String> conMap,
			@Param(value = "startNumber") int startNumber, 
			@Param(value = "pageSize") int pageSize); 
	//用途的索引
	public List<Map> getUseableList();  
	//光缆的索引
	public List<Map> getCableNameList(@Param(value = "CABLE_NAME") String CABLE_NAME,
			@Param(value = "startNumber") int startNumber, 
			@Param(value = "pageSize") int pageSize); 
	//获取查询数量，分页显示
	public int countCableNameList(@Param(value = "CABLE_NAME") String CABLE_NAME); 
	//光纤芯号的索引
	public List<Map> getFiberNoList(@Param(value = "RESOURCE_CABLE_ID")int RESOURCE_CABLE_ID);  
	//判断同一机房内是否有同名的端子号
	public int judgeOdf(@Param (value = "odfNo")String odfNo,
			@Param (value = "RESOURCE_ROOM_ID") int RESOURCE_ROOM_ID);
	//获取光纤表的fiberId
	public int getResourceFiberID(@Param (value = "fiberNo")String fiberNo,
			@Param (value = "cableId") int cableId);
	//增加ODF表
	public void insertOdf(@Param (value = "odf") Map odf,
			@Param (value = "RESOURCE_ROOM_ID") int RESOURCE_ROOM_ID,
			@Param (value = "fiberId")int fiberId);
	//删除ODF表
	public void deleteOdf(@Param (value = "odfId") int odfId);
	//修改ODF表
	public void updateOdf(@Param (value = "conMap") Map conMap);
	//修改ODF表,关联ODF,端口
	public void updateOdfRelate(@Param (value = "conMap") Map conMap); 
	//修改ODF表,删除关联ODF,端口
	public void updateOdfDelete(@Param (value = "conMap") Map conMap); 
	
	//获取关联的ODF子架
	public List<Map> getRelateOdfList(@Param (value = "roomId") int value);   
	//获取关联的ODF子架
	public Map getOdfData(@Param (value = "odfId") int odfId);  
	//判断端口是否被占用
	public List<Map> judgePortOccupation(@Param (value = "ptpId") String ptpId); 
	//判断odf端子是否被占用
	public String judgeOdfOccupation(@Param (value = "odfId") String odfId);
	
//--------------------------------------DDF--------------------------------------------------
	//初始化DDF子架信息时，获取记录数量，便于分页
	public int countDdfList( @Param(value = "conMap") Map<String,String> conMap); 
	//初始化DDF子架信息
	public List<Map> getDdfList( @Param(value = "conMap") Map<String,String> conMap,
			@Param(value = "startNumber") int startNumber, 
			@Param(value = "pageSize") int pageSize); 
	//DDF用途的索引
	public List<Map> getDdfUseableList();  
	//判断同一机房内是否有同名的端子号
	public int judgeDdf(@Param (value = "ddfNo")String ddfNo,
			@Param (value = "RESOURCE_ROOM_ID") int RESOURCE_ROOM_ID);
	//增加DDF表
	public void insertDdf(@Param (value = "ddf") Map ddf,
			@Param (value = "RESOURCE_ROOM_ID") int RESOURCE_ROOM_ID);
	//删除DDF表
	public void deleteDdf(@Param (value = "ddfId") int ddfId);  
	//修改DDF表
	public void updateDdf(@Param (value = "conMap") Map conMap);
	//判断端口是否被占用
	public List<Map> judgePortDDFOccupation(@Param (value = "ptpId") String ptpId); 
	//获取关联的DDF子架
	public Map getDdfData(@Param (value = "ddfId") int ddfId);  
	//获取关联的DDF子架
	public List<Map> getRelateDdfList(@Param (value = "roomId") int value);   
	//判断ddf端子是否被占用
	public String judgeDdfOccupation(@Param (value = "ddfId") String ddfId);
	//关联DDF子架
	public void updateDdfRelate(@Param (value = "conMap") Map conMap); 
	//删除关联DDF子架
	public void updateDdfDelete(@Param (value = "conMap") Map conMap); 
	//跳线管理DDF子架
	public void updateDdfJumpLine(@Param (value = "conMap") Map conMap); 
	//删除跳线DDF子架
	public void updateDelDdfJumpLine(@Param (value = "conMap") Map conMap);  
}
