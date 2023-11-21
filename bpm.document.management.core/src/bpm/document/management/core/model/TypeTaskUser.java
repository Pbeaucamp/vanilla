package bpm.document.management.core.model;

import java.io.Serializable;

public class TypeTaskUser implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	private int typeTaskId;
	private int userId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTypeTaskId() {
		return typeTaskId;
	}

	public void setTypeTaskId(int typeTaskId) {
		this.typeTaskId = typeTaskId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
}
