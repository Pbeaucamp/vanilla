package bpm.smart.web.client.wizards;

import java.util.Arrays;
import java.util.List;

import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.client.wizard.IGwtWizard;
import bpm.smart.core.model.RScriptModel;
import bpm.smart.web.client.services.SmartAirService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class RecodeCalculatedPage extends Composite implements IGwtPage {

	private static RecodeCalculatedPageUiBinder uiBinder = GWT.create(RecodeCalculatedPageUiBinder.class);

	interface RecodeCalculatedPageUiBinder extends UiBinder<Widget, RecodeCalculatedPage> {
	}
	
	interface MyStyle extends CssResource {
		String btn();
	}

	@UiField 
	HTMLPanel columnsPanel;
	
	@UiField 
	TextArea txtFormula;
	
	@UiField
	MyStyle style;

	private IGwtWizard parent;
	
	private String datasetName;
	//private String column;
	private String newColumn;
	

	public RecodeCalculatedPage(IGwtWizard parent, String datasetName, String column, String newColumn) {
		this.parent = parent;
		this.datasetName = datasetName;
		//this.column = column;
		this.newColumn = newColumn;
		
		initWidget(uiBinder.createAndBindUi(this));
		
		loadColumns();
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

				for(final String col : distincts){
					Button btn = new Button(col);
					btn.addStyleName(style.btn());
					btn.addStyleName(VanillaCSS.COMMONS_BUTTON);
					btn.addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							txtFormula.setText(txtFormula.getText() + (datasetName + "$" + col));
						}
					});
					columnsPanel.add(btn);
				}
			}
		}.getAsyncCallback());

	}
	
	
	@Override
	public boolean canGoBack() {
		return true;
	}

	@Override
	public boolean isComplete() {
		return !txtFormula.getText().equals("");
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
		String result = datasetName + "$" + newColumn + " <- " + txtFormula.getText();
		return result;
	}
	
	public IGwtWizard getWizardParent() {
		return parent;
	}
	
	@UiHandler("txtFormula")
	public void onKeyup(KeyUpEvent event) {
		parent.updateBtn();
	}

}
