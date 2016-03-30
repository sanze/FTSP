    //****************** 系统信息面板 ********************
    Ext.BLANK_IMAGE_URL="../../resource/ext/resources/images/default/s.gif";
    var sysBar = new Ext.Toolbar({
        items: [{
            xtype: 'tbtext',
            id:'sysDetail',
            text: ''
        },'->',{
            text: 'CPU / 内存 实时信息',
            icon:'../../resource/icons/buttonImages/active.png',
            handler:showCpuChart
        }]
    });
    var sysInfo = ({
        id:'sysInfo',
        xtype:'fieldset',
        collapsible: false,
        collapsed:false,
        height: 50,
        title:'系统信息',
        layout:'fit',
        items:[sysBar]
    });

    //****************** CPU信息面板 ********************
//    var cpuStore = new Ext.data.Store({
//     //   url: 'server-monitor!getCpuInfo.action',
//        reader: new Ext.data.JsonReader({
//            totalProperty: 'total',
//            root : "rows"
//        },[
//            "id","User","model","Idle","vendor","Wait","mhz","Total","Nice","Sys"
//        ])
//    });
    var cpuColumn = new Ext.grid.ColumnModel({
            // specify any defaults for each column
            defaults: {
                sortable: false,
                menuDisabled: true,
                forceFit:true
            },
            columns: [new Ext.grid.RowNumberer({
        		width : 26
        	}),{
                id: 'id',
                header: '名称',
                dataIndex: 'id',
                width:50,
                renderer:cpuLabel
            },{
                id: 'mhz',
                header: '主频',
                width:80,
                dataIndex: 'mhz',
                renderer:cpuFreq
            },{
                id: 'vendor',
                header: '厂商',
                width:60,
                dataIndex: 'vendor'
            },{
                id: 'model',
                header: '型号',
                width:200,
                dataIndex: 'model'
            },{
                id: 'Idle',
                header: 'CPU空闲',
                width:80,
                dataIndex: 'Idle'
            },{
                id: 'Total',
                header: 'CPU使用',
                width:80,
                dataIndex: 'Total'
            }]
        });
    var cpuGrid = new Ext.grid.GridPanel({
        id:"gridPanel",
        stripeRows:true,
        autoScroll:true,
        frame:false,
        cm: cpuColumn,
        store:new Ext.data.ArrayStore({
    		fields : [{name:'id'},{name:'User'},{name:'model'},{name:'Idle'},{name:'vendor'},{name:'Wait'},{name:'mhz'},{name:'Total'},{name:'Nice'},{name:'Sys'}],
    		data : []
    	}),
        loadMask: true
//        tbar: [{
//                text: '刷新信息',
//                hidden:true,
//                icon:'../../resource/icons/buttonImages/refresh.png',
//                handler:function(){
//                    cpuStore.removeAll();
//                    cpuStore.load();
//                }
//            },{
//                text: 'CPU实时信息',
//                icon:'../../resource/icons/buttonImages/active.png',
//                hidden:true,
//                handler:showCpuChart
//            },{
//                text: 'testAction',
//                hidden:true,
//                icon:'../../resource/icons/buttonImages/handOn.png',
//                handler:test
//            }
//        ]
    });
    var cpuInfo = ({
        id:'cpuInfo',
        xtype:'fieldset',
        collapsible: true,
        title:'CPU信息',
        collapsed: false,
        height:150,
        layout:'fit',
        items:[cpuGrid]
    });

    //****************** 内存信息面板 ********************
