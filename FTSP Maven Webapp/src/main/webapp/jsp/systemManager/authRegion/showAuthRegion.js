
var store=new Ext.data.ArrayStore({
	data: [
	 
	       ],
	fields:[
		   {name:"id",mapping:"id"},
		   {name:"text",mapping:"text"}
   ]
});


var columnModel = new Ext.grid.ColumnModel({
       defaults: {
           sortable: true,
           forceFit:false
       },
       columns: [{
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
       	}       }]
   });

var gridPanel = new Ext.grid.EditorGridPanel({
	id:"gridPanel",
	stripeRows:true,
	autoScroll:false,
	frame:false,
	cm: columnModel,
	store:store,
	loadMask: true,
	width: 600,
    height : Ext.getBody().getHeight()*0.6,
	border:true,
	hideHeaders:true,
	clicksToEdit: 2,//设置点击几次才可编辑  
	viewConfig: {
       forceFit:true
   }
}); 

var formPanel = new Ext.FormPanel({
	region : "center",
	layout: 'form',
	frame : false,
	bodyStyle : 'padding:20px 10px 20px 10px',
	//labelAlign : 'right',
	height : '550',
	width : 700,
	//labelWidth : 60,
	autoScroll : true,
	items : [    {
    	xtype : 'textfield',
		id : 'id',
		name : 'id',
		fieldLabel : 'id',
		hidden:true
    },{
    	xtype : 'textfield',
		id : 'name',
		name : 'name',
		fieldLabel : '权限域名',
		width: 600,
		height : 20,
		allowBlank: false
	},
		{
			xtype : 'textfield',
			id : 'note',
			name : 'note',
			fieldLabel : '描述',
		    width: 600,
		    height:20,
            allowBlank : false 
	},
	{
		 id:'nodeDatas',
         baseCls:'x-plain',
         border:true,
         items:gridPanel,
         bodyStyle:'padding:20px 0 20px 105px'
    }],
	buttons : [{
				text : '取消', 
				handler : close
			}]
});



function close() {
	var win = parent.Ext.getCmp('editWindow');
	if (win) {
		win.close();
	}
}


var menus=[];
function getMenuAuthsByAuthDomainId(){
	top.Ext.getBody().mask('正在导入数据，请稍候...');
	Ext.Ajax.request({
	    url: 'auth-region-manage!getMenuAuthsByAuthDomainId.action', 
	    method : 'POST',
	    params: {// 请求参数
	    	'id':id
			},
	    success: function(response) {
	    	top.Ext.getBody().unmask();
	    	var obj = Ext.decode(response.responseText);
			menus=obj.menus;//获取权限域对应的所有菜单
//			var arr=[];
//			if(obj.menus!=null && obj.menus.length>0){
//				for(var i=0;i<obj.menus.length;i++){
//					var jsonData={
//							'id':obj.menus[i].id,
//							'text':obj.menus[i].text
//					};
//					arr.push(jsonData);
//				}
//	    	}
			//console.info(menus);
			store.loadData(menus);   
	    },
	    error:function(response) {
	    	top.Ext.getBody().unmask();
        	Ext.Msg.alert("错误",response.responseText);
	    },
	    failure:function(response) {
	    	top.Ext.getBody().unmask();
        	Ext.Msg.alert("错误",response.responseText);
	    }
	}); 	
}

//修改时加载数据
function initModifyData(){
		getMenuAuthsByAuthDomainId();//获取权限域对应的所有菜单权限
		var row = parent.selections[0].data;
		Ext.getCmp('name').setValue(row.name);
		Ext.getCmp('note').setValue(row.note);
		Ext.getCmp('name').disable(true);
		Ext.getCmp('note').disable(true);
}



Ext.onReady(function() {
			initModifyData();
			new Ext.Viewport({
				//id : 'win',
				//loadMask : true,
				layout : 'border',
				items : formPanel
				//renderTo : Ext.getBody()
			});
			Ext.Msg = top.Ext.Msg;
			Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
			document.onmousedown = function() {
				top.Ext.menu.MenuMgr.hideAll();
			}
			Ext.QuickTips.init(); // 开启悬停提示
			Ext.form.Field.prototype.msgTarget = 'side'; // 提示显示风格
		});

