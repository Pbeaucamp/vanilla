package bpm.smart.web.client.wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import bpm.gwt.commons.client.custom.TextHolderBox;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.client.wizard.IGwtWizard;
import bpm.smart.core.model.RScriptModel;
import bpm.smart.web.client.I18N.LabelsConstants;
import bpm.smart.web.client.services.SmartAirService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class RecodeDatasetPage extends Composite implements IGwtPage {

	private static RecodeDatasetPageUiBinder uiBinder = GWT.create(RecodeDatasetPageUiBinder.class);

	interface RecodeDatasetPageUiBinder extends UiBinder<Widget, RecodeDatasetPage> {
	}

	interface MyStyle extends CssResource {
		
	}

	
	@UiField
	ListBox lstDatasets, lstColumns;
	
	@UiField 
	TextHolderBox txtNewColumn;
	
	@UiField 
	Label lblColumns, lblDatasets;
	
	@UiField
	MyStyle style;

	private IGwtWizard parent;

	public RecodeDatasetPage(RecodeWizard parent) {
		this.parent = parent;
		
		initWidget(uiBinder.createAndBindUi(this));
		lstDatasets.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		lstColumns.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		fillDatasets();
		
		switch(parent.getRecodeType()){
		case AGETORANGE:
			break;
		case CALCULATED:
			lstColumns.setVisible(false);
			break;
		case CLASS:
			lstColumns.setVisible(false);
			break;
		case DATETOAGE:
			break;
		case FILTER:
			lstColumns.setVisible(false);
			lblColumns.setText(LabelsConstants.lblCnst.NewDataset());
			txtNewColumn.setPlaceHolder(LabelsConstants.lblCnst.NewDataset());
			break;
		case MAP:
			break;
		case RECODE:
			break;
			
		}
	}
	
	private void fillDatasets() {
		RScriptModel box = new RScriptModel();
		String script = "f<-function(x){try(paste(x,class(get(x)), sep=';'))}\n";
//		script += "environment(f) <- "+user.getLogin()+user.getId() +"\n";
		script += "manual_result <- sapply(ls(),f)\n";
		script += "rm(f)";
		box.setScript(script);
//		box.setUserREnv(user.getLogin() + user.getId());
		box.setOutputs("manual_result".split(" "));

		parent.showWaitPart(true);
		SmartAirService.Connect.getInstance().executeScript(box, new GwtCallbackWrapper<RScriptModel>(parent, true) {
			@Override
			public void onSuccess(RScriptModel result) {

				List<String> list = new ArrayList<String>(Arrays.asList(result.getOutputVarstoString().get(0).split("\t")));
				Collections.sort(list);
				for(String item : list){
					if(item.contains(";") && item.split(";")[1].equals("data.frame")){
						lstDatasets.addItem(item.split(";")[0]);
					}
				}
				if(lstDatasets.getItemCount() > 0){
					onDatasetChange(null);
				}
			}
		}.getAsyncCallback());

	}
	
	@Override
	public boolean canGoBack() {
		return false;
	}

	@Override
	public boolean isComplete() {
		if(txtNewColumn.getText().equals("")){
			return false;
		}
		if(lstDatasets.getValue(lstDatasets.getSelectedIndex()).equals("")){
			return false;
		}
		if(lstColumns.getValue(lstColumns.getSelectedIndex()).equals("")){
			return false;
		}
		return true;
	}

	@Override
	public boolean canGoFurther() {
		return isComplete();
	}

	@Override
	public int getIndex() {
		return 0;
	}

	@UiHandler("txtNewColumn")
	public void onKeyup(KeyUpEvent event) {
		parent.updateBtn();
	}
	
	@UiHandler("lstDatasets")
	public void onDatasetChange(ChangeEvent e) {
		lstColumns.clear();
		RScriptModel box = new RScriptModel();
		String script = "manual_result <- names("+ lstDatasets.getValue(lstDatasets.getSelectedIndex()) +")";

		box.setScript(script);
		box.setOutputs("manual_result".split(" "));
		parent.showWaitPart(true);
		SmartAirService.Connect.getInstance().executeScript(box, new GwtCallbackWrapper<RScriptModel>(parent, true) {
			@Override
			public void onSuccess(RScriptModel result) {
				List<String> list = new ArrayList<String>(Arrays.asList(result.getOutputVarstoString().get(0).split("\t")));
				if(list.size()> 0) Collections.sort(list);
				for(String item : list){
					lstColumns.addItem(item);
				}
				parent.updateBtn();
			}
		}.getAsyncCallback());

		parent.updateBtn();
	}
	
	@UiHandler("lstColumns")
	public void onAddDatasourceClick(ClickEvent event) {
		parent.updateBtn();
	}
	
	public String getDatasetName() {
		return lstDatasets.getValue(lstDatasets.getSelectedIndex());
	}
	
	public String getColumnName() {
		return lstColumns.getValue(lstColumns.getSelectedIndex());
	}
	
	public String getNewColumnName() {
		return txtNewColumn.getText();
	}

	public IGwtWizard getWizardParent() {
		return parent;
	}
}
