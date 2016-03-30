/*!
 * Ext JS Library 3.4.0
 * ComboGrid.js
 */
Ext.ns('Ext.ux.form');

Ext.ux.form.ComboGrid = Ext.extend(Ext.form.TriggerField, {
	clearText: 'clear',
	textSeparator : ':',
	valueField : 'value',
	textField : ['text','value'],
//    autoLoad : false,
    editable : false,
    store : new Ext.data.Store({
        data : { "total":2,"rows":[{ "text":'text1', "value":1 }, { "text":'text2', "value":2 }] },
		reader: new Ext.data.JsonReader({
    		totalProperty: 'total',
			root : "rows"
			},[
			"text","value"
		])
    }),
    columns : [ {
        header : 'text',
        dataIndex : 'text'
    } ],
	initComponent: function(){
//		if (this.autoLoad) {
		this.store.on('beforeload',function(store){store.loaded=false;},this.store);
		this.store.on('load',function(store){store.loaded=true;},this.store);
		this.store.load();
		this.grid = new Ext.grid.GridPanel({
//	        width : (param.width?param.width:'auto'),
//	        height : param.height?param.height:200,
	        autoScroll : true,
	        store : this.store,
	        columns : this.columns,
	        viewConfig : {
	            forceFit : true
	        }
	    });
		
	    this.addEvents('reset');
	    this.addEvents('select');
	    
	    this.grid.on('rowclick', function(grid, rowIndex, e) {
	        this.collapse();
	        var selections = this.grid.getSelectionModel().getSelections();
	        if (selections.length == 0) return;
	 
	        for ( var i = 0; i < selections.length; i++) {
	            var record = selections[i];
	            this.setValue(record.get(this.valueField));
	        }
	        this.fireEvent('select',this,this.getValue());
	    }.createDelegate(this));
	    this.inputVal = new Ext.form.Hidden({
    		name:this.name,
    		value:this.value
	    });
	    Ext.ux.form.ComboGrid.superclass.initComponent.call(this);
	},
	onRender: function(ct, position){
		Ext.ux.form.ComboGrid.superclass.onRender.call(this, ct, position);
		
		this.el.dom.setAttribute("name", Ext.id());
		this.setValue(this.inputVal.getValue());
//	    this.el.on("click", this.onTriggerClick);
//		this.el.on('focus', this.expand);
//		this.initTrigger();
	    //this.applyToMarkup(this.id);
	    var parent = this.el.parent("form");
	    if (!parent) parent = this.el.parent();
	    this.inputVal.render(parent);
	},
	onDestroy: function(){
		Ext.destroy(
		    this.store,
			this.grid,
			this.selectMenu
		);
		Ext.destroyMembers(this, 'hiddenField');
		Ext.ux.form.ComboGrid.superclass.onDestroy.call(this);
	},
	setValue: function (val){
		this.inputVal.setValue(val);
		if(this.store.loaded){
	        var idx = this.store.find(this.valueField, val);
	        if (idx >= 0) {
	        	if(Ext.isArray(this.textField)){
	        		var texts=[];
	        		Ext.each(this.textField,function(item,index,allItems){
	        			var itemval=this.store.getAt(idx).get(item);
	        			if(itemval)texts.push(itemval);
	        		},this);
	        		val=texts.join(this.textSeparator);
	        	}else{
	        		val=this.store.getAt(idx).get(this.textField);
	        	}
	        }
	        Ext.ux.form.ComboGrid.superclass.setValue.call(this, val);
		}else{
			this.store.on('load',function(store){
				this.setValue(this.inputVal.getValue());
			},this);
		}
	},
	getValue: function (){
		var v = this.inputVal.getValue();
		if(v === this.emptyText || v === undefined){
			v = '';
		}
		return v;
	},
	isExpanded:function() {
    	if(this.selectMenu){
    		return !this.selectMenu.hidden;
    	}
    	return false;
    },
    expand:function() {
       	if(!this.disabled){
       		if(Ext.isEmpty(this.selectMenu)){
	       		this.selectMenu = new Ext.Window({
	       			layout: 'fit',
	       			width : this.listWidth?this.listWidth:'auto',
					height : this.listHeight?this.listHeight:150,
	       			isTopContainer : true,
	       			autoScroll:true,
//		       			maximized:false,
	       	    	closable: false,
	       	    	closeAction: 'hide',
//		       	    	border: false,
//		       	    	bodyBorder: false,
//	       			modal : true,
//		       	    	pageX :pageX,
//		       	    	pageY :pageY,
	       	        items : [ this.grid ],
	       	        fbar:this.allowBlank?['->',{text:this.clearText,handler:function(){this.setValue(null);this.selectMenu.hide();}.createDelegate(this)}]:undefined
	       	    });
       		}
       		var pageX=this.getPosition()[0];
       		var pageY=this.getPosition()[1]+this.getHeight();
       		this.selectMenu.setPosition(pageX,pageY);
       		this.selectMenu.show();
       		this.selectMenu.syncSize();
       		this.mon(Ext.getDoc(), {
    			scope: this,
    			mousewheel: this.collapseIf,
    			mousedown: this.collapseIf
    		});
       	}
    },
    collapseIf:function(e) {
    	if(!this.isDestroyed && !e.within(this.wrap)){
			if(this.grid&&!e.within(this.selectMenu.getEl())){
				this.collapse();
			}
		}
    },
    collapse:function() {
    	if(!this.disabled){
    		this.selectMenu.hide();
        	Ext.getDoc().un('mousewheel', this.collapseIf, this);
        	Ext.getDoc().un('mousedown', this.collapseIf, this);
    	}
    },
    onTriggerClick:function(){
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
    },
    reset:function reset(){
    	this.fireEvent('reset',this,this.inputVal.originalValue,this.inputVal.getValue());
		this.setValue(this.originalValue);
		this.clearInvalid();
	}
});
Ext.reg("combogrid", Ext.ux.form.ComboGrid);