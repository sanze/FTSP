package com.fujitsu.model;

public class ProcessModel{
	/*public enum STATUS{
		INIT("初始化"),
		RUNNING("正在执行"),
		PAUSING("正在暂停"),
		PAUSED("已暂停"),
		RESUMING("正在继续"),
		STOPING("正在停止"),
		STOPED("已停止"),
		INTERRUPT("异常中止"),
		COMPLETED("已完成");

		public String statusText;
		private STATUS(String statusText){
			this.statusText=statusText;
		}
	}*/
	private String text;
	private double processPercent;
	private boolean isCanceled;
	private String additionalText;
	private Integer cur;
	private Integer total;
	
	public ProcessModel(Integer cur,Integer total,String additionalText,boolean isCanceled) {
		this.total = total;
		this.additionalText = additionalText;
		this.isCanceled = isCanceled;
		setCur(cur);
	}
	
	public double getProcessPercent() {
		
		java.math.BigDecimal bg = new java.math.BigDecimal(processPercent);
        double value = bg.setScale(2, java.math.BigDecimal.ROUND_HALF_UP).doubleValue();
		return value;
	}
	public void setCur(Integer cur) {
		this.cur = cur;
		if(cur!=null&&total!=null){
			this.text="当前进度："+cur+"/"+total;
			this.processPercent=(cur>=total?1:cur*1.0/total);
		}else{
			this.text="当前进度："+"初始化...";
			this.processPercent=0;
		}
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public boolean isCanceled() {
		return isCanceled;
	}
	public void setCanceled(boolean isCanceled) {
		this.isCanceled = isCanceled;
		if(isCanceled)
			this.text="当前进度："+"正在取消操作...";
	}
	public void respCancel() {
		this.text="当前进度："+"操作已取消...";
		this.processPercent=1;
	}

	public String getText() {
		return text+(additionalText==null?"":"<br>"+additionalText);
	}
	
	public String getAdditionalText() {
		return additionalText;
	}

	public void setAdditionalText(String additionalText) {
		this.additionalText = additionalText;
	}
}
