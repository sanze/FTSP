/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
var store = new Ext.data.Store(
{
	url: 'getSectionWaveList.action',
	baseParams: {pageSize: 200},
	reader: new Ext.data.JsonReader({
        totalProperty: 'total',
		root : "rows"
    },[
	    "sectionId","sectionGroupName","sectionName","ASendPortId","ASendPortDisplayname","ASendPortValue","BSendPortId","BSendPortDisplayname","BSendPortValue","BReceivePortId","BReceivePortDisplayname","BReceivePortValue","waveNumber","standardWaveNumber","ASendStandardPower","AMaxPower","BSendStandardPower","BReceiveStandardPower","BMaxPower","totalAttenuationValue","comExpanCap","expanCapAlarm","AComExpanCap","BComExpanCap","ASendStandardTotalPower","BSendStandardTotalPower","BReceiveStandardTotalPower","createTime","updateTime","attenuationStandardValue","attenuationAlarm"
    ]),
    sortInfo: {field: 'sectionGroupName', direction: 'DESC'}
});

var checkboxSelectionModel = new Ext.ux.grid.LockingCheckboxSelectionModel();
checkboxSelectionModel.sortLock();
var cm = new Ext.ux.grid.LockingColumnModel({
    // specify any defaults for each column
    defaults: {
        sortable: true // columns are not sortable by default           
    },
    columns: [new Ext.grid.RowNumberer({
    	width:26,
    	locked:true
    	}),checkboxSelectionModel,{
        id: 'sectionId',
        header: 'sectionId',
        dataIndex: 'sectionId',
        hidden:true
    }, {
    	id : 'sectionGroupName',
        header: '光放段组',
        dataIndex: 'sectionGroupName',
        width: 120,
        editor: new Ext.form.TextField({
            allowBlank: false
        })
    },{
        id: 'sectionName',
        header: '光放段名称',
        dataIndex: 'sectionName',
        width: 100,
        editor: new Ext.form.TextField({
            allowBlank: false
        })
    }, {
        id:'waveNumber',
        header: '波道数',
        dataIndex: 'waveNumber',
        width: 60,
        editor: new Ext.form.NumberField({
	        allowNegative:false,
	        allowDecimals:false,
	        allowBlank:false,
	        maxValue:100,
	        minValue:0
	    })
    },{
     	id:'standardWaveNumber',
        header: '标称波道数',
        dataIndex: 'standardWaveNumber',
        width: 85,
        editor: new Ext.form.NumberField({
	        allowNegative:false,
	        allowDecimals:false,
	        allowBlank:false,
	        maxValue:100,
	        minValue:0
        })
     }, {
        id:'totalAttenuationValue',
        header: '总衰耗值',
        dataIndex: 'totalAttenuationValue',
        width: 70,
        renderer :colorGrid
    },{
        id:'attenuationStandardValue',
        header: '衰耗标准值',
        dataIndex: 'attenuationStandardValue',
        width: 80,
        editor: new Ext.form.NumberField({
	        allowNegative:false,
	        allowDecimals:true,
	        decimalPrecision:2,
	        allowBlank:false,
	        maxValue:100,
	        minValue:0
        }),
        renderer :colorGrid
    },{
        id:'attenuationAlarm',
        header: '衰耗预警',
        dataIndex: 'attenuationAlarm',
        width: 70,
        renderer :colorGrid
    },{
        id:'comExpanCap',
        header: '综合扩容能力',
        dataIndex: 'comExpanCap',
        width: 100,
        renderer :colorGrid
    }, {
        id:'expanCapAlarm',
        header: '扩容能力预警',
        dataIndex: 'expanCapAlarm',
        width: 100,
        renderer :colorGrid
    }, {
        id:'AComExpanCap',
        header: 'A站OA扩容能力',
        dataIndex: 'AComExpanCap',
        width: 110,
        renderer :colorGrid
    }, {
        id:'BComExpanCap',
        header: 'B站OA扩容能力',
        dataIndex: 'BComExpanCap',
        width: 110,
        renderer :colorGrid
    }, {
        id:'ASendStandardTotalPower',
        header: 'A站OA（发）标称总功率',
        dataIndex: 'ASendStandardTotalPower',
        width: 160,
        renderer :trans
    }, {
        id:'ASendPortValue',
        header: 'A站OA发总功率',
        dataIndex: 'ASendPortValue',
        width: 110,
        renderer :colorGrid
     },{
     	id:'BReceiveStandardTotalPower',
        header: 'B站OA（收）标称总功率',
        dataIndex: 'BReceiveStandardTotalPower',
        width: 160,
        renderer :trans
     },{
     	id:'BReceivePortValue',
        header: 'B站OA收总功率',
        dataIndex: 'BReceivePortValue',
        width: 110,
        renderer :colorGrid
     },{
     	id:'BSendStandardTotalPower',
        header: 'B站OA（发）标称总功率',
        dataIndex: 'BSendStandardTotalPower',
        width: 160,
        renderer :trans
     },{
     	id:'BSendPortValue',
        header: 'B站OA发总功率',
        dataIndex: 'BSendPortValue',
        width: 110,
        renderer :colorGrid
     },{
     	id:'ASendStandardPower',
        header: 'A站OA（发）单波标称功率',
        dataIndex: 'ASendStandardPower',
        width: 170,
        editor: new Ext.form.NumberField({
        	allowNegative:true,
	        allowDecimals:true,
	        decimalPrecision:2,
	        maxValue:100,
	        minValue:-30,
	        allowBlank:false
		}),
        renderer :trans
     },{
     	id:'AMaxPower',
        header: 'A站OA最大发光功率',
        dataIndex: 'AMaxPower',
        width: 135,
        editor: new Ext.form.NumberField({
        	allowNegative:true,
	        allowDecimals:true,
	        decimalPrecision:2,
	        maxValue:100,
	        minValue:-30,
	        allowBlank:false
		}),
        renderer :trans
     },{
     	id:'BReceiveStandardPower',
        header: 'B站OA（收）单波标称功率',
        dataIndex: 'BReceiveStandardPower',
        width: 170,
        editor: new Ext.form.NumberField({
        	allowNegative:true,
	        allowDecimals:true,
	        decimalPrecision:2,
	        maxValue:100,
	        minValue:-30,
	        allowBlank:false
	 	}),
        renderer :trans
     },{
     	id:'BSendStandardPower',
        header: 'B站OA（发）单波标称功率',
        dataIndex: 'BSendStandardPower',
        width: 170,
        editor: new Ext.form.NumberField({
        	allowNegative:true,
	        allowDecimals:true,
	        decimalPrecision:2,
	        maxValue:100,
	        minValue:-30,
	        allowBlank:false
		}),
        renderer :trans
     },{
     	id:'BMaxPower',
        header: 'B站OA最大发光功率',
        dataIndex: 'BMaxPower',
        width: 135,
        editor: new Ext.form.NumberField({
        	allowNegative:true,
	        allowDecimals:true,
	        decimalPrecision:2,
	        maxValue:100,
	        minValue:-30,
	        allowBlank:false
        }),
        renderer :trans
     },{
     	id:'updateTime',
        header: '最近更新时间',
        dataIndex: 'updateTime',
        width: 135
     },{
     	id:'createTime',
        header: '创建时间',
        dataIndex: 'createTime',
        width: 135
     }]
});
    
