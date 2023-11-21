package bpm.document.management.core.model;

import java.io.Serializable;

public class Currency implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int currencyId=0;
	private String currencyName="";
	private String currencyAbbr="";
	private String currencyFlag="";
	private float equivalent=0;
	private float vatPercentage=0;
	
	public int getCurrencyId() {
		return currencyId;
	}
	public void setCurrencyId(int currencyId) {
		this.currencyId = currencyId;
	}
	public String getCurrencyName() {
		return currencyName;
	}
	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}
	public String getCurrencyAbbr() {
		return currencyAbbr;
	}
	public void setCurrencyAbbr(String currencyAbbr) {
		this.currencyAbbr = currencyAbbr;
	}
	public String getCurrencyFlag() {
		return currencyFlag;
	}
	public void setCurrencyFlag(String currencyFlag) {
		this.currencyFlag = currencyFlag;
	}
	public float getEquivalent() {
		return equivalent;
	}
	public void setEquivalent(float equivalent) {
		this.equivalent = equivalent;
	}
	public float getVatPercentage() {
		return vatPercentage;
	}
	public void setVatPercentage(float vatPercentage) {
		this.vatPercentage = vatPercentage;
	}
}
