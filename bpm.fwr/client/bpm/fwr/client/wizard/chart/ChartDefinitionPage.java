package bpm.fwr.client.wizard.chart;

import bpm.fwr.api.beans.components.ChartComponent;
import bpm.fwr.api.beans.components.ChartType;
import bpm.fwr.api.beans.components.ChartTypes;
import bpm.fwr.api.beans.components.IChart;
import bpm.fwr.api.beans.components.OptionsFusionChart;
import bpm.fwr.api.beans.components.VanillaChartComponent;
import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.images.WysiwygImage;
import bpm.fwr.client.utils.ChartUtils;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.client.wizard.IGwtWizard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ChartDefinitionPage extends Composite implements IGwtPage {
	private static final String CSS_IMG_CHART = "imgChart";

	private static ChartDefinitionPageUiBinder uiBinder = GWT.create(ChartDefinitionPageUiBinder.class);

	interface ChartDefinitionPageUiBinder extends UiBinder<Widget, ChartDefinitionPage> {
	}

	interface MyStyle extends CssResource {
		String imgChart();
	}

	@UiField
	Label lblTitle, lblTypes, lblImg, lblChartTitle, lblDimension;

	@UiField
	ListBox lstTypes, lstDimension;

	@UiField
	Image typeImg;

	@UiField
	TextBox txtChartTitle;

	@UiField
	RadioButton /*radioBtnClassic, radioBtnFusion,*/ radioBtnStacked, radioBtnSBS;

	@UiField
	CheckBox checkGlassEnabled;

	@UiField
	HTMLPanel panelFusionOptions;

	@UiField
	MyStyle style;

	private IGwtWizard parent;
	private int index;
	private ChartType[] chartClassics;
	private ChartType[] chartFusions;

	public ChartDefinitionPage(IGwtWizard parent, int index, IChart component) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.index = index;

		lblTitle.setText(Bpm_fwr.LBLW.ChartSelectTypeOp());
		lblTypes.setText(Bpm_fwr.LBLW.ChartSelectType());
		lblImg.setText(Bpm_fwr.LBLW.ChartSelectImg());
		lblChartTitle.setText(Bpm_fwr.LBLW.ChartSelectChartTitle());

//		radioBtnClassic.setText(Bpm_fwr.LBLW.ClassicChart());
//		radioBtnFusion.setText(Bpm_fwr.LBLW.FusionChart());

		radioBtnSBS.setText(Bpm_fwr.LBLW.SideBySide());
		radioBtnStacked.setText(Bpm_fwr.LBLW.StackedColumn());

		checkGlassEnabled.setText(Bpm_fwr.LBLW.EnableStyleGlass());

		chartClassics = ChartType.CHART_TYPES_CLASSIC;
		chartFusions = ChartType.CHART_TYPES_FUSION;

		lstTypes.setVisibleItemCount(10);

		lblDimension.setText(Bpm_fwr.LBLW.Dimension());
		lstDimension.addItem("2D");
		lstDimension.addItem("3D");

		typeImg.setResource(WysiwygImage.INSTANCE.barchart());
		typeImg.addStyleName(style.imgChart());
		typeImg.addStyleName(CSS_IMG_CHART);

		if (component instanceof VanillaChartComponent) {
			txtChartTitle.setText(component.getChartTitle());

			VanillaChartComponent comp = (VanillaChartComponent) component;

//			radioBtnFusion.setValue(true);
			loadFusionPart();
			for (int i = 0; i < chartFusions.length; i++) {
				if (comp.getChartType().getType().getType().equals(chartFusions[i].getType().getType())) {
					lstTypes.setSelectedIndex(i);
					break;
				}
			}

			panelFusionOptions.setVisible(true);

			if (comp.getOptions().is3D()) {
				lstDimension.setSelectedIndex(1);
			}

			if (comp.getOptions().isGlassEnabled()) {
				checkGlassEnabled.setValue(true);
			}

			if (comp.getOptions().isStacked()) {
				radioBtnStacked.setValue(true);
			}
			else {
				radioBtnSBS.setValue(true);
			}

			refreshImg();
		}
		else if (component instanceof ChartComponent) {
			ChartComponent comp = (ChartComponent) component;

//			radioBtnClassic.setValue(true);
			loadClassicPart();

			panelFusionOptions.setVisible(false);
			radioBtnSBS.setValue(true);

			txtChartTitle.setText(component.getChartTitle());

			for (int i = 0; i < chartClassics.length; i++) {
				if (comp.getChartType().getType().equals(chartClassics[i].getType().getType())) {
					lstTypes.setSelectedIndex(i);
					break;
				}
			}

			refreshImg();
		}
		else {
//			radioBtnClassic.setValue(true);
			loadFusionPart();

			panelFusionOptions.setVisible(true);
			radioBtnSBS.setValue(true);
			refreshImg();
		}
	}

	private void refreshImg() {
		ChartType chart = getSelectedChartType();

//		if (radioBtnClassic.getValue()) {
//			typeImg.setResource(ChartUtils.getImageForChart(chart.getType()));
//		}
//		else {
			OptionsFusionChart chartOptions = getChartOptions();
			typeImg.setResource(ChartUtils.getImageForChart(chart.getType(), chartOptions));
//		}
	}

