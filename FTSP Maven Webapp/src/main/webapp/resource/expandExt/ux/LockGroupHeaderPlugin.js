/*
 * LockedGroupHeaderGrid version 1.0
 */
Ext.namespace("Ext.ux.plugins");

Ext.ux.plugins.LockedGroupHeaderGrid = function(config) {
	this.config = config;
};

Ext.extend(Ext.ux.plugins.LockedGroupHeaderGrid, Ext.util.Observable, {
	init: function(grid) {
		Ext.applyIf(grid.colModel, this.config);
		Ext.apply(grid.getView(), this.viewConfig);
	},

	viewConfig: {
		initTemplates: function() {
			this.constructor.prototype.initTemplates.apply(this, arguments);
	    	
	        var ts = this.templates || {};

			if (!ts.gcell) {
				ts.gcell = new Ext.XTemplate(
					'<td class="x-grid3-hd {cls} x-grid3-td-{id} ux-grid-hd-group-row-{row}" style="{style}">',
					'<div {tooltip} class="x-grid3-hd-inner x-grid3-hd-{id}" unselectable="on" style="{istyle}">',
					'<tpl if="values.btn"><a class="x-grid3-hd-btn" href="#"></a></tpl>',
					'{value}</div>',
					'</td>'
				);
			}
			this.templates = ts;
			this.hrowRe = new RegExp("ux-grid-hd-group-row-(\\d+)", "");
		},

		renderHeaders: function() {
			var ts = this.templates, headers = [[],[]], cm = this.cm, rows = cm.rows, tstyle = 'width:' + this.getTotalWidth() + ';',tw = this.cm.getTotalWidth(), lw = this.cm.getTotalLockedWidth();
			for (var row = 0, rlen = rows.length; row < rlen; row++) {
				var r = rows[row], cells = [[],[]];
				for (var i = 0, gcol = 0, len = r.length; i < len; i++) {
					var group = r[i];
					group.colspan = group.colspan || 1;
					var l = cm.isLocked(gcol)?1:0;
					var id = this.getColumnId(group.dataIndex ? cm.findColumnIndex(group.dataIndex) : gcol);
					var gs = Ext.ux.plugins.LockedGroupHeaderGrid.prototype.getGroupStyle.call(this, group, gcol);
					cells[l][i] = ts.gcell.apply({
						cls: group.header ? 'ux-grid-hd-group-cell' : 'ux-grid-hd-nogroup-cell',
						id: id,
						row: row,
						style: 'width:' + gs.width + ';' + (gs.hidden ? 'display:none;' : '') + (group.align ? 'text-align:' + group.align + ';' : ''),
						tooltip: group.tooltip ? (Ext.QuickTips.isEnabled() ? 'ext:qtip' : 'title') + '="' + group.tooltip + '"' : '',
						istyle: group.align == 'right' ? 'padding-right:16px' : '',
						btn: this.grid.enableHdMenu && group.header,
						value: group.header || '&nbsp;'
					});
					gcol += group.colspan;
				}
				headers[0][row] = ts.header.apply({
					tstyle: 'width:' + (tw - lw) + 'px;',
					cells: cells[0].join('')
				});
				headers[1][row] = ts.header.apply({
					tstyle: 'width:' + lw + 'px;',
					cells: cells[1].join('')
				});
				
			}
			//headers.push(this.constructor.prototype.renderHeaders.apply(this, arguments));
			//var h = this.constructor.prototype.renderHeaders.apply(this, arguments);
			var h = this.constructor.prototype.renderHeaders.call(this);
			headers[0][headers[0].length] = h[0];
			headers[1][headers[1].length] = h[1];
			return [headers[0].join(''),headers[1].join('')];
		},
		onColumnWidthUpdated : function(){
			this.constructor.prototype.onColumnWidthUpdated.apply(this, arguments);
			Ext.ux.plugins.LockedGroupHeaderGrid.prototype.updateGroupStyles.call(this);
		},

		onAllColumnWidthsUpdated : function(){
			this.constructor.prototype.onAllColumnWidthsUpdated.apply(this, arguments);
			Ext.ux.plugins.LockedGroupHeaderGrid.prototype.updateGroupStyles.call(this);
		},

		onColumnHiddenUpdated : function(){
			this.constructor.prototype.onColumnHiddenUpdated.apply(this, arguments);
			Ext.ux.plugins.LockedGroupHeaderGrid.prototype.updateGroupStyles.call(this);
		},

		getHeaderCell : function(index){
			//return this.mainHd.query(this.cellSelector)[index];
			var locked = this.cm.getLockedCount();
			if(index < locked)
			{
				return this.lockedHd.query(this.cellSelector)[index];
			} 
			else 
			{
				return this.mainHd.query(this.cellSelector)[(index-locked)];
			}
		},
		
		findHeaderCell : function(el){
			return el ? this.fly(el).findParent('td.x-grid3-hd', this.cellSelectorDepth) : false;
		},

		findHeaderIndex : function(el){
			var cell = this.findHeaderCell(el);
			return cell ? this.getCellIndex(cell) : false;
		},

		updateSortIcon : function(col, dir){
			var sc = this.sortClasses;
			var clen = this.cm.getColumnCount();
			var lclen = this.cm.getLockedCount();
			var hds = this.mainHd.select(this.cellSelector).removeClass(sc);
			var lhds = this.lockedHd.select(this.cellSelector).removeClass(sc);
			if(lclen > 0 && col < lclen)
				lhds.item(col).addClass(sc[dir == "DESC" ? 1 : 0]);
			else
				hds.item(col-lclen).addClass(sc[dir == "DESC" ? 1 : 0]);
		},
		/*handleHdDown: function(e, t){
			Ext.ux.grid.LockingGridView.superclass.handleHdDown.call(this, e, t);
			var el = Ext.get(t);
			if(el.hasClass('ux-grid-hd-group-cell') || Ext.fly(t).up('.ux-grid-hd-group-cell')){
				var hd = this.findHeaderCell(t),
                index = this.getCellIndex(hd),
                ms = this.hmenu.items, cm = this.cm;
                ms.get('asc').setDisabled(true);
                ms.get('desc').setDisabled(true);
                if(this.grid.enableColLock !== false){
	                ms.get('lock').setDisabled(cm.isLocked(index));
	                ms.get('unlock').setDisabled(!cm.isLocked(index));
                }
            }else if(Ext.fly(t).hasClass('x-grid3-hd-btn')){
    			if(this.grid.enableColLock !== false){
	                var hd = this.findHeaderCell(t),
	                    index = this.getCellIndex(hd),
	                    ms = this.hmenu.items, cm = this.cm;
	                //ms.get('lock').setDisabled(true);
	                //ms.get('unlock').setDisabled(true);
    	        }
            } 
        },*/

        handleHdMove: function(e, t){
            var hd = this.findHeaderCell(this.activeHdRef);
            if(hd && !this.headersDisabled && !Ext.fly(hd).hasClass('ux-grid-hd-group-cell')){
                var hw = this.splitHandleWidth || 5, r = this.activeHdRegion, x = e.getPageX(), ss = hd.style, cur = '';
                if(this.grid.enableColumnResize !== false){
                    if(x - r.left <= hw && this.cm.isResizable(this.activeHdIndex - 1)){
                        cur = Ext.isAir ? 'move' : Ext.isWebKit ? 'e-resize' : 'col-resize'; // col-resize
                                                                                                // not
                                                                                                // always
                                                                                                // supported
                    }else if(r.right - x <= (!this.activeHdBtn ? hw : 2) && this.cm.isResizable(this.activeHdIndex)){
                        cur = Ext.isAir ? 'move' : Ext.isWebKit ? 'w-resize' : 'col-resize';
                    }
                }
                ss.cursor = cur;
            }
        },

        handleHdOver: function(e, t){
            var hd = this.findHeaderCell(t);
            if(hd && !this.headersDisabled){
                this.activeHdRef = t;
                this.activeHdIndex = this.getCellIndex(hd);
                var fly = this.fly(hd);
                this.activeHdRegion = fly.getRegion();
                if(!(this.cm.isMenuDisabled(this.activeHdIndex) || fly.hasClass('ux-grid-hd-group-cell'))){
                    fly.addClass('x-grid3-hd-over');
                    this.activeHdBtn = fly.child('.x-grid3-hd-btn');
                    if(this.activeHdBtn){
                        this.activeHdBtn.dom.style.height = (hd.firstChild.offsetHeight - 1) + 'px';
                    }
                }
            }
        },
		handleHdMenuClick : function(item){
			var index = this.hdCtxIndex,
				cm = this.cm,
				id = item.getItemId(),
				llen = cm.getLockedCount();
			switch(id){
				case 'lock':
				case 'unlock':
					if(id==='lock'&&cm.getColumnCount(true) <= llen + 1){
						this.onDenyColumnLock();
						return undefined;
					}
					var rows = cm.rows,groupIndex=this.getColumnGroupRow(rows,index);
					var row=0,oldIndex=groupIndex[row].first,newIndex=llen,
					colspan = groupIndex[row].colspan;
					var d={
						oldIndex:oldIndex,
						newIndex:newIndex,
						row:row,
						colspan:colspan
					};
					var right = d.oldIndex < d.newIndex;
					this.moveHeaderRows(oldIndex,newIndex,row,rows);
					for (var c = 0; c < d.colspan; c++) {
						var oldIx = d.oldIndex + (right ? 0 : c), newIx = d.newIndex + (right ? -1 : c);
						cm.setLocked(oldIx, id==='lock', newIx != oldIx);
						if(oldIx != newIx){
							cm.moveColumn(oldIx, newIx);
							this.grid.fireEvent("columnmove", oldIx, newIx);
						}
					}
				break;
				default:
					return Ext.ux.grid.LockingGridView.superclass.handleHdMenuClick.call(this, item);
			}
			return true;
		},
		getColumnGroupRow : function(rows,col){
			var groupRow=[];
			for (var row = 0, rlen = rows.length; row < rlen; row++) {
				var r = rows[row], len = r.length, fromIx = 0, span = 1, toIx = len;
				for (var i = 0, gcol = 0; i < len; i++) {
					var group = r[i];
					if (col >= gcol && col < gcol + group.colspan) {
						groupRow[row]={first:gcol,colspan:group.colspan};
					}
					gcol += group.colspan;
				}
			}
			return groupRow;
		},
		moveHeaderRows : function(oldIndex,newIndex,rowIx,rows){
		    var colspan = Ext.ux.plugins.LockedGroupHeaderGrid.prototype.getGroupSpan.call(this, rowIx, oldIndex);
			var right = oldIndex < newIndex;
			for (var row = rowIx, rlen = rows.length; row < rlen; row++) {
				var r = rows[row], len = r.length, fromIx = 0, span = 1, toIx = len;
				for (var i = 0, gcol = 0; i < len; i++) {
					var group = r[i];
					if (oldIndex >= gcol && oldIndex < gcol + group.colspan) {
						fromIx = i;
					}
					if (oldIndex + colspan - 1 >= gcol && oldIndex + colspan - 1 < gcol + group.colspan) {
						span = i - fromIx + 1;
					}
					if (newIndex >= gcol && newIndex < gcol + group.colspan) {
						toIx = i;
					}
					gcol += group.colspan;
				}
				var groups = r.splice(fromIx, span);
				rows[row] = r.splice(0, toIx - (right ? span : 0)).concat(groups).concat(r);
			}
		},
		beforeColMenuShow : function(){
			var cm = this.cm, rows = this.cm.rows;
			this.colMenu.removeAll();
			for(var col = 0, clen = cm.getColumnCount(); col < clen; col++){
				var menu = this.colMenu, text = cm.getColumnHeader(col);
				if(cm.config[col].fixed !== true && cm.config[col].hideable !== false){
					for (var row = 0, rlen = rows.length; row < rlen; row++) {
						var r = rows[row], group, gcol = 0;
						for (var i = 0, len = r.length; i < len; i++) {
							group = r[i];
							if (col >= gcol && col < gcol + group.colspan) {
								break;
							}
							gcol += group.colspan;
						}
						if (group && group.header) {
							if (cm.hierarchicalColMenu) {
								var gid = 'group-' + row + '-' + gcol;
								var item = menu.items.item(gid);
								var submenu = item ? item.menu : null;
								if (!submenu) {
									submenu = new Ext.menu.Menu({id: gid});
									submenu.on("itemclick", this.handleHdMenuClick, this);
									var checked = false, disabled = true;
									for(var c = gcol, lc = gcol + group.colspan; c < lc; c++){
										if(!cm.isHidden(c)){
											checked = true;
										}
										if(cm.config[c].hideable !== false){
											disabled = false;
										}
									}
									menu.add({
										id: gid,
										text: group.header,
										menu: submenu,
										hideOnClick:false,
										checked: checked,
										disabled: disabled
									});
								}
								menu = submenu;
							} else {
								text = group.header + ' ' + text;
							}
						}
					}
					menu.add(new Ext.menu.CheckItem({
						id: "col-"+cm.getColumnId(col),
						text: text,
						checked: !cm.isHidden(col),
						hideOnClick:false,
						disabled: cm.config[col].hideable === false
					}));
				}
			}
		},

		afterRenderUI : function(){
			this.constructor.prototype.afterRenderUI.apply(this, arguments);
			Ext.apply(this.columnDrop, Ext.ux.plugins.LockedGroupHeaderGrid.prototype.columnDropConfig);
		}
	},

	columnDropConfig : {
		getTargetFromEvent : function(e){
			var t = Ext.lib.Event.getTarget(e);
			return this.view.findHeaderCell(t);
		},

		positionIndicator : function(h, n, e){
			var data = Ext.ux.plugins.LockedGroupHeaderGrid.prototype.getDragDropData.call(this, h, n, e);
			if (data === false) {
				return false;
			}
			var px = data.px + this.proxyOffsets[0];
			this.proxyTop.setLeftTop(px, data.r.top + this.proxyOffsets[1]);
			this.proxyTop.show();
			this.proxyBottom.setLeftTop(px, data.r.bottom);
			this.proxyBottom.show();
			return data.pt;
		},

		onNodeDrop : function(n, dd, e, data){
			var h = data.header;
			if(h != n){
				var d = Ext.ux.plugins.LockedGroupHeaderGrid.prototype.getDragDropData.call(this, h, n, e);
				if (d === false) {
					return false;
				}
				var cm = this.grid.colModel, right = d.oldIndex < d.newIndex, rows = cm.rows;
				for (var row = d.row, rlen = rows.length; row < rlen; row++) {
					var r = rows[row], len = r.length, fromIx = 0, span = 1, toIx = len;
					for (var i = 0, gcol = 0; i < len; i++) {
						var group = r[i];
						if (d.oldIndex >= gcol && d.oldIndex < gcol + group.colspan) {
							fromIx = i;
						}
						if (d.oldIndex + d.colspan - 1 >= gcol && d.oldIndex + d.colspan - 1 < gcol + group.colspan) {
							span = i - fromIx + 1;
						}
						if (d.newIndex >= gcol && d.newIndex < gcol + group.colspan) {
							toIx = i;
						}
						gcol += group.colspan;
					}
					var groups = r.splice(fromIx, span);
					rows[row] = r.splice(0, toIx - (right ? span : 0)).concat(groups).concat(r);
				}
				for (var c = 0; c < d.colspan; c++) {
					var oldIx = d.oldIndex + (right ? 0 : c), newIx = d.newIndex + (right ? -1 : c);
					cm.moveColumn(oldIx, newIx);
					this.grid.fireEvent("columnmove", oldIx, newIx);
				}
				return true;
			}
			return false;
		}
	},

	getGroupStyle: function(group, gcol) {
		var width = 0, hidden = true;
		for (var i = gcol, len = gcol + group.colspan; i < len; i++) {
			if (!this.cm.isHidden(i)) {
				var cw = this.cm.getColumnWidth(i);
				if(typeof cw == 'number'){
					width += cw;
				}
				hidden = false;
			}
		}
		return {
			width: (Ext.isBorderBox ? width : Math.max(width - this.borderWidth, 0)) + 'px',
			hidden: hidden
		};
	},

	updateGroupStyles: function(col) {
		var tables = [this.mainHd.query('.x-grid3-header-offset > table'),this.lockedHd.query('.x-grid3-header-offset > table')], tw = this.getTotalWidth(), lw = this.cm.getTotalLockedWidth(), rows = this.cm.rows;
		for (var row = 0; row < tables[0].length; row++) {
			tables[0][row].style.width = tw;
			tables[1][row].style.width = lw + 'px';
			if (row < rows.length) {
				var cells = [], c = [tables[1][row].firstChild.firstChild.childNodes, tables[0][row].firstChild.firstChild.childNodes];
				for (l = 0; l < 2; l++) {
					for (j = 0; j < c[l].length; j++) {
						cells.push(c[l][j]);
					}
				}
				for (var i = 0, gcol = 0; i < cells.length; i++) {
					var group = rows[row][i];
					if ((typeof col != 'number') || (col >= gcol && col < gcol + group.colspan)) {
						var gs = Ext.ux.plugins.LockedGroupHeaderGrid.prototype.getGroupStyle.call(this, group, gcol);
						cells[i].style.width = gs.width;
						cells[i].style.display = gs.hidden ? 'none' : '';
					}
					gcol += group.colspan;
				}
			}
		}
	},

	getGroupRowIndex : function(el){
		if(el){
			var m = el.className.match(this.hrowRe);
			if(m && m[1]){
				return parseInt(m[1], 10);
			}
		}
		return this.cm.rows.length;
	},

	getGroupSpan : function(row, col) {
		if (row < 0) {
			return {col: 0, colspan: this.cm.getColumnCount()};
		}
		var r = this.cm.rows[row];
		if (r) {
			for(var i = 0, gcol = 0, len = r.length; i < len; i++) {
				var group = r[i];
				if (col >= gcol && col < gcol + group.colspan) {
					return {col: gcol, colspan: group.colspan};
				}
				gcol += group.colspan;
			}
			return {col: gcol, colspan: 0};
		}
		return {col: col, colspan: 1};
	},

	getDragDropData : function(h, n, e){
		if (h.parentNode != n.parentNode &&
		    (this.grid.enableColLock&&(
		    	(h.parentNode==this.view.mainHd.child('.x-grid3-hd-row').dom&&n.parentNode==this.view.lockedHd.child('.x-grid3-hd-row').dom)||
			    (h.parentNode==this.view.lockedHd.child('.x-grid3-hd-row').dom&&n.parentNode==this.view.mainHd.child('.x-grid3-hd-row').dom)))) {
			return false;
		}
		var cm = this.grid.colModel;
		var x = Ext.lib.Event.getPageX(e);
		var r = Ext.lib.Dom.getRegion(n.firstChild);
		var px, pt;
		if((r.right - x) <= (r.right-r.left)/2){
			px = r.right+this.view.borderWidth;
			pt = "after";
		}else{
			px = r.left;
			pt = "before";
		}
		var oldIndex = this.view.getCellIndex(h);
		var newIndex = this.view.getCellIndex(n);
		if(cm.isFixed(newIndex)){
			return false;
		}
		var row = Ext.ux.plugins.LockedGroupHeaderGrid.prototype.getGroupRowIndex.call(this.view, h);
		var oldGroup = Ext.ux.plugins.LockedGroupHeaderGrid.prototype.getGroupSpan.call(this.view, row, oldIndex);
		var newGroup = Ext.ux.plugins.LockedGroupHeaderGrid.prototype.getGroupSpan.call(this.view, row, newIndex);
		oldIndex = oldGroup.col;
		newIndex = newGroup.col + (pt == "after" ? newGroup.colspan : 0);
		if(newIndex >= oldGroup.col && newIndex <= oldGroup.col + oldGroup.colspan){
			return false;
		}
		var parentGroup = Ext.ux.plugins.LockedGroupHeaderGrid.prototype.getGroupSpan.call(this.view, row - 1, oldIndex);
		if (newIndex < parentGroup.col || newIndex > parentGroup.col + parentGroup.colspan) {
			return false;
		}
		return {
			r: r,
			px: px,
			pt: pt,
			row: row,
			oldIndex: oldIndex,
			newIndex: newIndex,
			colspan: oldGroup.colspan
		};
	}

});