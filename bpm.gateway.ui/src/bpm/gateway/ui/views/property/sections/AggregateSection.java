package bpm.gateway.ui.views.property.sections;

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
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.transformations.AggregateTransformation;
import bpm.gateway.ui.composites.AggregateComposite;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class AggregateSection extends AbstractPropertySection {

	private Node node;
	private AggregateComposite composite;
	private Button skipNull, zeroNull;
	private SelectionAdapter btList = new SelectionAdapter() {

		@Override
		public void widgetSelected(SelectionEvent e) {
			if(skipNull.getSelection()) {
				((AggregateTransformation) node.getGatewayModel()).setNullMode(AggregateTransformation.MODE_SKIP);
			}
			else if(zeroNull.getSelection()) {
				((AggregateTransformation) node.getGatewayModel()).setNullMode(AggregateTransformation.MODE_ZERO);
			}
		}

	};

	public AggregateSection() {}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		Composite container = getWidgetFactory().createFlatFormComposite(parent);
		container.setLayout(new GridLayout());

		Group g = getWidgetFactory().createGroup(container, Messages.AggregateSection_0);
		g.setLayout(new GridLayout());
		g.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

		skipNull = getWidgetFactory().createButton(g, Messages.AggregateSection_1, SWT.RADIO);
		skipNull.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		skipNull.setToolTipText(Messages.AggregateSection_2);

		zeroNull = getWidgetFactory().createButton(g, Messages.AggregateSection_3, SWT.RADIO);
		zeroNull.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		zeroNull.setToolTipText(Messages.AggregateSection_4);

		composite = new AggregateComposite(container, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

	}

	@Override
	public void refresh() {
		AggregateTransformation agg = (AggregateTransformation) node.getGatewayModel();
		composite.setInputs(agg);

		if(agg.getNullMode() == AggregateTransformation.MODE_SKIP) {
			skipNull.setSelection(true);
			zeroNull.setSelection(false);
		}
		else {
			skipNull.setSelection(false);
			zeroNull.setSelection(true);
		}
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();
	}

	@Override
	public void aboutToBeHidden() {
		if (!skipNull.isDisposed()) {
			skipNull.removeSelectionListener(btList);
		}
		if (!zeroNull.isDisposed()) {
			zeroNull.removeSelectionListener(btList);
		}
		super.aboutToBeHidden();
	}

	@Override
	public void aboutToBeShown() {
		skipNull.addSelectionListener(btList);
		zeroNull.addSelectionListener(btList);
		super.aboutToBeShown();
	}

}
