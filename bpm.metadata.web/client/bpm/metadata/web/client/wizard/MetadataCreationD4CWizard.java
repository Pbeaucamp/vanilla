package bpm.metadata.web.client.wizard;

import java.util.List;

import com.google.gwt.user.client.ui.Composite;

import bpm.gwt.commons.client.services.FmdtServices;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.wizard.GwtWizard;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.shared.fmdt.metadata.Metadata;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataModel;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataPackage;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataRelation;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataResource;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataResources;
import bpm.gwt.commons.shared.fmdt.metadata.TableRelation;
import bpm.metadata.web.client.I18N.Labels;
import bpm.vanilla.platform.core.beans.data.DatabaseTable;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.resources.D4C;

public class MetadataCreationD4CWizard extends GwtWizard {

	private IGwtPage currentPage;

	private MetadataOptionPage optionPage;
	private DatasourceD4CDefinitionPage dsPage;
	private TableSelectionPage tableSelectionPage;
	private TableRelationPage tableRelationPage;
	private ColumnOptionPage columnOptionPage;
	
	private int userId;
	
	public MetadataCreationD4CWizard(int userId) {
		super(Labels.lblCnst.CreateMetadata());
		this.userId = userId;
		
		optionPage = new MetadataOptionPage();
		setCurrentPage(optionPage);
	}

	@Override
	public boolean canFinish() {
		return optionPage.isComplete() && dsPage != null && dsPage.isComplete() && tableSelectionPage != null && tableSelectionPage.isComplete() 
				&& tableRelationPage != null && tableRelationPage.isComplete() && columnOptionPage != null && columnOptionPage.isComplete();
	}

	@Override
	public void updateBtn() {
		setBtnBackState(currentPage.canGoBack() ? true : false);
		setBtnNextState(currentPage.canGoFurther() ? true : false);
		setBtnFinishState(canFinish() ? true : false);
	}

	@Override
	public void setCurrentPage(IGwtPage page) {
		setContentPanel((Composite) page);
		currentPage = page;
		updateBtn();
	}

	@Override
	protected void onNextClick() {
		if (currentPage instanceof MetadataOptionPage) {
			if (dsPage == null) {
				dsPage = new DatasourceD4CDefinitionPage(MetadataCreationD4CWizard.this, userId);
			}
			setCurrentPage(dsPage);
		}
		else if (currentPage instanceof DatasourceD4CDefinitionPage) {			
			Datasource datasource = getDatasource();
			if(datasource != null) {
				D4C d4c = dsPage.getD4C();
				String organisation = dsPage.getD4COrganisation();
				
				FmdtServices.Connect.getInstance().getDatabaseStructure(d4c, organisation, datasource, false, new GwtCallbackWrapper<List<DatabaseTable>>(this, true, true) {

					@Override
					public void onSuccess(List<DatabaseTable> result) {
						if (tableSelectionPage == null) {
							tableSelectionPage = new TableSelectionPage(MetadataCreationD4CWizard.this);
						}
						tableSelectionPage.buildPhysicalTree(result);
						setCurrentPage(tableSelectionPage);
					}
				}.getAsyncCallback());
			}
		}
		else if (currentPage instanceof TableSelectionPage) {
			List<DatabaseTable> logicalTables = tableSelectionPage.getLogicalTables();
			
			if (tableRelationPage == null) {
				tableRelationPage = new TableRelationPage(MetadataCreationD4CWizard.this);
			}
			tableRelationPage.loadTables(logicalTables);
			setCurrentPage(tableRelationPage);
		}
		else if (currentPage instanceof TableRelationPage) {
			Datasource datasource = dsPage.getDatasource();
			List<DatabaseTable> logicalTables = tableSelectionPage.getLogicalTables();
			
			if (columnOptionPage == null) {
				columnOptionPage = new ColumnOptionPage(MetadataCreationD4CWizard.this, datasource);
			}
			columnOptionPage.buildLogicalTree(logicalTables);
			setCurrentPage(columnOptionPage);
		}
	}

	private Datasource getDatasource() {
		return dsPage != null ? dsPage.getDatasource() : null;
	}

	@Override
	protected void onBackClick() {
		if (currentPage instanceof DatasourceDefinitionPage) {
			setCurrentPage(optionPage);
		}
		else if (currentPage instanceof TableSelectionPage) {
			setCurrentPage(dsPage);
		}
		else if (currentPage instanceof TableRelationPage) {
			setCurrentPage(tableSelectionPage);
		}
		else if (currentPage instanceof ColumnOptionPage) {
			setCurrentPage(tableRelationPage);
		}
	}

	@Override
	protected void onClickFinish() {
		String name = optionPage.getName();
		String description = optionPage.getDescription();
		
		Datasource datasource = dsPage.getDatasource();
		D4C d4cServer = dsPage.getD4C();
		String d4cOrganisation = dsPage.getD4COrganisation();
		List<DatabaseTable> logicalTables = tableSelectionPage.getLogicalTables();
		List<TableRelation> relations = tableRelationPage.getRelations();
		List<MetadataResource> resources = columnOptionPage.getResourcesAndApplyOptions();
		
		if (datasource == null || logicalTables == null || logicalTables.isEmpty()) {
			//TODO: Message
			return;
		}
		
		datasource.setTables(logicalTables);
		
		MetadataPackage pack = new MetadataPackage("Default pack", "", logicalTables, new MetadataResources(resources), null);
		MetadataModel model = new MetadataModel("Default model", pack, logicalTables, new MetadataRelation(relations));
		
		Metadata metadata = new Metadata(name, description, datasource, model);
		metadata.setD4cServer(d4cServer);
		metadata.setD4cOrganisation(d4cOrganisation);
		
		finish(metadata, MetadataCreationD4CWizard.this, null);
		hide();
	}

}
