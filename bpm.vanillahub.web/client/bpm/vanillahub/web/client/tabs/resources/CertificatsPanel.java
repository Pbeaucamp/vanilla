package bpm.vanillahub.web.client.tabs.resources;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.resources.ResourcePanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanillahub.core.beans.resources.Certificat;
import bpm.vanillahub.core.beans.resources.Certificat.TypeCertificat;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.images.Images;
import bpm.vanillahub.web.client.properties.resources.CertificatResourceProperties;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;

public class CertificatsPanel extends ResourcePanel<Certificat> {

	public CertificatsPanel(IResourceManager resourceManager) {
		super(Labels.lblCnst.Certificats(), Images.INSTANCE.action_b_24dp(), Labels.lblCnst.AddCertificat(), resourceManager);
	}

	@Override
	public void loadResources() {
		((ResourceManager) getResourceManager()).loadCertificats(this, this);
	}

	@Override
	protected List<ColumnWrapper<Certificat>> buildCustomColumns(TextCell cell, ListHandler<Certificat> sortHandler) {
		Column<Certificat, String> colType = new Column<Certificat, String>(cell) {

			@Override
			public String getValue(Certificat object) {
				switch (object.getTypeCertificat()) {
				case OPEN_PGP:
					return Labels.lblCnst.OpenPGP();
				case PRIVATE_KEY:
					return Labels.lblCnst.PrivateKey();
				default:
					return LabelsCommon.lblCnst.Unknown();
				}
			}
		};
		colType.setSortable(true);

		Column<Certificat, String> colFile = new Column<Certificat, String>(cell) {

			@Override
			public String getValue(Certificat object) {
				if(object.getTypeCertificat() == TypeCertificat.OPEN_PGP) {
					return object.getFile();
				}
				else {
					return Labels.lblCnst.OptionNotNeeded();
				}
			}
		};
		colFile.setSortable(true);

		Column<Certificat, String> colTechnicalName = new Column<Certificat, String>(cell) {

			@Override
			public String getValue(Certificat object) {
				if(object.getTypeCertificat() == TypeCertificat.OPEN_PGP) {
					return object.getTechnicalNameDisplay();
				}
				else {
					return Labels.lblCnst.OptionNotNeeded();
				}
			}
		};
		colTechnicalName.setSortable(true);


		sortHandler.setComparator(colType, new Comparator<Certificat>() {

			@Override
			public int compare(Certificat o1, Certificat o2) {
				return o1.getTypeCertificat().compareTo(o2.getTypeCertificat());
			}
		});
		sortHandler.setComparator(colFile, new Comparator<Certificat>() {

			@Override
			public int compare(Certificat o1, Certificat o2) {
				return o1.getFile().compareTo(o2.getFile());
			}
		});
		sortHandler.setComparator(colTechnicalName, new Comparator<Certificat>() {

			@Override
			public int compare(Certificat o1, Certificat o2) {
				return o1.getTechnicalNameDisplay().compareTo(o2.getTechnicalNameDisplay());
			}
		});

		List<ColumnWrapper<Certificat>> columns = new ArrayList<>();
		columns.add(new ColumnWrapper<Certificat>(colType, LabelsCommon.lblCnst.Type(), "150px"));
		columns.add(new ColumnWrapper<Certificat>(colFile, Labels.lblCnst.File(), null));
		columns.add(new ColumnWrapper<Certificat>(colTechnicalName, Labels.lblCnst.TechnicalName(), "150px"));
		return columns;
	}

	@Override
	protected String getDeleteConfirmMessage() {
		return Labels.lblCnst.DeleteCertificatConfirm();
	}

	@Override
	protected PropertiesPanel<Resource> buildPropertiesPanel(Resource resource) {
		return new CertificatResourceProperties(this, getResourceManager(), resource != null ? (Certificat) resource : null);
	}
}
