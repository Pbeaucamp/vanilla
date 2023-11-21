package bpm.birep.admin.client.preferences.pages;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.birep.admin.client.preferences.PreferenceConstants;


public class PageVanillaPlace extends PreferencePage implements IWorkbenchPreferencePage {

	private Text vanillaPlaceUrl;
	private Text log;
	private Text pass;
	
	
	public PageVanillaPlace() {
		super();
	}
	
	@Override
	protected Control createContents(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		
		Label l = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.Client_Preferences_PageVanillaPlace_0);

		vanillaPlaceUrl = new Text(c, SWT.BORDER);
		vanillaPlaceUrl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		vanillaPlaceUrl.setText(Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_BPM_VANILLA_PLACE_URL));
		
//		Label lblLog = new Label(c, SWT.NONE);
//		lblLog.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
//		lblLog.setText(Messages.PageVanillaPlace_0);
//		
//		log = new Text(c, SWT.BORDER);
//		log.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		log.setText(Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_BPM_VANILLA_PLACE_LOGIN));
//		
//		Label lblPass = new Label(c, SWT.NONE);
//		lblPass.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
//		lblPass.setText(Messages.PageVanillaPlace_1);
//		
//		pass = new Text(c, SWT.BORDER | SWT.PASSWORD);
//		pass.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		pass.setText(Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_BPM_VANILLA_PLACE_PASSWORD));
		
		return c;
	}
	
	public void init(IWorkbench workbench) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(store);
	}

	@Override
	protected void performApply() {
		super.performApply();
		Activator.getDefault().getPreferenceStore().setValue(PreferenceConstants.P_BPM_VANILLA_PLACE_URL, vanillaPlaceUrl.getText());
		Activator.getDefault().getPreferenceStore().setValue(PreferenceConstants.P_BPM_VANILLA_PLACE_LOGIN, log.getText());
		Activator.getDefault().getPreferenceStore().setValue(PreferenceConstants.P_BPM_VANILLA_PLACE_PASSWORD, pass.getText());
		Activator.getDefault().rebuildViewsContent();
	}

	@Override
	public boolean performOk() {
		boolean  b = super.performOk();

		Activator.getDefault().rebuildViewsContent();
		
		return b;
	}
	
	

}
