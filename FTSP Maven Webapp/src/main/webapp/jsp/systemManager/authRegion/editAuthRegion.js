/*
 * wuchao
 * 2013.12
 */
//模板选择下拉框
var masterplateChoo = new Ext.form.ComboBox({
	fieldLabel : '模板选择',
	id:'masterplate',
	name:'masterplate',
	store : new Ext.data.ArrayStore({
					fields : ['value','displayName'],
					data:[ [ '1', '域1' ], [ '2', '域2' ], [ '3', '域3' ]]
				}),
	valueField : 'value',
	displayField : 'displayName',
	emptyText:'域1',
	mode : 'local',
	triggerAction : 'all',
	width : '50%',
	listeners : {
		select : function(combo, record, index) {
		}
	}
});


//生成配置设备树
var Tree = Ext.tree;   
var tree = new Tree.TreePanel({  
	el:'tree-div',   
	height:350,
    width:250,
    useArrows:true,//是否在树中使用Vista样式箭头，默认为false。
	collapsible: true,
    rootVisible:true,     //隐藏根节点    
    border:false,          //边框    
    animate:true,         //动画效果    
    autoScroll:true,      //自动滚动    
    enableDD:false,       //拖拽节点                 
    containerScroll:true,  
    checkModel :true,//为true表示复选框
    onCheckModel :true,//为true表示复选框
    //expandable:true,//当不含子节点时，是否总显示一个加减图标，默认为false
    pathSeparator:'-> ',//树节点路径的分隔符，默认为'/'
    loader: new Tree.TreeLoader({   
    	//dataUrl:'auth-region-manage!getAuthTreeNodes.action?menuId=0'
    }),
    listeners : {
    	click: function(node) {  
        },
    	beforeload:function(node){
    			tree.loader.dataUrl='auth-region-manage!getAuthTreeNodes.action?id='+id+'&menuId='+node.id;   
    	},
    	load:function(node){//导入完看叶子节点是否在修改队列中，在则选中，不在则取消
    		if(node.childNodes){
    			var childs=node.childNodes;
    			for(var i=0;i<childs.length;i++){
    				var child=childs[i];
    				if(child.leaf){
    					var isExists=isInOperatorArr(child);//判断是否在操作队列中
    					if(isExists){
    						 child.ui.toggleCheck(true);       
					         child.attributes.checked = true;  
    					}else{
    						child.ui.toggleCheck(false);       
					        child.attributes.checked = false;  
    					}
    				}
    			}
    		}
    	}
    }
});   

//判断节点是否在操作队列中
function isInOperatorArr(node){
	if(operaMenus!=null && operaMenus.length>0){
		for(var i=0;i<operaMenus.length;i++){
			if(operaMenus[i].id==node.id){
				return true;
			}
		}
		return false;
	}
	return false;
}

// set the root node   
var root = new Tree.AsyncTreeNode({   
    text: 'FTSP3000',   
    draggable:false,   
    id:'0',
    leaf:false,
    checked:false,
    expand:true
});   
tree.setRootNode(root); 


//节点状态改变时触发
tree.on('checkchange', checkChange);  
function checkChange(node, checked){  
		if(checked){
			var brothers=node.parentNode.childNodes;
			for(var i=0;i<brothers.length;i++){
				if(brothers[i].id!=node.id){
					brothers[i].ui.toggleCheck(false);       
					brothers[i].attributes.checked = false;  
				}
			}
		}else{
			for(var j=0;j<operaMenus.length;j++){
	    		if(operaMenus[j].id==node.id){
	    			operaMenus.splice(j,1);
	    		}
	    	}
		}
}  


var store=new Ext.data.ArrayStore({
	data: [
	       ],
	fields:[
		   {name:"id",mapping:"id"},
		   {name:"text",mapping:"text"}
   ]
});

var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
	singleSelect :false
});

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
           width:250,
           dataIndex: 'text'
       }]
   });

var gridPanel = new Ext.grid.EditorGridPanel({
	id:"gridPanel",
	region:"center",
	stripeRows:true,
	autoScroll:true,
	frame:false,
	cm: columnModel,
	store:store,
	loadMask: true,
	height : 350,
	width : 250,
	hideHeaders:true,
	clicksToEdit: 2,//设置点击几次才可编辑  
	selModel:checkboxSelectionModel ,  //必须加不然不能选checkbox 
	viewConfig: {
       forceFit:false
   }
}); 

var formPanel = new Ext.FormPanel({
	region : "center",
	layout: 'form',
	frame : false,
	bodyStyle : 'padding:20px 20px 20px 20px',
	height : 600,
	width : 800,
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
			fieldLabel : '权限管理域名  <span style="color:red">*</span>',
			labelSeparator:' ', //表单label与其他元素朋分符
			width: 350,
			height : 25,
			allowBlank: false
		},{
			xtype : 'textarea',
			id : 'note',
			name : 'note',
			fieldLabel : '描述',
			labelSeparator:' ', //表单label与其他元素朋分符
		    width: 400,
		    height:30,
            allowBlank : false 
	},{
		layout: {
            type: 'hbox',
            pack: 'center',
            align: 'middle'          
        },
        width:800,
        items: [tree,
        {
        	height:400,
        	width:100,
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
                text: "<<",
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
            baseCls:'x-plain',
            border:true,
            items:gridPanel
        }]
    }],
	buttons : [{
				text : '确定', 
				handler : saveAuthRegion
			}, {
				text : '重置', 
				handler : resetPage
			}, {
				text : '取消', 
				handler : close
			}]
});

