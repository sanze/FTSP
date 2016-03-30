/**
 * @class Ext.ux.TreeCheckNodeUI
 * @extends Ext.tree.TreeNodeUI
 * 
 * �� Ext.tree.TreeNodeUI ����checkbox���ܵ�)չ,��̨���صĽ����Ϣ���÷�Ҫ��checked����
 * 
 * )չ�Ĺ��ܵ��У�
 * һ��֧��ֻ�����Ҷ�ӽ���ѡ��
 *    ֻ�е����ص���������leaf = true ʱ��������checkbox��ѡ
 * 	  ʹ��ʱ��ֻ����������ʱ���������� onlyLeafCheckable: true �ȿɣ�Ĭ����false
 * 
 * ����֧�ֶ���ĵ�ѡ
 *    ֻ����ѡ��һ����
 * 	  ʹ��ʱ��ֻ����������ʱ���������� checkModel: "single" �ȿ�
 * 
 * ����֧�ֶ���ļ�j��ѡ 
 *    ��ѡ����ʱ���Զ�ѡ��ý���µ������ӽ�㣬��ý������и���㣨������⣩���ر���֧���첽�����ӽ�㻹û��ʾʱ����Ӻ�̨ȡ���ӽ�㣬Ȼ����ѡ��/ȡ��ѡ��
 *    ʹ��ʱ��ֻ����������ʱ���������� checkModel: "cascade" ��"parentCascade"��"childCascade"�ȿ�
 * 
 * �����"check"�¼�
 *    ���¼����������checkbox����ı�ʱ����
 *    ʹ��ʱ��ֻ�����ע���¼�,�磺
 *    tree.on("check",function(node,checked){...});
 * 
 * Ĭ������£�checkModelΪ'multiple'��Ҳ���Ƕ�ѡ��onlyLeafCheckableΪfalse�����н�㶼��ѡ
 * 
 * ʹ�÷�������loader����� baseAttrs:{uiProvider:Ext.ux.TreeCheckNodeUI} �ȿ�.
 * ���磺
 *   var tree = new Ext.tree.TreePanel({
 *		el:'tree-ct',
 *		width:568,
 *		height:300,
 *		checkModel: 'cascade',   //����ļ�j��ѡ
 *		onlyLeafCheckable: false,//�������н�㶼��ѡ
 *		animate: false,
 *		rootVisible: false,
 *		autoScroll:true,
 *		loader: new Ext.tree.DWRTreeLoader({
 *			dwrCall:Tmplt.getTmpltTree,
 *			baseAttrs: { uiProvider: Ext.ux.TreeCheckNodeUI } //��� uiProvider ����
 *		}),
 *		root: new Ext.tree.AsyncTreeNode({ id:'0' })
 *	});
 *	tree.on("check",function(node,checked){alert(node.text+" = "+checked)}); //ע��"check"�¼�
 *	tree.render();
 * 
 */

