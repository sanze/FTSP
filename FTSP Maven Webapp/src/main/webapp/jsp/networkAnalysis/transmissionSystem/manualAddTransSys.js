var step = 1; // 新增

var tabPanel = new Ext.TabPanel({
	region : 'center',
	activeTab : 1,
	width : 700,
	deferredRender : false,
	items : [ {
		layout : 'fit',
		id : 'tab1',
		title : '节点树',
		items : [ nodeTree ]
	}, {
		layout : 'fit',
		id : 'tab2',
		title : '拓扑图',
		items : [ topoPanel ]
	} ]
});

var centerPanel = new Ext.Panel({
	region : 'east',
	split:true,
	id : 'centerPanel',
	layout : 'card',
	width : 600,
	activeItem :0,
	autoScroll : true,
	autoDestroy:false,
	border : false,
	unstyled : true,
	items : [ {
        id: 'card-1',
        layout : 'fit',
        items: [stepOne],
        buttons:[{
        	text:'下一步',
        	id:'next1',
        	handler:function(){
        		if(!stepOne.getForm().isValid()){
        			return;
        		}
        		changeItem(centerPanel,1);
        		step = 2;
        		var s = topo.swf;
        		if(s.getSelectedNodes(4).length > 0){
        			Ext.getCmp("topobtn").enable();
        		}else{
        			Ext.getCmp("topobtn").disable();
        		}
        	}
        }]
        
    },{
        id: 'card-2',
        layout : 'fit',
        items: [stepTwo],
        buttons:[{
        	text : '上一步',
        	id : 'back2',
        	handler : function() {
        		changeItem(centerPanel,0);
        		step = 1;
        		Ext.getCmp("topobtn").disable();
        	}
        },{
        	text : '下一步',
        	id : 'next2',
        	handler : function (){
        		if(neStore.getCount()==0){
        			Ext.Msg.alert("提示","请选择网元！");
        			return;
        		}
        		changeItem(centerPanel, 2);
        		step = 3;
        		var idList = new Array();
        		neStore.each(function(r){
        			idList.push(r.get('neId'));
        		});
        		Ext.getCmp("topobtn").disable();
        		getLinkBetweenNe(idList);
        	}
        }]
    },{
        id: 'card-3',
        layout : 'fit',
        items: [stepThree],
        buttons:[{
        	text : '上一步',
        	id : 'back3',
        	handler : function() {
        		changeItem(centerPanel, 1);
        		step = 2;
        		var s = topo.swf;
        		if(s.getSelectedNodes(4).length > 0){
        			Ext.getCmp("topobtn").enable();
        		}else{
        			Ext.getCmp("topobtn").disable();
        		}
        	}
        },{//此部分代码有待优化
        	text : '拓扑预览',
        	id : 'preview',
        	handler : topoPreview
        },{
        	text : '保存',
        	id : 'save',
        	handler : function() {
        		saveAll();
        	}
        }]
    } ]
});

function topoPreview(){
	var rows = new Array();
	neStore.each(function(r) {
		generateNeData(r.get('neName'), r.get('neId'));
	});
	selectedLinkStore.each(function(r) {
		generateLinkData(r.get('aNeId'), r.get('zNeId'));
	});
	var pre = new Ext.Window({
		id : 'pre',
		title : '拓扑预览',
		width : Ext.getBody().getWidth() * 0.75,
		height : Ext.getBody().getHeight() - 80,
		isTopContainer : true,
		modal : true,
		layout : 'fit',
		plain : true, // 是否为透明背景
		items : [ {
			id : "previewTopo",
			xtype : 'flex',
			type : "tp",
			isLocalDebug : false
		} ],
		buttons:[{
			text:'关闭',
			handler:function(){
				pre.close();
			}
		}]
	});
	Ext.getCmp("previewTopo").on("initialize", function() {
		var dat = {
			"total" : rows.length,
			"currentTopoType" : "EMS",
			"colorWR" : "#800000",
			"colorCR" : "#ff0000",
			"isFirstTopo" : "yes",
			"colorMN" : "#ffff00",
			"parentType" : -1,
			"colorMJ" : "#ff8000",
			"rows" : rows,
			"parentId" : -1,
			"title" : "拓扑预览",
			"layout" : "round",
			"privilege" : "all",
			"colorCL" : "#00ff00"
		};
		Ext.getCmp("previewTopo").loadData(dat);

	});
	pre.show();
	function generateNeData(displayName, nodeId) {
		var neNode = {
			"displayName" : displayName,
			"neType" : "3",
			"nodeId" : nodeId,
			"nodeOrLine" : "node",
			"nodeType" : "1",
			"parentId" : "",
			"position_X" : "",
			"position_Y" : ""
		};
		rows.push(neNode);
	}
	;

	function generateLinkData(from, to) {
		var linkNode = {
			"fromNode" : from,
			"fromNodeType" : "3",
			"lineType" : "neLine",
			"nodeOrLine" : "line",
			"tipString" : "",
			"toNode" : to,
			"toNodeType" : "3"
		};
		rows.push(linkNode);
	}
}
/** ---------------------------------------------- */
/**
 * 替换组件
 * @param p 容器
 * @param i 要替换进去的子组件
 */
