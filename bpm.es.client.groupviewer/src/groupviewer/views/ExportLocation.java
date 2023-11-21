package groupviewer.views;


import groupviewer.Messages;

import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;


public class ExportLocation extends WizardPage implements Listener{
	
	private static String url = ""; //$NON-NLS-1$
	private Button browseBtn;
	private Text urlField;
	private Button launch;
	private ExportModel model;

	public ExportLocation(ExportModel model){
		super("Page name test"); //$NON-NLS-1$
		setTitle(Messages.ExportLocation_2);
		setDescription(Messages.ExportLocation_3);
		setModel(model);
	}
		
	public void createControl(Composite parent) {
		// Main
		Composite main = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(2, false);
		gl.verticalSpacing = 8;
		main.setLayout(gl);
		main.setLayoutData(new GridData(SWT.FILL));
		
		// Title
		Label lab = new Label(main,SWT.HORIZONTAL | SWT.CENTER);
		lab.setText(Messages.ExportLocation_4);
		lab.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		
		// Separator
		Label separator = new Label(main,SWT.HORIZONTAL | SWT.CENTER | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true, false,2,1));
		
		// URL Label
		Label urlLab = new Label(main, SWT.HORIZONTAL);
		urlLab.setText(Messages.ExportLocation_5);
		
		// URL Field
		urlField = new Text(main, SWT.SINGLE);
		urlField.setText(url);
		urlField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		// Browse Button
		browseBtn = new Button(main,SWT.PUSH);
		browseBtn.setText(Messages.ExportLocation_6);
		browseBtn.setLayoutData(new GridData(SWT.RIGHT,SWT.CENTER,true, false,2,1));
		
		// Launch Button
		launch = new Button(main, SWT.CHECK);
		launch.setText(Messages.ExportLocation_7);
		launch.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, true,2,1));
		
		addListeners();
		setControl(main);
		loadSavedData();
		setPageComplete(true);
	}
	private void loadSavedData() {
		String appRoot = getModel().getAppRootPath();
		
		if (appRoot != null)
			urlField.setText(appRoot);
		
		launch.setSelection(getModel().isRunApp());
	}

	private void addListeners(){
		browseBtn.addSelectionListener(new SelectionAdapter() {
			
		      public void widgetSelected(SelectionEvent e) {
	    		  DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.OPEN);
	    		  urlField.setText(dialog.open());
		        }
		      });
		
		launch.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (getModel().isRunApp() != ((Button)e.widget).getSelection())
					getModel().setRunApp(((Button)e.widget).getSelection());	
			}
		});
		
		urlField.addListener(SWT.Modify, this);
	}
	public void handleEvent(Event event) {
		Status status = new Status(IStatus.OK, "not_used", 0, "", null); //$NON-NLS-1$ //$NON-NLS-2$
		
		if (event.widget == urlField){
			if (!checkURL()){
				status = new Status(IStatus.ERROR, "not_used", 0,  //$NON-NLS-1$
				           "Wrong location : Cannot find valid Flex Application", null);	 //$NON-NLS-1$
			}
			// Update Wizard Navigation Buttons
			getWizard().getContainer().updateButtons();
		}
		applyToStatusLine(status);
	}
	private boolean checkURL() {
		//if ()
		boolean retour = false;
		File app = new File(urlField.getText()+getModel().getAppName());
		retour = app.exists();
		//if (retour){
		//	File data = new File(urlField.getText()+"/data/graph.xml");
		//	retour = data.exists();
		//}		
		return retour;
	}
	
	private void applyToStatusLine(IStatus status) {
		String message= status.getMessage();
		if (message.length() == 0) message= null;
		switch (status.getSeverity()) {
			case IStatus.ERROR:
				setErrorMessage(message);
				setMessage(null);
				break;
			case IStatus.OK:
				setErrorMessage(null);
				setMessage(Messages.ExportLocation_12);
				break;		
		}
	}	
	@Override
	public boolean canFlipToNextPage() {	
		return checkURL();
	}
	
	private void setModel(ExportModel model){
		this.model = model;
	}
	
	protected ExportModel getModel(){
		return this.model;
	}
}
