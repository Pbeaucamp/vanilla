package bpm.fd.design.ui.structure.dialogs.creation.css;

import org.eclipse.swt.widgets.Composite;

public abstract class CompositeCssPart extends Composite{

	public CompositeCssPart(Composite parent, int style) {
		super(parent, style);
		
	}
	
	abstract public String getCssToString();

	
	abstract protected void createContent();
	
	abstract public String getPreviousCss();
}
