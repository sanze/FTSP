Ext.BLANK_IMAGE_URL="../../resource/ext/resources/images/default/s.gif";
var rawCpuData = [];
    var rawMemData = [];
    var errCnt=0;
    function pushData(cpu,mem){
        var data=[];
        if(rawCpuData.length>99){
            rawCpuData.splice(0,1);
            rawCpuData.push(cpu);
            rawMemData.splice(0,1);
            rawMemData.push(mem);
        }else{
            rawCpuData.push(cpu);
            rawMemData.push(mem);
        }

        var len = rawCpuData.length;
        for(var i=0; i < 100 - len; i++){
            data.push([i, -1, -1]);
        }
        for(var i=0; i < len; i++){
            data.push([99 - len + i, rawCpuData[i], rawMemData[i]]);
        }
        return data;
    }

    var store1 = new Ext.data.ArrayStore({
        fields: ['time', 'pCpu', 'pMem']
    });
    
    var cpuChart = new Ext.Panel({
        width: 700,
        height: 400,
        renderTo: document.body,
        title: '',
        items: {
            xtype: 'linechart',
            store: store1,
            url: '../../resource/ext/resources/charts.swf',
            xField: 'time',
            yAxis: new Ext.chart.NumericAxis({
                title: '使\n用\n率',
                minimum:0,
                maximum:100
            }),
            tipRenderer : function(chart, record, index, series){
                if(series.yField == 'pCpu'){
                    return (99 - record.data.time) + '秒之前\n服务器CPU使用率为: ' + Math.floor(record.data.pCpu*100)/100 + '%';
                }else{
                    return (99 - record.data.time) + '秒之前\n服务器内存使用率为: ' + Math.floor(record.data.pMem*100)/100 + '%';
                }
            },
            style: {
                color: 0x00ff00
            },
            extraStyle: {
                animationEnabled:false,
                xAxis: {
                    color: 0x0000aa,
                    labelRotation: -90,
                    showLabels:false,
                    majorGridLines: {size: 1, color: 0xeeeeee}
                },
                yAxis: {
                    color: 0x0000aa,
                    majorGridLines: {size: 1, color: 0xddddff}
                },
                legend: {
                    display: 'bottom',
                    padding: 5,
                    font:
                    {
                        family: 'Tahoma',
                        size: 13
                    }
                }
            },
            series: [{
                type:'line',
                displayName: 'CPU使用率',
                yField: 'pCpu',
                style: {
                    size: 5,
                    lineSize:1,
                    color: 0x00ff00
                }
            },{
                type:'line',
                displayName: '内存使用率',
                yField: 'pMem',
                style: {
                    size: 5,
                    lineSize:1,
                    color: 0x0000ff
                }
            }]

        }
    });

    function rtrvCPUData(){
        var obj;
        var rc = 0;
        Ext.Ajax.request({ 
            url: 'server-monitor!getUsage.action',
            params:{"ipAddress" : parent.ipAddress},
            type: 'post',
            success: function(response) {
                obj = Ext.decode(response.responseText);
                if(obj.returnResult==0){
                	//Ext.Msg.alert("错误","远程服务器连接失败");
                }else{
                store1.loadData(pushData(obj.cpu * 1, obj.mem * 1));
                errCnt = 0;
                }
            },
            error:function(response) {
                errCnt++;
                if(errCnt == 15){
                    Ext.Msg.alert("错误", response.responseText);
                }
            },
            failure:function(response) {
                errCnt++;
                if(errCnt == 15){
                    Ext.Msg.alert("服务器连接失败!", response.responseText);
                }
            }
        });
    }


    Ext.onReady(function(){
        document.onmousedown=function(){parent.parent.Ext.menu.MenuMgr.hideAll();}
        Ext.Msg = top.Ext.Msg;
        var win = new Ext.Viewport({
                        id:'win',
                        layout : 'fit',
                        items : [cpuChart],
                        renderTo : Ext.getBody()
                    });
        win.show(); 
        rawCpuData = [];
        rawMemData = [];
        store1.removeAll();
        setInterval(rtrvCPUData,1000);
    }); 
