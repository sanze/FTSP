/** 
 * 重写部分组件，在必输项后添加*号标示 
 */ 
 
 
/** 
 * 适用于TextField、NumberField(转自互联网) 
 */  
Ext.override(Ext.form.TextField, {  
            sideText : '',  
            onRender : function(ct, position) {  
                Ext.form.TextField.superclass.onRender.call(this, ct, position);  
                if (this.sideText != '' && !this.triggerAction) {  
                    this.sideEl = ct.createChild({  
                                tag : 'div',  
                                html : this.sideText  
                            });  
                    this.sideEl.addClass('x-form-sideText');  
                }  
            }  
        });  
/** 
 * 适用于ComboBox 
 */  
Ext.override(Ext.form.ComboBox, {  
    sideText : '',  
    onRender : function(ct, position) {  
        Ext.form.ComboBox.superclass.onRender.call(this, ct, position);  
        if (this.sideText != '') {  
            this.sideEl = ct.first('div').createChild({  
                        tag : 'div',  
                        style : 'padding-left: 19px; ',  
                        html : this.sideText  
                    });  
            this.sideEl.addClass('x-form-sideText');  
        }  
        if (this.hiddenName) {  
            this.hiddenField = this.el.insertSibling({  
                        tag : 'input',  
                        type : 'hidden',  
                        name : this.hiddenName,  
                        id : (this.hiddenId || this.hiddenName)  
                    }, 'before', true);  
            // prevent input submission  
            this.el.dom.removeAttribute('name');  
        }  
        if (Ext.isGecko) {  
            this.el.dom.setAttribute('autocomplete', 'off');  
        }  
        if (!this.lazyInit) {  
            this.initList();  
        } else {  
            this.on('focus', this.initList, this, {  
                        single : true  
                    });  
        }  
    }  
});
Ext.override(Ext.form.TriggerField, {  
    sideText : '',  
    onRender : function(ct, position) {  
        this.doc = Ext.isIE ? Ext.getBody() : Ext.getDoc();
        var st = this.sideText;
        this.sideText = "";
        Ext.form.TriggerField.superclass.onRender.call(this, ct, position);
        this.wrap = this.el.wrap({
                cls : 'x-form-field-wrap x-form-field-trigger-wrap'
            });
        this.trigger = this.wrap.createChild(this.triggerConfig || {
                tag : "img",
                src : Ext.BLANK_IMAGE_URL,
                cls : "x-form-trigger " + this.triggerClass
            });
        this.sideText = st;
        if (this.sideText != '') {  
            this.sideEl = ct.first('div').createChild({  
                        tag : 'div',  
                        style : 'padding-left: 19px; ',  
                        html : this.sideText  
                    });  
            this.sideEl.addClass('x-form-sideText');  
        }  
        
        this.sideText = "";
        this.initTrigger();
        if (!this.width) {
            this.wrap.setWidth(this.el.getWidth() + this.trigger.getWidth());
        }
        this.resizeEl = this.positionEl = this.wrap;

    }  
});