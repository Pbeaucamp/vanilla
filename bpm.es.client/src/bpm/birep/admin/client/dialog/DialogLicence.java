package bpm.birep.admin.client.dialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import adminbirep.Activator;
import adminbirep.Messages;

public class DialogLicence extends Dialog {
	

	private Text content;
	private Link contact;
	
	public DialogLicence(Shell parentShell) {
		super(parentShell);
		
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		content = new Text(container, SWT.BORDER | SWT.MULTI | SWT.WRAP |  SWT.V_SCROLL);
		content.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		content.setEditable(false);
		
		
		contact = new Link(container, SWT.NONE);
		contact.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		contact.setText("<a>Support</a>"); //$NON-NLS-1$
		contact.setText("<A HREF=\"mailto:vanilla@bpm-conseil.com\">Vanilla Registration Service</A>"); //$NON-NLS-1$
		contact.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					PlatformUI.getWorkbench().getBrowserSupport().createBrowser(IWorkbenchBrowserSupport.AS_EXTERNAL, "aCustomId", "url", "url").openURL(new URL("mailto:vanilla@bpm-conseil.com")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				} catch (PartInitException e1) {
					
					e1.printStackTrace();
				} catch (MalformedURLException e1) {
					
					e1.printStackTrace();
				}

			}
			
		});
		
		fillDatas();
		
		return container;
	}
	
	private void fillDatas(){
		File f = new File("Licence.txt"); //$NON-NLS-1$
		
		FileInputStream fis;
		try {
			fis = new FileInputStream(f);
			String txt = IOUtils.toString(fis, "UTF-8"); //$NON-NLS-1$
			fis.close();
			
			
			content.setText(txt);
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
	}

	
	@Override
	protected void initializeBounds() {
		getShell().setSize(800, 600);
		getShell().setText("Licence expire " + Activator.expirationDate); //$NON-NLS-1$
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Close", true); //$NON-NLS-1$
	}

	
	
}
