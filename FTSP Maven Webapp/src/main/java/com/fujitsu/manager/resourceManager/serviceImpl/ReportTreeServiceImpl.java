package com.fujitsu.manager.resourceManager.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.dao.mysql.ReportTreeMapper;
import com.fujitsu.manager.resourceManager.model.ReportTreeModel;
import com.fujitsu.manager.resourceManager.service.ReportTreeService;

@Service
@Transactional(rollbackFor = Exception.class)
public class ReportTreeServiceImpl extends ReportTreeService {
	public static final int areaLeaf=4;
	public static final int emsGroupType=4;
	public static final int emsType=5;
	@Resource
	private ReportTreeMapper reportTreeMapper;

	@Override
	public List<Map> treeGetChildNodes(ReportTreeModel reportTreeModel)throws CommonException {
		String type=reportTreeModel.getType();
		String nodeId=reportTreeModel.getId();
		Map node=null;
		if(type!=null && "1".equals(type)){//加载区域
			node=new HashMap();
			node.put("nodeId",(Integer.parseInt(reportTreeModel.getId())));
			constructAreaNodes(node,Integer.parseInt(reportTreeModel.getType()));
			//List<Map> nodes=reportTreeMapper.getChildNodesByNodeId(Integer.parseInt(nodeId),Integer.parseInt(nodeType));
			//construcAreaNode(nodes,nodeType);
			return (List<Map>)node.get("children");
		}else if(type!=null && "2".equals(type)){//加载区域下局站
			List<Map> nodes=reportTreeMapper.getChildNodesByNodeId(Integer.parseInt(nodeId),Integer.parseInt(type));
		}else if(type!=null && "4".equals(type)){//加载网元组
			List<Map> nodes=reportTreeMapper.getChildNodesByNodeId(Integer.parseInt(nodeId),Integer.parseInt(type));
			construcEmsGroupNode(nodes,reportTreeModel);
			return nodes;
		}else if(type!=null && "5".equals(type)){//构造网元数
			if(reportTreeModel.getParentIds()==null || "".equals(reportTreeModel.getParentIds())){
				return null;
			}
			//根据网元树
			List<Map> ems=construcEmsTreeByEmsGroupIds(reportTreeModel);
			return ems;
		}
		return null;
	}
	
	
	
	//根据网元树
	private List<Map> construcEmsTreeByEmsGroupIds(ReportTreeModel reportTreeModel) {
		if(reportTreeModel.getParentIds()==null || "".equals(reportTreeModel.getParentIds()) || "-99".equals(reportTreeModel.getParentIds())){
			return new ArrayList<Map>();
		}
		List<Map> tree=new ArrayList<Map>();
		String[] emsGroupIds=reportTreeModel.getParentIds().split(",");
		for(String emsGroupId:emsGroupIds){
			if(emsGroupId!=null && !"".equals(emsGroupId)){
				if("-1".equals(emsGroupId)){
					List<Map> ems=reportTreeMapper.getEmsByEmsGroupId(reportTreeModel.getUserId(),emsGroupId,CommonDefine.TREE.TREE_DEFINE);//根据网元组id获取对应的所有网管
					if(ems==null || ems.size()==0){
						return null;
					}
					for(Map node:ems){
						node.put("leaf",true);
						//node.put("expanded",true);
						node.put("id",emsType+"-"+node.get("nodeId"));
						//判断节点是否在选择的列表中
						boolean isExists=judgeNodeInLists(node.get("nodeId").toString(),reportTreeModel.getIds());
						if(isExists){
							node.put("checked","all");
						}else{
							node.put("checked","none");
						}
					}
					return ems;
				}else{
					Map emsGroup=reportTreeMapper.getEmsGroupByEmsGroupId(emsGroupId);//根据网管组id获取网管组信息
					List<Map> ems=reportTreeMapper.getEmsByEmsGroupId(reportTreeModel.getUserId(),emsGroupId,CommonDefine.TREE.TREE_DEFINE);//根据网元组id获取对应的所有网管
					emsGroup.put("checked","none");
					emsGroup.put("expanded",false);
					emsGroup.put("leaf",false);
					emsGroup.put("id",emsGroupType+"-"+emsGroupId);
					//构造网管节点
					construcEmsNode(emsGroup,ems,reportTreeModel);
					emsGroup.put("children",ems);
					tree.add(emsGroup);
				}
			}
		}
		return tree;
	}
	
