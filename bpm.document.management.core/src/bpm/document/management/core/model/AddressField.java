package bpm.document.management.core.model;

import java.io.Serializable;

public class AddressField implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private int id;
	private String lineOne;
	private String lineTwo;
	private String lineThree;
	private String postalCode;
	private String city;
	private String countryCode ="FR"; 
	private String country = "France";
	private String region;
	
	public AddressField() {
	}

	public AddressField(String lineOne, String lineTwo, String lineThree, String postalCode, String city, String countryCode, String country, String region) {
		super();
		this.lineOne = lineOne;
		this.lineTwo = lineTwo;
		this.lineThree = lineThree;
		this.postalCode = postalCode;
		this.city = city;
		this.countryCode = countryCode;
		this.country = country;
		this.region = region;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLineOne() {
		return lineOne;
	}

	public void setLineOne(String lineOne) {
		this.lineOne = lineOne;
	}

	public String getLineTwo() {
		return lineTwo;
	}

	public void setLineTwo(String lineTwo) {
		this.lineTwo = lineTwo;
	}

	public String getLineThree() {
		return lineThree;
	}

	public void setLineThree(String lineThree) {
		this.lineThree = lineThree;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}
	
	
}