//    var memStore = new Ext.data.Store({
//        url: 'server-monitor!getMemInfo.action',
//        reader: new Ext.data.JsonReader({
//            totalProperty: 'total',
//            root : "rows"
//        },["id","total","used","free","percent"])
//    });
//    var memStore =  new Ext.data.ArrayStore({
//		fields : [{name:'id'},{name:'total'},{name:'used'},{name:'free'},{name:'percent'}],
//		data : []
//	}
    var memColumn = new Ext.grid.ColumnModel({
        // specify any defaults for each column
        defaults: {
            sortable: false,
            menuDisabled: true,
            forceFit:true
        },
        columns: [new Ext.grid.RowNumberer({
    		width : 26
    	}),{
            id: 'id',
            header: '名称',
            dataIndex: 'id',
            width:100
        },{
            id: 'total',
            header: '总大小',
            width:60,
            dataIndex: 'total',
            renderer:memLabel
        },{
            id: 'used',
            header: '已使用',
            width:60,
            dataIndex: 'used',
            renderer:memLabel
        },{
            id: 'free',
            header: '可用',
            width:60,
            dataIndex: 'free',
            renderer:memLabel
        },{
            id: 'percent',
            header: '使用状况',
            width:300,
            dataIndex: 'percent',
            renderer:percentLabel
        }]
    });
    var memGrid = new Ext.grid.GridPanel({
        id:"memGrid",
        region:"center",
        stripeRows:true,
        autoScroll:true,
        frame:false,
        cm: memColumn,
        store:new Ext.data.ArrayStore({
    		fields : [{name:'id'},{name:'total'},{name:'used'},{name:'free'},{name:'percent'}],
    		data : []
    	}),
        loadMask: true
    });
    var memInfo = ({
        id:'memInfo',
        xtype:'fieldset',
        height:110,
        title:'内存信息',
        style:'margin-top:15px',
        collapsible: true,
        collapsed: false,
        layout:'fit',
        items:[memGrid]
    });
    
    //****************** 磁盘信息面板 ********************
