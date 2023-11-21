package bpm.fa.ui.ktable;

import java.sql.SQLException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.widgets.Display;

import bpm.fa.api.item.ItemElement;
import bpm.fa.api.item.ItemValue;
import bpm.fa.api.olap.OLAPResult;
import bpm.fa.ui.Messages;
import bpm.fa.ui.dialogs.DialogDrillTrhought;
import de.kupzog.ktable.KTable;

public class KTableMenuProvider {
	
	
	
	class MouseTracker implements MouseMoveListener {
		@Override
		public void mouseMove(MouseEvent e) {
			if (table != null){
				try {
					org.eclipse.swt.graphics.Point cell = table.getCellForCoordinates(e.x, e.y);
					
					if (cell.x >=0 && cell.y>=0){
						currentHovered = getCubeModel().getModelItemAt(cell);	
					}
					else{
						currentHovered = null;;
					}
				} catch (Exception e1) {
				}
			}
			

		}
		
	}
	
	
	
	 class ActionDelete extends Action{
		 public ActionDelete(){
			 super(Messages.KTableMenuProvider_0);
		 }
		public void run(){
			ItemElement e = (ItemElement)currentHovered.getItem();
			getCubeModel().getOLAPCube().remove(e);
			getCubeModel().performQuery();
			table.redraw();
			table.getParent().layout(true);
		}
	}
	 
	 class ActionDrillThrought extends Action{
		 public ActionDrillThrought(){
			 super(Messages.KTableMenuProvider_1);
		 }
		public void run(){
			ItemValue e = (ItemValue)currentHovered.getItem();
			
			try {
				OLAPResult res = getCubeModel().getOLAPCube().drillthrough(e, 0);
				DialogDrillTrhought dial = new DialogDrillTrhought(KTableMenuProvider.this.table.getShell(), res);
				dial.open();
			} catch (Exception e1) {
				e1.printStackTrace();
				MessageDialog.openError(KTableMenuProvider.this.table.getShell(), Messages.KTableMenuProvider_2, Messages.KTableMenuProvider_3 + e1.getMessage());
			}
			
		}
	}
	
	
	private KTable table;
	private CubeExplorerContent currentHovered;
	private MenuManager manager;
	
	private Action delete, drillThrought;
	
	public KTableMenuProvider(KTable table){
		this.table = table;
		
		
		
		manager = new MenuManager();
		delete = new ActionDelete();
		drillThrought = new ActionDrillThrought();
		manager.add(delete);
		manager.add(new Separator());
		manager.add(drillThrought);
		
		table.addMouseMoveListener(new MouseTracker());
		manager.addMenuListener(new IMenuListener() {
			
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				if (currentHovered == null || !(currentHovered.getItem() instanceof ItemElement)){
					delete.setEnabled(false);
				}
				else{
					delete.setEnabled(true);
					delete.setEnabled(((CubeModel)KTableMenuProvider.this.table.getModel()).getOLAPCube().getMdx().canRemove(currentHovered.getItem()));
					
				}
				
				if (currentHovered == null || !(currentHovered.getItem() instanceof ItemValue)){
					drillThrought.setEnabled(false);
				}
				else{
					drillThrought.setEnabled(true);
				}
			}
		});
	}
	
	public void createMenu(){
		table.setMenu(manager.createContextMenu(table));
	}
	
	private CubeModel getCubeModel(){
		return (CubeModel)table.getModel();
	}
}
