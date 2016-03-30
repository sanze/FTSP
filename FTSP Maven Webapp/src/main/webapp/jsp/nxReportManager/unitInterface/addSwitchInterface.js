var preProCnt=null;
var ROOTNODE = {};
 //波长下拉框
var waveLen;
function genWL(){
	var store = new Ext.data.ArrayStore({
		fields : ['id','displayName'],
		data:[['192.1','192.1'], ['192.55','192.55'],['193','193'],['193.45','193.45'],
		      ['193.9','193.9'], ['194.35','194.35'],['194.8','194.8'],['195.25','195.25'],
		      ['195.7','195.7'], ['192.15','192.15'],['192.6','192.6'],['193.05','193.05'],
		      ['193.5','193.5'], ['193.95','193.95'],['194.4','194.4'],['194.85','194.85'],
		      ['195.3','195.3'], ['195.75','195.75'],['192.2','192.2'],['192.65','192.65'],
		      ['193.1','193.1'], ['193.55','193.55'],['194','194'],['194.45','194.45'],
		      ['194.9','194.9'], ['195.35','195.35'],['195.8','195.8'],['192.25','192.25'],
		      ['192.7','192.7'], ['193.15','193.15'],['193.6','193.6'],['194.05','194.05'],
		      ['194.5','194.5'], ['194.95','194.95'],['195.4','195.4'],['195.85','195.85'],
		      ['192.3','192.3'], ['192.75','192.75'],['193.2','193.2'],['193.65','193.65'],
		      ['194.1','194.1'], ['194.55','194.55'],['195','195'], ['195.45','195.45'],
		      ['195.9','195.9'], ['192.35','192.35'],['192.8','192.8'],['193.25','193.25'],
		      ['193.7','193.7'], ['194.15','194.15'],['194.6','194.6'],['195.05','195.05'],		      
		      ['195.5','195.5'], ['195.95','195.95'],['192.4','192.4'],['192.85','192.85'],
		      ['193.3','193.3'], ['193.75','193.75'],['194.2','194.2'],['194.65','194.65'],
		      ['195.1','195.1'], ['195.55','195.55'],['196','196'],['192.45','192.45'],
		      ['192.9','192.9'], ['193.35','193.35'],['193.8','193.8'],['194.25','194.25'],
		      ['194.7','194.7'], ['195.15','195.15'],['195.6','195.6'],['196.05','196.05'],
		      ['192.5','192.5'], ['192.95','192.95'],['193.4','193.4'],['193.85','193.85'],
		      ['194.3','194.3'], ['194.75','194.75'],['195.2','195.2'],['195.65','195.65']
		]
	});
	store.sort("id");
	waveLen = new Ext.form.ComboBox({
		id : 'waveLen' + Ext.id(),
		triggerAction : 'all',
		editable : true,
		mode:'local',
		selectOnFocus:false,
		typeAheadDelay:500,
		store : store,
		displayField : "displayName",
		valueField : 'id',	
		typeAhead:true,
		resizable: true
	});
	return waveLen;
}

Ext.apply(Ext.form.VTypes,{ 
	float:function(val,field)  
    {  
        try  
        {  
            if(/^[-+]?(([0-9]+\.[0-9]{2})|([0-9]+\.[0-9]{1})|([0-9]*[1-9][0-9]*))$/.test(val)) 
                return true;  
            return false;     
        }  
        catch(e)  
        {  
            return false;     
        }  
	},  
	floatText:'精确到小数点后2位',  
	range1_10:  function(v) {
		return /^[0-9]+$/.test(v) && parseInt(v)>0&& parseInt(v)<11;    
	},
	range1_10Text: '数据范围为1~10'
});

