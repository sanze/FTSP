package com.fujitsu.manager.equipmentTestManager.serviceImpl.model;

/**
 * @Description：RTU光路
 * @author cao senrong
 * @date 2014-5-26
 * @version V1.0
 */
public class LightPathModel {

	public String Nop;//光路号  4字符
	public String Nops;//光路段号  2字符
	public String Rip;//光路段RTU IP  10字符
	public String Rpt;//光路段RTU 端口  10字符
	public String Tu;//光路段OSW子架号  2字符  固定1
	public String Sl;//光路段OSW槽道号  2字符
	public String P;//光路段OSW端口号  2字符
	public String getNop() {
		return Nop;
	}
	public void setNop(String nop) {
		Nop = nop;
	}
	public String getNops() {
		return Nops;
	}
	public void setNops(String nops) {
		Nops = nops;
	}
	public String getRip() {
		return Rip;
	}
	public void setRip(String rip) {
		Rip = rip;
	}
	public String getRpt() {
		return Rpt;
	}
	public void setRpt(String rpt) {
		Rpt = rpt;
	}
	public String getTu() {
		return Tu;
	}
	public void setTu(String tu) {
		Tu = tu;
	}
	public String getSl() {
		return Sl;
	}
	public void setSl(String sl) {
		Sl = sl;
	}
	public String getP() {
		return P;
	}
	public void setP(String p) {
		P = p;
	}
	

}
