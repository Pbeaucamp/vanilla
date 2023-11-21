package metadata.client.wizards.datasources;

import java.util.List;

import metadata.client.i18n.Messages;
import metadata.client.model.dialog.DialogSelectRepositoryItem;
import metadataclient.Activator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.metadata.layer.physical.olap.UnitedOlapConnection;
import bpm.metadata.layer.physical.olap.UnitedOlapFactoryConnection;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class PageType extends WizardPage {

	protected Composite container;
	protected Text file;
	protected String fileName;
	
	protected Button xmlSource;
	protected Button browserRepository;
	protected Label labelSourceType;
	protected RepositoryItem item;
	
	public PageType(String pageName) {
		super(pageName);
	}

	protected Button sql, olap, csv, xml, xls;
	protected int type = 0;
	
	
	public void createControl(Composite parent) {
		//create main composite & set layout
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// create contents
		createPageContent(mainComposite);

		// page setting
		setControl( mainComposite );
		setPageComplete(true);

	}
	
	private void createPageContent(Composite composite){
		Group g = new Group(composite, SWT.NONE);
		g.setLayout(new GridLayout());
		g.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		sql = new Button(g, SWT.RADIO);
		sql.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		sql.setText(Messages.PageType_0); //$NON-NLS-1$
		sql.setSelection(true);
		sql.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (sql.getSelection()){
					type = 0;
					file.setText(""); //$NON-NLS-1$
					
					browserRepository.setEnabled(xmlSource.getSelection());
					file.setEnabled(Activator.getDefault().getRepositoryConnection() != null);
					xmlSource.setEnabled(Activator.getDefault().getRepositoryConnection() != null);
					getContainer().updateButtons();
				}
				
			}
			
		});
		
		olap = new Button(g, SWT.RADIO);
		olap.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		olap.setText(Messages.PageType_1); //$NON-NLS-1$
		olap.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (olap.getSelection()){
					file.setText(""); //$NON-NLS-1$
					type = 1;
					browserRepository.setEnabled(true);
					file.setEnabled(true);
					xmlSource.setSelection(true);
					xmlSource.setEnabled(false);
					labelSourceType.setText(Messages.PageType_7);
					if(Activator.getDefault().getRepositoryConnection() == null) {
						file.setText(Messages.PageType_8);
					}
					container.layout();
					getContainer().updateButtons();
				}
				
			}
			
		});
		
		
		csv = new Button(g, SWT.RADIO);
		csv.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		csv.setText(Messages.PageType_2); //$NON-NLS-1$
		csv.setEnabled(false);
		
		xml = new Button(g, SWT.RADIO);
		xml.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		xml.setText(Messages.PageType_3); //$NON-NLS-1$
		xml.setEnabled(false);
		
		xls = new Button(g, SWT.RADIO);
		xls.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		xls.setText(Messages.PageType_4); //$NON-NLS-1$
		xls.setEnabled(false);
		
		container = new Composite(composite, SWT.NONE);
		container.setLayout(new GridLayout(3, false));
		container.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		xmlSource = new Button(container, SWT.CHECK);
		xmlSource.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		xmlSource.setText(Messages.PageType_11);
		xmlSource.setEnabled(Activator.getDefault().getRepositoryConnection() != null);
		xmlSource.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (xmlSource.getSelection()){
					item = null;
					if (olap.getSelection()){
						labelSourceType.setText(Messages.PageType_12);
					}
					else if (sql.getSelection()){
						labelSourceType.setText(Messages.PageType_13);
					}
					browserRepository.setEnabled(Activator.getDefault().getRepositoryConnection() != null);
				}
				else{
					browserRepository.setEnabled(false);
					labelSourceType.setText(Messages.PageType_10); //$NON-NLS-1$
				}
				
				container.layout();
				getContainer().updateButtons();
			}
			
		});
		
		labelSourceType = new Label(container, SWT.NONE);
		labelSourceType.setLayoutData(new GridData());
		labelSourceType.setText(Messages.PageType_10); //$NON-NLS-1$
		
		file = new Text(container, SWT.BORDER);
		file.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		file.setEditable(false);
		
		browserRepository = new Button(container, SWT.PUSH);
		browserRepository.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		browserRepository.setText("..."); //$NON-NLS-1$
		browserRepository.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (olap.getSelection()){
					if (!xmlSource.getSelection()){
						FileDialog fd = new FileDialog(getShell());
						fd.setFilterExtensions(new String[]{"*.fasd"}); //$NON-NLS-1$
						file.setText(fd.open());
						fileName = file.getText();
					}
					else{
						DialogSelectRepositoryItem dial = new DialogSelectRepositoryItem(getShell(), IRepositoryApi.FASD_TYPE, -1);
						
						if (dial.open() == DialogSelectRepositoryItem.OK){
							item = dial.getItem();
							file.setText(item.getItemName());
						}
					}
				}
				else if (sql.getSelection()){
					if (xmlSource.getSelection()){
						DialogDataSourcePicker d = new DialogDataSourcePicker(getShell(), Activator.getDefault().getRepositoryConnection());
						
						if (d.open() == DialogDataSourcePicker.OK){
							SQLConnection con  = d.getSQLConnection();
							
							((PageConnection)getNextPage()).setConnection(con);
							file.setText(con.getName());
						}
						
						
					}
				}
				
				getContainer().updateButtons();
				
				
				
				
			}
			
		});
		browserRepository.setEnabled(false);
		
	}
	
	public int getType(){
		return type;
	}

	@Override
	public IWizardPage getNextPage() {
		DataSourceWizard wizard = (DataSourceWizard)getWizard();
		if (type == 0){
			
			return wizard.connectionPage;
		}
		else{
			List<UnitedOlapConnection> l;
			if (!xmlSource.getSelection()){
				try {
					
				} catch (Exception e) {
					MessageDialog.openError(getShell(), Messages.PageType_9, e.getMessage()); //$NON-NLS-1$
					e.printStackTrace();
				}
			}
			else{
	
				try{
					
					if(Activator.getDefault().getRepositoryContext() == null) {
						MessageDialog.openWarning(getShell(), Messages.PageType_14, Messages.PageType_15);
					}
					else {
						
					
						IObjectIdentifier id = new ObjectIdentifier(Activator.getDefault().getRepositoryContext().getRepository().getId(), item.getId());
						
						l = UnitedOlapFactoryConnection.createUnitedOlapConnection(id,Activator.getDefault().getRepositoryContext());
						
						wizard.olapPage.createModel(l);
					}
				} catch (Exception e) {
					MessageDialog.openError(getShell(), Messages.PageType_9, e.getMessage()); //$NON-NLS-1$
					e.printStackTrace();
				}
			}
			
	
				
				
			
			
			return wizard.olapPage;
		}
	}

	@Override
	public boolean isPageComplete() {
		if (olap.getSelection()){
			return !file.getText().equals(""); //$NON-NLS-1$
		}
		
		return true;
	}


}
