package bpm.gateway.core.transformations.outputs;

import org.dom4j.Element;

import bpm.gateway.core.Transformation;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.outputs.RunInfobright;
import bpm.vanilla.platform.core.IRepositoryContext;

public class InfoBrightInjector extends DataBaseOutputStream{

	/* (non-Javadoc)
	 * @see bpm.gateway.core.transformations.outputs.DataBaseOutputStream#getElement()
	 */
	@Override
	public Element getElement() {
		
		Element e =  super.getElement();
		e.setName("infobrightInjector");
		return e;
	}

	/* (non-Javadoc)
	 * @see bpm.gateway.core.transformations.outputs.DataBaseOutputStream#getExecutioner(bpm.gateway.runtime.RuntimeEngine)
	 */
//	@Override
//	public TransformationRuntime getExecutioner(RuntimeEngine runtimeEngine) {
//		return new InfoBrightInjectorRuntime(this, runtimeEngine);
//	}
	

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunInfobright(this, bufferSize);
	}
	@Override
	public boolean addInput(Transformation stream) throws Exception {
		if (getInputs().size() > 0 && !getInputs().contains(stream)){
			throw new Exception("Only one input allowed on InfoBrightInjector steps");
		}
		return super.addInput(stream);
	}
}