var pageTool = new Ext.PagingToolbar({
	id:'pageTool',
    pageSize: 200,//每页显示的记录值
    store: store,
    displayInfo: true,
    displayMsg : '当前 {0} - {1} ，总数 {2}',
    emptyMsg: "没有记录"
 });

var gridPanel = new Ext.grid.EditorGridPanel({
	id:"gridPanel",
	region:"center",
//	title:'任务信息列表',
	cm:cm,
	view: new Ext.ux.grid.LockingGridView(),
    store:store,
//    autoHeight:true,
//    autoExpandColumn: 'taskDescription', // column with this id will be expanded
//    collapsed: false,   // initially collapse the group
//    collapsible: true,
    stripeRows : true, // 交替行效果
    loadMask: true,
    selModel:checkboxSelectionModel ,  //必须加不然不能选checkbox  
    bbar: pageTool,
//        tbar: pageTool,
    forceFit:true,
    listeners : {
    	'rowdblclick' : function(gridPanel, rowIndex, e){

    	}
    },
    tbar: [{
        text: '录入光放段',
        icon:'../../../resource/images/btnImages/add.png',
        handler: function(){
        	parent.addTabPage("../../jsp/performanceManager/opticalPathMonitorManager/addPmp.jsp","录入光放段");
        }
    },{
        text: '应用', 
        handler: function(){
			modifySectionWave();
        }
    },{
        text: '删除',
        icon:'../../../resource/images/btnImages/delete.png',
        handler: function(){
			deleteSectionWave();
        }
    },{
        text: '导出数据',
        icon:'../../../resource/images/btnImages/export.png',
        handler: function(){
			modifyTask();
        }
    }]
}); 
function modifyTask(){}

