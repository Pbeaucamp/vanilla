package bpm.gwt.commons.client.dialog;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.utils.ButtonImageCell;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.gwt.commons.client.wizard.CreatePublicUrlWizard;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.PublicUrl;
import bpm.vanilla.platform.core.beans.PublicUrl.TypeURL;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class PublicUrlDialog extends AbstractDialogBox {
	private static final String DATE_FORMAT = "HH:mm - dd/MM/yyyy";

	private static PublicUrlDialogUiBinder uiBinder = GWT.create(PublicUrlDialogUiBinder.class);

	interface PublicUrlDialogUiBinder extends UiBinder<Widget, PublicUrlDialog> {}

	interface MyStyle extends CssResource {
		String imgCell();

		String imgGrid();
	}

	@UiField
	MyStyle style;

	@UiField
	Image btnAdd;

	@UiField
	SimplePanel panelContent;

	private ListDataProvider<PublicUrl> dataProvider;

	private InfoUser infoUser;

	private int itemId;
	private TypeURL typeUrl;

	private RepositoryItem item;

	private String publicKeyPrefixUrl;

	public PublicUrlDialog(InfoUser infoUser, int itemId, RepositoryItem item, TypeURL typeUrl) {
		super(LabelsConstants.lblCnst.PublicUrl(), false, true);
		this.infoUser = infoUser;
		this.itemId = itemId;
		this.item = item;
		this.typeUrl = typeUrl;

		this.publicKeyPrefixUrl = infoUser.getVanillaRuntimeExternalUrl();
		if (publicKeyPrefixUrl.endsWith("/")) {
			publicKeyPrefixUrl = publicKeyPrefixUrl.substring(0, publicKeyPrefixUrl.lastIndexOf("/"));
		}
		publicKeyPrefixUrl += VanillaConstants.VANILLA_EXTERNAL_CALL + "?publickey=";

		setWidget(uiBinder.createAndBindUi(this));

		createButton(LabelsConstants.lblCnst.Close(), closeHandler);

		DataGrid<PublicUrl> grid = buildGrid();
		panelContent.setWidget(grid);

		refreshItems();
	}

	public void refreshItems() {
		CommonService.Connect.getInstance().getPublicUrls(itemId, typeUrl, new GwtCallbackWrapper<List<PublicUrl>>(this, true, true) {

			@Override
			public void onSuccess(List<PublicUrl> result) {
				loadItems(result);
			}
		}.getAsyncCallback());
	}

	private void loadItems(List<PublicUrl> elements) {
		if(elements == null) {
			elements = new ArrayList<PublicUrl>();
		}

		dataProvider.setList(elements);
	}

	private DataGrid<PublicUrl> buildGrid() {
		final DateTimeFormat dateFormatter = DateTimeFormat.getFormat(DATE_FORMAT);

		TextCell txtCell = new TextCell();
		Column<PublicUrl, String> urlColumn = new Column<PublicUrl, String>(txtCell) {

			@Override
			public String getValue(PublicUrl object) {
				return publicKeyPrefixUrl + object.getPublicKey();
			}
		};

		Column<PublicUrl, String> groupeColumn = new Column<PublicUrl, String>(txtCell) {

			@Override
			public String getValue(PublicUrl object) {
				List<Group> groups = infoUser.getAvailableGroups();
				if(groups != null) {
					for(Group group : groups) {
						if(group.getId() == object.getGroupId()) {
							return group.getName();
						}
					}
				}
				return LabelsConstants.lblCnst.Unknown();
			}
		};

		Column<PublicUrl, String> formatColumn = new Column<PublicUrl, String>(txtCell) {

			@Override
			public String getValue(PublicUrl object) {
				return object.getOutputFormat();
			}
		};

		Column<PublicUrl, String> creationDateColumn = new Column<PublicUrl, String>(txtCell) {

			@Override
			public String getValue(PublicUrl object) {
				if(object.getCreationDate() != null)
					return dateFormatter.format(object.getCreationDate());
				else
					return LabelsConstants.lblCnst.Unknown();
			}
		};

		Column<PublicUrl, String> expirationDateColumn = new Column<PublicUrl, String>(txtCell) {

			@Override
			public String getValue(PublicUrl object) {
				if(object.getEndDate() != null)
					return dateFormatter.format(object.getEndDate());
				else
					return LabelsConstants.lblCnst.NotDefined();
			}
		};

		ButtonImageCell deleteCell = new ButtonImageCell(CommonImages.INSTANCE.ic_delete_black_18dp(), LabelsConstants.lblCnst.DeletePublicUrl(), style.imgGrid());
		Column<PublicUrl, String> colDelete = new Column<PublicUrl, String>(deleteCell) {

			@Override
			public String getValue(PublicUrl object) {
				return "";
			}
		};
		colDelete.setFieldUpdater(new FieldUpdater<PublicUrl, String>() {

			@Override
			public void update(int index, final PublicUrl object, String value) {
				final InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.Confirmation(), LabelsConstants.lblCnst.Cancel(), LabelsConstants.lblCnst.ConfirmDeletePublicUrl(), true);
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {

					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if(dial.isConfirm()) {
							deletePublicUrl(object);
						}
					}
				});
				dial.center();
			}
		});

		ButtonImageCell copyCell = new ButtonImageCell(CommonImages.INSTANCE.copy_black(), LabelsConstants.lblCnst.CopyPublicUrl(), style.imgGrid());
		Column<PublicUrl, String> copyCol = new Column<PublicUrl, String>(copyCell) {
			@Override
			public String getValue(PublicUrl object) {
				return "";
			}
		};
		copyCol.setFieldUpdater(new FieldUpdater<PublicUrl, String>() {
			@Override
			public void update(int index, final PublicUrl object, String value) {
				copyToClipboard(publicKeyPrefixUrl + object.getPublicKey());
			}
		});

		dataProvider = new ListDataProvider<PublicUrl>(new ArrayList<PublicUrl>());

		DataGrid.Resources resources = new CustomResources();
		DataGrid<PublicUrl> dataGrid = new DataGrid<PublicUrl>(10000, resources);
		dataGrid.setSize("100%", "100%");
		dataGrid.addColumn(urlColumn, LabelsConstants.lblCnst.PublicUrl());
		dataGrid.addColumn(groupeColumn, LabelsConstants.lblCnst.Group());
		dataGrid.addColumn(formatColumn, LabelsConstants.lblCnst.Format());
		dataGrid.addColumn(creationDateColumn, LabelsConstants.lblCnst.CreationDate());
		dataGrid.addColumn(expirationDateColumn, LabelsConstants.lblCnst.ExpirationDate());
		dataGrid.addColumn(copyCol);
		dataGrid.addColumn(colDelete);
		dataGrid.setColumnWidth(colDelete, "70px");
		dataGrid.setEmptyTableWidget(new Label(LabelsConstants.lblCnst.NoPublicUrl()));

		dataProvider.addDataDisplay(dataGrid);

		return dataGrid;
	}

	private static native boolean copyToClipboard(String url) /*-{
		var textArea = document.createElement("textarea");
		textArea.value = url;
		textArea.style.position = "fixed"; //avoid scrolling to bottom
		document.body.appendChild(textArea);
		textArea.focus();
		textArea.select();
		try {
			var res =  document.execCommand('copy');
		} catch(e) {console.error(e);}
//		alert("8");
		document.body.removeChild(textArea);
		return false;
	}-*/;

	@UiHandler("btnAdd")
	public void onAddClick(ClickEvent event) {
		if(typeUrl == TypeURL.REPOSITORY_ITEM) {
			Group selectedGroup = infoUser.getGroup();
			PortailRepositoryItem portailItem = new PortailRepositoryItem(item, "");

			final LaunchReportInformations itemInfos = new LaunchReportInformations();
			itemInfos.setItem(portailItem);
			itemInfos.setSelectedGroup(selectedGroup);

			ReportingService.Connect.getInstance().getParameters(itemInfos, new GwtCallbackWrapper<List<VanillaGroupParameter>>(this, true, true) {

				@Override
				public void onSuccess(List<VanillaGroupParameter> groupParams) {
					itemInfos.setGroupParameters(groupParams);

					CreatePublicUrlWizard wiz = new CreatePublicUrlWizard(PublicUrlDialog.this, infoUser, item, itemInfos);
					wiz.center();
				}
			}.getAsyncCallback());
		}
		else if(typeUrl == TypeURL.DOCUMENT_VERSION) {
			CreatePublicUrlWizard wiz = new CreatePublicUrlWizard(this, infoUser, itemId);
			wiz.center();
		}
	}

	private void deletePublicUrl(PublicUrl publicUrl) {
		CommonService.Connect.getInstance().deletePublicUrl(publicUrl, new GwtCallbackWrapper<Void>(this, true, true) {

			@Override
			public void onSuccess(Void result) {
				refreshItems();
			}
		}.getAsyncCallback());
	}

	private ClickHandler closeHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			PublicUrlDialog.this.hide();
		}
	};
}
