package bpm.fd.design.ui.editors;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.ActionFactory;

import bpm.fd.api.core.model.structure.Cell;
import bpm.fd.api.core.model.structure.DivCell;
import bpm.fd.api.core.model.structure.FactoryStructure;
import bpm.fd.api.core.model.structure.Row;
import bpm.fd.api.core.model.structure.StackableCell;
import bpm.fd.api.core.model.structure.Table;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.Messages;
import bpm.fd.design.ui.editor.part.actions.ActionCopy;
import bpm.fd.design.ui.editors.structure.actions.ActionInsertColumn;
import bpm.fd.design.ui.editors.structure.actions.ActionInsertRow;
import bpm.fd.design.ui.editors.structure.actions.ActionMergeCells;
import bpm.fd.design.ui.structure.gef.editparts.CellPart;
import bpm.fd.design.ui.structure.gef.editparts.TablePart;



public class StructureEditorContextMenuProvider extends ContextMenuProvider {

	public static final String INSERTION_MENU_ID = "bpm.fd.design.ui.editors.StructureEditor.MenuManager.InsertionMenu"; //$NON-NLS-1$
	
	private ActionRegistry actionRegistry;
	
	public StructureEditorContextMenuProvider(EditPartViewer viewer, ActionRegistry actionRegistry) {
		super(viewer);
		setActionRegistry(actionRegistry);
	}

	@Override
	public void buildContextMenu(IMenuManager menu) {
		IAction action;

		
		GEFActionConstants.addStandardActionGroups(menu);
		
		action = getActionRegistry().getAction(ActionFactory.DELETE.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, new Separator());
		action = getActionRegistry().getAction(ActionFactory.UNDO.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
		action = getActionRegistry().getAction(ActionFactory.REDO.getId());
		if (action != null){
			menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
		}
		
		action = new ActionCopy();
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
	
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, new Separator());
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, createInsertionMenu());
		
		Action mergeAction = getMergeAction();
		if (mergeAction != null){
			menu.appendToGroup(GEFActionConstants.GROUP_EDIT, mergeAction);
		}
		
		Action deleteRow = getRowDeleteAction();
		if (deleteRow != null){
			menu.appendToGroup(GEFActionConstants.GROUP_EDIT, deleteRow);
		}
		
		Action deleteCol = getColumnDeleteAction();
		if (deleteCol != null){
			menu.appendToGroup(GEFActionConstants.GROUP_EDIT, deleteCol);
		}
		
