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
 * Dialog for the creation of the macro processes activities
 * 
 * @author Charles MARTIN
 * 
 */
public class DialogMacroProcess extends Dialog {

	public String type = ""; //$NON-NLS-1$

	public DialogMacroProcess(Shell parentShell) {
		super(parentShell);

	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(220, 300);

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL));
		composite.setLayout(new GridLayout(4, false));
		getShell().setText(Messages.DialogMacroProcess_1);

		final Text textfield = new Text(composite, SWT.NONE);

		textfield.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 4, 1));
		textfield.setEnabled(false);

		Button lab = new Button(composite, SWT.PUSH);
		lab.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.STRATEGYPROCESS_32));
		lab.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false, 1, 1));
		lab.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {
				type = "strategy"; //$NON-NLS-1$
				textfield.setText(Messages.DialogMacroProcess_3);

			}

		});

		Button lab1 = new Button(composite, SWT.PUSH);
		lab1.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.DESIGNPROCESS_32));
		lab1.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false, 1, 1));
		lab1.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {
				type = "design"; //$NON-NLS-1$
				textfield.setText(Messages.DialogMacroProcess_5);

			}

		});

		Button lab2 = new Button(composite, SWT.PUSH);
		lab2.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.IMPLEMENATIONPROCESS_32));
		lab2.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false, 1, 1));
		lab2.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {
				type = "implementation"; //$NON-NLS-1$
				textfield.setText(Messages.DialogMacroProcess_7);

			}

		});

		Button lab3 = new Button(composite, SWT.PUSH);
		lab3.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.CONTROLLINGPROCESS_32));
		lab3.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false, 1, 1));
		lab3.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {
				type = "controlling"; //$NON-NLS-1$
				textfield.setText(Messages.DialogMacroProcess_9);

			}

		});

		return composite;
	}

	/**
	 * 
	 * @return the type of the macro process
	 */
	public String getType() {
		return this.type;
	}

}
