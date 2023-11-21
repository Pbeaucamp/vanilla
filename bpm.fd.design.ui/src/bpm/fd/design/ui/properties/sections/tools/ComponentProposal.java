package bpm.fd.design.ui.properties.sections.tools;

import org.eclipse.jface.fieldassist.IContentProposal;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;

public class ComponentProposal implements IContentProposal{
	private static final String contentBase = "document.getElementById('"; //$NON-NLS-1$
	private static final String descBase = "Find the HTML element mathcing to the Component "; //$NON-NLS-1$
	private static final String labelBase = "find component ";  //$NON-NLS-1$
	private String label;
	private String content;
	private String description;
	
	
	public ComponentProposal(IComponentDefinition def){
		this.content = contentBase + def.getId() + "')"; //$NON-NLS-1$
		this.description = descBase + def.getName();
		this.label = labelBase + def.getName();
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
