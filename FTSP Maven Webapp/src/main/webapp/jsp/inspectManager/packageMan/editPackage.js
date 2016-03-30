/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */

/*-----------------------------------巡 检 设 备---------------------------------------------*/
//==================For the Tree====================
var treeParams={
  leafType:leafType//,
    //checkModel:"single",
    //onlyLeafCheckable:false
};
var treeurl="../../commonManager/tree.jsp?"+Ext.urlEncode(treeParams);
var westPanel = new Ext.Panel({
	title:"",
	id:"westPanel",
	region:"west",
	width: '25%',
	//autoScroll:true,
    boxMinWidth: 230,
    boxMinHeight: 260,
	forceFit:true,
	collapsed: false,   // initially collapse the group
    collapsible: false,
    collapseMode: 'mini',
    split:true,
    html:'<iframe id="tree_panel" name="tree_panel" src ="'+treeurl+'" height="100%" width="100%" frameBorder=0 border=0/>'
});

//巡检设备初始化加载
var store = new Ext.data.Store({
  url: 'inspect-engineer!initInspectEquip.action',
  reader: new Ext.data.JsonReader({
        totalProperty: 'total',
		root : "rows",
        fields : [
          {name:'equipType',mapping:"TARGET_TYPE"},
          {name:'equipId',mapping:"TARGET_ID"},
          {name: 'equipFullName',mapping:"DISPLAY_NAME"}]})
    
});


//添加包机人的巡检设备
function onGetChecked(getFunc){
	var pathParam="path"+CommonDefine.NameSeparator+"text";
	var result=getFunc(["nodeLevel","nodeId",pathParam],"top");
    var reader = new Ext.data.ArrayReader({
      fields : [
        {name:'equipType',mapping:"nodeLevel"},
        {name:'equipId',mapping:"nodeId"},
        {name: 'equipFullName',mapping:pathParam}]
    })
    obj=reader.readRecords(result);
    var Records=[];
    for(i=0;i<obj.records.length;i++){
      var recordIndex=store.findBy(function(record,id){
        if( record.get('equipType')==obj.records[i].get('equipType')&&
            record.get('equipId')==obj.records[i].get('equipId')){
            return true;
        }
      });
      if(recordIndex==-1){
        Records.push(obj.records[i])
      }
    }
    
    store.add(Records);
}


var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel();
var cm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	},
	columns : [new Ext.grid.RowNumberer({
		width : 26
	}), checkboxSelectionModel, {
            id: 'equipType',
            header: 'type',
            dataIndex: 'equipType',
            hidden:true
        },{
            id: 'equipId',
            header: 'id',
            dataIndex: 'equipId',
            hidden:true
        },{
			id : 'equipFullName',
			header : '设备',
			dataIndex : 'equipFullName'
	}]
});

var gridPanel = new Ext.grid.GridPanel({
	id : "gridPanel",
	region : "center",
	title:'',
	cm : cm,
	store : store,
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	viewConfig : {
		forceFit : true
	},
	tbar : 	["包机设备","->",{
		text : '删除',
		id:'deleteEquip',
		name:'deleteEquip',
		icon : '../../../resource/images/btnImages/delete.png',
		handler : function() {
		    var records = gridPanel.getSelectionModel().getSelections();
		    var len = records.length;
		    if(len <= 0){
		       parent.Ext.Msg.alert("提示","请选择需要删除的设备！");
		    }else{
		       for(var i = 0;i<len;i++ ){
			        gridPanel.store.remove(records[i]);
			   }
		    }			
		}
	}]
});
/*-----------------------------------包 机 人 基 本 信 息---------------------------------------------*/
var areaId;
var firstLevelAreaStore = new Ext.data.Store({
	    url: 'inspect-engineer!getAreaNameList.action',
	    reader: new Ext.data.JsonReader({
		   },[
	          {name:"areaName",mapping:"AREA_NAME"},
	          {name:"areaId",mapping:"RESOURCE_AREA_ID"}
	       ])
    }); 

firstLevelAreaStore.load({
	callback: function(r, options, success){
		if(success){

		}else{
			var obj = Ext.decode(response.responseText);
    		Ext.Msg.alert("提示",obj.returnMessage);
		}
	}
});

var secondLevelAreaStore = new Ext.data.Store({
	    reader: new Ext.data.JsonReader({
		   },[
		      {name:"areaId",mapping:"RESOURCE_AREA_ID"},
	          {name:"areaName",mapping:"AREA_NAME"}
	       ])
    }); 
    
