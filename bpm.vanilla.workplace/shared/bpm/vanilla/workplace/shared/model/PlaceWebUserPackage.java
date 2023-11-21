package bpm.vanilla.workplace.shared.model;

import com.google.gwt.user.client.rpc.IsSerializable;


public class PlaceWebUserPackage implements IsSerializable {

	private int id;
	private int userId;
	private int packageId;

	public PlaceWebUserPackage() { }

	public PlaceWebUserPackage(int userId, int packageId) {
		this.userId = userId;
		this.packageId = packageId;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getPackageId() {
		return packageId;
	}

	public void setPackageId(int packageId) {
		this.packageId = packageId;
	}
}
