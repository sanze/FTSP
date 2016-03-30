netLevelComboField.emptyText="全部";
projectComboGridField.allowBlank=false;
var querypanel = new Ext.form.FormPanel({
	id : "querypanel",
	name : "querypanel",
	region:"north",  
	height : 37, 
	border : false,
	tbar:['-',netLevelComboBar,'-',projectComboGridBar,'-', '月份：',{
		xtype : 'textfield',
		id : 'queryMonth', 
		name : 'queryMonth', 
		allowBlank : false,
		readOnly : true,
		anchor : '95%',
		cls : 'Wdate',
		listeners : {
			'focus' : function() {
				WdatePicker({
					el : "queryMonth",
					isShowClear : false,
					readOnly : true,
//					maxDate : new Date().getDate()<=3?'%y-{%M-2}':'%y-{%M-1}',
					dateFmt : 'yyyy-MM',
					autoPickDate : true
				});
				this.blur();
			}
		}
	},'-',{
		xtype : 'button',
		text : '查询', 
		id : 'searchPlanBtu',
		icon : '../../../resource/images/btnImages/search.png',
		handler : function(){
			generateTable();
		}
	},{
		xtype : 'button',
		text : '重置', 
		id : 'resetBtu',
		icon : '../../../resource/images/btnImages/refresh.png',
		handler : function(){
			netLevelComboBar.getForm().reset();
			projectComboGridBar.getForm().reset();
			Ext.getCmp('queryMonth').reset();
		}
	},'->',{
		xtype : 'displayfield', 
		width: 55,   
		value:'正常', 
		style : 'background:#33CC00;line-height:20px;text-align : center;vertical-align:middle;'
	},'&nbsp',{
		xtype : 'displayfield', 
		width : 55,
		value:'一般',
		style : 'background:#3366CC;line-height:20px;text-align : center;vertical-align:middle;'
	},'&nbsp',{
		xtype : 'displayfield', 
		width : 55,
		value:'次要',
		style : 'background:#FF9933;line-height:20px;text-align : center;vertical-align:middle;'
	},'&nbsp',{
		xtype : 'displayfield', 
		width : 55,
		value:'重要',
		style : 'background:#FF0000;line-height:20px;text-align : center;vertical-align:middle;'
	},'&nbsp',{
		xtype : 'displayfield', 
		width : 55,
		value:'无数据',
		style : 'background:#A9A9A9;line-height:20px;text-align : center;vertical-align:middle;'
	},'&nbsp','&nbsp','&nbsp','&nbsp']
});  

function headTable(div,days,tdWidth){
	var table=document.createElement("table");
 	table.className="my-table-header-style";  
    var tbody=document.createElement("tbody");
    var tr=document.createElement("tr");    
    for(var k=0;k<days+3;k++){
		var td=document.createElement("td");
		if(k==0 || k==1 || k==2){
			td.style.width="110px"; 
		}else{
			td.innerHTML=k-2; 
   			td.style.background="#3366CC";
   			td.style.color="white"; 
   			td.style.width=tdWidth;
		}
		tr.appendChild(td);
    } 
    tbody.appendChild(tr); 
	table.appendChild(tbody); 
	div.appendChild(table);
}

