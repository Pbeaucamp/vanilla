package bpm.google.table.oda.driver.ui.impl.datasource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.MatchResult;

import org.eclipse.nebula.widgets.pgroup.PGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

import bpm.google.table.oda.driver.runtime.impl.Connection;
import bpm.google.table.oda.driver.ui.Activator;
import bpm.google.table.oda.driver.ui.dialogs.MessageTable;
import bpm.google.table.oda.driver.ui.icons.IconsName;

import com.google.gdata.client.ClientLoginAccountType;
import com.google.gdata.client.GoogleService;
import com.google.gdata.client.Service.GDataRequest;
import com.google.gdata.client.Service.GDataRequest.RequestType;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ContentType;
import com.google.gdata.util.ServiceException;

public class ConnectionComposite extends Composite{

	private final static String QUERY_SHOW_TABLES = "SHOW TABLES";
	
	private final static String QUERY_DESCRIBE_TABLE = "DESCRIBE ";
	
	private final static String  START_DESCRIPTION_COLUMN = "col";


	private GoogleService service;

	private Text login, password;
	private List listTable;
	private Button btnOkConn;

	private ArrayList<Integer> listIDTables;
	private ArrayList<String> listNameTables, listNameColumns, listTypeColumn;
	
	
	private GoogleTableDataSourcePage page;

	public ConnectionComposite(Composite parent, int style, GoogleTableDataSourcePage pPage) {
		super(parent, style);

		this.setLayout(new GridLayout(2,false));
		page = pPage;
		
		buildContents();
		
		
	
	}

