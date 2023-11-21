package bpm.es.pack.manager.imp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.es.pack.manager.I18N.Messages;

public class CompositeConnectionSql extends CompositeInformation {

	private Combo driver;
	private Text host, dataBase, port, login, password, schema;
	public static HashMap<String, String> driverName;
	public static HashMap<String, String> driverPrefix;
	
	static{
		driverName = new HashMap<String, String>();
		driverPrefix = new HashMap<String, String>();
		Document doc;
		try {
			doc = DocumentHelper.parseText(IOUtils.toString(new FileInputStream("resources/driverjdbc.xml"), "UTF-8")); //$NON-NLS-1$ //$NON-NLS-2$
			Element root = doc.getRootElement();
			
			for(Object e : root.elements("driver")){ //$NON-NLS-1$
				driverName.put(((Element)e).attributeValue("name"), ((Element)e).attributeValue("className")); //$NON-NLS-1$ //$NON-NLS-2$
				driverPrefix.put(((Element)e).attributeValue("name"), ((Element)e).attributeValue("prefix")); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} catch (FileNotFoundException e1) {
			
			e1.printStackTrace();
		} catch (DocumentException e1) {
			
			e1.printStackTrace();
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
		
	}
	
	
//	private Text dataSourceName, name;
	
	
	
	
	
	
//	private SQLConnection sock = null; 
	
	public CompositeConnectionSql(Composite parent, int style) {
		super(parent, style);
		
		
		buildContent();
	}
	
		
	
	
		
	protected void buildContent(){
		this.setLayout(new GridLayout(2, false));
			
		
		Label l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(bpm.es.pack.manager.I18N.Messages.CompositeConnectionSql_3); 
		
		driver = new Combo(this, SWT.READ_ONLY);
		driver.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		driver.setItems(driverName.keySet().toArray(new String[driverName.size()]));
		
		
		Label l2 = new Label(this, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText(Messages.CompositeConnectionSql_8);
		
		host = new Text(this, SWT.BORDER);
		host.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			
		
		Label l3 = new Label(this, SWT.NONE);
		l3.setLayoutData(new GridData());
		l3.setText(Messages.CompositeConnectionSql_9); 
		
		port = new Text(this, SWT.BORDER);
		port.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			
		Label l4 = new Label(this, SWT.NONE);
		l4.setLayoutData(new GridData());
		l4.setText(Messages.CompositeConnectionSql_10); 
		
		dataBase = new Text(this, SWT.BORDER);
		dataBase.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			
		
		Label l5 = new Label(this, SWT.NONE);
		l5.setLayoutData(new GridData());
		l5.setText(Messages.CompositeConnectionSql_11);
		
		login = new Text(this, SWT.BORDER);
		login.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			
		
		Label l6 = new Label(this, SWT.NONE);
		l6.setLayoutData(new GridData());
		l6.setText(Messages.CompositeConnectionSql_12);
		
		password = new Text(this, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label l7 = new Label(this, SWT.NONE);
		l7.setLayoutData(new GridData());
		l7.setText(Messages.CompositeConnectionSql_13);
		
		schema = new Text(this, SWT.BORDER);
		schema.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		
//		Button test = new Button(this, SWT.PUSH);
//		test.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
//		test.setText("Test"); 
//		test.addSelectionListener(new SelectionAdapter(){
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//							
//			}
//			
//		});
		
		super.buildContent();
	}

	


	@Override
	public Properties getProperties() {
		Properties p = new Properties();
		
		p.put("dataBase", dataBase.getText()); //$NON-NLS-1$
		p.put("driver", driverName.get(driver.getText())); //$NON-NLS-1$
		p.put("host", host.getText()); //$NON-NLS-1$
		p.put("port", port.getText()); //$NON-NLS-1$
		p.put("schemaName", schema.getText()); //$NON-NLS-1$
		p.put("login", login.getText()); //$NON-NLS-1$
		p.put("password", password.getText()); //$NON-NLS-1$
		
		return p;
	}





	@Override
	public void setProperties(Properties prop) {
		if (prop.getProperty("dataBase") != null){ //$NON-NLS-1$
			dataBase.setText(prop.getProperty("dataBase")); //$NON-NLS-1$
		}
		if (prop.getProperty("driver") != null){ //$NON-NLS-1$
			//driver.setText(prop.getProperty("driver"));
			if (prop.getProperty("driver").contains(".")){ //$NON-NLS-1$ //$NON-NLS-2$
				int i =0;
				for(String s : driverName.values()){
					if (s.equals(prop.getProperty("driver"))){ //$NON-NLS-1$
						driver.select(i);
						break;
					}
					i++;
				}
			}
			else{
				int i =0;
				for(String s : driverName.keySet()){
					if (s.equals(prop.getProperty("driver"))){ //$NON-NLS-1$
						driver.select(i);
						break;
					}
					i++;
				}
			}
			
		}
		if (prop.getProperty("host") != null){ //$NON-NLS-1$
			host.setText(prop.getProperty("host")); //$NON-NLS-1$
		}
		if (prop.getProperty("port") != null){ //$NON-NLS-1$
			port.setText(prop.getProperty("port")); //$NON-NLS-1$
		}
		if (prop.getProperty("dataBase") != null){ //$NON-NLS-1$
			schema.setText(prop.getProperty("dataBase")); //$NON-NLS-1$
		}
		if (prop.getProperty("login") != null){ //$NON-NLS-1$
			login.setText(prop.getProperty("login")); //$NON-NLS-1$
		}
		if (prop.getProperty("password") != null){ //$NON-NLS-1$
			password.setText(prop.getProperty("password")); //$NON-NLS-1$
		}
	}

	
	
		
	
}
