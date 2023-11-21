package bpm.fd.repository.ui.wizard.actions;

import bpm.fd.api.core.model.FdProject;
import bpm.fd.design.ui.Activator;
import bpm.fd.repository.ui.dialogs.DialogUpdate;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.repository.Repository;

public class ActionUpdateProject {
	private IRepositoryApi sock;
	private IVanillaAPI vanillaApi;
	private FdProject project;
	private Repository rep;
	
	public ActionUpdateProject(IRepositoryApi sock, FdProject project, Repository rep, IVanillaAPI vanillaApi){
		this.sock = sock;
		this.project = project;
		this.rep = rep;
		this.vanillaApi = vanillaApi;
	}
	
	public void perform(){
		DialogUpdate d = new DialogUpdate(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), project, sock, vanillaApi, rep);
		d.open();
	}
}