var params = {"paramMap.RESOURCE_UNIT_INTERFACE_ID":unitInterfaceId};  
var cmbType = new Ext.form.DisplayField({ 
	id:2,
    fieldLabel:"板卡类型", 
    value: "WDM光开关盘",
    width:120 
});
var lastUnit = -1;
var tree = new Ext.ux.EquipTreeCombo({
	id:'tree',
	xtype:"equiptreecombo",
	width:480,
	listWidth:null,
	sideText : '<font color=red>*</font>',
	rootVisible: false,
	leafType:6,
    checkableLevel: [6],
    checkModel:"single",
	fieldLabel:"板卡",
    allowBlank: false,
	listeners:{
		afterrender : function(){
			if(isMod){
				params["paramMap.BASE_UNIT_ID"] = unitId;
				params["paramMap.UNIT_INTERFACE_ID"] = unitInterfaceId;
				lastUnit = unitId;
				this.disable();
			}
		},
		"checkchange":function(node,checked){
//			var nodes = me.getCheckedNodes(["nodeId","nodeLevel","text","path:nodeId", "path:nodeLevel", "path:text"]); 
			if(checked == "all"){
				if(node.attributes.nodeLevel<5){
					Ext.Msg.alert("提示", "请选择板卡！");
					return;
				}
				var unitId = node.attributes.nodeId;
				params["paramMap.BASE_UNIT_ID"] = unitId;
				unitInterfaceStore.baseParams.jsonString = unitId;
				unitInterfaceStore.load(); 
				var i=0;
				var selection = tree.getCheckedNodes(["neId" ])
				var neId = selection[0].neId
				ROOTNODE = {
						id: neId,
						level:4
				}
//				console.log("选择了 = " + unitId);
//				interfaceStore.baseParams.jsonString = neId;
//				interfaceStore.load();
				//如果选择的不是之前那一块板卡
				if(lastUnit != unitId){
					lastUnit = unitId;
//					Ext.Msg.confirm("提示", "选择新的板卡会导致前面的数据全部丢失，是否确认选择？",
//						function(){
//					})
					//如果选择的是另一块板卡，则删除所有保护组
					Ext.getCmp('proField').removeAll();
					generateProtectGroup(0);
				}
//				while(!!Ext.getCmp('ptpTree'+i)){
//					Ext.getCmp('ptpTree'+i).treeField.setRoot(neId,4);
//					i++;
//				}
			}else{
//				console.log("取消选择");
				//取消选择时清空
				clearData();
			}
		}
	}
});
var northPanel = new Ext.form.FormPanel({
	id : 'northPanel',
	title : "板卡类型", 
	padding:'5px 5px 5px 5px',
    height : 90,
    items : [tree, {
        layout : 'hbox',
    	border : false,
        margins:"5",
        height:24,
        layoutConfig:{
        	align:"stretch",
        	pack:"start"
        },
		items : [{
			layout:"form",
	    	border : false, 
			width:260,
			items:[cmbType]
		}, {
			layout:"form",
	    	border : false, 
			width:180,
			items:[{
				id : "proGroupCnt",
		    	sideText : '<font color=red>*</font>',
				xtype : "textfield", 
				width:50,
				vtype:"range1_10",
				fieldLabel : "保护组数量",
				allowBlank : false,
				value:isMod?null:1,
				listeners:{
					'blur':function (me){
						//如果已经选择了板卡
						onProtectionGroupCountChange();
					}
				}
			}] 
		}]
    }]
});

var unitInterfaceStore = null;
(function(){
	var dataStore = new Ext.data.Store({
	    url : 'nx-report!getPtpByUnit.action',
	    reader : new Ext.data.JsonReader({
	        totalProperty : 'total',
	        root : "rows"
		    }, [ "WAVE_LENGTH", "SLOT_NAME", "DISPLAY_NAME", "SLOT_NO",
				"BASE_UNIT_ID", "BASE_PTP_ID", "PORT_NO", "MAX_IN", "MIN_IN" ])
	});
	unitInterfaceStore = dataStore;
	var selModel = new Ext.grid.CheckboxSelectionModel();
	var cm = new Ext.grid.ColumnModel({
	    columns : [new Ext.grid.RowNumberer(),
		selModel,{
			id : 'DISPLAY_NAME',
			header : '接口',
			dataIndex : 'DISPLAY_NAME',
            width:200
		}]
	});
	
	unitListPanel = new Ext.grid.GridPanel({
	    id : "unitListPanel",
	    autoScroll : true,
	    // title:'用户管理',
	    cm : cm,
	    border : true,
	    store : dataStore,
	    stripeRows : true, // 交替行效果
	    loadMask : true,
	    selModel : selModel, // 必须加不然不能选checkbox
	    forceFit : true,
	    frame : false
	});
})(); 

var CombineRec = new Ext.data.Record.create([
    	{name : 'BASE_PTP_ID'},  
    	{name : 'DISPLAY_NAME'}, 
    	{name : 'POWER_BUDGET'}, 
    	{name : 'BUSSINESS_NAME'}, 
    	{name : 'SWITCH_THRESHOLD'}
    ]);
var PartRec = new Ext.data.Record.create([
      	{name : 'BASE_PTP_ID'},  
    	{name : 'DISPLAY_NAME'}, 
      	{name : 'POWER_BUDGET'}, 
		{name : 'DIRECTION'} 
	]);
var UnitRec = new Ext.data.Record.create([
    	{name : 'BASE_PTP_ID'}, 
    	{name : 'PTP_NAME'}, 
    	{name : 'UNIT_NAME'}, 
    	{name : 'SLOT_NAME'}, 
    	{name : 'MIN_IN'},
    	{name : 'WAVE_LENGTH'}
    ]); 
 

