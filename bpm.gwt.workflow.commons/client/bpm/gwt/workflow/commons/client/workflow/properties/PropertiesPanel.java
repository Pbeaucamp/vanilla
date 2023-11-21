package bpm.gwt.workflow.commons.client.workflow.properties;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.commons.client.custom.TextAreaHolderBox;
import bpm.gwt.commons.client.custom.TextHolderBox;
import bpm.gwt.commons.client.loading.CompositeWaitPanel;
import bpm.gwt.commons.client.panels.upload.FileUploadWidget;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.workflow.commons.client.IManager;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.NameChanger;
import bpm.gwt.workflow.commons.client.NameChecker;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.images.Images;
import bpm.gwt.workflow.commons.client.utils.ValueChangeHandlerWithError;
import bpm.gwt.workflow.commons.client.utils.VariableTextAreaHolderBox;
import bpm.gwt.workflow.commons.client.utils.VariableTextHolderBox;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;

public abstract class PropertiesPanel<T> extends CompositeWaitPanel {

	public enum WidgetWidth {
		PCT, SMALL_PX, LARGE_PX, PORT;
	}

	private static PropertiesPanelUiBinder uiBinder = GWT.create(PropertiesPanelUiBinder.class);

	interface PropertiesPanelUiBinder extends UiBinder<Widget, PropertiesPanel<?>> {
	}

	protected interface MyStyle extends CssResource {
		String txt();

		String txtArea();

		String lst();

		String check();

		String refresh();

		String valid();

		String validNoMargin();

		String error();

		String lstWithoutRefresh();

		String lstWithRefresh();

		String success();

		String panelContentWithoutVariable();

		String panelVariables();

		String panelVariablesDialog();

		String panelContentWithVariable();

		String panelContentWithVariableDialog();

		String warning();

		String simplePanel();

		String btn();

		String panelForButton();

		String lblHelp();

		String lblListDisplay();

		String simplePanelSize();
		
		String browsePanel();
		String browseButton();
		String browseTextBox();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelVariablesParent, panelVariables, panelContent;

	@UiField
	Label tabVariables, tabParameters;
	
//	private IWorkflowAppManager appManager;
	private IResourceManager resourceManager;

	private NameChecker nameChecker;
	private NameChanger nameChanger;

	private PropertiesText txtName;

	private int id;

	private List<VariableTextHolderBox> variableTexts = new ArrayList<VariableTextHolderBox>();
	private List<VariableTextAreaHolderBox> variableAreaTexts = new ArrayList<VariableTextAreaHolderBox>();
	private PanelWithVariables panelWithVariables;

	private boolean showVariables = true;

	public PropertiesPanel(IResourceManager resourceManager, String nameLabel, WidgetWidth widgetWidth, int id, String name, boolean showVariables, boolean isDialog) {
		initWidget(uiBinder.createAndBindUi(this));
		this.resourceManager = resourceManager;
		this.setId(id);

		txtName = addText(nameLabel, name, widgetWidth, false);
		txtName.addValueChangeHandler(txtName, nameHandler);

		if (showVariables) {
			tabVariables.addStyleName(VanillaCSS.TAB_SELECTED);

			panelVariablesParent.setVisible(true);
			if (isDialog) {
				panelVariablesParent.addStyleName(style.panelVariablesDialog());
				panelContent.addStyleName(style.panelContentWithVariableDialog());
			}
			else {
				panelVariablesParent.addStyleName(style.panelVariables());
				panelContent.addStyleName(style.panelContentWithVariable());
			}

			loadItems(resourceManager.getVariables(), resourceManager.getParameters(), true);
		}
		else {
			panelVariablesParent.setVisible(false);
			panelContent.addStyleName(style.panelContentWithoutVariable());
		}
	}

