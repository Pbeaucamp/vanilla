package bpm.vanilla.workplace.core;

import java.util.Date;

public interface IUser {
	
	public int getId();
	public String getName();
	public String getPassword();
	public String getMail();
	public Boolean getIsAdmin();
	public Date getCreationDate();
}
