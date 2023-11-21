package bpm.document.management.core.model;

import java.io.Serializable;

public class StandardAddress implements Serializable{

	private static final long serialVersionUID = 1L;

	private int saId=0;
	private String saName="";
	private String saComment="";
	private int countryId=0;
	private int cityId=0;
	
	private City city= new City();
	private Country country = new Country();
	
	public int getSaId() {
		return saId;
	}
	public void setSaId(int saId) {
		this.saId = saId;
	}
	public int getCountryId() {
		return countryId;
	}
	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}
	public int getCityId() {
		return cityId;
	}
	public void setCityId(int cityId) {
		this.cityId = cityId;
	}
	public String getSaName() {
		return saName;
	}
	public void setSaName(String saName) {
		this.saName = saName;
	}
	public City getCity() {
		return city;
	}
	public void setCity(City city) {
		this.city = city;
	}
	public Country getCountry() {
		return country;
	}
	public void setCountry(Country country) {
		this.country = country;
	}
	public String getSaComment() {
		if(saComment == null)
			return "";
		else
			return saComment;
	}
	public void setSaComment(String saComment) {
		this.saComment = saComment;
	}	
}