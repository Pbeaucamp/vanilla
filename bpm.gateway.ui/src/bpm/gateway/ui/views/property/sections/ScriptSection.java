package bpm.gateway.ui.views.property.sections;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.transformations.SqlScript;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class ScriptSection extends AbstractPropertySection {

	
	private Text script;
	private Node node;
	
	public ScriptSection() {
		
	}

	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new FillLayout(SWT.VERTICAL));
//		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = getWidgetFactory().createLabel(composite, Messages.ScriptSection_0, SWT.NONE);
//		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		script = getWidgetFactory().createText(composite, "", SWT.WRAP | SWT.BORDER | SWT.FLAT | SWT.MULTI); //$NON-NLS-1$
//		script.setLayoutData(new GridData(GridData.FILL_BOTH));
		

	}
	
	
	private ModifyListener listener = new ModifyListener() {

		public void modifyText(ModifyEvent e) {
			SqlScript tr = (SqlScript)node.getGatewayModel();
			tr.setDefinition(script.getText());
			
		}

        
    };
    
    @Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.node = (Node)((NodePart) input).getModel();
       
	}

    
    
	@Override
	public void refresh() {
		SqlScript tr = (SqlScript)node.getGatewayModel();
		if (tr.getDefinition() != null){
			script.setText(tr.getDefinition());
		}
	}

	@Override
	public void aboutToBeHidden() {
		if (script != null && ! script.isDisposed()){
			script.removeModifyListener(listener);
		}
		
		super.aboutToBeHidden();
		
	}

	@Override
	public void aboutToBeShown() {
		super.aboutToBeShown();
		if (script != null && ! script.isDisposed()){
			script.addModifyListener(listener);
		}
		
	}

}
