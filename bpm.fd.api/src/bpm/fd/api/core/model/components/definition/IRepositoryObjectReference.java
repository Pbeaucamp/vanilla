package bpm.fd.api.core.model.components.definition;

/**
 * An Interface used by IComponentDefinition using a DirectoryItem on a Vanilal Repository
 * @author ludo
 *
 */
public interface IRepositoryObjectReference {
	public int getDirectoryItemId();
	public void setDirectoryItemId(int directoryItemId);
	
	public int[] getObjectType();
	public int[] getObjectSubtypes();
}
