package bpm.vanilla.platform.core.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PublicUrl implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public enum TypeURL {
		REPOSITORY_ITEM(0), 
		DOCUMENT_VERSION(1);
		
		private int type;

		private static Map<Integer, TypeURL> map = new HashMap<Integer, TypeURL>();
		static {
	        for (TypeURL type : TypeURL.values()) {
	            map.put(type.getType(), type);
	        }
	    }
		
		private TypeURL(int type) {
			this.type = type;
		}
		
		public int getType() {
			return type;
		}
		
		public static TypeURL valueOf(int type) {
	        return map.get(type);
	    }
	}

	private int id;
	private String publicKey;
	private int groupId;
	private int userId;
	private int itemId;
	private int repositoryId;
	private Date creationDate;
	private Date endDate;
	private int active;
	private int deleted;
	private String outputFormat = "html";
	private Integer datasourceId = -1;

	private TypeURL typeUrl;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setId(String id) {
		this.id = new Integer(id);
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = new Integer(groupId);
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setUserId(String userId) {
		this.userId = new Integer(userId);
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public void setActive(String active) {
		this.active = new Integer(active);
	}

	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	public void setDeleted(String deleted) {
		this.deleted = new Integer(deleted);
	}

	public String getOutputFormat() {
		return outputFormat;
	}

	public void setOutputFormat(String outputFormat) {
		this.outputFormat = outputFormat;
	}

	public Integer getDatasourceId() {
		return datasourceId;
	}

	public void setDatasourceId(Integer datasourceId) {
		this.datasourceId = datasourceId;
	}

	public void setDatasourceId(String datasourceId) {
		this.datasourceId = new Integer(datasourceId);
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(int repositoryId) {
		this.repositoryId = repositoryId;
	}

	public TypeURL getTypeUrl() {
		return typeUrl;
	}

	public void setTypeUrl(TypeURL typeUrl) {
		this.typeUrl = typeUrl;
	}
}
