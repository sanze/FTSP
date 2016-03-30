/**
 * jQuery EasyUI 1.1.2
 * 
 * Licensed under the GPL:
 *   http://www.gnu.org/licenses/gpl.txt
 *
 * Copyright 2010 stworthy [ stworthy@gmail.com ] 
 * 
 */
(function($){
function _1(_2){
var _3=$(">div.tabs-header",_2);
var _4=0;
$("ul.tabs li",_3).each(function(){
_4+=$(this).outerWidth(true);
});
var _5=$("div.tabs-wrap",_3).width();
var _6=parseInt($("ul.tabs",_3).css("padding-left"));
return _4-_5+_6;
};
function _7(_8){
var _9=$(">div.tabs-header",_8);
var _a=0;
$("ul.tabs li",_9).each(function(){
_a+=$(this).outerWidth(true);
});
if(_a>_9.width()){
$(".tabs-scroller-left",_9).css("display","block");
$(".tabs-scroller-right",_9).css("display","block");
$(".tabs-wrap",_9).addClass("tabs-scrolling");
if($.boxModel==true){
$(".tabs-wrap",_9).css("left",2);
}else{
$(".tabs-wrap",_9).css("left",0);
}
var _b=_9.width()-$(".tabs-scroller-left",_9).outerWidth()-$(".tabs-scroller-right",_9).outerWidth();
$(".tabs-wrap",_9).width(_b);
}else{
$(".tabs-scroller-left",_9).css("display","none");
$(".tabs-scroller-right",_9).css("display","none");
$(".tabs-wrap",_9).removeClass("tabs-scrolling").scrollLeft(0);
$(".tabs-wrap",_9).width(_9.width());
$(".tabs-wrap",_9).css("left",0);
}
};
function _c(_d){
var _e=$.data(_d,"tabs").options;
var cc=$(_d);
if(_e.fit==true){
var p=cc.parent();
_e.width=p.width();
_e.height=p.height();
}
cc.width(_e.width).height(_e.height);
var _f=$(">div.tabs-header",_d);
if($.boxModel==true){
_f.width(_e.width-(_f.outerWidth()-_f.width()));
}else{
_f.width(_e.width);
}
_7(_d);
var _10=$(">div.tabs-panels",_d);
var _11=_e.height;
if(!isNaN(_11)){
if($.boxModel==true){
var _12=_10.outerHeight()-_10.height();
_10.css("height",(_11-_f.outerHeight()-_12)||"auto");
}else{
_10.css("height",_11-_f.outerHeight());
}
}else{
_10.height("auto");
}
var _13=_e.width;
if(!isNaN(_13)){
if($.boxModel==true){
_10.width(_13-(_10.outerWidth()-_10.width()));
}else{
_10.width(_13);
}
}else{
_10.width("auto");
}
};
function _14(_15){
var _16=$.data(_15,"tabs").options;
var tab=_17(_15);
if(tab){
var _18=$(_15).find(">div.tabs-panels");
var _19=_16.width=="auto"?"auto":_18.width();
var _1a=_16.height=="auto"?"auto":_18.height();
tab.panel("resize",{width:_19,height:_1a});
}
};
function _1b(_1c){
var cc=$(_1c);
cc.addClass("tabs-container");
cc.wrapInner("<div class=\"tabs-panels\"/>");
$("<div class=\"tabs-header\">"+"<div class=\"tabs-scroller-left\"></div>"+"<div class=\"tabs-scroller-right\"></div>"+"<div class=\"tabs-wrap\">"+"<ul class=\"tabs\"></ul>"+"</div>"+"</div>").prependTo(_1c);
var _1d=[];
var _1e=$(">div.tabs-header",_1c);
$(">div.tabs-panels>div",_1c).each(function(){
var pp=$(this);
_1d.push(pp);
_2b(_1c,pp);
});
$(".tabs-scroller-left, .tabs-scroller-right",_1e).hover(function(){
$(this).addClass("tabs-scroller-over");
},function(){
$(this).removeClass("tabs-scroller-over");
});
cc.bind("_resize",function(){
var _1f=$.data(_1c,"tabs").options;
if(_1f.fit==true){
_c(_1c);
_14(_1c);
}
return false;
});
return _1d;
};
function _20(_21){
var _22=$.data(_21,"tabs").options;
var _23=$(">div.tabs-header",_21);
var _24=$(">div.tabs-panels",_21);
if(_22.plain==true){
_23.addClass("tabs-header-plain");
}else{
_23.removeClass("tabs-header-plain");
}
if(_22.border==true){
_23.removeClass("tabs-header-noborder");
_24.removeClass("tabs-panels-noborder");
}else{
_23.addClass("tabs-header-noborder");
_24.addClass("tabs-panels-noborder");
}
$(".tabs-scroller-left",_23).unbind(".tabs").bind("click.tabs",function(){
var _25=$(".tabs-wrap",_23);
var pos=_25.scrollLeft()-_22.scrollIncrement;
_25.animate({scrollLeft:pos},_22.scrollDuration);
});
$(".tabs-scroller-right",_23).unbind(".tabs").bind("click.tabs",function(){
var _26=$(".tabs-wrap",_23);
var pos=Math.min(_26.scrollLeft()+_22.scrollIncrement,_1(_21));
_26.animate({scrollLeft:pos},_22.scrollDuration);
});
var _27=$.data(_21,"tabs").tabs;
for(var i=0,len=_27.length;i<len;i++){
var _28=_27[i];
var tab=_28.panel("options").tab;
var _29=_28.panel("options").title;
tab.unbind(".tabs").bind("click.tabs",{title:_29},function(e){
_39(_21,e.data.title);
});
tab.find("a.tabs-close").unbind(".tabs").bind("click.tabs",{title:_29},function(e){
_2a(_21,e.data.title);
return false;
});
}
};
function _2b(_2c,pp,_2d){
_2d=_2d||{};
pp.panel($.extend({},{selected:pp.attr("selected")=="true"},_2d,{border:false,noheader:true,closed:true,doSize:false,iconCls:(_2d.icon?_2d.icon:undefined),onLoad:function(){
$.data(_2c,"tabs").options.onLoad.call(_2c,pp);
}}));
var _2e=pp.panel("options");
var _2f=$(">div.tabs-header",_2c);
var _30=$("ul.tabs",_2f);
var tab=$("<li></li>").appendTo(_30);
var _31=$("<a href=\"javascript:void(0)\" class=\"tabs-inner\"></a>").appendTo(tab);
var _32=$("<span class=\"tabs-title\"></span>").html(_2e.title).appendTo(_31);
var _33=$("<span class=\"tabs-icon\"></span>").appendTo(_31);
if(_2e.closable){
_32.addClass("tabs-closable");
$("<a href=\"javascript:void(0)\" class=\"tabs-close\"></a>").appendTo(tab);
}
if(_2e.iconCls){
_32.addClass("tabs-with-icon");
_33.addClass(_2e.iconCls);
}
_2e.tab=tab;
};
function _34(_35,_36){
var _37=$.data(_35,"tabs").options;
var _38=$.data(_35,"tabs").tabs;
var pp=$("<div></div>").appendTo($(">div.tabs-panels",_35));
_38.push(pp);
_2b(_35,pp,_36);
_37.onAdd.call(_35,_36.title);
_7(_35);
_20(_35);
_39(_35,_36.title);
};
function _3a(_3b,_3c){
var pp=_3c.tab;
pp.panel($.extend({},_3c.options,{iconCls:(_3c.options.icon?_3c.options.icon:undefined)}));
var _3d=pp.panel("options");
var tab=_3d.tab;
tab.find("span.tabs-icon").attr("class","tabs-icon");
tab.find("a.tabs-close").remove();
tab.find("span.tabs-title").html(_3d.title);
if(_3d.closable){
tab.find("span.tabs-title").addClass("tabs-closable");
$("<a href=\"javascript:void(0)\" class=\"tabs-close\"></a>").appendTo(tab);
}else{
tab.find("span.tabs-title").removeClass("tabs-closable");
}
if(_3d.iconCls){
tab.find("span.tabs-title").addClass("tabs-with-icon");
tab.find("span.tabs-icon").addClass(_3d.iconCls);
}else{
tab.find("span.tabs-title").removeClass("tabs-with-icon");
}
_20(_3b);
$.data(_3b,"tabs").options.onUpdate.call(_3b,_3d.title);
};
function _2a(_3e,_3f){
var _40=$.data(_3e,"tabs").options;
var _41=$.data(_3e,"tabs").tabs;
var tab=_42(_3e,_3f,true);
if(!tab){
return;
}
if(_40.onBeforeClose.call(_3e,_3f)==false){
return;
}
tab.panel("options").tab.remove();
tab.panel("destroy");
_40.onClose.call(_3e,_3f);
_7(_3e);
var _43=_17(_3e);
if(_43){
_39(_3e,_43.panel("options").title);
}else{
if(_41.length){
_39(_3e,_41[0].panel("options").title);
}
}
};
function _42(_44,_45,_46){
var _47=$.data(_44,"tabs").tabs;
for(var i=0;i<_47.length;i++){
var tab=_47[i];
if(tab.panel("options").title==_45){
if(_46){
_47.splice(i,1);
}
return tab;
}
}
return null;
};
function _17(_48){
var _49=$.data(_48,"tabs").tabs;
for(var i=0;i<_49.length;i++){
var tab=_49[i];
if(tab.panel("options").closed==false){
return tab;
}
}
return null;
};
function _4a(_4b){
var _4c=$.data(_4b,"tabs").tabs;
for(var i=0;i<_4c.length;i++){
var tab=_4c[i];
if(tab.panel("options").selected){
_39(_4b,tab.panel("options").title);
return;
}
}
if(_4c.length){
_39(_4b,_4c[0].panel("options").title);
}
};
function _39(_4d,_4e){
var _4f=$.data(_4d,"tabs").options;
var _50=$.data(_4d,"tabs").tabs;
if(_50.length==0){
return;
}
var _51=_42(_4d,_4e);
if(!_51){
return;
}
var _52=_17(_4d);
if(_52){
_52.panel("close");
_52.panel("options").tab.removeClass("tabs-selected");
}
_51.panel("open");
var tab=_51.panel("options").tab;
tab.addClass("tabs-selected");
var _53=$(_4d).find(">div.tabs-header div.tabs-wrap");
var _54=tab.position().left+_53.scrollLeft();
var _55=_54-_53.scrollLeft();
var _56=_55+tab.outerWidth();
if(_55<0||_56>_53.innerWidth()){
var pos=Math.min(_54-(_53.width()-tab.width())/2,_1(_4d));
_53.animate({scrollLeft:pos},_4f.scrollDuration);
}else{
var pos=Math.min(_53.scrollLeft(),_1(_4d));
_53.animate({scrollLeft:pos},_4f.scrollDuration);
}
_14(_4d);
_4f.onSelect.call(_4d,_4e);
};
function _57(_58,_59){
return _42(_58,_59)!=null;
};
$.fn.tabs=function(_5a,_5b){
if(typeof _5a=="string"){
switch(_5a){
case "options":
return $.data(this[0],"tabs").options;
case "tabs":
return $.data(this[0],"tabs").tabs;
case "resize":
return this.each(function(){
_c(this);
_14(this);
});
case "add":
return this.each(function(){
_34(this,_5b);
});
case "close":
return this.each(function(){
_2a(this,_5b);
});
case "getTab":
return _42(this[0],_5b);
case "getSelected":
return _17(this[0]);
case "select":
return this.each(function(){
_39(this,_5b);
});
case "exists":
return _57(this[0],_5b);
case "update":
return this.each(function(){
_3a(this,_5b);
});
}
}
_5a=_5a||{};
return this.each(function(){
var _5c=$.data(this,"tabs");
var _5d;
if(_5c){
_5d=$.extend(_5c.options,_5a);
_5c.options=_5d;
}else{
var t=$(this);
_5d=$.extend({},$.fn.tabs.defaults,{width:(parseInt(t.css("width"))||undefined),height:(parseInt(t.css("height"))||undefined),fit:(t.attr("fit")?t.attr("fit")=="true":undefined),border:(t.attr("border")?t.attr("border")=="true":undefined),plain:(t.attr("plain")?t.attr("plain")=="true":undefined)},_5a);
var _5e=_1b(this);
_5c=$.data(this,"tabs",{options:_5d,tabs:_5e});
}
_20(this);
_c(this);
var _5f=this;
setTimeout(function(){
_4a(_5f);
},0);
});
};
$.fn.tabs.defaults={width:"auto",height:"auto",idSeed:0,plain:false,fit:false,border:true,scrollIncrement:100,scrollDuration:400,onLoad:function(_60){
},onSelect:function(_61){
},onBeforeClose:function(_62){
},onClose:function(_63){
},onAdd:function(_64){
},onUpdate:function(_65){
}};
})(jQuery);

