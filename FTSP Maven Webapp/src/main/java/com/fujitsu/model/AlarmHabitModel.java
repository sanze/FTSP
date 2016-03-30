package com.fujitsu.model;
import java.io.Serializable;
import java.util.List;
/**
 * 告警习惯设置实体类
 * wuchao
 * 2014.01
 */
public class AlarmHabitModel implements Serializable{
	private List<String> datas;//所有告警级别的设置数据
	public List<String> getDatas() {
		return datas;
	}
	public void setDatas(List<String> datas) {
		this.datas = datas;
	}
}
