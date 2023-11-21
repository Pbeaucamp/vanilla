package bpm.gwt.commons.client.meta;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.commons.client.custom.LabelDateBox;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.custom.v2.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.CommonService;
import bpm.vanilla.platform.core.beans.meta.Meta;
import bpm.vanilla.platform.core.beans.meta.MetaLink;
import bpm.vanilla.platform.core.beans.meta.MetaValue;
import bpm.vanilla.platform.core.beans.meta.Meta.TypeMeta;
import bpm.vanilla.platform.core.beans.resources.ClassDefinition;

public class MetaPanel extends Composite {

	private static MetaPanelUiBinder uiBinder = GWT.create(MetaPanelUiBinder.class);

	interface MetaPanelUiBinder extends UiBinder<Widget, MetaPanel> {
	}
	
	interface MyStyle extends CssResource {
		String fixed();
		String flexItem();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel mainPanel;
	
	private DateTimeFormat df = DateTimeFormat.getFormat(MetaValue.DATE_FORMAT);
	
	private Composite item;
	
	private MetaLink link;

	public MetaPanel(MetaLink metaLink) {
		initWidget(uiBinder.createAndBindUi(this));
		this.link = metaLink;
		
		Meta meta = metaLink.getMeta();
		switch (meta.getType()) {
		case STRING:
		case VALIDATION:
			LabelTextBox lblText = new LabelTextBox();
			lblText.setLabel(meta.getLabel());
			loadString(lblText, metaLink.getValue());
			this.item = lblText;
			break;
		case DATE:
			LabelDateBox lblDB = new LabelDateBox();
			lblDB.setLabel(meta.getLabel());
			loadDate(lblDB, metaLink.getValue());
			this.item = lblDB;
			break;
		default:
			break;
		}
		
		mainPanel.add(item);
		
		if (meta.getType() == TypeMeta.VALIDATION) {
			((LabelTextBox) item).setEnabled(false);
			item.addStyleName(style.fixed());
			
			ListBoxWithButton<ClassDefinition> lstValidations = new ListBoxWithButton<ClassDefinition>();
			lstValidations.setEnabled(false);
			lstValidations.addStyleName(style.flexItem());
			loadValidations(lstValidations, metaLink.getMeta());
			mainPanel.add(lstValidations);
		}
	}

	private void loadString(LabelTextBox lblText, MetaValue value) {
		if (value != null) {
			lblText.setText(value.getValue());
		}
	}

	private void loadDate(LabelDateBox lblDB, MetaValue value) {
		if (value != null && value.getValue() != null && !value.getValue().isEmpty()) {
			lblDB.setValue(df.parse(value.getValue()));
		}
	}

	private void loadValidations(final ListBoxWithButton<ClassDefinition> lstValidations, final Meta meta) {
		CommonService.Connect.getInstance().loadSchemaValidations(new GwtCallbackWrapper<List<ClassDefinition>>(null, false, false) {

			@Override
			public void onSuccess(List<ClassDefinition> result) {
				for (ClassDefinition validation : result) {
					lstValidations.addItem(validation.getName(), validation);
					if (meta != null && meta.getSchemaDefinition() != null && !meta.getSchemaDefinition().isEmpty() && meta.getSchemaDefinition().equals(validation.getName())) {
						lstValidations.setSelectedObject(validation);
					}
				}
			}
		}.getAsyncCallback());
	}

	public MetaLink getMetaValue() {
		if (link.getValue() == null) {
			link.setValue(new MetaValue());
		}
		link.getValue().setValue(getValue());
		return link;
	}

	private String getValue() {
		if (item instanceof LabelDateBox) {
			Date value = ((LabelDateBox) item).getValue();
			return value != null ? df.format(value) : null;
		}
		else if (item instanceof LabelTextBox) {
			return ((LabelTextBox) item).getText();
		}
		return null;
	}
}
