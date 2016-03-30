/**
 * @class Ext.Flex
 * @extends Ext.BoxComponent
 * @author 田洪俊
 * @constructor
 * @xtype flex
 */
Ext.Ajax.timeout = 90000000;
Ext.Flex = Ext.extend(Ext.BoxComponent, {
        /**
         * @cfg {String} flashVersion
         * 指定最低需要的Flash播放器版本。
         * 由于需要直接截图，所以版本需要10.0.0
         */
        flashVersion : '10.0.0',

        /**
         * @cfg {String} backgroundColor
         * Flex拓扑图的背景色。默认值为 <tt>'#ffffff'</tt>。
         */
        backgroundColor : '#ffffff',

        /**
         * @cfg {String} wmode
         * Flash对象的wmode参数。可以用来控制分层。默认值为 <tt>'opaque'</tt>。
         */
        wmode : 'opaque',
        mask : null,
        /**
         * @cfg {Object} flashVars
         * 传递给Flash对象的变量，是一系列(关键字|值)的集合。默认值为 <tt>undefined</tt>。
         */
        flashVars : undefined,

        /**
         * @cfg {Object} flashParams
         * 传递给Flash对象的参数，是一系列(关键字|值)的集合。可能的关键字可以在以下网址搜索:
         * http://kb2.adobe.com/cps/127/tn_12701.html 默认值为 <tt>undefined</tt>.
         */
        flashParams : undefined,

        /**
         * @cfg {String} url
         * Flex拓扑图的swf文件地址. Defaults to <tt>undefined</tt>.
         */
        url : undefined,
        type : "topo",
        subType : 0,
        swfId : undefined,
        swfWidth : '100%',
        swfHeight : '100%',
        /**
         * 尝试加入此参数让页面强制布局
         */
        forceLayout:true,

        /**
         * @cfg {Boolean} expressInstall
         * 如果使用本地快速安装文件，则设置为True。需要注意的是它使用了
         * Ext.Flex.EXPRESS_INSTALL_URL, 它指向了本地的文件。默认值为 <tt>true</tt>.
         */
        expressInstall : true,
        /**
         * @cfg {Boolean} isLocalDebug
         * 是否本地Debug（是则不适用Ext.Ajax.request，而是本地延时模拟）
         * 如果设为true，则必须实现getDat(e)函数，
         * 其中e={
         *  id:"flexID",
         *  type:"ajax",
         *  more:{
         *      url:"ActionURL",
         *      param:{...参数...},
         *      callback:"回调函数名"
         *  }
         * }
         * 默认值为false
         */
        isLocalDebug:false,
        initComponent : function () {
            Ext.Flex.superclass.initComponent.call(this);
            //根据不同的拓扑图类型加载不同的Flash文件（尚未开始）
            switch (this.type) {
            case "bayface":
                this.url = Ext.Flex.BAYFACE_URL;
                break;
            case "channel":
                this.url = Ext.Flex.CHANNEL_URL;
                break;
            case "apa":
            	this.url = Ext.Flex.APA_URL;
            	break;
            case "tp":
            	this.url = Ext.Flex.TP_URL;
            	break;
            case "ola":
            	this.url = Ext.Flex.OLA_URL;
            	break;
            case "testBay":
            	this.url = Ext.Flex.TEST_URL;
            	break;
            case "topo":
            default:
                this.url = Ext.Flex.TOPO_URL;
                break;
            }
            
            this.addEvents(
                /**
                 * @event initialize
                 *
                 * @param {Chart} this
                 */
                'initialize');
        },

        onRender : function () {
            Ext.Flex.superclass.onRender.apply(this, arguments);

            var params = Ext.apply({
                allowScriptAccess : 'always',
                allowFullScreen : true,
                bgcolor : this.backgroundColor,
                wmode : this.wmode
                }, this.flashParams),
            vars = Ext.apply({
                    allowedDomain : document.location.hostname,
                    subType : this.subType,
                    SwfId : this.getId(),
                    BridgeCallback : 'Ext.FlashEventProxy.onEvent'
                }, this.flashVars);

            new swfobject.embedSWF(this.url, this.id, this.swfWidth, this.swfHeight, this.flashVersion,
                this.expressInstall ? Ext.Flex.EXPRESS_INSTALL_URL : undefined, vars, params);

            this.swf = Ext.getDom(this.id);
            this.el = Ext.get(this.swf);
        },

        getSwfId : function () {
            return this.swfId || (this.swfId = "extswf" + (++Ext.Component.AUTO_ID));
        },

        getId : function () {
            return this.id || (this.id = "extflashcmp" + (++Ext.Component.AUTO_ID));
        },

        onFlashEvent : function (e) {
            switch (e.type) {
            case "swfReady":
                this.initSwf();
                return;
            case "log":
                this.log(e);
                return;
            case "ajax":
                this.ajax(e);
                return;
            }
            e.component = this;
            this.fireEvent(e.type.toLowerCase().replace(/event$/, ''), e);
        },
        
        initSwf : function () {
            this.onSwfReady(!!this.isInitialized);
            this.isInitialized = true;
            this.fireEvent('initialize', this);
        },

        beforeDestroy : function () {
            if (this.rendered) {
                swfobject.removeSWF(this.swf.id);
            }
            Ext.Flex.superclass.beforeDestroy.call(this);
        },
        loadData : function (dat) {
            this.swf.loadData(dat);
        },
        log : function (e) {
            console[e.more.channel](e.more.cat + " - " + e.more.msg);
        },
        setAlarmStyle : function (arrColor) {
            this.swf.setAlarmStyle(arrColor);
        },
        loadSystem : function (sysData) {
        	this.swf.loadSystem(sysData);
        },
        /**
         * 带回调的方法(前台Flex通过js向后台取数据并进行回调)
         */
        ajax: function(e){
            //console.log("Ext.Ajax Invoked!!!!!!!!!!");
            var url = e.more.url;
            var param = e.more.param;
            var callback = e.more.callback;
            var delay = (Math.random() * 5000)>>0;
            //console.log("callback in " + delay + "ms -> " + callback);
            //Ext.Ajax.Request();
            this.isLocalDebug = false;
            if(this.isLocalDebug){
                (function(){
                    var dat = getDat(e);
                    this.swf[callback](dat); 
                }).defer(delay,this);
            }else{ 
                Ext.Ajax.request({
                    url:e.more.url,
                    method: 'POST',//强制指定为POST方式，防止出现params为空的情况
                    params:e.more.param,
                    scope:this,
                    success:function(resp) {
                        dat = Ext.decode(resp.responseText);
                        this.swf[callback](dat);
                    }
                });
            }
        },
        onSwfReady : function (isReset) {
        	this.swf.setSize(this.getWidth(), this.getHeight());
        },
        // private
        mask : function () {
            mask.show();
        },
        // private
        removeMask : function () {
            mask.hide();
        },
        // private
        createFnProxy : function (fn) {
            var fnName = 'topoFnProxy' + (++Ext.Flex.PROXY_FN_ID);
            Ext.Flex.proxyFunction[fnName] = fn;
            return 'Ext.Flex.proxyFunction.' + fnName;
        },

        // private
        removeFnProxy : function (fn) {
            if (!Ext.isEmpty(fn)) {
                fn = fn.replace('Ext.Flex.proxyFunction.', '');
                delete Ext.Flex.proxyFunction[fn];
            }
        },

        // private
        getFunctionRef : function (val) {
            if (Ext.isFunction(val)) {
                return {
                    fn : val,
                    scope : this
                };
            } else {
                return {
                    fn : val.fn,
                    scope : val.scope || this
                };
            }
        },
        // private
        onDestroy : function () {
            Ext.Flex.superclass.onDestroy.call(this);
        },
        listeners : {
            resize : function (me, afterW, afterH, origW, origH) {
//                console.log("[" + origW + ", " + origH + "] -> [" + afterW + ", " + afterH + "]");
//                this.swf.setSize(afterW, afterH);
            }
        }
    });
