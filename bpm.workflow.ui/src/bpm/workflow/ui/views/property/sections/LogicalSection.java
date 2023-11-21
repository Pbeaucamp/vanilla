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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.workflow.runtime.model.activities.XorActivity;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

public class LogicalSection extends AbstractPropertySection {
	
	private XorActivity xorActivity;
	private Combo type;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);
		
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, false));
		
		CLabel lblCondition3 = getWidgetFactory().createCLabel(composite, Messages.LogicalSection_0);
		lblCondition3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		type = new Combo(composite, SWT.READ_ONLY);
		type.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		type.addSelectionListener(adaptder);

		type.setItems(XorActivity.TYPES);
	}
	
	@Override
	public void refresh() {
		type.setText(xorActivity.getType());
	}
	
	private SelectionAdapter adaptder = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			xorActivity.setType(type.getText());
		}
	};

	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        Node node = (Node)((NodePart) input).getModel();
        this.xorActivity = (XorActivity) node.getWorkflowObject();
	}
	
}
