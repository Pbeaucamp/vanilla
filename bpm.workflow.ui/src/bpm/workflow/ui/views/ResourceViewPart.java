package bpm.workflow.ui.views;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.model.WorkfowModelParameter;
import bpm.workflow.runtime.resources.Parameter;
import bpm.workflow.runtime.resources.servers.ListServer;
import bpm.workflow.runtime.resources.servers.Server;
import bpm.workflow.runtime.resources.variables.ListVariable;
import bpm.workflow.runtime.resources.variables.Variable;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.dialogs.DialogParameter;
import bpm.workflow.ui.dialogs.DialogVariable;
import bpm.workflow.ui.viewer.TreeLabelProvider;

/**
 * View which displays the resources of the current workflow model
 * 
 * @author CHARBONNIER, MARTIN
 * 
 */
public class ResourceViewPart extends ViewPart implements ITabbedPropertySheetPageContributor {

	public static final String ID = "bpm.workflow.ui.views.resources"; //$NON-NLS-1$

	public static final Font FONT_ACTIVE_CONNECTION = new Font(Display.getCurrent(), "Arial", 10, SWT.BOLD | SWT.ITALIC); //$NON-NLS-1$

	private TreeViewer resourcesViewer;

	public ResourceViewPart() {

	}

	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout());

		resourcesViewer = new TreeViewer(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		resourcesViewer.setContentProvider(new ResourcesContentProvider());
		LabelProvider labelProvider = new TreeLabelProvider();

		resourcesViewer.setLabelProvider(labelProvider);
		ResourceViewHelper.createTree(resourcesViewer);

		getSite().setSelectionProvider(resourcesViewer);

		resourcesViewer.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				Object o = ((IStructuredSelection) resourcesViewer.getSelection()).getFirstElement();
				if(o instanceof Variable) {

					DialogVariable dial = new DialogVariable(getSite().getShell(), (Variable) o);
					if(dial.open() == Dialog.OK) {
						try {
							Variable v = new Variable(dial.getProperties());
							ListVariable.getInstance().updateVariable(v);
						} catch(Exception e) {
							Shell shell = getSite().getShell();
							MessageDialog.openError(shell, Messages.ResourceViewPart_2, e.getMessage());
						}

						VariablesViewPart view = (VariablesViewPart) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(VariablesViewPart.ID);

						if(view != null) {
							view.refresh();
						}
					}

				}
				else if(o instanceof WorkfowModelParameter) {
					DialogParameter dial = new DialogParameter(getSite().getShell(), (WorkfowModelParameter) o);
					if(dial.open() == Dialog.OK) {
						ResourceViewPart view = (ResourceViewPart)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ResourceViewPart.ID);
						
						if (view != null){
							view.refresh();
						}
					}
				}

			}

		});
	}

	public Object getAdapter(Class adapter) {
		if(adapter == IPropertySheetPage.class)
			return new TabbedPropertySheetPage(this);
		return super.getAdapter(adapter);
	}

	@Override
	public void setFocus() {
		resourcesViewer.getTree().setFocus();

	}

	/**
	 * refresh the TreeViewer
	 */
	public void refresh() {
		resourcesViewer.refresh();

	}

	/**
	 * Delete a server
	 */
	public void deleteServer() {
		Object o = ((IStructuredSelection) resourcesViewer.getSelection()).getFirstElement();

		if(o instanceof Server) {
			ListServer.getInstance().removeServer((Server) o);
			((WorkflowModel) Activator.getDefault().getCurrentModel()).removeResource(((Server) o).getId());
			resourcesViewer.refresh();
			refresh();
		}
	}

	public void deleteParameter() {
		Object o = ((IStructuredSelection) resourcesViewer.getSelection()).getFirstElement();

		if(o instanceof WorkfowModelParameter) {
			((WorkflowModel) Activator.getDefault().getCurrentModel()).removeParameter((WorkfowModelParameter) o);
			resourcesViewer.refresh();
			refresh();
		}
	}

	/**
	 * Delete a variable
	 */
	public void deleteVariable() {

		Object o = ((IStructuredSelection) resourcesViewer.getSelection()).getFirstElement();

		if(o instanceof Variable) {
			Variable variable = (Variable) o;
			if(variable.getName().equalsIgnoreCase("{$VANILLA_HOME}") //$NON-NLS-1$
					|| variable.getName().equalsIgnoreCase("{$VANILLA_FILES}") //$NON-NLS-1$
					|| variable.getName().equalsIgnoreCase("E_TIME") //$NON-NLS-1$
					|| variable.getName().equalsIgnoreCase("E_IP")) { //$NON-NLS-1$

			}
			else {
				ListVariable.getInstance().removeVariable((Variable) o);
				((WorkflowModel) Activator.getDefault().getCurrentModel()).removeResource(((Variable) o).getId());
				resourcesViewer.refresh();
				refresh();
			}
		}

	}

	public String getContributorId() {
		return ID;
	}
}
