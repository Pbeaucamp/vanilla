package bpm.vanillahub.web.client.tabs.resources;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.resources.ResourcePanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanillahub.core.beans.resources.FileXSD;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.images.Images;
import bpm.vanillahub.web.client.properties.resources.XSDResourceProperties;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;

public class XSDPanel extends ResourcePanel<FileXSD> {

	public XSDPanel(IResourceManager resourceManager) {
		super(Labels.lblCnst.FileXSDs(), Images.INSTANCE.action_b_24dp(), Labels.lblCnst.AddFileXSD(), resourceManager);
	}

	@Override
	public void loadResources() {
		((ResourceManager) getResourceManager()).loadXSDs(this, this);
	}

	@Override
	protected String getDeleteConfirmMessage() {
		return Labels.lblCnst.DeleteFileXSDConfirm();
	}

	@Override
	protected List<ColumnWrapper<FileXSD>> buildCustomColumns(TextCell cell, ListHandler<FileXSD> sortHandler) {
		Column<FileXSD, String> colFile = new Column<FileXSD, String>(cell) {

			@Override
			public String getValue(FileXSD object) {
				return object.getFile();
			}
		};
		colFile.setSortable(true);

		sortHandler.setComparator(colFile, new Comparator<FileXSD>() {

			@Override
			public int compare(FileXSD o1, FileXSD o2) {
				return o1.getFile().compareTo(o2.getFile());
			}
		});

		List<ColumnWrapper<FileXSD>> columns = new ArrayList<>();
		columns.add(new ColumnWrapper<FileXSD>(colFile, Labels.lblCnst.File(), null));
		return columns;
	}

	@Override
	protected PropertiesPanel<Resource> buildPropertiesPanel(Resource resource) {
		return new XSDResourceProperties(this, getResourceManager(), resource != null ? (FileXSD) resource : null);
	}
}
