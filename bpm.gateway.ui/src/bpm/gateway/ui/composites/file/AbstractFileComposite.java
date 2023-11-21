package bpm.gateway.ui.composites.file;

import org.eclipse.swt.widgets.Composite;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.ui.gef.model.Node;

public abstract class AbstractFileComposite extends Composite {

	public AbstractFileComposite(Composite parent, int style) {
		super(parent, style);
	}

	public abstract AbstractTransformation getFileTransformation();
	
	public abstract void refresh(Node node);
	
}
