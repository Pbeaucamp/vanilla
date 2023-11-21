package bpm.metadata.client.security.viewers;

import java.util.ArrayList;
import java.util.List;

import metadataclient.Activator;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import bpm.metadata.MetaData;
import bpm.metadata.layer.business.AbstractBusinessTable;
import bpm.metadata.layer.business.BusinessModel;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;

public class FMDTClientTreeProvider implements ITreeContentProvider{
	private String groupName;
	
	public Object[] getChildren(Object parentElement) {
		List<Object> l = new ArrayList<Object>();
		if (parentElement instanceof IBusinessModel){
			l.addAll(((IBusinessModel)parentElement).getBusinessPackages(groupName));
		}
		if (parentElement instanceof IBusinessPackage){
			l.addAll(((IBusinessPackage)parentElement).getConnectionsNames(groupName));
			l.addAll(((IBusinessPackage)parentElement).getBusinessTables(groupName));
			l.addAll(((IBusinessPackage)parentElement).getResources(groupName));
		}
		if (parentElement instanceof IBusinessTable){
			l.addAll(((AbstractBusinessTable)parentElement).getChilds(groupName));
			l.addAll(((IBusinessTable)parentElement).getColumns(groupName));
		}
		return l.toArray(new Object[l.size()]);
	}

	public Object getParent(Object element) {
		
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof IBusinessModel){
			return !((IBusinessModel)element).getBusinessPackages(groupName).isEmpty();
		}
		if (element instanceof IBusinessPackage){
			return ((IBusinessPackage)element).getBusinessTables(groupName).size() +((IBusinessPackage)element).getConnectionsNames(groupName).size() +  ((IBusinessPackage)element).getResources(groupName).size() > 0; 
		}
		if (element instanceof IBusinessTable){
			return ((IBusinessTable)element).getColumns(groupName).size() + ((IBusinessTable)element).getChilds(groupName).size() > 0;  
		}
		return false;
	}

	public Object[] getElements(Object inputElement) {
		
		groupName = (String)inputElement;
		MetaData fmdt = Activator.getDefault().getModel();
		
		List<IBusinessModel> models = new ArrayList<IBusinessModel>();
		
		for(IBusinessModel m : fmdt.getBusinessModels()){
			if (((BusinessModel)m).isGrantedFor(groupName)){
				models.add(m);
			}
		}
		
		return models.toArray(new Object[models.size()]);
	}

	public void dispose() {
		
		
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		
	}

}
