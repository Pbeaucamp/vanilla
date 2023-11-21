package bpm.birep.admin.client.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import adminbirep.Messages;

public class DialogText extends Dialog {
	
	private Text content;
	private String text;
	private String title; 
	
	public DialogText(Shell parentShell, String text) {
		super(parentShell);
		this.text = text;
	}
	
	public DialogText(Shell parentShell, String text, String title) {
		super(parentShell);
		this.text = text;
		this.title = title;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		content = new Text(container, SWT.BORDER | SWT.MULTI | SWT.WRAP |  SWT.V_SCROLL);
		content.setText(text);
		content.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		content.setEditable(false);
		
		return parent;
	}

	@Override
	protected void initializeBounds() {
//		super.initializeBounds();
		if (title == null){
			getShell().setText(Messages.DialogText_0);
		}else{
			getShell().setText(title);
		}
		
		getShell().setSize(400, 300);
		
	}
	
	

}