var interfaceStore;
(function() {
	var data = [['接口1', '1', 'unit1'],['接口2', '2', 'unit2'],
			['接口3', '3', 'unit3'],['接口4', '4', 'unit4']];
	interfaceStore = new Ext.data.ArrayStore({
		fields : ["DISPLAY_NAME","SLOT_NO","NAME" ]
	});
	interfaceStore.loadData(data);
})(); 

function generateEl(i){
	var combineStore = new Ext.data.Store({
	    url : 'nx-report!getUsedPtp.action',
	    reader : new Ext.data.JsonReader({
	        totalProperty : 'total',
	        root : "rows",
	        fields:CombineRec
	    })
	});
	if(isMod){
//		console.log("mod - comb");
		combineStore.baseParams = {
	            "paramMap.unitInterfaceId" : unitInterfaceId,
	            "paramMap.groupNum" : i,
	            "paramMap.ptpType" : 3
	        };
		combineStore.load();
	}
	var selModel = new Ext.grid.CheckboxSelectionModel();
	var cm = new Ext.grid.ColumnModel({ 
	    defaults : {
	        sortable : true,
	    	width:110
	    },
	    columns : [new Ext.grid.RowNumberer({
			width : 26
		}),
		selModel,{
			id : 'DISPLAY_NAME',
			header : '接口', 
	    	width:140,
			isCellEditable:false,
			dataIndex : 'DISPLAY_NAME'
		},{
			id : 'POWER_BUDGET',
			header : '预算功率(dBm)', 
			isCellEditable:true, 
			dataIndex : 'POWER_BUDGET', 
	        editor: new Ext.form.TextField({
	            allowBlank: false,
	            vtype:"float"
	        })
		},{
			id : 'BUSSINESS_NAME',
			header : '<b>业务名称</b>', 
			isCellEditable:true,
			dataIndex : 'BUSSINESS_NAME',
			editor: new Ext.form.TextField({ 
	        })
		},{
			id : 'SWITCH_THRESHOLD',
			header : '倒换门限(dBm)',
			isCellEditable:true,
			width:270,
			dataIndex : 'SWITCH_THRESHOLD', 
	        editor: new Ext.form.TextField({
	            allowBlank: false,
	            vtype:"float"
	        })
		}]
	}); 
	var combinePanel = new Ext.grid.EditorGridPanel({
	    id : "combinePanel"+i,
	    autoScroll : true, 
	    cm : cm, 
	    title:'和路口',
	    flex:1,
	    border : true,
	    store : combineStore,
	    stripeRows : true, // 交替行效果
	    loadMask : true,
	    selModel : selModel, // 必须加不然不能选checkbox
	    forceFit : true,
	    frame : false
	});
	
	var partStore = new Ext.data.Store({
	    url : 'nx-report!getUsedPtp.action',
	    reader : new Ext.data.JsonReader({
	        totalProperty : 'total',
	        root : "rows",
	        fields:PartRec
	    })
	}); 
	if(isMod){
//		console.log("mod - part");
		partStore.baseParams = {
	            "paramMap.unitInterfaceId" : unitInterfaceId,
	            "paramMap.groupNum" : i,
	            "paramMap.ptpType" : 4
	        };
		partStore.load();
	}
	var selModel = new Ext.grid.CheckboxSelectionModel();
	var cm = new Ext.grid.ColumnModel({
	    defaults : {
	        sortable : true,
	    	width:110,
	        isCellEditable : true
	    },
	    columns : [new Ext.grid.RowNumberer({
			width : 26
		}),
		selModel, {
			id : 'DISPLAY_NAME',
			header : '接口', 
			isCellEditable:false,
			dataIndex : 'DISPLAY_NAME'
		},{
			id : 'POWER_BUDGET',
			header : '预算功率(dBm)',
			isCellEditable:true, 
			dataIndex : 'POWER_BUDGET', 
	        editor: new Ext.form.TextField({
	            allowBlank: false,
	            vtype:"float"
	        })
		},{
			id : 'DIRECTION',
			header : '<b>方向</b>',
			width:410,
			isCellEditable:true,
			dataIndex : 'DIRECTION', 
	        editor: new Ext.form.TextField({
	            allowBlank: false
	        })
		}]
	}); 
	var ptpTree = new Ext.ux.EquipTreeCombo({
		id:'ptpTree' + i,
		xtype:"equiptreecombo",
		width:480,
		listWidth:null,
		rootVisible: true,
		rootId :(!!ROOTNODE.id)?ROOTNODE.id:0,
		rootType :(!!ROOTNODE.level)?ROOTNODE.level:0,
		leafType:8,
	    checkableLevel: [8],
	    checkModel:"single",
		fieldLabel:"板卡",
		listeners:{
			"valid":function(me){
				var nodes = me.getCheckedNodes(["nodeId","nodeLevel","text","path:nodeId", "path:nodeLevel", "path:text"]); 
				if(!!nodes && nodes.length > 0){
					if(nodes[0].nodeLevel<6){
						Ext.Msg.alert("提示", "请选择PTP！");
						return;
					}
					var ptpId = nodes[0].nodeId;
					var nodeIdArray = nodes[0]['path:nodeId'].split(":");
//					console.log("nodeIdArray = " + nodeIdArray.join(":"));
//					console.log("ptpId = " + ptpId);
//					console.log("ID = " + this.id);
//					console.log("txt = " + nodes[0].text);
					var idx = this.id.substr(7, this.id.length);
					var store = Ext.getCmp("unitPanel" + idx).getStore();
					store.removeAll();
//					console.log(store);
					Ext.Ajax.request({
						url : 'nx-report!getBusinessPtpInfo.action',
						params : {
							unitId : ptpId,
							_dc : (new Date()).getTime()
						},
						method : 'POST',
						success : function(response) {
							var result = Ext.util.JSON.decode(response.responseText);
							var dat = result.rows[0];
							var nbi = new UnitRec({
								BASE_PTP_ID : dat.BASE_PTP_ID,
								PTP_NAME : dat.PTP_NAME,
								UNIT_NAME : dat.UNIT_NAME,
								SLOT_NAME : dat.SLOT_NAME,
								MIN_IN : dat.MIN_IN,
								WAVE_LENGTH : dat.WAVE_LENGTH || ""
							});
							store.add(nbi);
						},
						failure : function(response) {
							Ext.getBody().unmask();
							var result = Ext.util.JSON.decode(response.responseText);
							Ext.Msg.alert("提示", result.returnMessage);
						},
						error : function(response) {
							Ext.getBody().unmask();
							var result = Ext.util.JSON.decode(response.responseText);
							Ext.Msg.alert("提示", result.returnMessage);
						}
					});
				}
			}
		}
	});
	var partPanel = new Ext.grid.EditorGridPanel({
	    id : "partPanel"+i,
	    autoScroll : true,
	    cm : cm, 
	    flex:1,
	    title:'分路口',
	    border : true,
	    store : partStore,
	    stripeRows : true, // 交替行效果
	    loadMask : true,
	    selModel : selModel, // 必须加不然不能选checkbox
	    forceFit : true,
	    frame : false
	}); 
	
	var unitStore = new Ext.data.Store({
	    url : 'nx-report!getSavedBusinessPtpInfo.action',
	    reader : new Ext.data.JsonReader({
	        totalProperty : 'total',
	        root : "rows",
	        fields:UnitRec
	    })
	});  
	if(isMod){
//		console.log("mod - unit");
		unitStore.baseParams = {
	            "paramMap.unitInterfaceId" : unitInterfaceId,
	            "paramMap.groupNum" : i,
	            "paramMap.ptpType" : 5
	        };
		unitStore.load();
	}
	var selModel = new Ext.grid.CheckboxSelectionModel();
	var cm = new Ext.grid.ColumnModel({
	    defaults : {
	        sortable : true,
	    	width:110,
	        isCellEditable : true
	    },
	    columns : [new Ext.grid.RowNumberer({
			width : 26
		}),
		selModel,{
			id : 'PTP_NAME',
			header : '业务板卡接口',  
//			editable:(neId==0)?false:true,
			dataIndex : 'PTP_NAME'
		},{
			id : 'UNIT_NAME',
			header : '业务板卡名称', 
			isCellEditable:false,
			dataIndex : 'UNIT_NAME'
		},{
			id : 'SLOT_NAME',
			header : '业务板卡槽道', 
			isCellEditable:false,
			dataIndex : 'SLOT_NAME'
		},{
			id : 'MIN_IN',
			width:150,
			header : '业务板卡灵敏度(dBm)', 
			isCellEditable:false,
			dataIndex : 'MIN_IN',
			renderer:function(v){
				if(!!v && (v+"").indexOf(".")<0){
					v+=".00";
				}
				return v;
			}
		},{
			id : 'WAVE_LENGTH',
			header : '波道/波长',
			width:150,
			isCellEditable:true,
			dataIndex : 'WAVE_LENGTH',
			editor:genWL()
		}]
	});
	
	var unitPanel = new Ext.grid.EditorGridPanel({
	    id : "unitPanel"+i,
	    autoScroll : true,
	    cm : cm, 
	    flex:1,
	    border : true,
	    store : unitStore,
	    stripeRows : true, // 交替行效果
	    loadMask : true,
	    selModel : selModel, // 必须加不然不能选checkbox
	    forceFit : true,
	    tbar:[{
			xtype:"displayfield",
	    	value:"对应业务板卡",
	    	style:'font-weight:bold;color:#003399'
		},'-',ptpTree,'-',{
			xtype:"button",
	    	text:"关联光口标准",
	    	id:i+'relate',
	        icon : '../../../resource/images/btnImages/associate.png',
	    	handler:function(){
	    		var recs = Ext.getCmp("unitPanel"+this.id.substring(0, 1)).getSelectionModel().getSelected(); 
	    		if(!!!recs||recs.length ==0){
	    			Ext.Msg.alert("提示","请至少选择一个接口！");
	    			return;
	    		}
	    		if(recs.get('BASE_PTP_ID')==null || recs.get('BASE_PTP_ID')==""){
	    			Ext.Msg.alert("提示","请先增加业务板卡接口！");
	    			return;
	    		}
	    		curId=this.id.substring(0, 1);
	    		showRelateOpticalStandardValue();
	    	}
		}],
	    frame : false
	});   
	//unitStore.add(new UnitRec({ }));
}

