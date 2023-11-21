package bpm.gateway.ui.dialogs.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;

import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.userdefined.Parameter;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.editors.GatewayEditorInput;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.viewer.TreeContentProvider;
import bpm.gateway.ui.viewer.TreeJdbc;
import bpm.gateway.ui.viewer.TreeObject;
import bpm.gateway.ui.viewer.TreeParent;

public class DataBaseBrowserDialog extends Dialog {

	private DataBaseServer server;
	private TreeViewer viewer;
	private String sqlStatement;
	private Button isDistinct;
	
	private Text sql;
	private ListViewer parameterNames;
	

	
	private TreeJdbc selectedNode;
	
	private DocumentGateway document;
	
	public DataBaseBrowserDialog(Shell parentShell, DataBaseServer server) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.server = server;
	}

	public DataBaseBrowserDialog(Shell parentShell, DataBaseServer server, String sqlDefinition, DocumentGateway document) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.server = server;
		this.sqlStatement = sqlDefinition;
		this.document = document;
	}

	@Override
	protected void initializeBounds() {
		try {
			setInput();
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), Messages.DataBaseBrowserDialog_0, e.getMessage());
		} 
		
		this.getShell().setSize(800, 600);
	}



	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container  = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(3, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(container, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 3, 1));
		l.setText(Messages.DataBaseBrowserDialog_1);
		
		viewer = new TreeViewer(container, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));
		viewer.setLabelProvider(new LabelProvider(){
			
		});
		
		viewer.setContentProvider(new TreeContentProvider(){
			
		});
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
			 */
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection)event.getSelection();
				
				if (ss.isEmpty()){
					getButton(IDialogConstants.OK_ID).setEnabled(false);
					return;
				}
				if (! (ss.getFirstElement() instanceof TreeJdbc)){
					return;
				}
				
				TreeJdbc node = (TreeJdbc)ss.getFirstElement();
				
				if (node.type == TreeJdbc.TABLE || node.type == TreeJdbc.VIEW){
					getButton(IDialogConstants.OK_ID).setEnabled(true);
					generateSqlStatement(node);
					sql.setText(sqlStatement);
				}
				else{
					getButton(IDialogConstants.OK_ID).setEnabled(false);
				}
				
				
				
			}
			
		});
		
