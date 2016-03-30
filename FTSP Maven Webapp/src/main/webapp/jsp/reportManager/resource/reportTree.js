/**
 * 创建树
 */

var nodeReset = null;
var FAILED = 0;
function resetNode(node){// 加载失败时使用,重置节点为未加载状态
  node.loaded=false;
  node.attributes.expandable=true;
  node.collapse();
}
//根节点
var rootNode = new Ext.tree.AsyncTreeNode({
	text : 'FTSP',
	draggable : false,
	id : '0-0',
	checked : 'none',
	nodeId : 0,
	iconCls:'',
	leaf:false,
	expanded:true,
	uiProvider : rootVisible?Ext.ux.TreeCheckNodeUI:null
});

var treeLoader = new Ext.tree.TreeLoader({
	url : "report-tree!getChildNodes.action",
	baseAttrs: {uiProvider: Ext.ux.TreeCheckNodeUI,
					allowDrag: false,
					checked: "none"
				},
    nodeParameter: null,
	clearOnLoad: true,
	createNode : function (attr) {
		if (this.baseAttrs) {
			Ext.applyIf(attr, this.baseAttrs);
		}
		if (this.applyLoader !== false && !attr.loader) {
			attr.loader = this;
		}
		if (Ext.isString(attr.uiProvider)) {
			attr.uiProvider = this.uiProviders[attr.uiProvider] || eval(attr.uiProvider);
		}
		if (attr.nodeType) {
			return new Ext.tree.TreePanel.nodeTypes[attr.nodeType](attr);
		} else {
			return attr.leaf ? new Ext.tree.TreeNode(attr) : new Ext.tree.AsyncTreeNode(attr);
		}
	},
	listeners: {
		"beforeload": function(loader, node) {
	        loader.baseParams={
	        	type:type,
	        	parentIds:parentIds,
	        	ids:ids,
	        	id:node.attributes.nodeId
	        };
	    },
	    "load": function(loader, node, response) {
	    	function fleshNodeStatus(node){
	    		if(node.attributes.checked=="all"){
	    			node.ui.check("all");   
	    		}
			}
			rootNode.cascade(fleshNodeStatus);
	    	var result = Ext.decode(response.responseText);
	    	if(result&&(FAILED==result.returnResult)){
	           Ext.Msg.alert("提示",result.returnMessage);
	           nodeReset=node;
	           setTimeout("resetNode(nodeReset)",100);
               return;
	    	}
	    },
	    "loadexception": function(loader, node, response) {
	       Ext.Msg.alert("提示","节点加载失败!");
           nodeReset=node;
	       setTimeout("resetNode(nodeReset)",100);
	    }
	}
});





var treePanel = new Ext.ux.TreeCheckPanel({
	el:'tree-div',  
	region : "center",
	root : rootNode,
	rootVisible : rootVisible,
	autoScroll : true,
	animate : false,
	bodyStyle : 'padding:5px 5px 0',
	checkModel : "cascade",//"multiple"|"single"|"cascade"
	border : false,
	useArrows : false,
	forceLayout : true,
	border:true,
	enableDD : false,
	loader : treeLoader,
    selModel : new Ext.ux.TreeMultiSelectionModel(),//(checkModel=="single"?null:new Ext.ux.TreeMultiSelectionModel()),
    listeners: {
        checkchange: function(node,checked){
        	
        	
        	
        }
    },
    buttons : [{
		text : '确定',
		handler : getNodes
	},{
		text : '取消',
		handler : function(){
			var win = parent.Ext.getCmp('treeWindow');
			if (win) {
				win.close();
			}
		}
	}]
});

//function getCheckedNodes() {
//	var result = [];
//	var startNode = rootNode;
//	function checkMatch(node){
//		if ((!node.hidden)&&(node.attributes["checked"]=='all')) {
//    		return true;
//      	}
//      	return false;
//	}
//    var f = function() {
//    		if (checkMatch(this)){
//    		result.push(this);
//    	}
//    };
//    startNode.cascade(f);
//    return result;
//}


/**
 *参数:
 *  attribute:指定返回属性，String/Array
 *            可选，默认返回Node
 *  filter:   指定过滤类型，
 *            可选，默认为"all", ["top","leaf"]【所有、最顶层、最底层】
 *  level:    指定节点层级，Number/Array
 *            可选，默认为所有
 *  checked:  指定勾选类型，String/Array
 *            可选，默认为"all"，["all","part"]【全选、半选】
 *代码实例:     getCheckedNodes(["nodeId","nodeLevel","text"],"leaf",    [1,2],       ["all"]);
 *                              需要的节点属性列表                返回最底层    网管分组/网管   全选状态
 *返回值实例:   [{"nodeId":1,"nodeLevel":1,"text":"网管分组1"},{"nodeId":2010,"nodeLevel":2,"text":"一干_兰-成波分"}] 
 *
 *特殊说明:     若需要返回节点路径,attribute需包含符合格式"path[分隔符][属性]"的元素. 注: 分隔符指除数字,字母,下划线外的任意字符, 属性指拼接路径的节点属性.
 */
