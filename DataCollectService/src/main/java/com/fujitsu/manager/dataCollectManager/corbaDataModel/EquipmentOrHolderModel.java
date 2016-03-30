package com.fujitsu.manager.dataCollectManager.corbaDataModel;


/**
 * @author xuxiaojun
 *
 */
public class EquipmentOrHolderModel {

	private EquipmentHolderModel holder;
	private EquipmentModel equip;
	private int discriminator;
	private boolean uninitialized;
	
	public EquipmentModel getEquip() {
		return equip;
	}
	public void setEquip(EquipmentModel equip) {
		this.equip = equip;
	}
	public EquipmentHolderModel getHolder() {
		return holder;
	}
	public void setHolder(EquipmentHolderModel holder) {
		this.holder = holder;
	}
	public int getDiscriminator() {
		return discriminator;
	}
	public void setDiscriminator(int discriminator) {
		this.discriminator = discriminator;
	}
	public boolean isUninitialized() {
		return uninitialized;
	}
	public void setUninitialized(boolean uninitialized) {
		this.uninitialized = uninitialized;
	}
}
