package bpm.gwt.commons.client.datasource;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceCsv;
import bpm.vanilla.platform.core.beans.data.IDatasourceObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class DatasourceCsvPage extends Composite implements IDatasourceObjectPage {

	private static DatasourceCsvPageUiBinder uiBinder = GWT.create(DatasourceCsvPageUiBinder.class);

	interface DatasourceCsvPageUiBinder extends UiBinder<Widget, DatasourceCsvPage> {
	}
	
	@UiField
	FormPanel formCode;
	
	@UiField
	TextBox  txtSeparator, txtHdfs;
	
	@UiField
	SimplePanel panelUpload;
	
	@UiField
	HTMLPanel panelDisk, panelHdfs;
	
	@UiField
	ListBox lstType;
	
	@UiField
	Button  btnImport, btnImportHdfs;
	
	@UiField
	CheckBox  chkHasHeader;
	
	@UiField
	Label lblFileName;

	private Datasource datasource;
	private FileUpload fileUpload = new FileUpload();
	private String pathFile;
	private DatasourceWizard parent;

	public DatasourceCsvPage(DatasourceWizard parent, Datasource datasource) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.parent = parent;
		chkHasHeader.setText(LabelsConstants.lblCnst.NotConsiderFirstRow());
		chkHasHeader.setValue(true);
		txtSeparator.setText("");
		lstType.addItem(LabelsConstants.lblCnst.DiskFile(), "disk");
		lstType.addItem(LabelsConstants.lblCnst.HdfsFile(), "hdfs");
		//TOTEST
		
		txtSeparator.addStyleName(VanillaCSS.COMMONS_TEXTBOX);
		txtHdfs.addStyleName(VanillaCSS.COMMONS_TEXTBOX);
		lstType.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		btnImport.addStyleName(VanillaCSS.COMMONS_BUTTON);
		btnImportHdfs.addStyleName(VanillaCSS.COMMONS_BUTTON);
		
		
		this.datasource = datasource;
		if(datasource != null && datasource.getObject() instanceof DatasourceCsv) {
			txtSeparator.setText(((DatasourceCsv)datasource.getObject()).getSeparator());
			pathFile = ((DatasourceCsv)datasource.getObject()).getFilePath();
			String longName = pathFile.substring(pathFile.lastIndexOf("/")+1);
			
			testFileType(longName);
			chkHasHeader.setValue(((DatasourceCsv)datasource.getObject()).getHasHeader());
			if(((DatasourceCsv)datasource.getObject()).getSourceType().equals("disk")){
				lstType.setSelectedIndex(0);
				lblFileName.setText(longName.substring(longName.indexOf("_")+1));
				panelDisk.setVisible(true);
				panelHdfs.setVisible(false);
			} else {
				lstType.setSelectedIndex(1);
				txtHdfs.setText(longName.substring(longName.indexOf("_")+1));
				panelDisk.setVisible(false);
				panelHdfs.setVisible(true);
			}
			
		} else {
			lstType.setSelectedIndex(0);
			panelDisk.setVisible(true);
			panelHdfs.setVisible(false);
		}
		
		fileUpload.setName("file");
		panelUpload.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		fileUpload.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				fileUpload.getFilename();
				formCode.submit();
				
			}
		});
		panelUpload.add(fileUpload);
		
		formCode.setAction(GWT.getHostPageBaseURL() + "UploadFileServlet");
		formCode.setEncoding(FormPanel.ENCODING_MULTIPART);
		formCode.setMethod(FormPanel.METHOD_POST);
		formCode.addSubmitCompleteHandler(submitCompleteHandler);
		formCode.setWidget(panelUpload);
	}

	@Override
	public boolean canGoBack() {
		return true;
	}

	@Override
	public boolean isComplete() {
		return !(pathFile.equals(null)||pathFile.equals(""));
	}

	@Override
	public boolean canGoFurther() {
		return false;
	}

	@Override
	public int getIndex() {
		return 1;
	}

	@Override
	public IDatasourceObject getDatasourceObject() {
		DatasourceCsv csv = new DatasourceCsv();

		if(datasource != null && datasource.getObject() != null && datasource.getObject() instanceof DatasourceCsv) {
			csv = (DatasourceCsv) datasource.getObject();
		}
		csv.setSourceType(lstType.getValue(lstType.getSelectedIndex()));
		csv.setSeparator(txtSeparator.getText());
		csv.setFilePath(pathFile);
		csv.setHasHeader(chkHasHeader.getValue());
		return csv;
	}
	
	@UiHandler("btnImport")
	public void onImportClick(ClickEvent event) {
		fileUpload.getElement().<InputElement>cast().click();
	}
	
	@UiHandler("btnImportHdfs")
	public void onImportHdfsClick(ClickEvent event) {
		if(txtHdfs.getText().equals("")) return;
		parent.showWaitPart(true);
		CommonService.Connect.getInstance().getHdfsFile(txtHdfs.getText(), new AsyncCallback<String>() {	
			@Override
			public void onSuccess(String result) {
				parent.showWaitPart(false);
				if(result.split(";")[0].equals("success")){
					pathFile = result.split(";")[1];
				} else {
					pathFile = "";
				}
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				parent.showWaitPart(false);
				caught.printStackTrace();
				ExceptionManager.getInstance().handleException(caught, caught.getMessage());
			}
		});
	}
	
	@UiHandler("lstType")
	public void onChangeType(ClickEvent event) {
		if(lstType.getValue(lstType.getSelectedIndex()).equals("disk")){
			panelDisk.setVisible(true);
			panelHdfs.setVisible(false);
			pathFile = "";
			lblFileName.setText("");
		} else {
			panelDisk.setVisible(false);
			panelHdfs.setVisible(true);
			pathFile = "";
			txtHdfs.setText("");
		}
	}
	
	@UiHandler("txtHdfs")
	public void onChangeText(ChangeEvent event) {
		pathFile = "";
	}
	
	private SubmitCompleteHandler submitCompleteHandler = new SubmitCompleteHandler() {
		
		public void onSubmitComplete(SubmitCompleteEvent event) {
			String res = event.getResults();
			
			if(res != ""){			
				pathFile = res;
				String longName = pathFile.substring(pathFile.lastIndexOf("/")+1);
				lblFileName.setText(longName.substring(longName.indexOf("_")+1));
				testFileType(longName);
			}
			
		}
	};

	private void testFileType(String path) {
		//String path = lblFileName.getText();
		if(path.substring(path.lastIndexOf(".")+1).equals("csv")){
			txtSeparator.setEnabled(true);
		} else {
			txtSeparator.setEnabled(false);
			txtSeparator.setText("");
		}
		
	};
	
}
