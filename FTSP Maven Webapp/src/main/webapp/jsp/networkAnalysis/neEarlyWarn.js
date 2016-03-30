var neIds=''; 
var westPanel;
(function() {
	var treeParams = {
		rootId : 0,
		rootType : 0,
		rootText : "FTSP",
		rootVisible : false,
		leafType : 4
	};
	var treeurl = "../commonManager/tree.jsp?" + Ext.urlEncode(treeParams);
	westPanel = new Ext.Panel({
		id : "westPanel",
		region : "west",
		width : 280,
		autoScroll : true,
		boxMinWidth : 230,
		boxMinHeight : 260,
		forceFit : true,
		collapsed : false, // initially collapse the group
		collapsible : false,
		collapseMode : 'mini',
		split : true,
		html : '<iframe name="tree_panel" id="tree_panel" src ="' + treeurl
				+ '" height="100%" width="100%" frameBorder=0 border=0 />'
	});
})();

// ************************* 模板列表 ****************************
var store = new Ext.data.Store({
	url : 'todo.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "DISPLAY_NAME", "SLOT_OCCUPANCY" ,"PTP_OCCUPANCY_2M",
	     "PTP_OCCUPANCY_STM1" ,"PTP_OCCUPANCY_STM4","PTP_OCCUPANCY_STM16",
	     "PTP_OCCUPANCY_STM64","PTP_OCCUPANCY_STM256","MS_OCCUPANCY"])
}); 

var row = [  
	{ header: ''},//header表示父表头标题，colspan表示包含子列数目   
	{ header: ''},   
	{ header: ''},
	{ header: '端口可用率', colspan: 6, align: 'center' },   
	{ header: ''}   
 ];

var group = new Ext.ux.grid.ColumnHeaderGroup({  
    rows: [row]  
}); 

var cm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : false
	// columns are not sortable by default
	},
	columns : [
			new Ext.grid.RowNumberer({
				width : 26
			}),
			{
				id : 'DISPLAY_NAME',
				header : '网元',
				dataIndex : 'DISPLAY_NAME',
				width : 120
			},
			{
				id : 'SLOT_OCCUPANCY',
				header : '槽道可用率',
				dataIndex : 'SLOT_OCCUPANCY',
				width : 120,
				renderer :colorGrid
			},
			{
				id : 'PTP_OCCUPANCY_2M',
				header : '2M',
				dataIndex : 'PTP_OCCUPANCY_2M',
				width : 60,
				renderer :colorGrid
			},
			{
				id : 'PTP_OCCUPANCY_STM1',
				header : 'STM-1',
				dataIndex : 'PTP_OCCUPANCY_STM1',
				width : 60,
				renderer :colorGrid
			},
			{
				id : 'PTP_OCCUPANCY_STM4',
				header : 'STM-4',
				dataIndex : 'PTP_OCCUPANCY_STM4',
				width : 60,
				renderer :colorGrid
			},
			{
				id : 'PTP_OCCUPANCY_STM16',
				header : 'STM-16',
				dataIndex : 'PTP_OCCUPANCY_STM16',
				width : 60,
				renderer :colorGrid
			},
			{
				id : 'PTP_OCCUPANCY_STM64',
				header : 'STM-64',
				dataIndex : 'PTP_OCCUPANCY_STM64',
				width : 60,
				renderer :colorGrid
			},
			{
				id : 'PTP_OCCUPANCY_STM256',
				header : 'STM-256',
				dataIndex : 'PTP_OCCUPANCY_STM256',
				width : 60,
				renderer :colorGrid
			},
			{
				id : 'MS_OCCUPANCY',
				header : '复用段时隙平均可用率（所在传输系统）',
				dataIndex : 'MS_OCCUPANCY',
				width : 300,
				renderer :function(v,m,r){  
					if(v.length>0){
						var v1= []; 
						var mss = v.split(";");
						for (var k=0;k<mss.length;k++) {
							var ms = mss[k].split(":"); 
	//						id:Name:VC4:VC12
							var node={};  
							node.rlId=ms[0]; node.rlName=ms[1]; node.VC4=ms[2]; node.VC12=ms[3]; 
							v1.push(node);
						}   
						var vStr=""; 
						if(v1.length>0){ 
							for(var i=0;i<v1.length;i++){ 
								vStr += "<a href='#' onclick=toMultiSecEarlyWarDetail('"+v1[i].rlId+"')>"
								+v1[i].rlName+ "</a>"+"："+"VC4 "+ colorGrid(v1[i].VC4,m,r,0)+" VC12 "
								+colorGrid(v1[i].VC12,m,r,1)+"；";
							}
							return vStr.substring(0, vStr.length-1);
						}else{
							return vStr;
						}
					}
				}
			}]
}); 

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 200,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});


