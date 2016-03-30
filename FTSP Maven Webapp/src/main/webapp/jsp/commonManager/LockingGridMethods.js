
Ext.override(Ext.grid.GridPanel, {
	getState : function() {
		var o = {
			columns : []
		}, store = this.store, ss, gs;

		for ( var i = 0, c; (c = this.colModel.config[i]); i++) {
			o.columns[i] = {
				id : c.id,
				width : c.width
			};
			if (c.hidden) {
				o.columns[i].hidden = true;
			}
			if (c.sortable) {
				o.columns[i].sortable = true;
			}
			if(c.locked){
				o.columns[i].locked = true;
			}
			if(c.dataIndex){
				o.columns[i].dataIndex = c.dataIndex;
			}
			if(c.header){
				o.columns[i].header = c.header;
			}
		}
		if (store) {
			ss = store.getSortState();
			if (ss) {
				o.sort = ss;
			}
			if (store.getGroupState) {
				gs = store.getGroupState();
				if (gs) {
					o.group = gs;
				}
			}
		}
		return o;
	},
	applyState : function(state){
//		console.log("grid默认的恢复state方法调用！");
        var cm = this.colModel,
            cs = state.columns,
            store = this.store,
            s,
            c,
            colIndex;

        if(cs){
            for(var i = 0, len = cs.length; i < len; i++){
                s = cs[i];
                c = cm.getColumnById(s.id);
                if(c){
                    colIndex = cm.getIndexById(s.id);
                    cm.setState(colIndex, {
                        hidden: s.hidden,
                        width: s.width,
                        sortable: s.sortable,
                        locked: s.locked
                    });
                    if(colIndex != i){
                        cm.moveColumn(colIndex, i);
                    }
                }
            }
        }
        if(store){
            s = state.sort;
            if(s){
                store[store.remoteSort ? 'setDefaultSort' : 'sort'](s.field, s.direction);
            }
            s = state.group;
            if(store.groupBy){
                if(s){
                    store.groupBy(s);
                }else{
                    store.clearGrouping();
                }
            }

        }
        var o = Ext.apply({}, state);
        delete o.columns;
        delete o.sort;
        Ext.grid.GridPanel.superclass.applyState.call(this, o);
    }
});

