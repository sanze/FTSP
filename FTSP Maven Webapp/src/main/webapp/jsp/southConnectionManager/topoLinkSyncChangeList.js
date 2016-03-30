var tgRoot = new Ext.tree.AsyncTreeNode({text: 'Root'});
(function initData(){
	var attrs=parent.subFrameData["rows"];
	var nodes=[];
	for(var i=0;i<attrs.length;i++){
		var text="";
		attrs[i]['checked']=true;
		if(Ext.isDefined(attrs[i]['conflictList'])&&
			attrs[i]['conflictList'].length>0){
			attrs[i]['children']=attrs[i]['conflictList'];
			for(var j=0;j<attrs[i]['children'].length;j++){
				attrs[i]['children'][j]['cls']='';
				attrs[i]['children'][j]['leaf']=true;
				attrs[i]['children'][j]['changeTypeText']="冲突源";
				if(attrs[i]['children'][j]['isManual']==1){
					attrs[i]['children'][j]['isManualText']="人工添加";
				}else{
					attrs[i]['children'][j]['isManualText']="自动同步";
				}
			}
			text="（冲突）";
		}else{
			attrs[i]['leaf']=true;
		}
		if(attrs[i]['changeType']==1){
			attrs[i]['changeTypeText']="新增"+text;
			attrs[i]['icon']='../../resource/images/btnImages/add.png';
		}else if(attrs[i]['changeType']==2){
			attrs[i]['changeTypeText']="删除"+text;
			attrs[i]['icon']='../../resource/images/btnImages/delete.png';
		}else if(attrs[i]['changeType']==4){
			attrs[i]['changeTypeText']="更新"+text;
			attrs[i]['icon']='../../resource/images/btnImages/sync.png';
		}
		if(attrs[i]['isManual']==1){
			attrs[i]['isManualText']="人工添加";
		}else{
			attrs[i]['isManualText']="自动同步";
		}
	}
	tgRoot.attributes.children=attrs;
})();

var columns = [
        {
            id: 'changeType',
            header: '变化',
            width:120,
            dataIndex: 'changeTypeText'
        },{
            id: 'aNeDisplayName',
            header: 'A端网元',
            dataIndex: 'aNeDisplayName',
            width:80
        },{
            id: 'aNeId',
            header: 'A端网元',
            dataIndex: 'aNeId',
            width:80,
            hidden:true
        },{
            id: 'aPtp',
            header: 'A端端口',
            width:150,
            dataIndex: 'aPtp'
        },{
            id: 'zNeDisplayName',
		    header: 'Z端网元',
            dataIndex: 'zNeDisplayName',
            width:80
        },{
            id: 'zNeId',
            header: 'Z端网元',
            dataIndex: 'zNeId',
            width:100,
            hidden:true
        },{
            id: 'zPtp',
            header: 'Z端端口',
            width:150,
            dataIndex: 'zPtp'
        },{
            id: 'linkDisplayName',
            header: '名称',
            width:80,
            dataIndex: 'linkDisplayName'
        },{
            id: 'isManualText',
            header: '属性',
            width:60,
            dataIndex: 'isManualText'
        }];

 var connectListPanel = new Ext.ux.tree.TreeGrid({
		region:"center",
		//dataUrl: initData,
		root: tgRoot,
		//autoScroll : true,
		animate : false,
		//bodyStyle : 'padding:5px 5px 0',
		border : false,
		useArrows : false,
		forceLayout : true,
		enableDD : false,
		columns: columns,
		selModel: new Ext.ux.TreeMultiSelectionModel(),
		contextMenu: new Ext.menu.Menu({
	        items: [{
	            id: 'checked-node',
	            text: '勾选',
	            iconCls: "icon-checked-all"
	        },{
	            id: 'unchecked-node',
	            text: '反选',
	            iconCls: "icon-checked-none"
	        }],
	        listeners: {
	          itemclick: function(item) {
	            switch (item.id) {
	              case 'checked-node':
	                  sn=item.parentMenu.contextNodes;
	                  for(var i=0;i<sn.length;i++){
	                    if(sn[i].attributes.checked!=true){
	                      //if(sn[i].ui.checkbox&&sn[i].hidden!==true){
	                      sn[i].ui.toggleCheck();
	                      //}
	                    }
	                  }
	                  break;
	              case 'unchecked-node':
	                  sn=item.parentMenu.contextNodes;
	                  for(var i=0;i<sn.length;i++){
	                    if(sn[i].attributes.checked!=false){
	                      //if(sn[i].ui.checkbox&&sn[i].hidden!==true){
	                    	sn[i].ui.toggleCheck();
	                      //}
	                    }
	                  }
	                  break;
	            }
	          }
	        }
	    }),
	    listeners: {
	        contextmenu: function(node, e) {
//	          Register the context node with the menu so that a Menu Item's handler function can access
//	          it via its parentMenu property.
	        	if(node.disabled)
	        		return;
	        	var s = node.getOwnerTree().getSelectionModel();
	            if(s.isSelected(node)!==true){
	              node.select();
	            }
	            var c = node.getOwnerTree().contextMenu;
	            c.contextNodes = s.getSelectedNodes?s.getSelectedNodes():[s.getSelectedNode()];
	            c.showAt(e.getXY());
	        }
	    },
		buttons : [ /*{
			text : '更新',
			handler : function() {
				update();
			}
		}, */{
			text : '确定',
			handler : function() {
				//关闭修改任务信息窗口
				var win = parent.Ext.getCmp('topoLinkSyncChangeWindow');
				if (win) {
					win.close();
				}
			}
		},{
			text : '删除冲突源',
			handler : function() {
				update();
			}
		} ]
	});

