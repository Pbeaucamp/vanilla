package bpm.gwt.aklabox.commons.client.panels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import bpm.document.management.core.model.LOV;
import bpm.document.management.core.model.SourceConnection;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.observerpattern.Observer;
import bpm.gwt.aklabox.commons.client.services.AklaCommonService;
import bpm.gwt.aklabox.commons.client.utils.ChildDialogComposite;
import bpm.gwt.aklabox.commons.client.utils.DefaultDialog;
import bpm.gwt.aklabox.commons.client.utils.DefaultResultDialog;
import bpm.gwt.aklabox.commons.client.utils.MessageDialog;
import bpm.gwt.aklabox.commons.client.utils.WaitDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.Widget;

public class LovPanel extends ChildDialogComposite implements Observer {

	private static LovPanelUiBinder uiBinder = GWT.create(LovPanelUiBinder.class);

	interface LovPanelUiBinder extends UiBinder<Widget, LovPanel> {
	}

	@UiField
	TextBox txtName, txtLovFilter;
	@UiField
	SimplePanel lstSource, lstTable, lstItemCode, lstItemName, lstParent, lstParentItem;
	@UiField
	HTMLPanel contentPanel, sortPanel;
	@UiField
	Label lblName, lblSource, lblTable, lblItemCode, lblItemName;
	@UiField
	ListBox lstLovFilter;
	@UiField
	Image imgAddSource;
	@UiField
	Button btnSave, btnCancel;

	interface MyStyle extends CssResource {
		String lst();

		String lovbtn();
	}

	@UiField
	MyStyle style;

	private ValueListBox<SourceConnection> sources = new ValueListBox<SourceConnection>(new Renderer<SourceConnection>() {
		@Override
		public String render(SourceConnection object) {
			return object != null ? object.getName() : "";
		}

		@Override
		public void render(SourceConnection object, Appendable appendable) throws IOException {
			appendable.append(render(object));
		}
	});

	private ValueListBox<String> tables = new ValueListBox<String>(new Renderer<String>() {
		@Override
		public String render(String object) {
			return object != null ? object : "";
		}

		@Override
		public void render(String object, Appendable appendable) throws IOException {
			appendable.append(render(object));
		}
	});

	private ValueListBox<String> itemCodes = new ValueListBox<String>(new Renderer<String>() {
		@Override
		public String render(String object) {
			return object != null ? object : "";
		}

		@Override
		public void render(String object, Appendable appendable) throws IOException {
			appendable.append(render(object));
		}
	});

	private ValueListBox<String> itemNames = new ValueListBox<String>(new Renderer<String>() {
		@Override
		public String render(String object) {
			return object != null ? object : "";
		}

		@Override
		public void render(String object, Appendable appendable) throws IOException {
			appendable.append(render(object));
		}
	});
	
	private ValueListBox<LOV> parentList = new ValueListBox<LOV>(new Renderer<LOV>() {
		@Override
		public String render(LOV object) {
			return object != null ? object.getValueName() : "";
		}

		@Override
		public void render(LOV object, Appendable appendable) throws IOException {
			appendable.append(render(object));
		}
	});
	
	private ValueListBox<String> itemParent = new ValueListBox<String>(new Renderer<String>() {
		@Override
		public String render(String object) {
			return object != null ? object : "";
		}

		@Override
		public void render(String object, Appendable appendable) throws IOException {
			appendable.append(render(object));
		}
	});

	protected List<LOV> listLov;
	private boolean isAscending;
	private List<SourceConnection> listSources = new ArrayList<SourceConnection>();
	private LovItem lovEdit = null;

