/*
 节点数据格式
 attributes{
   id : (nodeLevel +'-'+ nodeId),
   nodeId,
   nodeLevel,
   parent: (parentLevel +'-'+ parentId),
   parentId,
   parentLevel,
   text,
   leaf,
   checked
 }
*/
/*
示例:
  引用tree.jsp的页面定义如下函数,可显示一个添加勾选节点的按钮,且触发事件为该函数
  function onGetChecked(getFunc){
    result=getFunc(["nodeId","nodeLevel","text"],"leaf",[1,2],["all"]);
    console.log(Ext.util.JSON.encode(result));
    ...
  }
*/
var FAILED = 0;

var nodeReset = null;
function resetNode(node){// 加载失败时使用,重置节点为未加载状态
  node.loaded=false;
  node.attributes.expandable=true;
  node.collapse();
}

var rootNode = new Ext.tree.AsyncTreeNode({
	text: 'FTSP3000',   
	draggable : false,
	id : '0',
	leaf:false,
	checked : "none",
	iconCls : ''
	//uiProvider : ''
});
var treeLoader = new Ext.tree.TreeLoader({
	url : "auth-region-manage!getAuthTreeNodes.action",
	baseAttrs: {uiProvider: Ext.ux.TreeCheckNodeUI,
				allowDrag: false,
				checked: "none"
				},
    nodeParameter: null,
	clearOnLoad: true,
	listeners: {
		"beforeload": function(loader, node) {//加载前触发
	        loader.baseParams={
        		menuId: node.id,
        		id: id
	        };
	    },
	    "load": function(loader, node, response) {//加载后触发
	    	var result = Ext.decode(response.responseText);
	    	if(result&&(FAILED==result.returnResult)){
	           Ext.Msg.alert("提示",result.returnMessage);
	           nodeReset=node;
	           setTimeout("resetNode(nodeReset)",100);
               return;
	    	}
        
            
            function updateNodeStatus(node){
            	  function judgeNodeAtModifyArr(node){
                  	var operaNes=parent.operaNes;
                  	if(operaNes!=null && operaNes.length>0){
                  		for(var i=0;i<operaNes.length;i++){
                  			if(node.attributes["id"]==operaNes[i].id){
                  				return true;
                  			}
                  		}
                  	}
                  	return false;
            	  }
            	  if(judgeNodeAtModifyArr(node)){
            		  	node.attributes.checked="all";
            			node.ui.check("all");   
            	  }
            }
            if(node.childNodes && node.childNodes[0].leaf){
            	node.cascade(updateNodeStatus);
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
	region : "center",
	root : rootNode,
	rootVisible : rootVisible,
	autoScroll : true,
	animate : false,
	bodyStyle : 'padding:20px 0 0 20px',
	checkModel : 'cascade',//"multiple"|"single"|"cascade"
	border : false,
	useArrows : false,
	onlyLeafCheckable: true,
	forceLayout : true,
	enableDD : false,
	pathSeparator:'->',//树节点路径的分隔符，默认为'/'
	loader : treeLoader,
    selModel : new Ext.ux.TreeMultiSelectionModel(),//(checkModel=="single"?null:new Ext.ux.TreeMultiSelectionModel()),
    listeners: {
        checkchange: function(node,checked){
          if(parent&&parent.onCheckChange){
            parent.onCheckChange(node,checked);
          }
        }
    }
});
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




Ext.onReady(function() {
	document.onmousedown = function() {
		if(window!=top)top.Ext.menu.MenuMgr.hideAll();
	};
	Ext.Msg = top.Ext.Msg;
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	Ext.QuickTips.init();
	
	rootNode.expand();
	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [treePanel]
	});
});
