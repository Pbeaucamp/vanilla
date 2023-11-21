package bpm.gwt.commons.client.viewer.aklabox;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.gwt.commons.shared.utils.CommonConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AklaboxPropertiesView extends Composite {

	private static ItemPropertiesViewUiBinder uiBinder = GWT.create(ItemPropertiesViewUiBinder.class);

	interface ItemPropertiesViewUiBinder extends UiBinder<Widget, AklaboxPropertiesView> {
	}

	@UiField
	TextBox txtItemName;
	
	@UiField(provided=true)
	ListBox lstFormat;

	@UiField
	HTMLPanel panelCheckboxs;

	@UiField
	Label lblTips;
	
	@UiField
	CheckBox checkLandscape;
	
	private boolean exportDashboard;
	private List<String> formats;
	private List<CheckBox> checkboxs = new ArrayList<CheckBox>();

	@UiConstructor
	public AklaboxPropertiesView(PortailRepositoryItem item, List<String> formats, List<String> folders, boolean exportDashboard) {
		lstFormat = new ListBox(!exportDashboard);
		initWidget(uiBinder.createAndBindUi(this));
		this.formats = formats;
		this.exportDashboard = exportDashboard;
		
		txtItemName.setText(item.getName());
		
		if(exportDashboard) {
			for (int i = 0; i < CommonConstants.FORMAT_VALUE_FD.length; i++) {
				lstFormat.addItem(CommonConstants.FORMAT_DISPLAY_FD[i], CommonConstants.FORMAT_VALUE_FD[i]);
			}
			lstFormat.setSelectedIndex(0);

			for (String folder : folders) {
				panelCheckboxs.add(createCheckbox(folder));
			}
		}
		else {
			if(formats != null && !formats.isEmpty()) {
				for(String format : formats) {
					lstFormat.addItem(format);
				}
				lstFormat.setSelectedIndex(0);
			}
			
			checkLandscape.setVisible(false);
			panelCheckboxs.setVisible(false);
			lblTips.setVisible(false);
		}
	}

	private Widget createCheckbox(String folder) {
		CheckBox checkbox = new CheckBox(folder);
		checkbox.setName(folder);
		checkbox.setValue(true);

		checkboxs.add(checkbox);

		SimplePanel panel = new SimplePanel();
		panel.add(checkbox);
		return panel;
	}

	public String getName() {
		return txtItemName.getText();
	}

	public List<String> getSelectedFormats() {
		List<String> selectedFormats = new ArrayList<String>();
		if(exportDashboard) {
			selectedFormats.add(lstFormat.getValue(lstFormat.getSelectedIndex()));
		}
		else if(formats != null) {
			for (int i = 0; i < formats.size(); i++) {
				if (lstFormat.getItemCount() > i && lstFormat.isItemSelected(i)) {
					selectedFormats.add(formats.get(i));
				}
			}
		}
		return selectedFormats;
	}
	
	public List<String> getSelectedFolders() {
		List<String> selectedFolders = new ArrayList<String>();
		for(CheckBox checkbox : checkboxs) {
			if(checkbox.getValue()) {
				selectedFolders.add(checkbox.getName());
			}
		}
		return selectedFolders;
	}
	
	public boolean isLandscape() {
		return checkLandscape.getValue();
	}

	@UiHandler("lstFormat")
	public void onChangeFormat(ChangeEvent event) {
		if (exportDashboard) {
			String selectedFormat = lstFormat.getValue(lstFormat.getSelectedIndex());
			if (selectedFormat.equalsIgnoreCase(CommonConstants.FORMAT_PDF_NAME)) {
				panelCheckboxs.setVisible(true);
			}
			else {
				panelCheckboxs.setVisible(false);
			}
		}
	}

}
