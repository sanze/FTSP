
/**
 * 作为保存area selector 状态的对象
 */
var original ={};

// -----------------------------------


var tabPanel = new Ext.TabPanel({
	region : 'west',
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
//----------------这里定义拓扑图相关----------------
var previewTopo = new Ext.Flex({
	id : "previewTopo",
	type : "tp",
	isLocalDebug : false
});

Ext.getCmp("previewTopo").on("initialize", function() {

	var rows = new Array();
	neStore.each(function(r) {
		generateNeData(r.get('neName'), r.get('neId'));
	});
	selectedLinkStore.each(function(r) {
		generateLinkData(r.get('aNeId'), r.get('zNeId'));
	});
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
	};

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

});

//---------------------------------

var centerPanel = new Ext.TabPanel({
	region : 'center',
	id : 'centerPanel',
	activeTab : 0,
	autoScroll : true,
	deferredRender : false,
	unstyled : true,
	items : [ {
		layout : 'fit',
		id : 'centerTab1',
		title : '系统属性',
		items : [ stepOne ],
		buttons:[{
			text : '重置',
			id : 'reset1',
			handler : resetStepOne
			},{
			text : '保存',
			id : 'save1',
			handler : saveSys}]
	}, {
		layout : 'fit',
		id : 'centerTab2',
		title : '系统内网元',
		items : [ stepTwo ],
		buttons:[{
			text : '重置',
			id : 'reset2',
			handler : resetStepTwo
			},{
			text : '保存',
			id : 'save2',
			handler : checkIfNeDeletable}]
	}, {
		layout : 'fit',
		id : 'centerTab3',
		title : '系统内链路',
		items : [ stepThree ],
		buttons:[{
			text : '重置',
			id : 'reset3',
			handler : resetStepThree
			},{
			text : '保存',
			id : 'save3',
			handler : saveLink}]
	}, {
		layout : 'fit',
		id : 'centerTab4',
		title : '拓扑图',
		items : [ previewTopo ]
	} ],
	listeners:{
		//要判断修改有没有保存，吐了。
		beforetabchange : function(t,newPanel,currentPanel){
			if(!currentPanel)
				return true;
			var id = currentPanel.items.items[0].id;
			if(id=="stepOne"){
				if(stepOne.getForm().isDirty()){
					console.log("11111111");
					Ext.Msg.confirm("提示","修改未保存，是否需要保存？",function(btn){
						if(btn=='yes'){
							saveSys(newPanel);
						}else{
							resetStepOne();
							t.activate(newPanel);
						}
					});
				}else{
					return true;
				}
			}else if(id=="stepTwo"){
				var neList = new Array();
				neStore.each(function(r){
					neList.push(r.get('neId'));
				});
				if(neList.sort().toString()!=original.neList.sort().toString()){
					Ext.Msg.confirm("提示","修改未保存，是否需要保存？",function(btn){
						if(btn=='yes'){
							checkIfNeDeletable(newPanel);
						}else{
							resetStepTwo(newPanel);
						}
					})
				}else{
					return true;
				}
			}else if(id=="stepThree"){
				var linkList = new Array();
				selectedLinkStore.each(function(r){
					linkList.push(r.get('linkId'));
				});
				if(linkList.sort().toString()!=original.linkList.sort().toString()){
					Ext.Msg.confirm("提示","修改未保存，是否需要保存？",function(btn){
						if(btn=='yes'){
							saveLink(newPanel);
						}else{
							resetStepThree(newPanel);
						}
					})
				}else{
					return true;
				}
			}else{
				return true;
			}
			
			return false;
		},
		tabchange:function(t,tab){
			if(tab.id=='centerTab2'){
				step = 2;
				var s = topo.swf;
			    if(s.getSelectedNodes(4).length > 0){
			        Ext.getCmp("topobtn").enable();
			    }else{
			    	Ext.getCmp("topobtn").disable();
			    }
			}else{
				step = 0;
				Ext.getCmp("topobtn").disable();
				return;
			}
		}
	}
});
//-------------------------------------------------

/** ---------------------------------------------- */

/**
 * 重置表单，因为area特殊，所以要单独设置
 */
function resetStepOne(){
	stepOne.getForm().reset();
	Ext.getCmp('area').getRawValue().id=original.RESOURCE_AREA_ID;
}
/**
 * 重置网元
 */
function resetStepTwo(newPanel) {
	if (original.neList.length > 0) {
		neStore.baseParams = {
			'intList' : original.neList
		};

		neStore.load({
			callback : function(records, options, success) {
				if(!!newPanel)
					centerPanel.activate(newPanel);
				if (!success)
					Ext.Msg.alert("提示", "查询失败！");
			}
		});
	} else {
		neStore.removeAll();
	}

}

function resetStepThree(newPanel){
	var params = {
			'paramMap.transSysId':id
	};
	var linkList = new Array();
	selectedLinkStore.baseParams = params;
	selectedLinkStore.load({
		callback : function(records, options, success) {
			records.every(function(r){
				linkList.push(r.get('linkId'));
				return true;
			});
			original.linkList = linkList;
			if(!!newPanel)
				centerPanel.activate(newPanel);
			getLinkBetweenNe(original.neList,null,id);
			if (!success)
				Ext.Msg.alert("提示", "查询失败！");
		}
	});
}

/**
 * 适应本页面的getValues方法
 * @returns {___anonymous3728_3729}
 */
function getValues(){
	var values = {};
	values.sysName=Ext.getCmp('sysName').getValue();
	values.sysCode=Ext.getCmp('sysCode').getValue();
	values.structure=Ext.getCmp('structure').getValue();
	values.domain=Ext.getCmp('domain').getValue();
	values.transMedium=Ext.getCmp('transMedium').getValue();
	values.proType=Ext.getCmp('proType').getValue();
	values.sysRate=Ext.getCmp('sysRate').getRawValue();
	values.genMethod=Ext.getCmp('genMethod').getValue();
	values.netLevel=Ext.getCmp('netLevel').getValue();
	values.note=Ext.getCmp('note').getValue();
	values.waveCount=Ext.getCmp('waveCount').getValue();
	values.nodeCount=neStore.getCount();
	return values;
}
/**
 * 保存系统信息
 * @param newPanel
 */
function saveSys(newPanel) {
	if(!stepOne.getForm().isValid()){
		return;
	}
	var saveParams = {
			'paramMap.transSysId':id,
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
			'paramMap.nodeCount':neStore.getCount()
	};
	top.Ext.getBody().mask('正在执行，请稍候...');
	Ext.Ajax.request({
	    url: 'trans-system!updateTransSystem.action', 
	    method : 'POST',
	    params: saveParams,
	    success: function(response) {
	    	top.Ext.getBody().unmask();
	    	var obj = Ext.decode(response.responseText);
			if(obj.returnResult){//新增成功
				//成功后需要清掉dirty状态
				stepOne.getForm().setValues(getValues());
				Ext.getCmp('area').originalValue = Ext.getCmp('area').getValue();
				original.RESOURCE_AREA_ID = Ext.getCmp('area').getRawValue().id;
				if(!!newPanel)
					centerPanel.activate(newPanel);
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

function saveLink(newPanel) {
	var linkList = new Array();
	selectedLinkStore.each(function(r){
		linkList.push(r.get('linkId'));
	});
	var saveParams = {
			'paramMap.transSysId':id,
			'paramMap.nodeCount':neStore.getCount(),
			'paramMap.operType':'edit',
			'paramMap.linkList':linkList.toString()
	};
	top.Ext.getBody().mask('正在执行，请稍候...');
	Ext.Ajax.request({
		url: 'trans-system!updateTransSystemLink.action', 
		method : 'POST',
		params: saveParams,
		success: function(response) {
			top.Ext.getBody().unmask();
			var obj = Ext.decode(response.responseText);
			if(obj.returnResult){//新增成功
				original.linkList = linkList;
				Ext.Msg.alert("提示", "保存成功！");
				getLinkBetweenNe(original.neList,null,id);
				if(!!newPanel)
					centerPanel.activate(newPanel);
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

function checkIfNeDeletable(newPanel){
	var neList = new Array();
	neStore.each(function(r){
		neList.push(r.get('neId'));
	});
	var params;
	if(neList.length>0)
		params = {
				'paramMap.transSysId':id,
				'intList':neList
		};
	else
		params = {
			'paramMap.transSysId':id,
			'paramMap.noNe':1
	};
	Ext.Ajax.request({
	    url: 'trans-system!checkIfNeDeletable.action', 
	    method : 'POST',
	    params: params,
	    success: function(response) {
	    	top.Ext.getBody().unmask();
	    	var obj = Ext.decode(response.responseText);
			if(obj.returnResult == 1){
				saveNe(neList,0,newPanel);
			}else{
				Ext.Msg.confirm("提示", "网元变动将导致一些链路将被移除，确定继续吗？",function(btn){
					if(btn=='yes'){
						 saveNe(neList,1,newPanel);
					}else{
					}
				});
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

/**
 * 保存ne
 * @param neList
 * @param savelink 是否要包存link，发生在网元变动影响link时
 * @param panel 切换激活的panel
 */
function saveNe(neList,savelink,newPanel){
	var saveParams = {
			'paramMap.transSysId':id,
			'paramMap.nodeCount':neStore.getCount(),
			'paramMap.operType':'edit',
			'intList':neList
	};
	top.Ext.getBody().mask('正在执行，请稍候...');
	Ext.Ajax.request({
		url: 'trans-system!updateTransSystemNe.action', 
		method : 'POST',
		params: saveParams,
		success: function(response) {
			top.Ext.getBody().unmask();
			var obj = Ext.decode(response.responseText);
			if(obj.returnResult){//新增成功
				Ext.Msg.alert("提示", "保存成功！");
				if(savelink==1)
					getLinkBetweenNe(neList,newPanel,id);
				else
					getLinkBetweenNe(neList,null,id);
				
				original.neList = neList;
//				if(!!panel)
//					centerPanel.activate(panel);
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


/**
 * 初始化页面
 */
function init() {
	var params = {
			'paramMap.transSysId':id
	};
	top.Ext.getBody().mask('正在执行，请稍候...');
	Ext.Ajax.request({
	    url: 'trans-system!getTransSystem.action', 
	    method : 'POST',
	    params: params,
	    success: function(response) {
	    	top.Ext.getBody().unmask();
	    	var obj = Ext.decode(response.responseText);
			if(obj.transSys){
				var transSys = obj.transSys[0];
//				Ext.getCmp('sysName').setValue(transSys.SYS_NAME);
//				Ext.getCmp('sysCode').setValue(transSys.SYS_CODE);
//				Ext.getCmp('structure').setValue(transSys.TYPE);
//				Ext.getCmp('domain').setValue(transSys.DOMAIN);
//				Ext.getCmp('transMedium').setValue(transSys.TRANS_MEDIUM);
//				Ext.getCmp('proType').setValue(transSys.PRO_GROUP_TYPE);
//				Ext.getCmp('sysRate').setRawValue(transSys.RATE);
//				Ext.getCmp('genMethod').setValue(transSys.GENERATE_METHOD);
//				Ext.getCmp('netLevel').setValue(transSys.NET_LEVEL);
//				Ext.getCmp('note').setValue(transSys.NOTE);
//				Ext.getCmp('area').setRawValue(transSys.AREA_NAME);
//				Ext.getCmp('area').getRawValue().id=transSys.RESOURCE_AREA_ID;
//				Ext.getCmp('waveCount').setValue(transSys.WAVE_COUNT);
				stepOne.getForm().setValues({				
					sysName:transSys.SYS_NAME,
					sysCode:transSys.SYS_CODE,
					structure:transSys.TYPE,
					domain:transSys.DOMAIN,
					transMedium:transSys.TRANS_MEDIUM,
					proType:transSys.PRO_GROUP_TYPE,
					sysRate:transSys.RATE,
					genMethod:transSys.GENERATE_METHOD,
					netLevel:transSys.NET_LEVEL,
					note:transSys.NOTE,
					area:transSys.AREA_NAME,
					waveCount:transSys.WAVE_COUNT});
				if(type == 3){
					//详情页面不需要提示空
					stepOne.getForm().clearInvalid();
				}
				Ext.getCmp('area').getRawValue().id=transSys.RESOURCE_AREA_ID;
				original.RESOURCE_AREA_ID = transSys.RESOURCE_AREA_ID;
				var neList = obj.neList;
				original.neList = neList;
				neStore.baseParams = {
						'intList' : neList
					};
				if(neList.length>0)
					neStore.load({
						callback : function(records, options, success) {
							if (!success)
								Ext.Msg.alert("提示", "查询失败！");
						}
					});
					
				var linkList = new Array();
				selectedLinkStore.baseParams = params;
				selectedLinkStore.load({
					callback : function(records, options, success) {
						records.every(function(r){
							linkList.push(r.get('linkId'));
							return true;
						});
						original.linkList = linkList;
						getLinkBetweenNe(neList,null,id);
						if (!success)
							Ext.Msg.alert("提示", "查询失败！");
					}
				});
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
			init();
			if(type == 3){
				Ext.getCmp('save1').hide();
				Ext.getCmp('save2').hide();
				Ext.getCmp('save3').hide();
				Ext.getCmp('reset1').hide();
				Ext.getCmp('reset2').hide();
				Ext.getCmp('reset3').hide();
				centerPanel.suspendEvents(false);
				stepTwoGrid.getColumnModel().setHidden(4, true); 
				uslGrid.hide();
				slGrid.setHeight(380);
				Ext.getCmp('usbutton').hide();
			}
		});