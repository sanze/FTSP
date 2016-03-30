package com.fujitsu.manager.demoForNew.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.manager.demoForNew.model.TreeModel;
import com.fujitsu.manager.demoForNew.service.DemoTreeService;

@Service
@Transactional(rollbackFor = Exception.class)
public class DemoTreeServiceImpl extends DemoTreeService {
	
//	@Resource
//	private DemoTestManagerMapper demoTreeManagerMapper;
//	
	/**
	 * Method name: getLowerTreeNOdes <BR>
	 * Description: 获取子节点内容<BR>
	 * Remark: 2013-12-15<BR>
	 * @author hg
	 * @return String<BR>
	 */
	@Override
	public List getLowerTreeNOdes(String id) {
		List lowerList = new ArrayList();
		//以下为根据传来的id获取数据（一般是从数据库里查询 目前示例直接一个方法获取）
		System.out.println("id= "+id);
		try{
			if(id!= null && id.equals("0")){
				/**此处  根据前台每次传入的ID获取数据
				 * （本示例是从java中获取的值，从数据库中取值同理）
				 */
				lowerList = getListById(id);
			}else if(id.equals("1")){
				
			}
		}catch(Exception e){
			e.printStackTrace();
			lowerList = null;
		}
		return lowerList;
	}
	
	private List getListById(String id){
		
		List list = new ArrayList();
		
		for(int i = 0;i<10;i++){
			TreeModel treeModel = new TreeModel();
			
			treeModel.setId(id+"200"+i);
			treeModel.setText("叶子节点"+i);
			treeModel.setLeaf(true);
			treeModel.setIconCls("");
			treeModel.setHref("");
			list.add(treeModel);
		}
		
		return list;
	}
}
