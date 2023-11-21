package org.fasd.views.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.fasd.datasource.DataObject;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.OLAPLevel;
import org.fasd.olap.OLAPMeasure;
import org.fasd.olap.OLAPSchema;
import org.fasd.utils.ActionGetPath;
import org.fasd.utils.Path;
import org.fasd.views.dialogs.DialogCheck;
import org.freeolap.FreemetricsPlugin;

public class ActionCheckSchema extends Action {
	private List<String> log = new ArrayList<String>();

	public void run() {
		OLAPSchema schema = FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema();

		for (OLAPCube cube : schema.getCubes()) {
			// check measure
			DataObject fact = cube.getFactDataObject();

			for (OLAPMeasure m : cube.getMes()) {
				// physical measure
				if (m.getType().equals("physical")) { //$NON-NLS-1$
					if (m.getOrigin() == null) {
						log.add("Cube " + cube.getName() + " : Measure " + m.getName() + " : no Origin defined"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
					if (!fact.getColumns().contains(m.getOrigin())) {
						log.add("Cube " + cube.getName() + " : Measure " + m.getName() + " : no matching Column found in fact Table"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
				}
				// calculated Measure
				else if (m.getType().equals("calculated")) { //$NON-NLS-1$
					String[] split = m.getFormula().split("]"); //$NON-NLS-1$
					for (int i = 1; i < split.length; i += 2) {
						if (split[i].length() > 1) {
							try {
								OLAPMeasure mes = schema.findMeasureNamed(split[i].substring(2));
								if (mes != null && !cube.getMes().contains(mes)) {
									log.add("Cube " + cube.getName() + " doesn't contains all measures needed to compute the CalculatedMeasure " + m.getName()); //$NON-NLS-1$ //$NON-NLS-2$
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}

					}

				}
			}
			if (cube.getDims().size() < 2) {
				log.add("Cube " + cube.getName() + " has less than 2 dimensions."); //$NON-NLS-1$ //$NON-NLS-2$
			}

			for (OLAPDimension d : cube.getDims()) {
				for (OLAPHierarchy h : d.getHierarchies()) {
					checkDimension(cube, h);
				}
			}
		}

		if (log.size() > 0) {
			DialogCheck dial = new DialogCheck(LanguageText.ActionCheckSchema_Error_In_Project, FreemetricsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), log);
			dial.setBlockOnOpen(false);
			dial.open();
		} else {
			MessageDialog.openInformation(FreemetricsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), LanguageText.ActionCheckSchema_Information, LanguageText.ActionCheckSchema_No_error);
		}
	}

	private void checkDimension(OLAPCube cube, OLAPHierarchy hiera) {
		List<DataObject> tables = new ArrayList<DataObject>();

		// check levels
		for (OLAPLevel l : hiera.getLevels()) {
			if (l.getItem() == null && l.getKeyExpressions().size() == 0) {
				log.add("Cube " + cube.getName() + " : Hierarchy " + hiera.getName() + " : Level " + l.getName() + " column not defined"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			} else if (l.getItem().getParent().isView()) {
				log.add("Hierarchy " + hiera.getName() + " : Level " + l.getName() + " column based on sql query, only supported on FactTable"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			} else {
				if (l.getItem() != null && !tables.contains(l.getItem().getParent())) {
					tables.add(l.getItem().getParent());
				}
				// parent child hierarchy
				if (l.isClosureNeeded()) {
					if (l.getClosureChildCol() == null) {
						log.add("Hierarchy " + hiera.getName() + " : Level " + l.getName() + " child column not defined"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
					if (l.getClosureParentCol() == null) {
						log.add("Hierarchy " + hiera.getName() + " : Level " + l.getName() + " parent column not defined"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}

				}
			}

		}

		// hierarchy : check the relation between fact and dims
		if (!tables.contains(cube.getFactDataObject()))
			tables.add(cube.getFactDataObject());

		ActionGetPath action = new ActionGetPath(FreemetricsPlugin.getDefault().getFAModel(), tables.toArray(new DataObject[tables.size()]));
		action.run();
		Path p = action.getPath();
		if (p == null && !hiera.getParent().isDegenerated(cube.getFactDataObject())) {
			String s = "Cube " + cube.getName() + " : Relation Missing between tables : "; //$NON-NLS-1$ //$NON-NLS-2$
			boolean first = true;
			for (DataObject o : tables) {
				if (first) {
					first = false;
					s += o.getName();
				} else {
					s += ", " + o.getName(); //$NON-NLS-1$
				}
			}
			log.add(s);
		} else if (p != null) {
			boolean factFound = false;
			boolean lvlFound = true;
			for (DataObject o : p.getUsedTables()) {
				if (o == cube.getFactDataObject()) {
					factFound = true;
				}

				if (!tables.contains(o)) {
					lvlFound = false;
				}

			}
			if (!lvlFound || !factFound) {
				log.add("Relations Missing between Dimension " + hiera.getParent().getName() + LanguageText.ActionCheckSchema_0 + cube.getName() + LanguageText.ActionCheckSchema_1); //$NON-NLS-1$
			}
		}

	}

	public boolean isValide() {
		if (log.size() > 0)
			return false;

		return true;
	}
}
