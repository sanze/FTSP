package com.fujitsu.manager.commonManager.service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.fujitsu.IService.ICommonManagerService;
import com.fujitsu.abstractService.AbstractService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.dao.mysql.CommonManagerMapper;

public abstract class CommonManagerService extends AbstractService implements ICommonManagerService {
	@Resource
	protected CommonManagerMapper commonManagerMapper;
	public Map getSysParam(String key) throws CommonException{
		return commonManagerMapper.selectSysParam(key);
	}
	public void setSysParam(Map param) throws CommonException{
		commonManagerMapper.setSysParam(
				param);
	}

/** ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ 共通树部分 ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ **/
	public static Map<String, Object> TREE_DEFINE = new HashMap<String, Object>();
	static {
		TREE_DEFINE.putAll(CommonDefine.TREE.TREE_DEFINE);
		TREE_DEFINE.put("DOMAIN_AUTH", CommonDefine.TREE.TREE_DEFINE.get("AUTH_VIEW"));
	}
	/**
	 * 节点排序比较类
	 *
	 */
	@SuppressWarnings("unchecked")
	public class NodeComparator implements Comparator<Map>{
		/**
		 * 在字符串中的数字部分前添加数字长度对应的ascii码值
		 * @param string
		 * @return
		 */
		private String fillDigit(String string){
			String[] digits=string.split("[^0-9]+");
			string=string.replaceAll("[0-9]+", "%/D%");
			for(String digit:digits){
				if(!digit.isEmpty()){
					try{
						digit=String.valueOf(Integer.parseInt(digit));//去除开头多余的0
					}catch(Exception e){}
					string=string.replaceFirst("%/D%", (char)(digit.length())+digit);
				}
			}
			return string;
		}
		@SuppressWarnings("unchecked")
		@Override
	    public int compare(Map node1, Map node2) {
			StringBuffer node1CmpBuf = new StringBuffer();
			StringBuffer node2CmpBuf = new StringBuffer();
			node1CmpBuf.append(node1.get("parentLevel"));
			node2CmpBuf.append(node2.get("parentLevel"));
			node1CmpBuf.append("-");
			node2CmpBuf.append("-");
			node1CmpBuf.append(node1.get("parentId"));
			node2CmpBuf.append(node2.get("parentId"));
			node1CmpBuf.append("-");
			node2CmpBuf.append("-");
			node1CmpBuf.append(node1.get("nodeLevel"));
			node2CmpBuf.append(node2.get("nodeLevel"));
			node1CmpBuf.append("-");
			node2CmpBuf.append("-");
			node1CmpBuf.append(node1.get("text"));
			node2CmpBuf.append(node2.get("text"));
			String node1CmpString=fillDigit(node1CmpBuf.toString());
			String node2CmpString=fillDigit(node2CmpBuf.toString());
	        return node1CmpString.compareTo(node2CmpString);
	    }
	}
	@SuppressWarnings("unchecked")
	public Map constructNode(final Map nodeConst, int nodeLevel, boolean leaf, String checked) throws CommonException{
		Map node=null;
		if(nodeConst!=null){
			node=new HashMap();
			node.putAll(nodeConst);
		}
		Map newNode=new HashMap();
		if(node!=null){
			node.put("id", ""+nodeLevel+'-'+node.get("nodeId"));
			node.put("leaf", leaf);
			node.put("nodeLevel", nodeLevel);
			node.put("disabled", node.containsKey("domainAuth")?(CommonDefine.FALSE==Integer.valueOf(node.get("domainAuth").toString())):false);
			
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
			
			//节点对应表的其他所有字段放到附加信息中
			Map additionalInfo=new HashMap();
			if(node.containsKey("additionalInfo")){
				if(node.get("additionalInfo") instanceof Map){
					additionalInfo=(Map)node.get("additionalInfo");
				}else{
					additionalInfo.put("additionalInfo", node.get("additionalInfo"));
				}
				node.remove("additionalInfo");
			}
			//节点基础信息,除此之外的内容移到additionalInfo
			List<String> basicInfoKeys= Arrays.asList(new String[]{
					"nodeId","text","parent","domainAuth",
					"id","leaf","nodeLevel","disabled","checked","parentLevel","parentId"});
			Object[] keys=node.keySet().toArray();
			
			for(Object key:keys){
				if(basicInfoKeys.contains(key)){
					newNode.put(key,node.get(key));
					node.remove(key);
				}
			}
			newNode.put("additionalInfo", node);
			
			//资源部分的附加信息,可从additionalInfo获取,待后续移除
			if(nodeLevel == CommonDefine.TREE.NODE.NE){
				node.remove("neModel");
				node.remove("roomId");
				if(newNode.get("neModel")==null){
					String  model = node.get("PRODUCT_NAME")==null?"---":node.get("PRODUCT_NAME").toString();
					newNode.put("neModel", model);
				}
				if(newNode.get("roomId")==null){
					String  roomId = node.get("RESOURCE_ROOM_ID")==null?"-1":node.get("RESOURCE_ROOM_ID").toString();
					newNode.put("roomId", roomId);
				}
			}
		}
		return newNode;
	}
/** _______________________________ 共通树部分 _______________________________ **/
}
