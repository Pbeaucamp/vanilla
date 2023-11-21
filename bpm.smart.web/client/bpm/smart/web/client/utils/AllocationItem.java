package bpm.smart.web.client.utils;

import java.util.List;

import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.smart.web.client.wizards.RecodeClassificationPage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class AllocationItem extends Composite {

	private static AllocationItemUiBinder uiBinder = GWT.create(AllocationItemUiBinder.class);

	interface AllocationItemUiBinder extends UiBinder<Widget, AllocationItem> {
	}

	interface MyStyle extends CssResource {
		
	}

	@UiField
	MyStyle style;

	@UiField
	ListBox lstItem;

	@UiField
	Label lblItem;

	private RecodeClassificationPage parent;
	private List<String> levels;
	private String item;

	public AllocationItem(RecodeClassificationPage parent, String item, List<String> levels) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.levels = levels;
		this.item = item;
		
		lstItem.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		initItem();
	}

	private void initItem() {
		lblItem.setText(item);
		lstItem.addItem("");
		for(String lev : levels){
			lstItem.addItem(lev);
		}
	}

	public String getAllocation() {
		return lstItem.getItemText(lstItem.getSelectedIndex());
	}
	
	public void updateLevels(List<String> levels){
		this.levels = levels;
		lstItem.clear();
		lstItem.addItem("");
		for(String lev : levels){
			lstItem.addItem(lev);
		}
	}
	
	@UiHandler("lstItem")
	public void onChange(ChangeEvent e){
		parent.updateAllocation();
	}

	public String getItem() {
		return item;
	}
	
	
}