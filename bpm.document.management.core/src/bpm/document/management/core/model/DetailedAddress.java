package bpm.document.management.core.model;

import java.io.Serializable;

public class DetailedAddress implements Serializable{

	private static final long serialVersionUID = 1L;

	private int daId=0;
	private String cabinetReference="";
	private int saId=0;
	
	private int levelCount=0;
	
	private StandardAddress sAddress = new StandardAddress();
	private Country country = new Country();
	private City city = new City();
	
	public int getDaId() {
		return daId;
	}
	public void setDaId(int daId) {
		this.daId = daId;
	}
	public String getCabinetReference() {
		return cabinetReference;
	}
	public void setCabinetReference(String cabinetReference) {
		this.cabinetReference = cabinetReference;
	}
	public int getSaId() {
		return saId;
	}
	public void setSaId(int saId) {
		this.saId = saId;
	}
	public StandardAddress getsAddress() {
		return sAddress;
	}
	public void setsAddress(StandardAddress sAddress) {
		this.sAddress = sAddress;
	}
	public Country getCountry() {
		return country;
	}
	public void setCountry(Country country) {
		this.country = country;
	}
	public City getCity() {
		return city;
	}
	public void setCity(City city) {
		this.city = city;
	}
	public int getLevelCount() {
		return levelCount;
	}
	public void setLevelCount(int levelCount) {
		this.levelCount = levelCount;
	}
}