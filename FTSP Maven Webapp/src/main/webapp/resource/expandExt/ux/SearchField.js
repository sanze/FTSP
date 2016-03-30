/*!
 * Ext JS Library 3.4.0
 * SearchField.js
 */
Ext.ns('Ext.ux.form');

Ext.ux.form.SearchField = Ext.extend(Ext.form.TwinTriggerField, {
    initComponent : function(){
        Ext.ux.form.SearchField.superclass.initComponent.call(this);
        this.on('specialkey', function(f, e){
            if(e.getKey() == e.ENTER){
                this.onTrigger2Click();
            }
        }, this);
    },

    validationEvent:false,
    validateOnBlur:false,
    trigger1Class:'x-form-clear-trigger',
    trigger2Class:'x-form-search-trigger',
    hideTrigger1:true,
    width:180,
    hasSearch : false,
    paramName : 'query',
    mode : 'local',// local/remote
    logical : 'or',

    onTrigger1Click : function(){
        if(this.hasSearch){
            this.el.dom.value = '';
            
            if(this.mode=='local'){
            	this.store.clearFilter(false);
            }else{
            	var o = {start: 0};
	            this.store.baseParams = this.store.baseParams || {};
	            this.store.baseParams[this.paramName] = '';
	            this.store.reload({params:o});
            }
            this.triggers[0].hide();
            this.hasSearch = false;
        }
    },

    onTrigger2Click : function(){
        var v = this.getRawValue();
        if(v.length < 1){
            this.onTrigger1Click();
            return;
        }
        if(this.mode=='local'){
        	var fields=this.paramName;
        	var filters = [];
        	if(!Ext.isArray(fields)){
        		fields=[fields];
        	}
        	Ext.each(fields,function(item,index,allItems){
        		item.value=v;
        		var filter = item,
        		func   = filter.fn,
        		scope  = filter.scope || this;
        		if (!Ext.isFunction(func)) {
        			func = this.createFilterFn(filter.property, filter.value, filter.anyMatch, filter.caseSensitive, filter.exactMatch);
        		}
        		filters.push({fn: func, scope: scope});
        	},this.store);
        	
        	function createMultipleFilterFn(filters,logical) {
        		return function(record) {
        			var isMatch = (logical=="or")?false:true;
        			for (var i=0, j = filters.length; i < j; i++) {
        				var filter = filters[i],
        				fn     = filter.fn,
        				scope  = filter.scope;
        				isMatch = (logical=="or")?
    						(isMatch || fn.call(scope, record)):
							(isMatch && fn.call(scope, record));
        			}
        			return isMatch;
        		};
        	}
        	
        	this.store.filterBy(createMultipleFilterFn(filters,this.logical));        	
        }else{
	        var o = {start: 0};
	        this.store.baseParams = this.store.baseParams || {};
	        this.store.baseParams[this.paramName] = v;
	        this.store.reload({params:o});
        }
        this.hasSearch = true;
        this.triggers[0].show();
    }
});