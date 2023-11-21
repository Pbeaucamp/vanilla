package bpm.birep.admin.client.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.vanilla.platform.core.repository.RepositoryItem;



public class DilaogInsertReportInDictionary extends Dialog {

	private RepositoryItem reportItem;
	
	public DilaogInsertReportInDictionary(Shell parentShell, RepositoryItem reportItem) {
		super(parentShell);
		this.reportItem = reportItem;
		
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		
		return super.createDialogArea(parent);
	}

	
	
}
