package bpm.fd.web.client.widgets;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import bpm.fd.core.ComponentParameter;
import bpm.fd.core.DashboardComponent;
import bpm.fd.core.IComponentUrl;
import bpm.fd.core.component.ButtonComponent;
import bpm.fd.core.component.ChartComponent;
import bpm.fd.core.component.CommentComponent;
import bpm.fd.core.component.ComponentType;
import bpm.fd.core.component.CubeViewComponent;
import bpm.fd.core.component.D4CComponent;
import bpm.fd.core.component.DataVizComponent;
import bpm.fd.core.component.DatagridComponent;
import bpm.fd.core.component.DynamicLabelComponent;
import bpm.fd.core.component.FilterComponent;
import bpm.fd.core.component.FlourishComponent;
import bpm.fd.core.component.GaugeComponent;
import bpm.fd.core.component.ImageComponent;
import bpm.fd.core.component.JspComponent;
import bpm.fd.core.component.KpiChartComponent;
import bpm.fd.core.component.KpiComponent;
import bpm.fd.core.component.LabelComponent;
import bpm.fd.core.component.LinkComponent;
import bpm.fd.core.component.MapComponent;
import bpm.fd.core.component.MarkdownComponent;
import bpm.fd.core.component.RChartComponent;
import bpm.fd.core.component.ReportComponent;
import bpm.fd.web.client.I18N.Labels;
import bpm.fd.web.client.actions.WidgetAction;
import bpm.fd.web.client.handlers.HasComponentSelectionHandler;
import bpm.fd.web.client.handlers.IComponentSelectionHandler;
import bpm.fd.web.client.images.DashboardImage;
import bpm.fd.web.client.panels.IDropPanel;
import bpm.fd.web.client.popup.PropertiesPopup;
import bpm.fd.web.client.utils.ChartTypeHelper;
import bpm.fd.web.client.utils.RChartTypeHelper;
import bpm.fd.web.client.utils.WidgetManager;
import bpm.gwt.commons.client.loading.IWait;

public class DashboardWidget extends WidgetComposite {
	
	public static final String WIDGET_ID = "WidgetId";
	public static final String ABSOLUTE_LEFT = "AbsoluteLeft";
	public static final String ABSOLUTE_TOP = "AbsoluteTop";

	private static ChartWidgetUiBinder uiBinder = GWT.create(ChartWidgetUiBinder.class);

	interface ChartWidgetUiBinder extends UiBinder<Widget, DashboardWidget> {
	}

	interface MyStyle extends CssResource {
		String img();
		String labelComponent();
		String resizeHorizontal();
		String resizeBoth();
		String selectedStyle();
		String paramNotValid();
		String provided();
		String provider();
		String draggableFix();
	}

	@UiField
	MyStyle style;

	@UiField
	FocusPanel focus, panelTop;
	
	@UiField
	Label lblTitle;
	
	@UiField
	HTML htmlContent;
	
	@UiField
	TextBox txtTitle;
	
	@UiField
	Image img;
	
	private boolean showHeaderLabel = true;
	private boolean isContentText = false;
	private boolean propertiesOpen = false;
	
	private WidgetManager widgetManager;
	private List<WidgetComposite> providers;
	
	private IDropPanel dropPanel;

	public DashboardWidget(IDropPanel dropPanel, IWait waitPanel, HasComponentSelectionHandler componentHandler, WidgetManager widgetManager, ComponentType tool, int positionLeft, int positionTop) {
		super(waitPanel, componentHandler, positionLeft, positionTop);
		initWidget(uiBinder.createAndBindUi(this));
		this.widgetManager = widgetManager;
		this.dropPanel = dropPanel;
		buildContent(widgetManager, tool);
	}
	
	public DashboardWidget(IDropPanel dropPanel, IWait waitPanel, HasComponentSelectionHandler componentHandler, WidgetManager widgetManager, DashboardComponent component) {
		super(waitPanel, componentHandler, component.getLeft(), component.getTop());
		initWidget(uiBinder.createAndBindUi(this));
		this.component = component;
		this.dropPanel = dropPanel;
		
		this.widgetManager = widgetManager;

		ComponentType tool = component.getType();
		buildContent(widgetManager, tool);
	}
	
