package bpm.fd.web.client.I18N;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;

public interface Labels extends Constants {

	public static Labels lblCnst = (Labels) GWT.create(Labels.class);

	String TitleApplication();
	String NewDashboard();
	String OpenDashboard();
	String MyDashboard();
	String SaveNewDashboard();
	String UpdateExistingDashboard();
	String DashboardSavedSuccess();
	String DashboardUpdatedSuccess();
	String Save();
	String Palette();
	String Structure();
	String Chart();
	String Filter();
	String Slicer();
	String DataGrid();
	String Gauge();
	String Report();
	String Dashlet();
	String OlapView();
	String Map();
	String WmsMap();
	String OsmMap();
	String Markdown();
	String Label();
	String Image();
	String URL();
	String Button();
	String Clock();
	String FormattedText();
	String Comment();
	String Div();
	String StackableCell();
	String DrillStackableCell();
	String Folder();
	String Unknown();
	String CSS();
	String Hide();
	String Apply();
	String Data();
	String Preview();
	String Designer();
	String Preferences();
	String Undo();
	String Redo();
	String PageName();
	String MyPage();
	String Remove();
	String Name();
	String Description();
	String General();
	String Options();
	String PropertiesOf();
	String BeCareful();
	String DashboardLooseFunctions();
	String MyLabel();
	String MyChart();
	String MyFilter();
	
	String Caption();
	String SubCaption();
	String ShowLabel();
	String ShowValues();
	String ShowBorder();
	String MultiLineLabels();
	String DynamicLegend();
	String ExportEnable();
	String BackgroundAlpha();
	String BackgroundSwAlpha();
	String BackgroundColor();
	String BorderColor();
	String BaseFontColor();
	String BorderThickness();
	String BaseFontSize();
	String LabelSize();
	String SlicingDistance();
	String PieSliceDepth();
	String PieRadius();
	String RotateLabels();
	String SlantLabels();
	String RotateValues();
	String PlaceValuesInside();
	String RotateYAxisName();
	String PrincipalYAxisName();
	String SecondaryYAxisName();
	String XAxisName();
	String LineSerieName();
	String FormatNumber();
	String FormatNumberScale();
	String ForceDecimal();
	String NumberPrefix();
	String NumberSuffix();
	String DecimalSeparator();
	String ThousandSeparator();
	String Decimals();
	
	String FilterRenderer();
	
	String SubmitOnChange();
	String SelectFirstValue();
	String InitParameterWithFirstValue();
	String Hidden();
	String Required();
	
	String DefaultValue();	
	String Size();
	String IsVertical();
	String MultipleValues();
	String Delay();
	String AutoRun();
	String BarColor();
	String SliderColor();
	
	String GroupField();
	String GroupFieldLabel();
	String SubGroupField();
	
	String OrderType();
	String Title();
	String Subtitle();

	String DropDownListBox();
	String Slider();
	String Checkbox();
	String RadioButton();
	String List();
	String TextField();
	String DatePicker();
	String Menu();
	String DynamicText();
	String MeasureName();
	String ValueField();
	String ApplyOnDistinctSerie();
	String PrimaryAxis();
	String Rendering();
	String Aggregator();
	String NoMeasures();
	String Datasources();
	String Datasets();
	String MyImage();
	String Images();
	String UploadImage();
	
	String ColumnLabel();
	String ColumnValue();
	String ColumnOrder();

	String ChartTypes();
	String Pie();
	String Column();
	String Bar();
	String Line();
	String Funnel();
	String Pyramid();
	String Marimeko();
	String Radar();
	String Combination();
	String Area();
	String Classic();
	String Stacked();
	String ThreeD();
	String ChartDisplayTypes();
	String ChartType();
	
	String ItemName();
	String Parameter();
	String Provider();
	String SelectComponent();
	String NoSelection();
	String Parameters();
	String Aggregations();
	String Modify();
	String Add();
	
	String Drill();
	String AddPage();
	String MyMarkdown();
	String MyReport();
	String MyCubeView();
	String CssClassSeparateThemBySpace();
	
	String BadColor();
	String MediumColor();
	String GoodColor();

	String InnerRadius();
	String OuterRadius();
	String StartAngle();
	String StopAngle();

	String Bulb();
	
	String MinMaxFromField();
	String MinField();
	String MaxField();	
	String MinValue();
	String MaxValue();
	
	String MinMaxThresholdFromField();
	String MinThresholdField();
	String MaxThresholdField();	
	String MinThresholdValue();
	String MaxThresholdValue();
	
	String TargetFromField();
	String TargetField();
	String TargetValue();
	
	String Tolerance();
	String CreateDashboard();
	String HideHeader();
	String ShowHeader();
	String MyHyperlink();
	String Logo();
	String IsNotSupported();
	String ExpandHeader();
	String ReduceHeader();
	String DashboardTitle();
	String MyGauge();
	
	String GeneralOptions();
	String PieOptions();
	String NumberFormat();
	String DisplayGridOnWorkspace();
	String MyParameter();
	String MyDashlet();
	String WidthBorder();
	String URLAndParam();
	String MyDatadrid();
	String IncludeTotal();
	String NoColumnAvailable();

	String ShowLegend();
	String Orientation();
	String Layout();
	String ShowBaseLayer();
	
	String ZoneField();
	String Series();
	String SerieName();
	String DisplayLayer();
	String MinMarker();
	String MaxMarker();
	String MinColor();
	String MaxColor();
	String MyMap();
	String CustomName();
	
	String AlignTop();
	String AlignBottom();
	String AlignLeft();
	String AlignRight();
	
	String ResizeWidth();
	String ResizeHeight();
	String Bottom();
	String Left();
	String Right();
	String Top();
	String AlignementTabBar();
	String Tab();
	String IsInteractive();
	String ShowDimensions();
	String ShowCubeFunctions();
	String DimensionMeasureSelection();
	String Type();
	String Dimension();
	String Measure();
	String MyButton();
	String Scripts();
	String Javascript();
	String Widgets();
	String Containers();
	String Controls();
	String Text();
	
	String SaveDashboard();
	String SaveDashboardBeforeExit();
	String DeleteConfirmation();
	String DeleteConfirmationMessage();
	String UpdateDashboardBeforeExit();
	String Horizontal();
	String Vertical();
	String DashboardDefinition();
	String KpiChart();
	String MyKpiChart();
	String Kpi();
	String MyKpi();
	String ComponentType();
	String Level();
	
	String StartDate();
	String EndDate();
	String ChooseDate();
	String AsParameter();
	
	
	//R Chart
	String RChart();
	String POINT();
	String LINE();
	String BAR_V();
	String BAR_H();
	String HIST();
	String MyChartR();
	String AxeXField();
	String AxeYField();
	String YAxisLabel();
	String XAxisLabel();
	String DynamicR();
	String SeperationBar();
	String BarOptions();
	String HistOptions();
	String density();
	String binsNumber();
	String specifyBins();
	String Boxplot();
	String Heatmap();
	String Treemap();
	String Acp();
	String Correlation();
	String colField();
	String listColumn();
	String EditTheAgregation();
	String DeleteTheAgregation();
	String AgregationProperties();
	
	String DataPreparation();
	String OrderField();
	String IsGroup();
	String Aggregation();
	String Data4Citizen();
	String MyData4Citizen();
	String DynamicLabel();
	String MyDynamicLabel();
	String LimitCommentNumber();
	String CommentNumber();
	String IsValidation();
	String FlourishIdAndParam();
	String MyFlourish();
	String Flourish();
}
