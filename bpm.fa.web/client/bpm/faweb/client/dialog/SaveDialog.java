package bpm.faweb.client.dialog;

import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.client.tree.FaWebRepositoryTree;
import bpm.faweb.shared.ChartParameters;
import bpm.faweb.shared.MapOptions;
import bpm.faweb.shared.TreeParentDTO;
import bpm.faweb.shared.infoscube.Calcul;
import bpm.faweb.shared.infoscube.ItemView;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.LoginService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.vanilla.platform.core.beans.Group;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SaveDialog extends AbstractDialogBox {

	private static SaveDialogUiBinder uiBinder = GWT.create(SaveDialogUiBinder.class);

	interface SaveDialogUiBinder extends UiBinder<Widget, SaveDialog> {
	}

	@UiField
	HTMLPanel contentPanel;
	
	private MainPanel mainCompParent;

	private VerticalPanel main = new VerticalPanel();

	private TextBox name = new TextBox();
	private TextBox comment = new TextBox();
	private TextBox internalVersion = new TextBox();
	private TextBox publicVersion = new TextBox();

	private int directoryId;

	private FlexTable tab = new FlexTable();

	private ListBox groupslist = new ListBox();

	public static boolean fromSave2 = false;

	private List<Group> groups;

	public SaveDialog(final MainPanel mainCompParent, final RepositoryContentDialog repoParent, final List<ItemView> viewToSave, final boolean isProjection) {
		super(FreeAnalysisWeb.LBL.Save(), false, true);
		this.mainCompParent = mainCompParent;
		
		setWidget(uiBinder.createAndBindUi(this));

		contentPanel.add(main);
		main.setSpacing(10);
		main.setBorderWidth(0);

		tab.setCellPadding(10);
		tab.setCellSpacing(10);

		FlexCellFormatter format = tab.getFlexCellFormatter();

		tab.setText(0, 0, FreeAnalysisWeb.LBL.Name());
		format.setWidth(0, 0, "150px");
		name.setWidth("150px");
		tab.setWidget(0, 1, name);
		format.setWidth(0, 1, "180px");

		tab.setText(1, 0, FreeAnalysisWeb.LBL.Comment());
		format.setWidth(1, 0, "150px");
		comment.setWidth("150px");
		tab.setWidget(1, 1, comment);
		format.setWidth(1, 1, "180px");

		tab.setText(2, 0, FreeAnalysisWeb.LBL.Internal());
		format.setWidth(2, 0, "150px");
		internalVersion.setWidth("150px");
		tab.setWidget(2, 1, internalVersion);
		format.setWidth(2, 1, "180px");

		tab.setText(3, 0, FreeAnalysisWeb.LBL.Public());
		format.setWidth(3, 0, "150px");
		publicVersion.setWidth("150px");
		tab.setWidget(3, 1, publicVersion);
		format.setWidth(3, 1, "180px");

		tab.setText(6, 0, FreeAnalysisWeb.LBL.Group());
		format.setWidth(6, 0, "150px");
		groupslist.setWidth("150px");
		tab.setWidget(6, 1, groupslist);
		format.setWidth(6, 1, "180px");

		getGroups();

		main.add(tab);

		if (isProjection) {
			name.setText(mainCompParent.getActualProjection().getName());
			comment.setText(mainCompParent.getActualProjection().getComment());
			name.setEnabled(false);
			comment.setEnabled(false);
		}

		createButton(FreeAnalysisWeb.LBL.Cancel(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				SaveDialog.this.hide();
			}
		});

		createButton(FreeAnalysisWeb.LBL.Apply(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Group selectedGroup = null;
				try {
					int selectedGroupId = Integer.parseInt(groupslist.getValue(groupslist.getSelectedIndex()));
					if (groups != null) {
						for (Group gr : groups) {
							if (gr.getId() == selectedGroupId) {
								selectedGroup = gr;
								break;
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					MessageHelper.openMessageDialog(FreeAnalysisWeb.LBL.Error(), "You need to select a valid group.");
					
					return;
				}

				String n = name.getText();
				String c = comment.getText();
				String i = internalVersion.getText();
				String p = publicVersion.getText();

				String group = selectedGroup != null ? selectedGroup.getName() : "";
				List<Calcul> calculs = mainCompParent.getCalculs();

				if (viewToSave != null) {
					mainCompParent.showWaitPart(true);
					
					FaWebService.Connect.getInstance().saveDashboard(mainCompParent.getKeySession(), viewToSave, n, group, c, p, i, directoryId, new AsyncCallback<String>() {
						public void onFailure(Throwable e) {
							mainCompParent.showWaitPart(false);
							
							e.printStackTrace();

							showFinishSaveDialog(e.getMessage(), false);
						}

						public void onSuccess(String result) {
							mainCompParent.showWaitPart(false);
							
							if (result.equalsIgnoreCase("success")) {
								showFinishSaveDialog(FreeAnalysisWeb.LBL.DashboardSaveWithSuccess(), true);

								refreshTree(repoParent);
								repoParent.showWaitPart(false);
							}
							else {
								showFinishSaveDialog(FreeAnalysisWeb.LBL.save_failed(), false);
							}
						}

					});
				}
				else if (isProjection) {
					mainCompParent.showWaitPart(true);
					
					FaWebService.Connect.getInstance().saveProjection(mainCompParent.getKeySession(), mainCompParent.getActualProjection(), n, c, i, p, group, directoryId, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							caught.printStackTrace();
							
							mainCompParent.showWaitPart(false);
						}

						@Override
						public void onSuccess(Void result) {
							mainCompParent.showWaitPart(false);
							showFinishSaveDialog(FreeAnalysisWeb.LBL.ProjectionSaveOk(), true);
						}
					});
				}
				else {
					ChartParameters chartParams = null;
					if (mainCompParent.getDisplayPanel().getChartTab() != null) {
						chartParams = mainCompParent.getDisplayPanel().getChartTab().getChartParameters();
					}
					
					MapOptions mapOptions = null;
					if (mainCompParent.getDisplayPanel().getMapTab() != null && mainCompParent.getDisplayPanel().getMapTab().getMapOptions() != null) {
						mapOptions = mainCompParent.getDisplayPanel().getMapTab().getMapOptions();
					}

					mainCompParent.showWaitPart(true);
					FaWebService.Connect.getInstance().saveService(mainCompParent.getKeySession(), n, group, chartParams, calculs, directoryId, c, i, p, mainCompParent.getDisplayPanel().getGridHtml(), mainCompParent.getInfosReport(), mapOptions, new AsyncCallback<Integer>() {
						public void onFailure(Throwable e) {
							e.printStackTrace();

							showFinishSaveDialog(e.getMessage(), false);
							mainCompParent.showWaitPart(false);
						}

						public void onSuccess(Integer result) {
							if (result != -1) {
								showFinishSaveDialog(FreeAnalysisWeb.LBL.SaveOk(), true);
							}
							else {
								showFinishSaveDialog(FreeAnalysisWeb.LBL.save_failed(), false);
							}
							mainCompParent.showWaitPart(false);
						}
					});
				}

				SaveDialog.this.hide();
			}
		});
	}

	private void showFinishSaveDialog(String result, boolean success) {
		FinishSaveDialog dial = new FinishSaveDialog(result, success);
		dial.center();
	}

	public void refreshTree(final RepositoryContentDialog p) {
		FaWebService.Connect.getInstance().getRepositories(mainCompParent.getKeySession(), FaWebService.FAV, new AsyncCallback<TreeParentDTO>() {

			public void onFailure(Throwable ex) {
				ex.printStackTrace();
			}

			public void onSuccess(TreeParentDTO result) {
				if (result != null) {
					p.setTree(new FaWebRepositoryTree(mainCompParent, p.getImages(), result));
				}
			}
		});

	}

	private void getGroups() {
		LoginService.Connect.getInstance().getAvailableGroups(new AsyncCallback<List<Group>>() {

			@Override
			public void onFailure(Throwable arg0) {
				arg0.printStackTrace();
			}

			@Override
			public void onSuccess(List<Group> groups) {
				SaveDialog.this.groups = groups;

				groupslist.clear();
				if (groups != null) {
					for (Group gr : groups) {
						groupslist.addItem(gr.getName(), String.valueOf(gr.getId()));
					}
				}
			}
		});

	}

	public TextBox getName() {
		return name;
	}

	public void setName(TextBox name) {
		this.name = name;
	}

	public TextBox getInternalVersion() {
		return internalVersion;
	}

	public void setInternalVersion(TextBox internalVersion) {
		this.internalVersion = internalVersion;
	}

	public TextBox getPublicVersion() {
		return publicVersion;
	}

	public void setPublicVersion(TextBox publicVersion) {
		this.publicVersion = publicVersion;
	}

	public TextBox getComment() {
		return comment;
	}

	public void setComment(TextBox comment) {
		this.comment = comment;
	}

	public int getDirectoryId() {
		return directoryId;
	}

	public void setDirectoryId(int directoryId) {
		this.directoryId = directoryId;
	}
}
