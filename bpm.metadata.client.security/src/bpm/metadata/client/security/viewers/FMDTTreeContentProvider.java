package bpm.metadata.client.security.viewers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import bpm.metadata.MetaData;
import bpm.metadata.layer.business.BusinessModel;
import bpm.metadata.layer.business.BusinessPackage;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.logical.AbstractDataSource;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;


public class FMDTTreeContentProvider implements ITreeContentProvider{
	
	private final String nodeResources =  "Resources";
	private final String nodeDataSources = "DataSources";
	private final String nodeBusinessModels = "BusinessModels";
	

	private MetaData fmdt;
	
	public Object[] getChildren(Object parentElement) {
		List<Object> l  = new ArrayList<Object>();
		if (parentElement instanceof IDataStream){
			l.addAll(((IDataStream)parentElement).getElements());
		}
		if (parentElement instanceof AbstractDataSource){
			l.addAll(((AbstractDataSource)parentElement).getConnections());
			l.addAll(((AbstractDataSource)parentElement).getDataStreams());
		}
		if (parentElement instanceof IBusinessModel){
			l.addAll(((BusinessModel)parentElement).getBusinessTables());
			l.addAll(((BusinessModel)parentElement).getBusinessPackages("none"));
			l.addAll(((BusinessModel)parentElement).getResources());
		}
		if (parentElement instanceof IBusinessPackage){
			
		}
		if (parentElement instanceof IBusinessTable){
			l.addAll(((IBusinessTable)parentElement).getChilds("none"));
			l.addAll(((IBusinessTable)parentElement).getColumns("none"));
		}
		if (parentElement == nodeBusinessModels){
			l.addAll(fmdt.getBusinessModels());
		}
		if (parentElement == nodeDataSources){
			l.addAll(fmdt.getDataSources());
		}
		if (parentElement == nodeResources){
			l.addAll(fmdt.getResources());
		}
		return l.toArray(new Object[l.size()]);
	}

	public Object getParent(Object element) {
		if (element instanceof IDataStream){
			return ((IDataStream)element).getDataSource();
		}
		if (element instanceof IDataStreamElement){
			return ((IDataStreamElement)element).getDataStream();
		}
		if (element instanceof AbstractDataSource){
			return nodeDataSources;
		}
		if (element instanceof IBusinessModel){
			return nodeBusinessModels;
		}
		if (element instanceof IBusinessPackage){
			return ((BusinessPackage)element).getBusinessModel();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element == nodeResources){
			return !fmdt.getResources().isEmpty();
		}
		if (element == nodeDataSources){
			return !fmdt.getDataSources().isEmpty();
		}
		if (element == nodeBusinessModels){
			return !fmdt.getBusinessModels().isEmpty();
		}
		if (element instanceof AbstractDataSource){
			return ((AbstractDataSource)element).getConnections().size() + ((AbstractDataSource)element).getDataStreams().size() > 0;
		}
		if (element instanceof IDataStream){
			return !((IDataStream)element).getElements().isEmpty();
		}
		if (element instanceof BusinessModel){
			return ((BusinessModel)element).getBusinessTables().size() + ((BusinessModel)element).getBusinessPackages("none").size() > 0;
		}
		if (element instanceof IBusinessTable){
			return ((IBusinessTable)element).getColumns("none").size() + ((IBusinessTable)element).getChilds("none").size() + ((IBusinessTable)element).getFilters().size() > 0; 
		}
//		if (element instanceof BusinessPackage){
//			return ((BusinessPackage)element).getBusinessTables("none").size() + ((BusinessPackage)element).getResources().size()  + ((BusinessPackage)element).getResources().size() > 0;
//		}
		return false;
	}

	public Object[] getElements(Object inputElement) {
		fmdt = (MetaData)inputElement;
		return new Object[]{nodeDataSources, nodeResources, nodeBusinessModels};
	}

	public void dispose() {
		
		
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		
	}

}
