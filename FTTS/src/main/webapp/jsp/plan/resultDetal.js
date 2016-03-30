
	var APointX1 = new Ext.Toolbar.TextItem('A 距离: - Km  ');
    var APointY1 = new Ext.Toolbar.TextItem('A db: - db');
	var APointX2 = new Ext.Toolbar.TextItem('B 距离: - Km  ');
    var APointY2 = new Ext.Toolbar.TextItem('B db: - db');   
	var APointX = new Ext.Toolbar.TextItem('距离差值: - Km  ');
    var APointY = new Ext.Toolbar.TextItem('Y差值: - db');   
    
Ext.onReady(function(){

var abpanel = new Ext.Panel({
	id:"abPanel",
	width: 300,
 	renderTo: 'chartPanel',
	stripeRows:true,
	autoScroll:false,
	frame:false,
	region:"center",
    items: [{
		layout:'column',
		border:false,
		items: [{
		columnWidth:.20,
		layout: 'form',
		border:false,
		items: [APointX1]
		},{
			columnWidth:.20,
			layout: 'form',
			border:false,
			items: [APointX2]
		},{
			columnWidth:.60,
			layout: 'form',
			border:false,
			items: [APointX]
		}]
    },{
		layout:'column',
		items: [{
		columnWidth:.20,
		layout: 'form',
		border:false,
		items: [APointY1]
		},{
			columnWidth:.20,
			layout: 'form',
			border:false,
			items: [APointY2]
		},{
			columnWidth:.60,
			layout: 'form',
			border:false,
			items: [APointY]
		}]
    }]
}); 
var win = new Ext.Viewport({
    id:'win',
	layout : 'border',
	items : [abpanel]
});

	win.show();

});

		

  

  

		
  


  

  