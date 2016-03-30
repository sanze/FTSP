  var Tbar = new Ext.Toolbar({
	border : false,
	items : [{
        text: '确认',
        icon : '',
        handler : function (){
			// 此处获取所有选择的节点
			var nodes =  tree.getNodeById('0');
			alert(nodes.id);
			
		} 
    },{
        text: '全选',
        icon : '',
    },{
        text: '反选',
        icon : '',
    },{
        text: '全不选',
        icon : '',
    }]
});
var Bbar = new Ext.Toolbar({
	border : false,
	items : [{
        text: '确认',
        icon : '',
    },{
        text: '全选',
        icon : '',
    },{
        text: '反选',
        icon : '',
    },{
        text: '全不选',
        icon : '',
    }]
});

var Tree = Ext.tree;   
var tree = new Tree.TreePanel({   
    el:'tree-div',   
    rootVisible:true,     //隐藏根节点    
    border:true,          //边框    
    animate:true,         //动画效果    
    autoScroll:true,      //自动滚动    
    enableDD:false,       //拖拽节点                 
    containerScroll:true,  
    checkModel :true,//为true表示复选框
    onCheckModel :true,//为true表示复选框
    loader: new Tree.TreeLoader({   
    dataUrl:'demo-tree!getLowerTreeNOdes.action'   
    }),
    tbar :  Tbar,
    bbar :  Bbar,
    listeners : {
    	click:function(n){
    		 Ext.Msg.alert("你点击的是", n.attributes.text);  
    	},
    	beforeload:function(node){
    		tree.loader.dataUrl='demo-tree!getLowerTreeNOdes.action?id='+node.id;   
    	}
    }
});   
// set the root node   
var root = new Tree.AsyncTreeNode({   
    text: 'demo tree 示例',   
    draggable:false,   
    //树的根节点的ID设置成0有个好处就是初始化树的时候默认先加载父亲节点为0的孩子结点   
    id:'0'  
});   
    tree.setRootNode(root); 



Ext.onReady(function(){   
    
    //从本地加载树的图片   
//    Ext.BLANK_IMAGE_URL = 'extjs/resources/images/vista/s.gif';    
     
        
        tree.render();   // render the tree   
        root.expand(); //只展开根节点      
        //展开树的所有节点,有一些特殊需求会要求我们一次展开所有的节点,传true   
        //root.expand(true); 
//        tree.expandAll();//展开所有结点  
    
    }); 