/**
 * 保护组数量变更
 */
function onProtectionGroupCountChange(){ 
//	console.log('onProtectionGroupCountChange');
	var curGroupCnt = Ext.getCmp('proGroupCnt').getValue();
	if(Ext.getCmp('proGroupCnt').isValid() && curGroupCnt!=""){ // && Ext.getCmp('tree').isValid()
//		console.log("preProCnt = " + preProCnt);
//		console.log("curProCnt = " + curGroupCnt);
		if(preProCnt!=null && preProCnt!=""){ 
			//如果新的保护组数量<之前的保护组数量，则直接删除
			if(curGroupCnt < preProCnt){
				for(var s=preProCnt-1; s>curGroupCnt-1; s--){
					Ext.getCmp('proField').remove(Ext.getCmp("fieldset_"+s));
				} 
//				return;
			}
			//如果新的保护组数量>之前的保护组数量，则直接追加
			if(Ext.getCmp('proGroupCnt').getValue()>preProCnt){
				generateProtectGroup(parseInt(preProCnt));
			} 
		}else{ 
			generateProtectGroup(0);
		}
		preProCnt=curGroupCnt;
	}
}
/**
 * 添加保护组
 * @param start 保护组起始ID
 */
function generateProtectGroup(start){  
//	console.log("generateProtectGroup(" + start + "");
	for(var i=start; i<Ext.getCmp('proGroupCnt').getValue(); i++){ 
		generateEl(i);
		var spacer = { 
			id:"fieldset_"+i,
			xtype:"fieldset",  
			title: '保护组'+ (i+1),  
			height:300,
			collapsed: false,  
			layout:"vbox",
			layoutConfig:{
				align:"stretch",
				pack:"start"
			},  
			items :[{  
			    layout:'fit',
			    xtype:"panel",
			    flex:3,
		 		layout : 'hbox',
		 		border:false,
		        layoutConfig:{
		         	align : "stretch",
		         	pack : "start"
		        },
		    	items:[{
					xtype:"panel",
					width:24,
					layout:"vbox",
					border:false,
		            layoutConfig:{
		            	align : "center",
		            	pack : "end"
		            },
					items:[{
						xtype:"button", 
						id:i+'up_up',
						text:"＞",
						handler:function(){ 
			            	var combinePanelID = "combinePanel"+this.id.substring(0, 1);
							var recs = unitListPanel.getSelectionModel().getSelections();
							var total = Ext.getCmp(combinePanelID).getStore().getCount();
							if((total + recs.length) > 1){
								Ext.Msg.alert("提示","和路口只支持一条数据！");
								return;
							}
							for ( var i = 0; i < recs.length; i++) { 
								addRecord(combinePanelID, recs[i]);
							}
						}
					},{
						xtype:"button",
						text:"＜",
						id:i+'up_down',
						handler:function(){
							var recs = Ext.getCmp("combinePanel"+this.id.substring(0, 1)).getSelectionModel().getSelections();
							Ext.getCmp("combinePanel"+this.id.substring(0, 1)).getStore().remove(recs); 
						}
					}]
				}, Ext.getCmp("combinePanel"+i)]
			},{  
			    layout:'fit',
			    xtype:"panel",
		 		layout : 'hbox',
		 	    flex:4,
		 		border:false,
		        layoutConfig:{
		         	align : "stretch",
		         	pack : "start"
		        },
		    	items:[{
					xtype:"panel",
					width:24,
					layout:"vbox",
					border:false,
		            layoutConfig:{
		            	align : "center",
		            	pack : "end"
		            },
					items:[{
						xtype:"button",
						id:i+'down_up',
						text:"＞",
						handler:function(){
			            	var partPanelID = "partPanel"+this.id.substring(0, 1);
							var recs = unitListPanel.getSelectionModel().getSelections();
							var total = Ext.getCmp(partPanelID).getStore().getCount();
							if((total + recs.length) > 2){
								Ext.Msg.alert("提示","分路口只支持两条数据！");
								return;
							}
							for ( var i = 0; i < recs.length; i++) { 
								addRecord(partPanelID, recs[i]);
							}
						}
					},{
						xtype:"button",
						id:i+'down_down',
						text:"＜",
						handler:function(){
							var recs = Ext.getCmp("partPanel"+this.id.substring(0, 1)).getSelectionModel().getSelections();
							Ext.getCmp("partPanel"+this.id.substring(0, 1)).getStore().remove(recs); 
						}
					}]
				}, Ext.getCmp("partPanel"+i)]
			},{
			    xtype:"panel",
			    flex:3,
			    layout : 'hbox',
			    border:false,
			    layoutConfig:{
			        align : "stretch",
			        pack : "start"
			    },
			    items:[{
			        xtype:"panel",
			        width:24,
			        layout:"vbox",
			        border:false,
			        layoutConfig:{
			            align : "center",
			            pack : "end"
			        },
			        items:[{
			            xtype:"button", 
			            id:i+'unit_add',
			            hidden:true,
			            text:"＞",
			            handler:function(){ 
			            	var unitPanelID = "unitPanel"+this.id.substring(0, 1);
//			            	console.log("unitPanelID = " + unitPanelID);
			                var recs = unitListPanel.getSelectionModel().getSelections();
			                var total = Ext.getCmp(unitPanelID).getStore().getCount();
//			            	console.log("total = " + total);
//			            	console.log("recs.length = " + recs.length);
			                if((total + recs.length) > 1){
			                    Ext.Msg.alert("提示","只支持一条数据！");
			                    return;
			                }
			                for ( var i = 0; i < recs.length; i++) { 
			                    addRecord(unitPanelID, recs[i]);
			                }
			            }
			        },{
			            xtype:"button",
			            text:"＜",
			            id:i+'unit_rem',
			            hidden:true,
			            handler:function(){
			            	var unitPanelID = "unitPanel"+this.id.substring(0, 1);
//			            	console.log("unitPanelID = " + unitPanelID);
			                var recs = Ext.getCmp(unitPanelID).getSelectionModel().getSelections();
			                Ext.getCmp(unitPanelID).getStore().remove(recs); 
			            }
			        }]
			    }, Ext.getCmp("unitPanel"+i)]
			}]
		};
	    Ext.getCmp('proField').add(spacer);
		Ext.getCmp('proField').doLayout();    
	}
}


