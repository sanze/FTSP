/** 
 * 重写下拉框beforequery方法用于模糊查询过滤数据
 */ 
 
Ext.override(Ext.form.ComboBox, {
	listeners:{//重写 EXT的ComboBox组件  
	    beforequery : function(e) {//重写beforequery方法 ,ComboBox本身没有这个方法 ,只是留了个监听 在执行查询时执行该方法 ,如果返回FALSE就不执行查询  
	        var combo = e.combo;
	        // alert(combo.list.)
	        combo.collapse();
	        if (!e.forceAll) {
	         var value = e.query;
	         if (value != null && value != '') {
	          combo.store.filterBy(function(record, id) {
	             var text = record.get(combo.displayField);
	             return (text.indexOf(value) != -1);
	            });
	
	         } else {
	          combo.store.clearFilter();
	         }
	         combo.onLoad();//不加第一次会显示不出来   
	         combo.expand();
	         return false;
	        }
	    }
	}
}); 