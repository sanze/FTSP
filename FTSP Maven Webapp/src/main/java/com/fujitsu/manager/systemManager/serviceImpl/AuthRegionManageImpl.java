package com.fujitsu.manager.systemManager.serviceImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.common.CommonException;
import com.fujitsu.dao.mysql.AuthRegionManageMapper;
import com.fujitsu.manager.systemManager.model.AuthRegion;
import com.fujitsu.manager.systemManager.model.AuthTreeModel;
import com.fujitsu.manager.systemManager.service.AuthRegionManageService;
import com.mongodb.Mongo;
@Service
@Transactional(rollbackFor = Exception.class)
public class AuthRegionManageImpl extends AuthRegionManageService{
	
	@Resource
	private AuthRegionManageMapper authRegionManageMapper;
	@Autowired
	private Mongo mongo;
	public Mongo getMongo() {
		return mongo;
	}
	public void setMongo(Mongo mongo) {
		this.mongo = mongo;
	}

	@Override
	public Map<String, Object> searchAuthDomain(int startNumber, int pageSize) {		
		Map map=new HashedMap();
		Map returnMap=new HashedMap();
		List<Map> enigneerList = new ArrayList<Map>();
		int total=authRegionManageMapper.countAuthRegionDataList(map);
		map.put("startNumber", startNumber);
		map.put("pageSize", pageSize);
		enigneerList=authRegionManageMapper.selectAuthRegionDataList(map);
		returnMap.put("rows", enigneerList);
		returnMap.put("total", total);
		return returnMap;
	}

	
	//获取树状数据
	@Override
	public List<AuthTreeModel> getAuthTreeNodes(AuthRegion authRegion) {
		List<AuthTreeModel> authNodes=new ArrayList<AuthTreeModel>();
		if(authRegion.getMenuId()==null){
			return null;
		}
		String isLeaf;//判定传过来来的菜单是否为叶子节点
		if(authRegion.getMenuId()!=null && "0".equals(authRegion.getMenuId())){
			isLeaf="0";
		}else{
			isLeaf=authRegionManageMapper.getIsLeafByMenuId(authRegion.getMenuId());
		}
		if(isLeaf!=null && "0".equals(isLeaf)){//不是叶子节点
			List<Map> authList=authRegionManageMapper.getAuthTreeNodes(authRegion);
			if(authList!=null && authList.size()>0){
				for(int i=0;i<authList.size();i++){
					Map m=authList.get(i);
					AuthTreeModel d=new AuthTreeModel();
					d.setId(m.get("sys_menu_id").toString());
					d.setText(m.get("menu_display_name").toString());
					d.setNode(m.get("sys_menu_id").toString());
					authNodes.add(d);
				}
			}
			return authNodes;
		}else if(isLeaf!=null && "1".equals(isLeaf)){//是叶子节点
			return getOperatorAuths(authRegion);
		
		}
		return authNodes;
	}
	
