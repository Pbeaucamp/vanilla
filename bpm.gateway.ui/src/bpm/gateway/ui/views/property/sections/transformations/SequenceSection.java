package bpm.gateway.ui.views.property.sections.transformations;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.transformations.Sequence;
import bpm.gateway.ui.ApplicationWorkbenchWindowAdvisor;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class SequenceSection extends AbstractPropertySection {

	protected Node node;

	private ComboViewer minFieldViewer;
	private Text /* min, */max, step, fieldName;
	// private Button isUnique;

	private ISelectionChangedListener cboListener = new ISelectionChangedListener() {

		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection ss = (IStructuredSelection) minFieldViewer.getSelection();

			if (ss.isEmpty()) {
				((Sequence) node.getGatewayModel()).setMinField("-1"); //$NON-NLS-1$
			}
			else {
				((Sequence) node.getGatewayModel()).setMinField((StreamElement) ss.getFirstElement());
			}
		}
	};

	private ModifyListener txtListener = new ModifyListener() {

		public void modifyText(ModifyEvent e) {
			Sequence tr = (Sequence) node.getGatewayModel();

			if (e.widget == fieldName) {
				tr.setFieldName(fieldName.getText());
			}
			else {

				String s = ((Text) e.widget).getText();

				try {
					Long.parseLong(s);
					((Text) e.widget).setBackground(null);

					// if (e.widget == min) {
					// tr.setMinValue(min.getText());
					// }
					if (e.widget == max) {
						tr.setMaxValue(max.getText());
					}
					else if (e.widget == step) {
						tr.setStep(step.getText());
					}

				} catch (Exception ex) {
					ColorRegistry reg = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
					((Text) e.widget).setBackground(reg.get(ApplicationWorkbenchWindowAdvisor.ERROR_COLOR_KEY));
				}
			}

		}

	};

	public SequenceSection() {

	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		parent.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, false));

		CLabel l = getWidgetFactory().createCLabel(composite, Messages.SequenceSection_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		fieldName = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		fieldName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		CLabel l1 = getWidgetFactory().createCLabel(composite, Messages.SequenceSection_2);
		l1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		//		min = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		// min.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true,
		// false));

		minFieldViewer = new ComboViewer(getWidgetFactory().createCCombo(composite, SWT.READ_ONLY));
		minFieldViewer.getCCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		minFieldViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((StreamElement) element).name;
			}
		});
		minFieldViewer.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			public void dispose() {
			}

			@SuppressWarnings({ "rawtypes", "unchecked" })
			public Object[] getElements(Object inputElement) {
				Collection c = (Collection) inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});

		CLabel l2 = getWidgetFactory().createCLabel(composite, Messages.SequenceSection_4);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		max = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		max.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		CLabel l3 = getWidgetFactory().createCLabel(composite, Messages.SequenceSection_6);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		step = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		step.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		// isUnique = getWidgetFactory().createButton(composite, "Is unique ID",
		// SWT.CHECK);
		// isUnique.setLayoutData(new GridData(GridData.FILL, GridData.CENTER,
		// true, false, 2, 1));
		//

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
		// min.removeModifyListener(txtListener);
		minFieldViewer.removeSelectionChangedListener(cboListener);
		max.removeModifyListener(txtListener);
		step.removeModifyListener(txtListener);
		fieldName.removeModifyListener(txtListener);

		Sequence tr = (Sequence) node.getGatewayModel();

		try {
			minFieldViewer.setInput(tr.getDescriptor(tr).getStreamElements());
			try {
				minFieldViewer.setSelection(new StructuredSelection(tr.getDescriptor(tr).getStreamElements().get(tr.getMinField())));
			} catch (Exception ex) {
				minFieldViewer.setSelection(new StructuredSelection());
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			minFieldViewer.setInput(new ArrayList<StreamElement>());
			minFieldViewer.setSelection(new StructuredSelection());
		}
		//		min.setText(tr.getMinValue() + ""); //$NON-NLS-1$
		max.setText(tr.getMaxValue() + ""); //$NON-NLS-1$
		step.setText(tr.getStep() + ""); //$NON-NLS-1$
		// isUnique.setSelection(tr.getUniqueId());
		fieldName.setText(tr.getFieldName());

		minFieldViewer.addSelectionChangedListener(cboListener);
		max.addModifyListener(txtListener);
		step.addModifyListener(txtListener);
		fieldName.addModifyListener(txtListener);

	}

	@Override
	public void aboutToBeHidden() {

		// isUnique.removeSelectionListener(btListener);

		super.aboutToBeHidden();
	}

	@Override
	public void aboutToBeShown() {

		// isUnique.addSelectionListener(btListener);

		super.aboutToBeShown();
	}

}
