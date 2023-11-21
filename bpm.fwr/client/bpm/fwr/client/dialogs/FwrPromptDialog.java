package bpm.fwr.client.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.fwr.api.beans.dataset.FwrPrompt;
import bpm.fwr.api.beans.dataset.GroupPrompt;
import bpm.fwr.api.beans.dataset.IResource;
import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.services.FwrServiceMetadata;
import bpm.fwr.client.utils.CustomListBoxWithWait;
import bpm.fwr.client.utils.GroupComboParameter;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FwrPromptDialog extends AbstractDialogBox {

	private static FwrPromptDialogUiBinder uiBinder = GWT.create(FwrPromptDialogUiBinder.class);

	interface FwrPromptDialogUiBinder extends UiBinder<Widget, FwrPromptDialog> {
	}
	
	@UiField
	HTMLPanel contentPanel;
	
	private static final String CSS_PROMPT_NAME = "promptName";
	private static final String CSS_PROMPT_NAME_FIRST = "promptNameFirst";
	private static final String CSS_LIST_BOX = "responseListBox";

	private List<IResource> resources;
	private HashMap<String, GroupComboParameter> groupComboBoxParam = new HashMap<String, GroupComboParameter>();

	public FwrPromptDialog(String title, List<IResource> resources) {
		super(title, false, true);
		this.resources = resources;
		
		setWidget(uiBinder.createAndBindUi(this));

		HorizontalPanel centerPanel = new HorizontalPanel();
		VerticalPanel labelPromptPanel = new VerticalPanel();
		VerticalPanel listPromptPanel = new VerticalPanel();

		boolean first = true;

		for (IResource resource : resources) {
			if (resource instanceof FwrPrompt) {
				final FwrPrompt prompt = (FwrPrompt) resource;
				buildPrompt(labelPromptPanel, listPromptPanel, prompt, first);
			}
			else if (resource instanceof GroupPrompt) {
				GroupPrompt grp = (GroupPrompt) resource;
				buildPrompt(labelPromptPanel, listPromptPanel, grp.getCascadingPrompts(), first);
			}
		}
		centerPanel.add(labelPromptPanel);
		centerPanel.add(listPromptPanel);

		contentPanel.add(centerPanel);
		
		createButtonBar(Bpm_fwr.LBLW.Ok(), okHandler, Bpm_fwr.LBLW.Cancel(), cancelHandler);
	}

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			finish(resources, FwrPromptDialog.this, null);
			FwrPromptDialog.this.hide();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			finish(null, null, null);
			FwrPromptDialog.this.hide();
		}
	};

	private void buildPrompt(VerticalPanel labelPromptPanel, VerticalPanel listPromptPanel, final FwrPrompt prompt, boolean first) {
		String promptTitle = prompt.getName();
		Label lblText = new Label(promptTitle + " (" + prompt.getName() + ")");
		if (first) {
			lblText.addStyleName(CSS_PROMPT_NAME_FIRST);
			first = false;
		}
		else {
			lblText.addStyleName(CSS_PROMPT_NAME);
		}

		labelPromptPanel.add(new HTML("<b>" + lblText + "</b>"));

		if (prompt.getType() == VanillaParameter.LIST_BOX) {
			ListBox listBox = null;
			if(prompt.getParamType().equals(VanillaParameter.PARAM_TYPE_MULTI)) {
				listBox = new ListBox(true);
				listBox.setVisibleItemCount(7);
			}
			else {
				listBox = new ListBox();
			}
			listBox.addStyleName(CSS_LIST_BOX);
			listBox.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					ListBox listTemp = (ListBox) event.getSource();
					List<String> vals = new ArrayList<String>();
					for (int i = 0; i < listTemp.getItemCount(); i++) {
						if (listTemp.isItemSelected(i)) {
							vals.add(listTemp.getValue(i));
						}
					}
					prompt.setSelectedValues(vals);
				}
			});

			final ListBox lstTemp = listBox;
			
			FwrServiceMetadata.Connect.getInstance().getPromptList(prompt, prompt.getMetadataId(), prompt.getPackageParent(), prompt.getModelParent(), new AsyncCallback<List<String>>() {

				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}

				@Override
				public void onSuccess(List<String> result) {
					if (result != null && !result.isEmpty()) {
						for (String str : result) {
							lstTemp.addItem(str);
						}
						prompt.initSelectedValues(result.get(0));
						lstTemp.setSelectedIndex(0);
					}
				}

			});

			listPromptPanel.add(listBox);
		}
		else if (prompt.getType() == VanillaParameter.TEXT_BOX) {
			final TextBox txtResponse = new TextBox();
			txtResponse.addStyleName(CSS_LIST_BOX);
			txtResponse.addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					String response = txtResponse.getText();
					prompt.initSelectedValues(response);
				}
			});
			listPromptPanel.add(txtResponse);
		}
	}

	private void buildPrompt(VerticalPanel labelPromptPanel, VerticalPanel listPromptPanel, final List<FwrPrompt> prompts, boolean first) {
		boolean firstLoad = true;
		List<CustomListBoxWithWait> listComboBox = new ArrayList<CustomListBoxWithWait>();

		for (final FwrPrompt prompt : prompts) {
			String promptTitle = prompt.getName();
			Label lblText = new Label(promptTitle + " (" + prompt.getName() + ")");
			if (first) {
				lblText.addStyleName(CSS_PROMPT_NAME_FIRST);
				first = false;
			}
			else {
				lblText.addStyleName(CSS_PROMPT_NAME);
			}

			labelPromptPanel.add(new HTML("<b>" + lblText + "</b>"));

			if (prompt.getType() == VanillaParameter.LIST_BOX) {
				ListBox cbox = null;
				if(prompt.getParamType().equals(VanillaParameter.PARAM_TYPE_MULTI)) {
					cbox = new ListBox(true);
					cbox.setVisibleItemCount(7);
				}
				else {
					cbox = new ListBox();
				}
				cbox.setName(prompt.getName());
				cbox.addStyleName(CSS_LIST_BOX);
				cbox.addChangeHandler(new ChangeHandler() {

					@Override
					public void onChange(ChangeEvent event) {
						ListBox listTemp = (ListBox) event.getSource();
						List<String> vals = new ArrayList<String>();
						for (int i = 0; i < listTemp.getItemCount(); i++) {
							if (listTemp.isItemSelected(i)) {
								vals.add(listTemp.getValue(i));
							}
						}
						prompt.setSelectedValues(vals);

						String selectedParam = listTemp.getName();
						GroupComboParameter groupComboParameter = groupComboBoxParam.get(selectedParam);

						if (!groupComboParameter.isLast()) {
							groupComboParameter.getChildBox().showWait(true);

							fillCascadingParam(groupComboBoxParam.get(listTemp.getName()).getChildBox(), prompts);
						}
					}
				});

				CustomListBoxWithWait waitBox = new CustomListBoxWithWait(cbox, prompt);

				if (firstLoad) {
					firstLoad = false;
					
					final ListBox lstTemp = cbox;
					FwrServiceMetadata.Connect.getInstance().getPromptList(prompt, prompt.getMetadataId(), prompt.getPackageParent(), prompt.getModelParent(), new AsyncCallback<List<String>>() {

						@Override
						public void onFailure(Throwable caught) {
							caught.printStackTrace();
						}

						@Override
						public void onSuccess(List<String> result) {
							if (result != null && !result.isEmpty()) {
								for (String str : result) {
									lstTemp.addItem(str);
								}
								prompt.initSelectedValues(result.get(0));
								lstTemp.setSelectedIndex(0);
								
								NativeEvent event = Document.get().createChangeEvent();
								ChangeEvent.fireNativeEvent(event, groupComboBoxParam.get(lstTemp.getName()).getActualBox().getListBox());
							}
						}

					});
				}

				listPromptPanel.add(waitBox);
				listComboBox.add(waitBox);
			}
			else if (prompt.getType() == VanillaParameter.TEXT_BOX) {
				final TextBox txtResponse = new TextBox();
				txtResponse.addStyleName(CSS_LIST_BOX);
				txtResponse.addChangeHandler(new ChangeHandler() {
					
					@Override
					public void onChange(ChangeEvent event) {
						String response = txtResponse.getText();
						prompt.initSelectedValues(response);
					}
				});
				listPromptPanel.add(txtResponse);
			}
		}

		if (listComboBox.size() > 0) {
			for (int i = 0; i < listComboBox.size() - 1; i++) {
				GroupComboParameter birtGroupCombo = new GroupComboParameter(listComboBox.get(i).getName(), listComboBox.get(i), listComboBox.get(i + 1), false);
				groupComboBoxParam.put(listComboBox.get(i).getName(), birtGroupCombo);
			}
			GroupComboParameter birtGroupCombo = new GroupComboParameter(listComboBox.get(listComboBox.size() - 1).getName(), listComboBox.get(listComboBox.size() - 1), null, true);
			groupComboBoxParam.put(listComboBox.get(listComboBox.size() - 1).getName(), birtGroupCombo);
		}
	}

	private void fillCascadingParam(final CustomListBoxWithWait childBox, List<FwrPrompt> prompts) {
		final FwrPrompt prompt = childBox.getPrompt();
		
		FwrServiceMetadata.Connect.getInstance().getCascadingPromptList(prompts, prompt, new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(List<String> result) {
				if (result != null && !result.isEmpty()) {
					childBox.getListBox().clear();
					for (String str : result) {
						childBox.getListBox().addItem(str);
					}
					prompt.initSelectedValues(result.get(0));
					childBox.getListBox().setSelectedIndex(0);
					
					NativeEvent event = Document.get().createChangeEvent();
					ChangeEvent.fireNativeEvent(event, groupComboBoxParam.get(childBox.getListBox().getName()).getActualBox().getListBox());
					
					childBox.showWait(false);
				}
			}

		});
	}
}
