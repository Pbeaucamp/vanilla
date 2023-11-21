package bpm.aklabox.workflow.core.model.resources;

public interface IResource {

	public void setId(int id);
	
	public int getId();
	
	public void setName(String name);

	public String getName();

	public void setAddress(String address);

	public String getAddress();

	public void setUserName(String userName);

	public void setUserId(int userId);

	public int getUserId();

	public String getUserName();

	public void setPassword(String password);

	public String getPassword();

}
