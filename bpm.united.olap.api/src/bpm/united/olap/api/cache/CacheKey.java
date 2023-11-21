package bpm.united.olap.api.cache;

import java.io.Serializable;

/**
 * represent a Key for the cacheDisk.
 * 
 * All parts of the key are md5 encrypted
 * @author ludo
 *
 */
public class CacheKey implements Serializable{
	public static final String Unkown = "Unkown";
	public static final String separator = "-";
	private String schemaId;
	private String groupId;
	private String queryId;
	private String repositoryId;
	private String directoryItemId;

	
	public CacheKey(String fullKey) throws Exception{
		String[] parts = fullKey.split(separator);
		if (parts.length != 5){
			throw new Exception("The cacheKey String representation " + fullKey + " is invalid");
		}
		for(int i = 0; i < 5 ; i++){
			if (i > 1 && parts[i].length() != 32){
				throw new Exception("The cacheKey String representation " + fullKey + " is invalid");
			}
		}
		this.repositoryId = parts[0];
		this.directoryItemId = parts[1];
		this.schemaId = parts[2];
		this.groupId = parts[3];
		this.queryId = parts[4];
		
		
	}
	
	public CacheKey(String repositoryId, String directoryItemId, String schemaId, String groupId, String queryId){
		this.repositoryId = repositoryId;
		this.directoryItemId = directoryItemId;
		this.schemaId = schemaId;
		this.groupId = groupId;
		this.queryId = queryId;
	}

	
	/**
	 * @return the repositoryId
	 */
	public String getRepositoryId() {
		return repositoryId;
	}

	/**
	 * @return the directoryItemId
	 */
	public String getDirectoryItemId() {
		return directoryItemId;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(getRepositoryId());
		b.append(separator);
		b.append(getDirectoryItemId());
		b.append(separator);
		
		b.append(getSchemaId());
		b.append(separator);
		b.append(getGroupId());
		b.append(separator);
		b.append(getQueryId());
		return b.toString();
	}
	/**
	 * @return the schemaId
	 */
	public String getSchemaId() {
		return schemaId;
	}

	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * @return the queryId
	 */
	public String getQueryId() {
		return queryId;
	}
	
	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof CacheKey && arg0 != null){
			return this.toString().equals(arg0.toString());
		}
		return false;
	}
	
}
