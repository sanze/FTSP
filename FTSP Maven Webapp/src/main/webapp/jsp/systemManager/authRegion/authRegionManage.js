/*!
 * Ext JS easy grid
 * creat by heguo 2013 year 12 manth 
 */
var start  = 0;
var limit  = 200;
var selections = undefined;
var store = new Ext.data.Store(
{
	url: 'auth-region-manage!searchAuthDomain.action',
	reader: new Ext.data.JsonReader({
        totalProperty: 'total',
    	root : "rows"
    },[
	   {name:"id",mapping:"sys_auth_domain_id"},
	   {name:"name",mapping:"name"},
	   {name:"note",mapping:"note"}
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

 
 var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
	 	singleSelect :false
	 });
 var columnModel = new Ext.grid.ColumnModel({
        // specify any defaults for each column
        defaults: {
            sortable: true,
            forceFit:false,
            align:'left'
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
            header: '权限域名称',
            width:(10+12*10),
            dataIndex: 'name'
        },{
            id: 'note',
            header: '描述',
			width:(10+25*10),
            dataIndex: 'note'
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
	tbar: { items:[
	               '-',{
	                    text: '新增',
	                    privilege : addAuth,
	            		icon : '../../../resource/images/btnImages/add.png',
	                    handler : function(){
	        				edit(0,0);
	                	}
	                },
	              
	                {
	                    text: '删除',
	                    privilege : delAuth,
	            		icon : '../../../resource/images/btnImages/delete.png',
	                    handler : function(){
	                       var selectRecord =gridPanel.getSelectionModel().getSelections(); 
	        			   if(selectRecord.length == 0){
	        			      Ext.Msg.alert("提示","请选择一行进行删除");
	        			   }else{
	        				   var deleArr=[];
	        				   for(var i=0;i<selectRecord.length;i++){
	        					   deleArr.push(selectRecord[i].get("id"));
	        				   }
	        			      deleteAuthRegion(deleArr);
	        			   }
	        			}
	                },
	                 
	                {
	                    text: '修改',
	                    privilege : modAuth,
	            		icon : '../../../resource/images/btnImages/modify.png',
	                    handler : function(){
	                         selections = gridPanel.getSelectionModel().getSelections();
	                         if(selections.length !=1){
	                        	 Ext.Msg.alert("提示","请选择一行进行修改");
	                         }else{
	                        	 edit(selections[0].get("id"),1);
	          			   	}
	        				 
	                	}
	                },
	                '-',   
	                {
	                    text: '详请',
	            		icon : '../../../resource/images/btnImages/application.png',
	                    height:25,
	                    width:30,
	                    style:'font-size:50px',  
	                    privilege : viewAuth,
	                    handler : function(){
	                         selections = gridPanel.getSelectionModel().getSelections();
	                         if(selections.length !=1){
	            			      Ext.Msg.alert("提示","请选择一行进行查看");
	            			   }else{
	            				   show(selections[0].get("id"));
	            			   }
	        				 
	                	}
	                }
	        	]
	}
}); 

function deleteAuthRegion(deleArr){
	Ext.Msg.confirm('提示','确认删除吗?',function(btn){
		if(btn=='yes'){
			  var jsonData = {"id":id}
			  Ext.Ajax.request({
			      url:'auth-region-manage!deleteAuthRegions.action',
			      method:'Post',
			      params:{
			    	  'ids':deleArr
			      },
			      success: function(response) {
				    	var obj = Ext.decode(response.responseText);
						if(obj.success){//新增成功
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

//新增及修改
function edit(id,type){
	var title="";
	if(type==0){
		title="新增权限管理域";
	}else if(type==1){
		title="修改权限管理域";
	}
	var url = '<iframe src = "editAuthRegion.jsp?saveType='+type+'&id='+id+'" height="100%" width="100%" frameBorder=0 border=0/>';
	var editWindow=new Ext.Window({
        id:'editWindow',
        title:title,
        width : Ext.getBody().getWidth()*0.6,      
        height : Ext.getBody().getHeight()-50, 
        isTopContainer : true,
        modal : true,
        autoScroll:false,
		maximized:false,
        html:url
     });
    editWindow.show();
}

//详细
function show(id){
	var url = '<iframe src = "showAuthRegion.jsp?id='+id+'" height="100%" width="100%" frameBorder=0 border=0/>';
	var editWindow=new Ext.Window({
        id:'editWindow',
        title:'详细',
        width : Ext.getBody().getWidth()*0.6,      
        height : Ext.getBody().getHeight()*0.9, 
        isTopContainer : true,
        modal : true,
        autoScroll:false,
		maximized:false,
        html:url
     });
    editWindow.show();
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