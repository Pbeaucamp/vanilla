package bpm.fd.design.ui.component.buttons.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.fd.api.core.model.components.definition.buttons.ButtonOptions;
import bpm.fd.design.ui.component.Messages;

public class ButtonPage extends WizardPage{
	public static final String PAGE_NAME = "bpm.fd.design.ui.component.buttons.pages.ButtonPage"; //$NON-NLS-1$
	public static final String PAGE_TITLE = "Button Page"; //$NON-NLS-1$
	public static final String PAGE_DESCRIPTION = Messages.ButtonPage_2;

	
	
	private Text labelContent;
	
	
	public ButtonPage(){
		this(PAGE_NAME);
		this.setTitle(PAGE_TITLE);
		this.setDescription(PAGE_DESCRIPTION);
	}
	
	protected ButtonPage(String pageName) {
		super(pageName);
	}
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		
		
			
		

		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true));
		l.setText(Messages.ButtonPage_3);
		
		labelContent = new Text(main, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		labelContent.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		labelContent.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
				
			}
			
		});
		

		setControl(main);
	}

	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		return !labelContent.getText().equals(""); //$NON-NLS-1$
	}

	public ButtonOptions getOptions() {
		ButtonOptions opt = new ButtonOptions();
		opt.setLabel(labelContent.getText());
		return opt;
	}
	
	
}