	public LovPanel() {
		initWidget(uiBinder.createAndBindUi(this));

		lstLovFilter.addItem(LabelsConstants.lblCnst.Name());
		lstLovFilter.addItem(LabelsConstants.lblCnst.Source());
		lstLovFilter.addItem(LabelsConstants.lblCnst.Table());
		lstLovFilter.addItem(LabelsConstants.lblCnst.ItemCode());
		lstLovFilter.addItem(LabelsConstants.lblCnst.ItemName());

		sources.addStyleName(style.lst());
		tables.addStyleName(style.lst());
		itemCodes.addStyleName(style.lst());
		itemNames.addStyleName(style.lst());
		parentList.addStyleName(style.lst());
		itemParent.addStyleName(style.lst());

		lstSource.add(sources);
		lstTable.add(tables);
		lstItemCode.add(itemCodes);
		lstItemName.add(itemNames);
		lstParent.add(parentList);
		lstParentItem.add(itemParent);
		btnCancel.setVisible(false);

		getAllLovItems();

		initSourceList();

		sources.addValueChangeHandler(new ValueChangeHandler<SourceConnection>() {
			@Override
			public void onValueChange(ValueChangeEvent<SourceConnection> event) {
				WaitDialog.showWaitPart(true);
				AklaCommonService.Connect.getService().getTables(event.getValue(), new AsyncCallback<List<String>>() {

					@Override
					public void onFailure(Throwable caught) {
						new DefaultResultDialog(caught.getMessage(), "failure").show();
						tables.setAcceptableValues(new ArrayList<String>());
						WaitDialog.showWaitPart(false);
					}

					@Override
					public void onSuccess(List<String> result) {
						tables.setAcceptableValues(new HashSet<>(result));
						WaitDialog.showWaitPart(false);
					}
				});
			}
		});

		tables.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				WaitDialog.showWaitPart(true);
				AklaCommonService.Connect.getService().getColumns((SourceConnection) sources.getValue(), (String) event.getValue(), new AsyncCallback<List<String>>() {

					@Override
					public void onFailure(Throwable caught) {
						new DefaultResultDialog(caught.getMessage(), "failure").show();
						itemCodes.setAcceptableValues(new ArrayList<String>());
						itemNames.setAcceptableValues(new ArrayList<String>());
						itemParent.setAcceptableValues(new ArrayList<String>());
						WaitDialog.showWaitPart(false);
					}

					@Override
					public void onSuccess(List<String> result) {
						itemCodes.setValue(result.get(0));
						itemNames.setValue(result.get(0));
						itemParent.setValue(result.get(0));
						itemCodes.setAcceptableValues(result);
						itemNames.setAcceptableValues(result);
						itemParent.setAcceptableValues(result);
						WaitDialog.showWaitPart(false);
					}
				});
			}
		});

	}

	public void getAllLovItems() {
		WaitDialog.showWaitPart(true);
		AklaCommonService.Connect.getService().getAllLov(new AsyncCallback<List<LOV>>() {

			@Override
			public void onSuccess(List<LOV> result) {
				LovPanel.this.listLov = result;
				parentList.setAcceptableValues(result);
				contentPanel.clear();
				for (LOV lov : result) {
					contentPanel.add(new LovItem(lov, LovPanel.this));
				}
				WaitDialog.showWaitPart(false);
			}

			@Override
			public void onFailure(Throwable caught) {
				new DefaultResultDialog(caught.getMessage(), "failure").show();
				WaitDialog.showWaitPart(false);
			}
		});
	}

	public void initSourceList() {
		WaitDialog.showWaitPart(true);
		AklaCommonService.Connect.getService().getAllConnections(new AsyncCallback<List<SourceConnection>>() {

			@Override
			public void onFailure(Throwable caught) {
				new DefaultResultDialog(caught.getMessage(), "failure").show();
				WaitDialog.showWaitPart(false);
			}

			@Override
			public void onSuccess(List<SourceConnection> result) {
				listSources = result;
				sources.setAcceptableValues(result);
				WaitDialog.showWaitPart(false);
			}

		});
	}

	@UiHandler("imgAddSource")
	void onAddSource(ClickEvent e) {
		final CreateNewSource createSource = new CreateNewSource();
		DefaultDialog dial = new DefaultDialog(LabelsConstants.lblCnst.Source(), createSource, 600, 210, 10);

		// final InformationsDialog archiveOrNot = new
		// InformationsDialog(LabelConstants.lblCnst.PreArchivingTitle(),
		// LabelConstants.lblCnst.Ok(), LabelConstants.lblCnst.Cancel(),
		// LabelConstants.lblCnst.PreArchivingMessage(), true);

		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (createSource.isConfirm()) {
					listSources.add(createSource.getSource());
					sources.setAcceptableValues(listSources);
				}
			}
		});
		dial.center();
	}

	@UiHandler("lblName")
	void lblTableCode(ClickEvent e) {
		onSortLOV(0, lblName);
	}

	@UiHandler("lblSource")
	void lblSource(ClickEvent e) {
		onSortLOV(1, lblSource);
	}

	@UiHandler("lblTable")
	void lblTable(ClickEvent e) {
		onSortLOV(2, lblTable);
	}

	@UiHandler("lblItemCode")
	void lblItemCode(ClickEvent e) {
		onSortLOV(3, lblItemCode);
	}

	@UiHandler("lblItemName")
	void lblItemName(ClickEvent e) {
		onSortLOV(4, lblItemName);
	}

	private void onSortLOV(final int i, Label lblSort) {
		Collections.sort(listLov, new Comparator<LOV>() {
			@Override
			public int compare(LOV o1, LOV o2) {
				switch (i) {
				case 0:
					if (isAscending) {
						return o1.getValueName().compareTo(o2.getValueName());
					}
					else {
						return o2.getValueName().compareTo(o1.getValueName());
					}
				case 1:
					if (isAscending) {
						return o1.getSource().getName().compareTo(o2.getSource().getName());
					}
					else {
						return o2.getSource().getName().compareTo(o1.getSource().getName());
					}
				case 2:
					if (isAscending) {
						return o1.getTable().compareTo(o2.getTable());
					}
					else {
						return o2.getTable().compareTo(o1.getTable());
					}
				case 3:
					if (isAscending) {
						return o1.getItemCode().compareTo(o2.getItemCode());
					}
					else {
						return o2.getItemCode().compareTo(o1.getItemCode());
					}
				case 4:
					if (isAscending) {
						return o1.getItemLabel().compareTo(o2.getItemLabel());
					}
					else {
						return o2.getItemLabel().compareTo(o1.getItemLabel());
					}

				default:
					return 0;
				}
			}
		});
		sortLOV(listLov, lblSort);
	}

	private void sortLOV(List<LOV> result, Label lblSort) {
		this.listLov = result;
		contentPanel.clear();
		for (Widget w : sortPanel) {
			w.removeStyleName("asc");
			w.removeStyleName("desc");
		}
		if (isAscending) {
			lblSort.addStyleName("asc");
			lblSort.removeStyleName("desc");
			isAscending = false;
		}
		else {
			lblSort.addStyleName("desc");
			lblSort.removeStyleName("asc");
			isAscending = true;
		}
		for (LOV lov : result) {
			contentPanel.add(new LovItem(lov, LovPanel.this));
		}
	}

	@UiHandler("btnSave")
	void onSave(ClickEvent e) {
		if (txtName.getText() != null && !txtName.getText().isEmpty() && sources.getValue() != null && tables.getValue() != null && itemCodes.getValue() != null && itemNames.getValue() != null) {
			if (lovEdit == null) {
				WaitDialog.showWaitPart(true);
				
				LOV lov = new LOV(txtName.getText(), (String) tables.getValue(), (String) itemCodes.getValue(), (String) itemNames.getValue(), (SourceConnection) sources.getValue());
				try {
					lov.setItemParent(itemParent.getValue());
					lov.setParentListId(parentList.getValue().getId());
				} catch (Exception e1) {
				}
				AklaCommonService.Connect.getService().saveLOV(lov, new AsyncCallback<LOV>() {

					@Override
					public void onSuccess(LOV result) {
						new DefaultResultDialog("Successfully saved LOV", "success").show();
						WaitDialog.showWaitPart(false);
						contentPanel.add(new LovItem(result, LovPanel.this));
						// getAllLovItems();
					}

					@Override
					public void onFailure(Throwable caught) {
						new DefaultResultDialog(caught.getMessage(), "failure").show();
						WaitDialog.showWaitPart(false);
					}
				});
			}
			else {
				final LOV lov = lovEdit.getLov();
				lov.setValueName(txtName.getText());
				lov.setSource((SourceConnection) sources.getValue());
				lov.setTable((String) tables.getValue());
				lov.setItemCode((String) itemCodes.getValue());
				lov.setItemLabel((String) itemNames.getValue());
				lov.setItemParent(itemParent.getValue());
				lov.setParentListId(parentList.getValue().getId());

				AklaCommonService.Connect.getService().updateLOV(lov, new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						new DefaultResultDialog("Successfully saved LOV", "success").show();
						WaitDialog.showWaitPart(false);

						lovEdit.removeFromParent();
						contentPanel.add(new LovItem(lov, LovPanel.this));

						btnSave.removeStyleName(style.lovbtn());
						btnCancel.removeStyleName(style.lovbtn());

						btnCancel.setVisible(false);
						lovEdit = null;

						txtName.setText("");
						itemCodes.setAcceptableValues(new ArrayList<String>());
						itemCodes.setValue(null);
						itemNames.setAcceptableValues(new ArrayList<String>());
						itemNames.setValue(null);
						tables.setAcceptableValues(new ArrayList<String>());
						tables.setValue(null);
						sources.setValue(null);
					}

					@Override
					public void onFailure(Throwable caught) {
						new DefaultResultDialog(caught.getMessage(), "failure").show();
						WaitDialog.showWaitPart(false);
					}
				});
			}
		}
		else {
			MessageDialog message = new MessageDialog(LabelsConstants.lblCnst.FillFieldsErrorMes(), false);
			DefaultDialog defaultDialog = new DefaultDialog(LabelsConstants.lblCnst.FillFieldsErrorTitle(), message, 400, 210, 10);
			defaultDialog.center();
		}
	}

	@UiHandler("btnLovFilter")
	void btnLovFilter(ClickEvent e) {
		switch (lstLovFilter.getSelectedIndex()) {
		case 0:
			for (Widget w : contentPanel) {
				if (w instanceof LovItem) {
					LovItem item = (LovItem) w;
					if (!item.getLov().getValueName().toLowerCase().contains(txtLovFilter.getText().toLowerCase())) {
						item.setVisible(false);
					}
					else {
						item.setVisible(true);
					}
				}
			}
			break;
		case 1:
			for (Widget w : contentPanel) {
				if (w instanceof LovItem) {
					LovItem item = (LovItem) w;
					if (!item.getLov().getSource().getName().toLowerCase().contains(txtLovFilter.getText().toLowerCase())) {
						item.setVisible(false);
					}
					else {
						item.setVisible(true);
					}
				}
			}
			break;
		case 2:
			for (Widget w : contentPanel) {
				if (w instanceof LovItem) {
					LovItem item = (LovItem) w;
					if (!item.getLov().getTable().toLowerCase().contains(txtLovFilter.getText().toLowerCase())) {
						item.setVisible(false);
					}
					else {
						item.setVisible(true);
					}
				}
			}
			break;
		case 3:
			for (Widget w : contentPanel) {
				if (w instanceof LovItem) {
					LovItem item = (LovItem) w;
					if (!item.getLov().getItemCode().toLowerCase().contains(txtLovFilter.getText().toLowerCase())) {
						item.setVisible(false);
					}
					else {
						item.setVisible(true);
					}
				}
			}
			break;
		case 4:
			for (Widget w : contentPanel) {
				if (w instanceof LovItem) {
					LovItem item = (LovItem) w;
					if (!item.getLov().getItemLabel().toLowerCase().contains(txtLovFilter.getText().toLowerCase())) {
						item.setVisible(false);
					}
					else {
						item.setVisible(true);
					}
				}
			}
			break;

		default:
			break;
		}
	}

	public void deleteLov(final LovItem item) {
		WaitDialog.showWaitPart(true);
		AklaCommonService.Connect.getService().deleteLOV(item.getLov(), new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				new DefaultResultDialog(caught.getMessage(), "failure").show();
				WaitDialog.showWaitPart(false);
			}

			@Override
			public void onSuccess(Void result) {
				listLov.remove(result);
				contentPanel.remove(item);
				WaitDialog.showWaitPart(false);
			}

		});

	}

	public void editLov(final LovItem lovitem) {
		WaitDialog.showWaitPart(true);
		final LOV item = lovitem.getLov();
		txtName.setText(item.getValueName());

		for (SourceConnection source : listSources) {
			if (source.getConnectionId() == item.getSource().getConnectionId())
				sources.setValue(source);
		}

		AklaCommonService.Connect.getService().getTables(item.getSource(), new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				new DefaultResultDialog(caught.getMessage(), "failure").show();
				tables.setAcceptableValues(new ArrayList<String>());
				WaitDialog.showWaitPart(false);
			}

			@Override
			public void onSuccess(List<String> result) {
				tables.setValue(item.getTable());
				tables.setAcceptableValues(result);

				AklaCommonService.Connect.getService().getColumns(item.getSource(), item.getTable(), new AsyncCallback<List<String>>() {

					@Override
					public void onFailure(Throwable caught) {
						new DefaultResultDialog(caught.getMessage(), "failure").show();
						itemCodes.setAcceptableValues(new ArrayList<String>());
						itemNames.setAcceptableValues(new ArrayList<String>());
						WaitDialog.showWaitPart(false);
					}

					@Override
					public void onSuccess(List<String> result) {
						itemCodes.setValue(item.getItemCode());
						itemNames.setValue(item.getItemLabel());
						itemCodes.setAcceptableValues(result);
						itemNames.setAcceptableValues(result);
						lovEdit = lovitem;

						btnSave.addStyleName(style.lovbtn());
						btnCancel.addStyleName(style.lovbtn());

						btnCancel.setVisible(true);
						WaitDialog.showWaitPart(false);
					}
				});
			}
		});
	}

	@UiHandler("btnCancel")
	void onCancel(ClickEvent e) {
		lovEdit = null;

		txtName.setText("");
		itemCodes.setAcceptableValues(new ArrayList<String>());
		itemCodes.setValue(null);
		itemNames.setAcceptableValues(new ArrayList<String>());
		itemNames.setValue(null);
		tables.setAcceptableValues(new ArrayList<String>());
		tables.setValue(null);
		sources.setValue(null);

		btnSave.removeStyleName(style.lovbtn());
		btnCancel.removeStyleName(style.lovbtn());

		btnCancel.setVisible(false);
	}

	@Override
	public void notifyObserver() {
		// setColor();
	}

	// private void setColor(){
	// ThemeColorManager.setButtonColor(UserMain.getInstance().getUser().getSelectedTheme(),
	// btnSave);
	// ThemeColorManager.setButtonColor(UserMain.getInstance().getUser().getSelectedTheme(),
	// btnCancel);
	// }
}
