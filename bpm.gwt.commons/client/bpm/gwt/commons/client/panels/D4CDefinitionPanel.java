package bpm.gwt.commons.client.panels;

import java.util.Comparator;
import java.util.List;

import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.HasActionCell;
import bpm.gwt.commons.client.custom.v2.Button;
import bpm.gwt.commons.client.custom.v2.CompositeCellHelper;
import bpm.gwt.commons.client.custom.v2.GridPanel;
import bpm.gwt.commons.client.dialog.AddD4CDialog;
import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.vanilla.platform.core.ManageAction;
import bpm.vanilla.platform.core.beans.resources.D4C;

/**
 * Widget to manage a D4C server definition
 *
 */
public class D4CDefinitionPanel extends Composite {

	private static D4CDefinitionPanelUiBinder uiBinder = GWT.create(D4CDefinitionPanelUiBinder.class);

	interface D4CDefinitionPanelUiBinder extends UiBinder<Widget, D4CDefinitionPanel> {
	}

	@UiField
	GridPanel<D4C> grid;
	
	private IWait waitPanel;
	
	private SingleSelectionModel<D4C> singleSelectionPanel;
	
	private boolean full;

	public D4CDefinitionPanel(IWait waitPanel, Handler selectionChangeHandler, boolean full) {
		this.waitPanel = waitPanel;
		this.full = full;
		initWidget(uiBinder.createAndBindUi(this));
		
		buildGrid(selectionChangeHandler);
		
		loadItems();
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				grid.setTopManually(30);
			}
		});
	}
	
	private void loadItems() {
		CommonService.Connect.getInstance().getD4CDefinitions(new GwtCallbackWrapper<List<D4C>>(waitPanel, true, true) {

			@Override
			public void onSuccess(List<D4C> result) {
				grid.loadItems(result);
			}
		}.getAsyncCallback());
	}

	private void buildGrid(Handler selectionChangeHandler) {
		Button btnAdd = new Button(LabelsConstants.lblCnst.AddServer(), CommonImages.INSTANCE.add_24());
		btnAdd.addClickHandler(addHandler);
		
		grid.setActionPanel("", btnAdd);
		
		TextCell cell = new TextCell();
		Column<D4C, String> colName = new Column<D4C, String>(cell) {
			@Override
			public String getValue(D4C object) {
				return object.getName();
			}
		};
		Column<D4C, String> colUrl = new Column<D4C, String>(cell) {
			@Override
			public String getValue(D4C object) {
				return object.getUrl();
			}
		};

		HasActionCell<D4C> editCell = new HasActionCell<D4C>(CommonImages.INSTANCE.edit_24(), LabelsConstants.lblCnst.Edit(), new Delegate<D4C>() {

			@Override
			public void execute(D4C object) {
				manageItem(object);
			}
		});

		HasActionCell<D4C> deleteCell = new HasActionCell<D4C>(CommonImages.INSTANCE.delete_24(), LabelsConstants.lblCnst.Delete(), new Delegate<D4C>() {

			@Override
			public void execute(D4C object) {
				deleteItem(object);
			}
		});

		CompositeCellHelper<D4C> compCell = new CompositeCellHelper<D4C>(editCell, deleteCell);
		Column<D4C, D4C> colAction = new Column<D4C, D4C>(compCell.getCell()) {
			@Override
			public D4C getValue(D4C object) {
				return object;
			}
		};
		
		grid.addColumn(LabelsConstants.lblCnst.Name(), colName, "100px", new Comparator<D4C>() {

			@Override
			public int compare(D4C o1, D4C o2) {
				return grid.compare(o1.getName(), o2.getName());
			}
		});
		grid.addColumn(LabelsConstants.lblCnst.Url(), colUrl, "150px", new Comparator<D4C>() {

			@Override
			public int compare(D4C o1, D4C o2) {
				return grid.compare(o1.getUrl(), o2.getUrl());
			}
		});
		grid.addColumn("", colAction, "70px", null);
		
		singleSelectionPanel = new SingleSelectionModel<>();
		if (selectionChangeHandler != null) {
			singleSelectionPanel.addSelectionChangeHandler(selectionChangeHandler);
		}
		grid.setSelectionModel(singleSelectionPanel);
		grid.setPageVisible(false);
	}
	
	private ClickHandler addHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			manageItem(null);
		}
	};
	
	private void manageItem(D4C item) {
		final AddD4CDialog dial = new AddD4CDialog(item, full);
		dial.center();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					D4C item = dial.getItem();
					CommonService.Connect.getInstance().manageD4CDefinition(item, ManageAction.SAVE_OR_UPDATE, new GwtCallbackWrapper<D4C>(waitPanel, true, true) {

						@Override
						public void onSuccess(D4C result) {
							loadItems();
						}
					}.getAsyncCallback());
				}
			}
		});
	}
	
	private void deleteItem(D4C item) {
		CommonService.Connect.getInstance().manageD4CDefinition(item, ManageAction.DELETE, new GwtCallbackWrapper<D4C>(waitPanel, true, true) {

			@Override
			public void onSuccess(D4C result) {
				loadItems();
			}
		}.getAsyncCallback());
	}
	
	public D4C getSelectedItem() {
		return singleSelectionPanel.getSelectedObject();
	}
}
