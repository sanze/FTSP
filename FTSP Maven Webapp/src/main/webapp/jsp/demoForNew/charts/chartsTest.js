Ext.BLANK_IMAGE_URL = '../ext/resources/images/default/s.gif';  
//左边功能树     
Ext.onReady(function(){     
       var  win=new Ext.Window({  
        title:'falsh报表展示1',  
            layout:'fit',  
            width:500,  
            height:400,  
            closeAction:'close',  
            plain:true,  
            html:'<div id="chart1div"></div>'  
        });  
          
    var FCF_MSColumn3DPanel=new Ext.Panel({  
        title:'综合3D柱状图',  
        html:'<div id="chart1div"></div>'  
    });  
    var FCF_Column3DPanel=new Ext.Panel({  
        title:'3D柱状图',  
        html:'<div id="chart2div"></div>'  
    });  
    var FCF_Pie3DPanel=new Ext.Panel({  
        title:'3D饼状图',  
        html:'<div id="chart3div"></div>'  
    });  
      
    var tabPanel=new Ext.TabPanel({  
            autoTabs       : true,  
            activeTab      : 0,  
            deferredRender : false,  
            border         : false,  
            items:[FCF_MSColumn3DPanel,FCF_Column3DPanel,FCF_Pie3DPanel]  
    });  
    tabPanel.on("tabchange",function(tp,p){  
        tp.doLayout();    
    })    
    win.add(tabPanel);  
    win.show();  
    win.on("beforedestroy",function(win){  
        win.show();  
        return false;  
    });  
  
  
        var chart1 = new FusionCharts("../../../resource/FusionCharts/Charts/FCF_MSColumn3D.swf", "chart1Id", "480", "380", "0", "1");       
        var xml="<graph xaxisname='机构' yaxisname='sales' hovercapbg='DEDEBE' hovercapborder='889E6D' rotateNames='0'" +  
                " yAxisMaxValue='100' numdivlines='9' divLineColor='CCCCCC' divLineAlpha='80' decimalPrecision='0' " +  
                "showAlternateHGridColor='1'font='Monaco' AlternateHGridAlpha='30' AlternateHGridColor='CCCCCC' caption='销售统计'" +  
                " subcaption='机构当月销售和上月销售对比' >" +  
                " <categories font='Monaco' fontSize='11' fontColor='000000'>" +  
                "   <category name='区域1'/>" +  
                "    <category name='区域2' />" +  
                "      <category name='区域3' />" +  
                "      <category name='区域4' />" +  
                "      <category name='区域5' />" +  
                "   </categories>" +  
                "   <dataset seriesname='四月' color='FDC12E'>" +  
                "     <set value='30' />" +  
                "     <set value='26' />" +  
                "      <set value='29' />" +  
                "      <set value='31' />" +  
                "      <set value='34' />" +  
                "  </dataset>" +  
                "    <dataset seriesname='五月' color='56B9F9'>" +  
                "     <set value='67' />" +  
                "      <set value='98' />" +  
                "      <set value='79' />" +  
                "      <set value='73' />" +  
                "      <set value='80' />" +  
                "   </dataset>" +  
                "</graph>";  
          
            chart1.setDataXML(xml);       
            chart1.render("chart1div");   
      
      
        var chart2 = new FusionCharts("../../../resource/FusionCharts/Charts/FCF_Column3D.swf", "chart2Id", "480", "380", "0", "1");         
        chart2.setDataXML("<graph xaxisname='月度' yaxisname='销售' subcaption='机构本季度销售统计'><set name='1月' value='10' color='D2626' /><set name='2月' value='10' color='D64646' /><set name='3月' value='11' color='AFD8F8' /></graph>");  
            chart2.render("chart2div");  
              
    var xml="<graph showNames='1'  decimalPrecision='0'  >" +  
            "<set name='机构2' value='20' />" +  
            "   <set name='机构3' value='7'  />" +  
            "    <set name='机构4' value='12' />" +  
            "    <set name='机构5' value='11' />" +  
            "   <set name='机构6' value='8' />" +  
            "    <set name='机构7' value='19'/>" +  
            "    <set name='机构8' value='15'/>" +  
            "</graph>";  
  
        var chart3 = new FusionCharts("../../../resource/FusionCharts/Charts/FCF_Pie3D.swf", "chart3Id", "480", "380", "0", "1");        
        chart3.setDataXML(xml);  
        chart3.render("chart3div");  
});   