Ext.ux.TreeCheckNodeUI = function() {
this.checkModel = 'multiple';
// only leaf can checked
this.onlyLeafCheckable = false;
Ext.ux.TreeCheckNodeUI.superclass.constructor.apply(this, arguments);
};
Ext.extend(Ext.ux.TreeCheckNodeUI, Ext.tree.TreeNodeUI, {
	initComponent : function(){
		Ext.ux.TreeCheckNodeUI.superclass.initComponent.call(this);
		this.addEvents("beforecheckchange");
	},
    renderElements : function(n, a, targetNode, bulkRender) {
     var tree = n.getOwnerTree();
     this.checkModel = tree.checkModel || this.checkModel;
     this.onlyLeafCheckable = tree.onlyLeafCheckable || false;
     // add some indent caching, this helps performance when
     // rendering a large tree
     this.indentMarkup = n.parentNode ? n.parentNode.ui
       .getChildIndent() : '';
     // var cb = typeof a.checked == 'boolean';
     var cb = (!this.onlyLeafCheckable || a.leaf);
     var checkImg = cb
       ? '<img class="x-tree-node-cb icon-checked-'+n.attributes.checked+'" src="'
       +this.emptyIcon+'">'
       : '';
     var href = a.href ? a.href : Ext.isGecko ? "" : "#";
     var buf = [
       '<li class="x-tree-node"><div ext:tree-node-id="',
       n.id,
       '" class="x-tree-node-el x-tree-node-leaf x-unselectable ',
       a.cls,
       '" unselectable="on">',
       '<span class="x-tree-node-indent">',
       this.indentMarkup,
       "</span>",
       '<img src="',
       this.emptyIcon,
       '" class="x-tree-ec-icon x-tree-elbow" />',
       '<img src="',
       a.icon || this.emptyIcon,
       '" class="x-tree-node-icon',
       (a.icon ? " x-tree-node-inline-icon" : ""),
       (a.iconCls ? " " + a.iconCls : ""),
       '" unselectable="on" />',
       checkImg,
       '<a hidefocus="on" class="x-tree-node-anchor" href="',
       href,
       '" tabIndex="1" ',
       a.hrefTarget ? ' target="' + a.hrefTarget + '"' : "",
       '><span unselectable="on">',
       n.text,
       "</span></a></div>",
       '<ul class="x-tree-node-ct" style="display:none;"></ul>',
       "</li>"].join('');
     var nel;
     if (bulkRender !== true && n.nextSibling
       && (nel = n.nextSibling.ui.getEl())) {
      this.wrap = Ext.DomHelper.insertHtml("beforeBegin", nel,
        buf);
     } else {
      this.wrap = Ext.DomHelper.insertHtml("beforeEnd",
        targetNode, buf);
     }
     this.elNode = this.wrap.childNodes[0];
     this.ctNode = this.wrap.childNodes[1];
     var cs = this.elNode.childNodes;
     this.indentNode = cs[0];
     this.ecNode = cs[1];
     this.iconNode = cs[2];
     var index = 3;
     if (cb) {
      this.checkbox = cs[3];
      Ext.fly(this.checkbox).on('click',
        this.onCheck.createDelegate(this, [null]));
      index++;
     }
     this.anchor = cs[index];
     this.textNode = cs[index].firstChild;
     n.on('expand', 
        this.expandCheck.createDelegate(this, [this.node]));
    },
    // private
    onCheck : function() {
    	if(this.checkbox.disabled)
    		return;
    	if(this.fireEvent('beforecheckchange', this.node, 
			this.node.attributes.checked, 
			this.toggleCheck(this.node.attributes.checked)))
    		this.check(this.toggleCheck(this.node.attributes.checked));
    },
    check : function(checked) {
    	var arr = this.node.getOwnerTree().getChecked();
        if (this.checkModel == 'single' && arr.length == 1 && arr[0].id != this.node.id){
         //console.log(arr[0]);
         arr[0].ui.check('none');
        }
     var n = this.node;
     n.attributes.checked = checked;
     this.setNodeIcon(n);
     if (this.checkModel == 'cascade'){
      this.childCheck(n, n.attributes.checked);
      this.parentCheck(n);// 影响父节点的选中状态
     }else if(/*this.checkModel=='multiple'&&*/checked=="all"){
    	 this.childCheck(n, "none");
         this.parentCheck(n, "part");// 影响父节点的选中状态
     }else if(checked=="none"){
    	 this.childCheck(n, "none");
    	 this.parentCheck(n);// 影响父节点的选中状态
     }
     this.fireEvent('checkchange', this.node, checked);
    },
    parentCheck : function(node, checked) {
     var currentNode = node;
     // 由当前节点开始，向上递归
     while ((currentNode = currentNode.parentNode) != null) {
      if ((!currentNode.getUI().checkbox)||currentNode.getUI().checkbox.disabled)
       continue;
      var part = false;
      var sel = 0;// 记录当前节点中被选中的子节点数
      if(checked){
    	  currentNode.attributes.checked = checked;
      }else{
        Ext.each(currentNode.childNodes, function(child) {// 如果子节点全部checked
         // ==
         // 'all',父节点也为全选,否则为半选
         if (child.attributes.checked == 'all')
          sel++;
         else if (child.attributes.checked == 'part') {
          part = true;
          return false;
         }
        });
        if (part)
            currentNode.attributes.checked = 'part';
        else {
          var selType = null;
          if (sel == currentNode.childNodes.length) {
           currentNode.attributes.checked = 'all';
          } else if (sel == 0) {
           currentNode.attributes.checked = 'none';
          } else {
           currentNode.attributes.checked = 'part';
          }
        }
      }
      this.setNodeIcon(currentNode);
     };
    },
    setNodeIcon : function(n) {
     if (n.getUI() && n.getUI().checkbox){
        Ext.fly(n.getUI().checkbox).dom.className="x-tree-node-cb icon-checked-"+n.attributes.checked;
        //Ext.fly(n.getUI().checkbox).removeClass("icon-checked-none");
        //Ext.fly(n.getUI().checkbox).removeClass("icon-checked-part");
        //Ext.fly(n.getUI().checkbox).removeClass("icon-checked-all");
        //Ext.fly(n.getUI().checkbox).addClass("icon-checked-"+n.attributes.checked);
     }
    },
    // private
    childCheck : function(node, checked) {
     // node.expand(true,true);
     if (node.childNodes)
      Ext.each(node.childNodes, function(child) {
    	 if ((!child.getUI().checkbox)||child.getUI().checkbox.disabled)
    	       return;
         child.attributes.checked = checked;
         this.setNodeIcon(child);
         this.childCheck(child, checked);
        }, this);
    },
    expandCheck : function(node) {
     if (this.checkModel == 'cascade'&&node.attributes.checked == 'all') {
      node.ui.childCheck(node, 'all');
     }
    },
    toggleCheck : function(value) {
      return (value == 'all' || value == 'part') ? 'none' : 'all';
    }
   });
