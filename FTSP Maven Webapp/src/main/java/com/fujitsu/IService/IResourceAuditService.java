package com.fujitsu.IService;

import com.fujitsu.common.CommonException;

/**
 * 2015/08/24
 * @author fanguangming
 *
 */
public interface IResourceAuditService {
	/**
	 * 综合资源系统向FTSP请求EMS数据
	 * @param startTime [YYYYMMDDHHMMSS]
	 * @param endTime [YYYYMMDDHHMMSS]
	 * @param dataType
	 * @param serialNumber
	 * @return true:成功/false:失败
	 */
    public boolean requestEmsData(String startTime, String endTime, int dataType, String serailNo) throws CommonException;
		
}
