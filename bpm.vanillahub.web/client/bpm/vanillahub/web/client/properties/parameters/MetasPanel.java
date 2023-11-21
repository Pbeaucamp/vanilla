package bpm.vanillahub.web.client.properties.parameters;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.shared.VanillaServerInformations;
import bpm.vanilla.platform.core.beans.meta.Meta;
import bpm.vanilla.platform.core.beans.meta.MetaLink;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanillahub.core.beans.activities.attributes.MetaDispatch;

public class MetasPanel extends Composite {

	private static MetasPanelUiBinder uiBinder = GWT.create(MetasPanelUiBinder.class);

	interface MetasPanelUiBinder extends UiBinder<Widget, MetasPanel> {
	}

	interface MyStyle extends CssResource {
		String lblParam();

		String paramP();

		String captionParam();
	}

	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel panelMeta;
	
	private IWait waitPanel;
	
	private List<MetaPanel> metas;
	
	private List<Variable> variables;
	private List<Parameter> parameters;

	public MetasPanel(IWait waitPanel, VanillaServerInformations server, int formId, List<MetaDispatch> values, List<Variable> variables, List<Parameter> parameters) {
		initWidget(uiBinder.createAndBindUi(this));
		this.waitPanel = waitPanel;
		this.variables = variables;
		this.parameters = parameters;
		
		loadMeta(server, formId, values);
	}

	private void loadMeta(VanillaServerInformations server, int formId, final List<MetaDispatch> values) {
		CommonService.Connect.getInstance().getMetaByForm(server, formId, new GwtCallbackWrapper<List<Meta>>(waitPanel, true, true) {

			@Override
			public void onSuccess(List<Meta> result) {
				if (result != null) {
					List<MetaLink> links = new ArrayList<MetaLink>();
					for (Meta meta : result) {
						links.add(new MetaLink(meta));
					}
					refreshMetaLinks(links, values);
				}
				else {
					refreshMetaLinks(null, null);
				}
			}
		}.getAsyncCallback());
	}

	public void refreshMetaLinks(List<MetaLink> links, List<MetaDispatch> values) {
		this.metas = new ArrayList<MetaPanel>();
		panelMeta.clear();
		
		if (links != null) {
			for (MetaLink link : links) {
				MetaDispatch metaDispatch = null;
				if (values != null) {
					for (MetaDispatch meta : values) {
						if (meta.getMetaKey().equals(link.getMeta().getKey())) {
							metaDispatch = meta;
						}
					}
				}
				
				MetaPanel metaPanel = new MetaPanel(link, metaDispatch, variables, parameters);
				
				metas.add(metaPanel);
				panelMeta.add(metaPanel);
			}
		}
		else {
			//TODO: Add empty message
		}
	}

	public List<MetaDispatch> getMetaDispatch() {
		List<MetaDispatch> links = new ArrayList<MetaDispatch>();
		if (metas != null) {
			for (MetaPanel meta : metas) {
				if (meta.getMetaDispatch() != null) {
					links.add(meta.getMetaDispatch());
				}
			}
		}
		return links;
	}
}
