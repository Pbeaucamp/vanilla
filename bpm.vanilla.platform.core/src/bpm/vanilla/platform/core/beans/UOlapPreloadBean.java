package bpm.vanilla.platform.core.beans;

public class UOlapPreloadBean {
	private long id;
	private int repositoryId;
	private int directoryItemId;
	private int vanillaGroupId;
	private String mdxQuery;
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @return the repositoryId
	 */
	public int getRepositoryId() {
		return repositoryId;
	}
	/**
	 * @param repositoryId the repositoryId to set
	 */
	public void setRepositoryId(int repositoryId) {
		this.repositoryId = repositoryId;
	}
	/**
	 * @return the directoryItemId
	 */
	public int getDirectoryItemId() {
		return directoryItemId;
	}
	/**
	 * @param directoryItemId the directoryItemId to set
	 */
	public void setDirectoryItemId(int directoryItemId) {
		this.directoryItemId = directoryItemId;
	}
	/**
	 * @return the vanillaGroupId
	 */
	public int getVanillaGroupId() {
		return vanillaGroupId;
	}
	/**
	 * @param vanillaGroupId the vanillaGroupId to set
	 */
	public void setVanillaGroupId(int vanillaGroupId) {
		this.vanillaGroupId = vanillaGroupId;
	}
	/**
	 * @return the mdxQuery
	 */
	public String getMdxQuery() {
		return mdxQuery;
	}
	/**
	 * @param mdxQuery the mdxQuery to set
	 */
	public void setMdxQuery(String mdxQuery) {
		this.mdxQuery = mdxQuery;
	}
	
	public String extractCubeName(){
		int i = getMdxQuery().toLowerCase().lastIndexOf("from " ) + 5;
		String cubeName = getMdxQuery().substring(i).trim().replace("[", "").replace("]", "");
		return cubeName;
	}
}
