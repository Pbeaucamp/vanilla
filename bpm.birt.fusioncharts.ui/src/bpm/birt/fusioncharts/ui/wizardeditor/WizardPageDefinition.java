package bpm.birt.fusioncharts.ui.wizardeditor;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import bpm.birt.fusioncharts.core.chart.AvailableChart;
import bpm.birt.fusioncharts.core.model.IChart;
import bpm.birt.fusioncharts.core.reportitem.FusionchartsItem;
import bpm.birt.fusioncharts.ui.Activator;
import bpm.birt.fusioncharts.ui.icons.Icons;
import bpm.birt.fusioncharts.ui.viewers.ChartTypeTableContentProvider;
import bpm.birt.fusioncharts.ui.viewers.ChartTypeTableLabelProvider;

public class WizardPageDefinition extends WizardPage  {
	public static final String P_VANILLA_RUNTIME = "bpm.birt.item.VanillaRuntime";

//	private static String GET_MAPS_SERVLET_URL = "/GetMapsServlet";
//	private static String ADD_MAP_SERVLET_URL = "/AddMapServlet";
	
	private FusionchartsItem vanillaChart;
	
	private String fusionChartJSPath;
	
	private IChart[] currentCharts; 
	private IChart selectedChart;
	private String chartName;
	private int selectedSubType = 0;
	private int selectedType = 0;
	private boolean is3DSelected = false;
	
	//VanillaRuntime part
	private Label lblVanillaRuntimeUrl;
	private Text txtVanillaRuntimeUrl;
	
	//Browser Part
	private Label lblChartPreview;
	private Browser previewChart;
	
	//Definition Part
	private Label lblChartType;
	private TableViewer chartTypeTableViewer;

	private Label lblChartSubType;
	
	//Subtype button
	private Button subtype1;
	private Button subtype2;
	
	private Label lblDimension;
	private Combo cbDimension;
	
	private Label lblStyle;
	private Button useGlassStyleCheck;
//	private Button enableScrollingCheck;
	
	//Glass Style
	private boolean useRoundEdges = false;
	//Multiseries
	private boolean isMultiSeries = false;
	//Scroll
	private boolean isScrollable = false;
	
	private ModifyListener listener = new ModifyListener(){

		public void modifyText(ModifyEvent e) {
			if (e.getSource().equals(txtVanillaRuntimeUrl)) {
				if(!txtVanillaRuntimeUrl.getText().equals("")){
					try {
						vanillaChart.setVanillaRuntimeUrl(txtVanillaRuntimeUrl.getText());
					} catch (SemanticException e1) {
						
						e1.printStackTrace();
					}
				}
			}
			getContainer().updateButtons();
		}
		
	};
	
	protected WizardPageDefinition(String pageName, FusionchartsItem vanillaChart) {
		super(pageName);
		this.vanillaChart = vanillaChart;
//		this.fusionChartJSPath = "http://localhost:8080/charts/FusionCharts.js";
		this.fusionChartJSPath = bpm.birt.fusioncharts.core.Activator.getDefault().getFusionChartJS();
	}

