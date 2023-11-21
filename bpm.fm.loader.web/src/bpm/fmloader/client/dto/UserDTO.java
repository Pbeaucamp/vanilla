package bpm.fmloader.client.dto;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UserDTO implements IsSerializable {

	private String username;
	private String password;
	private int id;
	private boolean isEncrypted;
	private boolean isCommentValidator;
	
	public UserDTO() {
		super();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setEncrypted(boolean isEncrypted) {
		this.isEncrypted = isEncrypted;
	}

	public boolean isEncrypted() {
		return isEncrypted;
	}

	public void setCommentValidator(boolean isCommentValidator) {
		this.isCommentValidator = isCommentValidator;
	}

	public boolean isCommentValidator() {
		return isCommentValidator;
	}
	
	
	
}
