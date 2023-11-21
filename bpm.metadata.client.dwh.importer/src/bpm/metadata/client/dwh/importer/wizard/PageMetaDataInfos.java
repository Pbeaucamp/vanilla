package bpm.metadata.client.dwh.importer.wizard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import metadataclient.Activator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.metadata.client.dwh.importer.converters.DwhMetaDataConverter;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class PageMetaDataInfos extends WizardPage {

	

	private CheckboxTreeViewer viewer;
	private Text businessModel, businessPackage;
	private ModifyListener txtListener = new ModifyListener() {
		
		@Override
		public void modifyText(ModifyEvent e) {
			getContainer().updateButtons();
			
		}
	};
	
	
	protected PageMetaDataInfos(String pageName) {
		super(pageName);
		
	}

	@Override
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		
		createContent(main);
		setControl(main);
		
	}
	
	private void createContent(Composite parent){
		Label l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Business Model Name");
		
		businessModel = new Text(parent, SWT.BORDER);
		businessModel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		businessModel.setText("myBusinessModel");
		businessModel.addModifyListener(txtListener);
		
		l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Business Package Name");
		
		businessPackage = new Text(parent, SWT.BORDER);
		businessPackage.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		businessPackage.setText("myBusinessPackage");
		businessPackage.addModifyListener(txtListener);
		
		l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 2, 1));
		l.setText("Select Vanilla Groups");
		
		
		viewer = new CheckboxTreeViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		viewer.setContentProvider(new ITreeContentProvider() {
			
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			@Override
			public void dispose() {
				
				
			}
			
			@Override
			public Object[] getElements(Object inputElement) {
				Collection<?> c = (Collection<?>)inputElement;
				return c.toArray(new Object[c.size()]);
			}

			@Override
			public Object[] getChildren(Object parentElement) {
				
				return null;
			}

			@Override
			public Object getParent(Object element) {
				
				return null;
			}

			@Override
			public boolean hasChildren(Object element) {
				
				return false;
			}
		});
		viewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Group)element).getName();
			}
		});
		
		try{
			IVanillaAPI api = new RemoteVanillaPlatform(Activator.getDefault().getRepositoryContext().getVanillaContext());
			viewer.setInput(api.getVanillaSecurityManager().getGroups());
		}catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(getShell(), "Error when loading Groups", ex.getMessage());
		}
	
	}
	@Override
	public boolean isPageComplete() {
		
		return !(businessModel.getText().isEmpty() || businessPackage.getText().isEmpty());
	}
	
	public Object getConfigurationContext(){
		HashMap<String, Object> configurationObject = new HashMap<String, Object>();
		List<String> groups = new ArrayList<String>();
		
		for(Object o : viewer.getCheckedElements()){
			groups.add(((Group)o).getName());
		}
		
		configurationObject.put(DwhMetaDataConverter.PROP_BUSINESS_MODEL_NAME, businessModel.getText());
		configurationObject.put(DwhMetaDataConverter.PROP_BUSINESS_PACKAGE_NAME, businessPackage.getText());
		configurationObject.put(DwhMetaDataConverter.PROP_VANILLA_GROUP_NAMES, groups);
		
		return configurationObject;
	}
}
