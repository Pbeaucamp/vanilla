package bpm.workflow.ui.views.property.sections;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.IParameters;
import bpm.workflow.runtime.model.activities.InterfaceActivity;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.composites.CompositeFormMapping;
import bpm.workflow.ui.composites.CompositeVariablesMapping;
import bpm.workflow.ui.gef.model.Link;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

/**
 * Section for the mappings between the forms, variables and objects which contain parameters
 * 
 * @author CHARBONNIER, MARTIN
 * 
 */
public class MappingSection extends AbstractPropertySection {
	private CompositeFormMapping composite;
	private Node node;

	private CompositeVariablesMapping compositeVar;

	private Label labelMissingInput;

	public MappingSection() {}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		Composite container = getWidgetFactory().createFlatFormComposite(parent);
		container.setLayout(new GridLayout());

		labelMissingInput = getWidgetFactory().createLabel(container, Messages.MappingSection_0);
		labelMissingInput.setLayoutData(new GridData());

		compositeVar = new CompositeVariablesMapping(container, SWT.NONE);
		compositeVar.setLayoutData(new GridData(GridData.FILL_BOTH));
		compositeVar.setVisible(false);

		composite = new CompositeFormMapping(container, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setVisible(false);

	}

	@Override
	public void refresh() {
		if(((IActivity) node.getWorkflowObject()).getSources().size() != 0) {

			if(((IActivity) node.getWorkflowObject()).getSources().get(0) instanceof InterfaceActivity) {

				InterfaceActivity i = null;
				for(Link l : node.getTargetLink()) {
					if(l.getSource().getWorkflowObject() instanceof InterfaceActivity) {
						i = (InterfaceActivity) l.getSource().getWorkflowObject();
					}
				}

				if(i != null) {
					composite.setInputs(i, (IParameters) node.getWorkflowObject());
					try {
						composite.fill();
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
				composite.setVisible(true);
				compositeVar.setVisible(false);
				labelMissingInput.setVisible(false);
			}
			else {
				composite.setVisible(false);
				compositeVar.setVisible(true);
				labelMissingInput.setVisible(false);
				compositeVar.setInputs((IParameters) node.getWorkflowObject());
				try {
					compositeVar.fill();

				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		else {
			labelMissingInput.setVisible(true);
			composite.setVisible(false);
			compositeVar.setVisible(false);
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
		if(node.getWorkflowObject() instanceof IActivity) {
			if(((IActivity) node.getWorkflowObject()).getSources().size() != 0) {

				if(((IActivity) node.getWorkflowObject()).getSources().get(0) instanceof InterfaceActivity) {
					composite.removeListners();
				}
			}
			compositeVar.removeListners();
		}
		super.aboutToBeHidden();
	}

	@Override
	public void aboutToBeShown() {
		if(node.getWorkflowObject() instanceof IActivity) {
			if(((IActivity) node.getWorkflowObject()).getSources().size() != 0) {

				if(((IActivity) node.getWorkflowObject()).getSources().get(0) instanceof InterfaceActivity) {
					composite.addListners();
				}
			}
			compositeVar.addListners();
		}
		super.aboutToBeShown();
	}
}
