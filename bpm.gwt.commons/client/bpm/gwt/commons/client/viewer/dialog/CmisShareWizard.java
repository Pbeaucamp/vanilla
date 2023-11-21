package bpm.gwt.commons.client.viewer.dialog;

import java.util.HashMap;
import java.util.List;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.popup.IShare;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.wizard.GwtWizard;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.shared.InfoShare;
import bpm.gwt.commons.shared.InfoShareCmis;
import bpm.gwt.commons.shared.cmis.CmisFolder;
import bpm.gwt.commons.shared.cmis.CmisInformations;
import bpm.gwt.commons.shared.repository.DocumentVersionDTO;
import bpm.gwt.commons.shared.utils.TypeExport;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;

public class CmisShareWizard extends GwtWizard implements IWait {
	
	private IGwtPage currentPage;

	private CmisShareConnectionPage connectionPage;
	private CmisShareSelectionPage selectionPage;

	private IShare share;
	private TypeExport typeExport;
	
	private LaunchReportInformations itemInfo;
	
	private String key;
	private String dashboardUrl;
	private List<String> folders;
	
	private DocumentVersionDTO docVersion;
	
	public CmisShareWizard(IShare share, String reportKey, LaunchReportInformations itemInfo) {
		super(LabelsConstants.lblCnst.ShareCmis());
		this.share = share;
		this.key = reportKey;
		this.itemInfo = itemInfo;
		this.typeExport = TypeExport.REPORT;
		
		buildContent();
	}

	public CmisShareWizard(IShare share, LaunchReportInformations itemInfo, String uuid, String dashboardUrl, List<String> folders) {
		super(LabelsConstants.lblCnst.ShareCmis());
		this.share = share;
		this.key = uuid;
		this.itemInfo = itemInfo;
		this.dashboardUrl = dashboardUrl;
		this.folders = folders;
		this.typeExport = TypeExport.DASHBOARD;
		
		buildContent();
	}
	
	public CmisShareWizard(IShare share, DocumentVersionDTO docVersion) {
		super(LabelsConstants.lblCnst.ShareCmis());
		this.share = share;
		this.docVersion = docVersion;
		this.typeExport = TypeExport.DOC_VERSION;
		
		buildContent();
	}

	private void buildContent() {
		connectionPage = new CmisShareConnectionPage(this, 0);
		setCurrentPage(connectionPage);
	}

	@Override
	public boolean canFinish() {
		return connectionPage.isComplete() && selectionPage != null && selectionPage.isComplete();
	}

	@Override
	public void setCurrentPage(IGwtPage page) {
		if (page instanceof CmisShareConnectionPage)
			setContentPanel((CmisShareConnectionPage) page);
		else if (page instanceof CmisShareSelectionPage)
			setContentPanel((CmisShareSelectionPage) page);
		currentPage = page;
		updateBtn();
	}

	@Override
	public void updateBtn() {
		setBtnBackState(currentPage.canGoBack() ? true : false);
		setBtnNextState(currentPage.canGoFurther() ? true : false);
		setBtnFinishState(canFinish() ? true : false);
	}

	@Override
	protected void onClickFinish() {

		CmisInformations cmisInfos = selectionPage.getCmisInfos();
		CmisFolder selectedFolder = selectionPage.getSelectedItem();
		String format = selectionPage.getFormat();

		if (format.isEmpty()) {
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.ChooseFormat());
			return;
		}
		
		CmisShareWizard.this.hide();
		
		if (typeExport == TypeExport.DASHBOARD || typeExport == TypeExport.REPORT) {
			HashMap<String, String> selectedFolders = new HashMap<String, String>();
			if (typeExport == TypeExport.DASHBOARD || typeExport == TypeExport.REPORT_GROUP) {
				selectedFolders = selectionPage.getSelectedFolders();
			}
			
			boolean isLandscape = selectionPage.isLandscape();

			InfoShareCmis infoShare = new InfoShareCmis(typeExport, cmisInfos, selectedFolder, key, itemInfo.getItem().getName(), format, isLandscape);
			infoShare.setDashboardUrl(dashboardUrl);
			infoShare.setSelectedFolders(selectedFolders);

			share.share(infoShare);
		}
		else if (typeExport == TypeExport.MARKDOWN) {
			InfoShare infoShare = new InfoShareCmis(typeExport, cmisInfos, selectedFolder, key, itemInfo.getItem().getName(), format, false);

			share.share(infoShare);
		}
	}

	@Override
	protected void onBackClick() {
		if (currentPage instanceof CmisShareSelectionPage) {
			setCurrentPage(connectionPage);
		}
	}

	@Override
	protected void onNextClick() {
		if (currentPage instanceof CmisShareConnectionPage) {
			
			CmisInformations cmisInfos = connectionPage.getCmisInfos();
			if (cmisInfos != null) {
				if (typeExport == TypeExport.DASHBOARD) {
					selectionPage = new CmisShareSelectionPage(this, 2, cmisInfos, folders);
				}
				else if (typeExport == TypeExport.REPORT) {
					selectionPage = new CmisShareSelectionPage(this, 2, cmisInfos, itemInfo);
				}
				setCurrentPage(selectionPage);
			}
		}
	}

}
