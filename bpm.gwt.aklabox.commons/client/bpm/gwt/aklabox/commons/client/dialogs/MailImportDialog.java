package bpm.gwt.aklabox.commons.client.dialogs;

import java.util.ArrayList;
import java.util.List;

import bpm.document.management.core.model.AkladematAdminEntity;
import bpm.document.management.core.model.AkladematAdminEntity.Status;
import bpm.document.management.core.model.MailEntity;
import bpm.document.management.core.model.MailFilter;
import bpm.document.management.core.model.MailServer;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.customs.LabelDateBox;
import bpm.gwt.aklabox.commons.client.customs.LabelTextBox;
import bpm.gwt.aklabox.commons.client.customs.ListBoxWithButton;
import bpm.gwt.aklabox.commons.client.services.AklaCommonService;
import bpm.gwt.aklabox.commons.client.services.GwtCallbackWrapper;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;

public class MailImportDialog extends AbstractDialogBox {

	private static MailImportDialogUiBinder uiBinder = GWT.create(MailImportDialogUiBinder.class);

	private DateTimeFormat dateFormat = DateTimeFormat.getFormat("yyyy-MM-dd");

	interface MailImportDialogUiBinder extends UiBinder<Widget, MailImportDialog> {
	}

	@UiField
	ListBoxWithButton<MailServer> lstMailServer;

	@UiField
	LabelTextBox txtSubject, txtFrom;

	@UiField
	LabelDateBox txtDateStart, txtDateEnd;

	@UiField
	SimplePanel gridPanel, pagerPanel;

	private ListDataProvider<AkladematAdminEntity<MailEntity>> dataprovider = new ListDataProvider<>();
	private MultiSelectionModel<AkladematAdminEntity<MailEntity>> selectionModel = new MultiSelectionModel<>();

	private boolean confirm = false;

	public boolean isConfirm() {
		return confirm;
	}

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			confirm = true;
			hide();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			confirm = false;
			hide();
		}
	};

	public MailImportDialog() {
		super("Importer Mails", true, true);
		setWidget(uiBinder.createAndBindUi(this));

		createDatagrid();

		AklaCommonService.Connect.getService().getMailServers(new GwtCallbackWrapper<List<MailServer>>(null, false, false) {
			@Override
			public void onSuccess(List<MailServer> result) {
				lstMailServer.setList(result, true);
			}
		}.getAsyncCallback());

		lstMailServer.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				MailFilter filter = new MailFilter();

				filter.setSubject(txtSubject.getText());
				filter.setFrom(txtFrom.getText());
				filter.setEnd(txtDateEnd.getValue());
				filter.setStart(txtDateStart.getValue());

				AklaCommonService.Connect.getService().getMailEntities(lstMailServer.getSelectedObject().getId(), filter, new GwtCallbackWrapper<List<AkladematAdminEntity<MailEntity>>>(null, false, false) {
					@Override
					public void onSuccess(List<AkladematAdminEntity<MailEntity>> result) {
						dataprovider.setList(result);
					}
				}.getAsyncCallback());
			}
		});
		lstMailServer.setBtnText("Importer mails");

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
	}

	private void createDatagrid() {
		Column<AkladematAdminEntity<MailEntity>, String> colSubject = new Column<AkladematAdminEntity<MailEntity>, String>(new TextCell()) {
			@Override
			public String getValue(AkladematAdminEntity<MailEntity> object) {
				return object.getObject().getSubject();
			}
		};
		Column<AkladematAdminEntity<MailEntity>, String> colFrom = new Column<AkladematAdminEntity<MailEntity>, String>(new TextCell()) {
			@Override
			public String getValue(AkladematAdminEntity<MailEntity> object) {
				return object.getObject().getFrom();
			}
		};
		Column<AkladematAdminEntity<MailEntity>, String> colDate = new Column<AkladematAdminEntity<MailEntity>, String>(new TextCell()) {
			@Override
			public String getValue(AkladematAdminEntity<MailEntity> object) {
				return dateFormat.format(object.getObject().getMailDate());
			}
		};
		Column<AkladematAdminEntity<MailEntity>, String> colAtt = new Column<AkladematAdminEntity<MailEntity>, String>(new TextCell()) {
			@Override
			public String getValue(AkladematAdminEntity<MailEntity> object) {
				if (object.getObject().getAttachments() != null && !object.getObject().getAttachments().isEmpty()) {
					StringBuilder buf = new StringBuilder();
					boolean first = true;
					for (String att : object.getObject().getAttachments()) {
						if (first) {
							first = false;
						} else {
							buf.append("&lt;br&gt;");
						}
						buf.append(att);
					}
					return buf.toString();
				}
				return "Aucune pièce jointe";
			}

			@Override
			public void render(Context context, AkladematAdminEntity<MailEntity> object, SafeHtmlBuilder sb) {
				if (object.getObject().getAttachments() != null && !object.getObject().getAttachments().isEmpty()) {
					StringBuilder buf = new StringBuilder();
					boolean first = true;
					for (String att : object.getObject().getAttachments()) {
						if (first) {
							first = false;
						} else {
							buf.append("<br>");
						}
						buf.append(att);
					}
					sb.appendHtmlConstant(buf.toString());
				} else {
					sb.appendHtmlConstant("Aucune pièce jointe");
				}
			}
		};
		Column<AkladematAdminEntity<MailEntity>, String> colStatus = new Column<AkladematAdminEntity<MailEntity>, String>(new TextCell()) {
			@Override
			public String getValue(AkladematAdminEntity<MailEntity> object) {
				return object.getStatus() == Status.NEW.getType() ? "Nouveau" : "Traité";
			}
		};

		DataGrid<AkladematAdminEntity<MailEntity>> datagrid = new DataGrid<AkladematAdminEntity<MailEntity>>(50);
		datagrid.setSize("100%", "100%");

		datagrid.addColumn(colSubject, "Objet");
		datagrid.addColumn(colFrom, "Expéditeur");
		datagrid.addColumn(colDate, "Date");
		datagrid.addColumn(colAtt, "Pièces jointes");
		datagrid.addColumn(colStatus, "Status");
		
		datagrid.setColumnWidth(colDate, "90px");
		datagrid.setColumnWidth(colStatus, "80px");

		datagrid.setSelectionModel(selectionModel);

		dataprovider.addDataDisplay(datagrid);

		gridPanel.setWidget(datagrid);

		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.setDisplay(datagrid);

		pagerPanel.setWidget(pager);
	}

	@Override
	public int getThemeColor() {
		return 5;
	}

	public List<AkladematAdminEntity<MailEntity>> getSelectedMails() {
		return new ArrayList<>(selectionModel.getSelectedSet());
	}
}
