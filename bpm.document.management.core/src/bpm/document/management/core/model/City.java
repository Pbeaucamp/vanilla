package bpm.document.management.core.model;

import java.io.Serializable;

public class City implements Serializable {

	private static final long serialVersionUID = 1L;

	private int cityId = 0;
	private String cityName = "";
	private String cityCode;
	private Integer depId;

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public Integer getDepId() {
		return depId;
	}

	public void setDepId(Integer depId) {
		this.depId = depId;
	}

	@Override
	public String toString() {
		return cityName;
	}
}
