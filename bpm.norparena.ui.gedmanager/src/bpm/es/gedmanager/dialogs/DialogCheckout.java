package bpm.es.gedmanager.dialogs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.es.gedmanager.api.GedModel;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.components.ged.GedLoadRuntimeConfig;

public class DialogCheckout extends Dialog {
	
	private GedModel gedModel;
	private GedDocument document;
	private int userId;
	private int groupId;
	
	private Text txtFilePath;
	
	public DialogCheckout(Shell parentShell, GedModel gedModel, GedDocument document, int userId, int groupId) {
		super(parentShell);
		this.gedModel = gedModel;
		this.document = document;
		this.userId = userId;
		this.groupId = groupId;
	}


	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(3, false));
		main.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		
		Label lblFilePath = new Label(main, SWT.NONE);
		lblFilePath.setText("File Path");
		
		txtFilePath = new Text(main, SWT.BORDER);
		txtFilePath.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false));

		Button browse = new Button(main, SWT.PUSH);
		browse.setLayoutData(new GridData(SWT.END, SWT.BEGINNING, false, false));
		browse.setText("Browse");
		browse.addSelectionListener(browseListener);
		
		return main;
	}
	
	@Override
	protected void okPressed() {
		String filePath = txtFilePath.getText();
		if(filePath != null && !filePath.isEmpty()){
			DocumentVersion docVersion = document.getLastVersion();
			
			GedLoadRuntimeConfig config = new GedLoadRuntimeConfig(docVersion.getParent(), userId);
			
			InputStream is;
			try {
				is = gedModel.loadGedDocument(config);
			
				// write the inputStream to a FileOutputStream
				OutputStream out = new FileOutputStream(new File(filePath));
			 
				int read = 0;
				byte[] bytes = new byte[1024];
			 
				while ((read = is.read(bytes)) != -1) {
					out.write(bytes, 0, read);
				}
	
				is.close();
				out.flush();
				out.close();
				
				gedModel.checkout(document.getId(), userId);

				MessageDialog.openInformation(getShell(), "Informations", "Your document has been successfully checkout.");
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(getShell(), "Error", "Unable to checkout this document - " + e.getMessage());
			}
		}
		
		super.okPressed();
	}
	
	private SelectionListener browseListener = new SelectionListener() {
		
		@Override
		public void widgetSelected(SelectionEvent e) {
			FileDialog dial = new FileDialog(getShell());
			String filePath = dial.open();
			if(filePath != null){
				txtFilePath.setText(filePath);
			}
		}
		
		@Override
		public void widgetDefaultSelected(SelectionEvent e) { }
	};
}

