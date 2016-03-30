var count=0;
var expander = new Ext.grid.RowExpander({ 
    tpl : new Ext.XTemplate(
    '<div class="detailData" style="margin-left:64px;width:97%;">',
    '',
    '</div>')
});  
expander.on("expand",function(expander,r,body,rowIndex){  
	count++;
	treeGridPanel.getView().headersDisabled =true; 
	var columns = treeGridPanel.getColumnModel().getColumnsBy(function(c){
		  return !c.hidden;
		});
	var cols=new Array(); 
	for(var index=3;index<parseInt(columns.length);index++){
		cols.push(columns[index]);
	} 
	if (Ext.DomQuery.select("div.x-panel-bwrap",body).length==0){ 
		var store = new Ext.data.Store({
			url: 'fault!getAlarmHavingConverge.action',// 数据请求地址
			baseParams : {// 请求参数
				'jsonString' : r.get("_id")  
			},
			reader : new Ext.data.JsonReader({// 数据源数据格式 
			root : 'rows'// 列表数据
			}, 	['_id','OBJECT_TYPE','PTP_ID','NE_ID','UNIT_ID','BASE_LINK_ID','BASE_PRO_GROUP_ID','SHELF_NO','CTP_ID',
				 'IS_ACK','PERCEIVED_SEVERITY','NATIVE_PROBABLE_CAUSE','NORMAL_CAUSE','EMS_GROUP_NAME','EMS_NAME','SUBNET_NAME',
				 'NE_NAME','PRODUCT_NAME','SLOT_DISPLAY_NAME','UNIT_NAME','PORT_NAME','DOMAIN','PTP_TYPE',
				 'INTERFACE_RATE','CTP_NAME','IS_CLEAR','FIRST_TIME','AMOUNT','NE_TIME','SERVICE_AFFECTING',
				 'CLEAR_TIME','DURATION','RECENT_DURATION','ACK_TIME','ACK_USER','ALARM_TYPE','CONVERGE_FLAG'])
		}); 
		store.load({
			callback : function(records,options,success){ 
				if (!success) {
					Ext.Msg.alert('错误', '查询失败！请重新查询');
				}
			}
		});
	    var cm = new Ext.grid.ColumnModel({ 
			columns :cols 
		});
		var grid = new Ext.grid.GridPanel({
		    store:store,
		    cm:cm,
		    renderTo:Ext.DomQuery.select("div.detailData",body)[0], 
		    hideHeaders :true,
		    autoHeight:true,  
			stripeRows : true,
			border:false
	    });
		grid.getEl().swallowEvent([  
           'mousedown', 'mouseup', 'click',  
           'contextmenu', 'mouseover', 'mouseout',  
           'dblclick', 'mousemove'  
        ]);  
	}
}); 

expander.on("collapse",function(expander,r,body,rowIndex){   
	count--;
	if(count==0){
		treeGridPanel.getView().headersDisabled =false;
	}
}); 

var storeCg = new Ext.data.Store({
	url : 'fault!getCurrentAlarms.action',// 数据请求地址 
	reader : new Ext.data.JsonReader({// 数据源数据格式
				totalProperty : 'total',// 记录数
				root : 'rows'// 列表数据
			}, 
			['_id','OBJECT_TYPE','PTP_ID','NE_ID','UNIT_ID','BASE_LINK_ID','BASE_PRO_GROUP_ID','SHELF_NO','CTP_ID',
			 'IS_ACK','PERCEIVED_SEVERITY','NATIVE_PROBABLE_CAUSE','NORMAL_CAUSE','EMS_GROUP_NAME','EMS_NAME',"SUBNET_NAME",
			 'NE_NAME','PRODUCT_NAME','SLOT_DISPLAY_NAME','UNIT_NAME','PORT_NAME','DOMAIN','PTP_TYPE',
			 'INTERFACE_RATE','CTP_NAME','IS_CLEAR','FIRST_TIME','AMOUNT','NE_TIME','SERVICE_AFFECTING',
			 'CLEAR_TIME','DURATION','RECENT_DURATION','ACK_TIME','ACK_USER','ALARM_TYPE','CONVERGE_FLAG','HAVE_CHILD'])
});
storeCg.on("load", function(st, recs, opt){
	//先清除状态
	Beeper.clear();
	//重新计算数据
	Ext.each(recs, function(v){
		if((v.get("IS_CLEAR")==2 && v.get("IS_ACK")==2 )){
			Beeper.add(v.get("EMS_NAME"), v.get("PERCEIVED_SEVERITY"));
		}
		return true;
	});
	//播放
	Beeper.play();
});
/**
 * 创建表格分页工具栏
 */
var pageToolCg = new Ext.PagingToolbar({
	pageSize : 500,
	store : storeCg,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : '没有记录'
});
var chk=new Ext.grid.CheckboxSelectionModel({ 
        singleSelect : false
    });
