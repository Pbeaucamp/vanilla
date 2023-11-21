package bpm.es.sessionmanager.views.providers;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import bpm.es.sessionmanager.api.IIndexedLog;
import bpm.vanilla.platform.core.beans.VanillaLogs;

public class LogContentProvider implements IStructuredContentProvider, ITreeContentProvider {
	
	private IIndexedLog indexLog;
	
    public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}
    
	public void dispose() {
	}
    
	public Object[] getElements(Object parent) {
		if (parent instanceof IIndexedLog) {
			indexLog = (IIndexedLog) parent;
			Object[] result = indexLog.getIndexKeys().toArray();
			return result;
		}
		else 
			return new Object[0];
	}
    
	public Object getParent(Object child) {
		return null;
	}
    
	public Object[] getChildren(Object parent) {
		if (parent instanceof String) {
			String key = (String) parent;
			List<VanillaLogs> logs = indexLog.getSessionLogForKey(key);
			return logs.toArray();
		}
		return new Object[0];
	}

    public boolean hasChildren(Object parent) {
    	if (parent instanceof String) {
    		return true;
    	}
		return false;
	}
}
