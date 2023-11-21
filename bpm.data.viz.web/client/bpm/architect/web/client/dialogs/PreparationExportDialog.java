package bpm.architect.web.client.dialogs;

import java.util.List;

import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.services.ArchitectService;
import bpm.data.viz.core.preparation.DataPreparation;
import bpm.data.viz.core.preparation.ExportPreparationInfo;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.dialog.CkanPackageDialog;
import bpm.gwt.commons.client.dialog.RepositoryDirectoryDialog;
import bpm.gwt.commons.client.listeners.CkanManager;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.CkanResource;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class PreparationExportDialog extends AbstractDialogBox implements CkanManager {

	public enum TypeExport {
		FILE("Fichier"),
		ARCHITECT("Vanilla Architect"),
		CKAN("Data4Citizen"),
		BASE(Labels.lblCnst.Database()), 
		ETL("ETL");

		private String label;

		private TypeExport(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}

		@Override
		public String toString() {
			return label;
		}
	}

	private static PreparationExportDialogUiBinder uiBinder = GWT.create(PreparationExportDialogUiBinder.class);

	interface PreparationExportDialogUiBinder extends UiBinder<Widget, PreparationExportDialog> {}

	@UiField
	ListBoxWithButton<TypeExport> lstType;

	@UiField
	SimplePanel panelType;
	
	
	private LabelTextBox baseName;
	private DataPreparation dataprep;

	private String exportFileKey;
	private String format = "csv";
	private InfoUser infoUser;
	
	public PreparationExportDialog(DataPreparation dp, InfoUser infoUser) {
		super("Exporter", false, true);

		this.dataprep = dp;
		this.infoUser = infoUser;
		
		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		lstType.addItem(TypeExport.FILE);
		lstType.addItem(TypeExport.ARCHITECT);
		if (infoUser.canAccess(VanillaConfiguration.P_CKAN_URL)) {
			lstType.addItem(TypeExport.CKAN);
		}
		lstType.addItem(TypeExport.BASE);
		lstType.addItem(TypeExport.ETL);

		panelType.add(new FileExportPanel());
	}

	@UiHandler("lstType")
	public void onChangeType(ChangeEvent event) {
		panelType.setVisible(true);

		panelType.clear();
		switch(lstType.getSelectedObject()) {
		case FILE:
			panelType.add(new FileExportPanel());
			break;
		case ARCHITECT:
			panelType.add(new ArchitectExportPanel(this));
			break;
			//		case "Dataset":
			//			panelType.add(new DatasetExportPanel());
			//			break;
		case CKAN:
			panelType.setVisible(false);
			break;
		case BASE:
			baseName = new LabelTextBox();
			baseName.setPlaceHolder("Name");
			panelType.add(baseName);
			break;
		case ETL:
			panelType.setVisible(false);
			break;
		}
	}

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			ExportPreparationInfo info = new ExportPreparationInfo();
			info.setDataPreparation(dataprep);
			info.setName(dataprep.getName());
			//info.setExportType(lstType.getSelectedObject().name());
			info.setExportType(format);
			if(panelType.getWidget() instanceof FileExportPanel) {
				info.setSeparator(((FileExportPanel) panelType.getWidget()).getSeparator());
			}
			exportPreparation(info);
		}
	};

	private void exportPreparation(ExportPreparationInfo info) {
		showWaitPart(true);
		final String exportType = info.getExportType();
		ArchitectService.Connect.getInstance().exportPreparation(info, new GwtCallbackWrapper<String>(PreparationExportDialog.this, true) {
			@Override
			public void onSuccess(String result) {
				manageExport(result, exportType);
				showWaitPart(false);
			}
		}.getAsyncCallback());
	}

	private void manageExport(String exportFileKey, String exportType) {
		this.exportFileKey = exportFileKey;

		switch(lstType.getSelectedObject()) {
		case FILE:
			hide();

			String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_STREAM_SERVLET + "?" + CommonConstants.STREAM_HASHMAP_NAME + "=" + exportFileKey + "&" + CommonConstants.STREAM_HASHMAP_FORMAT + "=csv";
			ToolsGWT.doRedirect(fullUrl);
			break;
		case ARCHITECT:
			if(format.equals("xlsx")) {
				ArchitectService.Connect.getInstance().confirmUpload(((ArchitectExportPanel)panelType.getWidget()).getSelectedContract(), dataprep.getName(), dataprep.getName() + ".xlsx", new GwtCallbackWrapper<Void>(null, false) {
					@Override
					public void onSuccess(Void result) {
						hide();
					}
				}.getAsyncCallback());
			}
			else {
				ArchitectService.Connect.getInstance().confirmUpload(((ArchitectExportPanel)panelType.getWidget()).getSelectedContract(), dataprep.getName() , dataprep.getName() + ".csv", new GwtCallbackWrapper<Void>(null, false) {
					@Override
					public void onSuccess(Void result) {
						hide();
					}
				}.getAsyncCallback());
			}
			break;
		case CKAN:
			CkanPackageDialog  dial = new CkanPackageDialog(this, null, null, true, null);
			dial.center();
			
//			CkanPackageDialog dial = new CkanPackageDialog(this);
//			dial.center();
			break;
		case BASE:
			ArchitectService.Connect.getInstance().createDatabase(baseName.getText(), dataprep, true, new GwtCallbackWrapper<Void>(null, false) {
				@Override
				public void onSuccess(Void result) {
					hide();
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.TableCreatedSuccessfully());
				}
			}.getAsyncCallback());
			break;
		case ETL:
			ArchitectService.Connect.getInstance().publicationETL(dataprep, new GwtCallbackWrapper<String>(PreparationExportDialog.this, true) {
				@Override
				public void onSuccess(String result) {
					List<Group> groups = infoUser.getAvailableGroups();
					RepositoryDirectoryDialog dial = new RepositoryDirectoryDialog(IRepositoryApi.GTW_TYPE, groups, result);
			        dial.center();
			        hide();
				}
			}.getAsyncCallback());
			break;
		}
	}

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

	@Override
	public void managePackage(CkanPackage pack, CkanResource resource) {
		showWaitPart(true);
		
		pack.setSelectedResource(resource);

		ArchitectService.Connect.getInstance().exportToCkan(resource.getName(), pack, exportFileKey, new GwtCallbackWrapper<Void>(this, true) {

			@Override
			public void onSuccess(Void result) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.DatasetUploadedToCkan());

				hide();
			}
		}.getAsyncCallback());
	}
	
	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
}
