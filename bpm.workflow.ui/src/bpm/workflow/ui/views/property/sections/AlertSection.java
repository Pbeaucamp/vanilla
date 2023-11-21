package bpm.workflow.ui.views.property.sections;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.part.NodePart;

public class AlertSection extends AbstractPropertySection {
	private Composite composite;
	private ComboViewer alerts;

	public AlertSection() {}

	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(3, false));

		CLabel lblAlert = getWidgetFactory().createCLabel(composite, Messages.AlertSection_0);
		lblAlert.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		alerts = new ComboViewer(composite, SWT.READ_ONLY);
		alerts.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		alerts.setLabelProvider(new LabelProvider() {

			public String getText(Object element) {
				return super.getText(element);
			}

		});

		alerts.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

			public void dispose() {}

			public Object[] getElements(Object inputElement) {
				return null;
			}
		});
		alerts.addSelectionChangedListener(alertChangeListener);

		Button btnEditLeft = new Button(composite, SWT.PUSH);
		btnEditLeft.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));

		btnEditLeft.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {

			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
	}

	public void refresh() {

		refreshAlerts();

	}

	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
	}

	protected void refreshAlerts() {

	}

	ISelectionChangedListener alertChangeListener = new ISelectionChangedListener() {

		public void selectionChanged(SelectionChangedEvent event) {

		}
	};
}
