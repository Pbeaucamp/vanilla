package bpm.workflow.ui.dialogs;

import java.util.Properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.workflow.runtime.resources.variables.Variable;
import bpm.workflow.ui.Messages;

/**
 * Dialog for the creation and edition of the variables
 * 
 * @author CHARBONNIER, MARTIN
 * 
 */
public class DialogVariable extends Dialog {
	private Text txtName;
	private Text txtDefault;
	private Combo cbType;
	private Properties prop = new Properties();
	private Variable variable;

	/**
	 * Create the dialog with the specified variable
	 * 
	 * @param parentShell
	 * @param variable
	 */
	public DialogVariable(Shell parentShell, Variable variable) {
		super(parentShell);
		this.variable = variable;
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(400, 300);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(2, false));
		getShell().setText(Messages.DialogVariable_0);

		Label lblName = new Label(composite, SWT.NONE);
		lblName.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblName.setText(Messages.DialogVariable_1);

		txtName = new Text(composite, SWT.BORDER);
		txtName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		if("".equalsIgnoreCase(variable.getName())) { //$NON-NLS-1$
			txtName.addModifyListener(modify);
		}

		else {
			txtName.setText(variable.getName());
			prop.setProperty("name", variable.getName()); //$NON-NLS-1$
			txtName.setEnabled(false);
		}

		Label lblType = new Label(composite, SWT.NONE);
		lblType.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblType.setText(Messages.DialogVariable_4);

		cbType = new Combo(composite, SWT.READ_ONLY);
		cbType.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		cbType.setItems(Variable.TYPES_NAMES);
		cbType.addSelectionListener(selection);
		cbType.select(variable.getType());
		prop.setProperty("type", cbType.getSelectionIndex() + ""); //$NON-NLS-1$ //$NON-NLS-2$

		Label lblDefault = new Label(composite, SWT.NONE);
		lblDefault.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblDefault.setText(Messages.DialogVariable_7);

		txtDefault = new Text(composite, SWT.BORDER);
		txtDefault.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txtDefault.addModifyListener(modify);
		if(!variable.getValues().isEmpty()) {
			txtDefault.setText(variable.getValues().get(0));
		}

		if(variable.getName().equalsIgnoreCase("{$VANILLA_HOME}") //$NON-NLS-1$
				|| variable.getName().equalsIgnoreCase("{$VANILLA_FILES}") //$NON-NLS-1$
				|| variable.getName().equalsIgnoreCase("E_TIME") //$NON-NLS-1$
				|| variable.getName().equalsIgnoreCase("E_IP")) { //$NON-NLS-1$
			try {
				txtName.setText(variable.getName());
				prop.setProperty("name", variable.getName()); //$NON-NLS-1$
				txtName.setEnabled(false);
				cbType.setEnabled(false);
				txtDefault.setText(Messages.DialogVariable_13);
				txtDefault.setEnabled(false);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return composite;
	}

	SelectionListener selection = new SelectionListener() {

		public void widgetDefaultSelected(SelectionEvent e) {

		}

		public void widgetSelected(SelectionEvent e) {
			prop.setProperty("type", cbType.getSelectionIndex() + ""); //$NON-NLS-1$ //$NON-NLS-2$
		}

	};

	ModifyListener modify = new ModifyListener() {

		public void modifyText(ModifyEvent e) {
			if(e.widget == txtName) {
				prop.setProperty("name", txtName.getText()); //$NON-NLS-1$
			}
			if(e.widget == txtDefault) {
				prop.setProperty("default", txtDefault.getText()); //$NON-NLS-1$
			}
		}

	};

	/**
	 * 
	 * @return the properties of the variable
	 */
	public Properties getProperties() {
		return prop;
	}

	@Override
	protected void okPressed() {
		if(prop.getProperty("default") == null) { //$NON-NLS-1$
			MessageDialog.openInformation(getShell(), Messages.DialogVariable_19, Messages.DialogVariable_20);
		}
		else if(prop.getProperty("name") == null) { //$NON-NLS-1$
			MessageDialog.openInformation(getShell(), Messages.DialogVariable_22, Messages.DialogVariable_23);
		}
		else {
			super.okPressed();

		}
	}

}
