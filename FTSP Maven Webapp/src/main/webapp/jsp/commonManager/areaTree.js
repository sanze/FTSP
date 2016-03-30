Ext.BLANK_IMAGE_URL = "data:image/gif;base64,R0lGODlhAQABAID/AMDAwAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==";
Ext.namespace("Ext.ux");
/**
 * 区域树 对象模型
 * 主要配置属性：
 * checkModel： 	可选："single"(单选模式),"multiple"(同层多选模式),"path"(同层多选模式)
 * maxLevel： 	显示的层级数，范围[1, 12]
 * 
 */
Ext.ux.AreaTree = Ext.extend(Ext.tree.TreePanel, {
        animate : true,
        enableDD : false,
        border : false,
        rootVisible : true,
        autoScroll : true,
//        height : 500,
        /**
         * 可配置选项
         * 取值范围：[1, 12]
         * 当<=10时，不出现局站
         * >10时，出现局站
         */
        maxLevel:12,
        maxAreaLevel:10,
        /**
         * 可配置选项
         * ="single"(单选模式)
         * ="multiple"(同层多选模式)
         * ="path"(不同路径多选模式)
         */
        checkModel : "single",
        singleSelection:false,
        forceSameLevel:false,
        curLevel:0,
        selCount:0,
        selectedNode:null,
        loader : null,
        uiProvider : Ext.ux.TreeCheckNodeUI,
        /**
         * 初始化函数
         */
        initComponent : function () {
        	//添加根节点
            this.root = new Ext.tree.AsyncTreeNode({
                id : "0-0-0",
                text : top.FieldNameDefine.AREA_ROOT_NAME,
                uiProvider : Ext.ux.TreeCheckNodeUI,
                checked:"none"
            });
            //根据模式的不同决定是否强制同层选择
            this.forceSameLevel = (this.checkModel == "multiple");
            var loader = new Ext.tree.TreeLoader({
                url : "area!getSubArea.action",
                baseAttrs : {
                	uiProvider : Ext.ux.TreeCheckNodeUI,
    				checked: "none"
                },
                baseParams:{
                    maxLevel:this.maxLevel
                },
            	createNode : function (attr) {
            		//创建节点的时候，根据节点ID 抽取层级数据 并设置图标
            		var nodeIconCls=["","area-level1",
            		                 "area-level2",
            		                 "area-level3",
            		                 "area-level4",
            		                 "area-level4",
            		                 "area-level4",
            		                 "area-level4",
            		                 "area-level4",
            		                 "area-level4",
            		                 "area-level4",
            		                 "area-station",
            		                 "area-room"];
            		if (this.baseAttrs) {
            			Ext.applyIf(attr, this.baseAttrs);
            		}
                    var lvl = attr.id.split("-")[1]>>0;
                    attr.iconCls = nodeIconCls[lvl];
                    return Ext.tree.TreeLoader.prototype.createNode.call(this, attr);
            	},
            	listeners: {
            		/**
            		 * load事件在某节点node加载完子节点之后触发
            		 * @param loader TreeLoader
            		 * @param node 加载完子节点的node
            		 * @param response 加载时的返回数据
            		 */
            	    "load": function(loader, prtNode, response) {
            	    	//console.log(node.id);
            	    	var id = prtNode.id.split("-")[0] >> 0;
            	    	var level = prtNode.id.split("-")[1] >> 0;

                        var childCnt = prtNode.childNodes ? prtNode.childNodes.length : 0;
                        for(var i = 0; i < childCnt; i++){
                        	var nod = prtNode.childNodes[i];
                	    	var clvl = nod.id.split("-")[1]>>0;
                        	//如果是展开的子节点是一级区域，则子节点的parentPath设置成""
							if(clvl == 1){
								nod.attributes["parentPath"] = "";
								nod.attributes["fullPath"] = nod.attributes["text"];// "(" + Ext.ux.AreaTree.LevelName[clvl] + ")";
								
							}else{
								//
								nod.attributes["parentPath"] = prtNode.attributes["fullPath"];
								nod.attributes["fullPath"] = nod.attributes["parentPath"] 
									+  nod.attributes["text"];
								//+ "(" + Ext.ux.AreaTree.LevelName[clvl] + ")";
								//if(clvl>Ext.ux.AreaTree.maxAreaLevel){
								//	nod.attributes["fullPath"] += "(" + Ext.ux.AreaTree.LevelName[clvl] + ")";
								//}
								
							}
                        }
            	    }
            	}
            });
            this.loader = loader;
            Ext.ux.AreaTree.superclass.initComponent.call(this);
            this.root.expand();
        },
        listeners : {
        	checkchange:function(nod,checkState){
        		var check = (checkState=="all");
    			if(this.checkModel == "multiple"){
//        				console.log("force Same Level");
    				//如果强制选同级
    				if(check){
    					//如果是 勾选 操作
    					var lvl = nod.id.split("-")[1]>>0;
    					if(!this.selCount){
    						//如果 之前没有选中节点
    						this.selCount++;	//计数++
    						this.curLevel = lvl;//定级
    					}else{
    						if(lvl == this.curLevel){
    							this.selCount++;
    						}else{
    							//勾选的不是同一级，则取消
    							nod.attributes.checked = "none";
    			        		nod.ui.setNodeIcon(nod);
    						}
    					}
    				}else{
    					//如果是 取消勾选 操作
    					this.selCount--;
    				}
    			}else if(this.checkModel == "path"){
					//如果是 路径勾选模式
    				//首先记录当前勾选节点的路径
					var curPath = nod.getPath("text");
//					console.log("cur path = " + curPath);
					//获取所有的勾选节点
					var nodes = this.getChecked();
					var hasConflict = false;
					for ( var i = 0; i < nodes.length; i++) {
						var node = nodes[i];
						//排除当前选择的节点
						if(node.id != nod.id){
							var nodePath = node.getPath("text");
//							console.log(String.format("#{0} - {1}", i+1, nodePath));
							//判断路径是否冲突
							hasConflict = (nodePath.length > curPath.length) ?
									(nodePath.indexOf(curPath) == 0) : 
										(curPath.indexOf(nodePath) == 0);
							if(hasConflict){
								//如果有冲突，即勾选的是同一路径上的，则取消之前的选择
								node.attributes.checked = "none";
								node.ui.setNodeIcon(node);
							}
						}
					}
				}
            },
	        click : function (n, e) {
	        }
        },
        getSelectedNodes:function(){
        	var rv = [];
        	var nodes = this.getChecked();
        	for(var i=0,len = nodes.length;i<len;i++){
        		var nodeId = nodes[i].id;
        		var node =  {
        			text:nodes[i].text,
        			id:nodeId.split("-")[0]>>0,
        			level:nodeId.split("-")[1]>>0,
        			parentId:nodeId.split("-")[2]>>0,
        			parentPath:nodes[i].attributes["parentPath"] || "",
        			fullPath:nodes[i].attributes["fullPath"] || "",
        			node:nodes[i]
    			};
        		rv.push(node);
        	}
            return {
                total:rv.length,
                nodes:rv
            };
        },
        getChecked : function(attr){
        	startNode =  this.root; 
        	var r = [];     
        	var f = function(){
        		if(this.attributes.checked == "all"){   
        			r.push(!attr ? this : (attr == 'id' ? this.id : this.attributes[attr]));
    			}   
    		};     
    		startNode.cascade(f);   
    		return r;   
		},
		/**
		 * 获取级别名称
		 * @param lvl 级别
		 * @returns 不同级别对应的名称
		 */
        getLevelName : function(lvl){
        	if(lvl < 0 || lvl >this.maxLevel) return "PARAM_ERROR";
    		return Ext.ux.AreaTree.LevelName[lvl];   
		},
		/**
		 * 获取级别名称
		 * @param lvlNames 级别名称，数组，四个元素
		 * @returns 不同级别对应的名称
		 */
        setLevelName : function(lvlNames){
        	if(Ext.isArray(lvlNames)){
        		if(lvlNames.length == 4){
        			
        		}
        	}
		},
		/**
		 * 根据节点名字查找节点
		 * @param nodeText 节点名字
		 * @param attr 如果需要只显示某个属性信息，在此设置
		 * @returns {Array}
		 */
        getNodeByName : function(nodeText, attr){
        	startNode =  this.root; 
        	var r = [];     
        	var f = function(){
        		if(this.text == nodeText){   
        			r.push(!attr ? this : (attr == 'id' ? this.id : this.attributes[attr]));
    			}   
    		};
    		startNode.cascade(f);   
    		return r;   
		},
		/**
		 * 重新加载指定节点
		 * @param nodeID 指定节点的ID xxx-yyy-zzz形式
		 */
		reloadNode : function(nodeID){
//			console.log(String.format("Searching {0}……", nodeID));
			startNode =  this.root;
        	var f = function(){
//        		console.log(this.id);
        		if(this.id == nodeID){   
//        			console.log("Found!");
        			this.reload();
    			}   
    		};     
    		startNode.cascade(f);
		}
    });

