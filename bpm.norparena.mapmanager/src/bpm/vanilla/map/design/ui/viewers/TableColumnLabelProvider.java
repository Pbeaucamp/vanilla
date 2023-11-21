package bpm.vanilla.map.design.ui.viewers;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import bpm.vanilla.map.core.design.IAddress;
import bpm.vanilla.map.core.design.IMapDefinition;

public class TableColumnLabelProvider implements ITableLabelProvider{

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		 String result = "ERROR"; //$NON-NLS-1$
        if (element instanceof IAddress) {
        	IAddress address = (IAddress) element;
            if (columnIndex == 0) {
                result = address.getLabel();
            } else if (columnIndex == 1) {
                result = address.getBloc();
            } else if (columnIndex == 2) {
                result = address.getArrondissement();
            } else if (columnIndex == 3) {
                result = address.getStreet1();
            } else if (columnIndex == 4) {
                result = address.getStreet2();
            } else if (columnIndex == 5) {
                result = address.getZipCode()+""; //$NON-NLS-1$
            } else if (columnIndex == 6) {
                result = address.getINSEECode()+""; //$NON-NLS-1$
            } else if (columnIndex == 7) {
                result = address.getCity();
            } else if (columnIndex == 8) {
                result = address.getCountry();
            }
        }
        else if (element instanceof IMapDefinition) {
        	IMapDefinition mapDef = (IMapDefinition) element;
            if (columnIndex == 0) {
                result = mapDef.getLabel();
            } else if (columnIndex == 1) {
            	//TODO : A remplacer
				return null;
			
            } else if (columnIndex == 2) {
            	//TODO : A remplacer
//				if(((IMapDefinition)element).getKmlItemId() != null){
//					return "Kml with itemId=" + ((IMapDefinition)element).getKmlItemId();
//				}
//				else{
					return null;
//				}
            } else if (columnIndex == 3) {
//				if(((IMapDefinition)element).getFusionMapsItemId() != null){
//					return "Fusion Map with itemId=" + ((IMapDefinition)element).getFusionMapsItemId();
//				}
//				else{
					return null;
//				}
            }
        }
        return result;
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		
		
	}

	@Override
	public void dispose() {
		
		
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		
		
	}


}
