package bpm.birt.fusioncharts.ui.wizardeditor;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.birt.report.designer.ui.dialogs.ExpressionBuilder;
import org.eclipse.birt.report.designer.ui.dialogs.ExpressionProvider;
import org.eclipse.birt.report.model.api.DataSetHandle;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.birt.fusioncharts.core.model.IChart;
import bpm.birt.fusioncharts.core.reportitem.FusionchartsItem;
import bpm.birt.fusioncharts.ui.Activator;
import bpm.birt.fusioncharts.ui.dialogs.BindingDatasetDialog;
import bpm.birt.fusioncharts.ui.icons.Icons;
import bpm.birt.fusioncharts.ui.model.Serie;

public class WizardPageData extends WizardPage  {

	public static String NEW_SERIES = "<New Series...>";
	public static String AGG_NONE = "None";
	public static String AGG_SUM = "Sum";
	public static String AGG_COUNT = "Count";
	public static String[] GROUP_TYPE = {AGG_SUM, AGG_COUNT};
	public static String[] NO_GROUP = {AGG_NONE};
	
	private FusionchartsItem vanillaChart;
	private DataSetHandle dataSet;
	private ExtendedItemHandle handle;
	
	private String fusionChartJSPath;
	
	private String chartTitle;
	private String chartId;
	private int width = 0;
	private int height = 0;
	private String parameters;
	
	private IChart[] currentCharts; 
	private IChart selectedChart;
	private int selectedSubType;
	private boolean isMultiSeries = false;
	private boolean is3DSelected = false;
	private boolean isScrollable = false;
	private boolean useRoundEdges = false;
	
	private Composite main;
	private Composite compositeDataset;
	
	private Label lblNoDataSet;
	private Button setDataset;
	
	private Browser previewChart;

	private Text txtValueY, txtValueX;
	private Combo cbSeriesY, cbGroupValueY;
	private Button deleteSerie, groupEnabled;
	
	//Format Chart Part
	private Text txtChartTitle, txtChartID, txtColorHex, txtWidth, txtHeight, txtSerieYTitle, txtParam;
	private Combo cbSeriesYName;
	
	//Parameters to set when you open the builder
	private List<Serie> series;
	private String exprX;
	private boolean group = false;
	
	private ModifyListener listener = new ModifyListener(){

		public void modifyText(ModifyEvent e) {
			if (e.getSource().equals(txtValueY)) {
				if(!txtValueY.getText().equals("")){
					series.get(cbSeriesY.getSelectionIndex()).setExpr(txtValueY.getText());
				}
			}
			else if (e.getSource().equals(txtValueX)) {
				exprX = txtValueX.getText();
			}
			else if (e.getSource().equals(txtChartTitle)) {
				chartTitle = txtChartTitle.getText();
			}
			else if (e.getSource().equals(txtChartID)) {
				chartId = txtChartID.getText();
			}
			else if (e.getSource().equals(txtSerieYTitle)) {
				if(!txtSerieYTitle.getText().equals("")){
					series.get(cbSeriesYName.getSelectionIndex()).setName(txtSerieYTitle.getText());
				}
			}
			else if (e.getSource().equals(txtColorHex)) {
				if(!txtColorHex.getText().equals("")){
					series.get(cbSeriesYName.getSelectionIndex()).setColor(txtColorHex.getText());
				}
			}
			else if (e.getSource().equals(txtWidth)) {
				width = Integer.parseInt(txtWidth.getText());
			}
			else if (e.getSource().equals(txtHeight)) {
				height = Integer.parseInt(txtHeight.getText());
			}
			getContainer().updateButtons();
		}
		
	};
	
	protected WizardPageData(String pageName, FusionchartsItem item, DataSetHandle dataSet, ExtendedItemHandle handle) {
		super(pageName);
		this.vanillaChart = item;
		this.dataSet = dataSet;
		this.handle = handle;
		this.fusionChartJSPath = bpm.birt.fusioncharts.core.Activator.getDefault().getFusionChartJS();
	}

	@Override
	public void createControl(Composite parent) {
		main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		createContentNoDataset();
		createContent();
		
		setControl(main);
		
		initValues();
	}
	
