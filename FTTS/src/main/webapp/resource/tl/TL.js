/**
 * 鼠标状态定义 
 * @module Define
 * @static
 */
var MOUSE_STATE = {
    NONE:0,
    MOUSEDOWN:1,
    DRAG:2
}
/**
 * 位置定义
 * @module Define
 * @static
 */
var POS = {
    TOP:0,
    RIGHT:1,
    BOTTOM:2,
    LEFT:3
}

var lastTime = 0;
var prefixes = 'webkit moz ms o'.split(' '); //各浏览器前缀

var requestAnimationFrame = window.requestAnimationFrame;
var cancelAnimationFrame = window.cancelAnimationFrame;

var prefix;
//通过遍历各浏览器前缀，来得到requestAnimationFrame和cancelAnimationFrame在当前浏览器的实现形式
for( var i = 0; i < prefixes.length; i++ ) {
    if ( requestAnimationFrame && cancelAnimationFrame ) {
      break;
    }
    prefix = prefixes[i];
    requestAnimationFrame = requestAnimationFrame || window[ prefix + 'RequestAnimationFrame' ];
    cancelAnimationFrame  = cancelAnimationFrame  || window[ prefix + 'CancelAnimationFrame' ] || window[ prefix + 'CancelRequestAnimationFrame' ];
}

//如果当前浏览器不支持requestAnimationFrame和cancelAnimationFrame，则会退到setTimeout
if ( !requestAnimationFrame || !cancelAnimationFrame ) {
    requestAnimationFrame = function( callback, element ) {
      var currTime = new Date().getTime();
      //为了使setTimteout的尽可能的接近每秒60帧的效果
      var timeToCall = Math.max( 0, 16 - ( currTime - lastTime ) ); 
      var id = window.setTimeout( function() {
        callback( currTime + timeToCall );
      }, timeToCall );
      lastTime = currTime + timeToCall;
      return id;
    };
    
    cancelAnimationFrame = function( id ) {
      window.clearTimeout( id );
    };
}

//得到兼容各浏览器的API
window.requestAnimationFrame = requestAnimationFrame; 
window.cancelAnimationFrame = cancelAnimationFrame;(function() {
	var initializing = false, fnTest = /xyz/.test(function() {
				xyz;
			}) ? /\b_super\b/ : /.*/;
	this.Class = function() {
		this.name = "Class";
	};
	Class.extend = function(prop) {
		// console.log((this.prototype.name || "Class") + ".extend -> " +
		// prop.name);
		var _super = this.prototype;
		initializing = true;
		var prototype = new this();
		initializing = false;
		for (var name in prop) {
			// 如果prop.name是函数 且 _super.name是函数 且 不是_super
			// 则 将他赋给prototype
			// 否则（为属性），直接赋给prototype
			prototype[name] = typeof prop[name] == "function"
					&& typeof _super[name] == "function"
					&& fnTest.test(prop[name]) ? (function(name, fn) {
				return function() {
					var tmp = this._super;
					this._super = _super[name];
					var ret = fn.apply(this, arguments);
					this._super = tmp;
					return ret;
				};
			})(name, prop[name]) : prop[name];
		}
		function Class() {
			// console.log("Class.name = " + this.name);
			// 如果存在init函数，则调用init函数进行对象初始化
			if (!initializing && this.init) {
				this.init.apply(this, arguments);
			} else {
				// console.log("initializing = " + initializing);
				// console.log("this.init = " + this.init);
			}
		}

		Class.prototype = prototype;
		Class.constructor = Class;
		Class.extend = arguments.callee;
		return Class;
	};
})();
/**
 * Link管理器
 * @class LinkManager
 * @static
 * @constructor
 * @author TianHongjun
 */
var LinkManager = LinkManager || {};
var LM = LinkManager;
(function(){
	LinkManager.data = [];
	LinkManager.rawLinks = [];
	/**
	 * 管理所有的port对象
	 * @property {Object} ports
	 */
	LinkManager.ports = {
			f:[],	//对应左侧List的Port
			u:[],	//OTDR对应的Port
			U:[],	//OSW 输入端
			p:[],	//OSW 输出端
			F:[]	//对应右侧List的Port
	};
	/**
	 * 储存所有的port对象状态
	 */
	LinkManager.state = {
			
	}
	/**
	 * LinkManager初始化，加载左右Link数据
	 * @method init
	 * @param stationId
	 */
	LinkManager.init = function(stationId){
		LinkManager.data = [];
		Ext.Ajax.request({
			url : "external-connect!getConnectInfoByStationId.action",
			type : 'post',
			params : {
				stationId:stationId
			},
			success : function(response, options){
				var obj = Ext.decode(response.responseText);
				LinkManager.data = obj.rows;
				TL.setStationName(obj.STATION_NAME)
			},
			failure:function(){
				Ext.Msg.alert("提示：", "后台运行出错！");
			},
			error:function(){
				Ext.Msg.alert("提示：", "后台运行出错！");
			}
		});
	};
	/**
	 * 注册Port，在Port创建时调用
	 * @method regPort
	 * @param {String} portType 端口类型
	 * @param {Port} port Port对象
	 */
	LinkManager.regPort = function(portType, port){
		if(portType == "f"
			|| portType == "F"
			|| portType == "u"
			|| portType == "U"
			|| portType == "p"){
			//符合就添加
			LinkManager.ports[portType].push(port);
		}else{
			//console.error("Port类型错误！");
		}
	}
	/**
	 * 根据Port类型和ID 获取Port
	 * @method getPort
	 * @private
	 * @param {String} portType
	 * @param {Number} portId
	 * @returns {Port} 返回找到的Port对象，如果找不到则为 __null__
	 */
	LinkManager.getPort = function(portType, portId){
		var ports = LM.ports[portType];
		var rv = null;
//		console.log("\tLM.getPort(" + portType + ", " + portId + ") = ")
//		console.log("\t该类型Port有" + ports.length + "个~");
		for ( var i = 0; i < ports.length; i++) {
			// 遍历Ports，找到port
			var port = ports[i];
//			console.log("\t\t" + port.id);
			if(port.id == portId){
				rv = port;
				port.occupied = true;
				break;
			}
		}
//		console.log(rv);
		return rv;
	}
	/**
	 * 根据原始数据生成Link对象数据
	 * 在左中右数据刷新时调用
	 * @method generateLinks
	 */
	LinkManager.generateLinks = function(){
//		console.log("LM.generateLinks()")
		TL.links = [];
		var links = LM.data;
		var aPortTypes = ["-", "u", "p", "f", "p", "f", "p"];
		var zPortTypes = ["-", "U", "F", "U", "F", "F", "U"];
		for ( var i = 0; i < links.length; i++) {
			var linkData = links[i];
//			console.log(linkData);
			var linkType = linkData.CONN_TYPE;
			var aId = linkData.A_END_ID;
			var zId = linkData.Z_END_ID;
//			console.log("  @links[" + (i+1) + "] - "
//					+ aPortTypes[linkType] + " -> " + zPortTypes[linkType]);
//			console.log("  find A=" + aId + " & Z=" + zId);
			var aPort = LM.getPort(aPortTypes[linkType], aId);
			if(!!aPort){
				aPort.linkText = linkData.FIBER_INFO;
			}
			var zPort = LM.getPort(zPortTypes[linkType], zId);
			if(!!zPort){
				zPort.linkText = linkData.FIBER_INFO;
			}
			if(!!aPort && !!zPort){
		        var link = new ZLink(aPort, zPort);
		        link.stationId = linkData.STATION_ID;
		        link.text = linkData.FIBER_INFO;
		        link.setConnType(linkData.CONN_TYPE);
		        TL.links.push(link);
			}
		}
	}
	/**
	 * 添加Link数据到LinkManager
	 * @method addLink
	 * @param {Object} linkData
	 */
	LinkManager.addLink = function(linkData){
//		console.log("LM.addLink");
		delete(linkData.rawLink);
		LM.data.push(linkData);
		var fs = LM.ports.f;
		for ( var i = 0; i < fs.length; i++) {
//			console.log(fs[i]);
			fs[i].occupied = LM.isFiberOccupied("f", fs[i].id);
//			console.log("l = " + fs[i].occupied)
		}
		var Fs = LM.ports.F;
		for ( var i = 0; i < Fs.length; i++) {
//			console.log(Fs[i]);
			Fs[i].occupied = LM.isFiberOccupied("F", Fs[i].id);
//			console.log("r = " + Fs[i].occupied)
		}
	}
	LinkManager.removeLink = function(link){
//		console.log("LM.removeLink");
		TL.links.remove(link)
		link.start.occupied = false;
		link.end.occupied = false;
		link.start.link = null;
		link.end.link = null;
//		console.log("\t b4 = " + LM.data.length);
		for ( var i = 0; i < LM.data.length; i++) {
			var dat = LM.data[i];
			if(dat.STATION_ID == link.stationId
					&& dat.A_END_ID == link.aEndId
					&& dat.Z_END_ID == link.zEndId
					&& dat.CONN_TYPE == link.connType){
//				console.log("\tstart delete");
				LM.data.remove(dat);
				break;
			}
		}
//		console.log("\t after = " + LM.data.length);
		var fs = LM.ports.f;
		for ( var i = 0; i < fs.length; i++) {
//			console.log(fs[i]);
			fs[i].occupied = LM.isFiberOccupied("f", fs[i].id);
//			console.log("l = " + fs[i].occupied)
		}
		var Fs = LM.ports.F;
		for ( var i = 0; i < Fs.length; i++) {
//			console.log(Fs[i]);
			Fs[i].occupied = LM.isFiberOccupied("F", Fs[i].id);
//			console.log("r = " + Fs[i].occupied)
		}
	}
	LinkManager.isFiberOccupied=function(portType, portId){
//		console.log("portType = " + portType + "  portId = " + portId)
		if(portType == "f"
			|| portType == "F"){
			var rv = false;
			for ( var i = 0; i < LM.data.length; i++) {
				var dat = LM.data[i];
				switch(dat.CONN_TYPE){
				//2:RTU-光缆 3:光缆-CTU 4:CTU-光缆 5:光缆-光缆
				case 2:
				case 4:
					rv = rv || (dat.Z_END_ID == portId);
					break;
				case 3:
					rv = rv || (dat.A_END_ID == portId);
					break;
				case 5:
					rv = rv || (dat.A_END_ID == portId || dat.Z_END_ID == portId);
					break;
				}
			}
			return rv;
		}
		return false;
	}
})();/**
 * Region 区域定义，用于快速判断鼠标是否在区域内
 * @module Controls
 * @class Region
 * @constructor 
 * @param {Number} x Region 的 x坐标
 * @param {Number} y Region 的 y坐标
 * @param {Number} width Region 的 宽度
 * @param {Number} height Region 的 高度
 */
function Region(x, y, w, h){
    this.x = x;
    this.y = y;
    this.width = w;
    this.height = h;
    this.l = x;
    this.t = y;
    this.r = x + w;
    this.b = y + h;
}
/**
 * 
 * @class Region
 * @method inRegion
 * @param {Number} x 鼠标 的 x坐标
 * @param {Number} y 鼠标 的 y坐标
 * @return {Boolean} 返回鼠标是否在区域内，是则true，否则false
 */
Region.prototype.inRegion = function(x, y){
    return((x - this.l)*(x-this.r)<=0 && (y - this.t)*(y - this.b)<=0);
}

/**
 * IObject 的构造函数,
 * 各种可见物体的基类，
 * 定义了x,y,宽高4个基本属性
 * @class IObject
 * @constructor init(cfg)
 * @param {Object} cfg 配置参数
 * 详见{{#crossLink "IObject.class/init"}}{{/crossLink}}
 */
var IObject = Class.extend({
    name:"IObject",
    /**
     * 对象ID
     * @property {Number} id
     */
    id:-1,
    /**
     * IObject的x坐标
     * @property {Number} x
     */
	x:0,
    /**
     * IObject的y坐标
     * @property {Number} y
     */
	y:0,
    /**
     * IObject的宽度
     * @property {Number} width
     */
    width:0,
    /**
     * IObject的高度
     * @property {Number} height
     */
    height:0,
    /**
     * IObject的区域定义
     * @property {Region} region
     */
    region:null,
    /**
     * 鼠标是否在区域内的指示标志
     * @property {Boolean} isCursorInside
     */
    isCursorInside:false,
    items : null,
    /**
     * 
     * IObject 构造函数
     * @method init
     * @param {Object} cfg
     *      @param {Number} cfg.x IObject 的 x坐标
     *      @param {Number} cfg.y IObject 的 y坐标
     *      @param {Number} cfg.width IObject 的 宽度
     *      @param {Number} cfg.height IObject 的 高度
     */
	init : function(cfg) {
        this.setCfg(cfg);
        //设置区域
        this.region = new Region(this.x, this.y, this.width, this.height);
	},
    /**
     * 设置参数，将参数赋值给对象本身
     * @method setCfg
     * @param {Object} cfg 配置参数，详情见init方法
     */
    setCfg:function (cfg) {
        if (cfg === undefined) return;
        for (var prop in cfg) {
            var v = cfg[prop];
            if (v === undefined){
                //console.warn("输入参数: '" + prop + "' 对应的值为空.");
            }
            else if (prop in this) {
                this[prop] = v;
            }
        }
    },
    /**
     * 设置父对象
     * @method draw
     * @param {Object} ctx Canvas的2dContext
     */
    draw:function(ctx){
        this.update();
        if(!!ctx){
            // console.log(this);
            // console.log(String.format("[{0}, {1}, {2}, {3}]", this.x, this.y, this.width, this.height))
            ctx.strokeStyle="#0000FF";
            ctx.fillStyle="rgba(255,255,200,0.5)";
            ctx.fillRect(this.x, this.y, this.width, this.height);
            ctx.strokeRect(this.x, this.y, this.width, this.height);
            if(this.isCursorInside && SHOW_HOVER){
                ctx.fillStyle="rgba(200,200,200,0.5)";
                ctx.fillRect(this.x, this.y, this.width, this.height);
            }
        }
    },
    /**
     * 数据更新函数
     * 涉及到计算的部分都丢这里
     * @method update
     * @param {Number} dt 时间差
     */
    update:function(dt){
        //this.x += Math.random()-0.5;
    },
    /**
     * 判断鼠标是否在区域内
     * @method inRegion
     * @param {Number} x 鼠标x坐标
     * @param {Number} y 鼠标y坐标
     */
    inRegion:function(x,y){
        this.isCursorInside = this.region.inRegion(x, y);
        // console.log(this.isCursorInside);
        return this.isCursorInside;
    },
    /**
     * 鼠标按下事件
     * @event onmousedown
     * @param {Object} curPos 鼠标坐标
     *      @param {Number} curPos.x 鼠标x坐标
     *      @param {Number} curPos.y 鼠标y坐标
     */
    onmousedown : function(curPos) {
//        console.log(this.name + ".onmousedown");
        return this.isCursorInside;
    },
    /**
     * 鼠标移动事件
     * @event onmousemove
     * @param {Object} curPos 当前坐标
     *      @param {Number} curPos.x 鼠标x坐标
     *      @param {Number} curPos.y 鼠标y坐标
     * @param {Object} delta  鼠标偏移
     *      @param {Number} delta.x 偏移量x
     *      @param {Number} delta.y 偏移量y
     */
    onmousemove : function(curPos, delta) {
        return this.isCursorInside;
    },
    /**
     * 鼠标松开事件
     * @event onmouseup
     * @param {Object} curPos 鼠标当前坐标
     *      @param {Number} curPos.x 鼠标x坐标
     *      @param {Number} curPos.y 鼠标y坐标
     */
    onmouseup : function(curPos) {
        return this.isCursorInside;
    },
    /**
     * 丢失焦点事件传递
     * @method lostFocus
     */
    lostFocus:function(){
    	this.isCursorInside = false;
        for(var len = this.items.length, i = len - 1; i >= 0; i--){
            var child = this.items[i];
            child.lostFocus();
        }
    }
});

