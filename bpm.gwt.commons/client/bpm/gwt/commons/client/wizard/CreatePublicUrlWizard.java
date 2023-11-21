package bpm.gwt.commons.client.wizard;

import java.util.Date;
import java.util.HashMap;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.PublicUrlDialog;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.vanilla.platform.core.beans.PublicUrl;
import bpm.vanilla.platform.core.beans.PublicUrl.TypeURL;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class CreatePublicUrlWizard extends GwtWizard implements IWait {

	private IGwtPage currentPage;

	private PublicUrlDialog parent;

	private PublicUrlDefinitionPage definitionPage;
	private PublicUrlParamPage paramPage;

	private InfoUser infoUser;
	
	private TypeURL typeUrl;
	private int itemId;

	private RepositoryItem item;
	private LaunchReportInformations itemInfos;

	public CreatePublicUrlWizard(PublicUrlDialog parent, InfoUser infoUser, RepositoryItem item, LaunchReportInformations itemInfos) {
		super(LabelsConstants.lblCnst.PublicUrl());
		this.parent = parent;
		this.infoUser = infoUser;
		this.typeUrl = TypeURL.REPOSITORY_ITEM;
		this.itemId = item.getId();
		this.item = item;
		this.itemInfos = itemInfos;

		buildContent();
	}

	public CreatePublicUrlWizard(PublicUrlDialog parent, InfoUser infoUser, int itemId) {
		super(LabelsConstants.lblCnst.PublicUrl());
		this.parent = parent;
		this.infoUser = infoUser;
		this.typeUrl = TypeURL.DOCUMENT_VERSION;
		this.itemId = itemId;

		buildContent();
	}

	private void buildContent() {
		definitionPage = new PublicUrlDefinitionPage(this, 0, infoUser.getAvailableGroups(), itemInfos);
		setCurrentPage(definitionPage);
	}

	@Override
	public boolean canFinish() {
		boolean paramComplete = true;
		if (paramPage != null) {
			paramComplete = paramPage.isComplete();
		}
		
		return definitionPage.isComplete() && paramComplete;
	}

	@Override
	public void setCurrentPage(IGwtPage page) {
		if (page instanceof PublicUrlDefinitionPage)
			setContentPanel((PublicUrlDefinitionPage) page);
		else if (page instanceof PublicUrlParamPage)
			setContentPanel((PublicUrlParamPage) page);
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
		int userId = infoUser.getUser().getId();
		int repositoryId = infoUser.getRepository().getId();
		
		Date endDate = definitionPage.getEndDate();
		int groupId = definitionPage.getSelectedGroup();
		
		String format = "";
		if (typeUrl == TypeURL.REPOSITORY_ITEM) {
			format = definitionPage.getSelectedFormat();
		}
		
		HashMap<String, String> parameters = paramPage != null ? paramPage.getParameters() : null;
		
		PublicUrl url = new PublicUrl();
		url.setItemId(itemId);
		url.setRepositoryId(repositoryId);
		url.setGroupId(groupId);
		url.setEndDate(endDate);
		url.setCreationDate(new Date());
		url.setOutputFormat(format);
		url.setUserId(userId);
		url.setDatasourceId(0);
		url.setTypeUrl(typeUrl);
		
		CommonService.Connect.getInstance().addPublicUrl(url, parameters, new GwtCallbackWrapper<PublicUrl>(this, true, true) {

			@Override
			public void onSuccess(PublicUrl result) {
				parent.refreshItems();
				
				hide();
			}
		}.getAsyncCallback());
	}

	@Override
	protected void onBackClick() {
		if (currentPage instanceof PublicUrlParamPage) {
			setCurrentPage(definitionPage);
		}
	}

	@Override
	protected void onNextClick() {
		if (currentPage instanceof PublicUrlDefinitionPage) {
			if (itemInfos != null && itemInfos.getGroupParameters() != null && !itemInfos.getGroupParameters().isEmpty()) {
				if (paramPage == null) {
					paramPage = new PublicUrlParamPage(CreatePublicUrlWizard.this, 2);
				}
				paramPage.setItem(itemInfos);
				setCurrentPage(paramPage);
			}
		}
	}

	public boolean isReport() {
		if (item != null && item.isReport()) {
			return true;
		}
		else {
			return false;
		}
	}

	public boolean hasParameters() {
		return itemInfos != null && itemInfos.getGroupParameters() != null && !itemInfos.getGroupParameters().isEmpty();
	}

}
