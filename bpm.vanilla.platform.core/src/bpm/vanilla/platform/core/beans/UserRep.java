package bpm.vanilla.platform.core.beans;


public class UserRep {
	int id;
	int userId;
	int repositoryId;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setId(String id){
		try {
			this.id = Integer.parseInt(id);
		} catch (NumberFormatException e) {
			
			e.printStackTrace();
		}
	}
	
	public int getUserId() {
		return userId;
	}
	
	public void setUserId(int userId){
		this.userId = userId;
	}
	
	public void setUserId(String userId) {
		try{
			this.userId = Integer.parseInt(userId);
		}
		catch(NumberFormatException e){
			
		}
	}
	
	public int getRepositoryId() {
		return repositoryId;
	}
	
	public void setRepositoryId(int repositoryId){
		this.repositoryId = repositoryId;
	}
	
	public void setRepositoryId(String repositoryId) {
		try{
			this.repositoryId = Integer.parseInt(repositoryId);
		}		catch(NumberFormatException e){
			
		}
	}
	
	public String getXml() {
		StringBuffer buf = new StringBuffer();
		buf.append("    <userrep>\n");
		buf.append("        <id>" +  id + "</id>\n");
		buf.append("        <userId>" +  userId + "</userId>\n");
		buf.append("        <repositoryId>" + repositoryId +  "</repositoryId>\n");
		buf.append("    </userrep>\n");
		return buf.toString();
	}

}
