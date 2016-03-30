var storeUnsel= new Ext.data.Store({
	url : 'alarm-converge!getApplyEquips.action', 
	baseParams:{"ids":parent.applyEmsIds},
	reader :  new Ext.data.JsonReader({
		root : 'rows',
		fields :['PRODUCT_NAME',"FACTORY"]
	})
});

storeUnsel.load({
	callback : function(records,options,success){
		if (!success) {
			Ext.Msg.alert('错误', '查询失败！请重新查询');
		}
	}
});

var storeSel= new Ext.data.Store({ 
	reader :  new Ext.data.JsonReader({ 
		root : "rows",
		fields :['PRODUCT_NAME',"FACTORY"]
	})
});

var uslSelectionModel = new Ext.grid.CheckboxSelectionModel({
	singleSelect : false
});
var uslEquip = new Ext.grid.ColumnModel({
	defaults : {
		sortable : true
	},
	columns : [ uslSelectionModel, {
		id : 'FACTORY', 
		dataIndex : 'FACTORY',
		width : 80,
		renderer :factorRenderer
	}, {
		id : 'PRODUCT_NAME', 
		dataIndex : 'PRODUCT_NAME',
		width : 150 
	}]
});

var slSelectionModel = new Ext.grid.CheckboxSelectionModel({
	singleSelect : false
});
var slEquip = new Ext.grid.ColumnModel({
	defaults : {
		sortable : true
	},
	columns : [ slSelectionModel, {
		id : 'FACTORY', 
		dataIndex : 'FACTORY',
		width : 80,
		renderer : factorRenderer
	}, {
		id : 'PRODUCT_NAME', 
		dataIndex : 'PRODUCT_NAME',
		width : 150,
		renderer:function(v,m,r){
			var isExist=false;
			storeUnsel.each(function(rec){
				if(r.get("PRODUCT_NAME")==rec.get("PRODUCT_NAME") &&
						r.get("FACTORY")==rec.get("FACTORY")){
					isExist=true;
					return false;
				}
			});
			if(!isExist){
				return '<font color="#FF0000">' + v +'</font>';
			} else{
				return v;
			}
		
		}
	}]
});
 
var uslGrid = new Ext.grid.GridPanel({
	id : 'uslGrid',
	title:'可选设备',
	height:300,
	flex:10,
	store : storeUnsel,
	autoScroll:true, 
	cm : uslEquip,
	hideHeaders:true,
	selModel : uslSelectionModel,
	frame : false, 
	stripeRows : true 
}); 


var slGrid = new Ext.grid.GridPanel({
	id : 'slGrid',
	title:'已选设备',
	height:300,
	flex:10,
	store : storeSel,
	autoScroll:true, 
	cm : slEquip,
	hideHeaders:true,
	selModel : slSelectionModel,
	frame : false, 
	stripeRows : true 
}); 

var equipPanel = new Ext.Panel({
    id:'equipPanel', 
    border:true,
    region:'center',
    layout: {
        type:'hbox',
        padding:'20',
        align:'stretch'
    }, 
    autoScroll:true,
    items:[uslGrid,new Ext.Panel({
		    id:'selBtnPanel',
		    autoScroll:false,
		    border:false, 
            flex:2,
		    layout: {
                type:'vbox', 
                pack:'center',
                align:'center'
            },
            defaults:{margins:'0 0 30 0'},
            items:[{ 
                xtype:'button',
                text: '＞', 
                handler:function(){
                	leftToRight();
                }
            },{
                xtype:'button',
                text: '＜',
                handler:function(){
                	RightToleft();
                }
            }]
	    }),slGrid],
		buttons : [{
		text : '确定',
		handler : function (){  
			var equipStr=""; 
			parent.eqStr="";
			storeSel.each(function(record){  
				equipStr+=factorRenderer(record.get("FACTORY"))+record.get("PRODUCT_NAME")+",";  
				parent.eqStr+=record.get("FACTORY")+"."+record.get("PRODUCT_NAME")+";";
			});
			equipStr = equipStr.substring(0,equipStr.lastIndexOf(","));  
			parent.eqStr = parent.eqStr.substring(0,parent.eqStr.lastIndexOf(";"));  
			parent.frames["editConvergeRule_panel"].Ext.getCmp('applyEquip').setValue(equipStr); 	
			var win = parent.Ext.getCmp('applyEquipWin');
			if (win) {
				win.close();
			} 
	  }
	}, {
		text : '取消',
		handler : function () { 
			var win = parent.Ext.getCmp('applyEquipWin');
			if (win) {
				win.close();
			}
        }
	}]
}); 
 
//左移
function leftToRight(){  
	var left = uslGrid.getSelectionModel().getSelections(); 
	for ( var i = 0; i < left.length; i++) {
		var recordIndex = storeSel.findBy(function(record, id) {
			if (record == left[i]) {
				return true;
			}
		});
		if (recordIndex == -1) {
			storeSel.add(left[i]);
		}
	}   
}

//右移
function RightToleft(){
	var right = slGrid.getSelectionModel().getSelections(); 
	if(right==undefined || right==null){
		return;
	}
	storeSel.remove(right);
}

function factorRenderer(v) { 
	switch (v) {
		case 1:
			return "华为";
		case 2:
			return "中兴";
		case 3:
			return "朗讯";
		case 4:
			return "烽火";
		case 9:
			return "富士通";
		default:
			return v;
	}
}

function initData(){  
	if(parent.eqStr==""||parent.eqStr==null){
		return;
	} 
	var arr = parent.eqStr.split(";");
	for(var i=0;i<arr.length;i++){ 
		var ar=arr[i].split(".");
		var record = new Ext.data.Record(['PRODUCT_NAME','FACTORY']);  
		record.set('PRODUCT_NAME',ar[1]);
		record.set('FACTORY',parseInt(ar[0]));
		storeSel.add(record);
	}
}

Ext.onReady(function(){
	Ext.QuickTips.init(); 
 	Ext.BLANK_IMAGE_URL="../../resource/ext/resources/images/default/s.gif";
 	document.onmousedown=function(){
 		top.Ext.menu.MenuMgr.hideAll();
	}; 
	initData();
  	var win =new Ext.Viewport({
  		id:'win',
        layout : 'border', 
        items : equipPanel
	});
 });
