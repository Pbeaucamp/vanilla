package bpm.smart.web.client.wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.client.wizard.IGwtWizard;
import bpm.smart.core.model.RScriptModel;
import bpm.smart.web.client.services.SmartAirService;
import bpm.smart.web.client.utils.AllocationItem;
import bpm.smart.web.client.utils.LevelItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class RecodeClassificationPage extends Composite implements IGwtPage {

	private static RecodeClassificationPageUiBinder uiBinder = GWT.create(RecodeClassificationPageUiBinder.class);

	interface RecodeClassificationPageUiBinder extends UiBinder<Widget, RecodeClassificationPage> {
	}
	
	interface MyStyle extends CssResource {
		
	}

	@UiField 
	HTMLPanel affectPanel, classPanel;
	
	@UiField 
	ListBox lstColumns;
	
	@UiField
	Image btnAdd;
	
	@UiField
	Button btnRefresh;
	
	@UiField
	MyStyle style;

	private IGwtWizard parent;
	
	private String datasetName;
	//private String column;
	private String newColumn;
	
	private List<String> levels = new ArrayList<String>();
	

	public RecodeClassificationPage(IGwtWizard parent, String datasetName, String column, String newColumn) {
		this.parent = parent;
		this.datasetName = datasetName;
		//this.column = column;
		this.newColumn = newColumn;
		
		initWidget(uiBinder.createAndBindUi(this));
		
		btnRefresh.addStyleName(VanillaCSS.COMMONS_BUTTON);
		lstColumns.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		loadColumns();
		initLevelPanel();
		
	}
	
	private void initLevelPanel() {
		classPanel.add(new LevelItem());
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

				lstColumns.addItem("");
				for(final String col : distincts){
					lstColumns.addItem(col);
				}
				
				onChangeColumn(null);
			}
		}.getAsyncCallback());

	}
	
	private void loadMapping() {
		StringBuffer script = new StringBuffer();
		script.append("if(is.numeric("+ datasetName + "$" + lstColumns.getItemText(lstColumns.getSelectedIndex()).trim() + ") | is.integer("+ datasetName + "$" + lstColumns.getItemText(lstColumns.getSelectedIndex()).trim() + ")){\n");
			script.append("manual_result<- format(" + datasetName + "$" + lstColumns.getItemText(lstColumns.getSelectedIndex()).trim() +")\n");
		script.append("} else {\n");
		script.append("manual_result<- " + datasetName + "$" + lstColumns.getItemText(lstColumns.getSelectedIndex()).trim()+"\n");
		script.append("}");
		
		RScriptModel model = new RScriptModel();
		model.setScript(script.toString());
		model.setOutputs(new String[] { "manual_result" });

		parent.showWaitPart(true);
		SmartAirService.Connect.getInstance().executeScript(model, new GwtCallbackWrapper<RScriptModel>(parent, true) {
			@Override
			public void onSuccess(RScriptModel result) {
				String values = result.getOutputVarstoString().get(0);
				List<String> distincts = Arrays.asList(values.trim().split("\\t"));

				HashSet<String> temp = new HashSet<String>(distincts);
				distincts = new ArrayList<String>(temp);

				for(String item: distincts){
					affectPanel.add(new AllocationItem(RecodeClassificationPage.this, item, levels));
				}
			}
		}.getAsyncCallback());

	}

	public List<String> getNewLevels(){
		List<String> result = new ArrayList<String>();
		
		for(int i = 0; i<classPanel.getWidgetCount(); i++){
			if(classPanel.getWidget(i) instanceof LevelItem){
				result.add(((LevelItem)classPanel.getWidget(i)).getLevel());
			}
		}
		return result;
	}
	
	public boolean isAllocationComplete(){
		for(int i = 0; i<affectPanel.getWidgetCount(); i++){
			if(affectPanel.getWidget(i) instanceof AllocationItem){
				if(((AllocationItem)affectPanel.getWidget(i)).getAllocation().equals("")){
					return false;
				}
			}
		}
		return true;
	}
	
	public Map<String, String> getAllocation(){
		Map<String, String> result = new HashMap<String, String>();
		for(int i = 0; i<affectPanel.getWidgetCount(); i++){
			if(affectPanel.getWidget(i) instanceof AllocationItem){
				AllocationItem item = (AllocationItem)affectPanel.getWidget(i);
				result.put(item.getItem(), item.getAllocation());
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
		return levels.size()>1 && isAllocationComplete();
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
		Map<String, String> allocation = getAllocation();
		for(String key : allocation.keySet()){
			classif.append("'"+key+"'='"+allocation.get(key)+"',");
		}
		StringBuffer result = new StringBuffer();
		result.append("classification <- c(" + classif.toString().substring(0, classif.length()-1) + ")\n");
		
		result.append(datasetName + "$" + newColumn + " <- as.factor(classification[as.character("+ datasetName + "$" + lstColumns.getItemText(lstColumns.getSelectedIndex()) +")])\n");
		
		return result.toString();
	}
	
	public IGwtWizard getWizardParent() {
		return parent;
	}
	
	@UiHandler("lstColumns")
	public void onChangeColumn(ChangeEvent event) {
		affectPanel.clear();
		if(!lstColumns.getItemText(lstColumns.getSelectedIndex()).equals("")){
			loadMapping();
		}
		
	}
	
	@UiHandler("btnRefresh")
	public void onClick(ClickEvent event){
		levels = getNewLevels();
		for(int i = 0; i<affectPanel.getWidgetCount(); i++){
			if(affectPanel.getWidget(i) instanceof AllocationItem){
				AllocationItem item = (AllocationItem)affectPanel.getWidget(i);
				item.updateLevels(levels);
			}
		}
	}

	@UiHandler("btnAdd")
	public void onAddRowLevel(ClickEvent event){
		classPanel.add(new LevelItem());
	}
	
	public void updateAllocation() {
		parent.updateBtn();
	}

}
