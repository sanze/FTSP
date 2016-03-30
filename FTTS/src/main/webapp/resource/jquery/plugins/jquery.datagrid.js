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
$.extend(Array.prototype,{indexOf:function(o){
for(var i=0,_1=this.length;i<_1;i++){
if(this[i]==o){
return i;
}
}
return -1;
},remove:function(o){
var _2=this.indexOf(o);
if(_2!=-1){
this.splice(_2,1);
}
return this;
}});
function _3(_4,_5){
var _6=$.data(_4,"datagrid").options;
var _7=$.data(_4,"datagrid").panel;
if(_5){
if(_5.width){
_6.width=_5.width;
}
if(_5.height){
_6.height=_5.height;
}
}
if(_6.fit==true){
var p=_7.panel("panel").parent();
_6.width=p.width();
_6.height=p.height();
}
_7.panel("resize",{width:_6.width,height:_6.height});
setTimeout(function(){
_8();
},0);
function _8(){
var _9=$.data(_4,"datagrid").panel;
var _a=_9.width();
var _b=_9.height();
var _c=_9.find("div.datagrid-view");
var _d=_c.find("div.datagrid-view1");
var _e=_c.find("div.datagrid-view2");
_c.width(_a);
_d.width(_d.find("table").width());
_e.width(_a-_d.outerWidth());
_d.find(">div.datagrid-header,>div.datagrid-body").width(_d.width());
_e.find(">div.datagrid-header,>div.datagrid-body").width(_e.width());
var hh;
var _f=_d.find(">div.datagrid-header");
var _10=_e.find(">div.datagrid-header");
var _11=_f.find("table");
var _12=_10.find("table");
_f.css("height",null);
_10.css("height",null);
_11.css("height",null);
_12.css("height",null);
hh=Math.max(_11.height(),_12.height());
_11.height(hh);
_12.height(hh);
if($.boxModel==true){
_f.height(hh-(_f.outerHeight()-_f.height()));
_10.height(hh-(_10.outerHeight()-_10.height()));
}else{
_f.height(hh);
_10.height(hh);
}
var _13=_c.find("div.datagrid-body");
if(_6.height=="auto"){
_13.height(_e.find("div.datagrid-body table").height()+18);
}else{
_13.height(_b-$(">div.datagrid-header",_e).outerHeight(true)-$(">div.datagrid-toolbar",_9).outerHeight(true)-$(">div.datagrid-pager",_9).outerHeight(true));
}
_c.height(_e.height());
_e.css("left",_d.outerWidth());
};
};
function _14(_15,_16){
var _17=$.data(_15,"datagrid").data.rows;
var _18=$.data(_15,"datagrid").options;
var _19=$.data(_15,"datagrid").panel;
var _1a=_19.find(">div.datagrid-view");
var _1b=_1a.find(">div.datagrid-view1");
var _1c=_1a.find(">div.datagrid-view2");
if(_18.rownumbers||(_18.frozenColumns&&_18.frozenColumns.length>0)){
if(_16>=0){
_1d(_16);
}else{
for(var i=0;i<_17.length;i++){
_1d(i);
}
}
}
if(_18.height=="auto"){
var _1e=_1c.find("div.datagrid-body table").height()+18;
_1b.find("div.datagrid-body").height(_1e);
_1c.find("div.datagrid-body").height(_1e);
_1a.height(_1c.height());
}
function _1d(_1f){
var tr1=_1b.find("tr[datagrid-row-index="+_1f+"]");
var tr2=_1c.find("tr[datagrid-row-index="+_1f+"]");
tr1.css("height",null);
tr2.css("height",null);
var _20=Math.max(tr1.height(),tr2.height());
tr1.css("height",_20);
tr2.css("height",_20);
};
};
function _21(_22,_23){
function _24(_25){
var _26=[];
$("tr",_25).each(function(){
var _27=[];
$("th",this).each(function(){
var th=$(this);
var col={title:th.html(),align:th.attr("align")||"left",sortable:th.attr("sortable")=="true"||false,checkbox:th.attr("checkbox")=="true"||false};
if(th.attr("field")){
col.field=th.attr("field");
}
if(th.attr("formatter")){
col.formatter=eval(th.attr("formatter"));
}
if(th.attr("editor")){
col.editor=th.attr("editor");
}
if(th.attr("rowspan")){
col.rowspan=parseInt(th.attr("rowspan"));
}
if(th.attr("colspan")){
col.colspan=parseInt(th.attr("colspan"));
}
if(th.attr("width")){
col.width=parseInt(th.attr("width"));
}
_27.push(col);
});
_26.push(_27);
});
return _26;
};
var _28=$("<div class=\"datagrid-wrap\">"+"<div class=\"datagrid-view\">"+"<div class=\"datagrid-view1\">"+"<div class=\"datagrid-header\">"+"<div class=\"datagrid-header-inner\"></div>"+"</div>"+"<div class=\"datagrid-body\">"+"<div class=\"datagrid-body-inner\"></div>"+"</div>"+"</div>"+"<div class=\"datagrid-view2\">"+"<div class=\"datagrid-header\">"+"<div class=\"datagrid-header-inner\"></div>"+"</div>"+"<div class=\"datagrid-body\"></div>"+"</div>"+"<div class=\"datagrid-resize-proxy\"></div>"+"</div>"+"</div>").insertAfter(_22);
_28.panel({doSize:false});
_28.panel("panel").addClass("datagrid").bind("_resize",function(){
var _29=$.data(_22,"datagrid").options;
if(_29.fit==true){
_3(_22);
setTimeout(function(){
_4f(_22);
},0);
}
return false;
});
$(_22).hide().appendTo($(">div.datagrid-view",_28));
var _2a=_24($("thead[frozen=true]",_22));
var _2b=_24($("thead[frozen!=true]",_22));
var _2c={total:0,rows:[]};
var _2d=_6c(_2b);
$(_22).find("tbody tr").each(function(){
_2c.total++;
var col={};
for(var i=0;i<_2d.length;i++){
col[_2d[i]]=$("td:eq("+i+")",this).html();
}
_2c.rows.push(col);
});
return {panel:_28,frozenColumns:_2a,columns:_2b,data:_2c};
};
function _2e(_2f){
var _30=$.data(_2f,"datagrid").options;
var _31=$.data(_2f,"datagrid").panel;
_31.panel({title:_30.title,iconCls:_30.iconCls,border:_30.border});
if(_30.frozenColumns){
var t=_32(_30.frozenColumns);
if(_30.rownumbers){
var td=$("<td rowspan=\""+_30.frozenColumns.length+"\"><div class=\"datagrid-header-rownumber\"></div></td>");
if($("tr",t).length==0){
td.wrap("<tr></tr>").parent().appendTo($("tbody",t));
}else{
td.prependTo($("tr:first",t));
}
}
$("div.datagrid-view1 div.datagrid-header-inner",_31).html(t);
}
if(_30.columns){
var t=_32(_30.columns);
$("div.datagrid-view2 div.datagrid-header-inner",_31).html(t);
}
$("div.datagrid-toolbar",_31).remove();
if(_30.toolbar){
var tb=$("<div class=\"datagrid-toolbar\"></div>").prependTo(_31);
for(var i=0;i<_30.toolbar.length;i++){
var btn=_30.toolbar[i];
if(btn=="-"){
$("<div class=\"datagrid-btn-separator\"></div>").appendTo(tb);
}else{
var _33=$("<a href=\"javascript:void(0)\"></a>");
_33[0].onclick=eval(btn.handler||function(){
});
_33.css("float","left").appendTo(tb).linkbutton($.extend({},btn,{plain:true}));
}
}
}
$("div.datagrid-pager",_31).remove();
if(_30.pagination){
var _34=$("<div class=\"datagrid-pager\"></div>").appendTo(_31);
_34.pagination({pageNumber:_30.pageNumber,pageSize:_30.pageSize,pageList:_30.pageList,onSelectPage:function(_35,_36){
_30.pageNumber=_35;
_30.pageSize=_36;
_37(_2f);
}});
_30.pageSize=_34.pagination("options").pageSize;
}
};
function _32(_38){
var t=$("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tbody></tbody></table>");
for(var i=0;i<_38.length;i++){
var tr=$("<tr></tr>").appendTo($("tbody",t));
var _39=_38[i];
for(var j=0;j<_39.length;j++){
var col=_39[j];
var _3a="";
if(col.rowspan){
_3a+="rowspan=\""+col.rowspan+"\" ";
}
if(col.colspan){
_3a+="colspan=\""+col.colspan+"\" ";
}
var td=$("<td "+_3a+"></td>").appendTo(tr);
if(col.checkbox){
td.attr("field",col.field);
$("<div class=\"datagrid-header-check\"></div>").html("<input type=\"checkbox\"/>").appendTo(td);
}else{
if(col.field){
td.attr("field",col.field);
td.append("<div class=\"datagrid-cell\"><span></span><span class=\"datagrid-sort-icon\"></span></div>");
$("span",td).html(col.title);
$("span.datagrid-sort-icon",td).html("&nbsp;");
$("div.datagrid-cell",td).width(col.width);
$("div.datagrid-cell",td).css("text-align",(col.align||"left"));
}else{
$("<div class=\"datagrid-cell-group\"></div>").html(col.title).appendTo(td);
}
}
}
}
return t;
};
function _3b(_3c){
var _3d=$.data(_3c,"datagrid").panel;
var _3e=$.data(_3c,"datagrid").options;
var _3f=$.data(_3c,"datagrid").data;
var _40=_3d.find("div.datagrid-body");
if(_3e.striped){
_40.find("tr:odd").addClass("datagrid-row-alt");
}
_40.find("tr").unbind(".datagrid").bind("mouseenter.datagrid",function(){
var _41=$(this).attr("datagrid-row-index");
_40.find("tr[datagrid-row-index="+_41+"]").addClass("datagrid-row-over");
}).bind("mouseleave.datagrid",function(){
var _42=$(this).attr("datagrid-row-index");
_40.find("tr[datagrid-row-index="+_42+"]").removeClass("datagrid-row-over");
}).bind("click.datagrid",function(){
var _43=$(this).attr("datagrid-row-index");
if(_3e.singleSelect==true){
_9e(_3c);
_ab(_3c,_43);
}else{
if($(this).hasClass("datagrid-row-selected")){
_b8(_3c,_43);
}else{
_ab(_3c,_43);
}
}
if(_3e.onClickRow){
_3e.onClickRow.call(_3c,_43,_3f.rows[_43]);
}
}).bind("dblclick.datagrid",function(){
var _44=$(this).attr("datagrid-row-index");
if(_3e.onDblClickRow){
_3e.onDblClickRow.call(_3c,_44,_3f.rows[_44]);
}
});
_40.find("div.datagrid-cell-check input[type=checkbox]").unbind(".datagrid").bind("click.datagrid",function(e){
var _45=$(this).parent().parent().parent().attr("datagrid-row-index");
if(_3e.singleSelect){
_9e(_3c);
_ab(_3c,_45);
}else{
if($(this).attr("checked")){
_ab(_3c,_45);
}else{
_b8(_3c,_45);
}
}
e.stopPropagation();
});
var _46=_3d.find("div.datagrid-header");
_46.find("td:has(div.datagrid-cell)").unbind(".datagrid").bind("mouseenter.datagrid",function(){
$(this).addClass("datagrid-header-over");
}).bind("mouseleave.datagrid",function(){
$(this).removeClass("datagrid-header-over");
});
_46.find("div.datagrid-cell").unbind(".datagrid").bind("click.datagrid",function(){
var _47=$(this).parent().attr("field");
var opt=_5b(_3c,_47);
if(!opt.sortable){
return;
}
_3e.sortName=_47;
_3e.sortOrder="asc";
var c="datagrid-sort-asc";
if($(this).hasClass("datagrid-sort-asc")){
c="datagrid-sort-desc";
_3e.sortOrder="desc";
}
_46.find("div.datagrid-cell").removeClass("datagrid-sort-asc datagrid-sort-desc");
$(this).addClass(c);
if(_3e.onSortColumn){
_3e.onSortColumn.call(_3c,_3e.sortName,_3e.sortOrder);
}
if(_3e.remoteSort){
_37(_3c);
}else{
_79(_3c,_3f);
}
});
_46.find("input[type=checkbox]").unbind(".datagrid").bind("click.datagrid",function(){
if(_3e.singleSelect){
return false;
}
if($(this).attr("checked")){
_a2(_3c);
}else{
_9e(_3c);
}
});
var _48=_3d.find(">div.datagrid-view");
var _49=_48.find(">div.datagrid-view1");
var _4a=_48.find(">div.datagrid-view2");
var _4b=_4a.find("div.datagrid-header");
var _4c=_49.find("div.datagrid-body");
_4a.find("div.datagrid-body").unbind(".datagrid").bind("scroll.datagrid",function(){
_4b.scrollLeft($(this).scrollLeft());
_4c.scrollTop($(this).scrollTop());
});
_46.find("div.datagrid-cell").resizable({handles:"e",minWidth:50,onStartResize:function(e){
var _4d=_48.find(">div.datagrid-resize-proxy");
_4d.css({left:e.pageX-$(_3d).offset().left-1});
_4d.css("display","block");
},onResize:function(e){
_48.find(">div.datagrid-resize-proxy").css({left:e.pageX-$(_3d).offset().left-1});
return false;
},onStopResize:function(e){
_4f(_3c,this);
var _4e=_3d.find("div.datagrid-view2");
_4e.find("div.datagrid-header").scrollLeft(_4e.find("div.datagrid-body").scrollLeft());
_48.find(">div.datagrid-resize-proxy").css("display","none");
}});
$("div.datagrid-view1 div.datagrid-header div.datagrid-cell",_3d).resizable({onStopResize:function(e){
_4f(_3c,this);
var _50=_3d.find("div.datagrid-view2");
_50.find("div.datagrid-header").scrollLeft(_50.find("div.datagrid-body").scrollLeft());
_48.find(">div.datagrid-resize-proxy").css("display","none");
_3(_3c);
}});
};
function _4f(_51,_52){
var _53=$.data(_51,"datagrid").panel;
var _54=$.data(_51,"datagrid").options;
var _55=_53.find("div.datagrid-body");
if(_52){
fix(_52);
}else{
$("div.datagrid-header div.datagrid-cell",_53).each(function(){
fix(this);
});
}
_5c(_51);
setTimeout(function(){
_14(_51);
_65(_51);
},0);
function fix(_56){
var _57=$(_56);
if(_57.width()==0){
return;
}
var _58=_57.parent().attr("field");
_55.find("td[field="+_58+"]").each(function(){
var td=$(this);
var _59=td.attr("colspan")||1;
if(_59==1){
var _5a=td.find("div.datagrid-cell");
if($.boxModel==true){
_5a.width(_57.outerWidth()-_5a.outerWidth()+_5a.width());
}else{
_5a.width(_57.outerWidth());
}
}
});
var col=_5b(_51,_58);
col.width=$.boxModel==true?_57.width():_57.outerWidth();
};
};
function _5c(_5d){
var _5e=$.data(_5d,"datagrid").panel;
var _5f=_5e.find("div.datagrid-header");
_5e.find("div.datagrid-body td.datagrid-td-merged").each(function(){
var td=$(this);
var _60=td.attr("colspan")||1;
var _61=td.attr("field");
var _62=_5f.find("td[field="+_61+"]");
var _63=_62.width();
for(var i=1;i<_60;i++){
_62=_62.next();
_63+=_62.outerWidth();
}
var _64=td.find(">div.datagrid-cell");
if($.boxModel==true){
_64.width(_63-(_64.outerWidth()-_64.width()));
}else{
_64.width(_63);
}
});
};
function _65(_66){
var _67=$.data(_66,"datagrid").panel;
_67.find("div.datagrid-editable").each(function(){
var ed=$.data(this,"datagrid.editor");
if(ed.editor.resize){
ed.editor.resize(ed.elem,$(this).width());
}
});
};
function _5b(_68,_69){
var _6a=$.data(_68,"datagrid").options;
if(_6a.columns){
for(var i=0;i<_6a.columns.length;i++){
var _6b=_6a.columns[i];
for(var j=0;j<_6b.length;j++){
var col=_6b[j];
if(col.field==_69){
return col;
}
}
}
}
if(_6a.frozenColumns){
for(var i=0;i<_6a.frozenColumns.length;i++){
var _6b=_6a.frozenColumns[i];
for(var j=0;j<_6b.length;j++){
var col=_6b[j];
if(col.field==_69){
return col;
}
}
}
}
return null;
};
function _6c(_6d){
if(_6d.length==0){
return [];
}
function _6e(_6f,_70,_71){
var _72=[];
while(_72.length<_71){
var col=_6d[_6f][_70];
if(col.colspan&&parseInt(col.colspan)>1){
var ff=_6e(_6f+1,_73(_6f,_70),parseInt(col.colspan));
_72=_72.concat(ff);
}else{
if(col.field){
_72.push(col.field);
}
}
_70++;
}
return _72;
};
function _73(_74,_75){
var _76=0;
for(var i=0;i<_75;i++){
var _77=parseInt(_6d[_74][i].colspan||"1");
if(_77>1){
_76+=_77;
}
}
return _76;
};
var _78=[];
for(var i=0;i<_6d[0].length;i++){
var col=_6d[0][i];
if(col.colspan&&parseInt(col.colspan)>1){
var ff=_6e(1,_73(0,i),parseInt(col.colspan));
_78=_78.concat(ff);
}else{
if(col.field){
_78.push(col.field);
}
}
}
return _78;
};
function _79(_7a,_7b){
var _7c=$.data(_7a,"datagrid").options;
var _7d=$.data(_7a,"datagrid").panel;
var _7e=$.data(_7a,"datagrid").selectedRows;
var _7f=_7b.rows;
$.data(_7a,"datagrid").data=_7b;
if(!_7c.remoteSort){
var opt=_5b(_7a,_7c.sortName);
if(opt){
var _80=opt.sorter||function(a,b,_81){
return (a>b?1:-1)*(_81=="asc"?1:-1);
};
_7b.rows.sort(function(r1,r2){
return _80(r1[_7c.sortName],r2[_7c.sortName],_7c.sortOrder);
});
}
}
var _82=_7d.find(">div.datagrid-view");
var _83=_82.find(">div.datagrid-view1");
var _84=_82.find(">div.datagrid-view2");
var _85=_6c(_7c.columns);
_84.find(">div.datagrid-body").html(_86(_85));
if(_7c.rownumbers||(_7c.frozenColumns&&_7c.frozenColumns.length>0)){
var _87=_6c(_7c.frozenColumns);
_83.find(">div.datagrid-body>div.datagrid-body-inner").html(_86(_87,_7c.rownumbers));
}
_7c.onLoadSuccess.call(_7a,_7b);
_84.find(">div.datagrid-body").scrollLeft(0).scrollTop(0);
var _88=$(">div.datagrid-pager",_7d);
if(_88.length){
if(_88.pagination("options").total!=_7b.total){
_88.pagination({total:_7b.total});
}
}
_14(_7a);
_3b(_7a);
function _86(_89,_8a){
function _8b(row){
if(!_7c.idField){
return false;
}
for(var i=0;i<_7e.length;i++){
if(_7e[i][_7c.idField]==row[_7c.idField]){
return true;
}
}
return false;
};
var _8c=["<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody>"];
for(var i=0;i<_7f.length;i++){
var row=_7f[i];
var _8d=_8b(row);
if(i%2&&_7c.striped){
_8c.push("<tr datagrid-row-index=\""+i+"\" class=\"datagrid-row-alt");
}else{
_8c.push("<tr datagrid-row-index=\""+i+"\" class=\"");
}
if(_8d==true){
_8c.push(" datagrid-row-selected");
}
_8c.push("\">");
if(_8a){
var _8e=i+1;
if(_7c.pagination){
_8e+=(_7c.pageNumber-1)*_7c.pageSize;
}
_8c.push("<td class=\"datagrid-td-rownumber\"><div class=\"datagrid-cell-rownumber\">"+_8e+"</div></td>");
}
for(var j=0;j<_89.length;j++){
var _8f=_89[j];
var col=_5b(_7a,_8f);
if(col){
var _90="width:"+(col.width)+"px;";
_90+="text-align:"+(col.align||"left")+";";
_90+=_7c.nowrap==false?"white-space:normal;":"";
_8c.push("<td field=\""+_8f+"\">");
_8c.push("<div style=\""+_90+"\" ");
if(col.checkbox){
_8c.push("class=\"datagrid-cell-check ");
}else{
_8c.push("class=\"datagrid-cell ");
}
_8c.push("\">");
if(col.checkbox){
if(_8d){
_8c.push("<input type=\"checkbox\" checked=\"checked\"/>");
}else{
_8c.push("<input type=\"checkbox\"/>");
}
}else{
if(col.formatter){
_8c.push(col.formatter(row[_8f],row,i));
}else{
_8c.push(row[_8f]);
}
}
_8c.push("</div>");
_8c.push("</td>");
}
}
_8c.push("</tr>");
}
_8c.push("</tbody></table>");
return _8c.join("");
};
};
function _91(_92,row){
var _93=$.data(_92,"datagrid").options;
var _94=$.data(_92,"datagrid").data.rows;
if(typeof row=="object"){
return _94.indexOf(row);
}else{
for(var i=0;i<_94.length;i++){
if(_94[i][_93.idField]==row){
return i;
}
}
return -1;
}
};
function _95(_96){
var _97=$.data(_96,"datagrid").options;
var _98=$.data(_96,"datagrid").panel;
var _99=$.data(_96,"datagrid").data;
if(_97.idField){
var _9a=$.data(_96,"datagrid").deletedRows;
var _9b=$.data(_96,"datagrid").selectedRows;
var _9c=[];
for(var i=0;i<_9b.length;i++){
(function(){
var row=_9b[i];
for(var j=0;j<_9a.length;j++){
if(row[_97.idField]==_9a[j][_97.idField]){
return;
}
}
_9c.push(row);
})();
}
return _9c;
}
var _9c=[];
$("div.datagrid-view2 div.datagrid-body tr.datagrid-row-selected",_98).each(function(){
var _9d=parseInt($(this).attr("datagrid-row-index"));
if(_99.rows[_9d]){
_9c.push(_99.rows[_9d]);
}
});
return _9c;
};
function _9e(_9f){
var _a0=$.data(_9f,"datagrid").panel;
$("div.datagrid-body tr.datagrid-row-selected",_a0).removeClass("datagrid-row-selected");
$("div.datagrid-body div.datagrid-cell-check input[type=checkbox]",_a0).attr("checked",false);
var _a1=$.data(_9f,"datagrid").selectedRows;
while(_a1.length>0){
_a1.pop();
}
};
function _a2(_a3){
var _a4=$.data(_a3,"datagrid").options;
var _a5=$.data(_a3,"datagrid").panel;
var _a6=$.data(_a3,"datagrid").data;
var _a7=$.data(_a3,"datagrid").selectedRows;
var _a8=_a6.rows;
var _a9=_a5.find("div.datagrid-body");
$("tr",_a9).addClass("datagrid-row-selected");
$("div.datagrid-cell-check input[type=checkbox]",_a9).attr("checked",true);
for(var _aa=0;_aa<_a8.length;_aa++){
if(_a4.idField){
(function(){
var row=_a8[_aa];
for(var i=0;i<_a7.length;i++){
if(_a7[i][_a4.idField]==row[_a4.idField]){
return;
}
}
_a7.push(row);
})();
}
_a4.onSelect.call(_a3,_aa,_a8[_aa]);
}
};
function _ab(_ac,_ad){
var _ae=$.data(_ac,"datagrid").panel;
var _af=$.data(_ac,"datagrid").options;
var _b0=$.data(_ac,"datagrid").data;
var _b1=$.data(_ac,"datagrid").selectedRows;
if(_ad<0||_ad>=_b0.rows.length){
return;
}
var tr=$("div.datagrid-body tr[datagrid-row-index="+_ad+"]",_ae);
var ck=$("div.datagrid-cell-check input[type=checkbox]",tr);
tr.addClass("datagrid-row-selected");
ck.attr("checked",true);
if(_af.idField){
var row=_b0.rows[_ad];
for(var i=0;i<_b1.length;i++){
if(_b1[i][_af.idField]==row[_af.idField]){
return;
}
}
_b1.push(row);
}
_af.onSelect.call(_ac,_ad,_b0.rows[_ad]);
};
function _b2(_b3,_b4){
var _b5=$.data(_b3,"datagrid").options;
var _b6=$.data(_b3,"datagrid").data;
if(_b5.idField){
var _b7=-1;
for(var i=0;i<_b6.rows.length;i++){
if(_b6.rows[i][_b5.idField]==_b4){
_b7=i;
break;
}
}
if(_b7>=0){
_ab(_b3,_b7);
}
}
};
function _b8(_b9,_ba){
var _bb=$.data(_b9,"datagrid").options;
var _bc=$.data(_b9,"datagrid").panel;
var _bd=$.data(_b9,"datagrid").data;
var _be=$.data(_b9,"datagrid").selectedRows;
if(_ba<0||_ba>=_bd.rows.length){
return;
}
var _bf=_bc.find("div.datagrid-body");
var tr=$("tr[datagrid-row-index="+_ba+"]",_bf);
var ck=$("tr[datagrid-row-index="+_ba+"] div.datagrid-cell-check input[type=checkbox]",_bf);
tr.removeClass("datagrid-row-selected");
ck.attr("checked",false);
var row=_bd.rows[_ba];
if(_bb.idField){
for(var i=0;i<_be.length;i++){
var _c0=_be[i];
if(_c0[_bb.idField]==row[_bb.idField]){
for(var j=i+1;j<_be.length;j++){
_be[j-1]=_be[j];
}
_be.pop();
break;
}
}
}
_bb.onUnselect.call(_b9,_ba,row);
};
function _c1(_c2,_c3){
var _c4=$.data(_c2,"datagrid").options;
var _c5=$.data(_c2,"datagrid").panel;
var _c6=$.data(_c2,"datagrid").data;
var _c7=$.data(_c2,"datagrid").editingRows;
var tr=$("div.datagrid-body tr[datagrid-row-index="+_c3+"]",_c5);
if(tr.hasClass("datagrid-row-editing")){
return;
}
if(_c4.onBeforeEdit.call(_c2,_c3,_c6.rows[_c3])==false){
return;
}
tr.addClass("datagrid-row-editing");
_c8(_c2,_c3);
_65(_c2);
_c7.push(_c6.rows[_c3]);
_c9(_c2,_c3,_c6.rows[_c3]);
_ca(_c2,_c3);
};
function _cb(_cc,_cd,_ce){
var _cf=$.data(_cc,"datagrid").options;
var _d0=$.data(_cc,"datagrid").panel;
var _d1=$.data(_cc,"datagrid").data;
var _d2=$.data(_cc,"datagrid").updatedRows;
var _d3=$.data(_cc,"datagrid").insertedRows;
var _d4=$.data(_cc,"datagrid").editingRows;
var row=_d1.rows[_cd];
var tr=$("div.datagrid-body tr[datagrid-row-index="+_cd+"]",_d0);
if(!tr.hasClass("datagrid-row-editing")){
return;
}
if(!_ce){
if(!_ca(_cc,_cd)){
return;
}
var _d5=false;
var _d6={};
var nd=_d7(_cc,_cd);
for(var _d8 in nd){
if(row[_d8]!=nd[_d8]){
row[_d8]=nd[_d8];
_d5=true;
_d6[_d8]=nd[_d8];
}
}
if(_d5){
if(_d3.indexOf(row)==-1){
if(_d2.indexOf(row)==-1){
_d2.push(row);
}
}
}
}
tr.removeClass("datagrid-row-editing");
_d4.remove(row);
_d9(_cc,_cd);
_da(_cc,_cd);
if(!_ce){
_cf.onAfterEdit.call(_cc,_cd,row,_d6);
}else{
_cf.onCancelEdit.call(_cc,_cd,row);
}
};
function _c9(_db,_dc,_dd){
var _de=$.data(_db,"datagrid").panel;
var tr=$("div.datagrid-body tr[datagrid-row-index="+_dc+"]",_de);
if(!tr.hasClass("datagrid-row-editing")){
return;
}
tr.find("div.datagrid-editable").each(function(){
var _df=$(this).parent().attr("field");
var ed=$.data(this,"datagrid.editor");
ed.editor.setValue(ed.elem,_dd[_df]);
});
};
function _d7(_e0,_e1){
var _e2=$.data(_e0,"datagrid").panel;
var tr=$("div.datagrid-body tr[datagrid-row-index="+_e1+"]",_e2);
if(!tr.hasClass("datagrid-row-editing")){
return {};
}
var _e3={};
tr.find("div.datagrid-editable").each(function(){
var _e4=$(this).parent().attr("field");
var ed=$.data(this,"datagrid.editor");
_e3[_e4]=ed.editor.getValue(ed.elem);
});
return _e3;
};
function _c8(_e5,_e6){
var _e7=$.data(_e5,"datagrid").options;
var _e8=$.data(_e5,"datagrid").panel;
var tr=$("div.datagrid-body tr[datagrid-row-index="+_e6+"]",_e8);
tr.find(">td").each(function(){
var _e9=$(this).find("div.datagrid-cell");
var _ea=$(this).attr("field");
var col=_5b(_e5,_ea);
if(col&&col.editor){
var _eb,_ec;
if(typeof col.editor=="string"){
_eb=col.editor;
}else{
_eb=col.editor.type;
_ec=col.editor.options;
}
var _ed=_e7.editors[_eb];
if(_ed){
var _ee=_e9.outerWidth();
_e9.addClass("datagrid-editable");
if($.boxModel==true){
_e9.width(_ee-(_e9.outerWidth()-_e9.width()));
}
_e9.html("<table border=\"0\" cellspacing=\"0\" cellpadding=\"1\"><tr><td></td></tr></table>");
_e9.find("table").attr("align",col.align);
$.data(_e9[0],"datagrid.editor",{editor:_ed,elem:_ed.init(_e9.find("td"),_ec)});
}
}
});
_14(_e5,_e6);
};
function _d9(_ef,_f0){
var _f1=$.data(_ef,"datagrid").panel;
var tr=$("div.datagrid-body tr[datagrid-row-index="+_f0+"]",_f1);
tr.find(">td").each(function(){
var _f2=$(this).find("div.datagrid-editable");
if(_f2.length){
var ed=$.data(_f2[0],"datagrid.editor");
if(ed.editor.destroy){
ed.editor.destroy(ed.elem);
}
$.removeData(_f2[0],"datagrid.editor");
var _f3=_f2.outerWidth();
_f2.removeClass("datagrid-editable");
if($.boxModel==true){
_f2.width(_f3-(_f2.outerWidth()-_f2.width()));
}
}
});
};
function _ca(_f4,_f5){
var _f6=$.data(_f4,"datagrid").panel;
var tr=$("div.datagrid-body tr[datagrid-row-index="+_f5+"]",_f6);
if(!tr.hasClass("datagrid-row-editing")){
return true;
}
var _f7=tr.find(".validatebox-text");
_f7.validatebox("validate");
_f7.trigger("mouseleave");
var _f8=tr.find(".validatebox-invalid");
return _f8.length==0;
};
function _f9(_fa,_fb){
var _fc=$.data(_fa,"datagrid").insertedRows;
var _fd=$.data(_fa,"datagrid").deletedRows;
var _fe=$.data(_fa,"datagrid").updatedRows;
if(!_fb){
var _ff=[];
_ff=_ff.concat(_fc);
_ff=_ff.concat(_fd);
_ff=_ff.concat(_fe);
return _ff;
}else{
if(_fb=="inserted"){
return _fc;
}else{
if(_fb=="deleted"){
return _fd;
}else{
if(_fb=="updated"){
return _fe;
}
}
}
}
return [];
};
function _da(_100,_101){
var _102=$.data(_100,"datagrid").panel;
var data=$.data(_100,"datagrid").data;
_102.find("div.datagrid-body tr[datagrid-row-index="+_101+"] td").each(function(){
var cell=$(this).find("div.datagrid-cell");
var _103=$(this).attr("field");
var col=_5b(_100,_103);
if(col){
if(col.formatter){
cell.html(col.formatter(data.rows[_101][_103],data.rows[_101],_101));
}else{
cell.html(data.rows[_101][_103]);
}
}
});
_14(_100,_101);
};
function _104(_105,_106){
var data=$.data(_105,"datagrid").data;
var _107=$.data(_105,"datagrid").insertedRows;
var _108=$.data(_105,"datagrid").deletedRows;
var _109=$.data(_105,"datagrid").editingRows;
var _10a=$.data(_105,"datagrid").selectedRows;
var row=data.rows[_106];
data.total-=1;
if(_107.indexOf(row)>=0){
_107.remove(row);
_10a.remove(row);
}else{
_108.push(row);
}
if(_109.indexOf(row)>=0){
_109.remove(row);
_d9(_105,_106);
}
var _10b=[];
for(var i=0;i<_109.length;i++){
var idx=data.rows.indexOf(_109[i]);
_10b.push(_d7(_105,idx));
_d9(_105,idx);
}
data.rows.remove(row);
_79(_105,data);
var _10c=[];
for(var i=0;i<_109.length;i++){
var idx=data.rows.indexOf(_109[i]);
_10c.push(idx);
}
_109.splice(0,_109.length);
for(var i=0;i<_10c.length;i++){
_c1(_105,_10c[i]);
_c9(_105,_10c[i],_10b[i]);
}
};
function _10d(_10e,row){
if(!row){
return;
}
var _10f=$.data(_10e,"datagrid").panel;
var data=$.data(_10e,"datagrid").data;
var _110=$.data(_10e,"datagrid").insertedRows;
var _111=$.data(_10e,"datagrid").editingRows;
data.total+=1;
data.rows.push(row);
_110.push(row);
var _112=[];
for(var i=0;i<_111.length;i++){
var idx=data.rows.indexOf(_111[i]);
_112.push(_d7(_10e,idx));
_d9(_10e,idx);
}
_79(_10e,data);
var _113=[];
for(var i=0;i<_111.length;i++){
var idx=data.rows.indexOf(_111[i]);
_113.push(idx);
}
_111.splice(0,_111.length);
for(var i=0;i<_113.length;i++){
_c1(_10e,_113[i]);
_c9(_10e,_113[i],_112[i]);
}
var _114=$("div.datagrid-view2 div.datagrid-body",_10f);
var _115=_114.find(">table");
var top=_115.outerHeight()-_114.outerHeight();
_114.scrollTop(top+20);
};
function _116(_117){
var data=$.data(_117,"datagrid").data;
var rows=data.rows;
var _118=[];
for(var i=0;i<rows.length;i++){
_118.push($.extend({},rows[i]));
}
$.data(_117,"datagrid").originalRows=_118;
$.data(_117,"datagrid").updatedRows=[];
$.data(_117,"datagrid").insertedRows=[];
$.data(_117,"datagrid").deletedRows=[];
$.data(_117,"datagrid").editingRows=[];
};
function _119(_11a){
var data=$.data(_11a,"datagrid").data;
var ok=true;
for(var i=0,len=data.rows.length;i<len;i++){
if(_ca(_11a,i)){
_cb(_11a,i,false);
}else{
ok=false;
}
}
if(ok){
_116(_11a);
}
};
function _11b(_11c){
var opts=$.data(_11c,"datagrid").options;
var _11d=$.data(_11c,"datagrid").originalRows;
var _11e=$.data(_11c,"datagrid").insertedRows;
var _11f=$.data(_11c,"datagrid").deletedRows;
var _120=$.data(_11c,"datagrid").updatedRows;
var _121=$.data(_11c,"datagrid").selectedRows;
var data=$.data(_11c,"datagrid").data;
for(var i=0;i<data.rows.length;i++){
_cb(_11c,i,true);
}
var rows=[];
var _122={};
if(opts.idField){
for(var i=0;i<_121.length;i++){
_122[_121[i][opts.idField]]=true;
}
}
_121.splice(0,_121.length);
for(var i=0;i<_11d.length;i++){
var row=$.extend({},_11d[i]);
rows.push(row);
if(_122[row[opts.idField]]){
_121.push(row);
}
}
data.total+=_11f.length-_11e.length;
data.rows=rows;
_79(_11c,data);
$.data(_11c,"datagrid").updatedRows=[];
$.data(_11c,"datagrid").insertedRows=[];
$.data(_11c,"datagrid").deletedRows=[];
$.data(_11c,"datagrid").editingRows=[];
};
function _37(_123,_124){
var _125=$.data(_123,"datagrid").panel;
var opts=$.data(_123,"datagrid").options;
if(_124){
opts.queryParams=_124;
}
if(!opts.url){
return;
}
var _126=$.extend({},opts.queryParams);
if(opts.pagination){
$.extend(_126,{page:opts.pageNumber,rows:opts.pageSize});
}
if(opts.sortName){
$.extend(_126,{sort:opts.sortName,order:opts.sortOrder});
}
if(opts.onBeforeLoad.call(_123,_126)==false){
return;
}
_127();
setTimeout(function(){
_128();
},0);
function _128(){
$.ajax({type:opts.method,url:opts.url,data:_126,dataType:"json",success:function(data){
setTimeout(function(){
_129();
},0);
_79(_123,data);
setTimeout(function(){
_116(_123);
},0);
},error:function(){
setTimeout(function(){
_129();
},0);
if(opts.onLoadError){
opts.onLoadError.apply(_123,arguments);
}
}});
};
function _127(){
$(">div.datagrid-pager",_125).pagination("loading");
var wrap=_125;
$("<div class=\"datagrid-mask\"></div>").css({display:"block",width:wrap.width(),height:wrap.height()}).appendTo(wrap);
$("<div class=\"datagrid-mask-msg\"></div>").html(opts.loadMsg).appendTo(wrap).css({display:"block",left:(wrap.width()-$("div.datagrid-mask-msg",wrap).outerWidth())/2,top:(wrap.height()-$("div.datagrid-mask-msg",wrap).outerHeight())/2});
};
function _129(){
_125.find("div.datagrid-pager").pagination("loaded");
_125.find("div.datagrid-mask-msg").remove();
_125.find("div.datagrid-mask").remove();
};
};
function _12a(_12b,_12c){
var rows=$.data(_12b,"datagrid").data.rows;
var _12d=$.data(_12b,"datagrid").panel;
_12c.rowspan=_12c.rowspan||1;
_12c.colspan=_12c.colspan||1;
if(_12c.index<0||_12c.index>=rows.length){
return;
}
if(_12c.rowspan==1&&_12c.colspan==1){
return;
}
var _12e=rows[_12c.index][_12c.field];
var tr=_12d.find("div.datagrid-body tr[datagrid-row-index="+_12c.index+"]");
var td=tr.find("td[field="+_12c.field+"]");
td.attr("rowspan",_12c.rowspan).attr("colspan",_12c.colspan);
td.addClass("datagrid-td-merged");
for(var i=1;i<_12c.colspan;i++){
td=td.next();
td.hide();
rows[_12c.index][td.attr("field")]=_12e;
}
for(var i=1;i<_12c.rowspan;i++){
tr=tr.next();
var td=tr.find("td[field="+_12c.field+"]").hide();
rows[_12c.index+i][td.attr("field")]=_12e;
for(var j=1;j<_12c.colspan;j++){
td=td.next();
td.hide();
rows[_12c.index+i][td.attr("field")]=_12e;
}
}
setTimeout(function(){
_5c(_12b);
},0);
};
$.fn.datagrid=function(_12f,_130){
if(typeof _12f=="string"){
switch(_12f){
case "options":
return $.data(this[0],"datagrid").options;
case "getPanel":
return $.data(this[0],"datagrid").panel;
case "getPager":
return $.data(this[0],"datagrid").panel.find("div.datagrid-pager");
case "resize":
return this.each(function(){
_3(this,_130);
});
case "reload":
return this.each(function(){
_37(this,_130);
});
case "fixColumnSize":
return this.each(function(){
_4f(this);
});
case "loadData":
return this.each(function(){
_79(this,_130);
_116(this);
});
case "getData":
return $.data(this[0],"datagrid").data;
case "getRows":
return $.data(this[0],"datagrid").data.rows;
case "getRowIndex":
return _91(this[0],_130);
case "getSelected":
var rows=_95(this[0]);
return rows.length>0?rows[0]:null;
case "getSelections":
return _95(this[0]);
case "clearSelections":
return this.each(function(){
_9e(this);
});
case "selectAll":
return this.each(function(){
_a2(this);
});
case "selectRow":
return this.each(function(){
_ab(this,_130);
});
case "selectRecord":
return this.each(function(){
_b2(this,_130);
});
case "unselectRow":
return this.each(function(){
_b8(this,_130);
});
case "beginEdit":
return this.each(function(){
_c1(this,_130);
});
case "endEdit":
return this.each(function(){
_cb(this,_130,false);
});
case "cancelEdit":
return this.each(function(){
_cb(this,_130,true);
});
case "refreshRow":
return this.each(function(){
_da(this,_130);
});
case "validateRow":
return this.each(function(){
_ca(this,_130);
});
case "appendRow":
return this.each(function(){
_10d(this,_130);
});
case "deleteRow":
return this.each(function(){
_104(this,_130);
});
case "getChanges":
return _f9(this[0],_130);
case "acceptChanges":
return _119(this[0]);
case "rejectChanges":
return _11b(this[0]);
case "mergeCells":
return this.each(function(){
_12a(this,_130);
});
}
}
_12f=_12f||{};
return this.each(function(){
var _131=$.data(this,"datagrid");
var opts;
if(_131){
opts=$.extend(_131.options,_12f);
_131.options=opts;
}else{
opts=$.extend({},$.fn.datagrid.defaults,{width:(parseInt($(this).css("width"))||undefined),height:(parseInt($(this).css("height"))||undefined),fit:($(this).attr("fit")?$(this).attr("fit")=="true":undefined)},_12f);
$(this).css("width",null).css("height",null);
var _132=_21(this,opts.rownumbers);
if(!opts.columns){
opts.columns=_132.columns;
}
if(!opts.frozenColumns){
opts.frozenColumns=_132.frozenColumns;
}
$.data(this,"datagrid",{options:opts,panel:_132.panel,selectedRows:[],data:{total:0,rows:[]},originalRows:[],updatedRows:[],insertedRows:[],deletedRows:[],editingRows:[]});
_79(this,_132.data);
_116(this);
}
_2e(this);
if(!_131){
_4f(this);
}
_3(this);
if(opts.url){
_37(this);
}
_3b(this);
});
};
var _133={text:{init:function(_134,_135){
var _136=$("<input type=\"text\" class=\"datagrid-editable-input\">").appendTo(_134);
return _136;
},getValue:function(elem){
return $(elem).val();
},setValue:function(elem,_137){
$(elem).val(_137);
},resize:function(elem,_138){
var _139=$(elem);
if($.boxModel==true){
_139.width(_138-(_139.outerWidth()-_139.width()));
}else{
_139.width(_138);
}
}},textarea:{init:function(_13a,_13b){
var _13c=$("<textarea class=\"datagrid-editable-input\"></textarea>").appendTo(_13a);
return _13c;
},getValue:function(elem){
return $(elem).val();
},setValue:function(elem,_13d){
$(elem).val(_13d);
},resize:function(elem,_13e){
var _13f=$(elem);
if($.boxModel==true){
_13f.width(_13e-(_13f.outerWidth()-_13f.width()));
}else{
_13f.width(_13e);
}
}},checkbox:{init:function(_140,_141){
var _142=$("<input type=\"checkbox\">").appendTo(_140);
_142.val(_141.on);
_142.attr("offval",_141.off);
return _142;
},getValue:function(elem){
if($(elem).attr("checked")){
return $(elem).val();
}else{
return $(elem).attr("offval");
}
},setValue:function(elem,_143){
if($(elem).val()==_143){
$(elem).attr("checked",true);
}else{
$(elem).attr("checked",false);
}
}},numberbox:{init:function(_144,_145){
var _146=$("<input type=\"text\" class=\"datagrid-editable-input\">").appendTo(_144);
_146.numberbox(_145);
return _146;
},getValue:function(elem){
return $(elem).val();
},setValue:function(elem,_147){
$(elem).val(_147);
},resize:function(elem,_148){
var _149=$(elem);
if($.boxModel==true){
_149.width(_148-(_149.outerWidth()-_149.width()));
}else{
_149.width(_148);
}
}},validatebox:{init:function(_14a,_14b){
var _14c=$("<input type=\"text\" class=\"datagrid-editable-input\">").appendTo(_14a);
_14c.validatebox(_14b);
return _14c;
},destroy:function(elem){
$(elem).validatebox("destroy");
},getValue:function(elem){
return $(elem).val();
},setValue:function(elem,_14d){
$(elem).val(_14d);
},resize:function(elem,_14e){
var _14f=$(elem);
if($.boxModel==true){
_14f.width(_14e-(_14f.outerWidth()-_14f.width()));
}else{
_14f.width(_14e);
}
}},datebox:{init:function(_150,_151){
var _152=$("<input type=\"text\" class=\"datagrid-editable-input\">").appendTo(_150);
_152.datebox(_151);
return _152;
},destroy:function(elem){
$(elem).datebox("destroy");
},getValue:function(elem){
return $(elem).val();
},setValue:function(elem,_153){
$(elem).val(_153);
},resize:function(elem,_154){
var _155=$(elem);
if($.boxModel==true){
_155.width(_154-(_155.outerWidth()-_155.width()));
}else{
_155.width(_154);
}
}},combobox:{init:function(_156,_157){
var _158=$("<input type=\"text\">").appendTo(_156);
_158.combobox($.extend({},(_157||{}),{onLoadSuccess:function(){
_158[0].loaded=true;
if(_157&&_157.onLoadSuccess){
_157.onLoadSuccess.apply(this,arguments);
}
}}));
if(!_157.url){
_158[0].loaded=true;
}
return _158;
},destroy:function(elem){
$(elem).combobox("destroy");
},getValue:function(elem){
return $(elem).combobox("getValue");
},setValue:function(elem,_159){
(function(){
if($(elem)[0].loaded){
$(elem).combobox("setValue",_159);
}else{
setTimeout(arguments.callee,100);
}
})();
},resize:function(elem,_15a){
$(elem).combobox("resize",_15a);
}},combotree:{init:function(_15b,_15c){
var _15d=$("<input type=\"text\">").appendTo(_15b);
_15d.combotree(_15c);
var tree=_15d.combotree("tree");
tree.tree({onLoadSuccess:function(){
_15d[0].loaded=true;
}});
if(!tree.tree("options").url){
_15d[0].loaded=true;
}
return _15d;
},destroy:function(elem){
$(elem).combotree("destroy");
},getValue:function(elem){
return $(elem).combotree("getValue");
},setValue:function(elem,_15e){
(function(){
if($(elem)[0].loaded){
$(elem).combotree("setValue",_15e);
}else{
setTimeout(arguments.callee,100);
}
})();
},resize:function(elem,_15f){
$(elem).combotree("resize",_15f);
}}};
$.fn.datagrid.defaults={title:null,iconCls:null,border:true,width:"auto",height:"auto",frozenColumns:null,columns:null,toolbar:null,striped:false,method:"post",nowrap:true,idField:null,url:null,loadMsg:"Processing, please wait ...",rownumbers:false,singleSelect:false,fit:false,pagination:false,pageNumber:1,pageSize:10,pageList:[10,20,30,40,50],queryParams:{},sortName:null,sortOrder:"asc",remoteSort:true,editors:_133,onBeforeLoad:function(_160){
},onLoadSuccess:function(){
},onLoadError:function(){
},onClickRow:function(_161,_162){
},onDblClickRow:function(_163,_164){
},onSortColumn:function(sort,_165){
},onSelect:function(_166,_167){
},onUnselect:function(_168,_169){
},onBeforeEdit:function(_16a,_16b){
},onAfterEdit:function(_16c,_16d,_16e){
},onCancelEdit:function(_16f,_170){
}};
})(jQuery);

