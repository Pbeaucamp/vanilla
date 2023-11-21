package bpm.vanilla.platform.core.beans;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UserGroup {
	private Integer id;
	private Integer userId;
	private Integer groupId;
	private Date creation = Calendar.getInstance().getTime();
	private String comment;
	
	public UserGroup(){}

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

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	public void setUserId(String userId){
		try {
			this.userId = Integer.parseInt(userId);
		} catch (NumberFormatException e) {
			
			e.printStackTrace();
		}
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}
	
	public void setGroupId(String groupId){
		try {
			this.groupId = Integer.parseInt(groupId);
		} catch (NumberFormatException e) {
			
			e.printStackTrace();
		}
	}

	public Date getCreation() {
		return creation;
	}

	public void setCreation(Date creation) {
		this.creation = creation;
	}
	
	public void setCreation(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			this.creation = sdf.parse(date);
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	
	public String getXml() {
		StringBuffer buf = new StringBuffer();
		buf.append("    <usergroup>\n");
		if (id != null)
			buf.append("        <id>" +  id + "</id>\n");
		buf.append("        <userId>" + userId +  "</userId>\n");
		buf.append("        <groupId>" +  groupId + "</groupId>\n");
//		buf.append("        <creation>" + creation +  "</creation>\n");
		buf.append("        <comment>" + comment +  "</comment>\n");
		buf.append("    </usergroup>\n");
		return buf.toString();
	}
	
}