Ext.ux.TreeCheckPanel = function() {
Ext.ux.TreeCheckPanel.superclass.constructor.apply(this, arguments);
};
Ext.extend(Ext.ux.TreeCheckPanel, Ext.tree.TreePanel, {
    /**
    * etrieve an array of checked nodes
    */
    getChecked : function(a, startNode) {
     startNode = startNode || this.root;
     var r = [];
     var f = function() {
      if (this.attributes.checked == 'all') {
       r.push(!a ? this : (a == 'id' ? this.getDepth() + '-' + this.id: this.attributes[a]));
       return false;
      }
     };
     startNode.cascade(f);
     return r;
    }
   });
Ext.ux.TreeCheckLoader = function() {
this.baseAttrs = {
   uiProvider : Ext.ux.TreeCheckNodeUI
};
Ext.ux.TreeCheckLoader.superclass.constructor.apply(this, arguments);
};
Ext.extend(Ext.ux.TreeCheckLoader, Ext.tree.TreeLoader);

/**
 * @class Ext.ux.TreeMultiSelectionModel
 * @extends Ext.tree.MultiSelectionModel
 * 
 * @description Add function : multi select with shiftKey
 */
Ext.ux.TreeMultiSelectionModel = function(config){
  Ext.apply(this, config);
  Ext.ux.TreeMultiSelectionModel.superclass.constructor.call(this);
}
Ext.extend(Ext.ux.TreeMultiSelectionModel, Ext.tree.MultiSelectionModel, {
  onNodeClick :
    function(node, e){
      if(e.shiftKey){
        var parent=node.parentNode;
        if(parent&&parent.indexOf(this.lastSelNode)!=-1){
          var ln=this.lastSelNode;
          if(e.ctrlKey!==true){
            this.clearSelections();
          }
          parent.eachChild(function(start,end,e){
            var cur=this.parentNode.indexOf(this);
            if((cur-start)*(cur-end)<=0){
              var sm=this.getOwnerTree().getSelectionModel();
              if(sm.isSelected(this)!==true&&this.disabled!==true)
                sm.select(this, e, e.shiftKey);
            };
          },null,[parent.indexOf(this.lastSelNode),parent.indexOf(node),e]);
          this.lastSelNode=ln;
        }else{
          this.select(node, e, false);
        }
      }else if(e.ctrlKey && this.isSelected(node)){
        this.unselect(node);
      }
      else{
        this.select(node, e, e.ctrlKey);
      }
    }
});