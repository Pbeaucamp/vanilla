package bpm.fd.design.ui.component.map;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;

import bpm.fd.api.core.model.components.definition.maps.IMapDatas;
import bpm.fd.api.core.model.components.definition.maps.MapDatas;
import bpm.fd.api.core.model.datas.ColumnDescriptor;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.viewer.comparators.LabelableComparator;
import bpm.fd.design.ui.viewer.contentproviders.ListContentProvider;
import bpm.fd.design.ui.viewer.labelprovider.DatasLabelProvider;
import bpm.fd.design.ui.wizard.OdaDataSetWizard;
import bpm.fd.design.ui.wizard.OdaDataSourceWizard;
import bpm.vanilla.map.core.design.MapVanilla;
import bpm.vanilla.map.remote.core.design.fusionmap.impl.RemoteMapDefinitionService;
import bpm.vanilla.platform.core.IVanillaContext;

public class CompositeMapDatas implements ICompositeMapDatas{
	protected ComboViewer dataSourceViewer;
	protected ComboViewer dataSetViewer;
	protected ComboViewer valueCol, zoneCol, mapVanilla;
	
	private Composite client;
	
	private boolean isFilling = false;
	
	public Composite getClient(){
		return client;
	}
	
	public Composite createContent(Composite parent){
		
		client = new Composite(parent, SWT.NONE);
		client.setLayout(new GridLayout(3, false));
		
		
		Label l = new Label(client, SWT.NONE);
		l.setText(Messages.CompositeMapDatas_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		dataSourceViewer = new ComboViewer(client, SWT.READ_ONLY);
		dataSourceViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		dataSourceViewer.setContentProvider(new ListContentProvider<DataSource>());
		dataSourceViewer.setLabelProvider(new DatasLabelProvider());
		dataSourceViewer.setComparator(new LabelableComparator());
		dataSourceViewer.addSelectionChangedListener(new SelectionChangedListener());
		dataSourceViewer.setInput(Activator.getDefault().getProject().getDictionary().getDatasources());
		
		Button createDataSource = new Button(client, SWT.PUSH);
		createDataSource.setText("..."); //$NON-NLS-1$
		createDataSource.setToolTipText(Messages.CompositeMapDatas_2);
		createDataSource.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		createDataSource.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					OdaDataSourceWizard wiz = new OdaDataSourceWizard();
					
					WizardDialog dial = new WizardDialog(client.getShell(), wiz);
					if (dial.open() == Dialog.OK){
						dataSourceViewer.setInput(Activator.getDefault().getProject().getDictionary().getDatasources());
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		});
		
		Label l2 = new Label(client, SWT.NONE);
		l2.setText(Messages.CompositeMapDatas_3);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		dataSetViewer = new ComboViewer(client, SWT.READ_ONLY);
		dataSetViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		dataSetViewer.setContentProvider(new ListContentProvider<DataSet>());
		dataSetViewer.setLabelProvider(new DatasLabelProvider());
		dataSetViewer.setComparator(new LabelableComparator());
		dataSetViewer.addSelectionChangedListener(new SelectionChangedListener());

		Button createDataSet = new Button(client, SWT.PUSH);
		createDataSet.setText("..."); //$NON-NLS-1$
		createDataSet.setToolTipText(Messages.CompositeMapDatas_5);
		createDataSet.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		createDataSet.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					OdaDataSetWizard wiz = new OdaDataSetWizard(null);
					
					WizardDialog dial = new WizardDialog(client.getShell(), wiz);
					if (dial.open() == Dialog.OK){
						dataSourceViewer.setSelection(dataSourceViewer.getSelection());
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		});
		
		l = new Label(client, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.CompositeMapDatas_6);
		
		
		valueCol = new ComboViewer(client, SWT.READ_ONLY);
		valueCol.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		valueCol.setLabelProvider(new DatasLabelProvider());
		valueCol.setContentProvider(new ListContentProvider<ColumnDescriptor>());
		valueCol.setComparator(new LabelableComparator());
		valueCol.addSelectionChangedListener(new SelectionChangedListener());

		valueCol.getControl().setEnabled(false);
		
		l = new Label(client, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.CompositeMapDatas_7);
		
		
		zoneCol = new ComboViewer(client, SWT.READ_ONLY);
		zoneCol.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		zoneCol.setLabelProvider(new DatasLabelProvider());
		zoneCol.setContentProvider(new ListContentProvider<ColumnDescriptor>());
		zoneCol.setComparator(new LabelableComparator());
		zoneCol.getControl().setEnabled(false);
		zoneCol.addSelectionChangedListener(new SelectionChangedListener());

