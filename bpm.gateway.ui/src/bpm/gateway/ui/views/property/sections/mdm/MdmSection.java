package bpm.gateway.ui.views.property.sections.mdm;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.mdm.IMdm;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class MdmSection extends AbstractPropertySection {
	
	private IMdm mdm;
	private Text entityName;
	
	public MdmSection() {
		
	}
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		
		
		super.createControls(parent, aTabbedPropertySheetPage);
		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout(3, false));
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = getWidgetFactory().createLabel(composite, Messages.MdmSection_0);
		l.setLayoutData(new GridData());
		
		entityName = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		entityName.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		entityName.setEditable(false);
		
		Button b = getWidgetFactory().createButton(composite, "...", SWT.PUSH); //$NON-NLS-1$
		b.setLayoutData(new GridData());
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				try{
					MdmDialog d = new MdmDialog(getPart().getSite().getShell(), ((Transformation)mdm).getDocument().getMdmHelper().getMdmApi());
					if (d.open() == MdmDialog.OK){
						mdm.setEntityName(d.getEntity().getName());
						mdm.setEntityUuid(d.getEntity().getUuid());
						entityName.setText(d.getEntity().getName());
					}
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(getPart().getSite().getShell(), Messages.MdmSection_3, Messages.MdmSection_4 + " \n " + ex.getMessage()); //$NON-NLS-1$
				}
				
			}
		});
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.mdm = (IMdm)((Node)((NodePart) input).getModel()).getGatewayModel();
	}
	
	@Override
	public void refresh() {
		if (mdm.getEntityName() == null){
			entityName.setText(""); //$NON-NLS-1$
		}
		else{
			entityName.setText(mdm.getEntityName());
		}
		if (Activator.getDefault().getRepositoryContext() == null){
			MessageDialog.openInformation(getPart().getSite().getShell(), Messages.MdmSection_6, Messages.FmdtInputSection_17);
			return;
		}
	}
}
