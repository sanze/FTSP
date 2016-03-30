/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
 
//指定provider
	Ext.state.Manager.setProvider(   
	    new Ext.state.SessionStorageStateProvider({   
	      expires: new Date(new Date().getTime()+(1000*60*60*24*365))   
	    })   
    );
 
var limit=200;
var store = new Ext.data.Store(
{
	url: 'inspect-engineer!getEngineerList.action',
	baseParams: {"limit":limit},
	reader: new Ext.data.JsonReader({
        totalProperty: 'total',
    	root : "rows"
    },[
	   {name:"packageManId",mapping:"INSPECT_ENGINEER_ID"},
	   {name:"name",mapping:"NAME"},
	   {name:"area",mapping:"AREA_NAME"},
	   {name:"department",mapping:"OFFICE"},
	   {name:"phone",mapping:"TELEPHONE"},
	   {name:"role",mapping:"ROLE"},
	   {name:"jobNo",mapping:"JOB_NO"},
	   {name:"note",mapping:"NOTE"}
    ])
});
store.load({
	callback: function(r, options, success){
		if(!success){
			var obj = Ext.decode(r.responseText);
    		Ext.Msg.alert("提示",obj.returnMessage);
		}
	}
});

var pageTool = new Ext.PagingToolbar({
	id:'pageTool',
    pageSize: limit,//每页显示的记录值
    store: store,
    displayInfo: true,
    displayMsg : '当前 {0} - {1} ，总数 {2}',
    emptyMsg: "没有记录"
});

 var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({singleSelect :false});
 var columnModel = new Ext.grid.ColumnModel({
        // specify any defaults for each column
        defaults: {
            sortable: true,
            forceFit:false
        },
        columns: [new Ext.grid.RowNumberer({
    		width : 26
    	}),checkboxSelectionModel,{
            id: 'packageManId',
            name:'packageManId',
            header: 'id',
            dataIndex: 'packageManId',
            hidden:true
        },{
            id: 'name',
            header: '包机人',
            width:(10+12*10),
            dataIndex: 'name',
            editor: new Ext.form.TextField({
                allowBlank: false
            })
        },{
            id: 'jobNo',
            header: '工号',
			width:(10+12*10),
            dataIndex: 'jobNo',
            editor: new Ext.form.TextField({
                allowBlank: false
            })
        },{
            id: 'phone',
            header: '电话',
			width:(10+12*10),
            dataIndex: 'phone',
            editor: new Ext.form.TextField({
                allowBlank: false
            })
        },{
            id: 'area',
            header: top.FieldNameDefine.AREA_NAME,
			width:(10+12*10),
            dataIndex: 'area',
            editor: new Ext.form.TextField({
                allowBlank: false
            })
        },{
            id: 'department',
            header: '部门',
			width:(10+12*15),
            dataIndex: 'department',
            editor: new Ext.form.TextField({
                allowBlank: false
            })
        },{
            id: 'role',
            header: '职务',
			width:(10+12*10),
            dataIndex: 'role',
            editor: new Ext.form.TextField({
                allowBlank: false
            })
        },{
            id: 'note',
            header: '备注',
			width:(10+12*10),
            dataIndex: 'note',
            editor: new Ext.form.TextField({
                allowBlank: false
            })
        }]
    });

