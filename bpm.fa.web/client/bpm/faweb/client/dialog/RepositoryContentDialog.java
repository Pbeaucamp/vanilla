package bpm.faweb.client.dialog;

import java.util.List;

import java_cup.internal_error;
import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.images.FaWebImage;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.client.tree.FaWebRepositoryTree;
import bpm.faweb.client.utils.Space;
import bpm.faweb.shared.TreeParentDTO;
import bpm.faweb.shared.infoscube.ItemView;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.loading.GreyAbsolutePanel;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.loading.WaitAbsolutePanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class RepositoryContentDialog extends AbstractDialogBox implements IWait {

	private static RepositoryContentDialogUiBinder uiBinder = GWT.create(RepositoryContentDialogUiBinder.class);

	interface RepositoryContentDialogUiBinder extends UiBinder<Widget, RepositoryContentDialog> {
	}
	
	interface MyStyle extends CssResource {
		String repositoryContent();
		String exportUpdate();
	}
	
	@UiField
	MyStyle style;

	@UiField
	HTMLPanel contentPanel;
	
	@UiField
	HTMLPanel panelHeader;
	
	@UiField
	SimplePanel panelContent;

	private MainPanel mainCompParent;
	
	private TreeParentDTO datas;
	private FaWebRepositoryTree tree;
	private Image[] images = new Image[] { new Image(FaWebImage.INSTANCE.folder()), new Image(FaWebImage.INSTANCE.view()) };
	private List<ItemView> viewToSave;
	
	private boolean isCharging = false;
	private GreyAbsolutePanel greyPanel;
	private WaitAbsolutePanel waitPanel;

	public RepositoryContentDialog(final MainPanel mainCompParent, TreeParentDTO d, List<ItemView> views) {
		super(LabelsConstants.lblCnst.Repository(), false, true);
		this.mainCompParent = mainCompParent;
		this.datas = d;
		this.viewToSave = views;
		
		setWidget(uiBinder.createAndBindUi(this));

		if (views != null) {
			images[1] = new Image(FaWebImage.INSTANCE.fd());
		}
		else {
			if (mainCompParent.getDisplayPanel().isOnProjection()) {
				images[1] = new Image(FaWebImage.INSTANCE.projection_16());
			}
		}

		tree = new FaWebRepositoryTree(mainCompParent, images, datas);

		Label lblRepositoryContent = new Label(LabelsConstants.lblCnst.RepositoryContent());
		lblRepositoryContent.addStyleName(style.repositoryContent());

		Label lblExportUpdate = new Label(LabelsConstants.lblCnst.ExportUpdateRepository());
		lblExportUpdate.addStyleName(style.exportUpdate());
		
		panelHeader.add(lblRepositoryContent);
		panelHeader.add(lblExportUpdate);

		setTree(tree);

		createButton(FreeAnalysisWeb.LBL.Cancel(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				RepositoryContentDialog.this.hide();
			}
		});
		createButton(FreeAnalysisWeb.LBL.Save(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				String select = getTree().getSelectedValue();

				if (viewToSave != null || select.equals("IDirectory")) {
					SaveDialog saveDialog = new SaveDialog(mainCompParent, RepositoryContentDialog.this, viewToSave, mainCompParent.getDisplayPanel().isOnProjection());
					saveDialog.setDirectoryId(getTree().getSelectedId());
					saveDialog.center();
				}
			}
		});

	}
	
	@UiHandler("imgRefresh")
	public void onRefreshClick(ClickEvent event) {
		showWaitPart(true);
		
		FaWebService.Connect.getInstance().getRepositories(mainCompParent.getKeySession(), FaWebService.FAV, new AsyncCallback<TreeParentDTO>() {

			public void onFailure(Throwable ex) {
				showWaitPart(false);
				
				ex.printStackTrace();
			}

			public void onSuccess(TreeParentDTO result) {
				showWaitPart(false);
				
				if (result != null) {
					setTree(new FaWebRepositoryTree(mainCompParent, images, result));
				}
			}
		});
	}

	public FaWebRepositoryTree getTree() {
		return tree;
	}

	public void setTree(FaWebRepositoryTree tree) {
		this.tree = tree;
		panelContent.setWidget(tree);
	}

	public Image[] getImages() {
		return images;
	}

	public void setImages(Image[] images) {
		this.images = images;
	}

	@Override
	public void showWaitPart(boolean visible) {
		if (visible) {
			if (!isCharging) {
				isCharging = true;

				greyPanel = new GreyAbsolutePanel();
				waitPanel = new WaitAbsolutePanel();

				contentPanel.add(greyPanel);
				contentPanel.add(waitPanel);
				
				int height = contentPanel.getOffsetHeight();
				int width = contentPanel.getOffsetWidth();

				DOM.setStyleAttribute(waitPanel.getElement(), "top", ((height / 2) - 50) + "px");
				DOM.setStyleAttribute(waitPanel.getElement(), "left", ((width / 2) - 100) + "px");
			}
		}
		else if (!visible) {
			if (isCharging) {
				isCharging = false;

				contentPanel.remove(greyPanel);
				contentPanel.remove(waitPanel);
			}
		}
	}

}
