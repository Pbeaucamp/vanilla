package bpm.workflow.ui.views.property.sections.inout;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.workflow.runtime.model.IAcceptInput;
import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.IOutput;
import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.model.activities.filemanagement.EncryptFileActivity;
import bpm.workflow.runtime.model.activities.reporting.ReportActivity;
import bpm.workflow.runtime.model.activities.vanilla.GedActivity;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

/**
 * Section for the concat PDF activity
 * 
 * @author Charles MARTIN
 * 
 */
public class Inputs extends AbstractPropertySection {
	public static Color mainBrown = new Color(Display.getDefault(), 209, 177, 129);
	public static Color secondBrown = new Color(Display.getDefault(), 238, 226, 208);

	private Node node;

	private CheckboxTableViewer tableViewer;
	private CLabel labelLabel;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, true));

		Composite c = getWidgetFactory().createFlatFormComposite(composite);
		c.setLayout(new GridLayout());
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		labelLabel = getWidgetFactory().createCLabel(c, ""); //$NON-NLS-1$
		labelLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, true, true, 1, 1));

		tableViewer = CheckboxTableViewer.newCheckList(c, SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		tableViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 2));
		tableViewer.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				List<IOutput> l = (List<IOutput>) inputElement;
				return l.toArray(new IOutput[l.size()]);
			}

			public void dispose() {

			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

		});
		tableViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((IOutput) element).getName();
			}
		});

		tableViewer.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event) {
				IOutput element = (IOutput) event.getElement();

				if(event.getChecked()) {
					String filePath = element.getOutputVariable();
					((IAcceptInput) node.getWorkflowObject()).addInput(filePath);

					if(element instanceof ReportActivity && node.getWorkflowObject() instanceof GedActivity) {
						((GedActivity) node.getWorkflowObject()).addItemIdForFile(((ReportActivity) element).getBiObject().getId(), element.getOutputVariable());
					}
					
					if(element instanceof EncryptFileActivity && tableViewer.getCheckedElements().length > 1) {
						MessageDialog.openError(getPart().getSite().getShell(), Messages.Inputs_0, Messages.Inputs_1);
						
						for(Object obj : tableViewer.getCheckedElements()) {
							if(obj != element) {
								tableViewer.setChecked(obj, false);
							}
						}
					}
				}
				else {
					String filePath = element.getOutputVariable();
					((IAcceptInput) node.getWorkflowObject()).removeInput(filePath);
				}

				try {
					List<String> toRm = new ArrayList<String>();
					for(String in : ((IAcceptInput) node.getWorkflowObject()).getInputs()) {
						boolean finded = false;
						for(Object o : (List) tableViewer.getInput()) {
							IOutput elem = (IOutput) o;
							if(elem.getOutputVariable().equals(in)) {
								finded = true;
								break;
							}
						}
						if(!finded) {
							toRm.add(in);
						}
					}
					for(String a : toRm) {
						((IAcceptInput) node.getWorkflowObject()).removeInput(a);
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}

		});

		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		tableViewer.getTable().setHeaderVisible(true);

	}

	@Override
	public void refresh() {
		IAcceptInput c = (IAcceptInput) node.getWorkflowObject();
		WorkflowModel model = (WorkflowModel) Activator.getDefault().getCurrentModel();
		labelLabel.setText(c.getPhrase());

		List<IOutput> providers = new ArrayList<IOutput>();
		for(IActivity a : model.getActivities().values()) {
			if(a instanceof IOutput && !a.equals(c))
				providers.add((IOutput) a);

		}

		tableViewer.setInput(providers);

		for(IOutput r : providers) {
			boolean check = false;
			for(String s : c.getInputs()) {
				if(s.equalsIgnoreCase(r.getOutputVariable())) {
					check = true;
					break;
				}
			}
			tableViewer.setChecked(r, check);

		}

	}

	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();

	}

}