//console.log("mod @ 2014年2月24日10:32:13");
Ext.Flex.CHANNEL_1 = 0;
Ext.Flex.CHANNEL_2 = 1;
Ext.Flex.PROXY_FN_ID = 0;
Ext.Flex.proxyFunction = {};
/**
 * Sets the url for installing flash if it doesn't exist. This should be set to a local resource.
 * @static
 * @type String
 */
//TODO 修改成实际的expressInstall
var href = location.href;
var index = href.indexOf("jsp") + 4;
var preUrl = href.substr(0, index);
Ext.Flex.EXPRESS_INSTALL_URL = preUrl + "flex/playerProductInstall.swf";
Ext.Flex.TOPO_URL = preUrl + "viewManager/Topo.swf";
Ext.Flex.CHANNEL_URL = preUrl + "flex/Channel.swf";
Ext.Flex.BAYFACE_URL = preUrl + "viewManager/Bayface.swf";
Ext.Flex.APA_URL = preUrl + "viewManager/APA.swf";
Ext.Flex.TP_URL = preUrl + "viewManager/TP.swf";
Ext.Flex.OLA_URL = preUrl + "viewManager/OLA.swf";
Ext.Flex.TEST_URL =preUrl + "resourceManager/RCBay.swf";
Ext.reg('flex', Ext.Flex);
//console.log("---------Flex.js finished--------");