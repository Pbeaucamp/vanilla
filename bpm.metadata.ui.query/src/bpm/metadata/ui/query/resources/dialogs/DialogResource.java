package bpm.metadata.ui.query.resources.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.metadata.resource.ComplexFilter;
import bpm.metadata.resource.IResource;
import bpm.metadata.resource.Prompt;
import bpm.metadata.resource.SqlQueryFilter;
import bpm.metadata.ui.query.resources.composites.CompositeComplexFilter;
import bpm.metadata.ui.query.resources.composites.CompositePrompt;
import bpm.metadata.ui.query.resources.composites.CompositeSqlFilter;
import bpm.metadata.ui.query.resources.composites.ResourceBuilder;

public class DialogResource extends Dialog{
	
	private IResource resource;
	private ResourceBuilder composite;
	
	public DialogResource(Shell parentShell, IResource resource) {
		super(parentShell);
		this.resource = resource;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		if (resource instanceof ComplexFilter){
			composite = new CompositeComplexFilter(parent, SWT.NONE, (ComplexFilter)resource);
		}
		else if (resource instanceof Prompt){
			composite = new CompositePrompt(parent, SWT.NONE, (Prompt)resource);
		}
		else if (resource instanceof SqlQueryFilter){
			composite = new CompositeSqlFilter(parent, SWT.NONE, (SqlQueryFilter)resource);
		}
		
		((Composite)composite).setLayoutData(new GridData(GridData.FILL_BOTH));
		return ((Composite)composite);
	}
	
	@Override
	protected void okPressed() {
		resource = composite.getResource();
		super.okPressed();
	}
}
