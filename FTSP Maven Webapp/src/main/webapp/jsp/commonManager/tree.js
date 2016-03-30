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
Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";

var SEARCH_LEVELS=["根","网管分组","网管","子网","网元"/*,"机架-子架","槽道-单元","子槽道-子单元","端口"*/];
var MaxSearchLevel=leafType>NodeDefine.NE?NodeDefine.NE:leafType;
var queryLevel=MaxSearchLevel;
var searchLevel=MaxSearchLevel;
var gKey = null;
var pathNodesId = null;
var FAILED = 0;
var NOTICE_TEXT = "提示";
var NONE=0;
var PATH=1;
var TARGET=2;


var nodeReset = null;
function resetNode(node){// 加载失败时使用,重置节点为未加载状态
  node.loaded=false;
  node.attributes.expandable=true;
  node.collapse();
}
var rootAttr={
	id : (rootType+'-'+rootId),
	text : rootText,
	nodeId : rootId,
	nodeLevel : rootType,
	uiProvider : rootVisible?Ext.ux.TreeCheckNodeUI:null,
	expanded: true,
	disabled: true
};
function constructNodeAttr(attr,loader){
	if (loader.baseAttrs) {
		Ext.applyIf(attr, loader.baseAttrs);
	}
	if (loader.applyLoader !== false && !attr.loader) {
		attr.loader = loader;
	}
	if (Ext.isString(attr.uiProvider)) {
		attr.uiProvider = loader.uiProviders[attr.uiProvider] || eval(attr.uiProvider);
	}
	// 设置层级图标
	if(!Ext.isEmpty(nodeIconCls)&&Ext.isNumber(attr['nodeLevel'])&&(attr['nodeLevel']<nodeIconCls.length)){
		attr.iconCls=nodeIconCls[attr.nodeLevel];
	}
	return attr;
}
var rootNode = new Ext.tree.TreeNode({hidden:true});
var treeLoader = new Ext.tree.TreeLoader({
	url : "tree!getChildNodes.action",
	baseAttrs: {uiProvider: Ext.ux.TreeCheckNodeUI,
				draggable: false,
				checked: "none"
				},
    nodeParameter: null,
	clearOnLoad: true,
	createNode : function (attr) {
		attr=constructNodeAttr(attr,this);
		if (attr.nodeType) {
			return new Ext.tree.TreePanel.nodeTypes[attr.nodeType](attr);
		} else {
			return attr.leaf ? new Ext.tree.TreeNode(attr) : new Ext.tree.AsyncTreeNode(attr);
		}
	},
	listeners: {
		"beforeload": function(loader, node) {
	        loader.baseParams={
	        	nodeId: node.attributes['nodeId'],
	        	nodeLevel: node.attributes['nodeLevel'],
	        	endLevel: leafType
	        };
	    },
	    "load": function(loader, node, response) {
	    	var result = Ext.decode(response.responseText);
	    	if(result&&(FAILED==result.returnResult)){
	           Ext.Msg.alert(NOTICE_TEXT,result.returnMessage);
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
            //从父节点继承neId属性
            for(var i=0;i<childCnt;i++){
              if(node.childNodes[i].attributes["nodeLevel"]==NodeDefine.NE){
                node.childNodes[i].attributes['neId']=node.childNodes[i].attributes['nodeId'];
              }else if(node.childNodes[i].attributes["nodeLevel"]>NodeDefine.NE){
                node.childNodes[i].attributes['neId']=node.attributes['neId'];
              }
            }
	    	/*var attr=node.id.split('-');// 叶层有嵌套情况设置叶节点
	    	var nodeType=attr[1]*1;
	    	if((nodeType>=leafType)&&(!node.hasChildNodes())){
	    		var newnode=new Ext.tree.AsyncTreeNode({
								text : node.text,
								id : node.id,
								cls : node.attributes.cls,
								leaf : true,
								checked : node.attributes.checked,
								allowDrag : node.attributes.allowDrag,
								uiProvider : node.attributes.uiProvider
							});
				parentNode=node.parentNode;
	    		parentNode.replaceChild(newnode,node);
	    		parentNode.expand();
	    	}*/
	    },
	    "loadexception": function(loader, node, response) {
	       Ext.Msg.alert(NOTICE_TEXT,"节点加载失败!"+
					"<BR>Status:"+response.statusText||"unknow");
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
    ]),
    listeners:{
    	"exception": function(proxy,type,action,options,response,arg){
    		Ext.Msg.alert(NOTICE_TEXT,"模糊搜索出错"+
					"<BR>Status:"+response.statusText||"unknow");
    	}
    }
});

var treeToolBar = new Ext.Toolbar({items:["-"]});
if((rootType+2<=SEARCH_LEVELS.length)&&(leafType>rootType)){
  if((rootType+2<=SEARCH_LEVELS.length)&&(leafType>rootType)){
    /*treeToolBar.add("网元搜索：");
    treeToolBar.add(new Ext.form.ComboBox({
        width : 140,
        minListWidth: 220,
        store: searchStore,
        valueField: 'nodeId',
        displayField: 'text',
        emptyText : '输入网元名',
        listEmptyText: '未找到匹配的结果',
        loadingText: '搜索中...',
        mode:'local', 
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
            gKey=record.get('text');
            if(gKey === null || gKey===""){
              pathNodesId = null;
              unFilterNodes(rootNode);
              return;
            }
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
              getAllPathNodes(gKey,searchLevel,rootId,rootType);
            }
          },
          beforequery:function(queryEvent){
            if(queryEvent.combo.lastQuery!=queryEvent.combo.getRawValue()){
              queryEvent.combo.lastQuery=queryEvent.combo.getRawValue();
              //gKey = queryEvent.query;
              queryNodes(queryEvent.combo.getRawValue(),searchLevel,rootId,rootType);
            }
          },
          scope : this
        }
      }));*/
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
        valueField: 'text',
        displayField: 'text',
        emptyText : '输入对象名',
        listEmptyText: '未找到匹配的结果',
        loadingText: '搜索中...',
        mode:'remote', 
        //minChars:1,  //输入几个字符开始搜索
        pageSize:searchStore.pageSize,
        queryDelay: 500,
        selectOnFocus: true,// 获得焦点时选中所有已输入文本
        //hideTrigger : true,
        typeAhead: false,
        autoSelect:false,
        enableKeyEvents : true,
        autoScroll:false,
    	resizable: true,
        listeners : {
          beforeselect: function(combo,record,index){
            search(record.get('nodeId'));
            combo.collapse();
            return false;
          },
          keypress: function(field, event) {
            field.setValue(field.getRawValue());
            if(event.getKey()==event.ENTER){//输入回车后开始过滤节点树
              search(field.getValue());
            }
          },
          beforequery:function(queryEvent){
            if(queryEvent.combo.lastQuery!=queryEvent.combo.getRawValue().trim()){
              queryEvent.combo.lastQuery=queryEvent.combo.getRawValue().trim();
              queryNodes(queryEvent.combo,queryEvent.combo.getRawValue(),queryLevel,rootId,rootType);
            }
            queryEvent.combo.expand();
            return false;
          },
          scope : this
        }
      }));
      var searchButtom={
        text:'筛选',
        icon:'../../resource/images/btnImages/search.png',
		handler: function(b,e){
			search(Ext.getCmp("searchCombo").getValue());
		}
      };
      treeToolBar.add(new Ext.SplitButton({
    	tooltip:searchButtom.text,
        icon:searchButtom.icon,
        handler:searchButtom.handler,
  		menu: {
  			style: {
				overflow: 'visible'     // For the Combo popup
			},
			iconCls: 'bmenu',
			items: [searchButtom,{
				text:'全部显示', 
				handler: function(b,e){
					unFilterNodes(rootNode);
				}
			}]
  		}
  	  }));
  }
}
treeToolBar.add('->');
treeToolBar.add({
	tooltip: '刷新',
    icon:'../../resource/images/btnImages/refresh.png',
    handler : function(){
      refresh(rootNode);
    }
});
//若父页面定义了onGetChecked函数,则显示>>按钮并在点击是调用
if(getContainerFunction(containerId,"onGetChecked")!=Ext.emptyFn){
    treeToolBar.add({
      id: 'getChecked',
        icon:'../../resource/images/btnImages/rightarrow_grey.png',
        listeners: {
        	'disable': function(th){
        		th.setIcon('../../resource/images/btnImages/rightarrow_grey.png');
        	},
        	'enable': function(th){
        		th.setIcon('../../resource/images/btnImages/rightarrow_red.png');
        	}
        },
        disabled: true,
        tooltip: '添加勾选节点',
        handler : function(){
        	getContainerFunction(containerId,"onGetChecked")(getCheckedNodes);
        }
    });
}
var treePanel = new Ext.ux.TreeCheckPanel({
	region : "center",
	root : rootNode,
	rootVisible : rootVisible,
	autoScroll : true,
	animate : true,
	bodyStyle : 'padding:5px 5px 0',
	// enableDD:true,
	// containerScroll: true,
	checkModel : checkModel,//"multiple"|"single"|"cascade"
    // onlyLeafCheckable: onlyLeafCheckable,
	border : false,
	forceLayout : true,
	enableDD : false,
	loader : treeLoader,
	tbar : treeToolBar,
    selModel : new Ext.ux.TreeMultiSelectionModel(),//(checkModel=="single"?null:new Ext.ux.TreeMultiSelectionModel()),
    contextMenu: new Ext.menu.Menu({
        items: [{
            id: 'checked-node',
            text: '勾选',
            iconCls: "icon-checked-all"
        },{
            id: 'unchecked-node',
            text: '反选',
            iconCls: "icon-checked-none"
        }],
        listeners: {
          itemclick: function(item) {
            switch (item.id) {
              case 'checked-node':
                  sn=item.parentMenu.contextNodes;
                  for(var i=0;i<sn.length;i++){
                    if(sn[i].attributes.checked!="all"){
                      if(sn[i].ui.checkbox&&sn[i].hidden!==true){
                        sn[i].ui.onCheck();
                      }
                    }
                  }
                  break;
              case 'unchecked-node':
                  sn=item.parentMenu.contextNodes;
                  for(var i=0;i<sn.length;i++){
                    if(sn[i].attributes.checked!="none"){
                      if(sn[i].ui.checkbox&&sn[i].hidden!==true){
                        sn[i].ui.onCheck();
                      }
                    }
                  }
                  break;
            }
          }
        }
    }),
    listeners: {
        contextmenu: function(node, e) {
//          Register the context node with the menu so that a Menu Item's handler function can access
//          it via its parentMenu property.
        	if(node.disabled)
        		return;
        	var s = node.getOwnerTree().getSelectionModel();
            if(s.isSelected(node)!==true){
              node.select();
            }
            var c = node.getOwnerTree().contextMenu;
            c.contextNodes = s.getSelectedNodes?s.getSelectedNodes():[s.getSelectedNode()];
            c.showAt(e.getXY());
        },
        checkchange: function(node,checked){
        	var btn=Ext.getCmp('getChecked');
        	if(btn){
        		if(btn.disabled&&this.getChecked().length>0){
        			btn.enable();
        		}else if(!btn.disabled&&this.getChecked().length==0){
        			btn.disable();
        		}
        	}
        	getContainerFunction(containerId,"onCheckChange")
        	(node,checked);
        },
        beforeappend: function(tree, parent, node){
            return getContainerFunction(containerId,"filterBy")
        	(tree, parent, node);
        }
    }
});
/**
 *参数:
 *  nodes: 要勾选节点的id/id列表，String/Array<String>
 *  
 *代码实例:
 *	单节点:checkedNodes("4-1"});
 *	多节点:checkedNodes(["4-1","4-2"]);
 *特殊说明:
 *  若父页面也定义了相同名称的函数,则会自动映射到父页面中
 */