	private void buildContent(WidgetManager widgetManager, ComponentType tool) {
		buildTool(widgetManager, tool);
		
		focus.addDoubleClickHandler(this);

		focus.addStyleName(style.draggableFix());
		focus.getElement().setDraggable(Element.DRAGGABLE_TRUE);
		focus.addDragStartHandler(dragStartHandler);
	}
	
	private DragStartHandler dragStartHandler = new DragStartHandler() {
		
		@Override
		public void onDragStart(DragStartEvent event) {
			event.stopPropagation();
			
			int absoluteLeft = focus.getAbsoluteLeft();
			int absoluteTop = focus.getAbsoluteTop();
			
			int x = event.getNativeEvent().getClientX() - absoluteLeft;
			int y = event.getNativeEvent().getClientY() - absoluteTop;
			
			event.setData(DashboardWidget.WIDGET_ID, getGeneratedId());
			event.setData(DashboardWidget.ABSOLUTE_LEFT, String.valueOf(x));
			event.setData(DashboardWidget.ABSOLUTE_TOP, String.valueOf(y));
			event.getDataTransfer().setDragImage(getElement(), x, y);
			
			List<DashboardWidget> widgets = widgetManager.getSelectedWidgets();
			if (widgets != null)  {
				boolean found = false;
				for (DashboardWidget widget : widgets) {
					if (widget.equals(DashboardWidget.this)) {
						found = true;
						break;
					}
				}
				
				if (!found) {
					widgetManager.clearWidgetSelection(null);
				}
			}
		}
	};

