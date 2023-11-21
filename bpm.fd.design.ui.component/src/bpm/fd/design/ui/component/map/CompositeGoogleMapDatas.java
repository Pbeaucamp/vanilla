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

import bpm.fd.api.core.model.components.definition.maps.GoogleMapDatas;
import bpm.fd.api.core.model.components.definition.maps.IMapDatas;
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

public class CompositeGoogleMapDatas implements ICompositeMapDatas{
	protected ComboViewer dataSourceViewer;
	protected ComboViewer dataSetViewer;
	protected ComboViewer valueCol, labelCol, longitudeCol, latitudeCol;
	
	private Composite client;
	
	
	private SelectionChangedListener listener = new SelectionChangedListener();
	private boolean isFilling = false;
	
	public Composite getClient(){
		return client;
	}
	
	public Composite createContent(Composite parent){
		
		client = new Composite(parent, SWT.NONE);
		client.setLayout(new GridLayout(3, false));
		
		
		Label l = new Label(client, SWT.NONE);
		l.setText(Messages.CompositeGoogleMapDatas_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		dataSourceViewer = new ComboViewer(client, SWT.READ_ONLY);
		dataSourceViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		dataSourceViewer.setContentProvider(new ListContentProvider<DataSource>());
		dataSourceViewer.setLabelProvider(new DatasLabelProvider());
		dataSourceViewer.setComparator(new LabelableComparator());
		dataSourceViewer.addSelectionChangedListener(listener);
		dataSourceViewer.setInput(Activator.getDefault().getProject().getDictionary().getDatasources());
		
		Button createDataSource = new Button(client, SWT.PUSH);
		createDataSource.setText("..."); //$NON-NLS-1$
		createDataSource.setToolTipText(Messages.CompositeGoogleMapDatas_2);
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
		l2.setText(Messages.CompositeGoogleMapDatas_3);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		dataSetViewer = new ComboViewer(client, SWT.READ_ONLY);
		dataSetViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		dataSetViewer.setContentProvider(new ListContentProvider<DataSet>());
		dataSetViewer.setLabelProvider(new DatasLabelProvider());
		dataSetViewer.setComparator(new LabelableComparator());
		dataSetViewer.addSelectionChangedListener(listener);

		Button createDataSet = new Button(client, SWT.PUSH);
		createDataSet.setText("..."); //$NON-NLS-1$
		createDataSet.setToolTipText(Messages.CompositeGoogleMapDatas_5);
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
		l.setText(Messages.CompositeGoogleMapDatas_6);
		
		
		valueCol = new ComboViewer(client, SWT.READ_ONLY);
		valueCol.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		valueCol.setLabelProvider(new DatasLabelProvider());
		valueCol.setContentProvider(new ListContentProvider<ColumnDescriptor>());
		valueCol.setComparator(new LabelableComparator());
		valueCol.addSelectionChangedListener(listener);

		valueCol.getControl().setEnabled(false);
		
		l = new Label(client, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.CompositeGoogleMapDatas_7);
		
		
		labelCol = new ComboViewer(client, SWT.READ_ONLY);
		labelCol.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		labelCol.setLabelProvider(new DatasLabelProvider());
		labelCol.setContentProvider(new ListContentProvider<ColumnDescriptor>());
		labelCol.setComparator(new LabelableComparator());
		labelCol.getControl().setEnabled(false);
		labelCol.addSelectionChangedListener(listener);

		
		l = new Label(client, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.CompositeGoogleMapDatas_8);
		
		
		latitudeCol = new ComboViewer(client, SWT.READ_ONLY);
		latitudeCol.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		latitudeCol.setLabelProvider(new DatasLabelProvider());
		latitudeCol.setContentProvider(new ListContentProvider<ColumnDescriptor>());
		latitudeCol.setComparator(new LabelableComparator());
		latitudeCol.getControl().setEnabled(false);
		latitudeCol.addSelectionChangedListener(listener);

		
		l = new Label(client, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.CompositeGoogleMapDatas_9);
		
		
		longitudeCol = new ComboViewer(client, SWT.READ_ONLY);
		longitudeCol.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		longitudeCol.setLabelProvider(new DatasLabelProvider());
		longitudeCol.setContentProvider(new ListContentProvider<ColumnDescriptor>());
		longitudeCol.setComparator(new LabelableComparator());
		longitudeCol.getControl().setEnabled(false);
		longitudeCol.addSelectionChangedListener(listener);

		
		return client;
	}
	
	public IMapDatas getMapDatas(){
		GoogleMapDatas datas = null;

		datas = new GoogleMapDatas();
		DataSet ds = (DataSet) ((IStructuredSelection)dataSetViewer.getSelection()).getFirstElement();
		datas.setDataSet(ds);
		
		
		if (!valueCol.getSelection().isEmpty()){
			datas.setValueFieldIndex(((ColumnDescriptor)((IStructuredSelection)valueCol.getSelection()).getFirstElement()).getColumnIndex());
		}
		
		if (!labelCol.getSelection().isEmpty()){
			datas.setLabelFieldIndex(((ColumnDescriptor)((IStructuredSelection)labelCol.getSelection()).getFirstElement()).getColumnIndex());	
		}
		
		if (!latitudeCol.getSelection().isEmpty()){
			datas.setLatitudeFieldIndex(((ColumnDescriptor)((IStructuredSelection)latitudeCol.getSelection()).getFirstElement()).getColumnIndex());	
		}
		
		
		if (!longitudeCol.getSelection().isEmpty()){
			datas.setLongitudeFieldIndex(((ColumnDescriptor)((IStructuredSelection)longitudeCol.getSelection()).getFirstElement()).getColumnIndex());	
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
				
				labelCol.setInput(new ArrayList<ColumnDescriptor>());
				labelCol.getControl().setEnabled(false);
				
				longitudeCol.setInput(new ArrayList<ColumnDescriptor>());
				longitudeCol.getControl().setEnabled(false);
				
				latitudeCol.setInput(new ArrayList<ColumnDescriptor>());
				latitudeCol.getControl().setEnabled(false);
			}
			else if (event.getSource() == dataSetViewer){
				IStructuredSelection ss = (IStructuredSelection)dataSetViewer.getSelection();
				
				if (ss.isEmpty()){
					List<ColumnDescriptor> l = new ArrayList<ColumnDescriptor>();
					valueCol.setInput(l);
					labelCol.setInput(l);
					longitudeCol.setInput(l);
					latitudeCol.setInput(l);
					return;

				}
				DataSet ds = (DataSet)ss.getFirstElement();

				valueCol.setInput(ds.getDataSetDescriptor().getColumnsDescriptors());
				labelCol.setInput(ds.getDataSetDescriptor().getColumnsDescriptors());
				longitudeCol.setInput(ds.getDataSetDescriptor().getColumnsDescriptors());
				latitudeCol.setInput(ds.getDataSetDescriptor().getColumnsDescriptors());
			
				valueCol.getControl().setEnabled(true);
				labelCol.getControl().setEnabled(true);
				longitudeCol.getControl().setEnabled(true);
				latitudeCol.getControl().setEnabled(true);
				
				
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
		
		
		
		GoogleMapDatas dt = (GoogleMapDatas)datas;
		if (dt.getLabelFieldIndex() != null && dt.getLabelFieldIndex() > 0){
			try{
				labelCol.setSelection(new StructuredSelection(datas.getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(dt.getLabelFieldIndex() - 1)));
			}catch(Exception ex){
			}
		}
		
		if (dt.getLatitudeFieldIndex() != null && dt.getLatitudeFieldIndex() > 0){
			try{
				latitudeCol.setSelection(new StructuredSelection(datas.getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(dt.getLatitudeFieldIndex() - 1)));
			}catch(Exception ex){
			}
		}
		if (dt.getLongitudeFieldIndex() != null && dt.getLongitudeFieldIndex() > 0){
			try{
				longitudeCol.setSelection(new StructuredSelection(datas.getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(dt.getLongitudeFieldIndex() - 1)));
			}catch(Exception ex){
			}
		}
		if (dt.getValueFieldIndex() != null && dt.getValueFieldIndex() > 0){
			try{
				valueCol.setSelection(new StructuredSelection(datas.getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(dt.getValueFieldIndex() - 1)));
			}catch(Exception ex){
			}
		}
		
		
		isFilling = false;
	}
}
