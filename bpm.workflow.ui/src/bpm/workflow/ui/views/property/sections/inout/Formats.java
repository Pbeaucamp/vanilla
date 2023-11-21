package bpm.workflow.ui.views.property.sections.inout;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.workflow.runtime.model.IReport;
import bpm.workflow.runtime.model.activities.ExcelAggregateActivity;
import bpm.workflow.runtime.model.activities.reporting.ReportActivity;
import bpm.workflow.runtime.resources.ReportObject;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

/**
 * Section for the output definition : name and type
 * 
 * @author CHARBONNIER, MARTIN
 * 
 */
public class Formats extends AbstractPropertySection {
	private Node node;
	private List format;

	public Formats() {}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		final Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, false));

		CLabel formatLabel = getWidgetFactory().createCLabel(composite, Messages.Formats_0);
		formatLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));

		format = new List(composite, SWT.READ_ONLY | SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		format.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));
		format.setItems(ReportObject.OUTPUT_FORMATS);

	}

	@Override
	public void refresh() {

		if(((IReport) node.getWorkflowObject()).getOutputFormats().size() > 0) {
			int[] a = new int[((IReport) node.getWorkflowObject()).getOutputFormats().size()];
			for(int i = 0; i < ((IReport) node.getWorkflowObject()).getOutputFormats().size(); i++) {
				a[i] = ((IReport) node.getWorkflowObject()).getOutputFormats().get(i);
			}
			format.select(a);
		}
		else {
			format.deselectAll();
		}

	}

	@Override
	public void aboutToBeShown() {
		format.addSelectionListener(formatadapter);

		super.aboutToBeShown();

	}

	@Override
	public void aboutToBeHidden() {
		format.removeSelectionListener(formatadapter);

		super.aboutToBeHidden();
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();

		refresh();
	}

	private SelectionAdapter formatadapter = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {

			if(node.getWorkflowObject() instanceof ReportActivity) {
				((IReport) node.getWorkflowObject()).setOutputFormats(format.getSelectionIndices());

				if(((ReportActivity) node.getWorkflowObject()).getTargets().size() != 0) {

					if(((ReportActivity) node.getWorkflowObject()).getTargets().get(0) instanceof ExcelAggregateActivity) {

						if(((ReportActivity) node.getWorkflowObject()).getOutputFormats().contains(2)) {
							ReportActivity r = (ReportActivity) node.getWorkflowObject();
							String valueoutput = ((ReportActivity) node.getWorkflowObject()).getOutputName();
							String valuemapping = ((ExcelAggregateActivity) ((ReportActivity) node.getWorkflowObject()).getTargets().get(0)).getNameTabs().get(valueoutput);
							if(valuemapping == null) {
								((ExcelAggregateActivity) ((ReportActivity) node.getWorkflowObject()).getTargets().get(0)).addMapping(r.getName(), r.getName());
							}
							else {
								((ExcelAggregateActivity) ((ReportActivity) node.getWorkflowObject()).getTargets().get(0)).removeMapping(valueoutput);

								((ExcelAggregateActivity) ((ReportActivity) node.getWorkflowObject()).getTargets().get(0)).addMapping(r.getName(), valuemapping);
							}
						}
					}

				}

			}

		}
	};

}
