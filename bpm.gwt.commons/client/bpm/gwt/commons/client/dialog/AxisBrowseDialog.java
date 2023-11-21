package bpm.gwt.commons.client.dialog;

import java.util.List;

import bpm.fm.api.model.Axis;
import bpm.fm.api.model.utils.AxisInfo;
import bpm.fm.api.model.utils.LevelMember;
import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.utils.VanillaCSS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class AxisBrowseDialog extends AbstractDialogBox {

	private static AxisBrowseDialogUiBinder uiBinder = GWT.create(AxisBrowseDialogUiBinder.class);

	interface AxisBrowseDialogUiBinder extends UiBinder<Widget, AxisBrowseDialog> {
	}
	
	interface MyStyle extends CssResource {
		String item();
	}
	
	@UiField
	Tree axisTree;
	
	@UiField
	MyStyle style;

	private Axis axis;
	
	private boolean isConfirm;
	
	public AxisBrowseDialog(Axis axis) {
		super(LabelsConstants.lblCnst.browseAxis() + " " + axis.getName(), false, true);
		
		this.axis = axis;
		
		setWidget(uiBinder.createAndBindUi(this));
		
		createButtonBar(LabelsConstants.lblCnst.Ok(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		
		fillTree();
	}
	
	private void fillTree() {
		CommonService.Connect.getInstance().browseAxis(axis, new AsyncCallback<AxisInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				ExceptionManager.getInstance().handleException(caught, "Error while loading axis values");
			}

			@Override
			public void onSuccess(AxisInfo result) {
				
				TreeItem root = new TreeItem(new HTML(new Image(CommonImages.INSTANCE.folder()) + " " + axis.getName()));
				
				axisTree.addItem(root);
				createMembers(result.getMembers(), root);
			}
		});
	}
	
	private void createMembers(List<LevelMember> members, TreeItem parent) {
		
		for(LevelMember member : members) {
			final TreeItem memItem = new TreeItem(new HTML(new Image(CommonImages.INSTANCE.column()) + " " + member.getLabel())){
				@Override
				public void setSelected(boolean selected) {
					if (!selected) {
						this.removeStyleName(VanillaCSS.TREE_ITEM_SELECTED);
					}
					else {
						this.addStyleName(VanillaCSS.TREE_ITEM_SELECTED);
					}
					super.setSelected(selected);
				}
			};
			
			memItem.addStyleName(style.item());
			memItem.setUserObject(member);
			parent.addItem(memItem);
			createMembers(member.getChildren(), memItem);
		}
		
	}

	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			isConfirm = true;
			AxisBrowseDialog.this.hide();
		}
	};
	
	public boolean isConfirm() {
		return isConfirm;
	}
	
	public LevelMember getSelectedMember() {
		TreeItem item = axisTree.getSelectedItem();
		LevelMember member = (LevelMember) item.getUserObject();
		return member;
	}

	private ClickHandler cancelHandler = new ClickHandler() {	
		@Override
		public void onClick(ClickEvent event) {
			AxisBrowseDialog.this.hide();
		}
	};
	
	

}