/**
 * Container 的构造函数,<br>
 * 以一个window做比喻<br>
 * 原始x，y，width，height就是window的xy宽高<br>
 * cx,cy,cw,ch就是内部空白区域（称为Client）的xy宽高
 * @module Controls
 * @class Container
 * @extends IObject
 * @constructor
 */
var Container = IObject.extend({
    name:"Container",
    /**
     * Client区域X
     * 内部使用，根据padding属性自动生成
     * @private
     * @property {Number} cx
     */
	cx:0,
	/**
	 * Client区域y 
     * 内部使用，根据padding属性自动生成
     * @private
     * @property {Number} cy
	 */
	cy:0,
	/**
	 * Client区域宽度 
     * 内部使用，根据padding属性自动生成
     * @private
     * @property {Number} cw
	 */
    cw:0,
    /**
     * Client区域高度 
     * 内部使用，根据padding属性自动生成
     * @private
     * @property {Number} ch
     */
    ch:0,
    /**
     * Container 留白区域定义 
     * 等同于css中padding定义
     * @property {String} padding
     */
    padding:"1",
    
    offset:null,
    /**
     * 最大偏移区域，
     * 由Clien最大区域减去Client显示区域获得
     * 当Client实际区域较大时，值为正数
     * 当Client实际区域较小时，值为负数
     * 判断依据：
     *      curPos.x + maxOffset.x + delta.x > 0 合法，否则非法
     *      y同理
     * 
     * @private
     * @property {Number} maxOffset
     */
    maxOffset:null,
    clientRegion:null,
    cursorInClient:false,
    items:null,
    /**
     * Container的初始化函数<br>
     * ->padding 内边距，Client区域与整个容器的边距，最大四个参数，中间以空格分开
     *           当只有一个参数时，四边距都是这个值
     * @method init
     * @param {Object} cfg 配置参数
     * @param {String} cfg.padding 内边距设置
     */
	init : function(cfg) {
	    // console.log("=======");
	    var canvas = document.createElement("canvas");
        this._super(cfg);
        //根据padding计算内部区域大小
        var td=bd=ld=rd=0;
        this.padding = this.padding.trim();
        if(!!this.padding){
            var args = this.padding.split(" ");
            var len = args.length;
            if(len>0){
                ld = rd = td = bd = args[0]>>0;
            }
            if(len>1){
                ld = rd = args[1]>>0;
            }
            if(len>2){
                bd = args[2]>>0;
            }
            if(len>3){
                ld = args[3]>>0;
            }
        }
        this.cx = ld + this.x;
        this.cy = td + this.y;
        this.cw = this.width - ld - rd;
        this.ch = this.height - td - bd;
        this.clientRegion = new Region(this.cx, this.cy, this.cw, this.ch);
        this.offset = {
            x : 0,
            y : 0
        };
        this.maxOffset = {
            x : 0,
            y : 0
        };
        // this.vScr = new ScrollBar({
            // x : this.cx + this.cw - 16,
            // y : this.cy,
            // width : 16,
            // height : this.ch,
            // data : this.offset,
            // key : "y"
        // });
        this.items = [];
	},
    /**
     * 绘制函数
     * 整体绘图逻辑
     * 1. 绘制外层图像
     * 2. 清除client区域
     * 3. 设置clip
     * 4. 绘制client区域
     *    4.1 绘制底图
     *    4.2 绘制scroll bar
     * 5. 恢复clip
     * 
     * 一般情况下是不需要重绘了
     * @method draw
     * @param {CanvasRenderingContext2D} ctx Canvas绘制上下文
     */
    draw:function(ctx){
        //绘制外层图像
        this.drawBg(ctx);
        if(!!ctx){
            //清除client区域
            ctx.fillStyle="blue";
            ctx.clearRect(this.cx, this.cy, this.cw, this.ch);
            //绘制client区域
            ctx.save();
            //设置clip
            ctx.fillStyle="red";
            ctx.beginPath();
            ctx.rect(this.cx, this.cy, this.cw, this.ch);
            ctx.closePath();
            ctx.clip();
            //必须加上Client区域偏移
            ctx.translate(this.cx+this.offset.x, this.cy+this.offset.y);
            // console.log(this.offset);
            //绘图，将client图像绘制到ctx
            this.drawClient(ctx);
            //恢复绘图状态
            ctx.restore();
            this.drawScroll(ctx);
            if(this.cursorInClient){
                // ctx.fillStyle="rgba(200,200,255,0.5)";
                // ctx.fillRect(this.cx, this.cy, this.cw, this.ch);
            }
            // this.vScr.draw(ctx);
        }
    },
    /**
     * 绘制背景/边框函数
     * @method drawBg
     * @param {CanvasRenderingContext2D} ctx Canvas绘制上下文
     */
    drawBg:function(ctx){
//        if(!!ctx){
//            ctx.fillStyle="#0000FF";
//            ctx.fillRect(this.x, this.y, this.width, this.height);
//        }
    },
    /**
     * 绘制Clien区域函数
     * @method drawClient
     * @param {CanvasRenderingContext2D} ctx Canvas绘制上下文
     */
    drawClient:function(ctx){
        // console.log(this.items.length)
        //逆序绘制
        for(var len = this.items.length, i=len-1;i>=0;i--){
            this.items[i].draw(ctx);
        }
    },
    /**
     * 绘制Clien区域函数
     * @method drawClient
     * @param {CanvasRenderingContext2D} ctx Canvas绘制上下文
     */
    drawScroll:function(ctx){
        var sx = this.x + this.width-3;
        var totalHeight = this.maxOffset.y + this.ch - 5;
        var barHeight = this.ch * this.ch / totalHeight;
        var oy = -this.offset.y/totalHeight * this.ch;
        if(this.ch > totalHeight) return;
        ctx.save();
        ctx.lineWidth = 4;
        ctx.lineCap = "round";
        ctx.beginPath();
        ctx.strokeStyle="rgba(180,180,180,0.5)";
        ctx.moveTo(sx, 0);
        ctx.lineTo(sx, this.ch);
        ctx.stroke();
        ctx.beginPath();
        ctx.strokeStyle="rgba(80,80,80,0.5)";
        ctx.moveTo(sx, oy);
        ctx.lineTo(sx, oy+ barHeight);
        ctx.stroke();
        ctx.restore();
    },
    /**
     * 判断鼠标是否在区域内
     * 加入Client区域以及滚动条判定
     * @method inRegion
     * @param {Number} x 鼠标x坐标
     * @param {Number} y 鼠标y坐标
     */
    inRegion:function(x, y){
        this.cursorInClient = this.clientRegion.inRegion(x, y);
        // this.vScr.inRegion(x, y);
        if(this.cursorInClient){
            for(var i=0,len=this.items.length; i<len; i++){
                //此处进行坐标转换
                //鼠标坐标减去Client区域的偏移量
                //再减去内部偏移量
                this.items[i].inRegion(x - this.cx - this.offset.x, y - this.cy - this.offset.y);
            };
        }else{
            for(var i=0,len=this.items.length; i<len; i++){
                this.items[i].isCursorInside = false;
            };
        }
        return this._super(x, y);
    },
    /**
     * 鼠标按下事件
     * @event onmousedown
     * @param {Object} curPos 鼠标坐标
     *      @param {Number} curPos.x 鼠标x坐标
     *      @param {Number} curPos.y 鼠标y坐标
     */
    onmousedown : function(curPos) {
        if(this.isCursorInside){
            // console.log(this.name + ".onmousedown()");
            var continueFlag = true;
            for (var i = 0;continueFlag && i < this.items.length; i++) {
                var o = this.items[i];
                if(o instanceof IObject){
                    continueFlag = !o.onmousedown(curPos);
                }
            }
        }
        return this.isCursorInside;
    },
    /**
     * 鼠标移动事件
     * @event onmousemove
     * @param {Object} curPos 当前坐标
     *      @param {Number} curPos.x 鼠标x坐标
     *      @param {Number} curPos.y 鼠标y坐标
     * @param {Object} delta  鼠标偏移
     *      @param {Number} delta.x 偏移量x
     *      @param {Number} delta.y 偏移量y
     */
    onmousemove : function(curPos, delta) {
        if(!!delta && this.isCursorInside){
        // console.log("Container.onmousemove()");
            // if(this.vScr.isCursorInside){
                // this.vScr.onmousemove(curPos, delta);
            // }else{
            if(this.offset.x + this.maxOffset.x + delta.x > 0){
                //屏蔽左右拖动功能
                // this.offset.x += delta.x;
            }
            //上下拖动
            if(this.offset.y + this.maxOffset.y + delta.y > 0){
                this.offset.y += delta.y;
            }
            //左右拖动边界限制
            if(this.offset.x>0){
                this.offset.x = 0;
            }
            //上下拖动边界限制
            if(this.offset.y>0){
                this.offset.y = 0;
            }
            for (var i = 0; i < this.items.length; i++) {
                var o = this.items[i];
                o.updateLines();
            }
            // }
        }
        return this.isCursorInside;
    },
    /**
     * 鼠标松开事件
     * @event onmouseup
     * @param {Object} curPos 鼠标当前坐标
     *      @param {Number} curPos.x 鼠标x坐标
     *      @param {Number} curPos.y 鼠标y坐标
     */
    onmouseup : function(curPos) {
        if(this.isCursorInside){
            // console.log(this.name + ".onmouseup()");
            var continueFlag = true;
            for (var i = 0;continueFlag && i < this.items.length; i++) {
                var o = this.items[i];
                if(o instanceof IObject){
                    continueFlag = !o.onmouseup(curPos);
                }
            }
        }
        return this.isCursorInside;
    },
    /**
     * 更新Client占用区域
     * @method updateClientSize
     */
    updateClientSize:function(){
        var l,r,t,b;
        l=r=t=b=0;
        for(var i=0,len=this.items.length; i<len; i++){
            var bz = this.items[i].getBounds();
            // l = Math.min(l, bz.left);
            r = Math.max(r, bz.right);
            // t = Math.max(t, bz.top);
            b = Math.max(b, bz.bottom);
        };
        //留出5像素的空余
        this.maxOffset.x = r - this.cw + 5;
        this.maxOffset.y = b - this.ch + 5;
    },
    /**
     * 添加一个Widget到Container中
     * @method add
     * @param {Widget} wgt Widget对象
     */
    add:function(wgt){
        // if(wgt instanceof Widget){
        this.items.push(wgt);
        this.updateClientSize();
        wgt.parent = this;
        wgt.index = this.items.length - 1;
        // }
    },
    clear:function(){
    	this.items = [];
    	//TODO 全局更新
    }
});

/**
 * ListView 的构造函数,
 * 以一个window做比喻
 * 原始x，y，width，height就是window的xy宽高
 * cx,cy,cw,ch就是内部空白区域（称为Client）的xy宽高
 * @module Controls
 * @class ListView
 * @namespace Container
 * @constructor
 */
var ListView = Container.extend({
    name:"ListView",
    //列表item之间的间隔
    snap:0,
    //item的背景
    bgColor:0xffffdd,
    //item的文字颜色
    textColor:0x000033,
    /**
     * 数据存储区，为一个数组，内部元素显示通过renderer来实现 
     * @private
     * @property {Object} data
     */
    data:null,
    /**
     * ListView中ListItem的个数
     * @property {Number} count
     */
    count:0,
    /**
     * ListView的端口位置
     * @property {Number} portPos
     */
    portPos:POS.LEFT,
    /**
     * 数据条目渲染函数
     * 可以通过配置项来覆盖，默认直接返回v 
     * @property {Function} renderer
     */
    renderer:function(v){
        return v+"";
    },
    /**
     * ListView 的初始化函数
     * @method init
     * @param cfg 配置参数
     * 介个是重点 renderer:function(v){
     *                return v.aaa+v.bbb
     *            }
     */
	init : function(cfg) {
	    //ListView就不矫情用什么边框了。
        this.padding = "1";
        //data放到这里初始化，否则多个LV会共用数组然后就GG思密达
        this.data=[];
        //调用父类
        this._super(cfg);
	},
    /**
     * 绘制背景/边框函数
     * @method drawBg
     * @param {CanvasRenderingContext2D} ctx Canvas绘制上下文
     */
    drawBg:function(ctx){
        if(!!ctx){
            ctx.fillStyle="#888888";
            ctx.fillRect(this.x, this.y, this.width, this.height);
        }
    },
	/**
     * 添加一项
     * @method add
     * @param {Object} v 要添加的数据
	 */
    add:function(v){
//        this.data.push(v);
//    	console.log(v);
    	v.FIBER_NAME = v.FIBER_NAME || "<空>";
    	v.NOTE = v.NOTE || "<空>";
        var itm = new ListItem({
            x: 0,
            y: this.count*24,
            text:this.renderer(v),
            width:this.width,
            height:24,
            id:v.RESOURCE_FIBER_ID,
            portPos:this.portPos,
            portText:v.FIBER_NAME
        });
        this.count++;
        this._super(itm);
    },
    /**
     * 添加一项（暂不使用）
     * @method addAll
     * @param {Array} arr 要添加的数据数组
     */
    addAll:function(arr){
        this.data = this.data.concat(arr);
        this.measureClient();
        this.count+=arr.length;
    },
    clear:function(){
    	this.count = 0;
    	this._super();
    }
});