	private List<AuthTreeModel> getOperatorAuths(AuthRegion authRegion) {
		//获取权限域对应的菜单权限
		//String menuAuth=authRegionManageMapper.getMenuAuthByAuth(authRegion);
		
		List<AuthTreeModel> auths=new ArrayList<AuthTreeModel>();
		AuthTreeModel d=new AuthTreeModel();
		d.setId(authRegion.getMenuId()+"_1111100000");
		d.setText("增,删,改,查,执行");
		d.setLeaf(true);
		d.setNode(authRegion.getMenuId());
		//d.setDisabled(false);
		auths.add(d);
		d=new AuthTreeModel();
		d.setId(authRegion.getMenuId()+"_0011100000");
		d.setText("改,查,执行");
		d.setLeaf(true);
		d.setNode(authRegion.getMenuId());
		//d.setDisabled(false);
		auths.add(d);
		d=new AuthTreeModel();
		d.setId(authRegion.getMenuId()+"_0001100000");
		d.setText("查,执行");
		d.setLeaf(true);
		d.setNode(authRegion.getMenuId());
		//d.setDisabled(false);
		auths.add(d);
		d=new AuthTreeModel();
		d.setId(authRegion.getMenuId()+"_0001000000");
		d.setText("查");
		d.setLeaf(true);
		d.setNode(authRegion.getMenuId());
		//d.setDisabled(false);
		auths.add(d);
		return auths;
	}
	
	
	//保存权限域
	@Override
	public Map<String, Object> saveAuthRegionData(AuthRegion authRegion)throws CommonException {
		Map m = new HashMap();
		try{
			if(authRegion.getId().equals("0")){
				authRegionManageMapper.insert(authRegion);
				Integer key=Integer.parseInt(authRegion.getId());
				if(authRegion.getAuthLists()!=null){
					for(int i=0;i<authRegion.getAuthLists().size();i++){
						String id=authRegion.getAuthLists().get(i);
						if(id!=null || !"".equals(id)){
							String[] ids=id.split("_");
							authRegion.setMenuId(ids[0]);
							authRegion.setAuthId(ids[1]);
							authRegionManageMapper.insertAuthRegionRefMenu(authRegion);
						}
					}
				}
			}else{
				authRegionManageMapper.update(authRegion);
				//删除权限与关联的菜单
				authRegionManageMapper.deleteAuthRegionRefMenu(authRegion);
				//插入权限与关联的菜单
				if(authRegion.getAuthLists()!=null){
					for(int i=0;i<authRegion.getAuthLists().size();i++){
						String id=authRegion.getAuthLists().get(i);
						String[] ids=id.split("_");
						authRegion.setMenuId(ids[0]);
						authRegion.setAuthId(ids[1]);
						authRegionManageMapper.insertAuthRegionRefMenu(authRegion);
					}
				}
			}
			m.put("success", true);
			m.put("msg", "保存成功！");
		}catch(Exception e){
			e.printStackTrace();
			m.put("success", false);
			m.put("msg", "保存失败！");
		}
		return m;
	}
	@Override
	public Map<String, Object> deleteAuthRegions(AuthRegion authRegion)throws CommonException {
		Map m = new HashMap();
		try{
			if(authRegion.getIds()!=null){
				for(int i=0;i<authRegion.getIds().size();i++){
					//先删除权限域关联的菜单
					authRegion.setId(authRegion.getIds().get(i));
					authRegionManageMapper.deleteAuthRegionRefMenu(authRegion);
					//删除用户引用的权限域
					authRegionManageMapper.deleUserRefAuthRegion(authRegion);
					//删除权限域
					authRegionManageMapper.delete(authRegion);
				}
			}
			m.put("success", true);
			m.put("msg", "保存成功！");
		}catch(Exception e){
			e.printStackTrace();
			m.put("success", false);
			m.put("msg", "保存失败！");
		}
		return m;
	}
	@Override
	public List<Map> getMenuAuthsByAuthDomainId(AuthRegion authRegion) {
		List<Map> menus=authRegionManageMapper.getMenuAuthsByAuthDomainId(authRegion);
		List<Map> datas=new ArrayList<Map>();
		if(menus!=null && menus.size()>0){
			for(int i=0;i<menus.size();i++){
				Map menu=menus.get(i);
				Map data=new HashMap();
				data.put("id",menu.get("sys_menu_id").toString()+"_"+menu.get("auth_sequence").toString());
				data.put("node",menu.get("sys_menu_id").toString());
				String menuPath=getMenuPathByMenu(menu);//获取菜单父子关系路径
				if(menuPath!=null){
					data.put("text",menuPath);
				}
				datas.add(data);
			}
		}
		return datas;
	}
	
	
	private String getMenuPathByMenu(Map menu) {
		String parentMenuId=menu.get("menu_parent_id").toString();
		String authName=getAuthNameByAuthSequence(menu.get("auth_sequence").toString());
		String menuPath=menu.get("menu_display_name").toString()+"-->"+authName;
		while(parentMenuId!=null && !"0".equals(parentMenuId)){
			//获取父菜单
			Map parentMenu=authRegionManageMapper.getParentMenuByMenuId(parentMenuId);
			if(parentMenu!=null){
				menuPath=parentMenu.get("menu_display_name").toString()+"-->"+menuPath;
				parentMenuId=parentMenu.get("menu_parent_id").toString();
			}else{
				return menuPath;
			}
		}
		return menuPath;
	}
	private String getAuthNameByAuthSequence(String authSequence) {
		if(authSequence==null){
			return null;
		}
		if("1111100000".equals(authSequence)){
			return "增,删,改,查,执行";
		}
		if("0011100000".equals(authSequence)){
			return "改,查,执行";
		}
		if("0001100000".equals(authSequence)){
			return "查,执行";
		}
		if("0001000000".equals(authSequence)){
			return "查";
		}
		return null;
	}
	@Override
	public Map validateUserAuthDomainName(AuthRegion authRegion) {
		Map m=new HashMap();
		int mps=authRegionManageMapper.validateUserAuthDomainName(authRegion.getName());
		if(mps>0){
			m.put("success",false);
			m.put("msg","权限域名称已存在");
			return m;
		}
		m.put("success",true);
		return m;
	}
	
	
	
	
}
