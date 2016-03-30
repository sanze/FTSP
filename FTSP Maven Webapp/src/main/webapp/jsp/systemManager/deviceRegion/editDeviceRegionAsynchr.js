/*
 * wuchao
 * 2013.12
 */
//子iframe
var subIframe;
var qheight=Ext.getBody().getHeight();
var treeParams={
	leafType:leafType
    //checkModel:"single",
    //onlyLeafCheckable:false
};
var treeurl="../../commonManager/tree.jsp?"+Ext.urlEncode(treeParams);

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
           width:380,
           dataIndex: 'text',
           renderer: function (data, metadata, record, rowIndex, columnIndex, store) {        
        	    var tip= record.get('text'); 
        	    metadata.attr = 'ext:qtip="' + tip+'"';   //关键  
        	    return data ;     
        	}
       }]
   });



var treePanel = new Ext.Panel({
	title:"选择设备域",
	id:"westPanel",
	height : 350,
	width : 250,
	autoScroll:true,
	forceFit:true,
    split:true,
    html:'<iframe id="tree_panel" name="tree_panel" src ="'+treeurl+'" height="100%" width="100%" frameBorder=0 border=0/>'
}); 

var gridPanel = new Ext.grid.EditorGridPanel({
	//renderTo: Ext.getCmp('nodeDatas'),
	id:"gridPanel",
	title:"确定设备域",
	height : 350,
	//region:"center",
	stripeRows:true,
	frame:false,
	cm: columnModel,
//	resizable:true,
	sm:checkboxSelectionModel,
	store:store,
	loadMask: true,
	hideHeaders:true,
	viewConfig: {
       forceFit:false
   }
}); 

// 自动调整列宽
store.on('load',function(t,records,index){
	var i = 0,maxLength=0;
	for(i;i<records.length;i++){
		var text = records[i].get('text');
		var chinese = text.match(/[^\x00-\xff]/g);
		if(chinese==null)
			chinese = '';
		var english = text.replace(/[^\x00-\xff]/g,'');
		if(english==null)
			english = '';
		var length = english.length*7+chinese.length*11+11;
		maxLength = length>maxLength?length:maxLength;
	}
	var width = Ext.getCmp('gridPanel').getColumnModel().getColumnWidth(3);
	Ext.getCmp('gridPanel').getColumnModel().setColumnWidth(3, maxLength);
});
//store.on('add',function(t,records,index){
//	var i = 0,maxLength=0;
//	for(i;i<records.length;i++){
//		var text = records[i].get('text');
//		var chinese = text.match(/[^\x00-\xff]/g);
//		if(chinese==null)
//			chinese = '';
//		var english = text.replace(/[^\x00-\xff]/g,'');
//		if(english==null)
//			english = '';
//		var length = english.length*7+chinese.length*12;
//		maxLength = length>maxLength?length:maxLength;
//	}
//	var width = Ext.getCmp('gridPanel').getColumnModel().getColumnWidth(3);
//	if(width<maxLength){
//	Ext.getCmp('gridPanel').getColumnModel().setColumnWidth(3, maxLength);
//	}
//});

function getBytesLength(str) {
	// 在GBK编码里，除了ASCII字符，其它都占两个字符宽
	return str.replace(/[^\x00-\xff]/g, 'xx').length;
	}

var masterplateStore = new Ext.data.Store({
    proxy : new Ext.data.HttpProxy({
              url: 'device-region-manage!getDeviceRegionData.action',
              async: false
            }),
//			paramNames: {start:'startNumber',limit:'pageSize'},
    pageSize:10,
	reader: new Ext.data.JsonReader({
        totalProperty: 'total',
		root : "rows"
    },[
	    "sys_device_domain_id","name","note"
    ])
});


