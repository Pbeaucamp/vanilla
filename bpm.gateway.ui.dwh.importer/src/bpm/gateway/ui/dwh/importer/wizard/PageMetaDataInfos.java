package bpm.gateway.ui.dwh.importer.wizard;

import java.util.HashMap;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.gateway.core.Server;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.ui.dwh.importer.converters.DwhGatewayConnectionConverter;

public class PageMetaDataInfos extends WizardPage {

	

	private Text dataSources;
	private Text connectionName;
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
		l.setText("Database server Name");
		
		
		dataSources = new Text(parent, SWT.NONE);
		dataSources.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		dataSources.addModifyListener(txtListener);
		
		l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Connection Name");
		
		connectionName = new Text(parent, SWT.NONE);
		connectionName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		connectionName.addModifyListener(txtListener);
				
		

		
//		dataSources = new ComboViewer(parent, SWT.READ_ONLY);
//		dataSources.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		dataSources.setContentProvider(new ArrayContentProvider());
//		dataSources.setLabelProvider(new LabelProvider(){
//			@Override
//			public String getText(Object element) {
//				return ((Server)element).getName();
//			}
//		});
//		
//		dataSources.setInput(ResourceManager.getInstance().getServers(DataBaseServer.class));
//		dataSources.addSelectionChangedListener(new ISelectionChangedListener() {
//			
//			@Override
//			public void selectionChanged(SelectionChangedEvent event) {
//				getContainer().updateButtons();
//				
//			}
//		});
	}
	@Override
	public boolean isPageComplete() {
		
		return !(connectionName.getText().isEmpty() || dataSources.getText().isEmpty()) ;
	}
	
	public Object getConfigurationContext(){
		HashMap<String, Object> configurationObject = new HashMap<String, Object>();
		
		configurationObject.put(DwhGatewayConnectionConverter.PROP_CONNECTION_NAME, connectionName.getText());
		configurationObject.put(DwhGatewayConnectionConverter.PROP_DATABASE_SERVER, dataSources.getText());
		
		
		return configurationObject;
	}
}