var thirdLevelAreaStore = new Ext.data.Store({
	    reader: new Ext.data.JsonReader({
		   },[
		      {name:"areaId",mapping:"RESOURCE_AREA_ID"},
	          {name:"areaName",mapping:"AREA_NAME"}
	       ])
    }); 

var button = {
   items:[
    {
        text: '确定',
        id:'ok',
        name:'ok',
        disabled : false,
        handler: function(){
          var engineerName = Ext.getCmp('engineerName').getValue();
          if(engineerName == ""){
              Ext.Msg.alert("提示","包机人不能为空。");
          }else{
            //新增修改包机人时，判断包机人工号是否重复。
		   var JobNo = Ext.getCmp('JobNo').getValue();
		   Ext.Ajax.request({
			 url:'inspect-engineer!checkJobNoExist.action',
			 method:'post',
			 params:{'JobNo':JobNo,
			         "engineerId":engineerId},
			 success:function(response,opts){
				 var obj=Ext.decode(response.responseText);
				 if(obj.exit){
				   Ext.Msg.alert("提示","有相同的工号。工号不可重复。");
				 }else{
				   addInspectEngineer(1);
				 }
				 },
			 failure: function(response,opts){
			     var obj=Ext.decode(response.responseText);
			     Ext.Msg.alert("提示",obj.result.returnMessage);   
			 }
		   });
           } 
		}
     },{
        text: '取消',
        id:'cancel',
        name:'cancel',
        disabled : false,
        handler: function(){
          // 关闭修改包机人窗口
			var window = parent.Ext.getCmp('editPackageWindow');
			if (window) {
				window.close();
			}
        }
     },{
        text: '应用',
        id:'apply',
        name:'apply',
        disabled : false,
        handler: function(){
          var engineerName = Ext.getCmp('engineerName').getValue();
          var JobNo = Ext.getCmp('JobNo').getValue();
          if(engineerName == ""){
              Ext.Msg.alert("提示","包机人不能为空。");
          }else if(JobNo == "")
          {
              Ext.Msg.alert("提示","工号不能为空。");
          }else{
            //新增修改包机人时，判断包机人工号是否重复。
		   
		   Ext.Ajax.request({
			 url:'inspect-engineer!checkJobNoExist.action',
			 method:'post',
			 params:{'JobNo':JobNo,
			         "engineerId":engineerId},
			 success:function(response,opts){
				 var obj=Ext.decode(response.responseText);
				 if(obj.exit){
				   Ext.Msg.alert("提示","有相同的工号。工号不可重复。");
				 }else{
				   addInspectEngineer(0);
				 }
				 },
			 failure: function(response,opts){
			     var obj=Ext.decode(response.responseText);
			     Ext.Msg.alert("提示",obj.result.returnMessage);   
			 }
		   });
		   }
        }
     },{xtype:'label',width:'15px'}]
   }

