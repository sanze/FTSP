Ext.onReady(function() {  

	 var treeIdPanel=new Ext.Panel({  
	        title:'展示tree的panel',  
	        html:'<div id="tree"></div>'  
	    });  
	
     var tree = new Ext.tree.TreePanel({  
                 el : 'tree',  
                 loader : new Ext.tree.TreeLoader()  
             });  

     var root = new Ext.tree.AsyncTreeNode({  
                text : '我是根',  
                 children : [  
                 {  
                             text : '01',  
                             qtip : '我是鼠标提示', //必须通过Ext.QuickTips.init();初始化  
                             children : [  
                             {  
                                         text : '01-01',  
                                         leaf : true 
                                     },  
                                     {  
                                         text : '01-02',  
                                         children : [  
                                         {  
                                                     text : '01-02-01',  
                                                     leaf : true 
                                                 },  
                                                 {  
                                                     text : '01-02-02',  
                                                     leaf : true 
                                                 }  
                                         ]  
                                     },  
                                     {  
                                         text : '01-03',  
                                         leaf : true,  
                                         href : 'http://www.g.cn',  
                                         hrefTarget : '_blank' 
                                     } //使用结点连接  
                             ]  

                         },  
                         {  
                             text : '02',  
                             leaf : true,  
                             icon : 'user_female.png' 
                         } //自定义结点图标  
                 ]  

             });  

     tree.setRootNode(root);  
     tree.render();  
     tree.on("dblclick", function(node) {  
                 Ext.Msg.alert("你双击的是", node.text);  
             });  

     //右键菜单  
     var contextmenu = new Ext.menu.Menu({  
                id : 'theContextMenu',  
                 items : [{  
                     text : '选定',  
                     handler : function() {  
                         alert('你选择的是=' 
                                 + tree.getSelectionModel().getSelectedNode().text  

                                 + " 结点");  
                     }  
                 }]  
             });  

     //右键菜单的显示  
     tree.on("contextmenu", function(node, e) {  
                 e.preventDefault();  
                 node.select();//结点进入选择状态  
                 contextmenu.showAt(e.getXY());  
             });  

     //定义单击事件  
     tree.on("click", function(node) {  
                 Ext.Msg.show({  
                             title : '提示',  
                            msg : "你单击了" + node,  
                             animEl : node.ui.textNode  
                         });  
             });  
     root.expand();  
     Ext.QuickTips.init(); //初始化接点提示  
     tree.expandAll();//展开所有结点  

 }); 
