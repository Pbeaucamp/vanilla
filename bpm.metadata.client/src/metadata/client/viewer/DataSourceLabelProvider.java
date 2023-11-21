package metadata.client.viewer;

import metadataclient.Activator;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;

public class DataSourceLabelProvider extends LabelProvider{
	@Override
	public String getText(Object element) {
		if (element instanceof IDataStream){
			return ((IDataStream)element).getName();
		}
		else if ( element instanceof IDataStreamElement){
			return ((IDataStreamElement)element).getName();
		}
		return super.getText(element);
	}
	@Override
	public Image getImage(Object element) {
		if (element instanceof IDataStream){
			return Activator.getDefault().getImageRegistry().get("log_table"); //$NON-NLS-1$
		}
		if (element instanceof IDataStreamElement){
			return Activator.getDefault().getImageRegistry().get("log_column"); //$NON-NLS-1$
		}
		return super.getImage(element);
	}
}