	private void buildTool(WidgetManager widgetManager, ComponentType tool) {
		boolean generateId = true;
		int width = DEFAULT_WIDTH;
		int height = DEFAULT_HEIGHT;

		String name = "";
		String title = "";
		if (component != null) {
			if (component.getName() != null) {
				name = component.getName();
				generateId = false;
			}
			title = component.getTitle();
		}
		
		switch (tool) {
		case LABEL:
			name = "Label";
			if (component == null) {
				component = new LabelComponent();
				title = Labels.lblCnst.MyLabel();
			}
			
			this.showHeaderLabel = false;
			this.isContentText = true;
			
			focus.setStyleName(style.labelComponent());
			focus.addStyleName(style.resizeBoth());
			img.removeFromParent();
			break;
		case CHART:
			name = "Chart";
			if (component == null) {
				component = new ChartComponent();
				title = Labels.lblCnst.MyChart();
				
				refreshImage(DashboardImage.INSTANCE.chart_pie_3D());
			}
			else {
				refreshImage(ChartTypeHelper.findImage(((ChartComponent)component).getNature()));
			}
			break;
		case RCHART:
			name = "R Chart";
			if (component == null) {
				component = new RChartComponent();
				title = Labels.lblCnst.MyChartR();
				
				refreshImage(DashboardImage.INSTANCE.line());
			}
			else {
				refreshImage(RChartTypeHelper.findImage(((RChartComponent)component).getNature()));
			}
			break;
		case FILTER:
			name = "Filter";
			if (component == null) {
				component = new FilterComponent();
				title = Labels.lblCnst.MyFilter();
			}

			refreshImage(DashboardImage.INSTANCE.filter());
			break;
		case IMAGE:
			name = "Image";
			if (component == null) {
				component = new ImageComponent();
				title = Labels.lblCnst.MyImage();

				refreshImage(DashboardImage.INSTANCE.no_image_svg());
			}
			else {
				String imageUrl = ((ImageComponent) component).getUrl();
				if (imageUrl != null) {
					refreshImage(imageUrl);
				}
				else {
					refreshImage(DashboardImage.INSTANCE.no_image_svg());
				}
			}
			break;
			
		case MARKDOWN:
			name = "Markdown";
			if (component == null) {
				component = new MarkdownComponent();
				title = Labels.lblCnst.MyMarkdown();
			}

			refreshImage(DashboardImage.INSTANCE.markdown_big());
			break;
		case REPORT:
			name = "Report";
			if (component == null) {
				component = new ReportComponent();
				title = Labels.lblCnst.MyReport();
			}

			refreshImage(DashboardImage.INSTANCE.report_svg());
			break;
		case KPI_CHART:
			name = "KPI_Chart";
			if (component == null) {
				component = new KpiChartComponent();
				title = Labels.lblCnst.MyKpiChart();
			}
			else {
				width = component.getWidth() > 0 ? component.getWidth() : DEFAULT_WIDTH;
			}

			refreshImage(DashboardImage.INSTANCE.kpi());
			break;
		case KPI:
			name = "KPI";
			if (component == null) {
				component = new KpiComponent();
				title = Labels.lblCnst.MyKpi();
			}
			else {
				width = component.getWidth() > 0 ? component.getWidth() : DEFAULT_WIDTH;
			}

			refreshImage(DashboardImage.INSTANCE.kpi_tool_svg());
			break;
		case DATAVIZ:
			name = "DATAPREPARATION";
			if (component == null) {
				component = new DataVizComponent();
				title = Labels.lblCnst.DataPreparation();
			}
			else {
				width = component.getWidth() > 0 ? component.getWidth() : DEFAULT_WIDTH;
			}

			refreshImage(DashboardImage.INSTANCE.kpi());
			break;
		case MAP:
			name = "Map";
			if (component == null) {
				component = new MapComponent();
				title = Labels.lblCnst.MyMap();
			}

			refreshImage(DashboardImage.INSTANCE.map_svg());
			break;
		case OLAP_VIEW:
			name = "OlapView";
			if (component == null) {
				component = new CubeViewComponent();
				title = Labels.lblCnst.MyCubeView();
			}

			refreshImage(DashboardImage.INSTANCE.olap_view_svg());
			break;
			
		case GAUGE:
			name = "Gauge";
			if (component == null) {
				component = new GaugeComponent();
				title = Labels.lblCnst.MyGauge();
			}

			refreshImage(DashboardImage.INSTANCE.gauge_svg());
			break;
			
		case URL:
			name = "Hyperlink";
			if (component == null) {
				component = new LinkComponent();
				title = Labels.lblCnst.MyHyperlink();
			}
			
			this.isContentText = true;
			
			focus.setStyleName(style.labelComponent());
			focus.addStyleName(style.resizeHorizontal());
			img.removeFromParent();
			break;
			
		case DASHLET:
			name = "Dashlet";
			if (component == null) {
				component = new JspComponent();
				title = Labels.lblCnst.MyDashlet();
			}
			else {
				width = component.getWidth() > 0 ? component.getWidth() : DEFAULT_WIDTH;
			}
			
			this.isContentText = true;
			
			focus.setStyleName(style.labelComponent());
			focus.addStyleName(style.resizeBoth());
			img.removeFromParent();
			break;
			
		case D4C:
			name = "Data4Citizen";
			if (component == null) {
				component = new D4CComponent();
				title = Labels.lblCnst.MyData4Citizen();
			}
			else {
				width = component.getWidth() > 0 ? component.getWidth() : DEFAULT_WIDTH;
			}
			
			this.isContentText = true;
			
			focus.setStyleName(style.labelComponent());
			focus.addStyleName(style.resizeBoth());
			img.removeFromParent();
			break;
			
		case COMMENT:
			name = "Comment";
			if (component == null) {
				component = new CommentComponent();
				title = Labels.lblCnst.Comment();
			}
			else {
				width = component.getWidth() > 0 ? component.getWidth() : DEFAULT_WIDTH;
			}
			
			this.isContentText = true;
			
			focus.setStyleName(style.labelComponent());
			focus.addStyleName(style.resizeBoth());
			img.removeFromParent();
			break;
			
		case DATA_GRID:
			name = "DataGrid";
			if (component == null) {
				component = new DatagridComponent();
				title = Labels.lblCnst.MyDatadrid();
			}
			else {
				width = component.getWidth() > 0 ? component.getWidth() : DEFAULT_WIDTH;
			}

			refreshImage(DashboardImage.INSTANCE.datagrid());
			break;
			
		case BUTTON:
			name = "Button";
			if (component == null) {
				component = new ButtonComponent();
				title = Labels.lblCnst.MyButton();
			}
			else {
				width = component.getWidth() > 0 ? component.getWidth() : DEFAULT_WIDTH;
			}

			this.showHeaderLabel = false;
			this.isContentText = true;
			
			focus.setStyleName(style.labelComponent());
			focus.addStyleName(style.resizeBoth());
			img.removeFromParent();
			break;
			
		case DYNAMIC_LABEL:
			name = "DynamicLabel";
			if (component == null) {
				component = new DynamicLabelComponent();
				title = Labels.lblCnst.MyDynamicLabel();
			}
			else {
				width = component.getWidth() > 0 ? component.getWidth() : DEFAULT_WIDTH;
			}

			refreshImage(DashboardImage.INSTANCE.dynamic_label_svg());
			break;
			
		case FLOURISH:
			name = "Flourish";
			if (component == null) {
				component = new FlourishComponent();
				title = Labels.lblCnst.MyFlourish();
			}
			else {
				width = component.getWidth() > 0 ? component.getWidth() : DEFAULT_WIDTH;
			}
			
			this.isContentText = true;
			
			focus.setStyleName(style.labelComponent());
			focus.addStyleName(style.resizeBoth());
			img.removeFromParent();
			break;

		default:
			break;
		}

		setGeneratedId(widgetManager.addWidget(name, DashboardWidget.this, generateId));

		lblTitle.setVisible(showHeaderLabel);
		htmlContent.setVisible(isContentText);
		
		if(component.getName() == null || component.getName().isEmpty()) {
			component.setName(getGeneratedId());
		}
		component.setTitle(title);
		setTitle(title);
		width = component.getWidth() > 0 ? component.getWidth() : DEFAULT_WIDTH;
		focus.getElement().getStyle().setWidth(width, Unit.PX);
		height = component.getHeight() > 0 ? component.getHeight() : DEFAULT_HEIGHT;
		focus.getElement().getStyle().setHeight(height, Unit.PX);
	}

