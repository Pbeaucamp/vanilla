package bpm.faweb.client.projection.dialog;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.images.FaWebImage;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.client.tree.FaWebRepositoryTree;
import bpm.faweb.shared.TreeParentDTO;
import bpm.faweb.shared.infoscube.InfosReport;
import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.utils.MessageHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * A dialog which allows to select or create a new projection
 * 
 * @author Marc Lanquetin
 * 
 */
public class DialogSelectProjection extends AbstractDialogBox {

	private static DialogSelectProjectionUiBinder uiBinder = GWT.create(DialogSelectProjectionUiBinder.class);

	interface DialogSelectProjectionUiBinder extends UiBinder<Widget, DialogSelectProjection> {
	}
	
	@UiField
	HTMLPanel contentPanel;

	@UiField
	HTMLPanel treePanel;

	private MainPanel mainPanel;
	private FaWebRepositoryTree tree;

	public DialogSelectProjection(MainPanel mainPanel) {
		super(FreeAnalysisWeb.LBL.ProjectionOpenOrCreate(), false, true);
		this.mainPanel = mainPanel;
		
		setWidget(uiBinder.createAndBindUi(this));
		
		createButtonBar(FreeAnalysisWeb.LBL.Open(), confirmHandler, FreeAnalysisWeb.LBL.Cancel(), cancelHandler);

		fillTree();
	}

	private void fillTree() {
		showWaitPart(true);
		
		FaWebService.Connect.getInstance().getRepositories(mainPanel.getKeySession(), FaWebService.PROJECTION, new AsyncCallback<TreeParentDTO>() {
			public void onSuccess(TreeParentDTO result) {
				if (result != null) {
					Image folder = new Image(FaWebImage.INSTANCE.folder());
					Image leaf = new Image(FaWebImage.INSTANCE.projection_16());
					Image[] i = { folder, leaf };

					tree = new FaWebRepositoryTree(mainPanel, i, result);

					treePanel.clear();
					treePanel.add(tree);
				}
				
				showWaitPart(false);
			}

			public void onFailure(Throwable ex) {
				showWaitPart(false);
				
				ex.printStackTrace();

				ExceptionManager.getInstance().handleException(ex, "Error while browsing the repository.");
			}
		});
	}
	
	@UiHandler("imgCreateProjection")
	public void onCreateProjectionClick(ClickEvent event) {
		DialogSelectProjection.this.hide();
		
		DialogCreateProjection dial = new DialogCreateProjection(mainPanel);
		dial.center();
	}
	
	private ClickHandler confirmHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			if(tree.getSelectedId() == -1) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), "You need to select a Item first.");
				return;
			}
			
			mainPanel.showWaitPart(true);
			
			FaWebService.Connect.getInstance().loadProjection(mainPanel.getKeySession(), tree.getSelectedId(), new AsyncCallback<InfosReport>() {

				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
					
					mainPanel.showWaitPart(false);
				}

				@Override
				public void onSuccess(InfosReport result) {
					mainPanel.setInfosReport(result);

					mainPanel.getDisplayPanel().loadProjection();

					DialogSelectProjection.this.hide();
					mainPanel.showWaitPart(false);
				}
			});
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			DialogSelectProjection.this.hide();
		}
	};
}
