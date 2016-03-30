package com.fujitsu.common.poi;

import java.util.Map;

@SuppressWarnings("rawtypes")
public class SettingParser {
	private Map info;

	/**
	 * 报表设置解析器
	 * 
	 * @param info
	 *            前台传过去的设置
	 */
	public SettingParser(Map info) {
		this.info = info;
	}

	/**
	 * 获取SDH设置的TP等级
	 * 
	 * @return
	 */
	public String getSdhTp() {
		String rlt = "";
		String names[] = { "STM1", "STM4", "STM16", "STM64", "STM256" };
		String lvls[] = { "STM-1", "STM-4", "STM-16", "STM-64", "STM-256" };
		int flag = 0;
		String origData = info.get("sdhTp").toString();
		boolean isOther = "1".equals(info.get("sdhTpOther").toString());
		// 标记勾选的内容
		for (int i = 0; i < 5; i++) {
			if (origData.indexOf(lvls[i]) >= 0) {
				flag |= (1 << i);
			}
		}
		// 如果勾选了其他，必须反选！
		if (isOther) {
			flag = ~flag;
		}
		// 查看实际勾选的内容
		for (int i = 0; i < 5; i++) {
			if ((flag & (1 << i)) > 0) {
				rlt += names[i] + "、";
			}
		}
		// 按需补上其他
		if (isOther) {
			rlt += "其他、";
		}
		// 去掉最后的顿号
		if (rlt.length() > 0) {
			rlt = rlt.substring(0, rlt.length() - 1);
		}
		return rlt;
	}

	/**
	 * 获取SDH设置的物理量 其他选项忽略
	 * 
	 * @return
	 */
	public String getSdhPhysics() {
		String rlt = "";
		String origData = info.get("sdhPm").toString();
		System.out.println(origData);
		// 判断收发光功率
		if (origData.indexOf("TPL_CUR") >= 0) {
			rlt += "发光功率、";
		}
		if (origData.indexOf("RPL_CUR") >= 0) {
			rlt += "收光功率、";
		}
		// 去掉最后的顿号
		if (rlt.length() > 0) {
			rlt = rlt.substring(0, rlt.length() - 1);
		}
		return rlt;
	}

	/**
	 * 获取SDH设置的计数值 其他选项忽略
	 * 
	 * @return
	 */
	public String getSdhCounter() {
		String rlt = "";
		String origData = info.get("sdhPm").toString();
		System.out.println(origData);
		// 判断收发光功率
		if (origData.indexOf("RS_BBE") >= 0) {
			rlt += "再生段误码(B1)、";
		}
		if (origData.indexOf("MS_BBE") >= 0) {
			rlt += "复用段误码(B2)、";
		}
		if (origData.indexOf("VC4_BBE") >= 0) {
			rlt += "VC4通道误码(B3)、";
		}
		if (origData.indexOf("VC3_BBE") >= 0) {
			rlt += "VC3/VC12通道误码(B3/V5)、";
		}
		// 去掉最后的顿号
		if (rlt.length() > 0) {
			rlt = rlt.substring(0, rlt.length() - 1);
		}
		return rlt;
	}

	/**
	 * 获取SDH设置-是否显示最大值最小值
	 * 
	 * @return
	 */
	public String getSdhMaxMin() {
		String origData = info.get("sdhPm").toString();
		if (origData.indexOf("TPL_MAX") >= 0
				|| origData.indexOf("RPL_MAX") >= 0) {
			return "显示";
		}
		return "不显示";
	}

	public String getWdmTp() {
		String rlt = "";
		String names[] = { "光传送和复用单元", "光通道", "光监控通道" };
		String lvls[] = { "OTS&OMS", "OCH", "OSC", };
		int flag = 0;
		String origData = info.get("wdmTp").toString();
		boolean isOther = "1".equals(info.get("wdmTpOther").toString());
		// 标记勾选的内容
		for (int i = 0; i < 3; i++) {
			if (origData.indexOf(lvls[i]) >= 0) {
				flag |= (1 << i);
			}
		}
		// 如果勾选了其他，必须反选！
		if (isOther) {
			flag = ~flag;
		}
		// 查看实际勾选的内容
		for (int i = 0; i < 3; i++) {
			if ((flag & (1 << i)) > 0) {
				rlt += names[i] + "、";
			}
		}
		// 按需补上其他
		if (isOther) {
			rlt += "其他、";
		}
		// 去掉最后的顿号
		if (rlt.length() > 0) {
			rlt = rlt.substring(0, rlt.length() - 1);
		}
		return rlt;
	}

	public String getWdmPhysics() {
		String rlt = "";
		String origData = info.get("wdmPm").toString();
		System.out.println(origData);
		// 判断收发光功率
		if (origData.indexOf("TPL_CUR") >= 0) {
			rlt += "发光功率、";
		}
		if (origData.indexOf("RPL_CUR") >= 0) {
			rlt += "收光功率、";
		}
		if (origData.indexOf("PCLSSNR_CUR") >= 0) {
			rlt += "信道信噪比、";
		}
		if (origData.indexOf("PCLSWL_CUR") >= 0) {
			rlt += "信道中心波长/偏移、";
		}
		if (origData.indexOf("PCLSOP_CUR") >= 0) {
			rlt += "信道光功率、";
		}
		// 去掉最后的顿号
		if (rlt.length() > 0) {
			rlt = rlt.substring(0, rlt.length() - 1);
		}
		return rlt;
	}

	public String getWdmCounter() {
		String rlt = "";
		String origData = info.get("wdmPm").toString();
		System.out.println(origData);
		// 判断收发光功率
		if (origData.indexOf("OSC_BBE") >= 0) {
			rlt += "光监控信道误码、";
		}
		if (origData.indexOf("FEC_BEF_COR_ER") >= 0) {
			rlt += "FEC误码率、";
		}
		if (origData.indexOf("OTU_BBE") >= 0) {
			rlt += "OTU误码、";
		}
		if (origData.indexOf("ODU_BBE") >= 0) {
			rlt += "ODU误码、";
		}
		// 去掉最后的顿号
		if (rlt.length() > 0) {
			rlt = rlt.substring(0, rlt.length() - 1);
		}
		return rlt;
	}

	public String getWdmMaxMin() {
		String origData = info.get("wdmPm").toString();
		if (origData.indexOf("TPL_MAX") >= 0
				|| origData.indexOf("RPL_MAX") >= 0
				|| origData.indexOf("PCLSSNR_MAX") >= 0
				|| origData.indexOf("PCLSWL_MAX") >= 0
				|| origData.indexOf("PCLSOP_MAX") >= 0) {
			return "显示";
		}
		return "不显示";
	}
}