	private void refreshImage(Object resource) {
		if(resource instanceof ImageResource) {
			img.setUrl(((ImageResource)resource).getSafeUri());
		}
		else {
			img.setUrl(((DataResource)resource).getSafeUri());
		}
		img.addStyleName(style.img());
	}

	private void refreshImage(String imageUrl) {
		imageUrl = imageUrl.replace("webapps", "..");
		
		img.setUrl(imageUrl);
		img.addStyleName(style.img());
	}
	
	@Override
	public void setTitle(String title) {
		panelTop.setTitle(title);
		lblTitle.setText(title);
		txtTitle.setText(title);
		
		refreshContentText();
	}
	
	public void refreshContentText() {
		if (component instanceof IComponentUrl) {
			htmlContent.setHTML(((IComponentUrl) component).getUrl());
		}
		else {
			htmlContent.setHTML(component.getTitle());
		}
	}
	
	@Override
	public int getWidth() {
		String width = focus.getElement().getStyle().getWidth();
		try {
			return Integer.parseInt(width.split("px")[0]);
		} catch (Exception e) { }
		
		return 100;
	}
	
	@Override
	public int getHeight() {
		String height = focus.getElement().getStyle().getHeight();
		try {
			return Integer.parseInt(height.split("px")[0]);
		} catch (Exception e) { }
		
		return 100;
	}
	
	public void resize(int width, int height) {
		focus.getElement().getStyle().setWidth(width, Unit.PX);
		focus.getElement().getStyle().setHeight(height, Unit.PX);
	}

	@UiHandler("panelTop")
	public void onTopDoubleClick(DoubleClickEvent event) {
		event.stopPropagation();
		
		updateUi(true);
	}
	
	@UiHandler("focus")
	public void onKeyComponent(KeyPressEvent event) {
		NativeEvent nEv = event.getNativeEvent();
		if(nEv.getKeyCode() == KeyCodes.KEY_DELETE) {
			WidgetAction action = new WidgetAction(widgetManager, dropPanel, this, this.getLeft(), this.getTop());
			dropPanel.getActionManager().launchAction(action, true);
		}
	}
	
