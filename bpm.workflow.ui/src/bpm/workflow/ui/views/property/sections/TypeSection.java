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

import bpm.workflow.runtime.model.activities.Comment;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

/**
 * Section for the selection of the type concerning the node
 * 
 * @author CHARBONNIER, MARTIN
 * 
 */
public class TypeSection extends AbstractPropertySection {
	private Node node;
	private Combo type;

	public TypeSection() {}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		final Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, false));

		CLabel lblType = getWidgetFactory().createCLabel(composite, Messages.TypeSection_0);
		lblType.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		type = new Combo(composite, SWT.READ_ONLY);
		type.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		type.setItems(Comment.TYPES_NAME);

	}

	@Override
	public void refresh() {

		type.select(((Comment) node.getWorkflowObject()).getTypeInt());

	}

	@Override
	public void aboutToBeShown() {
		if(!type.isListening(SWT.Selection))
			type.addSelectionListener(adapter);

		super.aboutToBeShown();

	}

	@Override
	public void aboutToBeHidden() {
		type.removeSelectionListener(adapter);
		super.aboutToBeHidden();
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();
	}

	private SelectionAdapter adapter = new SelectionAdapter() {

		@Override
		public void widgetSelected(SelectionEvent e) {
			node.setType(type.getSelectionIndex());
		}
	};

}
