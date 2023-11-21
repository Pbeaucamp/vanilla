package bpm.architect.web.client.panels;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import bpm.data.viz.core.preparation.PreparationRule;
import bpm.data.viz.core.preparation.PreparationRuleAddChar;
import bpm.data.viz.core.preparation.PreparationRuleCalc;
import bpm.data.viz.core.preparation.PreparationRuleFilter;
import bpm.data.viz.core.preparation.PreparationRuleFormat;
import bpm.data.viz.core.preparation.PreparationRuleMinMax;
import bpm.data.viz.core.preparation.PreparationRuleRecode;
import bpm.data.viz.core.preparation.PreparationRule.RuleType;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.workflow.commons.client.actions.ActionManager;
import bpm.gwt.workflow.commons.client.workflow.CustomController;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragHandlerAdapter;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.orange.links.client.connection.Connection;
import com.orange.links.client.shapes.FunctionShape;

import bpm.gwt.workflow.commons.client.utils.Splitter;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.panels.PropertiesPanel;



/**
 * @author Marc
 *
 */
public class DataVizVisualPanel extends Composite {

	private static DataVizVisualPanelUiBinder uiBinder = GWT.create(DataVizVisualPanelUiBinder.class);

	private static final int DEFAULT_BOTTOM = 210;
	private static final int DEFAULT_HEIGHT = 218;
	private static final int LEFT = 100;
	private static final int RIGHT = -100;
	private static final int DOWN = -100;
	private static final int UP = 100;
	private static final double ZOOM_STEP = 0.2;

	interface DataVizVisualPanelUiBinder extends UiBinder<Widget, DataVizVisualPanel> {
	}

	interface MyStyle extends CssResource {
		String gridView();
		String splitter();
	}

	/**
	 * Because this class has a default constructor, it can
	 * be used as a binder template. In other words, it can be used in other
	 * *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	 *   xmlns:g="urn:import:**user's package**">
	 *  <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder>
	 * Note that depending on the widget that is used, it may be necessary to
	 * implement HasHTML instead of HasText.
	 */

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelGrid, panelPropertiesParent, gridPanel;

	@UiField
	FocusPanel panelDrop;

	@UiField
	SimplePanel panelSplitter, panelProperties;

	@UiField
	Image btnLeft, btnRight, btnTop, btnBelow, btnRefresh;

	private DataVizDesignPanel parent;
	private ActionManager actionManager;
	private CustomController controller;
	private AbsolutePanel grid;
	private double zoomValue = 1;
	private PropertiesPanel propertiesPanel;
	private List<BoxItem> boxes;
	private PickupDragController dragController;
	private List<Connection> connections;

	public DataVizVisualPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public DataVizVisualPanel(DataVizDesignPanel parent) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;

		this.actionManager = new ActionManager();
		this.controller = new CustomController(actionManager, 2000, 2000);
		this.controller.getView().addStyleName(style.gridView());
		this.dragController = new PickupDragController(controller.getView(), true);
		this.dragController.addDragHandler(dragHandler);

		//this.refreshVisualPanel();
		if(this.grid != null) {
			this.grid.clear();
		}
		this.controller.showGrid(true);
		this.grid = controller.getView();

		this.boxes = new ArrayList<BoxItem>();
		this.connections = new ArrayList<Connection>();

		this.panelGrid.getElement().getStyle().setBottom(DEFAULT_BOTTOM, Unit.PX);

		this.panelGrid.add(grid);
		this.gridPanel.add(panelGrid);

