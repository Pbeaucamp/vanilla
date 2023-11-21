package bpm.workflow.ui.views;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.resources.variables.ListVariable;
import bpm.workflow.runtime.resources.variables.Variable;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.dialogs.DialogVariable;
import bpm.workflow.ui.viewer.TreeLabelProvider;
import bpm.workflow.ui.viewer.TreeParent;
import bpm.workflow.ui.viewer.TreeStaticObject;

/**
 * View which displays the variables
 * 
 * @author CHARBONNIER, MARTIN
 * 
 */
public class VariablesViewPart extends ViewPart implements ITabbedPropertySheetPageContributor {
	public static final String ID = "bpm.workflow.ui.views.variables"; //$NON-NLS-1$
	protected static TreeParent root;
	protected static TreeStaticObject variables;
	private TreeViewer variablesViewer;

	public VariablesViewPart() {

	}

	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout());

		variablesViewer = new TreeViewer(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		variablesViewer.setContentProvider(new VariablesContentProvider());
		LabelProvider labelProvider = new TreeLabelProvider();

		variablesViewer.setLabelProvider(labelProvider);
		root = new TreeParent(""); //$NON-NLS-1$
		variables = new TreeStaticObject(Messages.VariablesViewPart_2);
		root.addChild(variables);

		variablesViewer.setInput(root);

		createActions();
		createContextMenu();

		getSite().setSelectionProvider(variablesViewer);

		variablesViewer.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				Object o = ((IStructuredSelection) variablesViewer.getSelection()).getFirstElement();
				if(o instanceof Variable) {

					DialogVariable dial = new DialogVariable(getSite().getShell(), (Variable) o);
					if(dial.open() == Dialog.OK) {
						try {
							Variable v = new Variable(dial.getProperties());
							ListVariable.getInstance().updateVariable(v);
						} catch(Exception e) {
							Shell shell = getSite().getShell();
							MessageDialog.openError(shell, Messages.VariablesViewPart_3, e.getMessage());
						}

						VariablesViewPart view = (VariablesViewPart) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(VariablesViewPart.ID);

						if(view != null) {
							view.refresh();
						}
					}

				}

			}

		});
	}

	private void createContextMenu() {

	}

	private void createActions() {

	}

	public Object getAdapter(Class adapter) {
		if(adapter == IPropertySheetPage.class)
			return new TabbedPropertySheetPage(this);
		return super.getAdapter(adapter);
	}

	@Override
	public void setFocus() {
		variablesViewer.getTree().setFocus();

	}

	/**
	 * refresh the TreeViewer
	 */
	public void refresh() {
		root = new TreeParent(""); //$NON-NLS-1$
		variables = new TreeStaticObject(Messages.VariablesViewPart_5);
		root.addChild(variables);

		variablesViewer.setInput(root);
		variablesViewer.refresh();

	}

	public String getContributorId() {
		return ID;
	}

	/**
	 * Delete a variable
	 */
	public void deleteVariable() {

		Object o = ((IStructuredSelection) variablesViewer.getSelection()).getFirstElement();

		if(o instanceof Variable) {
			((WorkflowModel) Activator.getDefault().getCurrentModel()).removeResource(((Variable) o).getId());
			ListVariable.getInstance().removeVariable((Variable) o);
			variablesViewer.refresh();
		}

	}

}