var curId = null;
function relateOpticalStandardValue(rec){ 
	var optStdId = rec.get("optStdId");
	var minIn = rec.get("minIn"); 
	var recs = Ext.getCmp("unitPanel"+curId).getSelectionModel().getSelections();
	var modIds = [];
	for ( var i = 0; i < recs.length; i++) {
		modIds.push(recs[i].get("BASE_PTP_ID"));
	}
	
	var params = {"paramMap.ptpIds":modIds.toString(),"paramMap.optStdId":optStdId};
	Ext.getBody().mask('正在执行，请稍候...');
	Ext.Ajax.request({
		url : 'nx-report!relateOpticalStandardValue.action',
		params : params,
		method : 'POST',
		success : function(response) {
			Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			if (result.returnResult==0) {
				Ext.Msg.alert("提示", result.returnMessage);
			}
			if (result.returnResult==1) {
				Ext.Msg.alert("提示", result.returnMessage);
//				pageTool.doLoad(pageTool.cursor);
				for ( var i = 0; i < recs.length; i++) { 
					recs[i].set("MIN_IN", minIn);
					var ptpId = recs[i].get("BASE_PTP_ID");
					var idx = unitListPanel.getStore().find("BASE_PTP_ID", ptpId);
//					console.log("ptp @ " + idx)
					if(idx > -1){
						var r = unitListPanel.getStore().getAt(idx); 
						r.set("MIN_IN", minIn);
					}
				}
				Ext.getCmp("unitPanel"+curId).getStore().commitChanges(); 
			}
			
		},
		failure : function(response) {
			Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.Msg.alert("提示", result.returnMessage);
		},
		error : function(response) {
			Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.Msg.alert("提示", result.returnMessage);
		}
	});
}
function showRelateOpticalStandardValue(){
	var url = 'standardOpticalValueManage.jsp?authSequence=' + authSequence;
	var addUnitInterfaceWindow = new Ext.Window({
		id : 'relateOpticalStandardValueWindow',
		title : '关联光口标准',
		width: Ext.getBody().getWidth()*0.8,
		height: Ext.getBody().getHeight() * 0.9,
		isTopContainer : true,
		modal : true,
		autoScroll : true,
		html : "<iframe  id='关联光口标准' name = '关联光口标准'  src = " + url
				+ " height='100%' width='100%' frameBorder=0 border=0/>"
	});
	addUnitInterfaceWindow.show();
}
/**
 * 添加一条记录
 * @param gridID 存储到的store所在Grid的ID
 * @param rec 数据
 */
