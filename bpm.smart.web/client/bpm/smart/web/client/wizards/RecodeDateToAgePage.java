package bpm.smart.web.client.wizards;

import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.client.wizard.IGwtWizard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class RecodeDateToAgePage extends Composite implements IGwtPage{

	private static RecodeDateToAgePageUiBinder uiBinder = GWT.create(RecodeDateToAgePageUiBinder.class);

	interface RecodeDateToAgePageUiBinder extends UiBinder<Widget, RecodeDateToAgePage> {
	}
	
	interface MyStyle extends CssResource {
		
	}
	
	@UiField 
	TextBox txtFormat;
	
	@UiField
	MyStyle style;
	
	private IGwtWizard parent;
	
	private String datasetName;
	private String column;
	private String newColumn;
	

	public RecodeDateToAgePage(IGwtWizard parent, String datasetName, String column, String newColumn) {
		this.parent = parent;
		this.datasetName = datasetName;
		this.column = column;
		this.newColumn = newColumn;
		
		initWidget(uiBinder.createAndBindUi(this));
		txtFormat.addStyleName(VanillaCSS.COMMONS_TEXTBOX);
	}
	
	public String getGeneratedCode() {
		String result = "";
		
		result = "library(eeptools)\n";
		result += datasetName + "$" + newColumn + " <- floor(age_calc(as.Date("+ datasetName + "$" + column + (txtFormat.getText().equals("")? "" : ", '" + txtFormat.getText() + "'") +"), units = 'years'))";
		
		return result;
	}
	
	public IGwtWizard getWizardParent() {
		return parent;
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

}
