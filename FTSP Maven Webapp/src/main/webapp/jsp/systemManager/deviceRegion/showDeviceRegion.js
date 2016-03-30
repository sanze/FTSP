/*
 * wuchao
 * 2013.12
 */
//模板选择下拉框

//生成配置设备树



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
       	}
       }]
   });

var gridPanel = new Ext.grid.EditorGridPanel({
	//renderTo: Ext.getCmp('nodeDatas'),
	id:"gridPanel",
	region:"center",
	title:"设备管理清单",
	stripeRows:true,
	autoScroll:false,
	frame:false,
	cm: columnModel,
	store:store,
	loadMask: true,
//	width: 400,
	anchor:"90%",
    height : Ext.getBody().getHeight()*0.6,
	hideHeaders:true,
	viewConfig: {
       forceFit:true
   }
}); 

var formPanel = new Ext.FormPanel({
	region : "center",
	layout: 'form',
	frame : false,
	bodyStyle : 'padding:20px 10px 20px 20px',
	//labelAlign : 'right',
	height : '550',
	width : Ext.getBody().getWidth()*0.7,
	labelWidth : 60,
	autoScroll : true,
	items : [{
    	xtype : 'textfield',
		id : 'id',
		name : 'id',
		fieldLabel : 'id',
		hidden:true
    },{
        	xtype : 'textfield',
			id : 'name',
			name : 'name',
			fieldLabel : '设备域名:',
			labelSeparator:'', 
			anchor:"90%",
			height : 20
		},{
			xtype : 'textfield',
			id : 'note',
			name : 'note',
			fieldLabel : '描述:',
			labelSeparator:'', 
			anchor:"90%",
		    height:20
	},{
        id:'nodeDatas',
        bodyStyle:'padding-left: 65px;padding-top:20px;',
        baseCls:'x-plain',
        anchor:"90%",
        border:true,
        items:gridPanel
    }],
	buttons : [{
				text : '关闭',
				handler : close
			}]
});


function close() {
	var win = parent.Ext.getCmp('editWindow');
	if (win) {
		win.close();
	}
}





function initModifyData(){
		getNesByDeviceDomainId();//获取设备域对应的所有设备
		var row = parent.selections[0].data;
		Ext.getCmp('name').setValue(row.name);
		Ext.getCmp('note').setValue(row.note);
		Ext.getCmp('name').disable(true);
		Ext.getCmp('note').disable(true);
}

var operaNes=[];
//获取设备域对应的所有设备
function getNesByDeviceDomainId(){
	top.Ext.getBody().mask('正在导入数据,请稍候...');
	Ext.Ajax.request({
	    url: 'device-region-manage!getNesByDeviceDomainId.action', 
	    method : 'POST',
	    params: {// 请求参数
	    	'id':id
			},
	    success: function(response) {
	    	top.Ext.getBody().unmask();
	    	var obj = Ext.decode(response.responseText);
	    	qNes=obj.devices;
			if(qNes!=null && qNes.length>0){
				for(var i=0;i<qNes.length;i++){
					operaNes.push(qNes[i]);
				}
			}
			store.loadData(operaNes);   
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



Ext.onReady(function() {
			Ext.Msg = top.Ext.Msg;
			Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
			document.onmousedown = function() {
				top.Ext.menu.MenuMgr.hideAll();
			}
			Ext.QuickTips.init(); // 开启悬停提示
			Ext.form.Field.prototype.msgTarget = 'side'; // 提示显示风格
			var win = new Ext.Viewport({
				id : 'win',
				loadMask : true,
				layout : 'border',
				items : [formPanel],
				renderTo : Ext.getBody()
			});
			initModifyData();
		});

