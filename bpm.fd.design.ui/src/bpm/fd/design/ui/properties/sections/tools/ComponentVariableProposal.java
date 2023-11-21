package bpm.fd.design.ui.properties.sections.tools;

import org.eclipse.jface.fieldassist.IContentProposal;

import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.OutputParameter;

public class ComponentVariableProposal implements IContentProposal{
	private static final String contentBase = "parameters['"; //$NON-NLS-1$
	private static final String descBase = "Internal variable "; //$NON-NLS-1$
		
	
	private static final String contentOutBase = "parametersOut['"; //$NON-NLS-1$
	private static final String descOutBase = "The output value of a Component "; //$NON-NLS-1$
	private static final String labelOutBase = "Component's Output value  "; //$NON-NLS-1$
	private String label;
	private String content;
	private String description;
	
	
	public ComponentVariableProposal(ComponentParameter p){
		if (p instanceof OutputParameter){
			this.content = contentOutBase + p.getName().replace(" ", "_") + "']"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			this.description = descOutBase + p.getName();
			this.label = labelOutBase + p.getName().replace(" ", "_") + "']"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		else{
			this.content = contentBase + p.getName().replace(" ", "_") + "']"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			this.description = descBase + p.getName();
			this.label = description;
		}
		
	}
	
	public String getContent() {
		
		return content;
	}

	public int getCursorPosition() {
		return content.length();
	}

	public String getDescription() {
		return description;
	}

	public String getLabel() {
		return label;
	}

}
