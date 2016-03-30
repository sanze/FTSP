package com.fujitsu.manager.systemManager.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.dao.mysql.DeviceRegionManageMapper;
import com.fujitsu.manager.systemManager.model.DeviceRegion;
import com.fujitsu.manager.systemManager.service.DeviceRegionManageService;
import com.fujitsu.manager.systemManager.util.TypeChangeUtil;

@Service
@Transactional(rollbackFor = Exception.class)
public class DeviceRegionManageImpl extends DeviceRegionManageService{
	
	@Resource
	private DeviceRegionManageMapper deviceRegionManageMapper;
//	@Autowired
//	private Mongo mongo;
//	public Mongo getMongo() {
//		return mongo;
//	}
//	public void setMongo(Mongo mongo) {
//		this.mongo = mongo;
//	}

	@Override
	public Map<String, Object> searchDeviceDomain(int startNumber, int pageSize) {		
		Map map=new HashedMap();
		Map returnMap=new HashedMap();
		List<Map> enigneerList = new ArrayList<Map>();
		int total=deviceRegionManageMapper.countDeviceRegionDataList(map);
		map.put("startNumber", startNumber);
		map.put("pageSize", pageSize);
		enigneerList=deviceRegionManageMapper.selectDeviceRegionDataList(map);
		returnMap.put("rows", enigneerList);
		returnMap.put("total", total);
		return returnMap;
	}
	/**
	 * Method name: saveDeviceRegion <BR>
	 * Description: 保存设备域<BR>
	 * Remark: 2013-12-07<BR>
	 * @author wuchao
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> saveDeviceRegion(DeviceRegion deviceRegion){
		Map m = new HashMap();
//		try{
//			if(deviceRegion.getId().equals("0")){
//				deviceRegionManageMapper.insert(deviceRegion);
//				Integer key=Integer.parseInt(deviceRegion.getId());
//				if(deviceRegion.getNeList()!=null){
//					for(int i=0;i<deviceRegion.getNeList().size();i++){
//						String s=deviceRegion.getNeList().get(i);
//						if(s!=null && !"".equals(s)){
//							deviceRegion.setNeId(s);
//							deviceRegionManageMapper.insertDeviceRegionRefNe(deviceRegion);
//						}
//					}
//				}
//			}else{
//				deviceRegionManageMapper.update(deviceRegion);
//				//删除设备域关联的设备
//				deviceRegionManageMapper.deleteDeviceRegionRefNe(deviceRegion);
//				if(deviceRegion.getNeList()!=null){
//					for(int i=0;i<deviceRegion.getNeList().size();i++){
//						String s=deviceRegion.getNeList().get(i);
//						if(s!=null && !"".equals(s)){
//							deviceRegion.setNeId(deviceRegion.getNeList().get(i));
//							deviceRegionManageMapper.insertDeviceRegionRefNe(deviceRegion);
//						}
//					}
//				}
//			}
//			m.put("success", true);
//			m.put("msg", "保存成功！");
//		}catch(Exception e){
//			e.printStackTrace();
//			m.put("success", false);
//			m.put("msg", "保存失败！");
//		}
		return m;
	}
	
	/**
	 * Method name: deleteAuthDevice <BR>
	 * Description: 删除设备域<BR>
	 * Remark: 2013-12-02<BR>
	 * @author wuchao
	 * @return String<BR>
	 */
	@Override
	public boolean deleteDeviceDomain(List<Integer> lists) {
		DeviceRegion deviceRegion=new DeviceRegion();
		try{
			for(Integer i:lists){
				//先删除设备域关联的设备
				deviceRegion.setId(i.toString());
				deviceRegionManageMapper.deleteDeviceRegionRefNe(deviceRegion);
				//删除设备域与用户的关系
				deviceRegionManageMapper.deleteDeviceRegionRefUser(deviceRegion);
				//删除设备域
				deviceRegionManageMapper.delete(deviceRegion);
			}
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	
	
//	@Override
//	public Map<String, Object> getDeviceTreeData() {
//		//获取所有网管数据
//		return null;
//	}
//	
	
//	@Override
//	public List<DeviceTreeModel> getDeviceTreeNodes(DeviceRegion deviceRegion) {
//		List<DeviceTreeModel> emsModels=new ArrayList<DeviceTreeModel>();
//			//获取网管数据
//			List<Map> emsList=deviceRegionManageMapper.getEMSDatas(deviceRegion);
//			if(emsList!=null){
//				for(int i=0;i<emsList.size();i++){
//					Map ems=emsList.get(i);
//					DeviceTreeModel treeModel=new DeviceTreeModel();
//					treeModel.setId("ems"+ems.get("base_ems_connection_id").toString());
//					treeModel.setText((String)ems.get("display_name"));
//					
//					//获取网管下的子网
//					List<DeviceTreeModel> subnetTreeModels=new ArrayList<DeviceTreeModel>();
//					deviceRegion.setEmsId(ems.get("base_ems_connection_id").toString());
//					List<Map> subnetList=deviceRegionManageMapper.getSubnetByEMSIdDatas(deviceRegion);
//					if(subnetList!=null){
//						for(int j=0;j<subnetList.size();j++){
//							Map subnet=subnetList.get(j);
//							DeviceTreeModel subnetTreeModel=new DeviceTreeModel();
//							subnetTreeModel.setId("net"+subnet.get("base_subnet_id").toString());
//							subnetTreeModel.setText((String)subnet.get("display_name"));
//							
//							//获取子网下的网元
//							List<DeviceTreeModel> neTreeModels=new ArrayList<DeviceTreeModel>();
//							deviceRegion.setSubnetId(subnet.get("base_subnet_id").toString());
//							List<Map> neList=deviceRegionManageMapper.getDeviceBySubnetIdDatas(deviceRegion);
//							if(neList!=null){
//								for(int m=0;m<neList.size();m++){
//									Map ne=neList.get(m);
//									DeviceTreeModel neTreeModel=new DeviceTreeModel();
//									neTreeModel.setId("ne"+ne.get("base_ne_id").toString());
//									neTreeModel.setText((String)ne.get("name"));
//									String relaCou=ne.get("rela_ne_cou").toString();
//									if(relaCou!=null && "1".equals(relaCou)){
//										neTreeModel.setChecked(true);
//									}
//									neTreeModel.setLeaf(true);
//									neTreeModels.add(neTreeModel);
//								}
//							}
//							subnetTreeModel.setChildren(neTreeModels);
//							subnetTreeModels.add(subnetTreeModel);
//						}
//					}
//					treeModel.setChildren(subnetTreeModels);
//					emsModels.add(treeModel);
//				}
//			}
//		
//		
//		return emsModels;
//	}
	@Override
	public Map<String,Object> getDeviceRegionDetailById(DeviceRegion deviceRegion) {
		
		
		
		return null;
	}
	@Override
	public boolean createDeviceDomain(Map<String, String> map,List<Map<String, Integer>> list) {
		try{
			String id=map.get("id").toString();
			if(id==null || "".equals("id") || "0".equals(id)){//新增
				deviceRegionManageMapper.insert(map);
				Integer key=TypeChangeUtil.changeObjToInteger(map.get("id"));
				if(list!=null){
					DeviceRegion deviceRegion=new DeviceRegion();
					deviceRegion.setId(key.toString());
					for(int i=0;i<list.size();i++){
						String s=list.get(i).get("neId").toString();
						if(s!=null && !"".equals(s)){
							deviceRegion.setNeId(s);
							deviceRegion.setNeType(list.get(i).get("neType").toString());
							deviceRegionManageMapper.insertDeviceRegionRefNe(deviceRegion);
						}
					}
				}
			}else{//修改
				DeviceRegion deviceRegion = new DeviceRegion();
				deviceRegion.setId(map.get("id").toString());
				deviceRegion.setNote(map.get("note").toString());
				deviceRegionManageMapper.update(deviceRegion);
				//删除设备域关联的设备
				deviceRegionManageMapper.deleteDeviceRegionRefNe(deviceRegion);
				if(list!=null){
					for(int i=0;i<list.size();i++){
						String s=list.get(i).get("neId").toString();
						if(s!=null && !"".equals(s)){
							deviceRegion.setNeId(s);
							deviceRegion.setNeType(list.get(i).get("neType").toString());
							deviceRegionManageMapper.insertDeviceRegionRefNe(deviceRegion);
						}
					}
				}
			}
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	
	@Override
	public List<Map> getNesByDeviceDomainId(DeviceRegion deviceRegion) throws CommonException {
		List<Map> lists= deviceRegionManageMapper.getNesByDeviceDomainId(deviceRegion);
		if(lists!=null && lists.size()>0){
			for(Map m:lists){
				String id=(String)m.get("id");
				String nodeLevel=id.split("-")[0];
				String nodeId=id.split("-")[1];
				String parentPath=(String)m.get("text");
				if(Integer.parseInt(nodeLevel)==CommonDefine.TREE.NODE.EMSGROUP){
					m.put("text",parentPath);
				}else{
					//根据节点级别找出它的父亲路径
					parentPath=getParentPathByNodeLevelAndNodeId(parentPath,Integer.parseInt(nodeLevel),Integer.parseInt(nodeId));
					m.put("text",parentPath);
				}
			}
		}
		
		return lists;
	}
	private String getParentPathByNodeLevelAndNodeId(String parentPath,int nodeLevel,int nodeId) {
		while(nodeLevel!=0 && nodeLevel!=1){
			Map parentInfo=deviceRegionManageMapper.getParentPathByNodeLevelAndNodeId(nodeLevel,nodeId,TREE_DEFINE);
			
			String parentName=parentInfo.get("parentName").toString();
			String parentLevel=parentInfo.get("parentLevel").toString();
			String parentId=parentInfo.get("parentId").toString();
			parentPath=parentName+"->"+parentPath;
			nodeLevel=Integer.parseInt(parentLevel);
			nodeId=Integer.parseInt(parentId);
		}
		return parentPath;
	}
	
	
	
	protected static Map<String, Object> TREE_DEFINE = new HashMap<String, Object>();
	static {
		TREE_DEFINE.put("FALSE", CommonDefine.FALSE);
		TREE_DEFINE.put("CHILD_MAX", CommonDefine.TREE.CHILD_MAX);
		TREE_DEFINE.put("ROOT_ID", CommonDefine.TREE.ROOT_ID);
		TREE_DEFINE.put("ROOT_TEXT", CommonDefine.TREE.ROOT_TEXT);
		TREE_DEFINE.put("NODE_ROOT", CommonDefine.TREE.NODE.ROOT);
		TREE_DEFINE.put("NODE_EMSGROUP", CommonDefine.TREE.NODE.EMSGROUP);
		TREE_DEFINE.put("NODE_EMS", CommonDefine.TREE.NODE.EMS);
		TREE_DEFINE.put("NODE_SUBNET", CommonDefine.TREE.NODE.SUBNET);
		TREE_DEFINE.put("NODE_NE", CommonDefine.TREE.NODE.NE);
		TREE_DEFINE.put("NODE_SHELF", CommonDefine.TREE.NODE.SHELF);
		TREE_DEFINE.put("NODE_UNIT", CommonDefine.TREE.NODE.UNIT);
		TREE_DEFINE.put("NODE_SUBUNIT", CommonDefine.TREE.NODE.SUBUNIT);
		TREE_DEFINE.put("NODE_PTP", CommonDefine.TREE.NODE.PTP);
	}
	@Override
	public Map validateUserDeviceDomainName(DeviceRegion deviceRegion) {
		Map m=new HashMap();
		int mps=deviceRegionManageMapper.validateUserDeviceDomainName(deviceRegion.getName());
		if(mps>0){
			m.put("success",false);
			m.put("msg","设备域名称已存在");
			return m;
		}
		m.put("success",true);
		return m;
	}
	
	
	
}
