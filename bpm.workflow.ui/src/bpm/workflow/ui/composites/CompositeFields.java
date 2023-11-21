package bpm.workflow.ui.composites;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import bpm.workflow.runtime.model.ICanFillVariable;
import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.resources.variables.ListVariable;
import bpm.workflow.runtime.resources.variables.Variable;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.dialogs.DialogVariable;
import bpm.workflow.ui.views.VariablesViewPart;

/**
 * Composite for the visualization of the fields of a form and create variables for them
 * 
 * @author CHARBONNIER, MARTIN
 * 
 */
public class CompositeFields extends Composite {
	private ICanFillVariable variableProvider;
	private CheckboxTableViewer cbFields;
	private CheckboxTableViewer allreadyUsed;

	/**
	 * Create a composite for the fields of a form
	 * 
	 * @param parent
	 *            : the parent composite
	 * @param style
	 */
	public CompositeFields(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout());
		this.setBackground(parent.getBackground());
		createContent(this);
	}

	/**
	 * Set the input form
	 * 
	 * @param form
	 */
	public void setActivity(ICanFillVariable form) {
		this.variableProvider = form;
	}

	private void createContent(CompositeFields parent) {

		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(1, false));
		c.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		c.setBackground(parent.getBackground());

		Button map = new Button(c, SWT.PUSH | SWT.FLAT);
		map.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		map.setText(Messages.CompositeFields_0);
		map.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				for(Object o : cbFields.getCheckedElements()) {
					if(ListVariable.getInstance().getVariable((String) o) == null) {
						createVariable((String) o);
					}
				}

			}

		});

		cbFields = CheckboxTableViewer.newCheckList(c, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		cbFields.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		cbFields.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				List<String> l = (List<String>) inputElement;
				return l.toArray(new String[l.size()]);
			}

			public void dispose() {}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

		});

		Label l2 = new Label(c, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false, 2, 1));
		l2.setText(Messages.CompositeFields_1);
		l2.setBackground(parent.getBackground());

		allreadyUsed = CheckboxTableViewer.newCheckList(c, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		allreadyUsed.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		allreadyUsed.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				List<String> l = (List<String>) inputElement;
				return l.toArray(new String[l.size()]);
			}

			public void dispose() {}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

		});

	}

	/**
	 * Create a variable with the specified name
	 * 
	 * @param name
	 */
	protected void createVariable(String o) {
		Variable v = new Variable();
		v.setName(o);
		DialogVariable dial = new DialogVariable(getShell(), v);

		if(dial.open() == Dialog.OK) {
			try {
				Variable var = new Variable(dial.getProperties());
				ListVariable.getInstance().addVariable(var);
				variableProvider.addParameterMapping(o, o);
				((WorkflowModel) Activator.getDefault().getCurrentModel()).addResource(var);
			} catch(Exception e) {
				MessageDialog.openError(getShell(), Messages.CompositeFields_2, e.getMessage());
			}

			VariablesViewPart view = (VariablesViewPart) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(VariablesViewPart.ID);

			if(view != null) {
				view.refresh();
			}
		}

	}

	public void fill() {
		List<String> candidat = new ArrayList<String>();
		List<String> allready = new ArrayList<String>();
		cbFields.setInput(candidat);
		allreadyUsed.setInput(allready);
		for(String s : variableProvider.getParameters(Activator.getDefault().getRepositoryConnection())) {
			if(ListVariable.getInstance().getVariable(s) == null) {
				candidat.add(s);
			}
			else {
				allready.add(s);
			}
		}
		cbFields.setInput(candidat);
		allreadyUsed.setInput(allready);
	}

}
