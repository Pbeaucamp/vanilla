package bpm.vanilla.platform.core.beans;


public class RoleGroup {

	private Integer id;
	private int groupId;
	private int roleId;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public void setId(String id){
		try {
			this.id = Integer.parseInt(id);
		} catch (NumberFormatException e) {
			
			e.printStackTrace();
		}
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public void setGroupId(String groupId){
		try {
			this.groupId = Integer.parseInt(groupId);
		} catch (NumberFormatException e) {
			
			e.printStackTrace();
		}
	}
	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	public void setRoleId(String roleId){
		try {
			this.roleId = Integer.parseInt(roleId);
		} catch (NumberFormatException e) {
			
			e.printStackTrace();
		}
	}
	
	public String getXml() {
		StringBuffer buf = new StringBuffer();
		buf.append("    <rolegroup>\n");
		
		if (id != null)
			buf.append("        <id>" +  id + "</id>\n");
		buf.append("        <groupId>" +  groupId + "</groupId>\n");
		buf.append("        <roleId>" + roleId +  "</roleId>\n");
		buf.append("    </rolegroup>\n");
		return buf.toString();
	}
	
}
