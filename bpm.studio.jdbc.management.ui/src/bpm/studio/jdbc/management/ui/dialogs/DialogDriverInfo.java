package bpm.studio.jdbc.management.ui.dialogs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


import bpm.studio.jdbc.management.config.IConstants;
import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.studio.jdbc.management.model.ListDriver;
import bpm.studio.jdbc.management.util.JarClassFinder;

public class DialogDriverInfo extends Dialog{

	

	private Text name;
	private Text driverFile;
	private Text urlPrefix;
	private ComboViewer driverClass;
	
	private DriverInfo driverInfo;
	
	public DialogDriverInfo(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		main.setLayout(new GridLayout(3, false));
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Driver Name");
		
		name = new Text(main, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		name.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				check();
				
			}
		});
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Jdbc Jar File");
		
		driverFile = new Text(main, SWT.BORDER);
		driverFile.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		driverFile.setEnabled(false);
		
		Button b = new Button(main, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		b.setText("...");
		b.setToolTipText("Select a File");
		b.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
				fd.setFilterExtensions(new String[]{"*.jar"});
				fd.setFilterPath(Platform.getInstallLocation().getURL().getFile());
				
				
				
				
				String filePath = fd.open().replace("\\", "/").replace("//", "/");
				String shortFileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
				
				
				if (! (filePath.startsWith(IConstants.getJdbcJarFolder().replace("//", "/")) || ("/" + filePath).startsWith(IConstants.getJdbcJarFolder().replace("\\", "/").replace("//", "/")))){
					/*
					 * we need to copy the file in the /resources/jdbc folder
					 */
					
					try{
						FileInputStream fis = new FileInputStream(filePath);
						FileOutputStream fos = new FileOutputStream(IConstants.getJdbcJarFolder() + "/" + shortFileName);
						
												
						byte[] buf = new byte[1024];
						int l;
						while( (l = fis.read(buf)) > 0){
							fos.write(buf, 0, l);
							
						}
						
						fos.close();

						fis.close();
					}catch(Exception ex){
						ex.printStackTrace();
					}
					
				}
				
				driverFile.setText(shortFileName);
				try{
					driverClass.setInput(JarClassFinder.getClassImplementingJdbcDriver(filePath));
					
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(getShell(), "Problem", "Unable to analyze Jar file : " + ex.getMessage());
					driverClass.setInput(new ArrayList<Object>());
					if (!filePath.startsWith(IConstants.getJdbcJarFolder())){
						new File(filePath).delete();
					}
				}
				check();
			}
		});
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Driver class");
		
		driverClass = new ComboViewer(main, SWT.READ_ONLY);
		driverClass.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		driverClass.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				Collection<?> c = (Collection<?>)inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
		driverClass.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				Class<? extends Driver> c = (Class)element;
				return c.getName();
			}
		});
		driverClass.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				check();
				
			}
		});
		
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Url prefix");
		
		urlPrefix = new Text(main, SWT.BORDER);
		urlPrefix.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		urlPrefix.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				check();
				
			}
		});
				
		
		return main;
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setText("Jdbc Driver Info");
		getShell().setSize(600, 400);
	}
	
	
	private void check(){
		boolean valid = true;
		
		if ("".equals(name.getText().trim())){
			valid = false;
		}
		try{
			if (driverInfo != null && ListDriver.getInstance(Platform.getInstallLocation().getURL().getFile() + "/resources/driverjdbc.xml").getInfo(name.getText()) != null){
				valid = false;
			}

		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		if ("".equals(urlPrefix.getText().trim())){
			valid = false;
		}

		if (driverClass.getSelection().isEmpty()){
			valid = false;
		}
		
		getButton(IDialogConstants.OK_ID).setEnabled(valid);
				
	}
	
	@Override
	protected void okPressed() {
		if (driverInfo == null){
			driverInfo = new DriverInfo();
		}
		
		driverInfo.setClassName(((Class)((IStructuredSelection)driverClass.getSelection()).getFirstElement()).getName());
		driverInfo.setFile(driverFile.getText());
		driverInfo.setName(name.getText());
		driverInfo.setUrlPrefix(urlPrefix.getText());
		
		
		
		
		
		super.okPressed();
	}
	
	public DriverInfo getDriverInfo(){
		return driverInfo;
	}


	public String getFilePath() {
		
		return null;
	}
}
