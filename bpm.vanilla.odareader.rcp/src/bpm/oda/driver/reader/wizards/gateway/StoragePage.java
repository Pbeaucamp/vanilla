package bpm.oda.driver.reader.wizards.gateway;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import bpm.oda.driver.reader.Activator;
import bpm.oda.driver.reader.preferences.PreferencesConstants;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.repository.ui.composites.CompositeRepositorySelecter;
import bpm.vanilla.repository.ui.dialogs.DialogDirectoryPicker;

public class StoragePage extends WizardPage{
	protected static final String[] DESTINATIONS = new String[]{"FileSystem", "Repository"};
	private Combo destinationType;
	private Composite compositeConnection;
	private Composite holder;
	
	private Text transfoFileName;
	private Text folder;
	private Button bRep;
	private Combo groups;
	
	private Object folderObject; //string if fileSystem IDirectory if Repository
	
	private GatewayBean bean;
	
	protected StoragePage(String pageName, GatewayBean bean) {
		super(pageName);
		this.bean = bean;
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(3, false));
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText("Store Transformation On ");
		
		destinationType = new Combo(main, SWT.READ_ONLY);
		destinationType.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		destinationType.setItems(DESTINATIONS);
		destinationType.select(0);
		destinationType.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				String oldValue = bean.getStorageDestinationType();
				
				bean.setStorageDestinationType(destinationType.getText());
				boolean hasChanged = false;
				
				if (destinationType.getSelectionIndex() == 0){
					if (oldValue.equals(DESTINATIONS[1])){
						hasChanged = true;
					}
				}
				else{
					if (oldValue.equals(DESTINATIONS[0])){
						hasChanged = true;
					}
				}
				
				if (hasChanged){
					compositeConnection.dispose();
					if (destinationType.getSelectionIndex() == 0){
						createForFileSystem(holder);
					}
					else{
						createForRepository(holder);
					}
					holder.getParent().layout();
				}
				
				getContainer().updateButtons();
				
			}
			
		});
		
		
		holder = new Composite(main, SWT.NONE);
		holder.setLayout(new GridLayout(3, false));
		holder.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		
		
		createForFileSystem(main);
		
		
		
		setControl(main);
	}
	
	
	
	private void createForRepository(Composite main){
		compositeConnection = new CompositeRepositorySelecter(main, SWT.NONE);
		compositeConnection.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		compositeConnection.addListener(SWT.Selection, new Listener() {
			
			public void handleEvent(Event event) {
				
				bRep.setEnabled(((CompositeRepositorySelecter)compositeConnection).getSelectedRepositoryDefinition() != null);
				
			}
		});
		
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		((CompositeRepositorySelecter)compositeConnection).fill(store.getString(PreferencesConstants.PREF_VANILLA_LOGIN), 
				store.getString(PreferencesConstants.PREF_VANILLA_PASSWORD), 
				store.getString(PreferencesConstants.PREF_VANILLA_URL), -1);
		
		
		Label l = new Label(compositeConnection, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText("Transformation File Name");
		
		transfoFileName = new Text(compositeConnection, SWT.BORDER);
		transfoFileName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		transfoFileName.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				bean.setStorageDestinationName(transfoFileName.getText());
				getContainer().updateButtons();
				
			}
		});
		
		Composite c = new Composite(compositeConnection, SWT.NONE);
		c.setLayout(new GridLayout(3, false));
		c.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		l = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText("Folder");
		
		folder = new Text(c, SWT.BORDER);
		folder.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		folder.setEnabled(false);
		
		bRep = new Button(c, SWT.PUSH);
		bRep.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		bRep.setEnabled(false);
		bRep.setText("...");
		bRep.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				IRepositoryApi sock = ((CompositeRepositorySelecter)compositeConnection).getRepositorySocket();
				DialogDirectoryPicker dial = new DialogDirectoryPicker(getShell(), sock);
				if (dial.open() == DialogDirectoryPicker.OK){
					folderObject = dial.getDirectory();
					bean.setStorageDestinattionFolder(folderObject);
					folder.setText(dial.getDirectory().getName());
					
					try{
						if (groups.getItemCount() == 0){
							
							IVanillaAPI api = new RemoteVanillaPlatform(((CompositeRepositorySelecter)compositeConnection).getVanillaContext());
							
							List<Group> grps = api.getVanillaSecurityManager().getGroups();
							String[] names = new String[grps.size()];
							int i =0;
							for(Group g : grps) {
								names[i] = g.getName();
								i++;
							}
							
							groups.setItems(names);
							groups.select(0);
						}
					}catch(Exception ex){
						ex.printStackTrace();
						MessageDialog.openError(getShell(), "Error", "Unable to load vanilla groups : " + ex.getMessage());
					}
					
					
				}
				
				getContainer().updateButtons();
			}
		});
		
		l = new Label(compositeConnection, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText("Publish for Group");
		
		groups = new Combo(compositeConnection, SWT.READ_ONLY);
		groups.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		groups.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				bean.setGroupName(groups.getText());
			}
		});
		
	}

	private void createForFileSystem(Composite parent){
		compositeConnection = new Composite(parent, SWT.NONE);
		compositeConnection.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		compositeConnection.setLayout(new GridLayout(3, false));
		
		Label l = new Label(compositeConnection, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText("Transformation File Name");
		
		transfoFileName = new Text(compositeConnection, SWT.BORDER);
		transfoFileName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		transfoFileName.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				bean.setStorageDestinationName(transfoFileName.getText());
				getContainer().updateButtons();
				
			}
		});
		
		l = new Label(compositeConnection, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText("Folder");
		
		folder = new Text(compositeConnection, SWT.BORDER);
		folder.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		folder.setEnabled(false);
		
		Button b = new Button(compositeConnection, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		b.setText("...");
		b.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (destinationType.getSelectionIndex() == 0){
					DirectoryDialog d = new DirectoryDialog(getShell(), SWT.SAVE);
					String s = d.open();
					
					if (s != null){
						folder.setText(s);
						folderObject = s;
						bean.setStorageDestinattionFolder(folderObject);
					}
				}
				else{
					
				}
				getContainer().updateButtons();
			}
		});
	}
	
	public IRepositoryApi getRepositorySocket(){
		if (destinationType.getSelectionIndex() == 1){
			return ((CompositeRepositorySelecter)compositeConnection).getRepositorySocket();
		}
		return null;
	}
}
