package bpm.gateway.ui.views.property.sections.database.nosql;

import java.util.List;

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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.server.database.nosql.mongodb.MongoDbHelper;
import bpm.gateway.core.transformations.inputs.MongoDbInputStream;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.tools.dialogs.DialogBrowseContent;

public class MongoDbDefinitionSection extends AbstractPropertySection{
	private MongoDbInputStream model;
	
	private Text txtTableName;
	private Text txtColumnFamily;
	
	public MongoDbDefinitionSection() { }
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.model = (MongoDbInputStream)((Node)((NodePart) input).getModel()).getGatewayModel();
	}
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, false));
		
		Label lblTableName = getWidgetFactory().createLabel(composite, Messages.MongoDbDefinitionSection_0);
		lblTableName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		
		txtTableName = new Text(composite, SWT.BORDER);
		txtTableName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Label lblColumnFamily = getWidgetFactory().createLabel(composite, Messages.MongoDbDefinitionSection_1);
		lblColumnFamily.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		
		txtColumnFamily = new Text(composite, SWT.BORDER);
		txtColumnFamily.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        Composite buttonBar = getWidgetFactory().createComposite(composite);
        buttonBar.setLayout(new GridLayout(3, true));
        buttonBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

        Button apply = getWidgetFactory().createButton(buttonBar, Messages.StreamDefinitionSection_0, SWT.PUSH);
        apply.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
        apply.addSelectionListener(new SelectionAdapter(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				model.setTableName(txtTableName.getText());
				model.setdocumentFamily(txtColumnFamily.getText());
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
					
					if (model instanceof MongoDbInputStream){
						lst = MongoDbHelper.getValues(model, 100,null);
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
	}
	
	@Override
	public void refresh() {
		super.refresh();
		txtTableName.setText(model.getTableName());
		txtColumnFamily.setText(model.getDocumentFamily());
	}
}
