package bpm.sqldesigner.ui.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import bpm.sqldesigner.api.document.SchemaView;

public class SQLDesignEditorInput implements IEditorInput {

	private SchemaView schemaView = null;

	public SQLDesignEditorInput(SchemaView schemaView) {
		this.schemaView = schemaView;
	}

	public boolean exists() {
		return (schemaView != null);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof SQLDesignEditorInput))
			return false;
		return ((SQLDesignEditorInput) o).getName().equals(getName());
	}

	public ImageDescriptor getImageDescriptor() {
		return ImageDescriptor.getMissingImageDescriptor();
	}

	public String getName() {
		if (schemaView == null){
			return ""; //$NON-NLS-1$
		}
		return schemaView.getName();
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		if (schemaView == null){
			return ""; //$NON-NLS-1$
		}
		return schemaView.getName();
	}

	public Object getAdapter(Class adapter) {
		return null;
	}
	
	public SchemaView getSchemaView(){
		return schemaView;
	}
}