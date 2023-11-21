package bpm.workflow.runtime.resources;

/**
 * Interface for a BI object
 * @author CHARBONNIER, MARTIN
 *
 */
public interface BiObject extends IResource {
	/**
	 * 
	 * @return the Resource associated
	 */
	public IResource getOrigin();
}
