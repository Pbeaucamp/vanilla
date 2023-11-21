package bpm.smart.web.client.wizards;

import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.client.wizard.IGwtWizard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class RecodeResultPage extends Composite implements IGwtPage {

	private static RecodeResultPageUiBinder uiBinder = GWT.create(RecodeResultPageUiBinder.class);

	interface RecodeResultPageUiBinder extends UiBinder<Widget, RecodeResultPage> {
	}
	
	interface MyStyle extends CssResource {
		
	}
	
	@UiField 
	TextArea txtCode;
	
	@UiField
	MyStyle style;

	private IGwtWizard parent;
	private String code;
	

	public RecodeResultPage(IGwtWizard parent, String code) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.code = code;
		txtCode.setText(code);
		txtCode.setReadOnly(true);
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
		return false;
	}

	@Override
	public int getIndex() {
		return 2;
	}
	
	public String getCode() {
		return code;
	}

	public IGwtWizard getWizardParent() {
		return parent;
	}

}
