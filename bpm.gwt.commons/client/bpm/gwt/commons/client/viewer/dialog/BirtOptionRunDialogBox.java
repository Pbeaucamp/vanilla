package bpm.gwt.commons.client.viewer.dialog;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.viewer.Viewer;
import bpm.gwt.commons.client.viewer.param.ParametersPanel;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations.TypeRun;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.UserRunConfiguration;
import bpm.vanilla.platform.core.beans.UserRunConfigurationParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class BirtOptionRunDialogBox extends AbstractDialogBox {

	private static BirtOptionRunDialogBoxUiBinder uiBinder = GWT.create(BirtOptionRunDialogBoxUiBinder.class);

	interface BirtOptionRunDialogBoxUiBinder extends UiBinder<Widget, BirtOptionRunDialogBox> {
	}

	interface MyStyle extends CssResource {
		String lblParam();
	}

	@UiField
	HTMLPanel contentPanel;

	@UiField
	CaptionPanel fieldSetGroupParameter, fieldSetLocale, fieldSetFormat;

	@UiField
	CaptionPanel fieldSetBackground, fieldSetAlternate, fieldSetLimitRows, fieldSetComs;

	@UiField
	ListBox localeBox, lstConfig;

	@UiField(provided = true)
	ListBox lstFormat = new ListBox(true);

	@UiField
	RadioButton yesLocal, noLocal, yesRunBackground, noRunBackground;

	@UiField
	RadioButton yesAlternate, noAlternate, yesExistingConfig, noExistingConfig;

	@UiField
	Label lblLocal, lblConfDesc, lblExistingConf, lblDesc;

	@UiField
	TextBox txtLimitRows;

	@UiField
	CheckBox checkLimitRows, checkDisplayComments;

	@UiField
	HorizontalPanel panelLocale, panelBtnConfig;

	@UiField
	HTMLPanel panelAlternate, panelConfig;

	@UiField
	MyStyle style;

	@UiField
	SimplePanel panelParameters;

	private Viewer viewer;
	private LaunchReportInformations itemInfo;

	public BirtOptionRunDialogBox(Viewer viewer, LaunchReportInformations itemInfo) {
		super(getDialogName(itemInfo), false, true);
		this.viewer = viewer;
		this.itemInfo = itemInfo;

		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsConstants.lblCnst.Ok(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		buildContent();

		/* We set the non Visible Part */
		if (itemInfo.getGroupParameters() != null && !itemInfo.getGroupParameters().isEmpty() && panelParamDisplay(itemInfo.getGroupParameters())) {
			ParametersPanel panelParam = new ParametersPanel(itemInfo, true);
			panelParameters.setWidget(panelParam);
		}
		else {
			fieldSetGroupParameter.setVisible(false);
		}

		if (itemInfo.getReportResources() == null || itemInfo.getReportResources().isEmpty()) {
			fieldSetLocale.setVisible(false);
		}

		if (!itemInfo.getItem().isReport()) {
			fieldSetAlternate.setVisible(false);
			fieldSetLocale.setVisible(false);
			fieldSetFormat.setVisible(false);
			fieldSetLimitRows.setVisible(false);
			fieldSetComs.setVisible(false);
			fieldSetBackground.setVisible(false);
		}
	}

	private boolean panelParamDisplay(List<VanillaGroupParameter> groupParameters) {
		for (VanillaGroupParameter groupParameter : itemInfo.getGroupParameters()) {
			for (VanillaParameter param : groupParameter.getParameters()) {
				if (!param.isHidden()) {
					return true;
				}
			}
		}
		return false;
	}

	private static String getDialogName(LaunchReportInformations itemInfo) {
		if (itemInfo.getItem().isReport()) {
			return LabelsConstants.lblCnst.RunReport();
		}
		else if (itemInfo.getItem().getType() == IRepositoryApi.GTW_TYPE) {
			return LabelsConstants.lblCnst.RunGateway();
		}
		else {
			return LabelsConstants.lblCnst.RunWorkflow();
		}
	}

	public void buildContent() {

		if (itemInfo.getUserConfigurations() != null && !itemInfo.getUserConfigurations().isEmpty()) {
			yesExistingConfig.setValue(true);

			panelParameters.setVisible(false);

			for (UserRunConfiguration conf : itemInfo.getUserConfigurations()) {
				lstConfig.addItem(conf.getName(), String.valueOf(conf.getRunId()));
			}

			lstConfig.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					int confId = Integer.parseInt(lstConfig.getValue(lstConfig.getSelectedIndex()));
					String confTooltip = "";
					for (UserRunConfiguration conf : itemInfo.getUserConfigurations()) {
						if (conf.getRunId() == confId) {
							StringBuilder builder = new StringBuilder();
							for (UserRunConfigurationParameter param : conf.getParameters()) {
								builder.append(param.getName() + ": ");
								for (String value : param.getValues()) {
									builder.append(value + " - ");
								}
								builder.append("\n");
							}

							confTooltip = builder.toString();
						}
					}

					lstConfig.setTitle(confTooltip);
				}
			});

			if (lstConfig.getItemCount() > 0) {
				lstConfig.setSelectedIndex(0);

				NativeEvent event = Document.get().createChangeEvent();
				ChangeEvent.fireNativeEvent(event, lstConfig);
			}
		}
		else {
			panelBtnConfig.setVisible(false);
			panelConfig.setVisible(false);
			panelParameters.setVisible(true);
		}

		if (itemInfo.getAlternateDatasource() != null) {
			if (!itemInfo.getAlternateDatasource().getDataSourceNames().isEmpty()) {
				boolean addAtLeastOne = false;
				for (String dataSource : itemInfo.getAlternateDatasource().getDataSourceNames()) {
					if (!itemInfo.getAlternateDatasource().getAlternateConnections(dataSource).isEmpty()) {
						HTMLPanel panel = setAlternate(dataSource, itemInfo.getAlternateDatasource().getAlternateConnections(dataSource));
						panelAlternate.add(panel);

						addAtLeastOne = true;
					}
				}
				if (!addAtLeastOne) {
					fieldSetAlternate.setVisible(false);
				}
			}
			else {
				fieldSetAlternate.setVisible(false);
			}
		}
		else {
			fieldSetAlternate.setVisible(false);
		}

		if (itemInfo.getReportResources() != null && !itemInfo.getReportResources().isEmpty()) {
			for (String availableResource : itemInfo.getReportResources()) {
				localeBox.addItem(availableResource, availableResource);
			}
		}

		noLocal.setValue(true);
		panelLocale.setVisible(false);

		noRunBackground.setValue(true);

		noAlternate.setValue(true);
		panelAlternate.setVisible(false);

		checkLimitRows.setValue(false);
		txtLimitRows.setEnabled(false);
		
		checkDisplayComments.setValue(true);

		// if (itemInfo.getItem().hasDefaultFormat()) {
		// String lbl = LabelsConstants.lblCnst.DefaultReportFormatIs() + " " +
		// itemInfo.getItem().getItem().getDefaultFormat() + ". " +
		// LabelsConstants.lblCnst.ClickToChangeFormat();
		// linkDefaultFormat.setText(lbl);
		// }

		int indexDefaultFormat = -1;
		for (int i = 0; i < CommonConstants.FORMAT_DISPLAY.length; i++) {
			if (i < 2 || (!(itemInfo.getItem().getType() == IRepositoryApi.CUST_TYPE && itemInfo.getItem().getSubType() == IRepositoryApi.JASPER_REPORT_SUBTYPE))) {
				lstFormat.addItem(CommonConstants.FORMAT_DISPLAY[i], CommonConstants.FORMAT_VALUE[i]);
			}

			if (itemInfo.getItem().hasDefaultFormat() && itemInfo.getItem().getItem().getDefaultFormat().equalsIgnoreCase(CommonConstants.FORMAT_VALUE[i])) {
				indexDefaultFormat = i;
			}
		}

		if (indexDefaultFormat != -1) {
			lstFormat.setSelectedIndex(indexDefaultFormat);
		}
		else {
			lstFormat.setSelectedIndex(0);
		}
	}

	private HTMLPanel setAlternate(final String dataSource, List<String> alternateConnections) {

		Label lblAlternateDatasource = new Label(LabelsConstants.lblCnst.AlternateConnection() + " " + dataSource);

		ListBox lstAlternate = new ListBox(false);
		for (String alternate : alternateConnections) {
			lstAlternate.addItem(alternate);
		}
		lstAlternate.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				ListBox source = (ListBox) event.getSource();
				String alternateConnection = source.getValue(source.getSelectedIndex());
				itemInfo.getAlternateDataSourceConfig().setConnection(dataSource, alternateConnection);
			}
		});
		if (!alternateConnections.isEmpty()) {
			NativeEvent event = Document.get().createChangeEvent();
			ChangeEvent.fireNativeEvent(event, lstAlternate);
		}

		HTMLPanel panel = new HTMLPanel("");
		panel.add(lblAlternateDatasource);
		panel.add(lstAlternate);
		return panel;
	}

	// @UiHandler("linkDefaultFormat")
	// public void onChangeDefaultReportFormat(ClickEvent event) {
	// lstFormat.setVisible(true);
	// linkDefaultFormat.setVisible(false);
	// }

	@UiHandler("lstConfig")
	public void onConfigChange(ChangeEvent event) {
		ListBox src = (ListBox) event.getSource();
		int configId = Integer.parseInt(src.getValue(src.getSelectedIndex()));

		for (UserRunConfiguration conf : itemInfo.getUserConfigurations()) {
			if (conf.getRunId() == configId) {
				lblConfDesc.setText(conf.getDescription());
			}
		}
	}

	@UiHandler("yesLocal")
	public void onYesLocalClick(ValueChangeEvent<Boolean> event) {
		if (event.getValue()) {
			panelLocale.setVisible(true);
		}
	}

	@UiHandler("noLocal")
	public void onNoLocalClick(ValueChangeEvent<Boolean> event) {
		if (event.getValue()) {
			panelLocale.setVisible(false);
		}
	}

	@UiHandler("yesAlternate")
	public void onYesAlternate(ValueChangeEvent<Boolean> event) {
		if (event.getValue()) {
			panelAlternate.setVisible(true);
		}
	}

	@UiHandler("noAlternate")
	public void onNoAlternate(ValueChangeEvent<Boolean> event) {
		if (event.getValue()) {
			panelAlternate.setVisible(false);
		}
	}

	@UiHandler("yesExistingConfig")
	public void onYesExistingConfig(ValueChangeEvent<Boolean> event) {
		if (event.getValue()) {
			panelConfig.setVisible(true);
			panelParameters.setVisible(false);
		}
	}

	@UiHandler("noExistingConfig")
	public void onNoExistingConfig(ValueChangeEvent<Boolean> event) {
		if (event.getValue()) {
			panelConfig.setVisible(false);
			panelParameters.setVisible(true);
		}
	}

	@UiHandler("checkLimitRows")
	public void onCheckLimitRowsChanged(ValueChangeEvent<Boolean> event) {
		if (event.getValue()) {
			txtLimitRows.setEnabled(true);
		}
		else {
			txtLimitRows.setEnabled(false);
		}
	}

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			String locale = null;
			Integer limitRows = -1;
			List<String> outputs = null;
			boolean displayComments = true;

			if (itemInfo.getItem().isReport()) {
				if (yesLocal.getValue()) {
					locale = localeBox.getItemText(localeBox.getSelectedIndex());
				}

				outputs = new ArrayList<String>();
				if (lstFormat.isVisible()) {
					for (int i = 0; i < CommonConstants.FORMAT_DISPLAY.length; i++) {
						if (lstFormat.getItemCount() > i && lstFormat.isItemSelected(i)) {
							outputs.add(CommonConstants.FORMAT_VALUE[i]);
						}
					}
				}
				else {
					outputs.add(itemInfo.getItem().getItem().getDefaultFormat());
				}

				if (outputs.isEmpty()) {
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.ChooseFormat());
					return;
				}

				if (yesAlternate.getValue()) {
					if (itemInfo.getAlternateDataSourceConfig().getCount() != itemInfo.getAlternateDatasource().getCount()) {
						MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.NeedInfoAlternate());
						return;
					}
				}
				else {
					itemInfo.setAlternateDataSourceConfig(null);
					;
				}

				if (checkLimitRows.getValue()) {
					if (txtLimitRows.getText().isEmpty()) {
						MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.IndicateLimitRow());
						return;
					}

					try {
						limitRows = Integer.parseInt(txtLimitRows.getText());
					} catch (Exception e) {
						e.printStackTrace();
						MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.IndicateValidLimitRow());
						return;
					}
				}
				
				displayComments = checkDisplayComments.getValue();
			}

			if (yesExistingConfig.getValue()) {
				UserRunConfiguration config = null;
				for (UserRunConfiguration conf : itemInfo.getUserConfigurations()) {
					if (conf.getRunId() == Integer.parseInt(lstConfig.getValue(lstConfig.getSelectedIndex()))) {
						config = conf;
						break;
					}
				}

				if (config == null) {
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.NeedExistingParamConfig());
					return;
				}

				for (VanillaGroupParameter grParam : itemInfo.getGroupParameters()) {
					boolean paramFound = false;
					String paramName = "";
					for (VanillaParameter param : grParam.getParameters()) {
						paramName = param.getName();

						for (UserRunConfigurationParameter paramTmp : config.getParameters()) {
							if (param.getName().equals(paramTmp.getName())) {
								param.setSelectedValues(paramTmp.getValues());

								paramFound = true;
								break;
							}
						}
					}

					if (!paramFound) {
						MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.TheParameter() + " " + paramName + " " + LabelsConstants.lblCnst.ParamNotFound());
						return;
					}
				}
			}

			for (VanillaGroupParameter groupParam : itemInfo.getGroupParameters()) {
				for (VanillaParameter param : groupParam.getParameters()) {
					if (param.isRequired() && !param.isHidden()) {
						if (param.getSelectedValues() == null || param.getSelectedValues().isEmpty()) {
							MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.TheParameter() + " " + param.getName() + " " + LabelsConstants.lblCnst.FromGroup() + " " + groupParam.getName() + " " + LabelsConstants.lblCnst.ParamNotSet());
							return;
						}
					}
				}
			}

			itemInfo.setLocale(locale);
			itemInfo.setOutputs(outputs);
			itemInfo.setLimitRows(limitRows);
			itemInfo.setDisplayComments(displayComments);

			if (itemInfo.getTypeRun() == TypeRun.DISCO) {
				// DO Nothing
			}
			else if (yesRunBackground.getValue()) {
				itemInfo.setTypeRun(TypeRun.BACKGROUND);
			}
			else {
				itemInfo.setTypeRun(TypeRun.RUN);
			}

			viewer.runItem(itemInfo);

			BirtOptionRunDialogBox.this.hide();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			viewer.setItemInfo(itemInfo);

			BirtOptionRunDialogBox.this.hide();
		}
	};
}
