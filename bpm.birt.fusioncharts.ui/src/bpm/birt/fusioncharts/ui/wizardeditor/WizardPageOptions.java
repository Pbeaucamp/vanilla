package bpm.birt.fusioncharts.ui.wizardeditor;

import java.util.Properties;

import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import bpm.birt.fusioncharts.core.reportitem.FusionchartsItem;
import bpm.birt.fusioncharts.core.xmldata.*;
import bpm.birt.fusioncharts.ui.Activator;
import bpm.birt.fusioncharts.ui.icons.Icons;

public class WizardPageOptions extends WizardPage  {

	private FusionchartsItem vanillaChart;
	
	private FormToolkit toolkit;
	private Color white;
	
	protected WizardPageOptions(String pageName, FusionchartsItem item) {
		super(pageName);
		this.vanillaChart = item;
	}

	@Override
	public void createControl(Composite parent) {
		this.toolkit = new FormToolkit(getShell().getDisplay());
		this.white = getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE);
		
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true , true));

		ScrolledForm form = toolkit.createScrolledForm(main);
		form.getBody().setLayout(new GridLayout());
		form.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		createContent(form.getBody());
		
		setControl(main);
		
		initProperties(vanillaChart.getCustomProperties());
	}
	
	private void createContent(Composite main) {
		Section sectionBorderAndBG = toolkit.createSection(main, Section.TITLE_BAR | Section.TWISTIE);
		sectionBorderAndBG.setText("Border and Backgrounds");
		sectionBorderAndBG.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
	    buildBorderAndBackgroundsSection(sectionBorderAndBG);

	    
		Section sectionCanvas = toolkit.createSection(main, Section.TITLE_BAR | Section.TWISTIE);
		sectionCanvas.setText("Canvas");
		sectionCanvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
	    buildCanvasSection(sectionCanvas);

	    
		Section sectionTitleAxis = toolkit.createSection(main, Section.TITLE_BAR | Section.TWISTIE);
		sectionTitleAxis.setText("Title and Axis Names");
		sectionTitleAxis.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
	    buildTitleAndAxisNameSection(sectionTitleAxis);
	    

		Section sectionDataPlot = toolkit.createSection(main, Section.TITLE_BAR | Section.TWISTIE);
		sectionDataPlot.setText("Data Plot");
		sectionDataPlot.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
	    buildDataPlotSection(sectionDataPlot);
	    

		Section sectionLabels = toolkit.createSection(main, Section.TITLE_BAR | Section.TWISTIE);
		sectionLabels.setText("Labels");
		sectionLabels.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
	    buildLabelsSection(sectionLabels);
	    

		Section sectionDataValues = toolkit.createSection(main, Section.TITLE_BAR | Section.TWISTIE);
		sectionDataValues.setText("Data Values");
		sectionDataValues.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
	    buildDataValuesSection(sectionDataValues);
	    

		Section sectionPalette = toolkit.createSection(main, Section.TITLE_BAR | Section.TWISTIE);
		sectionPalette.setText("Palette");
		sectionPalette.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
	    buildPaletteSection(sectionPalette);
	    

		Section sectionTooltip = toolkit.createSection(main, Section.TITLE_BAR | Section.TWISTIE);
		sectionTooltip.setText("Tooltip");
		sectionTooltip.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
	    buildTooltipSection(sectionTooltip);
	    

		Section sectionMarginsAndPadding = toolkit.createSection(main, Section.TITLE_BAR | Section.TWISTIE);
		sectionMarginsAndPadding.setText("Margins and Paddings");
		sectionMarginsAndPadding.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
	    buildMarginsAndPaddingSection(sectionMarginsAndPadding);
	    

		Section sectionLegend = toolkit.createSection(main, Section.TITLE_BAR | Section.TWISTIE);
		sectionLegend.setText("Legend");
		sectionLegend.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
	    buildLegendSection(sectionLegend);
	    

		Section sectionNumberAndDecimals = toolkit.createSection(main, Section.TITLE_BAR | Section.TWISTIE);
		sectionNumberAndDecimals.setText("Number and Decimals");
		sectionNumberAndDecimals.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
	    buildFormatSection(sectionNumberAndDecimals);
	    
	    
		Section sectionPieAndDoughnut = toolkit.createSection(main, Section.TITLE_BAR | Section.TWISTIE);
		sectionPieAndDoughnut.setText("Pie and Doughnut");
		sectionPieAndDoughnut.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
	    buildPieAndDoughnutSection(sectionPieAndDoughnut);
	    
	    
		Section sectionStackedChart = toolkit.createSection(main, Section.TITLE_BAR | Section.TWISTIE);
		sectionStackedChart.setText("Stacked Chart");
		sectionStackedChart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
	    buildStackedChartSection(sectionStackedChart);
	}
	
	private Text bgColor, borderColor;
	private Spinner bgAlpha, borderAlpha, borderThickness;
	private Button showBorder;
	
	private void buildBorderAndBackgroundsSection(Section section) {
	    Composite composite = new Composite(section, SWT.NONE);
	    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    composite.setLayout(new GridLayout(3, false));
	    composite.setBackground(white);
	    
	    Label lblBgColor = new Label(composite, SWT.None);
	    lblBgColor.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
	    lblBgColor.setText("Define the chart background color: "); //$NON-NLS-1$
		
	    bgColor = new Text(composite, SWT.BORDER);
	    bgColor.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	    bgColor.setEditable(false);
	    bgColor.addMouseListener(new ColorListener(bgColor));
		
		Button btnPickColor = new Button(composite, SWT.PUSH);
		btnPickColor.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		btnPickColor.setText("Pick a color");
		btnPickColor.addSelectionListener(new ColorListener(bgColor));
		
		Label lblBgAlpha = new Label(composite, SWT.None);
		lblBgAlpha.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblBgAlpha.setText("Define the chart background alpha: "); //$NON-NLS-1$
		
		bgAlpha = new Spinner(composite, SWT.BORDER);
		bgAlpha.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		
		Label lblShowBorder = new Label(composite, SWT.None);
		lblShowBorder.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblShowBorder.setText("Show borders: "); //$NON-NLS-1$
		
	    showBorder = new Button(composite, SWT.CHECK);
	    showBorder.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		
		Label lblBorderColor = new Label(composite, SWT.None);
		lblBorderColor.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblBorderColor.setText("Define the border color: "); //$NON-NLS-1$
		
	    borderColor = new Text(composite, SWT.BORDER);
	    borderColor.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	    borderColor.setEditable(false);
	    borderColor.addMouseListener(new ColorListener(borderColor));
		
		Button btnPickFillColor = new Button(composite, SWT.PUSH);
		btnPickFillColor.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		btnPickFillColor.setText("Pick a color");
		btnPickFillColor.addSelectionListener(new ColorListener(borderColor));
		
		Label lblBorderAlpha = new Label(composite, SWT.None);
		lblBorderAlpha.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblBorderAlpha.setText("Define the border alpha: "); //$NON-NLS-1$
		
		borderAlpha = new Spinner(composite, SWT.BORDER);
		borderAlpha.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		
		Label lblBorderThickness = new Label(composite, SWT.None);
		lblBorderThickness.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblBorderThickness.setText("Define the border thickness: "); //$NON-NLS-1$
		
		borderThickness = new Spinner(composite, SWT.BORDER);
		borderThickness.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		
	    section.setClient(composite);
	}
	
	private Text canvasBgColor, canvasBorderColor;
	private Spinner canvasBgAlpha, canvasBorderAlpha, canvasBorderThickness;
	
	private void buildCanvasSection(Section section) {
	    Composite composite = new Composite(section, SWT.NONE);
	    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    composite.setLayout(new GridLayout(3, false));
	    composite.setBackground(white);

	    Label lblCanvasBgColor = new Label(composite, SWT.None);
	    lblCanvasBgColor.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
	    lblCanvasBgColor.setText("Define the canvas background color: "); //$NON-NLS-1$
		
	    canvasBgColor = new Text(composite, SWT.BORDER);
	    canvasBgColor.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	    canvasBgColor.setEditable(false);
	    canvasBgColor.addMouseListener(new ColorListener(canvasBgColor));
		
		Button btnPickColor = new Button(composite, SWT.PUSH);
		btnPickColor.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		btnPickColor.setText("Pick a color");
		btnPickColor.addSelectionListener(new ColorListener(canvasBgColor));
		
		Label lblCanvasBgAlpha = new Label(composite, SWT.None);
		lblCanvasBgAlpha.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblCanvasBgAlpha.setText("Define the canvas background alpha: "); //$NON-NLS-1$
		
		canvasBgAlpha = new Spinner(composite, SWT.BORDER);
		canvasBgAlpha.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		
		Label lblCanvasBorderColor = new Label(composite, SWT.None);
		lblCanvasBorderColor.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblCanvasBorderColor.setText("Define the canvas border color: "); //$NON-NLS-1$
		
	    canvasBorderColor = new Text(composite, SWT.BORDER);
	    canvasBorderColor.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	    canvasBorderColor.setEditable(false);
	    canvasBorderColor.addMouseListener(new ColorListener(canvasBorderColor));
		
		Button btnPickFillColor = new Button(composite, SWT.PUSH);
		btnPickFillColor.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		btnPickFillColor.setText("Pick a color");
		btnPickFillColor.addSelectionListener(new ColorListener(canvasBorderColor));
		
		Label lblCanvasBorderAlpha = new Label(composite, SWT.None);
		lblCanvasBorderAlpha.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblCanvasBorderAlpha.setText("Define the canvas border alpha: "); //$NON-NLS-1$
		
		canvasBorderAlpha = new Spinner(composite, SWT.BORDER);
		canvasBorderAlpha.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		
		Label lblCanvasBorderThickness = new Label(composite, SWT.None);
		lblCanvasBorderThickness.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblCanvasBorderThickness.setText("Define the canvas border thickness: "); //$NON-NLS-1$
		
		canvasBorderThickness = new Spinner(composite, SWT.BORDER);
		canvasBorderThickness.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		
	    section.setClient(composite);
	}
	
	private Text subcaption, yAxisName, xAxisName, fontColor;
	private Button rotateYAxisName;
	private Spinner fontSize;
	
	private void buildTitleAndAxisNameSection(Section section) {
	    Composite composite = new Composite(section, SWT.NONE);
	    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    composite.setLayout(new GridLayout(3, false));
	    composite.setBackground(white);
	    
	    Label lblFontSize = new Label(composite, SWT.None);
		lblFontSize.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblFontSize.setText("Define the font size: "); //$NON-NLS-1$
		
		fontSize = new Spinner(composite, SWT.BORDER);
		fontSize.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		
		Label lblFontColor = new Label(composite, SWT.None);
		lblFontColor.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblFontColor.setText("Define the font color: "); //$NON-NLS-1$
		
		fontColor = new Text(composite, SWT.BORDER);
		fontColor.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		fontColor.setEditable(false);
		fontColor.addMouseListener(new ColorListener(fontColor));
		
		Button btnPickColor = new Button(composite, SWT.PUSH);
		btnPickColor.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		btnPickColor.setText("Pick a color");
		btnPickColor.addSelectionListener(new ColorListener(fontColor));
		
		Label lblSubcaption = new Label(composite, SWT.None);
		lblSubcaption.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblSubcaption.setText("Chart Subcaption: "); //$NON-NLS-1$
		
		subcaption = new Text(composite, SWT.BORDER);
		subcaption.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));

		Label lblXAxisName = new Label(composite, SWT.None);
		lblXAxisName.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblXAxisName.setText("X Axis Name: "); //$NON-NLS-1$
		
		xAxisName = new Text(composite, SWT.BORDER);
		xAxisName.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));

		Label lblYAxisName = new Label(composite, SWT.None);
		lblYAxisName.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblYAxisName.setText("Y Axis Name: "); //$NON-NLS-1$
		
		yAxisName = new Text(composite, SWT.BORDER);
		yAxisName.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));

		Label lblRotateYAxisName = new Label(composite, SWT.None);
		lblRotateYAxisName.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblRotateYAxisName.setText("Rotate Y Axis Name: "); //$NON-NLS-1$

	    rotateYAxisName = new Button(composite, SWT.CHECK);
	    rotateYAxisName.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		
	    section.setClient(composite);
	}
	
	private Text plotGradientColor;
	
	private void buildDataPlotSection(Section section) {
	    Composite composite = new Composite(section, SWT.NONE);
	    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    composite.setLayout(new GridLayout(3, false));
	    composite.setBackground(white);
	    
	    Label lblPlotGradientColor = new Label(composite, SWT.None);
	    lblPlotGradientColor.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
	    lblPlotGradientColor.setText("Data plot grandient color: "); //$NON-NLS-1$
		
		plotGradientColor = new Text(composite, SWT.BORDER);
		plotGradientColor.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		plotGradientColor.setEditable(false);
		plotGradientColor.addMouseListener(new ColorListener(plotGradientColor));
		
		Button btnPickColor = new Button(composite, SWT.PUSH);
		btnPickColor.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		btnPickColor.setText("Pick a color");
		btnPickColor.addSelectionListener(new ColorListener(plotGradientColor));
	    
	    section.setClient(composite);
	}
	
	private Button showLabels, displayAuto, displayWrap, displayRotate, displayStagger;
	
	private void buildLabelsSection(Section section) {
	    Composite composite = new Composite(section, SWT.NONE);
	    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    composite.setLayout(new GridLayout(5, false));
	    composite.setBackground(white);
	    
	    Label lblShowLabels = new Label(composite, SWT.None);
	    lblShowLabels.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
	    lblShowLabels.setText("Show labels: "); //$NON-NLS-1$
		
	    showLabels = new Button(composite, SWT.CHECK);
	    showLabels.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 4, 1));
		
	    Label lblDisplayLabels = new Label(composite, SWT.None);
	    lblDisplayLabels.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
	    lblDisplayLabels.setText("Label display: "); //$NON-NLS-1$
		
		displayAuto = new Button(composite, SWT.RADIO);
		displayAuto.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		displayAuto.setText("AUTO");
		
		displayWrap = new Button(composite, SWT.RADIO);
		displayWrap.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		displayWrap.setText("WRAP");
		
		displayRotate = new Button(composite, SWT.RADIO);
		displayRotate.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		displayRotate.setText("ROTATE");
		
		displayStagger = new Button(composite, SWT.RADIO);
		displayStagger.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		displayStagger.setText("STAGGER");
		
	    section.setClient(composite);
	}
	
	private Button showValues, rotateValues, placeValuesInside;
	private Text valuesFontColor;
	private Spinner valuesFontSize;
	
	private void buildDataValuesSection(Section section) {
	    Composite composite = new Composite(section, SWT.NONE);
	    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    composite.setLayout(new GridLayout(3, false));
	    composite.setBackground(white);
	    
	    Label lblShowValues = new Label(composite, SWT.None);
	    lblShowValues.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
	    lblShowValues.setText("Show values: "); //$NON-NLS-1$
		
	    showValues = new Button(composite, SWT.CHECK);
	    showValues.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		
		Label lblRotateValues = new Label(composite, SWT.None);
		lblRotateValues.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblRotateValues.setText("Rotate the values: "); //$NON-NLS-1$
		
		rotateValues = new Button(composite, SWT.CHECK);
		rotateValues.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		
		Label lblPlaceValuesInside = new Label(composite, SWT.None);
		lblPlaceValuesInside.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblPlaceValuesInside.setText("Place the values inside: "); //$NON-NLS-1$
		
		placeValuesInside = new Button(composite, SWT.CHECK);
		placeValuesInside.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
	    
		Label lblBaseFontSize = new Label(composite, SWT.None);
		lblBaseFontSize.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblBaseFontSize.setText("Define the values font size: "); //$NON-NLS-1$
		
		valuesFontSize = new Spinner(composite, SWT.BORDER);
		valuesFontSize.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		
		Label lblBaseFontColor = new Label(composite, SWT.None);
		lblBaseFontColor.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblBaseFontColor.setText("Define the values font color: "); //$NON-NLS-1$
		
		valuesFontColor = new Text(composite, SWT.BORDER);
		valuesFontColor.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		valuesFontColor.setEditable(false);
		valuesFontColor.addMouseListener(new ColorListener(valuesFontColor));
		
		Button btnPickColor = new Button(composite, SWT.PUSH);
		btnPickColor.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		btnPickColor.setText("Pick a color");
		btnPickColor.addSelectionListener(new ColorListener(valuesFontColor));
		
	    section.setClient(composite);
	}
	
	private Button paletteUn, paletteDeux, paletteTrois, paletteQuatre, paletteCinq;
	
	private void buildPaletteSection(Section section) {
	    Composite composite = new Composite(section, SWT.NONE);
	    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    composite.setLayout(new GridLayout(6, false));
	    composite.setBackground(white);
	    
	    Label lblPalette = new Label(composite, SWT.None);
	    lblPalette.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
	    lblPalette.setText("Label palette: "); //$NON-NLS-1$
		
	    paletteUn = new Button(composite, SWT.RADIO);
	    paletteUn.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
	    paletteUn.setText("1");
		
	    paletteDeux = new Button(composite, SWT.RADIO);
	    paletteDeux.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	    paletteDeux.setText("2");
		
	    paletteTrois = new Button(composite, SWT.RADIO);
	    paletteTrois.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	    paletteTrois.setText("3");
		
	    paletteQuatre = new Button(composite, SWT.RADIO);
	    paletteQuatre.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	    paletteQuatre.setText("4");
		
	    paletteCinq = new Button(composite, SWT.RADIO);
	    paletteCinq.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	    paletteCinq.setText("5");
		
	    section.setClient(composite);
	}
	
	private Button showPercentValues, showPercentInTooltip;
	private Spinner slicingDistance;
	
	private void buildPieAndDoughnutSection(Section section) {
	    Composite composite = new Composite(section, SWT.NONE);
	    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    composite.setLayout(new GridLayout(2, false));
	    composite.setBackground(white);

	    Label lblShowPercentValues = new Label(composite, SWT.None);
	    lblShowPercentValues.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
	    lblShowPercentValues.setText("Show percent values: "); //$NON-NLS-1$
		
	    showPercentValues = new Button(composite, SWT.CHECK);
	    showPercentValues.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

	    Label lblShowPercentValuesInTooltip = new Label(composite, SWT.None);
	    lblShowPercentValuesInTooltip.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
	    lblShowPercentValuesInTooltip.setText("Show percent values in tooltip: "); //$NON-NLS-1$
		
	    showPercentInTooltip = new Button(composite, SWT.CHECK);
	    showPercentInTooltip.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	    
	    Label lblSlicingDistance = new Label(composite, SWT.None);
	    lblSlicingDistance.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
	    lblSlicingDistance.setText("Slicing distance: "); //$NON-NLS-1$
		
	    slicingDistance = new Spinner(composite, SWT.BORDER);
	    slicingDistance.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	    
	    section.setClient(composite);
	}
	
	private Button showSum;
	
	private void buildStackedChartSection(Section section) {
	    Composite composite = new Composite(section, SWT.NONE);
	    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    composite.setLayout(new GridLayout(2, false));
	    composite.setBackground(white);

	    Label lblShowSum = new Label(composite, SWT.None);
	    lblShowSum.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
	    lblShowSum.setText("Show sum in stacked chart: "); //$NON-NLS-1$
		
	    showSum = new Button(composite, SWT.CHECK);
	    showSum.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	    
	    section.setClient(composite);
	}
	
	private Text prefix, suffix, decimalSeparator, thousandSeparator;
	private Spinner numberDecimals, forceNumberDecimals;
	private Button formatNumberScale;
	
	private void buildFormatSection(Section section) {
	    Composite composite = new Composite(section, SWT.NONE);
	    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    composite.setLayout(new GridLayout(4, false));
	    composite.setBackground(white);

	    buildImageHelp(composite, FusionChartProperties.FORMAT_NUMBER_SCALE);
	    
	    Label lblFormatNumberScale = new Label(composite, SWT.None);
	    lblFormatNumberScale.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
	    lblFormatNumberScale.setText("Format number: "); //$NON-NLS-1$
		
		formatNumberScale = new Button(composite, SWT.CHECK);
		formatNumberScale.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	    
	    Label lblPrefix = new Label(composite, SWT.None);
	    lblPrefix.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false, 2, 1));
	    lblPrefix.setText("Set a prefix for the values: "); //$NON-NLS-1$
		
	    prefix = new Text(composite, SWT.BORDER);
	    prefix.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
	    
	    Label lblSuffix = new Label(composite, SWT.None);
	    lblSuffix.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false, 2, 1));
	    lblSuffix.setText("Set a suffix for the values: "); //$NON-NLS-1$
		
	    suffix = new Text(composite, SWT.BORDER);
	    suffix.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
	    
	    Label lblDecimalSeparator = new Label(composite, SWT.None);
	    lblDecimalSeparator.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false, 2, 1));
	    lblDecimalSeparator.setText("Define the decimal separator: "); //$NON-NLS-1$
		
	    decimalSeparator = new Text(composite, SWT.BORDER);
	    decimalSeparator.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
	    
	    Label lblThousandSeparator = new Label(composite, SWT.None);
	    lblThousandSeparator.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false, 2, 1));
	    lblThousandSeparator.setText("Define the thousand separator: "); //$NON-NLS-1$
		
	    thousandSeparator = new Text(composite, SWT.BORDER);
	    thousandSeparator.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));

	    Label lblNumberDecimal = new Label(composite, SWT.None);
	    lblNumberDecimal.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false, 2, 1));
	    lblNumberDecimal.setText("Define the number of decimals: "); //$NON-NLS-1$
		
	    numberDecimals = new Spinner(composite, SWT.BORDER);
	    numberDecimals.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));

	    Label lblForceDecimals = new Label(composite, SWT.None);
	    lblForceDecimals.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false, 2, 1));
	    lblForceDecimals.setText("Force a number of decimals: "); //$NON-NLS-1$
		
	    forceNumberDecimals = new Spinner(composite, SWT.BORDER);
	    forceNumberDecimals.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));

	    section.setClient(composite);
	}
	
	private Text tooltipBgColor, tooltipBorderColor;
	private Button showTooltip, showTooltipShadow;
	
	private void buildTooltipSection(Section section) {
	    Composite composite = new Composite(section, SWT.NONE);
	    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    composite.setLayout(new GridLayout(3, false));
	    composite.setBackground(white);

	    Label lblShowToolTip = new Label(composite, SWT.None);
	    lblShowToolTip.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
	    lblShowToolTip.setText("Show tooltips: "); //$NON-NLS-1$
		
		showTooltip = new Button(composite, SWT.CHECK);
		showTooltip.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));

	    Label lblShowTooltipShadow = new Label(composite, SWT.None);
	    lblShowTooltipShadow.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
	    lblShowTooltipShadow.setText("Show tooltips shadow: "); //$NON-NLS-1$
		
		showTooltipShadow = new Button(composite, SWT.CHECK);
		showTooltipShadow.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
	    
		Label lblTooltipBgColor = new Label(composite, SWT.None);
		lblTooltipBgColor.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblTooltipBgColor.setText("Tooltip background color: "); //$NON-NLS-1$
		
	    tooltipBgColor = new Text(composite, SWT.BORDER);
	    tooltipBgColor.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	    tooltipBgColor.setEditable(false);
	    tooltipBgColor.addMouseListener(new ColorListener(tooltipBgColor));
		
		Button btnPickHoverColor = new Button(composite, SWT.PUSH);
		btnPickHoverColor.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		btnPickHoverColor.setText("Pick a color");
		btnPickHoverColor.addSelectionListener(new ColorListener(tooltipBgColor));
	    
		Label lblTooltipBorderColor = new Label(composite, SWT.None);
		lblTooltipBorderColor.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblTooltipBorderColor.setText("Tooltip border color: "); //$NON-NLS-1$
		
		tooltipBorderColor = new Text(composite, SWT.BORDER);
		tooltipBorderColor.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		tooltipBorderColor.setEditable(false);
		tooltipBorderColor.addMouseListener(new ColorListener(tooltipBorderColor));
		
		Button btnPickBorderColor = new Button(composite, SWT.PUSH);
		btnPickBorderColor.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		btnPickBorderColor.setText("Pick a color");
		btnPickBorderColor.addSelectionListener(new ColorListener(tooltipBorderColor));
		
	    section.setClient(composite);
	}
	
	private Text chartLeftMargin, chartRightMargin, chartTopMargin, chartBottomMargin;
	private Text captionPadding, xAxisNamePadding, yAxisNamePadding, yAxisValuesPadding;
	private Text labelPadding, valuePadding;
	
	private void buildMarginsAndPaddingSection(Section section) {
	    Composite composite = new Composite(section, SWT.NONE);
	    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    composite.setLayout(new GridLayout(2, false));
	    composite.setBackground(white);

		Label lblChartLeftMargin = new Label(composite, SWT.None);
		lblChartLeftMargin.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblChartLeftMargin.setText("Chart left margin: "); //$NON-NLS-1$
		
		chartLeftMargin = new Text(composite, SWT.BORDER);
		chartLeftMargin.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		Label lblChartRightMargin = new Label(composite, SWT.None);
		lblChartRightMargin.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblChartRightMargin.setText("Chart right margin: "); //$NON-NLS-1$
		
		chartRightMargin = new Text(composite, SWT.BORDER);
		chartRightMargin.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		Label lblChartTopMargin = new Label(composite, SWT.None);
		lblChartTopMargin.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblChartTopMargin.setText("Chart top margin: "); //$NON-NLS-1$
		
		chartTopMargin = new Text(composite, SWT.BORDER);
		chartTopMargin.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		Label lblChartBottomMargin = new Label(composite, SWT.None);
		lblChartBottomMargin.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblChartBottomMargin.setText("Chart bottom margin: "); //$NON-NLS-1$
		
		chartBottomMargin = new Text(composite, SWT.BORDER);
		chartBottomMargin.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		Label lblCaptionPadding = new Label(composite, SWT.None);
		lblCaptionPadding.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblCaptionPadding.setText("Caption padding: "); //$NON-NLS-1$
		
		captionPadding = new Text(composite, SWT.BORDER);
		captionPadding.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		Label lblXAxisNamePadding = new Label(composite, SWT.None);
		lblXAxisNamePadding.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblXAxisNamePadding.setText("X Axis name padding: "); //$NON-NLS-1$
		
		xAxisNamePadding = new Text(composite, SWT.BORDER);
		xAxisNamePadding.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		Label lblYAxisNamePadding = new Label(composite, SWT.None);
		lblYAxisNamePadding.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblYAxisNamePadding.setText("Y Axis name padding: "); //$NON-NLS-1$
		
		yAxisNamePadding = new Text(composite, SWT.BORDER);
		yAxisNamePadding.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		Label lblYAxisValuesPadding = new Label(composite, SWT.None);
		lblYAxisValuesPadding.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblYAxisValuesPadding.setText("Y Axis values padding: "); //$NON-NLS-1$
		
		yAxisValuesPadding = new Text(composite, SWT.BORDER);
		yAxisValuesPadding.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		Label lblLabelPadding = new Label(composite, SWT.None);
		lblLabelPadding.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblLabelPadding.setText("Label padding: "); //$NON-NLS-1$
		
		labelPadding = new Text(composite, SWT.BORDER);
		labelPadding.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		Label lblValuePadding = new Label(composite, SWT.None);
		lblValuePadding.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblValuePadding.setText("Value padding: "); //$NON-NLS-1$
		
		valuePadding = new Text(composite, SWT.BORDER);
		valuePadding.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	    
	    section.setClient(composite);
	}
	
	private Button showLegend, legendRight, legendBottom;
	
	private void buildLegendSection(Section section) {
	    Composite composite = new Composite(section, SWT.NONE);
	    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    composite.setLayout(new GridLayout(3, false));
	    composite.setBackground(white);
	    
	    Label lblShowLegend = new Label(composite, SWT.None);
	    lblShowLegend.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
	    lblShowLegend.setText("Show legend: "); //$NON-NLS-1$
		
		showLegend = new Button(composite, SWT.CHECK);
		showLegend.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));

		Label lblLegendPosition = new Label(composite, SWT.None);
		lblLegendPosition.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblLegendPosition.setText("Legend position: "); //$NON-NLS-1$
		
		legendRight = new Button(composite, SWT.RADIO);
		legendRight.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		legendRight.setText("Right");
		
		legendBottom = new Button(composite, SWT.RADIO);
		legendBottom.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		legendBottom.setText("Bottom");
		
		section.setClient(composite);
	}
	
	private void buildImageHelp(Composite parent, String property){
		ImageRegistry reg = Activator.getDefault().getImageRegistry();
		
		Label lblHelp = new Label(parent, SWT.NONE);
		lblHelp.setImage(reg.get(Icons.HELP));
		
//		if(property.equals(FusionMapProperties.SHOW_SHADOW)){
//			lblHelp.setToolTipText("Whether to drop a shadow effect for the map?");
//		}
//		else if(property.equals(FusionMapProperties.SHOW_BEVEL)){
//			lblHelp.setToolTipText("Whether to render a 3D bevel effect to the map?");
//		}
		if(property.equals(FusionChartProperties.FORMAT_NUMBER_SCALE)){
			lblHelp.setToolTipText("Whether to add K (thousands) and M (millions) to a number after " +
					"truncating and rounding it?");
		}
//		else if(property.equals(FusionMapProperties.DECIMALS)){
//			lblHelp.setToolTipText("Number of decimal places to which all numbers on the map would be rounded to.");
//		}
	}
	
	public void initProperties(Properties props){
		
		if(props == null || props.isEmpty()){
			props = FusionChartProperties.buildDefaultProperties();
		}
		
		bgColor.setText(props.get(FusionChartProperties.BG_COLOR) != null ? 
				props.get(FusionChartProperties.BG_COLOR).toString() : "");
		bgAlpha.setSelection(props.get(FusionChartProperties.BG_ALPHA) != null
				&& !props.get(FusionChartProperties.BG_ALPHA).toString().isEmpty() ? 
				Integer.parseInt(props.get(FusionChartProperties.BG_ALPHA).toString()) : 0);
		showBorder.setSelection(props.get(FusionChartProperties.SHOW_BORDER) != null 
				&& props.get(FusionChartProperties.SHOW_BORDER).equals("1"));
		borderColor.setText(props.get(FusionChartProperties.BORDER_COLOR) != null ? 
				props.get(FusionChartProperties.BORDER_COLOR).toString() : "");
		borderAlpha.setSelection(props.get(FusionChartProperties.BORDER_ALPHA) != null
				&& !props.get(FusionChartProperties.BORDER_ALPHA).toString().isEmpty() ? 
				Integer.parseInt(props.get(FusionChartProperties.BORDER_ALPHA).toString()) : 0);
		borderThickness.setSelection(props.get(FusionChartProperties.BORDER_THICKNESS) != null
				&& !props.get(FusionChartProperties.BORDER_THICKNESS).toString().isEmpty() ? 
				Integer.parseInt(props.get(FusionChartProperties.BORDER_THICKNESS).toString()) : 0);

		canvasBgColor.setText(props.get(FusionChartProperties.CANVAS_BG_COLOR) != null ? 
				props.get(FusionChartProperties.CANVAS_BG_COLOR).toString() : "");
		canvasBgAlpha.setSelection(props.get(FusionChartProperties.CANVAS_BG_ALPHA) != null
				&& !props.get(FusionChartProperties.CANVAS_BG_ALPHA).toString().isEmpty() ? 
				Integer.parseInt(props.get(FusionChartProperties.CANVAS_BG_ALPHA).toString()) : 0);
		canvasBorderColor.setText(props.get(FusionChartProperties.CANVAS_BORDER_COLOR) != null ? 
				props.get(FusionChartProperties.CANVAS_BORDER_COLOR).toString() : "");
	    canvasBorderAlpha.setSelection(props.get(FusionChartProperties.CANVAS_BORDER_ALPHA) != null
				&& !props.get(FusionChartProperties.CANVAS_BORDER_ALPHA).toString().isEmpty() ? 
				Integer.parseInt(props.get(FusionChartProperties.CANVAS_BORDER_ALPHA).toString()) : 0);
	    canvasBorderThickness.setSelection(props.get(FusionChartProperties.CANVAS_BORDER_THICKNESS) != null
				&& !props.get(FusionChartProperties.CANVAS_BORDER_THICKNESS).toString().isEmpty() ? 
				Integer.parseInt(props.get(FusionChartProperties.CANVAS_BORDER_THICKNESS).toString()) : 0);

	    fontSize.setSelection(props.get(FusionChartProperties.CUSTOM_CAPTION_SIZE) != null
				&& !props.get(FusionChartProperties.CUSTOM_CAPTION_SIZE).toString().isEmpty() ? 
				Integer.parseInt(props.get(FusionChartProperties.CUSTOM_CAPTION_SIZE).toString()) : 0);
	    fontColor.setText(props.get(FusionChartProperties.CUSTOM_CAPTION_COLOR) != null ? 
				props.get(FusionChartProperties.CUSTOM_CAPTION_COLOR).toString() : "");
	    subcaption.setText(props.get(FusionChartProperties.SUBCAPTION) != null ? 
				props.get(FusionChartProperties.SUBCAPTION).toString() : "");
	    xAxisName.setText(props.get(FusionChartProperties.X_AXIS_NAME) != null ? 
				props.get(FusionChartProperties.X_AXIS_NAME).toString() : "");
	    yAxisName.setText(props.get(FusionChartProperties.Y_AXIS_NAME) != null ? 
				props.get(FusionChartProperties.Y_AXIS_NAME).toString() : "");
	    rotateYAxisName.setSelection(props.get(FusionChartProperties.ROTATE_Y_AXIS_NAME) != null 
				&& props.get(FusionChartProperties.ROTATE_Y_AXIS_NAME).equals("1"));

	    plotGradientColor.setText(props.get(FusionChartProperties.PLOT_GRADIENT_COLOR) != null ? 
				props.get(FusionChartProperties.PLOT_GRADIENT_COLOR).toString() : "");
	    
	    showLabels.setSelection(props.get(FusionChartProperties.SHOW_LABELS) != null 
				&& props.get(FusionChartProperties.SHOW_LABELS).equals("1"));
	    if(props.get(FusionChartProperties.LABEL_DISPLAY) != null 
				&& props.get(FusionChartProperties.LABEL_DISPLAY).equals("AUTO")){
	    	displayAuto.setSelection(true);
		}
	    else if(props.get(FusionChartProperties.LABEL_DISPLAY) != null 
				&& props.get(FusionChartProperties.LABEL_DISPLAY).equals("WRAP")){
	    	displayWrap.setSelection(true);
		}
	    else if(props.get(FusionChartProperties.LABEL_DISPLAY) != null 
				&& props.get(FusionChartProperties.LABEL_DISPLAY).equals("Rotate")){
	    	displayRotate.setSelection(true);
		}
		else {
			displayStagger.setSelection(true);
		}

	    showValues.setSelection(props.get(FusionChartProperties.SHOW_VALUES) != null 
				&& props.get(FusionChartProperties.SHOW_VALUES).equals("1"));
	    rotateValues.setSelection(props.get(FusionChartProperties.ROTATE_VALUES) != null 
				&& props.get(FusionChartProperties.ROTATE_VALUES).equals("1"));
	    placeValuesInside.setSelection(props.get(FusionChartProperties.PLACE_VALUES_INSIDE) != null 
				&& props.get(FusionChartProperties.PLACE_VALUES_INSIDE).equals("1"));
	    valuesFontSize.setSelection(props.get(FusionChartProperties.BASE_FONT_SIZE) != null
				&& !props.get(FusionChartProperties.BASE_FONT_SIZE).toString().isEmpty() ? 
				Integer.parseInt(props.get(FusionChartProperties.BASE_FONT_SIZE).toString()) : 0);
	    valuesFontColor.setText(props.get(FusionChartProperties.BASE_FONT_COLOR) != null ? 
				props.get(FusionChartProperties.BASE_FONT_COLOR).toString() : "");

	    if(props.get(FusionChartProperties.PALETTE) != null 
				&& props.get(FusionChartProperties.PALETTE).equals("1")){
	    	paletteUn.setSelection(true);
		}
	    else if(props.get(FusionChartProperties.PALETTE) != null 
				&& props.get(FusionChartProperties.PALETTE).equals("2")){
	    	paletteDeux.setSelection(true);
		}
	    else if(props.get(FusionChartProperties.PALETTE) != null 
				&& props.get(FusionChartProperties.PALETTE).equals("3")){
	    	paletteTrois.setSelection(true);
		}
	    else if(props.get(FusionChartProperties.PALETTE) != null 
				&& props.get(FusionChartProperties.PALETTE).equals("4")){
	    	paletteQuatre.setSelection(true);
		}
		else {
			paletteCinq.setSelection(true);
		}

	    showTooltip.setSelection(props.get(FusionChartProperties.SHOW_TOOLTIP) != null 
				&& props.get(FusionChartProperties.SHOW_TOOLTIP).equals("1"));
	    showTooltipShadow.setSelection(props.get(FusionChartProperties.SHOW_TOOLTIP_SHADOW) != null 
				&& props.get(FusionChartProperties.SHOW_TOOLTIP_SHADOW).equals("1"));
	    tooltipBgColor.setText(props.get(FusionChartProperties.TOOLTIP_BG_COLOR) != null ? 
				props.get(FusionChartProperties.TOOLTIP_BG_COLOR).toString() : "");
	    tooltipBorderColor.setText(props.get(FusionChartProperties.TOOLTIP_BORDER_COLOR) != null ? 
				props.get(FusionChartProperties.TOOLTIP_BORDER_COLOR).toString() : "");

	    chartLeftMargin.setText(props.get(FusionChartProperties.CHART_LEFT_MARGIN) != null ? 
				props.get(FusionChartProperties.CHART_LEFT_MARGIN).toString() : "");
	    chartRightMargin.setText(props.get(FusionChartProperties.CHART_RIGHT_MARGIN) != null ? 
				props.get(FusionChartProperties.CHART_RIGHT_MARGIN).toString() : "");
	    chartTopMargin.setText(props.get(FusionChartProperties.CHART_TOP_MARGIN) != null ? 
				props.get(FusionChartProperties.CHART_TOP_MARGIN).toString() : "");
	    chartBottomMargin.setText(props.get(FusionChartProperties.CHART_BOTTOM_MARGIN) != null ? 
				props.get(FusionChartProperties.CHART_BOTTOM_MARGIN).toString() : "");
	    captionPadding.setText(props.get(FusionChartProperties.CAPTION_PADDING) != null ? 
				props.get(FusionChartProperties.CAPTION_PADDING).toString() : "");
	    xAxisNamePadding.setText(props.get(FusionChartProperties.X_AXIS_NAME_PADDING) != null ? 
				props.get(FusionChartProperties.X_AXIS_NAME_PADDING).toString() : "");
	    yAxisNamePadding.setText(props.get(FusionChartProperties.Y_AXIS_NAME_PADDING) != null ? 
				props.get(FusionChartProperties.Y_AXIS_NAME_PADDING).toString() : "");
	    yAxisValuesPadding.setText(props.get(FusionChartProperties.Y_AXIS_VALUES_PADDING) != null ? 
				props.get(FusionChartProperties.Y_AXIS_VALUES_PADDING).toString() : "");
	    labelPadding.setText(props.get(FusionChartProperties.LABEL_PADDING) != null ? 
				props.get(FusionChartProperties.LABEL_PADDING).toString() : "");
	    valuePadding.setText(props.get(FusionChartProperties.VALUE_PADDING) != null ? 
				props.get(FusionChartProperties.VALUE_PADDING).toString() : "");
		
	    showLegend.setSelection(props.get(FusionChartProperties.SHOW_LEGEND) != null 
				&& props.get(FusionChartProperties.SHOW_LEGEND).equals("1"));
	    if(props.get(FusionChartProperties.LEGEND_POSITION) != null 
				&& props.get(FusionChartProperties.LEGEND_POSITION).equals("RIGHT")){
			legendRight.setSelection(true);
		}
		else {
			legendBottom.setSelection(true);
		}

	    formatNumberScale.setSelection(props.get(FusionChartProperties.SHOW_LEGEND) != null 
				&& props.get(FusionChartProperties.SHOW_LEGEND).equals("1"));
	    prefix.setText(props.get(FusionChartProperties.NUMBER_PREFIX) != null ? 
				props.get(FusionChartProperties.NUMBER_PREFIX).toString() : "");
	    suffix.setText(props.get(FusionChartProperties.NUMBER_SUFFIX) != null ? 
				props.get(FusionChartProperties.NUMBER_SUFFIX).toString() : "");
	    decimalSeparator.setText(props.get(FusionChartProperties.DECIMAL_SEPARATOR) != null ? 
				props.get(FusionChartProperties.DECIMAL_SEPARATOR).toString() : "");
		thousandSeparator.setText(props.get(FusionChartProperties.THOUSAND_SEPARATOR) != null ? 
				props.get(FusionChartProperties.THOUSAND_SEPARATOR).toString() : "");
		numberDecimals.setSelection(props.get(FusionChartProperties.DECIMALS) != null
				&& !props.get(FusionChartProperties.DECIMALS).toString().isEmpty() ? 
				Integer.parseInt(props.get(FusionChartProperties.DECIMALS).toString()) : 0);
		forceNumberDecimals.setSelection(props.get(FusionChartProperties.FORCE_DECIMALS) != null
				&& !props.get(FusionChartProperties.FORCE_DECIMALS).toString().isEmpty() ? 
				Integer.parseInt(props.get(FusionChartProperties.FORCE_DECIMALS).toString()) : 0);

		showPercentValues.setSelection(props.get(FusionChartProperties.SHOW_PERCENT_VALUES) != null 
				&& props.get(FusionChartProperties.SHOW_PERCENT_VALUES).equals("1"));
	    showPercentInTooltip.setSelection(props.get(FusionChartProperties.SHOW_PERCENT_IN_TOOLTIP) != null 
				&& props.get(FusionChartProperties.SHOW_PERCENT_IN_TOOLTIP).equals("1"));
	    slicingDistance.setSelection(props.get(FusionChartProperties.SLICING_DISTANCE) != null
				&& !props.get(FusionChartProperties.SLICING_DISTANCE).toString().isEmpty() ? 
				Integer.parseInt(props.get(FusionChartProperties.SLICING_DISTANCE).toString()) : 0);

	    showSum.setSelection(props.get(FusionChartProperties.SHOW_SUM) != null 
				&& props.get(FusionChartProperties.SHOW_SUM).equals("1"));
	}
	
	public void buildProperties(){
		Properties props = new Properties();

		props.put(FusionChartProperties.BG_COLOR, bgColor.getText());
		props.put(FusionChartProperties.BG_ALPHA, bgAlpha.getText());
		props.put(FusionChartProperties.SHOW_BORDER, showBorder.getSelection() ? 1 : 0);
		props.put(FusionChartProperties.BORDER_COLOR, borderColor.getText());
		props.put(FusionChartProperties.BORDER_ALPHA, borderAlpha.getText());
		props.put(FusionChartProperties.BORDER_THICKNESS, borderThickness.getText());

	    props.put(FusionChartProperties.CANVAS_BG_COLOR, canvasBgColor.getText());
		props.put(FusionChartProperties.CANVAS_BG_ALPHA, canvasBgAlpha.getText());
		props.put(FusionChartProperties.CANVAS_BORDER_COLOR, canvasBorderColor.getText());
		props.put(FusionChartProperties.CANVAS_BORDER_ALPHA, canvasBorderAlpha.getText());
	    props.put(FusionChartProperties.CANVAS_BORDER_THICKNESS, canvasBorderThickness.getText());

	    props.put(FusionChartProperties.CUSTOM_CAPTION_SIZE, fontSize.getText());
	    props.put(FusionChartProperties.CUSTOM_CAPTION_COLOR, fontColor.getText());
	    props.put(FusionChartProperties.SUBCAPTION, subcaption.getText());
	    props.put(FusionChartProperties.X_AXIS_NAME, xAxisName.getText());
	    props.put(FusionChartProperties.Y_AXIS_NAME, yAxisName.getText());
	    props.put(FusionChartProperties.ROTATE_Y_AXIS_NAME, rotateYAxisName.getSelection() ? 1 : 0);

	    
	    props.put(FusionChartProperties.PLOT_GRADIENT_COLOR, plotGradientColor.getText());
	    
	    props.put(FusionChartProperties.SHOW_LABELS, showLabels.getSelection() ? 1 : 0);
	    if(displayAuto.getSelection()){
	    	props.put(FusionChartProperties.LABEL_DISPLAY, "AUTO");
		}
	    else if(displayWrap.getSelection()){
	    	props.put(FusionChartProperties.LABEL_DISPLAY, "WRAP");
		}
	    else if(displayRotate.getSelection()){
	    	props.put(FusionChartProperties.LABEL_DISPLAY, "Rotate");
		}
	    else {
	    	props.put(FusionChartProperties.LABEL_DISPLAY, "Stagger");
		}

	    props.put(FusionChartProperties.SHOW_VALUES, showValues.getSelection() ? 1 : 0);
	    props.put(FusionChartProperties.ROTATE_VALUES, rotateValues.getSelection() ? 1 : 0);
	    props.put(FusionChartProperties.PLACE_VALUES_INSIDE, placeValuesInside.getSelection() ? 1 : 0);
	    props.put(FusionChartProperties.BASE_FONT_SIZE, valuesFontSize.getText());
	    props.put(FusionChartProperties.BASE_FONT_COLOR, valuesFontColor.getText());
	    
	    if(paletteUn.getSelection()){
	    	props.put(FusionChartProperties.PALETTE, "1");
		}
	    else if(paletteDeux.getSelection()){
	    	props.put(FusionChartProperties.PALETTE, "2");
		}
	    else if(paletteTrois.getSelection()){
	    	props.put(FusionChartProperties.PALETTE, "3");
		}
	    else if(paletteQuatre.getSelection()){
	    	props.put(FusionChartProperties.PALETTE, "4");
		}
		else {
	    	props.put(FusionChartProperties.PALETTE, "5");
		}

	    props.put(FusionChartProperties.SHOW_TOOLTIP, showTooltip.getSelection() ? 1 : 0);
	    props.put(FusionChartProperties.SHOW_TOOLTIP_SHADOW, showTooltipShadow.getSelection() ? 1 : 0);
	    props.put(FusionChartProperties.TOOLTIP_BG_COLOR, tooltipBgColor.getText());
	    props.put(FusionChartProperties.TOOLTIP_BORDER_COLOR, tooltipBorderColor.getText());

	    props.put(FusionChartProperties.CHART_LEFT_MARGIN, chartLeftMargin.getText());
	    props.put(FusionChartProperties.CHART_RIGHT_MARGIN, chartRightMargin.getText());
	    props.put(FusionChartProperties.CHART_TOP_MARGIN, chartTopMargin.getText());
	    props.put(FusionChartProperties.CHART_BOTTOM_MARGIN, chartBottomMargin.getText());
	    props.put(FusionChartProperties.CAPTION_PADDING, captionPadding.getText());
	    props.put(FusionChartProperties.X_AXIS_NAME_PADDING, xAxisNamePadding.getText());
	    props.put(FusionChartProperties.Y_AXIS_NAME_PADDING, yAxisNamePadding.getText());
	    props.put(FusionChartProperties.Y_AXIS_VALUES_PADDING, yAxisValuesPadding.getText());
	    props.put(FusionChartProperties.LABEL_PADDING, labelPadding.getText());
	    props.put(FusionChartProperties.VALUE_PADDING, valuePadding.getText());
		
	    props.put(FusionChartProperties.SHOW_LEGEND, showLegend.getSelection() ? 1 : 0);
	    
	    if(legendRight.getSelection()){
			props.put(FusionChartProperties.LEGEND_POSITION, "RIGHT");
		}
		else {
			props.put(FusionChartProperties.LEGEND_POSITION, "BOTTOM");
		}

	    props.put(FusionChartProperties.FORMAT_NUMBER_SCALE, formatNumberScale.getSelection() ? 1 : 0);
	    props.put(FusionChartProperties.NUMBER_PREFIX, prefix.getText());
	    props.put(FusionChartProperties.NUMBER_SUFFIX, suffix.getText());
	    props.put(FusionChartProperties.DECIMAL_SEPARATOR, decimalSeparator.getText());
	    props.put(FusionChartProperties.THOUSAND_SEPARATOR, thousandSeparator.getText());
		props.put(FusionChartProperties.DECIMALS, numberDecimals.getText());
		props.put(FusionChartProperties.FORCE_DECIMALS, forceNumberDecimals.getText());

		props.put(FusionChartProperties.SHOW_PERCENT_VALUES, showPercentValues.getSelection() ? 1 : 0 );
		props.put(FusionChartProperties.SHOW_PERCENT_IN_TOOLTIP, showPercentInTooltip.getSelection() ? 1 : 0);
	    props.put(FusionChartProperties.SLICING_DISTANCE, slicingDistance.getText());

	    props.put(FusionChartProperties.SHOW_SUM, showSum.getSelection() ? 1 : 0);

		try {
			vanillaChart.setCustomProperties(props);
		} catch (SemanticException e) {
			e.printStackTrace();
		}
	}
	
	private class ColorListener implements MouseListener, SelectionListener {

		private Text txtColor;
		
		public ColorListener(Text txtColor){
			this.txtColor = txtColor;
		}
		
		@Override
		public void mouseDoubleClick(MouseEvent e) {
			//Do nothing
		}

		@Override
		public void mouseDown(MouseEvent e) {
			//Do nothing
		}

		@Override
		public void mouseUp(MouseEvent e) {
			changeColor();
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			//Do nothing
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			changeColor();
		}
		
		private void changeColor(){
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
	        hex = hex.toUpperCase();
	        
	        Color color = new Color(getShell().getDisplay(), rgb);
	        
	        txtColor.setText(hex);
	        txtColor.setBackground(color);
		}
	}
}
