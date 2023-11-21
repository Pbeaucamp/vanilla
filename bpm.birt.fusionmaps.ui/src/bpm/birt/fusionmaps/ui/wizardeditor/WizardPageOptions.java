package bpm.birt.fusionmaps.ui.wizardeditor;

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

import bpm.birt.fusionmaps.core.reportitem.FusionmapsItem;
import bpm.birt.fusionmaps.ui.Activator;
import bpm.vanilla.map.core.design.fusionmap.FusionMapProperties;

public class WizardPageOptions extends WizardPage  {

	private FusionmapsItem vanillaMap;
	
	private FormToolkit toolkit;
	private Color white;
	
	//Fonctionnal Attributes PART
	private Button showLabels, includeNames, includeValue, showShadow, showBevel;
	private Text fontColor;
	private Spinner fontSize;
	
	//Map Cosmetics PART
	private Text bgColor, fillColor, hoverColor;
	private Spinner bgAlpha, fillAlpha;
	
	//Format Number PART
	private Text prefix, decimalSeparator, thousandSeparator;
	private Spinner numberDecimals;
	private Button formatNumberScale;

	//Tooltip PART
	private Text tooltipBgColor;
	private Button showTooltip;
	
	//Padding & Margin PART
	private Text mapLeftMargin, mapRightMargin, mapTopMargin, mapBottomMargin, legendPadding;
	
	//Legend PART
	private Text legendCaption, legendBgColor;
	private Spinner legendAlpha;
	private Button showLegend, legendBottom, legendRight;
	
	protected WizardPageOptions(String pageName, FusionmapsItem item) {
		super(pageName);
		this.vanillaMap = item;
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
		
		initProperties(vanillaMap.getCustomProperties());
	}
	
	private void createContent(Composite main) {
		Section sectionFoncAttri = toolkit.createSection(main, Section.TITLE_BAR | Section.TWISTIE);
		sectionFoncAttri.setText("Fonctionnal Attributes");
		sectionFoncAttri.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
	    buildFonctionnalAttributesSection(sectionFoncAttri);

	    
		Section sectionMapCosme = toolkit.createSection(main, Section.TITLE_BAR | Section.TWISTIE);
		sectionMapCosme.setText("Map Cosmetics");
		sectionMapCosme.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
	    buildMapCosmeticsSection(sectionMapCosme);

	    
		Section sectionFormat = toolkit.createSection(main, Section.TITLE_BAR | Section.TWISTIE);
		sectionFormat.setText("Format Number");
		sectionFormat.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
	    buildFormatSection(sectionFormat);
	    

		Section sectionTooltip = toolkit.createSection(main, Section.TITLE_BAR | Section.TWISTIE);
		sectionTooltip.setText("Tooltip");
		sectionTooltip.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
	    buildTooltipSection(sectionTooltip);
	    

		Section sectionMarginsAndPadding = toolkit.createSection(main, Section.TITLE_BAR | Section.TWISTIE);
		sectionMarginsAndPadding.setText("Margins & Paddings");
		sectionMarginsAndPadding.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
	    buildMarginsAndPaddingSection(sectionMarginsAndPadding);
	    

		Section sectionLegend = toolkit.createSection(main, Section.TITLE_BAR | Section.TWISTIE);
		sectionLegend.setText("Legend");
		sectionLegend.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
	    buildLegendSection(sectionLegend);
	}
	
