package bpm.mdm.ui.model.composites.viewer;

import org.eclipse.datatools.connectivity.oda.design.ColumnDefinition;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import bpm.mdm.model.Attribute;
import bpm.mdm.model.Entity;
import bpm.mdm.model.Model;
import bpm.mdm.model.Synchronizer;
import bpm.mdm.ui.Activator;
import bpm.mdm.ui.i18n.Messages;
import bpm.mdm.ui.icons.IconNames;



public class MdmLabelProvider extends LabelProvider{

	@Override
	public String getText(Object element) {
		if (element instanceof Model){
			return ((Model)element).getName();
		}
		if (element instanceof Entity){
			return ((Entity)element).getName();
		}
		if (element instanceof Attribute){
			return ((Attribute)element).getName();
		}
		if (element instanceof Synchronizer){
			return ""; //$NON-NLS-1$
			//return ((Synchronizer)element).getName();
		}
		return super.getText(element);
	}
	
	@Override
	public Image getImage(Object element) {
		if (element instanceof DataSetDesign){
			return Activator.getDefault().getImageRegistry().get(IconNames.TABLE);
		}
		if (element instanceof ColumnDefinition){
			return Activator.getDefault().getImageRegistry().get(IconNames.COLUMN);
		}
		return super.getImage(element);
	}
}
