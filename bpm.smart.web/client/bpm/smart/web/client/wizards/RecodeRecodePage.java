package bpm.smart.web.client.wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.client.wizard.IGwtWizard;
import bpm.smart.core.model.RScriptModel;
import bpm.smart.core.model.workflow.activity.RecodeActivity;
import bpm.smart.web.client.panels.properties.custom.RecodeMappingProperties;
import bpm.smart.web.client.services.SmartAirService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class RecodeRecodePage extends Composite implements IGwtPage {

	private static RecodeRecodePageUiBinder uiBinder = GWT.create(RecodeRecodePageUiBinder.class);

	interface RecodeRecodePageUiBinder extends UiBinder<Widget, RecodeRecodePage> {
	}
	
	interface MyStyle extends CssResource {
		String listPanel();
	}

	@UiField 
	HTMLPanel recodePanel;
	
	@UiField
	MyStyle style;

	private IGwtWizard parent;
	private RecodeMappingProperties mappingProperties;
	
	private String datasetName;
	private String column;
	private String newColumn;
	

	public RecodeRecodePage(IGwtWizard parent, String datasetName, String column, String newColumn) {
		this.parent = parent;
		this.datasetName = datasetName;
		this.column = column;
		this.newColumn = newColumn;
		
		initWidget(uiBinder.createAndBindUi(this));
		
		mappingProperties = new RecodeMappingProperties();
		mappingProperties.getElement().addClassName(style.listPanel());
		recodePanel.add(mappingProperties);
		loadMapping();
	}
	
	private void loadMapping() {
		String script = "manual_result<-" + datasetName + "$" + column;

		RScriptModel model = new RScriptModel();
		model.setScript(script);
		model.setOutputs(new String[] { "manual_result" });

		parent.showWaitPart(true);
		SmartAirService.Connect.getInstance().executeScript(model, new GwtCallbackWrapper<RScriptModel>(parent, true) {
			@Override
			public void onSuccess(RScriptModel result) {
				String values = result.getOutputVarstoString().get(0);
				List<String> distincts = Arrays.asList(values.trim().split("\\t"));

				HashSet<String> temp = new HashSet<String>(distincts);
				distincts = new ArrayList<String>(temp);

				mappingProperties.fillData(distincts, new RecodeActivity());
			}
		}.getAsyncCallback());

	}
	
	
	@Override
	public boolean canGoBack() {
		return true;
	}

	@Override
	public boolean isComplete() {
		return true;
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
		String result = "";
		
		String from = "c(";
		String to = "c(";
		for(String key : mappingProperties.getMappingValues().keySet()){
			from += "'"+ key + "',";
			to += "'"+ mappingProperties.getMappingValues().get(key) + "',";
		}
		from = from.substring(0,  from.length() - 1) + ")";
		to = to.substring(0,  to.length() - 1) + ")";
		
		result = "library(plyr)\n";
		result += datasetName + "$" + newColumn + " <- mapvalues("+ datasetName + "$" + column +", from = "+ from +", to = "+ to +")";
		
		
		return result;
	}
	
	public IGwtWizard getWizardParent() {
		return parent;
	}

}
