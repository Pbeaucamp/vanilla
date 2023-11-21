package bpm.fd.design.ui.rcp;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

import bpm.fd.api.core.model.FdProjectDescriptor;
import bpm.fd.design.ui.Activator;

public class FdSourceProvider extends AbstractSourceProvider {
	public static final String CURRENT_MODEL_VERSION = "bpm.fd.design.ui.rcp.currentModelVersion";
	
	private static final String ENABLED = "enabled";
	private static final String DISABLED = "disabled";
	
	public FdSourceProvider() {
		
	}

	@Override
	public void dispose() {
		

	}

	@Override
	public Map getCurrentState() {
		Map currentState = new HashMap();
		if (Activator.getDefault().getProject() != null ){
			if (FdProjectDescriptor.API_DESIGN_VERSION.equals(Activator.getDefault().getProject().getProjectDescriptor().getInternalApiDesignVersion())){
				currentState.put(CURRENT_MODEL_VERSION, DISABLED);
			}
			else{
				currentState.put(CURRENT_MODEL_VERSION, ENABLED);
			}
		}
		else{
			currentState.put(CURRENT_MODEL_VERSION, DISABLED);
		}
		
		return currentState;
	}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] {CURRENT_MODEL_VERSION};
	}

	public void editorChanged() {
		String currentState = (String)getCurrentState().get(CURRENT_MODEL_VERSION);
		fireSourceChanged(ISources.WORKBENCH, CURRENT_MODEL_VERSION, currentState);
		
	}

}
