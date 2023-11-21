package bpm.smart.web.client.wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.client.wizard.IGwtWizard;
import bpm.smart.core.model.RScriptModel;
import bpm.smart.web.client.services.SmartAirService;
import bpm.smart.web.client.utils.RFilter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class RecodeFilterPage extends Composite implements IGwtPage {

	private static RecodeFilterPageUiBinder uiBinder = GWT.create(RecodeFilterPageUiBinder.class);

	interface RecodeFilterPageUiBinder extends UiBinder<Widget, RecodeFilterPage> {
	}
	
	interface MyStyle extends CssResource {
		
	}

	@UiField 
	HTMLPanel filtersPanel;
	
	@UiField
	Image btnAdd;
	
	@UiField
	MyStyle style;

	private IGwtWizard parent;
	
	private String datasetName;
	//private String column;
	private String newFrame;
	
	private List<String> columns = new ArrayList<String>();
	

	public RecodeFilterPage(IGwtWizard parent, String datasetName, String column, String newColumn) {
		this.parent = parent;
		this.datasetName = datasetName;
		//this.column = column;
		this.newFrame = newColumn; //ici nom du nouveau dataset
		
		initWidget(uiBinder.createAndBindUi(this));
		
		loadColumns();
		
	}
	
	private void initFilterPanel() {
		filtersPanel.add(new RFilter(getWizardParent(), datasetName, columns));
	}

	private void loadColumns() {
		String script = "manual_result<-names(" + datasetName + ")";

		RScriptModel model = new RScriptModel();
		model.setScript(script);
		model.setOutputs(new String[] { "manual_result" });

		parent.showWaitPart(true);
		SmartAirService.Connect.getInstance().executeScript(model, new GwtCallbackWrapper<RScriptModel>(parent, true) {
			@Override
			public void onSuccess(RScriptModel result) {
				String values = result.getOutputVarstoString().get(0);
				List<String> distincts = Arrays.asList(values.trim().split("\\t"));
				columns = distincts;
				initFilterPanel();
			}
		}.getAsyncCallback());

	}
	
	public boolean isAllComplete(){
		for(int i = 0; i<filtersPanel.getWidgetCount(); i++){
			if(filtersPanel.getWidget(i) instanceof RFilter){
				if(!((RFilter)filtersPanel.getWidget(i)).isComplete()){
					return false;
				}
			}
		}
		return true;
	}
	
	public List<String> getFiltering(){
		List<String> result = new ArrayList<String>();
		for(int i = 0; i<filtersPanel.getWidgetCount(); i++){
			if(filtersPanel.getWidget(i) instanceof RFilter){
				RFilter item = (RFilter)filtersPanel.getWidget(i);
				result.add(item.getRequest());
			}
		}
		return result;
	}
	
	@Override
	public boolean canGoBack() {
		return true;
	}

	@Override
	public boolean isComplete() {
		return true; //filtersPanel.getWidgetCount()>0 && isAllComplete();
	}

	@Override
	public boolean canGoFurther() {
		return isComplete();
	}

	@Override
	public int getIndex() {
		return 1;
	}
	
	public String getGeneratedCode() {
		StringBuffer classif = new StringBuffer();
		List<String> filters = getFiltering();
		for(String filter : filters){
			classif.append(filter+",");
		}
		
		StringBuffer result = new StringBuffer();
		result.append("library(dplyr)\n");
		result.append(newFrame + " <- filter(" + datasetName + ", " + classif.toString().substring(0, classif.length()-1) + ")\n");
		result.append(newFrame);
		return result.toString();
	}
	
	public IGwtWizard getWizardParent() {
		return parent;
	}

	@UiHandler("btnAdd")
	public void onAddRowLevel(ClickEvent event){
		filtersPanel.add(new RFilter(getWizardParent(), datasetName, columns));
	}
	
	public void updateFiltering() {
		parent.updateBtn(); //quand ???
	}

}
