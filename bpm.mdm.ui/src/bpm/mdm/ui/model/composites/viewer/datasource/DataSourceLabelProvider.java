package bpm.mdm.ui.model.composites.viewer.datasource;

import org.eclipse.datatools.connectivity.oda.design.ColumnDefinition;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.datatools.connectivity.oda.design.DataSourceDesign;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import bpm.mdm.ui.Activator;
import bpm.mdm.ui.icons.IconNames;

public class DataSourceLabelProvider extends LabelProvider{
	@Override
	public String getText(Object element) {
		if (element instanceof DataSetDesign){
			return ((DataSetDesign)element).getName();
		}
		if (element instanceof DataSourceDesign){
			return ((DataSourceDesign)element).getName();
		}
		else if (element instanceof ColumnDefinition){
			return ((ColumnDefinition)element).getAttributes().getName();
		}
		return super.getText(element);
	}
	
	@Override
	public Image getImage(Object element) {
		if (element instanceof DataSetDesign){
			return Activator.getDefault().getImageRegistry().get(IconNames.TABLE);
		}
		else if (element instanceof ColumnDefinition){
			return Activator.getDefault().getImageRegistry().get(IconNames.COLUMN);
		}
		return super.getImage(element);
	}

}
