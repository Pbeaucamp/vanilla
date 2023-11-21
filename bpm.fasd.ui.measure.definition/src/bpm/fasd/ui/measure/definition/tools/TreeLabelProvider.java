package bpm.fasd.ui.measure.definition.tools;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPLevel;
import org.fasd.olap.OLAPMeasure;
import org.fasd.utils.trees.TreeColumn;
import org.fasd.utils.trees.TreeCube;
import org.fasd.utils.trees.TreeCubeView;
import org.fasd.utils.trees.TreeDatabase;
import org.fasd.utils.trees.TreeDim;
import org.fasd.utils.trees.TreeDimGroup;
import org.fasd.utils.trees.TreeDimView;
import org.fasd.utils.trees.TreeHierarchy;
import org.fasd.utils.trees.TreeLevel;
import org.fasd.utils.trees.TreeMember;
import org.fasd.utils.trees.TreeMes;
import org.fasd.utils.trees.TreeMesGroup;
import org.fasd.utils.trees.TreeParent;
import org.fasd.utils.trees.TreeRelation;
import org.fasd.utils.trees.TreeRoot;
import org.fasd.utils.trees.TreeTable;
import org.fasd.utils.trees.TreeView;
import org.fasd.utils.trees.TreeVirtualCube;
import org.fasd.utils.trees.TreeVirtualDim;
import org.fasd.utils.trees.TreeVirtualMes;


public class TreeLabelProvider extends LabelProvider {
	public String getText(Object obj) {
		return obj.toString();
	}
	public Image getImage(Object obj) {
		if (obj instanceof TreeCube) {
			try {
				if (((TreeCube)obj).getOLAPCube().getFactDataObject() == null){
					return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/err_cube.png");
				}
				return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/cube.gif");
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
		else if (obj instanceof TreeDim) {
			try {
				OLAPDimension d = ((TreeDim)obj).getOLAPDimension();
//					if (!d.getWidget().equals("")){
//						return new Image(Display.getCurrent(),d.getWidget());
//					}
					return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/dimension.gif");
//				}
				
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
		else if (obj instanceof TreeMes) {
			try {
				OLAPMeasure m = ((TreeMes)obj).getOLAPMeasure(); 
				if (m.getType().equals("calculated")){
					return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/obj_calc.png");
				}
					
				if (m.getType().equals("physical")){
					if (m.getOrigin() == null)
						return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/err_measure.png");
					else
						return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/obj_measure.png");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
		else if (obj instanceof TreeMesGroup) {
			try {
				return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/folder.png");
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
		else if (obj instanceof TreeDimGroup) {
			try {
				return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/folder.png");
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
		
		else if (obj instanceof TreeRoot) {
			try {
				return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/obj_element.png");
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
		else if (obj instanceof TreeHierarchy) {
			try {
				return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/hierarchy.gif");
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
		else if (obj instanceof TreeLevel) {
			try {
				OLAPLevel l= ((TreeLevel)obj).getOLAPLevel();
				if (l.getItem() == null){
					if (!l.getSql().trim().equals(""))
						return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/warn_level.png");
					
					return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/err_level.png");
				}
				return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/level.gif");
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
		else if (obj instanceof TreeDimView) {
			try {
				return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/obj_measure.png");
				
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
		else if (obj instanceof TreeView){
			try {
				return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/obj_column.png");
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
		else if (obj instanceof TreeTable) {
			try {
				if (((TreeTable)obj).getTable().getDataObjectType().equals("fact")){
					return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/fact_table.png");
				}
				else if (((TreeTable)obj).getTable().getDataObjectType().equals("dimension")){
					return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/dim_table.png");
				}
				else if (((TreeTable)obj).getTable().getDataObjectType().equals("closure")){
					return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/clos_table.png");
				}
				else if(((TreeTable)obj).getTable().getDataObjectType().equals("label")){
					return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/lab_table.png");
				}
				else if(((TreeTable)obj).getTable().getDataObjectType().equals("aggregate")){
					return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/agg_table.png");
				}
				else
					return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/obj_folder.png");
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
		else if (obj instanceof TreeColumn) {
			try {
				Class c = Class.forName(((TreeColumn)obj).getColumn().getClasse());
				if (java.lang.Number.class.isAssignableFrom(c)){
					return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/obj_col_num.png");
				}
				else if (String.class.isAssignableFrom(c)){
					return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/obj_col_text.png");
				}
				else if (java.sql.Date.class.isAssignableFrom(c) || java.util.Date.class.isAssignableFrom(c)){
					return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/obj_col_dat.png");
				}
				else
					return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/obj_file.png");
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
		else if (obj instanceof TreeDatabase) {
			try {
				return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/schema.gif");
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
		else if (obj instanceof TreeRelation) {
			try {
				return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/obj_relation.png");
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
		else if (obj instanceof TreeMember){
			try {
				return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/obj_file.png");
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
		else if (obj instanceof TreeVirtualCube){
			try {
				return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/cube.gif");
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
		else if (obj instanceof TreeVirtualDim){
			try {
				OLAPDimension d = ((TreeVirtualDim)obj).getVirtualDimension().getDim();
//					if (!d.getWidget().equals("")){
//						return new Image(Display.getCurrent(),d.getWidget());
//					}
					return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/dimension.gif");
//				}
				
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
		else if (obj instanceof TreeVirtualMes){
			try {
				OLAPMeasure m = ((TreeVirtualMes)obj).getVirtualMeasure().getMes(); 
				if (m.getType().equals("calculated")){
					return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/obj_calc.png");
				}
					
				if (m.getType().equals("physical")){
					if (m.getOrigin() == null)
						return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/err_measure.png");
					else
						return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/obj_measure.png");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
		else if (obj instanceof TreeParent){
			try {
				return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/obj_element.png");
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
		else if (obj instanceof TreeCubeView){
			try {
				return new Image(Display.getCurrent(),Platform.getInstallLocation().getURL().getPath() +"icons/cube_view.png");
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
		return null;
	}

}