	@Override
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true , true));
		
		createContent(main);
		
		setControl(main);
		
		initValues( );
	}
	
	private void createContent(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true , true));
		
		lblVanillaRuntimeUrl = new Label(main, SWT.BOLD);
		lblVanillaRuntimeUrl.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblVanillaRuntimeUrl.setText("Vanilla Runtime Url");
		
		txtVanillaRuntimeUrl = new Text(main, SWT.BORDER);
		txtVanillaRuntimeUrl.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		
		lblChartPreview = new Label(main, SWT.BOLD);
		lblChartPreview.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false, 2, 1));
		lblChartPreview.setText("Chart Preview");
		
		previewChart = new Browser(main, SWT.NONE);
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
		
		Composite globalDef = new Composite(main, SWT.NONE);
		globalDef.setLayout(new GridLayout(2, false));
		globalDef.setLayoutData(new GridData(SWT.LEFT, SWT.END, false, false, 2, 1));
		
		Composite chartTypeComp = new Composite(globalDef, SWT.NONE);
		chartTypeComp.setLayout(new GridLayout());
		chartTypeComp.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
		
		lblChartType = new Label(chartTypeComp, SWT.None);
		lblChartType.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblChartType.setText("Select Chart Type"); //$NON-NLS-1$
		
		chartTypeTableViewer = new TableViewer(chartTypeComp, SWT.BORDER);
		chartTypeTableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		chartTypeTableViewer.setContentProvider(new ChartTypeTableContentProvider());
		chartTypeTableViewer.setLabelProvider(new ChartTypeTableLabelProvider());
		chartTypeTableViewer.addSelectionChangedListener(new ISelectionChangedListener(){

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection)chartTypeTableViewer.getSelection();
				
				if (ss.isEmpty()){
					return; 
				}
				
				String type = (String)ss.getFirstElement();
				
				int typeNumber = -1;
				currentCharts = new IChart[0];
				String[] chartTypes = IChart.CHART_TYPES;
				for(int i=0; i<chartTypes.length;i++){
					if(type.equals(chartTypes[i])){
						typeNumber = i;
						break;
					}
				}
				
				selectedChart = changeTypeChart(typeNumber, false);
				refreshPreview();
			}
			
		});
	
		Composite chartSubTypeAndStyleComp = new Composite(globalDef, SWT.NONE);
		chartSubTypeAndStyleComp.setLayout(new GridLayout(2, false));
		chartSubTypeAndStyleComp.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
		
		lblChartSubType = new Label(chartSubTypeAndStyleComp, SWT.None);
		lblChartSubType.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false, 2, 1));
		lblChartSubType.setText("Select Subtype"); //$NON-NLS-1$
	
		Composite chartSubtypeComp = new Composite(chartSubTypeAndStyleComp, SWT.NONE);
		chartSubtypeComp.setLayout(new GridLayout(2, false));
		chartSubtypeComp.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 2, 1));
		
		builtSubtypePart(chartSubtypeComp);
		
		lblDimension = new Label(chartSubTypeAndStyleComp, SWT.None);
		lblDimension.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblDimension.setText("Dimension: "); //$NON-NLS-1$
		
		cbDimension = new Combo(chartSubTypeAndStyleComp, SWT.PUSH);
		cbDimension.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		cbDimension.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(cbDimension.getText().equals(IChart.TWO_D)){
					is3DSelected = false;
					if(selectedType != IChart.LINE || selectedType != IChart.PIE || selectedType != IChart.DOUGHNUT){
		            	useGlassStyleCheck.setEnabled(true);
		            	if(useGlassStyleCheck.getSelection()){
							useRoundEdges = true;
						}
						else{
							useRoundEdges = false;
						}
					}
					selectedChart = findChart();
					refreshPreview();
				}
				else{
					is3DSelected = true;
	            	useGlassStyleCheck.setEnabled(false);
					useRoundEdges = false;
					selectedChart = findChart();
					refreshPreview();
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
				
			}
		});
		
		lblStyle = new Label(chartSubTypeAndStyleComp, SWT.None);
		lblStyle.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblStyle.setText("Style: "); //$NON-NLS-1$
		
		Composite buttonCheckComp = new Composite(chartSubTypeAndStyleComp, SWT.NONE);
		buttonCheckComp.setLayout(new GridLayout(2, true));
		buttonCheckComp.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
		
		useGlassStyleCheck = new Button(buttonCheckComp, SWT.CHECK);
		useGlassStyleCheck.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		useGlassStyleCheck.setText("Use Glass Style");
		useGlassStyleCheck.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(useGlassStyleCheck.getSelection()){
					useRoundEdges = true;
					refreshPreview();
				}
				else{
					useRoundEdges = false;
					refreshPreview();
				}
				
				getContainer().updateButtons();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
				
			}	
		});
		