	//构造网管节点
	private void construcEmsNode(Map emsGroup,List<Map> nodes,ReportTreeModel reportTreeModel) {
		for(Map node:nodes){
			node.put("leaf",true);
			//node.put("expanded",true);
			node.put("id",emsType+"-"+node.get("nodeId"));
			//判断节点是否在选择的列表中
			boolean isExists=judgeNodeInLists(node.get("nodeId").toString(),reportTreeModel.getIds());
			if(isExists){
				emsGroup.put("expanded",true);
				node.put("checked","all");
			}else{
				node.put("checked","none");
			}
		}
	}
	
	
	//构造网管组节点
	private void construcEmsGroupNode(List<Map> nodes,ReportTreeModel reportTreeModel) {
		for(Map node:nodes){
			node.put("leaf",true);
			//node.put("expanded",true);
			node.put("id",emsType+"-"+node.get("nodeId"));
			//判断节点是否在选择的列表中
			boolean isExists=judgeNodeInLists(node.get("nodeId").toString(),reportTreeModel.getIds());
			if(isExists){
				node.put("checked","all");
			}else{
				node.put("checked","none");
			}
		}
	}

	
	//判断指定节点是否在列表中
	private boolean judgeNodeInLists(String nodeId, String ids){
		if(ids==null || "".equals(ids)){
			return false;
		}
		String[] lists=ids.split(",");
		for(String s:lists){
			if(!"".equals(s) && s.equals(nodeId)){
				return true;
			}
		}
		return false;
	}

	//获取区域节点
	private void construcAreaNode(List<Map> nodes,String nodeType) {
		for(Map node:nodes){
			if((Integer)node.get("area_level")==4){
				node.put("leaf", true);
				node.put("checked", "all");
			}else{
				node.put("leaf", false);
			}
			node.put("id", nodeType+"-"+node.get("nodeId"));
		}
	}

	//获取区域所有节点
	@SuppressWarnings("unchecked")
	public void constructAreaNodes(Map node,int type) throws CommonException {
		System.out.println("node:"+node);
		List<Map> nodes=reportTreeMapper.getChildNodesByNodeId((Integer)node.get("nodeId"),type);
		System.out.println(nodes);
		if(nodes!=null && nodes.size()>0){
			node.put("leaf",false);
			node.put("expanded",true);
			node.put("children",nodes);
			for(Map n:nodes){
				n.put("id",node.get("nodeLevel")+"-"+n.get("nodeId"));
				constructAreaNodes(n,type);
			}
		}else{
			node.put("leaf",true);
			node.put("checked", "all");
			node.put("expanded",true);
			node.put("id",node.get("nodeLevel")+"-"+node.get("nodeId"));
		}
	}

	
	@SuppressWarnings("unchecked")
	public Map constructNode(Map node, int nodeLevel, boolean leaf, String checked) throws CommonException{
		if(node!=null){
			node.put("id", ""+nodeLevel+'-'+node.get("nodeId"));
			node.put("leaf", leaf);
			node.put("nodeLevel", nodeLevel);
			if(nodeLevel == CommonDefine.TREE.NODE.NE){
				String  model = "---";
				if(node.get("neModel")!=null)
					model = node.get("neModel").toString();
				node.put("neModel", model);
			}
			if(checked!=null)node.put("checked", checked);
			try{
				String parent=(String)node.get("parent");
				if(parent!=null){
					String[] parentInfos=parent.split("-");
					node.put("parentLevel", Integer.parseInt(parentInfos[0]));
					node.put("parentId", Integer.parseInt(parentInfos[1]));
				}
			}catch(Exception e){
				throw new CommonException(e, MessageCodeDefine.COM_EXCPT_PROCESSING_ERROR);
			}
		}
		return node;
	}
	
	
}
