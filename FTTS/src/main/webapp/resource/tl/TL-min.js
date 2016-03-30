var MOUSE_STATE={NONE:0,MOUSEDOWN:1,DRAG:2};var POS={TOP:0,RIGHT:1,BOTTOM:2,LEFT:3};var lastTime=0;var prefixes="webkit moz ms o".split(" ");var requestAnimationFrame=window.requestAnimationFrame;var cancelAnimationFrame=window.cancelAnimationFrame;var prefix;for(var i=0;i<prefixes.length;i++){if(requestAnimationFrame&&cancelAnimationFrame){break}prefix=prefixes[i];requestAnimationFrame=requestAnimationFrame||window[prefix+"RequestAnimationFrame"];cancelAnimationFrame=cancelAnimationFrame||window[prefix+"CancelAnimationFrame"]||window[prefix+"CancelRequestAnimationFrame"]}if(!requestAnimationFrame||!cancelAnimationFrame){requestAnimationFrame=function(f,b){var a=new Date().getTime();var c=Math.max(0,16-(a-lastTime));var d=window.setTimeout(function(){f(a+c)},c);lastTime=a+c;return d};cancelAnimationFrame=function(a){window.clearTimeout(a)}}window.requestAnimationFrame=requestAnimationFrame;window.cancelAnimationFrame=cancelAnimationFrame;(function(){var a=false,b=/xyz/.test(function(){xyz})?/\b_super\b/:/.*/;this.Class=function(){this.name="Class"};Class.extend=function(h){var g=this.prototype;a=true;var f=new this();a=false;for(var d in h){f[d]=typeof h[d]=="function"&&typeof g[d]=="function"&&b.test(h[d])?(function(j,k){return function(){var m=this._super;this._super=g[j];var l=k.apply(this,arguments);this._super=m;return l}})(d,h[d]):h[d]}function c(){if(!a&&this.init){this.init.apply(this,arguments)}else{}}c.prototype=f;c.constructor=c;c.extend=arguments.callee;return c}})();var LinkManager=LinkManager||{};var LM=LinkManager;(function(){LinkManager.data=[];LinkManager.rawLinks=[];LinkManager.ports={f:[],u:[],U:[],p:[],F:[]};LinkManager.state={};LinkManager.init=function(a){LinkManager.data=[];Ext.Ajax.request({url:"external-connect!getConnectInfoByStationId.action",type:"post",params:{stationId:a},success:function(b,c){var d=Ext.decode(b.responseText);LinkManager.data=d.rows;TL.setStationName(d.STATION_NAME)},failure:function(){Ext.Msg.alert("提示：","后台运行出错！")},error:function(){Ext.Msg.alert("提示：","后台运行出错！")}})};LinkManager.regPort=function(a,b){if(a=="f"||a=="F"||a=="u"||a=="U"||a=="p"){LinkManager.ports[a].push(b)}else{}};LinkManager.getPort=function(a,d){var g=LM.ports[a];var f=null;for(var c=0;c<g.length;c++){var b=g[c];if(b.id==d){f=b;b.occupied=true;break}}return f};LinkManager.generateLinks=function(){TL.links=[];var l=LM.data;var c=["-","u","p","f","p","f","p"];var g=["-","U","F","U","F","F","U"];for(var f=0;f<l.length;f++){var k=l[f];var a=k.CONN_TYPE;var d=k.A_END_ID;var j=k.Z_END_ID;var b=LM.getPort(c[a],d);if(!!b){b.linkText=k.FIBER_INFO}var m=LM.getPort(g[a],j);if(!!m){m.linkText=k.FIBER_INFO}if(!!b&&!!m){var h=new ZLink(b,m);h.stationId=k.STATION_ID;h.text=k.FIBER_INFO;h.setConnType(k.CONN_TYPE);TL.links.push(h)}}};LinkManager.addLink=function(c){delete (c.rawLink);LM.data.push(c);var a=LM.ports.f;for(var d=0;d<a.length;d++){a[d].occupied=LM.isFiberOccupied("f",a[d].id)}var b=LM.ports.F;for(var d=0;d<b.length;d++){b[d].occupied=LM.isFiberOccupied("F",b[d].id)}};LinkManager.removeLink=function(d){TL.links.remove(d);d.start.occupied=false;d.end.occupied=false;d.start.link=null;d.end.link=null;for(var c=0;c<LM.data.length;c++){var f=LM.data[c];if(f.STATION_ID==d.stationId&&f.A_END_ID==d.aEndId&&f.Z_END_ID==d.zEndId&&f.CONN_TYPE==d.connType){LM.data.remove(f);break}}var a=LM.ports.f;for(var c=0;c<a.length;c++){a[c].occupied=LM.isFiberOccupied("f",a[c].id)}var b=LM.ports.F;for(var c=0;c<b.length;c++){b[c].occupied=LM.isFiberOccupied("F",b[c].id)}};LinkManager.isFiberOccupied=function(a,c){if(a=="f"||a=="F"){var f=false;for(var b=0;b<LM.data.length;b++){var d=LM.data[b];switch(d.CONN_TYPE){case 2:case 4:f=f||(d.Z_END_ID==c);break;case 3:f=f||(d.A_END_ID==c);break;case 5:f=f||(d.A_END_ID==c||d.Z_END_ID==c);break}}return f}return false}})();function Region(a,d,b,c){this.x=a;this.y=d;this.width=b;this.height=c;this.l=a;this.t=d;this.r=a+b;this.b=d+c}Region.prototype.inRegion=function(a,b){return((a-this.l)*(a-this.r)<=0&&(b-this.t)*(b-this.b)<=0)};var IObject=Class.extend({name:"IObject",id:-1,x:0,y:0,width:0,height:0,region:null,isCursorInside:false,items:null,init:function(a){this.setCfg(a);this.region=new Region(this.x,this.y,this.width,this.height)},setCfg:function(a){if(a===undefined){return}for(var c in a){var b=a[c];if(b===undefined){}else{if(c in this){this[c]=b}}}},draw:function(a){this.update();if(!!a){a.strokeStyle="#0000FF";a.fillStyle="rgba(255,255,200,0.5)";a.fillRect(this.x,this.y,this.width,this.height);a.strokeRect(this.x,this.y,this.width,this.height);if(this.isCursorInside&&SHOW_HOVER){a.fillStyle="rgba(200,200,200,0.5)";a.fillRect(this.x,this.y,this.width,this.height)}}},update:function(a){},inRegion:function(a,b){this.isCursorInside=this.region.inRegion(a,b);return this.isCursorInside},onmousedown:function(a){return this.isCursorInside},onmousemove:function(a,b){return this.isCursorInside},onmouseup:function(a){return this.isCursorInside},lostFocus:function(){this.isCursorInside=false;for(var a=this.items.length,b=a-1;b>=0;b--){var c=this.items[b];c.lostFocus()}}});var Container=IObject.extend({name:"Container",cx:0,cy:0,cw:0,ch:0,padding:"1",offset:null,maxOffset:null,clientRegion:null,cursorInClient:false,items:null,init:function(b){var d=document.createElement("canvas");this._super(b);var f=bd=ld=rd=0;this.padding=this.padding.trim();if(!!this.padding){var c=this.padding.split(" ");var a=c.length;if(a>0){ld=rd=f=bd=c[0]>>0}if(a>1){ld=rd=c[1]>>0}if(a>2){bd=c[2]>>0}if(a>3){ld=c[3]>>0}}this.cx=ld+this.x;this.cy=f+this.y;this.cw=this.width-ld-rd;this.ch=this.height-f-bd;this.clientRegion=new Region(this.cx,this.cy,this.cw,this.ch);this.offset={x:0,y:0};this.maxOffset={x:0,y:0};this.items=[]},draw:function(a){this.drawBg(a);if(!!a){a.fillStyle="blue";a.clearRect(this.cx,this.cy,this.cw,this.ch);a.save();a.fillStyle="red";a.beginPath();a.rect(this.cx,this.cy,this.cw,this.ch);a.closePath();a.clip();a.translate(this.cx+this.offset.x,this.cy+this.offset.y);this.drawClient(a);a.restore();if(this.cursorInClient){}}},drawBg:function(a){},drawClient:function(b){for(var a=this.items.length,c=a-1;c>=0;c--){this.items[c].draw(b)}},inRegion:function(b,d){this.cursorInClient=this.clientRegion.inRegion(b,d);if(this.cursorInClient){for(var c=0,a=this.items.length;c<a;c++){this.items[c].inRegion(b-this.cx-this.offset.x,d-this.cy-this.offset.y)}}else{for(var c=0,a=this.items.length;c<a;c++){this.items[c].isCursorInside=false}}return this._super(b,d)},onmousedown:function(b){if(this.isCursorInside){var c=true;for(var a=0;c&&a<this.items.length;a++){var d=this.items[a];if(d instanceof IObject){c=!d.onmousedown(b)}}}return this.isCursorInside},onmousemove:function(b,d){if(!!d&&this.isCursorInside){if(this.offset.x+this.maxOffset.x+d.x>0){}if(this.offset.y+this.maxOffset.y+d.y>0){this.offset.y+=d.y}if(this.offset.x>0){this.offset.x=0}if(this.offset.y>0){this.offset.y=0}for(var a=0;a<this.items.length;a++){var c=this.items[a];c.updateLines()}}return this.isCursorInside},onmouseup:function(b){if(this.isCursorInside){var c=true;for(var a=0;c&&a<this.items.length;a++){var d=this.items[a];if(d instanceof IObject){c=!d.onmouseup(b)}}}return this.isCursorInside},updateClientSize:function(){var d,h,g,c;d=h=g=c=0;for(var f=0,a=this.items.length;f<a;f++){var j=this.items[f].getBounds();h=Math.max(h,j.right);c=Math.max(c,j.bottom)}this.maxOffset.x=h-this.cw+5;this.maxOffset.y=c-this.ch+5},add:function(a){this.items.push(a);this.updateClientSize();a.parent=this;a.index=this.items.length-1},clear:function(){this.items=[]}});var ListView=Container.extend({name:"ListView",snap:0,bgColor:16777181,textColor:51,data:null,count:0,portPos:POS.LEFT,renderer:function(a){return a+""},init:function(a){this.padding="1";this.data=[];this._super(a)},drawBg:function(a){if(!!a){a.fillStyle="#888888";a.fillRect(this.x,this.y,this.width,this.height)}},add:function(a){a.FIBER_NAME=a.FIBER_NAME||"<空>";a.NOTE=a.NOTE||"<空>";var b=new ListItem({x:0,y:this.count*24,text:this.renderer(a),width:this.width,height:24,id:a.RESOURCE_FIBER_ID,portPos:this.portPos,portText:a.FIBER_NAME});this.count++;this._super(b)},addAll:function(a){this.data=this.data.concat(a);this.measureClient();this.count+=a.length},clear:function(){this.count=0;this._super()}});var Link=IObject.extend({name:"Link",start:null,end:null,init:function(b,a){this.start=b||TL.startPos;this.end=a||TL.curPos;b.link=this;a.link=this},inRegion:function(a,b){return false},draw:function(a){var b=this.start.getAbsPos();var c=this.end.getAbsPos();a.strokeStyle="blue";a.beginPath();a.moveTo(b.x,b.y);a.lineTo(c.x,c.y);a.closePath();a.stroke()}});var ZLink=Link.extend({name:"ZLink",points:null,connType:0,stationId:-1,aEndId:-1,zEndId:-1,text:"#",selected:false,init:function(b,a){this._super(b,a);this.points=[];this.aEndId=b.id;this.zEndId=a.id;b.occupied=true;a.occupied=true;this.update()},inRegion:function(k,j){var g=this.points.length;var l=[];var h=this.start.getAbsPos();var c=this.end.getAbsPos();l.push([h.x,h.y,this.points[0].x,this.points[0].y,k,j]);for(var f=0;f<g-1;f++){var b=this.points[f];var d=this.points[f+1];l.push([b.x,b.y,d.x,d.y,k,j])}l.push([d.x,d.y,c.x,c.y,k,j]);var a=false;for(var f=0,g=l.length;!a&&f<g;f++){a=hitTest.apply(null,l[f])}this.isCursorInside=a;return a},draw:function(a){if((this.start.isVisible()&&this.end.isVisible())||this.connType==6){this.update();if(true||TL.mouseState==MOUSE_STATE.DRAG){var c=this.start.getAbsPos();var b=this.end.getAbsPos();a.beginPath();a.moveTo(c.x,c.y);for(var d=0;d<this.points.length;d++){var f=this.points[d];a.lineTo(f.x,f.y)}a.lineTo(b.x,b.y);if(this.isCursorInside||this.selected){a.lineWidth=3;a.strokeStyle=this.selected?"blue":"yellow";a.stroke()}a.strokeStyle="blue";a.lineWidth=1;a.stroke()}}},update:function(){this.points=[];var k=this.start.getAbsPos();var b=this.end.getAbsPos();switch(this.connType){case 6:if(this.start.type=="p"){p=this.start;u=this.end}else{p=this.end;u=this.start}k=p.getAbsPos();b=u.getAbsPos();var m=k.x;for(var h=0,j=p.parent.items.length;h<j;h++){var c=p.parent.items[h];if(c!=p&&!!c.link&&c.link.connType==6){m+=10}if(c==p){break}}var g=p.parent.index*2;this.points.push({x:m+20+g,y:k.y});var d=1;if(k.y>b.y){d=-1}var f=k.y-p.y;var o=u.parent.getBounds();var a={y1:d>0?o.top-50:o.bottom,y2:d>0?o.top:o.bottom+50};a.mid=(a.y1+a.y2)/2;a.halfy=((a.y1-a.y2)/2);var n=(p.y-(d>0?a.y1:a.y2))/(p.y-a.mid)*a.halfy;var l=n*d+a.mid+f;this.points.push({x:m+20+g,y:l});this.points.push({x:b.x-20,y:(d>0?a.y2:a.y1)+f});this.points.push({x:b.x-20,y:b.y});if(this.start.type=="U"){this.points.reverse()}break;default:this.points.push({x:k.x+Math.min(40,(b.x-k.x)/3),y:k.y});this.points.push({x:b.x-Math.min(40,(b.x-k.x)/3),y:b.y})}},onmousedown:function(a){if(this.isCursorInside){if(!!TL.selectedLink&&this!=TL.selectedLink){TL.selectedLink.selected=false}this.selected=!this.selected}else{this.selected=false}if(this.selected){TL.selectedLink=this}return this.isCursorInside},setConnType:function(a){this.connType=a;if(a==2||a==4||a==6){this.text=this.end.text}}});var HITDISTANCE=6;function hitTest(c,k,b,j,g,f){var m=b-c;var l=j-k;var d=Math.sqrt(m*m+l*l);var h=(m*(g-c)+l*(f-k))/(d*d);var a=(m*(f-k)-l*(g-c))/d;return(h>0&&h<1&&Math.abs(a)<HITDISTANCE)}var SampleLink=Link.extend({name:"SampleLink",init:function(){},inRegion:function(a,b){return false},draw:function(a){if(!!TL.startNode){this.update();a.beginPath();a.strokeStyle="blue";a.moveTo(this.start.x,this.start.y);a.lineTo(this.end.x,this.end.y);a.closePath();a.stroke()}},update:function(){if(!!TL.startNode){this.start=TL.startNode.getAbsPos();this.end=TL.curPos}},onmousedown:function(a){}});var Widget=IObject.extend({name:"Widget",snap:0,bgColor:16777181,textColor:51,port:null,parent:null,absPos:null,init:function(a){this._super(a);this.absPos={x:0,y:0};this.items=[]},measureItem:function(b){var a=TL.fg.measureText(b);return{width:a.width,height:this.height}},draw:function(a){if(!!a){a.fillStyle="rgba(255,255,200,0.5)";a.fillRect(this.x,this.y,this.width,this.height)}},getBounds:function(){return{left:this.x,right:this.x+this.width,top:this.y,bottom:this.y+this.height}},addPort:function(a){var b=new Port(a);this.items.push(b);b.parent=this},drawPort:function(b){for(var c=0,a=this.items.length;c<a;c++){this.items[c].draw(b)}},onmousedown:function(c){if(this.isCursorInside){var d=true;for(var b=0,a=this.items.length;d&&b<a;b++){d=!this.items[b].onmousedown(c)}}return this.isCursorInside},onmousemove:function(a,b){return this.isCursorInside},onmouseup:function(c){if(this.isCursorInside){var d=true;for(var b=0,a=this.items.length;d&&b<a;b++){d=!this.items[b].onmouseup(c)}}return this.isCursorInside},inRegion:function(b,d){this._super(b,d);for(var c=0,a=this.items.length;c<a;c++){this.items[c].isCursorInside=false}for(var c=0,a=this.items.length;!this.isCursorInside&&c<a;c++){this.isCursorInside|=this.items[c].inRegion(b,d)}return this.isCursorInside},updateLines:function(){for(var c=0,a=this.items.length;c<a;c++){var b=this.items[c];if(b.link){b.link.update()}}}});Widget.create=function(a){switch(a.NAME){case"OTDR":return new OTDR(a);break;default:return new OSW(a);break}};var Port=IObject.extend({name:"Port",pos:null,link:null,parent:null,occupied:false,text:"@",type:"p",init:function(a){a.width=12;a.height=12;this._super(a);this.region=new Region(this.x-6,this.y-6,a.width,a.height);LM.regPort(this.type,this)},isVisible:function(){return this.region.inRegion(this.pos.x,this.pos.y)},draw:function(a){this.update();if(!!a){a.save();if(this.isCursorInside||this==TL.startNode){a.fillStyle="rgba(100,100,255,0.7)";a.fillRect(this.x-7,this.y-7,this.width+2,this.height+2);a.fillStyle="white";a.fillRect(this.x-6,this.y-6,this.width,this.height)}var b=(TL.startNode==this)||(!TL.possiblePortType)||TL.possiblePortType.indexOf(this.type)>-1;a.fillStyle=this.occupied?(this.isCursorInside?"red":"rgba(200,200,250,0.5)"):(b?"rgba(100,250,100,0.5)":"red");a.fillRect(this.x-4,this.y-4,this.width-4,this.height-4);a.restore()}},getBounds:function(){return{left:this.x,right:this.x+this.width,top:this.y,bottom:this.y+this.height}},onmousedown:function(g){if(this.isCursorInside){if(!TL.startNode){if(!this.occupied){TL.startNode=this;var a={f:["F","U"],u:["U"],U:["f","u","p"],p:["F","U"],F:["f","p"]};TL.possiblePortType=a[this.type]}}else{var j=TL.startNode;var c=this;TL.startNode=null;TL.possiblePortType=null;if(j==c||j.occupied||c.occupied||(j.parent==c.parent)){return}var h=j.type>c.type?c.type+j.type:j.type+c.type;var b={Uf:3,Ff:5,Uu:1,Up:6,Fp:2};var f=b[h]||0;if(f==2&&!TL.isRTU){f=4}if(f==5&&TL.isSameFiber()){return}if((j.type!="p"&&f==6)||(j.getAbsPos().x>c.getAbsPos().x)){var d=j;j=c;c=d}if(!!f){TL.addLink({startNode:j,endNode:c,connType:f})}}}return this.isCursorInside},onmousemove:function(a,b){return this.isCursorInside},onmouseup:function(a){if(this.isCursorInside){}return this.isCursorInside},getAbsPos:function(){var a=this.parent.parent;sx=a.cx+a.offset.x+this.x;sy=a.cy+a.offset.y+this.y;return{x:sx,y:sy}},isVisible:function(){var a=this.parent.parent;var b=a.offset.y+this.y;return b>0&&b<a.ch}});var OTDR=Widget.extend({name:"OTDR",port:null,SLOT_NO:-1,init:function(a){a.width=64;a.height=48;a.x=a.y=5;a.id=a.UNIT_ID;this._super(a);this.addPort({x:this.x+this.width+16,y:this.y+this.height/2,id:this.id,type:"u"})},measureItem:function(b){var a=TL.fg.measureText(b);return{width:a.width,height:this.height}},draw:function(a){if(!!a){var b=a.textAlign;a.strokeStyle="#000";a.fillStyle="rgba(255,255,230,1)";a.lineWidth=1;a.textAlign="center";a.fillRect(this.x,this.y,this.width,this.height);a.strokeRect(this.x,this.y,this.width,this.height);a.strokeText("OTDR",this.x+this.width/2,this.y+this.height/2);a.strokeText(this.SLOT_NO,this.x+this.width/2,this.y+this.height/2+12);a.textAlign=b;a.fillStyle="#000";a.fillRect(this.x+this.width,this.y+this.height/2,16,1);this.drawPort(a);if(this.isCursorInside&&SHOW_HOVER){a.fillStyle="rgba(200,200,200,0.5)";a.fillRect(this.x,this.y,this.width,this.height)}}},getBounds:function(){return{left:this.x,right:this.x+this.width+40,top:this.y,bottom:this.y+this.height}}});var OSW=Widget.extend({name:"OSW",portCount:8,SLOT_NO:-1,text:"",init:function(a){a.portCount=a.PORT_COUNT;a.width=64;a.height=a.portCount*12+12;a.id=a.UNIT_ID;this._super(a);var c=a.PORT_IDS.split(",");for(var b=0;b<this.portCount;b++){this.addPort({x:this.x+this.width+60,y:this.y+b*12+18,id:~~c[b],type:"p"})}this.addPort({x:this.x-16,y:this.y+this.height/2,id:this.id,type:"U",text:a.NAME})},draw:function(a){if(!!a){var c=a.textAlign;a.strokeStyle="#000";a.fillStyle="rgba(255,255,230,1)";a.lineWidth=1;a.textAlign="center";a.fillRect(this.x,this.y,this.width,this.height);a.strokeRect(this.x,this.y,this.width,this.height);a.strokeText("OSW",this.x+this.width/2,this.y+this.height/2);a.strokeText(this.SLOT_NO,this.x+this.width/2,this.y+this.height/2+12);a.textAlign=c;a.fillStyle="#000";a.fillRect(this.x-16,this.y+this.height/2,16,1);for(var b=0;b<this.portCount;b++){a.fillRect(this.x+this.width,this.y+b*12+18,60,1);a.strokeText((b+1)+".",this.x+this.width+5,this.y+b*12+18);if(!!this.items[b].linkText){a.strokeText(this.items[b].linkText,this.x+this.width+20,this.y+b*12+18)}}this.drawPort(a);if(this.isCursorInside&&SHOW_HOVER){a.fillStyle="rgba(200,200,200,0.5)";a.fillRect(this.x,this.y,this.width,this.height)}}},getBounds:function(){return{left:this.x,right:this.x+this.width+80,top:this.y,bottom:this.y+this.height}}});var ListItem=Widget.extend({name:"ListItem",displayText:"",fiberId:-1,fiberIndex:-1,text:"",textOffset:24,rollPosition:24,rollDelay:24,textWidth:24,portPos:POS.LEFT,bgColor:16777181,textColor:51,parent:null,port:null,portText:"#",init:function(a){this.fiberId=a.id;this.fiberIndex=a.index;this._super(a);if(this.portPos==POS.RIGHT){this.textOffset=4;this.addPort({x:this.x+this.width-12,y:this.y+12,id:a.id,type:"f",text:a.portText,occupied:LM.isFiberOccupied("f",a.id)})}else{this.addPort({x:this.x+12,y:this.y+12,id:a.id,type:"F",text:a.portText,occupied:LM.isFiberOccupied("F",a.id)})}this.textWidth=this.measureText()},measureText:function(){var a=TL.fg.measureText(this.text);return a.width},draw:function(a){a.save();a.fillStyle="black";a.strokeStyle="black";a.textBaseline="top";if(this.isCursorInside&&(this.textWidth+24>this.width)){a.fillText(this.text,this.x+this.textOffset-this.rollPosition,this.y+6);a.fillText(this.text,this.x+this.textOffset-this.rollPosition+this.textWidth+24,this.y+6);if(this.rollDelay>0){this.rollDelay--}else{this.rollPosition++}if(this.rollPosition>this.textWidth+24){this.rollPosition=0;this.rollDelay=50}}else{a.fillText(this.text,this.x+this.textOffset,this.y+6)}if(this.portPos==POS.RIGHT){a.clearRect(this.x+this.width-24,this.y,24,24)}else{a.clearRect(this.x,this.y,24,24)}if(this.isCursorInside){a.fillStyle="rgba(200,200,200,0.3)";a.fillRect(this.x,this.y,this.width,this.height)}this.drawPort(a);a.restore()},inRegion:function(a,b){this._super(a,b);this.items[0].inRegion(a,b);return this.isCursorInside}});var ui=ui||{};var css=function(){return{load:function(d,b){var b=b||document,f=b.createElement("link");f.type="text/css";f.rel="stylesheet";f.href=d;b.getElementsByTagName("head")[0].appendChild(f)},inject:function(d,b){var b=b||document,f=document.createElement("style");f.type="text/css";f.innerHTML=d;b.getElementsByTagName("head")[0].appendChild(f)}}}();function log(a){console.log(a)}var e=function(b){var a=document.getElementById(b);return{createChild:function(g,c){c=c||{};var j=document.createElement(g);for(var h in c){switch(h){case"id":j.id=c.id;break;case"css":var f=j.style;for(var d in c.css){f[d]=c.css[d]}break;default:j[h]=c[h]}}if(!!a){a.appendChild(j)}if(g=="canvas"){return j}else{return this}},getWidth:function(){return a.clientWidth||a.width||a.style.width},getHeight:function(){return a.clientHeight||a.height||a.style.height},createCombo:function(d){var c=document.getElementById(d.cmbId).style.width;c=c.substr(0,c.length-2)>>0;function h(j,k,n,m){var l={};l[k]=n;Ext.Ajax.request({url:j,type:"post",params:l,success:function(o,q){var r=Ext.decode(o.responseText);m(r)},failure:function(){Ext.Msg.alert("提示：","后台运行出错！")},error:function(){Ext.Msg.alert("提示：","后台运行出错！")}})}var f=new Ext.data.Store({url:d.url,reader:new Ext.data.JsonReader({totalProperty:"total",root:"rows"},[d.valueField,d.displayField])});var g=new Ext.form.ComboBox({id:d.id,typeAhead:true,triggerAction:"all",lazyRender:true,mode:"local",width:c,store:f,valueField:d.valueField,displayField:d.displayField,renderTo:d.cmbId,listeners:{select:function(k,l,j){var m=l.get(d.valueField);h(d.detailUrl,d.paramField,m,d.callback)}}});return this},css:function(c,d){a.style[c]=d},html:function(c){a.innerHTML=c}}};ui=(function(){var A="lc_"+Ext.id();var c="rc_"+Ext.id();var b="mc_"+Ext.id();var y="cvs_fg";var C="bgcvs_"+Ext.id();var d,o,h,s,l,v,t,B,g,f,k,j,r,q;var x,z,D;var m,n,w;function a(E){return document.getElementById(E)}if(!ui.initialized){ui.initialized=true;return{initDom:function(I){e(I).createChild("div",{id:A,css:{width:"180px",height:"22px",position:"absolute",left:"10px",top:"5px",background:"#ffcccc"}}).createCombo({id:"_tutu_leftCombo_",cmbId:A,url:"external-connect!getCableInfo.action",valueField:"CABLE_ID",displayField:"CABLE_NAME_FTTS",detailUrl:"external-connect!getFiberInfo.action",paramField:"cableId",callback:TL.loadLeftData});e(I).createChild("div",{id:c,css:{width:"180px",height:"22px",position:"absolute",right:"10px",top:"5px",background:"#ffcccc"}}).createCombo({id:"_tutu_rightCombo_",cmbId:c,url:"external-connect!getCableInfo.action",valueField:"CABLE_ID",displayField:"CABLE_NAME_FTTS",detailUrl:"external-connect!getFiberInfo.action",paramField:"cableId",callback:TL.loadRightData});e(I).createChild("div",{id:b,css:{width:"240px",height:"22px",margin:"5px auto",background:"#ffffcc"}}).createCombo({id:"_tutu_midCombo_",cmbId:b,url:"external-connect!getRcInfo.action",valueField:"RC_ID",displayField:"NUMBER",detailUrl:"external-connect!getUnitInfo.action",paramField:"rcId",callback:TL.loadMidData});var E=document.getElementById(I).clientWidth;var F=document.getElementById(I).clientHeight;F-=34;var H=document.getElementById("cvs_fg");H.width=E;H.height=F;var G=document.getElementById("cvs_bg");G.width=E;G.height=F;return{width:E,height:F,lx:10,rx:E-180-10,mx:E/2-120,ly:0,ry:0,my:0,lw:180,lh:F-10,rw:180,rh:F-10,mw:240,mh:F-10,cvs:H,ctx:H.getContext("2d"),bgCvs:G,bgCtx:G.getContext("2d")}},initData:function(E){if(!x){x=Ext.getCmp("_tutu_leftCombo_");m=x.getStore()}if(!z){z=Ext.getCmp("_tutu_rightCombo_");n=z.getStore()}if(!D){D=Ext.getCmp("_tutu_midCombo_");w=D.getStore()}m.baseParams.stationId=E;m.load();n.baseParams.stationId=E;n.load();w.baseParams.stationId=E;w.load()},debug:function(){console.log(x);console.log(m);console.log(z);console.log(n);console.log(D);console.log(w)}}}else{return ui}})();function getElementPos(c){c=c||window.event;var b=c.target||c.srcElement;var a=0,d=0;while(b.offsetParent){a+=b.offsetLeft;d+=b.offsetTop;b=b.offsetParent}return{x:a,y:d}}function getAbsMousePos(a){a=a||window.event;return{x:a.pageX||a.clientX+document.body.scrollLeft+document.documentElement.scrollLeft,y:a.pageY||a.clientY+document.body.scrollTop+document.documentElement.scrollTop}}function getMousePos(c){var b=getElementPos(c);var a=getAbsMousePos(c);TL.curPos.x=a.x-b.x;TL.curPos.y=a.y-b.y}function rndString(){var a=(Math.random()*20+5)>>0;var c="";for(var b=0;b<a;b++){c+=String.fromCharCode(97+(Math.random()*26)>>0)}return c}var SHOW_HOVER=true;var DEBUG=false;var lv=null;var aaa=null;var TL={objects:[],links:[],_lc:null,_mc:null,_rc:null,curStationId:-1,isRTU:false,listeners:{},width:800,height:600,moving:false,startNode:null,endNode:null,possiblePortType:null,fg:null,bg:null,bgCvs:null,curPos:{x:0,y:0},startPos:{x:0,y:0},lastPos:{x:0,y:0},mouseState:MOUSE_STATE.NONE,initDom:function(f){var d=ui.initDom(f);TL.width=d.width;TL.height=d.height;var a=new ListView({x:d.lx,y:d.ly,width:d.lw,height:d.lh,portPos:POS.RIGHT,renderer:function(g){var h=DEBUG?g.RESOURCE_FIBER_ID+"#":"";return h+g.FIBER_NO+". "+g.FIBER_NAME+" - "+g.NOTE}});TL._lc=a;var b=new ListView({x:d.rx-1,y:d.ry,width:d.rw,height:d.rh,portPos:POS.LEFT,renderer:function(g){var h=DEBUG?g.RESOURCE_FIBER_ID+"#":"";return h+g.FIBER_NO+". "+g.FIBER_NAME+" - "+g.NOTE}});TL._rc=b;var c=new Container({x:d.mx-50,y:d.my,width:d.mw+100,height:d.mh});TL._mc=c;TL.objects.push(a);TL.objects.push(b);TL.objects.push(c);TL.objects.push(new SampleLink());TL.fg=d.ctx;TL.bg=d.bgCtx;TL.cvs=d.cvs;TL.bgCvs=d.bgCvs;TL.regEvents();TL.regBufferImages()},initData:function(a){TL.curStationId=a;ui.initData(a)},loadLeftData:function(d){var a=d.total;var c=TL._lc;c.clear();LM.ports.f=[];for(var b=0;b<a;b++){c.add(d.rows[b])}TL.links=[];LM.generateLinks()},loadRightData:function(d){var a=d.total;var c=TL._rc;c.clear();LM.ports.F=[];for(var b=0;b<a;b++){c.add(d.rows[b])}TL.links=[];LM.generateLinks()},loadMidData:function(j){var c=j.total;var f=TL._mc;f.clear();LM.ports.u=[];LM.ports.U=[];LM.ports.p=[];var d=false;for(var a=0;a<c;a++){d=d||(j.rows[a].NAME=="OTDR")}TL.isRTU=d;var h=d?160:100;var g=5;for(var a=0;a<c;a++){var b=j.rows[a];b.x=h;b.y=g;var k=Widget.create(b);f.add(k);if(b.NAME!="OTDR"){g+=k.height+50}}TL.links=[];LM.generateLinks()},regEvents:function(){TL.cvs.onmousedown=TL.onmousedown;TL.cvs.onmouseup=TL.onmouseup;TL.cvs.onmousemove=TL.onmousemove;document.onkeydown=TL.onkeydown},onclick:function(b){console.log("TL.onclick");for(var a=0;a<TL.objects.length;a++){var c=TL.objects[a];if(c instanceof IObject){}}for(var a=0;a<TL.links.length;a++){}},onmousedown:function(f){if(f.button!=0){return true}getMousePos(f);var d=TL.curPos;TL.startPos.x=d.x;TL.startPos.y=d.y;var a=d.x;var h=d.y;TL.lastPos.x=d.x;TL.lastPos.y=d.y;TL.mouseState=MOUSE_STATE.MOUSEDOWN;var c=true;TL.selectedLink=null;for(var b=TL.links.length-1;b>=0;b--){var g=TL.links[b];if(g instanceof ZLink){c&=!g.onmousedown(d)}}for(var b=TL.objects.length-1;c&&b>=0;b--){var g=TL.objects[b];if(g instanceof IObject){c=!g.onmousedown(d)}}for(var b=0;b<TL.links.length;b++){}if(f&&f.stopPropagation){f.stopPropagation()}else{f.cancelBubble=true}TL.fireEvent("mouse_down",d);return false},onmouseup:function(c){TL.fireEvent("mouse_up",TL.curPos);TL.mouseState=MOUSE_STATE.NONE;var b=true;for(var a=TL.objects.length-1;b&&a>=0;a--){var d=TL.objects[a];if(d instanceof IObject){b=!d.onmouseup(TL.curPos)}}},onmousemove:function(b){TL.lastPos.x=TL.curPos.x;TL.lastPos.y=TL.curPos.y;getMousePos(b);var a=TL.curPos.x;var h=TL.curPos.y;TL.mouseState=(TL.mouseState!=MOUSE_STATE.NONE?MOUSE_STATE.DRAG:MOUSE_STATE.NONE);var d=true;for(var c=TL.links.length-1;d&&c>=0;c--){var f=TL.links[c];d=!f.inRegion(a,h)}for(c=TL.objects.length-1;d&&c>=0;c--){f=TL.objects[c];d=!f.inRegion(a,h)}var g=TL.mouseState==MOUSE_STATE.DRAG?TL.getDelta():null;for(c=0;c<TL.objects.length;c++){f=TL.objects[c];if(f instanceof IObject){f.onmousemove(TL.curPos,g)}}for(c=0;c<TL.links.length;c++){f=TL.objects[c];if(f instanceof IObject){f.onmousemove(TL.curPos,g)}}e("inspector").html("["+a+", "+h+"]");if(b&&b.stopPropagation){b.stopPropagation()}else{b.cancelBubble=true}},onkeydown:function(a){switch(a.keyCode){case 46:TL.deleteLink();break;default:}},draw:function(){var d=TL.bg;d.clearRect(0,0,TL.width,TL.height);for(var c=0,a=TL.objects.length;c<a;c++){TL.objects[c].draw(d)}for(c=0,a=TL.links.length;c<a;c++){TL.links[c].draw(d)}var b=TL.fg;b.clearRect(0,0,TL.width,TL.height);b.drawImage(this.bgCvs,0,0)},showTip:function(c,b,a){TL.tip.setGPS(c.Lng,c.Lat);TL.tip.visible=true;TL.tip.setTitle(b);TL.tip.setText(a)},render:function(){TL.draw();window.requestAnimationFrame(TL.render)},imgs:{},base64imgs:{up:"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAADP0lEQVQ4T5VTa0iTURh+vl2+Xd3FVrs4y8u0IjArKudMLcJa4dJpUdBFW5YFRtndoszC+hW0kEi6kJUZM5N+FRGtiAqLIUVRTVtbpqUubc52c1vfRIcR/eiF8+NweJ/zvs+FwN9Fy83NJVksFpfH43GpZzpVPqqGZTKZt76+PjCxhZhwIdRqNVsikcikUsmCGapUtUAUIyEQZvhHgp7unu+dHz92mN1u9wePxzNgNptHIr1RAK1WKxAKhXOzszVlxUXFOpZIxne6gBADEFBz8GkBvH/dbr3ReOuiw+FoEYvF9sg0owDUz5z4+PiMMkPJqcw87cLGR31offwNn/pI8IR8JCtCmJMQRJFmKuSkJ3Du3FmjxdJuNJlMXREAmk6nS6JO7brNhtVHr3Wi4XkA7nAICUI2iudz0d4ZBp30QSnywrAsFQr6d3f18RO7+/v7TZG9OelpaUU1x4/UNb8RCM60WOEFGzQOgcubE7BkFhemZy40v+wDj0kgQTyC7fpUvLjf+uBqw/WdRH5+viRLozlUXlFRWXLWAbMtBtNEQzAalFg0nR/l+M7LAdy1uMCjh1GgmQYV29ZbdfjIekKv1ytzsrNqlm8sK117rB0OTxz08xioL49HR48fyTISDmcAchETNa3d6HL6sDhNCm3ST9fefQe2RAFWGraVrj3chq+/4qCdy4ZiMgl6iED1minY1dAN7Wwu2mxedPW4kZOmQF7qoKtyz8Et0RVKK3ZVbjVa0faZDT7Hjy/uII6tkONAoQQ7rvTim2sYk/lh8PxB6LNSoIz51FtVRa0wSmJ6elEtReLNd2LByaYOMEkSg4Eg9ufJUVUgxp5LA/g65AWLUkIVG0K5Pgkv7o2ROC5jISVjvqF0de11O24+HYaXZGISg4NZciKiCUJECHJxGBuWT4WC2eM+WT0m47iREhMTM0o2bTytXrpswa0nLjQ96Yaz9wdiYxlQThJjpkqAVZlSxDGGAufr6oyvLJaokUalGrfy4pzsbQX6Qh1bKOH++BXJDR0CFgk+I4y3by22xsamC3a7/fYfVh4TOxomhUKakaRKyeTyYmJpNND9Pp93wOm0Wa0fHg4O/iNME1L5X3H+DWVEV/q2PdRcAAAAAElFTkSuQmCC",down:"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAADPklEQVQ4T5VTaUiTYRz/vTvene6w1Q5nqS0rArOics7UIqwVLp0aBh3asiwwym6LMgvrU9BCIukgKzPWIX0qIloSJRZDiqKatrZMS13anO1yW+9EhxF96P/t4eH/e57fReDvoWVnZ5MsFovL4/G41DWdGh81wzKZzFtfXx+YuEJMOBBqtZotkUhkCoU0LUk1I53Li4ml0UD3+3zeAafTZrV+eDw46P7g8XgGzGbzSGQ3CqDVagVCoXD+0qzMbXn6fB1bKOH++BV5jA4BiwSfEcbbtxZbY2PTBbvdfkcsFtsjvxkFoF7mJCYmppVs2nhavXzFolstLjS1dMPZ+wOxsQwoJ4kxWyXAmnQp4hhDgfN1dcZXFovRZDJ1RQBoOp0uKV+nq801lBbVXrfj5rNheEkmJjE4mCMnwAYNISIEuTiMDSunQsHscZ+sPrG7v7/fFOHNSU1NLag9fqTu5jux4GRTB5gkicFAEPtz5KjKE2PPpQF8HfKCRfqgig2hXJ+E1gfNj642XN9J5ObmSjI0mkOlFbsqtxqtaPvMBp/jxxd3EMdWyXEgX4IdV3rxzTWMyfwweP4g9BkzoIz51FtVdWQ9odfrlVmZGTWrDdtKiw+34euvOGjns6GYTIIeIlC9dgp2NXRDO5eLNpsXXT1uZKUokJM86Krcc3BLFGDlxrLS4mPtcHjioF/AQH15PDp6/JguI+FwBiAXMVHT3I0upw9LU6TQJv107d13YEuUQnlFRWXJWQfMthhMEw3BaFBiyUx+NCb3Xg7gvsUFHj2MPM00qNi23qrDFIVREVNSCmooEW+/EQjO3LXCG9GdQ+Dy5gQsm8OF6bkLt1/2gcckkCAewXZ9Mlofjok4biNlZe26zYaio9c60fAiAHc4hAQhG4ULuWjvDINOOaAUeWFYkQwF/bu7+viYjeNBio+PTyszlJxKz9EubnzSh+an3/CpjwRPyMd0RQjzEoIo0EyFnPQEzp07a7RY2qNBGuU5HuXMTE1ZYUGhjiWS8Z0uIMQABFSl+LQA3r9ut95ovHXR4XDc/SPKY0pFyySVShbNUiWrBaIYCYEwwz8S9HT3fO/8+LHD7Hb/o0wTWvlfdf4NzupX+v/vzKIAAAAASUVORK5CYII=",left:"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAADmElEQVQ4T2VTa0xbZRh+vnNpD/TC6toIQUatDLyRjVZTncGxShg1DhUF0W7LtuAFF7clZuIloxXQbSxO1CVzugvCUDdcDGrQ1crqbWFIM6byA3B0DcNi2wms0PbQy/EcIjrj9+f7vvfN+7y35yH476GqqqpUDMNk0TRyCGGWSG5BEKaTyeR4IpHwd3V1hUVTajGMLD5MJhNrMBiytFptmbW8vLqw8PYVCkU6R1ECwrPR2NDPv1740uk8GQgEnGNjY36PxxOXYhcASkpKGJ1Opzcajc9s2bLpaaLOVF7wpjAxHQMr5lqmTkdBPpCYmZztaGs/NDAw8G4wGLzkdrsTCwBi2Tq9Xr/Z7rA3j05z7GffXkRgTobZpAB5KgkFBGg0NKzFBtycEY43NTXv8nq9R8V2gkQqPT8/37xz5/NHVbkrl7/VPYq5qAyCOITspSzkDIXLVyKI8RSWyCnUVS5DzHfe29Kyb+PIyMg5UlFRoVpRWLj9JYe96e2vJtA3CsioJGpWaXB/kQbuoRkcck9BJnBIJRIw3SLDjlId9r7+qsMzOLifiOVnWlYX736k9rlN294fR2SOwfridFStyoAvFMebPUHMJxmkpVj8/mcEclkEb9QuR8eR1g/O/nD2RVJZWZlrXXvf3jXVzz5W0zIBq5FG46OZC8tJiQOkqH/3/NC+ccyG/Ti8YyV6uo6d+MbprJcAbigvt+wpfXir7QH7MKBk8c7G62G5TYHf/DyOfz+DNJZCktA49l0Y6vnLONVYhC9OtH14xuWql1q4znynqeHJbfXbbbt/gXuShlYBtD+Vg+ICFXZ98gdaTwfAsDQohoZRK+DUC3l470Drgf7+fjsROcAV5OU92PRa8+HuIUr5yskr4EgSS5U07jAoIG4SP46EwadozPNxNNbkYN2t0UhDg6N2eHj4U4kHEn1zLWtWN2+o2/qEvf0iPu+bRiiRCVqYB4sECE1ASBTV9yjhePxGfHTk4Mcu15mXRR74FomUJpfLzZs3rN9jLi0zd/RehfMnP3z+EGiSQHaWFmV334Tqe2UYdLn629qP1/M8f04EiC5qgdhsNpUoGlOpxVJXsc66llVmqCd5ATRDoBZJxUSmrnZ395z+urf3ICHE09nZKYlK+EdMf1M6TbyzRUHdpdfnFqXLOY1kj/GxqbFLvvOhUKhP/E5Imf+nxmtULc1EHovFFCzLcpI9Ho/HOI6bEwP5a6Us+f4CvPduztV//qsAAAAASUVORK5CYII=",right:"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAADmUlEQVQ4T2VTa0xbZRh+TnvKOS10wNZWKpCuTOAH6liKjMwbw9opUYhMNpWB7KJzyVTUjG0K2LJ4wWXJNjPxQrZlWxXB+4xlBDsmcdpoyxa8wbCWEQKmXTpKC23Pze8sssz4/vm+733zPt97eR4K/zVFbW2tlqZpo1KpzKUoKkMOSxJ/VRAwyfP8dG9v7xxxiYtp1OLFYrGo8vLyjAaDwfagzbah6PZbV2rT1KwoUojF5uMjI79cdPX19YRCoX6/3z/t9Xo5OfcaQHl5Oa3X65eXlJQ8U9/YsJ1Oz0obHQMuR+bBKYDsDBYrzQpIkZno0aPH3/P5fO8Gg8HA4OAgfw2AlK03m81bWltb9v0xq1W5hvwIhwXECH5CoUSakoIhNYmqe1cgPyPOOeyOlkAgcIy0E6Tk0gsKClY3N+86wZpWmTs/u4yrCREsIyJnmQYJXsTUFQ4UGUKqOonnq/MxN3Hh0v79B7aMjY15qKqqKq2luPjF3S+/aj84EIT39yQUNI0kFcf28kyUF6Xjm+Ewus+HkRSVKMsHnnsgG2/YHa0XR0YOUaT8rDV3rXmzfmvTky91XUIiqcHNSzVYUHBIUfJ4oVIPk06F3vOzODU0D00qj8NP5eLTrrePu88N7aVqampM99lsHZW1mzduO3gBaVojvtiVe325IlmYggxStrZPZuDyCehuzsbZnnc+dp35drcMkLPWau14aGPjE+vbhhFJycHme7RQSgIWOBGb7k7HLUYG7l9jePbE30CUw9eOQgx8fsTZ1+feI7ewtLS01PH0zqad698ahy9EQeQF8JyApnUG7Hv0JgyNzqHh/UmEYmTlWQKce2/DB4c7Dnl+8rZThANsYWHhI+3t9q7Tv6k1bd2TSGFUYBQC7iwglZBF/+yP4UpUQFxS4rUNy1BdJEZbX2nZNjo+/qXMA5m+Jqt17euPb93xmP2jv9DzfZTQVw1JkMCBhkClQEfP4OGyDDgaVuBk55EP3WfPtRAeTCwSSc0wzOrGhk0dxVZrac93SfT/8CempkMQJBomow62O4yor1gCz0C/59jJU3sSiYSHACwsaoGqq6vTSpJkub+iYkd1deU6XpO5JELII/ASshgKXHQ28tVp15kBt7uTiMzrdDplUUnXxfQvpdXkzNbpdGV5y02rWIbNlP3ziXg4EJgYJkL6kTyn5J//p8YbVC3PhInH46kqlYqV/RzHxVmWjZHExI1SlmP/ALUFfc7X7cZxAAAAAElFTkSuQmCC",port:"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAADFklEQVQ4T41Ta0iTYRQ+3/ftqqZDNxu1xSKpeR2EZOkywj/VCCmTXIoERZkUqT+SECEFichSKUELglqmRj+MZldDsIsgmmiZJquk1qK8bOk+d/lunbcLBCV04PDyXs5znnPe51Dw0+iEhAQ5TdPL1Gp1rCAIWoZhlkmSpBRFUcB1Ed/M4dks3vnGxsYCuBdIIEWCDQaDUqPRaBHAVFpammez7dgdER2nC/KMgqYkUS4F2DcTr4fq689dnpycHGVZ1jM1NeXHWJ4ymUyqyMjIeJlMlnij7fp5ndGceH/ET427gxDgJKAxRUwkAxsTIiDDRC02X2w4197eecvv939AkAUKqeuQdlJHe1uLQms2Nz+Ygel5AeQMUqMBJAlAROcECbITo6DYGsGdrKys6unp6eZ5/iOVmppqLisrK88vKD5U1zUL76fDoJKTyv42NiRCXoYGtqzyfb1+o6Pe4XB0UMnJyVnOO7dvjX/T61sezYBaiWmXMB5ZxKgZqNurlRZm3o3n59uLCIPtgwP9XWfvLSqevmGXzP4bMxAWoWpXPFhWCAuZmZm5VFpa2s7h4eGuCscXevRj8L8AKnboICdJEc7YkL6TAGzrf/6k63R3QPn4pR/UiqVLICwWsQ9n9unBspKft1qtuVRKSkpmZ0dbpyu0xnDq5meIUP67gSSY4yWIj5FD6wGdNOeeHCkoLNpPWSyWtXa7/fCRo2Xlx67OUANvWYj6RyPJd5JfOJG7HLL0bnfrpSu1TqfzLmU2m+NQouaLF5oajYnZ6ceveeA1ikjOUMD8qoZkxng4mBMHJdmyQElJSfnExEQvSvwTEZISJaxVqVTrmhobapPXWzc5ni3Qva/84GV5kKEUDVoF7MmIhs2rOV91dXVNX1/fw1Ao5Ha5XCwpmMhZiRaLbrTZbFuLCu1FOr3RGAKFihJEQQz7fC+GBvtqamqvBoNBF8dxX0gwGajfHSOCkuNBFLLRILVYXKNxr8BpFHFPHnvJRHq93nmPxxP6cxr/1B2NQDISSBxpMggkoea5MJrb7Q7juYhOWvLDvgNGAFwpttomFwAAAABJRU5ErkJggg==",close:"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABWklEQVQ4T2NkoBAwUqifAcWAMzNN/oMM/P//P8O/f//AtEX2BbyWwCVBmo3TTiM5aDeYfXhCAYNd4TWchoAlQJr55WyBrIdYfCTP8OjyFoa/f/+CMchl//7/A7MDml4wgg04PcP4v0n6GZKCY221KINWzCFIGJycZvjfLDMDyGIm0pC/DCvKyhhUw/dCDDg+Rf+/RXYWkZohypYUFTFoRB2AGHB0ku5/q9wcBoafv4gzhJ2NYWF+PoN27GGIAYf6tf7bFuQzMHz4QJwBAgIM83NzGXTjj0IMONCr8d++qJCB4ekz4gyQlmKYm53NoJ94HGLA3i7V/06lJQwMd+4QZ4CKCsPszEwGw+STEAN2tSv9d60oZ2C4eo04A7S1GGZlZDAYp55GJOWtTbL/YQkElFhgCQeWeOCJCCqnHrmfQVhYGDUvXL58+f+PHz+IcgUHBweDrq4uJCVSAgBrOpWwN42yugAAAABJRU5ErkJggg==",leaf:"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABQ0lEQVQ4T2NkoBAwUqifAWxA96pX/5EN+v2HgeHn7/8M33/9Z/j2E4iR6B+/GBh+AOV+//nPcLxPjhFugLiEKMPff0CJv/8Z/gAlfwHxb6BCkEE/Qfzf/4AYxGYAav7HcObCLYbzs40RBsBcANLwDWjLlx//GL58/w+kgfj7P6BLgK4CGgQD3z48Y7g4zwJiwPJtZ/5HeBoTDI6PQEPA4AcDg1tUKsOpbXMgBizedOJ/jK85AyOEiwL+/4fYiqz5w4+PDKGJxQynt8+F6Ji37vD/xEAbnC5A1wxSiGLArJX7/qeGOcINaGxsxOudhIQChtBMJBdMW7rrf2aUK0lh4BqZgvDC5EXb/+fEepAfBjADcDmBYBigG0ByGOB0ATDeP0LjHcT6AIx/UBr4AKRSkQMRZMCi5WsIBiK6Ang6IFknkgYA+YrcEXUbL2MAAAAASUVORK5CYII=",open:"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABaklEQVQ4T2NkoBAwUqifAcWAMzNN/oMM/P//P8O/f//AtEX2BbyWwCVBmo3TTsMd9PvPXzD7xBQ9BrvCazgNAUsgNM/F4qNkhn3dagx///4FY5DL/v3/B2YHNL1gBBtweobxf5P0M0AWNgNgZiajGL62WpRBNXwvJAxOTjP8b5Z5joHh5xTiwpSdk2FFWTGDXMA2BsZjsyTBAUcskFE3Z5DVMmFY0tbOoBi8E2KAZWoDUD8zATP+Mtw+cJnhze21DJa22QwLp7cwqITtQTPg53fchgCd/ePjW4bzqyYwmFuHAw2YxqAWsQ8SBnBXfPiA3xUCMgynFtYwSAorMezafpBBI+oAmgEgbzx9jNsQaTWGGztnM/x49Yzh9LE7DFoxhxApEe6KO3fwuuIjJyvDtW0LGC6ffc6gE3cE1YD7N79gaIYla3ACQsKy/lsZxMTEUPPC2bNn/3//jicgkYzn5ORkMDY2hqRESgAAE1ek/FnBZLMAAAAASUVORK5CYII=",},regImage:function(a,b){var c=new Image();c.src=b;TL.imgs[a]=c},regBase64Image:function(a,b){var c=new Image();c.src=b;TL.imgs[a]=c},regBufferImages:function(){for(var b in this.base64imgs){var a=new Image();a.src=this.base64imgs[b];TL.imgs[b]=a}},testImg:function(b){var a=document.createElement("img");a.src=TL.imgs[b]},getImage:function(b){if(b in TL.imgs){return TL.imgs[b]}else{if(b in TL.base64imgs){var a=new Image();a.src=TL.base64imgs[b];return a}else{return null}}},getDelta:function(){return{x:TL.curPos.x-TL.lastPos.x,y:TL.curPos.y-TL.lastPos.y}},createCanvasImage:function(a,b,c){var d=document.createElement("canvas");d.width=b;d.height=c},on:function(d,c,b){var a=TL.listeners[d];if(!a){TL.listeners[d]=[];a=TL.listeners[d]}if(!b){b=window}a.push({callback:c,scope:b})},fireEvent:function(h,d){var g=Array.prototype.slice.call(arguments,1);var c=TL.listeners[h];for(var b=0;b<g.length;b++){}if(!!c){for(var b=0,a=c.length;b<a;b++){var j=c[b].callback;var f=c[b].scope;j.apply(f,g)}}},un:function(d,f){var c=TL.listeners[d];if(!!c){for(var b=0,a=c.length;b<a;b++){if(f==c[b].callback){c.splice(b,1,0);break}}}},addLink:function(a){var f=new ZLink(a.startNode,a.endNode);f.stationId=TL.curStationId;f.setConnType(a.connType);var d="";if(f.connType==2||f.connType==4){var b=/\[.*\]/ig.exec(Ext.getCmp("_tutu_rightCombo_").getRawValue())[0];b=b.substr(1,b.length-2);var c=parseInt(a.endNode.parent.text);d=b+"("+c+")"}a.startNode.linkText=d;TL.fireEvent("create_link",{STATION_ID:f.stationId,A_END_ID:f.aEndId,Z_END_ID:f.zEndId,CONN_TYPE:f.connType,FIBER_INFO:d,rawLink:f})},deleteLink:function(){if(!TL.selectedLink){return}TL.fireEvent("delete_link",{STATION_ID:TL.selectedLink.stationId,A_END_ID:TL.selectedLink.aEndId,Z_END_ID:TL.selectedLink.zEndId,CONN_TYPE:TL.selectedLink.connType,rawLink:TL.selectedLink})},removeLinkByType:function(a){var b=[];if(!a.length){a=[a]}for(var c=0;c<TL.links.length;c++){var d=TL.links[c];if(a.indexOf(d.connType)<0){b.push(d)}}TL.links=b},addSuccessCallback:function(a){LM.data.push(a)},deleteSuccessCallback:function(a){LM.deleteData(a)},isSameFiber:function(){return Ext.getCmp("_tutu_leftCombo_").getValue()==Ext.getCmp("_tutu_rightCombo_").getValue()},setStationName:function(b){var a=document.getElementById("title");a.innerHTML="FTTS光纤连接图 - "+b}};