	@UiHandler("focus")
	public void onSelectWidget(ClickEvent event) {
		HasComponentSelectionHandler componentHandler = getComponentHandler();
		if (componentHandler != null && componentHandler.getComponentSelectionHandlers() != null) {
			for (IComponentSelectionHandler handler : new ArrayList<IComponentSelectionHandler>(componentHandler.getComponentSelectionHandlers())) {
				handler.selectComponent(this);
			}
		}
		NativeEvent nEv = event.getNativeEvent();
		if(!nEv.getCtrlKey()) {
			widgetManager.clearWidgetSelection(this);
		}
		select();
		widgetManager.selectWidget(this);
		
		if (component.getParameters() != null) {
			boolean hasOneProvider = false;
			providers = new ArrayList<>();
			for (ComponentParameter param : component.getParameters()) {
				if (param.getProvider() != null) {
					DashboardComponent provider = param.getProvider();
					
					WidgetComposite composite = widgetManager.getWidget(provider);
					if (composite != null) {
						hasOneProvider = true;
						
						composite.addStyleName(style.provider());
						providers.add(composite);
					}
				}
			}
			
			if (hasOneProvider) {
				focus.addStyleName(style.provided());
			}
		}
	}
	
	public void select() {
		focus.addStyleName(style.selectedStyle());
	}
	
	public void deselect() {
		focus.removeStyleName(style.selectedStyle());
		focus.setFocus(false);

		focus.removeStyleName(style.provided());
		if (providers != null) {
			for (WidgetComposite provider : providers) {
				provider.removeStyleName(style.provider());
			}
		}
		
		checkParameters();
	}

	@UiHandler("focus")
	public void onDeselectWidget(BlurEvent event) {
		event.preventDefault();
	}
	
	@Override
	public void onDoubleClick(DoubleClickEvent event) {
		event.stopPropagation();
		
		showProperties();
	}
	
	public void showProperties() {
		if (!propertiesOpen) {
			PropertiesPopup popup = new PropertiesPopup(getWaitPanel(), getComponentHandler(), component, false);
			popup.center();
			popup.addCloseHandler(this);
			
			propertiesOpen = true;
		}
	}
	
	public PropertiesPopup getPropertiesPanel() {
		return new PropertiesPopup(getWaitPanel(), getComponentHandler(), component, true);
	}

	@Override
	public void onClose(CloseEvent<PopupPanel> event) {
		refreshUiFromProperties();
	}

	@UiHandler("txtTitle")
	public void onDeselect(BlurEvent event) {
		updateUi(false);
	}

	@UiHandler("txtTitle")
	public void onEnter(KeyPressEvent event) {
		if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			updateUi(false);
		}
	}

	private void updateUi(boolean displayTextbox) {
		lblTitle.setVisible(showHeaderLabel && !displayTextbox);
		htmlContent.setVisible(isContentText && !displayTextbox);
		txtTitle.setVisible(displayTextbox);
		txtTitle.setFocus(displayTextbox);
		
		if (!displayTextbox) {
			String title = txtTitle.getText();
			component.setTitle(title);
			setTitle(title);
		}
		else {
			String title = txtTitle.getText();
			txtTitle.setCursorPos(title.length());
		}
	}

	public void refreshUiFromProperties() {
		this.propertiesOpen = false;
		
		String title = component.getTitle();
		setTitle(title);
		
		if (component instanceof ImageComponent) {
			ImageComponent image = (ImageComponent) component;
			if (image.getUrl() != null && !image.getUrl().isEmpty()) {
				refreshImage(image.getUrl());
			}
		}
		else if (component instanceof ChartComponent) {
			ChartComponent chart = (ChartComponent) component;
			if (chart.getNature() != null) {
				refreshImage(ChartTypeHelper.findImage(chart.getNature()));
			}
		}
		
		checkParameters();
	}

	private void checkParameters() {
		boolean isValid = true;
		if (component != null && component.getParameters() != null && !component.getParameters().isEmpty()) {
			for (ComponentParameter param : component.getParameters()) {
				if (param.getProvider() == null) {
					isValid = false;
					break;
				}
			}
		}

		if (isValid) {
			focus.removeStyleName(style.paramNotValid());
		}
		else {
			focus.addStyleName(style.paramNotValid());
		}
	}

	public DashboardComponent getComponent() {
		int top = getTop();
		int left = getLeft();
		
		int width = getWidth();
		int height = getHeight();
		
		component.setLeft(left);
		component.setTop(top);
		component.setWidth(width);
		component.setHeight(height);
		
		return component;
	}
	
	@Override
	public WidgetManager getWidgetManager() {
		return widgetManager;
	}
}
