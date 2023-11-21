package bpm.workflow.ui.views.property.sections;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.workflow.runtime.model.activities.vanilla.StartStopItemActivity;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

public class StartStopSection extends AbstractPropertySection {

	private Button checkStart;
	private Node node;
	private StartStopItemActivity activity;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		checkStart = getWidgetFactory().createButton(parent, Messages.StartStopSection_0, SWT.CHECK);
		checkStart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		checkStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean start = checkStart.getSelection();
				activity.setStart(start);
			}
		});
	}

	@Override
	public void refresh() {
		if(checkStart != null) {
			checkStart.setSelection(activity.isStart());
		}
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();
		Assert.isTrue(node.getWorkflowObject() instanceof StartStopItemActivity);
		this.activity = (StartStopItemActivity) node.getWorkflowObject();
	}
}
