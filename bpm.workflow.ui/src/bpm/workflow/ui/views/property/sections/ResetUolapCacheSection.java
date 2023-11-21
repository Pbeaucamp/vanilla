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

import bpm.workflow.runtime.model.activities.vanilla.ResetUolapCacheActivity;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

public class ResetUolapCacheSection extends AbstractPropertySection {

	private Button checkRebuild;
	private Node node;
	private ResetUolapCacheActivity activity;
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		checkRebuild = getWidgetFactory().createButton(parent, Messages.ResetUolapCacheSection_0, SWT.CHECK);
		checkRebuild.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		checkRebuild.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean rebuild = checkRebuild.getSelection();
				activity.setReloadCache(rebuild);
			}
		});
	}
	
	@Override
	public void refresh() {
		if(checkRebuild != null) {
			checkRebuild.setSelection(activity.isReloadCache());
		}
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.node = (Node)((NodePart) input).getModel();
        Assert.isTrue(node.getWorkflowObject() instanceof ResetUolapCacheActivity);
        this.activity = (ResetUolapCacheActivity)node.getWorkflowObject();
	}
}