	private void buildFonctionnalAttributesSection(Section section) {
	    Composite composite = new Composite(section, SWT.NONE);
	    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    composite.setLayout(new GridLayout(4, false));
	    composite.setBackground(white);
	    
	    Label lblShowLabels = new Label(composite, SWT.None);
	    lblShowLabels.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false, 2, 1));
	    lblShowLabels.setText("Show labels: "); //$NON-NLS-1$
		
	    showLabels = new Button(composite, SWT.CHECK);
	    showLabels.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
	    
	    Label lblIncludeNames = new Label(composite, SWT.None);
	    lblIncludeNames.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false, 2, 1));
	    lblIncludeNames.setText("Include names in labels: "); //$NON-NLS-1$
		
	    includeNames = new Button(composite, SWT.CHECK);
	    includeNames.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		
	    Label lblIncludeValues = new Label(composite, SWT.None);
	    lblIncludeValues.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false, 2, 1));
	    lblIncludeValues.setText("Include values in labels: "); //$NON-NLS-1$
		
	    includeValue = new Button(composite, SWT.CHECK);
	    includeValue.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));

	    buildImageHelp(composite, FusionMapProperties.SHOW_SHADOW);
	    
		Label lblShowShadow = new Label(composite, SWT.None);
		lblShowShadow.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblShowShadow.setText("Show shadow: "); //$NON-NLS-1$
		
		showShadow = new Button(composite, SWT.CHECK);
		showShadow.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		
	    buildImageHelp(composite, FusionMapProperties.SHOW_BEVEL);
		
		Label lblShowBevel = new Label(composite, SWT.None);
		lblShowBevel.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblShowBevel.setText("Show bevel: "); //$NON-NLS-1$
		
		showBevel = new Button(composite, SWT.CHECK);
		showBevel.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		
		Label lblFontSize = new Label(composite, SWT.None);
		lblFontSize.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false, 2, 1));
		lblFontSize.setText("Define the font size: "); //$NON-NLS-1$
		
		fontSize = new Spinner(composite, SWT.BORDER);
		fontSize.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		
		Label lblFontColor = new Label(composite, SWT.None);
		lblFontColor.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false, 2, 1));
		lblFontColor.setText("Define the font color: "); //$NON-NLS-1$
		
		fontColor = new Text(composite, SWT.BORDER);
		fontColor.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		fontColor.setEditable(false);
		fontColor.addMouseListener(new ColorListener(fontColor));
		
		Button btnPickColor = new Button(composite, SWT.PUSH);
		btnPickColor.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		btnPickColor.setText("Pick a color");
		btnPickColor.addSelectionListener(new ColorListener(fontColor));
		
	    section.setClient(composite);
	}
	
	private void buildMapCosmeticsSection(Section section) {
	    Composite composite = new Composite(section, SWT.NONE);
	    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    composite.setLayout(new GridLayout(3, false));
	    composite.setBackground(white);
		
	    Label lblBgColor = new Label(composite, SWT.None);
	    lblBgColor.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
	    lblBgColor.setText("Define the map background color: "); //$NON-NLS-1$
		
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
		lblBgAlpha.setText("Define the map background alpha: "); //$NON-NLS-1$
		
		bgAlpha = new Spinner(composite, SWT.BORDER);
		bgAlpha.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		
		Label lblFillColor = new Label(composite, SWT.None);
		lblFillColor.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblFillColor.setText("Define the zone fill color: "); //$NON-NLS-1$
		
	    fillColor = new Text(composite, SWT.BORDER);
	    fillColor.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	    fillColor.setEditable(false);
	    fillColor.addMouseListener(new ColorListener(fillColor));
		
		Button btnPickFillColor = new Button(composite, SWT.PUSH);
		btnPickFillColor.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		btnPickFillColor.setText("Pick a color");
		btnPickFillColor.addSelectionListener(new ColorListener(fillColor));
		
		Label lblFillAlpha = new Label(composite, SWT.None);
		lblFillAlpha.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblFillAlpha.setText("Define the zone fill alpha: "); //$NON-NLS-1$
		
		fillAlpha = new Spinner(composite, SWT.BORDER);
		fillAlpha.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		
		Label lblHoverColor = new Label(composite, SWT.None);
		lblHoverColor.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblHoverColor.setText("Define the zone hover color: "); //$NON-NLS-1$
		
	    hoverColor = new Text(composite, SWT.BORDER);
	    hoverColor.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	    hoverColor.setEditable(false);
	    hoverColor.addMouseListener(new ColorListener(hoverColor));
		
		Button btnPickHoverColor = new Button(composite, SWT.PUSH);
		btnPickHoverColor.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		btnPickHoverColor.setText("Pick a color");
		btnPickHoverColor.addSelectionListener(new ColorListener(hoverColor));
		
	    section.setClient(composite);
	}
	
	private void buildFormatSection(Section section) {
	    Composite composite = new Composite(section, SWT.NONE);
	    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    composite.setLayout(new GridLayout(4, false));
	    composite.setBackground(white);

	    buildImageHelp(composite, FusionMapProperties.FORMAT_NUMBER_SCALE);
	    
	    Label lblFormatNumberScale = new Label(composite, SWT.None);
	    lblFormatNumberScale.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
	    lblFormatNumberScale.setText("Format number: "); //$NON-NLS-1$
		
		formatNumberScale = new Button(composite, SWT.CHECK);
		formatNumberScale.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
	    
	    Label lblPrefix = new Label(composite, SWT.None);
	    lblPrefix.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false, 2, 1));
	    lblPrefix.setText("Set a prefix for the values: "); //$NON-NLS-1$
		
	    prefix = new Text(composite, SWT.BORDER);
	    prefix.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
	    
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

	    buildImageHelp(composite, FusionMapProperties.DECIMALS);
	    
	    Label lblNumberDecimal = new Label(composite, SWT.None);
	    lblNumberDecimal.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
	    lblNumberDecimal.setText("Define the number of decimals: "); //$NON-NLS-1$
		
	    numberDecimals = new Spinner(composite, SWT.BORDER);
	    numberDecimals.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));

	    section.setClient(composite);
	}
	
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
		
	    section.setClient(composite);
	}
	
	private void buildMarginsAndPaddingSection(Section section) {
	    Composite composite = new Composite(section, SWT.NONE);
	    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    composite.setLayout(new GridLayout(2, false));
	    composite.setBackground(white);

		Label lblMapLeftMargin = new Label(composite, SWT.None);
		lblMapLeftMargin.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblMapLeftMargin.setText("Map left margin: "); //$NON-NLS-1$
		
		mapLeftMargin = new Text(composite, SWT.BORDER);
		mapLeftMargin.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		Label lblMapRightMargin = new Label(composite, SWT.None);
		lblMapRightMargin.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblMapRightMargin.setText("Map right margin: "); //$NON-NLS-1$
		
		mapRightMargin = new Text(composite, SWT.BORDER);
		mapRightMargin.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		Label lblMapTopMargin = new Label(composite, SWT.None);
		lblMapTopMargin.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblMapTopMargin.setText("Map top margin: "); //$NON-NLS-1$
		
		mapTopMargin = new Text(composite, SWT.BORDER);
		mapTopMargin.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		Label lblMapBottomMargin = new Label(composite, SWT.None);
		lblMapBottomMargin.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblMapBottomMargin.setText("Map bottom margin: "); //$NON-NLS-1$
		
		mapBottomMargin = new Text(composite, SWT.BORDER);
		mapBottomMargin.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		Label lblLegendPadding = new Label(composite, SWT.None);
		lblLegendPadding.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblLegendPadding.setText("Legend padding: "); //$NON-NLS-1$
		
		legendPadding = new Text(composite, SWT.BORDER);
		legendPadding.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	    
	    section.setClient(composite);
	}
	
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

		Label lblLegendCaption = new Label(composite, SWT.None);
		lblLegendCaption.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblLegendCaption.setText("Legend caption: "); //$NON-NLS-1$
		
		legendCaption = new Text(composite, SWT.BORDER);
		legendCaption.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));

		Label lblLegendPosition = new Label(composite, SWT.None);
		lblLegendPosition.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblLegendPosition.setText("Legend position: "); //$NON-NLS-1$
		
		legendRight = new Button(composite, SWT.RADIO);
		legendRight.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		legendRight.setText("Right");
		
		legendBottom = new Button(composite, SWT.RADIO);
		legendBottom.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		legendBottom.setText("Bottom");
	    
		Label lblLegendBgColor = new Label(composite, SWT.None);
		lblLegendBgColor.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblLegendBgColor.setText("Legend background color: "); //$NON-NLS-1$
		
	    legendBgColor = new Text(composite, SWT.BORDER);
	    legendBgColor.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	    legendBgColor.setEditable(false);
	    legendBgColor.addMouseListener(new ColorListener(legendBgColor));
		
		Button btnPickColor = new Button(composite, SWT.PUSH);
		btnPickColor.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		btnPickColor.setText("Pick a color");
		btnPickColor.addSelectionListener(new ColorListener(legendBgColor));
		
		Label lblLegendBgAlpha = new Label(composite, SWT.None);
		lblLegendBgAlpha.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblLegendBgAlpha.setText("Define the zone fill alpha: "); //$NON-NLS-1$
		
		legendAlpha = new Spinner(composite, SWT.BORDER);
		legendAlpha.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		
		section.setClient(composite);
	}
	
	private void buildImageHelp(Composite parent, String property){
		ImageRegistry reg = Activator.getDefault().getImageRegistry();
		
		Label lblHelp = new Label(parent, SWT.NONE);
		lblHelp.setImage(reg.get("help_small.png"));
		
		if(property.equals(FusionMapProperties.SHOW_SHADOW)){
			lblHelp.setToolTipText("Whether to drop a shadow effect for the map?");
		}
		else if(property.equals(FusionMapProperties.SHOW_BEVEL)){
			lblHelp.setToolTipText("Whether to render a 3D bevel effect to the map?");
		}
		else if(property.equals(FusionMapProperties.FORMAT_NUMBER_SCALE)){
			lblHelp.setToolTipText("Whether to add K (thousands) and M (millions) to a number after " +
					"truncating and rounding it?");
		}
		else if(property.equals(FusionMapProperties.DECIMALS)){
			lblHelp.setToolTipText("Number of decimal places to which all numbers on the map would be rounded to.");
		}
	}
	
	public void initProperties(Properties props){
		
		if(props == null || props.isEmpty()){
			props = FusionMapProperties.buildDefaultProperties();
		}
		
		showLabels.setSelection(props.get(FusionMapProperties.SHOW_LABELS) != null 
				&& props.get(FusionMapProperties.SHOW_LABELS).equals("1"));
		includeNames.setSelection(props.get(FusionMapProperties.INCLUDE_NAME_IN_LABELS) != null 
				&& props.get(FusionMapProperties.INCLUDE_NAME_IN_LABELS).equals("1"));
		includeValue.setSelection(props.get(FusionMapProperties.INCLUDE_VALUE_IN_LABELS) != null 
				&& props.get(FusionMapProperties.INCLUDE_VALUE_IN_LABELS).equals("1"));
		showShadow.setSelection(props.get(FusionMapProperties.SHOW_SHADOW) != null 
				&& props.get(FusionMapProperties.SHOW_SHADOW).equals("1"));
		showBevel.setSelection(props.get(FusionMapProperties.SHOW_BEVEL) != null 
				&& props.get(FusionMapProperties.SHOW_BEVEL).equals("1"));
		fontSize.setSelection(props.get(FusionMapProperties.BASE_FONT_SIZE) != null 
				&& !props.get(FusionMapProperties.BASE_FONT_SIZE).toString().isEmpty() ? 
				Integer.parseInt(props.get(FusionMapProperties.BASE_FONT_SIZE).toString()) : 0);
		fontColor.setText(props.get(FusionMapProperties.BASE_FONT_COLOR) != null ? 
				props.get(FusionMapProperties.BASE_FONT_COLOR).toString() : "");
		

		bgColor.setText(props.get(FusionMapProperties.BG_COLOR) != null ? 
				props.get(FusionMapProperties.BG_COLOR).toString() : "");
		bgAlpha.setSelection(props.get(FusionMapProperties.BG_ALPHA) != null
				&& !props.get(FusionMapProperties.BG_ALPHA).toString().isEmpty() ? 
				Integer.parseInt(props.get(FusionMapProperties.BG_ALPHA).toString()) : 0);
		fillColor.setText(props.get(FusionMapProperties.FILL_COLOR) != null ? 
				props.get(FusionMapProperties.FILL_COLOR).toString() : "");
		fillAlpha.setSelection(props.get(FusionMapProperties.FILL_ALPHA) != null
				&& !props.get(FusionMapProperties.FILL_ALPHA).toString().isEmpty() ? 
				Integer.parseInt(props.get(FusionMapProperties.FILL_ALPHA).toString()) : 0);
		hoverColor.setText(props.get(FusionMapProperties.HOVER_COLOR) != null ? 
				props.get(FusionMapProperties.HOVER_COLOR).toString() : "");
		
		
		formatNumberScale.setSelection(props.get(FusionMapProperties.FORMAT_NUMBER_SCALE) != null 
				&& props.get(FusionMapProperties.FORMAT_NUMBER_SCALE).equals("1"));
		prefix.setText(props.get(FusionMapProperties.NUMBER_PREFIX) != null ? 
				props.get(FusionMapProperties.NUMBER_PREFIX).toString() : "");
		decimalSeparator.setText(props.get(FusionMapProperties.DECIMAL_SEPARATOR) != null ? 
				props.get(FusionMapProperties.DECIMAL_SEPARATOR).toString() : "");
		thousandSeparator.setText(props.get(FusionMapProperties.THOUSAND_SEPARATOR) != null ? 
				props.get(FusionMapProperties.THOUSAND_SEPARATOR).toString() : "");
		numberDecimals.setSelection(props.get(FusionMapProperties.DECIMALS) != null
				&& !props.get(FusionMapProperties.DECIMALS).toString().isEmpty() ? 
				Integer.parseInt(props.get(FusionMapProperties.DECIMALS).toString()) : 0);
		

		showTooltip.setSelection(props.get(FusionMapProperties.SHOW_TOOLTIP) != null 
				&& props.get(FusionMapProperties.SHOW_TOOLTIP).equals("1"));
		tooltipBgColor.setText(props.get(FusionMapProperties.TOOLTIP_BG_COLOR) != null ? 
				props.get(FusionMapProperties.TOOLTIP_BG_COLOR).toString() : "");

		
		mapLeftMargin.setText(props.get(FusionMapProperties.MAP_LEFT_MARGIN) != null ? 
				props.get(FusionMapProperties.MAP_LEFT_MARGIN).toString() : "");
		mapRightMargin.setText(props.get(FusionMapProperties.MAP_RIGHT_MARGIN) != null ? 
				props.get(FusionMapProperties.MAP_RIGHT_MARGIN).toString() : "");
		mapTopMargin.setText(props.get(FusionMapProperties.MAP_TOP_MARGIN) != null ? 
				props.get(FusionMapProperties.MAP_TOP_MARGIN).toString() : "");
		mapBottomMargin.setText(props.get(FusionMapProperties.MAP_BOTTOM_MARGIN) != null ? 
				props.get(FusionMapProperties.MAP_BOTTOM_MARGIN).toString() : "");
		legendPadding.setText(props.get(FusionMapProperties.LEGEND_PADDING) != null ? 
				props.get(FusionMapProperties.LEGEND_PADDING).toString() : "");


		showLegend.setSelection(props.get(FusionMapProperties.SHOW_LEGEND) != null 
				&& props.get(FusionMapProperties.SHOW_LEGEND).equals("1"));
		legendCaption.setText(props.get(FusionMapProperties.LEGEND_CAPTION) != null ? 
				props.get(FusionMapProperties.LEGEND_CAPTION).toString() : "");
		if(props.get(FusionMapProperties.LEGEND_POSITION) != null 
				&& props.get(FusionMapProperties.LEGEND_POSITION).equals("RIGHT")){
			legendRight.setSelection(true);
		}
		else {
			legendBottom.setSelection(true);
		}
		legendBgColor.setText(props.get(FusionMapProperties.LEGEND_BG_COLOR) != null ? 
				props.get(FusionMapProperties.LEGEND_BG_COLOR).toString() : "");
		legendAlpha.setSelection(props.get(FusionMapProperties.LEGEND_BG_ALPHA) != null
				&& !props.get(FusionMapProperties.LEGEND_BG_ALPHA).toString().isEmpty() ? 
				Integer.parseInt(props.get(FusionMapProperties.LEGEND_BG_ALPHA).toString()) : 0);
	}
	
	public void buildProperties(){
		Properties props = new Properties();

		props.put(FusionMapProperties.SHOW_LABELS, showLabels.getSelection() ? 1 : 0);
		props.put(FusionMapProperties.INCLUDE_NAME_IN_LABELS, includeNames.getSelection() ? 1 : 0);
		props.put(FusionMapProperties.INCLUDE_VALUE_IN_LABELS, includeValue.getSelection() ? 1 : 0);
		props.put(FusionMapProperties.SHOW_SHADOW, showShadow.getSelection() ? 1 : 0);
		props.put(FusionMapProperties.SHOW_BEVEL, showBevel.getSelection() ? 1 : 0);
		props.put(FusionMapProperties.BASE_FONT_SIZE, fontSize.getText());
		props.put(FusionMapProperties.BASE_FONT_COLOR, fontColor.getText());
		
		props.put(FusionMapProperties.BG_COLOR, bgColor.getText());
		props.put(FusionMapProperties.BG_ALPHA, bgAlpha.getText());
		props.put(FusionMapProperties.FILL_COLOR, fillColor.getText());
		props.put(FusionMapProperties.FILL_ALPHA, fillAlpha.getText());
		props.put(FusionMapProperties.HOVER_COLOR, hoverColor.getText());

		props.put(FusionMapProperties.FORMAT_NUMBER_SCALE, formatNumberScale.getSelection() ? 1 : 0);
		props.put(FusionMapProperties.NUMBER_PREFIX, prefix.getText());
		props.put(FusionMapProperties.DECIMAL_SEPARATOR, decimalSeparator.getText());
		props.put(FusionMapProperties.THOUSAND_SEPARATOR, thousandSeparator.getText());
		props.put(FusionMapProperties.DECIMALS, numberDecimals.getText());

		props.put(FusionMapProperties.SHOW_TOOLTIP, showTooltip.getSelection() ? 1 : 0);
		props.put(FusionMapProperties.TOOLTIP_BG_COLOR, tooltipBgColor.getText());

		props.put(FusionMapProperties.MAP_LEFT_MARGIN, mapLeftMargin.getText());
		props.put(FusionMapProperties.MAP_RIGHT_MARGIN, mapRightMargin.getText());
		props.put(FusionMapProperties.MAP_TOP_MARGIN, mapTopMargin.getText());
		props.put(FusionMapProperties.MAP_BOTTOM_MARGIN, mapBottomMargin.getText());
		props.put(FusionMapProperties.LEGEND_PADDING, legendPadding.getText());	

		props.put(FusionMapProperties.SHOW_LEGEND, showLegend.getSelection() ? 1 : 0);	
		props.put(FusionMapProperties.LEGEND_CAPTION, legendCaption.getText());
		if(legendRight.getSelection()){
			props.put(FusionMapProperties.LEGEND_POSITION, "RIGHT");	
		}
		else {
			props.put(FusionMapProperties.LEGEND_POSITION, "BOTTOM");
		}
		props.put(FusionMapProperties.LEGEND_BG_COLOR, legendBgColor.getText());	
		props.put(FusionMapProperties.LEGEND_BG_ALPHA, legendAlpha.getText());	
		
		try {
			vanillaMap.setCustomProperties(props);
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
