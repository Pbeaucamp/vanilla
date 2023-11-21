package bpm.mdm.ui.model.composites.viewer.datasource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.datatools.connectivity.oda.design.DataSourceDesign;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import bpm.mdm.model.helper.DataSetDesignConverter;

public class DataSourceContentProvider implements ITreeContentProvider{
	private HashMap<DataSourceDesign, List<DataSetDesign>> columns = new HashMap<DataSourceDesign, List<DataSetDesign>>();
	
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof DataSetDesign){
			DataSetDesignConverter.rebuildDataSetDesign(((DataSetDesign)parentElement));
			
			List l = ((DataSetDesign)parentElement).getPrimaryResultSet().getResultSetColumns().getResultColumnDefinitions();
			return l.toArray(new Object[l.size()]);
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof DataSetDesign){
			return true;
		}
		return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		
		for(DataSetDesign i : (List<DataSetDesign>)inputElement){
			if (columns.get(i.getDataSourceDesign()) == null){
				columns.put(i.getDataSourceDesign(), new ArrayList<DataSetDesign>());
			}
			columns.get(i.getDataSourceDesign()).add(i);
		}
		List l = new ArrayList((List<DataSetDesign>)inputElement);//columns.keySet());
		return l.toArray(new Object[l.size()]);
	}

	@Override
	public void dispose() {
		
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		
	}
}
