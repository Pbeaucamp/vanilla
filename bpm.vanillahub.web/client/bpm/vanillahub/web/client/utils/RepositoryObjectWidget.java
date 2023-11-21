package bpm.vanillahub.web.client.utils;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanillahub.web.client.images.Images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class RepositoryObjectWidget extends Composite implements DragStartHandler {

	private static RepositoryObjectWidgetUiBinder uiBinder = GWT.create(RepositoryObjectWidgetUiBinder.class);

	interface RepositoryObjectWidgetUiBinder extends UiBinder<Widget, RepositoryObjectWidget> {
	}

	interface MyStyle extends CssResource {
		String imgObject();
	}

	@UiField
	FocusPanel dragPanel;

	@UiField
	Image imgObject;

	@UiField
	Label lblObject;

	@UiField
	MyStyle style;

	private IRepositoryObject repositoryObject;

	public RepositoryObjectWidget(IRepositoryObject repositoryObject, boolean dragAndDrop) {
		initWidget(uiBinder.createAndBindUi(this));
		this.repositoryObject = repositoryObject;

		imgObject.setResource(findResource(repositoryObject));
		imgObject.addStyleName(style.imgObject());

		lblObject.setText(repositoryObject.getName());

		if (repositoryObject instanceof RepositoryItem && dragAndDrop) {
			getElement().setDraggable(Element.DRAGGABLE_TRUE);
			dragPanel.addDragStartHandler(this);
		}
	}

	public IRepositoryObject getRepositoryObject() {
		return repositoryObject;
	}

	private ImageResource findResource(IRepositoryObject repositoryObject) {
		if (repositoryObject instanceof RepositoryDirectory) {
			return Images.INSTANCE.folder();
		}
		else if (repositoryObject instanceof RepositoryItem) {
			RepositoryItem item = (RepositoryItem) repositoryObject;

			int type = item.getType();
			int subType = item.getSubtype();
			if (type == IRepositoryApi.BIW_TYPE) {
				return Images.INSTANCE.biw();
			}
			else if (type == IRepositoryApi.GTW_TYPE) {
				return Images.INSTANCE.gateway_16();
			}
		}

		return Images.INSTANCE.object();
	}

	@Override
	public void onDragStart(DragStartEvent event) {
		RepositoryItem item = (RepositoryItem) repositoryObject;

		event.setData("id", String.valueOf(item.getId()));
		event.setData("name", item.getName());
		event.getDataTransfer().setDragImage(getElement(), 10, 10);
	}

}
