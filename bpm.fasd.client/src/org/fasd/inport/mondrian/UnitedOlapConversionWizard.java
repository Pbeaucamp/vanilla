package org.fasd.inport.mondrian;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.fasd.i18N.LanguageText;
import org.fasd.inport.converter.MondrianToUOlapConverter;
import org.fasd.olap.FAModel;

public class UnitedOlapConversionWizard extends Dialog {

	private Button btnFromFile, btnFromRepository;
	
	protected UnitedOlapConversionWizard(Shell parentShell) {
		super(parentShell);
	}

	protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(LanguageText.UnitedOlapConversionWizard_0);
    }
	
	@Override
	protected Control createDialogArea(Composite parent) {
		
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(2,false));
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label lblConvertFrom = new Label(mainComposite, SWT.NONE);
		lblConvertFrom.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false,2,1));
		lblConvertFrom.setText(LanguageText.UnitedOlapConversionWizard_1);
		
		btnFromFile = new Button(mainComposite, SWT.PUSH);
		btnFromFile.setLayoutData(new GridData(SWT.FILL,SWT.FILL, false, false));
		btnFromFile.setImage(new Image(mainComposite.getDisplay(),Platform.getInstallLocation().getURL().getPath() +"icons/Open.png")); //$NON-NLS-1$
		btnFromFile.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog dial = new FileDialog(UnitedOlapConversionWizard.this.getShell());
				String filePath = dial.open();
				
				try {
					FAModel model = MondrianToUOlapConverter.convertFromFile(filePath);
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getShell(), LanguageText.UnitedOlapConversionWizard_3, LanguageText.UnitedOlapConversionWizard_4 + e1.getMessage());
				}
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		Label lblFromFile = new Label(mainComposite, SWT.NONE);
		lblFromFile.setLayoutData(new GridData(SWT.FILL,SWT.FILL, false, false));
		lblFromFile.setText(LanguageText.UnitedOlapConversionWizard_5);
		
		btnFromRepository = new Button(mainComposite, SWT.PUSH);
		btnFromRepository.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false));
		btnFromRepository.setImage(new Image(mainComposite.getDisplay(),Platform.getInstallLocation().getURL().getPath() +"icons/Open.png")); //$NON-NLS-1$
		btnFromRepository.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		Label lblFromRepository = new Label(mainComposite, SWT.NONE);
		lblFromRepository.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false));
		lblFromRepository.setText(LanguageText.UnitedOlapConversionWizard_7);
		
		return parent;
	}

	
	
}
