package bpm.fwr.client.wizard.map;

import java.util.ArrayList;
import java.util.List;

import bpm.fwr.api.beans.components.OptionsFusionMap;
import bpm.fwr.api.beans.components.VanillaMapComponent;
import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.images.WysiwygImage;
import bpm.fwr.client.utils.color.ColorPickerDialog;
import bpm.fwr.client.utils.color.ColorUtils;
import bpm.fwr.client.widgets.LabelCell;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class VanillaMapsDefinitionPage extends Composite implements IGwtPage {

	private static VanillaMapsDefinitionPageUiBinder uiBinder = GWT
			.create(VanillaMapsDefinitionPageUiBinder.class);

	interface VanillaMapsDefinitionPageUiBinder extends
			UiBinder<Widget, VanillaMapsDefinitionPage> {
	}
	
	interface MyStyle extends CssResource {
		String table();
		String lblColor();
	}

	@UiField
	Label lblFontSize, lblFontColor, lblBackgroundColor;
	
	@UiField
	TextBox txtFontSize, txtFontColor, txtBackgroundColor;
	
	@UiField(provided = true)
	PushButton btnAddColor;
	
	@UiField
	MyStyle style;
	
	@UiField
	CheckBox checkLabels, checkShadows, checkTooltips, checkLegend;
	
	@UiField
	RadioButton radioBtnBottom, radioBtnRight;
	
	@UiField
	CaptionPanel fontPanel, legendPanel, optionsPanel, colorPanel;
	
	private DataGrid<ColorRange> dataGrid;
	private List<ColorRange> colors;
	private ListDataProvider<ColorRange> dataProvider;

	public VanillaMapsDefinitionPage(VanillaMapComponent mapComponent) {
		btnAddColor = new PushButton(new Image(WysiwygImage.INSTANCE.addColor()));
		initWidget(uiBinder.createAndBindUi(this));
		
		colorPanel.setCaptionText(Bpm_fwr.LBLW.MapColor());
		optionsPanel.setCaptionText(Bpm_fwr.LBLW.MapOptions());
		legendPanel.setCaptionText(Bpm_fwr.LBLW.MapLegend());
		fontPanel.setCaptionText(Bpm_fwr.LBLW.MapFontAndBackground());
		
		lblFontSize.setText(Bpm_fwr.LBLW.FontSize() + ": ");
		lblFontColor.setText(Bpm_fwr.LBLW.FontColor() + ": ");
		lblBackgroundColor.setText(Bpm_fwr.LBLW.BackgroundColor() + ": ");
		
		checkLabels.setText(Bpm_fwr.LBLW.ShowLabels());
		checkShadows.setText(Bpm_fwr.LBLW.ShowShadows());
		checkTooltips.setText(Bpm_fwr.LBLW.ShowTooltips());
		checkLegend.setText(Bpm_fwr.LBLW.ShowLegend());
		
		radioBtnBottom.setText(Bpm_fwr.LBLW.Bottom());
		radioBtnRight.setText(Bpm_fwr.LBLW.Right());
		
		txtFontSize.setText("9");
		txtFontColor.setText("000000");
		txtFontColor.getElement().getStyle().setBackgroundColor("#000000");
		txtBackgroundColor.setText("FFFFFF");
		txtBackgroundColor.getElement().getStyle().setBackgroundColor("#FFFFFF");
		
		txtFontColor.setReadOnly(true);
		txtBackgroundColor.setReadOnly(true);
		radioBtnBottom.setValue(true);
		checkLabels.setValue(true);
		checkLegend.setValue(true);
		checkShadows.setValue(true);
		checkTooltips.setValue(true);
		
		dataGrid = createGridData();
	    colorPanel.add(dataGrid);

		if(colors == null){
			this.colors = new ArrayList<ColorRange>();
		}
		
	    if(mapComponent != null){

	    	if(mapComponent.getColors() != null){
			    for(ColorRange color : mapComponent.getColors()){
			    	addRow(color);
			    }
	    	}
		    
		    checkLabels.setValue(mapComponent.getOptions().isShowLabels());
			checkLegend.setValue(mapComponent.getOptions().isShowLegend());
			checkShadows.setValue(mapComponent.getOptions().isShowShadow());
			checkTooltips.setValue(mapComponent.getOptions().isShowTooltips());
		    
			if(mapComponent.getOptions().isLegendOnRight()){
				radioBtnRight.setValue(true);
			}
			else {
				radioBtnBottom.setValue(true);
			}
			
			txtFontSize.setText(mapComponent.getOptions().getFontSize() + "");
			
			txtFontColor.setText(mapComponent.getOptions().getFontColor());
			txtFontColor.getElement().getStyle().setBackgroundColor("#" + mapComponent.getOptions().getFontColor());
			
			txtBackgroundColor.setText(mapComponent.getOptions().getBackgroundColor());
			txtBackgroundColor.getElement().getStyle().setBackgroundColor("#" + mapComponent.getOptions().getBackgroundColor());
	    }
	}
	
	@UiHandler("btnAddColor")
	public void onAddClick(ClickEvent event){
		String name = "Color " + colors.size();
		int min = 0;
		int max = 1000;
		for(ColorRange color : colors){
			if(min < color.getMax()){
				min = color.getMax() + 1;
				max = min + 999;
			}
		}
		
		java.util.Random alea = new java.util.Random(System.currentTimeMillis());
		int r = Math.abs(alea.nextInt()) % 256;
		int g = Math.abs(alea.nextInt()) % 256;
		int b = Math.abs(alea.nextInt()) % 256;
		
		String hex = ColorUtils.rgb2hex(r, g, b).toUpperCase();
		
		ColorRange newColor = new ColorRange(name, hex, min, max);
		colors.add(newColor);
		refreshColors();
	}
	
	@UiHandler("txtFontColor")
	public void onFontColorClick(ClickEvent event){
		String colorHex = txtFontColor.getText();
		
		final ColorPickerDialog dlg = new ColorPickerDialog();
		dlg.setColor(colorHex);
		dlg.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				String newColorHex = dlg.getColor().toUpperCase();
				txtFontColor.setText(newColorHex);
				txtFontColor.getElement().getStyle().setBackgroundColor("#" + newColorHex);
			}
		});
		dlg.center();
	}
	
	@UiHandler("txtBackgroundColor")
	public void onBackgroundColorClick(ClickEvent event){
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

	private void showColorPicker(final ColorRange color) {
		final ColorPickerDialog dlg = new ColorPickerDialog();
		dlg.setColor(color.getHex());
		dlg.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				String newColorHex = dlg.getColor().toUpperCase();
				color.setHex(newColorHex);
				refreshColors();
			}
		});
		dlg.center();
	}

	private void addRow(ColorRange colorTemp){
		colors.add(colorTemp);
		refreshColors();
	}
	
	  /**
	   * Remove a row from the flex table.
	   */
	private void removeRow(ColorRange color) {
		colors.remove(color);
		refreshColors();
	}
	
	private DataGrid<ColorRange> createGridData() {
		Column<ColorRange, String> nameColumn = new Column<ColorRange, String>(new EditTextCell()) {

			@Override
			public String getValue(ColorRange object) {
				return object.getName();
			}
		};
		nameColumn.setFieldUpdater(new FieldUpdater<ColorRange, String>() {

			@Override
			public void update(int index, ColorRange object, String value) {
				object.setName(value);
				refreshColors();
			}
	    });

		Column<ColorRange, String> colorColumn = new Column<ColorRange, String>(new LabelCell()) {

			@Override
			public String getValue(ColorRange object) {
				return object.getHex();
			}
		};
		colorColumn.setFieldUpdater(new FieldUpdater<ColorRange, String>() {

			@Override
			public void update(int index, ColorRange object, String value) {
				showColorPicker(object);
			}
	    });

		Column<ColorRange, String> minColumn = new Column<ColorRange, String>(new EditTextCell()) {

			@Override
			public String getValue(ColorRange object) {
				return object.getMin() + "";
			}
		};
		minColumn.setFieldUpdater(new FieldUpdater<ColorRange, String>() {

			@Override
			public void update(int index, ColorRange object, String value) {
				int min = 0;
				try {
					min = Integer.parseInt(value);
				} catch (Exception e) {
					e.printStackTrace();
				}
				object.setMin(min);
				refreshColors();
			}
	    });

		Column<ColorRange, String> maxColumn = new Column<ColorRange, String>(new EditTextCell()) {

			@Override
			public String getValue(ColorRange object) {
				return object.getMax() + "";
			}
		};
		maxColumn.setFieldUpdater(new FieldUpdater<ColorRange, String>() {

			@Override
			public void update(int index, ColorRange object, String value) {
				int max = 0;
				try {
					max = Integer.parseInt(value);
				} catch (Exception e) {
					e.printStackTrace();
				}
				object.setMax(max);
				refreshColors();
			}
	    });
		
		Column<ColorRange, String> removeColumn = new Column<ColorRange, String>(new ButtonCell()) {

			@Override
			public String getValue(ColorRange object) {
				return "Del";
			}
		};
		removeColumn.setFieldUpdater(new FieldUpdater<ColorRange, String>() {

			@Override
			public void update(int index, ColorRange object, String value) {
				removeRow(object);
				refreshColors();
			}
	    });

		DataGrid<ColorRange> dataGrid = new DataGrid<ColorRange>(10000);
		dataGrid.setHeight("100%");
		dataGrid.setWidth("100%");
		dataGrid.addColumn(nameColumn, "Name");
		dataGrid.addColumn(colorColumn, "Color");
		dataGrid.addColumn(minColumn, "Min");
		dataGrid.addColumn(maxColumn, "Max");
		dataGrid.addColumn(removeColumn, "");
		dataGrid.setEmptyTableWidget(new Label("No Color"));

	    dataProvider = new ListDataProvider<ColorRange>();
	    dataProvider.addDataDisplay(dataGrid);
	    
	    return dataGrid;
	}

	private void refreshColors() {
		if(colors != null){
			dataProvider.setList(colors);
		}
		else {
			dataProvider.setList(new ArrayList<ColorRange>());
		}
	}
	
	public List<ColorRange> getColors(){
		return colors;
	}
	
	public OptionsFusionMap getMapOptions(){
		OptionsFusionMap options = new OptionsFusionMap();
		options.setBackgroundColor(txtBackgroundColor.getText());
		options.setFontColor(txtFontColor.getText());
		try {
			options.setFontSize(Integer.parseInt(txtFontSize.getText()));
		} catch (Exception e) {
			e.printStackTrace();
			options.setFontSize(9);
		}
		options.setLegendOnRight(radioBtnRight.getValue());
		options.setShowLabels(checkLabels.getValue());
		options.setShowLegend(checkLegend.getValue());
		options.setShowShadow(checkShadows.getValue());
		options.setShowTooltips(checkTooltips.getValue());
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
		return 1;
	}

	@Override
	public boolean isComplete() {
		return true;
	}
}
