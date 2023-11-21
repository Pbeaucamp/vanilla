package bpm.fd.web.client.panels.properties;

import java.util.HashMap;
import java.util.Map;

import bpm.fd.api.core.model.components.definition.chart.ChartNature;
import bpm.fd.core.component.ChartComponent;
import bpm.fd.web.client.I18N.Labels;
import bpm.fd.web.client.utils.ChartTypeHelper;
import bpm.gwt.commons.client.custom.CustomCheckbox;
import bpm.gwt.commons.client.custom.ListBoxWithButton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ChartTypeProperties extends Composite {

	public enum TypeChart {
		PIE(0), COLUMN(1), BAR(2), LINE(3), RADAR(4), COMBINATION(5), AREA(6);//, FUNNEL(4), PYRAMID(5), MARIMEKO(6);

		private int type;

		private static Map<Integer, TypeChart> map = new HashMap<Integer, TypeChart>();
		static {
			for (TypeChart serverType : TypeChart.values()) {
				map.put(serverType.getType(), serverType);
			}
		}

		private TypeChart(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static TypeChart valueOf(int type) {
			return map.get(type);
		}
	}

	public enum TypeDisplay {
		CLASSIC(0), STACKED(1);

		private int type;

		private static Map<Integer, TypeDisplay> map = new HashMap<Integer, TypeDisplay>();
		static {
			for (TypeDisplay serverType : TypeDisplay.values()) {
				map.put(serverType.getType(), serverType);
			}
		}

		private TypeDisplay(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static TypeDisplay valueOf(int type) {
			return map.get(type);
		}
	}

	private static ChartDataPropertiesUiBinder uiBinder = GWT.create(ChartDataPropertiesUiBinder.class);

	interface ChartDataPropertiesUiBinder extends UiBinder<Widget, ChartTypeProperties> {
	}

	@UiField
	ListBoxWithButton<String> lstTypesChart, lstTypesDisplay;

	@UiField
	CustomCheckbox check3D;

	@UiField
	Image imgChart;

	private ChartComponent component;

	private PropertiesPanel propertiesPanel;

	public ChartTypeProperties(ChartComponent component, PropertiesPanel propertiesPanel) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.component = component;
		this.propertiesPanel = propertiesPanel;

		
		for (TypeChart type : TypeChart.values()) {
			lstTypesChart.addItem(findName(type), String.valueOf(type.getType()));
		}

		for (TypeDisplay type : TypeDisplay.values()) {
			lstTypesDisplay.addItem(findName(type), String.valueOf(type.getType()));
		}

		if (component != null && component.getNature() != null) {
			buildContent(component.getNature());
		}
	}
	
	@UiHandler("lstTypesChart")
	public void onChangeChart(ChangeEvent event) {
		updateUi();
	}
	
	@UiHandler("lstTypesDisplay")
	public void onChangeDisplay(ChangeEvent event) {
		updateUi();
	}

	@UiHandler("check3D")
	public void onCheck3D(ClickEvent event) {
		updateUi();
	}

	private void buildContent(ChartNature nature) {
		TypeChart typeChart = ChartTypeHelper.findType(nature);
		lstTypesChart.setSelectedIndex(typeChart.getType());
		lstTypesDisplay.setSelectedIndex(nature.isStacked() ? 1 : 0);
		check3D.setValue(nature.is3D());
		
		updateUi();
	}

	private String findName(TypeChart type) {
		switch (type) {
		case PIE:
			return Labels.lblCnst.Pie();
		case COLUMN:
			return Labels.lblCnst.Column();
		case BAR:
			return Labels.lblCnst.Bar();
		case LINE:
			return Labels.lblCnst.Line();
//		case FUNNEL:
//			return Labels.lblCnst.Funnel();
//		case PYRAMID:
//			return Labels.lblCnst.Pyramid();
//		case MARIMEKO:
//			return Labels.lblCnst.Marimeko();
		case RADAR:
			return Labels.lblCnst.Radar();
		case COMBINATION:
			return Labels.lblCnst.Combination();
		case AREA:
			return Labels.lblCnst.Area();

		default:
			break;
		}
		return Labels.lblCnst.Unknown();
	}

	private String findName(TypeDisplay type) {
		switch (type) {
		case CLASSIC:
			return Labels.lblCnst.Classic();
		case STACKED:
			return Labels.lblCnst.Stacked();

		default:
			break;
		}
		return Labels.lblCnst.Unknown();
	}

	public void updateUi() {
		TypeChart typeChart = getSelectedTypeChart();
		TypeDisplay typeDisplay = getSelectedTypeDisplay();
		boolean is3D = is3D();

		Object img = ChartTypeHelper.findImage(typeChart, typeDisplay, is3D);
		if(img instanceof ImageResource) {
			this.imgChart.setUrl(((ImageResource)img).getSafeUri());
		}
		else {
			this.imgChart.setUrl(((DataResource)img).getSafeUri());
		}
		
		lstTypesDisplay.setVisible(canBeStackable(typeChart));
//		check3D.setVisible(canBe3D(typeChart));
		
		component.setNature(ChartTypeHelper.getChartNature((ChartComponent) component, typeChart, typeDisplay, is3D));
		if(propertiesPanel != null) {
			propertiesPanel.refreshOptions(component);
		}
	}

	private boolean canBeStackable(TypeChart typeChart) {
		return typeChart == TypeChart.COLUMN ||  typeChart == TypeChart.BAR;
	}

	private boolean canBe3D(TypeChart typeChart) {
		return typeChart == TypeChart.PIE || typeChart == TypeChart.COLUMN ||  typeChart == TypeChart.BAR ||  typeChart == TypeChart.COMBINATION;
	}

	public TypeChart getSelectedTypeChart() {
		int type = Integer.parseInt(lstTypesChart.getSelectedObject());
		return TypeChart.valueOf(type);
	}

	public TypeDisplay getSelectedTypeDisplay() {
		int type = Integer.parseInt(lstTypesDisplay.getSelectedObject());
		return TypeDisplay.valueOf(type);
	}

	public boolean is3D() {
//		return check3D.getValue();
		return false;
	}
}
