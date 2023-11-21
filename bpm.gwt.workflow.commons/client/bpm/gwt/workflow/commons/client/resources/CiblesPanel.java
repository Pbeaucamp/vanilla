package bpm.gwt.workflow.commons.client.resources;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.images.Images;
import bpm.gwt.workflow.commons.client.resources.properties.CibleResourceProperties;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.workflow.commons.resources.Cible;
import bpm.workflow.commons.resources.Cible.TypeCible;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;

public class CiblesPanel extends ResourcePanel<Cible> {

	public CiblesPanel(IResourceManager resourceManager) {
		super(LabelsCommon.lblCnst.Cibles(), Images.INSTANCE.cible_b_24dp(), LabelsCommon.lblCnst.AddCible(), resourceManager);
	}

	@Override
	public void loadResources() {
		getResourceManager().loadCibles(this, this);
	}

	@Override
	protected String getDeleteConfirmMessage() {
		return LabelsCommon.lblCnst.DeleteCibleConfirm();
	}

	@Override
	protected List<ColumnWrapper<Cible>> buildCustomColumns(TextCell cell, ListHandler<Cible> sortHandler) {
		Column<Cible, String> colType = new Column<Cible, String>(cell) {

			@Override
			public String getValue(Cible object) {
				return object.getType().toString();
			}
		};
		colType.setSortable(true);

		Column<Cible, String> colUrl = new Column<Cible, String>(cell) {

			@Override
			public String getValue(Cible object) {
				return object.getFolderDisplay() != null && !object.getFolderDisplay().isEmpty() ? object.getFolderDisplay() : object.getUrlDisplay();
			}
		};
		colUrl.setSortable(true);

		Column<Cible, String> colOptions = new Column<Cible, String>(cell) {

			@Override
			public String getValue(Cible object) {
				return object.getType() == TypeCible.CKAN ? (object.getCkanPackage() != null ? object.getCkanPackage().getName() : "" ) : object.getPortDisplay();
			}
		};
		colOptions.setSortable(true);

		sortHandler.setComparator(colType, new Comparator<Cible>() {

			@Override
			public int compare(Cible o1, Cible o2) {
				return o1.getType().compareTo(o2.getType());
			}
		});
		sortHandler.setComparator(colUrl, new Comparator<Cible>() {

			@Override
			public int compare(Cible o1, Cible o2) {
				return o1.getUrlDisplay().compareTo(o2.getUrlDisplay());
			}
		});
		sortHandler.setComparator(colOptions, new Comparator<Cible>() {

			@Override
			public int compare(Cible o1, Cible o2) {
				String o1Option = o1.getType() == TypeCible.CKAN ? (o1.getCkanPackage() != null ? o1.getCkanPackage().getName() : "" ) : o1.getPortDisplay();
				String o2Option = o2.getType() == TypeCible.CKAN ? (o2.getCkanPackage() != null ? o2.getCkanPackage().getName() : "" ) : o2.getPortDisplay();
				return o1Option.compareTo(o2Option);
			}
		});

		List<ColumnWrapper<Cible>> columns = new ArrayList<>();
		columns.add(new ColumnWrapper<Cible>(colType, LabelsCommon.lblCnst.Type(), "150px"));
		columns.add(new ColumnWrapper<Cible>(colUrl, LabelsCommon.lblCnst.URL(), null));
		columns.add(new ColumnWrapper<Cible>(colOptions, LabelsCommon.lblCnst.Options(), "150px"));
		return columns;
	}

	@Override
	protected PropertiesPanel<Resource> buildPropertiesPanel(Resource resource) {
		return new CibleResourceProperties(this, getResourceManager(), resource != null ? (Cible) resource : null);
	}
}
