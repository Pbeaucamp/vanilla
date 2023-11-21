package bpm.faweb.client.dialog;

import java.util.HashMap;
import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.images.FaWebImage;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.client.utils.Space;
import bpm.faweb.shared.ParameterDTO;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PromptDialog extends AbstractDialogBox {

	private static PromptDialogUiBinder uiBinder = GWT.create(PromptDialogUiBinder.class);

	interface PromptDialogUiBinder extends UiBinder<Widget, PromptDialog> {
	}

	@UiField
	HTMLPanel contentPanel;
	
	private ListBox lstParams;
	private TextBox txtSearch;

	private Image imgSearch;

	private ParameterDTO paramName;

	public PromptDialog(final MainPanel mainCompPanel, final ParameterDTO paramName, final HashMap<String, String> parameters, HashMap<ParameterDTO, List<String>> values) {
		super("Parameter", false, true);
		this.paramName = paramName;
		
		setWidget(uiBinder.createAndBindUi(this));
		
		final VerticalPanel mainPanel = new VerticalPanel();

		Label lbl = new Label(FreeAnalysisWeb.LBL.PromptText());

		HorizontalPanel searchPanel = new HorizontalPanel();
		txtSearch = new TextBox();
		txtSearch.setWidth("200px");
		imgSearch = new Image(FaWebImage.INSTANCE.searchsmall());
		imgSearch.setTitle(FreeAnalysisWeb.LBL.SearchDim());
		imgSearch.addStyleName("pointer");

		searchPanel.add(txtSearch);
		searchPanel.add(new Space("20px", "1px"));
		searchPanel.add(imgSearch);

		HorizontalPanel txtPanel = new HorizontalPanel();
		Label lblParam = new Label(paramName.getName());
		lblParam.getElement().getStyle().setMargin(5, Unit.PX);
		
		lstParams = new ListBox(false);
		lstParams.setWidth("400px");
		lstParams.getElement().getStyle().setMargin(5, Unit.PX);

		for (String val : values.get(paramName)) {
			lstParams.addItem(val, val);
		}

		txtPanel.add(lblParam);
		txtPanel.add(lstParams);
		txtPanel.setWidth("100%");

		mainPanel.add(lbl);
		mainPanel.add(searchPanel);
		mainPanel.add(txtPanel);

		mainPanel.setSize("300px", "180px");

		contentPanel.add(mainPanel);

		imgSearch.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				FaWebService.Connect.getInstance().searchDimensions(mainCompPanel.getKeySession(), txtSearch.getText(), PromptDialog.this.paramName.getLevel(), new AsyncCallback<List<String>>() {
					public void onSuccess(List<String> result) {
						lstParams.clear();
						for (String res : result) {
							lstParams.addItem(res, res);
						}
					}

					public void onFailure(Throwable caught) {

					}
				});
			}
		});
		
		createButton(FreeAnalysisWeb.LBL.Apply(), new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				parameters.put(PromptDialog.this.paramName.getName(), lstParams.getValue(lstParams.getSelectedIndex()));
				PromptDialog.this.hide();
			}
		});
	}

	public ParameterDTO getParameter() {
		return this.paramName;
	}
}