var eastPanel = new Ext.form.FormPanel({
	title:"",
	id:"eastPanel",
	region:"east",
	bodyStyle:'padding:10px 15px 10px 15px;',
	width: '40%',
	autoScroll:true,
    collapsible: false,
	labelWidth: 60,
//	labelAlign: 'right',
	fbar: button,
	defaults: {
		anchor: '95%',
		labelStyle:"margin-bottom:10px;",
		style:"margin-bottom:10px;"
	},
	items:[{
		xtype: 'hidden',
		id:'engineerID',
		name:'engineerID',
		fieldLabel: '包机人ID',
		disabled: true
	},{
		xtype: 'textfield',
		id:'engineerName',
		name:'engineerName',
		fieldLabel: '包机人',
		sideText: '<span style="color:red">*</span>'
	},{
		xtype: 'textfield',
		id:'JobNo',
		name:'JobNo',
		fieldLabel: '工号',
		sideText: '<span style="color:red">*</span>',
		validator:function(val){
			var vastatic = false;
			var validator = this;
            var error = true;
			Ext.Ajax.request({
			 url:'inspect-engineer!checkJobNoExist.action',
			 method:'post',
			 scope: validator,
			 params:{'JobNo':val,
			         "engineerId":engineerId},
			 success:function(response,opts){
				 var obj=Ext.decode(response.responseText);
				 if(obj.exit){
				  vastatic = true;
				  Ext.Msg.alert("提示","有相同的工号。工号不可重复。");
				 }else{
				  vastatic = false;
				 }
				 },
			 failure: function(response,opts){
			     var obj=Ext.decode(response.responseText);
			     Ext.Msg.alert("提示",obj.result.returnMessage);   
			 }
			 });
			//  return vastatic;
			},
		listeners: {
             focus: {
                 fn: function () { this.clearInvalid(); }
             }
         }
	},{
		xtype: 'textfield',
		id:'telephone',
		name:'telephone',
		fieldLabel: '电话'
	},{
        layout:'column',
        id:'area',
        name:'area',
		fieldLabel: top.FieldNameDefine.AREA_NAME,
        border:false,
		defaults:{
			layout : "form",
			border:false,
			columnWidth: .30
		},
        items:[{
            items: [{
            	xtype:'combo',
            	id:'firstLevelCombo',
				name: 'firstLevelCombo',
				hideLabel:true,
			//	fieldLabel: '一级区域',
				emptyText:'省',
				triggerAction: 'all',
				store:firstLevelAreaStore,
				valueField: 'areaId',
				displayField: 'areaName',
				allowBlank:true,
				editable:false,
				anchor:'95%',
				listeners:{
					select:function(combo,record,index){
						areaId = this.getValue();
						Ext.getCmp('secondLevelCombo').reset();
						Ext.getCmp('thirdLevelCombo').reset();
						secondLevelAreaStore.proxy = new Ext.data.HttpProxy({
					          url: 'inspect-engineer!getAreaNameList.action'
					    });
					    var jsonData = {
						   "level" : this.getValue()
						};
	                    secondLevelAreaStore.baseParams = jsonData;
	                    secondLevelAreaStore.load({
						    callback : function(r, options, success){//回调函数
								      if(success){          		    	
								      }else{
						                var obj = Ext.decode(response.responseText);
	    		                        Ext.Msg.alert("提示",obj.returnMessage);
								      }               			
								   }
						});
					}
				}
            }]
        },{
            items: [{
            	xtype:'combo',
            	id:'secondLevelCombo',
				name: 'secondLevelCombo',
				hideLabel:true,
				//fieldLabel: '二级区域',
				emptyText:'市',
				triggerAction: 'all',
				store:secondLevelAreaStore,
				valueField: 'areaId',
				displayField: 'areaName',
				allowBlank:true,
				editable:false,
				anchor:'95%',
				listeners:{
					select:function(combo,record,index){
                        areaId = this.getValue();
                        Ext.getCmp('thirdLevelCombo').reset();
						thirdLevelAreaStore.proxy = new Ext.data.HttpProxy({
					          url: 'inspect-engineer!getAreaNameList.action'
					    });
					    var jsonData = {
						   "level" : this.getValue()
						};
	                    thirdLevelAreaStore.baseParams = jsonData;
	                    thirdLevelAreaStore.load({
						    callback : function(r, options, success){//回调函数
								      if(success){          		    	
								      }else{
						                var obj = Ext.decode(response.responseText);
	    		                        Ext.Msg.alert("提示",obj.returnMessage);
								      }               			
								   }
						});
					}
				}
            }]
        },{
            items: [{
            	xtype:'combo',
            	id:'thirdLevelCombo',
				name: 'thirdLevelCombo',
				hideLabel:true,
				//fieldLabel: '三级区域',
				emptyText:'县',
				triggerAction: 'all',
				store:thirdLevelAreaStore,
				valueField: 'areaId',
				displayField: 'areaName',
				allowBlank:true,
				editable:false,
				anchor:'95%',
				listeners:{
					select:function(combo,record,index){
						areaId = this.getValue();
					}
				}
            }]
        },{
			columnWidth: .10,
			xtype:'button',
			id:'secondLevelResetButton',
			name: 'secondLevelResetButton',
			fieldLabel:' ',
			text: '重置',
			anchor:'95%',
			handler: function (){
				Ext.getCmp('firstLevelCombo').reset();
				Ext.getCmp('secondLevelCombo').reset();
				Ext.getCmp('thirdLevelCombo').reset();
			}
        }]
	},{
		xtype: 'textarea',
		id:'department',
		name:'department',
		fieldLabel: '部门'
	},{
		xtype: 'textfield',
		id:'role',
		name:'role',
		fieldLabel: '职务'
	},{
		xtype: 'textarea',
		id:'note',
		name:'note',
		fieldLabel: '备注'
	}]
});


