package bpm.gwt.commons.client.viewer.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.gwt.commons.client.tree.CmisTree;
import bpm.gwt.commons.client.wizard.GwtWizard;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.shared.cmis.CmisFolder;
import bpm.gwt.commons.shared.cmis.CmisInformations;
import bpm.gwt.commons.shared.cmis.CmisItem;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class CmisShareSelectionPage extends Composite implements IGwtPage {

	private static AddTaskRepositoryPageUiBinder uiBinder = GWT.create(AddTaskRepositoryPageUiBinder.class);

	interface AddTaskRepositoryPageUiBinder extends UiBinder<Widget, CmisShareSelectionPage> {
	}

	@UiField
	SimplePanel mainPanel;

	@UiField
	HTMLPanel panelCheckboxs;

	@UiField
	ListBox lstFormat;

	@UiField
	Label lblTips;

	@UiField
	CheckBox checkLandscape, checkExportChart;

	private GwtWizard parent;
	private int index;

	private CmisInformations cmisInfos;
	
	private CmisTree cmisTree;
	private List<CheckBox> checkboxs = new ArrayList<CheckBox>();

	public CmisShareSelectionPage(GwtWizard parent, int index, CmisInformations cmisInfos, LaunchReportInformations itemInfo) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.index = index;
		this.cmisInfos = cmisInfos;

		cmisTree = new CmisTree(cmisInfos);
		cmisTree.addSelectionHandler(selectionHandler);
		mainPanel.add(cmisTree);

		if (itemInfo.getOutputs() != null) {
			for (String format : itemInfo.getOutputs()) {
				lstFormat.addItem(format);
			}
		}
		checkLandscape.setVisible(false);
		lblTips.setVisible(false);
		checkExportChart.setVisible(false);

		panelCheckboxs.setVisible(false);
	}

	public CmisShareSelectionPage(GwtWizard parent, int index, CmisInformations cmisInfos, List<String> folders) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.index = index;
		this.cmisInfos = cmisInfos;

		cmisTree = new CmisTree(cmisInfos);
		cmisTree.addSelectionHandler(selectionHandler);
		mainPanel.add(cmisTree);

		for (int i = 0; i < CommonConstants.FORMAT_VALUE_FD.length; i++) {
			lstFormat.addItem(CommonConstants.FORMAT_DISPLAY_FD[i], CommonConstants.FORMAT_VALUE_FD[i]);
		}

		for (String folder : folders) {
			panelCheckboxs.add(createCheckbox(folder, folder));
		}
		checkExportChart.setVisible(false);
	}

	private Widget createCheckbox(String folder, String value) {
		CheckBox checkbox = new CheckBox(folder);
		checkbox.setName(value);
		checkbox.setValue(true);

		checkboxs.add(checkbox);

		SimplePanel panel = new SimplePanel();
		panel.add(checkbox);
		return panel;
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
		return index;
	}

	@Override
	public boolean isComplete() {
		CmisItem item = cmisTree.getSelectedItem();
		return item != null && item instanceof CmisFolder;
	}

	public CmisInformations getCmisInfos() {
		return cmisInfos;
	}

	public CmisFolder getSelectedItem() {
		return (CmisFolder) cmisTree.getSelectedItem();
	}

	public String getFormat() {
		return lstFormat.getValue(lstFormat.getSelectedIndex());
	}

	public HashMap<String, String> getSelectedFolders() {
		HashMap<String, String> selectedFolders = new HashMap<String, String>();
		for (CheckBox checkbox : checkboxs) {
			if (checkbox.getValue()) {
				selectedFolders.put(checkbox.getText(), checkbox.getName());
			}
		}
		return selectedFolders;
	}

	public boolean isLandscape() {
		return checkLandscape.getValue();
	}
	
	private SelectionHandler<TreeItem> selectionHandler = new SelectionHandler<TreeItem>() {
		
		@Override
		public void onSelection(SelectionEvent<TreeItem> event) {
			parent.updateBtn();
		}
	};
}
