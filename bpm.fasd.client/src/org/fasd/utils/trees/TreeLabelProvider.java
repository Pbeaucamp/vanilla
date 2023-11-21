package org.fasd.utils.trees;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPLevel;
import org.fasd.olap.OLAPMeasure;
import org.fasd.views.SecurityView.TreeDimView;
import org.fasd.views.SecurityView.TreeView;

public class TreeLabelProvider extends LabelProvider {
	public String getText(Object obj) {
		return obj.toString();
	}

	public Image getImage(Object obj) {
		if (obj instanceof TreeCube) {
			try {
				if (((TreeCube) obj).getOLAPCube().getFactDataObject() == null) {
					return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/err_cube.png"); //$NON-NLS-1$
				}
				return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/cube.gif"); //$NON-NLS-1$
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		} else if (obj instanceof TreeDim) {
			try {
				OLAPDimension d = ((TreeDim) obj).getOLAPDimension();
				return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/dimension.gif"); //$NON-NLS-1$

			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		} else if (obj instanceof TreeMes) {
			try {
				OLAPMeasure m = ((TreeMes) obj).getOLAPMeasure();
				if (m.getType().equals("calculated")) { //$NON-NLS-1$
					return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/obj_calc.png"); //$NON-NLS-1$
				}

				if (m.getType().equals("physical")) { //$NON-NLS-1$
					if (m.getOrigin() == null)
						return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/err_measure.png"); //$NON-NLS-1$
					else
						return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/obj_measure.png"); //$NON-NLS-1$
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		} else if (obj instanceof TreeMesGroup) {
			try {
				return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/folder.png"); //$NON-NLS-1$
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		} else if (obj instanceof TreeDimGroup) {
			try {
				return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/folder.png"); //$NON-NLS-1$
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}

		else if (obj instanceof TreeRoot) {
			try {
				return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/obj_element.png"); //$NON-NLS-1$
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		} else if (obj instanceof TreeHierarchy) {
			try {
				return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/hierarchy.gif"); //$NON-NLS-1$
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		} else if (obj instanceof TreeLevel) {
			try {
				OLAPLevel l = ((TreeLevel) obj).getOLAPLevel();
				if (l.getItem() == null) {
					if (!l.getSql().trim().equals("")) //$NON-NLS-1$
						return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/warn_level.png"); //$NON-NLS-1$

					return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/err_level.png"); //$NON-NLS-1$
				}
				return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/level.gif"); //$NON-NLS-1$
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		} else if (obj instanceof TreeDimView) {
			try {
				return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/obj_measure.png"); //$NON-NLS-1$

			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		} else if (obj instanceof TreeView) {
			try {
				return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/obj_column.png"); //$NON-NLS-1$
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		} else if (obj instanceof TreeTable) {
			try {
				if (((TreeTable) obj).getTable().getDataObjectType().equals("fact")) { //$NON-NLS-1$
					return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/fact_table.png"); //$NON-NLS-1$
				} else if (((TreeTable) obj).getTable().getDataObjectType().equals("dimension")) { //$NON-NLS-1$
					return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/dim_table.png"); //$NON-NLS-1$
				} else if (((TreeTable) obj).getTable().getDataObjectType().equals("closure")) { //$NON-NLS-1$
					return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/clos_table.png"); //$NON-NLS-1$
				} else if (((TreeTable) obj).getTable().getDataObjectType().equals("label")) { //$NON-NLS-1$
					return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/lab_table.png"); //$NON-NLS-1$
				} else if (((TreeTable) obj).getTable().getDataObjectType().equals("aggregate")) { //$NON-NLS-1$
					return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/agg_table.png"); //$NON-NLS-1$
				} else
					return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/obj_folder.png"); //$NON-NLS-1$
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		} else if (obj instanceof TreeColumn) {
			try {
				Class c = Class.forName(((TreeColumn) obj).getColumn().getClasse());
				System.out.println(c.getName());
				if (java.lang.Number.class.isAssignableFrom(c)) {
					return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/obj_col_num.png"); //$NON-NLS-1$
				} else if (String.class.isAssignableFrom(c)) {
					return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/obj_col_text.png"); //$NON-NLS-1$
				} else if (java.sql.Date.class.isAssignableFrom(c) || java.util.Date.class.isAssignableFrom(c)) {
					return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/obj_col_dat.png"); //$NON-NLS-1$
				} else
					return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/obj_file.png"); //$NON-NLS-1$
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		} else if (obj instanceof TreeDatabase) {
			try {
				return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/schema.gif"); //$NON-NLS-1$
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		} else if (obj instanceof TreeRelation) {
			try {
				return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/obj_relation.png"); //$NON-NLS-1$
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		} else if (obj instanceof TreeMember) {
			try {
				return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/obj_file.png"); //$NON-NLS-1$
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		} else if (obj instanceof TreeUOlapMember) {
			try {
				return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/obj_file.png"); //$NON-NLS-1$
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		} else if (obj instanceof TreeVirtualCube) {
			try {
				return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/cube.gif"); //$NON-NLS-1$
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		} else if (obj instanceof TreeVirtualDim) {
			try {
				OLAPDimension d = ((TreeVirtualDim) obj).getVirtualDimension().getDim();
				return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/dimension.gif"); //$NON-NLS-1$

			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		} else if (obj instanceof TreeVirtualMes) {
			try {
				OLAPMeasure m = ((TreeVirtualMes) obj).getVirtualMeasure().getMes();
				if (m.getType().equals("calculated")) { //$NON-NLS-1$
					return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/obj_calc.png"); //$NON-NLS-1$
				}

				if (m.getType().equals("physical")) { //$NON-NLS-1$
					if (m.getOrigin() == null)
						return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/err_measure.png"); //$NON-NLS-1$
					else
						return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/obj_measure.png"); //$NON-NLS-1$
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		} else if (obj instanceof TreeParent) {
			try {
				return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/obj_element.png"); //$NON-NLS-1$
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		} else if (obj instanceof TreeCubeView) {
			try {
				return new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/cube_view.png"); //$NON-NLS-1$
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
		return null;
	}

}
