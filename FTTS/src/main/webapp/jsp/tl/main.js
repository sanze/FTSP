/**
 * 
 * @param {Object} args
 * 		@param {Object} args.STATION_ID
 * 		@param {Object} args.A_END_ID
 * 		@param {Object} args.Z_END_ID
 * 		@param {Object} args.CONN_TYPE
 */
function createLink(args){
	//连接类型枚举
	var conTypes = ["-","OTDR → OSW",
	                "RTU的OSW → 光缆",
	                "光缆 → CTU的OSW",
	                "CTU的OSW → 光缆",
	                "光缆 → 光缆",
	                "OSW → OSW"]
	//debug信息格式化
    var msg = String.format("创建Link StationID = {0}\n\t<{1}> → <{2}>\n\t连接类型：{3}",
    		args.STATION_ID,
    		args.A_END_ID,
    		args.Z_END_ID,
    		conTypes[args.CONN_TYPE]);
//	console.log(msg);
	Ext.Ajax.request({
		url : "external-connect!addExternalConnect.action",
		type : 'post',
		params : {
			stationId:args.STATION_ID,
			aEndId:args.A_END_ID,
			zEndId:args.Z_END_ID,
			connType:args.CONN_TYPE
		},
		success : //creatLinkCallback.createCallback(args),
			function(response, options){
			var obj = Ext.decode(response.responseText);
			if(!!obj.returnResult){
				delete(args.rawLink)
				LM.addLink(args);
			}else{
//				console.log(args);
				LM.removeLink(args.rawLink);
				Ext.Msg.alert("提示：", obj.returnMessage);
			}
			LM.generateLinks();
		},
		failure:function(){
			Ext.Msg.alert("提示：", "后台请求失败！");
		},
		error:function(){
			Ext.Msg.alert("提示：", "后台运行出错！");
		}
	});
}

/**
 * 
 * @param {Object} args
 * 		@param {Object} args.STATION_ID
 * 		@param {Object} args.A_END_ID
 * 		@param {Object} args.Z_END_ID
 * 		@param {Object} args.CONN_TYPE
 */
function deleteLink(args){
	//连接类型枚举
	var conTypes = ["-","OTDR → OSW",
	                "RTU的OSW → 光缆",
	                "光缆 → CTU的OSW",
	                "CTU的OSW → 光缆",
	                "光缆 → 光缆",
	                "OSW → OSW"]
	//debug信息格式化
    var msg = String.format("删除Link StationID = {0}\n\t<{1}> → <{2}>\n\t连接类型：{3}",
    		args.STATION_ID,
    		args.A_END_ID,
    		args.Z_END_ID,
    		conTypes[args.CONN_TYPE]);
//	console.log(msg);
	Ext.getBody().mask();
	Ext.Ajax.request({
		url : "external-connect!delExternalConnect.action",
		type : 'post',
		params : {
			stationId:args.STATION_ID,
			aEndId:args.A_END_ID,
			zEndId:args.Z_END_ID,
			connType:args.CONN_TYPE
		},
		success : function(response, options){
			Ext.getBody().unmask();
			var obj = Ext.decode(response.responseText);
			TL.selectedLink = null;
//			console.log(obj.returnResult);
			if(!!obj.returnResult){
				args.rawLink.start.linkText = "";
				args.rawLink.end.linkText = "";
				LM.removeLink(args.rawLink);
			}else{
//				console.log(args);
				Ext.Msg.alert("提示：", "后台运行出错！");
			}
			LM.generateLinks();
		},
		failure:function(){
			Ext.getBody().unmask();
			Ext.Msg.alert("提示：", "后台运行出错！");
		},
		error:function(){
			Ext.getBody().unmask();
			Ext.Msg.alert("提示：", "后台运行出错！");
		}
	});
}
/**
 * 屏蔽部分事件
 */
function disableSelect(){
	document.body.ondragstart
		= document.body.onselectstart
		= document.body.onbeforecopy
		= function(){
			return false;
		};
	document.body.oncontextmenu = function(){
		if(!!TL.startNode){
			TL.startNode = null;
			TL.possiblePortType = null;
			return false;
		}
	};
}
Ext.onReady(function() {
//    console.log("Ext.onReady");
	disableSelect();
	Ext.Ajax.timeout = 90000000;
    TL.initDom("scene");
    TL.initData(gStationId);
    LM.init(gStationId);
    TL.render();
  //添加 事件监听测试
    TL.on("create_link", createLink);
    TL.on("delete_link", deleteLink);
});