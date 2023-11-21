package bpm.vanillahub.web.client.properties.parameters;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.workflow.commons.client.workflow.properties.VariableTextPanel;
import bpm.vanilla.platform.core.beans.meta.MetaLink;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanillahub.core.beans.activities.attributes.MetaDispatch;

public class MetaPanel extends Composite {

	private static MetaPanelUiBinder uiBinder = GWT.create(MetaPanelUiBinder.class);

	interface MetaPanelUiBinder extends UiBinder<Widget, MetaPanel> {
	}
	
	@UiField
	HTMLPanel mainPanel;
	
	@UiField
	CheckBox chkMeta;

	@UiField(provided=true)
	VariableTextPanel txt;
	
	private MetaLink link;

	public MetaPanel(MetaLink link, MetaDispatch meta, List<Variable> variables, List<Parameter> parameters) {
		this.txt = new VariableTextPanel(link.getMeta().getLabel(), meta != null ? meta.getValue() : null, variables, parameters);
		initWidget(uiBinder.createAndBindUi(this));
		this.link = link;
		
		chkMeta.setValue(meta != null);
	}

	public MetaDispatch getMetaDispatch() {
		return chkMeta.getValue() ? new MetaDispatch(link.getMeta().getKey(), txt.getVariableText()) : null;
	}
}
