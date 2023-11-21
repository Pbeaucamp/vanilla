package bpm.vanillahub.web.client.tabs.resources;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.resources.ResourcePanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanillahub.core.beans.resources.ApplicationServer;
import bpm.vanillahub.core.beans.resources.VanillaServer;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.images.Images;
import bpm.vanillahub.web.client.properties.resources.ApplicationServerResourceProperties;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;

public class ApplicationServerPanel extends ResourcePanel<ApplicationServer> {

	public ApplicationServerPanel(IResourceManager resourceManager) {
		super(Labels.lblCnst.ApplicationServers(), Images.INSTANCE.action_b_24dp(), Labels.lblCnst.AddApplicationServer(), resourceManager);
	}

	@Override
	public void loadResources() {
		((ResourceManager) getResourceManager()).loadApplicationServers(this, this);
	}

	@Override
	protected String getDeleteConfirmMessage() {
		return Labels.lblCnst.DeleteApplicationServerConfirm();
	}

	@Override
	protected List<ColumnWrapper<ApplicationServer>> buildCustomColumns(TextCell cell, ListHandler<ApplicationServer> sortHandler) {

		Column<ApplicationServer, String> colType = new Column<ApplicationServer, String>(cell) {

			@Override
			public String getValue(ApplicationServer object) {
				switch (object.getTypeServer()) {
				case VANILLA:
					return Labels.lblCnst.VanillaServer();
				case AKLABOX:
					return Labels.lblCnst.AklaboxServers();
				case LIMESURVEY:
					return Labels.lblCnst.LimeSurveyServers();
				default:
					break;
				}
				return LabelsConstants.lblCnst.Unknown();
			}
		};
		colType.setSortable(true);

		Column<ApplicationServer, String> colUrl = new Column<ApplicationServer, String>(cell) {

			@Override
			public String getValue(ApplicationServer object) {
				return object.getUrlDisplay();
			}
		};
		colUrl.setSortable(true);

		Column<ApplicationServer, String> colUser = new Column<ApplicationServer, String>(cell) {

			@Override
			public String getValue(ApplicationServer object) {
				return object.getLoginDisplay();
			}
		};
		colUser.setSortable(true);

		Column<ApplicationServer, String> colGroup = new Column<ApplicationServer, String>(cell) {

			@Override
			public String getValue(ApplicationServer object) {
				return object instanceof VanillaServer ? ((VanillaServer) object).getGroupName() : LabelsConstants.lblCnst.NotDefined();
			}
		};
		colGroup.setSortable(true);
		
		Column<ApplicationServer, String> colRepo = new Column<ApplicationServer, String>(cell) {

			@Override
			public String getValue(ApplicationServer object) {
				return object instanceof VanillaServer ? ((VanillaServer) object).getRepositoryName() : LabelsConstants.lblCnst.NotDefined();
			}
		};
		colRepo.setSortable(true);


		sortHandler.setComparator(colType, new Comparator<ApplicationServer>() {

			@Override
			public int compare(ApplicationServer o1, ApplicationServer o2) {
				return o1.getTypeServer().compareTo(o2.getTypeServer());
			}
		});
		sortHandler.setComparator(colUrl, new Comparator<ApplicationServer>() {

			@Override
			public int compare(ApplicationServer o1, ApplicationServer o2) {
				return o1.getUrlDisplay().compareTo(o2.getUrlDisplay());
			}
		});
		sortHandler.setComparator(colUser, new Comparator<ApplicationServer>() {

			@Override
			public int compare(ApplicationServer o1, ApplicationServer o2) {
				return o1.getLoginDisplay().compareTo(o2.getLoginDisplay());
			}
		});
		sortHandler.setComparator(colGroup, new Comparator<ApplicationServer>() {

			@Override
			public int compare(ApplicationServer o1, ApplicationServer o2) {
				if (o1 instanceof VanillaServer && o2 instanceof VanillaServer) {
					return ((VanillaServer) o1).getGroupName().compareTo(((VanillaServer) o2).getGroupName());
				}
				return 0;
			}
		});
		sortHandler.setComparator(colRepo, new Comparator<ApplicationServer>() {

			@Override
			public int compare(ApplicationServer o1, ApplicationServer o2) {
				return ((VanillaServer) o1).getRepositoryName().compareTo(((VanillaServer) o2).getRepositoryName());
			}
		});


		List<ColumnWrapper<ApplicationServer>> columns = new ArrayList<>();
		columns.add(new ColumnWrapper<ApplicationServer>(colType, Labels.lblCnst.ServerType(), "150px"));
		columns.add(new ColumnWrapper<ApplicationServer>(colUrl, LabelsCommon.lblCnst.URL(), "150px"));
		columns.add(new ColumnWrapper<ApplicationServer>(colUser, LabelsCommon.lblCnst.Login(), null));
		columns.add(new ColumnWrapper<ApplicationServer>(colGroup, Labels.lblCnst.Group(), "150px"));
		columns.add(new ColumnWrapper<ApplicationServer>(colRepo, Labels.lblCnst.Repository(), "150px"));
		return columns;
	}

	@Override
	protected PropertiesPanel<Resource> buildPropertiesPanel(Resource resource) {
		return new ApplicationServerResourceProperties(this, getResourceManager(), resource != null ? (ApplicationServer) resource : null);
	}

}
