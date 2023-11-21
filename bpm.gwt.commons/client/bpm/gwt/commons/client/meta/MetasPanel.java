package bpm.gwt.commons.client.meta;

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
import bpm.vanilla.platform.core.beans.meta.MetaLink.TypeMetaLink;

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
	HTMLPanel panelMeta;

	@UiField
	MyStyle style;
	
	private IWait waitPanel;
	
	private List<MetaPanel> metas;

	public MetasPanel(IWait waitPanel, VanillaServerInformations server, int itemId, TypeMetaLink type, int formId, boolean reloadWithForm) {
		initWidget(uiBinder.createAndBindUi(this));
		this.waitPanel = waitPanel;
		
		loadMetaLinks(server, itemId, type, formId, reloadWithForm);
	}

	public MetasPanel(IWait waitPanel, VanillaServerInformations server, int formId) {
		initWidget(uiBinder.createAndBindUi(this));
		this.waitPanel = waitPanel;
		
		loadMeta(server, formId);
	}

	private void loadMetaLinks(final VanillaServerInformations server, int itemId, TypeMetaLink type, final int formId, final boolean reloadWithForm) {
		CommonService.Connect.getInstance().getMetaLinks(server, itemId, type, true, new GwtCallbackWrapper<List<MetaLink>>(waitPanel, true, true) {

			@Override
			public void onSuccess(List<MetaLink> result) {
				if ((result == null || result.isEmpty()) && reloadWithForm) {
					loadMeta(server, formId);
				}
				else {
					refreshMetaLinks(result);
				}
			}
		}.getAsyncCallback());
	}

	private void loadMeta(VanillaServerInformations server, int formId) {
		CommonService.Connect.getInstance().getMetaByForm(server, formId, new GwtCallbackWrapper<List<Meta>>(waitPanel, true, true) {

			@Override
			public void onSuccess(List<Meta> result) {
				if (result != null) {
					List<MetaLink> links = new ArrayList<MetaLink>();
					for (Meta meta : result) {
						links.add(new MetaLink(meta));
					}
					refreshMetaLinks(links);
				}
				else {
					refreshMetaLinks(null);
				}
			}
		}.getAsyncCallback());
	}

	public void refreshMetaLinks(List<MetaLink> links) {
		this.metas = new ArrayList<MetaPanel>();
		panelMeta.clear();
		
		if (links != null) {
			for (MetaLink link : links) {
				MetaPanel metaPanel = new MetaPanel(link);
				
				metas.add(metaPanel);
				panelMeta.add(metaPanel);
			}
		}
		else {
			//TODO: Add empty message
		}
	}
	
	public List<MetaLink> getMetaLinks() {
		List<MetaLink> links = new ArrayList<MetaLink>();
		if (metas != null) {
			for (MetaPanel meta : metas) {
				links.add(meta.getMetaValue());
			}
		}
		return links;
	}
}
