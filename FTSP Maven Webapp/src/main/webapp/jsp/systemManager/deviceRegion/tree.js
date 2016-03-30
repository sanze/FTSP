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
var SEARCH_LEVELS=["根","网管分组","网管","子网","网元"/*,"机架-子架","槽道-单元","子槽道-子单元","端口"*/];
var queryLevel=leafType>NodeDefine.NE?NodeDefine.NE:leafType;
var searchLevel=queryLevel;
var gKey = null;
var pathNodesId = null;
var FAILED = 0;

var nodeReset = null;
function resetNode(node){// 加载失败时使用,重置节点为未加载状态
  node.loaded=false;
  node.attributes.expandable=true;
  node.collapse();
}

var rootNode = new Ext.tree.AsyncTreeNode({
	text : rootText,
	draggable : false,
	id : (rootType+'-'+rootId),
	checked : 'none',
	nodeLevel : rootType,
	nodeId : rootId,
	iconCls : (rootType<nodeIconCls.length?nodeIconCls[rootType]:""),
	uiProvider : rootVisible?Ext.ux.TreeCheckNodeUI:null
});
var treeLoader = new Ext.tree.TreeLoader({
	url : "tree!getChildNodes.action",
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
		// 设置层级图标
		if(nodeIconCls&&attr['nodeLevel']&&(attr['nodeLevel']<nodeIconCls.length)){
			attr.iconCls=nodeIconCls[attr.nodeLevel];
		}
		if (attr.nodeType) {
			return new Ext.tree.TreePanel.nodeTypes[attr.nodeType](attr);
		} else {
			return attr.leaf ? new Ext.tree.TreeNode(attr) : new Ext.tree.AsyncTreeNode(attr);
		}
	},
	listeners: {
		"beforeload": function(loader, node) {//加载前触发
	        loader.baseParams={
	        	nodeId: node.attributes['nodeId'],
	        	nodeLevel: node.attributes['nodeLevel'],
	        	endLevel: leafType
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
        
            //从父节点继承emsId属性
            var childCnt=node.childNodes?node.childNodes.length:0;
            for(var i=0;i<childCnt;i++){
              if(node.childNodes[i].attributes["nodeLevel"]==NodeDefine.EMSGROUP){
                node.childNodes[i].attributes['emsGroupName']=node.childNodes[i].attributes['text'];
              }else if(node.childNodes[i].attributes["nodeLevel"]==NodeDefine.EMS){
                node.childNodes[i].attributes['emsId']=node.childNodes[i].attributes['nodeId'];
                node.childNodes[i].attributes['emsGroupName']=node.attributes['emsGroupName'];
                node.childNodes[i].attributes['emsName']=node.childNodes[i].attributes['text'];
              }else if(node.childNodes[i].attributes["nodeLevel"]>NodeDefine.EMS){
                node.childNodes[i].attributes['emsId']=node.attributes['emsId'];
                node.childNodes[i].attributes['emsGroupName']=node.attributes['emsGroupName'];
                node.childNodes[i].attributes['emsName']=node.attributes['emsName'];
              }
            }
            
//            function updateNodeStatus(node){
//            	  function judgeNodeAtModifyArr(node){
//                  	var operaNes=parent.operaNes;
//                  	if(operaNes!=null && operaNes.length>0){
//                  		for(var i=0;i<operaNes.length;i++){
//                  			if(node.attributes["id"]==operaNes[i].id){
//                  				return true;
//                  			}
//                  		}
//                  	}
//                  	return false;
//            	  }
//            	  if(judgeNodeAtModifyArr(node)){
//            		  	node.attributes.checked="all";
//            			node.ui.check("all");   
//            	  }else{
//            		  node.attributes.checked="none";
//          			  node.ui.check("none");   
//            	  }
//            }
//            //判定节点是否在修改队列中
//            if(node.parentNode){
//            	node.cascade(updateNodeStatus);
//            }
            
	    },
	    "loadexception": function(loader, node, response) {
	       Ext.Msg.alert("提示","节点加载失败!");
           nodeReset=node;
	       setTimeout("resetNode(nodeReset)",100);
	    }
	}
});

//网元combobox的store
var searchStore = new Ext.data.Store(
{
    proxy : new Ext.data.HttpProxy({
              url: 'tree!searchNodes.action',
              async: false
            }),
//	paramNames: {start:'startNumber',limit:'pageSize'},
    pageSize:10,
	reader: new Ext.data.JsonReader({
        totalProperty: 'total',
		root : "rows"
    },[
	    "nodeId","nodeLevel","text","parentId","parentLevel","parent"
    ])
});

