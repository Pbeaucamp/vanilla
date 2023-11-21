package bpm.gateway.ui.views.property.sections.database.nosql;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import com.mongodb.MongoException;

import bpm.gateway.core.ITargetableStream;
import bpm.gateway.core.server.database.nosql.cassandra.CassandraConnection;
import bpm.gateway.core.server.database.nosql.cassandra.CassandraHelper;
import bpm.gateway.core.server.database.nosql.hbase.HBaseConnection;
import bpm.gateway.core.server.database.nosql.hbase.HBaseHelper;
import bpm.gateway.core.server.database.nosql.mongodb.MongoDbConnection;
import bpm.gateway.core.server.database.nosql.mongodb.MongoDbHelper;
import bpm.gateway.core.transformations.outputs.CassandraOutputStream;
import bpm.gateway.core.transformations.outputs.MongoDbOutputStream;
import bpm.gateway.ui.dialogs.database.DialogDatabaseTableCreator;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class MongoDbOutputDefinitionSection extends AbstractPropertySection{

	public MongoDbOutputStream model;
	private Text txtTableName, txtColumnFamily;
	
	private Node node;
	private String tableName ;
	private Label verif;
	private String veriftext = ""; //$NON-NLS-1$
	private boolean tableExist = false;
	List<Object> rowField = new ArrayList<Object>();
	List<List<Object>> lst = new ArrayList<List<Object>>();
	private Composite composite;
	private List<String> colNames ;
	
	private Button truncateRun; 
	
	public MongoDbOutputDefinitionSection(){}
	
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.node = (Node)((NodePart) input).getModel();
        this.model = (MongoDbOutputStream)((Node)((NodePart) input).getModel()).getGatewayModel();
        
	}
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

			colNames = new ArrayList<String>();
			colNames.add("title"); //$NON-NLS-1$
			colNames.add("values"); //$NON-NLS-1$
	       
//		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, false));
		
//		final Composite inputChoice = getWidgetFactory().createComposite(composite);
//		inputChoice.setLayout(new GridLayout(4, false));
		
		Label lblTableName = getWidgetFactory().createLabel(composite, Messages.DBOutputDefinitionSection_0);
		lblTableName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
//		
//		
//		
		txtTableName = new Text(composite, SWT.BORDER);
		txtTableName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));		
			
		Label lblColumnFamily = getWidgetFactory().createLabel(composite, Messages.DBOutputDefinitionSection_24);
		lblColumnFamily.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
//		
//		
//		
		txtColumnFamily = new Text(composite, SWT.BORDER);
		txtColumnFamily.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));		
		
		verif = getWidgetFactory().createLabel(composite, veriftext, SWT.FILL);
		verif.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 0));
		
		truncateRun = getWidgetFactory().createButton(composite, Messages.DBOutputDefinitionSection_2, SWT.CHECK);
		truncateRun.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		truncateRun.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				model.setTruncate(truncateRun.getSelection());
			}
			
		});
		Button apply = getWidgetFactory().createButton(composite, Messages.DataBaseSection_0, SWT.PUSH);
		apply.setLayoutData(new GridData());
		apply.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				model.setTableName(txtTableName.getText());
				model.setColumnFamily(txtColumnFamily.getText());
//				List<List<Object>> values = new ArrayList<List<Object>>();
//				
//				try {
//					
////					values = MongoDbHelper.getValues(null, -1,txtTableName.getText());
//				} catch (MongoException e1) {
//					e1.printStackTrace();
//				} catch (Exception e1) {
//					e1.printStackTrace();
//				}
//				if(values.isEmpty()){
//					veriftext ="table doesn t exist, create one." ;
//					verif.setText(veriftext);
//					model.setTableName(txtTableName.getText());
//					tableExist = false;
//					composite.layout();
//				}else{
//					veriftext ="" ;
//					
//					verif.setText(veriftext);
//					model.setTableName(txtTableName.getText());
//					tableExist = true;
//				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		
		Button bTruncate = getWidgetFactory().createButton(composite, Messages.DBOutputDefinitionSection_6, SWT.PUSH);
		bTruncate.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		bTruncate.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell sh = getPart().getSite().getShell();
				
				if(MessageDialog.openConfirm(sh, Messages.MongoDbOutputDefinitionSection_3, Messages.MongoDbOutputDefinitionSection_4)){
					if (model.getServer() == null){
						MessageDialog.openInformation(getPart().getSite().getShell(), Messages.DBOutputDefinitionSection_18, Messages.DBOutputDefinitionSection_19);
						return;
					}
					
					if (model.getTableName() != null){
						if(model instanceof MongoDbOutputStream){
							try {
								MongoDbHelper.truncate((MongoDbConnection)model.getServer().getCurrentConnection(null), model.getTableName(), model.getColumnFamily());
								MessageDialog.openInformation(sh, Messages.DBOutputDefinitionSection_7, Messages.DBOutputDefinitionSection_8 + model.getTableName() + Messages.DBOutputDefinitionSection_9);
							} catch (Exception e1) {
								e1.printStackTrace();
								MessageDialog.openError(sh, Messages.DBOutputDefinitionSection_10, Messages.DBOutputDefinitionSection_11 + e1.getMessage());
							}
						}
						else {
							try {
								HBaseHelper.truncate((HBaseConnection)model.getServer().getCurrentConnection(null), model.getTableName(), model.getColumnFamily());
								MessageDialog.openInformation(sh, Messages.DBOutputDefinitionSection_7,Messages.DBOutputDefinitionSection_24+": "+model.getColumnFamily()+Messages.MongoDbOutputDefinitionSection_0+ Messages.DBOutputDefinitionSection_8 + model.getTableName() + Messages.DBOutputDefinitionSection_9); //$NON-NLS-1$
							} catch (Exception e1) {
								e1.printStackTrace();
								MessageDialog.openError(sh, Messages.DBOutputDefinitionSection_10, Messages.DBOutputDefinitionSection_11 + e1.getMessage());
							}
						}
					}
				}
			}
			
		});

		
	}
	
	@Override
	public void refresh() {
		
		//super.refresh();
		txtTableName.setText(model.getTableName());
		txtColumnFamily.setText(model.getColumnFamily());
	}
}