	private void buildContents() {
		
		listIDTables = new ArrayList<Integer>();
		listNameTables = new ArrayList<String>();
		listNameColumns = new ArrayList<String>();
		listTypeColumn = new ArrayList<String>();
		
	//-------- Widgets for G-Mail Connection -------
		
		
		PGroup group = new PGroup(this, SWT.SMOOTH);
		group.setLayout(new GridLayout(2,false));
		group.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 4));
		group.setText("G-Mail Connection");
		group.setImage(Activator.getDefault().getImageRegistry().get(IconsName.ICO_MAIL));
	

		//Adress	
		Label lblAdress = new Label(group, SWT.NONE);
		lblAdress.setText("G-Mail Adress:");
		lblAdress.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));

		login = new Text(group, SWT.BORDER);
		login.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1,1));


		//Password
		Label lblPass = new Label(group, SWT.NONE);
		lblPass.setText("G-Mail Password:");
		lblPass.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));

		password = new Text(group, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1,1));

		//Valid connection
		btnOkConn = new Button(group, SWT.NONE);
		btnOkConn.setLayoutData(new GridData(GridData.CENTER, GridData.FILL, true, false, 2,1));
		btnOkConn.setText(" Login");
		btnOkConn.setImage(Activator.getDefault().getImageRegistry().get(IconsName.IMG_GOOGLE));

		
		
		//List to display SpreadSheets
		group = new PGroup(this, SWT.SMOOTH);
		group.setText("Available Table(s)");
		group.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2,10));
		group.setLayout(new GridLayout(1, false));
		group.setImage(Activator.getDefault().getImageRegistry().get(IconsName.ICO_COLUMN));

		listTable = new List(group, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL );
		listTable.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 5));
		
		
	//------------ Button Connection ----------
		
		//Listenner on Ok button
		btnOkConn.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				
			
				try {
					service = new GoogleService("fusiontables", "fusiontables");
					service.setUserCredentials(login.getText(), password.getText(), ClientLoginAccountType.GOOGLE);
					
					getAllTables(service);
					
				} catch (AuthenticationException errorAuth) {
					
					listTable.removeAll();
					MessageTable.showError(getShell(), "Unable to connect to this g-mail account. Please verify your login/password \n or if you are correctly connected to Internet.");
				}
				
			}
		});
		
		
	//Listenner on The list
		listTable.addSelectionListener(new SelectionListener(){
			
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			
			public void widgetSelected(SelectionEvent e) {
				
				page.setPageComplete(true);
				
				//Get Column Name and type
				int idTableSelected = listIDTables.get(listTable.getSelectionIndex());
				
				getColumnDescription(service, idTableSelected);
				
				
			}

		});

		
	}
	

	
	protected void getColumnDescription(GoogleService service2, int idTable) {
	
	listNameColumns.clear();
	listTypeColumn.clear();
		
	 try {
	    	//++ Url to show Description's table for this service (Add the table's ID)
	    	  URL url = new URL(
	    			  Connection.URL_TABLE + "?sql=" + URLEncoder.encode(QUERY_DESCRIBE_TABLE + " " + idTable, "UTF-8"));

	       //++ Query
	        GDataRequest request = service.getRequestFactory().getRequest(
	                RequestType.QUERY, url, ContentType.TEXT_PLAIN);


	        request.execute();

	       //++ Scanner to extract tables names and id
	        
	        Scanner scanner = new Scanner(request.getResponseStream());
	        int indexMatchGroup = 0;
	        
	        while (scanner.hasNextLine()) {
	        	scanner.findWithinHorizon(Connection.CSV_VALUE_PATTERN, 0);
	        	
	        	MatchResult match = scanner.match();
	        	
	        	String quotedString = match.group(2);
	        	String decoded = quotedString == null ? match.group(1)
	        			: quotedString.replaceAll("\"\"", "\"");
	        	
	        	//Test for skip the two first row : "Table id" and "name"
	        	if(indexMatchGroup >= 3 ){
	        		
	        		//Scanner is like : col1 , IS , NUMBER , ...
	        		if(!decoded.startsWith(START_DESCRIPTION_COLUMN)){
	        			
	        			if(indexMatchGroup % 2 == 0){
	        				listTypeColumn.add(decoded);
		        		}
		        		else{
		        			
		        			listNameColumns.add(decoded);
		        		}
	        		}
	        		else{
	        			indexMatchGroup++;
	        		}
	        		
	        	}
	        	indexMatchGroup++;
	        }
	        
		} catch (MalformedURLException e) {
			MessageTable.showError(getShell(), "Unable to display table's List. \n Reason : Url error.");
			e.printStackTrace();
		} catch (IOException e) {
			MessageTable.showError(getShell(), "Unable to display table's List. \n Reason : Unexpected Error");
			e.printStackTrace();
		} catch (ServiceException e) {
			MessageTable.showError(getShell(), "Unable to display table's List. \n Reason : Google Service Error");
			e.printStackTrace();
		}
		
		
	}

	protected void getAllTables(GoogleService service2) {
		
		listTable.removeAll();
		listIDTables.clear();
		listNameTables.clear();
		
		
	    try {
	    	//++ Url to show all table for this service.
	    	  URL url = new URL(
	    			  Connection.URL_TABLE + "?sql=" + URLEncoder.encode(QUERY_SHOW_TABLES, "UTF-8"));

	       //++ Query
	        GDataRequest request = service.getRequestFactory().getRequest(
	                RequestType.QUERY, url, ContentType.TEXT_PLAIN);


	        request.execute();

	       //++ Scanner to extract tables names and id
	        

	        Scanner scanner = new Scanner(request.getResponseStream());
	        int indexMatchGroup = 0;
	        
	        while (scanner.hasNextLine()) {
	        	scanner.findWithinHorizon(Connection.CSV_VALUE_PATTERN, 0);
	        	
	        	MatchResult match = scanner.match();
	        	
	        	String quotedString = match.group(2);
	        	String decoded = quotedString == null ? match.group(1)
	        			: quotedString.replaceAll("\"\"", "\"");
	        	
	        	//Test for skip the two first row : "Table id" and "name"
	        	if(indexMatchGroup >= 2 ){
	        		
	        		if(indexMatchGroup % 2 == 0){
	        			listIDTables.add(Integer.valueOf(decoded));
	        		}
	        		else{
	        			listNameTables.add(decoded);
	        			listTable.add(decoded);
	        		}
	        	}
	        	indexMatchGroup++;
	        }
	        
		} catch (MalformedURLException e) {
			MessageTable.showError(getShell(), "Unable to display table's List. \n Reason : Url error.");
			e.printStackTrace();
		} catch (IOException e) {
			MessageTable.showError(getShell(), "Unable to display table's List. \n Reason : Unexpected Error");
			e.printStackTrace();
		} catch (ServiceException e) {
			MessageTable.showError(getShell(), "Unable to display table's List. \n Reason : Google Service Error");
			e.printStackTrace();
		}
		
	}

	public String getTableRowCount(){
		
		String result = null;
		
		//Request to get Datas
		  try {
			  //Request like : SELECT * FROM <Id-table>
			  String querySelect = Connection.QUERY_COUNT_ALL + getIdTable();
			  
			  URL url = new URL(
					  Connection.URL_TABLE + "?sql=" + URLEncoder.encode(querySelect, "UTF-8"));
				    GDataRequest request = service.getRequestFactory().getRequest(
				            RequestType.QUERY, url, ContentType.TEXT_PLAIN);

				    request.execute();
				 
				    Scanner scanner = new Scanner(request.getResponseStream());
				    
				  					    
				    while (scanner.hasNextLine()) {
				      scanner.findWithinHorizon(Connection.CSV_VALUE_PATTERN, 0);
				      MatchResult match = scanner.match();
				      String quotedString = match.group(2);
				      String decoded = quotedString == null ? match.group(1)
				          : quotedString.replaceAll("\"\"", "\"");
				      
				      result =  decoded;
				      
				    }
				    
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ServiceException e) {
				e.printStackTrace();
			}
			
		return result;
		
	}
	
	
	public void initWidgetWithPropertie(java.util.Properties profileProps){
		
		if(profileProps != null){
			
			listIDTables = new ArrayList<Integer>();
			listNameTables = new ArrayList<String>();
			listNameColumns = new ArrayList<String>();
			listTypeColumn = new ArrayList<String>();
			
			try {
				service = new GoogleService("fusiontables", "fusiontables");
				service.setUserCredentials(profileProps.getProperty(Connection.P_USER), profileProps.getProperty(Connection.P_PASS), ClientLoginAccountType.GOOGLE);
				
				getAllTables(service);
				
			} catch (AuthenticationException errorAuth) {
				
				listTable.removeAll();
				MessageTable.showError(getShell(), "Unable to connect to this g-mail account. Please verify your login/password \n or if you are correctly connected to Internet.");
			}
			
			// + Login
			login.setText(profileProps.getProperty(Connection.P_USER));
			
			// + Pass
			password.setText(profileProps.getProperty(Connection.P_PASS));
			
			// + Set list Seletion
			int idTable = Integer.valueOf(profileProps.getProperty(Connection.P_ID_TABLE));
			boolean findIndex = false;
			int i = 0, indexSelection = 0;;
			
			while((!findIndex) && (i < listIDTables.size())){
				
				if(idTable == listIDTables.get(i)){
					findIndex = true;	
					indexSelection = i;
				}
				i++;
			}
			
			listTable.setSelection(indexSelection);
			
			
			
			// + Current Columns description
			getColumnDescription(service, Integer.valueOf(profileProps.getProperty(Connection.P_ID_TABLE)));
			

		}
	
	
	
}

	public String getUser(){
		return login.getText();
	}
	
	public String getPass(){
		return password.getText();
	}
	
	public String getIdTable(){

		int indexTableSelected = listTable.getSelectionIndex();

		return(String.valueOf(listIDTables.get(indexTableSelected)));

	}
	
	public String getColumnsName(){
		
		String temp = "";
		
		for(String name : listNameColumns){
			temp += name + Connection.PROPERTIES_SEPARATOR;
		}
		
		return temp;
	}
	
	public String getColumnsType(){
		
		String temp = "";
		
		for(String name : listTypeColumn){
			temp += name.toLowerCase() + Connection.PROPERTIES_SEPARATOR;
		}
		
		return temp;
	}
	
	public String getColumnsCount(){
		
		return String.valueOf(listNameColumns.size());
	}
	
	
	
	public boolean existSelection(){
		
		if(listTable.getSelectionIndex() == -1){
			MessageTable.showError(getShell(), "Please, select a table to valid your DataSource");
			return false;
		}
		else{
			return true;
		}
		
	}

}
