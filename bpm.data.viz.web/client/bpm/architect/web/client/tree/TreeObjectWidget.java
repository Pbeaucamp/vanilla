package bpm.architect.web.client.tree;

import bpm.gwt.commons.client.images.CommonImages;
import bpm.vanilla.platform.core.beans.resources.ClassDefinition;
import bpm.vanilla.platform.core.beans.resources.ClassField;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class TreeObjectWidget<T> extends Composite {
	
	public static String TREE_OBJECT_WIDGET = "TreeObjectWidget";

	private static TreeObjectWidgetUiBinder uiBinder = GWT.create(TreeObjectWidgetUiBinder.class);

	interface TreeObjectWidgetUiBinder extends UiBinder<Widget, TreeObjectWidget<?>> {
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

	private T item;
	
	public TreeObjectWidget(T item) {
		initWidget(uiBinder.createAndBindUi(this));
		this.item = item;

		imgObject.setResource(findResource(item));
		imgObject.addStyleName(style.imgObject());

		lblObject.setText(item.toString());
	}
	
	public void refreshLabel() {
		lblObject.setText(item.toString());
	}

	public T getItem() {
		return item;
	}

	private ImageResource findResource(T item) {
		if (item instanceof ClassDefinition) {
			return CommonImages.INSTANCE.directory_item();
		}
		else if (item instanceof ClassField) {
			return CommonImages.INSTANCE.datasource();
		}

		return CommonImages.INSTANCE.object();
	}
}
