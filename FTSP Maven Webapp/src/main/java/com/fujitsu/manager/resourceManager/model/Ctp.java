package com.fujitsu.manager.resourceManager.model;

public class Ctp {
	//默认的ctp值转化类型0：通用转化模式，1：华为转化模式
	private final static int DEFAULT_TYPE = 0;
	public Ctp(String ctpDes){
		this(ctpDes,DEFAULT_TYPE);
	}
	
	public Ctp(String ctpDes,int type){
		String[] str = ctpDes.split("M");
		for(int i = 0 ;i < str.length;i++){
			if(i == 0){
				this.desc = 2 + "M";
			}else{
				int num = Integer.parseInt(str[i]);
				if(type == DEFAULT_TYPE ){
					setCtp(num);
				}else{
					setCtpChange(num);
				}
			}
		}
	}
	private String desc;
	private int k;
	private int l;
	private int m;

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	public int getL() {
		return l;
	}

	public void setL(int l) {
		this.l = l;
	}

	public int getM() {
		return m;
	}

	public void setM(int m) {
		this.m = m;
	}
	
	private void setCtp(int num){
		num--;
		int m1 = num/3;
		int m2 = num%3;
		setM(m2+1);
		int l1 = m1/7;
		int l2 = m1%7;
		setL(l2+1);
		int k1 = l1/3;
		int k2 = l1%3;
		setK(k2+1);
	}
	
	//华为的ctp转化方式
	private void setCtpChange(int num){
		num--;
		int k1 = num/3;
		int k2 = num%3;
		setK(k2+1);
		int l1 = k1/7;
		int l2 = k1%7;
		setL(l2+1);
		int m1 = l1/3;
		int m2 = l1%3;
		setM(m2+1);
	}
}
