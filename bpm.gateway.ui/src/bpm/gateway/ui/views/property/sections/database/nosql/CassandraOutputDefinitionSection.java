package bpm.gateway.ui.views.property.sections.database.nosql;

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

import bpm.gateway.core.server.database.nosql.cassandra.CassandraConnection;
import bpm.gateway.core.server.database.nosql.cassandra.CassandraHelper;
import bpm.gateway.core.transformations.outputs.CassandraOutputStream;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class CassandraOutputDefinitionSection extends AbstractPropertySection {

	private CassandraOutputStream model;
	
	private Text targetName;
	private Button truncateRun;
	
	public CassandraOutputDefinitionSection() { }
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.model = (CassandraOutputStream)((Node)((NodePart) input).getModel()).getGatewayModel();
	}
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		
		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout(2, false));
		
		Label l = getWidgetFactory().createLabel(composite, Messages.DBOutputDefinitionSection_24);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		targetName = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		targetName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		truncateRun = getWidgetFactory().createButton(composite, Messages.DBOutputDefinitionSection_2, SWT.CHECK);
		truncateRun.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		truncateRun.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				model.setTruncate(truncateRun.getSelection());
			}
			
		});
		
		Button bApply = getWidgetFactory().createButton(composite, Messages.CassandraOutputDefinitionSection_0, SWT.PUSH);
		bApply.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		bApply.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (model.getServer() == null){
					MessageDialog.openInformation(getPart().getSite().getShell(), Messages.DBOutputDefinitionSection_16, Messages.DBOutputDefinitionSection_17);
					return;
				}
				
				model.setDefinition(targetName.getText());
				
				refresh();
			}
			
		});
		
		Button bTruncate = getWidgetFactory().createButton(composite, Messages.DBOutputDefinitionSection_6, SWT.PUSH);
		bTruncate.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		bTruncate.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell sh = getPart().getSite().getShell();
				
				if(MessageDialog.openConfirm(sh, Messages.CassandraOutputDefinitionSection_1, Messages.CassandraOutputDefinitionSection_2)){
					if (model.getServer() == null){
						MessageDialog.openInformation(getPart().getSite().getShell(), Messages.DBOutputDefinitionSection_18, Messages.DBOutputDefinitionSection_19);
						return;
					}
					
					if (model.getTableName() != null){
						try {
							CassandraHelper.truncate((CassandraConnection)model.getServer().getCurrentConnection(null), model.getTableName());
							MessageDialog.openInformation(sh, Messages.DBOutputDefinitionSection_7, Messages.DBOutputDefinitionSection_8 + model.getTableName() + Messages.DBOutputDefinitionSection_9);
						} catch (Exception e1) {
							e1.printStackTrace();
							MessageDialog.openError(sh, Messages.DBOutputDefinitionSection_10, Messages.DBOutputDefinitionSection_11 + e1.getMessage());
						}
					}
				}
			}
			
		});

	}
	
	
	@Override
	public void refresh() {
		if (model.getTableName() != null){
			targetName.setText(model.getTableName());
		}
		else{
			targetName.setText(""); //$NON-NLS-1$
		}
		
		truncateRun.setSelection(model.isTruncate());
	}

}
