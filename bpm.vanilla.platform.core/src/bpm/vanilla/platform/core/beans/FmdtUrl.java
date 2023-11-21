package bpm.vanilla.platform.core.beans;

//import org.dom4j.DocumentHelper;
//import org.dom4j.Element;

public class FmdtUrl {
	private int id;
	private int itemId;
	private Integer repositoryId;
	
	private String groupName;
	private String modelName;
	private String packageName;
	private String description;
	private String user;
	private String password;
	private String name;
	
	public FmdtUrl() {
		super();
	}

	public int getId() {
		return id;
	}

	/**
	 * @return the repositoryId
	 */
	public Integer getRepositoryId() {
		return repositoryId;
	}

	/**
	 * @param repositoryId the repositoryId to set
	 */
	public void setRepositoryId(Integer repositoryId) {
		this.repositoryId = repositoryId;
	}
	
	public void setRepositoryId(String repositoryId) {
		try{
			this.repositoryId = Integer.parseInt(repositoryId);
		}catch(NumberFormatException ex){
			
		}
	}

	public void setId(int id) {
		this.id = id;
	}
	public void setId(String id) {
		this.id = new Integer(id);
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = new Integer(itemId);
	}
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
//	public Element getElement() {
//		Element root = DocumentHelper.createElement("fmdturl");
//		root.addElement("id").setText(id+"");
//		root.addElement("itemid").setText(itemId+"");
//		root.addElement("groupname").setText(groupName);
//		root.addElement("modelname").setText(modelName);
//		
//		if (packageName != null){
//			root.addElement("packagename").setText(packageName);
//		}
//			
//		
//		if (description != null){
//			root.addElement("description").setText(description);
//		}
//			
//		
//		if (user != null){
//			root.addElement("user").setText(user);
//		}
//			
//		
//		if (password != null){
//			root.addElement("password").setText(password);
//		}
//			
//		if (repositoryId != null){
//			root.addElement("repositoryid").setText(repositoryId + "");
//		}
//		
//		root.addElement("name").setText(name);
//		
//		
//		return root;
//	}
//	
//	public String getXml() {
//		return getElement().asXML();
//	}
//	
	

}
