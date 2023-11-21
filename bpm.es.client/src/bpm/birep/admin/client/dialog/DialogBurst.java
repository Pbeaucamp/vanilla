package bpm.birep.admin.client.dialog;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.vanilla.platform.core.beans.Group;

public class DialogBurst extends Dialog {

	private CheckboxTreeViewer tree;
	private List<Group> groups;
	
	protected DialogBurst(Shell parentShell, List<Group> groups) {
		super(parentShell);
		this.groups = groups;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		
		
		return container;
	}

	
}
