Ext.BLANK_IMAGE_URL = "data:image/gif;base64,R0lGODlhAQABAID/AMDAwAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==";
Ext.namespace("Ext.ux");
Ext.namespace("CommonDefine");

Ext.applyIf(CommonDefine, 
{
	NOTICE_TEXT : "提示",
	FAILED: 0,
	SUCCESS: 1,
	FALSE: 0,
	TRUE: 1,
	// 网元类型1.SDH 2.WDM 3.OTN 4.PTN 5.微波 6.FTTX 9 虚拟网元 99 未知
	NE_TYPE_SDH_FLAG : 1,
	NE_TYPE_WDM_FLAG : 2,
	NE_TYPE_OTN_FLAG : 3,
	NE_TYPE_PTN_FLAG : 4,
	NE_TYPE_MICROWAVE_FLAG : 5,
	NE_TYPE_FTTX_FLAG : 6,
	NE_TYPE_VIRTUAL_NE_FLAG : 9,
	NE_TYPE_UNKNOW_FLAG : 99,

	NameSeparator : '：',
	TASK_TARGET_TYPE:{
		EMSGROUP : 1,
		EMS : 2,
		SUBNET : 3,
		NE : 4,
		SHELF : 5,
		UNIT : 6,
		SUBUNIT : 7,
		PTP : 8,
		SDH_CTP : 12,
		OTN_CTP : 13,
		TRUNK_LINE : 10,
		MULTI_SEC : 11,
		SDH_CIRCUIT : 70,
		WDM_CIRCUIT : 71,
		ETH_CIRCUIT : 72
	},
	/** ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ 共通树部分 ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ **/
	TREE: {
		CHILD_MAX : 5000,
		ROOT_ID : 0,
		ROOT_TEXT : "FTSP",
		CHECKED_ALL : "all",
		CHECKED_PART : "part",
		CHECKED_NONE : "none",

		// 节点包含信息
		PROPERTY_NODE_ID : "nodeId",
		PROPERTY_NODE_LEVEL : "nodeLevel",
		PROPERTY_TEXT : "text",

		NODE: {
			ROOT : 0,
			EMSGROUP : 1,
			EMS : 2,
			SUBNET : 3,
			NE : 4,
			SHELF : 5,
			UNIT : 6,
			SUBUNIT : 7,
			PTP : 8,
			LEAFMAX : 8
		},
		nodeIconCls:["icon-root","icon-emsgroup","icon-ems","icon-subnet","icon-ne","icon-shelf","icon-unit","icon-subunit","icon-ptp"],
        SEARCH_LEVELS:["根","网管分组","网管","子网","网元"/*,"机架-子架","槽道-单元","子槽道-子单元","端口"*/]
	}
	/** _______________________________ 共通树部分 _______________________________ **/
});
Ext.applyIf(CommonDefine, 
{
	filterNE_SDH: function (tree, parent, node){
		if(node.attributes["nodeLevel"]==CommonDefine.TREE.NODE.NE&&
			node.attributes["additionalInfo"]&&
			node.attributes["additionalInfo"]["TYPE"]==CommonDefine.NE_TYPE_SDH_FLAG){
			return false;//不显示SDH网元
		}
	},
	filterNE_WDM: function (tree, parent, node){
		if(node.attributes["nodeLevel"]==CommonDefine.TREE.NODE.NE&&
			node.attributes["additionalInfo"]&&
			node.attributes["additionalInfo"]["TYPE"]==CommonDefine.NE_TYPE_WDM_FLAG){
			return false;//不显示WDM网元
		}
	}
});
/**
 * 设备树 对象模型
 * 主要配置属性：
 * rootId:			根节点对象id, 默认为0, 即ROOT_ID
 * rootType:		根节点层级, 默认为0, 即ROOT 层
 * rootVisible:		根节点可见,  false隐藏根节点, 默认为false.
 * leafType:		叶节点层级, 默认为8, 即LEAFMAX
 * checkModel： 		勾选模式, 范围["cascade","single","multiple"] <br>即: 级联/单选/多选, 默认为"multiple".
 * forceSameLevel:	是否只能勾选同级对象, 类型boolean, 默认为false.
 * checkableLevel:	可勾选级别, 类型Number/Array,默认为null(所有级别).
 */
