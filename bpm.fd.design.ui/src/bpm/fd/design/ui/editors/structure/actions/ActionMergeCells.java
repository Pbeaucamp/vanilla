package bpm.fd.design.ui.editors.structure.actions;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;

import bpm.fd.api.core.model.structure.Cell;
import bpm.fd.api.core.model.structure.FactoryStructure;
import bpm.fd.api.core.model.structure.Table;
import bpm.fd.design.ui.Messages;
import bpm.fd.design.ui.structure.gef.editparts.CellPart;

public class ActionMergeCells extends Action{

	public static final int MERGE_HORIZONTAL = 1;
	public static final int MERGE_VERTICAL = 2;
	
	private List<CellPart> editparts; 
	private int type;
	private Table table;
	private FactoryStructure factory;
	
	public ActionMergeCells(FactoryStructure factory, int type, List<CellPart> editparts, Table table){
		super(Messages.ActionMergeCells_0);
		this.type = type;
		this.editparts = editparts;
		this.table = table;
		this.factory = factory;
	}
	
	public void run(){
		List<Cell> c = new ArrayList<Cell>();
		
		for(CellPart e : editparts){
			c.add((Cell)e.getModel());
		}
		
		Point p = table.getPosition(((Cell)editparts.get(0).getModel()));
		
		if (type == MERGE_HORIZONTAL){
			table.mergeHorizontalCells(factory, p.y, c);
		}
		else if (type == MERGE_VERTICAL){
			table.mergeVerticalCells(factory, p.x, c);
		}
		
		
		
	}
}
