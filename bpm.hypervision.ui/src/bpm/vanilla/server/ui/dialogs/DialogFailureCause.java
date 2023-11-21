package bpm.vanilla.server.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DialogFailureCause extends Dialog {

	private String content;

	public DialogFailureCause(Shell parentShell, String content) {
		super(parentShell);
		this.content = content;
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(700, 500);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		c.setLayout(new GridLayout());
		
		Text xml = new Text(c, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		xml.setLayoutData(new GridData(GridData.FILL_BOTH));

		if (content != null){
			xml.setText(content);
		}

		return c;
	}
	
	@Override
	protected Control createButtonBar(Composite parent) {
		return parent;
	}
}
