package bpm.gwt.commons.client.datasource;

import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceHBase;
import bpm.vanilla.platform.core.beans.data.DatasourceJdbc;
import bpm.vanilla.platform.core.beans.data.IDatasourceObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class DatasourceHBasePage extends Composite implements IDatasourceObjectPage {

	private static DatasourceHBasePageUiBinder uiBinder = GWT.create(DatasourceHBasePageUiBinder.class);

	interface DatasourceHBasePageUiBinder extends UiBinder<Widget, DatasourceHBasePage> {
	}
	
	@UiField
	TextBox txtIPServer, txtPort;
	

	private Datasource datasource;

	public DatasourceHBasePage(DatasourceWizard parent, Datasource datasource) {
		initWidget(uiBinder.createAndBindUi(this));
		txtIPServer.addStyleName(VanillaCSS.COMMONS_TEXTBOX);
		txtPort.addStyleName(VanillaCSS.COMMONS_TEXTBOX);
		
		this.datasource = datasource;
		if(datasource != null && datasource.getObject() != null && datasource.getObject() instanceof DatasourceHBase) {
			txtIPServer.setText(((DatasourceJdbc)datasource.getObject()).getPassword());
			txtPort.setText(((DatasourceJdbc)datasource.getObject()).getUrl());
		}
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
		return 1;
	}

	@Override
	public IDatasourceObject getDatasourceObject() {
		
		
		DatasourceHBase dtsHbase = new DatasourceHBase();
		
		if(datasource != null && datasource.getObject() != null && datasource.getObject() instanceof DatasourceHBase) {
			dtsHbase = (DatasourceHBase) datasource.getObject();
		}
		
		dtsHbase.setUrl(txtIPServer.getText());
		dtsHbase.setPort(txtPort.getText());
		
		
		return dtsHbase;
	}
	
	
}
