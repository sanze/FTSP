/*
 * hg
 * 2013.12
 */


// ==================页面====================
var formPanel = new Ext.FormPanel({
	region : "center",
	// labelAlign: 'top',
	frame : false,
//	title: '新增用户',
	bodyStyle : 'padding:20px 10px 0',
	// labelWidth: 100,
	labelAlign : 'right',
	autoScroll : true,
	items : [{
		xtype : 'textfield',
		id : 'id',
		name : 'id',
		fieldLabel : 'id',
		width : 200,
		height : 25,
		hidden : true
	},{
			xtype : 'displayfield',//displayfield
			id : 'name',
			name : 'name',
			fieldLabel : '姓名',
			width : 200,
			height : 25
		},{
			xtype : 'displayfield',
			id : 'address',
			name : 'address',
			fieldLabel : '地址',
			width : 200,
			height : 25           //此验证依然有效.不许为空. 
		},{
			xtype : 'displayfield',
			id : 'ip',
			name : 'ip',
			fieldLabel : 'ip地址',
			width : 200,
			height : 25
		},{
			xtype : 'displayfield',
			id : 'phone',
			name : 'phone',
			fieldLabel : '电话',
			width : 200,
			height : 25
		},{
			xtype : 'displayfield',
			id : 'note',
			name : 'note',
			fieldLabel : '摘要',
			width : 200
           
	}],
	buttons : [{
				text : '确定',
				icon : '../../../resource/images/buttonImages/submit.png',
				handler : close
			}]
});

// =================函数===================
function close() {
	var win = parent.Ext.getCmp('editWindow');
	if (win) {
		win.close();
	}
}

function init() {
	Ext.getCmp('id').setValue(id);
	
	if(saveType == '1'){
		var row = parent.selections[0].data;
		Ext.getCmp('name').setValue(row.name);
		Ext.getCmp('address').setValue(row.address);
		Ext.getCmp('ip').setValue(row.ip);
		Ext.getCmp('phone').setValue(row.phone);
		Ext.getCmp('note').setValue(row.note);
	}
//	Ext.getCmp('saveType').setValue(saveType);
	
}

Ext.onReady(function() {
			Ext.Msg = top.Ext.Msg;
			Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
			document.onmousedown = function() {
				top.Ext.menu.MenuMgr.hideAll();
			}
			Ext.QuickTips.init(); // 开启悬停提示
			Ext.form.Field.prototype.msgTarget = 'side'; // 提示显示风格
			var win = new Ext.Viewport({
						id : 'win',
						loadMask : true,
						layout : 'border',
						items : [formPanel],
						renderTo : Ext.getBody()
					});
			init();
		});

//扩展VTypes增加id验证方式,和年龄验证  
Ext.apply(Ext.form.VTypes,{  
        'age' : function(_v){  
            if(/^\d+$/.test(_v)){//判断必须是数字    
                var _age = parseInt(_v);  
                //增加业务逻辑,小于100的数字才符合年龄  
                if(0 < _age && _age <100){  
                    return true ;  
                }  
            }  
            return false ;  
        },  
        ageText : '年龄必须为数字，并且不能超过100岁，格式为23' , //出错信息后的默认提示信息      
        ageMask:/[0-9]/i  //键盘输入时的校验  
})  