//		enableScrollingCheck = new Button(buttonCheckComp, SWT.CHECK);
//		enableScrollingCheck.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
//		enableScrollingCheck.setText("Enable Scrolling");
//		enableScrollingCheck.addSelectionListener(new SelectionListener() {
//			
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				if(enableScrollingCheck.getSelection()){
//					isScrollable = true;
//					selectedChart = findChart();
//					refreshPreview();
//				}
//				else{
//					isScrollable = false;
//					selectedChart = findChart();
//					refreshPreview();
//				}
//			}
//
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) {
//				
//				
//			}	
//		});
	}
	
	private void builtSubtypePart(Composite comp) {
		subtype1 = new Button(comp, SWT.PUSH);
		subtype1.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		subtype1.setImage(Activator.getDefault().getImageRegistry().get(Icons.SIDE_BY_SIDE_COLUMN));
		subtype1.setToolTipText("test 1");
		subtype1.setVisible(false);
		subtype1.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedSubType = 0;
				if(selectedType == IChart.BAR){
					isMultiSeries = true;
				}
				selectedChart = findChart();
				refreshPreview();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
				
			}	
		});
		
		subtype2 = new Button(comp, SWT.PUSH);
		subtype2.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		subtype2.setImage(Activator.getDefault().getImageRegistry().get(Icons.STACKED_COLUMN));
		subtype2.setToolTipText("test 1");
		subtype2.setVisible(false);
		subtype2.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedSubType = 1;
				isMultiSeries = true;
				selectedChart = findChart();
				refreshPreview();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
				
			}	
		});
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
	
	private IChart changeTypeChart(int type, boolean init){
		String[] chartSubType;
		
		List<String> icons = new ArrayList<String>();
        switch (type) {
            case IChart.COLUMN:  
            	currentCharts = AvailableChart.AVAILABLE_CHARTS_COLUMN;
            	chartSubType = IChart.COLUMN_SUBTYPES;
            	icons = findIcons(IChart.COLUMN);
//            	enableScrollingCheck.setEnabled(true);
            	if(!is3DSelected){
                	useGlassStyleCheck.setEnabled(true);
                	if(!init){
		            	if(useGlassStyleCheck.getSelection()){
							useRoundEdges = true;
						}
						else{
							useRoundEdges = false;
						}
                	}
                	else{
                		useGlassStyleCheck.setGrayed(useRoundEdges);
                	}
            	}
            	isMultiSeries = false;
            	selectedType = IChart.COLUMN;
            	selectedSubType = IChart.COLUMN_SUBTYPE_SIDE_BY_SIDE;
            	refreshCbDimension();
            	break;
            case IChart.BAR:  
            	currentCharts = AvailableChart.AVAILABLE_CHARTS_BAR; 
            	chartSubType = IChart.BAR_SUBTYPES; 
            	icons = findIcons(IChart.BAR);
//            	enableScrollingCheck.setEnabled(false);
            	if(!is3DSelected){
                	useGlassStyleCheck.setEnabled(true);
                	if(!init){
		            	if(useGlassStyleCheck.getSelection()){
							useRoundEdges = true;
						}
						else{
							useRoundEdges = false;
						}
                	}
                	else{
                		useGlassStyleCheck.setSelection(useRoundEdges);
                	}
            	}
            	isMultiSeries = true;
            	selectedType = IChart.BAR;
            	selectedSubType = IChart.BAR_SUBTYPE_SIDE_BY_SIDE;
            	refreshCbDimension();
            	break;
            case IChart.LINE:  
            	currentCharts = AvailableChart.AVAILABLE_CHARTS_LINE; 
            	chartSubType = IChart.LINE_SUBTYPES;
            	icons = findIcons(IChart.LINE);
            	useGlassStyleCheck.setEnabled(false);
//            	enableScrollingCheck.setEnabled(true);
				useRoundEdges = false;
				is3DSelected = false;
            	isMultiSeries = false;
            	selectedType = IChart.LINE;
            	selectedSubType = IChart.LINE_SUBTYPE_OVERLAY;
            	refreshCbDimension();
            	break;
            case IChart.PIE:  
            	currentCharts = AvailableChart.AVAILABLE_CHARTS_PIE;  
            	chartSubType = IChart.PIE_SUBTYPES;
            	icons = findIcons(IChart.PIE);
            	useGlassStyleCheck.setEnabled(false);
//            	enableScrollingCheck.setEnabled(false);
				useRoundEdges = false;
            	isMultiSeries = false;
            	selectedType = IChart.PIE;
            	selectedSubType = IChart.PIE_SUBTYPE;
            	refreshCbDimension();
            	break;
            case IChart.DOUGHNUT:  
            	currentCharts = AvailableChart.AVAILABLE_CHARTS_DOUGHNUT; 
            	chartSubType = IChart.DOUGHNUT_SUBTYPES;
            	icons = findIcons(IChart.DOUGHNUT);
            	useGlassStyleCheck.setEnabled(false);
//            	enableScrollingCheck.setEnabled(false);
				useRoundEdges = false;
            	isMultiSeries = false;
            	selectedType = IChart.DOUGHNUT;
            	selectedSubType = IChart.DOUGHNUT_SUBTYPE;
            	refreshCbDimension();
            	break;
            case IChart.RADAR:  
            	currentCharts = AvailableChart.AVAILABLE_CHARTS_RADAR; 
            	chartSubType = IChart.RADAR_SUBTYPES;
            	icons = findIcons(IChart.RADAR);
            	useGlassStyleCheck.setEnabled(false);
//            	enableScrollingCheck.setEnabled(false);
				useRoundEdges = false;
            	isMultiSeries = true;
            	selectedType = IChart.RADAR;
            	selectedSubType = IChart.RADAR_SUBTYPE;
            	refreshCbDimension();
            	break;
            case IChart.PARETO:  
            	currentCharts = AvailableChart.AVAILABLE_CHARTS_PARETO;
            	chartSubType = IChart.PARETO_SUBTYPES;
            	icons = findIcons(IChart.PARETO);
//            	enableScrollingCheck.setEnabled(true);
            	if(!is3DSelected){
                	useGlassStyleCheck.setEnabled(true);
                	if(!init){
		            	if(useGlassStyleCheck.getSelection()){
							useRoundEdges = true;
						}
						else{
							useRoundEdges = false;
						}
                	}
                	else{
                		useGlassStyleCheck.setGrayed(useRoundEdges);
                	}
            	}
            	isMultiSeries = false;
            	selectedType = IChart.PARETO;
            	selectedSubType = IChart.PARETO_SUBTYPE;
            	refreshCbDimension();
            	break;
            case IChart.DRAG_NODE:
            	currentCharts = AvailableChart.AVAILABLE_DRAG_NODE; 
            	chartSubType = IChart.DRAG_NODE_SUBS; 
            	icons = findIcons(IChart.DRAG_NODE);
            	isMultiSeries = true;
            	selectedType = IChart.DRAG_NODE;
            	selectedSubType = IChart.DRAG_NODE_SUB;
            	refreshCbDimension();
            	break;
            default: 
            	chartSubType = null;
            	break;
        }
        
        refreshSubtypePart(chartSubType, icons);
		
        return findChart();
	}
	
	private List<String> findIcons(int type) {
		List<String> icons = new ArrayList<String>();
		switch (type) {
        case IChart.COLUMN:
        	icons.add(Icons.SIDE_BY_SIDE_COLUMN);
        	icons.add(Icons.STACKED_COLUMN);
        	break;
        case IChart.BAR:
        	icons.add(Icons.SIDE_BY_SIDE_BAR);
        	icons.add(Icons.STACKED_BAR);
        	break;
        case IChart.LINE:
        	icons.add(Icons.OVERLAY_LINE);
        	break;
        case IChart.PIE:
        	icons.add(Icons.PIE);
        	break;
        case IChart.DOUGHNUT:
        	icons.add(Icons.DOUGHNUT);
        	break;
        case IChart.RADAR:
        	icons.add(Icons.RADAR);
        	break;
        case IChart.PARETO:
        	icons.add(Icons.PARETO);
        	break;
        case IChart.DRAG_NODE:
        	icons.add(Icons.PARETO);
        	break;
        default:
        	break;
		}
		
		return icons;
	}
	
	private void refreshSubtypePart(String[] subTypes, List<String> icons) {
		if(subTypes != null){
			if(subTypes.length > 1){
				subtype1.setImage(Activator.getDefault().getImageRegistry().get(icons.get(0)));
				subtype1.setToolTipText(subTypes[0]);
				subtype1.setVisible(true);
				subtype2.setImage(Activator.getDefault().getImageRegistry().get(icons.get(1)));
				subtype2.setToolTipText(subTypes[1]);
				subtype2.setVisible(true);
			}
			else{
				subtype1.setImage(Activator.getDefault().getImageRegistry().get(icons.get(0)));
				subtype1.setToolTipText(subTypes[0]);
				subtype1.setVisible(true);
				if(subtype2.isVisible()){
					subtype2.setVisible(false);
				}
			}
		}
	}
	
	private IChart findChart(IChart[] charts){
		if(chartName != null){
			for(IChart chart : charts){
				if(chart.getChartName().equals(chartName)){
					return chart;
				}
			}
		}
		return null;
	}
	
	private void initValues() {
		chartTypeTableViewer.setInput(IChart.CHART_TYPES);
		
		decodeXml(vanillaChart.getXml());
		
		changeTypeChart(selectedType, true);
		selectedChart = findChart(currentCharts);
		
		if(selectedChart != null){
			selectedSubType = selectedChart.getSubType();
			is3DSelected = selectedChart.is3D();
			isMultiSeries = selectedChart.isMultiSeries();
			isScrollable = selectedChart.isScrollable();
		}
		else{
			selectedChart = changeTypeChart(selectedType, false);
		}

		refreshCbDimension();
		refreshPreview();
		
		IPreferenceStore s = new ScopedPreferenceStore(new InstanceScope(), "bpm.birt.fusionmaps.ui");
		String vanillaRuntimeUrl = s.getString("bpm.birt.item.VanillaRuntime");
		
		if(vanillaRuntimeUrl != null){
			txtVanillaRuntimeUrl.setText(vanillaRuntimeUrl);
			try {
				vanillaChart.setVanillaRuntimeUrl(vanillaRuntimeUrl);
			} catch (SemanticException e) {
				e.printStackTrace();
			}
		}
		else {
			txtVanillaRuntimeUrl.setText("http://localhost:7171/VanillaRuntime");
			try {
				vanillaChart.setVanillaRuntimeUrl("http://localhost:7171/VanillaRuntime");
			} catch (SemanticException e) {
				e.printStackTrace();
			}
		}
		
		txtVanillaRuntimeUrl.addModifyListener(listener);
	}
	
	private void refreshCbDimension() {
		if(selectedType != IChart.LINE && selectedType != IChart.RADAR){
			cbDimension.setItems(IChart.DIMENSIONS);
			if(is3DSelected){
				cbDimension.select(1);
			}
			else{
				cbDimension.select(0);
			}
		}
		else{
			cbDimension.setItems(IChart.DIMENSION_TWO_D_ONLY);
			cbDimension.select(0);
		}
	}
	
	private void refreshPreview(){
		int width = previewChart.getSize().x;
		int height = previewChart.getSize().y;
		IChart chart = selectedChart;
		
//		String chartPath = "http://localhost:8080/charts/" + chart.getChartName();
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
		html.append("		    	\"myChartId\", \"" + (width - 50) + "\", \"" + (height - 50) + "\", \"0\", \"1\");");
		html.append("            chart.setXMLData(\"" + chart.getXmlExemple(useRoundEdges) + "\");");
		html.append("            chart.render(\"mapdiv\");");
		html.append("        </script>");
		html.append("     </body>");
		html.append("</html>");

		((WizardPageData)getNextPage()).setDatasFromDef(currentCharts, selectedChart, selectedSubType, is3DSelected, isScrollable, useRoundEdges);
		previewChart.setText(html.toString());
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
				if (e.element("chartname") != null){
					
					Element d = e.element("chartname");
					if(d != null){
						chartName = d.getStringValue();
					}
				}
//				if (e.element("title") != null){
//					
//					Element d = e.element("title");
//					if(d != null){
//						this.chartTitle = d.getStringValue();
//					}
//				}
				if (e.element("type") != null){
					
					Element d = e.element("type");
					if(d != null){
						this.selectedType = Integer.parseInt(d.getStringValue());
					}
				}
				if (e.element("glassstyle") != null){
					
					Element d = e.element("glassstyle");
					if(d != null){
						if(d.getStringValue().equals("1")){
							this.useRoundEdges = true;
						}
						else{
							this.useRoundEdges = false;
						}
					}
				}
//				if (e.element("width") != null){
//					
//					Element d = e.element("width");
//					if(d != null){
//						this.width = Integer.parseInt(d.getStringValue());
//					}
//				}
//				if (e.element("height") != null){
//					
//					Element d = e.element("height");
//					if(d != null){
//						this.height = Integer.parseInt(d.getStringValue());
//					}
//				}
			}catch(Throwable ex){
				ex.printStackTrace();
			}
			
		}
	}
}
