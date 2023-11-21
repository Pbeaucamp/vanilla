package bpm.faweb.client.projection.panel;

import bpm.faweb.client.images.FaWebImage;
import bpm.faweb.client.projection.Functions;
import bpm.faweb.client.projection.panel.impl.DoubleClickableTreeItem;
import bpm.faweb.shared.infoscube.ItemMes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class ProjectionFormulaPanel extends Composite {

	private static ProjectionFormulaPanelUiBinder uiBinder = GWT.create(ProjectionFormulaPanelUiBinder.class);

	interface ProjectionFormulaPanelUiBinder extends UiBinder<Widget, ProjectionFormulaPanel> {
	}

	@UiField
	HTMLPanel functionsTreePanel;
	
	@UiField
	TextArea txtFormula;
	
	private ItemMes mes;
	
	public ProjectionFormulaPanel(ItemMes mes) {
		initWidget(uiBinder.createAndBindUi(this));
		this.mes = mes;
		fillFunctionsTree();
	}

	private void fillFunctionsTree() {
		Tree functionTree = new Tree();
		
		
		TreeItem opItem = new TreeItem(createTreeItemTitle("Operateurs"));
		
		for(int i = 0 ; i < Functions.OPERATORS.length ; i++) {
			DoubleClickableTreeItem item = new DoubleClickableTreeItem(new HTML(Functions.OPERATORS[i]), mes.getUname(), txtFormula);//createTreeItemTitle(Functions.OPERATORS[i]));
//			item.addDoubleClickHandler(handler);
			opItem.addItem(item);
		}
		
		TreeItem jsItem = new TreeItem(createTreeItemTitle("Javascript"));
		
		for(int i = 0 ; i < Functions.Js_Functions_Subcat.length ; i++) {
			TreeItem item = new TreeItem(createTreeItemTitle(Functions.Js_Functions_Subcat[i]));
			
			for(int j = 0 ; j < Functions.JS_FUNCTIONS_VALUES[i].length ; j++) {
				DoubleClickableTreeItem subIt = new DoubleClickableTreeItem(createTreeItemTitle(Functions.JS_FUNCTIONS_LABELS[i][j]), mes.getUname(), txtFormula);//createTreeItemTitle(Functions.OPERATORS[i]));
				subIt.setUserObject(Functions.JS_FUNCTIONS_VALUES[i][j]);
				item.addItem(subIt);
			}
			
//			item.addDoubleClickHandler(handler);
			jsItem.addItem(item);
		}
		
		DoubleClickableTreeItem mesItem = new DoubleClickableTreeItem(createTreeItemTitle("Actual Measure"), mes.getUname(), txtFormula);
		
		functionTree.addItem(opItem);
		functionTree.addItem(jsItem);
		functionTree.addItem(mesItem);
		
		functionsTreePanel.add(functionTree);
	}
	
//	private DoubleClickHandler handler = new DoubleClickHandler() {
//		@Override
//		public void onDoubleClick(DoubleClickEvent event) {
//			txtFormula.setText(txtFormula.getText() + ((TreeItem)event.getSource()).getText());
//		}
//	};
	
	private HTML createTreeItemTitle(String itemName) {
		Image img = new Image(FaWebImage.INSTANCE.treeitem());
		HTML html = new HTML(img + " " + itemName);
		return html;
	}
	
	public String getFormula() {
		return txtFormula.getText();
	}

	public void setFormula(String formula) {
		if(formula != null) {
			txtFormula.setText(formula);
		}
	}

}
