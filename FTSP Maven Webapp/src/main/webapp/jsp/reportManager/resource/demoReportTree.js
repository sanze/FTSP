




//var treeParams={//加载区域
//		type:'1',
//		parentIds:'',
//		ids:'0'
//};

//var treeParams={//加载网元组,  parentIds可以不设置
//		type:'4',
//		parentIds:'0',
//		ids:'2'
//};
//
//var treeParams={//加载网元组下 parentIds必须设置，格式为(2,3)
//		type:'5',
//		parentIds:'2,3',
//		ids:'2,4,11,12'
//};

var qValue={
		emsGroupIds:'',
		emsGroupNames:'',
		emsIds:'',
		emsNames:''
};

var emsGroup = new Ext.form.TextField({
	id : 'emsGroup',
	fieldLabel : '网管分组',
	listeners: {
        focus:function(){
        	getTree(4);
        }
    }
});
var ems = new Ext.form.TextField({
	id : 'ems',
	fieldLabel : '网管',
	listeners: {
		  focus:function(){
	        	getTree(5);
	        }
    }
});

/**
 * 创建查询条件panel
 */
var queryPanel = new Ext.Panel({
	id : 'queryPanel',
	height : 200,
	border : false,
	tbar : ['&nbsp;&nbsp;网管分组：',emsGroup,'-','网管：',ems,'-']
	
});



var emsGroupIds;
var emsIds;
//填入相应的信息
function fillChooseInfo(){
	emsGroupIds=qValue.emsGroupIds;
	emsIds=qValue.emsIds;
	emsGroup.setValue(qValue.emsGroupNames);
	ems.setValue(qValue.emsNames);
}


//获取树状数据
function getTree(type){
	var treeParams={};
	if(type==4){
		treeParams.type=type;
		treeParams.parentIds='';
		treeParams.ids=qValue.emsGroupIds;
	}else if(type==5){
		if(qValue.emsGroupIds==null || qValue.emsGroupIds==''){
			Ext.Msg.alert("错误","请先选择网元组");
			return;
		}
		treeParams.type=type;
		treeParams.parentIds=qValue.emsGroupIds;
		treeParams.ids=qValue.emsIds;
	}
	
	var treeurl="reportTree.jsp?"+Ext.urlEncode(treeParams);
	var url ='<iframe id="tree_panel" name="tree_panel" src ="'+treeurl+'" height="100%" width="100%" frameBorder=0 border=0/>'
	var treeWindow=new Ext.Window({
        id:'treeWindow',
        title:'',
        width:350,
        height:300,
        isTopContainer : true,
        modal : true,
        autoScroll:true,
		maximized:false,
        html:url
     });
	treeWindow.show();
}

Ext.onReady(function(){
	Ext.Msg = top.Ext.Msg;
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	
//	console.info(emsGroup);
	//document.onmousedown=function(){parent.Ext.menu.MenuMgr.hideAll();};
    var win = new Ext.Viewport({
    	id:'win',
        loadMask : true,
        items : queryPanel,
        renderTo : Ext.getBody()
    });
  });