Ext.reg("area", Ext.ux.AreaTree);
Ext.ux.AreaTree.LevelName=[top.FieldNameDefine.AREA_ROOT_NAME, 
  /* "一级"+*/top.FieldNameDefine.AREA_NAME, 
  /* "二级"+*/top.FieldNameDefine.AREA_NAME, 
  /* "三级"+*/top.FieldNameDefine.AREA_NAME, 
  /* "四级"+*/top.FieldNameDefine.AREA_NAME, 
  /* "五级"+*/top.FieldNameDefine.AREA_NAME, 
  /* "六级"+*/top.FieldNameDefine.AREA_NAME, 
   /*"七级"+*/top.FieldNameDefine.AREA_NAME, 
 /*  "八级"+*/top.FieldNameDefine.AREA_NAME, 
   /*"九级"+*/top.FieldNameDefine.AREA_NAME, 
 /*  "十级"+*/top.FieldNameDefine.AREA_NAME, 
   top.FieldNameDefine.STATION_NAME,
   "机房"];
/**
 * 直接获取级别属性
 */
(function getProperty() {
	Ext.Ajax.request({
		url : "area!getAreaProperty.action",
		method : "POST",
		success : function(response) {
//			console.dir(response);
			var obj = Ext.decode(response.responseText);
//			console.dir(obj);
			// nodes.nodes[0].node.parentNode.reload();
			if (!!obj) {
				//Ext.ux.AreaTree.LevelName=obj;
				Ext.ux.AreaTree.maxAreaLevel=obj.length-2;
//				for(var i = 1; i<obj.length; i++){
//					Ext.ux.AreaTree.LevelName[i] = obj["descLevel"+i];
//				}
			} else {
				Ext.Msg.alert('提示', '获取级别名称失败！');
			}
		}
	});
})();