function creatTable(result,div){ 
	json = eval(result.rList);  
	var tdWidth=(Ext.getBody().getWidth()-330)*0.9/result.days+"px";
	//标题行 
	if(json.length>0){  
		headTable(div,result.days,tdWidth);
		//循环表格 
	    for(var i=0; i<json.length; i++){ 
	 	   var table=document.createElement("table");
	 	   table.className="my-table-style"; 
	 	   if(i==0) table.style.marginTop="0px";
		   var tbody=document.createElement("tbody"); 
		   
		   for(var j=0;j<json[i].length;j++){ 
			   var tr=document.createElement("tr");   
			   var from=0;
			   for(var k=0;k<result.days+3;k++){
	   		       var td=document.createElement("td");
	   		       td.style.width=tdWidth;
	   		       if(k==2)  {
	   		    	   if(json[i][0][0].isflag=="isMain"){
	   		    		   td.innerHTML=json[i][j][0].z_PM_DESCRIPTION; 
	   		    	   }else{
	   		    		   td.innerHTML=json[i][j][0].zOsc_PM_DESCRIPTION; 
	   		    	   }  
	   	   			   td.style.width="135px";
	   	   			   td.style.textAlign="left";
	   		       } else if(k!=0 && k!=1){ 
	   		    	   var exLv=-99;
	   		    	   for(var m=from;m<json[i][j].length;m++){
	   		    		  if((k-2) == parseInt(json[i][j][m].COLLECT_DATE)){
	   		    			  if(json[i][0][0].isflag=="isOsc" && json[i][j][m].zOsc_EXCEPTION_LV!=null){
	   		    				 exLv=parseInt(json[i][j][m].zOsc_EXCEPTION_LV); 
	   		    				 break;
	   		    			  }else if(json[i][0][0].isflag=="isMain" && json[i][j][m].z_EXCEPTION_LV!=null){
	   		    				 exLv=parseInt(json[i][j][m].z_EXCEPTION_LV); 
	   		    				 break;
	   		    			  }
	   		    		  }  
	   		    	   } 
	   		    	   if(exLv!=-99){  
	   		    		   if (exLv == 0) {
	   		    			    td.style.background="#33CC00";  
	   		    		    } else if (exLv == 1) {
		 						td.style.background="#3366CC";  
		 					} else if (exLv == 2) {
		 						td.style.background="#FF9933";  
		 					} else if (exLv == 3) {
		 						td.style.background="#FF0000";  
		 					}
	   		    	   } else{
	   		    		   td.style.background="#A9A9A9";   
	   		    	   } 
	   		       }
	   		       tr.appendChild(td);
	   		    } 
			    tbody.appendChild(tr); 
	   		}
			table.appendChild(tbody);  
			//合并单元格开始
			for (var h = 1; h < json[i].length; h++) {
			   table.rows[h].removeChild(table.rows[h].cells[0]);
			   table.rows[h].removeChild(table.rows[h].cells[0]);
            } 
			table.rows[0].cells[0].rowSpan=json[i].length;  
			table.rows[0].cells[0].style.width="35px";  
		    table.rows[0].cells[0].innerHTML=i+1;   
		    
			table.rows[0].cells[1].rowSpan=json[i].length; 
		    table.rows[0].cells[1].style.background="#A9A9A9"; 
		    table.rows[0].cells[1].style.border="0px"; 
		    table.rows[0].cells[1].style.width="160px";
    	    if(json[i][0][0].isflag=="isMain"){
    		    table.rows[0].cells[1].innerHTML=json[i][0][0].direction+"(主信号)";   
    	    } else if(json[i][0][0].isflag=="isOsc"){
    		    table.rows[0].cells[1].innerHTML=json[i][0][0].direction+"(OSC信号)";   
    	    }
    	 //合并单元格结束
			div.appendChild(table);
	    } 
	}
}

function generateTable() {
	var netLevel = netLevelComboField.getValue();
	var project = projectComboGridField.comboGrid.getValue();
	var collectDate = Ext.getCmp('queryMonth').getValue();
	if(project==null || project=="" || collectDate==null ||collectDate=="") {
		Ext.Msg.alert("提示","请选择系统和月份！");
		return;
	} 
	var searchParam = {
		'netLevel' : netLevel,
		'transSysId' : project,
		'month' : collectDate 
	};
	Ext.getBody().mask('执行中...');
	Ext.Ajax.request({
		url : 'evaluate-statistic!generateDiagramTable.action',
		params : searchParam,
		method : 'POST',
		success : function(response) {
			Ext.getBody().unmask();
			var result = Ext.decode(response.responseText); 
 			if(result.returnResult==1){ 
 				var div = document.getElementById("show"); 
 				while(div.hasChildNodes()){
 					div.removeChild(div.lastChild);
 				} 
 				if(result.returnMessage=="性能数据为空！"){ 
 					Ext.Msg.alert("提示",result.returnMessage);
 					return;
 				}
 				creatTable(result,div);
			}else{ 
				Ext.Msg.alert("提示", "性能评估表生成失败！");
			}
		},
		failure : function(response) {
			var result = Ext.decode(response.responseText);
			Ext.getBody().unmask();
			Ext.Msg.alert("提示", result.returnMessage);
		},
		error : function(response) {
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.getBody().unmask();
			Ext.Msg.alert("提示", result.returnMessage);
		}
	}); 
}
 
var showPanel=new Ext.Panel({
	bodyStyle : 'padding:30px 0 0',
	id : "showPanel", 
	region:"center",
	border:false,
	html:'<div id="show" style="border:none; width:100%;height:100%;overflow-y:auto;overflow-x:auto;"></div>'
}); 

Ext.onReady(function(){ 
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	}; 
	Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 900000; 
	
	var win = new Ext.Viewport({
        id:'win',
		layout : 'border',
		items : [querypanel,showPanel]
	}); 
});