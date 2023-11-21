package bpm.gateway.ui.composites.labelproviders;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import bpm.gateway.core.StreamElement;
import bpm.gateway.ui.i18n.Messages;

public class DefaultStreamLabelProvider extends DecoratingLabelProvider
	implements ITableLabelProvider{

	public DefaultStreamLabelProvider(ILabelProvider provider,
			ILabelDecorator decorator) {
		super(provider, decorator);
		
	}

	public Image getColumnImage(Object element, int columnIndex) {
		
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		if (element == null){
			return null;
		}
		
		switch(columnIndex){
		case 0:
			return ((StreamElement)element).transfoName;
		case 1:
			return ((StreamElement)element).originTransfo;
		case 2: 
			return ((StreamElement)element).tableName;
		case 3:
			return ((StreamElement)element).name;
		case 4:
			return ((StreamElement)element).typeName;
		case 5:
			return ((StreamElement)element).className;
			
		}
		return ""; //$NON-NLS-1$
	}

	public void addListener(ILabelProviderListener listener) {
		
		
	}

	public void dispose() {
		
		
	}

	public boolean isLabelProperty(Object element, String property) {
		
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		
		
	}
}
