package bpm.gwt.commons.client.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.vanilla.platform.core.beans.resources.D4CItem;
import bpm.vanilla.platform.core.beans.resources.D4CItem.TypeD4CItem;
import bpm.vanilla.platform.core.beans.resources.D4CItemVisualization;

public class AddD4CItemDialog<T extends D4CItem> extends AbstractDialogBox {

	private static AddD4CItemDialogUiBinder uiBinder = GWT.create(AddD4CItemDialogUiBinder.class);

	interface AddD4CItemDialogUiBinder extends UiBinder<Widget, AddD4CItemDialog<?>> {
	}
	
	interface MyStyle extends CssResource {
		String maxHeight();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	LabelTextBox txtName, txtUrl;
	
	private T item;
	private int parentId;
	private TypeD4CItem type;

	private boolean isConfirm = false;

	public AddD4CItemDialog(int parentId, TypeD4CItem type) {
		this(null, parentId, type);
	}

	public AddD4CItemDialog(T item, int parentId, TypeD4CItem type) {
		super(LabelsConstants.lblCnst.D4CItem(), false, true);
		this.item = item;
		this.parentId = parentId;
		this.type = type;
		
		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsConstants.lblCnst.Confirmation(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		
		if (item != null) {
			txtName.setText(item.getName());
			
			if (item instanceof D4CItemVisualization) {
				txtUrl.setText(((D4CItemVisualization) item).getUrl());
			}
		}
	}
	
	public boolean isConfirm() {
		return isConfirm;
	}

	public D4CItem getItem() {
		String name = txtName.getText();
		String url = txtUrl.getText();
		
		D4CItem item = this.item;
		
		switch (type) {
		case VISUALIZATION:
			if (item == null) {
				item = new D4CItemVisualization();
			}
			((D4CItemVisualization) item).setUrl(url);
			break;

		default:
			break;
		}
		
		item.setName(name);
		item.setParentId(parentId);
		
		return item;
	}

	private boolean isComplete() {
		boolean nameDefined = !txtName.getText().isEmpty();
		
		switch (type) {
		case VISUALIZATION:
			return nameDefined && !txtUrl.getText().isEmpty();

		default:
			break;
		}
		
		return nameDefined;
	}

	private ClickHandler okHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			if (isComplete()) {
				isConfirm = true;
				
				hide();
			}
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

}