	private void loadItems(List<Variable> variables, List<Parameter> parameters, boolean refresh) {
		if (refresh) {
			for (VariableTextHolderBox txt : variableTexts) {
				txt.setVariables(variables);
				txt.setParameters(parameters);
			}

			for (VariableTextAreaHolderBox txt : variableAreaTexts) {
				txt.setVariables(variables);
				txt.setParameters(parameters);
			}

			if (panelWithVariables != null) {
				panelWithVariables.setVariables(variables, parameters);
			}
		}

		panelVariables.clear();
		if (showVariables) {
			if (variables != null) {
				for (Variable variable : variables) {
					panelVariables.add(new VariableItem(variable));
				}
			}
		}
		else {
			if (parameters != null) {
				for (Parameter parameter : parameters) {
					panelVariables.add(new VariableItem(parameter));
				}
			}
		}
	}

	@Override
	public void setWidth(String width) {
		panelContent.setWidth(width);
	}

	@UiHandler("tabVariables")
	public void onVariablesClick(ClickEvent event) {
		this.showVariables = true;
		tabVariables.addStyleName(VanillaCSS.TAB_SELECTED);
		tabParameters.removeStyleName(VanillaCSS.TAB_SELECTED);

		loadItems(resourceManager.getVariables(), resourceManager.getParameters(), false);
	}

	@UiHandler("tabParameters")
	public void onParametersClick(ClickEvent event) {
		this.showVariables = false;
		tabParameters.addStyleName(VanillaCSS.TAB_SELECTED);
		tabVariables.removeStyleName(VanillaCSS.TAB_SELECTED);

		loadItems(resourceManager.getVariables(), resourceManager.getParameters(), false);
	}

	@UiHandler("btnRefresh")
	public void onRefreshClick(ClickEvent event) {
		resourceManager.loadVariables(this, new IManager<Variable>() {

			@Override
			public void loadResources() {
			}

			@Override
			public void loadResources(final List<Variable> variables) {
				resourceManager.loadParameters(PropertiesPanel.this, new IManager<Parameter>() {

					@Override
					public void loadResources() {
					}

					@Override
					public void loadResources(List<Parameter> parameters) {
						loadItems(variables, parameters, true);
					}
				});
			}
		});
	}

	public void setNameChanger(NameChanger nameChanger) {
		this.nameChanger = nameChanger;
	}

	public void setNameChecker(NameChecker nameChecker) {
		this.nameChecker = nameChecker;
	}

	protected void checkName(PropertiesText text, String value) {
		if (value.isEmpty()) {
			if (nameChanger != null) {
				nameChanger.changeName(value, false);
			}
			text.setTxtError(LabelsCommon.lblCnst.NeedName());
			return;
		}
		else if (nameChecker != null && nameChecker.checkIfNameTaken(getId(), value)) {
			if (nameChanger != null) {
				nameChanger.changeName(value, false);
			}
			text.setTxtError(LabelsCommon.lblCnst.NameTaken());
			return;
		}

		if (nameChanger != null) {
			nameChanger.changeName(value, true);
		}
		text.setTxtError("");
	}

	public PropertiesText getTxtName() {
		return txtName;
	}

	protected void addPanel(Widget widget) {
		panelContent.add(widget);
	}

	protected PropertiesListBox addList(String displayLabel, List<ListItem> items, WidgetWidth widgetWidth, ChangeHandler changeHandler, ClickHandler refreshHandler) {
		Label lbl = null;
		if (displayLabel != null) {
			lbl = new Label(displayLabel);
			lbl.addStyleName(style.lblListDisplay());
			panelContent.add(lbl);
		}

		ListBox lst = new ListBox();
		lst.addStyleName(style.lst());

		if (items != null) {
			for (ListItem item : items) {
				lst.addItem(item.getItem(), item.getValue());
			}
		}
		if(changeHandler != null) {
			lst.addChangeHandler(changeHandler);
		}

		panelContent.add(lst);

		Image imgRefresh = null;
		if (refreshHandler != null) {
			imgRefresh = new Image(Images.INSTANCE.ic_autorenew_black_18dp());
			imgRefresh.addStyleName(style.refresh());
			imgRefresh.addClickHandler(refreshHandler);

			panelContent.add(imgRefresh);

			lst.addStyleName(style.lstWithRefresh());
		}
		else {
			lst.setWidth(getWidgetWidth(widgetWidth, false));
		}

		return new PropertiesListBox(lbl, lst, imgRefresh);
	}

