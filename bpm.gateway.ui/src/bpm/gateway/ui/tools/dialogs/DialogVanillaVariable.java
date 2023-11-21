package bpm.gateway.ui.tools.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;

import bpm.gateway.ui.Activator;
import bpm.gateway.ui.i18n.Messages;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Variable;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class DialogVanillaVariable extends Dialog {

	private TableViewer viewer;

	private Variable selection;

	public DialogVanillaVariable(Shell shell) {
		super(shell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		viewer = new TableViewer(parent, SWT.V_SCROLL | SWT.FULL_SELECTION);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				List<Variable> l = (List<Variable>) inputElement;
				return l.toArray(new Variable[l.size()]);
			}

			public void dispose() {

			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

		});

		viewer.setLabelProvider(new ITableLabelProvider() {

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				switch(columnIndex) {
					case 0:
						return ((Variable) element).getName();
					case 1:
						return ((Variable) element).getTypeName();
					case 2:
						return ((Variable) element).getValue();
				}
				return null;
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
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				getButton(IDialogConstants.OK_ID).setEnabled(!viewer.getSelection().isEmpty());

			}

		});

		TableColumn name = new TableColumn(viewer.getTable(), SWT.NONE);
		name.setText(Messages.DialogVanillaVariable_0);
		name.setWidth(200);

		TableColumn type = new TableColumn(viewer.getTable(), SWT.NONE);
		type.setText(Messages.DialogVanillaVariable_1);
		type.setWidth(200);

		TableColumn value = new TableColumn(viewer.getTable(), SWT.NONE);
		value.setText(Messages.DialogVanillaVariable_2);
		value.setWidth(200);

		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);

		return viewer.getControl();
	}

	@Override
	protected void okPressed() {
		IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
		selection = (Variable) ss.getFirstElement();

		super.okPressed();
	}

	public Variable getVariable() {
		return selection;
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 400);

		getShell().setText(Messages.DialogVanillaVariable_3);

		try {
			IVanillaContext c = Activator.getDefault().getVanillaContext();
			if(c == null) {
				close();
			}
			IVanillaAPI api = new RemoteVanillaPlatform(c);
			viewer.setInput(api.getVanillaSystemManager().getVariables());

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