//	@UiHandler("radioBtnFusion")
//	public void onRadioBtnFusionClick(ValueChangeEvent<Boolean> e) {
//		loadFusionPart();
//		refreshImg();
//		panelFusionOptions.setVisible(true);
//	}
//
//	@UiHandler("radioBtnClassic")
//	public void onRadioBtnClassicClick(ValueChangeEvent<Boolean> e) {
//		loadClassicPart();
//		refreshImg();
//		panelFusionOptions.setVisible(false);
//	}

	@UiHandler("checkGlassEnabled")
	public void onCheckedGlassClick(ValueChangeEvent<Boolean> e) {
		refreshImg();
	}

	@UiHandler("radioBtnSBS")
	public void onRadioBtnSBS(ValueChangeEvent<Boolean> e) {
		refreshImg();
	}

	@UiHandler("radioBtnStacked")
	public void onRadioBtnStacked(ValueChangeEvent<Boolean> e) {
		refreshImg();
	}

	@UiHandler("lstDimension")
	public void onDimensionChange(ChangeEvent event) {
		if (lstDimension.getValue(lstDimension.getSelectedIndex()).equals("3D")) {
			checkGlassEnabled.setValue(false);
			checkGlassEnabled.setEnabled(false);
		}
		else {
			refreshFusionPart(false);
		}
		refreshImg();
	}

	@UiHandler("lstTypes")
	public void onTypeChange(ChangeEvent event) {
		refreshFusionPart(true);
		refreshImg();
	}

	@UiHandler("txtChartTitle")
	public void onTypeChange(ValueChangeEvent<String> event) {
		parent.updateBtn();
	}

	private void refreshFusionPart(boolean init) {
		ChartType chart = getSelectedChartType();
		if (chart.getType() == ChartTypes.BAR) {
			updateStates(true, true, true, init);
		}
		else if (chart.getType() == ChartTypes.COLUMN) {
			updateStates(true, true, true, init);
		}
		else if (chart.getType() == ChartTypes.LINE) {
			updateStates(false, false, false, init);
		}
		else if (chart.getType() == ChartTypes.PIE) {
			updateStates(true, false, false, init);
		}
		else if (chart.getType() == ChartTypes.DOUGHNUT) {
			updateStates(true, false, false, init);
		}
		else if (chart.getType() == ChartTypes.RADAR) {
			updateStates(false, false, false, init);
		}
	}

	private void updateStates(boolean dimension, boolean stacked, boolean glass, boolean init) {
		radioBtnSBS.setEnabled(stacked);
		radioBtnStacked.setEnabled(stacked);
		lstDimension.setEnabled(dimension);
		checkGlassEnabled.setEnabled(glass);

		if (init) {
			radioBtnSBS.setValue(true);
			lstDimension.setSelectedIndex(0);
			checkGlassEnabled.setValue(false);
		}
	}

	private void loadFusionPart() {
		lstTypes.clear();
		for (ChartType type : chartFusions) {
			if (!type.isClassic()) {
				lstTypes.addItem(type.getName(), type.getType().getType());
			}
		}
		lstTypes.setSelectedIndex(0);
	}

	private void loadClassicPart() {
		lstTypes.clear();
		for (ChartType type : chartClassics) {
			if (type.isClassic()) {
				lstTypes.addItem(type.getName(), type.getType().getType());
			}
		}
		lstTypes.setSelectedIndex(0);
	}

	public String getChartTitle() {
		return txtChartTitle.getText().toString();
	}

//	public ChartOperations getSelectedChartOperation() {
//		String ope = lstOperation.getValue(lstOperation.getSelectedIndex());
//		if (ope.equals(ChartOperations.SUM.getOperation())) {
//			return ChartOperations.SUM;
//		}
//		else if (ope.equals(ChartOperations.COUNT.getOperation())) {
//			return ChartOperations.COUNT;
//		}
//		return ChartOperations.SUM;
//	}

	@Override
	public boolean canGoBack() {
		return false;
	}

	@Override
	public boolean canGoFurther() {
		return isComplete() ? true : false;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public boolean isComplete() {
		return txtChartTitle.getText().equals("") ? false : true;
	}

	public ChartType getSelectedChartType() {
		String type = lstTypes.getValue(lstTypes.getSelectedIndex());
//		if (radioBtnClassic.getValue()) {
//			for (ChartType chart : chartClassics) {
//				if (type.equals(chart.getType().getType())) {
//					return chart;
//				}
//			}
//		}
//		else {
			for (ChartType chart : chartFusions) {
				if (type.equals(chart.getType().getType())) {
					return chart;
				}
			}
//		}

		return null;
	}

	public OptionsFusionChart getChartOptions() {
		boolean is3D = lstDimension.getValue(lstDimension.getSelectedIndex()).equals("3D");
		boolean isStacked = radioBtnStacked.getValue();
		boolean isGlassEnabled = checkGlassEnabled.getValue();

		OptionsFusionChart chartOptions = new OptionsFusionChart();
		chartOptions.set3D(is3D);
		chartOptions.setGlassEnabled(isGlassEnabled);
		chartOptions.setStacked(isStacked);
//		chartOptions.setChartOperation(lstOperation.getItemText(lstOperation.getSelectedIndex()));

		return chartOptions;
	}
}
