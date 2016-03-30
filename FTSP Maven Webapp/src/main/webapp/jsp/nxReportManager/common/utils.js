	/**
	 * castArrayToObject(name,s[,filter])
	 * @方法描述 将一个对象数组转成一个对象，该方法用于将数组,对象转化成ongl表达式
	 * @name 需要添加的前缀
	 * @s 需要转化的对象数组
	 * @filter可选 过滤器,为空时则默认转化数组中对象的全部属性
	 * @举例说明:
	 * name = "person.china";
	 * s=[{"id":1,"name":"zhang"},{"id":2,"name":"wang"}];
	 * 若filter类型为"undefined",则转化后的结果为: 
	 * {"person.china[0].id":1,"person.china[0].name":"zhang","person.china[1].id":2,"person.china[1].name":"wang"}
	 * 若filter = function(p){
	 			if(p=="id"){
	 			 return true;
	 			}else{
	 				return false;
	 			}
	 		},则转化结果为:
	 * {"person.china[0].id":1,"person.china[1].id":2}
	 * @returns Object 转化好的对象
	 */
function castArrayToObject(name,s,filter){
	if(typeof name== "string"){
		var o ={};
		for(var i in s){
			var temp = addExOnProperties(name+"["+i+"].",s[i],filter);
			if(typeof temp =="object"){
				Ext.apply(o,temp);	
			}
		}
	return o;
	}
}

/**
 * addExOnProperties(pre,s[,filter])
 * @方法描述 为对象中属性名称加一个前缀
 * @pre 前缀名
 * @s 需要转化的对象
 * @filter可选  需要转化的对象属性,为空时则默认转化对象的全部属性
 * @returns 返回一个处理过的对象
 */
function addExOnProperties(pre,s,filter){
	if(typeof pre == "string"&& typeof s == "object"){
		var o = {};
		for(var p in s){
			if(typeof filter == "function"){
				if(!filter(p)){
					continue;
				}	
			}
			o[pre+p] = s[p];
		}
		return o;
	}	
}

function getResourceUnitManagerListModel(manages){
	if(typeof manages =="object"){
		var filter = function(p){
			if(p =="unitList"){
			return false;
			}else{
			return true;	
			}
		};
		var manageModel = castArrayToObject("manages",manages,filter);
		
		var unitModel = {};
		for(var i = 0;i<manages.length;i++){
				var temp = castArrayToObject("manages["+i+"].units",manages[i]["unitList"]);	
				Ext.apply(unitModel,temp);
		}
		Ext.apply(manageModel,unitModel);
		return manageModel;
	}	
}