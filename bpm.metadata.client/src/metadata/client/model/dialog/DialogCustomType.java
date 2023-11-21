package metadata.client.model.dialog;

import metadata.client.i18n.Messages;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DialogCustomType extends Dialog {
	private Text content;
	private String txt;
	
	public DialogCustomType(Shell parentShell, String content) {
		super(parentShell);
		txt = content;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.CustomTypeDialog_0); //$NON-NLS-1$
		
		content = new Text(c, SWT.NONE);
		content.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		content.setText(txt);
		
		return c;
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setSize(400, 200);
		getShell().setText(Messages.CustomTypeDialog_1);
		setShellStyle(SWT.CLOSE | SWT.RESIZE);
	}
	
	public String getNewCustomType() {
		return txt;
	}

	@Override
	protected void okPressed() {
		txt = content.getText();
		super.okPressed();
	}
	
	
	
	

}
