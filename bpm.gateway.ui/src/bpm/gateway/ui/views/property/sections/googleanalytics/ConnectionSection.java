package bpm.gateway.ui.views.property.sections.googleanalytics;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.transformations.googleanalytics.VanillaAnalyticsGoogle;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class ConnectionSection extends AbstractPropertySection {

	private VanillaAnalyticsGoogle model;
	private Text txtUsername, txtPassword, txtTableId;
	
	public ConnectionSection() {
		
	}
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		
		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout(2, false));
		
		Label lblInfos = getWidgetFactory().createLabel(composite, Messages.ConnectionSection_0);
		lblInfos.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		
		Label lblUsername = getWidgetFactory().createLabel(composite, Messages.ConnectionSection_1);
		lblUsername.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		
		txtUsername = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		txtUsername.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		txtUsername.addModifyListener(listener);
		
		
		Label lblPassword = getWidgetFactory().createLabel(composite, Messages.ConnectionSection_3);
		lblPassword.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		
		txtPassword = getWidgetFactory().createText(composite, "", SWT.PASSWORD); //$NON-NLS-1$
		txtPassword.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		txtPassword.addModifyListener(listener);
		
		
		Label lblTableId = getWidgetFactory().createLabel(composite, Messages.ConnectionSection_5);
		lblTableId.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		
		txtTableId = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		txtTableId.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		txtTableId.addModifyListener(listener);
	}
	
	@Override
	public void refresh() {
		if (txtUsername != null && !txtUsername.isDisposed()){
			txtUsername.removeModifyListener(listener);
			txtUsername.setText(model.getUsername() != null ? model.getUsername() : ""); //$NON-NLS-1$
			txtUsername.addModifyListener(listener);
		}
		
		if (txtPassword != null && !txtPassword.isDisposed()){
			txtPassword.removeModifyListener(listener);
			txtPassword.setText(model.getPassword() != null ? model.getPassword() : ""); //$NON-NLS-1$
			txtPassword.addModifyListener(listener);
		}
		
		if (txtTableId != null && !txtTableId.isDisposed()){
			txtTableId.removeModifyListener(listener);
			txtTableId.setText(model.getTableId() != null ? model.getTableId() : ""); //$NON-NLS-1$
			txtTableId.addModifyListener(listener);
		}
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.model = (VanillaAnalyticsGoogle)((Node)((NodePart) input).getModel()).getGatewayModel();
	}
	
	private ModifyListener listener = new ModifyListener() {
		
		@Override
        public void modifyText(ModifyEvent evt) {
        	if (evt.widget == txtUsername){
        		model.setUsername(txtUsername.getText());
        	}
        	else if (evt.widget == txtPassword){
        		model.setPassword(txtPassword.getText());
        	}
        	else if (evt.widget == txtTableId){
        		model.setTableId(txtTableId.getText());
        	}
        }
    };
}
