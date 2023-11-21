package bpm.excel.oda.ui.impl.localdatasource;

import java.nio.charset.Charset;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.excel.oda.runtime.impl.Connection;



public class LocalConnectionComposite  {

	private Composite client;
	private Text filePath;
	private Combo encoding;
	
	
	public Composite createContent(Composite parent){
		client = new Composite(parent, SWT.NONE);
		client.setLayout(new GridLayout(3, false));
		
		Label l = new Label(client, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Xls File Path");
		
		filePath = new Text(client, SWT.BORDER);
		filePath.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		filePath.setEnabled(false);
		filePath.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				getClient().notifyListeners(SWT.Modify, new Event());
				
			}
		});
	
	
		Button b = new Button(client, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		b.setText("...");
		b.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getClient().getShell(), SWT.OPEN);
				
				fd.setFilterExtensions(new String[]{"*.xls"});
				
				String s = fd.open();
				if (s != null){
					filePath.setText(s);
				}
			}
		});
	
	
		l = new Label(client, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Xls Encoding");
		
		
		encoding = new Combo(client, SWT.READ_ONLY);
		encoding.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		encoding.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getClient().notifyListeners(SWT.Modify, new Event());
			}
		});
		
		
		encoding.setItems(Charset.availableCharsets().keySet().toArray(new String[Charset.availableCharsets().size()]));
		encoding.setText("UTF-8");
		
		return client;
	}
	
	public Composite getClient(){
		return client;
	}
	
	/**
	 * properties Connection.FILE_NAME / Connection.ENCODING
	 * @return
	 */
	public Properties getFileInfo(){
		Properties p = new Properties();
		p.setProperty(Connection.FILE_NAME, filePath.getText());
		p.setProperty(Connection.ENCODING, encoding.getText());
		
		return p;
	}

	public void fill(Properties initialProperties) {
		if (initialProperties != null ){
			if (initialProperties.getProperty(Connection.FILE_NAME) != null){
				filePath.setText(initialProperties.getProperty(Connection.FILE_NAME));
			}
			if (initialProperties.getProperty(Connection.FILE_NAME) != null){
				encoding.setText(initialProperties.getProperty(Connection.ENCODING));
			}
		}
		
	}
}
