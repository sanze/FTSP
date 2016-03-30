/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
/**
 * @class Ext.grid.RevSelectionModel
 * @extends Ext.grid.RowSelectionModel
 * A custom selection model that renders a column of checkboxes that can be toggled to select or deselect rows.
 * @constructor
 * @param {Object} config The configuration options
 */
Ext.grid.RevSelectionModel = Ext.extend(Ext.grid.RowSelectionModel, {

    /**
     * @cfg {Boolean} checkOnly <tt>true</tt> if rows can only be selected by clicking on the
     * checkbox column (defaults to <tt>false</tt>).
     */
    /**
     * @cfg {String} header Any valid text or HTML fragment to display in the header cell for the
     * checkbox column.  Defaults to:<pre><code>
     * '&lt;div class="x-grid3-hd-checker">&#38;#160;&lt;/div>'</tt>
     * </code></pre>
     * The default CSS class of <tt>'x-grid3-hd-checker'</tt> displays a checkbox in the header
     * and provides support for automatic check all/none behavior on header click. This string
     * can be replaced by any valid HTML fragment, including a simple text string (e.g.,
     * <tt>'Select Rows'</tt>), but the automatic check all/none behavior will only work if the
     * <tt>'x-grid3-hd-checker'</tt> class is supplied.
     */
    header : '<div class="x-grid3-hd-checker">&#160;</div>',
    /**
     * @cfg {Number} width The default width in pixels of the checkbox column (defaults to <tt>20</tt>).
     */
    width : 20,
    /**
     * @cfg {Boolean} sortable <tt>true</tt> if the checkbox column is sortable (defaults to
     * <tt>false</tt>).
     */
    sortable : false,

    // private
    menuDisabled : true,
    fixed : true,
    hideable: false,
    dataIndex : '',
    id : 'checker',
    isColumn: true, // So that ColumnModel doesn't feed this through the Column constructor
    selectionState : 0,
    constructor : function(){
        Ext.grid.RevSelectionModel.superclass.constructor.apply(this, arguments);
        if(this.checkOnly){
            this.handleMouseDown = Ext.emptyFn;
        }
    },

    // private
    initEvents : function(){
        Ext.grid.RevSelectionModel.superclass.initEvents.call(this);
        this.grid.on('render', function(){
            Ext.fly(this.grid.getView().innerHd).on('mousedown', this.onHdMouseDown, this);
        }, this);
    },

    /**
     * @private
     * Process and refire events routed from the GridView's processEvent method.
     */
    processEvent : function(name, e, grid, rowIndex, colIndex){
        if (name == 'mousedown') {
            this.onMouseDown(e, e.getTarget());
            return false;
        } else {
            return Ext.grid.Column.prototype.processEvent.apply(this, arguments);
        }
    },

    // private
    onMouseDown : function(e, t){
        if(e.button === 0 && t.className == 'x-grid3-row-checker'){ // Only fire if left-click
            e.stopEvent();
            var row = e.getTarget('.x-grid3-row');
            if(row){
                var index = row.rowIndex;
                if(this.isSelected(index)){
                    this.deselectRow(index);
                }else{
                    this.selectRow(index, true);
                    this.grid.getView().focusRow(index);
                }
            }
        }
    },
    /**
     * 返回选择状态
     * 0 : 未选中 
     * 1 : 部分选中
     * 2 : 全选
     **/
    getSelectionState : function(){
		console.log("getSelectionState = " + this.selections.items.length);
        if(this.selections.items.length == this.grid.store.getCount()){
            return 2;
        }else if(this.selections.items.length > 0){
            return 1;
        }else{
            return 0;
        }
    },
    // private
    onHdMouseDown : function(e, t) {
    	console.log("onHdMouseDown");
        if(t.className == 'x-grid3-hd-checker'){
            e.stopEvent();
            var hd = Ext.fly(t.parentNode);
            console.log("this.getSelectionState() = " + this.getSelectionState());
            var isChecked = (this.getSelectionState()==2);
            console.log("isChecked = " + isChecked);
            var isPartChecked = (this.getSelectionState()==1);
            console.log("isPartChecked = " + isPartChecked);
            
            if(isChecked){
                hd.removeClass('x-grid3-hd-checker-on');
                hd.removeClass('x-grid3-hd-checker-part');
                this.clearSelections();
            }else if(isPartChecked){
                hd.addClass('x-grid3-hd-checker-part');
                this.reverseSelect();
            }else{
                hd.addClass('x-grid3-hd-checker-on');
                this.selectAll();
            }
        }
    },
    reverseSelect : function(){
        // this.selections.contains()
        if(this.isLocked()){
            return;
        }
        // this.selections.clear();
        console.log("reverseSelect");
        for(var i = 0, len = this.grid.store.getCount(); i < len; i++){
            var r = this.grid.store.getAt(i);
            var selected = this.selections.contains(r);
            console.log(String.format("ID[{0}] = {1} \tSelState = [{2}]", i+1, r.get("id"), selected?"██":"　"));
            if(selected){
                //console.log("contains");
                this.deselectRow(i);
            }else{
                //console.log("not contains");
                this.selectRow(i, true);
            }
        }
    },
    // private
    renderer : function(v, p, record){
        return '<div class="x-grid3-row-checker">&#160;</div>';
    },
    
    onEditorSelect: function(row, lastRow){
        if(lastRow != row && !this.checkOnly){
            this.selectRow(row); // *** highlight newly-selected cell and update selection
        }
    }
});