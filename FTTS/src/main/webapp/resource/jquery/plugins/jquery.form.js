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
function _1(_2,_3){
_3=_3||{};
if(_3.onSubmit){
if(_3.onSubmit.call(_2)==false){
return;
}
}
var _4=$(_2);
if(_3.url){
_4.attr("action",_3.url);
}
var _5="easyui_frame_"+(new Date().getTime());
var _6=$("<iframe id="+_5+" name="+_5+"></iframe>").attr("src",window.ActiveXObject?"javascript:false":"about:blank").css({position:"absolute",top:-1000,left:-1000});
var t=_4.attr("target"),a=_4.attr("action");
_4.attr("target",_5);
try{
_6.appendTo("body");
_6.bind("load",cb);
_4[0].submit();
}
finally{
_4.attr("action",a);
t?_4.attr("target",t):_4.removeAttr("target");
}
var _7=10;
function cb(){
_6.unbind();
var _8=$("#"+_5).contents().find("body");
var _9=_8.html();
if(_9==""){
if(--_7){
setTimeout(cb,100);
return;
}
return;
}
var ta=_8.find(">textarea");
if(ta.length){
_9=ta.val();
}else{
var _a=_8.find(">pre");
if(_a.length){
_9=_a.html();
}
}
if(_3.success){
_3.success(_9);
}
setTimeout(function(){
_6.unbind();
_6.remove();
},100);
};
};
function _b(_c,_d){
if(!$.data(_c,"form")){
$.data(_c,"form",{options:$.extend({},$.fn.form.defaults)});
}
var _e=$.data(_c,"form").options;
if(typeof _d=="string"){
var _f={};
if(_e.onBeforeLoad.call(_c,_f)==false){
return;
}
$.ajax({url:_d,data:_f,dataType:"json",success:function(_10){
_11(_10);
},error:function(){
_e.onLoadError.apply(_c,arguments);
}});
}else{
_11(_d);
}
function _11(_12){
var _13=$(_c);
for(var _14 in _12){
var val=_12[_14];
$("input[name="+_14+"]",_13).val(val);
$("textarea[name="+_14+"]",_13).val(val);
$("select[name="+_14+"]",_13).val(val);
if($.fn.combobox){
$("select[comboboxName="+_14+"]",_13).combobox("setValue",val);
}
if($.fn.combotree){
$("select[combotreeName="+_14+"]",_13).combotree("setValue",val);
}
}
_e.onLoadSuccess.call(_c,_12);
_15(_c);
};
};
function _16(_17){
$("input,select,textarea",_17).each(function(){
var t=this.type,tag=this.tagName.toLowerCase();
if(t=="text"||t=="hidden"||t=="password"||tag=="textarea"){
this.value="";
}else{
if(t=="checkbox"||t=="radio"){
this.checked=false;
}else{
if(tag=="select"){
this.selectedIndex=-1;
}
}
}
});
if($.fn.combobox){
$("select[comboboxName]",_17).combobox("clear");
}
if($.fn.combotree){
$("select[combotreeName]",_17).combotree("clear");
}
};
function _18(_19){
var _1a=$.data(_19,"form").options;
var _1b=$(_19);
_1b.unbind(".form").bind("submit.form",function(){
setTimeout(function(){
_1(_19,_1a);
},0);
return false;
});
};
function _15(_1c){
if($.fn.validatebox){
var box=$(".validatebox-text",_1c);
if(box.length){
box.validatebox("validate");
box.trigger("blur");
var _1d=$(".validatebox-invalid:first",_1c).focus();
return _1d.length==0;
}
}
return true;
};
$.fn.form=function(_1e,_1f){
if(typeof _1e=="string"){
switch(_1e){
case "submit":
return this.each(function(){
_1(this,$.extend({},$.fn.form.defaults,_1f||{}));
});
case "load":
return this.each(function(){
_b(this,_1f);
});
case "clear":
return this.each(function(){
_16(this);
});
case "validate":
return _15(this[0]);
}
}
_1e=_1e||{};
return this.each(function(){
if(!$.data(this,"form")){
$.data(this,"form",{options:$.extend({},$.fn.form.defaults,_1e)});
}
_18(this);
});
};
$.fn.form.defaults={url:null,onSubmit:function(){
},success:function(_20){
},onBeforeLoad:function(_21){
},onLoadSuccess:function(_22){
},onLoadError:function(){
}};
})(jQuery);