	protected FileUploadWidget addFileUpload(String uploadUrl, String placeHolder, String value, WidgetWidth widgetWidth) {
		String width = getWidgetWidth(widgetWidth, false);

		FileUploadWidget uploadPanel = new FileUploadWidget(uploadUrl, null);
		uploadPanel.setFileInfos(null, value);
		uploadPanel.setWidth(width);
		panelContent.add(uploadPanel);
		return uploadPanel;
	}

	protected PropertiesText addText(String placeHolder, String value, WidgetWidth widgetWidth, boolean password) {
		final TextHolderBox txt = new TextHolderBox();
		txt.addStyleName(style.txt());
		txt.setPassword(password);
		txt.setPlaceHolder(placeHolder);
		txt.setText(value != null ? value : "");

		txt.setWidth(getWidgetWidth(widgetWidth, false));

		Label lblError = new Label();
		lblError.addStyleName(style.error());

		panelContent.add(txt);
		panelContent.add(lblError);

		return new PropertiesText(txt, lblError, null, style.warning(), style.error());
	}

	protected PropertiesAreaText addTextArea(String placeHolder, String value, WidgetWidth widgetWidth) {
		TextAreaHolderBox txt = new TextAreaHolderBox();
		txt.addStyleName(style.txt());
		txt.setPlaceHolder(placeHolder);
		txt.setText(value != null ? value : "");
		txt.setWidth(getWidgetWidth(widgetWidth, true));
		txt.getElement().getStyle().setProperty("minWidth", getWidgetWidth(widgetWidth, true));
		txt.getElement().getStyle().setProperty("maxWidth", getWidgetWidth(widgetWidth, true));

		panelContent.add(txt);

		Label lblError = new Label();
		lblError.addStyleName(style.error());
		panelContent.add(lblError);

		return new PropertiesAreaText(txt, lblError, null, style.success(), style.error());
	}

	protected VariablePropertiesText addVariableText(String placeHolder, VariableString value, WidgetWidth widgetWidth, ClickHandler validHandler) {
		VariableTextHolderBox txt = new VariableTextHolderBox(value, placeHolder, style.txt(), resourceManager.getVariables(), resourceManager.getParameters());
		variableTexts.add(txt);

		panelContent.add(txt);

		Image imgValid = null;
		if (validHandler != null) {
			imgValid = new Image(Images.INSTANCE.ic_done_black_18dp());
			imgValid.addStyleName(style.validNoMargin());
			imgValid.addClickHandler(validHandler);

			panelContent.add(imgValid);

			txt.setWidth(getWidgetWidth(widgetWidth, true));
		}
		else {
			txt.setWidth(getWidgetWidth(widgetWidth, false));
		}

		Label lblError = new Label();
		lblError.addStyleName(style.error());
		panelContent.add(lblError);

		return new VariablePropertiesText(txt, lblError, imgValid, style.success(), style.error());
	}

	protected VariablePropertiesAreaText addVariableTextArea(String placeHolder, VariableString value, WidgetWidth widgetWidth, ClickHandler validHandler) {
		boolean withRefresh = validHandler != null;

		VariableTextAreaHolderBox txt = new VariableTextAreaHolderBox(value, placeHolder, resourceManager.getVariables(), resourceManager.getParameters(), style.txt());
		txt.setWidth(getWidgetWidth(widgetWidth, withRefresh));
		txt.getElement().getStyle().setProperty("minWidth", getWidgetWidth(widgetWidth, withRefresh));
		txt.getElement().getStyle().setProperty("maxWidth", getWidgetWidth(widgetWidth, withRefresh));
		variableAreaTexts.add(txt);

		panelContent.add(txt);

		Image imgValid = null;
		if (withRefresh) {
			imgValid = new Image(Images.INSTANCE.ic_done_black_18dp());
			imgValid.addStyleName(style.valid());
			imgValid.addClickHandler(validHandler);

			panelContent.add(imgValid);
		}

		Label lblError = new Label();
		lblError.addStyleName(style.error());
		panelContent.add(lblError);

		return new VariablePropertiesAreaText(txt, lblError, imgValid, style.success(), style.error());
	}

