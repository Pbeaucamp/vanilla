package metadata.client.model.dialog;

import metadata.client.model.composites.CompositeRelationStrategy;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.RelationStrategy;

public class DialogRelationStrategy extends Dialog {

	private IBusinessModel model;
	private RelationStrategy strategy;
	
	private CompositeRelationStrategy compositeRel;

	public DialogRelationStrategy(Shell parentShell, IBusinessModel model, RelationStrategy strategy) {
		super(parentShell);
		this.strategy = strategy;
		this.model = model;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		compositeRel = new CompositeRelationStrategy(parent, SWT.NONE, model, strategy);
		compositeRel.setLayout(new GridLayout(1, false));
		compositeRel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		return compositeRel;
	}
	


	@Override
	protected void okPressed() {
		
		compositeRel.okPressed();
		
		super.okPressed();
	}
	
}
