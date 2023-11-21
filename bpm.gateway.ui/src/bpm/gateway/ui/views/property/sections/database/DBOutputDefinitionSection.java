package bpm.gateway.ui.views.property.sections.database;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
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

import bpm.gateway.core.DataStream;
import bpm.gateway.core.ITargetableStream;
import bpm.gateway.core.server.database.DataBaseHelper;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.transformations.nosql.SqoopTransformation;
import bpm.gateway.ui.dialogs.database.DataBaseBrowserDialog;
import bpm.gateway.ui.dialogs.database.DialogDataBaseCustomTableCreator;
import bpm.gateway.ui.dialogs.utils.fields.DialogFieldsValues;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.tools.dialogs.DialogBrowseContent;

public class DBOutputDefinitionSection extends AbstractPropertySection {

	private Node node;
	
	private Text targetName;
	
	private Button truncateRun;
	
	public DBOutputDefinitionSection() {
		
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
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		
		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout(5, false));
		
		Label l = getWidgetFactory().createLabel(composite, Messages.DBOutputDefinitionSection_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		
		targetName = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		targetName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 4, 1));
		targetName.setEditable(false);
		
		truncateRun = getWidgetFactory().createButton(composite, Messages.DBOutputDefinitionSection_2, SWT.CHECK);
		truncateRun.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 5, 1));
		truncateRun.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				((ITargetableStream)node.getGatewayModel()).setTruncate(truncateRun.getSelection());
			}
			
		});
		
		Button bSelect = getWidgetFactory().createButton(composite, Messages.DBOutputDefinitionSection_3, SWT.PUSH);
		bSelect.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		bSelect.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (((DataStream)node.getGatewayModel()).getServer() == null){
					MessageDialog.openInformation(getPart().getSite().getShell(), Messages.DBOutputDefinitionSection_1, Messages.DBOutputDefinitionSection_5);
					return;
				}
				DataBaseBrowserDialog d = new DataBaseBrowserDialog(getPart().getSite().getShell(),
						(DataBaseServer)((ITargetableStream)node.getGatewayModel()).getServer(),
						((DataStream)node.getGatewayModel()).getDefinition(), node.getGatewayModel().getDocument());
				
				if (d.open() == Dialog.OK){
					targetName.setText(d.getTableName());
					/*
					 * perform the change on the model
					 */
					((ITargetableStream)node.getGatewayModel()).setDefinition(d.getSqlStatement());
					
					
				}
				
			}
			
		});
		
		Button bCreate = getWidgetFactory().createButton(composite, Messages.DBOutputDefinitionSection_4, SWT.PUSH);
		bCreate.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		bCreate.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (((DataStream)node.getGatewayModel()).getServer() == null){
					MessageDialog.openInformation(getPart().getSite().getShell(), Messages.DBOutputDefinitionSection_16, Messages.DBOutputDefinitionSection_17);
					return;
				}

//				DialogSqlTableCreator d = new DialogSqlTableCreator(getPart().getSite().getShell(), (ITargetableStream)node.getGatewayModel());
//				
//				if (d.open() == Dialog.OK){
//					targetName.setText(d.getTableName());
//					/*
//					 * perform the change on the model
//					 */
//					((ITargetableStream)node.getGatewayModel()).setDefinition(d.getSelectStatement());
//
//				}
				DialogDataBaseCustomTableCreator dd = new DialogDataBaseCustomTableCreator(getPart().getSite().getShell(), (ITargetableStream)node.getGatewayModel());
//				dd.open();
				if (dd.open() == Dialog.OK){
				targetName.setText(dd.getTableName());
				((ITargetableStream)node.getGatewayModel()).setDefinition(dd.getSelectStatement() + " " + dd.getTableName()); //$NON-NLS-1$
			}
