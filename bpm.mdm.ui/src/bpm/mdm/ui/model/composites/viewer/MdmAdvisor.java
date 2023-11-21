package bpm.mdm.ui.model.composites.viewer;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.databinding.viewers.TreeStructureAdvisor;

import bpm.mdm.model.Entity;

public class MdmAdvisor extends TreeStructureAdvisor{
	@Override
	public Object getParent(Object element) {
		
		return ((EObject)element).eContainer();
	}

	public Boolean hasChildren(Object element) {
		if (element instanceof Entity){
			return !((Entity)element).getAttributes().isEmpty();
		}
		return false;
	};
}
