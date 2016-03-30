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
var _3=$(_2);
_3.addClass("tree");
return _3;
};
function _4(_5){
var _6=[];
_7(_6,$(_5));
function _7(aa,_8){
_8.find(">li").each(function(){
var _9=$(this);
var _a={};
_a.text=_9.find(">span").html();
if(!_a.text){
_a.text=_9.html();
}
_a.id=_9.attr("id");
_a.iconCls=_9.attr("icon");
_a.checked=_9.attr("checked")=="true";
_a.state=_9.attr("state")||"open";
var _b=_9.find(">ul");
if(_b.length){
_a.children=[];
_7(_a.children,_b);
}
aa.push(_a);
});
};
return _6;
};
function _c(_d){
var _e=$.data(_d,"tree").options;
var _f=$.data(_d,"tree").tree;
$("div.tree-node",_f).unbind(".tree").bind("dblclick.tree",function(){
_86(_d,this);
_e.onDblClick.call(_d,_69(_d));
}).bind("click.tree",function(){
_86(_d,this);
_e.onClick.call(_d,_69(_d));
}).bind("mouseenter.tree",function(){
$(this).addClass("tree-node-hover");
return false;
}).bind("mouseleave.tree",function(){
$(this).removeClass("tree-node-hover");
return false;
});
$("span.tree-hit",_f).unbind(".tree").bind("click.tree",function(){
var _10=$(this).parent();
_44(_d,_10[0]);
return false;
}).bind("mouseenter.tree",function(){
if($(this).hasClass("tree-expanded")){
$(this).addClass("tree-expanded-hover");
}else{
$(this).addClass("tree-collapsed-hover");
}
}).bind("mouseleave.tree",function(){
if($(this).hasClass("tree-expanded")){
$(this).removeClass("tree-expanded-hover");
}else{
$(this).removeClass("tree-collapsed-hover");
}
});
$("span.tree-checkbox",_f).unbind(".tree").bind("click.tree",function(){
var _11=$(this).parent();
_12(_d,_11[0],!$(this).hasClass("tree-checkbox1"));
return false;
});
};
function _12(_13,_14,_15){
var _16=$.data(_13,"tree").options;
if(!_16.checkbox){
return;
}
var _17=$(_14);
var ck=_17.find(".tree-checkbox");
ck.removeClass("tree-checkbox0 tree-checkbox1 tree-checkbox2");
if(_15){
ck.addClass("tree-checkbox1");
}else{
ck.addClass("tree-checkbox0");
}
_18(_17);
_19(_17);
function _19(_1a){
var _1b=_1a.next().find(".tree-checkbox");
_1b.removeClass("tree-checkbox0 tree-checkbox1 tree-checkbox2");
if(_1a.find(".tree-checkbox").hasClass("tree-checkbox1")){
_1b.addClass("tree-checkbox1");
}else{
_1b.addClass("tree-checkbox0");
}
};
function _18(_1c){
var _1d=_51(_13,_1c[0]);
if(_1d){
var ck=$(_1d.target).find(".tree-checkbox");
ck.removeClass("tree-checkbox0 tree-checkbox1 tree-checkbox2");
if(_1e(_1c)){
ck.addClass("tree-checkbox1");
}else{
if(_1f(_1c)){
ck.addClass("tree-checkbox0");
}else{
ck.addClass("tree-checkbox2");
}
}
_18($(_1d.target));
}
function _1e(n){
var ck=n.find(".tree-checkbox");
if(ck.hasClass("tree-checkbox0")||ck.hasClass("tree-checkbox2")){
return false;
}
var b=true;
n.parent().siblings().each(function(){
if(!$(this).find(">div.tree-node .tree-checkbox").hasClass("tree-checkbox1")){
b=false;
}
});
return b;
};
function _1f(n){
var ck=n.find(".tree-checkbox");
if(ck.hasClass("tree-checkbox1")||ck.hasClass("tree-checkbox2")){
return false;
}
var b=true;
n.parent().siblings().each(function(){
if(!$(this).find(">div.tree-node .tree-checkbox").hasClass("tree-checkbox0")){
b=false;
}
});
return b;
};
};
};
function _20(_21,ul,_22,_23){
var _24=$.data(_21,"tree").options;
if(!_23){
$(ul).empty();
}
var _25=$(ul).prev().find(">span.tree-indent,>span.tree-hit").length;
_26(ul,_22,_25);
_c(_21);
var _27=null;
if(_21!=ul){
var _28=$(ul).prev();
_27=$.extend({},$.data(_28[0],"tree-node"),{target:_28[0],checked:_28.find(".tree-checkbox").hasClass("tree-checkbox1")});
}
_24.onLoadSuccess.call(_21,_27,_22);
function _26(ul,_29,_2a){
for(var i=0;i<_29.length;i++){
var li=$("<li></li>").appendTo(ul);
var _2b=_29[i];
if(_2b.state!="open"&&_2b.state!="closed"){
_2b.state="open";
}
var _2c=$("<div class=\"tree-node\"></div>").appendTo(li);
_2c.attr("node-id",_2b.id);
$.data(_2c[0],"tree-node",{id:_2b.id,text:_2b.text,iconCls:_2b.iconCls,attributes:_2b.attributes});
$("<span class=\"tree-title\"></span>").html(_2b.text).appendTo(_2c);
if(_24.checkbox){
if(_2b.checked){
$("<span class=\"tree-checkbox tree-checkbox1\"></span>").prependTo(_2c);
}else{
$("<span class=\"tree-checkbox tree-checkbox0\"></span>").prependTo(_2c);
}
}
if(_2b.children&&_2b.children.length){
var _2d=$("<ul></ul>").appendTo(li);
if(_2b.state=="open"){
$("<span class=\"tree-icon tree-folder tree-folder-open\"></span>").addClass(_2b.iconCls).prependTo(_2c);
$("<span class=\"tree-hit tree-expanded\"></span>").prependTo(_2c);
}else{
$("<span class=\"tree-icon tree-folder\"></span>").addClass(_2b.iconCls).prependTo(_2c);
$("<span class=\"tree-hit tree-collapsed\"></span>").prependTo(_2c);
_2d.css("display","none");
}
_26(_2d,_2b.children,_2a+1);
}else{
if(_2b.state=="closed"){
$("<span class=\"tree-folder\"></span>").addClass(_2b.iconCls).prependTo(_2c);
$("<span class=\"tree-hit tree-collapsed\"></span>").prependTo(_2c);
}else{
$("<span class=\"tree-icon tree-file\"></span>").addClass(_2b.iconCls).prependTo(_2c);
$("<span class=\"tree-indent\"></span>").prependTo(_2c);
}
}
for(var j=0;j<_2a;j++){
$("<span class=\"tree-indent\"></span>").prependTo(_2c);
}
}
};
};
function _2e(_2f,ul,_30,_31){
var _32=$.data(_2f,"tree").options;
_30=_30||{};
var _33=null;
if(_2f!=ul){
var _34=$(ul).prev();
_33=$.extend({},$.data(_34[0],"tree-node"),{target:_34[0],checked:_34.find(".tree-checkbox").hasClass("tree-checkbox1")});
}
if(_32.onBeforeLoad.call(_2f,_33,_30)==false){
return;
}
if(!_32.url){
return;
}
var _35=$(ul).prev().find(">span.tree-folder");
_35.addClass("tree-loading");
$.ajax({type:"post",url:_32.url,data:_30,dataType:"json",success:function(_36){
_35.removeClass("tree-loading");
_20(_2f,ul,_36);
if(_31){
_31();
}
},error:function(){
_35.removeClass("tree-loading");
_32.onLoadError.apply(_2f,arguments);
if(_31){
_31();
}
}});
};
function _37(_38,_39){
var _3a=$.data(_38,"tree").options;
var _3b=$(_39);
var hit=_3b.find(">span.tree-hit");
if(hit.length==0){
return;
}
if(hit.hasClass("tree-expanded")){
return;
}
var _3c=$.extend({},$.data(_39,"tree-node"),{target:_39,checked:_3b.find(".tree-checkbox").hasClass("tree-checkbox1")});
if(_3a.onBeforeExpand.call(_38,_3c)==false){
return;
}
hit.removeClass("tree-collapsed tree-collapsed-hover").addClass("tree-expanded");
hit.next().addClass("tree-folder-open");
var ul=_3b.next();
if(ul.length){
if(_3a.animate){
ul.slideDown("normal",function(){
_3a.onExpand.call(_38,_3c);
});
}else{
ul.css("display","block");
_3a.onExpand.call(_38,_3c);
}
}else{
var _3d=$("<ul style=\"display:none\"></ul>").insertAfter(_3b);
_2e(_38,_3d[0],{id:_3c.id},function(){
if(_3a.animate){
_3d.slideDown("normal",function(){
_3a.onExpand.call(_38,_3c);
});
}else{
_3d.css("display","block");
_3a.onExpand.call(_38,_3c);
}
});
}
};
function _3e(_3f,_40){
var _41=$.data(_3f,"tree").options;
var _42=$(_40);
var hit=_42.find(">span.tree-hit");
if(hit.length==0){
return;
}
if(hit.hasClass("tree-collapsed")){
return;
}
var _43=$.extend({},$.data(_40,"tree-node"),{target:_40,checked:_42.find(".tree-checkbox").hasClass("tree-checkbox1")});
if(_41.onBeforeCollapse.call(_3f,_43)==false){
return;
}
hit.removeClass("tree-expanded tree-expanded-hover").addClass("tree-collapsed");
hit.next().removeClass("tree-folder-open");
if(_41.animate){
_42.next().slideUp("normal",function(){
_41.onCollapse.call(_3f,_43);
});
}else{
_42.next().css("display","none");
_41.onCollapse.call(_3f,_43);
}
};
function _44(_45,_46){
var hit=$(_46).find(">span.tree-hit");
if(hit.length==0){
return;
}
if(hit.hasClass("tree-expanded")){
_3e(_45,_46);
}else{
_37(_45,_46);
}
};
function _47(_48){
var _49=_4a(_48);
for(var i=0;i<_49.length;i++){
_37(_48,_49[i].target);
var _4b=_4c(_48,_49[i].target);
for(var j=0;j<_4b.length;j++){
_37(_48,_4b[j].target);
}
}
};
function _4d(_4e,_4f){
var _50=[];
var p=_51(_4e,_4f);
while(p){
_50.unshift(p);
p=_51(_4e,p.target);
}
for(var i=0;i<_50.length;i++){
_37(_4e,_50[i].target);
}
};
function _52(_53){
var _54=_4a(_53);
for(var i=0;i<_54.length;i++){
_3e(_53,_54[i].target);
var _55=_4c(_53,_54[i].target);
for(var j=0;j<_55.length;j++){
_3e(_53,_55[j].target);
}
}
};
function _56(_57){
var _58=_4a(_57);
if(_58.length){
return _58[0];
}else{
return null;
}
};
function _4a(_59){
var _5a=[];
$(_59).find(">li").each(function(){
var _5b=$(this).find(">div.tree-node");
_5a.push($.extend({},$.data(_5b[0],"tree-node"),{target:_5b[0],checked:_5b.find(".tree-checkbox").hasClass("tree-checkbox1")}));
});
return _5a;
};
function _4c(_5c,_5d){
var _5e=[];
if(_5d){
_5f($(_5d));
}else{
var _60=_4a(_5c);
for(var i=0;i<_60.length;i++){
_5e.push(_60[i]);
_5f($(_60[i].target));
}
}
function _5f(_61){
_61.next().find("div.tree-node").each(function(){
_5e.push($.extend({},$.data(this,"tree-node"),{target:this,checked:$(this).find(".tree-checkbox").hasClass("tree-checkbox1")}));
});
};
return _5e;
};
function _51(_62,_63){
var _64=$(_63).parent().parent().prev();
if(_64.length){
return $.extend({},$.data(_64[0],"tree-node"),{target:_64[0],checked:_64.find(".tree-checkbox").hasClass("tree-checkbox1")});
}else{
return null;
}
};
function _65(_66){
var _67=[];
$(_66).find(".tree-checkbox1").each(function(){
var _68=$(this).parent();
_67.push($.extend({},$.data(_68[0],"tree-node"),{target:_68[0],checked:_68.find(".tree-checkbox").hasClass("tree-checkbox1")}));
});
return _67;
};
function _69(_6a){
var _6b=$(_6a).find("div.tree-node-selected");
if(_6b.length){
return $.extend({},$.data(_6b[0],"tree-node"),{target:_6b[0],checked:_6b.find(".tree-checkbox").hasClass("tree-checkbox1")});
}else{
return null;
}
};
function _6c(_6d,_6e){
var _6f=$(_6e.parent);
var ul;
if(_6f.length==0){
ul=$(_6d);
}else{
ul=_6f.next();
if(ul.length==0){
ul=$("<ul></ul>").insertAfter(_6f);
}
}
if(_6e.data&&_6e.data.length){
var _70=_6f.find("span.tree-icon");
if(_70.hasClass("tree-file")){
_70.removeClass("tree-file").addClass("tree-folder");
var hit=$("<span class=\"tree-hit tree-expanded\"></span>").insertBefore(_70);
if(hit.prev().length){
hit.prev().remove();
}
}
}
_20(_6d,ul[0],_6e.data,true);
};
function _71(_72,_73){
var _74=$(_73);
var li=_74.parent();
var ul=li.parent();
li.remove();
if(ul.find(">li").length==0){
var _74=ul.prev();
_74.find(".tree-icon").removeClass("tree-folder").addClass("tree-file");
_74.find(".tree-hit").remove();
$("<span class=\"tree-indent\"></span>").prependTo(_74);
if(ul[0]!=_72){
ul.remove();
}
}
};
function _75(_76,_77){
function _78(aa,ul){
ul.find(">li").each(function(){
var _79=$(this).find(">div.tree-node");
var _7a=$.extend({},$.data(_79[0],"tree-node"),{target:_79[0],checked:_79.find(".tree-checkbox").hasClass("tree-checkbox1")});
if(!_7b(_76,_79[0])){
_7a.state=_79.find(".tree-hit").hasClass("tree-expanded")?"open":"closed";
}
var sub=$(this).find(">ul");
if(sub.length){
_7a.children=[];
_78(_7a.children,sub);
}
aa.push(_7a);
});
};
var _7c=$(_77);
var _7d=$.extend({},$.data(_77,"tree-node"),{target:_77,checked:_7c.find(".tree-checkbox").hasClass("tree-checkbox1"),children:[]});
_78(_7d.children,_7c.next());
_71(_76,_77);
return _7d;
};
function _7e(_7f,_80){
var _81=$(_80.target);
var _82=$.data(_80.target,"tree-node");
if(_82.iconCls){
_81.find(".tree-icon").removeClass(_82.iconCls);
}
$.extend(_82,_80);
$.data(_80.target,"tree-node",_82);
_81.attr("node-id",_82.id);
_81.find(".tree-title").html(_82.text);
if(_82.iconCls){
_81.find(".tree-icon").addClass(_82.iconCls);
}
var ck=_81.find(".tree-checkbox");
ck.removeClass("tree-checkbox0 tree-checkbox1 tree-checkbox2");
if(_82.checked){
ck.addClass("tree-checkbox1");
}else{
ck.addClass("tree-checkbox0");
}
};
function _83(_84,id){
var _85=$(_84).find("div.tree-node[node-id="+id+"]");
if(_85.length){
return $.extend({},$.data(_85[0],"tree-node"),{target:_85[0],checked:_85.find(".tree-checkbox").hasClass("tree-checkbox1")});
}else{
return null;
}
};
function _86(_87,_88){
$("div.tree-node-selected",_87).removeClass("tree-node-selected");
$(_88).addClass("tree-node-selected");
};
function _7b(_89,_8a){
var _8b=$(_8a);
var hit=$(">span.tree-hit",_8b);
return hit.length==0;
};
$.fn.tree=function(_8c,_8d){
if(typeof _8c=="string"){
switch(_8c){
case "options":
return $.data(this[0],"tree").options;
case "loadData":
return this.each(function(){
_20(this,this,_8d);
});
case "reload":
return this.each(function(){
$(this).empty();
_2e(this,this);
});
case "getRoot":
return _56(this[0]);
case "getRoots":
return _4a(this[0]);
case "getParent":
return _51(this[0],_8d);
case "getChildren":
return _4c(this[0],_8d);
case "getChecked":
return _65(this[0]);
case "getSelected":
return _69(this[0]);
case "isLeaf":
return _7b(this[0],_8d);
case "find":
return _83(this[0],_8d);
case "select":
return this.each(function(){
_86(this,_8d);
});
case "check":
return this.each(function(){
_12(this,_8d,true);
});
case "uncheck":
return this.each(function(){
_12(this,_8d,false);
});
case "collapse":
return this.each(function(){
_3e(this,_8d);
});
case "expand":
return this.each(function(){
_37(this,_8d);
});
case "collapseAll":
return this.each(function(){
_52(this);
});
case "expandAll":
return this.each(function(){
_47(this);
});
case "expandTo":
return this.each(function(){
_4d(this,_8d);
});
case "toggle":
return this.each(function(){
_44(this,_8d);
});
case "append":
return this.each(function(){
_6c(this,_8d);
});
case "remove":
return this.each(function(){
_71(this,_8d);
});
case "pop":
return _75(this[0],_8d);
case "update":
return this.each(function(){
_7e(this,_8d);
});
}
}
var _8c=_8c||{};
return this.each(function(){
var _8e=$.data(this,"tree");
var _8f;
if(_8e){
_8f=$.extend(_8e.options,_8c);
_8e.options=_8f;
}else{
_8f=$.extend({},$.fn.tree.defaults,{url:$(this).attr("url"),checkbox:($(this).attr("checkbox")?$(this).attr("checkbox")=="true":undefined),animate:($(this).attr("animate")?$(this).attr("animate")=="true":undefined)},_8c);
$.data(this,"tree",{options:_8f,tree:_1(this)});
var _90=_4(this);
_20(this,this,_90);
}
if(_8f.data){
_20(this,this,_8f.data);
}
if(_8f.url){
_2e(this,this);
}
});
};
$.fn.tree.defaults={url:null,animate:false,checkbox:false,data:null,onBeforeLoad:function(_91,_92){
},onLoadSuccess:function(_93,_94){
},onLoadError:function(){
},onClick:function(_95){
},onDblClick:function(_96){
},onBeforeExpand:function(_97){
},onExpand:function(_98){
},onBeforeCollapse:function(_99){
},onCollapse:function(_9a){
}};
})(jQuery);

