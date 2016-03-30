/*
 * wuchao
 * 2013.12
 */
//子iframe
var subIframe;

var treeParams={
	id:id
};
var treeurl="tree.jsp?"+Ext.urlEncode(treeParams);

var store=new Ext.data.ArrayStore({
	data: [
	       ],
	fields:[
		   {name:"id",mapping:"id"},
		   {name:"text",mapping:"text"},
		   {name:"node",mapping:"node"}
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
           id: 'node',
           name:'node',
           header: 'node',
           dataIndex: 'node',
           hidden:true
       },{
           id: 'text',
           header: 'path',
           width:180,
           dataIndex: 'text',
           renderer: function (data, metadata, record, rowIndex, columnIndex, store) {        
        	    var tip= record.get('text'); 
        	    metadata.attr = 'ext:qtip="' + tip+'"';   //关键  
        	    return data ;     
        	}
       }]
   });

function onCheckChange(node,checked){
	if(checked=='all'){
		var brothers=node.parentNode.childNodes;
		for(var i=0;i<brothers.length;i++){
			if(brothers[i].id!=node.id){
				brothers[i].attributes.checked="none";
				brothers[i].ui.check("none");  
			}
		}
	}
}

var treePanel = new Ext.Panel({
	title:"",
	id:"westPanel",
	height : Ext.getBody().getHeight()*0.65,
	width : 200,
	forceFit:true,
	collapsed: false,   // initially collapse the group
    collapsible: false,
    collapseMode: 'mini',
    split:true,
    html:'<iframe id="tree_panel" name="tree_panel" src ="'+treeurl+'" height="'+Ext.getBody().getHeight()*0.65+'" width="100%" frameBorder=0 border=0/>'
}); 

var gridPanel = new Ext.grid.EditorGridPanel({
	//renderTo: Ext.getCmp('nodeDatas'),
	id:"gridPanel",
	//region:"center",
	stripeRows:true,
	autoScroll:true,
	frame:false,
	cm: columnModel,
	store:store,
	loadMask: true,
	height : Ext.getBody().getHeight()*0.65,
	width : 195,
	hideHeaders:true,
	clicksToEdit: 2,//设置点击几次才可编辑  
	selModel:checkboxSelectionModel ,  //必须加不然不能选checkbox 
	viewConfig: {
       forceFit:true
   }
}); 


var authName=new Ext.form.TextField({
	xtype : 'textfield',
	id : 'name',
	name : 'name',
	fieldLabel : '权限管理域名',
	sideText:'<span style="color:red">*</span>',
	//width: 350,
	anchor:'90%',
	allowBlank: false
});


var masterplateStore = new Ext.data.Store({
    proxy : new Ext.data.HttpProxy({
              url: 'auth-region-manage!searchAuthDomain.action',
              async: false
            }),
//			paramNames: {start:'startNumber',limit:'pageSize'},
    pageSize:10,
	reader: new Ext.data.JsonReader({
        totalProperty: 'total',
		root : "rows"
    },[
	    "sys_auth_domain_id","name","note"
    ])
});