/**
 * Link 的构造函数
 * 连接线基类
 * @module Controls
 * @class Link
 * @extends IObject
 * @constructor
 */
var Link = IObject.extend({
    name:"Link",
    //列表item之间的间隔
    start:null,
    //item的背景
    end:null,
    /**
     * Link 的初始化函数
     * @method init
     * @param {Object} cfg 配置参数
     *      @param {Object} cfg.start 起始点，一定要Object，不然没法绑定
     *          @param {Object} cfg.start.x 起始点X
     *          @param {Object} cfg.start.y 起始点X
     *      @param {Object} cfg.start 结束点，一定要Object，不然没法绑定
     */
    init : function(startNode, endNode) {
        // console.log(startNode);
        // console.log(endNode);
        this.start = startNode || TL.startPos;
        this.end = endNode || TL.curPos;
        startNode.link = this;
        endNode.link = this;
        // console.log("<" + this.start.x + ", " + this.start.y + "> - <" +
                    // this.end.x + ", " + this.end.y + ">")
    },
    /**
     * 判断鼠标是否在区域内
     * @method inRegion
     * @param {Number} x 鼠标x坐标
     * @param {Number} y 鼠标y坐标
     */
    inRegion:function(x,y){
        return false;
    },
    // update:function(){
    //         
    // },
    /**
     * Link 绘制函数
     * @method draw
     * @param {CanvasRenderingContext2D} ctx Canvas绘制上下文
     */
    draw:function(ctx){
//        if(TL.mouseState == MOUSE_STATE.DRAG){
        var s = this.start.getAbsPos();
        var e = this.end.getAbsPos();
        ctx.strokeStyle = "blue"
		ctx.beginPath();
        ctx.moveTo(s.x, s.y);
        ctx.lineTo(e.x, e.y);
        ctx.closePath();
        ctx.stroke();
//        }
    }
});

/**
 * ZLink 的构造函数
 * 连接线基类
 * @class ZLink
 * @constructor
 */
var ZLink = Link.extend({
    name:"ZLink",
    points:null,
    /**
     * 连接类型
     *  <li>1:OTDR-OSW</li>
     *  <li>2:OSW-光缆</li>
     *  <li>3:光缆-OSW</li>
     *  <li>4:OSW-光缆</li>
     *  <li>5:光缆-光缆</li>
     *  <li>6:OSW-OSW</li>
     * @property {Number} connType
     */
    connType:0,
    /**
     * 局站ID
     * @property {Number} stationId
     */
    stationId:-1,
    /**
     * A端ID
     * @property {Number} aEndId
     */
    aEndId:-1,
    /**
     * Z端ID
     * @property {Number} zEndId
     */
    zEndId:-1,
    text:"#",
    selected:false,
    /**
     * ZLink 的初始化函数
     * @method init
     * @param {Object} cfg 配置参数
     *      @param {Object} cfg.start 起始点，一定要Object，不然没法绑定
     *      @param {Object} cfg.start 结束点，一定要Object，不然没法绑定
     */
    init : function(start, end) {
        this._super(start, end);
        this.points=[];
        this.aEndId = start.id;
        this.zEndId = end.id;
        start.occupied = true;
        end.occupied = true;
//        console.log("start.type = " + start.type)
//        console.log("end.type = " + end.type)
        this.update();
    },
    /**
     * 判断鼠标是否在区域内
     * @method inRegion
     * @param {Number} x 鼠标x坐标
     * @param {Number} y 鼠标y坐标
     */
    inRegion:function(x,y){
        // console.log("ZLink.inRegion");
        //1.生成所有的线段
        var len = this.points.length;
        var lines = [];
        var startPos = this.start.getAbsPos();
        var endPos = this.end.getAbsPos();
        //push开头线段参数
        lines.push([startPos.x, startPos.y, 
                    this.points[0].x, this.points[0].y, 
                    x, y]);
        for (var i=0; i < len - 1; i++) {
            var fN = this.points[i];
            var tN = this.points[i+1];
            //push中间线段参数
            lines.push([fN.x, fN.y, tN.x, tN.y, x, y]);
        };
        //push结束线段参数
        lines.push([tN.x, tN.y, endPos.x, endPos.y, x, y]);
        //2. 每条都进行碰撞测试
        var hit = false;
        //    测试通过（鼠标在线上）则不继续
        for(var i=0,len = lines.length; !hit && i<len; i++){
            hit = hitTest.apply(null, lines[i]);
        }
        this.isCursorInside = hit;
        return hit;
    },
    /**
     * Link 绘制函数
     * @method draw
     * @param {CanvasRenderingContext2D} ctx Canvas绘制上下文
     */
    draw:function(ctx){
    	//绘制Link的条件
    	if((this.start.isVisible() && this.end.isVisible())
    			|| this.connType == 6){
	        //先计算各个中间点
	        this.update();
	        //再按照点路径进行绘制
	        if(true || TL.mouseState == MOUSE_STATE.DRAG){
	            var startPos = this.start.getAbsPos();
	            var endPos = this.end.getAbsPos();
	            ctx.beginPath();
	            ctx.moveTo(startPos.x, startPos.y);
	            for (var i=0; i < this.points.length; i++) {
	                var p = this.points[i];
	                ctx.lineTo(p.x, p.y);
	            };
	            ctx.lineTo(endPos.x, endPos.y);
	            if(this.isCursorInside || this.selected){
	                ctx.lineWidth = 3;
	                ctx.strokeStyle = this.selected?"blue": "yellow";
	                ctx.stroke();
	            }
	            ctx.strokeStyle = "blue";
	            ctx.lineWidth = 1;
	            ctx.stroke();
	        }
    	}
    },
    /**
     * 更新Link节点路径
     * @method  update
     * 
     */
    update:function(){
        this.points=[];
        var startPos = this.start.getAbsPos();
        var endPos = this.end.getAbsPos();
        switch(this.connType){
            case 6:
                if(this.start.type == "p"){
                    p = this.start;
                    u = this.end;
                }else{
                    p = this.end;
                    u = this.start;
                }
                //p总是在右边，u在左边，方向 p->u
                startPos = p.getAbsPos();
                endPos = u.getAbsPos();
                //p端口水平位置粗调
                var px = startPos.x;
                for(var i=0,len = p.parent.items.length;i<len;i++){
                    var port=p.parent.items[i];
                    if(port != p && !!port.link && port.link.connType == 6){
                        px += 10;
                    }
                    if(port == p){
                        break;
                    }
                }
                //p端口位置微调，不然如下情况会线段相交（pAdjOffset设置为0可以再现）
                //				OSW1.u->OSW2.p   OSW1.p->OSW3.u
                var pAdjOffset = p.parent.index * 2;
                //第一个点
                this.points.push({
                    x : px + 20 + pAdjOffset,
                    y : startPos.y
                });
                var dir = 1;//dir=1,说明p在上面
                if(startPos.y > endPos.y){
                    dir = -1;
                }
                // console.log(dir);
                //yOffset 为负
                var yOffset = startPos.y - p.y;
                // console.log(yOffset);
                var uBound = u.parent.getBounds();
                //目标OSW上方的空隙
                var uBlank = {
                    y1:dir > 0 ? uBound.top - 50 : uBound.bottom,
                    y2:dir > 0 ? uBound.top : uBound.bottom + 50
                }
                uBlank.mid = (uBlank.y1 + uBlank.y2)/2;
                uBlank.halfy = ((uBlank.y1 - uBlank.y2)/2);
                // console.log(uBlank);
                // console.log(p.y);
                var dy = (p.y - (dir>0?uBlank.y1:uBlank.y2))/(p.y - uBlank.mid) * uBlank.halfy;
                // console.log("dy=" + dy);
                var y = dy*dir + uBlank.mid + yOffset;
                // console.log(y)
                //第二个点
                this.points.push({
                    x : px + 20 + pAdjOffset,
                    y : y
                });
                //第三个点
                this.points.push({
                    x : endPos.x - 20,
                    y : (dir>0?uBlank.y2:uBlank.y1) + yOffset
                });
                // console.log("a=" + (dir>0?uBlank.y2:uBlank.y1) + "  b=" + yOffset)
                //第四个点
                this.points.push({
                    x : endPos.x - 20,
                    y : endPos.y
                });
                //从u连到p则需要把数据逆向排序下
                if(this.start.type == "U"){
                    this.points.reverse()
                }
            break;
            default:
//            	console.log(startPos.x + ", " + endPos.x)
                this.points.push({
                    x:startPos.x+Math.min(40,(endPos.x-startPos.x)/3),
                    y:startPos.y
                });
                this.points.push({
                    x:endPos.x-Math.min(40,(endPos.x-startPos.x)/3),
                    y:endPos.y
                });
        }
    },
    /**
     * 鼠标按下事件
     * @event onmousedown
     * @param {Object} curPos 鼠标坐标
     *      @param {Number} curPos.x 鼠标x坐标
     *      @param {Number} curPos.y 鼠标y坐标
     */
    onmousedown : function(curPos) {
//    	console.log("Zlink.isCursorInside = " + this.isCursorInside)
//    	console.log("\tpre   Zlink.selected = " + this.selected)
        if(this.isCursorInside){
        	if(!!TL.selectedLink && this != TL.selectedLink){
            	TL.selectedLink.selected = false;
        	}
            // console.log(this.name + ".onmousedown()");
            this.selected = !this.selected;
        }else{
            this.selected = false;
        }
//    	console.log("\tafter Zlink.selected = " + this.selected)
        //如果选中了，则设置TL的选中Link
    	if(this.selected){
    		TL.selectedLink = this;
    	}
        return this.isCursorInside;
    },
    setConnType:function(connType){
    	this.connType = connType;
    	if(connType == 2
    			|| connType == 4
    			|| connType == 6){
    		this.text = this.end.text;
    	}
//    	console.log(this.text);
    }
});
//碰撞宽度，距离线段 XX 像素之内都算碰到
var HITDISTANCE = 6;
/**
 * 检测一个点是否在一条线段上 
 * @method hitLine
 * @private
 * @param {Object} x1 线段起点坐标X
 * @param {Object} y1 线段起点坐标Y
 * @param {Object} x2 线段终点坐标X
 * @param {Object} y2 线段终点坐标Y
 * @param {Object} tx 检测点坐标X
 * @param {Object} ty 检测点坐标Y
 */
function hitTest(x1, y1, x2, y2, tx, ty) {
    var dx = x2 - x1;
    var dy = y2 - y1;
    var length = Math.sqrt(dx * dx + dy * dy);
    //计算投影比例 利用点乘公式中的投影长度来计算
    var percent = (dx * (tx - x1) + dy * (ty - y1)) / (length * length);
    //计算点到直线距离，利用面积公式 S=(Va 叉乘 Vb)/2=(长度*高度)/2 来计算
    var distance = (dx * (ty - y1) - dy * (tx - x1)) / length;
    return (percent > 0 && percent < 1 && Math.abs(distance) < HITDISTANCE);
}

/**
 * SampleLink 的构造函数
 * 样例连接线，仅在选择了一个Port之后出现
 * 直接连接
 * @class SampleLink
 * @extends Link
 * @constructor
 */
var SampleLink = Link.extend({
    name:"SampleLink",
    /**
     * SampleLink 的初始化函数
     * @method init
     */
    init : function() {
    },
    /**
     * 判断鼠标是否在区域内
     * @method inRegion
     * @param {Number} x 鼠标x坐标
     * @param {Number} y 鼠标y坐标
     * @return {Boolean} 总是返回False
     */
    inRegion:function(x,y){
        //TODO: 实现鼠标选择！！
        return false;
    },
    /**
     * SampleLink 绘制函数
     * 仅在选择了一个Node时显示
     * @method draw
     * @param {CanvasRenderingContext2D} ctx Canvas绘制上下文
     */
    draw:function(ctx){
        //仅在选择了一个Port之后出现
        if(!!TL.startNode){
            //先计算各个中间点
            this.update();
            //再按照点路径进行绘制
            ctx.beginPath();
            ctx.strokeStyle = "blue"
            ctx.moveTo(this.start.x, this.start.y);
            // for (var i=0; i < this.points.length; i++) {
                // var p = this.points[i];
                // ctx.lineTo(p.x, p.y);
            // };
            ctx.lineTo(this.end.x, this.end.y);
            ctx.closePath();
            ctx.stroke();
        }
    },
    /**
     * 更新Link节点路径
     * @method  update
     * 
     */
    update:function(){
        if(!!TL.startNode){
            this.start = TL.startNode.getAbsPos();
            this.end = TL.curPos;
        }
    },
    onmousedown : function(curPos) {
    	
    }
});

/**
 * Widget 的构造函数
 * @module Controls
 * @class Widget
 * @extends IObject
 * @constructor
 */
