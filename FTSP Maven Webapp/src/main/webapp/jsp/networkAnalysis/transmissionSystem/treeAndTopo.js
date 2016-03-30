//------------------------------------------------
/**
 * 节点树
 */
var treeParams = {
	leafType : 4
};
var treeurl = "../../commonManager/tree.jsp?" + Ext.urlEncode(treeParams);
var nodeTree = new Ext.Panel({
	id : "nodeTree",
	boxMinWidth : 230,
	boxMinHeight : 260,
	border:false,
	forceFit : true,
	split : true,
	html : '<iframe id="tree_panel" name="tree_panel" src ="' + treeurl
			+ '" height="100%" width="100%" frameBorder=0 border=0/>'
});
//定义这个方法可以使树上出现>>按钮
function onGetChecked(getFunc) {
	if (step != 2) {
		return;
	}
	var oldList = new Array();
	neStore.each(function(r) {
		oldList.push(r.get('neId'));
	});

	var pathParam = "path" + ":" + "text";
	// var neResult=getFunc(["nodeLevel","nodeId",pathParam],"all",4);
	var neResult = getFunc([ "nodeLevel", "nodeId" ], "all");
	var i = 0;
	var list = new Array();
	for (i; i < neResult.length; i++) {
		if (neResult[i].nodeLevel != 4) {
			Ext.Msg.alert('提示', '只能选择网元！');
			return;
		}
		// var nodes = {
		// 'nodeId' : neResult[i].nodeId,
		// 'nodeLevel' : neResult[i].nodeLevel
		// };
		list.push(neResult[i].nodeId);
	}

	var addList = mergeArray(oldList, list);
	neStore.baseParams = {
		'intList' : addList
	};
	neStore.load({
		callback : function(records, options, success) {
			if (!success)
				Ext.Msg.alert("提示", "查询失败！");
		}
	});
}

/**
 * 拓扑图
 */
var topo = new Ext.Flex({
	id : 'topo',
	isLocalDebug : false,
	border:false,
//	xtype:'flex',
//	width : 280,
	minSize : 230,
	maxSize : 320
});

var topoPanel = new Ext.Panel({
	id : 'topoPanel',
	layout : 'fit',
	border : false,
	items : [ topo ],
	tbar : [ '->', {
		icon : '../../../resource/images/btnImages/rightarrow_grey.png',
		id : 'topobtn',
		disabled : true,
		handler : topoToNeGrid,
		listeners: {
        	'disable': function(th){
        		th.setIcon('../../../resource/images/btnImages/rightarrow_grey.png');
        	},
        	'enable': function(th){
        		th.setIcon('../../../resource/images/btnImages/rightarrow_red.png');
        	}
        }
	} ]
});

topo.on("initialize", function () {
    Ext.Ajax.request({
        url: "topo!getNode.action",
        method : 'POST',
        params:{"nodeId":-1,
				"nodeType":-1,
				"direction":"forward",
				"privilege":authSequence
				},
        scope:this,
        success:function(resp) {
            dat = Ext.decode(resp.responseText);
            if (dat&&dat.returnResult==0){
            	Ext.Msg.alert("提示", dat.returnMessage);
            }else{
            	Ext.getCmp("topo").loadData(dat);
            }
        }
    });
});
topo.on("nodeselect", function () {
	if(step != 2){
		Ext.getCmp("topobtn").disable();
		return;
	}
    var s = topo.swf;
    if(s.getSelectedNodes(4).length > 0){
        Ext.getCmp("topobtn").enable();
    }else{
    	Ext.getCmp("topobtn").disable();
    }
});
topo.on("nodeselectclear", function () {
	if(step != 2){
		Ext.getCmp("topobtn").disable();
		return;
	}
	var s = topo.swf;
	if(s.getSelectedNodes(4).length > 0){
		Ext.getCmp("topobtn").enable();
	}else{
		Ext.getCmp("topobtn").disable();
	}
});


function topoToNeGrid() {
	var oldList = new Array();
	neStore.each(function(r) {
		oldList.push(r.get('neId'));
	});
	var s = topo.swf;
	var nodes = s.getSelectedNodes(4);
	if (nodes.length > 0) {
		var list = new Array();
		nodes.every(function(r) {
			// var nodes = {
			// 'nodeId' : r,
			// 'nodeLevel' : 4
			// };
			list.push(r);
			return true;
		});

		var addList = mergeArray(oldList, list);
		neStore.baseParams = {
			'intList' : addList
		};
		neStore.load({
			callback : function(records, options, success) {
				if (!success)
					Ext.Msg.alert("提示", "查询失败！");
			}
		});
	}
}




var mergeArray = function(arr1, arr2) {
	for ( var i = 0; i < arr1.length; i++) {
		for ( var j = 0; j < arr2.length; j++) {
			if (arr1[i] === arr2[j]) {
				arr1.splice(i, 1); //利用splice函数删除元素，从第i个位置，截取长度为1的元素
			}
		}
	}
	//alert(arr1.length)
	for ( var i = 0; i < arr2.length; i++) {
		arr1.push(arr2[i]);
	}
	return arr1;
}