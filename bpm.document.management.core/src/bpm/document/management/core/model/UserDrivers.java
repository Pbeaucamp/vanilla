package bpm.document.management.core.model;

import java.io.Serializable;

public class UserDrivers implements Serializable {

	private static final long serialVersionUID = 1L;

	private int driverId = 0;
	private int userId = 0;
	private String driverType = "";
	private String driverUserName = "";
	private String driverPassword = "";

	public int getDriverId() {
		return driverId;
	}

	public void setDriverId(int driverId) {
		this.driverId = driverId;
	}

	public String getDriverType() {
		return driverType;
	}

	public void setDriverType(String driverType) {
		this.driverType = driverType;
	}

	public String getDriverUserName() {
		return driverUserName;
	}

	public void setDriverUserName(String driverUserName) {
		this.driverUserName = driverUserName;
	}

	public String getDriverPassword() {
		return driverPassword;
	}

	public void setDriverPassword(String driverPassword) {
		this.driverPassword = driverPassword;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}
