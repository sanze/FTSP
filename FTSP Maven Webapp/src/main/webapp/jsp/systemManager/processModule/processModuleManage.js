var start  = 0;
var limit  = 10;
var selections = undefined;
//数据源
var store = new Ext.data.Store({
	url: 'process-module-manage!getProcessModuleData.action',
	reader: new Ext.data.JsonReader({
        totalProperty: 'total',
    	root : "rows"
    },[
	   {name:"ID",mapping:"ID"},
	   {name:"MODULE_NAME",mapping:"MODULE_NAME"},
	   {name:"MODULE_STATE",mapping:"MODULE_STATE"}
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
    		Ext.Msg.alert("提示",'导入数据失败,请重试!');
		}
	}
});

//分页工具栏
 var pageTool = new Ext.PagingToolbar({
	id:'pageTool',
    pageSize: limit,//每页显示的记录值
	store: store,
    displayInfo: true,
    displayMsg : '当前 {0} - {1} ，总数 {2}',
    emptyMsg: "没有记录"
 });

 //列表选择模式
 var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
	 singleSelect :false
 });
 
 //列头
 var columnModel = new Ext.grid.ColumnModel({
        defaults: {
            sortable: true,
            forceFit:false,
            align:'center'
        },
        columns: [new Ext.grid.RowNumberer({
    		width : 26
    	}),checkboxSelectionModel,{
            id: 'ID',
            name:'ID',
            header: 'ID',
            dataIndex: 'ID',
            hidden:true
        },{
            id: 'MODULE_NAME',
            header: '模块名称',
            width:(10+12*10),
            dataIndex: 'MODULE_NAME'
        },{
            id: 'MODULE_STATE',
            header: '模块状态',
			width:(10+12*10),
            dataIndex: 'MODULE_STATE',
            renderer : function(value,meta,record){
    			if(value==1){
    				meta.css="reportColor4";
    				return '开启';
    			}else{
    				meta.css="reportColor5";
    				return '关闭';
    			}

    		}
        }]
    });
 
var gridPanel = new Ext.grid.EditorGridPanel({
	title : '',
	id:"gridPanel",
	region:"center",
	stripeRows:true,
	autoScroll:true,
	frame:false,
	cm: columnModel,
	store:store,
	columnLines:true,
	loadMask: true,
	clicksToEdit: 2,//设置点击几次才可编辑  
	selModel:checkboxSelectionModel ,  //必须加不然不能选checkbox 
	viewConfig: {
        forceFit:false
    },
	bbar: pageTool, 
	tbar: {
		    items:[
       {xtype: 'tbspacer', width:20,shadow:false},       
       {
			xtype: 'label',
			text: '',
			width: 20
		},{
            text: '开启',
            //icon:'../../../resource/images/buttonImages/add.png',
            handler : function(){
                var selectRecord =gridPanel.getSelectionModel().getSelections(); 
 			   if(selectRecord.length == 0){
 			      Ext.Msg.alert("提示","请选择一行");
 			   }else{
 				   var deleArr=[];
 				   for(var i=0;i<selectRecord.length;i++){
 					   deleArr.push(selectRecord[i].get("ID"));
 				   }
 				   changeState(deleArr,1);
 			   }
 			}
        },
        {xtype: 'tbspacer', width:20,shadow:false},   
        {
            text: '关闭',
            //icon:'../../../resource/images/buttonImages/delete.png',
            handler : function(){
               var selectRecord =gridPanel.getSelectionModel().getSelections(); 
			   if(selectRecord.length == 0){
			      Ext.Msg.alert("提示","请选择一行");
			   }else{
				   var deleArr=[];
				   var ids=""
				   for(var i=0;i<selectRecord.length;i++){
					   if(i==(selectRecord.length-1)){
						   ids+=selectRecord[i].get("ID");
					   }else{
						   ids+=selectRecord[i].get("ID")+",";
					   }
				   }
				   changeState(ids,2);
			   }
			}
        }
	]
	}
}); 

function changeState(Arr,flag){
	var valuetype="";
	if(flag==1){
		valuetype="开启";
	}else{
		valuetype="关闭";
	}
	Ext.Msg.confirm('提示','确认'+valuetype+'吗?',function(btn){
			if(btn=='yes'){
				  Ext.Ajax.request({
				      url:'process-module-manage!changeState.action',
				      method:'Post',
				      params:{
				    	  'ids':Arr,
				  		  'flag':flag
				      },
				      success: function(response) {
					    	var obj = Ext.decode(response.responseText);
							if(obj.success){//
								store.reload();
							}else{
								Ext.Msg.alert("错误",obj.msg);
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
		});
}



Ext.onReady(function(){
	Ext.Msg = top.Ext.Msg;
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	Ext.QuickTips.init(); // 开启悬停提示
	Ext.form.Field.prototype.msgTarget = 'side'; // 提示显示风格
	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();}
	var win = new Ext.Viewport({
        id:'win',
		layout : 'border',
		items : [gridPanel]
	});
 });