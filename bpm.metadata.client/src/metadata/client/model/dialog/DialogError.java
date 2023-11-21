package metadata.client.model.dialog;

import metadata.client.i18n.Messages;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DialogError extends Dialog {
	private Text content;
	private String txt;
	public DialogError(Shell parentShell, String content) {
		super(parentShell);
		txt = content;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Messages.DialogError_0, true);
		
	
	}
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new FillLayout());
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		content = new Text(c, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL  | SWT.BORDER);
		content.setText(txt);
		return c;
	}
	@Override
	protected void initializeBounds() {
		getShell().setSize(400, 300);
		getShell().setText(Messages.DialogError_1);
		setShellStyle(SWT.CLOSE | SWT.RESIZE);
	}
	
	

}
