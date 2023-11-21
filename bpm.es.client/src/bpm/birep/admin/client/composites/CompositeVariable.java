package bpm.birep.admin.client.composites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import adminbirep.Messages;
import bpm.birep.admin.client.views.ViewVariable;
import bpm.vanilla.platform.core.beans.Variable;

public class CompositeVariable extends Composite {

	private Button update;
	private Text name, value, source;
	private Combo cbType, cbScope;
	private ViewVariable view;

	private Variable variable;

	public CompositeVariable(Composite parent, int style) {
		super(parent, style);
		buildContent();

	}

	public CompositeVariable(Composite parent, int style, Variable variable) {
		super(parent, style);
		this.variable = variable;
		buildContent();
		fillDatas();
		view = (ViewVariable) adminbirep.Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewVariable.ID);
	}

	private void fillDatas() {
		if (variable == null) {
			return;
		}
		if (variable.getName() != null)
			name.setText(variable.getName());
		if (variable.getValue() != null)
			value.setText(variable.getValue());
		if (variable.getType() != null)
			cbType.select(variable.getType());
		if (variable.getSource() != null)
			source.setText(variable.getSource());
		if (variable.getScope() != null)
			cbScope.select(variable.getScope());
	}

	private void buildContent() {

		this.setLayout(new GridLayout(2, false));

		Label l1 = new Label(this, SWT.NONE);
		l1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l1.setText(Messages.CompositeVariable_0);

		name = new Text(this, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label l2 = new Label(this, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l2.setText(Messages.CompositeVariable_1);

		value = new Text(this, SWT.BORDER);
		value.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label l3 = new Label(this, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l3.setText(Messages.CompositeVariable_2);

		cbType = new Combo(this, SWT.READ_ONLY);
		cbType.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
		cbType.setItems(Variable.TYPE_NAMES);

		Label l0 = new Label(this, SWT.NONE);
		l0.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l0.setText(Messages.CompositeVariable_3);

		cbScope = new Combo(this, SWT.READ_ONLY);
		cbScope.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
		cbScope.setItems(Variable.SCOPE_NAMES);

		Label l4 = new Label(this, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l4.setText(Messages.CompositeVariable_4);

		source = new Text(this, SWT.BORDER);
		source.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		update = new Button(this, SWT.PUSH);
		update.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));
		update.setText(Messages.CompositeVariable_5);
		update.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				variable.setName(name.getText());
				variable.setValue(value.getText());
				variable.setType(cbType.getSelectionIndex());
				variable.setScope(cbScope.getSelectionIndex());
				variable.setSource(source.getText());
				try {
					adminbirep.Activator.getDefault().getVanillaApi().getVanillaSystemManager().updateVariable(variable);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				try {
					view.createInput();
					view.refresh();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		Composite c = new Composite(this, SWT.NONE);
		c.setLayout(new GridLayout(2, true));
		c.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

	}

	/**
	 * if the variable is null, create it else set its values with the fields
	 * values
	 * 
	 * @return
	 */
	public Variable getVariable() {
		if (variable == null) {
			variable = new Variable();
		}

		variable.setName(name.getText());
		variable.setSource(source.getText());
		variable.setType(cbType.getSelectionIndex());
		variable.setScope(cbScope.getSelectionIndex());
		variable.setValue(value.getText());

		return variable;
	}
}
