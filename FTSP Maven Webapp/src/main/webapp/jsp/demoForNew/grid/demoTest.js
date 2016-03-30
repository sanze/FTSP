/*!
 * Ext JS easy grid
 * creat by heguo 2013 year 12 manth 
 */
var start  = 0;
var limit  = 10;
var selections = undefined;
var store = new Ext.data.Store(
{
	url: 'demo!getAllDemoData.action',
//	baseParams: {
//		"start":0,
//		"limit":limit
//		},
	reader: new Ext.data.JsonReader({
        totalProperty: 'total',
    	root : "rows"
    },[
	   {name:"id",mapping:"ID"},
	   {name:"name",mapping:"NAME"},
	   {name:"address",mapping:"ADDRESS"},
	   {name:"ip",mapping:"IP"},
	   {name:"phone",mapping:"PHONE"},
	   {name:"role",mapping:"ROLE"},
	   {name:"note",mapping:"NOTE"}
    ])
});
store.load({
	 params : {//这两个参数是分页的关键，当你点击 下一页 时，这里的值会传到后台,也就是会重新运行：store.load
		    start : 0,   
		    limit : limit
		   },
	callback: function(r, options, success){
		if(success){

		}else{
			var obj = Ext.decode(response.responseText);
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

 
 var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({singleSelect :true});
 var columnModel = new Ext.grid.ColumnModel({
        // specify any defaults for each column
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
            id: 'name',
            header: '姓名',
            width:(10+12*10),
            dataIndex: 'name'
        },{
            id: 'address',
            header: '地址',
			width:(10+12*10),
            dataIndex: 'address'
        },{
            id: 'ip',
            header: 'IP地址',
			width:(10+12*10),
            dataIndex: 'ip',
            editor: new Ext.form.TextField({
                allowBlank: false
            })
        },{
            id: 'phone',
            header: '电话',
			width:(10+12*10),
            dataIndex: 'phone'  
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
/*var rightMenu = new Ext.ux.grid.RightMenu({ 
	 items : [{ 
			 text : '删除', 
			 recHandler : deletePackage
		 }, { 
			 text : '修改', 
			 recHandler : function(record, rowIndex, grid) { 
			 } 
		 }] 
 }); */

 
var gridPanel = new Ext.grid.EditorGridPanel({
	title : 'Demo Test',
	id:"gridPanel",
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
		},{
            text: '新增',
            icon:'../../../resource/images/buttonImages/add.png',
            handler : function(){
				edit(0,0);
        	}
        },{
            text: '删除',
            icon:'../../../resource/images/buttonImages/delete.png',
            handler : function(){
               var selectRecord =gridPanel.getSelectionModel().getSelections(); 
			   if(selectRecord.length == 0)
			   {
			      Ext.Msg.alert("提示","请选择一行进行删除");
			   }else{
			      deleteId(selectRecord[0].get("id"));
			   }
				
			}
        },{
            text: '修改',
            icon:'../../../resource/images/buttonImages/modify.png',
            handler : function(){
                 selections = gridPanel.getSelectionModel().getSelections();
                 if(selections.length == 0)
  			   {
  			      Ext.Msg.alert("提示","请选择一行进行修改");
  			   }else{
  				 edit(selections[0].get("id"),1);
  			   }
				 
        	}
        },{
            text: '查看',
            icon:'../../../resource/images/buttonImages/setTask.png',
            handler : function(){
                 selections = gridPanel.getSelectionModel().getSelections();
                 if(selections.length == 0)
    			   {
    			      Ext.Msg.alert("提示","请选择一行进行查看");
    			   }else{
    				   show(selections[0].get("id"),1);
    			   }
				 
        	}
        }
	]/*,
	plugins: [rightMenu]*/
}); 
function deleteId(id){
Ext.Msg.confirm('提示','请确认删除?',
	function(btn){
		if(btn=='yes'){
		
		  var jsonData = {
	       "id":id
	      }
		
		  Ext.Ajax.request({
	      url:'demo!deleteDemoTest.action',
	      method:'Post',
	      params:jsonData,
	      success: function(response) {
//	    	  	console.info(response);
		    	Ext.Msg.alert("提示","删除成功");
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
//编辑
function edit(id,type){
	var url = '<iframe src = "editDemo.jsp?saveType='+type+'&id='+id+'" height="100%" width="100%" frameBorder=0 border=0/>';
	var editWindow=new Ext.Window({
        id:'editWindow',
        title:'新增/修改',
        width:600,
        height:400,
        isTopContainer : true,
        modal : true,
        autoScroll:true,
		maximized:false,
        html:url
     });
    editWindow.show();
}
//编辑
function show(id,type){
	var url = '<iframe src = "showDemo.jsp?saveType='+type+'&id='+id+'" height="100%" width="100%" frameBorder=0 border=0/>';
	var editWindow=new Ext.Window({
        id:'editWindow',
        title:'查看详细',
        width:600,
        height:400,
        isTopContainer : true,
        modal : true,
        autoScroll:true,
		maximized:false,
        html:url
     });
    editWindow.show();
}

Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL="../../ext/resources/images/default/s.gif";
	Ext.Ajax.timeout=900000; 
	//collapse menu
	document.onmousedown=function(){parent.Ext.menu.MenuMgr.hideAll();}
	Ext.Msg = top.Ext.Msg;
	
	var win = new Ext.Viewport({
        id:'win',
		layout : 'border',
		items : [gridPanel]
	});
 });