		l = new Label(client, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Map Vanilla");
		
		mapVanilla = new ComboViewer(client, SWT.READ_ONLY);
		mapVanilla.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		mapVanilla.setLabelProvider(new DatasLabelProvider());
		mapVanilla.setContentProvider(new ListContentProvider<MapVanilla>());
//		mapVanilla.setComparator(new LabelableComparator());
//		mapVanilla.getControl().setEnabled(false);
		mapVanilla.addSelectionChangedListener(new SelectionChangedListener());
		
		try {
			IVanillaContext ctx = bpm.fd.repository.ui.Activator.getDefault().getRepositoryContext().getVanillaContext();
			RemoteMapDefinitionService remoteMap = new RemoteMapDefinitionService();
			remoteMap.configure(ctx.getVanillaUrl());
		
			List<MapVanilla> maps = remoteMap.getAllMapsVanilla();
			mapVanilla.setInput(maps);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return client;
	}
	
	public IMapDatas getMapDatas(){
		MapDatas datas = null;

		datas = new MapDatas();
		DataSet ds = (DataSet) ((IStructuredSelection)dataSetViewer.getSelection()).getFirstElement();
		datas.setDataSet(ds);
		
		
		if (!valueCol.getSelection().isEmpty()){
			datas.setValueFieldIndex(((ColumnDescriptor)((IStructuredSelection)valueCol.getSelection()).getFirstElement()).getColumnIndex());
		}
		
		if (!zoneCol.getSelection().isEmpty()){
			datas.setZoneIdFieldIndex(((ColumnDescriptor)((IStructuredSelection)zoneCol.getSelection()).getFirstElement()).getColumnIndex());	
		}
		
		if(!mapVanilla.getSelection().isEmpty()) {
			datas.setMapVanilla(((MapVanilla)((IStructuredSelection)mapVanilla.getSelection()).getFirstElement()));
		}

		return datas;
	}
	
	public class SelectionChangedListener implements ISelectionChangedListener{

		public void selectionChanged(SelectionChangedEvent event) {
			if (event.getSource() == dataSourceViewer){
				IStructuredSelection ss = (IStructuredSelection)dataSourceViewer.getSelection();
				DataSource ds = (DataSource)ss.getFirstElement();

				dataSetViewer.setInput(ds.getDataSet());
				valueCol.setInput(new ArrayList<ColumnDescriptor>());
				valueCol.getControl().setEnabled(false);
				
				zoneCol.setInput(new ArrayList<ColumnDescriptor>());
				zoneCol.getControl().setEnabled(false);
				

			}
			else if (event.getSource() == dataSetViewer){
				IStructuredSelection ss = (IStructuredSelection)dataSetViewer.getSelection();
				
				if (ss.isEmpty()){
					List<ColumnDescriptor> l = new ArrayList<ColumnDescriptor>();
					valueCol.setInput(l);
					zoneCol.setInput(l);
					return;

				}
				DataSet ds = (DataSet)ss.getFirstElement();

				valueCol.setInput(ds.getDataSetDescriptor().getColumnsDescriptors());
				zoneCol.setInput(ds.getDataSetDescriptor().getColumnsDescriptors());
			
				valueCol.getControl().setEnabled(true);
				zoneCol.getControl().setEnabled(true);
				
				
			}
			
			
			if (!isFilling){
				client.notifyListeners(SWT.Modify, new Event());
			}
			
			
			
		}
		
	}

	public void fill(IMapDatas datas) {
		isFilling = true;
		
		for(DataSource ds : (List<DataSource>)dataSourceViewer.getInput()){
			if (ds.getName().equals(datas.getDataSet().getDataSourceName())){
				dataSourceViewer.setSelection(new StructuredSelection(ds));
				break;
			}
		}
		
		dataSetViewer.setSelection(new StructuredSelection(datas.getDataSet()));
		
		
		
		MapDatas dt = (MapDatas)datas;
		if (dt.getValueFieldIndex() != null && dt.getValueFieldIndex() > 0){
			try{
				valueCol.setSelection(new StructuredSelection(datas.getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(dt.getValueFieldIndex()-1)));
			}catch(Exception ex){
			}
		}
		
		if (dt.getZoneIdFieldIndex() != null && dt.getZoneIdFieldIndex() > 0){
			try{
				zoneCol.setSelection(new StructuredSelection(datas.getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(dt.getZoneIdFieldIndex()-1)));
			}catch(Exception ex){
			}
		}
		
		
		isFilling = false;
		
	}
}
