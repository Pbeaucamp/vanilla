package bpm.fwr.client.tree;

import bpm.fwr.client.images.WysiwygImage;
import bpm.fwr.client.tree.TreeItemOk.TypeItem;
import bpm.fwr.shared.models.IDirectoryDTO;
import bpm.fwr.shared.models.IDirectoryItemDTO;
import bpm.fwr.shared.models.TreeParentDTO;
import bpm.gwt.commons.client.custom.CustomHTML;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class FwrRepositoryTree extends Tree {

	public FwrRepositoryTree(TreeParentDTO parent){
		super();

		if(parent.getChildren() != null)
		for (Object item : parent.getChildren()) {
			if(item instanceof IDirectoryDTO) {
				IDirectoryDTO servApp = (IDirectoryDTO) item;
	
				TreeItemOk main = new TreeItemOk(new CustomHTML(new Image(WysiwygImage.INSTANCE.folder()) + " " + servApp.getName()), TypeItem.FOLDER, servApp.getId());
				builChildDir(servApp, main);
				this.addItem(main);
			}
		}
		
	}
	
	private void builChildDir(IDirectoryDTO servApp, TreeItem app) {

		if(servApp.getChildren() != null && servApp.getChildren().length >0){
			for (int j = 0; j < servApp.getChildren().length; j++) {

				if( servApp.getChildren()[j] != null &&  servApp.getChildren()[j] instanceof IDirectoryItemDTO){

					IDirectoryItemDTO item = (IDirectoryItemDTO) servApp.getChildren()[j];

					TreeItemOk child = new TreeItemOk(new CustomHTML(new Image(WysiwygImage.INSTANCE.report()) + " " + item.getName()), TypeItem.ITEM, item.getId());
					child.setUserObject(item);
					app.addItem(child);

				}
				else if(servApp.getChildren()[j] != null &&  servApp.getChildren()[j] instanceof IDirectoryDTO){
					IDirectoryDTO dir = (IDirectoryDTO) servApp.getChildren()[j];

					TreeItemOk child = new TreeItemOk(new CustomHTML(new Image(WysiwygImage.INSTANCE.folder()) + " " + dir.getName()), TypeItem.FOLDER, dir.getId());
					child.setUserObject(dir);
					app.addItem(child);
					
					builChildDir((IDirectoryDTO) servApp.getChildren()[j], child);

					app.addItem(child);

					
				}
			}
		}
	}
	
	
	public TypeItem getSelectedType() {
		TreeItem it = getSelectedItem();
		TreeItemOk fti = (TreeItemOk) it;
		return fti.getTypeItem();
	}
	
	public int getSelectedId(){
		TreeItem it = getSelectedItem();
		TreeItemOk fti = (TreeItemOk) it;
		return fti.getItemId();
	}

}