//重置
function resetPage(){
	var nodes = tree.getChecked();
	   if(nodes!=null && nodes.length>0){
		   for(var i=0;i<nodes.length;i++){
				   nodes[i].ui.toggleCheck(false);   
				   nodes[i].attributes.checked =false;  
		   }
	   }
	//重置新增
	if(saveType==0){
		store.loadData([]);   
	   return;
	}

	//重置修改
	operaMenus=[];
	if(menus!=null && menus.length>0){
		for(var i=0;i<menus.length;i++){
			operaMenus.push(menus[i]);
		}
	}
	store.loadData(operaMenus);  
	resetTreeStatus(root);
}

function resetTreeStatus(node){
	if(node.leaf){
		if(isNodeAtArr(node)){
			node.ui.toggleCheck(true);   
			node.attributes.checked =true;  
		}
	}
	node.eachChild(function(child) {       
		resetTreeStatus(child) ;
    }); 
}

function isNodeAtArr(node){
	for(var i=0;i<operaMenus.length;i++){
		if(operaMenus[i].id==node.id){
			return true;
		}
	}
	return false;
}


//点击后把树选中的节点加到右边
function addChooseNodes(){
	var nodes = tree.getChecked();
	   if(nodes!=null && nodes.length>0){
		   for(var i=0;i<nodes.length;i++){
				  if(!isNodeAtArr(nodes[i])){
					  var jsonData = {
							   "id":nodes[i].id, 
						       "text":nodes[i].getPath('text').substring(14)
					   };
					  operaMenus.push(jsonData); 
				  }
		   }
	   }
	store.loadData(operaMenus);    
}




//点击后把右边选中的列表去掉且从树中去掉
function removeChooseNodes(){
	var selectRecord =gridPanel.getSelectionModel().getSelections(); 
	if(selectRecord.length<=0){
		Ext.Msg.alert("提示","请选择需要去掉的选项!");
	}
	 for(var i = 0; i< selectRecord.length;i++){
	    	for(var j=0;j<operaMenus.length;j++){
	    		if(operaMenus[j].id==selectRecord[i].data.id){
	    			operaMenus.splice(j,1);
	    		}
	    		cancelTreeNodeByNodeId(selectRecord[i].data.id) //取消树中对应的节点
	    	}
	    	gridPanel.getStore().remove(selectRecord[i]);
	    	gridPanel.getView().refresh();
    }
	 
}

//取消节点的选择
function cancelTreeNodeByNodeId(id){
	   var nodes = tree.getChecked();
	   if(nodes!=null && nodes.length>0){
		   for(var i=0;i<nodes.length;i++){
			   if(nodes[i].id==id){
				   nodes[i].ui.toggleCheck(false);   
				   nodes[i].attributes.checked =false;  
			   }
		   }
	   }
	
}

//取消节点的选择框
function cancelNodeCheckBox(node){  
	node.ui.toggleCheck(false);   
    node.attributes.checked =false;  
    node.eachChild(function(child) {       
        child.attributes.checked = false;  
    });  
}  

function close() {
	var win = parent.Ext.getCmp('editWindow');
	if (win) {
		win.close();
	}
}





function saveAuthRegion() {
	if(!formPanel.getForm().isValid()){
		return ;
	}
	//获取选择的权限
	var authLists=[];
	for(var i=0;i<operaMenus.length;i++){
		authLists.push(operaMenus[i].id);
	}
	if(authLists==null || authLists.length==0){
		Ext.Msg.alert("提示","请选择需要绑定的权限!");
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
			"authLists":authLists
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
//	 //FormPanel自身带异步提交方式  
//	formPanel.getForm().submit({  
//	    url: 'device-region-manage!saveAuthRegion.action',  
//	    waitTitle : '请等待' ,  
//	    waitMsg: '正在提交中',  
//	    success:function(form,action){ 
//	    	parent.store.reload();
//	    	close();//关闭 dialog
//	    },  
//	    failure:function(form,action){  
//	    	Ext.Msg.alert('提示','保存失败！');  
//	    }  
//   });  
	
}

var menus;//获取权限域对应的所有菜单
var operaMenus=[];//新建一个数据供修改使用
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
			if(menus!=null && menus.length>0){
				for(var i=0;i<menus.length;i++){
					operaMenus.push(menus[i]);
				}
			}
			store.loadData(operaMenus);   
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
}


Ext.onReady(function() {
			root.expand();
			var win = new Ext.Viewport({
				id : 'win',
				loadMask : true,
				layout : 'border',
				items : [formPanel],
				renderTo : Ext.getBody()
			});
			if(saveType==1){//修改
				initModifyData();
				return;
			}
			
			Ext.Msg = top.Ext.Msg;
			Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
			document.onmousedown = function() {
				top.Ext.menu.MenuMgr.hideAll();
			}
			Ext.QuickTips.init(); // 开启悬停提示
			Ext.form.Field.prototype.msgTarget = 'side'; // 提示显示风格
		});

