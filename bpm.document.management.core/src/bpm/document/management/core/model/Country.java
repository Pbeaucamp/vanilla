package bpm.document.management.core.model;

import java.io.Serializable;

public class Country implements Serializable{

	private static final long serialVersionUID = 1L;

	private int countryId=0;
	private String countryName="";
	private String continent="";
	private String flags="";
	
	public int getCountryId() {
		return countryId;
	}
	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	public String getContinent() {
		return continent;
	}
	public void setContinent(String continent) {
		this.continent = continent;
	}
	public String getFlags() {
		return flags;
	}
	public void setFlags(String flags) {
		this.flags = flags;
	}
	
	@Override
	public String toString() {
		return countryName;
	}
}
