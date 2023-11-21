package bpm.vanilla.designer.ui.common;

/**
 * To implement by Activator that use complex models(more tahn one xml file)
 * to override the default checkin operation 
 * @author ludo
 *
 */
public interface ICheckoutReleaser {

	/**
	 * perform a checkin on the given model
	 * @param model
	 * @throws Exception
	 */
	public void checkin(Object model) throws Exception;
}
