package bpm.gwt.commons.client.dialog.ged;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.CustomDatagrid;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.GedInformations;
import bpm.gwt.commons.shared.repository.DocumentDefinitionDTO;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.vanilla.platform.core.beans.Group;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.google.gwt.view.client.MultiSelectionModel;

public class IndexFormDialog extends AbstractDialogBox implements IWait {

	private static IndexFormDialogUiBinder uiBinder = GWT.create(IndexFormDialogUiBinder.class);

	interface IndexFormDialogUiBinder extends UiBinder<Widget, IndexFormDialog> {
	}
	
	interface MyStyle extends CssResource {
		String txtTitle();
		String txtArea();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel contentPanel;
	
	@UiField
	FormPanel formPanel;

	private List<Group> groups;
	private DocumentDefinitionDTO selectedDocument;

	private MultiSelectionModel<Group> selectionModel = new MultiSelectionModel<Group>();

	private FileUpload fileUpload;
	private TextBox txtTitle;
	private TextArea summary;
	private Label lblFileIndex;
	
	private CheckBox checkUsePeremptionDate;
	private DateBox dateBox;
	
	private int indexState = -1;
	private String errorMsg;

	public IndexFormDialog(DocumentDefinitionDTO document, List<Group> availableGroups) {
		super(LabelsConstants.lblCnst.Indexfile(), false, true);
		this.groups = availableGroups;
		this.selectedDocument = document;
		
		setWidget(uiBinder.createAndBindUi(this));
		
		createButtonBar(LabelsConstants.lblCnst.Confirmation(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		
		FlexTable table = createMainPart(groups);

		formPanel.setAction(GWT.getHostPageBaseURL() + "VanillaPortail/IndexFile");
		formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanel.setMethod(FormPanel.METHOD_POST);
		formPanel.addSubmitHandler(submitHandler);
		formPanel.addSubmitCompleteHandler(submitCompleteHandler);
		formPanel.setWidget(table);
	}
	
	public IndexFormDialog(DocumentDefinitionDTO document, List<Group> availableGroups, String name, String format, String title, String desc) {
		super(LabelsConstants.lblCnst.Indexfile(), false, true);
		this.groups = availableGroups;
		this.selectedDocument = document;
		
		setWidget(uiBinder.createAndBindUi(this));
		
		createButtonBar(LabelsConstants.lblCnst.Confirmation(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		
		FlexTable table = createMainPart(groups);

		formPanel.setAction(GWT.getHostPageBaseURL() + "VanillaPortail/IndexFile" + "?" 
				+ CommonConstants.IS_IN_SESSION + "=true&" + CommonConstants.STREAM_HASHMAP_NAME + "=" + name + "&" 
				+ CommonConstants.STREAM_HASHMAP_FORMAT + "=" + format);
		formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanel.setMethod(FormPanel.METHOD_POST);
		formPanel.addSubmitHandler(submitHandler);
		formPanel.addSubmitCompleteHandler(submitCompleteHandler);
		formPanel.setWidget(table);
		
		if (selectedDocument == null) {
			txtTitle.setText(title);
			summary.setText(desc);
		}
		
		fileUpload.setVisible(false);
		lblFileIndex.setVisible(false);
	}

	private FlexTable createMainPart(List<Group> groups) {
		txtTitle = new TextBox();
		txtTitle.setMaxLength(150);
		txtTitle.addStyleName(style.txtTitle());

		summary = new TextArea();
		summary.addStyleName(style.txtArea());

		fileUpload = new FileUpload();
		fileUpload.setName("file");
		fileUpload.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				fileUpload.getFilename();
			}
		});

		HorizontalPanel container = new HorizontalPanel();
		container.add(fileUpload);

		// Create a DateBox
		checkUsePeremptionDate = new CheckBox(LabelsConstants.lblCnst.SetPeremptionDate());
		checkUsePeremptionDate.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if (event.getValue()) {
					dateBox.setEnabled(true);
				}
				else {
					dateBox.setEnabled(false);
				}
			}
		});

		DateTimeFormat dateFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_LONG);
		DatePicker datePicker = new DatePicker();
		dateBox = new DateBox(datePicker, new Date(), new DateBox.DefaultFormat(dateFormat));
		dateBox.setEnabled(false);
		
		lblFileIndex = new Label(LabelsConstants.lblCnst.FileToIndex());

		FlexTable table = new FlexTable();
		if (selectedDocument != null) {
			table.setWidget(0, 0, lblFileIndex);
			table.setWidget(0, 1, container);
			table.setWidget(1, 0, checkUsePeremptionDate);
			table.setWidget(1, 1, dateBox);
		}
		else {
			table.setText(0, 0, LabelsConstants.lblCnst.Title());
			table.setWidget(0, 1, txtTitle);
			table.setText(1, 0, LabelsConstants.lblCnst.Summary());
			table.setWidget(1, 1, summary);
			table.setWidget(2, 0, lblFileIndex);
			table.setWidget(2, 1, container);
			table.setWidget(3, 0, checkUsePeremptionDate);
			table.setWidget(3, 1, dateBox);
			table.setWidget(4, 0, new CustomDatagrid<Group>(groups, selectionModel, 135, LabelsConstants.lblCnst.NoGroup()));
		}

		FlexCellFormatter formatter = table.getFlexCellFormatter();
		formatter.setColSpan(4, 0, 2);

		return table;
	}

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			indexState = -1;
			IndexFormDialog.this.hide();
		}
	};

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			String title = txtTitle.getText();
			String description = summary.getText();

			List<Integer> groupIds = new ArrayList<Integer>();
			for (Group group : groups) {
				if (selectionModel.isSelected(group)) {
					groupIds.add(group.getId());
				}
			}

			if (title.isEmpty() && selectedDocument == null) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.FillTitle());
				return;
			}
			else if (groupIds.isEmpty() && selectedDocument == null) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.ChooseAGroup());
				return;
			}

			Date peremptionDate = null;
			if (checkUsePeremptionDate.getValue()) {
				peremptionDate = dateBox.getValue();
			}

			GedInformations gedInformations = new GedInformations(title, groupIds, description, peremptionDate);
			if (selectedDocument != null) {
				gedInformations.setDocumentDefinitionId(selectedDocument.getId());
			}

			CommonService.Connect.getInstance().indexFile(gedInformations, new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();

					ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.ErrorDuringIndexation());
				}

				@Override
				public void onSuccess(Void result) {
					formPanel.submit();
				}
			});
		}
	};

//	@Override
//	public void showWaitPart(boolean visible) {
//		biPortal.get().showWaitPart(visible);
//	}

	private SubmitCompleteHandler submitCompleteHandler = new SubmitCompleteHandler() {

		public void onSubmitComplete(SubmitCompleteEvent event) {
			showWaitPart(false);
			if(event.getResults().contains("success")){
				indexState = 1;
			} else {
				indexState = 0;
				errorMsg = event.getResults();
			}
			IndexFormDialog.this.hide();
		}
	};

	private SubmitHandler submitHandler = new SubmitHandler() {

		public void onSubmit(SubmitEvent event) {
			showWaitPart(true);
		}
	};

	public int getIndexState() {
		return indexState;
	}

	public String getErrorMsg() {
		return errorMsg;
	}
}
