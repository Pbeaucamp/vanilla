package bpm.birt.fusionmaps.ui.wizardeditor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.birt.report.model.api.ColumnHintHandle;
import org.eclipse.birt.report.model.api.DataSetHandle;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.birt.fusionmaps.core.reportitem.FusionmapsItem;
import bpm.birt.fusionmaps.ui.Activator;
import bpm.birt.fusionmaps.ui.dialogs.BindingDatasetDialog;
import bpm.birt.fusionmaps.ui.dialogs.ColorRangeDialog;
import bpm.birt.fusionmaps.ui.icons.Icons;
import bpm.birt.fusionmaps.ui.viewers.ColorTableContentProvider;
import bpm.birt.fusionmaps.ui.viewers.ColorTableLabelProvider;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;

public class WizardPageData extends WizardPage  {

	private ExtendedItemHandle handle;
	private FusionmapsItem vanillaMap;
	private DataSetHandle dataSet;
	
	private Composite main;
	private Composite compositeDataset;
	
	private Label lblNoDataSet;
	
	private Text txtMapParams;
	
	private Combo cbId;
	private Combo cbValues;
	private Text txtUnit;
	private TableViewer colors;
	
	private Button setDataset;
	
	private String unit;
	private String mapIdColumnName;
	private String mapValuesColumnName;
	private String parameters;
	private List<ColorRange> colorRanges;
	
	//Check if the init has been done
	private boolean init = false;
	
	protected WizardPageData(String pageName, FusionmapsItem item, DataSetHandle dataSet, ExtendedItemHandle handle) {
		super(pageName);
		this.vanillaMap = item;
		this.dataSet = dataSet;
		this.handle = handle;
	}

	@Override
	public void createControl(Composite parent) {
		main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		createContentNoDataset();
		createContent();
		
		setControl(main);
	}
	
	private void createContentNoDataset(){
		Composite compositeNoDataset = new Composite(main, SWT.NONE);
		compositeNoDataset.setLayout(new GridLayout(2, false));
		compositeNoDataset.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		lblNoDataSet = new Label(compositeNoDataset, SWT.NONE);
		lblNoDataSet.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		
		setDataset = new Button(compositeNoDataset, SWT.PUSH);
		setDataset.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		setDataset.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {					
				BindingDatasetDialog dial = new BindingDatasetDialog(getShell(), handle);
				if(dial.open() == Dialog.OK){
					if(init)
						refreshData();
					else
						initValues();
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
		compositeDataset = new Composite(main, SWT.NONE);
		compositeDataset.setLayout(new GridLayout(2, false));
		compositeDataset.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label lblValueUnit = new Label(compositeDataset, SWT.NONE);
		lblValueUnit.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblValueUnit.setText("Set a unit for your values: ");
		
		txtUnit = new Text(compositeDataset, SWT.BORDER);
		txtUnit.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		
		Label lblId = new Label(compositeDataset, SWT.NONE);
		lblId.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblId.setText("Select a column for the id: ");
		
		cbId = new Combo(compositeDataset, SWT.PUSH);
		cbId.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		cbId.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapIdColumnName = cbId.getText();
				getContainer().updateButtons();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		Label lblValue = new Label(compositeDataset, SWT.NONE);
		lblValue.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblValue.setText("Select a column for the values: ");
		
		cbValues = new Combo(compositeDataset, SWT.PUSH);
		cbValues.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		cbValues.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapValuesColumnName = cbValues.getText();
				getContainer().updateButtons();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		Label lblParam = new Label(compositeDataset, SWT.NONE);
		lblParam.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblParam.setText("XML Parameters: ");
		
		txtMapParams = new Text(compositeDataset, SWT.BORDER);
		txtMapParams.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		
		Composite compButton = new Composite(compositeDataset, SWT.NONE);
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
					prepareMapDataXml();
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
				prepareMapDataXml();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}	
		});
		
		colors = new TableViewer(compositeDataset);
		colors.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		colors.setContentProvider(new ColorTableContentProvider());
		colors.setLabelProvider(new ColorTableLabelProvider());
		
