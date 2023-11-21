package bpm.sqldesigner.ui.snapshot.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import bpm.sqldesigner.api.document.DocumentSnapshot;
import bpm.sqldesigner.ui.Activator;

public class SnapshotEditorInput implements IEditorInput{
	private DocumentSnapshot snapshot;
	
	public SnapshotEditorInput(DocumentSnapshot snapshot) {
		this.snapshot = snapshot;
		
		Activator.getDefault().refreshButton();
	}

	public boolean exists() {
		return (snapshot != null);
	}

	public ImageDescriptor getImageDescriptor() {
		return ImageDescriptor.getMissingImageDescriptor();
	}

	public String getName() {
		return snapshot.getName();
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return snapshot.getName();
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	public DocumentSnapshot getSnapshot() {
		
		return snapshot;
	}
	
	
}
