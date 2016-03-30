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
    rootVisible:true,     //隐藏根节点    
    border:false,          //边框    
    animate:true,         //动画效果    
    autoScroll:true,      //自动滚动    
    enableDD:false,       //拖拽节点                 
    containerScroll:true,  
    checkModel :true,//为true表示复选框
    onCheckModel :true,//为true表示复选框
    pathSeparator:'-> ',//树节点路径的分隔符，默认为'/'
    loader: new Tree.TreeLoader({   
    	//dataUrl:'device-region-manage!getDeviceTreeNodes.action?id='+id+'&saveType='+saveType
    	dataUrl:"report-tree!getChildNodes.action?nodeType=1&id=0",
    }),
    listeners : {
    	click:function(n){
    		
    	},
    	load:function(node){
    	}
    	//beforeload:function(node){
    			//tree.loader.dataUrl='device-region-manage!getDeviceTreeNodes.action?id='+node.id+'&depth='+node.getDepth();   
    	//}
    }
});   
// set the root node   
var root = new Tree.AsyncTreeNode({   
    text: 'FTSP3000',   
    draggable:false,   
    id:'0',
    checked:false,
    expand:true
});   
tree.setRootNode(root); 

//选中节点时触发,如果选中父节点，则级联选中子节点，取消类似
tree.on('checkchange', checkChange);  
function checkChange(node, checked){  
	//取消选择框时,级联取消父节点的选择框
    node.attributes.checked = checked;  
    node.eachChild(function(child) {     
        child.ui.toggleCheck(checked);       
        child.attributes.checked = checked;  
    });  
}  



function cancelParentNode(node){
	if(node.parentNode){
		node.parentNode.attributes.checked=false;
		node.parentNode.ui.toggleCheck(false);
		cancelParentNode(node.parentNode);
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
           dataIndex: 'text'
       }]
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
	border:false,
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
			fieldLabel : '设备管理域名  <span style="color:red">*</span>',
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
        border:false,
        width:800,
        items: [{
            baseCls:'x-plain',
            layout:'form',
            height:350,
            border:true,
            items:[{
            	width: 250,
                height: 50,
                border:false,
            	layout: {
                    type: 'hbox',
                    pack: 'center',
                    align: 'middle'          
                },
                defaults:{margins:'0 15 0 0'},
    			items:[{
	    				width:80,
						height: 20,
    				    xtype : 'textfield',
    					id : 'queryCom',
    					name : 'queryCom',
    					fieldLabel : ''
    			},new Ext.Button({
    				width:50,
    				height: 20,
                	text: "查询",
         	        listeners: { "click": function () {
         	        	queryNodes();
         	        }
         	        },
         	        id: "bt3",
         	        scale: 'medium'
                })]
    		},
    		tree
		]
        },{
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
				handler : save
			}, {
				text : '重置',
				handler : resetPage
			}, {
				text : '取消',
				handler : close
			}]
});


function resetPage(){
	if(saveType==0){
		store.loadData([]);   
	}
	root.reload();
	loadWholeTree();
	
}

//点击后把树选中的节点加到右边
function addChooseNodes(){
	   var nodes = tree.getChecked();
	   var arr = new Array();
	   for(var i=0;i<nodes.length;i++){
		  var hasP=hasParentNodes(nodes[i],nodes);//判断节点在节点列表中是否有父节点
		  if(!hasP){//有跟节点,先加入跟节点
			  var jsonData = {
					   "id":nodes[i].id,
				       "text":nodes[i].getPath('text').substring(2)
			   };
			  arr.push(jsonData);
		  }
	   }
	  //加载数据到右边
	   loadDataFromLToR(arr);
}


//判断节点在节点列表中是否有父节点
function hasParentNodes(node,nodes){
	 var array=[];
	 while(node.parentNode){
		 array.push(node.parentNode);
		 node=node.parentNode;
	 }
	 for(var i=0;i<array.length;i++){
		 var isExists=isExistsAtArray(array[0],nodes);
		 if(isExists){
			 return true;
		 }
	 }
	 return false;
}

//节点在数组中是否存在
function isExistsAtArray(node,nodes){
	for(var i=0;i<nodes.length;i++){
		if(node.id==nodes[i].id){
			return true;
		}
	}
	return false;
}

function loadDataFromLToR(arr){
	store.loadData(arr);       
}



