package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.List;

public class EmailInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public enum TypeEmail {
		INTERNAL,
		SHARE_PUBLIC_LINK
	}
	
	private TypeEmail type;
	
	private User owner;
	private User user;
	
	//We use one item or a list of items
	private IObject item;
	private List<Documents> items;
	private String password;
	
	//Used to pass the hash for a public share for example
	private String urlParam;
	
	public EmailInfo() {
	}	
	
	public EmailInfo(TypeEmail type, User user, User owner, List<Documents> items) {
		this.type = type;
		this.user = user;
		this.owner = owner;
		this.items = items;
	}
	
	public EmailInfo(TypeEmail type, User user, User owner, IObject item, String password, String publicUrlParam) {
		this.type = type;
		this.user = user;
		this.owner = owner;
		this.item = item;
		this.password = password;
		this.urlParam = publicUrlParam;
	}

	public TypeEmail getType() {
		return type;
	}

	public IObject getItem() {
		return item;
	}

	public String getPassword() {
		return password;
	}
	
	public User getUser() {
		return user;
	}
	
	public User getOwner() {
		return owner;
	}
	
	public List<Documents> getItems() {
		return items;
	}
	
	public String getUrlParam() {
		return urlParam;
	}
}