	private void createContentNoDataset(){
		Composite compositeNoDataset = new Composite(main, SWT.NONE);
		compositeNoDataset.setLayout(new GridLayout(3, false));
		compositeNoDataset.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		
		lblNoDataSet = new Label(compositeNoDataset, SWT.NONE);
		lblNoDataSet.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		
		setDataset = new Button(compositeNoDataset, SWT.PUSH);
		setDataset.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		setDataset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				BindingDatasetDialog dial = new BindingDatasetDialog(getShell(), handle);
				if(dial.open() == Dialog.OK){
					dataSet = handle.getDataSet();
					if(dataSet != null){
						changeVisibility(true);
					}
				}
			}
		});
		
		setDataSetInfo();
	}
	
	private void createContent() {
		compositeDataset = new Composite(main, SWT.NONE);
		compositeDataset.setLayout(new GridLayout(3, false));
		compositeDataset.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label lblChartPreview = new Label(compositeDataset, SWT.BOLD);
		lblChartPreview.setLayoutData(new GridData(SWT.CENTER, SWT.BEGINNING, false, false, 3, 1));
		lblChartPreview.setText("Chart Preview");
		
		Composite compValueY = new Composite(compositeDataset, SWT.NONE);
		compValueY.setLayout(new GridLayout(3, false));
		compValueY.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true));
		
		Label lblValueY = new Label(compValueY, SWT.NONE);
		lblValueY.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false, 3, 1));
		lblValueY.setText("Value (Y) Series:*");
		
		cbSeriesY = new Combo(compValueY, SWT.PUSH);
		cbSeriesY.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		cbSeriesY.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(cbSeriesY.getText().equals(NEW_SERIES)){
					String[] items = cbSeriesY.getItems();
					cbSeriesY.add("Serie " + items.length, items.length - 1);
					cbSeriesY.select(items.length - 1);
					
					refreshTxtValueY("");
					
					Serie serie = new Serie("Serie " + String.valueOf(items.length - 1), "F6BD0F", "", AGG_SUM);
					series.add(serie);
					refreshComboGroupValueY(serie);
					builtCbValueYName();
					
					isMultiSeries = true;
					selectedChart = findChart();
					refreshPreview();
				}
				else{
					refreshTxtValueY(series.get(cbSeriesY.getSelectionIndex()).getExpr());
					refreshComboGroupValueY(series.get(cbSeriesY.getSelectionIndex()));
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
				
			}
		});
		
		deleteSerie = new Button(compValueY, SWT.PUSH);
		deleteSerie.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		deleteSerie.setToolTipText("Delete the selected serie");
		deleteSerie.setImage(Activator.getDefault().getImageRegistry().get(Icons.DEL_SERIE));
		deleteSerie.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(series.size() != 1){
					int index = cbSeriesY.getSelectionIndex();
					series.remove(index);
					
					builtCbValueY();
					
					if(series.size() == 1){
						isMultiSeries = false;
						selectedChart = findChart();
						refreshPreview();
					}
					builtCbValueYName();
					
					getContainer().updateButtons();
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
				
			}
		});
		
		cbGroupValueY = new Combo(compValueY, SWT.PUSH);
		cbGroupValueY.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		cbGroupValueY.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = cbSeriesY.getSelectionIndex();
				series.get(index).setAgg(cbGroupValueY.getText());
				getContainer().updateButtons();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
				
			}
		});
		
		txtValueY = new Text(compValueY, SWT.BORDER);
		txtValueY.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		
		Button exprButtonValueY = new Button(compValueY, SWT.PUSH);
		exprButtonValueY.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		exprButtonValueY.setText("...");
		exprButtonValueY.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				openExpression(txtValueY);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
				
			}
		});
		
		previewChart = new Browser(compositeDataset, SWT.BORDER);
		previewChart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		previewChart.setJavascriptEnabled(true);
		previewChart.addControlListener(new ControlListener() {
			
			@Override
			public void controlResized(ControlEvent e) {
				refreshPreview();
			}
			
			@Override
			public void controlMoved(ControlEvent e) {
				
				
			}
		});

		Composite compValueX = new Composite(compositeDataset, SWT.NONE);
		compValueX.setLayout(new GridLayout(4, false));
		compValueX.setLayoutData(new GridData(SWT.CENTER, SWT.BEGINNING, false, false, 3, 1));
		
		Label lblValueX = new Label(compValueX, SWT.NONE);
		lblValueX.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblValueX.setText("Value (X) Series:*");
		
		txtValueX = new Text(compValueX, SWT.BORDER);
		txtValueX.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		
		Button exprButtonValueX = new Button(compValueX, SWT.PUSH);
		exprButtonValueX.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		exprButtonValueX.setText("...");
		exprButtonValueX.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				openExpression(txtValueX);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
				
			}
		});
		
		groupEnabled = new Button(compValueX, SWT.CHECK);
		groupEnabled.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		groupEnabled.setText("Enabled Group");
		groupEnabled.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(groupEnabled.getSelection()){
					group = true;
					refreshComboGroupValueY(series.get(cbSeriesY.getSelectionIndex()));
				}
				else{
					group = false;
					refreshComboGroupValueY(series.get(cbSeriesY.getSelectionIndex()));
				}
				
				getContainer().updateButtons();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
				
			}
		});
		
		Composite formatChartComp = new Composite(compositeDataset, SWT.NONE);
		formatChartComp.setLayout(new GridLayout(3, false));
		formatChartComp.setLayoutData(new GridData(SWT.FILL, SWT.END, true, false, 3, 1));
		
		Composite charTitleComp = new Composite(formatChartComp, SWT.NONE);
		charTitleComp.setLayout(new GridLayout(2, false));
		charTitleComp.setLayoutData(new GridData(SWT.FILL, SWT.END, true, false));
		
		Label lblChartTitle = new Label(charTitleComp, SWT.BOLD);
		lblChartTitle.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblChartTitle.setText("Chart Title: ");
		
		txtChartTitle = new Text(charTitleComp, SWT.BORDER);
		txtChartTitle.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		
		Label lblChartID = new Label(charTitleComp, SWT.BOLD);
		lblChartID.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblChartID.setText("Chart ID: ");
		
		txtChartID = new Text(charTitleComp, SWT.BORDER);
		txtChartID.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		
		Label lblWidth = new Label(charTitleComp, SWT.NONE);
		lblWidth.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblWidth.setText("Width: ");
		
		txtWidth = new Text(charTitleComp, SWT.BORDER);
		txtWidth.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		
		Label lblHeight = new Label(charTitleComp, SWT.NONE);
		lblHeight.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblHeight.setText("Height: ");
		
		txtHeight = new Text(charTitleComp, SWT.BORDER);
		txtHeight.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		
		Label lblParam = new Label(charTitleComp, SWT.NONE);
		lblParam.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblParam.setText("Parameters: ");
		
		txtParam = new Text(charTitleComp, SWT.BORDER);
		txtParam.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		
		Composite compValueYName = new Composite(formatChartComp, SWT.NONE);
		compValueYName.setLayout(new GridLayout(3, false));
		compValueYName.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false, 2, 1));
		
		Label lblSerieName = new Label(compValueYName, SWT.NONE);
		lblSerieName.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblSerieName.setText("Value (Y) Series:*");
		
		cbSeriesYName = new Combo(compValueYName, SWT.PUSH);
		cbSeriesYName.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		cbSeriesYName.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				String serieName = series.get(cbSeriesYName.getSelectionIndex()).getName();
				String color = series.get(cbSeriesYName.getSelectionIndex()).getColor();
				
				if(serieName == null){
					refreshTxtValueYName("");
				}
				else{
					refreshTxtValueYName(serieName);
				}
				
				if(color == null){
					txtColorHex.setText("");
				}
				else{
					txtColorHex.setText(color);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
				
			}
		});
		
		txtSerieYTitle = new Text(compValueYName, SWT.BORDER);
		txtSerieYTitle.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 3, 1));
		
		txtColorHex = new Text(compValueYName, SWT.BORDER);
		txtColorHex.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 3, 1));
		
		Button pickColor = new Button(compValueYName, SWT.PUSH);
		pickColor.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		pickColor.setText("Pick Color");
		pickColor.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				ColorDialog dial = new ColorDialog(getShell());
				RGB rgb = dial.open();
				String rHex = Integer.toHexString(rgb.red);
				if(rHex.equals("0")){
					rHex = "00";
				}
		        String gHex = Integer.toHexString(rgb.green);
				if(gHex.equals("0")){
					gHex = "00";
				}
		        String bHex = Integer.toHexString(rgb.blue);
				if(bHex.equals("0")){
					bHex = "00";
				} 
		        String hex = rHex + gHex + bHex ;
		        txtColorHex.setText(hex);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
				
			}	
		});	
		
		if(dataSet == null){
			changeVisibility(false);
		}
	}
	
	private void refreshTxtValueY(String text) {
		txtValueY.setText(text);
	}
	
	private void changeVisibility(boolean bool){
		compositeDataset.setVisible(bool);
	}
	
	public void setDatasFromDef(IChart[] currentCh, IChart selectedCh, int selectedSubT, boolean is3DSelect, 
			boolean isScroll, boolean useRoundEdg){
		if(isControlCreated()){
			this.currentCharts = currentCh;
			this.selectedChart = selectedCh;
			this.selectedSubType = selectedSubT;
			this.is3DSelected = is3DSelect;
			this.isScrollable = isScroll;
			this.useRoundEdges = useRoundEdg;
			if(selectedChart.getType() == IChart.PIE || selectedChart.getType() == IChart.DOUGHNUT){
				enableSerieSelection(false);
			}
			else{
				enableSerieSelection(true);
				if(series.size()>1 && !selectedChart.isMultiSeries()){
					isMultiSeries = true;
					selectedChart = findChart();
				}
			}
			refreshPreview();
			
			getContainer().updateButtons();
		}
	}
	
	private void initValues() {
		if(!vanillaChart.getXml().equals("")){
			decodeXml(vanillaChart.getXml());
		}
		
		groupEnabled.setSelection(group);

		cbGroupValueY.setItems(GROUP_TYPE);
		builtCbValueY();
		
		if(exprX != null){
			txtValueX.setText(exprX);
		}
		
		builtCbValueYName();
		
		if(chartTitle != null){
			txtChartTitle.setText(chartTitle);
		}
		
		if(chartId != null){
			txtChartID.setText(chartId);
		}
		
		txtWidth.setText(String.valueOf(width));
		txtHeight.setText(String.valueOf(height));
		if(parameters != null){
			txtParam.setText(parameters);
		}
		
		txtValueY.addModifyListener(listener);	
		txtValueX.addModifyListener(listener);
		
		txtChartTitle.addModifyListener(listener);
		txtChartID.addModifyListener(listener);
		txtSerieYTitle.addModifyListener(listener);
		txtWidth.addModifyListener(listener);	
		txtHeight.addModifyListener(listener);
		txtColorHex.addModifyListener(listener);
		
	}
	
	private void builtCbValueYName(){
		List<String> items = new ArrayList<String>();
		items.add("Serie 1");
		for(int i=0; i<series.size()-1;i++){
			items.add("Serie " + (i+2));
		}
		cbSeriesYName.setItems(items.toArray(new String[items.size()]));
		cbSeriesYName.select(0);
		
		if(!series.isEmpty()){
			if(series.get(0).getName() == null){
				refreshTxtValueYName("");
			}
			else{
				refreshTxtValueYName(series.get(0).getName());
			}
			
			if(series.get(0).getColor() == null){
				txtColorHex.setText("");
			}
			else{
				txtColorHex.setText(series.get(0).getColor());
			}
		}
	}

	private void refreshTxtValueYName(String text) {
		txtSerieYTitle.setText(text);
	}
	
	private void builtCbValueY(){
		List<String> items = new ArrayList<String>();
		items.add("Serie 1");
		if(series == null || series.isEmpty()){
			series = new ArrayList<Serie>();
			series.add(new Serie("Serie 1", "F6BD0F", "",AGG_SUM));
		}
		else{
			for(int i=0; i<series.size()-1;i++){
				items.add("Serie " + (i+2));
			}
		}
		items.add(NEW_SERIES);
		cbSeriesY.setItems(items.toArray(new String[items.size()]));
		cbSeriesY.select(0);
		
		refreshComboGroupValueY(series.get(0));
		
		refreshTxtValueY(series.get(0).getExpr());
	}
	
	private void refreshComboGroupValueY(Serie serie){
		if(group){
			cbGroupValueY.setItems(GROUP_TYPE);
			if(serie.getAgg().equals(AGG_SUM)){
				cbGroupValueY.select(0);
			}
			else if(serie.getAgg().equals(AGG_COUNT)){
				cbGroupValueY.select(1);
			}
		}
		else{
			cbGroupValueY.setItems(NO_GROUP);
			cbGroupValueY.select(0);
		}
	}
	
	private void setDataSetInfo(){
		if(dataSet == null){
			lblNoDataSet.setText("No dataset is set to this item");
			setDataset.setText("Select Dataset");
		}
		else{
			lblNoDataSet.setText("");
			setDataset.setText("Data Binding");
		}
	}
	

	
	public void setDatasFromFormatChart(String chartTitle, int width, int height, List<Serie> series){
		this.chartTitle = chartTitle;
		this.width = width;
		this.height = height;
		this.series = series;
		getContainer().updateButtons();
	}
	
	private void enableSerieSelection(boolean enable) {
		cbSeriesY.setEnabled(enable);
		deleteSerie.setEnabled(enable);		
	}
	
	private void openExpression( Text textControl )
	{
		String oldValue = textControl.getText( );
		
		ExpressionBuilder eb = new ExpressionBuilder( textControl.getShell( ), oldValue );
		eb.setExpressionProvider( new ExpressionProvider( handle ) );
		eb.setHelpAvailable(true);
		
		String result = oldValue;
		
		if ( eb.open( ) == Window.OK )
		{
			result = eb.getResult( );
		}
		
		if ( !oldValue.equals( result ) )
		{
			textControl.setText( result );
		}
	}
	
	public void prepareChartDataXml() {
		if(selectedChart != null){
			String xml = "";
			xml += "<root>\n";
			xml += "  <chart>\n";
			xml += "	<chartname>" + selectedChart.getChartName() + "</chartname>\n";
			xml += "	<title>" + chartTitle + "</title>\n";
			xml += "	<id>" + chartId + "</id>\n";
			xml += "	<type>" + selectedChart.getType() + "</type>\n";
			if(useRoundEdges){
				xml += "	<glassstyle>1</glassstyle>\n";
			}
			else{
				xml += "	<glassstyle>0</glassstyle>\n";			
			}
			if(width == 0){
				xml += "	<width>" + previewChart.getSize().x + "</width>\n";
			}
			else{
				xml += "	<width>" + width + "</width>\n";				
			}
			if(height == 0){
				xml += "	<height>" + previewChart.getSize().y + "</height>\n";
			}
			else{
				xml += "	<height>" + height + "</height>\n";				
			}
			xml += "	<parameters>" + txtParam.getText() + "</parameters>\n";
			xml += "	<series>\n";
			for(Serie serie : series){
				xml += "	  <serie>\n";
				xml += "	    <name>" + serie.getName() + "</name>\n";
				xml += "	    <color>" + serie.getColor() + "</color>\n";
				xml += "	    <expy>" + serie.getExpr() + "</expy>\n";
				xml += "	    <agg>" + serie.getAgg() + "</agg>\n";
				xml += "	  </serie>\n";
				if(selectedChart.getType() == IChart.PIE || selectedChart.getType() == IChart.DOUGHNUT){
					break;
				}
			}
			xml += "	</series>\n";
			xml += "	<expx>" + txtValueX.getText() + "</expx>\n";
			xml += "	<group>" + group + "</group>\n";
			xml += "  </chart>\n";
			xml += "</root>";
			
			try {
				vanillaChart.setXml(xml);
			} catch (SemanticException e) {
				
				e.printStackTrace();
			}		
		}
	}
	
	@Override
	public boolean isPageComplete() {
		boolean isOk = true;
		for(Serie serie : series){
			if(serie.getExpr().equals("")){
				isOk = false;
				break;
			}
		}
		if(isOk && !txtValueX.getText().equals("")){
			return true;
		}
		else{
			return false;
		}
	}
	
	private void refreshPreview(){
		int widthTemp = previewChart.getSize().x;
		int heightTemp = previewChart.getSize().y;
		IChart chart = selectedChart;
		
		String chartPath = bpm.birt.fusioncharts.core.Activator.getDefault().getChart(chart.getChartName());
		
		StringBuffer html = new StringBuffer();
		html.append("<html>");
		html.append("    <head>");
		html.append("        <title>Test Map</title>");
		html.append("        <script language=\"JavaScript\" src=\"" + fusionChartJSPath + "\"></script>");
		html.append("    </head>");
		html.append("    <body>");
		html.append("    	<div id=\"mapdiv\">");
		html.append("        	FusionMaps.");
		html.append("        </div>");
		html.append("        <script type=\"text/javascript\">");
		html.append("        	 var chart = new FusionCharts(\"" + chartPath + "\", ");
		html.append("		    	\"myChartId\", \"" + (widthTemp - 50) + "\", \"" + (heightTemp - 50) + "\", \"0\", \"1\");");
		html.append("            chart.setXMLData(\"" + chart.getXmlExemple(useRoundEdges) + "\");");
		html.append("            chart.render(\"mapdiv\");");
		html.append("        </script>");
		html.append("     </body>");
		html.append("</html>");
		
		previewChart.setText(html.toString());
	}
	
	private IChart findChart(){
		int subType = selectedSubType;
		boolean is3D = is3DSelected;
		boolean isMS = isMultiSeries;
		boolean isScroll = isScrollable;
		IChart temp = null;
		
		if(subType != -1){
			for(IChart chart : currentCharts){
				if(chart.getSubType() == subType){
					if(isMS && chart.isMultiSeries()){
						if(chart.isScrollable() && isScroll){
							if(is3D && chart.is3D()){
								temp = chart;
								break;
							}
							else if(!is3D && !chart.is3D()){
								temp = chart;
								break;
							}
						}
						else if(!isScroll && !chart.isScrollable()){
							if(is3D && chart.is3D()){
								temp = chart;
								break;
							}
							else if(!is3D && !chart.is3D()){
								temp = chart;
								break;
							}
						}
					}
					else if(!isMS && !chart.isMultiSeries()){
						if(chart.isScrollable() && isScroll){
							if(is3D && chart.is3D()){
								temp = chart;
								break;
							}
							else if(!is3D && !chart.is3D()){
								temp = chart;
								break;
							}
						}
						else if(!isScroll && !chart.isScrollable()){
							if(is3D && chart.is3D()){
								temp = chart;
								break;
							}
							else if(!is3D && !chart.is3D()){
								temp = chart;
								break;
							}
						}
					}
				}
			}
		}
		return temp;
	}
	
	private void decodeXml(String xml){
		Document document = null;
		try {
			document = DocumentHelper.parseText(xml);
		} catch (DocumentException e1) {
			
			e1.printStackTrace();
		}
			
		for(Element e : (List<Element>)document.getRootElement().elements("chart")){
			try{
				if (e.element("series") != null){
					series = new ArrayList<Serie>();
					for(Element g : (List<Element>)e.elements("series")){
						if (g.element("serie") != null){
							for(Element h : (List<Element>)g.elements("serie")){
								Serie tempSerie = new Serie();
								if (h.element("name") != null){
									
									Element d = h.element("name");
									if(d != null){
										tempSerie.setName(d.getStringValue());
									}
								}
								if (h.element("color") != null){
									
									Element d = h.element("color");
									if(d != null){
										tempSerie.setColor(d.getStringValue());
									}
								}
								if (h.element("expy") != null){
									
									Element d = h.element("expy");
									if(d != null){
										tempSerie.setExpr(d.getStringValue());
									}
								}
								if (h.element("agg") != null){
									
									Element d = h.element("agg");
									if(d != null){
										tempSerie.setAgg(d.getStringValue());
									}
								}
								series.add(tempSerie);
							}
						}
					}
				}
				if (e.element("title") != null){
					
					Element d = e.element("title");
					if(d != null){
						this.chartTitle = d.getStringValue();
					}
				}
				if (e.element("id") != null){
					
					Element d = e.element("id");
					if(d != null){
						this.chartId = d.getStringValue();
					}
				}
				if (e.element("width") != null){
					
					Element d = e.element("width");
					if(d != null){
						this.width = Integer.parseInt(d.getStringValue());
					}
				}
				if (e.element("height") != null){
					
					Element d = e.element("height");
					if(d != null){
						this.height = Integer.parseInt(d.getStringValue());
					}
				}
				if (e.element("parameters") != null){
					
					Element d = e.element("parameters");
					if(d != null){
						this.parameters = d.getStringValue();
					}
				}
				if (e.element("expx") != null){
					
					Element d = e.element("expx");
					if(d != null){
						this.exprX = d.getStringValue();
					}
				}
				if (e.element("group") != null){
					
					Element d = e.element("group");
					if(d != null){
						this.group = Boolean.parseBoolean(d.getStringValue());
					}
				}
			}catch(Throwable ex){
				ex.printStackTrace();
			}
			
		}
	}
}