Ext.ux.EquipTreePanel = Ext.extend(Ext.ux.TreeCheckPanel, {
	autoScroll : true,
	animate : true,
	//bodyStyle : 'padding:5px 5px 0',
	//border : false,
	forceLayout : true,
	enableDD : false,
	
	onCheckChange: Ext.emptyFn,
	filterBy: Ext.emptyFn,
	onGetChecked: Ext.emptyFn,
	selModel : new Ext.ux.TreeMultiSelectionModel(),
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
    setGetCheckedButton: function(){
    	var btn=this.topToolbar.getCheckedButton;
    	if(btn){
    		if(btn.disabled&&this.getChecked().length>0){
    			btn.enable();
    		}else if(!btn.disabled&&this.getChecked().length==0){
    			btn.disable();
    		}
    	}
    },
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
        refresh: function(){
        	this.setGetCheckedButton();
        },
        beforecheckchange: function(node,oval,nval){
        	if(nval!="all"){
        		return true;
        	}
        	var cLevel=node.attributes['nodeLevel'];
        	if(this.checkableLevel){
        		var _level=this.checkableLevel;
        		if(!Ext.isArray(_level)){
            		_level=[_level];
            	}
        		if(_level.indexOf(cLevel)==-1){
        			return false;
        		}
        	}
        	if(this.forceSameLevel&&this.checkModel=='multiple'){
        		var nodes=this.getChecked();
        		for(var i=0;i<nodes.length;i++){
        			if(cLevel!=nodes[i].attributes['nodeLevel']){
        				return false;
        			}
        		}
        	}
        },
        checkchange: function(node,checked){
        	this.setGetCheckedButton();
        	this.onCheckChange.createDelegate(this,[node,checked])();
        },
        beforeappend: function(tree, parent, node){
        	return tree.filterBy(tree, parent, node);
        }
    },
	nodeIconCls: CommonDefine.TREE.nodeIconCls,
	rootVisible: false,
	rootId: CommonDefine.TREE.ROOT_ID,
	rootType:CommonDefine.TREE.NODE.ROOT,
	leafType:CommonDefine.TREE.NODE.LEAFMAX,
	checkModel:'multiple',
    forceSameLevel:false,
    checkableLevel:null,
    loader : null,
    uiProvider : Ext.ux.TreeCheckNodeUI,
    idSeparator: '-',
	MaxSearchLevel: CommonDefine.TREE.NODE.NE,
	queryLevel: CommonDefine.TREE.NODE.NE,
	searchLevel: CommonDefine.TREE.NODE.NE,
	gKey : null,
	pathNodesId : null,
	NONE:0,
	PATH:1,
	TARGET:2,
	nodeReset : null,
	resetNode : function(node){// 加载失败时使用,重置节点为未加载状态
		node.loaded=false;
		node.attributes.expandable=true;
		node.collapse();
	},
	setRoot : function(id,level){
		if(id)this.rootId=id;
		if(level)this.rootType=level;
		var rootAttr={
			id : (this.rootType+this.idSeparator+this.rootId),
			nodeId : this.rootId,
			nodeLevel : this.rootType,
			uiProvider : this.rootVisible?this.uiProvider:Ext.tree.RootTreeNodeUI,
			expanded: true,
			disabled: true
		};
		var rootNode=this.loader.createNode.createDelegate(this.loader,[rootAttr])();
    	if(this.root&&this.root.rendered){
    		this.setRootNode(rootNode);
    		this.fireEvent('refresh',this);
    	}else
    		this.root=rootNode;
    	if(this.rootVisible){
    		var params={nodeId:this.rootId,nodeLevel:this.rootType};
    		Ext.Ajax.request({
    			scope : this,
    			url : "tree!getNode.action",
    			params : params,
    			method : 'POST',
    			success : function(response) {
    				var result = Ext.util.JSON.decode(response.responseText);
    				if(result&&(CommonDefine.FAILED==result.returnResult))
    	    			Ext.Msg.alert(CommonDefine.NOTICE_TEXT,result.returnMessage);
    	    		else{
    	    			for(var attr in result){
    	    				rootAttr[attr]=result[attr];
    	    			}
    	    			var tree=this;
    	    			var loader=tree.getLoader();
    					rootNode=loader.createNode.createDelegate(loader,[rootAttr])();
    					Ext.applyIf(this.root,rootNode);
    					this.root.setText(rootNode.text);
    					if(rootNode.disabled)
    						this.root.disable();
    					else
    						this.root.enable();
    				}
    			},
    			failure : function(response) {
    				Ext.Msg.alert(CommonDefine.NOTICE_TEXT, "初始化加载失败"+
    						"<BR>Status:"+response.statusText||"unknow");
    			},
    			error : function(response) {
    				Ext.Msg.alert(CommonDefine.NOTICE_TEXT, "初始化加载失败"+
    						"<BR>Status:"+response.statusText||"unknow");
    			}
    		});
    	}
	},
    /**
     * 初始化函数
     */
    initComponent : function () {
    	if((this.rootId==null)||(this.rootType==null)){
    		this.rootId=0;
    		this.rootType=0;
		}
    	this.rootVisible=(true==this.rootVisible);
		if(this.leafType==null){
			this.leafType=CommonDefine.TREE.NODE.LEAFMAX;
		}
		if(["cascade","single","multiple"].indexOf(this.checkModel)==-1){
			this.checkModel="cascade";
		}
        //根据模式的不同决定是否强制同层选择
        if(this.checkModel != "multiple"){
        	this.forceSameLevel=false;
        }
        if(this.leafType<this.MaxSearchLevel){
        	this.MaxSearchLevel=this.leafType;
        }
        this.queryLevel=this.MaxSearchLevel;
    	this.loader = new Ext.tree.TreeLoader({
    		url : "tree!getChildNodes.action",
    		baseAttrs: {
    			uiProvider: this.uiProvider,
				draggable: false,
				checked: "none"
			},
			endLevel: this.leafType,
			nodeIconCls: this.nodeIconCls,
    	    nodeParameter: null,
    		clearOnLoad: true,
    		createNode : function (attr) {
    			var constructNodeAttr=function(attr,loader){
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
    		    	var nodeIconCls=loader.nodeIconCls;
    		    	if(!Ext.isEmpty(nodeIconCls)&&Ext.isNumber(attr['nodeLevel'])&&(attr['nodeLevel']<nodeIconCls.length)){
    		    		attr.iconCls=nodeIconCls[attr.nodeLevel];
    		    	}
    		    	return attr;
    		    };
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
    		        	endLevel: loader.endLevel
    		        };
    		    },
    		    "load": function(loader, node, response) {
    		    	var result = Ext.decode(response.responseText);
    		    	if(result&&(CommonDefine.FAILED==result.returnResult)){
    		           Ext.Msg.alert(CommonDefine.NOTICE_TEXT,result.returnMessage);
    		           //nodeReset=node;
    		           //setTimeout("resetNode(nodeReset)",100);
    	               return;
    		    	}
    	        
    	            //从父节点继承emsId属性
    	            var childCnt=node.childNodes?node.childNodes.length:0;
    	            for(var i=0;i<childCnt;i++){
    	              if(node.childNodes[i].attributes["nodeLevel"]==CommonDefine.TREE.NODE.EMSGROUP){
    	                node.childNodes[i].attributes['emsGroupName']=node.childNodes[i].attributes['text'];
    	              }else if(node.childNodes[i].attributes["nodeLevel"]==CommonDefine.TREE.NODE.EMS){
    	                node.childNodes[i].attributes['emsId']=node.childNodes[i].attributes['nodeId'];
    	                node.childNodes[i].attributes['emsGroupName']=node.attributes['emsGroupName'];
    	                node.childNodes[i].attributes['emsName']=node.childNodes[i].attributes['text'];
    	              }else if(node.childNodes[i].attributes["nodeLevel"]>CommonDefine.TREE.NODE.EMS){
    	                node.childNodes[i].attributes['emsId']=node.attributes['emsId'];
    	                node.childNodes[i].attributes['emsGroupName']=node.attributes['emsGroupName'];
    	                node.childNodes[i].attributes['emsName']=node.attributes['emsName'];
    	              }
    	            }
    	            //从父节点继承neId属性
    	            for(var i=0;i<childCnt;i++){
    	              if(node.childNodes[i].attributes["nodeLevel"]==CommonDefine.TREE.NODE.NE){
    	                node.childNodes[i].attributes['neId']=node.childNodes[i].attributes['nodeId'];
    	              }else if(node.childNodes[i].attributes["nodeLevel"]>CommonDefine.TREE.NODE.NE){
    	                node.childNodes[i].attributes['neId']=node.attributes['neId'];
    	              }
    	            }
    		    },
    		    "loadexception": function(loader, node, response) {
    		       Ext.Msg.alert(CommonDefine.NOTICE_TEXT,"节点加载失败!"+
    						"<BR>Status:"+response.statusText||"unknow");
    	           //nodeReset=node;
    		       //setTimeout("resetNode(nodeReset)",100);
    		    }
    		}
    	});
    	this.setRoot(this.rootId,this.rootType);
        //网元combobox的store
        var searchStore = new Ext.data.Store(
        {
            proxy : new Ext.data.HttpProxy({
              url: 'tree!searchNodes.action',
              async: false
            }),
//        	paramNames: {start:'startNumber',limit:'pageSize'},
            pageSize:10,
        	reader: new Ext.data.JsonReader({
                totalProperty: 'total',
        		root : "rows"
            },[
        	    "nodeId","nodeLevel","text","parentId","parentLevel","parent"
            ]),
            listeners:{
            	"exception": function(proxy,type,action,options,response,arg){
            		Ext.Msg.alert(CommonDefine.NOTICE_TEXT,"模糊搜索出错"+
        					"<BR>Status:"+response.statusText||"unknow");
            	}
            }
        });
        var treeToolBar = new Ext.Toolbar({items:["-"]});
        var SEARCH_LEVELS=CommonDefine.TREE.SEARCH_LEVELS;
        var nodeIconCls=this.nodeIconCls;
        if((this.rootType+2<=SEARCH_LEVELS.length)&&(this.leafType>this.rootType)){
          if((this.rootType+2<=SEARCH_LEVELS.length)&&(this.leafType>this.rootType)){
            button = new Ext.Button({
              iconCls: nodeIconCls[this.queryLevel],
              tooltip: '对象类型',
              text: this.queryLevel<nodeIconCls.length?null:SEARCH_LEVELS[this.queryLevel]
            });
            if((this.rootType+3<=SEARCH_LEVELS.length)&&(this.leafType-this.rootType>1)){
              button.menu=new Ext.menu.Menu();
              endLv=(this.leafType<SEARCH_LEVELS.length)?this.leafType:(SEARCH_LEVELS.length-1);
              for(var lv=this.rootType+1;lv<=endLv;lv++){
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
                    var combo=b.parentMenu.findParentByType('toolbar').searchCombo;
                    combo.lastQuery=null;
                    combo.findParentByType('equiptreepanel').queryLevel=b.level;
                  }
                });
              }
            }
            treeToolBar.add(button);
            treeToolBar.searchLevelMenu=button;
            var searchCombo=new Ext.form.ComboBox({
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
            	queryNodes: function(combo,gKey,searchLevel,rootId,rootLevel){
            		Ext.apply(combo.getStore().baseParams,{
                		text: gKey,
                		nodeLevel:searchLevel,
                        endId: rootId,
                        endLevel: rootLevel,
                		hasPath:false,
                		limit: combo.pageSize
                	});
            		combo.getStore().load();
                },
                listeners : {
                  beforeselect: function(combo,record,index){
                	  var tree=combo.findParentByType('equiptreepanel');
                	  tree.search(record.get('nodeId'));
                	  combo.collapse();
                	  return false;
                  },
                  keypress: function(field, event) {
                    field.setValue(field.getRawValue());
                    if(event.getKey()==event.ENTER){//输入回车后开始过滤节点树
                    	var tree=field.findParentByType('equiptreepanel');
                  	  	tree.search(field.getValue());
                    }
                  },
                  beforequery:function(queryEvent){
                    if(queryEvent.combo.lastQuery!=queryEvent.combo.getRawValue().trim()){
                      queryEvent.combo.lastQuery=queryEvent.combo.getRawValue().trim();
                      var tree=queryEvent.combo.findParentByType('equiptreepanel');
                      var rootId=tree.rootId;
                      var rootType=tree.rootType;
                      var queryLevel=tree.queryLevel;
                      queryEvent.combo.queryNodes(queryEvent.combo,queryEvent.combo.getRawValue(),queryLevel,rootId,rootType);
                    }
                    queryEvent.combo.expand();
                    return false;
                  },
                  scope : this
                }
            });
            treeToolBar.add(searchCombo);
            treeToolBar.searchCombo=searchCombo;
            var searchMenuItem={
                text:'筛选',
                //icon:'../../resource/images/btnImages/search.png',
                iconCls:'icon-search',
        		handler: function(b,e){
        			var tree=b.findParentByType('equiptreepanel');
        			var combo=b.findParentByType('toolbar').searchCombo;
        			tree.search(combo.getValue());
        		}
            };
            var searchButtom=new Ext.SplitButton({
            	tooltip:searchMenuItem.text,
                //icon:searchMenuItem.icon,
            	iconCls:searchMenuItem.iconCls,
                handler:searchMenuItem.handler,
          		menu: {
          			style: {
        				overflow: 'visible'     // For the Combo popup
        			},
        			iconCls: 'bmenu',
        			items: [searchMenuItem,{
        				text:'全部显示', 
        				handler: function(b,e){
        					var tree=b.findParentByType('equiptreepanel');
        					var rootNode=tree.root;
        					tree.unFilterNodes(rootNode);
        				}
        			}]
          		}
          	});
            treeToolBar.add(searchButtom);
            treeToolBar.searchButtom=searchButtom;
          }
        }
        treeToolBar.add('->');
        treeToolBar.add({
        	tooltip: '刷新',
            //icon:'../../resource/images/btnImages/refresh.png',
        	iconCls:'icon-refresh',
            handler : function(b,e){
            	var tree=b.findParentByType('equiptreepanel');
            	var rootNode=tree.root;
            	tree.refresh(rootNode);
            }
        });
        //若父页面定义了onGetChecked函数,则显示>>按钮并在点击是调用
        if(this.onGetChecked!=Ext.emptyFn){
        	var getCheckedButton=new Ext.Button({
                //icon:'../../resource/images/btnImages/rightarrow_grey.png',
        		iconCls:'icon-rightarrow-grey',
        		listeners: {
                	'disable': function(th){
                		//th.setIcon('../../resource/images/btnImages/rightarrow_grey.png');
                		th.setIconClass('icon-rightarrow-grey');
                	},
                	'enable': function(th){
                		//th.setIcon('../../resource/images/btnImages/rightarrow_red.png');
                		th.setIconClass('icon-rightarrow-red');
                	}
                },
                disabled: true,
                tooltip: '添加勾选节点',
                handler : function(b,e){
					var tree=b.findParentByType('equiptreepanel');
					tree.onGetChecked.createDelegate(tree,[tree.getCheckedNodes.createDelegate(tree)])();
                }
            });
            treeToolBar.add(getCheckedButton);
        	treeToolBar.getCheckedButton=getCheckedButton;
        }
        this.tbar=treeToolBar;
        this.on('beforeexpandnode', function(node) {
    		if(this.pathNodesId!=null){
    			this.filterNodes(node,this.pathNodesId,this.searchLevel);
    		}
    	});
        Ext.ux.EquipTreePanel.superclass.initComponent.call(this);
    },
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
    checkNodes: function(ids){
    	if(ids){
    		if(!Ext.isArray(ids)){
    			ids=[ids]
    		}
    		for(var i=0;i<ids.length;i++){
    			var id = ids[i];
    			if(!Ext.isString(id)) continue;
    			var attrs = id.match(/\d+/g);
    			if(attrs&&attrs.length==2){
    				this.getAllPathNodes(parseInt(attrs[1]),attrs[0],this.rootId,this.rootType,this.showCheckedResult,true);
    			}
    		}
    	}
    },
    showCheckedResult:function(nodes){
    	this.showResult(this.root,nodes,CommonDefine.TREE.NODE.LEAFMAX,true,true);
    },

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
    getCheckedNodes:function(_attribute,_filter,_level,_checked) {
    	var result = [];
    	if(!_filter){ _filter="all";}
    	if(!_checked){ _checked="all";}
    	
    	if(!Ext.isArray(_checked)){
    		_checked=[_checked];
    	}
    	if(_level&&!Ext.isArray(_level)){
    		_level=[_level];
    	}
    	
    	var startNode = this.root;
    	function checkMatch(node){
    		if ((!node.hidden)&&(_checked.indexOf(node.attributes["checked"])!=-1)) {
        		if((!_level)||
        			(_level.indexOf(node.attributes["nodeLevel"])!=-1)){
        			if(node.getDepth()>0||node.getOwnerTree().rootVisible){
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
                            if(ownerTree.rootVisible){
                              attr[_attribute[index]]=this.getPath(pathAttr).substring(ownerTree.pathSeparator.length);
                            }else{
                            	var rootPathAttr=ownerTree.root.attributes[pathAttr];
                          	  	if(!Ext.isDefined(rootPathAttr))
                          		  rootPathAttr="";
                          	  	attr[_attribute[index]]=
                          	  		this.getPath(pathAttr).substring(
                                                         (ownerTree.pathSeparator+
                                                		 rootPathAttr+ownerTree.pathSeparator)
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
                          if(ownerTree.rootVisible){
                            result.push(this.getPath(pathAttr).substring(ownerTree.pathSeparator.length));
                          }else{
                        	  var rootPathAttr=ownerTree.root.attributes[pathAttr];
                        	  if(!Ext.isDefined(rootPathAttr))
                        		  rootPathAttr="";
                        	  result.push(this.getPath(pathAttr).substring(
                                                         (ownerTree.pathSeparator+
                                                		 rootPathAttr
                                                		 +ownerTree.pathSeparator)
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
    },

    getAllPathNodes:function(gKey,searchLevel,rootId,rootLevel,callback,silent){
    	if(!silent)this.getEl().mask('搜索中...');
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
    		scope: this,
    		url : path,
    		params: params,
    		method : 'POST',
    		success : function(response) {
    			var result = Ext.util.JSON.decode(response.responseText);
    	    	if(result&&(CommonDefine.FAILED==result.returnResult)){
    	    		if(!silent)Ext.Msg.alert(CommonDefine.NOTICE_TEXT,result.returnMessage);
    	    	}else{
    	    		callback.createDelegate(this,[result.rows])();
    			}
    	    	if(!silent)this.getEl().unmask();
    		},
    		failure : function(response) {
    			if(!silent)this.getEl().unmask();
    			Ext.Msg.alert(CommonDefine.NOTICE_TEXT, "过滤搜索出错"+
    					"<BR>Status:"+response.statusText||"unknow");
    		},
    		error : function(response) {
    			if(!silent)this.getEl().unmask();
    			Ext.Msg.alert(CommonDefine.NOTICE_TEXT, "过滤搜索出错"+
    					"<BR>Status:"+response.statusText||"unknow");
    		}
    	});
    },
    search:function(key){
    	this.gKey = key;
        if(this.gKey === null || Ext.isEmpty(this.gKey)){
          this.unFilterNodes(this.root);
          return;
        }
        this.searchLevel=this.queryLevel;
        this.getAllPathNodes(this.gKey,this.searchLevel,this.rootId,this.rootType,this.showSearchResult);
    },
    showSearchResult:function(nodes){
    	this.pathNodesId = nodes;
    	this.filterNodes(this.root, this.pathNodesId, this.searchLevel);
    	//this.root.collapseChildNodes(true);
    	this.showResult(this.root, this.pathNodesId, this.searchLevel);
    },
    // 树节点与搜索结果列表匹配
    // 返回结果: 0-不匹配 1-路径节点 2-目标节点
    // add by 庄洁亮
    isMatched:function(node,keys){
    	if(!keys||!keys.length||keys.length<1)
    		return this.NONE;
    	for(var i in keys){
    		var nodeId = node.attributes['nodeId'];
    		var nodeLevel = node.attributes['nodeLevel'];
    		if((nodeId==keys[i]['nodeId'])&&
    			(nodeLevel==keys[i]['nodeLevel'])){
              if(keys[i]['checked']=="all")
                return this.TARGET;
              else
                return this.PATH;
            }
    	}
    	return this.NONE;
    },
    // 树节点过滤
    // 根据搜索结果列表过滤节点
    // mod by 庄洁亮
    filterNodes:function(startNode, keys, endLevel) {
    	function filterIt(){
    		var nodeLevel = this.attributes['nodeLevel'];
    		//if(nodeLevel>endLevel)
    		//	return false;
    		var f = (this.getDepth()==0)||(this.getOwnerTree().isMatched(this,keys)>0);
    		if (!f) {
                function isParentTarget(node){//同级嵌套情况,判断父节点是否有目标节点
                  var nodeLevel = node.attributes['nodeLevel'];
                  var tree=node.getOwnerTree();
                  if(nodeLevel>=tree.searchLevel){
                    pathNode = node.parentNode;
                    pathLevel = pathNode.attributes['nodeLevel'];
                    if(pathLevel>=tree.searchLevel){
                      if(tree.isMatched(pathNode,keys)==2)//是目标节点
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
    },
    // 过滤结果显示
    // 展开显示首个结果节点
    // add by 庄洁亮
    showResult:function(startNode, keys, endLevel, showAll, checked) {
    	function matchedChild(node,keys, endLevel){
    		var matcheds=new Array();
    		var child=node.firstChild;
    		while(child){
    			if((child.attributes['nodeLevel']<=endLevel)&&node.getOwnerTree().isMatched(child,keys)>0)
    				matcheds.push(child);
    			child=child.nextSibling;
    		}
    		return matcheds;
    	}
    	function expandNode(node){
    		var matchedChilds=matchedChild(node,keys,endLevel);
    		for(var i=0;i<matchedChilds.length;i++){
    		  var child=matchedChilds[i];
    		  var tree=node.getOwnerTree();
    		  if(tree.isMatched(child,keys)==tree.TARGET){//当前节点已是目标节点时取消继续展开
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
    },
    /**
     * 取消过滤节点及其下属节点
     * @param startNode
     */
    unFilterNodes:function(startNode) {
    	startNode = startNode||this.root;
    	function showIt(){
    		if(this.hidden){
    			this.ui.show();
    			return false;
    		}
    	}
    	var tree=this;
    	function removeIt(){
    		if(!Ext.isDefined(tree.pathNodesId)) return false;
    		if(this.getDepth()==0){
    			tree.pathNodesId=null;
    			return false;
    		}
    		var pathNodesId=tree.pathNodesId;
    		for(var i=0;i<pathNodesId.length;i++){
    			if(pathNodesId[i]['id']==this.id)
    				tree.pathNodesId.remove(pathNodesId[i]);
    		}
    	}
    	startNode.cascade(removeIt);
    	startNode.cascade(showIt);
    	//startNode.collapseChildNodes(true);
    },
    /**
     * 刷新指定节点并保持下层展开状态
     * @param node 节点id或TreeNode 可选,默认为rootNode
     */
    refresh:function(node){
    	if(Ext.isString(node)){
    		node=this.getNodeById(node);
    		if(!Ext.isDefined(node)) return false;
    	}
    	node=node||this.root;
    	this.unFilterNodes(node);
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
    	this.fireEvent('refresh',this);
    }
});
Ext.reg("equiptreepanel", Ext.ux.EquipTreePanel);

Ext.ux.EquipTreeCombo = Ext.extend(Ext.form.TriggerField, {
	fieldLabel : '对象树',
	editable: false,
	separator: ", ",
	treeField: null,
	treeWindow: null,
	listWidth: 250,
	listHeight: 200,
	width: 150,
	attrFormat:"path:text",
	// private
    initComponent : function () {
    	//this.rawValue = {};
    	Ext.ux.EquipTreeCombo.superclass.initComponent.call(this);
        //this.addEvents('areaselected');
    	this.listWidth=this.listWidth?this.listWidth:this.width;
    },
	// private
    onRender : function (ct, position) {
    	Ext.ux.EquipTreeCombo.superclass.onRender.call(this, ct, position);
    	this.initTree();
//    	var pageX=this.getPosition()[0];
//		var pageY=this.getPosition()[1]+this.getHeight();
		if(this.treeWindow==null){
			this.treeWindow=new Ext.Window({
		        //title : title,
		        layout: 'fit',
				width : this.listWidth,
				height : this.listHeight,
//				pageX :pageX,
//				pageY :pageY+this.getHeight(),
		        isTopContainer : true,
		        renderTo: Ext.getBody(),
		        //modal : true,
		        autoScroll:true,
				maximized:false,
				closable: false,
				closeAction:'hide',
				items:[this.treeField]
		    });
		}
//		this.treeWindow.setPosition(pageX,pageY);
    },
    initTree : function (){
//    	var pageX=this.getPosition()[0];
//		var pageY=this.getPosition()[1];
    	var onCheckChange=function(tree){
    		var vals=this.treeField.getCheckedNodes.createDelegate(this.treeField,[this.attrFormat])();// TODO
    		var value=null;
    		if(vals){
    			value=vals.join(this.separator);
    			/*for(var idx=0;idx<vals.length;idx++){
    				value+=vals[idx]+this.separator;
    			}*/
    		}
    		this.setValue(value);
    	}.createDelegate(this);
		
		var iniConfig=this.initialConfig;
		iniConfig['id']=iniConfig['treeId'];
//		Ext.applyIf(config,iniConfig);
    	this.treeField=new Ext.ux.EquipTreePanel(iniConfig);
    	this.treeField.on('refresh',onCheckChange);
    	this.treeField.on('checkchange',onCheckChange);
    },
    reset : function(){
    	Ext.ux.EquipTreeCombo.superclass.reset.call(this);
    	this.treeField.refresh();
    },
    focus : function(){
    	Ext.ux.EquipTreeCombo.superclass.focus.call(this);
    },
    isExpanded: function(){
    	if(this.treeWindow){
    		return !this.treeWindow.hidden;
    	}
    	return false;
    },
    collapse : function(){
    	if(!this.isExpanded()){
    		return;
    	}
    	this.treeWindow.hide();
    	Ext.getDoc().un('mousewheel', this.collapseIf, this);
    	Ext.getDoc().un('mousedown', this.collapseIf, this);
    	this.fireEvent('collapse', this);
	},
	// private
	collapseIf : function(e){
		if(!this.isDestroyed && !e.within(this.wrap)){
			if(this.treeWindow&&!e.within(this.treeWindow.getEl())){
				var within=false;
				var arr=this.treeField.topToolbar.findByType('button');
				for(var i=0;i<arr.length;i++){
					if(arr[i].menu)
						within=within||e.within(arr[i].menu.getEl())
				}
				arr=this.treeField.topToolbar.findByType('combo');
				for(var i=0;i<arr.length;i++){
					within=within||e.within(arr[i].list)
				}
				within=within||e.within(this.treeField.contextMenu.getEl());
				if(!within){
					this.collapse();
				}
			}
		}
	},
	/**
	 * Expands the dropdown list if it is currently hidden. 
	 * Fires the {@link #expand} event on completion.
	 */
	expand : function(){
		if(this.isExpanded() || !this.hasFocus){
			return;
		}
		if(this.bufferSize){
			this.doResize(this.bufferSize);
			delete this.bufferSize;
		}
		var pageX=this.getPosition()[0];
		var pageY=this.getPosition()[1]+this.getHeight();
		if(this.treeWindow==null){
			this.treeWindow=new Ext.Window({
		        //title : title,
		        layout: 'fit',
				width : this.listWidth,
				height : this.listHeight,
//				pageX :pageX,
//				pageY :pageY+this.getHeight(),
		        isTopContainer : true,
		        //modal : true,
		        autoScroll:true,
				maximized:false,
				closable: false,
				closeAction:'hide',
				items:[this.treeField]
		    });
		}
		this.treeWindow.setPosition(pageX,pageY);
		this.treeWindow.show();
		
		//this.treeWindow.alignTo.apply(this.treeWindow, [this.el].concat(this.listAlign));
		
		this.mon(Ext.getDoc(), {
			scope: this,
			mousewheel: this.collapseIf,
			mousedown: this.collapseIf
		});
		this.fireEvent('expand', this);
	},
	onTriggerClick : function(){
		if(this.readOnly || this.disabled){
			return;
		}
		if(this.isExpanded()){
			this.collapse();
			this.el.focus();
		}else {
			this.onFocus({});
			this.expand();
			this.el.focus();
		}    
	},
	onDestroy : function(){
		Ext.destroy(
			this.treeWindow,
			this.treeField
		);
		Ext.ux.EquipTreeCombo.superclass.onDestroy.call(this);
	},
	getCheckedNodes : function(){
		if(this.treeField){
			return this.treeField.getCheckedNodes.createDelegate(this.treeField,arguments)();
		}
		return null;
	}
});
Ext.reg("equiptreecombo", Ext.ux.EquipTreeCombo);
Ext.QuickTips.init();