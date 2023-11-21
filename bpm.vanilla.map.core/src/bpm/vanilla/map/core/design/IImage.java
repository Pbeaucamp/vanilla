package bpm.vanilla.map.core.design;

public interface IImage {
	
	/**
	 * 
	 * @return the IImage Id
	 */
	public Integer getId();
	
	/**
	 * 
	 * @return the IImage Item Id
	 */
	public Integer getImageItemId();
	
	/**
	 * 
	 * @return the IImage Repository ID
	 */
	public Integer getImageRepositoryId();

	public void setId(Integer imageId);

	public void setImageItemId(Integer y);

	public void setImageRepositoryId(Integer repositoryId);
}