//点击后把右边选中的列表去掉且从树中去掉
function removeChooseNodes(){
	var selectRecord =gridPanel.getSelectionModel().getSelections(); 
	if(selectRecord.length<=0){
		Ext.Msg.alert("提示","请选择需要去掉的选项!");
	}
	 for(var i = 0; i< selectRecord.length;i++){
	    	var id=selectRecord[i].get("id");
	    	cancelNodeCheckBox(tree.getNodeById(id));
	    	//gridPanel.getStore().removeAt(selectRecord[i].get("id"));
	    	gridPanel.getStore().remove(selectRecord[i]);
	    	gridPanel.getView().refresh();
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


//根据右边选择的树节点获取所有网元
function getNeArraByChooseTree(){
//	var nodes = tree.getChecked();
//	var arr = new Array();
//	   for(var i=0;i<nodes.length;i++){
//		  if(nodes[i].leaf){//只加入叶子节点
//			  arr.push(nodes.id);
//		  }
//	   }
	var arr = new Array();
	var datas =gridPanel.getStore().data.items; 
	if(datas.length<=0){
		return null;
	}
	 for(var i = 0; i< datas.length;i++){
	    	var node=tree.getNodeById(datas[i].data.id);
	    	putLeafNodeToArr(arr,node);//放入节点,如果不是叶子节点，则往下找到叶子节点
    }
	 return arr;
}

//放入节点,如果不是叶子节点，则往下找到叶子节点
function putLeafNodeToArr(arr,node){
	if(node.leaf){
		arr.push(node.id.substring(2));
		return;
	}
	if(node.hasChildNodes){
		var childs=node.childNodes;
		for(var i=0;i<childs.length;i++){
			putLeafNodeToArr(arr,childs[i]);
		}
	}
}


//根据收索条件查询节点并定位到
function queryNodes(){
	 var queryText=Ext.getCmp("queryCom").getValue();
	 if(queryText==null || queryText=='' || queryText==undefined){
		 Ext.Msg.alert("错误",'请输入收索条件');
		 return;
	 }
	 tree.collapseAll();
	 root.expand();
	 findNodeByQueryComm(root,queryText);
}

//根据条件遍历节点，找到展开它
function findNodeByQueryComm(node, queryText){  
	var path=node.text;
	if(path.match(queryText)){
		if(node.parentNode){
			//级联扩展父节点
			expandParentNodeByChild(node.parentNode);
		}
	}
	if(node.hasChildNodes()){
		var childs=node.childNodes;
		for(var i=0;i<childs.length;i++){
				findNodeByQueryComm(childs[i],queryText);
		}
	} 
}  

//级联打开父节点
function expandParentNodeByChild(child){
	var parent=[];
	getParentNodes(child,parent);
	if(parent.length>0){
		for(var i=parent.length-1;i>=0;i--){
			parent[i].expand();
		}
	}
}

//获取节点的父节点
function getParentNodes(child,arr){
	arr.push(child);
	if(child.parentNode){
		arr.push(child.parentNode);
		getParentNodes(child.parentNode,arr);
	}
}


//子节点都选中时,则选中父节点
function iteratorNodes(node){  
    if(node.hasChildNodes()){
    	var childs=node.childNodes;
    	for(var i=0;i<childs.length;i++){
    		var child=childs[i];
    		if(child.attributes.checked && !node.attributes.checked){//如果节点是选中的则去判断兄弟节点是否都选中
    			var isCheck=isBrothersChecked(child);
    			if(isCheck){
    				node.ui.toggleCheck(true);     
    				node.attributes.checked=true;
    			}
    		}
    		iteratorNodes(child);
    	}
    	
    }
    
}  

//判断兄弟节点是否都选中
function isBrothersChecked(node){
	if(node.parentNode){
		var brothers=node.parentNode.childNodes;
		for(var i=0;i<brothers.length;i++){
			if(!brothers[i].attributes.checked){
				return false;
			}
		}
		return true;
	}
}



function save() {
	if(!formPanel.getForm().isValid()){
		return ;
	}
	//获取选择的网元数组
	var nes=getNeArraByChooseTree();
	if(nes==null || nes.length==0){
		Ext.Msg.alert("错误","请选择需要绑定的网元");
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
//        		var pageTool = parent.Ext.getCmp('pageTool');
//        		if(pageTool){
//					pageTool.doLoad(pageTool.cursor);
//				}
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

//修改时加载数据
function initModifyData(){
		var row = parent.selections[0].data;
		Ext.getCmp('name').setValue(row.name);
		Ext.getCmp('note').setValue(row.note);
		Ext.getCmp('name').disable(true);
}


//加载树
function loadWholeTree(){
	tree.render();   // render the tree   
	tree.body.mask('数据加载中...'); 
	tree.root.expand(false,false,function(){ 
		root.collapse(true,false,function(){
			tree.body.unmask(); 
			root.expand();
			if(saveType==1 || saveType==2){
				iteratorNodes(root);
				addChooseNodes();
			}
		});
	}); 
}

Ext.onReady(function() {
			//loadWholeTree();
			var win = new Ext.Viewport({
				id : 'win',
				loadMask : true,
				layout : 'border',
				items : [formPanel],
				renderTo : Ext.getBody()
			});
			root.expand();
			Ext.Msg = top.Ext.Msg;
			Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
			document.onmousedown = function() {
				top.Ext.menu.MenuMgr.hideAll();
			}
			Ext.QuickTips.init(); // 开启悬停提示
			Ext.form.Field.prototype.msgTarget = 'side'; // 提示显示风格
			if(saveType==1){//修改
				initModifyData();
			}
});

