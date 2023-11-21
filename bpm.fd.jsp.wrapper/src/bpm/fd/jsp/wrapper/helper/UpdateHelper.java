package bpm.fd.jsp.wrapper.helper;

import java.util.ArrayList;
import java.util.List;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.FdProjectRepositoryDescriptor;
import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.resources.IResource;
import bpm.fd.api.core.model.structure.Folder;
import bpm.fd.api.core.model.structure.FolderPage;
import bpm.fd.api.core.model.tools.ModelLoader;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;

public class UpdateHelper {

	private List<Object> elementToUpdate = new ArrayList<Object>();
	private List<Object> elementToAdd = new ArrayList<Object>();
	
	public void updateDashboard(FdProject project, List<Group> groups, FdProject loadedProject, IRepositoryApi repositoryApi) throws Exception {
		
		project.setDescriptor(loadedProject.getProjectDescriptor());
		
		findElements(project, loadedProject);
		
		ModelLoader.update(elementToUpdate, elementToAdd, project, repositoryApi, repositoryApi.getContext().getGroup().getName());
	}

	private void findElements(FdProject project, FdProject loadedProject) {
		
		FdProjectRepositoryDescriptor desc = (FdProjectRepositoryDescriptor) project.getProjectDescriptor();
		FdProjectRepositoryDescriptor oldDesc = (FdProjectRepositoryDescriptor) loadedProject.getProjectDescriptor();
		
		elementToUpdate.add(project.getFdModel());
		elementToUpdate.add(project.getDictionary());
		
		Folder folder = null;
		Folder loadedFolder = null;
		
		//find folders
		for(IBaseElement fdElem : project.getFdModel().getContent()) {
			if(fdElem instanceof Folder) {
				folder = (Folder) fdElem;
			}
		}
		for(IBaseElement fdElem : loadedProject.getFdModel().getContent()) {
			if(fdElem instanceof Folder) {
				loadedFolder = (Folder) fdElem;
			}
		}
		
		//fetch the models
		LOOK:for(IBaseElement fdPageElem : folder.getContent()) {
			FdModel model = (FdModel)((FolderPage)fdPageElem).getContent().get(0);
			for(IBaseElement oldFdPageElem : loadedFolder.getContent()) {
				FdModel oldModel = (FdModel)((FolderPage)oldFdPageElem).getContent().get(0);
				if(oldModel.getName().equals(model.getName())) {
					desc.replaceModelId(model, oldDesc.getModelPageId(oldModel));
					elementToUpdate.add(model);
					continue LOOK;
				}
			}
			elementToAdd.add(model);
		}
		
		//fetch the resources
		LOOK:for(IResource res : project.getResources()) {
			for(IResource oldRes : loadedProject.getResources()) {
				if(res.getName().equals(oldRes.getName())) {
					desc.replaceResourceId(res, oldDesc.getResourceId(oldRes));
					elementToUpdate.add(res);
					continue LOOK;
				}
			}
			elementToAdd.add(res);
		}
		
	}
	
}
