package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;
import com.fujitsu.manager.systemManager.model.AuthRegion;
import com.fujitsu.manager.systemManager.model.AuthTreeModel;

public interface IAuthRegionManageService {
	/**
	 * Method name: getAuthRegionData <BR>
	 * Description: 获取权限域数据<BR>
	 * Remark: 2013-12-02<BR>
	 * @author wuchao
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> searchAuthDomain(int startNumber, int pageSize) throws CommonException;
	/**
	 * Method name: saveDemoTest <BR>
	 * Description: 保存Demo_test表单元素<BR>
	 * Remark: 2013-11-15<BR>
	 * @author hg
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> saveAuthRegionData(AuthRegion authRegion) throws CommonException;
	/**
	 * Method name: deleteDemoTest <BR>
	 * Description: 删除demo_test一行记录<BR>
	 * Remark: 2013-12-15<BR>
	 * @author hg
	 * @return String<BR>
	 */
	public Map<String, Object> deleteAuthRegions(AuthRegion authRegion) throws CommonException;
	/**
	 * Method name: getDeviceTreeNodes <BR>
	 * Description: 树状数据<BR>
	 * Remark: 2013-12-07<BR>
	 * @author wuchao
	 * @return String<BR>
	 */
	public List<AuthTreeModel> getAuthTreeNodes(AuthRegion authRegion);
	/**
	 * Method name: getMenuAuthsByAuthDomainId <BR>
	 * Description: 获取权限域对应的权限<BR>
	 * Remark: 2013-12-10<BR>
	 * @author wuchao
	 * @return String<BR>
	 */
	public List<Map> getMenuAuthsByAuthDomainId(AuthRegion authRegion) throws CommonException;
	public Map validateUserAuthDomainName(AuthRegion authRegion);
	
}
