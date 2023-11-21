package bpm.architect.web.client.dialogs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.services.ArchitectService;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.commons.client.viewer.dialog.ViewerDialog;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.mdm.model.supplier.Contract;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.resources.ContractIntegrationInformations;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class IntegrationInformationsComposite extends Composite {

	private static IntegrationInformationsCompositeUiBinder uiBinder = GWT.create(IntegrationInformationsCompositeUiBinder.class);

	interface IntegrationInformationsCompositeUiBinder extends UiBinder<Widget, IntegrationInformationsComposite> {
	}
	
	@UiField
	Button btnGenerateProcess;
	
	@UiField
	HTMLPanel panelBtnGenerate, panelInfos;
	
	@UiField
	LabelTextBox txtSource, txtHub, txtEtl, txtDataset;
	
	@UiField
	bpm.gwt.commons.client.custom.v2.Button btnOpenSource, btnPlayHub, btnOpenHub, btnLaunchEtl, btnLaunchDataset, btnDeleteIntegration;
	
	private IWait waitPanel;
	private InfoUser infoUser;
	
	private Contract contract;
	private ContractIntegrationInformations integrationInfos;

	public IntegrationInformationsComposite(IWait waitPanel, InfoUser infoUser, Contract contract) {
		initWidget(uiBinder.createAndBindUi(this));
		this.waitPanel = waitPanel;
		this.infoUser = infoUser;
		this.contract = contract;
		
		loadItem();
		loadProperties();
	}

	private void loadProperties() {
		// TODO Auto-generated method stub
		
	}

	public void loadItem() {
		ArchitectService.Connect.getInstance().getIntegrationInfos(contract.getId(), new GwtCallbackWrapper<ContractIntegrationInformations>(waitPanel, true, true) {

			@Override
			public void onSuccess(ContractIntegrationInformations result) {
				refreshUi(result);
			}
		}.getAsyncCallback());
	}

	private void refreshUi(ContractIntegrationInformations integrationInfos) {
		this.integrationInfos = integrationInfos;
		if (integrationInfos != null) {
			panelBtnGenerate.setVisible(false);
			panelInfos.setVisible(true);
			
			txtSource.setText(integrationInfos.getType().toString() + " - " + integrationInfos.getItem());
			txtHub.setText(integrationInfos.getHubName());
			txtEtl.setText(integrationInfos.getEtlName() != null && !integrationInfos.getEtlName().isEmpty() ? integrationInfos.getEtlName() : Labels.lblCnst.NoLinkedETL());
			btnLaunchEtl.setVisible(integrationInfos.getEtlName() != null && !integrationInfos.getEtlName().isEmpty());
			txtDataset.setText(integrationInfos.getTargetDatasetName());
		}
		else {
			panelBtnGenerate.setVisible(true);
			panelInfos.setVisible(false);
		}
	}
	
	@UiHandler("btnGenerateProcess")
	public void onGenerateProcess(ClickEvent event) {
		CreateIntegrationDialog dial = new CreateIntegrationDialog(this, contract);
		dial.center();
	}
	
	@UiHandler("btnOpenSource")
	public void onOpenSource(ClickEvent event) {
		ArchitectService.Connect.getInstance().getSourceUrl(integrationInfos, new GwtCallbackWrapper<String>(null, false) {

			@Override
			public void onSuccess(String url) {
				ToolsGWT.doRedirect(url);
			}
		}.getAsyncCallback());
	}
	
	@UiHandler("btnPlayHub")
	public void onPlayHub(ClickEvent event) {
		
	}
	
	@UiHandler("btnOpenHub")
	public void onOpenHub(ClickEvent event) {
		String url = infoUser.getUrl(IRepositoryApi.HUB);
		openWebApp(url);
	}
	
	private void openWebApp(String webAppUrl) {
		String locale = LocaleInfo.getCurrentLocale().getLocaleName();
		CommonService.Connect.getInstance().forwardSecurityUrl(webAppUrl, locale, new GwtCallbackWrapper<String>(null, false) {

			@Override
			public void onSuccess(String url) {
				ToolsGWT.doRedirect(url);
			}
		}.getAsyncCallback());
	}
	
	@UiHandler("btnLaunchEtl")
	public void onLaunchEtl(ClickEvent event) {
		CommonService.Connect.getInstance().getItemById(integrationInfos.getEtlId(), new GwtCallbackWrapper<RepositoryItem>(waitPanel, true, true) {

			@Override
			public void onSuccess(RepositoryItem result) {
				PortailRepositoryItem itemReport = new PortailRepositoryItem(result, IRepositoryApi.BIG);

				ViewerDialog dial = new ViewerDialog(itemReport, infoUser.getGroup());
				dial.center();
			}
		}.getAsyncCallback());
	}
	
	@UiHandler("btnLaunchDataset")
	public void onLaunchDataset(ClickEvent event) {
		ArchitectService.Connect.getInstance().getDatasetUrl(integrationInfos, new GwtCallbackWrapper<String>(null, false) {

			@Override
			public void onSuccess(String url) {
				ToolsGWT.doRedirect(url);
			}
		}.getAsyncCallback());
	}
	
	@UiHandler("btnDeleteIntegration")
	public void onDeleteIntegration(ClickEvent event) {
		InformationsDialog dial = MessageHelper.openErrorDialog(Labels.lblCnst.DeleteIntegrationProcess(), Labels.lblCnst.ConfirmDeleteIntegrationProcess(), LabelsCommon.lblCnst.Confirmation(), LabelsCommon.lblCnst.Cancel(), true, null);
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					ArchitectService.Connect.getInstance().deleteIntegration(integrationInfos, new GwtCallbackWrapper<Void>(null, false) {

						@Override
						public void onSuccess(Void result) {
							loadItem();
						}
					}.getAsyncCallback());
				}
			}
		});
	}
}
