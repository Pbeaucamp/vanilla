package bpm.faweb.client.dialog;

import java.util.LinkedHashMap;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.client.utils.Space;
import bpm.faweb.shared.ParameterDTO;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class AddPromptsDialog extends AbstractDialogBox {

	private static AddPromptsDialogUiBinder uiBinder = GWT.create(AddPromptsDialogUiBinder.class);

	interface AddPromptsDialogUiBinder extends UiBinder<Widget, AddPromptsDialog> {
	}

	@UiField
	HTMLPanel contentPanel;
	
	@UiField
	SimplePanel panelContent;
	
	private MainPanel mainCompPanel;

	private TextBox txtName;
	private ListBox lstDims;
	private ListBox lstLevels;
	private ListBox lstCreated;

	private boolean newPrompt = false;

	private LinkedHashMap<String, LinkedHashMap<String, String>> dimensions;

	public AddPromptsDialog(final MainPanel mainCompPanel) {
		super(FreeAnalysisWeb.LBL.AddPrompt(), false, false);
		this.mainCompPanel = mainCompPanel;
		
		setWidget(uiBinder.createAndBindUi(this));

		VerticalPanel mainPanel = new VerticalPanel();

		// the content panel, with textbox and lists
		HorizontalPanel horiPanel = new HorizontalPanel();

		VerticalPanel leftPart = new VerticalPanel();
		Label lblName = new Label(FreeAnalysisWeb.LBL.Name());
		txtName = new TextBox();
		txtName.setWidth("180px");
		Label lblDims = new Label(FreeAnalysisWeb.LBL.Dimensions());
		lstDims = new ListBox(false);
		lstDims.setWidth("180px");
		Label lblLevels = new Label(FreeAnalysisWeb.LBL.Level());
		lstLevels = new ListBox(false);
		lstLevels.setWidth("180px");

		leftPart.add(lblName);
		leftPart.add(txtName);
		leftPart.add(new Space("1px", "15px"));
		leftPart.add(lblDims);
		leftPart.add(lstDims);
		leftPart.add(new Space("1px", "15px"));
		leftPart.add(lblLevels);
		leftPart.add(lstLevels);

		VerticalPanel rightPart = new VerticalPanel();
		Label lblCreated = new Label(FreeAnalysisWeb.LBL.PromptList());
		lstCreated = new ListBox(true);
		lstCreated.setSize("180px", "200px");
		for (ParameterDTO param : mainCompPanel.getInfosReport().getParameters()) {
			lstCreated.addItem(param.getName(), param.getName());
		}

		rightPart.add(lblCreated);
		rightPart.add(lstCreated);

		horiPanel.add(leftPart);
		horiPanel.add(new Space("20px", "1px"));
		horiPanel.add(rightPart);

		mainPanel.add(horiPanel);
		mainPanel.setSize("100%", "100%");

		panelContent.setWidget(mainPanel);

		lstDims.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				lstLevels.clear();
				String dimName = lstDims.getValue(lstDims.getSelectedIndex());
				LinkedHashMap<String, String> lvls = dimensions.get(dimName);
				for (String uname : lvls.keySet()) {
					lstLevels.addItem(lvls.get(uname), uname);
				}
			}
		});

		createButton(FreeAnalysisWeb.LBL.Close(), closeHander);

		showWaitPart(true);
		
		FaWebService.Connect.getInstance().getLevels(mainCompPanel.getKeySession(), new AsyncCallback<LinkedHashMap<String, LinkedHashMap<String, String>>>() {
			public void onSuccess(LinkedHashMap<String, LinkedHashMap<String, String>> result) {
				showWaitPart(false);
				
				dimensions = result;
				boolean first = true;
				for (String dimName : result.keySet()) {

					lstDims.addItem(dimName, dimName);
					if (first) {
						LinkedHashMap<String, String> lvls = result.get(dimName);
						for (String uname : lvls.keySet()) {
							lstLevels.addItem(lvls.get(uname), uname);
						}
						first = false;
					}
				}
			}

			public void onFailure(Throwable caught) {
				showWaitPart(false);
				
				caught.printStackTrace();
			}
		});
	}
	
	@UiHandler("imgAdd")
	public void onAddClick(ClickEvent event) {
		ParameterDTO param = new ParameterDTO();
		param.setName(txtName.getText());
		param.setLevel(lstLevels.getValue(lstLevels.getSelectedIndex()));
		mainCompPanel.getInfosReport().getParameters().add(param);
		newPrompt = true;
		refreshList();
	}
	
	@UiHandler("imgRemove")
	public void onRemoveClick(ClickEvent event) {
		String name = lstCreated.getValue(lstCreated.getSelectedIndex());
		ParameterDTO toRm = null;
		for (ParameterDTO param : mainCompPanel.getInfosReport().getParameters()) {
			if (param.getName().equalsIgnoreCase(name)) {
				toRm = param;
				break;
			}
		}
		mainCompPanel.getInfosReport().getParameters().remove(toRm);
		newPrompt = true;
		refreshList();
	}
	
	private ClickHandler closeHander = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			if (newPrompt) {
				mainCompPanel.showWaitPart(true);
				
				FaWebService.Connect.getInstance().addViewParameters(mainCompPanel.getKeySession(), mainCompPanel.getInfosReport().getParameters(), new AsyncCallback<Void>() {
					public void onSuccess(Void result) {
						AddPromptsDialog.this.hide();
						
						mainCompPanel.showWaitPart(false);
					}

					public void onFailure(Throwable caught) {
						AddPromptsDialog.this.hide();
						
						mainCompPanel.showWaitPart(false);
						
						caught.printStackTrace();
					}
				});
			}
			else {
				AddPromptsDialog.this.hide();
			}
		}
	};

	public void refreshList() {
		lstCreated.clear();
		for (ParameterDTO param : mainCompPanel.getInfosReport().getParameters()) {
			lstCreated.addItem(param.getName(), param.getName());
		}
	}
}
