package metadata.client.model.dialog;

import metadata.client.i18n.Messages;
import metadata.client.model.composites.CompositeRelation;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.logical.Relation;
import bpm.metadata.layer.logical.sql.SQLRelation;

public class DialogRelation extends Dialog {

	private CompositeRelation composite;
	private IDataSource dataSource;
	private Relation relation;
	
	public DialogRelation(Shell parentShell, IDataSource dataSource) {
		super(parentShell);
		this.dataSource = dataSource;
		setShellStyle(SWT.RESIZE | SWT.CLOSE);
	}
	
	
	public DialogRelation(Shell parentShell, IDataSource dataSource, Relation relation) {
		super(parentShell);
		this.dataSource = dataSource;
		this.relation = relation;
		setShellStyle(SWT.RESIZE | SWT.CLOSE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		if (relation == null){
			relation = new SQLRelation();
		}
		
		composite = new CompositeRelation(parent, SWT.NONE, null, true, dataSource, relation);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.addListener(SWT.Selection, new Listener(){

			public void handleEvent(Event event) {
				getButton(IDialogConstants.OK_ID).setEnabled(!relation.getJoins().isEmpty());
				
			}
			
		});
		return parent;
	}

	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogRelation_0); //$NON-NLS-1$
		getShell().setSize(800, 600);
		setShellStyle(SWT.RESIZE | SWT.CLOSE);
		composite.layout();
		composite.createModel();
	}


	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}
	@Override
	protected void okPressed() {
		super.okPressed();
	}
	
	public Relation getRelation(){
		return relation;
	}

}
