package bpm.gateway.core;

/**
 * This transfo act as a flag to say if the Transformation can generate Trash Stream
 * @author LCA
 *
 */
public interface Trashable {

	/**
	 * 
	 * @return the Transformation that will act as a Backup
	 */
	public Transformation getTrashTransformation(); 
	
	public void setTrashTransformation(Transformation transfo); 
	
//	public String getTrashLabel();
}
