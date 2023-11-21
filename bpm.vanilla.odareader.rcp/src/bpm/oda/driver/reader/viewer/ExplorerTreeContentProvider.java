package bpm.oda.driver.reader.viewer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import bpm.oda.driver.reader.impl.ui.OdaDatasExplorer;
import bpm.oda.driver.reader.model.ReaderModel;
import bpm.oda.driver.reader.model.Snapshot;
import bpm.oda.driver.reader.model.dataset.DataSet;
import bpm.oda.driver.reader.model.datasource.DataSource;

public class ExplorerTreeContentProvider implements ITreeContentProvider {

	private TreeViewer viewer;
	
	public ExplorerTreeContentProvider(TreeViewer pViewer){
		
		this.viewer = pViewer;
	}
	
	public Object[] getChildren(Object parentElement) {
		List<Object> l = new ArrayList<Object>();
		
		if (viewer.getInput() != null){
			if (parentElement == OdaDatasExplorer.NODE_DATASOURCES){
				l.addAll(((ReaderModel)viewer.getInput()).getListDataSource());
			}
			else if (parentElement == OdaDatasExplorer.NODE_DATASETS){
				l.addAll(((ReaderModel)viewer.getInput()).getListDataSet());
			}
			
			else if (parentElement == OdaDatasExplorer.NODE_SNAPSHOTS){
				l.addAll(((ReaderModel)viewer.getInput()).getListSnapshots());
			}
		}
		
		return l.toArray(new Object[l.size()]);
	}

	public Object getParent(Object element) {
		if (element instanceof DataSource){
			return OdaDatasExplorer.NODE_DATASOURCES;
		}
		else if (element instanceof DataSet){
			return OdaDatasExplorer.NODE_DATASETS;
		}
		
		else if (element instanceof Snapshot){
			return OdaDatasExplorer.NODE_SNAPSHOTS;
		}
		
		return null;
	}

	public boolean hasChildren(Object element) {
		
		if (element == null){
			return false;
		}
		if (element instanceof ReaderModel){
			return true;
		}
		else if (element == OdaDatasExplorer.NODE_DATASOURCES){
			return !((ReaderModel)viewer.getInput()).getListDataSource().isEmpty();
		}
		else if (element == OdaDatasExplorer.NODE_DATASETS){
			return !((ReaderModel)viewer.getInput()).getListDataSet().isEmpty();
		}
		
		else if (element == OdaDatasExplorer.NODE_SNAPSHOTS){
			return !((ReaderModel)viewer.getInput()).getListSnapshots().isEmpty();
		}
		
		else if (element instanceof DataSet){
			return ((DataSet)element).getDescriptor() != null;
		}
		return false;
	}

	public Object[] getElements(Object inputElement) {
		Object[] o = new Object[]{OdaDatasExplorer.NODE_DATASOURCES,
				OdaDatasExplorer.NODE_DATASETS,
				OdaDatasExplorer.NODE_SNAPSHOTS};
		return o;
	}

	public void dispose() {
		
		
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		
	}

}