		if(dataSet != null){
			initValues();
		}
		else{
			changeVisibility(false);
		}
	}
	
	private void refreshTableColor(){
		colors.setInput(colorRanges);
	}
	
	private void initValues() {
		decodeXml();
		
		//We set the unit for the values
		if(unit != null){
			txtUnit.setText(unit);
		}
		
		//We set the color that are already on the map
		if(colorRanges != null){
			colors.setInput(colorRanges);
		}
		else{
			colorRanges = new ArrayList<ColorRange>();
		}
		
		if(parameters != null){
			txtMapParams.setText(parameters);
		}
		
		refreshData();
		init = true;
	}
	
	private void changeVisibility(boolean bool){
		compositeDataset.setVisible(bool);
	}
	
	private void refreshData(){		
		List<ColumnHintHandle> columns = new ArrayList<ColumnHintHandle>();
		dataSet = handle.getDataSet();
		dataSet.getPropertyBindings();
		if(dataSet != null){
			Iterator it = dataSet.columnHintsIterator();
	
			while(it.hasNext()){
				columns.add((ColumnHintHandle)it.next());
			}
			
			List<String> nameColumnId = new ArrayList<String>();
			int i = 0;
			int indexId = -1;
			int indexValues = -1;
			for(ColumnHintHandle column : columns){
				nameColumnId.add(column.getColumnName());
				if(mapIdColumnName != null && column.getColumnName().equals(mapIdColumnName)){
					indexId = i;
				}
				if(mapValuesColumnName != null && column.getColumnName().equals(mapValuesColumnName)){
					indexValues = i;
				}
				i++;
			}
			
			String[] nameColId = nameColumnId.toArray(new String[nameColumnId.size()]);
			String[] nameColValues = nameColId;
			
			cbId.setItems(nameColId);
			cbValues.setItems(nameColValues);
			
			if(indexId != -1){
				cbId.select(indexId);
			}
			if(indexValues != -1){
				cbValues.select(indexValues);
			}
		}
		setDataSetInfo();
		changeVisibility(true);
	}
	
	public void prepareMapDataXml() {
		StringBuilder buf = new StringBuilder();
		buf.append("<root>\n");
		buf.append("  <map>\n");
		buf.append("    <unit>" + txtUnit.getText() + "</unit>\n");
		buf.append("    <id>" + mapIdColumnName + "</id>\n");
		buf.append("    <values>" + mapValuesColumnName + "</values>\n");
		buf.append("    <parameters>" + txtMapParams.getText() + "</parameters>\n");
		buf.append("    <colorRange>\n");
		for(ColorRange color : colorRanges){
			buf.append("      <color>\n");
			buf.append("        <name>" + color.getName() + "</name>\n");
			buf.append("        <hexa>" + color.getHex() + "</hexa>\n");
			buf.append("        <min>" + color.getMin() + "</min>\n");
			buf.append("        <max>" + color.getMax() + "</max>\n");
			buf.append("      </color>\n");
		}
		buf.append("    </colorRange>\n");
		buf.append("  </map>\n");
		buf.append("</root>");
		
		try {
			vanillaMap.setMapDataXML(buf.toString());
		} catch (SemanticException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public boolean isPageComplete() {
		if((mapIdColumnName != null && !mapIdColumnName.equals("")) 
				|| (mapValuesColumnName != null && !mapValuesColumnName.equals(""))){
			return true;
		}
		else{
			return true;
		}
	}
	
	//We get the parameters set to this map
	private void decodeXml(){
		String xml = vanillaMap.getMapDataXML();
		if(xml != null){
			Document document = null;
			try {
				document = DocumentHelper.parseText(xml);
			} catch (DocumentException e1) {
				e1.printStackTrace();
			}
			
			for(Element e : (List<Element>)document.getRootElement().elements("map")){
				try{
					if (e.element("unit") != null){
						
						Element d = e.element("unit");
						if(d != null){
							this.unit = d.getStringValue();
						}
					}
					if (e.element("id") != null){
						
						Element d = e.element("id");
						if(d != null){
							this.mapIdColumnName = d.getStringValue();
						}
					}
					if (e.element("values") != null){
						
						Element d = e.element("values");
						if(d != null){
							this.mapValuesColumnName = d.getStringValue();
						}
					}
					if (e.element("parameters") != null){
						
						Element d = e.element("parameters");
						if(d != null){
							this.parameters = d.getStringValue();
						}
					}
					colorRanges = new ArrayList<ColorRange>();
					if (e.element("colorRange") != null){
						for(Element g : (List<Element>) e.elements("colorRange")){
							if(g.element("color") != null){
								for(Element h : (List<Element>) g.elements("color")){
									ColorRange color = new ColorRange();
									if (h.element("name") != null){
										
										Element d = h.element("name");
										if(d != null){
											color.setName(d.getStringValue());
										}
									}
									if (h.element("hexa") != null){
										
										Element d = h.element("hexa");
										if(d != null){
											color.setHex(d.getStringValue());
										}
									}
									if (h.element("min") != null){
										
										Element d = h.element("min");
										if(d != null){
											color.setMin(Integer.parseInt(d.getStringValue()));
										}
									}
									if (h.element("max") != null){
										
										Element d = h.element("max");
										if(d != null){
											color.setMax(Integer.parseInt(d.getStringValue()));
										}
									}
									colorRanges.add(color);
								}
							}
						}
					}
				}catch(Throwable ex){
					ex.printStackTrace();
				}
				
			}
		}
	}
}
