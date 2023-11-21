package bpm.birep.admin.client.views;

import java.io.File;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import adminbirep.Activator;
import adminbirep.Messages;
import adminbirep.icons.Icons;

public class ViewCustomizeHome extends ViewPart {

	public static final String ID = "bpm.birep.admin.client.views.ViewCustomizeHome"; //$NON-NLS-1$
	
	private Label previewImage;
	private String imagePath;
	private ComboViewer cbTemplate;
	private Label lblUpload;
	private Button btnBrowse;
	private Text txtUpload;
	private Composite parent;
	private Button includeFastConnection;
	
	public ViewCustomizeHome() { }

	@Override
	public void createPartControl(Composite comp) {
		
		this.parent = comp;
		
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(2, false));
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		//the left part with the template choice and a possible file upload for the logo
		Composite leftPartComposite = new Composite(mainComposite, SWT.NONE);
		leftPartComposite.setLayout(new GridLayout(2,false));
		leftPartComposite.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true));
		
		Label lblSelectTemplate = new Label(leftPartComposite, SWT.NONE);
		lblSelectTemplate.setText(Messages.Client_Views_ViewCustomizeHome_2);
		lblSelectTemplate.setLayoutData(new GridData());
		
		cbTemplate = new ComboViewer(leftPartComposite, SWT.READ_ONLY);
		cbTemplate.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false));
		cbTemplate.setContentProvider(new ArrayContentProvider());
		cbTemplate.setLabelProvider(new LabelProvider());
		
		String[] templates = {"Default", "Custom logo"}; //$NON-NLS-1$ //$NON-NLS-2$
		cbTemplate.setInput(templates);
		
		cbTemplate.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				String selection = cbTemplate.getCombo().getItem(cbTemplate.getCombo().getSelectionIndex());
				
				Display display = parent.getDisplay();
				Shell shell = new Shell(display);
				shell.setSize(520, 200);
				shell.setLayout(new RowLayout());
				
				if(selection.equalsIgnoreCase("Default")) { //$NON-NLS-1$
					previewImage.setImage(Activator.getDefault().getImageRegistry().get(Icons.TEMPLATE_PREVIEW1));
					lblUpload.setVisible(false);
					btnBrowse.setVisible(false);
					txtUpload.setVisible(false);
				}
				else {
					previewImage.setImage(Activator.getDefault().getImageRegistry().get(Icons.TEMPLATE_PREVIEW2));
					lblUpload.setVisible(true);
					btnBrowse.setVisible(true);
					txtUpload.setVisible(true);
				}
			}
		});
		
		lblUpload = new Label(leftPartComposite, SWT.NONE);
		lblUpload.setText(Messages.Client_Views_ViewCustomizeHome_8);
		lblUpload.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 2, 1));
		lblUpload.setVisible(false);
		
		btnBrowse = new Button(leftPartComposite, SWT.PUSH);
		btnBrowse.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 2, 1));
		btnBrowse.setText(Messages.Client_Views_ViewCustomizeHome_9);
		btnBrowse.setVisible(false);
		
		btnBrowse.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				FileDialog imageDial = new FileDialog(parent.getShell(), SWT.SAVE);
				imagePath = imageDial.open();
				File f = new File(imagePath);
				txtUpload.setText(f.getName());
			}
		});
		
		txtUpload = new Text(leftPartComposite, SWT.BORDER);
		txtUpload.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 2, 1 ));
		txtUpload.setVisible(false);
		txtUpload.setEnabled(false);
		
		//the right part with the image
		Display display = parent.getDisplay();
		Shell shell = new Shell(display);
		shell.setSize(520, 200);
		shell.setLayout(new RowLayout());
		
		previewImage = new Label(mainComposite, SWT.NONE);
		
		previewImage.setImage(Activator.getDefault().getImageRegistry().get(Icons.TEMPLATE_PREVIEW1));
		
		includeFastConnection = new Button(leftPartComposite, SWT.CHECK);
		includeFastConnection.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 2, 1));
		includeFastConnection.setText(Messages.ViewCustomizeHome_0);
		try{
			includeFastConnection.setSelection(Activator.getDefault().getVanillaApi().getVanillaPreferencesManager().includeFastConnection());
		}catch(Exception ex){
			
		}
		Button btnSave = new Button(leftPartComposite, SWT.PUSH);
		btnSave.setLayoutData(new GridData(GridData.FILL, SWT.BOTTOM, true, false, 1, 1));
		btnSave.setText(Messages.Client_Views_ViewCustomizeHome_11);
		
		btnSave.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				
				if(MessageDialog.openConfirm(parent.getShell(), Messages.Client_Views_ViewCustomizeHome_12, Messages.Client_Views_ViewCustomizeHome_13)) {
					try {
						createNewIndexPage();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
	}

	protected void createNewIndexPage() throws Exception {
		if(cbTemplate.getCombo().getSelectionIndex() == -1) {
			MessageDialog.openInformation(parent.getShell(), Messages.ViewCustomizeHome_1, Messages.ViewCustomizeHome_2);
			return;
		}
		
		String selection = cbTemplate.getCombo().getItem(cbTemplate.getCombo().getSelectionIndex());
		
		//if its the default home page
		if(selection.equalsIgnoreCase("Default")) { //$NON-NLS-1$
			Activator.getDefault().getVanillaApi().getVanillaPreferencesManager().setCustomLogo(null);
		}
		else {
			File logo = new File(imagePath);
			if (logo.exists() && logo.isFile()){
				FileDataSource ds = new FileDataSource(logo);
				DataHandler h = new DataHandler(ds);
				

				Activator.getDefault().getVanillaApi().getVanillaPreferencesManager().setCustomLogo(h);
			}
			
		}
		Activator.getDefault().getVanillaApi().getVanillaPreferencesManager().setIncludeFastConnection(includeFastConnection.getSelection());
		MessageDialog.openInformation(parent.getShell(), Messages.Client_Views_ViewCustomizeHome_25, Messages.Client_Views_ViewCustomizeHome_26);
	}

	@Override
	public void setFocus() { }

}