//		Composite toolBar = new Composite(container, SWT.NONE);
//		toolBar.setLayout(new GridLayout());
//		toolBar.setLayoutData(new GridData(GridData.END, GridData.FILL, false, true, 1, 2));
//		
//		Button browseTable = new Button(toolBar, SWT.PUSH);
//		browsTab
//		browseTable.setText("Browse Table");
//		
//		Button viewDDL = new Button(toolBar, SWT.PUSH);
//		viewDDL.setText("DDL");
//		
//		Button distinctValues = new Button(toolBar, SWT.PUSH);
//		distinctValues.setText("distinctValues");
		
		isDistinct = new Button(container, SWT.CHECK);
		isDistinct.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		isDistinct.setText(Messages.DataBaseBrowserDialog_2);
		isDistinct.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				String s = sql.getText();
				if (isDistinct.getSelection()){
					if (!s.toLowerCase().contains(" distinct ") && s.contains("select") || s.contains("SELECT")){ //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						int start  = s.toLowerCase().indexOf("select") + 6; //$NON-NLS-1$
						
						s = "select distinct " + s.substring(start); //$NON-NLS-1$
						
					}
				}
				else{
					s  = s.replace(" distinct ", " "); //$NON-NLS-1$ //$NON-NLS-2$
					s = s.replace(" DISTINCT ", " "); //$NON-NLS-1$ //$NON-NLS-2$
				}
				sql.setText(s);
				sqlStatement = s;
			}
			
		});
		
		/*
		 * 
		 */
		
		Label l2 = new Label(container, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l2.setText(Messages.DataBaseBrowserDialog_3);
		
		
		Label l3 = new Label(container, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 2, 1));
		l3.setText(Messages.DataBaseBrowserDialog_13);
		
		parameterNames = new ListViewer(container, SWT.BORDER  | SWT.V_SCROLL | SWT.H_SCROLL);
		parameterNames.getList().setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true, 1, 1));
		parameterNames.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<Object> l = (List<Object>)inputElement;
				return l.toArray(new Object[l.size()]);
			}

			public void dispose() {
				
				
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				
				
			}
			
		});
		parameterNames.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				if (element instanceof Variable){
					return ((Variable)element).getName();
				}
				else{
					return ((Parameter)element).getName();
				}
			}
			
		});
		parameterNames.addDoubleClickListener(new IDoubleClickListener(){

			public void doubleClick(DoubleClickEvent event) {
				Object o = ((IStructuredSelection)parameterNames.getSelection()).getFirstElement();
				
				if (o instanceof Variable){
					if (((Variable)o).getType() == Variable.DATE || ((Variable)o).getType() == Variable.STRING){
						sql.setText(sql.getText() + "'" + ((Variable)o).getOuputName()+ "'") ; //$NON-NLS-1$ //$NON-NLS-2$
					}
					else{
						sql.setText(sql.getText() + ((Variable)o).getOuputName()) ;
					}
					
				}
				else if (o instanceof Parameter){
					if (((Parameter)o).getType() == Parameter.DATE || ((Parameter)o).getType() == Parameter.STRING){
						sql.setText(sql.getText() + "'" + ((Parameter)o).getOuputName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$
					}
					else{
						sql.setText(sql.getText() + ((Parameter)o).getOuputName());
					}
					
				}
				
				
				
			}
			
		});
		
		
				
		
		
		sql = new Text(container, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		sql.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		
		return container;
	}



	
	
	
	
	private void setInput() throws ServerException, SQLException{
		TreeParent root = new TreeParent(""); //$NON-NLS-1$
		
		DataBaseConnection con = (DataBaseConnection)server.getCurrentConnection(null);
		
		if (con == null){
			throw new ServerException(Messages.DataBaseBrowserDialog_4, server);
		}
		
		con.connect(document);
		Connection c = con.getSocket(document);
		
		
		DatabaseMetaData dmd = c.getMetaData();
		ResultSet rs = null;
		boolean hasSchema = false;
		try{
			rs = dmd.getSchemas();
		}catch(Exception e){
			hasSchema = false;
		}
		
	
		
		String[] types = new String[]{"TABLE", "VIEW", "ALIAS"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
		
		
		if (rs != null){
			while(rs.next()){
				hasSchema = true;
				
				TreeJdbc tSch = new TreeJdbc(rs.getString(1), TreeJdbc.SCHEMA);
				root.addChild(tSch);
				
				TreeParent tt = new TreeParent("TABLES"); //$NON-NLS-1$
				TreeParent tv = new TreeParent("VIEWS"); //$NON-NLS-1$
				
				getTables(c.getCatalog(), tSch.getName(), dmd, tt, tv, types);
				tSch.addChild(tt);
				tSch.addChild(tv);
		
			}
		}
		
		
		if (!hasSchema){
			
			TreeParent tt = new TreeParent("TABLES"); //$NON-NLS-1$
			TreeParent tv = new TreeParent("VIEWS"); //$NON-NLS-1$
			
			getTables(c.getCatalog(), null, dmd, tt, tv, types);
			root.addChild(tt);
			root.addChild(tv);
			
		}
		
//		con.disconnect();
		
		viewer.setInput(root);
		
		
		/*
		 * set parameter/variable inputs
		 */
		
		List<Object> l = new ArrayList<Object>();
		
		
		for(Variable v : ResourceManager.getInstance().getVariables()){
			l.add(v);
		}
		
		
		IEditorPart e = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		
		if (e !=  null){
			GatewayEditorInput in = (GatewayEditorInput)e.getEditorInput();
			
			for(Parameter p : in.getDocumentGateway().getParameters()){
				l.add(p);
			}
		}
		
		parameterNames.setInput(l);
		
		if (sqlStatement != null){
			sql.setText(sqlStatement);
			getButton(IDialogConstants.OK_ID).setEnabled(true);
		}
		
		if (sql.getText().toLowerCase().contains(" distinct ")){ //$NON-NLS-1$
			isDistinct.setSelection(true);
		}
	}
	
	
	private void getTables(String catalogName, String schema, DatabaseMetaData dmd, TreeParent parentTable,TreeParent parentVuew, String[] types) throws SQLException{
		ResultSet rs = null;
		try{
			rs = dmd.getTables(catalogName, schema, "%", types); //$NON-NLS-1$
			while(rs.next()){
				String t = rs.getString("TABLE_TYPE"); //$NON-NLS-1$
				
				TreeJdbc table = null;
				if (t.equalsIgnoreCase("TABLE")){ //$NON-NLS-1$
					table = new TreeJdbc(rs.getString("TABLE_NAME"), TreeJdbc.TABLE); //$NON-NLS-1$
					parentTable.addChild(table);
				}
				else if (t.equalsIgnoreCase("VIEW")){ //$NON-NLS-1$
					table = new TreeJdbc(rs.getString("TABLE_NAME"), TreeJdbc.VIEW); //$NON-NLS-1$
					parentVuew.addChild(table);
				}
				else if (t.equalsIgnoreCase("ALIAS")){ //$NON-NLS-1$
					table = new TreeJdbc(rs.getString("TABLE_NAME"), TreeJdbc.TABLE);		 //$NON-NLS-1$
					parentTable.addChild(table);
				}
				else{
					continue;
				}
				
				
//				ResultSet colRs = dmd.getColumns(catalogName, schema,table.getName(), "%");
//				while(colRs.next()){
//					TreeJdbc column = new TreeJdbc(colRs.getString("COLUMN_NAME"), TreeJdbc.COLUMN);
//					table.addChild(column);
//				}
				
			}
		}finally{
			rs.close();
		}
		
		
	}



	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createButtonBar(Composite parent) {
		
		Control c =  super.createButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
		return c;
	}



	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		selectedNode = (TreeJdbc)((IStructuredSelection)viewer.getSelection()).getFirstElement();
		
		sqlStatement = sql.getText();

		super.okPressed();
	}

	
	
	private void generateSqlStatement(TreeJdbc table){
		String schemaName = null;
		
		if (table.getParent().getParent() != viewer.getInput()){
			schemaName = table.getParent().getParent().getName(); 
		}
		
		
		StringBuffer buf = new StringBuffer();
		buf.append("select "); //$NON-NLS-1$
		
		if (isDistinct.getSelection()){
			buf.append("distinct "); //$NON-NLS-1$
		}
		
		boolean first = true;
		
		
		if (table.getChildren().isEmpty()){
			DataBaseConnection con = (DataBaseConnection)server.getCurrentConnection(null);

			Connection c = con.getSocket(document);
			try{
				DatabaseMetaData dmd = c.getMetaData();
				ResultSet colRs = dmd.getColumns(c.getCatalog(), schemaName,table.getName(), "%"); //$NON-NLS-1$
				while(colRs.next()){
					TreeJdbc column = new TreeJdbc(colRs.getString("COLUMN_NAME"), TreeJdbc.COLUMN); //$NON-NLS-1$
					table.addChild(column);
				}
				viewer.refresh();
			}catch(Exception e){
				
			}
			
			
		}
			
		
		for(TreeObject c : table.getChildren()){
			if (first){
				first = false;
			}
			else{
				buf.append(", "); //$NON-NLS-1$
			}
			buf.append(table.getName() + "." + c.getName()); //$NON-NLS-1$
		}
		
		buf.append(" from "); //$NON-NLS-1$
		if (schemaName != null){
			buf.append(schemaName + "." + table.getName()); //$NON-NLS-1$
		}
		else{
			buf.append(table.getName());
		}
		
		sqlStatement = buf.toString();
	}


	public String getSqlStatement(){
		return sqlStatement;
	}
	
	public String getTableName(){
		return selectedNode.getName();
	}
	
	
}
