package bpm.birt.gmaps.ui.wizardeditor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.birt.report.model.api.ColumnHintHandle;
import org.eclipse.birt.report.model.api.DataSetHandle;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
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

import bpm.birt.gmaps.core.reportitem.GooglemapsItem;
import bpm.birt.gmaps.ui.Activator;
import bpm.birt.gmaps.ui.dialogs.BindingDatasetDialog;
import bpm.birt.gmaps.ui.dialogs.ColorRangeDialog;
import bpm.birt.gmaps.ui.icons.Icons;
import bpm.birt.gmaps.ui.viewers.ColorTableContentProvider;
import bpm.birt.gmaps.ui.viewers.ColorTableLabelProvider;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;

public class WizardPageData extends WizardPage  {
	private TableViewer colors;
//	private List<ColorRange> colorRanges = new ArrayList<ColorRange>();
	
	private ExtendedItemHandle handle;
	private GooglemapsItem vanillaGMap;
	private DataSetHandle dataSet;
	
	private String mapLatitudeColumnName;
	private String mapLongitudeColumnName;
	private String mapValuesColumnName;
	private String mapLabelColumnName;
	
	private Composite main;
	private Label lblNoDataSet, lblLatitude, lblLongitude, lblValue, lblLabel;
	
	private Combo cbLatitude, cbLongitude, cbValues, cbLabel;
	
	private Button setDataset;//, addColor, deleteColor;;
	
	//Check if the init has been done
	private boolean init = false;
	
	protected WizardPageData(String pageName, GooglemapsItem item, DataSetHandle dataSet, ExtendedItemHandle handle) {
		super(pageName);
		this.vanillaGMap = item;
		this.dataSet = dataSet;
		this.handle = handle;
	}

	@Override
	public void createControl(Composite parent) {
		main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		createContentNoDataset();
		createContent();
		
		setControl(main);
	}
	
	private void createContentNoDataset(){
		lblNoDataSet = new Label(main, SWT.NONE);
		lblNoDataSet.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		
		setDataset = new Button(main, SWT.PUSH);
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
	}
	
	private void setDataSetInfo(){
		if(dataSet == null){
			lblNoDataSet.setText("No dataset is set to this item");
			setDataset.setText("Select Dataset");
		}
		else{
			lblNoDataSet.setText("");
			setDataset.setText("Change DataBinding");
		}
	}
	
	private void createContent(){		
		
		lblLabel = new Label(main, SWT.NONE);
		lblLabel.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblLabel.setText("Select a column for the marker name: ");
		
		cbLabel = new Combo(main, SWT.PUSH);
		cbLabel.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		cbLabel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapLabelColumnName = cbLabel.getText();
				try {
					vanillaGMap.setMapColumnLabel(mapLabelColumnName);
				} catch (SemanticException e1) {
					
					e1.printStackTrace();
				}
				getContainer().updateButtons();
			}
		});
		lblLatitude = new Label(main, SWT.NONE);
		lblLatitude.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblLatitude.setText("Select a column for the latitude: ");
		
		cbLatitude = new Combo(main, SWT.PUSH);
		cbLatitude.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		cbLatitude.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapLatitudeColumnName = cbLatitude.getText();
				try {
					vanillaGMap.setMapColumnLatitude(mapLatitudeColumnName);
				} catch (SemanticException e1) {
					
					e1.printStackTrace();
				}
				getContainer().updateButtons();
			}
		});
		
		lblLongitude = new Label(main, SWT.NONE);
		lblLongitude.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblLongitude.setText("Select a column for the longitude: ");
		
		cbLongitude = new Combo(main, SWT.PUSH);
		cbLongitude.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		cbLongitude.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapLongitudeColumnName = cbLongitude.getText();
				try {
					vanillaGMap.setMapColumnLongitude(mapLongitudeColumnName);
				} catch (SemanticException e1) {
					
					e1.printStackTrace();
				}
				getContainer().updateButtons();
			}
		});
		
		lblValue = new Label(main, SWT.NONE);
		lblValue.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblValue.setText("Select a column for the values: ");
		
		cbValues = new Combo(main, SWT.PUSH);
		cbValues.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		cbValues.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapValuesColumnName = cbValues.getText();
				try {
					vanillaGMap.setMapColumnValue(mapValuesColumnName);
				} catch (SemanticException e1) {
					e1.printStackTrace();
				}
				getContainer().updateButtons();
			}
		});
		
		
