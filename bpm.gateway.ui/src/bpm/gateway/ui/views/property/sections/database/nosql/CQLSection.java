package bpm.gateway.ui.views.property.sections.database.nosql;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.server.database.nosql.cassandra.CassandraHelper;
import bpm.gateway.core.transformations.inputs.CassandraInputStream;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.tools.dialogs.DialogBrowseContent;

public class CQLSection extends AbstractPropertySection {

	private CassandraInputStream model;
	private Text txtCql;
	
	
	public CQLSection() {
		
	}

	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.model = (CassandraInputStream)((Node)((NodePart) input).getModel()).getGatewayModel();
	}
	
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new FillLayout(SWT.VERTICAL));
		
		
		Composite c = getWidgetFactory().createFlatFormComposite(composite);
		c.setLayout(new GridLayout(2, false));
		
		CLabel labelLabel = getWidgetFactory().createCLabel(c, "Query"); //$NON-NLS-1$
		labelLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true, 1, 2));
		
		txtCql = getWidgetFactory().createText(c, "", SWT.MULTI | SWT.WRAP | SWT.V_SCROLL); //$NON-NLS-1$
		txtCql.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 2));;

        Composite buttonBar = getWidgetFactory().createComposite(composite);
        buttonBar.setLayout(new GridLayout(3, true));

        Button apply = getWidgetFactory().createButton(buttonBar, Messages.StreamDefinitionSection_0, SWT.PUSH);
        apply.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
        apply.addSelectionListener(new SelectionAdapter(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				model.setCQLDefinition(txtCql.getText());
			}
			
		});
		
		Button cancel = getWidgetFactory().createButton(buttonBar, Messages.StreamDefinitionSection_1, SWT.PUSH);
		cancel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		cancel.addSelectionListener(new SelectionAdapter(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				refresh();
			}
			
		});
        
		
        Button bBrowse = getWidgetFactory().createButton(buttonBar, Messages.StreamDefinitionSection_3, SWT.PUSH);
		bBrowse.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		bBrowse.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell sh = getPart().getSite().getShell();
				
				try {
					List<List<Object>> lst = null;
					
					if (model instanceof CassandraInputStream){
						lst = CassandraHelper.getValues(model, 100);
					}
					
					List<StreamElement> cols = model.getDescriptor(model).getStreamElements();
					
					DialogBrowseContent dial = new DialogBrowseContent(sh, lst, cols);
					dial.open();
					
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(sh, Messages.StreamDefinitionSection_8, Messages.StreamDefinitionSection_9 + e1.getMessage());
				}
			}
		});
	
//		 Button bDistcintValues = getWidgetFactory().createButton(buttonBar, Messages.StreamDefinitionSection_10, SWT.PUSH);
//		 bDistcintValues.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		 bDistcintValues.addSelectionListener(new SelectionAdapter(){
//
//				@Override
//				public void widgetSelected(SelectionEvent e) {
//					DataStream os = (DataStream)node.getGatewayModel();
//					Shell sh = getPart().getSite().getShell();
//
//					DialogFieldsValues dial = new DialogFieldsValues(sh, os);
//					dial.open();
//
//				}
//				
//			});
	}
	
	@Override
	public void refresh() {
		txtCql.setText(model.getCQLDefinition());
	}

}
