package bpm.es.dndserver.views.providers;

import java.text.SimpleDateFormat;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import bpm.es.dndserver.Messages;
import bpm.es.dndserver.api.Message;


public class MessageViewerLabelProvider implements ITableLabelProvider {
	
	public Image getColumnImage(Object element, int columnIndex) {
		
		
		return null;
	}
	
	public String getColumnText(Object element, int columnIndex) {
		Message msg = (Message) element;
		switch (columnIndex) {
			case 0 :
				return msg.getItemName();
			case 1 :
				return msg.getLevel() == Message.ERROR ? Messages.MessageViewerLabelProvider_0 : Messages.MessageViewerLabelProvider_1;				
			case 2 :
				return msg.getMessage();
			case 3 :
		}
		return ""; //$NON-NLS-1$
	}
	
	public void addListener(ILabelProviderListener listener) {
		
	}
	
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void dispose() {
	}

	public void removeListener(ILabelProviderListener listener) {
	}
}