/**
 * @class Ext.ux.AreaSelector
 * @extends Ext.form.TextField
 * 创建一个区域选择器.
 * @xtype fileuploadfield
 */
Ext.ux.AreaSelector = Ext.extend(Ext.form.TextField, {
    /**
     * @cfg {String} 选择按钮的文字 (默认值为：'...').  注意如果你使用了 {@link #buttonCfg}, 那么 buttonCfg.text 的值将会被使用
     */
    buttonText : '...',
    /**
     * @cfg {Boolean} 是否 只显示按钮
     */
    buttonOnly : false,
    /**
     * @cfg {Number} 按钮与文本框的间距
     */
    buttonOffset : 3,
    /**
     * @cfg {Object} buttonCfg 标准 {@link Ext.Button} 控件的配置对象
     */
    // private
    readOnly : true,
    privilege : "",
    targetLevel:20,
    checkModel : "single",
    targetControl:true,
    rawValue:null,
    winWidth:300,
    winHeight:450,
    /**
     * @hide
     * @method autoSize
     */
    autoSize : Ext.emptyFn,
    // private
    initComponent : function () {
    	this.rawValue = {};
        Ext.ux.AreaSelector.superclass.initComponent.call(this);
        this.addEvents('areaselected');
    },
    // private
    onRender : function (ct, position) {
        Ext.ux.AreaSelector.superclass.onRender.call(this, ct, position);
        this.wrap = this.el.wrap({
                cls : 'x-form-field-wrap x-form-file-wrap'
            });
        this.el.addClass('x-form-file-text');
//        this.el.applyStyles(this.style);
        if(!!this.buttonCfg && !!this.buttonCfg.iconCls){
        	this.buttonText = "";
        }
        var THIS = this;
        var btnCfg = Ext.applyIf(this.buttonCfg || {}, {
                text : this.buttonText,
                privilege : this.privilege,
                scope:THIS
            });
        var maxLevel = this.targetLevel;
        var w = this.winWidth;
        var h = this.winHeight;
//        console.log(maxLevel);
        this.button = new Ext.Button(Ext.apply(btnCfg, {
                    renderTo : this.wrap,
                    cls : 'x-form-file-btn' + (btnCfg.iconCls ? ' x-btn-icon' : '')
                }));
        this.button.on({
            scope:this,
            click : function () {
                var locator = new Ext.ux.AreaTree({
                     xtype:"area",
                     id:"areaFinder",
                     maxLevel:maxLevel,
                     checkModel: this.checkModel?this.checkModel:'single'
                });
                this.rawValue = {};
                var areaNames = Ext.ux.AreaTree.LevelName;
                var win = new Ext.Window({
                        title: '选择' + areaNames[locator.maxLevel],
                        id : 'selAreaWin71696ND1495626547494',
                        layout : 'fit',
                        modal : true,
                        closable:true,
                        plain:true,
                        closeAction:'close',
                        width: w,
                        height: h,
                        items : [locator],
                        buttons: [{
                        	scope:this,
                            text: '确定',
                            handler: function(){
                                //console.log(this);
                                var nodes = locator.getSelectedNodes();
                                var names = Ext.ux.AreaTree.LevelName;
                                if(nodes.total == 0){
                                    //没选中
                                    Ext.Msg.alert('提示', '请选择' + names[locator.maxLevel] + '！');
                                }else if(this.targetControl&& locator.maxLevel!=20 && nodes.nodes[0].level != locator.maxLevel){
                                    Ext.Msg.alert('提示', '请选择' + names[locator.maxLevel] + '！');
                                }else{
                                    this.rawValue = nodes.nodes[0];
                                    this.setValue(nodes.nodes[0].text);
                                    win.close();
                                }
                            }
                        },{
                            text: '取消',
                            handler: function(){
//                            	console.log("asdasdasd");
                                win.close();
                            }
                        }],
                        buttonAlign:"center"
                    });
                 win.show();
            }
        });
        if (this.buttonOnly) {
            this.el.hide();
            this.wrap.setWidth(this.button.getEl().getWidth());
        }

        this.resizeEl = this.positionEl = this.wrap;
    },

    reset : function () {
        this.rawValue = {};
        if (this.rendered) {
        }
        Ext.ux.AreaSelector.superclass.reset.call(this);
    },
    // private
    onResize : function (w, h) {
        Ext.ux.AreaSelector.superclass.onResize.call(this, w, h);

        this.wrap.setWidth(w);

        if (!this.buttonOnly) {
            var w = this.wrap.getWidth() - this.button.getEl().getWidth() - this.buttonOffset;
            this.el.setWidth(w);
        }
    },

    // private
    onDestroy : function () {
        Ext.ux.AreaSelector.superclass.onDestroy.call(this);
        Ext.destroy(this.fileInput, this.button, this.wrap);
    },

    onDisable : function () {
        Ext.ux.AreaSelector.superclass.onDisable.call(this);
        this.doDisable(true);
    },

    onEnable : function () {
        Ext.ux.AreaSelector.superclass.onEnable.call(this);
        this.doDisable(false);

    },
    // private
    doDisable : function (disabled) {
        this.button.setDisabled(disabled);
    },

    // private
    preFocus : Ext.emptyFn,

    // private
    alignErrorIcon : function () {
        this.errorIcon.alignTo(this.wrap, 'tl-tr', [2, 0]);
    },
    getRawValue:function(){
    	return this.rawValue;
    }
});
Ext.reg('areaselector', Ext.ux.AreaSelector);


