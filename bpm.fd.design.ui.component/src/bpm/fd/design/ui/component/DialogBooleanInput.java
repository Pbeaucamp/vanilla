package bpm.fd.design.ui.component;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public class DialogBooleanInput extends Dialog{
	
	private Button trueB, falseB;
	private boolean value;
	
	public DialogBooleanInput(Shell parent) {
		super(parent);
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Group g = new Group(parent, SWT.NONE);
		g.setText(Messages.DialogBooleanInput_0);
		g.setLayout(new GridLayout());
		g.setLayoutData(new GridData(GridData.FILL_BOTH));
	
		trueB = new Button(g, SWT.RADIO);
		trueB.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		trueB.setText("True");
		trueB.setSelection(true);
		
		falseB = new Button(g, SWT.RADIO);
		falseB.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		falseB.setText("False");
		
		return g;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		value = trueB.getSelection();
		super.okPressed();
	}

	public boolean getValue(){
		return value;
	}
}
