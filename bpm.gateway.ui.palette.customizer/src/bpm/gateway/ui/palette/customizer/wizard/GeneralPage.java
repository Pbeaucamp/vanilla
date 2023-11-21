package bpm.gateway.ui.palette.customizer.wizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class GeneralPage extends WizardPage{

	private Text paletteName;
	
	protected GeneralPage(String pageName, String title,ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		setControl(main);
		
		Label l = new Label(main, SWT.NONE);
		l.setText("Palette Name");
		l.setLayoutData(new GridData());
		
		paletteName = new Text(main, SWT.BORDER);
		paletteName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		paletteName.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
				
			}
		});
		
		
	}

	@Override
	public boolean isPageComplete() {
		return !paletteName.getText().equals("");
	}
	
	public String getPaletteName(){
		return paletteName.getText();
	}
}
