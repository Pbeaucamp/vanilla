package bpm.workflow.ui.views.property.sections.mail;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.IOutputProvider;
import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.model.activities.MailActivity;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

public class AttachmentSession extends AbstractPropertySection {
	private CheckboxTableViewer tableViewer;
	private Node node;

	public AttachmentSession() {}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new FillLayout(SWT.VERTICAL));

		Composite c = getWidgetFactory().createFlatFormComposite(composite);
		c.setLayout(new GridLayout(1, false));

		CLabel labelLabel = getWidgetFactory().createCLabel(c, Messages.AttachmentSession_0);
		labelLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, true, false, 1, 1));

		tableViewer = CheckboxTableViewer.newCheckList(c, SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		tableViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 2));
		tableViewer.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				List<IOutputProvider> l = (List<IOutputProvider>) inputElement;
				return l.toArray(new IOutputProvider[l.size()]);
			}

			public void dispose() {

			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

		});
		tableViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((IOutputProvider) element).getName();
			}
		});

		tableViewer.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event) {

				try {
					Object[] elements = tableViewer.getCheckedElements();

					((MailActivity) node.getWorkflowObject()).getInputs().clear();

					for(Object elem : elements) {

						IOutputProvider element = (IOutputProvider) elem;

						if(event.getChecked()) {
							((MailActivity) node.getWorkflowObject()).addInput(element.getOutputVariable());
						}
					}
					tableViewer.refresh();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}

		});

		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		tableViewer.getTable().setHeaderVisible(true);

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
	public void refresh() {
		WorkflowModel model = (WorkflowModel) Activator.getDefault().getCurrentModel();
		MailActivity ma = (MailActivity) node.getWorkflowObject();

		List<IOutputProvider> providers = new ArrayList<IOutputProvider>();
		for(IActivity a : model.getActivities().values()) {
			if(a instanceof IOutputProvider)
				providers.add((IOutputProvider) a);

		}
		tableViewer.setInput(providers);

		for(IOutputProvider r : providers) {
			boolean check = false;
			for(String s : ma.getInputs()) {
				if(s.equalsIgnoreCase(r.getOutputVariable())) {
					check = true;
					break;
				}
			}
			if(check) {
				tableViewer.setChecked(r, check);
			}
		}
	}

	@Override
	public void aboutToBeShown() {
		super.aboutToBeShown();

	}

	@Override
	public void aboutToBeHidden() {
		super.aboutToBeHidden();
	}

}
