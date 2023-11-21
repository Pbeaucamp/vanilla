package bpm.workflow.ui.views.property.sections;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.model.activities.PingHostActivity;
import bpm.workflow.runtime.resources.variables.ListVariable;
import bpm.workflow.runtime.resources.variables.Variable;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

/**
 * Section for the ping host activity
 * 
 * @author Charles MARTIN
 * 
 */
public class PingHostSection extends AbstractPropertySection {
	public static Color mainBrown = new Color(Display.getDefault(), 209, 177, 129);
	public static Color secondBrown = new Color(Display.getDefault(), 238, 226, 208);

	private Text select;

	private Node node;
	private Button validPath;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(new GridLayout());

		org.eclipse.swt.widgets.Group general = new org.eclipse.swt.widgets.Group(parent, SWT.NONE);
		general.setLayout(new GridLayout(3, false));
		general.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		general.setText(Messages.PingHostSection_0);

		CLabel labelcom = getWidgetFactory().createCLabel(general, Messages.PingHostSection_1);
		labelcom.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		select = getWidgetFactory().createText(general, ""); //$NON-NLS-1$
		select.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		validPath = new Button(general, SWT.PUSH);
		validPath.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		validPath.setText(Messages.PingHostSection_2);

		validPath.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				((PingHostActivity) node.getWorkflowObject()).setPath(select.getText());
				if(select.getText().contains("{$")) { //$NON-NLS-1$
					String finalstring = new String(select.getText());

					List<String> varsString = new ArrayList<String>();
					for(Variable variable : ListVariable.getInstance().getVariables()) {
						varsString.add(variable.getName());
					}
					for(String nomvar : varsString) {
						String toto = finalstring.replace("{$" + nomvar + "}", ListVariable.getInstance().getVariable(nomvar).getValues().get(0)); //$NON-NLS-1$ //$NON-NLS-2$
						if(!toto.equalsIgnoreCase(finalstring)) {
							((WorkflowModel) Activator.getDefault().getCurrentModel()).addResource(ListVariable.getInstance().getVariable(nomvar));

						}

						finalstring = toto;
					}

				}
			}

		});

	}

	@Override
	public void refresh() {

		if(((PingHostActivity) node.getWorkflowObject()).getPath() != null) {

			select.setText(((PingHostActivity) node.getWorkflowObject()).getPath());
		}

		String[] listeVar = new String[ListVariable.getInstance().getArray((WorkflowModel) Activator.getDefault().getCurrentModel()).length - 2];
		int i = 0;
		for(String string : ListVariable.getInstance().getArray((WorkflowModel) Activator.getDefault().getCurrentModel())) {
			if(!string.equalsIgnoreCase("{$VANILLA_FILES}") && !string.equalsIgnoreCase("{$VANILLA_HOME}")) { //$NON-NLS-1$ //$NON-NLS-2$
				listeVar[i] = string;
				i++;
			}

		}
		new ContentProposalAdapter(select, new TextContentAdapter(), new SimpleContentProposalProvider(listeVar), Activator.getDefault().getKeyStroke(), Activator.getDefault().getAutoActivationCharacters());

	}

	@Override
	public void aboutToBeShown() {

		super.aboutToBeShown();

	}

	public void aboutToBeHidden() {

		super.aboutToBeHidden();
	}

	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();

	}

}
