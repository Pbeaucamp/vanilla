package bpm.fwr.client.dialogs;

import java.util.List;

import bpm.fwr.api.beans.SaveOptions;
import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.panels.Viewer;
import bpm.fwr.client.services.FwrServiceConnection;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.vanilla.platform.core.beans.Group;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FwrSaveDialog extends AbstractDialogBox {
	
	private static FwrSaveDialogUiBinder uiBinder = GWT.create(FwrSaveDialogUiBinder.class);

	interface FwrSaveDialogUiBinder extends UiBinder<Widget, FwrSaveDialog> {
	}
	
	@UiField
	HTMLPanel contentPanel;

	private FlexTable tab;
	
	private TextBox name;
	private TextBox comment;
	private TextBox internalVersion;
	private TextBox publicVersion;
	private TextBox maxHisto;

	private ListBox groupslist;
	private CheckBox privateItem;


	private Viewer viewer;
	private int selectedDirectoryId;
	private boolean saveAsRptDesign;


	private List<Group> groups;

	public FwrSaveDialog(Viewer viewer, int selectedDirectoryId, boolean saveAsRptDesign) {
		super(Bpm_fwr.LBLW.save(), false, true);
		this.viewer = viewer;
		this.selectedDirectoryId = selectedDirectoryId;
		this.saveAsRptDesign = saveAsRptDesign;
		
		setWidget(uiBinder.createAndBindUi(this));
		
		createButtonBar(Bpm_fwr.LBLW.Ok(), okHandler, Bpm_fwr.LBLW.Cancel(), cancelHandler);

		name = new TextBox();
		name.setWidth("150px");

		comment = new TextBox();
		comment.setWidth("150px");

		internalVersion = new TextBox();
		internalVersion.setWidth("150px");

		publicVersion = new TextBox();
		publicVersion.setWidth("150px");

		maxHisto = new TextBox();
		maxHisto.setWidth("150px");
		maxHisto.setText("5");

		groupslist = new ListBox();
		groupslist.setWidth("150px");

		privateItem = new CheckBox(Bpm_fwr.LBLW.PrivateItem());

		tab = new FlexTable();
		tab.setCellPadding(5);
		tab.setCellSpacing(0);
		tab.setText(0, 0, Bpm_fwr.LBLW.Name());
		tab.setWidget(0, 1, name);
		tab.setText(1, 0, Bpm_fwr.LBLW.Comment());
		tab.setWidget(1, 1, comment);
		tab.setText(2, 0, Bpm_fwr.LBLW.Internal());
		tab.setWidget(2, 1, internalVersion);
		tab.setText(3, 0, Bpm_fwr.LBLW.Public());
		tab.setWidget(3, 1, publicVersion);
		tab.setText(4, 0, Bpm_fwr.LBLW.Histo());
		tab.setWidget(4, 1, maxHisto);
		tab.setWidget(5, 0, privateItem);
		tab.setText(6, 0, Bpm_fwr.LBLW.Group());
		tab.setWidget(6, 1, groupslist);

		FlexCellFormatter format = tab.getFlexCellFormatter();
		format.setWidth(0, 0, "150px");
		format.setWidth(0, 1, "180px");
		format.setWidth(1, 0, "150px");
		format.setWidth(1, 1, "180px");
		format.setWidth(2, 0, "150px");
		format.setWidth(2, 1, "180px");
		format.setWidth(3, 0, "150px");
		format.setWidth(3, 1, "180px");
		format.setWidth(4, 0, "150px");
		format.setWidth(4, 1, "180px");
		format.setColSpan(5, 0, 2);
		format.setHorizontalAlignment(5, 0, HorizontalPanel.ALIGN_LEFT);
		format.setWidth(6, 0, "150px");
		format.setWidth(6, 1, "180px");

		VerticalPanel main = new VerticalPanel();
		main.setSpacing(10);
		main.setBorderWidth(0);
		main.add(tab);

		contentPanel.add(main);

		getGroups();
	}

	private void getGroups() {
		FwrServiceConnection.Connect.getInstance().getGroupsService(new AsyncCallback<List<Group>>() {

			@Override
			public void onFailure(Throwable arg0) {
				arg0.printStackTrace();
			}

			@Override
			public void onSuccess(List<Group> groups) {
				FwrSaveDialog.this.groups = groups;

				groupslist.clear();
				if (groups != null) {
					for (Group gr : groups) {
						groupslist.addItem(gr.getName(), String.valueOf(gr.getId()));
					}
				}
			}
		});
	}

	private ClickHandler okHandler = new ClickHandler() {

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
				MessageHelper.openMessageDialog(Bpm_fwr.LBLW.Error(), "You need to select a valid group.");
			}

			String n = name.getText();
			String com = comment.getText();
			String internalVers = internalVersion.getText();
			String publicVers = publicVersion.getText();
			String group = selectedGroup != null ? selectedGroup.getName() : "";
			boolean priv = privateItem.getValue();
			int dirId = selectedDirectoryId;

			// add save options
			SaveOptions o = new SaveOptions();
			o.setName(n);
			o.setComment(com);
			o.setInternalVersion(internalVers);
			o.setPublicVerson(publicVers);
			o.setGroup(group);
			o.setPrivateItem(priv);
			o.setDirectoryId(dirId);

			if (saveAsRptDesign) {
				viewer.saveWysiwygReportAsBirtReport(o);
			}
			else {
				viewer.saveWysiwygReport(false, o);
			}

			FwrSaveDialog.this.hide();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			FwrSaveDialog.this.hide();
		}
	};
}
