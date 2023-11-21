package bpm.gwt.commons.client.datasource;

import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class DatasourceTypePage extends Composite implements IGwtPage {

	private static DatasourceTypePageUiBinder uiBinder = GWT.create(DatasourceTypePageUiBinder.class);

	interface DatasourceTypePageUiBinder extends UiBinder<Widget, DatasourceTypePage> {
	}

	@UiField
	ListBox lstType;
	
	@UiField 
	TextBox txtName;
	
	private DatasourceWizard parent;

	private Datasource datasource;

	public DatasourceTypePage(DatasourceWizard parent) {
		this.parent = parent;
		
		buildContent();
	}

	public DatasourceTypePage(DatasourceWizard parent, Datasource datasource) {
		this.parent = parent;
		this.datasource = datasource;
		
		buildContent();
		txtName.setText(datasource.getName());
	}
	
	private void buildContent() {
		initWidget(uiBinder.createAndBindUi(this));
		
		txtName.addStyleName(VanillaCSS.COMMONS_TEXTBOX);
		lstType.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		
		txtName.addDomHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				DatasourceTypePage.this.parent.updateBtn();
			}
		}, KeyUpEvent.getType());
		
		fillTypes();
	}

	private void fillTypes() {
		int i = 0;
		for(DatasourceType type : DatasourceType.values()) {		
			if(type == DatasourceType.CSV || type == DatasourceType.CSVVanilla) {
				continue;
			}
			lstType.addItem(type.getType(), type.toString());
			
					
			if(datasource != null && datasource.getType() == type) {
				lstType.setSelectedIndex(i);
			}		
			i++;
		}
	}

	@Override
	public boolean canGoBack() {
		return false;
	}

	@Override
	public boolean isComplete() {
		String name = getName();
		return name != null && !name.isEmpty();
	}

	@Override
	public boolean canGoFurther() {
		return isComplete();
	}

	@Override
	public int getIndex() {
		return 0;
	}

	public DatasourceType getSelectedType() {
		return DatasourceType.valueOf(lstType.getValue(lstType.getSelectedIndex()));
	}
	
	public String getName() {
		return txtName.getText();
	}
}
