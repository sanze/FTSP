/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
Ext.app.MenuLoader = Ext.extend(Ext.ux.tree.XmlTreeLoader, {
    processAttributes : function(attr){
    	if(attr.tagName == 'menu'){
    		attr.text = attr.name;
//    		attr.iconCls = 'author-' + attr.gender;
    		attr.loaded = true;
            attr.expanded = true;
    	}
        else if(attr.tagName == 'submenu'){
            attr.text = attr.name;
//            attr.iconCls = 'author-' + attr.gender;
            attr.loaded = true;
            attr.expanded = true;
        }
        else if(attr.tagName == 'leaf'){
            attr.text = attr.name;
            attr.href = attr.url;
//            attr.iconCls = 'book';
            attr.leaf = true;
//            attr.disabled = true;
        }
    }
});
 
Ext.onReady(function(){
	var westPanel = new Ext.tree.TreePanel({   
    	id: 'westPanel',
        region: 'center',
        layout:'fit',
//        renderTo:tree,
        frame:false,
        autoScroll: true,
//        useArrows:true,
        rootVisible: false,
        forceFit:true,
        
        root: new Ext.tree.AsyncTreeNode(),

        // Our custom TreeLoader:
        loader: new Ext.app.MenuLoader({
            dataUrl:'../../resource/xml/'+menuStr
        }),
        listeners: {
            'click': function(node,e){
            	e.stopEvent();
            	if(node.attributes.leaf){
            		var tempNode = node;
            		var menuPath = tempNode.attributes.text;
            		while(tempNode.getDepth()>1){
            			menuPath = tempNode.parentNode.attributes.text + ">>"+ menuPath;
            			tempNode = tempNode.parentNode;
            		}
//            		parent.changeMenuPath(menuPath);
					parent.addTabPage(node.attributes.href,node.attributes.text);
//					window.open(node.attributes.href,"center");
            	}
            }
        }
    });
	
    var win = new Ext.Viewport({
        id:'win',
		title:"菜单",
		layout : 'border',
		items : [westPanel],
		renderTo : Ext.getBody()
	});
});