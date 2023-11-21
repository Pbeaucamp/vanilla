package bpm.smart.web.client.panels.properties;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.workflow.commons.client.IManager;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.workflow.BoxItem;
import bpm.gwt.workflow.commons.client.workflow.WorkspacePanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesListBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.gwt.workflow.commons.client.workflow.properties.VariablePropertiesText;
import bpm.smart.core.model.workflow.activity.FileOutputActivity;
import bpm.smart.core.model.workflow.activity.FileOutputActivity.TypeOutput;
import bpm.smart.web.client.I18N.LabelsConstants;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.resources.Cible;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.ListBox;

public class FileOutputActivityProperties extends PropertiesPanel<Activity> implements IManager<Cible> {

	private IResourceManager resourceManager;
	private FileOutputActivity activity;

	private List<Cible> cibles;

	private PropertiesListBox lstCibles;
	private VariablePropertiesText txtDataset, txtOutputFile;

	public FileOutputActivityProperties(IResourceManager resourceManager, WorkspacePanel creationPanel, BoxItem item, FileOutputActivity activity) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.PCT, 0, activity.getName(), true, false);
		this.resourceManager = resourceManager;
		this.activity = activity;

		setNameChecker(creationPanel);
		setNameChanger(item);

		txtDataset = addVariableText("Input dataset name", activity.getDatasetVS(), WidgetWidth.PCT, null);

		List<ListItem> actions = new ArrayList<ListItem>();
		int i = 0;
		int selectedActionIndex = -1;
		for (TypeOutput type : TypeOutput.values()) {
			actions.add(new ListItem(type.toString(), type.getType()));

			if (activity.getTypeOutput() == type) {
				selectedActionIndex = i;
			}
			i++;
		}

		PropertiesListBox lstType = addList(LabelsConstants.lblCnst.SelectTypeOutput(), actions, WidgetWidth.SMALL_PX, changeTypeSource, null);
		if (selectedActionIndex != -1) {
			lstType.setSelectedIndex(selectedActionIndex);
		}
		
		this.cibles = resourceManager.getCibles();

		List<ListItem> items = new ArrayList<ListItem>();
		int selectedIndex = -1;
		if (cibles != null) {
			i = 0;
			for (Cible cible : cibles) {
				items.add(new ListItem(cible.getName(), cible.getId()));

				if (activity.getCibleId() != null && activity.getCibleId().intValue() == cible.getId()) {
					selectedIndex = i;
				}
				i++;
			}
		}

		lstCibles = addList(LabelsCommon.lblCnst.SelectCible(), items, WidgetWidth.PCT, changeCible, refreshHandler);
		lstCibles.setSelectedIndex(selectedIndex);
		
		txtOutputFile = addVariableText(LabelsCommon.lblCnst.OutputFileName(), activity.getOutputFileVS(), WidgetWidth.PCT, null);
	}

	private Cible findCible(int cibleId) {
		if (cibles != null) {
			for (Cible cible : cibles) {
				if (cible.getId() == cibleId) {
					return cible;
				}
			}
		}
		return null;
	}

	private ChangeHandler changeCible = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			int cibleId = Integer.parseInt(lstCibles.getValue(lstCibles.getSelectedIndex()));
			if (cibleId > 0) {
				activity.setCible(findCible(cibleId));
			}
		}
	};

	private ChangeHandler changeTypeSource = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			ListBox lst = (ListBox) event.getSource();
			int type = Integer.parseInt(lst.getValue(lst.getSelectedIndex()));

			activity.setTypeOutput(TypeOutput.valueOf(type));
		}
	};

	private ClickHandler refreshHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			resourceManager.loadCibles(FileOutputActivityProperties.this, FileOutputActivityProperties.this);
		}
	};

	@Override
	public void loadResources(List<Cible> result) {
		this.cibles = result;

		lstCibles.clear();
		int selectedIndex = -1;
		if (cibles != null) {
			int i = 0;
			for (Cible cible : cibles) {
				lstCibles.addItem(cible.getName(), String.valueOf(cible.getId()));

				if (activity.getCibleId() != null && activity.getCibleId().intValue() == cible.getId()) {
					selectedIndex = i;
				}
				i++;
			}
		}

		lstCibles.setSelectedIndex(selectedIndex);
	}

	@Override
	public Activity buildItem() {
		VariableString dataset = txtDataset.getVariableText();
		VariableString outputFile = txtOutputFile.getVariableText();
		
		activity.setDataset(dataset);
		activity.setOutputFile(outputFile);
		return activity;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public void loadResources() { }
}
