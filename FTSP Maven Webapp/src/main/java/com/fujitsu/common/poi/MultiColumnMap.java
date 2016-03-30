package com.fujitsu.common.poi;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MultiColumnMap {
	private String key;
	private String columnName;
	private int occupyCellNumx = 1;
	private int occupyCellNumy = 1;
	private boolean comboBasis = false;
	private final int DEFAULT_WIDTH  = 8;
	private int width = DEFAULT_WIDTH;
	private List<Integer> retinue = new ArrayList();
	/**
	 * MultiColumnMap Constructor
	 * @param key 数据对应的Key
	 * @param columnName 显示的列名称
	 */
	public MultiColumnMap(String key, String columnName) {
		this.key = key;
		this.columnName = columnName;
		try {
			this.width = Math.max(DEFAULT_WIDTH, columnName.getBytes("GB2312").length);
		} catch (UnsupportedEncodingException e) {
			this.width = DEFAULT_WIDTH;
		}
	}

	/**
	 * MultiColumnMap Constructor
	 * @param key 数据对应的Key
	 * @param columnName 显示的列名称
	 * @param occupyCellNumx  所占列数，将用于合并
	 * @param occupyCellNumy  所占行数，将用于合并
	 */
	public MultiColumnMap(String key, String columnName, int occupyCellNumx,int occupyCellNumy) {
		this.key = key;
		this.columnName = columnName;
		this.occupyCellNumx = occupyCellNumx;
		this.occupyCellNumy = occupyCellNumy;
		try {
			this.width = Math.max(width, columnName.getBytes("GB2312").length);
		} catch (UnsupportedEncodingException e) {
			this.width = DEFAULT_WIDTH;
		}
	}
	
	/**
	 * MultiColumnMap Constructor
	 * @param key 数据对应的Key
	 * @param columnName 显示的列名称
	 * @param occupyCellNumx  所占列数，将用于合并
	 * @param occupyCellNumy  所占行数，将用于合并
	 * @param comboBasis  本列之前都将合并同类项
	 */
	public MultiColumnMap(String key, String columnName, int occupyCellNumx,int occupyCellNumy,boolean comboBasis) {
		this.key = key;
		this.columnName = columnName;
		this.occupyCellNumx = occupyCellNumx;
		this.occupyCellNumy = occupyCellNumy;
		this.comboBasis = comboBasis;
		try {
			this.width = Math.max(width, columnName.getBytes("GB2312").length);
		} catch (UnsupportedEncodingException e) {
			this.width = DEFAULT_WIDTH;
		}
	}
	
	/**
	 * MultiColumnMap Constructor
	 * @param key 数据对应的Key
	 * @param columnName 显示的列名称
	 * @param occupyCellNumx  所占列数，将用于合并
	 * @param occupyCellNumy  所占行数，将用于合并
	 * @param width 列宽，以一个等宽字体的英文字符宽度为1,
	 */
	public MultiColumnMap(String key, String columnName, int occupyCellNumx,int occupyCellNumy, int width) {
		this.key = key;
		this.columnName = columnName;
		this.occupyCellNumx = occupyCellNumx;
		this.occupyCellNumy = occupyCellNumy;
		this.width = width;
	}
	

	/**
	 * MultiColumnMap Constructor
	 * @param key 数据对应的Key
	 * @param columnName 显示的列名称
	 * @param occupyCellNumx  所占列数，将用于合并
	 * @param occupyCellNumy  所占行数，将用于合并
	 * @param width 列宽，以一个等宽字体的英文字符宽度为1,
	 * @param comboBasis  合并同类项
	 */
	public MultiColumnMap(String key, String columnName, int occupyCellNumx,int occupyCellNumy, int width,boolean comboBasis) {
		this.key = key;
		this.columnName = columnName;
		this.occupyCellNumx = occupyCellNumx;
		this.occupyCellNumy = occupyCellNumy;
		this.comboBasis = comboBasis;
		this.width = width;
	}

	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public boolean getComboBasis() {
		return comboBasis;
	}
	public void setComboBasis(boolean comboBasis) {
		this.comboBasis = comboBasis;
	}
	public int getOccupyCellNumx() {
		return occupyCellNumx;
	}
	public void setOccupyCellNumx(int occupyCellNumx) {
		this.occupyCellNumx = occupyCellNumx;
	}
	public int getOccupyCellNumy() {
		return occupyCellNumy;
	}
	public void setOccupyCellNumy(int occupyCellNumy) {
		this.occupyCellNumy = occupyCellNumy;
	}

	public List<Integer> getRetinue() {
		return retinue;
	}

	/**
	 * 如果设置了这个属性，则对应第index列的column会随本column一起被合并
	 * @param index
	 */
	public MultiColumnMap addRetinue(Integer index) {
		this.retinue.add(index);
		return this;
	}
}
