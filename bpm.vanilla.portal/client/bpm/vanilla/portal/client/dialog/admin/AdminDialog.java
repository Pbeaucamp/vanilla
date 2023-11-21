package bpm.vanilla.portal.client.dialog.admin;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.CustomDatagrid;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.text.RichTextToolbar;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.portal.client.biPortal;
import bpm.vanilla.portal.client.services.BiPortalService;
import bpm.vanilla.portal.client.services.SecurityService;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;

public class AdminDialog extends AbstractDialogBox {
	
	private static final String LOGO_URL = "VanillaPortail/LogoPersoServlet";

	private static AdminDialogUiBinder uiBinder = GWT
			.create(AdminDialogUiBinder.class);

	interface AdminDialogUiBinder extends
			UiBinder<Widget, AdminDialog> {
	}
	
	interface MyStyle extends CssResource {
		String gridGroup();
	}
	
	@UiField
	HTMLPanel contentPanel;
	
	@UiField
	CaptionPanel captionLogo, captionText, captionShowAllRepository;
	
	@UiField
	HTMLPanel panelPersonalizable;
	
	@UiField
	SimplePanel panelGroups;
	
	@UiField
	ListBox listGroups;
	
	@UiField
	FormPanel formPanel;
	
	@UiField
	FileUpload fileUpload;
	
	@UiField
	CheckBox checkAllRepository;
	
	@UiField
	MyStyle style;
	
	private RichTextArea txtPersonalizable;
	
	private MultiSelectionModel<Group> selectionModel = new MultiSelectionModel<Group>();	
	private List<Group> availableGroups;

	public AdminDialog(User user, List<Group> availableGroups, boolean showAllRepository) {
		super(ToolsGWT.lblCnst.AdministrationDialogTitle(), false, true);
		this.availableGroups = availableGroups;
		
		setWidget(uiBinder.createAndBindUi(this));
		increaseZIndex();
		
		createButtonBar(ToolsGWT.lblCnst.Ok(), okHandler, ToolsGWT.lblCnst.Cancel(), cancelHandler);
		
		captionText.setCaptionText(ToolsGWT.lblCnst.WelcomeMessage());
		txtPersonalizable = new RichTextArea();
		txtPersonalizable.setWidth("100%");
		
		RichTextToolbar toolbarPerso = new RichTextToolbar(txtPersonalizable);
		toolbarPerso.setWidth("100%");

		panelPersonalizable.add(toolbarPerso);
		panelPersonalizable.add(txtPersonalizable);
		
		listGroups.addChangeHandler(changeHandler);
		
		captionShowAllRepository.setCaptionText(ToolsGWT.lblCnst.ShowAllRepositoryForAdmin());
		checkAllRepository.setText(ToolsGWT.lblCnst.ShowAllRepository());
		checkAllRepository.setValue(showAllRepository);
		
		
		captionLogo.setCaptionText(ToolsGWT.lblCnst.Logo());
		formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanel.setMethod(FormPanel.METHOD_POST);
		formPanel.addSubmitHandler(submitHandler);
		formPanel.addSubmitCompleteHandler(submitCompleteHandler);
		
		panelGroups.setWidget(new CustomDatagrid<Group>(availableGroups, selectionModel, 100, LabelsConstants.lblCnst.NoGroup()));
		
		for(Group gr : availableGroups){
			listGroups.addItem(gr.getName(), String.valueOf(gr.getId()));
		}
		
		if(!availableGroups.isEmpty()){
			txtPersonalizable.setHTML(availableGroups.get(0).getCustom1());
		}
	}
	
	private SubmitHandler submitHandler = new SubmitHandler() {
		
		public void onSubmit(SubmitEvent event) {
			biPortal.get().showWaitPart(true);
		}
	};
	
	private SubmitCompleteHandler submitCompleteHandler = new SubmitCompleteHandler() {
		
		public void onSubmitComplete(SubmitCompleteEvent event) {
			biPortal.get().showWaitPart(false);
			
			AdminDialog.this.hide();
		}
	};

	private ClickHandler okHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			biPortal.get().showWaitPart(true);
			
			boolean showAllRepository = checkAllRepository.getValue();
			
			BiPortalService.Connect.getInstance().setShowAllRepository(showAllRepository, new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					biPortal.get().showWaitPart(false);
					
					caught.printStackTrace();

					ExceptionManager.getInstance().handleException(caught, "");
				}

				@Override
				public void onSuccess(Void result) {

					
					List<Integer> grIds = new ArrayList<Integer>();
					for(Group group : availableGroups){
						if(selectionModel.isSelected(group)){
							grIds.add(group.getId());
						}
					}
					
					if(!grIds.isEmpty()){
						
						final String groupText = txtPersonalizable.getHTML();
						final String url = buildUrl(grIds);

						SecurityService.Connect.getInstance().udpateTextGroup(grIds, groupText, new AsyncCallback<Void>() {
							
							@Override
							public void onSuccess(Void result) {
								formPanel.setAction(url);
								formPanel.submit();

								biPortal.get().showWaitPart(false);
								
								for(Group group : availableGroups){
									if(selectionModel.isSelected(group)){
										group.setCustom1(groupText);
									}
								}
							}
							
							@Override
							public void onFailure(Throwable caught) {
								biPortal.get().showWaitPart(false);
								
								caught.printStackTrace();

								ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.FailedUpdateGroup());
							}
						});
					}
					else {
						biPortal.get().showWaitPart(false);
						
						MessageHelper.openMessageDialog(ToolsGWT.lblCnst.Information(), ToolsGWT.lblCnst.ChooseAGroup());
					}
				}
			});
		}

		private String buildUrl(List<Integer> grIds) {
			String url = GWT.getHostPageBaseURL() + LOGO_URL + "?groups=";
			boolean isFirst = true;
			for(Integer id : grIds){
				if(isFirst){
					url = url + id;
					isFirst = false;
				}
				else {
					url = url + ";" + id;
				}
			}
			
			return url;
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
	        AdminDialog.this.hide();
		}
	};
	
	private ChangeHandler changeHandler = new ChangeHandler() {
		
		@Override
		public void onChange(ChangeEvent event) {
			ListBox source = (ListBox)event.getSource();
			int grId = Integer.valueOf(source.getValue(source.getSelectedIndex()));
			for(Group gr : availableGroups){
				if(gr.getId() == grId){
					txtPersonalizable.setHTML(gr.getCustom1());
					return;
				}
			}
		}
	};
}
