package bpm.vanilla.portal.client.widget.custom;

import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.portal.client.panels.center.RepositoryContentPanel;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ItemThumbnailPanel extends Composite implements HasClickHandlers, ClickHandler {

	private static ItemThumbnailPanelUiBinder uiBinder = GWT.create(ItemThumbnailPanelUiBinder.class);

	interface ItemThumbnailPanelUiBinder extends UiBinder<Widget, ItemThumbnailPanel> {
	}
	
	interface MyStyle extends CssResource {
		String imgItem();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	Image imgItem;
	
	@UiField
	Label lblItem;

	private HandlerManager handlerManager = new HandlerManager(this);
	private RepositoryContentPanel repositoryPanel;
	private IRepositoryObject item;
	
	public ItemThumbnailPanel(RepositoryContentPanel repositoryPanel, IRepositoryObject item) {
		initWidget(uiBinder.createAndBindUi(this));
		this.repositoryPanel = repositoryPanel;
		this.item = item;
		
		imgItem.setResource(ToolsGWT.getImageForObject(item, true));
		imgItem.addStyleName(style.imgItem());
		
		lblItem.setText(item.getName());

		this.sinkEvents(Event.ONCONTEXTMENU | Event.ONCLICK | Event.ONDBLCLICK);
		this.addClickHandler(this);
	}

	@Override
	public void onBrowserEvent(Event event) {
		repositoryPanel.selectItem(this);
		repositoryPanel.dispatchAction(item, event);
		super.onBrowserEvent(event);
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		handlerManager.fireEvent(event);
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return handlerManager.addHandler(ClickEvent.getType(), handler);
	}

	@Override
	public void onClick(ClickEvent event) {
		repositoryPanel.displayInfoItem(item);
	}

}
