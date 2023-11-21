package bpm.fwr.client.dialogs;

import java.util.List;

import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.api.beans.dataset.GroupAgregation;
import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.draggable.HasBin;
import bpm.fwr.client.draggable.dragcontrollers.BinDragController;
import bpm.fwr.client.draggable.dropcontrollers.BinDropController;
import bpm.fwr.client.draggable.widgets.DraggableGroupAggregation;
import bpm.fwr.client.images.WysiwygImage;
import bpm.fwr.client.utils.GroupAggregates;
import bpm.fwr.client.widgets.BinWidget;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class AggregationDialogBox extends AbstractDialogBox implements HasBin, ICustomDialogBox {

	private static AggregationDialogBoxUiBinder uiBinder = GWT.create(AggregationDialogBoxUiBinder.class);

	interface AggregationDialogBoxUiBinder extends UiBinder<Widget, AggregationDialogBox> {
	}
	
	@UiField
	HTMLPanel contentPanel;
	
	private static final String CSS_EMPTY_BIN = "emptyBinAddColumn";
	private static final String CSS_LIST = "listAggregation";
	private static final String CSS_LABEL = "lblAggregationDialog";
	private static final String CSS_TEXT = "txtAggregationDialog";
	private static final String CSS_LIST_BOX = "listBoxAggregationDialog";
	private static final String CSS_BTN_ADD_AGG = "btnAddAgg";
	private static final String CSS_BTN_CONFIRM_ADD = "btnConfirmAdd";
	private static final String CSS_ADD_AGG_PANEL = "addAggPanel";
	private static final String CSS_AGGREGATION_SELECTED = "aggregationSelected";

	private static final int SIZE_ADD_PANEL = 280;

	private DropController binDropController;
	private PickupDragController binDragController;
	
	private AbsolutePanel listAggregation;
	private HTMLPanel addAggPanel;
	
	private Image btnAddAgg, btnModifAgg, btnStyleAgg;
	private ListBox listType, listGroupCol;
	private TextBox txtLabelAgg;
	private Button confirmBtn, btnAddConfirm;
	
	private boolean isAddOpen;
	private boolean isModif;
	
	private DraggableGroupAggregation selectedDraggable;
	
	private GroupAgregation selectedGroupForModif;
	private Column column;
	private List<Column> groups;
	
	public AggregationDialogBox(List<Column> groups, Column column, String defaultLanguage) {
		super(Bpm_fwr.LBLW.Aggregate(), false, true);
		
		setWidget(uiBinder.createAndBindUi(this));
		
		createButton(Bpm_fwr.LBLW.Ok(), okHandler);
		
		this.groups = groups;
		this.column = column;
		
		AbsolutePanel rootPanel = new AbsolutePanel();
		
		btnAddAgg = new Image(WysiwygImage.INSTANCE.add());
		btnAddAgg.addStyleName(CSS_BTN_ADD_AGG);
		btnAddAgg.addClickHandler(clickHandler);
		btnAddAgg.setTitle(Bpm_fwr.LBLW.AddAggregation());
		
		btnModifAgg = new Image(WysiwygImage.INSTANCE.edit());
		btnModifAgg.addStyleName(CSS_BTN_ADD_AGG);
		btnModifAgg.addClickHandler(clickHandler);
		btnModifAgg.setTitle(Bpm_fwr.LBLW.EditAggregation());

		btnStyleAgg = new Image(WysiwygImage.INSTANCE.style());
		btnStyleAgg.addStyleName(CSS_BTN_ADD_AGG);
		btnStyleAgg.addClickHandler(clickHandler);
		btnStyleAgg.setTitle(Bpm_fwr.LBLW.Style());
		
		VerticalPanel panelBtn = new VerticalPanel();
		panelBtn.add(btnAddAgg);
		panelBtn.add(btnModifAgg);
		panelBtn.add(btnStyleAgg);
		
		//AddAggregation Part
		Label lblLabelAgg = new Label(Bpm_fwr.LBLW.AggregationLabel());
		lblLabelAgg.addStyleName(CSS_LABEL);
		
		txtLabelAgg = new TextBox();
		txtLabelAgg.addStyleName(CSS_TEXT);

		Label lblType = new Label(Bpm_fwr.LBLW.AggregationOn());
		lblType.addStyleName(CSS_LABEL);
		
		listType = new ListBox(false);
		listType.addStyleName(CSS_LIST_BOX);
		for(String grAgg : GroupAggregates.AGG_FUNCTIONS) {
			listType.addItem(grAgg, grAgg);
		}
		
		Label lblGroupCol = new Label(Bpm_fwr.LBLW.AggregationType());
		lblGroupCol.addStyleName(CSS_LABEL);
		
		listGroupCol = new ListBox(false);
		listGroupCol.addStyleName(CSS_LIST_BOX);
		listGroupCol.addItem(Bpm_fwr.LBLW.AggregateOnTotal(), GroupAgregation.AGGREGATE_ON_TOTAL);
		if(groups != null){
			for(Column col : groups){
//				if(column.getName().equals(col.getName())) {
					if(defaultLanguage != null && !defaultLanguage.isEmpty()){
						listGroupCol.addItem(col.getTitle(defaultLanguage), col.getName());
					}
					else {
						listGroupCol.addItem(col.getName(), col.getName());
					}
//				}
			}
		}
		
		btnAddConfirm = new Button(Bpm_fwr.LBLW.AddAggregation());
		btnAddConfirm.addStyleName(CSS_BTN_CONFIRM_ADD);
		btnAddConfirm.addClickHandler(clickHandler);
		
		addAggPanel = new HTMLPanel("");
		addAggPanel.addStyleName(CSS_ADD_AGG_PANEL);
		addAggPanel.setWidth("250px");
		addAggPanel.add(lblLabelAgg);
		addAggPanel.add(txtLabelAgg);
		addAggPanel.add(lblType);
		addAggPanel.add(listGroupCol);
		addAggPanel.add(lblGroupCol);
		addAggPanel.add(listType);
		addAggPanel.add(btnAddConfirm);
		
		//ListAggregation Part
		binDragController = new BinDragController(rootPanel, false);

		listAggregation = new AbsolutePanel();
		listAggregation.addStyleName(CSS_LIST);
		
		
		HorizontalPanel middlePanel = new HorizontalPanel();
		middlePanel.add(panelBtn);
		middlePanel.add(addAggPanel);
		middlePanel.add(listAggregation);

		BinWidget imgBin = new BinWidget(WysiwygImage.INSTANCE.empty_bin64());
		imgBin.setStyleName(CSS_EMPTY_BIN);
		
		rootPanel.add(middlePanel);
		rootPanel.add(imgBin);
		
		contentPanel.add(rootPanel);

	    // add drop controller for trash bin
		binDropController = new BinDropController(imgBin, true);
		binDragController.registerDropController(binDropController);
		
		refreshListAggregation();
	}
	
	private void refreshListAggregation(){
		listAggregation.clear();
		
		GroupAgregation gr = column.getGroupAggregation();
		if(gr != null){
			final DraggableGroupAggregation grDrag = new DraggableGroupAggregation(gr, this, gr);
			grDrag.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					if(selectedDraggable != null){
						selectedDraggable.removeStyleName(CSS_AGGREGATION_SELECTED);
					}
					selectedDraggable = grDrag;
					selectedGroupForModif = grDrag.getGroupAgg();
					grDrag.addStyleName(CSS_AGGREGATION_SELECTED);
				}
			});
			binDragController.makeDraggable(grDrag);
			listAggregation.add(grDrag);
		}
	}
	
	public void refreshAddAggPart(){
		txtLabelAgg.setText(selectedGroupForModif.getLabel());
		if(selectedGroupForModif.getColumn().equals(GroupAgregation.AGGREGATE_ON_TOTAL)){
			listGroupCol.setSelectedIndex(0);
		}
		else {
			if(groups != null){
				for(int i=0; i<groups.size(); i++){
					if(groups.get(i).getName().equals(selectedGroupForModif.getColumn())){
						listGroupCol.setSelectedIndex(i + 1);
					}
				}
			}
		}
		
		for(int i=0; i < GroupAggregates.AGG_FUNCTIONS.length; i++){
			if(GroupAggregates.AGG_FUNCTIONS[i].equals(selectedGroupForModif.getType())){
				listType.setSelectedIndex(i);
			}
		}
	}
	
	public void cleanAddAggPart(){
		txtLabelAgg.setText("");
		listType.setSelectedIndex(0);
		listGroupCol.setSelectedIndex(0);
	}
	
	@Override
	public void onDetach() {
		binDragController.unregisterDropController(binDropController);
		super.onDetach();
	}
	
	private ClickHandler okHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			finish(null, null, null);
			AggregationDialogBox.this.hide();
		}
	};
	
	private ClickHandler clickHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			if(event.getSource().equals(btnAddAgg)){
				isModif = false;
				
				cleanAddAggPart();
				slideAnimation.run(1000);
			}
			else if(event.getSource().equals(btnModifAgg)){
				isModif = true;
				
				if(selectedGroupForModif != null){
					refreshAddAggPart();
					slideAnimation.run(1000);
				}
			}
			else if(event.getSource().equals(btnStyleAgg)) {
				if(selectedDraggable != null){
					selectedDraggable.showDialogText(selectedDraggable.getFinishListener());
				}
			}
			else if(event.getSource().equals(btnAddConfirm)){
				String label = txtLabelAgg.getText();
				String columnGroup = listGroupCol.getValue(listGroupCol.getSelectedIndex());
				String type = listType.getValue(listType.getSelectedIndex());
				
				if(!label.isEmpty()){
					if(!isModif){	
						GroupAgregation gr = new GroupAgregation();
						gr.setColumn(columnGroup);
						gr.setLabel(label);
						gr.setType(type);
						
						column.setGroupAggregation(gr);
					}
					else {
						selectedGroupForModif.setColumn(columnGroup);
						selectedGroupForModif.setLabel(label);
						selectedGroupForModif.setType(type);
					}
					refreshListAggregation();
					slideAnimation.run(1000);
				}
			}
		}
	};
	
	private Animation slideAnimation = new Animation() {

		@Override
		protected void onUpdate(double progress) {
			if(!isAddOpen){
				int progressValue = (int)(-(SIZE_ADD_PANEL)*(1-progress));
				DOM.setStyleAttribute(addAggPanel.getElement(), "marginRight", progressValue + "px");
			}
			else {
				int progressValue = (int)(-(SIZE_ADD_PANEL)*(progress));
				DOM.setStyleAttribute(addAggPanel.getElement(), "marginRight", progressValue + "px");
			}
		}
		
		protected void onComplete() {
			isAddOpen = !isAddOpen;
			if(isAddOpen){
				btnAddAgg.setVisible(false);
				btnModifAgg.setVisible(false);
				btnStyleAgg.setVisible(false);
			}
			else {
				btnAddAgg.setVisible(true);
				btnModifAgg.setVisible(true);
				btnStyleAgg.setVisible(true);
			}
		};
	};

	@Override
	public void updateBtn() {
		confirmBtn.setEnabled(true);
	}

	@Override
	public void widgetToTrash(Object widget) {
		if(widget instanceof DraggableGroupAggregation){
			column.setGroupAggregation(null);
			((DraggableGroupAggregation) widget).removeFromParent();
		}
	}
}
