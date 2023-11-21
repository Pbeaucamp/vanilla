package bpm.document.management.core.model;

import java.io.Serializable;

public class CountryCities implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int CC_ID=0;
	private int countryId=0;
	private int cityId=0;
	
	public int getCC_ID() {
		return CC_ID;
	}
	public void setCC_ID(int cC_ID) {
		CC_ID = cC_ID;
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

}