var gridPanel = new Ext.grid.GridPanel({
	id : 'grid',
	region : 'center',
	store : store,
	cm : cm,
	frame : false,
	stripeRows : true, // 交替行效果
	clicksToEdit : 2,// 设置点击几次才可编辑
	stateId : "neEarlyWarnId",
	stateful : true,
	tbar : [ '-', {
		text : '查询',
		icon : '../../resource/images/btnImages/search.png',
		privilege : viewAuth,
		handler : searchNeEarlyWarn
	}, "-", {
		text : '导出',
		icon : '../../resource/images/btnImages/export.png',
		privilege : actionAuth,
		handler : exportData
	} ], 
	bbar : pageTool,
	plugins: group 
});  
 

function colorGrid(v,m,r,f) { 
	if(m.id=="SLOT_OCCUPANCY"){
		return compareValue(v,SLOT_MJ,SLOT_MN,SLOT_WR);
	}
	if(m.id=="PTP_OCCUPANCY_2M" || m.id=="PTP_OCCUPANCY_STM1" || m.id=="PTP_OCCUPANCY_STM4"
		|| m.id=="PTP_OCCUPANCY_STM16" || m.id=="PTP_OCCUPANCY_STM64" || m.id=="PTP_OCCUPANCY_STM256"){
		return compareValue(v,PTP_MJ,PTP_MN,PTP_WR);
	} 
	if(m.id=="MS_OCCUPANCY"){ 
		if(f==0)
			return compareValue(v,MS_VC4_MJ,MS_VC4_MN,MS_VC4_WR); 
		else
			return compareValue(v,MS_VC12_MJ,MS_VC12_MN,MS_VC12_WR);
	}
	return v;
}

function toMultiSecEarlyWarDetail(id) { 
	var url = '../networkAnalysis/multiSecEarlyWarn.jsp?rlId='+id;
	parent.addTabPage(url, "复用段资源预警分析", authSequence);  
}
  
//查询
function searchNeEarlyWarn(){   
	var checkedNodeIds = window.frames["tree_panel"].getCheckedNodes([ "nodeId",
	                       "text","additionalInfo"],"all", 4, "all"); 
	if (checkedNodeIds.length == 0) {
		Ext.Msg.alert("提示", "请选择网元！");
		return;
	}    
	neIds="(";
	for(var i=0;i<checkedNodeIds.length;i++){  
		neIds+= checkedNodeIds[i].nodeId+",";   
	}
	neIds=neIds.substring(0, neIds.length-1)+")";
    store.proxy = new Ext.data.HttpProxy({
		url : 'network!searchNeEarlyWarn.action'
    });
	gridPanel.getEl().mask("正在查询...");
	store.baseParams = { "paramMap.neIds" :neIds,
			"limit":200};
	store.load({ 
	    callback : function (r, options, success) { 
	        if (success) {
				gridPanel.getEl().unmask();
	        }else {
				gridPanel.getEl().unmask();
	            var obj = Ext.decode(r.responseText);
	            Ext.Msg.alert("提示", obj.returnMessage);
	        }
	    }
	});
}

//导出
function exportData(){ 
	var params={
			"paramMap.neIds" :neIds,
			"paramMap.flag" : "1"
		};
	if (store.getCount() == 0) {
		Ext.Msg.alert("提示", "结果为空，不导出！");
	} else if (store.getTotalCount() > 2000) {
		Ext.Msg.confirm("提示", "最多导出2000条数据，是否导出？", function(btn) {
			if (btn == 'yes') {
				{  	
					gridPanel.getEl().mask("正在导出...");
					exportRequest(params);
				}
			}
		});
	} else{
		gridPanel.getEl().mask("正在导出...");
		exportRequest(params);  
	}
}

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
	Ext.Ajax.timeout = 90000000;
	getEarlyAlarmSetting(); 
	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [ gridPanel, westPanel ]
	});
	win.show(); 
});