function checkNodes(ids){
	if(ids){
		if(!Ext.isArray(ids)){
			ids=[ids]
		}
		for(var i=0;i<ids.length;i++){
			var id = ids[i];
			if(!Ext.isString(id)) continue;
			var attrs = id.match(/\d+/g);
			if(attrs&&attrs.length==2){
				getAllPathNodes(parseInt(attrs[1]),attrs[0],rootId,rootType,showCheckedResult,true);
			}
		}
	}
}
function showCheckedResult(nodes){
	showResult(rootNode,nodes,NodeDefine.LEAFMAX,true,true);
}

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
                        result.push(this.getPath(pathAttr).substring(ownerTree.pathSeparator.length));
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
	searchStore.load();
}
function getAllPathNodes(gKey,searchLevel,rootId,rootLevel,callback,silent){
	if(!silent)treePanel.getEl().mask('搜索中...');
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
	    		if(!silent)Ext.Msg.alert(NOTICE_TEXT,result.returnMessage);
	    	}else{
	    		callback(result.rows);
			}
	    	if(!silent)treePanel.getEl().unmask();
		},
		failure : function(response) {
			if(!silent)treePanel.getEl().unmask();
			Ext.Msg.alert(NOTICE_TEXT, "过滤搜索出错"+
					"<BR>Status:"+response.statusText||"unknow");
		},
		error : function(response) {
			if(!silent)treePanel.getEl().unmask();
			Ext.Msg.alert(NOTICE_TEXT, "过滤搜索出错"+
					"<BR>Status:"+response.statusText||"unknow");
		}
	});
}
function search(key){
	gKey = key;
    if(gKey === null || Ext.isEmpty(gKey)){
      unFilterNodes(rootNode);
      return;
    }
    searchLevel=queryLevel;
    getAllPathNodes(gKey,searchLevel,rootId,rootType,showSearchResult);
}
function showSearchResult(nodes){
	pathNodesId = nodes;
	filterNodes(rootNode, pathNodesId, searchLevel);
	rootNode.collapseChildNodes(true);
	showResult(rootNode, pathNodesId, searchLevel);
}
// 树节点与搜索结果列表匹配
// 返回结果: 0-不匹配 1-路径节点 2-目标节点
// add by 庄洁亮
function isMatched(node,keys){
	if(!keys||!keys.length||keys.length<1)
		return NONE;
	for(var i in keys){
		var nodeId = node.attributes['nodeId'];
		var nodeLevel = node.attributes['nodeLevel'];
		if((nodeId==keys[i]['nodeId'])&&
			(nodeLevel==keys[i]['nodeLevel'])){
          if(keys[i]['checked']=="all")
            return TARGET;
          else
            return PATH;
        }
	}
	return NONE;
}
// 树节点过滤
// 根据搜索结果列表过滤节点
// mod by 庄洁亮
function filterNodes(startNode, keys, endLevel) {
	function filterIt(){
		var nodeLevel = this.attributes['nodeLevel'];
		//if(nodeLevel>endLevel)
		//	return false;
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
function showResult(startNode, keys, endLevel, showAll, checked) {
	function matchedChild(node,keys, endLevel){
		var matcheds=new Array();
		var child=node.firstChild;
		while(child){
			if((child.attributes['nodeLevel']<=endLevel)&&isMatched(child,keys)>0)
				matcheds.push(child);
			child=child.nextSibling;
		}
		return matcheds;
	}
	function expandNode(node){
		var matchedChilds=matchedChild(node,keys,endLevel);
		for(var i=0;i<matchedChilds.length;i++){
		  var child=matchedChilds[i];
		  if(isMatched(child,keys)==TARGET){//当前节点已是目标节点时取消继续展开
        	if(checked&&!child.disabled&&!child.hidden){
        		if(child.ui.checkbox){
        			if(child.attributes.checked!="all"){
        				child.ui.onCheck();
        			}
        		}
        	}
          }else
        	  child.expand(false,false,expandNode);
		  if(!showAll)//仅显示第一个结果
      		return false;
	    }
		if(matchedChilds.length==0)
			node.collapse();// 没有孩子节点匹配时收起
	}
	startNode.expand(false,false,expandNode);
}
/**
 * 取消过滤节点及其下属节点
 * @param startNode
 */
function unFilterNodes(startNode) {
	startNode = startNode||rootNode;
	function showIt(){
		if(this.hidden){
			this.ui.show();
			return false;
		}
	}
	function removeIt(){
		if(!Ext.isDefined(pathNodesId)) return false;
		if(this.getDepth()==0){
			pathNodesId=null;
			return false;
		}
		for(var i=0;i<pathNodesId.length;i++){
			if(pathNodesId[i]['id']==this.id)
				pathNodesId.remove(pathNodesId[i]);
		}
	}
	startNode.cascade(removeIt);
	startNode.cascade(showIt);
	//startNode.collapseChildNodes(true);
};
/**
 * 刷新指定节点并保持下层展开状态
 * @param node 节点id或TreeNode 可选,默认为rootNode
 */
function refresh(node){
	if(Ext.isString(node)){
		node=rootNode.getOwnerTree().getNodeById(node);
		if(!Ext.isDefined(node)) return false;
	}
	node=node||rootNode;
	unFilterNodes(node);
	var paths=new Array();
	function isChildExpanded(node){
		var cs = node.childNodes;
		for(var i = 0, len = cs.length; i < len; i++) {
			if(cs[i].isExpanded()) return true;
		}
		return false;
	}
	function expandPaths(node){
		if(node.isExpanded()){//已展开
			var hasChild=node.hasChildNodes();//无子节点
			if(!hasChild||!isChildExpanded(node)){//无子节点或无子节点展开
				var path=node.getPath();
				if(paths.indexOf(path)==-1)
					paths.push(path);
				return false;
			}
		}
	}
	node.cascade(expandPaths);
	node.reload();
	var tree=node.getOwnerTree();
	for(var i=0;i<paths.length;i++){
		tree.expandPath(paths[i]);
	}
}

if(getContainerFunction(containerId,"checkNodes")!=Ext.emptyFn){
	setContainerFunction(containerId,"checkNodes",checkNodes);
}
if(getContainerFunction(containerId,"getCheckedNodes")!=Ext.emptyFn){
	setContainerFunction(containerId,"getCheckedNodes",getCheckedNodes);
}

function setContainerFunction(containerId,fnName,fn){
	var seted=false;
	if(parent){
		var container;
		if(!Ext.isEmpty(containerId)){
			container=parent.Ext.getCmp(containerId);
		}
		
		if(container){
				container[fnName]=fn;
		}else{
			parent[fnName]=fn;
        }
	}
	return seted;
}
function getContainerFunction(containerId,fnName){
	var fn=Ext.emptyFn;
	if(parent){
		var container;
		if(!Ext.isEmpty(containerId)){
			container=parent.Ext.getCmp(containerId);
			if(container&&Ext.isFunction(container[fnName])){
				fn=container[fnName];
			}
		}else{
			if((fn==Ext.emptyFn)&&
					Ext.isFunction(parent[fnName])){
				fn=parent[fnName];
			}
        }
	}
	return fn;
}

function initData(){
	if(rootVisible){
		var params={nodeId:rootId,nodeLevel:rootType};
		Ext.Ajax.request({
			url : "tree!getNode.action",
			params : params,
			method : 'POST',
			success : function(response) {
				var result = Ext.util.JSON.decode(response.responseText);
				if(result&&(FAILED==result.returnResult))
	    			Ext.Msg.alert(NOTICE_TEXT,result.returnMessage);
	    		else{
	    			for(var attr in result){
	    				rootAttr[attr]=result[attr];
	    			}
	    			var tree=rootNode.getOwnerTree();
					rootNode=tree.getLoader().createNode(rootAttr);
					tree.setRootNode(rootNode);
				}
			},
			failure : function(response) {
				Ext.Msg.alert(NOTICE_TEXT, "初始化加载失败"+
						"<BR>Status:"+response.statusText||"unknow");
			},
			error : function(response) {
				Ext.Msg.alert(NOTICE_TEXT, "初始化加载失败"+
						"<BR>Status:"+response.statusText||"unknow");
			}
		});
	}else{
		var tree=rootNode.getOwnerTree();
		rootNode=tree.getLoader().createNode(rootAttr);
		tree.setRootNode(rootNode);
	}
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
	Ext.QuickTips.init();
	
	initData();
	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [treePanel]
	});

	if(getContainerFunction(containerId,"onReady")!=Ext.emptyFn){
		getContainerFunction(containerId,"onReady")(treePanel);
	}
});
