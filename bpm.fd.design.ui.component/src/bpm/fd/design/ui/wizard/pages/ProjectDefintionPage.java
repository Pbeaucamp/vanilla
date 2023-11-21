package bpm.fd.design.ui.wizard.pages;

import java.io.File;
import java.util.Calendar;

import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.fd.api.core.model.FdProjectDescriptor;
import bpm.fd.design.ui.component.Messages;
import bpm.fd.repository.ui.Activator;
import bpm.vanilla.platform.core.repository.RepositoryItem;


public class ProjectDefintionPage extends WizardPage {

	
	private Text description;
	private Text author;
	private Text projectName;
	private Text projectVersion;
	private Text modelName;
	private Text dictionaryName;
	
	private File dictionaryFile;
	private String xml;
	
	private boolean fromRepository = false;
	
	public ProjectDefintionPage(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(3, false));
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// create contents
		createPageContent(mainComposite);

		// page setting
		setControl( mainComposite );
		setPageComplete(true);

	}
	
	private void createPageContent(Composite parent){
		Label l3 = new Label(parent, SWT.NONE);
		l3.setLayoutData(new GridData());
		l3.setText(Messages.ProjectDefintionPage_0); 
		
		projectName = new Text(parent, SWT.BORDER);
		projectName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		projectName.addModifyListener(new ModifyListener(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
				
			}
			
		});
		
		Label l4 = new Label(parent, SWT.NONE);
		l4.setLayoutData(new GridData());
		l4.setText(Messages.ProjectDefintionPage_1); 
		
		projectVersion = new Text(parent, SWT.BORDER);
		projectVersion.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		
		
		Label l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.ProjectDefintionPage_2);
		
		modelName = new Text(parent, SWT.BORDER);
		modelName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		modelName.setText(Messages.ProjectDefintionPage_3);
		modelName.addModifyListener(new ModifyListener(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
				
			}
			
		});

		
		
		Label l6 = new Label(parent, SWT.NONE);
		l6.setLayoutData(new GridData());
		l6.setText(Messages.ProjectDefintionPage_4); 
		
		dictionaryName = new Text(parent, SWT.BORDER);
		dictionaryName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		dictionaryName.setText(Messages.ProjectDefintionPage_5);
		dictionaryName.addModifyListener(new ModifyListener(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			public void modifyText(ModifyEvent e) {
				dictionaryFile = new File(dictionaryName.getText());
				if (!dictionaryFile.exists()){
					dictionaryFile = null;	
				}
				if (getContainer() != null){
					getContainer().updateButtons();
				}
				
				
			}
			
		});
		
		
		Button b = new Button(parent, SWT.NONE);
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		b.setText("..."); //$NON-NLS-1$
		b.addSelectionListener(new SelectionAdapter(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {

				DialogImportDictionary d = new DialogImportDictionary(getShell());
				if (d.open() == DialogImportDictionary.OK){
					if (d.getSelectedObject() instanceof RepositoryItem){
						String s = ((RepositoryItem)d.getSelectedObject()).getItemName();
						fromRepository = true;
						if (!s.endsWith(".dictionary")){ //$NON-NLS-1$
							s+=".dictionary"; //$NON-NLS-1$
						}
						
						try{
							xml = Activator.getDefault().getRepositoryConnection().getRepositoryService().loadModel((RepositoryItem)d.getSelectedObject());

							dictionaryName.setText(s);
						}catch(Exception ex){
							ex.printStackTrace();
							MessageDialog.openError(getShell(), Messages.ProjectDefintionPage_9, Messages.ProjectDefintionPage_10 + ex.getMessage());
						}
						
						
						
						
					}
					else{
						xml = null;
						String s = (String)d.getSelectedObject();
						fromRepository = false;
//						dictionaryFile = new File(s);
//						if (dictionaryFile.exists()){
//							dictionaryName.setText(s);
//						}
//						else{
							dictionaryFile = new File(s);
//						}s
					}
				}
				
				
				getContainer().updateButtons();
				
			}
			
		});
		
		
		Label l1 = new Label(parent, SWT.NONE);
		l1.setLayoutData(new GridData());
		l1.setText(Messages.ProjectDefintionPage_11); 
		
		description = new Text(parent, SWT.BORDER);
		description.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		Label l2 = new Label(parent, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText(Messages.ProjectDefintionPage_12); 
		
		author = new Text(parent, SWT.BORDER);
		author.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

	
		//initFields
		
		
	}

	
	public String getXml(){
		return xml;
	}
	
	@Override
	public boolean isPageComplete() {
		return !modelName.getText().equals("") && (!dictionaryName.getText().equals("") || dictionaryFile != null); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	
	public boolean isFromRepository(){
		return fromRepository;
	}
	
	public File getDictionaryFile(){
		return dictionaryFile;
	}
	
	public FdProjectDescriptor getProjectDescriptor(){
		FdProjectDescriptor prop = new FdProjectDescriptor();
		
		prop.setModelName(modelName.getText());
		prop.setDictionaryName(dictionaryName.getText());
		prop.setProjectVersion(projectVersion.getText());
		prop.setProjectName(projectName.getText());
			
		
		prop.setDescription(description.getText());
		prop.setAuthor(author.getText());
		prop.setCreation(Calendar.getInstance().getTime());
		prop.setInternalApiDesignVersion(FdProjectDescriptor.API_DESIGN_VERSION);
		return prop;
	}

	
	
	
}