var masterplateChoo = new Ext.form.ComboBox({
	fieldLabel : '模板选择',
	id:'masterplate',
	name:'masterplate',
	minListWidth: 220,
	//style:'margin-left:100px',
	store :masterplateStore,
	valueField : 'sys_auth_domain_id',
	displayField : 'name',
    mode:'remote', 
    pageSize:masterplateStore.pageSize,
    queryDelay: 500,
	typeAhead: false,
    autoSelect:false,
    anchor:'90%',
    editable : false,
    enableKeyEvents : true,
    resizable: true,
    autoScroll:true,
    listeners : {
        select: function(combo,record,index){
		  Ext.getCmp('name').setValue(record.data.name);
		  Ext.getCmp('note').setValue(record.data.note);
		  //获取设备域对应的所有设备
		  Ext.Ajax.request({
			    url: 'auth-region-manage!getMenuAuthsByAuthDomainId.action', 
			    method : 'POST',
			    params: {// 请求参数
			    	'id':record.data.sys_auth_domain_id
					},
			    success: function(response) {
			    	store.loadData([]);  
			    	var obj = Ext.decode(response.responseText);
			    	var ms=obj.menus;//获取权限域对应的所有菜单
			    	var mes=[];
					if(ms!=null && ms.length>0){
						for(var i=0;i<ms.length;i++){
							mes.push({
								"id":ms[i].id,
								"node":ms[i].node,
								"text":ms[i].text
							});
						}
					}
					store.loadData(mes);     
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
	width : 800,
	border:false,
	labelWidth : 80,
	autoScroll : true,
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
//            width: 260,
            labelWidth : 80,
            border : false,
            items : [authName]
        },{
            columnWidth : .5,
            layout : "form",
//            width: 260,
            labelWidth : 60,
            border : false,
            items : [masterplateChoo]
        }]
    },{
			xtype : 'textfield',
			id : 'note',
			name : 'note',
			fieldLabel : '描述 ',
			sideText:'<span style="color:red">*</span>',
			anchor:'95%',
            allowBlank : false 
	},
	{xtype: 'tbspacer', height:20,shadow:false},
	{
		layout: 'column',
        border:false,
        bodyStyle : 'padding-left:85px;',
        items: [treePanel,{
        	height:235,
        	width:50,
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
                height:20,
                width: 40,
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
                height:20,
                width: 40,
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
            baseCls:'x-plain',
            width:390,
            layout:'fit',
            border:true,
            items:gridPanel
        }]
    }]
});


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

//点击后把左边的选择的节点加到右边
function addChooseNodes(){
	  function judgeNodeAtModifyArr(node){
      	if(operaNes!=null && operaNes.length>0){
      		for(var i=0;i<operaNes.length;i++){
      			if(node.attributes["node"]==operaNes[i].node){
      				return i;
      			}
      		}
      	}
      	return -1;
	  }
	var arr=[];
	var nodes=subIframe.getCheckedNodes(null,'leaf');
	if(nodes!=null && nodes.length>0){
	   for(var i=nodes.length-1;i>=0;i--){
		   if(saveType==1){
			   var re=judgeNodeAtModifyArr(nodes[i]);
			   if(re!=-1){
				   operaNes[re].id=nodes[i].id;
				   operaNes[re].node=nodes[i].attributes["node"];
				   operaNes[re].text=nodes[i].getPath('text').substring(12);
			   }else{
				   var jsonData = {
						   "id":nodes[i].id,
						   "node":nodes[i].attributes["node"],
					       "text":nodes[i].getPath('text').substring(12)
				   };
				   operaNes.push(jsonData);
			   }
		   }
		   if(saveType==0){
					  var jsonData = {
							   "id":nodes[i].id,
							   "node":nodes[i].attributes["node"],
						       "text":nodes[i].getPath('text').substring(12)
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
      	if(menus!=null && menus.length>0){
      		for(var i=0;i<menus.length;i++){
      			if(node.attributes["id"]==menus[i].id){
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
		
		operaNes=[];
		if(menus!=null && menus.length>0){
			for(var i=0;i<menus.length;i++){
				operaNes.push({
					"id":menus[i].id,
					"node":menus[i].node,
					"text":menus[i].text
					});
			}
		}
		store.loadData(menus);   
		if(nodes!=null && nodes.length>0){
		   for(var i=0;i<nodes.length;i++){
			   removeNodeChecked(nodes[i]);
		   }
		}
	   	if(menus!=null && menus.length>0){
      		for(var i=0;i<menus.length;i++){
      			var nod=subIframe.treePanel.getNodeById(menus[i].id);
      			if(nod){
      				nod.attributes.checked="all";
      				nod.ui.check("all");   
      			}
      		}
      	}
		
		
//		if(nodes && nodes.length>0){
//			for(var i=0;i<nodes.length;i++){
//			    if(judgeNodeAtModifyArr(nodes[i])){
//			    	nodes[i].attributes.checked="all";
//			    	nodes[i].ui.check("all");   
//			    }else{
//			    	removeNodeChecked(nodes[i]);
//			    }
//			}
//		}
	}
}


function close() {
	var win = parent.Ext.getCmp('editWindow');
	if (win) {
		win.close();
	}
}


//根据右边选择的树节点获取所有权限
function getAuthArraByChooseTree(){
	var arr=[];
	var datas =gridPanel.getStore().data.items; 
	if(datas.length<=0){
		return null;
	}
	
	if(saveType==1){//修改时从队列中取值
		for(var i=0;i<operaNes.length;i++){
			arr.push(operaNes[i].id);
		}
		return arr;
	}
	
	 for(var i = 0; i< datas.length;i++){
		 //var node=subIframe.treePanel.getNodeById(datas[i].data.id);
		 //if(node!=undefined && node!=null){
			 arr.push(datas[i].data.id);
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

	var auths=getAuthArraByChooseTree();
	if(auths==null || auths.length==0){
		Ext.Msg.alert("错误","请选择绑定的菜单");
		return;
	}
	var name = Ext.getCmp("name").getValue();
	var note = Ext.getCmp("note").getValue();	
	top.Ext.getBody().mask('正在保存，请稍候...');
	Ext.Ajax.request({
	    url: 'auth-region-manage!saveAuthRegionData.action', 
	    method : 'POST',
	    params: {// 请求参数
	    	'id':id,
			'name':name,
			'note':note,
			"authLists":auths
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
		getAuthsByDeviceDomainId();//获取所有权限
		var row = parent.selections[0].data;
		Ext.getCmp('name').setValue(row.name);
		Ext.getCmp('note').setValue(row.note);
		Ext.getCmp('name').disable(true);
}



var menus=[];//全局,保存原始数据,供复原使用
var operaNes=[];//新建一个数据供修改使用
function getAuthsByDeviceDomainId(){
	top.Ext.getBody().mask('正在导入数据,请稍候...');
	Ext.Ajax.request({
	    url: 'auth-region-manage!getMenuAuthsByAuthDomainId.action', 
	    method : 'POST',
	    params: {// 请求参数
	    	'id':id
			},
	    success: function(response) {
	    	top.Ext.getBody().unmask();
	    	var obj = Ext.decode(response.responseText);
			ms=obj.menus;//获取权限域对应的所有菜单
			if(ms!=null && ms.length>0){
				for(var i=0;i<ms.length;i++){
					menus.push({
										"id":ms[i].id,
										"node":ms[i].node,
										"text":ms[i].text
										}
							
							);
					operaNes.push({
						"id":ms[i].id,
						"node":ms[i].node,
						"text":ms[i].text
						});
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
			
	
			
});

