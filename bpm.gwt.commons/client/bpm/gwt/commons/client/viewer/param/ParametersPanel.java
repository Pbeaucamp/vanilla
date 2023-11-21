package bpm.gwt.commons.client.viewer.param;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.viewer.widget.CustomComboParameter;
import bpm.gwt.commons.client.viewer.widget.CustomListBoxWithWait;
import bpm.gwt.commons.client.viewer.widget.CustomRadioButton;
import bpm.gwt.commons.client.viewer.widget.CustomRadioButtonGroupPanel;
import bpm.gwt.commons.client.viewer.widget.IListParameter;
import bpm.gwt.commons.client.viewer.widget.PortalGroupComboParameter;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class ParametersPanel extends Composite {

	private static ParametersPanelUiBinder uiBinder = GWT.create(ParametersPanelUiBinder.class);

	interface ParametersPanelUiBinder extends UiBinder<Widget, ParametersPanel> {
	}

	interface MyStyle extends CssResource {
		String lblParam();

		String paramP();

		String captionParam();
	}

	@UiField
	HTMLPanel panelParameter;

	@UiField
	MyStyle style;

	private HashMap<String, PortalGroupComboParameter> groupComboBoxParam = new HashMap<String, PortalGroupComboParameter>();

	private boolean fromLaunch;

	final DateTimeFormat dateFormat = DateTimeFormat.getFormat("yyyy-MM-dd");

	public ParametersPanel(LaunchReportInformations itemInfo, boolean fromLaunch) {
		initWidget(uiBinder.createAndBindUi(this));
		this.fromLaunch = fromLaunch;

		refreshParams(itemInfo);
	}

	public void refreshParams(LaunchReportInformations itemInfo) {
		for (VanillaGroupParameter groupParameter : itemInfo.getGroupParameters()) {

			boolean isGroupDisplay = false;
			for (VanillaParameter param : groupParameter.getParameters()) {
				if (!param.isHidden()) {
					isGroupDisplay = true;
					break;
				}
			}

			if (isGroupDisplay) {
				String labelGroupParameter = null;
				if (groupParameter.getPromptText() != null && !groupParameter.getPromptText().equals("")) {
					labelGroupParameter = "<b>" + groupParameter.getPromptText() + "</b>";
				}
				else if (groupParameter.getDisplayName() != null && !groupParameter.getDisplayName().equals("")) {
					labelGroupParameter = "<b>" + groupParameter.getDisplayName() + "</b>";
				}
				else {
					labelGroupParameter = "<b>" + groupParameter.getName() + "</b>";
				}

				HTMLPanel panelGroupParam = new HTMLPanel("");

				List<IListParameter> listComboBox = new ArrayList<IListParameter>();
				for (final VanillaParameter parameter : groupParameter.getParameters()) {

					Label lblParam = null;
					if (parameter.getPromptText() != null && !parameter.getPromptText().isEmpty()) {
						lblParam = new Label(parameter.getPromptText());
					}
					else if (parameter.getDisplayName() != null && !parameter.getDisplayName().isEmpty()) {
						lblParam = new Label(parameter.getDisplayName());
					}
					else {
						lblParam = new Label(parameter.getName());
					}
					lblParam.addStyleName(style.lblParam());

					final Widget fset;
					if (parameter.getControlType() == VanillaParameter.LIST_BOX) {
						IListParameter list = createComboField(itemInfo.getGroupParameters(), parameter, itemInfo);
						listComboBox.add(list);

						fset = list.getWidget();
					}
					else if (parameter.getControlType() == VanillaParameter.RADIO_BUTTON) {
						fset = createRadioButton(parameter);
					}
					else if (parameter.getControlType() == VanillaParameter.CHECK_BOX) {
						fset = createCheckBox(parameter);
					}
					else {
						fset = createTextField(parameter);
					}

					if (!parameter.isHidden()) {
						HTMLPanel paramPanel = new HTMLPanel("");
						paramPanel.addStyleName(style.paramP());
						paramPanel.add(lblParam);
						paramPanel.add(fset);

						if (!parameter.isRequired()) {

							CheckBox checkBoxIsRequired = new CheckBox();
							checkBoxIsRequired.setText(LabelsConstants.lblCnst.Optional());
							checkBoxIsRequired.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

								@Override
								public void onValueChange(ValueChangeEvent<Boolean> event) {

									if (event.getValue()) {
										if (fset instanceof CustomListBoxWithWait) {
											parameter.getSelectedValues().clear();
											// (parameter.getSelectedValues()).add("");
											((CustomListBoxWithWait) fset).getListBox().setSelectedIndex(-1);
											((CustomListBoxWithWait) fset).getListBox().setEnabled(false);
										}
										else if (fset instanceof CustomComboParameter) {
											parameter.getSelectedValues().clear();
											((CustomComboParameter) fset).getListBox().setSelectedIndex(-1);
											((CustomComboParameter) fset).getListBox().setEnabled(false);
										}
										else if (fset instanceof TextBox) {
											((TextBox) fset).setValue("");
											((TextBox) fset).setEnabled(false);
											parameter.getSelectedValues().clear();
											// parameter.setSelectedValues(null);
										}
										else if (fset instanceof DateBox) {
											((DateBox) fset).setEnabled(false);
											parameter.getSelectedValues().clear();
										}
									}
									else {
										if (fset instanceof CustomListBoxWithWait) {
											((CustomListBoxWithWait) fset).getListBox().setEnabled(true);
										}
										else if (fset instanceof CustomComboParameter) {
											((CustomComboParameter) fset).getListBox().setEnabled(true);
										}
										else if (fset instanceof TextBox) {
											((TextBox) fset).setEnabled(true);
											parameter.getSelectedValues().clear();
											parameter.addSelectedValue(((TextBox) fset).getText());
										}
										else if (fset instanceof DateBox) {
											((DateBox) fset).setEnabled(true);
											parameter.getSelectedValues().clear();
											parameter.addSelectedValue(dateFormat.format(((DateBox) fset).getValue()));

										}
									}
								}
							});
							paramPanel.add(checkBoxIsRequired);
						}

						panelGroupParam.add(paramPanel);
					}
				}

				CaptionPanel captionGroup = new CaptionPanel();
				captionGroup.setCaptionHTML(labelGroupParameter);
				captionGroup.addStyleName(style.captionParam());
				captionGroup.add(panelGroupParam);

				panelParameter.add(captionGroup);

				if (listComboBox.size() > 0) {
					for (int i = 0; i < listComboBox.size() - 1; i++) {
						PortalGroupComboParameter birtGroupCombo = new PortalGroupComboParameter(groupParameter, listComboBox.get(i).getName(), listComboBox.get(i).getCustomListBox(), listComboBox.get(i + 1).getCustomListBox(), false);
						groupComboBoxParam.put(listComboBox.get(i).getName(), birtGroupCombo);
					}
					PortalGroupComboParameter birtGroupCombo = new PortalGroupComboParameter(groupParameter, listComboBox.get(listComboBox.size() - 1).getName(), listComboBox.get(listComboBox.size() - 1).getCustomListBox(), null, true);
					groupComboBoxParam.put(listComboBox.get(listComboBox.size() - 1).getName(), birtGroupCombo);

					if (fromLaunch) {
						if (groupComboBoxParam.get(listComboBox.get(0).getName()).getGroupParam().isCascadingGroup()) {
							NativeEvent event = Document.get().createChangeEvent();
							ChangeEvent.fireNativeEvent(event, groupComboBoxParam.get(listComboBox.get(0).getName()).getActualBox().getListBox());
						}
						else {
							for (IListParameter cb : listComboBox) {
								NativeEvent event = Document.get().createChangeEvent();
								ChangeEvent.fireNativeEvent(event, cb.getListBox());
							}
						}
					}
				}
			}
		}
	}

	private Widget createRadioButton(VanillaParameter parameter) {
		CustomRadioButtonGroupPanel groupRadioBtn = new CustomRadioButtonGroupPanel(parameter);
		if (!parameter.getValues().isEmpty()) {
			boolean first = true;
			for (Entry<String, String> entry : parameter.getValues().entrySet()) {
				CustomRadioButton btn = new CustomRadioButton(parameter.getName(), entry.getValue());
				btn.setText(entry.getKey());

				if (first) {
					groupRadioBtn.addRadioButton(btn, first);
					first = false;
				}
				else {
					groupRadioBtn.addRadioButton(btn, first);
				}

				if (fromLaunch) {
					if (entry.getValue().equals(parameter.getDefaultValue())) {
						btn.setValue(true);
					}
				}
				else {
					for (String selectedValue : parameter.getSelectedValues()) {
						if (entry.getValue().equals(selectedValue)) {
							btn.setValue(true);
							break;
						}
					}
				}
			}
		}
		return groupRadioBtn;
	}

	private Widget createCheckBox(final VanillaParameter parameter) {
		CheckBox checkBox = new CheckBox();
		checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				parameter.getSelectedValues().clear();
				parameter.addSelectedValue(event.getValue().toString());
			}
		});

		if (fromLaunch) {
			List<String> selectedValues = new ArrayList<String>();
			if (parameter.getDefaultValue().equalsIgnoreCase("true")) {
				checkBox.setValue(true);
				selectedValues.add("true");
			}
			else {
				checkBox.setValue(false);
				selectedValues.add("false");
			}
			parameter.setSelectedValues(selectedValues);
		}
		else {
			if (parameter.getSelectedValues() != null && !parameter.getSelectedValues().isEmpty()) {
				if (parameter.getSelectedValues().get(0).equalsIgnoreCase("true")) {
					checkBox.setValue(true);
				}
				else {
					checkBox.setValue(false);
				}
			}
		}

		return checkBox;
	}

	private IListParameter createComboField(final List<VanillaGroupParameter> groupParams, final VanillaParameter parameter, final LaunchReportInformations itemInfo) {

		int display = 7;

		ListBox cbox = null;
		if (parameter.getParamType().equals("multi-value")) {
			cbox = new ListBox(true);
			cbox.setVisibleItemCount(display);
		}
		else {
			cbox = new ListBox(false);
			if (!fromLaunch) {
				cbox.setVisibleItemCount(display);
			}
		}
		cbox.setName(parameter.getName());

		if (!parameter.getValues().isEmpty()) {
			List<Integer> indexes = new ArrayList<Integer>();
			int i = 0;
			for (Entry<String, String> entry : parameter.getValues().entrySet()) {
				cbox.addItem(entry.getKey(), entry.getValue());

				if (fromLaunch) {
					if (entry.getValue().equals(parameter.getDefaultValue())) {
						indexes.add(i);
					}
				}
				else {
					for (String selectedValue : parameter.getSelectedValues()) {
						if (entry.getValue().equals(selectedValue)) {
							indexes.add(i);
							break;
						}
					}
				}
				i++;
			}

			for (Integer index : indexes) {
				cbox.setItemSelected(index, true);
			}
		}
		else {
			cbox.addItem(LabelsConstants.lblCnst.SelectParentParameter());
		}

		cbox.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				ListBox listTemp = (ListBox) event.getSource();

				String selectedParam = listTemp.getName();
				PortalGroupComboParameter groupComboParameter = groupComboBoxParam.get(selectedParam);

				for (VanillaParameter paramTemp : groupComboParameter.getGroupParam().getParameters()) {
					if (paramTemp.getName().equals(selectedParam)) {
						paramTemp.getSelectedValues().clear();
						for (int i = 0; i < listTemp.getItemCount(); i++) {
							if (listTemp.isItemSelected(i)) {
								paramTemp.addSelectedValue(listTemp.getValue(i));
							}
						}
						break;
					}
				}

				if (!groupComboParameter.isLast() && groupComboParameter.getGroupParam().isCascadingGroup()) {
					groupComboParameter.getChildBox().showWait(true);
					fillCascadingParam(itemInfo, groupComboBoxParam.get(listTemp.getName()).getChildBox(), groupComboBoxParam.get(listTemp.getName()).getChildBox().getName(), groupParams);
				}
			}
		});
		cbox.setWidth("100%");
		cbox.getElement().getStyle().setMarginTop(5, Unit.PX);

		if (parameter.allowNewValues()) {
			TextBox txtNewValue = new TextBox();
			txtNewValue.setName(parameter.getName());
			txtNewValue.addValueChangeHandler(new ValueChangeHandler<String>() {

				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					if (!event.getValue().isEmpty()) {
						TextBox txtTemp = (TextBox) event.getSource();

						String selectedParam = txtTemp.getName();
						PortalGroupComboParameter groupComboParameter = groupComboBoxParam.get(selectedParam);

						for (VanillaParameter paramTemp : groupComboParameter.getGroupParam().getParameters()) {
							if (paramTemp.getName().equals(selectedParam)) {
								paramTemp.getSelectedValues().clear();
								paramTemp.addSelectedValue(event.getValue());
								break;
							}
						}

						if (!groupComboParameter.isLast() && groupComboParameter.getGroupParam().isCascadingGroup()) {
							groupComboParameter.getChildBox().showWait(true);
							fillCascadingParam(itemInfo, groupComboBoxParam.get(txtTemp.getName()).getChildBox(), groupComboBoxParam.get(txtTemp.getName()).getChildBox().getName(), groupParams);
						}
					}
				}
			});

			CustomListBoxWithWait customListBox = new CustomListBoxWithWait(cbox, parameter);

			CustomComboParameter customCombo = new CustomComboParameter(parameter, customListBox, txtNewValue);
			return customCombo;
		}
		else {
			CustomListBoxWithWait customListBox = new CustomListBoxWithWait(cbox, parameter);
			return customListBox;
		}
	}

	private Widget createTextField(final VanillaParameter parameter) {
		if (parameter.getDataType() == 7) {
			DateBox dateBox = new DateBox();
			dateBox.setFormat(new DateBox.DefaultFormat(dateFormat));

			if (fromLaunch) {
				if (parameter.getDefaultValue() != null && !parameter.getDefaultValue().isEmpty()) {
					try {
						dateBox.setValue(DateTimeFormat.getFormat("yyyy-MM-dd").parse(parameter.getDefaultValue()));
					} catch (Exception e) {
					}

					List<String> values = new ArrayList<String>();
					values.add(parameter.getDefaultValue());
					parameter.setSelectedValues(values);
				}
				else {
					dateBox.setValue(new Date());

					String todayDate = DateTimeFormat.getFormat("yyyy-MM-dd").format(new Date());

					List<String> values = new ArrayList<String>();
					values.add(todayDate);
					parameter.setSelectedValues(values);
				}
			}
			else {
				try {
					dateBox.setValue(DateTimeFormat.getFormat("yyyy-MM-dd").parse(parameter.getSelectedValues().get(0)));
				} catch (Exception e) {
				}
			}

			dateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {

				@Override
				public void onValueChange(ValueChangeEvent<Date> event) {
					if (event.getValue() != null) {
						List<String> values = new ArrayList<String>();
						values.add(dateFormat.format(event.getValue()));
						parameter.setSelectedValues(values);
					}
					else {
						GWT.log("wrong value", null);
					}
				}
			});

			return dateBox;
		}
		else {
			TextBox tf = new TextBox();
			tf.setName(parameter.getName());

			if (fromLaunch) {
				tf.setValue(parameter.getDefaultValue());
				if (parameter.getDefaultValue() != null && !parameter.getDefaultValue().isEmpty()) {
					List<String> values = new ArrayList<String>();
					values.add(parameter.getDefaultValue());
					parameter.setSelectedValues(values);
				}
			}
			else {
				try {
					tf.setValue(parameter.getSelectedValues().get(0));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			tf.addValueChangeHandler(new ValueChangeHandler<String>() {

				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					if (event.getValue() != null && !event.getValue().isEmpty()) {
						parameter.getSelectedValues().clear();
						parameter.addSelectedValue(event.getValue());
					}
					else {
						parameter.setSelectedValues(null);
					}
				}
			});

			return tf;
		}
	}

	private void fillCascadingParam(LaunchReportInformations itemInfo, final CustomListBoxWithWait childBox, String paramName, List<VanillaGroupParameter> existingParams) {

		ReportingService.Connect.getInstance().getParameterValues(itemInfo, existingParams, paramName, new AsyncCallback<VanillaParameter>() {

			@Override
			public void onFailure(Throwable caught) {
				childBox.showWait(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.FailedSetCascadingParams());
			}

			@Override
			public void onSuccess(VanillaParameter arg0) {

				childBox.clear();
				for (Entry<String, String> entry : arg0.getValues().entrySet()) {
					childBox.addItem(entry.getKey(), entry.getValue());
				}

				childBox.setValues(arg0.getValues());

				if (childBox.getListBox().getItemCount() > 0) {
					childBox.getListBox().setSelectedIndex(0);

					NativeEvent event = Document.get().createChangeEvent();
					ChangeEvent.fireNativeEvent(event, childBox.getListBox());
				}

				childBox.showWait(false);
			}
		});
	}
}
