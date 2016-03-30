package com.fujitsu.IService;

import javax.jws.WebService;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;


/**
 * 
 * 接口名再考虑
 * @author xuxiaojun
 *
 */
@WebService
public interface IWSManagerService {

	/**
	 * @param routeId 测试路由id
	 * @param level
	 * @return
	 * @throws CommonException
	 */
//	@WebMethod 
//	@WebResult
	public CommonResult runTest(String routeId, String testParam, int level) throws CommonException;


}
