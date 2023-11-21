package bpm.metadata.web.client.panels.properties;

import bpm.gwt.commons.client.tree.MetadataTreeItem;
import bpm.gwt.commons.shared.fmdt.metadata.Metadata;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataModel;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataPackage;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataRelation;
import bpm.metadata.web.client.I18N.Labels;
import bpm.metadata.web.client.panels.MetadataPanel;
import bpm.vanilla.platform.core.beans.data.DatabaseColumn;
import bpm.vanilla.platform.core.beans.data.DatabaseTable;
import bpm.vanilla.platform.core.beans.data.Datasource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class PanelProperties extends Composite {

	private static MetadataPropertiesUiBinder uiBinder = GWT.create(MetadataPropertiesUiBinder.class);

	interface MetadataPropertiesUiBinder extends UiBinder<Widget, PanelProperties> {
	}
	
	interface MyStyle extends CssResource {
		String lblNoProperties();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	SimplePanel panelContent;
	
	@UiField
	Button btnApply, btnCancel;

	private MetadataPanel parent;
	private int userId;
	
	private Label lblNoProperties;
	
	private IPanelProperties panelProperties;
	private MetadataTreeItem<?> treeItem;

	public PanelProperties(MetadataPanel parent, int userId) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.userId = userId;
		
		lblNoProperties = new Label(Labels.lblCnst.NoProperties());
		lblNoProperties.setStyleName(style.lblNoProperties());
		
		panelContent.setWidget(lblNoProperties);
		
		btnApply.setEnabled(false);
		btnCancel.setEnabled(false);
	}

	public void buildContent(MetadataTreeItem<?> treeItem) {
		this.treeItem = treeItem;
		Object metadataItem = treeItem.getItem();
		
		if (metadataItem instanceof Metadata) {
			panelProperties = new MetadataProperties((Metadata) metadataItem);
		}
		else if (metadataItem instanceof Datasource) {
			panelProperties = new DatasourceProperties(parent, userId, parent.getMetadata(), (Datasource) metadataItem);
		}
		else if (metadataItem instanceof MetadataModel) {
			panelProperties = new ModelProperties((MetadataModel) metadataItem);
		}
		else if (metadataItem instanceof MetadataPackage) {
			panelProperties = new PackageProperties(parent, parent.getMetadata(), parent.getMetadata().getDatasource(), ((MetadataPackage) metadataItem).getParent(), (MetadataPackage) metadataItem);
		}
		else if (metadataItem instanceof MetadataRelation) {
			panelProperties = new RelationsProperties(parent, userId, parent.getMetadata().getDatasource(), (MetadataRelation) metadataItem);
		}
		else if (metadataItem instanceof DatabaseTable) {
			panelProperties = new TableProperties(parent, parent.getMetadata(), (DatabaseTable) metadataItem);
		}
		else if (metadataItem instanceof DatabaseColumn) {
			panelProperties = new ColumnProperties(parent, parent.getMetadata().getDatasource(), findPackageParent(treeItem), (DatabaseColumn) metadataItem);
		}
		else {
			panelContent.setWidget(lblNoProperties);
			btnApply.setEnabled(false);
			btnCancel.setEnabled(false);
			return;
		}
		panelContent.setWidget((Widget) panelProperties);
		btnApply.setEnabled(true);
		btnCancel.setEnabled(true);
	}

	private MetadataPackage findPackageParent(MetadataTreeItem<?> treeItem) {
		if (treeItem.getParentItem() != null && treeItem.getParentItem() instanceof MetadataTreeItem<?>) {
			MetadataTreeItem<?> metadataItem = (MetadataTreeItem<?>) treeItem.getParentItem();
			if (metadataItem.getItem() instanceof MetadataPackage) {
				return (MetadataPackage) metadataItem.getItem();
			}
			else {
				return findPackageParent(metadataItem);
			}
		}
		return null;
	}

	@UiHandler("btnApply")
	public void onApplyClick(ClickEvent event) {
		if (panelProperties != null && panelProperties.isValid()) {
			panelProperties.apply();
			parent.refreshTree();
		}
	}
	
	@UiHandler("btnCancel")
	public void onCancelClick(ClickEvent event) {
		if (treeItem != null) {
			buildContent(treeItem);
		}
	}
}
