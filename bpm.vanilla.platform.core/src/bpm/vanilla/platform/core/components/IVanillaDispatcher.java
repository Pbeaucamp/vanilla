package bpm.vanilla.platform.core.components;


/**
 * 
 * @author ludo
 *
 */
public interface IVanillaDispatcher {
	public Object run(IRuntimeConfig runtimeConfig) throws Exception;
	
	public IRunIdentifier runAsynch(IRuntimeConfig runtimeConfig) throws Exception;
	
	public Object getRunResultFrom(IRunIdentifier identifier) throws Exception;
}
