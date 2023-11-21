package bpm.gateway.ui.tools.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;

import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.ui.i18n.Messages;

public class DialogPickupConstant extends Dialog {

	private Combo variableType;
	private TableViewer tableViewer;
	private Variable output;

	public DialogPickupConstant(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label l = new Label(container, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DialogPickupConstant_0);

		variableType = new Combo(container, SWT.READ_ONLY);
		variableType.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		tableViewer = new TableViewer(container, SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION);
		tableViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		tableViewer.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				List<Variable> l = (List<Variable>) inputElement;
				return l.toArray(new Variable[l.size()]);
			}

			public void dispose() {

			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

		});

		tableViewer.setLabelProvider(new ITableLabelProvider() {

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				Variable v = (Variable) element;

				switch(columnIndex) {
					case 0:
						return v.getOuputName();
					case 1:
						return v.getName();
					case 2:
						return Variable.VARIABLES_TYPES[v.getType()];
					case 3:
						return v.getValueAsString();
				}
				return ""; //$NON-NLS-1$
			}

			public void addListener(ILabelProviderListener listener) {

			}

			public void dispose() {

			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {

			}

		});

		TableColumn output = new TableColumn(tableViewer.getTable(), SWT.NONE);;
		output.setText(Messages.DialogPickupConstant_2);
		output.setWidth(200);

		TableColumn name = new TableColumn(tableViewer.getTable(), SWT.NONE);;
		name.setText(Messages.DialogPickupConstant_3);
		name.setWidth(200);

		TableColumn type = new TableColumn(tableViewer.getTable(), SWT.NONE);;
		type.setText(Messages.DialogPickupConstant_4);
		type.setWidth(200);

		TableColumn value = new TableColumn(tableViewer.getTable(), SWT.NONE);;
		value.setText(Messages.DialogPickupConstant_5);
		value.setWidth(200);

		tableViewer.getTable().setHeaderVisible(true);

		return container;
	}

	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogPickupConstant_6);

		getShell().setSize(600, 300);
		tableViewer.setInput(ResourceManager.getInstance().getVariablesAndParameters());
	}

	@Override
	protected void okPressed() {
		output = (Variable) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement();
		super.okPressed();
	}

	public Variable getVariable() {
		return output;
	}

}