var  deviceName=new Ext.form.TextField({
	xtype : 'textfield',
	id : 'name',
	name : 'name',
	fieldLabel : '设备域名',
	sideText:'<span style="color:red">*</span>',
//	width : 250,
//	height : 20,
	anchor:'90%',
	allowBlank: false
});
var  note=new Ext.form.TextField({
	xtype:'textfield',
	id : 'note',
	name : 'note',
	fieldLabel : '描述',
	sideText:'<span style="color:red">*</span>',
	anchor:'95%',
    allowBlank : false
});
var masterplateChoo = new Ext.form.ComboBox({
	fieldLabel : '模板选择',
	id:'masterplate',
	name:'masterplate',
//	width: 270,
	anchor:'90%',
//	minListWidth: 220,
	//style:'margin-left:100px',
	store :masterplateStore,
	valueField : 'sys_device_domain_id',
	displayField : 'name',
    mode:'remote', 
    pageSize:masterplateStore.pageSize,
    queryDelay: 500,
	typeAhead: false,
    autoSelect:false,
    editable : false,
    enableKeyEvents : true,
    resizable: true,
//    autoScroll:true,
    listeners : {
        select: function(combo,record,index){
		  Ext.getCmp('name').setValue(record.data.name);
		  Ext.getCmp('note').setValue(record.data.note);
		  //获取设备域对应的所有设备
		  Ext.Ajax.request({
			    url: 'device-region-manage!getNesByDeviceDomainId.action', 
			    method : 'POST',
			    params: {// 请求参数
			    	'id':record.data.sys_device_domain_id
					},
			    success: function(response) {
			    	store.loadData([]);  
			    	var obj = Ext.decode(response.responseText);
			    	var nes=obj.devices;
			    	var ns=[];
					if(nes!=null && nes.length>0){
						for(var i=0;i<nes.length;i++){
							ns.push(nes[i]);
						}
					}
					store.loadData(ns);   
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
        },
        keypress: function(field, event) {
        	
        },
        beforequery:function(queryEvent){
        	masterplateStore.baseParams={
        			limit: masterplateStore.pageSize
    		};
        	masterplateStore.load({
        			callback : function(records,options,success){
        				if(!success)
        					Ext.Msg.alert("提示","搜索出错");
        			}
    		});
        },
        scope : this
      }
});

var formPanel = new Ext.FormPanel({
	frame : false,
	bodyStyle : 'padding:20px 20px 20px 20px',
	height : 450,
	width : 1100,
	border:false,
	labelWidth:60,
//	autoScroll : true,
	items : [
     {
    	xtype : 'textfield',
		id : 'id',
		name : 'id',
		fieldLabel : 'id',
		hidden:true
    },{
        layout : "column",
        border : false,
        items:[{
            columnWidth : .5,
            layout : "form",
//            width : 350,
            labelWidth : 60,
            border : false,
            items : [deviceName]
        },{
            columnWidth : .5,
            layout : "form",
//            width: 260,
            style : 'margin-left:15px',
            labelWidth : 60,
            border : false,
            items : [masterplateChoo]
        }]
    },note,
	{xtype: 'tbspacer', height:20,shadow:false},
	{
		layout: 'column',
        border:false,
        bodyStyle : 'padding-left:65px;',
        items: [treePanel,{
        	height:235,
        	width:40,
        	border:false,
        	layout: {
                type: 'vbox',
                pack: 'start',  //纵向对齐方式 start：从顶部；center：从中部；end：从底部
                align: 'center'  //对齐方式 center、left、right：居中、左对齐、右对齐；stretch：延伸；stretchmax：以最大的元素为标准延伸
            },
            defaults: {
                xtype: 'button'
            },
            items: [{
                xtype: 'tbspacer',          //插入的空填充
                flex: 1
            },{
                text: ">>",
                height:10,
                width: 35,
                flex: 1,                      //表示当前子元素尺寸所占的均分的份数。
                listeners: { "click": function () {
     	        	addChooseNodes();
     	        }
                }
            },{
                xtype: 'tbspacer',          //插入的空填充
                flex: 2
            },{
            	text: "&lt;&lt;",
                height:10,
                width: 35,
                flex: 1,                      //表示当前子元素尺寸所占的均分的份数。
                listeners: { "click": function () {
     	        	removeChooseNodes();
     	        }
                }
            },{
                xtype: 'tbspacer',          //插入的空填充
                flex: 1
            }]
        },{
            id:'nodeDatas',
//            columnWidth:.55,
            width:650,
            baseCls:'x-plain',
            border:true,
            layout:'fit',
            items:gridPanel
        }]
    }]
});



//点击后把左边的选择的节点加到右边
function addChooseNodes(){
	  function judgeNodeAtModifyArr(node){
      	if(operaNes!=null && operaNes.length>0){
      		for(var i=0;i<operaNes.length;i++){
      			if(node.attributes["id"]==operaNes[i].id){
      				return true;
      			}
      		}
      	}
      	return false;
	  }
	  
	  function deleteSubNodeByParentNodeText(nodeText){
			if(operaNes!=null && operaNes.length>0){
	      		for(var i=operaNes.length-1;i>=0;i--){
	      			if(operaNes[i].text.indexOf(nodeText)==0 && operaNes[i].text.length>nodeText.length){
	      				operaNes.splice(i,1);
	      				continue;
	      			}
	      			
	      			if(nodeText.indexOf(operaNes[i].text)==0 && nodeText.length>operaNes[i].text.length){
	      				operaNes.splice(i,1);
	      				continue;
	      			}
	      			
	      		}
	      	}
	  }
	  
	var arr=[];
	
	subIframe.treePanel.pathSeparator = '->';
	var nodes=subIframe.getCheckedNodes(null,'top');
	
//	var nodePath=subIframe.getCheckedNodes(['path[->][text]'],'top');
	if(nodes!=null && nodes.length>0){
	   for(var i=0;i<nodes.length;i++){
		  
		   //判断如果子节点在队列中，则删除子节点
		   if(saveType==1){
			   var nodeText=nodes[i].getPath('text').substring(8);
			   deleteSubNodeByParentNodeText(nodeText);
			   if(!judgeNodeAtModifyArr(nodes[i])){
					  var jsonData = {
							   "id":nodes[i].id,
						       "text":nodes[i].getPath('text').substring(8)
					   };
					  operaNes.push(jsonData);
				   }
		   }
		   
		  
		   if(saveType==0){
					  var jsonData = {
							   "id":nodes[i].id,
						       "text":nodes[i].getPath('text').substring(8)
					   };
					  arr.push(jsonData);
		   }
		   
		 
	   }
	}
	
	  if(saveType==0){
		  //加载数据到右边
			 store.loadData(arr);  
	  }else{
		  //加载数据到右边
			 store.loadData(operaNes);  
	  }
	
}


//移除节点的选中状态
function removeNodeChecked(node){
	if(node){
		node.attributes.checked="none";
		node.ui.check("none");   
	}
}

//点击后把右边选中的列表去掉且从树中去掉
function removeChooseNodes(){
	var selectRecord =gridPanel.getSelectionModel().getSelections(); 
	if(selectRecord.length<=0){
		Ext.Msg.alert("提示","请选择需要去掉的选项!");
	}
	for(var i = selectRecord.length-1; i>=0;i--){
	    	var id=selectRecord[i].get("id");
	    	if(saveType==1){
				for(var j=operaNes.length-1;j>=0;j--){
		    		if(operaNes[j].id==id){
		    			operaNes.splice(j,1);
		    		}
		    	}
			}
	    	removeNodeChecked(subIframe.treePanel.getNodeById(id));
	    	gridPanel.getStore().remove(selectRecord[i]);
	    	
    }
	gridPanel.getView().refresh();
}


function resetPage(){
  function judgeNodeAtModifyArr(node){
      	if(qNes!=null && qNes.length>0){
      		for(var i=0;i<qNes.length;i++){
      			if(node.attributes["id"]==qNes[i].id){
      				return true;
      			}
      		}
      	}
      	return false;
	  }
  
  	var nodes=subIframe.getCheckedNodes();
	if(saveType==0){//增加重置
		Ext.getCmp('name').setValue('');
		Ext.getCmp('note').setValue('');
		store.loadData([]);
		Ext.getCmp('masterplate').clearValue();
		if(nodes!=null && nodes.length>0){
		   for(var i=0;i<nodes.length;i++){
			   removeNodeChecked(nodes[i]);
		   }
		}
	}else if(saveType==1){//修改重置
		var row = parent.selections[0].data;
		Ext.getCmp('note').setValue(row.note);
		var arr=[];
		if(qNes){
			for(var i=0;i<qNes.length;i++){
				arr.push(qNes[i]);
			}
		}
		store.loadData(arr);   
		
		if(nodes && nodes.length>0){
			for(var i=0;i<nodes.length;i++){
			    if(judgeNodeAtModifyArr(nodes[i])){
			    	nodes[i].attributes.checked="all";
			    	nodes[i].ui.check("all");   
			    }else{
			    	removeNodeChecked(nodes[i]);
			    }
			}
		}
	}
}


function close() {
	var win = parent.Ext.getCmp('editWindow');
	if (win) {
		win.close();
	}
}


//根据右边选择的树节点获取所有网元
function getNeArraByChooseTree(){
	var arr=[];
	var datas =gridPanel.getStore().data.items; 
	if(datas.length<=0){
		return null;
	}
	
	if(saveType==1){//修改时从队列中取值
		if(operaNes!=null){
			for(var j=0;j<operaNes.length;j++){
				 var json={
					    	nodeId: operaNes[j].id.split('-')[1],
				        	nodeLevel: operaNes[j].id.split('-')[0],
				        	endLevel: 4
				 }
				 arr.push(Ext.encode(json));
	    	}
		}
		return arr;
	}
	
	 for(var i = 0; i< datas.length;i++){
		 //var node=subIframe.treePanel.getNodeById(datas[i].data.id);
		 //if(node!=undefined && node!=null){
			 var json={
				    	nodeId: datas[i].data.id.split('-')[1],
			        	nodeLevel: datas[i].data.id.split('-')[0],
			        	endLevel: 4
			 };
			 arr.push(Ext.encode(json));
		 //}
    }
	 return arr;
}

////在数组中放入指定节点的所有子节点
//function pubAllLeafNodesFromP(arr,node){
//	var isLeaf=function(node){
//		if(node.attributes['leaf']){
//			arr.push(node);
//		}
//	}
//	node.cascade(isLeaf);
//}


function save() {
	if(!formPanel.getForm().isValid()){
		return ;
	}
	//获取选择的网元数组
	var nes=getNeArraByChooseTree();
	if(nes==null || nes.length==0){
		Ext.Msg.alert("错误","请选择需要绑定的对象！");
		return;
	}
	var name = Ext.getCmp("name").getValue();
	var note = Ext.getCmp("note").getValue();	
	top.Ext.getBody().mask('正在保存，请稍候...');
	Ext.Ajax.request({
	    url: 'device-region-manage!createDeviceDomain.action', 
	    method : 'POST',
	    params: {// 请求参数
	    	'id':id,
			'name':name,
			'note':note,
			"neList":nes
			},
	    success: function(response) {
	    	top.Ext.getBody().unmask();
	    	var obj = Ext.decode(response.responseText);
			if(obj.success){//新增成功
				parent.store.reload();
        		close(); 
			}else{
				Ext.Msg.alert("错误",obj.msg);
			}
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
	    
		getNesByDeviceDomainId();//获取设备域对应的所有设备
		var row = parent.selections[0].data;
		Ext.getCmp('name').setValue(row.name);
		Ext.getCmp('note').setValue(row.note);
		Ext.getCmp('name').disable(true);
}


//获取设备域对应的所有设备
var qNes;//全局,保存原始数据,供复原使用
var operaNes=[];//新建一个数据供修改使用
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

var panel = new Ext.Panel({
	id : "panel",
	layout : "form",
	frame : false,
	border : false,
	autoScroll : true,
	region : "center",
	items : formPanel,
	buttons : [ {
		text : '确定',
		handler : save
	}, {
		text : '重置',
		handler : resetPage
	}, {
		text : '取消',
		handler : close
	} ]

});

Ext.onReady(function() {
			Ext.Msg = top.Ext.Msg;
			Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
			Ext.QuickTips.init(); // 开启悬停提示
			Ext.form.Field.prototype.msgTarget = 'side'; // 提示显示风格
			
			if(saveType==1){//修改
				masterplateChoo.hide();
				initModifyData();
			}else{
				masterplateChoo.show();
			}
			var win = new Ext.Viewport({
				id : 'win',
				loadMask : true,
				layout : 'border',
				items : panel,
				renderTo : Ext.getBody(),
				listeners:{
		           	afterrender: function( win,obj){
		           		subIframe=document.getElementById('tree_panel').contentWindow;
		           	}
				}
			});
			
//			Ext.apply(subIframe,{pathSeparator:'->'});
			
});

