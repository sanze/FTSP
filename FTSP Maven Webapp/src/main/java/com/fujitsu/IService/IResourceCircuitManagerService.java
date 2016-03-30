package com.fujitsu.IService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import jxl.read.biff.BiffException;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;

public interface IResourceCircuitManagerService {

	/**
	 * 获取资源网元的对应关系
	 * 
	 * @param map
	 * @param startNumber
	 * @param pageSize
	 * @return
	 */
	public Map<String, Object> getNeRelation(Map map, int start, int limit)
			throws CommonException;

	/**
	 * 修改资源网元对应关系
	 * 
	 * @param list
	 * @return
	 * @throws CommonException
	 */
	public CommonResult modifyResourceNe(List<Map> list) throws CommonException;

	/**
	 * 删除网元关系数据
	 * 
	 * @param list
	 * @throws CommonException
	 */
	public void deleteResourceNe(List<Map> list) throws CommonException;

	/**
	 * 新增网元关系
	 * 
	 * @param map
	 * @throws CommonException
	 */
	public CommonResult addResourceNe(Map map) throws CommonException;

	/**
	 * 查询资源电路
	 * 
	 * @param resCirName
	 * @param start
	 * @param limit
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> selectResourceCircuit(String resCirName,
			int start, int limit) throws CommonException;

	/**
	 * 统计资源稽核结果
	 * 
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> resultCount() throws CommonException;

	/**
	 * 根据稽核电路id和路径编号 查询路由信息
	 * 
	 * @param resCirId
	 * @param routeNum
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> selectResCirRoute(String resCirId,
			String routeNum) throws CommonException;

	/**
	 * 查询单条稽核电路信息
	 * 
	 * @param resCirId
	 * @return
	 * @throws CommonException
	 */
	public Map getSingleCir(String resCirId) throws CommonException;

	/**
	 * 根据稽核id 查询ftsp电路的路径数
	 * 
	 * @param resCirId
	 * @return
	 * @throws CommonException
	 */
	public Map getFtspRouteNumber(String resCirId) throws CommonException;

	/**
	 * 获取电路的路由信息
	 * 
	 * @param resCirId
	 * @return
	 * @throws CommonException
	 */
	public Map selectCircuitRoute(String resCirId) throws CommonException;

	/**
	 * 电路比对
	 * 
	 * @param list
	 * @return
	 * @throws CommonException
	 */
	public Map compareCircuit(List<Map> list) throws CommonException;

	/**
	 * 上传网元对应关系
	 * 
	 * @param uploadFile
	 * @param fileName
	 * @param path
	 * @return
	 * @throws CommonException
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws BiffException
	 */
	public Map UploadcheckNe(File uploadFile, String fileName, String path)
			throws CommonException;

	/**
	 * 导入电路工单
	 * 
	 * @param uploadFile
	 * @param fileName
	 * @param path
	 * @return
	 * @throws CommonException
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws BiffException
	 */
	public Map UploadResCir(File uploadFile, String fileName, String path)
			throws CommonException;
	
	/**
	 * 导入南昌华为网管数据
	 * 
	 * @param uploadFile
	 * @param fileName
	 * @param path
	 * @return
	 * @throws CommonException
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws BiffException
	 */
	public Map UploadNc(File uploadFile, String fileName, String path)
			throws CommonException;
	
	/**
	 * 导入南昌华为网管数据
	 * 
	 * @param uploadFile
	 * @param fileName
	 * @param path
	 * @return
	 * @throws CommonException
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws BiffException
	 */
	public Map UploadNcWDM(File uploadFile, String fileName, String path)
			throws CommonException;
	

}
