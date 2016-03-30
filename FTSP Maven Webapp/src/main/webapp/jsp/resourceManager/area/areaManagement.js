var descLevel = [top.FieldNameDefine.AREA_ROOT_NAME, "省", "市", "区", "街道"];
var number_zh = ["零", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十"];

var LEVEL = [];
var areaRows=null;
var maxLevel = descLevel.length-2;
var curLevel = "";
var tree = null;
var westPanel = null;
var dataStore = null;
var gridPanel = null;
var centerPanel = null;
var win = null;
function prepareAndShow(){
	LEVEL = [];
	var dataIndex=["level","levelName"];
	var selModel = new Ext.grid.CheckboxSelectionModel();
	var columns=[
		new Ext.grid.RowNumberer({width : 26})/*, 
		selModel*/
	];
	areaRows=[];
	curLevel = "";
	maxLevel = descLevel.length-3;
	for(var i=0;i<maxLevel;i++){
		LEVEL.push({
			levelId:"level"+(i+1),
			areaId:"area"+(i+1),
			header:number_zh[i+1]+"级"+top.FieldNameDefine.AREA_NAME,
			value:descLevel[i+1]
		});
		dataIndex.push(LEVEL[i].levelId);
		columns.push({
		    id : LEVEL[i].levelId,
		    header : LEVEL[i].header,
		    dataIndex : LEVEL[i].levelId,
		    levelValue: LEVEL[i].value,
		    width: 100,
		    renderer : function (v, m, r) {
		        if (v != "" && this.levelValue && this.levelValue.trim() != "")
		            v += "(" + this.levelValue + ")";
				return v;
		    }
		});
		areaRows.push({
			xtype:'textfield',
			id: LEVEL[i].areaId,
			name: LEVEL[i].areaId,
			fieldLabel: LEVEL[i].header,
			sideText : '<font color=red>*</font>',
			allowBlank:false,
			value: LEVEL[i].value,
			regex : (i<maxLevel-1||maxLevel>=10)?/^[^,]*$/:/^[^,]*,?[^,]*$/,
			width:150
		});
	}
	columns.push({
	    id : 'level',
	    header : top.FieldNameDefine.AREA_NAME+'级别',
	    dataIndex : 'level'
	}, {
	    id : 'levelName',
	    header : top.FieldNameDefine.AREA_NAME+'名称',
	    dataIndex : 'levelName',
	    renderer : function (v, m, r) {
	    	return curLevel;
	    }
	});

	westPanel = {
        region : "west",
        title : top.FieldNameDefine.AREA_NAME+'选择',
        width : 280,
        minSize : 230,
//        maxSize : 320,
        autoScroll : true,
        forceFit : true,
        collapsed : false,
        collapsible : false,
        collapseMode : 'mini',
        split : true,
        id : "tree",
        xtype : "area",
        maxLevel : maxLevel,
        checkModel : "single",
        listeners :{
        	click:function(node){
        		var names = descLevel;
        		curLevel = names[(node.id.split("-")[1]>>0)+1];
        		dataStore.baseParams.node = node.id;
        		dataStore.load();
        	}
        }
    };

	dataStore = new Ext.data.Store({
        url : 'area!getAreaGrid.action',
        baseParams : {
        	node : "0-0-0"
        },
        reader : new Ext.data.JsonReader({
            totalProperty : 'total',
            root : "rows"
        }, dataIndex)
    });

	var cm = new Ext.grid.ColumnModel({
        // specify any defaults for each column
        defaults : {
            sortable : true,
            width : 100
            // columns are not sortable by default
        },
        columns : columns
    });
	gridPanel = new Ext.grid.GridPanel({
        id : "gridPanel",
        autoScroll : true,
        // title:'用户管理',
        cm : cm,
        border : false,
        store : dataStore,
        stripeRows : true, // 交替行效果
        loadMask : true,
//        selModel : selModel, // 必须加不然不能选checkbox
        forceFit : true,
        frame : false
    });

	centerPanel = new Ext.Panel({
		id : 'centerPanel',
        region : 'center',
        //border : false,
        layout : 'fit',
        autoScroll : true,
        tbar : ["-", {
                xtype : 'button',
                icon : '../../../resource/images/btnImages/add.png',
                privilege:addAuth,
                text : '新增',
                handler : newArea
            }, {
                xtype : 'button',
                icon : '../../../resource/images/btnImages/delete.png',
                text : '删除',
                privilege:delAuth,
                handler : delArea
            }, {
                xtype : 'button',
                icon : '../../../resource/images/btnImages/modify.png',
                text : '修改',
                privilege:modAuth,
                handler : modArea
            }, "-", {
                xtype : 'button', 
                text : '级别名称',
                privilege:modAuth,
                handler : modProperty
            }
        ],
        items : [gridPanel]
    });
	win = new Ext.Viewport({
        id : 'win',
        layout : 'border',
        items : [westPanel, centerPanel],
        renderTo : Ext.getBody()
    });
	win.on("afterrender", function(){
		initPage();
	});
	win.on("beforeshow", function(){
		initPage();
	});
	win.show();
	tree = Ext.getCmp("tree");
}

/**
 * 添加 区域
 */
function newArea() {
	var nodes = tree.getSelectedNodes();
	
	if(!nodes.total){
		//如果没有选中，提示勾选
		Ext.Msg.alert('提示', '请先勾选一个节点！');
	}else{
//		console.dir(nodes);
		var prtId = nodes.nodes[0].id;
		var prtLevel = nodes.nodes[0].level;
		if(prtLevel == LEVEL.length){
			Ext.Msg.alert('提示', '无法对“'+LEVEL[LEVEL.length-1].header+'”进行子'+top.FieldNameDefine.AREA_NAME+'新增！');
			return;
		}
//		console.log(prtId + "        " + prtLevel);
		Ext.Msg.prompt('新增'+top.FieldNameDefine.AREA_NAME, top.FieldNameDefine.AREA_NAME+'名称:', function(btn, text){
		    if (btn == 'ok'){
//		        console.log(text);
		    	if(text!=null && text.length>0){
			    	//下一步：根据prtId、prtLevel、text新增节点
			    	//1. Ajax 请求
			    	Ext.Ajax.request({
			    		url:"area!addArea.action",
			    		params:{
			    			node:prtId + "-" + prtLevel + "-" + "999",
			    			areaName: text
			    		},
			    		success:function(response){
			    			var obj = Ext.decode(response.responseText);
							if (obj.returnResult == 1) {
								nodes.nodes[0].node.reload();
								dataStore.load();
								Ext.Msg.alert('提示', obj.returnMessage);
							} else {
								Ext.Msg.alert('提示', obj.returnMessage);
							}
			    		}
			    	});		    		
		    	}else{
		    		Ext.Msg.alert('提示', '请输入'+top.FieldNameDefine.AREA_NAME+'名称！');
		    	}

		    }
		});

	}
}
/**
 * 删除 区域
 */
function delArea() {
	var nodes = tree.getSelectedNodes();
	if(!nodes.total){
		//如果没有选中，提示勾选
		Ext.Msg.alert('提示', '请选择需要删除的'+top.FieldNameDefine.AREA_NAME+'！');
	}else{
		var nodeId = nodes.nodes[0].node.id;
		var lvl = nodes.nodes[0].level;
		if(lvl == 0){
			//根节点无法被删除
			Ext.Msg.alert('提示', '根节点无法被删除！');
			return;
		}
		Ext.Msg.confirm("提示","确认删除？",function(btn){
			//console.log(btn);
			if(btn=="yes"){
				//确认删除
				Ext.Ajax.request({
		    		url:"area!delArea.action",
		    		params:{
		    			node:nodeId
		    		},
		    		success:function(response){
		    			var obj = Ext.decode(response.responseText);
		    			if(obj.returnResult == 1){
			    			nodes.nodes[0].node.parentNode.reload();
			    			Ext.Msg.alert('提示', obj.returnMessage);
		    			}else{
			    			Ext.Msg.alert('提示', obj.returnMessage);
		    			}
		    		}
		    	});	
			}
		});
	}
}
function modArea(){
	var nodes = tree.getSelectedNodes();
	if(!nodes.total){
		//如果没有选中，提示勾选
		Ext.Msg.alert('提示', '请选择需要修改的'+top.FieldNameDefine.AREA_NAME+'！');
	}else{
		var curNode = nodes.nodes[0];
		if(curNode.level == 0){
			Ext.Msg.alert('提示', '请选择'+LEVEL[0].header+'到'+LEVEL[LEVEL.length-1].header+'！');
			return;
		}
//		console.dir(curNode);
		var oldParent = nodes.nodes[0].node.parentNode;
		var prtName = oldParent.text;
		var oldName = curNode.text;
		var revIndex = oldName.lastIndexOf("(");
		if(revIndex>=0)
			oldName = oldName.substr(0, revIndex);
		var oldLevel = curNode.level;
		var modWin = new Ext.Window({
    		title: '修改'+top.FieldNameDefine.AREA_NAME,
    		id : 'modAreaWin',
    		layout : 'form',
    		modal : true,
    		closable:true,
    		plain:true,
    		width: 300,
    		height: 120,
    		items : [{
    			xtype:"textfield",
    			id:"modAreaName",
    			width:150,
				labelWidth:60,
				value:oldName,
    			fieldLabel:top.FieldNameDefine.AREA_NAME+"名称"
			},{
				xtype:"areaselector",
				id:"modAreaSelector",
				width:170,
				winWidth:210,
				winHeight:280,
				labelWidth:60,
				value:prtName,
				targetLevel:oldLevel - 1,
				disabled:oldLevel==1,
				fieldLabel:"所属"+top.FieldNameDefine.AREA_NAME
			}],
    	    buttons: [{
    	        text: '确定',
    	        handler: function(){
    	        	//ms = Ext.getCmp("areaFinder");
    	        	var newName = Ext.getCmp("modAreaName").getValue();
    	        	var newParent = Ext.getCmp("modAreaSelector").getRawValue();
//    	        	console.log("oldParent = ");
//    	        	console.dir(oldParent);
//    	        	console.log("newParent = ");
//    	        	console.log(newParent);
//    	        	console.log("oldName == newName = " + (oldName == newName));
//    	        	console.log("!newParent.id = " + !(newParent.id));
//    	        	return;
    	        	if(oldName == newName && (!newParent.id)){
    	        		Ext.Msg.alert('提示', '请修改'+top.FieldNameDefine.AREA_NAME+'名称或者所属'+top.FieldNameDefine.AREA_NAME+'！');
    	        		return;
    	        	}else{
    	        		Ext.Ajax.request({
    			    		url:"area!modArea.action",
    			    		params:{
    			    			node:curNode.node.id,
    			    			areaName:newName,
    			    			newParentId: (!newParent.id) ? curNode.parentId:newParent.id
    			    		},
    			    		success:function(response){
    			    			var obj = Ext.decode(response.responseText);
    			    			//nodes.nodes[0].node.parentNode.reload();
    			    			if(obj.returnResult == 1){
    			    				oldParent.reload();
        			    			if(!!newParent.id){
        			    				Ext.getCmp("tree").reloadNode(newParent.node.id);
        			    			}
        			    			Ext.Msg.alert('提示', obj.returnMessage);
    			    			}else{
    				    			Ext.Msg.alert('提示', obj.returnMessage);
    			    			}
    			    		}
    			    	});	
    	        	}
    	        	modWin.close();
    	        }
    	    },{
    	        text: '取消',
    	        handler: function(){
    	        	modWin.close();
    	        }
    	    }],
    	    buttonAlign:"center"
    	});
		modWin.show();
	}
}
function modProperty(){
	var propFormItem = [];
	propFormItem.push(areaRows);
	propFormItem.push({
		xtype:'label',
		hidden:true,
		html:'<font color=red>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*为必填项</font>'
	});
	var propForm = new Ext.FormPanel({
		id:"propForm",
	    frame:false,
	    border:false,
	    bodyStyle:'padding:10px 10px 0 10px',
//		height: 95,
	    labelWidth: 80,
	    labelAlign: 'left',
	    split:true,
	    items: [propFormItem]
	});
	var win = new Ext.Window({
	    title: '级别名称',
	    id : 'selAreaWin',
//	    layout : 'fit',
	    modal : true,
	    closable:true,
	    plain:true,
	    closeAction:'close',
	    width: 300,
//	    height: 190,
	    items : [propForm],
	    buttons: [{
	    	scope:this,
	        text: '确定',
	        handler: function(){
	        	var bf=win.items.get(0).getForm();
	        	if(!bf.isDirty()||!bf.isValid()){
	        		return;
	        	}
	        	var param = [];
	        	for(var i=0;i<maxLevel;i++){
	        		param.push(Ext.getCmp(LEVEL[i].areaId).getValue());
	        	}
				param = param.join(",");
				Ext.Ajax.request({
					url:"area!setAreaProperty.action",
					params:{
						areaName : param
					},
					method:"POST",
					success:function(response){
						var result = Ext.util.JSON.decode(response.responseText);
				    	if(result&&(0==result.returnResult)){
				    		Ext.Msg.alert("提示",result.returnMessage);
				    	}else if(!!result){
							Ext.Msg.alert('提示', '级别名称修改成功！',function(){location.reload(true)});
							win.close();
						}else{
			    			Ext.Msg.alert('提示', '级别名称修改失败！');
						}
					}
				});
	        	
	        }
	    },{
	        text: '取消',
	        handler: function(){
	            win.close();
	        }
	    }],
	    buttonAlign:"right"
	    });
	win.show(this);
	
}
function getProperty(callback){
	Ext.Ajax.request({
		url:"area!getAreaProperty.action",
		method:"POST",
		success:function(response){
			var result = Ext.util.JSON.decode(response.responseText);
	    	if(result&&(0==result.returnResult)){
	    		Ext.Msg.alert("提示",result.returnMessage);
	    	}else if(!!result){
	    		descLevel = result;
	    		if(callback)
	    			callback();
			}else{
				Ext.Msg.alert('提示', '获取'+top.FieldNameDefine.AREA_NAME+'级别名称失败！');
			}
		}
	});	
}
function init(){
    getProperty(prepareAndShow);
}
Ext.onReady(function () {
	Ext.BLANK_IMAGE_URL = "data:image/gif;base64,R0lGODlhAQABAID/AMDAwAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==";

    Ext.Ajax.timeout = 900000;
    document.onmousedown = function () {
        top.Ext.menu.MenuMgr.hideAll();
    };
    // Ext.Msg = top.Ext.Msg;
    init();
});