function update() {
	var nodes=connectListPanel.getChecked();
	if(nodes.length==0){
		Ext.Msg.alert("信息", "请选择链路");
		return;
	}
	Ext.getBody().mask('正在执行，请稍候...');
	var datas=[];
	for(var i=0;i<nodes.length;i++){
		var model=nodes[i].attributes['linkAlterModel'];
		model.changeType=3;
		datas.push(Ext.util.JSON.encode(model));
	}
	var jsonData = {
		"emsConnectionId" : emsConnectionId,
		"jsonString" : datas
	};
	Ext.Ajax.request({
		url : 'connection!topoLinkSyncChangeList.action',
		method : 'POST',
		params : jsonData,
		success : function(response) {
			var obj = Ext.decode(response.responseText);
			Ext.getBody().unmask();
			if (obj.returnResult == 1) {
				Ext.Msg.alert("信息", obj.returnMessage);
				for(var i=0;i<nodes.length;i++){
					nodes[i].ui.toggleCheck(false);
					nodes[i].disable();
				}/*
				// 刷新列表
				var pageTool = parent.Ext.getCmp('pageTool');
				if (pageTool) {
					pageTool.doLoad(pageTool.cursor);
				}
				//关闭修改任务信息窗口
				var win = parent.Ext.getCmp('topoLinkSyncChangeWindow');
				if (win) {
					win.close();
				}*/
			}
			if (obj.returnResult == 0) {
				Ext.Msg.alert("信息", obj.returnMessage);
			}
		},
		error : function(response) {
			Ext.getBody().unmask();
			var obj = Ext.decode(response.responseText);
			Ext.Msg.alert("错误", obj.returnMessage);
		},
		failure : function(response) {
			Ext.getBody().unmask();
			var obj = Ext.decode(response.responseText);
			Ext.Msg.alert("错误", obj.returnMessage);
		}
	});
}

Ext.onReady(function() {
	Ext.Ajax.timeout=3600000; 
	//collapse menu
	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();};
//	Ext.Msg = top.Ext.Msg;

	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [ connectListPanel ],
		renderTo : Ext.getBody()
	});
});