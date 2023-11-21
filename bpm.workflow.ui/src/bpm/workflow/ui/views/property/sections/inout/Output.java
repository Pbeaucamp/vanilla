package bpm.workflow.ui.views.property.sections.inout;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.workflow.runtime.model.IOutputProvider;
import bpm.workflow.runtime.resources.variables.ListVariable;
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
public class Output extends AbstractPropertySection {
	public static Color mainBrown = new Color(Display.getDefault(), 209, 177, 129);
	public static Color secondBrown = new Color(Display.getDefault(), 238, 226, 208);

	private Node node;

	private Combo vanillaPaths;
	private Text outputName;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, true));

		CLabel lbl = getWidgetFactory().createCLabel(composite, Messages.Output_0);
		lbl.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		vanillaPaths = new Combo(composite, SWT.READ_ONLY);
		vanillaPaths.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));
		vanillaPaths.setItems(ListVariable.VANILLA_PATHS);
		vanillaPaths.addSelectionListener(adaptder);

		CLabel nameLabel = getWidgetFactory().createCLabel(composite, Messages.Output_1);
		nameLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		outputName = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		outputName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

	}

	@Override
	public void aboutToBeShown() {
		outputName.addModifyListener(modify);

		super.aboutToBeShown();

	}

	@Override
	public void aboutToBeHidden() {
		outputName.removeModifyListener(modify);
		super.aboutToBeHidden();
	}

	@Override
	public void refresh() {
		IOutputProvider c = (IOutputProvider) node.getWorkflowObject();

		int selectTo = -1;
		if(c.getPathToStore() != null) {
			for(String s : vanillaPaths.getItems()) {
				selectTo++;
				if(s.equalsIgnoreCase(c.getPathToStore()))
					break;
			}
		}
		vanillaPaths.select(selectTo);

		if(c.getOutputName() != null) {
			outputName.setText(c.getOutputName());
		}

		new ContentProposalAdapter(outputName, new TextContentAdapter(), new SimpleContentProposalProvider(ListVariable.getInstance().getNoEnvironementVariable()), Activator.getDefault().getKeyStroke(), Activator.getDefault().getAutoActivationCharacters());

	}

	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();
		refresh();
	}

	private ModifyListener modify = new ModifyListener() {

		public void modifyText(ModifyEvent e) {
			((IOutputProvider) node.getWorkflowObject()).setOutputName(outputName.getText());

		}
	};

	SelectionAdapter adaptder = new SelectionAdapter() {

		@Override
		public void widgetSelected(SelectionEvent e) {
			((IOutputProvider) node.getWorkflowObject()).setPathToStore(vanillaPaths.getText());
		}

	};

}