Ext.ux.AreaField = Ext.extend(Ext.form.TriggerField, {

    initComponent : function() {
        Ext.ux.AreaField.superclass.initComponent.call(this);
        if (!this.locator) {
            this.locator = new Ext.ux.AreaTree({
                     xtype:"area",
                     id:"areaLocator",
                     maxLevel:maxLevel
                });
        }
        if (!this.win) {
            this.win = new Ext.Panel({
                        height : 200,
                        border : false,
                        autoScroll : true,
                        items : [this.treePanel]
                    })
        }
    },
    menuEvents : function(method) {
        this.treePanel[method]('click', this.onSelect, this);//给树添加单击事件
        this.menu[method]('hide', this.onMenuHide, this);
        this.menu[method]('show', this.onFocus, this);
    },
    onSelect : function(node, e) {//单击树节点赋值
        this.setValue(node.id);
        this.menu.hide();
    },
    onMenuHide : function() {
        this.focus(false, 60);
        this.menuEvents('un');
    },
    onTriggerClick : function() {
        if (!this.menu) {
            this.menu = new Ext.menu.Menu({
                        hideOnClick : false,
                        focusOnSelect : false,
                        items : [this.panel]
                    });
        }
        this.onFocus();
        this.menu.show(this.el, 'tl-bl?');
        this.menuEvents('on');
    }
});
Ext.reg('areafield', Ext.ux.AreaField);

function getLevelIndex(level){
	return level>Ext.ux.AreaTree.maxAreaLevel?level-10+Ext.ux.AreaTree.maxAreaLevel:level;
}