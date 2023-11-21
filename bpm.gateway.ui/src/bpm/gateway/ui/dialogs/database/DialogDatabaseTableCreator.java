package bpm.gateway.ui.dialogs.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.gateway.core.ITargetableStream;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.tools.database.DBConverterManager;
import bpm.gateway.ui.i18n.Messages;


public class DialogDatabaseTableCreator extends Dialog{

	private Combo from;
	private Text createStatement;
	private ITargetableStream transformation;
	private Button go, useMigrationFile;
	private DBConverterManager converterManager;
	private String tableName, selectStatement;
	private Text name;
	
		
	public DialogDatabaseTableCreator(Shell parentShell, ITargetableStream transformation) {
		super(parentShell);
		this.transformation = transformation;
		setShellStyle(SWT.RESIZE | SWT.CLOSE);
	}
	
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
	

		useMigrationFile = new Button(container, SWT.CHECK);
		useMigrationFile.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		useMigrationFile.setText(Messages.DialogDatabaseTableCreator_0);
		useMigrationFile.setSelection(false);
		useMigrationFile.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				from.setEnabled(useMigrationFile.getSelection());
			}
			
		});
		
		Label l1 = new Label(container, SWT.NONE);
		l1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l1.setText(Messages.DialogDatabaseTableCreator_1);

		from = new Combo(container, SWT.READ_ONLY);
		from.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		from.setEnabled(false);

		l1 = new Label(container, SWT.NONE);
		l1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l1.setText(Messages.DialogDatabaseTableCreator_3);
		
		name = new Text(container, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		name.setText(Messages.DialogDatabaseTableCreator_4);
		
		go = new Button(container, SWT.PUSH);
		go.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		go.setText(Messages.DialogDatabaseTableCreator_2);
		go.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					getSql();
				} catch (Exception e1) {
					
					e1.printStackTrace();
				}
			}
			
		});
		

		
		createStatement= new Text(container, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		createStatement.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		
		fillDatas();
		
		return container;
	}
	
	
	private void fillDatas(){
	
		converterManager = new DBConverterManager("resources/conversion/"); //$NON-NLS-1$
		List<String> l = converterManager.getConvertersNames();
		String[] a = l.toArray(new String[l.size()]);
		

		from.setItems(a);
		
		
		
	}

	
	
	private void getSql() throws Exception{
			
		StreamDescriptor desc = transformation.getInputs().get(0).getDescriptor(transformation);
		StringBuffer buf = new StringBuffer("CREATE TABLE " + name.getText() + " (\n"); //$NON-NLS-1$ //$NON-NLS-2$
		selectStatement = "select "; //$NON-NLS-1$
		boolean first = true;
		for(StreamElement e : desc.getStreamElements()){
			if (first){
				first = false;
			}
			else{
				buf.append(",  \n"); //$NON-NLS-1$
				selectStatement += ", "; //$NON-NLS-1$
			}
			
			String completeName = e.name;
			String columnName = ""; //$NON-NLS-1$
			try {
				if(e.name.split("\\.").length>1) { //$NON-NLS-1$
					columnName = e.name.split("\\.")[1]; //$NON-NLS-1$
				}
				else {
					columnName = e.name;
				}
			} catch(Exception ec) {
				columnName = e.name;
			}
			
			
			selectStatement += e.name;
			String type = e.typeName;
			if (useMigrationFile.getSelection()){
				type = converterManager.getTypeSyntax(from.getText(), e.typeName);
			}
			else{
				type = e.typeName;
			}
			buf.append("\t"); //$NON-NLS-1$
			if (e.precision != null && !e.typeName.toLowerCase().contains("text")){ //$NON-NLS-1$
				
				if (e.typeName.contains(" ")){ //$NON-NLS-1$
					if (e.decimal != null && e.decimal.intValue() != 0){
						if (e.precision < e.decimal){
							e.precision = e.decimal;
						}
						buf.append(columnName +  " " + type.replace(" ", "(" + e.precision + "," +(e.decimal - 1) +") ")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					}
					else{
						buf.append(columnName +  " " + type.replace(" ", "(" + e.precision + ") ")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					}
				}
				else{
					if (e.decimal != null && e.decimal.intValue() != 0){
						e.precision = e.decimal;
						buf.append(columnName +  " " + type + "(" + e.precision + "," +e.decimal +")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					}
					else{
						buf.append(columnName +  " " + type + "(" + e.precision + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
				}
				
				
			}
			else{
				buf.append(columnName +  " " + converterManager.getTypeSyntax(from.getText(), e.typeName)); //$NON-NLS-1$
			}
	
		}
		
		buf.append(")"); //$NON-NLS-1$
		
		createStatement.setText(buf.toString());
		selectStatement += " from "; //$NON-NLS-1$
	}

	
	

	@Override
	protected void okPressed() {
		int start = createStatement.getText().toLowerCase().indexOf("create table") + "create table".length(); //$NON-NLS-1$ //$NON-NLS-2$
		
		tableName = createStatement.getText().substring(start, createStatement.getText().indexOf("(")).trim(); //$NON-NLS-1$
		
		
		
		
		Connection con = null;
		Statement stmt = null;
		try {
			if (((DataBaseConnection)transformation.getServer().getCurrentConnection(null)).getSocket(transformation.getDocument()) == null
					||
					((DataBaseConnection)transformation.getServer().getCurrentConnection(null)).getSocket(transformation.getDocument()).isClosed()){
				((DataBaseConnection)transformation.getServer().getCurrentConnection(null)).connect(transformation.getDocument());
			}
			
			con = ((DataBaseConnection)transformation.getServer().getCurrentConnection(null)).getSocket(transformation.getDocument());
			
			stmt = con.createStatement();
			
			
			stmt.execute(createStatement.getText());
	
			

//			((DataBaseConnection)transformation.getServer().getCurrentConnection()).disconnect();
			super.okPressed();
			MessageDialog.openInformation(getShell(), Messages.DialogDatabaseTableCreator_23, Messages.DialogDatabaseTableCreator_24);
			
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), Messages.DialogDatabaseTableCreator_25, e.getMessage());
		} 
		finally{
			if (stmt != null){
				try {
					stmt.close();
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
			}
			if(con != null){
				try {
					con.close();
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
			}
		}
		
		
		
		
	}


	@Override
	protected void initializeBounds() {
		this.getShell().setSize(800, 600);
		this.getButton(OK).setText(Messages.DialogDatabaseTableCreator_6);
	}
	
	public String getTableName(){
		return tableName;
		
	}
	
	public String getSelectStatement(){
		return selectStatement.replace("\n", "").replace("\t", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		
	}
	
	
	
	private String buildSelectStatement(String ddl){
		StringBuffer buf = new StringBuffer();
		buf.append("select "); //$NON-NLS-1$
		
		int startIndex = ddl.indexOf("("); //$NON-NLS-1$
		
		String[] cols = ddl.substring(startIndex + 1).split(","); //$NON-NLS-1$
		
		boolean first = true;
		for(String s : cols){
			try{
				String l = s.substring(0, s.indexOf(" ")); //$NON-NLS-1$
				if (first){
					first = false;
				}
				else{
					buf.append(", "); //$NON-NLS-1$
				}
				buf.append(l);
			}catch(Exception e){
				
			}
			
			
			
			
		}
		
		buf.append(" from " + tableName); //$NON-NLS-1$
		
		return buf.toString();
	}
	
	
	
}
