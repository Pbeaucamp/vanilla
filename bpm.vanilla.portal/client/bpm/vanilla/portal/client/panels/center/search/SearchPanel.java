package bpm.vanilla.portal.client.panels.center.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.repository.DocumentVersionDTO;
import bpm.vanilla.portal.client.panels.center.SearchManagerPanel;
import bpm.vanilla.portal.client.services.GedService;
import bpm.vanilla.portal.client.utils.ToolsGWT;
import bpm.vanilla.portal.shared.FieldDefinitionDTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class SearchPanel extends Composite {

	private static SearchPanelUiBinder uiBinder = GWT.create(SearchPanelUiBinder.class);

	interface SearchPanelUiBinder extends UiBinder<Widget, SearchPanel> {
	}

	@UiField
	TextBox txtSearch;

	@UiField
	RadioButton btnAllWords, btnNotAllWords;

	@UiField
	DisclosurePanel captionAdvancedSearch;

	private SearchManagerPanel parentPanel;
	private ComplexSearchPanel complexSearchPanel;

	private boolean isAnAdvancedSearch = false;

	public SearchPanel(SearchManagerPanel parentPanel) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parentPanel = parentPanel;
		
		complexSearchPanel = new ComplexSearchPanel();
		captionAdvancedSearch.setContent(complexSearchPanel);
		
		btnAllWords.setValue(true);
	}

	@UiHandler("captionAdvancedSearch")
	public void onOpenAdvancedClick(OpenEvent<DisclosurePanel> event) {
		isAnAdvancedSearch = true;

		txtSearch.setEnabled(false);
		btnAllWords.setEnabled(false);
		btnNotAllWords.setEnabled(false);
	}

	@UiHandler("captionAdvancedSearch")
	public void onCloseAdvancedClick(CloseEvent<DisclosurePanel> event) {
		isAnAdvancedSearch = false;

		txtSearch.setEnabled(true);
		btnAllWords.setEnabled(true);
		btnNotAllWords.setEnabled(true);
	}

	@UiHandler("btnSearch")
	public void onSearchClick(ClickEvent event) {
		parentPanel.showWaitPart(true);

		if (isAnAdvancedSearch) {
			HashMap<FieldDefinitionDTO, String> options = complexSearchPanel.getAdvancedSearchOption();
			
			boolean isInfoOk = true;
			for (Entry<FieldDefinitionDTO, String> opt : options.entrySet()) {
				if (opt.getValue() == null || opt.getValue().isEmpty()) {
					isInfoOk = false;
					break;
				}
			}

			if (isInfoOk) {
				GedService.Connect.getInstance().complexSearch(options, new String[0], true, new AsyncCallback<List<DocumentVersionDTO>>() {
					
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();

						parentPanel.showWaitPart(false);

						ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.Error());
					}

					@Override
					public void onSuccess(List<DocumentVersionDTO> result) {
						if (result != null) {
							parentPanel.loadResult(result);
						}
						else {
							parentPanel.loadResult(new ArrayList<DocumentVersionDTO>());
						}

						parentPanel.showWaitPart(false);
					}
				});
			}
			else {
				parentPanel.showWaitPart(false);

				MessageHelper.openMessageDialog(ToolsGWT.lblCnst.Information(), ToolsGWT.lblCnst.FillSearchBar());
			}
		}
		else {
			String queryString = txtSearch.getText();

			if (!queryString.isEmpty()) {
				String[] keywords = queryString.split(" ");

				GedService.Connect.getInstance().sampleSearch(keywords, btnAllWords.getValue(), new AsyncCallback<List<DocumentVersionDTO>>() {

					@Override
					public void onFailure(Throwable caught) {
						parentPanel.showWaitPart(false);

						caught.printStackTrace();

						ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.Error());
					}

					@Override
					public void onSuccess(List<DocumentVersionDTO> result) {
						if (result != null) {
							parentPanel.loadResult(result);
						}
						else {
							parentPanel.loadResult(new ArrayList<DocumentVersionDTO>());
						}

						parentPanel.showWaitPart(false);
					}
				});
			}
			else {
				parentPanel.showWaitPart(false);

				MessageHelper.openMessageDialog(ToolsGWT.lblCnst.Information(), ToolsGWT.lblCnst.FillSearchBar());
			}
		}
	}
}