var engineerPanel = new Ext.grid.EditorGridPanel({
	id:"engineerPanel",
	name:"engineerPanel",
	region:"center",
	stripeRows:true,
	autoScroll:true,
	frame:false,
	cm: columnModel,
	store:store,
	loadMask: true,
	clicksToEdit: 2,//设置点击几次才可编辑  
	selModel:checkboxSelectionModel ,  //必须加不然不能选checkbox 
	viewConfig: {
        forceFit:false
    },
	bbar: pageTool, 
	tbar: [{
			xtype: 'label',
			text: '',
			width: 20
		},'-',{
            text: '新增',
            icon:'../../../resource/images/btnImages/add.png',
            privilege:addAuth,
            handler : function(){
				editPackage(0,0);
        	}
        },'-',{
            text: '删除',
            icon:'../../../resource/images/btnImages/delete.png',
            privilege:delAuth,
            handler : function(){
               var selectRecord =engineerPanel.getSelectionModel().getSelections(); 
			   if(selectRecord.length == 0)
			   {
			      Ext.Msg.alert("提示","必须选择一或多个包机人务。");
			   }else{
			      var engineerIdList = new Array();
			      for(var i = 0; i< selectRecord.length;i++){
			         engineerIdList.push(selectRecord[i].get("packageManId"));
			      }
			      deletePackage(engineerIdList);
			   }
				
			}
        },'-',{
            text: '修改',
            icon:'../../../resource/images/btnImages/modify.png',
            privilege:modAuth,
            handler : function(){
               var selections = engineerPanel.getSelectionModel().getSelections();
               if(selections.length == 0)
			   {
			      Ext.Msg.alert("提示","必须选择一个包机人。");
			   }else if(selections.length > 1){
			      Ext.Msg.alert("提示","只可以选择一个包机人。");
			   }else{
				  editPackage(selections[0].get("packageManId"),1);
			   }
				 
        	}
        },'-',{
            text: '查看',
            icon:'../../../resource/images/btnImages/application.png',
            privilege:viewAuth,
            handler : function(){
				var selections = engineerPanel.getSelectionModel().getSelections();
				if(selections.length == 0)
			   {
			      Ext.Msg.alert("提示","必须选择一个包机人。");
			   }else if(selections.length > 1){
			      Ext.Msg.alert("提示","只可以选择一个包机人。");
			   }else{
				  editPackage(selections[0].get("packageManId"),2);
			   }
        	}
        },'-',{
            text: '导出',
            icon:'../../../resource/images/btnImages/export.png',
            privilege:actionAuth,
            handler : function(){
               exportInspectEngineer();
        	}
        }
	],
	stateId:'testStateRestoreId',  
	stateful:true
	/*,
	plugins: [rightMenu]*/
}); 
function deletePackage(engineerIdList){
Ext.Msg.confirm('提示','删除所选择的包机人。</br></br>确认删除?',
	function(btn){
		if(btn=='yes'){
		
		  var jsonData = {
	       "engineerIdList":engineerIdList
	      }
		
		  Ext.Ajax.request({
	      url:'inspect-engineer!deleteInspectEngineer.action',
	      method:'Post',
	      params:jsonData,
	      success: function(response) {
			    	var obj = Ext.decode(response.responseText);
		         	Ext.Msg.alert("信息",obj.returnMessage);
			    	store.reload();
			    },
	    error:function(response) {
	    	Ext.Msg.alert("错误",response.responseText);
	    },
	    failure:function(response) {
	    	Ext.Msg.alert("错误",response.responseText);
	    }
	   
	   }) 
		
		
		}
	});
}
//编辑包机人
function editPackage(engineerId,type){
	var editPackageWindow=new Ext.Window({
        id:'editPackageWindow',
        title:'包机设备设置',
        width:1000,
        height:500,
        isTopContainer : true,
        modal : true,
        autoScroll:true,
		//maximized:true,
        html:'<iframe src = "editPackage.jsp?saveType='+type+'&engineerId='+engineerId+'" height="100%" width="100%" frameBorder=0 border=0/>' 
     });
    editPackageWindow.show();
}

function exportInspectEngineer(){
  var count=engineerPanel.getStore().getCount();
  var msg;
  var engineerIdList = new Array();
  var selections = engineerPanel.getSelectionModel().getSelections();
  if(selections.length == 0 || selections.length == count)
  {
     msg = '导出全部包机人。</br></br>确认?';
     for(var i=0; i< store.getCount();i++){
       var engineerId = store.getAt(i).get("packageManId");
       engineerIdList.push(engineerId);
     }
  }else{
     msg = '导出选择的包机人。</br></br>确认?';
     for(var i = 0; i< selections.length;i++){
       engineerIdList.push(selections[i].get("packageManId"));
     }
  }
  Ext.Msg.confirm('提示',msg,
	function(btn){
		if(btn=='yes'){
		  var jsonData = {
			"engineerIdList":engineerIdList
		  };
		  Ext.Ajax.request({
	      url:'inspect-engineer!exportInspectEngineer.action',
	      method:'Post',
	      params:jsonData,
	      success: function(response) {
            top.Ext.getBody().unmask();
	    	var obj = Ext.decode(response.responseText);
	    	alert(obj.returnMessage);
	    	if(obj.returnResult == 1){
		    	window.location.href="download.action?"+Ext.urlEncode({filePath:obj.returnMessage});
            }
        	if(obj.returnResult == 0){
        		Ext.Msg.alert("信息","导出数据失败！");
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
	   
	   }) 
		
		
		}
	});

}

Ext.onReady(function(){

	Ext.Ajax.timeout=900000; 
	//collapse menu
	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();}
	Ext.Msg = top.Ext.Msg;
	
	
	
	var win = new Ext.Viewport({
        id:'win',
		layout : 'border',
		items : [engineerPanel]
	});
 });