		Action splitRow = getSplitRowAction();
		if (splitRow != null){
			menu.appendToGroup(GEFActionConstants.GROUP_EDIT, splitRow);
		}
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, new Separator());
		//getActionRegistry().getAction(ActionAlign.ALIGN_BOTTOM_ID);
		
	}
	
	
	
	private Action getColumnDeleteAction(){
		List<Cell> cells = new ArrayList<Cell>();
		for (EditPart p : (List<EditPart>)getViewer().getSelectedEditParts()){
			if (p.getModel() instanceof Cell){
				cells.add((Cell)p.getModel());
				
				for (int i = 1; i < ((Cell)p.getModel()).getRowSpan(); i++){
					cells.add(null);
				}
			}
			else {
				return null;
			}
		}
		
		
		if (cells.size() == 0){
			return null;
		}
		
		if(cells.get(0) instanceof StackableCell || cells.get(0) instanceof DivCell) {
			return null;
		}
		
		final Table parent = (Table)cells.get(0).getParentStructureElement();
		int baseRowNum = -1;
		
		for(Cell c : cells){
			if (baseRowNum == -1){
				baseRowNum = parent.getColPosition(c); 
			}
			else if (baseRowNum != parent.getColPosition(c)){
				return null;
			}
		}
		
		if (parent.getDetailsColumns(baseRowNum).size() != cells.size()){
			return null;
		}
		
		final int colNum = baseRowNum;
		Action a = new Action(Messages.StructureEditorContextMenuProvider_1){
			public void run(){
				parent.removeDetailsColumn(colNum);
			}
		};
		a.setId("bpm.fd.design.ui.editors.StructureEditor.actions.deleteColumn"); //$NON-NLS-1$
		return a;
	}
	
	
	private Action getRowDeleteAction(){
		final List<Cell> cells = new ArrayList<Cell>();
		for (EditPart p : (List<EditPart>)getViewer().getSelectedEditParts()){
			if (p.getModel() instanceof Cell){
				cells.add((Cell)p.getModel());
				
				for (int i = 1; i < ((Cell)p.getModel()).getColSpan(); i++){
					cells.add(null);
				}
			}
			else {
				return null;
			}
		}
		
		
		if (cells.size() == 0){
			return null;
		}
		
		if(cells.get(0) instanceof StackableCell || cells.get(0) instanceof DivCell) {
			return null;
		}
		
		final Table parent = (Table)cells.get(0).getParentStructureElement();
		int baseRowNum = -1;
		
		for(Cell c : cells){
			if (c != null){
				if (baseRowNum == -1){
					baseRowNum = parent.getRowPosition(c); 
				}
				else if (baseRowNum != parent.getRowPosition(c)){
					return null;
				}
			}
			
		}
		
		if (parent.getDetailsRows().get(baseRowNum).size() != cells.size()){
			return null;
		}
		
			
		Action a = new Action(Messages.StructureEditorContextMenuProvider_3){
			public void run(){
				parent.removeDetailsRow(new Row(cells));
			}
		};
		a.setId("bpm.fd.design.ui.editors.StructureEditor.actions.deleteRow"); //$NON-NLS-1$
		return a;
	}
	
	
	@SuppressWarnings("unchecked")
	private IMenuManager createInsertionMenu(){
		IMenuManager menu = new MenuManager(Messages.StructureEditorContextMenuProvider_5, INSERTION_MENU_ID);
		for(EditPart e : (List<EditPart>)getViewer().getSelectedEditParts()){
			if (e instanceof TablePart){
				FactoryStructure factory = Activator.getDefault().getProject().getFdModel().getStructureFactory();
				menu.add(new ActionInsertRow(factory, getViewer().getSelectedEditParts()));
				menu.add(new ActionInsertColumn(factory, getViewer().getSelectedEditParts()));
				break;
			}
		}


		return menu;
	}
	
	
	private ActionRegistry getActionRegistry(){
		return actionRegistry;
	}
	
	private void setActionRegistry(ActionRegistry registry){
		actionRegistry = registry;
	}

	
	private Action getMergeAction(){
		List<CellPart> cellsParts = new ArrayList<CellPart>();
		
		for(EditPart e : (List<EditPart>)getViewer().getSelectedEditParts()){
			if (e instanceof CellPart){
				cellsParts.add((CellPart)e);
			}
		}
		
		//test if cells can be merged horinzontaly
		
		Row parent = null;
		boolean canMergeHorizontaly = true;
		boolean canMergeVerticaly = true;
		
		HashMap<CellPart, Point> pos = new HashMap<CellPart, Point>();
		Table table = null;
		for(CellPart e : cellsParts){
			table = (Table)((Cell)e.getModel()).getParentStructureElement();
			Point p = table.getPosition((Cell)e.getModel());
			
			pos.put(e, p);
			
			
		}
		
		CellPart previous = null;
		for(CellPart c : pos.keySet()){
			if (previous == null){
				previous = c;
			}
			else{
				//check positions
				if (pos.get(previous).y != pos.get(c).y){
					canMergeHorizontaly = false;
				}
				if (pos.get(previous).x != pos.get(c).x){
					canMergeVerticaly = false;
				}
				
				//check spanning
				if (((Cell)previous.getModel()).getColSpan() != ((Cell)c.getModel()).getColSpan()){
					canMergeVerticaly = false;
				}
				if (((Cell)previous.getModel()).getRowSpan() != ((Cell)c.getModel()).getRowSpan()){
					canMergeHorizontaly = false;
				}
				previous = c;
			}
		}
		
		if (canMergeHorizontaly && cellsParts.size() > 1){
			FactoryStructure factory = Activator.getDefault().getProject().getFdModel().getStructureFactory();
			return new ActionMergeCells(factory, ActionMergeCells.MERGE_HORIZONTAL, cellsParts, table);
		}
		else if (canMergeVerticaly && cellsParts.size() > 1){
			FactoryStructure factory = Activator.getDefault().getProject().getFdModel().getStructureFactory();
			return new ActionMergeCells(factory, ActionMergeCells.MERGE_VERTICAL, cellsParts, table);
			
		}
		
		return null;
		
	}



	private Action getSplitRowAction(){
		final List<Cell> cells = new ArrayList<Cell>();
		for (EditPart p : (List<EditPart>)getViewer().getSelectedEditParts()){
			if (p.getModel() instanceof Cell){
				cells.add((Cell)p.getModel());
				
				for (int i = 1; i < ((Cell)p.getModel()).getColSpan(); i++){
					cells.add(null);
				}
			}
			else {
				return null;
			}
		}
		
		
		if (cells.size() == 0){
			return null;
		}
		
		if(cells.get(0) instanceof StackableCell || cells.get(0) instanceof DivCell) {
			return null;
		}
		
		final Table parent = (Table)cells.get(0).getParentStructureElement();
		int baseRowNum = -1;
		
		for(Cell c : cells){
			if (c != null){
				if (baseRowNum == -1){
					baseRowNum = parent.getRowPosition(c); 
				}
				else if (baseRowNum != parent.getRowPosition(c)){
					return null;
				}
			}
			
		}
		
		if (parent.getDetailsRows().get(baseRowNum).size() != cells.size()){
			return null;
		}
		
		final int selectedIndex = baseRowNum;
			
		Action a = new Action(Messages.StructureEditorContextMenuProvider_6){
			public void run(){
				FactoryStructure factory = Activator.getDefault().getProject().getFdModel().getStructureFactory();
				parent.insertRow(factory, selectedIndex);
//				parent.removeDetailsRow(new Row(cells));
			}
		};
		a.setId("bpm.fd.design.ui.editors.StructureEditor.actions.splitRow"); //$NON-NLS-1$
		return a;
	}
}
