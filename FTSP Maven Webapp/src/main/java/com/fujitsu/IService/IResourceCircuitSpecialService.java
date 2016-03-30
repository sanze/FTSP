package com.fujitsu.IService;

import java.io.File;

import com.fujitsu.common.CommonException;

public interface IResourceCircuitSpecialService {
	/**
	 * 
	 * @param fileName 需要保存的文件名
	 * @param tempFile 需要读取文件对象
	 * @param type ctp转化类型
	 * @return
	 * @throws CommonException
	 */
	public boolean importResourceFile(String fileName,File tempFile,int type)throws CommonException;
}
