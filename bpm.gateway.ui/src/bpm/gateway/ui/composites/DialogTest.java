package bpm.gateway.ui.composites;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class DialogTest extends Dialog {

	public DialogTest(Shell parentShell) {
		super(parentShell);
		
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		SimpleMappingComposite c = new SimpleMappingComposite(parent, SWT.NONE);
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		return c;
	}
	
}