function getCheckedNodes(_attribute,_filter,_level,_checked) {
	var result = [];
	if(!_filter){ _filter="all";}
	if(!_checked){ _checked="all";}
	
	if(!Ext.isArray(_checked)){
		_checked=[_checked];
	}
	if(_level&&!Ext.isArray(_level)){
		_level=[_level];
	}
	
	var startNode = rootNode;
	function checkMatch(node){
		if ((!node.hidden)&&(_checked.indexOf(node.attributes["checked"])!=-1)) {
    		if((!_level)||
    			(_level.indexOf(node.attributes["nodeLevel"])!=-1)){
    			if(node.getDepth()>0||rootVisible){
    				return true;
                }
	    	}
      	}
      	return false;
	}
    var f = function() {
    	if (checkMatch(this)){
    		var toPut=false;
    		var rep=true;
    		if((_filter == "top")&&((this.getDepth()===0)||
    			(!checkMatch(this.parentNode)))){
    			toPut=true;
   				rep=false;
    		}else if(_filter == "leaf"){
    			if((!this.childNodes)||this.childNodes.length===0){
   					toPut=true;
   					rep=false;
    			}else{
    				var isChildMatch=false;
    				var length=this.childNodes.length;
    				for(var index=0;index<length;index+=1){
    					if(checkMatch(this.childNodes[index])){
    						isChildMatch=true;
    						break;
    					}
    				}
    				if(!isChildMatch){
    					toPut=true;
		   				rep=false;
    				}
    			}
    		}else{
   				toPut=true;
    		}
    		if(toPut){
    			if(!_attribute){
    				result.push(this);
    			}else if(Ext.isArray(_attribute)){
					var attr=new Object();
					for(var index in _attribute){
                      if(Ext.isFunction(_attribute[index])){
                      	continue;
                      }
                      //以path[分隔符]开始
                      if(_attribute[index].search(/^(path[\W.*])/)!=-1){
                        var pathAttr =_attribute[index];
                        var ownerTree=this.getOwnerTree();
                        if(pathAttr.length>('path'.length+1)){
                          ownerTree.pathSeparator = pathAttr.substring('path'.length,'path'.length+1);
                          pathAttr=pathAttr.substring('path'.length+1);
                        }else{
                          pathAttr='id';
                        }
                        if(rootVisible){
                          attr[_attribute[index]]=this.getPath(pathAttr).substring(ownerTree.pathSeparator.length);
                        }else{
                          attr[_attribute[index]]=
                            this.getPath(pathAttr).substring(
                                                     (ownerTree.pathSeparator+
                                                      ownerTree.root.attributes[pathAttr]+ownerTree.pathSeparator)
                                                     .length);
                        }
                      }else{
						attr[_attribute[index]]=this.attributes[_attribute[index]];
                      }
					}
					result.push(attr);
				}else{
                    if(_attribute.search(/^(path[\W.*])/)!=-1){
                      var pathAttr=_attribute;
                      var ownerTree=this.getOwnerTree();
                      if(pathAttr.length>('path'.length+1)){
                        ownerTree.pathSeparator = pathAttr.substring('path'.length,'path'.length+1);
                        pathAttr=pathAttr.substring('path'.length+1);
                      }else{
                        pathAttr='id';
                      }
                      if(rootVisible){
                        result.push(this.getPath(pathAttr)).substring(ownerTree.pathSeparator.length);
                      }else{
                        result.push(this.getPath(pathAttr).substring(
                                                     (ownerTree.pathSeparator+
                                                      ownerTree.root.attributes[pathAttr]+ownerTree.pathSeparator)
                                                     .length));
                      }
                    }else{
					  result.push(this.attributes[_attribute]);
                    }
				}
    		}
    		return rep;
    	}
    };
    startNode.cascade(f);
    return result;
}


function getNodes(){
	if(type==4 || type==5){
		var ids='';
		var texts='';
		var nodes=getCheckedNodes(['nodeId','text'],'leaf');
		if(nodes!=null && nodes.length>0){
			for(var i=0;i<nodes.length;i++){
				ids+=nodes[i].nodeId;
				texts+=nodes[i].text;
				if(i!=nodes.length-1){
					ids+=',';
					texts+=',';
				}
			}
		}
		if(type==4){
			parent.qValue.emsGroupIds=ids;
			parent.qValue.emsGroupNames=texts;
		}else if(type==5){
			parent.qValue.emsIds=ids;
			parent.qValue.emsNames=texts;
		}
		 if(parent&&parent.fillChooseInfo){
	            parent.fillChooseInfo();
		 }
	}
	
	
	
	
	closeTreeWindow();
}

function closeTreeWindow(){
	var win = parent.Ext.getCmp('treeWindow');
	if (win) {
		win.close();
	}
}


Ext.onReady(function() {
	document.onmousedown = function() {
		if(window!=top)top.Ext.menu.MenuMgr.hideAll();
	};
	Ext.Msg = top.Ext.Msg;
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	Ext.QuickTips.init();
	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		width:800,
		height:500,
		border:true,
		items : [treePanel]
		
	});
});
