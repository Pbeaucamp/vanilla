package bpm.fd.design.ui.component.pages;

import java.util.Properties;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.wizard.IWizardComponent;

public class GeneralPage extends WizardPage{
	public static final String PAGE_NAME = "bpm.fd.design.ui.component.pages.GeneralPage"; //$NON-NLS-1$
	public static final String PAGE_TITLE = Messages.GeneralPage_1;
	public static final String PAGE_DESCRIPTION = Messages.GeneralPage_2;
	
	private Text name;
	private Text description;
	
	
	public static final String PROPERTY_NAME = "name"; //$NON-NLS-1$
	public static final String PROPERTY_DESCRIPTION = "description"; //$NON-NLS-1$
	
	public GeneralPage(){
		this(PAGE_NAME);
		this.setTitle(PAGE_TITLE);
		this.setDescription(PAGE_DESCRIPTION);
	}
	
	protected GeneralPage(String pageName) {
		super(pageName);
		
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		
		Label l = new Label(main, SWT.NONE);
		l.setText(Messages.GeneralPage_5);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		
		name = new Text(main, SWT.BORDER);
		name.setText(""); //$NON-NLS-1$
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		
		Label l2 = new Label(main, SWT.NONE);
		l2.setText(Messages.GeneralPage_7);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true));
		
		
		description = new Text(main, SWT.BORDER);
		description.setText(""); //$NON-NLS-1$
		description.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		setControl(main);
		
		/*
		 * init the componentName
		 */
		if ( getWizard() instanceof IWizardComponent){
			Class<?> c = ((IWizardComponent)getWizard()).getComponentClass();
			
			String s = c.getSimpleName() + "_"; //$NON-NLS-1$
			int i = 1;
			boolean  available = false;
			
			while(!available){
				IComponentDefinition def = Activator.getDefault().getProject().getDictionary().getComponent(s + i);
				if (def == null){
					available = true;
				}
				else{
					i++;
				}
			}
			
			name.setText(s + i);
			
		}
		name.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
				
			}
			
		});
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		if (!isNameOk(name.getText())){
			this.setErrorMessage(Messages.GeneralPage_10);
			return false;
		}
		this.setErrorMessage(null);
		return true;
	}

	
	boolean isNameOk(String name){
		if (name == null || name.equals("")){ //$NON-NLS-1$
			return false;
		}
		Dictionary dico = Activator.getDefault().getProject().getDictionary();
		for(IComponentDefinition c : dico.getComponents()){
			if (c.getName().equals(name)){
				return false;
			}
		}
		
		return true;
	}
	
	public Properties getValues(){
		Properties p = new Properties();
		p.setProperty(PROPERTY_NAME, name.getText());
		p.setProperty(PROPERTY_DESCRIPTION, description.getText());
		return p;
	}
}
