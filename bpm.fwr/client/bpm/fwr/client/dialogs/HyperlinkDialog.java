package bpm.fwr.client.dialogs;

import java.util.List;

import bpm.fwr.api.beans.HyperlinkColumn;
import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.action.ActionType;
import bpm.fwr.client.draggable.dragcontrollers.DataDragController;
import bpm.fwr.client.draggable.dropcontrollers.TextDropController;
import bpm.fwr.client.draggable.widgets.DraggableColumn;
import bpm.fwr.client.draggable.widgets.DraggableHTMLPanel;
import bpm.fwr.client.tree.DatasetsTree;
import bpm.fwr.client.tree.MetadataTree;
import bpm.fwr.client.widgets.ListWidget;
import bpm.fwr.client.widgets.TextBoxWidget;
import bpm.fwr.client.widgets.TextBoxWidget.TextBoxType;
import bpm.fwr.client.wizard.IManageTextBoxData;
import bpm.fwr.shared.models.metadata.FwrMetadata;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.utils.MessageHelper;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class HyperlinkDialog extends AbstractDialogBox implements IManageTextBoxData{

	private static HyperlinkDialogUiBinder uiBinder = GWT
			.create(HyperlinkDialogUiBinder.class);

	interface HyperlinkDialogUiBinder extends UiBinder<Widget, HyperlinkDialog> {
	}
	
	interface MyStyle extends CssResource {
		String txtColumn();
	}
	
	@UiField
	HTMLPanel contentPanel;
	
	@UiField
	AbsolutePanel rootPanel;
	
	@UiField
	TabLayoutPanel tabPanel;
	
	@UiField
	Label lblLabelUrl, lblUrl, lblColumnHyperlink, lblInformations;
	
	@UiField
	TextBox txtLabelUrl, txtUrl;
	
	@UiField
	CheckBox checkKeepValues;
	
	@UiField
	SimplePanel datasetTreePanel, datasetPanel, panelColumnHyperlink;

	@UiField
	MyStyle style;
	
	private PickupDragController puDragCtrl;
	private TextBoxWidget txtColumnHyperlink;
	private TextDropController txtHyperlinkDropCtrl;
	
	private ListWidget listWidgetParent;
	private int indexCell;

	private DraggableHTMLPanel panelColumn;
	private boolean suppressTheColumnFirst = false;

	public HyperlinkDialog(ListWidget listWidgetParent, int indexCell, List<FwrMetadata> metadatas, List<DataSet> dsAvailable, DraggableHTMLPanel panelColumn) {
		super(Bpm_fwr.LBLW.HyperlinkDefinition(), false, true);
		this.listWidgetParent = listWidgetParent;
		this.indexCell = indexCell;
		this.panelColumn = panelColumn;
		
		setWidget(uiBinder.createAndBindUi(this));
		
		createButtonBar(Bpm_fwr.LBLW.Apply(), okHandler, Bpm_fwr.LBLW.Cancel(), cancelHandler);

		this.puDragCtrl = new DataDragController(rootPanel, false);
		
		//Add datasetTree part
		MetadataTree metadataTree = new MetadataTree(null, puDragCtrl, null, false);
		metadataTree.setHeight("100%");
		metadataTree.setMetadatas(metadatas);
		datasetTreePanel.add(metadataTree);
		
		DatasetsTree datasetsTree = new DatasetsTree(dsAvailable, puDragCtrl);
		datasetsTree.setHeight("100%");
		datasetPanel.add(datasetsTree);
		
		txtColumnHyperlink = new TextBoxWidget(this, TextBoxType.HYPERLINK);
		txtColumnHyperlink.addStyleName(style.txtColumn());
		txtColumnHyperlink.setText(Bpm_fwr.LBLW.DropColonneHere());
		txtColumnHyperlink.setEnabled(false);
		
		panelColumnHyperlink.setWidget(txtColumnHyperlink);
		txtHyperlinkDropCtrl = new TextDropController(txtColumnHyperlink);
		
		lblLabelUrl.setText(Bpm_fwr.LBLW.UrlLabel() + ": ");
		lblUrl.setText(Bpm_fwr.LBLW.Url() + ": ");
		lblColumnHyperlink.setText(Bpm_fwr.LBLW.UrlColumn() + ": ");
		checkKeepValues.setText(Bpm_fwr.LBLW.UrlKeepValues());
		lblInformations.setText(Bpm_fwr.LBLW.UrlInformations());
		
		if(panelColumn != null){
			
			if(panelColumn.getColumn().getColumn() instanceof HyperlinkColumn) {
				HyperlinkColumn linkCol = (HyperlinkColumn)panelColumn.getColumn().getColumn();
				txtLabelUrl.setText(linkCol.getLabelUrl());
				txtUrl.setText(linkCol.getUrl());
				txtColumnHyperlink.setColumn(linkCol);
				txtColumnHyperlink.setText(linkCol.getName());
				checkKeepValues.setValue(linkCol.keepColumnValues());
				
				suppressTheColumnFirst = true;
			}
		}
	}
	
	@UiHandler("checkKeepValues")
	public void onKeepValuesChange(ValueChangeEvent<Boolean> event){
		if(event.getValue()){
			txtLabelUrl.setEnabled(false);
		}
		else {
			txtLabelUrl.setEnabled(true);
		}
	}

	private HyperlinkColumn createHyperlink() {
		String labelUrl = txtLabelUrl.getText();
		String url = txtUrl.getText();
		boolean keepValues = checkKeepValues.getValue();
		Column col = txtColumnHyperlink.getColumn();
		
		if(url.isEmpty() || (labelUrl.isEmpty() && !keepValues) || col == null){
			return null;
		}
		return transformColToHyperlink(col, labelUrl, url, keepValues);
	}
	
	private HyperlinkColumn transformColToHyperlink(Column col, String labelUrl, String url, boolean keepValues) {
		HyperlinkColumn column = new HyperlinkColumn();
		column.setGroupAggregation(col.getGroupAggregation());
		column.setAgregate(col.isAgregate());
		column.setCalculated(col.isCalculated());
		column.setExpression(col.getExpression());
		column.setFormat(col.getFormat());
		column.setJavaClass(col.getJavaClass());
		column.setMetadataId(col.getMetadataId());
		column.setMetadataParent(col.getMetadataParent());
		column.setBusinessModelParent(col.getBusinessModelParent());
		column.setName(col.getName());
		column.setBusinessPackageParent(col.getBusinessPackageParent());
		column.setBusinessTableParent(col.getBusinessTableParent());
		
		column.setDatasetParent(col.getDatasetParent());
		column.setSorted(col.isSorted());
		column.setSortType(col.getSortType());

		column.setTitle(col.getTitle());
		
		column.setType(col.getType());
		column.setWidth(col.getWidth());
		column.setLabelUrl(labelUrl);
		column.setUrl(url);
		column.setKeepColumnValues(keepValues);
		
		return column;
	}

	@Override
	public void onAttach() {
		puDragCtrl.registerDropController(txtHyperlinkDropCtrl);
		super.onAttach();
	}
	
	@Override
	public void onDetach() {
		puDragCtrl.unregisterDropController(txtHyperlinkDropCtrl);
		super.onDetach();
	}
	
	private ClickHandler okHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			HyperlinkColumn hyperlink = createHyperlink();
			if(hyperlink ==  null){
				MessageHelper.openMessageDialog(Bpm_fwr.LBLW.Information(), Bpm_fwr.LBLW.FillInformations());
			}
			else {
				listWidgetParent.manageHyperlink(hyperlink, indexCell, true, suppressTheColumnFirst, panelColumn, ActionType.DEFAULT);
				
				HyperlinkDialog.this.hide();
			}
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			HyperlinkDialog.this.hide();
		}
	};

	@Override
	public void manageWidget(DraggableColumn widget, TextBoxType type) {
		txtColumnHyperlink.setColumn(widget.getColumn());
		txtColumnHyperlink.setText(widget.getColumn().getName());
	}
}
