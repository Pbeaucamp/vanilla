package bpm.gateway.ui.views.property.sections.gid;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.transformations.GlobalDefinitionInput;
import bpm.gateway.core.transformations.gid.GidNode;
import bpm.gateway.core.transformations.gid.GidTreeBuilder;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class GidQuerySection extends AbstractPropertySection{
	private Node node;
	private Text text;
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		
		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout());
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		text = new Text(composite, SWT.BORDER | SWT.H_SCROLL |SWT.V_SCROLL | SWT.MULTI);
		text.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				((GlobalDefinitionInput)node.getGatewayModel()).setCustomQuery(text.getText());
			}
		});
		
		Button b = new Button(composite, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		b.setText(Messages.GidQuerySection_0);
		b.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				GlobalDefinitionInput gid = (GlobalDefinitionInput)node.getGatewayModel();
				
				StringBuffer buf = new StringBuffer();
				
				try{
					GidNode gidNode = new GidTreeBuilder().buildTree(gid);
					gid.setCustomQuery(gidNode.evaluteQuery().dump());
					try{
						buf.append(gidNode.evaluteQuery().dump());
					}catch(Exception ex){
						ex.printStackTrace();
						buf.append(ex.getMessage());		
					}
				}catch(Exception ex){
					ex.printStackTrace();
					buf.append(ex.getMessage());
				}
				
				text.setText(buf.toString());
				
			}
		});
		
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
	public void refresh() {
		text.setText(((GlobalDefinitionInput)node.getGatewayModel()).getCustomQuery());
	}

}
