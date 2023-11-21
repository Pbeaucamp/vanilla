package bpm.vanilla.portal.client.panels.center.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.vanilla.portal.client.services.GedService;
import bpm.vanilla.portal.client.utils.ToolsGWT;
import bpm.vanilla.portal.shared.FieldDefinitionDTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ComplexSearchPanel extends Composite {

	private static ComplexSearchPanelUiBinder uiBinder = GWT.create(ComplexSearchPanelUiBinder.class);

	interface ComplexSearchPanelUiBinder extends UiBinder<Widget, ComplexSearchPanel> {
	}
	
	interface MyStyle extends CssResource {
		String searchPanel();
		String lblSearchPanel();
		String line();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel mainPanel;

	private List<FieldDefinitionDTO> fieldDefinitions;
	
	private List<String> operators = new ArrayList<String>();
	private List<LineSearch> lines = new ArrayList<LineSearch>();
	
	public ComplexSearchPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		
		operators.add(ToolsGWT.lblCnst.Contain());
		loadFields();
	}
	
	private void loadFields() {
		if (fieldDefinitions == null) {
			GedService.Connect.getInstance().getFieldDefinitions(new AsyncCallback<List<FieldDefinitionDTO>>() {
				
				public void onSuccess(List<FieldDefinitionDTO> result) {
					fieldDefinitions = result;
					addLine();
				}
				
				public void onFailure(Throwable caught) {
					caught.printStackTrace();

					ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.Error());
				}
			});
		}
		else {
			addLine();
		}
	}

	private void addLine() {
		LineSearch line = new LineSearch(this);
		this.lines.add(line);
		mainPanel.add(line);
	}
	
	private void removeLine(LineSearch line) {
		if(lines.size() > 1){
			lines.remove(line);
			line.removeFromParent();
		}
	}
	
	public HashMap<FieldDefinitionDTO, String> getAdvancedSearchOption(){
		HashMap<FieldDefinitionDTO, String> advancedSearchOption = new HashMap<FieldDefinitionDTO, String>();
		for(LineSearch l : lines){
			FieldDefinitionDTO fieldDef = l.getFieldDefinition();
			String txt = l.getText();
			
			advancedSearchOption.put(fieldDef, txt);
		}
		
		return advancedSearchOption;
	}

	private class LineSearch extends SimplePanel {
		
		private FlexTable table;
		
		private ListBox lField;
		private ListBox lOperator;
		
		private TextBox text;
		
		public LineSearch(final ComplexSearchPanel panelParent) {
			lField = new ListBox(false);
			if(fieldDefinitions != null){
				for(FieldDefinitionDTO field : fieldDefinitions){
					lField.addItem(field.getName(), String.valueOf(field.getId()));
				}
			}
			
			lOperator = new ListBox(false);
			if(operators != null){
				for(String op : operators){
					lOperator.addItem(op);
				}
			}
			
			text = new TextBox();
			text.setMaxLength(50);
			text.setStyleName(style.lblSearchPanel());
			
			Button btnAdd = new Button();
			btnAdd.setText("+");
			btnAdd.addClickHandler(new ClickHandler() {
				
				public void onClick(ClickEvent event) {
					panelParent.addLine();
				}
			});
			
			Button btnDel = new Button();
			btnDel.setText("-");
			btnDel.addClickHandler(new ClickHandler() {
				
				public void onClick(ClickEvent event) {
					panelParent.removeLine(LineSearch.this);
				}
			});
			
			table = new FlexTable();
			table.setStylePrimaryName(style.searchPanel());
			table.addStyleDependentName(style.line());
			table.setWidget(0, 0, lField);
			table.setWidget(0, 2, lOperator);
			table.setWidget(0, 4, text);
			table.setWidget(0, 6, btnAdd);
			table.setWidget(0, 7, btnDel);
			
			this.add(table);
		}
		
		public FieldDefinitionDTO getFieldDefinition(){
			int fieldDefId = Integer.parseInt(lField.getValue(lField.getSelectedIndex()));
			for(FieldDefinitionDTO field : fieldDefinitions){
				if(field.getId() == fieldDefId){
					return field;
				}
			}
			
			return null;
		}
		
		public String getText(){
			return text.getText();
		}
	}
}
