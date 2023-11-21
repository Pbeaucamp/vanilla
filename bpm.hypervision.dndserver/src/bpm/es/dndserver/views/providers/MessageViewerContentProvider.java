package bpm.es.dndserver.views.providers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import bpm.es.dndserver.api.Message;

public class MessageViewerContentProvider implements IStructuredContentProvider {
	
    public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}
    
	public void dispose() {
	}
    
	public Object[] getElements(Object parent) {
		return getChildren(parent);
	}
    
	public Object getParent(Object child) {
		return null;
	}
    
	public Object[] getChildren(Object parent) {
		if (parent instanceof ArrayList<?>) {
			List<Message> actions = (List<Message>) parent;
			return actions.toArray();
		}

		return new Object[0];
	}

    public boolean hasChildren(Object parent) {
		
		return false;
	}
}
