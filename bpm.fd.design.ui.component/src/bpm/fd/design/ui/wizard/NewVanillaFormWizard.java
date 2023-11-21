package bpm.fd.design.ui.wizard;

import bpm.fd.api.core.model.FdProjectDescriptor;
import bpm.fd.api.core.model.FdVanillaFormModel;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.api.core.model.structure.FactoryStructure;

public class NewVanillaFormWizard extends NewFdMultiPageWizard{
	
	
	public NewVanillaFormWizard() {
		
	}
	
	protected MultiPageFdProject createFdProject(FdProjectDescriptor desc){
		return new MultiPageFdProject(desc, new FdVanillaFormModel(new FactoryStructure()));
	}

	
}