function addRecord(gridID, rec){
//	console.log(String.format("addRecord({0}, rec)", gridID));
	var store = Ext.getCmp(gridID).getStore();
	var ptpId = rec.get("BASE_PTP_ID");
	var name = rec.get("DISPLAY_NAME"); 
	var flag = gridID.substr(0,4);
	if(checkRedundent(ptpId)){
		if(flag=="comb"){  
			var nbi = new CombineRec({
				BASE_PTP_ID : ptpId, 
				DISPLAY_NAME : name,
				POWER_BUDGET:rec.get("POWER_BUDGET") || "-8.00",
				BUSSINESS_NAME:rec.get("BUSSINESS_NAME") || "",
				SWITCH_THRESHOLD:rec.get("SWITCH_THRESHOLD") || "-14.00"
			});
		}else if (flag=="part"){
			var nbi = new PartRec({
				BASE_PTP_ID : ptpId, 
				DISPLAY_NAME : name,
				POWER_BUDGET:rec.get("POWER_BUDGET") || "-8.00"
			});
		}else if (flag=="unit"){
			var nbi = new UnitRec({
				BASE_PTP_ID : ptpId,
				DISPLAY_NAME : name,
				BUSSINESS_NAME:rec.get("BUSSINESS_NAME") || "",
				SLOT:rec.get("SLOT_NAME"),
				MIN_IN:rec.get("MIN_IN"),
				WAVE_LENGTH:rec.get("WAVE_LENGTH")
			});
		} 
		store.add(nbi);
	}
}
var redundantPtps = [];
/**
 * 检查冗余，看ptp是否重复添加
 * @param ptpID
 * @returns {Boolean}
 */
