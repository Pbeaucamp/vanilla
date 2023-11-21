package bpm.fd.design.ui.project.views.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IEditorPart;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.IDimensionableDatas;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.Palette;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.Messages;
import bpm.fd.design.ui.editors.FdProjectEditorInput;

public class DeleteAction extends Action {
	private Viewer viewer;
	private Object toDelete;
	private Dictionary dictionary;
	
	public DeleteAction(String name, String id, Viewer viewer){
		super(name);
		setId(id);
		this.viewer = viewer;
	}

	@Override
	public void run() {
		toDelete = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
		dictionary = (Dictionary)viewer.getInput();
		
		if (toDelete instanceof IComponentDefinition){
			
			deleteComponent((IComponentDefinition)toDelete);
		}
		else if (toDelete instanceof DataSet){
			if (MessageDialog.openQuestion(viewer.getControl().getShell(), Messages.DeleteAction_0, Messages.DeleteAction_1)){
				deleteDataSet((DataSet)toDelete);
			}
			
		}
		else if (toDelete instanceof DataSource){
			if (MessageDialog.openQuestion(viewer.getControl().getShell(), Messages.DeleteAction_2, Messages.DeleteAction_3)){
				deleteDataSource((DataSource)toDelete);
			}
			
		}
		else if (toDelete instanceof Palette){
			if (MessageDialog.openQuestion(viewer.getControl().getShell(), "Remove Palette Color", "Do you really want to remove this palette?")){
				dictionary.removePalette((Palette)toDelete);
			}
		}
		viewer.refresh();
	}
	

	private void deleteComponent(IComponentDefinition def){
		List<FdModel> models = new ArrayList<FdModel>();
		
		try{
			for(IEditorPart e : Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditors()){
				if (e.getEditorInput() instanceof FdProjectEditorInput){
					
					if (((FdProjectEditorInput)e.getEditorInput()).getModel().getProject().getDictionary() == def.getDictionary()){
						models.add(((FdProjectEditorInput)e.getEditorInput()).getModel().getProject().getFdModel());
						if (((FdProjectEditorInput)e.getEditorInput()).getModel().getProject() instanceof MultiPageFdProject){
							models.addAll(((MultiPageFdProject)((FdProjectEditorInput)e.getEditorInput()).getModel().getProject()).getPagesModels());
						}
					}
					
					
				}
			}
		}catch(Exception ex){
			
		}
		
		
		for(FdModel model : models){
			model.removeComponent(def);
		}
		
		
		def.getDictionary().removeComponent(def);
	}
	
	private void deleteDataSet(DataSet ds){

		for(IComponentDefinition c : dictionary.getComponents()){
			if (c.getDatas() != null && c.getDatas().getDataSet() == ds || (c.getDatas() instanceof IDimensionableDatas && ((IDimensionableDatas)c.getDatas()).getDimensionDataSet() != null && ((IDimensionableDatas)c.getDatas()).getDimensionDataSet() == ds)){
				deleteComponent(c);
			}
		}
		
		dictionary.removeDataSet(ds);
		
		
	}
	
	
	private void deleteDataSource(DataSource ds){
		for(DataSet c : dictionary.getDatasets()){
			if (c.getDataSourceName().equals(ds.getName())){
				deleteDataSet(c);
			}
		}
		
		dictionary.removeDataSource(ds);
	}
	
	
	
}