var Widget = IObject.extend({
    name:"Widget",
    //列表item之间的间隔
    snap:0,
    //item的背景
    bgColor:0xffffdd,
    //item的文字颜色
    textColor:0x000033,
    /**
     * 数据存储区，为一个数组，内部元素显示通过renderer来实现 
     * @property {Port} port
     */
    port:null,
    parent:null,
    /**
     * Widget的绝对坐标
     * @property {Object} absPos
     */
    absPos:null,
    /**
     * Widget 的初始化函数
     * @method init
     * @param {Object} cfg 配置参数
     * 
     */
    init : function(cfg) {
        //调用父类
        this._super(cfg);
        this.absPos = {x:0,y:0};
        this.items = [];
    },
    /**
     * 计算大小
     * @method measureItem
     * @param {String} v  显示的文字
     */
    measureItem:function(v){
        var met = TL.fg.measureText(v);
        return {
            width : met.width,
            height : this.height
        }
    },
    /**
     * 绘制Widget
     * @method draw
     * @param {Object} ctx
     */
    draw:function(ctx){
        if(!!ctx){
            ctx.fillStyle="rgba(255,255,200,0.5)";
            ctx.fillRect(this.x, this.y, this.width, this.height);
        }
    },
    /**
     * 获取Widget的边界
     * @method  getBounds
     * @return {Object} 返回Widget的边界，用于计算占用区域
     */
    getBounds:function(){
        return {
            left:this.x,
            right:this.x + this.width,
            top:this.y,
            bottom:this.y+this.height
        }
    },
    /**
     * 添加端口
     * @method addPort
     * @param {Object} cfg Port配置参数
     * 		@param {Object} cfg.
     * 		@param {Object} cfg
     * 		@param {Object} cfg
     */
    addPort:function(cfg){
        var pt = new Port(cfg);
        this.items.push(pt);
        pt.parent = this;
    },
    /**
     * 绘制Port 
     * @method drawPort
     * @param {Object} ctx
     */
    drawPort:function(ctx){
        for(var i=0,len = this.items.length;i<len;i++){
            this.items[i].draw(ctx);
        }
    },
    /**
     * 鼠标按下事件
     * @event onmousedown
     * @param {Object} curPos 鼠标坐标
     *      @param {Number} curPos.x 鼠标x坐标
     *      @param {Number} curPos.y 鼠标y坐标
     */
    onmousedown : function(curPos) {
        if(this.isCursorInside){
            // console.log("\t" + this.name + ".onmousedown() items count = " + this.items.length);
            var cf = true;
            for(var i=0,len = this.items.length; cf && i<len;i++){
                // console.log("#" + (i+1) + " - item.onmousedown")
                cf = !this.items[i].onmousedown(curPos);
            }
        }
        return this.isCursorInside;
    },
    /**
     * 鼠标移动事件
     * @event onmousemove
     * @param {Object} curPos 当前坐标
     *      @param {Number} curPos.x 鼠标x坐标
     *      @param {Number} curPos.y 鼠标y坐标
     * @param {Object} delta  鼠标偏移
     *      @param {Number} delta.x 偏移量x
     *      @param {Number} delta.y 偏移量y
     */
    onmousemove : function(curPos, delta) {
        return this.isCursorInside;
    },
    /**
     * 鼠标松开事件
     * @event onmouseup
     * @param {Object} curPos 鼠标当前坐标
     *      @param {Number} curPos.x 鼠标x坐标
     *      @param {Number} curPos.y 鼠标y坐标
     */
    onmouseup : function(curPos) {
        if(this.isCursorInside){
            // console.log("\t" + this.name + ".onmouseup()");
            var cf = true;
            for(var i=0,len = this.items.length; cf && i<len;i++){
                cf = !this.items[i].onmouseup(curPos);
            }
        }
        return this.isCursorInside;
    },
    /**
     * 判断鼠标是否在区域内
     * @method inRegion
     * @param {Number} x 鼠标x坐标
     * @param {Number} y 鼠标y坐标
     */
    inRegion:function(x,y){
        this._super(x, y);
        for(var i=0,len = this.items.length; i<len; i++){
            this.items[i].isCursorInside = false;
        }
        for(var i=0,len = this.items.length; !this.isCursorInside && i<len; i++){
            this.isCursorInside |= this.items[i].inRegion(x, y);
        }
        return this.isCursorInside;
    },
    updateLines:function(){
        for(var i=0,len = this.items.length; i<len; i++){
            var port = this.items[i];
            if(port.link){
                port.link.update();
            }
        }
    }
});
/**
 * 工厂函数，用于快速创建对象
 * @param {Object} cfg 对象的配置参数
 * @param {String} cfg.NAME 对象的名称
 * @return {OTDR|OSW} 返回创建的OTDR/OSW对象
 */
Widget.create = function(cfg){
	switch(cfg.NAME){
	case "OTDR":
		return new OTDR(cfg);
	break;
	default:
		return new OSW(cfg);
	break;
	}
}/**
 * Port对象构造函数
 * @module Controls
 * @class Port
 * @constructor
 * @param {Object} cfg 位置对象
 */
var Port = IObject.extend({
    name:"Port",
    pos:null,
    /**
     * 连接的对象
     * @property {Object} link
     */
    link : null,
    parent : null,
    occupied : false,
    text:"@",
    /**
     * 端口类型
     * <li>f - 光纤</li>
     * <li>u - OTDR输出/OSW输入</li>
     * <li>p - OSW输出</li>
     * @property {String} type 
     */
    type : "p",
    /**
     * Port 的初始化函数
     * @method init
     * @param {Object} cfg 配置参数
     * 
     */
    init : function(cfg) {
        //固定Port大小
        cfg.width = 12;
        cfg.height = 12;
        //调用父类
        this._super(cfg);
        //由于Port对象以中心为坐标，所以region要做相应的偏移
        this.region = new Region(this.x-6, this.y-6, cfg.width, cfg.height);
        //注册Port对象
        LM.regPort(this.type, this);
    },
    
    /**
     * 判断 Port 对象是否可见
     * @method isVisible
     * @return {Boolean} 返回true表示Port可见
     */
    isVisible : function(){
        return this.region.inRegion(this.pos.x, this.pos.y);
    },
    /**
     * 设置父对象
     * @method draw
     * @param {Object} ctx Canvas的2dContext
     */
    draw:function(ctx){
        this.update();
        if(!!ctx){
            ctx.save();
            if(this.isCursorInside || this == TL.startNode){
                ctx.fillStyle="rgba(100,100,255,0.7)";
                ctx.fillRect(this.x - 7, this.y - 7, this.width + 2, this.height + 2);
                ctx.fillStyle="white";
                ctx.fillRect(this.x-6, this.y-6, this.width, this.height);
            }
            var isPossible = (TL.startNode == this) || (!TL.possiblePortType) || TL.possiblePortType.indexOf(this.type)>-1 && !TL.isSameFiber();
//            isPossible &= (TL.startNode.parent != this.parent)
//            var state = LM.isPortOccupied(this);
            ctx.fillStyle= this.occupied ?
            		(this.isCursorInside?"red":"rgba(200,200,250,0.5)")
            		:(isPossible?"rgba(100,250,100,0.5)":"red");
            ctx.fillRect(this.x-4, this.y-4, this.width-4, this.height-4);
            ctx.restore();
        }
    },
    /**
     * 获取Widget的边界
     * @method  getBounds
     * @return {Object} 返回Widget的边界，用于计算占用区域
     */
    getBounds:function(){
        return {
            left:this.x,
            //右侧宽度必须加上延伸出去的端口长度
            right:this.x + this.width, 
            top:this.y,
            bottom:this.y+this.height
        }
    },
    /**
     * 鼠标按下事件
     * @event onmousedown
     * @param {Object} curPos 鼠标坐标
     *      @param {Number} curPos.x 鼠标x坐标
     *      @param {Number} curPos.y 鼠标y坐标
     */
    onmousedown : function(curPos) {
        if(this.isCursorInside){
        	//如果之前没有选中
            if(!TL.startNode){
//            	console.log("之前没有选中节点")
            	//当前的也没有被占用
            	if(!this.occupied){
            		TL.startNode = this;
//                	console.log("\t设置为当前节点...")
                	//名称类型对应表
                	var possibleTypes = {
            			f:["F","U"],	//左侧List
            			u:["U"],		//OTDR输出端
            			U:["f","u","p"],//OSW输入端
            			p:["F","U"],	//OSW输出端
            			F:["f","p"]		//右侧List
                	}
//                	console.log(this.type);
                	//设置可能类型，增加指向性
                	TL.possiblePortType = possibleTypes[this.type];
//                	console.log("\t下一个可能节点类型为：" + TL.possiblePortType)
            	}
            }else{
//            	console.log("之前选中了某节点")
                //创建ZLink
            	var sn = TL.startNode;
            	var en = this;
            	TL.startNode = null;
            	TL.possiblePortType = null;
            	//如果点击的是本身则取消选择
            	if(sn == en || sn.occupied || en.occupied || (sn.parent == en.parent)){
            		return;
            	}
//            	console.log("\t检测下来可以连接")
            	var rawConType = sn.type > en.type ?
                		en.type + sn.type:
                			sn.type + en.type;
            	//名称类型对应表
            	var conTypes = {
            			Uf:3, //光缆-OSW
            			Ff:5, //光缆-光缆
            			Uu:1, //OTDR-OSW
            			Up:6, //OSW-OSW
            			Fp:2 //OSW-光缆  分为RTU/CTU
            	}
            	var realType = conTypes[rawConType] || 0;
            	//CTU情况下OSW-光缆 为4 ，RTU情况下OSW-光缆 为2
            	if(realType == 2 && !TL.isRTU){
            		realType = 4;
            	}
            	//当类型为光缆-光缆 且 是同一个光缆时，取消
            	if(realType == 5 && TL.isSameFiber()){
            		return;
            	}
//            	console.log("rawConType = " + rawConType + "  realType = " + realType);
//            	console.log("sn.Pos = " + sn.getAbsPos().x + ", " + sn.getAbsPos().y);
//            	console.log("en.Pos = " + en.getAbsPos().x + ", " + en.getAbsPos().y);
            	//设置A节点Z节点，保证位置OK,不对则交换节点
                if((sn.type != "p" && realType == 6) || 
                		(sn.getAbsPos().x > en.getAbsPos().x)){
//                	console.log("swap!!!!!!!")
                	var tmp = sn;
                	sn = en;
                	en = tmp;
                }
                //当类型OK时，创建link
            	if(!!realType){
                    TL.addLink({
                    	startNode: sn,
                    	endNode: en,
                    	connType:realType
                    });
            	}
            }
        }
        return this.isCursorInside;
    },
    /**
     * 鼠标移动事件
     * @event onmousemove
     * @param {Object} curPos 当前坐标
     *      @param {Number} curPos.x 鼠标x坐标
     *      @param {Number} curPos.y 鼠标y坐标
     * @param {Object} delta  鼠标偏移
     *      @param {Number} delta.x 偏移量x
     *      @param {Number} delta.y 偏移量y
     */
    onmousemove : function(curPos, delta) {
        return this.isCursorInside;
    },
    /**
     * 鼠标松开事件
     * @event onmouseup
     * @param {Object} curPos 鼠标当前坐标
     *      @param {Number} curPos.x 鼠标x坐标
     *      @param {Number} curPos.y 鼠标y坐标
     */
    onmouseup : function(curPos) {
        if(this.isCursorInside){
            // console.log("\t\t" + this.name + ".onmouseup()");
            // console.log(this.region);
            // TL.endNode = this;
        }
        return this.isCursorInside;
    },
    /**
     * 获取Port的绝对坐标 
     * @method getAbsPos
     * @return 返回Port的绝对坐标
     */
    getAbsPos:function(){
        var pp = this.parent.parent;
        sx = pp.cx + pp.offset.x + this.x;
        sy = pp.cy + pp.offset.y + this.y;
        return{
            x:sx,
            y:sy
        }
    },
    isVisible:function(){
        var pp = this.parent.parent;
        var y = pp.offset.y + this.y;
        return y>0 && y<pp.ch;
    }
});/**
 * OTDR 的构造函数
 * @module Controls
 * @class OTDR
 * @extends Widget
 * @namespace Widget
 * @constructor
 */
var OTDR = Widget.extend({
    name:"OTDR",
    /**
     * 数据存储区，为一个数组，内部元素显示通过renderer来实现 
     * @property {Port} port 端口对象
     */
    port:null,
    SLOT_NO:-1,
    /**
     * Widget 的初始化函数
     * @method init
     * @param {Object} cfg 配置参数
     * 
     */
    init : function(cfg) {
        //固定OTDR大小
        cfg.width = 64;
        cfg.height = 48;
        cfg.x = cfg.y = 5;
        cfg.id = cfg.UNIT_ID;
        //调用父类
        this._super(cfg);
        this.addPort({
            x:this.x + this.width + 16,
            y:this.y + this.height/2,
            id:this.id,
            type:"u"
        });
    },
    /**
     * 计算大小
     * @method measureItem
     * @param {String} v  显示的文字
     * @return {Object} 返回对象的大小
     */
    measureItem:function(v){
        var met = TL.fg.measureText(v);
        return {
            width : met.width,
            height : this.height
        }
    },
    /**
     * 绘制Widget
     * @method draw
     * @param {Object} ctx 绘制上下文
     */
    draw:function(ctx){
        //console.log("------------");
        if(!!ctx){
            // console.log(String.format("{0},{1} - {2},{3}",this.x, this.y, this.width, this.height));
            //绘制边框以及背景
            var align = ctx.textAlign;
            ctx.strokeStyle="#000";
            ctx.fillStyle="rgba(255,255,230,1)";
            ctx.lineWidth = 1;
            ctx.textAlign = "center";
            ctx.fillRect(this.x, this.y, this.width, this.height);
            ctx.strokeRect(this.x, this.y, this.width, this.height);
            //绘制文字，如果需要居中则把后面的1.5改成2
            ctx.strokeText("OTDR", this.x + this.width / 2, this.y + this.height / 2);
            ctx.strokeText(this.SLOT_NO, this.x + this.width / 2, this.y + this.height / 2 + 12);
            ctx.textAlign = align;
            // 画伸出去的线            
            ctx.fillStyle="#000";
            ctx.fillRect(this.x + this.width, this.y + this.height/2, 16, 1);
            //画端口
            this.drawPort(ctx);
            //一些检测，主要用于debug
            if(this.isCursorInside && SHOW_HOVER){
                ctx.fillStyle="rgba(200,200,200,0.5)";
                ctx.fillRect(this.x, this.y, this.width, this.height);
            }
        }
    },
    /**
     * 获取Widget的边界
     * @method  getBounds
     * @return {Object} 返回Widget的边界，用于计算占用区域
     */
    getBounds:function(){
        return {
            left:this.x,
            //右侧宽度必须加上延伸出去的端口长度
            right:this.x + this.width + 40, 
            top:this.y,
            bottom:this.y+this.height
        }
    }
});

/**
 * OSW 的构造函数
 * @module Controls
 * @class OSW
 * @extends Widget
 * @namespace Widget
 * @constructor
 * @param cfg
 */
