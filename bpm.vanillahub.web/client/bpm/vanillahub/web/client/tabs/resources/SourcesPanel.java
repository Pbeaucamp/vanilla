package bpm.vanillahub.web.client.tabs.resources;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.resources.ResourcePanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanillahub.core.beans.resources.Source;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.images.Images;
import bpm.vanillahub.web.client.properties.resources.SourceResourceProperties;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;

public class SourcesPanel extends ResourcePanel<Source> {
	
	public SourcesPanel(IResourceManager resourceManager) {
		super(Labels.lblCnst.Sources(), Images.INSTANCE.action_b_24dp(), Labels.lblCnst.AddSource(), resourceManager);
	}

	@Override
	public void loadResources() {
		((ResourceManager) getResourceManager()).loadSources(this, this);
	}

	@Override
	protected String getDeleteConfirmMessage() {
		return Labels.lblCnst.DeleteSourceConfirm();
	}

	@Override
	protected List<ColumnWrapper<Source>> buildCustomColumns(TextCell cell, ListHandler<Source> sortHandler) {
		Column<Source, String> colType = new Column<Source, String>(cell) {

			@Override
			public String getValue(Source object) {
				switch (object.getType()) {
				case FOLDER:
					return Labels.lblCnst.NetworkFolder();
				case WEB_SERVICE:
					return Labels.lblCnst.WebService();
				case FTP:
					return Labels.lblCnst.FTP();
				case SFTP:
					return Labels.lblCnst.SFTP();
				case D4C:
					return LabelsCommon.lblCnst.D4C();
				case MAIL:
					return LabelsCommon.lblCnst.Mail();
				default:
					return LabelsCommon.lblCnst.Unknown();
				}
			}
		};
		colType.setSortable(true);

		Column<Source, String> colPathOrUrl = new Column<Source, String>(cell) {

			@Override
			public String getValue(Source object) {
				switch (object.getType()) {
				case FOLDER:
					return object.getFolderPathDisplay();
				case WEB_SERVICE:
					return object.getUrlDisplay();
				case FTP:
				case SFTP:
					return object.getUrlDisplay();
				case D4C:
					return object.getUrlDisplay();
				case MAIL:
					return object.getUrlDisplay();
				default:
					return "";
				}
			}
		};
		colPathOrUrl.setSortable(true);

		Column<Source, String> colFilterOrWsdl = new Column<Source, String>(cell) {

			@Override
			public String getValue(Source object) {
				switch (object.getType()) {
				case FOLDER:
					return object.getFilterDisplay();
				case WEB_SERVICE:
					return object.getMethod() != null ? object.getMethod().getName() : "";
				case FTP:
				case SFTP:
					return object.getFilterDisplay();
				case D4C:
					return object.getCkanPackage().getName();
				case MAIL:
					return object.getFilterDisplay();
				default:
					return "";
				}
			}
		};
		colFilterOrWsdl.setSortable(true);

		sortHandler.setComparator(colType, new Comparator<Source>() {

			@Override
			public int compare(Source o1, Source o2) {
				return o1.getType().compareTo(o2.getType());
			}
		});
		sortHandler.setComparator(colPathOrUrl, new Comparator<Source>() {

			@Override
			public int compare(Source o1, Source o2) {
				String value1 = "";
				switch (o1.getType()) {
				case FOLDER:
					value1 = o1.getFolderPathDisplay();
					break;
				case WEB_SERVICE:
					value1 = o1.getUrlDisplay();
					break;
				case FTP:
				case SFTP:
					value1 = o1.getUrlDisplay();
					break;
				case D4C:
					value1 = o1.getUrlDisplay();
					break;
				case MAIL:
					value1 = o1.getUrlDisplay();
					break;
				}

				String value2 = "";
				switch (o2.getType()) {
				case FOLDER:
					value2 = o2.getFolderPathDisplay();
					break;
				case WEB_SERVICE:
					value2 = o2.getUrlDisplay();
					break;
				case FTP:
				case SFTP:
					value2 = o2.getUrlDisplay();
					break;
				case D4C:
					value2 = o2.getUrlDisplay();
					break;
				case MAIL:
					value2 = o2.getUrlDisplay();
					break;
				}

				return value1.compareTo(value2);
			}
		});
		sortHandler.setComparator(colFilterOrWsdl, new Comparator<Source>() {

			@Override
			public int compare(Source o1, Source o2) {
				String value1 = "";
				switch (o1.getType()) {
				case FOLDER:
					value1 = o1.getFilterDisplay();
					break;
				case WEB_SERVICE:
					value1 = o1.getMethod() != null ? o1.getMethod().getName() : "";
					break;
				case FTP:
				case SFTP:
					value1 = o1.getFilterDisplay();
					break;
				case D4C:
					value1 = o1.getCkanPackage().getName();
					break;
				case MAIL:
					value1 = o1.getFilterDisplay();
					break;
				}

				String value2 = "";
				switch (o2.getType()) {
				case FOLDER:
					value2 = o2.getFilterDisplay();
					break;
				case WEB_SERVICE:
					value2 = o2.getMethod() != null ? o2.getMethod().getName() : "";
					break;
				case FTP:
				case SFTP:
					value2 = o2.getFilterDisplay();
					break;
				case D4C:
					value2 = o2.getCkanPackage().getName();
					break;
				case MAIL:
					value2 = o2.getFilterDisplay();
					break;
				}

				return value1.compareTo(value2);
			}
		});

		List<ColumnWrapper<Source>> columns = new ArrayList<>();
		if (Source.FOLDER_AVAILABLE) {
			columns.add(new ColumnWrapper<Source>(colType, LabelsCommon.lblCnst.Type(), null));
		}
		columns.add(new ColumnWrapper<Source>(colPathOrUrl, LabelsCommon.lblCnst.Description(), null));
		columns.add(new ColumnWrapper<Source>(colFilterOrWsdl, Labels.lblCnst.Options(), null));
		return columns;
	}

	@Override
	protected PropertiesPanel<Resource> buildPropertiesPanel(Resource resource) {
		return new SourceResourceProperties(this, getResourceManager(), resource != null ? (Source) resource : null);
	}
}
