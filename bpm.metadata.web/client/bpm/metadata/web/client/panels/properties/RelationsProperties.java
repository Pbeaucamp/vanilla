package bpm.metadata.web.client.panels.properties;

import java.util.List;

import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataRelation;
import bpm.gwt.commons.shared.fmdt.metadata.TableRelation;
import bpm.metadata.web.client.panels.TableRelationCreationPanel;
import bpm.vanilla.platform.core.beans.data.Datasource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class RelationsProperties extends Composite implements IPanelProperties {

	private static RelationsPropertiesUiBinder uiBinder = GWT.create(RelationsPropertiesUiBinder.class);

	interface RelationsPropertiesUiBinder extends UiBinder<Widget, RelationsProperties> {
	}
	
	@UiField
	TableRelationCreationPanel relationCreationPanel;
	
	private MetadataRelation relation;
	
	public RelationsProperties(IWait waitPanel, int userId, Datasource datasource, MetadataRelation relation) {
		initWidget(uiBinder.createAndBindUi(this));
		this.relation = relation;

		relationCreationPanel.loadTables(datasource.getTables(), relation);
	}

	@Override
	public void apply() {
		List<TableRelation> relations = relationCreationPanel.getRelations();
		relation.setRelations(relations);
	}

	@Override
	public boolean isValid() {
		return true;
	}
}
