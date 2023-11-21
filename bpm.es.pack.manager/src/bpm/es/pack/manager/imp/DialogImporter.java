package bpm.es.pack.manager.imp;

import java.io.File;
import java.util.HashMap;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.es.pack.manager.I18N.Messages;

public class DialogImporter extends Dialog {

	private CompositeMapping comp ; 
	private HashMap<String, File> map;
	
	public DialogImporter(Shell parentShell, HashMap<String, File> map) {
		super(parentShell);
		this.map = map;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		
		comp = new CompositeMapping(parent, SWT.NONE, map);
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		return comp;
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(800, 600);
		getShell().setText(Messages.DialogImporter_0);
	}

	
}
