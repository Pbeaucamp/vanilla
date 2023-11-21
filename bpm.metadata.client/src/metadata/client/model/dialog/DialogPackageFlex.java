package metadata.client.model.dialog;

import java.io.File;
import java.net.URL;
import java.util.List;

import metadata.client.helper.GroupHelper;
import metadata.client.i18n.Messages;
import metadataclient.Activator;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import bpm.fmdtDrillerFlex.generator.FmdtDrillerFlexGenerator;
import bpm.metadata.layer.business.IBusinessPackage;

public class DialogPackageFlex extends Dialog {
	
	
	private Combo groups;
	private Browser browser;
	private IBusinessPackage pack ;
	
	public DialogPackageFlex(Shell parentShell, IBusinessPackage pack) {
		super(parentShell);
		this.setShellStyle(getShellStyle() | SWT.RESIZE);
		this.pack = pack;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(main, SWT.NONE);
		l.setText(Messages.DialogPackageFlex_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		groups = new Combo(main, SWT.READ_ONLY);
		groups.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		List<String> lst = GroupHelper.getGroups(0, 100);
		groups.setItems(lst.toArray(new String[lst.size()]));
		
		groups.addSelectionListener(new SelectionAdapter() {

			
			public void widgetSelected(SelectionEvent e) {
				FmdtDrillerFlexGenerator generator = new FmdtDrillerFlexGenerator(groups.getText(), pack);
				try{
					if (browser != null){
						browser.dispose();
						browser = new Browser(main, SWT.BORDER);
						browser.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
						main.layout();
					}
					
					Object o = pack.getBusinessTables(groups.getText());
					generator.generate(Platform.getInstallLocation().getURL().getPath() + Activator.FLEX_GENERATION_FILE_PATH + "/graph.xml"); //$NON-NLS-1$
					URL u = new File(Platform.getInstallLocation().getURL().getPath() + Activator.FLEX_GENERATION_FILE_PATH + "/../DrillerFlex.html").toURL(); //$NON-NLS-1$
					browser.setUrl(u.toString());
					browser.refresh();
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
			}
			
		});
		
		
				
		browser = new Browser(main, SWT.BORDER);
		browser.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		if (!lst.isEmpty()){
			groups.select(0);
			groups.notifyListeners(SWT.Selection, new Event());
		}
		return main;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button b = createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, true);
		b.addSelectionListener(new SelectionAdapter(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				okPressed();
			}
			
		});
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#initializeBounds()
	 */
	@Override
	protected void initializeBounds() {
		getShell().setText("Package Relation Viewer"); //$NON-NLS-1$
		getShell().setSize(800, 600);
	}
	
	
}
