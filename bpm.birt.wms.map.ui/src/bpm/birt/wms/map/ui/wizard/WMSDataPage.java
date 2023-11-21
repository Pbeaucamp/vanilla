package bpm.birt.wms.map.ui.wizard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.birt.report.model.api.ColumnHintHandle;
import org.eclipse.birt.report.model.api.DataSetHandle;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import bpm.birt.wms.map.core.reportitem.ColorRange;
import bpm.birt.wms.map.core.reportitem.WMSMapItem;
import bpm.birt.wms.map.ui.Activator;
import bpm.birt.wms.map.ui.Icons;
import bpm.birt.wms.map.ui.dialog.BindingDatasetDialog;
import bpm.birt.wms.map.ui.dialog.ColorRangeDialog;
import bpm.birt.wms.map.ui.dialog.ColorTableContentProvider;
import bpm.birt.wms.map.ui.dialog.ColorTableLabelProvider;

public class WMSDataPage extends WizardPage {

	private WMSMapItem item;
	private DataSetHandle dataset;
	private ExtendedItemHandle handle;
	
	private Button setDataset;
	private Label lblNoDataSet;
	
	private Combo lstZoneId;
	private Combo lstValue;
	private boolean init;
	
	private List<ColorRange> colorRanges = new ArrayList<ColorRange>();
	private TableViewer colors;

	public WMSDataPage(String name, WMSMapItem item, DataSetHandle dataset, ExtendedItemHandle handle) {
		super(name);
		this.item = item;
		this.dataset = dataset;
		this.handle = handle;
	}

	@Override
	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(2,false));
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		lblNoDataSet = new Label(mainComposite, SWT.NONE);
		lblNoDataSet.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		
		setDataset = new Button(mainComposite, SWT.PUSH);
		setDataset.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		setDataset.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {					
				BindingDatasetDialog dial = new BindingDatasetDialog(getShell(), handle);
				if(dial.open() == Dialog.OK){
					try{
						if(init)
							refreshData();
						else
							initValues();
					}catch(Exception ex){
						ex.printStackTrace();
					}
					
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}	
		});
		
		setDataSetInfo();
		
		Label lblZoneId = new Label(mainComposite, SWT.NONE);
		lblZoneId.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblZoneId.setText("Select a column for the zone id: ");
		
		lstZoneId = new Combo(mainComposite, SWT.PUSH);
		lstZoneId.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		lstZoneId.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				item.setProperty(WMSMapItem.P_ZONE_ID_COLUMN, lstZoneId.getText());
				getContainer().updateButtons();
			}
		});
		Label lblValue = new Label(mainComposite, SWT.NONE);
		lblValue.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblValue.setText("Select a column for the value: ");
		
		lstValue = new Combo(mainComposite, SWT.PUSH);
		lstValue.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		lstValue.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				item.setProperty(WMSMapItem.P_VALUE_COLUMN, lstValue.getText());
				getContainer().updateButtons();
			}
		});
		
		if(dataset != null){
			try {
				initValues();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
		createColor(mainComposite);
		
		setControl(mainComposite);
	}
	
	private void initValues() {
		init = true;
		refreshData();
	}

	private void refreshData() {
		List<ColumnHintHandle> columns = new ArrayList<ColumnHintHandle>();
		dataset = handle.getDataSet();
		if(dataset != null){
			Iterator<ColumnHintHandle> it = dataset.columnHintsIterator();
			
			while(it.hasNext()){
				columns.add((ColumnHintHandle)it.next());
			}
			
			List<String> nameColumn = new ArrayList<String>();
			for(ColumnHintHandle column : columns){
				nameColumn.add(column.getColumnName());
			}
			
			String[] nameCol = nameColumn.toArray(new String[nameColumn.size()]);
			
			lstZoneId.setItems(nameCol);
			lstValue.setItems(nameCol);
			
			if(item.getProperty(WMSMapItem.P_ZONE_ID_COLUMN) != null){
				int index = lstZoneId.indexOf(item.getPropertyString(WMSMapItem.P_ZONE_ID_COLUMN));
				lstZoneId.select(index);
			}
			if(item.getProperty(WMSMapItem.P_VALUE_COLUMN) != null){
				int index = lstValue.indexOf(item.getPropertyString(WMSMapItem.P_VALUE_COLUMN));
				lstValue.select(index);
			}
		}
		setDataSetInfo();
	}
	
	

	private void setDataSetInfo(){
		if(dataset == null){
			lblNoDataSet.setText("No dataset is set to this item");
			setDataset.setText("Select Dataset");
		}
		else{
			lblNoDataSet.setText("");
			setDataset.setText("Change DataBinding");
		}
	}
	
	private void createColor(Composite parent){
		Composite compButton = new Composite(parent, SWT.NONE);
		compButton.setLayout(new GridLayout(2, false));
		compButton.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false, 2, 1));
		
		Button addColor = new Button(compButton, SWT.PUSH);
		addColor.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		addColor.setImage(Activator.getDefault().getImageRegistry().get(Icons.ADD_COLOR));
		addColor.setToolTipText("Add Color");
		addColor.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {					
				ColorRangeDialog dial = new ColorRangeDialog(getShell(), colorRanges);
				if(dial.open() == Dialog.OK){
					refreshTableColor();
					getContainer().updateButtons();
				}
			}
	
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}	
		});
		
		Button deleteColor = new Button(compButton, SWT.PUSH);
		deleteColor.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		deleteColor.setImage(Activator.getDefault().getImageRegistry().get(Icons.DEL_COLOR));
		deleteColor.setToolTipText("Delete Color");
		deleteColor.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)colors.getSelection();
				
				if (ss.isEmpty()){
					return; 
				}
				
				ColorRange color = (ColorRange)ss.getFirstElement();
				colorRanges.remove(color);
				
				refreshTableColor();
			}
	
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}	
		});
		
		colors = new TableViewer(parent);
		colors.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		colors.setContentProvider(new ColorTableContentProvider());
		colors.setLabelProvider(new ColorTableLabelProvider());
	
	}


	private void refreshTableColor(){
		colors.setInput(colorRanges);
	}
	
	public List<ColorRange> getColorRanges() {
		return colorRanges;
	}
	
}
