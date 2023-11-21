package bpm.fwr.client.wizard.chart;

import bpm.fwr.api.beans.components.IChart;
import bpm.fwr.api.beans.components.OptionsFusionChart;
import bpm.fwr.api.beans.components.VanillaChartComponent;
import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.utils.color.ColorPickerDialog;
import bpm.gwt.commons.client.wizard.IGwtPage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ChartOptionsPage extends Composite implements IGwtPage {

	private static ChartOptionsPageUiBinder uiBinder = GWT.create(ChartOptionsPageUiBinder.class);

	interface ChartOptionsPageUiBinder extends UiBinder<Widget, ChartOptionsPage> {}

	@UiField
	CaptionPanel legendPanel, optionsPanel, axisPanel;

	@UiField
	Label lblXAxisName, lblYAxisName, lblBackgroundColor;

	@UiField
	TextBox txtXAxisName, txtYAxisName, txtBackgroundColor;

	@UiField
	CheckBox checkLabels, checkBorders, checkValues, checkLegend;

	@UiField
	RadioButton radioBtnBottom, radioBtnRight;

	public ChartOptionsPage(IChart chartComp) {
		initWidget(uiBinder.createAndBindUi(this));

		optionsPanel.setCaptionText(Bpm_fwr.LBLW.ChartOptions());
		legendPanel.setCaptionText(Bpm_fwr.LBLW.ChartLegend());
		axisPanel.setCaptionText(Bpm_fwr.LBLW.ChartAxisAndBackground());

		lblXAxisName.setText(Bpm_fwr.LBLW.XAxisName() + ": ");
		lblYAxisName.setText(Bpm_fwr.LBLW.YAxisName() + ": ");
		lblBackgroundColor.setText(Bpm_fwr.LBLW.BackgroundColor() + ": ");

		checkLabels.setText(Bpm_fwr.LBLW.ShowLabels());
		checkBorders.setText(Bpm_fwr.LBLW.ShowBorders());
		checkValues.setText(Bpm_fwr.LBLW.ShowValues());
		checkLegend.setText(Bpm_fwr.LBLW.ShowLegend());

		radioBtnBottom.setText(Bpm_fwr.LBLW.Bottom());
		radioBtnRight.setText(Bpm_fwr.LBLW.Right());

		txtBackgroundColor.setText("FFFFFF");
		txtBackgroundColor.getElement().getStyle().setBackgroundColor("#FFFFFF");

		txtBackgroundColor.setReadOnly(true);

		radioBtnBottom.setValue(true);
		checkLabels.setValue(true);
		checkLegend.setValue(true);
		checkValues.setValue(true);
		checkBorders.setValue(true);

		if(chartComp != null && chartComp instanceof VanillaChartComponent) {
			VanillaChartComponent ch = (VanillaChartComponent) chartComp;
			checkLabels.setValue(ch.getOptions().isShowLabels());
			checkLegend.setValue(ch.getOptions().isShowLegend());
			checkValues.setValue(ch.getOptions().isShowValues());
			checkBorders.setValue(ch.getOptions().isShowBorders());

			if(ch.getOptions().isLegendOnRight()) {
				radioBtnRight.setValue(true);
			}
			else {
				radioBtnBottom.setValue(true);
			}

			txtXAxisName.setText(ch.getOptions().getxAxisName());
			txtYAxisName.setText(ch.getOptions().getyAxisName());
			txtBackgroundColor.setText(ch.getOptions().getBackgroundColor());
			txtBackgroundColor.getElement().getStyle().setBackgroundColor("#" + ch.getOptions().getBackgroundColor());
		}
	}

	@UiHandler("txtBackgroundColor")
	public void onBackgroundColorClick(ClickEvent event) {
		String colorHex = txtBackgroundColor.getText();

		final ColorPickerDialog dlg = new ColorPickerDialog();
		dlg.setColor(colorHex);
		dlg.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				String newColorHex = dlg.getColor().toUpperCase();
				txtBackgroundColor.setText(newColorHex);
				txtBackgroundColor.getElement().getStyle().setBackgroundColor("#" + newColorHex);
			}
		});
		dlg.center();
	}

	public OptionsFusionChart getChartOptions() {
		OptionsFusionChart options = new OptionsFusionChart();
		options.setBackgroundColor(txtBackgroundColor.getText());
		options.setxAxisName(txtXAxisName.getText());
		options.setyAxisName(txtYAxisName.getText());
		options.setLegendOnRight(radioBtnRight.getValue());
		options.setShowLabels(checkLabels.getValue());
		options.setShowLegend(checkLegend.getValue());
		options.setShowValues(checkValues.getValue());
		options.setShowBorders(checkBorders.getValue());
		return options;
	}

	@Override
	public boolean canGoBack() {
		return true;
	}

	@Override
	public boolean canGoFurther() {
		return false;
	}

	@Override
	public int getIndex() {
		return 2;
	}

	@Override
	public boolean isComplete() {
		return true;
	}
}
