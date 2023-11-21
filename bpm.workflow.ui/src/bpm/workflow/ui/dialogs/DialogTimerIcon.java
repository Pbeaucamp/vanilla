package bpm.workflow.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.icons.IconsNames;

/**
 * Dialog for the type of the timer activities
 * 
 * @author Charles MARTIN
 * 
 */
public class DialogTimerIcon extends Dialog {

	public String type = ""; //$NON-NLS-1$

	public DialogTimerIcon(Shell parentShell) {
		super(parentShell);

	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(220, 150);

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL));
		composite.setLayout(new GridLayout(4, false));
		getShell().setText(Messages.DialogTimerIcon_1);

		final Text textfield = new Text(composite, SWT.NONE);

		textfield.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 4, 1));
		textfield.setEnabled(false);

		Button lab = new Button(composite, SWT.PUSH);
		lab.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.TIMER_32));
		lab.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false, 1, 1));
		lab.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {
				type = "timerstart"; //$NON-NLS-1$
				textfield.setText(Messages.DialogTimerIcon_3);
			}

		});

		Button lab1 = new Button(composite, SWT.PUSH);
		lab1.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.TIMERINTER_32));
		lab1.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false, 1, 1));
		lab1.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) {
				type = "timerinter"; //$NON-NLS-1$
				textfield.setText(Messages.DialogTimerIcon_5);
			}

		});

		return composite;
	}

	/**
	 * 
	 * @return the type of the timer activity
	 */
	public String getType() {
		return this.type;
	}

}