//		createColor(main);
		if(dataSet != null){
			try {
				initValues();
			} catch (Exception e1) {
				
				e1.printStackTrace();
			}
		}
		else{
			changeVisibility(false);
		}
	}
	
	private void changeVisibility(boolean bool){
		lblLatitude.setVisible(bool);
		lblLongitude.setVisible(bool);
		lblValue.setVisible(bool);
		cbLatitude.setVisible(bool);
		cbLongitude.setVisible(bool);
		cbValues.setVisible(bool);
		cbLabel.setVisible(bool);
		lblLabel.setVisible(bool);
	}
	
	private void initValues() throws Exception{	
		refreshData();
		init = true;
	}
	
	private void refreshData() throws Exception{		
		List<ColumnHintHandle> columns = new ArrayList<ColumnHintHandle>();
		dataSet = handle.getDataSet();
		if(dataSet != null){
			Iterator<ColumnHintHandle> it = dataSet.columnHintsIterator();
			
			while(it.hasNext()){
				columns.add((ColumnHintHandle)it.next());
			}
			
			List<String> nameColumn = new ArrayList<String>();
			for(ColumnHintHandle column : columns){
				nameColumn.add(column.getColumnName());
			}
			
			String[] nameCol = nameColumn.toArray(new String[nameColumn.size()]);
			
			cbLatitude.setItems(nameCol);
			cbLongitude.setItems(nameCol);
			cbValues.setItems(nameCol);
			cbLabel.setItems(nameCol);
			
			if(vanillaGMap.getMapColumnLatitude() != null){
				int index = cbLatitude.indexOf(vanillaGMap.getMapColumnLatitude());
				cbLatitude.select(index);
			}
			if(vanillaGMap.getMapColumnLongitude() != null){
				int index = cbLongitude.indexOf(vanillaGMap.getMapColumnLongitude());
				cbLongitude.select(index);
			}
			if(vanillaGMap.getMapColumnValue() != null){
				int index = cbValues.indexOf(vanillaGMap.getMapColumnValue());
				cbValues.select(index);
			}
			if(vanillaGMap.getMapColumnLabel() != null){
				int index = cbLabel.indexOf(vanillaGMap.getMapColumnLabel());
				cbLabel.select(index);
			}
		}
		setDataSetInfo();
		changeVisibility(true);
	}
	
	
	@Override
	public boolean isPageComplete() {
		if((mapLatitudeColumnName != null && !mapLatitudeColumnName.equals(""))
				|| (mapLongitudeColumnName != null && !mapLongitudeColumnName.equals("")) 
				|| (mapValuesColumnName != null && !mapValuesColumnName.equals("")
				|| (mapLabelColumnName != null && !mapLabelColumnName.equals("")))){
			return true;
		}
		else{
			return true;
		}
	}
	
//	private void createColor(Composite parent){
//		Composite compButton = new Composite(parent, SWT.NONE);
//		compButton.setLayout(new GridLayout(2, false));
//		compButton.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false, 2, 1));
//		
//		addColor = new Button(compButton, SWT.PUSH);
//		addColor.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
//		addColor.setImage(Activator.getDefault().getImageRegistry().get(Icons.ADD_COLOR));
//		addColor.setToolTipText("Add Color");
//		addColor.addSelectionListener(new SelectionListener() {
//			
//			@Override
//			public void widgetSelected(SelectionEvent e) {					
//				ColorRangeDialog dial = new ColorRangeDialog(getShell(), colorRanges);
//				if(dial.open() == Dialog.OK){
//					refreshTableColor();
//					prepareMapDataXml();
//					getContainer().updateButtons();
//				}
//			}
//
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) {
//				
//				
//			}	
//		});
//		
//		deleteColor = new Button(compButton, SWT.PUSH);
//		deleteColor.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
//		deleteColor.setImage(Activator.getDefault().getImageRegistry().get(Icons.DEL_COLOR));
//		deleteColor.setToolTipText("Delete Color");
//		deleteColor.addSelectionListener(new SelectionListener() {
//			
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				IStructuredSelection ss = (IStructuredSelection)colors.getSelection();
//				
//				if (ss.isEmpty()){
//					return; 
//				}
//				
//				ColorRange color = (ColorRange)ss.getFirstElement();
//				colorRanges.remove(color);
//				
//				refreshTableColor();
//				prepareMapDataXml();
//			}
//
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) {
//				
//				
//			}	
//		});
//		
//		colors = new TableViewer(main);
//		colors.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
//		colors.setContentProvider(new ColorTableContentProvider());
//		colors.setLabelProvider(new ColorTableLabelProvider());
//
//	}
	
	
//	private void refreshTableColor(){
//		colors.setInput(colorRanges);
//	}
	
	
//	private void prepareMapDataXml() {
//		String xml = "";
//		xml += "<root>\n";
//		xml += "    <colorRange>\n";
//		for(ColorRange color : colorRanges){
//			xml += "      <color>\n";
//			xml += "        <name>" + color.getName() + "</name>\n";
//			xml += "        <hexa>" + color.getHex() + "</hexa>\n";
//			xml += "        <min>" + color.getMin() + "</min>\n";
//			xml += "        <max>" + color.getMax() + "</max>\n";
//			xml += "      </color>\n";
//		}
//		xml += "    </colorRange>\n";
//		
//		xml += "</root>";
//		
//		try {
//			vanillaGMap.setMapColorRanges(xml);
//		} catch (SemanticException e) {
//			
//			e.printStackTrace();
//		}
//	}
}