var OSW = Widget.extend({
    name:"OSW",
    /**
     * OSW光端口数，不计输入光口
     * @property {Number} portCount
     */
    portCount:8,
    SLOT_NO:-1,
    text:"",
    /**
     * Widget 的初始化函数
     * @method init
     * @param {Object} cfg 配置参数
     * 
     */
    init : function(cfg) {
        //固定OSW大小
    	cfg.portCount = cfg.PORT_COUNT;
        cfg.width = 64;
        cfg.height = cfg.portCount * 12 + 12;
        cfg.id = cfg.UNIT_ID;
        //调用父类
        this._super(cfg);
        var portIds = cfg.PORT_IDS.split(",");
        for (var i=0; i < this.portCount; i++) {
//        	console.log("add port " + i);
            this.addPort({
                x : this.x + this.width + 60,
                y : this.y + i*12 + 18,
                //快速转化成数字+取整
                id : ~~portIds[i],
                type : "p"
            });
        };
        //添加输入口
        this.addPort({
            x:this.x - 16,
            y:this.y + this.height / 2,
            id:this.id,
            type:"U",
            text:cfg.NAME
        });
    },
    /**
     * 绘制Widget
     * @method draw
     * @param {Object} ctx 绘制上下文
     */
    draw:function(ctx){
        //console.log("------------");
        if(!!ctx){
            // console.log(String.format("{0},{1} - {2},{3}",this.x, this.y, this.width, this.height));
            //绘制边框以及背景
            var align = ctx.textAlign;
            ctx.strokeStyle="#000";
            ctx.fillStyle="rgba(255,255,230,1)";
            ctx.lineWidth = 1;
            ctx.textAlign = "center";
            ctx.fillRect(this.x, this.y, this.width, this.height);
            ctx.strokeRect(this.x, this.y, this.width, this.height);
            //绘制文字，如果需要居中则把后面的1.5改成2
            ctx.strokeText("OSW", this.x + this.width / 2, this.y + this.height / 2);
            ctx.strokeText(this.SLOT_NO, this.x + this.width / 2, this.y + this.height / 2 + 12);
            ctx.textAlign = align;
            ctx.fillStyle="#000";
            ctx.fillRect(this.x - 16, this.y + this.height / 2, 16, 1);
            for (var i=0; i < this.portCount; i++) {
                ctx.fillRect(this.x + this.width, this.y + i*12+18, 60, 1);
                // ctx.fillRect(this.x + this.width + 10, this.y - 5 + i*12+18, 9, 10);
                ctx.strokeText((i+1) + "." , this.x + this.width + 5, this.y + i*12+18);
                if(!!this.items[i].linkText){
                	ctx.strokeText(this.items[i].linkText , this.x + this.width + 20, this.y + i*12+18);
                }
//                
            };
            //画端口
            this.drawPort(ctx);
            // var img = TL.getImage("port");
            // ctx.drawImage(img, this.x + this.width + 10, this.y + this.height/2 - 8);
            if(this.isCursorInside && SHOW_HOVER){
                ctx.fillStyle="rgba(200,200,200,0.5)";
                ctx.fillRect(this.x, this.y, this.width, this.height);
            }
        }
    },
    /**
     * 获取Widget的边界
     * @method  getBounds
     * @return {Object} 返回Widget的边界，用于计算占用区域
     */
    getBounds:function(){
        return {
            left:this.x,
            //右侧宽度必须加上延伸出去的端口长度
            right:this.x + this.width + 80, 
            top:this.y,
            bottom:this.y+this.height
        }
    }
});

/**
 * ListItem 的构造函数,
 * ListView内显示的条目
 * @module Controls
 * @class ListItem
 * @extends Widget
 * @namespace Widget
 * @constructor
 */
var ListItem = Widget.extend({
    name:"ListItem",
    /**
     * 要显示的文字
     * @private
     * @property {String} displayText
     */
    displayText:"",
    /**
     * 光纤ID
     * @property {Number} fiberId
     */
    fiberId:-1,
    /**
     * 光纤索引
     * @property {Number} fiberIndex
     * @readOnly
     */
    fiberIndex:-1,
    /**
     * 光纤文字
     * @property {String} text
     */
    text:"",
    /**
     * 文字偏移
     * @private
     * @property {Number} textOffset
     */
    textOffset:24,
    /**
     * 文字滚动量
     * @private
     * @property {Number} rollPosition
     */
    rollPosition:24,
    /**
     * 文字滚动一圈停留时间
     * @private
     * @property {Number} rollDelay
     */
    rollDelay:24,
    /**
     * 文字宽度，自动计算生成
     * @private
     * @property {Number} textWidth
     */
    textWidth:24,
    /**
     * Port位置
     * @property {Number} portPos
     */
    portPos:POS.LEFT,
    //item的背景，留作后用
    bgColor:0xffffdd,
    //item的文字颜色，留作后用
    textColor:0x000033,
    /**
     * 父对象名称
     * @property {Object} parent
     */
    parent:null,
    /**
     * 端口位置 
     * @property {Port} port
     */
    port:null,
    portText:"#",
    /**
     * ListView 的初始化函数
     * @method init
     * @param {Object} cfg 配置参数
     *      @param {String} cfg.text 文字
     *      @param {Number} cfg.fiberId 光纤ID
     *      @param {Number} cfg.fiberIndex 光纤索引
     */
    init : function(cfg) {
//    	console.log(cfg);
        this.fiberId = cfg.id;
        this.fiberIndex = cfg.index;
        //调用父类
        this._super(cfg);
//        console.log(cfg);
        if(this.portPos == POS.RIGHT){
            this.textOffset = 4;
            //设置Port的位置
            this.addPort({
                x:this.x + this.width - 12,
                y:this.y+12,
                id:cfg.id,
                type:"f",
                text:cfg.portText,
                occupied:LM.isFiberOccupied("f", cfg.id)
            });
        }else{
            this.addPort({
                x:this.x + 12,
                y:this.y + 12,
                id:cfg.id,
                type:"F",
                text:cfg.portText,
                occupied:LM.isFiberOccupied("F", cfg.id)
            });
        }
        this.textWidth = this.measureText();
        // console.log(this.text);
    },
    /**
     * 计算显示的文字的宽度
     * @method measureText
     * @private
     * @param {String} v  ListItem 显示的文字
     * @return 返回显示的文字的宽度
     */
    measureText:function(){
        var met = TL.fg.measureText(this.text);
        return met.width;
    },
    /**
     * 绘制ListItem
     * @method drawItem
     * @param {Object} ctx
     */
    draw:function(ctx){
        ctx.save();
        ctx.fillStyle="black";
        ctx.strokeStyle="black";
        ctx.textBaseline = "top";
        // ctx.strokeRect(this.x,this.y,this.width,this.height);
        //绘制文字
            //实现长文字的循环滚动
            //文字长度足够 && 鼠标在他上面
        if(this.isCursorInside && (this.textWidth + 24 > this.width)){
            ctx.fillText(this.text, this.x + this.textOffset - this.rollPosition, this.y + 6);
            ctx.fillText(this.text, this.x + this.textOffset - this.rollPosition + this.textWidth + 24, this.y + 6);
            if(this.rollDelay>0){
                this.rollDelay--;
            }else{
                this.rollPosition++;
            }
            if(this.rollPosition > this.textWidth + 24){
                this.rollPosition = 0;
                this.rollDelay = 50;
            }
        }else{
            ctx.fillText(this.text, this.x + this.textOffset, this.y + 6);
        }
        //清除Port底色
        if(this.portPos == POS.RIGHT){
            ctx.clearRect(this.x + this.width - 24, this.y, 24, 24);
        }else{
            ctx.clearRect(this.x, this.y, 24, 24);
        }
        
        //绘制灰色Mask
        if(this.isCursorInside){
            ctx.fillStyle="rgba(200,200,200,0.3)";
            ctx.fillRect(this.x, this.y, this.width, this.height);
        }
        //绘制Port
        this.drawPort(ctx);
        ctx.restore();
    },
    /**
     * 判断鼠标是否在区域内
     * @method inRegion
     * @param {Number} x 鼠标x坐标
     * @param {Number} y 鼠标y坐标
     */
    inRegion:function(x,y){
        this._super(x, y);
        this.items[0].inRegion(x, y);
        return this.isCursorInside;
    }
});

var ui=ui||{};
/**
 * @module Misc
 * @class css
 * @constructor
 * @return 返回一个加载css的对象 
 */
var css = function () {
    return {
        /**
         * 在文档中加载css文件
         * @method load
         * @param {Object} e 文件地址
         * @param {Object} a document/null
         */
        load : function (e, a) {
            var a = a || document,
            c = a.createElement("link");
            c.type = "text/css";
            c.rel = "stylesheet";
            c.href = e;
            a.getElementsByTagName("head")[0].appendChild(c)
        },
        /**
         * 在文档中加载css代码
         * @method inject
         * @param {Object} e css代码
         * @param {Object} a document/null
         */
        inject : function (e, a) {
            var a = a || document,
            c = document.createElement("style");
            c.type = "text/css";
            c.innerHTML = e;
            a.getElementsByTagName("head")[0].appendChild(c)
        }
    }
}();

function log(v){
    console.log(v);
}
/**
 * 一个类似$的逗比玩意儿
 * @module Misc
 * @class e
 * @param {Object} id
 */
var e = function(id){
    var el = document.getElementById(id);
    return {
        createChild : function(type, cfg){
            // log(id + ".createChild -> " + type + "[" + cfg.id + "]");
            cfg = cfg || {};
            var child = document.createElement(type);
            for(var p in cfg){
                // log("Add -> cfg." + p)
                switch(p){
                case "id":
                    // log("\t" + p + " = " + cfg.id)
                    child.id = cfg.id;
                    break;
                case "css":
                    var style = child.style;
                    for(var s in cfg.css){
                        // log("\tStyle -> " + s + " = " + cfg.css[s])
                        style[s] = cfg.css[s];
                    }
                    break;
                default:
                    // log("\t" + p + " = " + cfg[p])
                    child[p] = cfg[p];
                }
            }
            // log(child);
            if(!!el){
                el.appendChild(child);
            }
            if(type == "canvas"){
                return child;
            }else{
                return this;
            }
        },
        /**
         * 获取对象的宽度
         * @method getWidth 
         */
        getWidth:function(){
            return el.clientWidth || el.width || el.style.width;
        },
        /**
         * 获取对象的高度
         * @method getWidth 
         */
        getHeight:function(){
            return el.clientHeight || el.height || el.style.height;
        },
        /**
         * 创建Ext的ComboBox
         * @method createCombo
         * @param {Object} cfg ComboBox的配置
         * @param {Object} cfg.cmbId ComboBox的对应日期Id
         * @param {Object} cfg.url ComboBox的Action URL
         * @param {Object} cfg.displayField ComboBox的显示字段
         * @param {Object} cfg.valueField ComboBox的值字段
         * @param {Object} cfg.callback ComboBox的选择回调
         */
        createCombo:function(cfg){
            var w = document.getElementById(cfg.cmbId).style.width;
            w=w.substr(0, w.length-2)>>0;
            // console.log(w);
            function loadDetail(url, field, id, callback){
            	var param = {};
            	param[field] = id;
    			Ext.Ajax.request({
    				url : url,
    				type : 'post',
    				params : param,
    				success : function(response, options){
    					var obj = Ext.decode(response.responseText);
//    					console.log(obj);
    					callback(obj);
    				},
    				failure:function(){
    					Ext.Msg.alert("提示：", "后台运行出错！");
    				},
    				error:function(){
    					Ext.Msg.alert("提示：", "后台运行出错！");
    				}
    			});
            }
            var store = new Ext.data.Store({
            	url : cfg.url,
            	reader : new Ext.data.JsonReader({
            		totalProperty : 'total',
            		root : "rows"
            	},[cfg.valueField, cfg.displayField]
            	),
            	listeners:{
            		'load':function(store,records,options){
            			if (records.length>0){
            				var obj = Ext.getCmp(cfg.id);
            				obj.setValue(records[0].data[cfg.valueField]);
            				obj.fireEvent('select',obj,records[0],0);
            			}
            		}
            	}
            });
//            store.load();
            var combo = new Ext.form.ComboBox({
            	id:cfg.id,
                typeAhead: true,
                triggerAction: 'all',
                lazyRender:true,
                mode: 'local',
                width:w,
                store: store,
                valueField: cfg.valueField,
                displayField: cfg.displayField,
                renderTo:cfg.cmbId,
                listeners:{
                	'select':function(me, rec, index){
                		var id = rec.get(cfg.valueField);
                		loadDetail(cfg.detailUrl, cfg.paramField, id, cfg.callback);
                	}
                }
            });
            return this;
        },
        /**
         * 设置对象的css样式
         * @method css(styleName, value)
         * @param {String} styleName 样式名称
         * @param {String} calue 样式值
         */
        css:function(styleName, value){
            el.style[styleName] = value;
        },
        /**
         * 设置对象的 innerHTML
         * @method html(value)
         * @param {String} calue innerHTML 的值
         */
        html:function(value){
            el.innerHTML = value;
        }
    }
};

