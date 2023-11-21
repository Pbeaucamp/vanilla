package bpm.gwt.commons.client.dialog;

import java.io.Serializable;
import java.util.List;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.SaveItemDialog.IRepositoryManager;
import bpm.gwt.commons.client.tree.RepositoryTree;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class RepositoryDirectoryDialog extends AbstractDialogBox {

	private static ExportToRepositoryDialogUiBinder uiBinder = GWT.create(ExportToRepositoryDialogUiBinder.class);

	interface ExportToRepositoryDialogUiBinder extends UiBinder<Widget, RepositoryDirectoryDialog> {
	}

	interface MyStyle extends CssResource {
		String tree();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel contentPanel;

	@UiField
	SimplePanel treePanel;

	private IRepositoryManager manager;
	private RepositoryTree repositoryTree;

	private Button confirmButton;
	private Boolean confirm = false;

	private int repositoryType;
	private int subRepositoryType = -1;
	private List<Group> availableGroups;
	private RepositoryItem newItem;
	private Serializable itemToSave;
	
	/* kmo 03/2016 connexion distance */
	private String url; 
	private String login; 
	private String pass; 
	private Group group; 
	private Repository repo;

	public RepositoryDirectoryDialog(final int repositoryType, List<Group> availableGroups, Serializable itemToSave) {
		super(LabelsConstants.lblCnst.ExportToRepository(), false, true);
		this.repositoryType = repositoryType;
		this.availableGroups = availableGroups;
		this.itemToSave = itemToSave;

		setWidget(uiBinder.createAndBindUi(this));

		createButton(LabelsConstants.lblCnst.Cancel(), cancelHandler);
		confirmButton = createButton(LabelsConstants.lblCnst.Confirmation(), confirmHandler);

		repositoryTree = new RepositoryTree(repositoryHandler);
		repositoryTree.addStyleName(style.tree());
		treePanel.add(repositoryTree);

		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {
				loadTree(repositoryType);
			}
		});
	}

	public RepositoryDirectoryDialog(IRepositoryManager manager, final int repositoryType, List<Group> availableGroups, Serializable itemToSave) {
		this(repositoryType, availableGroups, itemToSave);
		this.manager = manager;
	}
	
	public RepositoryDirectoryDialog(final int repositoryType, int subRepositoryType, List<Group> availableGroups, Serializable itemToSave) {
		super(LabelsConstants.lblCnst.ExportToRepository(), false, true);
		this.repositoryType = repositoryType;
		this.subRepositoryType = subRepositoryType;
		this.availableGroups = availableGroups;
		this.itemToSave = itemToSave;

		setWidget(uiBinder.createAndBindUi(this));

		createButton(LabelsConstants.lblCnst.Cancel(), cancelHandler);
		confirmButton = createButton(LabelsConstants.lblCnst.Confirmation(), confirmHandler);

		repositoryTree = new RepositoryTree(repositoryHandler);
		repositoryTree.addStyleName(style.tree());
		treePanel.add(repositoryTree);

		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {
				loadTree(repositoryType);
			}
		});
	}

	public RepositoryDirectoryDialog(final String url, final String login, final String pass, final Group group, final Repository repo, final int repositoryType, int subRepositoryType, List<Group> availableGroups, Serializable itemToSave) {
		super(LabelsConstants.lblCnst.ExportToRepository(), false, true);
		this.repositoryType = repositoryType;
		this.subRepositoryType = subRepositoryType;
		this.availableGroups = availableGroups;
		this.itemToSave = itemToSave;
		
		this.url = url;
		this.login = login;
		this.pass = pass;
		this.group = group;
		this.repo = repo;

		setWidget(uiBinder.createAndBindUi(this));

		createButton(LabelsConstants.lblCnst.Cancel(), cancelHandler);
		confirmButton = createButton(LabelsConstants.lblCnst.Confirmation(), confirmHandler);

		repositoryTree = new RepositoryTree(repositoryHandler);
		repositoryTree.addStyleName(style.tree());
		treePanel.add(repositoryTree);

		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {
				loadTree(url, login, pass, group, repo, repositoryType);
			}
		});
	}
	
	public void loadTree(int type) {
		repositoryTree.loadTree(type);
	}
	
	public void loadTree(String url, String login, String pass, Group group, Repository repo, int type) {
		repositoryTree.loadTree(url, login, pass, group, repo, type);
	}

	private SelectionHandler<TreeItem> repositoryHandler = new SelectionHandler<TreeItem>() {

		@Override
		public void onSelection(SelectionEvent<TreeItem> event) {
			if (repositoryTree.getSelectedDirectory() != null) {
				confirmButton.setEnabled(true);
			}
			else {
				confirmButton.setEnabled(false);
			}
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if (repositoryTree.getSelectedDirectory() == null) {
				return;
			}
			final SaveItemDialog dial;
			if(url == null){
				dial = new SaveItemDialog(manager, repositoryType, subRepositoryType, repositoryTree.getSelectedDirectory(), availableGroups, itemToSave);
			} else {
				dial = new SaveItemDialog(url, login, pass, group, repo, repositoryType, subRepositoryType, repositoryTree.getSelectedDirectory(), availableGroups, itemToSave);
			}
			
			dial.center();
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {
				
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					if (dial.isConfirm()) {
						hide();
					}
				}
			});
		}
	};

	public boolean getConfirm() {
		return confirm;
	}

	public RepositoryItem getSelectedDirectory() {
		return newItem;
	}

	public SimplePanel getTreePanel() {
		return treePanel;
	}

	public void setTreePanel(SimplePanel treePanel) {
		this.treePanel = treePanel;
	}
}