		this.panelPropertiesParent.setHeight(DEFAULT_HEIGHT + "px");
		this.panelSplitter.setWidget(new Splitter(panelGrid, panelPropertiesParent, style.splitter()));
	}

	private DragHandler dragHandler = new DragHandler() {

		@Override
		public void onDragEnd(DragEndEvent event) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onDragStart(DragStartEvent event) {
			// TODO Auto-generated method stub
			BoxItem selected = new BoxItem();
			Iterator<Widget> iterator = dragController.getSelectedWidgets().iterator();
			while(iterator.hasNext()) {
				selected = (BoxItem)iterator.next();
			}
			displayByRule(selected.getRule());
		}

		@Override
		public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPreviewDragStart(DragStartEvent event) throws VetoDragException {
			// TODO Auto-generated method stub

		}

	}; 


	@UiHandler("btnZoomMax")
	public void onZoomMax(ClickEvent e) {
		if (this.zoomValue <= 1) {
			this.zoomValue = this.zoomValue + ZOOM_STEP;
			this.setZoom(this.zoomValue);
		}
	}

	@UiHandler("btnZoomMin")
	public void onZoomMin(ClickEvent e) {
		if (this.zoomValue > 0.6) {
			this.zoomValue = this.zoomValue - ZOOM_STEP;
			this.setZoom(this.zoomValue);
		}
	}

	@UiHandler("btnLeft")
	void onMoveLeft(ClickEvent e) {
		this.moveGrid(LEFT, 0);
	}

	@UiHandler("btnRight")
	void onMoveRight(ClickEvent e) {
		this.moveGrid(RIGHT, 0);
	}

	@UiHandler("btnTop")
	void onMoveTop(ClickEvent e) {
		this.moveGrid(0, UP);
	}

	@UiHandler("btnBelow")
	void onMoveBelow(ClickEvent e) {
		this.moveGrid(0, DOWN);
	}

	@UiHandler("btnRefresh")
	public void onRefreshClick(ClickEvent event) {
		if(this.propertiesPanel!=null){
			this.propertiesPanel.refresh();
		}
		this.refreshVisualPanel();
		this.parent.getRulePanel().onDataClick(event);
	}

	private void applyCss(Element element, String property, String value) {
		element.getStyle().setProperty(property, value);
		System.out.println("Property : " + property + " Value : " + value);
	}

	public void setZoom(double zoomValue) {
		this.applyCss(this.grid.getElement(), "transformOrigin", "top left");
		this.applyCss(this.grid.getElement(), "transform", "scale(" + zoomValue + ")");
	}

	public void moveGrid(int xValue, int yValue) {
		int marginTop = this.getValueFromCss(this.grid.getElement().getStyle().getMarginTop()) + yValue;
		if (marginTop <= 0 && marginTop >= -2000) {
			this.grid.getElement().getStyle().setMarginTop(marginTop, Unit.PX);
		}

		int marginLeft = this.getValueFromCss(grid.getElement().getStyle().getMarginLeft()) + xValue;
		if (marginLeft <= 0 && marginLeft >= -2000) {
			this.grid.getElement().getStyle().setMarginLeft(marginLeft, Unit.PX);
		}
	}

	private int getValueFromCss(String value) {
		try {
			if (value.indexOf("px") > 0) {
				return Integer.parseInt(value.substring(0, value.indexOf("px")));
			}
			else if (value.indexOf("%") > 0) {
				return Integer.parseInt(value.substring(0, value.indexOf("%")));
			}
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	public void displayProperties(PropertiesPanel properties) {

		if (properties == null) {
			this.panelProperties.clear();
			this.panelProperties.setVisible(false);
		}
		else {
			this.panelProperties.setWidget(properties);
			this.panelProperties.setVisible(true);
		}
	}

	public void displayByRule(PreparationRule rule) {
		if(!rule.getType().equals(RuleType.LIBREOFFICE)) {
			this.propertiesPanel = new PropertiesPanel(rule, this.parent);
			this.displayProperties(this.propertiesPanel);
		}
		else {
			this.displayProperties(null);
		}
	}

	public void createConnection(BoxItem start, BoxItem end, boolean firstOrLast) {

		this.controller.addWidget(start,start.getLeft(),start.getTop());
		this.dragController.makeDraggable(start);

		if(!firstOrLast) {
			this.controller.addWidget(end,end.getLeft(),end.getTop());
			this.dragController.makeDraggable(end);
			this.connections.add(this.controller.drawStraightArrowConnection(start, end));
		}
		this.controller.registerDragController(dragController);
	}

	public BoxItem boxToDelete(PreparationRule rule) {

		String txt = this.setLabel(rule);
		for (BoxItem box : this.boxes) {
			if(box.getTitle().equals(txt)) {
				return box;
			}
		}
		return null;
	}

	public void deleteBox(PreparationRule rule) {

		BoxItem box = boxToDelete(rule);

		if(box!=null){
			Connection start = startConnection(box);
			Connection end = endConnection(box);

			if(start!=null) {
				this.connections.remove(start);
				start.delete();
			}
			if(end!=null) {
				this.connections.remove(end);
				end.delete();
			}


			int index = this.boxes.indexOf(box);

			if(index>0 && index<this.boxes.size()-1 ){
				BoxItem previous = box.getPrevious();
				BoxItem next = box.getNext();

				this.createConnection(previous, next, false);
			}

			this.controller.deleteWidget(box);
			this.grid.remove(box);
			this.boxes.remove(box);
		}
	}	

	public Connection endConnection(BoxItem box) {
		FunctionShape shape = new FunctionShape(this.controller, box);
		for(Connection c: this.connections) {
			if(c.getEndShape().equals(shape)) {
				return c;
			}
		}
		return null;
	}

	public Connection startConnection(BoxItem box) {
		FunctionShape shape = new FunctionShape(this.controller, box);
		for(Connection c: this.connections) {
			if(c.getStartShape().equals(shape)) {
				return c;
			}
		}
		return null;
	}

	public void addBox(PreparationRule rule) {
		BoxItem item = new BoxItem();
		String name = this.setLabel(rule);
		item.lblName.setText(name);
		item.setTitle(item.lblName.getText());
		item.setRule(rule);
		String color = getMainRule(rule);
		item.setColor(color);

		if(!this.boxes.isEmpty()) {
			item.setLeft(this.boxes.get(this.boxes.size()-1).getLeft() + 250);
			this.boxes.get(this.boxes.size()-1).setNext(item);
			item.setPrevious(this.boxes.get(this.boxes.size()-1));
			try {
				this.createConnection(item.getPrevious(), item, false);		
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.boxes.add(item);
		this.grid.add(item);
		this.createConnection(this.boxes.get(this.boxes.size()-1), null, true);
	}

	private String getRuleText(PreparationRule rule) {
		switch(rule.getType()) {
		case LOWER_CASE:
			return "Passer en minuscule";
		case UPPER_CASE:
			return "Passer en majuscule";
		case RECODE:
			return "Recoder (" + ((PreparationRuleRecode) rule).getOriginFormula() + " => " + ((PreparationRuleRecode) rule).getResultFormula() + ")";
		case SORT:
			return "Trier";
		case NORMALIZE:
			return "Normaliser";
		case ADD_CHAR:
			return Labels.lblCnst.RuleTypeAddChar() + " (" + ((PreparationRuleAddChar)rule).getCharToAdd() + ")";
		case ROUND:
			return "Arrondir";
		case FORMAT_NUMBER:
			return "Formatter (" + ((PreparationRuleFormat)rule).getPattern() + ")";
		case MAX:
			return "Maximum (" + ((PreparationRuleMinMax)rule).getValue() + ")";
		case MIN:
			return "Minimum (" + ((PreparationRuleMinMax)rule).getValue() + ")";
		case DATE_TO_AGE:
			return Labels.lblCnst.DateToAge();
		case DEDOUBLON:
			return Labels.lblCnst.Dedupe();
		case CALC:
			return "Calculer (" + ((PreparationRuleCalc)rule).getFormula() + ")";
		case FILTER:
			return "Filtrer (" + ((PreparationRuleFilter)rule).getFilter() + ")";
		case GROUP:
			return "Grouper";
		case AFFECTER:
			return "Affectation par tanche effectuer";
		case LIBREOFFICE:
			return "Libre Office";
		}
		return null;
	}

	public boolean boxExists(BoxItem box) {
		for(PreparationRule rule: this.parent.getDataPreparation().getRules()) {
			String text = this.setLabel(rule);
			if(box.lblName.getText().equals(text)) {
				return false;
			}
		}
		return true;
	}
	public BoxItem boxToModify() {

		for(BoxItem box: this.boxes) {
			if(this.boxExists(box)) {
				return box;
			}
		}
		return null;
	}

	public void editBox(PreparationRule rule) {
		BoxItem box = this.boxToModify();
		if(box!=null) {
			String name = this.setLabel(rule);
			box.lblName.setText(name);
			box.setTitle(box.lblName.getText());
		}
	}

	public String setLabel(PreparationRule rule){

		String label = "";

		if(rule.getColumn() != null) {
			String txt = "";
			boolean first = true;
			for(DataColumn col : rule.getColumns()) {
				if(first) {
					first = false;
				}
				else {
					txt += "/";
				}
				txt += col.getColumnLabel();
			}
			label = txt;
		}
		else if(rule.getType() == RuleType.CALC){
			label = rule.getNewColumnName();
		}

		if(rule.getType() == RuleType.FILTER){
			return this.getRuleText(rule);
		}

		if(rule.getType() == RuleType.LIBREOFFICE) {
			return "Export sur " + this.getRuleText(rule);
		}
		return this.getRuleText(rule) + " sur " + label;
	}

	public void refreshVisualPanel() {

		if(this.grid != null) {
			this.grid.clear();
		}
		this.controller.showGrid(true);
		this.grid = controller.getView();

		this.boxes = new ArrayList<BoxItem>();
		this.connections = new ArrayList<Connection>();

		for(PreparationRule rule: this.parent.getDataPreparation().getRules()) {
			this.addBox(rule);
		}
	}

	public PropertiesPanel getPropertiesPanel() {
		return propertiesPanel;
	}

	public void setPropertiesPanel(PropertiesPanel propertiesPanel) {
		this.propertiesPanel = propertiesPanel;
	}

	public String getMainRule(PreparationRule rule) {

		switch(rule.getType()) {
		case LOWER_CASE:
		case UPPER_CASE:
		case RECODE:
		case SORT:
		case NORMALIZE:
		case ADD_CHAR:
			return LabelsConstants.lblCnst.RuleCatText();
			
		case ROUND:
		case FORMAT_NUMBER:
		case MAX:
		case MIN:
			return LabelsConstants.lblCnst.RuleCatNumber();
			
		case DATE_TO_AGE:
			return LabelsConstants.lblCnst.RuleCatDate();
			
		case CALC:
			return LabelsConstants.lblCnst.RuleCatCalc();
			
		case DEDOUBLON:
		case FILTER:
		case GROUP:
		case AFFECTER:
			return LabelsConstants.lblCnst.RuleCatAdvanced();
		case LIBREOFFICE: 
			return "Libre Office";
		}
		return null;
	}
}
