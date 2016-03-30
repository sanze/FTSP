package com.fujitsu.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListResult extends Result {
	private int total;
	private List<Map> rows = new ArrayList<Map>();
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public List<Map> getRows() {
		return rows;
	}
	public void setRows(List<Map> rows) {
		this.rows = rows;
	}
}
