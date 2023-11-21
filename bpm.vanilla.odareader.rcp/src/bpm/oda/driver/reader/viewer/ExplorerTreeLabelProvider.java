package bpm.oda.driver.reader.viewer;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import bpm.oda.driver.reader.Activator;
import bpm.oda.driver.reader.icons.IconsName;
import bpm.oda.driver.reader.impl.ui.OdaDatasExplorer;
import bpm.oda.driver.reader.model.ILabelable;
import bpm.oda.driver.reader.model.Snapshot;
import bpm.oda.driver.reader.model.dataset.DataSet;
import bpm.oda.driver.reader.model.dataset.ParameterDescriptor;
import bpm.oda.driver.reader.model.datasource.DataSource;

public class ExplorerTreeLabelProvider extends LabelProvider{

	@Override
	public Image getImage(Object element) {
		if (element == OdaDatasExplorer.NODE_DATASOURCES){
			return Activator.getDefault().getImageRegistry().get(IconsName.ICO_FOLDER_DATASOURCE);
		}
		else if (element == OdaDatasExplorer.NODE_DATASETS){
			return Activator.getDefault().getImageRegistry().get(IconsName.ICO_FOLDER_DATASET);
		}
		
		else if (element == OdaDatasExplorer.NODE_SNAPSHOTS){
			return Activator.getDefault().getImageRegistry().get(IconsName.ICO_FOLDER_SNAPSHOT);
		}
		
		else if (element instanceof DataSource){
			return Activator.getDefault().getImageRegistry().get(IconsName.ICO_DATASOURCE);
		}
		
		else if (element instanceof DataSet){
			return Activator.getDefault().getImageRegistry().get(IconsName.ICO_DATASET);
		}
		
		else if (element instanceof Snapshot){
			return Activator.getDefault().getImageRegistry().get(IconsName.ICO_SNAPSHOT);
		}
		
		else{
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof DataSource){
			return ((DataSource)element).getName();
		}
		else if (element instanceof DataSet){
			return ((DataSet)element).getName();
		}
		else if (element instanceof ParameterDescriptor){
			return ((ParameterDescriptor)element).getLabel();
		}
		else if (element instanceof ILabelable){
			return ((ILabelable)element).getName();
		}
		return super.getText(element);
	}

	
	
}
