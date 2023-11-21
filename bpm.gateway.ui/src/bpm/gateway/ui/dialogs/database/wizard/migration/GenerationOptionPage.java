package bpm.gateway.ui.dialogs.database.wizard.migration;

import java.util.Properties;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.gateway.ui.i18n.Messages;

public class GenerationOptionPage extends WizardPage implements IGatewayWizardPage{

	private Text creationScript;
	private Button createDocPerTable;
	private Text creationScriptFilePath;
	private Text truncateScriptFilePath;
	private ModifyListener modifyListener;
	
	protected GenerationOptionPage(String pageName) {
		super(pageName);
		
	}
	
	
	
	
	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// create contents
		createPageContent(mainComposite);

		// page setting
		setControl( mainComposite );
		setPageComplete(false);

	}
	
	private void createPageContent(Composite parent){
		modifyListener = new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
				
			}
			
		};
		
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, true));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
				
		Group fileGroup = new Group(main, SWT.NONE);
		fileGroup.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		fileGroup.setLayout(new GridLayout(3, false));
		fileGroup.setText(Messages.GenerationOptionPage_0);
		
		Label l2 = new Label(fileGroup, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.GenerationOptionPage_1);
		
		creationScriptFilePath = new Text(fileGroup, SWT.BORDER);
		creationScriptFilePath.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		creationScriptFilePath.addModifyListener(modifyListener);
		
		Button b = new Button(fileGroup, SWT.PUSH);
		
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		b.setText("..."); //$NON-NLS-1$
		b.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getShell());
				fd.setFilterExtensions(new String[]{"*.*", "*.sql"}); //$NON-NLS-1$ //$NON-NLS-2$
				String s = fd.open();
				if (s != null){
					creationScriptFilePath.setText(s);
				}
			}
			
		});
		
		
		Label l3 = new Label(fileGroup, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Messages.GenerationOptionPage_5);
		
		truncateScriptFilePath = new Text(fileGroup, SWT.BORDER);
		truncateScriptFilePath.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		truncateScriptFilePath.addModifyListener(modifyListener);

		Button b1 = new Button(fileGroup, SWT.PUSH);
		b1.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		b1.setText("..."); //$NON-NLS-1$
		b1.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getShell());
				fd.setFilterExtensions(new String[]{"*.*", "*.sql"}); //$NON-NLS-1$ //$NON-NLS-2$
				String s = fd.open();
				if (s != null){
					truncateScriptFilePath.setText(s);
				}
			}
			
		});
		
		createDocPerTable = new Button(main, SWT.CHECK);
		createDocPerTable.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		createDocPerTable.setText(Messages.GenerationOptionPage_9);
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 2, 1));
		l.setText(Messages.GenerationOptionPage_10);
		
		creationScript = new Text(main, SWT.BORDER|SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		creationScript.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		creationScript.addModifyListener(modifyListener);

	}
	
	
	

	
	public Properties getPageProperties(){
		Properties p = new Properties();
		p.setProperty("creationScript", creationScript.getText()); //$NON-NLS-1$
		p.setProperty("creationFile", creationScriptFilePath.getText()); //$NON-NLS-1$
		p.setProperty("truncateFile", truncateScriptFilePath.getText()); //$NON-NLS-1$
		if (createDocPerTable.getSelection()){
			p.setProperty("multipleTransformation", ""  + createDocPerTable.getSelection()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		
		
		return p;
	}




	@Override
	public boolean isPageComplete() {
		return !(creationScript.getText().equals("") || creationScriptFilePath.getText().equals("") || truncateScriptFilePath.getText().equals("")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}




	public void setScript(String createScript) {
		creationScript.removeModifyListener(modifyListener);
		creationScript.setText(createScript);
		
		creationScript.addModifyListener(modifyListener);
		getContainer().updateButtons();
		
	}




	@Override
	public boolean canFlipToNextPage() {
		return false;
	}
	
	
}
