package bpm.vanillahub.web.client.tabs.resources;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.resources.ResourcePanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.vanilla.platform.core.beans.resources.ListOfValues;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.images.Images;
import bpm.vanillahub.web.client.properties.resources.ListOfValuesResourceProperties;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;

public class ListOfValuesPanel extends ResourcePanel<ListOfValues> {

	public ListOfValuesPanel(IResourceManager resourceManager) {
		super(Labels.lblCnst.ListOfValues(), Images.INSTANCE.action_b_24dp(), Labels.lblCnst.AddListOfValues(), resourceManager);
	}

	@Override
	public void loadResources() {
		((ResourceManager) getResourceManager()).loadListOfValues(this, this);
	}

	@Override
	protected String getDeleteConfirmMessage() {
		return Labels.lblCnst.DeleteListOfValuesConfirm();
	}

	@Override
	protected List<ColumnWrapper<ListOfValues>> buildCustomColumns(TextCell cell, ListHandler<ListOfValues> sortHandler) {
		List<ColumnWrapper<ListOfValues>> columns = new ArrayList<>();
		return columns;
	}

	@Override
	protected PropertiesPanel<Resource> buildPropertiesPanel(Resource resource) {
		return new ListOfValuesResourceProperties(this, getResourceManager(), resource != null ? (ListOfValues) resource : null);
	}

}
