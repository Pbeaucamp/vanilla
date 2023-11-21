package bpm.gateway.ui.views.property.sections.gid;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
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

public class GidSection extends AbstractPropertySection{
	private Node node;
	private Text text;
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		
		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout(2, false));
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Button b = new Button(composite, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2,1));
		b.setText(Messages.GidSection_0);
		b.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				GlobalDefinitionInput gid = (GlobalDefinitionInput)node.getGatewayModel();
				
				StringBuffer buf = new StringBuffer();
				buf.append("***** Tree ****** \n"); //$NON-NLS-1$
				
				try{
					GidNode gidNode = new GidTreeBuilder().buildTree(gid);
					
					
					buf.append(gidNode.dump("")); //$NON-NLS-1$
					buf.append("\n\n***** Query *****\n"); //$NON-NLS-1$
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
		
		text = new Text(composite, SWT.BORDER | SWT.H_SCROLL |SWT.V_SCROLL | SWT.MULTI);
		text.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.node = (Node)((NodePart) input).getModel();
	}
}
