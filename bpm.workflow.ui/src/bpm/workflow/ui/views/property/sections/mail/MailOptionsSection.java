package bpm.workflow.ui.views.property.sections.mail;

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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.model.activities.MailActivity;
import bpm.workflow.runtime.resources.variables.ListVariable;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;
/**
 * Section for the definition of the mail activities
 * @author CHARBONNIER, MARTIN
 *
 */
public class MailOptionsSection extends AbstractPropertySection {
	private static final String NO_VARIABLE = Messages.MailOptionsSection_0;
	private Text txtObject;
	private Text txtContent;
	private Text txtDestination;
	private Text txtFrom;
	private Node node;


	public MailOptionsSection() {
	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);

		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, false));
		
		CLabel lblFrom = getWidgetFactory().createCLabel(composite, Messages.MailOptionsSection_1);
		lblFrom.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		
		txtFrom = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		txtFrom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
								
		CLabel lblDestination = getWidgetFactory().createCLabel(composite, Messages.MailOptionsSection_2);
		lblDestination.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		
		CLabel lblTo = getWidgetFactory().createCLabel(composite, Messages.MailOptionsSection_3);
		lblTo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		
		txtDestination = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		txtDestination.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
			
		CLabel lblObject = getWidgetFactory().createCLabel(composite, Messages.MailOptionsSection_4);
		lblObject.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		
		txtObject = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		txtObject.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
				
		CLabel lblContent = getWidgetFactory().createCLabel(composite, Messages.MailOptionsSection_5);
		lblContent.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		
		txtContent = getWidgetFactory().createText(composite, "", SWT.MULTI | SWT.WRAP | SWT.V_SCROLL); //$NON-NLS-1$
		txtContent.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 3));
		
		getWidgetFactory().createCLabel(composite, "").setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false)); //$NON-NLS-1$
		
		getWidgetFactory().createCLabel(composite, "").setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false)); //$NON-NLS-1$

	}

	
	
	
	@Override
	public void refresh() {
		MailActivity m = (MailActivity) node.getWorkflowObject();
		
		String [] items = new String[ListVariable.getInstance().getNoEnvironementVariable().length + 1];
		items[0] = NO_VARIABLE;
		
		for (int i = 0; i < ListVariable.getInstance().getNoEnvironementVariable().length; i++) {
			items[i + 1] = ListVariable.getInstance().getNoEnvironementVariable()[i];
		}
	
		
		if (m.getDestination() != null) {
			txtDestination.setText(m.getDestination());
		}
		else {
			txtDestination.setText(""); //$NON-NLS-1$
		}
		if (m.getContent() != null) {
			txtContent.setText(m.getContent());
		}
		else {
			txtContent.setText(""); //$NON-NLS-1$
		}
		if (m.getObject() != null) {
			txtObject.setText(m.getObject());
		}
		else {
			txtObject.setText(""); //$NON-NLS-1$
		}
		if (m.getFrom() != null) {
			txtFrom.setText(m.getFrom());
		}
		else {
			txtFrom.setText(""); //$NON-NLS-1$
		}
		
		new ContentProposalAdapter(
				txtFrom, 
				new TextContentAdapter(), 
				new SimpleContentProposalProvider(ListVariable.getInstance().getArray((WorkflowModel) Activator.getDefault().getCurrentModel())),
				Activator.getDefault().getKeyStroke(), 
				Activator.getDefault().getAutoActivationCharacters());
		
		new ContentProposalAdapter(
				txtContent, 
				new TextContentAdapter(), 
				new SimpleContentProposalProvider(ListVariable.getInstance().getArray((WorkflowModel) Activator.getDefault().getCurrentModel())),
				Activator.getDefault().getKeyStroke(), 
				Activator.getDefault().getAutoActivationCharacters());
		
		new ContentProposalAdapter(
				txtObject, 
				new TextContentAdapter(), 
				new SimpleContentProposalProvider(ListVariable.getInstance().getArray((WorkflowModel) Activator.getDefault().getCurrentModel())),
				Activator.getDefault().getKeyStroke(), 
				Activator.getDefault().getAutoActivationCharacters());
		
		new ContentProposalAdapter(
				txtDestination, 
				new TextContentAdapter(), 
				new SimpleContentProposalProvider(ListVariable.getInstance().getArray((WorkflowModel) Activator.getDefault().getCurrentModel())),
				Activator.getDefault().getKeyStroke(), 
				Activator.getDefault().getAutoActivationCharacters());

	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.node = (Node)((NodePart) input).getModel();
	}

	@Override
	public void aboutToBeShown() {
		txtContent.addModifyListener(modify);
		txtDestination.addModifyListener(modify);
		txtObject.addModifyListener(modify);
		txtFrom.addModifyListener(modify);


		super.aboutToBeShown();
		
	}


	@Override
	public void aboutToBeHidden() {
		txtContent.removeModifyListener(modify);
		txtDestination.removeModifyListener(modify);
		txtObject.removeModifyListener(modify);
		txtFrom.removeModifyListener(modify);
		
		super.aboutToBeHidden();
	}


	private ModifyListener modify = new ModifyListener() {
	    
        public void modifyText(ModifyEvent evt) {
        	if (evt.widget == txtContent) {
        		((MailActivity) node.getWorkflowObject()).setContent(txtContent.getText());
        	}
        	else if (evt.widget == txtDestination) {
        		((MailActivity) node.getWorkflowObject()).setDestination(txtDestination.getText());
        	}
        	else if (evt.widget == txtObject) {
        		((MailActivity) node.getWorkflowObject()).setObject(txtObject.getText());
        	}
        	else if (evt.widget == txtFrom) {
        		((MailActivity) node.getWorkflowObject()).setFrom(txtFrom.getText());
        	}
        }
    };
    
}



