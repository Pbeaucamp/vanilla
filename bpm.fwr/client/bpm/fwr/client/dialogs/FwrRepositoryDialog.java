package bpm.fwr.client.dialogs;

import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.WysiwygPanel;
import bpm.fwr.client.panels.Viewer;
import bpm.fwr.client.services.FwrServiceMetadata;
import bpm.fwr.client.tree.FwrRepositoryTree;
import bpm.fwr.client.tree.TreeItemOk.TypeItem;
import bpm.fwr.shared.models.TreeParentDTO;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.listeners.FinishListener;
import bpm.gwt.commons.shared.InfoUser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FwrRepositoryDialog extends AbstractDialogBox {

	private static FwrRepositoryDialogUiBinder uiBinder = GWT.create(FwrRepositoryDialogUiBinder.class);

	interface FwrRepositoryDialogUiBinder extends UiBinder<Widget, FwrRepositoryDialog> {
	}
	
	interface MyStyle extends CssResource {
		String treePanel();
		String repositoryContent();
	}
	
	@UiField
	MyStyle style;

	@UiField
	HTMLPanel contentPanel;

	private WysiwygPanel wysiwygPanelParent;
	private Viewer viewer;

	private VerticalPanel middle;
	private FwrRepositoryTree tree;

	/**
	 * Use this constructor to Open an existing report.
	 * 
	 * @param wysiwygPanelParent
	 * @param datas
	 */
	public FwrRepositoryDialog(WysiwygPanel wysiwygPanelParent, TreeParentDTO datas) {
		super(LabelsConstants.lblCnst.Repository(), false, true);
		this.wysiwygPanelParent = wysiwygPanelParent;
		
		buildContent(datas, true);
	}

	/**
	 * Use this constructor to Save the report you are working on.
	 * 
	 * @param viewer
	 * @param datas
	 */
	public FwrRepositoryDialog(Viewer viewer, TreeParentDTO datas) {
		super(LabelsConstants.lblCnst.Repository(), false, true);
		this.viewer = viewer;
		
		buildContent(datas, false);
	}
	
	private void buildContent(TreeParentDTO datas, boolean load) {
		setWidget(uiBinder.createAndBindUi(this));

		tree = new FwrRepositoryTree(datas);

		VerticalPanel main = new VerticalPanel();
		main.setSpacing(10);
		main.setBorderWidth(0);

		Label lblRepositoryContent = new Label(LabelsConstants.lblCnst.RepositoryContent());
		lblRepositoryContent.addStyleName(style.repositoryContent());
		
		VerticalPanel header = new VerticalPanel();
		header.setPixelSize(580, 50);
		header.setSpacing(5);
		header.add(lblRepositoryContent);

		createButton(Bpm_fwr.LBLW.Cancel(), closeClickHandler);
		if (load) {
			header.add(new HTML("&nbsp;&nbsp;&nbsp;&nbsp;  " + LabelsConstants.lblCnst.LoadFromRepository()));

			createButton(Bpm_fwr.LBLW.BtnOpen(), openClickHandler);
		}
		else {
			header.add(new HTML("&nbsp;&nbsp;&nbsp;&nbsp;  " + LabelsConstants.lblCnst.ExportUpdateRepository()));

			createButton(Bpm_fwr.LBLW.save(), saveClickHandler);
		}


		middle = new VerticalPanel();

		main.add(header);
		main.add(middle);
		main.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		main.setCellHorizontalAlignment(middle, HorizontalPanel.ALIGN_CENTER);

		contentPanel.add(main);

		try {
			this.middle.remove(1);
		} catch (Exception e) { }
		ScrollPanel scrollPanel = new ScrollPanel(tree);
		scrollPanel.setSize("580px", "300px");
		scrollPanel.addStyleName(style.treePanel());
		this.middle.add(scrollPanel);
	}
	
	private ClickHandler closeClickHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			FwrRepositoryDialog.this.hide();
		}
	};

	private ClickHandler saveClickHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			ChooseSaveAsFwrOrBirtDialogBox dial = new ChooseSaveAsFwrOrBirtDialogBox();
			dial.addFinishListener(chooseSaveAsBirtFinishListener);
			dial.center();
		}
	};
	
	private ClickHandler openClickHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			Bpm_fwr.getInstance().showWaitPart(true);

			InfoUser infoUser = Bpm_fwr.getInstance().getInfoUser();

			FwrServiceMetadata.Connect.getInstance().loadReport(tree.getSelectedId(), infoUser.getGroup().getName(), new AsyncCallback<FWRReport>() {

				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();

					Bpm_fwr.getInstance().showWaitPart(false);
				}

				@Override
				public void onSuccess(FWRReport report) {
					Bpm_fwr.getInstance().showWaitPart(false);
					
					FwrRepositoryDialog.this.hide();

					report.getSaveOptions().setDirectoryItemid(tree.getSelectedId());
					wysiwygPanelParent.importReport(report);
				}
			});
		}
	};

	private FinishListener chooseSaveAsBirtFinishListener = new FinishListener() {

		@Override
		public void onFinish(Object saveAsBirt, Object source, String result1) {
			if (saveAsBirt instanceof Boolean) {
				if (tree.getSelectedType() == TypeItem.FOLDER) {
					FwrSaveDialog saveDialog = new FwrSaveDialog(viewer, tree.getSelectedId(), (Boolean) saveAsBirt);
					saveDialog.addCloseHandler(closeHandler);
					saveDialog.center();
				}
			}
		}
	};
	
	private CloseHandler<PopupPanel> closeHandler = new CloseHandler<PopupPanel>() {

		@Override
		public void onClose(CloseEvent<PopupPanel> event) {
			FwrRepositoryDialog.this.hide();
		};
	};

}
