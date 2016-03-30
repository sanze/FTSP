Ext.override(Ext.form.BasicForm, {
    isValid : function(){
        var valid = true;
        this.items.each(function(f){
           if(!f.validate()){
               valid = false;
               Ext.Msg.alert("提示", "请设置"+f.fieldLabel+"!");
               return valid;
           }
        });
        return valid;
    },
});