ui = (function(){
	//左侧Combobox对应DIV的ID
    var _leftComboID = "lc_" + Ext.id();
	//右侧Combobox对应DIV的ID
    var _rightComboID = "rc_" + Ext.id();
	//中间Combobox对应DIV的ID
    var _middleComboID = "mc_" + Ext.id();
    //前台canvas的ID
    var _canvasID = "cvs_fg";
    //缓冲区canvas的ID
    var _bgCanvasID = "bgcvs_" + Ext.id();
    var cw,ch,lw,lh,rw,rh,mw,mh,lx,ly,rx,ry,mx,my;
    //分别对应左侧、右侧、中间Combobox
    var lc,rc,mc;
    //分别对应左侧、右侧、中间Combobox的Store
    var ls,rs,ms;
    
    function el(id){
        return document.getElementById(id);
    }
    
    if(!ui.initialized){
        ui.initialized = true;
        return {
            initDom:function(prtId){
                //添加左侧下拉列表
                e(prtId).createChild("div", {
                    id:_leftComboID,
                    css:{
                        width:"180px",
                        height:"22px",
                        position:"absolute",
                        left:"10px",
                        top:"5px",
                        background:"#ffcccc"
                    }
                })
                .createCombo({
                	id:"_tutu_leftCombo_",
                	cmbId:_leftComboID,
                	url:"external-connect!getCableInfo.action",
                	valueField:"CABLE_ID",
                	displayField:"CABLE_NAME_FTTS",
                	detailUrl:"external-connect!getFiberInfo.action",
                	paramField:"cableId",
                	callback : TL.loadLeftData
                });
                
                //添加右侧下拉列表
                e(prtId).createChild("div", {
                    id:_rightComboID,
                    css:{
                        width:"180px",
                        height:"22px",
                        position:"absolute",
                        right:"10px",
                        top:"5px",
                        background:"#ffcccc"
                    }
                })
                .createCombo({
                	id:"_tutu_rightCombo_",
                	cmbId:_rightComboID,
                	url:"external-connect!getCableInfo.action",
                	valueField:"CABLE_ID",
                	displayField:"CABLE_NAME_FTTS",
                	detailUrl:"external-connect!getFiberInfo.action",
                	paramField:"cableId",
                	callback : TL.loadRightData
                });
                //添加中间下拉列表 for RTU/CTU 列表
                e(prtId).createChild("div", {
                    id:_middleComboID,
                    css:{
                        width:"240px",
                        height:"22px",
                        margin:"5px auto",
                        background:"#ffffcc"
                    }
                })
                .createCombo({
                	id:"_tutu_midCombo_",
                	cmbId:_middleComboID,
                	url:"external-connect!getRcInfo.action",
                	valueField:"RC_ID",
                	displayField:"NUMBER",
                	detailUrl:"external-connect!getUnitInfo.action",
                	paramField:"rcId",
                	callback:TL.loadMidData
                });
                var w = document.getElementById(prtId).clientWidth;
                var h = document.getElementById(prtId).clientHeight;
                h -= 34;
                //创建canvas对象
//                var cvs = e(prtId).createChild("canvas", {
//                    id:_canvasID,
//                    height:h,
//                    width:w,
//                    css:{
//                        position:"absolute",
//                        color:"#ffffcc",
//                        top:"34px"
//                        // ,height:h+"px",
//                        // width:w+"px"
//                    }
//                });
                var cvs = document.getElementById("cvs_fg");
                cvs.width = w;
                cvs.height = h;
                //创建canvas对象
//                var bgCvs = e("我就是打酱油的你们不要太在意").createChild("canvas", {
//                    id:_bgCanvasID,
//                    height:h,
//                    width:w
//                });
                var bgCvs = document.getElementById("cvs_bg");
                bgCvs.width = w;
                bgCvs.height = h;
                return {
                    width:w,
                    height:h,
                    lx:10,
                    rx:w-180-10,
                    mx:w/2-120,
                    ly:0,
                    ry:0,
                    my:0,
                    lw:180,
                    lh:h-10,
                    rw:180,
                    rh:h-10,
                    mw:240,
                    mh:h-10,
                    cvs:cvs,
                    ctx:cvs.getContext("2d"),
                    bgCvs:bgCvs,
                    bgCtx:bgCvs.getContext("2d")
                }
            },
            initData:function(stationId){
            	if(!lc){
            		lc = Ext.getCmp("_tutu_leftCombo_");
            		ls = lc.getStore();
            	}
            	if(!rc){
            		rc = Ext.getCmp("_tutu_rightCombo_");
            		rs = rc.getStore();
            	}
            	if(!mc){
            		mc = Ext.getCmp("_tutu_midCombo_");
            		ms = mc.getStore();
            	}
            	ls.baseParams.stationId = stationId;
            	ls.load();
            	rs.baseParams.stationId = stationId;
            	rs.load();
            	ms.baseParams.stationId = stationId;
            	ms.load();
            },
            debug:function(){
            	console.log(lc);
            	console.log(ls);
            	console.log(rc);
            	console.log(rs);
            	console.log(mc);
            	console.log(ms);
            }
        }
    }else{
        return ui;
    }
})();

function getElementPos(e) {
    e = e || window.event;
    var obj = e.target || e.srcElement;
    var x = 0, y = 0;
    while (obj.offsetParent) {
        x += obj.offsetLeft;
        y += obj.offsetTop;
        obj = obj.offsetParent;
    }
    return {
        x : x,
        y : y
    };
}

function getAbsMousePos(e) {
    e = e || window.event;
    return {
        x : e.pageX || e.clientX + document.body.scrollLeft + document.documentElement.scrollLeft,
        y : e.pageY || e.clientY + document.body.scrollTop + document.documentElement.scrollTop
    };
}

function getMousePos(e) {
    var element = getElementPos(e);
    var mouse = getAbsMousePos(e);
    TL.curPos.x = mouse.x - element.x;
    TL.curPos.y = mouse.y - element.y;
}

function rndString(){
    var len = (Math.random()*20+5)>>0;
    var s= "";
    for(var i=0;i<len;i++){
        s+=String.fromCharCode(97+(Math.random()*26)>>0);
    }
    return s;
}
//yuidoc -c C:\HellGate\Workspace\JS\DeviceConnector\yuidoc.json
/*
文档生成命令：
cd /d C:\HellGate\Workspace\JS\DeviceConnector
smartdoc

*/
/**
 * 全局管理
 * @module fw
 * @class TL
 * @author 田洪俊
 */
