/*------------------------------------左侧区域树----------------------------------------*/
var treeParams={
	rootId:0,
	rootType:0,
	rootText:"FTSP",
	rootVisible:false,
	leafType:2
}; 

var westPanel = new Ext.Panel({
	region : 'west',
	flex:1,
	html : '<iframe id="tree_panel" name = "tree_panel" src ="../../commonManager/tree.jsp?'
		+Ext.urlEncode(treeParams)+'" height="100%" width="100%" frameBorder=0 border=0/>'
}); 

function onGetChecked(getFunc){
	// 告警源选择
	var iframe = window.frames["tree_panel"] || window.frames[0];
	var checkedNodeIds;
	if (iframe.getCheckedNodes) {
		checkedNodeIds = iframe.getCheckedNodes(["nodeId", "path*text"], "top",[2]);
	} else {
		checkedNodeIds = iframe.contentWindow.getCheckedNodes(["nodeId","path*text"], "top",[2]);
	}
	if (checkedNodeIds.length == 0) {
		Ext.Msg.alert('提示', '请先勾选网管节点后再添加。');
		return false;
	} 
	var right = store.getRange();  
	var records = [];
	for ( var i = 0; i < checkedNodeIds.length; i++) {
		//组装record数据
		var rec = new Ext.data.Record(); 
		rec.set('nodeId', checkedNodeIds[i].nodeId); 
		var names = checkedNodeIds[i]['path*text'].split("*");
		 // 有网管分组
		if (names.length > 1) {
			rec.set('GROUP_NAME', names[0]);
			rec.set('EMS_NAME', names[1]);
		} else { 
		// 没有网管分组
			rec.set('GROUP_NAME', '');
			rec.set('EMS_NAME', names[0]);
		}
		var recordIndex = store.findBy(function(record, id) {
			if (rec.get("nodeId") == record.get("nodeId")) {
				return true;
			}
		});
		if (recordIndex == -1) {
			store.add(rec);
		}  
	} 
	gridPanel.getView().refresh();
};

/*------------------------------------右侧GridPanel-------------------------------------*/
var store= new Ext.data.Store({ 
	reader :  new Ext.data.JsonReader({ 
		root : "rows",
		fields :['nodeId','GROUP_NAME',"EMS_NAME"]
	})
});

var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
	singleSelect : false
});
var cm = new Ext.grid.ColumnModel({
	defaults : {
		sortable : true
	},
	columns : [ new Ext.grid.RowNumberer({
		width:26,
		locked : true
	}), checkboxSelectionModel,{
		header : 'nodeId',
		dataIndex : 'nodeId',
		width : 87,
		hidden : true
	},{
		header : '网管分组',
		dataIndex : 'GROUP_NAME',
		width : 150
	}, {
		header : '网管',
		dataIndex : 'EMS_NAME',
		width : 150
	}]
});

var gridPanel = new Ext.grid.GridPanel({
	region : 'center',
	store :store,
	cm : cm,
	flex:2,
	selModel : checkboxSelectionModel, 
	frame : false,
	stripeRows : true, 
	tbar : [{
	        text: '移除',
	        style : 'margin-left:10px;',
	        icon : '../../../resource/images/btnImages/delete.png',
	        handler : function(){
	        	RightToleft();
	        }
		}],
	buttons:[{ 
		  text: '确定',
		  handler : function(){
			  var applyStr="网管："; 
			  parent.applyEmsIdsStr="";
			  parent.applyEmsIds= new Array();
			  store.each(function(record){ 
				  applyStr+=record.get("EMS_NAME")+",";  
				  parent.applyEmsIdsStr+=record.get("nodeId")+"."+
				       		record.get("GROUP_NAME")+"."+record.get("EMS_NAME")+",";
				  parent.applyEmsIds.push(record.get("nodeId"));
			  });
			  applyStr = applyStr.substring(0,applyStr.lastIndexOf(",")); 
			  parent.applyEmsIdsStr = parent.applyEmsIdsStr.substring(0,parent.applyEmsIdsStr.lastIndexOf(",")); 
			  parent.frames["editConvergeRule_panel"].Ext.getCmp('regionText').setValue(applyStr); 
//			  parent.eqStr="";
//			  parent.frames["editConvergeRule_panel"].Ext.getCmp('applyEquip').setValue("");   
			  var win = parent.Ext.getCmp('applyAreaWin');
			  if (win) {
				  win.close();
			  }  
		 }
	},{
		  text: '取消',
		  handler : function(){
		     var win = parent.Ext.getCmp('applyAreaWin');
			 if (win) {
				 win.close();
			 }
		  }
	},{
		  text: '重置',
	}]
});

//移除
function RightToleft(){
	var right = gridPanel.getSelectionModel().getSelections(); 
	if(right==undefined || right==null){
		return;
	}
	store.remove(right);
}   

//初始化页面
function initData(){
	if(parent.applyEmsIdsStr==""||parent.applyEmsIdsStr==null){
		return;
	}
	var arr = parent.applyEmsIdsStr.split(",");
	for(var i=0;i<arr.length;i++){ 
		var ar=arr[i].split("."); 
		var record = new Ext.data.Record(['nodeId','GROUP_NAME','EMS_NAME']);  
		record.set('nodeId',ar[0]);
		if(ar[1]!=null && ar[1]!=""&& ar[1]!="null"){
			record.set('GROUP_NAME',ar[1]);
		} 
		record.set('EMS_NAME',ar[2]);
		store.add(record);
	}
}

var alarm = new Ext.Panel({ 
	region:'center',
	layout : {
		type : 'hbox',
		padding : '0',
		align : 'stretch'
	},
	 items : [westPanel,gridPanel]
});

Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'title';
	
 	Ext.BLANK_IMAGE_URL="../../resource/ext/resources/images/default/s.gif";
 	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();};
 	Ext.Msg = top.Ext.Msg;  
 	initData();
  	var win = new Ext.Viewport({
  		id:'win',
        layout : 'fit',
        items : [alarm]
	});
 });
