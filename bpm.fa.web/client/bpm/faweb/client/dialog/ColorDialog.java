package bpm.faweb.client.dialog;

import java.util.ArrayList;
import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.panels.center.IColor;
import bpm.faweb.client.utils.ColorPicker;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ColorDialog extends AbstractDialogBox {

	private static ColorDialogUiBinder uiBinder = GWT.create(ColorDialogUiBinder.class);

	interface ColorDialogUiBinder extends UiBinder<Widget, ColorDialog> {
	}
	
	interface MyStyle extends CssResource {
		String name();
		String color();
		String minimum();
		String maximum();
		String txt();
	}
	
	@UiField
	MyStyle style;

	@UiField
	HTMLPanel contentPanel;

	private IColor colorManager;
	
	private TextBox nameArea;
	private TextBox minArea;
	private TextBox maxArea;
	
	private String hexaColor;

	public ColorDialog(IColor colorManager) {
		super(FreeAnalysisWeb.LBL.DialogChoiceMapOption(), false, false);
		this.colorManager = colorManager;
		
		setWidget(uiBinder.createAndBindUi(this));

		// Create a table to layout the content
		VerticalPanel dialogContents = new VerticalPanel();
		dialogContents.setSpacing(4);
		contentPanel.add(dialogContents);

		HTML name = new HTML(FreeAnalysisWeb.LBL.Name());
		name.addStyleName(style.name());
		
		nameArea = new TextBox();
		nameArea.addStyleName(style.txt());

		HorizontalPanel namePanel = new HorizontalPanel();
		namePanel.add(name);
		namePanel.add(nameArea);

		HTML hexa = new HTML(FreeAnalysisWeb.LBL.Color());
		hexa.addStyleName(style.color());

		final ColorPicker colorPicker = new ColorPicker();
		colorPicker.getTextBox().addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				hexaColor = colorPicker.getTextBox().getText();
			}
		});

		HorizontalPanel hexPanel = new HorizontalPanel();
		hexPanel.add(hexa);
		hexPanel.add(colorPicker);

		HTML min = new HTML(FreeAnalysisWeb.LBL.Minimum());
		min.addStyleName(style.minimum());
		
		minArea = new TextBox();
		minArea.addStyleName(style.txt());
		
		HorizontalPanel minPanel = new HorizontalPanel();
		minPanel.add(min);
		minPanel.add(minArea);

		HTML max = new HTML(FreeAnalysisWeb.LBL.Maximum());
		max.addStyleName(style.maximum());
		
		maxArea = new TextBox();
		maxArea.addStyleName(style.txt());
		
		HorizontalPanel maxPanel = new HorizontalPanel();
		maxPanel.add(max);
		maxPanel.add(maxArea);
		
		dialogContents.add(namePanel);
		dialogContents.add(hexPanel);
		dialogContents.add(minPanel);
		dialogContents.add(maxPanel);
		
		createButtonBar(FreeAnalysisWeb.LBL.Apply(), confirmHandler, FreeAnalysisWeb.LBL.Cancel(), cancelHandler);
	}
	
	private ClickHandler confirmHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			ColorDialog.this.hide();

			List<String> colorTemp = new ArrayList<String>();

			colorTemp.add(nameArea.getText());
			colorTemp.add(hexaColor);
			colorTemp.add(minArea.getText());
			colorTemp.add(maxArea.getText());

			colorManager.addRow(colorTemp, true);
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			ColorDialog.this.hide();
		}
	};
}