//				DialogDatabaseTableCreator d = new DialogDatabaseTableCreator(getPart().getSite().getShell(), (ITargetableStream)node.getGatewayModel());
//				if (d.open() == Dialog.OK){
//					targetName.setText(d.getTableName());
//					((ITargetableStream)node.getGatewayModel()).setDefinition(d.getSelectStatement() + " " + d.getTableName()); //$NON-NLS-1$
//				}
			}
			
		});
		
		
		
		Button bTruncate = getWidgetFactory().createButton(composite, Messages.DBOutputDefinitionSection_6, SWT.PUSH);
		bTruncate.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		bTruncate.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell sh = getPart().getSite().getShell();
				
				if(MessageDialog.openConfirm(sh, Messages.DBOutputDefinitionSection_25, Messages.DBOutputDefinitionSection_26)){
					if (((DataStream)node.getGatewayModel()).getServer() == null){
						MessageDialog.openInformation(getPart().getSite().getShell(), Messages.DBOutputDefinitionSection_18, Messages.DBOutputDefinitionSection_19);
						return;
					}
	
					ITargetableStream os = (ITargetableStream)node.getGatewayModel();
					
					if (os.getTableName() != null){
						try {
							DataBaseHelper.truncate(os);
							MessageDialog.openInformation(sh, Messages.DBOutputDefinitionSection_7, Messages.DBOutputDefinitionSection_8 + os.getTableName() + Messages.DBOutputDefinitionSection_9);
						} catch (Exception e1) {
							e1.printStackTrace();
							MessageDialog.openError(sh, Messages.DBOutputDefinitionSection_10, Messages.DBOutputDefinitionSection_11 + e1.getMessage());
						}
					}
				}
			}
			
		});
	
	
		Button bBrowse = getWidgetFactory().createButton(composite, Messages.DBOutputDefinitionSection_12, SWT.PUSH);
		bBrowse.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		bBrowse.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (((DataStream)node.getGatewayModel()).getServer() == null){
					MessageDialog.openInformation(getPart().getSite().getShell(), Messages.DBOutputDefinitionSection_20, Messages.DBOutputDefinitionSection_21);
					return;
				}

				ITargetableStream os = (ITargetableStream)node.getGatewayModel();
				Shell sh = getPart().getSite().getShell();
				
				if (os.getTableName() != null){
					try {
						
						List<List<Object>> lst = DataBaseHelper.getValues(os, 100);
						
						DialogBrowseContent dial = new DialogBrowseContent(sh, lst, os.getDescriptor(os).getStreamElements());
						dial.open();
						
						
					} catch (Exception e1) {
						e1.printStackTrace();
						MessageDialog.openError(sh, Messages.DBOutputDefinitionSection_13, Messages.DBOutputDefinitionSection_14 + e1.getMessage());
					}
				}
			}
			
		});
	
		 Button bDistcintValues = getWidgetFactory().createButton(composite, Messages.DBOutputDefinitionSection_15, SWT.PUSH);
		 bDistcintValues.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		 bDistcintValues.addSelectionListener(new SelectionAdapter(){

				@Override
				public void widgetSelected(SelectionEvent e) {
					if (((DataStream)node.getGatewayModel()).getServer() == null){
						MessageDialog.openInformation(getPart().getSite().getShell(), Messages.DBOutputDefinitionSection_22, Messages.DBOutputDefinitionSection_23);
						return;
					}

					DataStream os = (DataStream)node.getGatewayModel();
					Shell sh = getPart().getSite().getShell();

					DialogFieldsValues dial = new DialogFieldsValues(sh, os);
					dial.open();

				}
				
			});
		


	}
	
	
	@Override
	public void refresh() {
		ITargetableStream transfo = (ITargetableStream)node.getGatewayModel();
		if (transfo.getTableName() != null){
			targetName.setText(transfo.getTableName());
		}
		else{
			targetName.setText(""); //$NON-NLS-1$
		}

		truncateRun.setSelection(transfo.isTruncate());
		if(transfo instanceof SqoopTransformation) {
			truncateRun.setVisible(false);
		}
//		try {
//			StreamDescriptor s = transfo.getDescriptor();
//			System.out.println();
//		} catch (ServerException e) {
//			
//			e.printStackTrace();
//		}
	}

}
