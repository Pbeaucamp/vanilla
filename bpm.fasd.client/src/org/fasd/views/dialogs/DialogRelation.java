package org.fasd.views.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPRelation;
import org.fasd.views.composites.CompositeRelation;


public class DialogRelation extends Dialog {
	@Override
	protected void initializeBounds() {
		super.initializeBounds();
		this.getShell().setText(LanguageText.DialogRelation_Relations);
		this.getShell().setSize(800, 600);
		
		Shell shell = this.getShell();
		Monitor primary = shell.getMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();
		
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		
		shell.setLocation(x, y);
	}

	private List<OLAPRelation> relations;
	private CompositeRelation composite;
	
	@Override
	protected Control createDialogArea(Composite parent) {
		composite = new CompositeRelation(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		return parent;
	}

	public DialogRelation(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void okPressed() {
		relations = composite.getRelations();
		super.okPressed();
	}

	public List<OLAPRelation> getRelations(){
		return relations;
	}
}
