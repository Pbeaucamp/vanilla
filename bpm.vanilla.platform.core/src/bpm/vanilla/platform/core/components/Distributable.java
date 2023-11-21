package bpm.vanilla.platform.core.components;

/**
 * Special Interface that should be implemented by all 
 * ComponentRemote and VanillaComponents that may not be in the same 
 * Applications than the VanilleRUntime 
 * @author ludo
 *
 */
public interface Distributable extends IVanillaComponent{
	
	/**
	 * only the name of the servlet
	 * when you want to call this servlet, the URL MUST BE 
	 * IVanillaComponentIdentifier.getUrl() + IVanillaComponentIdentifier.getId() +LOAD_EVALUATOR_SERVLET
	 */
	public static final String LOAD_EVALUATOR_SERVLET = "/loadEvaluator";
	/**
	 * compute a factor the Load Evaluation for this component. The result will
	 * be used to determine the component that is the less used to pick one 
	 * when there are more than on registered in the VanillaPlatform.
	 * 
	 * @return
	 * @throws Exception 
	 */
	public int computeLoadEvaluation() throws Exception;
	

}
