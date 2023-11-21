package bpm.workflow.ui.views.property.sections;

import java.util.HashMap;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.Transition;
import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.model.activities.XorActivity;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.Link;
import bpm.workflow.ui.gef.part.LinkEditPart;

/**
 * Section for the definition of the links (creation of conditions)
 * @author CAMUS, CHARBONNIER, MARTIN
 *
 */
public class LinkSection extends AbstractPropertySection {
	private Link link;
	private Transition transition;
	private Text txtName;
	private CLabel lblCondition,lblCondition2 ;
	private Composite composite;
	private LinkEditPart input;
	private Combo variable;
	public final static Color BLUE = new Color(Display.getCurrent(),0,0,255);
	public final static Color GREEN = new Color(Display.getCurrent(),0,221,0);
	public final static Color BLACK = new Color(Display.getCurrent(),0,0,0);
	
	public static HashMap<String, Color> colorMap = new HashMap<String, Color>();
	
	public LinkSection() {
		if(colorMap.isEmpty()) {
			colorMap.put("blue", BLUE); //$NON-NLS-1$
			colorMap.put("green", GREEN); //$NON-NLS-1$
			colorMap.put("black", BLACK); //$NON-NLS-1$
		}
	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, false));

		CLabel lblName = getWidgetFactory().createCLabel(composite, Messages.LinkSection_0);
		lblName.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		txtName = getWidgetFactory().createText(composite, "", SWT.READ_ONLY); //$NON-NLS-1$
		txtName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txtName.setEnabled(false);

		lblCondition = getWidgetFactory().createCLabel(composite, Messages.LinkSection_1);
		lblCondition.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false,2,1));
		
		lblCondition2 = getWidgetFactory().createCLabel(composite, Messages.LinkSection_2);
		lblCondition2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		variable = new Combo(composite, SWT.READ_ONLY);
		variable.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		variable.addSelectionListener(adaptder);
		
		String[] comp = new String[3];
		comp[0] = "state"; //$NON-NLS-1$
		comp[1] = "true"; //$NON-NLS-1$
		comp[2] = "false"; //$NON-NLS-1$
		variable.setItems(comp);

	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof LinkEditPart);
		this.link = (Link)((LinkEditPart) input).getModel();
		this.input = (LinkEditPart)input;
	}

	@Override
	public void refresh() {
		try {
			transition = ((WorkflowModel) Activator.getDefault().getCurrentModel()).getTransition((IActivity) link.getSource().getWorkflowObject(), (IActivity) link.getTarget().getWorkflowObject());

			txtName.setText(transition.getName());

			if (link.getSource().getWorkflowObject() instanceof XorActivity ) {

				if(link.getSource().getTargetLink().size() != 0){

					String color = transition.getColor();
					String value = transition.getCondition().getValue();
					
					if(color != null && !color.isEmpty()) {
						input.getFigure().setForegroundColor(colorMap.get(color));
					}

					if(value != null && !value.isEmpty()) {
						variable.setText(value);
					}
					else {
						variable.deselectAll();
					}

				}
				variable.setEnabled(true);
			}
			else {
				variable.deselectAll();
				variable.setEnabled(false);
			}

		} catch (Exception e) {
			e.printStackTrace();
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
	SelectionAdapter adaptder = new SelectionAdapter() {


		@Override
		public void widgetSelected(SelectionEvent e) {



			if(variable.getText() != null){

				transition.getCondition().setValue(variable.getText());
				if(variable.getText().equalsIgnoreCase("false")){ //$NON-NLS-1$
					transition.setColor("blue"); //$NON-NLS-1$
				}
				else if(variable.getText().equalsIgnoreCase("true")){ //$NON-NLS-1$
					transition.setColor("green"); //$NON-NLS-1$
				}
				else {
					transition.setColor("black"); //$NON-NLS-1$
				}
				
				input.getFigure().setForegroundColor(colorMap.get(transition.getColor()));

			}

		}

	};

}
