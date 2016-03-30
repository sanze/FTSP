package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;


/**
 * @author xuxiaojun
 *
 */
public interface CommonMapper {
	
	/**
	 * 	获取整表数据
	 * @param tableName 表名
	 * @return
	 */
	public List selectTable(
			@Param(value = "tableName") String tableName,
			@Param(value = "startNumber") Integer startNumber,
			@Param(value = "pageSize") Integer pageSize);
	
	/**
	 * 	获取整表数据count值
	 * @param tableName 表名
	 * @return
	 */
	public int selectTableCount(
			@Param(value = "tableName") String tableName);
	
	/**
	 * 	按id获取一条数据
	 * @param tableName 表名
	 * @param idName id字段名
	 * @param id id值
	 * @return
	 */
	public Map selectTableById(
			@Param(value = "tableName") String tableName,
			@Param(value = "idName") String idName,
			@Param(value = "id") int id);
	
	/**
	 * 按字段获取数据
	 * @param tableName 表名
	 * @param columnName 字段名
	 * @param columnValue 字段值
	 * @return
	 */
	public Map selectTableByColumn(
			@Param(value = "tableName") String tableName,
			@Param(value = "columnName") String columnName,
			@Param(value = "columnValue") String columnValue);
	
	/**
	 * 	按id获取数据列表
	 * @param tableName 表名
	 * @param idName id字段名
	 * @param id id值
	 * @return
	 */
	public List selectTableListById(
			@Param(value = "tableName") String tableName,
			@Param(value = "idName") String idName,
			@Param(value = "id") int id,
			@Param(value = "startNumber") Integer startNumber,
			@Param(value = "pageSize") Integer pageSize);
	
	/**
	 * 	按id获取数据列表count值
	 * @param tableName 表名
	 * @param idName id字段名
	 * @param id id值
	 * @return
	 */
	public int selectTableListCountById(
			@Param(value = "tableName") String tableName,
			@Param(value = "idName") String idName,
			@Param(value = "id") int id);
	
	/**
	 * 	按id获取数据列表
	 * @param tableName 表名
	 * @param idName id字段名
	 * @param id id值
	 * @return
	 */
	public List<Map<String, Object>> selectTableListByCol(
			@Param(value = "tableName") String tableName,
			@Param(value = "colName") String colName,
			@Param(value = "colValue") Object colValue,
			@Param(value = "startNumber") Integer startNumber,
			@Param(value = "pageSize") Integer pageSize);
	
	/**
	 * 查询数据
	 * @param tableName 表名
	 * @param colNames 字段名
	 * @param colValues 字段值
	 */
	public List<Map<String,Object>> selectTableListByNVList(
			@Param(value = "tableName") String tableName,
			@Param(value = "colNames") List<String> colNames,
			@Param(value = "colValues") List<Object> colValues,
			@Param(value = "startNumber") Integer startNumber,
			@Param(value = "pageSize") Integer pageSize);
	
	/**
	 * 查询数据
	 * @param tableName 表名
	 * @param colNames 字段名
	 * @param colValues 字段值
	 */
	public int selectTableListCountByNVList(
			@Param(value = "tableName") String tableName,
			@Param(value = "colNames") List<String> colNames,
			@Param(value = "colValues") List<Object> colValues);
	
	/**
	 * 	按id获取数据列表count值
	 * @param tableName 表名
	 * @param idName id字段名
	 * @param id id值
	 * @return
	 */
	public int selectTableListCountByCol(
			@Param(value = "tableName") String tableName,
			@Param(value = "colName") String colName,
			@Param(value = "colValue") Object colValue);
	
	/**
	 * 获取当前最大Id值
	 * @param tableName
	 * @param colName
	 * @return
	 */
	public Integer selectMaxIdFromTable(
			@Param(value = "dbName") String dbName,
			@Param(value = "tableName") String tableName);
}