	protected RadioButton addRadioButton(String name, String label, boolean value, ValueChangeHandler<Boolean> valueChangeHandler) {
		RadioButton radio = new RadioButton(name, label);
		radio.addStyleName(style.check());
		radio.setValue(value);

		if (valueChangeHandler != null) {
			radio.addValueChangeHandler(valueChangeHandler);
		}

		panelContent.add(radio);

		return radio;
	}

	protected CheckBox addCheckbox(String label, boolean value, ValueChangeHandler<Boolean> valueChangeHandler) {
		CheckBox check = new CheckBox(label);
		check.addStyleName(style.check());
		check.setValue(value);

		if (valueChangeHandler != null) {
			check.addValueChangeHandler(valueChangeHandler);
		}

		panelContent.add(check);

		return check;
	}

	protected SimplePanel addSimplePanel(boolean limitSize) {
		SimplePanel simplePanel = new SimplePanel();
		simplePanel.addStyleName(style.simplePanel());
		if (limitSize) {
			simplePanel.addStyleName(style.simplePanelSize());
		}

		panelContent.add(simplePanel);

		return simplePanel;
	}
	
	protected PropertiesTextBrowse addBrowse(String placeHolder, ClickHandler clickHandler) {
		
		Button btn = new Button("...");
		TextHolderBox txt = new TextHolderBox();
		txt.setPlaceHolder(placeHolder);
		
		HTMLPanel htmlPanel = new HTMLPanel("");
		htmlPanel.addStyleName(style.browsePanel());
		
		txt.addStyleName(style.txt());
		txt.addStyleName(style.browseTextBox());
		txt.setEnabled(false);
		btn.addStyleName(style.browseButton());
		
		htmlPanel.add(txt);
		htmlPanel.add(btn);
		
		btn.addClickHandler(clickHandler);
		
		panelContent.add(htmlPanel);
		
		return new PropertiesTextBrowse(txt, btn);
	}

	protected PropertiesButton addButton(String label, ClickHandler clickHandler) {
		Button btn = new Button(label);
		btn.addStyleName(style.btn());

		if (clickHandler != null) {
			btn.addClickHandler(clickHandler);
		}

		SimplePanel simplePanel = new SimplePanel();
		simplePanel.addStyleName(style.panelForButton());
		simplePanel.setWidget(btn);

		panelContent.add(simplePanel);

		Label lblError = new Label();
		lblError.addStyleName(style.error());
		panelContent.add(lblError);

		return new PropertiesButton(btn, lblError, style.success(), style.error());
	}

	protected void addHelper(String help) {
		Label lbl = new Label(help);
		lbl.addStyleName(style.lblHelp());

		panelContent.add(lbl);
	}

	protected Label addLabel() {
		Label lbl = new Label();
		lbl.addStyleName(style.lblHelp());

		panelContent.add(lbl);

		return lbl;
	}

	public void registerPanelWithVariables(PanelWithVariables panelWithVariables) {
		this.panelWithVariables = panelWithVariables;
	}

	private String getWidgetWidth(WidgetWidth widgetWidth, boolean withRefresh) {
		switch (widgetWidth) {
		case PCT:
			return "94%";
		case SMALL_PX:
			return withRefresh ? "430px" : "485px";
		case LARGE_PX:
			return "640px";
		case PORT:
			return "150px";
		default:
			break;
		}
		return "94%";
	}
	
	public MyStyle getStyle() {
		return style;
	}

	public abstract T buildItem();

	public abstract boolean isValid();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	private ValueChangeHandlerWithError nameHandler = new ValueChangeHandlerWithError() {

		@Override
		public void onValueChange(ValueChangeEvent<String> event) {
			String value = event.getValue();
			checkName(getText(), value);
		}
	};

	public class ListItem {

		private String item;
		private String value;

		public ListItem(String item, String value) {
			this.item = item;
			this.value = value;
		}

		public ListItem(String item, int value) {
			this.item = item;
			this.value = String.valueOf(value);
		}

		public String getItem() {
			return item;
		}

		public String getValue() {
			return value;
		}
	}
}
