/*
 * wuchao
 * 2013.12
 */
//子iframe
var subIframe;

var treeParams;
var treeurl;

var store=new Ext.data.ArrayStore({
	data: [
	       ],
	fields:[
		   {name:"id",mapping:"id"},
		   {name:"text",mapping:"text"}
   ]
});

var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({singleSelect :false});
var columnModel = new Ext.grid.ColumnModel({
       defaults: {
           sortable: true,
           forceFit:false
       },
       columns: [new Ext.grid.RowNumberer({
   		width : 26
   	}),checkboxSelectionModel,{
           id: 'id',
           name:'id',
           header: 'id',
           dataIndex: 'id',
           hidden:true
       },{
           id: 'text',
           header: 'path',
           width:200,
           dataIndex: 'text',
           renderer: function (data, metadata, record, rowIndex, columnIndex, store) {        
        	    var tip= record.get('text'); 
        	    metadata.attr = 'ext:qtip="' + tip+'"';   //关键  
        	    return data ;     
        	}
       }]
   });



var treePanel = new Ext.Panel({
	title:"",
	id:"westPanel",
	height : (Ext.getBody().getHeight()-60)*0.9,
	width : 280,
	forceFit:true,
	collapsed: false,   // initially collapse the group
    collapsible: false,
    collapseMode: 'mini',
    split:true,
    autoScroll:false,
    html:'<iframe id="tree_panel" name="tree_panel" src ="'+treeurl+'" height="'+(Ext.getBody().getHeight()-60)*0.9+'" width="100%" frameBorder=0 border=0/>'
}); 



var formPanel = new Ext.FormPanel({
	region : "center",
	layout: 'form',
	frame : false,
	bodyStyle : 'padding:20px 20px 20px 20px',
	height : '350',
	width : '100%',
	border:false,
	autoScroll : false,
	items : [
	{
		layout: 'form',
        border:false,
        items: [{
            id:'treeDatas',
            baseCls:'x-plain',
            border:true,
            items:treePanel
        }]
    }],
	buttons : [{
				text : '确定',
				handler : save
			},{
				text : '取消',
				handler : close
			}]
});







function close() {
	var win = parent.Ext.getCmp('treeWindow');
	if (win) {
		win.close();
	}
}



function save() {
	//获取选择的网元数组
	getNeArraByChooseTree();
	parent.emsIds=nodeIds;
	parent.emsNames=nodeNames;
	parent.fillChooseInfo();
	close();
}

var nodeIds='';
var nodeNames='';
//根据右边选择的树节点获取所有网元
function getNeArraByChooseTree(){
	var nodes=subIframe.getCheckedNodes(null,'leaf');
	
	if(nodes!=null && nodes.length>0){
		for(var i=0;i<nodes.length;i++){
			nodeIds+=nodes[i].id.substring(2);
			nodeNames+=nodes[i].text;
			if(i!=nodes.length-1){
				nodeIds+=',';
				nodeNames+=',';
			}
		}
	}	
}


Ext.onReady(function() {
			Ext.Msg = top.Ext.Msg;
			Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
			Ext.QuickTips.init(); // 开启悬停提示
			Ext.form.Field.prototype.msgTarget = 'side'; // 提示显示风格
			
			var win = new Ext.Viewport({
				id : 'win',
				loadMask : true,
				layout : 'border',
				items : formPanel,
				renderTo : Ext.getBody(),
				listeners:{
		           	afterrender: function( win,obj){
		           		subIframe=document.getElementById('tree_panel').contentWindow;
		           	}
				}
			});
});

