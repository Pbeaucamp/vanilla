package bpm.vanilla.designer.ui.common;

/**
 * This interface allow to register some IRepositoryContextChangedListener on an object
 * 
 * @author ludo
 *
 */
public interface IRepositoryContextMonitorable {
	public void addRepositoryContextChangedListener(IRepositoryContextChangedListener listener);
	public void removeRepositoryContextChangedListener(IRepositoryContextChangedListener listener);

}
