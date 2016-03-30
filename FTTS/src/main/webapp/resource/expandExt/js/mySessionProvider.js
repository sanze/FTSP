 /** 
 * use localStorage
 */ 
 
Ext.state.SessionStorageStateProvider = function(config){   
    Ext.state.SessionStorageStateProvider.superclass.constructor.call(this);   
//    this.expires = new Date(new Date().getTime()+(1000*60*60*24*7)); //7 days
    try{   
//        if(Ext.isIE)   
//        {   
//            this.storage = new IESessionStorage("ewpstate");   
//        }else if(window.localStorage)   
//        {   alert("firefox!");
//            this.storage =  window.localStorage[window.location.hostname];   
//        }
    	this.storage =  window.localStorage;
    }catch(e){   
    }   
};   
  
Ext.extend(Ext.state.SessionStorageStateProvider, Ext.state.Provider, {   
	statefulFlag : true,
    get : function(name, defaultValue){   
        if(!this.storage)   
        {   
            return defaultValue;   
        }   
        try{   
            var value = this.storage.getItem("ys-"+name+userId);   
//            console.log("get id="+"ys-"+name+userId+"：           "+value);
            return value == "undefined" ? defaultValue : this.decodeValue(value);   
        }catch(e){   
            return defaultValue;   
        }   
    },   
    // private   
    set : function(name, value){   
        if(!this.storage)   
        {   
            return;   
        }   
        if(typeof value == "undefined" || value === null){   
            this.clear(name);   
            return;   
        }   
        try{   
//        	console.log("set 方法写值：      "+value);
        	
//        	var columns = value.columns;
//        	for(var i=0;i<columns.length;i++){
////        		console.log("set 方法写值：      "+columns[i]);
//        		columns[i].isLock = true;
//        	}
            this.storage.setItem("ys-"+name+userId, this.encodeValue(value));   
            Ext.state.SessionStorageStateProvider.superclass.set.call(this, name+userId, value);   
        }catch(e){   
        }   
    },   
  
    // private   
    clear : function(name){   
        if(!this.storage)   
        {   
            return;   
        }   
        try{   
            this.storage.removeItem(name+userId);
            Ext.state.SessionStorageStateProvider.superclass.clear.call(this, name+userId);   
        }catch(e){   
        }   
    }   
});   
//  
//IESessionStorage = function(fileName){   
//    this.fileName = fileName;   
//    this.ele = document.documentElement;   
//    this.ele.addBehavior("#default#userdata");   
//    this.ele.load(fileName);   
//}   
//IESessionStorage.prototype = {   
//    setItem:function(key, value){   
//        this.ele.setAttribute(key, value);   
//        this.ele.save(this.fileName);   
//    },   
//    getItem:function(key){   
//        return this.ele.getAttribute(key);   
//    },   
//    removeItem:function(key){   
//        this.ele.removeAttribute(key);   
//        this.ele.save(this.fileName);   
//    },   
//    deleteSelf:function(){   
//        this.ele.expires = new Date(315532799000).toUTCString();   
//        this.ele.save(this.fileName);   
//    }   
//} 

Ext.state.Manager.setProvider(new Ext.state.SessionStorageStateProvider({
	expires : new Date(new Date().getTime() + (1000 * 60 * 60 * 24 * 365))
}));