//    var driveStore = new Ext.data.Store({
//        url: 'server-monitor!getDriveInfo.action',
//        reader: new Ext.data.JsonReader({
//            totalProperty: 'total',
//            root : "rows"
//        },["devName","dirName","fileSystem","driveType","total","used","free","percent"])
//    });
    var driveColumn = new Ext.grid.ColumnModel({
        // specify any defaults for each column
        defaults: {
            sortable: false,
            menuDisabled: true,
            forceFit:true
        },
        columns: [new Ext.grid.RowNumberer({
    		width : 26
    	}),{
            id: 'devName',
            header: '名称',
            dataIndex: 'devName',
            width:100
        },{
            id: 'fileSystem',
            header: '文件系统',
            width:60,
            dataIndex: 'fileSystem'
        },{
            id: 'driveType',
            header: '类型',
            width:80,
            dataIndex: 'driveType'
        },{
            id: 'total',
            header: '总大小',
            width:60,
            dataIndex: 'total',
            renderer:driveLabel
        },{
            id: 'used',
            header: '已使用',
            width:60,
            dataIndex: 'used',
            renderer:driveLabel
        },{
            id: 'free',
            header: '可用',
            width:60,
            dataIndex: 'free',
            renderer:driveLabel
        },{
            id: 'percent',
            header: '使用状况',
            width:300,
            dataIndex: 'percent',
            renderer:percentLabel
        }]
    });
    var driveGrid = new Ext.grid.GridPanel({
        id:"driveGrid",
        region:"center",
        stripeRows:true,
        autoScroll:true,
        frame:false,
        cm: driveColumn,
        store:new Ext.data.ArrayStore({
    		fields : [{name:'devName'},{name:'dirName'},{name:'fileSystem'},{name:'driveType'},{name:'total'},{name:'used'},{name:'free'},{name:'percent'}],
    		data : []
    	}),
        loadMask: true
    });
    var driveInfo = ({
        id:'driveInfo',
        xtype:'fieldset',
        height: 200,
        style:'margin-top:15px',
        title:'磁盘信息',
        collapsible: true,
        collapsed: false,
        layout:'fit',
        items:[driveGrid]
    });
    
    //****************** 网络信息面板 ********************
    var netBasic = ({
        id:'netBasic',
        xtype:'panel',
        title:'基本信息',
        border:true,
        height: 55,
        labelWidth : 75,
    	labelAlign : 'right',
    	labelSeparator : "：",
        items:[{
        	border:false,
    		layout:'column',
    		items:[{
    			columnWidth : .5,
    			layout : 'form',
    			border : false,
    			items:[{
    				xtype : 'displayfield',
    				id : 'netIp',
    				fieldLabel : 'IP地址'
    			}]
    		},{
    			columnWidth : .5,
    			layout : 'form',
    			border : false,
    			items:[{
    				xtype : 'displayfield',
    				id : 'netIpMask',
    				fieldLabel : '子网掩码'
    			}]
    		}]
        }]
    });
    var netStat = ({
        id:'netStat',
        xtype:'panel',
        title:'统计信息',
        flex:1,
        // layout:'column',
        labelWidth : 75,
    	labelAlign : 'right',
    	labelSeparator : "：",
    	items:[{
        	border:false,
    		layout:'column',
    		items:[{
    			columnWidth : .5,
    			layout : 'form',
    			border : false,
    			items:[{
    				xtype : 'displayfield',
    				id : 'rxPacket',
    				fieldLabel : '接收数据包'
    			},{
    				xtype : 'displayfield',
    				id : 'rxByte',
    				fieldLabel : '接收字节数'
    			},{
    				xtype : 'displayfield',
    				id : 'rxErr',
    				fieldLabel : '接收错误'
    			},{
    				xtype : 'displayfield',
    				id : 'rxDrop',
    				fieldLabel : '接收丢包'
    			}]
    		},{
    			columnWidth : .5,
    			layout : 'form',
    			border : false,
    			items:[{
    				xtype : 'displayfield',
    				id : 'txPacket',
    				fieldLabel : '发送数据包'
    			},{
    				xtype : 'displayfield',
    				id : 'txByte',
    				fieldLabel : '发送字节数'
    			},{
    				xtype : 'displayfield',
    				id : 'txErr',
    				fieldLabel : '发送错误'
    			},{
    				xtype : 'displayfield',
    				id : 'txDrop',
    				fieldLabel : '发送丢包'
    			}]
    		}]
        }]
    });
    
    var netInfo =({
        xtype:'fieldset',
        autoScroll:true,
        height:400,
        style:'margin-top:15px',
        title:'网络信息',
        collapsible: true,
        collapsed: false,
        items:[{id:'netInfo',layout:'form',anchor:'90%'}]
    });
    


    //****************** 总面板 ********************
	var serverPanel = new Ext.Panel({
	    id:'serverPanel',
	    title:'服务器信息',
	    labelWidth: 30,
	    padding:'5,5,5,5',
	    autoScroll:true,
	    collapsible:false,
	    collapsed:false,
	    items:[sysInfo, cpuInfo, memInfo, driveInfo, netInfo]
	});

    //测试SysMonAction功能
    function test(){
        Ext.Ajax.request({
            url: 'server-monitor!testMon.action',
            type: 'post',
            success: function(response) {
                obj = Ext.decode(response.responseText);
            },
            error:function(response) {
                Ext.Msg.alert("错误",response.responseText);
            },
            failure:function(response) {
                Ext.Msg.alert("错误",response.responseText);
            }
        });
    }

    //----------------------------------函数区----------------------------------
    /*
    *   初始化数据
    */
    function initData(){
        
//        memStore.load();
//        driveStore.load();
        Ext.Ajax.request({
            url: 'server-monitor!getSysInfo.action',
            params:{"ipAddress" : ipAddress},
            type: 'post',
            success: function(response) {
                objall = Ext.decode(response.responseText);
                if(objall.returnResult==0){
                	Ext.Msg.alert("错误","远程服务器连接失败");
                }else{
                obj=objall.sysInfo;
                Ext.getCmp('sysInfo').setTitle(obj.os + ' ' + obj.patch + '&nbsp;&nbsp;&nbsp;(' + obj.dataBit + '位)');
                Ext.getCmp('sysDetail').setText('系统信息详情: ' + obj.arch + '架构 ' + memLabel(obj.totalMem) + '内存 ');// + driveLabel(obj.totalSpace) + '硬盘');
                setNetInfo(objall.netInfo);
                var memArr = objall.memInfo;
                var memRecord=new Array();
		    	for ( var i = 0; i < memArr.length; i++) {
		    		// 定义表格的一条记录
		    		var record = new Ext.data.Record(['id','total','used','free','percent']);
			    	record.set('id',memArr[i].id);
			    	record.set('total',memArr[i].total);
			    	record.set('used',memArr[i].used);
			    	record.set('free',memArr[i].free);
			    	record.set('percent',memArr[i].percent);
			    	memRecord.push(record);
			    	
				}
		    	memGrid.getStore().add(memRecord);
		    	var cpuArr = objall.cpuInfo;
		    	var cpuRecord=new Array();
		    	for ( var i = 0; i < cpuArr.length; i++) {
		    		// 定义表格的一条记录
		    		var record = new Ext.data.Record(["id","User","model","Idle","vendor","Wait","mhz","Total","Nice","Sys"]);
		    		record.set('id',cpuArr[i].id);
		    		record.set('User',cpuArr[i].User);
		    		record.set('model',cpuArr[i].model);
		    		record.set('Idle',cpuArr[i].Idle);
		    		record.set('vendor',cpuArr[i].vendor);
		    		record.set('Wait',cpuArr[i].Wait);
		    		record.set('mhz',cpuArr[i].mhz);
		    		record.set('Total',cpuArr[i].Total);
		    		record.set('Nice',cpuArr[i].Nice);
		    		record.set('Sys',cpuArr[i].Sys);
		    		cpuRecord.push(record);
		    	}
		    	cpuGrid.getStore().add(cpuRecord);
		    	var driveArr = objall.driveInfo;
		    	var driveRecord=new Array();
		    	for ( var i = 0; i < driveArr.length; i++) {
		    		// 定义表格的一条记录
		    		var record = new Ext.data.Record(["devName","dirName","fileSystem","driveType","total","used","free","percent"]);
		    		record.set('devName',driveArr[i].devName);
		    		record.set('dirName',driveArr[i].dirName);
		    		record.set('fileSystem',driveArr[i].fileSystem);
		    		record.set('driveType',driveArr[i].driveType);
		    		record.set('total',driveArr[i].total);
		    		record.set('used',driveArr[i].used);
		    		record.set('free',driveArr[i].free);
		    		record.set('percent',driveArr[i].percent);
		    		record.set('Sys',driveArr[i].percent);
		    		driveRecord.push(record);
		    	}
		    	driveGrid.getStore().add(driveRecord);
		    	// 向表格里添加值
                }
            },
            error:function(response) {
                Ext.Msg.alert("错误",response.responseText);
            },
            failure:function(response) {
                Ext.Msg.alert("错误",response.responseText);
            }
        });
    }
    
    
    
    
    function setNetInfo(o){
    	var obj=o.netInfos;
    	if(obj && obj.length>0){
    		for(var i=0;i<obj.length;i++){
    			 var netBasic = ({
    			        id:'netBasic'+(i+1),
    			        xtype:'panel',
    			        title:'基本信息'+(i+1),
    			        border:true,
    			        height: 55,
    			        labelWidth : 75,
    			    	labelAlign : 'right',
    			    	labelSeparator : "：",
    			        items:[{
    			        	border:false,
    			    		layout:'column',
    			    		items:[{
    			    			columnWidth : .5,
    			    			layout : 'form',
    			    			border : false,
    			    			items:[{
    			    				xtype : 'displayfield',
    			    				id : 'netIp'+(i+1),
    			    				fieldLabel : 'IP地址'
    			    			}]
    			    		},{
    			    			columnWidth : .5,
    			    			layout : 'form',
    			    			border : false,
    			    			items:[{
    			    				xtype : 'displayfield',
    			    				id : 'netIpMask'+(i+1),
    			    				fieldLabel : '子网掩码'
    			    			}]
    			    		}]
    			        }]
    			    });
    			    var netStat = ({
    			        id:'netStat'+(i+1),
    			        xtype:'panel',
    			        title:'统计信息'+(i+1),
    			        flex:1,
    			        labelWidth : 75,
    			    	labelAlign : 'right',
    			    	labelSeparator : "：",
    			    	items:[{
    			        	border:false,
    			    		layout:'column',
    			    		items:[{
    			    			columnWidth : .5,
    			    			layout : 'form',
    			    			border : false,
    			    			items:[{
    			    				xtype : 'displayfield',
    			    				id : 'rxPacket'+(i+1),
    			    				fieldLabel : '接收数据包'
    			    			},{
    			    				xtype : 'displayfield',
    			    				id : 'rxByte'+(i+1),
    			    				fieldLabel : '接收字节数'
    			    			},{
    			    				xtype : 'displayfield',
    			    				id : 'rxErr'+(i+1),
    			    				fieldLabel : '接收错误'
    			    			},{
    			    				xtype : 'displayfield',
    			    				id : 'rxDrop'+(i+1),
    			    				fieldLabel : '接收丢包'
    			    			}]
    			    		},{
    			    			columnWidth : .5,
    			    			layout : 'form',
    			    			border : false,
    			    			items:[{
    			    				xtype : 'displayfield',
    			    				id : 'txPacket'+(i+1),
    			    				fieldLabel : '发送数据包'
    			    			},{
    			    				xtype : 'displayfield',
    			    				id : 'txByte'+(i+1),
    			    				fieldLabel : '发送字节数'
    			    			},{
    			    				xtype : 'displayfield',
    			    				id : 'txErr'+(i+1),
    			    				fieldLabel : '发送错误'
    			    			},{
    			    				xtype : 'displayfield',
    			    				id : 'txDrop'+(i+1),
    			    				fieldLabel : '发送丢包'
    			    			}]
    			    		}]
    			        }]
    			    });
    			
    			    var netVbox=({
    			    	layout:'vbox',
    			    	height:180,
    			        layoutConfig: {
    			            align : 'stretch',
    			            pack  : 'start'
    			        },
    			        items: [netBasic,netStat]
    			    });
    			    Ext.getCmp('netInfo').add(netVbox);
    			    Ext.getCmp('netIp'+(i+1)).setValue(''+obj[i].ip);
			        Ext.getCmp('netIpMask'+(i+1)).setValue(''+obj[i].mask);
			        Ext.getCmp('rxPacket'+(i+1)).setValue(''+obj[i].rxPacket);
			        Ext.getCmp('txPacket'+(i+1)).setValue(''+obj[i].txPacket);
			        Ext.getCmp('rxByte'+(i+1)).setValue(''+byteLabel(obj[i].rxByte));
			        Ext.getCmp('txByte'+(i+1)).setValue(''+byteLabel(obj[i].txByte));
			        Ext.getCmp('rxErr'+(i+1)).setValue(''+obj[i].rxErr);
			        Ext.getCmp('txErr'+(i+1)).setValue(''+obj[i].txErr);
			        Ext.getCmp('rxDrop'+(i+1)).setValue(''+obj[i].rxDrop);
			        Ext.getCmp('txDrop'+(i+1)).setValue(''+obj[i].txDrop);
    		}
    		Ext.getCmp('netInfo').doLayout();
    	}
    	
    	
    }
    /*
    *   格式化CPU名称
    */
    function cpuLabel(v){
        return 'CPU ' + (v + 1);
    }
    /*
    *   格式化CPU频率
    */
    function cpuFreq(v){
        if(v<1000){
            return v + 'M Hz';
        }else{
            return Math.floor(v / 10 + 0.5) / 100 + 'G Hz';
        }
    }
    /*
    *   显示实时CPU使用率
    */
    function showCpuChart(){
        var url = "cpu.jsp";
        var cpuChartForm = new Ext.Window({
            id:'cpuChartForm',
            title:'实时CPU/内存信息',
            width : 800,
            height : 450,
            isTopContainer : true,
            modal : true,
            plain:true,  //是否为透明背景
            html : '<iframe src='+url+' height="100%" width="100%" frameborder=0 border=0/>'
        });
        cpuChartForm.show();
    }
    /*
    *   格式化内存大小
    */
    function memLabel(v){
        v /= 1024 * 1024;
        v = Math.floor(v);
        if(v>1000){
            v /= 1024;
            return Math.floor(v*100 + 0.5)/100 + 'G';
        }else{
            return v + 'MB';
        }
    }
    /*
    *   格式化硬盘大小
    */
    function driveLabel(v){
        if(v === 0)
            return '-';
        if(typeof v=="undefined"){
        	return '-';
        }
        v /= 1024;
        v = Math.floor(v);
        if(v>1000){
            v /= 1024;
            return Math.floor(v*100 + 0.5)/100 + 'G';
        }else{
            return v + 'MB';
        }
    }
    /*
    *   格式化 字节数
    */
    function byteLabel(v){
        if(v === 0)
            return '0';
        if(typeof v=="undefined"){
        	return '-';
        }
        v /= 1024;
        if(v > 1024){   
            v /= 1024;
            if(v > 1024){
                v /= 1024;
                if(v > 1024){
                    v /= 1024;  //T
                    return Math.floor(v*100 + 0.5)/100 + 'T';
                }else{          //G
                    return Math.floor(v*100 + 0.5)/100 + 'G';
                }
            }else{              //M
                return Math.floor(v*100 + 0.5)/100 + 'M';
            }
        }else{                  //K
            return Math.floor(v*100 + 0.5)/100 + 'K';
        }
    }
    /*
    *   格式化使用率，以进度条显示,比较直观
    */
    function percentLabel(v){
    	if(typeof v=="undefined"){
        	v= 0;
        }
		var id = Ext.id();
		(function() { 
                var bar = new Ext.ProgressBar({
				height: 15,
                text: Math.floor(v*10000)/100 + '%',
				renderTo: id,
				value: v
				});
		}).defer(25);
		return (String.format('<div id="{0}"></div>', id));
    }
    

    Ext.onReady(function(){
        document.onmousedown=function(){parent.parent.Ext.menu.MenuMgr.hideAll();}
        Ext.Msg = top.Ext.Msg;
        var win = new Ext.Viewport({
                        id:'win',
                        layout : 'fit',
                        items : [serverPanel],
                        renderTo : Ext.getBody()
                    });
        win.show();
        initData();
    });
