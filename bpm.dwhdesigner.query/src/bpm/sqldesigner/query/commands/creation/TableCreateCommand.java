package bpm.sqldesigner.query.commands.creation;

import java.util.Iterator;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import bpm.sqldesigner.query.model.Column;
import bpm.sqldesigner.query.model.Schema;
import bpm.sqldesigner.query.model.Table;
import bpm.sqldesigner.query.model.selected.SelectedColumnsManager;
import bpm.sqldesigner.query.palette.SQLDesignerPalette;

public class TableCreateCommand extends Command {
	private Schema schema;
	private Table table;
	private SQLDesignerPalette palette;
	
	public TableCreateCommand() {
		super();
		schema = null;
		table = null;
	}

	public void setTable(Object t, SQLDesignerPalette palette) {
		if (t instanceof Table){
			table = (Table) t;
		}
		this.palette = palette;
		
			
	}

	public void setSchema(Object s) {
		if (s instanceof Schema) {
			schema = (Schema) s;
		}

	}

	public void setLayout(Rectangle r) {
		if (table == null)
			return;
		table.setLayout(r);
	}

	@Override
	public boolean canExecute() {
		if (table == null || schema == null)
			return false;
		return true;
	}

	@Override
	public void execute() {
		if (table.getChildren().size() == 0){
			palette.loadColumns(table);
		}
		
		schema.addChild(table);
		//table.getCte().setVisible(false);
		table.canBeCreated(false);
	}

	@Override
	public boolean canUndo() {
		if (schema == null || table == null)
			return false;
		return schema.contains(table);
	}

	@Override
	public void undo() {

		table.canBeCreated(true);
		Iterator<?> itC = table.getChildren().iterator();

		while (itC.hasNext()) {
			SelectedColumnsManager.getInstance().removeColumn(
					(Column) itC.next());
		}
		schema.removeChild(table);
		table.fireRemoveOrAdd();
	}
}