function checkRedundent(ptpID){
	var panelNames = ["combinePanel", "partPanel", "unitPanel"];
	var protCnt = preProCnt;
	for(var i=0; i < protCnt; i++){
		for(var j=0;j<3;j++){
			var gridID = panelNames[j] + i;
			var store = Ext.getCmp(gridID).getStore();
			if(store.find("BASE_PTP_ID", ptpID) >= 0){
//				console.log("\tHoly shit!");
//				redundantPtps.push(args) 
				return false;
			}
		}
	}
	return true;
}
/**
 * 获取添加的光开关信息
 * @returns {Array}
 * 单条数据格式：组号_类型号_PTPID
 */
function getSwitchData(){
	var panelNames = ["combinePanel", "partPanel", "unitPanel"];
	var protCnt = preProCnt;
	var rv = [];
	for(var i=0; i < protCnt; i++){
		for(var j=0;j<3;j++){
			var gridID = panelNames[j] + i;
			//console.log("group - " + gridID);
			var store = Ext.getCmp(gridID).getStore();
			store.each(function(rec){
				//var info = String.format("{0}_{1}_{2}",i,j,rec.get("BASE_PTP_ID"));
				//console.log("\t" + info + " - " + rec.get("DISPLAY_NAME"));
				rv.push(Ext.encode({
					GROUP_NUM:i,
					PTP_TYPE:3+j,
					BASE_PTP_ID:rec.get("BASE_PTP_ID"),
					POWER_BUDGET:rec.get("POWER_BUDGET"),
					BUSSINESS_NAME:rec.get("BUSSINESS_NAME"),
					SWITCH_THRESHOLD:rec.get("SWITCH_THRESHOLD"),
					DIRECTION:rec.get("DIRECTION"),
					WAVE_LENGTH:rec.get("WAVE_LENGTH")
				}));
			});
		}
	}
	return rv;
}
var mainPanel = new Ext.Panel({
	id : 'mainPanel',
	title : "端口设置",
	layout: "fit",
	flex:1,
	items : [{
		xtype:"panel",
		border:false,
		layout : 'hbox',
        layoutConfig:{
        	align:"stretch",
        	pack:"start"
        },
		items:[{
	        xtype:'fieldset', 
			title: '可选接口',  
		    width:220,
	        collapsed: false, 
	        layout:'fit',
	        items :[unitListPanel]
	    },{
			autoScroll:true,  
			border:false, 
			flex:1,
		    id:'proField',
		    name:'proField',  
		    items:[]  
		}]
	}]
});

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
	Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 900000;
	var win = new Ext.Viewport({
		id : 'win',
		layout : 'fit',
		items : [{
			id : 'winMain',
			layout : 'vbox',
	        layoutConfig:{
	        	align:"stretch",
	        	pack:"start"
	        },
			items : [northPanel, mainPanel],
			buttons:[{
				text:"确定",
				handler:function(){ 
                    if (!validate()) {
                        return;
                    }
					saveOrUpdate(true);
				}
			},{
				text:"取消",
				handler:function(){
					parent.Ext.getCmp('addSwitchInterfaceWindow').close();
				}
			},{
				text:"应用",
				hidden:!isMod,
				handler:function(){
                    if (!validate()) {
                        return;
                    }
					saveOrUpdate(false);
				}
			},{
				text:"重置",
				hidden:!isMod,
				handler:function(){
					reset();
				}
			}],
			buttonAlign:"right"
		}]
	}); 
	win.show();
	if(isMod){
		reset();
	}else{
		onProtectionGroupCountChange();
	}
}); 
function saveOrUpdate(prompt){
    params["paramMap.DISPLAY_NAME"] = tree.getValue();
    params["paramMap.UNIT_TYPE"] = "2";
    params["paramMap.PROTECT_GROUP_COUNT"] = preProCnt + "";
    if(unitInterfaceId!=null){
        params["paramMap.unitInterfaceId"] = unitInterfaceId;

    }
    params["modifyList"] = getSwitchData();
    //填充ID
    Ext.getBody().mask("保存中...");
    Ext.Ajax.request({
        url : 'nx-report!saveOptSwitch.action',
        params : params,
        method : 'POST',
        success : function (response) {
            Ext.getBody().unmask();
            var result = Ext.util.JSON.decode(response.responseText);
            if (result.returnResult == 0) {
                Ext.Msg.alert("提示", result.returnMessage);
            }
            if (result.returnResult > 0) {
                params["paramMap.RESOURCE_UNIT_INTERFACE_ID"] = result.returnResult;
                parent.pageTool.doLoad(parent.pageTool.cursor);
                if (!!prompt) {
                    Ext.Msg.confirm("提示", "数据保存成功，是否继续？", function (btn) {
                        if (btn != 'yes')
                            parent.Ext.getCmp('addSwitchInterfaceWindow').close();
                    });
                } else {
                    Ext.Msg.alert("提示", "数据保存成功!");
                }
            }
        },
        failure : function (response) {
            top.Ext.getBody().unmask();
            var result = Ext.util.JSON.decode(response.responseText);
            Ext.Msg.alert("提示", result.returnMessage);
        },
        error : function (response) {
            top.Ext.getBody().unmask();
            var result = Ext.util.JSON.decode(response.responseText);
            Ext.Msg.alert("提示", result.returnMessage);
        }
    });
}
function clearData(){
	unitInterfaceStore.removeAll();
	var protCnt = Ext.getCmp("proGroupCnt").getValue();
	if(!protCnt){
		protCnt = protCnt>>0;
	}else{
		protCnt = 0;
	}
	for(var i=0; i<protCnt; i++){
		Ext.getCmp("combinePanel" + i).getStore().removeAll();
		Ext.getCmp("partPanel" + i).getStore().removeAll();
		Ext.getCmp("unitPanel" + i).getStore().removeAll();
	}
}
function reset(){ 
	clearData();
	var protCount = 0;
    //开始加载初始数据
    Ext.Ajax.request({
        url : 'nx-report!getUnitInterface.action',
        params : {
            "paramMap.unitInterfaceId" : unitInterfaceId
        },
        method : 'POST',
        success : function (response) {
            var result = Ext.util.JSON.decode(response.responseText);
            tree.setValue(result.DISPLAY_NAME);
            protCount = result.PROTECT_GROUP_COUNT
            Ext.getCmp("proGroupCnt").setValue(protCount);
			onProtectionGroupCountChange();
//            console.log("reset cnt");
        },
        failure : function (response) {
            var result = Ext.util.JSON.decode(response.responseText);
            Ext.Msg.alert("提示", result.returnMessage);
        },
        error : function (response) {
            var result = Ext.util.JSON.decode(response.responseText);
            Ext.Msg.alert("提示", result.returnMessage);
        }
    });
    //开始加载可选板卡数据
    unitInterfaceStore.baseParams.jsonString = unitId;
    unitInterfaceStore.load();
}
function validate(){
	//检测板卡是否选择
	if(!tree.getValue()){
        Ext.Msg.alert("提示", "请先选择板卡！");
        return false;
	}
	//检测保护组数量是否OK
    if (!Ext.getCmp("proGroupCnt").isValid()) {
        Ext.Msg.alert("提示", "保护组数量没有正确填写！");
        return false;
    }
    return true;
	
}