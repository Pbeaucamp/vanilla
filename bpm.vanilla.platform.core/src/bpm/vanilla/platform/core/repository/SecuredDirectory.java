package bpm.vanilla.platform.core.repository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table (name = "sec_directory_secured")
public class SecuredDirectory {
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private Integer id;
	
	@Column(name = "directory_id")
	private Integer directoryId;
	
	@Column(name = "group_id")
	private Integer groupId;
	
	@Column(name = "grant_type")
	private Character grant;
	
	public SecuredDirectory(){}

	public SecuredDirectory(Integer id, Integer directoryId, Integer groupId, Character grant) {
		super();
		this.id = id;
		this.directoryId = directoryId;
		this.groupId = groupId;
		this.grant = grant;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public void setId(String id) {
		this.id = new Integer(id);
	}
	public Integer getDirectoryId() {
		return directoryId;
	}

	public void setDirectoryId(Integer directoryId) {
		this.directoryId = directoryId;
	}
	public void setDirectoryId(String directoryId) {
		this.directoryId = new Integer(directoryId);
	}
	
	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = new Integer(groupId);
	}
	public Character getGrant() {
		return grant;
	}

	public void setGrant(Character grant) {
		this.grant = grant;
	}
	public void setGrant(String grant) {
		this.grant = grant.charAt(0);
	}
	
//	public String getXml() {
//		StringBuffer buf = new StringBuffer();
//		buf.append("<secureddirectory>\n");
//		if (id != null)
//			buf.append("<id>" + this.id + "</id>");
//		buf.append("<directoryid>" + this.directoryId + "</directoryid>");
//		buf.append("<groupid>" + this.groupId + "</groupid>");
//		buf.append("<grant>" + this.grant + "</grant>");
//		
//		buf.append("</secureddirectory>\n");
//		return buf.toString();
//	}
	

}
