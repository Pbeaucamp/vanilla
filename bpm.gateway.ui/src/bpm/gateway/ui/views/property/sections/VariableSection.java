package bpm.gateway.ui.views.property.sections;

import java.text.SimpleDateFormat;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.ui.i18n.Messages;

public class VariableSection extends AbstractPropertySection {

	// private static Color errorColor =

	private Button intBt, floatBt, dateBt, booleanBt, stringBt;
	private Text valueTxt, scope, name;
	private Variable variable;

	public VariableSection() {

	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, false));

		Label l = getWidgetFactory().createLabel(composite, Messages.VariableSection_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		name = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		name.setEnabled(false);

		Label l2 = getWidgetFactory().createLabel(composite, Messages.VariableSection_2);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		scope = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		scope.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		scope.setEnabled(false);

		Group group = getWidgetFactory().createGroup(composite, Messages.VariableSection_4);
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		intBt = getWidgetFactory().createButton(group, "Integer", SWT.RADIO); //$NON-NLS-1$
		intBt.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		intBt.addSelectionListener(listener);

		floatBt = getWidgetFactory().createButton(group, "Float", SWT.RADIO); //$NON-NLS-1$
		floatBt.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		floatBt.addSelectionListener(listener);

		dateBt = getWidgetFactory().createButton(group, "Date", SWT.RADIO); //$NON-NLS-1$
		dateBt.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		dateBt.addSelectionListener(listener);

		booleanBt = getWidgetFactory().createButton(group, "Boolean", SWT.RADIO); //$NON-NLS-1$
		booleanBt.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		booleanBt.addSelectionListener(listener);

		stringBt = getWidgetFactory().createButton(group, "String", SWT.RADIO); //$NON-NLS-1$
		stringBt.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		stringBt.addSelectionListener(listener);

		Composite valueBox = getWidgetFactory().createComposite(composite);
		valueBox.setLayout(new GridLayout(3, false));
		valueBox.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		CLabel labelLabel = getWidgetFactory().createCLabel(valueBox, Messages.VariableSection_10);
		labelLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		valueTxt = getWidgetFactory().createText(valueBox, ""); //$NON-NLS-1$
		valueTxt.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		valueTxt.addModifyListener(txtListener);

		Button testValue = getWidgetFactory().createButton(valueBox, Messages.VariableSection_11, SWT.PUSH);
		testValue.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		testValue.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ColorRegistry registry = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
				try {
					if (dateBt.getSelection()) {

						InputDialog dial = new InputDialog(getPart().getSite().getShell(), Messages.VariableSection_12, Messages.VariableSection_13, "yyyy-MM-dd", null); //$NON-NLS-1$
						if (dial.open() == Dialog.OK) {
							SimpleDateFormat sdf = new SimpleDateFormat(dial.getValue());
							sdf.parse(valueTxt.getText());
						}
					}
					else if (intBt.getSelection()) {
						Integer.parseInt(valueTxt.getText());
					}
					else if (floatBt.getSelection()) {
						Float.parseFloat(valueTxt.getText());
					}
					else if (booleanBt.getSelection()) {
						Boolean.parseBoolean(valueTxt.getText());
					}

					MessageDialog.openInformation(getPart().getSite().getShell(), Messages.VariableSection_15, Messages.VariableSection_16);
					valueTxt.setBackground(registry.get("bpm.gateway.ui.colors.standard")); //$NON-NLS-1$
				} catch (Exception ex) {
					MessageDialog.openWarning(getPart().getSite().getShell(), Messages.VariableSection_18, Messages.VariableSection_19);

					valueTxt.setBackground(registry.get("bpm.gateway.ui.colors.errorfield")); //$NON-NLS-1$
				}

			}

		});

	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof Variable);
		this.variable = ((Variable) input);
	}

	@Override
	public void refresh() {

		name.setText(variable.getName());
		scope.setText(Variable.VARIABLES_NAME[variable.getScope()]);
		valueTxt.removeModifyListener(txtListener);
		try {
			valueTxt.setText(variable.getValueAsString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		valueTxt.addModifyListener(txtListener);

		int type = variable.getType();

		selectButton(type);

	}

	private void selectButton(int type) {
		switch (type) {
		case Variable.DATE:
			intBt.setSelection(false);
			floatBt.setSelection(false);
			stringBt.setSelection(false);
			dateBt.setSelection(true);
			booleanBt.setSelection(false);
			break;

		case Variable.FLOAT:
			intBt.setSelection(false);
			floatBt.setSelection(true);
			stringBt.setSelection(false);
			dateBt.setSelection(false);
			booleanBt.setSelection(false);
			break;
		case Variable.INTEGER:
			intBt.setSelection(true);
			floatBt.setSelection(false);
			stringBt.setSelection(false);
			dateBt.setSelection(false);
			booleanBt.setSelection(false);
			break;
		case Variable.STRING:
			intBt.setSelection(false);
			floatBt.setSelection(false);
			stringBt.setSelection(true);
			dateBt.setSelection(false);
			booleanBt.setSelection(false);
			break;
		case Variable.BOOLEAN:
			intBt.setSelection(false);
			floatBt.setSelection(false);
			stringBt.setSelection(false);
			dateBt.setSelection(false);
			booleanBt.setSelection(true);
			break;

		}
	}

	private SelectionListener listener = new SelectionAdapter() {

		@Override
		public void widgetSelected(SelectionEvent e) {
			Button button = (Button) e.getSource();

			int type = 0;
			if (button == intBt) {
				type = Variable.INTEGER;
			}
			else if (button == floatBt) {
				type = Variable.FLOAT;
			}
			else if (button == dateBt) {
				type = Variable.DATE;
			}
			else if (button == booleanBt) {
				type = Variable.BOOLEAN;
			}
			else if (button == stringBt) {
				type = Variable.STRING;
			}
			// VariableProperties properties = (VariableProperties)
			// node.getAdapter(IPropertySource.class);
			// properties.setPropertyValue(VariableProperties.PROPERTY_TYPE,
			// type);

		}

	};

	private ModifyListener txtListener = new ModifyListener() {

		public void modifyText(ModifyEvent evt) {

			if (evt.widget == valueTxt) {
				variable.setValue(valueTxt.getText());

			}

		}
	};
}