function addInspectEngineer(closeOrNot){
if(saveType == 2){
   // 关闭修改任务信息窗口
	var window = parent.Ext.getCmp('editPackageWindow');
	if (window) {
		window.close();
	}
}else{
   
   //新增修改包机人，保存包机人信息。
   var inspectEquipList = new Array();
   var inspectEquipNameList = new Array();
   
   for(var i=0; i<store.getCount();i++){
      inspectEquipList.push(store.getAt(i).get("equipType")+"_"+store.getAt(i).get("equipId")); 
      inspectEquipNameList.push(store.getAt(i).get("equipFullName")); 
   }

  // var engineerId = Ext.getCmp('engineerID').getValue();
   var engineerName = Ext.getCmp('engineerName').getValue();
   var JobNo = Ext.getCmp('JobNo').getValue();
   var telephone = Ext.getCmp('telephone').getValue();
   if(telephone.length > 21){
     Ext.Msg.alert("提示","包机人电话号码不能超过21位！");
     return;
   }

   //alert(areaId);
   var thirdLevelCombo = areaId;
   var department = Ext.getCmp('department').getValue();
   var role = Ext.getCmp('role').getValue();
   var note = Ext.getCmp('note').getValue();
   
   var jsonData = {
       "engineerId":engineerId,
       "engineerName":engineerName,
       "JobNo":JobNo,
       "telephone":telephone,
       "thirdLevelCombo":thirdLevelCombo,
       "department":department,
       "role":role,
       "note":note,
       "inspectEquipList":inspectEquipList,
       "inspectEquipNameList":inspectEquipNameList  
   }
   
   var url;
   
   if(saveType == 0){
	   if(engineerId == 0){//第一次打开新增包机人窗口，添加包机人，"应用"按下，此操作向数据库中插入新纪录
		   url = 'inspect-engineer!addInspectEngineer.action';
	   }else{//新增窗口未关闭，继续编辑包机人，"应用"或"确定"按钮按下，此操作为更新包机人
		   url = 'inspect-engineer!updateInspectEngineer.action';
	   }
     
   }else if(saveType == 1){
     url = 'inspect-engineer!updateInspectEngineer.action';
     
   }
   
   Ext.Ajax.request({
      url:url,
      method:'Post',
      params:jsonData,
      success: function(response) {
		    	var obj = Ext.decode(response.responseText);
		    	if(obj.returnResult == 0){
		    		Ext.Msg.alert("信息",obj.returnMessage);
	             }
	         	if(obj.returnResult == 1){
					engineerId = obj.engineerId;
		         	if(closeOrNot == 1){
		         	  Ext.Msg.alert("信息",obj.returnMessage,function(btn){
		         	    // 刷新列表
						var pageTool = parent.Ext.getCmp('pageTool');
						if (pageTool) {
							pageTool.doLoad(pageTool.cursor);
						}
		         	    // 关闭修改任务信息窗口
						var window = parent.Ext.getCmp('editPackageWindow');
						if (window) {
							window.close();
						}
						
						//var engineerStore = parent.Ext.getCmp('engineerPanel').getStore();
						//alert(engineerStore.getCount());
						//engineerStore.reload();
		         	  });
		         	}else if(closeOrNot == 0){
		         	  Ext.Msg.alert("信息",obj.returnMessage);
		         	  // 刷新列表
						var pageTool = parent.Ext.getCmp('pageTool');
						if (pageTool) {
							pageTool.doLoad(pageTool.cursor);
						}
		         	}
	         	}
		    },
    error:function(response) {
    	Ext.Msg.alert("错误",response.responseText);
    },
    failure:function(response) {
    	Ext.Msg.alert("错误",response.responseText);
    }
   
   })
}
}


//==========================center=============================
var centerPanel = new Ext.Panel({
    id:'centerPanel',
    border:false,
    region:'center',
    autoScroll:true,
    layout:'border',
    items:[gridPanel]
});
//==========================center=============================


//-----------------------------initial----------------------------------