var cmCg = new Ext.grid.ColumnModel({ 
			defaults : {// 所有列默认的属性
		sortable : true 
			},
	columns : [ new Ext.grid.RowNumberer({ 
		width : 26 
	}), expander,chk, 	{
		header : '告警数据ID',
		dataIndex : '_id',
		width : 80,
		hidden : true
	},{
		header : '告警对象类型',
		dataIndex : 'OBJECT_TYPE',
		width : 80,
		hidden : true
	},{
		header : '端口ID',
		dataIndex : 'PTP_ID',
		width : 80,
		hidden : true
	},{
		header : '网元ID',
		dataIndex : 'NE_ID',
		width : 80,
		hidden : true
	},{
		header : '板卡ID',
		dataIndex : 'UNIT_ID',
		width : 80,
		hidden : true
	},{
		header : '链路ID',
		dataIndex : 'BASE_LINK_ID',
		width : 80,
		hidden : true
	},{
		header : '保护组ID',
		dataIndex : 'BASE_PRO_GROUP_ID',
		width : 80,
		hidden : true
	},{
		header : '子架号',
		dataIndex : 'SHELF_NO',
		width : 30,
		hidden : true
	},{
				header : '确认',
				dataIndex : 'IS_ACK',
				width : 50,
				renderer : function(value){
					if(value==1){
						return '确认';
					}else if(value==2){
						return '';
					}
				} 
			},{
				header : '告警级别',
				dataIndex : 'PERCEIVED_SEVERITY',
				width : 80,
				renderer : function(value,meta,record){
					if(record.data["IS_CLEAR"]==2){
						if(value==1){
							meta.css= PS_CRITICAL_IMAGE;
							return '<font color='+PS_CRITICAL_FONT+'>紧急</font>';
						}else if(value==2){
							meta.css= PS_MAJOR_IMAGE;
							return '<font color='+PS_MAJOR_FONT+'>重要</font>';
						}else if(value==3){
							meta.css= PS_MINOR_IMAGE;
							return '<font color='+PS_MINOR_FONT+'>次要</font>';
						}else if(value==4){
							meta.css= PS_WARNING_IMAGE;
							return '<font color='+PS_WARNING_FONT+'>提示</font>';
						}
					}else{
						meta.css= PS_CLEARED_IMAGE;
						if(value==1){
							return '<font color='+PS_CLEARED_FONT+'>紧急</font>';
						}else if(value==2){
							return '<font color='+PS_CLEARED_FONT+'>重要</font>';
						}else if(value==3){
							return '<font color='+PS_CLEARED_FONT+'>次要</font>';
						}else if(value==4){
							return '<font color='+PS_CLEARED_FONT+'>提示</font>';
						}
					}
				} 
			},{
				header : '告警名称',
				dataIndex : 'NATIVE_PROBABLE_CAUSE',
				width : 100 
			},{
				header : '归一化名称',
				dataIndex : 'NORMAL_CAUSE',
				width : 100 
			},{
				header : '网管分组',
				dataIndex : 'EMS_GROUP_NAME',
				width : 100
			},{
				header : '网管',
				dataIndex : 'EMS_NAME',
				width : 100
	},{
		header : '子网',
		dataIndex : 'SUBNET_NAME',
		width : 70,
		hidden : true
	},{
		header : '网元名称',
		dataIndex : 'NE_NAME',
		width : 100
	},{
		header : '网元型号',
		dataIndex : 'PRODUCT_NAME',
		width : 80
	},{
		header : '槽道',
		dataIndex : 'SLOT_DISPLAY_NAME',
		width : 100
	},{
		header : '板卡',
		dataIndex : 'UNIT_NAME',
		width : 100
	},{
		header : '端口',
		dataIndex : 'PORT_NAME',
		width : 80
	},{
		header : '业务类型',
		dataIndex : 'DOMAIN',
		width : 60,
		renderer : function(value){
			if(value==1){
				return 'SDH';
			}else if(value==2){
				return 'WDM';
			}else if(value==3){
				return 'ETH';
			}else if(value==4){
				return 'ATM';
			}
		}
	},{
		header : '端口类型',
		dataIndex : 'PTP_TYPE',
		width : 80
	},{
		header : '速率',
		dataIndex : 'INTERFACE_RATE',
		width : 80
	},{
		header : '通道',
		dataIndex : 'CTP_NAME',
		width : 100
	},{
		header : '清除状态',
		dataIndex : 'IS_CLEAR',
		width : 80,
		renderer : function(value){
			if(value==1){
				return '已清除';
			}else{
				return '未清除';
			}
		}
	},{
		header : '首次发生时间',
		dataIndex : 'FIRST_TIME',
		width : 100
	},{
		header : '频次',
		dataIndex : 'AMOUNT',
		width : 40
	},{
		header : '最近发生时间',
		dataIndex : 'NE_TIME',
		width : 100
	},{
		header : '业务影响',
		dataIndex : 'SERVICE_AFFECTING',
		width : 60,
		renderer : function(value){
			if(value==1){
				return '影响';
			}else if(value==2){
				return '不影响';
			}else if(value==0){
				return '未知';
			}
		}
	},{
		header : '清除时间',
		dataIndex : 'CLEAR_TIME',
		width : 100
	},{
		header : '持续时间',
		dataIndex : 'DURATION',
		width : 100
	},{
		header : '最近持续时间',
		dataIndex : 'RECENT_DURATION',
		width : 100
	},{		
		header : '确认时间',
		dataIndex : 'ACK_TIME',
		width : 100
	},{
		header : '确认者',
		dataIndex : 'ACK_USER',
		width : 80
	},{
		header : '告警类型',
		dataIndex : 'ALARM_TYPE',
		width : 60,
		renderer : function(value){
			if(value==0){
				return '通信';
			}else if(value==1){
				return '服务';
			}else if(value==2){
				return '设备';
			}else if(value==3){
				return '处理';
			}else if(value==4){
				return '环境';
			}else if(value==5){
				return '安全';
			}else if(value==6){
				return '连接';
			}
		}
	}]
}); 

var treeGridPanel = new Ext.grid.GridPanel({ 
	store : storeCg,
	id:"treeGridPanel",
	cm : cmCg,
	selModel : chk, 
	enableColumnMove:false,
	stripeRows : true, // 交替行效果
	forceFit : true,
	plugins:[expander], 
	bbar : pageToolCg
});
treeGridPanel.on('rowdblclick', function(grid, rowIndex, e){
	grid.getSelectionModel().selectRow(rowIndex);
	gotoBayface();
});