var treeToolBar = null;
if((rootType+2<=SEARCH_LEVELS.length)&&(leafType>rootType)||(parent&&parent.onGetChecked)){
  treeToolBar=new Ext.Toolbar({items:["-"]});
  if((rootType+2<=SEARCH_LEVELS.length)&&(leafType>rootType)){
    treeToolBar.add("查询");
    button = new Ext.Button({
      iconCls: nodeIconCls[queryLevel],
      tooltip: '对象类型',
      text: queryLevel<nodeIconCls.length?null:SEARCH_LEVELS[queryLevel/*(leafType<SEARCH_LEVELS.length)?leafType:(SEARCH_LEVELS.length-1)*/]
    });
    if((rootType+3<=SEARCH_LEVELS.length)&&(leafType-rootType>1)){
      button.menu=new Ext.menu.Menu();
      endLv=(leafType<SEARCH_LEVELS.length)?leafType:(SEARCH_LEVELS.length-1);
      for(var lv=rootType+1;lv<=endLv;lv++){
        button.menu.addMenuItem({
          text: SEARCH_LEVELS[lv],
          level: lv,
          iconCls: nodeIconCls[lv],
          handler: function(b,e){
            var parent=b.parentMenu.findParentByType('button');
            if(b.iconCls){
              parent.setIconClass(b.iconCls);
              parent.setText(null);
            }else{
              parent.setText(b.text);
              parent.setIconClass(null);
            }
            var combo=b.parentMenu.findParentByType('toolbar').get('searchCombo');
            combo.lastQuery=null;
            queryLevel=b.level;}
        });
      }
    }
    treeToolBar.add(button);
    treeToolBar.add(new Ext.form.ComboBox({
        id: 'searchCombo',
        width : 120,
        minListWidth: 220,
        store: searchStore,
        valueField: 'nodeId',
        displayField: 'text',
        emptyText : '输入对象名',
        listEmptyText: '未找到匹配的结果',
        loadingText: '搜索中...',
        mode:'remote', 
        //minChars:1,  //输入几个字符开始搜索
        pageSize:searchStore.pageSize,
        queryDelay: 500,
        //selectOnFocus: true,// 获得焦点时选中所有已输入文本
        //hideTrigger : true,
        typeAhead: false,
        autoSelect:false,
        enableKeyEvents : true,
        resizable: true,
        autoScroll:true,
        listeners : {
          beforeselect: function(combo,record,index){
            gKey=record.get('nodeId');
            searchLevel=queryLevel;
            getAllPathNodes(gKey,searchLevel,rootId,rootType);
            combo.collapse();
            return false;
          },
          keypress: function(field, event) {
            field.setValue(field.getRawValue());
            if(event.getKey()==event.ENTER){//输入回车后开始过滤节点树
              gKey = field.getValue();
              if(gKey === null || gKey===""){
                pathNodesId = null;
                unFilterNodes(rootNode);
                return;
              }
              searchLevel=queryLevel;
              getAllPathNodes(gKey,searchLevel,rootId,rootType);
            }
          },
          beforequery:function(queryEvent){
            if(queryEvent.combo.lastQuery!=queryEvent.combo.getRawValue()){
                          queryEvent.combo.lastQuery=queryEvent.combo.getRawValue();
              queryNodes(queryEvent.combo,queryEvent.combo.getRawValue(),queryLevel,rootId,rootType);
              return false;
            }
          },
          scope : this
        }
      }));
    }
}
var treePanel = new Ext.ux.TreeCheckPanel({
	region : "center",
	root : rootNode,
	rootVisible : rootVisible,
	autoScroll : true,
	animate : false,
	bodyStyle : 'padding:5px 5px 0',
	// enableDD:true,
	// containerScroll: true,
	checkModel : checkModel,//"multiple"|"single"|"cascade"
    // onlyLeafCheckable: onlyLeafCheckable,
	border : false,
	useArrows : false,
	forceLayout : true,
	enableDD : false,
	pathSeparator:'->',//树节点路径的分隔符，默认为'/'
	loader : treeLoader,
	tbar : treeToolBar,
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

function queryNodes(combo,gKey,searchLevel,rootId,rootLevel){
    searchStore.baseParams={
		text: gKey,
		nodeLevel:searchLevel,
        endId: rootId,
        endLevel: rootLevel,
		hasPath:false,
		limit: searchStore.pageSize
	};
	searchStore.load({
		callback : function(records,options,success){
			if(!success)
				Ext.Msg.alert("提示","模糊搜索出错");
		}
	});
    combo.expand();
}
function getAllPathNodes(gKey,searchLevel,rootId,rootLevel){
	treePanel.getEl().mask('搜索中...');
	var path = 'tree!searchNodes.action';
	var params = {
		text: (typeof gKey == "number")?null:gKey,
        nodeId: (typeof gKey == "number")?gKey:0,
		nodeLevel: searchLevel,
        endId: rootId,
		endLevel: rootLevel,
		hasPath: true,
		limit: 0
	};
	Ext.Ajax.request({
		url : path,
		params: params,
		method : 'POST',
		success : function(response) {
			var result = Ext.util.JSON.decode(response.responseText);
	    	if(result&&(FAILED==result.returnResult)){
	    		Ext.Msg.alert("提示",result.returnMessage);
	    	}else{
				pathNodesId = result.rows;
				filterNodes(rootNode, pathNodesId, searchLevel);
				rootNode.collapseChildNodes(true);
				showResult(rootNode, pathNodesId, searchLevel);
			}
			treePanel.getEl().unmask();
		},
		failure : function(response) {
			treePanel.getEl().unmask();
			Ext.Msg.alert("提示", "过滤搜索出错");
		},
		error : function(response) {
			treePanel.getEl().unmask();
			Ext.Msg.alert("提示", "过滤搜索出错");
		}
	});
}
// 树节点与搜索结果列表匹配
// 返回结果: 0-不匹配 1-路径节点 2-目标节点
// add by 庄洁亮
function isMatched(node,keys){
	if(!keys||!keys.length||keys.length<1)
		return 0;
	for(var i in keys){
		var nodeId = node.attributes['nodeId'];
		var nodeLevel = node.attributes['nodeLevel'];
		if((nodeId==keys[i]['nodeId'])&&
			(nodeLevel==keys[i]['nodeLevel'])){
          if(keys[i]['checked']=="all")
            return 2;
          else
            return 1;
        }
	}
	return false;
}
// 树节点过滤
// 根据搜索结果列表过滤节点
// mod by 庄洁亮
function filterNodes(startNode, keys, endLevel) {
	function filterIt(){
		var nodeLevel = this.attributes['nodeLevel'];
		if(nodeLevel>endLevel)
			return false;
		var f = (this.getDepth()==0)||(isMatched(this,keys)>0);
		if (!f) {
            function isParentTarget(node){//同级嵌套情况,判断父节点是否有目标节点
              var nodeLevel = node.attributes['nodeLevel'];
              if(nodeLevel>=searchLevel){
                pathNode = node.parentNode;
                pathLevel = pathNode.attributes['nodeLevel'];
                if(pathLevel>=searchLevel){
                  if(isMatched(pathNode,keys)==2)//是目标节点
                    return true;
                  else
                    return isParentTarget(pathNode);
                }
                else
                  return false;
              }else
                return false;
            }
            function isChildChecked(node){//判断子节点是否有勾选节点
              var f=false;
              var childs = node.childNodes;
              if(childs){
                for(var i=0;i<childs.length;i++){
                  if(childs[i].attributes.checked!= "none"){
                    f=true;
                    break;
                  }
                  f=f||isChildChecked(childs[i]);
                }
              }
              return f;
            }
			if(this.attributes.checked!= "none"||isChildChecked(this)||isParentTarget(this)){
				this.ui.show();
			}else {
				this.ui.hide();
				return false
			}
		} else {
			this.ui.show();
		}
	}
	startNode.cascade(filterIt);
};
// 过滤结果显示
// 展开显示首个结果节点
// add by 庄洁亮
function showResult(startNode, keys, endLevel) {
	function firstMatchedChild(node,keys, endLevel){
		var child=node.firstChild;
		while(child){
			if((child.attributes['nodeLevel']<=endLevel)&&isMatched(child,keys)>0)
				return child;
			child=child.nextSibling;
		}
		return child;
	}
	function expandFirst(node){
		var firstChild=firstMatchedChild(node,keys,endLevel);
        if(isMatched(firstChild,keys)==2)//当前节点已是目标节点时取消继续展开
          return false;
		if(firstChild) firstChild.expand(false,false,expandFirst);
		else node.collapse();// 没有孩子节点匹配时收起
	}
	startNode.expand(false,false,expandFirst);
}
function unFilterNodes(startNode) {
	startNode = startNode||rootNode;
	function showIt(){
		if(this.hidden){
			this.ui.show();
			return false;
		}
	}
	startNode.cascade(showIt);
	startNode.collapseChildNodes(true);
};
function initData(){
	if(rootVisible&&rootText==null){
		var params={nodeId:rootId,nodeLevel:rootType};
		Ext.Ajax.request({
			url : "tree!getNode.action",
			params : params,
			method : 'POST',
			success : function(response) {
				var result = Ext.util.JSON.decode(response.responseText);
				if(result&&(FAILED==result.returnResult))
	    			Ext.Msg.alert("提示",result.returnMessage);
	    		else{
					rootNode.setText(result.text);
				}
			},
			failure : function(response) {
				Ext.Msg.alert("提示", "初始化加载失败");
			},
			error : function(response) {
				Ext.Msg.alert("提示", "初始化加载失败");
			}
		});
	}
	rootNode.expand();
	treePanel.on('expandnode', function(node) {
		if(pathNodesId!=null){
			filterNodes(node,pathNodesId,searchLevel);
		}
	});
}
Ext.onReady(function() {
	// collapse menu
	document.onmousedown = function() {
		if(window!=top)top.Ext.menu.MenuMgr.hideAll();
	};
	Ext.Msg = top.Ext.Msg;
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	Ext.QuickTips.init();
	
	initData();
	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [treePanel]
	});
});