function initData(){    
if(saveType != 0){
var jsonData = {
    "engineerId":engineerId,
    "flag":1
}
var inspectEquipList = new Array();
store.baseParams=jsonData;
   store.load({
     callback : function(records,options,success){
       if(!success)
         Ext.Msg.alert("提示","加载包机设备失败");
     }
   });
Ext.Ajax.request({
   url:'inspect-engineer!initInspectEngineer.action',
   method:'Post',
   params:jsonData,
   success: function(response) {
   	var obj = Ext.decode(response.responseText);
   	var engineer = obj.rows;
   	Ext.getCmp('engineerName').setValue(obj.engineerName);
   	Ext.getCmp('JobNo').setValue(obj.JobNo);
   	Ext.getCmp('telephone').setValue(obj.telephone);
   	Ext.getCmp('firstLevelCombo').setValue(obj.firstLevelCombo);
   	Ext.getCmp('secondLevelCombo').setValue(obj.secondLevelCombo);
   	if(obj.firstLevelComboId != ""){
   	//市级区域信息加载
   	secondLevelAreaStore.proxy = new Ext.data.HttpProxy({
          url: 'inspect-engineer!getAreaNameList.action'
    });
    var jsonData = {
	   "level" : obj.firstLevelComboId
	};
    secondLevelAreaStore.baseParams = jsonData;
    secondLevelAreaStore.load({
	    callback : function(r, options, success){//回调函数
			      if(success){          		    	
			      }else{
	                var obj = Ext.decode(response.responseText);
		                        Ext.Msg.alert("提示",obj.returnMessage);
			      }               			
			   }
	});
   	if(obj.secondLevelComboId != ""){
   	//县级区域信息加载
   	thirdLevelAreaStore.proxy = new Ext.data.HttpProxy({
          url: 'inspect-engineer!getAreaNameList.action'
    });
    var jsonData = {
	   "level" : obj.secondLevelComboId
	};
    thirdLevelAreaStore.baseParams = jsonData;
    thirdLevelAreaStore.load({
	    callback : function(r, options, success){//回调函数
			      if(success){          		    	
			      }else{
	                var obj = Ext.decode(response.responseText);
		            Ext.Msg.alert("提示",obj.returnMessage);
			      }               			
			   }
	});
	Ext.getCmp('thirdLevelCombo').setValue(obj.thirdLevelCombo);
   	//Ext.getCmp('thirdLevelCombo').setText(obj.thirdLevelCombo);
   	//alert(Ext.getCmp('thirdLevelCombo').getText());
   	areaId = obj.thirdLevelComboId;
   	}
   	}
   	Ext.getCmp('department').setValue(obj.department);
   	Ext.getCmp('role').setValue(obj.role);
   	Ext.getCmp('note').setValue(obj.note);
   	
   },
 error:function(response) {
 	Ext.Msg.alert("错误",response.responseText);
 },
 failure:function(response) {
 	Ext.Msg.alert("错误",response.responseText);
 }

})
 }
}

Ext.onReady(function(){
	Ext.Msg = top.Ext.Msg;

	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();}
    if(saveType == 2){
      var win = new Ext.Viewport({
    	id:'win',
        loadMask : true,
        layout: 'border',
        items : [centerPanel,eastPanel],
        renderTo : Ext.getBody()
      });     
	  Ext.getCmp('apply').setVisible(false);
      Ext.getCmp('cancel').setVisible(false);
      Ext.getCmp('deleteEquip').setDisabled(true);
    //  Ext.getCmp('eastPanel').setDisabled(true);
    //  Ext.getCmp('engineerName').setEditable(false);
    //  Ext.getCmp('JobNo').setEditable(false);
    //  Ext.getCmp('telephone').setDisabled(true);
    //  Ext.getCmp('area').setDisabled(true);
      Ext.getCmp('firstLevelCombo').setDisabled(true);
      Ext.getCmp('secondLevelCombo').setDisabled(true);
      Ext.getCmp('thirdLevelCombo').setDisabled(true);
      Ext.getCmp('secondLevelResetButton').setDisabled(true);
      //Ext.getCmp("engineerName").getEl().dom.readOnly = true;
      //Ext.getCmp("JobNo").getEl().dom.readOnly = true;
      //Ext.getCmp("telephone").getEl().dom.readOnly = true;
      //Ext.getCmp("area").getEl().dom.readOnly = true;
      //Ext.getCmp("department").getEl().dom.readOnly = true;
      //Ext.getCmp("role").getEl().dom.readOnly = true;
      //Ext.getCmp("note").getEl().dom.readOnly = true;
      //Ext.getCmp("secondLevelResetButton").getEl().dom.readOnly = true;
      
      Ext.getCmp("engineerName").setDisabled(true);
      Ext.getCmp("JobNo").setDisabled(true);
      Ext.getCmp("telephone").setDisabled(true);
      Ext.getCmp("area").setDisabled(true);
      Ext.getCmp("department").setDisabled(true);
      Ext.getCmp("role").setDisabled(true);
      Ext.getCmp("note").setDisabled(true);
      Ext.getCmp("secondLevelResetButton").setDisabled(true);
	}else{
	  var win = new Ext.Viewport({
    	id:'win',
        loadMask : true,
        layout: 'border',
        items : [centerPanel,westPanel,eastPanel],
        renderTo : Ext.getBody()
    });
	}
    
 	initData();
  });