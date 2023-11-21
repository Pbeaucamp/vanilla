package bpm.gateway.ui.tools.dialogs;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.gateway.core.ITargetableStream;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.jdbc.JDBCToolBox;
import bpm.gateway.ui.i18n.Messages;

public class DialogSqlTableCreator extends Dialog {
	private static Font font = new Font(Display.getCurrent(), "Times New Roman", 12, SWT.ITALIC); //$NON-NLS-1$
	private ITargetableStream transformation;
	
	private Text tableName;
	private Text sqlTxt;
	
	private String backup;
	private String selectStatement; 
	
	public DialogSqlTableCreator(Shell parentShell, ITargetableStream transformation) {
		super(parentShell);
		this.transformation = transformation;
		setShellStyle(SWT.RESIZE | SWT.CLOSE);
	}


	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(container, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DialogSqlTableCreator_0);
		
		
		tableName = new Text(container, SWT.BORDER);
		tableName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		tableName.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				String sql = sqlTxt.getText();
				sqlTxt.setText(sql.replace(" " + backup + " ", " " +tableName.getText()+ " ")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				backup = tableName.getText();
				
			}
			
		});
		
		Label l2 = new Label(container, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false, 2, 1));
		l2.setText(Messages.DialogSqlTableCreator_5);
		
		sqlTxt = new Text(container, SWT.BORDER | SWT.V_SCROLL | SWT.WRAP | SWT.MULTI);
		sqlTxt.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
//		sqlTxt.setFont(font);
		
		
		return container;
	}



	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Messages.DialogSqlTableCreator_6, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);

	}


	@Override
	protected void okPressed() {
		
		
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
			if (!con.getMetaData().storesMixedCaseIdentifiers()){
				
				if (con.getMetaData().storesLowerCaseIdentifiers()){
					tableName.setText(tableName.getText().toLowerCase());
				}
				else if (con.getMetaData().storesUpperCaseIdentifiers()){
					tableName.setText(tableName.getText().toUpperCase());
				}
				
			}
			
			stmt.execute(sqlTxt.getText());
			stmt.close();
			backup = tableName.getText();
			
						
			selectStatement = buildSelectStatement(sqlTxt.getText());
//			((DataBaseConnection)transformation.getServer().getCurrentConnection()).disconnect();
			super.okPressed();
			MessageDialog.openInformation(getShell(), Messages.DialogSqlTableCreator_7, Messages.DialogSqlTableCreator_8);
			
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), Messages.DialogSqlTableCreator_9, e.getMessage());
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
	
	
	public String getSelectStatement(){
		return selectStatement;
	}
	
	private void fillDatas(){
		try {
			
			if (transformation.getTableName() != null && !transformation.getTableName().trim().equals("")){ //$NON-NLS-1$
				tableName.setText(transformation.getTableName());
				backup = new String(transformation.getTableName());
			}
			else{
				backup = "tableName"; //$NON-NLS-1$
				tableName.setText(backup);
			}
			
			StreamDescriptor desc = transformation.getDescriptor(transformation);
			
			if (desc.getStreamElements().size() == 0 && !transformation.getInputs().isEmpty()){
				desc = transformation.getInputs().get(0).getDescriptor(transformation); 
			}
			sqlTxt.setText(JDBCToolBox.getTableCreationInstruction(tableName.getText(), desc));
		} catch (ServerException e) {
			e.printStackTrace();
		}
	}


	@Override
	protected void initializeBounds() {
		this.getShell().setText(Messages.DialogSqlTableCreator_12);
		this.getShell().setSize(600, 400);
		fillDatas();
	}


	public String getTableName() {
		return backup;
	}
	
	
	/**
	 * create the select statement from the create statement
	 * @param s
	 * @return
	 */
	private String buildSelectStatement(String ddl){
		StringBuffer buf = new StringBuffer();
		buf.append("select "); //$NON-NLS-1$
		
		int startIndex = ddl.indexOf("("); //$NON-NLS-1$
		
		String[] cols = ddl.substring(startIndex + 1).split(","); //$NON-NLS-1$
		
		boolean first = true;
		for(String s : cols){
			if (first){
				first = false;
			}
			else{
				buf.append(", "); //$NON-NLS-1$
			}
			
			buf.append(s.substring(0, s.indexOf(" "))); //$NON-NLS-1$
			
		}
		
		buf.append(" from " + backup); //$NON-NLS-1$
		
		return buf.toString();
	}
}
