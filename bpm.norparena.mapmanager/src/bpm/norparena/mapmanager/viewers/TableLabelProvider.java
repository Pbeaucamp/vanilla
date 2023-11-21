package bpm.norparena.mapmanager.viewers;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import bpm.norparena.mapmanager.Activator;
import bpm.norparena.mapmanager.icons.Icons;
import bpm.vanilla.map.core.design.IAddress;
import bpm.vanilla.map.core.design.IBuilding;
import bpm.vanilla.map.core.design.ICell;
import bpm.vanilla.map.core.design.IBuildingFloor;

public class TableLabelProvider extends LabelProvider{

	@Override
	public Image getImage(Object obj) {
		ImageRegistry reg = Activator.getDefault().getImageRegistry();
		if(obj instanceof IBuilding)
			return reg.get(Icons.BUILDING);
		if(obj instanceof IBuildingFloor)
			return reg.get(Icons.FLOOR);
		if(obj instanceof ICell)
			return reg.get(Icons.CELL);
		else
			return null;
				
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if(element instanceof IAddress)
			return ((IAddress)element).getLabel();		
		if(element instanceof IBuilding)
			return ((IBuilding)element).getLabel();
		if(element instanceof IBuildingFloor)
			return ((IBuildingFloor)element).getLabel();
		if(element instanceof ICell)
			return ((ICell)element).getLabel();
		return super.getText(element);
	}
}
