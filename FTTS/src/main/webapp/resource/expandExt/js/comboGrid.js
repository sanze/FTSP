var ComboGrid = function(param) {
	function FUNC(val){}
    var options = {
        valueField : 'value',
        textField : 'text',
        autoLoad : false,
        onSelect : FUNC,
        store : new Ext.data.Store({
            data : { "total":2,"rows":[{ "text":'text1', "value":'valu1' }, { "text":'text2', "value":'valu2' }] },
    		reader: new Ext.data.JsonReader({
        		totalProperty: 'total',
				root : "rows"
				},[
				"text","value"
			])
        }),
        columns : [ {
            header : '資料',
            dataIndex : 'text'
        } ]
    };
    Ext.applyIf(param, options);
    this.field = Ext.getCmp(param.applyId);
    this.field.setEditable(false);
    //this.el = this.field.el;
 
    var valueField = param.valueField;
    var textField = param.textField;
 
    var store = param.store||this.field.store;
    this.store=store;
    this.valueField=valueField;
    this.textField=textField;
 
    var columns = param.columns;
    if (param.autoLoad) {
        store.load();
    }
    this.grid = new Ext.grid.GridPanel({
//        width : (param.width?param.width:'auto'),
//        height : param.height?param.height:200,
        autoScroll : true,
        store : store,
        columns : columns,
        viewConfig : {
            forceFit : true
        }
    });
 
    this.inputVal = new Ext.form.Hidden({
        name : this.field.el.getAttribute("name"),
        value : this.field.el.getAttribute("value")
    });
    this.field.el.dom.setAttribute("name", Ext.id());
    
    this.setValue=function (val){
		this.inputVal.setValue(val);
        var idx = this.store.find(this.valueField, val);
        // console.log(idx);
        if (idx >= 0) {
        	if(Ext.isArray(this.textField)){
        		var texts=[];
        		Ext.each(this.textField,function(item,index,allItems){
        			var itemval=this.store.getAt(idx).get(item)
        			if(itemval)texts.push(itemval);
        		},this);
        		this.field.setValue(texts.join('：'));
        	}else{
        		this.field.setValue(this.store.getAt(idx).get(this.textField));
        	}
        }else{
        	this.field.setValue(val);
        }
        param.onSelect(this.inputVal.getValue());
	}.createDelegate(this);
	this.getValue=function (){
		var v = this.inputVal.getValue();
		if(v === this.field.emptyText || v === undefined){
			v = '';
		}
		return v;
	}.createDelegate(this);
	this.setValue(this.field.el.getAttribute("value"));
    this.field.isExpanded=function() {
    	if(this.selectMenu){
    		return !this.selectMenu.hidden;
    	}
    	return false;
    }.createDelegate(this);
    this.field.expand=function() {
       	if(!this.field.disabled){
       		if(this.selectMenu==null||this.selectMenu==undefined){
	       		this.selectMenu = new Ext.Window({
	       			layout: 'fit',
	       			width : this.field.listWidth?this.field.listWidth:'auto',
					height : this.field.listHeight?this.field.listHeight:150,
	       			isTopContainer : true,
	       			autoScroll:true,
//	       			maximized:false,
	       	    	closable: false,
	       	    	closeAction: 'hide',
//	       	    	border: false,
//	       	    	bodyBorder: false,
//       			modal : true,
//	       	    	pageX :pageX,
//	       	    	pageY :pageY,
	       	        items : [ this.grid ],
	       	        fbar:this.field.allowBlank?['->',{text:'清空',handler:function(){this.setValue(null);this.selectMenu.hide();}.createDelegate(this)}]:undefined
	       	    });
       		}
       		var pageX=this.field.getPosition()[0];
       		var pageY=this.field.getPosition()[1]+this.field.getHeight();
       		this.selectMenu.setPosition(pageX,pageY);
       		this.selectMenu.show();
       		this.selectMenu.syncSize();
       		this.field.mon(Ext.getDoc(), {
    			scope: this,
    			mousewheel: this.field.collapseIf,
    			mousedown: this.field.collapseIf
    		});
       	}
    }.createDelegate(this);
    this.field.collapseIf=function(e) {
    	if(!this.field.isDestroyed && !e.within(this.field.wrap)){
			if(this.grid&&!e.within(this.selectMenu.getEl())){
				this.field.collapse();
			}
		}
    }.createDelegate(this);
    this.field.collapse=function() {
    	if(!this.field.disabled){
    		this.selectMenu.hide();
        	Ext.getDoc().un('mousewheel', this.field.collapseIf, this.field);
        	Ext.getDoc().un('mousedown', this.field.collapseIf, this.field);
    	}
    }.createDelegate(this);
    this.field.onTriggerClick=function(){
    	if(this.readOnly || this.disabled){
    		return;
    	}
    	if(this.isExpanded()){
    		this.collapse();
    		this.el.focus();
		}else {
			this.expand();
			this.onFocus({});
			this.el.focus();
		}
    }.createDelegate(this.field);
    this.field.initTrigger();
    //this.field = new Ext.form.TriggerField(fieldConfig);
//    this.field.on("disable",function(e){
//    	this.field.disable();
//    }.createDelegate(this));
//    this.field.on("enable",function(e){
//    	this.field.enable();
//    }.createDelegate(this));
//    this.cmp.setWidth(this.cmp.getWidth()-this.cmp.getTriggerWidth());
    this.field.addEvents('reset');
    this.field.reset=function reset(){
    	this.field.fireEvent('reset',this.field,this.inputVal.originalValue,this.inputVal.getValue());
		this.field.setValue(this.field.originalValue);
		this.field.clearInvalid();
		this.inputVal.setValue(this.inputVal.originalValue);
		this.inputVal.clearInvalid();
	}.createDelegate(this);
    this.field.el.on("click", this.field.onTriggerClick);
//	this.field.el.on('focus', this.field.expand);
    this.grid.on('rowclick', function(grid, rowIndex, e) {
        this.field.collapse();
        var selections = this.grid.getSelectionModel().getSelections();
        if (selections.length == 0) {
 
            return;
        }
 
        for ( var i = 0; i < selections.length; i++) {
            var record = selections[i];
//            this.field.setValue(record.get(textField));
//            this.inputVal.setValue(record.get(valueField));
            this.setValue(record.get(valueField));
        }
        param.onSelect(this.inputVal.getValue());
 
    }.createDelegate(this));
    //this.field.applyToMarkup(param.applyId);
    var parent = this.field.el.parent("form");
 
    if (!parent)
        parent = this.field.el.parent();
    this.inputVal.render(parent);
};
