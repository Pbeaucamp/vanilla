package bpm.gateway.ui.dialogs.database.wizard.transfert;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import bpm.gateway.core.transformations.inputs.FMDTHelper;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.icons.IconsNames;
import bpm.gateway.ui.tools.dialogs.DialogRepositoryObject;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class FmdtTransfertPage extends WizardPage {

	private Text txtDirItemName, txtFilePath;
	private Combo cbBusinessModels, cbBusinessPackages, cbConnectionName, csvEncoding;
	private CheckboxTableViewer tableBusinessTables;
	private Button btnBrowseRepository, btnDelete;

	private IRepositoryContext ctx;
	private FMDTHelper fmdtHelper;
	
	private RepositoryItem item;
	private IBusinessPackage pack;
	private Collection<IBusinessModel> fmdtModels;
	private Collection<IBusinessPackage> fmdtPackages;
	
	private Collection<IBusinessTable> businessTables;
	
	public FmdtTransfertPage(String pageName) {
		super(pageName);
		this.ctx = Activator.getDefault().getRepositoryContext();
		this.fmdtHelper = new FMDTHelper(ctx);
	}

	@Override
	public void createControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(3, false));
		container.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		Label lblDirItemName = new Label(container, SWT.NONE);
		lblDirItemName.setText(Messages.FmdtInputSection_0);
		lblDirItemName.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		txtDirItemName = new Text(container, SWT.BORDER);
		txtDirItemName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txtDirItemName.addSelectionListener(dialRepositoryAdapter);
		
		btnBrowseRepository = new Button(container, SWT.PUSH);
		btnBrowseRepository.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		btnBrowseRepository.setText(Messages.FmdtInputSection_2);
		btnBrowseRepository.addSelectionListener(dialRepositoryAdapter);
		
		Label lblBusinessModels = new Label(container, SWT.NONE);
		lblBusinessModels.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblBusinessModels.setText(Messages.FmdtInputSection_3);
		
		cbBusinessModels = new Combo(container, SWT.READ_ONLY);
		cbBusinessModels.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		cbBusinessModels.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				fillBusinessPackages();
			}
			
		});
		
		Label lblBusinessPackages = new Label(container, SWT.NONE);
		lblBusinessPackages.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblBusinessPackages.setText(Messages.FmdtInputSection_4);
		

		cbBusinessPackages = new Combo(container, SWT.READ_ONLY);
		cbBusinessPackages.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		cbBusinessPackages.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				for(IBusinessPackage p : fmdtPackages){
					if (p.getName().equals(cbBusinessPackages.getText())){
						pack = p;
						try{
							fillConnections();
							fillBusinessTables();
						}catch(Exception ex){
							ex.printStackTrace();
							MessageDialog.openError(getShell(), Messages.FmdtInputSection_1, Messages.FmdtInputSection_8 + ex.getMessage());
						}
						break;
					}
				}
			}
		});
	
		Label lblConnectionName = new Label(container, SWT.NONE);
		lblConnectionName.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblConnectionName.setText(Messages.FmdtInputSection_5);
		
		cbConnectionName = new Combo(container, SWT.READ_ONLY);
		cbConnectionName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		Composite btnContainer = new Composite(container, SWT.NONE);
		btnContainer.setLayout(new GridLayout(2, false));
		btnContainer.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 3, 1));
		
		Button checkAll = new Button(btnContainer, SWT.PUSH);
		checkAll.setToolTipText(Messages.FmdtTransfertPage_0);
		checkAll.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.CHECK));
		checkAll.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				checkAllTables();
				
				refreshInput();
			}
		});
		
		Button unckeckAll = new Button(btnContainer, SWT.PUSH);
		unckeckAll.setToolTipText(Messages.FmdtTransfertPage_1);
		unckeckAll.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.NO_CHECK));
		unckeckAll.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				uncheckAllTables();
				
				refreshInput();
			}
		});
		
		tableBusinessTables = new CheckboxTableViewer(new Table(container, SWT.BORDER 
				| SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.CHECK));
		tableBusinessTables.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));
		tableBusinessTables.setContentProvider(new IStructuredContentProvider() {
			
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }
			
			@Override
			public void dispose() { }
			
			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				Collection<IBusinessTable> bt = (Collection<IBusinessTable>)inputElement;
				return bt.toArray(new IBusinessTable[bt.size()]);
			}
		});
		tableBusinessTables.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				return ((IBusinessTable)element).getName();
			}
		});
		
		Label lblFilePath = new Label(container, SWT.NONE);
		lblFilePath.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblFilePath.setText(Messages.FileGeneralSection_0);
		
		txtFilePath = new Text(container, SWT.BORDER);
		txtFilePath.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Button browse = new Button(container, SWT.PUSH);
		browse.setText(Messages.FileGeneralSection_3);
		browse.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		browse.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog fd = new DirectoryDialog(getShell());

				String path = fd.open();
				if (path != null){
					txtFilePath.setText(path);
				}
			}
		});
		
		Label lblEncoding = new Label(container, SWT.NONE);
		lblEncoding.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblEncoding.setText(Messages.FmdtTransfertPage_2);
		
		csvEncoding = new Combo(container, SWT.READ_ONLY);
		csvEncoding.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		for(String s : Charset.availableCharsets().keySet()){
			csvEncoding.add(s);
		}
		csvEncoding.setText("UTF-8"); //$NON-NLS-1$
		
		btnDelete = new Button(container, SWT.CHECK);
		btnDelete.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		btnDelete.setText(Messages.FmdtTransfertPage_3);
		
		setControl(container);
	}
	
	private void refreshInput() {
		if(businessTables == null){
			tableBusinessTables.setInput(new ArrayList<IBusinessTable>());
		}
		else {
			tableBusinessTables.setInput(businessTables);
		}
	}
	
	private void checkAllTables() {
		if(businessTables != null){
			for(IBusinessTable bt : businessTables){
				tableBusinessTables.setChecked(bt, true);
			}
		}
	}
	
	private void uncheckAllTables() {
		if(businessTables != null){
			for(IBusinessTable bt : businessTables){
				tableBusinessTables.setChecked(bt, false);
			}
		}
	}
	
	private void fillBusinessModels(){
		try {
			if(item != null){
				fmdtModels = fmdtHelper.getFmdtModel(item.getId());
				List<String> modelsNames = new ArrayList<String>();
				for(IBusinessModel m : fmdtModels){
					modelsNames.add(m.getName());
				}
				
				cbBusinessModels.setItems(modelsNames.toArray(new String[modelsNames.size()]));
				cbBusinessPackages.setItems(new String[]{});
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), Messages.FmdtInputSection_16, e.getMessage());
		}
	}
	
	private void fillBusinessPackages() {
		for(IBusinessModel m : fmdtModels){
			if (m.getName().equals(cbBusinessModels.getText())){
				fmdtPackages = m.getBusinessPackages("none"); //$NON-NLS-1$
				break;
			}
		}
		
		List<String> names = new ArrayList<String>();
		
		for(IBusinessPackage p : fmdtPackages){
			names.add(p.getName());
		}
		
		cbBusinessPackages.setItems(names.toArray(new String[fmdtPackages.size()]));
	}
	
	private void fillConnections() throws Exception{
		Group g  = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroups().iterator().next();
		
		List<String> l = pack.getConnectionsNames(g.getName());
		cbConnectionName.setItems(l.toArray(new String[l.size()]));
	}
	
	private void fillBusinessTables() {
		businessTables = pack.getBusinessTables("none"); //$NON-NLS-1$
		
		refreshInput();
	}
	
	private SelectionAdapter dialRepositoryAdapter = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			DialogRepositoryObject dial = 
				new DialogRepositoryObject(getShell(), IRepositoryApi.FMDT_TYPE);
			if (dial.open() == Dialog.OK){
				item = dial.getRepositoryItem();
				txtDirItemName.setText(item.getItemName());
				fillBusinessModels();
			}
		};
	};
	
	public Properties getInputProperties() throws Exception {
		Properties p = new Properties();
		if(item != null){
			p.setProperty(FmdtTransfertWizard.PROP_ITEM_ID, item.getId() + ""); //$NON-NLS-1$
		}
		else {
			throw new Exception(Messages.FmdtTransfertPage_6);
		}
		if(!cbBusinessModels.getText().isEmpty()){
			p.setProperty(FmdtTransfertWizard.PROP_BU_MODEL, cbBusinessModels.getText());
		}
		else {
			throw new Exception(Messages.FmdtTransfertPage_7);
		}
		
		if(!cbBusinessPackages.getText().isEmpty()){
			p.setProperty(FmdtTransfertWizard.PROP_BU_PACKAGE, cbBusinessPackages.getText());
		}
		else {
			throw new Exception(Messages.FmdtTransfertPage_8);
		}
		
		p.setProperty(FmdtTransfertWizard.PROP_CONNECTION_NAME, cbConnectionName.getText());
		
		return p;
	}
	
	public Properties getOuputProperties() throws Exception {
		Properties p = new Properties(); 
		if(!txtFilePath.getText().isEmpty()){
			p.setProperty(FmdtTransfertWizard.PROP_OUTPUT_FILE, txtFilePath.getText());
		}
		else {
			throw new Exception(Messages.FmdtTransfertPage_9);
		}
		
		if(!csvEncoding.getText().isEmpty()){
			p.setProperty(FmdtTransfertWizard.PROP_OUTPUT_ENCODING, csvEncoding.getText());
		}
		else {
			throw new Exception(Messages.FmdtTransfertPage_10);
		}
		
		p.setProperty(FmdtTransfertWizard.PROP_DELETE_FILE, String.valueOf(btnDelete.getSelection()));
		
		return p;
	}
	
	public List<IBusinessTable> getSelectedTables() throws Exception {
		List<IBusinessTable> selectedTables = new ArrayList<IBusinessTable>();
		if(businessTables != null){
			for(IBusinessTable bt : businessTables){
				if(tableBusinessTables.getChecked(bt)){
					selectedTables.add(bt);
				}
			}
		}
		
		if(selectedTables.isEmpty()){
			throw new Exception(Messages.FmdtTransfertPage_11);
		}
		return selectedTables;
	}
	
	public RepositoryItem getSelectedItem() {
		return item;
	}
}
