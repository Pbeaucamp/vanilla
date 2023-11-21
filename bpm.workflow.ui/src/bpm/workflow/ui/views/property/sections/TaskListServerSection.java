package bpm.workflow.ui.views.property.sections;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.workflow.runtime.model.activities.TaskListActivity;
import bpm.workflow.runtime.model.activities.TaskListType;
import bpm.workflow.runtime.resources.TaskListObject;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.dialogs.DialogRepositoryObject;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

public class TaskListServerSection extends AbstractPropertySection {

	private Text repositoryItem;
	private TaskListActivity activity;
	private Button reportType, gatewayType;

	private SelectionAdapter typeListener;

	public TaskListServerSection() {}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		final Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(3, false));

		Group group = getWidgetFactory().createGroup(parent, Messages.TaskListServerSection_0);
		group.setLayout(new GridLayout());

		reportType = getWidgetFactory().createButton(group, Messages.TaskListServerSection_1, SWT.RADIO);
		reportType.setLayoutData(new GridData());
		reportType.setSelection(true);

		gatewayType = getWidgetFactory().createButton(group, Messages.TaskListServerSection_2, SWT.RADIO);
		gatewayType.setLayoutData(new GridData());

		CLabel label = getWidgetFactory().createCLabel(composite, Messages.TaskListServerSection_3);
		label.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		repositoryItem = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		repositoryItem.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		repositoryItem.setEnabled(false);

		Button browse = getWidgetFactory().createButton(composite, "...", SWT.PUSH); //$NON-NLS-1$
		browse.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		browse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// if (activity.getServer() == null){
				// MessageDialog.openInformation(getPart().getSite().getShell(), "Missing setting", "Select a Repository server first");
				// return;
				// }

				DialogRepositoryObject dial = new DialogRepositoryObject(getPart().getSite().getShell(), activity.getType());
				if(dial.open() == DialogRepositoryObject.OK) {
					RepositoryItem it = dial.getRepositoryItem();
					TaskListObject o = new TaskListObject(it.getId(), activity.getType());
					o.setItemName(it.getItemName());
					activity.setTaskListObject(o);
					repositoryItem.setText(it.getItemName());
				}

			}
		});

		typeListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(reportType.getSelection()) {
					activity.setType(TaskListType.REPORT.name());
				}
				else {
					activity.setType(TaskListType.GATEWAY.name());
				}
			}
		};
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.activity = (TaskListActivity) ((Node) ((NodePart) input).getModel()).getWorkflowObject();
	}

	@Override
	public void refresh() {

		reportType.removeSelectionListener(typeListener);
		gatewayType.removeSelectionListener(typeListener);
		/*
		 * get RepositoryServers
		 */
		// List<Server> l = ListServer.getInstance().getServers(BiRepository.class);
		// repositoryServers.setInput(l);
		if(activity.getType() == TaskListType.GATEWAY) {
			reportType.setSelection(false);
			gatewayType.setSelection(true);
		}
		else {
			reportType.setSelection(true);
			gatewayType.setSelection(false);
		}
		// if (activity.getServer() != null){
		// // repositoryServers.setSelection(new StructuredSelection(activity.getServer()));
		//			
		// if (activity.getTaskListObject() != null){
		// IDirectoryItem it;
		// try {
		// it = Activator.getDefault().getRepository().getItem(activity.getTaskListObject().getId());
		// repositoryItem.setText(it.getName());
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// }
		//		
		//			
		// }
		//		

		// repositoryServers.addSelectionChangedListener(listener);
		reportType.addSelectionListener(typeListener);
		gatewayType.addSelectionListener(typeListener);
	}
}