var SHOW_HOVER = true;
var DEBUG = false;
var lv = null;
var aaa=null;
var TL = {
    objects : [],
    links : [],
    _lc:null,
    _mc:null,
    _rc:null,
    curStationId:-1,
    isRTU:false,
    /**
     * 事件监听对象
     * @property {Object} listeners
     */
    listeners:{},
    
    width : 800,
    height : 600,
    moving : false,
    startNode:null,
    endNode:null,
    /**
     * 可以选择的Port类型<br>
     * 在点击Port时进行更新
     * @property {String} possiblePortType
     */
    possiblePortType:null,
    fg:null,
    bg:null,
    bgCvs:null,
    /**
     * 鼠标当前坐标，由TL.onmousemove()实时更新 
     * @property {Object} curPos
     * 
     */
    curPos:{
        x:0,
        y:0
    },
    /**
     * 鼠标点击时的坐标，由TL.onmousedown()事件更新 
     * @property {Object} startPos
     */
    startPos:{
        x:0,
        y:0
    },
    /**
     * 鼠标上一个点坐标，由TL.onmousemove()实时更新 
     * @property {Object} lastPos
     */
    lastPos:{
        x:0,
        y:0
    },
    /**
     * 鼠标当前状态
     * @property {Number} mouseState
     */
    mouseState:MOUSE_STATE.NONE,
    // tip : new Tip(),
    /**
     * 构造函数
     * @method init
     * @param {String} prtId 父容器ID 
     */
    initDom:function(prtId){
        var param = ui.initDom(prtId);
//        console.log(param);
        TL.width = param.width;
        TL.height = param.height;
        //根据创建的Combobox位置创建相应的元素
        var lc = new ListView({
            x:param.lx,
            y:param.ly,
            width:param.lw,
            height:param.lh,
            portPos:POS.RIGHT,
            renderer:function(v){
            	//RESOURCE_FIBER_ID,FIBER_NO,FIBER_NAME,NOTE
            	var pre = DEBUG ? v.RESOURCE_FIBER_ID + "#" : "";
                return pre + v.FIBER_NO+". " + v.FIBER_NAME + " - " + v.NOTE;
            }
        });
        TL._lc = lc;
        //根据创建的Combobox位置创建相应的元素
        var rc = new ListView({
            x:param.rx-1,
            y:param.ry,
            width:param.rw,
            height:param.rh,
            portPos:POS.LEFT,
            renderer:function(v){
            	//RESOURCE_FIBER_ID,FIBER_NO,FIBER_NAME,NOTE
            	var pre = DEBUG ? v.RESOURCE_FIBER_ID + "#" : "";
                return pre + v.FIBER_NO+". " + v.FIBER_NAME + " - " + v.NOTE;
            }
        });
        TL._rc = rc;
        //根据创建的Combobox位置创建相应的元素
//        lv = rc;
        var mc = new Container({
            x:param.mx-50,
            y:param.my,
            width:param.mw+100,
            height:param.mh
            //,padding:"10 2 2"
        });
        TL._mc = mc;
        TL.objects.push(lc);
        TL.objects.push(rc);
        TL.objects.push(mc);
        TL.objects.push(new SampleLink());
        TL.fg = param.ctx;
        TL.bg = param.bgCtx;
        TL.cvs = param.cvs;
        TL.bgCvs = param.bgCvs;
        TL.regEvents();
        TL.regBufferImages();
        //添加一些测试数据
    },
    /**
     * TL数据初始化
     * @param {Number} stationId 局站ID
     * 
     */
    initData:function(stationId){
    	TL.curStationId = stationId;
    	ui.initData(stationId);
    },
    /**
     * 加载左侧List数据
     * @private
     * @method loadLeftData
     * @param {Object} dat 数据集
     * @param {Number} dat.total 数据集大小
     * @param {Array} dat.rows 数据集元素集合
     */
    loadLeftData:function(dat){
    	var len = dat.total;
        var lc = TL._lc;
        lc.clear();
    	LM.ports.f=[];
    	for(var i=0; i<len; i++){
    		/* RESOURCE_FIBER_ID,FIBER_NO,FIBER_NAME,NOTE */
    		lc.add(dat.rows[i]);
    	}
    	//移除显示
    	TL.links = [];
//    	TL.removeLinkByType([3,5]);
    	LM.generateLinks();
    },
    /**
     * 加载右侧List数据
     * @private
     * @method loadRightData
     * @param {Object} dat 数据集
     * @param {Number} dat.total 数据集大小
     * @param {Array} dat.rows 数据集元素集合
     */
    loadRightData:function(dat){
    	var len = dat.total;
        var rc = TL._rc;
        rc.clear();
    	LM.ports.F=[];
    	for(var i=0; i<len; i++){
    		/* RESOURCE_FIBER_ID,FIBER_NO,FIBER_NAME,NOTE */
    		rc.add(dat.rows[i]);
    	}
    	//移除显示
    	TL.links = [];
//    	TL.removeLinkByType([3,5]);
    	LM.generateLinks();
    },
    /**
     * 加载中间数据
     * @private
     * @method loadMidData
     * @param {Object} dat 数据集
     * @param {Number} dat.total 数据集大小
     * @param {Array} dat.rows 数据集元素集合
     */
    loadMidData:function(dat){
    	var len = dat.total;
        var mc = TL._mc;
        mc.clear();
    	LM.ports.u=[];
    	LM.ports.U=[];
    	LM.ports.p=[];
        //先看一遍有没有OTDR
        var hasOTDR = false;
    	for(var i=0; i<len; i++){
    		hasOTDR = hasOTDR || (dat.rows[i].NAME == "OTDR");
    	}
    	TL.isRTU = hasOTDR;
    	var oswX = hasOTDR ? 160 : 100;
    	var oswY = 5;
    	for(var i=0; i<len; i++){
    		//通过工厂创建对象并添加
    		var cfg = dat.rows[i];
    		cfg.x = oswX;
    		cfg.y = oswY;
    		var wgt = Widget.create(cfg);
    		mc.add(wgt);
    		if(cfg.NAME != "OTDR"){
    			oswY += wgt.height + 50;
    		}
    	}
    	//移除显示
    	TL.links = [];
//    	TL.removeLinkByType([1,3,6]);
    	LM.generateLinks();
    },
    /**
     * 注册事件
     * @method regEvents
     */
    regEvents : function() {
//        console.log("TL.regEvents()!");
        // TL.cvs.onclick = TL.onclick;
        TL.cvs.onmousedown = TL.onmousedown;
        TL.cvs.onmouseup = TL.onmouseup;
        TL.cvs.onmousemove = TL.onmousemove;
        document.onkeydown = TL.onkeydown;
    },
    /**
     * 鼠标点击事件
     * @event onclick
     * @param {Object} e 事件参数
     */
    onclick : function(e) {
        console.log("TL.onclick");
        for (var i = 0; i < TL.objects.length; i++) {
            var o = TL.objects[i];
            if(o instanceof IObject){
                //o.onmousedown(TL.curPos);
            }
        }
        for (var i = 0; i < TL.links.length; i++) {
            
        }
    },
    /**
     * 鼠标按下事件
     * @event onmousedown
     * @param {Object} e 事件参数
     */
    onmousedown : function(e) {
    	if(e.button != 0){
    		return true;
    	}
        //更新当前坐标
        getMousePos(e);
        var cp = TL.curPos;
        TL.startPos.x = cp.x;
        TL.startPos.y = cp.y;
        var x = cp.x;
        var y = cp.y;
        //将上次坐标更新到当前坐标，以防出bug
        TL.lastPos.x = cp.x;
        TL.lastPos.y = cp.y;
        TL.mouseState = MOUSE_STATE.MOUSEDOWN;
        // TL.startNode = null;
        // TL.endNode = null;
        //事件分发
        var continueFlag = true;
        //  Link的点击事件不需要拦截
        TL.selectedLink = null;
        for (var i = TL.links.length - 1; i >= 0; i--){
            var o = TL.links[i];
            // console.log(o);
            if(o instanceof ZLink){
                continueFlag &= !o.onmousedown(cp);
            }
        };
        for (var i = TL.objects.length - 1; continueFlag && i >= 0; i--){
            var o = TL.objects[i];
            // console.log(o);
            if(o instanceof IObject){
                continueFlag = !o.onmousedown(cp);
            }
        };
        for (var i = 0; i < TL.links.length; i++) {
        }
        //阻止事件冒泡，
        if (e && e.stopPropagation){
            e.stopPropagation();    
        }
        else{
            e.cancelBubble=true;
        }
        TL.fireEvent("mouse_down", cp);
        return false;
    },
    /**
     * 鼠标松开事件
     * @event onmouseup
     * @param {Object} e 事件参数
     */
    onmouseup : function(e) {
        //console.log("TL.onmouseup");
        TL.fireEvent("mouse_up", TL.curPos);
        TL.mouseState = MOUSE_STATE.NONE;
        //事件分发
        var continueFlag = true;
        for (var i = TL.objects.length - 1; continueFlag && i >= 0; i--){
            var o = TL.objects[i];
            // console.log(o);
            if(o instanceof IObject){
                continueFlag = !o.onmouseup(TL.curPos);
            }
        };
    },
    /**
     * 鼠标移动事件
     * @event onmousemove
     * @param {Object} e 事件参数
     */
    onmousemove : function(evt) {
        // console.log("TL.onmousemove");
        TL.lastPos.x = TL.curPos.x;
        TL.lastPos.y = TL.curPos.y;
        //更新当前坐标
        getMousePos(evt);
        var x = TL.curPos.x;
        var y = TL.curPos.y;
        //只要不是空状态，那么就是拖动状态
        TL.mouseState = (TL.mouseState != MOUSE_STATE.NONE ? MOUSE_STATE.DRAG : MOUSE_STATE.NONE);
        //鼠标坐标碰撞测试
        var continueFlag = true;
        for (var i = TL.links.length -1; continueFlag && i >= 0; i--) {
            var o = TL.links[i];
            continueFlag = !o.inRegion(x, y);
        }
        for (i = TL.objects.length -1; continueFlag && i >= 0; i--) {
            o = TL.objects[i];
            continueFlag = !o.inRegion(x, y);
        }
        //事件分发
        //  先来一发坐标偏移
        var delta = TL.mouseState == MOUSE_STATE.DRAG ? TL.getDelta() : null;
        for (i = 0; i < TL.objects.length; i++) {
            o = TL.objects[i];
            if(o instanceof IObject){
                o.onmousemove(TL.curPos, delta);
            }
        }
        for (i = 0; i < TL.links.length; i++) {
            o = TL.objects[i];
            if(o instanceof IObject){
                o.onmousemove(TL.curPos, delta);
            }
        }
        //鼠标坐标显示
        e("inspector").html("[" + x + ", " + y + "]");
        if (evt && evt.stopPropagation){
            evt.stopPropagation();    
        }
        else{
            evt.cancelBubble=true;
        }
    },
    onkeydown:function(e){
        // console.log(e.keyCode);
        // console.log(e);
        switch(e.keyCode){
            case 46:
            TL.deleteLink();
            break;
            default:
//           console.log("------------------");
        }
    },
    /**
     * 绘制图形
     * @method draw
     */
    draw : function() {
        var g = TL.bg;
        g.clearRect(0, 0, TL.width, TL.height);
        for (var i = 0, len = TL.objects.length; i < len; i++) {
            // TL.objects[i].update();
            TL.objects[i].draw(g);
        }
        for (i = 0, len = TL.links.length; i < len; i++) {
            // TL.links[i].update();
            TL.links[i].draw(g);
        }
        var fg = TL.fg;
        fg.clearRect(0, 0, TL.width, TL.height);
        fg.drawImage(this.bgCvs,0,0);
    },
    /**
     * 显示提示信息
     * @method showTip
     * @param {Object} pos 位置
     * @param {String} title 消息标题
     * @param {String} info 信息内容
     */
    showTip : function(pos, title, info) {
        TL.tip.setGPS(pos.Lng, pos.Lat);
        TL.tip.visible = true;
        TL.tip.setTitle(title);
        TL.tip.setText(info);
    },
    /**
     * 绘制循环，调用requestAnimationFrame方法，实现高效绘图
     * @method render
     */
    render:function(){
        TL.draw();
        window.requestAnimationFrame(TL.render);
    },
    // 图片缓存
    imgs : {},
    // Base64图片编码字符
    base64imgs : {
        up : "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAADP0lEQVQ4T5VTa0iTURh+vl2+Xd3FVrs4y8u0IjArKudMLcJa4dJpUdBFW5YFRtndoszC+hW0kEi6kJUZM5N+FRGtiAqLIUVRTVtbpqUubc52c1vfRIcR/eiF8+NweJ/zvs+FwN9Fy83NJVksFpfH43GpZzpVPqqGZTKZt76+PjCxhZhwIdRqNVsikcikUsmCGapUtUAUIyEQZvhHgp7unu+dHz92mN1u9wePxzNgNptHIr1RAK1WKxAKhXOzszVlxUXFOpZIxne6gBADEFBz8GkBvH/dbr3ReOuiw+FoEYvF9sg0owDUz5z4+PiMMkPJqcw87cLGR31offwNn/pI8IR8JCtCmJMQRJFmKuSkJ3Du3FmjxdJuNJlMXREAmk6nS6JO7brNhtVHr3Wi4XkA7nAICUI2iudz0d4ZBp30QSnywrAsFQr6d3f18RO7+/v7TZG9OelpaUU1x4/UNb8RCM60WOEFGzQOgcubE7BkFhemZy40v+wDj0kgQTyC7fpUvLjf+uBqw/WdRH5+viRLozlUXlFRWXLWAbMtBtNEQzAalFg0nR/l+M7LAdy1uMCjh1GgmQYV29ZbdfjIekKv1ytzsrNqlm8sK117rB0OTxz08xioL49HR48fyTISDmcAchETNa3d6HL6sDhNCm3ST9fefQe2RAFWGraVrj3chq+/4qCdy4ZiMgl6iED1minY1dAN7Wwu2mxedPW4kZOmQF7qoKtyz8Et0RVKK3ZVbjVa0faZDT7Hjy/uII6tkONAoQQ7rvTim2sYk/lh8PxB6LNSoIz51FtVRa0wSmJ6elEtReLNd2LByaYOMEkSg4Eg9ufJUVUgxp5LA/g65AWLUkIVG0K5Pgkv7o2ROC5jISVjvqF0de11O24+HYaXZGISg4NZciKiCUJECHJxGBuWT4WC2eM+WT0m47iREhMTM0o2bTytXrpswa0nLjQ96Yaz9wdiYxlQThJjpkqAVZlSxDGGAufr6oyvLJaokUalGrfy4pzsbQX6Qh1bKOH++BXJDR0CFgk+I4y3by22xsamC3a7/fYfVh4TOxomhUKakaRKyeTyYmJpNND9Pp93wOm0Wa0fHg4O/iNME1L5X3H+DWVEV/q2PdRcAAAAAElFTkSuQmCC",
        down : "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAADPklEQVQ4T5VTaUiTYRz/vTvene6w1Q5nqS0rArOics7UIqwVLp0aBh3asiwwym6LMgvrU9BCIukgKzPWIX0qIloSJRZDiqKatrZMS13anO1yW+9EhxF96P/t4eH/e57fReDvoWVnZ5MsFovL4/G41DWdGh81wzKZzFtfXx+YuEJMOBBqtZotkUhkCoU0LUk1I53Li4ml0UD3+3zeAafTZrV+eDw46P7g8XgGzGbzSGQ3CqDVagVCoXD+0qzMbXn6fB1bKOH++BV5jA4BiwSfEcbbtxZbY2PTBbvdfkcsFtsjvxkFoF7mJCYmppVs2nhavXzFolstLjS1dMPZ+wOxsQwoJ4kxWyXAmnQp4hhDgfN1dcZXFovRZDJ1RQBoOp0uKV+nq801lBbVXrfj5rNheEkmJjE4mCMnwAYNISIEuTiMDSunQsHscZ+sPrG7v7/fFOHNSU1NLag9fqTu5jux4GRTB5gkicFAEPtz5KjKE2PPpQF8HfKCRfqgig2hXJ+E1gfNj642XN9J5ObmSjI0mkOlFbsqtxqtaPvMBp/jxxd3EMdWyXEgX4IdV3rxzTWMyfwweP4g9BkzoIz51FtVdWQ9odfrlVmZGTWrDdtKiw+34euvOGjns6GYTIIeIlC9dgp2NXRDO5eLNpsXXT1uZKUokJM86Krcc3BLFGDlxrLS4mPtcHjioF/AQH15PDp6/JguI+FwBiAXMVHT3I0upw9LU6TQJv107d13YEuUQnlFRWXJWQfMthhMEw3BaFBiyUx+NCb3Xg7gvsUFHj2MPM00qNi23qrDFIVREVNSCmooEW+/EQjO3LXCG9GdQ+Dy5gQsm8OF6bkLt1/2gcckkCAewXZ9Mlofjok4biNlZe26zYaio9c60fAiAHc4hAQhG4ULuWjvDINOOaAUeWFYkQwF/bu7+viYjeNBio+PTyszlJxKz9EubnzSh+an3/CpjwRPyMd0RQjzEoIo0EyFnPQEzp07a7RY2qNBGuU5HuXMTE1ZYUGhjiWS8Z0uIMQABFSl+LQA3r9ut95ovHXR4XDc/SPKY0pFyySVShbNUiWrBaIYCYEwwz8S9HT3fO/8+LHD7Hb/o0wTWvlfdf4NzupX+v/vzKIAAAAASUVORK5CYII=",
        left : "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAADmElEQVQ4T2VTa0xbZRh+vnNpD/TC6toIQUatDLyRjVZTncGxShg1DhUF0W7LtuAFF7clZuIloxXQbSxO1CVzugvCUDdcDGrQ1crqbWFIM6byA3B0DcNi2wms0PbQy/EcIjrj9+f7vvfN+7y35yH476GqqqpUDMNk0TRyCGGWSG5BEKaTyeR4IpHwd3V1hUVTajGMLD5MJhNrMBiytFptmbW8vLqw8PYVCkU6R1ECwrPR2NDPv1740uk8GQgEnGNjY36PxxOXYhcASkpKGJ1Opzcajc9s2bLpaaLOVF7wpjAxHQMr5lqmTkdBPpCYmZztaGs/NDAw8G4wGLzkdrsTCwBi2Tq9Xr/Z7rA3j05z7GffXkRgTobZpAB5KgkFBGg0NKzFBtycEY43NTXv8nq9R8V2gkQqPT8/37xz5/NHVbkrl7/VPYq5qAyCOITspSzkDIXLVyKI8RSWyCnUVS5DzHfe29Kyb+PIyMg5UlFRoVpRWLj9JYe96e2vJtA3CsioJGpWaXB/kQbuoRkcck9BJnBIJRIw3SLDjlId9r7+qsMzOLifiOVnWlYX736k9rlN294fR2SOwfridFStyoAvFMebPUHMJxmkpVj8/mcEclkEb9QuR8eR1g/O/nD2RVJZWZlrXXvf3jXVzz5W0zIBq5FG46OZC8tJiQOkqH/3/NC+ccyG/Ti8YyV6uo6d+MbprJcAbigvt+wpfXir7QH7MKBk8c7G62G5TYHf/DyOfz+DNJZCktA49l0Y6vnLONVYhC9OtH14xuWql1q4znynqeHJbfXbbbt/gXuShlYBtD+Vg+ICFXZ98gdaTwfAsDQohoZRK+DUC3l470Drgf7+fjsROcAV5OU92PRa8+HuIUr5yskr4EgSS5U07jAoIG4SP46EwadozPNxNNbkYN2t0UhDg6N2eHj4U4kHEn1zLWtWN2+o2/qEvf0iPu+bRiiRCVqYB4sECE1ASBTV9yjhePxGfHTk4Mcu15mXRR74FomUJpfLzZs3rN9jLi0zd/RehfMnP3z+EGiSQHaWFmV334Tqe2UYdLn629qP1/M8f04EiC5qgdhsNpUoGlOpxVJXsc66llVmqCd5ATRDoBZJxUSmrnZ395z+urf3ICHE09nZKYlK+EdMf1M6TbyzRUHdpdfnFqXLOY1kj/GxqbFLvvOhUKhP/E5Imf+nxmtULc1EHovFFCzLcpI9Ho/HOI6bEwP5a6Us+f4CvPduztV//qsAAAAASUVORK5CYII=",
        right : "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAADmUlEQVQ4T2VTa0xbZRh+TnvKOS10wNZWKpCuTOAH6liKjMwbw9opUYhMNpWB7KJzyVTUjG0K2LJ4wWXJNjPxQrZlWxXB+4xlBDsmcdpoyxa8wbCWEQKmXTpKC23Pze8sssz4/vm+733zPt97eR4K/zVFbW2tlqZpo1KpzKUoKkMOSxJ/VRAwyfP8dG9v7xxxiYtp1OLFYrGo8vLyjAaDwfagzbah6PZbV2rT1KwoUojF5uMjI79cdPX19YRCoX6/3z/t9Xo5OfcaQHl5Oa3X65eXlJQ8U9/YsJ1Oz0obHQMuR+bBKYDsDBYrzQpIkZno0aPH3/P5fO8Gg8HA4OAgfw2AlK03m81bWltb9v0xq1W5hvwIhwXECH5CoUSakoIhNYmqe1cgPyPOOeyOlkAgcIy0E6Tk0gsKClY3N+86wZpWmTs/u4yrCREsIyJnmQYJXsTUFQ4UGUKqOonnq/MxN3Hh0v79B7aMjY15qKqqKq2luPjF3S+/aj84EIT39yQUNI0kFcf28kyUF6Xjm+Ewus+HkRSVKMsHnnsgG2/YHa0XR0YOUaT8rDV3rXmzfmvTky91XUIiqcHNSzVYUHBIUfJ4oVIPk06F3vOzODU0D00qj8NP5eLTrrePu88N7aVqampM99lsHZW1mzduO3gBaVojvtiVe325IlmYggxStrZPZuDyCehuzsbZnnc+dp35drcMkLPWau14aGPjE+vbhhFJycHme7RQSgIWOBGb7k7HLUYG7l9jePbE30CUw9eOQgx8fsTZ1+feI7ewtLS01PH0zqad698ahy9EQeQF8JyApnUG7Hv0JgyNzqHh/UmEYmTlWQKce2/DB4c7Dnl+8rZThANsYWHhI+3t9q7Tv6k1bd2TSGFUYBQC7iwglZBF/+yP4UpUQFxS4rUNy1BdJEZbX2nZNjo+/qXMA5m+Jqt17euPb93xmP2jv9DzfZTQVw1JkMCBhkClQEfP4OGyDDgaVuBk55EP3WfPtRAeTCwSSc0wzOrGhk0dxVZrac93SfT/8CempkMQJBomow62O4yor1gCz0C/59jJU3sSiYSHACwsaoGqq6vTSpJkub+iYkd1deU6XpO5JELII/ASshgKXHQ28tVp15kBt7uTiMzrdDplUUnXxfQvpdXkzNbpdGV5y02rWIbNlP3ziXg4EJgYJkL6kTyn5J//p8YbVC3PhInH46kqlYqV/RzHxVmWjZHExI1SlmP/ALUFfc7X7cZxAAAAAElFTkSuQmCC",
        port : "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAADFklEQVQ4T41Ta0iTYRQ+3/ftqqZDNxu1xSKpeR2EZOkywj/VCCmTXIoERZkUqT+SECEFichSKUELglqmRj+MZldDsIsgmmiZJquk1qK8bOk+d/lunbcLBCV04PDyXs5znnPe51Dw0+iEhAQ5TdPL1Gp1rCAIWoZhlkmSpBRFUcB1Ed/M4dks3vnGxsYCuBdIIEWCDQaDUqPRaBHAVFpammez7dgdER2nC/KMgqYkUS4F2DcTr4fq689dnpycHGVZ1jM1NeXHWJ4ymUyqyMjIeJlMlnij7fp5ndGceH/ET427gxDgJKAxRUwkAxsTIiDDRC02X2w4197eecvv939AkAUKqeuQdlJHe1uLQms2Nz+Ygel5AeQMUqMBJAlAROcECbITo6DYGsGdrKys6unp6eZ5/iOVmppqLisrK88vKD5U1zUL76fDoJKTyv42NiRCXoYGtqzyfb1+o6Pe4XB0UMnJyVnOO7dvjX/T61sezYBaiWmXMB5ZxKgZqNurlRZm3o3n59uLCIPtgwP9XWfvLSqevmGXzP4bMxAWoWpXPFhWCAuZmZm5VFpa2s7h4eGuCscXevRj8L8AKnboICdJEc7YkL6TAGzrf/6k63R3QPn4pR/UiqVLICwWsQ9n9unBspKft1qtuVRKSkpmZ0dbpyu0xnDq5meIUP67gSSY4yWIj5FD6wGdNOeeHCkoLNpPWSyWtXa7/fCRo2Xlx67OUANvWYj6RyPJd5JfOJG7HLL0bnfrpSu1TqfzLmU2m+NQouaLF5oajYnZ6ceveeA1ikjOUMD8qoZkxng4mBMHJdmyQElJSfnExEQvSvwTEZISJaxVqVTrmhobapPXWzc5ni3Qva/84GV5kKEUDVoF7MmIhs2rOV91dXVNX1/fw1Ao5Ha5XCwpmMhZiRaLbrTZbFuLCu1FOr3RGAKFihJEQQz7fC+GBvtqamqvBoNBF8dxX0gwGajfHSOCkuNBFLLRILVYXKNxr8BpFHFPHnvJRHq93nmPxxP6cxr/1B2NQDISSBxpMggkoea5MJrb7Q7juYhOWvLDvgNGAFwpttomFwAAAABJRU5ErkJggg==",
        close : "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABWklEQVQ4T2NkoBAwUqifAcWAMzNN/oMM/P//P8O/f//AtEX2BbyWwCVBmo3TTiM5aDeYfXhCAYNd4TWchoAlQJr55WyBrIdYfCTP8OjyFoa/f/+CMchl//7/A7MDml4wgg04PcP4v0n6GZKCY221KINWzCFIGJycZvjfLDMDyGIm0pC/DCvKyhhUw/dCDDg+Rf+/RXYWkZohypYUFTFoRB2AGHB0ku5/q9wcBoafv4gzhJ2NYWF+PoN27GGIAYf6tf7bFuQzMHz4QJwBAgIM83NzGXTjj0IMONCr8d++qJCB4ekz4gyQlmKYm53NoJ94HGLA3i7V/06lJQwMd+4QZ4CKCsPszEwGw+STEAN2tSv9d60oZ2C4eo04A7S1GGZlZDAYp55GJOWtTbL/YQkElFhgCQeWeOCJCCqnHrmfQVhYGDUvXL58+f+PHz+IcgUHBweDrq4uJCVSAgBrOpWwN42yugAAAABJRU5ErkJggg==",
        leaf : "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABQ0lEQVQ4T2NkoBAwUqifAWxA96pX/5EN+v2HgeHn7/8M33/9Z/j2E4iR6B+/GBh+AOV+//nPcLxPjhFugLiEKMPff0CJv/8Z/gAlfwHxb6BCkEE/Qfzf/4AYxGYAav7HcObCLYbzs40RBsBcANLwDWjLlx//GL58/w+kgfj7P6BLgK4CGgQD3z48Y7g4zwJiwPJtZ/5HeBoTDI6PQEPA4AcDg1tUKsOpbXMgBizedOJ/jK85AyOEiwL+/4fYiqz5w4+PDKGJxQynt8+F6Ji37vD/xEAbnC5A1wxSiGLArJX7/qeGOcINaGxsxOudhIQChtBMJBdMW7rrf2aUK0lh4BqZgvDC5EXb/+fEepAfBjADcDmBYBigG0ByGOB0ATDeP0LjHcT6AIx/UBr4AKRSkQMRZMCi5WsIBiK6Ang6IFknkgYA+YrcEXUbL2MAAAAASUVORK5CYII=",
        open : "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABaklEQVQ4T2NkoBAwUqifAcWAMzNN/oMM/P//P8O/f//AtEX2BbyWwCVBmo3TTsMd9PvPXzD7xBQ9BrvCazgNAUsgNM/F4qNkhn3dagx///4FY5DL/v3/B2YHNL1gBBtweobxf5P0M0AWNgNgZiajGL62WpRBNXwvJAxOTjP8b5Z5joHh5xTiwpSdk2FFWTGDXMA2BsZjsyTBAUcskFE3Z5DVMmFY0tbOoBi8E2KAZWoDUD8zATP+Mtw+cJnhze21DJa22QwLp7cwqITtQTPg53fchgCd/ePjW4bzqyYwmFuHAw2YxqAWsQ8SBnBXfPiA3xUCMgynFtYwSAorMezafpBBI+oAmgEgbzx9jNsQaTWGGztnM/x49Yzh9LE7DFoxhxApEe6KO3fwuuIjJyvDtW0LGC6ffc6gE3cE1YD7N79gaIYla3ACQsKy/lsZxMTEUPPC2bNn/3//jicgkYzn5ORkMDY2hqRESgAAE1ek/FnBZLMAAAAASUVORK5CYII=",
    },
    /**
     * 注册图片
     * @method regImage
     * @param {String} nm 图片名称
     * @param {String} url 图片URL
     */
    regImage : function(nm, url) {
        var image = new Image();
        image.src = url;
        TL.imgs[nm] = image;
    },
    /**
     * 注册Base64图片
     * @method regBase64Image
     * @param {String} nm 图片名称
     * @param {String} enc 图片经过Base64编码后的字符串
     */
    regBase64Image : function(nm, enc) {
        var image = new Image();
        image.src = enc;
        TL.imgs[nm] = image;
    },
    /**
     * 注册缓存的Base64图片
     * @method regBufferImages
     */
    regBufferImages : function() {
        for (var o in this.base64imgs) {
            //TH.log(o);
            var image = new Image();
            image.src = this.base64imgs[o];
            TL.imgs[o] = image;
        }
    },
    /**
     * 测试图片
     * 调用此方法之后就可以去Chrome缓存查看
     * @method testImg
     * @param {String} id 图片ID
     */
    testImg : function(id) {
        var script = document.createElement('img');
        // script.type = "text/javascript";
        script.src = TL.imgs[id];
    },
    /**
     * 获取图片
     * @method getImage
     * @param {String} imgName 图片ID
     */
    getImage : function(imgName) {
        if ( imgName in TL.imgs) {
            return TL.imgs[imgName];
        } else if ( imgName in TL.base64imgs) {
            var img = new Image();
            img.src = TL.base64imgs[imgName];
            return img;
        } else {
            return null;
        }

    },
    /**
     * 获取鼠标偏移量
     * @method getDelta
     * @return {Object} 返回鼠标运动的偏移量
     */
    getDelta:function(){
        return {
            x:TL.curPos.x - TL.lastPos.x,
            y:TL.curPos.y - TL.lastPos.y
        }
    },
    /**
     * 创建指定大小的Canvas
     * @method createCanvasImage
     * @param {String} nm Canvas图像的名字/ID
     * @param {Number} w Canvas宽度
     * @param {Number} h Canvas高度
     * @return {Canvas} 返回Canvas对象
     */
    createCanvasImage:function(nm, w, h){
        var cvs = document.createElement("canvas");
        cvs.width = w;
        cvs.height = h;
    },
    /**
     * 添加事件监听，现有的事件有：<br>
     * <dl>
     * <dt>create_link(startNode, endNode)</dt>
     * <dd>- startNode</dd>
     * <dd>- endNode</dd>
     * <dt>delete_link(linkId)</dt>
     * <dd>- linkId</dd>
     * <dt>mouse_down(curPos)</dt>
     * <dd>- curPos</dd>
     * <dt>mouse_up(curPos)</dt>
     * <dd>- curPos</dd>
     * </dl>
     * @method on
     * @param {String} evtName 事件名称
     * @param {Object} listener 事件回调函数
     * @param {Object} scope 事件运行环境，不填则使用window
     */
    on:function(evtName, listener, scope){
        var listenerArray = TL.listeners[evtName];
        if(!listenerArray){
            TL.listeners[evtName] = [];
            listenerArray = TL.listeners[evtName];
        }
        //兼容性处理
        if(!scope){
            scope = window;
        }
        //添加处理对象
        listenerArray.push({
            //回调函数
            callback : listener,
            //运行空间
            scope : scope
        });
    },
    /**
     * 触发事件 
     * @method fireEvent
     * @param {Object} evtName 事件名称
     * @param {Object} optionArgs 事件参数
     */
    fireEvent:function(evtName, optionArgs){
        // console.log("事件触发-<" + evtName + ">");
        var evtArgs = Array.prototype.slice.call(arguments, 1);
        var evtHandlers = TL.listeners[evtName];
        for(var i=0; i<evtArgs.length; i++){
            // console.log("#" + (i+1) + " - " + evtArgs[i])
        }
        if(!!evtHandlers){
            //调用所有注册的事件处理函数
            for(var i=0, len = evtHandlers.length; i<len; i++){
                // console.log(evtHandlers[i]);
                var callback = evtHandlers[i].callback;
                var scope = evtHandlers[i].scope;
                callback.apply(scope, evtArgs);
            }
        }
        // console.log("--------------------------------------------");
    },
    /**
     * 删除事件监听 
     * @method un
     * @param {String} evtName 事件名称
     * @param {Function} callback 回调函数
     * 
     */
    un:function(evtName, callback){
        var evtHandlers = TL.listeners[evtName];
        if(!!evtHandlers){
            //调用所有注册的事件处理函数
            for(var i=0, len = evtHandlers.length; i<len; i++){
                // console.log(evtHandlers[i]);
                //删除事件回调
                if(callback == evtHandlers[i].callback){
                    evtHandlers.splice(i,1,0);
                    break;
                }
            }
        }
    },
    /**
     * 添加Link<br>
     * 添加成功之后，触发事件create_link，通知外部程序Link已创建
     * @method addLink
     * @param {Object} cfg 起始点
     * 		@param {Port} cfg.startNode 起始点
     * 		@param {Port} cfg.endNode   结束点
     * 		@param {Number} cfg.stationId   结束点
     */
    addLink:function(cfg){
//        console.warn("TL.addLink");
        var link = new ZLink(cfg.startNode, cfg.endNode);
        link.stationId = TL.curStationId;
        link.setConnType(cfg.connType);
        var linkText = "";
        if(link.connType == 2 || link.connType==4){
            var cableName = /\[.*\]/ig.exec(Ext.getCmp("_tutu_rightCombo_").getRawValue())[0];
            cableName = cableName.substr(1,cableName.length - 2)
            var fiberId = parseInt(cfg.endNode.parent.text);
            linkText = cableName + "(" + fiberId + ")"
//            console.log("linkText = " + linkText)
        }
        cfg.startNode.linkText = linkText;
//        LM.addLink({
//        	STATION_ID:link.stationId,
//        	A_END_ID:link.aEndId,
//        	Z_END_ID:link.zEndId,
//        	FIBER_INFO:link.end.text,
//        	CONN_TYPE:link.connType
//        });
        //触发事件，通知外部程序Link已创建
    	//int stationed --OK,int ,int ,int connType
        //连接类型 1:OTDR-OSW   2:OSW-光缆 3:光缆-OSW 4:OSW-光缆 5:光缆-光缆 6:OSW-OSW
    	//TODO 参数修改
        TL.fireEvent("create_link", {
        	STATION_ID:link.stationId, 
        	A_END_ID:link.aEndId,
        	Z_END_ID:link.zEndId,
        	CONN_TYPE:link.connType,
        	FIBER_INFO:linkText,
        	rawLink:link
        });
    },
    /**
     * 删除Link
     * @private
     * @method  deleteLink
     */
    deleteLink : function(){
        //TL.selectedLink 为选中Link
    	if(!TL.selectedLink){
//    		console.warn("没有选中Link")
    		return
    	}
        //触发事件，通知外部程序Link已删除
        /**
         * 事件 delete_link 
         * @event delete_link
         * @param {Object} detail 删除的 Link 详情，字段和t_ftts_connect外部连接表的四个主要字段相对应
         *      @param {Object} detail.STATION_ID 删除的 Link 详情
         *      @param {Object} detail.A_END_ID 删除的 Link 详情
         *      @param {Object} detail.Z_END_ID 删除的 Link 详情
         *      @param {Object} detail.CONN_TYPE 删除的 Link 详情
         */
    	//int stationId,int aEndId,int zEndId,int connType
        //连接类型 1:OTDR-OSW 2:OSW-光缆 3:光缆-OSW 4:OSW-光缆 5:光缆-光缆 6:OSW-OSW
    	//TODO 参数修改
        TL.fireEvent("delete_link", {
        	STATION_ID:TL.selectedLink.stationId, 
        	A_END_ID:TL.selectedLink.aEndId,
        	Z_END_ID:TL.selectedLink.zEndId,
        	CONN_TYPE:TL.selectedLink.connType,
        	rawLink:TL.selectedLink
        });
    },
    /**
     * 根据连接类型来移除Link（仅显示层面，数据仍在）
     * @param {Array|Number} conTypes 累加类型数组
     */
    removeLinkByType:function(conTypes){
    	//所有保留的Link全丢newLinks里面
    	var newLinks = [];
    	if(!conTypes.length){
    		conTypes = [conTypes];
    	}
//    	console.log(conTypes);
    	for ( var i = 0; i < TL.links.length; i++) {
			var link = TL.links[i];
			//找不到说明不在删除名单中
			if(conTypes.indexOf(link.connType)<0){
//				console.log(link.connType + " is alive")
				newLinks.push(link);
			}
		}
//    	console.log(TL.links.length);
//    	console.log(newLinks.length);
    	TL.links = newLinks;
    },
    /**
     * Link创建成功的回调函数
     * @param {Object} v
     * 		@param {Object} v.STATION_ID
     * 		@param {Object} v.A_END_ID
     * 		@param {Object} v.Z_END_ID
     * 		@param {Object} v.CONN_TYPE
     */
    addSuccessCallback:function(v){
    	LM.data.push(v);
    },
    /**
     * Link创建成功的回调函数
     * @param {Object} v
     * 		@param {Object} v.STATION_ID
     * 		@param {Object} v.A_END_ID
     * 		@param {Object} v.Z_END_ID
     * 		@param {Object} v.CONN_TYPE
     */
    deleteSuccessCallback:function(v){
    	LM.deleteData(v);
    },
    /**
     * 判断2侧选择的是不是同一根光缆
     * @method isSameFiber
     * @return {Boolean} True：2侧选择的是同一根光缆<br>False:2侧选择的不是同一根光缆
     */
    isSameFiber:function(){
    	return Ext.getCmp("_tutu_leftCombo_").getValue() == Ext.getCmp("_tutu_rightCombo_").getValue();
    },
    setStationName:function(stationName){
    	var tag = document.getElementById("title");
    	tag.innerHTML = "FTTS光纤连接图 - " + stationName
    }
};