function changeItem(p,itemIndex){
	p.getLayout().setActiveItem(itemIndex); 
	p.doLayout();
}


/**
 * 保存系统的所有信息
 */
function saveAll(){
	if(!stepOne.getForm().isValid()){
		changeItem(centerPanel, 0);
		return;
	}
	var neList = new Array();
	neStore.each(function(r){
		neList.push(r.get('neId'));
	});
	var linkList = new Array();
	selectedLinkStore.each(function(r){
		linkList.push(r.get('linkId'));
	});
	var saveParams = {
			'paramMap.sysName':Ext.getCmp('sysName').getValue(),
			'paramMap.sysCode':Ext.getCmp('sysCode').getValue(),
			'paramMap.structure':Ext.getCmp('structure').getValue(),
			'paramMap.domain':Ext.getCmp('domain').getValue(),
			'paramMap.transMedium':Ext.getCmp('transMedium').getValue(),
			'paramMap.proType':Ext.getCmp('proType').getValue(),
			'paramMap.sysRate':Ext.getCmp('sysRate').getRawValue(),
			'paramMap.genMethod':Ext.getCmp('genMethod').getValue(),
			'paramMap.netLevel':Ext.getCmp('netLevel').getValue(),
			'paramMap.note':Ext.getCmp('note').getValue(),
			'paramMap.area':Ext.getCmp('area').getRawValue().id,
			'paramMap.waveCount':Ext.getCmp('waveCount').getValue(),
			'paramMap.nodeCount':neStore.getCount(),
			'paramMap.linkList':linkList.toString(),
			'intList':neList.length>0?neList:null
	};
	top.Ext.getBody().mask('正在执行，请稍候...');
	Ext.Ajax.request({
	    url: 'trans-system!newTransSystem.action', 
	    method : 'POST',
	    params: saveParams,
	    success: function(response) {
	    	top.Ext.getBody().unmask();
	    	var obj = Ext.decode(response.responseText);
			if(obj.returnResult){//新增成功
				stepOne.getForm().reset();
				neStore.removeAll();
				selectedLinkStore.removeAll();
				unselectedLinkStore.removeAll();
				changeItem(centerPanel, 0);
				Ext.Msg.alert("提示", obj.returnMessage);
			}else{
				Ext.Msg.alert("提示", obj.returnMessage);
			}
	    },
	    error:function(response) {
	    	top.Ext.getBody().unmask();
	    	Ext.Msg.alert("提示", obj.returnMessage);
	    },
	    failure:function(response) {
	    	top.Ext.getBody().unmask();
	    	Ext.Msg.alert("提示", obj.returnMessage);
	    }
	}); 	
}
Ext
		.onReady(function() {
			Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
			document.onmousedown = function() {
				top.Ext.menu.MenuMgr.hideAll();
			};

			Ext.Ajax.timeout = 90000000;

			var win = new Ext.Viewport({
				id : 'win',
				layout : 'border',
				items : [ centerPanel, tabPanel ]
			});
			win.show();

		});