function deleteSectionWave(){/*
	var jsonString=new Array();
	var cell = checkboxSelectionModel.getSelections();
	if(cell.length>0){
		for(var i=0;i<cell.length;i++){
	    		var sectionModel = {
		    		"sectionId":cell[i].get("sectionId")
		    	};
		    	jsonString.push(sectionModel);
	    }
	    var jsonData = {
        	"jsonString":Ext.encode(jsonString)
    	};
    	Ext.Msg.confirm('提示','确认删除？',
	      function(btn){
	        if(btn=='yes'){
    			Ext.getBody().mask('正在执行，请稍候...');
					Ext.Ajax.request({
			            url: 'deleteSectionWave.action',
			            method: 'POST',
			            params: jsonData,
			           	success : function(response){//回调函数
							Ext.getBody().unmask();
			            	var obj = Ext.decode(response.responseText);
							if(obj.returnResult == 0){
					   		    //刷新列表
				        		var pageTool = Ext.getCmp('pageTool');
				        		if(pageTool){
									pageTool.doLoad(pageTool.cursor);
								}
							}
							if(obj.returnResult == 1){
				        		Ext.Msg.alert("信息",obj.returnMessage);
				        	}
						},
						error:function(response) {
							Ext.getBody().unmask();
			            	Ext.Msg.alert('错误','数据加载失败，请检查连接！');
						},
						failure:function(response) {
							Ext.getBody().unmask();
			            	Ext.Msg.alert('错误','数据加载失败，请检查连接！');
						}
					});
	        }else{
		       
	        }
	     });
	}else{
		Ext.Msg.alert("信息","请选择需要删除的数据！");
	}*/
}

function modifySectionWave(){/*
//	var cell = checkboxSelectionModel.getSelections();
	var jsonString=new Array();
	var cell = store.getModifiedRecords();
	if(cell.length>0){
		for(var i=0;i<cell.length;i++){
	    		var sectionModel = {
		    		"sectionId":cell[i].get("sectionId"),
		    		
		    		"sectionGroupName":cell[i].get("sectionGroupName"),
		    		"sectionName":cell[i].get("sectionName"),
		    		"waveNumber":cell[i].get("waveNumber"),
		    		"standardWaveNumber":cell[i].get("standardWaveNumber"),
		    		"attenuationStandardValue":cell[i].get("attenuationStandardValue"),
		    		"ASendStandardPower":cell[i].get("ASendStandardPower"),
		    		"AMaxPower":cell[i].get("AMaxPower"),
		    		"BSendStandardPower":cell[i].get("BSendStandardPower"),
		    		"BReceiveStandardPower":cell[i].get("BReceiveStandardPower"),
		    		"BMaxPower":cell[i].get("BMaxPower"),
		    		"ASendPortValue":cell[i].get("ASendPortValue"),
		    		"BReceivePortValue":cell[i].get("BReceivePortValue"),
		    		"BSendPortValue":cell[i].get("BSendPortValue")
		    	};
		    	jsonString.push(sectionModel);
	    }
	    var jsonData = {
        	"jsonString":Ext.encode(jsonString)
    	};
    	//提交修改，不然store.getModifiedRecords();数据会累加
    	store.commitChanges();
    	Ext.getBody().mask('正在执行，请稍候...');
		Ext.Ajax.request({
            url: 'modifySectionWave.action',
            method: 'POST',
            params: jsonData,
           	success : function(response){//回调函数
				Ext.getBody().unmask();
            	var obj = Ext.decode(response.responseText);
				if(obj.returnResult == 0){
		   		    //刷新列表
	        		var pageTool = Ext.getCmp('pageTool');
	        		if(pageTool){
						pageTool.doLoad(pageTool.cursor);
					}
				}
				if(obj.returnResult == 1){
	        		Ext.Msg.alert("信息",obj.returnMessage);
	        	}
			},
			error:function(response) {
				Ext.getBody().unmask();
            	Ext.Msg.alert('错误','数据加载失败，请检查连接！');
			},
			failure:function(response) {
				Ext.getBody().unmask();
            	Ext.Msg.alert('错误','数据加载失败，请检查连接！');
			}
		 });
	}else{
		Ext.Msg.alert("信息","没有修改的数据！");
	}*/
}

function trans(v) {
	if (v == 'null' || v.length == 0){
		return '-';
	}else {
		return v;
	}
}

function colorGrid(v,m) {
	if(v=='一般告警'){
		m.css='x-grid-font-pink';
	}else if(v=='严重告警'){
		m.css='x-grid-font-red';
	}else{
		m.css='x-grid-font-blue';
	}
	return v;
}

Ext.onReady(function(){
	
 	Ext.BLANK_IMAGE_URL="../../../../resource/ext/resources/images/default/s.gif";
 	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();}
// 	Ext.Msg = top.Ext.Msg; 
 	Ext.Ajax.timeout=900000; 

    var win = new Ext.Viewport({
        id:'win',
		layout : 'border',
		items : gridPanel
	});
	win.show();	
	store.load();
  });
  
  