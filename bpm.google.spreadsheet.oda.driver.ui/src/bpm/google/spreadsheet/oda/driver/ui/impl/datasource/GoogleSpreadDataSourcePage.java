package bpm.google.spreadsheet.oda.driver.ui.impl.datasource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceWizardPage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
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

import bpm.google.spreadsheet.oda.driver.runtime.model.OdaGoogleProperties;
import bpm.google.spreadsheet.oda.driver.ui.Activator;
import bpm.google.spreadsheet.oda.driver.ui.Messages;
import bpm.google.spreadsheet.oda.driver.ui.icons.IconsName;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;



public class GoogleSpreadDataSourcePage extends DataSourceWizardPage{

	private final static String URL_GET_SPREADSHEET = "http://spreadsheets.google.com/feeds/spreadsheets/private/full"; //$NON-NLS-1$

	private Text login, password;
	private List listSpread;
	private Button btnOkConn;

	private java.util.List<SpreadsheetEntry> spreadsheets;
	private SpreadsheetEntry spreadSelected;
	private SpreadsheetService myService;
	
	private Properties prop;


	public GoogleSpreadDataSourcePage(String pageName) {
		super(pageName);
		
	}

	public GoogleSpreadDataSourcePage(String pageName, String title,
			ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		
	}

	public void createPageCustomControl(Composite pParent) {
		

		Composite parent = pParent;
		parent.setLayout(new GridLayout(2,false));

		setPingButtonVisible(false);
		setPageComplete(false);
		
		setMessage(Messages.GoogleSpreadDataSourcePage_1);
		
		PGroup group = new PGroup(parent, SWT.SMOOTH);
		group.setLayout(new GridLayout(2,false));
		group.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 4));
		group.setText(Messages.GoogleSpreadDataSourcePage_2);
		group.setImage(Activator.getDefault().getImageRegistry().get(IconsName.ICO_MAIL));
	

		//Adress	
		Label lblAdress = new Label(group, SWT.NONE);
		lblAdress.setText(Messages.GoogleSpreadDataSourcePage_3);
		lblAdress.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));

		login = new Text(group, SWT.BORDER);
		login.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1,1));


		//Password
		Label lblPass = new Label(group, SWT.NONE);
		lblPass.setText(Messages.GoogleSpreadDataSourcePage_4);
		lblPass.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));

		password = new Text(group, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1,1));

		//Valid connection
		btnOkConn = new Button(group, SWT.NONE);
		btnOkConn.setLayoutData(new GridData(GridData.CENTER, GridData.FILL, true, false, 2,1));
		btnOkConn.setText(Messages.GoogleSpreadDataSourcePage_5);
		btnOkConn.setImage(Activator.getDefault().getImageRegistry().get(IconsName.IMG_GOOGLE));

		
		
		//List to display SpreadSheets
		group = new PGroup(parent, SWT.SMOOTH);
		group.setText(Messages.GoogleSpreadDataSourcePage_6);
		group.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2,10));
		group.setLayout(new GridLayout(1, false));
		group.setImage(Activator.getDefault().getImageRegistry().get(IconsName.ICO_COLUMN));

		listSpread = new List(group, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL );
		listSpread.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 5));
	
		
		
		//Listenner on Ok button
		btnOkConn.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				
			
				try {
					
					myService = new SpreadsheetService(Messages.GoogleSpreadDataSourcePage_7);
					myService.setUserCredentials(login.getText(), password.getText());
					
					getAllSpreadSheet(myService);
					
					MessageDialog.openInformation(getShell(),
							Messages.GoogleSpreadDataSourcePage_8,
							Messages.GoogleSpreadDataSourcePage_9);

				} catch (AuthenticationException errorAuth) {
					
					listSpread.removeAll();
					
					MessageDialog .openError(getShell(), 
							Messages.GoogleSpreadDataSourcePage_10,
							Messages.GoogleSpreadDataSourcePage_11);
				}
				
			}
		});

		//Listenner on The list
		listSpread.addSelectionListener(new SelectionListener(){

			
			public void widgetDefaultSelected(SelectionEvent e) {
				

			}

			
			public void widgetSelected(SelectionEvent e) {

				for (int i = 0; i < spreadsheets.size(); i++) {
					setPageComplete(true);

					if(listSpread.getItem(listSpread.getSelectionIndex()).equals(spreadsheets.get(i).getTitle().getPlainText())){
						spreadSelected = spreadsheets.get(i);
					}
				}

			}

		});
		
		
		
		//Init widgets with properties if needed
		
		if(prop != null){
			login.setText(prop.getProperty(OdaGoogleProperties.P_USER));
			password.setText(prop.getProperty(OdaGoogleProperties.P_PASS));
			
			//Current list for this user.
			myService = new SpreadsheetService(Messages.GoogleSpreadDataSourcePage_12);
			try {
				myService.setUserCredentials(login.getText(), password.getText());
			
			} catch (AuthenticationException e2) {
				
				e2.printStackTrace();
				listSpread.removeAll();
				MessageDialog .openError(getShell(), 
						Messages.GoogleSpreadDataSourcePage_13,
						Messages.GoogleSpreadDataSourcePage_14);
			}
			
			getAllSpreadSheet(myService);
			setSelectedWorkSheet(prop);
			
		}


	}
	
	protected void setSelectedWorkSheet(Properties prop){
		
		String strSpreadSelected = prop.getProperty(OdaGoogleProperties.P_SPREADSHEET_SELECTED);
		int indexSpreadSelected = -1;
		
		for(int i = 0; i < listSpread.getItemCount(); i++){
			
			if(listSpread.getItem(i).equals(strSpreadSelected)){
				
				indexSpreadSelected = i;
			}
		}
		
	//test if the worksheet selected is available.
		if(indexSpreadSelected == -1){
			setErrorMessage(Messages.GoogleSpreadDataSourcePage_15 + strSpreadSelected + Messages.GoogleSpreadDataSourcePage_16);
		}
		else{
			listSpread.setSelection(indexSpreadSelected);
			spreadSelected = spreadsheets.get(indexSpreadSelected);
		}
		
	}

	protected void getAllSpreadSheet(SpreadsheetService myService) {
		
		try {
			
			listSpread.removeAll();
			
			URL metafeedUrl = new URL(URL_GET_SPREADSHEET);

			SpreadsheetFeed feed = myService.getFeed(metafeedUrl, SpreadsheetFeed.class);

			spreadsheets = feed.getEntries();


			SpreadsheetEntry entry = null;
			for (int i = 0; i < spreadsheets.size(); i++) {

				entry = spreadsheets.get(i);

				listSpread.add(entry.getTitle().getPlainText());

			}

		} catch (MalformedURLException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (ServiceException e) {
			
			e.printStackTrace();
		}


	}

	public Properties collectCustomProperties() {

		prop = new Properties();
		
	
		prop.setProperty(OdaGoogleProperties.P_USER, login.getText());
		prop.setProperty(OdaGoogleProperties.P_PASS, password.getText());
		prop.setProperty(OdaGoogleProperties.P_SPREADSHEET_SELECTED, spreadSelected.getTitle().getPlainText());
		
		return prop;
	}



	public void setInitialProperties(Properties propDS) {
		
		prop = propDS;

	}



}
