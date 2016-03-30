package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface ResourceAuditMapper {
	/**
	 * @author fanguangming
	 */
	/** *************************查询******************************* */


	/**
	 * 查询资源网元数据
	 * @return
	 */
	public List<Map> getBaseNeData(@Param(value = "startTime") String startTime,
			@Param(value = "endTime") String endTime, @Param(value = "dataState") String dataState);
	
	/**
	 * 查询资源子架数据
	 * @return
	 */
	public List<Map> getBaseShelfData(@Param(value = "startTime") String startTime,
			@Param(value = "endTime") String endTime, @Param(value = "dataState") String dataState);
	
	/**
	 * 查询资源板卡全部有效数据
	 * @return
	 */
	public List<Map> getBaseUnitData(@Param(value = "startTime") String startTime,
			@Param(value = "endTime") String endTime, @Param(value = "dataState") String dataState);
	
	/**
	 * 查询资源端口数据
	 * @return
	 */
	public List<Map> getBasePtpData(@Param(value = "startTime") String startTime,
			@Param(value = "endTime") String endTime, @Param(value = "dataState") String dataState,
			@Param(value = "selectStart") int selectStart, @Param(value = "everySelectCnt") int everySelectCnt);
	
	/**
	 * 查询资源端口数据的记录数
	 * @return
	 */
	public int getBasePtpDataCount(@Param(value = "startTime") String startTime,
			@Param(value = "endTime") String endTime, @Param(value = "dataState") String dataState);
	
	/**
	 * 查询资源SDH交叉数据
	 * @return
	 */
	public List<Map> getBaseSDHData(@Param(value = "startTime") String startTime,
			@Param(value = "endTime") String endTime, @Param(value = "dataState") String dataState,
			@Param(value = "selectStart") int selectStart, @Param(value = "everySelectCnt") int everySelectCnt);
	
	/**
	 * 查询资源SDH交叉数据的记录数
	 * @return
	 */
	public int getBaseSDHDataCount(@Param(value = "startTime") String startTime,
			@Param(value = "endTime") String endTime, @Param(value = "dataState") String dataState);
	
	
	/**
	 * 查询资源OTN交叉数据
	 * @return
	 */
	public List<Map> getBaseOTNData(@Param(value = "startTime") String startTime,
			@Param(value = "endTime") String endTime, @Param(value = "dataState") String dataState,
			@Param(value = "selectStart") int selectStart, @Param(value = "everySelectCnt") int everySelectCnt);

	/**
	 * 查询资源OTN交叉数据的记录数
	 * @return
	 */
	public int getBaseOTNDataCount(@Param(value = "startTime") String startTime,
			@Param(value = "endTime") String endTime, @Param(value = "dataState") String dataState);

}