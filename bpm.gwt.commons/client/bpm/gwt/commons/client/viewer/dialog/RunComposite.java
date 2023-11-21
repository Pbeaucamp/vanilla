package bpm.gwt.commons.client.viewer.dialog;

import java.util.List;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.viewer.param.ParametersPanel;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations.TypeRun;
import bpm.vanilla.platform.core.beans.UserRunConfiguration;
import bpm.vanilla.platform.core.beans.UserRunConfigurationParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class RunComposite extends Composite {

	private static RunReportsDialogUiBinder uiBinder = GWT.create(RunReportsDialogUiBinder.class);

	interface RunReportsDialogUiBinder extends UiBinder<Widget, RunComposite> {
	}

	interface MyStyle extends CssResource {
		String lblParam();
	}
	
	@UiField
	HTMLPanel contentPanel;

	@UiField
	CaptionPanel fieldSetGroupParameter, fieldSetLocale;

	@UiField
	CaptionPanel fieldSetAlternate, fieldSetLimitRows, fieldSetComs;

	@UiField
	ListBox localeBox, lstConfig;

	@UiField
	RadioButton yesLocal, noLocal;

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

	private LaunchReportInformations itemInfo;

	public RunComposite(LaunchReportInformations itemInfo) {
		initWidget(uiBinder.createAndBindUi(this));
		this.itemInfo = itemInfo;

		buildContent();

		/* We set the non Visible Part */
		if (itemInfo.getGroupParameters() != null && !itemInfo.getGroupParameters().isEmpty()) {
			ParametersPanel panelParam = new ParametersPanel(itemInfo, true);
			panelParameters.setWidget(panelParam);
		}
		else {
			fieldSetGroupParameter.setVisible(false);
		}

		if (itemInfo.getReportResources() == null || itemInfo.getReportResources().isEmpty()) {
			fieldSetLocale.setVisible(false);
		}

		if(!itemInfo.getItem().isReport()) {
			fieldSetAlternate.setVisible(false);
			fieldSetLocale.setVisible(false);
			fieldSetLimitRows.setVisible(false);
			fieldSetComs.setVisible(false);
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

		noAlternate.setValue(true);
		panelAlternate.setVisible(false);

		checkLimitRows.setValue(false);
		txtLimitRows.setEnabled(false);
		checkDisplayComments.setValue(true);
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
	
	public LaunchReportInformations getItemInfo() throws Exception {
		String locale = null;
		Integer limitRows = -1;
		boolean displayComments = true;
		
		if(itemInfo.getItem().isReport()) {
			if (yesLocal.getValue()) {
				locale = localeBox.getItemText(localeBox.getSelectedIndex());
			}

			if (yesAlternate.getValue()) {
				if (itemInfo.getAlternateDataSourceConfig().getCount() != itemInfo.getAlternateDatasource().getCount()) {
					throw new Exception(itemInfo.getItem().getName() + " : " + LabelsConstants.lblCnst.NeedInfoAlternate());
				}
			}
			else {
				itemInfo.setAlternateDataSourceConfig(null);;
			}

			if (checkLimitRows.getValue()) {
				if (txtLimitRows.getText().isEmpty()) {
					throw new Exception(itemInfo.getItem().getName() + " : " + LabelsConstants.lblCnst.IndicateLimitRow());
				}

				try {
					limitRows = Integer.parseInt(txtLimitRows.getText());
				} catch (Exception e) {
					e.printStackTrace();
					throw new Exception(itemInfo.getItem().getName() + " : " + LabelsConstants.lblCnst.IndicateValidLimitRow());
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
				throw new Exception(itemInfo.getItem().getName() + " : " + LabelsConstants.lblCnst.NeedExistingParamConfig());
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
					throw new Exception(itemInfo.getItem().getName() + " : " + LabelsConstants.lblCnst.TheParameter() + " " + paramName + " " + LabelsConstants.lblCnst.ParamNotFound());
				}
			}
		}

		for (VanillaGroupParameter groupParam : itemInfo.getGroupParameters()) {
			for (VanillaParameter param : groupParam.getParameters()) {
				if (param.isRequired() && !param.isHidden()) {
					if (param.getSelectedValues() == null || param.getSelectedValues().isEmpty()) {
						throw new Exception(itemInfo.getItem().getName() + " : " + LabelsConstants.lblCnst.TheParameter() + " " + param.getName() + " " + LabelsConstants.lblCnst.FromGroup() + " " + groupParam.getName() + " " + LabelsConstants.lblCnst.ParamNotSet());
					}
				}
			}
		}
		
		itemInfo.setLocale(locale);
		itemInfo.setLimitRows(limitRows);
		itemInfo.setDisplayComments(displayComments);
		
		if(itemInfo.getTypeRun() == TypeRun.DISCO) {
			//DO Nothing
		}
		else {
			itemInfo.setTypeRun(TypeRun.RUN);
		}
		
		return itemInfo;
	}
}
