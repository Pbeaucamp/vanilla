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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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

import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.model.activities.StarterActivity;
import bpm.workflow.runtime.resources.variables.ListVariable;
import bpm.workflow.runtime.resources.variables.Variable;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

/**
 * Section for the Executable Starter activity
 * 
 * @author Charles MARTIN
 * 
 */
public class RunPathSection extends AbstractPropertySection {
	public static Color mainBrown = new Color(Display.getDefault(), 209, 177, 129);
	public static Color secondBrown = new Color(Display.getDefault(), 238, 226, 208);

	private Text select;

	private Node node;
	private Combo typeSelect, typePlat;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		Composite general = getWidgetFactory().createFlatFormComposite(parent);
		general.setLayout(new GridLayout(3, false));

		CLabel labelcom = getWidgetFactory().createCLabel(general, Messages.RunPathSection_0);
		labelcom.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, false, 1, 1));

		select = getWidgetFactory().createText(general, ""); //$NON-NLS-1$
		select.setLayoutData(new GridData(SWT.FILL, GridData.FILL, true, false, 2, 1));
		select.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				((StarterActivity) node.getWorkflowObject()).setPath(select.getText());
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

		CLabel labelcom2 = getWidgetFactory().createCLabel(general, Messages.RunPathSection_1);
		labelcom2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));

		CLabel labelplat = getWidgetFactory().createCLabel(general, Messages.RunPathSection_2);
		labelplat.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));

		typePlat = new Combo(general, SWT.READ_ONLY);
		typePlat.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

		CLabel labeltype = getWidgetFactory().createCLabel(general, Messages.RunPathSection_3);
		labeltype.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));

		typeSelect = new Combo(general, SWT.READ_ONLY);
		typeSelect.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

	}

	@Override
	public void refresh() {

		if(((StarterActivity) node.getWorkflowObject()).getPath() != null) {

			select.setText(((StarterActivity) node.getWorkflowObject()).getPath());
		}

		String[] comp = StarterActivity.AVAILABLES_FORMATS;

		typeSelect.setItems(comp);
		String selectS = ((StarterActivity) node.getWorkflowObject()).getTypeEXE();
		int index2 = 0;
		for(String s : typeSelect.getItems()) {

			if(s.equalsIgnoreCase(selectS)) {
				typeSelect.select(index2);
				break;
			}
			index2++;
		}

		String[] comp1 = new String[3];
		comp1[0] = "Windows_NT_XP_Vista"; //$NON-NLS-1$
		comp1[1] = "Windows_95_98"; //$NON-NLS-1$
		comp1[2] = "UNIX"; //$NON-NLS-1$

		typePlat.setItems(comp1);
		String selectS1 = ((StarterActivity) node.getWorkflowObject()).getPlateforme();
		int index21 = 0;
		for(String s : typePlat.getItems()) {

			if(s.equalsIgnoreCase(selectS1)) {
				typePlat.select(index21);
				break;
			}
			index21++;
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
		typeSelect.addSelectionListener(selection);
		typePlat.addSelectionListener(selection);
		super.aboutToBeShown();

	}

	public void aboutToBeHidden() {
		try {
			typeSelect.removeSelectionListener(selection);
			typePlat.removeSelectionListener(selection);
			super.aboutToBeHidden();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();

	}

	SelectionListener selection = new SelectionListener() {

		public void widgetDefaultSelected(SelectionEvent e) {

		}

		public void widgetSelected(SelectionEvent e) {
			((StarterActivity) node.getWorkflowObject()).setTYPEExec(typeSelect.getText());
			((StarterActivity) node.getWorkflowObject()).setPlateforme(typePlat.getText());
		}

	};

}
