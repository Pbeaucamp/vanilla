package bpm.fd.design.ui.properties.sections.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;

public class ComponentProposalProvider implements IContentProposalProvider{
	private IContentProposal[] proposals;
	
	
	public ComponentProposalProvider(Collection<IComponentDefinition> components){
		 List<IContentProposal> proposals = new ArrayList<IContentProposal>();
		for(IComponentDefinition c : components){
			proposals.add(new ComponentProposal(c));
		}
		
				
		for(IComponentDefinition c : components){
			for(ComponentParameter p : c.getParameters()){
				
				proposals.add(new ComponentVariableProposal(p));
			}
			
		}
		
		this.proposals = proposals.toArray(new IContentProposal[proposals.size()]);
	}
	
	public IContentProposal[] getProposals(String contents, int position) {
		return proposals;
	}

}
